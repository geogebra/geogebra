package geogebraVoiceCommand.dialog;

import java.lang.reflect.Type;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import geogebraVoiceCommand.dialog.websocket.Message;
import geogebraVoiceCommand.modifierstringcommand.object.CmdBuilder;

public class Controller {
	private View  view;
	private Model model;

	private CmdBuilder builder;

	public Controller(View view, Model model) {
		this.view = view;
		this.model = model;
		builder = new CmdBuilder();

		model.setStompFrameHandler(new StompFrameHandler() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				return Message.class;
			}

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				String msg = ((Message)payload).getText();
//				view.setLbStatus(msg);
//				builder.build(msg);
				builder.executeCMD(msg);
			}
		});

	}

	public void start(){
		view.setVisible(true);
		model.connect();

		view.setLbStatus("ваш id:" + model.getUserId());
	}


}
