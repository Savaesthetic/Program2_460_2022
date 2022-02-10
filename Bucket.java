import java.io.IOException;
import java.io.RandomAccessFile;

/*
 * Bucket
 * Author: Alex Sava
 * 
 * This class is used in Prog2 to describe 
 * a bucket. A bucket holds up to 50 IndexRecords
 * that reference records in a seperate file. A 
 * bucket also holds a depth variable and a 
 * variable referencing the number of currently 
 * held IndexRecords.
 * 
 * There are no public variables or constants.
 * 
 * There is one constructor:
 * Bucket() - Creates a new bucket with 0 
 * IndexRecords, a depth of 1, and an array 
 * of size 50.
 * 
 * Class methods:
 * int getNumRecords();
 * int getBucketDepth();
 * IndexRecord[] getRecords();
 * void setDepth(val);
 * boolean isFull();
 * void pad(idLength);
 * void addRecord(id, dbIndex);
 * void readObject(stream);
 * void writeObject(stream);
 * String toString();
*/
public class Bucket {
    // class variables
    private int numRecords; // The number of records held in the class array
    private int bucketDepth; // The depth of the bucket
    private IndexRecord[] mappings; // Array holding all od the IndexRecords

    Bucket() {
        this.numRecords = 0;
        this.bucketDepth = 1;
        this.mappings = new IndexRecord[50];
    }

    /*
        * "Getters" for class field values.
        * Each returns their respective field value 
        * to the caller.
        */
    int getNumRecords() {
        return numRecords;
    }

    int getBucketDepth() {
        return bucketDepth;
    }

    IndexRecord[] getRecords() {
        return mappings;
    }

    /*
     * "Setters" for class field values
     * Each takes a value as input and replaces the value 
     * in their respective field with the input value.
    */
    void setDepth(int val) {
        bucketDepth = val;
    }

    /*
    * boolean isFull() -- 
    * Checks if the bucket contains 
    * 50 IndexRecords.
    * 
    * @return: bollean -- true if the bucket
    * holds 50 IndexRecords else false.
    * @params: None
    */
    boolean isFull() {
        return numRecords == 50;
    }

    /*
    * boolean pad(int idLength) -- 
    * Pads the bucket to a consistant size 
    * by filling the bucket with up to 50 
    * padded IndexRecords.
    * 
    * @return: None
    * @params: idLength -- Length of the padded 
    * string to be used in the IndexRecords used 
    * as padding.
    */
    void pad(int idLength) {
        StringBuilder padding = new StringBuilder(); // Padded string
        for (int i = 0; i < idLength; i++) {
            padding.append(" ");
        }
        for (int i = numRecords; i < 50; i++) {
            mappings[i] = new IndexRecord(padding.toString());
        }
    }

    /*
    * void addRecord(String id, long dbIndex) -- 
    * Adds an IndexRecord to the bucket and 
    * increments the numRecords variable.
    * 
    * @return: None
    * @params: id -- The string to be used
    * for the new IndexRecord.
    * dbIndex -- The index point to a record 
    * to be used for the new IndexRecord
    */
    void addRecord(String id, long dbIndex) {
        IndexRecord newRecord = new IndexRecord(id, dbIndex); // New record to be added
        mappings[this.numRecords] = newRecord;
        numRecords++;
    }

    /*
    * public void readObject(RandomAccessFile stream) -- 
    * Reads the contents of a binary file into a 
    * Bucket object.
    * 
    * @return: none
    * @params: 
    * RandomAccessFile stream -- a reference to 
    *  the file being read from.
    */
    void readObject(RandomAccessFile stream) {
        for (int i = 0; i < 50; i++) {
            IndexRecord record = new IndexRecord();
            record.readObject(stream);
            this.mappings[i] = record;
        }
        try {
            this.numRecords = stream.readInt();
            this.bucketDepth = stream.readInt();
        } catch (IOException e) {
            System.out.println("Error: Couldn't read bucket integer data.");
            System.exit(-1);
        }
    }

    /*
        * public void writeObject(RandomAccessFile stream) -- 
        * Writes the contents (fields) of the Bucket to 
        * the output file given by the stream object reference.
        * 
        * @return: none
        * @params: RandomAccessFile stream -- a reference to 
        * the file being written to.
        */
    void writeObject(RandomAccessFile stream) {
        for (IndexRecord record : mappings) {
            record.writeObject(stream);
        }
        try {
            stream.writeInt(numRecords);
            stream.writeInt(bucketDepth);
        } catch (IOException e) {
            System.out.println("Error: Error writing bucket integers to file.");
            System.exit(-1);
        }
    }

    /*
     * public String toString() -- overrites the
     * toString method found in classes to allow for 
     * simple printing of object information. Used 
     * for testing.
     * 
     * @return: String made up of formatted object info.
     * @params: none
    */
    public String toString() {
        StringBuilder result = new StringBuilder("Depth: " + bucketDepth + "\nNumRecords: " + 
        numRecords + "\n");
        for (int i = 0; i < mappings.length; i++) {
            result.append("" + i + ". ID: " + mappings[i].getId() + " Pointer: " + mappings[i].getIndexPointer() + "\n");
        }
        return result.toString();
    }
}
