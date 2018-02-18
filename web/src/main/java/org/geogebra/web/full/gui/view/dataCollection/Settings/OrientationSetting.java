package org.geogebra.web.full.gui.view.dataCollection.Settings;

import org.geogebra.common.plugin.SensorLogger.Types;
import org.geogebra.web.full.gui.view.dataCollection.DataCollectionView;
import org.geogebra.web.html5.main.AppW;

import com.himamis.retex.editor.share.util.Unicode;

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
	 * @param unit
	 *            unit of the sensor values
	 */
	public OrientationSetting(AppW app, DataCollectionView view,
			String captionString, String unit) {
		super(app, view, captionString, unit);
	}

	@Override
	protected void addContent() {
		addRow(Unicode.alpha + "", Types.ORIENTATION_X);
		addRow(Unicode.beta + "", Types.ORIENTATION_Y);
		addRow(Unicode.gamma + "", Types.ORIENTATION_Z);
	}
}
