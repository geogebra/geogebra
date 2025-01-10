package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.statistics.AlgoDotPlot;

/**
 * Drawable representation of a point plot
 * 
 */
public class DrawPointPlot extends Drawable {

	/** graph types */
	public enum DrawType {
		/** vertical bars */
		DOT_PLOT,
		/** horizontal bars */
		POINT_PLOT
	}

	private DrawType drawType = DrawType.DOT_PLOT;

	private boolean isVisible;
	private boolean labelVisible;
	private double[] coords = new double[2];

	private AlgoDotPlot algo;
	private ArrayList<DrawPoint> drawPoints;

	private int pointStyle;
	private int pointSize;
	private int oldPointSize = -1;
	private int oldPointStyle = -1;
	private GColor oldColor = null;
	private GColor pointColor;

	private double scaleFactor = 1;
	private GeoList pointList;

	/*************************************************
	 * @param view
	 *            view
	 * @param pointList
	 *            list of GeoPoints to plot
	 * @param drawType
	 *            type
	 */
	public DrawPointPlot(EuclidianView view, GeoList pointList,
			DrawType drawType) {
		this.view = view;
		this.drawType = drawType;
		geo = pointList;
		this.pointList = pointList;
		init();
		update();
	}

	private void init() {
		algo = (AlgoDotPlot) geo.getParentAlgorithm();
		drawPoints = new ArrayList<>();
		updatePointLists();
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()
				|| drawPoints == null) {
			return null;
		}

		GRectangle rect = drawPoints.get(0).getBounds();
		for (int i = 1; i < drawPoints.size(); i++) {
			rect.add(drawPoints.get(i).getBounds());
		}
		return rect;
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (isVisible) {

			for (int i = 0; i < drawPoints.size()
					&& i < pointList.size(); i++) {
				pointList.get(i).setHighlighted(isHighlighted());
				drawPoints.get(i).draw(g2);
			}

			if (labelVisible) {
				g2.setFont(view.getFontConic());
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
			}
		}
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {

		for (int i = 0; i < drawPoints.size(); i++) {
			if (drawPoints.get(i).hit(x, y, hitThreshold)) {
				setToolTipForPoint(i);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {

		for (int i = 0; i < drawPoints.size(); i++) {
			Drawable d = drawPoints.get(i);
			if (d.intersectsRectangle(rect)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isInside(GRectangle rect) {

		int size = drawPoints.size();
		for (int i = 0; i < size; i++) {
			Drawable d = drawPoints.get(i);
			if (!d.isInside(rect)) {
				return false;
			}
		}
		return size > 0;
	}

	@Override
	public void update() {

		isVisible = geo.isEuclidianVisible();
		if (!isVisible) {
			return;
		}
		if (!geo.getDrawAlgorithm().equals(geo.getParentAlgorithm())) {
			init();
		}
		labelVisible = geo.isLabelVisible();

		// add or remove point drawables
		updatePointLists();

		// adjust point coordinates for a density plot
		if (drawType == DrawType.DOT_PLOT && algo.stackAdjacentDots()) {
			doDotDensity();
		}

		pointStyle = pointList.getPointStyle();
		pointSize = pointList.getPointSize();
		pointColor = geo.getObjectColor();

		boolean doVisualStyleUpdate = (oldPointSize != pointSize)
				|| (oldPointStyle != pointStyle)
				|| !(oldColor.equals(pointColor));

		oldPointSize = pointSize;
		oldPointStyle = pointStyle;
		oldColor = geo.getObjectColor();

		for (int i = 0; i < pointList.size(); i++) {
			GeoPoint pt = (GeoPoint) pointList.get(i);

			if (doVisualStyleUpdate) {
				pt.setObjColor(pointColor);
				pt.setPointSize(pointSize);
				pt.setPointStyle(pointStyle);
			}

			drawPoints.get(i).update();
		}

		GeoPoint pt = (GeoPoint) pointList.get(0);
		coords[0] = pt.getX();
		coords[1] = pt.getY();
		view.toScreenCoords(coords);

		if (labelVisible) {
			xLabel = (int) coords[0];
			yLabel = (int) coords[1] + 2 * view.getFontSize();
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}
	}

	private void updatePointLists() {

		// find the number of points to draw
		int n = pointList.size();
		drawPoints.ensureCapacity(n);

		// adjust the lists
		if (n > drawPoints.size()) {
			// add
			for (int i = drawPoints.size(); i < n; i++) {
				GeoPoint pt = (GeoPoint) pointList.get(i);
				DrawPoint d = new DrawPoint(view, pt);
				d.setGeoElement(pt);
				drawPoints.add(d);
			}
		} else if (n < drawPoints.size()) {
			// remove
			for (int i = n; n < drawPoints.size();) {
				drawPoints.remove(i);
			}
		}
	}

	/**
	 * Sets the real world height of a point so that it fits in a stack of dots
	 * 
	 * @param pt
	 *            point
	 * @param dotCount
	 *            number of dots the point is stacked above the x-axis
	 */
	private void setDotHeight(GeoPoint pt, int dotCount) {
		double y;
		pointSize = pointList.getPointSize();

		// get y coord for the stacked dot
		y = (view.getYZero() - pointSize); // first dot on axis
		y = y - 2 * (dotCount - 1) * pointSize * scaleFactor; // higher dot
		y = view.toRealWorldCoordY(y);

		// set the y coord of the GeoPoint
		pt.setY(y);
		pt.updateCoords();
	}

	/**
	 * Stacks dots when x values are less than a dot diameter away. Follows
	 * Wilkinson's algorithm.
	 */
	private void doDotDensity() {

		pointSize = pointList.getPointSize();
		double h = 2 * pointSize * view.getInvXscale();
		scaleFactor = algo.getScaleFactor();

		GeoPoint pt = null;
		GeoList xList = algo.getUniqueXList();
		GeoList freqList = algo.getFrequencyList();

		int xIndex = 0;
		int dotCount = 1;
		double stackX = ((GeoNumeric) xList.get(xIndex)).getDouble();

		for (int i = 0; i < xList.size(); i++) {

			double x = ((GeoNumeric) xList.get(i)).getDouble();
			int freq = (int) ((GeoNumeric) freqList.get(i)).getDouble();

			if (x > stackX + h) {
				stackX = x;
				dotCount = 1;
			}

			for (int k = 0; k < freq; k++) {
				pt = (GeoPoint) pointList.get(xIndex);
				pt.setX(stackX);
				pt.updateCoords();
				setDotHeight(pt, dotCount);
				dotCount++;
				xIndex++;
			}
		}
	}

	/**
	 * @param index
	 *            index of point in the algorithm output list
	 */
	public void setToolTipForPoint(int index) {
		double x = getDotPlotX(index);
		String text = view.getKernel().format(x,
				StringTemplate.defaultTemplate);
		algo.setToolTipPointText(text);

		// force automatic tool tip update
		view.setToolTipText(" ");
	}

	private double getDotPlotX(int index) {
		double x = 0;
		int xIndex = 0;
		GeoList list1 = algo.getUniqueXList();
		GeoList list2 = algo.getFrequencyList();

		for (int i = 0; i < list1.size(); i++) {

			x = ((GeoNumeric) list1.get(i)).getDouble();
			int freq = (int) ((GeoNumeric) list2.get(i)).getDouble();

			for (int k = 0; k < freq; k++) {
				if (index == xIndex) {
					return x;
				}
				xIndex++;
			}
		}
		return x;
	}
}
