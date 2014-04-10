/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General private License as published by 
the Free Software Foundation.

 */

/*
 * DrawPoint.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package geogebra.common.euclidian.draw;

import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.event.PointerEventType;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 * 
 * @author Markus
 * @version 2011-01-10
 */
public final class DrawPointSlider {

	private int HIGHLIGHT_OFFSET;

	// used by getSelectionDiamaterMin()
	private static final int SELECTION_RADIUS_MIN = 12;

	private GeoPointND P;

	private int diameter, hightlightDiameter, pointSize;
	private boolean isVisible, labelVisible;
	// for dot and selection
	private geogebra.common.awt.GEllipse2DDouble circle = geogebra.common.factories.AwtFactory.prototype.newEllipse2DDouble();
	private geogebra.common.awt.GEllipse2DDouble circleHighlight = geogebra.common.factories.AwtFactory.prototype.newEllipse2DDouble();
	
	private geogebra.common.awt.GGeneralPath gp = null;

	private static geogebra.common.awt.GBasicStroke borderStroke = EuclidianStatic
			.getDefaultStroke();
	
	private static geogebra.common.awt.GBasicStroke[] emptyStrokes = new geogebra.common.awt.GBasicStroke[10];


	private double[] coords;

	private EuclidianView view;

	private GeoElement geo;

	/**
	 * Creates new DrawPoint
	 * 
	 * @param view View
	 * @param P point to be drawn
	 */
	public DrawPointSlider(EuclidianView view, GeoPointND P, GeoElement geo, Drawable drawable) {
		this.view = view;
		this.P = P;
		this.geo = geo;
		this.drawable = drawable;

		this.coords = new double[2];

		// crossStrokes[1] = new BasicStroke(1f);

		update();
	}
	
		


	
	final void update() {

		if (gp != null)
			gp.reset(); // stop trace being left when (filled diamond) point
						// moved

		isVisible = geo.isEuclidianVisible();

		double[] coords1 = new double[2];
		
			// looks if it's on view
			Coords p = view.getCoordsForView(P.getInhomCoordsInD(3));
			if (!Kernel.isZero(p.getZ())) {
				isVisible = false;
			} else {
				coords1[0] = p.getX();
				coords1[1] = p.getY();
			}
		
		
		// trace to spreadsheet is no longer bound to EV
		if (!isVisible)
			return;

		
		update(coords1);
	}
	
	/**
	 * update regarding coords values
	 * @param coords1 (x,y) real world coords
	 */
	final private void update(double[] coords1){

		isVisible = true;
		labelVisible = geo.isLabelVisible();
		this.coords = coords1;

		// convert to screen
		view.toScreenCoords(coords);

		// point outside screen?
		if (Double.isNaN(coords[0]) || Double.isNaN(coords[1])) { // fix for #63
			isVisible = false;
		} else if (coords[0] > view.getWidth() + P.getPointSize()
				|| coords[0] < -P.getPointSize()
				|| coords[1] > view.getHeight() + P.getPointSize()
				|| coords[1] < -P.getPointSize()) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// offscreen points too
		}

		if (pointSize != P.getPointSize()) {
			updateDiameter();
		}

		double xUL = (coords[0] - pointSize);
		double yUL = (coords[1] - pointSize);
		
		

		// Florian Sonner 2008-07-17
		
		// circle might be needed at least for tracing
		circle.setFrame(xUL, yUL, diameter, diameter);

		// selection area
		circleHighlight.setFrame(xUL - HIGHLIGHT_OFFSET,
				yUL - HIGHLIGHT_OFFSET, hightlightDiameter, hightlightDiameter);



		// draw trace
		

		if (isVisible && labelVisible) {
			drawable.labelDesc = geo.getLabelDescription();
			drawable.xLabel = (int) Math.round(coords[0] + 4);
			drawable.yLabel = (int) Math.round(yUL - pointSize);
			drawable.addLabelOffsetEnsureOnScreen();
		}
	}





	private void updateDiameter() {
		pointSize = P.getPointSize();
		diameter = 2 * pointSize;
		HIGHLIGHT_OFFSET = pointSize / 2 + 1;
		// HIGHLIGHT_OFFSET = pointSize / 2 + 1;
		hightlightDiameter = diameter + 2 * HIGHLIGHT_OFFSET;
	}

	private final Drawable drawable;

	

	
	final void draw(geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {
			if (geo.doHighlighting()) {
				g2.setPaint(geo.getSelColor());
				g2.fill(circleHighlight);
				g2.setStroke(borderStroke);
				g2.draw(circleHighlight);
			}


			
			
				// draw a dot
				g2.setPaint(geo.getObjectColor());
				g2.fill(circle);

				// black stroke
				g2.setPaint(geogebra.common.awt.GColor.black);
				g2.setStroke(borderStroke);
				g2.draw(circle);
			

			// label
			if (labelVisible) {
				g2.setFont(view.getFontPoint());
				g2.setPaint(geo.getLabelColor());
				drawable.drawLabel(g2);
			}
		}
	}

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen
	 * coords)
	 */
	
	final boolean hit(int x, int y, int hitThreshold) {
		int r = hitThreshold + SELECTION_RADIUS_MIN;
		double dx = coords[0] - x;
		double dy = coords[1] - y;
		return dx < r && dx > -r && dx*dx + dy*dy <= r * r;
	}

	
	final boolean isInside(geogebra.common.awt.GRectangle rect) {
		return rect.contains(circle.getBounds());
	}
	
	
	boolean intersectsRectangle(GRectangle rect){
		return circle.intersects(rect);
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	
	final private geogebra.common.awt.GRectangle getBounds() {
		// return selection circle's bounding box
		if (!geo.isEuclidianVisible()) {
			return null;
		}

		int selRadius = pointSize + HIGHLIGHT_OFFSET;
		int minRadius = view.getApplication().getCapturingThreshold(PointerEventType.MOUSE) + SELECTION_RADIUS_MIN;
		if (selRadius < minRadius){
			selRadius = minRadius;
		}

		return AwtFactory.prototype.newRectangle((int)coords[0] - selRadius, (int)coords[1] - selRadius,
				2 * selRadius, 2 * selRadius);
	}


	/*
	 * pointSize can be more than 9 (set from JavaScript, SetPointSize[])
	 * CAP_BUTT, JOIN_MITER behaves differently on JRE & GWT
	 * see #1699
	 */
	final private static geogebra.common.awt.GBasicStroke getEmptyStroke(int pointSize) {
		if (pointSize > 9)
			return AwtFactory.prototype.newBasicStrokeJoinMitre(pointSize / 2f);

		if (emptyStrokes[pointSize] == null)
			emptyStrokes[pointSize] = AwtFactory.prototype.newBasicStrokeJoinMitre(pointSize / 2f);

		return emptyStrokes[pointSize];
	}

}
