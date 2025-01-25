public class MemorySpace {

    private java.util.LinkedList<MemoryBlock> allocatedList;
    private java.util.LinkedList<MemoryBlock> freeList;

    public MemorySpace(int maxSize) {
        allocatedList = new java.util.LinkedList<>();
        freeList = new java.util.LinkedList<>();
        freeList.add(new MemoryBlock(0, maxSize));
    }

    public int malloc(int length) {
        java.util.Iterator<MemoryBlock> it = freeList.iterator();
        while (it.hasNext()) {
            MemoryBlock block = it.next();
            if (block.size >= length) {
                int baseAddress = block.baseAddress;
                if (block.size == length) {
                    it.remove(); // Completely use the free block
                } else {
                    block.baseAddress += length; // Shrink the free block
                    block.size -= length;
                }
                allocatedList.add(new MemoryBlock(baseAddress, length));
                return baseAddress;
            }
        }
        return -1;
    }

    public void free(int baseAddress) {
        java.util.Iterator<MemoryBlock> it = allocatedList.iterator();
        while (it.hasNext()) {
            MemoryBlock block = it.next();
            if (block.baseAddress == baseAddress) {
                it.remove();
                freeList.add(new MemoryBlock(baseAddress, block.size)); // Add the freed block to the free list
                return;
            }
        }
        throw new IllegalArgumentException("Invalid base address: " + baseAddress);
    }

    public void defrag() {
        freeList.sort((a, b) -> Integer.compare(a.baseAddress, b.baseAddress));
        java.util.LinkedList<MemoryBlock> newFreeList = new java.util.LinkedList<>();
        MemoryBlock last = null;

        for (MemoryBlock current : freeList) {
            if (last != null && last.baseAddress + last.size == current.baseAddress) {
                last.size += current.size; // Merge adjacent free blocks
            } else {
                if (last != null) {
                    newFreeList.add(last);
                }
                last = current;
            }
        }
        if (last != null) {
            newFreeList.add(last);
        }
        freeList = newFreeList;
    }

    @Override
    public String toString() {
        java.util.List<MemoryBlock> allBlocks = new java.util.LinkedList<>();
        allBlocks.addAll(freeList);
        allBlocks.addAll(allocatedList);
        allBlocks.sort((a, b) -> Integer.compare(a.baseAddress, b.baseAddress));
        return allBlocks.stream()
            .map(block -> String.format("(%d , %d)", block.baseAddress, block.size))
            .collect(java.util.stream.Collectors.joining("\n"));
    }

    private class MemoryBlock {
        int baseAddress;
        int size;

        MemoryBlock(int baseAddress, int size) {
            this.baseAddress = baseAddress;
            this.size = size;
        }
    }
}
