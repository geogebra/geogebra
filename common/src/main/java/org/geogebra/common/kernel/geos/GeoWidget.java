package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Base class for locateable geos like button and video.
 *
 */
public abstract class GeoWidget extends GeoElement implements Translateable {

	/** Corners */
	protected final GeoPointND startPoint;

	private double width;
	private double height;

	private double xScale;
	private double yScale;

	/**
	 * @param c
	 *            the construction.
	 */
	public GeoWidget(Construction c) {
		super(c);
		startPoint = new GeoPoint(c);
	}

	public GeoPointND getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(GeoPointND startPoint) {
		this.startPoint.set(startPoint);
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * Zooming in x direction
	 *
	 * @param factor
	 *            zoom factor;
	 *
	 */
	private void zoomX(double factor) {
		setWidth(getWidth() * factor);
	}

	/**
	 * Zooming in y direction
	 *
	 * @param factor
	 *            zoom factor;
	 *
	 */
	private void zoomY(double factor) {
		setHeight(getHeight() * factor);
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
		startPoint.translate(v);
	}

	@Override
	public boolean isTranslateable() {
		return true;
	}

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		sb.append("\t<startPoint x=\"")
			.append(startPoint.getInhomX())
			.append("\" y=\"")
			.append(startPoint.getInhomY())
			.append("\">\n");
	}
}
