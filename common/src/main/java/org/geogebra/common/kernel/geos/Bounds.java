package org.geogebra.common.kernel.geos;

import java.util.ArrayList;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Container for condition tripples (upper bound, lower bound, other conditions)
 * 
 * @author Zbynek
 * 
 */
public class Bounds {
	private boolean lowerSharp;
	private boolean upperSharp;
	private Double lower;
	private Double upper;
	private ExpressionNode condition;
	@Weak
	private Kernel kernel;
	private FunctionVariable fv;

	/**
	 * @param kernel
	 *            kernel
	 * @param fv
	 *            function variable
	 */
	public Bounds(Kernel kernel, FunctionVariable fv) {
		this.kernel = kernel;
		this.fv = fv;
	}

	/**
	 * Adds restrictions from the expression to current bounds
	 * 
	 * @param e0
	 *            expression
	 * @return new bounds
	 */
	public Bounds addRestriction(ExpressionNode e0) {
		ExpressionNode e = unfunction(e0);
		if (condition != null && condition.getOperation() == Operation.OR) {
			Bounds simple = copyInterval().addRestriction(e);
			Bounds left = simple.addRestriction(condition.getLeft().wrap());
			Bounds right = simple.addRestriction(condition.getRight().wrap());
			if (!left.isValid()) {
				return right;
			}
			if (!right.isValid()) {
				return left;
			}
		}
		if (e.getOperation().equals(Operation.AND)
				|| e.getOperation().equals(Operation.AND_INTERVAL)) {
			return addRestriction(e.getLeftTree())
					.addRestriction(e.getRightTree());
		}

		Bounds b = copyInterval();
		b.condition = condition; // If[x==1,1,If[x==2,3,4]]
		ExpressionValue lt = evalConstants(e.getLeft().unwrap());
		ExpressionValue rt = e.getRight() == null ? null
				: evalConstants(e.getRight().unwrap());

		boolean simple = e.getOperation() == Operation.GREATER
				|| e.getOperation() == Operation.GREATER_EQUAL
				|| e.getOperation() == Operation.LESS
				|| e.getOperation() == Operation.LESS_EQUAL
				|| e.getOperation() == Operation.EQUAL_BOOLEAN;

		if (simple && lt instanceof FunctionVariable
				&& rt instanceof NumberValue
				&& !(rt instanceof FunctionVariable)) {
			double d = rt.evaluateDouble();
			if (e.getOperation() == Operation.GREATER
					&& (lower == null || lower <= d)) {
				b.lower = d;
				b.lowerSharp = true;
			} else if ((e.getOperation() == Operation.GREATER_EQUAL
					|| e.getOperation() == Operation.EQUAL_BOOLEAN)
					&& (lower == null || lower < d)) {
				b.lower = d;
				b.lowerSharp = false;
			} else if (e.getOperation() == Operation.LESS
					&& (upper == null || upper >= d)) {
				b.upper = d;
				b.upperSharp = true;
			}
			if ((e.getOperation() == Operation.LESS_EQUAL
					|| e.getOperation() == Operation.EQUAL_BOOLEAN)
					&& (upper == null || upper > d)) { // x > d
				b.upper = d;
				b.upperSharp = false;
			}
		} else if (simple && rt instanceof FunctionVariable
				&& lt instanceof NumberValue
				&& !(lt instanceof FunctionVariable)) {
			double d = lt.evaluateDouble();
			if (e.getOperation() == Operation.LESS
					&& (lower == null || lower <= d)) {
				b.lower = d;
				b.lowerSharp = true;
			} else if ((e.getOperation() == Operation.LESS_EQUAL
					|| e.getOperation() == Operation.EQUAL_BOOLEAN)
					&& (lower == null || lower < d)) {
				b.lower = d;
				b.lowerSharp = false;
			} else if (e.getOperation() == Operation.GREATER
					&& (upper == null || upper >= d)) {
				b.upper = d;
				b.upperSharp = true;
			}
			if ((e.getOperation() == Operation.GREATER_EQUAL
					|| e.getOperation() == Operation.EQUAL_BOOLEAN)
					&& (upper == null || upper > d)) {
				b.upper = d;
				b.upperSharp = false;
			}
		} else {
			if (condition == null) {
				b.condition = e;
			} else {
				b.condition = condition.and(e);
			}
		}
		// If[x==1,2,If[x==3,4,5]]
		if (b.upper != null && b.lower != null && (b.condition != null)
				&& DoubleUtil.isEqual(b.upper.doubleValue(),
						b.lower.doubleValue())) {
			fv.set(b.upper);
			ExpressionValue v = b.condition
					.evaluate(StringTemplate.defaultTemplate);
			if (v instanceof BooleanValue && ((BooleanValue) v).getBoolean()) {
				b.condition = null;
			}
		}
		// If[x==1,2,If[x>3,4,5]]
		if (b.condition != null
				&& b.condition.getOperation() == Operation.NOT_EQUAL) {
			if (b.condition.getLeft() instanceof FunctionVariable
					&& b.condition.getRight() instanceof MyDouble) {
				double d = ((MyDouble) b.condition.getRight()).getDouble();
				if ((b.lower != null && d < b.lower)
						|| (b.upper != null && d > b.upper)) {
					b.condition = null;
				}
			} else if (b.condition.getRight() instanceof FunctionVariable
					&& b.condition.getLeft() instanceof MyDouble) {
				double d = ((MyDouble) b.condition.getLeft()).getDouble();
				if ((b.lower != null && d < b.lower)
						|| (b.upper != null && d > b.upper)) {
					b.condition = null;
				}
			}
		}
		return b;
	}

	private Bounds copyInterval() {
		Bounds b = new Bounds(kernel, fv);
		b.lower = lower;
		b.upper = upper;
		b.lowerSharp = lowerSharp;
		b.upperSharp = upperSharp;
		return b;
	}

	private ExpressionNode unfunction(ExpressionNode e) {
		if (e.getOperation() == Operation.FUNCTION
				&& e.getLeft() instanceof GeoFunction) {
			GeoFunction fn = ((GeoFunction) e.getLeft());
			return fn.getFunctionExpression().deepCopy(kernel)
					.replace(fn.getFunctionVariables()[0], fv).wrap();
		}
		return e;
	}

	private static ExpressionValue evalConstants(ExpressionValue rt) {
		if (rt != null && !rt.wrap().containsFreeFunctionVariable(null)) {
			return rt.evaluate(StringTemplate.defaultTemplate);
		}
		return rt;
	}

	/**
	 * @return whether this can be true for some x (allows false negatives)
	 */
	public boolean isValid() {
		return lower == null || upper == null || lower <= upper;
	}

	/**
	 * @param symbolic
	 *            true to keep variable names
	 * @param varString
	 *            variable string
	 * @param tpl
	 *            string template
	 * @return LaTeX string
	 */
	public String toLaTeXString(boolean symbolic, String varString,
			StringTemplate tpl) {
		StringBuilder ret = new StringBuilder();

		if (tpl.hasType(StringType.LATEX)) {

			if (upper == null && lower != null) {
				ret.append(varString);
				ret.append(" ");
				ret.append(lowerSharp ? ">" : Unicode.GREATER_EQUAL);
				ret.append(" ");
				ret.append(kernel.format(lower, tpl));
			} else if (lower == null && upper != null) {
				ret.append(varString);
				ret.append(" ");
				ret.append(upperSharp ? "<" : Unicode.LESS_EQUAL);
				ret.append(" ");
				ret.append(kernel.format(upper, tpl));
			} else if (lower != null && upper != null) {
				if (DoubleUtil.isEqual(lower, upper) && !lowerSharp
						&& !upperSharp) {
					ret.append(varString);
					ret.append(" = ");
					ret.append(kernel.format(lower, tpl));
				} else {
					ret.append(kernel.format(lower, tpl));
					ret.append(" ");
					ret.append(lowerSharp ? "<" : Unicode.LESS_EQUAL);
					ret.append(" ");
					ret.append(varString);
					ret.append(" ");
					ret.append(upperSharp ? "<" : Unicode.LESS_EQUAL);
					ret.append(" ");
					ret.append(kernel.format(upper, tpl));
				}
			}
			// upper and lower are null, we only retrn condition right here
			else if (condition != null) {
				return condition.toLaTeXString(symbolic, tpl);
			}
			// we may still need to append condition
			if (condition != null) {
				ret.insert(0, "(");
				ret.append(")\\wedge \\left(");
				ret.append(condition.toLaTeXString(symbolic, tpl));
				ret.append("\\right)");
			}

		} else {
			// StringType.MATHML
			// <apply><lt/><ci>x</ci><cn>3</cn></apply>

			if (upper == null && lower != null) {
				ret.append("<apply>");
				ret.append(lowerSharp ? "<gt/>" : "<geq/>");
				ret.append("<ci>");
				ret.append(varString);
				ret.append("</ci><cn>");
				ret.append(kernel.format(lower, tpl));
				ret.append("</cn></apply>");
			} else if (lower == null && upper != null) {
				ret.append("<apply>");
				ret.append(upperSharp ? "<lt/>" : "<leq/>");
				ret.append("<ci>");
				ret.append(varString);
				ret.append("</ci><cn>");
				ret.append(kernel.format(upper, tpl));
				ret.append("</cn></apply>");
			} else if (lower != null && upper != null) {
				if (DoubleUtil.isEqual(lower, upper) && !lowerSharp
						&& !upperSharp) {
					ret.append("<apply>");
					ret.append("<eq/>");
					ret.append("<ci>");
					ret.append(varString);
					ret.append("</ci><cn>");
					ret.append(kernel.format(lower, tpl));
					ret.append("</cn></apply>");
				} else {

					if (lowerSharp == upperSharp) {
						ret.append("<apply>");
						ret.append(lowerSharp ? "<lt/>" : "<leq/>");
						ret.append("<cn>");
						ret.append(kernel.format(lower, tpl));
						ret.append("</cn>");
						ret.append("<ci>");
						ret.append(varString);
						ret.append("</ci>");
						ret.append("<cn>");
						ret.append(kernel.format(upper, tpl));
						ret.append("</cn>");
						ret.append("</apply>");
					} else {
						// more complex for eg 3 < x <= 5

						ret.append("<apply>"); // <apply>
						ret.append("<and/>"); // <and/>
						ret.append("<apply>"); // <apply>
						ret.append(lowerSharp ? "<lt/>" : "<leq/>"); // <lt/>
						ret.append("<cn>");
						ret.append(kernel.format(lower, tpl));
						ret.append("</cn>"); // <cn>3</cn>
						ret.append("<ci>");
						ret.append(varString);
						ret.append("</ci>"); // <ci>x</ci>
						ret.append("</apply>"); // </apply>
						ret.append("<apply>"); // <apply>
						ret.append(upperSharp ? "<lt/>" : "<leq/>"); // <leq/>
						ret.append("<ci>");
						ret.append(varString);
						ret.append("</ci>"); // <ci>x</ci>
						ret.append("<cn>");
						ret.append(kernel.format(upper, tpl));
						ret.append("</cn>"); // <cn>5</cn>
						ret.append("</apply>"); // </apply>
						ret.append("</apply>"); // </apply>
					}

				}
			}
			// upper and lower are null, just return condition
			else if (condition != null) {
				return condition.toLaTeXString(symbolic, tpl);
			}
			// we may still need to append condition
			if (condition != null) {

				// prepend
				ret.insert(0, "<apply><and/>");
				ret.append(condition.toLaTeXString(symbolic, tpl));
				ret.append("</apply>");

			}

		}

		return ret.toString();
	}

	/**
	 * @return lower bound or -inf if unbounded
	 */
	public double getLower() {
		if (lower == null && condition != null) {
			Operation op = condition.getOperation();
			if (op == Operation.FUNCTION) {
				GeoFunction f = (GeoFunction) condition.getLeft();
				ExpressionNode exp = f.getFunctionExpression();
				if (exp.getOperation() == Operation.AND_INTERVAL) {
					ExpressionNode left = (ExpressionNode) exp.getLeft();
					ExpressionNode right = (ExpressionNode) exp.getRight();

					Operation opLeft = left.getOperation();
					Operation opRight = right.getOperation();

					if (opLeft.isInequalityLess()
							&& opRight.isInequalityLess()) {

						if (left.getRight() instanceof FunctionVariable) {
							return left.getLeft().evaluateDouble();
						} else if (right
								.getRight() instanceof FunctionVariable) {
							return right.getLeft().evaluateDouble();
						}

					}

				}
			}
		}

		return lower == null ? Double.valueOf(Double.NEGATIVE_INFINITY) : lower;
	}

	/**
	 * @return upper bound or +inf if unbounded
	 */
	public Double getUpper() {
		if (upper == null && condition != null) {
			Operation op = condition.getOperation();
			if (op == Operation.FUNCTION) {
				GeoFunction f = (GeoFunction) condition.getLeft();
				ExpressionNode exp = f.getFunctionExpression();
				if (exp.getOperation() == Operation.AND_INTERVAL) {
					ExpressionNode left = (ExpressionNode) exp.getLeft();
					ExpressionNode right = (ExpressionNode) exp.getRight();

					Operation opLeft = left.getOperation();
					Operation opRight = right.getOperation();

					if (opLeft.isInequalityLess()
							&& opRight.isInequalityLess()) {

						if (left.getLeft() instanceof FunctionVariable) {
							return left.getRight().evaluateDouble();
						} else if (right
								.getLeft() instanceof FunctionVariable) {
							return right.getRight().evaluateDouble();
						}
					}
				}
			}
		}
		return upper == null ? Double.valueOf(Double.POSITIVE_INFINITY) : upper;
	}

	@Override
	public String toString() {
		return (condition == null ? ""
				: this.condition.toString(StringTemplate.xmlTemplate)) + " on ("
				+ lower + "," + upper + ")";
	}

	/**
	 * @param condRoot
	 *            root of conditional expression
	 * @param cases
	 *            list of expressions for individual branches
	 * @param conditions
	 *            conditions for branches
	 * @param parentCond
	 *            condition for the root
	 * @param exclusive
	 *            whether to enforce mutually exclusive conditions
	 * @return whether parentCond is completely covered by the cases
	 */
	public static boolean collectCases(ExpressionNode condRoot,
			ArrayList<ExpressionNode> cases, ArrayList<Bounds> conditions,
			Bounds parentCond, boolean exclusive) {
		if (condRoot.getOperation() == Operation.IF_LIST) {
			MyList conds = (MyList) condRoot.getLeft().unwrap();
			Bounds currentCond = parentCond;
			for (int i = 0; i < conds.size(); i++) {
				conditions.add(currentCond
						.addRestriction(conds.getListElement(i).wrap()));
				if (exclusive) {
					currentCond = currentCond.addRestriction(parentCond
							.unfunction(conds.getListElement(i).wrap())
							.negation());
				}
			}

			MyList fns = (MyList) condRoot.getRight().unwrap();
			for (int i = 0; i < fns.size(); i++) {
				cases.add(fns.getListElement(i).wrap());
			}
			if (fns.size() > conds.size()) {
				conditions.add(parentCond);
			}
			return fns.size() > conds.size();
		}
		boolean complete = condRoot.getOperation() == Operation.IF_ELSE;
		ExpressionNode condFun = complete
				? ((MyNumberPair) condRoot.getLeft()).getX().wrap()
				: condRoot.getLeft().wrap();
		ExpressionNode ifFun = complete
				? ((MyNumberPair) condRoot.getLeft()).getY().wrap()
				: condRoot.getRight().wrap();
		ExpressionNode elseFun = complete ? condRoot.getRight().wrap() : null;

		Bounds positiveCond = parentCond.addRestriction(condFun);
		Bounds negativeCond = !positiveCond.isValid() ? parentCond
				: parentCond.addRestriction(condFun.negation());
		if (ifFun.isConditional()) {
			complete &= collectCases(ifFun, cases, conditions, positiveCond,
					true);
		} else {
			cases.add(ifFun);
			conditions.add(positiveCond);
		}

		if (elseFun != null && elseFun.isConditional()) {
			complete &= collectCases(elseFun, cases, conditions, negativeCond,
					true);
		} else if (elseFun != null) {
			cases.add(elseFun);
			conditions.add(negativeCond);
		}
		return complete;
	}

	/**
	 * @return whether this is a simple interval
	 */
	public boolean isInterval() {
		return this.condition == null;
	}
}