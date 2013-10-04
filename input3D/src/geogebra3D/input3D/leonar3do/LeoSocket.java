package geogebra3D.input3D.leonar3do;
 
import geogebra.common.util.Base64;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;



@WebSocket(maxMessageSize = 64 * 1024)
public class LeoSocket {
 
 
    


	/** bird x position */
    public double birdX;
    /** bird y position */
    public double birdY;
    /** bird z position */
    public double birdZ;
    
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
			
			
			gotMessage = true;
	        
	        
		} catch (IOException e) {
			e.printStackTrace();
		}         
        	
    }
    
 
    
}

