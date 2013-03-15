package geogebra3D.kernel3D;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.plugin.GeoClass;
import geogebra.euclidian.EuclidianViewD;
import geogebra3D.euclidian3D.Drawable3D;

/**
 * @author ggb3D
 * 
 */
public class GeoConic3D extends GeoConicND implements GeoElement3DInterface {// ,
																				// GeoCoordSys2D{

	/** 2D coord sys where the conic exists */
	private CoordSys coordSys;

	/** link with drawable3D */
	private Drawable3D drawable3D = null;

	private boolean isIntersection = false;

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
		super(c, 2);
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

	// ///////////////////////////////////////
	// link with Drawable3D

	/**
	 * set the 3D drawable linked to
	 * 
	 * @param d
	 *            the 3D drawable
	 */
	public void setDrawable3D(Drawable3D d) {
		drawable3D = d;
	}

	/**
	 * return the 3D drawable linked to
	 * 
	 * @return the 3D drawable linked to
	 */
	public Drawable3D getDrawable3D() {
		return drawable3D;
	}


	@Override
	public Coords getLabelPosition() {
		return new Coords(4); // TODO
	}

	// ///////////////////////////////////////
	// link with GeoElement2D

	public GeoElement getGeoElement2D() {
		return null;
	}

	public boolean hasGeoElement2D() {
		return false;
	}

	public void setGeoElement2D(GeoElement geo) {
		// TODO ?
	}

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
	final public String toString(StringTemplate tpl) {

		StringBuilder sbToString = new StringBuilder();
		sbToString.setLength(0);
		sbToString.append(label);

		sbToString.append(": ");
		sbToString.append(buildValueString(tpl));
		return sbToString.toString();
	}

	@Override
	public boolean hasValueStringChangeableRegardingView() {
		return true;
	}

	@Override
	protected StringBuilder buildValueString(StringTemplate tpl) {

		if (!(getViewForValueString() instanceof EuclidianViewD))
			return new StringBuilder("todo-GeoConic3D");

		EuclidianView view = (EuclidianView) getViewForValueString();

		// check if in view
		Coords M = view.getCoordsForView(getMidpoint3D());
		if (!Kernel.isZero(M.getZ())) {// check if in view
			return new StringBuilder(app.getPlain("NotIncluded"));
		}
		Coords[] ev = new Coords[2];
		for (int j = 0; j < 2; j++) {
			ev[j] = view.getCoordsForView(getEigenvec3D(j));
			if (!Kernel.isZero(ev[j].getZ())) {// check if in view
				return new StringBuilder(app.getPlain("NotIncluded"));
			}
		}

		double[] matrix = getMatrix();

		Coords mid2D = getMidpoint2D();
		translateMatrix(matrix, -mid2D.getX(), -mid2D.getY());

		Coords ev2D0 = getEigenvec(0);
		Coords ev2D1 = getEigenvec(1);
		double x = ev2D0.dotproduct(ev[0]);
		double y = ev2D1.dotproduct(ev[0]);
		double phi = Math.atan2(y, x);
		rotateMatrix(matrix, phi);

		translateMatrix(matrix, M.getX(), M.getY());

		return buildValueString(tpl,matrix);
	}

	@Override
	public void setSphereND(GeoPointND M, GeoSegmentND segment) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setSphereND(GeoPointND M, GeoPointND P) {
		// TODO Auto-generated method stub

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
	public void set(GeoElement geo) {

		if (geo instanceof GeoConicND) {
			super.set(geo);
			if (coordSys == null) // TODO remove that
				coordSys = new CoordSys(2);
			coordSys.set(((GeoConicND) geo).getCoordSys());
			setIsEndOfQuadric(((GeoConicND) geo).isEndOfQuadric());
		}

	}

	@Override
	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCircle(GeoPoint M, GeoPoint P) {
		// TODO Auto-generated method stub

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

	public Coords getPoint(double x2d, double y2d) {
		return getCoordSys().getPoint(x2d, y2d);
	}

	public Coords[] getNormalProjection(Coords coords) {
		return coords.projectPlane(getCoordSys().getMatrixOrthonormal());
	}

	public Coords[] getProjection(Coords coords, Coords willingDirection) {
		return coords.projectPlaneThruV(getCoordSys().getMatrixOrthonormal(),
				willingDirection);
	}

	// //////////////////////////////////
	// GeoCoordSys2D
	// //////////////////////////////////

	@Override
	public void pointChangedForRegion(GeoPointND PI) {
		// TODO
	}

	@Override
	public void regionChanged(GeoPointND PI) {

		//if kernel doesn't use path/region parameters, do as if point changed its coords
		if(!getKernel().usePathAndRegionParameters(PI)){
			pointChangedForRegion(PI);
			return;
		}
		
		// TODO
	}

	@Override
	public boolean isRegion() {
		return false; // TODO
	}

	public void setIsIntersection(boolean flag) {
		isIntersection = flag;
	}

	public boolean isIntersection() {
		return isIntersection;
	}

	@Override
	protected void doTranslate(Coords v) {
		coordSys.translate(v);
	}

	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		setUndefined();
		// TODO Auto-generated method stub
		
	}
}
