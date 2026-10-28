# Data Structures Assignment 2

**Selected structures:** Category A - Linear data structure (Circular Queue); Category B - Non-linear / Advanced data structure (Binary Search Tree, BST).

This individual Java console project implements both structures from scratch. It includes a guided demonstration, a menu for each category, a Circular Queue physical-state visualizer, BST traversals and tree-shape display, inclusive range search, and a dependency-free regression test suite.

## Requirements

- JDK 17 or newer
- No third-party libraries or build tools
- The Circular Queue is implemented with a regular integer array and modulo indexing
- The BST is implemented with a custom `Node` class
- `TreeSet` is used only as an independent reference in tests; it does not implement the submitted BST

## Project structure

```text
Assignment2_Final_Project/
├── src/
│   └── com/
│       └── example/
│           └── assignment2/
│               ├── Main.java
│               ├── CircularQueue.java
│               └── BinarySearchTree.java
├── test/
│   └── com/
│       └── example/
│           └── assignment2/
│               └── Assignment2Tests.java
├── run.bat
├── run.sh
├── test.bat
├── test.sh
├── README.md
├── HOW_TO_RUN.txt
├── DEMONSTRATION-OUTPUT.txt
├── TEST-RESULT.txt
├── Assignment2_CircularQueue_BST_Report.docx
├── Assignment2_CircularQueue_BST_Report.pdf
├── Final_Presentation.pptx
├── Final_Presentation.pdf
└── .gitignore
```

The application and test code use the Java package `com.example.assignment2`. The test suite is kept separate from the application source.

## Main menu

- **Category A - Linear data structure: Circular Queue:** enqueue, dequeue, peek, logical FIFO status, physical array/pointer view, clear, and capacity change (only while empty).
- **Category B - Non-linear / Advanced data structure: Binary Search Tree (BST):** insert, search, delete, traversals, min/max, tree display, range search, ordering validation, and clear.
- **Guided demonstrations:** runs the required Circular Queue and BST examples.
- **Exit:** closes the application.

## Circular Queue

- Fixed capacity; the interactive menu starts with capacity 5.
- `enqueue`, `dequeue`, `peek`, `isEmpty`, `isFull`, `size`, `capacity`, and `clear`.
- `display()` returns logical FIFO order.
- `physicalState()` shows array slots and `front`/`rear` positions, hides vacated cells, and distinguishes physical positions from logical order.
- Guided example: enqueue `10, 20, 30`; dequeue `10`; enqueue `40, 50, 60`; verify `[20, 30, 40, 50, 60]`; drain in FIFO order.
- Queue state persists when leaving and re-entering its submenu during a single run.

## Binary Search Tree

- Unique-value `insert`, `search`, `delete`, `findMin`, `findMax`, `clear`, size, empty-state check, height, and ordering validation.
- Iterative inorder, preorder, and postorder traversals.
- `rangeSearch(low, high)` returns inclusive matches in sorted order and rejects `low > high`.
- `displayStructure()` renders a labelled ASCII tree for ordinary-sized trees and uses parent/child links for tall trees.
- Guided example inserts `50, 30, 70, 20, 40, 60, 80` and demonstrates successful/unsuccessful searches, duplicate rejection, range search, leaf/one-child/two-child deletion, and empty-tree behaviour.
- The tree is not self-balancing; it persists when leaving and re-entering the submenu during a single run.

## Complexity summary

| Circular Queue operation | Time |
|---|---:|
| Enqueue, dequeue, peek, status checks, size, clear | O(1) |
| Logical display | O(n) |
| Physical state visualizer | O(capacity) |
| Backing array space | O(capacity) |

| BST operation | Time |
|---|---:|
| Insert, search, delete, minimum, maximum | O(h) |
| Inorder, preorder, postorder | O(n) |
| Inclusive range search | O(h + k) with pruning; worst-case O(n) |
| Height, invariant validation, structure display | O(n) |
| Tree storage | O(n) |

Here, `h` is the tree height and `k` is the number of returned range-search values. In a sorted-insertion worst case, this unbalanced BST can have height O(n), so insert/search/delete can take O(n). Core operations are iterative to avoid call-stack exhaustion on a skewed tree; `height()` uses O(n) auxiliary space for breadth-first traversal.

## Run and test

- **Windows:** double-click `run.bat` to launch the application. Run `test.bat` to compile and execute the tests.
- **macOS/Linux:** run `./run.sh` to launch or `./test.sh` to test. If needed, run `chmod +x run.sh test.sh` first.
- The scripts compile with `javac -encoding UTF-8 --release 17 -Xlint:all -Werror`. Compiled classes are generated in `out/`, which is excluded from source control and the submission ZIP.
- `HOW_TO_RUN.txt` provides step-by-step instructions. `DEMONSTRATION-OUTPUT.txt` records the guided demonstration, and `TEST-RESULT.txt` records the verification run used to prepare this package.

## Academic integrity note

AI assistance was used to help draft/review implementation details and tests. Follow the course policy for disclosure, and review the source so you can explain the algorithms and design decisions independently.

## Report layout

- The final individual-assignment report is 10 pages. The cover page is followed by a dedicated Abstract page (page 2) and Contents page (page 3); the main report begins on page 4.
- Both `Assignment2_CircularQueue_BST_Report.docx` and `Assignment2_CircularQueue_BST_Report.pdf` contain the same updated pagination and table of contents.

## Presentation files

- `Final_Presentation.pptx` is the editable 13-slide presentation.
- `Final_Presentation.pdf` is a PDF copy of the presentation.


Presentation polish: class-diagram labels are slightly larger, the two-child BST deletion explanation names the inorder successor explicitly, and the complexity table defines n, h, k, and capacity. The slide count and technical content are unchanged.
