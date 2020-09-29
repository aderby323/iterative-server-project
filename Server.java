import java.net.*;
import java.util.Date;
import java.io.*;

public class Server {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter port: ");
        int port = Integer.parseInt(reader.readLine());
        
        ServerSocket server = new ServerSocket(port);

        System.out.println("[Server] Waiting for a connection.");
        Socket socket = server.accept();
        System.out.println("[Server] Connected to client.");

        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        String date = new Date().toString();
        System.out.println("[Server] Sending date: " + date);
        output.println(date);

        System.out.println("[Server] Closing connection.");
        socket.close();
        server.close();
    }
}
