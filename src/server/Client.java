package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5555);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            System.out.println(in.readLine());

            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                out.println(msg);

                if (msg.equalsIgnoreCase("exit")) {
                    break;
                }
            }

            in.close();
            out.close();
            scanner.close();
            socket.close();
            System.out.println("Disconnected from the server.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
