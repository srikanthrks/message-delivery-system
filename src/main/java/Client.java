/**
 * Created by srikanth.kannan on 11/24/16.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

    // The client socket
    private static Socket clientSocket = null;
    // The output stream
    private static PrintStream outputStream = null;
    // The input stream
    private static InputStreamReader inputStream = null;
    //Buffered Reader for inputStream
    private static BufferedReader inputStreamBuffer = null;
    //Buffered Reader for getting input from the console
    private static BufferedReader consoleInputBuffer = null;
    private static boolean closed = false;

    public static void main(String[] args) {

        // The default port.
        int portNumber = 22221;
        // The default host.
        String host = "localhost";

        if (args.length < 2) {
            System.out
                    .println("Usage: java Client <host> <portNumber>\n"
                            + "Now using host=" + host + ", portNumber=" + portNumber);
        } else {
            host = args[0];
            portNumber = Integer.valueOf(args[1]).intValue();
        }

    /*
     * Open a socket on a given host and port. Open input and output streams.
     */
        try {
            clientSocket = new Socket(host, portNumber);
            consoleInputBuffer = new BufferedReader(new InputStreamReader(System.in));
            outputStream = new PrintStream(clientSocket.getOutputStream());
            inputStream = new InputStreamReader(clientSocket.getInputStream());
            inputStreamBuffer = new BufferedReader(inputStream);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host " + host);
        }

    /*
     * If everything has been initialized then we want to write some data to the
     * socket we have opened a connection to on the port portNumber.
     */
        if (clientSocket != null && outputStream != null && inputStreamBuffer != null) {
            try {

        /* Create a thread to read from the server. */
                new Thread(new Client()).start();
                while (!closed) {
                    String consoleInput = consoleInputBuffer.readLine().trim();
                    if("Bye".equalsIgnoreCase(consoleInput)){
                        outputStream.println(consoleInput);
                        break;
                    }
                    outputStream.println(consoleInput);
                }
        /*
         * Close the output stream, close the input stream, close the socket.
         */     consoleInputBuffer.close();
                outputStream.close();
                inputStream.close();
                inputStreamBuffer.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    }

    /*
     * Create a thread to read from the server. (non-Javadoc)
     */
    public void run() {
    /*
     * Keep reading from the socket till we receive "Bye" from the
     * server. Once we receive that, break out of the loop
     */
        String responseLine;
        try {
            while ((responseLine = inputStreamBuffer.readLine()) != null) {
                System.out.println(responseLine);
                /* if the server sends Bye, then break out of the loop */
                if ("Bye".equalsIgnoreCase(responseLine))
                    break;
            }
            closed = true;
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }
}
