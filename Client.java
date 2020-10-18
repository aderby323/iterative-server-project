import java.net.*;
import java.time.Duration;
import java.time.Instant;
import java.io.*;
import java.util.Scanner;

/* *
 * 
 * Iterative Socket Server w/ Multi-Threaded Client 
 * CNT 4504: Computer Networks and Distributed Processing 
 * Professor John Scott Kelly 
 * Alexander Derby, Afsara Chowdhurry, Emily Ottesen 
 * 10/16/2020
 *
 * */

public class Client {

    // Get IP address, port number, and user response
    public static void main(String[] args) throws IOException {
        
        Scanner input = new Scanner(System.in);

        // Get desired address and port number
        System.out.print("Enter address: ");
        String address = input.nextLine();
        System.out.print("Enter port number: ");
        int port = input.nextInt();

        while (true) {

            System.out.println("================================================================================");
            System.out.println("|| DateTime - Get date and time on the server.");
            System.out.println("|| Uptime - Amount of time server has been running since last boot-up");
            System.out.println("|| Memory Use - Current memory usage on the server.");
            System.out.println("|| Netstat - Lists network connections on the server.");
            System.out.println("|| Current Users - Lists users currently connected to the server.");
            System.out.println("|| Running Processes - Lists programs running on the server.");
            System.out.println("|| Quit - Exits the program.");
            System.out.println("================================================================================");
            
            System.out.print("\nEnter a command: ");
            String userInput = input.next();
            if (userInput.equalsIgnoreCase("quit")) { break; }
            System.out.println("\nNumber of clients to spawn (1, 5, 10, 25): ");
            int clientsToCreate = input.nextInt();
            if (clientsToCreate > 1 || (clientsToCreate >= 25 && clientsToCreate % 5 == 0)) {
                System.out.println("You may only create 1, 5, 10, or 25 clients.");
                continue;
            }

            // Create desired number of clients and add them to an array
            for (int i = 0; i < clientsToCreate; i++) {
                new ClientConnection(address, port, userInput, i+1).start();
            }
        }

        //Close the Scanner
        input.close();
    }
}

class ClientConnection extends Thread {

    private int id;
    private String response;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public ClientConnection(String address, int port, String response, int id) throws IOException {
        this.id = id;
        this.socket = new Socket(address, port);
        this.response = response;
        input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    @Override
    public void run() {
        Instant start = Instant.now();
        Instant finish;

        // Send the user's response to Server
        try {
            output.writeUTF(response);
            output.flush();

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        // Wait for the server to send a response, print each line,
        // and record the Turn-around time.
        try {
            while (true) {
                if (input.readUTF() != null) {
                    try {
                        finish = Instant.now();
                        System.out.println(input.readUTF());
                    } catch (Exception eof) {
                        eof.printStackTrace();
                    }
                    break;
                }
            }
            System.out.println("Turn-around time for Client " + id + ": " + Duration.between(start, finish).toMillis() + " ms.");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        // Kill the connection
        try { socket.close(); } 
        catch (IOException ioException) { ioException.printStackTrace(); }
    }


}