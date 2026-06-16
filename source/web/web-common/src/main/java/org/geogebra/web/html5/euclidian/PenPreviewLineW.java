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

package org.geogebra.web.html5.euclidian;

import java.util.List;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.PenPreviewLine;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.util.Smoothing;
import org.geogebra.web.awt.GGraphics2DW;
import org.geogebra.web.awt.JLMContext2D;

/**
 * Pen preview drawing that uses the Canvas API directly, skipping conversion
 * from List to GeneralPath
 */
public class PenPreviewLineW extends PenPreviewLine {

	@Override
	protected void drawPolyline(List<GPoint2D> rawPoints, GGraphics2D g2) {
		JLMContext2D g2w = ((GGraphics2DW) g2).getContext();
		g2w.beginPath();
		List<? extends GPoint2D> penPoints = Smoothing.transform(rawPoints);
		if (penPoints.isEmpty()) {
			if (!rawPoints.isEmpty()) {
				GPoint2D start = rawPoints.get(0);
				g2w.moveTo(start.x, start.y);
				GPoint2D end = rawPoints.get(rawPoints.size() - 1);
				g2w.lineTo(end.x, end.y);
				g2w.stroke();
			}
			return;
		}
		double prevx = penPoints.get(0).x;
		double prevy = penPoints.get(0).y;

		g2w.moveTo(prevx, prevy);

		if (penPoints.size() == 1) {
			g2w.lineTo(prevx, prevy);
		} else {
			MyPoint[] controls = new MyPoint[2];
			GeoLocusStroke.processContinuous(penPoints, 1, pt -> {
				switch (pt.getSegmentType()) {
				case MOVE_TO -> g2w.moveTo(pt.x, pt.y);
				case LINE_TO -> g2w.lineTo(pt.x, pt.y);
				case CONTROL -> {
					if (controls[0] == null) {
						controls[0] = pt;
					} else if (controls[1] == null) {
						controls[1] = pt;
					}
				}
				case CURVE_TO -> {
						g2w.bezierCurveTo(controls[0].x, controls[0].y,
								controls[0].x, controls[0].y, pt.x, pt.y);
						controls[0] = controls[1] = null;
				}
				case ARC_TO, AUXILIARY -> throw new IllegalStateException();
				}
			});
		}

		g2w.stroke();
	}
}
