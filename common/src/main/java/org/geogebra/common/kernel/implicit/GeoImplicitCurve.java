package org.geogebra.common.kernel.implicit;

import java.util.List;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EuclidianViewCE;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;

/**
 * GeoElement representing an implicit curve.
 * 
 */
public class GeoImplicitCurve extends GeoElement implements EuclidianViewCE,
		Traceable, Path {

	private FunctionNVar expression;
	private GeoLocus locus;

	private int gridWidth;
	private int gridHeight;
	private Equation equation;
	private boolean defined = true;
	private static final int INVALID = 5;

	GeoImplicitCurve(Construction c) {
		super(c);
		locus = new GeoLocus(c);
		locus.setDefined(true);
		cons.removeFromConstructionList(locus);
		c.registerEuclidianViewCE(this);
	}

	/**
	 * Constructs an implicit curve object with given equation containing
	 * variables as x and y.
	 * 
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 * @param equation
	 *            equation of the implicit curve
	 */
	public GeoImplicitCurve(Construction c, String label, Equation equation) {
		this(c);

		fromEquation(equation);
		updatePath();
		setLabel(label);
	}

	public GeoImplicitCurve(Construction c, Equation equation) {
		this(c);

		fromEquation(equation);
		updatePath();
	}

	/**
	 * Constructs and implicit curve with given function in x and y.
	 * 
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 * @param function
	 *            function defining the implicit curve
	 */
	/*
	 * public GeoImplicitCurve(Construction c, String label, FunctionNVar
	 * function) { this(c);
	 * 
	 * fromFunction(function); updatePath(); setLabel(label); }
	 */

	private void fromEquation(Equation equation) {
		this.equation = equation;
		ExpressionNode leftHandSide = equation.getLHS();
		ExpressionNode rightHandSide = equation.getRHS();

		ExpressionNode functionExpression = new ExpressionNode(kernel,
				leftHandSide, Operation.MINUS, rightHandSide);
		FunctionVariable x = new FunctionVariable(kernel, "x");
		FunctionVariable y = new FunctionVariable(kernel, "y");
		VariableReplacer repl = VariableReplacer.getReplacer();
		VariableReplacer.addVars("x", x);
		VariableReplacer.addVars("y", y);
		functionExpression.traverse(repl);
				
		expression = new FunctionNVar(functionExpression,
				new FunctionVariable[] { x,
						y });
	}

	/*
	 * private void fromFunction(FunctionNVar function) { expression = function;
	 * }
	 */
	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.IMPLICIT_CURVE;
	}

	@Override
	public GeoElement copy() {
		GeoImplicitCurve curve = new GeoImplicitCurve(cons);
		curve.set(this);
		return curve;
	}

	@Override
	public void set(GeoElement geo) {
		Object equationCopy = ((GeoImplicitCurve) geo).equation
				.deepCopy(kernel);
		fromEquation((Equation) equationCopy);

	}

	@Override
	public boolean isDefined() {
		return defined && expression != null;
	}

	@Override
	public void setUndefined() {
		defined = false;

	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return equation.toValueString(tpl);
	}

	@Override
	public String toString(StringTemplate tpl) {
		return label + ": " + toValueString(tpl);
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
	 * @param x
	 *            function variable x
	 * @param y
	 *            function variable y
	 * @return the value of the function
	 */
	public double evaluateImplicitCurve(double x, double y) {
		evalArray[0] = x;
		evalArray[1] = y;
		return evaluateImplicitCurve(evalArray);
	}

	/**
	 * @param values
	 *            function variables ({x, y})
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
		gridWidth = 120;
		gridHeight = 120;
		updatePath(viewBounds[0], viewBounds[3], viewBounds[1] - viewBounds[0],
				viewBounds[3] - viewBounds[2], viewBounds[4], viewBounds[5]);
	}

	private double[][] grid;
	private boolean[][] evald;

	private double rectX;
	private double rectY;
	private double rectW;
	private double rectH;

	/**
	 * @param rectX
	 *            top of the view
	 * @param rectY
	 *            left of the view
	 * @param rectW
	 *            width of the view
	 * @param rectH
	 *            height of the view
	 * @param xScale
	 *            x-scale of the view
	 * @param yScale
	 *            y-scale of the view
	 */
	public void updatePath(double rectX, double rectY, double rectW,
			double rectH, double xScale, double yScale) {
		this.rectX = rectX;
		this.rectY = rectY;
		this.rectW = rectW * 1.1;
		this.rectH = rectH * 1.1;
		App.debug(rectX + "x" + rectY + "," + rectW + "," + rectH);
		App.debug(gridWidth + "x" + gridHeight);
		App.debug("res" + xScale + " " + yScale);

		grid = new double[gridHeight][gridWidth];
		evald = new boolean[gridHeight - 1][gridWidth - 1];

		for (int i = 0; i < gridHeight; i++) {
			for (int j = 0; j < gridWidth; j++) {
				grid[i][j] = evaluateImplicitCurve(getRealWorldCoordinates(i, j));
			}
		}
		List<MyPoint> locusPoints = locus.getPoints();
		locusPoints.clear();
		// check the squares marching algorithm
		int i = 0;
		int j = -1;
		while (true) {
			if (j >= gridWidth - 2) {
				j = 0;
				i++;
			} else {
				j++;
			}
			if (i >= gridHeight - 2) {
				break;
			}
			
			MyPoint[] ps = getPointsForGrid(i, j);
			if (ps != null) {
				for (MyPoint p : ps) {
					locusPoints.add(p);
				}
			}
		}
	}

	private MyPoint[] getPointsForGridX(int i, int j) {
		MyPoint P, Q;
		double[] A = getRealWorldCoordinates(i, j);
		double[] C = getRealWorldCoordinates(i + 1, j + 1);
		P = new MyPoint(A[0], A[1], false);
		Q = new MyPoint(C[0], C[1], true);
		return new MyPoint[] { P, Q };
	}

	private MyPoint[] getPointsForGrid(int i, int j) {
		GridTypeEnum gridType = getGridType(i, j);
		MyPoint P, Q;
		double qx, qy, px, py;
		double[] A = getRealWorldCoordinates(i, j);
		double[] B = getRealWorldCoordinates(i, j + 1);
		double[] C = getRealWorldCoordinates(i + 1, j + 1);
		double[] D = getRealWorldCoordinates(i + 1, j);
		switch (gridType) {
		case T1000:
			qx = A[0];
			py = A[1];
			qy = lin(A, D, 1);
			px = lin(A, B, 0);
			P = new MyPoint(px, py, false);
			Q = new MyPoint(qx, qy, true);
			return new MyPoint[] {P, Q};
		case T0100:
			qx = B[0];
			py = B[1];
			qy = lin(C, B, 1);
			px = lin(A, B, 0);
			P = new MyPoint(px, py, false);
			Q = new MyPoint(qx, qy, true);
			return new MyPoint[] {P, Q};
		case T0010:
			qx = B[0];
			py = C[1];
			qy = lin(B, C, 1);
			px = lin(C, D, 0);
			P = new MyPoint(px, py, false);
			Q = new MyPoint(qx, qy, true);
			App.error("UNHANDLED");
			return new MyPoint[] {P, Q};
		case T0001:
			qx = A[0];
			py = C[1];
			qy = lin(A, D, 1);
			px = lin(C, D, 0);
			P = new MyPoint(px, py, false);
			Q = new MyPoint(qx, qy, true);
			App.error("UNHANDLED");
			return new MyPoint[] {P, Q};
		case T1001:
			py = C[1];
			px = lin(C, D, 0);
			qy = A[1];
			qx = lin(A, B, 0);
			P = new MyPoint(px, py, false);
			Q = new MyPoint(qx, qy, true);
			return new MyPoint[] {P, Q};
		case T1100:
			qx = A[0];
			qy = lin(A, D, 1);
			px = B[0];
			py = lin(C, B, 1);
			P = new MyPoint(px, py, false);
			Q = new MyPoint(qx, qy, true);
			return new MyPoint[] {P, Q};
		case T1010:
			
			P = new MyPoint(0, 0, false);
			Q = new MyPoint(0, 0, true);
			return new MyPoint[] { P, Q };
		case T0101:

			P = new MyPoint(0, 0, false);
			Q = new MyPoint(0, 0, true);
			return new MyPoint[] {P, Q};
		case TINVALID:

			P = new MyPoint(0, 0, false);
			Q = new MyPoint(0, 0, true);
			return new MyPoint[] {P, Q};
		}
		return null;
	}
	
	private double lin(double[] A, double[] B, int i) {
		double fB = evaluateImplicitCurve(B);
		double fA = evaluateImplicitCurve(A);
		double ratio = -fB / (fA - fB);
		if (ratio >= 0 && ratio <= 1) {
			return ratio * (A[i] - B[i]) + B[i];
		}

		return (A[i] + B[i]) * 0.5;
	}

	private GridTypeEnum getGridType(int i, int j) {
		double nw = grid[i][j];
		double ne = grid[i][j + 1];
		double sw = grid[i + 1][j];
		double se = grid[i + 1][j + 1];

		int snw = mySignFun(nw);
		int sne = mySignFun(ne);
		int ssw = mySignFun(sw);
		int sse = mySignFun(se);

		double sum = Math.abs(snw + sne + ssw + sse);
		if (sum > INVALID) {
			return GridTypeEnum.TINVALID;
		}
		if (sum == 4) { // all corners have the same sign
			return GridTypeEnum.T0000;
		}
		if (sum == 2) { // three corners have the same sign
			if (snw != sne) {
				if (snw != ssw) {
					return GridTypeEnum.T1000;
				}
				return GridTypeEnum.T0100;
			}
			if (ssw != snw) {
				return GridTypeEnum.T0001;
			}
			return GridTypeEnum.T0010;
		}
		// two corners have the same sign
		if (snw == ssw) {
			return GridTypeEnum.T1001;
		}
		if (snw == sne) {
			return GridTypeEnum.T1100;
		}
		if (snw > 0) {
			return GridTypeEnum.T1010;
		}
		return GridTypeEnum.T0101;
	}

	private int mySignFun(double val) {
		if (Double.isNaN(val) || Double.isInfinite(val)) {
			return INVALID;
		}
		if (val > 0) {
			return 1;
		}
		return -1;
	}

	private int signAbsSum(double args[]) {
		int s = 0;
		for (int i = 0; i < args.length; i++) {
			s += mySignFun(args[i]);
		}
		return Math.abs(s);
	}

	private double getRealWorldY(int i) {
		double s = rectH / gridHeight;
		return rectY - i * s;
	}

	private double getRealWorldX(int j) {
		double s = rectW/gridWidth;
		return rectX + j * s;
	}

	private double[] rwCoords = new double[2];
	private boolean trace;

	private double[] getRealWorldCoordinates(int i, int j) {
		double x = getRealWorldX(j);
		double y = getRealWorldY(i);
		return new double[]{x,y};
	}

	public boolean euclidianViewUpdate() {
		if (isDefined()) {
			updatePath();
			return true;
		}
		return false;
	}

	private enum GridTypeEnum {
		/** None NW */
		T0000, T1000, T0100, T0010, T0001, T1001, T1100, T1010, T0101, TINVALID;
	}
	
	

	@Override
	final public HitType getLastHitType(){
		return HitType.ON_BOUNDARY;
	}

	@Override
	public boolean isTraceable() {
		return true;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	@Override
	public boolean getTrace() {
		return trace;
	}

	@Override
	public void getXML(boolean listeners, StringBuilder sbxml) {
		if (isIndependent() && getDefaultGeoType() < 0) {
			sbxml.append("<expression");
			sbxml.append(" label =\"");
			sbxml.append(label);
			sbxml.append("\" exp=\"");
			StringUtil.encodeXML(sbxml, toString(StringTemplate.xmlTemplate));
			// expression
			sbxml.append("\"/>\n");
		}
		super.getXML(listeners, sbxml);
	}

	/**
	 * @param PI
	 *            point
	 */
	protected void polishPointOnPath(GeoPointND PI) {
		// TODO find intersection with x=x(PI) numerically
	}

	public void pointChanged(GeoPointND PI) {
		if (locus.getPoints().size() > 0) {
			locus.pointChanged(PI);
			polishPointOnPath(PI);
		}
	}

	public void pathChanged(GeoPointND PI) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(PI)) {
			pointChanged(PI);
			return;
		}

		if (locus.getPoints().size() > 0) {
			locus.pathChanged(PI);
			polishPointOnPath(PI);
		}
	}

	/**
	 * @param PI
	 *            point
	 * @return whether point is on path
	 */
	public boolean isOnPath(GeoPointND PI) {
		return isOnPath(PI, Kernel.STANDARD_PRECISION);
	}

	private double[] eval = new double[2];

	public boolean isOnPath(GeoPointND PI, double eps) {

		if (!PI.isDefined())
			return false;

		double px, py, pz;

		if (PI.isGeoElement3D()) {
			Coords coords = PI.getInhomCoordsInD3();
			if (!Kernel.isZero(coords.getZ())) {
				return false;
			}
			px = coords.getX();
			py = coords.getY();
		} else {
			GeoPoint P = (GeoPoint) PI;

			px = P.x;
			py = P.y;
			pz = P.z;

			if (P.isFinite()) {
				px /= pz;
				py /= pz;
			}
		}
		eval[0] = px;
		eval[1] = py;
		double value = this.expression.evaluate(eval);
		return Math.abs(value) < Kernel.MIN_PRECISION;
	}

	public double getMinParameter() {
		return locus.getMinParameter();
	}

	public double getMaxParameter() {
		return locus.getMaxParameter();
	}

	public boolean isClosedPath() {
		return locus.isClosedPath();
	}

	public PathMover createPathMover() {
		return locus.createPathMover();
	}
}
