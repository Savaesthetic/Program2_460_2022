public class Directory {
    private Long[] indexMappings;
    private int globalDepth;

    Directory() {
        this.globalDepth = 1;
        indexMappings = new Long[] {null, null, null, null, null, null, null, null, null, null};
    }

    int getGlobalDepth(){
        return this.globalDepth;
    }

    Long[] getMappings() {
        return this.indexMappings;
    }

    Long getValueAtIndex(int index) {
        return indexMappings[index];
    }

    void setValueAtIndex(int index, long pointerIndex) {
        this.indexMappings[index] = pointerIndex;
    }

    void splitDirectory() {
        this.globalDepth++;
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

    void setGlobalDepth(int val){
        this.globalDepth = val;
    }

    void setIndexMappings(Long[] val) {
        this.indexMappings = val;
    }
}
