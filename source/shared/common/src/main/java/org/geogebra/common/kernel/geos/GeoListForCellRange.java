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

package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.statistics.AlgoCellRange;

/**
 * @author mathieu GeoList for AlgoCellRange. We need to propagate add to
 *         addToUpdateSets() to parent algo input items
 *
 */
public class GeoListForCellRange extends GeoList {

	private AlgoCellRange algo;

	/**
	 * constructor
	 * @param c construction
	 * @param algo parent algo
	 */
	public GeoListForCellRange(Construction c, AlgoCellRange algo) {
		super(c);
		this.algo = algo;
	}

	@Override
	public boolean addToUpdateSets(final AlgoElement algorithm) {

		final boolean added = super.addToUpdateSets(algorithm);

		// propagate to algo parent input items
		if (algo != algorithm) {
			algo.addToItemsAlgoUpdateSets(algorithm);
		}

		return added;
	}

	@Override
	protected boolean isElementTypeXMLNeeded() {
		return getTypeStringForXML() != null;
	}
}
