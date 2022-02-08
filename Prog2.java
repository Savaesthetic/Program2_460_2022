import java.io.*;

class Prog2 {
    static Integer[] directory = new Integer[] {null, null, null, null, null, null, null, null, null, null};
    static int globalDepth = 1;
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
    }

    static RandomAccessFile createNewBucketFile() {
        File bucketRef = new File("hashBucket.bin");
        try {
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

    // This will be used to convert the Project ID given by user input
    // into an integer string to properly index into our directory
    static String Integerize(String id) {
        int bufferedLength = id.length(); // The length of the original id string with padding
        char[] idArray = id.trim().toCharArray();
        StringBuilder output = new StringBuilder();

        for (int i = idArray.length-1; i >= 0; i--) {
            int asciiVal = (int) idArray[i]; // cast character to integer to get ascii value
            int lsd = asciiVal % 10; // get the least significant digit by using mod
            output.append(lsd); // concatenate all of the lsd into a String for to get search index
        }

        // Pad integer string to same length as original string to keep consistancy
        while (output.length() < bufferedLength) {
            output.insert(0, "0");
        }

        return output.toString();
    }

    // This will be used to open and read the database file given as input
    // Using this database file will compromise of reading through the records
    // one by one adding each record into our hash bucket file.
    static void readDatabaseFile(RandomAccessFile dataStream, RandomAccessFile bucketStream) {
        try {
            int[] fieldLengths = getFieldLengths(dataStream); // Lengths of string fields in records
            int recordLength = getRecordLength(fieldLengths); // Total length of a record
            long numRecords = getNumRecords(recordLength, dataStream.length()); // Number of records in the file

            dataStream.seek(0); // Start the iteration at the beginning of the file
            int i = 0;
            while (i < numRecords) {
                DataRecord record = new DataRecord();
                record.fetchObject(dataStream, fieldLengths);
                addRecordToBucketFile(bucketStream, record);
                i++;
            }
        } catch (IOException e) {
            System.out.println("I/O ERROR: Couldn't get the file's length.");
            System.exit(-1);
        }
    }

    // This function will be used to add a given record to the bucket file we are creating
    static void addRecordToBucketFile(RandomAccessFile bucketStream, DataRecord record) {
        String index = Integerize(record.getProjectId()); // Convert record id into a string to index into directory
        Integer bucketIndex = directory[Integer.parseInt(index.substring(0, globalDepth))];
        System.out.printf("ID: %s, Integerized: %s, Index: %d, Val: %d\n", record.getProjectId(), index, Integer.parseInt(index.substring(0, globalDepth)), bucketIndex);
        // TODO
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
            System.out.println("I/O ERROR: Error accessing file to get field lengths.");
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