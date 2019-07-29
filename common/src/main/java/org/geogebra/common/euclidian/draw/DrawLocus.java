/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoLocusND;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.prover.AlgoEnvelope;
import org.geogebra.common.kernel.prover.AlgoLocusEquation;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.debug.Log;

/**
 * Drawable representation of locus
 *
 */
public class DrawLocus extends Drawable {
	private static final int BITMAP_PADDING = 10;

	private GeoLocusND<? extends MyPoint> locus;

	private boolean isVisible;
	private boolean labelVisible;
	private GeneralPathClippedForCurvePlotter gp;
	private double[] labelPosition;
	private CoordSys transformSys;
	private BoundingBox boundingBox;
	private GBufferedImage bitmap;
	private AlgoElement algo;

	private int bitmapShiftX;
	private int bitmapShiftY;

	/**
	 * Creates new drawable for given locus
	 * 
	 * @param view
	 *            view
	 * @param locus
	 *            locus
	 * @param transformSys
	 *            coord system of trnsformed locus
	 */
	public DrawLocus(EuclidianView view, GeoLocusND<? extends MyPoint> locus,
			CoordSys transformSys) {
		this.view = view;
		this.locus = locus;
		geo = locus;
		this.transformSys = transformSys;
		update();
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		bitmap = null;
		if (!isVisible) {
			return;
		}

		algo = geo.getParentAlgorithm();
		if (algo instanceof AlgoLocusEquation) {
			AlgoLocusEquation ale = (AlgoLocusEquation) geo
					.getParentAlgorithm();
			if (ale.resetFingerprint(geo.getKernel(), false)) {
				ale.update();
			}
		}
		if (algo instanceof AlgoEnvelope) {
			AlgoEnvelope ae = (AlgoEnvelope) geo.getParentAlgorithm();
			if (ae.resetFingerprint(geo.getKernel(), false)) {
				ae.update();
			}
		}

		buildGeneralPath(locus.getPoints());

		// line on screen?
		if (!geo.isInverseFill()
				&& !view.intersects(gp)) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// offscreen points too
		}
		updateStrokes(geo);

		labelVisible = geo.isLabelVisible();
		if (labelVisible) {
			labelDesc = geo.getLabelDescription();
			double xmin = view.getXmin();
			double xmax = view.getXmax();
			double ymin = view.getYmin();
			double ymax = view.getYmax();
			double x = labelPosition[0];
			double y = labelPosition[1];
			double width = view.getWidth();
			double height = view.getHeight();
			xLabel = (int) ((x - xmin) / (xmax - xmin) * width) + 5;
			yLabel = (int) (height - (y - ymin) / (ymax - ymin) * height) + 4
					+ view.getFontSize();
			/*
			 * Adding (5,4) will hopefully move the label out of the curve's
			 * direct hiding. This is just a hack, and it does not work always.
			 */
			addLabelOffsetEnsureOnScreen(1.0, 1.0, view.getFontLine());
		}

		// draw trace
		if (geo.isTraceable() && (geo instanceof Traceable)
				&& ((Traceable) geo).getTrace()) {
			isTracing = true;
			GGraphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null) {
				drawTrace(g2);
			}
		} else {
			if (isTracing) {
				isTracing = false;
				// view.updateBackground();
			}
		}
		if (geo.isInverseFill()) {
			setShape(AwtFactory.getPrototype().newArea(view.getBoundingPath()));
			getShape().subtract(AwtFactory.getPrototype().newArea(gp));
		}

		if (geo.getKernel().getApplication().isWhiteboardActive()
				&& geo.getGeoClassType() == GeoClass.PENSTROKE
				&& getBounds() != null) {
			getBoundingBox().setRectangle(getBounds2D());
		}
	}

	@Override
	protected final void drawTrace(GGraphics2D g2) {
		drawLocus(g2);
	}

	private void drawLocus(GGraphics2D g2) {
		if (isVisible) {

			if (geo.isPenStroke()
					&& !geo.getKernel().getApplication().isExporting()) {
				if (bitmap == null) {
					this.bitmap = makeImage(g2);
					GGraphics2D g2bmp = bitmap.createGraphics();
					g2bmp.setAntialiasing();
					bitmapShiftX = (int) getBounds().getMinX() - BITMAP_PADDING;
					bitmapShiftY = (int) getBounds().getMinY() - BITMAP_PADDING;
					g2bmp.translate(-bitmapShiftX, -bitmapShiftY);
					drawPath(g2bmp);
				}
				g2.drawImage(bitmap, bitmapShiftX, bitmapShiftY);
			} else {
				drawPath(g2);
			}

			if (geo.isFillable() && geo.isFilled()) {

				// fill using default/hatching/image as appropriate
				fill(g2, (geo.isInverseFill() ? getShape() : gp));
			}
		}
	}

	private void drawPath(GGraphics2D g2) {
		g2.setPaint(getObjectColor());
		g2.setStroke(objStroke);
		g2.draw(gp);
	}

	private GBufferedImage makeImage(GGraphics2D g2p) {
		return AwtFactory.getPrototype().newBufferedImage(
				(int) this.getBounds().getWidth() + 2 * BITMAP_PADDING,
				(int) this.getBounds().getHeight() + 2 * BITMAP_PADDING, g2p);
	}

	private void buildGeneralPath(ArrayList<? extends MyPoint> pointList) {
		if (gp == null) {
			gp = new GeneralPathClippedForCurvePlotter(view);
		} else {
			gp.reset();
		}

		// Use the last plotted point for positioning the label:
		labelPosition = CurvePlotter.draw(gp, pointList, transformSys);
		/*
		 * Due to numerical instability of the curve plotter algorithm this
		 * position may be changing too quickly which results in an annoying
		 * vibration of the label. To avoid this, we prefer to find the
		 * bottom-left position of the curve, that is, for which the sum of
		 * coordinates is minimal.
		 */
		int plSize = pointList.size();
		for (int i = 0; i < plSize; ++i) {
			double px = ((MyPoint) pointList.get(i)).x;
			double py = ((MyPoint) pointList.get(i)).y;
			if (px + py < labelPosition[0] + labelPosition[1]) {
				labelPosition[0] = px;
				labelPosition[1] = py;
			}
		}
	}

	@Override
	final public void draw(GGraphics2D g2) {
		if (isVisible) {
			if (isHighlighted()) {
				// draw locus
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				g2.draw(gp);
			}

			// draw locus
			drawLocus(g2);

			// label
			if (labelVisible) {
				g2.setFont(view.getFontLine());
				g2.setColor(geo.getLabelColor());
				drawLabel(g2);
			}
		}
	}

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen
	 * coords)
	 */
	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		GShape t = geo.isInverseFill() ? getShape() : gp;
		if (t == null) {
			return false; // hasn't been drawn yet (hidden)
		}

		if (geo.isFilled()) {
			return t.intersects(x - hitThreshold, y - hitThreshold,
					2 * hitThreshold, 2 * hitThreshold);
		}
		if (!isVisible || objStroke.getLineWidth() <= 0) {
			return false;
		}
		if (strokedShape == null) {
			// AND-547, initial buffer size
			try {
				strokedShape = objStroke.createStrokedShape(gp, 2500);
			} catch (Exception e) {
				Log.error("problem creating Locus shape: " + e.getMessage());
				return false;
			}
		}
		return strokedShape.intersects(x - hitThreshold, y - hitThreshold,
				2 * hitThreshold, 2 * hitThreshold);

		/*
		 * return gp.intersects(x-2,y-2,4,4) && !gp.contains(x-2,y-2,4,4);
		 */
	}

	@Override
	final public boolean isInside(GRectangle rect) {
		return rect.contains(gp.getBounds());
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return gp.intersects(rect);
	}

	@Override
	final public GeoElement getGeoElement() {
		return geo;
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		if (!geo.isDefined()
				|| (!locus.isClosedPath() && geo.getGeoClassType() != GeoClass.PENSTROKE)
				|| !geo.isEuclidianVisible() || gp == null) {
			return null;
		}
		return gp.getBounds();
	}

	private GRectangle2D getBounds2D() {
		if (!geo.isDefined() || !geo.isEuclidianVisible() || gp == null) {
			return null;
		}
		return gp.getBounds2D();
	}

	@Override
	public void updateByBoundingBoxResize(GPoint2D p,
			EuclidianBoundingBoxHandler handler) {
		if (!geo.getKernel().getApplication().isWhiteboardActive()) {
			return;
		}
		updateLocus(handler, p);
	}

	@Override
	public void updateGeo(GPoint2D p) {
		((GeoLocus) geo).resetSavedBoundingBoxValues(false);
	}

	/**
	 * update locus by dragging side handler
	 * 
	 * @param handler
	 *            - handler was hit
	 * @param p
	 *            - mouse position
	 */
	private void updateLocus(EuclidianBoundingBoxHandler handler,
			GPoint2D p) {
		updatePoints(handler, p,
				getBoundingBox().getRectangle());
		update();
		getBoundingBox().setRectangle(getBounds2D());
	}

	/**
	 * Updates the points when resizing the locus with bounding box handler
	 * 
	 * @param handler
	 *            handler was hit
	 * @param p
	 *            mouse position
	 * @param gRectangle2D
	 *            bounding box rectangle
	 */
	public void updatePoints(EuclidianBoundingBoxHandler handler,
			GPoint2D p, GRectangle2D gRectangle2D) {
		// save the original rates when scaling first time
		((GeoLocus) geo).saveOriginalRates(gRectangle2D);

		switch (handler) {
		case TOP:
		case BOTTOM:
			((GeoLocus) geo).updatePointsY(handler, p.getY(), gRectangle2D,
					Double.NaN);
			break;
		case LEFT:
		case RIGHT:
			((GeoLocus) geo).updatePointsX(handler, p.getX(), gRectangle2D);
			break;
		case TOP_LEFT:
		case BOTTOM_LEFT:
		case TOP_RIGHT:
		case BOTTOM_RIGHT:
			((GeoLocus) geo).saveRatio(gRectangle2D);
			double newWidth = ((GeoLocus) geo).updatePointsX(handler,
					p.getX(),
					gRectangle2D);
			((GeoLocus) geo).updatePointsY(handler, p.getY(), gRectangle2D,
					newWidth);
			break;
		default: // UNDEFINED - maybe not possible
			Log.warn("unhandled case");
		}
	}

	@Override
	public GRectangle getBoundsForStylebarPosition() {
		if (gp == null) {
			return null;
		}
		return gp.getBounds();
	}

	@Override
	public BoundingBox getBoundingBox() {
		if (geo.getKernel().getApplication().isWhiteboardActive()) {
			if (boundingBox == null) {
				boundingBox = createBoundingBox(false, false);
			}
			return boundingBox;
		}
		return null;
	}

}
