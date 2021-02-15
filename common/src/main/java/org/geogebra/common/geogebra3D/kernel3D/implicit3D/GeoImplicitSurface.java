package org.geogebra.common.geogebra3D.kernel3D.implicit3D;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoElement3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoTriangulatedSurface3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.Traversing.VariablePolyReplacer;
import org.geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoImplicitSurfaceND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.matrix.Coords3;
import org.geogebra.common.kernel.matrix.CoordsDouble3;
import org.geogebra.common.main.Feature;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * 
 * 
 */
public class GeoImplicitSurface extends GeoElement3D
		implements GeoImplicitSurfaceND, EquationValue {
	private static final boolean DEBUG = false;
	private static final Coords3 DUMMY_NORMAL = new CoordsDouble3(0, 0, 1.0);
	private boolean defined;
	private boolean hasDerivatives;
	private double[] evals = new double[3];
	private double[] normEval = new double[3];
	private GeoFunctionNVar expression;
	private GeoTriangulatedSurface3D surface3D;
	private FunctionNVar[] derivFunc = new FunctionNVar[3];
	private GeoFunctionNVar parametricFn;

	/**
	 * Create an empty GeoImplicitSurface
	 * 
	 * @param cons
	 *            {@link Construction}
	 */
	public GeoImplicitSurface(Construction cons) {
		super(cons);
		setAlphaValue(0.75f); // TODO remove that when construction default will
		// be created
		this.surface3D = new GeoTriangulatedSurface3D();
	}

	/**
	 * 
	 * @param cons
	 *            {@link Construction}
	 * @param eqn
	 *            {@link Equation}
	 */
	public GeoImplicitSurface(Construction cons, Equation eqn) {
		super(cons);
		setAlphaValue(0.75f); // TODO remove that when construction default will
								// be created
		this.surface3D = new GeoTriangulatedSurface3D();
		fromEquation(eqn);
		updateParametic(eqn);

	}

	private void updateParametic(Equation eqn) {
		ExpressionNode normal = eqn.getLHS().apply(Operation.MINUS,
				eqn.getRHS());
		normal = normal.deepCopy(cons.getKernel());
		String[] vars = { "x", "y", "z" };
		int[][] complement = { { 1, 2 }, { 0, 2 }, { 0, 1 } };
		FunctionVariable[] fVars = new FunctionVariable[3];
		Double[] coeff = new Double[3];
		for (int i = 0; i < 3; i++) {
			fVars[i] = new FunctionVariable(cons.getKernel(), vars[i]);
			normal.traverse(VariablePolyReplacer.getReplacer(fVars[i]));
			coeff[i] = normal.getCoefficient(fVars[i]);
		}
		for (int i = 0; i < 3; i++) {
			if (coeff[i] != null && !DoubleUtil.isZero(coeff[i])
					&& !Double.isNaN(coeff[i])) {
				MyDouble coef = new MyDouble(kernel, -coeff[i]);
				ExpressionNode m = new ExpressionNode(kernel,
						new ExpressionNode(kernel, normal, Operation.DIVIDE,
								coef),
						Operation.PLUS, fVars[i]);
				m.simplifyLeafs();
				FunctionNVar fun = new FunctionNVar(m,
						new FunctionVariable[] { fVars[complement[i][0]],
								fVars[complement[i][1]] });
				this.parametricFn = new GeoFunctionNVar(cons, fun);
				parametricFn.setShortLHS(vars[i]);
			}
		}
	}

	/**
	 * Copy Constructor
	 * 
	 * @param cons
	 *            {@link Construction}
	 * @param geoSurface
	 *            {@link GeoImplicitSurface}
	 */
	public GeoImplicitSurface(Construction cons,
			GeoImplicitSurface geoSurface) {
		this(cons);
		this.set(geoSurface);
	}

	/**
	 * @return true for implicit surfaces
	 */
	@Override
	public boolean isGeoImplicitSurface() {
		return true;
	}

	/**
	 * @param eqn
	 *            surface equation
	 */
	void fromEquation(Equation eqn) {
		setDefinition(eqn.wrap());

		ExpressionNode leftHandSide = eqn.getLHS();
		ExpressionNode rightHandSide = eqn.getRHS();

		ExpressionNode functionExpression = new ExpressionNode(kernel,
				leftHandSide, Operation.MINUS, rightHandSide);

		FunctionVariable x = new FunctionVariable(kernel, "x");
		FunctionVariable y = new FunctionVariable(kernel, "y");
		FunctionVariable z = new FunctionVariable(kernel, "z");
		VariableReplacer repl = VariableReplacer.getReplacer(kernel);

		VariableReplacer.addVars("x", x);
		VariableReplacer.addVars("y", y);
		VariableReplacer.addVars("z", z);

		functionExpression.traverse(repl);

		FunctionNVar fun = new FunctionNVar(functionExpression,
				new FunctionVariable[] { x, y, z });

		expression = new GeoFunctionNVar(cons, fun);
		defined = expression.isDefined();
		setDerivatives(x, y, z);
		updateSurface();
	}

	/**
	 * Set derivatives which are required to evaluate normals
	 */
	private void setDerivatives(FunctionVariable x, FunctionVariable y,
			FunctionVariable z) {
		FunctionNVar fn = expression.getFunction();
		if (fn == null) {
			this.hasDerivatives = false;
			return;
		}
		try {
			derivFunc[0] = fn.getDerivativeNoCAS(x, 1);
			derivFunc[1] = fn.getDerivativeNoCAS(y, 1);
			derivFunc[2] = fn.getDerivativeNoCAS(z, 1);
			this.hasDerivatives = true;
		} catch (Exception ex) {
			this.hasDerivatives = false;
		}
	}

	/**
	 * Check whether normal can be evaluated for the function using derivatives
	 * 
	 * @return true if normal can be evaluated
	 */
	public boolean isNormalEvaluable() {
		return hasDerivatives;
	}

	/**
	 * Evaluate normal at given points
	 * 
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 * @param z
	 *            z-coordinate
	 * @return direction of normal vector
	 */
	public Coords3 evaluateNormalAt(double x, double y, double z) {
		if (!hasDerivatives) {
			return DUMMY_NORMAL;
		}
		normEval[0] = x;
		normEval[1] = y;
		normEval[2] = z;
		CoordsDouble3 N = new CoordsDouble3();
		N.x = derivFunc[0].evaluate(normEval);
		N.y = derivFunc[1].evaluate(normEval);
		N.z = derivFunc[2].evaluate(normEval);
		if (N.isDefined()) {
			double mx = Math.max(Math.abs(N.x), Math.abs(N.y));
			mx = Math.max(mx, Math.abs(N.z));
			N.x = N.x / mx;
			N.y = N.y / mx;
			N.z = N.z / mx;
			return N.isDefined() ? N : DUMMY_NORMAL;
		}
		return DUMMY_NORMAL;
	}

	/**
	 * Evaluate normal at given coordinate
	 * 
	 * @param coords
	 *            3D Coordinate
	 * @return direction of normal
	 */
	public Coords3 evaluateNormalAt(Coords3 coords) {
		return evaluateNormalAt(coords.getXd(), coords.getYd(), coords.getZd());
	}

	/**
	 * Evaluate normal at coordinate c and store result in r
	 * 
	 * @param c
	 *            coordinate where normal is to be evaluated
	 * @param r
	 *            coordinate where output normal vector is stored
	 */
	public void evaluateNormalAt(Coords c, Coords r) {
		r.val[0] = 0;
		r.val[1] = 0;
		r.val[2] = 0;
		if (!hasDerivatives) {
			return;
		}
		double lt, rt, e = 1e-3, e2 = 2 * e;
		normEval[0] = c.val[0];
		normEval[1] = c.val[1];
		normEval[2] = c.val[2];
		r.val[0] = derivFunc[0].evaluate(normEval);
		r.val[1] = derivFunc[1].evaluate(normEval);
		r.val[2] = derivFunc[2].evaluate(normEval);
		for (int i = 0; i < 3; i++) {
			if (!MyDouble.isFinite(r.val[i])) {
				normEval[i] -= e;
				lt = evaluateAt(normEval);
				normEval[i] += e2;
				rt = evaluateAt(normEval);
				r.val[i] = (rt - lt) / e2;
			}
		}
		r.normalize(true);
	}

	/**
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param z
	 *            z coordinate
	 * @return value of the function at (x, y, z)
	 */
	public double evaluateAt(double x, double y, double z) {
		evals[0] = x;
		evals[1] = y;
		evals[2] = z;
		return evaluateAt(evals);
	}

	/**
	 * 
	 * @param xyz
	 *            {x, y, z} coordinate
	 * @return value of function at (x, y, z)
	 */
	public double evaluateAt(double[] xyz) {
		return expression.evaluate(xyz);
	}

	/**
	 * @return Surface3D
	 */
	public GeoTriangulatedSurface3D getSurface3D() {
		return surface3D;
	}

	@Override
	public Coords getLabelPosition() {
		return Coords.VZ;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.IMPLICIT_SURFACE_3D;
	}

	@Override
	public GeoElement copy() {
		GeoImplicitSurface surface = new GeoImplicitSurface(cons);
		surface.set(this);
		return surface;
	}

	@Override
	public void set(GeoElementND geo) {
		Equation equationCopy = (Equation) geo.getDefinition().unwrap()
				.deepCopy(kernel);
		fromEquation(equationCopy);
	}

	/**
	 * force to re-evaluate the surface
	 * 
	 * @param bounds
	 *            surface bound : {xmin, xmax, ymin, ymax, zmin, zmax, xscale,
	 *            yscale, zscale}
	 */
	public void updateSurface(double[] bounds) {
		if (isDefined()) {
			surface3D.clear();
			MarchingCube m = new MarchingCube(this);
			m.update(bounds);
		}
	}

	/**
	 * Force to re-evaluate the surface
	 */
	public void updateSurface() {
		double[] bounds = new double[9];
		double[] views = kernel.getViewBoundsForGeo(this);
		bounds[0] = views[0];
		bounds[1] = views[1];
		bounds[2] = views[2];
		bounds[3] = views[3];
		bounds[4] = kernel.getZmin(2);
		bounds[5] = kernel.getZmax(2);
		bounds[6] = views[4];
		bounds[7] = views[5];
		bounds[8] = kernel.getZscale(2);
		updateSurface(bounds);
	}

	@Override
	public FunctionNVar getExpression() {
		return expression.getFunction();
	}

	@Override
	public boolean isDefined() {
		return defined;
	}

	@Override
	public void setUndefined() {
		this.defined = false;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return getDefinition().toValueString(tpl);
	}

	@Override
	public String toString(StringTemplate tpl) {
		return label + ": " + toValueString(tpl);
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
	}

	@Override
	public boolean isEqual(GeoElementND geo) {
		return false;
	}

	@Override
	public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	@Override
	public boolean isTranslateable() {
		return true;
	}

	@Override
	public void dilate(NumberValue r, Coords S) {
		expression.dilate3D(r, S);
	}

	@Override
	public void translate(Coords v) {
		expression.translate3D(v);
	}

	@Override
	public void rotate(NumberValue r) {
		expression.rotate(r);
	}

	@Override
	public void rotate(NumberValue r, GeoPointND S) {
		expression.rotate(r, S);
	}

	@Override
	public void mirror(Coords Q) {
		expression.mirror3D(Q);
	}

	@Override
	public void mirror(GeoLineND g) {
		// TODO : implement it correctly
		// expression.mirror3D(g);
	}

	@Override
	public void mirror(GeoCoordSys2D plane) {
		// TODO implement mirror at plane
	}

	/**
	 * 
	 * @param debug
	 *            message to debug
	 */
	static void debug(String debug) {
		if (DEBUG) {
			Log.debug(debug);
		}
	}

	/**
	 * 
	 * @param array
	 *            list of double values
	 */
	static void debug(double... array) {
		debug(toString(array));
	}

	/**
	 * 
	 * @param coords
	 *            coordinates
	 * @return String representation of coordinate
	 */
	static String toString(double[] coords) {
		if (!DEBUG) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (int i = 0; i < coords.length; i++) {
			sb.append(coords[i]);
		}
		sb.append("}");
		return sb.toString();
	}

	private static abstract class ImplicitSurface {

		private static final int EMPTY_OR_INVALID = 0x1ff;

		protected GeoImplicitSurface s;

		private final int[][] EDGE_TABLE = new int[][] { {}, // 0x00, 0xff
				{ 0, 4, 3 }, // 0x01, 00000001
				{ 0, 5, 1 }, // 0x02, 00000010
				{ 1, 3, 4, 4, 5, 1 }, // 0x03, 00000011
				{ 1, 2, 6 }, // 0x04, 00000100
				{ 0, 4, 3, 1, 2, 6 }, // 0x05, 00000101
				{ 6, 5, 0, 0, 2, 6 }, // 0x06, 00000110
				{ 6, 5, 4, 6, 4, 3, 3, 2, 6 }, // 0x07, 0x00000111
				{ 2, 3, 7 }, // 0x08, 00001000
				{ 0, 4, 7, 7, 2, 0 }, // 0x09, 00001001
				{ 2, 3, 7, 0, 5, 1 }, // 0x0A, 00001010
				{ 5, 4, 7, 7, 2, 1, 1, 5, 7 }, // 0x0B, 00001011
				{ 1, 3, 7, 7, 6, 1 }, // 0x0C, 0x00001100
				{ 4, 7, 6, 6, 1, 0, 0, 4, 6 }, // 0x0D, 0x00001101
				{ 7, 6, 5, 5, 0, 3, 3, 7, 5 }, // 0x0E, 0x00001110
				{ 5, 4, 7, 7, 6, 5 }, // 0x0F, 00001111
				{ 4, 8, 11 }, // 0x10, 00010000
				{ 0, 8, 11, 0, 3, 11 }, // 0x11, 00010001
				{ 4, 8, 11, 0, 1, 5 }, // 0x12, 00010010
				{ 1, 3, 11, 1, 8, 11, 1, 8, 5 }, // 0x13, 00010011
				{ 4, 8, 11, 1, 2, 6 }, // 0x14, 00010100
				{ 0, 8, 11, 0, 3, 11, 1, 2, 6 }, // 0x15, 00010101
				{ 4, 8, 11, 2, 6, 5, 5, 0, 2 }, // 0x16, 00010110
				{ 8, 5, 11, 8, 10, 11, 5, 2, 3, 5, 2, 6 }, // 0x17, 000010111
				{ 4, 8, 11, 2, 3, 7 }, // 0x18, 00011000
				{ 0, 2, 8, 2, 8, 11, 11, 2, 7 }, // 0x19, 00011001
				{ 4, 8, 11, 2, 3, 7, 0, 1, 5 }, // 0x1A, 00011010
				{ 1, 5, 8, 1, 2, 8, 2, 8, 11, 2, 7, 11 }, // 0x1B, 00011011
				{ 4, 8, 11, 1, 3, 6, 3, 6, 7 }, // 0x1C, 00011100
				{ 0, 1, 8, 1, 6, 7, 1, 7, 8, 7, 8, 11 }, // 0x1D, 00011101
				{ 5, 6, 7, 0, 5, 7, 3, 7, 0, 4, 8, 11 }, // 0x1E, 00011110
				{ 5, 6, 7, 5, 8, 11, 5, 11, 7 }, // 0x1F, 00011111
				{ 5, 8, 9 }, // 0x20, 00100000
				{ 5, 8, 9, 0, 3, 4 }, // 0x21, 00100001
				{ 1, 8, 9, 1, 0, 8 }, // 0x22, 00100010
				{ 1, 3, 9, 9, 3, 8, 3, 8, 4 }, // 0x23, 00100011
				{ 5, 8, 9, 1, 2, 6 }, // 0x24, 00100100
				{ 5, 8, 9, 1, 2, 6, 0, 3, 4 }, // 0x25, 00100101
				{ 0, 2, 8, 8, 9, 2, 9, 2, 6 }, // 0x26, 00100110
				{ 4, 8, 9, 3, 4, 9, 2, 3, 6, 3, 6, 9 }, // 0x27, 00100111
				{ 5, 8, 9, 2, 3, 7 }, // 0x28, 00101000
				{ 5, 8, 9, 0, 4, 7, 0, 2, 7 }, // 0x29, 00101001
				{ 1, 8, 9, 1, 0, 8, 2, 3, 7 }, // 0x2A, 00101010
				{ 2, 4, 7, 2, 4, 8, 1, 2, 8, 1, 8, 9 }, // 0x2B, 00101011
				{ 5, 8, 9, 1, 3, 6, 3, 6, 7 }, // 0x2C, 00101100
				{ 4, 6, 7, 4, 6, 1, 4, 1, 0, 5, 8, 9 }, // 0x2D, 00101101
				{ 0, 3, 8, 3, 8, 9, 3, 7, 9, 6, 7, 9 }, // 0x2E, 00101110
				{ 4, 6, 7, 4, 8, 9, 4, 9, 6 }, // 0x2F, 00101111
				{ 4, 5, 9, 4, 9, 11 }, // 0x30, 00110000
				{ 3, 9, 11, 0, 3, 9, 0, 5, 9 }, // 0x31, 00110001
				{ 1, 9, 11, 0, 1, 11, 0, 4, 11 }, // 0x32, 00110010
				{ 1, 9, 11, 11, 3, 1 }, // 0x33, 00110011
				{ 4, 5, 9, 4, 9, 11, 1, 2, 6 }, // 0x34, 00110100
				{ 3, 9, 11, 0, 3, 9, 0, 5, 9, 1, 2, 6 }, // 0x35, 00110101
				{ 6, 8, 10, 4, 6, 8, 1, 4, 6, 1, 3, 4 }, // 0x36, 00110110
				{ 3, 9, 11, 3, 6, 9, 6, 2, 3 }, // 0x37, 00110111
				{ 4, 5, 9, 4, 9, 11, 2, 3, 7 }, // 0x38, 00111000
				{ 6, 9, 11, 4, 6, 9, 0, 2, 4, 2, 4, 6 }, // 0x39, 00111001
				{ 1, 9, 11, 0, 1, 11, 0, 4, 11, 2, 3, 7 }, // 0x3A, 00111010
				{ 1, 9, 11, 1, 2, 7, 1, 7, 11 }, // 0x3B, 00111011
				{ 4, 5, 9, 4, 9, 11, 1, 3, 6, 3, 6, 7 }, // 0x3C, 00111100
				{ 7, 9, 11, 6, 7, 9, 0, 1, 5 }, // 0x3D, 00111101
				{ 7, 9, 11, 6, 7, 9, 0, 3, 4 }, // 0x3E, 00111110
				{ 7, 9, 11, 6, 7, 9 }, // 0x3F, 00111111
				{ 6, 9, 10 }, // 0x40, 01000000
				{ 6, 9, 10, 0, 3, 4 }, // 0x41, 01000001
				{ 6, 9, 10, 0, 1, 5 }, // 0x42, 01000010
				{ 6, 9, 10, 1, 3, 4, 1, 4, 5 }, // 0x43, 01000011
				{ 1, 2, 9, 2, 9, 10 }, // 0x44, 01000100
				{ 1, 2, 9, 2, 9, 10, 0, 3, 4 }, // 0x45, 01000101
				{ 0, 2, 10, 0, 5, 10, 5, 9, 10 }, // 0x46, 01000110
				{ 4, 5, 9, 3, 4, 9, 2, 3, 10, 3, 9, 10 }, // 0x47, 01000111
				{ 6, 9, 10, 2, 3, 7 }, // 0x48, 01001000
				{ 6, 9, 10, 0, 4, 7, 0, 2, 7 }, // 0x49, 01001001
				{ 6, 9, 10, 2, 3, 7, 0, 1, 5 }, // 0x4A, 01001010
				{ 4, 5, 7, 2, 5, 7, 1, 2, 5, 6, 9, 10 }, // 0x4B, 01001011
				{ 1, 9, 3, 3, 9, 10, 3, 10, 7 }, // 0x4C, 01001100
				{ 4, 7, 10, 0, 4, 10, 0, 1, 9, 0, 9, 10 }, // 0x4D, 01001101
				{ 0, 3, 5, 3, 5, 9, 3, 7, 9, 7, 9, 10 }, // 0x4E, 01001110
				{ 4, 5, 7, 9, 10, 7, 9, 7, 5 }, // 0x4F, 01001111
				{ 6, 9, 10, 4, 8, 11 }, // 0x50, 01010000
				{ 6, 9, 10, 0, 8, 11, 0, 3, 11 }, // 0x51, 01010001
				{ 6, 9, 10, 4, 8, 11, 0, 1, 5 }, // 0x52, 01010010
				{ 1, 3, 11, 1, 8, 11, 1, 8, 5, 6, 9, 10 }, // 0x53, 01010011
				{ 1, 9, 10, 1, 2, 10, 3, 4, 11 }, // 0x54, 01010100
				{ 0, 8, 11, 0, 3, 11, 1, 9, 10, 1, 2, 10 }, // 0x55, 01010101
				{ 0, 2, 10, 0, 5, 10, 5, 9, 10, 4, 8, 11 }, // 0x56, 01010110
				{ 2, 3, 11, 2, 10, 11, 5, 8, 9 }, // 0x57, 01010111
				{ 2, 3, 7, 6, 9, 10, 4, 8, 11 }, // 0x58, 01011000
				{ 0, 2, 8, 2, 8, 11, 11, 2, 7, 6, 9, 10 }, // 0x59, 01011001
				{ 0, 1, 5, 2, 3, 7, 4, 8, 11, 6, 9, 10 }, // 0x5A, 01011010
				{ 1, 2, 6, 5, 8, 9, 7, 11, 10 }, // 0x5B, 01011011
				{ 1, 9, 3, 3, 9, 10, 3, 10, 7, 4, 8, 11 }, // 0x5C, 01011100
				{ 0, 1, 8, 1, 8, 9, 7, 10, 11 }, // 0x5D, 01011101
				{ 0, 3, 4, 5, 8, 9, 7, 10, 11 }, // 0x5E, 01011110
				{ 5, 8, 9, 7, 10, 11 }, // 0x5F, 01011111
				{ 5, 6, 10, 5, 8, 10 }, // 0x60, 01100000
				{ 5, 6, 10, 5, 8, 10, 0, 3, 4 }, // 0x61, 01100001
				{ 0, 8, 10, 0, 1, 6, 0, 6, 10 }, // 0x62, 01100010
				{ 6, 8, 10, 4, 6, 8, 1, 3, 4, 1, 4, 6 }, // 0x63, 01100011
				{ 2, 8, 10, 1, 2, 8, 1, 5, 8 }, // 0x64, 01100100
				{ 2, 8, 10, 1, 2, 8, 1, 5, 8, 0, 3, 4 }, // 0x65, 01100101
				{ 0, 2, 10, 0, 10, 8 }, // 0x66, 01100110
				{ 2, 8, 10, 2, 3, 4, 2, 4, 8 }, // 0x67, 01100111
				{ 5, 6, 10, 5, 8, 10, 2, 3, 7 }, // 0x68, 01101000
				{ 0, 4, 7, 0, 2, 7, 5, 6, 10, 5, 8, 10 }, // 0x69, 01101001
				{ 0, 8, 10, 0, 1, 6, 0, 6, 10, 2, 3, 7 }, // 0x6A, 01101010
				{ 4, 8, 10, 10, 7, 4, 1, 2, 6 }, // 0x6B, 01101011
				{ 7, 8, 10, 5, 7, 8, 1, 3, 5, 3, 5, 7 }, // 0x6C, 01101100
				{ 4, 8, 10, 10, 7, 4, 0, 1, 5 }, // 0x6D, 01101101
				{ 0, 8, 10, 0, 3, 10, 3, 7, 10 }, // 0x6E, 01101110
				{ 4, 8, 10, 10, 7, 4 }, // 0x6F, 01101111
				{ 4, 5, 6, 4, 6, 10, 4, 10, 11 }, // 0x70, 01110000
				{ 3, 10, 11, 0, 3, 10, 0, 6, 10, 0, 5, 6 }, // 0x71, 01110001
				{ 0, 1, 4, 1, 4, 6, 4, 6, 11, 6, 10, 11 }, // 0x72, 01110010
				{ 1, 3, 11, 1, 6, 10, 1, 10, 11 }, // 0x73, 01110011
				{ 1, 2, 10, 1, 10, 11, 1, 5, 11, 4, 5, 11 }, // 0x74, 01110100
				{ 3, 10, 11, 2, 3, 10, 0, 1, 5 }, // 0x75, 01110101
				{ 0, 2, 10, 0, 4, 10, 4, 10, 11 }, // 0x76, 01110110
				{ 3, 10, 11, 2, 3, 10 }, // 0x77, 01110111
				{ 4, 5, 6, 4, 6, 10, 4, 10, 11, 2, 3, 7 }, // 0x78, 01111000
				{ 7, 10, 11, 0, 5, 6, 0, 2, 6 }, // 0x79, 01111001
				{ 7, 10, 11, 0, 3, 4, 1, 2, 6 }, // 0x7A, 01111010
				{ 7, 10, 11, 1, 2, 6 }, // 0x7B, 01111011
				{ 7, 10, 11, 1, 5, 4, 4, 3, 1 }, // 0x7C, 01111100
				{ 7, 10, 11, 0, 1, 5 }, // 0x7D, 01111101
				{ 7, 10, 11, 0, 3, 4 }, // 0x7E, 01111110
				{ 7, 10, 11 }, // 0x7F, 01111111
		};

		protected double x1;
		protected double y1;
		protected double z1;
		protected double x2;
		protected double y2;
		protected double z2;

		protected double fracX;
		protected double fracY;
		protected double fracZ;
		protected double scaleX;
		protected double scaleY;
		protected double scaleZ;

		protected GeoTriangulatedSurface3D surf;

		private final Coords p1 = new Coords(0, 0, 0);
		private final Coords p2 = new Coords(0, 0, 0);
		private final Coords p3 = new Coords(0, 0, 0);
		private final Coords p4 = new Coords(0, 0, 0);
		private final Coords p5 = new Coords(0, 0, 0);
		private final Coords n1 = new Coords(0, 0, 0);
		private final Coords n2 = new Coords(0, 0, 0);
		private final Coords n3 = new Coords(0, 0, 0);

		public ImplicitSurface(GeoImplicitSurface s) {
			this.s = s;
		}

		public void update(double[] bounds) {
			this.x1 = bounds[0];
			this.y1 = bounds[2];
			this.z1 = bounds[4];
			this.x2 = bounds[1];
			this.y2 = bounds[3];
			this.z2 = bounds[5];
			this.scaleX = bounds[6];
			this.scaleY = bounds[7];
			this.scaleZ = bounds[8];
			this.surf = s.getSurface3D();
			this.update();
		}

		public abstract void update();

		public int config(Cube cube) {
			int config = cube.sign(Cube.V7);
			config = (config << 1) | cube.sign(Cube.V6);
			config = (config << 1) | cube.sign(Cube.V5);
			config = (config << 1) | cube.sign(Cube.V4);
			config = (config << 1) | cube.sign(Cube.V3);
			config = (config << 1) | cube.sign(Cube.V2);
			config = (config << 1) | cube.sign(Cube.V1);
			config = (config << 1) | cube.sign(Cube.V0);
			if (config <= 0 || config == 0xff) {
				return EMPTY_OR_INVALID;
			}
			return config > 0x7f ? (config ^ 0xff) : config;
		}

		public void addSurface(Cube cube) {
			int config = config(cube);
			double det;
			if (config != EMPTY_OR_INVALID) {
				int[] edges = EDGE_TABLE[config];
				int len = edges.length;
				for (int i = 0; i < len; i += 3) {
					surf.beginTriangulation();

					cube.pointOfIntersection(edges[i], p1.val);
					cube.pointOfIntersection(edges[i + 1], p2.val);
					cube.pointOfIntersection(edges[i + 2], p3.val);
					p2.sub(p1, p4);
					p3.sub(p1, p5);
					s.evaluateNormalAt(p1, n1);
					s.evaluateNormalAt(p2, n2);
					s.evaluateNormalAt(p3, n3);
					det = p4.dotCrossProduct(n1, p5);
					if (det < 0) {
						surf.insertPoint(p1.val, n1.val);
						surf.insertPoint(p2.val, n2.val);
						surf.insertPoint(p3.val, n3.val);
					} else {
						surf.insertPoint(p1.val, n1.val);
						surf.insertPoint(p3.val, n3.val);
						surf.insertPoint(p2.val, n2.val);
					}
					surf.endTriangulation();
				}
			}
		}
	}

	private static class MarchingCube extends ImplicitSurface {
		private static final int AVE_PXL = 40;
		private static final int MAX_SUB_DIV = 25;

		private int sizeX = 20;
		private int sizeY = 20;
		private int sizeZ = 20;

		public MarchingCube(GeoImplicitSurface s) {
			super(s);
		}

		private static int pixels(double c1, double c2, double scale) {
			return (int) Math.ceil((Math.abs(c1 - c2) * scale));
		}

		@Override
		public void update() {

			sizeX = Math.min(MAX_SUB_DIV, pixels(x1, x2, scaleX) / AVE_PXL + 1);
			sizeY = Math.min(MAX_SUB_DIV, pixels(y1, y2, scaleY) / AVE_PXL + 1);
			sizeZ = Math.min(MAX_SUB_DIV, pixels(z1, z2, scaleZ) / AVE_PXL + 1);
			debug("{x:" + sizeX + ";y:" + sizeY + ";z:" + sizeZ + "}");

			this.fracX = (x2 - x1) / sizeX;
			this.fracY = (y2 - y1) / sizeY;
			this.fracZ = (z2 - z1) / sizeZ;

			double[] xcoords = new double[sizeX + 1];
			double[] ycoords = new double[sizeY + 1];
			double[] zcoords = new double[sizeZ + 1];
			double[][] grid2d = new double[sizeY + 1][sizeX + 1];
			double[] grid1d = new double[sizeX + 1];

			double cur, prev;

			Cube cube = new Cube();

			for (int i = 0; i <= sizeX; i++) {
				xcoords[i] = x1 + i * fracX;
			}
			for (int i = 0; i <= sizeY; i++) {
				ycoords[i] = y1 + i * fracY;
			}
			for (int i = 0; i <= sizeZ; i++) {
				zcoords[i] = z1 + i * fracZ;
			}

			for (int i = 0; i <= sizeY; i++) {
				for (int j = 0; j <= sizeX; j++) {
					grid2d[i][j] = s.evaluateAt(xcoords[j], ycoords[i], z1);
				}
			}

			for (int k = 1; k <= sizeZ; k++) {
				for (int i = 0; i <= sizeX; i++) {
					grid1d[i] = s.evaluateAt(xcoords[i], y1, zcoords[k]);
				}
				cube.coords[Cube.Z1] = zcoords[k - 1];
				cube.coords[Cube.Z2] = zcoords[k];
				for (int i = 1; i <= sizeY; i++) {
					prev = s.evaluateAt(x1, ycoords[i], zcoords[k]);
					cube.coords[Cube.Y1] = ycoords[i - 1];
					cube.coords[Cube.Y2] = ycoords[i];
					for (int j = 1; j <= sizeX; j++) {
						cur = s.evaluateAt(xcoords[j], ycoords[i], zcoords[k]);
						cube.coords[Cube.X1] = xcoords[j - 1];
						cube.coords[Cube.X2] = xcoords[j];
						cube.cache[Cube.V0] = prev;
						cube.cache[Cube.V1] = cur;
						cube.cache[Cube.V2] = grid2d[i][j];
						cube.cache[Cube.V3] = grid2d[i][j - 1];
						cube.cache[Cube.V4] = grid1d[j - 1];
						cube.cache[Cube.V5] = grid1d[j];
						cube.cache[Cube.V6] = grid2d[i - 1][j];
						cube.cache[Cube.V7] = grid2d[i - 1][j - 1];
						super.addSurface(cube);
						grid2d[i - 1][j - 1] = grid1d[j - 1];
						grid1d[j - 1] = prev;
						prev = cur;
					}
					grid2d[i - 1][sizeX] = grid1d[sizeX];
					grid1d[sizeX] = prev;
				}
				System.arraycopy(grid1d, 0, grid2d[sizeY], 0, sizeX + 1);
			}
		}

	}

	// Here is vertices and edges numbering convention used throughout the
	// marching cube. Thus we can see vertices 7 and 1 map to (x1, y1, z1) and
	// (x2, y2, z2) respectively
	// ...........0___________0____________1
	// ........../|......................./|
	// ........./.|....................../.|
	// ......../..|...................../..|
	// .......4...|....................5...|
	// ....../....|.................../....|
	// ...../.....3................../.....1
	// ..../......|................./......|
	// ...4_______|____8___________5.......|
	// ...|.......|................|.......|
	// ...|.......|................|.......|
	// ...|.......3__________2_____|_______2
	// ...|....../.................|....../
	// ..11...../..................9...../
	// ...|..../...................|..../
	// ...|...7....................|...6
	// ...|../.....................|../
	// ...|./......................|./
	// ...7/___________10__________6/
	//
	private static class Cube {

		public static final int X1 = 0x00;
		public static final int Y1 = 0x01;
		public static final int Z1 = 0x02;
		public static final int X2 = 0x03;
		public static final int Y2 = 0x04;
		public static final int Z2 = 0x05;

		public static final int V0 = 0x00;
		public static final int V1 = 0x01;
		public static final int V2 = 0x02;
		public static final int V3 = 0x03;
		public static final int V4 = 0x04;
		public static final int V5 = 0x05;
		public static final int V6 = 0x06;
		public static final int V7 = 0x07;

		private static final int[][] EDGES = { { 0, 1 }, { 1, 2 }, { 2, 3 },
				{ 3, 0 }, { 0, 4 }, { 1, 5 }, { 2, 6 }, { 3, 7 }, { 4, 5 },
				{ 5, 6 }, { 6, 7 }, { 7, 4 } };

		private static final int[][] VERTICES = { { 0, 4, 5 }, { 3, 4, 5 },
				{ 3, 4, 2 }, { 0, 4, 2 }, { 0, 1, 5 }, { 3, 1, 5 }, { 3, 1, 2 },
				{ 0, 1, 2 } };

		/**
		 * Coordinates of the cube (x1, y1, z1) (x2, y2, z2)
		 */
		public double[] coords = new double[6];
		/**
		 * Cached evaluated value at each corner of the cube
		 */
		public double[] cache = new double[8];

		protected Cube() {

		}

		/**
		 * Fill point array with point of intersection of edge e with the plane.
		 * Caveat: It does not check if the given edge intersect with the
		 * surface
		 * 
		 * @param e
		 *            the edge number as per the convention
		 * @param pts
		 *            an array of length >= 3
		 */
		public void pointOfIntersection(int e, double[] pts) {
			int[] v = EDGES[e];
			int[] v1 = VERTICES[v[0]];
			int[] v2 = VERTICES[v[1]];
			double fa = eval(v[0]);
			double fb = eval(v[1]);
			if ((e & 4) == 4) {
				// y is changing (edges 4, 5, 6, and 7)
				pts[0] = coords[v1[0]];
				pts[1] = interpolate(fa, fb, coords[v1[1]], coords[v2[1]]);
				pts[2] = coords[v1[2]];
			} else if ((e & 1) == 1) {
				// z is changing (odd numbered edges)
				pts[0] = coords[v1[0]];
				pts[1] = coords[v1[1]];
				pts[2] = interpolate(fa, fb, coords[v1[2]], coords[v2[2]]);
			} else {
				// x is changing (even numbered edges)
				pts[0] = interpolate(fa, fb, coords[v1[0]], coords[v2[0]]);
				pts[1] = coords[v1[1]];
				pts[2] = coords[v1[2]];
			}
		}

		/**
		 * Sign of the vertex
		 * 
		 * @param vertex
		 *            vertex
		 * @return isFinite(v) ? ((v &lt;= 0) ? 0 : 1) : -1, where v is
		 *         evaluated value at given vertex
		 * 
		 */
		public int sign(int vertex) {
			double v = eval(vertex);
			if (MyDouble.isFinite(v)) {
				return v <= 0.0 ? 0 : 1;
			}
			return -1;
		}

		public double eval(int vertex) {
			return cache[vertex];
		}

		public static double interpolate(double fa, double fb, double p1,
				double p2) {
			double r = -fb / (fa - fb);
			if (r <= 1.0 && r >= 0.0) {
				return r * (p1 - p2) + p2;
			}
			return p2 + (p1 - p2) * 0.5;
		}
	}

	@Override
	public boolean isFillable() {
		return true;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.PARAMETRIC3D;
	}

	@Override
	public Equation getEquation() {
		return kernel.getAlgebraProcessor().parseEquation(this);
	}

	@Override
	public String[] getEquationVariables() {
		if (expression == null) {
			return null;
		}
		ArrayList<String> vars = new ArrayList<>();
		for (FunctionVariable var : expression.getFunctionVariables()) {
			if (expression.getFunctionExpression().contains(var)) {
				vars.add(var.toString(StringTemplate.defaultTemplate));
			}
		}
		return vars.toArray(new String[0]);
	}

	@Override
	final public void setToUser() {
		toStringMode = GeoLine.EQUATION_USER;
	}

	@Override
	public boolean setTypeFromXML(String style, String parameter, boolean force) {
		return false;
	}

	@Override
	public void setToImplicit() {
		// TODO Auto-generated method stub
	}

	@Override
	final public char getLabelDelimiter() {
		return ':';
	}

	@Override
	public boolean hasDrawable3D() {
		return kernel.getApplication().has(Feature.IMPLICIT_SURFACES);
	}

	/**
	 * @return parametric representation if it exists (null otherwise)
	 */
	public GeoFunctionNVar getParametric() {
		return parametricFn;
	}
}