package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoFractionText;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Computes symbolic derivative for each operation
 *
 */
public class Derivative {

	/**
	 * @param left
	 *            left expression
	 * @param right
	 *            right expression
	 * @param operation
	 *            operation
	 * @param fv
	 *            variable
	 * @param kernel0
	 *            kernel of resulting node
	 * @return derivative
	 */
	public static ExpressionNode get(ExpressionValue left,
			ExpressionValue right, Operation operation, FunctionVariable fv,
			Kernel kernel0) {
		// symbolic derivatives disabled in exam mode

		switch (operation) {

		// for eg (x < x1) * (a1 x^2 + b1 x + c1)
		// we need to return 0 for derivative of (x < x1)
		// so that the product rule gives the correct answer
		case LESS:
		case LESS_EQUAL:
		case GREATER:
		case GREATER_EQUAL:
		case NOT:
			return new ExpressionNode(kernel0, 0);
		case XCOORD:
			return coordDerivative(left, 0, fv, kernel0);
		case YCOORD:
			return coordDerivative(left, 1, fv, kernel0);
		case ZCOORD:
			return coordDerivative(left, 2, fv, kernel0);

		case POWER:
			return derivativePower(left, right, fv, kernel0);

		case NO_OPERATION:
			return wrap(left.derivative(fv, kernel0));
		case DIVIDE:
			if (right.isNumberValue() && !right.contains(fv)) {
				return wrap(left).derivative(fv, kernel0).divideSimplify(right);
			}
			if (right.isExpressionNode()
					&& ((ExpressionNode) right)
							.getOperation() == Operation.POWER
					&& !((ExpressionNode) right).getRight().contains(fv)) {
				ExpressionValue rl = right.wrap().getLeft();
				ExpressionValue rr = right.wrap().getRight();
				return wrap(left.derivative(fv, kernel0)).multiply(rl)
						.subtract(wrap(rl.derivative(fv, kernel0))
								.multiply(left).multiply(rr))
						.divide(wrap(rl).power(rr.wrap().plus(1)));
			}
			return wrap(left.derivative(fv, kernel0)).multiply(right)
					.subtract(
							wrap(right.derivative(fv, kernel0)).multiply(left))
					.divide(wrap(right).square());
		case MULTIPLY:
			if (right.isNumberValue() && !right.wrap().containsDeep(fv)) {
				return wrap(left).derivative(fv, kernel0).multiply(right);
			}
			if (left.isNumberValue() && !left.wrap().containsDeep(fv)) {
				return wrap(right).derivative(fv, kernel0).multiply(left);
			}
			ExpressionNode scalarExpanded = VectorArithmetic
					.expandScalarProduct(kernel0, left,
					right, operation);
			if (scalarExpanded != null) {
				return scalarExpanded.derivative(fv, kernel0);
			}
			return wrap(left).multiply(right.derivative(fv, kernel0))
					.plus(wrap(right).multiply(left.derivative(fv, kernel0)));
		case PLUS:
			return wrap(left.derivative(fv, kernel0))
					.plus(right.derivative(fv, kernel0));
		case MINUS:
			return wrap(left.derivative(fv, kernel0))
					.subtract(right.derivative(fv, kernel0));
		case SIN:
			return new ExpressionNode(kernel0, left, Operation.COS, null)
					.multiply((left).derivative(fv, kernel0));
		case COS:
			return new ExpressionNode(kernel0, left, Operation.SIN, null)
					.multiply((left).derivative(fv, kernel0)).multiply(-1);
		case TAN:
			return new ExpressionNode(kernel0, left, Operation.SEC, null)
					.square().multiply((left).derivative(fv, kernel0));
		case SEC:
			return new ExpressionNode(kernel0, left, Operation.SEC, null)
					.multiply(new ExpressionNode(kernel0, left, Operation.TAN,
							null))
					.multiply((left).derivative(fv, kernel0));
		case CSC:
			return new ExpressionNode(kernel0, left, Operation.CSC, null)
					.multiply(new ExpressionNode(kernel0, left, Operation.COT,
							null))
					.multiply((left).derivative(fv, kernel0)).multiply(-1);
		case COT:
			return new ExpressionNode(kernel0, left, Operation.CSC, null)
					.square().multiply((left).derivative(fv, kernel0))
					.multiply(-1);
		case SINH:
			return new ExpressionNode(kernel0, left, Operation.COSH, null)
					.multiply((left).derivative(fv, kernel0));
		case COSH:
			return new ExpressionNode(kernel0, left, Operation.SINH, null)
					.multiply((left).derivative(fv, kernel0));
		case TANH:
			return new ExpressionNode(kernel0, left, Operation.SECH, null)
					.square().multiply((left).derivative(fv, kernel0));
		case SECH:
			return new ExpressionNode(kernel0, left, Operation.SECH, null)
					.multiply(new ExpressionNode(kernel0, left, Operation.TANH,
							null))
					.multiply((left).derivative(fv, kernel0)).multiply(-1);
		case CSCH:
			return new ExpressionNode(kernel0, left, Operation.CSCH, null)
					.multiply(new ExpressionNode(kernel0, left, Operation.COTH,
							null))
					.multiply((left).derivative(fv, kernel0)).multiply(-1);
		case COTH:
			return new ExpressionNode(kernel0, left, Operation.CSCH, null)
					.square().multiply((left).derivative(fv, kernel0))
					.multiply(-1);

		case ARCSIND:
		case ARCSIN:
			return wrap(left.derivative(fv, kernel0))
					.divide(wrap(left).square().subtractR(1).sqrt());
		case ARCCOSD:
		case ARCCOS:
			return wrap(left.derivative(fv, kernel0))
					.divide(wrap(left).square().subtractR(1).sqrt())
					.multiply(-1);
		case ARCTAND:
		case ARCTAN:
			return wrap(left.derivative(fv, kernel0))
					.divide(wrap(left).square().plus(1));

		case ASINH:
			return wrap(left.derivative(fv, kernel0))
					.divide(wrap(left).square().plus(1).sqrt());
		case ACOSH:
			// sqrt(x+1)sqrt(x-1) not sqrt(x^2-1) as has wrong domain
			return wrap(left.derivative(fv, kernel0)).divide(wrap(left).plus(1)
					.sqrt().multiply(wrap(left).subtract(1).sqrt()));
		case ATANH:
			return wrap(left.derivative(fv, kernel0))
					.divide(wrap(left).square().subtractR(1));

		case ABS:
			return wrap(left.derivative(fv, kernel0)).multiply(left)
					.divide(wrap(left).abs());

		case SGN:
			// 0/x
			return wrap(new MyDouble(kernel0, 0)).divide(fv);

		case EXP:
			return wrap(left.derivative(fv, kernel0))
					.multiply(wrap(left).exp());

		case SI:
			return wrap(left.derivative(fv, kernel0))
					.multiply(wrap(left).sin().divide(left));

		case CI:
			return wrap(left.derivative(fv, kernel0))
					.multiply(wrap(left).cos().divide(left));

		case EI:
			return wrap(left.derivative(fv, kernel0))
					.multiply(wrap(left).exp().divide(left));

		case ERF:
			return wrap(left.derivative(fv, kernel0)).multiply(wrap(kernel0, 2))
					.divide(wrap(left).square().exp()
							.multiply(wrap(kernel0, Math.PI).sqrt()));

		case PSI:
			return wrap(left.derivative(fv, kernel0))
					.multiply(wrap(left).polygamma(1));

		case POLYGAMMA:
			if (left.isNumberValue() && !left.contains(fv)) {
				double n = left.evaluateDouble();
				return wrap(right.derivative(fv, kernel0))
						.multiply(wrap(right).polygamma(n + 1));
			}

			// TODO: general method (not possible?)
			break;

		case IF_ELSE:
			MyNumberPair np = (MyNumberPair) left;

			np = new MyNumberPair(kernel0, np.x, np.y.derivative(fv, kernel0));

			return new ExpressionNode(kernel0, np, Operation.IF_ELSE,
					right.derivative(fv, kernel0));

		case IF:
		case IF_SHORT:
			return new ExpressionNode(kernel0, left, Operation.IF,
					right.derivative(fv, kernel0));

		case IF_LIST:
			MyList rtDiff = new MyList(kernel0);
			MyList rt = (MyList) right;
			for (int i = 0; i < rt.size(); i++) {
				rtDiff.addListElement(
						rt.getListElement(i).derivative(fv, kernel0));
			}
			return new ExpressionNode(kernel0, left, Operation.IF_LIST, rtDiff);

		case LOG:
			// base e (ln)
			return wrap(left.derivative(fv, kernel0)).divide(left);

		case LOG10:
			return wrap(left.derivative(fv, kernel0)).divide(left)
					.divide(Math.log(10));

		case LOG2:
			return wrap(left.derivative(fv, kernel0)).divide(left)
					.divide(Math.log(2));

		case LOGB:
			if (left.isNumberValue() && !left.contains(fv)) {
				return wrap(right.derivative(fv, kernel0)).divide(right)
						.divide(Math.log(left.evaluateDouble()));
			}
			return right.wrap().apply(Operation.LOG)
					.divide(left.wrap().apply(Operation.LOG))
					.derivative(fv, kernel0);

		case NROOT:
			if (right.isNumberValue() && !right.contains(fv)) {
				return wrap(left.derivative(fv, kernel0))
						.multiply(wrap(left).nroot(right))
						.divide(wrap(left).multiply(right));
			}
			return left.wrap()
					.power(new ExpressionNode(kernel0, 1).divide(right))
					.derivative(fv, kernel0);

		case SQRT:
		case SQRT_SHORT:
			return wrap(left.derivative(fv, kernel0))
					.multiply(wrap(left).power(-0.5)).divide(2);
		case CBRT:
			// wrong domain
			// return
			// wrap(left.derivative(fv,
			// kernel)).multiply(wrap(left).power(-2d/3d)).divide(3);
			// correct domain
			return wrap(left.derivative(fv, kernel0))
					.divideSimplify(wrap(left).square().cbrt()).divide(3);

		case FUNCTION:
			if (left instanceof GeoFunction) {
				Function fun = ((GeoFunction) left).getFunction();
				FunctionVariable fv2 = fun.fVars[0];
				ExpressionValue deriv = fun.derivative(fv2, kernel0);

				Function fun2 = new Function((ExpressionNode) deriv, fv2);
				GeoFunction geoFun = new GeoFunction(kernel0.getConstruction(),
						fun2);

				ExpressionNode ret = new ExpressionNode(kernel0, geoFun,
						Operation.FUNCTION, right)
								.multiply(right.derivative(fv, kernel0));

				return ret;
			}
			break;
		case ARCTAN2:
			// (((-f(x)) * g'(x)) + (f'(x) * g(x))) / (f(x)^(2) + g(x)^(2))
			ExpressionNode numerator = left.derivative(fv, kernel0).wrap()
					.multiply(right).wrap()
					.subtract(right.derivative(fv, kernel0).wrap()
							.multiply(left).wrap());
			return numerator.divide(
					left.wrap().power(2).wrap().plus(right.wrap().power(2)));

		case LAMBERTW:
			// LambertW(x) -> LambertW(x)/(x*(LambertW(x)+1))
			// Better: LambertW(x) -> 1/(x+exp(LambertW(x)) works for x=0
			return new ExpressionNode(kernel0, left, Operation.LAMBERTW, right)
					.exp().plus(left).reciprocate()
					.multiply(left.derivative(fv, kernel0));

		case FACTORIAL:
			// x! -> psi(x+1) * x!
			return new ExpressionNode(kernel0, left.wrap().plus(1),
					Operation.PSI, null)
							.multiply(new ExpressionNode(kernel0, left,
									Operation.FACTORIAL, null))
							.multiply((left).derivative(fv, kernel0));

		case GAMMA:
			// gamma(x) -> gamma(x) psi(x)
			return new ExpressionNode(kernel0, left, Operation.PSI, null)
					.multiply(new ExpressionNode(kernel0, left, Operation.GAMMA,
							null))
					.multiply((left).derivative(fv, kernel0));

		case ROUND2:
		case ROUND:
		case FLOOR:
		case CEIL:
			return new ExpressionNode(kernel0, 0);

		case DOLLAR_VAR_COL:
			break;
		case DOLLAR_VAR_ROW:
			break;
		case DOLLAR_VAR_ROW_COL:
			break;
		case AND:
			break;
		case AND_INTERVAL:
			break;
		case ARBCOMPLEX:
			break;
		case ARBCONST:
			break;
		case ARBINT:
			break;

		case ARG:
			break;
		case ALT:
			break;
		case BETA:
			break;
		case BETA_INCOMPLETE:
			break;
		case BETA_INCOMPLETE_REGULARIZED:
			break;
		case CONJUGATE:
			break;
		case DERIVATIVE:
			break;
		case DIFF:
			break;
		case ELEMENT_OF:

			if (left.evaluatesToList()) {
				int index = (int) Math.round(right.evaluateDouble());
				MyList list = ((ListValue) left).getMyList();

				if (index >= 0 && index < list.getLength()) {

					ExpressionValue element = list.getListElement(index);

					ExpressionValue deriv = element.derivative(fv, kernel0);

					return deriv.wrap();
				}

			}

			break;
		case EQUAL_BOOLEAN:
			break;
		case FRACTIONAL_PART:
			return left.derivative(fv, kernel0).wrap();
		case FREEHAND:
			break;
		case FUNCTION_NVAR:
			break;
		case GAMMA_INCOMPLETE:
			break;
		case GAMMA_INCOMPLETE_REGULARIZED:
			break;
		case IMAGINARY:
			break;
		case IMPLICATION:
			break;
		case INTEGRAL:
			break;
		case IS_ELEMENT_OF:
			break;
		case IS_SUBSET_OF:
			break;
		case IS_SUBSET_OF_STRICT:
			break;
		case MULTIPLY_OR_FUNCTION:
			break;
		case NOT_EQUAL:
			break;
		case OR:
			break;
		case PARALLEL:
			break;
		case PERPENDICULAR:
			break;
		case RANDOM:
			break;
		case REAL:
			break;
		case SET_DIFFERENCE:
			break;
		case SUBSTITUTION:
			break;
		case SUM:
			break;
		case VECTORPRODUCT:
			break;
		case VEC_FUNCTION:
			break;
		case ZETA:
			break;
		default:
			break;
		}

		Log.error("unhandled operation in derivative() (no CAS version): "
				+ operation.toString());

		// undefined
		return wrap(kernel0, Double.NaN);
	}

	private static Inspecting checkCoordOperations = new Inspecting() {

		@Override
		public boolean check(ExpressionValue v) {
			return v.isOperation(Operation.XCOORD)
							|| v.isOperation(Operation.YCOORD)
							|| v.isOperation(Operation.ZCOORD);
		}
	};

	private static ExpressionNode coordDerivative(ExpressionValue left, int i,
			FunctionVariable fv, Kernel kernel0) {
		if (!left.wrap().containsDeep(fv)) {
			return new ExpressionNode(kernel0, 0d);
		}
		ExpressionNode en = VectorArithmetic.computeCoord(left.wrap(), i);
		if (!en.inspect(checkCoordOperations)) {
			return en.derivative(fv, kernel0);
		}
		Log.debug("fast derivatives can't handle " + ('x' + i) + " for "
				+ left.toValueString(StringTemplate.defaultTemplate));
		return new ExpressionNode(kernel0, Double.NaN);
	}

	private static ExpressionNode derivativePower(ExpressionValue left,
			ExpressionValue right, FunctionVariable fv, Kernel kernel0) {
		if (right.isNumberValue() && !right.contains(fv)) {
			if (DoubleUtil.isZero(right.evaluateDouble())) {
				return wrap(new MyDouble(kernel0, 0d));
			}
			ExpressionNode ret = null;
			// make sure Tangent[x^(1/3), A] works when x(A)<0
			if (right.isConstant()) {
				ret = derivativeConstantPower(left, right,
						fv, kernel0);
			}
			if (ret != null) {
				return ret;
			}

			return wrap(left).power(wrap(right).subtract(1))
					.multiply(left.derivative(fv, kernel0)).multiply(right);
		}
		ExpressionNode scalarExpanded = VectorArithmetic
				.expandScalarProduct(kernel0, left, right, Operation.POWER);
		if (scalarExpanded != null) {
			return scalarExpanded.derivative(fv, kernel0);
		}
		return wrap(left).power(right).multiply(
				wrap(right.derivative(fv, kernel0)).multiply(wrap(left).ln())
						.plus(wrap(right).multiply(left.derivative(fv, kernel0))
								.divideSimplify(left)));
	}

	private static ExpressionNode derivativeConstantPower(ExpressionValue left,
			ExpressionValue right, FunctionVariable fv, Kernel kernel0) {
		double rightDoub = right.evaluateDouble();
		if (DoubleUtil.isEqual(rightDoub, 2)) {
			return wrap(left).multiply(left.derivative(fv, kernel0)).multiply(
					right);
		}
		// not an integer, convert to x^(a/b)
		if (!DoubleUtil.isInteger(rightDoub)) {

			double[] fraction = AlgoFractionText.decimalToFraction(rightDoub,
					Kernel.STANDARD_PRECISION);

			double a = fraction[0];
			double b = fraction[1];

			// Log.debug(a + " / " + b);

			if (b == 0) {
				return wrap(new MyDouble(kernel0, Double.NaN));
			}

			// a/b-1 = (a-b)/b
			ExpressionNode newPower = wrap(new MyDouble(kernel0, a - b))
					.divide(new MyDouble(kernel0, b));

			// x^(1/b-1) * a / b * x'
			return wrap(left).power(newPower).multiply(a).divide(b)
					.multiply(left.derivative(fv, kernel0));
		}
		return null;
	}

	private static ExpressionNode wrap(Kernel kernel0, double d) {
		return new ExpressionNode(kernel0, d);
	}

	private static ExpressionNode wrap(ExpressionValue exp) {
		return exp.wrap();
	}

}
