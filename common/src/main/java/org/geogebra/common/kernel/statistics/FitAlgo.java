package org.geogebra.common.kernel.statistics;

/**
 * Algorithm that allows extraction of parameters
 */
public interface FitAlgo {

	/**
	 * @return list of parameters used in the result; meaning depends on algo
	 */
	double[] getCoeffs();

}
