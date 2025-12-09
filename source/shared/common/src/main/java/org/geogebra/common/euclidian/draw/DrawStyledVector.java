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
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.clipping.ClipLine;

public class DrawStyledVector {

	private GLine2D line;
	private final EuclidianView view;
	private final DrawableVisibility visibility;
	private final GPoint2D[] tmpClipPoints = {new GPoint2D(), new GPoint2D()};
	private GArea area;

	DrawStyledVector(DrawableVisibility visibility, EuclidianView view) {
		this.view = view;
		this.visibility = visibility;
	}

	void update(VectorShape vectorShape) {
		DrawVectorModel model = vectorShape.model();
		boolean startOnScreen = model.isStartOnScreen(view);
		boolean endOnScreen = model.isEndOnScreen(view);

		model.update();

		if (startOnScreen && endOnScreen) {
			line = vectorShape.body();
		} else {
			clipLine(vectorShape, model);
		}

		if (visibility.isVisible()) {
			createVectorShape(model.getStroke(), model.length(), vectorShape);
		}
	}

	private void clipLine(VectorShape vectorShape, DrawVectorModel model) {
		if (checkOffScreen(model)) {
			visibility.setVisible(false);
		} else {
			line = vectorShape.clipLine(view.getWidth(), view.getHeight());
		}
	}

	private boolean checkOffScreen(DrawVectorModel model) {
		// A or B off screen
		// clip at screen, that's important for huge coordinates
		// check if any of vector is on-screen
		GPoint2D[] clippedPoints = ClipLine.getClipped(model.getStartX(),
				model.getStartY(), model.getEndX(), model.getEndY(),
				view.getMinXScreen() - EuclidianStatic.CLIP_DISTANCE,
				view.getMaxXScreen() + EuclidianStatic.CLIP_DISTANCE,
				view.getMinYScreen() - EuclidianStatic.CLIP_DISTANCE,
				view.getMaxYScreen() + EuclidianStatic.CLIP_DISTANCE,
				tmpClipPoints);
		return clippedPoints == null;
	}
	
	private void createVectorShape(GBasicStroke stroke, double length, VectorShape vectorShape) {
		area = AwtFactory.getPrototype().newArea();
		GShape strokedLine = stroke.createStrokedShape(line, 255);
		area.add(GCompositeShape.toArea(strokedLine));

		if (length > 0) {
			area.add(GCompositeShape.toArea(vectorShape.head()));
		}
	}

	void draw(GGraphics2D g2) {
		if (visibility.isVisible() && area != null) {
			g2.fill(area);
		}
	}

	public GRectangle getBounds() {
		return area == null ? null : area.getBounds();
	}

	/**
	 * Checks if this shape intersects given rectangle.
	 * @param x1 rectangle's left
	 * @param y1 rectangle's top
	 * @param w rectangle's width
	 * @param h rectangle's height
	 * @return whether this shape instersects the rectangle.
	 */
	public boolean intersects(int x1, int y1, int w, int h) {
		return area != null && area.intersects(x1, y1, w, h);
	}

	/**
	 * Fill the shape in given graphics.
	 * @param g2 graphics
	 */
	public void fill(GGraphics2D g2) {
		g2.fill(area);
	}
}
