package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Client1 {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5555);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            System.out.println(in.readLine());
            while (true) {
                System.out.print("Username: ");
                String username = scanner.nextLine();

                if (username.isEmpty() || username.equals("exit")) {
                    socket.close();
                    return;
                }

                if (username.length() < 3) {
                    System.out.println("Username too short.\n");
                    continue;
                }

                out.println(username);

                if (in.readLine().equals("GOOD")) {
                    break;
                } else {
                    System.out.println("Username already exists.\n");
                }
            }

            System.out.println("\nYou can start chatting (user:message).");

            Thread sender = new Thread(() -> {
                try {
                    while (scanner.hasNextLine()) {
                        String msg = scanner.nextLine();
                        out.println(msg);

                        if (msg.equalsIgnoreCase("exit")) {
                            socket.close();
                        }
                    }
                } catch (IOException ignored) {}
            });

            Thread receiver = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException ignored) {}
            });

            sender.start();
            receiver.start();
            sender.join();
            receiver.join();

            socket.close();
            System.out.println("Disconnected from the server.");
        } catch (SocketException e) {
            System.out.println("Socket closed.");
        } catch (IOException | InterruptedException ignored) {}
    }
}