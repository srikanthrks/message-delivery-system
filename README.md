A message delivery system with a server/hub which relays messages to the clients/receivers.
This is a maven project, so this will include all the dependencies when running 'mvn clean install' command

================
Pre-requisites:
================
1. maven, greater than apache-maven-3.2.5
2. Java JDK 1.8 or greater installed

NOTE: The source code was compiled on Java 1.8 and needs a version greater than Java 1.8

=========================
Steps to run the program:
=========================
1. after downloading the project, open a terminal and go to the prohect root directory messsage-delivery-system
2. run 'mvn clean install' to download all the dependencies
3. compile the Server.java and Client.java in the directory src/main/java
  a. javac Server.java
  b. javac Client.java
4. Run the Server by typing 'java Server <port-number>', e.g, java Server 2223 (if no <port-number> is specified it runs on 22221)
5. Run the Client by typing 'java Client <host-name> <port-number>', e.g, java Client localhost 2223 (if no <port-number> is specified it runs on 22221)
NOTE: If you are specifying your own <port-number> then use the same <port-number> in both Client and Server and > 1023. 
