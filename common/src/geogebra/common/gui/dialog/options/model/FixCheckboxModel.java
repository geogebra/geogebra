package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoBoolean;

public class FixCheckboxModel extends BooleanOptionModel {

	public FixCheckboxModel(IBooleanOptionListener listener) {
		super(listener);
	}

	@Override
	public void updateProperties() {
		// check if properties have same values
		Object[] geos = getGeos();
		GeoBoolean temp, geo0 = (GeoBoolean) geos[0];
		boolean isEqual = true;

		for (int i = 1; i < geos.length; i++) {
			temp = (GeoBoolean) geos[i];
			// same object visible value
			if (geo0.isCheckboxFixed() != temp.isCheckboxFixed()) {
				isEqual = false;
				break;
			}
		}
		
		getListener().updateCheckbox(isEqual ? geo0.isCheckboxFixed(): false);
	}

	public void applyChanges(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoBoolean bool = (GeoBoolean) getGeoAt(i);
			bool.setCheckboxFixed(value);
			bool.updateRepaint();

		}
}
	
	@Override
	public boolean checkGeos() {
		for (int i = 0; i < getGeosLength(); i++) {
			Object geo = getGeoAt(i);
			if (geo instanceof GeoBoolean) {
				GeoBoolean bool = (GeoBoolean) geo;
				if (!bool.isIndependent()) {
					return false;
				}
			} else
				return false;
		}
		return true;
		}
}

