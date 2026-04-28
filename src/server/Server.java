package server;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static final ConcurrentHashMap<String, PrintWriter> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5555)) {
            System.out.println("Server started on port 5555.");

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClient(socket)).start();
            }

        } catch (IOException ignored) {
            System.out.println("Server haven't started on port 5555.");
        }
    }

    private static void handleClient(Socket socket) {
        String username = null;

        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("Connected to server.");

            while (true) {
                username = in.readLine().toLowerCase();

                if (username.trim().isEmpty() || username.equalsIgnoreCase("exit")) {
                    socket.close();
                    return;
                }

                if (clients.putIfAbsent(username, out) == null) {
                    out.println("GOOD");
                    break;
                } else {
                    out.println("BAD");
                }
            }

            System.out.println(username + " joined");

            String line;
            while ((line = in.readLine()) != null) {

                if (line.equalsIgnoreCase("exit")) {
                    break;
                }

                String[] parts = line.split(":", 2);

                if (parts.length == 2) {
                    String recipient = parts[0].trim().toLowerCase();
                    String message = parts[1].trim();

                    PrintWriter receiver = clients.get(recipient);

                    if (receiver != null) {
                        if (recipient.equals(username)) {
                            out.println("You can't chat with yourself.");
                        } else {
                            receiver.println(username + ": " + message);
                        }
                    } else {
                        out.println("User not found.");
                    }
                } else {
                    out.println("\nYou can start chatting (user:message).");
                }
            }

        } catch (IOException ignored) {
            System.out.println("Client disconnected.");

        } finally {
            try {
                if (username != null) {
                    clients.remove(username);
                    System.out.println(username + " left");
                }

                socket.close();
            } catch (IOException ignored) {}
        }
    }
}