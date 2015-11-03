package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.euclidian.EuclidianViewCompanion;
import org.geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.RegionParameters;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.Functional2Var;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.RotateableND;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.plugin.GeoClass;

public class GeoPlane3D extends GeoElement3D implements Functional2Var,
		ViewCreator, GeoCoords4D, GeoPlaneND, Translateable, Traceable,
		RotateableND, MirrorableAtPlane, Transformable, Dilateable {

	/** default labels */
	private static final char[] Labels = { 'p', 'q', 'r' };

	private static boolean KEEP_LEADING_SIGN = true;

	double xmin, xmax, ymin, ymax; // values for grid and interactions
	double xPlateMin, xPlateMax, yPlateMin, yPlateMax; // values for plate

	// grid and plate
	boolean gridVisible = false;
	boolean plateVisible = true;
	double dx = Double.NaN; // distance between two marks on the grid //TODO use
							// object
	// properties
	double dy = Double.NaN;

	/** coord sys */
	protected CoordSys coordsys;

	// string
	protected static final String[] VAR_STRING = { "x", "y", "z" };

	/**
	 * creates an empty plane
	 * 
	 * @param c
	 *            construction
	 */
	public GeoPlane3D(Construction c) {
		super(c);

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		coordsys = new CoordSys(2);

		this.xmin = -2.5;
		this.xmax = 2.5;
		this.ymin = -2.5;
		this.ymax = 2.5;

		// grid
		setGridVisible(false);

	}

	public GeoPlane3D(Construction cons, String label, double a, double b,
			double c, double d) {
		this(cons);

		setEquation(a, b, c, d);
		setLabel(label);

	}

	private void setEquation(double a, double b, double c, double d,
			boolean makeCoordSys) {

		setEquation(new double[] { a, b, c, d }, makeCoordSys);

	}

	public void setEquation(double a, double b, double c, double d) {

		setEquation(a, b, c, d, true);
	}

	@Override
	public void setCoords(double x, double y, double z, double w) {
		setEquation(x, y, z, w, false);
	}

	private void setEquation(double[] v, boolean makeCoordSys) {

		if (makeCoordSys || !getCoordSys().isDefined()) {
			getCoordSys().makeCoordSys(v);
			getCoordSys().makeOrthoMatrix(true, true);
		}
	}

	// /////////////////////////////////
	// REGION INTERFACE

	@Override
	public boolean isRegion() {
		return true;
	}

	@Override
	public Coords[] getNormalProjection(Coords coords) {
		return getCoordSys().getNormalProjection(coords);
	}

	@Override
	public Coords[] getProjection(Coords oldCoords, Coords willingCoords,
			Coords willingDirection) {
		Coords[] result = new Coords[] { new Coords(4), new Coords(4) };
		willingCoords.projectPlaneThruVIfPossible(getCoordSys()
				.getMatrixOrthonormal(), oldCoords, willingDirection,
				result[0], result[1]);

		return result;
	}

	@Override
	public boolean isInRegion(GeoPointND P) {
		Coords planeCoords = getNormalProjection(P.getInhomCoordsInD3())[1];
		// Application.debug(P.getLabel()+":\n"+planeCoords);
		return Kernel.isEqual(planeCoords.get(3), 0, Kernel.STANDARD_PRECISION);
	}

	@Override
	public boolean isInRegion(double x0, double y0) {
		return true;
	}

	@Override
	public void pointChangedForRegion(GeoPointND P) {

		P.updateCoords2D();
		P.updateCoordsFrom2D(false, null);

	}

	@Override
	public void regionChanged(GeoPointND P) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(P)
				|| P.getRegionParameters().isNaN()) {
			pointChangedForRegion(P);
			return;
		}

		// pointChangedForRegion(P);
		RegionParameters rp = P.getRegionParameters();
		P.setCoords(getPoint(rp.getT1(), rp.getT2()), false);

	}

	@Override
	public Coords getPoint(double x2d, double y2d) {
		return getCoordSys().getPoint(x2d, y2d);
	}

	// /////////////////////////////////
	// GRID AND PLATE

	/** sets corners of the plate */
	public void setPlateCorners(double x1, double y1, double x2, double y2) {
		if (x1 < x2) {
			this.xPlateMin = x1;
			this.xPlateMax = x2;
		} else {
			this.xPlateMin = x2;
			this.xPlateMax = x1;
		}
		if (y1 < y2) {
			this.yPlateMin = y1;
			this.yPlateMax = y2;
		} else {
			this.yPlateMin = y2;
			this.yPlateMax = y1;
		}
	}

	/** sets corners of the grid */
	public void setGridCorners(double x1, double y1, double x2, double y2) {
		if (x1 < x2) {
			this.xmin = x1;
			this.xmax = x2;
		} else {
			this.xmin = x2;
			this.xmax = x1;
		}
		if (y1 < y2) {
			this.ymin = y1;
			this.ymax = y2;
		} else {
			this.ymin = y2;
			this.ymax = y1;
		}
	}

	/**
	 * set grid distances (between two ticks)
	 * 
	 * @param dx
	 * @param dy
	 */
	public void setGridDistances(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}

	/** @return x min */
	public double getXmin() {
		return xmin;
	}

	/** @return y min */
	public double getYmin() {
		return ymin;
	}

	/** @return x max */
	public double getXmax() {
		return xmax;
	}

	/** @return y max */
	public double getYmax() {
		return ymax;
	}

	/** @return plate x min */
	public double getXPlateMin() {
		return xPlateMin;
	}

	/** @return plate y min */
	public double getYPlateMin() {
		return yPlateMin;
	}

	/** @return plate x max */
	public double getXPlateMax() {
		return xPlateMax;
	}

	/** @return plate y max */
	public double getYPlateMax() {
		return yPlateMax;
	}

	/** returns if there is a grid to plot or not */
	public boolean isGridVisible() {
		return gridVisible && isEuclidianVisible();
	}

	public boolean setGridVisible(boolean grid) {
		if (gridVisible == grid) {
			return false;
		}
		gridVisible = grid;
		return true;
	}

	/** returns if there is a plate visible */
	public boolean isPlateVisible() {
		return plateVisible && isEuclidianVisible();
	}

	public void setPlateVisible(boolean flag) {
		plateVisible = flag;
	}

	/** returns x delta for the grid */
	public double getGridXd() {
		return dx;
	}

	/** returns y delta for the grid */
	public double getGridYd() {
		return dy;
	}

	// /////////////////////////////////
	// GEOELEMENT3D

	private CoordMatrix parametricMatrix;

	/**
	 * return the (v1, v2, o) parametric matrix of this plane, ie each point of
	 * the plane is (v1, v2, o)*(a,b,1) for some a, b value
	 * 
	 * @return the (v1, v2, o) parametric matrix of this plane
	 */
	public CoordMatrix getParametricMatrix() {
		CoordMatrix4x4 m4 = getCoordSys().getMatrixOrthonormal();
		if (parametricMatrix == null) {
			parametricMatrix = new CoordMatrix(4, 3);
		}
		parametricMatrix.setVx(m4.getVx());
		parametricMatrix.setVy(m4.getVy());
		parametricMatrix.setOrigin(m4.getOrigin());
		return parametricMatrix;
	}

	@Override
	public Coords getMainDirection() {

		return getCoordSys().getNormal();
	}

	@Override
	public Coords getLabelPosition() {
		return getCoordSys().getPoint(0.5, 0.5);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.PLANE3D;
	}

	@Override
	public GeoPlane3D copy() {
		GeoPlane3D p = new GeoPlane3D(cons);

		// TODO move this elsewhere
		CoordSys cs = p.getCoordSys();
		cs.set(this.getCoordSys());

		return p;
	}

	@Override
	public boolean isEqual(GeoElement Geo) {
		// TODO Raccord de methode auto-genere
		return false;
	}

	/**
	 * Also allow setting from line x+y=1, which may come from user or CAS
	 * instead of x+y+0z=1
	 */
	@Override
	public void set(GeoElementND geo) {
		if (geo instanceof GeoPlane3D) {
			GeoPlane3D plane = (GeoPlane3D) geo;
			getCoordSys().set(plane.getCoordSys());
		}
		if (geo instanceof GeoLine) {
			GeoLine line = (GeoLine) geo;
			setEquation(line.getX(), line.getY(), 0, line.getZ());
		}
	}

	public void setCoordSys(CoordSys cs) {
		getCoordSys().set(cs);
	}

	@Override
	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);
		if (geo.isGeoPlane()) {
			setFading(((GeoPlaneND) geo).getFading());
		}
	}

	@Override
	public void setUndefined() {
		coordsys.setUndefined();

	}

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		return isDefined();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return buildValueString(tpl).toString();
	}

	@Override
	final public String toString(StringTemplate tpl) {

		StringBuilder sbToString = getSbToString();
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(": "); // TODO use kernel property
		sbToString.append(buildValueString(tpl));
		return sbToString.toString();
	}

	private StringBuilder buildValueString(StringTemplate tpl) {

		// we need to keep 0z in equation to be sure that y+0z=1 will be loaded
		// as a plane
		StringBuilder ret = kernel.buildImplicitEquation(getCoordSys()
				.getEquationVector().get(), VAR_STRING, KEEP_LEADING_SIGN,
				true, !isLabelSet(), '=', tpl);

		if (tpl.hasCASType()) {
			StringBuilder sbTemp = new StringBuilder();
			// Giac
			sbTemp.append("plane(");
			sbTemp.append(ret);
			sbTemp.append(")");

			return sbTemp;
		}

		return ret;

	}

	/** to be able to fill it with an alpha value */
	@Override
	public boolean isFillable() {
		return true;
	}

	// ///////////////////////////////////////////
	// 2 VAR FUNCTION INTERFACE
	// //////////////////////////////////////////

	@Override
	public Coords evaluateNormal(double u, double v) {

		return coordsys.getNormal();
	}

	@Override
	public Coords evaluatePoint(double u, double v) {

		return coordsys.getPointForDrawing(u, v);
		// return coordsys.getPoint(u, v);

	}

	@Override
	public double getMinParameter(int index) {

		return 0; // TODO

	}

	@Override
	public double getMaxParameter(int index) {

		return 0; // TODO

	}

	@Override
	public CoordSys getCoordSys() {
		return coordsys;
	}

	@Override
	public boolean isDefined() {
		return coordsys.isDefined();
	}

	@Override
	public boolean isMoveable() {
		return false;
	}

	@Override
	public String getDefaultLabel() {
		return getDefaultLabel(Labels, false);
	}

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		Coords equation = getCoordSys().getEquationVector();

		// equation
		sb.append("\t<coords");
		sb.append(" x=\"");
		sb.append(equation.getX());
		sb.append("\"");
		sb.append(" y=\"");
		sb.append(equation.getY());
		sb.append("\"");
		sb.append(" z=\"");
		sb.append(equation.getZ());
		sb.append("\"");
		sb.append(" w=\"");
		sb.append(equation.getW());
		sb.append("\"");
		sb.append("/>\n");

		// fading
		sb.append("\t<fading val=\"");
		sb.append(getFading());
		sb.append("\"/>\n");

	}

	@Override
	public boolean isGeoPlane() {
		return true;
	}

	// ////////////////////////////////
	// FADING

	private float fading = 0.10f;

	@Override
	public void setFading(float fading) {
		this.fading = fading;
	}

	@Override
	public float getFading() {
		return fading;
	}

	// ////////////////////////////////
	// 2D VIEW

	private EuclidianViewForPlaneCompanion euclidianViewForPlane;

	@Override
	public void createView2D() {
		euclidianViewForPlane = (EuclidianViewForPlaneCompanion) kernel
				.getApplication().getCompanion()
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
		return euclidianViewForPlane != null
				&& kernel.getApplication().getGuiManager()
						.showView(euclidianViewForPlane.getId());
	}

	@Override
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
			euclidianViewForPlane.updateForPlane();
		}
	}

	@Override
	public void setEuclidianViewForPlane(EuclidianViewCompanion view) {
		euclidianViewForPlane = (EuclidianViewForPlaneCompanion) view;
	}

	@Override
	public Coords getDirectionInD3() {
		return getCoordSys().getNormal();
	}

	@Override
	public double getMeasure() {
		return Double.POSITIVE_INFINITY;
	}

	private Coords tmpCoords1, tmpCoords2;

	@Override
	public double distance(GeoPointND P) {

		return Math.abs(distanceWithSign(P));

	}

	/**
	 * 
	 * @param P
	 *            point
	 * @return distance from point P to this plane, with sign
	 */
	public double distanceWithSign(GeoPointND P) {

		if (tmpCoords1 == null) {
			tmpCoords1 = new Coords(3);
		}
		if (tmpCoords2 == null) {
			tmpCoords2 = new Coords(3);
		}

		tmpCoords1.setSub(P.getInhomCoordsInD3(), getCoordSys().getOrigin());
		tmpCoords2.setValues(getDirectionInD3(), 3);
		tmpCoords2.normalize();

		return tmpCoords1.dotproduct(tmpCoords2);

	}

	public double distanceWithSign(GeoPlaneND P) {

		if (tmpCoords1 == null) {
			tmpCoords1 = new Coords(3);
		}
		if (tmpCoords2 == null) {
			tmpCoords2 = new Coords(3);
		}
		tmpCoords2.setValues(getDirectionInD3(), 3);
		tmpCoords2.normalize();

		tmpCoords1.setValues(P.getDirectionInD3(), 3);
		tmpCoords1.normalize();

		if (!Kernel.isEqual(1, Math.abs(tmpCoords1.dotproduct(tmpCoords2)))) {
			return 0;
		}

		tmpCoords1.setSub(P.getCoordSys().getOrigin(), getCoordSys()
				.getOrigin());

		return tmpCoords1.dotproduct(tmpCoords2);

	}

	// ///////////////////////////////////
	// TRANSLATE
	// ///////////////////////////////////

	@Override
	public void translate(Coords v) {
		getCoordSys().translate(v);
		getCoordSys().translateEquationVector(v);
	}

	@Override
	public boolean isTranslateable() {
		return true;
	}

	// ////////////////
	// TRACE
	// ////////////////

	private boolean trace;

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

	// //////////////////////
	// ROTATIONS
	// //////////////////////

	/**
	 * rotate the plane
	 * 
	 * @param rot
	 *            rotation matrix
	 * @param center
	 *            rotation center
	 */
	final public void rotate(CoordMatrix rot, Coords center) {
		coordsys.rotate(rot, center);
		coordsys.makeEquationVector();
	}

	@Override
	final public void rotate(NumberValue phiVal) {
		coordsys.rotate(phiVal.getDouble(), Coords.O);
		coordsys.makeEquationVector();
	}

	@Override
	final public void rotate(NumberValue phiVal, GeoPointND Q) {
		coordsys.rotate(phiVal.getDouble(), Q.getInhomCoordsInD3());
		coordsys.makeEquationVector();
	}

	final private void rotate(NumberValue phiVal, Coords center,
			Coords direction) {
		coordsys.rotate(phiVal.getDouble(), center, direction.normalized());
		coordsys.makeEquationVector();
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

	@Override
	public void mirror(Coords Q) {
		coordsys.mirror(Q);
		coordsys.mirrorEquationVector(Q);

	}

	@Override
	public void mirror(GeoLineND line) {

		Coords point = line.getStartInhomCoords();
		Coords direction = line.getDirectionInD3().normalized();

		coordsys.mirror(point, direction);
		coordsys.makeEquationVector();

	}

	@Override
	public void mirror(GeoCoordSys2D plane) {

		coordsys.mirror(plane.getCoordSys());
		coordsys.makeEquationVector();

	}

	// //////////////////////
	// DILATE
	// //////////////////////

	@Override
	public void dilate(NumberValue rval, Coords S) {

		double r = rval.getDouble();

		coordsys.dilate(r, S);
		coordsys.dilateEquationVector(r, S);

	}

	@Override
	final public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	@Override
	public boolean is6dofMoveable() {
		return true;
	}

	public ValueType getValueType() {
		return ValueType.EQUATION;
	}

	@Override
	protected void getXMLanimationTags(final StringBuilder sb) {
		// no need for planes
	}

}
