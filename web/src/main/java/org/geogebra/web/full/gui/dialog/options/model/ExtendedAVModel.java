package org.geogebra.web.full.gui.dialog.options.model;

import org.geogebra.common.gui.dialog.options.model.BooleanOptionModel;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasExtendedAV;
import org.geogebra.common.main.App;

public class ExtendedAVModel extends BooleanOptionModel {

	public ExtendedAVModel(IBooleanOptionListener listener, App app) {
		super(listener, app);
	}

	@Override
	public boolean isValidAt(int index) {
		return getGeoAt(index) instanceof HasExtendedAV;
	}

	@Override
	public boolean getValueAt(int index) {
		return isValidAt(index)
				&& ((HasExtendedAV) getGeoAt(index)).isAVSliderOrCheckboxVisible();
	}

	@Override
	public void apply(int index, boolean value) {
		if (isValidAt(index)) {
			GeoElement geo = getGeoAt(index);
			((HasExtendedAV) geo).setAVSliderOrCheckboxVisible(value);
			geo.updateVisualStyleRepaint(GProperty.COMBINED);
		}
	}

	@Override
	public String getTitle() {
		return "ShowSliderInAlgebraView";
	}

}
