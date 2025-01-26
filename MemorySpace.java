import java.util.LinkedList;

/**
 * Represents a managed memory space. The memory space manages a list of
 * allocated memory blocks and a list of free memory blocks. Methods "malloc" and "free"
 * handle memory allocation and deallocation, respectively.
 */
public class MemorySpace {

    private LinkedList<MemoryBlock> allocatedList;  // List of currently allocated memory blocks
    private LinkedList<MemoryBlock> freeList;       // List of currently free memory blocks

    /**
     * Constructs a new managed memory space of a given maximal size.
     *
     * @param maxSize the size of the memory space to be managed
     */
    public MemorySpace(int maxSize) {
        allocatedList = new LinkedList<>();
        freeList = new LinkedList<>();
        freeList.add(new MemoryBlock(0, maxSize)); // Initially, all memory is free.
        System.out.println("Initial memory setup: " + this);
    }

    /**
     * Allocates a memory block of a requested length (in words).
     * Returns the base address of the allocated block, or -1 if unable to allocate.
     *
     * @param length the length (in words) of the memory block to allocate
     * @return the base address of the allocated block, or -1 if allocation fails
     */
    public int malloc(int length) {
        if (length <= 0) {
            System.out.println("Malloc request for non-positive size: " + length);
            return -1; // Cannot allocate non-positive sizes.
        }

        System.out.println("Attempting to allocate: " + length);
        for (MemoryBlock block : freeList) {
            if (block.size >= length) {
                int baseAddress = block.baseAddress;

                if (block.size == length) {
                    freeList.remove(block);
                } else {
                    block.baseAddress += length;
                    block.size -= length;
                }

                MemoryBlock newBlock = new MemoryBlock(baseAddress, length);
                allocatedList.add(newBlock);
                System.out.println("Allocated " + length + " at base " + baseAddress + ". Updated memory: " + this);
                return baseAddress;
            }
        }

        System.out.println("Failed to allocate " + length);
        return -1; // No suitable block found.
    }

    /**
     * Frees the memory block whose base address equals the given address.
     * This implementation deletes the block from the allocatedList and adds it to the free list.
     *
     * @param baseAddress the base address of the block to free
     */
    public void free(int baseAddress) {
        System.out.println("Attempting to free block at: " + baseAddress);
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
            System.out.println("Freed block at base " + baseAddress + ". Before defragmentation: " + this);
            defrag();
        } else {
            throw new IllegalArgumentException("Block with base address " + baseAddress + " not found.");
        }
    }

    /**
     * Performs defragmentation of the free list by merging contiguous blocks.
     */
    public void defrag() {
        System.out.println("Defragmenting...");
        freeList.sort((a, b) -> Integer.compare(a.baseAddress, b.baseAddress));
        LinkedList<MemoryBlock> newFreeList = new LinkedList<>();

        MemoryBlock current = null;
        for (MemoryBlock block : freeList) {
            if (current == null) {
                current = block;
            } else {
                if (current.baseAddress + current.size == block.baseAddress) {
                    current.size += block.size; // Merge contiguous blocks
                } else {
                    newFreeList.add(current);
                    current = block;
                }
            }
        }
        if (current != null) {
            newFreeList.add(current);
        }
        freeList = newFreeList;
        System.out.println("Post-defragmentation: " + this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Memory Space State:\nFree Blocks: ");
        for (MemoryBlock block : freeList) {
            sb.append("(").append(block.baseAddress).append(", ").append(block.size).append(") ");
        }
        sb.append("\nAllocated Blocks: ");
        for (MemoryBlock block : allocatedList) {
            sb.append("(").append(block.baseAddress).append(", ").append(block.size).append(") ");
        }
        return sb.toString();
    }

    /**
     * Represents a memory block with a base address and size.
     */
    private static class MemoryBlock {
        int baseAddress;
        int size;

        MemoryBlock(int baseAddress, int size) {
            this.baseAddress = baseAddress;
            this.size = size;
        }
    }

    public static void main(String[] args) {
        MemorySpace memorySpace = new MemorySpace(100); // Initialize with 100 units of space
        System.out.println("Initial memory state: " + memorySpace);

        int address1 = memorySpace.malloc(10);
        int address2 = memorySpace.malloc(20);
        System.out.println("Memory state after allocations: " + memorySpace);

        memorySpace.free(address1);
        System.out.println("Memory state after freeing first block: " + memorySpace);

        // Demonstrating the use of address2
        System.out.println("Freeing block allocated at address2 (" + address2 + ")");
        memorySpace.free(address2);
        System.out.println("Memory state after freeing second block: " + memorySpace);

        memorySpace.defrag();
        System.out.println("Final memory state after defragmentation: " + memorySpace);
    }
}
