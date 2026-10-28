#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
rm -rf out
mkdir -p out
javac -encoding UTF-8 --release 17 -Xlint:all -Werror -d out \
  src/com/example/assignment2/CircularQueue.java \
  src/com/example/assignment2/BinarySearchTree.java \
  src/com/example/assignment2/Main.java
exec java -cp out com.example.assignment2.Main
