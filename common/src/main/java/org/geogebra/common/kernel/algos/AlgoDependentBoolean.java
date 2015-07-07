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
import org.geogebra.common.kernel.arithmetic.PolynomialNode;
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
	private PolynomialNode polyRoot = new PolynomialNode();

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

	// fill the polynomial tree
	public void expressionNodeToPolynomial(ExpressionNode expNode,
			PolynomialNode polyNode) {
		if (polyNode.getPoly() != null) {
			return;
		}
		if (polyNode.getLeft().getPoly() != null
				&& polyNode.getRight().getPoly() != null) {
			Polynomial leftPoly = polyNode.getLeft().getPoly();
			Polynomial rightPoly = polyNode.getRight().getPoly();
			switch (polyNode.getOperation()) {
			case PLUS:
				polyNode.setPoly(leftPoly.add(rightPoly));
				break;
			case MINUS:
				polyNode.setPoly(leftPoly.subtract(rightPoly));
				break;
			case MULTIPLY:
				polyNode.setPoly(leftPoly.multiply(rightPoly));
				break;
			case POWER:
				polyNode.setPoly(leftPoly.multiply(leftPoly));
				break;
			default:
				break;
			}
		}
		if (expNode.getLeft().isExpressionNode()) {
			expressionNodeToPolynomial((ExpressionNode) expNode.getLeft(),
				polyNode.getLeft());
		}
		if (expNode.getRight().isExpressionNode()) {
		expressionNodeToPolynomial((ExpressionNode) expNode.getRight(),
				polyNode.getRight());
		}

	}

	// build a Polynomial tree from ExpressionNode
	private void buildPolynomialTree(ExpressionNode expNode,
			PolynomialNode polyNode) {
		if (expNode == null) {
			return;
		}
		polyNode.setOperation(expNode.getOperation());
		if (expNode.getLeft() != null) {
			polyNode.setLeft(new PolynomialNode());
			if (expNode.getLeft().isExpressionNode()) {
				buildPolynomialTree((ExpressionNode) expNode.getLeft(),
					polyNode.getLeft());
			} else {
				if (expNode.getLeft() instanceof GeoSegment) {
					polyNode.getLeft().setPoly(
							new Polynomial(
									getBotanaVarOfSegment((GeoSegment) expNode
											.getLeft())));
				}
				if (expNode.getLeft() instanceof MyDouble) {
					polyNode.getLeft().setPoly(
							new Polynomial(Integer.parseInt(expNode.getLeft()
									.toString())));
				}
			}

		}
		if (expNode.getRight() != null) {
			polyNode.setRight(new PolynomialNode());
			if (expNode.getRight().isExpressionNode()) {
				buildPolynomialTree((ExpressionNode) expNode.getRight(),
					polyNode.getRight());
			} else {
				if (expNode.getRight() instanceof GeoSegment) {
					polyNode.getRight().setPoly(
							new Polynomial(
									getBotanaVarOfSegment((GeoSegment) expNode
											.getRight())));
				}
				if (expNode.getRight() instanceof MyDouble) {
					polyNode.getRight().setPoly(
							new Polynomial(Integer.parseInt(expNode.getRight()
									.toString())));
				}
			}
		}
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
		// Easy cases: both sides are GeoElements:
		if (root.getLeft().isGeoElement() && root.getRight().isGeoElement()) {

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
		}

		// More difficult cases: sides are expressions:
		if ((root.getLeftTree().isExpressionNode() || root.getRightTree()
				.isExpressionNode())
				&& root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {

			traverseExpression(root);
			// case right side of equation is number
			if ((operations.size() == segmentSquares.size()
					&& operations.get(operations.size() - 1) == Operation.EQUAL_BOOLEAN && root
						.getRight() instanceof MyDouble)
			// case left side of equation is number
					|| ((operations.size() == segmentSquares.size()
							&& operations.get(0) == Operation.EQUAL_BOOLEAN && root
								.getLeft() instanceof MyDouble))
					// case all the terms in equation are segment squares
					|| operations.size() + 1 == segmentSquares.size()) {
				ArrayList<Polynomial> polynomials = getSegmentDistPolynomials();
				Polynomial[][] ret = new Polynomial[1][polynomials.size() + 1];
				int i = 0;
				for (Polynomial p : polynomials) {
					ret[0][i] = p;
					i++;
				}
				buildPolynomialTree(root, polyRoot);
				expressionNodeToPolynomial(root, polyRoot);
				while (polyRoot.getLeft().getPoly() == null
						|| polyRoot.getRight().getPoly() == null) {
					expressionNodeToPolynomial(root, polyRoot);
				}
				Polynomial condPoly = polyRoot.getLeft().getPoly()
						.subtract(polyRoot.getRight().getPoly());
				ret[0][polynomials.size()] = condPoly;
				return ret;

			}
			throw new NoSymbolicParametersException(); // unhandled equation
		}
		throw new NoSymbolicParametersException(); // unhandled equation
	}

	// TODO Consider locusequability

	public ExpressionNode getExpression() {
		return root;
	}
}
