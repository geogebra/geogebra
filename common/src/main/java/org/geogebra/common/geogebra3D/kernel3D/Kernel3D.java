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

import org.geogebra.common.geogebra3D.io.MyXMLHandler3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoDispatcher3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoElement3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPointVector3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoVectorPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.Manager3D;
import org.geogebra.common.geogebra3D.kernel3D.arithmetic.ExpressionNodeEvaluator3D;
import org.geogebra.common.geogebra3D.kernel3D.commands.AlgebraProcessor3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoAxis3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoElement3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3DConstant;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSpace;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.io.MyXMLHandler;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionCompanion;
import org.geogebra.common.kernel.EVProperty;
import org.geogebra.common.kernel.GeoFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Manager3DInterface;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
import org.geogebra.common.kernel.algos.AlgoPointVector;
import org.geogebra.common.kernel.algos.AlgoVectorPoint;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCoords4D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoRayND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.App;
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
	private double zmin3;
	private double zmax3;
	private double zscale3;

	/**
	 * @param app
	 *            application
	 * @param factory
	 *            factory for geos
	 */
	public Kernel3D(App app, GeoFactory factory) {
		super(app, factory);
	}

	@Override
	public GeoAxis3D getZAxis3D() {
		return (GeoAxis3D) cons.getZAxis();
	}

	@Override
	public GeoPlane3DConstant getXOYPlane() {
		return (GeoPlane3DConstant) cons.getXOYPlane();
	}

	@Override
	public GeoSpace getSpace() {
		return (GeoSpace) cons.getSpace();
	}

	/*
	 * ******************************************* Methods for 3D manager
	 * *******************************************
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

	/*
	 * ******************************************* Methods for MyXMLHandler
	 * *******************************************
	 */

	/**
	 * creates the 3D construction cons
	 */
	@Override
	protected void newConstruction() {
		cons = new Construction(this);
	}

	@Override
	public MyXMLHandler newMyXMLHandler(Kernel kernel,
			Construction construction) {
		return new MyXMLHandler3D(kernel, construction);
	}

	@Override
	public ExpressionNodeEvaluator newExpressionNodeEvaluator(Kernel kernel) {
		return new ExpressionNodeEvaluator3D(app.getLocalization(), kernel,
				app.getConfig().createOperationArgumentFilter());
	}

	/**
	 * @param kernel
	 *            kernel
	 * @return a new algebra processor (used for 3D)
	 */
	@Override
	public AlgebraProcessor newAlgebraProcessor(Kernel kernel) {
		return new AlgebraProcessor3D(kernel, app.newCommandDispatcher(kernel));
	}

	/** return all points of the current construction */
	@Override
	public TreeSet<GeoElement> getPointSet() {
		TreeSet<GeoElement> t3d = getConstruction()
				.getGeoSetLabelOrder(GeoClass.POINT3D);
		TreeSet<GeoElement> t = super.getPointSet();

		t.addAll(t3d);
		// TODO add super.getPointSet()
		return t;
	}

	/*
	 * ******************************************* Methods for MyXMLHandler
	 * *******************************************
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

		if (geo instanceof GeoConic3D && geo.isIndependent()) {
			try {
				double ox = Double.parseDouble(attrs.get("ox"));
				double oy = Double.parseDouble(attrs.get("oy"));
				double oz = Double.parseDouble(attrs.get("oz"));
				double ow = Double.parseDouble(attrs.get("ow"));

				// direction
				double vx = Double.parseDouble(attrs.get("vx"));
				double vy = Double.parseDouble(attrs.get("vy"));
				double vz = Double.parseDouble(attrs.get("vz"));
				double wx = Double.parseDouble(attrs.get("wx"));
				double wy = Double.parseDouble(attrs.get("wy"));
				double wz = Double.parseDouble(attrs.get("wz"));
				CoordSys cs = ((GeoConic3D) geo).getCoordSys();
				if (cs == null) {
					cs = new CoordSys(2);
				}
				cs.addPoint(new Coords(ox, oy, oz, ow));
				cs.addVector(new Coords(vx, vy, vz));
				cs.addVector(new Coords(wx, wy, wz));
				cs.makeOrthoMatrix(false, false);
				// cs.makeOrthoMatrix(true, true);
				((GeoConic3D) geo).setCoordSys(cs);
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		if (!(geo instanceof GeoCoords4D)) {
			return super.handleCoords(geo, attrs);
		}
		if (geo.getParentAlgorithm() != null
				&& !geo.isPointInRegion() && !geo.isPointOnPath()) {
			// the coords from XML are redundant and may be buggy (see APPS-1382)
			return true;
		}
		try {
			double x = Double.parseDouble(attrs.get("x"));
			double y = Double.parseDouble(attrs.get("y"));
			double z = Double.parseDouble(attrs.get("z"));
			double w = Double.parseDouble(attrs.get("w"));
			((GeoCoords4D) geo).setCoords(x, y, z, w);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public GeoPlane3D getDefaultPlane() {
		return getXOYPlane();
	}

	// //////////////////////////////////
	// 2D FACTORY EXTENSION
	// //////////////////////////////////

	@Override
	final public GeoRayND rayND(String label, GeoPointND P, GeoPointND Q) {
		if (P.isGeoElement3D() || Q.isGeoElement3D()) {
			return getManager3D().ray3D(label, P, Q);
		}
		return super.ray(label, (GeoPoint) P, (GeoPoint) Q);
	}

	@Override
	final public GeoSegmentND segmentND(String label, GeoPointND P,
			GeoPointND Q) {

		if (P.isGeoElement3D() || Q.isGeoElement3D()) {
			return getManager3D().segment3D(label, P, Q);
		}
		return super.segment(label, (GeoPoint) P, (GeoPoint) Q);
	}

	@Override
	final public GeoElement[] polygonND(String[] labels, GeoPointND[] P) {

		boolean is3D = false;
		for (int i = 0; i < P.length && !is3D; i++) {
			if (P[i].isGeoElement3D()) {
				is3D = true;
			}
		}

		if (is3D) {
			return getManager3D().polygon3D(labels, P);
		}
		return super.polygon(labels, P);
	}

	@Override
	public GeoElement[] polyLineND(String label, GeoPointND[] P) {

		boolean is3D = false;
		for (int i = 0; i < P.length && !is3D; i++) {
			if (P[i].isGeoElement3D()) {
				is3D = true;
			}
		}

		if (is3D) {
			return getManager3D().polyLine3D(label, P);
		}
		return super.polyLine(label, P);

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
		prolongGraphicsBoundArrays(3);

		this.xmin[2] = xmin;
		this.xmax[2] = xmax;
		this.ymin[2] = ymin;
		this.ymax[2] = ymax;
		this.zmin3 = zmin;
		this.zmax3 = zmax;
		this.xscale[2] = xscale;
		this.yscale[2] = yscale;
		this.zscale3 = zscale;

		notifyEuclidianViewCE(EVProperty.ZOOM);
	}

	@Override
	public GeoPointND rigidPolygonPointOnCircle(GeoConicND circle,
			GeoPointND point1) {
		if (circle.isGeoElement3D()) {
			return getManager3D().point3D(null, circle, point1.getInhomX(),
					point1.getInhomY(), point1.getInhomZ(), false, true);
		}
		return super.rigidPolygonPointOnCircle(circle, point1);
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

	/**
	 * 
	 * @param vec
	 *            vector
	 * @return Point[vector]
	 */
	@Override
	public GeoPointND wrapInPoint(GeoVectorND vec) {
		if (vec instanceof GeoVector3D) {
			AlgoPointVector3D algo = new AlgoPointVector3D(cons,
					cons.getOrigin(), vec);
			cons.removeFromConstructionList(algo);
			return algo.getQ();
		}
		AlgoPointVector algo = new AlgoPointVector(cons, cons.getOrigin(), vec);
		cons.removeFromConstructionList(algo);
		return algo.getQ();
	}

	@Override
	public ConstructionCompanion createConstructionCompanion(
			Construction cons1) {
		return new ConstructionCompanion3D(cons1);
	}
}