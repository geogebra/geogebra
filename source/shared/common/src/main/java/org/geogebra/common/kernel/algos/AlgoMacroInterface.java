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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Interface for macro algorithm (to separate Geos from Algos)
 *
 */
public interface AlgoMacroInterface {
	/**
	 * Replace references to macro geos in function
	 * 
	 * @param f
	 *            function
	 */
	public void initFunction(FunctionNVar f);

	/**
	 * Replace references to macro geos in list
	 * 
	 * @param l
	 *            macro list
	 * @param geoList
	 *            parent construction list
	 */
	public void initList(GeoList l, GeoList geoList);

	/**
	 * Compares drawing priority of two elements
	 * 
	 * @param geoElement
	 *            first element
	 * @param other
	 *            second element
	 * @return whether geoElement should be drawn before other
	 */
	public int drawBefore(GeoElement geoElement, GeoElement other);
}
