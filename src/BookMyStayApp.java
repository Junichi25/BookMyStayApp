import java.util.*;
import java.io.*;

/* ===============================================================
                     MAIN APPLICATION
   =============================================================== */
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("============================================");
        System.out.println("      Welcome to the Book My Stay System    ");
        System.out.println("============================================");
        System.out.println("Hotel Booking Management System - Version 1.0");
        System.out.println("System initialized successfully.");
        System.out.println("Application ready for operations...\n");

        // ===========================================================
        //       USE CASE 12: LOAD PERSISTED SYSTEM STATE (RECOVERY)
        // ===========================================================

        System.out.println("\n============================================");
        System.out.println("   Use Case 12: System Recovery on Startup  ");
        System.out.println("============================================\n");

        PersistenceService persistence = new PersistenceService();

        RoomInventory inventory = persistence.loadInventory();
        BookingHistory bookingHistory = persistence.loadHistory();

        if (inventory == null) {
            System.out.println("No saved inventory found. Initializing fresh data...\n");
            inventory = new RoomInventory();
            inventory.addRoomType("Single Room", 5);
            inventory.addRoomType("Double Room", 3);
            inventory.addRoomType("Suite Room", 2);
        }

        if (bookingHistory == null) {
            System.out.println("No saved booking history found. Initializing fresh history...\n");
            bookingHistory = new BookingHistory();
        }

        // ---------- USE CASE 2 ----------
        System.out.println("Use Case 2: Basic Room Types & Static Availability\n");

        SingleRoom single = new SingleRoom();
        DoubleRoom doubleRoom = new DoubleRoom();
        SuiteRoom suiteRoom = new SuiteRoom();

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
            requestQueue.addRequest(new Reservation("Rahul", "InvalidRoom"));
        }
        catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }

        requestQueue.displayQueue();

        // ---------- USE CASE 6 ----------
        System.out.println("\n============================================");
        System.out.println(" Use Case 6: Reservation Confirmation & Room Allocation ");
        System.out.println("============================================\n");

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

        // ===========================================================
        //                USE CASE 11: CONCURRENCY
        // ===========================================================

        System.out.println("\n============================================");
        System.out.println("   Use Case 11: Concurrent Booking Simulation");
        System.out.println("============================================\n");

        BookingRequestQueue sharedQueue = new BookingRequestQueue();

        RoomInventory sharedInventory = new RoomInventory();
        sharedInventory.addRoomType("Single Room", 3);
        sharedInventory.addRoomType("Double Room", 2);
        sharedInventory.addRoomType("Suite Room", 1);

        BookingHistory concurrentHistory = new BookingHistory();

        RoomAllocationService concurrentAllocator =
                new RoomAllocationService(sharedInventory, concurrentHistory);

        Thread t1 = new Thread(new ConcurrentBookingTask(sharedQueue, "Ravi", "Single Room"));
        Thread t2 = new Thread(new ConcurrentBookingTask(sharedQueue, "Sneha", "Suite Room"));
        Thread t3 = new Thread(new ConcurrentBookingTask(sharedQueue, "Arjun", "Double Room"));
        Thread t4 = new Thread(new ConcurrentBookingTask(sharedQueue, "Neha", "Single Room"));

        t1.start(); t2.start(); t3.start(); t4.start();

        try { t1.join(); t2.join(); t3.join(); t4.join(); }
        catch (Exception ex) {}

        System.out.println("\nProcessing all concurrent requests safely...\n");

        try {
            concurrentAllocator.processAllRequests(sharedQueue);
        }
        catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }

        System.out.println("\nFinal Inventory After Concurrent Allocation:");
        sharedInventory.displayInventory();


        // ===========================================================
        //        USE CASE 12: SAVE SYSTEM STATE ON SHUTDOWN
        // ===========================================================

        System.out.println("\n============================================");
        System.out.println("   Use Case 12: Saving System State Safely  ");
        System.out.println("============================================\n");

        persistence.saveInventory(inventory);
        persistence.saveHistory(bookingHistory);

        System.out.println("System state saved successfully. Safe to shutdown.\n");
    }
}

/* ===============================================================
                  USE CASE 11 SUPPORT CLASS
   =============================================================== */

class ConcurrentBookingTask implements Runnable {

    private BookingRequestQueue queue;
    private String guest;
    private String roomType;

    public ConcurrentBookingTask(BookingRequestQueue queue, String guest, String roomType) {
        this.queue = queue;
        this.guest = guest;
        this.roomType = roomType;
    }

    @Override
    public void run() {
        synchronized (queue) {
            try {
                queue.addRequest(new Reservation(guest, roomType));
            }
            catch (Exception ex) {
                System.out.println("Thread Error: " + ex.getMessage());
            }
        }
    }
}

/* ===============================================================
                  USE CASE 9: CUSTOM EXCEPTIONS
   =============================================================== */

class InvalidRoomTypeException extends Exception {
    public InvalidRoomTypeException(String m){super(m);}
}
class InvalidGuestNameException extends Exception {
    public InvalidGuestNameException(String m){super(m);}
}
class InventoryException extends Exception {
    public InventoryException(String m){super(m);}
}

/* ===============================================================
                  USE CASE 9: VALIDATION SERVICE
   =============================================================== */

class ValidationService {

    public static void validateGuest(String guestName)
            throws InvalidGuestNameException {

        if (guestName == null || guestName.trim().isEmpty())
            throw new InvalidGuestNameException("Guest name cannot be empty.");
    }

    public static void validateRoomType(String type, RoomInventory inv)
            throws InvalidRoomTypeException {

        if (!inv.exists(type))
            throw new InvalidRoomTypeException("Invalid room type: " + type);
    }

    public static void validateAvailability(String type, RoomInventory inv)
            throws InventoryException {

        if (inv.getAvailability(type) <= 0)
            throw new InventoryException("No rooms available for: " + type);
    }
}

/* ===============================================================
                          ROOM CLASSES
   =============================================================== */

abstract class Room {
    protected int numberOfBeds;
    protected int squareFeet;
    protected double pricePerNight;

    public Room(int beds, int sqft, double price) {
        this.numberOfBeds = beds;
        this.squareFeet = sqft;
        this.pricePerNight = price;
    }

    public void displayRoomDetails() {
        System.out.println("Beds: " + numberOfBeds);
        System.out.println("Size: " + squareFeet + " sqft");
        System.out.println("Price per night: ₹" + pricePerNight);
    }
}

class SingleRoom extends Room { public SingleRoom() { super(1, 250, 1500);} }
class DoubleRoom extends Room { public DoubleRoom() { super(2, 400, 2500);} }
class SuiteRoom  extends Room { public SuiteRoom()  { super(3, 750, 5000);} }

/* ===============================================================
                  INVENTORY (updated)
   =============================================================== */

class RoomInventory implements Serializable {

    private static final long serialVersionUID = 1L;

    private HashMap<String, Integer> inventory = new HashMap<>();

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    public boolean exists(String type) {
        return inventory.containsKey(type);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public synchronized void decrement(String type) throws InventoryException {
        int cur = inventory.getOrDefault(type, 0);
        if (cur <= 0) throw new InventoryException("No rooms left: " + type);
        inventory.put(type, cur - 1);
    }

    public synchronized void increment(String type) {
        inventory.put(type, inventory.getOrDefault(type, 0) + 1);
    }

    public void displayInventory() {
        System.out.println("-------------------------");
        for (String t : inventory.keySet()) {
            System.out.println(t + " -> Available: " + inventory.get(t));
        }
        System.out.println("-------------------------\n");
    }

    public HashMap<String,Integer> getAll() { return inventory; }
}

/* ===============================================================
                  USE CASE 4: ROOM SEARCH
   =============================================================== */

class RoomSearchService {

    private RoomInventory inventory;

    public RoomSearchService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void displayAvailableRooms(HashMap<String, Room> models) {

        System.out.println("Searching available rooms...");
        System.out.println("----------------------------------");

        for (String rt : models.keySet()) {

            int avail = inventory.getAvailability(rt);

            if (avail > 0) {
                System.out.println("\nRoom Type: " + rt);
                models.get(rt).displayRoomDetails();
                System.out.println("Available: " + avail);
            }
        }

        System.out.println("\nSearch Completed\n");
    }
}

/* ===============================================================
                  USE CASE 5: REQUEST QUEUE
   =============================================================== */

class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    String guestName;
    String requestedRoomType;
    String assignedRoomID;

    public Reservation(String guest, String type) {
        this.guestName = guest;
        this.requestedRoomType = type;
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
        System.out.println("\nPending Requests (FIFO):");
        for (Reservation r : queue) {
            System.out.println("Guest: " + r.guestName +
                    " | Room: " + r.requestedRoomType);
        }
    }
}

/* ===============================================================
                  USE CASE 6: ROOM ALLOCATION
   =============================================================== */

class RoomAllocationService {

    private RoomInventory inventory;
    private BookingHistory history;

    private Set<String> allocatedIDs = new HashSet<>();

    public RoomAllocationService(RoomInventory inv, BookingHistory hist) {
        this.inventory = inv;
        this.history = hist;
    }

    public synchronized List<String> processAllRequests(BookingRequestQueue queue) throws Exception {

        List<String> ids = new ArrayList<>();

        while (!queue.isEmpty()) {

            Reservation r = queue.getNext();

            ValidationService.validateRoomType(r.requestedRoomType, inventory);
            ValidationService.validateAvailability(r.requestedRoomType, inventory);

            String roomID = generateUniqueID(r.requestedRoomType);

            allocatedIDs.add(roomID);

            inventory.decrement(r.requestedRoomType);

            r.assignedRoomID = roomID;
            history.addBooking(r);

            System.out.println("ALLOCATED: " + r.guestName +
                    " -> " + roomID);

            ids.add(roomID);
        }

        return ids;
    }

    private String generateUniqueID(String type) {

        String id;
        do {
            id = type.charAt(0) + "-" + ((int)(Math.random()*900+100));
        } while (allocatedIDs.contains(id));

        return id;
    }
}

/* ===============================================================
                USE CASE 7: ADD-ON SERVICES
   =============================================================== */

class AddOnService {
    String name;
    double cost;
    public AddOnService(String n, double c) { name=n; cost=c; }
}

class AddOnServiceManager {

    private HashMap<String, List<AddOnService>> map = new HashMap<>();

    public void addService(String id, AddOnService s) {
        map.putIfAbsent(id, new ArrayList<>());
        map.get(id).add(s);
    }

    public void printServiceReport() {

        System.out.println("\n==== Add-On Services Report ====");

        for (String id : map.keySet()) {
            System.out.println("\nReservation ID: " + id);

            double total = 0;

            for (AddOnService s : map.get(id)) {
                System.out.println(" - " + s.name + " : ₹" + s.cost);
                total += s.cost;
            }

            System.out.println("Total: ₹" + total);
        }

        System.out.println("===============================\n");
    }
}

/* ===============================================================
           USE CASE 8: BOOKING HISTORY & REPORTS
   =============================================================== */

class BookingHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Reservation> bookings = new ArrayList<>();

    public void addBooking(Reservation r) { bookings.add(r); }

    public List<Reservation> getHistory() { return bookings; }

    public Reservation getByRoomID(String id) {
        for (Reservation r : bookings) {
            if (id.equals(r.assignedRoomID))
                return r;
        }
        return null;
    }

    public void removeBooking(Reservation r) {
        bookings.remove(r);
    }
}

class BookingReportService {

    private BookingHistory history;

    public BookingReportService(BookingHistory h) {
        history = h;
    }

    public void printBookingHistory() {

        System.out.println("==== Booking History ====");

        for (Reservation r : history.getHistory()) {
            System.out.println("Guest: " + r.guestName +
                    " | Type: " + r.requestedRoomType +
                    " | Room ID: " + r.assignedRoomID);
        }

        System.out.println("=========================\n");
    }

    public void printSummaryReport() {

        int s=0, d=0, su=0;

        for (Reservation r : history.getHistory()) {
            switch(r.requestedRoomType) {
                case "Single Room": s++; break;
                case "Double Room": d++; break;
                case "Suite Room": su++; break;
            }
        }

        System.out.println("==== Summary ====");
        System.out.println("Single: " + s);
        System.out.println("Double: " + d);
        System.out.println("Suite: " + su);
        System.out.println("==================\n");
    }
}

/* ===============================================================
      USE CASE 10: BOOKING CANCELLATION & ROLLBACK
   =============================================================== */

class BookingCancellationService {

    private RoomInventory inventory;
    private BookingHistory history;

    private Stack<String> rollback = new Stack<>();

    public BookingCancellationService(RoomInventory inv, BookingHistory hist) {
        inventory = inv;
        history = hist;
    }

    public void cancelBooking(String roomID) {

        Reservation r = history.getByRoomID(roomID);

        if (r == null) {
            System.out.println("No booking found for: " + roomID);
            return;
        }

        rollback.push(roomID);

        inventory.increment(r.requestedRoomType);

        history.removeBooking(r);

        System.out.println("Cancelled booking for Room ID: " + roomID);
    }
}

/* ===============================================================
      USE CASE 12: DATA PERSISTENCE & SYSTEM RECOVERY
   =============================================================== */

class PersistenceService {

    private final String INVENTORY_FILE = "inventory.dat";
    private final String HISTORY_FILE   = "history.dat";

    /* ------------------ SAVE METHODS ------------------ */

    public void saveInventory(RoomInventory inv) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(INVENTORY_FILE))) {

            oos.writeObject(inv);
            System.out.println("Inventory saved.");

        } catch (Exception ex) {
            System.out.println("Error saving inventory: " + ex.getMessage());
        }
    }

    public void saveHistory(BookingHistory hist) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(HISTORY_FILE))) {

            oos.writeObject(hist);
            System.out.println("Booking history saved.");

        } catch (Exception ex) {
            System.out.println("Error saving booking history: " + ex.getMessage());
        }
    }

    /* ------------------ LOAD METHODS ------------------ */

    public RoomInventory loadInventory() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(INVENTORY_FILE))) {

            System.out.println("Recovered saved inventory from file.\n");
            return (RoomInventory) ois.readObject();

        } catch (Exception ex) {
            System.out.println("No previous inventory found.");
            return null;
        }
    }

    public BookingHistory loadHistory() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(HISTORY_FILE))) {

            System.out.println("Recovered saved booking history from file.\n");
            return (BookingHistory) ois.readObject();

        } catch (Exception ex) {
            System.out.println("No previous booking history found.");
            return null;
        }
    }
}