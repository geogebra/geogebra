package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoWidget;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public abstract class DrawWidget extends Drawable {

	private final GRectangle2D bounds = AwtFactory.getPrototype().newRectangle2D();
	private double originalRatio = Double.NaN;

	public void updateBounds() {
		getGeoElement().zoomIfNeeded();

		GeoPointND startPoint = getGeoElement().getStartPoint();

		double left = view.toScreenCoordXd(startPoint.getInhomX());
		double top = view.toScreenCoordYd(startPoint.getInhomY());
		double width = getGeoElement().getWidth();
		double height = getGeoElement().getHeight();

		bounds.setFrame(left, top, width, height);
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
		return bounds.getBounds();
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
		return bounds.getX();
	}

	/**
	 * @return top corner y-coord in EV
	 */
	public final double getTop() {
		return bounds.getY();
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
