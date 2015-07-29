package org.geogebra.web.web.gui.view.dataCollection.Settings;

import org.geogebra.common.plugin.SensorLogger.Types;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.view.dataCollection.DataCollectionView;

/**
 * Settings for sensor Microphone
 */
public class LoudnessSetting extends SensorSetting {

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
	public LoudnessSetting(AppW app, DataCollectionView view,
			String captionString, String unit) {
		super(app, view, captionString, unit);
	}

	@Override
	protected void addContent() {
		addRow(app.getMenu("Loudness") + ":", Types.LOUDNESS);
	}
}
