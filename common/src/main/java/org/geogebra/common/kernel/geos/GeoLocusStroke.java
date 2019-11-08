package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.MyMath;

import java.util.ArrayList;

/**
 * Class for polylines created using pen
 * 
 * @author Zbynek
 */
public class GeoLocusStroke extends GeoLocus
		implements MatrixTransformable, Translateable, Transformable, Mirrorable,
		PointRotateable, Dilateable {

	/** cache the part of XML that follows after expression label="stroke1" */
	private StringBuilder xmlPoints;

	/**
	 * @param cons
	 *            construction
	 */
	public GeoLocusStroke(Construction cons) {
		super(cons);
		setVisibleInView3D(false);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.PENSTROKE;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return label;
	}

	@Override
	public boolean hasLineOpacity() {
		return true;
	}

	@Override
	public boolean isPinnable() {
		return true;
	}

	@Override
	final public boolean isAlgebraViewEditable() {
		return false;
	}

	@Override
	public boolean isLabelVisible() {
		return false;
	}

	@Override
	public DescriptionMode needToShowBothRowsInAV() {
		return DescriptionMode.VALUE;
	}

	@Override
	public GeoElement copy() {
		GeoLocusStroke ret = new GeoLocusStroke(cons);
		ret.set(this);
		return ret;
	}

	/**
	 * Run a callback for points, skipping the control points.
	 * 
	 * @param handler
	 *            handler to be called for each point
	 */
	public void processPointsWithoutControl(
			AsyncOperation<MyPoint> handler) {
		MyPoint last = null;
		for (MyPoint pt : getPoints()) {
			if (pt.getSegmentType() != SegmentType.CONTROL) {
				// also ignore third point added to simple segment
				// to able to calc control points
				if (!(last != null
						&& last.getSegmentType() == pt.getSegmentType()
						&& last.isEqual(pt))) {
					handler.callback(pt);
					last = pt;
				}
			}
		}
	}

	@Override
	public boolean isMatrixTransformable() {
		return true;
	}

	@Override
	public void matrixTransform(double a00, double a01, double a10,
			double a11) {
		for (MyPoint pt : getPoints()) {
			double x = pt.x;
			double y = pt.y;
			pt.x = a00 * x + a01 * y;
			pt.y = a10 * x + a11 * y;
		}
	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		for (MyPoint pt : getPoints()) {
			double x = pt.x;
			double y = pt.y;
			double z = a20 * x + a21 * y + a22;
			pt.x = (a00 * x + a01 * y + a02) / z;
			pt.y = (a10 * x + a11 * y + a12) / z;
		}
	}

	@Override
	public void dilate(NumberValue r, Coords S) {
		double rval = r.getDouble();
		double crval = 1 - rval;

		for (MyPoint pt : getPoints()) {
			pt.x = rval * pt.x + crval * S.getX();
			pt.y = rval * pt.y + crval * S.getY();
		}
	}

	@Override
	public void mirror(Coords Q) {
		for (MyPoint pt : getPoints()) {
			pt.x = 2 * Q.getX() - pt.x;
			pt.y = 2 * Q.getY() - pt.y;
		}
	}

	@Override
	public void mirror(GeoLineND g1) {
		GeoLine g = (GeoLine) g1;

		// Y = S(phi).(X - Q) + Q
		// where Q is a point on g, S(phi) is the mirrorTransform(phi)
		// and phi/2 is the line's slope angle

		// get arbitrary point of line
		double qx, qy;
		if (Math.abs(g.getX()) > Math.abs(g.getY())) {
			qx = -g.getZ() / g.getX();
			qy = 0.0d;
		} else {
			qx = 0.0d;
			qy = -g.getZ() / g.getY();
		}

		// S(phi)
		double phi = 2.0 * Math.atan2(-g.getX(), g.getY());

		double cos = Math.cos(phi);
		double sin = Math.sin(phi);

		for (MyPoint pt : getPoints()) {
			// translate -Q
			pt.x -= qx;
			pt.y -= qy;

			double x0 = pt.x * cos + pt.y * sin;
			pt.y = pt.x * sin - pt.y * cos;
			pt.x = x0;

			// translate back +Q
			pt.x += qx;
			pt.y += qy;
		}
	}

	@Override
	public void rotate(NumberValue r, GeoPointND S) {
		Coords Q = S.getInhomCoords();

		double phi = r.getDouble();
		double cos = MyMath.cos(phi);
		double sin = Math.sin(phi);

		double qx = Q.getX();
		double qy = Q.getY();

		for (MyPoint pt : getPoints()) {
			double x = pt.x;
			double y = pt.y;

			pt.x = (x - qx) * cos + (qy - y) * sin + qx;
			pt.y = (x - qx) * sin + (y - qy) * cos + qy;
		}
	}

	@Override
	public void rotate(NumberValue r) {
		double phi = r.getDouble();
		double cos = MyMath.cos(phi);
		double sin = Math.sin(phi);

		for (MyPoint pt : getPoints()) {
			double x = pt.x;
			double y = pt.y;

			pt.x = x * cos - y * sin;
			pt.y = x * sin + y * cos;
		}
	}

	@Override
	public boolean isTranslateable() {
		return true;
	}

	@Override
	public void translate(Coords v) {
		for (MyPoint pt : getPoints()) {
			pt.x += v.getX();
			pt.y += v.getY();
		}

		resetXMLPointBuilder();
	}

	@Override
	public boolean isMoveable() {
		return true;
	}

	@Override
	public boolean isFillable() {
		return false;
	}

	@Override
	public boolean isAlgebraDuplicateable() {
		return false;
	}

	@Override
	public boolean isPenStroke() {
		return true;
	}

	/**
	 * Reset list of points for XML
	 */
	public void resetXMLPointBuilder() {
		xmlPoints = null;
	}

	/**
	 * @return builder fox XML representation of points
	 */
	public StringBuilder getXMLPointBuilder() {
		return xmlPoints;
	}

	/**
	 * @param xmlPointBuilder
	 *            builder fox XML representation of points
	 */
	public void setXMLPointBuilder(StringBuilder xmlPointBuilder) {
		this.xmlPoints = xmlPointBuilder;
	}

	@Override
	public String toOutputValueString(StringTemplate tpl) {
		return label;
	}

	/**
	 * @return list of points without the control points
	 */
	public ArrayList<MyPoint> getPointsWithoutControl() {
		final ArrayList<MyPoint> pointsNoControl = new ArrayList<>();
		processPointsWithoutControl(new AsyncOperation<MyPoint>() {

			@Override
			public void callback(MyPoint obj) {
				pointsNoControl.add(obj);
			}
		});
		return pointsNoControl;
	}
}
