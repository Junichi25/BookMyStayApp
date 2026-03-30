import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("============================================");
        System.out.println("      Welcome to the Book My Stay System    ");
        System.out.println("============================================");
        System.out.println("Hotel Booking Management System - Version 1.0");
        System.out.println("System initialized successfully.");
        System.out.println("Application ready for operations...\n");

        // ---------- USE CASE 2 ----------
        System.out.println("Use Case 2: Basic Room Types & Static Availability\n");

        SingleRoom single = new SingleRoom();
        DoubleRoom doubleRoom = new DoubleRoom();
        SuiteRoom suiteRoom = new SuiteRoom();

        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 5);
        inventory.addRoomType("Double Room", 3);
        inventory.addRoomType("Suite Room", 2);

        // ---------- USE CASE 4 ----------
        System.out.println("\n============================================");
        System.out.println("      Use Case 4: Room Search & Lookup      ");
        System.out.println("============================================\n");

        RoomSearchService searchService = new RoomSearchService(inventory);

        HashMap<String, Room> roomModels = new HashMap<>();
        roomModels.put("Single Room", single);
        roomModels.put("Double Room", doubleRoom);
        roomModels.put("Suite Room", suiteRoom);

        searchService.displayAvailableRooms(roomModels);

        // ---------- USE CASE 5 ----------
        System.out.println("\n============================================");
        System.out.println("   Use Case 5: Booking Request (FCFS Queue)  ");
        System.out.println("============================================\n");

        BookingRequestQueue requestQueue = new BookingRequestQueue();

        try {
            requestQueue.addRequest(new Reservation("Amit", "Single Room"));
            requestQueue.addRequest(new Reservation("John", "Suite Room"));
            requestQueue.addRequest(new Reservation("Priya", "Double Room"));
            requestQueue.addRequest(new Reservation("Rahul", "InvalidRoom"));   // INVALID
        }
        catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }

        requestQueue.displayQueue();

        // ---------- USE CASE 6 ----------
        System.out.println("\n============================================");
        System.out.println(" Use Case 6: Reservation Confirmation & Room Allocation ");
        System.out.println("============================================\n");

        BookingHistory bookingHistory = new BookingHistory();
        RoomAllocationService allocationService =
                new RoomAllocationService(inventory, bookingHistory);

        List<String> allocatedReservationIDs = new ArrayList<>();

        try {
            allocatedReservationIDs = allocationService.processAllRequests(requestQueue);
        }
        catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }

        System.out.println("\nFinal Inventory After Allocation:");
        inventory.displayInventory();

        // ---------- USE CASE 7 ----------
        System.out.println("\n============================================");
        System.out.println("      Use Case 7: Add-On Service Selection  ");
        System.out.println("============================================");

        AddOnServiceManager addOnManager = new AddOnServiceManager();

        AddOnService breakfast = new AddOnService("Breakfast Buffet", 499);
        AddOnService airportPickup = new AddOnService("Airport Pickup", 999);

        if (allocatedReservationIDs.size() > 0)
            addOnManager.addService(allocatedReservationIDs.get(0), breakfast);

        if (allocatedReservationIDs.size() > 1)
            addOnManager.addService(allocatedReservationIDs.get(1), airportPickup);

        addOnManager.printServiceReport();

        // ---------- USE CASE 8 ----------
        System.out.println("\n============================================");
        System.out.println("     Use Case 8: Booking History & Reports  ");
        System.out.println("============================================\n");

        BookingReportService reportService = new BookingReportService(bookingHistory);
        reportService.printBookingHistory();
        reportService.printSummaryReport();

        // ---------- USE CASE 10 ----------
        System.out.println("\n============================================");
        System.out.println("  Use Case 10: Booking Cancellation & Rollback ");
        System.out.println("============================================\n");

        BookingCancellationService cancelService =
                new BookingCancellationService(inventory, bookingHistory);

        if (!allocatedReservationIDs.isEmpty()) {
            String cancelID = allocatedReservationIDs.get(0);
            cancelService.cancelBooking(cancelID);
        }

        System.out.println("\nInventory After Cancellation:");
        inventory.displayInventory();

        System.out.println("\nUpdated Booking History:");
        reportService.printBookingHistory();
    }
}


/* ===============================================================
                  USE CASE 9: CUSTOM EXCEPTIONS
   =============================================================== */

class InvalidRoomTypeException extends Exception {
    public InvalidRoomTypeException(String msg) { super(msg); }
}

class InvalidGuestNameException extends Exception {
    public InvalidGuestNameException(String msg) { super(msg); }
}

class InventoryException extends Exception {
    public InventoryException(String msg) { super(msg); }
}


/* ===============================================================
                   USE CASE 9: VALIDATION SERVICE
   =============================================================== */

class ValidationService {

    public static void validateGuest(String guestName) throws InvalidGuestNameException {
        if (guestName == null || guestName.trim().isEmpty())
            throw new InvalidGuestNameException("Guest name cannot be empty.");
    }

    public static void validateRoomType(String roomType, RoomInventory inventory)
            throws InvalidRoomTypeException {

        if (!inventory.exists(roomType))
            throw new InvalidRoomTypeException("Invalid room type: " + roomType);
    }

    public static void validateAvailability(String roomType, RoomInventory inventory)
            throws InventoryException {

        if (inventory.getAvailability(roomType) <= 0)
            throw new InventoryException("No rooms available for: " + roomType);
    }
}


/* ===============================================================
                         ROOM CLASSES
   =============================================================== */

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

class SingleRoom extends Room { public SingleRoom() { super(1, 250, 1500.0);} }
class DoubleRoom extends Room { public DoubleRoom() { super(2, 400, 2500.0);} }
class SuiteRoom extends Room  { public SuiteRoom()  { super(3, 750, 5000.0);} }


/* ===============================================================
                  INVENTORY (updated with UC9)
   =============================================================== */

class RoomInventory {

    private HashMap<String, Integer> inventory = new HashMap<>();

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    public boolean exists(String roomType) {
        return inventory.containsKey(roomType);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public void decrement(String type) throws InventoryException {
        int current = inventory.getOrDefault(type, 0);

        if (current <= 0)
            throw new InventoryException("Inventory cannot go negative for: " + type);

        inventory.put(type, current - 1);
    }

    public void increment(String type) {
        int current = inventory.getOrDefault(type, 0);
        inventory.put(type, current + 1);
    }

    public void displayInventory() {
        for (String t : inventory.keySet()) {
            System.out.println(t + " -> Available: " + inventory.get(t));
        }
    }
}


/* ===============================================================
                   USE CASE 4: ROOM SEARCH
   =============================================================== */

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

        System.out.println("\nSearch completed.\n");
    }
}


/* ===============================================================
                   USE CASE 5: REQUEST QUEUE
   =============================================================== */

class Reservation {

    String guestName;
    String requestedRoomType;
    String assignedRoomID;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.requestedRoomType = roomType;
    }
}

class BookingRequestQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) throws Exception {

        ValidationService.validateGuest(r.guestName);

        System.out.println("Request Added -> " + r.guestName +
                " (" + r.requestedRoomType + ")");

        queue.add(r);
    }

    public Reservation getNext() { return queue.poll(); }

    public boolean isEmpty() { return queue.isEmpty(); }

    public void displayQueue() {
        System.out.println("\nCurrent Booking Requests (FIFO):");
        System.out.println("--------------------------------------");

        if (queue.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }

        for (Reservation r : queue) {
            System.out.println("Guest: " + r.guestName +
                    " | Requested: " + r.requestedRoomType);
        }
    }
}


/* ===============================================================
             USE CASE 6: ROOM ALLOCATION
   =============================================================== */

class RoomAllocationService {

    private RoomInventory inventoryRef;
    private BookingHistory historyRef;

    private Set<String> allocatedRoomIDs = new HashSet<>();

    public RoomAllocationService(RoomInventory inventoryRef,
                                 BookingHistory historyRef) {

        this.inventoryRef = inventoryRef;
        this.historyRef = historyRef;
    }

    public List<String> processAllRequests(BookingRequestQueue queue) throws Exception {

        List<String> reservationIDs = new ArrayList<>();

        System.out.println("Processing Booking Requests...\n");

        while (!queue.isEmpty()) {

            Reservation r = queue.getNext();

            System.out.println("Processing -> " + r.guestName +
                    " (" + r.requestedRoomType + ")");

            ValidationService.validateRoomType(r.requestedRoomType, inventoryRef);
            ValidationService.validateAvailability(r.requestedRoomType, inventoryRef);

            String roomID = generateUniqueRoomID(r.requestedRoomType);

            allocatedRoomIDs.add(roomID);

            inventoryRef.decrement(r.requestedRoomType);

            r.assignedRoomID = roomID;
            historyRef.addBooking(r);

            System.out.println("Reservation Confirmed");
            System.out.println("Assigned Room ID: " + roomID + "\n");

            reservationIDs.add(roomID);
        }

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
}


/* ===============================================================
                USE CASE 7: ADD-ON SERVICES
   =============================================================== */

class AddOnService {
    String name;
    double cost;

    public AddOnService(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }
}

class AddOnServiceManager {

    private HashMap<String, List<AddOnService>> map = new HashMap<>();

    public void addService(String reservationID, AddOnService service) {
        map.putIfAbsent(reservationID, new ArrayList<>());
        map.get(reservationID).add(service);
    }

    public void printServiceReport() {

        System.out.println("\n============ Add-On Services Report ============");

        if (map.isEmpty()) {
            System.out.println("No add-on services selected.");
            return;
        }

        for (String id : map.keySet()) {
            System.out.println("\nReservation ID: " + id);

            double total = 0;
            for (AddOnService s : map.get(id)) {
                System.out.println(" - " + s.name + " : ₹" + s.cost);
                total += s.cost;
            }
            System.out.println("Total Add-On Cost: ₹" + total);
        }

        System.out.println("=================================================\n");
    }
}


/* ===============================================================
                 USE CASE 8: BOOKING HISTORY
   =============================================================== */

class BookingHistory {

    private List<Reservation> bookings = new ArrayList<>();

    public void addBooking(Reservation r) { bookings.add(r); }

    public List<Reservation> getHistory() { return bookings; }

    public Reservation getByRoomID(String id) {
        for (Reservation r : bookings) {
            if (r.assignedRoomID != null && r.assignedRoomID.equals(id))
                return r;
        }
        return null;
    }

    public void removeBooking(Reservation r) {
        bookings.remove(r);
    }
}

class BookingReportService {

    private BookingHistory historyRef;

    public BookingReportService(BookingHistory historyRef) {
        this.historyRef = historyRef;
    }

    public void printBookingHistory() {

        System.out.println("============ Booking History =============");

        if (historyRef.getHistory().isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Reservation r : historyRef.getHistory()) {
            System.out.println("Guest: " + r.guestName +
                    " | Room Type: " + r.requestedRoomType +
                    " | Room ID: " + r.assignedRoomID);
        }

        System.out.println("==========================================\n");
    }

    public void printSummaryReport() {

        System.out.println("============ Booking Summary Report =============");

        int single = 0, dbl = 0, suite = 0;

        for (Reservation r : historyRef.getHistory()) {
            switch (r.requestedRoomType) {
                case "Single Room": single++; break;
                case "Double Room": dbl++; break;
                case "Suite Room":  suite++; break;
            }
        }

        System.out.println("Total Bookings: " + historyRef.getHistory().size());
        System.out.println("Single Rooms Booked: " + single);
        System.out.println("Double Rooms Booked: " + dbl);
        System.out.println("Suite Rooms Booked: " + suite);

        System.out.println("=================================================\n");
    }
}


/* ===============================================================
            USE CASE 10: BOOKING CANCELLATION & ROLLBACK
   =============================================================== */

class BookingCancellationService {

    private RoomInventory inventoryRef;
    private BookingHistory historyRef;
    private Stack<String> rollbackStack = new Stack<>();

    public BookingCancellationService(RoomInventory inventoryRef, BookingHistory historyRef) {
        this.inventoryRef = inventoryRef;
        this.historyRef = historyRef;
    }

    public void cancelBooking(String roomID) {

        System.out.println("Attempting Cancellation for Room ID: " + roomID);

        Reservation r = historyRef.getByRoomID(roomID);

        if (r == null) {
            System.out.println("Cancellation Failed: No such booking exists.");
            return;
        }

        rollbackStack.push(roomID);

        inventoryRef.increment(r.requestedRoomType);

        historyRef.removeBooking(r);

        System.out.println("Cancellation Successful");
        System.out.println("Rolled back Room ID: " + rollbackStack.peek());
    }
}