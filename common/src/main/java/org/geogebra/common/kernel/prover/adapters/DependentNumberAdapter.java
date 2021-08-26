package org.geogebra.common.kernel.prover.adapters;

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
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.PolynomialNode;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PTerm;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

public class DependentNumberAdapter extends ProverAdapter {

	private Set<GeoSegment> allSegmentsFromExpression = new HashSet<>();
	private ArrayList<Entry<GeoElement, PVariable>> segVarPairs = new ArrayList<>();
	private int nrOfMaxDecimals = 0;

	public PPolynomial[] getBotanaPolynomials(AlgoDependentNumber adn, GeoElementND geo)
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}
		Kernel kernel = adn.getKernel();
		ExpressionNode definition = adn.getExpression();
		traverseExpression(definition, kernel);

		if (botanaVars == null) {
			botanaVars = new PVariable[segVarPairs.size() + 1];
			// variable for the expression
			botanaVars[0] = new PVariable(kernel);
			if (!segVarPairs.isEmpty()) {
				Iterator<Entry<GeoElement, PVariable>> it = segVarPairs.iterator();
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

		GeoGebraCAS cas = (GeoGebraCAS) kernel.getGeoGebraCAS();

		String exprGiacStr = "";
		// expand(lcm(denom(coeff(gg)))*gg);
		// see also CASgiac.createEliminateScript()
		String gg = definition.toString(StringTemplate.giacTemplate) + "-"
				+ Kernel.TMP_VARIABLE_PREFIX + botanaVars[0];
		exprGiacStr = "expand(lcm(denom(coeff(" + gg + ")))*(" + gg + "))";

		nrOfMaxDecimals = 0;

		String strForGiac = getStrForGiac(exprGiacStr);

		try {
			String giacOutput = cas.getCurrentCAS().evaluateRaw(strForGiac);

			giacOutput = giacOutput.substring(1, giacOutput.length() - 1)
					.replaceAll(Kernel.TMP_VARIABLE_PREFIX2, "");
			// also decrypting variable names

			ValidExpression resultVE = (adn.getKernel().getGeoGebraCAS()).getCASparser()
					.parseGeoGebraCASInputAndResolveDummyVars(giacOutput, adn.getKernel(), null);

			polyNode = new PolynomialNode();
			buildPolynomialTree((ExpressionNode) resultVE, polyNode);

			expressionNodeToPolynomial((ExpressionNode) resultVE, polyNode);
			while (polyNode.getPoly() == null) {
				expressionNodeToPolynomial((ExpressionNode) resultVE, polyNode);
			}

			botanaPolynomials = new PPolynomial[botanaVars.length];
			botanaPolynomials[0] = polyNode.getPoly();

			if (!segVarPairs.isEmpty()) {
				Iterator<Entry<GeoElement, PVariable>> it = segVarPairs.iterator();
				int k = 1;
				while (it.hasNext()) {
					Entry<GeoElement, PVariable> curr = it.next();
					PVariable[] currBotVars = ((GeoSegment) curr.getKey()).getBotanaVars(geo);
					PPolynomial seg = new PPolynomial(curr.getValue());
					botanaPolynomials[k] = seg.multiply(seg).subtract(PPolynomial.sqrDistance(
							currBotVars[0], currBotVars[1], currBotVars[2], currBotVars[3]));
					k++;
				}
			}

			allSegmentsFromExpression = new HashSet<>();
			// remove variables as geoSegment names
			if (adn.isRewriteFormula() && !segVarPairs.isEmpty()) {
				Iterator<Entry<GeoElement, PVariable>> it = segVarPairs.iterator();
				while (it.hasNext()) {
					Entry<GeoElement, PVariable> curr = it.next();
					GeoSegment currGeoSeg = (GeoSegment) curr.getKey();
					currGeoSeg.setLabelSet(false);
				}
			}
			segVarPairs = new ArrayList<>();

			return botanaPolynomials;

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		throw new NoSymbolicParametersException();
	}

	private void traverseExpression(ExpressionNode node, Kernel kernel)
			throws NoSymbolicParametersException {
		// Log.debug(node.toString());
		if (node.getLeft() != null
				&& ((node.getLeft().isGeoElement() && node.getLeft() instanceof GeoSegment)
						|| node.getLeft() instanceof GeoDummyVariable)) {
			processNode(node.getLeft(), kernel);
		}
		if (node.getRight() != null
				&& ((node.getRight().isGeoElement() && node.getRight() instanceof GeoSegment)
						|| node.getRight() instanceof GeoDummyVariable)) {
			processNode(node.getRight(), kernel);
		}

		if (node.getLeft() != null && node.getLeft().isExpressionNode()) {
			traverseExpression((ExpressionNode) node.getLeft(), kernel);
		}
		if (node.getRight() != null && node.getRight().isExpressionNode()) {
			traverseExpression((ExpressionNode) node.getRight(), kernel);
		}

		if (node.getLeft() != null && node.getLeft().isExpressionNode() && node.getRight() != null
				&& node.getRight().isExpressionNode()) {
			return;
		}
		// case number with segment, eg. 2*a^2
		if (node.getLeft() instanceof MyDouble && node.getRight().isExpressionNode()
				&& (node.getOperation() == Operation.DIVIDE
						|| node.getOperation() == Operation.MULTIPLY)) {
			return;
		}
		// case segment with number, eg. a^2*1,5
		if (node.getRight() instanceof MyDouble && node.getLeft().isExpressionNode()) {
			return;
		}
	}

	private void processNode(ExpressionValue ev, Kernel kernel) {
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
			Construction cons = kernel.getConstruction();
			boolean suppress = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(false);
			if (s.getLabelSimple() == null) {
				s.setLabel(currentVar.toString());
				s.setAuxiliaryObject(true);
				s.setEuclidianVisible(false);
				s.update();
			}
			cons.setSuppressLabelCreation(suppress);
			Entry<GeoElement, PVariable> pair = new AbstractMap.SimpleEntry<>(
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
	public void buildPolynomialTree(ExpressionNode expNode, PolynomialNode polyNode)
			throws NoSymbolicParametersException {
		// Log.debug(expNode.toString());
		if (expNode == null) {
			return;
		}
		// simplify polynomial if the left and right sides are numbers
		if (expNode.getLeft() instanceof MyDouble && expNode.getRight() instanceof MyDouble) {
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
				i = new BigDecimal(d * Math.pow(10, nrOfMaxDecimals)).toBigInteger();
				Log.error("Possible numerical error in converting formula coefficients to integer");
				/* TODO: check if this conversion is really correct */
			} else {
				i = new BigDecimal(d).toBigInteger();
			}
			polyNode.setPoly(new PPolynomial(i));
			return;
		}
		polyNode.setOperation(expNode.getOperation());
		if (expNode.getLeft() != null) {
			polyNode.setLeft(new PolynomialNode());
			if (expNode.getLeft().isExpressionNode()) {
				buildPolynomialTree((ExpressionNode) expNode.getLeft(), polyNode.getLeft());
			} else {
				if (expNode.getLeft() instanceof GeoDummyVariable) {
					polyNode.getLeft()
							.setPoly(new PPolynomial(
									getVarOfGeoDummy(((GeoDummyVariable) expNode.getLeft())
											.toString(StringTemplate.defaultTemplate))));
				}
				if (expNode.getLeft() instanceof MySpecialDouble) {
					Double d = expNode.getLeft().evaluateDouble();
					BigInteger i;
					// if in the expression exists rational number with n
					// decimals
					// (if there's more than one rational number, then n is the
					// max of
					// decimal numbers)
					// than multiply the coefficient with 10^n
					if (nrOfMaxDecimals != 0) {
						i = new BigDecimal(d * Math.pow(10, nrOfMaxDecimals)).toBigInteger();
						Log.error("Possible num. error in converting formula coeff. to integer");
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
				buildPolynomialTree((ExpressionNode) expNode.getRight(), polyNode.getRight());
			} else {
				if (expNode.getRight() instanceof GeoDummyVariable) {
					try {
						polyNode.getRight()
								.setPoly(new PPolynomial(
										getVarOfGeoDummy(((GeoDummyVariable) expNode.getRight())
												.toString(StringTemplate.defaultTemplate))));
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
							i = polyNode.getLeft().getPoly().getConstant()
									.multiply(new BigInteger(Long.toString((long) d)));
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
					if (nrOfMaxDecimals != 0 && expNode.getOperation() != Operation.POWER) {
						i = new BigInteger(
								Long.toString(((long) (d * Math.pow(10, nrOfMaxDecimals)))));
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
	public void expressionNodeToPolynomial(ExpressionNode expNode, PolynomialNode polyNode)
			throws NoSymbolicParametersException {
		if (polyNode.getPoly() != null) {
			return;
		}
		if (polyNode.getLeft() != null && polyNode.getRight() == null
				&& polyNode.getOperation() == Operation.NO_OPERATION) {
			PPolynomial leftPoly = polyNode.getLeft().getPoly();
			polyNode.setPoly(leftPoly);
		}
		if (polyNode.getLeft() != null && polyNode.getLeft().getPoly() != null
				&& polyNode.getRight() != null && polyNode.getRight().getPoly() != null) {
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
		if (expNode.getLeft().isExpressionNode() && polyNode.getLeft().getPoly() == null) {
			expressionNodeToPolynomial((ExpressionNode) expNode.getLeft(), polyNode.getLeft());
		}
		if (expNode.getRight() != null && expNode.getRight().isExpressionNode()
				&& polyNode.getRight().getPoly() == null) {
			expressionNodeToPolynomial((ExpressionNode) expNode.getRight(), polyNode.getRight());
		}
		if (expNode.getLeft() instanceof MyDouble && polyNode.getLeft().getPoly() == null) {
			BigInteger coeff = new BigDecimal(expNode.getLeft().evaluateDouble()).toBigInteger();
			polyNode.getLeft().setPoly(new PPolynomial(coeff));
		}
		if (expNode.getRight() instanceof MyDouble && polyNode.getRight().getPoly() == null) {
			BigInteger coeff = new BigDecimal(expNode.getRight().evaluateDouble()).toBigInteger();
			polyNode.getRight().setPoly(new PPolynomial(coeff));
		}
		if (expNode.getLeft() instanceof MyDouble
				&& expNode.getRight() instanceof GeoDummyVariable) {
			BigInteger coeff = new BigDecimal(expNode.getLeft().evaluateDouble()).toBigInteger();
			PVariable v = getVarOfGeoDummy(
					expNode.getRight().toString(StringTemplate.defaultTemplate));
			if (v != null) {
				PTerm t = new PTerm(v);
				polyNode.setPoly(new PPolynomial(coeff, t));
				return;
			}
		}
	}

	private String getStrForGiac(String str) {
		StringBuilder strForGiac = new StringBuilder();
		strForGiac.append("eliminate([");
		strForGiac.append(str);
		StringBuilder labelsStr = new StringBuilder();

		labelsStr.append(Kernel.TMP_VARIABLE_PREFIX);
		labelsStr.append(botanaVars[0].toString());

		strForGiac.append(",");
		strForGiac.append(Kernel.TMP_VARIABLE_PREFIX);
		strForGiac.append(botanaVars[0].toString());
		strForGiac.append("=");
		strForGiac.append(botanaVars[0].toString());
		Iterator<GeoSegment> it = allSegmentsFromExpression.iterator();
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
		strForGiac.append(labelsStr);
		strForGiac.append("])");
		return strForGiac.toString();
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

}
