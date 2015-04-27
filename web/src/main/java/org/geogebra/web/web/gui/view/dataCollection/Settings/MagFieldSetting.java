package org.geogebra.web.web.gui.view.dataCollection.Settings;

import org.geogebra.common.plugin.SensorLogger.Types;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.view.dataCollection.DataCollectionView;

public class MagFieldSetting extends SensorSetting {

	/**
	 * @param app
	 * @param view
	 * @param captionString
	 */
	public MagFieldSetting(AppW app, DataCollectionView view,
			String captionString) {
		super(app, view, captionString);
	}

	@Override
	protected void addContent() {
		addRow("x:", Types.MAGNETIC_FIELD_X);
		addRow("y:", Types.MAGNETIC_FIELD_Y);
		addRow("z:", Types.MAGNETIC_FIELD_Z);
	}
}
