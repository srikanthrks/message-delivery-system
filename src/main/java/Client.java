/**
 * Created by srikanth.kannan on 11/24/16.
 */
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) {

        Socket clientSocket = null;
        InputStreamReader is = null;
        BufferedReader br = null;
        PrintStream os = null;
        InputStreamReader inputLine = null;
        BufferedReader inputLineBuff = null;

    /*
     * Open a socket on port 2222. Open the input and the output streams.
     */
        try {
            clientSocket = new Socket("localhost", 22221);
            os = new PrintStream(clientSocket.getOutputStream());
            is = new InputStreamReader(clientSocket.getInputStream());
            inputLine = new InputStreamReader(new BufferedInputStream(System.in));
            inputLineBuff = new BufferedReader(inputLine);
            br = new BufferedReader(is);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to host");
        }

    /*
     * If everything has been initialized then we want to write some data to the
     * socket we have opened a connection to on port 2222.
     */
        if (clientSocket != null && os != null && is != null) {
            try {

        /*
         * Keep on reading from/to the socket till we receive the "Ok" from the
         * server, once we received that then we break.
         */
                System.out.println("The client started. Type any text. To quit it type 'Bye' without quotes.");
                String responseLine;
                os.println(inputLineBuff.readLine());
                while ((responseLine = br.readLine()) != null) {
                    System.out.println(responseLine);
                    if ("Bye".equalsIgnoreCase(responseLine)) {
                        break;
                    }
                    os.println(inputLineBuff.readLine());
                }

        /*
         * Close the output stream, close the input stream, close the socket.
         */
                os.close();
                is.close();
                clientSocket.close();
            } catch (UnknownHostException e) {
                System.err.println("Trying to connect to unknown host: " + e);
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    }
}
