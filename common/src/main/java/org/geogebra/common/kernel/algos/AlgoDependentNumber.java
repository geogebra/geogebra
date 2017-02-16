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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
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
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PTerm;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
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

	private PVariable[] botanaVars;
	private PPolynomial[] botanaPolynomials;
	/*
	 * Rewrite formulas appearing in other geos to contain GeoGebra definitions.
	 * E.g. when entering a+2b, convert this formula to Segment[A,B] +
	 * 2Segment[C,D] in all other occurrences. Sometimes this is not what we
	 * want, e.g. on creating formulas from the prover automatically.
	 */
	private boolean rewriteFormula = true;

	private Set<GeoSegment> allSegmentsFromExpression = new HashSet<GeoSegment>();
	private ArrayList<Entry<GeoElement, PVariable>> segVarPairs = new ArrayList<Entry<GeoElement, PVariable>>();
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
	public PVariable[] getBotanaVars(GeoElementND geo) {
		return botanaVars;
	}

	/**
	 * Add Botana variables manually in case of building an AlgoDependentNumber
	 * externally.
	 * 
	 * @param vars
	 *            the used Botana variables in the expression to be built
	 */
	public void setBotanaVars(PVariable[] vars) {
		botanaVars = vars;
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {

		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		ExpressionNode definition = this.getExpression();
		traverseExpression(definition);

		if (botanaVars == null) {
			botanaVars = new PVariable[segVarPairs.size() + 1];
			// variable for the expression
			botanaVars[0] = new PVariable(kernel);
			if (!segVarPairs.isEmpty()) {
				Iterator<Entry<GeoElement, PVariable>> it = segVarPairs
						.iterator();
				int k = 1;
				while (it.hasNext()) {
					Entry<GeoElement, PVariable> curr = it.next();
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

			giacOutput = giacOutput.substring(1, giacOutput.length() - 1)
					.replaceAll(Kernel.TMP_VARIABLE_PREFIX2, "");
			// also decrypting variable names

			ValidExpression resultVE = (getKernel().getGeoGebraCAS())
					.getCASparser().parseGeoGebraCASInputAndResolveDummyVars(
							giacOutput, getKernel(), null);

			polyNode = new PolynomialNode();
			buildPolynomialTree((ExpressionNode) resultVE, polyNode);

			expressionNodeToPolynomial((ExpressionNode) resultVE, polyNode);
			while (polyNode.getPoly() == null) {
				expressionNodeToPolynomial((ExpressionNode) resultVE, polyNode);
			}

			botanaPolynomials = new PPolynomial[botanaVars.length];
			botanaPolynomials[0] = polyNode.getPoly();

			if (!segVarPairs.isEmpty()) {
				Iterator<Entry<GeoElement, PVariable>> it = segVarPairs
						.iterator();
				int k = 1;
				while (it.hasNext()) {
					Entry<GeoElement, PVariable> curr = it.next();
					PVariable[] currBotVars = ((GeoSegment) curr.getKey())
							.getBotanaVars(geo);
					PPolynomial seg = new PPolynomial(curr.getValue());
					botanaPolynomials[k] = seg.multiply(seg)
							.subtract(PPolynomial.sqrDistance(currBotVars[0],
									currBotVars[1], currBotVars[2],
									currBotVars[3]));
					k++;
				}
			}

			allSegmentsFromExpression = new HashSet<GeoSegment>();
			// remove variables as geoSegment names
			if (rewriteFormula && !segVarPairs.isEmpty()) {
				Iterator<Entry<GeoElement, PVariable>> it = segVarPairs
						.iterator();
				while (it.hasNext()) {
					Entry<GeoElement, PVariable> curr = it.next();
					GeoSegment currGeoSeg = (GeoSegment) curr.getKey();
					currGeoSeg.setLabelSet(false);
				}
			}
			segVarPairs = new ArrayList<Entry<GeoElement, PVariable>>();

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
		if (node.getLeft() != null && ((node.getLeft().isGeoElement()
				&& node.getLeft() instanceof GeoSegment)
				|| node.getLeft() instanceof GeoDummyVariable)) {
			processNode(node.getLeft());
		}
		if (node.getRight() != null && ((node.getRight().isGeoElement()
				&& node.getRight() instanceof GeoSegment)
				|| node.getRight() instanceof GeoDummyVariable)) {
			processNode(node.getRight());
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

	private void processNode(ExpressionValue ev) {
		GeoSegment s = null;
		if (ev instanceof GeoDummyVariable) {
			GeoDummyVariable v = (GeoDummyVariable) ev;
			GeoElement e = v.getElementWithSameName();
			if (e instanceof GeoSegment) {
				s = (GeoSegment) e;
			}
		} else if (ev instanceof GeoSegment) {
			s = (GeoSegment) ev;
		}
		if (s != null) {
			PVariable currentVar = new PVariable(kernel);
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
			Entry<GeoElement, PVariable> pair = new AbstractMap.SimpleEntry<GeoElement, PVariable>(
					s, currentVar);
			searchSegVarPair(pair);
			allSegmentsFromExpression.add(s);
		}
	}

	private void searchSegVarPair(Entry<GeoElement, PVariable> pair) {
		if (!segVarPairs.isEmpty()) {
			Iterator<Entry<GeoElement, PVariable>> it = segVarPairs.iterator();
			int k = 0;
			while (it.hasNext()) {
				Entry<GeoElement, PVariable> curr = it.next();
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
			PPolynomial leftPoly = polyNode.getLeft().getPoly();
			polyNode.setPoly(leftPoly);
		}
		if (polyNode.getLeft() != null && polyNode.getLeft().getPoly() != null
				&& polyNode.getRight() != null
				&& polyNode.getRight().getPoly() != null) {
			PPolynomial leftPoly = polyNode.getLeft().getPoly();
			PPolynomial rightPoly = polyNode.getRight().getPoly();
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
				/* It must fit in Long. If not, it will take forever. */
				Long pow = polyNode.getRight().evaluateLong();
				if (pow != null) {
					PPolynomial poly = leftPoly;
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
			BigInteger coeff = new BigDecimal(
					expNode.getLeft().evaluateDouble()).toBigInteger();
			polyNode.getLeft().setPoly(new PPolynomial(coeff));
		}
		if (expNode.getRight() instanceof MyDouble
				&& polyNode.getRight().getPoly() == null) {
			BigInteger coeff = new BigDecimal(
					expNode.getRight().evaluateDouble()).toBigInteger();
			polyNode.getRight().setPoly(new PPolynomial(coeff));
		}
		if (expNode.getLeft() instanceof MyDouble
				&& expNode.getRight() instanceof GeoDummyVariable) {
			BigInteger coeff = new BigDecimal(
					expNode.getLeft().evaluateDouble()).toBigInteger();
			PVariable v = getVarOfGeoDummy(expNode.getRight()
					.toString(StringTemplate.defaultTemplate));
			if (v != null) {
				PTerm t = new PTerm(v);
				polyNode.setPoly(new PPolynomial(coeff, t));
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
			BigInteger i;
			// if in the expression exists rational number with n decimals
			// (if there's more than one rational number, then n is the max of
			// decimal numbers)
			// than multiply the coefficient with 10^n
			if (nrOfMaxDecimals != 0) {
				i = new BigDecimal(d * Math.pow(10, nrOfMaxDecimals))
						.toBigInteger();
				Log.error(
						"Possible numerical error in converting formula coefficients to integer");
				/* TODO: check if this conversion is really correct */
			} else {
				i = new BigDecimal(d).toBigInteger();
			polyNode.setPoly(new PPolynomial(i));
			return;
			}
		}
		polyNode.setOperation(expNode.getOperation());
		if (expNode.getLeft() != null) {
			polyNode.setLeft(new PolynomialNode());
			if (expNode.getLeft().isExpressionNode()) {
				buildPolynomialTree((ExpressionNode) expNode.getLeft(),
						polyNode.getLeft());
			} else {
				if (expNode.getLeft() instanceof GeoDummyVariable) {
					polyNode.getLeft().setPoly(new PPolynomial(getVarOfGeoDummy(
							((GeoDummyVariable) expNode.getLeft()).toString(
									StringTemplate.defaultTemplate))));
				}
				if (expNode.getLeft() instanceof MySpecialDouble) {
					Double d = expNode.getLeft().evaluateDouble();
					BigInteger i;
					// if in the expression exists rational number with n decimals
					// (if there's more than one rational number, then n is the max of
					// decimal numbers)
					// than multiply the coefficient with 10^n
					if (nrOfMaxDecimals != 0) {
						i = new BigDecimal(d * Math.pow(10, nrOfMaxDecimals))
								.toBigInteger();
						Log.error(
								"Possible numerical error in converting formula coefficients to integer");
						/* TODO: check if this conversion is really correct */
					} else {
						i = new BigDecimal(d).toBigInteger();
					}
					polyNode.getLeft().setPoly(new PPolynomial(i));
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
						polyNode.getRight().setPoly(new PPolynomial(
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
						polyNode.setPoly(new PPolynomial(i));
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
					polyNode.getRight().setPoly(new PPolynomial(i));
				}
			}
		}
	}

	private PVariable getVarOfGeoDummy(String str) {
		for (PVariable variable : botanaVars) {
			if (variable.getName().equals(str)) {
				return variable;
			}
		}
		// It's possible that the variable is in segVarPairs.
		Iterator<Entry<GeoElement, PVariable>> it = segVarPairs.iterator();
		while (it.hasNext()) {
			Entry<GeoElement, PVariable> e = it.next();
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

		labelsStr.append(Kernel.TMP_VARIABLE_PREFIX);
		labelsStr.append(botanaVars[0].toString());

		strForGiac.append(",");
		strForGiac.append(Kernel.TMP_VARIABLE_PREFIX);
		strForGiac.append(botanaVars[0].toString());
		strForGiac.append("=");
		strForGiac.append(botanaVars[0].toString());

		while (it.hasNext()) {
			GeoSegment currSeg = it.next();
			labelsStr.append(",");
			labelsStr.append(Kernel.TMP_VARIABLE_PREFIX);
			labelsStr.append(currSeg.getLabelSimple());
			/*
			 * Use encrypted variable names here to prevent Giac translating
			 * them later to certain constants like e or i.
			 */
			strForGiac.append(",");
			strForGiac.append(Kernel.TMP_VARIABLE_PREFIX);
			strForGiac.append(currSeg.getLabelSimple());
			strForGiac.append("=");
			strForGiac.append(Kernel.TMP_VARIABLE_PREFIX2);
			strForGiac.append(currSeg.getLabelSimple());
		}
		strForGiac.append("],[");
		strForGiac.append(labelsStr + "])");
		return strForGiac.toString();
	}

}
