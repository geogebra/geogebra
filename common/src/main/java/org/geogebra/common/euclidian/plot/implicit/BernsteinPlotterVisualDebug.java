package org.geogebra.common.euclidian.plot.implicit;

import java.util.List;
import java.util.Map;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.implicit.PlotRectConfig;

/**
 * Draws visual debug on EV, cell bounds, kinds, possible solutions, edges, etc.
 * with colors.
 * This class should not be used in release!
 */
final class BernsteinPlotterVisualDebug implements VisualDebug<BernsteinPlotCell> {
	private final EuclidianViewBounds bounds;
	private List<BernsteinPlotCell> cells;

	BernsteinPlotterVisualDebug(EuclidianViewBounds bounds) {
		this.bounds = bounds;
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (cells == null) {
			return;
		}

		for (BernsteinPlotCell cell : cells) {
			drawCell(g2, cell);
		}

	}

	private void drawEdgePoints(GGraphics2D g2, Map<EdgeKind, GPoint2D> points) {
		points.forEach((kind, p) -> drawEdgePoint(g2, p, kind));
	}

	private static void setLineWidth(GGraphics2D g2, int width) {
		g2.setStroke(AwtFactory.getPrototype().newBasicStroke(width));
	}

	private void drawCell(GGraphics2D g2, BernsteinPlotCell cell) {
		if (!needsHighlight(cell.getRectConfig())) {
			return;
		}
		GColor color = getCellColor(cell);

		int x = (int) (bounds.toScreenCoordXd(cell.boundingBox.x1()));
		int y = (int) (bounds.toScreenCoordYd(cell.boundingBox.y1()));
		int width = (int) (bounds.toScreenCoordXd(cell.boundingBox.x2()) - x);
		int height = (int) (bounds.toScreenCoordYd(cell.boundingBox.y2()) - y);
		g2.setColor(color);

		setLineWidth(g2, 1);
		g2.drawRect(x, y, width, height);
		drawConfigText(g2, cell, x, y - width / 2);
//		drawEdgePoints(g2, cell.getEdgeSolutions());
	}

	private void drawEdgePoint(GGraphics2D g2, GPoint2D p, EdgeKind kind) {
		if (p == null) {
			return;
		}

		setLineWidth(g2, 5);
		g2.setColor(getEdgeColor(kind));
		int x = (int) bounds.toScreenCoordXd(p.x);
		int y = (int) bounds.toScreenCoordYd(p.y);
		if (kind == EdgeKind.RIGHT) {
			x += 5;
		}
		g2.drawRect(x, y, 1, 1);
	}

	private GColor getEdgeColor(EdgeKind kind) {
		switch (kind) {
		case TOP:
			return GColor.RED;
		case BOTTOM:
			return GColor.BLUE;
		case LEFT:
			return GColor.ORANGE;
		case RIGHT:
			return GColor.YELLOW;
		}
		return GColor.LIGHT_GRAY;
	}

	private boolean needsHighlight(PlotRectConfig config) {
		return true;
	}

	private static void drawConfigText(GGraphics2D g2, BernsteinPlotCell cell, int x, int y) {
		BernsteinEdgeConfig config = (BernsteinEdgeConfig) cell.getRectConfig();
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

	/**
	 *
	 * @param cells to display debug information from.
	 */
	@Override
	public void setData(List<BernsteinPlotCell> cells) {
		this.cells = cells;
	}
}
