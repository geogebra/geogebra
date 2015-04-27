package org.geogebra.web.web.gui.view.dataCollection.Settings;

import org.geogebra.common.plugin.SensorLogger.Types;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.view.dataCollection.DataCollectionView;

public class LightSetting extends SensorSetting {

	/**
	 * @param app
	 * @param view
	 * @param captionString
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
