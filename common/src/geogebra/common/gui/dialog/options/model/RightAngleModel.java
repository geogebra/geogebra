package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.AngleProperties;

public class RightAngleModel extends BooleanOptionModel {

	public RightAngleModel(IBooleanOptionListener listener) {
		super(listener);
	}

	@Override
	public void updateProperties() {
		AngleProperties geo0 = (AngleProperties) getGeoAt(0);
		getListener().updateCheckbox(geo0.isEmphasizeRightAngle());
	}

	public void applyChanges(boolean value) {
		Object[] geos = getGeos();
		for (int i = 0; i < getGeosLength(); i++) {
			AngleProperties geo = (AngleProperties) geos[i];
			geo.setEmphasizeRightAngle(value);
			geo.updateRepaint();
			}
	}
	@Override
	public boolean checkGeos() {
		boolean geosOK = true;
		Object[] geos = getGeos();
		for (int i = 0; i < getGeosLength(); i++) {
			if (!(geos[i] instanceof AngleProperties)) {
				geosOK = false;
				break;
			}
			/*
			 * // If it isn't a right angle else if
			 * (!Kernel.isEqual(((GeoAngle)geos[i]).getValue(),
			 * Kernel.PI_HALF)){ geosOK=false; break; }
			 */
		}
		return geosOK;
	}

}
