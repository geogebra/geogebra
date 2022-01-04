package org.geogebra.common.geogebra3D.kernel3D.geos;

import java.util.TreeMap;

import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.AutoColor;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.RegionParameters;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.Functional2Var;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.ChangeableParent;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.kernel.kernelND.RotateableND;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.matrix.Coords3;
import org.geogebra.common.kernel.matrix.CoordsDouble3;
import org.geogebra.common.plugin.GeoClass;

/**
 * Class for cartesian curves in 3D
 * 
 * @author Mathieu
 * 
 */
public class GeoSurfaceCartesian3D extends GeoSurfaceCartesianND
		implements Functional2Var, Traceable, Region,
		MirrorableAtPlane, RotateableND, Transformable {
	private boolean isSurfaceOfRevolutionAroundOx = false;
	private CoordMatrix4x4 tmpMatrix4x4;
	private double[] xyzuv;
	private double lastHitU;
	private double lastHitV;
	private double[] tmp = new double[2];
	private boolean trace;
	private boolean hasLastHitParameters = false;

	private Coords der1 = new Coords(3);
	private Coords der2 = new Coords(3);
	private Coords normal = new Coords(3);
	private CoordsDouble3 p1 = new CoordsDouble3();
	private CoordsDouble3 p2 = new CoordsDouble3();
	private ChangeableParent changeableParent;

	/**
	 * empty constructor (for ConstructionDefaults3D)
	 * 
	 * @param c
	 *            construction
	 */
	public GeoSurfaceCartesian3D(Construction c) {
		super(c);
		isSurfaceOfRevolutionAroundOx = false;
	}

	/**
	 * common constructor
	 * 
	 * @param c
	 *            construction
	 * @param point
	 *            expression defining the surface
	 * @param fun
	 *            functions
	 */
	public GeoSurfaceCartesian3D(Construction c, ExpressionNode point,
			FunctionNVar[] fun) {
		super(c, point, fun);
		isSurfaceOfRevolutionAroundOx = false;
	}

	/**
	 * 
	 * @param surface
	 *            Surface to be copied
	 */
	public GeoSurfaceCartesian3D(GeoSurfaceCartesian3D surface) {
		super(surface.cons);
		set(surface);
	}

	@Override
	public void evaluatePoint(double u, double v, Coords3 p) {
		tmp[0] = u;
		tmp[1] = v;
		p.set(fun[0].evaluate(tmp), fun[1].evaluate(tmp), fun[2].evaluate(tmp));
	}

	private boolean setNormalFromNeighbours(Coords3 p, double u, double v,
			Coords3 n) {

		evaluatePoint(u + SurfaceEvaluable.NUMERICAL_DELTA, v, p1);
		if (!p1.isDefined()) {
			return false;
		}
		evaluatePoint(u, v + SurfaceEvaluable.NUMERICAL_DELTA, p2);
		if (!p2.isDefined()) {
			return false;
		}

		der1.setX(p1.x - p.getXd());
		der1.setY(p1.y - p.getYd());
		der1.setZ(p1.z - p.getZd());
		der2.setX(p2.x - p.getXd());
		der2.setY(p2.y - p.getYd());
		der2.setZ(p2.z - p.getZd());

		normal.setCrossProduct3(der1, der2);
		n.setNormalizedIfPossible(normal);

		return true;
	}

	@Override
	public boolean evaluateNormal(Coords3 p, double u, double v, Coords3 n) {
		tmp[0] = u;
		tmp[1] = v;

		double val;
		for (int i = 0; i < 3; i++) {
			val = fun1evaluate(0, i, tmp);
			if (Double.isNaN(val)) {
				return setNormalFromNeighbours(p, u, v, n);
			}
			der1.set(i + 1, val);

			val = fun1evaluate(1, i, tmp);
			if (Double.isNaN(val)) {
				return setNormalFromNeighbours(p, u, v, n);
			}
			der2.set(i + 1, val);
		}

		normal.setCrossProduct3(der1, der2);
		n.setNormalizedIfPossible(normal);

		return true;
	}

	/**
	 * set the jacobian matrix for bivariate newton method
	 * 
	 * @param uv
	 *            parameter values
	 * @param vx
	 *            direction x
	 * @param vy
	 *            direction y
	 * @param vz
	 *            direction z
	 * @param matrix
	 *            output matrix
	 */
	public void setJacobianForBivariate(double[] uv, double vx, double vy,
			double vz, CoordMatrix matrix) {

		final double dfxu = fun1evaluate(0, 0, uv);
		final double dfyu = fun1evaluate(0, 1, uv);
		final double dfzu = fun1evaluate(0, 2, uv);
		final double dfxv = fun1evaluate(1, 0, uv);
		final double dfyv = fun1evaluate(1, 1, uv);
		final double dfzv = fun1evaluate(1, 2, uv);

		matrix.set(1, 1, vz * dfyu - vy * dfzu);
		matrix.set(1, 2, vz * dfyv - vy * dfzv);

		matrix.set(2, 1, vx * dfzu - vz * dfxu);
		matrix.set(2, 2, vx * dfzv - vz * dfxv);

	}

	/**
	 * set vector for bivariate newton method ie
	 * 
	 * vector = this(u,v) (X) v + c
	 * 
	 * @param uv
	 *            parameters
	 * @param xyz
	 *            helper array
	 * @param vx
	 *            x(v)
	 * @param vy
	 *            y(v)
	 * @param vz
	 *            z(v)
	 * @param cx
	 *            x(c)
	 * @param cy
	 *            y(c)
	 * @param cz
	 *            z(c)
	 * @param vector
	 *            output vector
	 */
	public void setVectorForBivariate(double[] uv, double[] xyz, double vx,
			double vy, double vz, double cx, double cy, double cz,
			Coords vector) {

		xyz[0] = fun[0].evaluate(uv);
		xyz[1] = fun[1].evaluate(uv);
		xyz[2] = fun[2].evaluate(uv);

		vector.setX(vz * xyz[1] - vy * xyz[2] + cx);
		vector.setY(vx * xyz[2] - vz * xyz[0] + cy);
		vector.setZ(vy * xyz[0] - vx * xyz[1] + cz);
	}

	@Override
	public GeoElement copy() {
		return new GeoSurfaceCartesian3D(this);
	}

	@Override
	public boolean isEqual(GeoElementND geo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void set(GeoElementND geo) {
		super.set(geo);
		if (geo instanceof GeoSurfaceCartesian3D) {
			GeoSurfaceCartesian3D geoSurface = (GeoSurfaceCartesian3D) geo;
			isSurfaceOfRevolutionAroundOx = geoSurface.isSurfaceOfRevolutionAroundOx;
		}
	}

	@Override
	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.SURFACECARTESIAN3D;
	}

	@Override
	public Coords getLabelPosition() {
		return Coords.O; // TODO
	}

	@Override
	public Coords getMainDirection() {
		return Coords.VZ; // TODO
	}

	@Override
	public boolean isGeoElement3D() {
		return true;
	}

	/** to be able to fill it with an alpha value */
	@Override
	public boolean isFillable() {
		return true;
	}

	// /////////////////////////
	// FUNCTIONAL2VAR

	/**
	 * evaluate point at parameters u,v
	 * 
	 * @param u
	 *            first parameter
	 * @param v
	 *            second parameter
	 * @param p
	 *            point
	 */
	@Override
	public void evaluatePoint(double u, double v, Coords p) {
		tmp[0] = u;
		tmp[1] = v;
		for (int i = 0; i < 3; i++) {
			p.set(i + 1, fun[i].evaluate(tmp));
		}
	}

	@Override
	public Coords evaluateNormal(double u, double v) {

		Coords n = new Coords(4);

		tmp[0] = u;
		tmp[1] = v;

		double val;
		for (int i = 0; i < 3; i++) {
			val = fun1evaluate(0, i, tmp);
			der1.set(i + 1, val);

			val = fun1evaluate(1, i, tmp);
			der2.set(i + 1, val);
		}

		n.setCrossProduct4(der1, der2);
		n.normalize();
		return n;
	}

	// /////////////////////////
	// SPECIFIC XML

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		// line style
		getLineStyleXML(sb);

		// level of detail
		if (getLevelOfDetail() == LevelOfDetail.QUALITY) {
			sb.append("\t<levelOfDetailQuality val=\"true\"/>\n");
		}
	}

	// ////////////////
	// TRACE
	// ////////////////
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
	final public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	@Override
	public void setAllVisualPropertiesExceptEuclidianVisible(GeoElement geo,
			boolean keepAdvanced, boolean setAuxiliaryProperty) {
		super.setAllVisualPropertiesExceptEuclidianVisible(geo, keepAdvanced,
				setAuxiliaryProperty);

		if (geo.hasLevelOfDetail()) {
			setLevelOfDetail(((SurfaceEvaluable) geo).getLevelOfDetail());
		}
	}

	@Override
	public ValueType getValueType() {
		return ValueType.PARAMETRIC3D;
	}

	@Override
	public ExpressionValue evaluateSurface(double u, double v) {
		tmp[0] = u;
		tmp[1] = v;
		return new Geo3DVec(kernel, fun[0].evaluate(tmp), fun[1].evaluate(tmp),
				fun[2].evaluate(tmp));
	}

	@Override
	public void pointChangedForRegion(GeoPointND P) {

		GeoPoint3D p = (GeoPoint3D) P;

		// use last hit parameters if exist
		if (hasLastHitParameters()) {
			RegionParameters rp = P.getRegionParameters();
			rp.setT1(lastHitU);
			rp.setT2(lastHitV);
			Coords c = Coords.createInhomCoorsInD3();
			evaluatePoint(lastHitU, lastHitV, c);
			setDerivatives();
			Coords n = evaluateNormal(lastHitU, lastHitV);
			rp.setNormal(n);
			p.setCoords(c, false);
			p.updateCoords();
			p.setWillingCoordsUndefined();
			p.setWillingDirectionUndefined();
			resetLastHitParameters();
			return;
		}

		Coords coords, direction;
		if (p.hasWillingCoords()) { // use willing coords
			coords = p.getWillingCoords();
		} else {
			// use real coords
			coords = p.getInhomCoordsInD3();
		}

		if (xyzuv == null) {
			xyzuv = new double[5];
		}

		// use willing direction if exist
		if (p.hasWillingDirection()) {
			direction = p.getWillingDirection();
			RegionParameters rp = p.getRegionParameters();

			if (getClosestParameters(rp.getT1(), rp.getT2(), coords.getX(),
					coords.getY(), coords.getZ(), direction.getX(),
					direction.getY(), direction.getZ(), xyzuv)) {

				rp.setT1(xyzuv[3]);
				rp.setT2(xyzuv[4]);
				Coords n = evaluateNormal(xyzuv[3], xyzuv[4]);
				rp.setNormal(n);
				p.setCoords(new Coords(xyzuv[0], xyzuv[1], xyzuv[2], 1), false);
				p.updateCoords();
			}

			p.setWillingCoordsUndefined();
			p.setWillingDirectionUndefined();
			resetLastHitParameters();
			return;
		}

		// find closest point, looking for zero normal
		getClosestParameters(coords.getX(), coords.getY(), coords.getZ(),
				xyzuv);

		RegionParameters rp = p.getRegionParameters();
		rp.setT1(xyzuv[3]);
		rp.setT2(xyzuv[4]);
		Coords n = evaluateNormal(xyzuv[3], xyzuv[4]);
		rp.setNormal(n);
		p.setCoords(new Coords(xyzuv[0], xyzuv[1], xyzuv[2], 1), false);
		p.updateCoords();
		p.setWillingCoordsUndefined();
		p.setWillingDirectionUndefined();
		resetLastHitParameters();

	}

	/**
	 * find best point on surface colinear to (x0,y0,z0) point in (vx,vy,vz)
	 * direction
	 * 
	 * @param x0
	 *            origin x
	 * @param xMax
	 *            max x value
	 * @param y0
	 *            origin y
	 * @param z0
	 *            origin z
	 * @param vx
	 *            vector x
	 * @param vy
	 *            vector y
	 * @param vz
	 *            vector z
	 * @param vSquareNorm
	 *            vector square norm
	 * @param xyzuvOut
	 *            (x,y,z,u,v) best point coords and parameters
	 * @return true if point found
	 */
	public boolean getBestColinear(double x0, double xMax, double y0, double z0,
			double vx, double vy, double vz, double vSquareNorm,
			double[] xyzuvOut) {
		if (jacobian == null) {
			jacobian = new CoordMatrix(2, 2);
			bivariateVector = new Coords(3);
			bivariateDelta = new Coords(2);
			uv = new double[2];
			xyz = new double[3];

		}

		// we use bivariate newton method:
		// A(x0,y0,z0) and B(x1,y1,z1) delimits the hitting segment
		// M(u,v) is a point on the surface
		// we want vector product AM*AB to equal 0, so A, B, M are colinear
		// we only check first and second values of AM*AB since third will
		// be a consequence

		double gxc = z0 * vy - vz * y0;
		double gyc = x0 * vz - vx * z0;
		double gzc = y0 * vx - vy * x0;

		double uMin, uMax;
		if (isSurfaceOfRevolutionAroundOx) {
			uMin = x0;
			uMax = xMax;
		} else {
			uMin = getMinParameter(0);
			uMax = getMaxParameter(0);
		}

		double vMin = getMinParameter(1);
		double vMax = getMaxParameter(1);

		double finalError = Double.NaN;
		double dotProduct = -1;

		// make several tries
		double du = (uMax - uMin) / BIVARIATE_SAMPLES;
		double dv = (vMax - vMin) / BIVARIATE_SAMPLES;
		for (int ui = 0; ui <= BIVARIATE_SAMPLES; ui++) {
			uv[0] = uMin + ui * du;
			for (int vi = 0; vi <= BIVARIATE_SAMPLES; vi++) {
				uv[1] = vMin + vi * dv;
				double error = findBivariateColinear(x0, y0, z0, vx, vy, vz,
						vSquareNorm, gxc, gyc, gzc, uv);
				if (!Double.isNaN(error)) {
					// check if the hit point is in the correct direction
					double d = (xyz[0] - x0) * vx + (xyz[1] - y0) * vy
							+ (xyz[2] - z0) * vz;
					if (d >= 0) {
						if (dotProduct < 0 || d < dotProduct) {
							dotProduct = d;
							finalError = error;
							xyzuvOut[0] = xyz[0];
							xyzuvOut[1] = xyz[1];
							xyzuvOut[2] = xyz[2];
							xyzuvOut[3] = uv[0];
							xyzuvOut[4] = uv[1];
						}
					}
				}
			}
		}

		return !Double.isNaN(finalError);
	}

	private double findBivariateColinear(final double x0, final double y0,
			final double z0, final double vx, final double vy, final double vz,
			final double vSquareNorm, final double gxc, final double gyc,
			final double gzc, double[] uvParams) {

		for (int i = 0; i < BIVARIATE_JUMPS; i++) {

			// calc angle vector between hitting direction and hitting
			// origin-point on surface
			setVectorForBivariate(uvParams, xyz, vx, vy, vz, gxc, gyc, gzc,
					bivariateVector);

			double dx = xyz[0] - x0;
			double dy = xyz[1] - y0;
			double dz = xyz[2] - z0;
			double d = dx * dx + dy * dy + dz * dz;
			double error = bivariateVector.dotproduct3(bivariateVector);

			// check if sin(angle)^2 is small enough, then stop
			if (error < Kernel.STANDARD_PRECISION * vSquareNorm * d) {
				return error;
			}

			// set jacobian matrix and solve it
			setJacobianForBivariate(uvParams, vx, vy, vz, jacobian);
			jacobian.pivotDegenerate(bivariateDelta, bivariateVector);

			// if no solution, dismiss
			if (!bivariateDelta.isDefined()) {
				return Double.NaN;
			}

			// calc new parameters
			uvParams[0] -= bivariateDelta.getX();
			uvParams[1] -= bivariateDelta.getY();

			// check bounds
			randomBackInIntervalsIfNeeded(uvParams);
		}

		return Double.NaN;
	}

	@Override
	public void regionChanged(GeoPointND P) {
		pointChangedForRegion(P);
	}

	@Override
	public boolean isInRegion(GeoPointND P) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInRegion(double x0, double y0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * reset last hitted parameters
	 */
	public void resetLastHitParameters() {
		hasLastHitParameters = false;
	}

	private boolean hasLastHitParameters() {
		return hasLastHitParameters;
	}

	/**
	 * set last hit u,v parameters
	 * 
	 * @param u
	 *            first parameter
	 * @param v
	 *            second parameter
	 */
	public void setLastHitParameters(double u, double v) {
		lastHitU = u;
		lastHitV = v;
		hasLastHitParameters = true;
	}

	/**
	 * 
	 * @return true if surface of revolution around Ox by definition
	 */
	@Override
	public boolean isSurfaceOfRevolutionAroundOx() {
		return isSurfaceOfRevolutionAroundOx;
	}

	/**
	 * set this to be a surface of revolution around Ox
	 * 
	 * @param flag
	 *            flag
	 */
	public void setIsSurfaceOfRevolutionAroundOx(boolean flag) {
		isSurfaceOfRevolutionAroundOx = flag;
	}

	@Override
	public boolean showLineProperties() {
		return true;
	}

	@Override
	public int getMinimumLineThickness() {
		return 0;
	}

	@Override
	public boolean hasFillType() {
		return false;
	}

	@Override
	public void setLineThicknessOrVisibility(final int th) {
		setLineThickness(th);
	}

	@Override
	public void printCASEvalMapXML(StringBuilder sb) {
		// fun.printCASevalMapXML(sb);
	}

	@Override
	public void updateCASEvalMap(TreeMap<String, String> map) {
		// TODO
	}

	/**
	 * @param fun
	 *            array of coordinate functions
	 */
	public void setFun(FunctionNVar[] fun) {
		this.fun = fun;
		this.fun1 = null;
		this.fun2 = null;

	}

	@Override
	public void mirror(GeoLineND line) {
		SurfaceTransform.mirror(fun, kernel, line);
	}

	@Override
	public void mirror(GeoCoordSys2D plane) {
		SurfaceTransform.mirror(fun, kernel, plane);
	}

	@Override
	public void rotate(NumberValue r, GeoPointND S) {
		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}
		SurfaceTransform.rotate(fun, kernel, r, S, tmpMatrix4x4);
	}

	@Override
	public void rotate(NumberValue r) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}
		SurfaceTransform.rotate(fun, kernel, r, tmpMatrix4x4);

	}

	@Override
	public void rotate(NumberValue r, GeoPointND S,
			GeoDirectionND orientation) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}
		SurfaceTransform.rotate(fun, kernel, r, S, orientation, tmpMatrix4x4);
	}

	// private void transform(CoordMatrix4x4 m) {
	//
	// SurfaceTransform.transform(fun, kernel, m);
	//
	// }

	@Override
	public void rotate(NumberValue r, GeoLineND line) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}

		SurfaceTransform.rotate(fun, kernel, r, line, tmpMatrix4x4);

	}

	@Override
	public AutoColor getAutoColorScheme() {
		return AutoColor.SURFACES;
	}

	@Override
	public boolean hasChangeableParent3D() {
		return changeableParent != null;
	}

	@Override
	public ChangeableParent getChangeableParent3D() {
		return changeableParent;
	}

	/**
	 * @param cp
	 *            changeable parent
	 */
	final public void setChangeableParent(ChangeableParent cp) {
		changeableParent = cp;
	}

}
