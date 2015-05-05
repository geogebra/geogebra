package org.geogebra.web.web.gui.view.dataCollection.Settings;

import org.geogebra.common.plugin.SensorLogger.Types;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.view.dataCollection.DataCollectionView;

/**
 * Settings for sensor Light
 */
public class LightSetting extends SensorSetting {

	/**
	 * 
	 * @param app
	 *            {@link AppW}
	 * @param view
	 *            {@link DataCollectionView}
	 * @param captionString
	 *            the String to look up for translations
	 */
	public LightSetting(AppW app, DataCollectionView view,
			String captionString) {
		super(app, view, captionString);
	}

	@Override
	protected void addContent() {
		addRow("light:", Types.LIGHT);
	}
}
