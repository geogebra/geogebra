package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;

public class AnimatingModel extends BooleanOptionModel {
	private App app;
	private Kernel kernel;
	
	public AnimatingModel(App app, IBooleanOptionListener listener) {
		super(listener);
		this.app = app;
		kernel = app.getKernel();
	}

	@Override
	public void updateProperties() {
		// check if properties have same values
		GeoElement temp, geo0 = getGeoAt(0);
		boolean equalAnimating = true;

		for (int i = 1; i < getGeosLength(); i++) {
			temp = getGeoAt(i);
			// same object visible value
			if (geo0.isAnimating() != temp.isAnimating())
				equalAnimating = false;
		}
		getListener().updateCheckbox(equalAnimating ? geo0.isAnimating(): false);
	}

	public void applyChanges(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setAnimating(value);
			geo.updateRepaint();
		}
		if (value)
			kernel.getAnimatonManager().startAnimation();
}
	
	@Override
	public boolean checkGeos() {
		boolean geosOK = true;
		for (int i = 0; i < getGeosLength(); i++) {
			if (!getGeoAt(i).isAnimatable()) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	
	}
}

