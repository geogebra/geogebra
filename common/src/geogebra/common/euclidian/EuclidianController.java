/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package geogebra.common.euclidian;

import geogebra.common.awt.GPoint;
import geogebra.common.awt.GPoint2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.draw.DrawConic;
import geogebra.common.euclidian.draw.DrawConicPart;
import geogebra.common.euclidian.draw.DrawSlider;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.euclidian.event.PointerEventType;
import geogebra.common.euclidian.modes.ModeDelete;
import geogebra.common.gui.view.data.PlotPanelEuclidianViewInterface;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoDispatcher;
import geogebra.common.kernel.algos.AlgoDynamicCoordinatesInterface;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoFunctionFreehand;
import geogebra.common.kernel.algos.AlgoRadius;
import geogebra.common.kernel.algos.AlgoTranslate;
import geogebra.common.kernel.algos.AlgoVector;
import geogebra.common.kernel.algos.AlgoVectorPoint;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.PolyFunction;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.Furniture;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoAxis;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElement.HitType;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumberValue;
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
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.GeoAxisND;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoElementND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoQuadricND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.kernel.statistics.AlgoFitLineY;
import geogebra.common.main.App;
import geogebra.common.main.DialogManager;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.common.main.Localization;
import geogebra.common.main.SelectionManager;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.Operation;
import geogebra.common.util.AsyncOperation;
import geogebra.common.util.MyMath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

@SuppressWarnings("javadoc")
public abstract class EuclidianController {

	/**
	 * max value for alpha to consider an object transparent (i.e. we can see
	 * through it)
	 */
	public static final float MAX_TRANSPARENT_ALPHA_VALUE = 0.8f;

	/**
	 * max value for alpha to consider an object visible
	 */
	public static final float MIN_VISIBLE_ALPHA_VALUE = 0.05f;

	protected static final int POLYGON_NORMAL = 0;

	protected static final int POLYGON_RIGID = 1;

	protected static final int POLYGON_VECTOR = 2;

	protected static final double MOUSE_DRAG_MAX_DIST_SQUARE = 36;

	protected static final int MAX_CONTINUITY_STEPS = 4;

	protected static void removeAxes(ArrayList<GeoElement> geos) {

		for (int i = geos.size() - 1; i >= 0; i--) {
			GeoElement geo = geos.get(i);
			if (geo instanceof GeoAxis) {
				geos.remove(i);
			}
		}
	}

	private ModeDelete deleteMode;

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

	public GeoPointND movedGeoPoint;

	public boolean movedGeoPointDragged = false;

	protected GeoLineND movedGeoLine;

	protected GeoConic movedGeoConic;

	protected GeoImplicitPoly movedGeoImplicitPoly;

	protected GeoVectorND movedGeoVector;

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

	protected MyDouble tempNum;

	protected double rotationLastAngle;

	protected ArrayList<GeoElement> translateableGeos;

	protected Coords translationVec;

	protected Hits tempArrayList = new Hits();

	protected Hits tempArrayList2 = new Hits();

	protected Hits tempArrayList3 = new Hits();

	protected final ArrayList<GeoPointND> selectedPoints = new ArrayList<GeoPointND>();

	protected final ArrayList<GeoNumeric> selectedNumbers = new ArrayList<GeoNumeric>();

	protected final ArrayList<GeoNumberValue> selectedNumberValues = new ArrayList<GeoNumberValue>();

	protected final ArrayList<GeoLineND> selectedLines = new ArrayList<GeoLineND>();

	protected final ArrayList<GeoDirectionND> selectedDirections = new ArrayList<GeoDirectionND>();

	protected final ArrayList<GeoSegmentND> selectedSegments = new ArrayList<GeoSegmentND>();

	protected final ArrayList<Region> selectedRegions = new ArrayList<Region>();

	protected final ArrayList<Path> selectedPaths = new ArrayList<Path>();

	protected final ArrayList<GeoConicND> selectedConicsND = new ArrayList<GeoConicND>();

	protected final ArrayList<GeoImplicitPoly> selectedImplicitpoly = new ArrayList<GeoImplicitPoly>();

	protected final ArrayList<GeoFunction> selectedFunctions = new ArrayList<GeoFunction>();

	protected final ArrayList<GeoCurveCartesian> selectedCurves = new ArrayList<GeoCurveCartesian>();

	protected final ArrayList<GeoVectorND> selectedVectors = new ArrayList<GeoVectorND>();

	protected final ArrayList<GeoPolygon> selectedPolygons = new ArrayList<GeoPolygon>();

	protected final ArrayList<GeoPolyLine> selectedPolyLines = new ArrayList<GeoPolyLine>();

	protected final ArrayList<GeoElement> selectedGeos = new ArrayList<GeoElement>();

	protected final ArrayList<GeoList> selectedLists = new ArrayList<GeoList>();
	protected Hits highlightedGeos = new Hits();

	protected ArrayList<GeoElement> justCreatedGeos = new ArrayList<GeoElement>();

	protected boolean selectionPreview = false;

	public boolean hideIntersection = false;

	public GeoElement resultedGeo;

	protected boolean temporaryMode = false;

	protected boolean dontClearSelection = false;

	protected boolean draggingOccured = false;

	protected boolean draggingOccuredBeforeRelease = false;

	public boolean draggingBeyondThreshold = false;

	protected GeoPointND pointCreated = null;

	protected boolean moveModeSelectionHandled;

	protected boolean highlightJustCreatedGeos = true;

	protected ArrayList<GeoElement> pastePreviewSelected = null;

	protected ArrayList<GeoElement> pastePreviewSelectedAndDependent;

	protected int mode;

	protected int oldMode;

	protected int moveMode = MOVE_NONE;

	protected Macro macro;

	protected Test[] macroInput;

	protected int defaultInitialDelay;

	protected boolean toggleModeChangedKernel = false;

	protected boolean altDown = false;

	protected GeoElement rotGeoElement;

	protected GeoPoint rotationCenter;

	protected int polygonMode = POLYGON_NORMAL;

	protected double[] transformCoordsOffset = new double[2];

	protected boolean allowSelectionRectangleForTranslateByVector = true;

	protected int previousPointCapturing;

	protected ArrayList<GeoPointND> persistentStickyPointList = new ArrayList<GeoPointND>();

	protected final App app;

	protected final SelectionManager selection;

	protected final Localization l10n;

	public Kernel kernel;

	protected GPoint startLoc;

	public GPoint mouseLoc;

	protected GPoint lastMouseLoc;

	protected GPoint oldLoc = new GPoint();

	private GPoint2D.Double startPoint = new GPoint2D.Double();

	protected GPoint2D.Double lineEndPoint = null;

	protected GPoint selectionStartPoint = new GPoint();

	protected ArrayList<Double> tempDependentPointX;

	protected ArrayList<Double> tempDependentPointY;

	protected boolean mouseIsOverLabel = false;

	public EuclidianView view;

	protected int collectingRepaints = 0; // if greater than 0, some repaints
											// may be omitted

	protected boolean collectedRepaints = false; // whether to repaint when
													// collectingRepaints set to
													// 0

	private boolean externalHandling;

	private long lastMouseRelease;

	int index;

	protected EuclidianControllerCompanion companion;

	ModeDelete getDeleteMode() {
		if (deleteMode == null && view != null) {
			deleteMode = new ModeDelete(view);
		}
		return deleteMode;
	}

	public EuclidianController(App app) {
		this.app = app;
		this.selection = app.getSelectionManager();
		this.l10n = app.getLocalization();
		createCompanions();
	}
	
	protected void createCompanions(){
		this.companion = newCompanion();
	}

	public EuclidianControllerCompanion getCompanion() {
		return companion;
	}

	protected EuclidianControllerCompanion newCompanion() {
		return new EuclidianControllerCompanion(this);
	}

	/**
	 * Start collecting the minor repaints (view.repaintView's not at the end of
	 * the events) This method may be called more times, but there should be one
	 * stopCollectingMinorRepaints for one startCollectingMinorRepaints in the
	 * same method (and repaint can be done if every level is closed)
	 */
	public void startCollectingMinorRepaints() {

		if (collectingRepaints < 0) // should not happen, just in case
			collectingRepaints = 0;

		if (collectingRepaints == 0)
			collectedRepaints = false;
		collectingRepaints++;
	}

	/**
	 * Stop collecting the minor repaints (view.repaintView's not at the end of
	 * the events)
	 * 
	 * @return: whether the actual method shall repaint anything
	 */
	public void stopCollectingMinorRepaints() {
		collectingRepaints--;
		if (collectingRepaints <= 0 && collectedRepaints) {
			view.repaintView();
			collectingRepaints = 0;
			collectedRepaints = false;
		}
	}

	// ==============================================
	// Pen

	public geogebra.common.euclidian.EuclidianPen pen;

	// ==============================================
	// Delete tool

	// private int deleteToolSize = EuclidianConstants.DEFAULT_ERASER_SIZE;

	protected Hits handleAddSelectedArrayList = new Hits();

	private boolean textfieldHasFocus = false;

	private MyButton pressedButton;

	public static final int MOVE_NONE = 101;
	public static final int MOVE_POINT = 102;
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
	protected static final int MOVE_ATTACH_DETACH = 125;
	protected static final int MOVE_PLANE = 126;

	private static final int ZOOM_RECT_THRESHOLD = 30;
	private static final int DRAG_THRESHOLD = 10;

	// ==============================================
	// Paste preview

	protected void updatePastePreviewPosition() {
		if (translationVec == null) {
			translationVec = new Coords(2);
		}
		translationVec.setX(xRW - getStartPointX());
		translationVec.setY(yRW - getStartPointY());
		setStartPointLocation(xRW, yRW);
		if (tmpCoordsL3 == null) {
			tmpCoordsL3 = new Coords(3);
		}
		tmpCoordsL3.setX(xRW);
		tmpCoordsL3.setY(yRW);
		tmpCoordsL3.setZ(0);
		GeoElement.moveObjects(pastePreviewSelected, translationVec,
				tmpCoordsL3, null, view);
	}

	protected Coords tmpCoordsL3;

	public final void setPastePreviewSelected() {

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
		pastePreviewSelectedAndDependent.addAll(getAppSelectedGeos());

		GeoElement geo;
		boolean firstMoveable = true;
		for (int i = 0; i < getAppSelectedGeos().size(); i++) {
			geo = getAppSelectedGeos().get(i);
			if (geo.isIndependent() && geo.isMoveable()) {
				pastePreviewSelected.add(geo);
				if (firstMoveable) {
					if (geo.isGeoPoint()) {
						if (geo instanceof GeoPoint) {
							setStartPointLocation(((GeoPoint) geo).getInhomX(),
									((GeoPoint) geo).getInhomY());
						} else if (geo.isGeoElement3D()) {
							setStartPointLocation(
									((GeoPointND) geo).getInhomX(),
									((GeoPointND) geo).getInhomY());
						}
						firstMoveable = false;
					} else if (geo.isGeoText()) {
						if (((GeoText) geo).hasAbsoluteLocation()) {
							GeoPointND loc = ((GeoText) geo).getStartPoint();
							setStartPointLocation(loc.getInhomX(),
									loc.getInhomY());
							firstMoveable = false;
						}
					} else if (geo.isGeoNumeric()) {
						if (!((GeoNumeric) geo).isAbsoluteScreenLocActive()) {
							setStartPointLocation(
									((GeoNumeric) geo).getRealWorldLocX(),
									((GeoNumeric) geo).getRealWorldLocY());
							firstMoveable = false;
						} else {
							setStartPointLocation(
									view.toRealWorldCoordX(((GeoNumeric) geo)
											.getAbsoluteScreenLocX()),
									view.toRealWorldCoordY(((GeoNumeric) geo)
											.getAbsoluteScreenLocY()));
							firstMoveable = false;
						}
					} else if (geo.isGeoImage()) {
						if (((GeoImage) geo).hasAbsoluteLocation()) {
							GeoPoint loc = ((GeoImage) geo).getStartPoints()[2];
							if (loc != null) { // top left defined
								// transformCoordsOffset[0]=loc.inhomX-xRW;
								// transformCoordsOffset[1]=loc.inhomY-yRW;
								setStartPointLocation(loc.inhomX, loc.inhomY);
								firstMoveable = false;
							} else {
								loc = ((GeoImage) geo).getStartPoint();
								if (loc != null) { // bottom left defined
													// (default)
									// transformCoordsOffset[0]=loc.inhomX-xRW;
									// transformCoordsOffset[1]=loc.inhomY-yRW;
									setStartPointLocation(loc.inhomX,
											loc.inhomY);
									firstMoveable = false;
								} else {
									loc = ((GeoImage) geo).getStartPoints()[1];
									if (loc != null) { // bottom right defined
										// transformCoordsOffset[0]=loc.inhomX-xRW;
										// transformCoordsOffset[1]=loc.inhomY-yRW;
										setStartPointLocation(loc.inhomX,
												loc.inhomY);
										firstMoveable = false;
									}
								}
							}
						}
					} else if (geo.isGeoBoolean()) {
						// moveMode = MOVE_BOOLEAN;
						setStartPointLocation(
								view.toRealWorldCoordX(((GeoBoolean) geo)
										.getAbsoluteScreenLocX()),
								view.toRealWorldCoordY(((GeoBoolean) geo)
										.getAbsoluteScreenLocY() + 20));
						firstMoveable = false;
					} else if (geo instanceof Furniture) {
						setStartPointLocation(
								view.toRealWorldCoordX(((Furniture) geo)
										.getAbsoluteScreenLocX() - 5),
								view.toRealWorldCoordY(((Furniture) geo)
										.getAbsoluteScreenLocY() + 30));
						firstMoveable = false;
					}
				}
			}
		}
		if (firstMoveable) {
			setStartPointLocation((view.getXmin() + view.getXmax()) / 2,
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
			if (geo.isGeoPoint() && (geo instanceof GeoPoint)
					&& geo.isIndependent()) {
				for (int j = 0; j < persistentStickyPointList.size(); j++) {
					GeoPointND geo2 = persistentStickyPointList.get(j);
					if (Kernel.isEqual(geo2.getInhomX(),
							((GeoPoint) geo).getInhomX())
							&& Kernel.isEqual(geo2.getInhomY(),
									((GeoPoint) geo).getInhomY())) {
						geo.setEuclidianVisible(false);
						String geolabel = geo.getLabelSimple();
						kernel.getAlgebraProcessor().processAlgebraCommand(
								geo.getLabelSimple() + "="
										+ ((GeoElement) geo2).getLabelSimple(),
								false);
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
		case EuclidianConstants.MODE_MOVE:
			deletePastePreviewSelected();
			break;

		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
			// take all selected objects and hide them
			Collection<GeoElement> coll = getAppSelectedGeos();
			Iterator<GeoElement> it = coll.iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				geo.setEuclidianVisible(false);
				geo.updateRepaint();
			}
			break;

		case EuclidianConstants.MODE_PEN:
			// case EuclidianConstants.MODE_PENCIL:
		case EuclidianConstants.MODE_FREEHAND_SHAPE:
			getPen().resetPenOffsets();

			view.setSelectionRectangle(null);
			break;
		}

		if (toggleModeChangedKernel) {
			app.storeUndoInfo();
		}
	}

	protected final void clearSelection(ArrayList<?> selectionList,
			boolean doUpdateSelection) {

		// unselect
		selectionList.clear();
		selectedGeos.clear();
		if (doUpdateSelection) {
			selection.clearSelectedGeos();
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

		return companion.getSingleIntersectionPoint(a, b, true);
	}

	/***************************************************************************
	 * helper functions for selection sets
	 **************************************************************************/
	public final GeoElement[] getSelectedGeos() {
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
		Iterator<GeoSegmentND> it = selectedSegments.iterator();
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

	public GeoPointND createNewPoint2D(String label, boolean forPreviewable,
			Path path, double x, double y, boolean complex, boolean coords2D) {
		checkZooming(forPreviewable);

		return getAlgoDispatcher().Point(label, path, x, y, !forPreviewable,
				complex, coords2D);
	}

	final protected GeoPointND createNewPoint2D(String label,
			boolean forPreviewable, Region region, double x, double y,
			boolean complex, boolean coords2D) {
		checkZooming(forPreviewable);

		GeoPointND ret = getAlgoDispatcher().PointIn(label, region, x, y,
				!forPreviewable, complex, coords2D);
		return ret;
	}

	final public GeoPointND createNewPoint(String label,
			boolean forPreviewable, Region region, double x, double y,
			double z, boolean complex, boolean coords2D) {

		if (region.toGeoElement().isGeoElement3D()) {
			checkZooming(forPreviewable);

			if (tmpCoordsL4 == null) {
				tmpCoordsL4 = new Coords(4);
			}
			tmpCoordsL4.setX(x);
			tmpCoordsL4.setY(y);
			tmpCoordsL4.setZ(z);
			tmpCoordsL4.setW(1);
			GeoPointND point = kernel.getManager3D().Point3DIn(label, region,
					tmpCoordsL4, !forPreviewable, coords2D);

			return point;
		}
		return createNewPoint2D(label, forPreviewable, region, x, y, complex,
				coords2D);
	}

	private Coords tmpCoordsL4;

	public void setKernel(Kernel kernel) {
		this.kernel = kernel;
	}

	public Kernel getKernel() {
		return kernel;
	}

	public void clearJustCreatedGeos() {
		boolean needsUpdate = justCreatedGeos.size() > 0;
		justCreatedGeos.clear();
		if (needsUpdate) {
			app.updateStyleBars();

			if (app.isUsingFullGui() && app.getGuiManager() != null) {
				app.getGuiManager().updateMenubarSelection();
			}
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

			if (!geo.isFixed()) {
				geo.setHighlighted(highlight);
			}
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
				ArrayList<GeoPointND> ip = cp.getParentAlgorithm()
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

	protected final <T> int addToSelectionList(ArrayList<T> selectionList,
			T geo, int max) {
		if (geo == null) {
			return 0;
		}

		int ret = 0;
		if (selectionList.contains(geo)) { // remove from selection
			selectionList.remove(geo);
			if (!selectionList.equals(selectedGeos)) {
				selectedGeos.remove(geo);
			}
			ret = -1;
		} else { // new element: add to selection
			if (selectionList.size() < max) {
				selectionList.add(geo);
				if (!selectionList.equals(selectedGeos)) {
					selectedGeos.add((GeoElement) geo);
				}
				ret = 1;
			}
		}
		if (ret != 0) {
			selection.toggleSelectedGeo((GeoElement) geo);
		}
		return ret;
	}

	protected final int addToHighlightedList(ArrayList<?> selectionList,
			ArrayList<GeoElement> geos, int max) {

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

	protected GeoElement chooseGeo(ArrayList<GeoElement> geos,
			boolean includeFixed) {
		return chooseGeo(geos, includeFixed, true);
	}

	protected GeoElement chooseGeo(ArrayList<GeoElement> geos,
			boolean includeFixed, boolean includeConstants) {
		if (geos == null) {
			return null;
		}

		if (geos.size() > 1 || !includeConstants) {
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
			 * (app.areChooserPopupsEnabled()) ret =
			 * dialog.showDialog((EuclidianView) view, mouseLoc);
			 * ttm.setEnabled(true);
			 */

			// now just choose geo with highest drawing priority:
			ret = geos.get(0);

			for (int i = 0; i < geos.size(); i++) {
				if (geos.get(i).drawBefore(ret, true)) {
					ret = geos.get(i);
				}
			}
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
	@SuppressWarnings("unchecked")
	protected final <T extends GeoElementND> int addToSelectionList(
			ArrayList<T> selectionList, ArrayList<GeoElement> geos, int max,
			boolean addMoreThanOneAllowed, boolean tryDeselect) {

		if (geos == null) {
			return 0;
			// GeoElement geo;
		}

		// ONLY ONE ELEMENT IN THE EFFECTIVE HITS
		if (tryDeselect && (geos.size() == 1)) {
			// select or deselect it
			return addToSelectionList(selectionList, (T) geos.get(0), max);
		}

		// SEVERAL ELEMENTS
		// here none of the selected geos should be removed

		// we don't want to add repeated elements
		geos.removeAll(selectionList);
		// too many objects -> choose one
		if (!addMoreThanOneAllowed
				|| ((geos.size() + selectionList.size()) > max)) {
			// Application.printStacktrace(geos.toString());
			return addToSelectionList(selectionList,
					(T) chooseGeo(geos, true, true), max);
		}

		// already selected objects -> choose one
		boolean contained = false;
		for (int i = 0; i < geos.size(); i++) {
			if (selectionList.contains(geos.get(i))) {
				contained = true;
			}
		}
		if (contained) {
			return addToSelectionList(selectionList,
					(T) chooseGeo(geos, true, true), max);
		}

		// add all objects to list
		int count = 0;
		for (int i = 0; i < geos.size(); i++) {
			count += addToSelectionList(selectionList, (T) geos.get(i), max);
		}
		return count;
	}

	public final int selGeos() {
		return selectedGeos.size();
	}

	public final int selPoints() {
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
			ArrayList<? extends GeoElementND> list, Test geoClass) {

		if (selectionPreview) {
			return addToHighlightedList(list,
					hits.getHits(geoClass, handleAddSelectedArrayList), max);
		}
		return addToSelectionList(list,
				hits.getHits(geoClass, handleAddSelectedArrayList), max,
				addMore, hits.size() == 1);
	}

	protected int handleAddSelectedRegions(Hits hits, int max, boolean addMore,
			ArrayList<Region> list) {
		if (selectionPreview) {
			return addToHighlightedList(list,
					hits.getRegionHits(handleAddSelectedArrayList), max);
		}
		return addToSelectionList(list,
				hits.getRegionHits(handleAddSelectedArrayList), max, addMore,
				hits.size() == 1);
	}

	public final int addSelectedGeo(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
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

	/**
	 * only used in 3D
	 * 
	 * @param sourcePoint
	 */
	protected void createNewPoint(GeoPointND sourcePoint) {
		// 3D
	}

	/**
	 * only used in 3D
	 * 
	 * @param intersectionPoint
	 */
	protected void createNewPointIntersection(GeoPointND intersectionPoint) {
		// 3D
	}

	protected final GeoElement[] join(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}

		// points needed
		addSelectedPoint(hits, 2, false);
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
			ret[0] = getKernel().getManager3D()
					.Ray3D(null, points[0], points[1]).toGeoElement();
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

	final private GeoElement[] segment() {
		GeoPointND[] points = getSelectedPointsND();
		GeoElement[] ret = companion.segmentAlgo(kernel.getConstruction(),
				points[0], points[1]).getOutput();
		ret[0].setLabel(null);
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
			if (selPolygons() == 1) {
				GeoPolygon[] poly = getSelectedPolygons();

				GeoPoint[] points = (GeoPoint[]) poly[0].getPoints();

				GeoPoint[] pointsCopy = new GeoPoint[points.length];

				// make a free copy of all points
				for (int i = 0; i < points.length; i++) {
					pointsCopy[i] = points[i].copy();
					pointsCopy[i].setLabel(null);
					// points[i] = new GeoPoint(kernel.getConstruction(), null,
					// points[i].inhomX, points[i].inhomY, 1.0);
				}

				checkZooming();

				GeoElement[] ret = kernel.VectorPolygon(null, pointsCopy);

				// offset the copy slightly
				double offset = view.toRealWorldCoordX(view.getWidth()) / 15;

				((GeoPolygon) ret[0]).getPoints()[0].setCoords(
						pointsCopy[0].inhomX + offset, pointsCopy[0].inhomY
								- offset, 1.0);
				((GeoPolygon) ret[0]).getPoints()[0].updateRepaint();

				return ret;

			}
		} else if (polygonMode == POLYGON_RIGID) {
			addSelectedPolygon(hits, 1, false);
			if (selPolygons() == 1) {
				GeoPolygon[] poly = getSelectedPolygons();

				checkZooming();

				// offset the copy slightly
				double offset = view.toRealWorldCoordX(view.getWidth()) / 15;

				GeoElement[] ret = kernel
						.RigidPolygon(poly[0], offset, -offset);

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
				this.kernel.addingPolygon();
				GeoElement[] elms = polygon();
				// return polygon();
				this.kernel.notifyPolygonAdded();
				return elms;
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
			GeoElement[] ret0 = kernel
					.RigidPolygon(null, getSelectedPointsND());
			if (ret0 != null) {
				ret[0] = ret0[0];
			}
			return ret;
		} else if (polygonMode == POLYGON_VECTOR) {
			GeoElement[] ret = { null };
			GeoElement[] ret0 = kernel.VectorPolygon(null,
					getSelectedPointsND());
			if (ret0 != null) {
				ret[0] = ret0[0];
			}
			return ret;
		} else {

			GeoElement[] ret = { null };
			GeoElement[] ret0 = kernel.Polygon(null, getSelectedPointsND());
			if (ret0 != null) {
				ret[0] = ret0[0];
			}
			return ret;
		}
	}

	protected GeoElement[] intersect(Hits intersectHits) {

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
			hits.getHits(Test.GEOLINEND, tempArrayList);
			goodHits.addAll(tempArrayList);

			if (goodHits.size() < 2) {
				hits.getHits(Test.GEOCONICND, tempArrayList);
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
			checkZooming();

			GeoPointND point = getAlgoDispatcher().IntersectLines(null,
					lines[0], lines[1]);
			checkCoordCartesian(point);
			return new GeoElement[] { (GeoElement) point };
		}
		// two conics
		else if (selConics() >= 2) {
			GeoConicND[] conics = getSelectedConicsND();
			GeoElement[] ret = { null };
			if (singlePointWanted) {
				checkZooming();

				ret[0] = getAlgoDispatcher().IntersectConicsSingle(null,
						(GeoConic) conics[0], (GeoConic) conics[1], xRW, yRW);
				checkCoordCartesian((GeoPointND) ret[0]);
			} else {
				ret = (GeoElement[]) getAlgoDispatcher().IntersectConics(null,
						conics[0], conics[1]);
				for (int i = 0; i < ret.length; i++) {
					checkCoordCartesian((GeoPointND) ret[i]);
				}
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

				return new GeoElement[] { getAlgoDispatcher()
						.IntersectFunctions(null, fun[0], fun[1], initPoint) };
			}
			// polynomials
			if (singlePointWanted) {
				checkZooming();

				return new GeoElement[] { getAlgoDispatcher()
						.IntersectPolynomialsSingle(null, fun[0], fun[1], xRW,
								yRW) };
			}
			return getAlgoDispatcher().IntersectPolynomials(null, fun[0],
					fun[1]);
		}
		// one line and one conic
		else if ((selLines() >= 1) && (selConics() >= 1)) {
			GeoConicND[] conic = getSelectedConicsND();
			GeoLineND[] line = getSelectedLinesND();
			GeoElement[] ret = { null };
			checkZooming();

			if (singlePointWanted) {
				ret[0] = getAlgoDispatcher().IntersectLineConicSingle(null,
						(GeoLine) line[0], (GeoConic) conic[0], xRW, yRW);
				checkCoordCartesian((GeoPointND) ret[0]);
			} else {
				ret = (GeoElement[]) getAlgoDispatcher().IntersectLineConic(
						null, line[0], conic[0]);
				for (int i = 0; i < ret.length; i++) {
					checkCoordCartesian((GeoPointND) ret[i]);
				}
			}

			return ret;
		}
		// line and polyLine
		else if ((selLines() >= 1) && (selPolyLines() >= 1)) {
			GeoLine line = getSelectedLines()[0];
			GeoPolyLine polyLine = getSelectedPolyLines()[0];
			GeoElement[] ret = { null };
			checkZooming();

			ret = getAlgoDispatcher().IntersectLinePolyLine(
					new String[] { null }, line, polyLine);
			return ret;
		}
		// line and curve
		else if ((selLines() >= 1) && (selCurves() >= 1)) {
			GeoLine line = getSelectedLines()[0];
			GeoCurveCartesian curve = getSelectedCurves()[0];
			GeoElement[] ret = { null };
			checkZooming();

			ret = getAlgoDispatcher().IntersectLineCurve(new String[] { null },
					line, curve);
			return ret;
		}
		// curve-curve
		else if ((selCurves() >= 2)) {
			GeoCurveCartesian[] curves = getSelectedCurves();
			GeoElement[] ret = { null };
			checkZooming();

			// multiple points disabled in ggb42, Reduce too slow
			if (singlePointWanted) {
				ret = getAlgoDispatcher().IntersectCurveCurveSingle(
						new String[] { null }, curves[0], curves[1], xRW, yRW);
			} else {
				ret = getAlgoDispatcher().IntersectCurveCurve(
						new String[] { null }, curves[0], curves[1]);
			}
			return ret;
		} // line and polygon
		else if ((selLines() >= 1) && (selPolygons() >= 1)) {
			GeoLine line = getSelectedLines()[0];
			GeoPolygon polygon = getSelectedPolygons()[0];
			GeoElement[] ret = { null };
			checkZooming();

			ret = getAlgoDispatcher().IntersectLinePolygon(
					new String[] { null }, line, polygon);
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
					ret[0] = getAlgoDispatcher().IntersectPolynomialLineSingle(
							null, fun[0], line[0], xRW, yRW);
				} else {
					ret = getAlgoDispatcher().IntersectPolynomialLine(null,
							fun[0], line[0]);
				}
			} else {
				GeoPoint initPoint = new GeoPoint(kernel.getConstruction());
				initPoint.setCoords(xRW, yRW, 1.0);
				ret[0] = getAlgoDispatcher().IntersectFunctionLine(null,
						fun[0], line[0], initPoint);
			}
			return ret;
			// function and conic
		} else if ((selFunctions() >= 1) && (selConics() >= 1)) {
			GeoConic[] conic = getSelectedConics();
			GeoFunction[] fun = getSelectedFunctions();
			// if (fun[0].isPolynomialFunction(false)){
			checkZooming();

			if (singlePointWanted) {
				return new GeoElement[] { getAlgoDispatcher()
						.IntersectPolynomialConicSingle(null, fun[0], conic[0],
								xRW, yRW) };
			}
			return getAlgoDispatcher().IntersectPolynomialConic(null, fun[0],
					conic[0]);
			// }
		} else if (selImplicitpoly() >= 1) {
			if (selFunctions() >= 1) {
				GeoImplicitPoly p = getSelectedImplicitpoly()[0];
				GeoFunction fun = getSelectedFunctions()[0];
				// if (fun.isPolynomialFunction(false)){
				checkZooming();

				if (singlePointWanted) {
					return new GeoElement[] { getAlgoDispatcher()
							.IntersectImplicitpolyPolynomialSingle(null, p,
									fun, xRW, yRW) };
				}
				return getAlgoDispatcher().IntersectImplicitpolyPolynomial(
						null, p, fun);
				// }else
				// return null;
			} else if (selLines() >= 1) {
				GeoImplicitPoly p = getSelectedImplicitpoly()[0];
				GeoLine l = getSelectedLines()[0];
				checkZooming();

				if (singlePointWanted) {
					return new GeoElement[] { getAlgoDispatcher()
							.IntersectImplicitpolyLineSingle(null, p, l, xRW,
									yRW) };
				}
				return getAlgoDispatcher()
						.IntersectImplicitpolyLine(null, p, l);
			} else if (selConics() >= 1) {
				GeoImplicitPoly p = getSelectedImplicitpoly()[0];
				GeoConic c = getSelectedConics()[0];
				checkZooming();

				if (singlePointWanted) {
					return new GeoElement[] { getAlgoDispatcher()
							.IntersectImplicitpolyConicSingle(null, p, c, xRW,
									yRW) };
				}
				return getAlgoDispatcher().IntersectImplicitpolyConic(null, p,
						c);
			} else if (selImplicitpoly() >= 2) {
				GeoImplicitPoly[] p = getSelectedImplicitpoly();
				checkZooming();

				if (singlePointWanted) {
					return new GeoElement[] { getAlgoDispatcher()
							.IntersectImplicitpolysSingle(null, p[0], p[1],
									xRW, yRW) };
				}
				return getAlgoDispatcher().IntersectImplicitpolys(null, p[0],
						p[1]);
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
				checkZooming();

				if (((GeoElement) points[0]).isGeoElement3D()
						|| ((GeoElement) vectors[0]).isGeoElement3D()) {
					ret[0] = (GeoElement) getKernel().getManager3D().Line3D(
							null, points[0], vectors[0]);
				} else {
					ret[0] = getAlgoDispatcher().Line(null,
							(GeoPoint) points[0], (GeoVector) vectors[0]);
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
					ret[0] = getAlgoDispatcher().Line(null,
							(GeoPoint) points[0], (GeoLine) lines[0]);
				}
				return ret;
			}
		}
		return null;
	}

	/**
	 * ensure that the point will show 2D cartesion coords
	 * 
	 * @param point
	 *            point
	 */
	private static void checkCoordCartesian(GeoPointND point) {
		if (point.getMode() != Kernel.COORD_CARTESIAN) {
			point.setCartesian();
			point.updateRepaint();
		}
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
				GeoPointND[] points = getSelectedPointsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new parabola
				GeoElement[] ret = { null };
				checkZooming();

				ret[0] = companion.parabola(points[0], lines[0]);
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

				ret[0] = getAlgoDispatcher().OrthogonalLine(null,
						(GeoPoint) points[0], (GeoVector) vectors[0]);
				return ret;

			} else if (selLines() == 1) {
				// fetch selected point and line
				GeoPointND[] points = getSelectedPointsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new line
				return companion.orthogonal(points[0], lines[0]);
			}
		}
		return null;
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
			ret[0] = companion.midpoint(points[0], points[1]);
			ret[0].setLabel(null);
			return ret;
		} else if (selSegments() == 1) {
			// fetch the selected segment
			GeoSegmentND[] segments = getSelectedSegmentsND();
			checkZooming();
			ret[0] = companion.midpoint(segments[0]);
			return ret;
		} else if (selConics() == 1) {
			// fetch the selected segment
			GeoConicND[] conics = getSelectedConicsND();
			checkZooming();
			ret[0] = companion.midpoint(conics[0]);
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

			getDialogManager().showFunctionInspector(functions[0]);
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
			GeoPointND[] points = getSelectedPointsND();
			checkZooming();

			companion.lineBisector(points[0], points[1]);
			return ret;
		} else if (selSegments() == 1) {
			// fetch the selected segment
			GeoSegmentND[] segments = getSelectedSegmentsND();
			checkZooming();

			companion.lineBisector(segments[0]);
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
			GeoPointND[] points = getSelectedPointsND();
			GeoElement[] ret = { null };
			checkZooming();

			ret[0] = companion.angularBisector(points[0], points[1], points[2]);
			return ret;
		} else if (selLines() == 2) {
			// fetch the two lines
			GeoLineND[] lines = getSelectedLinesND();
			checkZooming();

			return companion.angularBisector(lines[0], lines[1]);
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

			ret[0] = companion.ellipseHyperbola(points[0], points[1],
					points[2], GeoConicNDConstants.CONIC_ELLIPSE);
			break;

		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
			checkZooming();

			ret[0] = companion.ellipseHyperbola(points[0], points[1],
					points[2], GeoConicNDConstants.CONIC_HYPERBOLA);
			break;

		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
			checkZooming();

			ret[0] = companion.circumcircleArc(points[0], points[1], points[2]);
			break;

		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			checkZooming();

			ret[0] = companion.circumcircleSector(points[0], points[1],
					points[2]);
			break;

		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
			checkZooming();

			ret[0] = companion.circleArcSector(points[0], points[1], points[2],
					GeoConicNDConstants.CONIC_PART_ARC);
			break;

		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
			checkZooming();

			ret[0] = companion.circleArcSector(points[0], points[1], points[2],
					GeoConicNDConstants.CONIC_PART_SECTOR);
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
			GeoPointND[] points = getSelectedPointsND();
			GeoElement locus;
			checkZooming();

			if (points[0].getPath() == null) {
				locus = companion.locus(points[0], points[1]);
			} else {
				locus = companion.locus(points[1], points[0]);
			}
			GeoElement[] ret = { null };
			ret[0] = locus;
			return ret;
		} else if ((selPoints() == 1) && (selNumbers() == 1)) {
			GeoPoint[] points = getSelectedPoints();
			GeoNumeric[] numbers = getSelectedNumbers();
			checkZooming();

			GeoLocus locus = getAlgoDispatcher().Locus(null, points[0],
					numbers[0]);
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
			GeoPointND[] points = getSelectedPointsND();
			GeoElement[] ret = { null };
			checkZooming();

			ret[0] = companion.conic5(points);
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

			return getTextDispatcher().createSlopeText(line, mouseLoc);
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
			found = addSelectedList(hits, 1, false) != 0;
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
				GeoConicND[] conics = getSelectedConicsND();
				GeoPointND[] points = getSelectedPointsND();
				// create new tangents
				checkZooming();

				return companion.tangent(points[0], conics[0]);
			} else if (selLines() == 1) {
				GeoConicND[] conics = getSelectedConicsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new line
				checkZooming();

				return companion.tangent(lines[0], conics[0]);
			}
		} else if (selConics() == 2) {
			GeoConicND[] conics = getSelectedConicsND();
			// create new tangents
			checkZooming();

			return companion.tangent(conics[0], conics[1]);

		} else if (selFunctions() == 1) {
			if (selPoints() == 1) {
				GeoFunction[] functions = getSelectedFunctions();
				GeoPointND[] points = getSelectedPointsND();
				// create new tangents
				GeoElement[] ret = { null };
				checkZooming();

				ret[0] = getAlgoDispatcher().Tangent(null, points[0],
						functions[0]);
				return ret;
			}
		} else if (selCurves() == 1) {
			if (selPoints() == 1) {
				GeoCurveCartesian[] curves = getSelectedCurves();
				GeoPointND[] points = getSelectedPointsND();
				// create new tangents
				GeoElement[] ret = { null };
				checkZooming();

				ret[0] = kernel.Tangent(null, points[0], curves[0]);
				return ret;
			}
		} else if (selImplicitpoly() == 1) {
			if (selPoints() == 1) {
				GeoImplicitPoly implicitPoly = getSelectedImplicitpoly()[0];
				GeoPointND[] points = getSelectedPointsND();
				// create new tangents
				checkZooming();

				return getAlgoDispatcher().Tangent(null, points[0],
						implicitPoly);

			} /*
			 * else if (selLines() == 1) { GeoImplicitPoly implicitPoly =
			 * getSelectedImplicitpoly()[0]; GeoLineND[] lines =
			 * getSelectedLinesND(); // create new line checkZooming();
			 * 
			 * return getAlgoDispatcher().Tangent(null, lines[0], implicitPoly);
			 * }
			 */// not implemented yet
		}
		return null;
	}

	public boolean deleteAll(Hits hits) {
		if (hits.isEmpty()) {
			return false;
		}

		for (int i = 0; i < hits.size(); i++) {
			hits.get(i).removeOrSetUndefinedIfHasFixedDescendent();
		}

		return true;
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
				GeoConicND[] conics = getSelectedConicsND();
				GeoPointND[] points = getSelectedPointsND();
				// create new tangents
				checkZooming();

				ret[0] = companion.polarLine(points[0], conics[0]);
				return ret;

			} else if (selLines() == 1) {
				GeoConicND[] conics = getSelectedConicsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new line
				checkZooming();

				ret[0] = companion.diameterLine(lines[0], conics[0]);
				return ret;
			} else if (selVectors() == 1) {
				GeoConicND[] conics = getSelectedConicsND();
				GeoVectorND[] vecs = getSelectedVectorsND();
				// create new line
				checkZooming();

				ret[0] = companion.diameterLine(vecs[0], conics[0]);
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
		if (app.getGeoForCopyStyle() == null) {
			app.setGeoForCopyStyle(geo);
			Hits oldhits = new Hits();
			oldhits.addAll(getAppSelectedGeos());
			for (int i = oldhits.size() - 1; i >= 0; i--) {
				GeoElement oldgeo = oldhits.get(i);
				// if (!(movedGeoElement.getClass().isInstance(oldgeo))) {
				if (!(Test.getSpecificTest(app.getGeoForCopyStyle())
						.check(oldgeo))) {
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
			selection.addSelectedGeo(geo);
		} else {
			if (geo == app.getGeoForCopyStyle()) {
				// deselect
				selection.removeSelectedGeo(geo);
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
	 * @return true if a checkbox/textfield/button just has been hitted, to
	 *         avoid properties view to show graphics properties
	 */
	public boolean checkBoxOrTextfieldOrButtonJustHitted() {
		return checkBoxOrButtonJustHitted || isTextfieldHasFocus();
	}

	protected abstract void initToolTipManager();

	protected void initShowMouseCoords() {
		view.setShowMouseCoords((mode == EuclidianConstants.MODE_POINT)
				|| (mode == EuclidianConstants.MODE_MOVE));
	}

	public final void wrapMouseEntered() {
		if (isTextfieldHasFocus()) {
			return;
		}

		initToolTipManager();
		initShowMouseCoords();
		view.mouseEntered();
	}

	protected boolean getSelectables(Hits hits) {

		addSelectedGeo(hits.getSelectableHits(), 1, false);
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
			getDialogManager().showNumberInputDialogSegmentFixed(
					l10n.getMenu(getKernel().getModeText(mode)),
					getSelectedPointsND()[0]);

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

			getDialogManager().showNumberInputDialogAngleFixed(
					l10n.getMenu(getKernel().getModeText(mode)),
					getSelectedSegmentsND(), getSelectedPointsND(), selGeos,
					this);

			return null;

		}
		return null;
	}

	protected GeoElement[] switchModeForCircleOrSphere2(int sphereMode) {
		checkZooming();

		GeoPointND[] points = getSelectedPointsND();
		if (sphereMode == EuclidianConstants.MODE_SEMICIRCLE) {
			return new GeoElement[] { companion
					.semicircle(points[0], points[1]) };
		}
		return companion.createCircle2(points[0], points[1]);

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
					view.setShowAxis(EuclidianViewInterfaceCommon.AXIS_X,
							false, true);
					break;

				case GeoAxisND.Y_AXIS:
					// view.showAxes(view.getShowXaxis(), false);
					view.setShowAxis(EuclidianViewInterfaceCommon.AXIS_Y,
							false, true);
					break;
				}
				app.updateMenubar();
			} else {
				selection.toggleSelectedGeo(geo);
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
			getDialogManager().showTextCreationDialog(loc);
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
			getDialogManager().showSliderCreationDialog(mouseLoc.x, mouseLoc.y);
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
			if (app.getGuiManager() != null) {// FIXME: fix this better
				app.getGuiManager().loadImage(loc, null, altDown);
			}
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
				GeoPointND[] points = getSelectedPointsND();
				checkZooming();

				return companion.mirrorAtPoint(polys[0], points[0]);
			} else if (selGeos() > 0) {
				// mirror all selected geos
				GeoElement[] geos = getSelectedGeos();
				GeoPointND point = getSelectedPointsND()[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				checkZooming();

				for (int i = 0; i < geos.length; i++) {
					if (geos[i] != point) {
						if (geos[i] instanceof Transformable) {
							ret.addAll(Arrays.asList(companion.mirrorAtPoint(
									geos[i], point)));
						} else if (geos[i].isGeoPolygon()) {
							ret.addAll(Arrays.asList(companion.mirrorAtPoint(
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

	protected final GeoElement[] mirrorAtLine(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}

		int max = selLines() == 0 ? 1 : 2;

		// Transformable
		Hits mirAbles = hits.getHits(Test.TRANSFORMABLE, tempArrayList);
		int count = addSelectedGeo(mirAbles, max, false);

		// first selected GeoElement is GeoLine
		if (count == 1 && selGeos() >= 1) {
			GeoElement geo = selectedGeos.get(selectedGeos.size() - 1);
			if (geo instanceof GeoLineND) {
				selectedLines.clear();
				selectedLines.add((GeoLineND) geo);
			}
		}

		// polygon
		if (count <= 0) {
			count = addSelectedPolygon(hits, max, false);
		}

		// line = mirror
		if (count <= 0) {
			addSelectedLine(hits, max, false);
		}

		// we got the mirror point
		if (selLines() >= 1) {
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoLine[] lines = getSelectedLines();
				checkZooming();

				return getAlgoDispatcher().Mirror(null, polys[0], lines[0]);
			} else if (selGeos() > 1) { // line is also selected
				// mirror all selected geos
				GeoElement[] geos = getSelectedGeos();
				GeoLineND line = getSelectedLinesND()[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				checkZooming();

				for (int i = 0; i < geos.length; i++) {
					if (geos[i] != line) {
						if (geos[i] instanceof Transformable) {
							ret.addAll(Arrays.asList(companion.mirrorAtLine(
									geos[i], line)));
						} else if (geos[i].isGeoPolygon()) {
							ret.addAll(Arrays.asList(companion.mirrorAtLine(
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
							ret.addAll(Arrays.asList(getAlgoDispatcher()
									.Mirror(null, geos[i], line)));
						} else if (geos[i].isGeoPolygon()) {
							ret.addAll(Arrays.asList(getAlgoDispatcher()
									.Mirror(null, geos[i], line)));
						}
					}
				}
				GeoElement[] retex = {};
				return ret.toArray(retex);
			}
		}
		return null;
	}

	private boolean clearHighlightedGeos() {

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

	public boolean refreshHighlighting(Hits hits, boolean isControlDown) {

		Hits oldHighlightedGeos = highlightedGeos.clone();

		// clear old highlighting
		boolean repaintNeeded = clearHighlightedGeos();

		selectionPreview = true; // only preview selection, see also
		// mouseReleased()

		// TODO - this can trigger a tool on mouse-move
		// https://www.geogebra.org/forum/viewtopic.php?f=8&t=33719
		// removing breaks previews in trunk
		processMode(hits, isControlDown, null); // build highlightedGeos List

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

		// if highlightedGeos are the same as the old highlightedGeos, do not
		// repaint
		// old refreshHighlighting repainted every time when one of them was not
		// empty
		if (!repaintNeeded)
			return false;
		else if (oldHighlightedGeos.size() == highlightedGeos.size()
				&& oldHighlightedGeos.containsAll(highlightedGeos))
			return false;

		return true;
	}

	public boolean highlight(GeoElement geo) {
		boolean repaintNeeded = clearHighlightedGeos();

		if (geo != null) {
			highlightedGeos.add(geo);
			setHighlightedGeos(true);
			repaintNeeded = true;
		}

		return repaintNeeded;
	}

	public boolean highlight(ArrayList<GeoElement> geos) {
		boolean repaintNeeded = clearHighlightedGeos();

		if (geos != null && geos.size() > 0) {
			for (GeoElement geo : geos)
				highlightedGeos.add(geo);
			setHighlightedGeos(true);
			repaintNeeded = true;
		}

		return repaintNeeded;
	}

	public void clearSelections() {
		clearSelections(true, true);
	}

	/**
	 * Clear selection
	 * 
	 * @param repaint
	 *            whether all views need repainting afterwards
	 * @param updateSelection
	 *            call (or not) updateSelection()
	 */
	public void clearSelections(boolean repaint, boolean updateSelection) {
		startCollectingMinorRepaints();

		clearSelected();

		selection.clearSelectedGeos(repaint, updateSelection);

		// if we clear selection and highlighting,
		// we may want to clear justCreatedGeos also
		clearJustCreatedGeos();

		// clear highlighting
		refreshHighlighting(null, false); // this may call repaint

		stopCollectingMinorRepaints();
	}

	public void clearSelected() {
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
	}

	final protected boolean attachDetach(Hits hits) {
		if (detachFrom != null || needsAttach) {
			hits.remove(movedGeoPoint);

			// replace point with point it was dragged to
			if (hits.containsGeoPoint()
					&& ((GeoElement) movedGeoPoint).hasChildren()) {
				try {
					this.kernel.getConstruction().replace(
							(GeoElement) movedGeoPoint,
							hits.getFirstHit(Test.GEOPOINTND));
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				// if the GeoPoint is moved from one element to the other it is
				// first detached (which deletes the information about the
				// target) and then attached. Therefore the target has to be
				// stored beforehand
				String attachTo = movedGeoPoint.isPointOnPath() ? movedGeoPoint
						.getPath().getLabel(StringTemplate.defaultTemplate)
						: "";

				// deatch
				if (movedGeoPoint != null && detachFrom != null
						&& !hits.contains(detachFrom)) {
					String name = movedGeoPoint
							.getLabel(StringTemplate.defaultTemplate);
					this.app.getKernel()
							.getAlgoDispatcher()
							.detach(movedGeoPoint,
									view.toRealWorldCoordX(mouseLoc.x),
									view.toRealWorldCoordY(mouseLoc.y),
									detachFromPath, detachFromRegion);
					movedGeoPoint = (GeoPointND) this.kernel.getConstruction()
							.geoTableVarLookup(name);
				}

				// attch
				if (movedGeoPoint != null && needsAttach) {
					if (!attachTo.equals("")) {
						Path path = (Path) this.kernel.getConstruction()
								.geoTableVarLookup(attachTo);
						this.kernel.getAlgoDispatcher().attach(movedGeoPoint,
								path, view, getMouseLocRW());
					}
				}
			}

			needsAttach = false;
			detachFrom = null;
			if (selGeos() > 0) {
				clearSelections();
			}
			return true;
		}

		if (hits.isEmpty()) {
			return false;
		}

		addSelectedRegion(hits, 1, false);

		addSelectedPath(hits, 1, false);

		addSelectedPoint(hits, 1, false);

		if (selectedPoints.size() == 1) {

			GeoPointND p = selectedPoints.get(0);

			if (p.isPointOnPath() || p.isPointInRegion()) {

				getSelectedPointsND();
				getSelectedRegions();
				getSelectedPaths();

				checkZooming();
				GeoPointND ret = getAlgoDispatcher().detach(p, view);

				if (ret != null) {
					clearSelections();
					view.updateCursor(ret);
					return true;
				}

				return false;
			}
		}

		if (selPoints() == 1) {
			if ((selPaths() == 1) && !isAltDown()) { // press alt to force
														// region
														// (ie inside) not path
														// (edge)
				Path paths[] = getSelectedPaths();
				GeoPointND[] points = getSelectedPointsND();

				if (((GeoElement) paths[0]).isChildOf((GeoElement) points[0])) {
					return false;
				}

				if (((GeoElement) paths[0]).isGeoPolygon()
						|| (((GeoElement) paths[0]).isGeoConic() && (((GeoConicND) paths[0])
								.getLastHitType() == HitType.ON_FILLING))) {

					checkZooming();
					GeoPointND ret = getAlgoDispatcher().attach(points[0],
							(Region) paths[0], view, getMouseLocRW());

					if (ret != null) {
						clearSelections();
						view.updateCursor(ret);
						return true;
					}

					return false;
				}

				checkZooming();
				GeoPointND ret = getAlgoDispatcher().attach(points[0],
						paths[0], view, getMouseLocRW());

				if (ret != null) {
					clearSelections();
					view.updateCursor(ret);
					return true;
				}

				return false;

			} else if (selRegions() == 1) {
				Region regions[] = getSelectedRegions();
				GeoPointND[] points = getSelectedPointsND();

				if (!((GeoElement) regions[0])
						.isChildOf((GeoElement) points[0])) {

					checkZooming();
					GeoPointND ret = getAlgoDispatcher().attach(points[0],
							regions[0], view, getMouseLocRW());

					if (ret != null) {
						clearSelections();
						view.updateCursor(ret);
						return true;
					}

					return false;
				}

			}
		}
		return false;
	}

	private Coords mouseLocRW;

	protected Coords getMouseLocRW() {

		if (mouseLocRW == null) {
			mouseLocRW = Coords.createInhomCoorsInD3();
		}

		if (mouseLoc == null) {
			mouseLocRW.setX(0);
			mouseLocRW.setY(0);
		} else {
			mouseLocRW.setX(view.toRealWorldCoordX(mouseLoc.x));
			mouseLocRW.setY(view.toRealWorldCoordY(mouseLoc.y));
		}

		return mouseLocRW;
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
				return companion.translate(polys[0], vec);
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
							ret.addAll(Arrays.asList(companion.translate(
									geos[i], vec)));
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
		if (selGeos() == 0
				|| (selGeos() == 1 && selectedGeos.get(0) instanceof GeoPointND)
				&& !hits.containsGeoPoint()) {
			// if the first geo to be selected is a point and the second is not,
			// the point will be used as rotation center
			Hits rotAbles = hits.getHits(Test.TRANSFORMABLE, tempArrayList);
			count = addSelectedGeo(rotAbles, 2, false);
		}

		// polygon
		if (count == 0) {
			count = addSelectedPolygon(hits, 1, false);
		}

		// rotation center
		if (count == 0) {
			addSelectedPoint(hits, 1, false);
		}

		if (selGeos() > 1 && selPoints() == 0
				&& selectedGeos.get(0) instanceof GeoPointND) {
			// If a point is selected as first geo, it is not added to
			// selectedPoints, because the last point that is selected is used
			// as rotation center.
			// Therefore a point that was selected first has to be added to
			// selecetedPoints, if another geo is selected in the second step
			selectedPoints.add((GeoPointND) selectedGeos.get(0));
		}

		// we got the rotation center point
		if (selPoints() == 1 && selGeos() > 1) {

			GeoElement[] selGeos = getSelectedGeos();

			getDialogManager()
					.showNumberInputDialogRotate(
							l10n.getMenu(getKernel().getModeText(mode)),
							getSelectedPolygons(), getSelectedPointsND(),
							selGeos, this);

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

			getDialogManager()
					.showNumberInputDialogDilate(
							l10n.getMenu(getKernel().getModeText(mode)),
							getSelectedPolygons(), getSelectedPointsND(),
							selGeos, this);

			return null;

			/*
			 * NumberValue num =
			 * app.getGuiManager().showNumberInputDialog(l10n.getMenu
			 * (getKernel().getModeText(mode)), l10n.getPlain("Numeric"), null);
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
		AlgoFitLineY algo = new AlgoFitLineY(kernel.getConstruction(), label,
				list);
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
		setMouseLocation(event.isAltDown(), event.getX(), event.getY());
	}

	protected void setMouseLocation(boolean alt, int x, int y) {
		mouseLoc = new GPoint(x, y);

		setAltDown(alt);

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
	 * 
	 * @return percentage for which we capture point to grid
	 */
	public float getPointCapturingPercentage() {
		return EuclidianStyleConstants.POINT_CAPTURING_GRID;
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
					if ((Math.abs(gp.getInhomX() - xRW) < (view
							.getGridDistances(0) * pointCapturingPercentage))
							&& (Math.abs(gp.getInhomY() - yRW) < (view
									.getGridDistances(1) * pointCapturingPercentage))) {
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
			pointCapturingPercentage = getPointCapturingPercentage();

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

				if (oddOrEvenRow == 0) {
					// X = (x, y) ... next grid point
					double x = Kernel.roundToScale(xRW / root3, isoGrid);
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
					double x = Kernel.roundToScale(
							(xRW / root3) - (view.getGridDistances(0) / 2),
							isoGrid);
					double y = Kernel
							.roundToScale(yRW - (isoGrid / 2), isoGrid);
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

				double x = Kernel.roundToScale(xRW, view.getGridDistances(0));
				double y = Kernel.roundToScale(yRW, view.getGridDistances(1));

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
				double r2 = Kernel.roundToScale(r, view.getGridDistances(0));

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

	public AlgoDispatcher getAlgoDispatcher() {
		return kernel.getAlgoDispatcher();
	}

	protected final GeoElement[] area(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}

		int count = addSelectedPolygon(hits, 1, false);
		if (count == 0) {
			addSelectedConic(hits, 2, false);
		}

		// area of CONIC
		if (selConics() == 1) {
			GeoConicND conic = getSelectedConicsND()[0];

			// check if arc
			if (conic.isGeoConicPart()) {
				GeoConicPart conicPart = (GeoConicPart) conic;
				if (conicPart.getConicPartType() == GeoConicNDConstants.CONIC_PART_ARC) {
					clearSelections();
					return null;
				}
			}

			// standard case: conic
			checkZooming();

			GeoNumeric area = getAlgoDispatcher().Area(null, conic);

			return getTextDispatcher().getAreaText(conic, area, mouseLoc);
		}

		// area of polygon
		else if (selPolygons() == 1) {
			GeoPolygon[] poly = getSelectedPolygons();

			return getTextDispatcher().getAreaText(poly[0], poly[0], mouseLoc);
		}

		return null;
	}

	protected boolean regularPolygon(Hits hits) {
		if (hits.isEmpty()) {
			return false;
		}

		// need two points
		addSelectedPoint(hits, 2, false);

		if (selPoints() == 2) {
			GeoPointND[] points = getSelectedPointsND();
			getDialogManager().showNumberInputDialogRegularPolygon(
					l10n.getMenu(getKernel().getModeText(mode)), this,
					points[0], points[1]);
			return true;
		}
		return false;
	}

	/**
	 * add selected planes for angle tool (3D)
	 * 
	 * @param hits
	 *            current hits
	 * @param count
	 *            previous count
	 * @return new count
	 */
	protected int addSelectedPlanesForAngle(Hits hits, int count) {

		return count;
	}

	/**
	 * 
	 * @return angle from plane/plane or plane/line
	 */
	protected GeoAngle createAngle3D() {
		return null;
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

		// try planes (for 3D)
		if (!polyFound) {
			count = addSelectedPlanesForAngle(hits, count);
		}

		GeoAngle angle = null;
		GeoElement[] angles = null;
		if (selPoints() == 3) {
			GeoPointND[] points = getSelectedPointsND();
			angle = companion.createAngle(points[0], points[1], points[2]);
		} else if (selVectors() == 2) {
			GeoVectorND[] vecs = getSelectedVectorsND();
			checkZooming();
			angle = companion.createAngle(vecs[0], vecs[1]);
		} else if (selLines() == 2) {
			GeoLineND[] lines = getSelectedLinesND();
			checkZooming();

			angle = companion.createLineAngle(lines[0], lines[1]);
		} else if (polyFound && (selGeos() == 1)) {
			checkZooming();

			angles = companion.createAngles((GeoPolygon) getSelectedGeos()[0]);
		} else { // 3D
			angle = createAngle3D();
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

	private TextDispatcher textDispatcher;

	protected TextDispatcher getTextDispatcher() {
		if (textDispatcher == null) {
			textDispatcher = new TextDispatcher(kernel, view);
		}
		return textDispatcher;
	}

	protected final GeoElement[] distance(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}

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
		// quit here, see #3885
		if (selectionPreview) {
			return null;
		}
		// TWO POINTS
		if (selPoints() == 2) {
			// length
			GeoPointND[] points = getSelectedPointsND();
			checkZooming();

			GeoElement[] ret = { null };
			ret[0] = getTextDispatcher().createDistanceText(points[0],
					points[1]);
			return ret;
		}

		// POINT AND LINE
		else if ((selPoints() == 1) && (selLines() == 1)) {
			GeoPointND[] points = getSelectedPointsND();
			GeoLineND[] lines = getSelectedLinesND();

			GeoElement[] ret = { null };
			ret[0] = getTextDispatcher()
					.createDistanceText(points[0], lines[0]);

			clearSelections(); // make sure segment will be unselected

			return ret;
		}

		// SEGMENT
		// make this after point-line
		else if (selSegments() == 1) {
			// length
			GeoSegmentND[] segments = getSelectedSegmentsND();

			// length
			GeoElement seg = (GeoElement) segments[0];
			if (seg.isLabelVisible()) {
				seg.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			} else {
				seg.setLabelMode(GeoElement.LABEL_VALUE);
			}
			segments[0].setLabelVisible(true);
			segments[0].updateRepaint();
			GeoElement[] ret = { seg };
			return ret; // return this not null because the kernel has
						// changed
		}

		// TWO LINES
		else if (selLines() == 2) {
			GeoLineND[] lines = getSelectedLinesND();
			GeoElement[] ret = { null };
			checkZooming();

			ret[0] = getAlgoDispatcher().Distance(null, lines[0], lines[1]);
			return ret; // return this not null because the kernel has changed
		}

		// circumference of CONIC
		else if (selConics() == 1) {
			GeoConicND conic = getSelectedConicsND()[0];

			return getTextDispatcher().createCircumferenceText(conic, mouseLoc);
		}

		// perimeter of POLYGON
		else if (selPolygons() == 1) {
			GeoPolygon[] poly = getSelectedPolygons();

			return getTextDispatcher().createPerimeterText(poly[0], mouseLoc);
		}

		return null;
	}

	protected final boolean showCheckBox() {
		if (selectionPreview) {
			return false;
		}

		getDialogManager().showBooleanCheckboxCreationDialog(mouseLoc, null);
		return false;
	}

	protected final GeoElement[] compasses(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}

		// we already have two points that define the radius
		if (selPoints() == 2) {
			GeoPointND[] points = new GeoPointND[2];
			points[0] = selectedPoints.get(0);
			points[1] = selectedPoints.get(1);

			// check for centerPoint
			GeoPointND centerPoint = (GeoPointND) chooseGeo(hits,
					Test.GEOPOINTND);

			if (centerPoint != null) {
				if (selectionPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add((GeoElement) centerPoint);
					addToHighlightedList(selectedPoints, tempArrayList, 3);
					return null;
				}
				checkZooming();

				// three points: center, distance between two points
				GeoElement circle = CircleCompasses(centerPoint, points[0],
						points[1]);
				GeoElement[] ret = { circle };
				clearSelections();
				return ret;
			}
		}

		// we already have a circle that defines the radius
		else if (selConics() == 1) {
			GeoConicND circle = selectedConicsND.get(0);

			// check for centerPoint
			GeoPointND centerPoint = (GeoPointND) chooseGeo(hits,
					Test.GEOPOINTND);

			if (centerPoint != null) {
				if (selectionPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add((GeoElement) centerPoint);
					addToHighlightedList(selectedPoints, tempArrayList, 3);
					return null;
				}
				checkZooming();

				// center point and circle which defines radius
				GeoElement circlel = Circle(centerPoint, circle);
				GeoElement ret[] = { circlel };
				clearSelections();
				return ret;
			}
		}
		// we already have a segment that defines the radius
		else if (selSegments() == 1) {
			GeoSegmentND segment = selectedSegments.get(0);

			// check for centerPoint
			GeoPointND centerPoint = (GeoPointND) chooseGeo(hits,
					Test.GEOPOINTND);

			if (centerPoint != null) {
				if (selectionPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add((GeoElement) centerPoint);
					addToHighlightedList(selectedPoints, tempArrayList, 3);
					return null;
				}
				checkZooming();

				// center point and segment
				GeoElement circlel = companion.circle(kernel.getConstruction(),
						centerPoint, segment);
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
				GeoConicND c = selectedConicsND.get(0);
				if (!c.isCircle()) {
					selectedConicsND.remove(0);
					clearSelections();
				}
			}
		}

		return null;
	}

	/**
	 * circle with midpoint A and radius the same as circle/sphere Michael
	 * Borcherds 2008-03-14
	 */
	final private GeoConicND Circle(
	// this is actually a macro
			GeoPointND A, GeoQuadricND c) {

		Construction cons = kernel.getConstruction();

		AlgoRadius radius = new AlgoRadius(cons, c);
		cons.removeFromConstructionList(radius);

		GeoConicND circle = companion.circle(cons, A, radius.getRadius());
		circle.setToSpecific();
		circle.update();
		// notifyUpdate(circle);
		return circle;
	}

	/**
	 * circle with midpoint M and radius BC Michael Borcherds 2008-03-14
	 */
	final private GeoConicND CircleCompasses(GeoPointND A, GeoPointND B,
			GeoPointND C) {

		Construction cons = kernel.getConstruction();

		AlgoElement algoSegment = companion.segmentAlgo(cons, B, C);
		cons.removeFromConstructionList(algoSegment);

		GeoConicND circle = companion.circle(cons, A,
				(NumberValue) algoSegment.getOutput(0));
		circle.setToSpecific();
		circle.update();
		// notifyUpdate(circle);
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
			GeoVectorND[] vecs = getSelectedVectorsND();
			GeoPointND[] points = getSelectedPointsND();
			checkZooming();

			GeoElement[] ret = { null };
			ret[0] = companion.vectorPoint(points[0], vecs[0]);
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
			getDialogManager().showNumberInputDialogCirclePointRadius(
					l10n.getMenu(getKernel().getModeText(mode)),
					getSelectedPointsND()[0], view);
			return true;
		}
		return false;
	}

	/** return the current movedGeoPoint */
	public GeoElement getMovedGeoPoint() {
		return ((GeoElement) movedGeoPoint);
	}

	public final GeoPointND updateNewPoint(boolean forPreviewable, Hits hits,
			boolean onPathPossible, boolean inRegionPossible,
			boolean intersectPossible, boolean chooseGeo, boolean complex) {

		// App.printStacktrace("\n"+hits);

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
						// hits are labeled
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

						// check if region is opaque
						if (((GeoElement) region).getAlphaValue() > MAX_TRANSPARENT_ALPHA_VALUE) {
							hits.removeGeosAfter((GeoElement) region);
						}

						boolean sideInHits = false;
						if (((GeoElement) region).isGeoPolygon()) {
							GeoSegmentND[] sides = ((GeoPolygon) region)
									.getSegments();

							if (sides != null) {
								for (int k = 0; k < sides.length; k++) {
									// sideInHits = sideInHits ||
									// hits.remove(sides[k]); //not removing
									// sides,
									// just test
									if (hits.contains(sides[k])) {
										sideInHits = true;
										break;
									}
								}
							}

							if (sideInHits) {
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
							if (createNewPointInRegionPossible((GeoConicND) region)) {
								createPoint = true;
								hits.remove(region); // conic won't be treated
														// as a path
							} else {
								createPoint = true;
							}

						} else if (region instanceof GeoFunction) {
							// eg x<4, y<4 (check not needed here for x+y<4)
							if (((GeoFunction) region).isInequality()) {
								createPoint = true;
								hits.remove(region); // inequality won't be
														// treated
														// as a path
							} else {
								createPoint = true;
							}
						}

						// if no polygon side in hits, then remove all polygons
						// for path
						// (use it also when region is conic, etc.)
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
				Hits pathHits = hits.getHits(Test.PATH_NO_FILL_HIT,
						tempArrayList);
				if (!pathHits.isEmpty()) {
					if (onPathPossible) {
						if (chooseGeo) {
							path = (Path) chooseGeo(pathHits, true);
						} else {
							path = (Path) pathHits.get(0);
						}
						if (path != null) {
							createPoint = true;
						}
					} else {
						createPoint = true;
					}
				}
			}
		}

		if (createPoint) {
			transformCoords(); // use point capturing if on
			// branches reordered to prefer path, and then region
			if ((path != null) && onPathPossible) {
				point = companion.createNewPoint(forPreviewable, path, complex);
				// App.debug(path);
			} else if ((region != null) && inRegionPossible) {
				point = companion.createNewPoint(forPreviewable, region,
						complex);
				// App.debug(region);
			} else {
				point = companion.createNewPoint(forPreviewable, complex);
				view.setShowMouseCoords(true);
			}
		}

		return point;
	}

	protected boolean createNewPointInRegionPossible(GeoConicND conic) {
		return ((mode == EuclidianConstants.MODE_POINT_ON_OBJECT) && (conic
				.getLastHitType() == HitType.ON_FILLING));

	}

	protected GeoPointND getNewPoint(Hits hits, boolean onPathPossible,
			boolean inRegionPossible, boolean intersectPossible, boolean complex) {

		return updateNewPoint(false, hits, onPathPossible, inRegionPossible,
				intersectPossible, true, complex);
	}

	protected boolean createNewPoint(Hits hits, boolean onPathPossible,
			boolean inRegionPossible, boolean intersectPossible,
			boolean doSingleHighlighting, boolean complex) {

		pointCreated = null;

		if (!allowPointCreation()) {
			return false;
		}

		GeoPointND point = getNewPoint(hits, onPathPossible, inRegionPossible,
				intersectPossible, complex);

		if (point != null) {
			pointCreated = point;

			handleMovedElement((GeoElement) point, false,
					PointerEventType.MOUSE);

			view.setDragCursor();
			if (doSingleHighlighting) {
				doSingleHighlighting(getMovedGeoPoint());
			}

			return true;
		}

		moveMode = MOVE_NONE;
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
	protected final void macro(Hits hits, final AsyncOperation callback2) {
		// try to get next needed type of macroInput
		index = selGeos();

		// standard case: try to get one object of needed input type
		boolean objectFound = 1 == handleAddSelected(hits, macroInput.length,
				false, selectedGeos, macroInput[index]);

		// some old code for polygon removed in [6779]

		// we're done if in selection preview
		if (selectionPreview) {
			if (callback2 != null)
				callback2.callback(false);
			return;
		}

		// only one point needed: try to create it
		if (!objectFound
				&& (macroInput[index].equals(Test.GEOPOINT) || macroInput[index]
						.equals(Test.GEOPOINTND))) {
			if (createNewPoint(hits, true, true, false)) {
				// take movedGeoPoint which is the newly created point
				selectedGeos.add(getMovedGeoPoint());
				selection.addSelectedGeo(getMovedGeoPoint());
				objectFound = true;
				pointCreated = null;
			}
		}

		// object found in handleAddSelected()
		if (objectFound || macroInput[index].equals(Test.GEONUMERIC)
				|| macroInput[index].equals(Test.GEOANGLE)) {
			if (!objectFound) {
				index--;
			}

			AsyncOperation callback3 = new AsyncOperation() {

				@Override
				public void callback(Object num) {
					if (num == null) {
						// no success: reset mode
						view.resetMode();
						if (callback2 != null)
							callback2.callback(false);
						return;
					}
					// great, we got our number
					if (num instanceof NumberValue
							&& ((NumberValue) num).isGeoElement()) {
						selectedGeos.add(((NumberValue) num).toGeoElement());
					}

					readNumberOrAngleIfNeeded(this);

					if (selGeos() == macroInput.length) {
						macroProcess(callback2);
					}
				}

			};
			// look ahead if we need a number or an angle next
			readNumberOrAngleIfNeeded(callback3);
		}

		macroProcess(callback2);

	}

	public void readNumberOrAngleIfNeeded(AsyncOperation callback3) {
		if (++index < macroInput.length) {

			// maybe we need a number
			if (macroInput[index].equals(Test.GEONUMERIC)) {
				app.getDialogManager().showNumberInputDialog(
						macro.getToolOrCommandName(), l10n.getPlain("Numeric"),
						null, callback3);

			}

			// maybe we need an angle
			else if (macroInput[index].equals(Test.GEOANGLE)) {
				app.getDialogManager().showAngleInputDialog(
						macro.getToolOrCommandName(), l10n.getPlain("Angle"),
						"45\u00b0", callback3);

			}
		}

	}

	public void macroProcess(AsyncOperation callback2) {
		// do we have everything we need?
		if (selGeos() == macroInput.length) {
			checkZooming();

			kernel.useMacro(null, macro, getSelectedGeos());
			if (callback2 != null)
				callback2.callback(true);
			return;
		}
		if (callback2 != null)
			callback2.callback(false);
	}

	protected final boolean button(boolean textfield) {
		if (!selectionPreview && (mouseLoc != null)) {
			getDialogManager().showButtonCreationDialog(mouseLoc.x, mouseLoc.y,
					textfield);
		}
		return false;
	}

	protected boolean switchModeForProcessMode(Hits hits,
			boolean isControlDown, final AsyncOperation callback) {

		Boolean changedKernel = false;
		GeoElement[] ret = null;

		switch (mode) {
		// case EuclidianConstants.MODE_VISUAL_STYLE:
		case EuclidianConstants.MODE_MOVE:
			// highlight and select hits
			if (selectionPreview) {
				getSelectables(hits.getTopHits());
			} else {
				if (draggingOccured && (selection.selectedGeosSize() == 1)) {
					selection.clearSelectedGeos();
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
			boolean addToSelection = isControlDown;
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
		case EuclidianConstants.MODE_ORTHOGONAL_THREE_D:
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
			changedKernel = getDeleteMode().process(hits.getTopHits(),
					isControlDown);
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
			changedKernel = text(hits
					.getOtherHits(Test.GEOIMAGE, tempArrayList));
			break;

		// new image
		case EuclidianConstants.MODE_IMAGE:
			changedKernel = image(hits.getOtherHits(Test.GEOIMAGE,
					tempArrayList)); // e.isAltDown());
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

		case EuclidianConstants.MODE_ATTACH_DETACH:
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
			ret = distance(hits);
			break;

		case EuclidianConstants.MODE_MACRO:
			// TODO: is memorizeJustCreatedGeos... needed here?
			// if not, wee needn't callback2 here, we can use the
			// another callback object in macro, which we got
			// in parameter.

			AsyncOperation callback2 = new AsyncOperation() {
				@Override
				public void callback(Object ret) {
					memorizeJustCreatedGeosAfterProcessMode(null);
					if (callback != null)
						callback.callback(ret);
				}
			};

			macro(hits, callback2);
			return false;
			// break;

		case EuclidianConstants.MODE_AREA:
			ret = area(hits);
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
			// case EuclidianConstants.MODE_PENCIL:
		case EuclidianConstants.MODE_FREEHAND_SHAPE:
			// changedKernel = pen();
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

		return endOfSwitchModeForProcessMode(ret, changedKernel, callback);
	}

	final protected boolean endOfSwitchModeForProcessMode(GeoElement[] ret,
			boolean changedKernel, AsyncOperation callback) {
		memorizeJustCreatedGeosAfterProcessMode(ret);

		if (callback != null)
			callback.callback(changedKernel || (ret != null));

		if (!changedKernel) {
			return ret != null;
		}

		return changedKernel;
	}

	protected void memorizeJustCreatedGeosAfterProcessMode(GeoElement[] ret) {
		if (ret != null) {
			memorizeJustCreatedGeos(ret);
		} else if (!selectionPreview) {
			clearJustCreatedGeos();
		}
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
			if (Test.PATH_NO_FILL_HIT.check(geo) && !geo.isGeoPolygon()) {
				companion.processModeLock((Path) geo);
			} else if (geo.isGeoPoint()) {
				companion.processModeLock((GeoPointND) geo);
			} else {
				transformCoords(); // grid lock
			}
		} else {
			if (!isAltDown()) {
				// interferes with Alt to get multiples of 15 degrees (web)
				transformCoords(); // grid lock
			}
		}
	}

	public final boolean processMode(Hits processHits, boolean isControlDown) {
		final Hits hits2 = processHits;
		AsyncOperation callback = new AsyncOperation() {

			@Override
			public void callback(Object changedKernel) {
				if (changedKernel.equals(true)) {
					app.storeUndoInfo();
				}
				endOfWrapMouseReleased(hits2, false, false, null); // type =
																	// null is
																	// not a
																	// problem
																	// since alt
																	// = false
			}
		};
		return processMode(processHits, isControlDown, callback);
	}

	public final boolean processMode(Hits processHits, boolean isControlDown,
			final AsyncOperation callback) {
		Hits hits = processHits;
		boolean changedKernel = false;

		if (hits == null) {
			hits = new Hits();
		}

		AsyncOperation callback2;
		if (callback == null) {
			callback2 = null;
		} else {
			callback2 = new AsyncOperation() {

				@Override
				public void callback(Object ret) {
					callback.callback(ret);
					updatePreview();
				}
			};
		}

		changedKernel = switchModeForProcessMode(hits, isControlDown, callback2);

		if(changedKernel){
			toolCompleted();
		}

		if (callback == null)
			updatePreview();

		return changedKernel;
	}

	/**
	 * This method is called, if the construction of the selected tool is
	 * finished. E.g. after a new segment was created, if the segment tool is
	 * selected.
	 */
	public void toolCompleted() {
		// not used in common, overwritten for other projects
	}

	public void updatePreview() {
		// update preview
		if (view.getPreviewDrawable() != null) {
			view.updatePreviewableForProcessMode();
			if (mouseLoc != null) {
				xRW = view.toRealWorldCoordX(mouseLoc.x);
				yRW = view.toRealWorldCoordY(mouseLoc.y);

				processModeLock();

				view.getPreviewDrawable().updateMousePos(xRW, yRW);
			}
			view.repaintView();
		}
	}

	/**
	 * @param rightClick
	 *            in 3D we need to check left/right click
	 */
	protected void processReleaseForMovedGeoPoint(boolean rightClick) {

		// deselect point after drag, but not on click
		// outdated - we want to leave the point selected after drag now
		// if (movedGeoPointDragged) getMovedGeoPoint().setSelected(false);

		if (app.isUsingFullGui()) {
			getMovedGeoPoint().resetTraceColumns();
		}

	}

	/**
	 * right-release the mouse makes stop 3D rotation
	 * 
	 * @return false
	 */
	protected boolean processRightReleaseFor3D(PointerEventType type) {
		return false;
	}

	protected final void rotateObject(boolean repaint) {
		double newAngle = Math.atan2(yRW - rotationCenter.inhomY, xRW
				- rotationCenter.inhomX);
		double angle = newAngle - rotationLastAngle;

		tempNum.set(angle);
		if (rotGeoElement.isChangeable()) {
			((PointRotateable) rotGeoElement).rotate(tempNum, rotationCenter);
			if (repaint) {
				rotGeoElement.updateRepaint();
			} else {
				rotGeoElement.updateCascade();
			}
		} else {
			ArrayList<GeoPointND> pts = rotGeoElement.getFreeInputPoints(view);
			for (GeoPointND pt : pts) {
				pt.rotate(tempNum, rotationCenter);
			}
			GeoElement.updateCascade(pts, new TreeSet<AlgoElement>(), false);
			view.repaint();
		}
		rotationLastAngle = newAngle;

	}

	protected final void moveLabel() {
		movedLabelGeoElement.setLabelOffset((oldLoc.x + mouseLoc.x)
				- startLoc.x, (oldLoc.y + mouseLoc.y) - startLoc.y);
		// no update cascade needed
		movedLabelGeoElement.update();
		kernel.notifyRepaint();
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

	protected void moveLine(boolean repaint) {
		// make parallel geoLine through (xRW, yRW)
		movedGeoLine.setLineThrough(xRW, yRW);
		updateAfterMove((GeoElement) movedGeoLine, repaint);
	}

	/**
	 * update the moved geo
	 */
	protected final static void updateAfterMove(GeoElement geo, boolean repaint) {
		if (repaint) {
			geo.updateRepaint();
		} else {
			geo.updateCascade();
		}
	}

	protected final void moveVector(boolean repaint) {

		moveVector();

		updateAfterMove((GeoElement) movedGeoVector, repaint);

	}

	protected void moveVector() {
		GeoPointND P = movedGeoVector.getStartPoint();
		if (P == null) {
			moveVector(xRW - transformCoordsOffset[0], yRW
					- transformCoordsOffset[1]);
		} else {
			Coords c = view.getCompanion().getCoordsForView(P);
			moveVector(xRW - c.getX(), yRW - c.getY());
		}
	}

	protected final void moveVector(double x, double y) {
		movedGeoVector.setCoords(x, y, 0.0);
	}

	protected final void moveVectorStartPoint(boolean repaint) {
		GeoPointND P = movedGeoVector.getStartPoint();
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
			// getStartPointX()), view.toScreenCoordY(yRW - getStartPointY()));
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

	protected void moveTextAbsoluteLocation() {
		GeoPoint loc = (GeoPoint) movedGeoText.getStartPoint();
		loc.setCoords(xRW - getStartPointX(), yRW - getStartPointY(), 1.0);
	}

	protected final void moveImage(boolean repaint) {
		if (movedGeoImage.isAbsoluteScreenLocActive()) {
			// movedGeoImage.setAbsoluteScreenLoc( oldLoc.x +
			// mouseLoc.x-startLoc.x,
			// oldLoc.y + mouseLoc.y-startLoc.y);

			movedGeoImage.setAbsoluteScreenLoc(
					view.toScreenCoordX(xRW - getStartPointX()),
					view.toScreenCoordY(yRW - getStartPointY()));

			if (repaint) {
				movedGeoImage.updateRepaint();
			} else {
				movedGeoImage.updateCascade();
			}
		} else {
			if (movedGeoImage.hasAbsoluteLocation()) {
				// absolute location: translate all defined corners
				double vx = xRW - getStartPointX();
				double vy = yRW - getStartPointY();
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

		if (isAltDown()
				&& (movedGeoConic.getType() == GeoConicNDConstants.CONIC_PARABOLA || movedGeoConic
						.getType() == GeoConicNDConstants.CONIC_DOUBLE_LINE)) {

			// drag a parabola bit keep the vertex fixed
			// CONIC_DOUBLE_LINE needed for y=0x^2

			double vX = movedGeoConic.b.getX();
			double vY = movedGeoConic.b.getY();

			int index = movedGeoConic.getType() == GeoConicNDConstants.CONIC_PARABOLA ? 0
					: 1;
			double c = movedGeoConic.getEigenvec(index).getX();
			double s = movedGeoConic.getEigenvec(index).getY();

			double coeff;
			double dx = xRW - vX;
			double dy = yRW - vY;
			coeff = (c * dx + s * dy) / ((s * dx - c * dy) * (s * dx - c * dy));
			if (coeff > 1E8) {
				coeff = 1E6;
			} else if (coeff < -1E8) {
				coeff = -1E6;
			}
			movedGeoConic.translate(-vX, -vY);

			movedGeoConic.setCoeffs(coeff * s * s, -2 * coeff * s * c, coeff
					* c * c, -c, -s, 0);

			movedGeoConic.translate(vX, vY);
		} else {
			// just translate conic
			movedGeoConic.set(tempConic);
			movedGeoConic.translate(xRW - getStartPointX(), yRW
					- getStartPointY());
		}

		if (repaint) {
			movedGeoConic.updateRepaint();
		} else {
			movedGeoConic.updateCascade();
		}
	}

	protected final void moveImplicitPoly(boolean repaint) {
		movedGeoImplicitPoly.set(tempImplicitPoly);
		movedGeoImplicitPoly.translate(xRW - getStartPointX(), yRW
				- getStartPointY());

		// set points
		for (int i = 0; i < moveDependentPoints.size(); i++) {
			GeoPoint g = moveDependentPoints.get(i);
			g.setCoords2D(tempDependentPointX.get(i),
					tempDependentPointY.get(i), 1);
			if (tmpCoordsL3 == null) {
				tmpCoordsL3 = new Coords(3);
			}
			tmpCoordsL3.setX(xRW - getStartPointX());
			tmpCoordsL3.setY(yRW - getStartPointY());
			tmpCoordsL3.setZ(1);
			g.translate(tmpCoordsL3);
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
		// g.translate(new Coords(xRW - getStartPointX(), yRW -
		// getStartPointY()));
		// }
		// }else if (elem instanceof GeoImplicitPoly){
		//
		// }
		// }

	}

	double vertexX = Double.NaN, vertexY = Double.NaN;

	protected final void moveFreehand(boolean repaint) {

		movedGeoFunction.set(tempFunction);
		movedGeoFunction.translate(xRW - getStartPointX(), yRW
				- getStartPointY());

		setStartPointLocation(xRW, yRW);

		if (repaint) {
			movedGeoFunction.updateRepaint();
		} else {
			movedGeoFunction.updateCascade();
		}

	}

	private double initxRW = Double.NaN;
	private double initFactor = Double.NaN;

	protected final void moveFunction(boolean repaint) {

		boolean quadratic = false;

		if (isAltDown()) {
			if (!Double.isNaN(vertexX) && movedGeoFunction.isIndependent()) {
				quadratic = true;
			} else {

				// <Alt>drag eg sin(3x-4) to change frequency

				ExpressionNode en = movedGeoFunction.getFunction()
						.getExpression();

				if (Operation.isSimpleFunction(en.getOperation())) {
					ExpressionValue arg = en.getLeft();

					if (arg.isExpressionNode()) {

						ExpressionNode enArg = (ExpressionNode) arg;

						Operation op2 = enArg.getOperation();

						// check for sin(3x+2)
						if (op2.equals(Operation.PLUS)
								|| op2.equals(Operation.MINUS)) {
							ExpressionValue left = enArg.getLeft();
							ExpressionValue right = enArg.getRight();

							// eg sin(3-x)
							// sin(3-1x) OK though
							if (!(right.isExpressionNode())
									&& !(left.isExpressionNode())) {
								return;
							}

							if (left instanceof MyDouble) {
								enArg = (ExpressionNode) right;
								op2 = enArg.getOperation();
							} else if (right instanceof MyDouble) {
								enArg = (ExpressionNode) left;
								op2 = enArg.getOperation();
							} else {

								// not sin(linear expression)
								return;
							}

						}

						if (op2.equals(Operation.MULTIPLY)) {
							ExpressionValue left = enArg.getLeft();
							ExpressionValue right = enArg.getRight();

							// eg sin(x 4)
							if (left instanceof FunctionVariable
									&& right instanceof MyDouble) {
								// swap left and right
								ExpressionValue tmp = right;
								right = left;
								left = tmp;
							}

							// eg sin(4 x)
							if (right instanceof FunctionVariable
									&& left instanceof MyDouble) {

								if (Double.isNaN(initxRW)) {
									initxRW = xRW;
									initFactor = ((MyDouble) left).getDouble();
									return;
								}

								((MyDouble) left).set(initxRW / xRW
										* initFactor);
								movedGeoFunction.updateRepaint();
							}

						}
					}

				}

				return;
			}

		}

		if (quadratic) {
			double p = (yRW - vertexY) / ((xRW - vertexX) * (xRW - vertexX));

			// slow method, less code
			// GeoFunction geo =
			// kernel.getAlgebraProcessor().evaluateToFunction(p
			// +" * (x - "+vertexX+")^2 + "+vertexY , true);

			// fast method, doesn't use parser
			MyDouble a = new MyDouble(kernel, p);
			MyDouble h = new MyDouble(kernel, vertexX);
			MyDouble k = new MyDouble(kernel, vertexY);

			FunctionVariable fv = new FunctionVariable(kernel);
			ExpressionNode squareE = new ExpressionNode(kernel, fv,
					Operation.MINUS, h).power(new MyDouble(kernel, 2))
					.multiply(a).plus(k);
			Function squareF = new Function(squareE, fv);
			squareF.initFunction();
			GeoFunction square = new GeoFunction(kernel.getConstruction());
			square.setFunction(squareF);

			movedGeoFunction.set(square);
		} else {
			movedGeoFunction.set(tempFunction);
			movedGeoFunction.translate(xRW - getStartPointX(), yRW
					- getStartPointY());
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
				view.toScreenCoordX(xRW - getStartPointX()),
				view.toScreenCoordY(yRW - getStartPointY()));

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
		// AbstractApplication.printStacktrace("");
		// part of snap to grid code
		movedGeoButton.setAbsoluteScreenLoc(
				view.toScreenCoordX(xRW - getStartPointX()),
				view.toScreenCoordY(yRW - getStartPointY()));

		if (repaint) {
			movedGeoButton.updateRepaint();
		} else {
			movedGeoButton.updateCascade();
		}
	}

	protected final double getSliderValue(GeoNumeric movedSlider, boolean click) {
		double min = movedSlider.getIntervalMin();
		double max = movedSlider.getIntervalMax();
		double param;
		if (movedSlider.isSliderHorizontal()) {
			if (movedSlider.isAbsoluteScreenLocActive()) {
				param = mouseLoc.x - getStartPointX();
			} else {
				param = xRW - getStartPointX();
			}
		} else {
			if (movedSlider.isAbsoluteScreenLocActive()) {
				param = getStartPointY() - mouseLoc.y;
			} else {
				param = yRW - getStartPointY();
			}
		}
		// make sure we don't show eg 5.2 for slider <-5,5> in the hit treshold
		param = Math.max(0, Math.min(movedSlider.getSliderWidth(), param));
		param = (param * (max - min)) / movedSlider.getSliderWidth();

		// round to animation step scale
		param = Kernel.roundToScale(param, movedSlider.getAnimationStep());
		double val = min + param;

		if (movedSlider.getAnimationStep() > Kernel.MIN_PRECISION) {
			// round to decimal fraction, e.g. 2.800000000001 to 2.8
			val = Kernel.checkDecimalFraction(val);
		}

		if (movedSlider.isGeoAngle()) {

			val = Kernel.checkDecimalFraction(val * Kernel.CONST_180_PI)
					/ Kernel.CONST_180_PI;

		}

		if (!click) {
			// dragging with mouse
			return val;
		}

		// new behaviour from GeoGebra 4.2
		// clicking just moves slider up one "notch"
		// better for touch screens

		if (Kernel.isEqual(val, movedSlider.getValue())) {
			return val;
		}

		if (val > movedSlider.getValue()) {
			return Math.min(
					movedSlider.getValue() + movedSlider.getAnimationStep(),
					movedSlider.getIntervalMax());
		}

		return Math.max(
				movedSlider.getValue() - movedSlider.getAnimationStep(),
				movedSlider.getIntervalMin());
	}

	/**
	 * @param repaint
	 *            TODO ignored now -- on purpose ?
	 */
	protected final void moveNumeric(boolean repaint, boolean click) {

		double newVal = getSliderValue(movedGeoNumeric, click);
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
					view.toScreenCoordX(xRW - getStartPointX()),
					view.toScreenCoordY(yRW - getStartPointY()), temporaryMode);
		} else {
			movedGeoNumeric.setSliderLocation(xRW - getStartPointX(), yRW
					- getStartPointY(), temporaryMode);
		}

		// don't cascade, only position of the slider has changed
		movedGeoNumeric.update();

		if (repaint) {
			kernel.notifyRepaint();
		}
	}

	protected void moveDependent(boolean repaint) {

		translationVec.setX(xRW - getStartPointX());
		translationVec.setY(yRW - getStartPointY());

		setStartPointLocation(xRW, yRW);

		// we don't specify screen coords for translation as all objects are
		// Transformables
		kernel.movingGeoSet();
		if (tmpCoordsL3 == null) {
			tmpCoordsL3 = new Coords(3);
		}
		tmpCoordsL3.setX(xRW);
		tmpCoordsL3.setY(yRW);
		tmpCoordsL3.setZ(0);
		GeoElement.moveObjects(translateableGeos, translationVec, tmpCoordsL3,
				null, view);
		kernel.movedGeoSet(translateableGeos);
		if (repaint) {
			kernel.notifyRepaint();
		}
	}

	protected final void moveAttached(boolean repaint) {

		AlgoElement algo = movedGeoElement.getParentAlgorithm();
		GeoPoint pt1 = (GeoPoint) algo.getInput()[4];
		GeoPoint pt2 = (GeoPoint) algo.getInput()[5];
		double dx = view.getXscale() * (xRW - getStartPointX());
		double dy = view.getYscale() * (yRW - getStartPointY());
		setStartPointLocation(xRW, yRW);
		pt1.setCoords(pt1.getX() + dx, pt1.getY() - dy, 1);
		pt2.setCoords(pt2.getX() + dx, pt2.getY() - dy, 1);
		algo.update();

		if (repaint) {
			kernel.notifyRepaint();
		}
	}

	protected void moveMultipleObjects(boolean repaint) {
		translationVec.setX(xRW - getStartPointX());
		translationVec.setY(yRW - getStartPointY());
		setStartPointLocation(xRW, yRW);
		startLoc = mouseLoc;

		// move all selected geos
		if (tmpCoordsL3 == null) {
			tmpCoordsL3 = new Coords(3);
		}
		tmpCoordsL3.setX(xRW);
		tmpCoordsL3.setY(yRW);
		tmpCoordsL3.setZ(0);
		GeoElement.moveObjects(
				companion.removeParentsOfView(getAppSelectedGeos()),
				translationVec, tmpCoordsL3, null, view);
		if (repaint) {
			kernel.notifyRepaint();
		}
	}

	protected double getStartPointX() {
		return startPoint.x;
	}

	protected double getStartPointY() {
		return startPoint.y;
	}

	public void setMovedGeoPoint(GeoElement geo) {
		movedGeoPoint = (GeoPointND) geo;

		AlgoElement algo = ((GeoElement) movedGeoPoint).getParentAlgorithm();
		if ((algo != null) && (algo instanceof AlgoDynamicCoordinatesInterface)) {
			movedGeoPoint = ((AlgoDynamicCoordinatesInterface) algo)
					.getParentPoint();
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
		case EuclidianConstants.MODE_ATTACH_DETACH:
			// removed: polygons can still be selected if they are the only
			// object clicked on
			// case EuclidianView.MODE_INTERSECT:
			// case EuclidianView.MODE_INTERSECTION_CURVE:
			break;
		case EuclidianConstants.MODE_MOVE:
			hits.removePolygonsIfSidePresent();
			break;
		default:
			hits.removePolygons();
			break;
		}
	}

	protected boolean switchModeForMouseReleased(int evMode, Hits hitsReleased,
			boolean kernelChanged, boolean controlDown, PointerEventType type) {
		Hits hits = hitsReleased;
		boolean changedKernel = kernelChanged;
		boolean focusNeeded = true;
		switch (evMode) {
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
		case EuclidianConstants.MODE_DILATE_FROM_POINT:
		case EuclidianConstants.MODE_MIRROR_AT_POINT:
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds
														// 2008-03-23
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			setViewHits(type);
			hits = view.getHits();
			hits.removePolygons();
			// hits = view.getHits(mouseLoc);
			if (hits.isEmpty()) {
				changedKernel = createNewPoint(hits, false, false, true);
			} else {
				changedKernel = (pointCreated != null);
			}
			break;

		case EuclidianConstants.MODE_BUTTON_ACTION:
		case EuclidianConstants.MODE_TEXTFIELD_ACTION:
			// make sure script not triggered
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
		// case EuclidianConstants.MODE_VISUAL_STYLE:
		case EuclidianConstants.MODE_TRANSLATEVIEW:
			if (draggingOccured || !temporaryMode) {
				changedKernel = true;

			} else {
				// Ctrl pressed, we need to select a point
				setViewHits(type);
				handleSelectClick(view.getHits().getTopHits(),// view.getTopHits(mouseLoc),
						controlDown);
			}
			break;
		case EuclidianConstants.MODE_MOVE:
		case EuclidianConstants.MODE_SELECTION_LISTENER:
			// handle selection click
			setViewHits(type);
			handleSelectClick(view.getHits().getTopHits(),// view.getTopHits(mouseLoc),
					controlDown);
		default:

			// change checkbox (boolean) state on mouse up only if there's been
			// no drag
			setViewHits(type);
			hits = view.getHits().getTopHits();
			// hits = view.getTopHits(mouseLoc);
			if (!hits.isEmpty()) {
				GeoElement hit = hits.get(0);
				if (hit != null) {
					if (hit.isGeoButton() && !(hit.isGeoTextField())) {
						checkBoxOrButtonJustHitted = true;
						selection.removeSelectedGeo(hit, true, false); // make
																		// sure
																		// doesn't
																		// get
																		// selected
						app.updateSelection(false);
					} else if (hit.isGeoBoolean()) {
						GeoBoolean bool = (GeoBoolean) (hits.get(0));
						if (!isCheckboxFixed(bool)) { // otherwise changed on
														// mouse
							// down
							hitCheckBox(bool);
							selection.removeSelectedGeo(bool, true, false); // make
																			// sure
																			// doesn't
																			// get
																			// selected
							app.updateSelection(false);
							bool.updateCascade();
						}
					} else {
						GeoElement geo1 = chooseGeo(hits, true);
						// ggb3D : geo1 may be null if it's axes or xOy plane
						if (geo1 != null) {
							focusNeeded = false;
							runScriptsIfNeeded(geo1);
						}
					}
				}
			}
		}
		if (focusNeeded && mode != EuclidianConstants.MODE_SELECTION_LISTENER) {
			view.requestFocusInWindow();
		}

		return changedKernel;
	}

	private boolean checkBoxOrButtonJustHitted = false;

	protected boolean penDragged;

	protected void hitCheckBox(GeoBoolean bool) {
		bool.setValue(!bool.getBoolean());
		this.checkboxChangeOccured = true;
		this.checkBoxOrButtonJustHitted = true;
	}

	protected Hits addPointCreatedForMouseReleased(Hits releasedHits) {
		Hits hits = releasedHits;
		if (hits.isEmpty()) {
			hits = new Hits();
			hits.add((GeoElement) pointCreated);
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
			view.setToolTipText(l10n.getPlainTooltip("resetConstruction"));
			view.setHitCursor();
			return;
		}

		// animation button
		boolean hitAnimationButton = view.hitAnimationButton(event.getX(),
				event.getY());
		repaintNeeded = view.setAnimationButtonsHighlighted(hitAnimationButton);
		if (hitAnimationButton) {
			if (kernel.isAnimationPaused()) {
				view.setToolTipText(l10n.getPlainTooltip("Play"));
			} else {
				view.setToolTipText(l10n.getPlainTooltip("Pause"));
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
		GeoElement geo = view.getLabelHit(mouseLoc, event.getType());
		if (geo != null) {
			mouseIsOverLabel = true;
		} else {
			mouseIsOverLabel = false;
		}
		if (moveMode(mode)) { // label hit in move mode: block all other hits
			if (geo != null) {
				noHighlighting = true;
				tempArrayList.clear();
				tempArrayList.add(geo);
				hits = tempArrayList;
			}
		}

		if (hits.isEmpty()) {
			setViewHits(event.getType());
			hits = view.getHits();
			switchModeForRemovePolygons(hits);
		}

		if (hits.isEmpty()) {
			view.setToolTipText(null);
			view.setDefaultCursor();
		} else {
			if ((event.isShiftDown() || mode == EuclidianConstants.MODE_TRANSLATEVIEW)
					&& (hits.size() >= 1)) {
				if (hits.hasXAxis()) {
					view.setResizeXAxisCursor();
				} else if (hits.hasYAxis()) {
					view.setResizeYAxisCursor();
				} else {
					view.setHitCursor();
				}
			} else {
				view.setHitCursor();
			}
		}

		// for testing: save the full hits for later use
		Hits tempFullHits = hits.clone();

		// set tool tip text
		// the tooltips are only shown if algebra view is visible
		// if (app.isUsingLayout() && app.getGuiManager().showAlgebraView()) {
		// hits = view.getTopHits(hits);

		hits = hits.getTopHits();
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

					setStartPointLocation(((GeoNumeric) hit).getSliderX(),
							((GeoNumeric) hit).getSliderY());

					// boolean valueShowing = hit.isLabelVisible()
					// && (hit.getLabelMode() == GeoElement.LABEL_NAME_VALUE ||
					// hit.getLabelMode() == GeoElement.LABEL_VALUE);

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

		startCollectingMinorRepaints();
		boolean control = app.isControlDown(event);
		if (noHighlighting ? refreshHighlighting(null, control)
				: refreshHighlighting(tempFullHits, control)) {

			kernel.notifyRepaint();
		} else if (repaintNeeded) {
			view.repaintView();
		}
		stopCollectingMinorRepaints();
	}

	public void wrapMouseMoved(AbstractEvent event) {
		if (isTextfieldHasFocus()) {
			return;
		}

		setMouseLocation(event);

		processMouseMoved(event);

	}

	protected abstract void resetToolTipManager();

	public void wrapMouseExited(AbstractEvent event) {
		if (isTextfieldHasFocus()) {
			return;
		}

		app.storeUndoInfoIfSetCoordSystemOccured();

		startCollectingMinorRepaints();
		refreshHighlighting(null, app.isControlDown(event));
		resetToolTipManager();
		view.setAnimationButtonsHighlighted(false);
		view.setShowMouseCoords(false);
		mouseLoc = null;
		kernel.notifyRepaint();
		stopCollectingMinorRepaints();
		view.mouseExited();

	}

	protected void handleSelectClick(ArrayList<GeoElement> geos,
			boolean ctrlDown) {
		if (geos == null) {
			selection.clearSelectedGeos();
		} else {
			if (ctrlDown) {
				// boolean selected = geo.is
				selection.toggleSelectedGeo(chooseGeo(geos, true));
				// app.geoElementSelected(geo, true); // copy definiton to input
				// bar
			} else {
				if (!moveModeSelectionHandled) {
					GeoElement geo = chooseGeo(geos, true);
					if (geo != null) {
						selection.clearSelectedGeos(false);
						selection.addSelectedGeo(geo);
					}
				}
			}
		}
	}

	// make sure scripts not run twice
	private boolean scriptsHaveRun = false;

	private boolean doubleClickStarted;

	private GPoint lastMouseUpLoc;

	private boolean checkboxChangeOccured = false;

	protected void wrapMouseclicked(boolean control, int clickCount,
			PointerEventType type) {

		// double-click on object selects MODE_MOVE and opens redefine dialog
		if (clickCount == 2) {
			if (app.isApplet() || control) {
				return;
			}

			selection.clearSelectedGeos(true, false);
			app.updateSelection(false);
			// hits = view.getTopHits(mouseLoc);
			setViewHits(type);
			Hits hits = view.getHits().getTopHits();
			switchModeForRemovePolygons(hits);
			if (!hits.isEmpty()) {
				app.setMode(EuclidianConstants.MODE_MOVE);
				GeoElement geo0 = hits.get(0);

				// if (app.isUsingFullGui() && app.getGuiManager() != null) {
				if (geo0.isGeoNumeric() && ((GeoNumeric) geo0).isSlider()) {
					// double-click slider -> Object Properties
					getDialogManager().showPropertiesDialog(hits);
				} else if (!geo0.isFixed()
						&& !(geo0.isGeoBoolean() && geo0.isIndependent())
						&& !(geo0.isGeoImage() && geo0.isIndependent())
						&& !geo0.isGeoButton()) {
					getDialogManager().showRedefineDialog(hits.get(0), true);
				}
				// }
			}
		}

	}

	/**
	 * @param gPoint
	 *            point
	 */
	public boolean textfieldJustFocusedW(int x, int y, PointerEventType type) {
		return false;
	}

	public void resetMovedGeoPoint() {
		movedGeoPoint = null;
	}

	public void setStartPointLocation() {
		setStartPointLocation(xRW, yRW);
	}

	public void setStartPointLocationWithOrigin(double x, double y) {
		setStartPointLocation(xRW - x, yRW - y);
	}

	protected void handleMovedElementMultiple() {
		moveMode = MOVE_MULTIPLE_OBJECTS;
		setStartPointLocation();
		startLoc = mouseLoc;
		view.setDragCursor();
		if (translationVec == null) {
			translationVec = new Coords(2);
		}
	}

	public void handleMovedElement(GeoElement geo, boolean multiple,
			PointerEventType type) {

		resetMovedGeoPoint();
		movedGeoElement = geo;

		// default if nothing matches
		moveMode = MOVE_NONE;

		// multiple geos selected
		if ((movedGeoElement != null) && multiple) {
			handleMovedElementMultiple();
		}

		// DEPENDENT object: changeable parents?
		// move free parent points (e.g. for segments)
		else if (!movedGeoElement.isMoveable(view)) {
			handleMovedElementDependent();
		}

		// free point
		else {
			handleMovedElementFree(type);
		}
	}

	protected boolean handleMovedElementDependentWithChangeableCoordParentNumbers() {
		// geo with changeable coord parent numbers
		if (movedGeoElement.hasChangeableCoordParentNumbers()) {
			movedGeoElement.recordChangeableCoordParentNumbers();
			translateableGeos = new ArrayList<GeoElement>();
			translateableGeos.add(movedGeoElement);
			return true;
		}

		return false;
	}

	protected void handleMovedElementDependent() {
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

				if (input[1].isIndependent()) {
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
		} else if (movedGeoElement.isGeoSegment() || movedGeoElement.isGeoRay()
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
				if ((input[1].isIndependent() || input[1].getParentAlgorithm() instanceof AlgoVectorPoint)
						&& input[1] instanceof GeoVectorND) {
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

			if (sameVector
					&& ((vec.getLabelSimple() == null) || vec.isIndependent())) {
				transformCoordsOffset[0] = xRW - vec.x;
				transformCoordsOffset[1] = yRW - vec.y;
				movedGeoVector = vec;
				moveMode = MOVE_VECTOR_NO_GRID;
				return;
			}
		}

		// STANDARD case: get free input points of dependent movedGeoElement
		if (!handleMovedElementDependentWithChangeableCoordParentNumbers()
				&& movedGeoElement.hasMoveableInputPoints(view)) {
			// allow only moving of the following object types
			if (movedGeoElement.isGeoLine() || movedGeoElement.isGeoPolygon()
					|| (movedGeoElement instanceof GeoPolyLine)
					|| movedGeoElement.isGeoConic()
					|| movedGeoElement.isGeoImage()
					|| movedGeoElement.isGeoList()
					|| movedGeoElement.isGeoVector()) {
				if (translateableGeos == null)
					translateableGeos = new ArrayList<GeoElement>();
				else
					translateableGeos.clear();

				addMovedGeoElementFreeInputPointsToTranslateableGeos();

				if (movedGeoElement.isGeoList())
					translateableGeos.add(movedGeoElement);
			}
		}

		handleMovedElementDependentInitMode();
	}

	protected void handleMovedElementDependentInitMode() {
		// init move dependent mode if we have something to move ;-)
		if (translateableGeos != null && translateableGeos.size() > 0) {
			moveMode = MOVE_DEPENDENT;

			if (translateableGeos.get(0).isGeoPoint()) {
				GeoPointND point = ((GeoPointND) translateableGeos.get(0));
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

	final protected boolean handleMovedElementFreePoint() {
		if (movedGeoElement.isGeoPoint()) {
			moveMode = MOVE_POINT;
			setMovedGeoPoint(movedGeoElement);
			// make sure snap-to-grid works after e.g. pressing a button
			transformCoordsOffset[0] = 0;
			transformCoordsOffset[1] = 0;
			return true;
		}

		return false;
	}

	final protected boolean handleMovedElementFreeText() {

		if (movedGeoElement.isGeoText()) {
			moveMode = MOVE_TEXT;
			movedGeoText = (GeoText) movedGeoElement;
			view.setShowMouseCoords(false);
			view.setDragCursor();

			if (movedGeoText.isAbsoluteScreenLocActive()) {
				oldLoc.setLocation(movedGeoText.getAbsoluteScreenLocX(),
						movedGeoText.getAbsoluteScreenLocY());
				startLoc = mouseLoc;

				// part of snap to grid code - buggy, so commented out
				// setStartPointLocation(xRW -
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

			return true;
		}

		return false;
	}

	protected void handleMovedElementFree(PointerEventType type) {

		if (handleMovedElementFreePoint()) {
			return;
		}

		// free line
		else if (movedGeoElement.isGeoLine()) {
			moveMode = MOVE_LINE;
			movedGeoLine = (GeoLineND) movedGeoElement;
			view.setShowMouseCoords(true);
			view.setDragCursor();
		}

		// free vector
		else if (movedGeoElement.isGeoVector()) {
			movedGeoVector = (GeoVectorND) movedGeoElement;

			// change vector itself or move only startpoint?
			// if vector is dependent or
			// mouseLoc is closer to the startpoint than to the end
			// point
			// then move the startpoint of the vector
			if (movedGeoVector.hasAbsoluteLocation()) {
				GeoPointND sP = movedGeoVector.getStartPoint();
				double sx = 0;
				double sy = 0;
				if (sP != null) {
					Coords c = view.getCompanion().getCoordsForView(sP);
					sx = c.getX();
					sy = c.getY();
				}
				// if |mouse - startpoint| < 1/2 * |vec| then move
				// startpoint
				Coords vCoords = view.getCoordsForView(movedGeoVector
						.getCoordsInD3());
				if ((2d * MyMath.length(xRW - sx, yRW - sy)) < MyMath.length(
						vCoords.getX(), vCoords.getY())) { // take
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
		else if (handleMovedElementFreeText()) {
			return;
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

				AlgoFunctionFreehand algo = (AlgoFunctionFreehand) movedGeoElement
						.getParentAlgorithm();

				GeoElement input = algo.getInput()[0];
				if (!algo.getInput()[0].isLabelSet()
						&& input.getParentAlgorithm() == null) {
					moveMode = MOVE_FREEHAND;
					movedGeoFunction = (GeoFunction) movedGeoElement;
				}

			} else if (movedGeoElement.isIndependent()) {
				moveMode = MOVE_FUNCTION;

				movedGeoFunction = (GeoFunction) movedGeoElement;
				vertexX = Double.NaN;
				vertexY = Double.NaN;

				initxRW = Double.NaN;
				initFactor = Double.NaN;

				if (movedGeoFunction.getFunction()
						.getSymbolicPolynomialFactors(false, true) != null) {
					LinkedList<PolyFunction> factors = movedGeoFunction
							.getFunction().getPolynomialFactors(false);
					if (factors.size() == 1 && factors.get(0).getDegree() == 2) {
						double c = movedGeoFunction.evaluate(0);
						double s = movedGeoFunction.evaluate(1);
						double a = 0.5 * (s + movedGeoFunction.evaluate(-1))
								- c;
						double b = s - a - c;

						// cordinates of vertex (just calculated once)
						// used for alt-drag as well
						vertexX = -b / a / 2.0;
						vertexY = -(b * b - 4.0 * a * c) / (4.0 * a);

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
		else if (movedGeoElement.isGeoNumeric()
				&& movedGeoElement.getParentAlgorithm() == null) {
			movedGeoNumeric = (GeoNumeric) movedGeoElement;
			moveMode = MOVE_NUMERIC;

			DrawableND d = view.getDrawableFor(movedGeoNumeric);
			if (d instanceof DrawSlider && movedGeoElement.isEuclidianVisible()
					&& mouseLoc != null) {
				// otherwise using Move Tool -> move dot
				if (isMoveSliderExpected(app.getCapturingThreshold(type))) {
					moveMode = MOVE_SLIDER;
					if (movedGeoNumeric.isAbsoluteScreenLocActive()) {
						oldLoc.setLocation(
								movedGeoNumeric.getAbsoluteScreenLocX(),
								movedGeoNumeric.getAbsoluteScreenLocY());
						startLoc = mouseLoc;

						// part of snap to grid code
						setStartPointLocation(
								xRW - view.toRealWorldCoordX(oldLoc.x), yRW
										- view.toRealWorldCoordY(oldLoc.y));
						transformCoordsOffset[0] = view
								.toRealWorldCoordX(oldLoc.x) - xRW;
						transformCoordsOffset[1] = view
								.toRealWorldCoordY(oldLoc.y) - yRW;
					} else {
						setStartPointLocation(
								xRW - movedGeoNumeric.getRealWorldLocX(), yRW
										- movedGeoNumeric.getRealWorldLocY());
						transformCoordsOffset[0] = movedGeoNumeric
								.getRealWorldLocX() - xRW;
						transformCoordsOffset[1] = movedGeoNumeric
								.getRealWorldLocY() - yRW;
					}
				} else {
					setStartPointLocation(movedGeoNumeric.getSliderX(),
							movedGeoNumeric.getSliderY());

					// update straightaway in case it's just a click (no drag)
					moveNumeric(true, true);
				}
			}

			view.setShowMouseCoords(false);
			view.setDragCursor();
		}

		// checkbox
		else if (movedGeoElement.isGeoBoolean()) {
			movedGeoBoolean = (GeoBoolean) movedGeoElement;

			// if fixed checkbox dragged, behave as if it's been clicked
			// important for electronic whiteboards / tablets
			if (isCheckboxFixed(movedGeoBoolean)) {
				movedGeoBoolean.setValue(!movedGeoBoolean.getBoolean());
				selection.removeSelectedGeo(movedGeoBoolean); // make sure
																// doesn't get
				// selected
				movedGeoBoolean.updateCascade();
				this.checkboxChangeOccured = true;
			}

			// move checkbox
			moveMode = MOVE_BOOLEAN;
			startLoc = mouseLoc;
			oldLoc.x = movedGeoBoolean.getAbsoluteScreenLocX();
			oldLoc.y = movedGeoBoolean.getAbsoluteScreenLocY();

			// part of snap to grid code (the constant 5 comes from DrawBoolean)
			setStartPointLocation(xRW - view.toRealWorldCoordX(oldLoc.x), yRW
					- view.toRealWorldCoordY(oldLoc.y));
			transformCoordsOffset[0] = view.toRealWorldCoordX(oldLoc.x + 5)
					- xRW;
			transformCoordsOffset[1] = view.toRealWorldCoordY(oldLoc.y + 5)
					- yRW;

			view.setShowMouseCoords(false);
			view.setDragCursor();

		}

		// button
		else if (movedGeoElement instanceof Furniture
				&& ((Furniture) movedGeoElement).isFurniture()) {

			// for applets:
			// allow buttons to be dragged only if the button tool is selected
			// (important for tablets)
			if (!app.isApplet() || temporaryMode) {
				// ie Button Mode is really selected

				movedGeoButton = (Furniture) movedGeoElement;
				// move button
				moveMode = MOVE_BUTTON;
				startLoc = mouseLoc;
				oldLoc.x = movedGeoButton.getAbsoluteScreenLocX();
				oldLoc.y = movedGeoButton.getAbsoluteScreenLocY();

				// part of snap to grid code
				setStartPointLocation(xRW - view.toRealWorldCoordX(oldLoc.x),
						yRW - view.toRealWorldCoordY(oldLoc.y));
				transformCoordsOffset[0] = view.toRealWorldCoordX(oldLoc.x)
						- xRW;
				transformCoordsOffset[1] = view.toRealWorldCoordY(oldLoc.y)
						- yRW;

				view.setShowMouseCoords(false);
				view.setDragCursor();
			} else {
				// need to trigger scripts
				// (on tablets only get drag events)
				// we don't want to run InputBox's script if it was just clicked
				if (!(movedGeoElement instanceof GeoTextField)) {
					runScriptsIfNeeded(movedGeoElement);
				}
			}

		}

		// image
		else if (movedGeoElement.isGeoImage()
				&& movedGeoElement.isMoveable(view)) {
			moveMode = MOVE_IMAGE;
			movedGeoImage = (GeoImage) movedGeoElement;
			view.setShowMouseCoords(false);
			view.setDragCursor();

			if (movedGeoImage.isAbsoluteScreenLocActive()) {
				oldLoc.setLocation(movedGeoImage.getAbsoluteScreenLocX(),
						movedGeoImage.getAbsoluteScreenLocY());
				startLoc = mouseLoc;

				// part of snap to grid code
				setStartPointLocation(xRW - view.toRealWorldCoordX(oldLoc.x),
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

	private void addMovedGeoElementFreeInputPointsToTranslateableGeos() {
		ArrayList<GeoPointND> freeInputPoints = movedGeoElement
				.getFreeInputPoints(view);
		for (GeoPointND p : freeInputPoints) {
			translateableGeos.add((GeoElement) p);
		}
	}

	/**
	 * checks wheter the slider itself or the point of the slider should be
	 * moved
	 * 
	 * @return true if the slider should be moved; false if the point on the
	 *         slider should be moved (i.e. change the number)
	 */
	protected boolean isMoveSliderExpected(int hitThreshold) {
		DrawSlider ds = (DrawSlider) view.getDrawableFor(movedGeoNumeric);
		// TEMPORARY_MODE true -> dragging slider using Slider Tool
		// or right-hand mouse button
		return ((temporaryMode && app.isRightClickEnabled()) || !movedGeoNumeric
				.isSliderFixed())
				&& !ds.hitPoint(mouseLoc.x, mouseLoc.y, hitThreshold)
				&& ds.hitSlider(mouseLoc.x, mouseLoc.y, hitThreshold);
	}

	protected void setStartPointLocation(double x, double y) {
		startPoint.setLocation(x, y);

	}

	/**
	 * Dragging a fixed checkbox should change its state (important for EWB etc)
	 * 
	 * Also for iPads etc HTML5: don't allow dragging unless we have a GUI
	 */
	private boolean isCheckboxFixed(GeoBoolean geoBool) {
		return geoBool.isCheckboxFixed()
				|| (app.isHTML5Applet() && !App.isFullAppGui());
	}

	protected void updateSelectionRectangle(boolean keepScreenRatio) {
		if (view.getSelectionRectangle() == null) {
			view.setSelectionRectangle(geogebra.common.factories.AwtFactory.prototype
					.newRectangle(0, 0));
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
				rect.setLocation(selectionStartPoint.x, selectionStartPoint.y);
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

	protected boolean isDraggingBeyondThreshold() {
		return mouseLoc != null
				&& (Math.abs(mouseLoc.x - selectionStartPoint.x) > DRAG_THRESHOLD || Math
						.abs(mouseLoc.y - selectionStartPoint.y) > DRAG_THRESHOLD);
	}

	/**
	 * @return true, if the freehand mode is prepared (e.g. polygons, circle)
	 */
	protected boolean freehandModePrepared() {
		return false;
	}

	protected void handleMouseDragged(boolean repaint, AbstractEvent event) {
		startCollectingMinorRepaints();
		if (!draggingBeyondThreshold && isDraggingBeyondThreshold()) {
			draggingBeyondThreshold = true;
		}
		if (freehandModePrepared()) {
			stopCollectingMinorRepaints();
			// no repaint, so that the line drawn by the freehand mode will not
			// disappear
			return;
		}
		if (draggingBeyondThreshold && mode == EuclidianConstants.MODE_DELETE) {

			getDeleteMode().handleMouseDraggedForDelete(event,
					getDeleteToolSize(), false);

			kernel.notifyRepaint();
			stopCollectingMinorRepaints();

			return;
		}
		// moveMode was set in mousePressed()
		switch (moveMode) {
		case MOVE_ROTATE:
			rotateObject(repaint);
			break;

		case MOVE_POINT:
			companion.movePoint(repaint, event);
			break;

		case MOVE_POINT_WITH_OFFSET:
			movePointWithOffset(repaint);
			break;

		case MOVE_ATTACH_DETACH:
			moveAttachDetach(repaint, event);
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

			moveNumeric(repaint, false);
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
			if (movedGeoElement.getParentAlgorithm() != null
					&& movedGeoElement.getParentAlgorithm().getClassName() == Commands.AttachCopyToView) {
				moveAttached(repaint);
			} else {
				moveDependent(repaint);
			}
			break;

		case MOVE_PLANE:
			companion.movePlane(repaint, event);
			break;

		case MOVE_MULTIPLE_OBJECTS:
			moveMultipleObjects(repaint);
			break;

		case MOVE_VIEW:
			if (repaint) {
				if (temporaryMode
						&& mode != EuclidianConstants.MODE_TRANSLATEVIEW) {
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
				if (temporaryMode) {
					view.setResizeXAxisCursor();
				}

				// check if zero is on the screen
				double xzero = view.getXZero();
				double xzeroRW = 0;
				double newXZero = xzero;
				if (xzero < 0) {
					xzero = 0;
					xzeroRW = view.getXmin();
				} else if (xzero > view.getWidth()) {
					xzero = view.getWidth();
					xzeroRW = view.getXmax();
				}

				// take care when we get close to the origin
				if (Math.abs(mouseLoc.x - xzero) < 2) {
					mouseLoc.x = (int) Math
							.round(mouseLoc.x > xzero ? xzero + 2 : xzero - 2);
				}
				double xscale = (mouseLoc.x - xzero) / (xTemp - xzeroRW);

				// move zero if off screen
				if (newXZero < 0) {
					newXZero = -xzeroRW * xscale;
				} else if (newXZero > view.getWidth()) {
					newXZero = view.getWidth() - xzeroRW * xscale;
				}

				view.setCoordSystem(newXZero, view.getYZero(), xscale,
						view.getYscale());
			}
			break;

		case MOVE_Y_AXIS:
			if (repaint) {
				if (temporaryMode) {
					view.setResizeYAxisCursor();
				}

				// check if zero is on the screen
				double yzero = view.getYZero();
				double yzeroRW = 0;
				double newYZero = yzero;
				if (yzero < 0) {
					yzero = 0;
					yzeroRW = view.getYmax();
				} else if (yzero > view.getHeight()) {
					yzero = view.getHeight();
					yzeroRW = view.getYmin();
				}

				// take care when we get close to the origin
				if (Math.abs(mouseLoc.y - yzero) < 2) {
					mouseLoc.y = (int) Math.round(mouseLoc.y > yzero ? view
							.getYZero() + 2 : yzero - 2);
				}
				double yscale = (yzero - mouseLoc.y) / (yTemp - yzeroRW);

				// move zero if off screen
				if (newYZero < 0) {
					newYZero = yzeroRW * yscale;
				} else if (newYZero > view.getHeight()) {
					newYZero = view.getHeight() + yzeroRW * yscale;
				}
				view.setCoordSystem(view.getXZero(), newYZero,
						view.getXscale(), yscale);
			}
			break;

		default: // do nothing
		}
		stopCollectingMinorRepaints();
		kernel.notifyRepaint();
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
			// case EuclidianConstants.MODE_VISUAL_STYLE:
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

	final protected void handleMousePressedForMoveMode(AbstractEvent e,
			boolean drag) {

		// fix for meta-click to work on Mac/Linux
		if (app.isControlDown(e)) {
			return;
		}

		// move label?
		GeoElement geo = view.getLabelHit(mouseLoc, e.getType());
		if (geo != null) {
			moveMode = MOVE_LABEL;
			movedLabelGeoElement = geo;
			oldLoc.setLocation(geo.labelOffsetX, geo.labelOffsetY);
			startLoc = mouseLoc;
			view.setDragCursor();
			return;
		}

		// find and set movedGeoElement
		setViewHits(e.getType());
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

		ArrayList<GeoElement> selGeos = getAppSelectedGeos();

		// if object was chosen before, take it now!
		if ((selGeos.size() == 1) && !hits.isEmpty()
				&& hits.contains(selGeos.get(0))) {
			// object was chosen before: take it
			geo = selGeos.get(0);
		} else {
			// choose out of hits
			geo = chooseGeo(hits, false);

			if (!selGeos.contains(geo)) {
				// repaint done next step, no update for properties view (will
				// display ev properties)
				selection.clearSelectedGeos(geo == null, false);
				selection.updateSelection(false);
				selection.addSelectedGeo(geo, true, true);
				// app.geoElementSelected(geo, false); // copy definiton to
				// input bar
			}
		}

		Hits th = viewHits.getTopHits();
		// make sure dragging a fixed eg button triggers the scripts
		// important for tablets, IWBs
		if (geo == null && th.size() > 0) {

			geo = th.get(0);

			if (geo.isFixed()) {
				runScriptsIfNeeded(geo);
				moveMode = MOVE_NONE;
				resetMovedGeoPoint();
				return;

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

		handleMovedElement(geo, selGeos.size() > 1, e.getType());

		view.repaintView();
	}

	protected boolean shouldCancelDrag() {
		if (System.currentTimeMillis() < EuclidianConstants.DRAGGING_DELAY
				+ lastMousePressedTime) {
			// we wait at least DRAGGING_DELAY (100ms) before starting drag
			// used for interactive boards
			return !EuclidianView.isPenMode(mode);
		}
		return false;
	}

	public void wrapMouseDragged(AbstractEvent event, boolean startCapture) {
		
		if (shouldCancelDrag()) {
			return;
		}

		scriptsHaveRun = false;

		if (isTextfieldHasFocus() && moveMode != MOVE_BUTTON) {
			return;
		}
		if (pressedButton != null) {
			pressedButton.setDraggedOrContext(true);
		}
		if (penMode(mode)) {
			penDragged = true;
			getPen().handleMouseDraggedForPenMode(event);
			return;
		}

		clearJustCreatedGeos();
		if (!draggingOccured) {

			draggingOccured = true;

			// make sure dragging triggers reset/play/pause
			// needed for tablets
			if (hitResetIcon()) {
				app.reset();
				return;
			} else if (view.hitAnimationButton(event.getX(), event.getY())) {
				if (kernel.isAnimationRunning()) {
					kernel.getAnimatonManager().stopAnimation();
				} else {
					kernel.getAnimatonManager().startAnimation();
				}
				view.repaintView();
				app.setUnsaved();
				return;
			}

			if ((mode == EuclidianConstants.MODE_TRANSLATE_BY_VECTOR)
					&& (selGeos() == 0)) {
				translateHitsByVector(event.getType());
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
				setViewHits(event.getType());
				GeoElement geo0 = null;
				Hits hits0 = view.getHits();
				if (!hits0.isEmpty()){
					geo0 = hits0.get(0);
				}
				if (!app.showToolBar()
						&& geo0 != null
						&& (geo0.isGeoTextField() || geo0.isGeoBoolean()
								|| geo0.isGeoButton() || (geo0.isGeoNumeric() && ((GeoNumeric) geo0)
								.isSlider()))) {
					draggingOccured = false;
					return;
				}
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
					temporaryMode = true;
					oldMode = mode; // remember current mode
					view.setMode(EuclidianConstants.MODE_MOVE);
					handleMousePressedForMoveMode(event, true);

					// make sure that dragging doesn't deselect the geos
					dontClearSelection = true;

					return;
				}

			}
			if (!app.isRightClickEnabled()) {
				return;
				// Michael Borcherds 2007-10-07
			}

			if (mode == EuclidianConstants.MODE_MOVE_ROTATE) {
				selection.clearSelectedGeos(false);
				selection.addSelectedGeo(rotationCenter, false, true);
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
			if ((!temporaryMode)
					|| (view.getHits().size() == 0)
					|| !view.getHits().get(0).isMoveable(view)
					|| (!view.getHits().get(0).isGeoPoint() && view.getHits()
							.get(0).hasDrawable3D())) {
				if (processRotate3DView()) { // in 2D view, return false
					if (temporaryMode) {
						temporaryMode = false;
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
			// if (app.isHTML5Applet() && !App.isFullAppGui()) {

			// alternative: could make drag move the view?
			// TEMPORARY_MODE = true;
			// oldMode = mode; // remember current mode
			// view.setMode(EuclidianConstants.MODE_TRANSLATEVIEW);

			// }
			// zoom rectangle (right drag) or selection rectangle (left drag)
			// Michael Borcherds 2007-10-07 allow dragging with right mouse
			// button
			// else
			if (app.isSelectionRectangleAllowed()
					&& ((app.isRightClick(event)) || allowSelectionRectangle())
					&& !temporaryMode) {
				// Michael Borcherds 2007-10-07
				// set zoom rectangle's size
				// right-drag: zoom
				// Shift-right-drag: zoom without preserving aspect ratio
				if (app.isPrerelease()) {
					updateSelectionRectangle(false);
				} else {
					updateSelectionRectangle((app.isRightClick(event) && !event
							.isShiftDown())
							// MACOS:
							// Cmd-left-drag: zoom
							// Cmd-shift-left-drag: zoom without preserving
							// aspect ratio
							|| (app.isMacOS() && app.isControlDown(event)
									&& !event.isShiftDown() && !app
										.isRightClick(event))
							|| view.isLockedAxesRatio());
				}
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

	private void translateHitsByVector(PointerEventType type) {
		setViewHits(type);

		Hits hits = view.getHits().getTopHits();

		GeoElement topHit = hits.get(0);

		if (topHit.isGeoVector()) {

			if ((topHit.getParentAlgorithm() instanceof AlgoVector)) { // Vector[A,B]
				AlgoVector algo = (AlgoVector) topHit.getParentAlgorithm();
				GeoPointND p = algo.getInputPoints().get(0);
				GeoPointND q = algo.getInputPoints().get(1);
				checkZooming();

				GeoVector vec = getAlgoDispatcher().Vector(null, 0, 0);
				vec.setEuclidianVisible(false);
				vec.setAuxiliaryObject(true);
				GeoElement[] pp = getAlgoDispatcher().Translate(null,
						(GeoElement) p, vec);
				GeoElement[] qq = getAlgoDispatcher().Translate(null,
						(GeoElement) q, vec);
				AlgoVector newVecAlgo = new AlgoVector(
						kernel.getConstruction(), null, (GeoPointND) pp[0],
						(GeoPointND) qq[0]);
				setTranslateStart(topHit, vec);

				// make sure vector looks the same when translated
				pp[0].setEuclidianVisible(p.isEuclidianVisible());
				qq[0].update();
				qq[0].setEuclidianVisible(q.isEuclidianVisible());
				qq[0].update();
				newVecAlgo.getGeoElements()[0]
						.setVisualStyleForTransformations(topHit);

				app.setMode(EuclidianConstants.MODE_MOVE);
				movedGeoVector = vec;
				moveMode = MOVE_VECTOR_NO_GRID;
				return;
			}
			movedGeoPoint = new GeoPoint(kernel.getConstruction(), null, 0, 0,
					0);
			AlgoTranslate algoTP = new AlgoTranslate(kernel.getConstruction(),
					null, (GeoElement) movedGeoPoint, (GeoVec3D) topHit);
			GeoPoint p = (GeoPoint) algoTP.getGeoElements()[0];

			AlgoVector newVecAlgo = new AlgoVector(kernel.getConstruction(),
					null, movedGeoPoint, p);

			// make sure vector looks the same when translated
			((GeoPoint) movedGeoPoint).setEuclidianVisible(false);
			((GeoPoint) movedGeoPoint).update();
			p.setEuclidianVisible(false);
			p.update();
			newVecAlgo.getGeoElements()[0]
					.setVisualStyleForTransformations(topHit);

			moveMode = MOVE_POINT;
		}

		if (topHit.isTranslateable() || topHit instanceof GeoPoly) {
			GeoVectorND vec;
			if (topHit instanceof GeoPoly) {
				// for polygons, we need a labelled vector so that all
				// the vertices move together
				vec = createVectorForTranslation(null);
				vec.setEuclidianVisible(false);
				((GeoElement) vec).setAuxiliaryObject(true);
			} else {
				vec = createVectorForTranslation();
			}
			getAlgoDispatcher().TranslateND(null, topHit, vec);
			setTranslateStart(topHit, vec);

			app.setMode(EuclidianConstants.MODE_MOVE, ModeSetter.TOOLBAR);
			movedGeoVector = vec;
			moveMode = MOVE_VECTOR_NO_GRID;
			return;
		}
	}

	protected GeoVectorND createVectorForTranslation() {
		return getAlgoDispatcher().Vector();
	}

	protected GeoVectorND createVectorForTranslation(String label) {
		return getAlgoDispatcher().Vector(label);
	}

	/**
	 * set translate start infos
	 */
	protected void setTranslateStart(GeoElement geo, GeoVectorND vec) {
		transformCoordsOffset[0] = xRW;
		transformCoordsOffset[1] = yRW;
	}

	protected static boolean penMode(int mode2) {
		switch (mode2) {
		case EuclidianConstants.MODE_PEN:
			// case EuclidianConstants.MODE_PENCIL:
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
	protected boolean processRightPressFor3D() {
		// 3D only
		return false;
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

	protected void handleMousePressedForRotateMode(PointerEventType type) {
		GeoElement geo;
		Hits hits;

		// we need the center of the rotation
		if (rotationCenter == null) {
			setViewHits(type);
			rotationCenter = (GeoPoint) chooseGeo(
					view.getHits().getHits(Test.GEOPOINT, tempArrayList), true);
			selection.addSelectedGeo(rotationCenter);
			moveMode = MOVE_NONE;
		} else {
			setViewHits(type);
			hits = view.getHits();
			hits.removePolygons();
			// hits = view.getHits(mouseLoc);
			// got rotation center again: deselect
			if (!hits.isEmpty() && hits.contains(rotationCenter)) {
				selection.removeSelectedGeo(rotationCenter);
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
				selection.addSelectedGeo(geo);
			}
			rotGeoElement = geo;

			if (geo != null) {
				doSingleHighlighting(rotGeoElement);
				// rotGeoElement.setHighlighted(true);

				// init values needed for rotation
				rotationLastAngle = Math.atan2(yRW - rotationCenter.inhomY, xRW
						- rotationCenter.inhomX);
				moveMode = MOVE_ROTATE;
			} else {
				moveMode = MOVE_NONE;
			}
		}
	}

	protected final void mousePressedTranslatedView(PointerEventType type) {

		Hits hits;

		// check if axis is hit
		// hits = view.getHits(mouseLoc);
		setViewHits(type);
		hits = view.getHits();
		hits.removePolygons();

		moveMode = MOVE_VIEW;
		if (!hits.isEmpty() && moveAxesPossible()) {
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

		setDragCursorIfMoveView();

		// xZeroOld = view.getXZero();
		// yZeroOld = view.getYZero();
		view.rememberOrigins();
		xTemp = xRW;
		yTemp = yRW;
		view.setShowAxesRatio((moveMode == MOVE_X_AXIS)
				|| (moveMode == MOVE_Y_AXIS));
		// view.setDrawMode(EuclidianConstants.DRAW_MODE_DIRECT_DRAW);

	}

	protected boolean moveAxesPossible() {
		return !view.isLockedAxesRatio() && view.isZoomable();
	}

	protected void setDragCursorIfMoveView() {
		if (moveMode == MOVE_VIEW) {
			view.setDragCursor();
		}

	}

	protected void switchModeForMousePressed(AbstractEvent e) {
		PointerEventType type = e.getType();
		Hits hits;
		// TODO we shall never get mode > 1000 here
		if (mode > 1000)
			app.setMode(EuclidianConstants.MODE_MOVE);
		switch (mode) {
		// create new point at mouse location
		// this point can be dragged: see mouseDragged() and mouseReleased()
		case EuclidianConstants.MODE_COMPLEX_NUMBER:
			setViewHits(type);
			hits = view.getHits();
			createNewPointForModePoint(hits, true);
			break;
		case EuclidianConstants.MODE_POINT:
		case EuclidianConstants.MODE_POINT_ON_OBJECT:
			setViewHits(type);
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
			setViewHits(type);
			hits = view.getHits();
			hits.removePolygons();
			createNewPointForModeOther(hits);
			break;

		case EuclidianConstants.MODE_VECTOR_POLYGON:
		case EuclidianConstants.MODE_RIGID_POLYGON:
			setViewHits(type);
			hits = view.getHits();

			// allow first object clicked on to be a Polygon -> create new
			// Rigid/Vector Polygon from it
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
				setViewHits(type);
				hits = view.getHits();
				hits.removePolygons();
				if (hits.size() == 0) {
					createNewPoint(hits, false, true, true);
				}
			}
			break;

		case EuclidianConstants.MODE_PARALLEL:
		case EuclidianConstants.MODE_ORTHOGONAL:
		case EuclidianConstants.MODE_ORTHOGONAL_THREE_D:

			setViewHits(type);
			hits = view.getHits();
			hits.removePolygons();
			if (hits.size() == 0) {
				createNewPoint(hits, false, true, true);
			} else if (selLines() == 1 && hits.get(0).isPath()) {
				// make sure clicking on line then line works #2610
				createNewPointForModeOther(hits);
			}

			break;

		case EuclidianConstants.MODE_PARABOLA: // Michael Borcherds 2008-04-08
			setViewHits(type);
			hits = view.getHits();

			// we clicked a line, we want it as a directrix
			if (hits.size() > 0 && hits.get(0).isGeoLine()) {
				// do nothing
			} else {
				createNewPoint(hits, false, false, false, false, false);
			}
			break;
		case EuclidianConstants.MODE_LINE_BISECTOR:
		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
		case EuclidianConstants.MODE_TANGENTS:
		case EuclidianConstants.MODE_POLAR_DIAMETER:
			// hits = view.getHits(mouseLoc);
			break;

		case EuclidianConstants.MODE_COMPASSES: // Michael Borcherds 2008-03-13
			// hits = view.getHits(mouseLoc);

			if (type == PointerEventType.TOUCH) {
				view.setPreview(null);
			}

			setViewHits(type);
			hits = view.getHits();
			hits.removePolygons();
			if (hits.isEmpty()) {
				createNewPoint(hits, false, true, true);
			}
			break;

		case EuclidianConstants.MODE_ANGLE:
			// hits = view.getTopHits(mouseLoc);
			setViewHits(type);
			hits = view.getHits().getTopHits();
			// check if we got a polygon
			if (hits.isEmpty()) {
				createNewPoint(hits, false, false, true);
			}
			break;

		case EuclidianConstants.MODE_ANGLE_FIXED:
		case EuclidianConstants.MODE_MIDPOINT:
			// hits = view.getHits(mouseLoc);
			setViewHits(type);
			hits = view.getHits();
			hits.removePolygons();
			if (hits.isEmpty()
					|| (!hits.get(0).isGeoSegment() && !hits.get(0)
							.isGeoConic())) {
				createNewPoint(hits, false, false, true);
			}
			break;

		case EuclidianConstants.MODE_MOVE_ROTATE:
			handleMousePressedForRotateMode(type);
			break;

		// move an object
		case EuclidianConstants.MODE_MOVE:
			// case EuclidianConstants.MODE_VISUAL_STYLE:
			handleMousePressedForMoveMode(e, false);
			break;

		// move drawing pad or axis
		case EuclidianConstants.MODE_TRANSLATEVIEW:

			mousePressedTranslatedView(type);

			break;

		case EuclidianConstants.MODE_ATTACH_DETACH:
			GeoPoint p = (GeoPoint) this.view.getHits().getFirstHit(
					Test.GEOPOINT);
			if (p != null && p.isMoveable()) {
				// set movedGeoPoint etc.
				handleMovedElement(p, false, PointerEventType.MOUSE);
				this.moveMode = MOVE_ATTACH_DETACH;
			}
			break;

		case EuclidianConstants.MODE_DELETE:
			getDeleteMode().mousePressed(type);

		default:
			moveMode = MOVE_NONE;
		}
	}

	private long lastMousePressedTime;

	public void wrapMousePressed(AbstractEvent event) {
		if (this.lastMouseRelease + EuclidianConstants.DOUBLE_CLICK_DELAY > System
				.currentTimeMillis()
				&& MyMath.length(event.getX() - lastMouseUpLoc.x, event.getY()
						- lastMouseUpLoc.y) <= 3) {
			this.doubleClickStarted = true;
			// return;
		}

		lastMousePressedTime = System.currentTimeMillis();

		app.storeUndoInfoIfSetCoordSystemOccured();
		app.maySetCoordSystem();

		scriptsHaveRun = false;

		penDragged = false;

		if (app.isUsingFullGui() && app.getGuiManager() != null) {
			// determine parent panel to change focus
			// EuclidianDockPanelAbstract panel =
			// (EuclidianDockPanelAbstract)SwingUtilities.getAncestorOfClass(EuclidianDockPanelAbstract.class,
			// (Component)e.getSource());

			// if(panel != null) {
			// app.getGuiManager().getLayout().getDockManager().setFocusedPanel(panel);
			// }
			app.getGuiManager().setFocusedPanel(event, false);
			app.getGuiManager().mousePressedForPropertiesView();

			if (view instanceof PlotPanelEuclidianViewInterface) {
				setMode(EuclidianConstants.MODE_MOVE);
			}
		}

		setMouseLocation(event);

		if (handleMousePressedForViewButtons()) {
			return;
		}

		Hits hits;

		if (penMode(mode)) {
			setViewHits(event.getType());
			hits = view.getHits();
			hits.removeAllButImages();
			getPen().handleMousePressedForPenMode(event, hits);
			return;
		}
		this.pressedButton = view.getHitButton(mouseLoc, event.getType());
		if (pressedButton != null) {
			pressedButton.setPressed(true);
			pressedButton.setDraggedOrContext(event.isMetaDown()
					|| event.isPopupTrigger());

			if (!event.isRightClick()) {
				runScriptsIfNeeded(pressedButton.getButton());
			}
		}
		// TODO:repaint?

		// GeoElement geo;
		transformCoords();

		moveModeSelectionHandled = false;
		draggingOccured = false;
		draggingBeyondThreshold = false;
		view.setSelectionRectangle(null);
		selectionStartPoint.setLocation(mouseLoc);

		if (hitResetIcon()
				|| view.hitAnimationButton(event.getX(), event.getY())) {
			// see mouseReleased
			return;
		}

		if (app.isRightClick(event)) {
			// ggb3D - for 3D rotation
			processRightPressFor3D();

			return;
		} else if (shallMoveView(event)) {
			// Michael Borcherds 2007-12-08 BEGIN
			// bugfix: couldn't select multiple objects with Ctrl

			setViewHits(event.getType());
			hits = view.getHits();
			switchModeForRemovePolygons(hits);
			if (!hits.isEmpty()) { // bugfix 2008-02-19 removed this:&&
									// ((GeoElement) hits.get(0)).isGeoPoint())
				dontClearSelection = true;
			}

			if (hits.isEmpty() || !app.isPrerelease()) {
				temporaryMode = true;
				oldMode = mode; // remember current mode
				view.setMode(EuclidianConstants.MODE_TRANSLATEVIEW);
			}
			// if over an axis, force the correct cursor to be displayed
			if (view.getHits().hasXAxis() || view.getHits().hasYAxis()) {
				processMouseMoved(event);
			}
		}

		switchModeForMousePressed(event);
	}

	private boolean shallMoveView(AbstractEvent event) {
		if (!app.isPrerelease()) {
			return app.isShiftDragZoomEnabled() && (
			// MacOS: shift-cmd-drag is zoom
					(event.isShiftDown() && !app.isControlDown(event)) // All
																		// Platforms:
																		// Shift
																		// key
							|| (event.isControlDown() && app.isWindows() // old
																			// Windows
																			// key:
							// Ctrl
							// key
							) || app.isMiddleClick(event));
		}
		return !doubleClickStarted && mode == EuclidianConstants.MODE_MOVE;
	}

	protected void runScriptsIfNeeded(GeoElement geo1) {
		// make sure that Input Boxes lose focus (and so update) before running
		// scripts
		if (view.getHits().size() > 0
				&& view.getHits().get(0) instanceof GeoTextField) {
			view.requestFocusInWindow();
		}
		if (!scriptsHaveRun) {
			scriptsHaveRun = true;
			app.runScripts(geo1, (String) null);
		}

	}

	protected boolean processZoomRectangle() {
		GRectangle rect = view.getSelectionRectangle();
		if (rect == null) {
			return false;
		}

		if ((rect.getWidth() < ZOOM_RECT_THRESHOLD)
				|| (rect.getHeight() < ZOOM_RECT_THRESHOLD)
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
	 * 
	 * @param hits
	 *            srt of its geos, may not be null
	 * @param test
	 *            test to filter specific object types
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
		setAppSelectedGeos(hits, false);
		app.updateSelection(hits.size() > 0);
	}

	protected void processSelectionRectangle(boolean alt, boolean isControlDown) {
		startCollectingMinorRepaints();

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
			setAppSelectedGeos(hits);
			changedKernel = processMode(hits, isControlDown, null);
			view.setSelectionRectangle(null);
			break;

		case EuclidianConstants.MODE_FITLINE:

			// check for list first
			if (hits.size() == 1) {
				if (hits.get(0).isGeoList()) {
					selectedGeos.addAll(hits);
					setAppSelectedGeos(hits);
					changedKernel = processMode(hits, isControlDown, null);
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
				setAppSelectedGeos(hits);
				changedKernel = processMode(hits, isControlDown, null);
				view.setSelectionRectangle(null);
			}
			break;

		default:
			// STANDARD CASE
			setAppSelectedGeos(hits, false);
			app.updateSelection((hits != null));

			// if alt pressed, create list of objects as string and copy to
			// input bar
			if ((hits != null) && (hits.size() > 0) && alt
					&& app.isUsingFullGui() && app.getGuiManager() != null
					&& app.showAlgebraInput()) {

				geogebra.common.javax.swing.GTextComponent textComponent = app
						.getGuiManager().getAlgebraInputTextField();

				StringBuilder sb = new StringBuilder();
				sb.append(" {");
				for (int i = 0; i < hits.size(); i++) {
					sb.append(hits.get(i).getLabel(
							StringTemplate.defaultTemplate));
					if (i < (hits.size() - 1)) {
						sb.append(", ");
					}
				}
				sb.append("} ");
				textComponent.replaceSelection(sb.toString());
			}
			break;
		}

		if (changedKernel) {
			app.storeUndoInfo();
		}

		stopCollectingMinorRepaints();
		kernel.notifyRepaint();
	}

	protected void processSelection() {

		startCollectingMinorRepaints();

		Hits hits = new Hits();
		hits.addAll(getAppSelectedGeos());
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
				setAppSelectedGeos(hits);
				processMode(hits, false, null);

				view.setSelectionRectangle(null);
			}
			break;

		default:
			break;
		}

		stopCollectingMinorRepaints();
		kernel.notifyRepaint();
	}

	public void showDrawingPadPopup(GPoint mouse) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().showDrawingPadPopup(view, mouse);
		}
	}

	public void wrapMouseReleased(final AbstractEvent event) {
		
		int x = event.getX();
		int y = event.getY();
		boolean right = app.isRightClick(event);
		boolean control = app.isControlDown(event);
		boolean alt = event.isAltDown();
		final boolean meta = event.isPopupTrigger() || event.isMetaDown();
		PointerEventType type = event.getType();

		if (this.doubleClickStarted && !draggingOccured) {
			wrapMouseclicked(control, 2, type);
		}
		this.doubleClickStarted = false;
		this.lastMouseRelease = System.currentTimeMillis();
		this.lastMouseUpLoc = new GPoint(x, y);

		app.storeUndoInfoIfSetCoordSystemOccured();

		if (pressedButton != null) {
			pressedButton.setDraggedOrContext(pressedButton
					.getDraggedOrContext() || meta);

			// make sure that Input Boxes lose focus (and so update) before
			// running scripts
			view.requestFocusInWindow();

			pressedButton.setPressed(false);
			pressedButton = null;
		}

		// remove deletion rectangle
		if (view.getDeletionRectangle() != null) {
			// ended deletion
			view.setDeletionRectangle(null);
			view.repaintView();
			app.storeUndoInfo();
		}

		// reset
		transformCoordsOffset[0] = 0;
		transformCoordsOffset[1] = 0;

		if (this.textfieldJustFocusedW(x, y, type)) {
			return;
		}

		if (penMode(mode) && penDragged) {
			getPen().handleMouseReleasedForPenMode(right, x, y);
			app.storeUndoInfo();
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
			
			processReleaseForMovedGeoPoint(right);
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

			if (app.isUsingFullGui()) {
				movedGeoNumeric.resetTraceColumns();
			}
		}

		movedGeoPointDragged = false;
		movedGeoNumericDragged = false;

		if ((view.getHits() == null)
				|| (view.getHits().size() == 0)
				|| !(view.getHits().getTopHits().get(0) instanceof GeoTextField || view
						.getHits().getTopHits().get(0) instanceof GeoList)) {
			view.requestFocusInWindow();
		}

		setMouseLocation(alt, x, y);

		transformCoords();
		Hits hits = null;

		if (hitResetIcon()) {
			app.reset();
			return;
		} else if (view.hitAnimationButton(x, y)) {
			if (kernel.isAnimationRunning()) {
				kernel.getAnimatonManager().stopAnimation();
			} else {
				kernel.getAnimatonManager().startAnimation();
			}
			view.repaintView();
			app.setUnsaved();
			return;
		}

		// allow drag with right mouse button or ctrl
		// make sure Ctrl still works for selection (when no dragging occured)
		if (right || (control && draggingOccured))// &&
													// !TEMPORARY_MODE)
		{
			if (processRightReleaseFor3D(type)) {
				return;
			}
			if (!temporaryMode) {
				processRightReleased(right, control, type);
				return;
			}
		}

		// handle moving
		boolean changedKernel = false;
		if (draggingOccured) {
			
			draggingOccuredBeforeRelease = true;
			draggingOccured = false;
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
			if (!temporaryMode) {
				// Michael Borcherds 2007-10-08
				if (allowSelectionRectangle()) {
					processSelectionRectangle(alt, control);

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
					changedKernel, control, type);
		}

		startCollectingMinorRepaints();

		// remember helper point, see createNewPoint()
		if ((changedKernel || this.checkboxChangeOccured) && !changedKernel0
				&& !modeCreatesHelperPoints(mode)) {
			this.checkboxChangeOccured = false;
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
		setViewHits(type);
		hits = view.getHits();
		switchModeForRemovePolygons(hits);

		// Michael Borcherds 2007-12-08 BEGIN moved up a few lines (bugfix:
		// Tools eg Line Segment weren't working with grid on)
		// grid capturing on: newly created point should be taken
		if (pointCreated != null) {
			hits = addPointCreatedForMouseReleased(hits);
		}
		pointCreated = null;
		// Michael Borcherds 2007-12-08 END

		if (temporaryMode) {
			
			// Michael Borcherds 2007-10-13 BEGIN
			view.setMode(oldMode);
			temporaryMode = false;
			// Michael Borcherds 2007-12-08 BEGIN bugfix: couldn't select
			// multiple points with Ctrl
			if (dontClearSelection == false) {
				clearSelections();
			}
			dontClearSelection = false;
			// Michael Borcherds 2007-12-08 END
			// mode = oldMode;
			// Michael Borcherds 2007-10-13 END
		}
		// Michael Borcherds 2007-10-12 bugfix: ctrl-click on a point does the
		// original mode's command at end of drag if a point was clicked on
		// also needed for right-drag
		else {
			final Hits hits2 = hits;
			AsyncOperation callback = new AsyncOperation() {

				@Override
				public void callback(Object changedKernel) {
					if (changedKernel.equals(true)) {
						app.storeUndoInfo();
					}
					endOfWrapMouseReleased(hits2, event);
				}
			};
			processMode(hits, control, callback);

		}

		endOfWrapMouseReleased(hits, event);

		draggingOccuredBeforeRelease = false;

	}

	private boolean modeCreatesHelperPoints(int mode2) {
		switch (mode2) {
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
			return true;
		}

		return false;
	}

	public void endOfWrapMouseReleased(Hits hits, AbstractEvent event) {
		boolean control = app.isControlDown(event);
		boolean alt = event.isAltDown();
		PointerEventType type = event.getType();
		endOfWrapMouseReleased(hits, control, alt, type);
	}

	public void endOfWrapMouseReleased(Hits hits, boolean control, boolean alt,
			PointerEventType type) {
		
		if (!hits.isEmpty()) {
			view.setDefaultCursor();
		} else {
			view.setHitCursor();
		}

		refreshHighlighting(null, control);

		// reinit vars
		// view.setDrawMode(EuclidianConstants.DRAW_MODE_BACKGROUND_IMAGE);
		moveMode = MOVE_NONE;
		initShowMouseCoords();
		view.setShowAxesRatio(false);

		if (!setJustCreatedGeosSelected()) { // first try to set just created
												// geos as selected
			// if none, do specific stuff for properties view
			if (app.isUsingFullGui() && app.getGuiManager() != null) {// prevent
																		// objects
																		// created
																		// by a
																		// script
				if (checkBoxOrButtonJustHitted) // does nothing
					checkBoxOrButtonJustHitted = false;
				else
					app.getGuiManager()
							.mouseReleasedForPropertiesView(
									mode != EuclidianConstants.MODE_MOVE
											&& mode != EuclidianConstants.MODE_MOVE_ROTATE);
			}
		}
		// Alt click: copy definition to input field
		if (alt && app.showAlgebraInput()) {
			altClicked(type);
		}
		stopCollectingMinorRepaints();
		kernel.notifyRepaint();
	}

	private void altClicked(PointerEventType type) {
		setViewHits(type);
		Hits hits = view.getHits().getTopHits();
		if ((hits != null) && (hits.size() > 0)) {
			hits.removePolygons();
			GeoElement geo = hits.get(0);

			// F3 key: copy definition to input bar
			if (mode != EuclidianConstants.MODE_ATTACH_DETACH) {
				app.getGlobalKeyDispatcher().handleFunctionKeyForAlgebraInput(
						3, geo);
			}

			moveMode = MOVE_NONE;
			return;
		}

	}

	private void processRightReleased(boolean right, boolean control,
			PointerEventType type) {
		
		if (!app.isRightClickEnabled()) {
			return;
		}
		if (!app.isPrerelease() && type != PointerEventType.TOUCH
				&& processZoomRectangle()) {
			return;
			// Michael Borcherds 2007-10-08
		}
		// make sure cmd-click selects multiple points (not open
		// properties)
		if ((app.isMacOS() && control) || !right) {
			return;
		}
		if (draggingOccured && app.isPrerelease()) {
			if (allowSelectionRectangle()) {
				processSelectionRectangle(false, control);
				return;
			}
		}
		// get selected GeoElements
		// show popup menu after right click
		setViewHits(type);
		Hits hits = view.getHits().getTopHits();
		if (hits.isEmpty()) {
			// no hits
			if (app.isUsingFullGui() && app.getGuiManager() != null) {

				if (view.getSelectionRectangle() != null) {
					// don't show a contextMenu if there's a
					// selectionRectangle
					processSelectionRectangle(false, control);
					return;
				} else if (selection.selectedGeosSize() > 0) {
					// GeoElement selGeo = (GeoElement)
					// getAppSelectedGeos().get(0);
					showPopupMenuChooseGeo(getAppSelectedGeos(), hits);
				} else {
					showDrawingPadPopup(mouseLoc);
				}
			}
		} else {
			// there are hits
			if (selection.selectedGeosSize() > 0) {
				
				if (mode == EuclidianConstants.MODE_MOVE) { // only for move
															// mode
					
					// right click on already selected geos -> show menu for
					// them
					// right click on object(s) not selected -> clear
					// selection
					// and show menu just for new objects

					if (!hits.intersect(getAppSelectedGeos())) {
						selection.clearSelectedGeos(false); // repaint will be
															// done next step
						selection.addSelectedGeos(hits, true);
					} else {
						// selection.addSelectedGeo(hits.get(0));
					}

					if (app.isUsingFullGui() && app.getGuiManager() != null) {
						showPopupMenuChooseGeo(getAppSelectedGeos(), hits);
					}

				} else { // other modes : want to apply tool of one of the hits
							// (choose geo and show popup menu)
					if (app.isUsingFullGui() && app.getGuiManager() != null) {
						
						GeoElement geo = chooseGeo(hits, true, false);

						if (geo == null)// when axis is clicked
							showDrawingPadPopup(mouseLoc);
						else {
							ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
							geos.add(geo);
							showPopupMenuChooseGeo(geos, hits);
						}
					}
				}

			} else {
				// no selected geos: choose geo and show popup menu
				if (app.isUsingFullGui() && app.getGuiManager() != null) {
					
					GeoElement geo = chooseGeo(hits, true, false);

					if (geo == null)// when axis is clicked
						showDrawingPadPopup(mouseLoc);
					else {
						ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
						geos.add(geo);
						showPopupMenuChooseGeo(geos, hits);
					}
				}
			}
		}
	}

	/**
	 * set just created geos as selected (if any)
	 * 
	 * @return true if any just created geos
	 */
	public boolean setJustCreatedGeosSelected() {
		if (justCreatedGeos != null && justCreatedGeos.size() > 0) {
			setAppSelectedGeos(justCreatedGeos);
			return true;
		}
		return false;
	}

	public void wrapMouseWheelMoved(int x, int y, double delta,
			boolean shiftOrMeta, boolean alt) {

		if (isTextfieldHasFocus()) {
			return;
		}

		if (penMode(mode)) {
			return;
		}

		app.maySetCoordSystem();

		// don't allow mouse wheel zooming for applets if mode is not zoom mode
		boolean allowMouseWheel = !app.isApplet()
				|| (mode == EuclidianConstants.MODE_ZOOM_IN)
				|| (mode == EuclidianConstants.MODE_ZOOM_OUT)
				|| (app.isShiftDragZoomEnabled() && shiftOrMeta);
		if (!allowMouseWheel) {
			return;
		}

		wheelZoomingOccurred = true;

		setMouseLocation(alt, x, y);

		// double px = view.width / 2d;
		// double py = view.height / 2d;
		double px = mouseLoc.x;
		double py = mouseLoc.y;
		// double dx = view.getXZero() - px;
		// double dy = view.getYZero() - py;

		double xFactor = 1;
		if (alt) {
			xFactor = 1.5;
		}

		double reverse = app.isMouseWheelReversed() ? -1 : 1;

		double factor = ((delta * reverse) > 0) ? EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR
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

	public void setLineEndPoint(geogebra.common.awt.GPoint2D p) {
		if (p == null)
			lineEndPoint = null;
		else
			lineEndPoint = new GPoint2D.Double(p.getX(), p.getY());
		useLineEndPoint = true;
	}

	public Hits getHighlightedgeos() {
		return highlightedGeos.clone();
	}

	public void setAlpha(float alpha) {
		ArrayList<GeoElement> geos = getAppSelectedGeos();
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			geo.setAlphaValue(alpha);
			geo.updateRepaint();
		}
	}

	public void setSize(int size) {
		// if (mode == EuclidianView.MODE_VISUAL_STYLE) {
		ArrayList<GeoElement> geos = getAppSelectedGeos();

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
			// getPen().setAbsoluteScreenPosition(true);
			break;
		/*
		 * case EuclidianConstants.MODE_PENCIL: getPen().setFreehand(false);
		 * getPen().setAbsoluteScreenPosition(false); break;
		 */
		/*
		 * boolean createUndo = true; // scale both EVs 1:1 if
		 * (app.getEuclidianView().isVisible()) {
		 * app.getEuclidianView().zoomAxesRatio(1, true); createUndo = false; }
		 * 
		 * if (app.hasEuclidianView2() && app.getEuclidianView2().isVisible()) {
		 * app.getEuclidianView2().zoomAxesRatio(1, createUndo); }//
		 */

		/*
		 * ArrayList<GeoElement> selection = getAppSelectedGeos();
		 * pen.setPenGeo(null); if (selection.size() == 1) { GeoElement geo =
		 * selection.get(0); // getCorner(1) == null as we can't write to
		 * transformed images if (geo.isGeoImage()) { GeoPoint2 c1 = ((GeoImage)
		 * geo).getCorner(0); GeoPoint2 c2 = ((GeoImage) geo).getCorner(1);
		 * GeoPoint2 c3 = ((GeoImage) geo).getCorner(2);
		 * 
		 * if (((c3 == null) && (c2 == null // c2 = null -> not transformed ))
		 * // or c1 and c2 are the correct spacing for the // image not to be
		 * transformed // (ie image was probably created by the Pen Tool) ||
		 * ((c1 != null) && (c2 != null) && noZoomNeeded(c1,c2,(GeoImage)geo)))
		 * { pen.setPenGeo(geo); } } else if (geo instanceof GeoPolyLine) {
		 * pen.setPenGeo(geo); } }
		 */

		// no break;

		// case EuclidianConstants.MODE_VISUAL_STYLE:

		// openMiniPropertiesPanel();

		// break;

		case EuclidianConstants.MODE_PARALLEL:
			previewDrawable = view.createPreviewParallelLine(selectedPoints,
					selectedLines);
			break;

		case EuclidianConstants.MODE_PARABOLA:
			previewDrawable = view.createPreviewParabola(selectedPoints,
					selectedLines);
			break;

		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
			previewDrawable = view.createPreviewAngleBisector(selectedPoints);
			break;

		case EuclidianConstants.MODE_ORTHOGONAL:
		case EuclidianConstants.MODE_ORTHOGONAL_THREE_D:
			previewDrawable = view.createPreviewPerpendicularLine(
					selectedPoints, selectedLines);
			break;

		case EuclidianConstants.MODE_LINE_BISECTOR:
			previewDrawable = view
					.createPreviewPerpendicularBisector(selectedPoints);
			break;

		case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
			previewDrawable = view.createPreviewConic(mode1, selectedPoints);
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
			previewDrawable = new DrawConic(view, mode1, selectedPoints,
					selectedSegments, selectedConicsND);
			break;

		// preview for arcs and sectors
		case EuclidianConstants.MODE_SEMICIRCLE:
		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			previewDrawable = new DrawConicPart(view, mode1, selectedPoints);
			break;

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			useLineEndPoint = false;
			previewDrawable = view.createPreviewVector(selectedPoints);
			break;

		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
			if (this.view.getEuclidianViewNo() == 1 && app.hasEuclidianView2(1)) {
				return previewDrawable;
			}
			// select all hidden objects
			Iterator<GeoElement> it = kernel.getConstruction()
					.getGeoSetConstructionOrder().iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				// independent numbers should not be set visible
				// as this would produce a slider
				if (!geo.isSetEuclidianVisible()
						&& !((geo instanceof NumberValue || geo instanceof BooleanValue) && geo
								.isIndependent())) {
					geo.setEuclidianVisible(true);
					selection.addSelectedGeo(geo);
					geo.updateRepaint();
				}
			}
			break;

		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
			app.setGeoForCopyStyle(null); // this will be the active geo
											// template
			break;

		case EuclidianConstants.MODE_MOVE_ROTATE:
			rotationCenter = null; // this will be the active geo template
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

		// this should not happen in theory
		if (app.getGuiManager() == null
				&& newMode != EuclidianConstants.MODE_TRANSLATEVIEW
				&& newMode != EuclidianConstants.MODE_MOVE)
			return;

		this.mode = newMode;
		initShowMouseCoords();
		// Michael Borcherds 2007-10-12
		// clearSelections();
		if (!temporaryMode
				&& !(EuclidianView.usesSelectionRectangleAsInput(newMode) && view
						.getSelectionRectangle() != null)) {
			clearSelections();
		}
		// Michael Borcherds 2007-10-12
		moveMode = MOVE_NONE;

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
				processSelectionRectangle(false, false);
			}
		} else if (EuclidianView.usesSelectionAsInput(newMode)) {
			initNewMode(newMode);
			if (app.getActiveEuclidianView() == view) {
				processSelection();
			}
		} else {
			if (!temporaryMode) {
				selection.clearSelectedGeos(false);
			}
			initNewMode(newMode);
		}

		kernel.notifyRepaint();
	}

	public void zoomInOut(boolean altPressed, boolean minusPressed) {
		double factor = minusPressed ? 1d / EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR
				: EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR;

		// accelerated zoom
		if (altPressed) {
			factor *= minusPressed ? 2d / 3d : 1.5;
		}

		zoomInOut(factor, 4);

	}

	public void zoomInOut(double factor, int steps) {
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
		// make zooming a little bit smoother by having some steps
		view.setAnimatedCoordSystem(
		// px + dx * factor,
		// py + dy * factor,
				px, py, factor, view.getXscale() * factor, steps, false);
		// view.yscale * factor);
		app.setUnsaved();

	}

	public App getApplication() {
		return app;
	}

	/**
	 * show popup menu when no geo is selected
	 * 
	 * @param selectedGeos1
	 *            first hits on the mouse
	 * @param hits
	 *            hits on the mouse
	 */
	protected void showPopupMenuChooseGeo(ArrayList<GeoElement> selectedGeos1,
			Hits hits) {

		// app.getGuiManager().showPopupMenu(firstHits,view, mouseLoc);

		if (app.getGuiManager() != null) {
			app.getGuiManager().showPopupChooseGeo(selectedGeos1, hits, view,
					mouseLoc);
		}
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

	private boolean wheelZoomingOccurred = false;

	public void checkZooming() {
		checkZooming(false);
	}

	/**
	 * when object created, make undo point if scroll wheel has been used
	 * 
	 * @param forPreviewable
	 *            whether this is for preview only
	 */
	public void checkZooming(boolean forPreviewable) {

		/*
		 * TODO what about this method? if (forPreviewable) { return; }
		 * 
		 * if (wheelZoomingOccurred) { app.storeUndoInfo(); }
		 * 
		 * wheelZoomingOccurred = false;
		 */
	}

	public int getDeleteToolSize() {
		EuclidianSettings settings = this.view.getSettings();
		if (settings != null) {
			return this.view.getSettings().getDeleteToolSize();
		}

		return EuclidianConstants.DEFAULT_ERASER_SIZE;
	}

	public boolean isCollectingRepaints() {
		return collectingRepaints > 0;
	}

	public void setCollectedRepaints(boolean collected) {
		collectedRepaints = collected;
	}

	protected DialogManager getDialogManager() {
		return app.getDialogManager();
	}

	protected ArrayList<GeoElement> getAppSelectedGeos() {
		return selection.getSelectedGeos();
	}

	protected void setAppSelectedGeos(ArrayList<GeoElement> geos) {
		selection.setSelectedGeos(geos);
	}

	protected void setAppSelectedGeos(ArrayList<GeoElement> geos,
			boolean updateSelection) {
		selection.setSelectedGeos(geos, updateSelection);
	}

	public boolean isTextfieldHasFocus() {
		return textfieldHasFocus;
	}

	public void calculateEnvironment() {
		// TODO Auto-generated method stub

	}

	public void setExternalHandling(boolean b) {
		this.externalHandling = b;
	}

	public EnvironmentStyle getEnvironmentStyle() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	public void onPinch(final int x, final int y, final double scaleFactor) {
		this.mouseLoc = new GPoint(x, y);
		zoomInOut(scaleFactor,
				scaleFactor < EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR ? 1 : 2);
	}

	public double oldDistance;

	private PointerEventType defaultEventType = PointerEventType.MOUSE;
	protected static final double MINIMAL_PIXEL_DIFFERENCE_FOR_ZOOM = 10;

	public void twoTouchStart(double x1, double y1, double x2, double y2) {
		twoTouchStartCommon(x1, y1, x2, y2);
	}

	final public void twoTouchStartCommon(double x1, double y1, double x2, double y2) {
		this.oldDistance = MyMath.length(x1 - x2, y1 - y2);
	}

	public void twoTouchMove(double x1, double y1, double x2, double y2) {
		twoTouchMoveCommon(x1, y1, x2, y2);
	}
	
	final public void twoTouchMoveCommon(double x1, double y1, double x2, double y2) {
		int centerX, centerY;
		double newDistance;

		centerX = (int) (x1 + x2) / 2;
		centerY = (int) (y1 + y2) / 2;

		if (this.oldDistance > 0) {
			newDistance = MyMath.length(x1 - x2, y1 - y2);

			if (Math.abs(newDistance - this.oldDistance) > MINIMAL_PIXEL_DIFFERENCE_FOR_ZOOM) {
				onPinch(centerX, centerY, newDistance / this.oldDistance);
				this.oldDistance = newDistance;
			}
		}
	}

	public boolean isExternalHandling() {
		return externalHandling;
	}

	/**
	 * in future 3 will be supported for 3rd 2D View
	 * 
	 * @return 1 (EV1) , 2 (EV2), -1 (3D) or EVNO_GENERAL = 1001
	 * 
	 */
	public int getEvNo() {
		return this.view.evNo;
	}

	public final void setDefaultEventType(PointerEventType pointerEventType) {
		this.defaultEventType = pointerEventType;
	}

	public final PointerEventType getDefaultEventType() {
		return this.defaultEventType;
	}

	/**
	 * factor by which hit-threshold is increased while dragging for
	 * attachDetach (while the point is attached to a Path or Region)
	 */
	private static final int INCREASED_THRESHOLD_FACTOR = 2;
	protected Object detachFrom;
	private boolean detachFromPath, detachFromRegion;
	private boolean needsAttach = false;

	private void moveAttachDetach(boolean repaint, AbstractEvent event) {
		if (movedGeoPoint.isPointOnPath() || movedGeoPoint.isPointInRegion()) {
			int th = app.getCapturingThreshold(PointerEventType.MOUSE);
			app.setCapturingThreshold(INCREASED_THRESHOLD_FACTOR * th);
			this.view.setHits(new GPoint(event.getX(), event.getY()),
					event.getType());
			app.setCapturingThreshold(th);
		} else {
			this.view.setHits(new GPoint(event.getX(), event.getY()),
					event.getType());
		}
		// clone because that way view.getHits still contains Polygons
		Hits hits = view.getHits().clone();
		hits.removePolygons();

		// use view.getHits for Region, because it still contains Polygons
		if (movedGeoPoint.isPointOnPath()
				&& !hits.contains(movedGeoPoint.getPath())) {
			needsAttach = false;
			detachFromPath = true;
			detachFromRegion = false;
			if (detachFrom == null) {
				detachFrom = movedGeoPoint.getPath();
			}
			this.addToSelectionList(selectedPaths, movedGeoPoint.getPath(), 1);
			((GeoPoint) movedGeoPoint).removePath();
			movedGeoPoint.setCoords(view.toRealWorldCoordX(event.getX()),
					view.toRealWorldCoordY(event.getY()), 1);
		} else if (movedGeoPoint.isPointInRegion()
				&& !view.getHits().contains(movedGeoPoint.getRegion())) {
			// moved away from the Path/Region the point is attached to ->
			// detach
			needsAttach = false;
			detachFromPath = false;
			detachFromRegion = true;
			if (detachFrom == null) {
				detachFrom = movedGeoPoint.getRegion();
			}
			this.addToSelectionList(selectedRegions, movedGeoPoint.getRegion(),
					1);
			((GeoPoint) movedGeoPoint).setRegion(null);
			movedGeoPoint.setCoords(view.toRealWorldCoordX(event.getX()),
					view.toRealWorldCoordY(event.getY()), 1);
		} else {
			for (int i = hits.size() - 1; i >= 0; i--) {
				if (hits.get(i).isChildOf((GeoElement) movedGeoPoint)) {
					hits.remove(i);
				}
			}

			addSelectedPath(hits, 1, false);
			if (selectedPaths.size() > 0) {
				// moved point to a Path -> attach
				needsAttach = true;
				movedGeoPoint.setPath(selectedPaths.get(0));
				movedGeoPoint.setCoords(view.toRealWorldCoordX(event.getX()),
						view.toRealWorldCoordY(event.getY()), 1);
			} else {
				// move point
				companion.movePoint(repaint, event);
				// already includes updateCascade
				return;
			}
		}
		((GeoElement) movedGeoPoint).updateCascade();
	}

	/**
	 * set the view attached to this
	 * 
	 * @param view
	 *            view
	 */
	public abstract void setView(EuclidianView view);

	public void setDialogOccurred() {
		// use in 3D
	}

	/**
	 * move mouse cursor if waiting for
	 */
	public void moveIfWaiting() {
		// used in web
	}

	/**
	 * set view hits for current mouse location
	 * 
	 * @param type
	 *            event type
	 */
	protected void setViewHits(PointerEventType type) {
		view.setHits(type);
	}

	public boolean isTemporaryMode() {
		return temporaryMode;
	}

	
	public void resetModeAfterFreehand() {
		// not used in common, overwritten for other projects
	}

	public void prepareModeForFreehand() {
		// not used in common, overwritten for other projects
	}

	public void onPointerEventStart(AbstractEvent e) {
		// not used in common, overwritten for other projects
		
	}

}
