package org.geogebra.web.plugin;

import java.util.ArrayList;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.plugin.SensorLogger;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.view.dataCollection.DataCollectionView;
import org.geogebra.web.html5.util.JSON;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;

/**
 * @author gabor WebSocket logger for external mobile app
 */
public class WebsocketLogger extends SensorLogger {

	private WebSocketConnection connection = null;
	private ArrayList<WebSocketListener> wsListeners = new ArrayList<>();
	private String websocketUrl;

	public WebsocketLogger(Kernel kernel) {
		super(kernel);
	}

	private void constructUrl() {
		boolean secure = false;
		if (Window.Location.getProtocol().equals("https:")) {
			this.websocketUrl = "wss:";
			secure = true;
		} else {
			this.websocketUrl = "ws:";

		}
		if (appID != null && appID.indexOf(".") > -1) {
			this.websocketUrl += appID + ":8080";
		} else {
			if (secure) {
				this.websocketUrl += GeoGebraConstants.DATA_LOGGING_WEBSOCKET_URL
						+ ":"
						+ GeoGebraConstants.DATA_LOGGING_WEBSOCKET_SECURE_PORT;
			} else {
				this.websocketUrl += GeoGebraConstants.DATA_LOGGING_WEBSOCKET_URL
						+ ":" + GeoGebraConstants.DATA_LOGGING_WEBSOCKET_PORT;
			}
		}
		Log.debug(this.websocketUrl);
	}

	private void createConnection() {
		if (this.connection == null
		        || this.connection.getReadyState() != WebSocketConnection.OPEN) {
			constructUrl();
			this.connection = WebSocketFactory.create(this.websocketUrl);
			this.connection.onOpen(new OpenEventHandler() {

				@Override
				public void open(JavaScriptObject event) {
					startHandShake();
				}

			});
		} else {
			Log.debug("websocket connection is already established");
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

			@Override
			public void error(JavaScriptObject e) {
				Log.debug("Error with webSocket");
			}
		});
	}

	private void startHandShake() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("appID", appID);
		} catch (Exception e) {
			Log.debug("JSON error: " + e.getMessage());
		}
		connection.send(obj.toString());
	}

	/**
	 * asks the mobile data app for a list of available sensors
	 */
	public void triggerAvailableSensors() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("appID", appID);
			obj.put("availableSensors", "");
		} catch (Exception e) {
			Log.debug("JSON error: " + e.getMessage());
		}
		connection.send(obj.toString());
	}

	/**
	 * asks the mobile data app for the actual frequency
	 */
	public void triggerFrequency() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("appID", new JSONString(appID));
			obj.put("frequency", new JSONString(""));
		} catch (Exception e) {
			Log.debug("JSON error: " + e.getMessage());
		}
		connection.send(obj.toString());
	}

	private void initCloseHandler() {
		connection.onClose(new CloseEventHandler() {

			@Override
			public void close(JavaScriptObject event) {
				Log.debug("Connection closed of Websocket");
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
		for (WebSocketListener listener : this.wsListeners) {
			listener.onIDchecked(correctID);
		}
	}

	private void handleFrequency(String frequency) {
		for (WebSocketListener listener : this.wsListeners) {
			listener.onFrequency(Integer.parseInt(frequency));
		}
	}

	/**
	 * if sensor is available, the settings for this sensor should be visible in
	 * the {@link DataCollectionView}
	 * 
	 * @param json
	 *            availability data from WS
	 */
	private void handleAvailableSensors(JavaScriptObject json) {
		handleAvailable(json, Types.ACCELEROMETER_X);
		handleAvailable(json, Types.MAGNETIC_FIELD_X);
		handleAvailable(json, Types.ORIENTATION_X);
		handleAvailable(json, Types.LOUDNESS);
		handleAvailable(json, Types.PROXIMITY);
		handleAvailable(json, Types.LIGHT);
		for (WebSocketListener listener : this.wsListeners) {
			listener.onSensorActive(Types.TIMESTAMP, true);
		}
	}

	private void handleAvailable(JavaScriptObject json, Types type) {
		if (JSON.get(json, type.toString()) != null) {
			for (WebSocketListener listener : this.wsListeners) {
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
		for (WebSocketListener listener : this.wsListeners) {
			listener.onDataReceived(sensor, timestamp, dataCount);
		}
	}

	private void initMsgHandler() {
		connection.onMessage(new MessageEventHandler() {

			@Override
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
		this.wsListeners.add(listener);
	}

	public void removeListener(WebSocketListener listener) {
		this.wsListeners.remove(listener);
	}
}