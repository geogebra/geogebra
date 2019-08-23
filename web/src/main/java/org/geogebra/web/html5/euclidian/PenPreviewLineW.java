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
		JLMContext2d g2w = ((GGraphics2DW) g2).getContext();
		g2w.beginPath();
		g2w.moveTo(penPoints.get(0).x, penPoints.get(0).y);
		for (int i = 1; i < penPoints.size() - 1; i++) {
			g2w.lineTo(penPoints.get(i).x, penPoints.get(i).y);
		}
		g2w.stroke();
	}
}
