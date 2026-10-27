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

package org.geogebra.common.properties.aliases;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.properties.EnumeratedProperty;

/**
 * A property whose value is a color.
 */
public interface ColorProperty extends EnumeratedProperty<GColor> {

	/**
	 * Whether {@link #getValue()} is known to be shared by every target of this property.
	 * Properties returning a representative value for multiple targets should return false
	 * unless they can establish that all targets have the same value.
	 * @return whether the current value is known to be uniform
	 */
	default boolean hasUniformValue() {
		return true;
	}
}
