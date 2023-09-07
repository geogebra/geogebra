package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

public class SelectionAllowedModel extends BooleanOptionModel {

	public SelectionAllowedModel(IBooleanOptionListener listener, App app) {
		super(listener, app);
	}

	@Override
	public boolean getValueAt(int index) {
		return getGeoAt(index).isSelectionAllowed(null);
	}

	@Override
	public void apply(int index, boolean value) {
		GeoElement geo = getGeoAt(index);
		geo.setSelectionAllowed(value);
		geo.updateVisualStyleRepaint(GProperty.COMBINED);
		// do NOT unselect here to allow changing moore properties in settings dialog
	}

	/**
	 * Change selection property of an object, update sleection
	 * @param geo construction element
	 * @param app application
	 * @param allowSelection whether to allow selection
	 */
	public static void applyTo(GeoElement geo, App app, boolean allowSelection) {
		geo.setSelectionAllowed(allowSelection);
		if (!allowSelection) {
			app.getSelectionManager().removeSelectedGeo(geo);
		}
		geo.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public String getTitle() {
		return "SelectionAllowed";
	}

	@Override
	protected boolean isValidAt(int index) {
		return true;
	}

}
