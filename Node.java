/**
 * Represents a node in a linked list. Each node points to a MemoryBlock object. 
 */
public class Node {

    MemoryBlock block;  // The memory block that this node points at
    Node next = null;   // The next node in the list

    /**
     * Constructs a new node, pointing to the given memory block.
     * 
     * @param block
     *        the given memory block
     */
    public Node(MemoryBlock block) {
        this.block = block;
    }

    /**
     * Constructs a new node, pointing to the given memory block and the next node.
     * 
     * @param block
     *        the given memory block
     * @param next
     *        the next node in the list
     */
    public Node(MemoryBlock block, Node next) {
        this.block = block;
        this.next = next;
    }

    /**
     * A textual representation of this node, for debugging.
     * The node's contents, which is a memory block, appears within
     * curly brackets. For example: {(208,10)}. 
     */
    @Override
    public String toString() {
        return "{" + block + "}";
    }
}
