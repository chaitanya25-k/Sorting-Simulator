import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class SortingBenchmarkDashboard extends JFrame {

    private DefaultTableModel tableModel;
    private JComboBox<Integer> sizeSelector;
    private JButton runButton;
    private JProgressBar progressBar;

    public SortingBenchmarkDashboard() {
        setTitle("Database Indexing & Sorting Simulator");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));

        tabbedPane.addTab("Benchmarking Dashboard", createDashboardPanel());
        tabbedPane.addTab("Live Visualizer", createVisualizerPanel());
        tabbedPane.addTab("How It Works", createHowItWorksPanel());

        add(tabbedPane);
    }

    // --- Page 1: The Main Dashboard ---
    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Data Size: "));
        Integer[] sizes = {1000, 5000, 10000, 25000};
        sizeSelector = new JComboBox<>(sizes);
        topPanel.add(sizeSelector);

        runButton = new JButton("Run Benchmarks");
        runButton.addActionListener(e -> runBenchmarks());
        topPanel.add(runButton);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        topPanel.add(progressBar);

        dashboardPanel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Algorithm", "Distribution", "Comparisons", "Swaps/Moves", "Time (ms)"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable resultsTable = new JTable(tableModel);
        resultsTable.setRowHeight(25);
        resultsTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        resultsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        dashboardPanel.add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        return dashboardPanel;
    }

    // --- Page 2: The Live Visualizer ---
    private JPanel createVisualizerPanel() {
        JPanel visPanel = new JPanel(new BorderLayout());

        VisualizerCanvas canvas = new VisualizerCanvas();
        visPanel.add(canvas, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();

        String[] algorithms = {"Bubble Sort", "Merge Sort", "Quick Sort", "Counting Sort"};
        JComboBox<String> algoSelector = new JComboBox<>(algorithms);
        bottomPanel.add(new JLabel("Algorithm: "));
        bottomPanel.add(algoSelector);

        JButton startBtn = new JButton("Start Visualization");
        startBtn.addActionListener(e -> {
            startBtn.setEnabled(false);
            algoSelector.setEnabled(false);
            String selectedAlgo = (String) algoSelector.getSelectedItem();
            canvas.startAnimation(selectedAlgo, () -> {
                startBtn.setEnabled(true);
                algoSelector.setEnabled(true);
            });
        });
        bottomPanel.add(startBtn);
        visPanel.add(bottomPanel, BorderLayout.SOUTH);

        return visPanel;
    }

    // Custom Canvas to draw the moving bars
    class VisualizerCanvas extends JPanel {
        private int[] array;
        private int currentIndex = -1;
        private int compareIndex = -1;
        private final int DELAY = 25;

        public VisualizerCanvas() {
            setBackground(Color.BLACK);
            resetArray();
        }

        private void resetArray() {
            array = new int[60];
            Random rand = new Random();
            for (int i = 0; i < array.length; i++) {
                array[i] = rand.nextInt(400) + 50;
            }
            repaint();
        }

        public void startAnimation(String algorithm, Runnable onComplete) {
            resetArray();
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    switch (algorithm) {
                        case "Bubble Sort": visBubbleSort(); break;
                        case "Merge Sort": visMergeSort(0, array.length - 1); break;
                        case "Quick Sort": visQuickSort(0, array.length - 1); break;
                        case "Counting Sort": visCountingSort(); break;
                    }
                    currentIndex = -1;
                    compareIndex = -1;
                    repaint();
                    return null;
                }

                @Override
                protected void done() {
                    onComplete.run();
                }
            };
            worker.execute();
        }

        private void drawAndWait() throws InterruptedException {
            repaint();
            Thread.sleep(DELAY);
        }

        // --- Visualizer Algorithms ---

        private void visBubbleSort() throws InterruptedException {
            int n = array.length;
            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n - i - 1; j++) {
                    currentIndex = j; compareIndex = j + 1;
                    if (array[j] > array[j + 1]) {
                        int temp = array[j]; array[j] = array[j + 1]; array[j + 1] = temp;
                    }
                    drawAndWait();
                }
            }
        }

        private void visMergeSort(int left, int right) throws InterruptedException {
            if (left < right) {
                int mid = left + (right - left) / 2;
                visMergeSort(left, mid);
                visMergeSort(mid + 1, right);

                // Merge process visualization
                int[] temp = new int[right - left + 1];
                int i = left, j = mid + 1, k = 0;

                while (i <= mid && j <= right) {
                    currentIndex = i; compareIndex = j; drawAndWait();
                    if (array[i] <= array[j]) temp[k++] = array[i++];
                    else temp[k++] = array[j++];
                }
                while (i <= mid) temp[k++] = array[i++];
                while (j <= right) temp[k++] = array[j++];

                for (i = left; i <= right; i++) {
                    currentIndex = i; compareIndex = -1;
                    array[i] = temp[i - left];
                    drawAndWait();
                }
            }
        }

        private void visQuickSort(int low, int high) throws InterruptedException {
            if (low < high) {
                int pivot = array[high];
                int i = (low - 1);
                for (int j = low; j < high; j++) {
                    currentIndex = j; compareIndex = high; drawAndWait();
                    if (array[j] <= pivot) {
                        i++;
                        int temp = array[i]; array[i] = array[j]; array[j] = temp;
                        drawAndWait();
                    }
                }
                int temp = array[i + 1]; array[i + 1] = array[high]; array[high] = temp;
                currentIndex = i + 1; compareIndex = high; drawAndWait();

                int pi = i + 1;
                visQuickSort(low, pi - 1);
                visQuickSort(pi + 1, high);
            }
        }

        private void visCountingSort() throws InterruptedException {
            int max = Arrays.stream(array).max().orElse(0);
            int[] count = new int[max + 1];
            int[] output = new int[array.length];

            // Visualize Scanning/Counting Phase
            for (int i = 0; i < array.length; i++) {
                currentIndex = i; compareIndex = -1; drawAndWait();
                count[array[i]]++;
            }

            for (int i = 1; i <= max; i++) count[i] += count[i - 1];

            for (int i = array.length - 1; i >= 0; i--) {
                output[count[array[i]] - 1] = array[i];
                count[array[i]]--;
            }

            // Visualize Reconstruction Phase
            for (int i = 0; i < array.length; i++) {
                currentIndex = i; compareIndex = -1;
                array[i] = output[i];
                drawAndWait();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (array == null) return;

            int width = getWidth();
            int height = getHeight();
            int barWidth = width / array.length;

            for (int i = 0; i < array.length; i++) {
                if (i == currentIndex || i == compareIndex) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.CYAN);
                }

                int barHeight = array[i];
                int x = i * barWidth;
                int y = height - barHeight;

                g.fillRect(x, y, barWidth - 2, barHeight);
            }
        }
    }

    // --- Page 3: The 'How it Works' Information Page ---
    private JPanel createHowItWorksPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);

        String infoText = "<html><div style='padding: 20px; font-family: sans-serif; font-size: 14px;'>"
                + "<h1 style='color: #2c3e50;'>How the Simulator Works</h1>"
                + "<p>This tool simulates how database engines evaluate and choose sorting algorithms by stress-testing them under different data conditions.</p>"
                + "<h2 style='color: #2980b9;'>1. Data Generation (The Scenarios)</h2>"
                + "<ul>"
                + "<li><b>Random:</b> Completely shuffled data. Simulates inserting unorganized records.</li>"
                + "<li><b>Nearly Sorted:</b> Only 5% of elements are out of order. Simulates a well-maintained database index that just had a few recent inserts.</li>"
                + "<li><b>Reverse Sorted:</b> Perfect backward order. Often the worst-case scenario for comparison algorithms.</li>"
                + "</ul>"
                + "<h2 style='color: #2980b9;'>2. The Algorithms</h2>"
                + "<ul>"
                + "<li><b>Bubble Sort:</b> A slow, nested-loop approach. Used here as a baseline for poor performance.</li>"
                + "<li><b>Merge Sort:</b> Divide and conquer. Predictable performance but requires extra memory.</li>"
                + "<li><b>Quick Sort:</b> Pivot-based. Highly efficient for random data.</li>"
                + "<li><b>Counting Sort:</b> A non-comparison algorithm. Blazing fast for limited ranges.</li>"
                + "</ul>"
                + "<h2 style='color: #2980b9;'>3. What We Measure</h2>"
                + "<ul>"
                + "<li><b>Comparisons:</b> How many times the algorithm asked, 'Is A greater than B?'.</li>"
                + "<li><b>Swaps/Moves:</b> How many times data was physically moved in memory.</li>"
                + "<li><b>Time (ms):</b> Measured down to fractions of a millisecond.</li>"
                + "</ul>"
                + "</div></html>";

        JLabel infoLabel = new JLabel(infoText);
        infoLabel.setVerticalAlignment(SwingConstants.TOP);
        infoPanel.add(new JScrollPane(infoLabel), BorderLayout.CENTER);

        return infoPanel;
    }

    // --- Benchmark Execution ---
    private void runBenchmarks() {
        int size = (int) sizeSelector.getSelectedItem();
        tableModel.setRowCount(0);
        runButton.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        SwingWorker<Void, Object[]> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String[] distributions = {"Random", "Nearly Sorted", "Reverse Sorted"};
                String[] algorithms = {"Bubble Sort", "Merge Sort", "Quick Sort", "Counting Sort"};

                for (String dist : distributions) {
                    int[] baseArray = generateData(size, dist);

                    for (String algo : algorithms) {
                        int[] arrCopy = Arrays.copyOf(baseArray, baseArray.length);
                        Result res = runSort(algo, arrCopy);
                        publish(new Object[]{algo, dist, res.comparisons, res.swaps, res.timeMs});
                    }
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Object[]> chunks) {
                for (Object[] row : chunks) {
                    tableModel.addRow(row);
                }
            }

            @Override
            protected void done() {
                runButton.setEnabled(true);
                progressBar.setVisible(false);
            }
        };
        worker.execute();
    }

    // --- Data Generation & Sorting Logic ---
    private int[] generateData(int size, String type) {
        int[] arr = new int[size];
        Random rand = new Random();
        for (int i = 0; i < size; i++) arr[i] = i + 1;

        switch (type) {
            case "Random":
                for (int i = 0; i < size; i++) {
                    int swapIdx = rand.nextInt(size);
                    int temp = arr[i]; arr[i] = arr[swapIdx]; arr[swapIdx] = temp;
                }
                break;
            case "Nearly Sorted":
                int swaps = size / 20;
                for (int i = 0; i < swaps; i++) {
                    int idx1 = rand.nextInt(size);
                    int idx2 = rand.nextInt(size);
                    int temp = arr[idx1]; arr[idx1] = arr[idx2]; arr[idx2] = temp;
                }
                break;
            case "Reverse Sorted":
                for (int i = 0; i < size; i++) arr[i] = size - i;
                break;
        }
        return arr;
    }

    private Result runSort(String algo, int[] arr) {
        Result res = new Result();
        long startTime = System.nanoTime();
        switch (algo) {
            case "Bubble Sort": bubbleSort(arr, res); break;
            case "Merge Sort": mergeSort(arr, 0, arr.length - 1, res); break;
            case "Quick Sort": quickSort(arr, 0, arr.length - 1, res); break;
            case "Counting Sort": countingSort(arr, res); break;
        }
        res.timeMs = (System.nanoTime() - startTime) / 1_000_000.0;
        return res;
    }

    private void bubbleSort(int[] arr, Result res) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                res.comparisons++;
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j]; arr[j] = arr[j + 1]; arr[j + 1] = temp;
                    res.swaps++;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }

    private void mergeSort(int[] arr, int left, int right, Result res) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, left, mid, res);
            mergeSort(arr, mid + 1, right, res);
            merge(arr, left, mid, right, res);
        }
    }

    private void merge(int[] arr, int left, int mid, int right, Result res) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;
        while (i <= mid && j <= right) {
            res.comparisons++;
            if (arr[i] <= arr[j]) temp[k++] = arr[i++];
            else temp[k++] = arr[j++];
            res.swaps++;
        }
        while (i <= mid) { temp[k++] = arr[i++]; res.swaps++; }
        while (j <= right) { temp[k++] = arr[j++]; res.swaps++; }
        for (i = left; i <= right; i++) arr[i] = temp[i - left];
    }

    private void quickSort(int[] arr, int low, int high, Result res) {
        if (low < high) {
            int pi = partition(arr, low, high, res);
            quickSort(arr, low, pi - 1, res);
            quickSort(arr, pi + 1, high, res);
        }
    }

    private int partition(int[] arr, int low, int high, Result res) {
        int pivot = arr[high];
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            res.comparisons++;
            if (arr[j] <= pivot) {
                i++;
                int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
                res.swaps++;
            }
        }
        int temp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = temp;
        res.swaps++;
        return i + 1;
    }

    private void countingSort(int[] arr, Result res) {
        int max = Arrays.stream(arr).max().orElse(0);
        int[] count = new int[max + 1];
        int[] output = new int[arr.length];
        for (int num : arr) { count[num]++; res.swaps++; }
        for (int i = 1; i <= max; i++) count[i] += count[i - 1];
        for (int i = arr.length - 1; i >= 0; i--) {
            output[count[arr[i]] - 1] = arr[i];
            count[arr[i]]--; res.swaps++;
        }
        System.arraycopy(output, 0, arr, 0, arr.length);
    }

    static class Result {
        long comparisons = 0;
        long swaps = 0;
        double timeMs = 0;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new SortingBenchmarkDashboard().setVisible(true));
    }
}
