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

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Filter that determines whether a given {@link Property}
 * is allowed for a specific {@link GeoElement}.
 */
@FunctionalInterface
public interface GeoElementPropertyFilter {
    /**
     * Determines if the specified {@link Property} is allowed for the given {@link GeoElement}.
     * @param property the {@link Property} to be evaluated
     * @param geoElement the {@link GeoElement} to which the property applies
     * @return {@code true} if the property is allowed for the geo element, {@code false} otherwise
     */
    boolean isAllowed(Property property, GeoElement geoElement);
}
