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
