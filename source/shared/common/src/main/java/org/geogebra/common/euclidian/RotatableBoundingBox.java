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

import static org.geogebra.common.euclidian.BoundingBox.ROTATION_HANDLER_RADIUS;
import static org.geogebra.common.euclidian.BoundingBox.SIDE_HANDLER_HEIGHT;
import static org.geogebra.common.euclidian.BoundingBox.SIDE_HANDLER_WIDTH;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;

/**
 * Bounding box for a single element that rotates together with the geo.
 */
public class RotatableBoundingBox implements BoundingBoxDelegate {

	private final MediaBoundingBox box;

	public RotatableBoundingBox(MediaBoundingBox box) {
		this.box = box;
	}

	@Override
	public void createHandlers() {
		box.initHandlers(4, 5);
	}

	@Override
	public GEllipse2DDouble createCornerHandler() {
		return AwtFactory.getPrototype().newEllipse2DDouble();
	}

	@Override
	public GRectangle2D createSideHandler() {
		return AwtFactory.getPrototype().newRectangle2D();
	}

	@Override
	public void draw(GGraphics2D g2) {
		box.drawHandlers(g2);
	}

	@Override
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		return false;
	}

	@Override
	public void setHandlerFromCenter(int handlerIndex, double x, double y) {
		GShape handler = box.handlers.get(handlerIndex);
		if (box.isRotationHandler(handlerIndex)) {
			((GEllipse2DDouble) handler).setFrameFromCenter(x, y,
					x + ROTATION_HANDLER_RADIUS, y + ROTATION_HANDLER_RADIUS);
		} else if (box.isCornerHandler(handler)) {
			((GEllipse2DDouble) handler).setFrameFromCenter(x, y, x + BoundingBox.HANDLER_RADIUS,
					y + BoundingBox.HANDLER_RADIUS);
		} else if (box.isSideHandler(handler)) {
			GRectangle2D rectangleHandler = (GRectangle2D) handler;
			int width = handlerIndex % 2 == 0 ? SIDE_HANDLER_WIDTH : SIDE_HANDLER_HEIGHT;
			int height = handlerIndex % 2 == 0 ? SIDE_HANDLER_HEIGHT : SIDE_HANDLER_WIDTH;
			rectangleHandler.setFrame(x - width, y - height,
					width * 2, height * 2);
		}
	}
}
