package org.geogebra.common.euclidian.modes;

import java.util.ArrayList;
import java.util.Iterator;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.algos.AlgoAttachCopyToView;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoPolyLine;
import org.geogebra.common.kernel.algos.AlgorithmSet;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPenStroke;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.debug.Log;

public class ModeDelete {
	private EuclidianView view;
	private EuclidianController ec;
	private boolean objDeleteMode = false, penDeleteMode = false;
	private ArrayList<GPoint2D> interPoints;

	public ModeDelete(EuclidianView view) {
		this.ec = view.getEuclidianController();
		this.view = view;
		this.interPoints = new ArrayList<GPoint2D>();
	}

	GRectangle rect = AwtFactory.getPrototype().newRectangle(0, 0, 100, 100);

	public void handleMouseDraggedForDelete(AbstractEvent e, int deleteSize,
			boolean forceOnlyStrokes) {
		if (e == null) {
			return;
		}

		int eventX = e.getX();
		int eventY = e.getY();
		rect.setBounds(eventX - deleteSize / 2, eventY - deleteSize / 2,
				deleteSize, deleteSize);
		view.setDeletionRectangle(rect);
		view.setIntersectionHits(rect);
		Hits h = view.getHits();
		if (!this.objDeleteMode && !this.penDeleteMode) {
			updatePenDeleteMode(h);
		}
		boolean onlyStrokes = forceOnlyStrokes || this.penDeleteMode;

		// hide cursor, the new "cursor" is the deletion rectangle
		view.setCursor(EuclidianCursor.TRANSPARENT);

		Iterator<GeoElement> it = h.iterator();

		AlgorithmSet as = null;
		while (it.hasNext()) {
			GeoElement geo = it.next();
			// delete tool should delete the object for dragging
			// at whiteboard
			// see MOW-97
			if (view.getApplication().isWhiteBoard()
					&& ec.getMode() == EuclidianConstants.MODE_DELETE) {
				geo.removeOrSetUndefinedIfHasFixedDescendent();
			} else if (geo instanceof GeoPenStroke) {
				GeoPenStroke gps = (GeoPenStroke) geo;

				// we need two arrays for the case that AlgoAttachCopyToView is
				// involved
				// the original points (dataPoints) are saved, but will be
				// translated
				// and everything by the algorithm so that the
				// GeoPenStroke-output
				// holds the points which are really drawn (and should be used
				// for
				// hit detection).

				GeoPointND[] realPoints = gps.getPoints();
				GeoPointND[] dataPoints;

				if (geo.getParentAlgorithm() != null && (geo
						.getParentAlgorithm() instanceof AlgoAttachCopyToView)) {
					AlgoElement ae = geo.getParentAlgorithm();
					for (int i = 0; i < ae.getInput().length; i++) {
						if (ae.getInput()[i] instanceof GeoPenStroke) {
							gps = (GeoPenStroke) ae.getInput()[i];
						}
					}
				}

				if (gps.getParentAlgorithm() != null
						&& gps.getParentAlgorithm() instanceof AlgoPolyLine) {
					dataPoints = ((AlgoPolyLine) gps.getParentAlgorithm())
							.getPoints();
				} else {
					dataPoints = gps.getPoints();
				}

				// find out if this stroke is still visible
				// after removing points
				boolean hasVisibleLine = false;
				boolean lastWasVisible = false;
				boolean hasVisiblePart = false;
				if (realPoints.length == dataPoints.length) {
					for (int i = 0; i < dataPoints.length; i++) {
						GeoPoint p = (GeoPoint) realPoints[i];
						if (p.isDefined() && Math.max(
								Math.abs(eventX
										- view.toScreenCoordXd(p.inhomX)),
								Math.abs(eventY - view.toScreenCoordYd(
										p.inhomY))) <= deleteSize / 2.0) {
							// end point of segment is in rectangle
							if ((i - 1 >= 0 && dataPoints[i - 1].isDefined())) {
								// get intersection point
								getAllIntersectionPoint(
											dataPoints[i - 1], dataPoints[i],
											rect);
								if (!interPoints.isEmpty()
										&& interPoints.size() == 1) {
										double realX = view.toRealWorldCoordX(
											interPoints.get(0)
													.getX());
										double realY = view.toRealWorldCoordY(
											interPoints.get(0)
													.getY());
									// switch old point with intersection point
										dataPoints[i].setCoords(realX, realY,
											1);
								}
								// no intersection point
								else if (interPoints.isEmpty()) {
									double pointX = view.toScreenCoordXd(
											dataPoints[i - 1].getInhomX());
									double pointY = view.toScreenCoordYd(
											dataPoints[i - 1].getInhomY());
									GPoint2D point = AwtFactory.getPrototype()
											.newPoint2D(pointX, pointY);
									// if the first point is also inside of
									// rectangle
									if (rect.contains(point)) {
										// we can set the end point to undefined
										dataPoints[i].setUndefined();
										dataPoints[i].resetDefinition();
									}
								}
								// TODO handle more than one intersection points
								else {
									// Log.debug(
									// "LIST_SIZE: " + interPoints.size());
									/*
									 * double realX = view.toRealWorldCoordX(
									 * interPoints.get(0).getX()); double realY
									 * = view.toRealWorldCoordY(
									 * interPoints.get(0).getY()); // switch old
									 * point with intersection point
									 * dataPoints[i].setCoords(realX, realY, 1);
									 * GeoPointND[] newDataPoints = new
									 * GeoPointND[dataPoints.length + 1];
									 * newDataPoints = Arrays
									 * .copyOfRange(dataPoints, 0, i);
									 */
									if (areClose(interPoints.get(0),
											interPoints.get(1))) {
										double realX = view.toRealWorldCoordX(
												interPoints.get(0).getX());
										double realY = view.toRealWorldCoordY(
												interPoints.get(0).getY());
										// switch old point with intersection
										// point
										dataPoints[i].setCoords(realX, realY,
												1);
									} else {
										// Log.debug("ARE_NOT_CLOSE");
										dataPoints[i].setUndefined();
										dataPoints[i].resetDefinition();
									}
								}
							}
							// start point of segment is in rectangle
							else if (i - 1 >= 0
									&& !dataPoints[i - 1].isDefined()
									&& i + 1 < dataPoints.length
									&& dataPoints[i + 1].isDefined()) {
								// get intersection point
								getAllIntersectionPoint(dataPoints[i],
										dataPoints[i + 1], rect);
								if (!interPoints.isEmpty()
										&& interPoints.size() == 1) {
										double realX = view.toRealWorldCoordX(
											interPoints.get(0).getX());
										double realY = view.toRealWorldCoordY(
											interPoints.get(0).getY());
									// switch old point with intersection point
									dataPoints[i].setCoords(realX, realY, 1);
								}
								// no intersection
								else if (interPoints.isEmpty()) {
									double pointX = view.toScreenCoordXd(
											dataPoints[i + 1].getInhomX());
									double pointY = view.toScreenCoordYd(
											dataPoints[i + 1].getInhomY());
									GPoint2D point = AwtFactory.getPrototype()
											.newPoint2D(pointX, pointY);
									// if end point is also inside of the
									// rectangle
									if (rect.contains(point)) {
										// we can set point the start point at
										// undefined
										dataPoints[i].setUndefined();
										dataPoints[i].resetDefinition();
									}
								}
								// TODO handle more intersection points
								else {
									// Log.debug(
									// "LIST_SIZE: " + interPoints.size());
									if (areClose(interPoints.get(0),
											interPoints.get(1))) {
										double realX = view.toRealWorldCoordX(
												interPoints.get(0).getX());
										double realY = view.toRealWorldCoordY(
												interPoints.get(0).getY());
										// switch old point with intersection
										// point
										dataPoints[i].setCoords(realX, realY,
												1);
									} else {
										// Log.debug("ARE_NOT_CLOSE");
										dataPoints[i].setUndefined();
										dataPoints[i].resetDefinition();
									}
								}
							}
							// handle first point and last point
							else if ((i == 0
									&& ((i + 1 < dataPoints.length
											&& !dataPoints[i + 1].isDefined())
											|| (i + 1 == dataPoints.length)))
									|| (i - 1 >= 0
											&& !dataPoints[i - 1].isDefined()
											&& i + 1 == dataPoints.length)) {
								dataPoints[i].setUndefined();
								dataPoints[i].resetDefinition();
							}
							// handle single remained point
							else if (i - 1 >= 0
									&& !dataPoints[i - 1].isDefined()
									&& i + 1 < dataPoints.length
								  && !dataPoints[i + 1].isDefined()) {
								dataPoints[i].setUndefined();
								dataPoints[i].resetDefinition();
							}
								 
							if (as == null) {
								as = dataPoints[i].getAlgoUpdateSet();
							} else {
								as.addAll(dataPoints[i].getAlgoUpdateSet());
							}
						} else {
							if (i < dataPoints.length - 1
									&& dataPoints[i].isDefined()
									&& dataPoints[i + 1].isDefined()) {
								getAllIntersectionPoint(dataPoints[i],
										dataPoints[i + 1], rect);
								if (!interPoints.isEmpty()
										&& interPoints.size() >= 2) {
									dataPoints[i].setUndefined();
									dataPoints[i].resetDefinition();
								}
								if (as == null) {
									as = dataPoints[i].getAlgoUpdateSet();
								} else {
									as.addAll(dataPoints[i].getAlgoUpdateSet());
								}
							}
						}

						if (lastWasVisible && dataPoints[i].isDefined()) {
							hasVisibleLine = true;
						}
						lastWasVisible = dataPoints[i].isDefined();
						if (!hasVisiblePart && dataPoints[i].isDefined()) {
							hasVisiblePart = true;
						}
					}

				} else {
					Log.debug(
							"Can't delete points on stroke. Different number of in and output points.");
				}
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

		ec.deleteAll(h);
		if (as != null)
			as.updateAll();
	}

	private void updatePenDeleteMode(Hits h) {
		// if we switched to pen deletion just now, some geos may still need
		// removing
		Iterator<GeoElement> it2 = h.iterator();
		while (it2.hasNext()) {
			GeoElement geo2 = it2.next();
			if (geo2 instanceof GeoPenStroke) {
				this.penDeleteMode = true;
			}
		}
	}

	public GPoint2D getTopIntersectionPoint(GeoPointND point1,
			GeoPointND point2,
			GRectangle rectangle) {

		// Top line
		return getIntersectionPoint(point1, point2,
				rectangle.getX(), rectangle.getY(),
						rectangle.getX() + rectangle.getWidth(),
				rectangle.getY());
	}

	public GPoint2D getBottomIntersectionPoint(GeoPointND point1,
			GeoPointND point2, GRectangle rectangle) {

		// Bottom line
		return getIntersectionPoint(point1, point2,
				rectangle.getX(),
						rectangle.getY() + rectangle.getHeight(),
						rectangle.getX() + rectangle.getWidth(),
				rectangle.getY() + rectangle.getHeight());
	}

	public GPoint2D getLeftIntersectionPoint(GeoPointND point1,
			GeoPointND point2, GRectangle rectangle) {

		// Left side
		return getIntersectionPoint(point1, point2,
				rectangle.getX(), rectangle.getY(),
						rectangle.getX(),
				rectangle.getY() + rectangle.getHeight());
	}

	public GPoint2D getRightIntersectionPoint(GeoPointND point1,
			GeoPointND point2, GRectangle rectangle) {

		// Right side
		return getIntersectionPoint(point1, point2,
				rectangle.getX() + rectangle.getWidth(),
						rectangle.getY(),
						rectangle.getX() + rectangle.getWidth(),
				rectangle.getY() + rectangle.getHeight());
	}

	public void getAllIntersectionPoint(GeoPointND point1,
			GeoPointND point2,
			GRectangle rectangle) {

		interPoints = new ArrayList<GPoint2D>();

		// intersection points
		GPoint2D topInter = getTopIntersectionPoint(point1, point2, rectangle);
		if (topInter != null) {
			// Log.debug("TOP_INTER");
			interPoints.add(topInter);
		}
		GPoint2D bottomInter = getBottomIntersectionPoint(point1, point2,
				rectangle);
		if (bottomInter != null) {
			// Log.debug("BOTTOM_INTER");
			interPoints.add(bottomInter);
		}
		GPoint2D leftInter = getLeftIntersectionPoint(point1, point2,
				rectangle);
		if (leftInter != null) {
			// Log.debug("LEFT_INTER");
			interPoints.add(leftInter);
		}
		GPoint2D rightInter = getRightIntersectionPoint(point1, point2,
				rectangle);
		if (rightInter != null) {
			// Log.debug("RIGHT_INTER");
			interPoints.add(rightInter);
		}

	}

	public GPoint2D getIntersectionPoint(GeoPointND point1, GeoPointND point2,
			double startPointX, double startPointY, double endPointX,
			double endPointY) {

		double x1 = view.toScreenCoordXd(point1.getInhomX());
		double y1 = view.toScreenCoordYd(point1.getInhomY());
		double x2 = view.toScreenCoordXd(point2.getInhomX());
		double y2 = view.toScreenCoordYd(point2.getInhomY());

		double x3 = startPointX;
		double y3 = startPointY;
		double x4 = endPointX;
		double y4 = endPointY;

		GPoint2D p = null;

		double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
		if (d != 0.0) {
			double xi = ((x3 - x4) * (x1 * y2 - y1 * x2)
					- (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
			double yi = ((y3 - y4) * (x1 * y2 - y1 * x2)
					- (y1 - y2) * (x3 * y4 - y3 * x4)) / d;
			
			if (onSegment(Math.round(x1), Math.round(y1), xi, yi,
					Math.round(x2), Math.round(y2))
					&& onSegment(Math.round(x3), Math.round(y3), xi, yi,
							Math.round(x4), Math.round(y4))) {
				p = new GPoint2D.Double(xi, yi);
			}

		}
		return p;
	}

	private boolean onSegment(double segStartX, double segStartY,
			double interPointX, double interPointY, double segEndX,
			double segEndY) {
		if (interPointX <= Math.max(segStartX, segEndX) + 1
				&& interPointX >= Math.min(segStartX, segEndX) - 1
				&& interPointY <= Math.max(segStartY, segEndY) + 1
				&& interPointY >= Math.min(segStartY, segEndY) - 1)
			return true;

		return false;
	}

	private boolean areClose(GPoint2D point1, GPoint2D point2) {

		double distance = Math.hypot(point1.getX() - point2.getX(),
				point1.getY() - point2.getY());

		// Log.debug("DISTANCE: " + distance);

		return distance < 20;
	}

	public void mousePressed(PointerEventType type) {
		this.objDeleteMode = false;
		this.penDeleteMode = false;

	}

	public final boolean process(Hits hits, boolean control,
			boolean selPreview) {

		if (hits.isEmpty() || this.penDeleteMode) {
			return false;
		}

		ec.addSelectedGeo(hits, 1, false, selPreview);
		if (ec.selGeos() == 1) {
			GeoElement[] geos = ec.getSelectedGeos();
			AlgorithmSet as = null;
			// delete only parts of geoPenStroke, not the whole object
			// when eraser tool is used
			if (geos[0] instanceof GeoPenStroke
					&& ec.getMode() == EuclidianConstants.MODE_ERASER) {
				updatePenDeleteMode(hits);
				int eventX = 0;
				int eventY = 0;
				if (ec.getMouseLoc() != null) {
					eventX = ec.getMouseLoc().getX();
					eventY = ec.getMouseLoc().getY();
					rect.setBounds(eventX - ec.getDeleteToolSize() / 2,
							eventY - ec.getDeleteToolSize() / 2,
							ec.getDeleteToolSize(), ec.getDeleteToolSize());
				} else {
					return false;
				}
				GeoPenStroke gps = (GeoPenStroke) geos[0];
				GeoPointND[] realPoints = gps.getPoints();
				GeoPointND[] dataPoints;

				if (geos[0].getParentAlgorithm() != null && (geos[0]
						.getParentAlgorithm() instanceof AlgoAttachCopyToView)) {
					AlgoElement ae = geos[0].getParentAlgorithm();
					for (int i = 0; i < ae.getInput().length; i++) {
						if (ae.getInput()[i] instanceof GeoPenStroke) {
							gps = (GeoPenStroke) ae.getInput()[i];
						}
					}
				}
				if (gps.getParentAlgorithm() != null
						&& gps.getParentAlgorithm() instanceof AlgoPolyLine) {
					dataPoints = ((AlgoPolyLine) gps.getParentAlgorithm())
							.getPoints();
				} else {
					dataPoints = gps.getPoints();
				}

				// find out if this stroke is still visible
				// after removing points
				boolean hasVisibleLine = false;
				boolean lastWasVisible = false;
				boolean hasVisiblePart = false;
				if (realPoints.length == dataPoints.length) {
					for (int i = 0; i < dataPoints.length; i++) {
						GeoPoint p = (GeoPoint) realPoints[i];
						if (p.isDefined() && Math.max(
								Math.abs(eventX
										- view.toScreenCoordXd(p.inhomX)),
								Math.abs(eventY
										- view.toScreenCoordYd(p.inhomY))) <= ec
												.getDeleteToolSize() / 2.0) {

							// end point of segment is in rectangle
							if ((i - 1 >= 0 && dataPoints[i - 1].isDefined())) {
								// get intersection point
								getAllIntersectionPoint(dataPoints[i - 1],
										dataPoints[i], rect);
								if (!interPoints.isEmpty()
										&& interPoints.size() == 1) {
									double realX = view.toRealWorldCoordX(
											interPoints.get(0).getX());
									double realY = view.toRealWorldCoordY(
											interPoints.get(0).getY());
									// switch old point with intersection point
									dataPoints[i].setCoords(realX, realY, 1);
								}
								// no intersection point
								else if (interPoints.isEmpty()) {
									double pointX = view.toScreenCoordXd(
											dataPoints[i - 1].getInhomX());
									double pointY = view.toScreenCoordYd(
											dataPoints[i - 1].getInhomY());
									GPoint2D point = AwtFactory.getPrototype()
											.newPoint2D(pointX, pointY);
									// if the first point is also inside of
									// rectangle
									if (rect.contains(point)) {
										// we can set the end point to undefined
										dataPoints[i].setUndefined();
										dataPoints[i].resetDefinition();
									}
								}
								// TODO handle more than one intersection points
								else {
									// Log.debug(
									// "LIST_SIZE: " + interPoints.size());
									/*
									 * double realX = view.toRealWorldCoordX(
									 * interPoints.get(0).getX()); double realY
									 * = view.toRealWorldCoordY(
									 * interPoints.get(0).getY()); // switch old
									 * point with intersection point
									 * dataPoints[i].setCoords(realX, realY, 1);
									 * GeoPointND[] newDataPoints = new
									 * GeoPointND[dataPoints.length + 1];
									 * newDataPoints = Arrays
									 * .copyOfRange(dataPoints, 0, i);
									 */
									if (areClose(interPoints.get(0),
											interPoints.get(1))) {
										double realX = view.toRealWorldCoordX(
												interPoints.get(0).getX());
										double realY = view.toRealWorldCoordY(
												interPoints.get(0).getY());
										// switch old point with intersection
										// point
										dataPoints[i].setCoords(realX, realY,
												1);
									} else {
										// Log.debug("ARE_NOT_CLOSE");
										dataPoints[i].setUndefined();
										dataPoints[i].resetDefinition();
									}
								}
							}
							// start point of segment is in rectangle
							else if (i - 1 >= 0
									&& !dataPoints[i - 1].isDefined()
									&& i + 1 < dataPoints.length
									&& dataPoints[i + 1].isDefined()) {
								// get intersection point
								getAllIntersectionPoint(dataPoints[i],
										dataPoints[i + 1], rect);
								if (!interPoints.isEmpty()
										&& interPoints.size() == 1) {
									double realX = view.toRealWorldCoordX(
											interPoints.get(0).getX());
									double realY = view.toRealWorldCoordY(
											interPoints.get(0).getY());
									// switch old point with intersection point
									dataPoints[i].setCoords(realX, realY, 1);
								}
								// no intersection
								else if (interPoints.isEmpty()) {
									double pointX = view.toScreenCoordXd(
											dataPoints[i + 1].getInhomX());
									double pointY = view.toScreenCoordYd(
											dataPoints[i + 1].getInhomY());
									GPoint2D point = AwtFactory.getPrototype()
											.newPoint2D(pointX, pointY);
									// if end point is also inside of the
									// rectangle
									if (rect.contains(point)) {
										// we can set point the start point at
										// undefined
										dataPoints[i].setUndefined();
										dataPoints[i].resetDefinition();
									}
								}
								// TODO handle more intersection points
								else {
									// Log.debug(
									// "LIST_SIZE: " + interPoints.size());
									if (areClose(interPoints.get(0),
											interPoints.get(1))) {
										double realX = view.toRealWorldCoordX(
												interPoints.get(0).getX());
										double realY = view.toRealWorldCoordY(
												interPoints.get(0).getY());
										// switch old point with intersection
										// point
										dataPoints[i].setCoords(realX, realY,
												1);
									} else {
										// Log.debug("ARE_NOT_CLOSE");
										dataPoints[i].setUndefined();
										dataPoints[i].resetDefinition();
									}
								}
							}
							// handle first point and last point
							else if ((i == 0 && ((i + 1 < dataPoints.length
									&& !dataPoints[i + 1].isDefined())
									|| (i + 1 == dataPoints.length)))
									|| (i - 1 >= 0
											&& !dataPoints[i - 1].isDefined()
											&& i + 1 == dataPoints.length)) {
								dataPoints[i].setUndefined();
								dataPoints[i].resetDefinition();
							}
							// handle single remained point
							else if (i - 1 >= 0
									&& !dataPoints[i - 1].isDefined()
									&& i + 1 < dataPoints.length
									&& !dataPoints[i + 1].isDefined()) {
								dataPoints[i].setUndefined();
								dataPoints[i].resetDefinition();
							}

							if (as == null) {
								as = dataPoints[i].getAlgoUpdateSet();
							} else {
								as.addAll(dataPoints[i].getAlgoUpdateSet());
							}
						} else {
							if (i < dataPoints.length - 1
									&& dataPoints[i].isDefined()
									&& dataPoints[i + 1].isDefined()) {
								getAllIntersectionPoint(dataPoints[i],
										dataPoints[i + 1], rect);
								if (!interPoints.isEmpty()
										&& interPoints.size() >= 2) {
									dataPoints[i].setUndefined();
									dataPoints[i].resetDefinition();
								}
							}
							if (as == null) {
								as = dataPoints[i].getAlgoUpdateSet();
							} else {
								as.addAll(dataPoints[i].getAlgoUpdateSet());
							}
						}

						if (lastWasVisible && dataPoints[i].isDefined()) {
							hasVisibleLine = true;
						}
						lastWasVisible = dataPoints[i].isDefined();
						if (!hasVisiblePart && dataPoints[i].isDefined()) {
							hasVisiblePart = true;
						}
					}
				} else {
					Log.debug(
							"Can't delete points on stroke. Different number of in and output points.");
				}
				if (!hasVisiblePart) { // still something visible, don't delete
					geos[0].removeOrSetUndefinedIfHasFixedDescendent(); // remove
																		// this
																		// Stroke
				}
				if (as != null)
					as.updateAll();
			}
			// delete this object
			else {
				geos[0].removeOrSetUndefinedIfHasFixedDescendent();
			}
			return true;
		}
		return false;
	}
}
