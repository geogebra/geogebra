package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.euclidian.EuclidianViewCompanion;
import org.geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.RotateableND;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.Unicode;

/**
 * @author ggb3D
 * 
 */
public class GeoConic3D extends GeoConicND implements RotateableND,
		MirrorableAtPlane, ViewCreator {

	/** 2D coord sys where the conic exists */
	private CoordSys coordSys;

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
		super(c, 2, isIntersection);
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
		return coordSys.getPoint(super.getMidpoint2D());
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
	public String toString(StringTemplate tpl) {

		StringBuilder sbToString = new StringBuilder();

		switch (getType()) {
		case CONIC_CIRCLE:
		case CONIC_ELLIPSE:
		case CONIC_HYPERBOLA:
		case CONIC_PARABOLA:
			GeoFunction.initStringBuilder(sbToString, tpl, label, "t",
					isLabelSet(), false);
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
	public boolean hasValueStringChangeableRegardingView() {
		return true;
	}

	private void buildValueStringMidpointConic(boolean plusMinusX, String s1,
			String s2, StringTemplate tpl, StringBuilder sbBuildValueString) {
		buildValueString(plusMinusX, s1, s2, getHalfAxis(0), getHalfAxis(1),
				tpl, sbBuildValueString);
	}

	private void buildValueString(boolean plusMinusX, String s1, String s2,
			double r1, double r2, StringTemplate tpl,
			StringBuilder sbBuildValueString) {

		Coords center = getMidpoint3D();
		GeoPoint.buildValueStringCoordCartesian3D(kernel, tpl, center.getX(),
				center.getY(), center.getZ(), sbBuildValueString);

		Coords ev1 = getEigenvec3D(0);
		Coords ev2 = getEigenvec3D(1);

		String separator = GeoPoint.buildValueStringSeparator(kernel, tpl);

		sbBuildValueString.append(" + (");

		kernel.appendTwoCoeffs(plusMinusX, r1 * ev1.getX(), r2 * ev2.getX(),
				s1, s2, tpl, sbBuildValueString);

		sbBuildValueString.append(separator);
		sbBuildValueString.append(" ");

		kernel.appendTwoCoeffs(plusMinusX, r1 * ev1.getY(), r2 * ev2.getY(),
				s1, s2, tpl, sbBuildValueString);

		sbBuildValueString.append(separator);
		sbBuildValueString.append(" ");

		kernel.appendTwoCoeffs(plusMinusX, r1 * ev1.getZ(), r2 * ev2.getZ(),
				s1, s2, tpl, sbBuildValueString);

		sbBuildValueString.append(')');
	}

	@Override
	protected StringBuilder buildValueString(StringTemplate tpl) {

		StringBuilder sbBuildValueString = new StringBuilder();
		if (!isDefined()) {
			sbBuildValueString.append("?");
			return sbBuildValueString;
		}

		switch (getType()) {
		case CONIC_CIRCLE:
		case CONIC_ELLIPSE:
			buildValueStringMidpointConic(false, "cos(t)", "sin(t)", tpl,
					sbBuildValueString);
			break;

		case CONIC_HYPERBOLA:
			buildValueStringMidpointConic(true, "cosh(t)", "sinh(t)", tpl,
					sbBuildValueString);
			break;

		case CONIC_PARABOLA:
			buildValueString(false, "t\u00b2", "t", linearEccentricity,
					2 * linearEccentricity, tpl, sbBuildValueString);
			break;

		case CONIC_SINGLE_POINT:
			Coords center = getMidpoint3D();
			GeoPoint.buildValueStringCoordCartesian3D(kernel, tpl,
					center.getX(), center.getY(), center.getZ(),
					sbBuildValueString);
			break;

		case CONIC_INTERSECTING_LINES:
			center = getMidpoint3D();
			Coords d1 = getDirection3D(0);
			Coords d2 = getDirection3D(1);
			Coords e1 = d1.add(d2).mul(0.5);
			Coords e2 = d2.sub(d1).mul(0.5);
			e2.checkReverseForFirstValuePositive();
			sbBuildValueString.append("X = (");
			sbBuildValueString.append(kernel.format(center.getX(), tpl));
			sbBuildValueString.append(", ");
			sbBuildValueString.append(kernel.format(center.getY(), tpl));
			sbBuildValueString.append(", ");
			sbBuildValueString.append(kernel.format(center.getZ(), tpl));
			sbBuildValueString.append(") + ");
			sbBuildValueString.append(Unicode.lambda);
			sbBuildValueString.append(" (");
			kernel.appendTwoCoeffs(e1.getX(), e2.getX(), tpl,
					sbBuildValueString);
			sbBuildValueString.append(", ");
			kernel.appendTwoCoeffs(e1.getY(), e2.getY(), tpl,
					sbBuildValueString);
			sbBuildValueString.append(", ");
			kernel.appendTwoCoeffs(e1.getZ(), e2.getZ(), tpl,
					sbBuildValueString);
			sbBuildValueString.append(")");
			break;

		case CONIC_PARALLEL_LINES:
			Coords c1 = getOrigin3D(0);
			Coords c2 = getOrigin3D(1);
			Coords d = getDirection3D(0);
			e1 = c1.add(c2).mul(0.5);
			e2 = c2.sub(c1).mul(0.5);
			e2.checkReverseForFirstValuePositive();
			sbBuildValueString.append("X = (");
			kernel.appendTwoCoeffs(e1.getX(), e2.getX(), tpl,
					sbBuildValueString);
			sbBuildValueString.append(", ");
			kernel.appendTwoCoeffs(e1.getY(), e2.getY(), tpl,
					sbBuildValueString);
			sbBuildValueString.append(", ");
			kernel.appendTwoCoeffs(e1.getZ(), e2.getZ(), tpl,
					sbBuildValueString);
			sbBuildValueString.append(") + ");
			sbBuildValueString.append(Unicode.lambda);
			sbBuildValueString.append(" (");
			sbBuildValueString.append(kernel.format(d.getX(), tpl));
			sbBuildValueString.append(", ");
			sbBuildValueString.append(kernel.format(d.getY(), tpl));
			sbBuildValueString.append(", ");
			sbBuildValueString.append(kernel.format(d.getZ(), tpl));

			sbBuildValueString.append(")");
			break;

		case CONIC_DOUBLE_LINE:
			center = getMidpoint3D();
			d = getDirection3D(0);
			sbBuildValueString.append("X = (");
			sbBuildValueString.append(kernel.format(center.getX(), tpl));
			sbBuildValueString.append(", ");
			sbBuildValueString.append(kernel.format(center.getY(), tpl));
			sbBuildValueString.append(", ");
			sbBuildValueString.append(kernel.format(center.getZ(), tpl));
			sbBuildValueString.append(") + ");
			sbBuildValueString.append(Unicode.lambda);
			sbBuildValueString.append(" (");
			sbBuildValueString.append(kernel.format(d.getX(), tpl));
			sbBuildValueString.append(", ");
			sbBuildValueString.append(kernel.format(d.getY(), tpl));
			sbBuildValueString.append(", ");
			sbBuildValueString.append(kernel.format(d.getZ(), tpl));

			sbBuildValueString.append(")");
			break;

		case CONIC_EMPTY:
			sbBuildValueString.append("?");
			break;

		default:
			App.debug("unknown conic type");
			sbBuildValueString.append("?");
			break;
		}

		return sbBuildValueString;

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
		setMidpoint(coordSys.getNormalProjection(m.getInhomCoordsInD3())[1]
				.get());

		setSinglePointMatrix();

		singlePoint();

	}

	private void setSinglePointMatrix() {
		for (int i = 0; i < matrix.length; i++)
			matrix[i] = 0;

		for (int i = 0; i < 3; i++)
			matrix[i] = 1.0d;
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
	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void set(GeoElementND geo) {

		if (geo instanceof GeoConicND) {
			super.set(geo);
			if (coordSys == null) // TODO remove that
				coordSys = new CoordSys(2);
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
		super.getXMLtags(sb);
		// curve thickness and type
		getLineStyleXML(sb);

	}

	// //////////////////////////////////
	// GeoCoordSys2D
	// //////////////////////////////////

	@Override
	public Coords getPoint(double x2d, double y2d) {
		return getCoordSys().getPoint(x2d, y2d);
	}

	@Override
	public Coords[] getNormalProjection(Coords coords) {
		return getCoordSys().getNormalProjection(coords);
	}

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
	public void matrixTransform(double a00, double a01, double a10, double a11) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = CoordMatrix4x4.Identity();
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

	private CoordMatrix4x4 tmpMatrix4x4;

	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = CoordMatrix4x4.Identity();
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

	public void rotate(NumberValue phiVal, GeoPointND Q,
			GeoDirectionND orientation) {

		rotate(phiVal, Q.getInhomCoordsInD3(), orientation.getDirectionInD3());

	}

	public void rotate(NumberValue phiVal, GeoLineND line) {

		rotate(phiVal, line.getStartInhomCoords(), line.getDirectionInD3());

	}

	final private void rotate(NumberValue phiVal, Coords center,
			Coords direction) {
		coordSys.rotate(phiVal.getDouble(), center, direction.normalized());
	}

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

	public void mirror(Coords Q) {
		getCoordSys().mirror(Q);
	}

	public void mirror(GeoLineND line) {
		Coords point = line.getStartInhomCoords();
		Coords direction = line.getDirectionInD3().normalized();

		getCoordSys().mirror(point, direction);

	}

	public void mirror(GeoCoordSys2D plane) {

		getCoordSys().mirror(plane.getCoordSys());
	}

	// //////////////////////
	// DILATE
	// //////////////////////

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

	private EuclidianViewForPlaneCompanion euclidianViewForPlane;

	public void createView2D() {
		euclidianViewForPlane = (EuclidianViewForPlaneCompanion) kernel
				.getApplication().getCompanion()
				.createEuclidianViewForPlane(this, true);
		euclidianViewForPlane.setTransformRegardingView();
	}

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

	public boolean hasView2DVisible() {
		return euclidianViewForPlane != null
				&& kernel.getApplication().getGuiManager()
						.showView(euclidianViewForPlane.getId());
	}

	public void setView2DVisible(boolean flag) {

		if (euclidianViewForPlane == null) {
			if (flag)
				createView2D();
			return;
		}

		kernel.getApplication().getGuiManager()
				.setShowView(flag, euclidianViewForPlane.getId());

	}

	@Override
	public void update() {
		super.update();
		if (euclidianViewForPlane != null) {
			euclidianViewForPlane.updateMatrix();
			updateViewForPlane();
		}
	}

	private void updateViewForPlane() {
		euclidianViewForPlane.updateAllDrawables(true);
	}

	public void setEuclidianViewForPlane(EuclidianViewCompanion view) {
		euclidianViewForPlane = (EuclidianViewForPlaneCompanion) view;
	}

	public boolean isParametric() {
		return true;
	}

	public ValueType getValueType() {
		return ValueType.PARAMETRIC3D;
	}

}
