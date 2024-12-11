package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

public class AnimatingModel extends BooleanOptionModel {
	private Kernel kernel;

	public AnimatingModel(App app, IBooleanOptionListener listener) {
		super(listener, app);
		kernel = app.getKernel();
	}

	@Override
	public void applyChanges(boolean value) {
		super.applyChanges(value);
		if (value) {
			kernel.getAnimationManager().startAnimation();
		}
		storeUndoInfo();
	}

	@Override
	public String getTitle() {
		return "Animating";
	}

	@Override
	public boolean isValidAt(int index) {
		return getGeoAt(index).isAnimatable();
	}

	@Override
	public boolean getValueAt(int index) {
		return getGeoAt(index).isAnimating();
	}

	@Override
	public void apply(int index, boolean value) {
		GeoElement geo = getGeoAt(index);
		geo.setAnimating(value);
		geo.updateRepaint();
	}

}
