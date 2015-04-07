package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoElement;

public class AuxObjectModel extends BooleanOptionModel {

	private static final long serialVersionUID = 1L;

	public AuxObjectModel(IBooleanOptionListener listener) {
		super(listener);
	}

	public void apply(int index, boolean value) {
		GeoElement geo = getGeoAt(index);
		geo.setAuxiliaryObject(value);
		geo.updateRepaint();
	}

	@Override
	public boolean getValueAt(int index) {
		return getGeoAt(index).isAuxiliaryObject();

	}

	@Override
	public boolean isValidAt(int index) {
			return getGeoAt(index).isAlgebraVisible();
		
	}
}
