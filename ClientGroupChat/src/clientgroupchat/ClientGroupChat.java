/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientgroupchat;

import FileReader.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.text.ParseException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yoga
 */
public class ClientGroupChat {

    static public Socket clientSocket;
    static public BufferedReader objectFromServer;
    static public PrintWriter objectToServer;
    static public Scanner scan;
    static public long sharekey;
    static public long sharevalue;
    static public String file="share.txt";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException, Exception {
        // TODO code application logic here
        Scanner scan = new Scanner(System.in);

        System.out.print("Input server IP hostname : ");
        String host = scan.nextLine();
        clientSocket = new Socket(host, 2000);
        Thread t = new Thread(new StringGetter());
        t.start();
        while (true) {
            sleep(100);
            //send msg to server
            String msg = scan.nextLine();
            ParseCommand(msg);
        }
    }
    
    public static void ParseCommand(String str) throws Exception{
         String[] command = str.split("\\s+");
         if(command[0].equals("send")){
             //String msg= command[0]+ " "+ sharekey + " "+ sharevalue;
             String msg= command[0]+ " "+FileReader.FileToString(command[1]);
             sendToServer(msg);
         }else if (command[0].equals("save")){
             file=command[1];
         }else{
             sendToServer(str);
         }
    }

    public static class StringGetter
            extends Thread {

        public void run() {
            try {
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String response;
                while (true) {
                    response = inFromServer.readLine();
                    System.out.println("Receive from server : " + response);
                    Parse(response);
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientGroupChat.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(ClientGroupChat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void Parse(String str) throws ParseException, Exception {
            String[] command = str.split("\\s+");
            if (command[0].equals("share")){
                sharekey=Long.parseLong(command[1]);
                sharevalue=Long.parseLong(command[2]);
                String sshare=command[1] + " " + command[2];
                byte[] content = sshare.getBytes();
                FileReader.savefile(file, content);
                
            }
         
                
            
        }

    }

    public static void sendToServer(String msg) throws Exception {
        //create output stream attached to socket
        PrintWriter outToServer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        //send msg to server
        outToServer.print(msg + '\n');
        outToServer.flush();
    }
}
