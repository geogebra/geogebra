package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;

import java.util.Arrays;

/**
 * Algorithm for cubic spline. Use the Gauss-Jordan method to solve the linear
 * system. The spline is parametric
 * 
 * @author Giuliano Bellucci
 * 
 */
public class AlgoCubicSpline extends AlgoElement {

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

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param inputList
	 *            list of points
	 */
	public AlgoCubicSpline(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;
		listC = new GeoList(cons);
		parametersValues = new float[inputList.size()];
		Arrays.fill(parametersValues, Float.MAX_VALUE);
		compute();
		setInputOutput(); 
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;
		super.setOutputLength(1);
		super.setOutput(0, listC);
		setDependencies(); 
	}

	@Override
	public void compute() {
		if (!inputList.isDefined()) {
			listC.setUndefined();
			return;
		}

		int size = inputList.size();

		points = new float[size][2];
		for (int i = 0; i < size; i++) {
			GeoPoint p = (GeoPoint) inputList.get(i);
			points[i][0] = (float) p.getX();
			points[i][1] = (float) p.getY();
		}
		parametersX = getSystemSolution(getLinearSystemParametric(0));
		parametersY = getSystemSolution(getLinearSystemParametric(1));
		execute();
	}

	/**
	 * @return list of parametric curves
	 */
	public GeoList getList() {
		return listC;
	}
	
	@Override
	public GetCommand getClassName() {
		return Commands.CubicSpline;
	}

	private static float[] getSystemSolution(float[][] matrix) {
		boolean nok = false;
		float[] solution = new float[matrix.length];
		float[] temp = new float[matrix[0].length];
		int column;
		int row;
		int i;
		int j;

		for (column = 0; column < matrix.length - 1; column++) {
			for (i = column; i < matrix.length - 1; i++) {
				for (j = i + 1; j < matrix.length; j++) {
					if (Math.abs(matrix[i][column]) < Math
							.abs(matrix[j][column])) {
						System.arraycopy(matrix[i], column, temp, column,
								matrix.length + 1 - column);
						System.arraycopy(matrix[j], column, matrix[i], column,
								matrix.length + 1 - column);
						System.arraycopy(temp, column, matrix[j], column,
								matrix.length + 1 - column);
					}
				}
			}

			for (row = column; row < matrix.length && matrix[row][column] == 0; row++)
				;
			float value;
			if (row != matrix.length - 1) {
				for (i = column; i < matrix.length; i++) {
					if (matrix[i][column] != 0 && i != row) {
						value = matrix[i][column] / matrix[row][column];
						for (j = column; j < matrix.length + 1; j++) {
							matrix[row][j] = matrix[row][j] * value;
							matrix[i][j] = matrix[i][j] - matrix[row][j];
						}
					}
				}
			}
		}
		j = 0;
		nok = true;
		for (; j < matrix.length && nok; j++) {
			if (matrix[matrix.length - 1][j] != 0) {
				nok = false;
			}
		}
		if (nok) {
			throw new RuntimeException("All zeroes in a row");
		}
		solution[solution.length - 1] = matrix[matrix.length - 1][matrix.length]
				/ matrix[matrix.length - 1][matrix.length - 1];
		float buffer;
		int ii;
		for (i = matrix.length - 2; i > -1; i--) {
			buffer = 0;
			for (ii = matrix.length - 1; ii > i; ii--) {
				buffer = buffer + solution[ii] * matrix[i][ii];
			}
			solution[i] = (matrix[i][matrix.length] - buffer) / matrix[i][i];
		}
		return solution;
	}

	

	private float[][] getLinearSystemParametric(int c) {

		int i = 0, k = 0, j, ii, jj;
		float currentValueFromZeroToOne;
		cumulativeValueOfParameter = new float[points.length];
		cumulativeValueOfParameter[0] = 0;
		for (ii = 1; ii < points.length; ii++) {
			cumulativeValueOfParameter[ii] = 0;
			for (jj = 1; jj <= ii; jj++) {
				cumulativeValueOfParameter[ii] = cumulativeValueOfParameter[ii]
						+ (float) Math.sqrt((points[jj][0] - points[jj - 1][0])
								* (points[jj][0] - points[jj - 1][0])
								+ (points[jj][1] - points[jj - 1][1])
								* (points[jj][1] - points[jj - 1][1]));
			}
		}
		float[][] matrix = new float[(points.length - 1) * 4][(points.length - 1) * 4 + 1];
		for (j = 0; j < points.length - 1; j++) {
			currentValueFromZeroToOne = cumulativeValueOfParameter[j]
					/ cumulativeValueOfParameter[cumulativeValueOfParameter.length - 1];
			matrix[i][k] = currentValueFromZeroToOne
					* currentValueFromZeroToOne * currentValueFromZeroToOne;
			matrix[i][k + 1] = currentValueFromZeroToOne
					* currentValueFromZeroToOne;
			matrix[i][k + 2] = currentValueFromZeroToOne;
			matrix[i][k + 3] = 1;
			matrix[i][matrix.length] = points[j][c];
			i++;
			k += 4;

		}
		k = 0;
		for (j = 1; j < points.length; j++) {
			currentValueFromZeroToOne = cumulativeValueOfParameter[j]
					/ cumulativeValueOfParameter[cumulativeValueOfParameter.length - 1];
			matrix[i][k] = currentValueFromZeroToOne
					* currentValueFromZeroToOne * currentValueFromZeroToOne;
			matrix[i][k + 1] = currentValueFromZeroToOne
					* currentValueFromZeroToOne;
			matrix[i][k + 2] = currentValueFromZeroToOne;
			matrix[i][k + 3] = 1;
			matrix[i][matrix.length] = points[j][c];
			i++;
			k += 4;

		}
		k = 0;
		for (j = 1; j < points.length - 1; j++) {
			currentValueFromZeroToOne = cumulativeValueOfParameter[j]
					/ cumulativeValueOfParameter[cumulativeValueOfParameter.length - 1];
			matrix[i][k] = currentValueFromZeroToOne
					* currentValueFromZeroToOne * 3;
			matrix[i][k + 1] = currentValueFromZeroToOne * 2;
			matrix[i][k + 2] = 1;
			matrix[i][k + 3] = 0;
			matrix[i][k + 4] = -matrix[i][k];
			matrix[i][k + 5] = -matrix[i][k + 1];
			matrix[i][k + 6] = -1;
			matrix[i][k + 7] = 0;
			matrix[i][matrix.length] = 0;
			i++;
			k += 4;

		}

		k = 0;
		for (j = 1; j < points.length - 1; j++) {
			currentValueFromZeroToOne = cumulativeValueOfParameter[j]
					/ cumulativeValueOfParameter[cumulativeValueOfParameter.length - 1];
			matrix[i][k] = currentValueFromZeroToOne * 6;
			matrix[i][k + 1] = 2;
			matrix[i][k + 2] = 0;
			matrix[i][k + 3] = 0;
			matrix[i][k + 4] = -matrix[i][k];
			matrix[i][k + 5] = -2;
			matrix[i][k + 6] = 0;
			matrix[i][k + 7] = 0;
			matrix[i][matrix.length] = 0;
			i++;
			k += 4;

		}
		matrix[i][0] = 0;
		matrix[i][1] = 2;
		i++;
		matrix[i][matrix.length - 4] = 6;
		matrix[i][matrix.length - 3] = 2;
		return matrix;

	}

	private float[] getParameterIntervalLimits() {
		parameterIntervalLimits = new float[cumulativeValueOfParameter.length];
		parameterIntervalLimits[0] = 0;
		for (int i = 1; i < cumulativeValueOfParameter.length; i++) {
			parameterIntervalLimits[i] = cumulativeValueOfParameter[i]
					/ cumulativeValueOfParameter[cumulativeValueOfParameter.length - 1];
		}
		return parameterIntervalLimits;
	}

	private void calculateParameterValues() {
		int j = 0;
		float parameterValue = 0;
		float[] lx = getParameterIntervalLimits();
		for (float p = 0; p <= 1; p = p + 0.001f) {
			parameterValue = calculate(p, lx);
			if (Arrays.binarySearch(parametersValues, parameterValue) < 0) {
				if (j<parametersValues.length){
					parametersValues[j] = parameterValue;
					j++;
				}
			}
		}
		parametersValues[parametersValues.length - 1] = 1;
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
		listC.clear();
		calculateParameterValues();
		int k = 0;
		for (int i = 0; i < parametersX.length; i += 4) {
			AlgebraProcessor ap = kernel.getAlgebraProcessor();
			GeoFunction fx = ap.evaluateToFunction("splineX(t)=" + parametersX[i] + "*t^3+"
					+ parametersX[i + 1] + "*t^2+" + parametersX[i + 2] + "*t+" + parametersX[i + 3], true);
			GeoFunction fy = ap.evaluateToFunction("splineY(t)=" + parametersY[i] + "*t^3+"
					+ parametersY[i + 1] + "*t^2+" + parametersY[i + 2] + "*t+" + parametersY[i + 3], true);
			GeoCurveCartesian curve = new GeoCurveCartesian(cons);
			curve.setFunctionX(fx.getFunction());
			curve.setFunctionY(fy.getFunction());
			curve.setInterval(parametersValues[k], parametersValues[k + 1]);
			listC.add(curve);
			k++;
		}
	}
}
