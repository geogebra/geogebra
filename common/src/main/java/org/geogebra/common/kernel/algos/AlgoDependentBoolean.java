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
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.Traversing.GeoNumericLabelCollector;
import org.geogebra.common.kernel.arithmetic.Traversing.GeoNumericReplacer;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.prover.AlgoAreCongruent;
import org.geogebra.common.kernel.prover.AlgoAreEqual;
import org.geogebra.common.kernel.prover.AlgoAreParallel;
import org.geogebra.common.kernel.prover.AlgoArePerpendicular;
import org.geogebra.common.kernel.prover.AlgoIsOnPath;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.PolynomialNode;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Term;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

/**
 *
 * @author Markus
 */
public class AlgoDependentBoolean extends AlgoElement implements
		SymbolicParametersAlgo, SymbolicParametersBotanaAlgoAre, DependentAlgo {

	private Set<GeoSegment> allSegmentsFromExpression = new HashSet<GeoSegment>();
	private Variable[] botanaVars;
	private ArrayList<Polynomial> extraPolys = new ArrayList<Polynomial>();
	private int nrOfMaxDecimals;
	// substitution list of segments with variables
	private ArrayList<Map.Entry<GeoElement, Variable>> varSubstListOfSegs;

	private GeoBoolean bool; // output

	private boolean substNeeded = false;

	private boolean leftWasDist = false, rightWasDist = false;

	/**
	 * @param cons
	 *            construction
	 * @param root
	 *            defining expression
	 */
	public AlgoDependentBoolean(Construction cons,
			ExpressionNode root) {
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
		input = bool.getDefinition().getGeoElementVariables();

		super.setOutputLength(1);
		super.setOutput(0, bool);
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

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		ExpressionNode root = bool.getDefinition();
		if (!root.getLeft().isGeoElement() || !root.getRight().isGeoElement())
			throw new NoSymbolicParametersException();

		GeoElement left = (GeoElement) root.getLeft();
		GeoElement right = (GeoElement) root.getRight();

		if (root.getOperation().equals(Operation.PERPENDICULAR)) {
			AlgoArePerpendicular algo = new AlgoArePerpendicular(cons,
					left, right);
			algo.getFreeVariables(variables);
			algo.remove();
			return;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons,
 left, right);
			algo.getFreeVariables(variables);
			algo.remove();
			return;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreCongruent algo = new AlgoAreCongruent(cons, left, right);
			algo.getFreeVariables(variables);
			algo.remove();
			return;
		}

		throw new NoSymbolicParametersException();
	}

	public int[] getDegrees() throws NoSymbolicParametersException {
		ExpressionNode root = bool.getDefinition();
		if (!root.getLeft().isGeoElement() || !root.getRight().isGeoElement())
			throw new NoSymbolicParametersException();

		GeoElement left = (GeoElement) root.getLeft();
		GeoElement right = (GeoElement) root.getRight();

		if (root.getOperation().equals(Operation.PERPENDICULAR)) {
			AlgoArePerpendicular algo = new AlgoArePerpendicular(cons,
					left, right);
			int[] ret = algo.getDegrees();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons,
 left, right);
			int[] ret = algo.getDegrees();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreCongruent algo = new AlgoAreCongruent(cons, left, right);
			int[] ret = algo.getDegrees();
			algo.remove();
			return ret;
		}

		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(HashMap<Variable, BigInteger> values)
			throws NoSymbolicParametersException {
		ExpressionNode root = bool.getDefinition();
		if (!root.getLeft().isGeoElement() || !root.getRight().isGeoElement())
			throw new NoSymbolicParametersException();

		GeoElement left = (GeoElement) root.getLeft();
		GeoElement right = (GeoElement) root.getRight();

		if (root.getOperation().equals(Operation.PERPENDICULAR)) {
			AlgoArePerpendicular algo = new AlgoArePerpendicular(cons,
					left, right);
			BigInteger[] ret = algo.getExactCoordinates(values);
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons, left, right);
			BigInteger[] ret = algo.getExactCoordinates(values);
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreCongruent algo = new AlgoAreCongruent(cons, left, right);
			BigInteger[] ret = algo.getExactCoordinates(values);
			algo.remove();
			return ret;
		}

		throw new NoSymbolicParametersException();
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		ExpressionNode root = bool.getDefinition();
		if (!root.getLeft().isGeoElement() || !root.getRight().isGeoElement())
			throw new NoSymbolicParametersException();

		GeoElement left = (GeoElement) root.getLeft();
		GeoElement right = (GeoElement) root.getRight();

		if (root.getOperation().equals(Operation.PERPENDICULAR)) {
			AlgoArePerpendicular algo = new AlgoArePerpendicular(cons,
					left, right);
			Polynomial[] ret = algo.getPolynomials();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons, left, right);
			Polynomial[] ret = algo.getPolynomials();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreCongruent algo = new AlgoAreCongruent(cons, left, right);
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
				Long pow = polyNode.getRight().evaluateLong();
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
		if (expNode.getLeft() instanceof MyDouble
				&& polyNode.getLeft().getPoly() == null) {
			int coeff = (int) expNode.getLeft().evaluateDouble();
			polyNode.getLeft().setPoly(new Polynomial(coeff));
		}
		if (expNode.getRight() instanceof MyDouble
				&& polyNode.getRight().getPoly() == null) {
			int coeff = (int) expNode.getRight().evaluateDouble();
			polyNode.getRight().setPoly(new Polynomial(coeff));
		}
		if (expNode.getLeft() instanceof MyDouble
				&& expNode.getRight() instanceof GeoDummyVariable) {
			int coeff = (int) expNode.getLeft().evaluateDouble();
			Variable v = getVariable(expNode.getRight().toString(
					StringTemplate.defaultTemplate));
			if (v != null) {
				Term t = new Term(v);
				polyNode.setPoly(new Polynomial(coeff, t));
				return;
			}
		}
	}

	// get Variable with given name
	private Variable getVariable(String varStr) {
		if (botanaVars != null) {
			for (int i = 0; i < botanaVars.length; i++) {
				if (varStr.equals(botanaVars[i].getName())) {
					return botanaVars[i];
				}
			}
		}
		return null;
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
			case DIVIDE:
				d = (double) 1;
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
					try {
					polyNode.getRight().setPoly(
									new Polynomial(
											getBotanaVar(expNode
													.getRight()
													.toString(
															StringTemplate.defaultTemplate))));
					} catch (Exception e) {
						throw new NoSymbolicParametersException();
					}
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
						case DIVIDE:
							i = 1;
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
		if (node.getLeft() != null && node.getLeft().isGeoElement()
				&& node.getLeft() instanceof GeoSegment) {
			// if segment was given with command, eg. Segment[A,B]
			// set new name for segment (which giac will use later)
			if (((GeoSegment) node.getLeft()).getLabelSimple() == null) {
				((GeoSegment) node.getLeft()).setLabel(new Variable()
						.toString());
			}
			allSegmentsFromExpression.add((GeoSegment) node.getLeft());
		}
		if (node.getRight() != null && node.getRight().isGeoElement()
				&& node.getRight() instanceof GeoSegment) {
			// if segment was given with command, eg. Segment[A,B]
			// set new name for segment (which giac will use later)
			if (((GeoSegment) node.getRight()).getLabelSimple() == null) {
				((GeoSegment) node.getRight()).setLabel(new Variable()
						.toString());
			}
			allSegmentsFromExpression.add((GeoSegment) node.getRight());
		}
		if (node.getLeft() != null && node.getLeft().isExpressionNode()) {
			traverseExpression((ExpressionNode) node.getLeft());
		}
		if (node.getRight() != null && node.getRight().isExpressionNode()) {
			traverseExpression((ExpressionNode) node.getRight());
		}

		if (node.getLeft() != null && node.getLeft().isExpressionNode()
				&& node.getRight().isExpressionNode()) {
			return;
		}
		// case number with segment, eg. 2*a^2
		if (node.getLeft() instanceof MyDouble
				&& node.getRight().isExpressionNode()
				&& (node.getOperation() == Operation.DIVIDE || node
						.getOperation() == Operation.MULTIPLY)) {
			return;
		}
		// case segment with number, eg. a^2*1,5
		if (node.getRight() instanceof MyDouble
				&& node.getLeft().isExpressionNode()) {
			return;
		}
	}

	public Polynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		ExpressionNode root = bool.getDefinition();

		// replace Distance[A,B] with geoSegment
		if (!(root.getLeft().isExpressionNode())
				&& root.getLeft() instanceof GeoNumeric) {
			AlgoElement algo = ((GeoElement) root.getLeft())
					.getParentAlgorithm();
			if (algo instanceof AlgoDistancePoints) {
				GeoSegment geo = cons.getSegmentFromAlgoList(
						(GeoPoint) algo.getInput(0),
						(GeoPoint) algo.getInput(1));
				if (geo != null) {
					root.setLeft(geo);
				} else {
					geo = new GeoSegment(cons, (GeoPoint) algo.input[0],
							(GeoPoint) algo.input[1]);
					geo.setParentAlgorithm(algo);
					root.setLeft(geo);
					leftWasDist = true;
				}
			}
		}
		if (!(root.getRight().isExpressionNode())
				&& root.getRight() instanceof GeoNumeric) {
			AlgoElement algo = ((GeoElement) root.getRight())
					.getParentAlgorithm();
			if (algo instanceof AlgoDistancePoints) {
				GeoSegment geo = cons.getSegmentFromAlgoList(
						(GeoPoint) algo.getInput(0),
						(GeoPoint) algo.getInput(1));
				if (geo != null) {
					root.setRight(geo);
				} else {
					geo = new GeoSegment(cons, (GeoPoint) algo.input[0],
							(GeoPoint) algo.input[1]);
					geo.setParentAlgorithm(algo);
					root.setRight(geo);
					rightWasDist = true;
				}
			}
		}

		// Easy cases: both sides are GeoElements:
		if (root.getLeft().isGeoElement()
				&& (!(root.getLeft() instanceof GeoNumeric)
						|| ((GeoElement) root.getLeft()).getParentAlgorithm()
								.getRelatedModeID() == EuclidianConstants.MODE_AREA)
				&& root.getRight().isGeoElement()
				&& (!(root.getRight() instanceof GeoNumeric)
						|| ((GeoElement) root.getRight()).getParentAlgorithm()
								.getRelatedModeID() == EuclidianConstants.MODE_AREA)) {

			GeoElement left = (GeoElement) root.getLeft();
			GeoElement right = (GeoElement) root.getRight();

			if (root.getOperation().equals(Operation.PERPENDICULAR)) {
				AlgoArePerpendicular algo = new AlgoArePerpendicular(cons,
						left, right);
				Polynomial[][] ret = algo.getBotanaPolynomials();
				cons.removeFromConstructionList(algo);
				return ret;
			}
			if (root.getOperation().equals(Operation.PARALLEL)) {
				AlgoAreParallel algo = new AlgoAreParallel(cons, left,
						right);
				Polynomial[][] ret = algo.getBotanaPolynomials();
				cons.removeFromConstructionList(algo);
				return ret;
			}
			if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
				if (root.getLeft() instanceof GeoNumeric
						&& ((GeoElement) root.getLeft()).getParentAlgorithm()
								.getRelatedModeID() == EuclidianConstants.MODE_AREA
						&& root.getRight() instanceof GeoNumeric
						&& ((GeoElement) root.getLeft()).getParentAlgorithm()
								.getRelatedModeID() == EuclidianConstants.MODE_AREA) {
					AlgoAreEqual algo = new AlgoAreEqual(cons, left, right);
					Polynomial[][] ret = algo.getBotanaPolynomials();
					cons.removeFromConstructionList(algo);
					algo.setProtectedInput(true);
					if (leftWasDist) {
						left.getParentAlgorithm().setProtectedInput(true);
						left.doRemove();
					}
					if (rightWasDist) {
						right.getParentAlgorithm().setProtectedInput(true);
						right.doRemove();
					}
					return ret;
				}
				AlgoAreCongruent algo = new AlgoAreCongruent(cons, left, right);
				Polynomial[][] ret = algo.getBotanaPolynomials();
				cons.removeFromConstructionList(algo);
				algo.setProtectedInput(true);
				if (leftWasDist) {
					left.getParentAlgorithm().setProtectedInput(true);
					left.doRemove();
				}
				if (rightWasDist) {
					right.getParentAlgorithm().setProtectedInput(true);
					right.doRemove();
				}
				return ret;
			}
			if (root.getOperation().equals(Operation.IS_ELEMENT_OF)) {
				AlgoIsOnPath algo = new AlgoIsOnPath(cons, (GeoPoint) left,
						(Path) right);
				Polynomial[][] ret = algo.getBotanaPolynomials();
				cons.removeFromConstructionList(algo);
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
		if (((root.getLeft().isExpressionNode() || root.getRight()
				.isExpressionNode()) && root.getOperation().equals(
				Operation.EQUAL_BOOLEAN))
				|| (root.getLeft() instanceof GeoElement
						&& root.getRight() instanceof MyDouble
				&& root.getOperation().equals(Operation.EQUAL_BOOLEAN))){
			traverseExpression(root);
			// try to check substituted and expanded expression

			ExpressionNode rootCopy = root.deepCopy(kernel);
			// collect all labels of GeoNumerics from expression
			Set<String> setOfGeoNumLabels = new TreeSet<String>();
			rootCopy.traverse(
					GeoNumericLabelCollector.getCollector(setOfGeoNumLabels));
			if (!setOfGeoNumLabels.isEmpty()) {
				substNeeded = true;
			}
			Iterator<String> it = setOfGeoNumLabels.iterator();
			while (it.hasNext()) {
				String varStr = it.next();
				// get GeoNumeric from construction with given label
				GeoNumeric geo = (GeoNumeric) cons.geoTableVarLookup(varStr);
				// get substitute formula of GeoNumeric
				ExpressionNode replExp = ((AlgoDependentNumber) geo
						.getParentAlgorithm()).getExpression();
				GeoNumericReplacer repl = GeoNumericReplacer.getReplacer(geo,
						replExp, kernel);
				// replace GeoNumeric with formula expression
				rootCopy.traverse(repl);
			}
			// traverse substituted expression to collect segments
			traverseExpression(rootCopy);

			if (((rootCopy.getLeft() instanceof GeoSegment
					&& rootCopy.getRight() instanceof MyDouble)
					|| (rootCopy.getRight() instanceof GeoSegment
							&& rootCopy.getLeft() instanceof MyDouble))
					&& rootCopy.getOperation()
							.equals(Operation.EQUAL_BOOLEAN)) {
				Polynomial[][] ret = null;
				return ret;
			}

			GeoGebraCAS cas = (GeoGebraCAS) kernel.getGeoGebraCAS();
			try {
				// get expanded expression of root
				String expandGiacOutput = cas.getCurrentCAS()
						.evaluateRaw(
								"expand("
										+ rootCopy.getLeftTree().toString(
												StringTemplate.giacTemplate)
										+ ")");
				if (!expandGiacOutput.contains("?")
						&& !expandGiacOutput.equals("{}")) {
					// parse expanded string into expression
					ValidExpression expandValidExp = (kernel.getGeoGebraCAS())
							.getCASparser()
							.parseGeoGebraCASInputAndResolveDummyVars(
									expandGiacOutput, kernel, null);
					traverseExpression((ExpressionNode) expandValidExp);
				}
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Polynomial[][] ret = null;
			return ret;

		}
		throw new NoSymbolicParametersException(); // unhandled expression
	}

	

	/**
	 * @return input expression
	 */
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
	 * @return string for giac from input expression
	 */
	public String getStrForGiac() {
		String[] labels = new String[allSegmentsFromExpression.size()];
		extraPolys.clear();
		if (botanaVars == null) {
			botanaVars = new Variable[allSegmentsFromExpression.size()];
		}
		if (varSubstListOfSegs == null) {
			varSubstListOfSegs = new ArrayList<Entry<GeoElement, Variable>>();
		}
		int index = 0;
		for (GeoSegment segment : allSegmentsFromExpression) {
			labels[index] = segment.getLabel(StringTemplate.giacTemplate);
			if (botanaVars[index] == null) {
				botanaVars[index] = new Variable();
			}
			// collect substitution of segments with variables
			Entry<GeoElement, Variable> subst = new AbstractMap.SimpleEntry<GeoElement, Variable>(
					segment, botanaVars[index]);
			if (!varSubstListOfSegs.isEmpty()) {
				Iterator<Entry<GeoElement, Variable>> it = varSubstListOfSegs
						.iterator();
				int k = 0;
				while (it.hasNext()) {
					Entry<GeoElement, Variable> curr = it.next();
					if (curr.getKey().equals(segment)
							&& curr.getValue().equals(botanaVars[index])) {
						break;
					}
					k++;
				}
				if (k == varSubstListOfSegs.size()) {
					varSubstListOfSegs.add(subst);
				}
			} else {
				varSubstListOfSegs.add(subst);
			}
			Variable[] thisSegBotanaVars = segment.getBotanaVars(segment);
			Polynomial s = new Polynomial(botanaVars[index]);
			Polynomial currPoly = s.multiply(s).subtract(
					Polynomial.sqrDistance(thisSegBotanaVars[0],
							thisSegBotanaVars[1], thisSegBotanaVars[2],
							thisSegBotanaVars[3]));
			extraPolys.add(currPoly);
			index++;
		}
		String rootStr;
		// make sure we use substituted expression
		// if substitution was made in root
		if (substNeeded) {
			ExpressionNode rootCopy = bool.getDefinition().deepCopy(kernel);
			// collect all labels of GeoNumerics from expression
			Set<String> setOfGeoNumLabels = new TreeSet<String>();
			rootCopy.traverse(GeoNumericLabelCollector
					.getCollector(setOfGeoNumLabels));
			Iterator<String> it = setOfGeoNumLabels.iterator();
			while (it.hasNext()) {
				String varStr = it.next();
				// get GeoNumeric from construction with given label
				GeoNumeric geo = (GeoNumeric) cons.geoTableVarLookup(varStr);
				// get substitute formula of GeoNumeric
				ExpressionNode replExp = ((AlgoDependentNumber) geo
						.getParentAlgorithm()).getExpression();
				GeoNumericReplacer repl = GeoNumericReplacer.getReplacer(geo,
						replExp, kernel);
				// replace GeoNumeric with formula expression
				rootCopy.traverse(repl);
			}
			rootStr = rootCopy.toString(StringTemplate.giacTemplate);
		} else {
			rootStr = bool.getDefinition()
					.toString(StringTemplate.giacTemplate);
		}
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
		Log.debug(strForGiac.toString());
		return strForGiac.toString();
	}

	/**
	 * @return string for giac
	 */
	public String getUserGiacString() {
		String[] labels = new String[allSegmentsFromExpression.size()];
		int index = 0;
		for (GeoSegment segment : allSegmentsFromExpression) {
			labels[index] = segment.getLabel(StringTemplate.giacTemplate);
			index++;
		}
		String rootStr;
		// make sure we use substituted expression
		// if substitution was made in root
		if (substNeeded) {
			ExpressionNode rootCopy = bool.getDefinition().deepCopy(kernel);
			// collect all labels of GeoNumerics from expression
			Set<String> setOfGeoNumLabels = new TreeSet<String>();
			rootCopy.traverse(GeoNumericLabelCollector
					.getCollector(setOfGeoNumLabels));
			Iterator<String> it = setOfGeoNumLabels.iterator();
			while (it.hasNext()) {
				String varStr = it.next();
				// get GeoNumeric from construction with given label
				GeoNumeric geo = (GeoNumeric) cons.geoTableVarLookup(varStr);
				// get substitute formula of GeoNumeric
				ExpressionNode replExp = ((AlgoDependentNumber) geo
						.getParentAlgorithm()).getExpression();
				GeoNumericReplacer repl = GeoNumericReplacer.getReplacer(geo,
						replExp, kernel);
				// replace GeoNumeric with formula expression
				rootCopy.traverse(repl);
			}
			rootStr = rootCopy.toString(StringTemplate.giacTemplate);
		} else {
			rootStr = bool.getDefinition()
					.toString(StringTemplate.giacTemplate);
		}
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
			strForGiac.append("," + labels[i] + "=" + botanaVars[i]);
		}
		strForGiac.append("],[");
		strForGiac.append(labelsStr + "])");
		return strForGiac.toString();
	}

	/**
	 * @return distance polynomials
	 */
	public ArrayList<Polynomial> getExtraPolys() {
		return extraPolys;
	}

	/**
	 * @return substitution list of segments with variables
	 */
	public ArrayList<Entry<GeoElement, Variable>> getVarSubstListOfSegs() {
		return varSubstListOfSegs;
	}

}
