package org.geogebra.common.euclidian.modes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.algos.AlgoLocusStroke;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.util.debug.Log;

/**
 * Delete mode controller for locus based penstrokes
 */
public class ModeDeleteLocus {
	private EuclidianView view;
	private EuclidianController ec;
	private boolean objDeleteMode = false;
	private boolean penDeleteMode = false;
	private ArrayList<MyPoint> interPoints;
	private GRectangle rect = AwtFactory.getPrototype().newRectangle(0, 0, 100,
			100);

	/**
	 * @param view
	 *            EV
	 */
	public ModeDeleteLocus(EuclidianView view) {
		this.ec = view.getEuclidianController();
		this.view = view;
		this.interPoints = new ArrayList<>();
	}

	/**
	 * @param e
	 *            mouse event
	 * @param forceOnlyStrokes
	 *            whether to only delete strokes
	 */
	public void handleMouseDraggedForDelete(AbstractEvent e, boolean forceOnlyStrokes) {
		if (e == null) {
			return;
		}

		int eventX = e.getX();
		int eventY = e.getY();
		rect.setBounds(eventX - ec.getDeleteToolSize() / 2,
				eventY - ec.getDeleteToolSize() / 2,
				ec.getDeleteToolSize(), ec.getDeleteToolSize());

		view.setDeletionRectangle(rect);
		view.getHitDetector().setIntersectionHits(rect);
		Hits h = view.getHits();
		if (!this.objDeleteMode && !this.penDeleteMode) {
			updatePenDeleteMode(h);
		}
		boolean onlyStrokes = forceOnlyStrokes || this.penDeleteMode;

		// hide cursor, the new "cursor" is the deletion rectangle
		view.setCursor(EuclidianCursor.TRANSPARENT);

		Iterator<GeoElement> it = h.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			// delete tool should delete the object for dragging
			// at whiteboard
			// see MOW-97
			if (view.getApplication().isWhiteboardActive()
					&& ec.getMode() == EuclidianConstants.MODE_DELETE) {
				geo.removeOrSetUndefinedIfHasFixedDescendent();
			} else if (geo instanceof GeoLocusStroke) {
				boolean hasVisiblePart = deletePartOfPenStroke(
						(GeoLocusStroke) geo, eventX, eventY);

				if (hasVisiblePart) { // still something visible, don't delete
					it.remove(); // remove this Stroke from hits
				}
			} else {
				if (!this.penDeleteMode) {
					this.objDeleteMode = true;
				}
				if (onlyStrokes) {
					it.remove();
				}
			}
		}
		// do not delete images using eraser
		h.removeImages();
		ec.deleteAll(h);
	}

	/**
	 * @param hits
	 *            hit objects
	 * @param selPreview
	 *            for preview
	 * @return whether something was deleted
	 */
	public boolean process(Hits hits, boolean selPreview) {
		if (hits.isEmpty() || this.penDeleteMode) {
			return false;
		}
		ec.addSelectedGeo(hits, 1, false, selPreview);
		if (ec.selGeos() == 1) {
			GeoElement[] geos = ec.getSelectedGeos();
			// delete only parts of GeoLocusStroke, not the whole object
			// when eraser tool is used
			if (geos[0] instanceof GeoLocusStroke
					&& ec.getMode() == EuclidianConstants.MODE_ERASER) {
				updatePenDeleteMode(hits);
				if (ec.getMouseLoc() == null) {
					return false;
				}

				int eventX = ec.getMouseLoc().getX();
				int eventY = ec.getMouseLoc().getY();
				rect.setBounds(eventX - ec.getDeleteToolSize() / 2,
						eventY - ec.getDeleteToolSize() / 2,
						ec.getDeleteToolSize(), ec.getDeleteToolSize());

				boolean hasVisiblePart = deletePartOfPenStroke(
						(GeoLocusStroke) geos[0], eventX, eventY);

				if (!hasVisiblePart) { // still something visible, don't delete
					// remove this Stroke
					geos[0].removeOrSetUndefinedIfHasFixedDescendent();
				}
			}
			// delete this object
			else {
				if (!(geos[0] instanceof GeoImage)) {
					geos[0].removeOrSetUndefinedIfHasFixedDescendent();
				}
			}
			return true;
		}
		return false;
	}

	private boolean deletePartOfPenStroke(GeoLocusStroke gls, int eventX, int eventY) {
		ArrayList<MyPoint> dataPoints = gls.getPointsWithoutControl();

		double deleteThreshold = ec.getDeleteToolSize() / 2.0;

		boolean hasVisiblePart = false;
		if (dataPoints.size() > 0) {
			for (int i = 0; i < dataPoints.size(); i++) {
				MyPoint p = dataPoints.get(i);
				if (p.isDefined()
						&& Math.abs(eventX - view.toScreenCoordXd(p.getX())) <= deleteThreshold
						&& Math.abs(eventY - view.toScreenCoordYd(p.getY())) <= deleteThreshold) {
					// end point of segment is in rectangle
					if ((i - 1 >= 0 && dataPoints.get(i - 1).isDefined())) {
						// get intersection point
						interPoints.clear();
						interPoints = getAllIntersectionPoint(gls, dataPoints.get(i - 1),
								dataPoints.get(i), rect);

						if (interPoints.size() == 1) {
							i = handleEraserAtJoinPointOrEndOfSegments(gls, dataPoints, i);
						} else {
							i = handleEraserAtPoint(dataPoints, i);
						}
					} else if (i - 1 >= 0 && !dataPoints.get(i - 1).isDefined()
							&& i + 1 < dataPoints.size()
							&& dataPoints.get(i + 1).isDefined()) {
						// start point of segment is in rectangle
						handleEraserAtStartPointOfSegment(gls, dataPoints, i);
					} else {
						// handle first/last/single remained point
						handleLastFirstOrSinglePoints(dataPoints, i);
					}
				} else if (i < dataPoints.size() - 1 && dataPoints.get(i).isDefined()
						&& dataPoints.get(i + 1).isDefined()) {
					// eraser is between the endpoints of segment
					i = handleEraserBetweenPointsOfSegment(gls, dataPoints, i);
				}

				if (!hasVisiblePart && dataPoints.get(i).isDefined()) {
					hasVisiblePart = true;
				}
			}

			deleteUnnecessaryUndefPoints(dataPoints);

			gls.getPoints().clear();
			((AlgoLocusStroke) gls.getParentAlgorithm()).appendPointArray(dataPoints);
			gls.update();
		} else {
			Log.debug(
					"Can't delete points on stroke: output & input sizes differ.");
		}

		return hasVisiblePart;
	}

	private static void deleteUnnecessaryUndefPoints(List<MyPoint> dataPoints) {
		ArrayList<MyPoint> dataPointList = new ArrayList<>(
				dataPoints.size());
		int i = 1;
		while (i < dataPoints.size()) {
			if ((!dataPoints.get(i).isDefined()
					&& !dataPoints.get(i - 1).isDefined())) {
				i++;
			} else {
				dataPointList.add(dataPoints.get(i - 1));
				i++;
			}
		}
		if (dataPoints.get(i - 1).isDefined()) {
			dataPointList.add(dataPoints.get(i - 1));
		}
		if (dataPointList.size() != dataPoints.size()) {
			dataPoints.clear();
			dataPoints.addAll(dataPointList);
		}
	}

	// add new undefined points and update old points coordinates
	private static List<MyPoint> getNewPolyLinePoints(
			List<MyPoint> dataPoints, int newSize,
			int i, int indexInter1, int indexUndef, int indexInter2,
			double[] realCoords) {
		MyPoint[] newDataPoints = Arrays.copyOf(
				dataPoints.toArray(new MyPoint[0]),
				dataPoints.size() + newSize);

		if (newSize == 1) {
			for (int j = dataPoints.size(); j > i + 1; j--) {
				newDataPoints[j + newSize - 1] = dataPoints.get(j - 1);
			}
		} else if (newSize == -1) {
			for (int j = dataPoints.size(); j > i; j--) {
				newDataPoints[j] = dataPoints.get(j - 1);
			}
		} else {
			for (int j = dataPoints.size(); j > i - newSize + 3; j--) {
				newDataPoints[j + newSize - 1] = dataPoints.get(j - 1);
			}
		}
		newDataPoints[indexInter1] = ngp(realCoords[0], realCoords[1]);
		newDataPoints[indexUndef] = ngp();
		newDataPoints[indexInter2] = ngp(realCoords[2], realCoords[3]);

		return Arrays.asList(newDataPoints);
	}

	private static MyPoint ngp() {
		return new MyPoint(Double.NaN, Double.NaN, SegmentType.LINE_TO);
	}

	private static MyPoint ngp(double d, double e) {
		return new MyPoint(d, e, SegmentType.LINE_TO);
	}

	private void updatePenDeleteMode(Hits h) {
		// if we switched to pen deletion just now, some geos may still need
		// removing
		for (GeoElement geo2 : h) {
			if (geo2 instanceof GeoLocusStroke) {
				this.penDeleteMode = true;
				return;
			}
		}
	}

	/**
	 * method to get all intersection points of a segment with the eraser (with
	 * each side of rectangle)
	 *
	 * @param point1
	 *            start point of segment
	 * @param point2
	 *            end point of segment
	 * @param rectangle
	 *            eraser
	 * @return list of intersection points
	 */
	private ArrayList<MyPoint> getAllIntersectionPoint(GeoLocusStroke gls,
				MyPoint point1, MyPoint point2, GRectangle rectangle) {
		double x = view.toRealWorldCoordX(rectangle.getX());
		double y = view.toRealWorldCoordY(rectangle.getY());
		double width = rectangle.getWidth() * view.getInvXscale();
		double height = rectangle.getHeight() * view.getInvYscale();

		// intersection points
		ArrayList<MyPoint> interPointList = gls.getAllIntersectionPoints(point1, point2,
				x, y, width, height);

		for (MyPoint point : interPointList) {
			point.setX(view.toScreenCoordX(point.getX()));
			point.setY(view.toScreenCoordY(point.getY()));
		}

		return interPointList;
	}

	// check if the two intersection point is close enough
	private static boolean areClose(GPoint2D point1, GPoint2D point2) {
		double distance = Math.hypot(point1.getX() - point2.getX(),
				point1.getY() - point2.getY());
		return distance < 20;
	}

	private double[] getInterRealCoords(MyPoint point) {
		double[] coords = new double[4];

		double realX1 = view.toRealWorldCoordX(interPoints.get(0).getX());
		double realY1 = view.toRealWorldCoordY(interPoints.get(0).getY());
		double realX2 = view.toRealWorldCoordX(interPoints.get(1).getX());
		double realY2 = view.toRealWorldCoordY(interPoints.get(1).getY());

		double distance1 = Math.hypot(point.getX() - realX1,
				point.getY() - realY1);

		double distance2 = Math.hypot(point.getX() - realX2,
				point.getY() - realY2);

		// we need to decide the order of intersection points
		// in order to set correct the intersection points
		if (distance1 < distance2) {
			coords[0] = realX1;
			coords[1] = realY1;
			coords[2] = realX2;
			coords[3] = realY2;
		} else {
			coords[0] = realX2;
			coords[1] = realY2;
			coords[2] = realX1;
			coords[3] = realY1;
		}
		return coords;
	}

	public void mousePressed() {
		this.objDeleteMode = false;
		this.penDeleteMode = false;
	}

	private static void handleLastFirstOrSinglePoints(List<MyPoint> dataPoints,
			int i) {
		if ((i == 0 && ((i + 1 < dataPoints.size()
				&& !dataPoints.get(i + 1).isDefined())
				|| (i + 1 == dataPoints.size())))
				|| (i - 1 >= 0 && !dataPoints.get(i - 1).isDefined()
						&& i + 1 == dataPoints.size())) {
			dataPoints.get(i).setUndefined();
		}
		// handle single remained point
		else if (i - 1 >= 0 && !dataPoints.get(i - 1).isDefined()
				&& i + 1 < dataPoints.size()
				&& !dataPoints.get(i + 1).isDefined()) {
			dataPoints.get(i).setUndefined();
		}
	}

	private void handleEraserAtStartPointOfSegment(GeoLocusStroke gls, List<MyPoint> dataPoints,
			int i) {
		// get intersection points
		interPoints.clear();
		interPoints = getAllIntersectionPoint(gls, dataPoints.get(i),
				dataPoints.get(i + 1),
				rect);
		if (interPoints.size() == 1) {
			double realX = view.toRealWorldCoordX(interPoints.get(0).getX());
			double realY = view.toRealWorldCoordY(interPoints.get(0).getY());
			// switch old point with intersection point
			dataPoints.get(i).setCoords(realX, realY);
		}
		// no intersection
		else if (interPoints.isEmpty()) {
			double pointX = view.toScreenCoordXd(dataPoints.get(i + 1).getX());
			double pointY = view.toScreenCoordYd(dataPoints.get(i + 1).getY());
			GPoint2D point = AwtFactory.getPrototype().newPoint2D(pointX,
					pointY);
			// if end point is also inside of the
			// rectangle
			if (rect.contains(point)) {
				// we can set point the start point at
				// undefined
				dataPoints.get(i).setUndefined();
			}
		}
		// 2 intersection points
		else {
			if (areClose(interPoints.get(0), interPoints.get(1))) {
				double realX = view
						.toRealWorldCoordX(interPoints.get(0).getX());
				double realY = view
						.toRealWorldCoordY(interPoints.get(0).getY());
				// switch old point with intersection
				// point
				dataPoints.get(i).setCoords(realX, realY);
			} else {
				dataPoints.get(i).setUndefined();
			}
		}
	}

	private int handleEraserAtPoint(List<MyPoint> dataPoints, int i) {
		int index = i;
		// no intersection points
		if (interPoints.isEmpty()) {
			double pointX = view.toScreenCoordXd(dataPoints.get(i - 1).getX());
			double pointY = view.toScreenCoordYd(dataPoints.get(i - 1).getY());
			GPoint2D point = AwtFactory.getPrototype().newPoint2D(pointX,
					pointY);
			// if the first point is also inside of
			// rectangle
			if (rect.contains(point)) {
				// we can set the end point to undefined
				dataPoints.get(i).setUndefined();
			}
		}
		// two intersection points
		else {
			if (areClose(interPoints.get(0), interPoints.get(1))) {
				double realX = view
						.toRealWorldCoordX(interPoints.get(0).getX());
				double realY = view
						.toRealWorldCoordY(interPoints.get(0).getY());
				// switch old point with intersection
				// point
				dataPoints.get(i).setCoords(realX, realY);
			} else {
				double[] realCoords = getInterRealCoords(
						dataPoints.get(i - 1));
				swap(dataPoints, getNewPolyLinePoints(
						dataPoints, 1, i, i - 1, i, i + 1,
						realCoords));

				index = i + 2;
			}
		}
		return index;
	}

	private int handleEraserAtJoinPointOrEndOfSegments(GeoLocusStroke gls, List<MyPoint> dataPoints,
			int i) {
		ArrayList<MyPoint> secondInterPoints;

		if (i + 1 < dataPoints.size() && dataPoints.get(i + 1).isDefined()) {
			// see if there is intersection point with next segment
			secondInterPoints = getAllIntersectionPoint(gls, dataPoints.get(i),
					dataPoints.get(i + 1), rect);
			// case point is the join point of 2 segments
			if (secondInterPoints.size() == 1) {
				interPoints.add(secondInterPoints.get(0));
				double[] realCoords = getInterRealCoords(
						dataPoints.get(i - 1));
				swap(dataPoints, getNewPolyLinePoints(dataPoints, 2, i, i,
						i + 1, i + 2, realCoords));
				return i + 3;
			}
		} else {
			// point is endpoint of segment
			double realX = view.toRealWorldCoordX(interPoints.get(0).getX());
			double realY = view.toRealWorldCoordY(interPoints.get(0).getY());
			// switch old point with
			// intersection point
			dataPoints.get(i).setCoords(realX, realY);
		}

		return i;
	}

	private static void swap(List<MyPoint> dataPoints,
			List<MyPoint> newPolyLinePoints) {
		dataPoints.clear();
		dataPoints.addAll(newPolyLinePoints);
	}

	private int handleEraserBetweenPointsOfSegment(GeoLocusStroke gls,
			List<MyPoint> dataPoints, int i) {
		interPoints.clear();
		interPoints = getAllIntersectionPoint(gls, dataPoints.get(i),
				dataPoints.get(i + 1), rect);

		if (interPoints.size() >= 2) {
			double[] realCoords = getInterRealCoords(dataPoints.get(i));
			swap(dataPoints, getNewPolyLinePoints(dataPoints, 3, i, i + 1,
						i + 2, i + 3, realCoords));
			return i + 4;
		}

		return i;
	}
}
