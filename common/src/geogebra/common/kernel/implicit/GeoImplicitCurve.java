package geogebra.common.kernel.implicit;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.EuclidianViewCE;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.main.App;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.Operation;

/**
 * GeoElement representing an implicit curve.
 * 
 */
public class GeoImplicitCurve extends GeoElement implements EuclidianViewCE {

	private FunctionNVar expression;
	private GeoLocus locus;

	private int gridWidth;
	private int gridHeight;

	private GeoImplicitCurve(Construction c) {
		super(c);
		locus = new GeoLocus(c);
		locus.setDefined(true);
		c.registerEuclidianViewCE(this);
	}

	/**
	 * Constructs an implicit curve object with given equation
	 * containing variables as x and y.
	 * @param c construction
	 * @param label label
	 * @param equation equation of the implicit curve
	 */
	public GeoImplicitCurve(Construction c, String label, Equation equation) {
		this(c);
		setLabel(label);
		fromEquation(equation);
		updatePath();
	}

	/**
	 * Constructs and implicit curve with given function in x and y.
	 * @param c construction
	 * @param label label
	 * @param function function defining the implicit curve
	 */
	public GeoImplicitCurve(Construction c, String label, FunctionNVar function) {
		this(c);
		setLabel(label);
		fromFunction(function);
		updatePath();
	}

	private void fromEquation(Equation equation) {
		ExpressionNode leftHandSide = equation.getLHS();
		ExpressionNode rightHandSide = equation.getRHS();

		ExpressionNode functionExpression = new ExpressionNode(kernel,
				leftHandSide, Operation.MINUS, rightHandSide);
		expression = new FunctionNVar(functionExpression,
				new FunctionVariable[] { new FunctionVariable(kernel, "x"),
						new FunctionVariable(kernel, "y") });
	}

	private void fromFunction(FunctionNVar function) {
		expression = function;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.IMPLICIT_CURVE;
	}

	@Override
	public GeoElement copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDefined() {
		return expression != null;
	}

	@Override
	public void setUndefined() {
		// TODO Auto-generated method stub

	}

	@Override
	public String toValueString(StringTemplate tpl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
	}

	@Override
	public boolean isEqual(GeoElement geo) {
		// TODO Auto-generated method stub
		return false;
	}

	private double[] evalArray = new double[2];

	/**
	 * @param x function variable x
	 * @param y function variable y
	 * @return the value of the function
	 */
	public double evaluateImplicitCurve(double x, double y) {
		evalArray[0] = x;
		evalArray[1] = y;
		return evaluateImplicitCurve(evalArray);
	}

	/**
	 * @param values function variables ({x, y})
	 * @return the value of the function
	 */
	public double evaluateImplicitCurve(double[] values) {
		return expression.evaluate(values);
	}

	/**
	 * @return Locus representing this curve
	 */
	public GeoLocus getLocus() {
		return locus;
	}

	/**
	 * Updates the path of the curve.
	 */
	public void updatePath() {
		double[] viewBounds = kernel.getViewBoundsForGeo(this);
		if (viewBounds[0] == Double.POSITIVE_INFINITY) { // no active View
			viewBounds = new double[] { -10, 10, -10, 10, 10, 10 }; // get some
																	// value...
		}
		// increase grid size for big screen, #1563
		gridWidth = 30;
		gridHeight = 30;
		updatePath(viewBounds[0], viewBounds[3], viewBounds[1] - viewBounds[0],
				viewBounds[3] - viewBounds[2], viewBounds[4], viewBounds[5]);
	}

	private double[][] grid;
	private boolean[][] evald;

	/**
	 * @param rectX top of the view
	 * @param rectY left of the view
	 * @param rectW width of the view
	 * @param rectH height of the view
	 * @param xScale x-scale of the view
	 * @param yScale y-scale of the view
	 */
	public void updatePath(double rectX, double rectY, double rectW,
			double rectH, double xScale, double yScale) {
		App.debug(rectX + "x" + rectY + "," + rectW + "," + rectH);
		App.debug(gridWidth + "x" + gridHeight);
		App.debug("res" + xScale + " " + yScale);

		grid = new double[gridHeight][gridWidth];
		evald = new boolean[gridHeight - 1][gridWidth - 1];
		for (int j = 0; j < gridWidth; j++) {
			grid[0][j] = evaluateImplicitCurve(getRealWorldCoordinates(0, j,
					rectX, rectY, rectH, rectW));
			evald[0][j] = false;
		}

		for (int i = 1; i < gridHeight; i++) {
			grid[i][0] = evaluateImplicitCurve(getRealWorldCoordinates(i, 0,
					rectX, rectY, rectH, rectW));
			evald[i][0] = false;
		}

		for (int i = 1; i < gridHeight; i++) {
			for (int j = 1; j < gridWidth; j++) {
				grid[i][j] = evaluateImplicitCurve(getRealWorldCoordinates(i,
						j, rectX, rectY, rectH, rectW));
				evald[i][j] = false;
			}
		}

		int i = 0;
		int j = -1;
		while (true) {
			if (j >= gridWidth - 1) {
				j = 0;
				i++;
			} else {
				j++;
			}
			if (i >= gridHeight - 1) {
				break;
			}
			if (evald[i][j]) {
				continue;
			}

		}
	}

	private double getRealWorldX(int i, double rectX, double rectH) {
		return rectX + i * (rectH / gridHeight);
	}

	private double getRealWorldY(int j, double rectY, double rectW) {
		return rectY + j * (rectW / gridWidth);
	}

	private double[] rwCoords = new double[2];

	private double[] getRealWorldCoordinates(int i, int j, double rectX,
			double rectY, double rectH, double rectW) {
		rwCoords[0] = getRealWorldX(i, rectX, rectH);
		rwCoords[1] = getRealWorldY(j, rectY, rectW);
		return rwCoords;
	}

	public boolean euclidianViewUpdate() {
		if (isDefined()) {
			updatePath();
			return true;
		}
		return false;
	}

}
