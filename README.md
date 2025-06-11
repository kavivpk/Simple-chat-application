# Simple Chat Application

A real-time chat application built with Java, featuring a client-server architecture using socket programming. Users can join chat rooms, send messages, and communicate in real-time.

## 🚀 Features

- **Real-time messaging** - Instant message delivery using TCP sockets
- **Multiple clients** - Support for multiple users chatting simultaneously
- **User identification** - Each user has a unique username
- **Broadcast messaging** - Messages are broadcast to all connected clients
- **Connection status** - Join/leave notifications for users
- **Simple GUI** - Easy-to-use Swing-based interface
- **Cross-platform** - Runs on Windows, macOS, and Linux

## 📋 Prerequisites

- Java Development Kit (JDK) 8 or higher
- IDE (IntelliJ IDEA, Eclipse, or VS Code recommended)
- Basic understanding of Java and networking concepts

## 🛠️ Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/simple-chat-application.git
   cd simple-chat-application
   ```

2. **Compile the project**
   ```bash
   javac -d bin src/**/*.java
   ```

3. **Alternative: Import into your IDE**
   - Open your preferred Java IDE
   - Import the project as a Java project
   - Build the project

## 🎯 Usage

### Starting the Server

1. **Run the ChatServer class**
   ```bash
   java -cp bin com.chatapp.server.ChatServer
   ```
   
2. **Default configuration:**
   - Server runs on `localhost:12345`
   - Server will display "Server started on port 12345" when ready

### Connecting Clients

1. **Run the ChatClient class**
   ```bash
   java -cp bin com.chatapp.client.ChatClient
   ```

2. **Enter your details:**
   - Server IP (default: localhost)
   - Port (default: 12345) 
   - Username (must be unique)

3. **Start chatting!**
   - Type messages in the input field
   - Press Enter or click Send to broadcast messages
   - See real-time messages from other users

### Example Session

```
Server: User "Alice" joined the chat
Alice: Hello everyone!
Server: User "Bob" joined the chat  
Bob: Hi Alice! How's it going?
Alice: Great! Welcome to the chat Bob
Server: User "Charlie" joined the chat
Charlie: Hey guys! 👋
```

## 📁 Project Structure

```
simple-chat-application/
├── README.md
├── src/
│   ├── com/
│   │   └── chatapp/
│   │       ├── server/
│   │       │   ├── ChatServer.java
│   │       │   └── ClientHandler.java
│   │       ├── client/
│   │       │   ├── ChatClient.java
│   │       │   └── ChatGUI.java
│   │       └── common/
│   │           ├── Message.java
│   │           └── Protocol.java
├── bin/                    # Compiled classes
├── docs/                   # Documentation
├── screenshots/            # Application screenshots
└── tests/                  # Unit tests
```

## 🔧 Technical Details

### Architecture

- **Client-Server Model**: Centralized server handles all client connections
- **Multi-threading**: Each client connection runs on a separate thread
- **Socket Programming**: TCP sockets for reliable message delivery
- **Swing GUI**: User-friendly graphical interface for the client

### Key Components

**Server Side:**
- `ChatServer.java` - Main server class that accepts client connections
- `ClientHandler.java` - Handles individual client communication

**Client Side:**
- `ChatClient.java` - Core client networking logic
- `ChatGUI.java` - Swing-based user interface

**Common:**
- `Message.java` - Message data structure
- `Protocol.java` - Communication protocol constants

### Message Protocol

```java
// Message types
public static final String USER_JOIN = "USER_JOIN";
public static final String USER_LEAVE = "USER_LEAVE"; 
public static final String CHAT_MESSAGE = "CHAT_MESSAGE";
public static final String SERVER_MESSAGE = "SERVER_MESSAGE";

// Message format: TYPE|USERNAME|CONTENT|TIMESTAMP
```

## 🎨 Screenshots

### Server Console
```
Server started on port 12345
Client connected: /127.0.0.1:54321
User 'Alice' joined the chat
User 'Bob' joined the chat
Broadcasting message from Alice: Hello everyone!
```

### Client GUI
![Chat Client Interface](screenshots/chat-client.png)

## 🚀 Advanced Features (Optional Extensions)

- **Private messaging** - Direct messages between users
- **Chat rooms** - Multiple chat rooms/channels
- **File sharing** - Send files between users
- **Emoji support** - Rich text with emojis
- **Message history** - Persistent chat history
- **User authentication** - Login system with passwords
- **Encryption** - Secure message transmission

## 🧪 Testing

### Manual Testing

1. **Start server** and verify it's listening on the correct port
2. **Connect multiple clients** with different usernames
3. **Send messages** from each client and verify broadcast
4. **Disconnect clients** and verify cleanup

### Automated Testing

```bash
# Run unit tests
java -cp bin:lib/junit.jar org.junit.runner.JUnitCore com.chatapp.tests.ChatServerTest
```

## 🐛 Troubleshooting

### Common Issues

**"Connection refused" error:**
- Ensure the server is running before starting clients
- Check if the port (12345) is available
- Verify firewall settings

**"Username already taken":**
- Each client must have a unique username
- Try a different username

**Messages not appearing:**
- Check network connectivity
- Verify server is broadcasting messages
- Restart both server and client

**GUI not responding:**
- Ensure you're running the ChatGUI class, not ChatClient
- Check for Java Swing compatibility issues

## 🤝 Contributing

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/new-feature`)
3. **Commit changes** (`git commit -am 'Add new feature'`)
4. **Push to branch** (`git push origin feature/new-feature`)
5. **Create Pull Request**

### Development Guidelines

- Follow Java naming conventions
- Add comments for complex logic
- Write unit tests for new features
- Update documentation for API changes

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Inspired by classic IRC chat systems
- Built using Java Socket programming concepts
- GUI designed with Java Swing
- Thanks to the Java community for excellent documentation

## 📚 Learning Resources

- [Java Socket Programming Tutorial](https://docs.oracle.com/javase/tutorial/networking/sockets/)
- [Java Swing Documentation](https://docs.oracle.com/javase/tutorial/uiswing/)
- [Multithreading in Java](https://docs.oracle.com/javase/tutorial/essential/concurrency/)

---

⭐ **Star this repository if you found it helpful!**

📝 **Found a bug or have a suggestion? Please open an issue!**
