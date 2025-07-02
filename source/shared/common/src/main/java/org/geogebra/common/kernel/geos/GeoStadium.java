package org.geogebra.common.kernel.geos;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.GeoClass;

public final class GeoStadium extends GeoLocus implements MatrixTransformable, Translateable,
		Transformable {
	private GeoPoint p;
	private GeoPoint q;
	private GeoNumeric height;
	private List<GeoPoint> keyPoints;

	/**
	 * @param c {@link Construction}
	 */
	public GeoStadium(Construction c) {
		super(c);
		setLineThickness(EuclidianStyleConstants.DEFAULT_LINE_THICKNESS);
		setLabelVisible(false);
		setEuclidianVisible(true);
		setAlphaValue(0);
		setBackgroundColor(GColor.WHITE);
		setObjColor(GeoGebraColorConstants.GEOGEBRA_OBJECT_BLACK);
	}

	/**
	 * @param c {@link Construction}
	 * @param p centre of the first semicircle
	 * @param q centre of the second semicircle
	 * @param height of the stadium (2x radius of a semicircle)
	 */
	public GeoStadium(Construction c, GeoPoint p, GeoPoint q, GeoNumeric height) {
		this(c);
		this.p = p;
		this.q = q;
		this.height = height;
		keyPoints = List.of(p, q);
	}

	@Override
	public void getXMLtags(StringBuilder builder) {
		getStyleXML(builder);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.SHAPE_STADIUM;
	}

	/**
	 * @return the centre of the first semicircle.
	 */
	public GeoPoint getP() {
		return p;
	}

	/**
	 * @return the centre of the second semicircle.
	 */
	public GeoPoint getQ() {
		return q;
	}

	/**
	 * @return the height of the stadium (2x radius)
	 */
	public GeoNumeric getHeight() {
		return height;
	}

	/**
	 * Updates the parameters of the GeoStadium
	 * @param x1 x coordinate of first centre
	 * @param y1 y coordinate of first centre
	 * @param x2 x coordinate of second centre
	 * @param y2 y coordinate of second centre
	 * @param h the height of the stadium (2x radius)
	 */
	public void update(double x1, double y1, double x2, double y2, double h) {
		if (x1 < x2) {
			p.setCoords(x1, y1, 1);
			q.setCoords(x2, y2, 1);
		} else {
			p.setCoords(x2, y2, 1);
			q.setCoords(x1, y1, 1);
		}
		height.setValue(h);
	}

	@Override
	public void rotate(NumberValue r, GeoPointND S) {
		rotate(r, S.getInhomCoords());
	}

	private void rotate(NumberValue angle, Coords coords) {
		for (GeoPoint pt : keyPoints) {
			pt.rotate(angle, coords);
		}
		if (algoParent != null) {
			algoParent.compute();
		}
	}

	@Override
	public void rotate(NumberValue angle) {
		rotate(angle, new Coords(0, 0));
	}

	/**
	 * Sets the next label available for the stadium to the object.
	 */
	public void setDefaultLabel() {
		setLabel(getDefaultLabel());
	}

	@Override
	public void matrixTransform(double a00, double a01, double a10, double a11) {
		for (GeoPoint pt : keyPoints) {
			double x = pt.x;
			double y = pt.y;
			pt.setCoords(a00 * x + a01 * y, a10 * x + a11 * y, 1);
		}
	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10, double a11,
			double a12, double a20, double a21, double a22) {
		for (GeoPoint pt : keyPoints) {
			double x = pt.x;
			double y = pt.y;
			double z = a20 * x + a21 * y + a22;
			pt.setCoords((a00 * x + a01 * y + a02) / z,
					(a10 * x + a11 * y + a12) / z, 1);
		}
	}

	@Override
	public void translate(Coords v) {
		for (GeoPoint pt : keyPoints) {
			pt.setCoords(pt.x + v.getX(), pt.y + v.getY(), 1);
		}
	}
}
