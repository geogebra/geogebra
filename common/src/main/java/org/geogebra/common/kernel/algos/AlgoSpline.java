package org.geogebra.common.kernel.algos;

import java.util.Arrays;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.Operation;

/**
 * Algorithm for spline.
 * 
 * @author Giuliano Bellucci
 * 
 */
public class AlgoSpline extends AlgoElement {

	/**
	 * list of points
	 */
	private GeoList inputList;
	private GeoCurveCartesianND spline;
	private GeoNumberValue degree;
	private float[][] floatPoints;
	private float[][] parameters;
	private int length;
	private float[] cumulativeValueOfParameter;
	private int degreeValue;
	private float[] parametersValues;
	private float[] parameterIntervalLimits;
	private int dimension = 2;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param inputList
	 *            list of points
	 */
	public AlgoSpline(Construction cons, String label, GeoList inputList) {
		this(cons, label, inputList, new GeoNumeric(cons, 3));
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param inputList
	 *            list of points
	 * @param degree
	 *            grade of polynoms
	 */
	public AlgoSpline(final Construction cons, final String label,
			final GeoList inputList, final GeoNumberValue degree) {
		super(cons);
		this.degree = degree;
		this.inputList = inputList;
		for (int i = 0; i < inputList.size() && dimension == 2; i++) {
			GeoPointND p = (GeoPointND) inputList.get(i);
			dimension = p.getDimension();
		}
		parameters = new float[dimension][];
		floatPoints = new float[inputList.size()][dimension];
		if (dimension == 3) {
			spline = new GeoCurveCartesian3D(cons);
		} else {
			spline = new GeoCurveCartesian(cons);
		}
		spline.setEuclidianVisible(true);
		parametersValues = new float[inputList.size()];
		compute();
		setInputOutput();
		spline.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = inputList;
		input[1] = degree.toGeoElement();
		super.setOutputLength(1);
		super.setOutput(0, spline);
		setDependencies();
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Spline;
	}

	/**
	 * @return spline
	 */
	public GeoCurveCartesianND getSpline() {
		return spline;
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

	public float[] getParameterIntervalLimits() {
		length = cumulativeValueOfParameter.length;
		parameterIntervalLimits = new float[length];
		for (int i = 1; i < length; i++) {
			parameterIntervalLimits[i] = cumulativeValueOfParameter[i]
					/ cumulativeValueOfParameter[cumulativeValueOfParameter.length - 1];
		}
		return parameterIntervalLimits;
	}

	public void compute() {
		if (!this.inputList.isDefined()) {
			spline.setUndefined();
			return;
		}

		degreeValue = (int) degree.getDouble() + 1;

		if (degreeValue < 4 || degreeValue > floatPoints.length + 1) {
			spline.setUndefined();
			return;
		}
		int i = 0;
		for (; i < floatPoints.length; i++) {
			GeoPointND p = (GeoPointND) this.inputList.get(i);
			for (int j = 0; j < dimension; j++) {
				floatPoints[i][j] = (float) p.getInhomCoordsInD(dimension).get(
						j + 1);
			}
		}
		for (i = 0; i < dimension; i++) {
			parameters[i] = getSystemSolution(getLinearSystemParametric(i));
		}
		for (i = 0; i < dimension; i++)
			if (parameters[i] == null) {
				return;
			}

		FunctionVariable fv = new FunctionVariable(this.kernel, "t");
		MyList[] alt = new MyList[dimension];
		ExpressionNode[] nodes = new ExpressionNode[dimension];
		for (i = 0; i < dimension; i++) {
			alt[i] = new MyList(kernel);
		}
		MyList cond = new MyList(kernel);
		calculateParameterValues();
		int t = 1;

		for (int k = 0; k < parameters[0].length; k += this.degreeValue) {
			for (i = 0; i < dimension; i++) {
				nodes[i] = new ExpressionNode(kernel, 0);
			}
			for (int j = degreeValue - 1; j > -1; j--) {
				for (i = 0; i < dimension; i++) {
					nodes[i] = nodes[i].plus(new ExpressionNode(kernel,
							parameters[i][k + degreeValue - 1 - j])
							.multiplyR(fv.wrap().power(j)));
				}
			}
			for (i = 0; i < dimension; i++) {
				alt[i].addListElement(nodes[i]);
			}
			if (t < this.parameterIntervalLimits.length) {
				cond.addListElement(fv.wrap().lessThan(
						this.parameterIntervalLimits[t++]));
			}
		}

		Function[] functions = new Function[dimension];
		for (i = 0; i < dimension; i++) {
			functions[i] = new Function(new ExpressionNode(kernel, cond,
					Operation.IF_LIST, alt[i]), fv);
		}
		this.spline.setFun(functions);
		this.spline.setInterval(0, 1);
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

			for (row = column; row < length && matrix[row][column] == 0; row++) {
				// do nothing
			}
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
			spline.setUndefined();
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
		int i;
		for (col = 1; col < length; col++) {
			for (row = 1; row <= col; row++) {
				float value = 0;
				for (i = 0; i < dimension; i++) {
					value += (floatPoints[row][i] - floatPoints[row - 1][i])
							* (floatPoints[row][i] - floatPoints[row - 1][i]);
				}
				cumulativeValueOfParameter[col] = cumulativeValueOfParameter[col]
						+ (float) Math.sqrt(value);
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
		if (inputList.get(0).equals(inputList.get(inputList.size() - 1))) {
			for (int currentDerivative = degreeValue - 2; currentDerivative > 0; currentDerivative--) {
				col = 0;
				calcExtremesDerivative(matrix[row], col, currentDerivative);
				row++;
				col += degreeValue;
			}
		} else {
			matrix[row][0] = 0;
			matrix[row][1] = fact(degreeValue - 2);
			row++;
			matrix[row][matrix.length - degreeValue] = fact(degreeValue - 1);
			matrix[row][matrix.length - degreeValue + 1] = fact(degreeValue - 2);
		}
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

	private void calcExtremesDerivative(float[] row, int col,
			int currentDerivative) {
		for (int i = col; i < col + degreeValue; i++) {
			row[i] = calcCoeff(i, currentDerivative, 0);
			row[row.length - 1 - degreeValue + i] = -calcCoeff(i,
					currentDerivative, 1);
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
}
