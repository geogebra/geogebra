package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoElement;

public class FixObjectModel extends BooleanOptionModel {

	private static final long serialVersionUID = 1L;

	public FixObjectModel(IBooleanOptionListener listener) {
		super(listener);
	}

	public void apply(int index, boolean value) {
		GeoElement geo = getGeoAt(index);
		geo.setFixed(value);
		geo.updateRepaint();
	}

	@Override
	public boolean isValidAt(int index) {
		return getGeoAt(index).isFixable();
	}

	@Override
	public boolean getValueAt(int index) {
		return getGeoAt(index).isFixed();
	}
}
