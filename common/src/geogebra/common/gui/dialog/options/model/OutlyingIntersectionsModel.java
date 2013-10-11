package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.LimitedPath;

public class OutlyingIntersectionsModel extends OptionsModel {
	public interface IOutlyingIntersectionsListener {
		void updateCheckbox(boolean isEqual);
	}
	private IOutlyingIntersectionsListener listener;
	
	public OutlyingIntersectionsModel(IOutlyingIntersectionsListener listener) {
		this.listener = listener;
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

		listener.updateCheckbox(equalVal);

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

