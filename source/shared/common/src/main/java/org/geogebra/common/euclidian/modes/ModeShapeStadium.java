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

package org.geogebra.common.euclidian.modes;

import java.util.ArrayList;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.algos.AlgoStadium;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoStadium;

public class ModeShapeStadium {
	private final GGeneralPath gpPreview = AwtFactory.getPrototype()
			.newGeneralPath();
	private final EuclidianViewBounds bounds;
	private GPoint2D rwStartPoint = new GPoint2D();
	private AlgoStadium previewAlgo;
	private GeoPoint p;
	private GeoPoint q;
	private GeoNumeric heightNum;

	/**
	 *
	 * @param cons {@link Construction}
	 * @param bounds {@link EuclidianViewBounds}
	 */
	public ModeShapeStadium(Construction cons, EuclidianViewBounds bounds) {
		this.bounds = bounds;
		p = new GeoPoint(cons);
		q = new GeoPoint(cons);
		heightNum = new GeoNumeric(cons);
		previewAlgo = new AlgoStadium(cons, p, q, heightNum);
		previewAlgo.remove();
	}

	/**
	 * Updates the preview of the stadium as creating by drag.
	 *
	 * @param sx x in screen coordinates
	 * @param sy y in screen coordinates
	 */
	public void updatePreview(int sx, int sy) {
		updatePoints(bounds.toRealWorldCoordX(sx), bounds.toRealWorldCoordY(sy));
		previewAlgo.update();
		ArrayList<MyPoint> points = previewAlgo.getPoints();
		gpPreview.reset();
		MyPoint firstPoint = points.get(0);
		gpPreview.moveTo(bounds.toScreenCoordXd(firstPoint.x),
				bounds.toScreenCoordYd(firstPoint.y));
		for (MyPoint p : points) {
			gpPreview.lineTo(bounds.toScreenCoordXd(p.x), bounds.toScreenCoordYd(p.y));
		}
		gpPreview.closePath();
	}

	/**
	 * Creates the stadium within a rectangle specified by the start point as top left and (sx, sy)
	 * as bottom right corner.
	 *
	 * @param cons {@link Construction}
	 * @param sx x in screen coordinates
	 * @param sy y in screen coordinates
	 * @return the stadium geo.
	 */
	public GeoElement create(Construction cons, int sx, int sy) {
		updatePoints(bounds.toRealWorldCoordX(sx), bounds.toRealWorldCoordY(sy));
		AlgoStadium algoCreate = new AlgoStadium(cons, p.copy(), q.copy(), heightNum.copy());
		GeoStadium stadium = (GeoStadium) algoCreate.getOutput(0);
		stadium.setDefined(true);
		stadium.setDefaultLabel();

		gpPreview.reset();

		return stadium;
	}

	private void updatePoints(double endX, double endY) {
		double dx = endX - rwStartPoint.x;
		double dy = endY - rwStartPoint.y;
		double width = Math.abs(dx);
		double height = Math.abs(dy);
		double left = Math.min(rwStartPoint.x, rwStartPoint.x + dx);
		double top = Math.min(rwStartPoint.y, rwStartPoint.y + dy);
		double right = left + width;
		double bottom = top + height;
		double radius = height / 2;
		double middle = top + radius;
		double h = bottom - top;
		p.setCoords(left + radius, middle, 1);
		q.setCoords(right - radius, middle, 1);
		heightNum.setValue(h);
	}

	/**
	 *
	 * @return the preview of the stadium as path.
	 */
	public GGeneralPath getGpPreview() {
		return gpPreview;
	}

	/**
	 * Sets the start (drag) point
	 * @param sx x in screen coordinates
	 * @param sy y in screen coordinates
	 */
	public void setStartPoint(int sx, int sy) {
		rwStartPoint.setLocation(bounds.toRealWorldCoordX(sx),
				bounds.toRealWorldCoordY(sy));
	}
}
