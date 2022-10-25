package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

public class AuxObjectModel extends BooleanOptionModel {

	public AuxObjectModel(IBooleanOptionListener listener, App app) {
		super(listener, app);
	}

	@Override
	public void apply(int index, boolean value) {
		GeoElement geo = getGeoAt(index);
		geo.setAuxiliaryObject(value);
		geo.updateRepaint();
		storeUndoInfo();
	}

	@Override
	public String getTitle() {
		return "AuxiliaryObject";
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
