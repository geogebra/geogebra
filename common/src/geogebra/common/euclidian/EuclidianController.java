/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package geogebra.common.euclidian;


import geogebra.common.awt.GColor;
import geogebra.common.awt.GPoint;
import geogebra.common.awt.GPoint2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.draw.DrawConic;
import geogebra.common.euclidian.draw.DrawConicPart;
import geogebra.common.euclidian.draw.DrawSlider;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.AlgoDispatcher;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.PathNormalizer;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.advanced.AlgoClosestPoint;
import geogebra.common.kernel.advanced.AlgoDynamicCoordinates;
import geogebra.common.kernel.advanced.AlgoFunctionFreehand;
import geogebra.common.kernel.advanced.AlgoPolarLine;
import geogebra.common.kernel.algos.AlgoArcLength;
import geogebra.common.kernel.algos.AlgoCirclePointRadius;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoIntersect;
import geogebra.common.kernel.algos.AlgoIntersectLineConic;
import geogebra.common.kernel.algos.AlgoIntersectPolynomialLine;
import geogebra.common.kernel.algos.AlgoIntersectSingle;
import geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import geogebra.common.kernel.algos.AlgoMidpoint;
import geogebra.common.kernel.algos.AlgoPolygon;
import geogebra.common.kernel.algos.AlgoRadius;
import geogebra.common.kernel.algos.AlgoTranslate;
import geogebra.common.kernel.algos.AlgoVector;
import geogebra.common.kernel.algos.AlgoVectorPoint;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.PolyFunction;
import geogebra.common.kernel.geos.Furniture;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoAxis;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPoly;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.geos.PointProperties;
import geogebra.common.kernel.geos.PointRotateable;
import geogebra.common.kernel.geos.Test;
import geogebra.common.kernel.geos.Transformable;
import geogebra.common.kernel.geos.Translateable;
import geogebra.common.kernel.implicit.AlgoIntersectImplicitpolys;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.GeoAxisND;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoConicND.HitType;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.kernel.statistics.AlgoFitLineY;
import geogebra.common.main.App;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.Operation;
import geogebra.common.util.MyMath;
import geogebra.common.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

@SuppressWarnings("javadoc")
public abstract class EuclidianController {

	protected static int POLYGON_NORMAL = 0;

	protected static int POLYGON_RIGID = 1;

	protected static int POLYGON_VECTOR = 2;

	protected static double MOUSE_DRAG_MAX_DIST_SQUARE = 36;

	protected static int MAX_CONTINUITY_STEPS = 4;

	private static boolean textBoxFocused;

	protected static void removeAxes(ArrayList<GeoElement> geos) {
	
		for (int i = geos.size() - 1; i >= 0; i--) {
			GeoElement geo = geos.get(i);
			if (geo instanceof GeoAxis) {
				geos.remove(i);
			}
		}
	}


	protected int mx; protected int my; //mouse coordinates

	protected double xTemp;

	protected double yTemp;

	public double xRW;

	public double yRW;

	double xRWold = Double.NEGATIVE_INFINITY;

	double yRWold = xRWold;

	double temp;

	protected boolean useLineEndPoint = false;

	protected GeoConic tempConic;

	protected GeoImplicitPoly tempImplicitPoly;

	protected ArrayList<GeoPoint> moveDependentPoints;

	protected GeoFunction tempFunction;

	protected GeoPointND movedGeoPoint;

	protected boolean movedGeoPointDragged = false;

	protected GeoLine movedGeoLine;

	protected GeoConic movedGeoConic;

	protected GeoImplicitPoly movedGeoImplicitPoly;

	protected GeoVector movedGeoVector;

	protected GeoText movedGeoText;

	protected GeoImage oldImage;

	protected GeoImage movedGeoImage;

	protected GeoFunction movedGeoFunction;

	protected GeoNumeric movedGeoNumeric;

	protected boolean movedGeoNumericDragged = false;

	protected GeoBoolean movedGeoBoolean;

	protected Furniture movedGeoButton;

	protected GeoElement movedLabelGeoElement;

	protected GeoElement movedGeoElement;

	protected GeoElement recordObject = null;

	protected MyDouble tempNum;

	protected double rotStartAngle;

	protected ArrayList<GeoElement> translateableGeos;

	protected Coords translationVec;

	protected Hits tempArrayList = new Hits();

	protected Hits tempArrayList2 = new Hits();

	protected Hits tempArrayList3 = new Hits();

	protected ArrayList<GeoPointND> selectedPoints = new ArrayList<GeoPointND>();

	protected ArrayList<GeoNumeric> selectedNumbers = new ArrayList<GeoNumeric>();

	protected ArrayList<NumberValue> selectedNumberValues = new ArrayList<NumberValue>();

	protected ArrayList<GeoLineND> selectedLines = new ArrayList<GeoLineND>();

	protected ArrayList<GeoDirectionND> selectedDirections = new ArrayList<GeoDirectionND>();

	protected ArrayList<GeoSegment> selectedSegments = new ArrayList<GeoSegment>();

	protected ArrayList<Region> selectedRegions = new ArrayList<Region>();

	protected ArrayList<Path> selectedPaths = new ArrayList<Path>();

	protected ArrayList<GeoConicND> selectedConicsND = new ArrayList<GeoConicND>();

	protected ArrayList<GeoImplicitPoly> selectedImplicitpoly = new ArrayList<GeoImplicitPoly>();

	protected ArrayList<GeoFunction> selectedFunctions = new ArrayList<GeoFunction>();

	protected ArrayList<GeoCurveCartesian> selectedCurves = new ArrayList<GeoCurveCartesian>();

	protected ArrayList<GeoVectorND> selectedVectors = new ArrayList<GeoVectorND>();

	protected ArrayList<GeoPolygon> selectedPolygons = new ArrayList<GeoPolygon>();

	protected ArrayList<GeoPolyLine> selectedPolyLines = new ArrayList<GeoPolyLine>();

	protected ArrayList<GeoElement> selectedGeos = new ArrayList<GeoElement>();

	protected ArrayList<GeoList> selectedLists = new ArrayList<GeoList>();

	protected Hits highlightedGeos = new Hits();

	protected ArrayList<GeoElement> justCreatedGeos = new ArrayList<GeoElement>();

	protected boolean selectionPreview = false;

	public boolean hideIntersection = false;

	public boolean previewFromResultedGeo = false;

	public GeoElement resultedGeo;

	protected boolean TEMPORARY_MODE = false;

	protected boolean DONT_CLEAR_SELECTION = false;

	protected boolean DRAGGING_OCCURED = false;

	protected boolean POINT_CREATED = false;

	protected boolean moveModeSelectionHandled;

	protected boolean highlightJustCreatedGeos = true;

	protected ArrayList<GeoElement> pastePreviewSelected = null;

	protected ArrayList<GeoElement> pastePreviewSelectedAndDependent;

	protected int mode;

	protected int oldMode;

	protected int moveMode = MOVE_NONE;

	protected Macro macro;

	protected Test[] macroInput;

	protected int DEFAULT_INITIAL_DELAY;

	protected boolean toggleModeChangedKernel = false;

	protected boolean altDown = false;

	protected GeoElement rotGeoElement;

	protected GeoElement rotStartGeo;

	protected GeoPoint rotationCenter;

	protected int polygonMode = POLYGON_NORMAL;

	protected double[] transformCoordsOffset = new double[2];

	protected boolean allowSelectionRectangleForTranslateByVector = true;

	protected int previousPointCapturing;

	protected ArrayList<GeoPointND> persistentStickyPointList = new ArrayList<GeoPointND>();

	protected App app;

	protected Kernel kernel;

	protected GPoint startLoc;

	public GPoint mouseLoc;

	protected GPoint lastMouseLoc;

	protected GPoint oldLoc = new GPoint();

	protected GPoint2D.Double startPoint = new GPoint2D.Double();

	protected GPoint2D.Double lineEndPoint = null;

	protected GPoint selectionStartPoint = new GPoint();

	protected ArrayList<Double> tempDependentPointX;

	protected ArrayList<Double> tempDependentPointY;

	protected boolean mouseIsOverLabel = false;

	protected EuclidianView view;
	
	// ==============================================
	// Pen

	public geogebra.common.euclidian.EuclidianPen pen;

	protected Hits handleAddSelectedArrayList = new Hits();

	protected boolean textfieldHasFocus = false;

	protected String sliderValue = null;

	private MyButton pressedButton;
	
	protected static final int MOVE_NONE = 101;
	protected static final int MOVE_POINT = 102;
	protected static final int MOVE_LINE = 103;
	protected static final int MOVE_CONIC = 104;
	protected static final int MOVE_VECTOR = 105;
	protected static final int MOVE_VECTOR_STARTPOINT = 205;
	public static final int MOVE_VIEW = 106;
	protected static final int MOVE_FUNCTION = 107;
	protected static final int MOVE_LABEL = 108;
	protected static final int MOVE_TEXT = 109;
	protected static final int MOVE_NUMERIC = 110;
	protected static final int MOVE_SLIDER = 111;
	protected static final int MOVE_IMAGE = 112;
	protected static final int MOVE_ROTATE = 113;
	protected static final int MOVE_DEPENDENT = 114;
	protected static final int MOVE_MULTIPLE_OBJECTS = 115;
	protected static final int MOVE_X_AXIS = 116;
	protected static final int MOVE_Y_AXIS = 117;
	protected static final int MOVE_BOOLEAN = 118;
	protected static final int MOVE_BUTTON = 119;
	public static final int MOVE_ROTATE_VIEW = 120;
	protected static final int MOVE_IMPLICITPOLY = 121;
	protected static final int MOVE_VECTOR_NO_GRID = 122;
	protected static final int MOVE_POINT_WITH_OFFSET = 123;
	protected static final int MOVE_FREEHAND = 124;

	public abstract void setApplication(App app);

	
	
	
	
	// ==============================================
	// Paste preview

	
	protected void updatePastePreviewPosition() {
		if (translationVec == null) {
			translationVec = new Coords(2);
		}
		translationVec.setX(xRW - startPoint.x);
		translationVec.setY(yRW - startPoint.y);
		startPoint.setLocation(xRW, yRW);
		GeoElement.moveObjects(pastePreviewSelected, translationVec,
				new Coords(xRW, yRW, 0), null);
	}

	public void setPastePreviewSelected() {
	
		// don't allow paste on top of another paste until its placed
		if (pastePreviewSelected != null) {
			while (!pastePreviewSelected.isEmpty()) {
				GeoElement geo = pastePreviewSelected.get(0);
				pastePreviewSelected.remove(geo);
				geo.remove();
			}
		} else {
			pastePreviewSelected = new ArrayList<GeoElement>();
		}
		pastePreviewSelectedAndDependent = new ArrayList<GeoElement>();
		pastePreviewSelectedAndDependent.addAll(app.getSelectedGeos());
	
		GeoElement geo;
		boolean firstMoveable = true;
		for (int i = 0; i < app.getSelectedGeos().size(); i++) {
			geo = app.getSelectedGeos().get(i);
			if (geo.isIndependent() && geo.isMoveable()) {
				pastePreviewSelected.add(geo);
				if (firstMoveable) {
					if (geo.isGeoPoint()) {
						startPoint.setLocation(((GeoPoint) geo).inhomX,
								((GeoPoint) geo).inhomY);
						firstMoveable = false;
					} else if (geo.isGeoText()) {
						if (((GeoText) geo).hasAbsoluteLocation()) {
							GeoPoint loc = (GeoPoint) ((GeoText) geo)
									.getStartPoint();
							startPoint.setLocation(loc.inhomX, loc.inhomY);
							firstMoveable = false;
						}
					} else if (geo.isGeoNumeric()) {
						if (!((GeoNumeric) geo).isAbsoluteScreenLocActive()) {
							startPoint.setLocation(
									((GeoNumeric) geo).getRealWorldLocX(),
									((GeoNumeric) geo).getRealWorldLocY());
							firstMoveable = false;
						} else {
							startPoint.setLocation(view
									.toRealWorldCoordX(((GeoNumeric) geo)
											.getAbsoluteScreenLocX()), view
									.toRealWorldCoordY(((GeoNumeric) geo)
											.getAbsoluteScreenLocY()));
							firstMoveable = false;
						}
					} else if (geo.isGeoImage()) {
						if (((GeoImage) geo).hasAbsoluteLocation()) {
							GeoPoint loc = ((GeoImage) geo).getStartPoints()[2];
							if (loc != null) { // top left defined
								// transformCoordsOffset[0]=loc.inhomX-xRW;
								// transformCoordsOffset[1]=loc.inhomY-yRW;
								startPoint.setLocation(loc.inhomX, loc.inhomY);
								firstMoveable = false;
							} else {
								loc = ((GeoImage) geo).getStartPoint();
								if (loc != null) { // bottom left defined
													// (default)
									// transformCoordsOffset[0]=loc.inhomX-xRW;
									// transformCoordsOffset[1]=loc.inhomY-yRW;
									startPoint.setLocation(loc.inhomX,
											loc.inhomY);
									firstMoveable = false;
								} else {
									loc = ((GeoImage) geo).getStartPoints()[1];
									if (loc != null) { // bottom right defined
										// transformCoordsOffset[0]=loc.inhomX-xRW;
										// transformCoordsOffset[1]=loc.inhomY-yRW;
										startPoint.setLocation(loc.inhomX,
												loc.inhomY);
										firstMoveable = false;
									}
								}
							}
						}
					} else if (geo.isGeoBoolean()) {
						// moveMode = MOVE_BOOLEAN;
						startPoint.setLocation(view
								.toRealWorldCoordX(((GeoBoolean) geo)
										.getAbsoluteScreenLocX()), view
								.toRealWorldCoordY(((GeoBoolean) geo)
										.getAbsoluteScreenLocY() + 20));
						firstMoveable = false;
					} else if (geo instanceof Furniture) {
						startPoint.setLocation(view
								.toRealWorldCoordX(((Furniture) geo)
										.getAbsoluteScreenLocX() - 5), view
								.toRealWorldCoordY(((Furniture) geo)
										.getAbsoluteScreenLocY() + 30));
						firstMoveable = false;
					}
				}
			}
		}
		if (firstMoveable) {
			startPoint.setLocation((view.getXmin() + view.getXmax()) / 2,
					(view.getYmin() + view.getYmax()) / 2);
		}
		if ((pastePreviewSelected != null) && !pastePreviewSelected.isEmpty()) {
			previousPointCapturing = view.getPointCapturingMode();
			view.setPointCapturing(EuclidianStyleConstants.POINT_CAPTURING_STICKY_POINTS);
	
			// remove moved points from sticky points temporarily
			for (int i = 0; i < pastePreviewSelectedAndDependent.size(); i++) {
				geo = pastePreviewSelectedAndDependent.get(i);
				if (geo instanceof GeoPointND) {
					if (view.getStickyPointList().contains(geo)) {
						view.getStickyPointList().remove(geo);
					}
				}
			}
			persistentStickyPointList = new ArrayList<GeoPointND>();
			persistentStickyPointList.addAll(view.getStickyPointList());
	
			if (mouseLoc != null) {
				transformCoords();
				updatePastePreviewPosition();
				kernel.notifyRepaint();
			}
		}
	}

	public boolean mayPaste() {
		if (pastePreviewSelected == null) {
			return true;
		}
		return pastePreviewSelected.isEmpty();
	}

	public void deletePastePreviewSelected() {
		if (pastePreviewSelected != null) {
			while (!pastePreviewSelected.isEmpty()) {
				GeoElement geo = pastePreviewSelected.get(0);
				pastePreviewSelected.remove(geo);
				geo.remove();
			}
			pastePreviewSelected = null;
		}
		if (pastePreviewSelectedAndDependent != null) {
			pastePreviewSelectedAndDependent = null;// new
													// ArrayList<GeoElement>();
		}
	}

	public void mergeStickyPointsAfterPaste() {
	
		for (int i = 0; i < pastePreviewSelected.size(); i++) {
			GeoElement geo = pastePreviewSelected.get(i);
			if (geo.isGeoPoint() && geo.isIndependent()) {
				for (int j = 0; j < persistentStickyPointList.size(); j++) {
					GeoPoint geo2 = (GeoPoint) persistentStickyPointList
							.get(j);
					if (Kernel.isEqual(geo2.getInhomX(),
							((GeoPoint) geo).getInhomX())
							&& Kernel.isEqual(geo2.getInhomY(),
									((GeoPoint) geo).getInhomY())) {
						geo.setEuclidianVisible(false);
						String geolabel = geo.getLabelSimple();
						kernel.getAlgebraProcessor().processAlgebraCommand(
								geo.getLabelSimple() + "="
										+ geo2.getLabelSimple(), false);
						kernel.lookupLabel(geolabel).setEuclidianVisible(false);
						kernel.lookupLabel(geolabel).updateRepaint();
						break;
					}
				}
			}
		}
	}

	public int getMode() {
		return mode;
	}
	
	public int getMoveMode() {
		return moveMode;
	}

	protected void endOfMode(int endMode) {
		switch (endMode) {
		case EuclidianConstants.MODE_RECORD_TO_SPREADSHEET:
			// just to be sure recordObject is set to null
			// usually this is already done at mouseRelease
			if (recordObject != null) {
				if (app.getTraceManager()
						.isTraceGeo(recordObject)) {
					app.getTraceManager().removeSpreadsheetTraceGeo(recordObject);
				}
				recordObject.setSelected(false);
				recordObject = null;
			}
			break;
	
		case EuclidianConstants.MODE_MOVE:
			deletePastePreviewSelected();
			break;
	
		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
			// take all selected objects and hide them
			Collection<GeoElement> coll = app.getSelectedGeos();
			Iterator<GeoElement> it = coll.iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				geo.setEuclidianVisible(false);
				geo.updateRepaint();
			}
			break;
	
		case EuclidianConstants.MODE_PEN:
		//case EuclidianConstants.MODE_PENCIL:
		case EuclidianConstants.MODE_FREEHAND_SHAPE:
			getPen().resetPenOffsets();
	
			view.setSelectionRectangle(null);
			break;
		}
	
		if (toggleModeChangedKernel) {
			app.storeUndoInfo();
		}
	}

	protected final void clearSelection(ArrayList<?> selectionList, boolean doUpdateSelection) {
	
		// unselect
		selectionList.clear();
		selectedGeos.clear();
		if (doUpdateSelection) {
			app.clearSelectedGeos();
		}
		view.repaintView();
	}

	protected final void clearSelection(ArrayList<?> selectionList) {
		clearSelection(selectionList, true);
	}

	protected Hits getRegionHits(Hits hits) {
		return hits.getRegionHits(tempArrayList);
	}

	protected GeoPointND getSingleIntersectionPoint(Hits hits) {
		if (hits.isEmpty() || (hits.size() < 2)) {
			return null;
		}
	
		GeoElement a = hits.get(0);
		GeoElement b = hits.get(1);
		
		return getSingleIntersectionPoint(a,b);
	}
	
	/**
	 * 
	 * @param a first geo
	 * @param b second geo
	 * @return single intersection points from geos a,b
	 */
	protected GeoPointND getSingleIntersectionPoint(GeoElement a, GeoElement b) {
	
		// first hit is a line
		if (a.isGeoLine()) {
			if (b.isGeoLine()) {
				if (!((GeoLine) a).linDep((GeoLine) b)) {
					return getAlgoDispatcher()
							.IntersectLines(null, (GeoLine) a, (GeoLine) b);
				}
				return null;
			} else if (b.isGeoConic()) {
				return IntersectLineConicSingle(null, (GeoLine) a,
						(GeoConic) b, xRW, yRW);
			} else if (b.isGeoFunctionable()) {
				// line and function
				GeoFunction f = ((GeoFunctionable) b).getGeoFunction();
				if (f.isPolynomialFunction(false)) {
					return IntersectPolynomialLineSingle(null, f,
							(GeoLine) a, xRW, yRW);
				}
				GeoPoint initPoint = new GeoPoint(
						kernel.getConstruction());
				initPoint.setCoords(xRW, yRW, 1.0);
				return getAlgoDispatcher().IntersectFunctionLine(null, f, (GeoLine) a,
						initPoint);
			} else {
				return null;
			}
		}
		// first hit is a conic
		else if (a.isGeoConic()) {
			if (b.isGeoLine()) {
				return IntersectLineConicSingle(null, (GeoLine) b,
						(GeoConic) a, xRW, yRW);
			} else if (b.isGeoConic() && !a.isEqual(b)) {
				return getAlgoDispatcher().IntersectConicsSingle(null, (GeoConic) a,
						(GeoConic) b, xRW, yRW);
			} else {
				return null;
			}
		}
		// first hit is a function
		else if (a.isGeoFunctionable()) {
			GeoFunction aFun = ((GeoFunctionable) a).getGeoFunction();
			if (b.isGeoLine()) {
				// line and function
				if (aFun.isPolynomialFunction(false)) {
					return IntersectPolynomialLineSingle(null, aFun,
							(GeoLine) b, xRW, yRW);
				}
				GeoPoint initPoint = new GeoPoint(
						kernel.getConstruction());
				initPoint.setCoords(xRW, yRW, 1.0);
				return getAlgoDispatcher().IntersectFunctionLine(null, aFun,
						(GeoLine) b, initPoint);
			} else if (b.isGeoFunctionable()) {
				GeoFunction bFun = ((GeoFunctionable) b).getGeoFunction();
				if (aFun.isPolynomialFunction(false)
						&& bFun.isPolynomialFunction(false)) {
					return getAlgoDispatcher().IntersectPolynomialsSingle(null, aFun, bFun,
							xRW, yRW);
				}
				GeoPoint initPoint = new GeoPoint(
						kernel.getConstruction());
				initPoint.setCoords(xRW, yRW, 1.0);
				return getAlgoDispatcher().IntersectFunctions(null, aFun, bFun,
						initPoint);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/***************************************************************************
	 * helper functions for selection sets
	 **************************************************************************/
	protected final GeoElement[] getSelectedGeos() {
		GeoElement[] ret = new GeoElement[selectedGeos.size()];
		int i = 0;
		Iterator<GeoElement> it = selectedGeos.iterator();
		while (it.hasNext()) {
			ret[i] = it.next();
			i++;
		}
		clearSelection(selectedGeos);
		return ret;
	}

	protected final void getSelectedPointsND(GeoPointND[] result) {
	
		for (int i = 0; i < selectedPoints.size(); i++) {
			result[i] = selectedPoints.get(i);
		}
		clearSelection(selectedPoints);
	
	}

	/**
	 * return selected points as ND points
	 * 
	 * @return selected points
	 */
	protected final GeoPointND[] getSelectedPointsND() {
	
		GeoPointND[] ret = new GeoPointND[selectedPoints.size()];
		getSelectedPointsND(ret);
	
		return ret;
	}

	protected final GeoPoint[] getSelectedPoints() {
	
		GeoPoint[] ret = new GeoPoint[selectedPoints.size()];
		getSelectedPointsND(ret);
	
		return ret;
	
	}

	protected final GeoNumeric[] getSelectedNumbers() {
		GeoNumeric[] ret = new GeoNumeric[selectedNumbers.size()];
		for (int i = 0; i < selectedNumbers.size(); i++) {
			ret[i] = selectedNumbers.get(i);
		}
		clearSelection(selectedNumbers);
		return ret;
	}

	protected final NumberValue[] getSelectedNumberValues() {
		NumberValue[] ret = new NumberValue[selectedNumberValues.size()];
		for (int i = 0; i < selectedNumberValues.size(); i++) {
			ret[i] = selectedNumberValues.get(i);
		}
		clearSelection(selectedNumberValues);
		return ret;
	}

	protected final GeoList[] getSelectedLists() {
		GeoList[] ret = new GeoList[selectedLists.size()];
		for (int i = 0; i < selectedLists.size(); i++) {
			ret[i] = selectedLists.get(i);
		}
		clearSelection(selectedLists);
		return ret;
	}

	protected final GeoPolygon[] getSelectedPolygons() {
		GeoPolygon[] ret = new GeoPolygon[selectedPolygons.size()];
		for (int i = 0; i < selectedPolygons.size(); i++) {
			ret[i] = selectedPolygons.get(i);
		}
		clearSelection(selectedPolygons);
		return ret;
	}

	protected final GeoPolyLine[] getSelectedPolyLines() {
		GeoPolyLine[] ret = new GeoPolyLine[selectedPolyLines.size()];
		for (int i = 0; i < selectedPolyLines.size(); i++) {
			ret[i] = selectedPolyLines.get(i);
		}
		clearSelection(selectedPolyLines);
		return ret;
	}

	protected final void getSelectedLinesND(GeoLineND[] lines) {
		int i = 0;
		Iterator<GeoLineND> it = selectedLines.iterator();
		while (it.hasNext()) {
			lines[i] = it.next();
			i++;
		}
		clearSelection(selectedLines);
	}

	protected final GeoLineND[] getSelectedLinesND() {
		GeoLineND[] lines = new GeoLineND[selectedLines.size()];
		getSelectedLinesND(lines);
	
		return lines;
	}

	protected final GeoLine[] getSelectedLines() {
		GeoLine[] lines = new GeoLine[selectedLines.size()];
		getSelectedLinesND(lines);
	
		return lines;
	}

	protected final void getSelectedSegmentsND(GeoSegmentND[] segments) {
		int i = 0;
		Iterator<GeoSegment> it = selectedSegments.iterator();
		while (it.hasNext()) {
			segments[i] = it.next();
			i++;
		}
		clearSelection(selectedSegments);
	}

	protected final GeoSegmentND[] getSelectedSegmentsND() {
		GeoSegmentND[] segments = new GeoSegmentND[selectedSegments.size()];
		getSelectedSegmentsND(segments);
	
		return segments;
	}

	protected final GeoSegment[] getSelectedSegments() {
		GeoSegment[] segments = new GeoSegment[selectedSegments.size()];
		getSelectedSegmentsND(segments);
	
		return segments;
	}

	protected final void getSelectedVectorsND(GeoVectorND[] vectors) {
		int i = 0;
		Iterator<GeoVectorND> it = selectedVectors.iterator();
		while (it.hasNext()) {
			vectors[i] = it.next();
			i++;
		}
		clearSelection(selectedVectors);
	}

	protected final GeoVectorND[] getSelectedVectorsND() {
		GeoVectorND[] vectors = new GeoVectorND[selectedVectors.size()];
		getSelectedVectorsND(vectors);
	
		return vectors;
	}

	protected final GeoVector[] getSelectedVectors() {
		GeoVector[] vectors = new GeoVector[selectedVectors.size()];
		getSelectedVectorsND(vectors);
	
		return vectors;
	}

	protected final GeoConic[] getSelectedConics() {
		GeoConic[] conics = new GeoConic[selectedConicsND.size()];
		int i = 0;
		Iterator<GeoConicND> it = selectedConicsND.iterator();
		while (it.hasNext()) {
			conics[i] = (GeoConic) it.next();
			i++;
		}
		clearSelection(selectedConicsND);
		return conics;
	}

	protected final GeoConic[] getSelectedCircles() {
		GeoConic[] circles = new GeoConic[selectedConicsND.size()];
		int i = 0;
		Iterator<GeoConicND> it = selectedConicsND.iterator();
		while (it.hasNext()) {
			GeoConicND c = it.next();
			if (c.isCircle()) {
				circles[i] = (GeoConic) c;
				i++;
			}
		}
		clearSelection(selectedConicsND);
		return circles;
	}
	
	protected final GeoConicND[] getSelectedCirclesND() {
		GeoConicND[] circles = new GeoConicND[selectedConicsND.size()];
		int i = 0;
		Iterator<GeoConicND> it = selectedConicsND.iterator();
		while (it.hasNext()) {
			GeoConicND c = it.next();
			if (c.isCircle()) {
				circles[i] = c;
				i++;
			}
		}
		clearSelection(selectedConicsND);
		return circles;
	}
	
	protected final GeoConicND[] getSelectedConicsND() {
		GeoConicND[] conics = new GeoConicND[selectedConicsND.size()];
		int i = 0;
		Iterator<GeoConicND> it = selectedConicsND.iterator();
		while (it.hasNext()) {
			conics[i] = it.next();
			i++;
		}
		clearSelection(selectedConicsND);
		return conics;
	}

	protected final GeoDirectionND[] getSelectedDirections() {
		GeoDirectionND[] directions = new GeoDirectionND[selectedDirections
				.size()];
		int i = 0;
		Iterator<GeoDirectionND> it = selectedDirections.iterator();
		while (it.hasNext()) {
			directions[i] = it.next();
			i++;
		}
		clearSelection(selectedDirections);
		return directions;
	}

	protected final Region[] getSelectedRegions() {
		Region[] regions = new Region[selectedRegions.size()];
		int i = 0;
		Iterator<Region> it = selectedRegions.iterator();
		while (it.hasNext()) {
			regions[i] = it.next();
			i++;
		}
		clearSelection(selectedRegions);
		return regions;
	}

	protected final Path[] getSelectedPaths() {
		Path[] paths = new Path[selectedPaths.size()];
		int i = 0;
		Iterator<Path> it = selectedPaths.iterator();
		while (it.hasNext()) {
			paths[i] = it.next();
			i++;
		}
		clearSelection(selectedPaths);
		return paths;
	}

	protected final GeoImplicitPoly[] getSelectedImplicitpoly() {
		GeoImplicitPoly[] implicitPoly = new GeoImplicitPoly[selectedImplicitpoly
				.size()];
		int i = 0;
		Iterator<GeoImplicitPoly> it = selectedImplicitpoly.iterator();
		while (it.hasNext()) {
			implicitPoly[i] = it.next();
			i++;
		}
		clearSelection(selectedImplicitpoly);
		return implicitPoly;
	}

	protected final GeoFunction[] getSelectedFunctions() {
		GeoFunction[] functions = new GeoFunction[selectedFunctions.size()];
		int i = 0;
		Iterator<GeoFunction> it = selectedFunctions.iterator();
		while (it.hasNext()) {
			functions[i] = it.next();
			i++;
		}
		clearSelection(selectedFunctions);
		return functions;
	}

	protected final GeoCurveCartesian[] getSelectedCurves() {
		GeoCurveCartesian[] curves = new GeoCurveCartesian[selectedCurves
				.size()];
		int i = 0;
		Iterator<GeoCurveCartesian> it = selectedCurves.iterator();
		while (it.hasNext()) {
			curves[i] = it.next();
			i++;
		}
		clearSelection(selectedCurves);
		return curves;
	}

	/***************************************************************************
	 * mode implementations
	 * 
	 * the following methods return true if a factory method of the kernel was
	 * called
	 **************************************************************************/
	protected boolean allowPointCreation() {
		return (mode == EuclidianConstants.MODE_POINT)
				|| (mode == EuclidianConstants.MODE_POINT_ON_OBJECT)
				|| (mode == EuclidianConstants.MODE_COMPLEX_NUMBER)
				|| app.isOnTheFlyPointCreationActive();
	}

	/**
	 * @param forPreviewable in 3D we might want a preview 
	 */
	protected GeoPointND createNewPoint(boolean forPreviewable, boolean complex) {
		
		checkZooming(forPreviewable); 
		
		GeoPointND ret = getAlgoDispatcher().Point(null,
				Kernel.checkDecimalFraction(xRW),
				Kernel.checkDecimalFraction(yRW), complex);
		return ret;
	}

	protected GeoPointND createNewPoint(boolean forPreviewable, Path path, boolean complex) {
		return createNewPoint(forPreviewable, path,
				Kernel.checkDecimalFraction(xRW),
				Kernel.checkDecimalFraction(yRW), 0, complex);
	}

	protected GeoPointND createNewPoint(boolean forPreviewable, Region region, boolean complex) {
		return createNewPoint(forPreviewable, region,
				Kernel.checkDecimalFraction(xRW),
				Kernel.checkDecimalFraction(yRW), 0, complex);
	}

	protected GeoPointND createNewPoint2D(boolean forPreviewable, Path path, double x,
			double y, boolean complex) {
		checkZooming(forPreviewable); 
		
				return getAlgoDispatcher().Point(null, path, x, y, !forPreviewable, complex);
			}

	protected final GeoPointND createNewPoint2D(boolean forPreviewable, Region region, double x,
			double y, boolean complex) {
		checkZooming(forPreviewable); 
		
				GeoPointND ret = getAlgoDispatcher().PointIn(null, region, x, y, !forPreviewable, complex);
				return ret;
			}

	public GeoPointND createNewPoint(boolean forPreviewable, Region region, double x,
			double y, double z, boolean complex) {
			
				if (region.toGeoElement().isGeoElement3D()) {
					checkZooming(forPreviewable); 
					
					return kernel.getManager3D().Point3DIn(null, region,
							new Coords(x, y, z, 1), !forPreviewable);
				}
				return createNewPoint2D(forPreviewable, region, x, y, complex);
			}

	public GeoPointND createNewPoint(boolean forPreviewable, Path path, double x,
			double y, double z, boolean complex) {
			
				if (path.toGeoElement().isGeoElement3D()) {
					checkZooming(forPreviewable); 
					
					return kernel.getManager3D().Point3D(null, path, x, y, z,
							!forPreviewable);
				}
				return createNewPoint2D(forPreviewable, path, x, y, complex);
			}

	public void setKernel(Kernel kernel) {
		this.kernel = kernel;
	}

	public Kernel getKernel() {
		return kernel;
	}

	public void clearJustCreatedGeos() {
		justCreatedGeos.clear();
		app.updateStyleBars();
	
		if (app.isUsingFullGui() && app.getGuiManager() != null) {
			app.getGuiManager().updateMenubarSelection();
		}
	}

	public ArrayList<GeoElement> getJustCreatedGeos() {
		return justCreatedGeos;
	}

	public void memorizeJustCreatedGeos(ArrayList<GeoElement> geos) {
		justCreatedGeos.clear();
		justCreatedGeos.addAll(geos);
		if (app.isUsingFullGui() && app.getGuiManager() != null) {
			app.updateStyleBars();
			app.getGuiManager().updateMenubarSelection();
		}
	}

	public void memorizeJustCreatedGeos(GeoElement[] geos) {
		justCreatedGeos.clear();
		for (int i = 0; i < geos.length; i++) {
			if (geos[i] != null) {
				justCreatedGeos.add(geos[i]);
			}
		}
		if (app.isUsingFullGui() && app.getGuiManager() != null) {
			app.updateStyleBars();
			app.getGuiManager().updateMenubarSelection();
		}
	}

	protected final void setHighlightedGeos(boolean highlight) {
		GeoElement geo;
		Iterator<GeoElement> it = highlightedGeos.iterator();
		while (it.hasNext()) {
			geo = it.next();
			geo.setHighlighted(highlight);
		}
	}

	public void doSingleHighlighting(GeoElement geo) {
		if (geo == null) {
			return;
		}
	
		if (highlightedGeos.size() > 0) {
			setHighlightedGeos(false);
		}
	
		highlightedGeos.add(geo);
		geo.setHighlighted(true);
		kernel.notifyRepaint();
	}

	/**
	 * 
	 * @return true if the mouse is over a label
	 */
	public boolean mouseIsOverLabel() {
		return mouseIsOverLabel;
	}

	/**
	 * Removes parent points of segments, rays, polygons, etc. from selGeos that
	 * are not necessary for transformations of these objects.
	 */
	protected void removeParentPoints(ArrayList<GeoElement> selGeos) {
		tempArrayList.clear();
		tempArrayList.addAll(selGeos);
	
		// remove parent points
		for (int i = 0; i < selGeos.size(); i++) {
			GeoElement geo = selGeos.get(i);
	
			switch (geo.getGeoClassType()) {
			case SEGMENT:
			case RAY:
				// remove start and end point of segment
				GeoLine line = (GeoLine) geo;
				tempArrayList.remove(line.getStartPoint());
				tempArrayList.remove(line.getEndPoint());
				break;
	
			case CONICPART:
				GeoConicPart cp = (GeoConicPart) geo;
				ArrayList<GeoPoint> ip = cp.getParentAlgorithm()
						.getInputPoints();
				tempArrayList.removeAll(ip);
				break;
	
			case POLYGON:
				// remove points and segments of poly
				GeoPolygon poly = (GeoPolygon) geo;
				GeoPointND[] points = poly.getPoints();
				for (int k = 0; k < points.length; k++) {
					tempArrayList.remove(points[k]);
				}
				GeoSegmentND[] segs = poly.getSegments();
				for (int k = 0; k < segs.length; k++) {
					tempArrayList.remove(segs[k]);
				}
				break;
	
			case PENSTROKE:
			case POLYLINE:
				// remove points and segments of poly
				GeoPolyLine polyl = (GeoPolyLine) geo;
				points = polyl.getPoints();
				for (int k = 0; k < points.length; k++) {
					tempArrayList.remove(points[k]);
				}
				break;
			}
		}
	
		selGeos.clear();
		selGeos.addAll(tempArrayList);
	}

	@SuppressWarnings("unchecked")
	protected final int addToSelectionList(@SuppressWarnings("rawtypes") ArrayList selectionList, GeoElement geo,
			int max) {
				if (geo == null) {
					return 0;
				}
			
				int ret = 0;
				if (selectionList.contains(geo)) { // remove from selection
					selectionList.remove(geo);
					if (!selectionList.equals(selectedGeos)
							) {
						selectedGeos.remove(geo);
					}
					ret = -1;
				} else { // new element: add to selection
					if (selectionList.size() < max) {
						selectionList.add(geo);
						if (!selectionList.equals(selectedGeos)) {
							selectedGeos.add(geo);
						}
						ret = 1;
					}
				}
				if (ret != 0) {
					app.toggleSelectedGeo(geo);
				}
				return ret;
			}

	protected final int addToHighlightedList(ArrayList<?> selectionList, ArrayList<GeoElement> geos,
			int max) {
			
				if (geos == null) {
					return 0;
				}
			
				GeoElement geo;
				int ret = 0;
				for (int i = 0; i < geos.size(); i++) {
					geo = geos.get(i);
					if (selectionList.contains(geo)) {
						ret = (ret == 1) ? 1 : -1;
					} else {
						if (selectionList.size() < max) {
							highlightedGeos.add(geo); // add hit
							ret = 1;
						}
					}
				}
				return ret;
			}

	protected GeoElement chooseGeo(ArrayList<GeoElement> geos, boolean includeFixed) {
		if (geos == null) {
			return null;
		}
	
		if (geos.size() > 1) {
			removeAxes(geos);
		}
	
		GeoElement ret = null;
		GeoElement retFree = null;
		GeoElement retPath = null;
		GeoElement retIndex = null;
		GeoElement retSegment = null;
	
		switch (geos.size()) {
		case 0:
			break;
	
		case 1:
			ret = geos.get(0);
	
			if (!includeFixed && ret.isFixed()) {
				return null;
			}
	
			break;
	
		default:
	
			int maxLayer = -1;
	
			int layerCount = 0;
	
			// work out max layer, and
			// count no of objects in max layer
			for (int i = 0; i < geos.size(); i++) {
				GeoElement geo = (geos.get(i));
				int layer = geo.getLayer();
	
				if ((layer > maxLayer) && (includeFixed || !geo.isFixed())) {
					maxLayer = layer;
					layerCount = 1;
					ret = geo;
				} else if (layer == maxLayer) {
					layerCount++;
				}
	
			}
	
			// Application.debug("maxLayer"+maxLayer);
			// Application.debug("layerCount"+layerCount);
	
			// only one object in top layer, return it.
			if (layerCount == 1) {
				return ret;
			}
	
			int pointCount = 0;
			int freePointCount = 0;
			int pointOnPathCount = 0;
			int segmentCount = 0;
			// int polygonCount = 0;
			int minIndex = Integer.MAX_VALUE;
	
			// count no of points in top layer
			for (int i = 0; i < geos.size(); i++) {
				GeoElement geo = (geos.get(i));
	
				if (geo.isGeoPoint() && (geo.getLayer() == maxLayer)
						&& (includeFixed || !geo.isFixed())) {
					pointCount++;
					ret = geo;
	
					// find point with the lowest construction index
					// changed from highest so that tessellation works
					// eg two points like (a + x(A), b + y(A))
					// we want to drag the older one
					int index = geo.getConstructionIndex();
					if (index < minIndex) {
						minIndex = index;
						retIndex = geo;
					}
	
					// find point-on-path/region with the highest construction
					// index
					if (((GeoPointND) geo).isPointOnPath()
							|| ((GeoPointND) geo).isPointInRegion()) {
						pointOnPathCount++;
						if (retPath == null) {
							retPath = geo;
						} else {
							if (geo.getConstructionIndex() > retPath
									.getConstructionIndex()) {
								retPath = geo;
							}
						}
					}
	
					// find free point with the highest construction index
					if (geo.isIndependent()) {
						freePointCount++;
						if (retFree == null) {
							retFree = geo;
						} else {
							if (geo.getConstructionIndex() > retFree
									.getConstructionIndex()) {
								retFree = geo;
							}
						}
					}
				}
			}
			// Application.debug("pointOnPathCount"+pointOnPathCount);
			// Application.debug("freePointCount"+freePointCount);
			// Application.debug("pointCount"+pointCount);
	
			// return point-on-path with highest index
			if (pointOnPathCount > 0) {
				return retPath;
			}
	
			// return free-point with highest index
			if (freePointCount > 0) {
				return retFree;
			}
	
			// only one point in top layer, return it
			if (pointCount == 1) {
				return ret;
			}
	
			// just return the most recently created point
			if (pointCount > 1) {
				return retIndex;
			}
	
			/*
			 * try { throw new Exception("choose"); } catch (Exception e) {
			 * e.printStackTrace();
			 * 
			 * }
			 */
	
			boolean allFixed = false;
	
			// remove fixed objects (if there are some not fixed)
			if (!includeFixed && (geos.size() > 1)) {
	
				allFixed = true;
				for (int i = 0; i < geos.size(); i++) {
					if (!geos.get(i).isFixed()) {
						allFixed = false;
					}
				}
	
				if (!allFixed) {
					for (int i = geos.size() - 1; i >= 0; i--) {
						GeoElement geo = geos.get(i);
						if (geo.isFixed()) {
							geos.remove(i);
						}
					}
				}
	
				if (geos.size() == 1) {
					return geos.get(0);
				}
			}
	
			// int maxPolygonLayer = 0;
			// count segments and polygons
			for (int i = 0; i < geos.size(); i++) {
				GeoElement geo = (geos.get(i));
	
				if (geo.isGeoSegment()) {
					segmentCount++;
					if (retSegment == null) {
						retSegment = geo;
					} else {
	
						// select Segment with lowest layer (& construction
						// index)
						if ((retSegment.getLayer() < geo.getLayer())
								|| ((retSegment.getLayer() == geo.getLayer()) && (retSegment
										.getConstructionIndex() > geo
										.getConstructionIndex()))) {
							retSegment = geo;
	
						}
					}
					// } else if (geo.isGeoPolygon()) {
					// polygonCount++;
					// if (geo.getLayer() > maxPolygonLayer) maxPolygonLayer =
					// geo.getLayer();
				}
			}
	
			// check for edge of polygon being selected (priority over polygon
			// itself)
			// if (segmentCount == 1 && (segmentCount + polygonCount ==
			// geos.size())) {
			// if (retSegment.getLayer() >= maxPolygonLayer) return retSegment;
			// }
	
			// give segments priority over eg Polygons, Lines
			// that they might be drawn on top of
			if (segmentCount > 0) {
				return retSegment;
			}
	
			// don't want a popup in this case
			// eg multiple fixed images from Pen Tool
			if (!includeFixed && allFixed) {
				return null;
			}
	
			/*
			 * no points selected, multiple objects selected // popup a menu to
			 * choose from ToolTipManager ttm = ToolTipManager.sharedInstance();
			 * ttm.setEnabled(false); ListDialog dialog = new
			 * ListDialog((EuclidianView) view, geos, null); if
			 * (app.areChooserPopupsEnabled()) ret = dialog.showDialog((EuclidianView)
			 * view, mouseLoc); ttm.setEnabled(true);
			 */
	
			// now just choose geo with highest drawing priority:
			int maxIndex = 0;
			long maxDrawingPriority = Integer.MIN_VALUE;
			
			for (int i = 0 ; i < geos.size() ; i++) {
				if (geos.get(i).getDrawingPriority() > maxDrawingPriority) {
					maxDrawingPriority = geos.get(i).getDrawingPriority();
					maxIndex = i;
				}
			}
			ret = geos.get(maxIndex);
		}
		return ret;
	
	}

	/**
	 * Shows dialog to choose one object out of hits[] that is an instance of
	 * specified class (note: subclasses are included)
	 * 
	 */
	protected GeoElement chooseGeo(Hits hits, Test geoclass) {
		return chooseGeo(hits.getHits(geoclass, tempArrayList), true);
	}

	/**
	 * selectionList may only contain max objects a choose dialog will be shown
	 * if not all objects can be added
	 * 
	 * @param geos
	 *            a clone of the to-be-added list
	 * @param addMoreThanOneAllowed
	 *            it's possible to add several objects without choosing
	 */
	protected final int addToSelectionList(ArrayList<?> selectionList, ArrayList<GeoElement> geos,
			int max, boolean addMoreThanOneAllowed, boolean tryDeselect) {
			
				if (geos == null) {
					return 0;
					// GeoElement geo;
				}
			
				// ONLY ONE ELEMENT IN THE EFFECTIVE HITS
				if (tryDeselect && (geos.size() == 1)) {
					// select or deselect it
					return addToSelectionList(selectionList, geos.get(0), max);
				}
			
				// SEVERAL ELEMENTS
				// here none of the selected geos should be removed
			
				// we don't want to add repeated elements
				geos.removeAll(selectionList);
				// too many objects -> choose one
				if (!addMoreThanOneAllowed
						|| ((geos.size() + selectionList.size()) > max)) {
					// Application.printStacktrace(geos.toString());
					return addToSelectionList(selectionList, chooseGeo(geos, true), max);
				}
			
				// already selected objects -> choose one
				boolean contained = false;
				for (int i = 0; i < geos.size(); i++) {
					if (selectionList.contains(geos.get(i))) {
						contained = true;
					}
				}
				if (contained) {
					return addToSelectionList(selectionList, chooseGeo(geos, true), max);
				}
			
				// add all objects to list
				int count = 0;
				for (int i = 0; i < geos.size(); i++) {
					count += addToSelectionList(selectionList, geos.get(i), max);
				}
				return count;
			}

	protected final int selGeos() {
		return selectedGeos.size();
	}

	protected final int selPoints() {
		return selectedPoints.size();
	}

	protected final int selNumbers() {
		return selectedNumbers.size();
	}

	protected final int selNumberValues() {
		return selectedNumberValues.size();
	}

	protected final int selLists() {
		return selectedLists.size();
	}

	protected final int selPolyLines() {
		return selectedPolyLines.size();
	}

	protected final int selPolygons() {
		return selectedPolygons.size();
	}

	protected final int selLines() {
		return selectedLines.size();
	}

	protected final int selDirections() {
		return selectedDirections.size();
	}

	protected final int selSegments() {
		return selectedSegments.size();
	}

	protected final int selVectors() {
		return selectedVectors.size();
	}

	protected final int selConics() {
		return selectedConicsND.size();
	}

	protected final int selPaths() {
		return selectedPaths.size();
	}

	protected final int selRegions() {
		return selectedRegions.size();
	}

	protected final int selImplicitpoly() {
		return selectedImplicitpoly.size();
	}

	protected final int selFunctions() {
		return selectedFunctions.size();
	}

	protected final int selCurves() {
		return selectedCurves.size();
	}

	protected int handleAddSelected(Hits hits, int max, boolean addMore,
			ArrayList<?> list, Test geoClass) {
			
				if (selectionPreview) {
					return addToHighlightedList(list,
							hits.getHits(geoClass, handleAddSelectedArrayList), max);
				}
				return addToSelectionList(list,
						hits.getHits(geoClass, handleAddSelectedArrayList), max,
						addMore, hits.size() == 1);
			}

	protected int handleAddSelectedRegions(Hits hits, int max,
			boolean addMore, ArrayList<?> list) {
				if (selectionPreview) {
					return addToHighlightedList(list,
							hits.getRegionHits(handleAddSelectedArrayList), max);
				}
				return addToSelectionList(list,
						hits.getRegionHits(handleAddSelectedArrayList), max,
						addMore, hits.size() == 1);
			}

	protected final int addSelectedGeo(Hits hits, int max, boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				selectedGeos, Test.GEOELEMENT);
	}

	protected final int addSelectedPoint(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
				return handleAddSelected(hits, max, addMoreThanOneAllowed,
						selectedPoints, Test.GEOPOINTND);
			}

	public final int addSelectedNumeric(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
				return handleAddSelected(hits, max, addMoreThanOneAllowed,
						selectedNumbers, Test.GEONUMERIC);
			}

	public final int addSelectedNumberValue(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
				return handleAddSelected(hits, max, addMoreThanOneAllowed,
						selectedNumberValues, Test.NUMBERVALUE);
			}

	protected final int addSelectedLine(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
				return handleAddSelected(hits, max, addMoreThanOneAllowed,
						selectedLines, Test.GEOLINEND);
			}

	protected final int addSelectedSegment(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
				return handleAddSelected(hits, max, addMoreThanOneAllowed,
						selectedSegments, Test.GEOSEGMENTND);
			}

	protected final int addSelectedVector(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
				return addSelectedVector(hits, max, addMoreThanOneAllowed,
						Test.GEOVECTORND);
			}

	protected final int addSelectedVector(Hits hits, int max,
			boolean addMoreThanOneAllowed, Test geoClass) {
				return handleAddSelected(hits, max, addMoreThanOneAllowed,
						selectedVectors, geoClass);
			}

	protected final int addSelectedPath(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
				return handleAddSelected(hits, max, addMoreThanOneAllowed,
						selectedPaths, Test.PATH);
			}

	protected final int addSelectedRegion(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
				return handleAddSelectedRegions(hits, max, addMoreThanOneAllowed,
						selectedRegions);
			}

	protected final int addSelectedImplicitpoly(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
				return handleAddSelected(hits, max, addMoreThanOneAllowed,
						selectedImplicitpoly, Test.GEOIMPLICITPOLY);
			}

	protected final int addSelectedPolygon(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
				return handleAddSelected(hits, max, addMoreThanOneAllowed,
						selectedPolygons, Test.GEOPOLYGON);
			}

	protected final int addSelectedPolyLine(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
				return handleAddSelected(hits, max, addMoreThanOneAllowed,
						selectedPolyLines, Test.GEOPOLYLINE);
			}

	protected final int addSelectedList(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
				return handleAddSelected(hits, max, addMoreThanOneAllowed,
						selectedLists, Test.GEOLIST);
			}

	protected final int addSelectedDirection(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
				return handleAddSelected(hits, max, addMoreThanOneAllowed,
						selectedDirections, Test.GEODIRECTIONND);
			}

	protected final int addSelectedCircle(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
				ArrayList<GeoConic> selectedCircles = new ArrayList<GeoConic>();
				for (Object c : selectedConicsND) {
					if (((GeoConic) c).isCircle()) {
						selectedCircles.add((GeoConic) c);
					}
				}
				return handleAddSelected(hits, max, addMoreThanOneAllowed,
						selectedCircles, Test.GEOCONIC);
			}

	protected final int addSelectedConic(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
				return handleAddSelected(hits, max, addMoreThanOneAllowed,
						selectedConicsND, Test.GEOCONICND);
			}

	protected final int addSelectedFunction(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
				return handleAddSelected(hits, max, addMoreThanOneAllowed,
						selectedFunctions, Test.GEOFUNCTION);
			}

	protected final int addSelectedCurve(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
				return handleAddSelected(hits, max, addMoreThanOneAllowed,
						selectedCurves, Test.GEOCURVECARTESIAN);
			}

	/** only used in 3D 
	 * @param sourcePoint */
	protected void createNewPoint(GeoPointND sourcePoint) {
		//3D
	}

	/** only used in 3D 
	 * @param intersectionPoint */
	protected void createNewPointIntersection(GeoPointND intersectionPoint) {
		//3D
	}

	protected final GeoElement[] join(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// points needed
		addSelectedPoint(hits, 2, false);
		// Application.debug("addSelectedPoint : "+hits+"\nselectedPoints = "+selectedPoints);
		if (selPoints() == 2) {
			// fetch the two selected points
			return join();
		}
		return null;
	}

	protected GeoElement[] join() {
		GeoPointND[] points = getSelectedPointsND();
		GeoElement[] ret = { null };
		if (((GeoElement) points[0]).isGeoElement3D()
				|| ((GeoElement) points[1]).isGeoElement3D()) {
			ret[0] = getKernel().getManager3D().Line3D(null, points[0],
					points[1]);
		} else {
			ret[0] = getAlgoDispatcher().Line(null, (GeoPoint) points[0],
					(GeoPoint) points[1]);
		}
		return ret;
	}

	protected void updateMovedGeoPoint(GeoPointND point) {
		movedGeoPoint = point;
	}

	protected GeoElement[] ray() {
		GeoPointND[] points = getSelectedPointsND();
		GeoElement[] ret = { null };
		if (((GeoElement) points[0]).isGeoElement3D()
				|| ((GeoElement) points[1]).isGeoElement3D()) {
			ret[0] = getKernel().getManager3D().Ray3D(null,
					points[0], points[1]).toGeoElement();
		} else {
			ret[0] = getAlgoDispatcher().Ray(null, (GeoPoint) points[0],
					(GeoPoint) points[1]);
		}
		return ret;
	}

	protected final GeoElement[] segment(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
			return segment();
			/*
			 * GeoPoint[] points = getSelectedPoints(); kernel.Segment(null,
			 * points[0], points[1]);
			 */
		}
		return null;
	}

	protected GeoElement[] segment() {
		GeoPointND[] points = getSelectedPointsND();
		GeoElement[] ret = { null };
		if (((GeoElement) points[0]).isGeoElement3D()
				|| ((GeoElement) points[1]).isGeoElement3D()) {
			ret[0] = (GeoElement) getKernel().getManager3D().Segment3D(null,
					points[0], points[1]);
		} else {
			ret[0] = getAlgoDispatcher().Segment(null, (GeoPoint) points[0],
					(GeoPoint) points[1]);
		}
		return ret;
	}

	protected final GeoElement[] vector(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
	
			GeoPointND[] points = getSelectedPointsND();
			return new GeoElement[] { vector(points[0], points[1]) };
		}
		return null;
	}

	protected GeoElement vector(GeoPointND a, GeoPointND b) {
		checkZooming(); 
		
		if (((GeoElement) a).isGeoElement3D()
				|| ((GeoElement) b).isGeoElement3D()) {
			return kernel.getManager3D().Vector3D(null, a, b);
		}
		return getAlgoDispatcher().Vector(null, (GeoPoint) a, (GeoPoint) b);
	}

	protected final GeoElement[] ray(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
			/*
			 * GeoPoint[] points = getSelectedPoints(); kernel.Ray(null,
			 * points[0], points[1]);
			 */
			return ray();
		}
	
		return null;
	}

	protected final GeoElement[] polygon(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
		
		if (polygonMode == POLYGON_VECTOR) {
			addSelectedPolygon(hits, 1, false);
			//AbstractApplication.debug(selGeos()+"");
			if (selPolygons() == 1) {
				GeoPolygon[] poly = getSelectedPolygons();
				
				GeoPoint[] points = (GeoPoint[]) poly[0].getPoints();
				
				GeoPoint[] pointsCopy = new GeoPoint[points.length];

				
				// make a free copy of all points
				for (int i = 0 ; i < points.length ; i++) {
					pointsCopy[i] = points[i].copy();
					pointsCopy[i].setLabel(null);
					//points[i] = new GeoPoint(kernel.getConstruction(), null, points[i].inhomX, points[i].inhomY, 1.0);
				}
				
				checkZooming(); 
				
				GeoElement[] ret = kernel.VectorPolygon(null, pointsCopy);
				
				// offset the copy slightly
				double offset = view.toRealWorldCoordX(view.getWidth()) / 15;
				
				((GeoPolygon) ret[0]).getPoints()[0].setCoords(pointsCopy[0].inhomX + offset, pointsCopy[0].inhomY - offset, 1.0);
				((GeoPolygon) ret[0]).getPoints()[0].updateRepaint();
				
				return ret;

			}
		} else 	if (polygonMode == POLYGON_RIGID) {
			addSelectedPolygon(hits, 1, false);
			//AbstractApplication.debug(selGeos()+"");
			if (selPolygons() == 1) {
				GeoPolygon[] poly = getSelectedPolygons();
								
				checkZooming(); 
				
				// offset the copy slightly
				double offset = view.toRealWorldCoordX(view.getWidth()) / 15;

				GeoElement[] ret = kernel.RigidPolygon(poly[0], offset, -offset);
				
				
				
				return ret;

			}
		}
			
		// if the first point is clicked again, we are finished
		if (selPoints() > 2) {
			// check if first point was clicked again
			boolean finished = !selectionPreview
					&& hits.contains(selectedPoints.get(0));
			if (finished) {
				// build polygon
				return polygon();
				// kernel.Polygon(null, getSelectedPoints());
			}
		}
	
		// points needed
		if (((polygonMode == POLYGON_RIGID) || (polygonMode == POLYGON_VECTOR))
				&& (selPoints() > 0)) { // only want free points withput
										// children for rigid polys (apart from
										// first)
			GeoElement geo = chooseGeo(hits, false);
			if ((geo == null) || !geo.isGeoPoint() || !geo.isIndependent()
					|| geo.hasChildren()) {
				// addToSelectionList(selectedPoints, geo,
				// GeoPolygon.POLYGON_MAX_POINTS);
				return null;
			}
		}
		addSelectedPoint(hits, GeoPolygon.POLYGON_MAX_POINTS, false);
		return null;
	}

	protected final GeoElement[] polyline(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// if the first point is clicked again, we are finished
		if (selPoints() > 2) {
			// check if first point was clicked again
			boolean finished = !selectionPreview
					&& hits.contains(selectedPoints.get(0));
			if (finished) {
				// build polygon
				checkZooming(); 
				
				return kernel.PolyLine(null, getSelectedPoints(), false);
			}
		}
	
		// points needed
		addSelectedPoint(hits, GeoPolyLine.POLYLINE_MAX_POINTS, false);
		return null;
	}

	protected GeoElement[] polygon() {
		checkZooming(); 
		
		if (polygonMode == POLYGON_RIGID) {
			GeoElement[] ret = { null };
			GeoElement[] ret0 = kernel.RigidPolygon(null, getSelectedPoints());
			if (ret0 != null) {
				ret[0] = ret0[0];
			}
			return ret;
		} else if (polygonMode == POLYGON_VECTOR) {
			GeoElement[] ret = { null };
			GeoElement[] ret0 = kernel.VectorPolygon(null, getSelectedPoints());
			if (ret0 != null) {
				ret[0] = ret0[0];
			}
			return ret;
		} else {
			// check if there is a 3D point
			GeoPointND[] pointsND = getSelectedPointsND();
			GeoPoint[] points = new GeoPoint[pointsND.length];
			boolean point3D = false;
			for (int i = 0; (i < pointsND.length) && !point3D; i++) {
				if (((GeoElement) pointsND[i]).isGeoElement3D()) {
					point3D = true;
				} else {
					points[i] = (GeoPoint) pointsND[i];
				}
			}
			if (point3D) {
				GeoElement[] ret = { null };
				GeoElement[] ret0 = kernel.getManager3D().Polygon3D(null,
						pointsND);
				if (ret0 != null) {
					ret[0] = ret0[0];
				}
				return ret;
			}
			GeoElement[] ret = { null };
			GeoElement[] ret0 = kernel.Polygon(null, points);
			if (ret0 != null) {
				ret[0] = ret0[0];
			}
			return ret;
		}
	}

	protected GeoElement[] intersect(Hits intersectHits) {
		// Application.debug(selectedLines);
		Hits hits = intersectHits;
		// obscure bug: intersection of x=0 and (x-1)^2+(y-1)^=1 can intersect
		// x=0 and y axis sometimes
		if (hits.size() > 2) {
			removeAxes(hits);
		}
	
		if (hits.isEmpty()) {
			return null;
		}
	
		// when two objects are selected at once then only one single
		// intersection point should be created
		boolean singlePointWanted = selGeos() == 0;
	
		// check how many interesting hits we have
		if (!selectionPreview && (hits.size() > (2 - selGeos()))) {
			Hits goodHits = new Hits();
			// goodHits.add(selectedGeos);
			hits.getHits(Test.GEOLINE, tempArrayList);
			goodHits.addAll(tempArrayList);
	
			if (goodHits.size() < 2) {
				hits.getHits(Test.GEOCONIC, tempArrayList);
				goodHits.addAll(tempArrayList);
			}
			if (goodHits.size() < 2) {
				hits.getHits(Test.GEOFUNCTION, tempArrayList);
				goodHits.addAll(tempArrayList);
			}
			if (goodHits.size() < 2) {
				hits.getHits(Test.GEOPOLYGON, tempArrayList);
				goodHits.addAll(tempArrayList);
			}
			if (goodHits.size() < 2) {
				hits.getHits(Test.GEOPOLYLINE, tempArrayList);
				goodHits.addAll(tempArrayList);
			}
	
			// if (goodHits.size() > 2 - selGeos()) {
			// // choose one geo, and select only this one
			// GeoElement geo = chooseGeo(goodHits, true);
			// hits.clear();
			// hits.add(geo);
			// } else {
			hits = goodHits;
			// }
		}
	
		// get lines, conics and functions
		// now there's no popup chooser, when we use the intersect Tool where
		// multiple objects intersect
		// just choose any 2
		addSelectedLine(hits, 10, true);
		addSelectedConic(hits, 10, true);
		addSelectedFunction(hits, 10, true);
		addSelectedImplicitpoly(hits, 10, true);
		addSelectedPolygon(hits, 10, true);
		addSelectedPolyLine(hits, 10, true);
		addSelectedCurve(hits, 10, true);
	
		singlePointWanted = singlePointWanted && (selGeos() >= 2);
	
		// if (selGeos() > 2)
		// return false;
	
		// two lines
		if (selLines() >= 2) {
			GeoLineND[] lines = getSelectedLinesND();
			GeoElement[] ret = { null };
			checkZooming(); 
			
			ret[0] = (GeoElement) getAlgoDispatcher().IntersectLines(null, lines[0],
					lines[1]);
			return ret;
		}
		// two conics
		else if (selConics() >= 2) {
			GeoConicND[] conics = getSelectedConicsND();
			GeoElement[] ret = { null };
			if (singlePointWanted) {
				checkZooming(); 
				
				ret[0] = getAlgoDispatcher().IntersectConicsSingle(null,
						(GeoConic) conics[0], (GeoConic) conics[1], xRW, yRW);
			} else {
				ret = (GeoElement[]) getAlgoDispatcher().IntersectConics(null, conics[0],
						conics[1]);
			}
			return ret;
		} else if (selFunctions() >= 2) {
			GeoFunction[] fun = getSelectedFunctions();
			boolean polynomials = fun[0].isPolynomialFunction(false)
					&& fun[1].isPolynomialFunction(false);
			if (!polynomials) {
				GeoPoint initPoint = new GeoPoint(kernel.getConstruction());
				initPoint.setCoords(xRW, yRW, 1.0);
				checkZooming(); 
				
				return new GeoElement[] { getAlgoDispatcher().IntersectFunctions(null,
						fun[0], fun[1], initPoint) };
			}
			// polynomials
			if (singlePointWanted) {
				checkZooming(); 
				
				return new GeoElement[] { getAlgoDispatcher()
						.IntersectPolynomialsSingle(null, fun[0], fun[1],
								xRW, yRW) };
			}
			return getAlgoDispatcher().IntersectPolynomials(null, fun[0], fun[1]);
		}
		// one line and one conic
		else if ((selLines() >= 1) && (selConics() >= 1)) {
			GeoConic[] conic = getSelectedConics();
			GeoLine[] line = getSelectedLines();
			GeoElement[] ret = { null };
			checkZooming(); 
			
			if (singlePointWanted) {
				ret[0] = IntersectLineConicSingle(null, line[0],
						conic[0], xRW, yRW);
			} else {
				ret = getAlgoDispatcher().IntersectLineConic(null, line[0], conic[0]);
			}
	
			return ret;
		}
		// line and polyLine
		else if ((selLines() >= 1) && (selPolyLines() >= 1)) {
			GeoLine line = getSelectedLines()[0];
			GeoPolyLine polyLine = getSelectedPolyLines()[0];
			GeoElement[] ret = { null };
			checkZooming(); 
			
			ret = getAlgoDispatcher().IntersectLinePolyLine(new String[] { null }, line,
					polyLine);
			return ret;
		}
		// line and curve
		else if ((selLines() >= 1) && (selCurves() >= 1)) {
			GeoLine line = getSelectedLines()[0];
			GeoCurveCartesian curve = getSelectedCurves()[0];
			GeoElement[] ret = { null };
			checkZooming(); 
			
			ret = getAlgoDispatcher().IntersectLineCurve(new String[] { null }, line,
					curve);
			return ret;
		}
		// line and polygon
		else if ((selLines() >= 1) && (selPolygons() >= 1)) {
			GeoLine line = getSelectedLines()[0];
			GeoPolygon polygon = getSelectedPolygons()[0];
			GeoElement[] ret = { null };
			checkZooming(); 
			
			ret = getAlgoDispatcher().IntersectLinePolygon(new String[] { null }, line,
					polygon);
			return ret;
		}
		// line and function
		else if ((selLines() >= 1) && (selFunctions() >= 1)) {
			GeoLine[] line = getSelectedLines();
			GeoFunction[] fun = getSelectedFunctions();
			GeoElement[] ret = { null };
			checkZooming(); 
			
			if (fun[0].isPolynomialFunction(false)) {
				if (singlePointWanted) {
					ret[0] = IntersectPolynomialLineSingle(null, fun[0],
							line[0], xRW, yRW);
				} else {
					ret = getAlgoDispatcher().IntersectPolynomialLine(null, fun[0], line[0]);
				}
			} else {
				GeoPoint initPoint = new GeoPoint(kernel.getConstruction());
				initPoint.setCoords(xRW, yRW, 1.0);
				ret[0] = getAlgoDispatcher().IntersectFunctionLine(null, fun[0], line[0],
						initPoint);
			}
			return ret;
			// function and conic
		} else if ((selFunctions() >= 1) && (selConics() >= 1)) {
			GeoConic[] conic = getSelectedConics();
			GeoFunction[] fun = getSelectedFunctions();
			// if (fun[0].isPolynomialFunction(false)){
			checkZooming(); 
			
			if (singlePointWanted) {
				return new GeoElement[] { IntersectPolynomialConicSingle(null, fun[0], conic[0],
								xRW, yRW) };
			}
			return getAlgoDispatcher().IntersectPolynomialConic(null, fun[0], conic[0]);
			// }
		} else if (selImplicitpoly() >= 1) {
			if (selFunctions() >= 1) {
				GeoImplicitPoly p = getSelectedImplicitpoly()[0];
				GeoFunction fun = getSelectedFunctions()[0];
				// if (fun.isPolynomialFunction(false)){
				checkZooming(); 
				
				if (singlePointWanted) {
					return new GeoElement[] { IntersectImplicitpolyPolynomialSingle(null, p,
									fun, xRW, yRW) };
				}
				return getAlgoDispatcher().IntersectImplicitpolyPolynomial(null, p, fun);
				// }else
				// return null;
			} else if (selLines() >= 1) {
				GeoImplicitPoly p = getSelectedImplicitpoly()[0];
				GeoLine l = getSelectedLines()[0];
				checkZooming(); 
				
				if (singlePointWanted) {
					return new GeoElement[] { IntersectImplicitpolyLineSingle(null, p, l, xRW,
									yRW) };
				}
				return getAlgoDispatcher().IntersectImplicitpolyLine(null, p, l);
			} else if (selConics() >= 1) {
				GeoImplicitPoly p = getSelectedImplicitpoly()[0];
				GeoConic c = getSelectedConics()[0];
				checkZooming(); 
				
				if (singlePointWanted) {
					return new GeoElement[] { IntersectImplicitpolyConicSingle(null, p, c, xRW,
									yRW) };
				}
				return getAlgoDispatcher().IntersectImplicitpolyConic(null, p, c);
			} else if (selImplicitpoly() >= 2) {
				GeoImplicitPoly[] p = getSelectedImplicitpoly();
				checkZooming(); 
				
				if (singlePointWanted) {
					return new GeoElement[] { IntersectImplicitpolysSingle(null, p[0], p[1],
									xRW, yRW) };
				}
				return getAlgoDispatcher().IntersectImplicitpolys(null, p[0], p[1]);
			}
		}
		return null;
	}


	/**
	 * one intersection point of polynomial f and line l near to (xRW, yRW)
	 */
	final private GeoPoint IntersectPolynomialLineSingle(String label,
			GeoFunction f, GeoLine l, double xRW, double yRW) {

		if (!f.isPolynomialFunction(false))
			return null;

		AlgoIntersectPolynomialLine algo = getAlgoDispatcher().getIntersectionAlgorithm(f, l);
		int index = algo.getClosestPointIndex(xRW, yRW);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	final private GeoPoint IntersectPolynomialConicSingle(String label,
			GeoFunction f, GeoConic c, double x, double y) {
		AlgoIntersect algo = getAlgoDispatcher().getIntersectionAlgorithm(f, c);
		int idx = algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}



	/**
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 */
	final private GeoPoint IntersectLineConicSingle(String label, GeoLine g,
			GeoConic c, double xRW, double yRW) {
		AlgoIntersectLineConic algo = getAlgoDispatcher().getIntersectionAlgorithm(g, c);
		int index = algo.getClosestPointIndex(xRW, yRW);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}


	/**
	 * get single intersection points of a implicitPoly and a line
	 */
	final private GeoPoint IntersectImplicitpolyLineSingle(String label,
			GeoImplicitPoly p, GeoLine l, double x, double y) {
		AlgoIntersect algo = getAlgoDispatcher().getIntersectionAlgorithm(p, l);
		int idx = algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get single intersection points of a implicitPoly and a line
	 */
	final private GeoPoint IntersectImplicitpolyPolynomialSingle(String label,
			GeoImplicitPoly p, GeoFunction f, double x, double y) {
		if (!f.isPolynomialFunction(false))
			return null;
		AlgoIntersect algo = getAlgoDispatcher().getIntersectionAlgorithm(p, f);
		int idx = algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	/**
	 * get single intersection points of implicitPolys and conic near given
	 * Point (x,y)
	 * 
	 * @param x
	 * @param y
	 */
	final public GeoPoint IntersectImplicitpolyConicSingle(String label,
			GeoImplicitPoly p1, GeoConic c1, double x, double y) {
		AlgoIntersectImplicitpolys algo = getAlgoDispatcher().getIntersectionAlgorithm(p1, c1);
		int idx = algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	/**
	 * get single intersection points of two implicitPolys near given Point
	 * (x,y)
	 * 
	 * @param x
	 * @param y
	 */
	final private GeoPoint IntersectImplicitpolysSingle(String label,
			GeoImplicitPoly p1, GeoImplicitPoly p2, double x, double y) {
		AlgoIntersectImplicitpolys algo = getAlgoDispatcher().getIntersectionAlgorithm(p1, p2);
		int idx = algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	protected final GeoElement[] parallel(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		boolean hitPoint = (addSelectedPoint(hits, 1, false) != 0);
		if (!hitPoint) {
			if (selLines() == 0) {
				addSelectedVector(hits, 1, false);
			}
			if (selVectors() == 0) {
				addSelectedLine(hits, 1, false);
			}
		}
	
		if (selPoints() == 1) {
			GeoElement[] ret = { null };
			if (selVectors() == 1) {
				// fetch selected point and vector
				GeoPointND[] points = getSelectedPointsND();
				GeoVectorND[] vectors = getSelectedVectorsND();
				// create new line
				checkZooming(); 
				
				if (((GeoElement) points[0]).isGeoElement3D()
						|| ((GeoElement) vectors[0]).isGeoElement3D()) {
					ret[0] = (GeoElement) getKernel().getManager3D().Line3D(
							null, points[0], vectors[0]);
				} else {
					ret[0] = getAlgoDispatcher().Line(null, (GeoPoint) points[0],
							(GeoVector) vectors[0]);
				}
				return ret;
			} else if (selLines() == 1) {
				// fetch selected point and vector
				GeoPointND[] points = getSelectedPointsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new line
				if (((GeoElement) points[0]).isGeoElement3D()
						|| ((GeoElement) lines[0]).isGeoElement3D()) {
					ret[0] = (GeoElement) getKernel().getManager3D().Line3D(
							null, points[0], lines[0]);
				} else {
					ret[0] = getAlgoDispatcher().Line(null, (GeoPoint) points[0],
							(GeoLine) lines[0]);
				}
				return ret;
			}
		}
		return null;
	}

	protected final GeoElement[] parabola(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		boolean hitPoint = (addSelectedPoint(hits, 1, false) != 0);
		if (!hitPoint) {
			addSelectedLine(hits, 1, false);
		}
	
		if (selPoints() == 1) {
			if (selLines() == 1) {
				// fetch selected point and line
				GeoPoint[] points = getSelectedPoints();
				GeoLine[] lines = getSelectedLines();
				// create new parabola
				GeoElement[] ret = { null };
				checkZooming(); 
				
				ret[0] = getAlgoDispatcher().Parabola(null, points[0], lines[0]);
				return ret;
			}
		}
		return null;
	}

	protected GeoElement[] orthogonal(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		boolean hitPoint = (addSelectedPoint(hits, 1, false) != 0);
	
		return orthogonal(hits, hitPoint);
	
	}

	protected GeoElement[] orthogonal(Hits hits, boolean hitPoint) {
	
		if (!hitPoint) {
			if (selLines() == 0) {
				addSelectedVector(hits, 1, false, Test.GEOVECTOR);
			}
			if (selVectors() == 0) {
				addSelectedLine(hits, 1, false);
			}
		}
	
		if (selPoints() == 1) {
			if (selVectors() == 1) {
				// fetch selected point and vector
				GeoPointND[] points = getSelectedPointsND();
				GeoVectorND[] vectors = getSelectedVectorsND();
				// create new line
				GeoElement[] ret = { null };
				// no defined line through a point and orthogonal to a vector in
				// 3D
				if (((GeoElement) points[0]).isGeoElement3D()) {
					return null;
				}
				checkZooming(); 
				
				ret[0] = getAlgoDispatcher().OrthogonalLine(null, (GeoPoint) points[0],
						(GeoVector) vectors[0]);
				return ret;
	
			} else if (selLines() == 1) {
				// fetch selected point and line
				GeoPointND[] points = getSelectedPointsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new line
				return orthogonal(points[0], lines[0]);
			}
		}
		return null;
	}

	protected GeoElement[] orthogonal(GeoPointND point, GeoLineND line) {
		checkZooming(); 
		
		if (((GeoElement) point).isGeoElement3D()
				|| ((GeoElement) line).isGeoElement3D()) {
			return new GeoElement[] { (GeoElement) getKernel().getManager3D()
					.OrthogonalLine3D(null, point, line,
							( view).getDirection()) };
		}
		return orthogonal2D(point, line);
	}

	protected GeoElement[] orthogonal2D(GeoPointND point, GeoLineND line) {
		return new GeoElement[] { getAlgoDispatcher().OrthogonalLine(null,
				(GeoPoint) point, (GeoLine) line) };
	}

	protected final GeoElement[] midpoint(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		boolean hitPoint = (addSelectedPoint(hits, 2, false) != 0);
	
		if (!hitPoint && (selPoints() == 0)) {
			addSelectedSegment(hits, 1, false); // segment needed
			if (selSegments() == 0) {
				addSelectedConic(hits, 1, false); // conic needed
			}
		}
	
		GeoElement[] ret = { null };
	
		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPointND[] points = getSelectedPointsND();
			checkZooming(); 
			
			if (((GeoElement) points[0]).isGeoElement3D()
					|| ((GeoElement) points[1]).isGeoElement3D()) {
				ret[0] = (GeoElement) kernel.getManager3D().Midpoint(null,
						points[0], points[1]);
			} else {
				ret[0] = getAlgoDispatcher().Midpoint(null, (GeoPoint) points[0],
						(GeoPoint) points[1]);
			}
			return ret;
		} else if (selSegments() == 1) {
			// fetch the selected segment
			GeoSegmentND[] segments = getSelectedSegmentsND();
			checkZooming(); 
			
			if (((GeoElement) segments[0]).isGeoElement3D()) {
				ret[0] = (GeoElement) kernel.getManager3D().Midpoint(null,
						segments[0]);
			} else {
				ret[0] = getAlgoDispatcher().Midpoint(null, (GeoSegment) segments[0]);
			}
			return ret;
		} else if (selConics() == 1) {
			// fetch the selected segment
			GeoConic[] conics = getSelectedConics();
			checkZooming(); 
			
			ret[0] = getAlgoDispatcher().Center(null, conics[0]);
			return ret;
		}
		return null;
	}

	protected final boolean functionInspector(Hits hits) {
		if (hits.isEmpty()) {
			return false;
		}
		if (selFunctions() == 0)
			this.addSelectedFunction(hits, 1, false);
		if (selFunctions() == 1) {
			GeoFunction[] functions = getSelectedFunctions();
	
			app.getDialogManager()
					.showFunctionInspector(functions[0]);
			app.setMoveMode();
		}
	
		return false;
	}

	protected final GeoElement[] lineBisector(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
		boolean hitPoint = false;
	
		if (selSegments() == 0) {
			hitPoint = (addSelectedPoint(hits, 2, false) != 0);
		}
	
		if (!hitPoint && (selPoints() == 0)) {
			addSelectedSegment(hits, 1, false); // segment needed
		}
	
		GeoElement[] ret = { null };
		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
			checkZooming(); 
			
			ret[0] = getAlgoDispatcher().LineBisector(null, points[0], points[1]);
			return ret;
		} else if (selSegments() == 1) {
			// fetch the selected segment
			GeoSegment[] segments = getSelectedSegments();
			checkZooming(); 
			
			ret[0] = getAlgoDispatcher().LineBisector(null, segments[0]);
			return ret;
		}
		return null;
	}

	protected final GeoElement[] angularBisector(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
		boolean hitPoint = false;
	
		if (selLines() == 0) {
			hitPoint = (addSelectedPoint(hits, 3, false) != 0);
		}
		if (!hitPoint && (selPoints() == 0)) {
			addSelectedLine(hits, 2, false);
		}
	
		if (selPoints() == 3) {
			// fetch the three selected points
			GeoPoint[] points = getSelectedPoints();
			GeoElement[] ret = { null };
			checkZooming(); 
			
			ret[0] = getAlgoDispatcher().AngularBisector(null, points[0], points[1],
					points[2]);
			return ret;
		} else if (selLines() == 2) {
			// fetch the two lines
			GeoLine[] lines = getSelectedLines();
			checkZooming(); 
			
			return getAlgoDispatcher().AngularBisector(null, lines[0], lines[1]);
		}
		return null;
	}

	protected final GeoElement[] threePoints(Hits hits, int threePointsMode) {
		
		if (hits.isEmpty()) {
			return null;
		}
	
		// points needed
		addSelectedPoint(hits, 3, false);
		if (selPoints() == 3) {
			return switchModeForThreePoints(threePointsMode);
		}
		return null;
	}

	protected GeoElement[] switchModeForThreePoints(int threePointsMode) {
		// fetch the three selected points
		GeoPointND[] points = getSelectedPointsND();
		GeoElement[] ret = { null };
		switch (threePointsMode) {
		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
			checkZooming(); 
			
			if (((GeoElement) points[0]).isGeoElement3D()
					|| ((GeoElement) points[1]).isGeoElement3D()
					|| ((GeoElement) points[2]).isGeoElement3D()) {
				ret[0] = kernel.getManager3D().Circle3D(null, points[0],
						points[1], points[2]);
			} else {
				ret[0] = getAlgoDispatcher().Circle(null, (GeoPoint) points[0],
						(GeoPoint) points[1], (GeoPoint) points[2]);
			}
			break;
	
		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
			checkZooming(); 
			
			ret[0] = getAlgoDispatcher().Ellipse(null, (GeoPoint) points[0],
					(GeoPoint) points[1], (GeoPoint) points[2]);
			break;
	
		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
			checkZooming(); 
			
			ret[0] = getAlgoDispatcher().Hyperbola(null, (GeoPoint) points[0],
					(GeoPoint) points[1], (GeoPoint) points[2]);
			break;
	
		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
			checkZooming(); 
			
			ret[0] = getAlgoDispatcher().CircumcircleArc(null, (GeoPoint) points[0],
					(GeoPoint) points[1], (GeoPoint) points[2]);
			break;
	
		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			checkZooming(); 
			
			ret[0] = getAlgoDispatcher().CircumcircleSector(null, (GeoPoint) points[0],
					(GeoPoint) points[1], (GeoPoint) points[2]);
			break;
	
		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
			checkZooming(); 
			
			ret[0] = getAlgoDispatcher().CircleArc(null, (GeoPoint) points[0],
					(GeoPoint) points[1], (GeoPoint) points[2]);
			break;
	
		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
			checkZooming(); 
			
			ret[0] = getAlgoDispatcher().CircleSector(null, (GeoPoint) points[0],
					(GeoPoint) points[1], (GeoPoint) points[2]);
			break;
	
		default:
			return null;
		}
	
		return ret;
	}

	protected final boolean relation(Hits hits) {
		if (hits.isEmpty()) {
			return false;
		}
	
		addSelectedGeo(hits, 2, false);
		if (selGeos() == 2) {
			// fetch the three selected points
			GeoElement[] geos = getSelectedGeos();
			app.showRelation(geos[0], geos[1]);
			return true;
		}
		return false;
	}

	protected final GeoElement[] locus(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// points needed
		addSelectedPoint(hits, 2, false);
		addSelectedNumeric(hits, 1, false);
	
		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
			GeoLocus locus;
			checkZooming(); 
			
			if (points[0].getPath() == null) {
				locus = getAlgoDispatcher().Locus(null, points[0], points[1]);
			} else {
				locus = getAlgoDispatcher().Locus(null, points[1], points[0]);
			}
			GeoElement[] ret = { null };
			ret[0] = locus;
			return ret;
		} else if ((selPoints() == 1) && (selNumbers() == 1)) {
			GeoPoint[] points = getSelectedPoints();
			GeoNumeric[] numbers = getSelectedNumbers();
			checkZooming(); 
			
			GeoLocus locus = getAlgoDispatcher().Locus(null, points[0], numbers[0]);
			GeoElement[] ret = { locus };
			return ret;
		}
		return null;
	}

	protected final GeoElement[] conic5(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// points needed
		addSelectedPoint(hits, 5, false);
		if (selPoints() == 5) {
			// fetch the three selected points
			GeoPoint[] points = getSelectedPoints();
			GeoElement[] ret = { null };
			checkZooming(); 
			
			ret[0] = getAlgoDispatcher().Conic(null, points);
			return ret;
		}
		return null;
	}

	protected GeoElement[] slope(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		addSelectedLine(hits, 1, false);
	
		if (selLines() == 1) {
			GeoLine line = getSelectedLines()[0];
	
			GeoNumeric slope;
			/*
			 * if (strLocale.equals("de_AT")) { slope = kernel.Slope("k", line);
			 * } else { slope = kernel.Slope("m", line); }
			 */
	
			String label = app.getPlain("ExplicitLineGradient");
	
			// make sure automatic naming goes m, m_1, m_2, ..., m_{10}, m_{11}
			// etc
			if (kernel.lookupLabel(label) != null) {
				int i = 1;
				while (kernel.lookupLabel(i > 9 ? label + "_{" + i + "}"
						: label + "_" + i) != null) {
					i++;
				}
				label = i > 9 ? label + "_{" + i + "}" : label + "_" + i;
			}
	
			checkZooming(); 
			
			slope = getAlgoDispatcher().Slope(label, line);
	
			// show value
			if (slope.isLabelVisible()) {
				slope.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			} else {
				slope.setLabelMode(GeoElement.LABEL_VALUE);
			}
			slope.setLabelVisible(true);
			slope.updateRepaint();
			GeoElement[] ret = { slope };
			return ret;
		}
		return null;
	}

	protected final GeoElement[] tangents(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		boolean found = false;
		found = addSelectedConic(hits, 2, false) != 0;
		if (!found) {
			found = addSelectedFunction(hits, 1, false) != 0;
		}
		if (!found) {
			found = addSelectedCurve(hits, 1, false) != 0;
		}
		if (!found) {
			found = addSelectedImplicitpoly(hits, 1, false) != 0;
		}
	
		if (!found) {
			if (selLines() == 0) {
				addSelectedPoint(hits, 1, false);
			}
			if (selPoints() == 0) {
				addSelectedLine(hits, 1, false);
			}
		}
	
		if (selConics() == 1) {
			if (selPoints() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoPoint[] points = getSelectedPoints();
				// create new tangents
				checkZooming(); 
				
				return getAlgoDispatcher().Tangent(null, points[0], conics[0]);
			} else if (selLines() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoLine[] lines = getSelectedLines();
				// create new line
				checkZooming(); 
				
				return getAlgoDispatcher().Tangent(null, lines[0], conics[0]);
			}
		} else if (selConics() == 2) {
			GeoConic[] conics = getSelectedConics();
			// create new tangents
			checkZooming(); 
			
			return getAlgoDispatcher().CommonTangents(null, conics[0], conics[1]);
		} else if (selFunctions() == 1) {
			if (selPoints() == 1) {
				GeoFunction[] functions = getSelectedFunctions();
				GeoPoint[] points = getSelectedPoints();
				// create new tangents
				GeoElement[] ret = { null };
				checkZooming(); 
				
				ret[0] = getAlgoDispatcher().Tangent(null, points[0], functions[0]);
				return ret;
			}
		} else if (selCurves() == 1) {
			if (selPoints() == 1) {
				GeoCurveCartesian[] curves = getSelectedCurves();
				GeoPoint[] points = getSelectedPoints();
				// create new tangents
				GeoElement[] ret = { null };
				checkZooming(); 
				
				ret[0] = kernel.Tangent(null, points[0], curves[0]);
				return ret;
			}
		} else if (selImplicitpoly() == 1) {
			if (selPoints() == 1) {
				GeoImplicitPoly implicitPoly = getSelectedImplicitpoly()[0];
				GeoPoint[] points = getSelectedPoints();
				// create new tangents
				checkZooming(); 
				
				return getAlgoDispatcher().Tangent(null, points[0], implicitPoly);
			} else if (selLines() == 1) {
				GeoImplicitPoly implicitPoly = getSelectedImplicitpoly()[0];
				GeoLine[] lines = getSelectedLines();
				// create new line
				checkZooming(); 
				
				return getAlgoDispatcher().Tangent(null, lines[0], implicitPoly);
			}
		}
		return null;
	}

	protected final boolean delete(Hits hits) {
		if (hits.isEmpty()) {
			return false;
		}
	
		addSelectedGeo(hits, 1, false);
		if (selGeos() == 1) {
			// delete this object
			GeoElement[] geos = getSelectedGeos();
			geos[0].removeOrSetUndefinedIfHasFixedDescendent();
			return true;
		}
		return false;
	}

	protected final GeoElement[] polarLine(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
		boolean hitConic = false;
	
		hitConic = (addSelectedConic(hits, 1, false) != 0);
	
		if (!hitConic) {
			if (selVectors() == 0) {
				addSelectedVector(hits, 1, false);
			}
			if (selLines() == 0) {
				addSelectedPoint(hits, 1, false);
			}
			if (selPoints() == 0) {
				addSelectedLine(hits, 1, false);
			}
		}
	
		if (selConics() == 1) {
			GeoElement[] ret = { null };
			if (selPoints() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoPoint[] points = getSelectedPoints();
				// create new tangents
				checkZooming(); 
				
				ret[0] = PolarLine(null, points[0], conics[0]);
				return ret;
			} else if (selLines() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoLine[] lines = getSelectedLines();
				// create new line
				checkZooming(); 
				
				ret[0] = getAlgoDispatcher().DiameterLine(null, lines[0], conics[0]);
				return ret;
			} else if (selVectors() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoVector[] vecs = getSelectedVectors();
				// create new line
				checkZooming(); 
				
				ret[0] = getAlgoDispatcher().DiameterLine(null, vecs[0], conics[0]);
				return ret;
			}
		}
		return null;
	}

	/**
	 * polar line to P relativ to c
	 */
	final private GeoLine PolarLine(String label, GeoPoint P, GeoConic c) {
		AlgoPolarLine algo = new AlgoPolarLine(kernel.getConstruction(), label, c, P);
		GeoLine polar = algo.getLine();
		return polar;
	}


	protected final boolean showHideLabel(Hits hits) {
		if (hits.isEmpty()) {
			return false;
		}
	
		if (selectionPreview) {
			addSelectedGeo(hits, 1000, false);
			return false;
		}
	
		GeoElement geo = chooseGeo(
				hits.getOtherHits(Test.GEOAXIS, tempArrayList), true);
		if (geo != null) {
			geo.setLabelVisible(!geo.isLabelVisible());
			geo.updateRepaint();
			return true;
		}
		return false;
	}

	protected final boolean copyVisualStyle(Hits hits) {
		if (hits.isEmpty()) {
			return false;
		}
	
		if (selectionPreview) {
			addSelectedGeo(hits, 1000, false);
			return false;
		}
	
		GeoElement geo = chooseGeo(
				hits.getOtherHits(Test.GEOAXIS, tempArrayList), true);
		if (geo == null) {
			return false;
		}

		// movedGeoElement is the active geo
		if (app.getGeoForCopyStyle() == null) {
			app.setGeoForCopyStyle(geo);
			Hits oldhits = new Hits();
			oldhits.addAll(app.getSelectedGeos());
			for (int i = oldhits.size() - 1; i >= 0; i--) {
				GeoElement oldgeo = oldhits.get(i);
				//if (!(movedGeoElement.getClass().isInstance(oldgeo))) {
				if (!(Test.getSpecificTest(app.getGeoForCopyStyle()).check(oldgeo))) {
					oldhits.remove(i);
				}
			}
			if (oldhits.size() > 0) {
				// there were appropriate selected elements
				// apply visual style for them
				// standard case: copy visual properties
				for (int i = 0; i < oldhits.size(); i++) {
					GeoElement oldgeo = oldhits.get(i);
					oldgeo.setAdvancedVisualStyle(app.getGeoForCopyStyle());
					oldgeo.updateRepaint();
				}
				clearSelections();
				return true;
			}
			// there were no appropriate selected elements
			// set movedGeoElement
			app.addSelectedGeo(geo);
		} else {
			if (geo == app.getGeoForCopyStyle()) {
				// deselect
				app.removeSelectedGeo(geo);
				app.setGeoForCopyStyle(null);
				if (toggleModeChangedKernel) {
					app.storeUndoInfo();
				}
				toggleModeChangedKernel = false;
			} else {
				// standard case: copy visual properties
				geo.setAdvancedVisualStyle(app.getGeoForCopyStyle());
				geo.updateRepaint();
				return true;
			}
		}
		return false;
	}

	public GPoint getMouseLoc() {
		return mouseLoc;
	}

	public void textfieldHasFocus(boolean hasFocus) {
		textfieldHasFocus = hasFocus;
	}
	
	/**
	 * 
	 * @return true if a checkbox/textfield/button just has been hitted,
	 * to avoid properties view to show graphics properties
	 */
	public boolean checkBoxOrTextfieldOrButtonJustHitted(){
		return checkBoxOrButtonJustHitted || textfieldHasFocus;
	}
	
	protected abstract void initToolTipManager();

	protected void initShowMouseCoords() {
		view.setShowMouseCoords((mode == EuclidianConstants.MODE_POINT)
				|| (mode == EuclidianConstants.MODE_MOVE));
	}

	protected final void wrapMouseEntered() {
		if (textfieldHasFocus) {
			return;
		}
	
		initToolTipManager();
		initShowMouseCoords();
		view.mouseEntered();
	}
	
	protected boolean move(Hits hits){
		
		addSelectedGeo(hits.getMoveableHits(view), 1, false);
		return false;
	}

	protected final boolean moveRotate(Hits hits) {
		addSelectedGeo(hits.getPointRotateableHits(view, rotationCenter), 1,
				false);
		return false;
	}

	protected final boolean point(Hits hits) {
		addSelectedGeo(hits.getHits(Test.PATH, tempArrayList), 1, false);
		return false;
	}

	protected final boolean geoElementSelected(Hits hits, boolean addToSelection) {
		if (hits.isEmpty()) {
			return false;
		}
	
		addSelectedGeo(hits, 1, false);
		if (selGeos() == 1) {
			GeoElement[] geos = getSelectedGeos();
			app.geoElementSelected(geos[0], addToSelection);
		}
		return false;
	}

	protected final boolean segmentFixed(Hits hits) {
		if (hits.isEmpty()) {
			return false;
		}
	
		// dilation center
		addSelectedPoint(hits, 1, false);
	
		// we got the point
		if (selPoints() == 1) {
			// get length of segment
			app.getDialogManager()
					.showNumberInputDialogSegmentFixed(
							app.getMenu(getKernel().getModeText(mode)),
							getSelectedPoints()[0]);
	
			return true;
		}
		return false;
	}

	protected final GeoElement[] angleFixed(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// dilation center
		int count = addSelectedPoint(hits, 2, false);
	
		if (count == 0) {
			addSelectedSegment(hits, 1, false);
		}
	
		// we got the points
		if ((selPoints() == 2) || (selSegments() == 1)) {
	
			GeoElement[] selGeos = getSelectedGeos();
	
			app.getDialogManager()
					.showNumberInputDialogAngleFixed(
							app.getMenu(getKernel().getModeText(mode)),
							getSelectedSegments(), getSelectedPoints(), selGeos);
	
			return null;
	
		}
		return null;
	}
	
	protected abstract GeoElement[] createCircle2ForPoints3D(GeoPointND p0, GeoPointND p1);

	protected GeoElement[] createCircle2(GeoPointND p0, GeoPointND p1) {
		checkZooming(); 
		
		if (((GeoElement) p0).isGeoElement3D()
				|| ((GeoElement) p1).isGeoElement3D()) {
			return createCircle2ForPoints3D(p0, p1);
		}
		return new GeoElement[] { getAlgoDispatcher().Circle(null, (GeoPoint) p0,
				(GeoPoint) p1) };
	}

	protected GeoElement[] switchModeForCircleOrSphere2(int sphereMode) {
		checkZooming(); 
		
		GeoPointND[] points = getSelectedPointsND();
		if (sphereMode == EuclidianConstants.MODE_SEMICIRCLE) {
			return new GeoElement[] { getAlgoDispatcher().Semicircle(null,
					(GeoPoint) points[0], (GeoPoint) points[1]) };
		}
		return createCircle2(points[0], points[1]);
	
	}

	protected final GeoElement[] circleOrSphere2(Hits hits, int sphereMode) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the three selected points
			return switchModeForCircleOrSphere2(sphereMode);
		}
		return null;
	}

	protected final boolean showHideObject(Hits hits) {
		if (hits.isEmpty()) {
			return false;
		}
	
		if (selectionPreview) {
			addSelectedGeo(hits, 1000, false);
			return false;
		}
	
		GeoElement geo = chooseGeo(hits, true);
		if (geo != null) {
			// hide axis
			if (geo instanceof GeoAxis) {
				switch (((GeoAxis) geo).getType()) {
				case GeoAxisND.X_AXIS:
					// view.showAxes(false, view.getShowYaxis());
					view.setShowAxis(EuclidianViewInterfaceCommon.AXIS_X, false, true);
					break;
	
				case GeoAxisND.Y_AXIS:
					// view.showAxes(view.getShowXaxis(), false);
					view.setShowAxis(EuclidianViewInterfaceCommon.AXIS_Y, false, true);
					break;
				}
				app.updateMenubar();
			} else {
				app.toggleSelectedGeo(geo);
			}
			return true;
		}
		return false;
	}

	protected final boolean text(Hits hits) {
		GeoPointND loc = null; // location
	
		if (hits.isEmpty()) {
			if (selectionPreview) {
				return false;
			}
			// create new Point
			checkZooming(); 
			
			loc = new GeoPoint(kernel.getConstruction());
			loc.setCoords(xRW, yRW, 1.0);
		} else {
			
			// points needed
			addSelectedPoint(hits, 1, false);
			if (selPoints() >= 1) {
				// fetch the selected point
				GeoPointND[] points = getSelectedPointsND();
				loc = points[0];
			} else if (!selectionPreview) {
				checkZooming(); 
				
				loc = new GeoPoint(kernel.getConstruction());
				loc.setCoords(xRW, yRW, 1.0);
			}
		}
	
		// got location
		if (loc != null) {
			app.getDialogManager().showTextCreationDialog(loc);
			return true;
		}
	
		return false;
	}

	public boolean isAltDown() {
		return altDown;
	}

	public void setAltDown(boolean altDown) {
		this.altDown = altDown;
	}

	protected final boolean slider() {
		if (!selectionPreview && (mouseLoc != null)) {
			app.getDialogManager()
					.showSliderCreationDialog(mouseLoc.x, mouseLoc.y);
		}
		return false;
	}

	protected final boolean image(Hits hits) {
		GeoPoint loc = null; // location
	
		if (hits.isEmpty()) {
			if (selectionPreview) {
				return false;
			}
			// create new Point
			checkZooming(); 
			
			loc = new GeoPoint(kernel.getConstruction());
			loc.setCoords(xRW, yRW, 1.0);
		} else {
			// points needed
			addSelectedPoint(hits, 1, false);
			if (selPoints() >= 1) {
				// fetch the selected point
				GeoPoint[] points = getSelectedPoints();
				loc = points[0];
			} else if (!selectionPreview) {
				checkZooming(); 
				
				loc = new GeoPoint(kernel.getConstruction());
				loc.setCoords(xRW, yRW, 1.0);
			}
		}
	
		// got location
		if (loc != null) {
			app.getGuiManager().loadImage(loc, null, altDown);
			return true;
		}
	
		return false;
	}

	protected final GeoElement[] mirrorAtPoint(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// try to get one Transformable
		int count = 0;
		if (selGeos() == 0) {
			Hits mirAbles = hits.getHits(Test.TRANSFORMABLE, tempArrayList);
			count = addSelectedGeo(mirAbles, 1, false);
		}
	
		// polygon
		if (count == 0) {
			count = addSelectedPolygon(hits, 1, false);
		}
	
		// point = mirror
		if (count == 0) {
			count = addSelectedPoint(hits, 1, false);
		}
	
		// we got the mirror point
		if (selPoints() == 1) {
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoPoint[] points = getSelectedPoints();
				checkZooming(); 
				
				return getAlgoDispatcher().Mirror(null, polys[0], points[0]);
			} else if (selGeos() > 0) {
				// mirror all selected geos
				GeoElement[] geos = getSelectedGeos();
				GeoPoint point = getSelectedPoints()[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				checkZooming(); 
				
				for (int i = 0; i < geos.length; i++) {
					if (geos[i] != point) {
						if (geos[i] instanceof Transformable) {
							ret.addAll(Arrays.asList(getAlgoDispatcher().Mirror(null,
									geos[i], point)));
						} else if (geos[i].isGeoPolygon()) {
							ret.addAll(Arrays.asList(getAlgoDispatcher().Mirror(null,
									geos[i], point)));
						}
					}
				}
				GeoElement[] retex = {};
				return ret.toArray(retex);
			}
		}
		return null;
	}

	public String getSliderValue() {
		return sliderValue;
	}

	protected final GeoElement[] mirrorAtLine(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// Transformable
		int count = 0;
		if (selGeos() == 0) {
			Hits mirAbles = hits.getHits(Test.TRANSFORMABLE, tempArrayList);
			count = addSelectedGeo(mirAbles, 1, false);
		}
	
		// polygon
		if (count == 0) {
			count = addSelectedPolygon(hits, 1, false);
		}
	
		// line = mirror
		if (count == 0) {
			addSelectedLine(hits, 1, false);
		}
	
		// we got the mirror point
		if (selLines() == 1) {
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoLine[] lines = getSelectedLines();
				checkZooming(); 

				return getAlgoDispatcher().Mirror(null, polys[0], lines[0]);
			} else if (selGeos() > 0) {
				// mirror all selected geos
				GeoElement[] geos = getSelectedGeos();
				GeoLine line = getSelectedLines()[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				checkZooming(); 
				
				for (int i = 0; i < geos.length; i++) {
					if (geos[i] != line) {
						if (geos[i] instanceof Transformable) {
							ret.addAll(Arrays.asList(getAlgoDispatcher().Mirror(null,
									geos[i], line)));
						} else if (geos[i].isGeoPolygon()) {
							ret.addAll(Arrays.asList(getAlgoDispatcher().Mirror(null,
									geos[i], line)));
						}
					}
				}
				GeoElement[] retex = {};
				return ret.toArray(retex);
			}
		}
		return null;
	}

	protected final GeoElement[] mirrorAtCircle(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// Transformable
		int count = 0;
		if (selGeos() == 0) {
			Hits mirAbles = hits.getHits(Test.TRANSFORMABLE, tempArrayList);
			mirAbles.removeImages();
			count = addSelectedGeo(mirAbles, 1, false);
		}
	
		// polygon
		if (count == 0) {
			count = addSelectedPolygon(hits, 1, false);
		}
	
		// line = mirror
		if (count == 0) {
			addSelectedConic(hits, 1, false);
		}
	
		// we got the mirror point
		if (selConics() == 1) {
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoConic[] lines = getSelectedCircles();
				checkZooming(); 
				
				return getAlgoDispatcher().Mirror(null, polys[0], lines[0]);
			} else if (selGeos() > 0) {
				// mirror all selected geos
				GeoElement[] geos = getSelectedGeos();
				GeoConic line = getSelectedCircles()[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				checkZooming(); 
				
				for (int i = 0; i < geos.length; i++) {
					if (geos[i] != line) {
						if (geos[i] instanceof Transformable) {
							ret.addAll(Arrays.asList(getAlgoDispatcher().Mirror(null,
									geos[i], line)));
						} else if (geos[i].isGeoPolygon()) {
							ret.addAll(Arrays.asList(getAlgoDispatcher().Mirror(null,
									geos[i], line)));
						}
					}
				}
				GeoElement[] retex = {};
				return ret.toArray(retex);
			}
		}
		return null;
	}
	
	
	private boolean clearHighlightedGeos(){

		boolean repaintNeeded = false;

		// clear old highlighting
		if (highlightedGeos.size() > 0) {
			setHighlightedGeos(false);
			repaintNeeded = true;
		}
		// find new objects to highlight
		highlightedGeos.clear();

		return repaintNeeded;
	}
	
	public boolean refreshHighlighting(Hits hits, AbstractEvent event) {
	
		// clear old highlighting
		boolean repaintNeeded = clearHighlightedGeos();		

		selectionPreview = true; // only preview selection, see also
		// mouseReleased()
		processMode(hits, event); // build highlightedGeos List
	
		if (highlightJustCreatedGeos) {
			highlightedGeos.addAll(justCreatedGeos); // we also highlight just
														// created geos
		}
	
		selectionPreview = false; // reactivate selection in mouseReleased()
	
		// set highlighted objects
		if (highlightedGeos.size() > 0) {
			setHighlightedGeos(true);
			repaintNeeded = true;
		}
		return repaintNeeded;
	}

	public boolean highlight(GeoElement geo){
		boolean repaintNeeded = clearHighlightedGeos();	

		if (geo!=null){
			highlightedGeos.add(geo);
			setHighlightedGeos(true);
			repaintNeeded = true;
		}

		return repaintNeeded;
	}
	
	public boolean highlight(ArrayList<GeoElement> geos){
		boolean repaintNeeded = clearHighlightedGeos();	

		if (geos!=null && geos.size()>0){
			for (GeoElement geo : geos)
				highlightedGeos.add(geo);
			setHighlightedGeos(true);
			repaintNeeded = true;
		}

		return repaintNeeded;
	}

	public void clearSelections() {
		clearSelections(true,true);
	}

	/**
	 * Clear selection
	 * @param repaint whether all views need repainting afterwards
	 * @param updateSelection call (or not) updateSelection()
	 */
	public void clearSelections(boolean repaint, boolean updateSelection) {

		clearSelection(selectedNumbers, false);
		clearSelection(selectedNumberValues, false);
		clearSelection(selectedPoints, false);
		clearSelection(selectedLines, false);
		clearSelection(selectedSegments, false);
		clearSelection(selectedConicsND, false);
		clearSelection(selectedVectors, false);
		clearSelection(selectedPolygons, false);
		clearSelection(selectedGeos, false);
		clearSelection(selectedFunctions, false);
		clearSelection(selectedCurves, false);
		clearSelection(selectedLists, false);
		clearSelection(selectedPaths, false);
		clearSelection(selectedRegions, false);
	
		app.clearSelectedGeos(repaint,updateSelection);
	
		// if we clear selection and highlighting,
		// we may want to clear justCreatedGeos also
		clearJustCreatedGeos();
	
		// clear highlighting
		refreshHighlighting(null, null);
	}

	protected boolean attach(GeoPointND p, Path path) {
		
		GeoPoint point = (GeoPoint) p;
	
		try {
			Construction cons = kernel.getConstruction();
			boolean oldLabelCreationFlag = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			checkZooming(); 
			
			GeoPoint newPoint = getAlgoDispatcher().Point(null, path,
					view.toRealWorldCoordX(mx), view.toRealWorldCoordY(my),
					false, false);
			cons.setSuppressLabelCreation(oldLabelCreationFlag);
			kernel.getConstruction().replace(point, newPoint);
			clearSelections();
			return true;
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
	}

	protected boolean attach(GeoPointND p, Region region) {
	
		GeoPoint point = (GeoPoint) p;
		
		try {
			Construction cons = kernel.getConstruction();
			boolean oldLabelCreationFlag = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			checkZooming(); 
			
			GeoPoint newPoint = getAlgoDispatcher().PointIn(null, region,
					view.toRealWorldCoordX(mx), view.toRealWorldCoordY(my),
					false, false);
			cons.setSuppressLabelCreation(oldLabelCreationFlag);
			kernel.getConstruction().replace(point, newPoint);
			clearSelections();
			return true;
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
	}
	
	protected boolean detach(GeoPointND point) {
		
		GeoPoint p = (GeoPoint) point;
		
		getSelectedPoints();
		getSelectedRegions();
		getSelectedPaths();

		// move point (20,20) pixels when detached
		double x = view.toScreenCoordX(p.inhomX) + 20;
		double y = view.toScreenCoordY(p.inhomY) + 20;

		try {
			Construction cons = kernel.getConstruction();
			boolean oldLabelCreationFlag = cons
					.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			checkZooming(); 
			
			GeoPoint newPoint = new GeoPoint(
					kernel.getConstruction(), null,
					view.toRealWorldCoordX(x),
					view.toRealWorldCoordY(y), 1.0);
			cons.setSuppressLabelCreation(oldLabelCreationFlag);
			cons.replace(p, newPoint);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		clearSelections();
		return true;
	}
	
	

	final protected boolean attachDetach(Hits hits) {
		if (hits.isEmpty()) {
			return false;
		}
	
		addSelectedRegion(hits, 1, false);
	
		addSelectedPath(hits, 1, false);
	
		addSelectedPoint(hits, 1, false);
	
		if (selectedPoints.size() == 1) {
	
			GeoPointND p = selectedPoints.get(0);
	
			if (p.isPointOnPath() || p.isPointInRegion()) {
	
				detach(p);
			}
		}
	
		if (selPoints() == 1) {
			if ((selPaths() == 1) && !isAltDown()) { // press alt to force region
													// (ie inside) not path
													// (edge)
				Path paths[] = getSelectedPaths();
				GeoPointND[] points = getSelectedPoints();
	
				// Application.debug("path: "+paths[0]+"\npoint: "+points[0]);
	
				if (((GeoElement) paths[0]).isChildOf((GeoElement) points[0])) {
					return false;
				}
	
				if (((GeoElement) paths[0]).isGeoPolygon()
						|| (((GeoElement) paths[0]).isGeoConic() && (((GeoConicND) paths[0])
								.getLastHitType() == HitType.ON_FILLING))) {
					return attach(points[0], (Region) paths[0]);
				}
	
				return attach(points[0], paths[0]);
	
			} else if (selRegions() == 1) {
				Region regions[] = getSelectedRegions();
				GeoPointND[] points = getSelectedPoints();
	
				if (!((GeoElement) regions[0]).isChildOf((GeoElement) points[0])) {
					return attach(points[0], regions[0]);
				}
	
			}
		}
		return false;
	}

	protected GeoElement[] translate(GeoElement geo, GeoVectorND vec) {
		checkZooming(); 
		
		return getAlgoDispatcher().Translate(null, geo, (GeoVector) vec);
	}

	protected final GeoElement[] translateByVector(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// Transformable
		int count = 0;
		if (selGeos() == 0) {
			Hits transAbles = hits.getHits(Test.TRANSLATEABLE, tempArrayList);
			count = addSelectedGeo(transAbles, 1, false);
		}
	
		// polygon
		if (count == 0) {
			count = addSelectedPolygon(hits, 1, false);
		}
	
		// list
		if (count == 0) {
			count = addSelectedList(hits, 1, false);
		}
	
		// translation vector
		if (count == 0) {
			count = addSelectedVector(hits, 1, false);
		}
	
		// create translation vector
		if (count == 0) {
			count = addSelectedPoint(hits, 2, false);
			selectedGeos.removeAll(selectedPoints);
			allowSelectionRectangleForTranslateByVector = false;
		}
	
		// we got the mirror point
		if ((selVectors() == 1) || (selPoints() == 2)) {
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoVectorND vec = null;
				if (selVectors() == 1) {
					vec = getSelectedVectorsND()[0];
				} else {
					GeoPointND[] ab = getSelectedPointsND();
					vec = (GeoVectorND) vector(ab[0], ab[1]);
				}
				allowSelectionRectangleForTranslateByVector = true;
				return translate(polys[0], vec);
			} else if (selGeos() > 0) {
				// mirror all selected geos
				GeoElement[] geos = getSelectedGeos();
				GeoVectorND vec = null;
				if (selVectors() == 1) {
					vec = getSelectedVectorsND()[0];
				} else {
					GeoPointND[] ab = getSelectedPointsND();
					vec = (GeoVectorND) vector(ab[0], ab[1]);
				}
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				for (int i = 0; i < geos.length; i++) {
					if (geos[i] != vec) {
						if ((geos[i] instanceof Translateable)
								|| geos[i].isGeoPolygon()
								|| geos[i].isGeoList()) {
							ret.addAll(Arrays.asList(translate(geos[i], vec)));
						}
					}
				}
				GeoElement[] retex = {};
				allowSelectionRectangleForTranslateByVector = true;
				return ret.toArray(retex);
			}
		}
		return null;
	}

	protected final GeoElement[] rotateByAngle(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// Transformable
		int count = 0;
		if (selGeos() == 0) {
			Hits rotAbles = hits.getHits(Test.TRANSFORMABLE, tempArrayList);
			count = addSelectedGeo(rotAbles, 1, false);
		}
	
		// polygon
		if (count == 0) {
			count = addSelectedPolygon(hits, 1, false);
		}
	
		// rotation center
		if (count == 0) {
			addSelectedPoint(hits, 1, false);
		}
	
		// we got the rotation center point
		if ((selPoints() == 1) && (selGeos() > 0)) {
	
			GeoElement[] selGeos = getSelectedGeos();
	
			app.getDialogManager()
					.showNumberInputDialogRotate(
							app.getMenu(getKernel().getModeText(mode)),
							getSelectedPolygons(), getSelectedPoints(), selGeos);
	
			return null;
	
		}
	
		return null;
	}

	protected final GeoElement[] dilateFromPoint(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// dilateable
		int count = 0;
		if (selGeos() == 0) {
			Hits dilAbles = hits.getHits(Test.DILATEABLE, tempArrayList);
			count = addSelectedGeo(dilAbles, 1, false);
		}
	
		// polygon
		if (count == 0) {
			count = addSelectedPolygon(hits, 1, false);
		}
	
		// dilation center
		if (count == 0) {
			addSelectedPoint(hits, 1, false);
		}
	
		// we got the mirror point
		if (selPoints() == 1) {
	
			GeoElement[] selGeos = getSelectedGeos();
	
			app.getDialogManager()
					.showNumberInputDialogDilate(
							app.getMenu(getKernel().getModeText(mode)),
							getSelectedPolygons(), getSelectedPoints(), selGeos);
	
			return null;
	
			/*
			 * NumberValue num =
			 * app.getGuiManager().showNumberInputDialog(app.getMenu
			 * (getKernel().getModeText(mode)), app.getPlain("Numeric"), null);
			 * if (num == null) { view.resetMode(); return null; }
			 * 
			 * if (selPolygons() == 1) { GeoPolygon[] polys =
			 * getSelectedPolygons(); GeoPoint[] points = getSelectedPoints();
			 * return kernel.Dilate(null, polys[0], num, points[0]); } else if
			 * (selGeos() > 0) { // mirror all selected geos GeoElement [] geos
			 * = getSelectedGeos(); GeoPoint point = getSelectedPoints()[0];
			 * ArrayList<GeoElement> ret = new ArrayList<GeoElement>(); for (int
			 * i=0; i < geos.length; i++) { if (geos[i] != point) { if (geos[i]
			 * instanceof Dilateable || geos[i].isGeoPolygon())
			 * ret.addAll(Arrays.asList(kernel.Dilate(null, geos[i], num,
			 * point))); } } GeoElement[] retex = {}; return ret.toArray(retex);
			 * }
			 */
		}
		return null;
	}

	protected final GeoElement[] fitLine(Hits hits) {
	
		GeoList list;
	
		addSelectedList(hits, 1, false);
	
		GeoElement[] ret = { null };
		checkZooming(); 
		
		if (selLists() > 0) {
			list = getSelectedLists()[0];
			if (list != null) {
				ret[0] = FitLineY(null, list);
				return ret;
			}
		} else {
			addSelectedPoint(hits, 999, true);
	
			if (selPoints() > 1) {
				GeoPoint[] points = getSelectedPoints();
				list = geogebra.common.kernel.commands.CommandProcessor
						.wrapInList(kernel, points, points.length,
								GeoClass.POINT);
				if (list != null) {
					ret[0] = FitLineY(null, list);
					return ret;
				}
			}
		}
		return null;
	}
	
	/**
	 * FitLineY[list of coords] Michael Borcherds
	 */
	final public GeoLine FitLineY(String label, GeoList list) {
		AlgoFitLineY algo = new AlgoFitLineY(kernel.getConstruction(), label, list);
		GeoLine line = algo.getFitLineY();
		return line;
	}



	protected final GeoElement[] createList(Hits hits) {
		GeoList list;
		GeoElement[] ret = { null };
	
		if (!selectionPreview && (hits.size() > 1)) {
			checkZooming(); 
			
			list = getAlgoDispatcher().List(null, hits, false);
			if (list != null) {
				ret[0] = list;
				return ret;
			}
		}
		return null;
	}

	protected void calcRWcoords() {
		xRW = (mouseLoc.x - view.getXZero()) * view.getInvXscale();
		yRW = (view.getYZero() - mouseLoc.y) * view.getInvYscale();
	}

	protected void setMouseLocation(AbstractEvent event) {
		mouseLoc = event.getPoint();
	
		setAltDown(event.isAltDown());
	
		if (mouseLoc.x < 0) {
			mouseLoc.x = 0;
		} else if (mouseLoc.x > view.getViewWidth()) {
			mouseLoc.x = view.getViewWidth();
		}
		if (mouseLoc.y < 0) {
			mouseLoc.y = 0;
		} else if (mouseLoc.y > view.getViewHeight()) {
			mouseLoc.y = view.getViewHeight();
		}
	}

	/**
	 * COORD TRANSFORM SCREEN -> REAL WORLD
	 * 
	 * real world coords -> screen coords ( xscale 0 xZero ) T = ( 0 -yscale
	 * yZero ) ( 0 0 1 )
	 * 
	 * screen coords -> real world coords ( 1/xscale 0 -xZero/xscale ) T^(-1) =
	 * ( 0 -1/yscale yZero/yscale ) ( 0 0 1 )
	 */
	public void transformCoords() {
		// calc real world coords
		calcRWcoords();
	
		// if alt pressed, make sure slope is a multiple of 15 degrees
		if (((mode == EuclidianConstants.MODE_JOIN)
				|| (mode == EuclidianConstants.MODE_SEGMENT)
				|| (mode == EuclidianConstants.MODE_RAY)
				|| (mode == EuclidianConstants.MODE_VECTOR)
				|| (mode == EuclidianConstants.MODE_POLYGON) || (mode == EuclidianConstants.MODE_POLYLINE))
				&& useLineEndPoint && (lineEndPoint != null)) {
			xRW = lineEndPoint.x;
			yRW = lineEndPoint.y;
			return;
		}
	
		if ((mode == EuclidianConstants.MODE_MOVE)
				&& ((moveMode == MOVE_NUMERIC)
						|| (moveMode == MOVE_VECTOR_NO_GRID) || (moveMode == MOVE_POINT_WITH_OFFSET))) {
			return;
		}
	
		// point capturing to grid
		double pointCapturingPercentage = 1;
		switch (view.getPointCapturingMode()) {
	
		case EuclidianStyleConstants.POINT_CAPTURING_STICKY_POINTS:
			pointCapturingPercentage = 0.125;
			ArrayList<GeoPointND> spl = view.getStickyPointList();
			boolean captured = false;
			if (spl != null) {
				for (int i = 0; i < spl.size(); i++) {
					GeoPoint gp = (GeoPoint) spl.get(i);
					if ((Math.abs(gp.getInhomX() - xRW) < (view.getGridDistances(0) * pointCapturingPercentage))
							&& (Math.abs(gp.getInhomY() - yRW) < (view.getGridDistances(1) * pointCapturingPercentage))) {
						xRW = gp.getInhomX();
						yRW = gp.getInhomY();
						captured = true;
						break;
					}
				}
			}
			if (captured) {
				break;
			}
	
			// fall through
		case EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC:
			if (!view.isGridOrAxesShown()) {
				break;
			}
	
			// fall through
		case EuclidianStyleConstants.POINT_CAPTURING_ON:
			pointCapturingPercentage = 0.125;
	
			// fall through
		case EuclidianStyleConstants.POINT_CAPTURING_ON_GRID:
			
			xRW += getTransformCoordsOffset(0);
			yRW += getTransformCoordsOffset(1);
	
			switch (view.getGridType()) {
			case EuclidianView.GRID_ISOMETRIC:
	
				// isometric Michael Borcherds 2008-04-28
				// iso grid is effectively two rectangular grids overlayed
				// (offset)
				// so first we decide which one we're on (oddOrEvenRow)
				// then compress the grid by a scale factor of root3
				// horizontally to make it square.
	
				double root3 = Math.sqrt(3.0);
				double isoGrid = view.getGridDistances(0);
				int oddOrEvenRow = (int) Math.round((2.0 * Math.abs(yRW
						- Kernel.roundToScale(yRW, isoGrid)))
						/ isoGrid);
	
				// Application.debug(oddOrEvenRow);
	
				if (oddOrEvenRow == 0) {
					// X = (x, y) ... next grid point
					double x = Kernel
							.roundToScale(xRW / root3, isoGrid);
					double y = Kernel.roundToScale(yRW, isoGrid);
					// if |X - XRW| < gridInterval * pointCapturingPercentage
					// then take the grid point
					double a = Math.abs(x - (xRW / root3));
					double b = Math.abs(y - yRW);
					if ((a < (isoGrid * pointCapturingPercentage))
							&& (b < (isoGrid * pointCapturingPercentage))) {
						xRW = (x * root3) - getTransformCoordsOffset(0);
						yRW = y - getTransformCoordsOffset(1);
					} else {
						xRW -= getTransformCoordsOffset(0);
						yRW -= getTransformCoordsOffset(1);
					}
	
				} else {
					// X = (x, y) ... next grid point
					double x = Kernel.roundToScale((xRW / root3)
							- (view.getGridDistances(0) / 2), isoGrid);
					double y = Kernel.roundToScale(yRW - (isoGrid / 2),
							isoGrid);
					// if |X - XRW| < gridInterval * pointCapturingPercentage
					// then take the grid point
					double a = Math.abs(x - ((xRW / root3) - (isoGrid / 2)));
					double b = Math.abs(y - (yRW - (isoGrid / 2)));
					if ((a < (isoGrid * pointCapturingPercentage))
							&& (b < (isoGrid * pointCapturingPercentage))) {
						xRW = ((x + (isoGrid / 2)) * root3)
								- getTransformCoordsOffset(0);
						yRW = (y + (isoGrid / 2)) - getTransformCoordsOffset(1);
					} else {
						xRW -= getTransformCoordsOffset(0);
						yRW -= getTransformCoordsOffset(1);
					}
	
				}
				break;
	
			case EuclidianView.GRID_CARTESIAN:
	
				// X = (x, y) ... next grid point
	
				double x = Kernel.roundToScale(xRW,
						view.getGridDistances(0));
				double y = Kernel.roundToScale(yRW,
						view.getGridDistances(1));
	
				// if |X - XRW| < gridInterval * pointCapturingPercentage then
				// take the grid point
				double a = Math.abs(x - xRW);
				double b = Math.abs(y - yRW);
	
				if ((a < (view.getGridDistances(0) * pointCapturingPercentage))
						&& (b < (view.getGridDistances(1) * pointCapturingPercentage))) {
					xRW = x - getTransformCoordsOffset(0);
					yRW = y - getTransformCoordsOffset(1);
				} else {
					xRW -= getTransformCoordsOffset(0);
					yRW -= getTransformCoordsOffset(1);
				}
				break;
	
			case EuclidianView.GRID_POLAR:
	
				// r = get nearest grid circle radius
				double r = MyMath.length(xRW, yRW);
				double r2 = Kernel.roundToScale(r,
						view.getGridDistances(0));
	
				// get nearest radial gridline angle
				double angle = Math.atan2(yRW, xRW);
				double angleOffset = angle % view.getGridDistances(2);
				if (angleOffset < (view.getGridDistances(2) / 2)) {
					angle = angle - angleOffset;
				} else {
					angle = (angle - angleOffset) + view.getGridDistances(2);
				}
	
				// get grid point
				double x1 = r2 * Math.cos(angle);
				double y1 = r2 * Math.sin(angle);
	
				// if |X - XRW| < gridInterval * pointCapturingPercentage then
				// take the grid point
				double a1 = Math.abs(x1 - xRW);
				double b1 = Math.abs(y1 - yRW);
	
				if ((a1 < (view.getGridDistances(0) * pointCapturingPercentage))
						&& (b1 < (view.getGridDistances(1) * pointCapturingPercentage))) {
					xRW = x1 - getTransformCoordsOffset(0);
					yRW = y1 - getTransformCoordsOffset(1);
				} else {
					xRW -= getTransformCoordsOffset(0);
					yRW -= getTransformCoordsOffset(1);
				}
				break;
			}
	
		default:
		}
	}

	private double getTransformCoordsOffset(int i) {
		
		// turn off for alt-drag parabola
		if (isAltDown()) {
			return 0;
		}
		
		return transformCoordsOffset[i];
	}

	protected GeoAngle createAngle(GeoPointND A, GeoPointND B, GeoPointND C) {
		checkZooming(); 
		
		return getAlgoDispatcher().Angle(null, (GeoPoint) A, (GeoPoint) B, (GeoPoint) C);
	}

	protected GeoAngle createLineAngle(GeoLine[] lines) {
		GeoAngle angle = null;
	
		checkZooming(); 
		
		// did we get two segments?
		if ((lines[0] instanceof GeoSegment)
				&& (lines[1] instanceof GeoSegment)) {
			// check if the segments have one point in common
			GeoSegment a = (GeoSegment) lines[0];
			GeoSegment b = (GeoSegment) lines[1];
			// get endpoints
			GeoPoint a1 = a.getStartPoint();
			GeoPoint a2 = a.getEndPoint();
			GeoPoint b1 = b.getStartPoint();
			GeoPoint b2 = b.getEndPoint();
	
			if (a1 == b1) {
				angle = getAlgoDispatcher().Angle(null, a2, a1, b2);
			} else if (a1 == b2) {
				angle = getAlgoDispatcher().Angle(null, a2, a1, b1);
			} else if (a2 == b1) {
				angle = getAlgoDispatcher().Angle(null, a1, a2, b2);
			} else if (a2 == b2) {
				angle = getAlgoDispatcher().Angle(null, a1, a2, b1);
			}
		}
	
		if (angle == null) {
			angle = getAlgoDispatcher().Angle(null, lines[0], lines[1]);
		}
	
		return angle;
	}

	private AlgoDispatcher getAlgoDispatcher() {
		return kernel.getAlgoDispatcher();
	}





	protected String removeUnderscores(String label) {
		// remove all indices
		return label.replaceAll("_", "");
	}

	/**
	 * Creates a text that shows a number value of geo at the current mouse
	 * position.
	 */
	protected GeoText createDynamicText(String type, GeoElement object, GeoElement value, GPoint loc) {
		// create text that shows length
		try {
			
			// type might be eg "Area of %0" or "XXX %0 YYY"
			
			String descText;
			
			if (object.isGeoPolygon()) {
				descText = descriptionPoints(type, (GeoPolygon) object);
			} else {
				descText = app.getPlain(type, "\" + Name[" + object.getLabel(StringTemplate.defaultTemplate) + "] + \"");
			}
			
			// create dynamic text
			String dynText = "\"" + descText + " = \" + " + value.getLabel(StringTemplate.defaultTemplate);
	
			checkZooming(); 
			
			GeoText text = kernel.getAlgebraProcessor().evaluateToText(dynText,
					true, true);
			text.setAbsoluteScreenLocActive(true);
			text.setAbsoluteScreenLoc(loc.x, loc.y);
			text.setBackgroundColor(GColor.WHITE);
			text.updateRepaint();
			return text;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Creates a text that shows the distance length between geoA and geoB at
	 * the given startpoint.
	 */
	protected GeoText createDistanceText(GeoElement geoA, GeoElement geoB, GeoPoint textCorner,
			GeoNumeric length) {
				StringTemplate tpl = StringTemplate.defaultTemplate;
				// create text that shows length
				try {
					String strText = "";
					boolean useLabels = geoA.isLabelSet() && geoB.isLabelSet();
					if (useLabels) {
						length.setLabel(removeUnderscores(StringUtil.toLowerCase(app.getCommand("Distance"))
								//.toLowerCase(Locale.US)
								+ geoA.getLabel(tpl)
								+ geoB.getLabel(tpl)));
						// strText = "\"\\overline{\" + Name["+ geoA.getLabel()
						// + "] + Name["+ geoB.getLabel() + "] + \"} \\, = \\, \" + "
						// + length.getLabel();
			
						// DistanceAB="\\overline{" + %0 + %1 + "} \\, = \\, " + %2
						// or
						// DistanceAB=%0+%1+" \\, = \\, "+%2
						strText = app.getPlain("DistanceAB.LaTeX",
								"Name[" + geoA.getLabel(tpl) + "]",
								"Name[" + geoB.getLabel(tpl) + "]", length.getLabel(tpl));
						// Application.debug(strText);
						geoA.setLabelVisible(true);
						geoB.setLabelVisible(true);
						geoA.updateRepaint();
						geoB.updateRepaint();
					} else {
						length.setLabel(removeUnderscores(StringUtil.toLowerCase(app.getCommand("Distance"))));
								//.toLowerCase(Locale.US)));
						strText = "\"\"" + length.getLabel(tpl);
					}
			
					// create dynamic text
					checkZooming(); 
					
					GeoText text = kernel.getAlgebraProcessor().evaluateToText(strText,
							true, true);
					if (useLabels) {
						text.setLabel(removeUnderscores(app.getPlain("Text")
								+ geoA.getLabel(tpl) + geoB.getLabel(tpl)));
						text.setLaTeX(useLabels, true);
					}
			
					text.setStartPoint(textCorner);
					text.setBackgroundColor(GColor.WHITE);
					text.updateRepaint();
					return text;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

	protected GeoElement[] area(Hits hits, AbstractEvent event) {
		if (hits.isEmpty()) {
			return null;
		}
		
		GPoint mouseCoords = event.getPoint();
	
		int count = addSelectedPolygon(hits, 1, false);
		if (count == 0) {
			addSelectedConic(hits, 2, false);
		}
	
		// area of CONIC
		if (selConics() == 1) {
			GeoConic conic = getSelectedConics()[0];
	
			// check if arc
			if (conic.isGeoConicPart()) {
				GeoConicPart conicPart = (GeoConicPart) conic;
				if (conicPart.getConicPartType() == GeoConicPart.CONIC_PART_ARC) {
					clearSelections();
					return null;
				}
			}
	
			// standard case: conic
			checkZooming(); 
			
			GeoNumeric area = getAlgoDispatcher().Area(null, conic);
	
			// text
			GeoText text = createDynamicText("AreaOfA", conic, area,
					mouseCoords);
			if (conic.isLabelSet()) {
				area.setLabel(removeUnderscores(StringUtil.toLowerCase(app.getCommand("Area"))
						+ conic.getLabelSimple()));
				text.setLabel(removeUnderscores(app.getPlain("Text")
						+ conic.getLabelSimple()));
			}
			GeoElement[] ret = { text };
			return ret;
		}
	
		// area of polygon
		else if (selPolygons() == 1) {
			GeoPolygon[] poly = getSelectedPolygons();
	
			// dynamic text with polygon's area
			GeoText text = createDynamicText("AreaOfA",  poly[0],
					poly[0], mouseLoc);
			if (poly[0].isLabelSet()) {
				text.setLabel(removeUnderscores(app.getPlain("Text")
						+ poly[0].getLabelSimple()));
			}
			GeoElement[] ret = { text };
			return ret;
		}
	
		return null;
	}

	protected String descriptionPoints(String type, GeoPolygon poly) {
		// build description text including point labels
		StringBuilder descText = new StringBuilder();
	
		// use points for polygon with static points (i.e. no list of points)
		GeoPoint[] points = null;
		if (poly.getParentAlgorithm() instanceof AlgoPolygon) {
			points = ((AlgoPolygon) poly.getParentAlgorithm()).getPoints();
		}
	
		if (points != null) {
			descText.append(" \"");
			boolean allLabelsSet = true;
			for (int i = 0; i < points.length; i++) {
				if (points[i].isLabelSet()) {
					descText.append(" + Name[" + points[i].getLabel(StringTemplate.defaultTemplate)
							+ "]");
				} else {
					allLabelsSet = false;
					i = points.length;
				}
			}
	
			if (allLabelsSet) {
				descText.append(" + \"");
				for (int i = 0; i < points.length; i++) {
					points[i].setLabelVisible(true);
					points[i].updateRepaint();
				}
			} else {
				return app.getPlain(type, "\" + Name[" + poly.getLabel(StringTemplate.defaultTemplate) + "] + \"");
			}
		}
		return app.getPlain(type,  descText.toString() );
	}

	protected boolean regularPolygon(Hits hits) {
		if (hits.isEmpty()) {
			return false;
		}
	
		// need two points
		addSelectedPoint(hits, 2, false);
	
		if (selPoints() == 2) {
			GeoPoint[] points = getSelectedPoints();
			app.getDialogManager()
					.showNumberInputDialogRegularPolygon(
							app.getMenu(getKernel().getModeText(mode)),
							points[0], points[1]);
			return true;
		}
		return false;
	}

	protected final GeoElement[] angle(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		int count = 0;
		if (selPoints() == 0) {
			if (selVectors() == 0) {
				count = addSelectedLine(hits, 2, false);
			}
			if (selLines() == 0) {
				count = addSelectedVector(hits, 2, false);
			}
		}
		if (count == 0) {
			count = addSelectedPoint(hits, 3, false);
		}
	
		// try polygon too
		boolean polyFound = false;
		if (count == 0) {
			polyFound = 1 == addSelectedGeo(
					hits.getHits(Test.GEOPOLYGON, tempArrayList), 1, false);
		}
	
		GeoAngle angle = null;
		GeoElement[] angles = null;
		if (selPoints() == 3) {
			GeoPointND[] points = getSelectedPointsND();
			angle = createAngle(points[0], points[1], points[2]);
		} else if (selVectors() == 2) {
			GeoVector[] vecs = getSelectedVectors();
			checkZooming(); 
			
			angle = getAlgoDispatcher().Angle(null, vecs[0], vecs[1]);
		} else if (selLines() == 2) {
			GeoLine[] lines = getSelectedLines();
			checkZooming(); 
			
			angle = createLineAngle(lines);
		} else if (polyFound && (selGeos() == 1)) {
			checkZooming(); 
			
			angles = getAlgoDispatcher().Angles(null, (GeoPolygon) getSelectedGeos()[0]);
		}
	
		if (angle != null) {
			// commented in V3.0:
			// angle.setAllowReflexAngle(false);
			// make sure that we show angle value
			if (angle.isLabelVisible()) {
				angle.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			} else {
				angle.setLabelMode(GeoElement.LABEL_VALUE);
			}
			angle.setLabelVisible(true);
			angle.updateRepaint();
			GeoElement[] ret = { angle };
			return ret;
		} else if (angles != null) {
			for (int i = 0; i < angles.length; i++) {
				// make sure that we show angle value
				if (angles[i].isLabelVisible()) {
					angles[i].setLabelMode(GeoElement.LABEL_NAME_VALUE);
				} else {
					angles[i].setLabelMode(GeoElement.LABEL_VALUE);
				}
				angles[i].setLabelVisible(true);
				angles[i].updateRepaint();
			}
			return angles;
		} else {
			return null;
		}
	}

	protected final GeoElement[] distance(Hits hits, AbstractEvent event) {
		if (hits.isEmpty()) {
			return null;
		}
		
		GPoint mouseCoords = event.getPoint();
	
		int count = addSelectedPoint(hits, 2, false);
		if (count == 0) {
			addSelectedLine(hits, 2, false);
		}
		if (count == 0) {
			addSelectedConic(hits, 2, false);
		}
		if (count == 0) {
			addSelectedPolygon(hits, 2, false);
		}
		if (count == 0) {
			addSelectedSegment(hits, 2, false);
		}
	
		// TWO POINTS
		if (selPoints() == 2) {
			// length
			GeoPoint[] points = getSelectedPoints();
			checkZooming(); 
			
			GeoNumeric length = getAlgoDispatcher().Distance(null, (GeoPointND) points[0],
					(GeoPointND) points[1]);
	
			// set startpoint of text to midpoint of two points
			GeoPoint midPoint = Midpoint(points[0], points[1]);
			GeoElement[] ret = { null };
			ret[0] = createDistanceText(points[0], points[1], midPoint, length);
			return ret;
		}
	
		// SEGMENT
		else if (selSegments() == 1) {
			// length
			GeoSegment[] segments = getSelectedSegments();
	
			// length
			if (segments[0].isLabelVisible()) {
				segments[0].setLabelMode(GeoElement.LABEL_NAME_VALUE);
			} else {
				segments[0].setLabelMode(GeoElement.LABEL_VALUE);
			}
			segments[0].setLabelVisible(true);
			segments[0].updateRepaint();
			return segments; // return this not null because the kernel has
								// changed
		}
	
		// TWO LINES
		else if (selLines() == 2) {
			GeoLine[] lines = getSelectedLines();
			GeoElement[] ret = { null };
			checkZooming(); 
			
			ret[0] = getAlgoDispatcher().Distance(null, lines[0], lines[1]);
			return ret; // return this not null because the kernel has changed
		}
	
		// POINT AND LINE
		else if ((selPoints() == 1) && (selLines() == 1)) {
			GeoPoint[] points = getSelectedPoints();
			GeoLine[] lines = getSelectedLines();
			GeoNumeric length = getAlgoDispatcher().Distance(null, points[0], lines[0]);
	
			checkZooming(); 
			
			// set startpoint of text to midpoint between point and line
			GeoPoint midPoint = Midpoint(points[0],
					ClosestPoint(points[0], lines[0]));
			GeoElement[] ret = { null };
			ret[0] = createDistanceText(points[0], lines[0], midPoint, length);
			return ret;
		}
	
		// circumference of CONIC
		else if (selConics() == 1) {
			GeoConic conic = getSelectedConics()[0];
			if (conic.isGeoConicPart()) {
				
				Construction cons = kernel.getConstruction();
				AlgoArcLength algo = new AlgoArcLength(cons, null, (GeoConicPart) conic);
				//cons.removeFromConstructionList(algo);
				GeoNumeric arcLength = algo.getArcLength();
				
				GeoText text = createDynamicText("ArcLengthOfA", conic, arcLength,
						mouseCoords);
					text.setLabel(removeUnderscores(app.getPlain("Text")
							+ conic.getLabelSimple()));
				GeoElement[] ret = { text };
				return ret;

				
			}
	
			// standard case: conic
			checkZooming(); 
			
			GeoNumeric circumFerence = getAlgoDispatcher().Circumference(null, conic);
	
			// text
			GeoText text = createDynamicText("CircumferenceOfA", conic,
					circumFerence, mouseCoords);
			if (conic.isLabelSet()) {
				circumFerence.setLabel(removeUnderscores(StringUtil.toLowerCase(app.getCommand(
						"Circumference"))
						+ conic.getLabel(StringTemplate.defaultTemplate)));
				text.setLabel(removeUnderscores(app.getPlain("Text")
						+ conic.getLabel(StringTemplate.defaultTemplate)));
			}
			GeoElement[] ret = { text };
			return ret;
		}
	
		// perimeter of CONIC
		else if (selPolygons() == 1) {
			GeoPolygon[] poly = getSelectedPolygons();
			checkZooming(); 
			
			GeoNumeric perimeter = getAlgoDispatcher().Perimeter(null, poly[0]);
	
			// text
			GeoText text = createDynamicText("PerimeterOfA", poly[0],
					perimeter, mouseCoords);
	
			if (poly[0].isLabelSet()) {
				perimeter.setLabel(removeUnderscores(StringUtil.toLowerCase(app.getCommand("Perimeter"))
						+ poly[0].getLabelSimple()));
				text.setLabel(removeUnderscores(app.getPlain("Text")
						+ poly[0].getLabelSimple()));
			}
			GeoElement[] ret = { text };
			return ret;
		}
	
		return null;
	}

	/**
	 * Creates Midpoint M = (P + Q)/2 without label (for use as e.g. start
	 * point)
	 */
	final private GeoPoint Midpoint(GeoPoint P, GeoPoint Q) {
		
		AlgoMidpoint algo = new AlgoMidpoint(kernel.getConstruction(), P, Q);

		return algo.getPoint();
	}


	/**
	 * Returns the projected point of P on line g (or nearest for a Segment)
	 */
	final private GeoPoint ClosestPoint(GeoPoint P, GeoLine g) {
		
		Construction cons = kernel.getConstruction();
		
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		AlgoClosestPoint cp = new AlgoClosestPoint(cons, g, P);

		cons.setSuppressLabelCreation(oldMacroMode);
		return cp.getP();
	}

	protected final boolean showCheckBox() {
		if (selectionPreview) {
			return false;
		}
	
		app.getDialogManager()
				.showBooleanCheckboxCreationDialog(mouseLoc, null);
		return false;
	}

	protected final GeoElement[] compasses(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// we already have two points that define the radius
		if (selPoints() == 2) {
			GeoPoint[] points = new GeoPoint[2];
			points[0] = (GeoPoint) selectedPoints.get(0);
			points[1] = (GeoPoint) selectedPoints.get(1);
	
			// check for centerPoint
			GeoPoint centerPoint = (GeoPoint) chooseGeo(hits, Test.GEOPOINT);
	
			if (centerPoint != null) {
				if (selectionPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add(centerPoint);
					addToHighlightedList(selectedPoints, tempArrayList, 3);
					return null;
				}
				checkZooming(); 
				
				// three points: center, distance between two points
				GeoElement circle = CircleCompasses(null, centerPoint,
						points[0], points[1]);
				GeoElement[] ret = { circle };
				clearSelections();
				return ret;
			}
		}
	
		// we already have a circle that defines the radius
		else if (selConics() == 1) {
			GeoConic circle = (GeoConic) selectedConicsND.get(0);
	
			// check for centerPoint
			GeoPoint centerPoint = (GeoPoint) chooseGeo(hits, Test.GEOPOINT);
	
			if (centerPoint != null) {
				if (selectionPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add(centerPoint);
					addToHighlightedList(selectedPoints, tempArrayList, 3);
					return null;
				}
				checkZooming(); 
				
				// center point and circle which defines radius
				GeoElement circlel = Circle(null, centerPoint,
						circle);
				GeoElement ret[] = { circlel };
				clearSelections();
				return ret;
			}
		}
		// we already have a segment that defines the radius
		else if (selSegments() == 1) {
			GeoSegment segment = selectedSegments.get(0);
	
			// check for centerPoint
			GeoPoint centerPoint = (GeoPoint) chooseGeo(hits, Test.GEOPOINT);
	
			if (centerPoint != null) {
				if (selectionPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add(centerPoint);
					addToHighlightedList(selectedPoints, tempArrayList, 3);
					return null;
				}
				checkZooming(); 
				
				// center point and segment
				GeoElement circlel = getAlgoDispatcher().Circle(null, centerPoint,
						segment);
				GeoElement[] ret = { circlel };
				clearSelections();
				return ret;
			}
		}
	
		// don't have radius yet: need two points or segment
		boolean hitPoint = (addSelectedPoint(hits, 2, false) != 0);
		if (!hitPoint && (selPoints() != 2)) {
			addSelectedSegment(hits, 1, false);
			addSelectedConic(hits, 1, false);
	
			// don't allow conics other than circles to be selected
			if (selectedConicsND.size() > 0) {
				GeoConic c = (GeoConic) selectedConicsND.get(0);
				if (!c.isCircle()) {
					selectedConicsND.remove(0);
					clearSelections();
				}
			}
		}
	
		return null;
	}


	/**
	 * circle with midpoint A and radius the same as circle Michael Borcherds
	 * 2008-03-14
	 */
	final private GeoConic Circle(
	// this is actually a macro
			String label, GeoPoint A, GeoConic c) {

		Construction cons = kernel.getConstruction();
		
		AlgoRadius radius = new AlgoRadius(cons, c);
		cons.removeFromConstructionList(radius);

		AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons, label, A,
				radius.getRadius());
		GeoConic circle = algo.getCircle();
		circle.setToSpecific();
		circle.update();
		//notifyUpdate(circle);
		return circle;
	}
	
	/**
	 * circle with midpoint M and radius BC Michael Borcherds 2008-03-14
	 */
	final private GeoConic CircleCompasses(
	// this is actually a macro
			String label, GeoPoint A, GeoPoint B, GeoPoint C) {

		Construction cons = kernel.getConstruction();

		AlgoJoinPointsSegment algoSegment = new AlgoJoinPointsSegment(cons, B,
				C, null);
		cons.removeFromConstructionList(algoSegment);

		AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons, label, A,
				algoSegment.getSegment(), true);
		GeoConic circle = algo.getCircle();
		circle.setToSpecific();
		circle.update();
		//notifyUpdate(circle);
		return circle;
	}
	
	protected final GeoElement[] vectorFromPoint(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// point
		int count = addSelectedPoint(hits, 1, false);
	
		// vector
		if (count == 0) {
			addSelectedVector(hits, 1, false);
		}
	
		if ((selPoints() == 1) && (selVectors() == 1)) {
			GeoVector[] vecs = getSelectedVectors();
			GeoPoint[] points = getSelectedPoints();
			checkZooming(); 
			
			GeoPoint endPoint = (GeoPoint) getAlgoDispatcher().Translate(null, points[0],
					vecs[0])[0];
			GeoElement[] ret = { null };
			ret[0] = getAlgoDispatcher().Vector(null, points[0], endPoint);
			return ret;
		}
		return null;
	}

	protected final boolean circlePointRadius(Hits hits) {
		if (hits.isEmpty()) {
			return false;
		}
	
		addSelectedPoint(hits, 1, false);
	
		// we got the center point
		if (selPoints() == 1) {
			app.getDialogManager()
					.showNumberInputDialogCirclePointRadius(
							app.getMenu(getKernel().getModeText(mode)),
							getSelectedPointsND()[0], view);
			return true;
		}
		return false;
	}

	/** return the current movedGeoPoint */
	public GeoElement getMovedGeoPoint() {
		return ((GeoElement) movedGeoPoint);
	}

	public final GeoPointND updateNewPoint(boolean forPreviewable, Hits hits, boolean onPathPossible,
			boolean inRegionPossible, boolean intersectPossible, boolean chooseGeo, boolean complex) {
			
				// create hits for region
				Hits regionHits = getRegionHits(hits);
			
				// only keep polygon in hits if one side of polygon is in hits too
				// removed: Point Tool creates Point on edge of Polygon
				if ((mode != EuclidianConstants.MODE_POINT)
						&& (mode != EuclidianConstants.MODE_POINT_ON_OBJECT)
						&& (mode != EuclidianConstants.MODE_COMPLEX_NUMBER)
						&& !hits.isEmpty()) {
					hits.keepOnlyHitsForNewPointMode();
				}
			
				// Application.debug(hits);
			
				Path path = null;
				Region region = null;
				boolean createPoint = true;
				if (hits.containsGeoPoint()) {
					createPoint = false;
					if (forPreviewable) {
						createNewPoint((GeoPointND) hits.getHits(Test.GEOPOINTND,
								tempArrayList).get(0));
					}
				}
			
				GeoPointND point = null;
			
				// try to get an intersection point
				if (createPoint && intersectPossible) {
					GeoPointND intersectPoint = getSingleIntersectionPoint(hits);
					if (intersectPoint != null) {
						if (!forPreviewable) {
							point = intersectPoint;
							// we don't use an undefined or infinite
							// intersection point
							if (!point.showInEuclidianView()) {
								//hits are labeled
								point.remove();
							} else {
								createPoint = false;
							}
						} else {
							createNewPointIntersection(intersectPoint);
							createPoint = false;
						}
					}
				}
			
				// Application.debug(hits+"\ncreatePoint="+createPoint+"\ninRegionPossible="+inRegionPossible+"\nchooseGeo="+chooseGeo);
			
				// check for paths and regions
				if (createPoint) {
			
					boolean createPointOnBoundary = false;
			
					// check if point lies in a region and if we are allowed to place a
					// point
					// in a region
					if (!regionHits.isEmpty()) {
						if (inRegionPossible) {
							if (chooseGeo) {
								region = (Region) chooseGeo(regionHits, true);
							} else {
								region = (Region) regionHits.get(0);
							}
							if (region != null) {
								if (((GeoElement) region).isGeoPolygon()) {
									GeoSegmentND[] sides = ((GeoPolygon) region)
											.getSegments();
									boolean sideInHits = false;
									for (int k = 0; k < sides.length; k++) {
										// sideInHits = sideInHits ||
										// hits.remove(sides[k]); //not removing sides,
										// just test
										if (hits.contains(sides[k])) {
											sideInHits = true;
											break;
										}
									}
			
									if (!sideInHits) {
										createPoint = true;
										hits.removePolygonsIfSideNotPresent(); // if a
																				// polygon
																				// is a
																				// region,
																				// need
																				// only
																				// polygons
																				// that
																				// should
																				// be a
																				// path
										if (mode == EuclidianConstants.MODE_POINT_ON_OBJECT) {
											hits.removeSegmentsFromPolygons(); // remove
																				// polygon's
																				// segments
																				// to
																				// take
																				// the
																				// polygon
																				// for
																				// path
										}
									} else {
										if (mode == EuclidianConstants.MODE_POINT_ON_OBJECT) {
											// if one wants a point on boundary of a
											// polygon
											createPoint = false;
											createPointOnBoundary = true;
										} else {
											createPoint = false;
											hits.remove(region); // (OPTIONAL) if side
																	// is in hits, still
																	// don't need the
																	// polygon as a path
											region = null;
										}
									}
								} else if (((GeoElement) region).isGeoConic()) {
									if ((mode == EuclidianConstants.MODE_POINT_ON_OBJECT)
											&& (((GeoConicND) region).getLastHitType() == HitType.ON_FILLING)) {
										createPoint = true;
										hits.remove(region); // conic won't be treated
																// as a path
									} else {
										createPoint = true;
									}
								}
							} else {
								createPoint = true;
							}
						} else {
							createPoint = true;
							// if inRegionPossible is false, the point is created as a
							// free point
						}
					}
			
					// check if point lies on path and if we are allowed to place a
					// point
					// on a path
					if (createPointOnBoundary) {
						// special case for MODE_POINT_ON_OBJECT : if an edge of a
						// polygon is clicked, create Point[polygon]
						path = (Path) region;
						region = null;
						createPoint = true;
					} else {
						Hits pathHits = hits.getHits(Test.PATH, tempArrayList);
						if (!pathHits.isEmpty()) {
							if (onPathPossible) {
								if (chooseGeo) {
									path = (Path) chooseGeo(pathHits, true);
								} else {
									path = (Path) pathHits.get(0);
								}
								createPoint = path != null;
							} else {
								createPoint = true;
							}
						}
					}
				}
			
				// Application.debug("createPoint 3 = "+createPoint);
			
				if (createPoint) {
					transformCoords(); // use point capturing if on
					// branches reordered to prefer path, and then region
					if ((path != null) && onPathPossible) {
						point = createNewPoint(forPreviewable, path, complex);
					} else if ((region != null) && inRegionPossible) {
						point = createNewPoint(forPreviewable, region, complex);
					} else {
						point = createNewPoint(forPreviewable, complex);
						view.setShowMouseCoords(true);
					}
				}
			
				return point;
			}

	protected GeoPointND getNewPoint(Hits hits, boolean onPathPossible, boolean inRegionPossible,
			boolean intersectPossible, boolean complex) {
			
				return updateNewPoint(false, hits, onPathPossible, inRegionPossible,
						intersectPossible, true, complex);
			}

	protected boolean createNewPoint(Hits hits, boolean onPathPossible,
			boolean inRegionPossible, boolean intersectPossible, boolean doSingleHighlighting, boolean complex) {
			
				if (!allowPointCreation()) {
					return false;
				}
			
				GeoPointND point = getNewPoint(hits, onPathPossible, inRegionPossible,
						intersectPossible, complex);
			
				if (point != null) {
			
					updateMovedGeoPoint(point);
			
					movedGeoElement = getMovedGeoPoint();
					moveMode = MOVE_POINT;
					view.setDragCursor();
					if (doSingleHighlighting) {
						doSingleHighlighting(getMovedGeoPoint());
					}
					POINT_CREATED = true;
			
					return true;
				}
				moveMode = MOVE_NONE;
				POINT_CREATED = false;
				return false;
			}

	protected final boolean createNewPoint(Hits hits, boolean onPathPossible,
			boolean intersectPossible, boolean doSingleHighlighting) {
			
				// inRegionpossible must be false so that the Segment Tool creates a
				// point on the edge of a circle
				return createNewPoint(hits, onPathPossible, false, intersectPossible,
						doSingleHighlighting, false);
			}

	/**
	 * Handles selected objects for a macro
	 * 
	 * @param hits
	 * @return
	 */
	protected final boolean macro(Hits hits) {
		// try to get next needed type of macroInput
		int index = selGeos();
	
		// standard case: try to get one object of needed input type
		boolean objectFound = 1 == handleAddSelected(hits,
				macroInput.length, false, selectedGeos, macroInput[index]);
	
		// some old code for polygon removed in [6779]
	
		// we're done if in selection preview
		if (selectionPreview) {
			return false;
		}
	
		// only one point needed: try to create it
		if (!objectFound && (macroInput[index].equals(Test.GEOPOINT)||macroInput[index].equals(Test.GEOPOINTND))) {
			if (createNewPoint(hits, true, true, false)) {
				// take movedGeoPoint which is the newly created point
				selectedGeos.add(getMovedGeoPoint());
				app.addSelectedGeo(getMovedGeoPoint());
				objectFound = true;
				POINT_CREATED = false;
			}
		}
	
		// object found in handleAddSelected()
		if (objectFound || macroInput[index].equals(Test.GEONUMERIC)
				|| macroInput[index].equals(Test.GEOANGLE)) {
			if (!objectFound) {
				index--;
			}
			// look ahead if we need a number or an angle next
			while (++index < macroInput.length) {
				// maybe we need a number
				if (macroInput[index].equals(Test.GEONUMERIC)) {
					NumberValue num = app
							.getDialogManager()
							.showNumberInputDialog(
									macro.getToolOrCommandName(),
									app.getPlain("Numeric"), null);
					if (num == null) {
						// no success: reset mode
						view.resetMode();
						return false;
					}
					// great, we got our number
					if (num.isGeoElement()) {
						selectedGeos.add(num.toGeoElement());
					}
				}
	
				// maybe we need an angle
				else if (macroInput[index].equals(Test.GEOANGLE)) {
					Object[] ob = app
							.getDialogManager()
							.showAngleInputDialog(macro.getToolOrCommandName(),
									app.getPlain("Angle"), "45\u00b0");
					NumberValue num = (NumberValue) ob[0];
	
					if (num == null) {
						// no success: reset mode
						view.resetMode();
						return false;
					}
					// great, we got our angle
					if (num.isGeoElement()) {
						selectedGeos.add(num.toGeoElement());
					}
				} else {
					break;
				}
			}
		}
	
		// Application.debug("index: " + index + ", needed type: " +
		// macroInput[index]);
	
		// do we have everything we need?
		if (selGeos() == macroInput.length) {
			checkZooming(); 
			
			kernel.useMacro(null, macro, getSelectedGeos());
			return true;
		}
		return false;
	}

	protected final boolean button(boolean textfield) {
		if (!selectionPreview && (mouseLoc != null)) {
			app.getDialogManager()
					.showButtonCreationDialog(mouseLoc.x, mouseLoc.y, textfield);
		}
		return false;
	}
	
	protected boolean switchModeForProcessMode(Hits hits, AbstractEvent event) {
	
		Boolean changedKernel = false;
		GeoElement[] ret = null;
	
		switch (mode) {
		//case EuclidianConstants.MODE_VISUAL_STYLE:
		case EuclidianConstants.MODE_MOVE:
			// move() is for highlighting and selecting
			if (selectionPreview) {
				move(hits.getTopHits());
			} else {
				if (DRAGGING_OCCURED && (app.selectedGeosSize() == 1)) {
					app.clearSelectedGeos();
				}
	
			}
			break;
	
		case EuclidianConstants.MODE_MOVE_ROTATE:
			// moveRotate() is a dummy function for highlighting only
			if (selectionPreview) {
				moveRotate(hits.getTopHits());
			}
			break;
	
		case EuclidianConstants.MODE_POINT:
		case EuclidianConstants.MODE_COMPLEX_NUMBER:
		case EuclidianConstants.MODE_POINT_ON_OBJECT:
			// point() is dummy function for highlighting only
			if (selectionPreview) {
				if ((mode == EuclidianConstants.MODE_POINT)
						|| (mode == EuclidianConstants.MODE_COMPLEX_NUMBER)) {
					hits.keepOnlyHitsForNewPointMode();
				}
	
				point(hits);
			} else {
				GeoElement[] ret0 = { null };
				ret0[0] = hits.getFirstHit(Test.GEOPOINTND);
				ret = ret0;
				clearSelection(selectedPoints);
			}
			break;
	
		// copy geo to algebra input
		case EuclidianConstants.MODE_SELECTION_LISTENER:
			boolean addToSelection = (event != null)
					&& (app.isControlDown(event));
			geoElementSelected(hits.getTopHits(), addToSelection);
			break;
	
		// new line through two points
		case EuclidianConstants.MODE_JOIN:
			ret = join(hits);
			break;
	
		// new segment through two points
		case EuclidianConstants.MODE_SEGMENT:
			ret = segment(hits);
			break;
	
		// segment for point and number
		case EuclidianConstants.MODE_SEGMENT_FIXED:
			changedKernel = segmentFixed(hits);
			break;
	
		// angle for two points and number
		case EuclidianConstants.MODE_ANGLE_FIXED:
			ret = angleFixed(hits);
			break;
	
		case EuclidianConstants.MODE_MIDPOINT:
			ret = midpoint(hits);
			break;
	
		// new ray through two points or point and vector
		case EuclidianConstants.MODE_RAY:
			ret = ray(hits);
			break;
	
		case EuclidianConstants.MODE_POLYLINE:
			ret = polyline(hits);
			break;
	
		// new polygon through points
		case EuclidianConstants.MODE_POLYGON:
			polygonMode = POLYGON_NORMAL;
			ret = polygon(hits);
			break;
	
		case EuclidianConstants.MODE_RIGID_POLYGON:
			polygonMode = POLYGON_RIGID;
			ret = polygon(hits);
			break;
	
		case EuclidianConstants.MODE_VECTOR_POLYGON:
			polygonMode = POLYGON_VECTOR;
			ret = polygon(hits);
			break;
	
		// new vector between two points
		case EuclidianConstants.MODE_VECTOR:
			ret = vector(hits);
			break;
	
		// intersect two objects
		case EuclidianConstants.MODE_INTERSECT:
			ret = intersect(hits);
			break;
	
		// new line through point with direction of vector or line
		case EuclidianConstants.MODE_PARALLEL:
			ret = parallel(hits);
			break;
	
		// Michael Borcherds 2008-04-08
		case EuclidianConstants.MODE_PARABOLA:
			ret = parabola(hits);
			break;
	
		// new line through point orthogonal to vector or line
		case EuclidianConstants.MODE_ORTHOGONAL:
			ret = orthogonal(hits);
			break;
	
		// new line bisector
		case EuclidianConstants.MODE_LINE_BISECTOR:
			ret = lineBisector(hits);
			break;
	
		// new angular bisector
		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
			ret = angularBisector(hits);
			break;
	
		// new circle (2 points)
		case EuclidianConstants.MODE_CIRCLE_TWO_POINTS:
			// new semicircle (2 points)
		case EuclidianConstants.MODE_SEMICIRCLE:
			ret = circleOrSphere2(hits, mode);
			break;
	
		case EuclidianConstants.MODE_LOCUS:
			ret = locus(hits);
			break;
	
		// new circle (3 points)
		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			ret = threePoints(hits, mode);
			break;
	
		// new conic (5 points)
		case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
			ret = conic5(hits);
			break;
	
		// relation query
		case EuclidianConstants.MODE_RELATION:
			relation(hits.getTopHits());
			break;
	
		// new tangents
		case EuclidianConstants.MODE_TANGENTS:
			ret = tangents(hits.getTopHits());
			break;
	
		case EuclidianConstants.MODE_POLAR_DIAMETER:
			ret = polarLine(hits.getTopHits());
			break;
	
		// delete selected object
		case EuclidianConstants.MODE_DELETE:
			changedKernel = delete(hits.getTopHits());
			break;
	
		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
			if (showHideObject(hits.getTopHits())) {
				toggleModeChangedKernel = true;
			}
			break;
	
		case EuclidianConstants.MODE_SHOW_HIDE_LABEL:
			if (showHideLabel(hits.getTopHits())) {
				toggleModeChangedKernel = true;
			}
			break;
	
		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
			if (copyVisualStyle(hits.getTopHits())) {
				toggleModeChangedKernel = true;
			}
			break;
	
		// new text
		case EuclidianConstants.MODE_TEXT:
			changedKernel = text(
					hits.getOtherHits(Test.GEOIMAGE, tempArrayList));
			break;
	
		// new image
		case EuclidianConstants.MODE_IMAGE:
			changedKernel = image(
					hits.getOtherHits(Test.GEOIMAGE, tempArrayList)); // e.isAltDown());
			break;
	
		// new slider
		case EuclidianConstants.MODE_SLIDER:
			changedKernel = slider();
			break;
	
		case EuclidianConstants.MODE_MIRROR_AT_POINT:
			ret = mirrorAtPoint(hits.getTopHits());
			break;
	
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
			ret = mirrorAtLine(hits.getTopHits());
			break;
	
		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds
														// 2008-03-23
			ret = mirrorAtCircle(hits.getTopHits());
			break;
	
		case EuclidianConstants.MODE_ATTACH_DETACH: // Michael Borcherds
													// 2008-03-23
			changedKernel = attachDetach(hits.getTopHits());
			break;
	
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			ret = translateByVector(hits.getTopHits());
			break;
	
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			ret = rotateByAngle(hits.getTopHits());
			break;
	
		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			ret = dilateFromPoint(hits.getTopHits());
			break;
	
		case EuclidianConstants.MODE_FITLINE:
			ret = fitLine(hits);
			break;
	
		case EuclidianConstants.MODE_CREATE_LIST:
			ret = createList(hits);
			break;
	
		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS:
			changedKernel = circlePointRadius(hits);
			break;
	
		case EuclidianConstants.MODE_ANGLE:
			ret = angle(hits.getTopHits());
			break;
	
		case EuclidianConstants.MODE_VECTOR_FROM_POINT:
			ret = vectorFromPoint(hits);
			break;
	
		case EuclidianConstants.MODE_DISTANCE:
			ret = distance(hits, event);
			break;
	
		case EuclidianConstants.MODE_MACRO:
			changedKernel = macro(hits);
			break;
	
		case EuclidianConstants.MODE_AREA:
			ret = area(hits, event);
			break;
	
		case EuclidianConstants.MODE_SLOPE:
			ret = slope(hits);
			break;
	
		case EuclidianConstants.MODE_REGULAR_POLYGON:
			changedKernel = regularPolygon(hits);
			break;
	
		case EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX:
			changedKernel = showCheckBox();
			break;
	
		case EuclidianConstants.MODE_BUTTON_ACTION:
			changedKernel = button(false);
			break;
	
		case EuclidianConstants.MODE_TEXTFIELD_ACTION:
			changedKernel = button(true);
			break;
	
		case EuclidianConstants.MODE_PEN:
		//case EuclidianConstants.MODE_PENCIL:
		case EuclidianConstants.MODE_FREEHAND_SHAPE:
			//changedKernel = pen();
			break;
	
		// Michael Borcherds 2008-03-13
		case EuclidianConstants.MODE_COMPASSES:
			ret = compasses(hits);
			break;
	
		case EuclidianConstants.MODE_FUNCTION_INSPECTOR:
			changedKernel = functionInspector(hits);
			break;
	
		default:
			// do nothing
		}
	
		if (ret != null) {
			memorizeJustCreatedGeos(ret);
		} else if (!selectionPreview) {
			clearJustCreatedGeos();
		}
	
		if (!changedKernel) {
			return ret != null;
		}
	
		return changedKernel;
	}

	protected void processModeLock(Path path) {
		checkZooming();
		
		GeoPoint p = getAlgoDispatcher().Point(null, path, xRW, yRW, false, false);
		p.update();
		xRW = p.inhomX;
		yRW = p.inhomY;
	}

	protected void processModeLock(GeoPointND point) {
		Coords coords = point.getInhomCoordsInD(2);
		xRW = coords.getX();
		yRW = coords.getY();
	}

	public void processModeLock() {
	
		// make previewable "lock" onto points & paths
		// priority for highlighted geos (points)
		Hits getTopHits = highlightedGeos.getTopHits();
		// nothing highlighted, look at eg circles, lines
		if (getTopHits.size() == 0) {
			getTopHits = view.getHits().getTopHits();
		}
	
		if (getTopHits.size() > 0) {
			GeoElement geo = getTopHits.get(0);
			if (geo instanceof Path) {
				processModeLock((Path) geo);
			} else if (geo.isGeoPoint()) {
				processModeLock((GeoPointND) geo);
			} else {
				transformCoords(); // grid lock
			}
		} else {
			if (!isAltDown()) {
				// interferes with Alt to get multiples of 15  degrees (web)
				transformCoords(); // grid lock
			}
		}
	}

	
	public final boolean processMode(Hits processHits, AbstractEvent event) {
		Hits hits = processHits;
		boolean changedKernel = false;
	
		if (hits == null) {
			hits = new Hits();
		}
	
		changedKernel = switchModeForProcessMode(hits, event);
	
		// update preview
		if (view.getPreviewDrawable() != null) {
			view.getPreviewDrawable().updatePreview();
			if (mouseLoc != null) {
				xRW = view.toRealWorldCoordX(mouseLoc.x);
				yRW = view.toRealWorldCoordY(mouseLoc.y);
	
				processModeLock();
	
				view.getPreviewDrawable().updateMousePos(xRW, yRW);
			}
			view.repaintView();
		}
	
		return changedKernel;
	}

	/**
	 * @param event in 3D we need to check left/right click 
	 */
	protected void processReleaseForMovedGeoPoint(AbstractEvent event) {
	
		// deselect point after drag, but not on click
		// outdated - we want to leave the point selected after drag now
		// if (movedGeoPointDragged) getMovedGeoPoint().setSelected(false);
	
		if ((mode != EuclidianConstants.MODE_RECORD_TO_SPREADSHEET)
				&& app.isUsingFullGui()) {
			getMovedGeoPoint().resetTraceColumns();
		}
	
	}

	/**
	 * right-release the mouse makes stop 3D rotation
	 * 
	 * @return false
	 */
	protected boolean processRightReleaseFor3D() {
		return false;
	}

	protected final void rotateObject(boolean repaint) {
		double angle = Math.atan2(yRW - rotationCenter.inhomY, xRW
				- rotationCenter.inhomX)
				- rotStartAngle;
	
		tempNum.set(angle);
		rotGeoElement.set(rotStartGeo);
		((PointRotateable) rotGeoElement).rotate(tempNum, rotationCenter);
	
		if (repaint) {
			rotGeoElement.updateRepaint();
		} else {
			rotGeoElement.updateCascade();
		}
	}

	protected final void moveLabel() {
		movedLabelGeoElement.setLabelOffset((oldLoc.x + mouseLoc.x)
				- startLoc.x, (oldLoc.y + mouseLoc.y) - startLoc.y);
		// no update cascade needed
		movedLabelGeoElement.update();
		kernel.notifyRepaint();
	}

	protected void movePoint(boolean repaint, AbstractEvent event) {
		movedGeoPoint.setCoords(Kernel.checkDecimalFraction(xRW),
				Kernel.checkDecimalFraction(yRW), 1.0);

		if (event.isAltDown()) {

			// 1/24 -> steps of 15 degrees (for circle)
			// otherwise use Object Properties -> Algebra -> Increment
			//double multiplier = event.isAltDown() ? 1.0/24.0 : movedGeoPoint.getAnimationStep();
			
			double multiplier = movedGeoPoint.getAnimationStep();
			
			int n = (int) Math.ceil(1.0 / multiplier);
			
			if (n < 1) {
				n = 1;
			}

			if (movedGeoPoint.hasPath()) {

				double dist = Double.MAX_VALUE;

				Path path = movedGeoPoint.getPath();

				double t = movedGeoPoint.getPathParameter().t;

				// convert to 0 <= t < 1
				t = PathNormalizer.toNormalizedPathParameter(t, path.getMinParameter(), path.getMaxParameter());

				double t_1 = t;

				// find closest parameter
				// avoid rounding errors by using an int & multiplier
				for (int i = 0 ; i < n ; i ++) {
					if (Math.abs(t - i * multiplier) < dist) {
						t_1 = i * multiplier;
						dist = Math.abs(t - i * multiplier);
					}
				}

				movedGeoPoint.getPathParameter().t = PathNormalizer.toParentPathParameter(t_1, path.getMinParameter(), path.getMaxParameter());

				path.pathChanged(movedGeoPoint);
				movedGeoPoint.updateCoords();

			}
		}


		((GeoElement) movedGeoPoint).updateCascade();
		movedGeoPointDragged = true;

		if (repaint) {
			kernel.notifyRepaint();
		}
	}

	protected void movePointWithOffset(boolean repaint) {
		movedGeoPoint.setCoords(
				Kernel.checkDecimalFraction(xRW - transformCoordsOffset[0]),
				Kernel.checkDecimalFraction(yRW - transformCoordsOffset[1]),
				1.0);
		((GeoElement) movedGeoPoint).updateCascade();
		movedGeoPointDragged = true;
	
		if (repaint) {
			kernel.notifyRepaint();
		}
	}

	protected final void moveLine(boolean repaint) {
		// make parallel geoLine through (xRW, yRW)
		movedGeoLine.setCoords(movedGeoLine.x, movedGeoLine.y,
				-((movedGeoLine.x * xRW) + (movedGeoLine.y * yRW)));
		if (repaint) {
			movedGeoLine.updateRepaint();
		} else {
			movedGeoLine.updateCascade();
		}
	}

	protected final void moveVector(boolean repaint) {
		GeoPoint P = movedGeoVector.getStartPoint();
		if (P == null) {
			movedGeoVector.setCoords(xRW - transformCoordsOffset[0], yRW
					- transformCoordsOffset[1], 0.0);
		} else {
			movedGeoVector.setCoords(xRW - P.inhomX, yRW - P.inhomY, 0.0);
		}
	
		if (repaint) {
			movedGeoVector.updateRepaint();
		} else {
			movedGeoVector.updateCascade();
		}
	}

	protected final void moveVectorStartPoint(boolean repaint) {
		GeoPoint P = movedGeoVector.getStartPoint();
		P.setCoords(xRW, yRW, 1.0);
	
		if (repaint) {
			movedGeoVector.updateRepaint();
		} else {
			movedGeoVector.updateCascade();
		}
	}

	protected final void moveText(boolean repaint) {
		
		if (movedGeoText.isAbsoluteScreenLocActive()) {
			movedGeoText.setAbsoluteScreenLoc((oldLoc.x + mouseLoc.x)
					- startLoc.x, (oldLoc.y + mouseLoc.y) - startLoc.y);
	
			// part of snap to grid code - buggy, so commented out
			// movedGeoText.setAbsoluteScreenLoc(view.toScreenCoordX(xRW -
			// startPoint.x), view.toScreenCoordY(yRW - startPoint.y));
		} else {
			if (movedGeoText.hasAbsoluteLocation()) {
				// absolute location: change location
				moveTextAbsoluteLocation();
				
			} else {
				// relative location: move label (change label offset)
				movedGeoText.setLabelOffset((oldLoc.x + mouseLoc.x)
						- startLoc.x, (oldLoc.y + mouseLoc.y) - startLoc.y);
			}
		}
	
		if (repaint) {
			movedGeoText.updateRepaint();
		} else {
			movedGeoText.updateCascade();
		}
	}
	
	protected void moveTextAbsoluteLocation(){
		GeoPoint loc = (GeoPoint) movedGeoText.getStartPoint();
		loc.setCoords(xRW - startPoint.x, yRW - startPoint.y, 1.0);
	}

	protected final void moveImage(boolean repaint) {
		if (movedGeoImage.isAbsoluteScreenLocActive()) {
			// movedGeoImage.setAbsoluteScreenLoc( oldLoc.x +
			// mouseLoc.x-startLoc.x,
			// oldLoc.y + mouseLoc.y-startLoc.y);
	
			movedGeoImage.setAbsoluteScreenLoc(
					view.toScreenCoordX(xRW - startPoint.x),
					view.toScreenCoordY(yRW - startPoint.y));
	
			if (repaint) {
				movedGeoImage.updateRepaint();
			} else {
				movedGeoImage.updateCascade();
			}
		} else {
			if (movedGeoImage.hasAbsoluteLocation()) {
				// absolute location: translate all defined corners
				double vx = xRW - startPoint.x;
				double vy = yRW - startPoint.y;
				movedGeoImage.set(oldImage);
				for (int i = 0; i < 3; i++) {
					GeoPoint corner = movedGeoImage.getCorner(i);
					if (corner != null) {
						corner.setCoords(corner.inhomX + vx,
								corner.inhomY + vy, 1.0);
					}
				}
	
				if (repaint) {
					movedGeoImage.updateRepaint();
				} else {
					movedGeoImage.updateCascade();
				}
			}
		}
	}
	

	protected final void moveConic(boolean repaint) {
		
		if (isAltDown()  && (movedGeoConic.getType() == GeoConicNDConstants.CONIC_PARABOLA || movedGeoConic.getType() == GeoConicNDConstants.CONIC_DOUBLE_LINE)) {
			
			// drag a parabola bit keep the vertex fixed
			// CONIC_DOUBLE_LINE needed for y=0x^2
			
			
			double vX = movedGeoConic.b.getX();
			double vY = movedGeoConic.b.getY();
			
			int index = movedGeoConic.getType() == GeoConicNDConstants.CONIC_PARABOLA?0:1;
			double c = movedGeoConic.getEigenvec(index).getX();
			double s = movedGeoConic.getEigenvec(index).getY();
			
			double coeff;
			double dx = xRW - vX;
			double dy = yRW - vY;
			coeff =  (c*dx+s*dy) / ( (s*dx - c*dy) * (s*dx - c*dy) );
			if (coeff > 1E8) {
			    coeff = 1E6;
			   } else if (coeff < -1E8) {
			    coeff = -1E6;
			   }
			movedGeoConic.translate(-vX, -vY);
			
			movedGeoConic.setCoeffs(coeff*s*s, -2*coeff*s*c, coeff*c*c, -c, -s, 0);
			
			movedGeoConic.translate(vX, vY);
		} else {
			// just translate conic
			movedGeoConic.set(tempConic);
			movedGeoConic.translate(xRW - startPoint.x, yRW - startPoint.y);			
		}
		
	
		if (repaint) {
			movedGeoConic.updateRepaint();
		} else {
			movedGeoConic.updateCascade();
		}
	}

	protected final void moveImplicitPoly(boolean repaint) {
		movedGeoImplicitPoly.set(tempImplicitPoly);
		movedGeoImplicitPoly.translate(xRW - startPoint.x, yRW - startPoint.y);
	
		// set points
		for (int i = 0; i < moveDependentPoints.size(); i++) {
			GeoPoint g = moveDependentPoints.get(i);
			g.setCoords2D(tempDependentPointX.get(i),
					tempDependentPointY.get(i), 1);
			g.translate(new Coords(xRW - startPoint.x, yRW - startPoint.y, 1));
			// g.updateCascade();
		}
	
		if (repaint) {
			movedGeoImplicitPoly.updateRepaint();
		} else {
			movedGeoImplicitPoly.updateCascade();
		}
	
		// int i=0;
		// for (GeoElement elem:movedGeoImplicitPoly.getAllChildren()){
		// if (elem instanceof GeoPoint){
		// if (movedGeoImplicitPoly.isParentOf(elem)){
		// GeoPoint g=((GeoPoint)elem);
		// g.getPathParameter().setT(tempDependentPointOnPath.get(i++));
		// tempImplicitPoly.pathChanged(g);
		// g.translate(new Coords(xRW - startPoint.x, yRW - startPoint.y));
		// }
		// }else if (elem instanceof GeoImplicitPoly){
		//
		// }
		// }
	
	}

	double vertexX = Double.NaN, vertexY = Double.NaN;

	protected final void moveFreehand(boolean repaint) {
	
		movedGeoFunction.set(tempFunction);
		movedGeoFunction.translate(xRW - startPoint.x, yRW - startPoint.y);
	
		startPoint.x = xRW;
		startPoint.y = yRW;
		
	if (repaint) {
		movedGeoFunction.updateRepaint();
	} else {
		movedGeoFunction.updateCascade();
	}
	
	}
	
	
	protected final void moveFunction(boolean repaint) {
		
		boolean quadratic = false;
		
		if (isAltDown() && !Double.isNaN(vertexX) && movedGeoFunction.isIndependent()) {
			quadratic = true;
		}
		
		
		if (quadratic) {
			double p = (yRW - vertexY) / ( (xRW - vertexX) * (xRW - vertexX) );
			
			// slow method, less code
			//GeoFunction geo = kernel.getAlgebraProcessor().evaluateToFunction(p +" * (x - "+vertexX+")^2 + "+vertexY , true);
			
			// fast method, doesn't use parser
			MyDouble a = new MyDouble(kernel, p);
			MyDouble h = new MyDouble(kernel, vertexX);
			MyDouble k = new MyDouble(kernel, vertexY);
			
			FunctionVariable fv = new FunctionVariable(kernel);	
			ExpressionNode squareE = new ExpressionNode(kernel,fv,Operation.MINUS,h)
						.power(new MyDouble(kernel,2)).multiply(a).plus(k);
			Function squareF = new Function(squareE,fv);
			squareF.initFunction();
			GeoFunction square = new GeoFunction(kernel.getConstruction());		
			square.setFunction(squareF);		

			movedGeoFunction.set(square);			
		} else {
			movedGeoFunction.set(tempFunction);
			movedGeoFunction.translate(xRW - startPoint.x, yRW - startPoint.y);
		}
		
		if (repaint) {
			movedGeoFunction.updateRepaint();
		} else {
			movedGeoFunction.updateCascade();
		}
	}
	
	protected final void moveBoolean(boolean repaint) {
		// movedGeoBoolean.setAbsoluteScreenLoc( oldLoc.x +
		// mouseLoc.x-startLoc.x,
		// oldLoc.y + mouseLoc.y-startLoc.y);
	
		// part of snap to grid code
		movedGeoBoolean.setAbsoluteScreenLoc(
				view.toScreenCoordX(xRW - startPoint.x),
				view.toScreenCoordY(yRW - startPoint.y));
	
		if (repaint) {
			movedGeoBoolean.updateRepaint();
		} else {
			movedGeoBoolean.updateCascade();
		}
	}

	protected final void moveButton(boolean repaint) {
		// movedGeoButton.setAbsoluteScreenLoc( oldLoc.x +
		// mouseLoc.x-startLoc.x,
		// oldLoc.y + mouseLoc.y-startLoc.y);
	//AbstractApplication.printStacktrace("");
		// part of snap to grid code
		movedGeoButton.setAbsoluteScreenLoc(
				view.toScreenCoordX(xRW - startPoint.x),
				view.toScreenCoordY(yRW - startPoint.y));
	
		if (repaint) {
			movedGeoButton.updateRepaint();
		} else {
			movedGeoButton.updateCascade();
		}
	}

	protected final double getSliderValue(GeoNumeric movedSlider) {
		double min = movedSlider.getIntervalMin();
		double max = movedSlider.getIntervalMax();
		double param;
		if (movedSlider.isSliderHorizontal()) {
			if (movedSlider.isAbsoluteScreenLocActive()) {
				param = mouseLoc.x - startPoint.x;
			} else {
				param = xRW - startPoint.x;
			}
		} else {
			if (movedSlider.isAbsoluteScreenLocActive()) {
				param = startPoint.y - mouseLoc.y;
			} else {
				param = yRW - startPoint.y;
			}
		}
		param = (param * (max - min)) / movedSlider.getSliderWidth();
	
		// round to animation step scale
		param = Kernel.roundToScale(param,
				movedSlider.getAnimationStep());
		double val = min + param;
	
		if (movedSlider.getAnimationStep() > Kernel.MIN_PRECISION) {
			// round to decimal fraction, e.g. 2.800000000001 to 2.8
			val = Kernel.checkDecimalFraction(val);
		}
	
		if (movedSlider.isGeoAngle()) {
			if (val < 0) {
				val = 0;
			} else if (val > Kernel.PI_2) {
				val = Kernel.PI_2;
			}
	
			val = Kernel.checkDecimalFraction(val
					* Kernel.CONST_180_PI)
					/ Kernel.CONST_180_PI;
	
		}
	
		return val;
	}

	/**
	 * @param repaint TODO ignored now -- on purpose ? 
	 */
	protected final void moveNumeric(boolean repaint) {
	
		double newVal = getSliderValue(movedGeoNumeric);
		double oldVal = movedGeoNumeric.getValue();
	
		// don't set the value unless needed
		// (causes update)
		double min = movedGeoNumeric.getIntervalMin();
		if ((min == oldVal) && (newVal < min)) {
			return;
		}
		double max = movedGeoNumeric.getIntervalMax();
		if ((max == oldVal) && (newVal > max)) {
			return;
		}
	
		// do not set value unless it really changed!
		if (oldVal == newVal) {
			return;
		}
	
		movedGeoNumeric.setValue(newVal);
		movedGeoNumericDragged = true;
	
		// movedGeoNumeric.setAnimating(false); // stop animation if slider
		// dragged
	
		// if (repaint)
		movedGeoNumeric.updateRepaint();
		// else
		// movedGeoNumeric.updateCascade();
	}

	protected final void moveSlider(boolean repaint) {
	
		// TEMPORARY_MODE true -> dragging slider using Slider Tool
		// or right-hand mouse button
	
		if (movedGeoNumeric.isAbsoluteScreenLocActive()) {
			// movedGeoNumeric.setAbsoluteScreenLoc( oldLoc.x +
			// mouseLoc.x-startLoc.x,
			// oldLoc.y + mouseLoc.y-startLoc.y, TEMPORARY_MODE);
	
			// part of snap to grid code
			movedGeoNumeric.setAbsoluteScreenLoc(
					view.toScreenCoordX(xRW - startPoint.x),
					view.toScreenCoordY(yRW - startPoint.y), TEMPORARY_MODE);
		} else {
			movedGeoNumeric.setSliderLocation(xRW - startPoint.x, yRW
					- startPoint.y, TEMPORARY_MODE);
		}
	
		// don't cascade, only position of the slider has changed
		movedGeoNumeric.update();
	
		if (repaint) {
			kernel.notifyRepaint();
		}
	}

	protected void moveDependent(boolean repaint) {
	
		translationVec.setX(xRW - startPoint.x);
		translationVec.setY(yRW - startPoint.y);
	
		startPoint.setLocation(xRW, yRW);
	
		// we don't specify screen coords for translation as all objects are
		// Transformables
		GeoElement.moveObjects(translateableGeos, translationVec, new Coords(
				xRW, yRW, 0), null);
		if (repaint) {
			kernel.notifyRepaint();
		}
	}
	
	protected void moveAttached(boolean repaint) {
		
		AlgoElement algo = movedGeoElement.getParentAlgorithm();
		GeoPoint pt1 = (GeoPoint)algo.getInput()[4];
		GeoPoint pt2 = (GeoPoint)algo.getInput()[5];
		double dx = view.getXscale()*(xRW - startPoint.x);
		double dy = view.getYscale()*(yRW - startPoint.y);
		startPoint.setLocation(xRW, yRW);
		pt1.setCoords(pt1.getX()+dx,pt1.getY()-dy,1);
		pt2.setCoords(pt2.getX()+dx,pt2.getY()-dy,1);
		App.debug(xRW+","+yRW+":"+startPoint.x+","+startPoint.y);
		algo.update();
		 
		if (repaint) {
			kernel.notifyRepaint();
		}
	}

	protected ArrayList<GeoElement> removeParentsOfView(ArrayList<GeoElement> list) {
		return list;
	}

	protected void moveMultipleObjects(boolean repaint) {
		translationVec.setX(xRW - startPoint.x);
		translationVec.setY(yRW - startPoint.y);
		startPoint.setLocation(xRW, yRW);
		startLoc = mouseLoc;
	
		// move all selected geos
		GeoElement.moveObjects(removeParentsOfView(app.getSelectedGeos()),
				translationVec, new Coords(xRW, yRW, 0), null);
	
		if (repaint) {
			kernel.notifyRepaint();
		}
	}

	public void setMovedGeoPoint(GeoElement geo) {
		movedGeoPoint = (GeoPointND) geo;
	
		AlgoElement algo = ((GeoElement) movedGeoPoint).getParentAlgorithm();
		if ((algo != null) && (algo instanceof AlgoDynamicCoordinates)) {
			movedGeoPoint = ((AlgoDynamicCoordinates) algo).getParentPoint();
		}
	
		view.setShowMouseCoords(!app.isApplet() && !movedGeoPoint.hasPath());
		view.setDragCursor();
	}

	/**
	 * for some modes, polygons are not to be removed
	 * 
	 * @param hits
	 */
	protected void switchModeForRemovePolygons(Hits hits) {
		switch (mode) {
		case EuclidianConstants.MODE_POINT:
		case EuclidianConstants.MODE_COMPLEX_NUMBER:
		case EuclidianConstants.MODE_POINT_ON_OBJECT:
			// removed: polygons can still be selected if they are the only
			// object clicked on
			// case EuclidianView.MODE_INTERSECT:
			// case EuclidianView.MODE_INTERSECTION_CURVE:
			break;
		default:
			hits.removePolygons();
		}
	}

	protected boolean switchModeForMouseReleased(int evMode, Hits hitsReleased,
			boolean kernelChanged) {
				Hits hits = hitsReleased;
				boolean changedKernel = kernelChanged;
				switch (evMode) {
				case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
				case EuclidianConstants.MODE_DILATE_FROM_POINT:
				case EuclidianConstants.MODE_MIRROR_AT_POINT:
				case EuclidianConstants.MODE_MIRROR_AT_LINE:
				case EuclidianConstants.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds
																// 2008-03-23
				case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
					view.setHits(mouseLoc);
					hits = view.getHits();
					hits.removePolygons();
					// hits = view.getHits(mouseLoc);
					if (hits.isEmpty()) {
						POINT_CREATED = createNewPoint(hits, false, false, true);
					}
					changedKernel = POINT_CREATED;
					break;
			
				case EuclidianConstants.MODE_TRANSLATEVIEW:
					changedKernel = true;
					break;
			
				case EuclidianConstants.MODE_BUTTON_ACTION:
				case EuclidianConstants.MODE_TEXTFIELD_ACTION:
					// make sure script not triggered
					break;
			
				default:
			
					// change checkbox (boolean) state on mouse up only if there's been
					// no drag
					view.setHits(mouseLoc);
					hits = view.getHits().getTopHits();
					// hits = view.getTopHits(mouseLoc);
					if (!hits.isEmpty()) {
						GeoElement hit = hits.get(0);
						if (hit!=null){
							if (hit.isGeoButton() && !(hit.isGeoTextField())) {
								checkBoxOrButtonJustHitted = true;
								app.removeSelectedGeo(hit, true, false); // make sure doesn't get selected
								app.updateSelection(false);
							}
							else if (hit.isGeoBoolean()) {
								GeoBoolean bool = (GeoBoolean) (hits.get(0));
								if (!isCheckboxFixed(bool)) { // otherwise changed on mouse
									// down
									hitCheckBox(bool);
									app.removeSelectedGeo(bool, true, false); // make sure doesn't get selected
									app.updateSelection(false);
									bool.updateCascade();
								}
							} else {
								GeoElement geo1 = chooseGeo(hits, true);
								// ggb3D : geo1 may be null if it's axes or xOy plane
								if (geo1 != null) {

									// make sure that Input Boxes lose focus (and so update) before running scripts
									view.requestFocusInWindow();

									app.runScripts(geo1, (String)null);
								}
							}
						}
					}
				}
			
				return changedKernel;
			}
	
	
	private boolean checkBoxOrButtonJustHitted = false;

	private boolean penDragged;
	
	protected void hitCheckBox(GeoBoolean bool){
		bool.setValue(!bool.getBoolean());
		checkBoxOrButtonJustHitted = true;
	}
	


	protected Hits addPointCreatedForMouseReleased(Hits releasedHits) {
		Hits hits = releasedHits;
		if (hits.isEmpty()) {
			hits = new Hits();
			hits.add(getMovedGeoPoint());
		}
	
		return hits;
	}

	protected boolean moveMode(int evMode) {
		if ((evMode == EuclidianConstants.MODE_MOVE)) {
			return true;
		}
		return false;
	}

	protected boolean hitResetIcon() {
		return app.showResetIcon()
				&& ((mouseLoc.y < 18) && (mouseLoc.x > (view.getViewWidth() - 18)));
	}

	protected void processMouseMoved(AbstractEvent event) {
	
		boolean repaintNeeded;
	
		// reset icon
		if (hitResetIcon()) {
			view.setToolTipText(app.getPlainTooltip("resetConstruction"));
			view.setHitCursor();
			return;
		}
	
		// animation button
		boolean hitAnimationButton = view.hitAnimationButton(event);
		repaintNeeded = view.setAnimationButtonsHighlighted(hitAnimationButton);
		if (hitAnimationButton) {
			if (kernel.isAnimationPaused()) {
				view.setToolTipText(app.getPlainTooltip("Play"));
			} else {
				view.setToolTipText(app.getPlainTooltip("Pause"));
			}
			view.setHitCursor();
			view.repaintView();
			return;
		}
	
		// standard handling
		Hits hits = new Hits();
		boolean noHighlighting = false;
		setAltDown(event.isAltDown());
		
		// label hit
		GeoElement geo = view.getLabelHit(mouseLoc);
		if (geo != null) {
			mouseIsOverLabel = true;
		} else {
			mouseIsOverLabel = false;
		}
		if (moveMode(mode)) { // label hit in move mode: block all other hits
			if (geo != null) {
				// Application.debug("hop");
				noHighlighting = true;
				tempArrayList.clear();
				tempArrayList.add(geo);
				hits = tempArrayList;
			}
		}
	
		if (hits.isEmpty()) {
			view.setHits(mouseLoc);
			hits = view.getHits();
			switchModeForRemovePolygons(hits);
		}
		
		if (hits.isEmpty()) {
			view.setToolTipText(null);
			view.setDefaultCursor();
		} else {
			if (event.isShiftDown() && (hits.size() == 1)
					&& (hits.get(0) instanceof GeoAxis)) {
				if (((GeoAxis) hits.get(0)).getType() == GeoAxisND.X_AXIS) {
					view.setResizeXAxisCursor();
				} else {
					view.setResizeYAxisCursor();
				}
			} else {
				view.setHitCursor();
			}
		}
		
		// for testing: save the full hits for later use
		Hits tempFullHits = hits.clone();
		
		// Application.debug("tempFullHits="+tempFullHits);
	
		// set tool tip text
		// the tooltips are only shown if algebra view is visible
		// if (app.isUsingLayout() && app.getGuiManager().showAlgebraView()) {
		// hits = view.getTopHits(hits);
	
		hits = hits.getTopHits();
		if(sliderValue!=null)
			repaintNeeded = true;
		sliderValue = null;
		if (hits.size() == 1) {
			GeoElement hit = hits.get(0);
			int labelMode = hit.getLabelMode();
			if (hit.isGeoNumeric()
					&& ((GeoNumeric) hit).isSlider()
					&& ((labelMode == GeoElement.LABEL_NAME_VALUE) || (labelMode == GeoElement.LABEL_VALUE))) {
	
				// only do this if we are not pasting something from the
				// clipboard right now
				// because moving on the label of a slider might move the pasted
				// objects away otherwise
				if ((pastePreviewSelected == null) ? (true)
						: (pastePreviewSelected.isEmpty())) {
	
					startPoint.setLocation(((GeoNumeric) hit).getSliderX(),
							((GeoNumeric) hit).getSliderY());
					
					boolean valueShowing = hit.isLabelVisible()
							&& (hit.getLabelMode() == GeoElement.LABEL_NAME_VALUE || hit.getLabelMode() == GeoElement.LABEL_VALUE);
	
					// preview just for fixed sliders (with value showing)
					if (((GeoNumeric) hit).isSliderFixed() && valueShowing) {
						sliderValue = hit.isGeoAngle()? kernel
								.formatAngle(getSliderValue((GeoNumeric) hit),
										StringTemplate.defaultTemplate).toString():kernel
								.format(getSliderValue((GeoNumeric) hit),
										StringTemplate.defaultTemplate);
					}
				}
			}
		}
		
		
		
		if (!hits.isEmpty()) {
			boolean alwaysOn = false;
			if (view.getAllowToolTips() == EuclidianStyleConstants.TOOLTIPS_ON) {
				alwaysOn = true;
			}
			
			String text = GeoElement.getToolTipDescriptionHTML(hits, true,
					true, alwaysOn);
			
			if ("<html></html>".equals(text)) {
				text = null;
			}
			
			view.setToolTipText(text);
		} else {
			view.setToolTipText(null);
			// }
		}
	
		// update previewable
		if (view.getPreviewDrawable() != null) {
			view.updatePreviewable();
			repaintNeeded = true;
		}
		
		if ((pastePreviewSelected != null) && !pastePreviewSelected.isEmpty()) {
			transformCoords();
			updatePastePreviewPosition();
			repaintNeeded = true;
		}
		
		// show Mouse coordinates, manage alt -> multiple of 15 degrees
		else if (view.getShowMouseCoords() && view.getAllowShowMouseCoords()) {
			transformCoords();
			repaintNeeded = true;
		}
	
		// Application.debug(tempFullHits.getTopHits(2,10));
		// manage highlighting & "snap to object"
		// Application.debug("noHighlighting = "+noHighlighting);
		// Application.debug("hits = "+hits.toString());
		// repaintNeeded = noHighlighting ? refreshHighlighting(null) :
		// refreshHighlighting(hits)
		// || repaintNeeded;
	
		repaintNeeded = noHighlighting ? refreshHighlighting(null, event)
				: refreshHighlighting(tempFullHits, event) || repaintNeeded;
		if (repaintNeeded) {
			kernel.notifyRepaint();
		}
	}

	protected void wrapMouseMoved(AbstractEvent event) {
		
		if (textfieldHasFocus) {
			return;
		}
		
		setMouseLocation(event);
		
		processMouseMoved(event);
		//event.release(e.getID()); //does it necessary?
		
	}
	
	protected abstract void resetToolTipManager();

	protected void wrapMouseExited(AbstractEvent event) {
		if (textfieldHasFocus) {
			return;
		}
			
		refreshHighlighting(null, event);
		resetToolTipManager();
		view.setAnimationButtonsHighlighted(false);
		view.setShowMouseCoords(false);
		mouseLoc = null;
		view.repaintView();
		view.mouseExited();
		
	}

	protected void handleSelectClick(ArrayList<GeoElement> geos, boolean ctrlDown) {
		if (geos == null) {
			app.clearSelectedGeos();
		} else {
			if (ctrlDown) {
				// boolean selected = geo.is
				app.toggleSelectedGeo(chooseGeo(geos, true));
				// app.geoElementSelected(geo, true); // copy definiton to input
				// bar
			} else {
				if (!moveModeSelectionHandled) {
					GeoElement geo = chooseGeo(geos, true);
					if (geo != null) {
						app.clearSelectedGeos(false);
						app.addSelectedGeo(geo);
					}
				}
			}
		}
	}

	protected void mouseClickedMode(AbstractEvent event, int mode1) {
	
		switch (mode1) {
		case EuclidianConstants.MODE_RECORD_TO_SPREADSHEET:
			clearSelections();
			break;
		//case EuclidianConstants.MODE_VISUAL_STYLE:
		case EuclidianConstants.MODE_MOVE:
		case EuclidianConstants.MODE_SELECTION_LISTENER:
			switch (event.getClickCount()) {
			case 1:
				// handle selection click
				view.setHits(mouseLoc);
				handleSelectClick(view.getHits().getTopHits(),// view.getTopHits(mouseLoc),
						app.isControlDown(event));
				break;
			/*
			 * // open properties dialog on double click case 2: if
			 * (app.isApplet()) return;
			 * 
			 * app.clearSelectedGeos(); hits = view.getTopHits(mouseLoc); if
			 * (hits != null && mode == EuclidianConstants.MODE_MOVE) {
			 * GeoElement geo0 = (GeoElement)hits.get(0); if (!geo0.isFixed() &&
			 * !(geo0.isGeoImage() && geo0.isIndependent()))
			 * app.getGuiManager().showRedefineDialog((GeoElement)hits.get(0));
			 * } break;
			 */
			}
			break;
	
		case EuclidianConstants.MODE_ZOOM_IN:
			view.zoom(mouseLoc.x, mouseLoc.y, EuclidianView.MODE_ZOOM_FACTOR,
					15, false);
			toggleModeChangedKernel = true;
			break;
	
		case EuclidianConstants.MODE_ZOOM_OUT:
			view.zoom(mouseLoc.x, mouseLoc.y,
					1d / EuclidianView.MODE_ZOOM_FACTOR, 15, false);
			toggleModeChangedKernel = true;
			break;
		}
	}

	protected void wrapMouseclicked(AbstractEvent event) {
		if(textBoxFocused) return;
		
		if (penMode(mode)) {
			return;
		}
	
		Hits hits;
		// GeoElement geo;
	
		setAltDown(event.isAltDown());
	
		if (mode != EuclidianConstants.MODE_SELECTION_LISTENER){
			if ((view.getHits() == null)||(view.getHits().size()==0)||
					!(view.getHits().getTopHits().get(0) instanceof GeoTextField)){
				view.requestFocusInWindow();
			}
		}
	
		if (app.isRightClick(event)) {
			return;
		}
		setMouseLocation(event);
		
		// double-click on object selects MODE_MOVE and opens redefine dialog
		if (event.getClickCount() == 2 || event.isAltDown()) {
			if (app.isApplet() || app.isControlDown(event)) {
				return;
			}
	
			app.clearSelectedGeos(true,false);
			app.updateSelection(false);
			// hits = view.getTopHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits().getTopHits();
			switchModeForRemovePolygons(hits);
			if (!hits.isEmpty()) {
				view.setMode(EuclidianConstants.MODE_MOVE);
				GeoElement geo0 = hits.get(0);

				//if (app.isUsingFullGui() && app.getGuiManager() != null) {
					if (geo0.isGeoNumeric() && ((GeoNumeric) geo0).isSlider()) {
						// double-click slider -> Object Properties
						app.getDialogManager()
								.showPropertiesDialog(hits);
					} else if (!geo0.isFixed()
							&& !(geo0.isGeoBoolean() && geo0.isIndependent())
							&& !(geo0.isGeoImage() && geo0.isIndependent())
							&& !geo0.isGeoButton()) {
						app.getDialogManager()
								.showRedefineDialog(hits.get(0), true);
					}
				//}
			}
		}

		mouseClickedMode(event, mode);
	
		// Alt click: copy definition to input field
		if (event.isAltDown() && app.showAlgebraInput()) {
			view.setHits(mouseLoc);
			hits = view.getHits().getTopHits();
			if ((hits != null) && (hits.size() > 0)) {
				hits.removePolygons();
				GeoElement geo = hits.get(0);
	
				// F3 key: copy definition to input bar
				if (mode != EuclidianConstants.MODE_ATTACH_DETACH) {
					app.getGlobalKeyDispatcher()
							.handleFunctionKeyForAlgebraInput(3, geo);
				}
	
				moveMode = MOVE_NONE;
				return;
			}
		}
	}

	public void resetMovedGeoPoint() {
		movedGeoPoint = null;
	}

	public void setStartPointLocation() {
		startPoint.setLocation(xRW, yRW);
	}
	
	public void setStartPointLocationWithOrigin(double x, double y) {
		startPoint.setLocation(xRW-x, yRW-y);
	}

	public void handleMovedElement(GeoElement geo, boolean multiple) {
		resetMovedGeoPoint();
		movedGeoElement = geo;
		
		// default if nothing matches
		moveMode = MOVE_NONE;
		
		// multiple geos selected
		if ((movedGeoElement != null) && multiple) {
			moveMode = MOVE_MULTIPLE_OBJECTS;
			setStartPointLocation();
			startLoc = mouseLoc;
			view.setDragCursor();
			if (translationVec == null) {
				translationVec = new Coords(2);
			}
		}
		
		
	
		// DEPENDENT object: changeable parents?
		// move free parent points (e.g. for segments)
		else if (!movedGeoElement.isMoveable(view)) {
	
			translateableGeos = null;
			GeoVector vec = null;
			boolean sameVector = true;
	
			// allow dragging of Translate[Object, vector] if 'vector' is
			// independent
			if (movedGeoElement instanceof GeoPoly) {
				GeoPoly poly = (GeoPoly) movedGeoElement;
				GeoPointND[] pts = poly.getPoints();
	
				// get vector for first point
				AlgoElement algo = ((GeoElement) pts[0]).getParentAlgorithm();
				if (algo instanceof AlgoTranslate) {
					GeoElement[] input = algo.getInput();
					
					if ( input[1].isIndependent()) {
						vec = (GeoVector) input[1];
	
						// now check other points are translated by the same vector
						for (int i = 1; i < pts.length; i++) {
							algo = ((GeoElement) pts[i]).getParentAlgorithm();
							if (!(algo instanceof AlgoTranslate)) {
								sameVector = false;
								break;
							}
							input = algo.getInput();
	
							GeoVector vec2 = (GeoVector) input[1];
							if (vec != vec2) {
								sameVector = false;
								break;
							}
	
						}
					}
	
				}
			} else if (movedGeoElement.isGeoSegment()
					|| movedGeoElement.isGeoRay()
					|| (movedGeoElement.getParentAlgorithm() instanceof AlgoVector)) {
				GeoPointND start = null;
				GeoPointND end = null;
				if (movedGeoElement.getParentAlgorithm() instanceof AlgoVector) {
					// Vector[A,B]
					AlgoVector algoVec = (AlgoVector) movedGeoElement
							.getParentAlgorithm();
					start = algoVec.getInputPoints().get(0);
					end = algoVec.getInputPoints().get(1);
	
					if (start.isIndependent() && !end.isIndependent()) {
						end = null;
						Coords coords = start.getInhomCoords();
						transformCoordsOffset[0] = xRW - coords.getX();
						transformCoordsOffset[1] = yRW - coords.getY();
						moveMode = MOVE_POINT_WITH_OFFSET;
						movedGeoPoint = start;
						return;
	
					}
	
				} else {
					// Segment/ray
					GeoLineND line = (GeoLineND) movedGeoElement;
					start = line.getStartPoint();
					end = line.getEndPoint();
				}
	
				if ((start != null) && (end != null)) {
					// get vector for first point
					AlgoElement algo = start.getParentAlgorithm();
					AlgoElement algo2 = end.getParentAlgorithm();
					if ((algo instanceof AlgoTranslate)
							&& (algo2 instanceof AlgoTranslate)) {
						GeoElement[] input = algo.getInput();
						vec = (GeoVector) input[1];
						GeoElement[] input2 = algo2.getInput();
						GeoVector vec2 = (GeoVector) input2[1];
	
						// now check if points are translated by the same vector
						if (vec != vec2) {
							sameVector = false;
						}
	
					}
				}
			} else if (movedGeoElement.isTranslateable()) {
				AlgoElement algo = movedGeoElement.getParentAlgorithm();
				if (algo instanceof AlgoTranslate) {
					GeoElement[] input = algo.getInput();
					if (input[1].isIndependent()) {
						vec = (GeoVector) input[1];
					}
				}
			} else if (movedGeoElement.getParentAlgorithm() instanceof AlgoVectorPoint) {
				// allow Vector[(1,2)] to be dragged
				vec = (GeoVector) movedGeoElement;
			}
	
			if (vec != null) {
				if (vec.getParentAlgorithm() instanceof AlgoVectorPoint) {
					// unwrap Vector[(1,2)]
					AlgoVectorPoint algo = (AlgoVectorPoint) vec
							.getParentAlgorithm();
					moveMode = MOVE_POINT_WITH_OFFSET;
					transformCoordsOffset[0] = xRW - vec.x;
					transformCoordsOffset[1] = yRW - vec.y;
					movedGeoPoint = algo.getP();
					return;
				}
	
				if (sameVector && ((vec.getLabelSimple() == null) || vec.isIndependent())) {
					transformCoordsOffset[0] = xRW - vec.x;
					transformCoordsOffset[1] = yRW - vec.y;
					movedGeoVector = vec;
					moveMode = MOVE_VECTOR_NO_GRID;
					return;
				}
			}
	
			// point with changeable coord parent numbers
			if (movedGeoElement.hasChangeableCoordParentNumbers()) {
				movedGeoElement.recordChangeableCoordParentNumbers();
				translateableGeos = new ArrayList<GeoElement>();
				translateableGeos.add(movedGeoElement);
			}
	
			// STANDARD case: get free input points of dependent movedGeoElement
			else if (movedGeoElement.hasMoveableInputPoints(view)) {
				// allow only moving of the following object types
				if (movedGeoElement.isGeoLine()
						|| movedGeoElement.isGeoPolygon()
						|| (movedGeoElement instanceof GeoPolyLine)
						|| movedGeoElement.isGeoConic()
						|| movedGeoElement.isGeoImage()
						|| movedGeoElement.isGeoList()
						|| movedGeoElement.isGeoVector()) {
					if(translateableGeos==null)
						translateableGeos = new ArrayList<GeoElement>();
					else
						translateableGeos.clear();
					translateableGeos.addAll(movedGeoElement
							.getFreeInputPoints(view));
					if(movedGeoElement.isGeoList())
						translateableGeos.add(movedGeoElement);
				}
			}
	
			// init move dependent mode if we have something to move ;-)
			if (translateableGeos != null && translateableGeos.size() > 0) {
				moveMode = MOVE_DEPENDENT;
	
				if (translateableGeos.get(0) instanceof GeoPoint) {
					GeoPoint point = ((GeoPoint) translateableGeos.get(0));
					if (point.getParentAlgorithm() != null) {
						// make sure snap-to-grid works for dragging (a + x(A),
						// b + x(B))
						transformCoordsOffset[0] = 0;
						transformCoordsOffset[1] = 0;
	
					} else {
						// snap to grid when dragging polygons, segments, images
						// etc
						// use first point
						point.getInhomCoords(transformCoordsOffset);
						transformCoordsOffset[0] -= xRW;
						transformCoordsOffset[1] -= yRW;
					}
				}
	
				setStartPointLocation();
	
				view.setDragCursor();
				if (translationVec == null) {
					translationVec = new Coords(2);
				}
			} else {
				moveMode = MOVE_NONE;
			}
		}
	
		// free point
		else if (movedGeoElement.isGeoPoint()) {
			moveMode = MOVE_POINT;
			setMovedGeoPoint(movedGeoElement);
			// make sure snap-to-grid works after e.g. pressing a button
			transformCoordsOffset[0] = 0;
			transformCoordsOffset[1] = 0;
		}
	
		// free line
		else if (movedGeoElement.isGeoLine()) {
			moveMode = MOVE_LINE;
			movedGeoLine = (GeoLine) movedGeoElement;
			view.setShowMouseCoords(true);
			view.setDragCursor();
		}
	
		// free vector
		else if (movedGeoElement.isGeoVector()) {
			movedGeoVector = (GeoVector) movedGeoElement;
	
			// change vector itself or move only startpoint?
			// if vector is dependent or
			// mouseLoc is closer to the startpoint than to the end
			// point
			// then move the startpoint of the vector
			if (movedGeoVector.hasAbsoluteLocation()) {
				GeoPoint sP = movedGeoVector.getStartPoint();
				double sx = 0;
				double sy = 0;
				if (sP != null) {
					sx = sP.inhomX;
					sy = sP.inhomY;
				}
				// if |mouse - startpoint| < 1/2 * |vec| then move
				// startpoint
				if ((2d * MyMath.length(xRW - sx, yRW - sy)) < MyMath.length(
						movedGeoVector.x, movedGeoVector.y)) { // take
					// startPoint
					moveMode = MOVE_VECTOR_STARTPOINT;
					if (sP == null) {
						sP = new GeoPoint(kernel.getConstruction());
						sP.setCoords(xRW, xRW, 1.0);
						try {
							movedGeoVector.setStartPoint(sP);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				} else {
					moveMode = MOVE_VECTOR;
				}
			} else {
				moveMode = MOVE_VECTOR;
			}
	
			view.setShowMouseCoords(true);
			view.setDragCursor();
		}
	
		// free text
		else if (movedGeoElement.isGeoText()) {
			moveMode = MOVE_TEXT;
			movedGeoText = (GeoText) movedGeoElement;
			view.setShowMouseCoords(false);
			view.setDragCursor();
	
			if (movedGeoText.isAbsoluteScreenLocActive()) {
				oldLoc.setLocation(movedGeoText.getAbsoluteScreenLocX(),
						movedGeoText.getAbsoluteScreenLocY());
				startLoc = mouseLoc;
	
				// part of snap to grid code - buggy, so commented out
				// startPoint.setLocation(xRW -
				// view.toRealWorldCoordX(oldLoc.x), yRW -
				// view.toRealWorldCoordY(oldLoc.y));
				// movedGeoText.setNeedsUpdatedBoundingBox(true);
				// movedGeoText.update();
				// transformCoordsOffset[0]=movedGeoText.getBoundingBox().getX()-xRW;
				// transformCoordsOffset[1]=movedGeoText.getBoundingBox().getY()-yRW;
			} else if (movedGeoText.hasAbsoluteLocation()) {

				// absolute location: change location
				GeoPoint loc = (GeoPoint) movedGeoText.getStartPoint();
				if (loc == null) {
					loc = new GeoPoint(kernel.getConstruction());
					loc.setCoords(0, 0, 1.0);
					try {
						movedGeoText.setStartPoint(loc);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					setStartPointLocation();
				} else {
					setStartPointLocationWithOrigin(loc.inhomX, loc.inhomY);
	
					GeoPoint loc2 = new GeoPoint(loc);
					movedGeoText.setNeedsUpdatedBoundingBox(true);
					movedGeoText.update();
					loc2.setCoords(movedGeoText.getBoundingBox().getX(),
							movedGeoText.getBoundingBox().getY(), 1.0);
	
					transformCoordsOffset[0] = loc2.inhomX - xRW;
					transformCoordsOffset[1] = loc2.inhomY - yRW;
				}
			} else {
				// for relative locations label has to be moved
				oldLoc.setLocation(movedGeoText.labelOffsetX,
						movedGeoText.labelOffsetY);
				startLoc = mouseLoc;
			}
		}
	
		// free conic
		else if (movedGeoElement.isGeoConic()) {
			moveMode = MOVE_CONIC;
			movedGeoConic = (GeoConic) movedGeoElement;
			view.setShowMouseCoords(false);
			view.setDragCursor();
			
			// make sure vertex snaps to grid for parabolas
			if (movedGeoConic.getType() == GeoConicNDConstants.CONIC_PARABOLA) {
				double vX = movedGeoConic.b.getX();
				double vY = movedGeoConic.b.getY();

				transformCoordsOffset[0] = vX - xRW;
				transformCoordsOffset[1] = vY - yRW;

			}
	
			setStartPointLocation();
			if (tempConic == null) {
				tempConic = new GeoConic(kernel.getConstruction());
			}
			tempConic.set(movedGeoConic);
		} else if (movedGeoElement.isGeoImplicitPoly()) {
			moveMode = MOVE_IMPLICITPOLY;
			movedGeoImplicitPoly = (GeoImplicitPoly) movedGeoElement;
			view.setShowMouseCoords(false);
			view.setDragCursor();
	
			setStartPointLocation();
			if (tempImplicitPoly == null) {
				tempImplicitPoly = new GeoImplicitPoly(movedGeoImplicitPoly);
			} else {
				tempImplicitPoly.set(movedGeoImplicitPoly);
			}
	
			if (tempDependentPointX == null) {
				tempDependentPointX = new ArrayList<Double>();
			} else {
				tempDependentPointX.clear();
			}
	
			if (tempDependentPointY == null) {
				tempDependentPointY = new ArrayList<Double>();
			} else {
				tempDependentPointY.clear();
			}
	
			if (moveDependentPoints == null) {
				moveDependentPoints = new ArrayList<GeoPoint>();
			} else {
				moveDependentPoints.clear();
			}
	
			for (GeoElement f : movedGeoImplicitPoly.getAllChildren()) {
				// if (f instanceof GeoPoint &&
				// f.getParentAlgorithm().getInput().length==1 &&
				// f.getParentAlgorithm().getInput()[0] instanceof Path){
				if ((f instanceof GeoPoint)
						&& movedGeoImplicitPoly.isParentOf(f)) {
					GeoPoint g = (GeoPoint) f;
					if (!Kernel.isZero(g.getZ())) {
						moveDependentPoints.add(g);
						tempDependentPointX.add(g.getX() / g.getZ());
						tempDependentPointY.add(g.getY() / g.getZ());
					}
				}
			}
			// for (GeoElement elem:movedGeoImplicitPoly.getAllChildren()){
			// if (elem instanceof GeoPoint){
			// if (movedGeoImplicitPoly.isParentOf(elem)){
			// tempDependentPointOnPath.add(((GeoPoint)elem).getPathParameter().getT());
			// }
			// }
			// }
	
		}
		// else removed otherwise AlgoFunctionFreehand can't be dragged
		if (movedGeoElement.isGeoFunction()) {

			if (movedGeoElement.getParentAlgorithm() instanceof AlgoFunctionFreehand) {
				
				moveMode = MOVE_FREEHAND;
				movedGeoFunction = (GeoFunction) movedGeoElement;
				
			} else if(movedGeoElement.isIndependent()) {
				moveMode = MOVE_FUNCTION;

				movedGeoFunction = (GeoFunction) movedGeoElement;
				vertexX = Double.NaN;
				vertexY = Double.NaN;

				if (movedGeoFunction.getFunction().getSymbolicPolynomialFactors(false,true) != null) {
					LinkedList<PolyFunction> factors= movedGeoFunction.getFunction().getPolynomialFactors(false);
					if(factors.size() == 1 && factors.get(0).getDegree() == 2){
						double c = movedGeoFunction.evaluate(0);
						double s = movedGeoFunction.evaluate(1);		
						double a = 0.5*(s+movedGeoFunction.evaluate(-1))-c;
						double b = s-a-c;

						// cordinates of vertex (just calculated once)
						// used for alt-drag as well
						vertexX = -b/a/2.0;
						vertexY = -(b * b - 4.0 * a * c) / (4.0 * a );

						// make sure vertex snaps to grid for parabolas
						transformCoordsOffset[0] = vertexX - xRW;
						transformCoordsOffset[1] = vertexY - yRW;


					}
				}
			}
			
			view.setShowMouseCoords(false);
			view.setDragCursor();
	
			setStartPointLocation();
			if (tempFunction == null) {
				tempFunction = new GeoFunction(kernel.getConstruction());
			}
			tempFunction.set(movedGeoFunction);
		}
	
		// free number
		else if (movedGeoElement.isGeoNumeric() && movedGeoElement.getParentAlgorithm() == null) {
			movedGeoNumeric = (GeoNumeric) movedGeoElement;
			moveMode = MOVE_NUMERIC;
	
			DrawableND d = view.getDrawableFor(movedGeoNumeric);
			if (d instanceof DrawSlider) {
				// should we move the slider
				// or the point on the slider, i.e. change the number
				DrawSlider ds = (DrawSlider) d;
				// TEMPORARY_MODE true -> dragging slider using Slider Tool
				// or right-hand mouse button
	
				// otherwise using Move Tool -> move dot
				if (((TEMPORARY_MODE && app.isRightClickEnabled()) || !movedGeoNumeric
						.isSliderFixed())
						&& !ds.hitPoint(mouseLoc.x, mouseLoc.y)
						&& ds.hitSlider(mouseLoc.x, mouseLoc.y)) {
					moveMode = MOVE_SLIDER;
					if (movedGeoNumeric.isAbsoluteScreenLocActive()) {
						oldLoc.setLocation(
								movedGeoNumeric.getAbsoluteScreenLocX(),
								movedGeoNumeric.getAbsoluteScreenLocY());
						startLoc = mouseLoc;
	
						// part of snap to grid code
						startPoint.setLocation(
								xRW - view.toRealWorldCoordX(oldLoc.x), yRW
										- view.toRealWorldCoordY(oldLoc.y));
						transformCoordsOffset[0] = view
								.toRealWorldCoordX(oldLoc.x) - xRW;
						transformCoordsOffset[1] = view
								.toRealWorldCoordY(oldLoc.y) - yRW;
					} else {
						startPoint.setLocation(
								xRW - movedGeoNumeric.getRealWorldLocX(), yRW
										- movedGeoNumeric.getRealWorldLocY());
						transformCoordsOffset[0] = movedGeoNumeric
								.getRealWorldLocX() - xRW;
						transformCoordsOffset[1] = movedGeoNumeric
								.getRealWorldLocY() - yRW;
					}
				} else {
					startPoint.setLocation(movedGeoNumeric.getSliderX(),
							movedGeoNumeric.getSliderY());
	
					// update straightaway in case it's just a click (no drag)
					moveNumeric(true);
				}
			}
	
			view.setShowMouseCoords(false);
			view.setDragCursor();
		}
	
		// checkbox
		else if (movedGeoElement.isGeoBoolean()) {
			movedGeoBoolean = (GeoBoolean) movedGeoElement;
	
			// if fixed checkbox dragged, behave as if it's been clicked
			// important for electronic whiteboards
			if (isCheckboxFixed(movedGeoBoolean)) {
				movedGeoBoolean.setValue(!movedGeoBoolean.getBoolean());
				app.removeSelectedGeo(movedGeoBoolean); // make sure doesn't get
														// selected
				movedGeoBoolean.updateCascade();
	
			}
	
			// move checkbox
			moveMode = MOVE_BOOLEAN;
			startLoc = mouseLoc;
			oldLoc.x = movedGeoBoolean.getAbsoluteScreenLocX();
			oldLoc.y = movedGeoBoolean.getAbsoluteScreenLocY();
	
			// part of snap to grid code (the constant 5 comes from DrawBoolean)
			startPoint.setLocation(xRW - view.toRealWorldCoordX(oldLoc.x), yRW
					- view.toRealWorldCoordY(oldLoc.y));
			transformCoordsOffset[0] = view.toRealWorldCoordX(oldLoc.x + 5)
					- xRW;
			transformCoordsOffset[1] = view.toRealWorldCoordY(oldLoc.y + 5)
					- yRW;
	
			view.setShowMouseCoords(false);
			view.setDragCursor();
	
		}
	
		// button
		else if (movedGeoElement instanceof Furniture && ((Furniture)movedGeoElement).isFurniture()) {
			movedGeoButton = (Furniture) movedGeoElement;
			// move checkbox
			moveMode = MOVE_BUTTON;
			startLoc = mouseLoc;
			oldLoc.x = movedGeoButton.getAbsoluteScreenLocX();
			oldLoc.y = movedGeoButton.getAbsoluteScreenLocY();
	
			// part of snap to grid code
			startPoint.setLocation(xRW - view.toRealWorldCoordX(oldLoc.x), yRW
					- view.toRealWorldCoordY(oldLoc.y));
			transformCoordsOffset[0] = view.toRealWorldCoordX(oldLoc.x) - xRW;
			transformCoordsOffset[1] = view.toRealWorldCoordY(oldLoc.y) - yRW;
	
			view.setShowMouseCoords(false);
			view.setDragCursor();
		}
	
		// image
		else if (movedGeoElement.isGeoImage()) {
			moveMode = MOVE_IMAGE;
			movedGeoImage = (GeoImage) movedGeoElement;
			view.setShowMouseCoords(false);
			view.setDragCursor();
	
			if (movedGeoImage.isAbsoluteScreenLocActive()) {
				oldLoc.setLocation(movedGeoImage.getAbsoluteScreenLocX(),
						movedGeoImage.getAbsoluteScreenLocY());
				startLoc = mouseLoc;
	
				// part of snap to grid code
				startPoint.setLocation(xRW - view.toRealWorldCoordX(oldLoc.x),
						yRW - view.toRealWorldCoordY(oldLoc.y));
				transformCoordsOffset[0] = view.toRealWorldCoordX(oldLoc.x)
						- xRW;
				transformCoordsOffset[1] = view.toRealWorldCoordY(oldLoc.y)
						- yRW;
			} else if (movedGeoImage.hasAbsoluteLocation()) {
				setStartPointLocation();
				oldImage = new GeoImage(movedGeoImage);
	
				GeoPoint loc = movedGeoImage.getStartPoints()[2];
				if (loc != null) { // top left defined
					transformCoordsOffset[0] = loc.inhomX - xRW;
					transformCoordsOffset[1] = loc.inhomY - yRW;
				} else {
					loc = movedGeoImage.getStartPoint();
					if (loc != null) { // bottom left defined (default)
						transformCoordsOffset[0] = loc.inhomX - xRW;
						transformCoordsOffset[1] = loc.inhomY - yRW;
					} else {
						loc = movedGeoImage.getStartPoints()[1];
						if (loc != null) { // bottom right defined
							transformCoordsOffset[0] = loc.inhomX - xRW;
							transformCoordsOffset[1] = loc.inhomY - yRW;
						}
					}
				}
			}
		}
	
	}

	/*
	 * Dragging a fixed checkbox should change its state (important for EWB etc)
	 * 
	 * Also for iPads etc
	 * HTML5: don't allow dragging unless we have a GUI
	 */
	private boolean isCheckboxFixed(GeoBoolean geoBool) {
		return geoBool.isCheckboxFixed() || (app.isHTML5Applet() && !App.isFullAppGui());
	}

	protected void updateSelectionRectangle(boolean keepScreenRatio) {
		if (view.getSelectionRectangle() == null) {
			view.setSelectionRectangle(geogebra.common.factories.AwtFactory.prototype.newRectangle(0,0));
		}
	
		int dx = mouseLoc.x - selectionStartPoint.x;
		int dy = mouseLoc.y - selectionStartPoint.y;
		int dxabs = Math.abs(dx);
		int dyabs = Math.abs(dy);
	
		int width = dx;
		int height = dy;
	
		// the zoom rectangle should have the same aspect ratio as the view
		if (keepScreenRatio) {
			double ratio = (double) view.getViewWidth()
					/ (double) view.getViewHeight();
			if (dxabs >= (dyabs * ratio)) {
				height = (int) (Math.round(dxabs / ratio));
				if (dy < 0) {
					height = -height;
				}
			} else {
				width = (int) Math.round(dyabs * ratio);
				if (dx < 0) {
					width = -width;
				}
			}
		}
	
		GRectangle rect = view.getSelectionRectangle();
		if (height >= 0) {
			if (width >= 0) {
				rect.setLocation(selectionStartPoint.x,selectionStartPoint.y);
				rect.setSize(width, height);
			} else { // width < 0
				rect.setLocation(selectionStartPoint.x + width,
						selectionStartPoint.y);
				rect.setSize(-width, height);
			}
		} else { // height < 0
			if (width >= 0) {
				rect.setLocation(selectionStartPoint.x, selectionStartPoint.y
						+ height);
				rect.setSize(width, -height);
			} else { // width < 0
				rect.setLocation(selectionStartPoint.x + width,
						selectionStartPoint.y + height);
				rect.setSize(-width, -height);
			}
		}
	}

	protected void handleMouseDragged(boolean repaint, AbstractEvent event) {
		// moveMode was set in mousePressed()
		switch (moveMode) {
		case MOVE_ROTATE:
			rotateObject(repaint);
			break;
	
		case MOVE_POINT:
			movePoint(repaint, event);
			break;
	
		case MOVE_POINT_WITH_OFFSET:
			movePointWithOffset(repaint);
			break;
	
		case MOVE_LINE:
			moveLine(repaint);
			break;
	
		case MOVE_VECTOR:
		case MOVE_VECTOR_NO_GRID:
			moveVector(repaint);
			break;
	
		case MOVE_VECTOR_STARTPOINT:
			moveVectorStartPoint(repaint);
			break;
	
		case MOVE_CONIC:
			moveConic(repaint);
			break;
	
		case MOVE_IMPLICITPOLY:
			moveImplicitPoly(repaint);
			break;
	
		case MOVE_FREEHAND:
			moveFreehand(repaint);
			break;
		
		case MOVE_FUNCTION:
			moveFunction(repaint);
			break;
	
	
		case MOVE_LABEL:
			moveLabel();
			break;
	
		case MOVE_TEXT:
			moveText(repaint);
			break;
	
		case MOVE_IMAGE:
			moveImage(repaint);
			break;
	
		case MOVE_NUMERIC:
			// view.incrementTraceRow(); // for spreadsheet/trace
	
			moveNumeric(repaint);
			break;
	
		case MOVE_SLIDER:
			moveSlider(repaint);
			break;
	
		case MOVE_BOOLEAN:
			moveBoolean(repaint);
			break;
	
		case MOVE_BUTTON:
			moveButton(repaint);
			break;
	
		case MOVE_DEPENDENT:
			if(movedGeoElement.getParentAlgorithm() != null && movedGeoElement.getParentAlgorithm().getClassName()==Algos.AlgoAttachCopyToView){
				moveAttached(repaint);
			}
			else {
				moveDependent(repaint);
			}
			break;
	
		case MOVE_MULTIPLE_OBJECTS:
			moveMultipleObjects(repaint);
			break;
	
		case MOVE_VIEW:
			if (repaint) {
				if (TEMPORARY_MODE) {
					view.setMoveCursor();
				}
				/*
				 * view.setCoordSystem(xZeroOld + mouseLoc.x - startLoc.x,
				 * yZeroOld + mouseLoc.y - startLoc.y, view.getXscale(),
				 * view.getYscale());
				 */
				view.setCoordSystemFromMouseMove(mouseLoc.x - startLoc.x,
						mouseLoc.y - startLoc.y, MOVE_VIEW);
			}
			break;
	
		case MOVE_X_AXIS:
			if (repaint) {
				if (TEMPORARY_MODE) {
					view.setResizeXAxisCursor();
				}
	
				// take care when we get close to the origin
				if (Math.abs(mouseLoc.x - view.getXZero()) < 2) {
					mouseLoc.x = (int) Math
							.round(mouseLoc.x > view.getXZero() ? view
									.getXZero() + 2 : view.getXZero() - 2);
				}
				double xscale = (mouseLoc.x - view.getXZero()) / xTemp;
				view.setCoordSystem(view.getXZero(), view.getYZero(), xscale,
						view.getYscale());
			}
			break;
	
		case MOVE_Y_AXIS:
			if (repaint) {
				if (TEMPORARY_MODE) {
					view.setResizeYAxisCursor();
				}
				// take care when we get close to the origin
				if (Math.abs(mouseLoc.y - view.getYZero()) < 2) {
					mouseLoc.y = (int) Math
							.round(mouseLoc.y > view.getYZero() ? view
									.getYZero() + 2 : view.getYZero() - 2);
				}
				double yscale = ( view.getYZero() - mouseLoc.y) / yTemp;
				view.setCoordSystem(view.getXZero(), view.getYZero(),
						view.getXscale(), yscale);
			}
			break;
	
		default: // do nothing
		}
	}

	protected boolean viewHasHitsForMouseDragged() {
		return !(view.getHits().isEmpty());
	}

	/**
	 * right-drag the mouse makes 3D rotation
	 * 
	 * @return false
	 */
	protected boolean processRotate3DView() {
		return false;
	}

	protected boolean allowSelectionRectangle() {
		switch (mode) {
		// move objects
		case EuclidianConstants.MODE_MOVE:
			return moveMode == MOVE_NONE;
	
			// move rotate objects
		case EuclidianConstants.MODE_MOVE_ROTATE:
			return selPoints() > 0; // need rotation center
	
			// object selection mode
		case EuclidianConstants.MODE_SELECTION_LISTENER:
			GeoElementSelectionListener sel = app.getCurrentSelectionListener();
			if (sel == null) {
				return false;
			}
			if (app.isUsingFullGui() && app.getGuiManager() != null) {
				return !app.getGuiManager().isInputFieldSelectionListener();
			}
			return true;
	
			// transformations
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			return allowSelectionRectangleForTranslateByVector;
	
		case EuclidianConstants.MODE_DILATE_FROM_POINT:
		case EuclidianConstants.MODE_MIRROR_AT_POINT:
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds
														// 2008-03-23
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
		case EuclidianConstants.MODE_FITLINE:
		case EuclidianConstants.MODE_CREATE_LIST:
		//case EuclidianConstants.MODE_VISUAL_STYLE:
		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
			return true;
	
			// checkbox, button
		case EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX:
		case EuclidianConstants.MODE_BUTTON_ACTION:
		case EuclidianConstants.MODE_TEXTFIELD_ACTION:
			return true;
	
		default:
			return false;
		}
	}

	protected void handleMousePressedForMoveMode(AbstractEvent e, boolean drag) {
	
		// fix for meta-click to work on Mac/Linux
		if (app.isControlDown(e)) {
			return;
		}
	
		// move label?
		GeoElement geo = view.getLabelHit(mouseLoc);
		// Application.debug("label("+(System.currentTimeMillis()-t0)+")");
		if (geo != null) {
			moveMode = MOVE_LABEL;
			movedLabelGeoElement = geo;
			oldLoc.setLocation(geo.labelOffsetX, geo.labelOffsetY);
			startLoc = mouseLoc;
			view.setDragCursor();
			return;
		}
	
		// find and set movedGeoElement
		view.setHits(mouseLoc);
		Hits viewHits = view.getHits();
	
		// make sure that eg slider takes precedence over a polygon (in the same
		// layer)
		viewHits.removePolygons();
	
		Hits moveableList;
	
		// if we just click (no drag) on eg an intersection, we want it selected
		// not a popup with just the lines in
	
		// now we want this behaviour always as
		// * there is no popup
		// * user might do eg click then arrow keys
		// * want drag with left button to work (eg tessellation)
	
		// consider intersection of 2 circles.
		// On drag, we want to be able to drag a circle
		// on click, we want to be able to select the intersection point
		if (drag) {
			moveableList = viewHits.getMoveableHits(view);
		} else {
			moveableList = viewHits;
		}
	
		Hits hits = moveableList.getTopHits();
		
		ArrayList<GeoElement> selGeos = app.getSelectedGeos();
	
		// if object was chosen before, take it now!
		if ((selGeos.size() == 1) && !hits.isEmpty()
				&& hits.contains(selGeos.get(0))) {
			// object was chosen before: take it
			geo = selGeos.get(0);
		} else {
			// choose out of hits
			geo = chooseGeo(hits, false);
			
			if (!selGeos.contains(geo)) {
				app.clearSelectedGeos(false); //repaint done next step
				app.addSelectedGeo(geo,true,true);
				// app.geoElementSelected(geo, false); // copy definiton to
				// input bar
			}
		}
	
		if ((geo != null) && !geo.isFixed()) {
			moveModeSelectionHandled = true;
		} else {
			// no geo clicked at
			moveMode = MOVE_NONE;
			resetMovedGeoPoint();
			return;
		}

		handleMovedElement(geo, selGeos.size() > 1);
	
		view.repaintView();
	}

	protected void wrapMouseDragged(AbstractEvent event) {
		sliderValue = null;
		if (textfieldHasFocus && moveMode != MOVE_BUTTON) {
			return;
		}
		if(pressedButton!=null){
			pressedButton.setDraggedOrContext(true);
		}
		if (penMode(mode)) {
			penDragged = true;
			getPen().handleMousePressedForPenMode(event, null);
			return;
		}
	
		clearJustCreatedGeos();
	
		if (!DRAGGING_OCCURED) {
	
			DRAGGING_OCCURED = true;
	
			if ((mode == EuclidianConstants.MODE_TRANSLATE_BY_VECTOR)
					&& (selGeos() == 0)) {
				view.setHits(mouseLoc);
	
				Hits hits = view.getHits().getTopHits();
	
				GeoElement topHit = hits.get(0);
	
				if (topHit.isGeoVector()) {
	
					if ((topHit.getParentAlgorithm() instanceof AlgoVector)) { // Vector[A,B]
						AlgoVector algo = (AlgoVector) topHit
								.getParentAlgorithm();
						GeoPoint p = algo.getInputPoints().get(0);
						GeoPoint q = algo.getInputPoints().get(1);
						checkZooming(); 
						
						GeoVector vec = getAlgoDispatcher().Vector(null, 0, 0);
						vec.setEuclidianVisible(false);
						vec.setAuxiliaryObject(true);
						GeoElement[] pp = getAlgoDispatcher().Translate(null, p, vec);
						GeoElement[] qq = getAlgoDispatcher().Translate(null, q, vec);
						AlgoVector newVecAlgo = new AlgoVector(kernel.getConstruction(), null,
								(GeoPointND) pp[0], (GeoPointND) qq[0]);
						transformCoordsOffset[0] = xRW;
						transformCoordsOffset[1] = yRW;
	
						// make sure vector looks the same when translated
						pp[0].setEuclidianVisible(p.isEuclidianVisible());
						qq[0].update();
						qq[0].setEuclidianVisible(q.isEuclidianVisible());
						qq[0].update();
						newVecAlgo.getGeoElements()[0].setVisualStyleForTransformations(topHit);
	
						app.setMode(EuclidianConstants.MODE_MOVE);
						movedGeoVector = vec;
						moveMode = MOVE_VECTOR_NO_GRID;
						return;
					}
					movedGeoPoint = new GeoPoint(kernel.getConstruction(),
							null, 0, 0, 0);
					AlgoTranslate algoTP = new AlgoTranslate(
							kernel.getConstruction(), null,
							(GeoElement) movedGeoPoint, (GeoVec3D) topHit);
					GeoPoint p = (GeoPoint) algoTP.getGeoElements()[0];

					AlgoVector newVecAlgo = new AlgoVector(kernel.getConstruction(), null,
							movedGeoPoint, p);
					
					// make sure vector looks the same when translated
					((GeoPoint) movedGeoPoint).setEuclidianVisible(false);
					((GeoPoint) movedGeoPoint).update();
					p.setEuclidianVisible(false);
					p.update();
					newVecAlgo.getGeoElements()[0].setVisualStyleForTransformations(topHit);
					
					moveMode = MOVE_POINT;
				}
	
				if (topHit.isTranslateable() || topHit instanceof GeoPoly) {
					GeoVector vec;
					if (topHit instanceof GeoPoly) {
						// for polygons, we need a labelled vector so that all
						// the vertices move together
						vec = getAlgoDispatcher().Vector(null, 0, 0);
						vec.setEuclidianVisible(false);
						vec.setAuxiliaryObject(true);
					} else {
						vec = getAlgoDispatcher().Vector(0, 0);
					}
					getAlgoDispatcher().Translate(null, hits.get(0), vec);
					transformCoordsOffset[0] = xRW;
					transformCoordsOffset[1] = yRW;
	
					app.setMode(EuclidianConstants.MODE_MOVE);
					movedGeoVector = vec;
					moveMode = MOVE_VECTOR_NO_GRID;
					return;
				}
			}
	
			// Michael Borcherds 2007-10-07 allow right mouse button to drag
			// points
			// mathieu : also if it's mode point, we can drag the point
			if (app.isRightClick(event)
					|| (mode == EuclidianConstants.MODE_POINT)
					|| (mode == EuclidianConstants.MODE_COMPLEX_NUMBER)
					|| (mode == EuclidianConstants.MODE_POINT_ON_OBJECT)
					|| (mode == EuclidianConstants.MODE_SLIDER)
					|| (mode == EuclidianConstants.MODE_BUTTON_ACTION)
					|| (mode == EuclidianConstants.MODE_TEXTFIELD_ACTION)
					|| (mode == EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX)
					|| (mode == EuclidianConstants.MODE_TEXT)) {
				view.setHits(mouseLoc);
	
				// make sure slider tool drags only sliders, not other object
				// types
				if (mode == EuclidianConstants.MODE_SLIDER) {
					if (view.getHits().size() != 1) {
						return;
					}
	
					if (!(view.getHits().get(0) instanceof GeoNumeric)) {
						return;
					}
				} else if ((mode == EuclidianConstants.MODE_BUTTON_ACTION)
						|| (mode == EuclidianConstants.MODE_TEXTFIELD_ACTION)) {
					if (view.getHits().size() != 1) {
						return;
					}
	
					if (!(view.getHits().get(0) instanceof GeoButton)) {
						return;
					}
				} else if (mode == EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX) {
					if (view.getHits().size() != 1) {
						return;
					}
	
					if (!(view.getHits().get(0) instanceof GeoBoolean)) {
						return;
					}
				} else if (mode == EuclidianConstants.MODE_TEXT) {
					if (view.getHits().size() != 1) {
						return;
					}
	
					if (!(view.getHits().get(0) instanceof GeoText)) {
						return;
					}
				}
	
				if (viewHasHitsForMouseDragged()) {
					TEMPORARY_MODE = true;
					oldMode = mode; // remember current mode
					view.setMode(EuclidianConstants.MODE_MOVE);
					handleMousePressedForMoveMode(event, true);
	
					// make sure that dragging doesn't deselect the geos
					DONT_CLEAR_SELECTION = true;
	
					return;
				}
	
			}
			if (!app.isRightClickEnabled()) {
				return;
				// Michael Borcherds 2007-10-07
			}
	
			if (mode == EuclidianConstants.MODE_MOVE_ROTATE) {
				app.clearSelectedGeos(false);
				app.addSelectedGeo(rotationCenter, false, true);
			}
		}
		lastMouseLoc = mouseLoc;
		setMouseLocation(event);
		transformCoords();
	
		// ggb3D - only for 3D view
		if (moveMode == MOVE_ROTATE_VIEW) {
			if (processRotate3DView()) {
				return;
			}
		}
	
		if (app.isRightClick(event)) {
			// if there's no hit, or if first hit is not moveable, do 3D view
			// rotation
			if ((!TEMPORARY_MODE)
					|| (view.getHits().size() == 0)
					|| !view.getHits().get(0).isMoveable(view)
					|| (!view.getHits().get(0).isGeoPoint() &&  view.getHits()
							.get(0).hasDrawable3D())) {
				if (processRotate3DView()) { // in 2D view, return false
					if (TEMPORARY_MODE) {
						TEMPORARY_MODE = false;
						mode = oldMode;
						view.setMode(mode);
					}
					return;
				}
			}
		}
	
		// dragging eg a fixed point shouldn't start the selection rectangle
		if (view.getHits().isEmpty()) {
			
			// HTML5 applet -> no selection rectangle
			if (app.isHTML5Applet() && !App.isFullAppGui()) {
				
				// alternative: could make drag move the view?
				//TEMPORARY_MODE = true;
				//oldMode = mode; // remember current mode
				//view.setMode(EuclidianConstants.MODE_TRANSLATEVIEW);

			}			
			// zoom rectangle (right drag) or selection rectangle (left drag)
			// Michael Borcherds 2007-10-07 allow dragging with right mouse
			// button
			else if (((app.isRightClick(event)) || allowSelectionRectangle())
					&& !TEMPORARY_MODE) {
				// Michael Borcherds 2007-10-07
				// set zoom rectangle's size
				// right-drag: zoom
				// Shift-right-drag: zoom without preserving aspect ratio
				updateSelectionRectangle((app.isRightClick(event) && !event
						.isShiftDown())
				// MACOS:
				// Cmd-left-drag: zoom
				// Cmd-shift-left-drag: zoom without preserving aspect ratio
						|| (app.isMacOS() && app.isControlDown(event)
								&& !event.isShiftDown() && !app
									.isRightClick(event))
						|| view.isLockedAxesRatio());
				view.repaintView();
				return;
			}
		}
	
		// update previewable
		if (view.getPreviewDrawable() != null) {
			view.getPreviewDrawable().updateMousePos(
					view.toRealWorldCoordX(mouseLoc.x),
					view.toRealWorldCoordY(mouseLoc.y));
		}
	
		/*
		 * Conintuity handling
		 * 
		 * If the mouse is moved wildly we take intermediate steps to get a more
		 * continous behaviour
		 */
		if (kernel.isContinuous() && (lastMouseLoc != null)) {
			double dx = mouseLoc.x - lastMouseLoc.x;
			double dy = mouseLoc.y - lastMouseLoc.y;
			double distsq = (dx * dx) + (dy * dy);
			if (distsq > MOUSE_DRAG_MAX_DIST_SQUARE) {
				double factor = Math.sqrt(MOUSE_DRAG_MAX_DIST_SQUARE / distsq);
				dx *= factor;
				dy *= factor;
	
				// number of continuity steps <= MAX_CONTINUITY_STEPS
				int steps = Math
						.min((int) (1.0 / factor), MAX_CONTINUITY_STEPS);
				int mlocx = mouseLoc.x;
				int mlocy = mouseLoc.y;
	
				// Application.debug("BIG drag dist: " + Math.sqrt(distsq) +
				// ", steps: " + steps );
				for (int i = 1; i <= steps; i++) {
					mouseLoc.x = (int) Math.round(lastMouseLoc.x + (i * dx));
					mouseLoc.y = (int) Math.round(lastMouseLoc.y + (i * dy));
					calcRWcoords();
	
					handleMouseDragged(false, event);
				}
	
				// set endpoint of mouse movement if we are not already there
				if ((mouseLoc.x != mlocx) || (mouseLoc.y != mlocy)) {
					mouseLoc.x = mlocx;
					mouseLoc.y = mlocy;
					calcRWcoords();
				}
			}
		}
	
		if (pastePreviewSelected != null) {
			if (!pastePreviewSelected.isEmpty()) {
				updatePastePreviewPosition();
			}
		}
	
		handleMouseDragged(true, event);
	}

	private static boolean penMode(int mode2) {
		switch (mode2) {
		case EuclidianConstants.MODE_PEN:
		//case EuclidianConstants.MODE_PENCIL:
		case EuclidianConstants.MODE_FREEHAND_SHAPE:
			return true;
		}

	return false;
	}

	/**
	 * 
	 * @return true if a view button has been pressed (see 3D)
	 */
	protected boolean handleMousePressedForViewButtons() {
		return false;
	}

	/** right-press the mouse makes start 3D rotation */
	protected void processRightPressFor3D() {
		//3D only
	}

	protected void createNewPointForModePoint(Hits hits, boolean complex) {
		if ((mode == EuclidianConstants.MODE_POINT)
				|| (mode == EuclidianConstants.MODE_COMPLEX_NUMBER)) {// remove
																		// polygons
																		// :
																		// point
																		// inside
																		// a
																		// polygon
																		// is
																		// created
																		// free,
																		// as in
																		// v3.2
			hits.removeAllPolygons();
			hits.removeConicsHittedOnFilling();
			createNewPoint(hits, true, false, true, true, complex);
		} else {// if mode==EuclidianView.MODE_POINT_ON_OBJECT, point can be in
				// a region
			createNewPoint(hits, true, true, true, true, complex);
		}
	}

	protected void createNewPointForModeOther(Hits hits) {
		createNewPoint(hits, true, false, true, true, false);
	}

	protected void handleMousePressedForRotateMode() {
		GeoElement geo;
		Hits hits;
	
		// we need the center of the rotation
		if (rotationCenter == null) {
			view.setHits(mouseLoc);
			rotationCenter = (GeoPoint) chooseGeo(
					view.getHits().getHits(Test.GEOPOINT, tempArrayList),
					true);
			app.addSelectedGeo(rotationCenter);
			moveMode = MOVE_NONE;
		} else {
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			// hits = view.getHits(mouseLoc);
			// got rotation center again: deselect
			if (!hits.isEmpty() && hits.contains(rotationCenter)) {
				app.removeSelectedGeo(rotationCenter);
				rotationCenter = null;
				moveMode = MOVE_NONE;
				return;
			}
	
			moveModeSelectionHandled = true;
	
			// find and set rotGeoElement
			hits = hits.getPointRotateableHits(view, rotationCenter);
			if (!hits.isEmpty() && hits.contains(rotGeoElement)) {
				geo = rotGeoElement;
			} else {
				geo = chooseGeo(hits, true);
				app.addSelectedGeo(geo);
			}
			rotGeoElement = geo;
	
			if (geo != null) {
				doSingleHighlighting(rotGeoElement);
				// rotGeoElement.setHighlighted(true);
	
				// init values needed for rotation
				rotStartGeo = rotGeoElement.copy();
				rotStartAngle = Math.atan2(yRW - rotationCenter.inhomY, xRW
						- rotationCenter.inhomX);
				moveMode = MOVE_ROTATE;
			} else {
				moveMode = MOVE_NONE;
			}
		}
	}

	protected final void mousePressedTranslatedView() {
	
		Hits hits;
	
		// check if axis is hit
		// hits = view.getHits(mouseLoc);
		view.setHits(mouseLoc);
		hits = view.getHits();
		hits.removePolygons();
		// Application.debug("MODE_TRANSLATEVIEW - "+hits.toString());
	
		/*
		 * if (!hits.isEmpty() && hits.size() == 1) { Object hit0 = hits.get(0);
		 * if (hit0 == kernel.getXAxis()) moveMode = MOVE_X_AXIS; else if (hit0
		 * == kernel.getYAxis()) moveMode = MOVE_Y_AXIS; else moveMode =
		 * MOVE_VIEW; } else { moveMode = MOVE_VIEW; }
		 */
	
		moveMode = MOVE_VIEW;
		if (!hits.isEmpty() && !view.isLockedAxesRatio() && view.isZoomable()) {
			for (Object hit : hits) {
				if (hit == kernel.getXAxis()) {
					moveMode = MOVE_X_AXIS;
				}
				if (hit == kernel.getYAxis()) {
					moveMode = MOVE_Y_AXIS;
				}
			}
		}
	
		startLoc = mouseLoc;
		if (!TEMPORARY_MODE) {
			if (moveMode == MOVE_VIEW) {
				view.setMoveCursor();
			} else {
				view.setDragCursor();
			}
		}
	
		// xZeroOld = view.getXZero();
		// yZeroOld = view.getYZero();
		view.rememberOrigins();
		xTemp = xRW;
		yTemp = yRW;
		view.setShowAxesRatio((moveMode == MOVE_X_AXIS)
				|| (moveMode == MOVE_Y_AXIS));
		// view.setDrawMode(EuclidianConstants.DRAW_MODE_DIRECT_DRAW);
	
	}

	protected void switchModeForMousePressed(AbstractEvent e) {
	
		Hits hits;
		//TODO we shall never get mode > 1000 here
		if(mode>1000)
			app.setMode(EuclidianConstants.MODE_MOVE);
		switch (mode) {
		// create new point at mouse location
		// this point can be dragged: see mouseDragged() and mouseReleased()
		case EuclidianConstants.MODE_COMPLEX_NUMBER:
			view.setHits(mouseLoc);
			hits = view.getHits();
			createNewPointForModePoint(hits, true);
			break;
		case EuclidianConstants.MODE_POINT:
		case EuclidianConstants.MODE_POINT_ON_OBJECT:
			view.setHits(mouseLoc);
			hits = view.getHits();

			// if mode==EuclidianView.MODE_POINT_ON_OBJECT, point can be in a
			// region
			createNewPointForModePoint(hits, false);
			break;
	
		case EuclidianConstants.MODE_SEGMENT:
		case EuclidianConstants.MODE_SEGMENT_FIXED:
		case EuclidianConstants.MODE_JOIN:
		case EuclidianConstants.MODE_RAY:
		case EuclidianConstants.MODE_VECTOR:
		case EuclidianConstants.MODE_CIRCLE_TWO_POINTS:
		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS:
		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
		case EuclidianConstants.MODE_SEMICIRCLE:
		case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
		case EuclidianConstants.MODE_POLYGON:
		case EuclidianConstants.MODE_POLYLINE:
		case EuclidianConstants.MODE_REGULAR_POLYGON:
			// hits = view.getHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			createNewPointForModeOther(hits);
			break;
	
		case EuclidianConstants.MODE_VECTOR_POLYGON:
		case EuclidianConstants.MODE_RIGID_POLYGON:
			view.setHits(mouseLoc);
			hits = view.getHits();
			
			// allow first object clicked on to be a Polygon -> create new Rigid/Vector Polygon from it
			if (hits.size() > 1) {
				hits.removePolygons();
			}
			if (hits.size() == 1 && hits.get(0).isGeoPolygon()) { 
				// do nothing
			} else {
				createNewPoint(hits, false, false, false, false, false);
			}
			break;
	
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			if (!allowSelectionRectangleForTranslateByVector) {
				view.setHits(mouseLoc);
				hits = view.getHits();
				hits.removePolygons();
				if (hits.size() == 0) {
					createNewPoint(hits, false, true, true);
				}
			}
			break;
	
		case EuclidianConstants.MODE_PARALLEL:
		case EuclidianConstants.MODE_PARABOLA: // Michael Borcherds 2008-04-08
		case EuclidianConstants.MODE_ORTHOGONAL:
		case EuclidianConstants.MODE_LINE_BISECTOR:
		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
		case EuclidianConstants.MODE_TANGENTS:
		case EuclidianConstants.MODE_POLAR_DIAMETER:
			// hits = view.getHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			if (hits.size() == 0) {
				createNewPoint(hits, false, true, true);
			}
			break;
	
		case EuclidianConstants.MODE_COMPASSES: // Michael Borcherds 2008-03-13
			// hits = view.getHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			if (hits.isEmpty()) {
				createNewPoint(hits, false, true, true);
			}
			break;
	
		case EuclidianConstants.MODE_ANGLE:
			// hits = view.getTopHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits().getTopHits();
			// check if we got a polygon
			if (hits.isEmpty()) {
				createNewPoint(hits, false, false, true);
			}
			break;
	
		case EuclidianConstants.MODE_ANGLE_FIXED:
		case EuclidianConstants.MODE_MIDPOINT:
			// hits = view.getHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			if (hits.isEmpty()
					|| (!hits.get(0).isGeoSegment() && !hits.get(0)
							.isGeoConic())) {
				createNewPoint(hits, false, false, true);
			}
			break;
	
		case EuclidianConstants.MODE_MOVE_ROTATE:
			handleMousePressedForRotateMode();
			break;
	
		case EuclidianConstants.MODE_RECORD_TO_SPREADSHEET:
			view.setHits(mouseLoc);
			hits = view.getHits();
			GeoElement tracegeo = hits.getFirstHit(Test.GEOPOINTND);
			if (tracegeo == null) {
				tracegeo = hits.getFirstHit(Test.GEOVECTOR);
			}
			if (tracegeo == null) {
				tracegeo = hits.getFirstHit(Test.GEONUMERIC);
			}
			if (tracegeo == null) {
				tracegeo = hits.getFirstHit(Test.GEOLIST);
			}
			if (tracegeo != null) {
				if (recordObject == null) {
					if (!app.getTraceManager().isTraceGeo(tracegeo)) {
						app.getTraceManager().addSpreadsheetTraceGeo(tracegeo);
					}
					recordObject = tracegeo;
				}
				handleMousePressedForMoveMode(e, false);
				tracegeo.updateRepaint();
			}
			break;
	
		// move an object
		case EuclidianConstants.MODE_MOVE:
		//case EuclidianConstants.MODE_VISUAL_STYLE:
			handleMousePressedForMoveMode(e, false);
			break;
	
		// move drawing pad or axis
		case EuclidianConstants.MODE_TRANSLATEVIEW:
	
			mousePressedTranslatedView();
	
			break;
	
		default:
			moveMode = MOVE_NONE;
		}
	}

	protected void wrapMousePressed(AbstractEvent event) {
		

		penDragged = false;
		
		if (app.isUsingFullGui() && app.getGuiManager() != null) {
			// determine parent panel to change focus
			// EuclidianDockPanelAbstract panel =
			// (EuclidianDockPanelAbstract)SwingUtilities.getAncestorOfClass(EuclidianDockPanelAbstract.class,
			// (Component)e.getSource());
	
			// if(panel != null) {
			// app.getGuiManager().getLayout().getDockManager().setFocusedPanel(panel);
			// }
			app.getGuiManager().setFocusedPanel(event,false);		
			app.getGuiManager().mousePressedForPropertiesView();
			
		}
		
		
		setMouseLocation(event);
	
		if (handleMousePressedForViewButtons()) {
			return;
		}
	
		Hits hits;
	
		if (penMode(mode)) {
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removeAllButImages();
			getPen().handleMousePressedForPenMode(event, hits);
			return;
		}
		this.pressedButton = view.getHitButton(mouseLoc);
		if(pressedButton!=null){
		pressedButton.setPressed(true);
		pressedButton.setDraggedOrContext(event.isMetaDown()
				|| event.isPopupTrigger());
		}
		//TODO:repaint?
	
		// GeoElement geo;
		transformCoords();
	
		moveModeSelectionHandled = false;
		DRAGGING_OCCURED = false;
		view.setSelectionRectangle(null);
		selectionStartPoint.setLocation(mouseLoc);
	
		if (hitResetIcon() || view.hitAnimationButton(event)) {
			// see mouseReleased
			return;
		}
	
		if (app.isRightClick(event)) {
			// ggb3D - for 3D rotation
			processRightPressFor3D();
	
			return;
		} else if (app.isShiftDragZoomEnabled() && (
		// MacOS: shift-cmd-drag is zoom
				(event.isShiftDown() && !app.isControlDown(event)) // All
																	// Platforms:
																	// Shift key
						|| (event.isControlDown() && app.isWindows() // old
																		// Windows
																		// key:
																		// Ctrl
																		// key
						) || app.isMiddleClick(event))) {
			// Michael Borcherds 2007-12-08 BEGIN
			// bugfix: couldn't select multiple objects with Ctrl
	
			view.setHits(mouseLoc);
			hits = view.getHits();
			switchModeForRemovePolygons(hits);
			if (!hits.isEmpty()) // bugfix 2008-02-19 removed this:&&
									// ((GeoElement) hits.get(0)).isGeoPoint())
			{
				DONT_CLEAR_SELECTION = true;
			}
			// Michael Borcherds 2007-12-08 END
			TEMPORARY_MODE = true;
			oldMode = mode; // remember current mode
			view.setMode(EuclidianConstants.MODE_TRANSLATEVIEW);
		}

		switchModeForMousePressed(event);
	}

	protected boolean processZoomRectangle() {
		GRectangle rect = view.getSelectionRectangle();
		if (rect == null) {
			return false;
		}
	
		if ((rect.getWidth() < 30) || (rect.getHeight() < 30)
				|| !app.isShiftDragZoomEnabled() // Michael Borcherds 2007-12-11
		) {
			view.setSelectionRectangle(null);
			view.repaintView();
			return false;
		}
	
		view.resetMode();
		// zoom zoomRectangle to EuclidianView's size
		// double factor = (double) view.width / (double) rect.width;
		// Point p = rect.getLocation();
		view.setSelectionRectangle(null);
		// view.setAnimatedCoordSystem((view.xZero - p.x) * factor,
		// (view.yZero - p.y) * factor, view.xscale * factor, 15, true);
	
		// zoom without (necessarily) preserving the aspect ratio
		view.setAnimatedRealWorldCoordSystem(
				view.toRealWorldCoordX(rect.getMinX()),
				view.toRealWorldCoordX(rect.getMaxX()),
				view.toRealWorldCoordY(rect.getMaxY()),
				view.toRealWorldCoordY(rect.getMinY()), 15, true);
		return true;
	}
	/**
	 * Removes geos that don't match given test from geos and updates selection
	 * @param hits srt of its geos, may not be null
	 * @param test test to filter specific object types
	 */
	protected void processSelectionRectangleForTransformations(Hits hits,
			Test test) {
				for (int i = 0; i < hits.size(); i++) {
					GeoElement geo = hits.get(i);
					if (!(test.check(geo))
					// || geo.isGeoPolygon()
					) {
						hits.remove(i);
					}
				}
				removeParentPoints(hits);
				selectedGeos.addAll(hits);
				app.setSelectedGeos(hits,false);
				app.updateSelection(hits.size()>0);
			}

	protected void processSelectionRectangle(AbstractEvent e) {
		clearSelections();
		view.setHits(view.getSelectionRectangle());
		Hits hits = view.getHits();
	
		boolean changedKernel = false;
		
		switch (mode) {
		case EuclidianConstants.MODE_SELECTION_LISTENER:
			break;
	
		case EuclidianConstants.MODE_MIRROR_AT_POINT:
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds
														// 2008-03-23
			processSelectionRectangleForTransformations(hits,
					Test.TRANSFORMABLE);
			break;
	
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			processSelectionRectangleForTransformations(hits,
					Test.TRANSFORMABLE);
			break;
	
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			processSelectionRectangleForTransformations(hits,
					Test.TRANSFORMABLE);
			break;
	
		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			processSelectionRectangleForTransformations(hits, Test.DILATEABLE);
			break;
	
		case EuclidianConstants.MODE_CREATE_LIST:
			removeParentPoints(hits);
			selectedGeos.addAll(hits);
			app.setSelectedGeos(hits);
			changedKernel = processMode(hits, e);
			view.setSelectionRectangle(null);
			break;
	
		case EuclidianConstants.MODE_FITLINE:
	
			// check for list first
			if (hits.size() == 1) {
				if (hits.get(0).isGeoList()) {
					selectedGeos.addAll(hits);
					app.setSelectedGeos(hits);
					changedKernel = processMode(hits, e);
					view.setSelectionRectangle(null);
					break;
				}
			}
	
			// remove non-Points
			for (int i = 0; i < hits.size(); i++) {
				GeoElement geo = hits.get(i);
				if (!(Test.GEOPOINT.check(geo))) {
					hits.remove(i);
				}
			}
	
			// Fit line makes sense only for more than 2 points (or one list)
			if (hits.size() < 3) {
				hits.clear();
			} else {
				removeParentPoints(hits);
				selectedGeos.addAll(hits);
				app.setSelectedGeos(hits);
				changedKernel = processMode(hits, e);
				view.setSelectionRectangle(null);
			}
			break;
	
		default:
			// STANDARD CASE
			app.setSelectedGeos(hits,false);
			app.updateSelection((hits != null) && (hits.size() > 0));
	
			// if alt pressed, create list of objects as string and copy to
			// input bar
			if ((hits != null) && (hits.size() > 0) && (e != null)
					&& e.isAltDown() && app.isUsingFullGui()
					&& app.showAlgebraInput()) {
				
	
				geogebra.common.javax.swing.GTextComponent textComponent = app.getGuiManager()
						.getAlgebraInputTextField();
	
				StringBuilder sb = new StringBuilder();
				sb.append(" {");
				for (int i = 0; i < hits.size(); i++) {
					sb.append(hits.get(i).getLabel(StringTemplate.defaultTemplate));
					if (i < (hits.size() - 1)) {
						sb.append(", ");
					}
				}
				sb.append("} ");
				// Application.debug(sb+"");
				textComponent.replaceSelection(sb.toString());
			}
			break;
		}
		
		if (changedKernel) {
			app.storeUndoInfo();
		}
	
		kernel.notifyRepaint();
	}

	protected void processSelection() {
		Hits hits = new Hits();
		hits.addAll(app.getSelectedGeos());
		clearSelections();
	
		switch (mode) {
		case EuclidianConstants.MODE_MIRROR_AT_POINT:
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds
														// 2008-03-23
			processSelectionRectangleForTransformations(hits,
					Test.TRANSFORMABLE);
			break;
	
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			processSelectionRectangleForTransformations(hits,
					Test.TRANSFORMABLE);
			break;
	
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			processSelectionRectangleForTransformations(hits,
					Test.TRANSFORMABLE);
			break;
	
		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			processSelectionRectangleForTransformations(hits, Test.DILATEABLE);
			break;
	
		// case EuclidianConstants.MODE_CREATE_LIST:
		case EuclidianConstants.MODE_FITLINE:
			for (int i = 0; i < hits.size(); i++) {
				GeoElement geo = hits.get(i);
				if (!(Test.GEOPOINT.check(geo))) {
					hits.remove(i);
				}
			}
			// Fit line makes sense only for more than 2 points
			if (hits.size() < 3) {
				hits.clear();
			} else {
				removeParentPoints(hits);
				selectedGeos.addAll(hits);
				app.setSelectedGeos(hits);
				processMode(hits, null);
				view.setSelectionRectangle(null);
			}
			break;
	
		default:
			break;
		}
	
		kernel.notifyRepaint();
	}

	public void showDrawingPadPopup(GPoint mouse) {
		app.getGuiManager().showDrawingPadPopup(view, mouse);
	}

	protected void wrapMouseReleased(AbstractEvent event) {
		if(pressedButton!=null){
			pressedButton.setDraggedOrContext(pressedButton.getDraggedOrContext()
					|| event.isMetaDown() || event.isPopupTrigger());
			
			// make sure that Input Boxes lose focus (and so update) before running scripts
			view.requestFocusInWindow();
			
			pressedButton.setPressed(false);	
			pressedButton=null;
		}
		sliderValue = null;
		if (event != null) {
			mx = event.getX();
			my = event.getY();
		}
		// reset
		transformCoordsOffset[0] = 0;
		transformCoordsOffset[1] = 0;
	
		if (textfieldHasFocus) {
			return;
		}
	
		if (penMode(mode) && penDragged) {
			getPen().handleMouseReleasedForPenMode(event);
			return;
		}
	
		boolean changedKernel0 = false;
		if (pastePreviewSelected != null) {
	
			mergeStickyPointsAfterPaste();
	
			// add moved points to sticky points again
			for (int i = 0; i < pastePreviewSelectedAndDependent.size(); i++) {
				GeoElement geo = pastePreviewSelectedAndDependent.get(i);
				if (geo.isGeoPoint()) {
					if (!view.getStickyPointList().contains(geo)) {
						view.getStickyPointList().add((GeoPointND) geo);
					}
				}
			}
			persistentStickyPointList = new ArrayList<GeoPointND>();
	
			pastePreviewSelected = null;
			pastePreviewSelectedAndDependent = null;
			view.setPointCapturing(previousPointCapturing);
			changedKernel0 = true;
			app.getKernel().getConstruction().getUndoManager()
					.storeUndoInfoAfterPasteOrAdd();
		}
	
		// if (mode != EuclidianConstants.MODE_RECORD_TO_SPREADSHEET)
		// view.resetTraceRow(); // for trace/spreadsheet
		if (getMovedGeoPoint() != null) {
	
			processReleaseForMovedGeoPoint(event);
			/*
			 * // deselect point after drag, but not on click if
			 * (movedGeoPointDragged) getMovedGeoPoint().setSelected(false);
			 * 
			 * if (mode != EuclidianConstants.MODE_RECORD_TO_SPREADSHEET)
			 * getMovedGeoPoint().resetTraceColumns();
			 */
		}
		if (movedGeoNumeric != null) {
	
			// deselect slider after drag, but not on click
			// if (movedGeoNumericDragged) movedGeoNumeric.setSelected(false);
	
			if ((mode != EuclidianConstants.MODE_RECORD_TO_SPREADSHEET)
					&& app.isUsingFullGui()) {
				movedGeoNumeric.resetTraceColumns();
			}
		}
	
		movedGeoPointDragged = false;
		movedGeoNumericDragged = false;
	
		if ((view.getHits() == null)||(view.getHits().size()==0)||
				!(view.getHits().getTopHits().get(0) instanceof GeoTextField)){
			view.requestFocusInWindow();
		}
		
		setMouseLocation(event);
	
		setAltDown(event.isAltDown());
	
		transformCoords();
		Hits hits = null;
	
		if (hitResetIcon()) {
			app.reset();
			return;
		} else if (view.hitAnimationButton(event)) {
			if (kernel.isAnimationRunning()) {
				kernel.getAnimatonManager().stopAnimation();
			} else {
				kernel.getAnimatonManager().startAnimation();
			}
			view.repaintView();
			app.setUnsaved();
			return;
		}
	
		// Michael Borcherds 2007-10-08 allow drag with right mouse button
		if ((app.isRightClick(event) || app.isControlDown(event)))// &&
																			// !TEMPORARY_MODE)
		{
			if (processRightReleaseFor3D()) {
				return;
			}
			if (!TEMPORARY_MODE) {
				if (!app.isRightClickEnabled()) {
					return;
				}
				if (processZoomRectangle()) {
					return;
					// Michael Borcherds 2007-10-08
				}
	
				// make sure cmd-click selects multiple points (not open
				// properties)
				if ((app.isMacOS() && app.isControlDown(event))
						|| !app.isRightClick(event)) {
					return;
				}
	
				// get selected GeoElements
				// show popup menu after right click
				view.setHits(mouseLoc);
				hits = view.getHits().getTopHits();
				if (hits.isEmpty()) {
					// no hits
					if (app.isUsingFullGui() && app.getGuiManager() != null) {
						if (app.selectedGeosSize() > 0) {
							// GeoElement selGeo = (GeoElement)
							// app.getSelectedGeos().get(0);
							app.getGuiManager().showPopupMenu(
									app.getSelectedGeos(),  view, mouseLoc);
						} else {
							showDrawingPadPopup(mouseLoc);
						}
					}
				} else {
					// there are hits
					if (app.selectedGeosSize() > 0) {
	
						// right click on already selected geos -> show menu for
						// them
						// right click on object(s) not selected -> clear
						// selection
						// and show menu just for new objects
						
						if (!hits.intersect(app.getSelectedGeos())) {
							app.clearSelectedGeos(false); //repaint will be done next step
							app.addSelectedGeos(hits, true);
						} else {
							//app.addSelectedGeo(hits.get(0));
						}

						if (app.isUsingFullGui() && app.getGuiManager() != null)
							showPopupMenuChooseGeo(app.getSelectedGeos(),hits);
							//app.getGuiManager().showPopupMenu(app.getSelectedGeos(), view, mouseLoc);

					} else {
						// no selected geos: choose geo and show popup menu				
						if (app.isUsingFullGui() && app.getGuiManager() != null) {
							//if (geo != null) {
							
							    GeoElement geo = chooseGeo(hits, true);
								ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
								geos.add(geo);
								showPopupMenuChooseGeo(geos,hits);

							/* Now overriden
							} else {
								// for 3D : if the geo hitted is xOyPlane, then
								// chooseGeo return null
								// app.getGuiManager().showDrawingPadPopup((EuclidianView)
								// view, mouseLoc);
								showDrawingPadPopup(mouseLoc);
							}
							*/
						}
						
					}
				}
				return;
			}
		}
	
		// handle moving
		boolean changedKernel = false;
		if (DRAGGING_OCCURED) {
	
			DRAGGING_OCCURED = false;
			// // copy value into input bar
			// if (mode == EuclidianView.MODE_MOVE && movedGeoElement != null) {
			// app.geoElementSelected(movedGeoElement,false);
			// }
	
			// check movedGeoElement.isLabelSet() to stop moving points
			// in Probability Calculator triggering Undo
			changedKernel = ((movedGeoElement != null) && movedGeoElement
					.isLabelSet()) && (moveMode != MOVE_NONE);
			movedGeoElement = null;
			rotGeoElement = null;
	
			// Michael Borcherds 2007-10-08 allow dragging with right mouse
			// button
			if (!TEMPORARY_MODE) {
				// Michael Borcherds 2007-10-08
				if (allowSelectionRectangle()) {
					processSelectionRectangle(event);
	
					return;
				}
			}
		} else {
			// no hits: release mouse button creates a point
			// for the transformation tools
			// (note: this cannot be done in mousePressed because
			// we want to be able to select multiple objects using the selection
			// rectangle)
	
			changedKernel = switchModeForMouseReleased(mode, hits,
					changedKernel);
		}
	
		// remember helper point, see createNewPoint()
		if (changedKernel && !changedKernel0) {
			app.storeUndoInfo();
		}
	
		// make sure that when alt is pressed for creating a segment or line
		// it works if the endpoint is on a path
		if (useLineEndPoint && (lineEndPoint != null)) {
			mouseLoc.x = view.toScreenCoordX(lineEndPoint.x);
			mouseLoc.y = view.toScreenCoordY(lineEndPoint.y);
			useLineEndPoint = false;
		}
	
		// now handle current mode
		view.setHits(mouseLoc);
		hits = view.getHits();
		switchModeForRemovePolygons(hits);
		// Application.debug(mode + "\n" + hits.toString());
	
		// Michael Borcherds 2007-12-08 BEGIN moved up a few lines (bugfix:
		// Tools eg Line Segment weren't working with grid on)
		// grid capturing on: newly created point should be taken
		// Application.debug("POINT_CREATED="+POINT_CREATED+"\nhits=\n"+hits+"\ngetMovedGeoPoint()="+getMovedGeoPoint());
		if (POINT_CREATED) {
			hits = addPointCreatedForMouseReleased(hits);
		}
		POINT_CREATED = false;
		// Michael Borcherds 2007-12-08 END
	
		if (TEMPORARY_MODE) {
	
			// Michael Borcherds 2007-10-13 BEGIN
			view.setMode(oldMode);
			TEMPORARY_MODE = false;
			// Michael Borcherds 2007-12-08 BEGIN bugfix: couldn't select
			// multiple points with Ctrl
			if (DONT_CLEAR_SELECTION == false) {
				clearSelections();
			}
			DONT_CLEAR_SELECTION = false;
			// Michael Borcherds 2007-12-08 END
			// mode = oldMode;
			// Michael Borcherds 2007-10-13 END
		}
		// Michael Borcherds 2007-10-12 bugfix: ctrl-click on a point does the
		// original mode's command at end of drag if a point was clicked on
		// also needed for right-drag
		else {
			if (mode != EuclidianConstants.MODE_RECORD_TO_SPREADSHEET) {
				changedKernel = processMode(hits, event);
			}
			if (changedKernel) {
				app.storeUndoInfo();
			}
		}
		// Michael Borcherds 2007-10-12
	
		// Michael Borcherds 2007-10-12
		// moved up a few lines
		// changedKernel = processMode(hits, e);
		// if (changedKernel)
		// app.storeUndoInfo();
		// Michael Borcherds 2007-10-12
	
		if (!hits.isEmpty()) {
			// Application.debug("hits ="+hits);
			view.setDefaultCursor();
		} else {
			view.setHitCursor();
		}
	
		if ((mode == EuclidianConstants.MODE_RECORD_TO_SPREADSHEET)
				&& (recordObject != null)) {
			clearSelections();
		} else {
			// this is in the else branch to avoid running it twice
			refreshHighlighting(null, event);
		}
	
		// reinit vars
		// view.setDrawMode(EuclidianConstants.DRAW_MODE_BACKGROUND_IMAGE);
		moveMode = MOVE_NONE;
		initShowMouseCoords();
		view.setShowAxesRatio(false);
		

		
		if (!setJustCreatedGeosSelected()){ //first try to set just created geos as selected
			//if none, do specific stuff for properties view
			if (app.isUsingFullGui() && app.getGuiManager() != null) {//prevent objects created by a script
				if (checkBoxOrButtonJustHitted) //does nothing
					checkBoxOrButtonJustHitted = false;
				else
					app.getGuiManager().mouseReleasedForPropertiesView(mode!=EuclidianConstants.MODE_MOVE && mode!=EuclidianConstants.MODE_MOVE_ROTATE);
			}
		}
		
		kernel.notifyRepaint();
		
		
	}
	
	/**
	 * set just created geos as selected (if any)
	 * @return true if any just created geos
	 */
	public boolean setJustCreatedGeosSelected(){
		if (justCreatedGeos!=null && justCreatedGeos.size()>0){
			app.setSelectedGeos(justCreatedGeos);
			return true;
		}
		return false;
	}

	protected void wrapMouseWheelMoved(AbstractEvent event) {
		
		if (textfieldHasFocus) {
			return;
		}
	
		if (penMode(mode)) {
			return;
		}
	
		// don't allow mouse wheel zooming for applets if mode is not zoom mode
		boolean allowMouseWheel = !app.isApplet()
				|| (mode == EuclidianConstants.MODE_ZOOM_IN)
				|| (mode == EuclidianConstants.MODE_ZOOM_OUT)
				|| (app.isShiftDragZoomEnabled() && (event.isControlDown()
						|| event.isMetaDown() || event.isShiftDown()));
		
		if (app.isHTML5Applet()) {
			allowMouseWheel = mode == EuclidianConstants.MODE_ZOOM_IN
					|| mode == EuclidianConstants.MODE_ZOOM_OUT
					|| app.isShiftDragZoomEnabled();
		}
		
		if (!allowMouseWheel) {
			return;
		}
		
		wheelZoomingOccurred = true;	
		
		setMouseLocation(event);
	
		// double px = view.width / 2d;
		// double py = view.height / 2d;
		double px = mouseLoc.x;
		double py = mouseLoc.y;
		// double dx = view.getXZero() - px;
		// double dy = view.getYZero() - py;
	
		double xFactor = 1;
		if (event.isAltDown()) {
			xFactor = 1.5;
		}
	
		double reverse = app.isMouseWheelReversed() ? -1 : 1;
	
		double factor = ((event.getWheelRotation() * reverse) > 0) ? EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR
				* xFactor
				: 1d / (EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR * xFactor);
	
		// make zooming a little bit smoother by having some steps
	
		view.setAnimatedCoordSystem(
		// px + dx * factor,
		// py + dy * factor,
				px, py, factor, view.getXscale() * factor, 4, false);
		// view.yscale * factor);
		app.setUnsaved();
	}

	public GeoElement getRecordObject() {
		return recordObject;
	}

	public void setLineEndPoint(geogebra.common.awt.GPoint2D p) {
		if(p==null)
			lineEndPoint = null;
		else
		lineEndPoint = new GPoint2D.Double(p.getX(),p.getY());
		useLineEndPoint = true;
	}

	public Hits getHighlightedgeos() {
		return highlightedGeos.clone();
	}

	public void setAlpha(float alpha) {
		ArrayList<GeoElement> geos = app.getSelectedGeos();
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			geo.setAlphaValue(alpha);
			geo.updateRepaint();
		}
	}

	public void setSize(int size) {
		// if (mode == EuclidianView.MODE_VISUAL_STYLE) {
		ArrayList<GeoElement> geos = app.getSelectedGeos();
	
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof PointProperties) {
				((PointProperties) geo).setPointSize(size);
				geo.updateRepaint();
			} else {
				geo.setLineThickness(size);
				geo.updateRepaint();
			}
		}
		// }
	
	}

	public void setLineEndPoint(GPoint2D.Double point) {
		lineEndPoint = point;
		useLineEndPoint = true;
	}

	protected Previewable switchPreviewableForInitNewMode(int mode1) {
	
		Previewable previewDrawable = null;
		// init preview drawables
		switch (mode1) {
	
		case EuclidianConstants.MODE_FREEHAND_SHAPE:
			getPen().setFreehand(true);
	
			break;
		case EuclidianConstants.MODE_PEN:
			getPen().setFreehand(false);
			getPen().setAbsoluteScreenPosition(true);
			break;
			/*
		case EuclidianConstants.MODE_PENCIL:
			getPen().setFreehand(false);
			getPen().setAbsoluteScreenPosition(false);
			break;
	*/
			/*
			 * boolean createUndo = true; // scale both EVs 1:1 if
			 * (app.getEuclidianView().isVisible()) {
			 * app.getEuclidianView().zoomAxesRatio(1, true); createUndo =
			 * false; }
			 * 
			 * if (app.hasEuclidianView2() &&
			 * app.getEuclidianView2().isVisible()) {
			 * app.getEuclidianView2().zoomAxesRatio(1, createUndo); }//
			 */
	
			/*
			ArrayList<GeoElement> selection = app.getSelectedGeos();
			pen.setPenGeo(null);
			if (selection.size() == 1) {
				GeoElement geo = selection.get(0);
				// getCorner(1) == null as we can't write to transformed images
				if (geo.isGeoImage()) {
					GeoPoint2 c1 = ((GeoImage) geo).getCorner(0);
					GeoPoint2 c2 = ((GeoImage) geo).getCorner(1);
					GeoPoint2 c3 = ((GeoImage) geo).getCorner(2);
	
					if (((c3 == null)
							&& (c2 == null // c2 = null -> not transformed
							))
							// or c1 and c2 are the correct spacing for the
							// image not to be transformed
							// (ie image was probably created by the Pen Tool)
							|| ((c1 != null) && (c2 != null)
									&& noZoomNeeded(c1,c2,(GeoImage)geo))) {
						pen.setPenGeo(geo);
					} 
				} else if (geo instanceof GeoPolyLine) {
					pen.setPenGeo(geo);
				}
			}*/
	
			// no break;
	
		//case EuclidianConstants.MODE_VISUAL_STYLE:
	
			// openMiniPropertiesPanel();
	
		//	break;
	
		case EuclidianConstants.MODE_PARALLEL:
			previewDrawable = view.createPreviewParallelLine(selectedPoints,
					selectedLines);
			break;
	
		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
			previewDrawable = view.createPreviewAngleBisector(selectedPoints);
			break;
	
		case EuclidianConstants.MODE_ORTHOGONAL:
			previewDrawable = view.createPreviewPerpendicularLine(
					selectedPoints, selectedLines);
			break;
	
		case EuclidianConstants.MODE_LINE_BISECTOR:
			previewDrawable = view
					.createPreviewPerpendicularBisector(selectedPoints);
			break;
	
		case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
			previewDrawable = view
					.createPreviewConic(mode1, selectedPoints);
			break;
	
		case EuclidianConstants.MODE_JOIN: // line through two points
			useLineEndPoint = false;
			previewDrawable = view.createPreviewLine(selectedPoints);
			break;
	
		case EuclidianConstants.MODE_SEGMENT:
			useLineEndPoint = false;
			previewDrawable = view.createPreviewSegment(selectedPoints);
			break;
	
		case EuclidianConstants.MODE_RAY:
			useLineEndPoint = false;
			previewDrawable = view.createPreviewRay(selectedPoints);
			break;
	
		case EuclidianConstants.MODE_VECTOR:
			useLineEndPoint = false;
			previewDrawable = view.createPreviewVector(selectedPoints);
			break;
	
		case EuclidianConstants.MODE_POLYGON:
		case EuclidianConstants.MODE_RIGID_POLYGON:
		case EuclidianConstants.MODE_VECTOR_POLYGON:
			previewDrawable = view.createPreviewPolygon(selectedPoints);
			break;
	
		case EuclidianConstants.MODE_POLYLINE:
			previewDrawable = view.createPreviewPolyLine(selectedPoints);
			break;
	
		case EuclidianConstants.MODE_CIRCLE_TWO_POINTS:
		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
			previewDrawable = view.createPreviewConic(mode1, selectedPoints);
			break;
	
		case EuclidianConstants.MODE_ANGLE:
			previewDrawable = view.createPreviewAngle(selectedPoints);
			break;
	
		// preview for compass: radius first
		case EuclidianConstants.MODE_COMPASSES:
			previewDrawable = new DrawConic( view, mode1,
					selectedPoints, selectedSegments, selectedConicsND);
			break;
	
		// preview for arcs and sectors
		case EuclidianConstants.MODE_SEMICIRCLE:
		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			previewDrawable = new DrawConicPart( view, mode1,
					selectedPoints);
			break;
	
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			useLineEndPoint = false;
			previewDrawable = view.createPreviewVector(selectedPoints);
			break;
	
		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
			// select all hidden objects
			Iterator<GeoElement> it = kernel.getConstruction()
					.getGeoSetConstructionOrder().iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				// independent numbers should not be set visible
				// as this would produce a slider
				if (!geo.isSetEuclidianVisible()
						&& !((geo.isNumberValue() || geo.isBooleanValue()) && geo
								.isIndependent())) {
					geo.setEuclidianVisible(true);
					app.addSelectedGeo(geo);
					geo.updateRepaint();
				}
			}
			break;
	
		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
			app.setGeoForCopyStyle(null); // this will be the active geo template
			break;
	
		case EuclidianConstants.MODE_MOVE_ROTATE:
			rotationCenter = null; // this will be the active geo template
			break;
	
		case EuclidianConstants.MODE_RECORD_TO_SPREADSHEET:
	
			// G.Sturr 2010-5-14
			if (recordObject != null) {
				app.getTraceManager().removeSpreadsheetTraceGeo(recordObject);
				// END G.Sturr
			}
	
			recordObject = null;
	
			break;
	
		default:
			// macro mode?
			if (mode1 >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
				// get ID of macro
				int macroID = mode1 - EuclidianConstants.MACRO_MODE_ID_OFFSET;
				macro = kernel.getMacro(macroID);
				macroInput = macro.getInputTypes();
				this.mode = EuclidianConstants.MODE_MACRO;
			}
			break;
		}
	
		return previewDrawable;
	}


	protected void initNewMode(int newMode) {
		this.mode = newMode;
		initShowMouseCoords();
		// Michael Borcherds 2007-10-12
		// clearSelections();
		if (!TEMPORARY_MODE
				&& !EuclidianView.usesSelectionRectangleAsInput(newMode)) {
			clearSelections();
		}
		// Michael Borcherds 2007-10-12
		moveMode = MOVE_NONE;
	
		if (newMode == EuclidianConstants.MODE_RECORD_TO_SPREADSHEET) {
			if (!app.getGuiManager().hasSpreadsheetView()) {
				app.getGuiManager().attachSpreadsheetView();
			}
			if (!app.getGuiManager().showView(
					App.VIEW_SPREADSHEET)) {
				app.getGuiManager().setShowView(true,
						App.VIEW_SPREADSHEET);
			}
		}
	
		view.setPreview(switchPreviewableForInitNewMode(newMode));
		toggleModeChangedKernel = false;
	}

	public void setMode(int newMode) {

		if (pen != null) {
			pen.resetPenOffsets();
		}

		if ((newMode == EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS)
				|| (newMode == EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS)
				|| (newMode == EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS)) {
			return;
		}
	
		endOfMode(mode);
	
		allowSelectionRectangleForTranslateByVector = true;
	
		if (EuclidianView.usesSelectionRectangleAsInput(newMode)
				&& (view.getSelectionRectangle() != null)) {
			initNewMode(newMode);
			if (app.getActiveEuclidianView() == view) {
				processSelectionRectangle(null);
			}
		} else if (EuclidianView.usesSelectionAsInput(newMode)) {
			initNewMode(newMode);
			if (app.getActiveEuclidianView() == view) {
				processSelection();
			}
		} else {
			if (!TEMPORARY_MODE) {
				app.clearSelectedGeos(false);
			}
			initNewMode(newMode);
		}
	
		kernel.notifyRepaint();
	}

	public void zoomInOut(boolean altPressed, boolean minusPressed) {
		boolean allowZoom = !app.isApplet()
				|| (mode == EuclidianConstants.MODE_ZOOM_IN)
				|| (mode == EuclidianConstants.MODE_ZOOM_OUT)
				|| app.isShiftDragZoomEnabled();
		if (!allowZoom) {
			return;
		}
		double px, py;
		if (mouseLoc != null) {
			px = mouseLoc.x;
			py = mouseLoc.y;
		} else {
			px = view.getWidth() / 2;
			py = view.getHeight() / 2;
		}

		double factor = minusPressed ? 1d / EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR
				: EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR;

		// accelerated zoom
		if (altPressed) {
			factor *= minusPressed ? 2d / 3d : 1.5;
		}

		// make zooming a little bit smoother by having some steps
		view.setAnimatedCoordSystem(
		// px + dx * factor,
		// py + dy * factor,
				px, py, factor, view.getXscale() * factor, 4, false);
		// view.yscale * factor);
		app.setUnsaved();

	}

	public App getApplication() {
		return app;
	}

	/**
	 * show popup menu when no geo is selected
	 * @param selectedGeos1 first hits on the mouse
	 * @param hits hits on the mouse
	 */
	protected void showPopupMenuChooseGeo(ArrayList<GeoElement> selectedGeos1, Hits hits){

		//app.getGuiManager().showPopupMenu(firstHits,view, mouseLoc);
		
		app.getGuiManager().showPopupChooseGeo(
				selectedGeos1,
				hits,
				view, mouseLoc);

	}
	

	final public EuclidianPen getPen() {
		
		if (pen == null) {
			pen = new EuclidianPen(app, view);

		}	
		
		return pen;
	}
	

	public void resetPen() {
		if (pen != null) {
			pen.resetPenOffsets();
		}
		
	}

	public static void textBoxFocused(boolean b) {
		textBoxFocused = b;
		
	}
	
	private boolean wheelZoomingOccurred = false;

	private void checkZooming() {
		checkZooming(false);
	}

	/*
	 * when object created, make undo point if scroll wheel has been used
	 */
	private void checkZooming(boolean forPreviewable) {

		if (forPreviewable) {
			return;
			// Application.debug("check zooming");
		}

		if (wheelZoomingOccurred) {
			app.storeUndoInfo();
		}

		wheelZoomingOccurred = false;
	}	
}
