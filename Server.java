import java.net.*;
import java.io.*;

/* *
 * 
 * Iterative Socket Server w/ Multi-Threaded Client 
 * CNT 4504: Computer Networks and Distributed Processing 
 * Professor John Scott Kelly 
 * Alexander Derby, Afsara Chowdhurry, Emily Ottesen 
 * 10/18/2020
 *
 * */

public class Server implements Runnable {
    private Thread request;
    private ServerSocket server;
    private ClientHandler client;

    public Server(int port) throws IOException {
        server = new ServerSocket(port);
        System.out.println("[Server] Server has been created.");
    }

    @Override
    public void run() {
        while (request != null) {
            try {
                System.out.println("[Server] Waiting for connection.");
                addRequest(server.accept());
            } catch (IOException ioException) { ioException.printStackTrace(); }
        }
    }

    private void addRequest(Socket socket) throws IOException {
        client = new ClientHandler(this, socket);
        System.out.println("[Server] Client accepted.");

        client.start();
    }

    public void start() {
        if (request == null) {
            request = new Thread(this);
            request.start();
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter port: ");
        int port = Integer.parseInt(reader.readLine());
        
        Server server = new Server(port);
        server.start();
    }
}

class ClientHandler extends Thread {

    Server server;
    DataInputStream input;
    DataOutputStream output;
    Socket socket;

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            String request = input.readUTF();
            System.out.println(request);
            switch (request.toLowerCase()) {
                case "datetime":
                    executeRequest("datetime");
                    break;
                case "uptime":
                    executeRequest("uptime");
                    break;
                case "memoryuse":
                    executeRequest("free");
                    break;
                case "netstat":
                    executeRequest("netstat");
                    break;
                case "currentusers":
                    executeRequest("who");
                    break;
                case "runningprocesses":
                    executeRequest("ps -e");
                    break;
                default:
                    output.writeUTF("Unknown request.");
                    break;
                }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                output.close();
                input.close();
                socket.close();
            } catch (IOException ioException) { ioException.printStackTrace(); }
        }
    }

    private void executeRequest(String request) throws IOException {
 
		Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(request);
        StringBuilder response = new StringBuilder();
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream())); 
        String line=null;

        while ((line = input.readLine()) != null) {
			System.out.println(line);
            response.append(line + "\n");
        }
        respondToClient(response);
    }

    private void respondToClient(StringBuilder response) {
        try {
            output.writeUTF(response.toString());
            output.flush();
        } catch (IOException ioException) { ioException.printStackTrace(); }
    }
}
