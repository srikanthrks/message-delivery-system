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

    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    private static Socket clientSocket = null;

    // This chat server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 10;
    // Thread array, one thread for each of the client connections
    private static final ClientThread[] threads = new ClientThread[maxClientsCount];
    //thread count
    private static int threadNum;

    public static void main(String args[]) {

        // The default port number.
        int portNumber = 22221;
        if (args.length < 1) {
            System.out
                    .println("Usage: java Server <portNumber>\n"
                            + "Now using port number=" + portNumber);
        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
            System.out.println("Starting Server on port# " + portNumber);
        }

    /*
     * Open a server socket on the portNumber (default 2222). Note that we can
     * not choose a port less than 1023 if we are not privileged users (root).
     */
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println("Unable to create the Server Socket: " + e);
        }

    /*
     * Create a client socket for each connection and pass it to a new client thread
     */
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                for (threadNum = 0; threadNum < maxClientsCount; threadNum++) {
                    if (threads[threadNum] == null) {
                        (threads[threadNum] = new ClientThread(clientSocket, threads, threadNum)).start();
                        break;
                    }
                }
                if (threadNum == maxClientsCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server too busy. Try later.");
                    os.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}

/*
 * The chat client thread. This client thread opens the input and the output
 * streams for a particular client. When a client leaves the chat room this thread terminates it.
 */
class ClientThread extends Thread {

    private InputStreamReader inputStream = null;
    private BufferedReader bufferRead = null;
    private PrintStream outputStream = null;
    private Socket clientSocket = null;
    private ClientThread[] threads;
    private int maxClientsCount;
    private int threadNum;

    public ClientThread(Socket clientSocket, ClientThread[] threads, int threadNum) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
        this.threadNum = threadNum;
    }

    public void run() {
        int maxClientsCount = this.maxClientsCount;
        ClientThread[] threads = this.threads;

        try {
      /*
       * Create input and output streams for this client.
       */
            inputStream = new InputStreamReader(clientSocket.getInputStream());
            bufferRead = new BufferedReader(inputStream);
            outputStream = new PrintStream(clientSocket.getOutputStream());
            //outputStream.println("Enter your name");

            while (true) {
                String line = bufferRead.readLine();
                //todo: move line != null above
                if(line !=null){
                    if ("quit".equalsIgnoreCase(line)) {
                        break;
                    }
                    /**for (int i = 0; i < maxClientsCount; i++) {
                     if (threads[i] != null && i == threadNum) {
                     if("Who Am I ?".equalsIgnoreCase(line)){
                     threads[i].outputStream.println("From <" + threadNum + ">: " + line);
                     }
                     }//if
                     }//for**/

                    if("who am I ?".equalsIgnoreCase(line)) {
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null && i == threadNum) {
                                threads[i].outputStream.println(threadNum);
                            }
                        }
                    }

                    else if("Who is here ?".equalsIgnoreCase(line)) {
                        StringBuffer stringBuffer = new StringBuffer();
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null && threads[i] != this) {
                                stringBuffer.append(i + " ");
                            }
                        }
                        if("".equals(stringBuffer.toString().trim())){
                            stringBuffer.append("No one");
                        }
                        threads[threadNum].outputStream.println(stringBuffer.toString());
                    }

                    else if(line.startsWith("Send ")) {
                        String[] split = line.split("\\s");
                        String msg;
                        if(split.length > 0){
                            msg = split[1];
                        }
                        //Pattern.matches("\\\\to\d+", split);
                    }
                    /* If the client sends Bye, send Bye */
                    else if("Bye".equalsIgnoreCase(line)) {
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null && i == threadNum) {
                                threads[i].outputStream.println("Bye");
                                threads[i] = null;
                            }
                        }
                    }
                }

            }//while

            /**String name = bufferRead.readLine().trim();
             outputStream.println("Hello " + name  + ". Welcome to our chat room.\nTo leave enter quit in a new line");

             for (int i = 0; i < maxClientsCount; i++) {
             if (threads[i] != null && threads[i] != this) {
             threads[i].outputStream.println("=== A new user " + name + " entered the chat room !!! ===");
             }
             }
             while (true) {
             String line = bufferRead.readLine();
             if (line !=null  && line.startsWith("quit")) {
             break;
             }
             for (int i = 0; i < maxClientsCount; i++) {
             if (threads[i] != null) {
             threads[i].outputStream.println("From <" + clientNum + ">: " + line);
             }
             }
             }

             for (int i = 0; i < maxClientsCount; i++) {
             if (threads[i] != null && threads[i] != this) {
             threads[i].outputStream.println("=== The user " + clientNum + " is leaving the chat room !!! ===");
             }
             }
             outputStream.println("=== Bye " + clientNum + " ===");
             **/

      /*
       * Clean up. Set the current thread variable to null so that a new client
       * could be accepted by the server.
       */
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] == this) {
                    threads[i] = null;
                }
            }

      /*
       * Close the output stream, close the input stream, close the socket.
       */
            inputStream.close();
            outputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Exception: " + e);
        }
    }
}

