@echo off
setlocal
cd /d "%~dp0"
if exist out rmdir /s /q out
mkdir out
javac -encoding UTF-8 --release 17 -Xlint:all -Werror -d out src\com\example\assignment2\CircularQueue.java src\com\example\assignment2\BinarySearchTree.java src\com\example\assignment2\Main.java
if errorlevel 1 (
    echo Compilation failed. Check that JDK 17 or newer is installed.
    exit /b 1
)
java -cp out com.example.assignment2.Main
