package geogebra3D.input3D.leonar3do;
 
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.geogebra.common.util.Base64;



@WebSocket(maxMessageSize = 64 * 1024)
public class LeoSocket {
 
	
	

	// to remove unwanted logging
	static{
		class NoLogging implements Logger {
			@Override public String getName() { return "no"; }
			@Override public void warn(String msg, Object... args) { }
			@Override public void warn(Throwable thrown) { }
			@Override public void warn(String msg, Throwable thrown) { }
			@Override public void info(String msg, Object... args) { }
			@Override public void info(Throwable thrown) { }
			@Override public void info(String msg, Throwable thrown) { }
			@Override public boolean isDebugEnabled() { return false; }
			@Override public void setDebugEnabled(boolean enabled) { }
			@Override public void debug(String msg, Object... args) { }
			@Override public void debug(Throwable thrown) { }
			@Override public void debug(String msg, Throwable thrown) { }
			@Override public Logger getLogger(String name) { return this; }
			@Override public void ignore(Throwable ignored) { }
		}

		org.eclipse.jetty.util.log.Log.setLog(new NoLogging());


	}


	/** bird x position */
    public double birdX;
    /** bird y position */
    public double birdY;
    /** bird z position */
    public double birdZ;
	
    public double birdOrientationX, birdOrientationY, birdOrientationZ, birdOrientationW;
     
    public double leftEyeX, leftEyeY, leftEyeZ;
    public double rightEyeX, rightEyeY, rightEyeZ;
    public double glassesCenterX, glassesCenterY, glassesCenterZ;
    public double glassesOrientationX, glassesOrientationY, glassesOrientationZ, glassesOrientationW;

    public double bigButton, smallButton, vibration;
    
    /** says if it has got a message from leo */
    public boolean gotMessage = false;      
    
    
    private Session session;
    
    private WebSocketClient client;
    
    private RemoteEndpoint remoteEndpoint;
    
   

 
    public LeoSocket() {
     
    	client = new WebSocketClient();
    	
        String destUri = "ws://localhost:5000";
        
        
		try {
			System.out.println("start client");
			client.start();
			URI echoUri = new URI(destUri);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			System.out.println("connect client");
			client.connect(this, echoUri, request);
			System.out.println("Connecting to : "+echoUri);
		} catch (Throwable t) {
			System.out.println("failed to connect web socket");
			t.printStackTrace();
		}
		
    }
    
 

 
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
        this.session = null;
    }
    
    private boolean connected = false;
 
    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.printf("Got connect: %s%n", session);
        this.session = session;
        remoteEndpoint = session.getRemote();
       
        connected = true;

    }
    
    
    
    public boolean getLeoData(){
    	
    	if (!connected)
    		return false;
    	
    	try {
    		gotMessage = false;
			remoteEndpoint.sendString("getLeoData");
			return true;
		} catch (IOException e) {
			System.out.println("failed to send getLeoData");
			return false;
		}
    }
    
 
    @OnWebSocketMessage
    public void onMessage(String msg) {
        
        try {
			byte[] buffer = Base64.decode(msg);
			ByteBuffer bb = ByteBuffer.wrap(buffer);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			
			// ignore leo version
			bb.getDouble();
			
			// bird position
			birdX = bb.getDouble();
			birdY = bb.getDouble();
			birdZ = bb.getDouble();
			
			// bird orientation
			birdOrientationX = bb.getDouble();
			birdOrientationY = bb.getDouble();
			birdOrientationZ = bb.getDouble();
			birdOrientationW = bb.getDouble();

			//glasses
			leftEyeX = bb.getDouble(); 
			leftEyeY = bb.getDouble(); 
			leftEyeZ = bb.getDouble();

			rightEyeX = bb.getDouble(); 
			rightEyeY = bb.getDouble(); 
			rightEyeZ = bb.getDouble();

			glassesCenterX = bb.getDouble(); 
			glassesCenterY = bb.getDouble(); 
			glassesCenterZ = bb.getDouble();

			glassesOrientationX = bb.getDouble(); 
			glassesOrientationY = bb.getDouble(); 
			glassesOrientationZ = bb.getDouble(); 
			glassesOrientationW = bb.getDouble();

			//buttons
			bigButton = bb.getDouble(); 
			smallButton = bb.getDouble(); 

			//vibration
			vibration = bb.getDouble();

			
			gotMessage = true;
	        
	        
		} catch (IOException e) {
			e.printStackTrace();
		}         
        	
    }
    
 
    
}

