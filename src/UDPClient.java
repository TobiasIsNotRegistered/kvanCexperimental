
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient {
    private DatagramSocket udpSocket;
    private InetAddress serverAddress;
    private int port;
    private Scanner scanner;
    boolean shutdown = false;

    private UDPClient(String destinationAddr, int port) throws IOException {
        this.serverAddress = InetAddress.getByName(destinationAddr);
        this.port = port;
        udpSocket = new DatagramSocket(this.port);
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) throws NumberFormatException, IOException {
        UDPClient sender = new UDPClient(args[0], Integer.parseInt(args[1]));
        System.out.println("-- Running UDP Client at " + InetAddress.getLocalHost() + " --");
        sender.sendBroadcastPing();
        //sender.start();
    }
    private void start() throws IOException {
        String in;
        while (!shutdown) {

            System.out.println(">> Write msg, press enter to send. Send 'shutdown' to close client. ");
            in = scanner.nextLine();

            if (in.equalsIgnoreCase("shutdown")){
                udpSocket.close();
                scanner.close();
                System.out.println(">> Socket + Scanner closed.");
                shutdown = true;
            }else {
                System.out.println(">> trying to send msg: " + in + " on Adress: " + serverAddress + " on Port: " + port);
                DatagramPacket p = new DatagramPacket(in.getBytes(), in.getBytes().length, serverAddress, port);
                this.udpSocket.send(p);
            }
        }
    }

    private void sendBroadcastPing() throws IOException{
        String username;
        String randomID;
        String ping; //this client wants to talk
        String pong; //answer from server

        System.out.println(">> Enter username (6chars): ");
        username = scanner.nextLine().trim();
        randomID = "999";
        System.out.println(">> Username set to: " + username + ". Your ID is: " + randomID);

        ping = "#TEP#" + username + "#" + randomID + "#";
        DatagramPacket p = new DatagramPacket(ping.getBytes(), ping.getBytes().length, serverAddress, port);
        this.udpSocket.send(p);
        System.out.println(">> Waiting for answer from server...");

        //wait for an answer
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        // blocks until a packet is received
        udpSocket.receive(packet);
        pong = new String(packet.getData()).trim();

        String[] data = pong.split("#");
        String status = data[3];
        System.out.println(">> received status from server: " + status);
    }
}