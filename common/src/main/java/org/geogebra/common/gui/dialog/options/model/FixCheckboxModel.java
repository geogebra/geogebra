package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.main.App;

public class FixCheckboxModel extends BooleanOptionModel {

	public FixCheckboxModel(IBooleanOptionListener listener, App app) {
		super(listener, app);
	}

	private GeoBoolean getBooleanAt(int index) {
		return (GeoBoolean) getObjectAt(index);

	}

	@Override
	public boolean getValueAt(int index) {
		return getBooleanAt(index).isLockedPosition();

	}

	@Override
	public boolean isValidAt(int index) {
		Object geo = getObjectAt(index);
		if (geo instanceof GeoBoolean) {
			GeoBoolean bool = (GeoBoolean) geo;
			if (!bool.isIndependent()) {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	@Override
	public void apply(int index, boolean value) {
		GeoBoolean bool = getBooleanAt(index);
		bool.setCheckboxFixed(value);
		bool.updateRepaint();
	}
}
