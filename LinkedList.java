
/**
 * Represents a list of Nodes.
 */
public class LinkedList {
    private Node first; // pointer to the first element of this list
    private Node last;  // pointer to the last element of this list
    private int size;   // number of elements in this list

    /**
     * Constructs a new list.
     */
    public LinkedList() {
        first = null;
        last = null; // Correctly initialize last as null (for empty list)
        size = 0;
    }

    /**
     * Gets the first node of the list
     * @return The first node of the list.
     */
    public Node getFirst() {
        return this.first;
    }

    /**
     * Gets the last node of the list
     * @return The last node of the list.
     */
    public Node getLast() {
        return this.last;
    }

    /**
     * Gets the current size of the list
     * @return The size of the list.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Gets the node located at the given index in this list.
     * @param index the index of the node to retrieve, between 0 and size
     * @throws IllegalArgumentException if index is negative or greater than or equal to size
     * @return the node at the given index
     */
    public Node getNode(int index) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("index must be between 0 and size");
        }
        Node current = first;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    /**
     * Gets the memory block located at the given index in this list.
     * @param index the index of the node to retrieve
     * @return the memory block at the given index or null if index is invalid
     */
    public MemoryBlock getBlock(int index) {
        Node node = getNode(index);
        return node != null ? node.block : null;
    }

    /**
     * Finds the index of the node containing the given memory block.
     * @param block the memory block to locate
     * @return the index of the node containing the block, or -1 if not found
     */
    public int indexOf(MemoryBlock block) {
        Node current = first;
        for (int index = 0; current != null; index++) {
            if (current.block.equals(block)) {
                return index;
            }
            current = current.next;
        }
        return -1;
    }

    /**
     * Adds a new node with the specified memory block to the beginning of the list.
     * @param block the memory block to add
     */
    public void addFirst(MemoryBlock block) {
        Node newNode = new Node(block);
        if (first == null) { // If the list is empty
            first = newNode;
            last = newNode;  // Make sure both first and last point to the new node
        } else {
            newNode.next = first;
            first = newNode;
        }
        size++;
    }

    /**
     * Adds a new node with the specified memory block to the end of the list.
     * @param block the memory block to add
     */
    public void addLast(MemoryBlock block) {
        Node newNode = new Node(block);
        if (last == null) { // If the list is empty
            first = newNode;
            last = newNode;  // Make sure both first and last point to the new node
        } else {
            last.next = newNode;
            last = newNode;
        }
        size++;
    }

    /**
     * Inserts a new node with the specified memory block at the specified index.
     * @param index the position to insert the node
     * @param block the memory block to insert
     */
    public void add(int index, MemoryBlock block) {
        if (index < 0 || index > size) {
            throw new IllegalArgumentException("Index out of bounds");
        }
    
        if (index == 0) {
            addFirst(block);
        } else if (index == size) {
            addLast(block);
        } else {
            Node previous = getNode(index - 1);
            Node newNode = new Node(block);
            newNode.next = previous.next;
            previous.next = newNode;
            size++;
        }
    }    
    /**
     * Removes the specified node from the list.
     * @param node the node to remove
     */
    public void remove(Node node) {
        if (node == null) {
            throw new NullPointerException("Cannot remove null node");
        }
        if (node == first) {
            first = first.next;
            if (first == null) {
                last = null;
            }
        } else {
            Node current = first;
            while (current != null && current.next != node) {
                current = current.next;
            }
            if (current != null) {
                current.next = node.next;
                if (node == last) {
                    last = current;
                }
            } else {
                throw new IllegalArgumentException("Node not found in the list");
            }
        }
        size--;
    }

    /**
     * Removes the node at the specified index from the list.
     * @param index the index of the node to remove
     */
    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("index must be between 0 and size");
        }
        remove(getNode(index));
    }

    /**
     * Removes the node containing the specified memory block from the list.
     * @param block the memory block of the node to remove
     */
    public void remove(MemoryBlock block) {
        remove(getNode(indexOf(block)));
    }

    /**
     * A textual representation of this list, for debugging.
     * @return a string representing the list
     */
    public String toString() {
        String s = "";
        Node current = first;
        while (current != null) {
        s = s + current.block + " ";
        current = current.next;
        }
        return s;
        }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     * @return a ListIterator over the elements in this list
     */
    public ListIterator iterator() {
        return new ListIterator(first);
    }
}
