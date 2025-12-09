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

package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.HashMap;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.RectangleTransformable;

/**
 * State holder for bounding box resize (multi selection)
 * 
 * @author Hunor Karaman
 *
 */
public class BoundingBoxResizeState {

	private double widthThreshold = Double.NEGATIVE_INFINITY;
	private double heightThreshold = Double.NEGATIVE_INFINITY;

	private double widthHeightRatio = 1;

	private GRectangle2D rect;
	private final EuclidianView view;
	private final ArrayList<GeoElement> geos;

	private HashMap<GeoElement, ArrayList<GPoint2D>> ratios;

	/**
	 * @param rect
	 *            bounding box rectangle
	 * @param geos
	 *            list of selected GeoElements
	 * @param view
	 *            current view
	 */
	public BoundingBoxResizeState(GRectangle2D rect, ArrayList<GeoElement> geos,
			EuclidianView view) {
		ratios = new HashMap<>();
		this.rect = rect;
		this.geos = geos;
		this.view = view;

		if (this.rect != null) {
			widthHeightRatio = rect.getWidth() / rect.getHeight();
			for (GeoElement geo : geos) {
				Drawable dr = (Drawable) view.getDrawableFor(geo);
				// check and update thresholds

				if (dr == null) {
					continue;
				}

				if (geo instanceof RectangleTransformable) {
					updateThresholdsFor((RectangleTransformable) geo);
				}
				// calculate the min/max coordinates

				ArrayList<GPoint2D> forGeo = new ArrayList<>(2);
				GRectangle2D rectangle = view.getBoundingBox().getRectangle();
				for (GPoint2D pt : dr.toPoints()) {
					double relativeX = relativeDist(pt.getX(), rectangle.getMinX(),
							rectangle.getWidth());
					double relativeY = relativeDist(pt.getY(), rectangle.getMinY(),
							rectangle.getHeight());
					forGeo.add(new MyPoint(relativeX, relativeY));
				}

				ratios.put(geo, forGeo);
			}
		}
	}

	private double relativeDist(double val, double min, double range) {
		return range == 0.0 ? 0.5 : (val - min) / range;
	}

	private void updateThresholdsFor(RectangleTransformable geo) {
		if (geo.getMinWidth() > widthThreshold) {
			widthThreshold = geo.getMinWidth();
		}
		if (geo.getMinHeight() > heightThreshold) {
			heightThreshold = geo.getMinHeight();
		}
	}

	/**
	 * @param geo construction element
	 * @return positions of the corners of the geo from the side of bounding box
	 *         in ratio [minX, maxX, minY, maxY]
	 */
	public ArrayList<GPoint2D> getRatios(GeoElement geo) {
		return ratios.get(geo);
	}

	/**
	 * Update the bounding box resize state. Some of the width or height thresholds
	 * might have changed in the meantime (e.g. GeoInlineText)
	 */
	public void updateThresholds() {
		if (this.rect != null) {
			widthHeightRatio = rect.getWidth() / rect.getHeight();
			for (GeoElement geo : geos) {
				DrawableND dr = view.getDrawableFor(geo);
				// check and update thresholds
				if (dr != null && geo instanceof  RectangleTransformable) {
					updateThresholdsFor((RectangleTransformable) geo);
				}
			}
		}
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
}
