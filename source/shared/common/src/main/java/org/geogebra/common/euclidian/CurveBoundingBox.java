package org.geogebra.common.euclidian;

import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.factories.AwtFactory;
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
