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

package org.geogebra.common.exam.restrictions.ib;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.main.syntax.suggestionfilter.LineSelectorSyntaxFilter;

/**
 * Additional syntax restrictions for the IB exam
 */
public final class IBSyntaxFilter extends LineSelectorSyntaxFilter {

	/**
	 * Create the filter.
	 */
	public IBSyntaxFilter() {
		addSelector(Commands.Integral, 2);
		addSelector(Commands.Invert, 0);
		addSelector(Commands.Tangent, 1, 3);
	}
}
