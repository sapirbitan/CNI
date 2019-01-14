
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
public class MaliciousServer {
    private PrintStream _output;
    private DataInputStream _input;
    private Socket socket;
    public MaliciousServer(){}//constructor
    //add A/B's Ip and send it to B/A
    public void sendMsg(String[] splitted_msg, String data)throws IOException{
        String msgMA = splitted_msg[0]+"&" + data;
        _output.println(msgMA);
        System.out.println("Message sent: "+msgMA + " data: " +data);
    }
    public static void main (String[] args) throws IOException {
        MaliciousServer server = new MaliciousServer();
        //getting a message from A and splitting it into two halves - address and message

        //create a server in order to receive messages from both A and B
        ServerSocket server_socket = new ServerSocket(7000);
        server.socket = server_socket.accept();//listening
        server._input = new DataInputStream(server.socket.getInputStream());
        String A_address =server.socket.getInetAddress().toString();
        System.out.println("Connection with " + A_address + " established");
        String dataAB, msgAB;
        msgAB=server._input.readLine(); //get message from A to B
        System.out.println(msgAB);
        String[] splitted_msg = msgAB.split("&",2);
        dataAB=splitted_msg[1];
        System.out.println("Message received: "+ msgAB + " data: " +dataAB);
        //close A's connection to M
        server.socket.close();
        server_socket.close();
        //changing a random letter
        char[] data = dataAB.toCharArray();
        //getting a random location
        Random rand = new Random();
        int _rand = rand.nextInt(dataAB.length()-1) + 1;
        //changing the letter in the random location
        if(data[_rand]!= 'a')
            data[_rand] = 'a';
        else
            data[_rand] = 'b';
        String dataAB_changed = String.valueOf(data);
        System.out.println("Message received; data: "+ dataAB + " changed data: " +dataAB_changed);
        //sending the message to B
        server.socket = new Socket("localhost", 7002);	//creating a socket in orser to connect to B
        server._output = new PrintStream(server.socket.getOutputStream());
        //create message to B "ip&data" and send to B
        server.sendMsg(splitted_msg, dataAB_changed);

        //getting a reply from B
        server._input = new DataInputStream(server.socket.getInputStream());
        String dataBA, msgBM;
        msgBM =server._input.readLine();
        String[] splitted_msg1=msgBM.split("&",2);
        dataBA = splitted_msg1[1];
        System.out.println("Message received: "+msgBM + " data: " + dataBA);
        server.socket.close(); //close B's connection to M

        //sending the reply to A
        server.socket = new Socket(splitted_msg1[0], 7001);
        server._output = new PrintStream(server.socket.getOutputStream());
        server.sendMsg(splitted_msg1, dataBA);

        //close A's connection to M
        server.socket.close();
        server_socket.close();
    }

}
