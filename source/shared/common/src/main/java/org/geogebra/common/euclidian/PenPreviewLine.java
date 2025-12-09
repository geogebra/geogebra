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

import java.util.List;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;

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
