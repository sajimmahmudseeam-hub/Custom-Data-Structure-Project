package com.example.assignment2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.TreeSet;

/** Dependency-free regression tests for the two custom data structures. */
public final class Assignment2Tests {
    private static int passed;

    private Assignment2Tests() { }

    public static void main(String[] args) {
        testQueueRequiredExampleAndVisualizer();
        testQueueEdgeCases();
        testQueueFailureState();
        testQueueRandomizedOperations();
        testBstRequiredExample();
        testBstRangeSearch();
        testBstDeletionCases();
        testBstDeletionSuccessorEdgeCases();
        testBstEmptyAndBoundaryCases();
        testBstVisualization();
        testInteractiveMenusAndGuidedDemo();
        testBstStressAndRandomOperations();
        System.out.println("All " + passed + " checks passed.");
    }

    private static void testQueueRequiredExampleAndVisualizer() {
        CircularQueue queue = new CircularQueue(5);
        check(queue.isEmpty() && !queue.isFull(), "new queue starts empty and not full");
        check(queue.display().equals("[]"), "empty queue displays []");
        check(queue.physicalState().contains("front=0, rear=0, size=0/5"),
                "empty physical-state view shows normalized indexes");

        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);
        check(queue.display().equals("[10, 20, 30]"), "required queue first enqueue sequence");
        check(queue.frontIndex() == 0 && queue.rearIndex() == 3 && queue.size() == 3,
                "queue indexes after first three enqueues");
        check(queue.dequeue() == 10, "required queue dequeue removes 10");
        check(queue.display().equals("[20, 30]"), "queue logical order after dequeuing 10");
        check(queue.physicalState().contains("[0] <empty>"),
                "physical-state view hides the vacated slot's stale primitive value");
        check(queue.frontIndex() == 1 && queue.rearIndex() == 3,
                "front and rear after first dequeue");

        queue.enqueue(40);
        queue.enqueue(50);
        queue.enqueue(60);
        check(queue.display().equals("[20, 30, 40, 50, 60]"),
                "required queue example produces exact logical FIFO sequence");
        check(queue.isFull() && queue.size() == 5, "required queue example fills every slot");
        check(queue.frontIndex() == 1 && queue.rearIndex() == 1,
                "full queue safely has matching front and rear indexes");
        check(queue.physicalState().contains("[0] 60"), "visualizer shows wrapped value at slot zero");
        check(queue.physicalState().contains("[1] 20  <-- front and rear (full queue)"),
                "visualizer labels front and rear correctly in full state");
        check(queue.physicalState().contains("Logical FIFO order: [20, 30, 40, 50, 60]"),
                "visualizer separates physical layout from logical FIFO order");
        checkThrows(IllegalStateException.class, () -> queue.enqueue(70),
                "full queue rejects an extra enqueue");
        check(queue.display().equals("[20, 30, 40, 50, 60]"),
                "rejected full enqueue leaves queue unchanged");

        int[] expectedDrain = {20, 30, 40, 50, 60};
        for (int expected : expectedDrain) {
            check(queue.dequeue() == expected, "required queue drains in FIFO order at " + expected);
        }
        check(queue.isEmpty() && queue.frontIndex() == 0 && queue.rearIndex() == 0,
                "drained queue returns to canonical empty state");
    }

    private static void testQueueEdgeCases() {
        checkThrows(IllegalArgumentException.class, () -> new CircularQueue(0),
                "zero capacity rejected");
        checkThrows(IllegalArgumentException.class, () -> new CircularQueue(-1),
                "negative capacity rejected");
        CircularQueue queue = new CircularQueue(3);
        checkThrows(NoSuchElementException.class, queue::dequeue, "empty dequeue throws");
        checkThrows(NoSuchElementException.class, queue::peek, "empty peek throws");
        queue.enqueue(-7);
        check(queue.peek() == -7, "negative integer is accepted");
        queue.enqueue(0);
        check(queue.size() == 2, "size increments accurately");
        check(queue.dequeue() == -7, "FIFO removes oldest first");
        check(queue.peek() == 0 && queue.size() == 1, "peek does not remove the front value");
        queue.clear();
        check(queue.isEmpty() && queue.size() == 0, "clear resets logical state");
        check(queue.display().equals("[]"), "clear displays an empty queue");
        check(queue.physicalState().contains("[1] <empty>"),
                "clear hides stale values from the physical-state view");

        CircularQueue single = new CircularQueue(1);
        single.enqueue(Integer.MIN_VALUE);
        check(single.isFull(), "capacity-one queue becomes full");
        check(single.dequeue() == Integer.MIN_VALUE, "capacity-one queue dequeues boundary value");
        single.enqueue(Integer.MAX_VALUE);
        check(single.peek() == Integer.MAX_VALUE, "capacity-one queue can be reused");
        check(single.dequeue() == Integer.MAX_VALUE && single.isEmpty(),
                "capacity-one queue returns to empty after reuse");
    }

    private static void testQueueFailureState() {
        CircularQueue queue = new CircularQueue(2);
        queue.enqueue(11);
        queue.enqueue(22);
        checkThrows(IllegalStateException.class, () -> queue.enqueue(33), "full enqueue throws");
        check(queue.display().equals("[11, 22]") && queue.size() == 2,
                "failed enqueue leaves queue unchanged");
        check(queue.dequeue() == 11 && queue.dequeue() == 22,
                "queue remains FIFO after failed enqueue");
        checkThrows(NoSuchElementException.class, queue::dequeue,
                "empty dequeue remains safe after draining");
        check(queue.isEmpty() && queue.size() == 0,
                "failed operation preserves valid empty state");
        queue.enqueue(44);
        queue.clear();
        queue.enqueue(55);
        check(queue.display().equals("[55]") && queue.peek() == 55,
                "queue reuse after clear contains no stale logical values");
    }

    private static void testQueueRandomizedOperations() {
        CircularQueue queue = new CircularQueue(17);
        List<Integer> reference = new ArrayList<>();
        Random random = new Random(20261026L);

        for (int i = 0; i < 1_000; i++) {
            int action = random.nextInt(5);
            if (action <= 1) {
                if (reference.size() < queue.capacity()) {
                    int value = random.nextInt();
                    queue.enqueue(value);
                    reference.add(value);
                } else {
                    checkQuiet(queue.dequeue() == reference.remove(0),
                            "random full queue removes the oldest value before reuse");
                }
            } else if (action == 2) {
                if (!reference.isEmpty()) {
                    checkQuiet(queue.dequeue() == reference.remove(0),
                            "random queue dequeue matches FIFO reference");
                }
            } else if (action == 3) {
                if (!reference.isEmpty()) {
                    checkQuiet(queue.peek() == reference.get(0),
                            "random queue peek matches reference front");
                }
            } else if (random.nextInt(20) == 0) {
                queue.clear();
                reference.clear();
            }

            checkQuiet(queue.display().equals(reference.toString()),
                    "random queue contents match reference sequence");
            checkQuiet(queue.size() == reference.size(), "random queue size matches reference");
            checkQuiet(queue.isEmpty() == reference.isEmpty(), "random queue empty state matches reference");
            checkQuiet(queue.isFull() == (reference.size() == queue.capacity()),
                    "random queue full state matches reference");
            checkQuiet(queue.rearIndex() == (queue.frontIndex() + queue.size()) % queue.capacity(),
                    "random queue index invariant holds");
        }
    }

    private static void testBstRequiredExample() {
        BinarySearchTree tree = requiredTree();
        check(tree.size() == 7, "required BST has seven unique nodes");
        check(tree.isValidBST(), "required BST satisfies ordering invariant");
        check(tree.inorderTraversal().equals(Arrays.asList(20, 30, 40, 50, 60, 70, 80)),
                "required BST inorder traversal");
        check(tree.preorderTraversal().equals(Arrays.asList(50, 30, 20, 40, 70, 60, 80)),
                "required BST preorder traversal");
        check(tree.postorderTraversal().equals(Arrays.asList(20, 40, 30, 60, 80, 70, 50)),
                "required BST postorder traversal");
        check(tree.search(40), "required BST successful search for 40");
        check(!tree.search(90), "required BST unsuccessful search for 90");
        check(tree.findMin() == 20, "required BST minimum is 20");
        check(tree.findMax() == 80, "required BST maximum is 80");
        check(!tree.insert(40), "required BST ignores duplicate 40");
        check(tree.size() == 7, "duplicate insertion does not increase size");
        check(tree.height() == 2, "required BST height is two edges");
    }

    private static void testBstRangeSearch() {
        BinarySearchTree tree = requiredTree();
        check(tree.rangeSearch(35, 70).equals(Arrays.asList(40, 50, 60, 70)),
                "range search returns inclusive matching values in sorted order");
        check(tree.rangeSearch(50, 50).equals(Arrays.asList(50)),
                "range search supports equal lower and upper bounds");
        check(tree.rangeSearch(81, 100).isEmpty(), "range search returns empty when no value matches");
        check(tree.rangeSearch(20, 80).equals(tree.inorderTraversal()),
                "range search can include every tree value");
        checkThrows(IllegalArgumentException.class, () -> tree.rangeSearch(10, 0),
                "range search rejects lower bound greater than upper bound");

        BinarySearchTree empty = new BinarySearchTree();
        check(empty.rangeSearch(-10, 10).isEmpty(), "range search on empty tree returns empty list");
        tree.clear();
        tree.insert(Integer.MIN_VALUE);
        tree.insert(0);
        tree.insert(Integer.MAX_VALUE);
        check(tree.rangeSearch(Integer.MIN_VALUE, Integer.MAX_VALUE)
                        .equals(Arrays.asList(Integer.MIN_VALUE, 0, Integer.MAX_VALUE)),
                "range search supports integer boundary values");
        check(tree.rangeSearch(1, Integer.MAX_VALUE).equals(Arrays.asList(Integer.MAX_VALUE)),
                "range search applies upper bound correctly at integer maximum");
    }

    private static void testBstDeletionCases() {
        BinarySearchTree tree = requiredTree();
        check(tree.delete(20), "delete leaf node 20");
        check(tree.inorderTraversal().equals(Arrays.asList(30, 40, 50, 60, 70, 80)),
                "leaf deletion produces expected inorder");
        check(tree.isValidBST(), "tree remains valid after leaf deletion");

        check(tree.delete(30), "delete one-child node 30 after deleting 20");
        check(tree.inorderTraversal().equals(Arrays.asList(40, 50, 60, 70, 80)),
                "one-child deletion promotes node 40");
        check(tree.isValidBST(), "tree remains valid after one-child deletion");

        tree = requiredTree();
        check(tree.delete(50), "delete two-child root 50");
        check(tree.inorderTraversal().equals(Arrays.asList(20, 30, 40, 60, 70, 80)),
                "two-child deletion uses inorder successor 60");
        check(tree.isValidBST(), "tree remains valid after two-child deletion");
        check(tree.size() == 6, "two-child deletion decreases size once");
        check(!tree.delete(999), "deleting a missing value returns false");
        check(tree.size() == 6, "missing-value deletion leaves size unchanged");

        BinarySearchTree rootOneChild = new BinarySearchTree();
        rootOneChild.insert(10);
        rootOneChild.insert(5);
        check(rootOneChild.delete(10), "delete root with one child");
        check(rootOneChild.inorderTraversal().equals(Arrays.asList(5)),
                "root replaced by only child");
        check(rootOneChild.delete(5), "delete final root leaf");
        check(rootOneChild.isEmpty() && rootOneChild.isValidBST(),
                "deleting last node leaves valid empty tree");
    }

    private static void testBstDeletionSuccessorEdgeCases() {
        BinarySearchTree directSuccessor = new BinarySearchTree();
        directSuccessor.insert(10);
        directSuccessor.insert(5);
        directSuccessor.insert(15);
        check(directSuccessor.delete(10), "two-child delete works when successor is direct right child");
        check(directSuccessor.inorderTraversal().equals(Arrays.asList(5, 15))
                        && directSuccessor.isValidBST(),
                "direct successor deletion preserves ordering");

        BinarySearchTree successorWithRightChild = new BinarySearchTree();
        for (int value : new int[]{50, 30, 80, 60, 70}) {
            successorWithRightChild.insert(value);
        }
        check(successorWithRightChild.delete(50), "two-child delete handles successor with a right child");
        check(successorWithRightChild.inorderTraversal().equals(Arrays.asList(30, 60, 70, 80)),
                "successor right child is correctly relinked");
        check(successorWithRightChild.isValidBST() && successorWithRightChild.size() == 4,
                "successor-with-child deletion keeps valid size and ordering");
    }

    private static void testBstEmptyAndBoundaryCases() {
        BinarySearchTree tree = new BinarySearchTree();
        check(tree.isEmpty() && tree.size() == 0, "new BST is empty");
        check(tree.height() == -1, "empty BST height is -1");
        check(tree.inorderTraversal().isEmpty(), "empty inorder is empty");
        check(tree.preorderTraversal().isEmpty(), "empty preorder is empty");
        check(tree.postorderTraversal().isEmpty(), "empty postorder is empty");
        check(!tree.search(10), "empty-tree search returns false");
        check(!tree.delete(10), "empty-tree deletion returns false");
        checkThrows(NoSuchElementException.class, tree::findMin, "empty-tree minimum throws");
        checkThrows(NoSuchElementException.class, tree::findMax, "empty-tree maximum throws");

        tree.insert(Integer.MIN_VALUE);
        tree.insert(Integer.MAX_VALUE);
        check(tree.isValidBST(), "BST invariant supports integer boundaries");
        check(tree.inorderTraversal().equals(Arrays.asList(Integer.MIN_VALUE, Integer.MAX_VALUE)),
                "boundary values are ordered correctly");
        check(tree.findMin() == Integer.MIN_VALUE, "boundary minimum correct");
        check(tree.findMax() == Integer.MAX_VALUE, "boundary maximum correct");
        tree.clear();
        check(tree.isEmpty() && tree.size() == 0, "BST clear resets root and size");
    }

    private static void testBstVisualization() {
        BinarySearchTree tree = requiredTree();
        String diagram = tree.displayStructure();
        check(diagram.startsWith("50\n|-- L: 30"), "tree visualizer labels the root left child");
        check(diagram.contains("|   |-- L: 20") && diagram.contains("|   `-- R: 40"),
                "tree visualizer labels both child directions in the left subtree");
        check(diagram.contains("`-- R: 70") && diagram.contains("    |-- L: 60")
                        && diagram.contains("    `-- R: 80"),
                "tree visualizer labels both child directions in the right subtree");

        tree.clear();
        check(tree.displayStructure().equals("(empty tree)"),
                "tree visualizer handles empty tree");

        BinarySearchTree leftOnly = new BinarySearchTree();
        leftOnly.insert(10);
        leftOnly.insert(5);
        check(leftOnly.displayStructure().contains("`-- L: 5"),
                "tree visualizer distinguishes a lone left child");

        BinarySearchTree rightOnly = new BinarySearchTree();
        rightOnly.insert(10);
        rightOnly.insert(15);
        check(rightOnly.displayStructure().contains("`-- R: 15"),
                "tree visualizer distinguishes a lone right child");

        BinarySearchTree tall = new BinarySearchTree();
        for (int value = 0; value < 12; value++) {
            tall.insert(value);
        }
        String compact = tall.displayStructure();
        check(compact.startsWith("Tree is tall; showing parent/child links:"),
                "tall tree visualizer uses compact layout instead of deep indentation");
        check(compact.contains("0 -> left: -, right: 1")
                        && compact.contains("11 -> left: -, right: -"),
                "compact tree visualizer preserves parent-child links");
    }

    private static void testInteractiveMenusAndGuidedDemo() {
        String input = "1\n1\n123\n0\n1\n4\n0\n"
                + "2\n1\n50\n0\n2\n2\n50\n0\n3\n0\n";
        String output = runMainWithInput(input);
        checkQuiet(output.contains("logical FIFO=[123]"),
                "queue state persists when leaving and reopening its menu");
        checkQuiet(output.contains("Value to search: Found."),
                "BST state persists when leaving and reopening its menu");
        checkQuiet(output.contains("BST | size=1 | height=0 | empty=false"),
                "BST menu reports compact status rather than dumping every value repeatedly");
        checkQuiet(output.contains("Expected logical FIFO queue: [20, 30, 40, 50, 60]"),
                "guided demo prints the exact required queue sequence");
        checkQuiet(output.contains("Search 90 (miss): false"),
                "guided demo uses the required unsuccessful BST search value");
        checkQuiet(output.contains("Range search [35, 70]: [40, 50, 60, 70]"),
                "guided demo shows the range-search feature");
        checkQuiet(output.contains("Duplicate 40 insertion result (false means rejected): false"),
                "guided demo explains duplicate-insert return value");
        checkQuiet(output.contains("Delete two-child root 50: true"),
                "guided demo shows two-child BST deletion");
        checkQuiet(output.contains("Goodbye."), "main menu exits cleanly");

        String capacityOutput = runMainWithInput("1\n7\n3\n0\n0\n");
        checkQuiet(capacityOutput.contains("Queue capacity changed to 3."),
                "queue capacity can be changed when the queue is empty");
        checkQuiet(capacityOutput.contains("Category A - Linear Data Structure: Circular Queue (capacity 3)"),
                "changed queue capacity persists after returning to the main menu");

        String invalidInputOutput = runMainWithInput("not-a-number\n0\n");
        checkQuiet(invalidInputOutput.contains("Please enter a valid 32-bit integer."),
                "invalid integer input is re-prompted rather than crashing");

        String eofOutput = runMainWithInput("1\n");
        checkQuiet(eofOutput.contains("Input ended. Goodbye."),
                "end-of-input exits cleanly instead of throwing an uncaught exception");
    }

    private static String runMainWithInput(String input) {
        java.io.InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try (PrintStream captureStream = new PrintStream(captured, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(captureStream);
            Main.main(new String[0]);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        return captured.toString(StandardCharsets.UTF_8);
    }

    private static void testBstStressAndRandomOperations() {
        BinarySearchTree deep = new BinarySearchTree();
        final int count = 5_000;
        for (int i = 0; i < count; i++) {
            deep.insert(i);
        }
        checkQuiet(deep.size() == count, "deep BST size after sorted insertion");
        checkQuiet(deep.height() == count - 1, "deep BST height without recursion");
        checkQuiet(deep.isValidBST(), "deep BST validation without recursion");
        checkQuiet(deep.search(count - 1), "deep BST search at deepest node");
        checkQuiet(deep.preorderTraversal().size() == count, "deep BST preorder without recursion");
        checkQuiet(deep.postorderTraversal().size() == count, "deep BST postorder without recursion");
        checkQuiet(deep.inorderTraversal().get(count - 1) == count - 1, "deep BST inorder is sorted");
        checkQuiet(deep.rangeSearch(count - 3, count - 1).equals(Arrays.asList(count - 3, count - 2, count - 1)),
                "range search remains correct near the end of a deep skewed tree");
        checkQuiet(deep.displayStructure().contains("4999 -> left: -, right: -"),
                "tree visualizer falls back safely for a deep skewed tree");
        checkQuiet(deep.delete(count / 2), "deep BST iterative deletion");
        checkQuiet(deep.size() == count - 1 && deep.isValidBST(), "deep BST valid after deletion");

        BinarySearchTree actual = new BinarySearchTree();
        TreeSet<Integer> reference = new TreeSet<>();
        Random random = new Random(20261025L); // fixed seed makes tests reproducible
        for (int i = 0; i < 2_000; i++) {
            int value = random.nextInt(501) - 250;
            switch (random.nextInt(3)) {
                case 0 -> checkQuiet(actual.insert(value) == reference.add(value),
                        "random insert matches reference");
                case 1 -> checkQuiet(actual.delete(value) == reference.remove(value),
                        "random delete matches reference");
                default -> checkQuiet(actual.search(value) == reference.contains(value),
                        "random search matches reference");
            }
            checkQuiet(actual.size() == reference.size(), "random operation preserves size");
            checkQuiet(actual.isValidBST(), "random operation preserves BST ordering");
            checkQuiet(actual.inorderTraversal().equals(new ArrayList<>(reference)),
                    "random operation matches sorted reference values");

            int first = random.nextInt(601) - 300;
            int second = random.nextInt(601) - 300;
            int low = Math.min(first, second);
            int high = Math.max(first, second);
            List<Integer> expectedRange = new ArrayList<>(reference.subSet(low, true, high, true));
            checkQuiet(actual.rangeSearch(low, high).equals(expectedRange),
                    "random range search matches reference subset");
        }
    }

    private static BinarySearchTree requiredTree() {
        BinarySearchTree tree = new BinarySearchTree();
        for (int value : new int[]{50, 30, 70, 20, 40, 60, 80}) {
            checkQuiet(tree.insert(value), "insert sample value " + value);
        }
        return tree;
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("FAILED: " + message);
        }
        passed++;
        System.out.println("PASS: " + message);
    }

    private static void checkQuiet(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("FAILED: " + message);
        }
        passed++;
    }

    private static void checkThrows(Class<? extends Throwable> expected,
                                    Runnable action, String message) {
        try {
            action.run();
        } catch (RuntimeException actual) {
            if (expected.isInstance(actual)) {
                check(true, message);
                return;
            }
            throw new AssertionError("FAILED: " + message + "; expected "
                    + expected.getSimpleName() + " but got " + actual, actual);
        }
        throw new AssertionError("FAILED: " + message + "; expected exception "
                + expected.getSimpleName());
    }
}
