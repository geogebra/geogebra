package org.geogebra.web.html5.euclidian;

import java.util.List;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.PenPreviewLine;
import org.geogebra.web.html5.awt.GGraphics2DW;

import com.himamis.retex.renderer.web.graphics.JLMContext2d;

/**
 * Pen preview drawing that uses the Canvas API directly, skipping conversion
 * from List to GeneralPath
 */
public class PenPreviewLineW extends PenPreviewLine {

	@Override
	protected void drawPolyline(List<GPoint> penPoints, GGraphics2D g2) {
		int minQuadDistance = 20;
		JLMContext2d g2w = ((GGraphics2DW) g2).getContext();
		g2w.beginPath();

		double prevx = penPoints.get(0).x;
		double prevy = penPoints.get(0).y;

		g2w.moveTo(prevx, prevy);

		if (penPoints.size() == 1) {
			g2w.lineTo(prevx, prevy);
		} else {
			for (int i = 1; i < penPoints.size() - 2; i++) {
				double c = (penPoints.get(i).x + penPoints.get(i + 1).x) / 2.0;
				double d = (penPoints.get(i).y + penPoints.get(i + 1).y) / 2.0;
				if (Math.abs(prevx - c) + Math.abs(prevy - d) > minQuadDistance) {
					g2w.quadraticCurveTo(penPoints.get(i).x, penPoints.get(i).y, c, d);
				} else {
					g2w.lineTo(c, d);
				}
				prevx = c;
				prevy = d;
			}

			// For the last 2 points
			g2w.quadraticCurveTo(
					penPoints.get(penPoints.size() - 2).x,
					penPoints.get(penPoints.size() - 2).y,
					penPoints.get(penPoints.size() - 1).x,
					penPoints.get(penPoints.size() - 1).y
			);
		}

		g2w.stroke();
	}
}
