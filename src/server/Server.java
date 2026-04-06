package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Server implements Runnable {
    private final Socket socket;

    public Server(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            String remote_ip = socket.getInetAddress().getHostAddress();
            int remote_port = socket.getPort();
            String local_ip = socket.getLocalAddress().getHostAddress();
            int local_port = socket.getLocalPort();
            System.out.println("Client connected. Source IP: " + remote_ip + " | Destination IP: " + local_ip + " | Source Port: " + remote_port + " | Destination Port: " + local_port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Successfully connected to TCP server.");
            Scanner in = new Scanner(socket.getInputStream());
            while (in.hasNextLine()) {
                String line = in.nextLine();
                System.out.println("Client " + remote_ip + " message: '" + line + "'");
                if (line.equals("exit")) {
                    socket.close();
                    break;
                }
            }
            System.out.println("Client " + remote_ip + " disconnected.");
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}