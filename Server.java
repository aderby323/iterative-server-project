import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class Server {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter port: ");
        int port = Integer.parseInt(reader.readLine());
        
        ServerSocket server = new ServerSocket(port);
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

        System.out.println("[Server] Waiting for a connection.");
        Socket socket = server.accept();
        System.out.println("[Server] Connected to client.");

        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        try {
            while (true) {
                String request = input.readLine();
                if (request.equalsIgnoreCase("exit")) { 
                    output.println("exit");
                    break; 
                }
                switch (request.toLowerCase()) {
                    case "datetime":
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
                        output.println(dateFormat.format(new Date()));
                        break;
                    case "uptime":
                        long uptime = runtimeMXBean.getUptime();
                        output.println(uptime);
                        break;
                    case "memoryuse":
                        output.println((double) Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
                        break;
                    case "netstat":
                        Process process = Runtime.getRuntime().exec("netstat");
                        output.println();
                        break;
                    case "currentusers":
                        output.println();
                        break;
                    case "runningprocesses":
                        output.println();
                        break;
                    default:
                        output.println("Unknown request.");
                        break;
                }
            }
        } finally {
            System.out.println("[Server] Closing connection.");
            socket.close();
            server.close();
        }
    }
}
