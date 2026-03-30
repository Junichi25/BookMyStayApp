import java.util.HashMap;

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("============================================");
        System.out.println("      Welcome to the Book My Stay System    ");
        System.out.println("============================================");
        System.out.println("Hotel Booking Management System - Version 1.0");
        System.out.println("System initialized successfully.");
        System.out.println("Application ready for operations...\n");

        // ---------- USE CASE 2: BASIC ROOM INITIALIZATION ----------
        System.out.println("Use Case 2: Basic Room Types & Static Availability\n");

        // Create room objects
        SingleRoom single = new SingleRoom();
        DoubleRoom doubleRoom = new DoubleRoom();
        SuiteRoom suiteRoom = new SuiteRoom();

        // Static availability values
        int singleRoomAvailable = 5;
        int doubleRoomAvailable = 3;
        int suiteRoomAvailable = 2;

        System.out.println("Hotel Room Initialization");

        System.out.println("\nSingle Room:");
        single.displayRoomDetails();
        System.out.println("Available: " + singleRoomAvailable);

        System.out.println("\nDouble Room:");
        doubleRoom.displayRoomDetails();
        System.out.println("Available: " + doubleRoomAvailable);

        System.out.println("\nSuite Room:");
        suiteRoom.displayRoomDetails();
        System.out.println("Available: " + suiteRoomAvailable);

        // -----------------------------------------------------------
        //              USE CASE 3: CENTRALIZED INVENTORY
        // -----------------------------------------------------------

        System.out.println("\n============================================");
        System.out.println("     Use Case 3: Centralized Inventory      ");
        System.out.println("============================================\n");

        RoomInventory inventory = new RoomInventory();

        // Register room inventory
        inventory.addRoomType("Single Room", 5);
        inventory.addRoomType("Double Room", 3);
        inventory.addRoomType("Suite Room", 2);

        // Display inventory
        inventory.displayInventory();

        // -----------------------------------------------------------
        //              USE CASE 4: ROOM SEARCH (READ-ONLY)
        // -----------------------------------------------------------

        System.out.println("\n============================================");
        System.out.println("      Use Case 4: Room Search & Lookup      ");
        System.out.println("============================================\n");

        RoomSearchService searchService = new RoomSearchService(inventory);

        // Map room objects to names
        HashMap<String, Room> roomModels = new HashMap<>();
        roomModels.put("Single Room", single);
        roomModels.put("Double Room", doubleRoom);
        roomModels.put("Suite Room", suiteRoom);

        // Perform search
        searchService.displayAvailableRooms(roomModels);
    }
}

// ================= ROOM CLASSES BELOW =================

abstract class Room {
    protected int numberOfBeds;
    protected int squareFeet;
    protected double pricePerNight;

    public Room(int numberOfBeds, int squareFeet, double pricePerNight) {
        this.numberOfBeds = numberOfBeds;
        this.squareFeet = squareFeet;
        this.pricePerNight = pricePerNight;
    }

    public void displayRoomDetails() {
        System.out.println("Beds: " + numberOfBeds);
        System.out.println("Size: " + squareFeet + " sqft");
        System.out.println("Price per night: ₹" + pricePerNight);
    }
}

class SingleRoom extends Room {
    public SingleRoom() {
        super(1, 250, 1500.0);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super(2, 400, 2500.0);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super(3, 750, 5000.0);
    }
}

// ================= INVENTORY CLASS (USE CASE 3) =================

/**
 * Version 3.0 - Centralized inventory using HashMap
 */
class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    // Add or update availability
    public void addRoomType(String roomType, int availableCount) {
        inventory.put(roomType, availableCount);
    }

    // Retrieve availability
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    // Display current inventory state
    public void displayInventory() {
        System.out.println("Centralized Inventory (HashMap Based):");
        System.out.println("--------------------------------------");

        for (String roomType : inventory.keySet()) {
            System.out.println(roomType + " -> Available: " + inventory.get(roomType));
        }
    }
}

// ================= USE CASE 4: ROOM SEARCH SERVICE =================

/**
 * Version 4.0 - Read-only room lookup
 */
class RoomSearchService {

    private RoomInventory inventoryRef;

    public RoomSearchService(RoomInventory inventoryRef) {
        this.inventoryRef = inventoryRef;
    }

    /**
     * Displays only available rooms.
     * Does NOT modify inventory. (READ-ONLY OPERATION)
     */
    public void displayAvailableRooms(HashMap<String, Room> roomModels) {

        System.out.println("Searching available rooms...");
        System.out.println("--------------------------------------");

        for (String roomType : roomModels.keySet()) {

            int available = inventoryRef.getAvailability(roomType);

            if (available > 0) {
                System.out.println("\nRoom Type: " + roomType);
                roomModels.get(roomType).displayRoomDetails();
                System.out.println("Available: " + available);
            }
        }

        System.out.println("\nSearch completed. No inventory changed.\n");
    }
}