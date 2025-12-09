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

import javax.annotation.CheckForNull;

/**
 * A property whose values are constrained to a subset of all possible values.
 * Any possible value can be validated against this property
 * using {@link ConstrainedProperty#validateValue(Object)}.
 * @param <V> the type of the value
 */
public interface ConstrainedProperty<V> extends ValuedProperty<V> {

	/**
	 * Validates a value for this property. If the value is valid, it returns a null,
	 * otherwise it returns a localized error message.
	 * @param value value to validate for this property
	 * @return null if the value is valid, otherwise a localized error message
	 */
	@CheckForNull String validateValue(V value);

}
