package org.geogebra.web.plugin;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONString;
import org.geogebra.common.plugin.SensorLogger;
import org.geogebra.web.html5.util.JSON;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author gabor WebSocket logger for external mobile app
 */
public class WebsocketLogger extends SensorLogger {

	protected WebSocketConnection connection = null;

	public WebsocketLogger(Kernel kernel) {
		this.kernel = kernel;
	}

	public void createConnection() {
		if (this.connection == null
		        || this.connection.getReadyState() != WebSocketConnection.OPEN) {
			this.connection = WebSocketFactory
			        .create(GeoGebraConstants.DATA_LOGGING_WEBSOCKET_URL);
			this.connection.onOpen(new OpenEventHandler() {

				public void open(JavaScriptObject event) {
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
		
		initErrorHandler();

		return true;
	}

	protected void initErrorHandler() {
		connection.onError(new ErrorEventHandler() {

			public void error(JavaScriptObject e) {
				App.debug("Error with webSocket");
			}
		});
	}

	protected void startHandShake() {
		JSONObject obj = new JSONObject();
		obj.put("appID", new JSONString(appID));
		connection.send(obj.toString());
	}

	protected void initCloseHandler() {
		connection.onClose(new CloseEventHandler() {

			public void close(JavaScriptObject event) {
				App.debug("Connection closed of Websocket");
			}
		});
	}

	protected void handle(JavaScriptObject json) {
		// TODO : Maybe do it faster somehow - only logging that is sent?
		if (JSON.get(json, Types.ACCELEROMETER_X.toString()) != null) {
			log(Types.ACCELEROMETER_X,
			        Float.parseFloat(JSON.get(json,
			                Types.ACCELEROMETER_X.toString())));
		}
		if (JSON.get(json, Types.ACCELEROMETER_Y.toString()) != null) {
			log(Types.ACCELEROMETER_Y,
			        Float.parseFloat(JSON.get(json,
			                Types.ACCELEROMETER_Y.toString())));
		}
		if (JSON.get(json, Types.ACCELEROMETER_Z.toString()) != null) {
			log(Types.ACCELEROMETER_Z,
			        Float.parseFloat(JSON.get(json,
			                Types.ACCELEROMETER_Z.toString())));
		}
		if (JSON.get(json, Types.MAGNETIC_FIELD_X.toString()) != null) {
			log(Types.MAGNETIC_FIELD_X,
			        Float.parseFloat(JSON.get(json,
			                Types.MAGNETIC_FIELD_X.toString())));
		}
		if (JSON.get(json, Types.MAGNETIC_FIELD_Y.toString()) != null) {
			log(Types.MAGNETIC_FIELD_Y,
			        Float.parseFloat(JSON.get(json,
			                Types.MAGNETIC_FIELD_Y.toString())));
		}
		if (JSON.get(json, Types.MAGNETIC_FIELD_Z.toString()) != null) {
			log(Types.MAGNETIC_FIELD_Z,
			        Float.parseFloat(JSON.get(json,
			                Types.MAGNETIC_FIELD_Z.toString())));
		}
		if (JSON.get(json, Types.ORIENTATION_X.toString()) != null) {
			log(Types.ORIENTATION_X, Float.parseFloat(JSON.get(json,
			                Types.ORIENTATION_X.toString())));
		}
		if (JSON.get(json, Types.ORIENTATION_Y.toString()) != null) {
			log(Types.ORIENTATION_Y, Float.parseFloat(JSON.get(json,
			                Types.ORIENTATION_Y.toString())));
		}
		if (JSON.get(json, Types.ORIENTATION_Z.toString()) != null) {
			log(Types.ORIENTATION_Z, Float.parseFloat(JSON.get(json,
			                Types.ORIENTATION_Z.toString())));
		}
		
		if (JSON.get(json, Types.DATA_COUNT.toString()) != null) {
			log(Types.DATA_COUNT, Float.parseFloat(JSON.get(json,
			        Types.DATA_COUNT.toString())));
		}
		if (JSON.get(json, Types.TIMESTAMP.toString()) != null) {
			log(Types.TIMESTAMP, Float.parseFloat(JSON.get(json,
			        Types.TIMESTAMP.toString())));
		}
		if (JSON.get(json, Types.LOUDNESS.toString()) != null) {
			log(Types.LOUDNESS,
			        Float.parseFloat(JSON.get(json, Types.LOUDNESS.toString())));
		}
		if (JSON.get(json, Types.PROXIMITY.toString()) != null) {
			log(Types.PROXIMITY, Float.parseFloat(JSON.get(json,
			        Types.PROXIMITY.toString())));
		}
		if (JSON.get(json, Types.LIGHT.toString()) != null) {
			log(Types.LIGHT,
					Float.parseFloat(JSON.get(json, Types.LIGHT.toString())));
		}
	}

	protected void initMsgHandler() {
		connection.onMessage(new MessageEventHandler() {

			public void message(JavaScriptObject msg) {
				String data = JSON.get(msg, "data");
				JavaScriptObject jsonData = JSON.parse(data);
				handle(jsonData);
			}
		});
	}

	@Override
	public void closeSocket() {
		if (connection != null
		        && connection.getReadyState() != WebSocketConnection.CONNECTING) {
			connection.close();
			connection = null;
		}
	}
}