import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.time.*;
import java.time.format.DateTimeFormatter;

// Model Classes
class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private String mobile;
    private boolean isAdmin;

    public User(String id, String name, String email, String password, String mobile, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.isAdmin = isAdmin;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getMobile() { return mobile; }
    public boolean isAdmin() { return isAdmin; }
}

class ParkingSlot {
    private String id;
    private String location;
    private String status; // AVAILABLE, OCCUPIED, MAINTENANCE
    private String type;   // STANDARD, PREMIUM, HANDICAPPED
    private double price;  // price per minute

    public ParkingSlot(String id, String location, String status, String type, double price) {
        this.id = id;
        this.location = location;
        this.status = status;
        this.type = type;
        this.price = price;
    }

    public String getId() { return id; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
    public String getType() { return type; }
    public double getPrice() { return price; }

    public void setStatus(String status) { this.status = status; }
}

class Booking {
    private String id;
    private String userId;
    private String slotId;
    private LocalDateTime bookingTime;
    private int duration;
    private String status;

    public Booking(String id, String userId, String slotId, LocalDateTime bookingTime, int duration, String status) {
        this.id = id;
        this.userId = userId;
        this.slotId = slotId;
        this.bookingTime = bookingTime;
        this.duration = duration;
        this.status = status;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getSlotId() { return slotId; }
    public LocalDateTime getBookingTime() { return bookingTime; }
    public int getDuration() { return duration; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

class Payment {
    private String id;
    private String bookingId;
    private double amount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paymentTime;

    public Payment(String id, String bookingId, double amount, String paymentMethod, String paymentStatus, LocalDateTime paymentTime) {
        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentTime = paymentTime;
    }

    public String getId() { return id; }
    public String getBookingId() { return bookingId; }
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public LocalDateTime getPaymentTime() { return paymentTime; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}

// Service Classes
class UserService {
    private Map<String, User> users = new HashMap<>();
    private Map<String, String> userCredentials = new HashMap<>();

    public UserService() {
        // Initialize test users
        addUser(new User("1", "Admin", "admin@parking.com", "admin123", "1234567890", true));
        addUser(new User("2", "John Doe", "john@example.com", "password123", "9876543210", false));
    }

    public boolean addUser(User user) {
        if (users.containsKey(user.getEmail())) {
            return false;
        }
        users.put(user.getEmail(), user);
        userCredentials.put(user.getEmail(), user.getPassword());
        return true;
    }

    public User loginUser(String email, String password) {
        System.out.println("Login attempt - Email: " + email);
        if (userCredentials.containsKey(email)) {
            String storedPassword = userCredentials.get(email);
            if (storedPassword.equals(password)) {
                System.out.println("Login successful for: " + email);
                return users.get(email);
            }
        }
        System.out.println("Login failed for: " + email);
        return null;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}


class ParkingService {
    private Map<String, ParkingSlot> parkingSlots = new HashMap<>();
    private List<ParkingSlot> slotsList = new ArrayList<>();

    public ParkingService() {
        addParkingSlot(new ParkingSlot("1", "A1", "AVAILABLE", "STANDARD", 5.0));
        addParkingSlot(new ParkingSlot("2", "A2", "AVAILABLE", "STANDARD", 5.0));
        addParkingSlot(new ParkingSlot("3", "B1", "AVAILABLE", "PREMIUM", 10.0));
        addParkingSlot(new ParkingSlot("4", "B2", "MAINTENANCE", "PREMIUM", 10.0));
        addParkingSlot(new ParkingSlot("5", "C1", "AVAILABLE", "HANDICAPPED", 3.0));
    }

    public void addParkingSlot(ParkingSlot slot) {
        parkingSlots.put(slot.getId(), slot);
        slotsList.add(slot);
    }

    public List<ParkingSlot> getAllParkingSlots() {
        return slotsList;
    }

    public List<ParkingSlot> getAvailableParkingSlots() {
        List<ParkingSlot> availableSlots = new ArrayList<>();
        for (ParkingSlot slot : slotsList) {
            if ("AVAILABLE".equals(slot.getStatus())) {
                availableSlots.add(slot);
            }
        }
        return availableSlots;
    }

    public boolean bookParkingSlot(String slotId) {
        ParkingSlot slot = parkingSlots.get(slotId);
        if (slot != null && "AVAILABLE".equals(slot.getStatus())) {
            slot.setStatus("OCCUPIED");
            return true;
        }
        return false;
    }

    public boolean releaseParkingSlot(String slotId) {
        ParkingSlot slot = parkingSlots.get(slotId);
        if (slot != null && "OCCUPIED".equals(slot.getStatus())) {
            slot.setStatus("AVAILABLE");
            return true;
        }
        return false;
    }
}

class BookingService {
    private Map<String, Booking> bookings = new HashMap<>();
    private Map<String, List<Booking>> userBookings = new HashMap<>();
    private int bookingCounter = 1;

    public Booking createBooking(String userId, String slotId, int duration) {
        String bookingId = "B" + bookingCounter++;
        LocalDateTime now = LocalDateTime.now();
        Booking booking = new Booking(bookingId, userId, slotId, now, duration, "CONFIRMED");
        
        bookings.put(bookingId, booking);
        
        if (!userBookings.containsKey(userId)) {
            userBookings.put(userId, new ArrayList<>());
        }
        userBookings.get(userId).add(booking);
        
        return booking;
    }

    public boolean cancelBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking != null && "CONFIRMED".equals(booking.getStatus())) {
            booking.setStatus("CANCELLED");
            return true;
        }
        return false;
    }

    public List<Booking> getUserBookings(String userId) {
        return userBookings.getOrDefault(userId, new ArrayList<>());
    }
}

class PaymentService {
    private Map<String, Payment> payments = new HashMap<>();
    private Map<String, Payment> bookingPayments = new HashMap<>();
    private int paymentCounter = 1;

    public Payment createPayment(String bookingId, double amount, String paymentMethod) {
        String paymentId = "P" + paymentCounter++;
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(paymentId, bookingId, amount, paymentMethod, "COMPLETED", now);
        
        payments.put(paymentId, payment);
        bookingPayments.put(bookingId, payment);
        
        return payment;
    }

    public Payment getPaymentForBooking(String bookingId) {
        return bookingPayments.get(bookingId);
    }
}

// Shared application context to keep services singleton across the app
class AppContext {
    public static final UserService userService = new UserService();
    public static final ParkingService parkingService = new ParkingService();
    public static final BookingService bookingService = new BookingService();
    public static final PaymentService paymentService = new PaymentService();
}

// UI Classes
class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private UserService userService;

    public LoginFrame() {
        this.userService = AppContext.userService;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Smart Parking System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);
        
        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);
        
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            
            User user = userService.loginUser(email, password);
            if (user != null) {
                if (user.isAdmin()) {
                    new AdminDashboard().setVisible(true);
                } else {
                    new UserDashboard(user).setVisible(true);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginFrame.this, 
                        "Invalid email or password", "Login Failed", 
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            new RegistrationFrame().setVisible(true);
            dispose();
        });
        
        panel.add(loginButton);
        panel.add(registerButton);
        
        add(panel);
    }
}


class RegistrationFrame extends JFrame {
    private JTextField nameField, emailField, mobileField;
    private JPasswordField passwordField;
    private JButton registerButton, backButton;
    private UserService userService;

    public RegistrationFrame() {
        this.userService = AppContext.userService;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Smart Parking System - Registration");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Initialize all UI components
        panel.add(new JLabel("Name:"));
        nameField = new JTextField();
        panel.add(nameField);
        
        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);
        
        panel.add(new JLabel("Mobile:"));
        mobileField = new JTextField();
        panel.add(mobileField);
        
        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);
        
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");
        
        registerButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String mobile = mobileField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            
            if (name.isEmpty() || email.isEmpty() || mobile.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "All fields are required", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User newUser = new User(
                UUID.randomUUID().toString(),
                name,
                email,
                password,
                mobile,
                false
            );
            
            if (userService.addUser(newUser)) {
                JOptionPane.showMessageDialog(this, 
                    "Registration successful!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                new LoginFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Email already registered", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        
        panel.add(registerButton);
        panel.add(backButton);
        
        add(panel);
    }
}


class UserDashboard extends JFrame {
    private User user;
    private ParkingService parkingService;
    private BookingService bookingService;
    private PaymentService paymentService;

    public UserDashboard(User user) {
        this.user = user;
        this.parkingService = AppContext.parkingService;
        this.bookingService = AppContext.bookingService;
        this.paymentService = AppContext.paymentService;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Smart Parking System - User Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(new JLabel("Welcome, " + user.getName()));
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        JPanel bookParkingPanel = createBookParkingPanel();
        tabbedPane.addTab("Book Parking", bookParkingPanel);
        
        JPanel myBookingsPanel = createMyBookingsPanel();
        tabbedPane.addTab("My Bookings", myBookingsPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        footerPanel.add(logoutButton);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private JPanel createBookParkingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        List<ParkingSlot> availableSlots = parkingService.getAvailableParkingSlots();
        
        JPanel slotsPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        slotsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (ParkingSlot slot : availableSlots) {
            JPanel slotPanel = new JPanel(new BorderLayout());
            slotPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            
            JLabel locationLabel = new JLabel(slot.getLocation());
            JLabel typeLabel = new JLabel("Type: " + slot.getType());
            JLabel priceLabel = new JLabel("Price: $" + slot.getPrice());
            
            JButton bookButton = new JButton("Book");
            bookButton.addActionListener(e -> {
                String durationStr = JOptionPane.showInputDialog(this, "Enter duration in minutes:");
                try {
                    int duration = Integer.parseInt(durationStr);
                    if (duration <= 0) {
                        throw new NumberFormatException();
                    }
                    
                    if (parkingService.bookParkingSlot(slot.getId())) {
                        Booking booking = bookingService.createBooking(user.getId(), slot.getId(), duration);
                        double amount = slot.getPrice() * duration;
                        paymentService.createPayment(booking.getId(), amount, "CREDIT_CARD");
                        
                        JOptionPane.showMessageDialog(this, 
                            "Booking successful!\n" +
                            "Slot: " + slot.getLocation() + "\n" +
                            "Duration: " + duration + " minutes\n" +
                            "Amount: $" + amount,
                            "Booking Confirmation", JOptionPane.INFORMATION_MESSAGE);
                        
                        panel.removeAll();
                        panel.add(createBookParkingPanel(), BorderLayout.CENTER);
                        panel.revalidate();
                        panel.repaint();
                    } else {
                        JOptionPane.showMessageDialog(this, "Slot is no longer available", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid duration", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            JPanel infoPanel = new JPanel(new GridLayout(3, 1));
            infoPanel.add(locationLabel);
            infoPanel.add(typeLabel);
            infoPanel.add(priceLabel);
            
            slotPanel.add(infoPanel, BorderLayout.CENTER);
            slotPanel.add(bookButton, BorderLayout.SOUTH);
            
            slotsPanel.add(slotPanel);
        }
        
        JScrollPane scrollPane = new JScrollPane(slotsPanel);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createMyBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        List<Booking> bookings = bookingService.getUserBookings(user.getId());
        
        if (bookings.isEmpty()) {
            panel.add(new JLabel("You have no bookings yet.", JLabel.CENTER), BorderLayout.CENTER);
            return panel;
        }
        
        String[] columnNames = {"Booking ID", "Slot", "Time", "Duration", "Status", "Amount", "Action"};
        Object[][] data = new Object[bookings.size()][7];
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            ParkingSlot slot = parkingService.getAllParkingSlots().stream()
                    .filter(s -> s.getId().equals(booking.getSlotId()))
                    .findFirst()
                    .orElse(null);
            
            Payment payment = paymentService.getPaymentForBooking(booking.getId());
            
            data[i][0] = booking.getId();
            data[i][1] = slot != null ? slot.getLocation() : "Unknown";
            data[i][2] = booking.getBookingTime().format(formatter);
            data[i][3] = booking.getDuration() + " mins";
            data[i][4] = booking.getStatus();
            data[i][5] = payment != null ? "$" + payment.getAmount() : "N/A";
            
            JButton actionButton;
            if ("CONFIRMED".equals(booking.getStatus())) {
                actionButton = new JButton("Cancel");
                actionButton.addActionListener(e -> {
                    if (bookingService.cancelBooking(booking.getId())) {
                        if (slot != null) {
                            parkingService.releaseParkingSlot(slot.getId());
                        }
                        JOptionPane.showMessageDialog(this, "Booking cancelled successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        panel.removeAll();
                        panel.add(createMyBookingsPanel(), BorderLayout.CENTER);
                        panel.revalidate();
                        panel.repaint();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to cancel booking", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } else {
                actionButton = new JButton("View");
                actionButton.setEnabled(false);
            }
            
            data[i][6] = actionButton;
        }
        
        JTable bookingsTable = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        
        bookingsTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        bookingsTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
}

class AdminDashboard extends JFrame {
    private ParkingService parkingService;
    private UserService userService;

    public AdminDashboard() {
        this.parkingService = AppContext.parkingService;
        this.userService = AppContext.userService;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Smart Parking System - Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(new JLabel("Admin Dashboard"));
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        JPanel parkingManagementPanel = createParkingManagementPanel();
        tabbedPane.addTab("Parking Management", parkingManagementPanel);
        
        JPanel userManagementPanel = createUserManagementPanel();
        tabbedPane.addTab("User Management", userManagementPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        footerPanel.add(logoutButton);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private JPanel createParkingManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel addSlotPanel = new JPanel(new GridLayout(1, 6, 5, 5));
        addSlotPanel.setBorder(BorderFactory.createTitledBorder("Add New Parking Slot"));
        
        JTextField locationField = new JTextField();
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"STANDARD", "PREMIUM", "HANDICAPPED"});
        JTextField priceField = new JTextField();
        JButton addButton = new JButton("Add Slot");
        
        addSlotPanel.add(new JLabel("Location:"));
        addSlotPanel.add(locationField);
        addSlotPanel.add(new JLabel("Type:"));
        addSlotPanel.add(typeComboBox);
        addSlotPanel.add(new JLabel("Price:"));
        addSlotPanel.add(priceField);
        
        addButton.addActionListener(e -> {
            try {
                String location = locationField.getText();
                String type = (String) typeComboBox.getSelectedItem();
                double price = Double.parseDouble(priceField.getText());
                
                if (location.isEmpty()) {
                    throw new IllegalArgumentException("Location cannot be empty");
                }
                
                ParkingSlot newSlot = new ParkingSlot(
                    UUID.randomUUID().toString(),
                    location,
                    "AVAILABLE",
                    type,
                    price
                );
                
                parkingService.addParkingSlot(newSlot);
                
                JOptionPane.showMessageDialog(this, "Parking slot added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                panel.removeAll();
                panel.add(createParkingManagementPanel(), BorderLayout.CENTER);
                panel.revalidate();
                panel.repaint();
                
                locationField.setText("");
                priceField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid price", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(addSlotPanel, BorderLayout.NORTH);
        
        List<ParkingSlot> allSlots = parkingService.getAllParkingSlots();
        String[] columnNames = {"ID", "Location", "Type", "Price", "Status", "Action"};
        Object[][] data = new Object[allSlots.size()][6];
        
        for (int i = 0; i < allSlots.size(); i++) {
            ParkingSlot slot = allSlots.get(i);
            data[i][0] = slot.getId();
            data[i][1] = slot.getLocation();
            data[i][2] = slot.getType();
            data[i][3] = "$" + slot.getPrice();
            data[i][4] = slot.getStatus();
            
            JButton actionButton;
            if ("MAINTENANCE".equals(slot.getStatus())) {
                actionButton = new JButton("Activate");
                actionButton.addActionListener(e -> {
                    slot.setStatus("AVAILABLE");
                    JOptionPane.showMessageDialog(this, "Slot activated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    panel.removeAll();
                    panel.add(createParkingManagementPanel(), BorderLayout.CENTER);
                    panel.revalidate();
                    panel.repaint();
                });
            } else {
                actionButton = new JButton("Maintenance");
                actionButton.addActionListener(e -> {
                    slot.setStatus("MAINTENANCE");
                    JOptionPane.showMessageDialog(this, "Slot marked for maintenance", "Success", JOptionPane.INFORMATION_MESSAGE);
                    panel.removeAll();
                    panel.add(createParkingManagementPanel(), BorderLayout.CENTER);
                    panel.revalidate();
                    panel.repaint();
                });
            }
            
            data[i][5] = actionButton;
        }
        
        JTable slotsTable = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        
        slotsTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        slotsTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(slotsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        List<User> allUsers = userService.getAllUsers();
        String[] columnNames = {"ID", "Name", "Email", "Mobile", "Role"};
        Object[][] data = new Object[allUsers.size()][5];
        
        for (int i = 0; i < allUsers.size(); i++) {
            User user = allUsers.get(i);
            data[i][0] = user.getId();
            data[i][1] = user.getName();
            data[i][2] = user.getEmail();
            data[i][3] = user.getMobile();
            data[i][4] = user.isAdmin() ? "Admin" : "User";
        }
        
        JTable usersTable = new JTable(data, columnNames);
        usersTable.setEnabled(false);
        
        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
}

class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof JButton) {
            return (JButton) value;
        }
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String label;
    private boolean isPushed;

    public ButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        if (value instanceof JButton) {
            button = (JButton) value;
            label = button.getText();
        }
        isPushed = true;
        return button;
    }

    public Object getCellEditorValue() {
        if (isPushed) {
            button.doClick();
        }
        isPushed = false;
        return label;
    }

    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}