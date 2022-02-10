import java.io.IOException;
import java.io.RandomAccessFile;

public class Bucket {
    private int numRecords;
    private int bucketDepth;
    private IndexRecord[] mappings;

    Bucket() {
        this.numRecords = 0;
        this.bucketDepth = 1;
        this.mappings = new IndexRecord[50];
    }

    int getNumRecords() {
        return numRecords;
    }

    int getBucketDepth() {
        return bucketDepth;
    }

    IndexRecord[] getRecords() {
        return mappings;
    }

    void setDepth(int val) {
        bucketDepth = val;
    }

    boolean isFull() {
        return numRecords == 50;
    }

    void pad(int idLength) {
        StringBuilder padding = new StringBuilder();
        for (int i = 0; i < idLength; i++) {
            padding.append(" ");
        }
        for (int i = numRecords; i < 50; i++) {
            this.mappings[i] = new IndexRecord(padding.toString());
        }
    }

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

    void addRecord(String id, long dbIndex) {
        IndexRecord newRecord = new IndexRecord(id, dbIndex);
        this.mappings[this.numRecords] = newRecord;
        this.numRecords++;
    }

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

    // USED TO TESTING
    public String toString() {
        StringBuilder result = new StringBuilder("Depth: " + bucketDepth + "\nNumRecords: " + 
        numRecords + "\n");
        for (int i = 0; i < mappings.length; i++) {
            result.append("" + i + ". ID: " + mappings[i].getId() + " Pointer: " + mappings[i].getIndexPointer() + "\n");
        }
        return result.toString();
    }
}
