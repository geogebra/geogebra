package org.geogebra.web.plugin;

import java.util.ArrayList;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONString;
import org.geogebra.common.plugin.SensorLogger;
import org.geogebra.web.html5.util.JSON;
import org.geogebra.web.web.gui.view.dataCollection.DataCollectionView;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;

/**
 * @author gabor WebSocket logger for external mobile app
 */
public class WebsocketLogger extends SensorLogger {

	private WebSocketConnection connection = null;
	private ArrayList<WebSocketListener> listeners = new ArrayList<WebSocketListener>();
	private String websocket_url;

	public WebsocketLogger(Kernel kernel) {
		this.kernel = kernel;
		constructUrl();
	}

	private void constructUrl() {
		if (Window.Location.getProtocol().equals("https:")) {
			this.websocket_url = "wss:"
					+ GeoGebraConstants.DATA_LOGGING_WEBSOCKET_URL;
		} else {
			this.websocket_url = "ws:"
					+ GeoGebraConstants.DATA_LOGGING_WEBSOCKET_URL;
		}
	}

	private void createConnection() {
		if (this.connection == null
		        || this.connection.getReadyState() != WebSocketConnection.OPEN) {
			this.connection = WebSocketFactory
.create(this.websocket_url);
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

	private void initErrorHandler() {
		connection.onError(new ErrorEventHandler() {

			public void error(JavaScriptObject e) {
				App.debug("Error with webSocket");
			}
		});
	}

	private void startHandShake() {
		JSONObject obj = new JSONObject();
		obj.put("appID", new JSONString(appID));
		connection.send(obj.toString());
	}

	/**
	 * asks the mobile data app for a list of available sensors
	 */
	public void triggerAvailableSensors() {
		JSONObject obj = new JSONObject();
		obj.put("appID", new JSONString(appID));
		obj.put("availableSensors", new JSONString(""));
		connection.send(obj.toString());
	}

	public void triggerFrequency() {
		JSONObject obj = new JSONObject();
		obj.put("appID", new JSONString(appID));
		obj.put("frequency", new JSONString(""));
		connection.send(obj.toString());
	}

	private void initCloseHandler() {
		connection.onClose(new CloseEventHandler() {

			public void close(JavaScriptObject event) {
				App.debug("Connection closed of Websocket");
			}
		});
	}

	/**
	 * differs between received values and list of available sensors
	 * 
	 * @param json
	 *            JSON
	 */
	void handle(JavaScriptObject json) {
		if (JSON.get(json, Types.MOBILE_FOUND.toString()) != null) {
			handleIDchecked(String.valueOf(
					JSON.get(json, Types.MOBILE_FOUND.toString())).equals(
					"true"));
		} else if (JSON.get(json, Types.FREQUENCY.toString()) != null) {
			handleFrequency(JSON.get(json, Types.FREQUENCY.toString()));
		} else if (JSON.get(json, Types.ACCELEROMETER_X.toString())
				.equals("true")
					|| JSON.get(json, Types.ACCELEROMETER_X.toString()).equals(
							"false")) {
			handleAvailableSensors(json);
		} else {
			handleData(json);
		}
	}


	private void handleIDchecked(boolean correctID) {
		for (WebSocketListener listener : this.listeners) {
			listener.onIDchecked(correctID);
		}
	}

	private void handleFrequency(String frequency) {
		for (WebSocketListener listener : this.listeners) {
			listener.onFrequency(Integer.parseInt(frequency));
		}
	}

	/**
	 * if sensor is available, the settings for this sensor should be visible in
	 * the {@link DataCollectionView}
	 * 
	 * @param json
	 */
	private void handleAvailableSensors(JavaScriptObject json) {
		handleAvailable(json, Types.ACCELEROMETER_X);
		handleAvailable(json, Types.MAGNETIC_FIELD_X);
		handleAvailable(json, Types.ORIENTATION_X);
		handleAvailable(json, Types.LOUDNESS);
		handleAvailable(json, Types.PROXIMITY);
		handleAvailable(json, Types.LIGHT);
		for (WebSocketListener listener : this.listeners) {
			listener.onSensorActive(Types.TIMESTAMP, true);
		}
	}

	private void handleAvailable(JavaScriptObject json, Types type) {
		if (JSON.get(json, type.toString()) != null) {
			for (WebSocketListener listener : this.listeners) {
				listener.onSensorActive(type,
						Boolean
						.parseBoolean(JSON.get(json,
 type.toString())));
			}
		}

	}

	private void handleData(JavaScriptObject json) {
		beforeLog();
		double timestamp = Float.parseFloat(JSON.get(json,
				Types.TIMESTAMP.toString())) * 0.001;

		// TODO : Maybe do it faster somehow - only logging that is sent?
		if (JSON.get(json, Types.ACCELEROMETER_X.toString()) != null) {
			log(Types.ACCELEROMETER_X,
					timestamp,
			        Float.parseFloat(JSON.get(json,
			                Types.ACCELEROMETER_X.toString())));
		}
		if (JSON.get(json, Types.ACCELEROMETER_Y.toString()) != null) {
			log(Types.ACCELEROMETER_Y,
					timestamp,
			        Float.parseFloat(JSON.get(json,
			                Types.ACCELEROMETER_Y.toString())));
		}
		if (JSON.get(json, Types.ACCELEROMETER_Z.toString()) != null) {
			log(Types.ACCELEROMETER_Z,
					timestamp,
			        Float.parseFloat(JSON.get(json,
			                Types.ACCELEROMETER_Z.toString())));
		}
		if (JSON.get(json, Types.MAGNETIC_FIELD_X.toString()) != null) {
			log(Types.MAGNETIC_FIELD_X,
					timestamp,
			        Float.parseFloat(JSON.get(json,
			                Types.MAGNETIC_FIELD_X.toString())));
		}
		if (JSON.get(json, Types.MAGNETIC_FIELD_Y.toString()) != null) {
			log(Types.MAGNETIC_FIELD_Y,
					timestamp,
			        Float.parseFloat(JSON.get(json,
			                Types.MAGNETIC_FIELD_Y.toString())));
		}
		if (JSON.get(json, Types.MAGNETIC_FIELD_Z.toString()) != null) {
			log(Types.MAGNETIC_FIELD_Z,
					timestamp,
			        Float.parseFloat(JSON.get(json,
			                Types.MAGNETIC_FIELD_Z.toString())));
		}
		if (JSON.get(json, Types.ORIENTATION_X.toString()) != null) {
			log(Types.ORIENTATION_X,
					timestamp,
					Float.parseFloat(JSON.get(json,
			                Types.ORIENTATION_X.toString())));
		}
		if (JSON.get(json, Types.ORIENTATION_Y.toString()) != null) {
			log(Types.ORIENTATION_Y,
					timestamp,
					Float.parseFloat(JSON.get(json,
			                Types.ORIENTATION_Y.toString())));
		}
		if (JSON.get(json, Types.ORIENTATION_Z.toString()) != null) {
			log(Types.ORIENTATION_Z,
					timestamp,
					Float.parseFloat(JSON.get(json,
			                Types.ORIENTATION_Z.toString())));
		}
		
		if (JSON.get(json, Types.DATA_COUNT.toString()) != null) {
			log(Types.DATA_COUNT, timestamp, Float.parseFloat(JSON.get(json,
			        Types.DATA_COUNT.toString())));
		}
		if (JSON.get(json, Types.TIMESTAMP.toString()) != null) {
			log(Types.TIMESTAMP, timestamp, timestamp);
		}
		if (JSON.get(json, Types.LOUDNESS.toString()) != null) {
			log(Types.LOUDNESS, timestamp,
			        Float.parseFloat(JSON.get(json, Types.LOUDNESS.toString())));
		}
		if (JSON.get(json, Types.PROXIMITY.toString()) != null) {
			log(Types.PROXIMITY, timestamp, Float.parseFloat(JSON
					.get(json,
			        Types.PROXIMITY.toString())));
		}
		if (JSON.get(json, Types.LIGHT.toString()) != null) {
			log(Types.LIGHT, timestamp,
					Float.parseFloat(JSON.get(json, Types.LIGHT.toString())));
		}
	}



	private void initMsgHandler() {
		connection.onMessage(new MessageEventHandler() {

			public void message(JavaScriptObject msg) {
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

	public void addListener(WebSocketListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(WebSocketListener listener) {
		this.listeners.remove(listener);
	}
}