/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * GeoPoint.java
 *
 * The point (x,y) has homogenous coordinates (x,y,1)
 *
 * Created on 30. August 2001, 17:39
 */

package org.geogebra.common.geogebra3D.kernel3D.geos;

import java.util.ArrayList;
import java.util.TreeSet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoDependentPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.LocateableList;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.PathNormalizer;
import org.geogebra.common.kernel.PathOrPoint;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.RegionParameters;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.advanced.AlgoDynamicCoordinates3D;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.commands.ParametricProcessor;
import org.geogebra.common.kernel.geos.ChangeableParent;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.Region3D;
import org.geogebra.common.kernel.kernelND.RotateableND;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * 
 * @author Markus + ggb3D
 */
public class GeoPoint3D extends GeoVec4D implements GeoPointND, PathOrPoint,
		MatrixTransformable, RotateableND,
		Transformable, MirrorableAtPlane {

	private boolean isInfinite;
	private boolean isDefined;
	private int pointSize;

	// mouse moving
	private Coords willingCoords = null; // = new Ggb3DVector( new double[]
	// {0,0,0,1.0});
	private Coords willingDirection = null; // new Ggb3DVector( new double[]
	// {0,0,1,0.0});

	// paths
	private Path path;
	private PathParameter pp;

	// region
	private Region region;
	private RegionParameters regionParameters;
	/** 2D x-coord when point is on a region */
	private double x2D = 0;
	/** 2D y-coord when point is on a region */
	private double y2D = 0;
	/** 2D z-coord when point is on a region (distance) */
	private double z2D = 0;

	/** temp inhomogeneous coordinates */
	public Coords inhom = Coords.createInhomCoorsInD3();
	private Coords inhom2D;
	private double zScale = 1;
	private boolean setEuclidianVisibleBySetParentAlgorithm = true;

	// list of Locateables (GeoElements) that this point is start point of
	// if this point is removed, the Locateables have to be notified
	private LocateableList locateableList;

	private ArrayList<NumberValue> changeableCoordNumbers = null;
	private boolean hasPolarParentNumbers = false;
	private CoordMatrix4x4 tmpMatrix4x4;
	private Coords tmpCoordsLength3;
	/** move mode */
	protected int moveMode = MOVE_MODE_TOOL_DEFAULT;

	private Coords moveNormalDirection;

	private boolean showUndefinedInAlgebraView = true;

	private Coords tmpCoords1;
	private Coords tmpCoords2;
	private Coords tmpCoords3;

	private Coords tmpWillingCoords;
	private Coords tmpWillingDirection;
	private Coords tmpCoordsOld;

	/** matrix used as orientation by the {@link Drawable3D} */
	private CoordMatrix4x4 m_drawingMatrix = null;
	private ArrayList<GeoElement> incidenceList;
	private boolean trace;

	private double animationValue;

	private ChangeableParent changeableParent = null;

	private static TreeSet<AlgoElement> tempSet;

	/**
	 * @return whether getCoordParentNumbers() returns polar variables (r; phi).
	 */
	private boolean hasPolarParentNumbers() {
		return hasPolarParentNumbers;
	}

	/**
	 * @param c
	 *            construction
	 */
	public GeoPoint3D(Construction c) {
		super(c);
		setDrawingMatrix(CoordMatrix4x4.identity());
		setCartesian3D();
		setUndefined();
		this.setIncidenceList(null);
	}

	/**
	 * Creates point on path
	 * 
	 * @param c
	 *            construction
	 * @param path
	 *            path
	 */
	public GeoPoint3D(Construction c, Path path) {
		super(c);
		setDrawingMatrix(CoordMatrix4x4.identity());
		setCartesian3D();
		setPath(path);
	}

	@Override
	public void setVisualStyle(GeoElement geo, boolean setAuxiliaryProperty) {
		super.setVisualStyle(geo, setAuxiliaryProperty);
		if (geo.isGeoPoint()) {
			setPointSize(((GeoPointND) geo).getPointSize());
			setPointStyle(((GeoPointND) geo).getPointStyle());
		} else if (geo instanceof PointProperties) {
			setPointSize(((PointProperties) geo).getPointSize());
			setPointStyle(((PointProperties) geo).getPointStyle());
		}
	}

	@Override
	public void setPath(Path path) {
		this.path = path;
	}

	/**
	 * Creates point in region
	 * 
	 * @param c
	 *            construction
	 * @param region
	 *            region
	 */
	public GeoPoint3D(Construction c, Region region) {
		super(c);
		setDrawingMatrix(CoordMatrix4x4.identity());
		setCartesian3D();
		setRegion(region);
	}

	@Override
	public void setRegion(Region region) {
		this.region = region;

	}

	// /////////////////////////////////////////////////////////
	// GeoPointND interface (TODO move it to abstract method)

	@Override
	public double distance(GeoPointND P) {
		return getInhomCoordsInD3().distance(P.getInhomCoordsInD3());
	}

	// /////////////////////////////////////////////////////////
	// COORDINATES

	@Override
	public double getX() {
		return getCoords().get(1);
	}

	@Override
	public double getY() {
		return getCoords().get(2);
	}

	@Override
	public double getZ() {
		return getCoords().get(3);
	}

	/**
	 * Sets homogenous coordinates and updates inhomogenous coordinates
	 * 
	 * @param v
	 *            coords
	 * @param doPathOrRegion
	 *            says if path (or region) calculations have to be done
	 */
	@Override
	final public void setCoords(Coords v, boolean doPathOrRegion) {
		super.setCoords(v);

		updateCoords();

		if (doPathOrRegion) {

			// region
			if (hasRegion()) {
				// Application.printStacktrace(getLabel());

				region.pointChangedForRegion(this);
			}

			// path
			if (isPointOnPath()) {
				// remember path parameter for undefined case
				// PathParameter tempPathParameter = getTempPathparameter();
				// tempPathParameter.set(getPathParameter());
				path.pointChanged(this);

				// make sure animation starts from the correct place
				animationValue = PathNormalizer.toNormalizedPathParameter(
						getPathParameter().t, path.getMinParameter(),
						path.getMaxParameter());

			}
			updateCoords();
		} else if (isPointOnPath()) {
			// make sure animation value is consistent with path parameter
			animationValue = PathNormalizer.toNormalizedPathParameter(
					getPathParameter().t, path.getMinParameter(),
					path.getMaxParameter());
		}

	}

	@Override
	final public void setCoords(Coords v) {
		setCoords(v, true);
	}

	@Override
	public void setCoordsFromPoint(GeoPointND point) {
		setCoords(point.getInhomCoordsInD3());
	}

	@Override
	final public void setCoords(double x, double y, double z, double w) {
		setWillingCoordsUndefined();
		setCoords(new Coords(x, y, z, w));
	}

	// sets from 2D coords
	@Override
	final public void setCoords(double x, double y, double z) {
		setCoords(x, y, 0, z);
	}

	@Override
	final public void updateCoords() {
		// infinite point
		// #5202
		if (!Double.isNaN(v.getW())
				&& DoubleUtil.isEpsilon(v.getW(), v.getX(), v.getY(), v.getZ())) {
			isInfinite = true;
			isDefined = !(Double.isNaN(v.get(1)) || Double.isNaN(v.get(2))
					|| Double.isNaN(v.get(3)));
			inhom.setX(Double.NaN);
			inhom.setY(Double.NaN);
			inhom.setZ(Double.NaN);
		}
		// finite point
		else {
			isInfinite = false;
			isDefined = v.isDefined();

			if (isDefined) {
				// make sure the z coordinate is always positive
				// this is important for the orientation of a line or ray
				// computed using two points P, Q with cross(P, Q)
				// TODO cast in GgbVector
				if (v.get(4) < 0) {
					for (int i = 1; i <= 4; i++) {
						v.set(i, (v.get(i)) * (-1.0));
					}
				}

				// update inhomogenous coords
				if (v.get(4) == 1.0) {
					inhom.set(1, v.get(1));
					inhom.set(2, v.get(2));
					inhom.set(3, v.get(3));
				} else {
					inhom.set(1, v.get(1) / v.get(4));
					inhom.set(2, v.get(2) / v.get(4));
					inhom.set(3, v.get(3) / v.get(4));
				}
			} else {
				inhom.setX(Double.NaN);
				inhom.setY(Double.NaN);
				inhom.setZ(Double.NaN);
			}
		}

		// sets the drawing matrix to coords
		getDrawingMatrix().setOrigin(getCoords());
	}

	/**
	 * @param v
	 *            inhomogeneous coordinates
	 */
	final public void setCoords(GeoVec3D v) {
		setCoords(v.x, v.y, v.z, 1.0);
	}

	/**
	 * Returns (x/w, y/w, z/w) GgbVector.
	 */
	@Override
	final public Coords getInhomCoords() {
		return inhom;
	}

	@Override
	public Coords getInhomCoordsInD(int dimension) {
		switch (dimension) {
		case 3:
			return getInhomCoordsInD3();
		case 2:
			return getInhomCoordsInD2();
		default:
			return null;
		}
	}

	@Override
	public Coords getInhomCoordsInD2() {

		if (inhom2D == null) {
			inhom2D = new Coords(2);
		}
		inhom2D.setX(inhom.getX());
		inhom2D.setY(inhom.getY());

		return inhom2D;
	}

	@Override
	public Coords getInhomCoordsInD3() {
		return inhom;
	}

	@Override
	final public double getInhomX() {
		return inhom.getX();
	}

	@Override
	final public double getInhomY() {
		return inhom.getY();
	}

	@Override
	final public double getInhomZ() {
		return inhom.getZ();
	}

	@Override
	public Coords getCoordsInD2IfInPlane(CoordSys coordSys) {

		if (setCoords2D(coordSys)) {
			return tmpCoordsLength3;
		}

		return null;
	}

	@Override
	public Coords getCoordsInD2(CoordSys coordSys) {
		setCoords2D(coordSys);
		return tmpCoordsLength3;
	}

	private boolean setCoords2D(CoordSys coordSys) {
		Coords coords;
		if (tmpCoords1 == null) {
			tmpCoords1 = Coords.createInhomCoorsInD3();
		}

		if (hasWillingCoords()) {
			coords = getWillingCoords();
		} else {
			// use real coords
			coords = getCoords();
		}

		// matrix for projection
		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}

		if (coordSys == null) { // project on plane xOy
			CoordMatrix4x4.identity(tmpMatrix4x4);
		} else {
			tmpMatrix4x4.set(coordSys.getMatrixOrthonormal());
		}

		if (!hasWillingDirection()) {
			// projection
			coords.projectPlaneInPlaneCoords(tmpMatrix4x4, tmpCoords1);
		} else {
			// use willing direction for projection
			coords.projectPlaneThruVIfPossibleInPlaneCoords(tmpMatrix4x4,
					getWillingDirection(), tmpCoords1);
		}

		if (tmpCoordsLength3 == null) {
			tmpCoordsLength3 = new Coords(3);
		}

		double w = tmpCoords1.getW();
		tmpCoordsLength3.setX(tmpCoords1.getX() / w);
		tmpCoordsLength3.setY(tmpCoords1.getY() / w);
		tmpCoordsLength3.setZ(1);

		return DoubleUtil.isZero(tmpCoords1.getZ());
	}

	@Override
	public Coords getCoordsInD(int dimension) {
		switch (dimension) {
		case 3:
			return getCoords();
		case 2:
			/*
			 * GgbVector coords; if (getWillingCoords()!=null) if
			 * (getWillingDirection()!=null){ //TODO use region matrix in place
			 * of identity
			 * coords=getWillingCoords().projectPlaneThruV(GgbMatrix4x4
			 * .Identity(), getWillingDirection())[1]; }else
			 * coords=getWillingCoords
			 * ().projectPlane(GgbMatrix4x4.Identity())[1]; else
			 * coords=getCoords(); GgbVector v = new GgbVector(3);
			 * v.setX(coords.getX()); v.setY(coords.getY());
			 * v.setZ(coords.getW()); return v;
			 */
			return getCoordsInD2();
		default:
			return null;
		}
	}

	@Override
	public Coords getCoordsInD2() {
		return getCoordsInD2(CoordSys.Identity3D);
	}

	@Override
	public Coords getCoordsInD3() {
		return getCoords();
	}

	/**
	 * Returns (x/w, y/w, z/w) GgbVector.
	 */
	@Override
	final public void getInhomCoords(double[] d) {
		double[] coords = getInhomCoords().get();
		for (int i = 0; i < d.length; i++) {
			d[i] = coords[i];
		}
	}

	@Override
	final public double[] vectorTo(GeoPointND QI) {
		GeoPoint3D Q = (GeoPoint3D) QI;
		// Application.debug("v=\n"+Q.getCoords().sub(getCoords()).get());
		return Q.getCoords().sub(getCoords()).get();
	}

	@Override
	public boolean movePoint(Coords rwTransVec, Coords endPosition) {
		boolean movedGeo = false;

		if (endPosition != null) {
			// setCoords(endPosition.x, endPosition.y, 1);
			// movedGeo = true;
		}

		// translate point
		else {

			Coords coords;
			Coords current = getInhomCoords();

			if (current.getLength() < rwTransVec.getLength()) {
				coords = current.add(rwTransVec);
			} else {
				coords = current.addSmaller(rwTransVec);
			}
			setCoords(coords);

			movedGeo = true;
		}

		return movedGeo;
	}

	// /////////////////////////////////////////////////////////
	// PATHS

	@Override
	final public boolean isPointOnPath() {
		return path != null;
	}

	@Override
	public Path getPath() {
		return path;
	}

	@Override
	final public PathParameter getPathParameter() {
		if (pp == null) {
			pp = new PathParameter(0);
		}
		return pp;
	}

	/**
	 * Updates coords from path
	 */
	final public void doPath() {
		path.pointChanged(this);
		// check if the path is a 2D path : in this case, 2D coords have been
		// modified
		if (!(path.toGeoElement().isGeoElement3D()
				|| path.toGeoElement().isGeoList())) {
			updateCoordsFrom2D(false, null);
		}
		updateCoords();

	}

	// copied on GeoPoint
	@Override
	public boolean isPointerChangeable() {
		return GeoPoint.isPointChangeable(this);
	}

	// /////////////////////////////////////////////////////////
	// REGION

	/**
	 * says if the point is in a Region
	 * 
	 * @return true if the point is in a Region
	 */
	@Override
	final public boolean hasRegion() {
		return region != null;
	}

	@Override
	final public boolean isPointInRegion() {
		return region != null;
	}

	/**
	 * Updates coords from region
	 */
	final public void doRegion() {
		region.pointChangedForRegion(this);

		updateCoords();
	}

	@Override
	final public RegionParameters getRegionParameters() {
		if (regionParameters == null) {
			regionParameters = new RegionParameters();
		}
		return regionParameters;
	}

	@Override
	final public Region getRegion() {
		return region;
	}

	/**
	 * update the 2D coords on the region (regarding willing coords and
	 * direction)
	 */
	@Override
	public void updateCoords2D() {
		if (region != null) { // use region 2D coord sys

			updateCoords2D(region, true);

		} else { // project on xOy plane
			x2D = getX();
			y2D = getY();
			z2D = getZ();
		}

	}

	/**
	 * update the 2D coords on the region (regarding willing coords and
	 * direction)
	 * 
	 * @param reg
	 *            region
	 * @param updateParameters
	 *            whether to update regionParameters
	 */
	public void updateCoords2D(Region reg, boolean updateParameters) {
		Coords coords;
		Coords[] project;
		if (!(reg instanceof Region3D)) {
			Log.warn(reg + " is not 3D region");
			return;
		}
		if (hasWillingCoords()) {
			coords = getWillingCoords();
		} else {
			// use real coords
			coords = getCoords();
		}

		if (!hasWillingDirection()) { // use normal direction for
			// projection
			project = ((Region3D) reg).getNormalProjection(coords);
			// coords.projectPlane(coordSys2D.getMatrix4x4());
		} else { // use willing direction for projection
			project = ((Region3D) reg).getProjection(getCoords(), coords,
					getWillingDirection());
			// project =
			// coords.projectPlaneThruV(coordSys2D.getMatrix4x4(),getWillingDirection());
		}

		x2D = project[1].get(1);
		y2D = project[1].get(2);
		z2D = project[1].get(3);

		if (updateParameters) {
			RegionParameters rp = getRegionParameters();
			rp.setT1(project[1].get(1));
			rp.setT2(project[1].get(2));
			rp.setNormal(reg.getMainDirection());
		}
	}

	/**
	 * set 2D coords
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	@Override
	public void setCoords2D(double x, double y, double z) {
		x2D = x / z;
		y2D = y / z;
	}

	@Override
	public double getX2D() {
		return x2D;
	}

	@Override
	public double getY2D() {
		return y2D;
	}

	/**
	 * @return inhomogenous 2D z
	 */
	public double getZ2D() {
		return z2D;
	}

	/**
	 * update 3D coords regarding 2D coords (if coordsys!=null, use it; else if
	 * region!=null, use its coord sys; else project on xOy plane)
	 * 
	 * @param doPathOrRegion
	 *            says if the path or the region calculations have to be done
	 */
	@Override
	public void updateCoordsFrom2D(boolean doPathOrRegion, CoordSys coordsys) {
		if (coordsys != null) {
			setCoords(coordsys.getPoint(getX2D(), getY2D()), doPathOrRegion);
		} else if (region != null) {
			/*
			 * if (getLabel().contains("B1")){
			 * Application.debug(getX2D()+","+getY2D()); if (getX2D()>3)
			 * Application.printStacktrace("ici"); }
			 */
			setCoords(((Region3D) region).getPoint(getX2D(), getY2D(),
					new Coords(4)), doPathOrRegion);
		} else {
			setCoords(new Coords(getX2D(), getY2D(), 0, 1), doPathOrRegion);
		}
	}

	@Override
	public void updateCoordsFrom2D(boolean doPathOrRegion) {
		updateCoordsFrom2D(doPathOrRegion, CoordSys.Identity3D);
	}

	// /////////////////////////////////////////////////////////
	// WILLING COORDS

	/**
	 * @param willingCoords
	 *            willing coordinates
	 */
	public void setWillingCoords(Coords willingCoords) {
		if (this.willingCoords == null) {
			this.willingCoords = Coords.createInhomCoorsInD3();
		}

		if (willingCoords == null || !willingCoords.isDefined()) {
			this.willingCoords.setUndefined();
		} else {
			this.willingCoords.set(willingCoords);
		}
	}

	/**
	 * Make willing coordinates undefined
	 */
	public void setWillingCoordsUndefined() {
		if (this.willingCoords == null) {
			this.willingCoords = Coords.createInhomCoorsInD3();
		}

		this.willingCoords.setUndefined();
	}

	/**
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 * @param w
	 *            inhomogeneous w
	 */
	public void setWillingCoords(double x, double y, double z, double w) {
		if (this.willingCoords == null) {
			this.willingCoords = Coords.createInhomCoorsInD3();
		}

		willingCoords.setX(x);
		willingCoords.setY(y);
		willingCoords.setZ(z);
		willingCoords.setW(w);
	}

	/**
	 * @param willingDirection
	 *            willing direction
	 */
	public void setWillingDirection(Coords willingDirection) {
		if (this.willingDirection == null) {
			this.willingDirection = new Coords(4);
		}

		if (willingDirection == null || !willingDirection.isDefined()) {
			this.willingDirection.setUndefined();
		} else {
			this.willingDirection.set(willingDirection);
		}
	}

	/**
	 * Make willing direction undefined
	 */
	public void setWillingDirectionUndefined() {
		if (this.willingDirection == null) {
			this.willingDirection = new Coords(4);
		}

		this.willingDirection.setUndefined();
	}

	/**
	 * @return willing coordinates
	 */
	public Coords getWillingCoords() {
		return willingCoords;
	}

	/**
	 * @return whether willing coordinates exist and are defined
	 */
	public boolean hasWillingCoords() {
		return willingCoords != null && willingCoords.isDefined();
	}

	/**
	 * @return willing direction
	 */
	public Coords getWillingDirection() {
		return willingDirection;
	}

	/**
	 * @return whether willing direction exist and are defined
	 */
	public boolean hasWillingDirection() {
		return willingDirection != null && willingDirection.isDefined();
	}

	/**
	 * set current zScale from this point (should be set from 3D view)
	 * @param scale z scale
	 */
	public void setZScale(double scale) {
		zScale = scale;
	}

	@Override
	public double getZScale() {
		return zScale;
	}

	// /////////////////////////////////////////////////////////
	// COMMON STUFF

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.POINT3D;
	}

	/**
	 * Copy constructor
	 * 
	 * @param point
	 *            original
	 */
	public GeoPoint3D(GeoPointND point) {
		super(point.getConstruction());
		setDrawingMatrix(CoordMatrix4x4.identity());
		set(point);
	}

	@Override
	public GeoPoint3D copy() {
		return new GeoPoint3D(this);
	}

	@Override
	final public boolean isGeoPoint() {
		return true;
	}

	@Override
	public boolean isDefined() {
		return isDefined;
	}

	/*
	 * public void set(GeoPointND P){ set((GeoElement) P); }
	 */
	@Override
	public void set(GeoElementND geo) {
		set(geo, true);
	}

	@Override
	public void set(GeoElementND geo, boolean macroFeedback) {
		if (geo.isGeoPoint()) {
			GeoPointND p = (GeoPointND) geo;
			if (p.getPathParameter() != null) {
				PathParameter pathParameter = getPathParameter();
				pathParameter.set(p.getPathParameter());
			}
			animationValue = p.getAnimationValue();
			setCoords(p);
			// TODO ? moveMode = p.getMoveMode();
			updateCoords();
			setMode(p.getToStringMode()); // complex etc
			reuseDefinition(geo);
		} else {
			setUndefined();
		}
		/*
		 * TODO else if (geo.isGeoVector()) { GeoVector v = (GeoVector) geo;
		 * setCoords(v.x, v.y, 1d); setMode(v.toStringMode); // complex etc }
		 */

	}

	@Override
	public void setUndefined() {
		setCoords(new Coords(Double.NaN, Double.NaN, Double.NaN, Double.NaN),
				false);
		setWillingCoordsUndefined();
		isDefined = false;
		isInfinite = false;

	}

	@Override
	public boolean showInEuclidianView() {
		return isDefined && !isInfinite;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return label
				+ GeoPoint.getEqualSign(getToStringMode(),
				tpl.getCoordStyle(kernel.getCoordStyle()), tpl)
				+ toValueString(tpl);
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		if (isInfinite()) {
			return "?";
		}
		if (tpl.hasCASType() && getDefinition() != null) {
			return getDefinition().toValueString(tpl);
		}
		StringBuilder sbToString = getSbBuildValueString();

		// boolean isVisibleInView2D = false;
		Coords p = getInhomCoordsInD3();

		if (getToStringMode() == Kernel.COORD_CARTESIAN_3D) {
			GeoPoint.buildValueStringCoordCartesian3D(kernel, tpl, p.getX(),
					p.getY(), p.getZ(), sbToString);
		} else if (getToStringMode() == Kernel.COORD_SPHERICAL) {
			GeoPoint.buildValueStringCoordSpherical(kernel, tpl, p.getX(),
					p.getY(), p.getZ(), sbToString);
		} else if (!DoubleUtil.isZero(p.getZ())) {
			if (getToStringMode() == Kernel.COORD_POLAR) {
				GeoPoint.buildValueStringCoordSpherical(kernel, tpl, p.getX(),
						p.getY(), p.getZ(), sbToString);
			} else {
				GeoPoint.buildValueStringCoordCartesian3D(kernel, tpl, p.getX(),
						p.getY(), p.getZ(), sbToString);
			}
		} else {
			GeoPoint.buildValueString(kernel, tpl, getToStringMode(), p.getX(),
					p.getY(), sbToString);
		}

		return sbToString.toString();
	}

	@Override
	public boolean isEqual(GeoElementND geo) {
		if (!geo.isGeoPoint()) {
			return false;
		}

		return isEqualPointND((GeoPointND) geo);

	}

	@Override
	public boolean isEqualPointND(GeoPointND P) {
		if (!(isDefined() && P.isDefined())) {
			return false;
		}

		// both finite
		if (isFinite() && P.isFinite()) {
			Coords c1 = getInhomCoords();
			Coords c2 = P.getInhomCoordsInD3();
			return DoubleUtil.isEqual(c1.getX(), c2.getX())
					&& DoubleUtil.isEqual(c1.getY(), c2.getY())
					&& DoubleUtil.isEqual(c1.getZ(), c2.getZ());
		} else if (isInfinite() && P.isInfinite()) {
			Coords c1 = getCoords();
			Coords c2 = P.getCoordsInD3();
			return c1.crossProduct(c2).equalsForKernel(0,
					Kernel.STANDARD_PRECISION);
		} else {
			return false;
		}
	}

	// /////////////////////////////////////
	// PointProperties

	@Override
	public int getPointSize() {
		return pointSize;
	}

	@Override
	public int getPointStyle() {
		// TODO
		return 0;
	}

	@Override
	public void setPointSize(int size) {
		pointSize = size;
	}

	@Override
	public void setPointStyle(int type) {
		// TODO

	}

	// ////////////////////////////////
	// XML

	/**
	 * returns all class-specific xml tags for saveXML GeoGebra File Format
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		// polar or cartesian coords
		switch (getToStringMode()) {
		case Kernel.COORD_POLAR:
			sb.append("\t<coordStyle style=\"polar\"/>\n");
			break;

		case Kernel.COORD_COMPLEX:
			sb.append("\t<coordStyle style=\"complex\"/>\n");
			break;

		case Kernel.COORD_CARTESIAN:
			sb.append("\t<coordStyle style=\"cartesian\"/>\n");
			break;

		case Kernel.COORD_SPHERICAL:
			sb.append("\t<coordStyle style=\"spherical\"/>\n");
			break;

		default:
			// don't save default (Kernel.COORD_CARTESIAN_3D)
		}

		// point size
		sb.append("\t<pointSize val=\"");
		sb.append(pointSize);
		sb.append("\"/>\n");

	}

	@Override
	public void appendStartPointXML(StringBuilder sb) {
		sb.append("\t<startPoint ");

		if (isAbsoluteStartPoint()) {
			sb.append("x=\"");
			sb.append(getCoords().get(1));
			sb.append("\" y=\"");
			sb.append(getCoords().get(2));
			sb.append("\" z=\"");
			sb.append(getCoords().get(3));
			sb.append("\" w=\"");
			sb.append(getCoords().get(4));
			sb.append("\"/>\n");
		} else {
			sb.append("exp=\"");
			StringUtil.encodeXML(sb, getLabel(StringTemplate.xmlTemplate));
			sb.append("\"/>\n");
		}
	}

	@Override
	final public boolean isAbsoluteStartPoint() {
		return isIndependent() && !isLabelSet();
	}

	// ////////////////////////////////
	// LocateableList

	@Override
	public LocateableList getLocateableList() {
		if (locateableList == null) {
			locateableList = new LocateableList(this);
		}
		return locateableList;
	}

	@Override
	public boolean hasLocateableList() {
		return locateableList != null;
	}

	@Override
	public void setLocateableList(LocateableList locateableList) {
		this.locateableList = locateableList;
	}

	/**
	 * Tells Locateables that their start point is removed and calls
	 * super.remove()
	 */
	@Override
	public void doRemove() {
		if (locateableList != null) {

			locateableList.doRemove();

		}

		super.doRemove();
	}

	/**
	 * Calls super.update() and updateCascade() for all registered locateables.
	 */
	@Override
	public void update(boolean drag) {
		super.update(drag);

		// update all registered locatables (they have this point as start
		// point)
		if (locateableList != null) {
			GeoElement.updateCascadeLocation(locateableList, cons);
		}
	}

	protected static TreeSet<AlgoElement> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<>();
		}
		return tempSet;
	}

	// ////////////////////////////////
	// GeoPoint2 interface

	@Override
	public boolean isFinite() {
		return isDefined && !isInfinite;
	}

	@Override
	public boolean isInfinite() {
		return isInfinite;
	}

	@Override
	public double[] getPointAsDouble() {
		return getInhomCoords().get();
	}

	@Override
	public Geo3DVec getVector() {
		return new Geo3DVec(kernel, getX(), getY(), getZ());
	}

	// ////////////////////////////////
	// display in a 2D view ?

	/*
	 * public boolean isVisibleInView(Object view){ if (view==((Application3D)
	 * app).getEuclidianView3D()) return true;
	 * 
	 * if (view==((Application3D) app).getEuclidianView()) return
	 * AbstractKernel.isZero(getCoords().getZ());
	 * 
	 * return false;
	 * 
	 * }
	 */

	// ////////////////////////////////
	// GeoElement3DInterface interface

	@Override
	public Coords getLabelPosition() {
		// Application.debug(inhom.toString());
		return getInhomCoordsInD3();
	}

	// ///////////////////////////////////////
	// MOVING THE POINT (3D)
	// ///////////////////////////////////////

	@Override
	public void switchMoveMode(int mode) {
		switch (moveMode) {
		case MOVE_MODE_XY:
			moveMode = MOVE_MODE_Z;
			break;
		case MOVE_MODE_Z:
			moveMode = MOVE_MODE_XY;
			break;
		case MOVE_MODE_TOOL_DEFAULT:
			if (mode == EuclidianConstants.MODE_MOVE) {
				moveMode = MOVE_MODE_Z;
			} else {
				moveMode = MOVE_MODE_XY;
			}
			break;
		default:
			// do nothing
			break;
		}
	}

	/**
	 * @param flag
	 *            move mode
	 */
	public void setMoveMode(int flag) {
		moveMode = flag;
	}

	@Override
	public int getMoveMode() {
		if (changeableParent != null) {
			return MOVE_MODE_NONE;
		}
		if (this.hasChangeableCoordParentNumbers()) {
			return moveMode;
		}
		if (!isIndependent()) {
			AlgoElement algo = getParentAlgorithm();
			if (algo instanceof AlgoDynamicCoordinates3D) {
				return moveMode;
			}
			return MOVE_MODE_NONE;
		}

		if (isLocked()) {
			return MOVE_MODE_NONE;
		}

		if (isPointOnPath()) {
			return MOVE_MODE_NONE; // too complicated to use MOVE_MODE_Z when
									// not lines
		}

		if (hasRegion()) {
			GeoElement geo = (GeoElement) region;
			if (geo.isGeoQuadric() && ((GeoQuadric3D) geo)
					.getType() == GeoQuadricNDConstants.QUADRIC_LINE) {
				return MOVE_MODE_NONE;
			}
			return MOVE_MODE_XY;
		}

		return moveMode;

	}

	/**
	 * 
	 * @return value of moveMode
	 */
	public int getRealMoveMode() {
		return moveMode;
	}

	/**
	 * sets the normal to moving directions (for region points)
	 * 
	 * @param d
	 *            direction
	 */
	public void setMoveNormalDirection(Coords d) {
		moveNormalDirection = d.copyVector();
	}

	/**
	 * 
	 * @return the normal to moving directions (for region points)
	 */
	public Coords getMoveNormalDirection() {
		return moveNormalDirection;
	}

	@Override
	public void showUndefinedInAlgebraView(boolean flag) {
		showUndefinedInAlgebraView = flag;
	}

	@Override
	public final boolean showInAlgebraView() {
		return isDefined || showUndefinedInAlgebraView;
	}

	@Override
	public void setParentAlgorithm(AlgoElement algorithm) {
		super.setParentAlgorithm(algorithm);
		if (algorithm != null) {
			// set colors to dependent colors
			setConstructionDefaults(setEuclidianVisibleBySetParentAlgorithm,
					false);
		}
	}

	/**
	 * if the point has a parent algorithm, we may don't want its visibility to
	 * be changed
	 */
	public void dontSetEuclidianVisibleBySetParentAlgorithm() {
		setEuclidianVisibleBySetParentAlgorithm = false;
	}

	@Override
	public void updateColumnHeadingsForTraceValues() {
		resetSpreadsheetColumnHeadings();

		spreadsheetColumnHeadings.add(getColumnHeadingText(new ExpressionNode(
				kernel, kernel.getAlgebraProcessor().getXBracket(), // "x("
				Operation.PLUS,
				new ExpressionNode(kernel, getNameGeo(), // Name[this]
						Operation.PLUS,
						kernel.getAlgebraProcessor().getCloseBracket())))); // ")"
		spreadsheetColumnHeadings.add(getColumnHeadingText(new ExpressionNode(
				kernel, kernel.getAlgebraProcessor().getYBracket(), // "y("
				Operation.PLUS,
				new ExpressionNode(kernel, getNameGeo(), // Name[this]
						Operation.PLUS,
						kernel.getAlgebraProcessor().getCloseBracket())))); // ")"
		spreadsheetColumnHeadings.add(getColumnHeadingText(new ExpressionNode(
				kernel, kernel.getAlgebraProcessor().getZBracket(), // "z("
				Operation.PLUS,
				new ExpressionNode(kernel, getNameGeo(), // Name[this]
						Operation.PLUS,
						kernel.getAlgebraProcessor().getCloseBracket())))); // ")"

	}

	@Override
	public TraceModesEnum getTraceModes() {
		return TraceModesEnum.SEVERAL_VALUES_OR_COPY;
	}

	@Override
	public String getTraceDialogAsValues() {
		String name = getLabelTextOrHTML(false);

		return "x("
				+ name
				+ "), y("
				+ name
				+ "), z("
				+ name
				+ ")";
	}

	@Override
	public void addToSpreadsheetTraceList(
			ArrayList<GeoNumeric> spreadsheetTraceList) {
		GeoNumeric xx = new GeoNumeric(cons, inhom.getX());
		spreadsheetTraceList.add(xx);
		GeoNumeric yy = new GeoNumeric(cons, inhom.getY());
		spreadsheetTraceList.add(yy);
		GeoNumeric zz = new GeoNumeric(cons, inhom.getZ());
		spreadsheetTraceList.add(zz);
	}

	@Override
	public void matrixTransform(double a, double b, double c, double d) {
		double x = getX();
		double y = getY();

		double x1 = a * x + b * y;
		double y1 = c * x + d * y;

		setCoords(x1, y1, getZ(), getW());
	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {

		double x = getX();
		double y = getY();
		double z = getZ();

		double x1 = a00 * x + a01 * y + a02 * z;
		double y1 = a10 * x + a11 * y + a12 * z;
		double z1 = a20 * x + a21 * y + a22 * z;

		setCoords(x1, y1, z1, getW());

	}

	@Override
	public boolean isMatrixTransformable() {
		return true;
	}

	@Override
	public int getDimension() {
		return 3;
	}

	@Override
	final public boolean isCasEvaluableObject() {
		return true;
	}

	@Override
	public void setCartesian() {
		setMode(Kernel.COORD_CARTESIAN);
	}

	@Override
	public void setCartesian3D() {
		setMode(Kernel.COORD_CARTESIAN_3D);
	}

	@Override
	public void setSpherical() {
		setMode(Kernel.COORD_SPHERICAL);
	}

	@Override
	public void setPolar() {
		setMode(Kernel.COORD_POLAR);
	}

	@Override
	public void setComplex() {
		setMode(Kernel.COORD_COMPLEX);
	}

	@Override
	final public void rotate(NumberValue phiValue) {
		double phi = phiValue.getDouble();
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);

		double x = getX();
		double y = getY();
		double z = getZ();

		setCoords(x * cos - y * sin, x * sin + y * cos, z, getW());
	}

	@Override
	final public void rotate(NumberValue phiValue, GeoPointND point) {
		rotate(phiValue, point.getInhomCoords());
	}

	@Override
	final public void rotate(NumberValue phiValue, Coords point) {
		double phi = phiValue.getDouble();
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);

		double x = getX();
		double y = getY();
		double z = getZ();
		double w = getW();

		double qx = w * point.getX();
		double qy = w * point.getY();

		setCoords((x - qx) * cos + (qy - y) * sin + qx,
				(x - qx) * sin + (y - qy) * cos + qy, z, w);
	}

	@Override
	public void rotate(NumberValue phiValue, GeoPointND S,
			GeoDirectionND orientation) {

		Coords o1 = S.getInhomCoordsInD3();
		Coords vn = orientation.getDirectionInD3();

		rotate(phiValue, o1, vn);

	}

	private void rotate(NumberValue phiValue, Coords o1, Coords vn) {

		rotate(phiValue.getDouble(), o1, vn);
	}

	/**
	 * rotate around line (point + vector) with angle phi
	 * 
	 * @param phi
	 *            angle
	 * @param o1
	 *            point
	 * @param vn
	 *            vector
	 */
	public void rotate(double phi, Coords o1, Coords vn) {
		if (vn.isZero() || Double.isNaN(phi)) {
			setUndefined();
			return;
		}

		Coords point = getInhomCoordsInD3();
		if (tmpCoords1 == null) {
			tmpCoords1 = Coords.createInhomCoorsInD3();
		}
		point.projectLine(o1, vn, tmpCoords1, null); // point projected on the
														// line

		if (tmpCoords2 == null) {
			tmpCoords2 = new Coords(4);
		}
		tmpCoords2.setSub(point, tmpCoords1);

		double cos = Math.cos(phi);
		double sin = Math.sin(phi);

		double l = vn.calcNorm();
		if (tmpCoords3 == null) {
			tmpCoords3 = new Coords(4);
		}
		tmpCoords3.setCrossProduct4(vn, tmpCoords2);

		setCoords(tmpCoords1.setAdd(tmpCoords1, tmpCoords2.setAdd(
				tmpCoords2.mulInside(cos), tmpCoords3.mulInside(sin / l))));
	}

	@Override
	public void rotate(NumberValue phiValue, GeoLineND line) {
		rotate(phiValue.getDouble(), line);
	}

	/**
	 * rotate around line with angle phi
	 * 
	 * @param phi
	 *            angle
	 * @param line
	 *            line
	 */
	public void rotate(double phi, GeoLineND line) {
		Coords o1 = line.getStartInhomCoords();
		Coords vn = line.getDirectionInD3();

		rotate(phi, o1, vn);

	}

	// ///////////////////////////
	// PATH OR POINT INTERFACE
	// ///////////////////////////

	@Override
	public void pointChanged(GeoPointND p) {
		if (p.isGeoElement3D()) {
			p.setCoords(this.getCoords(), false);
		} else {
			Coords coords = this.getCoords();
			if (!DoubleUtil.isZero(coords.getZ())) {
				p.setUndefined();
			} else {
				GeoPoint.pointChanged(p, coords.getX(), coords.getY(),
						coords.getW());
			}
		}
		p.getPathParameter().setT(0);
	}

	@Override
	public void pathChanged(GeoPointND PI) {
		pointChanged(PI);
	}

	@Override
	public boolean isOnPath(GeoPointND PI, double eps) {
		return isEqual(PI);
	}

	@Override
	public double getMinParameter() {
		return 0;
	}

	@Override
	public double getMaxParameter() {
		return 0;
	}

	@Override
	public boolean isClosedPath() {
		return false;
	}

	@Override
	public PathMover createPathMover() {
		return null;
	}

	@Override
	public double distanceToPath(PathOrPoint path1) {
		if (tmpCoordsOld == null) {
			tmpCoordsOld = new Coords(4);
		}
		tmpCoordsOld.set(getInhomCoords());

		if (tmpWillingCoords == null) {
			tmpWillingCoords = Coords.createInhomCoorsInD3();
		}
		if (tmpWillingDirection == null) {
			tmpWillingDirection = new Coords(4);
		}
		boolean hadWillingCoords;
		if (hasWillingCoords()) {
			hadWillingCoords = true;
			tmpWillingCoords.set(getWillingCoords());
		} else {
			hadWillingCoords = false;
			tmpWillingCoords.set(tmpCoordsOld);
		}
		if (hasWillingDirection()) {
			tmpWillingDirection.set(getWillingDirection());
		} else {
			tmpWillingDirection.setUndefined();
		}

		path1.pointChanged(this);

		double d;
		if (!tmpWillingDirection.isDefined()) {
			d = getInhomCoords().distance(tmpWillingCoords);
		} else {
			d = getInhomCoords().distLine(tmpWillingCoords,
					tmpWillingDirection);
			setWillingDirection(tmpWillingDirection);
		}

		if (hadWillingCoords) {
			setWillingCoords(tmpWillingCoords);
		}

		setCoords(tmpCoordsOld, false);

		return d;

	}

	/**
	 * returns a 4x4 matrix for drawing the {@link Drawable3D}
	 * 
	 * @return the drawing matrix
	 */
	public CoordMatrix4x4 getDrawingMatrix() {
		return m_drawingMatrix;
	}

	/**
	 * sets the 4x4 matrix for drawing the {@link Drawable3D} and the label
	 * 
	 * @param a_drawingMatrix
	 *            the drawing matrix
	 */
	public void setDrawingMatrix(CoordMatrix4x4 a_drawingMatrix) {
		this.m_drawingMatrix = a_drawingMatrix;
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

	// //////////////////////
	// MIRROR
	// //////////////////////

	@Override
	public void mirror(Coords Q) {
		double w = getW();
		double qx = w * Q.getX();
		double qy = w * Q.getY();
		double qz = w * Q.getZ();

		setCoords(2.0 * qx - getX(), 2.0 * qy - getY(), 2.0 * qz - getZ(), w);

	}

	@Override
	public void mirror(GeoLineND line) {
		Coords o1 = line.getStartInhomCoords();
		Coords vn = line.getDirectionInD3();

		Coords point = getInhomCoordsInD3();
		if (tmpCoords1 == null) {
			tmpCoords1 = Coords.createInhomCoorsInD3();
		}
		point.projectLine(o1, vn, tmpCoords1, null); // point projected on the
														// line

		// mirror at projected point
		mirror(tmpCoords1);
	}

	@Override
	public void mirror(GeoCoordSys2D plane) {
		if (tmpCoords1 == null) {
			tmpCoords1 = Coords.createInhomCoorsInD3();
		}

		getInhomCoordsInD3().projectPlane(
				plane.getCoordSys().getMatrixOrthonormal(), tmpCoords1);
		mirror(tmpCoords1);
	}

	// //////////////////////
	// DILATE
	// //////////////////////

	@Override
	public void dilate(NumberValue rval, Coords S) {
		double r = rval.getDouble();
		double temp = (1 - r);

		double w = getW();

		setCoords(r * getX() + temp * S.getX() * w,
				r * getY() + temp * S.getY() * w,
				r * getZ() + temp * S.getZ() * w, w);
	}

	// for identifying incidence by construction
	// case by case.
	// currently implemented for
	// lines: line by two point, intersect lines, line/conic, point on line
	// TODO: parallel line, perpenticular line
	/**
	 * @return list of objects incident by construction
	 */
	@Override
	public ArrayList<GeoElement> getIncidenceList() {
		return incidenceList;
	}

	/**
	 * @param list
	 *            list of objects incident by construction
	 */
	public void setIncidenceList(ArrayList<GeoElement> list) {
		if (list == null) {
			incidenceList = new ArrayList<>();
		} else {
			incidenceList = new ArrayList<>(list);
		}
	}

	/**
	 * initialize incidenceList
	 */
	public void createIncidenceList() {
		incidenceList = new ArrayList<>();
	}

	/**
	 * add geo to incidenceList of this, and also add this to pointsOnConic
	 * (when geo is a conic) or to pointsOnLine (when geo is a line)
	 * 
	 * @param geo
	 *            incident object
	 */
	@Override
	public void addIncidence(GeoElement geo, boolean isStartPoint) {
		if (incidenceList == null) {
			createIncidenceList();
		}
		if (!incidenceList.contains(geo)) {
			incidenceList.add(geo);
		}

		// GeoConicND, GeoLine, GeoPoint are the three types who have an
		// incidence list
		if (geo.isGeoConic()) {
			((GeoConicND) geo).addPointOnConic(this);
		} else if (geo.isGeoLine() && !isStartPoint) {
			((GeoLineND) geo).addPointOnLine(this);
		}
	}

	/**
	 * @param geo
	 *            incident geo to be removed
	 */
	@Override
	public final void removeIncidence(GeoElement geo) {
		if (incidenceList != null) {
			incidenceList.remove(geo);
		}

		if (geo.isGeoConic()) {
			((GeoConicND) geo).removePointOnConic(this);
		} else if (geo.isGeoLine()) {
			((GeoLineND) geo).removePointOnLine(this);
		}
	}

	@Override
	public boolean evaluatesTo3DVector() {
		return true;
	}

	@Override
	public void set(double param1, double param2, MyPoint leftPoint,
			MyPoint rightPoint) {

		setCoords(new Coords(param2 * leftPoint.x + param1 * rightPoint.x,
				param2 * leftPoint.y + param1 * rightPoint.y,
				param2 * leftPoint.getZ() + param1 * rightPoint.getZ(), 1.0),
				false);

		updateCoords();
	}

	@Override
	public void translate(Coords v0) {
		if (tmpCoords2 == null) {
			tmpCoords2 = new Coords(4);
		}
		tmpCoords2.setMul(v0, v.getW());
		v.addInside(tmpCoords2);
		setCoords(v);
	}

	@Override
	public GeoElementND doAnimationStep(double frameRate, GeoList parent) {
		return GeoPoint.doAnimationStep(frameRate, this, path, parent);
	}

	@Override
	public boolean isAnimatable() {
		return isPointOnPath() && isPointerChangeable();
	}

	@Override
	public double getAnimationValue() {
		return animationValue;
	}

	@Override
	public void setAnimationValue(double val) {
		animationValue = val;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.VECTOR3D;
	}

	@Override
	public ValidExpression toValidExpression() {
		return getVector();
	}

	@Override
	public void removePath() {
		path = null;
		pp = null;
	}

	/**
	 * @return parent sliders if this is defined as (a+a0,b+b0,c+c0)
	 */
	final public ArrayList<NumberValue> getCoordParentNumbers() {
		// init changeableCoordNumbers
		if (changeableCoordNumbers == null) {
			changeableCoordNumbers = new ArrayList<>(3);
			AlgoElement parentAlgo = getParentAlgorithm();

			// dependent point of form P = (a, b)
			if (parentAlgo instanceof AlgoDependentPoint3D) {
				AlgoDependentPoint3D algo = (AlgoDependentPoint3D) parentAlgo;
				ExpressionNode en = algo.getExpression();

				// (xExpression, yExpression)
				if (en.isLeaf() && en.getLeft() instanceof MyVec3DNode) {
					// (xExpression, yExpression)
					MyVec3DNode vn = (MyVec3DNode) en.getLeft();
					hasPolarParentNumbers = vn
							.getToStringMode() == Kernel.COORD_SPHERICAL
							|| vn.getToStringMode() == Kernel.COORD_POLAR;

					try {
						// try to get free number variables used in coords for
						// this point
						// don't allow expressions like "a + x(A)" for polar
						// coords (r; phi)
						ExpressionValue xcoord = vn.getX();
						ExpressionValue ycoord = vn.getY();
						ExpressionValue zcoord = vn.getZ();
						ParametricProcessor proc = kernel.getAlgebraProcessor()
								.getParamProcessor();
						NumberValue xNum = proc.getCoordNumber(xcoord);
						NumberValue yNum = proc.getCoordNumber(ycoord);
						NumberValue zNum = proc.getCoordNumber(zcoord);

						if (xNum instanceof GeoNumeric
								&& ((GeoNumeric) xNum).isPointerChangeable()) {
							changeableCoordNumbers.add(xNum);
						} else {
							changeableCoordNumbers.add(null);
						}
						if (yNum instanceof GeoNumeric
								&& ((GeoNumeric) yNum).isPointerChangeable()) {
							changeableCoordNumbers.add(yNum);
						} else {
							changeableCoordNumbers.add(null);
						}
						if (zNum instanceof GeoNumeric
								&& ((GeoNumeric) zNum).isPointerChangeable()) {
							changeableCoordNumbers.add(zNum);
						} else {
							changeableCoordNumbers.add(null);
						}
					} catch (Throwable e) {
						changeableCoordNumbers.clear();
						e.printStackTrace();
					}
				}
			}
		}

		return changeableCoordNumbers;
	}

	/**
	 * Used for polyhedron net: first polygon set it
	 * 
	 * @param cp
	 *            changeable parent
	 * 
	 */
	@Override
	final public void setChangeableParentIfNull(
			ChangeableParent cp) {
		if (changeableParent == null) {
			changeableParent = cp;
		}
	}

	@Override
	public boolean hasChangeableParent3D() {
		if (isLocked()) {
			return false;
		}
		return changeableParent != null;
	}

	@Override
	public ChangeableParent getChangeableParent3D() {
		return changeableParent;
	}

	/**
	 * Returns whether this point has three changeable numbers as coordinates,
	 * e.g. point A = (a, b, c) where a, b and c are free GeoNumeric objects.
	 */
	@Override
	final public boolean hasChangeableCoordParentNumbers() {
		// TODO why does this check only x,y?
		if (isLocked()) {
			return false;
		}

		ArrayList<NumberValue> coords = getCoordParentNumbers();
		if (coords.size() == 0) {
			return false;
		}

		NumberValue num1 = coords.get(0);
		NumberValue num2 = coords.get(1);

		if (num1 == null && num2 == null) {
			return false;
		}

		if (num1 instanceof GeoNumeric && num2 instanceof GeoNumeric) {
			GeoElement maxObj1 = GeoElement
					.as(((GeoNumeric) num1).getIntervalMaxObject());
			GeoElement maxObj2 = GeoElement
					.as(((GeoNumeric) num2).getIntervalMaxObject());
			GeoElement minObj1 = GeoElement
					.as(((GeoNumeric) num1).getIntervalMinObject());
			GeoElement minObj2 = GeoElement
					.as(((GeoNumeric) num2).getIntervalMinObject());
			if (maxObj1 != null && maxObj1.isChildOrEqual((GeoElement) num2)) {
				return false;
			}
			if (minObj1 != null && minObj1.isChildOrEqual((GeoElement) num2)) {
				return false;
			}
			if (maxObj2 != null && maxObj2.isChildOrEqual((GeoElement) num1)) {
				return false;
			}
			if (minObj2 != null && minObj2.isChildOrEqual((GeoElement) num1)) {
				return false;
			}
		}

		return (num1 instanceof GeoNumeric
				&& ((GeoNumeric) num1).isPointerChangeable())
				|| (num2 instanceof GeoNumeric
						&& ((GeoNumeric) num2).isPointerChangeable());
	}

	@Override
	public boolean moveFromChangeableCoordParentNumbers(Coords rwTransVec,
			Coords targetPosition, ArrayList<GeoElement> updateGeos,
			ArrayList<GeoElement> tempMoveObjectList) {

		if (!hasChangeableCoordParentNumbers()) {
			return false;
		}

		Coords endPosition = targetPosition;
		if (endPosition == null) {
			endPosition = getInhomCoords().add(rwTransVec);
		}

		// translate x and y coordinates by changing the parent coords
		// accordingly
		ArrayList<NumberValue> freeCoordNumbers = getCoordParentNumbers();
		NumberValue xvar = freeCoordNumbers.get(0);
		NumberValue yvar = freeCoordNumbers.get(1);
		NumberValue zvar = freeCoordNumbers.get(2);

		// polar coords (r; phi)
		if (hasPolarParentNumbers()) {
			// don't move

		}

		// cartesian coords (xvar + constant, yvar + constant)
		else {
			// only change if GeoNumeric
			if (xvar instanceof GeoNumeric) {
				GeoPoint.incrementParentNumeric(endPosition.getX() - getInhomX(),
						(GeoNumeric) xvar, targetPosition);
			}

			if (xvar != yvar && yvar instanceof GeoNumeric) {
				GeoPoint.incrementParentNumeric(endPosition.getY() - getInhomY(),
						(GeoNumeric) yvar, targetPosition);
			}

			if (zvar != yvar && zvar != xvar && zvar instanceof GeoNumeric) {
				GeoPoint.incrementParentNumeric(endPosition.getZ() - getInhomZ(),
						(GeoNumeric) zvar, targetPosition);
			}
		}

		if (xvar instanceof GeoNumeric) {
			addParentToUpdateList((GeoNumeric) xvar,
					updateGeos, tempMoveObjectList);
		}
		if (yvar instanceof GeoNumeric) {
			addParentToUpdateList((GeoNumeric) yvar,
					updateGeos, tempMoveObjectList);
		}
		if (zvar instanceof GeoNumeric) {
			addParentToUpdateList((GeoNumeric) zvar,
					updateGeos, tempMoveObjectList);
		}

		return true;
	}

	@Override
	final public String toStringDescription(StringTemplate tpl) {
		boolean isAvDescrip = Kernel.ALGEBRA_STYLE_DESCRIPTION == getKernel().getAlgebraStyle();
		if (isAvDescrip) {
			return getKernel().getLocalization().getMenu("Point") + " " + label;
		}
		return toString(tpl);
	}

	@Override
	public boolean isWhollyIn2DView(EuclidianView ev) {
		return DoubleUtil.isZero(inhom.getZ());
	}

	/**
	 * Increments path parameter
	 * 
	 * @param a
	 *            increment
	 */
	@Override
	public void addToPathParameter(double a) {
		PathParameter parameter = getPathParameter();
		parameter.t += a;

		// update point relative to path
		path.pathChanged(this);
		updateCoords();

		// make sure point is still on path
		path.pointChanged(this);
	}

	@Override
	public void addAuralOperations(Localization loc, ScreenReaderBuilder sb) {
		GeoPoint.addAuralArrows(loc, sb, this);
		super.addAuralOperations(loc, sb);
	}

	@Override
	public String getAuralTextForMove() {
		return GeoPoint.pointMovedAural(kernel.getLocalization(), this);
	}

	@Override
	public boolean showPointProperties() {
		return true;
	}

	@Override
	public void setRegionChanged(double x, double y) {
		setCoords2D(x, y, 1);
		updateCoordsFrom2D(false, null);
	}

	@Override
	public void pointChanged(GeoPolygon polygon) {
		Coords coordsOld = getInhomCoords().copyVector();

		// prevent from region bad coords calculations
		Region oldRegion = getRegion();
		setRegion(null);

		double minDist = Double.POSITIVE_INFINITY;
		Coords res = null;
		double param = 0;

		// use auxiliary segment if no or not enough segments
		GeoSegment3D segment = null;
		GeoSegmentND[] segments = polygon.getSegments();
		if (segments == null || segments.length < polygon.getPointsLength()) {
			segment = new GeoSegment3D(cons);
		}

		// find closest point on each segment
		for (int i = 0; i < polygon.getPointsLength(); i++) {

			setCoords(coordsOld, false); // prevent circular path.pointChanged

			if (segment == null) {
				segments[i].pointChanged(this);
			} else {
				segment.setCoordFromPoints(polygon.getPoint3D(i),
						polygon.getPoint3D(
								(i + 1) % polygon.getPointsLength()));
				segment.pointChanged(this);
			}

			double dist; // = P.getInhomCoords().sub(coordsOld).squareNorm();
			// double dist = 0;
			if (hasWillingCoords() && hasWillingDirection()) {
				dist = getInhomCoords().distLine(getWillingCoords(),
						getWillingDirection());
			} else {
				dist = getInhomCoords().sub(coordsOld).squareNorm();
			}

			if (dist < minDist) {
				minDist = dist;
				// remember closest point
				res = getInhomCoords().copyVector();
				param = i + pp.getT();
				// Application.debug(i);
			}
		}

		if (res != null) {
			setCoords(res, false);
			pp.setT(param);
		}

		setRegion(oldRegion);
	}

}
