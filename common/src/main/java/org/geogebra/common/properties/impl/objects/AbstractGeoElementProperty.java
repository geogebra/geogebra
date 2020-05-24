package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.properties.GeoElementProperty;

/**
 * Holds the reference to the GeoElement and to the name of the property.
 */
public abstract class AbstractGeoElementProperty implements GeoElementProperty {

	private String name;
	private GeoElement geoElement;

	protected AbstractGeoElementProperty(String name, GeoElement geoElement) {
		if (!isApplicableTo(geoElement)) {
			throw new NotApplicablePropertyException(geoElement, this);
		}
		this.name = name;
		this.geoElement = geoElement;
	}

	/**
	 * @param element Element with properties.
	 * @return True if the element has the property.
	 */
	abstract boolean isApplicableTo(GeoElement element);

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

	boolean isApplicableTo(GeoList list) {
		for (int i = 0; i < list.size(); i++) {
			if (!isApplicableTo(list.get(i))) {
				return false;
			}
		}
		return true;
	}

	boolean isTextOrInput(GeoElement element) {
		return element instanceof GeoText || element instanceof GeoInputBox;
	}
}
