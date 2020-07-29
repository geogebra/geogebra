package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Base class for locateable geos like button and video.
 *
 */
public abstract class GeoWidget extends GeoElement
		implements Translateable, PointRotateable, RectangleTransformable {

	/** Corners */
	protected GPoint2D startPoint;

	private double width;
	private double height;

	private double angle;

	private double xScale;
	private double yScale;

	/**
	 * @param c
	 *            the construction.
	 */
	public GeoWidget(Construction c) {
		super(c);
		startPoint = new GPoint2D();
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public double getAngle() {
		return angle;
	}

	@Override
	public void setAngle(double angle) {
		this.angle = angle;
	}

	@Override
	public void setSize(double w, double h) {
		width = w;
		height = h;
	}

	@Override
	public GPoint2D getLocation() {
		return startPoint;
	}

	@Override
	public void setLocation(GPoint2D location) {
		this.startPoint = location;
	}

	@Override
	public abstract double getMinWidth();

	@Override
	public abstract double getMinHeight();

	/**
	 * Zooming in x direction
	 *
	 * @param factor
	 *            zoom factor;
	 *
	 */
	private void zoomX(double factor) {
		width *= factor;
	}

	/**
	 * Zooming in y direction
	 *
	 * @param factor
	 *            zoom factor;
	 *
	 */
	private void zoomY(double factor) {
		height *= factor;
	}

	/**
	 * Zoom the video if the video is not pinned, and the scales of the view
	 * changed.
	 */
	public void zoomIfNeeded() {
		if (xScale == 0) {
			xScale = app.getActiveEuclidianView().getXscale();
			yScale = app.getActiveEuclidianView().getYscale();
			return;
		}

		if (xScale != app.getActiveEuclidianView().getXscale()) {
			zoomX(app.getActiveEuclidianView().getXscale() / xScale);
			xScale = app.getActiveEuclidianView().getXscale();
		}
		if (yScale != app.getActiveEuclidianView().getYscale()) {
			zoomY(app.getActiveEuclidianView().getYscale() / yScale);
			yScale = app.getActiveEuclidianView().getYscale();
		}
	}

	@Override
	public ValueType getValueType() {
		return ValueType.VOID;
	}

	@Override
	public void setUndefined() {
		// do nothing
	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public boolean showInEuclidianView() {
		return true;
	}

	@Override
	final public boolean isAlgebraViewEditable() {
		return !isIndependent();
	}

	@Override
	public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return "";
	}

	@Override
	public void translate(Coords v) {
		startPoint.setLocation(startPoint.getX() + v.getX(), startPoint.getY() + v.getY());
	}

	@Override
	public boolean isTranslateable() {
		return true;
	}

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		XMLBuilder.appendPosition(sb, this);
	}

	@Override
	public void rotate(NumberValue r) {
		angle -= r.getDouble();
	}

	@Override
	public void rotate(NumberValue r, GeoPointND S) {
		angle -= r.getDouble();
		GeoInline.rotate(startPoint, r, S);
	}
}
