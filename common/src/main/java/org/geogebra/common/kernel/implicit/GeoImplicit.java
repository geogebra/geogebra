package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocusND;

public interface GeoImplicit extends Path {

	double[][] getCoeff();

	void setCoeff(double[][] coeff);

	void setCoeff(double[][][] coeff);

	void setDefined();

	int getDeg();

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
	 * Switch to user input form
	 */
	void setInputForm();

	void translate(double d, double e);

	FunctionNVar getExpression();

	boolean isValidInputForm();

	/**
	 * @return whether the print form is input
	 */
	boolean isInputForm();

	/**
	 * Switch print form to expanded
	 */
	void setExtendedForm();

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
	public CoordSys getTransformedCoordSys();

	/**
	 * 
	 * @return function expression creating this implicit curve
	 */
	public FunctionNVar getFunctionDefinition();

	/**
	 * 
	 * @return plane equation
	 */
	public Coords getPlaneEquation();

	/**
	 * 
	 * @return z-coord translation (if exists)
	 */
	public double getTranslateZ();

}
