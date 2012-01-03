package geogebra.common.euclidian;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import geogebra.common.awt.Point;
import geogebra.common.awt.Point2D;
import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.View;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
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
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.main.AbstractApplication;

public abstract class EuclidianController {

	protected static int POLYGON_NORMAL = 0;

	protected static int POLYGON_RIGID = 1;

	protected static int POLYGON_VECTOR = 2;

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

	protected AbstractKernel kernel;

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
	
	public abstract void setKernel(AbstractKernel kernel);
	
	public abstract AbstractKernel getKernel();
	
	public abstract void setApplication(AbstractApplication app);

	public abstract void clearJustCreatedGeos();

	public abstract void clearSelections();

	public abstract void memorizeJustCreatedGeos(ArrayList<GeoElement> geos);

	public abstract void memorizeJustCreatedGeos(GeoElement[] geos);

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
					if (AbstractKernel.isEqual(geo2.getInhomX(),
							((GeoPoint2) geo).getInhomX())
							&& AbstractKernel.isEqual(geo2.getInhomY(),
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

}
