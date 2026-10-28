package com.example.assignment2;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An unbalanced binary search tree of unique integer values.
 * Duplicate insertions are ignored. Core operations are iterative to avoid
 * Java call-stack exhaustion on a highly skewed tree; the tree is not balanced.
 */
public final class BinarySearchTree {
    private static final int ASCII_DISPLAY_MAX_HEIGHT = 8;

    private Node root;
    private int size;

    private static final class Node {
        private int value;
        private Node left;
        private Node right;

        private Node(int value) {
            this.value = value;
        }
    }

    /** Inserts a unique value; returns false without changing the tree if duplicated. O(h). */
    public boolean insert(int value) {
        if (root == null) {
            root = new Node(value);
            size++;
            return true;
        }

        Node current = root;
        while (true) {
            if (value == current.value) {
                return false;
            }
            if (value < current.value) {
                if (current.left == null) {
                    current.left = new Node(value);
                    size++;
                    return true;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node(value);
                    size++;
                    return true;
                }
                current = current.right;
            }
        }
    }

    /** Returns true if value exists in the tree; otherwise false. O(h). */
    public boolean search(int value) {
        Node current = root;
        while (current != null) {
            if (value == current.value) {
                return true;
            }
            current = value < current.value ? current.left : current.right;
        }
        return false;
    }

    /** Returns the smallest value; throws NoSuchElementException for an empty tree. O(h). */
    public int findMin() {
        ensureNotEmpty();
        Node current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    /** Returns the largest value; throws NoSuchElementException for an empty tree. O(h). */
    public int findMax() {
        ensureNotEmpty();
        Node current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }

    /**
     * Returns values in the inclusive range [low, high] in ascending order.
     * Throws IllegalArgumentException if low > high. Uses BST ordering to
     * skip subtrees that cannot contain a result. O(h + k), where k is result size,
     * for the pruned traversal; worst-case O(n) in a skewed tree.
     */
    public List<Integer> rangeSearch(int low, int high) {
        if (low > high) {
            throw new IllegalArgumentException("Lower bound must not exceed upper bound.");
        }

        List<Integer> result = new ArrayList<>();
        List<Node> stack = new ArrayList<>();
        Node current = root;
        while (current != null || !stack.isEmpty()) {
            // If current is below low, its entire left subtree is also too small.
            while (current != null) {
                if (current.value >= low) {
                    stack.add(current);
                    current = current.left;
                } else {
                    current = current.right;
                }
            }
            if (stack.isEmpty()) {
                break;
            }

            current = stack.remove(stack.size() - 1);
            if (current.value > high) {
                break; // Inorder order is ascending; all later values are too large.
            }
            result.add(current.value);
            current = current.right;
        }
        return result;
    }

    /** Deletes value and reports whether it existed. Two-child deletion uses the inorder successor. O(h). */
    public boolean delete(int value) {
        Node parent = null;
        Node current = root;

        while (current != null && current.value != value) {
            parent = current;
            current = value < current.value ? current.left : current.right;
        }
        if (current == null) {
            return false;
        }

        if (current.left != null && current.right != null) {
            Node successorParent = current;
            Node successor = current.right;
            while (successor.left != null) {
                successorParent = successor;
                successor = successor.left;
            }
            current.value = successor.value;
            parent = successorParent;
            current = successor;
        }

        // The node to physically unlink now has at most one child.
        Node replacement = current.left != null ? current.left : current.right;
        if (parent == null) {
            root = replacement;
        } else if (parent.left == current) {
            parent.left = replacement;
        } else {
            parent.right = replacement;
        }
        size--;
        return true;
    }

    /** Inorder traversal (left, node, right); a valid BST yields ascending values. O(n). */
    public List<Integer> inorderTraversal() {
        List<Integer> result = new ArrayList<>(size);
        List<Node> stack = new ArrayList<>();
        Node current = root;
        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.add(current);
                current = current.left;
            }
            current = stack.remove(stack.size() - 1);
            result.add(current.value);
            current = current.right;
        }
        return result;
    }

    /** Preorder traversal (node, left, right). O(n). */
    public List<Integer> preorderTraversal() {
        List<Integer> result = new ArrayList<>(size);
        if (root == null) {
            return result;
        }
        List<Node> stack = new ArrayList<>();
        stack.add(root);
        while (!stack.isEmpty()) {
            Node current = stack.remove(stack.size() - 1);
            result.add(current.value);
            if (current.right != null) {
                stack.add(current.right);
            }
            if (current.left != null) {
                stack.add(current.left);
            }
        }
        return result;
    }

    /** Postorder traversal (left, right, node). O(n). */
    public List<Integer> postorderTraversal() {
        List<Integer> result = new ArrayList<>(size);
        List<Node> stack = new ArrayList<>();
        Node current = root;
        Node lastVisited = null;
        while (current != null || !stack.isEmpty()) {
            if (current != null) {
                stack.add(current);
                current = current.left;
            } else {
                Node top = stack.get(stack.size() - 1);
                if (top.right != null && lastVisited != top.right) {
                    current = top.right;
                } else {
                    result.add(top.value);
                    lastVisited = stack.remove(stack.size() - 1);
                }
            }
        }
        return result;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int size() {
        return size;
    }

    /** Height in edges: empty tree = -1, leaf = 0. O(n) time and O(n) auxiliary space. */
    public int height() {
        if (root == null) {
            return -1;
        }
        List<Node> queue = new ArrayList<>();
        queue.add(root);
        int next = 0;
        int height = -1;
        while (next < queue.size()) {
            int levelEnd = queue.size();
            while (next < levelEnd) {
                Node node = queue.get(next++);
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
            }
            height++;
        }
        return height;
    }

    /** Validates strict BST ordering, including Integer.MIN_VALUE/MAX_VALUE. O(n). */
    public boolean isValidBST() {
        if (root == null) {
            return true;
        }
        List<BoundedNode> stack = new ArrayList<>();
        stack.add(new BoundedNode(root, Long.MIN_VALUE, Long.MAX_VALUE));
        while (!stack.isEmpty()) {
            BoundedNode item = stack.remove(stack.size() - 1);
            long value = item.node.value;
            if (value <= item.minExclusive || value >= item.maxExclusive) {
                return false;
            }
            if (item.node.left != null) {
                stack.add(new BoundedNode(item.node.left, item.minExclusive, value));
            }
            if (item.node.right != null) {
                stack.add(new BoundedNode(item.node.right, value, item.maxExclusive));
            }
        }
        return true;
    }

    /** Drops the root reference and resets size in O(1). */
    public void clear() {
        root = null;
        size = 0;
    }

    /** Returns the inorder values for concise status output. O(n). */
    public String display() {
        return inorderTraversal().toString();
    }

    /**
     * Renders the tree shape for small trees. For tall trees, prints compact
     * parent/child links instead of an enormous indented diagram. Iterative in
     * both cases, so the visualizer itself does not risk recursive stack overflow.
     */
    public String displayStructure() {
        if (root == null) {
            return "(empty tree)";
        }
        if (height() > ASCII_DISPLAY_MAX_HEIGHT) {
            return displayCompactStructure();
        }

        StringBuilder result = new StringBuilder();
        List<DisplayEntry> stack = new ArrayList<>();
        stack.add(new DisplayEntry(root, "", true, true, ""));
        while (!stack.isEmpty()) {
            DisplayEntry entry = stack.remove(stack.size() - 1);
            if (entry.rootNode) {
                result.append(entry.node.value).append('\n');
            } else {
                result.append(entry.prefix)
                        .append(entry.lastSibling ? "`-- " : "|-- ")
                        .append(entry.childSide).append(entry.node.value).append('\n');
            }

            String childPrefix = entry.rootNode
                    ? ""
                    : entry.prefix + (entry.lastSibling ? "    " : "|   ");
            if (entry.node.right != null) {
                stack.add(new DisplayEntry(entry.node.right, childPrefix, false, true, "R: "));
            }
            if (entry.node.left != null) {
                stack.add(new DisplayEntry(entry.node.left, childPrefix, false,
                        entry.node.right == null, "L: "));
            }
        }
        return result.toString().stripTrailing();
    }

    private String displayCompactStructure() {
        StringBuilder result = new StringBuilder("Tree is tall; showing parent/child links:\n");
        List<Node> queue = new ArrayList<>();
        queue.add(root);
        int next = 0;
        while (next < queue.size()) {
            Node node = queue.get(next++);
            result.append(node.value).append(" -> left: ")
                    .append(node.left == null ? "-" : Integer.toString(node.left.value))
                    .append(", right: ")
                    .append(node.right == null ? "-" : Integer.toString(node.right.value))
                    .append('\n');
            if (node.left != null) {
                queue.add(node.left);
            }
            if (node.right != null) {
                queue.add(node.right);
            }
        }
        return result.toString().stripTrailing();
    }

    private void ensureNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("Tree is empty.");
        }
    }

    @Override
    public String toString() {
        return display();
    }

    private static final class BoundedNode {
        private final Node node;
        private final long minExclusive;
        private final long maxExclusive;

        private BoundedNode(Node node, long minExclusive, long maxExclusive) {
            this.node = node;
            this.minExclusive = minExclusive;
            this.maxExclusive = maxExclusive;
        }
    }

    private static final class DisplayEntry {
        private final Node node;
        private final String prefix;
        private final boolean rootNode;
        private final boolean lastSibling;
        private final String childSide;

        private DisplayEntry(Node node, String prefix, boolean rootNode,
                            boolean lastSibling, String childSide) {
            this.node = node;
            this.prefix = prefix;
            this.rootNode = rootNode;
            this.lastSibling = lastSibling;
            this.childSide = childSide;
        }
    }
}
