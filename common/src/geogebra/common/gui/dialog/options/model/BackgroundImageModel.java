package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoImage;

public class BackgroundImageModel extends OptionsModel {
	public interface IBackroundImageListener {
		void updateCheckbox(boolean equalIsBGimage);
	}

	private static final long serialVersionUID = 1L;
	private IBackroundImageListener listener;

	public BackgroundImageModel(IBackroundImageListener listener) {
		this.listener = listener;
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
		listener.updateCheckbox(equalIsBGimage);

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


