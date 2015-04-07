/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D;

import java.util.LinkedHashMap;
import java.util.TreeSet;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.geogebra3D.io.MyXMLHandler3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoDispatcher3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoElement3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoVectorPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.Manager3D;
import org.geogebra.common.geogebra3D.kernel3D.arithmetic.ExpressionNodeEvaluator3D;
import org.geogebra.common.geogebra3D.kernel3D.commands.AlgebraProcessor3D;
import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcher3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoAngle3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoAxis3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoClippingCube3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCoords4D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoElement3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3DConstant;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoRay3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSpace;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSurfaceCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.io.MyXMLHandler;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Manager3DInterface;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
import org.geogebra.common.kernel.algos.AlgoVectorPoint;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoAxisND;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoRayND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

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

	public Kernel3D(App app) {

		super(app);

	}

	@Override
	public GeoAxisND getXAxis3D() {
		return ((Construction3D) cons).getXAxis3D();
	}

	@Override
	public GeoAxisND getYAxis3D() {
		return ((Construction3D) cons).getYAxis3D();
	}

	@Override
	public GeoAxis3D getZAxis3D() {
		return ((Construction3D) cons).getZAxis3D();
	}

	@Override
	public GeoPlane3DConstant getXOYPlane() {
		return ((Construction3D) cons).getXOYPlane();
	}

	@Override
	public GeoClippingCube3D getClippingCube() {
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

		case EuclidianConstants.MODE_PLANE:
			return "Plane";

		case EuclidianConstants.MODE_ORTHOGONAL_PLANE:
			return "OrthogonalPlane";

		case EuclidianConstants.MODE_PARALLEL_PLANE:
			return "ParallelPlane";

		case EuclidianConstants.MODE_CUBE:
			return "Cube";

		case EuclidianConstants.MODE_TETRAHEDRON:
			return "Tetrahedron";

		case EuclidianConstants.MODE_PRISM:
			return "Prism";

		case EuclidianConstants.MODE_EXTRUSION:
			return "Extrusion";

		case EuclidianConstants.MODE_CONIFY:
			return "Conify";

		case EuclidianConstants.MODE_PYRAMID:
			return "Pyramid";

		case EuclidianConstants.MODE_NET:
			return "Net";

		case EuclidianConstants.MODE_SPHERE_POINT_RADIUS:
			return "SpherePointRadius";

		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
			return "Sphere2";

		case EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS:
			return "Cone";

		case EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS:
			return "Cylinder";

		case EuclidianConstants.MODE_ROTATEVIEW:
			return "RotateView";

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION:
			return "CirclePointRadiusDirection";

		case EuclidianConstants.MODE_CIRCLE_AXIS_POINT:
			return "CircleAxisPoint";

		case EuclidianConstants.MODE_VOLUME:
			return "Volume";

		case EuclidianConstants.MODE_MIRROR_AT_PLANE:
			return "MirrorAtPlane";

		case EuclidianConstants.MODE_ROTATE_AROUND_LINE:
			return "RotateAroundLine";

		case EuclidianConstants.MODE_ORTHOGONAL_THREE_D:
			return "OrthogonalThreeD";

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
	public ExpressionNodeEvaluator newExpressionNodeEvaluator(Kernel kernel) {
		return new ExpressionNodeEvaluator3D(app.getLocalization(), kernel);
	}

	/**
	 * @param kernel
	 *            kernel
	 * @return a new algebra processor (used for 3D)
	 */
	@Override
	public AlgebraProcessor newAlgebraProcessor(Kernel kernel) {
		CommandDispatcher cd = new CommandDispatcher3D(kernel);
		return new AlgebraProcessor3D(kernel, cd);
	}

	/** return all points of the current construction */
	@Override
	public TreeSet<GeoElement> getPointSet() {
		TreeSet<GeoElement> t3d = getConstruction().getGeoSetLabelOrder(
				GeoClass.POINT3D);
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

		if (geo instanceof GeoLine3D) {
			try {
				// origin
				double ox = Double.parseDouble(attrs.get("ox"));
				double oy = Double.parseDouble(attrs.get("oy"));
				double oz = Double.parseDouble(attrs.get("oz"));
				double ow = Double.parseDouble(attrs.get("ow"));

				// direction
				double vx = Double.parseDouble(attrs.get("vx"));
				double vy = Double.parseDouble(attrs.get("vy"));
				double vz = Double.parseDouble(attrs.get("vz"));
				double vw = Double.parseDouble(attrs.get("vw"));

				((GeoLine3D) geo).setCoord(new Coords(ox, oy, oz, ow),
						new Coords(vx, vy, vz, vw));
				return true;
			} catch (Exception e) {
				return false;
			}
		}

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
	public GeoPlane3D getDefaultPlane() {
		return getXOYPlane();
	}

	// ///////////////////////////////
	// OVERRIDES KERNEL
	// ///////////////////////////////

	@Override
	public GeoLineND OrthogonalLine(String label, GeoPointND P, GeoLineND l,
			GeoDirectionND direction) {
		return getManager3D().OrthogonalLine3D(label, P, l, direction);
	}

	@Override
	public String getXMLFileFormat() {
		return GeoGebraConstants.XML_FILE_FORMAT;
	}

	@Override
	public GeoElement copy3D(GeoElement geo) {

		switch (geo.getGeoClassType()) {

		case POINT:
			return new GeoPoint3D((GeoPointND) geo);

		case VECTOR:
			GeoVector3D v = new GeoVector3D(geo.getConstruction());
			v.set(geo);
			return v;

		case LINE:
			GeoElement ret = new GeoLine3D(geo.getConstruction());
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

		case POLYGON:
			ret = new GeoPolygon3D(geo.getConstruction());
			ret.set(geo);
			return ret;

		case CONIC:
			return new GeoConic3D((GeoConicND) geo);

		default:
			return super.copy3D(geo);
		}
	}

	@Override
	public GeoElement copyInternal3D(Construction cons, GeoElement geo) {

		switch (geo.getGeoClassType()) {

		case POLYGON:
			GeoPolygon3D poly = new GeoPolygon3D(cons, null);
			((GeoPolygon) geo).copyInternal(cons, poly);
			return poly;
		default:
			return super.copyInternal3D(cons, geo);
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

	@Override
	protected AlgoDispatcher newAlgoDispatcher(Construction cons1) {
		return new AlgoDispatcher3D(cons1);
	}

	@Override
	public double getZmax(int i) {
		if (i == 2) {
			return zmax3;
		}
		return super.getZmax(i);
	}

	@Override
	public double getZmin(int i) {
		if (i == 2) {
			return zmin3;
		}
		return super.getZmin(i);
	}

	@Override
	public double getZscale(int i) {
		if (i == 2) {
			return zscale3;
		}
		return super.getZscale(i);
	}

	private double zmin3, zmax3, zscale3;

	/**
	 * Tells this kernel about the bounds and the scales for x-Axis and y-Axis
	 * used in EudlidianView. The scale is the number of pixels per unit.
	 * (useful for some algorithms like findminimum). All
	 * 
	 * @param view
	 *            view
	 * @param xmin
	 *            left x-coord
	 * @param xmax
	 *            right x-coord
	 * @param ymin
	 *            bottom y-coord
	 * @param ymax
	 *            top y-coord
	 * @param zmin
	 *            min z
	 * @param zmax
	 *            max z
	 * @param xscale
	 *            x scale (pixels per unit)
	 * @param yscale
	 *            y scale (pixels per unit)
	 * @param zscale
	 *            z scale
	 */
	final public void setEuclidianView3DBounds(int view, double xmin,
			double xmax, double ymin, double ymax, double zmin, double zmax,
			double xscale, double yscale, double zscale) {

		if (3 > this.xmin.length) {

			this.xmin = prolong(this.xmin, 3);
			this.xmax = prolong(this.xmin, 3);

			this.ymin = prolong(this.ymin, 3);
			this.ymax = prolong(this.ymax, 3);

			this.xscale = prolong(this.xscale, 3);
			this.yscale = prolong(this.yscale, 3);
		}

		this.xmin[2] = xmin;
		this.xmax[2] = xmax;
		this.ymin[2] = ymin;
		this.ymax[2] = ymax;
		this.zmin3 = zmin;
		this.zmax3 = zmax;
		this.xscale[2] = xscale;
		this.yscale[2] = yscale;
		this.zscale3 = zscale;

		notifyEuclidianViewCE();
	}

	@Override
	protected GeoPointND RigidPolygonPointOnCircle(GeoConicND circle,
			GeoPointND point1) {
		if (circle.isGeoElement3D()) {
			return getManager3D().Point3D(null, circle, point1.getInhomX(),
					point1.getInhomY(), point1.getInhomZ(), false, true);
		}
		return super.RigidPolygonPointOnCircle(circle, point1);
	}

	@Override
	protected void RigidPolygonAddEndOfCommand(StringBuilder sb, boolean is3D) {
		if (is3D) {
			sb.append("],xOyPlane]");
		} else {
			super.RigidPolygonAddEndOfCommand(sb, is3D);
		}

	}

	@Override
	public GeoElement wrapInVector(GeoPointND pt) {
		if (pt instanceof GeoPoint3D) {
			AlgoVectorPoint3D algo = new AlgoVectorPoint3D(cons, pt);
			cons.removeFromConstructionList(algo);
			return (GeoElement) algo.getVector();
		}
		AlgoVectorPoint algo = new AlgoVectorPoint(cons, pt);
		cons.removeFromConstructionList(algo);
		return (GeoElement) algo.getVector();
	}

}