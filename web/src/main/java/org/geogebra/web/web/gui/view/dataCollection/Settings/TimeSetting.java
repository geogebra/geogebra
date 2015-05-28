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
	 */
	public TimeSetting(AppW app, DataCollectionView view, String captionString) {
		super(app, view, captionString);
	}

	@Override
	protected void addContent() {
		addRow(app.getMenu("Time") + ":", Types.TIMESTAMP);
		addRow(app.getMenu("Count") + ":", Types.DATA_COUNT);
	}
}
