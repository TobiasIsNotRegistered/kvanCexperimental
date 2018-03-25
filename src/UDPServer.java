
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class UDPServer {
    private DatagramSocket udpSocket;
    private int port;
    private List<User> users;

    public UDPServer(int port) throws SocketException, IOException {
        this.port = port;
        this.udpSocket = new DatagramSocket(this.port);
    }

    private void listenOnBroadcast() throws Exception {
        System.out.println("-- Running Server at " + InetAddress.getLocalHost() + "--");
        String msg;
        users = new ArrayList<User>();

        while (true) {

            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            // blocks until a packet is received
            udpSocket.receive(packet);
            msg = new String(packet.getData()).trim();

            System.out.println(
                    ">> Message from " + packet.getAddress().getHostAddress() + ": " + msg);

            String[] data = msg.split("#");
            if(data[0].equalsIgnoreCase("TEP")){
                users.add(new User(data[1], packet.getAddress(), Integer.valueOf(data[2]), "connected"));
            }

            String pong = "#TAP#" + data[1] + "#" +  data[2] + "#connected#";
            System.out.println(">> answering user with: " + pong);
            DatagramPacket p = new DatagramPacket(pong.getBytes(), pong.getBytes().length, packet.getAddress(), port);
            this.udpSocket.send(p);
        }
    }

    public static void main(String[] args) throws Exception {
        UDPServer client = new UDPServer(Integer.parseInt(args[0]));
        client.listenOnBroadcast();
    }

    public class User {
        String name;
        InetAddress inet;
        int id;
        String status;

        public User(String a, InetAddress b, int ID, String c){
            name = a;
            inet = b;
            id = ID;
            status = c;
        }
    }

}



