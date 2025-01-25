import java.util.Iterator;
import java.util.LinkedList;
import java.util.Comparator;

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
        freeList.addLast(new MemoryBlock(0, maxSize)); // Initial whole block is free
    }

    /**
     * Allocates a memory block of a requested length (in words). Returns the
     * base address of the allocated block, or -1 if unable to allocate.
     *
     * @param length the length (in words) of the memory block that has to be allocated
     * @return the base address of the allocated block, or -1 if unable to allocate
     */
    public int malloc(int length) {
        Iterator<MemoryBlock> iterator = freeList.iterator();
        while (iterator.hasNext()) {
            MemoryBlock block = iterator.next();
            if (block.size >= length) {
                int baseAddress = block.baseAddress;
                if (block.size == length) {
                    iterator.remove();
                } else {
                    block.baseAddress += length;
                    block.size -= length;
                }
                allocatedList.add(new MemoryBlock(baseAddress, length));
                return baseAddress;
            }
        }
        return -1; // No suitable block found
    }    
    /**
     * Frees the memory block whose base address equals the given address.
     * This implementation deletes the block whose base address equals the given
     * address from the allocatedList, and adds it at the end of the free list.
     *
     * @param baseAddress the starting address of the block to free
     */

    public void free(int baseAddress) {
        Iterator<MemoryBlock> iterator = allocatedList.iterator();
        while (iterator.hasNext()) {
            MemoryBlock block = iterator.next();
            if (block.baseAddress == baseAddress) {
                iterator.remove();
                mergeIntoFreeList(block);
                return;
            }
        }
        throw new IllegalArgumentException("Invalid base address.");
    }    

    /**
     * Helper method to merge a block into the free list, attempting to maintain
     * order and merge contiguous blocks.
     *
     * @param block the block to merge into the free list
     */
    private void mergeIntoFreeList(MemoryBlock block) {
        // Insert and merge logic here, ensuring blocks are merged if they are adjacent
    }

    /**
     * Performs defragmentation of this memory space.
     * Normally, called by malloc, when it fails to find a memory block of the requested size.
     */
    public void defrag() {
    freeList.sort(Comparator.comparingInt(a -> a.baseAddress));
    LinkedList<MemoryBlock> newFreeList = new LinkedList<>();
    MemoryBlock previous = null;

    for (MemoryBlock current : freeList) {
        if (previous != null && previous.baseAddress + previous.size == current.baseAddress) {
            previous.size += current.size;
        } else {
            if (previous != null) {
                newFreeList.add(previous);
            }
            previous = current;
        }
    }
    if (previous != null) {
        newFreeList.add(previous);
    }
    freeList = newFreeList;
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
