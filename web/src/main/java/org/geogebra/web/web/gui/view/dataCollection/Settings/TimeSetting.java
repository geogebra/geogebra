package org.geogebra.web.web.gui.view.dataCollection.Settings;

import org.geogebra.common.plugin.SensorLogger.Types;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.view.dataCollection.DataCollectionView;

/**
 * Settings for Time + Datacount
 */
public class TimeSetting extends SensorSetting {

	/**
	 * 
	 * @param app
	 *            {@link AppW}
	 * @param view
	 *            {@link DataCollectionView}
	 * @param captionString
	 *            the String to look up for translations
	 * @param unit
	 *            unit of the sensor values
	 */
	public TimeSetting(AppW app, DataCollectionView view, String captionString,
			String unit) {
		super(app, view, captionString, unit);
	}

	@Override
	protected void addFrequencyPanel() {
		// no frequency panel
	}

	@Override
	protected void addContent() {
		addRow(app.getMenu("Timestamp") + ":", Types.TIMESTAMP);
		addRow(app.getMenu("DataCount") + ":", Types.DATA_COUNT);
	}
}
