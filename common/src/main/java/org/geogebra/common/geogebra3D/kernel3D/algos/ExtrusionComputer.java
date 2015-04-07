package org.geogebra.common.geogebra3D.kernel3D.algos;

/**
 * computer used for extrusions (prism, cylinder)
 * 
 * @author matthieu
 *
 */
public class ExtrusionComputer {

	private AlgoForExtrusion algo;

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

	private int computed;

	/**
	 * compute the algo
	 */
	public void compute() {
		computed++;
	}

	/**
	 * 
	 * @return number of times computed
	 */
	public int getComputed() {
		return computed;
	}

	/**
	 * @return the algo
	 */
	public AlgoForExtrusion getAlgo() {
		return algo;
	}

}
