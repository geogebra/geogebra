package org.geogebra.common.kernel.stepbystep;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.GeoGebraCasInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

public class StepByStepHelper {

	private Kernel kernel;
	private GeoGebraCasInterface cas;
	private Parser parser;
	private StringTemplate tpl;

	public StepByStepHelper(Kernel kernel) {
		this.kernel = kernel;
		cas = kernel.getGeoGebraCAS();
		parser = kernel.getParser();
		tpl = StringTemplate.defaultTemplate;
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
	 * 			expression tree to parse
	 * 
	 * @return lowest common denominator as a string
	 */
	
	public String getDenominator(ExpressionValue ev) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.getOperation() == Operation.DIVIDE) {
				return en.getRight().toString(tpl);
			}

			String toReturnLeft = getDenominator(en.getLeft());
			String toReturnRight = getDenominator(en.getRight());

			if (!toReturnLeft.isEmpty() && !toReturnRight.isEmpty()) {
				return LCM("(" + toReturnLeft + "), (" + toReturnRight + ")");
			} else if (!toReturnLeft.isEmpty()) {
				return toReturnLeft;
			} else {
				return toReturnRight;
			}
		}
		return "";
	}

	public String getSQRoots(ExpressionValue ev) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.getOperation() == Operation.SQRT) {
				return en.toString(tpl);
			}

			if (en.getOperation() == Operation.MULTIPLY || en.getOperation() == Operation.DIVIDE) {
				if (countOperation(en.getLeft(), Operation.SQRT) > 0 || 
						countOperation(en.getRight(), Operation.SQRT) > 0) {
					return en.toString(tpl);
				}
			}

			if (en.getOperation() == Operation.PLUS
					|| en.getOperation() == Operation.MINUS) {
				
				String toReturnLeft = getSQRoots(en.getLeft());
				String toReturnRight = getSQRoots(en.getRight());
				
				if (!toReturnRight.isEmpty()) {
					if(en.getOperation() == Operation.PLUS) {
						return toReturnLeft + " + " + toReturnRight;
					}
					return toReturnLeft + " - (" + toReturnRight + ")";
				}
				return toReturnLeft;
			}

		}
		return "";
	}
	
	public String getNonIrrational(ExpressionValue ev) {
		return regroup(ev.toString(tpl) + " - (" + getSQRoots(ev) + ")");
	}
	
	public String getOneSquareRoot(ExpressionValue ev) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.getOperation() == Operation.SQRT) {
				return en.toString(tpl);
			}

			if (en.getOperation() == Operation.MULTIPLY || en.getOperation() == Operation.DIVIDE) {
				if (countOperation(en.getLeft(), Operation.SQRT) > 0 || 
						countOperation(en.getRight(), Operation.SQRT) > 0) {
					return en.toString(tpl);
				}
			}

			if (en.getOperation() == Operation.PLUS
					|| en.getOperation() == Operation.MINUS) {
				
				String toReturnLeft = getSQRoots(en.getLeft());
				String toReturnRight = getSQRoots(en.getRight());
				
				if (!toReturnLeft.isEmpty()) {
					return toReturnLeft;
				}
				if (en.getOperation() == Operation.PLUS) {
					return toReturnRight;
				}
				return " - (" + toReturnRight + ")";
			}

		}
		return "";
	}

	public String findVariable(ExpressionValue ev, String variable) {
		if (ev != null && isEqual(ev.toString(tpl), variable)) {
			return ev.toString(tpl);
		}
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (isEqual(en.toString(tpl), variable)) {
				return en.toString(tpl);
			}

			if (en.getOperation() == Operation.MULTIPLY || en.getOperation() == Operation.DIVIDE) {
				if (isEqual(en.getLeft().toString(tpl), variable) ||
						isEqual(en.getRight().toString(tpl), variable)) {
					return en.toString(tpl);
				}
			}

			if (en.getOperation() == Operation.PLUS || en.getOperation() == Operation.MINUS) {
				String toReturn = findVariable(en.getLeft(), variable);
				if (!toReturn.isEmpty()) {
					return toReturn;
				}

				toReturn = findVariable(en.getRight(), variable);
				if (!toReturn.isEmpty() && en.getOperation() == Operation.MINUS) {
					return "- (" + toReturn + ")";
				}
				return toReturn;
			}

		}
		return "";
	}
	
	public String findCoefficient(ExpressionValue ev, String variable) {
		if (ev != null && ev.toString(tpl).equals(variable)) {
			return "1";
		}
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (isEqual(en.toString(tpl), variable)) {
				return "1";
			}

			if (en.getOperation() == Operation.MULTIPLY || en.getOperation() == Operation.DIVIDE) {
				if (isEqual(en.getLeft().toString(tpl), variable)) {
					return en.getRight().toString(tpl);
				} else if (isEqual(en.getRight().toString(tpl), variable)) {
					return en.getLeft().toString(tpl);
				}
			}

			if (en.getOperation() == Operation.PLUS || en.getOperation() == Operation.MINUS) {
				String toReturn = findCoefficient(en.getLeft(), variable);
				if (!isZero(toReturn)) {
					return toReturn;
				}

				toReturn = findCoefficient(en.getRight(), variable);
				if (!isZero(toReturn) && en.getOperation() == Operation.MINUS) {
					return "- (" + toReturn + ")";
				}
				return toReturn;
			}
		}
		return "";
	}
	
	public String findConstant(ExpressionValue ev) {
		if (ev != null && !containsVariable(ev)) {
			return ev.toString(tpl);
		}
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.getOperation() == Operation.PLUS
					|| en.getOperation() == Operation.MINUS) {
				String toReturn;

				toReturn = findConstant(en.getLeft());
				if (!isZero(toReturn)) {
					return toReturn;
				}

				toReturn = findConstant(en.getRight());
				if (!isZero(toReturn) && en.getOperation() == Operation.MINUS) {
					return "-(" + toReturn + ")";
				}

				return toReturn;
			}
		}

		return "0";
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

	public boolean isTrivial(String LHS, String RHS) {
		ExpressionValue ev = getExpressionTree(regroup(LHS + " - " + RHS));
		return countOperation(ev, Operation.PLUS) + countOperation(ev, Operation.MINUS) == 1 && 
				getValue(findConstant(ev)) != 0;
	}
	
	public int countOperation(ExpressionValue ev, Operation op) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			int found = 0;
			
			if (en.getOperation() == op) {
				found ++;
			}

			found += countOperation(en.getLeft(), op);
			found += countOperation(en.getRight(), op);

			return found;
		}

		return 0;
	}

	public String evaluateAbsoluteValue(String eq, String a, String b) {
		ExpressionValue ev = getExpressionTree(eq);
		return swapAbsInTree(ev, a, b).toString(tpl);
	}

	private ExpressionValue swapAbsInTree(ExpressionValue ev, String a, String b) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;
			if (en.getOperation() == Operation.ABS) {
				if (isNegative(en.getLeft().toString(tpl),a, b)) {
					en.setOperation(Operation.MULTIPLY);
					en.setRight(en.getLeft());
					en.setLeft(new MyDouble(kernel, -1));
					return en;
				}
				return en.getLeft();
			}

			en.setLeft(swapAbsInTree(en.getLeft(), a, b));
			en.setRight(swapAbsInTree(en.getRight(), a, b));

			return en;
		}

		return ev;
	}

	private boolean isNegative(String x, String a, String b) {
		String evaluateAt;

		if ("-inf".equals(a)) {
			evaluateAt = simplify(b + "-10");
		} else if ("+inf".equals(b)) {
			evaluateAt = simplify(a + "+10");
		} else {
			evaluateAt = simplify("(" + a + "+" + b + ") / 2");
		}

		double val = getValue(x.replaceAll("x", "(" + evaluateAt + ")"));
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

	public boolean isValidSolution(String LHS, String RHS, String solution) {
		ExpressionValue bothSides = getExpressionTree(LHS + " + " + RHS);
		String denominators = getDenominator(bothSides);

		if (containsVariable(getExpressionTree(denominators))) {
			String evaluatedDenominator = evaluateAt(denominators, solution);
			if (isZero(evaluatedDenominator)) {
				return false;
			}
		}

		String evaluatedLHS = evaluateAt(LHS, solution);
		String evaluatedRHS = evaluateAt(RHS, solution);

		if(!isEqual(evaluatedLHS, evaluatedRHS)) {
			return false;
		}

		return true;
	}

	public String evaluateAt(String expression, String value) {
		return simplify(expression.replace("x", "(" + value + ")"));
	}

	public double getCoefficientValue(ExpressionValue ev, String s) {
		return getValue(findCoefficient(ev, s));
	}

	private boolean isZero(String s) {
		return stripSpaces(s).isEmpty() || stripSpaces(s).equals("0");
	}

	private boolean isEqual(String a, String b) {
		return isZero(simplify(a + " - (" + b + ")"));
	}
	
	public String callCAS(String s, String cmd) {
		return cas.evaluateGeoGebraCAS(cmd + "[" + s + "]", null, tpl, kernel);
	}

	public int degree(String s) {
		String d = callCAS(s, "Degree");
		if ("?".equals(d)) {
			return -1;
		}
		return Integer.parseInt(d);
	}

	public String[] getCASSolutions(String LHS, String RHS) {
		String s = callCAS(LHS + " = " + RHS, "Solutions");
		return s.replaceAll("[ {}]", "").split(",");
	}
	
	public String stripSpaces(String s) {
		return s.replaceAll(" ", "");
	}

	public String regroup(String s) {
		return callCAS(s, "Regroup");
	}

	public String expand(String s) {
		return callCAS(s, "ExpandOnly");
	}

	public String simplify(String s) {
		return callCAS(s, "Simplify");
	}

	public String partialFractions(String s) {
		return callCAS(s, "PartialFractions");
	}

	public String LCM(String s) {
		return callCAS(s, "LCM");
	}

	public String factor(String s) {
		return callCAS(s, "Factor");
	}
}
