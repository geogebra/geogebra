package org.geogebra.common.kernel.construction;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Creates default GeoElement objects for CAS
 */
public class CasGeoElementFactory implements GeoElementFactory {

	private GeoElementFactory defaultGeoElementFactory;

	public CasGeoElementFactory(Construction construction) {
		defaultGeoElementFactory = new DefaultGeoElementFactory(construction);
	}

	@Override
	public GeoNumeric createNumeric() {
		GeoNumeric numeric = defaultGeoElementFactory.createNumeric();
		numeric.setDrawable(false);
		return numeric;
	}

	@Override
	public GeoAngle createAngle() {
		GeoAngle angle = defaultGeoElementFactory.createAngle();
		angle.setDrawable(false);
		return angle;
	}

	@Override
	public GeoElementPropertyInitializer getPropertyInitializer() {
		return defaultGeoElementFactory.getPropertyInitializer();
	}
}
