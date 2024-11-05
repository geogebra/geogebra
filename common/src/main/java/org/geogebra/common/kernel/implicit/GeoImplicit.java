package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocusND;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Interface for implicit curves
 */
public interface GeoImplicit extends Path, EquationValue {

	/**
	 * @return coefficients
	 */
	double[][] getCoeff();

	/**
	 * @param coeff
	 *            coefficients
	 */
	void setCoeff(double[][] coeff);

	/**
	 * @param coeff
	 *            factored coefficients
	 */
	void setCoeff(double[][][] coeff);

	/**
	 * Reset undefined flag
	 */
	void setDefined();

	/**
	 * @return total degree
	 */
	int getDeg();

	/**
	 * @return whether it's visible in at least one view
	 */
	boolean isOnScreen();

	/**
	 * @return locus for drawing
	 */
	GeoLocusND<? extends MyPoint> getLocus();

	/**
	 * @return degree in x or -1
	 */
	int getDegX();

	/**
	 * @return degree in y or -1
	 */
	int getDegY();

	/**
	 * @param dx
	 *            horizontal shift
	 * @param dy
	 *            vertical shift
	 */
	void translate(double dx, double dy);

	/**
	 * @return LHS-RHS
	 */
	FunctionNVar getExpression();

	/**
	 * @return whether user input can be used
	 */
	boolean isValidInputForm();

	/**
	 * @return whether the print form is input
	 */
	boolean isInputForm();

	/**
	 * @param equation
	 *            equation
	 * @param coeff
	 *            coefficients for polynomial or null
	 */
	void fromEquation(Equation equation, double[][] coeff);

	/**
	 * Make sure locus points are not computed
	 */
	void preventPathCreation();

	/**
	 * @param coeff
	 *            coefficients
	 */
	void setCoeff(ExpressionValue[][] coeff);

	/**
	 * @param points
	 *            list of points
	 */
	void throughPoints(GeoList points);

	/**
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @return f_x(x,y) where f = lhs-rhs
	 */
	double derivativeX(double x, double y);

	/**
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @return f_y(x,y) where f = lhs-rhs
	 */
	double derivativeY(double x, double y);

	/**
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @return f(x,y) where f = lhs-rhs
	 */
	double evaluateImplicitCurve(double x, double y);

	/**
	 * 
	 * @return coord sys for transformations
	 */
	CoordSys getTransformedCoordSys();

	/**
	 * 
	 * @return function expression creating this implicit curve
	 */
	FunctionNVar getFunctionDefinition();

	/**
	 * 
	 * @return plane equation
	 */
	Coords getPlaneEquation();

	/**
	 * 
	 * @return z-coord translation (if exists)
	 */
	double getTranslateZ();

	/**
	 * copy derivatives
	 * @param x - x derivative
	 * @param y - y derivative
	 * @param xy - xy derivative
	 */
	void setDerivatives(FunctionNVar x, FunctionNVar y, FunctionNVar xy);

	/**
	 * @param equationExpanded expanded form of definition
	 */
	void setExpanded(Equation equationExpanded);

	// TODO APPS-5867 mend implicit curves/surfaces into equation form concept
	void setToUser();

	void setToImplicit();
}
