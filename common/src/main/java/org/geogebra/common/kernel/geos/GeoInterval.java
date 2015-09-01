package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoMacroInterface;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.Unicode;

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

	//private double rightBound = Double.NaN;
	//private double leftBound = Double.NaN;
	private double[] leftRightBoundsField;
	
	//private String rightStr = "", leftStr = "";
	private String leftRightStrField[];
	
	//private char rightInequality = ' ';
	//private char leftInequality = ' ';
	private char[] leftRightInequalityField;
	/**
	 * Returns string description of the interval
	 * 
	 * @param symbolic
	 *            true for symbolic, false for numeric
	 * @return string description of the interval
	 */
	private String toString(boolean symbolic,StringTemplate tpl) {

		// output as nice string eg 3 < x < 5

		if (!isDefined()) {
			return "?";
		}
		
		// return "3 < x < 5";//fun.toValueString();

		ExpressionNode en = fun.getExpression();
		if (en.getOperation().equals(Operation.AND) || en.getOperation().equals(Operation.AND_INTERVAL)) {
			ExpressionValue left = en.getLeft();
			ExpressionValue right = en.getRight();

			if (left.isExpressionNode() && right.isExpressionNode()) {

				updateBoundaries();

				if (!Double.isNaN(leftRightBoundsField[1]) && !Double.isNaN(leftRightBoundsField[0])
						&& leftRightBoundsField[0] <= leftRightBoundsField[1]) {
					sbToString.setLength(0);
					sbToString.append(symbolic ? leftRightStrField[0] : kernel
							.format(leftRightBoundsField[0],tpl));
					sbToString.append(' ');
					sbToString.append(leftRightInequalityField[0]);
					sbToString.append(' ');
					sbToString.append(getVarString(tpl));
					sbToString.append(' ');
					sbToString.append(leftRightInequalityField[1]);
					sbToString.append(' ');
					sbToString.append(symbolic ? leftRightStrField[1] : kernel
							.format(leftRightBoundsField[1],tpl));
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

	private void updateBoundaries() {
		if (leftRightBoundsField == null) {
			leftRightBoundsField = new double[2];
			leftRightBoundsField[0] = Double.NaN;
			leftRightBoundsField[1] = Double.NaN;
		}
		
		if (leftRightStrField == null) {
			leftRightStrField = new String[2];
		}

		if (leftRightInequalityField == null) {
			leftRightInequalityField = new char[2];
		}

		updateBoundaries(fun.getExpression(), leftRightBoundsField, leftRightStrField, leftRightInequalityField);	
	}

	@Override
	public String toSymbolicString(StringTemplate tpl) {
		if (isDefined()) {
			return toString(true,tpl);
		}
		return "?";
	}

	@Override
	public String toLaTeXString(boolean symbolic,StringTemplate tpl) {
		if (isDefined()) {
			return fun.toLaTeXString(symbolic,tpl);
		}
		return "?";
	}

	@Override
	public boolean isEqual(GeoElement geo) {
		return false;
	}

	public static void updateBoundaries(ExpressionNode en, double[] leftRightDouble, String[] leftRightStr, char[] leftRightInequalityChar) {
		
		char leftInequality, rightInequality;
		double leftBound, rightBound;
				
		leftBound = leftRightDouble[0];
		rightBound = leftRightDouble[1];
		leftInequality = leftRightInequalityChar[0];
		rightInequality = leftRightInequalityChar[1];
		
		if (en.getOperation().equals(Operation.AND) || en.getOperation().equals(Operation.AND_INTERVAL)) {
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
					rightBound = setRightBound(leftRight, leftRightStr);
				} else if (leftRight instanceof FunctionVariable
						&& leftLeft.isNumberValue()) {
					leftInequality = opLeft.equals(Operation.LESS) ? '<'
							: Unicode.LESS_EQUAL;
					leftBound = setLeftBound(leftLeft, leftRightStr);
				}

			} else if ((opLeft.equals(Operation.GREATER) || opLeft
					.equals(Operation.GREATER_EQUAL))) {
				if (leftLeft instanceof FunctionVariable
						&& leftRight.isNumberValue()) {
					leftInequality = opLeft.equals(Operation.GREATER) ? '<'
							: Unicode.LESS_EQUAL;
					leftBound = setLeftBound(leftRight, leftRightStr);
				} else if (leftRight instanceof FunctionVariable
						&& leftLeft.isNumberValue()) {
					rightInequality = opLeft.equals(Operation.GREATER) ? '<'
							: Unicode.LESS_EQUAL;
					rightBound = setRightBound(leftLeft, leftRightStr);
				}

			}

			if ((opRight.equals(Operation.LESS) || opRight
					.equals(Operation.LESS_EQUAL))) {
				if (rightLeft instanceof FunctionVariable
						&& rightRight.isNumberValue()) {
					rightInequality = opRight.equals(Operation.LESS) ? '<'
							: Unicode.LESS_EQUAL;
					rightBound = setRightBound(rightRight, leftRightStr);
				} else if (rightRight instanceof FunctionVariable
						&& rightLeft.isNumberValue()) {
					leftInequality = opRight.equals(Operation.LESS) ? '<'
							: Unicode.LESS_EQUAL;
					leftBound = setLeftBound(rightLeft, leftRightStr);
				}

			} else if ((opRight.equals(Operation.GREATER) || opRight
					.equals(Operation.GREATER_EQUAL))) {
				if (rightLeft instanceof FunctionVariable
						&& rightRight.isNumberValue()) {
					leftInequality = opRight.equals(Operation.GREATER) ? '<'
							: Unicode.LESS_EQUAL;
					leftBound = setLeftBound(rightRight, leftRightStr);
				} else if (rightRight instanceof FunctionVariable
						&& rightLeft.isNumberValue()) {
					rightInequality = opRight.equals(Operation.GREATER) ? '<'
							: Unicode.LESS_EQUAL;
					rightBound = setRightBound(rightLeft, leftRightStr);
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
		
		// values to return
		leftRightDouble[0] = leftBound;
		leftRightDouble[1] = rightBound;
		leftRightInequalityChar[0] = leftInequality;
		leftRightInequalityChar[1] = rightInequality;
	}

	private static double setLeftBound(ExpressionValue nv, String[] leftRightStr) {
		if (nv.isGeoElement()) {
			leftRightStr[0] = ((GeoElement) nv).getLabel(StringTemplate.defaultTemplate);
		} else {
			leftRightStr[0] = nv.toString(StringTemplate.defaultTemplate);
		}
		return nv.evaluateDouble();
		
	}

	private static double setRightBound(ExpressionValue nv, String[] leftRightStr) {
		if (nv.isGeoElement()) {
			leftRightStr[1] = ((GeoElement) nv).getLabel(StringTemplate.defaultTemplate);
		} else {
			leftRightStr[1] = nv.toString(StringTemplate.defaultTemplate);
		}
		return nv.evaluateDouble();
	}

	/**
	 * @return left bound of the interval
	 */
	public double getMin() {
		updateBoundaries();
		return leftRightBoundsField[0];

	}

	/**
	 * @return right bound of the interval
	 */
	public double getMax() {
		updateBoundaries();
		return leftRightBoundsField[1];

	}

	/**
	 * @return center of the interval (number)
	 */
	public double getMidPoint() {
		updateBoundaries();
		return (leftRightBoundsField[1] + leftRightBoundsField[0]) / 2;

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
