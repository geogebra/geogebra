package geogebraVoiceCommand.dialog;

import java.io.IOException;

import org.springframework.messaging.simp.stomp.StompFrameHandler;

import geogebraVoiceCommand.dialog.websocket.WebSocketConnection;
import geogebraVoiceCommand.modifierstringcommand.object.CmdBuilder;

public class Model {

	private String userId;

	private WebSocketConnection webSocketConnection;

	public Model() {

		webSocketConnection = new WebSocketConnection();
	}

	public void setStompFrameHandler(StompFrameHandler stompFrameHandler) {
		webSocketConnection.setStompFrameHandler(stompFrameHandler);
	}

	public String getUserId(){
		if(userId == null){
			try {
				userId = webSocketConnection.getId();
			} catch (IOException e) {
				System.err.println("error user id");
			}
		}

		return userId;
	}

	public boolean connect(){
		return webSocketConnection.connectWs(getUserId());
	}



}
