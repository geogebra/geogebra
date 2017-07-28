package org.geogebra.common.kernel.stepbystep;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.GeoGebraCasInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.Traversing.VariablePolyReplacer;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

public class StepByStepHelper {

	private Kernel kernel;
	private GeoGebraCasInterface cas;
	private Parser parser;
	private StringTemplate tpl;

	private TreeOperations op;

	public StepByStepHelper(Kernel kernel) {
		this.kernel = kernel;
		cas = kernel.getGeoGebraCAS();
		parser = kernel.getParser();
		tpl = StringTemplate.defaultTemplate;

		op = new TreeOperations(kernel);
	}

	public void inorder(ExpressionValue ev) {
		if (ev != null) {
			if (ev.isExpressionNode()) {
				ExpressionNode en = (ExpressionNode) ev;
				inorder(en.getLeft());
				Log.error(en.getOperation().toString());
				inorder(en.getRight());
			} else {
				Log.error(ev.toString(tpl));
			}
		}
		Log.error("null");
	}

	public ExpressionValue getExpressionTree(String s) {
		if (s.isEmpty()) {
			return null;
		}

		try {
			return parser.parseGeoGebraExpression(s);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the lowest common denominator of the expression tree
	 * 
	 * @param ev
	 *            expression tree to parse
	 * 
	 * @return lowest common denominator as a string
	 */

	public ExpressionValue getDenominator(ExpressionValue ev) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.getOperation() == Operation.DIVIDE) {
				return en.getRight();
			}

			if (en.getOperation() == Operation.PLUS || en.getOperation() == Operation.MINUS || en.getOperation() == Operation.MULTIPLY) {
				ExpressionValue toReturnLeft = getDenominator(en.getLeft());
				ExpressionValue toReturnRight = getDenominator(en.getRight());

				if (toReturnLeft != null && toReturnRight != null) {
					return LCM(toReturnLeft, toReturnRight);
				} else if (toReturnLeft != null) {
					return toReturnLeft;
				} else {
					return toReturnRight;
				}
			}
		}
		return null;
	}

	public ExpressionValue getSQRoots(ExpressionValue ev) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.getOperation() == Operation.SQRT) {
				return en;
			}

			if (en.getOperation() == Operation.MULTIPLY || en.getOperation() == Operation.DIVIDE) {
				if (countOperation(en.getLeft(), Operation.SQRT) > 0 || countOperation(en.getRight(), Operation.SQRT) > 0) {
					return en;
				}
			}

			if (en.getOperation() == Operation.PLUS || en.getOperation() == Operation.MINUS) {
				ExpressionValue toReturnLeft = getSQRoots(en.getLeft());
				ExpressionValue toReturnRight = getSQRoots(en.getRight());

				if (en.getOperation() == Operation.PLUS) {
					return op.add(toReturnLeft, toReturnRight);
				}
				return op.subtract(toReturnLeft, toReturnRight);
			}
		}
		return null;
	}

	public ExpressionValue getNonIrrational(ExpressionValue ev) {
		return regroup(op.subtract(ev, getSQRoots(ev)));
	}

	public ExpressionValue getOneSquareRoot(ExpressionValue ev) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.getOperation() == Operation.SQRT) {
				return en;
			}

			if (en.getOperation() == Operation.MULTIPLY || en.getOperation() == Operation.DIVIDE) {
				if (countOperation(en.getLeft(), Operation.SQRT) > 0 || countOperation(en.getRight(), Operation.SQRT) > 0) {
					return en;
				}
			}

			if (en.getOperation() == Operation.PLUS || en.getOperation() == Operation.MINUS) {
				ExpressionValue toReturnLeft = getOneSquareRoot(en.getLeft());
				ExpressionValue toReturnRight = getOneSquareRoot(en.getRight());

				if (toReturnLeft != null) {
					return toReturnLeft;
				}
				if (en.getOperation() == Operation.PLUS) {
					return toReturnRight;
				}
				return op.minus(toReturnRight);
			}

		}
		return null;
	}

	public ExpressionValue findVariable(ExpressionValue ev, String variable) {
		if (ev != null && isEqual(ev.toString(tpl), variable)) {
			return ev;
		}
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (isEqual(en.toString(tpl), variable)) {
				return en;
			}

			if (en.getOperation() == Operation.MULTIPLY || en.getOperation() == Operation.DIVIDE) {
				if (isEqual(en.getLeft().toString(tpl), variable) || isEqual(en.getRight().toString(tpl), variable)) {
					return en;
				}
			}

			if (en.getOperation() == Operation.PLUS || en.getOperation() == Operation.MINUS) {
				ExpressionValue toReturn = findVariable(en.getLeft(), variable);
				if (toReturn != null) {
					return toReturn;
				}

				toReturn = findVariable(en.getRight(), variable);
				if (toReturn != null && en.getOperation() == Operation.MINUS) {
					return op.minus(toReturn);
				}
				return toReturn;
			}
		}
		return null;
	}

	public ExpressionValue findCoefficient(ExpressionValue ev, String variable) {
		if (ev != null && ev.toString(tpl).equals(variable)) {
			return new MyDouble(kernel, 1);
		}
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (isEqual(en.toString(tpl), variable)) {
				return new MyDouble(kernel, 1);
			}

			if (en.getOperation() == Operation.MULTIPLY || en.getOperation() == Operation.DIVIDE) {
				if (isEqual(en.getLeft().toString(tpl), variable)) {
					return en.getRight();
				} else if (isEqual(en.getRight().toString(tpl), variable)) {
					return en.getLeft();
				}
			}

			if (en.getOperation() == Operation.PLUS || en.getOperation() == Operation.MINUS) {
				ExpressionValue toReturnLeft = findCoefficient(en.getLeft(), variable);
				ExpressionValue toReturnRight = findCoefficient(en.getRight(), variable);

				if (en.getOperation() == Operation.PLUS) {
					return op.add(toReturnLeft, toReturnRight);
				}
				return op.subtract(toReturnLeft, toReturnRight);
			}
		}
		return null;
	}

	public ExpressionValue findCoefficient(ExpressionValue ev) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.getOperation() == Operation.MULTIPLY || en.getOperation() == Operation.DIVIDE) {
				if (!containsVariable(en.getLeft())) {
					return en.getLeft();
				} else if (!containsVariable(en.getRight())) {
					return en.getRight();
				}
			}
		}
		return null;
	}

	public ExpressionValue findConstant(ExpressionValue ev) {
		if (ev != null && !containsVariable(ev)) {
			return ev;
		}
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.getOperation() == Operation.PLUS || en.getOperation() == Operation.MINUS) {
				ExpressionValue toReturnLeft = findConstant(en.getLeft());
				ExpressionValue toReturnRight = findConstant(en.getRight());

				if (en.getOperation() == Operation.PLUS) {
					return op.add(toReturnLeft, toReturnRight);
				}
				return op.subtract(toReturnLeft, toReturnRight);
			}
		}

		return null;
	}

	public String getPower(ExpressionValue ev, String variable) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.getOperation() == Operation.POWER && containsVariable(en.getLeft())) {
				return en.getRight().toString(tpl);
			}

			if (en.getOperation() == Operation.MULTIPLY || en.getOperation() == Operation.DIVIDE) {
				String toReturn;

				toReturn = getPower(en.getLeft(), variable);
				if (!isZero(toReturn)) {
					return toReturn;
				}

				toReturn = getPower(en.getRight(), variable);
				return toReturn;
			}
		}

		return "";
	}

	public void getParts(ArrayList<String> parts, ExpressionValue ev) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.getOperation() == Operation.MULTIPLY) {
				getParts(parts, en.getLeft());
				getParts(parts, en.getRight());
			} else {
				parts.add(en.toString(tpl));
			}
		} else if (ev != null && !ev.isConstant()) {
			parts.add(ev.toString(tpl));
		}
	}

	public void getAbsoluteValues(ArrayList<String> absoluteValues, ExpressionValue ev) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.getOperation() == Operation.ABS) {
				absoluteValues.add(en.getLeft().toString(tpl));
			} else {
				getAbsoluteValues(absoluteValues, en.getLeft());
				getAbsoluteValues(absoluteValues, en.getRight());
			}
		}
	}

	public boolean containsVariable(ExpressionValue ev) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			boolean found;
			found = containsVariable(en.getLeft());
			found = found || containsVariable(en.getRight());

			return found;
		}

		return ev != null && !ev.isConstant();
	}

	public boolean containsLinear(ExpressionValue ev, String variable) {
		return !isZero(findCoefficient(ev, variable));
	}

	public boolean isProduct(ExpressionValue ev) {
		if (ev == null || !ev.isExpressionNode()) {
			return false;
		}
		return ((ExpressionNode) ev).getOperation() == Operation.MULTIPLY;
	}

	public boolean shouldTakeRoot(ExpressionValue evRHS, ExpressionValue evLHS, String variable) {
		ExpressionValue ev = regroup(op.subtract(evRHS, evLHS));
		ExpressionValue constants = findConstant(ev);
		ev = regroup(op.subtract(ev, constants));

		if(ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;
			
			if(en.getOperation() == Operation.POWER) {
				return true;
			}

			if (en.getOperation() == Operation.MULTIPLY || en.getOperation() == Operation.DIVIDE) {
				if (en.getLeft().isConstant() && en.getRight().isExpressionNode() && ((ExpressionNode) en.getRight()).getOperation() == Operation.POWER) {
					return true;
				}
				if (en.getRight().isConstant() && en.getLeft().isExpressionNode() && ((ExpressionNode) en.getLeft()).getOperation() == Operation.POWER) {
					return true;
				}
			} else if (!isZero(getPower(en.getRight(), variable)) && isEqual(getValue(getPower(en.getRight(),
					variable)), getValue(getPower(en.getLeft(), variable)))) {
				return true;
			}
		}
		
		return false;
	}

	public boolean canCompleteCube(ExpressionValue ev, String variable) {
		if (degree(ev) != 3) {
			return false;
		}

		ExpressionValue cubic = findCoefficient(ev, variable + "^3");
		ExpressionValue quadratic = findCoefficient(ev, variable + "^2");
		ExpressionValue linear = findCoefficient(ev, variable);

		if (!isOne(cubic)) {
			return false;
		}

		if (isEqual("(" + quadratic + ")^2", "3*(" + linear + ")")) {
			return true;
		}

		return false;
	}

	public boolean canBeReducedToQuadratic(ExpressionValue ev, String variable) {
		int degree = degree(ev);
		
		if(degree / 2 * 2 != degree) { 		// if degree is odd
			return false;
		}

		for (int i = 1; i < degree; i++) {
			if (i != degree / 2) {
				ExpressionValue coeff = findCoefficient(ev, variable + "^" + i);
				if (!isZero(coeff)) {
					return false;
				}
			}
		}

		return true;
	}

	public boolean integerCoefficients(ExpressionValue ev, String variable) {
		int degree = degree(ev);

		double constant = getValue(findConstant(ev));
		if (Math.floor(constant) != constant) {
			return false;
		}

		for (int i = 1; i <= degree; i++) {
			double coeff = getCoefficientValue(ev, variable + "^" + i);
			if (Math.floor(coeff) != coeff) {
				return false;
			}
		}

		return true;
	}

	public boolean shouldMultiply(ExpressionValue ev) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.getOperation() == Operation.DIVIDE) {
				if (containsVariable(en.getLeft()) || containsVariable(en.getRight())) {
					return true;
				}
			}

			if (en.getOperation() == Operation.MULTIPLY) {
				if (containsVariable(en.getLeft()) && countOperation(en.getRight(), Operation.DIVIDE) > 0) {
					return true;
				}
				if (containsVariable(en.getRight()) && countOperation(en.getLeft(), Operation.DIVIDE) > 0) {
					return true;
				}
			}

			boolean found = false;

			found |= shouldMultiply(en.getLeft());
			found |= shouldMultiply(en.getRight());

			return found;
		}

		return false;
	}

	public int countRoots(ExpressionValue ev) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.getOperation() == Operation.SQRT && (containsVariable(en.getLeft()) || containsVariable(en.getRight()))) {
				return 1;
			}

			return countRoots(en.getLeft()) + countRoots(en.getRight());
		}

		return 0;
	}

	public int countOperation(ExpressionValue ev, Operation operation) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.getOperation() == operation) {
				return 1;
			}

			return countOperation(en.getLeft(), operation) + countOperation(en.getRight(), operation);
		}

		return 0;
	}

	public ExpressionValue swapAbsInTree(ExpressionValue ev, String a, String b, String variable) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;
			if (en.getOperation() == Operation.ABS) {
				if (isNegative(en.getLeft().toString(tpl), a, b, variable)) {
					en.setOperation(Operation.MULTIPLY);
					en.setRight(en.getLeft());
					en.setLeft(new MyDouble(kernel, -1));
					return en;
				}
				return en.getLeft();
			}

			en.setLeft(swapAbsInTree(en.getLeft(), a, b, variable));
			en.setRight(swapAbsInTree(en.getRight(), a, b, variable));

			return en;
		}

		return ev;
	}

	private boolean isNegative(String x, String a, String b, String variable) {
		String evaluateAt;

		if ("-inf".equals(a)) {
			evaluateAt = simplify(b + "-10");
		} else if ("+inf".equals(b)) {
			evaluateAt = simplify(a + "+10");
		} else {
			evaluateAt = simplify("(" + a + "+" + b + ") / 2");
		}

		double val = getValue(x.replaceAll(variable, "(" + evaluateAt + ")"));
		return val < 0;
	}

	public double getValue(String s) {
		if (s.isEmpty()) {
			return 0;
		}

		if ("-inf".equals(s)) {
			return Double.NEGATIVE_INFINITY;
		} else if ("+inf".equals(s)) {
			return Double.POSITIVE_INFINITY;
		}

		String val = callCAS(s, "Numeric");

		try {
			return Double.parseDouble(val);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return 0;
	}

	public double getValue(ExpressionValue ev) {
		return ev == null ? 0 : getValue(ev.toString(tpl));
	}

	public void getRootsForValidation(List<String> roots, ExpressionValue ev) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.getOperation() == Operation.SQRT) {
				roots.add(en.getLeft().toString(tpl));
			} else {
				getRootsForValidation(roots, en.getLeft());
				getRootsForValidation(roots, en.getRight());
			}
		}
	}

	public boolean isValidSolution(ExpressionValue LHS, ExpressionValue RHS, ExpressionValue solution, String variable) {
		ExpressionValue bothSides = op.add(LHS, RHS);
		ExpressionValue denominators = getDenominator(bothSides);

		if (containsVariable(denominators)) {
			if (isEqual(evaluateAt(denominators, solution, variable), 0)) {
				return false;
			}
		}

		double evaluatedLHS = evaluateAt(LHS, solution, variable);
		double evaluatedRHS = evaluateAt(RHS, solution, variable);

		if (!isEqual(evaluatedLHS, evaluatedRHS)) {
			return false;
		}

		return true;
	}

	public String evaluateAt(String expression, String value, String variable) {
		return simplify(expression.replace(variable, "(" + value + ")"));
	}

	public double evaluateAt(ExpressionValue expression, ExpressionValue value, String variable) {
		FunctionVariable fVar = new FunctionVariable(kernel, variable);
		fVar.set(value.evaluateDouble());
		VariablePolyReplacer s = VariablePolyReplacer.getReplacer(fVar);
		return expression.traverse(s).evaluateDouble();
	}

	public double getCoefficientValue(ExpressionValue ev, String s) {
		return getValue(findCoefficient(ev, s));
	}

	private static boolean isZero(String s) {
		return stripSpaces(s).equals("") || stripSpaces(s).equals("0");
	}

	private static boolean isZero(ExpressionValue ev) {
		return ev == null || isZero(ev.toString(StringTemplate.defaultTemplate));
	}

	private static boolean isOne(String s) {
		return stripSpaces(s).equals("") || stripSpaces(s).equals("1");
	}

	private static boolean isOne(ExpressionValue ev) {
		return ev == null || isOne(ev.toString(StringTemplate.defaultTemplate));
	}

	private boolean isEqual(String a, String b) {
		return isZero(simplify(a + " - (" + b + ")"));
	}

	public boolean isEqual(ExpressionValue a, ExpressionValue b) {
		return isEqual(a.toString(tpl), b.toString(tpl));
	}

	public String callCAS(String s, String cmd) {
		return cas.evaluateGeoGebraCAS(cmd + "[" + s + "]", null, tpl, kernel);
	}

	public int degree(ExpressionValue ev) {
		String d = callCAS(ev.toString(tpl), "Degree");
		if ("?".equals(d)) {
			return -1;
		}
		return Integer.parseInt(d);
	}

	public ExpressionValue[] getCASSolutions(String LHS, String RHS, String variable) {
		String s = callCAS(LHS + " = " + RHS + ", " + variable, "Solutions");

		String[] solutions = s.replaceAll("[ {}]", "").split(",");

		if (solutions.length == 1 && "".equals(solutions[0])) {
			return new ExpressionValue[0];
		}

		ExpressionValue[] ev = new ExpressionValue[solutions.length];

		for (int i = 0; i < solutions.length; i++) {
			ev[i] = getExpressionTree(solutions[i]);
		}

		return ev;
	}

	public static String stripSpaces(String s) {
		return s.replaceAll(" ", "");
	}

	public ExpressionValue regroup(ExpressionValue ev) {
		return getExpressionTree(callCAS(callCAS(ev.toString(tpl), "Regroup"), "Regroup"));
	}

	public String regroup(String s) {
		return callCAS(s, "Regroup");
	}

	public String expand(String s) {
		return callCAS(s, "ExpandOnly");
	}

	public ExpressionValue expand(ExpressionValue ev) {
		return getExpressionTree(callCAS(ev.toString(tpl), "ExpandOnly"));
	}

	public ExpressionValue simplify(ExpressionValue ev) {
		return getExpressionTree(callCAS(ev.toString(tpl), "Simplify"));
	}

	public String simplify(String s) {
		return callCAS(s, "Simplify");
	}

	public String partialFractions(String s) {
		return callCAS(s, "PartialFractions");
	}

	public ExpressionValue LCM(ExpressionValue a, ExpressionValue b) {
		return getExpressionTree(callCAS("(" + a.toString(tpl) + "), (" + b.toString(tpl) + ")", "LCM"));
	}

	public ExpressionValue factor(ExpressionValue ev) {
		return getExpressionTree(callCAS(ev.toString(tpl), "Factor"));
	}

	private static boolean isEqual(double a, double b) {
		if (Math.abs(a - b) < 0.00000001) {
			return true;
		}
		return false;
	}
}
