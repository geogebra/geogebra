/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra3D.kernel3D;

import geogebra.GeoGebra3D;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.io.MyXMLHandler;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Manager3DInterface;
import geogebra.common.kernel.algos.AlgoDispatcher;
import geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.commands.CommandDispatcher;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoAxisND;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPlaneND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoRayND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.main.MyError;
import geogebra.common.plugin.GeoClass;
import geogebra3D.App3D;
import geogebra3D.io.MyXMLHandler3D;
import geogebra3D.kernel3D.arithmetic.ExpressionNodeEvaluator3D;
import geogebra3D.kernel3D.commands.AlgebraProcessor3D;
import geogebra3D.kernel3D.commands.CommandDispatcher3D;

import java.util.LinkedHashMap;
import java.util.TreeSet;

/**
 * 
 * Class used for (3D) calculations
 * 
 * <h3>How to add a method for creating a {@link GeoElement3D}</h3>
 * 
 * <ul>
 * <li>simply call the element's constructor
 * <p>
 * <code>
   final public GeoNew3D New3D(String label, ???) { <br> &nbsp;&nbsp;
       GeoNew3D ret = new GeoNew3D(cons, ???); <br> &nbsp;&nbsp;
       // stuff <br> &nbsp;&nbsp;
       ret.setLabel(label); <br> &nbsp;&nbsp;           
       return ret; <br> 
   }
   </code></li>
 * <li>use an {@link AlgoElement3D}
 * <p>
 * <code>
   final public GeoNew3D New3D(String label, ???) { <br> &nbsp;&nbsp;
     AlgoNew3D algo = new AlgoNew3D(cons, label, ???); <br> &nbsp;&nbsp;
	 return algo.getGeo(); <br> 
   }
   </code></li>
 * </ul>
 * 
 * 
 * @author ggb3D
 * 
 */

public class Kernel3D extends Kernel {

	protected App3D app3D;

	public Kernel3D(App3D app) {

		super(app);
		this.app3D = app;

	}

	public GeoAxisND getXAxis3D() {
		return ((Construction3D) cons).getXAxis3D();
	}

	public GeoAxisND getYAxis3D() {
		return ((Construction3D) cons).getYAxis3D();
	}

	public GeoAxis3D getZAxis3D() {
		return ((Construction3D) cons).getZAxis3D();
	}

	@Override
	public GeoPlane3DConstant getXOYPlane() {
		return ((Construction3D) cons).getXOYPlane();
	}
	
	public GeoClippingCube3D getClippingCube(){
		return ((Construction3D) cons).getClippingCube();
	}
	
	@Override
	public GeoSpace getSpace() {
		return ((Construction3D) cons).getSpace();
	}

	/* *******************************************
	 * Methods for EuclidianView/EuclidianView3D
	 * *******************************************
	 */

	@Override
	public String getModeText(int mode) {
		switch (mode) {
		case EuclidianConstants.MODE_VIEW_IN_FRONT_OF:
			return "ViewInFrontOf";

		case EuclidianConstants.MODE_PLANE_THREE_POINTS:
			return "PlaneThreePoint";

		case EuclidianConstants.MODE_PLANE_POINT_LINE:
			return "PlanePointLine";

		case EuclidianConstants.MODE_ORTHOGONAL_PLANE:
			return "OrthogonalPlane";

		case EuclidianConstants.MODE_PARALLEL_PLANE:
			return "ParallelPlane";

		case EuclidianConstants.MODE_PRISM:
			return "Prism";

		case EuclidianConstants.MODE_EXTRUSION:
			return "Extrusion";
			
		case EuclidianConstants.MODE_CONIFY:
			return "Conify";
			
		case EuclidianConstants.MODE_PYRAMID:
			return "Pyramid";

		case EuclidianConstants.MODE_SPHERE_POINT_RADIUS:
			return "SpherePointRadius";

		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
			return "Sphere2";

		case EuclidianConstants.MODE_ROTATEVIEW:
			return "RotateView";

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION:
			return "CirclePointRadiusDirection";

		case EuclidianConstants.MODE_CIRCLE_AXIS_POINT:
			return "CircleAxisPoint";

		default:
			return super.getModeText(mode);
		}
	}

	/* *******************************************
	 * Methods for 3D manager *******************************************
	 */

	@Override
	public Manager3DInterface newManager3D(Kernel kernel) {
		return new Manager3D(kernel);
	}

	/**
	 * Returns whether the variable name "z" may be used. Note that the 3D
	 * kernel does not allow this as it uses "z" in plane equations like 3x + 2y
	 * + z = 5.
	 * 
	 * @return whether z may be used as a variable name
	 */
	@Override
	public boolean isZvarAllowed() {
		return false;
	}

	/* *******************************************
	 * Methods for MyXMLHandler *******************************************
	 */

	/**
	 * creates the 3D construction cons
	 */
	@Override
	protected void newConstruction() {
		cons = new Construction3D(this);
	}

	@Override
	public MyXMLHandler newMyXMLHandler(Kernel kernel, Construction construction) {
		return new MyXMLHandler3D(kernel, construction);
	}

	@Override
	public ExpressionNodeEvaluator newExpressionNodeEvaluator() {
		return new ExpressionNodeEvaluator3D(app3D.getLocalization());
	}

	public App3D getApplication3D() {
		return app3D;
	}

	/**
	 * @param kernel kernel
	 * @return a new algebra processor (used for 3D)
	 */
	@Override
	public AlgebraProcessor newAlgebraProcessor(Kernel kernel){
		CommandDispatcher cd = new CommandDispatcher3D(kernel);
		return new AlgebraProcessor3D(kernel,cd);
	}

	/** return all points of the current construction */
	@Override
	public TreeSet<GeoElement> getPointSet() {
		TreeSet<GeoElement> t3d = getConstruction().getGeoSetLabelOrder(GeoClass.POINT3D);
		TreeSet<GeoElement> t = super.getPointSet();

		t.addAll(t3d);
		// TODO add super.getPointSet()
		return t;
	}

	/**
	 * Creates a new GeoElement object for the given type string.
	 * 
	 * @param type
	 *            : String as produced by GeoElement.getXMLtypeString()
	 */
	@Override
	public GeoElement createGeoElement(Construction cons1, String type)
			throws MyError {

		switch (type.charAt(0)) {
		case 'a': 
			if (type.equals("axis3d"))
				return new GeoAxis3D(cons1);
			else if (type.equals("angle3d")) 
				return new GeoAngle3D(cons1);
			
		case 'c': 
			if (type.equals("conic3d"))
				return new GeoConic3D(cons1);
			else if (type.equals("curvecartesian3d")) 
				return new GeoCurveCartesian3D(cons1);
			
		case 'l': 
			if (type.equals("line3d"))
				return new GeoLine3D(cons1);

		case 'p': 
			if (type.equals("point3d")) {
				return new GeoPoint3D(cons1);
			} else if (type.equals("polygon3d"))
				return new GeoPolygon3D(cons1, null);
			else if (type.equals("plane3d")) 
				return new GeoPlane3D(cons1);
			else if (type.equals("polyline3d")) 
				return new GeoPolyLine3D(cons1, null);
			else if (type.equals("polyhedron")) 
				return new GeoPolyhedron(cons1);

		case 'q': 
			if (type.equals("quadric3d") || type.equals("quadric")) {
				return new GeoQuadric3D(cons1);
			} else if (type.equals("quadric3dpart"))
				return new GeoQuadric3DPart(cons1);
			else if (type.equals("quadric3dlimited")) 
				return new GeoQuadric3DLimited(cons1);

		case 'r': 
			if (type.equals("ray3d"))
				return new GeoRay3D(cons1);
			
		case 's': 
			if (type.equals("segment3d"))
				return new GeoSegment3D(cons1);
			if (type.equals("surfacecartesian3d"))
				return new GeoSurfaceCartesian3D(cons1);
			
		case 'v': 
			if (type.equals("vector3d"))
				return new GeoVector3D(cons1);

		}

		// not a 3D object, now check 2D objects in Kernel
		return super.createGeoElement(cons1, type);
	}

	/* *******************************************
	 * Methods for MyXMLHandler *******************************************
	 */
	@Override
	public boolean handleCoords(GeoElement geo,
			LinkedHashMap<String, String> attrs) {

		/*
		 * Application.debug("attrs =\n"+attrs);
		 * Application.debug("attrs(x) = "+attrs.get("x"));
		 * Application.debug("attrs(y) = "+attrs.get("y"));
		 * Application.debug("attrs(z) = "+attrs.get("z"));
		 * Application.debug("attrs(w) = "+attrs.get("w"));
		 */

		if (!(geo instanceof GeoCoords4D)) {
			return super.handleCoords(geo, attrs);
		}

		try {
			double x = Double.parseDouble(attrs.get("x"));
			double y = Double.parseDouble(attrs.get("y"));
			double z = Double.parseDouble(attrs.get("z"));
			double w = Double.parseDouble(attrs.get("w"));
			((GeoCoords4D) geo).setCoords(x, y, z, w);
			// Application.debug(geo.getLabel()+": x="+x+", y="+y+", z="+z+", w="+w);
			return true;
		} catch (Exception e) {
			// Application.debug("erreur : "+e);
			return false;
		}
	}

	@Override
	public GeoPlaneND getDefaultPlane() {
		return app3D.getEuclidianView3D().getxOyPlane();
	}

	// ///////////////////////////////
	// OVERRIDES KERNEL
	// ///////////////////////////////
	@Override
	public GeoPointND IntersectLines(String label, GeoLineND g, GeoLineND h) {

		if (((GeoElement) g).isGeoElement3D()
				|| ((GeoElement) h).isGeoElement3D())
			return (GeoPointND) getManager3D().Intersect(label, (GeoElement) g,
					(GeoElement) h);
		return super.IntersectLines(label, g, h);

	}

	@Override
	public GeoPointND[] IntersectConics(String[] labels, GeoConicND a,
			GeoConicND b) {

		if (((GeoElement) a).isGeoElement3D()
				|| ((GeoElement) b).isGeoElement3D())
			return getManager3D().IntersectConics(labels, a, b);
		return super.IntersectConics(labels, a, b);
	}

	@Override
	public GeoLineND OrthogonalLine(String label, GeoPointND P, GeoLineND l,
			GeoDirectionND direction) {
		return getManager3D().OrthogonalLine3D(label, P, l, direction);
	}

	@Override
	public String getXMLFileFormat() {
		return GeoGebra3D.XML_FILE_FORMAT;
	}
	
	@Override
	public GeoNumeric Distance(String label, GeoLineND g, GeoLineND h) {
		if (((GeoElement) g).isGeoElement3D()
				|| ((GeoElement) h).isGeoElement3D())
			return getManager3D().Distance(label, g, h);
		return super.Distance(label, g, h);
	}


	
	/**
	 * 
	 * @param geo
	 * @return 3D copy of the geo (if exists)
	 */
	public GeoElement copy3D(GeoElement geo) {

		switch (geo.getGeoClassType()) {

		case POINT:
			return new GeoPoint3D((GeoPointND) geo);

		case LINE:
			GeoCoordSys1D ret = new GeoLine3D(geo.getConstruction());
			ret.set(geo);
			return ret;
		case SEGMENT:
			ret = new GeoSegment3D(geo.getConstruction());
			ret.set(geo);
			return ret;
		case RAY:
			ret = new GeoRay3D(geo.getConstruction());
			ret.set(geo);
			return ret;

		case CONIC:
			return new GeoConic3D((GeoConicND) geo);

		default:
			return geo.copy();
		}
	}

	/**
	 * 
	 * @param cons
	 * @param geo
	 * @return 3D copy internal of the geo (if exists)
	 */
	public GeoElement copyInternal3D(Construction cons, GeoElement geo) {

		switch (geo.getGeoClassType()) {

		case POLYGON:
			GeoPolygon3D poly = new GeoPolygon3D(cons, null);
			GeoPointND[] geoPoints = ((GeoPolygon) geo).getPointsND();
			GeoPointND[] points = new GeoPointND[geoPoints.length];
			for (int i = 0; i < geoPoints.length; i++) {
				points[i] = new GeoPoint3D(geoPoints[i]);
				((GeoElement) points[i]).setConstruction(cons);
			}
			poly.setPoints(points);
			poly.set(geo);
			return poly;
		default:
			return geo.copyInternal(cons);
		}
	}

	// //////////////////////////////////
	// 2D FACTORY EXTENSION
	// //////////////////////////////////

	@Override
	final public GeoRayND RayND(String label, GeoPointND P, GeoPointND Q) {
		if (((GeoElement) P).isGeoElement3D()
				|| ((GeoElement) P).isGeoElement3D())
			return getManager3D().Ray3D(label, P, Q);
		return super.Ray(label, (GeoPoint) P, (GeoPoint) Q);
	}

	@Override
	final public GeoSegmentND SegmentND(String label, GeoPointND P, GeoPointND Q) {

		if (((GeoElement) P).isGeoElement3D()
				|| ((GeoElement) P).isGeoElement3D())
			return getManager3D().Segment3D(label, P, Q);
		return super.Segment(label, (GeoPoint) P, (GeoPoint) Q);
	}

	@Override
	final public GeoElement[] PolygonND(String[] labels, GeoPointND[] P) {

		boolean is3D = false;
		for (int i = 0; i < P.length && !is3D; i++)
			if (((GeoElement) P[i]).isGeoElement3D())
				is3D = true;

		if (is3D)
			return getManager3D().Polygon3D(labels, P);
		return super.Polygon(labels, P);
	}

	@Override
	public GeoElement[] PolyLineND(String[] labels, GeoPointND[] P) {

		boolean is3D = false;
		for (int i = 0; i < P.length && !is3D; i++)
			if (((GeoElement) P[i]).isGeoElement3D())
				is3D = true;

		if (is3D)
			return getManager3D().PolyLine3D(labels, P);
		return super.PolyLine(labels, P, false);

	}
	
	
	protected AlgoDispatcher newAlgoDispatcher(){
		return new AlgoDispatcher3D(cons);
	}


}