package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoWidget;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public abstract class DrawWidget extends Drawable {

	private GRectangle bounds;
	private double originalRatio = Double.NaN;

	public void updateBounds() {
		GeoPointND startPoint = getGeoElement().getStartPoint();

		int left = view.toScreenCoordX(startPoint.getInhomX());
		int top = view.toScreenCoordY(startPoint.getInhomY());
		int width = (int) getGeoElement().getWidth();
		int height = (int) getGeoElement().getHeight();

		bounds = AwtFactory.getPrototype().newRectangle(left, top, width, height);
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return bounds != null && bounds.contains(x, y);
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return rect.contains(bounds);
	}

	@Override
	public GRectangle getBounds() {
		return bounds;
	}

	/**
	 * @param newWidth
	 *            pixel width at current zoom
	 */
	public final void setWidth(int newWidth) {
		getGeoElement().setWidth(newWidth);
	}

	/**
	 * @param newHeight
	 *            pixel height at current zoom
	 */
	public final void setHeight(int newHeight) {
		getGeoElement().setHeight(newHeight);
	}

	/**
	 * @return width on screen at current zoom
	 */
	public final int getWidth() {
		return (int) getGeoElement().getWidth();
	}

	/**
	 * @return height on screen at current zoom
	 */
	public final int getHeight() {
		return (int) getGeoElement().getHeight();
	}

	/**
	 * @return left corner x-coord in EV
	 */
	public final int getLeft() {
		if (bounds == null) {
			updateBounds();
		}

		return (int) bounds.getX();
	}

	/**
	 * @return top corner y-coord in EV
	 */
	public final int getTop() {
		if (bounds == null) {
			updateBounds();
		}

		return (int) bounds.getY();
	}

	/**
	 * @param x
	 *            left corner x-coord in EV
	 * @param y
	 *            top corner y-coord in EV
	 */
	public final void setScreenLocation(int x, int y) {
		getGeoElement().getStartPoint()
				.setCoords(view.toRealWorldCoordX(x), view.toRealWorldCoordY(y), 1);
	}

	/**
	 * @return aspect ratio at start of resize (NaN if last drag changed it)
	 */
	public final double getOriginalRatio() {
		return originalRatio;
	}

	/**
	 * Reset aspect ratio.
	 */
	public final void resetRatio() {
		originalRatio = Double.NaN;
	}

	private void updateOriginalRatio() {
		double width = getWidth();
		double height = getHeight();
		originalRatio = height / width;
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> pts) {
		if (Double.isNaN(originalRatio)) {
			updateOriginalRatio();
		}
		BoundingBox.resize(this, pts.get(0), pts.get(1));
	}

	/**
	 * @return the geo linked to this
	 */
	public abstract GeoWidget getGeoElement();

	/**
	 * @return whether aspect ratio is fixed for this widget
	 */
	public abstract boolean isFixedRatio();

	/**
	 * @return embed ID
	 */
	public abstract int getEmbedID();

	public abstract boolean isBackground();

	public abstract void setBackground(boolean b);
}
