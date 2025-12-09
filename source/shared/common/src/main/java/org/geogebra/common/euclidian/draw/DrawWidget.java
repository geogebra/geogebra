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

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MediaBoundingBox;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoWidget;
import org.geogebra.common.kernel.geos.RectangleTransformable;

public abstract class DrawWidget extends Drawable implements HasTransformation {

	private final TransformableRectangle rectangle;

	/**
	 * @param view view
	 * @param geo construction element
	 */
	public DrawWidget(EuclidianView view, GeoElement geo, boolean fixedRatio) {
		super(view, geo);
		this.rectangle = new TransformableRectangle(view, (RectangleTransformable) geo,
				fixedRatio);
	}

	protected void updateBounds() {
		rectangle.updateSelfAndBoundingBox();
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return rectangle.hit(x, y);
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return rect.contains(getBounds());
	}

	@Override
	public GRectangle getBounds() {
		return rectangle.getBounds();
	}

	@Override
	public MediaBoundingBox getBoundingBox() {
		return rectangle.getBoundingBox();
	}

	@Override
	public BoundingBox<? extends GShape> getSelectionBoundingBox() {
		return getBoundingBox();
	}

	/**
	 * @return width on screen at current zoom
	 */
	public final double getWidth() {
		return getGeoElement().getWidth();
	}

	/**
	 * @return height on screen at current zoom
	 */
	public final double getHeight() {
		return getGeoElement().getHeight();
	}

	/**
	 * @return left corner x-coord in EV
	 */
	public final double getLeft() {
		return view.toScreenCoordX(getGeoElement().getLocation().getX());
	}

	/**
	 * @return top corner y-coord in EV
	 */
	public final double getTop() {
		return view.toScreenCoordY(getGeoElement().getLocation().getY());
	}

	@Override
	public List<GPoint2D> toPoints() {
		return rectangle.toPoints();
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> pts) {
		rectangle.fromPoints(pts);
	}

	@Override
	public void updateByBoundingBoxResize(GPoint2D point,
			EuclidianBoundingBoxHandler handler) {
		rectangle.updateByBoundingBoxResize(point, handler);
	}

	@Override
	public abstract GeoWidget getGeoElement();

	/**
	 * @return embed ID
	 */
	public abstract int getEmbedID();

	/**
	 * @return whether the widget is in the background (hidden and not editable)
	 */
	public abstract boolean isBackground();

	/**
	 * Switch between foreground (in front of graphics, editable) and background (hidden) mode.
	 * @param background whether it should be in the background.
	 */
	public abstract void setBackground(boolean background);

	@Override
	public GAffineTransform getTransform() {
		return rectangle.getDirectTransform();
	}

	/**
	 * Get point in widget coordinates from EV coordinates.
	 * @param x x-coordinate in EV
	 * @param y y-coordinate in EV
	 * @return transformed point
	 */
	public GPoint2D getInversePoint(int x, int y) {
		return rectangle.getInversePoint(x, y);
	}
}
