package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 * This interface describes the symbolic parameters of algorithms for the Botana
 * method. Based on Simon's SymbolicParametersAlgo.java.
 * 
 * @author Zoltan Kovacs
 */
public interface SymbolicParametersBotanaAlgo {

	/**
	 * Calculates the free variables of an object for the Botana method
	 * 
	 * @param geo
	 *            The corresponding GeoElement
	 * @return array of the free variables
	 * @throws NoSymbolicParametersException
	 *             if it is not possible to obtain suitable polynomials
	 */
	public PVariable[] getBotanaVars(GeoElementND geo)
			throws NoSymbolicParametersException;

	/**
	 * Calculates the polynomials of an object for the Botana method
	 * 
	 * @param geo
	 *            The corresponding GeoElement
	 * @return array of the polynomials
	 * @throws NoSymbolicParametersException
	 *             if it is not possible to obtain suitable polynomials
	 */
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException;
}
