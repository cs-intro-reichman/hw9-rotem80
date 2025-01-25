/**
 * Represents a managed memory space. The memory space manages a list of allocated
 * memory blocks, and a list of free memory blocks. The methods "malloc" and "free" are
 * used, respectively, for creating new blocks and recycling existing blocks.
 */
public class MemorySpace {

    // A list of the memory blocks that are presently allocated
    private java.util.LinkedList<MemoryBlock> allocatedList;

    // A list of memory blocks that are presently free
    private java.util.LinkedList<MemoryBlock> freeList;

    /**
     * Constructs a new managed memory space of a given maximal size.
     *
     * @param maxSize the size of the memory space to be managed
     */
    public MemorySpace(int maxSize) {
        allocatedList = new java.util.LinkedList<>();
        freeList = new java.util.LinkedList<>();
        freeList.addLast(new MemoryBlock(0, maxSize));
    }

    /**
     * Allocates a memory block of a requested length (in words). Returns the
     * base address of the allocated block, or -1 if unable to allocate.
     *
     * @param length the length (in words) of the memory block that has to be allocated
     * @return the base address of the allocated block, or -1 if unable to allocate
     */
    public int malloc(int length) {
        for (MemoryBlock block : freeList) {
            if (block.size >= length) {
                int baseAddress = block.baseAddress;
                allocatedList.add(new MemoryBlock(baseAddress, length));
                if (block.size > length) {
                    block.baseAddress += length;
                    block.size -= length;
                } else {
                    freeList.remove(block);
                }
                return baseAddress;
            }
        }
        return -1;
    }

    /**
     * Frees the memory block whose base address equals the given address.
     * This implementation deletes the block whose base address equals the given
     * address from the allocatedList, and adds it at the end of the free list.
     *
     * @param baseAddress the starting address of the block to free
     */
    public void free(int baseAddress) {
        MemoryBlock toFree = null;
        for (MemoryBlock block : allocatedList) {
            if (block.baseAddress == baseAddress) {
                toFree = block;
                break;
            }
        }
        if (toFree != null) {
            allocatedList.remove(toFree);
            freeList.add(toFree);
        } else {
            throw new IllegalArgumentException("Invalid base address.");
        }
    }

    /**
     * A textual representation of the free list and the allocated list of this memory space,
     * for debugging purposes.
     */
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

    /**
     * Performs defragmentation of this memory space.
     * Normally, called by malloc, when it fails to find a memory block of the requested size.
     */
    public void defrag() {
        freeList.sort((a, b) -> Integer.compare(a.baseAddress, b.baseAddress));
        java.util.LinkedList<MemoryBlock> newFreeList = new java.util.LinkedList<>();
        MemoryBlock last = null;

        for (MemoryBlock current : freeList) {
            if (last != null && last.baseAddress + last.size == current.baseAddress) {
                last.size += current.size;
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

    /**
     * Represents a memory block with a base address and size.
     */
    private class MemoryBlock {
        int baseAddress;
        int size;

        MemoryBlock(int baseAddress, int size) {
            this.baseAddress = baseAddress;
            this.size = size;
        }
    }
}
