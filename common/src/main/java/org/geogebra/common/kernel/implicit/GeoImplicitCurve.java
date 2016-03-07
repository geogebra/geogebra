package org.geogebra.common.kernel.implicit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.DecompositionSolver;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EuclidianViewCE;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.ConicMirrorable;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.Mirrorable;
import org.geogebra.common.kernel.geos.PointRotateable;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;

/**
 * GeoElement representing an implicit curve.
 * 
 */
public class GeoImplicitCurve extends GeoElement implements EuclidianViewCE,
		Traceable, Path, Translateable, Dilateable, Mirrorable, ConicMirrorable,
		Transformable, PointRotateable, GeoImplicit {
	/**
	 * Movements around grid [TOP, BOTTOM, LEFT, RIGHT]
	 */
	static final int[][] MOVE = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
	private GeoFunctionNVar expression;
	private FunctionNVar[] diffExp = new FunctionNVar[3];

	private GeoLocus locus;

	/**
	 * Underlying drawing algorithm
	 */
	protected final QuadTree quadTree = new WebExperimentalQuadTree();

	private double[] evalArray = new double[2];
	private double[] derEvalArray = new double[2];

	private boolean defined = true;
	private boolean trace;
	private boolean hasDerivatives;
	private boolean isConstant;
	private int degX;
	private int degY;
	private double[][] coeff;
	private double[][] coeffSquarefree;

	private double[] eval = new double[2];
	private boolean calcPath = true;
	private boolean inputForm;

	/**
	 * Construct an empty Implicit Curve Object
	 * 
	 * @param c
	 *            construction
	 */
	public GeoImplicitCurve(Construction c) {
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
		fromEquation(equation, null);
		setLabel(label);
	}

	/**
	 * Create an {@link GeoImplicitCurve} object for given equation containing
	 * variables as x and y
	 * 
	 * @param c
	 *            construction
	 * @param equation
	 *            equation of the implicit curve
	 */
	public GeoImplicitCurve(Construction c, Equation equation) {
		this(c);
		fromEquation(equation, null);
	}

	/**
	 * Create a new {@link GeoImplicitCurve} for a given function
	 * 
	 * @param c
	 *            {@link Construction}
	 * @param func
	 *            {@link FunctionNVar}
	 */
	public GeoImplicitCurve(Construction c, FunctionNVar func) {
		this(c);
		MyDouble rhs = new MyDouble(kernel, 0.0);
		Equation eqn = new Equation(kernel, func, rhs);
		fromEquation(eqn, null);
	}

	/**
	 * create a copy of given ImplicitCurve
	 * 
	 * @param curve
	 *            curve to copy
	 */
	public GeoImplicitCurve(GeoImplicitCurve curve) {
		this(curve.cons);
		this.set(curve);
	}

	/**
	 * Create expression from the equation
	 * 
	 * @param eqn
	 *            equation
	 */
	public void fromEquation(Equation eqn, double[][] coeff) {
		setDefinition(eqn.wrap());

		ExpressionNode leftHandSide = eqn.getLHS();
		ExpressionNode rightHandSide = eqn.getRHS();

		if (rightHandSide.getRight() == null
				&& rightHandSide.getLeft() instanceof MyDouble
				&& Kernel.isEqual(rightHandSide.getLeft().evaluateDouble(), 0)) {
			ExpressionNode copyLeft = leftHandSide.deepCopy(kernel);
			// get factors without power of left side
			ArrayList<ExpressionNode> factors = copyLeft.getFactorsWithoutPow();
			if (!factors.isEmpty()) {

			}
		}
		ExpressionNode functionExpression = new ExpressionNode(kernel,
				leftHandSide, Operation.MINUS, rightHandSide);
		FunctionVariable x = new FunctionVariable(kernel, "x");
		FunctionVariable y = new FunctionVariable(kernel, "y");
		VariableReplacer repl = VariableReplacer.getReplacer(kernel);
		VariableReplacer.addVars("x", x);
		VariableReplacer.addVars("y", y);
		functionExpression.traverse(repl);

		FunctionNVar fun = new FunctionNVar(functionExpression,
				new FunctionVariable[] { x, y });
		expression = new GeoFunctionNVar(cons, fun);
		setDerivatives(x, y);
		defined = expression.isDefined();

		if (coeff != null) {
			doSetCoeff(coeff);
		} else {
			updateCoeff(eqn);
		}
		euclidianViewUpdate();
	}

	private void updateCoeff(Equation eqn) {
		eqn.initEquation();
		Polynomial lhs = eqn.getNormalForm();
		if (eqn.mayBePolynomial()) {
			setCoeff(lhs.getCoeff());
		} else {
			resetCoeff();
		}

	}

	public void setCoeff(ExpressionValue[][] ev) {

		resetCoeff();
		degX = ev.length - 1;
		coeff = new double[ev.length][];
		for (int i = 0; i < ev.length; i++) {
			coeff[i] = new double[ev[i].length];
			if (ev[i].length > degY + 1)
				degY = ev[i].length - 1;
			for (int j = 0; j < ev[i].length; j++) {
				if (ev[i][j] == null)
					coeff[i][j] = 0;
				else
					coeff[i][j] = ev[i][j].evaluateDouble();
				if (Double.isInfinite(coeff[i][j])) {
					setUndefined();
				}
				isConstant = isConstant
						&& (Kernel.isZero(coeff[i][j]) || (i == 0 && j == 0));
			}
		}
		// getFactors();
		// if (calcPath&&this.calcPath)
		// updatePath();

	}

	private void resetCoeff() {
		isConstant = true;
		degX = -1;
		degY = -1;
		coeff = null;
		coeffSquarefree = null;

	}

	private void setDerivatives(FunctionVariable x, FunctionVariable y) {
		try {
			hasDerivatives = true;
			FunctionNVar func = expression.getFunction();
			diffExp[0] = func.getDerivativeNoCAS(x, 1);
			diffExp[1] = func.getDerivativeNoCAS(y, 1);
			ExpressionNode der = new ExpressionNode(kernel,
					diffExp[0].getExpression().multiply(-1.0), Operation.DIVIDE,
					diffExp[1].getExpression());
			diffExp[2] = new FunctionNVar(der, new FunctionVariable[] { x, y });
		} catch (Exception ex) {
			hasDerivatives = false;
		}
	}

	/**
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return value of partial derivative, if exist, at (x, y) w.r.t x, NaN
	 *         otherwise
	 * 
	 */
	public double derivativeX(double x, double y) {
		return derivative(diffExp[0], x, y);
	}

	/**
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return value of partial derivative, if exist, at (x, y) w.r.t y, NaN
	 *         otherwise
	 */
	public double derivativeY(double x, double y) {
		return derivative(diffExp[1], x, y);
	}

	/**
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return value of derivative, if exist, at (x, y) w.r.t x, NaN otherwise
	 */
	public double derivative(double x, double y) {
		return derivative(diffExp[2], x, y);
	}

	private double derivative(FunctionNVar func, double x, double y) {
		if (func != null) {
			derEvalArray[0] = x;
			derEvalArray[1] = y;
			return func.evaluate(derEvalArray);
		}
		return Double.NaN;
	}

	/**
	 * @return partial derivative w.r.t. x
	 */
	public FunctionNVar getDerivativeX() {
		return diffExp[0];
	}

	/**
	 * @return partial derivative w.r.t. y
	 */
	public FunctionNVar getDerivativeY() {
		return diffExp[1];
	}

	/**
	 * @return -1 * derivative(x) / derivative(y)
	 */
	public FunctionNVar getDerivativeXY() {
		return diffExp[2];
	}

	/**
	 * 
	 * @return true if derivative of the function exists
	 */
	public boolean hasDerivative() {
		return hasDerivatives;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.IMPLICIT_POLY;
	}

	@Override
	public GeoElement copy() {
		GeoImplicitCurve curve = new GeoImplicitCurve(cons);
		curve.set(this);
		return curve;
	}

	@Override
	public boolean hasDrawable3D() {
		return true;
	}
	@Override
	public void set(GeoElementND geo) {
		if (geo.getDefinition().unwrap() instanceof Equation) {
			fromEquation((Equation) geo.getDefinition().unwrap()
.deepCopy(kernel),
					null);
		} else if (geo instanceof GeoImplicitCurve) {
			ExpressionValue lhs = ((GeoImplicitCurve) geo).expression
					.getFunctionExpression()
					.deepCopy(kernel);
			// Object equationCopy = ((GeoImplicitCurve) geo).equation
			// .deepCopy(kernel);
			fromEquation(new Equation(kernel, lhs, new MyDouble(kernel, 0)),
					((GeoImplicitCurve) geo).coeff);
		}

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
	public boolean isGeoImplicitCurve() {
		return true;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return getDefinition() == null ? "" : getDefinition()
				.toValueString(tpl);
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
		return false;
	}

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
		if (viewBounds[0] == Double.POSITIVE_INFINITY) {
			viewBounds = new double[] { -10, 10, -10, 10, 10, 10 };
		}

		updatePathQuadTree(viewBounds[0], viewBounds[3],
				viewBounds[1] - viewBounds[0], viewBounds[3] - viewBounds[2],
				viewBounds[4], viewBounds[5]);
	}

	private void updatePathQuadTree(double x, double y, double w, double h,
			double scaleX, double scaleY) {
		locus.getPoints().clear();
		quadTree.updatePath(x, y - h, w, h, scaleX, scaleY);
	}

	/**
	 * Update euclidian view
	 */
	@Override
	public boolean euclidianViewUpdate() {
		if (isDefined()) {
			updatePath();
			return true;
		}
		return false;
	}

	@Override
	final public HitType getLastHitType() {
		return HitType.ON_BOUNDARY;
	}

	@Override
	public boolean isPath() {
		return true;
	}

	@Override
	public boolean isTraceable() {
		return true;
	}

	@Override
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

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		getLineStyleXML(sb);
		if (coeff != null) {
			sb.append("\t<coefficients rep=\"array\" data=\"");
			sb.append("[");
			for (int i = 0; i < coeff.length; i++) {
				if (i > 0)
					sb.append(',');
				sb.append("[");
				for (int j = 0; j < coeff[i].length; j++) {
					if (j > 0)
						sb.append(',');
					sb.append(coeff[i][j]);
				}
				sb.append("]");
			}
			sb.append("]");
			sb.append("\" />\n");
		}
	}

	/**
	 * set defined
	 */
	public void setDefined() {
		this.defined = true;
	}

	/**
	 * @param PI
	 *            point
	 */
	protected void polishPointOnPath(GeoPointND PI) {
		quadTree.polishPointOnPath(PI);
	}

	@Override
	public void pointChanged(GeoPointND PI) {
		if (locus.getPoints().size() > 0) {
			locus.pointChanged(PI);
			polishPointOnPath(PI);
		}
	}

	@Override
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



	@Override
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

	@Override
	public double getMinParameter() {
		return locus.getMinParameter();
	}

	@Override
	public double getMaxParameter() {
		return locus.getMaxParameter();
	}

	@Override
	public boolean isClosedPath() {
		return locus.isClosedPath();
	}

	@Override
	public PathMover createPathMover() {
		return locus.createPathMover();
	}

	@Override
	public void translate(Coords v) {
		expression.translate(v);
		if (coeff != null) {
			updateCoeff(new Equation(kernel,
					expression.getFunctionExpression(), new MyDouble(kernel, 0)));
		}
		euclidianViewUpdate();
	}

	/**
	 * translate the curve
	 * 
	 * @param dx
	 *            distance in x direction
	 * @param dy
	 *            distance in y direction
	 */
	public void translate(double dx, double dy) {
		translate(new Coords(dx, dy, 1));
	}

	@Override
	public void mirror(Coords Q) {
		expression.mirror(Q);
		euclidianViewUpdate();
	}

	@Override
	public void mirror(GeoLineND g) {
		expression.mirror(g);
		euclidianViewUpdate();
	}

	@Override
	public void dilate(NumberValue r, Coords S) {
		expression.dilate(r, S);
		euclidianViewUpdate();
	}

	@Override
	public void rotate(NumberValue phi) {
		expression.rotate(phi);
		euclidianViewUpdate();
	}

	@Override
	public void rotate(NumberValue phi, GeoPointND S) {
		expression.rotate(phi, S);
		euclidianViewUpdate();
	}

	public void mirror(GeoConic c) {
		MyDouble r2 = new MyDouble(kernel, c.getHalfAxis(0) * c.getHalfAxis(0));
		expression.translate(c.getMidpoint2D().mul(-1));
		FunctionVariable x = expression.getFunctionVariables()[0];
		FunctionVariable y = expression.getFunctionVariables()[1];
		ExpressionNode expr = expression.getFunctionExpression().deepCopy(kernel);
		FunctionVariable x2 = new FunctionVariable(kernel, "x");
		FunctionVariable y2 = new FunctionVariable(kernel, "y");
		ExpressionValue newX = x2.wrap().multiply(r2)
				.divide(x2.wrap().power(2).plus(y2.wrap().power(2)));
		ExpressionValue newY = y2.wrap().multiply(r2)
				.divide(x2.wrap().power(2).plus(y2.wrap().power(2)));
		expr.replace(x, newX);
		expr.replace(y, newY);
		FunctionNVar f2 = new FunctionNVar(expr, new FunctionVariable[] { x2,
				y2 });
		expression.setFunction(f2);
		expression.translate(c.getMidpoint2D());
		setDefinition(new Equation(kernel, expr, new MyDouble(kernel, 0))
				.wrap());

		if (getCoeff() != null) {
			double cx = c.getMidpoint().getX();
			double cy = c.getMidpoint().getY();
			double cr = c.getCircleRadius();

			plugInRatPoly(new double[][] {
					{ cx * cx * cx + cx * cy * cy - cx * cr * cr, -2 * cx * cy,
							cx }, { -2 * cx * cx + cr * cr, 0, 0 },
					{ cx, 0, 0 } }, new double[][] {
					{ cx * cx * cy + cy * cy * cy - cy * cr * cr,
							-2 * cy * cy + cr * cr, cy },
					{ -2 * cx * cy, 0, 0 }, { cy, 0, 0 } }, new double[][] {
					{ cx * cx + cy * cy, -2 * cy, 1 }, { -2 * cx, 0, 0 },
					{ 1, 0, 0 } }, new double[][] {
					{ cx * cx + cy * cy, -2 * cy, 1 }, { -2 * cx, 0, 0 },
					{ 1, 0, 0 } });
		} else {
			// for polynomials pluhIn does that
			euclidianViewUpdate();
		}
	}

	public void plugInRatPoly(double[][] pX, double[][] pY, double[][] qX,
			double[][] qY) {
		int degXpX = pX.length - 1;
		int degYpX = 0;
		for (int i = 0; i < pX.length; i++) {
			if (pX[i].length - 1 > degYpX)
				degYpX = pX[i].length - 1;
		}
		int degXqX = -1;
		int degYqX = -1;
		if (qX != null) {
			degXqX = qX.length - 1;
			for (int i = 0; i < qX.length; i++) {
				if (qX[i].length - 1 > degYqX)
					degYqX = qX[i].length - 1;
			}
		}
		int degXpY = pY.length - 1;
		int degYpY = 0;
		for (int i = 0; i < pY.length; i++) {
			if (pY[i].length - 1 > degYpY)
				degYpY = pY[i].length - 1;
		}
		int degXqY = -1;
		int degYqY = -1;
		if (qY != null) {
			degXqY = qY.length - 1;
			for (int i = 0; i < qY.length; i++) {
				if (qY[i].length - 1 > degYqY)
					degYqY = qY[i].length - 1;
			}
		}
		boolean sameDenom = false;
		if (qX != null && qY != null) {
			sameDenom = true;
			if (degXqX == degXqY && degYqX == degYqY) {
				for (int i = 0; i < qX.length; i++)
					if (!Arrays.equals(qY[i], qX[i])) {
						sameDenom = false;
						break;
					}
			}
		}
		int commDeg = 0;
		if (sameDenom) {
			// find the "common" degree, e.g. x^4+y^4->4, but x^4 y^4->8
			commDeg = getDeg();
		}
		int newDegX = Math.max(degXpX, degXqX) * degX
				+ Math.max(degXpY, degXqY) * degY;
		int newDegY = Math.max(degYpX, degYqX) * degX
				+ Math.max(degYpY, degYqY) * degY;

		double[][] newCoeff = new double[newDegX + 1][newDegY + 1];
		double[][] tmpCoeff = new double[newDegX + 1][newDegY + 1];
		double[][] ratXCoeff = new double[newDegX + 1][newDegY + 1];
		double[][] ratYCoeff = new double[newDegX + 1][newDegY + 1];
		int tmpCoeffDegX = 0;
		int tmpCoeffDegY = 0;
		int newCoeffDegX = 0;
		int newCoeffDegY = 0;
		int ratXCoeffDegX = 0;
		int ratXCoeffDegY = 0;
		int ratYCoeffDegX = 0;
		int ratYCoeffDegY = 0;

		for (int i = 0; i < newDegX; i++) {
			for (int j = 0; j < newDegY; j++) {
				newCoeff[i][j] = 0;
				tmpCoeff[i][j] = 0;
				ratXCoeff[i][j] = 0;
				ratYCoeff[i][j] = 0;
			}
		}
		ratXCoeff[0][0] = 1;
		for (int x = coeff.length - 1; x >= 0; x--) {
			if (qY != null) {
				ratYCoeff[0][0] = 1;
				ratYCoeffDegX = 0;
				ratYCoeffDegY = 0;
			}
			int startY = coeff[x].length - 1;
			if (sameDenom)
				startY = commDeg - x;
			for (int y = startY; y >= 0; y--) {
				if (qY == null || y == startY) {
					if (coeff[x].length > y)
						tmpCoeff[0][0] += coeff[x][y];
				} else {
					polyMult(ratYCoeff, qY, ratYCoeffDegX, ratYCoeffDegY,
							degXqY, degYqY); // y^N-i
					ratYCoeffDegX += degXqY;
					ratYCoeffDegY += degYqY;
					if (coeff[x].length > y)
						for (int i = 0; i <= ratYCoeffDegX; i++) {
							for (int j = 0; j <= ratYCoeffDegY; j++) {
								tmpCoeff[i][j] += coeff[x][y] * ratYCoeff[i][j];
								if (y == 0) {
									ratYCoeff[i][j] = 0; // clear in last loop
								}
							}
						}
					tmpCoeffDegX = Math.max(tmpCoeffDegX, ratYCoeffDegX);
					tmpCoeffDegY = Math.max(tmpCoeffDegY, ratYCoeffDegY);
				}
				if (y > 0) {
					polyMult(tmpCoeff, pY, tmpCoeffDegX, tmpCoeffDegY, degXpY,
							degYpY);
					tmpCoeffDegX += degXpY;
					tmpCoeffDegY += degYpY;
				}
			}
			if (qX != null && x != coeff.length - 1 && !sameDenom) {
				polyMult(ratXCoeff, qX, ratXCoeffDegX, ratXCoeffDegY, degXqX,
						degYqX);
				ratXCoeffDegX += degXqX;
				ratXCoeffDegY += degYqX;
				polyMult(tmpCoeff, ratXCoeff, tmpCoeffDegX, tmpCoeffDegY,
						ratXCoeffDegX, ratXCoeffDegY);
				tmpCoeffDegX += ratXCoeffDegX;
				tmpCoeffDegY += ratXCoeffDegY;
			}
			for (int i = 0; i <= tmpCoeffDegX; i++) {
				for (int j = 0; j <= tmpCoeffDegY; j++) {
					newCoeff[i][j] += tmpCoeff[i][j];
					tmpCoeff[i][j] = 0;
				}
			}
			newCoeffDegX = Math.max(newCoeffDegX, tmpCoeffDegX);
			newCoeffDegY = Math.max(newCoeffDegY, tmpCoeffDegY);
			tmpCoeffDegX = 0;
			tmpCoeffDegY = 0;
			if (x > 0) {
				polyMult(newCoeff, pX, newCoeffDegX, newCoeffDegY, degXpX,
						degYpX);
				newCoeffDegX += degXpX;
				newCoeffDegY += degYpX;
			}
		}
		// maybe we made the degree larger than necessary, so we try to get it
		// down.
		coeff = PolynomialUtils.coeffMinDeg(newCoeff);
		// calculate new degree
		degX = coeff.length - 1;
		degY = 0;
		for (int i = 0; i < coeff.length; i++) {
			degY = Math.max(degY, coeff[i].length - 1);
		}

		updatePath();
		if (algoUpdateSet != null) {
			double a = 0, ax = 0, ay = 0, b = 0, bx = 0, by = 0;
			if (qX == null && qY == null && degXpX <= 1 && degYpX <= 1
					&& degXpY <= 1 && degYpY <= 1) {
				if ((degXpX != 1 || degYpX != 1 || pX[1].length == 1 || Kernel
						.isZero(pX[1][1]))
						&& (degXpY != 1 || degYpY != 1 || pY[1].length == 1 || Kernel
								.isZero(pY[1][1]))) {
					if (pX.length > 0) {
						if (pX[0].length > 0) {
							a = pX[0][0];
						}
						if (pX[0].length > 1) {
							ay = pX[0][1];
						}
					}
					if (pX.length > 1) {
						ax = pX[1][0];
					}
					if (pY.length > 0) {
						if (pY[0].length > 0) {
							b = pY[0][0];
						}
						if (pY[0].length > 1) {
							by = pY[0][1];
						}
					}
					if (pY.length > 1) {
						bx = pY[1][0];
					}
					double det = ax * by - bx * ay;
					if (!Kernel.isZero(det)) {
						double[][] iX = new double[][] {
								{ (b * ay - a * by) / det, -ay / det },
								{ by / det } };
						double[][] iY = new double[][] {
								{ -(b * ax - a * bx) / det, ax / det },
								{ -bx / det } };

						Iterator<AlgoElement> it = algoUpdateSet.getIterator();
						while (it != null && it.hasNext()) {
							AlgoElement elem = it.next();
							if (elem instanceof AlgoPointOnPath
									&& isIndependent()) {
								GeoPoint point = (GeoPoint) ((AlgoPointOnPath) elem)
										.getP();
								if (!Kernel.isZero(point.getZ())) {
									double x = point.getX() / point.getZ();
									double y = point.getY() / point.getZ();
									double px = evalPolyCoeffAt(x, y, iX);
									double py = evalPolyCoeffAt(x, y, iY);
									point.setCoords(px, py, 1);
									point.updateCoords();
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param coeff
	 *            coefficients of evaluated poly P
	 * @return P(x,y)
	 */
	public static double evalPolyCoeffAt(double x, double y, double[][] coeff) {
		double sum = 0;
		double zs = 0;
		// Evaluating Poly via the Horner-scheme
		if (coeff != null)
			for (int i = coeff.length - 1; i >= 0; i--) {
				zs = 0;
				for (int j = coeff[i].length - 1; j >= 0; j--) {
					zs = y * zs + coeff[i][j];
				}
				sum = sum * x + zs;
			}
		return sum;
	}

	/**
	 * 
	 * @param polyDest
	 * @param polySrc
	 *            polyDest=polyDest*polySrc;
	 */
	static void polyMult(double[][] polyDest, double[][] polySrc,
			int degDestX, int degDestY, int degSrcX, int degSrcY) {
		double[][] result = new double[degDestX + degSrcX + 1][degDestY
				+ degSrcY + 1];
		for (int n = 0; n <= degDestX + degSrcX; n++) {
			for (int m = 0; m <= degDestY + degSrcY; m++) {
				double sum = 0;
				for (int k = Math.max(0, n - degSrcX); k <= Math.min(n,
						degDestX); k++)
					for (int j = Math.max(0, m - degSrcY); j <= Math.min(m,
							degDestY); j++)
						sum += polyDest[k][j] * polySrc[n - k][m - j];
				result[n][m] = sum;
			}
		}
		for (int n = 0; n <= degDestX + degSrcX; n++) {
			for (int m = 0; m <= degDestY + degSrcY; m++) {
				polyDest[n][m] = result[n][m];
			}
		}
	}
	/**
	 * 
	 * @return FunctionNVar
	 */
	public FunctionNVar getExpression() {
		return expression.getFunction();
	}

	/**
	 * @param fa
	 *            f(p1)
	 * @param fb
	 *            f(p2)
	 * @param p1
	 *            first point
	 * @param p2
	 *            second point
	 * @return linear interpolation of p1 and p2 based on f(p1) and f(p2)
	 */
	public static double interpolate(double fa, double fb, double p1,
			double p2) {
		double r = -fb / (fa - fb);
		if (r >= 0 && r <= 1) {
			return r * (p1 - p2) + p2;
		}
		return (p1 + p2) * 0.5;
	}

	/**
	 * 
	 * @param in
	 *            parameters {f(x1, y1), f(x2, y2), x1, y1, x2, y2}
	 * @param out
	 *            interpolated {x, y}
	 */
	public static void interpolate(double[] in, double[] out) {
		double r = -in[1] / (in[0] - in[1]);
		if (!MyDouble.isFinite(r) || r > 1.0 || r < 0.0) {
			r = 0.5;
		}
		out[0] = r * (in[2] - in[4]) + in[4];
		out[1] = r * (in[3] - in[5]) + in[5];
	}

	/**
	 * 
	 * @param c1
	 *            first curve
	 * @param c2
	 *            second curve
	 * @param n
	 *            maximum number of samples in output
	 * @return list of points which may be closer to the path of both implicit
	 *         curves
	 */
	public static List<Coords> probableInitialPoints(GeoImplicitCurve c1,
			GeoImplicitCurve c2, int n) {
		return c1.quadTree.probablePoints(c2, n);
	}

	/**
	 * 
	 * @param f1
	 *            First function
	 * @param f2
	 *            Second function
	 * @param params
	 *            {xMin, yMin, xMax, yMax}
	 * @param n
	 *            number of samples
	 * @return at most n points around which the path of the function might
	 *         intersect in the rectangle defined by (xMin, yMin), (xMax, yMax).
	 *         The rectangle is sampled at regular interval of ceil(sqrt(n))
	 */
	public static List<Coords> probableInitialPoints(FunctionNVar f1,
			FunctionNVar f2, double[] params, int n) {
		return probableInitialPoints(f1, f2, params[0], params[1], params[2],
				params[3], n);
	}

	/**
	 * 
	 * @param f1
	 *            First function
	 * @param f2
	 *            Second function
	 * @param xMin
	 *            minimum x value
	 * @param yMin
	 *            minimum y value
	 * @param xMax
	 *            maximum x value
	 * @param yMax
	 *            maximum y value
	 * @param n
	 *            number of samples
	 * @return at most n points around which the path of the function might
	 *         intersect in the rectangle defined by (xMin, yMin), (xMax, yMax).
	 *         The rectangle is sampled at regular interval of ceil(sqrt(n))
	 */
	public static List<Coords> probableInitialPoints(FunctionNVar f1,
			FunctionNVar f2, double xMin, double yMin, double xMax, double yMax,
			int n) {

		int root = (int) (Math.sqrt(n) + 1);
		List<Coords> out = new ArrayList<Coords>();
		if (xMin >= xMax || yMin >= yMax) {
			// empty intersecting rectangle
			return out;
		}

		double inx = (xMax - xMin) / (root + 1), inx2 = 0.5 * inx;
		double iny = (yMax - yMin) / (root + 1), iny2 = 0.5 * iny;
		double[] y1 = new double[root + 1];
		double[] y2 = new double[root + 1];
		boolean[] present = new boolean[n + 1];
		double cur1, cur2, prev1, prev2;
		double[] eval = new double[] { xMin, yMin };
		y1[0] = f1.evaluate(eval);
		y2[0] = f2.evaluate(eval);
		for (int i = 1; i <= root; i++) {
			eval[0] = xMin + i * inx;
			y1[i] = f1.evaluate(eval);
			y2[i] = f2.evaluate(eval);
			if ((y1[i - 1] * y1[i] <= 0.0) && (y2[i - 1] * y2[i] <= 0.0)) {
				present[i] = true;
				eval[0] -= inx2;
				out.add(new Coords(eval));
			}
		}

		for (int i = 1; i <= root; i++) {
			eval[1] = yMin + i * iny;
			prev1 = f1.evaluate(eval);
			prev2 = f2.evaluate(eval);
			for (int j = 1; j <= root; j++) {
				eval[0] = xMin + j * inx;
				cur1 = f1.evaluate(eval);
				cur2 = f2.evaluate(eval);
				if (!present[j] && (y1[i - 1] * y1[i] <= 0.0)
						&& (y2[i - 1] * y2[i] <= 0.0)) {
					present[j] = true;
					out.add(new Coords(eval[0] - inx2, eval[1] - iny2));

					if (out.size() == n) {
						return out;
					}

				} else {
					present[j] = false;
				}
				y1[i - 1] = prev1;
				y2[i - 1] = prev2;
				prev1 = cur1;
				prev2 = cur2;
			}
			y1[root] = prev1;
			y2[root] = prev2;
		}
		if (out.size() < 2) {
			out.add(new Coords(0.5 * (xMin + xMax), 0.5 * (yMin + yMax)));
			out.add(new Coords(0.25 * (xMin + xMax), 0.25 * (yMin + yMax)));
			out.add(new Coords(0.75 * (xMin + xMax), 0.25 * (yMin + yMax)));
			out.add(new Coords(0.25 * (xMin + xMax), 0.75 * (yMin + yMax)));
			out.add(new Coords(0.75 * (xMin + xMax), 0.75 * (yMin + yMax)));
		}
		return out;
	}

	/**
	 * Base class for quadtree algorithms
	 */
	private abstract class QuadTree {
		/**
		 * All corners are inside / outside
		 */
		@SuppressWarnings("unused")
		public static final int T0000 = 0;

		/**
		 * only bottom left corner is inside / outside
		 */
		public static final int T0001 = 1;

		/**
		 * bottom right corner is inside / outside
		 */
		public static final int T0010 = 2;

		/**
		 * both corners at the bottom are inside / outside
		 */
		public static final int T0011 = 3;

		/**
		 * top left corner is inside / outside
		 */
		public static final int T0100 = 4;

		/**
		 * opposite corners are inside / outside. NOTE: This configuration is
		 * regarded as invalid
		 */
		public static final int T0101 = 5;

		/**
		 * both the corners at the left are inside / outside
		 */
		public static final int T0110 = 6;

		/**
		 * only top left corner is inside / outside
		 */
		public static final int T0111 = 7;

		/**
		 * invalid configuration. expression value is undefined / infinity for
		 * at least one of the corner
		 */
		public static final int T_INV = -1;
		public static final int EMPTY = 0;
		public static final int FINISHED = Integer.MAX_VALUE;
		public static final int VALID = 1;
		/**
		 * it would be better to adjust LIST_THRESHOLD based on platform
		 */
		public int LIST_THRESHOLD = 48;
		protected double x;
		protected double y;
		protected double w;
		protected double h;
		protected double scaleX;
		protected double scaleY;
		protected ArrayList<MyPoint> locusPoints;
		private LinkedList<PointList> openList = new LinkedList<PointList>();
		private MyPoint[] pts = new MyPoint[2];
		private PointList p1, p2;
		private MyPoint temp;
		private ListIterator<PointList> itr1, itr2;

		public QuadTree() {

		}

		public int config(Rect r) {
			int config = 0;
			for (int i = 0; i < 4; i++) {
				config = (config << 1) | sign(r.evals[i]);
			}
			return config >= 8 ? (~config) & 0xf : config;
		}

		/**
		 * 
		 * @param val
		 *            value to check
		 * @return the sign depending on the value. if value is infinity or NaN
		 *         it returns T_INV, otherwise it returns 1 for +ve value 0
		 *         otherwise
		 */
		public int sign(double val) {
			if (Double.isInfinite(val) || Double.isNaN(val)) {
				return T_INV;
			} else if (val > 0.0) {
				return 1;
			} else {
				return 0;
			}
		}

		public void abortList() {
			itr1 = openList.listIterator();
			while (itr1.hasNext()) {
				p1 = itr1.next();
				locusPoints.add(p1.start);
				locusPoints.addAll(p1.pts);
				locusPoints.add(p1.end);
			}
			openList.clear();
		}

		private boolean equal(MyPoint q1, MyPoint q2) {
			return Kernel.isEqual(q1.x, q2.x, 1e-10)
					&& Kernel.isEqual(q1.y, q2.y, 1e-10);
		}

		public int addSegment(Rect r) {
			int status = createSegment(r);
			if (status == VALID) {
				if (pts[0].x > pts[1].x) {
					temp = pts[0];
					pts[0] = pts[1];
					pts[1] = temp;
				}
				itr1 = openList.listIterator();
				itr2 = openList.listIterator();
				boolean flag1 = false, flag2 = false;
				while (itr1.hasNext()) {
					p1 = itr1.next();
					if (equal(pts[1], p1.start)) {
						flag1 = true;
						break;
					}
				}

				while (itr2.hasNext()) {
					p2 = itr2.next();
					if (equal(pts[0], p2.end)) {
						flag2 = true;
						break;
					}
				}

				if (flag1 && flag2) {
					itr1.remove();
					p2.mergeTo(p1);
				} else if (flag1) {
					p1.extendBack(pts[0]);
				} else if (flag2) {
					p2.extendFront(pts[1]);
				} else {
					openList.addFirst(new PointList(pts[0], pts[1]));
				}
				if (openList.size() > LIST_THRESHOLD) {
					abortList();
				}
			}
			return status;
		}

		public int createSegment(Rect r) {
			int gridType = config(r);
			if (gridType == T0101 || gridType == T_INV) {
				return gridType;
			}

			double x1 = r.x1(), x2 = r.x2(), y1 = r.y1(), y2 = r.y2();
			double tl = r.evals[0], tr = r.evals[1], br = r.evals[2], bl = r.evals[3];
			double q1 = 0.0, q2 = 0.0;

			switch (gridType) {
			// one or three corners are inside / outside
			case T0001:
				pts[0] = new MyPoint(x1, interpolate(bl, tl, y2, y1), false);
				pts[1] = new MyPoint(interpolate(bl, br, x1, x2), y2, true);
				q1 = Math.min(Math.abs(bl), Math.abs(tl));
				q2 = Math.min(Math.abs(bl), Math.abs(br));
				break;

			case T0010:
				pts[0] = new MyPoint(x2, interpolate(br, tr, y2, y1), false);
				pts[1] = new MyPoint(interpolate(br, bl, x2, x1), y2, true);
				q1 = Math.min(Math.abs(br), Math.abs(tr));
				q2 = Math.min(Math.abs(br), Math.abs(bl));
				break;

			case T0100:
				pts[0] = new MyPoint(x2, interpolate(tr, br, y1, y2), false);
				pts[1] = new MyPoint(interpolate(tr, tl, x2, x1), y1, true);
				q1 = Math.min(Math.abs(tr), Math.abs(br));
				q2 = Math.min(Math.abs(tr), Math.abs(tl));
				break;

			case T0111:
				pts[0] = new MyPoint(x1, interpolate(tl, bl, y1, y2), false);
				pts[1] = new MyPoint(interpolate(tl, tr, x1, x2), y1, true);
				q1 = Math.min(Math.abs(bl), Math.abs(tl));
				q2 = Math.min(Math.abs(tl), Math.abs(tr));
				break;

			// two consecutive corners are inside / outside
			case T0011:
				pts[0] = new MyPoint(x1, interpolate(tl, bl, y1, y2), false);
				pts[1] = new MyPoint(x2, interpolate(tr, br, y1, y2), true);
				q1 = Math.min(Math.abs(tl), Math.abs(bl));
				q2 = Math.min(Math.abs(tr), Math.abs(br));
				break;

			case T0110:
				pts[0] = new MyPoint(interpolate(tl, tr, x1, x2), y1, false);
				pts[1] = new MyPoint(interpolate(bl, br, x1, x2), y2, true);
				q1 = Math.min(Math.abs(tl), Math.abs(tr));
				q2 = Math.min(Math.abs(bl), Math.abs(br));
				break;
			default:
				return EMPTY;
			}
			// check continuity of the function between P1 and P2
			double p = Math.abs(evaluateImplicitCurve(pts[0].x, pts[0].y));
			double q = Math.abs(evaluateImplicitCurve(pts[1].x, pts[1].y));
			if ((p <= q1 && q <= q2)) {
				return VALID;
			}
			return EMPTY;
		}

		/**
		 * force to redraw the rectangular area bounded by (startX, startY,
		 * startX + w, startY + h)
		 * 
		 * @param startX
		 *            starting x coordinate
		 * @param startY
		 *            starting y coordinate
		 * @param width
		 *            width of the rectangular view
		 * @param height
		 *            height of the rectangular view
		 * @param slX
		 *            scaleX
		 * @param slY
		 *            scaleY
		 */
		public void updatePath(double startX, double startY, double width,
				double height, double slX, double slY) {
			this.x = startX;
			this.y = startY;
			this.w = width;
			this.h = height;
			this.scaleX = slX;
			this.scaleY = slY;
			this.locusPoints = getLocus().getPoints();
			this.updatePath();
			this.abortList();
		}

		public void polishPointOnPath(GeoPointND pt) {
			pt.setUndefined();
		}

		public List<Coords> probablePoints(GeoImplicitCurve other, int n) {
			double xMin = Math.max(x, other.quadTree.x);
			double yMin = Math.max(y, other.quadTree.y);
			double xMax = Math.min(x + w, other.quadTree.x + w);
			double yMax = Math.min(y + h, other.quadTree.y + h);
			return probableInitialPoints(getExpression(), other.getExpression(),
					xMin, yMin, xMax, yMax, n);
		}

		public int edgeConfig(Rect r) {
			int config = (intersect(r.evals[0], r.evals[1]) << 3)
					| (intersect(r.evals[1], r.evals[2]) << 2)
					| (intersect(r.evals[2], r.evals[3]) << 1)
					| (intersect(r.evals[3], r.evals[0]));
			if (config == 15 || config == 0) {
				return EMPTY;
			}
			return config;
		}

		/**
		 * 
		 * @param c1
		 *            the value of curve at one of the square vertices
		 * @param c2
		 *            the value of curve at the other vertex
		 * @return true if the edge connecting two vertices intersect with curve
		 *         segment
		 */
		private int intersect(double c1, double c2) {
			if (c1 * c2 <= 0.0) {
				return 1;
			}
			return 0;
		}

		public abstract void updatePath();
	}

	private class PointList {
		MyPoint start;
		MyPoint end;
		LinkedList<MyPoint> pts = new LinkedList<MyPoint>();

		public PointList(MyPoint start, MyPoint end) {
			this.start = start;
			this.end = end;
			this.start.lineTo = false;
			this.end.lineTo = true;
		}

		public void mergeTo(PointList pl) {
			this.pts.addLast(this.end);
			pl.start.lineTo = true;
			this.pts.addLast(pl.start);
			this.end = pl.end;
			int s1 = this.pts.size(), s2 = pl.pts.size();

			if (s2 == 0) {
				return;
			}

			if (s1 < s2) {
				ListIterator<MyPoint> itr = this.pts.listIterator(s1 - 1);
				while (itr.hasPrevious()) {
					pl.pts.addFirst(itr.previous());
				}
				this.pts = pl.pts;
			} else {
				ListIterator<MyPoint> itr = pl.pts.listIterator();
				while (itr.hasNext()) {
					this.pts.addLast(itr.next());
				}
			}
		}

		public void extendBack(MyPoint p) {
			p.lineTo = false;
			this.start.lineTo = true;
			this.pts.addFirst(start);
			this.start = p;
		}

		public void extendFront(MyPoint p) {
			p.lineTo = true;
			this.pts.addLast(this.end);
			this.end = p;
		}
	}

	/**
	 * Border mask
	 */
	static final int[] MASK = { 0x9, 0xC, 0x6, 0x3 };

	private static class Timer {
		public long now;
		public long elapse;

		public static Timer newTimer() {
			return new Timer();
		}

		public void reset() {
			this.now = System.currentTimeMillis();
		}

		public void record() {
			this.elapse = System.currentTimeMillis() - now;
		}
	}

	/**
	 * @author GSoCImplicitCurve-2015
	 */
	private class WebExperimentalQuadTree extends QuadTree {
		private static final int RES_COARSE = 8;
		private static final int MAX_SPLIT = 40;
		private int plotDepth;
		private int segmentCheckDepth;
		private int sw;
		private int sh;
		private Rect[][] grid;
		private Rect temp;
		private Timer timer = Timer.newTimer();

		public WebExperimentalQuadTree() {
			super();
		}

		@Override
		public void updatePath() {
			this.sw = Math.min(MAX_SPLIT, (int) (w * scaleX / RES_COARSE));
			this.sh = Math.min(MAX_SPLIT, (int) (h * scaleY / RES_COARSE));
			if (sw == 0 || sh == 0) {
				return;
			}

			if (grid == null || grid.length != sh || grid[0].length != sw) {
				this.grid = new Rect[sh][sw];
				for (int i = 0; i < sh; i++) {
					for (int j = 0; j < sw; j++) {
						this.grid[i][j] = new Rect();
					}
				}
			}

			if (temp == null) {
				temp = new Rect();
			}

			double frx = w / sw;
			double fry = h / sh;

			double[] vertices = new double[sw + 1];
			double[] xcoords = new double[sw + 1];
			double[] ycoords = new double[sh + 1];
			double cur, prev;

			for (int i = 0; i <= sw; i++) {
				xcoords[i] = x + i * frx;
			}

			for (int i = 0; i <= sh; i++) {
				ycoords[i] = y + i * fry;
			}

			for (int i = 0; i <= sw; i++) {
				vertices[i] = evaluateImplicitCurve(xcoords[i], ycoords[0]);
			}

			// initialize grid configuration at the search depth
			int i, j;
			double dx, dy, fx, fy;
			// debug = true;
			timer.reset();
			for (i = 1; i <= sh; i++) {
				prev = evaluateImplicitCurve(xcoords[0], ycoords[i]);
				fy = ycoords[i] - 0.5 * fry;
				for (j = 1; j <= sw; j++) {
					cur = evaluateImplicitCurve(xcoords[j], ycoords[i]);
					Rect rect = this.grid[i - 1][j - 1];
					rect.set(j - 1, i - 1, frx, fry, false);
					rect.coords.val[0] = xcoords[j - 1];
					rect.coords.val[1] = ycoords[i - 1];
					rect.evals[0] = vertices[j - 1];
					rect.evals[1] = vertices[j];
					rect.evals[2] = cur;
					rect.evals[3] = prev;
					rect.status = edgeConfig(rect);
					rect.shares = 0xff;
					fx = xcoords[j] - 0.5 * frx;
					dx = derivativeX(fx, fy);
					dy = derivativeY(fx, fy);
					dx = Math.abs(dx) + Math.abs(dy);
					if (Kernel.isZero(dx, 0.001)) {
						rect.singular = true;
					}
					this.grid[i - 1][j - 1] = rect;
					vertices[j - 1] = prev;
					prev = cur;
				}
				vertices[sw] = prev;
			}

			timer.record();

			if (timer.elapse <= 10) {
				// Fast device optimize for UX
				plotDepth = 3;
				segmentCheckDepth = 2;
				LIST_THRESHOLD = 48;
			} else {
				// Slow device detected reduce parameters
				plotDepth = 2;
				segmentCheckDepth = 1;
				LIST_THRESHOLD = 24;
			}

			for (i = 0; i < sh; i++) {
				for (j = 0; j < sw; j++) {
					if (!grid[i][j].singular && grid[i][j].status != EMPTY) {
						temp.set(grid[i][j]);
						plot(temp, 0);
						grid[i][j].status = FINISHED;
					}
				}
			}

			timer.record();

			if (timer.elapse >= 500) {
				// I can't do anything more. I've been working for 500 ms
				// Therefore I am tired
				return;
			} else if (timer.elapse >= 300) {
				// I am exhausted, reducing load!
				plotDepth -= 1;
				segmentCheckDepth -= 1;
			}

			for (int k = 0; k < 4; k++) {
				for (i = 0; i < sh; i++) {
					for (j = 0; j < sw; j++) {
						if (grid[i][j].singular
								&& grid[i][j].status != FINISHED) {
							temp.set(grid[i][j]);
							plot(temp, 0);
							grid[i][j].status = FINISHED;
						}
					}
				}
			}
		}

		public void createTree(Rect r, int depth) {
			Rect[] n = r.split(GeoImplicitCurve.this);
			plot(n[0], depth);
			plot(n[1], depth);
			plot(n[2], depth);
			plot(n[3], depth);
		}

		public void plot(Rect r, int depth) {
			if (depth < segmentCheckDepth) {
				createTree(r, depth + 1);
				return;
			}
			int e = edgeConfig(r);
			if (grid[r.y][r.x].singular || e != EMPTY) {
				if (depth >= plotDepth) {
					if (addSegment(r) == T0101) {
						createTree(r, depth + 1);
						return;
					}
					if (r.x != 0 && (e & r.shares & 0x1) != 0) {
						if (grid[r.y][r.x - 1].status == EMPTY) {
							grid[r.y][r.x - 1].status = 1;
						}
					}
					if (r.x + 1 != sw && (e & r.shares & 0x4) != 0) {
						if (grid[r.y][r.x + 1].status == EMPTY) {
							grid[r.y][r.x + 1].status = 1;
						}
					}
					if (r.y != 0 && (e & r.shares & 0x8) != 0) {
						if (grid[r.y - 1][r.x].status == EMPTY) {
							grid[r.y - 1][r.x].status = 1;
						}
					}
					if (r.y + 1 != sh && (e & r.shares & 0x2) != 0) {
						if (grid[r.y + 1][r.x].status == EMPTY) {
							grid[r.y + 1][r.x].status = 1;
						}
					}
				} else {
					createTree(r, depth + 1);
				}
			}
		}

		@Override
		public void polishPointOnPath(GeoPointND pt) {
			double x1 = onScreen(pt.getInhomX(), this.x, this.x + this.w);
			double y1 = onScreen(pt.getInhomY(), this.y, this.y + this.h);
			double d1 = evaluateImplicitCurve(x1, y1);
			if (Kernel.isZero(d1)) {
				pt.setCoords(new Coords(x1, y1, 1.0), false);
				return;
			}
			double mv = Math.max(w, h) / MAX_SPLIT, x2, y2, d2, mx, my, md;
			for (int i = 0; i < MOVE.length; i++) {
				x2 = x1 + mv * MOVE[i][0];
				y2 = y1 + mv * MOVE[i][1];
				d2 = evaluateImplicitCurve(x2, y2);
				if (d2 * d1 <= 0.0) {
					int count = 0;
					while (!Kernel.isZero(d2) && count < 64) {
						mx = 0.5 * (x1 + x2);
						my = 0.5 * (y1 + y2);
						md = evaluateImplicitCurve(mx, my);
						if (Kernel.isZero(md)) {
							pt.setCoords(new Coords(mx, my, 1.0), false);
							return;
						}
						if (d1 * md <= 0.0) {
							d2 = md;
							x2 = mx;
							y2 = my;
						} else {
							d1 = md;
							x1 = mx;
							y1 = my;
						}
						count++;
					}
				}
			}
		}

		private double onScreen(double v, double mn, double mx) {
			if (Double.isNaN(v) || Double.isInfinite(v) || v < mn || v > mx) {
				return (mn + mx) * 0.5;
			}
			return v;
		}
	}

	@Override
	public boolean hasLineOpacity() {
		return true;
	}

	public ValueType getValueType() {
		return ValueType.EQUATION;
	}

	public double[][] getCoeff() {
		return coeff;
	}

	public void setCoeff(double[][] coeff) {
		setCoeff(coeff, true);
	}

	public int getDeg() {
		if (coeff == null) {
			return -1;
		}
		int deg = 0;
		for (int d = degX + degY; d >= 0; d--) {
			for (int x = 0; x <= degX; x++) {
				int y = d - x;
				if (y >= 0 && y < coeff[x].length) {
					if (Math.abs(coeff[x][y]) > Kernel.STANDARD_PRECISION) {
						deg = d;
						d = 0;
						break;
					}
				}
			}
		}
		return deg;
	}

	public boolean isOnScreen() {
		return defined && locus.isDefined() && locus.getPoints().size() > 0;
	}

	public double evalPolyAt(double x, double y) {
		if (coeff != null) {
			return GeoImplicitCurve.evalPolyCoeffAt(x, y, coeff);
		}
		return this.expression.evaluate(x, y, 0);
	}

	public int getDegX() {
		return degX;
	}

	public int getDegY() {
		return degY;
	}

	public void setInputForm() {
		inputForm = true;

	}

	public double evalDiffXPolyAt(double x, double y) {
		if (coeff != null) {
			return GeoImplicitPoly.evalDiffXPolyAt(x, y, coeff);
		}
		return evallDiff(0, x, y);
	}

	private double evallDiff(int i, double inhomX, double inhomY) {
		ExpressionNode diffEx = expression.getFunctionExpression()
				.derivative(expression.getFunctionVariables()[i], kernel);
		expression.getFunctionVariables()[0].set(inhomX);
		expression.getFunctionVariables()[1].set(inhomY);
		return diffEx.evaluateDouble();

	}

	public double evalDiffYPolyAt(double x, double y) {
		if (coeff != null) {
			return GeoImplicitPoly.evalDiffYPolyAt(x, y, coeff);
		}
		return evallDiff(1, x, y);
	}

	/**
	 * @return squarefree coefficients
	 */
	public double[][] getCoeffSquarefree() {
		return coeffSquarefree;
	}

	public void preventPathCreation() {
		calcPath = false;

	}

	public boolean isValidInputForm() {
		return getDefinition() != null;
	}

	public boolean isInputForm() {
		return inputForm;
	}

	public void setExtendedForm() {
		inputForm = false;

	}

	public void throughPoints(GeoList points) {
		ArrayList<GeoPoint> p = new ArrayList<GeoPoint>();
		for (int i = 0; i < points.size(); i++)
			p.add((GeoPoint) points.get(i));
		throughPoints(p);
	}

	/**
	 * make curve through given points
	 * 
	 * @param points
	 *            ArrayList of points
	 */
	public void throughPoints(ArrayList<GeoPoint> points) {
		if ((int) Math.sqrt(9 + 8 * points.size()) != Math.sqrt(9 + 8 * points
				.size())) {
			setUndefined();
			return;
		}

		int degree = (int) (0.5 * Math.sqrt(8 * (1 + points.size()))) - 1;
		int realDegree = degree;

		RealMatrix extendMatrix = new Array2DRowRealMatrix(points.size(),
				points.size() + 1);
		RealMatrix matrix = new Array2DRowRealMatrix(points.size(),
				points.size());
		double[][] coeffMatrix = new double[degree + 1][degree + 1];

		DecompositionSolver solver;

		double[] matrixRow = new double[points.size() + 1];
		double[] results;

		for (int i = 0; i < points.size(); i++) {
			double x = points.get(i).x / points.get(i).z;
			double y = points.get(i).y / points.get(i).z;

			for (int j = 0, m = 0; j < degree + 1; j++)
				for (int k = 0; j + k != degree + 1; k++)
					matrixRow[m++] = Math.pow(x, j) * Math.pow(y, k);
			extendMatrix.setRow(i, matrixRow);
		}

		int solutionColumn = 0, noPoints = points.size();

		do {
			if (solutionColumn > noPoints) {
				noPoints = noPoints - realDegree - 1;

				if (noPoints < 2) {
					setUndefined();
					return;
				}

				extendMatrix = new Array2DRowRealMatrix(noPoints, noPoints + 1);
				realDegree -= 1;
				matrixRow = new double[noPoints + 1];

				for (int i = 0; i < noPoints; i++) {
					double x = points.get(i).x;
					double y = points.get(i).y;

					for (int j = 0, m = 0; j < realDegree + 1; j++)
						for (int k = 0; j + k != realDegree + 1; k++)
							matrixRow[m++] = Math.pow(x, j) * Math.pow(y, k);
					extendMatrix.setRow(i, matrixRow);
				}

				matrix = new Array2DRowRealMatrix(noPoints, noPoints);
				solutionColumn = 0;
			}

			results = extendMatrix.getColumn(solutionColumn);

			for (int i = 0, j = 0; i < noPoints + 1; i++) {
				if (i == solutionColumn)
					continue;
				matrix.setColumn(j++, extendMatrix.getColumn(i));
			}
			solutionColumn++;

			solver = new LUDecompositionImpl(matrix).getSolver();
		} while (!solver.isNonSingular());

		for (int i = 0; i < results.length; i++)
			results[i] *= -1;

		double[] partialSolution = solver.solve(results);

		double[] solution = new double[partialSolution.length + 1];

		for (int i = 0, j = 0; i < solution.length; i++)
			if (i == solutionColumn - 1)
				solution[i] = 1;
			else {
				solution[i] = (Kernel.isZero(partialSolution[j])) ? 0
						: partialSolution[j];
				j++;
			}

		for (int i = 0, k = 0; i < realDegree + 1; i++)
			for (int j = 0; i + j < realDegree + 1; j++)
				coeffMatrix[i][j] = solution[k++];

		this.setCoeff(coeffMatrix, true);

		setDefined();
		for (int i = 0; i < points.size(); i++)
			if (!this.isOnPath(points.get(i), 1)) {
				this.setUndefined();
				return;
			}

	}

	private void setCoeff(double[][] coeffMatrix, boolean updatePath) {
		// TODO Auto-generated method stub ImplicitCurve[{A,B,C,D,E,F,G,H,I}]
		doSetCoeff(coeffMatrix);
		setDefined();
		FunctionVariable x = new FunctionVariable(kernel, "x");
		FunctionVariable y = new FunctionVariable(kernel, "y");
		ExpressionNode expr = null;
		for (int i = 0; i <= degX; i++) {
			// different rows have different lengths
			for (int j = 0; j < coeff[i].length; j++) {
				if (i == 0 && j == 0) {
					expr = new ExpressionNode(kernel, coeff[0][0]);
				} else {
					expr = expr.plus(x.wrap().power(i)
							.multiply(y.wrap().power(j)).multiply(coeff[i][j]));
				}
			}
		}
		setDefinition(new Equation(kernel, expr, new MyDouble(kernel, 0))
				.wrap());
			expression = new GeoFunctionNVar(cons, new FunctionNVar(expr,
					new FunctionVariable[] { x, y }));
		if (updatePath) {
			updatePath();
		}

	}

	private void doSetCoeff(double[][] coeffMatrix) {
		this.coeff = coeffMatrix;
		this.degX = coeff.length - 1;
		this.degY = coeff[0].length - 1;
	}

	public CoordSys getCoordSys() {
		return CoordSys.Identity3D;
	}

}
