package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;

public class SymbolicModel extends BooleanOptionModel {

	public SymbolicModel(App app) {
		super(null, app);
	}

	@Override
	public boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index).getGeoElementForPropertiesDialog();

		return geo.isGeoNumeric() || geo.isGeoText() ||
				isSymbolicGeoInputBox(geo);
	}

	private boolean isSymbolicGeoInputBox(GeoElement geo) {
		if (!(app.has(Feature.SYMBOLIC_INPUTFIELDS))) {
			return false;
		}

		return geo.isGeoInputBox() && ((GeoInputBox)geo).canBeSymbolic();
	}

	@Override
	public boolean getValueAt(int index) {
		// not used as updateProperties is overridden.
		return getObjectAt(index) instanceof HasSymbolicMode
				&& ((HasSymbolicMode) getObjectAt(index)).isSymbolicMode();
	}

	@Override
	public void apply(int index, boolean value) {
		HasSymbolicMode geo = (HasSymbolicMode) getObjectAt(index);
		geo.setSymbolicMode(value, true);
		geo.updateRepaint();

	}

}
