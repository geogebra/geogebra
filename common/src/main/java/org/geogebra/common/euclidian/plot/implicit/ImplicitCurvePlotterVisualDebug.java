package org.geogebra.common.euclidian.plot.implicit;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

final class ImplicitCurvePlotterVisualDebug {
	private final EuclidianViewBounds bounds;
	private final List<BernsteinPlotCell> subContexts;

	ImplicitCurvePlotterVisualDebug(EuclidianViewBounds bounds,
			List<BernsteinPlotCell> subContexts) {
		this.bounds = bounds;
		this.subContexts = subContexts;
	}


	void draw(GGraphics2D g2) {
	//	drawOutput(g2, output);
		for (BernsteinPlotCell ctx : subContexts) {
			drawDebug(g2, ctx);
		}

	}

	private void drawDebug(GGraphics2D g2, BernsteinPlotCell cell) {
		GColor color;
		switch (cell.getKind()) {
		case CELL0:
			color = GColor.GREEN;
			break;
		case CELL1:
			color = GColor.BLUE;
			break;
		case CELL2:
			color = GColor.GRAY;
			break;
		default:
			color = GColor.BLACK;
		}

		int x = (int) (bounds.toScreenCoordXd(cell.boundingBox.getX1()));
		int y = (int) (bounds.toScreenCoordYd(cell.boundingBox.getY1()));
		int width = (int) (bounds.toScreenCoordXd(cell.boundingBox.getX2()) - x);
		int height = (int) (bounds.toScreenCoordYd(cell.boundingBox.getY2()) - y);
		g2.setColor(color);

//		g2.fillRect(x, y, width, height);
		g2.setColor(GColor.BLACK.deriveWithAlpha(25));
		g2.drawRect(x, y, width, height);
	}

	private void drawOutput(GGraphics2D g2, List<BoxEdge> output) {
		for (BoxEdge edge : output) {
			g2.setColor(edge.getKind().getColor());
			GPoint2D p = edge.startPoint();
			g2.fillRect((int) bounds.toScreenCoordXd(p.x),
					(int) bounds.toScreenCoordYd(p.y), 1, 1);
		}

	}

	public void drawEdges(GGraphics2D g2, List<BoxEdge> edges) {
		for (BoxEdge edge : edges) {
			edge.draw(g2, bounds);
		}

	}
}
