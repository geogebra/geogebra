package geogebra3D.input3D.leonar3do;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
 
 
public class Client {
     
    public static void main(String[] zero) {
         
         
        Socket socket;
        BufferedReader in;
        PrintWriter out;
 
        try {

        	socket = new Socket("localhost",1024);   
        	System.out.println("Demande de connexion");

        	/*
                in = new BufferedReader (new InputStreamReader (socket.getInputStream()));
                String message_distant = in.readLine();
                System.out.println(message_distant);
        	 */
        	
            out = new PrintWriter(socket.getOutputStream());
            out.println("Coucou");
            out.println("Mon gars");
            out.flush();


        	socket.close();

        }catch (UnknownHostException e) {

            e.printStackTrace();
        }catch (IOException e) {
             
            e.printStackTrace();
        }
    }
 
}