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

package org.geogebra.common.kernel;

import java.math.BigInteger;
import java.util.HashMap;

import org.geogebra.common.kernel.arithmetic.ArbitraryConstantRegistry;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.main.settings.SettingListener;

/**
 * Generic interface for language-specific part of CAS which is plugged into
 * GoGebraCAS.
 */
public interface CASGenericInterface extends SettingListener {

	/**
	 * Translates a variable/constant assignment like "x := 3" into the format
	 * expected by the CAS. Function-Assignments have to be translated
	 * using @see translateFunctionDeclaration().
	 * 
	 * @param label
	 *            the label of the assignment, e.g. x
	 * @param body
	 *            the value that will be assigned to the label, e.g. "3"
	 * @return String in CAS format.
	 */
	String translateAssignment(String label, String body);

	/**
	 * Evaluates a valid expression and returns the resulting String in GeoGebra
	 * notation.
	 * 
	 * @param casInput
	 *            in GeoGebraCAS syntax
	 * @param arbconst
	 *            arbitrary constant handler
	 * @param tpl
	 *            string template
	 * @param cell
	 *            CAS cell
	 * @param kernel
	 *            kernel
	 * @return evaluation result
	 * @throws CASException
	 *             if evaluation fails
	 */
	String evaluateGeoGebraCAS(ValidExpression casInput,
			ArbitraryConstantRegistry arbconst, StringTemplate tpl, GeoCasCell cell,
			Kernel kernel) throws CASException;

	/**
	 * Evaluates an expression in the syntax of the currently active CAS (Giac).
	 * 
	 * @param exp
	 *            The expression to be evaluated.
	 * @return result string (null possible)
	 * @throws Throwable
	 *             if evaluation fails
	 */
	String evaluateRaw(String exp) throws Throwable;

	/**
	 * Appends list start marker to the builder (eg {)
	 * 
	 * @param sbCASCommand
	 *            string builder
	 */
	void appendListStart(StringBuilder sbCASCommand);

	/**
	 * Appends list start marker to the builder (eg })
	 * 
	 * @param sbCASCommand
	 *            string builder
	 */
	void appendListEnd(StringBuilder sbCASCommand);

	/**
	 * 
	 * @param inputExpression
	 *            input
	 * @param arbconst
	 *            constant handler
	 * @param kernel
	 *            kernel
	 * @return evaluated input
	 */
	ExpressionValue evaluateToExpression(
			ValidExpression inputExpression, ArbitraryConstantRegistry arbconst,
			Kernel kernel);

	/**
	 * @param exp
	 *            input
	 * @return output
	 */
	String evaluateCAS(String exp);

	/**
	 * Creates a program to return the elimination ideal in factorized form.
	 * 
	 * @param polys
	 *            input polynomials (comma separated strings)
	 * @param elimVars
	 *            variables to eliminate (comma separated strings)
	 * @return factors in the same form as Singular gives
	 */
	String createEliminateFactorizedScript(String polys,
			String elimVars);

	/**
	 * Creates a program to return the elimination ideal in non-factorized form.
	 * 
	 * @param polys
	 *            input polynomials (comma separated strings)
	 * @param elimVars
	 *            variables to eliminate (comma separated strings)
	 * @param oneCurve
	 *            if the output consists of more polynomials, consider the
	 *            intersections of them as points with real coordinates and
	 *            convert them to a single product
	 * @param precision
	 *            the size of a unit on the screen in pixels
	 * @return the elimination ideal
	 */
	String createEliminateScript(String polys, String elimVars,
			boolean oneCurve, Long precision);

	/**
	 * Creates a program to check if an equation system has no solution, using
	 * Groebner basis w.r.t. the revgradlex order.
	 * 
	 * @param substitutions
	 *            e.g [v1=0,v2=1]
	 * @param polys
	 *            polynomials, e.g. "v1+v2-3*v4-10"
	 * @param freeVars
	 *            free variables
	 * @param dependantVars
	 *            dependent variables
	 * @param transcext
	 *            use coefficient form transcendent extension
	 * @return the program code
	 */
	String createGroebnerSolvableScript(
			HashMap<PVariable, BigInteger> substitutions, String polys,
			String freeVars, String dependantVars, boolean transcext);

	/**
	 * @param rawResult
	 *            output from eliminate() and coeffs() commands
	 * @return 2D array of coefficients
	 */
	double[][][] getBivarPolyCoefficientsAll(String rawResult);

	/**
	 * Make sure the result of async computation is cleared to avoid potential
	 * timing problems
	 */
	void clearResult();

	/**
	 * @return whether loading is finished
	 */
	boolean isLoaded();

	/**
	 * @return true if using Giac/JNI, false for giac.js
	 */
	boolean externalCAS();

	/**
	 * @param substitutions
	 *            substitutions
	 * @param polys
	 *            polynomials
	 * @param freeVars
	 *            free variables
	 * @param dependantVars
	 *            dependent variables
	 * @return Groebner script
	 */
	String createGroebnerInitialsScript(
			HashMap<PVariable, BigInteger> substitutions,
			String polys, String freeVars, String dependantVars);

	/**
	 * Clear the raw evaluation cache.
	 */
	void clearCache();
}
