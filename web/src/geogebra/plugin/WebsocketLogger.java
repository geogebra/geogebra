package geogebra.plugin;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.common.plugin.SensorLogger;
import geogebra.html5.main.AppW;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author gabor WebSocket logger for external mobile app
 */
public class WebsocketLogger extends SensorLogger {

	private WebSocketConnection connection = null;

	public WebsocketLogger(Kernel kernel) {
		this.kernel = kernel;
	}

	protected void createConnection() {
		if (this.connection == null
		        || this.connection.getReadyState() != WebSocketConnection.OPEN) {
			this.connection = WebSocketFactory
			        .create(GeoGebraConstants.DATA_LOGGING_WEBSOCKET_URL);
			this.connection.onOpen(new OpenEventHandler() {

				public void open(JavaScriptObject event) {
					App.debug("websocket connection opened");
					startHandShake();
				}

			});
		} else {
			App.debug("websocket connection is already established");
		}
    }

	@Override
	public boolean startLogging() {
		initStartLogging();

		closeSocket();

		createConnection();

		initMsgHandler();

		initCloseHandler();
		
		return true;
	}

	private void startHandShake() {
		connection.send(appID);
	}

	private void initCloseHandler() {
		connection.onClose(new CloseEventHandler() {

			public void close(JavaScriptObject event) {
				App.debug("Connection closed of Websocket");
			}
		});
	}

	private void initMsgHandler() {
		connection.onMessage(new MessageEventHandler() {

			public void message(JavaScriptObject msg) {
				AppW.console(msg);
			}
		});
	}

	@Override
	protected void closeSocket() {
		if (connection != null
		        && connection.getReadyState() != WebSocketConnection.CONNECTING) {
			connection.close();
			connection = null;
		}
	}

}
