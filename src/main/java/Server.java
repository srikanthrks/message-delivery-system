/**
 * Created by srikanth.kannan on 11/24/16.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String args[]) {

        ServerSocket echoServer = null;
        String line;
        InputStreamReader is;
        BufferedReader br;
        PrintStream os;
        Socket clientSocket = null;

    /*
     * Open a server socket on port 2222. Note that we can't choose a port less
     * than 1023 if we are not privileged users (root).
     */
        try {
            echoServer = new ServerSocket(22221);
        } catch (IOException e) {
            System.out.println(e);
        }

    /*
     * Create a socket object from the ServerSocket to listen to and accept
     * connections. Open input and output streams.
     */
        System.out.println("The server started. To stop it press <CTRL><C>.");
        try {
            clientSocket = echoServer.accept();
            int clientCount = 0;
            is = new InputStreamReader(clientSocket.getInputStream());
            br = new BufferedReader(is);
            os = new PrintStream(clientSocket.getOutputStream());

      /* As long as we receive data, echo that data back to the client. */
            while (true) {
                line = br.readLine();
                if(line == null) {
                    System.out.println(" Client closed");
                    break;
                }
                else if(line.equalsIgnoreCase("who am i ?")){
                    System.out.println(" From client: " + line);
                    os.println(clientCount++);
                }
                else {
                    System.out.println(" From client: " + line);
                    os.println("From server: " + line);
                }

            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}