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

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoSlope;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;

import com.google.j2objc.annotations.Weak;

/**
 * Draws the slope triangle for the slope of a line.
 * @author Markus Hohenwarter
 */
public class DrawSlope extends Drawable {

	private GeoNumeric slope;
	private AlgoSlope algo;

	private boolean isVisible;
	private boolean labelVisible;
	private int xLabelHor;
	private int yLabelHor;
	private String horLabel; // horizontal label, i.e. triangleSize

	private double[] coords = new double[2];
	private GeneralPathClipped gp;
	@Weak
	private Kernel kernel;

	/**
	 * Creates new drawable for slope
	 * 
	 * @param view
	 *            view
	 * @param slope
	 *            slope number
	 */
	public DrawSlope(EuclidianView view, GeoNumeric slope) {
		this.view = view;
		kernel = view.getKernel();
		this.slope = slope;
		geo = slope;

		slope.setDrawable(true);

		// get parent line
		init();
		update();
	}

	private void init() {
		algo = (AlgoSlope) slope.getDrawAlgorithm();

	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (isVisible) {
			if (!geo.getDrawAlgorithm().equals(geo.getParentAlgorithm())) {
				init();
			}
			int slopeTriangleSize = slope.getSlopeTriangleSize();
			double rwHeight = slope.getValue() * slopeTriangleSize;
			double height = view.getYscale() * rwHeight;
			if (Math.abs(height) > Float.MAX_VALUE) {
				isVisible = false;
				return;
			}

			// get point on line g
			algo.getInhomPointOnLine(coords);

			view.toScreenCoords(coords);

			// draw slope triangle
			double x = coords[0];
			double y = coords[1];
			double xright = x + view.getXscale() * slopeTriangleSize;
			if (gp == null) {
				gp = new GeneralPathClipped(view);
			}
			gp.resetWithThickness(geo.getLineThickness());
			gp.moveTo(x, y);
			gp.lineTo(xright, y);
			gp.lineTo(xright, y - height);
			// closePath important for clipping: #4048
			gp.closePath();
			// gp on screen?
			if (!view.intersects(gp.getGeneralPath())) {
				isVisible = false;
				// don't return here to make sure that getBounds() works for
				// offscreen points too
			}

			// label position
			labelVisible = geo.isLabelVisible();
			StringTemplate tpl = StringTemplate.defaultTemplate;
			if (labelVisible) {
				if (slopeTriangleSize > 1) {
					StringBuilder sb = new StringBuilder();
					switch (slope.getLabelMode()) {
					case GeoElementND.LABEL_NAME_VALUE:
						sb.append(slopeTriangleSize);
						sb.append(' ');
						sb.append(geo.getLabel(tpl));
						sb.append(" = ");
						sb.append(kernel.format(rwHeight, tpl));
						break;

					case GeoElementND.LABEL_VALUE:
						sb.append(kernel.format(rwHeight, tpl));
						break;

					default: // case GeoElement.LABEL_NAME:
						sb.append(slopeTriangleSize);
						sb.append(' ');
						sb.append(geo.getLabel(tpl));
						break;
					}
					labelDesc = sb.toString();
				} else {
					labelDesc = geo.getLabelDescription();
				}
				yLabel = (int) (y - height / 2.0f + 6);
				xLabel = (int) xright + 5;
				addLabelOffset();

				// position off horizontal label (i.e. slopeTriangleSize)
				xLabelHor = (int) ((x + xright) / 2.0);
				yLabelHor = (int) (y + view.getFontSize() + 2);
				StringBuilder sb = new StringBuilder();
				sb.append(slopeTriangleSize);
				horLabel = sb.toString();
			}
			updateStrokes(slope);
		}
	}

	@Override
	final public void draw(GGraphics2D g2) {
		if (isVisible) {
			fill(g2, gp.getGeneralPath()); // fill using default/hatching/image as
							// appropriate

			if (isHighlighted()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				gp.draw(g2);
			}

			if (geo.getLineThickness() > 0) {
				g2.setPaint(getObjectColor());
				g2.setStroke(objStroke);
				gp.draw(g2);
			}

			if (labelVisible) {
				g2.setPaint(slope.getLabelColor());
				g2.setFont(view.getFontLine());
				drawLabel(g2);
				view.drawStringWithOutline(g2, horLabel, xLabelHor, yLabelHor,
						geo.getObjectColor());
			}
		}
	}

	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
		return gp != null
				&& (gp.contains(x, y) || gp.intersects(x, y, hitThreshold));
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return gp != null && gp.intersects(rect);
	}

	@Override
	final public boolean isInside(GRectangle rect) {
		return false;
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible() || gp == null) {
			return null;
		}
		return gp.getBounds();
	}

}
