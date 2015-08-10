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
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyBoolean;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.PolynomialNode;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.prover.AlgoAreCongruent;
import org.geogebra.common.kernel.prover.AlgoAreParallel;
import org.geogebra.common.kernel.prover.AlgoArePerpendicular;
import org.geogebra.common.kernel.prover.AlgoIsOnPath;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Operation;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoDependentBoolean extends AlgoElement implements
		SymbolicParametersAlgo, SymbolicParametersBotanaAlgoAre, DependentAlgo {

	private Set<GeoSegment> allSegmentsFromExpression = new HashSet<GeoSegment>();
	private Variable[] botanaVars;
	private ArrayList<Polynomial> extraPolys = new ArrayList<Polynomial>();
	private int nrOfMaxDecimals;

	private ExpressionNode root; // input
	private GeoBoolean bool; // output

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
					left, right);
			algo.getFreeVariables(variables);
			algo.remove();
			return;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons, "",
 left, right);
			algo.getFreeVariables(variables);
			algo.remove();
			return;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreCongruent algo = new AlgoAreCongruent(cons, "", left, right);
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
					left, right);
			int[] ret = algo.getDegrees();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons, "",
 left, right);
			int[] ret = algo.getDegrees();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreCongruent algo = new AlgoAreCongruent(cons, "", left, right);
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
					left, right);
			BigInteger[] ret = algo.getExactCoordinates(values);
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons, "", left, right);
			BigInteger[] ret = algo.getExactCoordinates(values);
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreCongruent algo = new AlgoAreCongruent(cons, "", left, right);
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
					left, right);
			Polynomial[] ret = algo.getPolynomials();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons, "", left, right);
			Polynomial[] ret = algo.getPolynomials();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreCongruent algo = new AlgoAreCongruent(cons, "", left, right);
			Polynomial[] ret = algo.getPolynomials();
			algo.remove();
			return ret;
		}

		throw new NoSymbolicParametersException();
	}

	/**
	 * fill the polynomial tree
	 * 
	 * @param expNode
	 *            - expression node
	 * @param polyNode
	 *            - polynomial node
	 * @throws NoSymbolicParametersException
	 *             - unhandled operations
	 */
	public void expressionNodeToPolynomial(ExpressionNode expNode,
			PolynomialNode polyNode) throws NoSymbolicParametersException {
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
				Integer pow = polyNode.getRight().evaluateInteger();
				if (pow != null) {
					Polynomial poly = leftPoly;
					for (Integer i = 1; i < pow; i++) {
						poly = poly.multiply(leftPoly);
					}
					polyNode.setPoly(poly);
				}
				break;
			default:
				throw new NoSymbolicParametersException();
			}
		}
		if (expNode.getLeft().isExpressionNode()
				&& polyNode.getLeft().getPoly() == null) {
			expressionNodeToPolynomial((ExpressionNode) expNode.getLeft(),
				polyNode.getLeft());
		}
		if (expNode.getRight().isExpressionNode()
				&& polyNode.getRight().getPoly() == null) {
		expressionNodeToPolynomial((ExpressionNode) expNode.getRight(),
				polyNode.getRight());
		}

	}

	/**
	 * build a Polynomial tree from ExpressionNode
	 * 
	 * @param expNode
	 *            - expression node
	 * @param polyNode
	 *            - polynomial node
	 * @throws NoSymbolicParametersException
	 *             - unhandled operations
	 */
	public void buildPolynomialTree(ExpressionNode expNode,
			PolynomialNode polyNode) throws NoSymbolicParametersException {
		if (expNode == null) {
			return;
		}
		// simplify polynomial if the left and right sides are numbers
		if (expNode.getLeft() instanceof MyDouble
				&& expNode.getRight() instanceof MyDouble) {
			double d1 = expNode.getLeft().evaluateDouble();
			double d2 = expNode.getRight().evaluateDouble();
			Double d;
			switch (expNode.getOperation()) {
			case PLUS:
				d = d1 + d2;
				break;
			case MINUS:
				d = d1 - d2;
				break;
			case MULTIPLY:
				d = d1 * d2;
				break;
			case POWER:
				d = Math.pow(d1, d2);
				break;
			default:
				throw new NoSymbolicParametersException();
			}
			int i;
			// if in the expression exists rational number with n decimals
			// (if there's more than one rational number, then n is the max of
			// decimal numbers)
			// than multiply the coefficient with 10^n
			if (nrOfMaxDecimals != 0) {
				i = (int) (d * Math.pow(10, nrOfMaxDecimals));
			} else {
				i = d.intValue();
			}
			polyNode.setPoly(new Polynomial(i));
			return;
		}
		polyNode.setOperation(expNode.getOperation());
		if (expNode.getLeft() != null) {
			polyNode.setLeft(new PolynomialNode());
			if (expNode.getLeft().isExpressionNode()) {
				buildPolynomialTree((ExpressionNode) expNode.getLeft(),
					polyNode.getLeft());
			} else {
				if (expNode.getLeft() instanceof GeoDummyVariable) {
					polyNode.getLeft()
							.setPoly(
									new Polynomial(
											getBotanaVar(expNode
													.getLeft()
													.toString(
															StringTemplate.defaultTemplate))));
				}
				if (expNode.getLeft() instanceof MySpecialDouble) {
					Double d = expNode.getLeft().evaluateDouble();
					int i;
					// if in the expression exists rational number with n
					// decimals
					// (if there's more than one rational number, then n is the
					// max of decimal numbers)
					// than multiply the coefficient with 10^n
					if (nrOfMaxDecimals != 0) {
						i = (int) (d * Math.pow(10, nrOfMaxDecimals));
					}
					else {
						i = d.intValue();
					}
					polyNode.getLeft().setPoly(new Polynomial(i));
				}
			}

		}
		if (expNode.getRight() != null) {
			polyNode.setRight(new PolynomialNode());
			if (expNode.getRight().isExpressionNode()) {
				buildPolynomialTree((ExpressionNode) expNode.getRight(),
					polyNode.getRight());
			} else {
				if (expNode.getRight() instanceof GeoDummyVariable) {
					polyNode.getRight().setPoly(
									new Polynomial(
											getBotanaVar(expNode
													.getRight()
													.toString(
															StringTemplate.defaultTemplate))));
				}
				if (expNode.getRight() instanceof MySpecialDouble) {
					Double d = expNode.getRight().evaluateDouble();
					int i;
					// simplify the polynomial if in expression is product of
					// numbers
					if (polyNode.getLeft().getPoly() != null
							&& polyNode.getLeft().getPoly().isConstant()) {
						switch (polyNode.getOperation()) {
						case MULTIPLY:
							i = (int) (polyNode.getLeft().getPoly()
									.getConstant() * d);
							break;

						default:
							throw new NoSymbolicParametersException();
						}
						polyNode.setPoly(new Polynomial(i));
						return;
					}
					// if in the expression exists rational number with n
					// decimals
					// (if there's more than one rational number, then n is the
					// max of decimal numbers)
					// than multiply the coefficient with 10^n
					if (nrOfMaxDecimals != 0
							&& expNode.getOperation() != Operation.POWER) {
						i = (int) (d * Math.pow(10, nrOfMaxDecimals));
					}
					else {
						i = d.intValue();
					}
					polyNode.getRight().setPoly(new Polynomial(i));
				}
			}
		}
	}

	private Variable getBotanaVar(String str) {
		for (Variable variable : botanaVars) {
			if (variable.getName().equals(str)) {
				return variable;
			}
		}
		return null;
	}

	// procedure to traverse inorder the expression
	private void traverseExpression(ExpressionNode node)
			throws NoSymbolicParametersException {
		if (node.getLeft().isGeoElement()
				&& node.getLeft() instanceof GeoSegment) {
			// if segment was given with command, eg. Segment[A,B]
			// set new name for segment (which giac will use later)
			if (((GeoSegment) node.getLeft()).getLabelSimple() == null) {
				((GeoSegment) node.getLeft()).setLabel(new Variable()
						.toString());
			}
			allSegmentsFromExpression.add((GeoSegment) node.getLeft());
		}
		if (node.getRight().isGeoElement()
				&& node.getRight() instanceof GeoSegment) {
			// if segment was given with command, eg. Segment[A,B]
			// set new name for segment (which giac will use later)
			if (((GeoSegment) node.getRight()).getLabelSimple() == null) {
				((GeoSegment) node.getRight()).setLabel(new Variable()
						.toString());
			}
			allSegmentsFromExpression.add((GeoSegment) node.getRight());
		}
		if (node.getLeft().isExpressionNode()) {
			traverseExpression((ExpressionNode) node.getLeft());
		}
		if (node.getRight().isExpressionNode()) {
			traverseExpression((ExpressionNode) node.getRight());
		}
		// expression is trusted, if children are trusted
		if (node.getLeft().isExpressionNode()
				&& node.getLeftTree().getTrustable()
				&& node.getRight().isExpressionNode()
				&& node.getRightTree().getTrustable()) {
			node.setTrustable(true);
			return;
		}
		// case number with segment, eg. 2*a^2
		if (node.getLeft() instanceof MyDouble
				&& node.getRight().isExpressionNode()
				&& node.getRightTree().getTrustable()
				&& (node.getOperation() == Operation.DIVIDE || node
						.getOperation() == Operation.MULTIPLY)) {
			node.setTrustable(true);
			return;
		}
		// case segment with number, eg. a^2*1,5
		if (node.getRight() instanceof MyDouble
				&& node.getLeft().isExpressionNode()
				&& node.getLeftTree().getTrustable()) {
			node.setTrustable(true);
			return;
		}
		// * and / with number is trusted
		if (node.getLeft() instanceof MyDouble
				|| node.getRight() instanceof MyDouble) {
			if ((node.getOperation() == Operation.DIVIDE || node.getOperation() == Operation.MULTIPLY)) {
					 node.setTrustable(true);
				 }
		}
		// case we have something in even power
		// check if in the parentheses we have halfTrusted expression (segments
		// multiplied or divided)
		if (node.getOperation() == Operation.POWER) {
			if (node.getRight() instanceof MyDouble) {
				double d = node.getRight().evaluateDouble();
				if (Kernel.isInteger(d) && d % 2 == 0
						&& node.getLeftTree().getHalfTrustable()) {
					node.setTrustable(true);
				}
			}
		}
		// h*j*2 or h*j*k halfTrusted
		if (node.getLeft().isExpressionNode()
				&& node.getLeftTree().getHalfTrustable()
				&& node.getRight() instanceof NumberValue
				&& (node.getOperation() == Operation.DIVIDE || node
						.getOperation() == Operation.MULTIPLY)) {
			node.setHalfTrustable(true);
		}
		// 2*h*j or k*h*j halfTrusted
		if (node.getRight().isExpressionNode()
				&& node.getRightTree().getHalfTrustable() 
				&& node.getLeft() instanceof NumberValue
				&& (node.getOperation() == Operation.DIVIDE || node
						.getOperation() == Operation.MULTIPLY)) {
			node.setHalfTrustable(true);
		}
		// h*j*k*l halfTrusted
		if (node.getLeft().isExpressionNode()
				&& node.getRight().isExpressionNode()
				&& node.getLeftTree().getHalfTrustable()
				&& node.getRightTree().getHalfTrustable()
				&& (node.getOperation() == Operation.DIVIDE || node
						.getOperation() == Operation.MULTIPLY)) {
			node.setHalfTrustable(true);
		}
	}

	public Polynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		// Easy cases: both sides are GeoElements:
		if (root.getLeft().isGeoElement() && root.getRight().isGeoElement()) {

			GeoElement left = (GeoElement) root.getLeft();
			GeoElement right = (GeoElement) root.getRight();

			if (root.getOperation().equals(Operation.PERPENDICULAR)) {
				AlgoArePerpendicular algo = new AlgoArePerpendicular(cons, "",
						left, right);
				Polynomial[][] ret = algo.getBotanaPolynomials();
				algo.remove();
				return ret;
			}
			if (root.getOperation().equals(Operation.PARALLEL)) {
				AlgoAreParallel algo = new AlgoAreParallel(cons, "", left,
						right);
				Polynomial[][] ret = algo.getBotanaPolynomials();
				algo.remove();
				return ret;
			}
			if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
				AlgoAreCongruent algo = new AlgoAreCongruent(cons, "", left, right);
				Polynomial[][] ret = algo.getBotanaPolynomials();
				algo.remove();
				return ret;
			}
			if (root.getOperation().equals(Operation.IS_ELEMENT_OF)) {
				AlgoIsOnPath algo = new AlgoIsOnPath(cons, "", (GeoPoint) left,
						(Path) right);
				Polynomial[][] ret = algo.getBotanaPolynomials();
				algo.remove();
				return ret;
			}
		}

		// handle special case, when left expression is given by another algo
		if (!(root.getLeft().isExpressionNode())
				&& !(root.getLeft() instanceof MyDouble)) {
			AlgoElement algo = ((GeoElement) root.getLeft()).getParentAlgorithm();
			if (algo instanceof AlgoDependentNumber) {
				root.setLeft(((AlgoDependentNumber) algo).getExpression());
			}
		}
		// handle special case, when right expression is given by another algo
		if (!(root.getRight().isExpressionNode())
				&& !(root.getRight() instanceof MyDouble)) {
			AlgoElement algo = ((GeoElement) root.getRight())
					.getParentAlgorithm();
			if (algo instanceof AlgoDependentNumber) {
				root.setRight(((AlgoDependentNumber) algo).getExpression());
			}
		}

		// More difficult cases: sides are expressions:
		if ((root.getLeft().isExpressionNode() || root.getRight()
				.isExpressionNode())
				&& root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			traverseExpression(root);
			// we won't accept untrusted expressions
			if (root.getLeftTree().isExpressionNode()
					&& root.getRightTree().isExpressionNode()
					&& !root.getTrustable()) {
				throw new NoSymbolicParametersException(); // not safe
															// expressions
			}
			Polynomial[][] ret = null;
			return ret;

		}
		throw new NoSymbolicParametersException(); // unhandled expression
	}

	// TODO Consider locusequability

	/**
	 * @return input expression
	 */
	public ExpressionNode getExpression() {
		return root;
	}

	/**
	 * @return input operation
	 */
	public Operation getOperation() {
		return root.getOperation();
	}

	/**
	 * @return string for giac from input expression
	 */
	public String getStrForGiac() {
		String[] labels = new String[allSegmentsFromExpression.size()];
		botanaVars = new Variable[allSegmentsFromExpression.size()];
		int index = 0;
		for (GeoSegment segment : allSegmentsFromExpression) {
			labels[index] = segment.getLabel(StringTemplate.giacTemplate);
			botanaVars[index] = new Variable();
			Variable[] thisSegBotanaVars = segment.getBotanaVars(segment);
			Polynomial s = new Polynomial(botanaVars[index]);
			Polynomial currPoly = s.multiply(s).subtract(
					Polynomial.sqrDistance(thisSegBotanaVars[0],
							thisSegBotanaVars[1], thisSegBotanaVars[2],
							thisSegBotanaVars[3]));
			extraPolys.add(currPoly);
			index++;
		}
		String rootStr = root.toString(StringTemplate.giacTemplate);
		String[] splitedStr = rootStr.split(",");
		rootStr = splitedStr[0].substring(28, splitedStr[0].length() - 2);
		StringBuilder strForGiac = new StringBuilder();
		strForGiac.append("eliminate([" + rootStr + "=0");
		StringBuilder labelsStr = new StringBuilder();
		for (int i = 0; i < labels.length; i++) {
			if (i == 0) {
				labelsStr.append(labels[i]);
			} else {
				labelsStr.append("," + labels[i]);
			}
			strForGiac.append("," + labels[i] + "^2=" + botanaVars[i] + "^2");
		}
		strForGiac.append("],[");
		strForGiac.append(labelsStr + "])");
		App.debug(strForGiac.toString());
		return strForGiac.toString();
	}

	/**
	 * @return distance polynomials
	 */
	public ArrayList<Polynomial> getExtraPolys() {
		return extraPolys;
	}

}
