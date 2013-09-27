package geogebra3D.input3D.leonar3do;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
 
 
public class Client {
	

	
	public static Socket socket = null;
    public static Thread t1;

	public static void main(String[] zero){

		PrintWriter out;
		BufferedReader in;
		
		try {
	         
	        System.out.println("Connecting...");
	        socket = new Socket("127.0.0.1",5000);
	        System.out.println("Connected with local port "+socket.getLocalPort()); 
	         
	        out = new PrintWriter(socket.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader( socket.getInputStream()));
	        
	        System.out.println("out");
	        out.println("getLeoData");
	        
	        System.out.println("in:");
	        System.out.println(in.readLine());

			out.close();
			in.close();
			socket.close();
	         
	         
	    } catch (UnknownHostException e) {
	      System.err.println("Connection failed to "+socket.getLocalAddress());
	    } catch (IOException e) {
	      System.err.println("No server on port "+socket.getLocalPort());
	    }

		
	}
	
 
}


