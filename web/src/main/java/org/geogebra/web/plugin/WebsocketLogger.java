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
			this.connection = WebSocketFactory.create(this.websocket_url);
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

	/**
	 * asks the mobile data app for the actual frequency
	 */
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
						Boolean.parseBoolean(JSON.get(json, type.toString())));
			}
		}

	}

	private void handleData(JavaScriptObject json) {
		beforeLog();
		int dataCount = Integer.parseInt(JSON.get(json,
				Types.DATA_COUNT.toString()));
		int timestampMS = Integer.parseInt(JSON.get(json,
				Types.TIMESTAMP.toString()));
		double timestamp = Float.parseFloat(JSON.get(json,
				Types.TIMESTAMP.toString())) * 0.001;

		// TODO : Maybe do it faster somehow - only logging that is sent?
		String sensorAx = JSON.get(json, Types.ACCELEROMETER_X.toString());
		if (sensorAx != null) {
			log(Types.ACCELEROMETER_X, timestamp, Float.parseFloat(sensorAx));
			onDataReceived(Types.ACCELEROMETER_X, timestampMS, dataCount);
		}

		String sensorAy = JSON.get(json, Types.ACCELEROMETER_Y.toString());
		if (sensorAy != null) {
			log(Types.ACCELEROMETER_Y, timestamp, Float.parseFloat(sensorAy));
		}

		String sensorAz = JSON.get(json, Types.ACCELEROMETER_Z.toString());
		if (sensorAz != null) {
			log(Types.ACCELEROMETER_Z, timestamp, Float.parseFloat(sensorAz));
		}

		String sensorMx = JSON.get(json, Types.MAGNETIC_FIELD_X.toString());
		if (sensorMx != null) {
			log(Types.MAGNETIC_FIELD_X, timestamp, Float.parseFloat(sensorMx));
			onDataReceived(Types.MAGNETIC_FIELD_X, timestampMS, dataCount);
		}

		String sensorMy = JSON.get(json, Types.MAGNETIC_FIELD_Y.toString());
		if (sensorMy != null) {
			log(Types.MAGNETIC_FIELD_Y, timestamp, Float.parseFloat(sensorMy));
		}

		String sensorMz = JSON.get(json, Types.MAGNETIC_FIELD_Z.toString());
		if (sensorMz != null) {
			log(Types.MAGNETIC_FIELD_Z, timestamp, Float.parseFloat(sensorMz));
		}

		String sensorOx = JSON.get(json, Types.ORIENTATION_X.toString());
		if (sensorOx != null) {
			log(Types.ORIENTATION_X, timestamp, Float.parseFloat(sensorOx));
			onDataReceived(Types.ORIENTATION_X, timestampMS, dataCount);
		}

		String sensorOy = JSON.get(json, Types.ORIENTATION_Y.toString());
		if (sensorOy != null) {
			log(Types.ORIENTATION_Y, timestamp, Float.parseFloat(sensorOy));
		}

		String sensorOz = JSON.get(json, Types.ORIENTATION_Z.toString());
		if (sensorOz != null) {
			log(Types.ORIENTATION_Z, timestamp, Float.parseFloat(sensorOz));
		}

		if (JSON.get(json, Types.DATA_COUNT.toString()) != null) {
			log(Types.DATA_COUNT, timestamp, dataCount);
		}

		if (JSON.get(json, Types.TIMESTAMP.toString()) != null) {
			log(Types.TIMESTAMP, timestamp, timestamp);
		}

		String sensorLo = JSON.get(json, Types.LOUDNESS.toString());
		if (sensorLo != null) {
			onDataReceived(Types.LOUDNESS, timestampMS, dataCount);
			log(Types.LOUDNESS, timestamp, Float.parseFloat(sensorLo));
		}

		String sensorP = JSON.get(json, Types.PROXIMITY.toString());
		if (sensorP != null) {
			log(Types.PROXIMITY, timestamp, Float.parseFloat(sensorP));
			onDataReceived(Types.PROXIMITY, timestampMS, dataCount);
		}

		String sensorLi = JSON.get(json, Types.LIGHT.toString());
		if (sensorLi != null) {
			log(Types.LIGHT, timestamp, Float.parseFloat(sensorLi));
			onDataReceived(Types.LIGHT, timestampMS, dataCount);
		}
	}

	private void onDataReceived(Types sensor, double timestamp, int dataCount) {
		for (WebSocketListener listener : this.listeners) {
			listener.onDataReceived(sensor, timestamp, dataCount);
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