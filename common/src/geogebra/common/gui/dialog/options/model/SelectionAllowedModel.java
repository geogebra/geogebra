package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoElement;

public class SelectionAllowedModel extends BooleanOptionModel {

	public SelectionAllowedModel(IBooleanOptionListener listener) {
		super(listener);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyChanges(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setSelectionAllowed(value);
			geo.updateRepaint();
		}
	}

	@Override
	public void updateProperties() {
		GeoElement geo0 = getGeoAt(0);
		boolean isEqual = true;

		for (int i = 1; i < getGeosLength(); i++) {
			if (geo0.isSelectionAllowed() != getGeoAt(i).isSelectionAllowed()) {
				isEqual = false;
				break;
			}

		}

		getListener().updateCheckbox(isEqual ? geo0.isSelectionAllowed(): false);


	}

	@Override
	public boolean checkGeos() {
		return true;
	}

}
