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

import java.util.ArrayList;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.CurvePlotterUtils;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoLocusND;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.prover.AlgoEnvelope;
import org.geogebra.common.kernel.prover.AlgoLocusEquation;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.debug.Log;

/**
 * Drawable representation of locus
 */
public class DrawLocus extends Drawable {
	private static final int BITMAP_PADDING = 20;

	private GeoLocusND<? extends MyPoint> locus;

	private boolean isVisible;
	private boolean labelVisible;
	private GeneralPathClippedForCurvePlotter gp;
	private double[] labelPosition;
	private CoordSys transformSys;

	private GBufferedImage bitmap;

	private int bitmapShiftX;
	private int bitmapShiftY;

	private GRectangle partialHitClip;

	/**
	 * Creates new drawable for given locus
	 * @param view view
	 * @param locus locus
	 * @param transformSys coord system of transformed locus
	 */
	public DrawLocus(EuclidianView view, GeoLocusND<? extends MyPoint> locus,
			CoordSys transformSys) {
		this.view = view;
		this.locus = locus;
		geo = locus;
		this.transformSys = transformSys;
	}

	@Override
	public void update() {
		isVisible = geo.isEuclidianVisible();
		bitmap = null;
		if (!isVisible) {
			return;
		}

		updateAlgos();

		buildGeneralPath(locus.getPoints());
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
			yLabel = (int) (height - (y - ymin) / (ymax - ymin) * height) + 4 + view.getFontSize();
			/*
			 * Adding (5,4) will hopefully move the label out of the curve's
			 * direct hiding. This is just a hack, and it does not work always.
			 */
			addLabelOffsetEnsureOnScreen(1.0, 1.0, view.getFontLine());
		}

		drawAndUpdateTraceIfNeeded(geo.isTraceable()
				&& (geo instanceof Traceable) && ((Traceable) geo).getTrace());
		if (geo.isInverseFill()) {
			setShape(view.getBoundsArea());
			getShape().subtract(AwtFactory.getPrototype().newArea(gp.getGeneralPath()));
		}
	}

	protected void updateAlgos() {
		AlgoElement algo = geo.getParentAlgorithm();
		if (algo instanceof AlgoLocusEquation) {
			AlgoLocusEquation ale = (AlgoLocusEquation) geo.getParentAlgorithm();
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
	}

	@Override
	protected final void drawTrace(GGraphics2D g2) {
		drawLocus(g2);
	}

	protected void drawLocus(GGraphics2D g2) {
		if (!isVisible) {
			return;
		}
		GRectangle bounds = getBounds();
		GRectangle viewBounds = view.getFrame();

		if (geo.isPenStroke() && !geo.getKernel().getApplication().isExporting()) {
			if (bitmap == null) {
				GRectangle bitmapBounds = getBitmapBounds(bounds, viewBounds);
				if (bitmapBounds.getWidth() <= 0 || bitmapBounds.getHeight() <= 0) {
					return;
				}
				bitmap = makeImage(g2, bitmapBounds);
				bitmapShiftX = (int) bitmapBounds.getMinX() - BITMAP_PADDING;
				bitmapShiftY = (int) bitmapBounds.getMinY() - BITMAP_PADDING;

				GGraphics2D graphics = bitmap.createGraphics();
				graphics.setAntialiasing();
				graphics.translate(-bitmapShiftX, -bitmapShiftY);
				drawPath(graphics, gp);
			}
			g2.drawImage(bitmap, bitmapShiftX, bitmapShiftY);
		} else {
			drawPath(g2, gp);
		}

		if (geo.isFillable() && geo.isFilled()) {
			// fill using default/hatching/image as appropriate
			fill(g2, geo.isInverseFill() ? getShape() : getGeneralPath());
		}
	}

	protected void drawPath(GGraphics2D g2, GeneralPathClippedForCurvePlotter gp) {
		g2.setPaint(getObjectColor());
		g2.setStroke(objStroke);
		gp.draw(g2);
	}

	private GBufferedImage makeImage(GGraphics2D g2p, GRectangle bounds) {
		return AwtFactory.getPrototype().newBufferedImage(
				(int) bounds.getWidth(), (int) bounds.getHeight(), g2p);
	}

	private GRectangle getBitmapBounds(GRectangle bounds, GRectangle viewBounds) {
		GRectangle2D rectangle = bounds.createIntersection(viewBounds);
		return AwtFactory.getPrototype().newRectangle(
				(int) rectangle.getX(), (int) rectangle.getY(),
				(int) rectangle.getWidth() + 2 * BITMAP_PADDING,
				(int) rectangle.getHeight() + 2 * BITMAP_PADDING);
	}

	private void buildGeneralPath(ArrayList<? extends MyPoint> pointList) {
		lazyCreateGeneralPath();
		// Use the last plotted point for positioning the label:
		setLabelPosition(pointList);
	}

	protected void setLabelPosition(ArrayList<? extends MyPoint> pointList) {
		labelPosition = CurvePlotterUtils.draw(gp, pointList, transformSys);
		/*
		 * Due to numerical instability of the curve plotter algorithm this
		 * position may be changing too quickly which results in an annoying
		 * vibration of the label. To avoid this, we prefer to find the
		 * bottom-left position of the curve, that is, for which the sum of
		 * coordinates is minimal.
		 */
		int plSize = pointList.size();
		for (int i = 0; i < plSize; ++i) {
			double px = pointList.get(i).x;
			double py = pointList.get(i).y;
			if (px + py < labelPosition[0] + labelPosition[1]) {
				labelPosition[0] = px;
				labelPosition[1] = py;
			}
		}
	}

	protected void lazyCreateGeneralPath() {
		if (gp == null) {
			gp = newGeneralPath();
		}
		gp.resetWithThickness(geo.getLineThickness());
	}

	protected GeneralPathClippedForCurvePlotter newGeneralPath() {
		return new GeneralPathClippedForCurvePlotter(view);
	}

	@Override
	final public void draw(GGraphics2D g2) {
		if (isVisible) {
			if (isHighlighted()) {
				drawHighlighted(g2);
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

	@Override
	public void drawStroke(GGraphics2D g2) {
		drawStrokedPath(g2, gp);
	}

	void drawStrokedPath(GGraphics2D g2, GeneralPathClippedForCurvePlotter gp) {
		if (partialHitClip != null) {
			g2.setClip(partialHitClip, true);
			gp.draw(g2);
			g2.resetClip();
		} else {
			gp.draw(g2);
		}
	}

	protected void drawHighlighted(GGraphics2D g2) {
		g2.setPaint(geo.getSelColor());
		g2.setStroke(selStroke);
		drawStroke(g2);
	}

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen
	 * coords)
	 */
	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		GShape t = geo.isInverseFill() ? getShape() : getGeneralPath();
		if (t == null) {
			return false; // hasn't been drawn yet (hidden)
		}

		if (geo.isFilled()) {
			return t.intersects(x - hitThreshold, y - hitThreshold, 2 * hitThreshold,
					2 * hitThreshold);
		}
		if (!isVisible || objStroke.getLineWidth() <= 0) {
			return false;
		}
		updateStrokedShape();
		return strokedShape != null && strokedShape.intersects(x - hitThreshold, y - hitThreshold,
				2 * hitThreshold, 2 * hitThreshold);
	}

	private GShape getGeneralPath() {
		return gp == null ? null : gp.getGeneralPath();
	}

	private void updateStrokedShape() {
		if (strokedShape == null) {
			// AND-547, initial buffer size
			try {
				strokedShape = objStroke.createStrokedShape(getGeneralPath(), 2500);
			} catch (Exception e) {
				Log.error("problem creating Locus shape: " + e.getMessage());
			}
		}
	}

	@Override
	final public boolean isInside(GRectangle rect) {
		return rect.contains(gp.getBounds());
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		updateStrokedShape();
		return strokedShape != null && strokedShape.intersects(rect);
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

	@Override
	public GRectangle getBoundsClipped() {
		if (this.partialHitClip != null) {
			return gp.getBounds().createIntersection(partialHitClip).getBounds();
		}
		return getBounds();
	}

	@Override
	public GRectangle getPartialHitClip() {
		return partialHitClip;
	}

	@Override
	public GRectangle getBoundsForStylebarPosition() {
		return getBoundsClipped();
	}

	@Override
	public void setPartialHitClip(GRectangle rect) {
		this.partialHitClip = rect;
	}

	@Override
	public boolean resetPartialHitClip(int x, int y) {
		if (partialHitClip != null && !partialHitClip.contains(x, y)) {
			partialHitClip = null;
			return geo.isSelected();
		}
		return false;
	}

	@Override
	public ArrayList<GPoint2D> toPoints() {
		ArrayList<GPoint2D> points = new ArrayList<>();
		for (MyPoint pt : locus.getPoints()) {
			points.add(
					new MyPoint(view.toScreenCoordXd(pt.getX()), view.toScreenCoordYd(pt.getY())));
		}
		return points;
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> points) {
		int i = 0;
		for (MyPoint pt : locus.getPoints()) {
			pt.setLocation(view.toRealWorldCoordX(points.get(i).getX()),
					view.toRealWorldCoordY(points.get(i).getY()));
			i++;
		}
		if (locus instanceof GeoLocusStroke) {
			((GeoLocusStroke) locus).resetXMLPointBuilder();
		}
	}

	public GeneralPathClippedForCurvePlotter getPath() {
		return gp;
	}
}
