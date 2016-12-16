/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servergroupchat;

import SharingScheme.ShamirScheme;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yoga
 */
public class ServerGroupChat {

    static public ArrayList<ClientController> listClient = new ArrayList();
    static public ServerSocket server;
    static public ArrayList<Boolean> sendshare = new ArrayList();
    static public ShamirScheme scheme;
    static public ArrayList<BigInteger> x = new ArrayList();
    static public ArrayList<BigInteger> y = new ArrayList();
    static public boolean permitted= false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        String ip;
        String hostname;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Server IP address : " + ip);
            System.out.println("Port : 2000");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        server = new ServerSocket(2000);
        while (true) {
            Socket socket = server.accept();
            System.out.println("connected");
            ClientController clientcontroller = new ClientController(socket);
            listClient.add(clientcontroller);
            sendshare.add(false);

            Thread t = new Thread(clientcontroller);
            t.start();
        }

        // TODO code application logic here
    }

    public static void sendToSpesificClient(String str, int x) throws Exception {
        //untuk kirim move dari 1 client ke semua client dalam room
        listClient.get(x).SendToClient(str);
    }
    
    public static boolean CheckSend(){
        for(int i =0 ; i<sendshare.size();i++){
            if(!sendshare.get(i)){
                return false;
            }
        }
        return true;
    }

    public static class ClientController
            extends Thread {

        public Socket socket;

        public ClientController(Socket clientSocket) {
            this.socket = clientSocket;
        }

        void SendToClient(String msg) throws Exception {
            //create output stream attached to socket
            PrintWriter outToClient = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            //send msg to server
            outToClient.print(msg + '\n');
            outToClient.flush();
        }
        
        public int getID(){
            for(int i =0; i<listClient.size();i++){
                if(listClient.get(i).equals(this)){
                    return i;
                }
            }
            return -1;
        }

        void Parse(String msg) throws ParseException, Exception {
            String[] command = msg.split("\\s+");
            
            if (command[0].equals("create")){
                scheme = new ShamirScheme();
                String response = "share ";
                ArrayList<Long> shares = scheme.Share(listClient.size());
                for(int i=0; i < listClient.size();i++){
                    String sendmessage = response.concat(String.valueOf(i+1)).concat(" ").concat(shares.get(i).toString());
                    System.out.println(sendmessage);
                    sendToSpesificClient(sendmessage,i);
                }
            } else if (command[0].equals("send")){
                sendshare.set(getID(), Boolean.TRUE);
                x.add(new BigInteger(command[1]));
                y.add(new BigInteger(command[2]));
                if (CheckSend()){
                    permitted= scheme.CheckShare(x, y);
                    System.out.println("ini" +permitted);
                    
                }
            } else if (command[0].equals("chat")){
                if (permitted){
                    String message= String.valueOf(getID())+": ";
                    for(int i = 1 ; i< command.length;i++){
                        message=message.concat(command[i]);
                        
                        message=message.concat(" ");
                    }
                    for(int i=0 ; i<listClient.size();i++){
                        sendToSpesificClient(message,i);
                    }
                }
            }
        }

        public void run() {
            try {
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String request;
                while ((request = inFromClient.readLine()) != null) {
                    System.out.println("receive from client : " + request);
                    Parse(request);
                }
            } catch (SocketException ex) {
            } catch (IOException ex) {
                Logger.getLogger(ServerGroupChat.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(ServerGroupChat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
