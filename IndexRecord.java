import java.io.IOException;
import java.io.RandomAccessFile;

public class IndexRecord {
        private String id;
        private long indexPointer;

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

        String getId() {
            return id;
        }

        long getIndexPointer() {
            return indexPointer;
        }

        void readObject(RandomAccessFile stream) {
            try {
                int stringLength = stream.readInt();
                byte[] idBytes = new byte[stringLength];
                stream.readFully(idBytes);
                this.id = new String(idBytes);
                indexPointer = stream.readLong();
            } catch (IOException e) {
                System.out.println("ERROR: Couldn't read index record in from file.");
                System.exit(-1);
            }
        }

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
