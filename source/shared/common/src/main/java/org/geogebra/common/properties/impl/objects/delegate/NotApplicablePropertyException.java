/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
