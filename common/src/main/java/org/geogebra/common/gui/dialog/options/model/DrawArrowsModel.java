package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.main.App;

public class DrawArrowsModel extends BooleanOptionModel {

	public DrawArrowsModel(IBooleanOptionListener listener, App app) {
		super(listener, app);
	}

	@Override
	public boolean getValueAt(int index) {
		return getGeoAt(index).isSelectionAllowed(null);
	}

	@Override
	public void apply(int index, boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (geo instanceof GeoLocus) {
				((GeoLocus) geo).drawAsArrows(value);
				geo.updateRepaint();
			}
		}
		storeUndoInfo();
	}

	@Override
	protected boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		return geo instanceof GeoLocus && ((GeoLocus)geo).hasDrawArrows();
	}

	@Override
	public void updateProperties() {
		if (getListener() != null) {
			getListener().updateCheckbox(checkDrawArrow());
		}
	}

	private boolean checkDrawArrow() {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (geo instanceof GeoLocus) {
				return geo.isDrawArrows();
			}
		}
		return false;
	}

}
