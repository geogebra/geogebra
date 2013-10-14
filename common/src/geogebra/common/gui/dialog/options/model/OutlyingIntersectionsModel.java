package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.LimitedPath;

public class OutlyingIntersectionsModel extends BooleanOptionModel {

	public OutlyingIntersectionsModel(IBooleanOptionListener listener) {
		super(listener);
	}

	@Override
	public void updateProperties() {
		Object[] geos = getGeos();
		LimitedPath temp, geo0 = (LimitedPath) geos[0];
		boolean equalVal = true;

		for (int i = 0; i < geos.length; i++) {
			temp = (LimitedPath) geos[i];
			// same value?
			if (geo0.allowOutlyingIntersections() != temp
					.allowOutlyingIntersections())
				equalVal = false;
		}

	
		getListener().updateCheckbox(equalVal ? geo0.allowOutlyingIntersections():
			false);

	}

	public void applyChanges(boolean value) {
		Object[] geos = getGeos();
		for (int i = 0; i < getGeosLength(); i++) {
			LimitedPath geo = (LimitedPath) geos[i];
			geo.setAllowOutlyingIntersections(value);
			geo.toGeoElement().updateRepaint();

		}	
	}
	
	@Override
	public boolean checkGeos() {
		Object[] geos = getGeos();		
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (!(geo instanceof LimitedPath))
					return false;
			}
			return true;
	}
}

