import java.net.*;
import java.io.*;
import java.util.Scanner;

/* *
 * 
 * Iterative Socket Server w/ Multi-Threaded Client 
 * CNT 4504: Computer Networks and Distributed Processing 
 * Professor John Scott Kelly 
 * Alexander Derby, Afsara Chowdhurry, Emily Ottesen 
 * 10/13/2020
 *
 * */

public class Client extends Thread {

    String address;
    String response;
    Socket socket;
    BufferedReader input;
    DataOutputStream output;
    int port;

    // Creates a new Client that connects to the Server, sends a response, and gets a response
    public Client(String response, String address, int port) throws IOException {
        this.address = address;
        this.port = port;
        this.response = response;
        socket = new Socket(address, port);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    @Override
    public void run() {
        boolean isFinished = false;
        String serverResponse;

        // Send the user's response to Server
        try {
            output.writeUTF(response);
            output.flush();

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        // Wait for the server to send a response and print each line
        while (!isFinished) {
            try {
                while ((serverResponse = input.readLine()) != null) {
                    System.out.println(serverResponse);
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            isFinished = true;
        }

        // Kill the connection
        try { socket.close(); } 
        catch (IOException ioException) { ioException.printStackTrace(); }
    }

    // Get IP address, port number, and user response
    public static void main(String[] args) throws IOException, InterruptedException {
        
        Scanner input = new Scanner(System.in);
        Client[] clients = new Client[25];

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
            String userInput = input.nextLine();
            if (userInput.equalsIgnoreCase("quit")) { break; }
            System.out.println("Number of clients to spawn: ");
            int clientsToCreate = input.nextInt();

            // Create n Clients that user requests
            for (int i = 0; i < clientsToCreate; i++) {
                Client spawnedClient = new Client(userInput, address, port);
                clients[i] = spawnedClient;
            }

            // Start each thread
            for (Client client : clients){
                client.start();
            }

            // Wait for each thread to die
            for (Client client : clients) {
                client.join();
            }

            // Remove existing clients from array
            for (int i = 0; i < clientsToCreate; i++) {
                clients[i] = null;
            }
        }

        //Close the Scanner
        input.close();
    }
}