package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * State holder for bounding box resize (multi selection)
 * 
 * @author Hunor Karaman
 *
 */
public class BoundingBoxResizeState {
	private GRectangle2D rect;
	private ArrayList<ArrayList<GPoint2D>> ratios;
	private double widthHeightRatio = 1;
	private double widthThreshold = Double.NEGATIVE_INFINITY;
	private double heightThreshold = Double.NEGATIVE_INFINITY;

	/**
	 * @param rect
	 *            bounding box rectangle
	 * @param geos
	 *            list of selected GeoElements
	 * @param view
	 *            current view
	 */
	public BoundingBoxResizeState(GRectangle2D rect, ArrayList<GeoElement> geos,
			EuclidianView view, boolean diagonal) {
		this.rect = rect;
		ratios = new ArrayList<>();

		if (this.rect != null) {
			widthHeightRatio = rect.getWidth() / rect.getHeight();
			for (int i = 0; i < geos.size(); i++) {
				GeoElement geo = geos.get(i);
				Drawable dr = (Drawable) view.getDrawableFor(geo);
				// check and update thresholds
				if (dr.getWidthThreshold() > widthThreshold) {
					widthThreshold = diagonal ? dr.getDiagonalWidthThreshold()
							: dr.getWidthThreshold();
				}
				if (dr.getHeightThreshold() > heightThreshold) {
					heightThreshold = dr.getHeightThreshold();
				}
				// calculate the min/max coordinates

				ArrayList<GPoint2D> forGeo = new ArrayList<>(2);
				GRectangle2D rectangle = view.getBoundingBox().getRectangle();
				for (GPoint2D pt : dr.toPoints()) {
					forGeo.add(
							new MyPoint((pt.getX() - rectangle.getMinX()) / rectangle.getWidth(),
									(pt.getY() - rectangle.getMinY()) / rectangle.getHeight()));
				}

				ratios.add(forGeo);
			}
		}
	}

	/**
	 * @param i
	 *            index of the geo
	 * @return positions of the corners of the geo from the side of bounding box
	 *         in ratio [minX, maxX, minY, maxY]
	 */
	public ArrayList<GPoint2D> getRatios(int i) {
		return ratios.get(i);
	}

	/**
	 * @return bounding box bounds
	 */
	public GRectangle2D getRectangle() {
		return this.rect;
	}

	/**
	 * @return bounding box bounds
	 */
	public double getWidthHeightRatio() {
		return this.widthHeightRatio;
	}

	/**
	 * @return minimum width of the bounding box based on the selected elements
	 */
	public double getWidthThreshold() {
		return widthThreshold;
	}

	/**
	 * @return minimum height of the bounding box based on the selected elements
	 */
	public double getHeightThreshold() {
		return heightThreshold;
	}

	/**
	 * @param i
	 *            index of the element
	 * @return starting width of the element
	 */
	public double getWidth(int i) {
		return rect.getWidth() * (ratios.get(i).get(1).getX() - ratios.get(i).get(0).getX());
	}

	/**
	 * @param i
	 *            index of the element
	 * @return starting height of the element
	 */
	public double getHeight(int i) {
		return rect.getHeight() * (ratios.get(i).get(1).getY() - ratios.get(i).get(0).getY());
	}
}
