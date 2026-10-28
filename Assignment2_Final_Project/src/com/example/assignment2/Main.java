package com.example.assignment2;

import java.util.NoSuchElementException;
import java.util.Scanner;

/** Interactive console application for the custom Circular Queue and BST. */
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            try {
                runApplication(scanner);
            } catch (InputEndedException ex) {
                System.out.println("\nInput ended. Goodbye.");
            }
        }
    }

    private static void runApplication(Scanner scanner) {
        CircularQueue queue = new CircularQueue(5);
        BinarySearchTree tree = new BinarySearchTree();
        boolean running = true;

        while (running) {
            System.out.println("\n=== Data Structures Assignment 2 ===");
            System.out.println("MAIN MENU:");
            System.out.println("1. Category A - Linear Data Structure: Circular Queue (capacity " + queue.capacity() + ")");
            System.out.println("2. Category B - Non-linear / Advanced: Binary Search Tree (BST) (" + tree.size() + " nodes)");
            System.out.println("3. Run required guided demonstrations");
            System.out.println("0. Exit");
            switch (readInt(scanner, "Choose an option: ")) {
                case 1 -> queue = queueMenu(scanner, queue);
                case 2 -> bstMenu(scanner, tree);
                case 3 -> runDemonstrations();
                case 0 -> running = false;
                default -> System.out.println("Invalid option. Please select 0, 1, 2, or 3.");
            }
        }
        System.out.println("\nThank you for using Data Structures Assignment 2. Goodbye.");
    }

    /** Returns the queue so a capacity change persists after leaving the submenu. */
    private static CircularQueue queueMenu(Scanner scanner, CircularQueue queue) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\nCATEGORY A - LINEAR DATA STRUCTURE: CIRCULAR QUEUE");
            System.out.printf("Queue %s | size=%d/%d | empty=%s | full=%s%n",
                    queue.display(), queue.size(), queue.capacity(), queue.isEmpty(), queue.isFull());
            System.out.println("1. Enqueue");
            System.out.println("2. Dequeue");
            System.out.println("3. Peek");
            System.out.println("4. Status");
            System.out.println("5. Physical array view");
            System.out.println("6. Clear");
            System.out.println("7. Change capacity (queue must be empty)");
            System.out.println("0. Back");
            switch (readInt(scanner, "Choose an option: ")) {
                case 1 -> {
                    int value = readInt(scanner, "Value to enqueue: ");
                    try {
                        queue.enqueue(value);
                        System.out.println("Enqueued " + value + ".");
                    } catch (IllegalStateException ex) {
                        System.out.println("Operation failed: " + ex.getMessage());
                    }
                }
                case 2 -> {
                    try {
                        System.out.println("Dequeued: " + queue.dequeue());
                    } catch (NoSuchElementException ex) {
                        System.out.println("Operation failed: " + ex.getMessage());
                    }
                }
                case 3 -> {
                    try {
                        System.out.println("Front value: " + queue.peek());
                    } catch (NoSuchElementException ex) {
                        System.out.println("Operation failed: " + ex.getMessage());
                    }
                }
                case 4 -> System.out.println("Empty=" + queue.isEmpty() + ", full=" + queue.isFull()
                        + ", size=" + queue.size() + "/" + queue.capacity()
                        + ", logical FIFO=" + queue.display());
                case 5 -> System.out.println(queue.physicalState());
                case 6 -> {
                    queue.clear();
                    System.out.println("Queue cleared.");
                }
                case 7 -> {
                    if (!queue.isEmpty()) {
                        System.out.println("Clear the queue before changing capacity; no values were removed.");
                        break;
                    }
                    int newCapacity = readPositiveInt(scanner, "New positive capacity: ");
                    queue = new CircularQueue(newCapacity);
                    System.out.println("Queue capacity changed to " + newCapacity + ".");
                }
                case 0 -> inMenu = false;
                default -> System.out.println("Invalid option.");
            }
        }
        return queue;
    }

    private static void bstMenu(Scanner scanner, BinarySearchTree tree) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\nCATEGORY B - NON-LINEAR / ADVANCED: BINARY SEARCH TREE (BST)");
            String heightLabel = tree.isEmpty() ? "N/A" : Integer.toString(tree.height());
            System.out.printf("BST | size=%d | height=%s | empty=%s%n",
                    tree.size(), heightLabel, tree.isEmpty());
            System.out.println("1. Insert");
            System.out.println("2. Search");
            System.out.println("3. Delete");
            System.out.println("4. Traversals");
            System.out.println("5. Min/Max");
            System.out.println("6. Tree structure");
            System.out.println("7. Range search");
            System.out.println("8. Validate");
            System.out.println("9. Clear");
            System.out.println("0. Back");
            switch (readInt(scanner, "Choose an option: ")) {
                case 1 -> {
                    int value = readInt(scanner, "Value to insert: ");
                    System.out.println(tree.insert(value)
                            ? "Inserted " + value + "."
                            : "Duplicate rejected; tree unchanged.");
                }
                case 2 -> {
                    int value = readInt(scanner, "Value to search: ");
                    System.out.println(tree.search(value) ? "Found." : "Not found.");
                }
                case 3 -> {
                    int value = readInt(scanner, "Value to delete: ");
                    System.out.println(tree.delete(value)
                            ? "Deleted."
                            : "Value not found; tree unchanged.");
                }
                case 4 -> printTraversals(tree);
                case 5 -> {
                    try {
                        System.out.println("Minimum: " + tree.findMin());
                        System.out.println("Maximum: " + tree.findMax());
                    } catch (NoSuchElementException ex) {
                        System.out.println("Operation failed: " + ex.getMessage());
                    }
                }
                case 6 -> System.out.println(tree.displayStructure());
                case 7 -> {
                    int low = readInt(scanner, "Inclusive lower bound: ");
                    int high = readInt(scanner, "Inclusive upper bound: ");
                    try {
                        System.out.println("Values in [" + low + ", " + high + "]: "
                                + tree.rangeSearch(low, high));
                    } catch (IllegalArgumentException ex) {
                        System.out.println("Invalid range: " + ex.getMessage());
                    }
                }
                case 8 -> System.out.println("BST ordering valid: " + tree.isValidBST());
                case 9 -> {
                    tree.clear();
                    System.out.println("Tree cleared.");
                }
                case 0 -> inMenu = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void runDemonstrations() {
        demonstrateQueue();
        demonstrateBST();
    }

    private static void demonstrateQueue() {
        System.out.println("\n--- Circular Queue demonstration (capacity 5) ---");
        CircularQueue queue = new CircularQueue(5);
        printQueueState("Start empty", queue);

        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);
        printQueueState("After enqueue 10, 20, 30", queue);

        System.out.println("dequeue() -> " + queue.dequeue() + " (expected 10)");
        printQueueState("After removing the first element", queue);

        queue.enqueue(40);
        queue.enqueue(50);
        queue.enqueue(60);
        printQueueState("After enqueue 40, 50, 60 (wrap-around complete)", queue);
        System.out.println("Expected logical FIFO queue: [20, 30, 40, 50, 60]");

        try {
            queue.enqueue(70);
            System.out.println("ERROR: full queue accepted an extra value.");
        } catch (IllegalStateException ex) {
            System.out.println("Expected full-queue behavior: " + ex.getMessage());
        }
        printQueueState("After rejected enqueue; state must be unchanged", queue);

        System.out.println("Drain queue in FIFO order:");
        while (!queue.isEmpty()) {
            System.out.println("  dequeue() -> " + queue.dequeue());
        }
        printQueueState("After draining the queue", queue);
    }

    private static void printQueueState(String title, CircularQueue queue) {
        System.out.println("\n" + title + ":");
        System.out.println(queue.physicalState());
    }

    private static void demonstrateBST() {
        System.out.println("\n--- Binary Search Tree demonstration ---");
        int[] values = {50, 30, 70, 20, 40, 60, 80};
        BinarySearchTree tree = new BinarySearchTree();
        for (int value : values) {
            tree.insert(value);
        }

        System.out.println("Inserted: 50, 30, 70, 20, 40, 60, 80");
        System.out.println("Tree structure:\n" + tree.displayStructure());
        printTraversals(tree);
        System.out.println("Search 40 (hit): " + tree.search(40));
        System.out.println("Search 90 (miss): " + tree.search(90));
        System.out.println("Minimum / maximum: " + tree.findMin() + " / " + tree.findMax());
        System.out.println("Duplicate 40 insertion result (false means rejected): " + tree.insert(40));
        System.out.println("Range search [35, 70]: " + tree.rangeSearch(35, 70));

        System.out.println("\nDeletion case 1 - leaf node 20:");
        System.out.println("Deleted: " + tree.delete(20));
        System.out.println(tree.displayStructure());

        System.out.println("\nDeletion case 2 - node 30 now has one child (40):");
        System.out.println("Deleted: " + tree.delete(30));
        System.out.println(tree.displayStructure());

        tree.clear();
        for (int value : values) {
            tree.insert(value);
        }
        System.out.println("\nOriginal tree rebuilt for deletion case 3:");
        System.out.println(tree.displayStructure());
        System.out.println("Delete two-child root 50: " + tree.delete(50));
        System.out.println(tree.displayStructure());
        System.out.println("Delete missing value 999: " + tree.delete(999));
        System.out.println("BST ordering valid: " + tree.isValidBST());

        tree.clear();
        System.out.println("\nEmpty-tree behavior:");
        System.out.println("Search 40: " + tree.search(40));
        System.out.println("Delete 40: " + tree.delete(40));
        try {
            tree.findMin();
        } catch (NoSuchElementException ex) {
            System.out.println("findMin(): " + ex.getMessage());
        }
        try {
            tree.findMax();
        } catch (NoSuchElementException ex) {
            System.out.println("findMax(): " + ex.getMessage());
        }
    }

    private static void printTraversals(BinarySearchTree tree) {
        System.out.println("Inorder   : " + tree.inorderTraversal());
        System.out.println("Preorder  : " + tree.preorderTraversal());
        System.out.println("Postorder : " + tree.postorderTraversal());
    }

    private static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            int value = readInt(scanner, prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Enter a positive integer.");
        }
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                throw new InputEndedException();
            }
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid 32-bit integer.");
            }
        }
    }

    private static final class InputEndedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
