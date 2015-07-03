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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyBoolean;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.prover.AlgoAreEqual;
import org.geogebra.common.kernel.prover.AlgoAreParallel;
import org.geogebra.common.kernel.prover.AlgoArePerpendicular;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.plugin.Operation;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoDependentBoolean extends AlgoElement implements
		SymbolicParametersAlgo, SymbolicParametersBotanaAlgoAre, DependentAlgo {

	private ExpressionNode root; // input
	private GeoBoolean bool; // output
	private ArrayList<ExpressionNode> segmentSquares = new ArrayList<ExpressionNode>();
	private ArrayList<Variable> segBotanaVars = new ArrayList<Variable>();
	private ArrayList<GeoSegment> listOfSegments = new ArrayList<GeoSegment>();
	private ArrayList<Operation> operations = new ArrayList<Operation>();

	public AlgoDependentBoolean(Construction cons, String label,
			ExpressionNode root) {
		super(cons);
		this.root = root;

		bool = new GeoBoolean(cons);
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();
		bool.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		return Algos.Expression;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = root.getGeoElementVariables();

		super.setOutputLength(1);
		super.setOutput(0, bool);
		setDependencies(); // done by AlgoElement
	}

	public GeoBoolean getGeoBoolean() {
		return bool;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		try {

			// needed for eg Sequence[If[liste1(i) < a
			boolean oldLabelStatus = cons.isSuppressLabelsActive();
			kernel.getConstruction().setSuppressLabelCreation(true);

			ExpressionValue ev = root.evaluate(StringTemplate.defaultTemplate);
			kernel.getConstruction().setSuppressLabelCreation(oldLabelStatus);

			if (ev.isGeoElement())
				bool.setValue(((GeoBoolean) ev).getBoolean());
			else
				bool.setValue(((MyBoolean) ev).getBoolean());
		} catch (Exception e) {
			bool.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// was defined as e.g. c = a & b
		return root.toString(tpl);
	}

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if (!root.getLeft().isGeoElement() || !root.getRight().isGeoElement())
			throw new NoSymbolicParametersException();

		GeoElement left = (GeoElement) root.getLeft();
		GeoElement right = (GeoElement) root.getRight();

		if (root.getOperation().equals(Operation.PERPENDICULAR)) {
			AlgoArePerpendicular algo = new AlgoArePerpendicular(cons, "",
					(GeoLine) left, (GeoLine) right);
			algo.getFreeVariables(variables);
			algo.remove();
			return;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons, "",
					(GeoLine) left, (GeoLine) right);
			algo.getFreeVariables(variables);
			algo.remove();
			return;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreEqual algo = new AlgoAreEqual(cons, "", left, right);
			algo.getFreeVariables(variables);
			algo.remove();
			return;
		}

		throw new NoSymbolicParametersException();
	}

	public int[] getDegrees() throws NoSymbolicParametersException {
		if (!root.getLeft().isGeoElement() || !root.getRight().isGeoElement())
			throw new NoSymbolicParametersException();

		GeoElement left = (GeoElement) root.getLeft();
		GeoElement right = (GeoElement) root.getRight();

		if (root.getOperation().equals(Operation.PERPENDICULAR)) {
			AlgoArePerpendicular algo = new AlgoArePerpendicular(cons, "",
					(GeoLine) left, (GeoLine) right);
			int[] ret = algo.getDegrees();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons, "",
					(GeoLine) left, (GeoLine) right);
			int[] ret = algo.getDegrees();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreEqual algo = new AlgoAreEqual(cons, "", left, right);
			int[] ret = algo.getDegrees();
			algo.remove();
			return ret;
		}

		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(HashMap<Variable, BigInteger> values)
			throws NoSymbolicParametersException {

		if (!root.getLeft().isGeoElement() || !root.getRight().isGeoElement())
			throw new NoSymbolicParametersException();

		GeoElement left = (GeoElement) root.getLeft();
		GeoElement right = (GeoElement) root.getRight();

		if (root.getOperation().equals(Operation.PERPENDICULAR)) {
			AlgoArePerpendicular algo = new AlgoArePerpendicular(cons, "",
					(GeoLine) left, (GeoLine) right);
			BigInteger[] ret = algo.getExactCoordinates(values);
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons, "",
					(GeoLine) left, (GeoLine) right);
			BigInteger[] ret = algo.getExactCoordinates(values);
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreEqual algo = new AlgoAreEqual(cons, "", left, right);
			BigInteger[] ret = algo.getExactCoordinates(values);
			algo.remove();
			return ret;
		}

		throw new NoSymbolicParametersException();
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {

		if (!root.getLeft().isGeoElement() || !root.getRight().isGeoElement())
			throw new NoSymbolicParametersException();

		GeoElement left = (GeoElement) root.getLeft();
		GeoElement right = (GeoElement) root.getRight();

		if (root.getOperation().equals(Operation.PERPENDICULAR)) {
			AlgoArePerpendicular algo = new AlgoArePerpendicular(cons, "",
					(GeoLine) left, (GeoLine) right);
			Polynomial[] ret = algo.getPolynomials();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons, "",
					(GeoLine) left, (GeoLine) right);
			Polynomial[] ret = algo.getPolynomials();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreEqual algo = new AlgoAreEqual(cons, "", left, right);
			Polynomial[] ret = algo.getPolynomials();
			algo.remove();
			return ret;
		}

		throw new NoSymbolicParametersException();
	}

	// procedure to traverse inorder the expression
	private void traverseExpression(ExpressionNode node) {
		// node has form a^2, a is GeoSegment
		if (node.isSegmentSquare()) {
			// collect segment squares
			segmentSquares.add(node);
		}
		if (node.getLeft().isExpressionNode()) {
			traverseExpression((ExpressionNode) node.getLeft());
		}
		if (!node.isSegmentSquare()) {
			// collect non-square operations
			operations.add(node.getOperation());
		}
		if (node.getRight().isExpressionNode()) {
			traverseExpression((ExpressionNode) node.getRight());
		}
	}

	// function to get a segments botanaVar
	private Variable getBotanaVarOfSegment(GeoSegment segment) {
		int index = 0;
		for (GeoSegment geoSegment : listOfSegments) {
			if (geoSegment == segment) {
				return segBotanaVars.get(index);
			}
			index++;
		}
		return null;
	}

	// function to get the condition polynomial
	private Polynomial getConditionPolynomial() {
		// get first segments botana variable
		GeoSegment firstSegment = (GeoSegment) segmentSquares.get(0).getLeft();
		Variable firstSegBotanaVar = getBotanaVarOfSegment(firstSegment);
		Polynomial firstSegPoly = new Polynomial(firstSegBotanaVar);
		// add to condition polynomial
		Polynomial polynomial = firstSegPoly.multiply(firstSegPoly);
		boolean isRightSideSide = false;
		int index = 1;
		// if left side of equation is 0
		// handle as right side of equation = 0
		if (operations.get(0) == Operation.EQUAL_BOOLEAN) {
			operations.remove(0);
		}
		while (index < segmentSquares.size()) {
			// get current segments botana variable
			GeoSegment currentSegment = (GeoSegment) segmentSquares.get(index)
					.getLeft();
			Variable currentSegBotanaVar = getBotanaVarOfSegment(currentSegment);
			Polynomial currentSegPoly = new Polynomial(currentSegBotanaVar);
			Polynomial term = currentSegPoly.multiply(currentSegPoly);
			// add/subtract/multiply polynomial with nem term
			switch (operations.get(index - 1)) {
			case PLUS:
				if (!isRightSideSide) {
					polynomial = polynomial.add(term);
				} else {
					// we reached the equations other side
					// proceed inverse operation
					polynomial = polynomial.subtract(term);
				}
				break;
			case MINUS:
				if (!isRightSideSide) {
					polynomial = polynomial.subtract(term);
				} else {
					// we reached the equations other side
					// proceed inverse operation
					polynomial = polynomial.add(term);
				}
				break;
			case EQUAL_BOOLEAN:
				isRightSideSide = true;
				polynomial = polynomial.subtract(term);
				break;
			default:

				break;
			}
			index++;
		}
		return polynomial;
	}

	// function to collect distance polynomials of segments
	// ex: segment a = [A,B] = v5 , A = (v1,v2), B = (v3,v4)
	// v5^2 = sqrDist(v1,v2,v3,v4)
	private ArrayList<Polynomial> getSegmentDistPolynomials() {
		int index = 0;
		ArrayList<Polynomial> polynomials = new ArrayList<Polynomial>();
		Set<GeoSegment> setOfSegments = new HashSet<GeoSegment>();
		while (index < segmentSquares.size()) {
			// check if current segment was already processed
			if (setOfSegments.contains(segmentSquares.get(index).getLeft())) {
				index++;
			} else {
				// current segment was not processed
				GeoSegment currentSegment = (GeoSegment) segmentSquares.get(
						index).getLeft();
				// add to processed segments
				setOfSegments.add(currentSegment);
				listOfSegments.add(currentSegment);
				// create new botana variable for segment
				Variable segmentVar = new Variable();
				segBotanaVars.add(segmentVar);
				// get coordinates of end points of segment
				Variable botanaVars[] = ((GeoLine) currentSegment)
						.getBotanaVars(currentSegment);
				Polynomial s = new Polynomial(segmentVar);
				// distance polynomial
				// v5^2 = sqrDist(v1,v2,v3,v4)
				Polynomial currentSegmentDist = s.multiply(s).subtract(
						Polynomial.sqrDistance(botanaVars[0], botanaVars[1],
								botanaVars[2], botanaVars[3]));
				polynomials.add(currentSegmentDist);
				index++;
			}
		}

		return polynomials;
	}

	public Polynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {

		if ((root.getLeftTree().isExpressionNode() || root.getRightTree()
				.isExpressionNode())
				&& root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {

			traverseExpression(root);
			// case right side of equation is 0
			if ((operations.size() == segmentSquares.size()
					&& operations.get(operations.size() - 1) == Operation.EQUAL_BOOLEAN
					&& root.getRight() instanceof MyDouble && Integer
					.parseInt(root.getRight().toString()) == 0)
			// case left side of equation is 0
					|| ((operations.size() == segmentSquares.size()
							&& operations.get(0) == Operation.EQUAL_BOOLEAN
							&& root.getLeft() instanceof MyDouble && Integer
							.parseInt(root.getLeft().toString()) == 0))
					// case all the terms in equation are segment squares
					|| operations.size() + 1 == segmentSquares.size()) {
				ArrayList<Polynomial> polynomials = getSegmentDistPolynomials();
				Polynomial[][] ret = new Polynomial[1][polynomials.size() + 1];
				int i = 0;
				for (Polynomial p : polynomials) {
					ret[0][i] = p;
					i++;
				}
				ret[0][polynomials.size()] = getConditionPolynomial();
				return ret;

			}
			throw new NoSymbolicParametersException();
		}


		if (!root.getLeft().isGeoElement() || !root.getRight().isGeoElement())
			throw new NoSymbolicParametersException();

		GeoElement left = (GeoElement) root.getLeft();
		GeoElement right = (GeoElement) root.getRight();

		if (root.getOperation().equals(Operation.PERPENDICULAR)) {
			AlgoArePerpendicular algo = new AlgoArePerpendicular(cons, "",
					(GeoLine) left, (GeoLine) right);
			Polynomial[][] ret = algo.getBotanaPolynomials();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons, "",
					(GeoLine) left, (GeoLine) right);
			Polynomial[][] ret = algo.getBotanaPolynomials();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreEqual algo = new AlgoAreEqual(cons, "", left, right);
			Polynomial[][] ret = algo.getBotanaPolynomials();
			algo.remove();
			return ret;
		}

		throw new NoSymbolicParametersException();
	}

	// TODO Consider locusequability

	public ExpressionNode getExpression() {
		return root;
	}
}
