package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.GeoNumeric;
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
	private GeoNumberValue degree;
	private int degreeValue;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param inputList
	 *            list of points
	 */
	public AlgoSpline(Construction cons, String label, GeoList inputList) {
		this(cons,label,inputList,new GeoNumeric(cons,3));
	}
	
	public AlgoSpline(Construction cons, String label, GeoList inputList,GeoNumberValue degree) {
		super(cons);
		this.degree = degree;
		this.inputList = inputList;
		listC = new GeoList(cons);
		spline = new GeoSpline(cons);
		spline.setAllVisualProperties(listC, true);
		listC.setAlgebraVisible(false);
		listC = null;
		parametersValues = new float[inputList.size()];
		points = new float[inputList.size()][2];
		compute();
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
	public void compute() {
		if (!inputList.isDefined()) {
			spline.setUndefined();
			return;
		}
		degreeValue=(int)degree.getDouble()+1;
		
		length = points.length;
		
		if (degreeValue<4 || degreeValue>length+1){
			spline.setUndefined();
			return;
		}
			
		for (int i = 0; i < length; i++) {
			GeoPoint p = (GeoPoint) inputList.get(i);
			points[i][0] = (float) p.getX();
			points[i][1] = (float) p.getY();
		}
		parametersX = getSystemSolution(getLinearSystemParametric(0));
		parametersY = getSystemSolution(getLinearSystemParametric(1));
		if (parametersX==null || parametersY==null){
			return;
		}
		execute();
		setInputOutput();
	}

	public GeoSpline getSpline() {
		return spline;
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Spline;
	}

	private float[] getSystemSolution(float[][] matrix) {
		boolean nok = false;
		spline.setDefined(true);
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
		float[][] matrix = new float[(length - 1) * degreeValue][(length - 1) * degreeValue + 1];
		row = 0;
		col = 0;
		for (pointIndex = 0; pointIndex < length - 1; pointIndex++) {
			currentValueFromZeroToOne = cumulativeValueOfParameter[pointIndex]
					/ cumulativeValueOfParameter[length - 1];
			evalForPoint(matrix, row, col, currentValueFromZeroToOne);
			matrix[row][matrix.length] = points[pointIndex][c];
			row++;
			col += degreeValue;

		}
		col = 0;
		for (pointIndex = 1; pointIndex < length; pointIndex++) {
			currentValueFromZeroToOne = cumulativeValueOfParameter[pointIndex]
					/ cumulativeValueOfParameter[length - 1];
			evalForPoint(matrix, row, col, currentValueFromZeroToOne);
			matrix[row][matrix.length] = points[pointIndex][c];
			row++;
			col += degreeValue;

		}
		
		for (int currentDerivative=degreeValue-2;currentDerivative>0;currentDerivative--){
			col=0;
			for (pointIndex = 1; pointIndex < length - 1; pointIndex++) {
				currentValueFromZeroToOne = cumulativeValueOfParameter[pointIndex]
						/ cumulativeValueOfParameter[length - 1];
				calcDerivative(matrix[row],col,currentDerivative,currentValueFromZeroToOne);
				row++;
				col+=degreeValue;
			}
		}
		matrix[row][0] = 0;
		matrix[row][1] = fact(degreeValue-2);
		row++;
		matrix[row][matrix.length - degreeValue] = fact(degreeValue-1);
		matrix[row][matrix.length - degreeValue+1] = fact(degreeValue-2);
		
		row++;
		int num=2;
		for (;row<matrix.length;row++){
			matrix[row][matrix.length - num*degreeValue] = fact(degreeValue-1)* cumulativeValueOfParameter[num-1]
					/ cumulativeValueOfParameter[length - 1];
			matrix[row][matrix.length - num*degreeValue+1] = fact(degreeValue-2);
			num++;
		}
		
		
		return matrix;

	}

	private static float fact(int i) {
		int f=1;
		for (int j=2;j<=i;j++ ){
			f*=j;
		}
		return f;
	}

	private void calcDerivative(float[] row, int col,
			int currentDerivative, float currentValueFromZeroToOne) {
		for (int i=col;i<col+degreeValue;i++){
			row[i]=calcCoeff(i,currentDerivative,currentValueFromZeroToOne);
			row[i+degreeValue]=-row[i];
		}
	}

	private float calcCoeff(int col, int currentDerivative,
			float currentValueFromZeroToOne) {
		int exp=col%degreeValue;
		exp=degreeValue-exp-1;
		float coeff=(float)Math.pow(currentValueFromZeroToOne,exp-1);
		if (exp==0){
			return 0;
		}
		for (int i=degreeValue-1;i>currentDerivative;i--){
			coeff*=exp;
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

	public GeoNumberValue getDegree() {
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
		for (int i = 0; i < length; i += degreeValue) {
			AlgebraProcessor ap = kernel.getAlgebraProcessor();
			StringBuilder sbX = new StringBuilder("splineX(t)=");
			StringBuilder sbY = new StringBuilder("splineY(t)=");
			for (int j = degreeValue - 1; j > -1; j--) {
				sbX.append(parametersX[i + degreeValue - 1 - j] + "*t^" + j + "+");
				sbY.append(parametersY[i + degreeValue - 1 - j] + "*t^" + j + "+");
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
			curve.setObjColor(spline.getObjectColor());
			spline.add(curve);
			k++;
		}
	}
}
