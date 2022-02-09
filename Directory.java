public class Directory {
    private Long[] indexMappings;
    private int globalDepth;

    Directory() {
        globalDepth = 1;
        indexMappings = new Long[] {null, null, null, null, null, null, null, null, null, null};
    }

    int getGlobalDepth(){
        return globalDepth;
    }

    Long[] getMappings() {
        return indexMappings;
    }

    Long getValueAtIndex(int index) {
        return indexMappings[index];
    }

    void setValueAtIndex(int index, Long pointerIndex) {
        indexMappings[index] = pointerIndex;
    }

    void splitDirectory() {
        globalDepth++;
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
        globalDepth = val;
    }

    void setIndexMappings(Long[] val) {
        indexMappings = val;
    }
}
