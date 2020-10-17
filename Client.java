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
    public static void main(String[] args) throws IOException, InterruptedException {
        
        Scanner input = new Scanner(System.in);
        ClientConnection[] clients = new ClientConnection[25];

        System.out.print("Enter address: ");
        String address = input.nextLine();
        System.out.print("Enter port number: ");
        int port = input.nextInt();

        Socket socket = new Socket(address, port);

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
            System.out.println("\nNumber of clients to spawn: ");
            int clientsToCreate = input.nextInt();

            for (int i = 0; i < clientsToCreate; i++) {
                ClientConnection client = new ClientConnection(socket, userInput);
                clients[i] = client;
            }

            for (ClientConnection client : clients) {
                client.start();
            }

            for(ClientConnection client : clients) {
                client.join();
            }
        }

        //Close the Scanner
        input.close();
    }
}

class ClientConnection extends Thread {

    private String response;
    private Socket socket;
    private BufferedReader input;
    private DataOutputStream output;

    public ClientConnection(Socket socket, String response) throws IOException {
        this.socket = socket;
        this.response = response;
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    @Override
    public void run() {
        String serverResponse;
        boolean retrievedResonse = false;
        Instant start = Instant.now();

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
            while (!retrievedResonse) {
                while ((serverResponse = input.readLine()) != null) {
                    System.out.println(serverResponse);
                    retrievedResonse = true;
                }
            }   
            Instant finish = Instant.now();
            System.out.println(Duration.between(start, finish).toMillis());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        // Kill the connection
        try { socket.close(); } 
        catch (IOException ioException) { ioException.printStackTrace(); }
    }


}