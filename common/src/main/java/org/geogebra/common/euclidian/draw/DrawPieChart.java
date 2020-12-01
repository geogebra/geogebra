package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GArc2D;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.algos.ChartStyle;
import org.geogebra.common.kernel.statistics.AlgoPieChart;
import org.geogebra.common.kernel.statistics.GeoPieChart;

public class DrawPieChart extends Drawable {
	private final GeoPieChart chart;
	ArrayList<GArc2D> slices = new ArrayList<>();
	private final ChartFilling chartFilling = new ChartFilling();

	/**
	 * @param ev view
	 * @param geo chart
	 */
	public DrawPieChart(EuclidianView ev, GeoPieChart geo) {
		super(ev, geo);
		this.chart = geo;
		update();
	}

	@Override
	public void update() {
		double old = 0;
		slices.clear();
		for (Double val: chart.getData()) {
			GArc2D slice = AwtFactory.getPrototype().newArc2D();
			double centerX = chart.getCenter().getX();
			double centerY = chart.getCenter().getY();
			double radiusX = chart.getRadius() * view.getXscale();
			double radiusY = chart.getRadius() * view.getYscale();
			slice.setArc(view.toScreenCoordXd(centerX) - radiusX,
					view.toScreenCoordYd(centerY) - radiusY,
					2 * radiusX, 2 * radiusY,
					old * 360, val * 360, GArc2D.PIE);
			old = old + val;
			slices.add(slice);
		}
		updateStrokes(geo);
	}

	@Override
	public void draw(GGraphics2D g2) {
		ChartStyle style = ((AlgoPieChart) geo.getParentAlgorithm()).getStyle();
		g2.setStroke(objStroke);
		for (int i = 0; i < slices.size(); i++) {
			GColor color = style.getBarColor(i + 1);
			g2.setColor(color.deriveWithAlpha(geo.getLineOpacity()));
			g2.draw(slices.get(i));
			chartFilling.fill(g2, slices.get(i), style, i + 1, this);
		}
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		double centerX = chart.getCenter().getX();
		double centerY = chart.getCenter().getY();
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
