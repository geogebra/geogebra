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

package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Filter for algebra output formats.
 */
public interface AlgebraOutputFormatFilter {
	/**
	 * Determines whether the specific format is allowed for the given element by this filter.
	 * @param geoElement the element for which the format is evaluated
	 * @param outputFormat the format to be evaluated
	 * @return {@code true} if the given format is allowed
	 * for the given element, {@code false} otherwise
	 */
	boolean isAllowed(GeoElement geoElement, AlgebraOutputFormat outputFormat);
}
