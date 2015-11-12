package org.geogebra.common.kernel.implicit;

import java.util.ArrayList;
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
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.ConicMirrorable;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
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
		Traceable, Path, Translateable, Dilateable, Mirrorable,
		ConicMirrorable, Transformable,
 PointRotateable, GeoImplicit {
	/**
	 * Movements around grid [TOP, BOTTOM, LEFT, RIGHT]
	 */
	static final int[][] MOVE = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
	private GeoFunctionNVar expression;
	private FunctionNVar[] diffExp = new FunctionNVar[3];

	private GeoLocus locus;

	private Equation equation;
	/**
	 * Underlying drawing algorithm
	 */
	protected final QuadTree quadTree = new ExperimentalQuadTree();

	private double[] evalArray = new double[2];
	private double[] derEvalArray = new double[2];

	private boolean defined = true;
	private boolean trace;
	private boolean hasDerivatives;

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
		fromEquation(equation);
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
		fromEquation(equation);
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
		fromEquation(eqn);
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
	public void fromEquation(Equation eqn) {
		this.equation = eqn;
		ExpressionNode leftHandSide = eqn.getLHS();
		ExpressionNode rightHandSide = eqn.getRHS();

		ExpressionNode functionExpression = new ExpressionNode(kernel,
				leftHandSide, Operation.MINUS, rightHandSide);
		FunctionVariable x = new FunctionVariable(kernel, "x");
		FunctionVariable y = new FunctionVariable(kernel, "y");
		VariableReplacer repl = VariableReplacer.getReplacer();
		VariableReplacer.addVars("x", x);
		VariableReplacer.addVars("y", y);
		functionExpression.traverse(repl);
				
		FunctionNVar fun = new FunctionNVar(functionExpression,
				new FunctionVariable[] { x,
						y });
		expression = new GeoFunctionNVar(cons, fun);
		setDerivatives(x, y);
		defined = expression.isDefined();
		euclidianViewUpdate();
	}

	private void setDerivatives(FunctionVariable x, FunctionVariable y) {
		try {
			hasDerivatives = true;
			FunctionNVar func = expression.getFunction();
			diffExp[0] = func.getDerivativeNoCAS(x, 1);
			diffExp[1] = func.getDerivativeNoCAS(y, 1);
			ExpressionNode der = new ExpressionNode(kernel, diffExp[0]
					.getExpression().multiply(-1.0), Operation.DIVIDE,
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
		return GeoClass.IMPLICIT_CURVE;
	}

	@Override
	public GeoElement copy() {
		GeoImplicitCurve curve = new GeoImplicitCurve(cons);
		curve.set(this);
		return curve;
	}

	@Override
	public void set(GeoElementND geo) {
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
	public boolean isGeoImplicitCurve() {
		return true;
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
		updatePathQuadTree(viewBounds[0], viewBounds[3], viewBounds[1]
				- viewBounds[0], viewBounds[3] - viewBounds[2], viewBounds[4],
				viewBounds[5]);
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
	final public HitType getLastHitType(){
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

	private double[] eval = new double[2];

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
		// may be conic mirrorable
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
	public static double interpolate(double fa, double fb, double p1, double p2) {
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
			FunctionNVar f2, double xMin, double yMin, double xMax,
			double yMax, int n) {

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
		public static final int T_INV = 16;

		protected double x;
		protected double y;
		protected double w;
		protected double h;
		protected double fracX;
		protected double fracY;
		protected double scaleX;
		protected double scaleY;
		protected ArrayList<MyPoint> locusPoints;

		public QuadTree() {

		}

		/**
		 * 
		 * @param topLeft
		 *            value of function evaluated at top left corner of the
		 *            square cell
		 * @param topRight
		 *            value of function evaluated at top right corner of the
		 *            square cell
		 * @param bottomRight
		 *            value of the function evaluated at bottom right corner of
		 *            the square cell
		 * @param bottomLeft
		 *            value of the function evaluated at bottom left corner of
		 *            the square cell
		 * @return an integer between 0 to 7 representing type of configuration
		 *         of the cell or T_INV if for an invalid cell configuration
		 */
		public int config(double topLeft, double topRight, double bottomRight,
				double bottomLeft) {
			// find and pack corner configuration
			int config = sign(topLeft);
			config = (config << 1) | sign(topRight);
			config = (config << 1) | sign(bottomRight);
			config = (config << 1) | sign(bottomLeft);
			if (config >= 16) {
				// invalid configuration
				return T_INV;
			} else if (config >= 8) {
				// get complementary configuration for the cell
				config = (~config) & 0xf;
			}
			return config;
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

		/**
		 * Add a new segment which exists in square cell bounded by (x1, y1) and
		 * (x2, y2) to the locus
		 * 
		 * @param x1
		 *            leftmost x coordinate
		 * @param y1
		 *            topmost y coordinate
		 * @param x2
		 *            rightmost x coordinate
		 * @param y2
		 *            bottom most y coordinate
		 */
		public void addSegment(double x1, double y1, double x2, double y2) {
			// evaluate curve at each corner
			double tl = evaluateImplicitCurve(x1, y1);
			double tr = evaluateImplicitCurve(x2, y1);
			double br = evaluateImplicitCurve(x2, y2);
			double bl = evaluateImplicitCurve(x1, y2);
			// add segment
			addSegment(x1, y1, x2, y2, tl, tr, br, bl);
		}

		/**
		 * @param x1
		 *            leftmost x coordinate
		 * @param y1
		 *            topmost y coordinate
		 * @param x2
		 *            rightmost x coordinate
		 * @param y2
		 *            bottom most y coordinate
		 * @param tl
		 *            value of the function evaluated at top-left corner
		 * @param tr
		 *            value of the function evaluated at top-right corner
		 * @param br
		 *            value of the function evaluated at bottom-right corner
		 * @param bl
		 *            value of the function evaluated at bottom-left corner
		 */
		public void addSegment(double x1, double y1, double x2, double y2,
				double tl, double tr, double br, double bl) {
			// get an appropriate segment
			MyPoint[] pts = getSegmentFor(config(tl, tr, br, bl), x1, y1, x2,
					y2, tl, tr, br, bl);

			// add segment to locus
			if (pts != null) {
				locusPoints.add(pts[0]);
				locusPoints.add(pts[1]);
			}
		}

		/**
		 * Create an appropriate segment for the given square cell
		 * 
		 * @param gridType
		 *            type of the grid
		 * @param x1
		 *            leftmost x coordinate
		 * @param y1
		 *            topmost y coordinate
		 * @param x2
		 *            rightmost x coordinate
		 * @param y2
		 *            bottom most y coordinate
		 * @param tl
		 *            value of the function evaluated at top-left corner
		 * @param tr
		 *            value of the function evaluated at top-right corner
		 * @param br
		 *            value of the function evaluated at bottom-right corner
		 * @param bl
		 *            value of the function evaluated at bottom-left corner
		 * @return Array of two points representing a segment of straight line
		 *         from the first point to the second point or null for invalid
		 *         cell
		 */
		public MyPoint[] getSegmentFor(int gridType, double x1,
				double y1, double x2, double y2, double tl, double tr,
				double br, double bl) {

			MyPoint P = null, Q = null;
			double p1 = 0.0, p2 = 0.0;
			switch (gridType) {
			// one or three corners are inside / outside
			case T0001:
				P = new MyPoint(x1, interpolate(bl, tl, y2, y1), false);
				Q = new MyPoint(interpolate(bl, br, x1, x2), y2, true);
				p1 = Math.min(Math.abs(bl), Math.abs(tl));
				p2 = Math.min(Math.abs(bl), Math.abs(br));
				break;

			case T0010:
				P = new MyPoint(x2, interpolate(br, tr, y2, y1), false);
				Q = new MyPoint(interpolate(br, bl, x2, x1), y2, true);
				p1 = Math.min(Math.abs(br), Math.abs(tr));
				p2 = Math.min(Math.abs(br), Math.abs(bl));
				break;

			case T0100:
				P = new MyPoint(x2, interpolate(tr, br, y1, y2), false);
				Q = new MyPoint(interpolate(tr, tl, x2, x1), y1, true);
				p1 = Math.min(Math.abs(tr), Math.abs(br));
				p2 = Math.min(Math.abs(tr), Math.abs(tl));
				break;

			case T0111:
				P = new MyPoint(x1, interpolate(tl, bl, y1, y2), false);
				Q = new MyPoint(interpolate(tl, tr, x1, x2), y1, true);
				p1 = Math.min(Math.abs(bl), Math.abs(tl));
				p2 = Math.min(Math.abs(tl), Math.abs(tr));
				break;

				// two consecutive corners are inside / outside
			case T0011:
				P = new MyPoint(x1, interpolate(tl, bl, y1, y2), false);
				Q = new MyPoint(x2, interpolate(tr, br, y1, y2), true);
				p1 = Math.min(Math.abs(tl), Math.abs(bl));
				p2 = Math.min(Math.abs(tr), Math.abs(br));
				break;

			case T0110:
				P = new MyPoint(interpolate(tl, tr, x1, x2), y1, false);
				Q = new MyPoint(interpolate(bl, br, x1, x2), y2, true);
				p1 = Math.min(Math.abs(tl), Math.abs(tr));
				p2 = Math.min(Math.abs(bl), Math.abs(br));
				break;

				// a pair of opposite corners are inside / outside
			case T0101:
				// invalid/value is undefined for at least on of the corner
			case T_INV:
			case T0000:
			default:
				return null;
			}
			// check continuity of the function between P1 and P2
			double p = Math.abs(evaluateImplicitCurve(P.x, P.y));
			double q = Math.abs(evaluateImplicitCurve(Q.x, Q.y));
			if ((p <= p1 && q <= p2)) {
				return new MyPoint[] { P, Q };
			}
			return null;
		}

		/**
		 * 
		 * @param startX
		 *            leftmost x coordinate of the square cell
		 * @param startY
		 *            topmost y coordinate of the square cell
		 * @param endX
		 *            rightmost x coordinate of the square cell
		 * @param endY
		 *            bottom most y coordinate of the square cell
		 * @return true if the square cell contains a crossing segment
		 */
		public boolean hasSegment(double startX, double startY, double endX,
				double endY) {
			double tl = evaluateImplicitCurve(startX, startY);
			double br = evaluateImplicitCurve(endX, endY);
			int s1 = sign(tl);
			int s2 = sign(br);
			if ((s1 == s2) && (tl + br != 0.0)) {
				if (sign(evaluateImplicitCurve(endX, startY)) == s1) {
					return s1 != sign(evaluateImplicitCurve(startX, endY));
				}
			}
			return true;
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
		}

		public void polishPointOnPath(GeoPointND pt) {
			pt.setUndefined();
		}

		public List<Coords> probablePoints(GeoImplicitCurve other, int n) {
			// TODO: It would be nice idea to find xMin, yMin etc from the locus
			double xMin = Math.max(x, other.quadTree.x);
			double yMin = Math.max(y, other.quadTree.y);
			double xMax = Math.min(x + w, other.quadTree.x + w);
			double yMax = Math.min(y + h, other.quadTree.y + h);
			return probableInitialPoints(getExpression(),
					other.getExpression(), xMin, yMin, xMax, yMax, n);
		}

		public abstract void updatePath();
	}

	/**
	 * Implementation of the first quad-tree algorithm. The algorithm searches
	 * for the cells which contain a curve segment up to seach_depth. It goes
	 * deep up to plot_depth if a square cell has a segment and eventually plot
	 * the segment
	 */
	@SuppressWarnings("unused")
	private class SimpleQuadTree extends QuadTree {
		private static final int MAX_PLOT_DEPTH = 1 << 8;
		private static final int MAX_SEARCH_DEPTH = 1 << 6;
		private int searchDepth;
		private int plotDepth;

		public SimpleQuadTree() {
			super();
		}

		@Override
		public void updatePath() {
			// get max of height and width to get square shaped subdivision
			double mx = Math.max(w, h);
			// Ensure that grid size should be at least eight pixel
			int pxls = (int) (Math.max(w * scaleX, h * scaleY) * 0.125 + 1);
			if(pxls == 0) {
				return;
			}
			// Ceil to next power of two
			int hBits = Integer.highestOneBit(pxls);
			if ((pxls & (pxls - 1)) != 0) {
				hBits <<= 1;
			}
			// Adjust plot and search depth according to size of viewport
			plotDepth = Math.min(hBits, MAX_PLOT_DEPTH);
			searchDepth = Math.min(hBits >> 2, MAX_SEARCH_DEPTH);

			// initialize x and y fractions corresponding to PLOT_DEPTH
			super.fracX = mx / plotDepth;
			super.fracY = mx / plotDepth;
			// execute quad-tree
			createTree(0, 0, 1);
		}

		/**
		 * An integer based fast implementation of the subdivision algorithm
		 * 
		 * @param startX
		 *            starting x coordinate
		 * @param startY
		 *            starting y coordinate
		 * @param depth
		 *            current depth which is always power of two
		 */
		private void subdivide(int startX, int startY, int depth) {
			int frac = plotDepth / depth;
			createTree(startX, startY, depth);
			createTree(startX | frac, startY, depth);
			createTree(startX | frac, startY | frac, depth);
			createTree(startX, startY | frac, depth);
		}

		/**
		 * An integer based implementation of quad-tree
		 * 
		 * @param startX
		 *            starting x coordinate
		 * @param startY
		 *            starting y coordinate
		 * @param depth
		 *            current depth which is always power of two
		 */
		private void createTree(int startX, int startY, int depth) {
			if (depth < searchDepth) {
				// increase the depth and divide the region further
				subdivide(startX, startY, depth << 1);
			} else {
				// calculate the current fraction of the whole grid
				int frac = plotDepth / depth;

				// calculate all four coordinate based on current fraction, x
				// and y coordinate
				double x1 = this.x + startX * fracX;
				double x2 = this.x + (startX + frac) * fracX;
				double y1 = this.y + startY * fracY;
				double y2 = this.y + (startY + frac) * fracY;

				// check whether square cell contains a segment
				if (hasSegment(x1, y1, x2, y2)) {
					// subdivide of current depth is smaller than plot_depth
					// other wise plot the current cell
					if (depth < plotDepth) {
						subdivide(startX, startY, depth << 1);
					} else {
						addSegment(x1, y1, x2, y2);
					}
				}
			}
		}

	}

	/**
	 * Quadtree implementation which exploits curvature property of the curve.
	 * 
	 * For more details flow the link and see second approach
	 * http://comjnl.oxfordjournals.org/content/33/5/402.full.pdf+html
	 */
	@SuppressWarnings("unused")
	private class CurvatureQuadTree extends QuadTree {
		/**
		 * Maximum search depth
		 */
		private static final int MAX_SEARCH_DEPTH = 1 << 5;
		/**
		 * Minimum grid size in pixel. Subdivision stops and square cell is
		 * plotted when grid size equals MIN_GRID_SIZE
		 */
		private static final int MIN_GRID_SIZE = 4;
		/**
		 * Maximum grid size in pixel. Plotting won't start if grid size is
		 * larger than MAX_GRID_SIZE
		 */
		private static final int MAX_GRID_SIZE = 64;
		/**
		 * A factor to determine whether a cell should be plotted or subdivided
		 */
		private static final int N = 8;

		private int minPlotDepth;
		private int searchDepth;
		private int plotDepth;

		private FunctionNVar fx;
		private FunctionNVar fy;
		private FunctionNVar fxx;
		private FunctionNVar fyy;
		private FunctionNVar fxy;

		public CurvatureQuadTree() {
			super();
		}

		private void init() {
			// calculate the required derivatives
			FunctionNVar func = getExpression();
			fx = func.getDerivativeNoCAS(getVar(func, "x"), 1);
			fy = func.getDerivativeNoCAS(getVar(func, "y"), 1);
			fxx = fx.getDerivativeNoCAS(getVar(fx, "x"), 1);
			fxy = fx.getDerivativeNoCAS(getVar(fx, "y"), 1);
			fyy = fy.getDerivativeNoCAS(getVar(fy, "y"), 1);
		}

		private FunctionVariable getVar(FunctionNVar func, String name) {
			FunctionVariable[] fvs = func.getFunctionVariables();
			for (int i = 0; i < fvs.length; i++) {
				if (name.equals(fvs[i].getSetVarString())) {
					return fvs[i];
				}
			}
			return new FunctionVariable(kernel, name);
		}

		/**
		 * evaluate radius of the curvature at (x1, y1)
		 * 
		 * @param x1
		 *            x-coordinate
		 * @param y1
		 *            y-coordinate
		 * @return radius of curvature at (x1, y1)
		 */
		private double radius(double x1, double y1) {
			double[] arr = new double[] { x1, y1 };
			double xv = fx.evaluate(arr);
			double yv = fy.evaluate(arr);
			double xx = fxx.evaluate(arr);
			double yy = fyy.evaluate(arr);
			double xy = fxy.evaluate(arr);
			double x2 = xv * xv; // x^2
			double y2 = yv * yv; // y^2
			double num = x2 + y2;
			double den = xx * y2 + yy * x2 - 2 * xy * xv * yv;
			return Math.pow(num, 1.5) / den;
		}

		@Override
		public void updatePath() {
			init();
			// get max of height and width to get square shaped subdivision
			double mx = Math.max(w, h);

			int pxls = (int) (Math.max(w * scaleX, h * scaleY) + 1);
			if (pxls == 0) {
				return;
			}
			// ceil to power of two
			int hBits = Integer.highestOneBit(pxls);
			if ((pxls & (pxls - 1)) != 0) {
				hBits <<= 1;
			}

			// max plot depth
			plotDepth = hBits / MIN_GRID_SIZE;
			// max search depth
			searchDepth = Math.min(hBits / MAX_GRID_SIZE, MAX_SEARCH_DEPTH);
			// min plot depth
			minPlotDepth = Math.min(searchDepth << 2, plotDepth);

			// initialize x and y fractions corresponding to PLOT_DEPTH
			super.fracX = mx / plotDepth;
			super.fracY = mx / plotDepth;

			// execute quad-tree
			createTree(0, 0, 1);
		}

		/**
		 * An integer based fast implementation of the subdivision algorithm
		 * 
		 * @param startX
		 *            starting x coordinate
		 * @param startY
		 *            starting y coordinate
		 * @param depth
		 *            current depth which is always power of two
		 */
		private void subdivide(int startX, int startY, int depth) {
			int frac = plotDepth / depth;
			createTree(startX, startY, depth);
			createTree(startX | frac, startY, depth);
			createTree(startX | frac, startY | frac, depth);
			createTree(startX, startY | frac, depth);
		}

		/**
		 * An integer based implementation of quad-tree
		 * 
		 * @param startX
		 *            starting x coordinate
		 * @param startY
		 *            starting y coordinate
		 * @param depth
		 *            current depth which is always power of two
		 */
		private void createTree(int startX, int startY, int depth) {
			if (depth < searchDepth) {
				// increase the depth and divide the region further
				subdivide(startX, startY, depth << 1);
			} else {
				// calculate the current fraction of the whole grid
				int frac = plotDepth / depth;

				// calculate all four coordinate based on current fraction, x
				// and y coordinate
				double x1 = this.x + startX * fracX;
				double x2 = this.x + (startX + frac) * fracX;
				double y1 = this.y + startY * fracY;
				double y2 = this.y + (startY + frac) * fracY;

				double d = (x2 - x1) * 0.5;
				// check whether square cell contains a segment
				if (hasSegment(x1, y1, x2, y2)) {
					// plot if radius is large enough or depth equals plotDepth
					if ((depth >= minPlotDepth && canPlot(x1 + d, y1 + d, d))
							|| depth >= plotDepth) {
						addSegment(x1, y1, x2, y2);
					} else {
						subdivide(startX, startY, depth << 1);
					}
				}
			}
		}

		/**
		 * 
		 * @param xc
		 *            x coordinate at the center of the square cell
		 * @param yc
		 *            y coordinate at the center of the square cell
		 * @param d
		 *            size of the square cell
		 * @return true if we can plot the current segment
		 */
		private boolean canPlot(double xc, double yc, double d) {
			double r = radius(xc, yc);
			if(Double.isNaN(r) && Double.isInfinite(r)) {
				return false;
			}
			return Math.abs(r) > N * d;
		}
	}

	/**
	 * Experimental implementation of quad tree. Maximum search_depth (S) is
	 * currently 5,
	 * 
	 */
	private class ExperimentalQuadTree extends QuadTree {
		/**
		 * maximum search depth
		 */
		private static final int MAX_SEARCH_DEPTH = 1 << 5;
		/**
		 * minimum possible grid size in pixels
		 */
		private static final int MIN_GRID_SIZE = 8;
		/**
		 * maximum possible grid size in pixels
		 */
		private static final int MAX_GRID_SIZE = 64;
		/**
		 * a constant indicating that square cell has been plotted
		 */
		private static final int FINISHED = Integer.MAX_VALUE;
		/**
		 * a constant indicating square cell is empty
		 */
		private static final int EMPTY = 0;

		private int searchDepth;
		private int stepMask;
		private int steps;
		private int plotDepth;
		private int maxPoints;
		private int[][] grid;
		private int[][] mark;
		private int[][] points;
		private int segmentCheckDepth;
		private int currentX;
		private int currentY;
		private double[][] rect;
		private double[] coordx;
		private double[] coordy;
		private boolean[][] status;
		public ExperimentalQuadTree() {
			int m = MAX_SEARCH_DEPTH + 2;
			this.maxPoints = MAX_SEARCH_DEPTH * (MAX_SEARCH_DEPTH + 1);
			this.points = new int[maxPoints][2];
			grid = new int[m][m];
			mark = new int[m][m];
			rect = new double[m][m];
			coordx = new double[m];
			coordy = new double[m];
			status = new boolean[m][m];
		}

		@Override
		public void updatePath() {
			// get max of height and width to get square shaped subdivision
			double mx = Math.max(w, h);

			int pxls = (int) (Math.max(w * scaleX, h * scaleY) + 1);
			if (pxls == 0) {
				return;
			}
			// ceil to power of two
			int hBits = Integer.highestOneBit(pxls);
			if ((pxls & (pxls - 1)) != 0) {
				hBits <<= 1;
			}

			// max plot depth
			plotDepth = hBits / MIN_GRID_SIZE;

			// max search depth
			searchDepth = Math.max(1,
					Math.min(hBits / MAX_GRID_SIZE, MAX_SEARCH_DEPTH));

			double frx = mx / searchDepth;
			double fry = mx / searchDepth;

			// allocate memory to memorize x and y coordinates at the search
			// depth
			int end = searchDepth + 1;
			double[] vertices = new double[end];
			double[] xcoords = new double[end];
			double[] ycoords = new double[end];
			double cur, prev;

			for (int i = 0; i <= searchDepth; i++) {
				xcoords[i] = x + i * frx;
				ycoords[i] = y + i * fry;
			}

			for (int i = 0; i <= searchDepth; i++) {
				vertices[i] = evaluateImplicitCurve(xcoords[i], ycoords[0]);
			}

			// initialize grid configuration at the search depth
			int count = 0, top = 0, i, j, ni, nj, k;
			for (i = 1; i <= searchDepth; i++) {
				prev = evaluateImplicitCurve(xcoords[0], ycoords[i]);
				mark[0][i] = mark[i][0] = mark[end][i] = mark[i][end] = 2;
				for (j = 1; j <= searchDepth; j++) {
					cur = evaluateImplicitCurve(xcoords[j], ycoords[i]);

					grid[i][j] = edgeConfig(vertices[j - 1],
							vertices[j], cur, prev);
					mark[i][j] = 0;
					if (grid[i][j] != EMPTY) {
						count++;
						points[top][0] = i;
						points[top++][1] = j;
						mark[i][j] = 1;
					}
					vertices[j - 1] = prev;
					prev = cur;
				}
				vertices[searchDepth] = prev;
			}

			if (count <= 96) {
				plotDepth <<= 2;
			} else if (count <= 192) {
				plotDepth <<= 1;
			}

			// set step size for search depth
			steps = plotDepth / searchDepth;

			// step mask to identify intersection
			stepMask = steps - 1;

			steps = Integer.numberOfTrailingZeros(steps);

			super.fracX = mx / plotDepth;
			super.fracY = mx / plotDepth;

			segmentCheckDepth = Math.min(searchDepth << 2, plotDepth);

			// for the very first iteration we check that if a square grid has a
			// segment, but in subsequent iteration we don't perform any check
			int rtop = maxPoints;
			while (top != 0) {
				i = points[--top][0];
				j = points[top][1];
				mark[i][j] = 2;
				currentX = j;
				currentY = i;
				plot(xcoords[j - 1], ycoords[i - 1], frx, fry);
				for (k = 0; k < 4; k++) {
					ni = i + MOVE[k][0];
					nj = j + MOVE[k][1];
					if (mark[ni][nj] == 0 && grid[ni][nj] != EMPTY) {
						mark[ni][nj] = 1;
						points[--rtop][0] = ni;
						points[rtop][1] = nj;
					}
				}
				grid[i][j] = FINISHED;
			}

			if (rtop == maxPoints) {
				return;
			}
			top = maxPoints - rtop;
			if (top <= 96) {
				plotDepth = searchDepth << 4;
				segmentCheckDepth = plotDepth;
			} else if (top <= 192) {
				plotDepth = searchDepth << 4;
				segmentCheckDepth = plotDepth >> 1;
			} else {
				segmentCheckDepth = Math.min(128, plotDepth);
			}

			steps = plotDepth / searchDepth;

			// step mask to identify intersection
			stepMask = steps - 1;

			steps = Integer.numberOfTrailingZeros(steps);

			super.fracX = mx / plotDepth;
			super.fracY = mx / plotDepth;
			
			top = rtop;

			while (top != maxPoints) {
				i = points[top][0];
				j = points[top++][1];
				mark[i][j] = 2;
				currentX = j;
				currentY = i;
				plot(xcoords[j - 1], ycoords[i - 1], frx, fry);
				for (k = 0; k < 4; k++) {
					ni = i + MOVE[k][0];
					nj = j + MOVE[k][1];
					if (mark[ni][nj] == 0 && grid[ni][nj] != EMPTY) {
						mark[ni][nj] = 1;
						points[--top][0] = ni;
						points[top][1] = nj;
					}
				}
				grid[i][j] = FINISHED;
			}
		}

		private void plot(double x1, double y1, double w1, double h1) {
			int size = plotDepth / searchDepth;
			int inc = plotDepth / segmentCheckDepth;
			double frx = w1 / size;
			double fry = h1 / size;
			coordx[0] = x1;
			coordy[0] = y1;
			for (int i = 1; i <= size; i++) {
				coordx[i] = coordx[i - 1] + frx;
				coordy[i] = coordy[i - 1] + fry;
			}

			for (int i = 0; i <= size; i++) {
				for (int j = 0; j <= size; j++) {
					status[i][j] = false;
				}
			}

			for (int i = 0; i <= size; i += inc) {
				rect[0][i] = evaluateImplicitCurve(coordx[i], y1);
				status[0][i] = true;
			}

			double tl, tr, bl, br;
			for (int i = inc, pi = 0; i <= size; i += inc) {
				rect[i][0] = evaluateImplicitCurve(x1, coordy[i]);
				status[i][0] = true;
				for (int j = inc, pj = 0; j <= size; j += inc) {
					rect[i][j] = evaluateImplicitCurve(coordx[j], coordy[i]);
					status[i][j] = true;
					tl = rect[pi][pj];
					tr = rect[pi][j];
					br = rect[i][j];
					bl = rect[i][pj];
					if (edgeConfig(tl, tr, br, bl) != EMPTY) {
						plot(pj, pi, segmentCheckDepth);
					}
					pj = j;
				}
				pi = i;
			}
		}

		private void createTree(int x1, int y1, int depth) {
			int f = plotDepth / depth;
			plot(x1, y1, depth);
			plot(x1 | f, y1, depth);
			plot(x1 | f, y1 | f, depth);
			plot(x1, y1 | f, depth);
		}

		private double checkAndEvaluate(int x1, int y1) {
			if (!status[y1][x1]) {
				status[y1][x1] = true;
				rect[y1][x1] = evaluateImplicitCurve(coordx[x1], coordy[y1]);
			}
			return rect[y1][x1];
		}

		private void plot(int sx, int sy, int depth) {
			if (depth < segmentCheckDepth) {
				createTree(sx, sy, depth << 1);
				return;
			}
			// calculate the current fraction of the whole grid
			int frac = plotDepth / depth;
			int ex = sx + frac;
			int ey = sy + frac;
			double tl = checkAndEvaluate(sx, sy);
			double bl = checkAndEvaluate(sx, ey);
			double tr = checkAndEvaluate(ex, sy);
			double br = checkAndEvaluate(ex, ey);

			// calculate all four coordinate based on current fraction, x
			// and y coordinate
			
			if (edgeConfig(tl, tr, br, bl) != EMPTY) {
				if (depth == plotDepth) {
					addSegment(coordx[sx], coordy[sy], coordx[ex], coordy[ey],
							tl, tr, br, bl);

					if ((sx & stepMask) == 0) {
						grid[currentY][currentX - 1] |= intersect(tl, bl);
					}
					if ((ex & stepMask) == 0) {
						grid[currentY][currentX + 1] |= intersect(tr, br);
					}
					if ((sy & stepMask) == 0) {
						grid[currentY - 1][currentX] |= intersect(tl, tr);
					}
					if ((ey & stepMask) == 0) {
						grid[currentY + 1][currentX] |= intersect(bl, br);
					}
				} else {
					createTree(sx, sy, depth << 1);
				}
			}
		}

		/**
		 * @param tl
		 *            value of the function evaluated at top-left corner
		 * @param tr
		 *            value of the function evaluated at top-right corner
		 * @param br
		 *            value of the function evaluated at bottom-right corner
		 * @param bl
		 *            value of the function evaluated at bottom-left corner
		 * @return edge configuration based on value of curve at all vertices of
		 *         the square
		 */
		private int edgeConfig(double tl, double tr, double br, double bl) {
			int config = (intersect(bl, tl) << 3) | (intersect(tl, tr) << 2)
					| (intersect(tr, br) << 1) | (intersect(br, bl));
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

		private boolean adjustX(GeoPointND pt, double x1, double x2, double y1,
				int d) {
			if (d == 0) {
				return false;
			}
			double d1 = evaluateImplicitCurve(x1, y1);
			double d2 = evaluateImplicitCurve(x2, y1);
			if (Kernel.isZero(d1)) {
				pt.setCoords(new Coords(x1, y1, 1.0), false);
			} else if (Kernel.isZero(d2)) {
				pt.setCoords(new Coords(x2, y1, 1.0), false);
			} else if (intersect(d1, d2) == 1) {
				if (!adjustX(pt, x1, interpolate(d1, d2, x1, x2), y1, d - 1)) {
					return adjustX(pt, interpolate(d1, d2, x1, x2), x2, y1,
							d - 1);
				}
			} else {
				return false;
			}
			return true;
		}

		private boolean adjustY(GeoPointND pt, double x1, double y1, double y2,
				int d) {
			if (d == 0) {
				return false;
			}
			double d1 = evaluateImplicitCurve(x1, y1);
			double d2 = evaluateImplicitCurve(x1, y2);
			if (Kernel.isZero(d1)) {
				pt.setCoords(new Coords(x1, y1, 1.0), false);
			} else if (Kernel.isZero(d2)) {
				pt.setCoords(new Coords(x1, y2, 1.0), false);
			} else if (intersect(d1, d2) == 1) {
				if (!adjustY(pt, x1, y1, (y1 + y2) * 0.5, d - 1)) {
					return adjustY(pt, x1, (y1 + y2) * 0.5, y2, d - 1);
				}
			} else {
				return false;
			}
			return true;
		}

		@Override
		public void polishPointOnPath(GeoPointND pt) {
			double px = onScreen(pt.getInhomX(), this.x, this.x + this.w);
			double py = onScreen(pt.getInhomY(), this.y, this.y + this.h);
			double d1 = evaluateImplicitCurve(px, py);
			if (!Kernel.isZero(d1)) {
				boolean adjusted = adjustX(pt, px, px + fracX, py, 8);
				if (!adjusted) {
					adjusted = adjustX(pt, px - fracX, px, py, 8);
				}
				if (!adjusted) {
					adjusted = adjustY(pt, px, py, py + fracY, 8);
				}
				if (!adjusted) {
					adjustY(pt, px, py - fracY, py, 8);
				}
			} else {
				pt.setCoords(new Coords(px, py, 1.0), false);
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
		// TODO Auto-generated method stub
		return null;
	}

	public void setCoeff(double[][] coeff) {
		// TODO Auto-generated method stub

	}

	public int getDeg() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isOnScreen() {
		return defined && locus.isDefined() && locus.getPoints().size() > 0;
	}

	public double evalPolyAt(double evaluate, double x) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getDegX() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getDegY() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setInputForm() {
		// TODO Auto-generated method stub

	}

	public double evalDiffXPolyAt(double inhomX, double inhomY) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evalDiffYPolyAt(double inhomX, double inhomY) {
		// TODO Auto-generated method stub
		return 0;
	}

}
