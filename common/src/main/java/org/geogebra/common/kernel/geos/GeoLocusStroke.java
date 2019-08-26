package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.AsyncOperation;

/**
 * Class for polylines created using pen
 * 
 * @author Zbynek
 */
public class GeoLocusStroke extends GeoLocus
		implements MatrixTransformable, Translateable {

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

	/**
	 * @return the definitng points
	 */
	public MyPoint[] getPointsND() {
		// if (getParentAlgorithm() instanceof AlgoLocusStroke) {
		// return ((AlgoLocusStroke) getParentAlgorithm()).getPointsND();
		// }
		MyPoint[] pts = new MyPoint[getPoints().size()];
		int i = 0;
		for (MyPoint pt : getPoints()) {
			pts[i++] = pt;
		}
		return pts;
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

		resetPointsWithoutControl();
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
	public void resetPointsWithoutControl() {
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
}
