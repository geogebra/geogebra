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

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoPolyLine;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;

public class PolyLineBoundingBox extends BoundingBox<GEllipse2DDouble> {

	private final GeoPolyLine poly;
	private final App app;

	/**
	 * @param poly polyline
	 * @param app application
	 */
	public PolyLineBoundingBox(GeoPolyLine poly, App app) {
		this.poly = poly;
		this.app = app;
	}

	@Override
	public void draw(GGraphics2D g2) {
		drawHandlers(g2);
	}

	@Override
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		return false;
	}

	@Override
	protected void createHandlers() {
		initHandlers(2 * poly.getNumPoints() - 1, 0);
	}

	@Override
	protected GEllipse2DDouble createCornerHandler() {
		return AwtFactory.getPrototype().newEllipse2DDouble();
	}

	@Override
	protected GEllipse2DDouble createSideHandler() {
		return AwtFactory.getPrototype().newEllipse2DDouble();
	}

	/**
	 * @param i
	 *            handler index
	 * @param x
	 *            screen x-coord
	 * @param y
	 *            screen y-coord
	 */
	public void setHandlerFromCenter(int i, double x, double y) {
		if (i < handlers.size()) {
			double radius = i < poly.getNumPoints() ? END_POINT_RADIUS : SPLITTER_RADIUS;
			handlers.get(i).setFrameFromCenter(x, y, x + radius, y + radius);
		}
	}

	@Override
	public EuclidianCursor getCursor(EuclidianBoundingBoxHandler nrHandler) {
		return EuclidianCursor.DRAG;
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

	@Override
	protected void drawRotationHandler(GGraphics2D g2) {
		// no rotation
	}

	@Override
	protected void drawHandlers(GGraphics2D g2) {
		int index = 0;
		g2.setStroke(AwtFactory.getPrototype().newBasicStroke(2));
		for (GShape handler : handlers) {
			if (index < poly.getNumPoints()) {
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

		drawRotationHandler(g2);
	}

	@Override
	public void handleDoubleClick(GPoint pointer, int hitThreshold) {
		int id = hitHandlers(pointer.x, pointer.y, hitThreshold);
		if (id >= poly.getNumPoints()) {
			int idx = id - poly.getNumPoints();
			double x1 = poly.getPoint(idx).getInhomX();
			double y1 = poly.getPoint(idx).getInhomY();
			double x2 = poly.getPoint(idx + 1).getInhomX();
			double y2 = poly.getPoint(idx + 1).getInhomY();
			double x = (x1 + x2) * .5 + (y2 - y1) * .25;
			double y = (y1 + y2) * .5 + (x1 - x2) * .25;
			undoable(algo -> algo.insertPoint(idx + 1, x, y));
		} else if (id > 0 && id < poly.getNumPoints() - 1) {
			undoable(algo -> algo.removePoint(id));
		}
	}

	private void undoable(Consumer<AlgoPolyLine> action) {
		AlgoElement parentAlgorithm = poly.getParentAlgorithm();
		if (parentAlgorithm instanceof AlgoPolyLine) {
			UpdateActionStore store = new UpdateActionStore(app.getSelectionManager(),
					app.getKernel().getConstruction().getUndoManager());
			store.addIfNotPresent(poly, MoveMode.NONE);
			action.accept((AlgoPolyLine) parentAlgorithm);
			store.storeUndo();
		}
	}
}
