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

import javax.annotation.Nonnull;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.main.GeoGebraColorConstants;

public class CurveBoundingBox extends BoundingBox<GEllipse2DDouble> {

	private final double[] handlerCenterX = new double[4];
	private final double[] handlerCenterY = new double[4];
	private final GLine2D line = AwtFactory.getPrototype().newLine2D();

	@Override
	protected void createHandlers() {
		initHandlers(4, 0);
	}

	@Override
	protected GEllipse2DDouble createCornerHandler() {
		return AwtFactory.getPrototype().newEllipse2DDouble();
	}

	@Override
	protected GEllipse2DDouble createSideHandler() {
		return null;
	}

	@Override
	public void draw(GGraphics2D g2) {
		drawHandlers(g2);
	}

	@Override
	protected void drawHandlers(GGraphics2D g2) {
		int index = 0;
		g2.setStroke(AwtFactory.getPrototype().newBasicStroke(2));
		g2.setColor(GeoGebraColorConstants.NEUTRAL_400);
		connectHandlers(0, 1, g2);
		connectHandlers(2, 3, g2);
		for (GShape handler : handlers) {
			if (index == 0 || index == 3) {
				fillHandlerWhite(g2, handler);
				g2.setColor(GeoGebraColorConstants.NEUTRAL_600);
				g2.draw(handler);
			} else {
				g2.setColor(GeoGebraColorConstants.NEUTRAL_600);
				g2.fill(handler);
				g2.setColor(GColor.WHITE);
				g2.draw(handler);
			}
			index++;
		}
	}

	private void connectHandlers(int from, int to, GGraphics2D g2) {
		line.setLine(handlerCenterX[from], handlerCenterY[from],
				handlerCenterX[to], handlerCenterY[to]);
		g2.draw(line);
	}

	@Override
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		return false;
	}

	/**
	 * Update handler from coordinates.
	 * @param i handler index
	 * @param x screen x-coordinate
	 * @param y screen y-coordinate
	 */
	public void setHandlerFromCenter(int i, double x, double y) {
		if (i < handlers.size()) {
			double radius = i == 0 || i == 3 ? END_POINT_RADIUS : SPLITTER_RADIUS;
			handlers.get(i).setFrameFromCenter(x, y, x + radius, y + radius);
			handlerCenterX[i] = x;
			handlerCenterY[i] = y;
		}
	}

	@Override
	public @Nonnull ShapeManipulationHandler getHitHandler(int x, int y,
			int hitThreshold) {
		int hit = hitHandlers(x, y, hitThreshold);
		if (hit >= 0) {
			return new ControlPointHandler(hit);
		}
		return EuclidianBoundingBoxHandler.UNDEFINED;
	}
}
