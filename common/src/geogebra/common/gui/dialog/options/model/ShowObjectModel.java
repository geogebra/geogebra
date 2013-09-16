package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoElement;

public class ShowObjectModel extends OptionsModel {
	public interface IShowObjectListener {
		void updateCheckbox(boolean equalObjectVal, boolean showObjectCondition);

	}
	
	private IShowObjectListener listener;
	
	public ShowObjectModel(IShowObjectListener listener) {
		this.listener = listener;
	}
	
	
	public void applyChanges(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setEuclidianVisible(value);
			geo.updateRepaint();
		}
		
	}
	
	@Override
	public void updateProperties() {
		// check if properties have same values
		GeoElement temp, geo0 = getGeoAt(0);
		boolean equalObjectVal = true;
		boolean showObjectCondition = geo0.getShowObjectCondition() != null;

		for (int i = 1; i < getGeosLength(); i++) {
			temp = getGeoAt(i);
			// same object visible value
			if (geo0.isSetEuclidianVisible() != temp
					.isSetEuclidianVisible()) {
				equalObjectVal = false;
				break;
			}

			if (temp.getShowObjectCondition() != null) {
				showObjectCondition = true;
			}
		}
		
		listener.updateCheckbox(equalObjectVal, showObjectCondition);

	}
	
	@Override
	public boolean checkGeos() {
		
		boolean geosOK = true;
		for (Object temp: getGeos()) {
			GeoElement geo = (GeoElement) temp;
			 if (!geo.isDrawable()
			// can't allow a free fixed number to become visible (as a
			// slider)
					|| (geo.isGeoNumeric() && geo.isFixed())) {
				geosOK = false;
				break;
			}
			
		}
		return geosOK;
	}

}