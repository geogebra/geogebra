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

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GShape;
import org.geogebra.ggbjdk.java.awt.geom.Line2D;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle2D;
import org.geogebra.ggbjdk.java.awt.geom.RoundRectangle2D;
import org.jspecify.annotations.NonNull;

public class SvgGraphics extends GGraphicsCommon {

	private final StringBuilder svg;
	private GColor color;

	public SvgGraphics(int width, int height) {
		svg = new StringBuilder(String.format(
				"<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 %1$s %2$s\" width=\"%1$s\" height=\"%2$s\">",
				width, height));
	}

	@Override
	public void draw(@NonNull GShape s) {
		path(s.getPathIterator(null), false);
	}

	@Override
	public void drawString(String str, int x, int y) {
		drawString(str, (double) x, y);
	}

	@Override
	public void drawString(String str, double x, double y) {
		svg.append("\n<text x=\"")
				.append(x)
				.append("\" y=\"")
				.append(y)
				.append("\">")
				.append(str)
				.append("</text>");
	}

	@Override
	public void fill(@NonNull GShape s) {
		path(s.getPathIterator(null), true);
	}

	private void path(@NonNull GPathIterator it, boolean fill) {
		svg.append("\n<path d=\"");
		double[] coords = new double[6];
		while (!it.isDone()) {
			int cu = it.currentSegment(coords);
			switch (cu) {
				default:
					// do nothing
					break;
				case GPathIterator.SEG_MOVETO:
					svg.append("M").append(coords[0]).append(",").append(coords[1]);
					break;
				case GPathIterator.SEG_LINETO:
					svg.append("L").append(coords[0]).append(",").append(coords[1]);
					break;
				case GPathIterator.SEG_CUBICTO:
					svg.append("C")
							.append(coords[0])
							.append(",")
							.append(coords[1])
							.append(",")
							.append(coords[2])
							.append(",")
							.append(coords[3])
							.append(",")
							.append(coords[4])
							.append(",")
							.append(coords[5]);
					break;
				case GPathIterator.SEG_QUADTO:
					svg.append("Q")
							.append(coords[0])
							.append(",")
							.append(coords[1])
							.append(",")
							.append(coords[2])
							.append(",")
							.append(coords[3]);
					break;
				case GPathIterator.SEG_CLOSE:
					svg.append("Z");
			}
			it.next();
		}
		String type = fill ? "fill" : "stroke";

		svg.append("\" ")
				.append(type)
				.append("=\"#")
				.append(hex(color.getRed()))
				.append(hex(color.getGreen()))
				.append(hex(color.getBlue()))
				.append("\" stroke-opacity=\"")
				.append(color.getAlpha() / 255.0)
				.append("\"/>");
	}

	private String hex(int val) {
		return (val < 16 ? "0" : "") + Integer.toString(val, 16);
	}

	@Override
	public void setPaint(GPaint paint) {
		if (paint instanceof GColor asColor) {
			color = asColor;
		}
	}

	@Override
	public GColor getColor() {
		return color;
	}

	@Override
	public void setColor(GColor color) {
		this.color = color;
	}

	@Override
	public void fillRect(int x, int y, int w, int h) {
		fill(new Rectangle2D.Double(x, y, w, h));
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		path(new Line2D.Double(x1, y1, x2, y2).getPathIterator(null), false);
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		draw(new Rectangle2D.Double(x, y, width, height));
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		path(
				new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight).getPathIterator(null),
				false);
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		path(
				new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight).getPathIterator(null),
				true);
	}

	@Override
	public void drawStraightLine(double x1, double y1, double x2, double y2) {
		path(new Line2D.Double(x1, y1, x2, y2).getPathIterator(null), false);
	}

	@Override
	public void addStraightLineToGeneralPath(double x1, double y1, double x2, double y2) {
		drawStraightLine(x1, y1, x2, y2);
	}

	@Override
	public String toString() {
		return svg.toString() + "\n</svg>";
	}
}
