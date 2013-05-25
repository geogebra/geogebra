package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSpline;

import java.util.Arrays;

/**
 * Algorithm for cubic spline. Use the Gauss-Jordan method to solve the linear
 * system. The spline is parametric
 * 
 * @author Giuliano Bellucci
 * 
 */
public class AlgoSpline extends AlgoElement {

	/**
	 * list of points
	 */
	GeoList inputList;

	private float[] cumulativeValueOfParameter;
	private float[] parametersX;
	private float[] parametersY;
	private float[][] points;
	private float[] parameterIntervalLimits;
	private float[] parametersValues;
	private GeoList listC;
	private GeoSpline spline;
	private static int length;
	private int degree;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param inputList
	 *            list of points
	 */
	public AlgoSpline(Construction cons, String label, GeoList inputList) {
		this(cons,label,inputList,4);
	}
	
	public AlgoSpline(Construction cons, String label, GeoList inputList,int grade) {
		super(cons);
		this.degree = grade;
		this.inputList = inputList;
		listC = new GeoList(cons);
		spline = new GeoSpline(cons);
		spline.setAllVisualProperties(listC, true);
		listC.setAlgebraVisible(false);
		listC = null;
		parametersValues = new float[inputList.size()];
		points = new float[inputList.size()][2];
		setInputOutput();
		compute();
		spline.setLabel(label);
	}
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;
		super.setOutputLength(1);
		super.setOutput(0, spline);
		setDependencies();
	}

	@Override
	public void compute() {
		if (!inputList.isDefined()) {
			spline.setUndefined();
			return;
		}

		length = points.length;

		for (int i = 0; i < length; i++) {
			GeoPoint p = (GeoPoint) inputList.get(i);
			points[i][0] = (float) p.getX();
			points[i][1] = (float) p.getY();
		}
		parametersX = getSystemSolution(getLinearSystemParametric(0));
		parametersY = getSystemSolution(getLinearSystemParametric(1));
		execute();
	}

	public GeoSpline getSpline() {
		return spline;
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Spline;
	}

	private static float[] getSystemSolution(float[][] matrix) {
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
			throw new RuntimeException("All zeroes in a row");
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
		length = points.length;
		cumulativeValueOfParameter = new float[length];
		for (col = 1; col < length; col++) {
			for (row = 1; row <= col; row++) {
				cumulativeValueOfParameter[col] = cumulativeValueOfParameter[col]
						+ (float) Math
								.sqrt((points[row][0] - points[row - 1][0])
										* (points[row][0] - points[row - 1][0])
										+ (points[row][1] - points[row - 1][1])
										* (points[row][1] - points[row - 1][1]));
			}
		}
		float[][] matrix = new float[(length - 1) * degree][(length - 1) * degree + 1];
		row = 0;
		col = 0;
		for (pointIndex = 0; pointIndex < length - 1; pointIndex++) {
			currentValueFromZeroToOne = cumulativeValueOfParameter[pointIndex]
					/ cumulativeValueOfParameter[length - 1];
			evalForPoint(matrix, row, col, currentValueFromZeroToOne);
			matrix[row][matrix.length] = points[pointIndex][c];
			row++;
			col += degree;

		}
		col = 0;
		for (pointIndex = 1; pointIndex < length; pointIndex++) {
			currentValueFromZeroToOne = cumulativeValueOfParameter[pointIndex]
					/ cumulativeValueOfParameter[length - 1];
			evalForPoint(matrix, row, col, currentValueFromZeroToOne);
			matrix[row][matrix.length] = points[pointIndex][c];
			row++;
			col += degree;

		}
		
		for (int currentDerivative=degree-2;currentDerivative>0;currentDerivative--){
			col=0;
			for (pointIndex = 1; pointIndex < length - 1; pointIndex++) {
				currentValueFromZeroToOne = cumulativeValueOfParameter[pointIndex]
						/ cumulativeValueOfParameter[length - 1];
				calcDerivative(matrix[row],col,currentDerivative,currentValueFromZeroToOne);
				row++;
				col+=degree;
			}
		}
		
		/*col = 0;
		for (pointIndex = 1; pointIndex < length - 1; pointIndex++) {
			currentValueFromZeroToOne = cumulativeValueOfParameter[pointIndex]
					/ cumulativeValueOfParameter[length - 1];
			matrix[row][col] = currentValueFromZeroToOne
					* currentValueFromZeroToOne * 3;
			matrix[row][col + 1] = currentValueFromZeroToOne * 2;
			matrix[row][col + 2] = 1;
			matrix[row][col + 3] = 0;
			matrix[row][col + 4] = -matrix[row][col];
			matrix[row][col + 5] = -matrix[row][col + 1];
			matrix[row][col + 6] = -1;
			matrix[row][col + 7] = 0;
			matrix[row][matrix.length] = 0;
			row++;
			col += degree;

		}

		col = 0;
		for (pointIndex = 1; pointIndex < length - 1; pointIndex++) {
			currentValueFromZeroToOne = cumulativeValueOfParameter[pointIndex]
					/ cumulativeValueOfParameter[length - 1];
			matrix[row][col] = currentValueFromZeroToOne * 6;
			matrix[row][col + 1] = 2;
			matrix[row][col + 2] = 0;
			matrix[row][col + 3] = 0;
			matrix[row][col + 4] = -matrix[row][col];
			matrix[row][col + 5] = -2;
			matrix[row][col + 6] = 0;
			matrix[row][col + 7] = 0;
			matrix[row][matrix.length] = 0;
			row++;
			col += degree;

		}*/
		matrix[row][0] = 0;
		matrix[row][1] = fact(degree-2);
		row++;
		matrix[row][matrix.length - degree] = fact(degree-1);
		matrix[row][matrix.length - degree+1] = fact(degree-2);
		
		row++;
		int num=2;
		for (;row<matrix.length;row++){
			matrix[row][matrix.length - num*degree] = fact(degree-1)* cumulativeValueOfParameter[num-1]
					/ cumulativeValueOfParameter[length - 1];
			matrix[row][matrix.length - num*degree+1] = fact(degree-2);
			num++;
		}
		
		
		return matrix;

	}

	private float fact(int i) {
		int f=1;
		for (int j=2;j<=i;j++ ){
			f*=j;
		}
		return f;
	}

	private void calcDerivative(float[] row, int col,
			int currentDerivative, float currentValueFromZeroToOne) {
		for (int i=col;i<col+degree;i++){
			row[i]=calcCoeff(i,currentDerivative,currentValueFromZeroToOne);
			row[i+degree]=-row[i];
		}
	}

	private float calcCoeff(int col, int currentDerivative,
			float currentValueFromZeroToOne) {
		int exp=col%degree;
		exp=degree-exp-1;
		float coeff=(float)Math.pow(currentValueFromZeroToOne,exp-1);
		if (exp==0){
			return 0;
		}
		for (int i=degree-1;i>currentDerivative;i--){
			coeff*=exp;
			exp--;
		}
		return coeff;
	}

	private float evalForPoint(float[][] matrix, int row, int col,
			float currentValueFromZeroToOne) {
		double value = 0;
		for (int j = degree - 1; j > -1; j--) {
			matrix[row][col + degree - j - 1] = (float) Math.pow(
					currentValueFromZeroToOne, j);
		}
		return (float) value;
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

	public float[] getParametersX() {
		return parametersX;
	}

	public float[] getParametersY() {
		return parametersY;
	}

	public float[][] getPoints() {
		return points;
	}

	public int getDegree() {
		return degree;
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

	private void execute() {
		Arrays.fill(parametersValues, Float.MAX_VALUE);
		spline.clear();
		calculateParameterValues();
		int k = 0;
		length = parametersX.length;
		for (int i = 0; i < length; i += degree) {
			AlgebraProcessor ap = kernel.getAlgebraProcessor();
			StringBuilder sbX = new StringBuilder("splineX(t)=");
			StringBuilder sbY = new StringBuilder("splineY(t)=");
			for (int j = degree - 1; j > -1; j--) {
				sbX.append(parametersX[i + degree - 1 - j] + "*t^" + j + "+");
				sbY.append(parametersY[i + degree - 1 - j] + "*t^" + j + "+");
			}
			GeoFunction fx = ap.evaluateToFunction(
					sbX.substring(0, sbX.length() - 1), true);
			GeoFunction fy = ap.evaluateToFunction(
					sbY.substring(0, sbY.length() - 1), true);
			/*
			 * GeoFunction fx = ap.evaluateToFunction("splineX(t)=" +
			 * parametersX[i] + "*t^3+" + parametersX[i + 1] + "*t^2+" +
			 * parametersX[i + 2] + "*t+" + parametersX[i + 3], true);
			 * GeoFunction fy = ap.evaluateToFunction("splineY(t)=" +
			 * parametersY[i] + "*t^3+" + parametersY[i + 1] + "*t^2+" +
			 * parametersY[i + 2] + "*t+" + parametersY[i + 3], true);
			 */
			GeoCurveCartesian curve = new GeoCurveCartesian(cons);
			curve.setFunctionX(fx.getFunction());
			curve.setFunctionY(fy.getFunction());
			curve.setInterval(parametersValues[k], parametersValues[k + 1]);
			curve.setEuclidianVisible(true);
			curve.setObjColor(spline.getObjectColor());
			spline.add(curve);
			k++;
		}
	}
}
