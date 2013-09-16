package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoElement;

public class AuxObjectModel extends OptionsModel {
	public interface IAuxObjectListener {
		void updateCheckbox(boolean equalObjectVal);
	}

	private static final long serialVersionUID = 1L;
	private IAuxObjectListener listener;

	public AuxObjectModel(IAuxObjectListener listener) {
		this.listener = listener;
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
		listener.updateCheckbox(equalAux);

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
