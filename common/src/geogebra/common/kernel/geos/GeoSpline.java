package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.MatrixTransformable;
import geogebra.common.kernel.PathMover;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.VarString;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.roots.RealRootFunction;
import geogebra.common.plugin.GeoClass;

import java.util.Arrays;

/**
 * Use the Gauss-Jordan method to solve the linear system. The spline is
 * parametric
 * 
 * @author Giuliano
 * 
 */

public class GeoSpline extends GeoElement implements Transformable, VarString,
		ParametricCurve, LineProperties, Translateable, PointRotateable,
		Mirrorable, ConicMirrorable, Dilateable, MatrixTransformable {

	private GeoList points;
	private GeoList curves;

	private float[] parametersValues;
	private float[] parametersX;
	private float[] parametersY;
	private float[] cumulativeValueOfParameter;
	private float[] parameterIntervalLimits;
	private float[][] floatPoints;
	private GeoNumberValue degree;
	private int degreeValue;
	private int length;

	private boolean isDefined = true;
	private boolean trace = false;

	/**
	 * @param c
	 *            construction
	 * @param label
	 * @param points
	 *            list of points crossed by the spline
	 * @param degree
	 *            degree of polynoms
	 */
	public GeoSpline(Construction c, GeoList points, GeoNumberValue degree) {
		super(c);
		this.points = points;
		curves = new GeoList(c);
		curves.setEuclidianVisible(true);
		parametersValues = new float[points.size()];
		floatPoints = new float[points.size()][2];
		this.degree = degree;
	}

	public GeoSpline(Construction c, GeoList points) {
		this(c, points, new GeoNumeric(c, 3));
	}

	/**
	 * @param cons
	 */
	public GeoSpline(final Construction cons) {
		super(cons);
	}

	public GeoSpline(GeoSpline geoSpline) {
		this(geoSpline.cons);
		points = new GeoList(cons);
		set(geoSpline);
	}

	public float[] getCumulativeValueOfParameter() {
		return cumulativeValueOfParameter;
	}

	public float[] getParametersX() {
		return parametersX;
	}

	public float[] getParametersY() {
		return parametersY;
	}

	public float[][] getFloatPoints() {
		return floatPoints;
	}

	public GeoNumberValue getDegree() {
		return degree;
	}

	public GeoList getPoints() {
		return points;
	}

	@Override
	public void set(GeoElement geo) {
		GeoSpline s = (GeoSpline) geo;
		points.clear();
		for (int i = 0; i < s.getPoints().size(); i++) {
			points.add(new GeoPoint((GeoPoint) s.getPoints().get(i)));
		}
		degree = new GeoNumeric(cons, s.getDegree().getDouble());
		parametersValues = new float[points.size()];
		floatPoints = new float[points.size()][2];
		curves = new GeoList(cons);
		curves.setEuclidianVisible(true);
		recalculate();
	}

	@Override
	public GeoElement copy() {
		return new GeoSpline(this);
	}

	@Override
	public boolean isGeoCurveCartesian() {
		return true;
		/*
		 * We can't refer to class names as these get obfuscated for real
		 * release. Let's make sure that either no GeoSpline is needed and
		 * everything is handled from GeoCurveCartesian or if there is an
		 * advantage of subclassing, then GeoSpline is a subclass of
		 * GeoCurveCartesian
		 * 
		 * 
		 * StackTraceElement[] s = Thread.getAllStackTraces().get(
		 * Thread.currentThread()); int i = search(s); i++; return
		 * s[i].getClassName().equals( "geogebra.common.kernel.geos.GeoElement")
		 * || s[i].getClassName().equals(
		 * "geogebra.common.kernel.arithmetic.ExpressionNode") ||
		 * s[i].getClassName().equals( "geogebra.common.kernel.parser.Parser");
		 */
	}

	@Override
	public boolean isPath() {
		return true;
	}

	@Override
	public boolean isMatrixTransformable() {
		return true;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.SPLINE;
	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return true;
	}

	@Override
	public boolean isDefined() {
		return isDefined;
	}

	@Override
	public void setUndefined() {
		isDefined = false;
	}

	@Override
	public boolean isFillable() {
		return isClosedPath();
	}

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		return isDefined();
	}

	public boolean getTrace() {
		return trace;
	}

	public boolean isFunctionInX() {
		return false;
	}

	@Override
	public boolean isEqual(GeoElement geo) {
		return false;
	}

	@Override
	public boolean isInverseFillable() {
		return isFillable();
	}

	public boolean isClosedPath() {
		return Kernel.isEqual(((GeoPoint) points.get(0)).getX(),
				((GeoPoint) points.get(points.size() - 1)).getX(),
				Kernel.STANDARD_PRECISION)
				&& Kernel.isEqual(((GeoPoint) points.get(0)).getY(),
						((GeoPoint) points.get(points.size() - 1)).getY(),
						Kernel.STANDARD_PRECISION);
	}

	public void setTrace(boolean flag) {
		trace = flag;
	}

	public void pointChanged(GeoPointND PI) {
		curves.pointChanged(PI);
	}

	public void pathChanged(GeoPointND PI) {
		curves.pathChanged(PI);
	}

	public boolean isOnPath(GeoPointND PI, double eps) {
		return curves.isOnPath(PI, eps);
	}

	public PathMover createPathMover() {
		return curves.createPathMover();
	}

	public double getMinParameter() {
		return 0;
	}

	public double getMaxParameter() {
		return 1;
	}

	public void evaluateCurve(double t, double[] out) {
		if (t < 0 || t > 1) {
			return;
		}
		((GeoCurveCartesian) curves.get(getParameterIndex(t))).evaluateCurve(t,
				out);
	}

	public GeoVec2D evaluateCurve(double t) {
		if (t < 0 || t > 1) {
			return new GeoVec2D(kernel);
		}
		return ((GeoCurveCartesian) curves.get(getParameterIndex(t)))
				.evaluateCurve(t);
	}

	public double evaluateCurvature(double t) {
		if (t < 0 || t > 1) {
			return Double.NaN;
		}
		return ((GeoCurveCartesian) curves.get(getParameterIndex(t)))
				.evaluateCurvature(t);
	}

	private int getParameterIndex(double t) {
		if (t < parametersValues[0]) {
			return 0;
		}
		for (int i = 1; i < parametersValues.length; i++) {
			if (t < parametersValues[i]) {
				return i - 1;
			}
		}
		return parametersValues.length - 2;
	}

	@Override
	public double distance(GeoPoint p) {
		GeoCurveCartesian curve = curveOfDistance(p);
		return curve.distance(p);
	}

	/**
	 * @param p
	 *            point
	 * @return curve with min distance
	 */
	public GeoCurveCartesian curveOfDistance(GeoPointND p) {
		double min = Double.MAX_VALUE;
		GeoCurveCartesian minF = null;
		for (int i = 0; i < curves.size(); i++) {
			GeoCurveCartesian f = (GeoCurveCartesian) curves.get(i);
			if (f.distance(p) < min) {
				min = f.distance(p);
				minF = f;
			}
		}
		return minF;
	}

	public RealRootFunction getRealRootFunctionX() {
		return new SplineDistance(true);
	}

	public RealRootFunction getRealRootFunctionY() {
		return new SplineDistance(false);
	}

	@Override
	public String toString(StringTemplate tpl) {
		StringBuilder sbToString = new StringBuilder(80);
		sbToString.setLength(0);
		if (isLabelSet()) {
			sbToString.append(label);
			sbToString.append(':');
		}
		sbToString.append(toValueString(tpl));
		return sbToString.toString();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		if (isDefined()) {
			StringBuilder sbTemp = new StringBuilder(80);
			sbTemp.setLength(0);
			sbTemp.append('[');
			for (int i = 0; i < curves.size(); i++) {
				sbTemp.append(((GeoCurveCartesian) curves.get(i))
						.toValueString(tpl));
			}
			sbTemp.append(']');
			return sbTemp.toString();
		}
		return app.getPlain("Undefined");
	}

	public String getVarString(StringTemplate tpl) {
		if (isDefined()) {
			StringBuilder sbTemp = new StringBuilder(80);
			sbTemp.setLength(0);
			sbTemp.append('[');
			for (int i = 0; i < curves.size(); i++) {
				sbTemp.append(((GeoCurveCartesian) curves.get(i))
						.toValueString(tpl));
			}
			sbTemp.append(']');
			return sbTemp.toString();
		}
		return app.getPlain("Undefined");
	}

	@Override
	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		if (isDefined()) {
			String s = "";
			for (int i = 0; i < curves.size(); i++) {
				s += curves.get(i).toLaTeXString(symbolic, tpl);
			}
			return s;
		}
		return " \\text{" + app.getPlain("Undefined") + "} ";
	}

	public void recalculate() {
		if (!points.isDefined()) {
			setUndefined();
			return;
		}

		degreeValue = (int) degree.getDouble() + 1;

		if (degreeValue < 4 || degreeValue > floatPoints.length + 1) {
			setUndefined();
			return;
		}
		int i = 0;
		for (; i < floatPoints.length - 1; i++) {
			GeoPoint p = (GeoPoint) points.get(i);
			floatPoints[i][0] = (float) p.getX();
			floatPoints[i][1] = (float) p.getY();
		}

		floatPoints[i][0] = (float) ((GeoPoint) points.get(i)).getX();
		floatPoints[i][1] = (float) ((GeoPoint) points.get(i)).getY();

		parametersX = getSystemSolution(getLinearSystemParametric(0));
		parametersY = getSystemSolution(getLinearSystemParametric(1));
		if (parametersX == null || parametersY == null) {
			return;
		}
		constructSpline();
	}

	public float[] getParameterIntervalLimits() {
		length = cumulativeValueOfParameter.length;
		parameterIntervalLimits = new float[length];
		for (int i = 1; i < length; i++) {
			parameterIntervalLimits[i] = cumulativeValueOfParameter[i]
					/ cumulativeValueOfParameter[cumulativeValueOfParameter.length - 1];
		}
		return parameterIntervalLimits;
	}

	private void calculateParameterValues() {
		int j = 0;
		float parameterValue = 0;
		float[] lx = getParameterIntervalLimits();
		for (float p = 0; p <= 1; p = p + 0.01f) {
			parameterValue = calculate(p, lx);
			if (Arrays.binarySearch(parametersValues, parameterValue) < 0) {
				if (j < parametersValues.length) {
					parametersValues[j] = parameterValue;
					j++;
				}
			}
		}
		parametersValues[length - 1] = 1;
	}

	private static float calculate(float x, float[] m) {
		for (int i = m.length - 1; i > -1; i--) {
			if (x > m[i]) {
				return m[i];
			}
		}
		return 0;
	}

	private void constructSpline() {
		Arrays.fill(parametersValues, Float.MAX_VALUE);
		curves.clear();
		calculateParameterValues();
		int k = 0;
		length = parametersX.length;
		for (int i = 0; i < length; i += degreeValue) {
			AlgebraProcessor ap = kernel.getAlgebraProcessor();
			StringBuilder sbX = new StringBuilder("splineX(t)=");
			StringBuilder sbY = new StringBuilder("splineY(t)=");
			for (int j = degreeValue - 1; j > -1; j--) {
				sbX.append(parametersX[i + degreeValue - 1 - j] + "*t^" + j
						+ "+");
				sbY.append(parametersY[i + degreeValue - 1 - j] + "*t^" + j
						+ "+");
			}
			GeoFunction fx = ap.evaluateToFunction(
					sbX.substring(0, sbX.length() - 1), true);
			GeoFunction fy = ap.evaluateToFunction(
					sbY.substring(0, sbY.length() - 1), true);
			GeoCurveCartesian curve = new GeoCurveCartesian(cons);
			curve.setFunctionX(fx.getFunction());
			curve.setFunctionY(fy.getFunction());
			curve.setInterval(parametersValues[k], parametersValues[k + 1]);
			curve.setEuclidianVisible(true);
			curve.setObjColor(getObjectColor());
			curves.add(curve);
			k++;
		}
		curves.setObjColor(getObjectColor());
	}

	private float[] getSystemSolution(float[][] matrix) {
		boolean nok = false;
		length = matrix.length;
		float[] solution = new float[length];
		float[] temp = new float[matrix[0].length];
		int column;
		int row;
		int i;
		int j;
		for (column = 0; column < length - 1; column++) {
			for (i = column; i < length - 1; i++) {
				for (j = i + 1; j < length; j++) {
					if (Math.abs(matrix[i][column]) < Math
							.abs(matrix[j][column])) {
						System.arraycopy(matrix[i], column, temp, column,
								length + 1 - column);
						System.arraycopy(matrix[j], column, matrix[i], column,
								length + 1 - column);
						System.arraycopy(temp, column, matrix[j], column,
								length + 1 - column);
					}
				}
			}

			for (row = column; row < length && matrix[row][column] == 0; row++)
				;
			float value;
			if (row != length - 1) {
				for (i = column; i < length; i++) {
					if (matrix[i][column] != 0 && i != row) {
						value = matrix[i][column] / matrix[row][column];
						for (j = column; j < length + 1; j++) {
							matrix[row][j] = matrix[row][j] * value;
							matrix[i][j] = matrix[i][j] - matrix[row][j];
						}
					}
				}
			}
		}
		j = 0;
		nok = true;
		for (; j < length && nok; j++) {
			if (matrix[length - 1][j] != 0) {
				nok = false;
			}
		}
		if (nok) {
			setUndefined();
			return null;
		}
		solution[solution.length - 1] = matrix[length - 1][length]
				/ matrix[length - 1][length - 1];
		float buffer;
		int ii;
		for (i = length - 2; i > -1; i--) {
			buffer = 0;
			for (ii = length - 1; ii > i; ii--) {
				buffer = buffer + solution[ii] * matrix[i][ii];
			}
			solution[i] = (matrix[i][length] - buffer) / matrix[i][i];
		}
		return solution;
	}

	private float[][] getLinearSystemParametric(int c) {
		int row = 0;
		int col = 0;
		int pointIndex;
		float currentValueFromZeroToOne;
		length = floatPoints.length;
		cumulativeValueOfParameter = new float[length];
		for (col = 1; col < length; col++) {
			for (row = 1; row <= col; row++) {
				cumulativeValueOfParameter[col] = cumulativeValueOfParameter[col]
						+ (float) Math
								.sqrt((floatPoints[row][0] - floatPoints[row - 1][0])
										* (floatPoints[row][0] - floatPoints[row - 1][0])
										+ (floatPoints[row][1] - floatPoints[row - 1][1])
										* (floatPoints[row][1] - floatPoints[row - 1][1]));
			}
		}
		float[][] matrix = new float[(length - 1) * degreeValue][(length - 1)
				* degreeValue + 1];
		row = 0;
		col = 0;
		for (pointIndex = 0; pointIndex < length - 1; pointIndex++) {
			currentValueFromZeroToOne = cumulativeValueOfParameter[pointIndex]
					/ cumulativeValueOfParameter[length - 1];
			evalForPoint(matrix, row, col, currentValueFromZeroToOne);
			matrix[row][matrix.length] = floatPoints[pointIndex][c];
			row++;
			col += degreeValue;

		}
		col = 0;
		for (pointIndex = 1; pointIndex < length; pointIndex++) {
			currentValueFromZeroToOne = cumulativeValueOfParameter[pointIndex]
					/ cumulativeValueOfParameter[length - 1];
			evalForPoint(matrix, row, col, currentValueFromZeroToOne);
			matrix[row][matrix.length] = floatPoints[pointIndex][c];
			row++;
			col += degreeValue;

		}

		for (int currentDerivative = degreeValue - 2; currentDerivative > 0; currentDerivative--) {
			col = 0;
			for (pointIndex = 1; pointIndex < length - 1; pointIndex++) {
				currentValueFromZeroToOne = cumulativeValueOfParameter[pointIndex]
						/ cumulativeValueOfParameter[length - 1];
				calcDerivative(matrix[row], col, currentDerivative,
						currentValueFromZeroToOne);
				row++;
				col += degreeValue;
			}
		}
		matrix[row][0] = 0;
		matrix[row][1] = fact(degreeValue - 2);
		row++;
		matrix[row][matrix.length - degreeValue] = fact(degreeValue - 1);
		matrix[row][matrix.length - degreeValue + 1] = fact(degreeValue - 2);
		row++;
		int num = 2;

		for (; row < matrix.length; row++) {
			matrix[row][matrix.length - num * degreeValue] = fact(degreeValue - 1)
					* cumulativeValueOfParameter[num - 1]
					/ cumulativeValueOfParameter[length - 1];
			matrix[row][matrix.length - num * degreeValue + 1] = fact(degreeValue - 2);
			num++;
		}
		return matrix;
	}

	private static float fact(int i) {
		int f = 1;
		for (int j = 2; j <= i; j++) {
			f *= j;
		}
		return f;
	}

	private void calcDerivative(float[] row, int col, int currentDerivative,
			float currentValueFromZeroToOne) {
		for (int i = col; i < col + degreeValue; i++) {
			row[i] = calcCoeff(i, currentDerivative, currentValueFromZeroToOne);
			row[i + degreeValue] = -row[i];
		}
	}

	private float calcCoeff(int col, int currentDerivative,
			float currentValueFromZeroToOne) {
		int exp = col % degreeValue;
		exp = degreeValue - exp - 1;
		float coeff = (float) Math.pow(currentValueFromZeroToOne, exp - 1);
		if (exp == 0) {
			return 0;
		}
		for (int i = degreeValue - 1; i > currentDerivative; i--) {
			coeff *= exp;
			exp--;
		}
		return coeff;
	}

	private float evalForPoint(float[][] matrix, int row, int col,
			float currentValueFromZeroToOne) {
		double value = 0;
		for (int j = degreeValue - 1; j > -1; j--) {
			matrix[row][col + degreeValue - j - 1] = (float) Math.pow(
					currentValueFromZeroToOne, j);
		}
		return (float) value;
	}

	public final GeoCurveCartesian get(int index) {
		return (GeoCurveCartesian) curves.get(index);
	}

	public final int size() {
		return curves.size();
	}

	class SplineDistance implements RealRootFunction {

		private boolean isX;

		SplineDistance(boolean isX) {
			this.isX = isX;
		}

		public double evaluate(double x) {
			int i = findIndex(x);
			if (i != 0 && i != points.size()) {
				if (isX) {
					return ((GeoCurveCartesian) curves.get(i)).getFunX()
							.evaluate(x);
				}
				return ((GeoCurveCartesian) curves.get(i)).getFunY()
						.evaluate(x);
			}
			return Double.NaN;
		}

		private int findIndex(double x) {
			for (int i = 0; i < points.size(); i++) {
				boolean result;
				if (isX) {
					result = isGreatherX(x, i);
				} else {
					result = isGreatherY(x, i);
				}
				if (result) {
					return i;
				}
			}
			return points.size();
		}

		private boolean isGreatherX(double x, int i) {
			return ((GeoPoint) points.get(i)).getX() > x;
		}

		private boolean isGreatherY(double y, int i) {
			return ((GeoPoint) points.get(i)).getY() > y;
		}
	}

	public void translate(Coords v) {
		for (int i = 0; i < points.size(); i++) {
			((GeoPoint) points.get(i)).translate(v);
		}
		recalculate();
	}

	@Override
	public boolean isTranslateable() {
		return true;
	}

	public void rotate(NumberValue r) {
		for (int i = 0; i < points.size(); i++) {
			((GeoPoint) points.get(i)).rotate(r);
		}
		recalculate();
	}

	public void rotate(NumberValue r, GeoPoint S) {
		for (int i = 0; i < points.size(); i++) {
			((GeoPoint) points.get(i)).rotate(r, S);
		}
		recalculate();
	}

	public void mirror(GeoPoint Q) {
		for (int i = 0; i < points.size(); i++) {
			((GeoPoint) points.get(i)).mirror(Q);
		}
		recalculate();
	}

	public void mirror(GeoLine g) {
		for (int i = 0; i < points.size(); i++) {
			((GeoPoint) points.get(i)).mirror(g);
		}
		recalculate();
	}

	public void mirror(GeoConic c) {
		for (int i = 0; i < points.size(); i++) {
			((GeoPoint) points.get(i)).mirror(c);
		}
		recalculate();
	}

	public void matrixTransform(double a00, double a01, double a10, double a11) {
		for (int i = 0; i < points.size(); i++) {
			((GeoPoint) points.get(i)).matrixTransform(a00, a01, a10, a11);
		}
		recalculate();
	}

	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		for (int i = 0; i < points.size(); i++) {
			((GeoPoint) points.get(i)).matrixTransform(a00, a01, a02, a10, a11,
					a12, a20, a21, a22);
		}
		recalculate();
	}

	public void dilate(NumberValue r, GeoPoint S) {
		for (int i = 0; i < points.size(); i++) {
			((GeoPoint) points.get(i)).dilate(r, S);
		}
		recalculate();
	}

	
	public double[] newPoint(){
		return new double[2];
	}
}
