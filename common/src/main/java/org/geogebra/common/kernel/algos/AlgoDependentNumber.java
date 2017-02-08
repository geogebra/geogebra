/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.PolynomialNode;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Term;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

/**
 * Algorithm for dependent numbers, e.g. c = a + b.
 * 
 * @author Markus Hohenwarter
 */
public class AlgoDependentNumber extends AlgoElement
		implements DependentAlgo, SymbolicParametersBotanaAlgo {

	private GeoNumberValue number; // output

	private Variable[] botanaVars;
	private Polynomial[] botanaPolynomials;
	/*
	 * Rewrite formulas appearing in other geos to contain GeoGebra definitions.
	 * E.g. when entering a+2b, convert this formula to Segment[A,B] +
	 * 2Segment[C,D] in all other occurrences. Sometimes this is not what we
	 * want, e.g. on creating formulas from the prover automatically.
	 */
	private boolean rewriteFormula = true;

	private Set<GeoSegment> allSegmentsFromExpression = new HashSet<GeoSegment>();
	private ArrayList<Entry<GeoElement, Variable>> segVarPairs = new ArrayList<Entry<GeoElement, Variable>>();
	private int nrOfMaxDecimals = 0;

	/**
	 * Creates new AlgoDependentNumber
	 * 
	 * @param cons
	 *            construction
	 * 
	 * @param root
	 *            expression defining the result
	 * @param isAngle
	 *            true for angles
	 */

	public AlgoDependentNumber(Construction cons, ExpressionNode root,
			boolean isAngle) {
		this(cons, root, isAngle, null, true);
	}

	/**
	 * Creates new AlgoDependentNumber
	 * 
	 * @param cons
	 *            construction
	 * @param root
	 *            expression defining the result
	 * @param isAngle
	 *            true for angles
	 * @param evaluate
	 *            pre-evaluated result
	 */
	public AlgoDependentNumber(Construction cons, ExpressionNode root,
			boolean isAngle, ExpressionValue evaluate) {
		this(cons, root, isAngle, evaluate, true);
	}

	/**
	 * Creates new AlgoDependentNumber
	 * 
	 * @param cons
	 *            construction
	 * @param root
	 *            expression defining the result
	 * @param isAngle
	 *            true for angles
	 * @param evaluate
	 *            pre-evaluated result
	 * @param addToConstructionList
	 *            add object to the construction list
	 */
	public AlgoDependentNumber(Construction cons, ExpressionNode root,
			boolean isAngle, ExpressionValue evaluate,
			boolean addToConstructionList) {
		this(cons, root, isAngle, evaluate, true, true);
	}

	/**
	 * Creates new AlgoDependentNumber
	 * 
	 * @param cons
	 *            construction
	 * @param root
	 *            expression defining the result
	 * @param isAngle
	 *            true for angles
	 * @param evaluate
	 *            pre-evaluated result
	 * @param addToConstructionList
	 *            add object to the construction list
	 * @param rewrite
	 *            rewrite the related formulas in other geos
	 */
	public AlgoDependentNumber(Construction cons, ExpressionNode root,
			boolean isAngle, ExpressionValue evaluate,
			boolean addToConstructionList, boolean rewrite) {
		super(cons, addToConstructionList);
		rewriteFormula = rewrite;
		// simplify constant integers, e.g. -1 * 300 becomes -300
		if (rewriteFormula) {
			root.simplifyConstantIntegers();
		}
		if (evaluate instanceof GeoNumberValue) {
			// fix error with a=7, b = a renaming a instead of creating b
			number = (GeoNumberValue) ((GeoNumberValue) evaluate)
					.copyInternal(cons);
			// just a copy of segment / polygon / arc, not eg. list1(1)
			if (!number.isGeoNumeric() && root.unwrap().isGeoElement()) {
				number.setEuclidianVisible(false);
			}
		} else if (isAngle) {
			number = new GeoAngle(cons);

			// check fileloading to make loading old files (<=4.2) works
			// no allowReflexAngle or forceReflexAngle in XML by default
			if (!cons.isFileLoading() || cons.isAllowUnboundedAngles()) {
				// make sure eg summing angles of polygon a+b+c+d gives correct
				// answer
				((GeoAngle) number).setAngleStyle(AngleStyle.UNBOUNDED);
			}
			// dependent angles are not drawable
			((GeoAngle) number).setDrawable(false, false);
		} else {
			number = new GeoNumeric(cons);
		}
		number.toGeoElement().setDefinition(root);
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
		input = number.getDefinition().getGeoElementVariables();
		if (input == null) {
			input = new GeoElement[0];
		}
		setOutputLength(1);
		setOutput(0, number.toGeoElement());
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting number
	 */
	public GeoNumberValue getNumber() {
		return number;
	}

	@Override
	public ExpressionNode getExpression() {
		return number.getDefinition();
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		if (!rewriteFormula) {
			return;
		}
		try {
			NumberValue nv = (NumberValue) number.getDefinition()
					.evaluate(StringTemplate.defaultTemplate);
			ExpressionNode def = number.getDefinition();
			if (number instanceof GeoNumeric) {
				((GeoNumeric) number).setValue(nv.getDouble());
			} else {
				number.set(nv.toGeoElement(cons));
			}
			number.setDefinition(def);
		} catch (Throwable e) {
			number.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// was defined as e.g. r = 5a - 3b
		// return 5a - 3b
		return number.getDefinition().toString(tpl);
	}

	@Override
	public boolean isLocusEquable() {
		/*
		 * This is just a workaround to allow the
		 * A=DynamicCoordinates[A',round(x(A')*10)/10,round(y(A')*10)/10] trick
		 * (see
		 * http://tube.geogebra.org/material/simple/id/128631#material/150977).
		 * For a complete solution here we should analyze the formula if it is
		 * indeed fully implemented. FIXME
		 */
		return true;
	}

	@Override
	public Variable[] getBotanaVars(GeoElementND geo) {
		return botanaVars;
	}

	/**
	 * Add Botana variables manually in case of building an AlgoDependentNumber
	 * externally.
	 * 
	 * @param vars
	 *            the used Botana variables in the expression to be built
	 */
	public void setBotanaVars(Variable[] vars) {
		botanaVars = vars;
	}

	@Override
	public Polynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {

		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		ExpressionNode definition = this.getExpression();
		traverseExpression(definition);

		if (botanaVars == null) {
			botanaVars = new Variable[segVarPairs.size() + 1];
			// variable for the expression
			botanaVars[0] = new Variable();
			if (!segVarPairs.isEmpty()) {
				Iterator<Entry<GeoElement, Variable>> it = segVarPairs
						.iterator();
				int k = 1;
				while (it.hasNext()) {
					Entry<GeoElement, Variable> curr = it.next();
					botanaVars[k] = curr.getValue();
					k++;
				}
			} else {
				throw new NoSymbolicParametersException();
			}
		}

		PolynomialNode polyNode = new PolynomialNode();
		buildPolynomialTree(definition, polyNode);

		GeoGebraCAS cas = (GeoGebraCAS) getKernel().getGeoGebraCAS();

		String exprGiacStr = "";
		// expand(lcm(denom(coeff(gg)))*gg);
		// see also CASgiac.createEliminateScript()
		String gg = definition.toString(StringTemplate.giacTemplate)
				+ "-ggbtmpvar" + botanaVars[0];
		exprGiacStr = "expand(lcm(denom(coeff(" + gg + ")))*(" + gg + "))";

		nrOfMaxDecimals = 0;

		String strForGiac = getStrForGiac(exprGiacStr);

		try {
			String giacOutput = cas.getCurrentCAS().evaluateRaw(strForGiac);

			giacOutput = giacOutput.substring(1, giacOutput.length() - 1);

			ValidExpression resultVE = (getKernel().getGeoGebraCAS())
					.getCASparser().parseGeoGebraCASInputAndResolveDummyVars(
							giacOutput, getKernel(), null);

			polyNode = new PolynomialNode();
			buildPolynomialTree((ExpressionNode) resultVE, polyNode);

			expressionNodeToPolynomial((ExpressionNode) resultVE, polyNode);
			while (polyNode.getPoly() == null) {
				expressionNodeToPolynomial((ExpressionNode) resultVE, polyNode);
			}

			botanaPolynomials = new Polynomial[botanaVars.length];
			botanaPolynomials[0] = polyNode.getPoly();

			if (!segVarPairs.isEmpty()) {
				Iterator<Entry<GeoElement, Variable>> it = segVarPairs
						.iterator();
				int k = 1;
				while (it.hasNext()) {
					Entry<GeoElement, Variable> curr = it.next();
					Variable[] currBotVars = ((GeoSegment) curr.getKey())
							.getBotanaVars(geo);
					Polynomial seg = new Polynomial(curr.getValue());
					botanaPolynomials[k] = seg.multiply(seg)
							.subtract(Polynomial.sqrDistance(currBotVars[0],
									currBotVars[1], currBotVars[2],
									currBotVars[3]));
					k++;
				}
			}

			allSegmentsFromExpression = new HashSet<GeoSegment>();
			// remove variables as geoSegment names
			if (rewriteFormula && !segVarPairs.isEmpty()) {
					Iterator<Entry<GeoElement, Variable>> it = segVarPairs
							.iterator();
					while (it.hasNext()) {
						Entry<GeoElement, Variable> curr = it.next();
						GeoSegment currGeoSeg = (GeoSegment) curr.getKey();
						currGeoSeg.setLabelSet(false);
					}
			}
			segVarPairs = new ArrayList<Entry<GeoElement, Variable>>();

			return botanaPolynomials;

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		throw new NoSymbolicParametersException();
	}

	private void traverseExpression(ExpressionNode node)
			throws NoSymbolicParametersException {
		// Log.debug(node.toString());
		if (node.getLeft() != null && node.getLeft().isGeoElement()
				&& node.getLeft() instanceof GeoSegment) {
			// if segment was given with command, eg. Segment[A,B]
			// set new name for segment (which giac will use later)
			Variable currentVar = new Variable();
			/*
			 * This is voodoo magic here. We may need a different solution
			 * rather than playing with the label. TODO.
			 */
			boolean suppress = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(false);
			if (((GeoSegment) node.getLeft()).getLabelSimple() == null) {
				GeoSegment left = (GeoSegment) node.getLeft();
				left.setLabel(currentVar.toString());
				left.setAuxiliaryObject(true);
				left.setEuclidianVisible(false);
				left.update();
			}
			cons.setSuppressLabelCreation(suppress);

			Entry<GeoElement, Variable> pair = new AbstractMap.SimpleEntry<GeoElement, Variable>(
					(GeoSegment) node.getLeft(), currentVar);
			searchSegVarPair(pair);
			allSegmentsFromExpression.add((GeoSegment) node.getLeft());
		}
		if (node.getRight() != null && ((node.getRight().isGeoElement()
				&& node.getRight() instanceof GeoSegment)
				|| node.getRight() instanceof GeoDummyVariable)) {
			// if segment was given with command, eg. Segment[A,B]
			// set new name for segment (which giac will use later)
			ExpressionValue right = node.getRight();
			GeoSegment s = null;

			if (right instanceof GeoDummyVariable) {
				GeoDummyVariable v = (GeoDummyVariable) node.getRight();
				GeoElement e = v.getElementWithSameName();
				if (e instanceof GeoSegment) {
					s = (GeoSegment) e;
				}
			} else if (right instanceof GeoSegment) {
				s = (GeoSegment) right;
			}

			if (s != null) {
				Variable currentVar = new Variable();
				/*
				 * This is voodoo magic here. We may need a different solution
				 * rather than playing with the label. TODO.
				 */
				boolean suppress = cons.isSuppressLabelsActive();
				cons.setSuppressLabelCreation(false);
				if (s.getLabelSimple() == null) {
					s.setLabel(currentVar.toString());
					s.setAuxiliaryObject(true);
					s.setEuclidianVisible(false);
					s.update();
				}
				cons.setSuppressLabelCreation(suppress);
				Entry<GeoElement, Variable> pair = new AbstractMap.SimpleEntry<GeoElement, Variable>(
						s, currentVar);
				searchSegVarPair(pair);
				allSegmentsFromExpression.add(s);
			}
		}
		if (node.getLeft() != null && node.getLeft().isExpressionNode()) {
			traverseExpression((ExpressionNode) node.getLeft());
		}
		if (node.getRight() != null && node.getRight().isExpressionNode()) {
			traverseExpression((ExpressionNode) node.getRight());
		}

		if (node.getLeft() != null && node.getLeft().isExpressionNode()
				&& node.getRight() != null
				&& node.getRight().isExpressionNode()) {
			return;
		}
		// case number with segment, eg. 2*a^2
		if (node.getLeft() instanceof MyDouble
				&& node.getRight().isExpressionNode()
				&& (node.getOperation() == Operation.DIVIDE
						|| node.getOperation() == Operation.MULTIPLY)) {
			return;
		}
		// case segment with number, eg. a^2*1,5
		if (node.getRight() instanceof MyDouble
				&& node.getLeft().isExpressionNode()) {
			return;
		}
	}

	private void searchSegVarPair(Entry<GeoElement, Variable> pair) {
		if (!segVarPairs.isEmpty()) {
			Iterator<Entry<GeoElement, Variable>> it = segVarPairs.iterator();
			int k = 0;
			while (it.hasNext()) {
				Entry<GeoElement, Variable> curr = it.next();
				if (curr.getKey().equals(pair.getKey())
						&& curr.getValue().equals(pair.getValue())) {
					break;
				}
				k++;
			}
			if (k == segVarPairs.size()) {
				segVarPairs.add(pair);
			}
		} else {
			segVarPairs.add(pair);
		}
	}

	/**
	 * Attempt to create a Polynomial from a PolynomialNode by using the same
	 * object stored as ExpressionNode as well. The output will be put into
	 * polyNode.poly and can be retrieved by using polyNode.getPoly(). The
	 * PolynomialNode has to be created by buildPolynomialTree(expNode,
	 * polyNode) first. It is possible that the process will not be successful
	 * for the first run. In such cases multiple runs should be performed until
	 * polyNode.poly is not null.
	 * 
	 * @param expNode
	 *            ExpressionNode presentation of the polynomial
	 * @param polyNode
	 *            PolynomialNode presentation of the polynomial
	 * @throws NoSymbolicParametersException
	 *             if the conversion is not possible for some reason (maybe
	 *             because of unhandled cases)
	 * 
	 * @author Csilla Solyom-Gecse
	 * @author Zoltan Kovacs
	 * 
	 *         TODO: Find a more elegant way to do that.
	 */
	public void expressionNodeToPolynomial(ExpressionNode expNode,
			PolynomialNode polyNode) throws NoSymbolicParametersException {
		if (polyNode.getPoly() != null) {
			return;
		}
		if (polyNode.getLeft() != null && polyNode.getRight() == null
				&& polyNode.getOperation() == Operation.NO_OPERATION) {
			Polynomial leftPoly = polyNode.getLeft().getPoly();
			polyNode.setPoly(leftPoly);
		}
		if (polyNode.getLeft() != null && polyNode.getLeft().getPoly() != null
				&& polyNode.getRight() != null
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
		if (expNode.getRight() != null && expNode.getRight().isExpressionNode()
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
			Variable v = getVarOfGeoDummy(expNode.getRight()
					.toString(StringTemplate.defaultTemplate));
			if (v != null) {
				Term t = new Term(v);
				polyNode.setPoly(new Polynomial(coeff, t));
				return;
			}
		}
	}

	/**
	 * Creates a PolynomialNode from an ExpressionNode.
	 * 
	 * @param expNode
	 *            ExpressionNode presentation of the polynomial
	 * @param polyNode
	 *            PolynomialNode presentation of the polynomial
	 * @throws NoSymbolicParametersException
	 *             if the conversion is not possible for some reason (maybe
	 *             because of unhandled cases)
	 * 
	 * @author Csilla Solyom-Gecse
	 * @author Zoltan Kovacs
	 * 
	 *         TODO: Find a more elegant way to do that.
	 */
	public void buildPolynomialTree(ExpressionNode expNode,
			PolynomialNode polyNode) throws NoSymbolicParametersException {
		// Log.debug(expNode.toString());
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
				d = d1 / d2;
				String[] splitter = d.toString().split("\\.");
				if (nrOfMaxDecimals < splitter[1].length()) {
					nrOfMaxDecimals = splitter[1].length();
				}
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
					polyNode.getLeft().setPoly(new Polynomial(getVarOfGeoDummy(
							((GeoDummyVariable) expNode.getLeft()).toString(
									StringTemplate.defaultTemplate))));
				}
				if (expNode.getLeft() instanceof MySpecialDouble) {
					Double d = expNode.getLeft().evaluateDouble();
					long i;
					// if in the expression exists rational number with n
					// decimals
					// (if there's more than one rational number, then n is the
					// max of decimal numbers)
					// than multiply the coefficient with 10^n
					if (nrOfMaxDecimals != 0) {
						i = (int) (d * Math.pow(10, nrOfMaxDecimals));
					} else {
						i = d.longValue();
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
						polyNode.getRight().setPoly(new Polynomial(
								getVarOfGeoDummy(((GeoDummyVariable) expNode
										.getRight()).toString(
												StringTemplate.defaultTemplate))));
					} catch (Exception e) {
						throw new NoSymbolicParametersException();
					}
				}
				if (expNode.getRight() instanceof MySpecialDouble) {
					// see also AlgoDependentBoolean
					double d = expNode.getRight().evaluateDouble();
					BigInteger i;
					// simplify the polynomial if in expression is product of
					// numbers
					if (polyNode.getLeft().getPoly() != null
							&& polyNode.getLeft().getPoly().isConstant()) {
						switch (polyNode.getOperation()) {
						case MULTIPLY:
							i = polyNode.getLeft().getPoly()
							.getConstant().multiply(new BigInteger(Long.toString((long) d)));
							break;
						case DIVIDE:
							i = BigInteger.ONE;
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
						i = new BigInteger(Long.toString(((long) (d * Math.pow(10, nrOfMaxDecimals)))));
					} else {
						i = new BigInteger(Long.toString(((long) d)));
					}
					polyNode.getRight().setPoly(new Polynomial(i));
				}
			}
		}
	}

	private Variable getVarOfGeoDummy(String str) {
		for (Variable variable : botanaVars) {
			if (variable.getName().equals(str)) {
				return variable;
			}
		}
		// It's possible that the variable is in segVarPairs.
		Iterator<Entry<GeoElement, Variable>> it = segVarPairs.iterator();
		while (it.hasNext()) {
			Entry<GeoElement, Variable> e = it.next();
			GeoElement ge = e.getKey();
			if (ge.getLabelSimple().equals(str)) {
				return e.getValue();
			}
		}
		// This will cause a NPE (should not happen):
		Log.error("Internal error in AlgoDependentNumber");
		return null;

	}

	private String getStrForGiac(String str) {
		StringBuilder strForGiac = new StringBuilder();
		strForGiac.append("eliminate([" + str);
		StringBuilder labelsStr = new StringBuilder();
		Iterator<GeoSegment> it = allSegmentsFromExpression.iterator();
		labelsStr.append("ggbtmpvar" + botanaVars[0].toString());
		strForGiac.append("," + "ggbtmpvar" + botanaVars[0].toString() + "="
				+ botanaVars[0].toString());
		while (it.hasNext()) {
			GeoSegment currSeg = it.next();
			labelsStr.append(",ggbtmpvar" + currSeg.getLabelSimple());
			strForGiac.append("," + "ggbtmpvar" + currSeg.getLabelSimple() + "="
					+ currSeg.getLabelSimple());
		}
		strForGiac.append("],[");
		strForGiac.append(labelsStr + "])");
		return strForGiac.toString();
	}

}
