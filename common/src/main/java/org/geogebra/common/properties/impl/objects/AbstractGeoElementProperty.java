package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.properties.GeoElementProperty;
import org.geogebra.common.properties.impl.AbstractProperty;

/**
 * Holds the reference to the GeoElement and to the name of the property.
 */
public abstract class AbstractGeoElementProperty extends AbstractProperty
		implements GeoElementProperty {

	private GeoElement geoElement;

	protected AbstractGeoElementProperty(String name, GeoElement geoElement)
			throws NotApplicablePropertyException {
		super(geoElement.getKernel().getLocalization(), name);
		this.geoElement = geoElement;
		if (!isApplicableTo(geoElement)) {
			throw new NotApplicablePropertyException(geoElement, this);
		}
	}

	/**
	 * @param element Element with properties.
	 * @return True if the element has the property.
	 */
	abstract boolean isApplicableTo(GeoElement element);

	GeoElement getElement() {
		return geoElement;
	}

	@Override
	public boolean isEnabled() {
		return geoElement.isEuclidianVisible();
	}

	boolean isApplicableToGeoList(GeoList list) {
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
