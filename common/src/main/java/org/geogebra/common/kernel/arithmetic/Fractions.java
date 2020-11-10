package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Fraction arithmetic utility class.
 */
public class Fractions {
	private static final double MAX_NUM_DENOMINATOR = 1E15;

	/**
	 * @param expr
	 *            expression
	 * @param kernel
	 *            kernel
	 * @param allowPi
	 *            whether to allow multiples of pi
	 * @return resolved expression: either MyDouble or simple fraction
	 */
	protected static ExpressionValue getResolution(ExpressionNode expr,
			Kernel kernel, boolean allowPi) {
		ExpressionValue[] fraction = new ExpressionValue[2];
		expr.getFraction(fraction, true);
		if (fraction[0] != null) {
			ExpressionValue ltVal = fraction[0].evaluate(StringTemplate.defaultTemplate);
			double lt = ltVal.evaluateDouble();

			boolean pi = false;
			double piDiv = lt / Math.PI;
			if (allowPi && DoubleUtil.isInteger(piDiv)
					&& !DoubleUtil.isZero(piDiv) && lt < MAX_NUM_DENOMINATOR) {
				lt = piDiv;
				pi = true;
			}
			double rt = 1;
			if (fraction[1] != null) {
				rt = fraction[1].evaluateDouble();
			} else if (!pi) {
				// keep angle dimension
				return ltVal.deepCopy(kernel).wrap();
			}
			if (DoubleUtil.isInteger(rt) && DoubleUtil.isInteger(lt) && !DoubleUtil.isZero(rt)
					&& Math.abs(lt) < MAX_NUM_DENOMINATOR && Math.abs(rt) < MAX_NUM_DENOMINATOR) {

				double g = Math.abs(Kernel.gcd(Math.round(lt), Math.round(rt))) * Math.signum(rt);
				lt = lt / g;
				rt = rt / g;
				return (pi ? multiplyPi(lt, kernel)
						: new ExpressionNode(kernel, lt)).divide(rt);
			}
			double ratio = lt / rt;
			return numericResolve(pi, ratio, expr, kernel);
		}
		return expr.evaluate(StringTemplate.defaultTemplate).wrap();
	}

	private static ExpressionNode multiplyPi(double lt, Kernel kernel) {
		MyDouble pi = new MySpecialDouble(kernel, Math.PI, Unicode.PI_STRING);
		if (MyDouble.exactEqual(lt, 1)) {
			return pi.wrap();
		} else {
			ExpressionValue left = lt == -1 ? new MinusOne(kernel) : new MyDouble(kernel, lt);
			return MyDouble.exactEqual(lt, 1)
					? pi.wrap()
					: new ExpressionNode(kernel, left, Operation.MULTIPLY, pi);
		}
	}

	private static ExpressionValue numericResolve(boolean pi, double ratio, ExpressionNode expr,
			Kernel kernel) {
		if (!MyDouble.isFinite(ratio)) {
			return new ExpressionNode(kernel, expr.evaluateDouble());
		}
		return new ExpressionNode(kernel, pi ? Math.PI * ratio : ratio);
	}

	private static boolean checkFraction(ExpressionValue[] parts, ExpressionValue lt,
			boolean expandPlus) {
		ExpressionValue left1 = lt == null ? null : lt.unwrap();
		if (left1 instanceof ExpressionNode) {
			((ExpressionNode) left1).getFraction(parts, expandPlus);
			return true;
		} else if (left1 instanceof GeoNumeric && ((GeoNumeric) left1).getDefinition() != null) {
			((GeoElement) left1).getDefinition().getFraction(parts, expandPlus);
			return true;
		}
		return false;
	}

	/**
	 * @param parts
	 *            output: [numerator, denominator]
	 * @param expr
	 *            expression
	 * @param expandPlus
	 *            whether to expand a/d+b/c to a single fraction
	 */
	protected static void getFraction(ExpressionValue[] parts, ExpressionNode expr,
			boolean expandPlus) {
		ExpressionValue numL, numR, denL = null, denR = null;
		if (checkFraction(parts, expr.getLeft(), expandPlus)) {

			numL = parts[0];
			denL = parts[1];
		} else {
			numL = expr.getLeft();
		}

		if (numL == null) {
			parts[0] = expr;
			parts[1] = null;
			return;
		}

		if (checkFraction(parts, expr.getRight(), expandPlus)) {
			numR = parts[0];
			denR = parts[1];
		} else {
			numR = expr.getRight();
		}
		Kernel kernel;
		switch (expr.getOperation()) {
		case MULTIPLY:
			parts[0] = numL.wrap().multiply(numR);
			parts[1] = multiplyCheck(denR, denL);
			return;
		case DIVIDE:
			parts[0] = multiplyCheck(denR, numL);
			parts[1] = multiplyCheck(denL, numR);
			return;
		case POWER:

			ExpressionValue exponent = expr.getRight();
			kernel = expr.getKernel();

			if (exponent.evaluateDouble() < 0) {
				parts[1] = powerCheck(numL, ExpressionNode.unaryMinus(kernel, exponent));
				parts[0] = denL == null ? new MyDouble(kernel, 1) : powerCheck(denL,
						ExpressionNode.unaryMinus(kernel, exponent));
			} else {
				parts[0] = powerCheck(numL, exponent);
				parts[1] = powerCheck(denL, exponent);
			}
			return;
		case PLUS:
			if (expandPlus) {
				parts[0] = multiplyCheck(denR, numL).wrap().plus(multiplyCheck(denL, numR));
				parts[1] = multiplyCheck(denR, denL);
				return;
			}
		case MINUS:
			if (expandPlus) {
				parts[0] = multiplyCheck(denR, numL).wrap().subtract(multiplyCheck(denL, numR));
				parts[1] = multiplyCheck(denR, denL);
				return;
			}
		case FUNCTION:
			if (expandPlus && expr.getLeft() instanceof Functional) {
				Function fn = ((Functional) expr.getLeft()).getFunction();
				ExpressionValue at = denR == null ? numR : numR.wrap().divide(denR);
				if (fn != null && at instanceof NumberValue) {
					ExpressionNode expCopy = fn.getExpression().deepCopy(fn.getKernel());

					expCopy.replace(fn.getFunctionVariables()[0], at);
					expCopy.getFraction(parts, expandPlus);
					return;
				}
			}
		default:
			parts[0] = expr;
			parts[1] = null;
		}
	}

	private static ExpressionValue multiplyCheck(ExpressionValue denR, ExpressionValue denL) {
		return denL == null ? denR : (denR == null ? denL : denL.wrap().multiply(denR));
	}

	private static ExpressionValue powerCheck(ExpressionValue base, ExpressionValue exp) {
		return exp == null ? base : (base == null ? null : base.wrap().power(exp));
	}
}
