
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ImprovedHost {
    public void startWorking(String a_or_b, String partner_ip_addr, String M_ip_addr)throws IOException{
        if(a_or_b.equals("A")) {//host A
            sendMessage(a_or_b, partner_ip_addr, M_ip_addr);
        }
        else if(a_or_b.equals("B")) {//host B
            getMessage(7002, a_or_b,partner_ip_addr);
        }
        else{
            System.out.println("Error!");
        }
    }
    //getting message from server M , and decrypting it
    //if the host is B this function also sends a message to A via M
    public void getMessage(int IP, String a_or_b, String partner_ip_addr) throws IOException{
        ServerSocket server = new ServerSocket(IP);
        try {
            //listening - waiting for a connection request from M
            Socket socket = server.accept();
            System.out.println("connection established");
            DataInputStream _input = new DataInputStream(socket.getInputStream());
            PrintStream _output = new PrintStream(socket.getOutputStream());
            //getting the message from M and extracting the data
            String msgMB =_input.readLine();//or msgMA if we're getting the message for A
            String splitted_msg[] = new String[2];
            splitted_msg=msgMB.split("&",2);
            String dataBA = splitted_msg[1];
            String data[] = new String[3];
            data[0]=dataBA.substring(0,dataBA.length()/3);
            data[1]=dataBA.substring(dataBA.length()/3,2*dataBA.length()/3);
            data[2]=dataBA.substring(2*dataBA.length()/3,dataBA.length());
            if(a_or_b.equals("B")) {
                if (data[0].equals(data[1]))
                    dataBA = data[0];
                else if (data[1].equals(data[2]))
                    dataBA = data[1];
                else
                    dataBA = data[2];
            }
            System.out.println("Message received: "+ msgMB + " ,data: " +dataBA);
            if(a_or_b.equals("B")){//send message back to A
                _input = new DataInputStream(System.in);
                System.out.println("What is your reply?");
                dataBA = _input.readLine();
                String msgBM = partner_ip_addr +"&" +dataBA;
                _output.println(msgBM);//sending message to M after adding A's Ip to it
                System.out.println("Message sent: " +msgBM+ ", data: " + dataBA);
            }
            try {//close socket and derver
                socket.close();
                server.close();					}
            catch (IOException e) {
                System.err.println(e);
            }
        }

        catch (IOException e) {
            System.err.println(e);
        }


    }
    // sending message to B via server M - using repetition in order to bypass the malicious server
    public void sendMessage(String a_or_b, String partner_ip_addr, String M_ip_addr)throws IOException{
        String msgAM;
        String dataAB;
        Socket socket;
        DataInputStream _input;
        PrintStream _output;
        Scanner user_input;
        try {
            socket = new Socket(M_ip_addr,7000);
            System.out.println("Connection to " + M_ip_addr + " established");
            _input = new DataInputStream(socket.getInputStream());
            _output = new PrintStream(socket.getOutputStream());
            System.out.println("What message would you like to send?");
            //getting message from user
            user_input = new Scanner(System.in);
            String input = user_input.nextLine();
            dataAB=input;
            dataAB = dataAB+dataAB+dataAB;//using repetition  -  3 times is the minimal number of times
            // it is possible to repeat the word in order to distinguish between the real word to the changed one
            msgAM = partner_ip_addr+ "&" + dataAB;
            //sending message to M
            _output.println(msgAM);
            System.out.println("Message sent: " +msgAM + ", data: " + dataAB);

            try {//close socket
                socket.close();
                System.out.println("Host disconnected !");
            }  catch (IOException ioExcept){}
        } catch (Exception connectionFailed) {
            System.out.println("Couldn't connect"); System.err.println(connectionFailed);
        }
        getMessage(7001,a_or_b,partner_ip_addr);//gקtting message from B
    }
    public static void main(String[] args) throws IOException {
        ImprovedHost host = new ImprovedHost();
        host.startWorking(args[0], args[1], args[2]);
    }
}
