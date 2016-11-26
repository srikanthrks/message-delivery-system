/**
 * Created by srikanth.kannan on 11/24/16.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Pattern;

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
            System.out.println("Starting Server on port# " + portNumber + "\nPress Ctrl + C to quit");
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

                    /* Send foobar to 2 matching the regex // "[Ss]end\s+.+to\s*\d"
                    /* Send foobar to 2 and 3 matching the regex // "[Ss]end\s+.+to\s*\d" */

                    else if(Pattern.compile("[Ss]end\\s+.+to\\s*\\d").matcher(line).find()) {
                        String msg;
                        String afterSend = line.substring(4);//everything after "Send", "foobar to 2" in this case
                        String [] afterSendArray = afterSend.split("to"); // contains "foobar" in [0] and "2" in [1]
                        msg = afterSendArray[0].trim(); // contains the message to be sent, "foobar" in this case

                        ArrayList<Integer> clientList = new ArrayList<Integer>();
                        /* if sending to multiple clients */
                        if(afterSendArray[1].contains("and")){
                            String[] sendersArrays = afterSendArray[1].split("and");
                            for(String str: sendersArrays){
                                try {
                                    clientList.add(Integer.parseInt(str.trim()));
                                } catch (NumberFormatException e) {
                                    System.out.println(" Not a valid client number : " + e);
                                }
                            }
                        }
                        /*  if sending to only one client */
                        else{
                            try {
                                clientList.add(Integer.parseInt(afterSendArray[1].trim()));
                            } catch (NumberFormatException e) {
                                System.out.println(" Not a valid client number : " + e);
                            }
                        }

                        for(int client : clientList){
                            if(threads[client] != null){
                                threads[client].outputStream.println(msg);
                            }
                        }

                    }

                    else if(Pattern.compile("[Ss]end\\s+.+to\\s*\\d+\\s*and\\s*\\d+").matcher(line).find()) {
                        String msg;
                        String afterSend = line.substring(4);//everything after "Send", "foobar to 2" in this case
                        String [] afterSendArray = afterSend.split("to"); // contains "foobar" in [0] and "2 and 3" in [1]
                        msg = afterSendArray[0].trim(); // contains the message to be sent, "foobar" in this case
                        String[] senders = afterSendArray[1].trim().split("and");// afterSendArray[1] contains "2 and 3"
                        for (int i = 0, j=0; i < maxClientsCount && j< senders.length; i++) {
                            if (threads[i] != null && i == Integer.parseInt(senders[j].trim())) {
                                threads[i].outputStream.println(msg);
                            }
                        }

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
                    else{
                        threads[threadNum].outputStream.println("Valid messages are:\n " +
                                "Who am i ?\n Who is here ?\n Send message to 2 \n Send message to 1 and 3");
                    }
                }

            }//while

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

