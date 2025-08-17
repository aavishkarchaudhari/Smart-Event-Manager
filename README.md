# 📅 Smart Event Manager

A robust, command-line based application built with Java for scheduling, managing, and tracking events. This project leverages Object-Oriented principles and file handling for persistent data storage, featuring smart conflict detection to prevent scheduling overlaps.

---

## ✨ Core Features

-   🔑 **Admin Authentication**: Secure access to event management functions with a simple, hardcoded password.
-   ➕ **Full CRUD Functionality**: Easily **Add**, **Edit**, and **Delete** events with details like name, date, time, type, and location.
-   ⚡ **Smart Conflict Detection**: Automatically detects and alerts you of overlapping events when adding or editing.
-   💡 **Slot Suggestions**: If a conflict is found, the system intelligently suggests available time slots for the selected day (between 08:00 and 17:00).
-   👀 **Multiple View Options**:
    -   `Today's Events`: See all events scheduled for the current date.
    -   `Day View`: Display all events for any selected date.
    -   `All Events`: Get a complete, sorted list of all scheduled events.
-   🔍 **Powerful Search**: Quickly find events by keyword, searching through event names and types (case-insensitive).
-   📧 **Simulated Reminders**: Load a list of attendee emails from a `.txt` file to simulate sending event reminders.
-   📊 **Insightful Statistics**: View key metrics, including the total number of events and a breakdown of events by type.
-   💾 **Persistent Storage**: All event data is automatically saved to a binary file (`events.dat`) using Java Serialization, ensuring your data is safe between sessions.

---

## 🚀 Getting Started

Follow these steps to compile and run the application on your local machine.

### 1. Prerequisites

-   Make sure you have **Java Development Kit (JDK) 8 or higher** installed. You can verify your installation by running:
    ```bash
    java -version
    ```

### 2. Compilation

-   Navigate to the project directory in your terminal and compile the `.java` file:
    ```bash
    javac SmartEventManager.java
    ```

### 3. Running the Application

-   Run the compiled Java class from your terminal:
    ```bash
    java SmartEventManager
    ```
-   You will be prompted to enter the admin password. The default password is **`Aavishkar`**.

---

## 📋 Example Usage

Here is a sample workflow of how to use the Smart Event Manager:

```plaintext
Welcome to the Smart Event Manager!
Enter admin password to manage events (or type 'exit' to close): Aavishkar

--- Admin Menu ---
1. Add Event
2. Edit Event
3. Delete Event
4. View Events
5. Search Events
6. Send Event Reminders
7. View Statistics
8. Logout
Choose an option: 1

--- Add New Event ---
Enter Event Name: Project Kick-off
Enter Date (DD-MM-YYYY): 25-12-2024
Enter Time (HH:MM): 11:00
Enter Event Type (e.g., Meeting, Conference, Personal): Meeting
Enter Location (optional): Conference Room A
Event added successfully!

--- Admin Menu ---
1. Add Event
...
Choose an option: 4

--- View Events ---
1. View Today's Events
2. View Events for a Specific Day
3. View All Events
Choose an option: 3

-----------------------------------------------------------------------------------------------------
ID: 1   | Name: Project Kick-off       | Date: 25-12-2024   | Time: 11:00   | Type: Meeting         | Location: Conference Room A
-----------------------------------------------------------------------------------------------------
