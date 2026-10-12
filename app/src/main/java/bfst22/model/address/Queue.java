package bfst22.model.address;


import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  The {@code Queue} class represents a first-in-first-out (FIFO)
 *  queue of generic items.
 *  It supports the usual <em>enqueue</em> and <em>dequeue</em>
 *  operations, along with methods for peeking at the first item,
 *  testing if the queue is empty, and iterating through
 *  the items in FIFO order.
 *  <p>
 *  This implementation uses a singly linked list with a static nested class for
 *  linked-list nodes.
 *  The <em>enqueue</em>, <em>dequeue</em>, <em>peek</em>, <em>size</em>, and <em>is-empty</em>
 *  operations all take constant time in the worst case.
 *  <p>
 *  For additional documentation, see <a href="https://algs4.cs.princeton.edu/13stacks">Section 1.3</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *
 *  @param <Item> the generic type of an item in this queue
 */
public class Queue<Item> implements Iterable<Item> {
    private Node<Item> first;    // beginning of queue
    private Node<Item> last;     // end of queue
    private int n;               // number of elements on queue

    // helper linked list class
    private static class Node<Item> {
        private Item item;
        private Node<Item> next;
    }

    /**
     * Initializes an empty queue.
     */
    public Queue() {
        first = null;
        last  = null;
        n = 0;
    }

    /**
     * Returns true if this queue is empty.
     *
     * @return {@code true} if this queue is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return first == null;
    }

    /**
     * Returns the number of items in this queue.
     *
     * @return the number of items in this queue
     */
    public int size() {
        return n;
    }

    /**
     * Adds the item to this queue.
     *
     * @param  item the item to add
     */
    public void enqueue(Item item) {
        Node<Item> oldlast = last;
        last = new Node<>();
        last.item = item;
        last.next = null;
        if (isEmpty()) first = last;
        else           oldlast.next = last;
        n++;
    }

    /**
     * Returns a string representation of this queue.
     *
     * @return the sequence of items in FIFO order, separated by spaces
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Item item : this) {
            s.append(item);
            s.append(' ');
        }
        return s.toString();
    }

    /**
     * Returns an iterator that iterates over the items in this queue in FIFO order.
     *
     * @return an iterator that iterates over the items in this queue in FIFO order
     */
    public Iterator<Item> iterator()  {
        return new LinkedIterator(first);
    }

    // an iterator, doesn't implement remove() since it's optional
    private class LinkedIterator implements Iterator<Item> {
        private Node<Item> current;

        public LinkedIterator(Node<Item> first) {
            current = first;
        }

        public boolean hasNext()  { return current != null;                     }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next;
            return item;
        }
    }
}

