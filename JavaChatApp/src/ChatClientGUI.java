import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatClientGUI extends JFrame {
    private JTextArea messageArea;
    private JTextField textField;
    private JButton sendButton;
    private ChatClient client;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private String username;

    public ChatClientGUI() {
        super("Chat Application");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Prompt for the user's name
        username = JOptionPane.showInputDialog(this, "Enter your name:");
        if (username == null || username.trim().isEmpty()) {
            username = "Anonymous"; // Default name if input is blank or canceled
        }
        this.setTitle("Chat Application - " + username);

        // Message area setup
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        messageArea.setBackground(Color.LIGHT_GRAY);
        add(new JScrollPane(messageArea), BorderLayout.CENTER);

        // Text input area with send button
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        textField = new JTextField();
        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());

        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // Add KeyListener to the text field for Enter key functionality
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        // Initialize and start the ChatClient
        try {
            this.client = new ChatClient("127.0.0.1", 5000, this::onMessageReceived);
            client.startClient();
            appendMessage("Connected to the server.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error connecting to the server", "Connection error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void sendMessage() {
        String message = textField.getText().trim();
        if (!message.isEmpty()) {
            // Include the username with each message and append it to the message area
            String fullMessage = username + ": " + message;
            appendMessage(fullMessage); // Display the sent message
            client.sendMessage(fullMessage); // Send the message to the server
            textField.setText(""); // Clear the text field after sending
        }
    }

    private void onMessageReceived(String message) {
        appendMessage(message); // Display the received message
    }

    private void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            String time = timeFormat.format(new Date());
            messageArea.append("[" + time + "] " + message + "\n");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClientGUI().setVisible(true));
    }
}
