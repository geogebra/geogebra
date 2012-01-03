package geogebra.common.euclidian;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import geogebra.common.awt.Point;
import geogebra.common.awt.Point2D;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.View;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
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
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.geos.Test;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.main.AbstractApplication;

public abstract class EuclidianController {

	protected static int POLYGON_NORMAL = 0;

	protected static int POLYGON_RIGID = 1;

	protected static int POLYGON_VECTOR = 2;

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

	protected ArrayList<GeoPoint2> moveDependentPoints;

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

	protected GeoButton movedGeoButton;

	protected GeoElement movedLabelGeoElement;

	protected GeoElement movedGeoElement;

	protected GeoElement recordObject = null;

	protected MyDouble tempNum;

	protected double rotStartAngle;

	protected ArrayList translateableGeos;

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

	protected ArrayList<GeoConic> selectedConicsND = new ArrayList<GeoConic>();

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

	protected GeoPoint2 rotationCenter;

	protected int polygonMode = POLYGON_NORMAL;

	protected double[] transformCoordsOffset = new double[2];

	protected boolean allowSelectionRectangleForTranslateByVector = true;

	protected int previousPointCapturing;

	protected ArrayList<GeoPointND> persistentStickyPointList = new ArrayList<GeoPointND>();

	protected AbstractApplication app;

	protected Kernel kernel;

	protected Point startLoc;

	public Point mouseLoc;

	protected Point lastMouseLoc;

	protected Point oldLoc = new Point();

	protected Point2D.Double startPoint = new Point2D.Double();

	protected Point2D.Double lineEndPoint = null;

	protected Point selectionStartPoint = new Point();

	protected ArrayList<Double> tempDependentPointX;

	protected ArrayList<Double> tempDependentPointY;

	protected boolean mouseIsOverLabel = false;

	protected EuclidianViewInterfaceSlim view;
	
	// ==============================================
	// Pen

	public geogebra.common.euclidian.EuclidianPen pen;

	protected Hits handleAddSelectedArrayList = new Hits();
	
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

	public abstract void handleMovedElement(GeoElement selGeo, boolean b);
	
	public abstract void setApplication(AbstractApplication app);

	public abstract void clearSelections();

	public abstract boolean isAltDown();

	public abstract void setLineEndPoint(geogebra.common.awt.Point2D endPoint);

	public abstract GeoElement getRecordObject();

	public abstract void setMode(int mode);
	
	protected abstract void transformCoords();
	
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
						startPoint.setLocation(((GeoPoint2) geo).inhomX,
								((GeoPoint2) geo).inhomY);
						firstMoveable = false;
					} else if (geo.isGeoText()) {
						if (((GeoText) geo).hasAbsoluteLocation()) {
							GeoPoint2 loc = (GeoPoint2) ((GeoText) geo)
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
							GeoPoint2 loc = ((GeoImage) geo).getStartPoints()[2];
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
					} else if (geo.isGeoButton()) {
						startPoint.setLocation(view
								.toRealWorldCoordX(((GeoButton) geo)
										.getAbsoluteScreenLocX() - 5), view
								.toRealWorldCoordY(((GeoButton) geo)
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
					GeoPoint2 geo2 = (GeoPoint2) persistentStickyPointList
							.get(j);
					if (Kernel.isEqual(geo2.getInhomX(),
							((GeoPoint2) geo).getInhomX())
							&& Kernel.isEqual(geo2.getInhomY(),
									((GeoPoint2) geo).getInhomY())) {
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

	protected void endOfMode(int mode) {
		switch (mode) {
		case EuclidianConstants.MODE_RECORD_TO_SPREADSHEET:
			// just to be sure recordObject is set to null
			// usually this is already done at mouseRelease
			if (recordObject != null) {
				if (app.getTraceManager()
						.isTraceGeo(recordObject)) {
					app.getGuiManager().removeSpreadsheetTrace(recordObject);
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
		case EuclidianConstants.MODE_FREEHAND:
			pen.resetPenOffsets();
	
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
	
		// first hit is a line
		if (a.isGeoLine()) {
			if (b.isGeoLine()) {
				if (!((GeoLine) a).linDep((GeoLine) b)) {
					return kernel
							.IntersectLines(null, (GeoLine) a, (GeoLine) b);
				} else {
					return null;
				}
			} else if (b.isGeoConic()) {
				return kernel.IntersectLineConicSingle(null, (GeoLine) a,
						(GeoConic) b, xRW, yRW);
			} else if (b.isGeoFunctionable()) {
				// line and function
				GeoFunction f = ((GeoFunctionable) b).getGeoFunction();
				if (f.isPolynomialFunction(false)) {
					return kernel.IntersectPolynomialLineSingle(null, f,
							(GeoLine) a, xRW, yRW);
				} else {
					GeoPoint2 startPoint = new GeoPoint2(
							kernel.getConstruction());
					startPoint.setCoords(xRW, yRW, 1.0);
					return kernel.IntersectFunctionLine(null, f, (GeoLine) a,
							startPoint);
				}
			} else {
				return null;
			}
		}
		// first hit is a conic
		else if (a.isGeoConic()) {
			if (b.isGeoLine()) {
				return kernel.IntersectLineConicSingle(null, (GeoLine) b,
						(GeoConic) a, xRW, yRW);
			} else if (b.isGeoConic() && !a.isEqual(b)) {
				return kernel.IntersectConicsSingle(null, (GeoConic) a,
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
					return kernel.IntersectPolynomialLineSingle(null, aFun,
							(GeoLine) b, xRW, yRW);
				} else {
					GeoPoint2 startPoint = new GeoPoint2(
							kernel.getConstruction());
					startPoint.setCoords(xRW, yRW, 1.0);
					return kernel.IntersectFunctionLine(null, aFun,
							(GeoLine) b, startPoint);
				}
			} else if (b.isGeoFunctionable()) {
				GeoFunction bFun = ((GeoFunctionable) b).getGeoFunction();
				if (aFun.isPolynomialFunction(false)
						&& bFun.isPolynomialFunction(false)) {
					return kernel.IntersectPolynomialsSingle(null, aFun, bFun,
							xRW, yRW);
				} else {
					GeoPoint2 startPoint = new GeoPoint2(
							kernel.getConstruction());
					startPoint.setCoords(xRW, yRW, 1.0);
					return kernel.IntersectFunctions(null, aFun, bFun,
							startPoint);
				}
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

	protected final GeoPoint2[] getSelectedPoints() {
	
		GeoPoint2[] ret = new GeoPoint2[selectedPoints.size()];
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
		Iterator<GeoConic> it = selectedConicsND.iterator();
		while (it.hasNext()) {
			conics[i] = it.next();
			i++;
		}
		clearSelection(selectedConicsND);
		return conics;
	}

	protected final GeoConic[] getSelectedCircles() {
		GeoConic[] circles = new GeoConic[selectedConicsND.size()];
		int i = 0;
		Iterator<GeoConic> it = selectedConicsND.iterator();
		while (it.hasNext()) {
			GeoConic c = it.next();
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
		Iterator<GeoConic> it = selectedConicsND.iterator();
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

	protected GeoPointND createNewPoint(boolean forPreviewable, boolean complex) {
		GeoPointND ret = kernel.Point(null,
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
				return kernel.Point(null, path, x, y, !forPreviewable, complex);
			}

	protected GeoPointND createNewPoint2D(boolean forPreviewable, Region region, double x,
			double y, boolean complex) {
				GeoPointND ret = kernel.PointIn(null, region, x, y, true, complex);
				return ret;
			}

	public GeoPointND createNewPoint(boolean forPreviewable, Region region, double x,
			double y, double z, boolean complex) {
			
				if (((GeoElement) region).isGeoElement3D()) {
					return kernel.getManager3D().Point3DIn(null, region,
							new Coords(x, y, z, 1), !forPreviewable);
				} else {
					return createNewPoint2D(forPreviewable, region, x, y, complex);
				}
			}

	public GeoPointND createNewPoint(boolean forPreviewable, Path path, double x,
			double y, double z, boolean complex) {
			
				if (((GeoElement) path).isGeoElement3D()) {
					return kernel.getManager3D().Point3D(null, path, x, y, z,
							!forPreviewable);
				} else {
					return createNewPoint2D(forPreviewable, path, x, y, complex);
				}
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
	
		if (app.isUsingFullGui()) {
			app.getGuiManager().updateMenubarSelection();
		}
	}

	public ArrayList<GeoElement> getJustCreatedGeos() {
		return justCreatedGeos;
	}

	public void memorizeJustCreatedGeos(ArrayList<GeoElement> geos) {
		justCreatedGeos.clear();
		justCreatedGeos.addAll(geos);
		app.updateStyleBars();
		app.getGuiManager().updateMenubarSelection();
	}

	public void memorizeJustCreatedGeos(GeoElement[] geos) {
		justCreatedGeos.clear();
		for (int i = 0; i < geos.length; i++) {
			if (geos[i] != null) {
				justCreatedGeos.add(geos[i]);
			}
		}
		app.updateStyleBars();
		app.getGuiManager().updateMenubarSelection();
	}

	protected final void setHighlightedGeos(boolean highlight) {
		GeoElement geo;
		Iterator<GeoElement> it = highlightedGeos.iterator();
		while (it.hasNext()) {
			geo = it.next();
			geo.setHighlighted(highlight);
		}
	}

	protected void doSingleHighlighting(GeoElement geo) {
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
				ArrayList<GeoPoint2> ip = cp.getParentAlgorithm()
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

	protected final int addToSelectionList(ArrayList selectionList, GeoElement geo,
			int max) {
				if (geo == null) {
					return 0;
				}
			
				int ret = 0;
				if (selectionList.contains(geo)) { // remove from selection
					selectionList.remove(geo);
					if (selectionList != selectedGeos) {
						selectedGeos.remove(geo);
					}
					ret = -1;
				} else { // new element: add to selection
					if (selectionList.size() < max) {
						selectionList.add(geo);
						if (selectionList != selectedGeos) {
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
			ret = null;
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
				} else {
					return addToSelectionList(list,
							hits.getHits(geoClass, handleAddSelectedArrayList), max,
							addMore, hits.size() == 1);
				}
			}

	protected int handleAddSelectedRegions(Hits hits, int max,
			boolean addMore, ArrayList<?> list) {
				if (selectionPreview) {
					return addToHighlightedList(list,
							hits.getRegionHits(handleAddSelectedArrayList), max);
				} else {
					return addToSelectionList(list,
							hits.getRegionHits(handleAddSelectedArrayList), max,
							addMore, hits.size() == 1);
				}
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

}
