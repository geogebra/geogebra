package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GArc2D;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.ChartStyle;
import org.geogebra.common.kernel.statistics.AlgoPieChart;
import org.geogebra.common.kernel.statistics.GeoPieChart;

public class DrawPieChart extends Drawable {
	private static final double INITIAL_ANGLE = 0.25; // fraction of full angle
	private final GeoPieChart chart;
	private final ArrayList<GArc2D> slices = new ArrayList<>();
	private final ChartFilling chartFilling = new ChartFilling();
	private final GEllipse2DDouble outline;
	private final ArrayList<GLine2D> rays = new ArrayList<>();
	private boolean labelVisible;

	/**
	 * @param ev view
	 * @param geo chart
	 */
	public DrawPieChart(EuclidianView ev, GeoPieChart geo) {
		super(ev, geo);
		this.chart = geo;
		outline = AwtFactory.getPrototype().newEllipse2DDouble();
		update();
	}

	@Override
	public void update() {
		double old = INITIAL_ANGLE;
		slices.clear();
		rays.clear();
		double centerX = view.toScreenCoordXd(chart.getCenter().getX());
		double centerY = view.toScreenCoordYd(chart.getCenter().getY());
		double radiusX = chart.getRadius() * view.getXscale();
		double radiusY = chart.getRadius() * view.getYscale();
		outline.setFrameFromCenter(centerX, centerY,
				centerX + radiusX, centerY + radiusY);
		for (Double val: chart.getData()) {
			GArc2D slice = AwtFactory.getPrototype().newArc2D();
			slice.setArc(centerX - radiusX, centerY - radiusY,
					2 * radiusX, 2 * radiusY,
					old * 360, - val * 360, GArc2D.PIE);
			old = old - val;
			slices.add(slice);
			if (geo.getLineThickness() > 0) {
				GLine2D ray = AwtFactory.getPrototype().newLine2D();
				ray.setLine(centerX, centerY, centerX + Math.cos(old * Kernel.PI_2) * radiusX,
						centerY - Math.sin(old * Kernel.PI_2) * radiusY);
				rays.add(ray);
			}
		}
		updateStrokes(geo);
		labelVisible = geo.isLabelVisible();
		if (labelVisible) {
			xLabel = (int) (centerX + Math.sqrt(.5) * radiusX);
			yLabel = (int) (centerY + Math.sqrt(.5) * radiusY) + view.getFontSize();
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (isEuclidianVisible()) {
			ChartStyle style = ((AlgoPieChart) geo.getParentAlgorithm()).getStyle();
			g2.setStroke(objStroke);
			for (int i = 0; i < slices.size(); i++) {
				GColor color = style.getBarColor(i + 1);
				g2.setColor(color.deriveWithAlpha(geo.getLineOpacity()));
				chartFilling.fill(g2, slices.get(i), style, i + 1, this);
			}
			g2.setColor(getObjectColor());
			if (rays.size() > 0) {
				for (GLine2D ray : rays) {
					g2.draw(ray);
				}
				g2.draw(outline);
			}
			if (labelVisible) {
				drawLabel(g2);
			}
		}
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		GPoint2D center = chart.getCenter();
		if (center == null) {
			return false;
		}
		double centerX = center.getX();
		double centerY = center.getY();
		double radius = chart.getRadius();
		double rwx = view.toRealWorldCoordX(x);
		double rwy = view.toRealWorldCoordY(y);
		double distSquare = (centerX - rwx) * (centerX - rwx)
				+ (centerY - rwy) * (centerY - rwy) ;
		return distSquare <= radius * radius;
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return false;
	}

}
