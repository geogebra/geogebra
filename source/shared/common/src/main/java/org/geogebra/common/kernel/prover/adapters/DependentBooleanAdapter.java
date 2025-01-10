package org.geogebra.common.kernel.prover.adapters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.algos.AlgoDistancePoints;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
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
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PTerm;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

public class DependentBooleanAdapter extends ProverAdapter {
	private boolean leftWasDist = false, rightWasDist = false;
	private boolean substNeeded = false;
	private Set<GeoSegment> allSegmentsFromExpression = new HashSet<>();
	private ArrayList<PPolynomial> extraPolys = new ArrayList<>();
	private int nrOfMaxDecimals;

	// substitution list of segments with variables
	private ArrayList<Map.Entry<GeoElement, PVariable>> varSubstListOfSegs;

	public PPolynomial[][] getBotanaPolynomials(GeoBoolean bool,
			Construction cons) throws NoSymbolicParametersException {
		ExpressionNode root = bool.getDefinition();
		Kernel kernel = cons.getKernel();
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
				AlgoArePerpendicular algo = new AlgoArePerpendicular(cons, left,
						right);
				PPolynomial[][] ret = algo.getBotanaPolynomials();
				cons.removeFromConstructionList(algo);
				return ret;
			}
			if (root.getOperation().equals(Operation.PARALLEL)) {
				AlgoAreParallel algo = new AlgoAreParallel(cons, left, right);
				PPolynomial[][] ret = algo.getBotanaPolynomials();
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
					PPolynomial[][] ret = algo.getBotanaPolynomials();
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
				PPolynomial[][] ret = algo.getBotanaPolynomials();
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
				PPolynomial[][] ret = algo.getBotanaPolynomials();
				cons.removeFromConstructionList(algo);
				return ret;
			}
		}

		// handle special case, when left expression is given by another algo
		if (!(root.getLeft().isExpressionNode())
				&& !(root.getLeft() instanceof MyDouble)) {
			AlgoElement algo = ((GeoElement) root.getLeft())
					.getParentAlgorithm();
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
		if (((root.getLeft().isExpressionNode()
				|| root.getRight().isExpressionNode())
				&& root.getOperation().equals(Operation.EQUAL_BOOLEAN))
				|| (root.getLeft() instanceof GeoElement
						&& root.getRight() instanceof MyDouble
						&& root.getOperation()
								.equals(Operation.EQUAL_BOOLEAN))) {
			traverseExpression(root, kernel);
			// try to check substituted and expanded expression

			ExpressionNode rootCopy = root.deepCopy(kernel);
			// collect all labels of GeoNumerics from expression
			Set<String> setOfGeoNumLabels = new TreeSet<>();
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
			traverseExpression(rootCopy, kernel);

			if (((rootCopy.getLeft() instanceof GeoSegment
					&& rootCopy.getRight() instanceof MyDouble)
					|| (rootCopy.getRight() instanceof GeoSegment
							&& rootCopy.getLeft() instanceof MyDouble))
					&& rootCopy.getOperation()
							.equals(Operation.EQUAL_BOOLEAN)) {
				PPolynomial[][] ret = null;
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
						&& !"{}".equals(expandGiacOutput)) {
					// parse expanded string into expression
					ValidExpression expandValidExp = (kernel.getGeoGebraCAS())
							.getCASparser()
							.parseGeoGebraCASInputAndResolveDummyVars(
									expandGiacOutput, kernel, null);
					traverseExpression((ExpressionNode) expandValidExp, kernel);
				}
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				Log.debug(e);
			}

			PPolynomial[][] ret = null;
			return ret;

		}
		throw new NoSymbolicParametersException(); // unhandled expression

	}

	// procedure to traverse inorder the expression
	private void traverseExpression(ExpressionNode node, Kernel kernel)
			throws NoSymbolicParametersException {
		if (node.getLeft() != null && node.getLeft().isGeoElement()
				&& node.getLeft() instanceof GeoSegment) {
			// if segment was given with command, eg. Segment[A,B]
			// set new name for segment (which giac will use later)
			if (((GeoSegment) node.getLeft()).getLabelSimple() == null) {
				((GeoSegment) node.getLeft())
						.setLabel(new PVariable(kernel).toString());
			}
			allSegmentsFromExpression.add((GeoSegment) node.getLeft());
		}
		if (node.getRight() != null && node.getRight().isGeoElement()
				&& node.getRight() instanceof GeoSegment) {
			// if segment was given with command, eg. Segment[A,B]
			// set new name for segment (which giac will use later)
			if (((GeoSegment) node.getRight()).getLabelSimple() == null) {
				((GeoSegment) node.getRight())
						.setLabel(new PVariable(kernel).toString());
			}
			allSegmentsFromExpression.add((GeoSegment) node.getRight());
		}
		if (node.getLeft() != null && node.getLeft().isExpressionNode()) {
			traverseExpression((ExpressionNode) node.getLeft(), kernel);
		}
		if (node.getRight() != null && node.getRight().isExpressionNode()) {
			traverseExpression((ExpressionNode) node.getRight(), kernel);
		}

		if (node.getLeft() != null && node.getLeft().isExpressionNode()
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

	/**
	 * @return string for giac from input expression
	 * @throws NoSymbolicParametersException
	 *             when no polynomials can be obtained
	 */
	public String getStrForGiac(GeoBoolean bool, Construction cons)
			throws NoSymbolicParametersException {
		Kernel kernel = cons.getKernel();
		String[] labels = new String[allSegmentsFromExpression.size()];
		extraPolys.clear();
		if (botanaVars == null) {
			botanaVars = new PVariable[allSegmentsFromExpression.size()];
		}
		if (varSubstListOfSegs == null) {
			varSubstListOfSegs = new ArrayList<>();
		}
		int index = 0;
		for (GeoSegment segment : allSegmentsFromExpression) {
			labels[index] = segment.getLabel(StringTemplate.giacTemplate);
			if (botanaVars[index] == null) {
				botanaVars[index] = new PVariable(kernel);
			}
			// collect substitution of segments with variables
			Entry<GeoElement, PVariable> subst = new AbstractMap.SimpleEntry<>(
					segment, botanaVars[index]);
			if (!varSubstListOfSegs.isEmpty()) {
				Iterator<Entry<GeoElement, PVariable>> it = varSubstListOfSegs
						.iterator();
				int k = 0;
				while (it.hasNext()) {
					Entry<GeoElement, PVariable> curr = it.next();
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
			PVariable[] thisSegBotanaVars = segment.getBotanaVars(segment);
			PPolynomial s = new PPolynomial(botanaVars[index]);
			PPolynomial currPoly = s.multiply(s)
					.subtract(PPolynomial.sqrDistance(thisSegBotanaVars[0],
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
			Set<String> setOfGeoNumLabels = new TreeSet<>();
			rootCopy.traverse(
					GeoNumericLabelCollector.getCollector(setOfGeoNumLabels));
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
		String[] splitStr = rootStr.split(",");
		/*
		 * This 10 is hardcoded, it is the length of "[ggbIsZero" which
		 * is the beginning of rootStr. FIXME
		 */
		rootStr = splitStr[0].substring(10, splitStr[0].length() - 1);
		StringBuilder strForGiac = new StringBuilder();
		strForGiac.append("eliminate([");
		strForGiac.append(rootStr);
		strForGiac.append("=0");
		StringBuilder labelsStr = new StringBuilder();
		for (int i = 0; i < labels.length; i++) {
			if (i == 0) {
				labelsStr.append(labels[i]);
			} else {
				labelsStr.append(",");
				labelsStr.append(labels[i]);
			}
			strForGiac.append(",");
			strForGiac.append(labels[i]);
			strForGiac.append("^2=");
			strForGiac.append(botanaVars[i]);
			strForGiac.append("^2");
		}
		strForGiac.append("],[");
		strForGiac.append(labelsStr);
		strForGiac.append("])");
		Log.debug(strForGiac.toString());
		return strForGiac.toString();
	}

	/**
	 * @return string for giac
	 */
	public String getUserGiacString(GeoBoolean bool, Construction cons) {
		Kernel kernel = cons.getKernel();
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
			Set<String> setOfGeoNumLabels = new TreeSet<>();
			rootCopy.traverse(
					GeoNumericLabelCollector.getCollector(setOfGeoNumLabels));
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
		String[] splitStr = rootStr.split(",");
		/*
		 * This 10 is hardcoded, it is the length of "[ggbIsZero" which
		 * is the beginning of rootStr. FIXME
		 */
		rootStr = splitStr[0].substring(10, splitStr[0].length() - 1);
		StringBuilder strForGiac = new StringBuilder();
		strForGiac.append("eliminate([");
		strForGiac.append(rootStr);
		strForGiac.append("=0");
		StringBuilder labelsStr = new StringBuilder();
		for (int i = 0; i < labels.length; i++) {
			if (i == 0) {
				labelsStr.append(labels[i]);
			} else {
				labelsStr.append(",");
				labelsStr.append(labels[i]);
			}
			strForGiac.append(",").append(labels[i]).append("=").append(botanaVars[i]);
		}
		strForGiac.append("],[");
		strForGiac.append(labelsStr);
		strForGiac.append("])");
		return strForGiac.toString();
	}

	/**
	 * @return distance polynomials
	 */
	public ArrayList<PPolynomial> getExtraPolys() {
		return extraPolys;
	}

	/**
	 * @return substitution list of segments with variables
	 */
	public ArrayList<Entry<GeoElement, PVariable>> getVarSubstListOfSegs() {
		return varSubstListOfSegs;
	}

	// get Variable with given name
	private PVariable getVariable(String varStr) {
		if (botanaVars != null) {
			for (int i = 0; i < botanaVars.length; i++) {
				if (varStr.equals(botanaVars[i].getName())) {
					return botanaVars[i];
				}
			}
		}
		return null;
	}

	private PVariable getBotanaVar(String str) {
		for (PVariable variable : botanaVars) {
			if (variable.getName().equals(str)) {
				return variable;
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
			polyNode.setPoly(new PPolynomial(i));
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
							.setPoly(new PPolynomial(
									getBotanaVar(expNode.getLeft().toString(
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
					} else {
						i = d.intValue();
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
								getBotanaVar(expNode.getRight().toString(
										StringTemplate.defaultTemplate))));
					} catch (Exception e) {
						throw new NoSymbolicParametersException();
					}
				}
				if (expNode.getRight() instanceof MySpecialDouble) {
					double d = expNode.getRight().evaluateDouble();
					BigInteger i;
					// simplify the polynomial if in expression is product of
					// numbers
					if (polyNode.getLeft().getPoly() != null
							&& polyNode.getLeft().getPoly().isConstant()) {
						switch (polyNode.getOperation()) {
						case MULTIPLY:
							i = polyNode.getLeft().getPoly().getConstant()
									.multiply(new BigInteger(
											Long.toString((long) d)));
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
						i = new BigInteger(Long.toString(
								((long) (d * Math.pow(10, nrOfMaxDecimals)))));
					} else {
						i = new BigInteger(Long.toString(((long) d)));
					}
					polyNode.getRight().setPoly(new PPolynomial(i));
				}
			}
		}
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
		if (expNode.getRight().isExpressionNode()
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
			PVariable v = getVariable(expNode.getRight()
					.toString(StringTemplate.defaultTemplate));
			if (v != null) {
				PTerm t = new PTerm(v);
				polyNode.setPoly(new PPolynomial(coeff, t));
				return;
			}
		}
	}

}
