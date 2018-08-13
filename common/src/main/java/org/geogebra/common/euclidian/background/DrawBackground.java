package org.geogebra.common.euclidian.background;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GGraphics2D;
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
	private EuclidianView view;
	private EuclidianSettings settings;
	private double gap;
	private GBasicStroke rulerStroke;
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
		rulerStroke = EuclidianStatic.getStroke(settings.isRulerBold() ? 2f : 1f,
				settings.getRulerLineStyle());
		g2.setStroke(rulerStroke);
		updateRulerGap();
		gap = settings.getBackgroundRulerGap();
		width = 10.5;
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
		case SVG:
		case ELEMENTARY12:
		case ELEMENTARY12_HOUSE:
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
		int h = svg.getHeight();
		int y = (int) view.getYZero() % h;
		if (y > 0) {
			y -= h;
		}

		int x = (int) getStartX();
		g2.saveTransform();
		g2.translate(0, y);
		y = 0;

		g2.scale(view.getXscale() / 25, view.getYscale() / 25);
		while (h != 0 && y < view.getMaxYScreen()) {
			g2.drawImage(svg, x, y);
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

	private void drawHorizontalLines(GGraphics2D g2, boolean subgrid) {
		double start = view.getYZero() % gap;

		// draw main grid
		g2.setColor(subgrid ? settings.getBgSubLineColor()
				: settings.getBgRulerColor());
		g2.startGeneralPath();
		double x = getStartX();
		double xEnd = getEndX();
		double yEnd = view.getHeight();
		double y = start - gap;

		if (subgrid) {
			double subGap = gap / 10;
			int lineCount = 0;
			while (y <= yEnd) {
				if (lineCount % 10 != 0) {
					addStraightLineToGeneralPath(g2, x, y, xEnd, y);
				}
				y += subGap;
				lineCount++;
			}
		} else {
			while (y <= yEnd) {
				addStraightLineToGeneralPath(g2, x, y, xEnd, y);
				y += gap;
			}
		}
		g2.endAndDrawGeneralPath();
	}

	private void drawVerticalLines(GGraphics2D g2, boolean subgrid) {
		double start = view.getYZero() % gap;
		// draw main grid
		g2.setColor(subgrid ? settings.getBgSubLineColor()
				: settings.getBgRulerColor());
		g2.startGeneralPath();

		double x = getStartX();
		double xEnd = getEndX();
		double y = start - gap;
		double height = view.getHeight() + 2 * gap;

		if (subgrid) {
			double subGap = gap / 10;
			int lineCount = 0;
			while (x <= xEnd) {
				if (lineCount % 10 != 0) {
					addStraightLineToGeneralPath(g2, x, y, x, y + height);
				}
				x += subGap;
				lineCount++;
			}
		} else {
			while (x <= xEnd) {
				addStraightLineToGeneralPath(g2, x, y, x, y + height);
				x += gap;
			}
			// make sure last line is drawn despite of rounding errors
			addStraightLineToGeneralPath(g2, xEnd, y, xEnd, y + height);
		}
		g2.endAndDrawGeneralPath();
	}

	private void drawRuledBackground(GGraphics2D g2) {
		drawHorizontalLines(g2, false);
		drawVerticalFrame(g2);
	}

	private void drawSquaredBackground(GGraphics2D g2) {
		drawHorizontalLines(g2, false);
		drawVerticalLines(g2, false);
	}

	private void drawSquaredSubgrid(GGraphics2D g2) {
		drawHorizontalLines(g2, true);
		drawVerticalLines(g2, true);
	}

	private static void addStraightLineToGeneralPath(GGraphics2D g2, double x1,
			double y1, double x2, double y2) {
		g2.addStraightLineToGeneralPath(x1, y1, x2, y2);
	}
}
