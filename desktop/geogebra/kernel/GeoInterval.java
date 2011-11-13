package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.util.Unicode;
/**
 * Boolean function of the type a(<|<=)x(<|<=)b
 * @author Markus Hohenwarter
 *
 */
public class GeoInterval extends GeoFunction {

	/**
	 * Creates new GeoInterval
	 * @param c
	 * @param label
	 * @param f
	 */
	public GeoInterval(Construction c, String label, Function f) {
		super(c, label, f);
	}

	/**
	 * Copy constructor
	 * @param geoInterval
	 */
	public GeoInterval(GeoInterval geoInterval) {
		super(geoInterval.cons);
		set(geoInterval);
	}

	/**
	 * Creates new unlabeled interval
	 * @param cons
	 */
	public GeoInterval(Construction cons) {
		super(cons);
	}

	public GeoElement copy() {
		return new GeoInterval(this);
	}

	public void set(GeoElement geo) {
		GeoInterval geoFun = (GeoInterval) geo;				

		if (geo == null || geoFun.fun == null) {
			fun = null;
			isDefined = false;
			return;
		} else {
			isDefined = geoFun.isDefined;
			fun = new Function(geoFun.fun, kernel);
		}			

		// macro OUTPUT
		if (geo.cons != cons && isAlgoMacroOutput()) {								
			// this object is an output object of AlgoMacro
			// we need to check the references to all geos in its function's expression
			if (!geoFun.isIndependent()) {
				AlgoMacro algoMacro = (AlgoMacro) getParentAlgorithm();
				algoMacro.initFunction(this.fun);	
			}			
		}
	}

	public String getClassName() {
		return "GeoInterval";
	}

	protected String getTypeString() {
		return "Interval";
	}

	public int getGeoClassType() {
		return GEO_CLASS_INTERVAL;
	}
	
	private StringBuilder sbToString2;

	public String toString() {
		if (sbToString2 == null) sbToString2 = new StringBuilder();
		else sbToString2.setLength(0);
		if(isLabelSet()) {
			sbToString2.append(label);
			sbToString2.append(": ");
		}
		sbToString2.append(toSymbolicString());
		return sbToString2.toString();
	}

	public String toValueString() {
		return toString(false);
	}

	private double rightBound = Double.NaN;
	private double leftBound = Double.NaN;

	private String rightStr = "", leftStr = "";
	private char rightInequality = ' ';
	private char leftInequality = ' ';


	/**
	 * Returns string description of the interval
	 * @param symbolic true for symbolic, false for numeric
	 * @return string description of the interval
	 */
	private String toString(boolean symbolic) {		

		// output as nice string eg 3 < x < 5

		if (!isDefined()) return app.getPlain("undefined");

		//return "3 < x < 5";//fun.toValueString();

		ExpressionNode en = fun.getExpression();
		if (en.operation == ExpressionNode.AND) {
			ExpressionValue left = en.left;
			ExpressionValue right = en.right;

			if (left.isExpressionNode() && right.isExpressionNode()) {

				updateBoundaries();

				if (!Double.isNaN(rightBound) && !Double.isNaN(leftBound) && leftBound <= rightBound) {
					sbToString.setLength(0);
					sbToString.append(symbolic ? leftStr : kernel.format(leftBound));
					sbToString.append(' ');
					sbToString.append(leftInequality);
					sbToString.append(' ');
					sbToString.append(getVarString());
					sbToString.append(' ');
					sbToString.append(rightInequality);
					sbToString.append(' ');
					sbToString.append(symbolic ? rightStr : kernel.format(rightBound));
					return sbToString.toString();
					//return kernel.format(leftBound) +leftInequality+" x "+rightInequality+kernel.format(rightBound);
				}
			}
		} 


		// eg x<3 && x>10
		//Application.debug("fall through");
		return symbolic ? super.toSymbolicString() : super.toValueString();		

	}	

	public String toSymbolicString() {	
		if (isDefined())
			return toString(true);
		else
			return app.getPlain("undefined");
	}

	public String toLaTeXString(boolean symbolic) {
		if (isDefined())
			return fun.toLaTeXString(symbolic);
		else
			return app.getPlain("undefined");
	}

	public boolean isEqual(GeoElement geo) {
		return false;
	}

	private void updateBoundaries() {
		ExpressionNode en = fun.getExpression();
		if (en.operation == ExpressionNode.AND) {
			ExpressionValue left = en.left;
			ExpressionValue right = en.right;
			ExpressionNode enLeft = (ExpressionNode)left;
			ExpressionNode enRight = (ExpressionNode)right;

			int opLeft = enLeft.operation;
			int opRight = enRight.operation;

			ExpressionValue leftLeft = enLeft.left;
			ExpressionValue leftRight = enLeft.right;
			ExpressionValue rightLeft = enRight.left;
			ExpressionValue rightRight = enRight.right;

			if ((opLeft == ExpressionNode.LESS || opLeft == ExpressionNode.LESS_EQUAL)) {
				if (leftLeft instanceof FunctionVariable && leftRight.isNumberValue()) {
					rightInequality = opLeft == ExpressionNode.LESS ? '<' : Unicode.LESS_EQUAL;
					setRightBound(leftRight);
				}
				else if (leftRight instanceof FunctionVariable && leftLeft.isNumberValue()) {
					leftInequality = opLeft == ExpressionNode.LESS ? '<' : Unicode.LESS_EQUAL;
					setLeftBound(leftLeft);					
				}

			} else
				if ((opLeft == ExpressionNode.GREATER || opLeft == ExpressionNode.GREATER_EQUAL)) {
					if (leftLeft instanceof FunctionVariable && leftRight.isNumberValue()) {
						leftInequality = opLeft == ExpressionNode.GREATER ? '<' : Unicode.LESS_EQUAL;
						setLeftBound(leftRight);
					}
					else if (leftRight instanceof FunctionVariable && leftLeft.isNumberValue()) {
						rightInequality = opLeft == ExpressionNode.GREATER ? '<' : Unicode.LESS_EQUAL;
						setRightBound(leftLeft);
					}

				}

			if ((opRight == ExpressionNode.LESS || opRight == ExpressionNode.LESS_EQUAL)) {
				if (rightLeft instanceof FunctionVariable && rightRight.isNumberValue()) {
					rightInequality = opRight == ExpressionNode.LESS ? '<' : Unicode.LESS_EQUAL;
					setRightBound(rightRight);
				}
				else if (rightRight instanceof FunctionVariable && rightLeft.isNumberValue()) {
					leftInequality = opRight == ExpressionNode.LESS ? '<' : Unicode.LESS_EQUAL;
					setLeftBound(rightLeft);
				}

			} else
				if ((opRight == ExpressionNode.GREATER || opRight == ExpressionNode.GREATER_EQUAL)) {
					if (rightLeft instanceof FunctionVariable && rightRight.isNumberValue()) {
						leftInequality = opRight == ExpressionNode.GREATER ? '<' : Unicode.LESS_EQUAL;
						setLeftBound(rightRight);
					}
					else if (rightRight instanceof FunctionVariable && rightLeft.isNumberValue()) {
						rightInequality = opRight == ExpressionNode.GREATER ? '<' : Unicode.LESS_EQUAL;
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
	private void setLeftBound(ExpressionValue nv){
		leftBound = ((NumberValue)nv.evaluate()).getDouble();
		if(nv.isGeoElement())
			leftStr = ((GeoElement)nv).getLabel();
		else
			leftStr =  nv.toString();
	}
	private void setRightBound(ExpressionValue nv){
		rightBound = ((NumberValue)nv.evaluate()).getDouble();
		if(nv.isGeoElement())
			rightStr = ((GeoElement)nv).getLabel();
		else
			rightStr = nv.toString();
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

	public boolean isGeoInterval() {
		return true;
	}
	
	public String toOutputValueString(){
		return toValueString();
	}



}
