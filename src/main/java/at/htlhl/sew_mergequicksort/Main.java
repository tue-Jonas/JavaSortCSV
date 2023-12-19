package at.htlhl.sew_mergequicksort;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the path of the CSV file:");
        String csvFilePath = scanner.nextLine();

        try {
            displayColumnHeaders(csvFilePath);
        } catch (IOException e) {
            System.err.println("Error reading file headers: " + e.getMessage());
            return;
        }

        System.out.println("Enter the column index (starting from 0):");
        int columnIndex = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.println("Enter the name of the output file:");
        String outputFileName = scanner.nextLine();

        System.out.println("Select sorting algorithm:\n1. Mergesort\n2. Quicksort");
        String algorithmChoice = scanner.nextLine();

        try {
            List<String> data = readColumnFromCSV(csvFilePath, columnIndex);
            sortData(data, algorithmChoice);
            writeSortedDataToFile(data, outputFileName);
            System.out.println("Data sorted and saved to " + outputFileName);
        } catch (IOException e) {
            System.err.println("File error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid input: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void displayColumnHeaders(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String firstLine = reader.readLine();
            if (firstLine != null) {
                String[] headers = firstLine.split(",");
                System.out.println("Available columns:");
                for (int i = 0; i < headers.length; i++) {
                    System.out.println(i + ": " + headers[i]);
                }
            }
        }
    }

    private static List<String> readColumnFromCSV(String fileName, int columnIndex) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine(); // Skip headers
            List<String> columnData = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length > columnIndex) {
                    columnData.add(cleanData(fields[columnIndex]));
                }
            }
            return columnData;
        }
    }

    private static String cleanData(String data) {
        return data.trim().replace("\"", "").replace("'", "");
    }

    private static void sortData(List<String> data, String algorithmChoice) {
        long startTime = System.currentTimeMillis();
        if ("1".equals(algorithmChoice)) {
            performMergeSort(data);
        } else if ("2".equals(algorithmChoice)) {
            performQuickSort(data, 0, data.size() - 1);
        } else {
            throw new IllegalArgumentException("Incorrect algorithm choice.");
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Sorting time: " + (endTime - startTime) + "ms");
    }

    private static void writeSortedDataToFile(List<String> data, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String record : data) {
                writer.write(record);
                writer.newLine();
            }
        }
    }

    private static void performMergeSort(List<String> list) {
        if (list.size() < 2) return;

        int middle = list.size() / 2;
        List<String> leftHalf = new ArrayList<>(list.subList(0, middle));
        List<String> rightHalf = new ArrayList<>(list.subList(middle, list.size()));

        performMergeSort(leftHalf);
        performMergeSort(rightHalf);
        merge(list, leftHalf, rightHalf);
    }

    private static void merge(List<String> combined, List<String> left, List<String> right) {
        int leftIndex = 0, rightIndex = 0, combinedIndex = 0;

        while (leftIndex < left.size() && rightIndex < right.size()) {
            if (left.get(leftIndex).compareTo(right.get(rightIndex)) <= 0) {
                combined.set(combinedIndex++, left.get(leftIndex++));
            } else {
                combined.set(combinedIndex++, right.get(rightIndex++));
            }
        }

        while (leftIndex < left.size()) {
            combined.set(combinedIndex++, left.get(leftIndex++));
        }
        while (rightIndex < right.size()) {
            combined.set(combinedIndex++, right.get(rightIndex++));
        }
    }

    private static void performQuickSort(List<String> list, int start, int end) {
        if (start < end) {
            int partitionIndex = partition(list, start, end);
            performQuickSort(list, start, partitionIndex - 1);
            performQuickSort(list, partitionIndex + 1, end);
        }
    }

    private static int partition(List<String> list, int start, int end) {
        String pivot = list.get(end);
        int i = start - 1;

        for (int j = start; j < end; j++) {
            if (list.get(j).compareTo(pivot) < 0) {
                i++;
                String temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }

        String temp = list.get(i + 1);
        list.set(i + 1, list.get(end));
        list.set(end, temp);

        return i + 1;
    }

}