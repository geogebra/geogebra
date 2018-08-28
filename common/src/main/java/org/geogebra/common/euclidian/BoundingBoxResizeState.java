package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * state holder for bounding box resize (multi selection)
 * 
 * @author Hunor Karaman
 *
 */
public class BoundingBoxResizeState {
	private GRectangle2D boundingBoxRect;
	private GPoint[] geoPositions;
	private final EuclidianView view;

	/**
	 * @param view
	 *            current EuclidianView
	 */
	public BoundingBoxResizeState(EuclidianView view) {
		this.view = view;

		setBoundingBoxRect(view.getBoundingBox().getRectangle());
	}

	/**
	 * @param view
	 *            current EuclidianView
	 * @param rect
	 *            default bounding box rectangle
	 */
	public BoundingBoxResizeState(EuclidianView view, GRectangle2D rect) {
		this.view = view;

		setBoundingBoxRect(rect);
	}

	/**
	 * @return {@link GRectangle2D} of BoundingBox
	 */
	public GRectangle2D getBoundingBoxRect() {
		return boundingBoxRect;
	}

	/**
	 * @param boundingBoxRect
	 *            the rectangle to set to
	 */
	public void setBoundingBoxRect(GRectangle2D boundingBoxRect) {
		this.boundingBoxRect = boundingBoxRect;
	}

	/**
	 * @return the positions of the elements
	 */
	public GPoint[] getGeoPositions() {
		return geoPositions;
	}

	/**
	 * calculates the positions of geos relative to the bounding box
	 * 
	 * @param geos
	 *            GeoElements in the selection
	 */
	public void calculatePositions(ArrayList<GeoElement> geos) {
		if (boundingBoxRect == null) {
			return;
		}

		geoPositions = new GPoint[geos.size()];

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			Drawable dr = (Drawable) view.getDrawableFor(geo);
			if (dr != null) {
				GRectangle bounds = dr.getBounds();
				double dx = 0, dy = 0;

				switch (view.getHitHandler()) {
				case TOP:
					dy = boundingBoxRect.getMaxY() - bounds.getMaxY();
					break;
				case RIGHT:
					dx = boundingBoxRect.getMaxX() - bounds.getMaxX();
					break;
				case BOTTOM:
					dy = boundingBoxRect.getMinY() - bounds.getMinY();
					break;
				case LEFT:
					dx = boundingBoxRect.getMinX() - bounds.getMinX();
					break;
				}

				geoPositions[i] = new GPoint((int) dx, (int) dy);
			}
		}
	}
}
