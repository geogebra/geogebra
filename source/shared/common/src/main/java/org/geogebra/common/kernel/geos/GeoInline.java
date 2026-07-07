/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawInline;
import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.MyMath;

public abstract class GeoInline extends GeoElement implements Translateable, Rotatable,
		RectangleTransformable {

	private GPoint2D location;

	private double width;
	private double height;

	private double angle;
	/** cannot be moved by other users */
	private boolean isLockedForMultiuser = false;

	private GColor borderColor = GColor.BLACK;
	private double contentWidth;
	private double contentHeight;

	// only used for loading files that were created before zoom was enabled for text elements
	private boolean zoomingEnabled = true;
	private double scale = 1;

	/**
	 * @param cons construction
	 */
	public GeoInline(Construction cons) {
		super(cons);
		zoomIfNeeded();
	}

	@Override
	public ValueType getValueType() {
		return ValueType.TEXT;
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
	}

	@Override
	public boolean isAlgebraViewEditable() {
		return false;
	}

	@Override
	public boolean isLabelShowable() {
		return false;
	}

	@Override
	public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getAngle() {
		return angle;
	}

	@Override
	public GPoint2D getLocation() {
		return location;
	}

	@Override
	public void setSize(double width, double height) {
		if (getWidth() != 0) {
			contentWidth = contentWidth * width / getWidth();
		}
		if (getHeight() != 0) {
			contentHeight = contentHeight * height / getHeight();
		}
		this.width = width;
		this.height = height;
	}

	/**
	 * Set minimal height.
	 * @param minHeight minimal height in pixels.
	 */
	public abstract void setMinHeight(double minHeight);

	/**
	 * @param angle rotation angle in radians
	 */
	@Override
	public void setAngle(double angle) {
		this.angle = angle;
	}

	/**
	 * @param location
	 *            on-screen location
	 */
	@Override
	public void setLocation(GPoint2D location) {
		this.location = location;
	}

	/**
	 * @param content editor content; encoding depends on editor type
	 */
	public abstract void setContent(String content);

	/**
	 * @return editor content; encoding depends on editor type
	 */
	public abstract String getContent();

	public void setBorderColor(GColor borderColor) {
		this.borderColor = borderColor;
	}

	public GColor getBorderColor() {
		return this.borderColor;
	}

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

	/**
	 * returns all class-specific xml tags for getXML
	 */
	@Override
	protected void getStyleXML(XMLStringBuilder sb) {
		getXMLFixedTag(sb);
		getXMVisualTags(sb);
		sb.startTag("contentSize")
				.attr("width", contentWidth)
				.attr("height", contentHeight)
				.endTag();

		XMLBuilder.appendPosition(sb, this);
	}

	@Override
	public void getXMLTags(XMLStringBuilder sb) {
		super.getXMLTags(sb);
		sb.startTag("content").attr("val", getContent()).endTag();
	}

	/**
	 * @return text formatter
	 */
	public HasTextFormat getFormatter() {
		DrawInline drawable = (DrawInline) kernel.getApplication()
				.getActiveEuclidianView().getDrawableFor(this);
		return drawable == null ? null : drawable.getController();
	}

	/**
	 * Zooms the text element.
	 */
	public void zoomIfNeeded() {
		EuclidianView view = app.getActiveEuclidianView();
		double xScale = view != null ? view.getXscale() : EuclidianView.SCALE_STANDARD;
		double yScale = view != null ? view.getYscale() : EuclidianView.SCALE_STANDARD;
		width = contentWidth * xScale / EuclidianView.SCALE_STANDARD * scale;
		height = contentHeight * yScale / EuclidianView.SCALE_STANDARD * scale;
	}

	public double getContentWidth() {
		return contentWidth;
	}

	public void setContentWidth(double contentWidth) {
		this.contentWidth = contentWidth;
	}

	public double getContentHeight() {
		return contentHeight;
	}

	public void setContentHeight(double contentHeight) {
		this.contentHeight = contentHeight;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public boolean isZoomingEnabled() {
		return zoomingEnabled;
	}

	public void setZoomingEnabled(boolean zoomingEnabled) {
		this.zoomingEnabled = zoomingEnabled;
	}

	public boolean isLockedForMultiuser() {
		return isLockedForMultiuser;
	}

	public void setLockedForMultiuser(boolean locked) {
		isLockedForMultiuser = locked;
	}

	/**
	 * dispatches unlock event for multiuser
	 */
	public void unlockForMultiuser() {
		getApp().getEventDispatcher().unlockTextElement(this);
	}

	/**
	 * sets width and height of element without changing contentSize
	 * @param width width
	 * @param height height
	 */
	public void setSizeOnly(double width, double height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean hasBackgroundColor() {
		return true;
	}

	/**
	 * Set a scale factor for legacy text elements for backward compatible rendering.
	 * @param scale scale factor
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}
}
