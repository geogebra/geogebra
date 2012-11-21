package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoMacroInterface;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.Operation;
import geogebra.common.util.Unicode;

/**
 * Boolean function of the type a(<|<=)x(<|<=)b
 * 
 * @author Markus Hohenwarter
 * 
 */
public class GeoInterval extends GeoFunction {

	/**
	 * Creates new GeoInterval
	 * 
	 * @param c construction
	 * @param label label
	 * @param f boolean function
	 */
	public GeoInterval(Construction c, String label, Function f) {
		super(c, label, f);
	}

	/**
	 * Copy constructor
	 * 
	 * @param geoInterval interval to copy
	 */
	public GeoInterval(GeoInterval geoInterval) {
		super(geoInterval.cons);
		set(geoInterval);
	}

	/**
	 * Creates new unlabeled interval
	 * 
	 * @param cons construction
	 */
	public GeoInterval(Construction cons) {
		super(cons);
	}

	@Override
	public GeoElement copy() {
		return new GeoInterval(this);
	}

	@Override
	public void set(GeoElement geo) {
		GeoInterval geoFun = (GeoInterval) geo;

		if (geo == null || geoFun.fun == null) {
			fun = null;
			isDefined = false;
			return;
		}
		isDefined = geoFun.isDefined;
		fun = new Function(geoFun.fun, kernel);

		// macro OUTPUT
		if (geo.cons != cons && isAlgoMacroOutput()) {
			// this object is an output object of AlgoMacro
			// we need to check the references to all geos in its function's
			// expression
			if (!geoFun.isIndependent()) {
				AlgoMacroInterface algoMacro = (AlgoMacroInterface) getParentAlgorithm();
				algoMacro.initFunction(this.fun);
			}
		}
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.INTERVAL;
	}

	private StringBuilder sbToString2;

	@Override
	public String toString(StringTemplate tpl) {
		if (sbToString2 == null)
			sbToString2 = new StringBuilder();
		else
			sbToString2.setLength(0);
		if (isLabelSet()) {
			sbToString2.append(label);
			sbToString2.append(": ");
		}
		sbToString2.append(toSymbolicString(tpl));
		return sbToString2.toString();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return toString(false,tpl);
	}

	private double rightBound = Double.NaN;
	private double leftBound = Double.NaN;

	private String rightStr = "", leftStr = "";
	private char rightInequality = ' ';
	private char leftInequality = ' ';

	/**
	 * Returns string description of the interval
	 * 
	 * @param symbolic
	 *            true for symbolic, false for numeric
	 * @return string description of the interval
	 */
	private String toString(boolean symbolic,StringTemplate tpl) {

		// output as nice string eg 3 < x < 5

		if (!isDefined())
			return app.getPlain("Undefined");

		// return "3 < x < 5";//fun.toValueString();

		ExpressionNode en = fun.getExpression();
		if (en.getOperation().equals(Operation.AND)) {
			ExpressionValue left = en.getLeft();
			ExpressionValue right = en.getRight();

			if (left.isExpressionNode() && right.isExpressionNode()) {

				updateBoundaries();

				if (!Double.isNaN(rightBound) && !Double.isNaN(leftBound)
						&& leftBound <= rightBound) {
					sbToString.setLength(0);
					sbToString.append(symbolic ? leftStr : kernel
							.format(leftBound,tpl));
					sbToString.append(' ');
					sbToString.append(leftInequality);
					sbToString.append(' ');
					sbToString.append(getVarString(tpl));
					sbToString.append(' ');
					sbToString.append(rightInequality);
					sbToString.append(' ');
					sbToString.append(symbolic ? rightStr : kernel
							.format(rightBound,tpl));
					return sbToString.toString();
					// return kernel.format(leftBound)
					// +leftInequality+" x "+rightInequality+kernel.format(rightBound);
				}
			}
		}

		// eg x<3 && x>10
		// Application.debug("fall through");
		return symbolic ? super.toSymbolicString(tpl) : super.toValueString(tpl);

	}

	@Override
	public String toSymbolicString(StringTemplate tpl) {
		if (isDefined()) {
			return toString(true,tpl);
		}
		return app.getPlain("Undefined");
	}

	@Override
	public String toLaTeXString(boolean symbolic,StringTemplate tpl) {
		if (isDefined()) {
			return fun.toLaTeXString(symbolic,tpl);
		}
		return app.getPlain("Undefined");
	}

	@Override
	public boolean isEqual(GeoElement geo) {
		return false;
	}

	private void updateBoundaries() {
		ExpressionNode en = fun.getExpression();
		if (en.getOperation().equals(Operation.AND)) {
			ExpressionValue left = en.getLeft();
			ExpressionValue right = en.getRight();
			ExpressionNode enLeft = (ExpressionNode) left;
			ExpressionNode enRight = (ExpressionNode) right;

			Operation opLeft = enLeft.getOperation();
			Operation opRight = enRight.getOperation();

			ExpressionValue leftLeft = enLeft.getLeft();
			ExpressionValue leftRight = enLeft.getRight();
			ExpressionValue rightLeft = enRight.getLeft();
			ExpressionValue rightRight = enRight.getRight();

			if ((opLeft.equals(Operation.LESS) || opLeft
					.equals(Operation.LESS_EQUAL))) {
				if (leftLeft instanceof FunctionVariable
						&& leftRight.isNumberValue()) {
					rightInequality = opLeft.equals(Operation.LESS) ? '<'
							: Unicode.LESS_EQUAL;
					setRightBound(leftRight);
				} else if (leftRight instanceof FunctionVariable
						&& leftLeft.isNumberValue()) {
					leftInequality = opLeft.equals(Operation.LESS) ? '<'
							: Unicode.LESS_EQUAL;
					setLeftBound(leftLeft);
				}

			} else if ((opLeft.equals(Operation.GREATER) || opLeft
					.equals(Operation.GREATER_EQUAL))) {
				if (leftLeft instanceof FunctionVariable
						&& leftRight.isNumberValue()) {
					leftInequality = opLeft.equals(Operation.GREATER) ? '<'
							: Unicode.LESS_EQUAL;
					setLeftBound(leftRight);
				} else if (leftRight instanceof FunctionVariable
						&& leftLeft.isNumberValue()) {
					rightInequality = opLeft.equals(Operation.GREATER) ? '<'
							: Unicode.LESS_EQUAL;
					setRightBound(leftLeft);
				}

			}

			if ((opRight.equals(Operation.LESS) || opRight
					.equals(Operation.LESS_EQUAL))) {
				if (rightLeft instanceof FunctionVariable
						&& rightRight.isNumberValue()) {
					rightInequality = opRight.equals(Operation.LESS) ? '<'
							: Unicode.LESS_EQUAL;
					setRightBound(rightRight);
				} else if (rightRight instanceof FunctionVariable
						&& rightLeft.isNumberValue()) {
					leftInequality = opRight.equals(Operation.LESS) ? '<'
							: Unicode.LESS_EQUAL;
					setLeftBound(rightLeft);
				}

			} else if ((opRight.equals(Operation.GREATER) || opRight
					.equals(Operation.GREATER_EQUAL))) {
				if (rightLeft instanceof FunctionVariable
						&& rightRight.isNumberValue()) {
					leftInequality = opRight.equals(Operation.GREATER) ? '<'
							: Unicode.LESS_EQUAL;
					setLeftBound(rightRight);
				} else if (rightRight instanceof FunctionVariable
						&& rightLeft.isNumberValue()) {
					rightInequality = opRight.equals(Operation.GREATER) ? '<'
							: Unicode.LESS_EQUAL;
					setRightBound(rightLeft);
				}

			}
		} else {
			rightBound = Double.NaN;
			leftBound = Double.NaN;
		}

		if (rightBound < leftBound) {
			rightBound = Double.NaN;
			leftBound = Double.NaN;
		}

	}

	private void setLeftBound(ExpressionValue nv) {
		leftBound = nv.evaluateNum().getDouble();
		if (nv.isGeoElement())
			leftStr = ((GeoElement) nv).getLabel(StringTemplate.defaultTemplate);
		else
			leftStr = nv.toString(StringTemplate.defaultTemplate);
	}

	private void setRightBound(ExpressionValue nv) {
		rightBound = nv.evaluateNum().getDouble();
		if (nv.isGeoElement())
			rightStr = ((GeoElement) nv).getLabel(StringTemplate.defaultTemplate);
		else
			rightStr = nv.toString(StringTemplate.defaultTemplate);
	}

	/**
	 * @return left bound of the interval
	 */
	public double getMin() {
		updateBoundaries();
		return leftBound;

	}

	/**
	 * @return right bound of the interval
	 */
	public double getMax() {
		updateBoundaries();
		return rightBound;

	}

	/**
	 * @return center of the interval (number)
	 */
	public double getMidPoint() {
		updateBoundaries();
		return (rightBound + leftBound) / 2;

	}

	@Override
	public boolean isGeoInterval() {
		return true;
	}

	@Override
	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}
	
	@Override
	public int getMinimumLineThickness() {
		return 0;
	}


}
