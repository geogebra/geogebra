package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Fraction arithmetic utility class.
 */
public class Fractions {
	private static final double MAX_NUM_DENOMINATOR = 1E15;
	private static final int ALLOW_PI_LIMIT = 1000;

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
		if (!expr.inspect(Fractions::isDecimal)) {
			ExpressionValue[] fraction = new ExpressionValue[2];
			expr.getFraction(fraction, true);
			if (fraction[0] != null) {
				ExpressionValue ltVal = fraction[0].evaluate(StringTemplate.defaultTemplate);
				double lt = ltVal.evaluateDouble();

				boolean pi = false;
				double piDiv = lt / Math.PI;
				if (allowPi && DoubleUtil.isInteger(piDiv)
						&& !DoubleUtil.isZero(piDiv) && Math.abs(piDiv) < ALLOW_PI_LIMIT) {
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
						&& Math.abs(lt) < MAX_NUM_DENOMINATOR
						&& Math.abs(rt) < MAX_NUM_DENOMINATOR) {

					double g =
							Math.abs(Kernel.gcd(Math.round(lt), Math.round(rt))) * Math.signum(rt);
					lt = lt / g;
					rt = rt / g;
					return (pi ? multiplyPi(lt, kernel)
							: new ExpressionNode(kernel, lt)).divide(rt);
				}
				double ratio = lt / rt;
				return numericResolve(pi, ratio, expr, kernel);
			}
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
		if (!Double.isFinite(ratio)) {
			return new ExpressionNode(kernel, expr.evaluateDouble());
		}
		return new ExpressionNode(kernel, pi ? Math.PI * ratio : ratio);
	}

	private static boolean checkFraction(ExpressionValue[] parts, ExpressionValue lt,
			boolean expandPlusAndDecimals) {
		if (lt == null) {
			return false;
		}
		ExpressionValue left1 = lt.unwrap();
		if (left1 instanceof ExpressionNode) {
			((ExpressionNode) left1).getFraction(parts, expandPlusAndDecimals);
			return true;
		} else if (left1 instanceof GeoNumeric && ((GeoNumeric) left1).getDefinition() != null) {
			((GeoNumeric) left1).getFraction(parts, expandPlusAndDecimals);
			return true;
		} else if (left1.isRecurringDecimal()) {
			RecurringDecimal.asFraction(parts, left1.wrap());
			return true;
		} else if (left1 instanceof MySpecialDouble && expandPlusAndDecimals) {
			return ((MySpecialDouble) left1).asFraction(parts);
		}
		return false;
	}

	/**
	 * @param parts
	 *            output: [numerator, denominator]
	 * @param expr
	 *            expression
	 * @param expandPlusAndDecimals
	 *            whether to expand a/d+b/c to a single fraction and convert 0.5 to 1/2
	 */
	protected static void getFraction(ExpressionValue[] parts, ExpressionNode expr,
			boolean expandPlusAndDecimals) {
		if (expr.unwrap().isRecurringDecimal()) {
			RecurringDecimal.asFraction(parts, expr);
			return;
		}
		if (expr.unwrap() instanceof MySpecialDouble && expandPlusAndDecimals) {
			((MySpecialDouble) expr.unwrap()).asFraction(parts);
			return;
		}
		ExpressionValue numL, numR, denL = null, denR = null;
		if (checkFraction(parts, expr.getLeft(), expandPlusAndDecimals)) {

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

		if (checkFraction(parts, expr.getRight(), expandPlusAndDecimals)) {
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
		case INVISIBLE_PLUS:
			if (expandPlusAndDecimals) {
				parts[0] = multiplyCheck(denR, numL).wrap().plus(multiplyCheck(denL, numR));
				parts[1] = multiplyCheck(denR, denL);
				return;
			}
		case MINUS:
			if (expandPlusAndDecimals) {
				parts[0] = multiplyCheck(denR, numL).wrap().subtract(multiplyCheck(denL, numR));
				parts[1] = multiplyCheck(denR, denL);
				return;
			}
		case FUNCTION:
			if (expandPlusAndDecimals && expr.getLeft() instanceof Functional) {
				Function fn = ((Functional) expr.getLeft()).getFunction();
				ExpressionValue at = denR == null ? numR : numR.wrap().divide(denR);
				if (fn != null && at instanceof NumberValue) {
					ExpressionNode expCopy = fn.getExpression().deepCopy(fn.getKernel());

					expCopy.replace(fn.getFunctionVariables()[0], at);
					expCopy.getFraction(parts, expandPlusAndDecimals);
					return;
				}
			}
		default:
			parts[0] = expr;
			parts[1] = null;
		}
	}

	/**
	 * Whether given element can be printed as exact fraction with current rounding
	 * @param geo element
	 * @param kernel used to determine rounding
	 * @return whether the fraction has exact decimal value
	 */
	public static boolean isExactFraction(GeoElementND geo, Kernel kernel) {
		if (!(geo instanceof GeoNumeric) || geo.getDefinition() == null) {
			return false;
		}
		ExpressionNode fractionOrMultipleOfPi = geo.getDefinition().asFraction();
		return isSimpleFraction(fractionOrMultipleOfPi)
				&& isExactFraction(fractionOrMultipleOfPi, kernel);
	}

	private static boolean isSimpleFraction(ExpressionNode fractionOrMultipleOfPi) {
		return fractionOrMultipleOfPi.isOperation(Operation.DIVIDE)
				&& !fractionOrMultipleOfPi.getLeft().unwrap().isExpressionNode()
				&& !(fractionOrMultipleOfPi.getLeft().unwrap() instanceof MySpecialDouble);
	}

	private static boolean isExactFraction(ExpressionNode fraction, Kernel kernel) {
		ExpressionValue denominator = fraction.getRight();
		// For any denominator we have unique (q, deg2, deg5) such that
		// denominator = q * 2^deg2 * 5^deg5, gcd(q,2) = 1, gcd(q,5) = 1.
		// The fraction has finite number of decimal digits if and only if q==1.
		int q = (int) denominator.evaluateDouble();
		int deg2 = 0;
		while (q % 2 == 0) {
			q /= 2;
			deg2++;
		}
		int deg5 = 0;
		while (q % 5 == 0) {
			q /= 5;
			deg5++;
		}
		if (q != 1) {
			return false;
		}
		// Now we know the fraction is of the form num / (2^deg2*5^deg5),
		// that can also be written as expandedNum / 10^maxDeg,
		// where maxDeg = max(deg2, deg5) and expandedNum = num * 2^(maxDeg-deg2) * 5^(maxDeg-deg5).
		// That is a decimal fraction with log10(expandedNum) digits, maxDeg of them after the ".".
		int maxDeg = Math.max(deg2, deg5);
		if (kernel.useSignificantFigures) {
			// here we care about total digits; assume no trailing zeros since fraction is not int
			double num = fraction.getLeft().evaluateDouble();
			double expandedNum = Math.abs(num)
					* Math.pow(2, maxDeg - deg2) * Math.pow(5, maxDeg - deg5);
			return Math.log10(expandedNum) < kernel.getPrintFigures();
		} else {
			// here we only care about digits after the "."
			return maxDeg <= kernel.getPrintDecimals();
		}
	}

	private static boolean isDecimal(ExpressionValue left) {
		return left instanceof MySpecialDouble && ((MySpecialDouble) left).isDecimal();
	}

	private static ExpressionValue multiplyCheck(ExpressionValue denR, ExpressionValue denL) {
		return denL == null ? denR : (denR == null ? denL : denL.wrap().multiply(denR));
	}

	private static ExpressionValue powerCheck(ExpressionValue base, ExpressionValue exp) {
		return exp == null ? base : (base == null ? null : base.wrap().power(exp));
	}
}
