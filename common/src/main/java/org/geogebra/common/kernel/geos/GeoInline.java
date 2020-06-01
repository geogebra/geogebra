package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.MyMath;

public abstract class GeoInline extends GeoElement implements Translateable, PointRotateable,
		RectangleTransformable {

	private GPoint2D location;

	private double width;
	private double height;

	private double angle;

	public GeoInline(Construction cons) {
		super(cons);
	}

	/**
	 * Get the height of the element.
	 *
	 * @return height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Get the width of the element.
	 *
	 * @return width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @return rotation angle in radians
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * Get the location of the text.
	 *
	 * @return location
	 */
	public GPoint2D getLocation() {
		return location;
	}

	/**
	 * Set the width of the element.
	 *
	 * @param width element width in pixels
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * Set the height of the element.
	 *
	 * @param height height in pixels
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * @param angle rotation angle in radians
	 */
	public void setAngle(double angle) {
		this.angle = angle;
	}

	/**
	 * @param location
	 *            on-screen location
	 */
	public void setLocation(GPoint2D location) {
		this.location = location;
	}

	/**
	 * @param content editor content; encoding depends on editor type
	 */
	public abstract void setContent(String content);

	/**
	 * @return min width in pixels, depends on content
	 */
	public abstract double getMinWidth();

	/**
	 * @return min height in pixels, depends on content
	 */
	public abstract double getMinHeight();

	@Override
	public void translate(Coords v) {
		location.setLocation(location.getX() + v.getX(), location.getY() + v.getY());
	}

	@Override
	public boolean isTranslateable() {
		return true;
	}

	@Override
	public void rotate(NumberValue r) {
		angle -= r.getDouble();
	}

	@Override
	public void rotate(NumberValue r, GeoPointND S) {
		angle -= r.getDouble();
		rotate(location, r, S);
	}

	protected static void rotate(GPoint2D location, NumberValue r, GeoPointND S) {
		double phi = r.getDouble();
		double cos = MyMath.cos(phi);
		double sin = Math.sin(phi);
		double qx = S.getInhomCoords().getX();
		double qy = S.getInhomCoords().getY();

		double x = location.getX();
		double y = location.getY();

		location.setLocation((x - qx) * cos + (qy - y) * sin + qx,
				(x - qx) * sin + (y - qy) * cos + qy);
	}
}
