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

package org.geogebra.common.properties;

/**
 * A property that runs an action when selected. Some properties do not hold a value as most do,
 * but are associated with an action. This action can be called
 * with {@link ActionableProperty#performAction()}.
 * <p>
 * For example, a property that centers all objects in the view can be of this type.
 */
public interface ActionableProperty extends Property {

	/**
	 * Performs the action associated with this property.
	 */
	void performAction();
}
