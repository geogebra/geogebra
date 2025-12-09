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

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.clipping.ClipLine;

/**
 * Draws the vector with the default head
 */
public class DefaultVectorShape implements VectorShape {

	private final DrawVectorModel model;
	private final GLine2D line;

	private final GPoint2D arrowBase;
	private final GPoint2D[] tmpClipPoints = {new GPoint2D(), new GPoint2D()};

	/**
	 *
	 * @param model {@link DrawVectorModel}
	 */
	public DefaultVectorShape(DrawVectorModel model) {
		this.model = model;
		line = AwtFactory.getPrototype().newLine2D();
		arrowBase = new GPoint2D();
	}

	@Override
	public DrawVectorModel model() {
		return model;
	}

	@Override
	public GLine2D body() {
		calculateArrowBase();
		line.setLine(model.getStartX(), model.getStartY(),
				arrowBase.getX(), arrowBase.getY());
		return line;
	}

	@Override
	public GShape head() {
		GGeneralPath arrow = AwtFactory.getPrototype().newGeneralPath();
		double vX = model.getPositionVectorX() / 4.0;
		double vY = model.getPositionVectorY() / 4.0;

		calculateArrowBase();
		arrow.reset();
		arrow.moveTo(model.getEndX(), model.getEndY());
		arrow.lineTo(arrowBase.x - vY, arrowBase.y + vX);
		arrow.lineTo(arrowBase.x + vY, arrowBase.y - vX);
		arrow.closePath();
		return arrow;
	}

	private void calculateArrowBase() {
		arrowBase.setLocation(model.getEndX() - model.getPositionVectorX(),
				model.getEndY() - model.getPositionVectorY());
	}

	@Override
	public GLine2D clipLine(int width, int height) {
		calculateArrowBase();
		GPoint2D[] clippedPoints = ClipLine.getClipped(model.getStartX(),
				model.getStartY(),
				arrowBase.getX(), arrowBase.getY(), -EuclidianStatic.CLIP_DISTANCE,
				width + EuclidianStatic.CLIP_DISTANCE,
				-EuclidianStatic.CLIP_DISTANCE,
				height + EuclidianStatic.CLIP_DISTANCE,
				tmpClipPoints);
		if (clippedPoints != null) {
			line.setLine(clippedPoints[0].getX(),
					clippedPoints[0].getY(), clippedPoints[1].getX(),
					clippedPoints[1].getY());
		}
		return line;
	}
}
