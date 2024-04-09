# Java Tiles: Online Multiplayer Okey Game

Java Tiles is an ongoing project aimed at developing an online multiplayer version of the classic game Okey. Players can currently join the server as clients and engage in chat interactions. Developed by vonUken, this project promises to deliver an immersive gaming experience once completed.

## Introduction

Welcome to Java Tiles! This project aims to bring the beloved game of Okey to your fingertips, enabling players to enjoy thrilling matches with friends and family from anywhere in the world. While the game is still in development, the current version allows players to connect to the server as clients and chat with each other.

## Installation and Usage Instructions

### Building the JAR File:

1. **Java Installation:**
   - Ensure you have Java installed on your system. If not, download and install Java from the [official website](https://www.java.com).

2. **Building the JAR File:**
   - Open a terminal or command prompt.
   - Navigate to the root directory of the Java Tiles project.
   - Run the following command to build the JAR file (for Windows):
     ```
     ./gradlew jar
     ```

### Running as a Client:

1. **Starting a Client:**
   - After building the JAR file, run the following command to start a client and connect to a server:
     ```
     java -jar .\build\libs\JavaTiles-0.0.1-ALPHA.jar client <ipaddress>:<port> [<username>]
     ```
   - Replace `<ipaddress>` and `<port>` with the IP address and port number of the server.
   - `<username>` is an optional parameter to specify your username.

### Running as a Server:

1. **Starting a Server:**
   - After building the JAR file, run the following command to start the server:
     ```
     java -jar .\build\libs\JavaTiles-0.0.1-ALPHA.jar server <port>
     ```
   - Replace `<port>` with the desired port number for the server.

## Known Issues

1. **Disconnection on Some Commands:**
   - Some commands may lead to disconnection from the server. This issue is being addressed and will be resolved in future updates.

## Contributing

We welcome contributions from the community to improve Java Tiles. Feel free to submit bug reports, feature requests, or pull requests on our GitHub repository. Your feedback and contributions are greatly appreciated!

## Contact

For any inquiries or support, please contact us at:
- [robin.goekcen@stud.unibas.ch](mailto:robin.goekcen@stud.unibas.ch)
- [i.uka@stud.unibas.ch](mailto:i.uka@stud.unibas.ch)
- [pascal.vonfellenber@stud.unibas.ch](mailto:pascal.vonfellenber@stud.unibas.ch)
- [boran.goekcen@stud.unibas.ch](mailto:boran.goekcen@stud.unibas.ch)
