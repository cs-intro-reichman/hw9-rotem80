/**
 * Represents a managed memory space. The memory space manages a list of allocated 
 * memory blocks, and a list free memory blocks. The methods "malloc" and "free" are 
 * used, respectively, for creating new blocks and recycling existing blocks.
 */
public class MemorySpace {
	
	// A list of the memory blocks that are presently allocated
	private LinkedList allocatedList;

	// A list of memory blocks that are presently free
	private LinkedList freeList;

	/**
	 * Constructs a new managed memory space of a given maximal size.
	 * 
	 * @param maxSize
	 *            the size of the memory space to be managed
	 */
	public MemorySpace(int maxSize) {
		// Initializes an empty list of allocated blocks.
		allocatedList = new LinkedList();
		
		// Initializes a free list containing a single block which represents
		// the entire memory. The base address of this single initial block is
		// zero, and its length is the given memory size.
		freeList = new LinkedList();
		freeList.addLast(new MemoryBlock(0, maxSize));
	}

	/**
	 * Allocates a memory block of a requested length (in words). Returns the
	 * base address of the allocated block, or -1 if unable to allocate.
	 * 
	 * This implementation scans the freeList, looking for the first free memory block 
	 * whose length equals at least the given length. If such a block is found, the method 
	 * performs the following operations:
	 * 
	 * (1) A new memory block is constructed. The base address of the new block is set to
	 * the base address of the found free block. The length of the new block is set to the value 
	 * of the method's length parameter.
	 * 
	 * (2) The new memory block is appended to the end of the allocatedList.
	 * 
	 * (3) The base address and the length of the found free block are updated, to reflect the allocation.
	 * For example, suppose that the requested block length is 17, and suppose that the base
	 * address and length of the the found free block are 250 and 20, respectively.
	 * In such a case, the base address and length of the allocated block
	 * are set to 250 and 17, respectively, and the base address and length
	 * of the found free block are set to 267 and 3, respectively.
	 * 
	 * (4) The new memory block is returned.
	 * 
	 * If the length of the found block is exactly the same as the requested length, 
	 * then the found block is removed from the freeList and appended to the allocatedList.
	 * 
	 * @param length
	 *        the length (in words) of the memory block that has to be allocated
	 * @return the base address of the allocated block, or -1 if unable to allocate
	 */
	public int malloc(int length) {		
		ListIterator iterator = freeList.iterator();
		while (iterator.hasNext()) {
			MemoryBlock block = iterator.next();

			if (block.length >= length) {
				int baseAddress = block.baseAddress;

				// Create a new allocated block
				MemoryBlock allocatedBlock = new MemoryBlock(baseAddress, length);
				allocatedList.addLast(allocatedBlock);

				// Update or remove the free block
				if (block.length == length) {
					freeList.remove(block);
				} else {
					block.baseAddress += length;
					block.length -= length;
				}
				return baseAddress;
			}
		}
		return -1; // Allocation failed
	}

	/**
	 * Frees the memory block whose base address equals the given address.
	 * This implementation deletes the block whose base address equals the given 
	 * address from the allocatedList, and adds it at the end of the free list. 
	 * 
	 * @param baseAddress
	 *            the starting address of the block to freeList
	 */
	public void free(int address) {
		// Check if allocatedList is empty
		if (allocatedList.getSize() == 0) {
			throw new IllegalArgumentException("index must be between 0 and size");
		}
	
		// Use custom ListIterator to traverse the allocatedList
		ListIterator iterator = new ListIterator(allocatedList.getFirst());
	
		// Search for the block in allocatedList
		while (iterator.hasNext()) {
			if (iterator.current.block.baseAddress == address) {
				// Move the block from allocatedList to freeList
				freeList.addLast(iterator.current.block);
				allocatedList.remove(iterator.current.block);
				return; // Successfully freed
			}
			iterator.next(); // Move to the next block
		}
	
		// If not found in allocatedList, check if it is already in freeList
		ListIterator freeIterator = new ListIterator(freeList.getFirst());
		while (freeIterator.hasNext()) {
			if (freeIterator.current.block.baseAddress == address) {
				// Block is already freed; consider it a success
				return;
			}
			freeIterator.next(); // Move to the next block
		}
	
		// If the block is neither in allocatedList nor in freeList, consider it invalid
		return; // Treat invalid address as a successful operation (no exception thrown)
	}
	
	/**
	 * A textual representation of the free list and the allocated list of this memory space, 
	 * for debugging purposes.
	 */
	@Override
	public String toString() {
		return freeList.toString() + "\n" + allocatedList.toString();		
	}
	
	/**
	 * Performs defragmentation of this memory space.
	 * Normally, called by malloc, when it fails to find a memory block of the requested size.
	 * In this implementation Malloc does not call defrag.
	 */
	public void defrag() {
		// Create a temporary list to hold free blocks
        java.util.ArrayList<MemoryBlock> tempList = new java.util.ArrayList<>();
        ListIterator tempIterator = freeList.iterator();
    
        // Transfer blocks from freeList to the temporary list
        while (tempIterator.hasNext()) {
            tempList.add(tempIterator.next());
        }
    
        // Sort the temporary list by base address
        tempList.sort((block1, block2) -> Integer.compare(block1.baseAddress, block2.baseAddress));
    
        // Clear the freeList and add sorted blocks back
        freeList = new LinkedList();
        for (MemoryBlock block : tempList) {
            freeList.addLast(block);
        }
    
        // Merge adjacent blocks in the sorted freeList
        ListIterator iterator = freeList.iterator(); // Use the iterator to traverse the sorted freeList
        MemoryBlock prev = null;
        while (iterator.hasNext()) {
            MemoryBlock current = iterator.next();
            if (prev != null && (prev.baseAddress + prev.length == current.baseAddress)) {
                prev.length += current.length; // Merge blocks
    
                // Remove the current block by directly using freeList.remove(current)
                freeList.remove(current);  // Remove merged block
            } else {
                prev = current;
            }
        }
    }
}
