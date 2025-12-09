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

/**
 * Common algo for Intersect
 * 
 * + used for trimmed intersections
 * 
 * + used for finding pre-existent intersect algos
 */
public abstract class AlgoIntersectAbstract extends AlgoElement {

	/**
	 * @param cons
	 *            construction
	 */
	public AlgoIntersectAbstract(Construction cons) {
		super(cons);
	}

	/**
	 * @param cons
	 *            construction
	 * @param addToConstructionList
	 *            whether to add this to XML
	 */
	public AlgoIntersectAbstract(Construction cons,
			boolean addToConstructionList) {
		super(cons, addToConstructionList);
	}

	/*
	 * This should apply to every subclass. In case it does not, a case per case
	 * should be used. Luckily, some are already implemented.
	 */

}
