import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

// Note: To handle Excel files for email reminders, you would typically use a library
// like Apache POI. For simplicity, this example will read from a simple .txt file
// where each line is an email address. This avoids external dependencies.

/**
 * Represents a single event.
 * This class holds all the information related to an event.
 */
class Event implements Serializable {
    private static final long serialVersionUID = 1L; // For serialization
    private final int id;
    private String name;
    private LocalDate date;
    private LocalTime time;
    private String type;
    private String location;

    public Event(int id, String name, LocalDate date, LocalTime time, String type, String location) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.type = type;
        this.location = location;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    // Setters for editing
    public void setName(String name) {
        this.name = name;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return String.format("ID: %-3d | Name: %-20s | Date: %-12s | Time: %-7s | Type: %-15s | Location: %s",
                id, name, date.format(dateFormatter), time.format(timeFormatter), type,
                location.isEmpty() ? "N/A" : location);
    }
}

/**
 * Handles reading and writing events to a file for persistent storage.
 */
class StorageManager {
    private static final String FILE_NAME = "events.dat";

    /**
     * Saves the list of events to a binary file.
     * 
     * @param events The list of events to save.
     */
    public void saveEvents(List<Event> events) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(events);
        } catch (IOException e) {
            System.out.println("Error: Could not save events to file. " + e.getMessage());
        }
    }

    /**
     * Loads the list of events from the binary file.
     * 
     * @return A list of events, or an empty list if the file doesn't exist or is
     *         empty.
     */
    @SuppressWarnings("unchecked")
    public List<Event> loadEvents() {
        File file = new File(FILE_NAME);
        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                return (List<Event>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(
                        "Error: Could not load events from file. A new file will be created. " + e.getMessage());
            }
        }
        return new ArrayList<>(); // Return empty list if file not found or error
    }
}

/**
 * Main class for the Smart Event Manager application.
 * Handles the command-line interface and all business logic.
 */
public class SmartEventManager {
    private List<Event> events;
    private final StorageManager storageManager;
    private final Scanner scanner;
    private int nextEventId = 1;

    // Formatters for date and time input
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String ADMIN_PASSWORD = "Aavishkar"; // Simple hardcoded password

    public SmartEventManager() {
        this.storageManager = new StorageManager();
        this.events = storageManager.loadEvents();
        this.scanner = new Scanner(System.in);
        // Determine the next event ID based on loaded events
        if (!events.isEmpty()) {
            nextEventId = events.stream().mapToInt(Event::getId).max().orElse(0) + 1;
        }
    }

    /**
     * The main entry point of the application.
     */
    public static void main(String[] args) {
        SmartEventManager manager = new SmartEventManager();
        manager.run();
    }

    /**
     * Main application loop that shows the menu and processes user input.
     */
    public void run() {
        System.out.println("Welcome to the Smart Event Manager!");
        while (true) {
            System.out.print("Enter admin password to manage events (or type 'exit' to close): ");
            String input = scanner.nextLine();

            if ("exit".equalsIgnoreCase(input)) {
                break;
            }

            if (ADMIN_PASSWORD.equals(input)) {
                adminMenu();
            } else {
                System.out.println("Incorrect password. Access denied.");
            }
        }
        System.out.println("Thank you for using Smart Event Manager. Goodbye!");
    }

    /**
     * Displays the admin menu and handles admin actions.
     */
    private void adminMenu() {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Add Event");
            System.out.println("2. Edit Event");
            System.out.println("3. Delete Event");
            System.out.println("4. View Events");
            System.out.println("5. Search Events");
            System.out.println("6. Send Event Reminders");
            System.out.println("7. View Statistics");
            System.out.println("8. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    addEvent();
                    break;
                case "2":
                    editEvent();
                    break;
                case "3":
                    deleteEvent();
                    break;
                case "4":
                    viewEventsMenu();
                    break;
                case "5":
                    searchEvents();
                    break;
                case "6":
                    sendReminders();
                    break;
                case "7":
                    viewStatistics();
                    break;
                case "8":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Handles adding a new event.
     */
    private void addEvent() {
        System.out.println("\n--- Add New Event ---");
        try {
            System.out.print("Enter Event Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Date (DD-MM-YYYY): ");
            LocalDate date = LocalDate.parse(scanner.nextLine(), DATE_FORMATTER);

            System.out.print("Enter Time (HH:MM): ");
            LocalTime time = LocalTime.parse(scanner.nextLine(), TIME_FORMATTER);

            // Conflict Detection
            if (hasConflict(date, time, -1)) { // -1 for ID means it's a new event
                System.out.println("!! Conflict Detected: An event already exists at this date and time.");
                suggestAvailableSlots(date);
                return;
            }

            System.out.print("Enter Event Type (e.g., Meeting, Conference, Personal): ");
            String type = scanner.nextLine();

            System.out.print("Enter Location (optional): ");
            String location = scanner.nextLine();

            Event newEvent = new Event(nextEventId++, name, date, time, type, location);
            events.add(newEvent);
            storageManager.saveEvents(events);
            System.out.println("Event added successfully!");

        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date or time format. Please use DD-MM-YYYY and HH:MM.");
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Handles editing an existing event.
     */
    private void editEvent() {
        System.out.println("\n--- Edit Event ---");
        System.out.print("Enter the ID of the event to edit: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Optional<Event> eventOpt = findEventById(id);

            if (eventOpt.isPresent()) {
                Event event = eventOpt.get();
                System.out.println("Editing Event: " + event);

                System.out.print("Enter new Name (or press Enter to keep '" + event.getName() + "'): ");
                String name = scanner.nextLine();
                if (!name.isEmpty())
                    event.setName(name);

                System.out.print("Enter new Date (DD-MM-YYYY) (or press Enter to keep '"
                        + event.getDate().format(DATE_FORMATTER) + "'): ");
                String dateStr = scanner.nextLine();
                LocalDate newDate = event.getDate();
                if (!dateStr.isEmpty()) {
                    newDate = LocalDate.parse(dateStr, DATE_FORMATTER);
                }

                System.out.print("Enter new Time (HH:MM) (or press Enter to keep '"
                        + event.getTime().format(TIME_FORMATTER) + "'): ");
                String timeStr = scanner.nextLine();
                LocalTime newTime = event.getTime();
                if (!timeStr.isEmpty()) {
                    newTime = LocalTime.parse(timeStr, TIME_FORMATTER);
                }

                // Check for conflicts with the new date/time
                if (hasConflict(newDate, newTime, event.getId())) {
                    System.out.println(
                            "!! Conflict Detected: Cannot move event to this time slot as it's already occupied.");
                    suggestAvailableSlots(newDate);
                    return;
                }
                event.setDate(newDate);
                event.setTime(newTime);

                System.out.print("Enter new Type (or press Enter to keep '" + event.getType() + "'): ");
                String type = scanner.nextLine();
                if (!type.isEmpty())
                    event.setType(type);

                System.out.print("Enter new Location (or press Enter to keep '" + event.getLocation() + "'): ");
                String location = scanner.nextLine();
                if (!location.isEmpty())
                    event.setLocation(location);

                storageManager.saveEvents(events);
                System.out.println("Event updated successfully!");

            } else {
                System.out.println("Event with ID " + id + " not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a number.");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date or time format.");
        }
    }

    /**
     * Handles deleting an event.
     */
    private void deleteEvent() {
        System.out.println("\n--- Delete Event ---");
        System.out.print("Enter the ID of the event to delete: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            boolean removed = events.removeIf(event -> event.getId() == id);

            if (removed) {
                storageManager.saveEvents(events);
                System.out.println("Event deleted successfully.");
            } else {
                System.out.println("Event with ID " + id + " not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a number.");
        }
    }

    /**
     * Displays the menu for viewing events.
     */
    private void viewEventsMenu() {
        System.out.println("\n--- View Events ---");
        System.out.println("1. View Today's Events");
        System.out.println("2. View Events for a Specific Day");
        System.out.println("3. View All Events");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                viewEventsForDate(LocalDate.now());
                break;
            case "2":
                System.out.print("Enter Date (DD-MM-YYYY): ");
                try {
                    LocalDate date = LocalDate.parse(scanner.nextLine(), DATE_FORMATTER);
                    viewEventsForDate(date);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format.");
                }
                break;
            case "3":
                displayEvents(events);
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    /**
     * Displays a list of events.
     * 
     * @param eventList The list of events to display.
     */
    private void displayEvents(List<Event> eventList) {
        if (eventList.isEmpty()) {
            System.out.println("No events to display.");
            return;
        }
        System.out.println(
                "-----------------------------------------------------------------------------------------------------");
        // Sort events by date and then by time before displaying
        eventList.stream()
                .sorted(Comparator.comparing(Event::getDate).thenComparing(Event::getTime))
                .forEach(System.out::println);
        System.out.println(
                "-----------------------------------------------------------------------------------------------------");
    }

    /**
     * Filters and displays events for a specific date.
     * 
     * @param date The date to filter by.
     */
    private void viewEventsForDate(LocalDate date) {
        System.out.println("\n--- Events for " + date.format(DATE_FORMATTER) + " ---");
        List<Event> dayEvents = events.stream()
                .filter(e -> e.getDate().equals(date))
                .collect(Collectors.toList());
        displayEvents(dayEvents);
    }

    /**
     * Searches for events by a keyword in their name or type.
     */
    private void searchEvents() {
        System.out.println("\n--- Search Events ---");
        System.out.print("Enter search keyword (for name or type): ");
        String keyword = scanner.nextLine().toLowerCase();

        List<Event> foundEvents = events.stream()
                .filter(e -> e.getName().toLowerCase().contains(keyword) || e.getType().toLowerCase().contains(keyword))
                .collect(Collectors.toList());

        System.out.println("Found " + foundEvents.size() + " matching event(s):");
        displayEvents(foundEvents);
    }

    /**
     * Sends reminders for a specific event.
     * Reads emails from a text file.
     */
    private void sendReminders() {
        System.out.println("\n--- Send Event Reminders ---");
        System.out.print("Enter the ID of the event to send reminders for: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Optional<Event> eventOpt = findEventById(id);

            if (eventOpt.isPresent()) {
                Event event = eventOpt.get();
                System.out.print("Enter the path to the attendee emails file (e.g., attendees.txt): ");
                String filePath = scanner.nextLine();

                try {
                    List<String> emails = Files.readAllLines(Paths.get(filePath));
                    if (emails.isEmpty()) {
                        System.out.println("No emails found in the file.");
                        return;
                    }

                    System.out.println("\nSending reminders for event: " + event.getName());
                    for (String email : emails) {
                        // This is a simulation. In a real app, you'd use JavaMail API.
                        System.out.println("  -> Sending reminder to " + email);
                    }
                    System.out.println("All reminders sent successfully.");

                } catch (IOException e) {
                    System.out.println("Error: Could not read the emails file. Please check the path.");
                }

            } else {
                System.out.println("Event with ID " + id + " not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }

    /**
     * Displays basic statistics about the events.
     */
    private void viewStatistics() {
        System.out.println("\n--- Event Statistics ---");
        if (events.isEmpty()) {
            System.out.println("No events to show statistics for.");
            return;
        }

        System.out.println("Total number of events: " + events.size());

        // Count events by type
        Map<String, Long> eventsByType = events.stream()
                .collect(Collectors.groupingBy(Event::getType, Collectors.counting()));

        System.out.println("\nEvents by Type:");
        eventsByType.forEach((type, count) -> System.out.println("  - " + type + ": " + count));
    }

    // --- Helper Methods ---

    private Optional<Event> findEventById(int id) {
        return events.stream().filter(event -> event.getId() == id).findFirst();
    }

    /**
     * Checks if a new or updated event conflicts with existing events.
     * A conflict is defined as another event on the same day and at the same time.
     * We assume events last for one hour for simplicity.
     * 
     * @param date    The date of the event to check.
     * @param time    The time of the event to check.
     * @param eventId The ID of the event being checked. Pass -1 for a new event,
     *                or the event's own ID if editing to exclude it from the check.
     * @return true if a conflict exists, false otherwise.
     */

    private boolean hasConflict(LocalDate date, LocalTime time, int eventId) {
        for (Event existingEvent : events) {
            if (existingEvent.getId() == eventId) {
                continue; // Skip checking against itself when editing
            }
            if (existingEvent.getDate().equals(date)) {
                // Assuming events are 1 hour long. A conflict occurs if the new event's start
                // time
                // is within the 1-hour block of an existing event.
                if (time.isBefore(existingEvent.getTime().plusHours(1))
                        && time.isAfter(existingEvent.getTime().minusHours(1))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Suggests available 1-hour time slots for a given date.
     * 
     * @param date The date to find slots for.
     */
    private void suggestAvailableSlots(LocalDate date) {
        System.out.println("--- Suggested Available Slots for " + date.format(DATE_FORMATTER) + " ---");

        // Get times of all events on the given day, sorted
        List<LocalTime> bookedTimes = events.stream()
                .filter(e -> e.getDate().equals(date))
                .map(Event::getTime)
                .sorted()
                .collect(Collectors.toList());

        List<String> availableSlots = new ArrayList<>();
        // Check slots from 8 AM to 5 PM (08:00 to 17:00)
        LocalTime potentialStartTime = LocalTime.of(8, 0);
        LocalTime dayEnd = LocalTime.of(17, 0);

        while (potentialStartTime.isBefore(dayEnd)) {
            final LocalTime checkTime = potentialStartTime;
            // Check if this potential start time conflicts with any booked time
            boolean isConflict = bookedTimes.stream().anyMatch(bookedTime -> checkTime.isBefore(bookedTime.plusHours(1))
                    && checkTime.isAfter(bookedTime.minusHours(1)));

            if (!isConflict) {
                availableSlots.add(checkTime.format(TIME_FORMATTER));
            }
            // Move to the next hour
            potentialStartTime = potentialStartTime.plusHours(1);
        }

        if (availableSlots.isEmpty()) {
            System.out.println("No available 1-hour slots found between 08:00 and 17:00.");
        } else {
            System.out.println("Available slots: " + String.join(", ", availableSlots));
        }
    }
}
