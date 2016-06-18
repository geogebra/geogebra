package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoElement;

public class SelectionAllowedModel extends BooleanOptionModel {

	public SelectionAllowedModel(IBooleanOptionListener listener) {
		super(listener);
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
