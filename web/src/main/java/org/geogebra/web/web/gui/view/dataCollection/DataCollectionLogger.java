package org.geogebra.web.web.gui.view.dataCollection;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.util.JSON;
import org.geogebra.web.plugin.OpenEventHandler;
import org.geogebra.web.plugin.WebSocketConnection;
import org.geogebra.web.plugin.WebSocketFactory;
import org.geogebra.web.plugin.WebsocketLogger;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * SensorLogger for the DataCollectionView
 *
 */
public class DataCollectionLogger extends WebsocketLogger {

	private DataCollection dataCollection;

	/**
	 * 
	 * @param kernel
	 *            {@link Kernel}
	 * @param dataCollection
	 *            {@link DataCollection}
	 */
	public DataCollectionLogger(Kernel kernel, DataCollection dataCollection) {
		super(kernel);
		this.dataCollection = dataCollection;
	}

	@Override
	public void createConnection() {
		if (this.connection == null
				|| this.connection.getReadyState() != WebSocketConnection.OPEN) {
			this.connection = WebSocketFactory
					.create(GeoGebraConstants.DATA_LOGGING_WEBSOCKET_URL);
			initMsgHandler();
			initCloseHandler();
			initErrorHandler();
			this.connection.onOpen(new OpenEventHandler() {

				public void open(JavaScriptObject event) {
					startHandShake();
				}

			});
		} else {
			App.debug("websocket connection is already established");
		}
	}

	/**
	 * stops logging
	 */
	@Override
	public void stopLogging() {
		kernel.setUndoActive(oldUndoActive);
		kernel.storeUndoInfo();

		listeners.clear();
		listenersL.clear();
		listenersF.clear();
		listenersAges.clear();
	}

	@Override
	public boolean startLogging() {
		initStartLogging();
		return true;
	}

	/**
	 * differs between received values and list of available sensors
	 */
	@Override
	protected void handle(JavaScriptObject json) {
		if (JSON.get(json, Types.MOBILE_FOUND.toString()) != null) {
			wrongID();
		} else if (JSON.get(json, Types.ACCELEROMETER_X.toString()) != null) {
			if (JSON.get(json, Types.ACCELEROMETER_X.toString()).equals("true")
					|| JSON.get(json, Types.ACCELEROMETER_X.toString()).equals(
							"false")) {
				handleAvailableSensors(json);
			} else {
				super.handle(json);
			}
		}
	}

	private void wrongID() {
		dataCollection.wrongID();
	}

	/**
	 * if sensor is available, the settings for this sensor should be visible in
	 * the {@link DataCollectionView}
	 * 
	 * @param json
	 */
	private void handleAvailableSensors(JavaScriptObject json) {
		if (JSON.get(json, Types.ACCELEROMETER_X.toString()) != null) {
			dataCollection.sensorActive(
					Types.ACCELEROMETER_X,
					Boolean.parseBoolean(JSON.get(json,
					Types.ACCELEROMETER_X.toString())));
		}
		if (JSON.get(json, Types.MAGNETIC_FIELD_X.toString()) != null) {
			dataCollection.sensorActive(
					Types.MAGNETIC_FIELD_X,
					Boolean.parseBoolean(JSON.get(json,
							Types.MAGNETIC_FIELD_X.toString())));
		}
		if (JSON.get(json, Types.ORIENTATION_X.toString()) != null) {
			dataCollection.sensorActive(
					Types.ORIENTATION_X,
					Boolean.parseBoolean(JSON.get(json,
							Types.ORIENTATION_X.toString())));
		}
		if (JSON.get(json, Types.LOUDNESS.toString()) != null) {
			dataCollection.sensorActive(
					Types.LOUDNESS,
					Boolean.parseBoolean(JSON.get(json,
							Types.LOUDNESS.toString())));
		}
		if (JSON.get(json, Types.PROXIMITY.toString()) != null) {
			dataCollection.sensorActive(
					Types.PROXIMITY,
					Boolean.parseBoolean(JSON.get(json,
							Types.PROXIMITY.toString())));
		}
		if (JSON.get(json, Types.LIGHT.toString()) != null) {
			dataCollection.sensorActive(Types.LIGHT, Boolean.parseBoolean(JSON
					.get(json, Types.LIGHT.toString())));
		}
	}
}
