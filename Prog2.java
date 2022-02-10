import java.io.*;
import java.util.Scanner;

/*
 * Prog2.java -- This program was written to get a better 
 * idea on the workings of extendible hashing.
 * 
 * Extendible hashing stores pointers to records stored 
 * on a file, possibly in a database, in buckets. 
 * Records that hash similarly will be stored in the 
 * same bucket and these buckets be accessed through 
 * a directory which will be indexed based on the 
 * record hash.
 * 
 * Extendible hashing allows for querys to be done 
 * without linearly searching through a large 
 * database file saving on time and space as smaller 
 * buckets are retrieved and searched rather than 
 * the entire database file.
 * 
 * Op Reqs: This program relies on 4 other java files. 
 * Bucket.java, DataRecord.java, Directory.java, IndexRecord.java.
 * These four additional files describe objects that are used 
 * within Prog2. Additionally, this program requires a binary file 
 * holding records as input to the command line.
 * 
 * Required features: Everything has been completed. The 
 * directory is simply held in memory while the buckets are 
 * stored in a file. The querying is done by indexing through 
 * the directory and bucket rather than a direct search 
 * through the databse file holding all of the records.
 * 
 * Author: Alex Sava
 * Course: CSC 460 Database Design
 * Assignment: Program 2
 * Professor: Dr. McCann
 * TA's: Haris Riaz, Aayush Pinto
 * Due Date: 02/10/2022
*/
class Prog2 {
    static Directory directory = new Directory(); // Initialize a directory to be used for bucket file
    public static void main (String[] args) {
        File dbRef = null; // Reference to Database file
        RandomAccessFile dbStream = null; // Reference to data stream of the Database File
        RandomAccessFile bucketStream = null; // Reference to the data stream for the Hash Bucket File

        // Creating file reference to the Database and Hashbucket files
        if (args.length == 0) {
            System.out.println("Error: .bin file required as input.");
            System.exit(-1);
        } else {
            try {
                dbRef = new File(args[0]);
                dbStream = new RandomAccessFile(dbRef, "rw");
                bucketStream = createNewBucketFile();
            } catch (FileNotFoundException e) {
                System.out.println("I/O ERROR: Something went wrong with the "
                                + "creation of the RandomAccessFile object.");
                System.exit(-1);
            }
        }

        // Main program functions
        readDatabaseFile(dbStream, bucketStream);
        getSearchQuery(dbStream, bucketStream);
    }

    /*
    * RandomAccessFile createNewBucketFile() -- 
    * Creates a new bucket file in the current 
    * directory to be used every time the 
    * program is run.
    * 
    * @return: data stream to the bucket file.
    * @params: None
    */
    static RandomAccessFile createNewBucketFile() {
        File bucketRef = new File("hashBucket.bin"); // File reference to bucket file
        try {
            // If the file already exists we have to recreate it
            if (bucketRef.exists()) {
                bucketRef.delete();
                bucketRef.createNewFile();
            } else {
                bucketRef.createNewFile();
            }
            return new RandomAccessFile(bucketRef, "rw");
        } catch (IOException e) {
            System.out.println("I/O ERROR: Something went wrong with the "
                                + "creation of the Hash Bucket File.");
            System.exit(-1);
            return null;
        }
    }


    /*
    * String Integerize(String id) -- 
    * Hashes the input string so that 
    * we can use it to index into the 
    * directory.
    * 
    * @return: Hashed string.
    * @params: id -- Original string
    */
    static String Integerize(String id) {
        char[] idArray = id.trim().toCharArray(); // String in character array form
        StringBuilder output = new StringBuilder(); // Hashed string builder

        for (int i = idArray.length-1; i >= 0; i--) {
            int asciiVal = (int) idArray[i]; // cast character to integer to get ascii value
            int lsd = asciiVal % 10; // get the least significant digit by using mod
            output.append(lsd); // concatenate all of the lsd into a String to get hashed string
        }

        return output.toString();
    }

    /*
    * void ReadDatabaseFile(dbStream, bucketStream) -- 
    * Reads through all of the records in the database 
    * file and adds them to our extendible hashing index.
    * 
    * @return: None
    * @params: dbStream - data stream for the database file
    * bucketStream - data stream for the bucket file
    */
    static void readDatabaseFile(RandomAccessFile dbStream, RandomAccessFile bucketStream) {
        try {
            int[] fieldLengths = getFieldLengths(dbStream); // Lengths of string fields in records
            int recordLength = getRecordLength(fieldLengths); // Total length of a record
            long numRecords = getNumRecords(recordLength, dbStream.length()); // Number of records in the file

            dbStream.seek(0); // Start the iteration at the beginning of the file
            int i = 0;
            while (i < numRecords) {
                long dbIndex = dbStream.getFilePointer();
                DataRecord record = new DataRecord(); // Current record in database file
                record.fetchObject(dbStream, fieldLengths);
                addRecordToBucketFile(bucketStream, record.getProjectId(), dbIndex);
                i++;
            }
        } catch (IOException e) {
            System.out.println("I/O ERROR: Couldn't get the file's length.");
            System.exit(-1);
        }
    }

    /*
    * void addRecordToBucketFile(bucketStream, projectId, dbIndex) -- 
    * Adds the given record to the extendible hashing index
    * 
    * @return: None
    * @params: bucketStream - data stream for the bucket file
    * projectId - String id for the record
    * dbIndex - index pointing to record location in database file
    */
    static void addRecordToBucketFile(RandomAccessFile bucketStream, String projectId, long dbIndex) {
        String intId = Integerize(projectId); // Convert project id into an integer string to index into directory
        int dirIndex = Integer.parseInt(intId.substring(0, directory.getGlobalDepth())); // Index used to access directory
        Long bucketIndex = directory.getValueAtIndex(dirIndex); // Index for bucket in bucket file

        Bucket currBucket = getBucket(bucketStream, bucketIndex, projectId.length()); // Bucket we are adding record index into
        boolean writeFlag = addRecord(bucketStream, currBucket, projectId, dbIndex, dirIndex); // Flag to check if write has occured
        // Writing bucket to file may occur during recursive call so we dont want to rewrite
        if (writeFlag) {
            writeBucket(bucketStream, currBucket, dirIndex);
        }
    }

    /*
    * void getBucket(bucketStream, bucketIndex, idLength) -- 
    * Grabs bucket from bucket file or creates one if index 
    * to bucket file does not exist.
    * 
    * @return: Bucket from the bucket file
    * @params: bucketStream - data stream for the bucket file
    * bucketIndex - index for the bucket we want to grab from the bucket file
    * idLength - Length used to pad newly created bucket if bucket index 
    * doesnt exist
    */
    static Bucket getBucket(RandomAccessFile bucketStream, Long bucketIndex, int idLength) {
        Bucket currBucket = new Bucket(); // Bucket we want to return
        // If the index for the bucket file doesnt exist, create and return a new bucket
        if (bucketIndex == null) {
            currBucket.pad(idLength);
            currBucket.setDepth(directory.getGlobalDepth());
            return currBucket;
        } else { // Read and return existing bucket from bucket file
            try {
                bucketStream.seek(bucketIndex);
                currBucket.readObject(bucketStream);
                return currBucket;
            } catch (IOException e) {
                System.out.println("Error: Could not index into bucket file during retrieval.");
                System.exit(-1);
                return null;
            }
        }
    }

    /*
    * boolean addRecord(bucketStream, currBucket, recordId, 
    * dbIndex, dirIndex) -- 
    * Adds record index to the given bucket and also handles 
    * directory and bucket splitting if bucket is full.
    * 
    * @return: boolean stating if writing bucket needs to 
    * occur outside or inside function
    * @params: bucketStream - data stream for the bucket file
    * currBucket - bucket we want to add record to
    * recordId - id for record index
    * dbIndex - index into databse file for record
    * dirIndex - index into directory for given bucket
    */
    static boolean addRecord(RandomAccessFile bucketStream, Bucket currBucket, String recordId, 
    long dbIndex, int dirIndex) {
        // If bucket isn't full, simply append to bucket and writing will occur outside of function
        if (!currBucket.isFull()) {
            currBucket.addRecord(recordId, dbIndex);
            return true; // Can probably be altered to write here instead of returning boolean
        } else {
            // If bucket depth and global depth are equal then we want to split the directory
            if (currBucket.getBucketDepth() == directory.getGlobalDepth()) {
                directory.splitDirectory();
                dirIndex *= 10; // Move index to new location for the new directory
            }
            // If directory is not splitting we have to adjust index to handle reassigning the 10 new buckets
            while(dirIndex%10 != 0) {
                dirIndex--;
            }
            // Remove all references to old bucket
            for (int i = 0; i < 10; i++) {
                directory.setValueAtIndex(dirIndex + i, null);
            }
            IndexRecord[] currRecords = currBucket.getRecords(); // Array of all the index record in the current bucket
            // Re add all of the current buckets records to the new directory
            for (IndexRecord record : currRecords) {
                addRecordToBucketFile(bucketStream, record.getId(), record.getIndexPointer());
            }
            // Add the new index record taht we initially wanted to add
            addRecordToBucketFile(bucketStream, recordId, dbIndex);
            return false; // Write occured in base case so we do not want to write again
        }
    }

    /*
    * void writeBucket(bucketStream, currBucket, dirIndex) -- 
    * Writes given bucket to the bucket file.
    * 
    * @return: None
    * @params: bucketStream - data stream for the bucket file
    * currBucket - bucket we want to add record to
    * dirIndex - index into directory for given bucket
    */
    static void writeBucket(RandomAccessFile bucketStream, Bucket currBucket, int dirIndex) {
        try {
            // If index in bucket file for current bucket does not exist simply write to end of file
            if (directory.getValueAtIndex(dirIndex) == null) {
                bucketStream.seek(bucketStream.length()); // Seek to the end of file
                directory.setValueAtIndex(dirIndex, bucketStream.getFilePointer()); // Set directory to point to current spot
                currBucket.writeObject(bucketStream); // Write bucket
                return;
            } else {
                bucketStream.seek(directory.getValueAtIndex(dirIndex));
                currBucket.writeObject(bucketStream);
                return;
            }
        } catch (IOException e) {
            System.out.println("Error: Error writing bucket to file.");
                System.exit(-1);
        }
    }

    /*
    * void getSearchQuery(dbStream, bucketStream) -- 
    * Gets search query from user.
    * 
    * @return: None
    * @params: bucketStream - data stream for the bucket file
    * dbStream - data stream for the database file
    */
    static void getSearchQuery(RandomAccessFile dbStream, RandomAccessFile bucketStream) {
        Scanner userInput = new Scanner(System.in); // Scanner for system input
        while (true) {
            System.out.println("Enter Project ID suffix to search for (-1 to exit).");
            String input = userInput.nextLine(); // Query to search for
            if (input.equals("-1")) {
                break;
            }
            String intInput = Integerize(input); // Query converted to hash
            // Only need to search a single bucket if query length is >= global depth
            if (intInput.length() >= directory.getGlobalDepth()) {
                int dirIndex = Integer.parseInt(intInput.substring(0, directory.getGlobalDepth()));
                searchInRange(dbStream, bucketStream, dirIndex, 1, input);
            } else { // Need to search range of buckets
                int dirIndex = Integer.parseInt(intInput);
                int startIndex = dirIndex * (int) (Math.pow(10, directory.getGlobalDepth()- input.length()));
                int range = (int) (Math.pow(10, directory.getGlobalDepth() - input.length()));
                searchInRange(dbStream, bucketStream, startIndex, range, input);
            }
        }
        try {
            // Close all running streams
            userInput.close();
            dbStream.close();
            bucketStream.close();
        } catch (IOException e) {
            System.out.println("Error: Error closing database or bucket file.");
            System.exit(-1);
        }
    }

    /*
    * void searchInRange(dbStream, bucketStream, startIndex, range, query) -- 
    * Iterates through directory to grab all buckets in
    * range of search.
    * 
    * @return: None
    * @params: bucketStream - data stream for the bucket file
    * dbStream - data stream for the database file
    * startIndex - directory index to start search at
    * range - the number of bucekts to search
    * query - record id taht we are searching for
    */
    static void searchInRange(RandomAccessFile dbStream, RandomAccessFile bucketStream,
     int startIndex, int range, String query) {
         int exclusiveEnd = startIndex + range; // last index we will search
         int[] fieldLengths = getFieldLengths(dbStream); // Lengths of string fields in records
         int recordsFound = 0; // number of records returned for query
         for (int i = startIndex; i < exclusiveEnd; i++) {
             Long bucketIndex = directory.getValueAtIndex(i); // index for bucket to search
             Bucket currBucket = getBucket(bucketStream, bucketIndex, query.length()); // bucket to search
             recordsFound += searchBucket(dbStream, currBucket, query, fieldLengths);
         }
         System.out.println(recordsFound + " records matched your query.");
    }

    /*
    * int searchBucket(dbStream, curBucket, query, fieldLengths) -- 
    * Search through the given bucket to find and record indexes 
    * that match our query
    * 
    * @return: Number of index records taht matcher the query
    * @params: dbStream - data stream for the database file
    * curBucket - the bucket we are searching
    * query - record id taht we are searching for
    * fieldLengths - necessary for reading record from databse file
    */
    static int searchBucket(RandomAccessFile dbStream, Bucket curBucket, String query, int[] fieldLengths) {
        int recordsFound = 0;
        IndexRecord[] indexes = curBucket.getRecords();
        for (IndexRecord index : indexes) {
            if (index.getId().trim().endsWith(query)) {
                recordsFound++;
                DataRecord record = getRecord(dbStream, index.getIndexPointer(), fieldLengths);
                System.out.println(record);
            }
        }
        return recordsFound;
    }

    /*
    * DataRecord getRecord(dbStream, index, fieldLengths) -- 
    * Return the record from the database file at given index.
    * 
    * @return: The record from the database file
    * @params: dbStream - data stream for the database file
    * index - index for the record in the database file
    * fieldLenghts - required lengths for reading record strings
    */
    static DataRecord getRecord(RandomAccessFile dbStream, long index, int[] fieldLengths) {
        try {
            dbStream.seek(index);
            DataRecord record = new DataRecord(); // Record we want to return
            record.fetchObject(dbStream, fieldLengths);
            return record;
        } catch (IOException e) {
            System.out.println("Error: Error reading record from database.");
            System.exit(-1);
            return null;
        }
    }

    /*
     * static void getFieldLengths -- get the lengths of 
     * each string field of a record by reading information
     * stored at the end of an input file
     * 
     * @return: int[] -- array holding the lengths of 
     *  each string field in a specific order
     * @params: dataStream -- reference to binary file
     * to be read from
    */
    static int[] getFieldLengths(RandomAccessFile dataStream) {
        try {
            long fileLength = dataStream.length(); // Length of the input file
            dataStream.seek(fileLength - (9*4));
            int[] fieldLengths = {dataStream.readInt(), dataStream.readInt(), 
                dataStream.readInt(), dataStream.readInt(), dataStream.readInt(), 
                dataStream.readInt(), dataStream.readInt(), dataStream.readInt(), 
                dataStream.readInt()}; // Array used to store length information being read from the file
            return fieldLengths;
        } catch (IOException e) {
            System.out.println("I/O ERROR: Error getting file meta data. File may be empty.");
            System.exit(-1);
            return null;
        }
    }

    /*
     * static void getRecordLength -- get the total length 
     * of a single record
     * 
     * @return: in holding the length of a record
     * @params: fieldLengths -- integer array holding the 
     * lengths of each string field in the record
    */
    static int getRecordLength(int[] fieldLengths) {
        int recordLength = 4*4; // Initialize record size with 4 integers of 4 bytes
        for (int length: fieldLengths) {
            recordLength += length;
        }
        return recordLength;
    }

    /*
     * static void getNumRecords -- get the total number 
     * of records in the input file
     * 
     * @return: long holding the total number of records
     * @params: recordLength -- the length of a single 
     *  record in the file.
     * fileLength -- the total length of the file
    */
    static long getNumRecords(int recordLength, long fileLength) {
        return (fileLength - (9*4)) / recordLength;
    }
}