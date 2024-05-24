package org.geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.LinkedList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationSolverInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.AlgoComplexRootsPolynomial;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.implicit.PolynomialUtils;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

public class AlgoIsFactored extends AlgoElement {

	private final GeoFunctionable inputGeo;
	private final GeoBoolean outputBoolean;

	private FunctionVariable fv;

	protected EquationSolverInterface eqnSolver;
	private final Solution solution = new Solution();

	private ArrayList<MySpecialDouble> multiplyCoeffs = new ArrayList<>();

	/**
	 * @param cons construction
	 * @param inputGeo function or number
	 */
	public AlgoIsFactored(Construction cons, GeoFunctionable inputGeo) {
		super(cons);
		this.inputGeo = inputGeo;
		this.outputBoolean = new GeoBoolean(cons);

		eqnSolver = cons.getKernel().getEquationSolver();
		solution.resetRoots();

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.IsFactored;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[]{inputGeo.toGeoElement()};
		setOnlyOutput(outputBoolean);
		setDependencies(); // done by AlgoElement
	}

	@Override
	public final void compute() {
		if (inputGeo instanceof GeoNumeric) {
			outputBoolean.setValue(true);
			return;
		}
		Function function = inputGeo.getFunction();
		if (!function.isPolynomialFunction(true, false)) {
			outputBoolean.setUndefinedProverOnly();
			return;
		}
		PolyFunction polyFun =
				function.expandToPolyFunction(function.getExpression(), false, false);
		int degree = polyFun.getDegree();
		if (degree > 5) {
			outputBoolean.setUndefinedProverOnly();
			return;
		}
		outputBoolean.setDefined();
		multiplyCoeffs.clear();
		fv = function.getFunctionVariable();
		ExpressionNode node = function.getGeoFunction().getFunctionExpression();
		if (node != null) {
			boolean isFactored = isFactored(node) && multiplyCoeffs.size() <= 1;
			outputBoolean.setValue(isFactored);
		}
	}

	private boolean isFactored(ExpressionNode node) {
		if (node.isOperation(Operation.MULTIPLY)) {
			if (node.getLeft() instanceof MySpecialDouble) {
				multiplyCoeffs.add((MySpecialDouble) node.getLeft());
				if (node.getRight() instanceof MySpecialDouble) {
					multiplyCoeffs.add((MySpecialDouble) node.getRight());
				}
			}
			if (node.getLeft().isExpressionNode()) {
				if (node.getRight().isExpressionNode()) {
					return isFactored(node.getLeft().wrap()) && isFactored(
							node.getRight().wrap());
				} else if (node.getRight() instanceof MySpecialDouble) {
					multiplyCoeffs.add((MySpecialDouble) node.getRight());
				}
				return isFactored(node.getLeft().wrap());
			} else if (node.getRight().isExpressionNode()) {
				return isFactored(node.getRight().wrap());
			}
			return true;
		}

		if (node.isOperation(Operation.POWER)) {
			if (node.getRight() instanceof MySpecialDouble) {
				if (node.getLeft().isExpressionNode()) {
					return isFactored(node.getLeft().wrap());
				}
				return true;
			}
			return false;
		}
		return isFactoredPolynomial(node);
	}

	private boolean isFactoredPolynomial(ExpressionNode node) {
		GeoFunction geoFun = node.buildFunction(fv);
		Function fun = geoFun.getFunction();
		if (fun != null && fun.isPolynomialFunction(true, false)) {
			PolyFunction polyFun = fun.expandToPolyFunction(fun.getExpression(),
					false, false);
			if (polyFun != null) {
				// return false for e.g. (x-1)^2+2
				if (!hasValidStructure(node)) {
					return false;
				}
				// check coefficients of polynomial
				if (!hasValidCoefficients(node, fun)) {
					return false;
				}
				// check roots
				return hasValidRoots(polyFun, fun);
			}
		}
		return false;
	}

	private boolean hasValidCoefficients(ExpressionNode node, Function fun) {
		LinkedList<PolyFunction> factorList = fun.getPolynomialFactors(true, true);
		if (factorList != null) {
			double[] coeffs;
			double value = 1.0;
			boolean isSameSign = true;
			for (PolyFunction pf : factorList) {
				pf.updateCoeffValues();
				coeffs = pf.getCoeffsCopy();
				// return false for e.g. 2x-2
				if (Kernel.gcd(coeffs) != 1) {
					return false;
				}
				// return false for e.g. x^2+x
				if (coeffs.length > 2) {
					if (coeffs[0] == 0) {
						return false;
					}
				}
				if (canBeSimplified(coeffs, node)) {
					return false;
				}
				value = coeffs[coeffs.length - 1];
				for (double c : coeffs) {
					if (((int) value ^ (int) c) < 0) {
						isSameSign = false;
						break;
					}
				}
			}
			return value >= 0 || !isSameSign;
		}
		return true;
	}

	private boolean hasValidRoots(PolyFunction polyFun, Function fun) {
		int degree = polyFun.getDegree();
		if (degree > 1) {
			// check roots
			AlgoRootsPolynomial
					.calcRootsMultiple(fun, 0, solution, eqnSolver);
			int numRoots = solution.curRealRoots;
			if (numRoots > 1) {
				for (int i = 0; i < numRoots; i++) {
					// take only rational roots
					if (isRationalNumber(solution.curRoots[i])) {
						return false;
					}
				}
			}
			boolean hasValidRealRoots = !(numRoots == 1
					&& isRationalNumber(solution.curRoots[0]));
			// check complex roots
			if (hasValidComplexRoots(fun, polyFun, degree)) {
				return false;
			}
			return hasValidRealRoots;
		}
		return true;
	}

	private boolean hasValidComplexRoots(Function fun, PolyFunction polyFun, int degree) {
		// basic idea: if roots are conjugates: (x-z)(x-zconjugate) = x^2-2*Re(z)x+|z|^2
		// coefficients should be integer
		double[] curComplexRoots =
				AlgoComplexRootsPolynomial.calcComplexRoots(fun, solution, null, eqnSolver);
		double[] realRoots = solution.curRoots;
		// check if there are roots with b = 0 (z=a+ib)
		boolean hasOnlyRootsComplexZero = true;
		// check if there are roots with a = 0 (z=a+ib)
		boolean hasOnlyRootsRealZero = true;
		for (int k = 0; k < solution.curRealRoots; k++) {
			if (!DoubleUtil.isZero(curComplexRoots[k], 1E-6)) {
				hasOnlyRootsComplexZero = false;
			}
			if (!DoubleUtil.isZero(realRoots[k], 1E-6)) {
				hasOnlyRootsRealZero = false;
			}
		}
		if (hasOnlyRootsComplexZero && degree > 2) {
			return true;
		}
		if (hasOnlyRootsRealZero) {
			return degree > 2;
		}
		//check the complex conjugates
		for (int i = 0; i < solution.curRealRoots; i++) {
			for (int j = i + 1; j < solution.curRealRoots; j++) {
				if (DoubleUtil.isEqual(realRoots[i], realRoots[j], 1E-6)) {
					if (DoubleUtil.isEqual(curComplexRoots[i], -curComplexRoots[j], 1E-6)) {
						double lc = PolynomialUtils.getLeadingCoeff(polyFun.getCoeffs());
						double f1 = realRoots[i] * realRoots[i]
								+ curComplexRoots[i] * curComplexRoots[i];
						double f2 = 2 * realRoots[i];
						if (isIntegerNumber(f1 * lc) && isIntegerNumber(f2 * lc)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private boolean canBeSimplified(double[] coeffs, ExpressionNode node) {
		int counter = numberOfTerms(node, 0);
		int coeffNotZero = 0;
		ArrayList<Double> coeffsNotInt = new ArrayList<>();
		for (double c : coeffs) {
			if ((int) c != c) {
				coeffsNotInt.add(c);
			}
			if (c != 0) {
				coeffNotZero++;
			}
		}
		// return false for e.g. 3x-2x
		if (counter > coeffNotZero) {
			return true;
		}
		if (coeffsNotInt.isEmpty()) {
			return false;
		}
		double product = 1;
		for (MySpecialDouble d : multiplyCoeffs) {
			product *= d.getDouble();
		}
		boolean allInteger = true;
		for (double c : coeffs) {
			double cMultiplied = c * product;
			if (!DoubleUtil.isInteger(cMultiplied)) {
				allInteger = false;
				break;
			}
		}
		return allInteger;
	}

	private int numberOfTerms(ExpressionNode node, int counter) {
		int c = counter;
		if (node.getOperation().isPlusorMinus()) {
			if (node.getLeft().isExpressionNode()) {
				c = numberOfTerms(node.getLeft().wrap(), c);
			} else {
				c++;
			}
			if (node.getRight().isExpressionNode()) {
				c = numberOfTerms(node.getRight().wrap(), c);
			} else {
				c++;
			}
		} else {
			c++;
		}
		return c;
	}

	private boolean hasValidStructure(ExpressionNode node) {
		if (node.getOperation().isPlusorMinus()) {
			if (node.getLeft().isExpressionNode()) {
				if (node.getRight().isExpressionNode()) {
					return hasValidStructure(node.getLeft().wrap()) && hasValidStructure(
							node.getRight().wrap());
				}
				return hasValidStructure(node.getLeft().wrap());
			}
			if (node.getRight().isExpressionNode()) {
				return hasValidStructure(node.getRight().wrap());
			}
		}
		if (node.isOperation(Operation.POWER) && node.getLeft().isExpressionNode()) {
			if (node.getLeft().wrap().getOperation().isPlusorMinus()) {
				return false;
			}
		}
		if (node.isOperation(Operation.MULTIPLY)) {
			if (node.getLeft().isExpressionNode()) {
				if (node.getRight().isExpressionNode()) {
					if (node.getLeft().wrap().getOperation().isPlusorMinus() || node.getRight()
							.wrap().getOperation().isPlusorMinus()) {
						return false;
					}
					return hasValidStructure(node.getLeft().wrap()) && hasValidStructure(
							node.getRight().wrap());
				}
				if (node.getLeft().wrap().getOperation().isPlusorMinus()) {
					return false;
				}
				return hasValidStructure(node.getLeft().wrap());
			}
			if (node.getRight().isExpressionNode()) {
				if (node.getRight().wrap().getOperation().isPlusorMinus()) {
					return false;
				}
				return hasValidStructure(node.getRight().wrap());
			}
		}
		return true;
	}

	private boolean isRationalNumber(double number) {
		double n = Math.abs(number);
		for (int i = 0; i < 20; i++) {
			double a = Math.floor(n);
			if ((n - a) < 1e-6) {
				return true;
			}
			n = 1 / (n - a);
		}
		return false;
	}

	private boolean isIntegerNumber(double number) {
		return DoubleUtil.isEqual(number, Math.round(number), 1E-6);
	}
}
