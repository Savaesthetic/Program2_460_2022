/*
 * Directory
 * Author: Alex Sava
 * 
 * This class is used in Prog2 to describe the 
 * directory. The directory is essentially an 
 * array holding references to buckets that 
 * further hold references. The directory also 
 * keeps track of the global depth of the 
 * extendible hashing.
 * 
 * There are no public variables or constants.
 * 
 * There is one constructor:
 * Directory() - Creates a new directory with 
 * global depth one and no references to buckets.
 * 
 * Class methods:
 * int getGlobalDepth();
 * Long[] getMappings();
 * void setGlobalDepth(val);
 * Long getValueAtIndex(index);
 * void setValueAtIndex(index, pointerIndex);
 * void splitDirectory();
*/
public class Directory {
    // class variables
    // Array holding the indexes the point to bucket locations in the file.
    private Long[] indexMappings;
    private int globalDepth; // The global depth of the extendible hashing

    Directory() {
        globalDepth = 1;
        indexMappings = new Long[] {null, null, null, null, null, null, null, null, null, null};
    }

    /*
        * "Getters" for class field values.
        * Each returns their respective field value 
        * to the caller.
        */
    int getGlobalDepth(){
        return globalDepth;
    }

    Long[] getMappings() {
        return indexMappings;
    }

    /*
     * "Setters" for class field values
     * Each takes a value as input and replaces the value 
     * in their respective field with the input value.
    */
    void setGlobalDepth(int val){
        globalDepth = val;
    }
    
    /*
     * Long getValueAtIndex(int index) -- 
     * Returns the value in the class array from 
     * a specified index.
     * 
     * @return: Long holding the index of a bucket in a 
     * bucket file.
     * @params: int index -- index for the class array.
    */
    Long getValueAtIndex(int index) {
        return indexMappings[index];
    }

    /*
     * void setValueAtIndex(int index, Long pointerIndex) -- 
     * Sets the value at the given index in the class array 
     * to the pointerIndex value.
     * 
     * @return: None
     * @params: int index -- index for the class array.
     * Long pointerIndex - value holding the index of a 
     * bucket i na bucket file.
    */
    void setValueAtIndex(int index, Long pointerIndex) {
        indexMappings[index] = pointerIndex;
    }

    /*
     * void splitDirectory() -- 
     * Splits the directory by increasing the class 
     * array size tenfold and reappointing all the 
     * original values to the new directory. Also 
     * increases the global depth of the directory 
     * by 1.
     * 
     * @return: None
     * @params: None
    */
    void splitDirectory() {
        globalDepth++;
        // New directory array 10 times teh size
        Long[] newMappings = new Long[indexMappings.length * 10];
        int z = 0;
        for (int i = 0; i < indexMappings.length; i++) {
            for (int j = 0; j < 10; j++) {
                newMappings[z] = indexMappings[i];
                z++;
            }
        }
        indexMappings = newMappings;
    }
}
