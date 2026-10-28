package com.example.assignment2;

import java.util.NoSuchElementException;

/**
 * A fixed-capacity FIFO queue of integers backed by a circular array.
 *
 * Representation invariants:
 * - 0 <= size <= capacity
 * - front is the next index to remove
 * - rear is the next index to insert into
 * - rear == (front + size) % capacity (including the full state)
 * - when empty, front and rear are normalized to zero
 */
public final class CircularQueue {
    private final int[] elements;
    private int front;
    private int rear;
    private int size;

    /** Creates an empty queue with a positive fixed capacity. */
    public CircularQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }
        elements = new int[capacity];
    }

    /** Inserts value at rear; throws IllegalStateException if full. O(1). */
    public void enqueue(int value) {
        if (isFull()) {
            throw new IllegalStateException("Queue is full.");
        }
        elements[rear] = value;
        rear = (rear + 1) % elements.length;
        size++;
    }

    /** Removes and returns the oldest value; throws if empty. O(1). */
    public int dequeue() {
        ensureNotEmpty();
        int value = elements[front];
        front = (front + 1) % elements.length;
        size--;
        if (size == 0) {
            // Keep a predictable representation after the final removal.
            front = 0;
            rear = 0;
        }
        return value;
    }

    /** Returns the oldest value without removing it; throws if empty. O(1). */
    public int peek() {
        ensureNotEmpty();
        return elements[front];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == elements.length;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return elements.length;
    }

    /** Returns the current physical index of the next dequeue operation. */
    public int frontIndex() {
        return front;
    }

    /** Returns the current physical index of the next enqueue operation. */
    public int rearIndex() {
        return rear;
    }

    /** Resets the queue's logical state in O(1) time. */
    public void clear() {
        front = 0;
        rear = 0;
        size = 0;
    }

    /** Returns values in logical FIFO order, independent of physical wrap-around. O(n). */
    public String display() {
        if (isEmpty()) {
            return "[]";
        }
        StringBuilder result = new StringBuilder("[");
        for (int offset = 0; offset < size; offset++) {
            if (offset > 0) {
                result.append(", ");
            }
            result.append(elements[(front + offset) % elements.length]);
        }
        return result.append(']').toString();
    }

    /**
     * Describes every physical slot and marks front/rear positions. Vacated
     * slots are shown as <empty>, even though their primitive array cells may
     * still contain old integer bits. This method is intended for learning and
     * runs in O(capacity) time and output space.
     */
    public String physicalState() {
        StringBuilder result = new StringBuilder("Physical array slots:\n");
        for (int index = 0; index < elements.length; index++) {
            result.append(String.format("  [%d] %s", index,
                    isOccupied(index) ? Integer.toString(elements[index]) : "<empty>"));
            if (index == front && index == rear) {
                result.append("  <-- front and rear");
                if (isEmpty()) {
                    result.append(" (empty queue)");
                } else if (isFull()) {
                    result.append(" (full queue)");
                }
            } else if (index == front) {
                result.append("  <-- front (next dequeue)");
            } else if (index == rear) {
                result.append("  <-- rear (next insertion)");
            }
            result.append('\n');
        }
        result.append("front=").append(front)
                .append(", rear=").append(rear)
                .append(", size=").append(size).append('/').append(capacity())
                .append("\nLogical FIFO order: ").append(display());
        return result.toString();
    }

    private boolean isOccupied(int index) {
        if (isEmpty()) {
            return false;
        }
        int distanceFromFront = (index - front + elements.length) % elements.length;
        return distanceFromFront < size;
    }

    private void ensureNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty.");
        }
    }

    @Override
    public String toString() {
        return display();
    }
}
