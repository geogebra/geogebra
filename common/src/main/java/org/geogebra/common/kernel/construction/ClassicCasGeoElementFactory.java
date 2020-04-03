package org.geogebra.common.kernel.construction;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoNumeric;

public class ClassicCasGeoElementFactory implements GeoElementFactory {

	private GeoElementFactory defaultGeoElementFactory;

	public ClassicCasGeoElementFactory(Construction construction) {
		defaultGeoElementFactory = new DefaultGeoElementFactory(construction);
	}

	@Override
	public GeoNumeric createNumeric() {
		return defaultGeoElementFactory.createNumeric();
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
