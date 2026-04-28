package server;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final int PORT = 12345;
    private static final ConcurrentHashMap<String, PrintWriter> clientMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("Relay Server started on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();

                String clientIP = clientSocket.getInetAddress().getHostAddress();
                System.out.println("New connection from: " + clientIP);

                new Thread(new ClientHandler(clientSocket, clientIP)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private record ClientHandler(Socket socket, String clientIP) implements Runnable {

        @Override
            public void run() {
                try (
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
                ) {
                    clientMap.put(clientIP, out);

                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        if (inputLine.contains(":")) {
                            String[] parts = inputLine.split(":", 2);
                            String targetIP = parts[0];
                            String message = parts[1];

                            PrintWriter targetWriter = clientMap.get(targetIP);
                            if (targetWriter != null) {
                                System.out.println("Relaying message from " + clientIP + " to " + targetIP);
                                targetWriter.println(message);
                            } else {
                                out.println("System: Target " + targetIP + " not found.");
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Client " + clientIP + " disconnected.");
                } finally {
                    clientMap.remove(clientIP);
                    try {
                        socket.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
}