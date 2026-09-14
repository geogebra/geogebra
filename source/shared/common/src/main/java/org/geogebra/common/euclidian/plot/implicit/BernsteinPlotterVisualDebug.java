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

package org.geogebra.common.euclidian.plot.implicit;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.MyPoint;

/**
 * Renders a debugging overlay of Bernstein cells and marching-config
 * information onto a EuclidianView.
 *
 * <p><strong>NOTE:</strong> this class is intended for development only
 * and must not be included in production releases.</p>
 */

final class BernsteinPlotterVisualDebug implements VisualDebug {
	private final EuclidianViewBounds bounds;
	private List<BernsteinPlotCell> cells;
	private List<MyPoint> edgePoints = null;

	/**
	 * Creates a new visual debugger for the given view bounds, cells, and contour.
	 * @param bounds the {@link EuclidianViewBounds} used to convert model to screen coords
	 * @param cells the list of {@link BernsteinPlotCell}s to render
	 */
	BernsteinPlotterVisualDebug(EuclidianViewBounds bounds, List<BernsteinPlotCell> cells) {
		this.bounds = bounds;
		this.cells = cells;
	}

	/**
	 * Draws the debug overlay for every cell that meets the debug criteria.
	 *
	 * @param g2 the graphics context onto which cells are drawn
	 */
	@Override
	public void draw(GGraphics2D g2) {
		if (cells == null) {
			return;
		}

		if (edgePoints != null) {
			drawEdgePoints(g2);
		}
	}

	private void drawEdgePoints(GGraphics2D g2) {
		g2.setColor(GColor.BLUE);
		for (MyPoint p: edgePoints) {
			double sx = bounds.toScreenCoordXd(p.x);
			double sy = bounds.toScreenCoordYd(p.y);
			int r = 5;
			g2.fillRect((int) (sx - r), (int) (sy + r), r, r);
		}
	}

	@Override
	public void fill(GGraphics2D g2) {
		draw(g2);
	}

	@Override
	public void setEdgePoints(List<MyPoint> edgePoints) {
		this.edgePoints = edgePoints;
	}

	@SuppressWarnings("unused")
	private boolean found(BernsteinPlotCell cell) {
		BernsteinBoundingBox box = cell.boundingBox;
		return  box.y2() > 0.86 && box.y1() < 0.95
		 && cell.getMarchingConfig() == BernsteinMarchingConfig.T0110;
	}

	@SuppressWarnings("unused")
	private void drawCell(GGraphics2D g2, BernsteinPlotCell cell) {
		GColor color = ContourDebugColorScheme.of(cell.getKind());

		int x = (int) bounds.toScreenCoordXd(cell.boundingBox.x1());
		int y = (int) bounds.toScreenCoordYd(cell.boundingBox.y1());
		int width = (int) (bounds.toScreenCoordXd(cell.boundingBox.x2()) - x);
		int height = (int) (bounds.toScreenCoordYd(cell.boundingBox.y2()) - y);
		g2.setColor(color);

		g2.drawRect(x, y, width, height);
		drawConfigText(g2, cell, x, y - width / 2);
	}

	private static void drawConfigText(GGraphics2D g2, BernsteinPlotCell cell, int x, int y) {
		BernsteinMarchingConfig config = (BernsteinMarchingConfig) cell.getMarchingConfig();
		g2.setColor(config.color());
		g2.drawString(config.toString(), x, y);
	}
}
