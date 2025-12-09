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

package org.geogebra.common.euclidian.background;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.settings.EuclidianSettings;

/**
 * Helper class for drawing the background
 * 
 * @author laszlo
 *
 */
public class DrawBackground {
	/*
	 * Default scale (when the view not zoomed) of the svg file used as
	 * background
	 */
	private static final double SVG_SCALE = 2;
	private static final double SVG_BASE_WIDTH = 539;
	private static final double RULING_BASE_WIDTH = 10.5;
	private static final double DOT_SIZE = 2;
	private static final int MIN_DOT_DISTANCE = 5;
	private final EuclidianView view;
	private final EuclidianSettings settings;
	private double gap;
	private double yScale;
	private double width;

	/**
	 * 
	 * @param euclidianView
	 *            view
	 * @param settings
	 *            euclidian settings
	 */
	public DrawBackground(EuclidianView euclidianView, EuclidianSettings settings) {
		view = euclidianView;
		this.settings = settings;
	}

	/**
	 * Draws the background for MOW.
	 * 
	 * @param g2
	 *            graphics
	 */
	public void draw(GGraphics2D g2) {
		GBasicStroke rulerStroke = EuclidianStatic.getStroke(settings.isRulerBold() ? 2f : 1f,
				settings.getRulerLineStyle());
		g2.setStroke(rulerStroke);
		updateRulerGap();
		gap = settings.getBackgroundRulerGap();
		width = RULING_BASE_WIDTH;
		switch (settings.getBackgroundType()) {
		case RULER:
			drawRuledBackground(g2);
			break;
		case SQUARE_BIG:
			drawSquaredSubgrid(g2);
			drawSquaredBackground(g2);
			break;
		case SQUARE_SMALL:
			gap = settings.getBackgroundRulerGap() / 2;
			width = 21;
			drawSquaredBackground(g2);
			break;
		case DOTS:
			drawDottedBackground(g2);
			break;
		case ISOMETRIC:
		case POLAR:
			// do nothing; uses standard grid paint
			break;
		case SVG:
		case ELEMENTARY12:
		case ELEMENTARY12_HOUSE:
		case ELEMENTARY12_COLORED:
		case ELEMENTARY34:
		case MUSIC:
			drawSVG(g2);
			break;
		default:
			break;
		}
	}

	private void drawSVG(GGraphics2D g2) {
		MyImage svg = view.getSVGBackground();
		double scale = view.getYscale() / EuclidianView.SCALE_STANDARD * SVG_SCALE;
		int h = (int) (svg.getHeight() * scale);
		int y = (int) (view.getYZero() % h);
		if (y > 0) {
			y -= h;
		}

		width = svg.getWidth() / SVG_BASE_WIDTH * RULING_BASE_WIDTH;

		int x = (int) getStartX();
		g2.saveTransform();

		g2.scale(scale, scale);
		while (h != 0 && y < view.getMaxYScreen()) {
			g2.drawImage(svg, (int) (x / scale), (int) (y / scale));
			y += h;
		}
		g2.restoreTransform();
	}

	/**
	 * Update the gap between two ruler lines, if the view zoomed
	 */
	public void updateRulerGap() {
		if (yScale == 0) {
			yScale = view.getYscale();
			return;
		}
		if (yScale != view.getYscale()) {
			double factor = view.getYscale() / yScale;
			settings.setBackgroundRulerGap(settings.getBackgroundRulerGap() * factor);
			yScale = view.getYscale();
		}
	}

	private double getStartX() {
		return view.getXZero() - width * gap;
	}

	private double getEndX() {
		return view.getXZero() + width * gap;
	}

	private void drawVerticalFrame(GGraphics2D g2) {
		double start = view.getYZero() % gap;
		// draw main grid
		g2.setColor(settings.getBgRulerColor());
		g2.startGeneralPath();

		double x = getStartX();
		double xEnd = getEndX();
		double yEnd = view.getHeight();
		double y = start - gap;
		g2.addStraightLineToGeneralPath(x, y, x, yEnd);
		g2.addStraightLineToGeneralPath(xEnd, y, xEnd, yEnd);
		g2.endAndDrawGeneralPath();
	}

	private void drawHorizontalLines(GGraphics2D g2, boolean subgrid, boolean infinite) {
		double start = view.getYZero() % gap;
		double startX = infinite ? start - gap : getStartX();
		double endX = infinite ? view.getWidth() : getEndX();

		doDrawHorizontalLines(g2, subgrid, startX, endX, start - gap,
				view.getHeight());
	}

	private void doDrawHorizontalLines(GGraphics2D g2, boolean subgrid, double xStart, double xEnd,
			double yStart, double yEnd) {
		// draw main grid
		g2.setColor(subgrid ? settings.getBgSubLineColor()
				: settings.getBgRulerColor());
		g2.startGeneralPath();

		double y = yStart;
		if (subgrid) {
			double subGap = gap / 10;
			int lineCount = 0;
			while (y <= yEnd) {
				if (lineCount % 10 != 0) {
					addStraightLineToGeneralPath(g2, xStart, y, xEnd, y);
				}
				y += subGap;
				lineCount++;
			}
		} else {
			while (y <= yEnd) {
				addStraightLineToGeneralPath(g2, xStart, y, xEnd, y);
				y += gap;
			}
		}
		g2.endAndDrawGeneralPath();
	}

	private void drawVerticalLines(GGraphics2D g2, boolean subgrid) {
		double start = view.getYZero() % gap;
		double startX = (view.getXZero() % gap) - gap;
		double endX = view.getWidth();

		doDrawVerticalLines(g2, subgrid, startX, endX, start - gap,
				view.getHeight() + 2 * gap);
	}

	private void doDrawVerticalLines(GGraphics2D g2, boolean subgrid, double xStart, double xEnd,
			double yStart, double height) {
		// draw main grid
		g2.setColor(subgrid ? settings.getBgSubLineColor()
				: settings.getBgRulerColor());
		g2.startGeneralPath();

		double x = xStart;
		if (subgrid) {
			double subGap = gap / 10;
			int lineCount = 0;
			while (x <= xEnd) {
				if (lineCount % 10 != 0) {
					addStraightLineToGeneralPath(g2, x, yStart, x, yStart + height);
				}
				x += subGap;
				lineCount++;
			}
		} else {
			while (x <= xEnd) {
				addStraightLineToGeneralPath(g2, x, yStart, x, yStart + height);
				x += gap;
			}
			// make sure last line is drawn despite of rounding errors
			addStraightLineToGeneralPath(g2, xEnd, yStart, xEnd, yStart + height);
		}
		g2.endAndDrawGeneralPath();
	}

	private void drawRuledBackground(GGraphics2D g2) {
		drawHorizontalLines(g2, false, false);
		drawVerticalFrame(g2);
	}

	private void drawSquaredBackground(GGraphics2D g2) {
		drawHorizontalLines(g2, false, true);
		drawVerticalLines(g2, false);
	}

	private void drawSquaredSubgrid(GGraphics2D g2) {
		drawHorizontalLines(g2, true, true);
		drawVerticalLines(g2, true);
	}

	private static void addStraightLineToGeneralPath(GGraphics2D g2, double x1,
			double y1, double x2, double y2) {
		g2.addStraightLineToGeneralPath(x1, y1, x2, y2);
	}

	private void drawDottedBackground(GGraphics2D g2) {
		double start = view.getYZero() % gap;
		double startX = (view.getXZero() % gap) - gap;
		double endX = view.getWidth();

		drawDots(g2, startX, endX, start - gap, view.getHeight() + 2 * gap,
				gap, gap, settings);
	}

	/**
	 * Draw dotted grid
	 * @param g2 - graphics
	 * @param xStart - horizontal starting point of view
	 * @param xEnd - horizontal end point of view
	 * @param yStart - vertical starting point of view
	 * @param yEnd - vertical end point of view
	 * @param dotDistanceX - horizontal distance between dots
	 * @param dotDistanceY - vertical distance between dots
	 * @param settings - determines color
	 */
	public static void drawDots(GGraphics2D g2, double xStart, double xEnd,
			double yStart, double yEnd, double dotDistanceX, double dotDistanceY,
			EuclidianSettings settings) {
		if (dotDistanceX < MIN_DOT_DISTANCE || dotDistanceY < MIN_DOT_DISTANCE) {
			return;
		}

		g2.setColor(settings.getGridColor());

		double y = yStart;
		while (y <= yEnd) {
			double x = xStart;
			while (x <= xEnd) {
				drawDot(g2, x - 1, y - 1);
				x += dotDistanceX;
			}
			y += dotDistanceY;
		}
		// make sure last line of dots is drawn despite of rounding errors
		drawDot(g2, xEnd - 1, yStart + yEnd - 1);
	}

	private static void drawDot(GGraphics2D g2, double x, double y) {
		GShape dot = AwtFactory.getPrototype().newEllipse2DDouble(x, y, DOT_SIZE, DOT_SIZE);
		g2.fill(dot);
		g2.draw(dot);
	}
}
