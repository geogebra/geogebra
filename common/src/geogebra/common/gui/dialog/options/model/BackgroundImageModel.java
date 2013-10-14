package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoImage;

public class BackgroundImageModel extends BooleanOptionModel {
	
	public BackgroundImageModel(IBooleanOptionListener listener) {
		super(listener);
	}

	public void applyChanges(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoImage geo = (GeoImage) getGeoAt(i);
			geo.setInBackground(value);
			geo.updateRepaint();
		}
	}

	@Override
	public void updateProperties() {
		// TODO Auto-generated method stub
		GeoImage temp, geo0 = (GeoImage)getGeoAt(0);
		boolean equalIsBGimage = true;

		for (int i = 0; i < getGeosLength(); i++) {
			temp = (GeoImage) getGeoAt(i);
			// same object visible value
			if (geo0.isInBackground() != temp.isInBackground())
				equalIsBGimage = false;
		}
		getListener().updateCheckbox(equalIsBGimage ? geo0.isInBackground():false);

	}

	@Override
	public boolean checkGeos() {
		for (int i = 0; i < getGeosLength(); i++) {
			if (!(getGeoAt(i) instanceof GeoImage)) {
				return false;
			}

		}

		return true;
	}
}


