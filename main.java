package com.chatapp.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.text.SimpleDateFormat;

/**
 * ChatServer - Main server class for the Simple Chat Application
 * Handles multiple client connections and broadcasts messages to all connected users
 * 
 * @author Your Name
 * @version 1.0
 */
public class ChatServer {
    
    // Server configuration
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 50;
    
    // Server components
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private Map<String, ClientHandler> connectedClients;
    private boolean isRunning;
    
    // Logging
    private SimpleDateFormat timeFormat;
    
    /**
     * Constructor - Initialize server components
     */
    public ChatServer() {
        this.connectedClients = new ConcurrentHashMap<>();
        this.threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
        this.timeFormat = new SimpleDateFormat("HH:mm:ss");
        this.isRunning = false;
    }
    
    /**
     * Start the chat server
     */
    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            isRunning = true;
            
            System.out.println("=================================");
            System.out.println("🚀 Chat Server Started Successfully");
            System.out.println("📍 Port: " + PORT);
            System.out.println("👥 Max Clients: " + MAX_CLIENTS);
            System.out.println("⏰ Time: " + timeFormat.format(new Date()));
            System.out.println("=================================");
            
            // Accept client connections
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    
                    // Create new client handler
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    
                    // Submit to thread pool
                    threadPool.submit(clientHandler);
                    
                    logMessage("New client connection from: " + clientSocket.getInetAddress());
                    
                } catch (IOException e) {
                    if (isRunning) {
                        logError("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            logError("Failed to start server: " + e.getMessage());
        }
    }
    
    /**
     * Stop the chat server
     */
    public void stopServer() {
        try {
            isRunning = false;
            
            // Close all client connections
            broadcastMessage("SERVER", "Server is shutting down...", "SYSTEM");
            
            // Close server socket
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            
            // Shutdown thread pool
            threadPool.shutdown();
            
            logMessage("Server stopped successfully");
            
        } catch (IOException e) {
            logError("Error stopping server: " + e.getMessage());
        }
    }
    
    /**
     * Add a new client to the server
     */
    public synchronized boolean addClient(String username, ClientHandler clientHandler) {
        if (connectedClients.containsKey(username)) {
            return false; // Username already exists
        }
        
        connectedClients.put(username, clientHandler);
        
        // Notify all clients about new user
        broadcastMessage("SERVER", username + " joined the chat", "USER_JOIN");
        
        // Send welcome message to new user
        clientHandler.sendMessage("SERVER", "Welcome to the chat, " + username + "!", "WELCOME");
        
        // Send current user list to new user
        sendUserList(clientHandler);
        
        logMessage("User '" + username + "' joined the chat (" + connectedClients.size() + " users online)");
        
        return true;
    }
    
    /**
     * Remove a client from the server
     */
    public synchronized void removeClient(String username) {
        ClientHandler clientHandler = connectedClients.remove(username);
        
        if (clientHandler != null) {
            // Notify all clients about user leaving
            broadcastMessage("SERVER", username + " left the chat", "USER_LEAVE");
            
            logMessage("User '" + username + "' left the chat (" + connectedClients.size() + " users online)");
        }
    }
    
    /**
     * Broadcast message to all connected clients
     */
    public synchronized void broadcastMessage(String sender, String message, String messageType) {
        String timestamp = timeFormat.format(new Date());
        
        // Create formatted message
        String formattedMessage = formatMessage(sender, message, messageType, timestamp);
        
        // Send to all connected clients
        List<String> disconnectedUsers = new ArrayList<>();
        
        for (Map.Entry<String, ClientHandler> entry : connectedClients.entrySet()) {
            try {
                entry.getValue().sendDirectMessage(formattedMessage);
            } catch (Exception e) {
                // Mark for removal if sending fails
                disconnectedUsers.add(entry.getKey());
            }
        }
        
        // Remove disconnected users
        for (String username : disconnectedUsers) {
            removeClient(username);
        }
        
        // Log broadcast
        if (!"SYSTEM".equals(messageType)) {
            logMessage("Broadcast from " + sender + ": " + message);
        }
    }
    
    /**
     * Send private message between users
     */
    public synchronized void sendPrivateMessage(String sender, String recipient, String message) {
        ClientHandler recipientHandler = connectedClients.get(recipient);
        ClientHandler senderHandler = connectedClients.get(sender);
        
        if (recipientHandler != null) {
            String timestamp = timeFormat.format(new Date());
            String privateMessage = formatMessage("[Private] " + sender, message, "PRIVATE", timestamp);
            
            // Send to recipient
            recipientHandler.sendDirectMessage(privateMessage);
            
            // Send confirmation to sender
            if (senderHandler != null) {
                String confirmMessage = formatMessage("SERVER", "Private message sent to " + recipient, "SYSTEM", timestamp);
                senderHandler.sendDirectMessage(confirmMessage);
            }
            
            logMessage("Private message from " + sender + " to " + recipient + ": " + message);
        } else {
            // Notify sender that recipient is not found
            if (senderHandler != null) {
                String errorMessage = formatMessage("SERVER", "User '" + recipient + "' not found", "ERROR", timeFormat.format(new Date()));
                senderHandler.sendDirectMessage(errorMessage);
            }
        }
    }
    
    /**
     * Send list of online users to a specific client
     */
    private void sendUserList(ClientHandler clientHandler) {
        StringBuilder userList = new StringBuilder("Online users: ");
        for (String username : connectedClients.keySet()) {
            userList.append(username).append(", ");
        }
        
        // Remove trailing comma
        if (userList.length() > 14) {
            userList.setLength(userList.length() - 2);
        }
        
        clientHandler.sendMessage("SERVER", userList.toString(), "USER_LIST");
    }
    
    /**
     * Get list of connected users
     */
    public synchronized Set<String> getConnectedUsers() {
        return new HashSet<>(connectedClients.keySet());
    }
    
    /**
     * Get number of connected clients
     */
    public synchronized int getClientCount() {
        return connectedClients.size();
    }
    
    /**
     * Format message with protocol
     */
    private String formatMessage(String sender, String message, String type, String timestamp) {
        return String.format("%s|%s|%s|%s", type, sender, message, timestamp);
    }
    
    /**
     * Log server messages with timestamp
     */
    private void logMessage(String message) {
        System.out.println("[" + timeFormat.format(new Date()) + "] " + message);
    }
    
    /**
     * Log error messages
     */
    private void logError(String error) {
        System.err.println("[" + timeFormat.format(new Date()) + "] ERROR: " + error);
    }
    
    /**
     * Handle server commands (for future expansion)
     */
    public void handleServerCommand(String command) {
        switch (command.toLowerCase()) {
            case "status":
                System.out.println("\n=== Server Status ===");
                System.out.println("Connected clients: " + getClientCount());
                System.out.println("Online users: " + getConnectedUsers());
                System.out.println("Server uptime: " + getUptime());
                System.out.println("==================");
                break;
                
            case "stop":
                System.out.println("Stopping server...");
                stopServer();
                break;
                
            case "help":
                showHelp();
                break;
                
            default:
                System.out.println("Unknown command: " + command);
                showHelp();
        }
    }
    
    /**
     * Show available server commands
     */
    private void showHelp() {
        System.out.println("\n=== Available Commands ===");
        System.out.println("status  - Show server status");
        System.out.println("stop    - Stop the server");
        System.out.println("help    - Show this help");
        System.out.println("========================");
    }
    
    /**
     * Get server uptime (simplified)
     */
    private String getUptime() {
        return "Server running"; // Simplified for this example
    }
    
    /**
     * Main method - Entry point for the server
     */
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        
        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down server...");
            server.stopServer();
        }));
        
        // Start server in a separate thread
        Thread serverThread = new Thread(server::startServer);
        serverThread.start();
        
        // Handle server commands from console
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nType 'help' for available commands or 'stop' to shutdown:");
        
        while (true) {
            try {
                String command = scanner.nextLine().trim();
                if ("stop".equalsIgnoreCase(command)) {
                    server.stopServer();
                    break;
                } else if (!command.isEmpty()) {
                    server.handleServerCommand(command);
                }
            } catch (Exception e) {
                break; // Exit on any input error
            }
        }
        
        scanner.close();
        System.exit(0);
    }
}
