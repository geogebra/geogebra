package org.geogebra.web.web.gui.view.dataCollection.Settings;

import org.geogebra.common.plugin.SensorLogger.Types;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.view.dataCollection.DataCollectionView;

/**
 * Settings for sensor Orientation
 */
public class OrientationSetting extends SensorSetting {

	/**
	 * 
	 * @param app
	 *            {@link AppW}
	 * @param view
	 *            {@link DataCollectionView}
	 * @param captionString
	 *            the String to look up for translations
	 */
	public OrientationSetting(AppW app, DataCollectionView view, String captionString) {
		super(app, view, captionString);
	}

	@Override
	protected void addContent() {
		addRow("x:", Types.ORIENTATION_X);
		addRow("y:", Types.ORIENTATION_Y);
		addRow("z:", Types.ORIENTATION_Z);
	}
}
