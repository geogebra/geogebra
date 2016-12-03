package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.euclidian.HatchingHandler;
import org.geogebra.common.kernel.algos.AlgoBarChart;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.FillType;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.debug.Log;

/**
 * Drawable representation of a bar graph
 * 
 */
public class DrawBarGraph extends Drawable {

	/** graph types */
	public enum DrawType {
		/** vertical bars */
		VERTICAL_BAR,
		/** horizontal bars */
		HORIZONTAL_BAR,
		/** step graph */
		STEP_GRAPH_CONTINUOUS,
		/** stick graph */
		STEP_GRAPH_JUMP
	}

	private DrawType drawType = DrawType.VERTICAL_BAR;

	// point types -- given by user, hence not enum
	/** closed point on the right */
	public static final int POINT_RIGHT = 1;
	/** closed point on the right, open on the left */
	public static final int POINT_RIGHT_OPEN_LEFT = 2;
	/** no points */
	public static final int POINT_NONE = 0;
	/** closed point on the left */
	public static final int POINT_LEFT = -1;
	/** closed point on the left, open on the right */
	public static final int POINT_LEFT_OPEN_RIGHT = -2;

	private int pointType = POINT_NONE;

	private boolean isVisible, labelVisible;
	private double[] coords = new double[2];
	/*
	 * Use an array to customize bars
	 */
	private GeneralPathClipped[] gp;
	private GeoNumeric sum;
	private AlgoBarChart algo;
	private ArrayList<GeoPoint> pts;
	private ArrayList<DrawPoint> drawPoints;

	/*************************************************
	 * @param view
	 *            view
	 * @param n
	 *            number (bar chart)
	 */
	public DrawBarGraph(EuclidianView view, GeoNumeric n) {
		this.view = view;
		sum = n;
		geo = n;

		n.setDrawable(true);

		init();
		update();
	}

	private void init() {
		algo = (AlgoBarChart) geo.getDrawAlgorithm();
		drawType = algo.getDrawType();

		if (algo.hasPoints()) {
			pts = new ArrayList<GeoPoint>();
			drawPoints = new ArrayList<DrawPoint>();
			updatePointLists();
		}
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}

		// GRectangle
		// rect=geogebra.common.factories.AwtFactory.getPrototype().newRectangle(
		// (int)(algo.getLeftBorder()[0]),(int)algo.getFreqMax(),
		// (int)(algo.getLeftBorder().length*algo.getWidth()),(int)algo.getFreqMax());
		// return rect;

		GRectangle rect = gp[0].getBounds();
		for (int i = 1; i < gp.length; i++) {
			rect.add(gp[i].getBounds());
		}
		return rect;
	}

	private ArrayList<HatchingHandler> hatchingHandlers = null;

	@Override
	public void draw(GGraphics2D g2) {
		// Save fill, color and alfa of object
		GColor color = geo.getObjectColor();
		GColor selColor = geo.getSelColor();
		FillType fillType = geo.getFillType();
		String fileName = geo.getImageFileName();
		float alpha = geo.getAlphaValue();
		AlgoBarChart algop = (AlgoBarChart) geo.getParentAlgorithm();
		if (isVisible) {
			try {
				if (geo.doHighlighting()) {
					g2.setPaint(sum.getSelColor());
					g2.setStroke(selStroke);
					for (int i = 0; i < gp.length; i++) {
						int k = i + 1;
						if (algop.getBarColor(k) != null) {
							GColor col = algop.getBarColor(k);
							g2.setPaint(GColor.newColor(
									col.getRed(), col.getGreen(),
									col.getBlue(), col.getAlpha()));
						} else {
							g2.setPaint(selColor);
						}
						g2.draw(gp[i]);
					}
					g2.setPaint(selColor);
				}
			} catch (Exception e) {
				Log.debug(e.getMessage());
			}



			try {
				if (algo.getDrawType() != DrawType.STEP_GRAPH_CONTINUOUS) {
					/*
					 * Use tags for draw if there are
					 */
					for (int i = 0; i < gp.length; i++) {
						int k = i + 1;
						if (algop.getBarColor(k) != null) {
							GColor col = algop.getBarColor(k);
							geo.setObjColor(col);
							geo.setAlphaValue(col.getAlpha());
						}

						float barAlpha = algop.getBarAlpha(k);
						if (barAlpha != -1.0) {
							geo.setAlphaValue(barAlpha);
						}

						geo.setFillType(
								algop.getBarFillType(k, geo.getFillType()));

						// if (algop.getBarSymbol(k) != null) {
						// geo.setFillSymbol(algop.getBarSymbol(k));
						// }
						// if (algop.getBarImage(k) != null) {
						// geo.setImageFileName(algop.getBarImage(k));
						// }
						// if (algop.getBarHatchDistance(k) != -1) {
						// geo.setHatchingDistance(algop
						// .getBarHatchDistance(k));
						// }
						// if (algop.getBarHatchAngle(k) != -1) {
						// geo.setHatchingAngle(algop.getBarHatchAngle(k));
						// }

						GPaint gpaint = null;
						GBufferedImage subImage = null;

						if (algop.getBarFillType(k).isHatch()) {

							initHatchingHandlerArray();

							HatchingHandler handler = hatchingHandlers.get(i);

							if (handler == null) {
								handler = new HatchingHandler();
								hatchingHandlers.set(i, handler);

							}

							GColor barColor = algop.getBarColor(k);
							if (barColor == null) {
								barColor = geo.getObjectColor();
							}

							gpaint = handler.setHatching(g2, decoStroke,
									barColor, geo.getBackgroundColor(),
									algop.getBarAlpha(k),
									algop.getBarHatchDistance(k),
									algop.getBarHatchAngle(k),
									algop.getBarFillType(k),
									algop.getBarSymbol(k),
									geo.getKernel().getApplication());

							if (geo.getKernel().getApplication()
									.isHTML5Applet()) {
								// not needed in desktop
								subImage = handler.getSubImage();
							}

						}

						fill(g2, gp[i], gpaint, subImage);

						// appropriate
						// Restore values
						geo.setObjColor(color);
						geo.setFillType(fillType);
						// geo.setHatchingAngle((int) hatchingAngle);
						// geo.setHatchingDistance(hatchingDistance);
						// geo.setFillSymbol(symbol);
						geo.setImageFileName(fileName);
						geo.setAlphaValue(alpha);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if (geo.getLineThickness() > 0) {
					g2.setPaint(getObjectColor());
					g2.setStroke(objStroke);
					for (int i = 0; i < gp.length; i++) {
						int k = i + 1;
						if (algop.getBarColor(k) != null) {
							GColor col = algop.getBarColor(k);
							g2.setPaint(GColor.newColor(
									col.getRed(), col.getGreen(),
									col.getBlue(), geo.getLineOpacity()));
						} else {
							g2.setPaint(color);
						}
						g2.draw(gp[i]);
					}
					g2.setPaint(color);
				}
			} catch (Exception e) {
				Log.debug(e.getMessage());
			}
			if (labelVisible) {
				g2.setFont(view.getFontConic());
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
			}

			// point
			if (algo.hasPoints()) {
				for (int i = 0; i < drawPoints.size(); i++) {
					drawPoints.get(i).draw(g2);
				}
			}
		}
	}

	private void initHatchingHandlerArray() {
		if (hatchingHandlers == null) {
			hatchingHandlers = new ArrayList<HatchingHandler>();

		}

		// fill array, we might not need hatching for all bars
		// but probably will
		if (hatchingHandlers.size() < gp.length) {

			for (int i = hatchingHandlers.size() - 1; i < gp.length; i++) {

				hatchingHandlers.add(null);

			}

		}
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		if (gp != null) {
			for (int i = 0; i < gp.length; i++) {
				if ((gp[i].contains(x, y) || gp[i].intersects(x, y,
						hitThreshold))) {
					setToolTip(i);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		if (gp != null) {
			for (int i = 0; i < gp.length; i++) {
				if (gp[i].intersects(rect)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isInside(GRectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;

	}

	@Override
	public void update() {

		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		if (!geo.getDrawAlgorithm().equals(geo.getParentAlgorithm()))
			init();
		labelVisible = geo.isLabelVisible();
		updateStrokes(sum);

		// init gp
		gp = new GeneralPathClipped[algo.getIntervals()];
		for (int i = 0; i < gp.length; i++) {
			gp[i] = new GeneralPathClipped(view);
			gp[i].reset();
		}

		double[] xVal = algo.getLeftBorder();
		double[] yVal = algo.getValues();

		double width = algo.getWidth();
		int N = algo.getIntervals();

		if (algo.hasPoints()) {
			updatePointLists();
		}

		drawType = algo.getDrawType();
		pointType = algo.getPointType();
		int pointStyle;

		if (algo.hasPoints() && pointType != POINT_NONE) {

			if (pointType == POINT_LEFT || pointType == POINT_LEFT_OPEN_RIGHT) {
				pointStyle = EuclidianStyleConstants.POINT_STYLE_DOT;
			} else {
				pointStyle = EuclidianStyleConstants.POINT_STYLE_CIRCLE;
			}

			for (int i = 0; i < N; i++) {
				coords[0] = xVal[i];
				coords[1] = yVal[i];
				pts.get(i).setCoords(coords[0], coords[1], 1.0);
				pts.get(i).setObjColor(geo.getObjectColor());
				pts.get(i).setPointSize(2 + (geo.getLineThickness() + 1) / 3);
				pts.get(i).setPointStyle(pointStyle);
				if (pointType == POINT_RIGHT) {
					pts.get(i).setEuclidianVisible(false);
				}
				drawPoints.get(i).update();
			}

			if (drawType == DrawType.STEP_GRAPH_CONTINUOUS
					|| drawType == DrawType.STEP_GRAPH_JUMP) {

				if (pointType == POINT_LEFT
						|| pointType == POINT_LEFT_OPEN_RIGHT) {
					pointStyle = EuclidianStyleConstants.POINT_STYLE_CIRCLE;
				} else {
					pointStyle = EuclidianStyleConstants.POINT_STYLE_DOT;
				}

				// step graph right points
				for (int i = 0; i < N - 1; i++) {
					coords[0] = xVal[i + 1];
					coords[1] = yVal[i];
					pts.get(N + i).setCoords(coords[0], coords[1], 1.0);
					pts.get(N + i).setObjColor(geo.getObjectColor());
					pts.get(N + i)
							.setPointSize(2 + (geo.getLineThickness() + 1) / 3);
					pts.get(N + i).setPointStyle(pointStyle);
					if (pointType == POINT_LEFT) {
						pts.get(N + i).setEuclidianVisible(false);
					}
					drawPoints.get(N + i).update();
				}
			}

		}

		double halfWidth = width / 2;

		switch (drawType) {

		case VERTICAL_BAR:

			if (width <= 0) {
				for (int i = 0; i < N; i++) {
					coords[0] = xVal[i];
					coords[1] = 0;
					view.toScreenCoords(coords);
					gp[i].moveTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);
				}

			} else {
				for (int i = 0; i < N; i++) {
					coords[0] = xVal[i];
					coords[1] = 0;
					view.toScreenCoords(coords);
					gp[i].moveTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);

					coords[0] = xVal[i] + width;
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);

					coords[0] = xVal[i] + width;
					coords[1] = 0;
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = 0;
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);
				}
			}

			break;

		case HORIZONTAL_BAR:

			if (width <= 0) {
				for (int i = 0; i < N; i++) {
					coords[0] = 0;
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp[i].moveTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);
				}

			} else {
				for (int i = 0; i < N; i++) {
					coords[0] = 0;
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp[i].moveTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i] + width;
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);

					coords[0] = 0;
					coords[1] = yVal[i] + width;
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);

					coords[0] = 0;
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);
				}
			}
			break;

		case STEP_GRAPH_CONTINUOUS:

			for (int i = 0; i < N - 1; i++) {

				// move to start point
				coords[0] = xVal[i] + halfWidth;
				coords[1] = yVal[i];
				view.toScreenCoords(coords);
				gp[i].moveTo(coords[0], coords[1]);

				// across
				coords[0] = xVal[i + 1] + halfWidth;
				coords[1] = yVal[i];
				view.toScreenCoords(coords);
				gp[i].lineTo(coords[0], coords[1]);

				// up
				coords[0] = xVal[i + 1] + halfWidth;
				coords[1] = yVal[i + 1];
				view.toScreenCoords(coords);
				gp[i].lineTo(coords[0], coords[1]);
			}

			// up to last point
			coords[0] = xVal[N - 1] + halfWidth;
			coords[1] = yVal[N - 1];
			view.toScreenCoords(coords);
			gp[gp.length - 1].lineTo(coords[0], coords[1]);

			break;

		case STEP_GRAPH_JUMP:

			for (int i = 0; i < N - 1; i++) {

				// move to start point
				coords[0] = xVal[i] + halfWidth;
				coords[1] = yVal[i];
				view.toScreenCoords(coords);
				gp[i].moveTo(coords[0], coords[1]);

				// across
				coords[0] = xVal[i + 1] + halfWidth;
				coords[1] = yVal[i];
				view.toScreenCoords(coords);
				gp[i].lineTo(coords[0], coords[1]);

			}

			break;
		}

		// gp on screen?
		isVisible = false;
		// don't return here to make sure that getBounds() works for
		// off screen points too
		for (int i = 0; i < gp.length; i++) {
			if (gp[i].intersects(0, 0, view.getWidth(), view.getHeight())) {
				isVisible = true;
				break;
			}
		}
		// TODO: improve label position
		if (labelVisible) {
			xLabel = (int) coords[0];
			yLabel = (int) coords[1] - view.getFontSize();
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}

	}

	private void updatePointLists() {

		// find the number of points to draw
		int n;
		if (drawType == DrawType.STEP_GRAPH_CONTINUOUS
				|| drawType == DrawType.STEP_GRAPH_JUMP) {
			n = 2 * algo.getIntervals() - 1;
		} else {
			n = algo.getIntervals();
		}

		// adjust the lists
		if (n > pts.size()) {
			// add
			for (int i = pts.size(); i < n; i++) {
				addPt();
			}
		} else if (n < pts.size()) {
			// remove
			// for (int i = n; n < pts.size(); i++) {
			// remove in reverse order!
			for (int i = pts.size() - 1; i >= n; i--) {
				pts.remove(i);
				drawPoints.remove(i);
			}
		}
	}

	private void addPt() {

		GeoPoint p = new GeoPoint(view.getKernel().getConstruction());
		p.setLabelVisible(false);
		DrawPoint d = new DrawPoint(view, p);
		d.setGeoElement(p);

		pts.add(p);
		drawPoints.add(d);
	}

	private void setToolTip(int barIndex) {
		algo.setToolTipText(barIndex);
		view.setToolTipText(null);
	}

}
