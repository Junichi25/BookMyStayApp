import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

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

        SingleRoom single = new SingleRoom();
        DoubleRoom doubleRoom = new DoubleRoom();
        SuiteRoom suiteRoom = new SuiteRoom();

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

        inventory.addRoomType("Single Room", 5);
        inventory.addRoomType("Double Room", 3);
        inventory.addRoomType("Suite Room", 2);

        inventory.displayInventory();

        // -----------------------------------------------------------
        //              USE CASE 4: ROOM SEARCH (READ-ONLY)
        // -----------------------------------------------------------

        System.out.println("\n============================================");
        System.out.println("      Use Case 4: Room Search & Lookup      ");
        System.out.println("============================================\n");

        RoomSearchService searchService = new RoomSearchService(inventory);

        HashMap<String, Room> roomModels = new HashMap<>();
        roomModels.put("Single Room", single);
        roomModels.put("Double Room", doubleRoom);
        roomModels.put("Suite Room", suiteRoom);

        searchService.displayAvailableRooms(roomModels);


        // -----------------------------------------------------------
        //                 USE CASE 5: BOOKING REQUEST QUEUE
        // -----------------------------------------------------------

        System.out.println("\n============================================");
        System.out.println("   Use Case 5: Booking Request (FCFS Queue)  ");
        System.out.println("============================================\n");

        BookingRequestQueue requestQueue = new BookingRequestQueue();

        requestQueue.addRequest(new Reservation("Amit", "Single Room"));
        requestQueue.addRequest(new Reservation("John", "Suite Room"));
        requestQueue.addRequest(new Reservation("Priya", "Double Room"));
        requestQueue.addRequest(new Reservation("Rahul", "Single Room"));

        requestQueue.displayQueue();


        // -----------------------------------------------------------
        //          USE CASE 6: ROOM ALLOCATION & CONFIRMATION
        // -----------------------------------------------------------

        System.out.println("\n============================================");
        System.out.println(" Use Case 6: Reservation Confirmation & Room Allocation ");
        System.out.println("============================================\n");

        RoomAllocationService allocationService =
                new RoomAllocationService(inventory);

        List<String> allocatedReservationIDs = allocationService.processAllRequests(requestQueue);

        System.out.println("\nFinal Inventory After Allocation:");
        inventory.displayInventory();


        // -----------------------------------------------------------
        //          USE CASE 7: ADD-ON SERVICE SELECTION
        // -----------------------------------------------------------

        System.out.println("\n============================================");
        System.out.println("      Use Case 7: Add-On Service Selection  ");
        System.out.println("============================================");
        System.out.println("Guests may now choose optional services.\n");

        AddOnServiceManager addOnManager = new AddOnServiceManager();

        // Sample add-on services
        AddOnService breakfast = new AddOnService("Breakfast Buffet", 499);
        AddOnService airportPickup = new AddOnService("Airport Pickup", 999);
        AddOnService spa = new AddOnService("Spa Access", 1499);

        // Attach services to the first two reservations (demo)
        if (allocatedReservationIDs.size() >= 2) {
            addOnManager.addService(allocatedReservationIDs.get(0), breakfast);
            addOnManager.addService(allocatedReservationIDs.get(0), spa);

            addOnManager.addService(allocatedReservationIDs.get(1), airportPickup);
        }

        // Show the report
        addOnManager.printServiceReport();
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

class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRoomType(String roomType, int availableCount) {
        inventory.put(roomType, availableCount);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decrement(String roomType) {
        int current = inventory.getOrDefault(roomType, 0);
        inventory.put(roomType, Math.max(0, current - 1));
    }

    public void displayInventory() {
        System.out.println("--------------------------------------");
        for (String roomType : inventory.keySet()) {
            System.out.println(roomType + " -> Available: " + inventory.get(roomType));
        }
        System.out.println("--------------------------------------");
    }
}

// ================= USE CASE 4: ROOM SEARCH SERVICE =================

class RoomSearchService {

    private RoomInventory inventoryRef;

    public RoomSearchService(RoomInventory inventoryRef) {
        this.inventoryRef = inventoryRef;
    }

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

// ================= USE CASE 5: BOOKING REQUEST QUEUE =================

class Reservation {
    String guestName;
    String requestedRoomType;

    public Reservation(String guestName, String requestedRoomType) {
        this.guestName = guestName;
        this.requestedRoomType = requestedRoomType;
    }
}

class BookingRequestQueue {

    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    public void addRequest(Reservation r) {
        System.out.println("Request Added -> " + r.guestName + " (" + r.requestedRoomType + ")");
        requestQueue.add(r);
    }

    public Reservation getNext() {
        return requestQueue.poll(); // FIFO
    }

    public boolean isEmpty() {
        return requestQueue.isEmpty();
    }

    public void displayQueue() {
        System.out.println("\nCurrent Booking Requests (FIFO):");
        System.out.println("--------------------------------------");

        if (requestQueue.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }

        for (Reservation r : requestQueue) {
            System.out.println("Guest: " + r.guestName + " | Requested: " + r.requestedRoomType);
        }
    }
}

// ================= USE CASE 6: ROOM ALLOCATION SERVICE =================

class RoomAllocationService {

    private RoomInventory inventoryRef;

    private Set<String> allocatedRoomIDs = new HashSet<>();
    private HashMap<String, Set<String>> allocationMap = new HashMap<>();

    public RoomAllocationService(RoomInventory inventoryRef) {
        this.inventoryRef = inventoryRef;
    }

    public List<String> processAllRequests(BookingRequestQueue queue) {

        List<String> reservationIDs = new ArrayList<>();

        System.out.println("Processing Booking Requests...\n");

        while (!queue.isEmpty()) {

            Reservation r = queue.getNext();
            System.out.println("Processing -> " + r.guestName + " (" + r.requestedRoomType + ")");

            if (inventoryRef.getAvailability(r.requestedRoomType) == 0) {
                System.out.println("No rooms available for " + r.requestedRoomType);
                continue;
            }

            String roomID = generateUniqueRoomID(r.requestedRoomType);

            allocatedRoomIDs.add(roomID);

            allocationMap.putIfAbsent(r.requestedRoomType, new HashSet<>());
            allocationMap.get(r.requestedRoomType).add(roomID);

            inventoryRef.decrement(r.requestedRoomType);

            System.out.println("✔ Reservation Confirmed!");
            System.out.println("Assigned Room ID: " + roomID + "\n");

            reservationIDs.add(roomID);
        }

        printAllocationSummary();
        return reservationIDs;
    }

    private String generateUniqueRoomID(String roomType) {
        String id;
        do {
            int number = (int)(Math.random() * 900) + 100;
            id = roomType.substring(0, 1).toUpperCase() + "-" + number;
        } while (allocatedRoomIDs.contains(id));
        return id;
    }

    private void printAllocationSummary() {
        System.out.println("\n========= Allocation Summary =========");

        for (String roomType : allocationMap.keySet()) {
            System.out.println(roomType + " -> " + allocationMap.get(roomType));
        }

        System.out.println("======================================\n");
    }
}

// ================= USE CASE 7: ADD-ON SERVICES =================

class AddOnService {
    String serviceName;
    double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }
}

class AddOnServiceManager {

    private HashMap<String, List<AddOnService>> serviceMap = new HashMap<>();

    public void addService(String reservationID, AddOnService service) {
        serviceMap.putIfAbsent(reservationID, new ArrayList<>());
        serviceMap.get(reservationID).add(service);

        System.out.println("Added service '" + service.serviceName +
                "' to Reservation " + reservationID);
    }

    public void printServiceReport() {

        System.out.println("\n============ Add-On Services Report ============");

        if (serviceMap.isEmpty()) {
            System.out.println("No add-on services selected.");
            return;
        }

        for (String reservationID : serviceMap.keySet()) {
            System.out.println("\nReservation ID: " + reservationID);
            double total = 0;

            for (AddOnService s : serviceMap.get(reservationID)) {
                System.out.println(" - " + s.serviceName + " : ₹" + s.cost);
                total += s.cost;
            }

            System.out.println("Total Add-On Cost: ₹" + total);
        }

        System.out.println("=================================================\n");
    }
}