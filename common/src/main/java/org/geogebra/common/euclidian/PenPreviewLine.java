package org.geogebra.common.euclidian;

import java.util.List;

import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.factories.AwtFactory;

/**
 * Utility for drawing pen preview as polyline in graphics.
 * 
 * @author Zbynek
 */
public class PenPreviewLine {

	/**
	 * Draw a polyline connecting the points to a canvas.
	 * 
	 * @param penPoints
	 *            pen points
	 * @param graphics
	 *            graphics
	 */
	protected void drawPolyline(List<GPoint> penPoints, GGraphics2D graphics) {
		GGeneralPath gp = AwtFactory.getPrototype().newGeneralPath();
		gp.moveTo(penPoints.get(0).x, penPoints.get(0).y);
		for (int i = 1; i < penPoints.size() - 1; i++) {
			gp.lineTo(penPoints.get(i).x, penPoints.get(i).y);

		}
		graphics.draw(gp);
	}
}
