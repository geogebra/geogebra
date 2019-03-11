package org.geogebra.common.spy.construction;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.GeoElement;

class ConstructionDefaultsSpy extends ConstructionDefaults {

	ConstructionDefaultsSpy(Construction cons2) {
		super(cons2);
	}

	@Override
	public void setDefaultVisualStyles(
			GeoElement geo,
			boolean isReset, boolean setEuclidianVisible, boolean setAuxiliaryProperty) {

	}
}
