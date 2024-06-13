package com.oopfinal;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class LaundryManager {
    private List<LaundryItem> laundryItems;
    private static final String DB_FILE_PATH = Paths
            .get(System.getProperty("user.dir"), "src\\main\\java\\com\\oopfinal\\db\\data.csv").toString();

    public LaundryManager() {
        laundryItems = new ArrayList<>();
        loadFromCSV();
    }

    public void add(LaundryItem item) {
        laundryItems.add(item);
        saveToCSV();
    }

    public void edit(String id) {
        Optional<LaundryItem> optionalItem = laundryItems.stream().filter(item -> item.getId().equals(id)).findFirst();
        if (optionalItem.isPresent()) {
            LaundryItem item = optionalItem.get();
            String originalId = item.getId(); // Store the original UUID
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);

            // Display old values
            System.out.println("Old student ID: " + item.getStudentId());
            System.out.println("Old student name: " + item.getStudentName());
            System.out.println("Old cloth weight: " + item.getClothWeight());
            System.out.println("Old cloth count: " + item.getClothCount());

            // Prompt for new values
            System.out.print("Enter new student ID (press Enter to keep old value): ");
            String newStudentId = scanner.nextLine();
            if (!newStudentId.isEmpty()) {
                item.setStudentId(newStudentId);
            }

            System.out.print("Enter new student name (press Enter to keep old value): ");
            String newStudentName = scanner.nextLine();
            if (!newStudentName.isEmpty()) {
                item.setStudentName(newStudentName);
            }

            System.out.print("Enter new cloth weight (press Enter to keep old value): ");
            String newClothWeightStr = scanner.nextLine();
            if (!newClothWeightStr.isEmpty()) {
                double newClothWeight = Double.parseDouble(newClothWeightStr);
                item.setClothWeight(newClothWeight);
            }

            // Edit cloth types
            for (Cloth cloth : item.getClothTypes()) {
                System.out.printf("Current count of %s: %d\n", cloth.getType(), cloth.getCount());
                System.out.printf("Enter new count for %s (press Enter to keep old value): ", cloth.getType());
                String newCountStr = scanner.nextLine();
                if (!newCountStr.isEmpty()) {
                    int newCount = Integer.parseInt(newCountStr);
                    cloth.setCount(newCount);
                }
            }

            // Update total cloth count
            item.updateClothCount();

            // Restore the original UUID
            item.setId(originalId);
            saveToCSV(); // Save changes to CSV

        } else {
            System.out.println("Item with ID " + id + " not found.");
        }
    }

    public void pickup(String id) {
        Optional<LaundryItem> optionalItem = laundryItems.stream().filter(item -> item.getId().equals(id)).findFirst();
        if (optionalItem.isPresent()) {
            LaundryItem item = optionalItem.get();
            item.setPickedUp(true);
            saveToCSV();
        } else {
            System.out.println("Item with ID " + id + " not found.");
        }
    }

    public void showAll() {
        System.out.printf("%-36s %-15s %-15s %-12s %-12s %-12s\n", "ID", "Student ID", "Student Name", "Cloth Weight",
                "Cloth Count", "Picked Up");
        System.out.println(
                "-------------------------------------------------------------------------------------------------------");
        for (LaundryItem item : laundryItems) {
            System.out.printf("%-36s %-15s %-15s %-12.2f %-12d %-12s\n",
                    item.getId(), item.getStudentId(), item.getStudentName(),
                    item.getClothWeight(), item.getClothCount(), item.isPickedUp() ? "Yes" : "No");
            for (Cloth cloth : item.getClothTypes()) {
                System.out.printf("    %-30s: %d\n", cloth.getType(), cloth.getCount());
            }
        }
    }

    public void searchById(String id) {
        Optional<LaundryItem> optionalItem = laundryItems.stream().filter(item -> item.getId().equals(id)).findFirst();
        if (optionalItem.isPresent()) {
            System.out.println(optionalItem.get());
        } else {
            System.out.println("Item with ID " + id + " not found.");
        }
    }

    private void saveToCSV() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(DB_FILE_PATH))) {
            for (LaundryItem item : laundryItems) {
                List<String> data = new ArrayList<>();
                data.add(item.getId());
                data.add(item.getStudentId());
                data.add(item.getStudentName());
                data.add(String.valueOf(item.getClothWeight()));
                data.add(String.valueOf(item.getClothCount()));
                data.add(String.valueOf(item.isPickedUp()));

                for (Cloth cloth : item.getClothTypes()) {
                    data.add(cloth.getType() + ":" + cloth.getCount());
                }

                writer.writeNext(data.toArray(new String[0]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFromCSV() {
        try (CSVReader reader = new CSVReader(new FileReader(DB_FILE_PATH))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                LaundryItem item = new LaundryItem(
                        nextLine[0], // Using stored UUID
                        nextLine[1],
                        nextLine[2],
                        Double.parseDouble(nextLine[3]),
                        Integer.parseInt(nextLine[4]));
                item.setPickedUp(Boolean.parseBoolean(nextLine[5]));

                for (int i = 6; i < nextLine.length; i++) {
                    String[] clothData = nextLine[i].split(":");
                    String clothType = clothData[0];
                    int clothCount = Integer.parseInt(clothData[1]);

                    switch (clothType) {
                        case "Pants":
                            item.addClothType(new Pants(clothCount));
                            break;
                        case "Shirts":
                            item.addClothType(new Shirts(clothCount));
                            break;
                        case "Shorts":
                            item.addClothType(new Shorts(clothCount));
                            break;
                        // Add other cloth types similarly
                    }
                }

                laundryItems.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LaundryManager manager = new LaundryManager();
        Scanner scanner = new Scanner(System.in);

        try {
            while (true) {
                System.out.println("Laundry Management System");
                System.out.println("======================================");
                System.out.println("Options:");
                System.out.println("add, edit, pickup, showall, exit");
                System.out.print("Enter command: ");
                String command = scanner.nextLine();

                switch (command.toLowerCase()) {
                    case "add":
                        System.out.print("Enter student ID: ");
                        String studentId = scanner.nextLine();
                        System.out.print("Enter student name: ");
                        String studentName = scanner.nextLine();
                        System.out.print("Enter cloth weight: ");
                        double clothWeight = Double.parseDouble(scanner.nextLine());

                        LaundryItem newItem = new LaundryItem(UUID.randomUUID().toString(), studentId, studentName,
                                clothWeight, 0);

                        System.out.println("Enter cloth types and counts (enter '0' to finish):");
                        while (true) {
                            System.out.print("Enter cloth type (1 for Pants, 2 for Shirts, 3 for Shorts): ");
                            int clothType = Integer.parseInt(scanner.nextLine());

                            if (clothType == 0) {
                                break;
                            }

                            System.out.print("Enter count: ");
                            int clothCount = Integer.parseInt(scanner.nextLine());

                            switch (clothType) {
                                case 1:
                                    newItem.addClothType(new Pants(clothCount));
                                    break;
                                case 2:
                                    newItem.addClothType(new Shirts(clothCount));
                                    break;
                                case 3:
                                    newItem.addClothType(new Shorts(clothCount));
                                    break;
                                // Add other cloth types similarly
                            }
                        }

                        // Calculate total cloth count
                        newItem.updateClothCount();

                        System.out.print("Enter total cloth count: ");
                        int clothCount = Integer.parseInt(scanner.nextLine());

                        if (newItem.getClothCount() != clothCount) {
                            System.out.println(
                                    "Warning: Total cloth count does not match the sum of individual cloth types.");
                            System.out.println("Total cloth count: " + newItem.getClothCount());
                            System.out.print("Confirm the cloth count (y/n): ");
                            String confirm = scanner.nextLine();
                            if (!confirm.equalsIgnoreCase("y")) {
                                System.out.println("Add operation cancelled.");
                                break;
                            }
                        }

                        newItem.setClothCount(clothCount);
                        manager.add(newItem);
                        break;
                    case "edit":
                        System.out.print("Enter ID to edit: ");
                        String editId = scanner.nextLine();
                        manager.edit(editId);
                        break;
                    case "pickup":
                        System.out.print("Enter ID to pickup: ");
                        String pickupId = scanner.nextLine();
                        manager.pickup(pickupId);
                        break;
                    case "showall":
                        manager.showAll();
                        break;
                    case "exit":
                        // scanner.close();
                        return;
                    default:
                        System.out.println("Unknown command.");
                }
            }

        } finally {
            scanner.close(); // Close the Scanner object in the finally block
        }
    }
}