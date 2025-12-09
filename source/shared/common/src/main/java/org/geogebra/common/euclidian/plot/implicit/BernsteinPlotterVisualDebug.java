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
import org.geogebra.common.util.debug.Log;

/**
 * Draws visual debug on EV, cell bounds, kinds, possible solutions, edges, etc.
 * with colors.
 * This class should not be used in release!
 */
final class BernsteinPlotterVisualDebug implements VisualDebug {
	private final EuclidianViewBounds bounds;
	private List<BernsteinPlotCell> cells;

	BernsteinPlotterVisualDebug(EuclidianViewBounds bounds, List<BernsteinPlotCell> cells) {
		this.bounds = bounds;
		this.cells = cells;
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (cells == null) {
			return;
		}

		for (BernsteinPlotCell cell : cells) {
			if (found(cell.boundingBox)) {
				drawCell(g2, cell);
				if (cell.getMarchingConfig() == BernsteinMarchingConfig.T1111) {
					Log.debug(cell.polynomial);
				}
			}
		}

	}

	private boolean found(BernsteinBoundingBox box) {
		return box.x1() > 0.15 && box.x2() < 0.16
				&& box.y1() < -0.01;
	}

	private void drawCell(GGraphics2D g2, BernsteinPlotCell cell) {
		GColor color = getCellColor(cell);

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

	private static GColor getCellColor(BernsteinPlotCell cell) {
		switch (cell.getKind()) {
		case CELL0:
			return GColor.GREEN;
		case CELL1:
			return GColor.YELLOW;
		case CELL2:
			return GColor.GRAY;
		default:
			return GColor.BLACK;
		}
	}
}
