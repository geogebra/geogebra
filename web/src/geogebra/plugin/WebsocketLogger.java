package geogebra.plugin;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.common.plugin.SensorLogger;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author gabor WebSocket logger for external mobile app
 */
public class WebsocketLogger extends SensorLogger {

	/**
	 * port to receive UDP logging on
	 */
	public static int port = 7166;

	private WebSocketConnection connection;

	public WebsocketLogger(Kernel kernel) {
		this.kernel = kernel;
		this.connection = WebSocketFactory
		        .create(
		        GeoGebraConstants.DATA_LOGGING_WEBSOCKET_URL);
		this.connection.onOpen(new OpenEventHandler() {

			public void open(JavaScriptObject event) {
				App.debug("websocket connection opened");
			}
		});
	}

	public boolean startLogging() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void closeSocket() {
		connection.close();
	}

}
