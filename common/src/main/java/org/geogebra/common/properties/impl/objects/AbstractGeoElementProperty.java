package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.GeoElementProperty;

/**
 * Holds the reference to the GeoElement and to the name of the property.
 */
public abstract class AbstractGeoElementProperty implements GeoElementProperty {

	private String name;
	private GeoElement geoElement;

	protected AbstractGeoElementProperty(String name, GeoElement geoElement) {
		this.name = name;
		this.geoElement = geoElement;
	}

	@Override
	public String getName() {
		return name;
	}

	GeoElement getElement() {
		return geoElement;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
