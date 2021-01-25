package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidianForPlane.EuclidianViewForPlaneCompanionInterface;
import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.RotateableND;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.DoubleUtil;

/**
 * @author ggb3D
 * 
 */
public class GeoConic3D extends GeoConicND
		implements RotateableND, MirrorableAtPlane, ViewCreator {

	/** 2D coord sys where the conic exists */
	private CoordSys coordSys = new CoordSys(2);
	private Coords midpoint3D;
	private CoordMatrix4x4 tmpMatrix4x4;

	private EuclidianViewForPlaneCompanionInterface euclidianViewForPlane;

	/**
	 * Creates an empty 3D conic with 2D coord sys
	 * 
	 * @param c
	 *            construction
	 * @param cs
	 *            2D coord sys
	 */
	public GeoConic3D(Construction c, CoordSys cs) {
		this(c);
		setCoordSys(cs);
	}

	/**
	 * Copy constructor
	 * 
	 * @param conic
	 *            original
	 */
	public GeoConic3D(GeoConicND conic) {
		this(conic.getConstruction());
		set(conic);
	}

	/**
	 * Creates an empty 3D conic with 2D coord sys
	 * 
	 * @param c
	 *            construction
	 */
	public GeoConic3D(Construction c) {
		this(c, false);
	}

	/**
	 * 
	 * @param c
	 *            construction
	 * @param isIntersection
	 *            if this is an intersection curve
	 */
	public GeoConic3D(Construction c, boolean isIntersection) {
		super(c, 2, isIntersection, GeoConicND.EQUATION_PARAMETRIC);
	}

	@Override
	protected void createFields(int dimension) {
		midpoint3D = Coords.createInhomCoorsInD3();
		super.createFields(dimension);
	}

	// ///////////////////////////////////////
	// link with the 2D coord sys

	/**
	 * set the 2D coordinate system
	 * 
	 * @param cs
	 *            the 2D coordinate system
	 */
	public void setCoordSys(CoordSys cs) {

		// Application.printStacktrace(cs.getMatrixOrthonormal().toString());
		this.coordSys = cs;
	}

	@Override
	public CoordSys getCoordSys() {
		return coordSys;
	}

	/*
	 * private Coords midpoint2D;
	 * 
	 * /** sets the coords of the 2D midpoint
	 * 
	 * @param coords
	 * 
	 * public void setMidpoint2D(Coords coords){ midpoint2D=coords; }
	 * 
	 * public Coords getMidpoint2D(){ return midpoint2D; }
	 */

	@Override
	public Coords getMainDirection() {
		return coordSys.getNormal();
	}

	// ///////////////////////////////////////
	// GeoConicND

	/*
	 * public Coords getMidpoint2D(){ return
	 * coordSys.getPoint(super.getMidpoint2D());
	 * 
	 * }
	 */

	@Override
	public Coords getEigenvec3D(int i) {
		return coordSys.getVector(super.getEigenvec(i));
	}

	@Override
	public Coords getMidpointND() {
		return getMidpoint3D();
	}

	@Override
	public Coords getMidpoint3D() {
		return coordSys.getPoint(super.getMidpoint2D(), midpoint3D);
	}

	@Override
	public Coords getDirection3D(int i) {
		return getCoordSys().getVector(lines[i].y, -lines[i].x);
	}

	@Override
	public Coords getOrigin3D(int i) {
		return getCoordSys().getPoint(startPoints[i].x, startPoints[i].y);
	}

	// ///////////////////////////////////////
	// GeoConic3D
	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.CONIC3D;
	}

	/**
	 * it's a 3D GeoElement.
	 * 
	 * @return true
	 */
	@Override
	public boolean isGeoElement3D() {
		return true;
	}

	@Override
	public boolean hasFillType() {
		return false;
	}

	@Override
	public String toString(StringTemplate tpl) {

		StringBuilder sbToString = new StringBuilder();

		switch (getType()) {
		case CONIC_CIRCLE:
		case CONIC_ELLIPSE:
		case CONIC_HYPERBOLA:
		case CONIC_PARABOLA:
			sbToString.setLength(0);
			sbToString.append(label);
			sbToString.append(": ");
			// GeoFunction.initStringBuilder(sbToString, tpl, label, "t",
			// isLabelSet(), false);
			break;
		default:
			sbToString.setLength(0);
			sbToString.append(label);
			sbToString.append(": ");
			break;
		}

		sbToString.append(buildValueString(tpl));
		return sbToString.toString();
	}

	@Override
	protected StringBuilder buildValueString(StringTemplate tpl) {

		return buildParametricValueString(tpl, 3);

	}

	@Override
	public void setSphereND(GeoPointND M, GeoSegmentND segment) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setSphereND(GeoPointND M, GeoPointND P) {
		// TODO Auto-generated method stub

	}

	/**
	 * set the conic as single point equal to m
	 * 
	 * @param m
	 *            point
	 */
	public void setSinglePoint(GeoPointND m) {

		// coordSys.setSimpleCoordSysWithOrigin(m.getInhomCoordsInD3());

		// set midpoint as projection of m on the current coord sys
		setMidpoint(
				coordSys.getNormalProjection(m.getInhomCoordsInD3())[1].get());

		setSinglePointMatrix();

		singlePoint();

	}

	/**
	 * set the conic as single point equal to coords
	 * 
	 * @param coords
	 *            point
	 */
	public void setSinglePoint(Coords coords) {

		coordSys.setSimpleCoordSysWithOrigin(coords);

		// set midpoint as projection of m on the current coord sys
		setMidpoint(0, 0);

		setSinglePointMatrix();

		singlePoint();

	}

	private void setSinglePointMatrix() {
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = 0;
		}

		for (int i = 0; i < 3; i++) {
			matrix[i] = 1.0d;
		}
	}

	/**
	 * set this to sigle point at m location
	 * 
	 * @param conic
	 *            conic which will be single point
	 * @param m
	 *            point
	 */
	static final public void setSinglePoint(GeoConic3D conic, Coords m) {

		CoordSys cs = conic.getCoordSys();
		if (cs == null) {
			cs = new CoordSys(2);
			conic.setCoordSys(cs);
		}
		cs.resetCoordSys();
		cs.addPoint(m);
		cs.completeCoordSys2D();
		cs.makeOrthoMatrix(false, false);

		conic.setMidpoint(new double[] { 0, 0 });

		conic.setSinglePointMatrix();

		conic.singlePoint();

	}

	@Override
	public GeoElement copy() {
		return new GeoConic3D(this);
	}

	/*
	 * protected String getTypeString() { switch (type) { case
	 * GeoConic.CONIC_CIRCLE: return "Circle"; default: return "Conic3D"; }
	 * 
	 * 
	 * }
	 */

	@Override
	public boolean isEqual(GeoElementND Geo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void set(GeoElementND geo) {

		if (geo instanceof GeoConicND) {
			super.set(geo);
			if (coordSys == null) {
				coordSys = new CoordSys(2);
			}
			coordSys.set(((GeoConicND) geo).getCoordSys());
			setIsEndOfQuadric(((GeoConicND) geo).isEndOfQuadric());
		}

	}

	// //////////////////////////////////
	// XML
	// //////////////////////////////////

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		if (getCoordSys() != null && isIndependent()) {

			Coords v0 = getCoordSys().getV(0);
			Coords v1 = getCoordSys().getV(1);
			Coords origin = getCoordSys().getOrigin();

			sb.append("\t<coords ox=\"");
			sb.append(origin.get(1));
			sb.append("\" oy=\"");
			sb.append(origin.get(2));
			sb.append("\" oz=\"");
			sb.append(origin.get(3));
			sb.append("\" ow=\"");
			sb.append(origin.get(4));
			sb.append("\" vx=\"");
			sb.append(v0.get(1));
			sb.append("\" vy=\"");
			sb.append(v0.get(2));
			sb.append("\" vz=\"");
			sb.append(v0.get(3));
			sb.append("\" wx=\"");
			sb.append(v1.get(1));
			sb.append("\" wy=\"");
			sb.append(v1.get(2));
			sb.append("\" wz=\"");
			sb.append(v1.get(3));
			sb.append("\"/>\n");
		}
		// curve thickness and type printed by conicND
		super.getXMLtags(sb);

	}

	// //////////////////////////////////
	// GeoCoordSys2D
	// //////////////////////////////////

	@Override
	public Coords getPoint(double x2d, double y2d, Coords coords) {
		return getCoordSys().getPoint(x2d, y2d, coords);
	}

	@Override
	public Coords[] getNormalProjection(Coords coords) {
		return getCoordSys().getNormalProjection(coords);
	}

	/**
	 * @param coords
	 *            projected point
	 * @param willingDirection
	 *            direction of projection
	 * @return projected point
	 */
	public Coords[] getProjection(Coords coords, Coords willingDirection) {

		Coords[] result = new Coords[] { new Coords(4), new Coords(4) };

		coords.projectPlaneThruV(getCoordSys().getMatrixOrthonormal(),
				willingDirection, result[0], result[1]);

		return result;
	}

	// //////////////////////////////////
	// GeoCoordSys2D
	// //////////////////////////////////

	/**
	 * 
	 * @return true if is an intersection curve
	 */
	public boolean isIntersection() {
		return isIntersection;
	}

	@Override
	protected void doTranslate(Coords v) {
		coordSys.translate(v);
	}

	@Override
	public void matrixTransform(double a00, double a01, double a10,
			double a11) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = CoordMatrix4x4.identity();
		} else {
			tmpMatrix4x4.set(1, 3, 0);
			tmpMatrix4x4.set(1, 4, 0);

			tmpMatrix4x4.set(2, 3, 0);
			tmpMatrix4x4.set(2, 4, 0);

			tmpMatrix4x4.set(3, 1, 0);
			tmpMatrix4x4.set(3, 2, 0);
			tmpMatrix4x4.set(3, 3, 0);
			tmpMatrix4x4.set(3, 4, 0);

			tmpMatrix4x4.set(4, 1, 0);
			tmpMatrix4x4.set(4, 2, 0);
			tmpMatrix4x4.set(4, 3, 0);
			tmpMatrix4x4.set(4, 4, 1);
		}

		tmpMatrix4x4.set(1, 1, a00);
		tmpMatrix4x4.set(1, 2, a01);
		tmpMatrix4x4.set(2, 1, a10);
		tmpMatrix4x4.set(2, 2, a11);

		double[] ret = getCoordSys().matrixTransform(tmpMatrix4x4);

		super.matrixTransform(ret[0], ret[1], 0, ret[2]);
	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = CoordMatrix4x4.identity();
		} else {
			tmpMatrix4x4.set(1, 4, 0);
			tmpMatrix4x4.set(2, 4, 0);
			tmpMatrix4x4.set(3, 4, 0);

			tmpMatrix4x4.set(4, 1, 0);
			tmpMatrix4x4.set(4, 2, 0);
			tmpMatrix4x4.set(4, 3, 0);
			tmpMatrix4x4.set(4, 4, 1);
		}

		tmpMatrix4x4.set(1, 1, a00);
		tmpMatrix4x4.set(1, 2, a01);
		tmpMatrix4x4.set(1, 3, a02);

		tmpMatrix4x4.set(2, 1, a10);
		tmpMatrix4x4.set(2, 2, a11);
		tmpMatrix4x4.set(2, 3, a12);

		tmpMatrix4x4.set(3, 1, a20);
		tmpMatrix4x4.set(3, 2, a21);
		tmpMatrix4x4.set(3, 3, a22);

		double[] ret = getCoordSys().matrixTransform(tmpMatrix4x4);

		super.matrixTransform(ret[0], ret[1], 0, ret[2]);

	}

	@Override
	final public void rotate(NumberValue phiVal) {
		coordSys.rotate(phiVal.getDouble(), Coords.O);
	}

	@Override
	final public void rotate(NumberValue phiVal, GeoPointND Q) {
		coordSys.rotate(phiVal.getDouble(), Q.getInhomCoordsInD3());
	}

	@Override
	public void rotate(NumberValue phiVal, GeoPointND Q,
			GeoDirectionND orientation) {

		rotate(phiVal, Q.getInhomCoordsInD3(), orientation.getDirectionInD3());

	}

	@Override
	public void rotate(NumberValue phiVal, GeoLineND line) {

		rotate(phiVal, line.getStartInhomCoords(), line.getDirectionInD3());

	}

	final private void rotate(NumberValue phiVal, Coords center,
			Coords direction) {
		coordSys.rotate(phiVal.getDouble(), center, direction.normalized());
	}

	@Override
	public Coords getDirectionInD3() {
		switch (type) {
		case CONIC_LINE:
		case CONIC_EMPTY:
		case CONIC_SINGLE_POINT:
			return null;
		default:
			return getCoordSys().getVz();
		}
	}

	// ////////////////////////////////////////////
	// TRANSLATE
	// ////////////////////////////////////////////

	@Override
	public void translate(Coords v) {
		getCoordSys().translate(v);
	}

	// //////////////////////
	// MIRROR
	// //////////////////////

	@Override
	public void mirror(Coords Q) {
		getCoordSys().mirror(Q);
	}

	@Override
	public void mirror(GeoLineND line) {
		Coords point = line.getStartInhomCoords();
		Coords direction = line.getDirectionInD3().normalized();

		getCoordSys().mirror(point, direction);

	}

	@Override
	public void mirror(GeoCoordSys2D plane) {

		getCoordSys().mirror(plane.getCoordSys());
	}

	// //////////////////////
	// DILATE
	// //////////////////////

	@Override
	public void dilate(NumberValue rval, Coords S) {

		double r = rval.getDouble();

		getCoordSys().dilate(r, S);

		if (r < 0) { // mirror was done in coord sys
			r = -r;
		}

		dilate(r);

	}

	// ////////////////////////////////
	// 2D VIEW

	@Override
	public int getViewID() {
		return euclidianViewForPlane.getId();
	}

	@Override
	public void createView2D() {
		euclidianViewForPlane = kernel.getApplication().getCompanion()
				.createEuclidianViewForPlane(this, true);
		euclidianViewForPlane.setTransformRegardingView();
	}

	@Override
	public void removeView2D() {
		euclidianViewForPlane.doRemove();
	}

	@Override
	public void doRemove() {
		if (euclidianViewForPlane != null) {
			removeView2D();
		}
		super.doRemove();
	}

	@Override
	public boolean hasView2DVisible() {
		return euclidianViewForPlane != null && kernel.getApplication()
				.getGuiManager().showView(euclidianViewForPlane.getId());
	}

	@Override
	public void setView2DVisible(boolean flag) {

		if (euclidianViewForPlane == null) {
			if (flag) {
				createView2D();
			}
			return;
		}

		kernel.getApplication().getGuiManager().setShowView(flag,
				euclidianViewForPlane.getId());

	}

	@Override
	public void update(boolean drag) {
		super.update(drag);
		if (euclidianViewForPlane != null) {
			euclidianViewForPlane.updateMatrix();
			updateViewForPlane();
		}
	}

	private void updateViewForPlane() {
		euclidianViewForPlane.updateAllDrawables(true);
	}

	@Override
	public void setEuclidianViewForPlane(
			EuclidianViewForPlaneCompanionInterface view) {
		euclidianViewForPlane = view;
	}

	@Override
	public boolean isParametric() {
		return true;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.PARAMETRIC3D;
	}

	@Override
	public void evaluateFirstDerivativeForParabola(double t, double[] result) {
		Coords eigenvec0 = getEigenvec(0);
		Coords eigenvec1 = getEigenvec(1);
		double x = p * (t * eigenvec0.getX() + eigenvec1.getX());
		double y = p * (t * eigenvec0.getY() + eigenvec1.getY());

		result[0] = x * coordSys.getVx().getX() + y * coordSys.getVy().getX();
		result[1] = x * coordSys.getVx().getY() + y * coordSys.getVy().getY();
		result[2] = x * coordSys.getVx().getZ() + y * coordSys.getVy().getZ();
	}

	@Override
	public void evaluateSecondDerivativeForParabola(double t, double[] result) {
		Coords eigenvec0 = getEigenvec(0);
		double x = p * eigenvec0.getX();
		double y = p * eigenvec0.getY();

		result[0] = x * coordSys.getVx().getX() + y * coordSys.getVy().getX();
		result[1] = x * coordSys.getVx().getY() + y * coordSys.getVy().getY();
		result[2] = x * coordSys.getVx().getZ() + y * coordSys.getVy().getZ();

	}

	@Override
	public boolean isRegion3D() {
		return true;
	}

	@Override
	public boolean isWhollyIn2DView(EuclidianView ev) {

		// check center
		if (!DoubleUtil.isZero(getMidpoint3D().getInhomCoords().getZ())) {
			return false;
		}

		// check direction
		Coords normal = getMainDirection();
		return DoubleUtil.isZero(normal.getX())
				&& DoubleUtil.isZero(normal.getY());

	}

}
