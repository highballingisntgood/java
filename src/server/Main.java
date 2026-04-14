package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
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