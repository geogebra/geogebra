package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * State holder for bounding box resize (multi selection)
 * 
 * @author Hunor Karaman
 *
 */
public class BoundingBoxResizeState {

	private GRectangle2D rect;
	private double[][] ratios;
	private double widthHeightRatio = 1;

	/**
	 * @param rect
	 *            bounding box rectangle
	 * @param geos
	 *            list of selected GeoElements
	 * @param view
	 *            current view
	 */
	public BoundingBoxResizeState(GRectangle2D rect,
			ArrayList<GeoElement> geos, EuclidianView view) {
		this.rect = rect;

		ratios = new double[geos.size()][4];

		if (this.rect != null) {
			widthHeightRatio = rect.getWidth() / rect.getHeight();

			for (int i = 0; i < geos.size(); i++) {
				Drawable dr = (Drawable) view.getDrawableFor(geos.get(i));

				ratios[i][0] = (dr.getBounds().getMinX()
						- view.getBoundingBox().getRectangle().getMinX())
						/ view.getBoundingBox().getRectangle().getWidth();
				ratios[i][1] = (dr.getBounds().getMaxX()
						- view.getBoundingBox().getRectangle().getMinX())
						/ view.getBoundingBox().getRectangle().getWidth();
				ratios[i][2] = (dr.getBounds().getMinY()
						- view.getBoundingBox().getRectangle().getMinY())
						/ view.getBoundingBox().getRectangle().getHeight();
				ratios[i][3] = (dr.getBounds().getMaxY()
						- view.getBoundingBox().getRectangle().getMinY())
						/ view.getBoundingBox().getRectangle().getHeight();
			}
		}
	}

	/**
	 * @return positions of the geos from the side of bounding box in ratio
	 */
	public double[][] getRatios() {
		return this.ratios;
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
}
