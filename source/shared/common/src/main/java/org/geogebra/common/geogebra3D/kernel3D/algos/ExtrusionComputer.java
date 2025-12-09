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

package org.geogebra.common.geogebra3D.kernel3D.algos;

/**
 * computer used for extrusions (prism, cylinder)
 * 
 * @author Mathieu
 *
 */
public class ExtrusionComputer {

    private static final int MIN_COMPUTATIONS_VALIDATING_DRAGGING = 1;
	private static final int MIN_COMPUTATIONS_VALIDATING_DRAGGING_AR = 15;

	private AlgoForExtrusion algo;
	private int computed;

	/**
	 * 
	 * @param algo
	 *            algorithm
	 */
	public ExtrusionComputer(AlgoForExtrusion algo) {
		this.algo = algo;
		algo.setExtrusionComputer(this);
		computed = 0;
	}

	/**
	 * compute the algo
	 */
	public void compute() {
		computed++;
	}

	/**
	 * 
	 * @return if computed at least once
	 */
	public boolean getWasComputedByDragging(boolean isAREnabled) {
		return computed > getComputationsThreshold(isAREnabled);
	}

	/**
	 * @return the algo
	 */
	public AlgoForExtrusion getAlgo() {
		return algo;
	}

	private int getComputationsThreshold(boolean isAREnabled) {
		return isAREnabled ? MIN_COMPUTATIONS_VALIDATING_DRAGGING_AR
				: MIN_COMPUTATIONS_VALIDATING_DRAGGING;
	}

}
