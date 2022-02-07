import java.io.*;

// QUESTIONS:
// ARE OUR BUCKETS SORTED?? indexing by project id but records in db file are sorted on credits issued if I remember correnctly

class Prog2 {
    public static void main (String[] args) {
        File fileRef = null; // Reference to input file
        RandomAccessFile dataStream = null; // Reference to input stream of input file

        if (args.length == 0) { // Check to make sure file was provided as argument
            System.out.println("Error: .bin file required as input.");
            System.exit(-1);
        } else {
            try {
            fileRef = new File(args[0]);
            dataStream = new RandomAccessFile(fileRef, "rw");
            } catch (FileNotFoundException e) {
            System.out.println("I/O ERROR: Something went wrong with the "
                             + "creation of the RandomAccessFile object.");
            System.exit(-1);
            }
        }

        // Main program functions
        readDatabaseFile(dataStream);
    }

    // This will be used to convert the Project ID given by user input
    // into and Integer to properly search our hash with
    static String convertIDtoInt(String id) {
        char[] idArray = id.toCharArray();
        StringBuilder output = new StringBuilder();
        for (int i = idArray.length-1; i >= 0; i--) {
            int asciiVal = (int) idArray[i]; // cast character to integer to get ascii value
            int lsd = asciiVal % 10; // get the least significant digit by using mod
            output.append(lsd); // concatenate all of the lsd into a String for to get search index
        }
        return output.toString();
    }

    // This will be used to open and read the database file given as input
    // Using this database file will compromise of reading through the records
    // one by one adding each record into our hash bucket file.
    static void readDatabaseFile(RandomAccessFile dataStream) {
        //TODO
        try {
            int[] fieldLengths = getFieldLengths(dataStream); // Lengths of string fields in records
            int recordLength = getRecordLength(fieldLengths); // Total length of a record
            long numRecords = getNumRecords(recordLength, dataStream.length()); // Number of records in the file

            dataStream.seek(0); // Start the iteration at the beginning of the file
            int i = 0;
            while (i < numRecords) {
                DataRecord record = new DataRecord();
                record.fetchObject(dataStream, fieldLengths);
                addRecordToBucketFile();// TODO MOST IMPORTANT PART OF THE PROGRAM
                i++;
            }
        } catch (IOException e) {
            System.out.println("I/O ERROR: Couldn't get the file's length.");
            System.exit(-1);
        }
    }

    // This function will be used to add a given record to the bucket file we are creating
    static void addRecordToBucketFile() {
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

// Dont know if this needs to be a class or just an object
class Directory {

}

// Probably will be needed
class HashIndex {
		   
}

/*
 * DataRecord
 * Author: Alex Sava
 * 
 * This class was used in Prog1A to create and write 
 * DataRecords to a binary file. In Prog1B we are using 
 * the same class to hold records that we are reading 
 * from binary files created by Prog1A. The toString 
 * method is overloaded to make printing out specific 
 * information about the class simpler.
 * 
 * The following class variables are all fields that 
 * hold data from a csv file derived from the 
 * Voluntary Registry Offsets Database.
 * String projectId (id for the given project)
 * String projectName (name of the given project)
 * String status (current status of the given project)
 * String scope (project industry)
 * String type (project focus)
 * String methodology (project protocol)
 * String region (project region)
 * String country (project country)
 * String state (project state)
 * int creditsIssued (total carbon credits issued)
 * int creditsRetired (total carbon credits retired)
 * int creditsRemaining (total carbon credits remaining)
 * int firstYear (year of project start)
 * 
 * Uses default constructor with following methods:
 * String getProjectId()
 * String getProjectName()
 * String getProjectStatus()
 * String getProjectScope()
 * String getProjectType()
 * String getProjectMethodology()
 * String getProjectRegion()
 * String getProjectCountry()
 * String getProjectState()
 * int getCreditsIssued()
 * int getCreditsRemaining()
 * int getCreditsRetired()
 * int getFirstYear()
 * void setProjectId(val)
 * void setProjectName(val)
 * void setProjectStatus(val)
 * void setProjectScope(val)
 * void setProjectType(val)
 * void setProjectMethodology(val)
 * void setProjectRegion(val)
 * void setProjectCountry(val)
 * void setProjectState(val)
 * void setCreditsIssued(val)
 * void setCreditsRemaining(val)
 * void setCreditsRetired(val)
 * void setFirstYear(val)
 * void dumpObject(stream)
 * void fetchObject(stream, stringLengths)
 * String toString()
*/
class DataRecord {
    // Fields that make up a DataRecord
    private String projectId; // id of project in record
    private String projectName; // name of project in record
    private String status; // status of project in record
    private String scope; // industry of project in record
    private String type; // focus of project in record
    private String methodology; // protocol of project in record
    private String region; // region of project in record
    private String country; // country of project in record
    private String state; // state of project in record
    private int creditsIssued; // credits issued by project in record
    private int creditsRetired; // credits retired by project in record
    private int creditsRemaining; // credits remaining for project in record
    private int firstYear; // first year of project in record

    /*
     * "Getters" for class field values.
     * Each returns their respective field value 
     * to the caller.
    */
    public String getProjectId() { return projectId; }
    public String getProjectName() { return projectName; }
    public String getStatus() { return status; }
    public String getScope() { return scope; }
    public String getType() { return type; }
    public String getMethodology() { return methodology; }
    public String getRegion() { return region; }
    public String getCountry() { return country; }
    public String getState() { return state; }
    public int getCreditsIssued() { return creditsIssued; }
    public int getCreditsRetired() { return creditsRetired; }
    public int getCreditsRemaining() { return creditsRemaining; }
    public int getFirstYear() { return firstYear; }

    /*
     * "Setters" for class field values
     * Each takes a value as input and replaces the value 
     * in their respective field with the input value.
    */
    public void setProjectId(String val) { projectId = val; }
    public void setProjectName(String val) { projectName = val; }
    public void setStatus(String val) { status = val; }
    public void setScope(String val) { scope = val; }
    public void setType(String val) { type = val; }
    public void setMethodology(String val) { methodology = val; }
    public void setRegion(String val) { region = val; }
    public void setCountry(String val) { country = val; }
    public void setState(String val) { state = val; }
    public void setCreditsIssued(int val) { creditsIssued = val; }
    public void setCreditsRetired(int val) { creditsRetired = val; }
    public void setCreditsRemaining(int val) { creditsRemaining = val; }
    public void setFirstYear(int val) { firstYear = val; }

    /*
     * public void dumpObject(RandomAccessFile stream) -- 
     * Writes the contents (fields) of the DataRecord to 
     * the output file given by the stream object reference.
     * 
     * @return: none
     * @params: RandomAccessFile stream -- a reference to 
     * the file being written to.
    */
    public void dumpObject(RandomAccessFile stream)
    {
        /*
         * Using the file reference methods to individually
         * write each class field to the file in bytes.
        */
        try {
            stream.writeBytes(projectId);
            stream.writeBytes(projectName);
            stream.writeBytes(status);
            stream.writeBytes(scope);
            stream.writeBytes(type);
            stream.writeBytes(methodology);
            stream.writeBytes(region);
            stream.writeBytes(country);
            stream.writeBytes(state);
            stream.writeInt(creditsIssued);
            stream.writeInt(creditsRetired);
            stream.writeInt(creditsRemaining);
            stream.writeInt(firstYear);
        } catch (IOException e) {
            System.out.println("I/O ERROR: Couldn't write to the file;\n\t"
                                + "perhaps the file system is full?");
            System.exit(-1);
        }
    }

    /*
     * public void fetchObject(RandomAccessFile stream, int[] stringLengths) -- 
     * Reads the contents in a binary file to store data into a DataRecord 
     * object.
     * 
     * @return: none
     * @params: 
     * RandomAccessFile stream -- a reference to 
     *  the file being read from.
     * int[] stringLengths -- the lengths of each 
     *  string field in the record. Used to create 
     *  correctly sized byte containers for the 
     *  data being read.
    */
    public void fetchObject(RandomAccessFile stream, int[] stringLengths) {
        // Byte arrays used to store field data read in as bytes from file
        byte[] idBytes = new byte[stringLengths[0]];
        byte[] nameBytes = new byte[stringLengths[1]];
        byte[] statusBytes= new byte[stringLengths[2]];
        byte[] scopeBytes = new byte[stringLengths[3]];
        byte[] typeBytes = new byte[stringLengths[4]];
        byte[] methodBytes = new byte[stringLengths[5]];
        byte[] regionBytes = new byte[stringLengths[6]];
        byte[] countryBytes = new byte[stringLengths[7]];
        byte[] stateBytes = new byte[stringLengths[8]];

        /*
         * Read 9 strings and 4 integers from the given 
         * file in order and store the string in their 
         * respective byte arrays while directly storing 
         * the ints in their respective fields. The byte 
         * arrays are then converted to strings and stored 
         * in their respective fields.
        */
        try {
            stream.readFully(idBytes);
            projectId = new String(idBytes);
            stream.readFully(nameBytes);
            projectName = new String(nameBytes);
            stream.readFully(statusBytes);
            status = new String(statusBytes);
            stream.readFully(scopeBytes);
            scope = new String(scopeBytes);
            stream.readFully(typeBytes);
            type = new String(typeBytes);
            stream.readFully(methodBytes);
            methodology = new String(methodBytes);
            stream.readFully(regionBytes);
            region = new String(regionBytes);
            stream.readFully(countryBytes);
            country = new String(countryBytes);
            stream.readFully(stateBytes);
            state = new String(stateBytes);
            creditsIssued = stream.readInt();
            creditsRetired = stream.readInt();
            creditsRemaining = stream.readInt();
            firstYear = stream.readInt();
        } catch (IOException e) {
            System.out.println("I/O ERROR: Couldn't read from the file;\n\t"
                            + "is the file accessible?");
            System.exit(-1);
        }
    }

    /*
     * public String toString() -- overrites the
     * toString method found in classes to allow for 
     * simple printing of object information.
     * 
     * @return: String made up of formatted object info.
     * @params: none
    */
    public String toString() {
        return "["+projectId+"]["+projectName+"]["+String.valueOf(creditsIssued)+"]";
    }
}