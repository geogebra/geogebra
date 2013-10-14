package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoElement;

public class AuxObjectModel extends BooleanOptionModel {

	private static final long serialVersionUID = 1L;

	public AuxObjectModel(IBooleanOptionListener listener) {
		super(listener);
	}

	public void applyChanges(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setAuxiliaryObject(value);
			geo.updateRepaint();
		}
	}

	@Override
	public void updateProperties() {
		// TODO Auto-generated method stub
		GeoElement temp, geo0 = getGeoAt(0);
		boolean equalAux = true;

		for (int i = 0; i < getGeosLength(); i++) {
			temp = (GeoElement) getGeoAt(i);
			// same object visible value
			if (geo0.isAuxiliaryObject() != temp.isAuxiliaryObject())
				equalAux = false;
		}
		getListener().updateCheckbox(equalAux ? geo0.isAuxiliaryObject():false);

	}

	@Override
	public boolean checkGeos() {
		for (int i = 0; i < getGeosLength(); i++) {
			if (!getGeoAt(i).isAlgebraVisible()) {
				return false;
			}

		}

		return true;
	}
}
