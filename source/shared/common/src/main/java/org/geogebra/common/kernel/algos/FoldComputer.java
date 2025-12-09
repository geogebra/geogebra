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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;

/**
 * Sum command helper
 * 
 * @author Zbynek
 *
 */
public interface FoldComputer {

	/**
	 * Create element of the resulting type.
	 * @param cons construction
	 * @param listElement element class of the folded list
	 * @return template element
	 */
	GeoElement getTemplate(Construction cons, GeoClass listElement);

	/**
	 * Apply operation to current state and given element.
	 * @param geoElement element
	 * @param op operation
	 */
	void add(GeoElement geoElement, Operation op);

	/**
	 * @param geoElement element
	 * @param kernel kernel
	 */
	void setFrom(GeoElement geoElement, Kernel kernel);

	/**
	 * @param geoElement construction element
	 * @return whether it can be used as argument
	 */
	boolean check(GeoElement geoElement);

	/**
	 * Update result after last computation.
	 */
	void finish();

}