package org.geogebra.web.web.gui.view.dataCollection.Settings;

import org.geogebra.common.plugin.SensorLogger.Types;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.view.dataCollection.DataCollectionView;


/**
 * @author geogebra
 *
 */
public class AccSetting extends SensorSetting {

	/**
	 * @param app
	 * @param view
	 * @param captionString
	 */
	public AccSetting(AppW app, DataCollectionView view, String captionString) {
		super(app, view, captionString);
	}

	@Override
	protected void addContent() {
		addRow("x:", Types.ACCELEROMETER_X);
		addRow("y:", Types.ACCELEROMETER_Y);
		addRow("z:", Types.ACCELEROMETER_Z);
	}
}
