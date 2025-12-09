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

package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;

/**
 * Abstract class for algos that use some CAS algo as helper
 */
public abstract class AlgoUsingTempCASalgo extends AlgoElement
		implements UsesCAS {
	/**
	 * CAS algo helper
	 */
	protected AlgoElement algoCAS;

	/**
	 * @param c
	 *            construction
	 */
	public AlgoUsingTempCASalgo(Construction c) {
		super(c);
	}

	/**
	 * @param c
	 *            construction
	 * @param addToConstructionList
	 *            whether we want this in construction list
	 */
	public AlgoUsingTempCASalgo(Construction c, boolean addToConstructionList) {
		super(c, addToConstructionList);
	}

	@Override
	public void remove() {
		if (removed) {
			return;
		}
		super.remove();
		if (algoCAS != null) {
			algoCAS.remove();
		}
	}

	/**
	 * Creates a temporary CAS algorithm, computes it, stores the results and
	 * deletes the algorithm from the construction
	 */
	public abstract void refreshCASResults();

}
