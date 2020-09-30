import java.net.*;
import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter port: ");
        int port = Integer.parseInt(reader.readLine());

        Socket socket = new Socket("139.62.210.153", port);

        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        String serverRespone = input.readLine();
        System.out.println(serverRespone);

        socket.close();
    }
}