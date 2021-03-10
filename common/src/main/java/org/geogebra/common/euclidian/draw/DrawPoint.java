/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * DrawPoint.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoIntersectAbstract;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;

/**
 * 
 * @author Markus
 * @version 2011-01-10
 */
public final class DrawPoint extends SetDrawable {

	private int HIGHLIGHT_OFFSET;

	// used by getSelectionDiamaterMin()
	private static final int SELECTION_RADIUS_MIN = 12;

	private GeoPointND P;

	private int diameter;
	private int hightlightDiameter;
	private int pointSize;
	private boolean isVisible;
	private boolean labelVisible;
	// for dot and selection
	private GEllipse2DDouble circle = AwtFactory.getPrototype()
			.newEllipse2DDouble();
	private GEllipse2DDouble circleHighlight = AwtFactory.getPrototype()
			.newEllipse2DDouble();

	private GLine2D line1; // for cross
	private GLine2D line2;
	private GLine2D line3; // for diamond
	private GLine2D line4;
	private GGeneralPath gp = null;

	private static GBasicStroke borderStroke = EuclidianStatic
			.getDefaultStroke();
	private static GBasicStroke[] fillStrokes = new GBasicStroke[10];
	private static GBasicStroke[] emptyStrokes = new GBasicStroke[10];

	private boolean isPreview;

	private double[] coords;
	private double[] coords1 = new double[2];

	/**
	 * 
	 * @param threshold
	 *            controller threshold
	 * @return distance threshold to select a point
	 */
	public static int getSelectionThreshold(int threshold) {
		return threshold + SELECTION_RADIUS_MIN;
	}

	/**
	 * Creates new DrawPoint
	 * 
	 * @param view
	 *            view
	 * @param P
	 *            point to be drawn
	 */
	public DrawPoint(EuclidianView view, GeoPointND P) {
		this(view, P, false);
	}

	/**
	 * Creates new DrawPoint
	 * 
	 * @param view
	 *            View
	 * @param P
	 *            point to be drawn
	 * @param isPreview
	 *            true iff preview
	 */
	public DrawPoint(EuclidianView view, GeoPointND P, boolean isPreview) {
		this.view = view;
		this.P = P;
		geo = (GeoElement) P;

		this.isPreview = isPreview;
		this.coords = new double[2];

		// crossStrokes[1] = new BasicStroke(1f);

		update();
	}

	@Override
	public void update() {

		if (gp != null) {
			gp.reset(); // stop trace being left when (filled diamond) point
						// moved
		}

		isVisible = geo.isEuclidianVisible();

		if (isPreview) {
			Coords p = P.getInhomCoordsInD2();
			coords1[0] = p.getX();
			coords1[1] = p.getY();
		} else {
			// looks if it's on view
			Coords p = view.getCoordsForView(P.getInhomCoordsInD3());
			if (!DoubleUtil.isZero(p.getZ())) {
				isVisible = false;
			} else {
				coords1[0] = p.getX();
				coords1[1] = p.getY();
			}
		}

		// trace to spreadsheet is no longer bound to EV
		if (!isVisible) {
			return;
		}

		update(coords1);
	}

	/**
	 * update regarding coords values
	 * 
	 * @param coords2
	 *            (x,y) real world coords
	 */
	public void update(double[] coords2) {
		update(coords2, true);
	}

	private void update(double[] coords2, boolean rwCoords) {

		isVisible = true;
		labelVisible = getTopLevelGeo().isLabelVisible();
		this.coords = coords2;

		if (rwCoords) {
			// convert to screen
			view.toScreenCoords(coords);

			// point outside screen?
			if (Double.isNaN(coords[0]) || Double.isNaN(coords[1])) { // fix for
																		// #63
				isVisible = false;
			} else if (coords[0] > view.getWidth() + P.getPointSize()
					|| coords[0] < -P.getPointSize()
					|| coords[1] > view.getHeight() + P.getPointSize()
					|| coords[1] < -P.getPointSize()) {
				isVisible = false;
				// don't return here to make sure that getBounds() works for
				// offscreen points too
			}
		}

		if (pointSize != P.getPointSize()) {
			updateDiameter();
		}

		double xUL = (coords[0] - pointSize);
		double yUL = (coords[1] - pointSize);

		int pointStyle = P.getPointStyle();

		if (pointStyle == -1) {
			pointStyle = EuclidianStyleConstants.POINT_STYLE_DOT;
		}

		double root3over2;

		switch (pointStyle) {
		case EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND:
			double xR = coords[0] + pointSize;
			double yB = coords[1] + pointSize;

			if (gp == null) {
				gp = AwtFactory.getPrototype().newGeneralPath();
			}
			gp.moveTo((xUL + xR) / 2, yUL);
			gp.lineTo(xUL, (yB + yUL) / 2);
			gp.lineTo((xUL + xR) / 2, yB);
			gp.lineTo(xR, (yB + yUL) / 2);
			gp.closePath();
			break;

		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH:
		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH:

			double direction = 1.0;
			if (pointStyle == EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH) {
				direction = -1.0;
			}

			if (gp == null) {
				gp = AwtFactory.getPrototype().newGeneralPath();
			}
			root3over2 = Math.sqrt(3.0) / 2.0;
			gp.moveTo(coords[0], (coords[1] + direction * pointSize));
			gp.lineTo((coords[0] + pointSize * root3over2),
					(coords[1] - direction * pointSize / 2));
			gp.lineTo((coords[0] - pointSize * root3over2),
					(coords[1] - direction * pointSize / 2));
			gp.lineTo(coords[0], (coords[1] + direction * pointSize));
			gp.closePath();
			break;

		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST:
		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST:

			direction = 1.0;
			if (pointStyle == EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST) {
				direction = -1.0;
			}

			if (gp == null) {
				gp = AwtFactory.getPrototype().newGeneralPath();
			}
			root3over2 = Math.sqrt(3.0) / 2.0;
			gp.moveTo((coords[0] + direction * pointSize), coords[1]);
			gp.lineTo((coords[0] - direction * pointSize / 2),
					(coords[1] + pointSize * root3over2));
			gp.lineTo((coords[0] - direction * pointSize / 2),
					(coords[1] - pointSize * root3over2));
			gp.lineTo((coords[0] + direction * pointSize), coords[1]);
			gp.closePath();
			break;

		case EuclidianStyleConstants.POINT_STYLE_EMPTY_DIAMOND:
			xR = coords[0] + pointSize;
			yB = coords[1] + pointSize;

			if (line1 == null) {
				line1 = AwtFactory.getPrototype().newLine2D();
				line2 = AwtFactory.getPrototype().newLine2D();
			}
			if (line3 == null) {
				line3 = AwtFactory.getPrototype().newLine2D();
				line4 = AwtFactory.getPrototype().newLine2D();
			}
			line1.setLine((xUL + xR) / 2, yUL, xUL, (yB + yUL) / 2);
			line2.setLine(xUL, (yB + yUL) / 2, (xUL + xR) / 2, yB);
			line3.setLine((xUL + xR) / 2, yB, xR, (yB + yUL) / 2);
			line4.setLine(xR, (yB + yUL) / 2, (xUL + xR) / 2, yUL);
			break;

		case EuclidianStyleConstants.POINT_STYLE_PLUS:
			xR = coords[0] + pointSize;
			yB = coords[1] + pointSize;

			if (line1 == null) {
				line1 = AwtFactory.getPrototype().newLine2D();
				line2 = AwtFactory.getPrototype().newLine2D();
			}
			line1.setLine((xUL + xR) / 2, yUL, (xUL + xR) / 2, yB);
			line2.setLine(xUL, (yB + yUL) / 2, xR, (yB + yUL) / 2);
			break;

		case EuclidianStyleConstants.POINT_STYLE_CROSS:
			xR = coords[0] + pointSize;
			yB = coords[1] + pointSize;

			if (line1 == null) {
				line1 = AwtFactory.getPrototype().newLine2D();
				line2 = AwtFactory.getPrototype().newLine2D();
			}
			line1.setLine(xUL, yUL, xR, yB);
			line2.setLine(xUL, yB, xR, yUL);
			break;

		case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
			break;

		// case EuclidianStyleConstants.POINT_STYLE_DOT:
		// default:
		}

		// circle might be needed at least for tracing
		circle.setFrame(xUL, yUL, diameter, diameter);

		// selection area
		circleHighlight.setFrame(xUL - HIGHLIGHT_OFFSET, yUL - HIGHLIGHT_OFFSET,
				hightlightDiameter, hightlightDiameter);

		// draw trace
		if (P.getTrace()) {
			isTracing = true;
			GGraphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null) {
				drawTrace(g2);
			}
		} else {
			if (isTracing) {
				isTracing = false;
			}
		}

		if (isVisible && labelVisible) {
			labelDesc = getTopLevelGeo().getLabelDescription();
			xLabel = (int) Math.round(coords[0] + 4);
			yLabel = (int) Math.round(yUL - pointSize);
			addLabelOffsetEnsureOnScreen(view.getFontPoint());
		}
	}

	private void updateDiameter() {
		pointSize = P.getPointSize();
		diameter = 2 * pointSize;

		HIGHLIGHT_OFFSET = pointSize + 1;

		hightlightDiameter = diameter + 2 * HIGHLIGHT_OFFSET;
	}

	private void drawClippedSection(GeoElement path1, GeoElement path2, GGraphics2D g2) {
		DrawableND drawPath1 = view.newDrawable(path1);
		DrawableND drawPath2 = view.newDrawable(path2);

		if (drawPath1 != null || drawPath2 != null) {
			P.getInhomCoords(coords1);

			view.toScreenCoords(coords1);
			double radius = 30;
			GEllipse2DDouble circleClip = AwtFactory.getPrototype()
					.newEllipse2DDouble(coords1[0] - radius, coords1[1] - radius,
							2 * radius, 2 * radius);
			g2.setClip(circleClip);

			forceDraw(g2, path1, drawPath1);
			forceDraw(g2, path2, drawPath2);
			g2.resetClip();
		}
	}

	private void forceDraw(GGraphics2D g2, GeoElement path, DrawableND drawPath) {
		if (drawPath != null) {
			path.forceEuclidianVisible(true);
			drawPath.update();
			((Drawable) drawPath).draw(g2);
			path.forceEuclidianVisible(false);
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (isVisible) {
			if (isHighlighted()) {
				g2.setPaint(geo.getSelColor());
				g2.fill(circleHighlight);
				g2.setStroke(borderStroke);
				g2.draw(circleHighlight);
			}

			// option "show trimmed intersecting lines"
			if (geo.getShowTrimmedIntersectionLines()) {
				AlgoElement algo = geo.getParentAlgorithm();

				if (algo instanceof AlgoIntersectAbstract) {
					GeoElement[] geos = algo.getInput();
					if (geos.length > 1) {
						drawClippedSection(geos[0], geos[1], g2);
					}
				}
			}

			int pointStyle = P.getPointStyle();

			if (pointStyle == -1) {
				pointStyle = EuclidianStyleConstants.POINT_STYLE_DOT;
			}

			switch (pointStyle) {
			case EuclidianStyleConstants.POINT_STYLE_PLUS:
			case EuclidianStyleConstants.POINT_STYLE_CROSS:
				// draw cross like: X or +
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(getEmptyStroke(pointSize));
				g2.draw(line1);
				g2.draw(line2);
				break;

			case EuclidianStyleConstants.POINT_STYLE_EMPTY_DIAMOND:
				// draw diamond
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(getEmptyStroke(pointSize));
				g2.draw(line1);
				g2.draw(line2);
				g2.draw(line3);
				g2.draw(line4);
				break;

			case EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND:
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH:
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH:
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST:
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST:
				// draw diamond
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(getFillStroke(pointSize));
				g2.draw(gp);
				g2.fill(gp);
				break;

			case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
				// draw a circle
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(getEmptyStroke(pointSize));
				g2.draw(circle);
				break;

			case EuclidianStyleConstants.POINT_STYLE_NO_OUTLINE:
				g2.setPaint(geo.getObjectColor());
				g2.fill(circle);
				break;

			default:
				// draw a dot
				g2.setPaint(geo.getObjectColor());
				g2.fill(circle);

				// black stroke
				g2.setPaint(geo.getShowHideColor(GColor.BLACK));
				g2.setStroke(borderStroke);
				g2.draw(circle);
			}

			// label
			if (labelVisible) {
				g2.setFont(view.getFontPoint());
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
			}
		}
	}

	@Override
	protected void drawTrace(GGraphics2D g2) {
		g2.setPaint(geo.getObjectColor());

		int pointStyle = P.getPointStyle();

		switch (pointStyle) {
		case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
			g2.setStroke(getEmptyStroke(pointSize));
			g2.draw(circle);
			break;

		case EuclidianStyleConstants.POINT_STYLE_CROSS:
		default: // case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
			g2.fill(circle);
		}
	}

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen
	 * coords)
	 */
	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		int threshold = isPreview ? hitThreshold * 2 : hitThreshold;
		int r = Math.max(pointSize,
				getSelectionThreshold(threshold));
		double dx = coords[0] - x;
		double dy = coords[1] - y;
		return dx < r && dx > -r && dx * dx + dy * dy <= r * r;
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return rect.contains(circle.getBounds());
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return circle.intersects(rect);
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	public GRectangle getBounds() {
		// return selection circle's bounding box
		if (!geo.isEuclidianVisible()) {
			return null;
		}

		int selRadius = pointSize + HIGHLIGHT_OFFSET;
		int minRadius = view.getApplication().getCapturingThreshold(
				PointerEventType.MOUSE) + SELECTION_RADIUS_MIN;
		if (selRadius < minRadius) {
			selRadius = minRadius;
		}

		return AwtFactory.getPrototype().newRectangle(
				(int) coords[0] - selRadius, (int) coords[1] - selRadius,
				2 * selRadius, 2 * selRadius);
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	/*
	 * pointSize can be more than 9 (set from JavaScript, SetPointSize[])
	 * CAP_BUTT, JOIN_MITER behaves differently on JRE & GWT see #1699
	 */
	private static GBasicStroke getEmptyStroke(int pointSize) {
		if (pointSize > 9) {
			return AwtFactory.getPrototype()
					.newBasicStrokeJoinMitre(pointSize / 2f);
		}

		if (emptyStrokes[pointSize] == null) {
			emptyStrokes[pointSize] = AwtFactory.getPrototype()
					.newBasicStrokeJoinMitre(pointSize / 2f);
		}

		return emptyStrokes[pointSize];
	}

	/*
	 * pointSize can be more than 9 (set from JavaScript, SetPointSize[])
	 * CAP_BUTT, JOIN_MITER behaves differently on JRE & GWT see #1699
	 */
	private static GBasicStroke getFillStroke(int pointSize) {

		if (pointSize > 9) {
			return AwtFactory.getPrototype().newBasicStroke(pointSize / 2f);
		}

		if (fillStrokes[pointSize] == null) {
			fillStrokes[pointSize] = AwtFactory.getPrototype()
					.newBasicStroke(pointSize / 2f);
		}

		return fillStrokes[pointSize];
	}

	/**
	 * @param pointType
	 *            point style
	 */
	public void setPointStyle(int pointType) {
		if (pointType == this.P.getPointStyle()) {
			return;
		}
		P.setPointStyle(pointType);
		update();
	}

	/**
	 * @return the circle as area
	 */
	public GArea getDot() {
		return AwtFactory.getPrototype().newArea(this.circle);
	}

	/**
	 * Draw a point with given size and style for preview.
	 * 
	 * @param x
	 *            x-coord.
	 * @param y
	 *            y-coord.
	 */
	public void updateStylePreview(double x, double y) {
		if (gp != null) {
			gp.reset();
		}

		double[] coords2 = { x, y };
		update(coords2, false);
	}

	/**
	 * @param isPreview
	 *            whether this is a preview
	 */
	public void setPreview(boolean isPreview) {
		this.isPreview = isPreview;
	}

}
