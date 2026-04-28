package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

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
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("Successfully connected to TCP server.");
            String line;

            while ((line = in.readLine()) != null) {
                System.out.println("Client " + remote_ip + " message: '" + line + "'");
                if (line.equals("exit")) {
                    break;
                }
            }

            in.close();
            out.close();
            socket.close();
            System.out.println("Client " + remote_ip + " disconnected.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static volatile boolean running = true;
    static ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(5555);
            System.out.println("Server started.");

            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    new Thread(new Server(socket)).start();
                } catch (IOException e) {
                    if (!running) {
                        System.out.println("Server stopped.");
                        break;
                    }
                    throw new RuntimeException(e);
                }
            }

            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}