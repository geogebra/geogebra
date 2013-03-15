package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.PathParameter;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.plugin.GeoClass;
import geogebra3D.euclidian3D.Drawable3D;

/**
 * Class extending {@link GeoPolygon} in 3D world.
 * 
 * @author ggb3D
 * 
 */
public class GeoPolyLine3D extends GeoPolyLine implements
		GeoElement3DInterface {

	private boolean defined = false;

	private int index1;
	private int index2;
	private Coords direction1 = null;
	private Coords direction2 = null;
	private Coords direction3 = null;

	/** link with drawable3D */
	private Drawable3D drawable3D = null;

	/** for possibly planar object */
	private boolean isPlanar = false;
	private Coords normal = null;

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

	// ///////////////////////////////////////
	// Overwrite GeoPolyLine

	@Override
	public GeoElement copyInternal(Construction cons) {
		GeoPolyLine3D ret = new GeoPolyLine3D(cons, null);
		ret.points = GeoElement.copyPointsND(cons, points);
		ret.set(this);
		return ret;
	}

	@Override
	public void set(GeoElement geo) {
		GeoPolyLine3D poly = (GeoPolyLine3D) geo;
		length = poly.length;
		defined = poly.defined;

		// make sure both arrays have same size
		if (points.length != poly.points.length) {
			GeoPointND[] tempPoints = new GeoPointND[poly.points.length];
			for (int i = 0; i < tempPoints.length; i++) {
				tempPoints[i] = i < points.length ? points[i]
						: createNewPoint();
			}
			points = tempPoints;
		}

		for (int i = 0; i < points.length; i++) {
			points[i].set(poly.points[i]);
		}
	}

	// The only place where GeoPoint3D is directly referred to
	protected GeoPointND createNewPoint() {
		return new GeoPoint3D(cons);
	}

	private GeoSegment3D seg = new GeoSegment3D(cons);

	private void setSegmentPoints(GeoPointND geoPoint, GeoPointND geoPoint2) {
		seg.setCoord(geoPoint, geoPoint2);
	}

	@Override
	public boolean isOnPath(GeoPointND P, double eps) {

		if (P.getPath() == this)
			return true;

		// check if P is on one of the segments
		for (int i = 0; i < points.length - 1; i++) {
			setSegmentPoints(points[i], points[i + 1]);
			if (seg.isOnPath(P, eps))
				return true;
		}
		return false;
	}

	@Override
	public void pathChanged(GeoPointND P) {
		
		//if kernel doesn't use path/region parameters, do as if point changed its coords
		if(!getKernel().usePathAndRegionParameters(P)){
			pointChanged(P);
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
			if (t < 0)
				t += (points.length - 1);
			index = (int) Math.floor(t);
		}
		setSegmentPoints(points[index], points[index + 1]);

		double segParameter = t - index;

		// calc point for given parameter; must NOT doPathOrRegion
		P.setCoords(seg.getPointCoords(segParameter), false);

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

		while (index >= 0 && index < getNumPoints() - 1) {
			setSegmentPoints(points[index], points[index + 1]);
			localT = seg.getParamOnLine(P);
			if (localT < 0)
				index--;
			else if (localT > 1)
				index++;
			else
				break;
		}

		if (index >= getNumPoints() - 1)
			index = getNumPoints() - 1;
		else if (index < 0)
			index = 0;

		t = index + Math.min(1, Math.max(0, localT));
		pp.setT(t);

		// udpate point using pathChanged
		pathChanged(P);
	}

	@Override
	public void calcLength() {

		// last point not checked in loop
		if (!points[points.length - 1].isDefined()) {
			setUndefined();
			length = Double.NaN;
			return;
		}

		length = 0;

		for (int i = 0; i < points.length - 1; i++) {
			if (!points[i].isDefined()) {
				setUndefined();
				length = Double.NaN;
				return;
			}
			setSegmentPoints(points[i], points[i + 1]);
			length += seg.getLength();
		}
		setDefined();
	}

	@Override
	public void rotate(NumberValue r) {
		return; // TODO
	}

	public void rotate(NumberValue r, GeoPointND S) {
		return; // TODO
	}

	@Override
	public void matrixTransform(double a00, double a01, double a10, double a11) {
		return; // TODO
	}

	@Override
	public void translate(Coords v) {
		return; // TODO
	}

	public void dilate(NumberValue r, GeoPointND S) {
		return; // TODO
	}

	public void mirror(GeoPointND Q) {
		return; // TODO
	}

	@Override
	public void mirror(GeoLine g) {
		return; // TODO
	}

	@Override
	public boolean isAllVertexLabelsSet() {
		for (int i = 0; i < points.length; i++)
			if (!points[i].isLabelSet())
				return false;
		return true;
	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		return; // TODO
	}

	/**
	 * @deprecated use getPointND(int i)
	 */
	@Override
	public GeoPoint getPoint(int i) {
		return null;
	}

	/**
	 * @deprecated
	 */
	@Override
	public void toGeoCurveCartesian(GeoCurveCartesian curve) {
		return;
	}

	// /////////////////////
	// /isPlanar

	public boolean isPlanar() {
		return isPlanar;
	}

	public void calcIsPlanar() {
		if (!isDefined())
			return;
		if (getNumPoints() <= 3) {
			isPlanar = true;
			return;
		}

		normal = null;
		index1 = index2 = 0;
		direction1 = direction2 = direction3 = null;

		for (; index1 < getNumPoints() - 1; index1++) {
			if (!points[index1].getInhomCoordsInD(3).equalsForKernel(
					points[0].getInhomCoordsInD(3), Kernel.STANDARD_PRECISION)) {
				direction1 = points[index1].getInhomCoordsInD(3).sub(
						points[0].getInhomCoordsInD(3));
				break;
			}
		}

		if (direction1 == null) {
			isPlanar = true;
			return;
		}

		for (index2 = index1 + 1; index2 < getNumPoints(); index2++) {
			direction2 = points[index2].getInhomCoordsInD(3).sub(
					points[index1].getInhomCoordsInD(3));
			normal = direction1.crossProduct(direction2);
			if (!normal.equalsForKernel(new Coords(0, 0, 0), Kernel.STANDARD_PRECISION)) {
				break;
			} else {
				direction2 = null;
				normal = null;
			}
		}

		if (direction2 == null || index2 == getNumPoints() - 1) {
			isPlanar = true;
			return;
		}

		if (index2 + 1 < getNumPoints()) {
			direction3 = points[index2 + 1].getInhomCoordsInD(3).sub(
					points[index2].getInhomCoordsInD(3));
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


	public boolean hasGeoElement2D() {
		// TODO Auto-generated method stub
		return false;
	}

	public GeoElement getGeoElement2D() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setGeoElement2D(GeoElement geo) {
		// TODO Auto-generated method stub

	}

}
