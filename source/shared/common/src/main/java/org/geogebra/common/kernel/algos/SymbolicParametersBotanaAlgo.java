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
