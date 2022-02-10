import java.io.IOException;
import java.io.RandomAccessFile;

/*
 * IndexRecord
 * Author: Alex Sava
 * 
 * This class is used in Prog2 to describe IndexRecord 
 * objects held in buckets. An IndexRecord is used to 
 * access records from a stored file so the IndexRecord 
 * class holds a records name and a pointer to the 
 * record in the stored file.
 * 
 * There are no public variables or constants.
 * 
 * Threr are three constructors:
 * IndexRecord() - Creates an empty object.
 * IndexRecord(id) - Creates an IndexRecord with the 
 * given id.
 * IndexRecord(id, indexPointer) - Creates an 
 * IndexRecord with the given id and indexPointer.
 * 
 * Class methods:
 * String getId();
 * long getIndexPointer();
 * void readObject(stream);
 * void writeObject(stream);
*/
public class IndexRecord {
        // Class variables
        private String id; // Id of the project in the record
        private long indexPointer; // Index of the record in the file.

        // Constructors
        IndexRecord() {
        }

        IndexRecord(String id) {
            this.id = id;
            this.indexPointer = 0;
        }

        IndexRecord(String id, long indexPointer) {
            this.id = id;
            this.indexPointer = indexPointer;
        }

        /*
        * "Getters" for class field values.
        * Each returns their respective field value 
        * to the caller.
        */
        String getId() {
            return id;
        }

        long getIndexPointer() {
            return indexPointer;
        }


        /*
        * public void readObject(RandomAccessFile stream) -- 
        * Reads the contents of a binary file into a 
        * IndexRecord object.
        * 
        * @return: none
        * @params: 
        * RandomAccessFile stream -- a reference to 
        *  the file being read from.
        */
        void readObject(RandomAccessFile stream) {
            try {
                // The length of the string to be read in for the id
                int stringLength = stream.readInt();
                // Byte array used to store the contents of the binary file
                byte[] idBytes = new byte[stringLength];
                stream.readFully(idBytes);
                id = new String(idBytes);
                indexPointer = stream.readLong();
            } catch (IOException e) {
                System.out.println("ERROR: Couldn't read index record in from file.");
                System.exit(-1);
            }
        }

        /*
        * public void writeObject(RandomAccessFile stream) -- 
        * Writes the contents (fields) of the IndexRecord to 
        * the output file given by the stream object reference.
        * 
        * @return: none
        * @params: RandomAccessFile stream -- a reference to 
        * the file being written to.
        */
        void writeObject(RandomAccessFile stream) {
            try {
                stream.writeInt(id.length());
                stream.writeBytes(id);
                stream.writeLong(indexPointer);
            } catch (IOException e) {
                System.out.println("Error: Error writing record index to bucket file.");
                System.exit(-1);
            }
        }
}
