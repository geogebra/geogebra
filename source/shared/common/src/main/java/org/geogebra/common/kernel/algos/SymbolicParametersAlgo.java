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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 * This interface describes the symbolic parameters of algorithms
 * 
 * @author Simon Weitzhofer
 *
 */
public interface SymbolicParametersAlgo {

	/**
	 * Getter for the SymbolicParameters
	 * 
	 * @return the SymbolicParameters
	 */
	public SymbolicParameters getSymbolicParameters();

	/**
	 * Calculates the set of free variables.
	 * 
	 * @param variables
	 *            all free variables used
	 * @throws NoSymbolicParametersException
	 *             thrown if no symbolic parameters are available.
	 * 
	 */
	public void getFreeVariables(HashSet<PVariable> variables)
			throws NoSymbolicParametersException;

	/**
	 * Calculates the maximum degree of the variables
	 * 
	 * @param a
	 *            Recio's method
	 * 
	 * @return the degrees of the coordinates
	 * @throws NoSymbolicParametersException
	 *             thrown if no symbolic parameters are available.
	 */
	public int[] getDegrees(AbstractProverReciosMethod a)
			throws NoSymbolicParametersException;

	/**
	 * Calculates the homogeneous coordinates of the object when substituting
	 * the variables by its values.
	 * 
	 * @param values
	 *            The values the variables are substituted with
	 * @return the coordinates
	 * @throws NoSymbolicParametersException
	 *             is thrown if it is not possible to obtain the exact
	 *             coordinates
	 */
	BigInteger[] getExactCoordinates(
			HashMap<PVariable, BigInteger> values)
			throws NoSymbolicParametersException;

	/**
	 * Calculates the polynomial describing the algorithm or statement
	 * 
	 * @return the polynomial
	 * @throws NoSymbolicParametersException
	 *             if it is not possible to obtain an algebraic description
	 */
	public PPolynomial[] getPolynomials() throws NoSymbolicParametersException;

	/**
	 * See AlgoElement.remove()
	 */
	public void remove();
}
