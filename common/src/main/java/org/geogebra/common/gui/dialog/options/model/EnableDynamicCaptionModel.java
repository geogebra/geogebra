package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.HasDynamicCaption;
import org.geogebra.common.main.App;

public class EnableDynamicCaptionModel extends BooleanOptionModel {

	public EnableDynamicCaptionModel(IBooleanOptionListener listener, App app) {
		super(listener, app);
	}

	private HasDynamicCaption at(int index) {
		return (HasDynamicCaption) getObjectAt(index);
	}

	@Override
	public boolean getValueAt(int index) {
		return at(index).hasDynamicCaption();
	}

	@Override
	public boolean isValidAt(int index) {
		return getObjectAt(index) instanceof HasDynamicCaption;
	}

	@Override
	public void apply(int index, boolean value) {
		HasDynamicCaption asGeoText = at(index);
		if (value) {
			asGeoText.clearDynamicCaption();
		} else {
			asGeoText.removeDynamicCaption();
		}

		asGeoText.updateRepaint();
	}
}
