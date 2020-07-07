package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.RotateableND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.debug.Log;

/**
 * Class extending {@link GeoPolygon} in 3D world.
 * 
 * @author ggb3D
 * 
 */
public class GeoPolyLine3D extends GeoPolyLine implements RotateableND,
		MirrorableAtPlane {

	private int index1;
	private int index2;
	private Coords direction1 = null;
	private Coords direction2 = null;
	private Coords direction3 = null;

	/** for possibly planar object */
	private boolean isPlanar = false;
	private Coords normal = null;
	private Coords tmpCoords;

	private GeoSegment3D seg = new GeoSegment3D(cons);

	/**
	 * common constructor for 3D.
	 * 
	 * @param c
	 *            the construction
	 * @param points
	 *            vertices
	 */
	public GeoPolyLine3D(Construction c, GeoPointND[] points) {
		super(c, points);
	}

	/**
	 * @param c
	 *            construction
	 */
	public GeoPolyLine3D(Construction c) {
		super(c);
	}

	// ///////////////////////////////////////
	// GeoPolyLine3D

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.POLYLINE3D;
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

	// ///////////////////////////////////////
	// Overwrite GeoPolyLine

	@Override
	public GeoElement copyInternal(Construction cons1) {
		GeoPolyLine3D ret = new GeoPolyLine3D(cons1, null);
		ret.points = GeoElement.copyPointsND(cons1, points);
		ret.set(this);
		return ret;
	}

	@Override
	public void set(GeoElementND geo) {
		if (!(geo instanceof GeoPolyLine)) {
			Log.error("wrong class");
			return;
		}
		GeoPolyLine poly = (GeoPolyLine) geo;
		length = poly.getLength();
		defined = poly.isDefined();

		if (!defined || poly.getPointsND() == null) {
			return;
		}

		// make sure both arrays have same size
		if (points.length != poly.getPointsND().length) {
			GeoPointND[] tempPoints = new GeoPointND[poly.getPointsND().length];
			for (int i = 0; i < tempPoints.length; i++) {
				tempPoints[i] = i < points.length ? points[i]
						: createNewPoint();
			}
			points = tempPoints;
		}
		for (int i = 0; i < points.length; i++) {
			points[i].set(poly.getPointsND()[i]);
		}
	}

	/**
	 * The only place where GeoPoint3D is directly referred to
	 * 
	 * @return 3D point
	 */
	protected GeoPointND createNewPoint() {
		return new GeoPoint3D(cons);
	}

	private void setSegmentPoints(GeoPointND geoPoint, GeoPointND geoPoint2) {
		seg.setCoord(geoPoint, geoPoint2);
	}

	@Override
	public boolean isOnPath(GeoPointND P, double eps) {

		if (P.getPath() == this) {
			return true;
		}

		// check if P is on one of the segments
		for (int i = 0; i < points.length - 1; i++) {
			setSegmentPoints(points[i], points[i + 1]);
			if (seg.isOnPath(P, eps)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void pathChanged(GeoPointND P) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(P)) {
			pointChanged(P);
			return;
		}
		if (points.length == 1) {
			P.setCoords(points[0].getCoordsInD3(), false);
			return;
		}
		// parameter is between 0 and points.length - 1,
		// i.e. floor(parameter) gives the point index
		int index;

		PathParameter pp = P.getPathParameter();
		double t = pp.getT();
		if (t == points.length - 1) { // at very end of path
			index = points.length - 2;
		} else {
			t = t % (points.length - 1);
			if (t < 0) {
				t += (points.length - 1);
			}
			index = (int) Math.floor(t);
		}
		setSegmentPoints(points[index], points[index + 1]);

		double segParameter = t - index;

		// calc point for given parameter; must NOT doPathOrRegion
		if (tmpCoords == null) {
			tmpCoords = new Coords(4);
		}
		seg.getPointCoords(segParameter, tmpCoords);
		P.setCoords(tmpCoords, false);

		pp.setT(t);
	}

	@Override
	public void pointChanged(GeoPointND P) {

		// double qx = P.x/P.z;
		// double qy = P.y/P.z;
		// double minDist = Double.POSITIVE_INFINITY;
		// double resx=0, resy=0, resz=0, param=0;
		PathParameter pp = P.getPathParameter();
		double t = pp.getT();
		double localT = 0;
		int index;

		// find projection on the line of current segment
		// if the projection is out of the segment, look at the next (or the
		// previous) segment
		index = (int) Math.floor(t);
		// direction indicates in which way we are stepping to prevent infinite
		// loop
		int direction = 0;
		while (index >= 0 && index < getNumPoints() - 1) {
			setSegmentPoints(points[index], points[index + 1]);
			localT = seg.getParamOnLine(P);
			if (localT < 0 && direction <= 0) {
				direction = -1;
				index--;
			} else if (localT > 1 && direction >= 0) {
				direction = 1;
				index++;
			} else {
				break;
			}
		}

		if (index >= getNumPoints() - 1) {
			index = getNumPoints() - 1;
		} else if (index < 0) {
			index = 0;
		}

		t = index + Math.min(1, Math.max(0, localT));
		pp.setT(t);

		// update point using pathChanged
		pathChanged(P);
	}

	@Override
	public void matrixTransform(double a00, double a01, double a10,
			double a11) {
		// TODO
	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		// TODO
	}

	/**
	 * @deprecated use getPointND(int i)
	 */
	@Deprecated
	@Override
	public GeoPoint getPoint(int i) {
		return null;
	}

	// /////////////////////
	// /isPlanar

	/**
	 * @return whether this is in plane
	 */
	public boolean isPlanar() {
		return isPlanar;
	}

	/**
	 * Update the planar flag
	 */
	public void calcIsPlanar() {
		if (!isDefined()) {
			return;
		}
		if (getNumPoints() <= 3) {
			isPlanar = true;
			return;
		}

		normal = null;
		index1 = index2 = 0;
		direction1 = direction2 = direction3 = null;

		for (; index1 < getNumPoints() - 1; index1++) {
			if (!points[index1].getInhomCoordsInD3().equalsForKernel(
					points[0].getInhomCoordsInD3(),
					Kernel.STANDARD_PRECISION)) {
				direction1 = points[index1].getInhomCoordsInD3()
						.sub(points[0].getInhomCoordsInD3());
				break;
			}
		}

		if (direction1 == null) {
			isPlanar = true;
			return;
		}

		for (index2 = index1 + 1; index2 < getNumPoints(); index2++) {
			direction2 = points[index2].getInhomCoordsInD3()
					.sub(points[index1].getInhomCoordsInD3());
			normal = direction1.crossProduct(direction2);
			if (!normal.equalsForKernel(new Coords(0, 0, 0),
					Kernel.STANDARD_PRECISION)) {
				break;
			}
			direction2 = null;
			normal = null;
		}

		if (direction2 == null || index2 == getNumPoints() - 1) {
			isPlanar = true;
			return;
		}

		if (index2 + 1 < getNumPoints()) {
			direction3 = points[index2 + 1].getInhomCoordsInD3()
					.sub(points[index2].getInhomCoordsInD3());
			if (!direction3.crossProduct(normal).equalsForKernel(
					new Coords(0, 0, 0), Kernel.STANDARD_PRECISION)) {
				isPlanar = false;
				return;
			}
			isPlanar = true;
			direction3 = null;
			return;
		}
	}

	@Override
	public void rotate(NumberValue r, GeoPointND S, GeoDirectionND orientation) {
		for (int i = 0; i < points.length; i++) {
			((RotateableND) points[i]).rotate(r, S, orientation);
		}
	}

	@Override
	public void rotate(NumberValue r, GeoLineND line) {
		for (int i = 0; i < points.length; i++) {
			((RotateableND) points[i]).rotate(r, line);
		}
	}

	@Override
	public void mirror(GeoCoordSys2D plane) {
		for (int i = 0; i < points.length; i++) {
			((MirrorableAtPlane) points[i]).mirror(plane);
		}
	}

}
