package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.AlgebraSettings;

public class FixObjectModel extends BooleanOptionModel {

	public FixObjectModel(IBooleanOptionListener listener, App app) {
		super(listener, app);
	}

	@Override
	public void apply(int index, boolean value) {
		GeoElement geo = getGeoAt(index);
		geo.setFixed(value);
		geo.updateRepaint();
	}

	@Override
	public String getTitle() {
		return "FixObject";
	}

	@Override
	public boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		AlgebraSettings algebraSettings = app.getSettings().getAlgebra();
		return geo.isFixable() && (!algebraSettings.isEquationChangeByDragRestricted()
				|| !geo.isFunctionOrEquationFromUser());
	}

	@Override
	public boolean getValueAt(int index) {
		return getGeoAt(index).isLocked();
	}
}
