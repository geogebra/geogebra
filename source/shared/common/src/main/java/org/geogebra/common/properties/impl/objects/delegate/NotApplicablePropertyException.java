package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;

public class NotApplicablePropertyException extends Exception {

	private final String message;

	NotApplicablePropertyException(GeoElement element, GeoElementDelegate property) {
		message = "The property " + property.getClass().getSimpleName()
				+ " cannot be applied to the element with label " + element.getLabelSimple();
	}

	/**
	 * @param element element to which a property can't be applied
	 */
	public NotApplicablePropertyException(GeoElement element) {
		message = "The property "
				+ " cannot be applied to the element with label " + element.getLabelSimple();
	}

	@Override
	public String getMessage() {
		return message;
	}
}
