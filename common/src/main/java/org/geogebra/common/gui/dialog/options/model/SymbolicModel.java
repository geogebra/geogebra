package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.geogebra.common.kernel.geos.InequalityProperties;

public class SymbolicModel extends BooleanOptionModel {



	public SymbolicModel() {
		super(null);
	}
	
	private InequalityProperties getInequalityPropertiesAt(int index) {
		return (InequalityProperties) getObjectAt(index);
	}



	@Override
	public boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index).getGeoElementForPropertiesDialog();


		return geo.isGeoNumeric();
	}

	@Override
	public boolean getValueAt(int index) {
		// not used as updateProperties is overridden.
		return getObjectAt(index) instanceof HasSymbolicMode
				&& ((HasSymbolicMode) getObjectAt(index)).isSymboicMode();
	}

	@Override
	public void apply(int index, boolean value) {
		HasSymbolicMode geo = (HasSymbolicMode) getObjectAt(index);
		geo.setSymbolicMode(value);
		geo.updateRepaint();

	}

}
