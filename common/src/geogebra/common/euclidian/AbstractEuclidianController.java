package geogebra.common.euclidian;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import geogebra.common.awt.Point;
import geogebra.common.awt.Point2D;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.View;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoPolygon;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
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
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.geos.Test;
import geogebra.common.kernel.geos.Transformable;
import geogebra.common.kernel.geos.Translateable;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.GeoAxisND;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.main.AbstractApplication;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.GeoClass;
import geogebra.common.util.MyMath;

public abstract class AbstractEuclidianController {

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

	private static boolean noPointsIn(Hits hits) {
		for (int i = 0; i < hits.size(); i++) {
			if ((hits.get(i)).isGeoPoint()) {
				return false;
			}
		}
		return true;
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

	protected EuclidianViewInterfaceCommon view;
	
	// ==============================================
	// Pen

	public geogebra.common.euclidian.EuclidianPen pen;

	protected Hits handleAddSelectedArrayList = new Hits();

	protected boolean textfieldHasFocus = false;

	protected String sliderValue = null;
	
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

	public abstract void setLineEndPoint(geogebra.common.awt.Point2D endPoint);

	public abstract GeoElement getRecordObject();

	public abstract void setMode(int mode);
	
	
	
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

	/** only used in 3D */
	protected void createNewPoint(GeoPointND sourcePoint) {
	}

	/** only used in 3D */
	protected void createNewPointIntersection(GeoPointND intersectionPoint) {
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
			ret[0] = getKernel().Line(null, (GeoPoint2) points[0],
					(GeoPoint2) points[1]);
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
			ret[0] = (GeoElement) getKernel().getManager3D().Ray3D(null,
					points[0], points[1]);
		} else {
			ret[0] = getKernel().Ray(null, (GeoPoint2) points[0],
					(GeoPoint2) points[1]);
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
			ret[0] = getKernel().Segment(null, (GeoPoint2) points[0],
					(GeoPoint2) points[1]);
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
		if (((GeoElement) a).isGeoElement3D()
				|| ((GeoElement) b).isGeoElement3D()) {
			return kernel.getManager3D().Vector3D(null, a, b);
		} else {
			return kernel.Vector(null, (GeoPoint2) a, (GeoPoint2) b);
		}
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
				return kernel.PolyLine(null, getSelectedPoints());
			}
		}
	
		// points needed
		addSelectedPoint(hits, GeoPolyLine.POLYLINE_MAX_POINTS, false);
		return null;
	}

	protected GeoElement[] polygon() {
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
			GeoPoint2[] points = new GeoPoint2[pointsND.length];
			boolean point3D = false;
			for (int i = 0; (i < pointsND.length) && !point3D; i++) {
				if (((GeoElement) pointsND[i]).isGeoElement3D()) {
					point3D = true;
				} else {
					points[i] = (GeoPoint2) pointsND[i];
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
			} else {
				GeoElement[] ret = { null };
				GeoElement[] ret0 = kernel.Polygon(null, points);
				if (ret0 != null) {
					ret[0] = ret0[0];
				}
				return ret;
			}
		}
	}

	protected GeoElement[] intersect(Hits hits) {
		// Application.debug(selectedLines);
	
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
	
		singlePointWanted = singlePointWanted && (selGeos() >= 2);
	
		// if (selGeos() > 2)
		// return false;
	
		// two lines
		if (selLines() >= 2) {
			GeoLineND[] lines = getSelectedLinesND();
			GeoElement[] ret = { null };
			ret[0] = (GeoElement) kernel.IntersectLines(null, lines[0],
					lines[1]);
			return ret;
		}
		// two conics
		else if (selConics() >= 2) {
			GeoConicND[] conics = getSelectedConicsND();
			GeoElement[] ret = { null };
			if (singlePointWanted) {
				ret[0] = kernel.IntersectConicsSingle(null,
						(GeoConic) conics[0], (GeoConic) conics[1], xRW, yRW);
			} else {
				ret = (GeoElement[]) kernel.IntersectConics(null, conics[0],
						conics[1]);
			}
			return ret;
		} else if (selFunctions() >= 2) {
			GeoFunction[] fun = getSelectedFunctions();
			boolean polynomials = fun[0].isPolynomialFunction(false)
					&& fun[1].isPolynomialFunction(false);
			if (!polynomials) {
				GeoPoint2 startPoint = new GeoPoint2(kernel.getConstruction());
				startPoint.setCoords(xRW, yRW, 1.0);
				return new GeoElement[] { kernel.IntersectFunctions(null,
						fun[0], fun[1], startPoint) };
			} else {
				// polynomials
				if (singlePointWanted) {
					return new GeoElement[] { kernel
							.IntersectPolynomialsSingle(null, fun[0], fun[1],
									xRW, yRW) };
				} else {
					return kernel.IntersectPolynomials(null, fun[0], fun[1]);
				}
			}
		}
		// one line and one conic
		else if ((selLines() >= 1) && (selConics() >= 1)) {
			GeoConic[] conic = getSelectedConics();
			GeoLine[] line = getSelectedLines();
			GeoElement[] ret = { null };
			if (singlePointWanted) {
				ret[0] = kernel.IntersectLineConicSingle(null, line[0],
						conic[0], xRW, yRW);
			} else {
				ret = kernel.IntersectLineConic(null, line[0], conic[0]);
			}
	
			return ret;
		}
		// line and polyLine
		else if ((selLines() >= 1) && (selPolyLines() >= 1)) {
			GeoLine line = getSelectedLines()[0];
			GeoPolyLine polyLine = getSelectedPolyLines()[0];
			GeoElement[] ret = { null };
			ret = kernel.IntersectLinePolyLine(new String[] { null }, line,
					polyLine);
			return ret;
		}
		// line and polygon
		else if ((selLines() >= 1) && (selPolygons() >= 1)) {
			GeoLine line = getSelectedLines()[0];
			GeoPolygon polygon = getSelectedPolygons()[0];
			GeoElement[] ret = { null };
			ret = kernel.IntersectLinePolygon(new String[] { null }, line,
					polygon);
			return ret;
		}
		// line and function
		else if ((selLines() >= 1) && (selFunctions() >= 1)) {
			GeoLine[] line = getSelectedLines();
			GeoFunction[] fun = getSelectedFunctions();
			GeoElement[] ret = { null };
			if (fun[0].isPolynomialFunction(false)) {
				if (singlePointWanted) {
					ret[0] = kernel.IntersectPolynomialLineSingle(null, fun[0],
							line[0], xRW, yRW);
				} else {
					ret = kernel.IntersectPolynomialLine(null, fun[0], line[0]);
				}
			} else {
				GeoPoint2 startPoint = new GeoPoint2(kernel.getConstruction());
				startPoint.setCoords(xRW, yRW, 1.0);
				ret[0] = kernel.IntersectFunctionLine(null, fun[0], line[0],
						startPoint);
			}
			return ret;
			// function and conic
		} else if ((selFunctions() >= 1) && (selConics() >= 1)) {
			GeoConic[] conic = getSelectedConics();
			GeoFunction[] fun = getSelectedFunctions();
			// if (fun[0].isPolynomialFunction(false)){
			if (singlePointWanted) {
				return new GeoElement[] { kernel
						.IntersectPolynomialConicSingle(null, fun[0], conic[0],
								xRW, yRW) };
			} else {
				return kernel.IntersectPolynomialConic(null, fun[0], conic[0]);
				// }
			}
		} else if (selImplicitpoly() >= 1) {
			if (selFunctions() >= 1) {
				GeoImplicitPoly p = getSelectedImplicitpoly()[0];
				GeoFunction fun = getSelectedFunctions()[0];
				// if (fun.isPolynomialFunction(false)){
				if (singlePointWanted) {
					return new GeoElement[] { kernel
							.IntersectImplicitpolyPolynomialSingle(null, p,
									fun, xRW, yRW) };
				} else {
					return kernel.IntersectImplicitpolyPolynomial(null, p, fun);
					// }else
					// return null;
				}
			} else if (selLines() >= 1) {
				GeoImplicitPoly p = getSelectedImplicitpoly()[0];
				GeoLine l = getSelectedLines()[0];
				if (singlePointWanted) {
					return new GeoElement[] { kernel
							.IntersectImplicitpolyLineSingle(null, p, l, xRW,
									yRW) };
				} else {
					return kernel.IntersectImplicitpolyLine(null, p, l);
				}
			} else if (selConics() >= 1) {
				GeoImplicitPoly p = getSelectedImplicitpoly()[0];
				GeoConic c = getSelectedConics()[0];
				if (singlePointWanted) {
					return new GeoElement[] { kernel
							.IntersectImplicitpolyConicSingle(null, p, c, xRW,
									yRW) };
				} else {
					return kernel.IntersectImplicitpolyConic(null, p, c);
				}
			} else if (selImplicitpoly() >= 2) {
				GeoImplicitPoly[] p = getSelectedImplicitpoly();
				if (singlePointWanted) {
					return new GeoElement[] { kernel
							.IntersectImplicitpolysSingle(null, p[0], p[1],
									xRW, yRW) };
				} else {
					return kernel.IntersectImplicitpolys(null, p[0], p[1]);
				}
			}
		}
		return null;
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
				if (((GeoElement) points[0]).isGeoElement3D()
						|| ((GeoElement) vectors[0]).isGeoElement3D()) {
					ret[0] = (GeoElement) getKernel().getManager3D().Line3D(
							null, points[0], vectors[0]);
				} else {
					ret[0] = kernel.Line(null, (GeoPoint2) points[0],
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
					ret[0] = getKernel().Line(null, (GeoPoint2) points[0],
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
				GeoPoint2[] points = getSelectedPoints();
				GeoLine[] lines = getSelectedLines();
				// create new parabola
				GeoElement[] ret = { null };
				ret[0] = kernel.Parabola(null, points[0], lines[0]);
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
				} else {
					ret[0] = kernel.OrthogonalLine(null, (GeoPoint2) points[0],
							(GeoVector) vectors[0]);
				}
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
		if (((GeoElement) point).isGeoElement3D()
				|| ((GeoElement) line).isGeoElement3D()) {
			return new GeoElement[] { (GeoElement) getKernel().getManager3D()
					.OrthogonalLine3D(null, point, line,
							((AbstractEuclidianView) view).getDirection()) };
		} else {
			return orthogonal2D(point, line);
		}
	}

	protected GeoElement[] orthogonal2D(GeoPointND point, GeoLineND line) {
		return new GeoElement[] { getKernel().OrthogonalLine(null,
				(GeoPoint2) point, (GeoLine) line) };
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
			if (((GeoElement) points[0]).isGeoElement3D()
					|| ((GeoElement) points[1]).isGeoElement3D()) {
				ret[0] = (GeoElement) kernel.getManager3D().Midpoint(null,
						points[0], points[1]);
			} else {
				ret[0] = kernel.Midpoint(null, (GeoPoint2) points[0],
						(GeoPoint2) points[1]);
			}
			return ret;
		} else if (selSegments() == 1) {
			// fetch the selected segment
			GeoSegmentND[] segments = getSelectedSegmentsND();
			if (((GeoElement) segments[0]).isGeoElement3D()) {
				ret[0] = (GeoElement) kernel.getManager3D().Midpoint(null,
						segments[0]);
			} else {
				ret[0] = kernel.Midpoint(null, (GeoSegment) segments[0]);
			}
			return ret;
		} else if (selConics() == 1) {
			// fetch the selected segment
			GeoConic[] conics = getSelectedConics();
			ret[0] = kernel.Center(null, conics[0]);
			return ret;
		}
		return null;
	}

	protected final boolean functionInspector(Hits hits) {
		if (hits.isEmpty()) {
			return false;
		}
	
		boolean hitFunction = (addSelectedFunction(hits, 1, false) != 0);
	
		if (selFunctions() == 1) {
			GeoFunction[] functions = getSelectedFunctions();
	
			app.getGuiManager().getDialogManager()
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
			GeoPoint2[] points = getSelectedPoints();
			ret[0] = kernel.LineBisector(null, points[0], points[1]);
			return ret;
		} else if (selSegments() == 1) {
			// fetch the selected segment
			GeoSegment[] segments = getSelectedSegments();
			ret[0] = kernel.LineBisector(null, segments[0]);
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
			GeoPoint2[] points = getSelectedPoints();
			GeoElement[] ret = { null };
			ret[0] = kernel.AngularBisector(null, points[0], points[1],
					points[2]);
			return ret;
		} else if (selLines() == 2) {
			// fetch the two lines
			GeoLine[] lines = getSelectedLines();
			return kernel.AngularBisector(null, lines[0], lines[1]);
		}
		return null;
	}

	protected final GeoElement[] threePoints(Hits hits, int mode) {
	
		if (hits.isEmpty()) {
			return null;
		}
	
		// points needed
		addSelectedPoint(hits, 3, false);
		if (selPoints() == 3) {
			return switchModeForThreePoints();
		}
		return null;
	}

	protected GeoElement[] switchModeForThreePoints() {
		// fetch the three selected points
		GeoPointND[] points = getSelectedPointsND();
		GeoElement[] ret = { null };
		switch (mode) {
		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
			if (((GeoElement) points[0]).isGeoElement3D()
					|| ((GeoElement) points[1]).isGeoElement3D()
					|| ((GeoElement) points[2]).isGeoElement3D()) {
				ret[0] = kernel.getManager3D().Circle3D(null, points[0],
						points[1], points[2]);
			} else {
				ret[0] = kernel.Circle(null, (GeoPoint2) points[0],
						(GeoPoint2) points[1], (GeoPoint2) points[2]);
			}
			break;
	
		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
			ret[0] = kernel.Ellipse(null, (GeoPoint2) points[0],
					(GeoPoint2) points[1], (GeoPoint2) points[2]);
			break;
	
		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
			ret[0] = kernel.Hyperbola(null, (GeoPoint2) points[0],
					(GeoPoint2) points[1], (GeoPoint2) points[2]);
			break;
	
		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
			ret[0] = kernel.CircumcircleArc(null, (GeoPoint2) points[0],
					(GeoPoint2) points[1], (GeoPoint2) points[2]);
			break;
	
		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			ret[0] = kernel.CircumcircleSector(null, (GeoPoint2) points[0],
					(GeoPoint2) points[1], (GeoPoint2) points[2]);
			break;
	
		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
			ret[0] = kernel.CircleArc(null, (GeoPoint2) points[0],
					(GeoPoint2) points[1], (GeoPoint2) points[2]);
			break;
	
		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
			ret[0] = kernel.CircleSector(null, (GeoPoint2) points[0],
					(GeoPoint2) points[1], (GeoPoint2) points[2]);
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
			GeoPoint2[] points = getSelectedPoints();
			GeoLocus locus;
			if (points[0].getPath() == null) {
				locus = kernel.Locus(null, points[0], points[1]);
			} else {
				locus = kernel.Locus(null, points[1], points[0]);
			}
			GeoElement[] ret = { null };
			ret[0] = locus;
			return ret;
		} else if ((selPoints() == 1) && (selNumbers() == 1)) {
			GeoPoint2[] points = getSelectedPoints();
			GeoNumeric[] numbers = getSelectedNumbers();
			GeoLocus locus = kernel.Locus(null, points[0], numbers[0]);
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
			GeoPoint2[] points = getSelectedPoints();
			GeoElement[] ret = { null };
			ret[0] = kernel.Conic(null, points);
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
	
			slope = kernel.Slope(label, line);
	
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
				GeoPoint2[] points = getSelectedPoints();
				// create new tangents
				return kernel.Tangent(null, points[0], conics[0]);
			} else if (selLines() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoLine[] lines = getSelectedLines();
				// create new line
				return kernel.Tangent(null, lines[0], conics[0]);
			}
		} else if (selConics() == 2) {
			GeoConic[] conics = getSelectedConics();
			// create new tangents
			return kernel.CommonTangents(null, conics[0], conics[1]);
		} else if (selFunctions() == 1) {
			if (selPoints() == 1) {
				GeoFunction[] functions = getSelectedFunctions();
				GeoPoint2[] points = getSelectedPoints();
				// create new tangents
				GeoElement[] ret = { null };
				ret[0] = kernel.Tangent(null, points[0], functions[0]);
				return ret;
			}
		} else if (selCurves() == 1) {
			if (selPoints() == 1) {
				GeoCurveCartesian[] curves = getSelectedCurves();
				GeoPoint2[] points = getSelectedPoints();
				// create new tangents
				GeoElement[] ret = { null };
				ret[0] = kernel.Tangent(null, points[0], curves[0]);
				return ret;
			}
		} else if (selImplicitpoly() == 1) {
			if (selPoints() == 1) {
				GeoImplicitPoly implicitPoly = getSelectedImplicitpoly()[0];
				GeoPoint2[] points = getSelectedPoints();
				// create new tangents
				return kernel.Tangent(null, points[0], implicitPoly);
			} else if (selLines() == 1) {
				GeoImplicitPoly implicitPoly = getSelectedImplicitpoly()[0];
				GeoLine[] lines = getSelectedLines();
				// create new line
				return kernel.Tangent(null, lines[0], implicitPoly);
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
				GeoPoint2[] points = getSelectedPoints();
				// create new tangents
				ret[0] = kernel.PolarLine(null, points[0], conics[0]);
				return ret;
			} else if (selLines() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoLine[] lines = getSelectedLines();
				// create new line
				ret[0] = kernel.DiameterLine(null, lines[0], conics[0]);
				return ret;
			} else if (selVectors() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoVector[] vecs = getSelectedVectors();
				// create new line
				ret[0] = kernel.DiameterLine(null, vecs[0], conics[0]);
				return ret;
			}
		}
		return null;
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
		if (movedGeoElement == null) {
			movedGeoElement = geo;
			Hits oldhits = new Hits();
			oldhits.addAll(app.getSelectedGeos());
			for (int i = oldhits.size() - 1; i >= 0; i--) {
				GeoElement oldgeo = oldhits.get(i);
				//if (!(movedGeoElement.getClass().isInstance(oldgeo))) {
				if (!(Test.getSpecificTest(movedGeoElement).check(oldgeo))) {
					oldhits.remove(i);
				}
			}
			if (oldhits.size() > 0) {
				// there were appropriate selected elements
				// apply visual style for them
				// standard case: copy visual properties
				for (int i = 0; i < oldhits.size(); i++) {
					GeoElement oldgeo = oldhits.get(i);
					oldgeo.setAdvancedVisualStyle(movedGeoElement);
					oldgeo.updateRepaint();
				}
				clearSelections();
				return true;
			} else {
				// there were no appropriate selected elements
				// set movedGeoElement
				app.addSelectedGeo(geo);
			}
		} else {
			if (geo == movedGeoElement) {
				// deselect
				app.removeSelectedGeo(geo);
				movedGeoElement = null;
				if (toggleModeChangedKernel) {
					app.storeUndoInfo();
				}
				toggleModeChangedKernel = false;
			} else {
				// standard case: copy visual properties
				geo.setAdvancedVisualStyle(movedGeoElement);
				geo.updateRepaint();
				return true;
			}
		}
		return false;
	}

	public Point getMouseLoc() {
		return mouseLoc;
	}

	public void textfieldHasFocus(boolean hasFocus) {
		textfieldHasFocus = hasFocus;
	}
	
	protected abstract void initToolTipManager();

	protected void initShowMouseCoords() {
		view.setShowMouseCoords((mode == EuclidianConstants.MODE_POINT)
				|| (mode == EuclidianConstants.MODE_MOVE));
	}

	protected void wrapMouseEntered(AbstractEvent event) {
		if (textfieldHasFocus) {
			return;
		}
	
		initToolTipManager();
		initShowMouseCoords();
		view.mouseEntered();
	}

	protected boolean move(Hits hits) {
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
			app.getGuiManager()
					.getDialogManager()
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
	
			app.getGuiManager()
					.getDialogManager()
					.showNumberInputDialogAngleFixed(
							app.getMenu(getKernel().getModeText(mode)),
							getSelectedSegments(), getSelectedPoints(), selGeos);
	
			return null;
	
		}
		return null;
	}
	
	protected abstract GeoElement[] createCircle2ForPoints3D(GeoPointND p0, GeoPointND p1);

	protected GeoElement[] createCircle2(GeoPointND p0, GeoPointND p1) {
		if (((GeoElement) p0).isGeoElement3D()
				|| ((GeoElement) p1).isGeoElement3D()) {
			return createCircle2ForPoints3D(p0, p1);
		} else {
			return new GeoElement[] { kernel.Circle(null, (GeoPoint2) p0,
					(GeoPoint2) p1) };
		}
	}

	protected GeoElement[] switchModeForCircleOrSphere2(int mode) {
		GeoPointND[] points = getSelectedPointsND();
		if (mode == EuclidianConstants.MODE_SEMICIRCLE) {
			return new GeoElement[] { kernel.Semicircle(null,
					(GeoPoint2) points[0], (GeoPoint2) points[1]) };
		} else {
			return createCircle2(points[0], points[1]);
		}
	
	}

	protected final GeoElement[] circleOrSphere2(Hits hits, int mode) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the three selected points
			return switchModeForCircleOrSphere2(mode);
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

	protected final boolean text(Hits hits, int mode, boolean altDown) {
		GeoPointND loc = null; // location
	
		if (hits.isEmpty()) {
			if (selectionPreview) {
				return false;
			} else {
				// create new Point
				loc = new GeoPoint2(kernel.getConstruction());
				loc.setCoords(xRW, yRW, 1.0);
			}
		} else {
			// points needed
			addSelectedPoint(hits, 1, false);
			if (selPoints() == 1) {
				// fetch the selected point
				GeoPointND[] points = getSelectedPointsND();
				loc = points[0];
			}
		}
	
		// got location
		if (loc != null) {
			app.getGuiManager().getDialogManager().showTextCreationDialog(loc);
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
			app.getGuiManager().getDialogManager()
					.showSliderCreationDialog(mouseLoc.x, mouseLoc.y);
		}
		return false;
	}

	protected final boolean image(Hits hits, int mode, boolean altDown) {
		GeoPoint2 loc = null; // location
	
		if (hits.isEmpty()) {
			if (selectionPreview) {
				return false;
			} else {
				// create new Point
				loc = new GeoPoint2(kernel.getConstruction());
				loc.setCoords(xRW, yRW, 1.0);
			}
		} else {
			// points needed
			addSelectedPoint(hits, 1, false);
			if (selPoints() == 1) {
				// fetch the selected point
				GeoPoint2[] points = getSelectedPoints();
				loc = points[0];
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
				GeoPoint2[] points = getSelectedPoints();
				return kernel.Mirror(null, polys[0], points[0]);
			} else if (selGeos() > 0) {
				// mirror all selected geos
				GeoElement[] geos = getSelectedGeos();
				GeoPoint2 point = getSelectedPoints()[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				for (int i = 0; i < geos.length; i++) {
					if (geos[i] != point) {
						if (geos[i] instanceof Transformable) {
							ret.addAll(Arrays.asList(kernel.Mirror(null,
									geos[i], point)));
						} else if (geos[i].isGeoPolygon()) {
							ret.addAll(Arrays.asList(kernel.Mirror(null,
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
				return kernel.Mirror(null, polys[0], lines[0]);
			} else if (selGeos() > 0) {
				// mirror all selected geos
				GeoElement[] geos = getSelectedGeos();
				GeoLine line = getSelectedLines()[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				for (int i = 0; i < geos.length; i++) {
					if (geos[i] != line) {
						if (geos[i] instanceof Transformable) {
							ret.addAll(Arrays.asList(kernel.Mirror(null,
									geos[i], line)));
						} else if (geos[i].isGeoPolygon()) {
							ret.addAll(Arrays.asList(kernel.Mirror(null,
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
				return kernel.Mirror(null, polys[0], lines[0]);
			} else if (selGeos() > 0) {
				// mirror all selected geos
				GeoElement[] geos = getSelectedGeos();
				GeoConic line = getSelectedCircles()[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				for (int i = 0; i < geos.length; i++) {
					if (geos[i] != line) {
						if (geos[i] instanceof Transformable) {
							ret.addAll(Arrays.asList(kernel.Mirror(null,
									geos[i], line)));
						} else if (geos[i].isGeoPolygon()) {
							ret.addAll(Arrays.asList(kernel.Mirror(null,
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
	
	public abstract boolean processMode(Hits hits, AbstractEvent event);

	public final boolean refreshHighlighting(Hits hits) {
		boolean repaintNeeded = false;
	
		// clear old highlighting
		if (highlightedGeos.size() > 0) {
			setHighlightedGeos(false);
			repaintNeeded = true;
		}
		// find new objects to highlight
		highlightedGeos.clear();
		selectionPreview = true; // only preview selection, see also
		// mouseReleased()
		processMode(hits, null); // build highlightedGeos List
	
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

	public void clearSelections() {
	
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
	
		app.clearSelectedGeos();
	
		// if we clear selection and highlighting,
		// we may want to clear justCreatedGeos also
		clearJustCreatedGeos();
	
		// clear highlighting
		refreshHighlighting(null);
	}

	protected final boolean attach(GeoPoint2 point, Path path) {
	
		try {
			Construction cons = kernel.getConstruction();
			boolean oldLabelCreationFlag = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			GeoPoint2 newPoint = kernel.Point(null, path,
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

	protected final boolean attach(GeoPoint2 point, Region region) {
	
		try {
			Construction cons = kernel.getConstruction();
			boolean oldLabelCreationFlag = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			GeoPoint2 newPoint = kernel.PointIn(null, region,
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

	protected final boolean attachDetach(Hits hits, AbstractEvent event) {
		if (hits.isEmpty()) {
			return false;
		}
	
		addSelectedRegion(hits, 1, false);
	
		addSelectedPath(hits, 1, false);
	
		addSelectedPoint(hits, 1, false);
	
		if (selectedPoints.size() == 1) {
	
			GeoPoint2 p = (GeoPoint2) selectedPoints.get(0);
	
			if (p.isPointOnPath() || p.isPointInRegion()) {
	
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
					GeoPoint2 newPoint = new GeoPoint2(
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
		}
	
		if (selPoints() == 1) {
			if ((selPaths() == 1) && !isAltDown()) { // press alt to force region
													// (ie inside) not path
													// (edge)
				Path paths[] = getSelectedPaths();
				GeoPoint2[] points = getSelectedPoints();
	
				// Application.debug("path: "+paths[0]+"\npoint: "+points[0]);
	
				if (((GeoElement) paths[0]).isChildOf(points[0])) {
					return false;
				}
	
				if (((GeoElement) paths[0]).isGeoPolygon()
						|| (((GeoElement) paths[0]).isGeoConic() && (((GeoConicND) paths[0])
								.getLastHitType() == GeoConicND.HIT_TYPE_ON_FILLING))) {
					return attach(points[0], (Region) paths[0]);
				}
	
				return attach(points[0], paths[0]);
	
			} else if (selRegions() == 1) {
				Region regions[] = getSelectedRegions();
				GeoPoint2[] points = getSelectedPoints();
	
				if (!((GeoElement) regions[0]).isChildOf(points[0])) {
					return attach(points[0], regions[0]);
				}
	
			}
		}
		return false;
	}

	protected GeoElement[] translate(GeoElement geo, GeoVectorND vec) {
		return kernel.Translate(null, geo, (GeoVector) vec);
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
	
			app.getGuiManager()
					.getDialogManager()
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
	
			app.getGuiManager()
					.getDialogManager()
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
		if (selLists() > 0) {
			list = getSelectedLists()[0];
			if (list != null) {
				ret[0] = kernel.FitLineY(null, list);
				return ret;
			}
		} else {
			addSelectedPoint(hits, 999, true);
	
			if (selPoints() > 1) {
				GeoPoint2[] points = getSelectedPoints();
				list = geogebra.common.kernel.commands.CommandProcessor
						.wrapInList(kernel, points, points.length,
								GeoClass.POINT);
				if (list != null) {
					ret[0] = kernel.FitLineY(null, list);
					return ret;
				}
			}
		}
		return null;
	}

	protected final GeoElement[] createList(Hits hits) {
		GeoList list;
		GeoElement[] ret = { null };
	
		if (!selectionPreview && (hits.size() > 1)) {
			list = kernel.List(null, hits, false);
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
					GeoPoint2 gp = (GeoPoint2) spl.get(i);
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
	
		case EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC:
			if (!view.isGridOrAxesShown()) {
				break;
			}
	
		case EuclidianStyleConstants.POINT_CAPTURING_ON:
			pointCapturingPercentage = 0.125;
	
		case EuclidianStyleConstants.POINT_CAPTURING_ON_GRID:
	
			xRW += transformCoordsOffset[0];
			yRW += transformCoordsOffset[1];
	
			switch (view.getGridType()) {
			case AbstractEuclidianView.GRID_ISOMETRIC:
	
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
						xRW = (x * root3) - transformCoordsOffset[0];
						yRW = y - transformCoordsOffset[1];
					} else {
						xRW -= transformCoordsOffset[0];
						yRW -= transformCoordsOffset[1];
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
								- transformCoordsOffset[0];
						yRW = (y + (isoGrid / 2)) - transformCoordsOffset[1];
					} else {
						xRW -= transformCoordsOffset[0];
						yRW -= transformCoordsOffset[1];
					}
	
				}
				break;
	
			case AbstractEuclidianView.GRID_CARTESIAN:
	
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
					xRW = x - transformCoordsOffset[0];
					yRW = y - transformCoordsOffset[1];
				} else {
					xRW -= transformCoordsOffset[0];
					yRW -= transformCoordsOffset[1];
				}
				break;
	
			case AbstractEuclidianView.GRID_POLAR:
	
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
					xRW = x1 - transformCoordsOffset[0];
					yRW = y1 - transformCoordsOffset[1];
				} else {
					xRW -= transformCoordsOffset[0];
					yRW -= transformCoordsOffset[1];
				}
				break;
			}
	
		default:
		}
	}

	protected GeoAngle createAngle(GeoPointND A, GeoPointND B, GeoPointND C) {
		return kernel.Angle(null, (GeoPoint2) A, (GeoPoint2) B, (GeoPoint2) C);
	}

	protected GeoAngle createLineAngle(GeoLine[] lines) {
		GeoAngle angle = null;
	
		// did we get two segments?
		if ((lines[0] instanceof GeoSegment)
				&& (lines[1] instanceof GeoSegment)) {
			// check if the segments have one point in common
			GeoSegment a = (GeoSegment) lines[0];
			GeoSegment b = (GeoSegment) lines[1];
			// get endpoints
			GeoPoint2 a1 = a.getStartPoint();
			GeoPoint2 a2 = a.getEndPoint();
			GeoPoint2 b1 = b.getStartPoint();
			GeoPoint2 b2 = b.getEndPoint();
	
			if (a1 == b1) {
				angle = kernel.Angle(null, a2, a1, b2);
			} else if (a1 == b2) {
				angle = kernel.Angle(null, a2, a1, b1);
			} else if (a2 == b1) {
				angle = kernel.Angle(null, a1, a2, b2);
			} else if (a2 == b2) {
				angle = kernel.Angle(null, a1, a2, b1);
			}
		}
	
		if (angle == null) {
			angle = kernel.Angle(null, lines[0], lines[1]);
		}
	
		return angle;
	}

	protected String removeUnderscores(String label) {
		// remove all indices
		return label.replaceAll("_", "");
	}

	/**
	 * Creates a text that shows a number value of geo at the current mouse
	 * position.
	 */
	protected GeoText createDynamicText(String descText, GeoElement value, Point loc) {
		// create text that shows length
		try {
			// create dynamic text
			String dynText = "\"" + descText + " = \" + " + value.getLabel();
	
			GeoText text = kernel.getAlgebraProcessor().evaluateToText(dynText,
					true, true);
			text.setAbsoluteScreenLocActive(true);
			text.setAbsoluteScreenLoc(loc.x, loc.y);
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
	protected GeoText createDistanceText(GeoElement geoA, GeoElement geoB, GeoPoint2 startPoint,
			GeoNumeric length) {
				// create text that shows length
				try {
					String strText = "";
					boolean useLabels = geoA.isLabelSet() && geoB.isLabelSet();
					if (useLabels) {
						length.setLabel(removeUnderscores(app.toLowerCase(app.getCommand("Distance"))
								//.toLowerCase(Locale.US)
								+ geoA.getLabel()
								+ geoB.getLabel()));
						// strText = "\"\\overline{\" + Name["+ geoA.getLabel()
						// + "] + Name["+ geoB.getLabel() + "] + \"} \\, = \\, \" + "
						// + length.getLabel();
			
						// DistanceAB="\\overline{" + %0 + %1 + "} \\, = \\, " + %2
						// or
						// DistanceAB=%0+%1+" \\, = \\, "+%2
						strText = app.getPlain("DistanceAB.LaTeX",
								"Name[" + geoA.getLabel() + "]",
								"Name[" + geoB.getLabel() + "]", length.getLabel());
						// Application.debug(strText);
						geoA.setLabelVisible(true);
						geoB.setLabelVisible(true);
						geoA.updateRepaint();
						geoB.updateRepaint();
					} else {
						length.setLabel(removeUnderscores(app.toLowerCase(app.getCommand("Distance"))));
								//.toLowerCase(Locale.US)));
						strText = "\"\"" + length.getLabel();
					}
			
					// create dynamic text
					GeoText text = kernel.getAlgebraProcessor().evaluateToText(strText,
							true, true);
					if (useLabels) {
						text.setLabel(removeUnderscores(app.getPlain("Text")
								+ geoA.getLabel() + geoB.getLabel()));
						text.setLaTeX(useLabels, true);
					}
			
					text.setStartPoint(startPoint);
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
		
		Point mouseCoords = event.getPoint();
	
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
			GeoNumeric area = kernel.Area(null, conic);
	
			// text
			GeoText text = createDynamicText(app.getCommand("Area"), area,
					mouseCoords);
			if (conic.isLabelSet()) {
				area.setLabel(removeUnderscores(app.toLowerCase(app.getCommand("Area"))
						+ conic.getLabel()));
				text.setLabel(removeUnderscores(app.getPlain("Text")
						+ conic.getLabel()));
			}
			GeoElement[] ret = { text };
			return ret;
		}
	
		// area of polygon
		else if (selPolygons() == 1) {
			GeoPolygon[] poly = getSelectedPolygons();
	
			// dynamic text with polygon's area
			GeoText text = createDynamicText(
					descriptionPoints(app.getCommand("Area"), poly[0]),
					poly[0], mouseLoc);
			if (poly[0].isLabelSet()) {
				text.setLabel(removeUnderscores(app.getPlain("Text")
						+ poly[0].getLabel()));
			}
			GeoElement[] ret = { text };
			return ret;
		}
	
		return null;
	}

	protected String descriptionPoints(String prefix, GeoPolygon poly) {
		// build description text including point labels
		String descText = prefix;
	
		// use points for polygon with static points (i.e. no list of points)
		GeoPoint2[] points = null;
		if (poly.getParentAlgorithm() instanceof AlgoPolygon) {
			points = ((AlgoPolygon) poly.getParentAlgorithm()).getPoints();
		}
	
		if (points != null) {
			descText = descText + " \"";
			boolean allLabelsSet = true;
			for (int i = 0; i < points.length; i++) {
				if (points[i].isLabelSet()) {
					descText = descText + " + Name[" + points[i].getLabel()
							+ "]";
				} else {
					allLabelsSet = false;
					i = points.length;
				}
			}
	
			if (allLabelsSet) {
				descText = descText + " + \"";
				for (int i = 0; i < points.length; i++) {
					points[i].setLabelVisible(true);
					points[i].updateRepaint();
				}
			} else {
				descText = app.getCommand("Area");
			}
		}
		return descText;
	}

	protected boolean regularPolygon(Hits hits) {
		if (hits.isEmpty()) {
			return false;
		}
	
		// need two points
		addSelectedPoint(hits, 2, false);
	
		if (selPoints() == 2) {
			GeoPoint2[] points = getSelectedPoints();
			app.getGuiManager()
					.getDialogManager()
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
			angle = kernel.Angle(null, vecs[0], vecs[1]);
		} else if (selLines() == 2) {
			GeoLine[] lines = getSelectedLines();
			angle = createLineAngle(lines);
		} else if (polyFound && (selGeos() == 1)) {
			angles = kernel.Angles(null, (GeoPolygon) getSelectedGeos()[0]);
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
		
		Point mouseCoords = event.getPoint();
	
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
			GeoPoint2[] points = getSelectedPoints();
			GeoNumeric length = kernel.Distance(null, (GeoPointND) points[0],
					(GeoPointND) points[1]);
	
			// set startpoint of text to midpoint of two points
			GeoPoint2 midPoint = kernel.Midpoint(points[0], points[1]);
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
			ret[0] = kernel.Distance(null, lines[0], lines[1]);
			return ret; // return this not null because the kernel has changed
		}
	
		// POINT AND LINE
		else if ((selPoints() == 1) && (selLines() == 1)) {
			GeoPoint2[] points = getSelectedPoints();
			GeoLine[] lines = getSelectedLines();
			GeoNumeric length = kernel.Distance(null, points[0], lines[0]);
	
			// set startpoint of text to midpoint between point and line
			GeoPoint2 midPoint = kernel.Midpoint(points[0],
					kernel.ClosestPoint(points[0], lines[0]));
			GeoElement[] ret = { null };
			ret[0] = createDistanceText(points[0], lines[0], midPoint, length);
			return ret;
		}
	
		// circumference of CONIC
		else if (selConics() == 1) {
			GeoConic conic = getSelectedConics()[0];
			if (conic.isGeoConicPart()) {
				// length of arc
				GeoConicPart conicPart = (GeoConicPart) conic;
				if (conicPart.getConicPartType() == GeoConicPart.CONIC_PART_ARC) {
					// arc length
					if (conic.isLabelVisible()) {
						conic.setLabelMode(GeoElement.LABEL_NAME_VALUE);
					} else {
						conic.setLabelMode(GeoElement.LABEL_VALUE);
					}
					conic.updateRepaint();
					GeoElement[] ret = { conic };
					return ret; // return this not null because the kernel has
								// changed
				}
			}
	
			// standard case: conic
			GeoNumeric circumFerence = kernel.Circumference(null, conic);
	
			// text
			GeoText text = createDynamicText(app.getCommand("Circumference"),
					circumFerence, mouseCoords);
			if (conic.isLabelSet()) {
				circumFerence.setLabel(removeUnderscores(app.toLowerCase(app.getCommand(
						"Circumference"))
						+ conic.getLabel()));
				text.setLabel(removeUnderscores(app.getPlain("Text")
						+ conic.getLabel()));
			}
			GeoElement[] ret = { text };
			return ret;
		}
	
		// perimeter of CONIC
		else if (selPolygons() == 1) {
			GeoPolygon[] poly = getSelectedPolygons();
			GeoNumeric perimeter = kernel.Perimeter(null, poly[0]);
	
			// text
			GeoText text = createDynamicText(
					descriptionPoints(app.getCommand("Perimeter"), poly[0]),
					perimeter, mouseCoords);
	
			if (poly[0].isLabelSet()) {
				perimeter.setLabel(removeUnderscores(app.toLowerCase(app.getCommand("Perimeter"))
						+ poly[0].getLabel()));
				text.setLabel(removeUnderscores(app.getPlain("Text")
						+ poly[0].getLabel()));
			}
			GeoElement[] ret = { text };
			return ret;
		}
	
		return null;
	}
}
