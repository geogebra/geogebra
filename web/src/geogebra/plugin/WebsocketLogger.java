package geogebra.plugin;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.ggtapi.models.json.JSONString;
import geogebra.common.plugin.SensorLogger;
import geogebra.html5.main.AppW;
import geogebra.html5.util.JSON;

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
		JSONObject obj = new JSONObject();
		obj.put("appID", new JSONString(appID));
		connection.send(obj.toString());
	}

	private void initCloseHandler() {
		connection.onClose(new CloseEventHandler() {

			public void close(JavaScriptObject event) {
				App.debug("Connection closed of Websocket");
			}
		});
	}

	void handle(JavaScriptObject json) {
		// TODO : Maybe do it faster somehow - only logging that is sent?
		if (JSON.get(json, Types.ACCELEROMETER_X.name()) != null) {
			log(Types.ACCELEROMETER_X,
			        Float.parseFloat(JSON.get(json,
			                Types.ACCELEROMETER_X.name())));
		}
		if (JSON.get(json, Types.ACCELEROMETER_Y.name()) != null) {
			log(Types.ACCELEROMETER_Y,
			        Float.parseFloat(JSON.get(json,
			                Types.ACCELEROMETER_Y.name())));
		}
		if (JSON.get(json, Types.ACCELEROMETER_Z.name()) != null) {
			log(Types.ACCELEROMETER_Z,
			        Float.parseFloat(JSON.get(json,
			                Types.ACCELEROMETER_Z.name())));
		}
		if (JSON.get(json, Types.MAGNETIC_FIELD_X.name()) != null) {
			log(Types.MAGNETIC_FIELD_X,
			        Float.parseFloat(JSON.get(json,
			                Types.MAGNETIC_FIELD_X.name())));
		}
		if (JSON.get(json, Types.MAGNETIC_FIELD_Y.name()) != null) {
			log(Types.MAGNETIC_FIELD_Y,
			        Float.parseFloat(JSON.get(json,
			                Types.MAGNETIC_FIELD_Y.name())));
		}
		if (JSON.get(json, Types.MAGNETIC_FIELD_Z.name()) != null) {
			log(Types.MAGNETIC_FIELD_Z,
			        Float.parseFloat(JSON.get(json,
			                Types.MAGNETIC_FIELD_Z.name())));
		}
		if (JSON.get(json, Types.ORIENTATION_X.name()) != null) {
			log(Types.ORIENTATION_X, Float.parseFloat(JSON.get(json,
			        Types.ORIENTATION_X.name())));
		}
		if (JSON.get(json, Types.ORIENTATION_Y.name()) != null) {
			log(Types.ORIENTATION_Y, Float.parseFloat(JSON.get(json,
			        Types.ORIENTATION_Y.name())));
		}
		if (JSON.get(json, Types.ORIENTATION_Z.name()) != null) {
			log(Types.ORIENTATION_Z, Float.parseFloat(JSON.get(json,
			        Types.ORIENTATION_Z.name())));
		}
		if (JSON.get(json, Types.ACCELEROMETER_X.name()) != null) {
			log(Types.ACCELEROMETER_X,
			        Float.parseFloat(JSON.get(json,
			                Types.ACCELEROMETER_X.name())));
		}
		if (JSON.get(json, Types.DATA_COUNT.name()) != null) {
			log(Types.DATA_COUNT,
			        Float.parseFloat(JSON.get(json, Types.DATA_COUNT.name())));
		}
		if (JSON.get(json, Types.TIMESTAMP.name()) != null) {
			log(Types.TIMESTAMP,
			        Float.parseFloat(JSON.get(json, Types.TIMESTAMP.name())));
		}
		if (JSON.get(json, Types.LOUDNESS.name()) != null) {
			log(Types.LOUDNESS,
			        Float.parseFloat(JSON.get(json, Types.LOUDNESS.name())));
		}
		if (JSON.get(json, Types.PROXIMITY.name()) != null) {
			log(Types.PROXIMITY,
			        Float.parseFloat(JSON.get(json, Types.PROXIMITY.name())));
		}
		if (JSON.get(json, Types.LIGHT.name()) != null) {
			log(Types.LIGHT,
			        Float.parseFloat(JSON.get(json, Types.LIGHT.name())));
		}

	}

	private void initMsgHandler() {
		connection.onMessage(new MessageEventHandler() {

			public void message(JavaScriptObject msg) {
				AppW.nativeConsole(msg);
				String data = JSON.get(msg, "data");
				JavaScriptObject jsonData = JSON.parse(data);
				handle(jsonData);

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
