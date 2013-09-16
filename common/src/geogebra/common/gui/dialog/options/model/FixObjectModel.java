package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoElement;

public class FixObjectModel extends OptionsModel {
	public interface IFixObjectListener {
		void updateCheckbox(boolean equalObjectVal);
	}

	private static final long serialVersionUID = 1L;
	private IFixObjectListener listener;

	public FixObjectModel(IFixObjectListener listener) {
		this.listener = listener;
	}

	public void applyChanges(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setFixed(value);
			geo.updateRepaint();
		}
	}

	@Override
	public void updateProperties() {
		// TODO Auto-generated method stub
		GeoElement temp, geo0 = getGeoAt(0);
		boolean equalFix = true;

		for (int i = 0; i < getGeosLength(); i++) {
			temp = (GeoElement) getGeoAt(i);
			// same object visible value
			if (geo0.isFixed() != temp.isFixed())
				equalFix = false;
		}
		listener.updateCheckbox(equalFix);

	}

	@Override
	public boolean checkGeos() {
		for (int i = 0; i < getGeosLength(); i++) {
			if (!getGeoAt(i).isFixable()) {
				return false;
			}

		}

		return true;
	}
}
