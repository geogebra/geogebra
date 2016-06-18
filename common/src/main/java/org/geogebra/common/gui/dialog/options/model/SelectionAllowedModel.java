package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

public class SelectionAllowedModel extends BooleanOptionModel {

	public SelectionAllowedModel(IBooleanOptionListener listener, App app) {
		super(listener, app);
	}

	@Override
	public boolean checkGeos() {
		return true;
	}

	@Override
	public boolean getValueAt(int index) {
		return getGeoAt(index).isSelectionAllowed(null);
	}

	@Override
	public void apply(int index, boolean value) {
		GeoElement geo = getGeoAt(index);
		geo.setSelectionAllowed(value);
		geo.updateRepaint();

	}

	@Override
	protected boolean isValidAt(int index) {
		// TODO Auto-generated method stub
		return false;
	}

}
