/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.kernel.prover.AlgoAreCongruent;
import org.geogebra.common.kernel.prover.AlgoAreParallel;
import org.geogebra.common.kernel.prover.AlgoArePerpendicular;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.adapters.DependentBooleanAdapter;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.plugin.Operation;

/**
 * Boolean expression, may require CAS for expressions like f==g where f,g are functions,
 * curves or surfaces.
 *
 * @author Markus
 */
public class AlgoDependentBoolean extends AlgoElement implements
		SymbolicParametersAlgo, SymbolicParametersBotanaAlgoAre, DependentAlgo {

	private GeoBoolean bool; // output
	private DependentBooleanAdapter proverAdapter;

	/**
	 * @param cons
	 *            construction
	 * @param root
	 *            defining expression
	 */
	public AlgoDependentBoolean(Construction cons, ExpressionNode root) {
		super(cons);

		bool = new GeoBoolean(cons);
		bool.setDefinition(root);
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.Expression;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		setInputFrom(bool.getDefinition());
		setOnlyOutput(bool);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return the resulting boolean
	 */
	public GeoBoolean getGeoBoolean() {
		return bool;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		ExpressionValue ev;
		try {

			ev = bool.getDefinition().evaluate(StringTemplate.defaultTemplate);
		} catch (Exception e) {
			ev = null;
		}

		ExpressionNode root = bool.getDefinition();
		if (ev instanceof BooleanValue) {
			bool.setValue(((BooleanValue) ev).getBoolean());
		} else {
			bool.setUndefined();
		}
		bool.setDefinition(root);

	}

	@Override
	final public String toString(StringTemplate tpl) {
		// was defined as e.g. c = a & b
		return bool.getDefinition().toString(tpl);
	}

	@Override
	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	@Override
	public void getFreeVariables(HashSet<PVariable> variables)
			throws NoSymbolicParametersException {

		SymbolicParametersAlgo algo = getRootAlgo();
		if (algo != null) {
			algo.getFreeVariables(variables);
			algo.remove();
			return;
		}

		throw new NoSymbolicParametersException();
	}

	@Override
	public int[] getDegrees(AbstractProverReciosMethod a)
			throws NoSymbolicParametersException {
		SymbolicParametersAlgo algo = getRootAlgo();
		if (algo != null) {
			int[] ret = algo.getDegrees(a);
			algo.remove();
			return ret;
		}

		throw new NoSymbolicParametersException();
	}

	@Override
	public BigInteger[] getExactCoordinates(
			HashMap<PVariable, BigInteger> values)
			throws NoSymbolicParametersException {

		SymbolicParametersAlgo algo = getRootAlgo();
		if (algo != null) {
			BigInteger[] ret = algo.getExactCoordinates(values);
			algo.remove();
			return ret;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public PPolynomial[] getPolynomials() throws NoSymbolicParametersException {
		SymbolicParametersAlgo algo = getRootAlgo();
		if (algo != null) {
			PPolynomial[] ret = algo.getPolynomials();
			algo.remove();
			return ret;
		}

		throw new NoSymbolicParametersException();
	}

	private SymbolicParametersAlgo getRootAlgo()
			throws NoSymbolicParametersException {
		ExpressionNode root = bool.getDefinition();
		if (!root.getLeft().isGeoElement() || !root.getRight().isGeoElement()) {
			throw new NoSymbolicParametersException();
		}

		GeoElement left = (GeoElement) root.getLeft();
		GeoElement right = (GeoElement) root.getRight();

		if (root.getOperation().equals(Operation.PERPENDICULAR)) {
			AlgoArePerpendicular algo = new AlgoArePerpendicular(cons, left,
					right);

			return algo;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons, left, right);

			return algo;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreCongruent algo = new AlgoAreCongruent(cons, left, right);

			return algo;
		}
		return null;
	}

	@Override
	public PPolynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		return getProverAdapter().getBotanaPolynomials(bool, cons);
	}

	/**
	 * @return input expression
	 */
	@Override
	public ExpressionNode getExpression() {
		return bool.getDefinition();
	}

	/**
	 * @return input operation
	 */
	public Operation getOperation() {
		return bool.getDefinition().getOperation();
	}

	/**
	 * @return prover adapter
	 */
	public DependentBooleanAdapter getProverAdapter() {
		if (proverAdapter == null) {
			proverAdapter = new DependentBooleanAdapter();
		}
		return proverAdapter;
	}

	/**
	 * @return string for giac from input expression
	 * @throws NoSymbolicParametersException
	 *             when no polynomials can be obtained
	 */

	public String getStrForGiac() throws NoSymbolicParametersException {
		return getProverAdapter().getStrForGiac(bool, cons);
	}

	/**
	 * @return string for giac
	 */
	public String getUserGiacString() {
		return getProverAdapter().getUserGiacString(bool, cons);
	}

}
