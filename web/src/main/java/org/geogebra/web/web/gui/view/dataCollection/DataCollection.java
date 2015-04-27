package org.geogebra.web.web.gui.view.dataCollection;

import java.util.HashMap;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.plugin.SensorLogger.Types;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.GuiManagerW;

/**
 *
 */
public class DataCollection {

	private DataCollectionLogger sensorLogger;
	private DataCollectionView dataView;
	private AppW app;

	/**
	 * 
	 * @param app
	 *            {@link AppW}
	 */
	public DataCollection(AppW app) {
		this.app = app;
		this.sensorLogger = new DataCollectionLogger(app.getKernel(), this);
		this.dataView = ((GuiManagerW) app.getGuiManager())
				.getDataCollectionView();
	}

	/**
	 * connects the application with the mobile data app
	 * 
	 * @param id
	 *            to connect with
	 */
	public void onConnect(String id) {
		// set app id
		sensorLogger.registerGeo(Types.APP_ID.toString(), new GeoText(app
				.getKernel().getConstruction(), id));
		sensorLogger.createConnection();
	}

	/**
	 * disconnects the application from the websocket
	 */
	public void onDisconnect() {
		sensorLogger.closeSocket();
	}

	/**
	 * starts the data collection
	 */
	public void start() {
		HashMap<Types, GeoElement> activeSensors = this.dataView
				.getActivedSensors();

		for (Types type : activeSensors.keySet()) {
			GeoElement argument = activeSensors.get(type);

			if (argument instanceof GeoNumeric || argument instanceof GeoText) {
				sensorLogger.registerGeo(type.toString(), argument);
			} else if (argument instanceof GeoList) {
				sensorLogger.registerGeoList(type.toString(),
						(GeoList) argument);
			} else if (argument instanceof GeoFunction) {
				sensorLogger.registerGeoFunction(type.toString(),
						(GeoFunction) argument);
			}
			
			if (!argument.isLabelSet()) {
				argument.setLabel(type.toString());
			}
		}
	}

	/**
	 * stops the data collection
	 */
	public void stop() {
		sensorLogger.stopLogging();
	}

	/**
	 * called to update the UI for the given sensor
	 * 
	 * @param sensor
	 *            {@link Types}
	 * @param flag
	 *            {@code true} to show the settings for the sensor in the
	 *            {@link #dataView}
	 */
	public void sensorActive(Types sensor, boolean flag) {
		this.dataView.setVisible(sensor, flag);
	}

	/**
	 * called if no mobile-data app found with the entered appID
	 */
	public void wrongID() {
		this.dataView.onWrongID();
	}
}
