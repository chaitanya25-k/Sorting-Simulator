# Database Indexing & Sorting Simulator

A Java Swing application designed to benchmark, visualize, and explain how different sorting algorithms perform under various data conditions. This project simulates the stress-testing database engines use to select optimal indexing strategies based on data size and distribution.



![Benchmarking Dashboard]l(link to Screenshot_2026-10-25_192947.png)
*The Benchmarking Dashboard running 12 different scenarios across varying data distributions.*

![Live Visualizer](link_to_visualizer_screenshot.png)
*The Live Visualizer animating the Quick Sort algorithm in real time.*

## ✨ Features

* **Benchmarking Dashboard:** Runs Bubble Sort, Merge Sort, Quick Sort, and Counting Sort against varying array sizes (1,000 to 25,000 elements).
* **Multiple Data Distributions:** Tests algorithms against Random, Nearly Sorted, and Reverse Sorted data to mimic real-world database scenarios.
* **Granular Metrics:** Tracks exact array comparisons, physical memory swaps/moves, and execution time down to fractional milliseconds using `System.nanoTime()`.
* **Live Visualizer:** An animated, multithreaded graphical canvas that lets you watch the algorithms sort arrays in real time.
* **Multithreading:** Utilizes `SwingWorker` to prevent UI freezing during heavy background computations.

## 🚀 How to Run

### Prerequisites
* Java Development Kit (JDK) 8 or higher installed on your system.

### Running from the Command Line
1. Clone this repository or download the source code.
2. Open your terminal or command prompt and navigate to the folder containing the file.
3. Compile the Java file:
   ```bash
   javac SortingBenchmarkDashboard.java
