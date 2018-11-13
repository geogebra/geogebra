package org.geogebra.common.geogebra3D.kernel3D.algos;

/**
 * computer used for extrusions (prism, cylinder)
 * 
 * @author Mathieu
 *
 */
public class ExtrusionComputer {
    
    private static int MIN_COMPUTATIONS_VALIDATING_DRAGGING = 1;

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
	public boolean getWasComputedByDragging() {
		return computed > MIN_COMPUTATIONS_VALIDATING_DRAGGING;
	}

	/**
	 * @return the algo
	 */
	public AlgoForExtrusion getAlgo() {
		return algo;
	}

}
