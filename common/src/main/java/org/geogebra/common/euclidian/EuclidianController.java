/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

 */

package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.EuclidianPenFreehand.ShapeType;
import org.geogebra.common.euclidian.controller.MouseTouchGestureController;
import org.geogebra.common.euclidian.draw.DrawAudio;
import org.geogebra.common.euclidian.draw.DrawConic;
import org.geogebra.common.euclidian.draw.DrawConicPart;
import org.geogebra.common.euclidian.draw.DrawDropDownList;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.euclidian.draw.DrawPolyLine;
import org.geogebra.common.euclidian.draw.DrawPolygon;
import org.geogebra.common.euclidian.draw.DrawSegment;
import org.geogebra.common.euclidian.draw.DrawSlider;
import org.geogebra.common.euclidian.draw.DrawText;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.euclidian.modes.ModeDelete;
import org.geogebra.common.euclidian.modes.ModeDeleteInterface;
import org.geogebra.common.euclidian.modes.ModeDeleteLocus;
import org.geogebra.common.euclidian.modes.ModeMacro;
import org.geogebra.common.euclidian.modes.ModeShape;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.gui.view.data.PlotPanelEuclidianViewInterface;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoCirclePointRadius;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
import org.geogebra.common.kernel.algos.AlgoDynamicCoordinatesInterface;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoExtremumMulti;
import org.geogebra.common.kernel.algos.AlgoExtremumPolynomial;
import org.geogebra.common.kernel.algos.AlgoExtremumPolynomialInterval;
import org.geogebra.common.kernel.algos.AlgoFunctionFreehand;
import org.geogebra.common.kernel.algos.AlgoIntersectLineConic;
import org.geogebra.common.kernel.algos.AlgoRadius;
import org.geogebra.common.kernel.algos.AlgoRoots;
import org.geogebra.common.kernel.algos.AlgoRootsPolynomial;
import org.geogebra.common.kernel.algos.AlgoRootsPolynomialInterval;
import org.geogebra.common.kernel.algos.AlgoTranslate;
import org.geogebra.common.kernel.algos.AlgoVector;
import org.geogebra.common.kernel.algos.AlgoVectorPoint;
import org.geogebra.common.kernel.algos.AlgoVertexConic;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.Furniture;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoAxis;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.HitType;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.geos.GeoFrame;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.kernel.geos.GeoWidget;
import org.geogebra.common.kernel.geos.Lineable2D;
import org.geogebra.common.kernel.geos.MoveGeos;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.kernel.geos.PointRotateable;
import org.geogebra.common.kernel.geos.PolygonFactory;
import org.geogebra.common.kernel.geos.TestGeo;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.kernel.kernelND.GeoAxisND;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoImplicitSurfaceND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.kernelND.HasSegments;
import org.geogebra.common.kernel.statistics.AlgoFitLineY;
import org.geogebra.common.kernel.statistics.CmdFitLineY;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.main.SpecialPointsListener;
import org.geogebra.common.main.SpecialPointsManager;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

public abstract class EuclidianController implements SpecialPointsListener {

	/**
	 * max value for alpha to consider an object transparent (i.e. we can see
	 * through it)
	 */
	public static final float MAX_TRANSPARENT_ALPHA_VALUE = 0.8f;
	public static final int MAX_TRANSPARENT_ALPHA_VALUE_INT = (int) (255
			* MAX_TRANSPARENT_ALPHA_VALUE);
	/**
	 * max value for alpha to consider an object visible
	 */
	public static final float MIN_VISIBLE_ALPHA_VALUE = 0.05f;
	protected static final int MIN_MOUSE_MOVE_FOR_AXIS_SCALE = 2;

	public static final int MOVE_NONE = 101;
	public static final int MOVE_POINT = 102;
	public static final int MOVE_VIEW = 106;
	public static final int MOVE_ROTATE_VIEW = 120;
	public static final int MOVE_PLANE = 126;
	/**
	 * Threshold for the selection rectangle distance squared (10 pixel circle)
	 */
	public final static double SELECTION_RECT_THRESHOLD_SQR = 200.0;
	public final static double FREEHAND_MODE_THRESHOLD_SQR = 200.0;
	protected static final int POLYGON_NORMAL = 0;
	protected static final int POLYGON_RIGID = 1;
	protected static final int POLYGON_VECTOR = 2;
	protected static final double MOUSE_DRAG_MAX_DIST_SQUARE = 36;
	protected static final int MAX_CONTINUITY_STEPS = 4;
	protected static final int MOVE_LINE = 103;
	protected static final int MOVE_CONIC = 104;
	protected static final int MOVE_VECTOR = 105;
	protected static final int MOVE_VECTOR_STARTPOINT = 205;
	protected static final int MOVE_FUNCTION = 107;
	protected static final int MOVE_LABEL = 108;
	protected static final int MOVE_TEXT = 109;
	protected static final int MOVE_NUMERIC = 110;
	protected static final int MOVE_SLIDER = 111;
	protected static final int MOVE_IMAGE = 112;
	protected static final int MOVE_ROTATE = 113;
	protected static final int MOVE_DEPENDENT = 114;
	protected static final int MOVE_MULTIPLE_OBJECTS = 115;
	public static final int MOVE_X_AXIS = 116;
	public static final int MOVE_Y_AXIS = 117;
	protected static final int MOVE_BOOLEAN = 118;
	protected static final int MOVE_BUTTON = 119;
	protected static final int MOVE_VECTOR_NO_GRID = 122;
	protected static final int MOVE_POINT_WITH_OFFSET = 123;
	protected static final int MOVE_FREEHAND = 124;
	protected static final int MOVE_ATTACH_DETACH = 125;
	protected static final int MOVE_IMPLICIT_CURVE = 127;
	public static final int MOVE_Z_AXIS = 128;
	public static final int MOVE_STROKE = 129;
	public static final int MOVE_AUDIO_SLIDER = 130;
	protected static final int MOVE_MEDIA = 131;
	protected static final double MINIMAL_PIXEL_DIFFERENCE_FOR_ZOOM = 10;
	private static final float ZOOM_RECTANGLE_SNAP_RATIO = 1.2f;
	private static final int ZOOM_RECT_THRESHOLD = 30;
	protected static final int DRAG_THRESHOLD = 10;
	/**
	 * factor by which hit-threshold is increased while dragging for
	 * attachDetach (while the point is attached to a Path or Region)
	 */
	private static final int INCREASED_THRESHOLD_FACTOR = 2;
	protected final App app;
	protected final SelectionManager selection;
	protected final Localization localization;
	public double xRW;
	public double yRW;
	public GeoPointND movedGeoPoint;
	protected GeoElement resultedGeo;
	public boolean draggingBeyondThreshold = false;
	protected Kernel kernel;
	public GPoint mouseLoc;
	private EuclidianView view;
	protected EuclidianPen pen;
	private double oldDistance;
	private boolean wasBoundingBoxHit;
	private boolean isMultiResize;
	private BoundingBoxResizeState startBoundingBoxState;
	protected double xTemp;
	protected double yTemp;
	protected boolean useLineEndPoint = false;
	protected GeoConic tempConic;
	protected GeoImplicitCurve tempImplicitCurve;
	protected ArrayList<GeoPoint> moveDependentPoints;
	protected GeoFunction tempFunction;
	protected GeoLineND movedGeoLine;
	protected GeoConicND movedGeoConic;
	protected GeoImplicitCurve movedGeoImplicitCurve;
	protected GeoVectorND movedGeoVector;
	protected GeoText movedGeoText;
	protected GeoImage oldImage;
	protected GeoImage movedGeoImage;
	protected GeoFunction movedGeoFunction;
	protected GeoNumeric movedGeoNumeric;
	protected GeoBoolean movedGeoBoolean;
	protected Furniture movedGeoButton;
	protected GeoWidget movedGeoMedia;
	protected GeoElement movedLabelGeoElement;
	protected GeoElement movedGeoElement;
	protected Drawable resizedShape = null;
	private MyDouble tempNum;
	protected double rotationLastAngle;
	protected ArrayList<GeoElement> translateableGeos;
	protected Coords translationVec;
	protected Hits tempArrayList = new Hits();
	protected Hits highlightedGeos = new Hits();
	protected ArrayList<GeoElement> justCreatedGeos = new ArrayList<>();
	protected boolean temporaryMode = false;
	protected boolean dontClearSelection = false;
	protected boolean draggingOccured = false;
	protected boolean draggingOccurredBeforeRelease = false;
	protected GeoPointND pointCreated = null;

	// may be omitted
	protected boolean moveModeSelectionHandled;

	protected boolean highlightJustCreatedGeos = true;
	protected ArrayList<GeoElement> pastePreviewSelected = null;
	protected ArrayList<GeoElement> pastePreviewSelectedAndDependent;
	protected int mode;
	protected int oldMode;
	protected int moveMode = MOVE_NONE;

	protected boolean toggleModeChangedKernel = false;
	protected boolean altDown = false;
	protected GeoElement rotGeoElement;
	protected GeoPoint rotationCenter;
	protected int polygonMode = POLYGON_NORMAL;
	protected double[] transformCoordsOffset = new double[2];
	protected Hits tempRegionHitsArrayList = new Hits();

	// ==============================================
	// Pen
	protected boolean allowSelectionRectangleForTranslateByVector = true;

	// ==============================================
	// Delete tool

	// private int deleteToolSize = EuclidianConstants.DEFAULT_ERASER_SIZE;
	protected int previousPointCapturing;
	protected ArrayList<GeoPointND> persistentStickyPointList = new ArrayList<>();
	protected GPoint startLoc;
	protected GPoint lastMouseLoc;
	protected GPoint oldLoc = new GPoint();
	protected GPoint2D.Double lineEndPoint = null;
	protected GPoint selectionStartPoint = new GPoint();
	protected ArrayList<Double> tempDependentPointX;
	protected ArrayList<Double> tempDependentPointY;
	protected boolean mouseIsOverLabel = false;
	protected EuclidianControllerCompanion companion;
	/**
	 * position of last mouseDown or touchStart
	 */
	protected GPoint startPosition;
	protected GeoPointND firstSelectedPoint;
	protected Hits handleAddSelectedArrayList = new Hits();
	protected Coords tmpCoordsL3;
	protected boolean penDragged;
	protected boolean shapeDragged;
	protected int oldShapeMode = -1;
	protected boolean doubleClickStarted;
	protected double twoTouchStartX;
	protected double twoTouchStartY;
	protected double twoTouchStartDistance;
	/**
	 * conic which's size is changed
	 */
	protected GeoConic scaleConic;
	/**
	 * saves the actual position when the view is moved during drawing an
	 * element with preview (e.g. a line or a segment)
	 */
	protected GPoint movePosition;
	/**
	 * the mode of the actual multitouch-event
	 */
	protected ScaleMode multitouchMode = ScaleMode.view;
	/**
	 * actual scale of the axes (has to be saved during multitouch)
	 */
	protected double scale;
	protected double originalRadius;
	/**
	 * midpoint of scaleConic: [0] ... x-coordinate [1] ... y-coordinate
	 */
	protected double[] midpoint;
	/**
	 * x-coordinates of the points that define scaleConic
	 */
	protected double[] originalPointX;
	/**
	 * y-coordinates of the points that define scaleConic
	 */
	protected double[] originalPointY;
	/**
	 * preview rectangle shape for text tool
	 */
	GRectangle textRectangleShape;

	protected Object detachFrom;
	protected boolean freehandModePrepared = false;
	protected long lastMousePressedTime;

	// ==============================================
	// Paste preview
	private double vertexX = Double.NaN;
	private double vertexY = Double.NaN;
	private ModeDeleteInterface deleteMode;
	private ModeShape shapeMode;
	private GPoint2D.Double startPoint = new GPoint2D.Double();
	private boolean externalHandling;
	private long lastMouseRelease;
	private long lastTouchRelease;
	private boolean animationButtonPressed = false;
	private boolean textfieldHasFocus = false;
	private MyButton pressedButton;
	private Coords tmpCoordsL4;
	private Coords mouseLocRW;
	private TextDispatcher textDispatcher;
	private double initxRW = Double.NaN;
	private double initFactor = Double.NaN;
	private boolean checkBoxOrButtonJustHitted = false;
	// make sure scripts not run twice
	private boolean scriptsHaveRun = false;
	private GPoint lastMouseUpLoc;
	private boolean checkboxChangeOccured = false;
	private PointerEventType defaultEventType = PointerEventType.MOUSE;
	private boolean detachFromPath;
	private boolean detachFromRegion;
	private boolean needsAttach = false;
	private boolean freehandModeSet = false;
	private int previousMode = -1;

	private boolean altCopy;

	private GeoNumeric circleRadius;
	private PointerEventType oldEventType = PointerEventType.MOUSE;
	private Runnable pointerUpCallback;

	protected double newZero;
	protected double newScale;
	private boolean objectMenuActive;
	private List<CoordSystemListener> zoomerListeners = new LinkedList<>();
	private MyModeChangedListener modeChangeListener = null;

	private SelectionToolPressResult lastSelectionPressResult = SelectionToolPressResult.DEFAULT;
	private GeoElement lastSelectionToolGeoToRemove;
	private ArrayList<GeoElement> moveMultipleObjectsList;
	protected ArrayList<GeoElement> previewPointHits = new ArrayList<>();
	private long draggingDelay = EuclidianConstants.DRAGGING_DELAY;

	private GeoFrame lastVideo = null;
	private boolean videoMoved;
	private boolean popupJustClosed = false;
	private ModeMacro modeMacro;

	private SnapController snapController = new SnapController();

	/**
	 * state for selection tool over press/release
	 */
	private enum SelectionToolPressResult {
		/** default state */
		DEFAULT,
		/** on press there were no hit */
		EMPTY,
		/**
		 * on press there were one already selected geo, we want to remove it on release
		 * except dragging occurs
		 */
		REMOVE,
		/**
		 * on press there were one not already selected geo, we add it on press and
		 * don't want to remove it on release
		 */
		ADD
	}

	/**
	 * @param app
	 *            application
	 */
	public EuclidianController(App app) {
		this.app = app;
		this.selection = app.getSelectionManager();
		this.localization = app.getLocalization();
		createCompanions();
	}

	protected static void removeAxes(ArrayList<GeoElement> geos) {
		for (int i = geos.size() - 1; i >= 0; i--) {
			GeoElement geo = geos.get(i);
			if (geo instanceof GeoAxis) {
				geos.remove(i);
			}
		}
	}

	/**
	 * ensure that the point will show 2D cartesion coords
	 *
	 * @param point
	 *            point
	 */
	private static void checkCoordCartesian(GeoPointND point) {
		if (point.getToStringMode() != Kernel.COORD_CARTESIAN) {
			point.setCartesian();
			point.updateRepaint();
		}
	}

	/**
	 * update the moved geo
	 */
	protected static void updateAfterMove(GeoElementND geo,
			boolean repaint) {
		if (repaint) {
			geo.updateRepaint();
		} else {
			geo.updateCascade();
		}
	}

	/**
	 * @param mode2
	 *            app mode
	 * @return whether it's a pen or freehand mode
	 */
	public boolean penMode(int mode2) {
		switch (mode2) {
		case EuclidianConstants.MODE_PEN:
			// case EuclidianConstants.MODE_PENCIL:
		case EuclidianConstants.MODE_FREEHAND_SHAPE:
		case EuclidianConstants.MODE_HIGHLIGHTER:
			return true;
		}
		return false;
	}

	/**
	 * @param modeConst
	 *            app mode
	 * @return whether it's one of the shape modes (MOW)
	 */
	public boolean shapeMode(int modeConst) {
		switch (modeConst) {
		case EuclidianConstants.MODE_SHAPE_CIRCLE:
		case EuclidianConstants.MODE_SHAPE_ELLIPSE:
		case EuclidianConstants.MODE_SHAPE_FREEFORM:
		case EuclidianConstants.MODE_SHAPE_LINE:
		case EuclidianConstants.MODE_SHAPE_POLYGON:
		case EuclidianConstants.MODE_SHAPE_RECTANGLE:
		case EuclidianConstants.MODE_SHAPE_RECTANGLE_ROUND_EDGES:
		case EuclidianConstants.MODE_SHAPE_SQUARE:
		case EuclidianConstants.MODE_SHAPE_TRIANGLE:
			return true;

		}
		return false;
	}

	/**
	 * @return whether multiple elements are selected (MOW)
	 */
	public boolean isMultiSelection() {
		return (mode == EuclidianConstants.MODE_SELECT_MOW
				|| (mode == EuclidianConstants.MODE_TRANSLATEVIEW
						&& temporaryMode
						&& oldMode == EuclidianConstants.MODE_SELECT_MOW))
				&& selection.getSelectedGeos().size() > 1;
	}

	private static boolean modeCreatesHelperPoints(int mode2) {
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

	/**
	 *
	 * @param mode2
	 *            EV mode
	 * @return true if the mode should trigger undo when dragging a geo
	 */
	protected boolean modeTriggersUndoOnDragGeo(int mode2) {
		return true;
	}

	ModeDeleteInterface getDeleteMode() {
		if (deleteMode == null && view != null) {
			deleteMode = view.getApplication().has(Feature.MOW_PEN_IS_LOCUS) 
					? new ModeDeleteLocus(view) : new ModeDelete(view);
		}
		return deleteMode;
	}

	ModeShape getShapeMode() {
		if (shapeMode == null && view != null) {
			shapeMode = new ModeShape(view);
		}
		return shapeMode;
	}

	/**
	 * @return shape being resized by bounding box
	 */
	public Drawable getResizedShape() {
		return resizedShape;
	}

	/**
	 * @param resizedShape
	 *            shape being resized by boundong box
	 */
	public void setResizedShape(Drawable resizedShape) {
		this.resizedShape = resizedShape;
	}

	protected void createCompanions() {
		this.companion = newCompanion();
	}

	/**
	 * @return 2D, 3D or plane companion
	 */
	public EuclidianControllerCompanion getCompanion() {
		// attempted fix for
		// java.lang.NullPointerException
		// at
		// EuclidianController.getCompanion(EuclidianController.java:452)
		// https://play.google.com/apps/publish/?dev_acc=05873811091523087820#ErrorClusterDetailsPlace:p=org.geogebra.android&et=CRASH&lr=LAST_7_DAYS&ecn=java.lang.NullPointerException&tf=SourceFile&tc=org.geogebra.android.gui.dialogs.RegularPolygonDialog&tm=doneButtonClicked&nid&an&c&s=new_status_desc
		createCompanionsIfNeeded();
		return companion;
	}

	protected void createCompanionsIfNeeded() {
		if (companion == null) {
			createCompanions();
		}
	}

	protected EuclidianControllerCompanion newCompanion() {
		return new EuclidianControllerCompanion(this);
	}

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
		MoveGeos.moveObjects(pastePreviewSelected, translationVec,
				tmpCoordsL3, null, view);
	}

	private void setPastePreviewPosition() {
		if (translationVec == null) {
			translationVec = new Coords(2);
		}
		double middleX = view.toRealWorldCoordX(view.getWidth() / 2.0);
		double middleY = view.toRealWorldCoordY(view.getHeight() / 2.0);
		translationVec.setX(middleX - getStartPointX());
		translationVec.setY(middleY - getStartPointY());
		setStartPointLocation(middleX, middleY);
		if (tmpCoordsL3 == null) {
			tmpCoordsL3 = new Coords(3);
		}
		tmpCoordsL3.setX(middleX);
		tmpCoordsL3.setY(middleY);
		tmpCoordsL3.setZ(0);
		MoveGeos.moveObjects(pastePreviewSelected, translationVec,
				tmpCoordsL3, null, view);
	}

	/**
	 * Select paste preview.
	 */
	public final void setPastePreviewSelected() {
		// don't allow paste on top of another paste until its placed
		if (pastePreviewSelected != null) {
			while (!pastePreviewSelected.isEmpty()) {
				GeoElement geo = pastePreviewSelected.get(0);
				pastePreviewSelected.remove(geo);
				geo.remove();
			}
		} else {
			pastePreviewSelected = new ArrayList<>();
		}
		pastePreviewSelectedAndDependent = new ArrayList<>();
		pastePreviewSelectedAndDependent.addAll(getAppSelectedGeos());

		boolean firstMoveable = true;
		for (GeoElement geo : getAppSelectedGeos()) {
			if ((geo.isIndependent() || geo.isPenStroke())
					&& geo.isMoveable()) {
				pastePreviewSelected.add(geo);
				if (firstMoveable) {
					if (geo.isGeoPoint()) {
						setStartPointLocation(((GeoPointND) geo).getInhomX(),
								((GeoPointND) geo).getInhomY());
						firstMoveable = false;
					} else if (geo instanceof GeoText) {
						if (((GeoText) geo).hasAbsoluteLocation()) {
							GeoPointND loc = ((GeoText) geo).getStartPoint();
							if (loc != null) {
								setStartPointLocation(loc.getInhomX(),
										loc.getInhomY());
							} else {
								double x = ((GeoText) geo)
										.getAbsoluteScreenLocX();
								double y = ((GeoText) geo)
										.getAbsoluteScreenLocY();
								x = (int) view.toRealWorldCoordX(x);
								y = (int) view.toRealWorldCoordY(y);
								setStartPointLocation(x, y);
							}
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
								setStartPointLocation(loc.inhomX, loc.inhomY);
								firstMoveable = false;
							} else {
								loc = ((GeoImage) geo).getStartPoint();
								if (loc != null) { // bottom left defined
									setStartPointLocation(loc.inhomX,
											loc.inhomY);
									firstMoveable = false;
								} else {
									loc = ((GeoImage) geo).getStartPoints()[1];
									if (loc != null) { // bottom right defined
										setStartPointLocation(loc.inhomX,
												loc.inhomY);
										firstMoveable = false;
									}
								}
							}
						}
					} else if (geo.isGeoBoolean()) {
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
					} else if (geo instanceof GeoConic) {
						updateConicForPaste((GeoConic) geo);
					} else if (geo instanceof GeoLocusStroke) {
						setStartPointLocation(
								((GeoLocusStroke) geo).getPoints().get(0)
										.getX(),
								((GeoLocusStroke) geo).getPoints().get(0)
										.getY());
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
			view.setPointCapturing(
					EuclidianStyleConstants.POINT_CAPTURING_STICKY_POINTS);

			// remove moved points from sticky points temporarily
			for (GeoElement geo : pastePreviewSelectedAndDependent) {
				if (geo instanceof GeoPointND) {
					if (view.getStickyPointList().contains(geo)) {
						view.getStickyPointList().remove(geo);
					}
				}
			}
			persistentStickyPointList = new ArrayList<>();
			persistentStickyPointList.addAll(view.getStickyPointList());

			setPastePreviewPosition();
			kernel.notifyRepaint();
		}
	}

	private void updateConicForPaste(GeoConic geo) {
		if (geo.isCircle()) {
			geo.setCircle(
					new GeoPoint(kernel.getConstruction(),
							view.toRealWorldCoordX(view.getWidth() / 2.0),
							view.toRealWorldCoordY(view.getHeight() / 2.0), 1),
					geo.getCircleRadius());
		} else if (geo.isEllipse()) {
			geo.translate(
					view.toRealWorldCoordX(view.getWidth() / 2.0)
							- geo.getMidpoint().getX(),
					view.toRealWorldCoordY(view.getHeight() / 2.0)
							- geo.getMidpoint().getY());
			geo.updateRepaint();
		}
	}

	/**
	 * @return whether the previous paste is finished (no paste preview)
	 */
	public boolean mayPaste() {
		return pastePreviewSelected == null
				|| pastePreviewSelected.isEmpty();
	}

	/**
	 * Remove all paste preview objects.
	 */
	public void deletePastePreviewSelected() {
		if (pastePreviewSelected != null) {
			while (!pastePreviewSelected.isEmpty()) {
				GeoElement geo = pastePreviewSelected.get(0);
				pastePreviewSelected.remove(geo);
				geo.remove();
			}
			pastePreviewSelected = null;
		}
		pastePreviewSelectedAndDependent = null;
	}

	/**
	 * Merge points after paste.
	 */
	public void mergeStickyPointsAfterPaste() {
		EvalInfo info = new EvalInfo(
				!kernel.getConstruction().isSuppressLabelsActive(), true);
		for (GeoElement geo : pastePreviewSelected) {
			if (geo.isGeoPoint() && (geo instanceof GeoPoint)
					&& geo.isIndependent()) {
				for (GeoPointND geo2 : persistentStickyPointList) {
					if (DoubleUtil.isEqual(geo2.getInhomX(),
							((GeoPoint) geo).getInhomX())
							&& DoubleUtil.isEqual(geo2.getInhomY(),
							((GeoPoint) geo).getInhomY())) {
						geo.setEuclidianVisible(false);
						String geolabel = geo.getLabelSimple();

						kernel.getAlgebraProcessor()
								.changeGeoElementNoExceptionHandling(geo,
										geo2.wrap(), info, false, null,
										ErrorHelper.silent());
						GeoElement newGeo = kernel.lookupLabel(geolabel);
						if (newGeo != null) {
							newGeo.setEuclidianVisible(false);
							newGeo.updateRepaint();
						}

						break;
					}
				}
			}
		}
	}

	/**
	 * @return previous mode
	 */
	public int getPreviousMode() {
		return previousMode;
	}

	/**
	 * @return current mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @return whether current mode has a dialog with input
	 */
	public boolean modeNeedsKeyboard() {
		return getMode() == EuclidianConstants.MODE_ANGLE_FIXED
				|| getMode() == EuclidianConstants.MODE_SEGMENT_FIXED
				|| getMode() == EuclidianConstants.MODE_REGULAR_POLYGON
				|| getMode() == EuclidianConstants.MODE_CIRCLE_POINT_RADIUS
				|| getMode() == EuclidianConstants.MODE_ROTATE_BY_ANGLE
				|| getMode() == EuclidianConstants.MODE_SLIDER
				|| getMode() == EuclidianConstants.MODE_TEXT
				|| getMode() == EuclidianConstants.MODE_BUTTON_ACTION
				|| getMode() == EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX
				|| getMode() == EuclidianConstants.MODE_TEXTFIELD_ACTION;
	}

	/**
	 * @param newMode
	 *            app mode
	 * @param ms
	 *            type of mode setting event
	 */
	public void setMode(int newMode, ModeSetter ms) {
		if (getModeChangeListener() != null && !temporaryMode) {
			getModeChangeListener().onModeChange(newMode);
		}
		if (pen != null) {
			pen.resetPenOffsets();
		}

		if (view.getShapePolygon() != null) {
			view.setShapePolygon(null);
			getShapeMode().clearPointList();
			view.repaintView();
		}

		// GGB-545
		// problem with
		// http://tube-beta.geogebra.org/student/99999?cb=jenkins4576
		// view.closeDropdowns();

		if ((newMode == EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS)
				|| (newMode == EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS)
				|| (newMode == EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS)) {
			return;
		}

		if (ms == ModeSetter.TOOLBAR) {
			if (app.getGuiManager() != null) {
				switch (newMode) {
				case EuclidianConstants.MODE_CAMERA:
					app.getGuiManager().loadWebcam();
					return;
					
				case EuclidianConstants.MODE_AUDIO:
					getDialogManager().showAudioInputDialog();
					break;
					
				case EuclidianConstants.MODE_VIDEO:
					getDialogManager().showVideoInputDialog();
					break;
					
				case EuclidianConstants.MODE_PDF:
					getDialogManager().showPDFInputDialog();
					break;

				case EuclidianConstants.MODE_GRASPABLE_MATH:
					app.getEmbedManager().openGraspableMTool();
					break;

				case EuclidianConstants.MODE_EXTENSION:
					getDialogManager().showEmbedDialog();
					break;
					
				default:
					break;
				}	
			}

			if (app.getEmbedManager() != null
					&& (newMode == EuclidianConstants.MODE_GRAPHING
					|| newMode == EuclidianConstants.MODE_CAS)) {
					setUpEmbedManager(newMode);
			}

			if (newMode == EuclidianConstants.MODE_IMAGE) {
				image(view.getHits().getOtherHits(TestGeo.GEOIMAGE, tempArrayList),
						false);
				app.setMode(EuclidianConstants.MODE_SELECT_MOW,
						ModeSetter.DOCK_PANEL);
				return;
			}

		}

		endOfMode(mode);

		allowSelectionRectangleForTranslateByVector = true;

		if (EuclidianView.usesSelectionRectangleAsInput(newMode)
				&& (view.getSelectionRectangle() != null)) {
			initNewMode(newMode);
			if (app.getActiveEuclidianView() == view) {
				processSelectionRectangle(false, false, false);
			}
		} else if (EuclidianView.usesSelectionAsInput(newMode)) {
			initNewMode(newMode);
			if (app.getActiveEuclidianView() == view) {
				processSelection();
			}

		} else {
			boolean clear = !EuclidianConstants.isMoveOrSelectionMode(mode)
					|| !EuclidianConstants.keepSelectionWhenSet(newMode);
			if (!temporaryMode && clear) {
				selection.clearSelectedGeos(false);
				resetMovedGeoPoint();
			}
			initNewMode(newMode, clear);
		}

		kernel.notifyRepaint();
	}

	private void setUpEmbedManager(int mode) {
		GeoEmbed ge = new GeoEmbed(kernel.getConstruction());
		if (mode == EuclidianConstants.MODE_CAS) {
			ge.setAppName("cas");
		}
		ge.initPosition(view);
		ge.setEmbedId(app.getEmbedManager().nextID());
		ge.setLabel(null);
		app.storeUndoInfo();
		app.invokeLater(new Runnable() {

			@Override
			public void run() {
				app.setMode(EuclidianConstants.MODE_SELECT_MOW,
						ModeSetter.DOCK_PANEL);
			}
		});
	}

	/**
	 * @return whether current mode is undoable
	 */
	public boolean isUndoableMode() {
		switch (mode) {
		case EuclidianConstants.MODE_MOVE:
		case EuclidianConstants.MODE_SELECT:
		case EuclidianConstants.MODE_SELECT_MOW:
		case EuclidianConstants.MODE_TEXT:
		case EuclidianConstants.MODE_DELETE:
		case EuclidianConstants.MODE_RELATION:
		case EuclidianConstants.MODE_SLIDER:
		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
		case EuclidianConstants.MODE_SHOW_HIDE_LABEL:
		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
		case EuclidianConstants.MODE_ZOOM_IN:
		case EuclidianConstants.MODE_ZOOM_OUT:
		case EuclidianConstants.MODE_SELECTION_LISTENER:
		case EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX:
		case EuclidianConstants.MODE_BUTTON_ACTION:
		case EuclidianConstants.MODE_TEXTFIELD_ACTION:
		case EuclidianConstants.MODE_PEN:
		case EuclidianConstants.MODE_PROBABILITY_CALCULATOR:
		case EuclidianConstants.MODE_FREEHAND_SHAPE:
		case EuclidianConstants.MODE_VIEW_IN_FRONT_OF:
			return false;
		}

		return mode < EuclidianConstants.MODE_CAS_EVALUATE;
	}

	/**
	 * @return current move mode
	 */
	public int getMoveMode() {
		return moveMode;
	}

	protected void endOfMode(int endMode) {
		switch (endMode) {
		default:
			// do nothing
			break;
		case EuclidianConstants.MODE_MOVE:
			// deletePastePreviewSelected();
			break;

		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
			// take all selected objects and hide them
			for (GeoElement geo : getAppSelectedGeos()) {
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
			storeUndoInfo();
		}
	}

	protected final void clearSelection(ArrayList<?> selectionList) {
		selection.clearSelection(selectionList, true);
		view.repaintView();
	}

	protected Hits getRegionHits(Hits hits) {
		return hits.getRegionHits(tempRegionHitsArrayList);
	}

	protected GeoPointND getSingleIntersectionPoint(Hits hits) {
		if (hits.isEmpty() || (hits.size() < 2)) {
			return null;
		}

		GeoElement a = hits.get(0);
		GeoElement b = hits.get(1);

		return companion.getSingleIntersectionPoint(a, b, true);
	}

	/**
	 * Copy list of selected geos and clear the original.
	 * 
	 * @return selected geos
	 */
	public final GeoElement[] getSelectedGeos() {
		GeoElement[] selected = getSelectedGeoList().toArray(new GeoElement[0]);
		clearSelection(getSelectedGeoList());
		return selected;
	}

	/**
	 * return selected points as ND points
	 *
	 * @return selected points
	 */
	protected final GeoPointND[] getSelectedPointsND() {
		GeoPointND[] selected = getSelectedPointList().toArray(new GeoPointND[0]);
		clearSelection(getSelectedPointList());
		return selected;
	}

	protected final GeoPoint[] getSelectedPoints() {
		GeoPoint[] selected = getSelectedPointList().toArray(new GeoPoint[0]);
		clearSelection(getSelectedPointList());
		return selected;
	}

	protected final GeoNumeric[] getSelectedNumbers() {
		GeoNumeric[] selected = getSelectedNumberList().toArray(new GeoNumeric[0]);
		clearSelection(getSelectedNumberList());
		return selected;
	}

	protected final GeoNumberValue[] getSelectedNumberValues() {
		GeoNumberValue[] selected = getSelectedNumberValueList().toArray(new GeoNumberValue[0]);
		clearSelection(getSelectedNumberValueList());
		return selected;
	}

	protected final GeoList[] getSelectedLists() {
		GeoList[] selected = getSelectedListList().toArray(new GeoList[0]);
		clearSelection(getSelectedListList());
		return selected;
	}

	protected final GeoPolygon[] getSelectedPolygons() {
		GeoPolygon[] selected = getSelectedPolygonList().toArray(new GeoPolygon[0]);
		clearSelection(getSelectedPolygonList());
		return selected;
	}

	protected final GeoPolyLine[] getSelectedPolyLines() {
		GeoPolyLine[] selected = getSelectedPolyLineList().toArray(new GeoPolyLine[0]);
		clearSelection(getSelectedPolyLineList());
		return selected;
	}

	protected final GeoLineND[] getSelectedLinesND() {
		GeoLineND[] selected = getSelectedLineList().toArray(new GeoLineND[0]);
		clearSelection(getSelectedLineList());
		return selected;
	}

	protected final GeoLine[] getSelectedLines() {
		GeoLine[] selected = getSelectedLineList().toArray(new GeoLine[0]);
		clearSelection(getSelectedLineList());
		return selected;
	}

	protected final GeoSegmentND[] getSelectedSegmentsND() {
		GeoSegmentND[] selected = getSelectedSegmentList().toArray(new GeoSegmentND[0]);
		clearSelection(getSelectedSegmentList());
		return selected;
	}

	protected final GeoSegment[] getSelectedSegments() {
		GeoSegment[] selected = getSelectedSegmentList().toArray(new GeoSegment[0]);
		clearSelection(getSelectedSegmentList());
		return selected;
	}

	protected final GeoVectorND[] getSelectedVectorsND() {
		GeoVectorND[] selected = getSelectedVectorList().toArray(new GeoVectorND[0]);
		clearSelection(getSelectedVectorList());
		return selected;
	}

	protected final GeoVector[] getSelectedVectors() {
		GeoVector[] selected = getSelectedVectorList().toArray(new GeoVector[0]);
		clearSelection(getSelectedVectorList());
		return selected;
	}

	protected final GeoConicND[] getSelectedConicsND() {
		GeoConicND[] selected = getSelectedConicNDList().toArray(new GeoConicND[0]);
		clearSelection(getSelectedConicNDList());
		return selected;
	}

	protected final GeoConic[] getSelectedConics() {
		GeoConic[] selected = getSelectedConicNDList().toArray(new GeoConic[0]);
		clearSelection(getSelectedConicNDList());
		return selected;
	}

	protected final GeoDirectionND[] getSelectedDirections() {
		GeoDirectionND[] selected = getSelectedDirectionList().toArray(new GeoDirectionND[0]);
		clearSelection(getSelectedDirectionList());
		return selected;
	}

	protected final Region[] getSelectedRegions() {
		Region[] selected = getSelectedRegionList().toArray(new Region[0]);
		clearSelection(getSelectedRegionList());
		return selected;
	}

	protected final Path[] getSelectedPaths() {
		Path[] selected = getSelectedPathList().toArray(new Path[0]);
		clearSelection(getSelectedPathList());
		return selected;
	}

	protected final GeoImplicit[] getSelectedImplicitpoly() {
		GeoImplicit[] selected = getSelectedImplicitpolyList().toArray(new GeoImplicit[0]);
		clearSelection(getSelectedImplicitpolyList());
		return selected;
	}

	protected final GeoImplicitSurfaceND[] getSelectedImplicitSurface() {
		GeoImplicitSurfaceND[] selected = getSelectedImplicitSurfaceList()
				.toArray(new GeoImplicitSurfaceND[0]);
		clearSelection(getSelectedImplicitSurfaceList());
		return selected;
	}

	protected final GeoFunction[] getSelectedFunctions() {
		GeoFunction[] selected = getSelectedFunctionList().toArray(new GeoFunction[0]);
		clearSelection(getSelectedFunctionList());
		return selected;
	}

	protected final GeoFunctionNVar[] getSelectedFunctionsNVar() {
		GeoFunctionNVar[] selected = getSelectedFunctionNVarList().toArray(new GeoFunctionNVar[0]);
		clearSelection(getSelectedFunctionNVarList());
		return selected;
	}

	protected final GeoCurveCartesian[] getSelectedCurves() {
		GeoCurveCartesian[] selected = getSelectedCurveList().toArray(new GeoCurveCartesian[0]);
		clearSelection(getSelectedCurveList());
		return selected;
	}

	protected final GeoConic[] getSelectedCircles() {
		GeoConic[] circles = new GeoConic[getSelectedConicNDList().size()];
		int i = 0;
		for (GeoConicND c : getSelectedConicNDList()) {
			if (c.isCircle()) {
				circles[i] = (GeoConic) c;
				i++;
			}
		}
		clearSelection(getSelectedConicNDList());
		return circles;
	}

	/***************************************************************************
	 * mode implementations
	 * <p/>
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
	 * @param label
	 *            point label
	 * @param forPreviewable
	 *            preview?
	 * @param path
	 *            parent path
	 * @param x
	 *            x-coord in RW
	 * @param y
	 *            y-coord in RW
	 * @param complexPoint
	 *            complex?
	 * @param coords2D
	 *            whether to use 2D coords
	 * @return point
	 */
	public GeoPointND createNewPoint2D(String label, boolean forPreviewable,
			Path path, double x, double y, boolean complexPoint, boolean coords2D) {
		return getAlgoDispatcher().point(label, path, x, y, !forPreviewable,
				complexPoint, coords2D);
	}

	final protected GeoPointND createNewPoint2D(String label,
			boolean forPreviewable, Region region, double x, double y,
			boolean complexPoint, boolean coords2D) {
		return getAlgoDispatcher().pointIn(label, region, x, y,
				!forPreviewable, complexPoint, coords2D);
	}

	/**
	 * @param label
	 *            point label
	 * @param forPreviewable
	 *            preview?
	 * @param region
	 *            parent region
	 * @param x
	 *            x-coord in RW
	 * @param y
	 *            y-coord in RW
	 * @param z
	 *            z-coord in RW
	 * @param complexPoint
	 *            complex?
	 * @param coords2D
	 *            whether to use 2D coords
	 * @return point
	 */
	final public GeoPointND createNewPoint(String label, boolean forPreviewable,
			Region region, double x, double y, double z, boolean complexPoint,
			boolean coords2D) {
		if (region.toGeoElement().isGeoElement3D()) {
			if (tmpCoordsL4 == null) {
				tmpCoordsL4 = new Coords(4);
			}
			tmpCoordsL4.setX(x);
			tmpCoordsL4.setY(y);
			tmpCoordsL4.setZ(z);
			tmpCoordsL4.setW(1);

			return kernel.getManager3D().point3DIn(label, region,
					tmpCoordsL4, !forPreviewable, coords2D);
		}
		return createNewPoint2D(label, forPreviewable, region, x, y, complexPoint,
				coords2D);
	}

	public Kernel getKernel() {
		return kernel;
	}

	public void setKernel(Kernel kernel) {
		this.kernel = kernel;
	}

	/**
	 * Clear the list of just created geos
	 */
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

	/**
	 * @return newly created geos
	 */
	public ArrayList<GeoElement> getJustCreatedGeos() {
		return justCreatedGeos;
	}

	/**
	 * @param geos
	 *            newly created geos
	 */
	public void memorizeJustCreatedGeos(ArrayList<GeoElement> geos) {
		justCreatedGeos.clear();
		justCreatedGeos.addAll(geos);
		if (app.isUsingFullGui() && app.getGuiManager() != null) {
			app.updateStyleBars();
			app.getGuiManager().updateMenubarSelection();
		}
	}

	/**
	 * TODO copied; use the array version instead
	 * 
	 * @param geos
	 *            newly created geos
	 */
	public void memorizeJustCreatedGeos(GeoElementND[] geos) {
		justCreatedGeos.clear();
		for (GeoElementND geo : geos) {
			if (geo != null) {
				justCreatedGeos.add(geo.toGeoElement());
			}
		}
		if (app.isUsingFullGui() && app.getGuiManager() != null) {
			app.updateStyleBars();
			app.getGuiManager().updateMenubarSelection();
		}
	}

	protected final void setHighlightedGeos() {
		for (GeoElement highlightedGeo : highlightedGeos) {
			highlightedGeo.setHighlighted(false);
		}
	}

	/**
	 * @param geo
	 *            geo to highlight
	 */
	public final void doSingleHighlighting(GeoElement geo) {
		if (geo == null) {
			return;
		}

		if (highlightedGeos.size() > 0) {
			setHighlightedGeos();
		}

		highlightedGeos.add(geo);
		geo.setHighlighted(true);
		kernel.notifyRepaint();
	}

	/**
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
		for (GeoElement geo : selGeos) {
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
				cp.getParentAlgorithm().removeInputPoints(tempArrayList);
				break;

			case POLYGON:
				// remove points and segments of poly
				GeoPolygon poly = (GeoPolygon) geo;
				GeoPointND[] points = poly.getPoints();
				tempArrayList.removeAll(Arrays.asList(points));
				GeoSegmentND[] segs = poly.getSegments();
				tempArrayList.removeAll(Arrays.asList(segs));
				break;

			case POLYLINE:
				// remove points and segments of poly
				GeoPolyLine polyl = (GeoPolyLine) geo;
				points = polyl.getPoints();
				tempArrayList.removeAll(Arrays.asList(points));
				break;

			default:
				// do nothing
			}
		}

		selGeos.clear();
		selGeos.addAll(tempArrayList);
	}

	protected final int addToHighlightedList(ArrayList<?> selectionList,
			ArrayList<GeoElement> geos, int max) {
		if (geos == null) {
			return 0;
		}

		int ret = 0;
		for (GeoElement geo : geos) {
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
			break;

		default:
			int maxLayer = -1;

			int layerCount = 0;

			// work out max layer, and
			// count no of objects in max layer
			for (GeoElement geo : geos) {
				int layer = geo.getLayer();

				if ((layer > maxLayer) && (includeFixed || !geo.isLocked())) {
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
			int minIndex = Integer.MAX_VALUE;

			// count no of points in top layer
			for (GeoElement geo : geos) {
				if (geo.isGeoPoint() && (geo.getLayer() == maxLayer)
						&& (includeFixed || !geo.isLocked())) {
					pointCount++;
					ret = geo;

					// find point with the lowest construction index
					// changed from highest so that tessellation works
					// eg two points like (a + x(A), b + y(A))
					// we want to drag the older one
					int consIndex = geo.getConstructionIndex();
					if (consIndex < minIndex) {
						minIndex = consIndex;
						retIndex = geo;
					}

					// find point-on-path/region with the highest construction
					// index
					if (geo.isPointOnPath() || geo.isPointInRegion()) {
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

			boolean allFixed = false;

			// remove fixed objects (if there are some not fixed)
			if (!includeFixed && (geos.size() > 1)) {

				allFixed = true;
				for (GeoElement geo : geos) {
					if (!geo.isLocked()) {
						allFixed = false;
					}
				}

				if (!allFixed) {
					for (int i = geos.size() - 1; i >= 0; i--) {
						GeoElement geo = geos.get(i);
						if (geo.isLocked()) {
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
			for (GeoElement geo : geos) {
				if (geo.isGeoSegment()) {
					segmentCount++;
					if (retSegment == null) {
						retSegment = geo;
					} else {
						// select Segment with lowest layer (& construction
						// index)
						if ((retSegment.getLayer() < geo.getLayer())
								|| ((retSegment.getLayer() == geo.getLayer())
								&& (retSegment.getConstructionIndex()
								> geo.getConstructionIndex()))) {
							retSegment = geo;
						}
					}
				}
			}

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

			// now just choose geo with highest drawing priority:
			ret = geos.get(0);

			for (GeoElement geo : geos) {
				// other not drawn before = other is on top
				if (!geo.drawBefore(ret, true)) {
					ret = geo;
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
	protected GeoElement chooseGeo(Hits hits, TestGeo geoclass) {
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
	 * @return 0/1/-1 if nothing happened / geo selected / geo unselected
	 */
	@SuppressWarnings("unchecked")
	protected final <T extends GeoElementND> int addToSelectionList(
			ArrayList<T> selectionList, ArrayList<GeoElement> geos, int max,
			boolean addMoreThanOneAllowed, boolean tryDeselect) {
		if (geos == null) {
			return 0;
		}

		// ONLY ONE ELEMENT IN THE EFFECTIVE HITS
		if (tryDeselect && (geos.size() == 1)) {
			// select or deselect it
			return selection.addToSelectionList(selectionList, (T) geos.get(0),
					max);
		}

		// SEVERAL ELEMENTS
		// here none of the selected geos should be removed

		// we don't want to add repeated elements
		geos.removeAll(selectionList);
		// too many objects -> choose one
		if (!addMoreThanOneAllowed
				|| ((geos.size() + selectionList.size()) > max)) {
			return selection.addToSelectionList(selectionList,
					(T) chooseGeo(geos, true, true), max);
		}

		// already selected objects -> choose one
		boolean contained = false;
		for (GeoElement geo : geos) {
			if (selectionList.contains(geo)) {
				contained = true;
			}
		}
		if (contained) {
			return selection.addToSelectionList(selectionList,
					(T) chooseGeo(geos, true, true), max);
		}

		// add all objects to list
		int count = 0;
		for (GeoElement geo : geos) {
			count += selection.addToSelectionList(selectionList,
					(T) geo, max);
		}
		return count;
	}

	public final int selGeos() {
		return getSelectedGeoList().size();
	}

	public final int selPoints() {
		return getSelectedPointList().size();
	}

	protected final int selNumbers() {
		return getSelectedNumberList().size();
	}

	protected final int selNumberValues() {
		return getSelectedNumberValueList().size();
	}

	protected final int selLists() {
		return getSelectedListList().size();
	}

	protected final int selPolyLines() {
		return getSelectedPolyLineList().size();
	}

	public final int selPolygons() {
		return getSelectedPolygonList().size();
	}

	protected final int selLines() {
		return getSelectedLineList().size();
	}

	protected final int selDirections() {
		return getSelectedDirectionList().size();
	}

	protected final int selSegments() {
		return getSelectedSegmentList().size();
	}

	protected final int selVectors() {
		return getSelectedVectorList().size();
	}

	protected final int selConics() {
		return getSelectedConicNDList().size();
	}

	protected final int selPaths() {
		return getSelectedPathList().size();
	}

	protected final int selRegions() {
		return getSelectedRegionList().size();
	}

	protected final int selImplicitpoly() {
		return getSelectedImplicitpolyList().size();
	}

	protected final int selFunctions() {
		return getSelectedFunctionList().size();
	}

	protected final int selFunctionsNVar() {
		return getSelectedFunctionNVarList().size();
	}

	protected final int selCurves() {
		return getSelectedCurveList().size();
	}

	/**
	 * @return 0/1/-1 if nothing happened / geo selected / geo unselected
	 */
	public int handleAddSelected(Hits hits, int max, boolean addMore,
			ArrayList<? extends GeoElementND> list, TestGeo geoClass,
			boolean selPreview) {
		if (selPreview) {
			return addToHighlightedList(list,
					hits.getHits(geoClass, handleAddSelectedArrayList), max);
		}
		return addToSelectionList(list,
				hits.getHits(geoClass, handleAddSelectedArrayList), max,
				addMore, hits.size() == 1);
	}

	protected int handleAddSelectedRegions(Hits hits, int max, boolean addMore,
			ArrayList<Region> list, boolean selPreview) {
		if (selPreview) {
			return addToHighlightedList(list,
					hits.getRegionHits(handleAddSelectedArrayList), max);
		}
		return addToSelectionList(list,
				hits.getRegionHits(handleAddSelectedArrayList), max, addMore,
				hits.size() == 1);
	}

	/**
	 * @param hits
	 *            hits
	 * @param max
	 *            max size of selected geos after
	 * @param addMoreThanOneAllowed
	 *            allow adding more than one
	 * @param selPreview
	 *            whether to add to selection preview instead of selection
	 * @return 0/1/-1 if nothing happened / geo selected / geo unselected
	 */
	public final int addSelectedGeo(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedGeoList(), TestGeo.GEOELEMENT, selPreview);
	}

	protected final int addSelectedPoint(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedPointList(), TestGeo.GEOPOINTND, selPreview);
	}

	/**
	 * @param hits
	 *            hits
	 * @param max
	 *            max size of selected numbers after
	 * @param addMoreThanOneAllowed
	 *            allow adding more than one
	 * @param selPreview
	 *            whether to add to selection preview instead of selection
	 * @return 0/1/-1 if nothing happened / geo selected / geo unselected
	 */
	public final int addSelectedNumeric(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedNumberList(), TestGeo.GEONUMERIC, selPreview);
	}

	/**
	 * @param hits
	 *            hits
	 * @param max
	 *            max size of selected number values after
	 * @param addMoreThanOneAllowed
	 *            allow adding more than one
	 * @param selPreview
	 *            whether to add to selection preview instead of selection
	 * @return 0/1/-1 if nothing happened / geo selected / geo unselected
	 */
	public final int addSelectedNumberValue(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedNumberValueList(), TestGeo.NUMBERVALUE, selPreview);
	}

	protected final int addSelectedLine(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedLineList(), TestGeo.GEOLINEND, selPreview);
	}

	protected final int addSelectedSegment(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedSegmentList(), TestGeo.GEOSEGMENTND, selPreview);
	}

	protected final int addSelectedVector(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return addSelectedVector(hits, max, addMoreThanOneAllowed,
				TestGeo.GEOVECTORND, selPreview);
	}

	protected final int addSelectedVector(Hits hits, int max,
			boolean addMoreThanOneAllowed, TestGeo geoClass, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedVectorList(), geoClass, selPreview);
	}

	protected final int addSelectedPath(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedPathList(), TestGeo.PATH, selPreview);
	}

	protected final int addSelectedRegion(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelectedRegions(hits, max, addMoreThanOneAllowed,
				getSelectedRegionList(), selPreview);
	}

	protected final int addSelectedImplicitpoly(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedImplicitpolyList(), TestGeo.GEOIMPLICIT, selPreview);
	}

	protected final int addSelectedImplicitSurface(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedImplicitSurfaceList(), TestGeo.GEOIMPLICITSURFACE,
				selPreview);
	}

	protected final int addSelectedPolygon(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedPolygonList(), TestGeo.GEOPOLYGON, selPreview);
	}

	protected final int addSelectedSpecialPolygon(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview, TestGeo test) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedPolygonList(), test, selPreview);
	}

	protected final int addSelectedPolyLine(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedPolyLineList(), TestGeo.GEOPOLYLINE, selPreview);
	}

	protected final int addSelectedList(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedListList(), TestGeo.GEOLIST, selPreview);
	}

	protected final int addSelectedDirection(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedDirectionList(), TestGeo.GEODIRECTIONND, selPreview);
	}

	protected final int addSelectedConic(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedConicNDList(), TestGeo.GEOCONICND, selPreview);
	}

	protected final int addSelectedFunction(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedFunctionList(), TestGeo.GEOFUNCTION, selPreview);
	}

	protected final int addSelectedFunctionNVar(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedFunctionNVarList(), TestGeo.GEOFUNCTIONNVAR,
				selPreview);
	}

	protected final int addSelectedFunction2Var(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedFunctionNVarList(), TestGeo.GEOFUNCTION2VAR,
				selPreview);
	}

	protected final int addSelectedCurve(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				getSelectedCurveList(), TestGeo.GEOCURVECARTESIAN, selPreview);
	}

	/**
	 * Copy a point, only used in 3D
	 *
	 * @param sourcePoint
	 *            original point
	 */
	public void createNewPoint(GeoPointND sourcePoint) {
		// 3D
	}

	/**
	 * only used in 3D
	 *
	 * @param intersectionPoint
	 *            original point
	 */
	protected void createNewPointIntersection(GeoPointND intersectionPoint) {
		// 3D
	}

	protected final GeoElement[] join(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		if (draggingOccurredBeforeRelease(selPoints() == 0)) {
			return null;
		}

		// points needed
		addSelectedPoint(hits, 2, false, selPreview);
		if (selPoints() == 2) {
			// fetch the two selected points
			return join();
		}
		return null;
	}

	/**
	 * @param notAlreadyStarted
	 *            true current created geo is not already started
	 * @return true if dragging occurred before release, so maybe we don't want
	 *         to start new geo
	 */
	protected boolean draggingOccurredBeforeRelease(boolean notAlreadyStarted) {
		return false;
	}

	protected GeoElement[] join() {
		GeoPointND[] points = getSelectedPointsND();
		GeoElement[] ret = { null };
		if (points[0].isGeoElement3D() || points[1].isGeoElement3D()) {
			ret[0] = getKernel().getManager3D().line3D(null, points[0],
					points[1]);
		} else {
			ret[0] = getAlgoDispatcher().line(null, (GeoPoint) points[0],
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
		if (points[0].isGeoElement3D() || points[1].isGeoElement3D()) {
			ret[0] = getKernel().getManager3D()
					.ray3D(null, points[0], points[1]).toGeoElement();
		} else {
			ret[0] = getAlgoDispatcher().ray(null, (GeoPoint) points[0],
					(GeoPoint) points[1]);
		}
		return ret;
	}

	protected final GeoElement[] segment(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		if (draggingOccurredBeforeRelease(selPoints() == 0)) {
			return null;
		}

		// points needed
		addSelectedPoint(hits, 2, false, selPreview);
		if (selPoints() == 2) {
			// fetch the two selected points
			return segment();
		}
		return null;
	}

	private GeoElement[] segment() {
		GeoPointND[] points = getSelectedPointsND();
		GeoElement[] ret = companion
				.segmentAlgo(kernel.getConstruction(), points[0], points[1])
				.getOutput();
		ret[0].setLabel(null);
		return ret;
	}

	protected final GeoElement[] vector(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		// points needed
		addSelectedPoint(hits, 2, false, selPreview);
		if (selPoints() == 2) {
			// fetch the two selected points

			GeoPointND[] points = getSelectedPointsND();
			return new GeoElement[] { vector(points[0], points[1]) };
		}
		return null;
	}

	protected GeoElement vector(GeoPointND a, GeoPointND b) {
		return getAlgoDispatcher().vectorND(null, a, b);
	}

	protected final GeoElement[] ray(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		// points needed
		addSelectedPoint(hits, 2, false, selPreview);
		if (selPoints() == 2) {
			// fetch the two selected points
			return ray();
		}

		return null;
	}

	protected final GeoElement[] polygon(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		if (polygonMode == POLYGON_VECTOR) {
			addSelectedPolygon(hits, 1, false, selPreview);
			if (selPolygons() == 1) {
				GeoPolygon[] poly = getSelectedPolygons();

				GeoPointND[] points = poly[0].getPoints();

				GeoPointND[] pointsCopy = new GeoPointND[points.length];

				// make a free copy of all points
				for (int i = 0; i < points.length; i++) {
					pointsCopy[i] = points[i].copy();
					pointsCopy[i].setLabel(null);
				}

				GeoElement[] ret = new PolygonFactory(kernel).vectorPolygon(null, pointsCopy);

				// offset the copy slightly
				double offset = view.toRealWorldCoordX(view.getWidth()) / 15;

				((GeoPolygon) ret[0]).getPoints()[0].setCoords(
						pointsCopy[0].getInhomX() + offset,
						pointsCopy[0].getInhomY() - offset, 1.0);
				((GeoPolygon) ret[0]).getPoints()[0].updateRepaint();

				return ret;
			}
		} else if (polygonMode == POLYGON_RIGID) {
			addSelectedPolygon(hits, 1, false, selPreview);
			if (selPolygons() == 1) {
				GeoPolygon[] poly = getSelectedPolygons();

				// offset the copy slightly
				double offset = view.toRealWorldCoordX(view.getWidth()) / 15;

				return new PolygonFactory(kernel).rigidPolygon(poly[0], offset,
						-offset, null);
			}
		}

		if (draggingOccurredBeforeRelease(selPoints() == 0)) {
			return null;
		}

		// if the first point is clicked again, we are finished
		if (selPoints() > 2) {
			// check if first point was clicked again
			boolean finished = !selPreview
					&& hits.contains(getSelectedPointList().get(0));
			if (finished) {
				// build polygon
				this.kernel.addingPolygon();
				GeoElement[] elms = polygon();
				this.kernel.notifyPolygonAdded();
				return elms;
			}
		}

		// points needed
		if (((polygonMode == POLYGON_RIGID) || (polygonMode == POLYGON_VECTOR))
				&& (selPoints() > 0)) { // only want free points withput
			// children for rigid polys (apart from
			// first)
			// testing needed - see GGB-1982
			GeoElement geo = chooseGeo(hits, false);
			if ((geo == null) || !geo.isGeoPoint() || !geo.isIndependent()
					|| geo.hasChildren()) {
				return null;
			}
		}
		addSelectedPoint(hits, GeoPolygon.POLYGON_MAX_POINTS, false,
				selPreview);
		return null;
	}

	protected final GeoElement[] polyline(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		// if the first point is clicked again, we are finished
		if (selPoints() > 2) {
			// check if first point was clicked again
			boolean finished = !selPreview
					&& hits.contains(getSelectedPointList().get(0));
			if (finished) {
				// build polygon
				return kernel.polyLineND(null, getSelectedPointsND());
			}
		}

		// points needed
		addSelectedPoint(hits, GeoPolyLine.POLYLINE_MAX_POINTS, false,
				selPreview);
		return null;
	}

	protected GeoElement[] polygon() {
		if (polygonMode == POLYGON_RIGID) {
			GeoElement[] ret = { null };
			GeoElement[] ret0 = new PolygonFactory(kernel).rigidPolygon(null,
					getSelectedPointsND());
			if (ret0 != null) {
				ret[0] = ret0[0];
			}
			return ret;
		} else if (polygonMode == POLYGON_VECTOR) {
			GeoElement[] ret = { null };
			GeoElement[] ret0 = new PolygonFactory(kernel).vectorPolygon(null,
					getSelectedPointsND());
			if (ret0 != null) {
				ret[0] = ret0[0];
			}
			return ret;
		} else {

			GeoElement[] ret = { null };
			GeoElement[] ret0 = kernel.polygon(null, getSelectedPointsND());
			if (ret0 != null) {
				ret[0] = ret0[0];
			}
			return ret;
		}
	}

	protected GeoElementND[] intersect(Hits intersectHits, boolean selPreview) {
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
		final boolean initialSelectionEmpty = selGeos() == 0;

		// check how many interesting hits we have
		if (!selPreview && (hits.size() > (2 - selGeos()))) {
			Hits goodHits = new Hits();
			hits.getHits(TestGeo.GEOLINEND, tempArrayList);
			goodHits.addAll(tempArrayList);

			if (goodHits.size() < 2) {
				hits.getHits(TestGeo.GEOCONICND, tempArrayList);
				goodHits.addAll(tempArrayList);
			}
			if (goodHits.size() < 2) {
				hits.getHits(TestGeo.GEOFUNCTION, tempArrayList);
				goodHits.addAll(tempArrayList);
			}
			if (goodHits.size() < 2) {
				hits.getHits(TestGeo.GEOPOLYGON, tempArrayList);
				goodHits.addAll(tempArrayList);
			}
			if (goodHits.size() < 2) {
				hits.getHits(TestGeo.GEOPOLYLINE, tempArrayList);
				goodHits.addAll(tempArrayList);
			}

			hits = goodHits;
		}

		// get lines, conics and functions
		// now there's no popup chooser, when we use the intersect Tool where
		// multiple objects intersect
		// just choose any 2
		addSelectedLine(hits, 10, true, selPreview);
		addSelectedConic(hits, 10, true, selPreview);
		addSelectedFunction(hits, 10, true, selPreview);
		addSelectedImplicitpoly(hits, 10, true, selPreview);
		addSelectedPolygon(hits, 10, true, selPreview);
		addSelectedPolyLine(hits, 10, true, selPreview);
		addSelectedCurve(hits, 10, true, selPreview);

		boolean singlePointWanted = initialSelectionEmpty && (selGeos() >= 2);

		// two lines
		if (selLines() >= 2) {
			GeoLineND[] lines = getSelectedLinesND();

			GeoPointND point = getAlgoDispatcher().intersectLines(null,
					lines[0], lines[1]);
			checkCoordCartesian(point);
			return new GeoElementND[] { point };
		}
		// two conics
		else if (selConics() >= 2) {
			GeoConicND[] conics = getSelectedConicsND();
			GeoElementND[] ret = { null };
			if (singlePointWanted) {
				ret[0] = getAlgoDispatcher().intersectConicsSingle(null,
						(GeoConic) conics[0], (GeoConic) conics[1], xRW, yRW);
				checkCoordCartesian((GeoPointND) ret[0]);
			} else {
				ret = getAlgoDispatcher().intersectConics(null,
						conics[0], conics[1]);
				for (GeoElementND geo : ret) {
					checkCoordCartesian((GeoPointND) geo);
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

				return new GeoElement[] { getAlgoDispatcher()
						.intersectFunctions(null, fun[0], fun[1], initPoint) };
			}
			// polynomials
			if (singlePointWanted) {
				return new GeoElement[] {
						getAlgoDispatcher().intersectPolynomialsSingle(null,
								fun[0], fun[1], xRW, yRW) };
			}
			return getAlgoDispatcher().intersectPolynomials(null, fun[0],
					fun[1]);
		}
		// one line and one conic
		else if ((selLines() >= 1) && (selConics() >= 1)) {
			GeoConicND[] conic = getSelectedConicsND();
			GeoLineND[] line = getSelectedLinesND();
			GeoElementND[] ret = { null };

			if (singlePointWanted) {
				ret[0] = getAlgoDispatcher().intersectLineConicSingle(null,
						(GeoLine) line[0], (GeoConic) conic[0], xRW, yRW);
				checkCoordCartesian((GeoPointND) ret[0]);
			} else {
				ret = getAlgoDispatcher()
						.intersectLineConic(null, line[0], conic[0]);
				for (GeoElementND geo : ret) {
					checkCoordCartesian((GeoPointND) geo);
				}
			}

			return ret;
		}
		// line and polyLine
		else if ((selLines() >= 1) && (selPolyLines() >= 1)) {
			GeoLine line = getSelectedLines()[0];
			GeoPolyLine polyLine = getSelectedPolyLines()[0];

			return getAlgoDispatcher().intersectLinePolyLine(
					new String[] { null }, line, polyLine);
		}
		// line and curve
		else if ((selLines() >= 1) && (selCurves() >= 1)) {
			GeoLine line = getSelectedLines()[0];
			GeoCurveCartesian curve = getSelectedCurves()[0];

			return getAlgoDispatcher()
					.intersectLineCurve(new String[] { null }, line, curve);
		}
		// curve-curve
		else if ((selCurves() >= 2)) {
			GeoCurveCartesian[] curves = getSelectedCurves();
			GeoElement[] ret;

			// multiple points disabled in ggb42, Reduce too slow
			if (singlePointWanted) {
				ret = getAlgoDispatcher().intersectCurveCurveSingle(
						new String[] { null }, curves[0], curves[1], xRW, yRW);
			} else {
				ret = getAlgoDispatcher().intersectCurveCurve(
						new String[] { null }, curves[0], curves[1]);
			}
			return ret;
		} // line and polygon
		else if ((selLines() >= 1) && (selPolygons() >= 1)) {
			GeoLine line = getSelectedLines()[0];
			GeoPolygon polygon = getSelectedPolygons()[0];

			return getAlgoDispatcher()
					.intersectLinePolygon(new String[] { null }, line, polygon);
		}

		// polyLine and polyLine
		else if (selPolyLines() >= 2) {
			GeoPolyLine[] polylines = getSelectedPolyLines();

			return getAlgoDispatcher().intersectPolyLines(
					new String[] { null }, polylines[0], polylines[1]);
		}

		// polygon and polygon - both as boundary
		else if (selPolygons() >= 2) {
			GeoPolygon[] polygons = getSelectedPolygons();

			return getAlgoDispatcher().intersectPolygons(
					new String[] { null }, polygons[0], polygons[1], false);
		}

		// line and function
		else if ((selLines() >= 1) && (selFunctions() >= 1)) {
			GeoLine[] line = getSelectedLines();
			GeoFunction[] fun = getSelectedFunctions();
			GeoElement[] ret = { null };

			if (singlePointWanted && fun[0].isPolynomialFunction(false)) {

					ret[0] = getAlgoDispatcher().intersectPolynomialLineSingle(
							null, fun[0], line[0], xRW, yRW);
			} else {
				GeoPoint initPoint = new GeoPoint(kernel.getConstruction());
				initPoint.setCoords(xRW, yRW, 1.0);
					ret = getAlgoDispatcher().intersectPolynomialLine(null,
						fun[0], line[0], initPoint);

			}
			return ret;
		}
		// polynomial and polyLine
		else if ((selPolyLines() >= 1) && (selFunctions() >= 1)) {
			GeoPolyLine[] polyLine = getSelectedPolyLines();
			GeoFunction[] fun = getSelectedFunctions();

			if (fun[0].isPolynomialFunction(false)) {
				return getAlgoDispatcher().intersectPolynomialPolyLine(null,
						fun[0], polyLine[0]);
			}

			GeoPoint initPoint = new GeoPoint(kernel.getConstruction());
			initPoint.setCoords(xRW, yRW, 1.0);
			return getAlgoDispatcher().intersectNPFunctionPolyLine(null, fun[0],
					polyLine[0], initPoint);

		}
		// polynomial and polygon
		else if ((selPolygons() >= 1) && (selFunctions() >= 1)) {
			GeoPolygon[] polygon = getSelectedPolygons();
			GeoFunction[] fun = getSelectedFunctions();

			if (fun[0].isPolynomialFunction(false)) {
				return getAlgoDispatcher().intersectPolynomialPolygon(null,
						fun[0], polygon[0]);
			}

			GeoPoint initPoint = new GeoPoint(kernel.getConstruction());
			initPoint.setCoords(xRW, yRW, 1.0);
			return getAlgoDispatcher().intersectNPFunctionPolygon(null, fun[0],
					polygon[0], initPoint);
		}
		// function and conic
		else if ((selFunctions() >= 1) && (selConics() >= 1)) {
			GeoConic[] conic = getSelectedConics();
			GeoFunction[] fun = getSelectedFunctions();

			if (singlePointWanted) {
				return new GeoElement[] {
						getAlgoDispatcher().intersectPolynomialConicSingle(null,
								fun[0], conic[0], xRW, yRW) };
			}
			return getAlgoDispatcher().intersectPolynomialConic(null, fun[0],
					conic[0]);
		} else if (selImplicitpoly() >= 1) {
			if (selFunctions() >= 1) {
				GeoImplicit p = getSelectedImplicitpoly()[0];
				GeoFunction fun = getSelectedFunctions()[0];

				if (singlePointWanted) {
					return new GeoElement[] { getAlgoDispatcher()
							.intersectImplicitpolyPolynomialSingle(null, p, fun,
									xRW, yRW) };
				}
				return getAlgoDispatcher().intersectImplicitpolyPolynomial(null,
						p, fun);
			} else if (selLines() >= 1) {
				GeoImplicit p = getSelectedImplicitpoly()[0];
				GeoLine l = getSelectedLines()[0];

				if (singlePointWanted) {
					return new GeoElement[] {
							getAlgoDispatcher().intersectImplicitpolyLineSingle(
									null, p, l, xRW, yRW) };
				}
				return getAlgoDispatcher().intersectImplicitpolyLine(null, p,
						l);
			} else if (selConics() >= 1) {
				GeoImplicit p = getSelectedImplicitpoly()[0];
				GeoConic c = getSelectedConics()[0];

				if (singlePointWanted) {
					return new GeoElement[] { getAlgoDispatcher()
							.intersectImplicitpolyConicSingle(null, p, c, xRW,
									yRW) };
				}
				return getAlgoDispatcher().intersectImplicitpolyConic(null, p,
						c);
			} else if (selImplicitpoly() >= 2) {
				GeoImplicit[] p = getSelectedImplicitpoly();

				if (singlePointWanted) {
					return new GeoElement[] {
							getAlgoDispatcher().intersectImplicitpolysSingle(
									null, p[0], p[1], xRW, yRW) };
				}
				return getAlgoDispatcher().intersectImplicitpolys(null, p[0],
						p[1]);
			}

			// intersect implicitPoly and polyLine
			else if (selPolyLines() >= 1) {
				GeoImplicit p = getSelectedImplicitpoly()[0];
				GeoPolyLine pl = getSelectedPolyLines()[0];

				return getAlgoDispatcher().intersectImplicitpolyPolyLine(null,
						p, pl);
			}

			// intersect implicitPoly and polygon
			else if (selPolygons() >= 1) {
				GeoImplicit p = getSelectedImplicitpoly()[0];
				GeoPolygon pl = getSelectedPolygons()[0];

				return getAlgoDispatcher().intersectImplicitpolyPolygon(null, p,
						pl);
			}
		}
		return null;
	}

	protected final GeoElementND[] parallel(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		boolean hitPoint = (addSelectedPoint(hits, 1, false, selPreview) != 0);
		if (!hitPoint) {
			if (selLines() == 0) {
				addSelectedVector(hits, 1, false, selPreview);
			}
			if (selVectors() == 0) {
				addSelectedLine(hits, 1, false, selPreview);
			}
			if (selFunctions() == 0) {
				addSelectedFunction(hits, 1, false, selPreview);
			}
		}

		if (selPoints() == 1) {
			GeoElementND[] ret = { null };
			if (selVectors() == 1) {
				// fetch selected point and vector
				GeoPointND[] points = getSelectedPointsND();
				GeoVectorND[] vectors = getSelectedVectorsND();
				// create new line

				if (points[0].isGeoElement3D()
						|| vectors[0].isGeoElement3D()) {
					ret[0] = getKernel().getManager3D()
							.line3D(null, points[0], vectors[0]);
				} else {
					ret[0] = getAlgoDispatcher().line(null,
							(GeoPoint) points[0], (GeoVector) vectors[0]);
				}
				return ret;
			} else if (selLines() == 1) {
				// fetch selected point and line
				GeoPointND[] points = getSelectedPointsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new line
				if (points[0].isGeoElement3D() || lines[0].isGeoElement3D()) {
					ret[0] = getKernel().getManager3D()
							.line3D(null, points[0], lines[0]);
				} else {
					ret[0] = getAlgoDispatcher().line(null,
							(GeoPoint) points[0], (GeoLine) lines[0]);
				}
				return ret;
			} else if (selFunctions() == 1) {
				// fetch selected point and (linear) function
				GeoPointND[] points = getSelectedPointsND();
				GeoFunction[] lines = getSelectedFunctions();
				ret[0] = getAlgoDispatcher().line(null, (GeoPoint) points[0],
						(Lineable2D) lines[0]);
				return ret;
			}
		}
		return null;
	}

	protected final GeoElement[] parabola(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		boolean hitPoint = (addSelectedPoint(hits, 1, false, selPreview) != 0);
		if (!hitPoint) {
			addSelectedLine(hits, 1, false, selPreview);
		}

		if (selPoints() == 1) {
			if (selLines() == 1) {
				// fetch selected point and line
				GeoPointND[] points = getSelectedPointsND();
				GeoLineND[] lines = getSelectedLinesND();

				// create new parabola
				return new GeoElement[] {companion.parabola(points[0], lines[0])};
			}
		}
		return null;
	}

	protected GeoElementND[] orthogonal(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		boolean hitPoint = addSelectedPoint(hits, 1, false, selPreview) != 0;

		return orthogonal(hits, hitPoint, selPreview);
	}

	final protected GeoElementND[] orthogonal(Hits hits, boolean hitPoint,
			boolean selPreview) {

		if (!hitPoint) {
			if (selLines() == 0) {
				addSelectedVector(hits, 1, false, TestGeo.GEOVECTOR, selPreview);
			}
			if (selVectors() == 0) {
				addSelectedLine(hits, 1, false, selPreview);
			}
			if (selFunctions() == 0) {
				addSelectedFunction(hits, 1, false, selPreview);
			}
		}

		if (selPoints() == 1) {
			if (selVectors() == 1) {
				// fetch selected point and vector
				GeoPointND[] points = getSelectedPointsND();
				GeoVectorND[] vectors = getSelectedVectorsND();
				// no defined line through a point and orthogonal to a vector in
				// 3D
				if (points[0].isGeoElement3D()) {
					return null;
				}

				// create new line
				return new GeoElement[] {getAlgoDispatcher().orthogonalLine(null,
						(GeoPoint) points[0], (GeoVector) vectors[0])};
			} else if (selLines() == 1) {
				// fetch selected point and line
				GeoPointND[] points = getSelectedPointsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new line
				return companion.orthogonal(points[0], lines[0]);
			} else if (selFunctions() == 1) {
				// fetch selected point and (linear) function
				GeoPointND[] points = getSelectedPointsND();
				GeoFunction[] lines = getSelectedFunctions();
				// create new line
				return companion.orthogonal(points[0], lines[0]);
			}
		}
		return null;
	}

	protected final GeoElement[] midpoint(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		boolean hitPoint = addSelectedPoint(hits, 2, false, selPreview) != 0;

		if (!hitPoint && (selPoints() == 0)) {
			addSelectedSegment(hits, 1, false, selPreview); // segment needed
			if (selSegments() == 0) {
				addSelectedConic(hits, 1, false, selPreview); // conic needed
			}
			if (selSegments() == 0 && selConics() == 0) {
				addSelectedPolygon(hits, 1, false, selPreview); // conic needed
			}
		}

		GeoElement[] ret = { null };

		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPointND[] points = getSelectedPointsND();
			ret[0] = companion.midpoint(points[0], points[1]);
			ret[0].setLabel(null);
			return ret;
		} else if (selSegments() == 1) {
			// fetch the selected segment
			GeoSegmentND[] segments = getSelectedSegmentsND();
			ret[0] = companion.midpoint(segments[0]);
			return ret;
		} else if (selConics() == 1) {
			// fetch the selected segment
			GeoConicND[] conics = getSelectedConicsND();
			ret[0] = companion.midpoint(conics[0]);
			return ret;
		} else if (selPolygons() == 1) {
			// fetch the selected polygon
			GeoPolygon[] polys = getSelectedPolygons();
			ret[0] = companion.centroid(polys[0]);
			return ret;
		}

		return null;
	}

	protected final boolean functionInspector(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return false;
		}
		if (selFunctions() == 0) {
			this.addSelectedFunction(hits, 1, false, selPreview);
		}
		if (selFunctions() == 1) {
			GeoFunction[] functions = getSelectedFunctions();
			// set mode first to prevent concurrency issue
			app.setMode(EuclidianConstants.MODE_MOVE, ModeSetter.DOCK_PANEL);
			getDialogManager().showFunctionInspector(functions[0]);

		}

		return false;
	}

	protected final GeoElement[] lineBisector(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}
		boolean hitPoint = false;

		if (selSegments() == 0) {
			hitPoint = addSelectedPoint(hits, 2, false, selPreview) != 0;
		}

		if (!hitPoint && selPoints() == 0) {
			addSelectedSegment(hits, 1, false, selPreview); // segment needed
		}

		GeoElement[] ret = { null };
		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPointND[] points = getSelectedPointsND();

			companion.lineBisector(points[0], points[1]);
			return ret;
		} else if (selSegments() == 1) {
			// fetch the selected segment
			GeoSegmentND[] segments = getSelectedSegmentsND();

			companion.lineBisector(segments[0]);
			return ret;
		}
		return null;
	}

	protected final GeoElement[] angularBisector(Hits hits,
			boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}
		boolean hitPoint = false;

		if (selLines() == 0) {
			hitPoint = (addSelectedPoint(hits, 3, false, selPreview) != 0);
		}
		if (!hitPoint && selPoints() == 0) {
			addSelectedLine(hits, 2, false, selPreview);
		}

		if (selPoints() == 3) {
			// fetch the three selected points
			GeoPointND[] points = getSelectedPointsND();

			return new GeoElement[] {companion.angularBisector(points[0], points[1], points[2])};
		} else if (selLines() == 2) {
			// fetch the two lines
			GeoLineND[] lines = getSelectedLinesND();

			return companion.angularBisector(lines[0], lines[1]);
		}

		return null;
	}

	protected final GeoElement[] threePoints(Hits hits, int threePointsMode,
			boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		// points needed
		addSelectedPoint(hits, 3, false, selPreview);
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
			if (points[0].isGeoElement3D() || points[1].isGeoElement3D()
					|| points[2].isGeoElement3D()) {
				ret[0] = kernel.getManager3D().circle3D(null, points[0],
						points[1], points[2]);
			} else {
				ret[0] = getAlgoDispatcher().circle(null, (GeoPoint) points[0],
						(GeoPoint) points[1], (GeoPoint) points[2]);
			}
			break;

		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
			ret[0] = companion.ellipseHyperbola(points[0], points[1], points[2],
					GeoConicNDConstants.CONIC_ELLIPSE);
			break;

		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
			ret[0] = companion.ellipseHyperbola(points[0], points[1], points[2],
					GeoConicNDConstants.CONIC_HYPERBOLA);
			break;

		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
			ret[0] = companion.circumcircleArc(points[0], points[1], points[2]);
			break;

		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			ret[0] = companion.circumcircleSector(points[0], points[1],
					points[2]);
			break;

		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
			ret[0] = companion.circleArcSector(points[0], points[1], points[2],
					GeoConicNDConstants.CONIC_PART_ARC);
			break;

		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
			ret[0] = companion.circleArcSector(points[0], points[1], points[2],
					GeoConicNDConstants.CONIC_PART_SECTOR);
			break;

		default:
			return null;
		}

		return ret;
	}

	protected final boolean relation(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return false;
		}

		addSelectedGeo(hits, 4, false, selPreview);
		int selGeos = selGeos();
		if (selGeos >= 2) {
			GeoElement[] geos = getSelectedGeos();
			app.showRelation(geos[0], geos[1], selGeos > 2 ? geos[2] : null,
					selGeos > 3 ? geos[3] : null);
			return true;
		}
		return false;
	}

	protected final GeoElement[] locus(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		// points needed
		addSelectedPoint(hits, 2, false, selPreview);
		addSelectedNumeric(hits, 1, false, selPreview);

		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPointND[] points = getSelectedPointsND();
			GeoElement locus;

			if (points[0].getPath() == null) {
				locus = companion.locus(points[0], points[1]);
			} else {
				locus = companion.locus(points[1], points[0]);
			}

			return new GeoElement[] {locus};
		} else if ((selPoints() == 1) && (selNumbers() == 1)) {
			GeoPointND[] points = getSelectedPointsND();
			GeoNumeric[] numbers = getSelectedNumbers();

			GeoElement locus = getAlgoDispatcher().locus(null, points[0],
					numbers[0]);
			return new GeoElement[]{ locus };
		}

		return null;
	}

	protected final GeoElement[] conic5(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		// points needed
		addSelectedPoint(hits, 5, false, selPreview);
		if (selPoints() == 5) {
			// fetch the three selected points
			GeoPointND[] points = getSelectedPointsND();

			return new GeoElement[] {companion.conic5(points)};
		}

		return null;
	}

	protected GeoElement[] slope(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		addSelectedLine(hits, 1, false, selPreview);

		if (selLines() == 1) {
			GeoLine line = getSelectedLines()[0];

			return getTextDispatcher().createSlopeText(line, null, mouseLoc);
		}
		addSelectedFunction(hits, 1, false, selPreview);
		if (selFunctions() == 1) {
			GeoFunction f = getSelectedFunctions()[0];

			return getTextDispatcher().createSlopeText(null, f, mouseLoc);
		}
		return null;
	}

	protected final GeoElement[] tangents(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		boolean found = addSelectedConic(hits, 2, false, selPreview) != 0;
		if (!found) {
			found = addSelectedFunction(hits, 1, false, selPreview) != 0;
		}
		if (!found) {
			found = addSelectedCurve(hits, 1, false, selPreview) != 0;
		}
		if (!found) {
			found = addSelectedImplicitpoly(hits, 1, false, selPreview) != 0;
		}
		if (!found) {
			found = addSelectedList(hits, 1, false, selPreview) != 0;
		}

		if (!found) {
			if (selLines() == 0) {
				addSelectedPoint(hits, 1, false, selPreview);
			}
			if (selPoints() == 0) {
				addSelectedLine(hits, 1, false, selPreview);
			}
		}
		if (selConics() == 1) {
			if (selPoints() == 1) {
				GeoConicND[] conics = getSelectedConicsND();
				GeoPointND[] points = getSelectedPointsND();
				// create new tangents

				return companion.tangent(points[0], conics[0]);
			} else if (selLines() == 1) {
				GeoConicND[] conics = getSelectedConicsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new line

				return companion.tangent(lines[0], conics[0]);
			}
		} else if (selConics() == 2) {
			GeoConicND[] conics = getSelectedConicsND();
			// create new tangents

			return companion.tangent(conics[0], conics[1]);

		} else if (selFunctions() == 1) {
			if (selPoints() == 1) {
				GeoFunction[] functions = getSelectedFunctions();
				GeoPointND[] points = getSelectedPointsND();
				// create new tangents
				GeoElement[] ret = { null };

				ret[0] = getAlgoDispatcher().tangent(null, points[0],
						functions[0]);
				return ret;
			}
		} else if (selCurves() == 1) {
			if (selPoints() == 1) {
				GeoCurveCartesian[] curves = getSelectedCurves();
				GeoPointND[] points = getSelectedPointsND();
				// create new tangents
				GeoElement[] ret = { null };

				ret[0] = kernel.tangent(null, points[0], curves[0]);
				return ret;
			}
		} else if (selImplicitpoly() == 1) {
			if (selPoints() == 1) {
				GeoImplicit implicitPoly = getSelectedImplicitpoly()[0];
				GeoPointND[] points = getSelectedPointsND();
				// create new tangents

				return getAlgoDispatcher().tangent(null, points[0],
						implicitPoly);
			}
		}

		return null;
	}

	/**
	 * Delete all hit objects.
	 * 
	 * @param hits
	 *            selected objects
	 */
	public void deleteAll(Hits hits) {
		for (GeoElement hit : hits) {
			hit.removeOrSetUndefinedIfHasFixedDescendent();
		}
	}

	protected final GeoElementND[] polarLine(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}
		boolean hitConic = (addSelectedConic(hits, 1, false, selPreview) != 0);

		if (!hitConic) {
			if (selVectors() == 0) {
				addSelectedVector(hits, 1, false, selPreview);
			}
			if (selLines() == 0) {
				addSelectedPoint(hits, 1, false, selPreview);
			}
			if (selPoints() == 0) {
				addSelectedLine(hits, 1, false, selPreview);
			}
		}

		if (selConics() == 1) {
			GeoElementND[] ret = { null };
			if (selPoints() == 1) {
				GeoConicND[] conics = getSelectedConicsND();
				GeoPointND[] points = getSelectedPointsND();
				// create new tangents

				ret[0] = companion.polarLine(points[0], conics[0]);
				return ret;

			} else if (selLines() == 1) {
				GeoConicND[] conics = getSelectedConicsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new line

				ret[0] = companion.diameterLine(lines[0], conics[0]);
				return ret;
			} else if (selVectors() == 1) {
				GeoConicND[] conics = getSelectedConicsND();
				GeoVectorND[] vecs = getSelectedVectorsND();
				// create new line

				ret[0] = companion.diameterLine(vecs[0], conics[0]);
				return ret;
			}
		}
		return null;
	}

	protected final boolean showHideLabel(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return false;
		}

		if (selPreview) {
			addSelectedGeo(hits, 1000, false, selPreview);
			return false;
		}

		GeoElement geo = chooseGeo(
				hits.getOtherHits(TestGeo.GEOAXIS, tempArrayList), true);
		if (geo != null) {
			geo.setLabelVisible(!geo.isLabelVisible());
			geo.updateRepaint();
			return true;
		}

		return false;
	}

	protected final boolean copyVisualStyle(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return false;
		}

		if (selPreview) {
			addSelectedGeo(hits, 1000, false, selPreview);
			return false;
		}

		GeoElement geo = chooseGeo(
				hits.getOtherHits(TestGeo.GEOAXIS, tempArrayList), true);
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
				if (!(TestGeo.getSpecificTest(app.getGeoForCopyStyle())
						.check(oldgeo))) {
					oldhits.remove(i);
				}
			}
			if (oldhits.size() > 0) {
				// there were appropriate selected elements
				// apply visual style for them
				// standard case: copy visual properties
				for (GeoElement oldgeo : oldhits) {
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
					storeUndoInfo();
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

	/**
	 * @return most recent mouse location
	 */
	public GPoint getMouseLoc() {
		return mouseLoc;
	}

	/**
	 * @param hasFocus
	 *            whether an input box has focus
	 */
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

	/**
	 * Handle mouse entered event.
	 */
	public void wrapMouseEntered() {
		if (isTextfieldHasFocus()) {
			return;
		}

		initToolTipManager();
		initShowMouseCoords();
		view.mouseEntered();
	}

	protected boolean getSelectables(Hits hits, boolean selPreview) {
		addSelectedGeo(hits.getSelectableHits(), 1, false, selPreview);
		return false;
	}

	protected final boolean moveRotate(Hits hits, boolean selPreview) {
		addSelectedGeo(hits.getPointRotateableHits(view, rotationCenter), 1,
				false, selPreview);
		return false;
	}

	protected final boolean point(Hits hits, boolean selPreview) {
		addSelectedGeo(hits.getHits(TestGeo.PATH, tempArrayList), 1, false,
				selPreview);
		return false;
	}

	protected final void geoElementSelected(Hits hits,
			boolean addToSelection, boolean selPreview) {
		if (hits.isEmpty()) {
			return;
		}

		addSelectedGeo(hits, 1, false, selPreview);
		if (selGeos() == 1) {
			GeoElement[] geos = getSelectedGeos();
			app.geoElementSelected(geos[0], addToSelection);
		}
	}

	protected final boolean segmentFixed(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return false;
		}

		// dilation center
		addSelectedPoint(hits, 1, false, selPreview);

		// we got the point
		if (selPoints() == 1) {
			// get length of segment
			getDialogManager().showNumberInputDialogSegmentFixed(
					localization.getMenu(EuclidianConstants.getModeText(mode)),
					getSelectedPointsND()[0]);

			return true;
		}
		return false;
	}

	protected final GeoElement[] angleFixed(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		// dilation center
		int count = addSelectedPoint(hits, 2, false, selPreview);

		if (count == 0) {
			addSelectedSegment(hits, 1, false, selPreview);
		}

		// we got the points
		if ((selPoints() == 2) || (selSegments() == 1)) {
			GeoElement[] selGeos = getSelectedGeos();

			getDialogManager().showNumberInputDialogAngleFixed(
					localization.getMenu(EuclidianConstants.getModeText(mode)),
					getSelectedSegmentsND(), getSelectedPointsND(), selGeos,
					this);
		}

		return null;
	}

	protected GeoElement[] switchModeForCircleOrSphere2(int sphereMode) {
		GeoPointND[] points = getSelectedPointsND();
		if (sphereMode == EuclidianConstants.MODE_SEMICIRCLE) {
			return new GeoElement[] {
					companion.semicircle(points[0], points[1]) };
		}

		return companion.createCircle2(points[0], points[1]);
	}

	protected final GeoElement[] circleOrSphere2(Hits hits, int sphereMode,
			boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		// points needed
		addSelectedPoint(hits, 2, false, selPreview);
		if (selPoints() == 2) {
			// fetch the three selected points
			return switchModeForCircleOrSphere2(sphereMode);
		}

		return null;
	}

	protected final boolean showHideObject(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return false;
		}

		if (selPreview) {
			addSelectedGeo(hits, 1000, false, selPreview);
			return false;
		}

		GeoElement geo = chooseGeo(hits, true);
		if (geo != null) {
			// hide axis
			if (geo instanceof GeoAxis) {
				switch (((GeoAxis) geo).getType()) {
				default:
				case GeoAxisND.X_AXIS:
					view.setShowAxis(EuclidianViewInterfaceCommon.AXIS_X, false,
							true);
					break;

				case GeoAxisND.Y_AXIS:
					view.setShowAxis(EuclidianViewInterfaceCommon.AXIS_Y, false,
							true);
					break;
				case GeoAxisND.Z_AXIS:
					view.setShowAxis(EuclidianViewInterfaceCommon.AXIS_Z, false,
							true);
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

	protected final boolean text(Hits hits, boolean selPreview) {
		if (getTextController() != null && getTextController().getHit() != null
				&& !app.has(Feature.MOW_TEXT_TOOL)) {
			return false;
		}

		GeoPointND loc = null; // location
		boolean rw = true;

		// threshold for custom or default size
		boolean customSize = app.has(Feature.MOW_TEXT_TOOL)
				&& textRectangleShape != null;

		if (hits.isEmpty()) {
			if (selPreview) {
				return false;
			}

			// create new Point
			if (customSize) {
				loc = customTextPosition();
			} else {
				loc = new GeoPoint(kernel.getConstruction());
				rw = companion.setCoordsToMouseLoc(loc);
			}
		} else {
			// points needed
			addSelectedPoint(hits, 1, false, selPreview);
			if (selPoints() >= 1) {
				// fetch the selected point
				GeoPointND[] points = getSelectedPointsND();
				loc = points[0];
			} else if (!selPreview) {
				if (customSize) {
					loc = customTextPosition();
				} else {
					loc = new GeoPoint(kernel.getConstruction());
					rw = companion.setCoordsToMouseLoc(loc);
				}
			}
		}

		// got location
		if (app.has(Feature.MOW_TEXT_TOOL)) {
			if (wasBoundingBoxHit) {
				return false;
			}

			GeoText t = getTextController().createText(loc);
			if (t != null) {
				memorizeJustCreatedGeos(t.asArray());
			}
			if (textRectangleShape != null) {
				if (customSize) {
					getTextController().resizeEditor(
							Math.max(DrawText.MIN_EDITOR_WIDTH,
									(int) textRectangleShape.getWidth()),
							Math.max(DrawText.MIN_EDITOR_HEIGHT,
									(int) textRectangleShape.getHeight()));
				}
				textRectangleShape = null;
				view.setShapeRectangle(null);
				view.repaintView();
			}
			return true;
		}
		if (loc != null && getDialogManager() != null) {
			getDialogManager().showTextCreationDialog(loc, rw);
			return true;
		}

		return false;
	}

	private GeoPointND customTextPosition() {
		return new GeoPoint(kernel.getConstruction(), textRectangleShape.getX(),
				textRectangleShape.getY() + app.getFontSize()
						+ EuclidianStatic.EDITOR_MARGIN,
				1);
	}

	/**
	 * @return whether alt is pressed
	 */
	public boolean isAltDown() {
		return altDown;
	}

	/**
	 * @param altDown
	 *            whether alt is pressed
	 */
	public void setAltDown(boolean altDown) {
		this.altDown = altDown;
	}

	/**
	 * @return the controller for in-place editing texts.
	 */
	public TextController getTextController() {
		return null;
	}

	protected final boolean slider(boolean selPreview) {
		if (!selPreview && (mouseLoc != null) && getDialogManager() != null) {
			getDialogManager().showSliderCreationDialog(mouseLoc.x, mouseLoc.y);
		}
		return false;
	}

	protected final boolean image(Hits hits, boolean selPreview) {
		GeoPoint loc = null; // location

		if (hits.isEmpty()) {
			if (selPreview) {
				return false;
			}
			// create new Point
		} else {
			// points needed
			addSelectedPoint(hits, 1, false, selPreview);
			if (selPoints() >= 1) {
				// fetch the selected point
				GeoPoint[] points = getSelectedPoints();
				loc = points[0];
			} else if (!selPreview) {
				loc = new GeoPoint(kernel.getConstruction());
				loc.setCoords(xRW, yRW, 1.0);
			}
		}

		if (app.getGuiManager() != null) { // FIXME: fix this better
			app.getGuiManager().loadImage(loc, null, altDown, view);
		}
		return true;
	}

	protected final GeoElement[] mirrorAtPoint(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		// try to get one Transformable
		int count = 0;
		if (selGeos() == 0) {
			Hits mirAbles = hits.getHits(TestGeo.TRANSFORMABLE, tempArrayList);
			count = addSelectedGeo(mirAbles, 1, false, selPreview);
		}

		// polygon
		if (count == 0) {
			count = addSelectedPolygon(hits, 1, false, selPreview);
		}

		// point = mirror
		if (count == 0) {
			addSelectedPoint(hits, 1, false, selPreview);
		}

		// we got the mirror point
		if (selPoints() == 1) {
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoPointND[] points = getSelectedPointsND();

				return companion.mirrorAtPoint(polys[0], points[0]);
			} else if (selGeos() > 0) {
				// mirror all selected geos
				GeoElement[] geos = getSelectedGeos();
				GeoPointND point = getSelectedPointsND()[0];
				ArrayList<GeoElement> ret = new ArrayList<>();

				for (GeoElement geo : geos) {
					if (geo != point) {
						if (geo instanceof Transformable) {
							ret.addAll(Arrays.asList(
									companion.mirrorAtPoint(geo, point)));
						} else if (geo.isGeoPolygon()) {
							ret.addAll(Arrays.asList(
									companion.mirrorAtPoint(geo, point)));
						}
					}
				}
				GeoElement[] retex = {};
				return ret.toArray(retex);
			}
		}
		return null;
	}

	protected final GeoElement[] mirrorAtLine(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		int max = selLines() == 0 ? 1 : 2;

		// Transformable
		Hits mirAbles = hits.getHits(TestGeo.TRANSFORMABLE, tempArrayList);
		int count = addSelectedGeo(mirAbles, max, false, selPreview);

		// first selected GeoElement is GeoLine
		if (count == 1 && selGeos() >= 1) {
			GeoElement geo = getSelectedGeoList()
					.get(getSelectedGeoList().size() - 1);
			if (geo instanceof GeoLineND) {
				getSelectedLineList().clear();
				getSelectedLineList().add((GeoLineND) geo);
			}
		}

		// polygon
		if (count <= 0) {
			count = addSelectedPolygon(hits, max, false, selPreview);
		}

		// line = mirror
		if (count <= 0) {
			addSelectedLine(hits, max, false, selPreview);
		}

		// we got the mirror point
		if (selLines() >= 1) {
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoLine[] lines = getSelectedLines();

				return getAlgoDispatcher().mirror(null, polys[0], lines[0]);
			} else if (selGeos() > 1) { // line is also selected
				// mirror all selected geos
				GeoElement[] geos = getSelectedGeos();
				GeoLineND line = getSelectedLinesND()[0];
				ArrayList<GeoElement> ret = new ArrayList<>();

				for (GeoElement geo : geos) {
					if (geo != line) {
						if (geo instanceof Transformable) {
							ret.addAll(Arrays.asList(
									companion.mirrorAtLine(geo, line)));
						} else if (geo.isGeoPolygon()) {
							ret.addAll(Arrays.asList(
									companion.mirrorAtLine(geo, line)));
						}
					}
				}

				return ret.toArray(new GeoElement[0]);
			}
		}

		return null;
	}

	protected final GeoElement[] mirrorAtCircle(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		// Transformable
		int count = 0;
		if (selGeos() == 0) {
			Hits mirAbles = hits.getHits(TestGeo.TRANSFORMABLE, tempArrayList);
			mirAbles.removeImages();
			count = addSelectedGeo(mirAbles, 1, false, selPreview);
		}

		// polygon
		if (count == 0) {
			count = addSelectedPolygon(hits, 1, false, selPreview);
		}

		// line = mirror
		if (count == 0) {
			addSelectedConic(hits, 1, false, selPreview);
		}

		// we got the mirror point
		if (selConics() == 1) {
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoConic circle = getSelectedCircles()[0];

				return getAlgoDispatcher().mirror(null, polys[0], circle);
			} else if (selGeos() > 0) {
				// mirror all selected geos
				GeoElement[] geos = getSelectedGeos();
				GeoConic circle = getSelectedCircles()[0];
				ArrayList<GeoElement> ret = new ArrayList<>();

				for (GeoElement geo : geos) {
					if (geo != circle && circle != null) {
						if (geo instanceof Transformable) {
							ret.addAll(Arrays.asList(getAlgoDispatcher()
									.mirror(null, geo, circle)));
						} else if (geo.isGeoPolygon()) {
							ret.addAll(Arrays.asList(getAlgoDispatcher()
									.mirror(null, geo, circle)));
						}
					}
				}

				return ret.toArray(new GeoElement[0]);
			}
		}

		return null;
	}

	private boolean clearHighlightedGeos() {
		boolean repaintNeeded = false;

		// clear old highlighting
		if (highlightedGeos.size() > 0) {
			setHighlightedGeos();
			repaintNeeded = true;
		}
		// find new objects to highlight
		highlightedGeos.clear();

		return repaintNeeded;
	}

	/**
	 * @param hits
	 *            hits
	 * @param isControlDown
	 *            whether controll is pressed
	 * @return whether highlighting changed
	 */
	public boolean refreshHighlighting(Hits hits, boolean isControlDown) {
		Hits oldHighlightedGeos = highlightedGeos.cloneHits();

		// clear old highlighting
		boolean repaintNeeded = clearHighlightedGeos();

		// TODO - this can trigger a tool on mouse-move
		// https://help.geogebra.org/topic/-trac-2518-bug-while-using-measuring-tool-
		// removing breaks previews in trunk
		boolean oldTranslateRectangle = this.allowSelectionRectangleForTranslateByVector;
		processModeForHighlight(hits, isControlDown);
		// build highlightedGeos List
		this.allowSelectionRectangleForTranslateByVector = oldTranslateRectangle;
		if (highlightJustCreatedGeos) {
			highlightedGeos.addAll(justCreatedGeos); // we also highlight just
			// created geos
		}

		// set highlighted objects
		if (highlightedGeos.size() > 0) {
			repaintNeeded = true;
		}

		// if highlightedGeos are the same as the old highlightedGeos, do not
		// repaint
		// old refreshHighlighting repainted every time when one of them was not
		// empty
		if (!repaintNeeded) {
			return false;
		} else if (oldHighlightedGeos.size() == highlightedGeos.size()
				&& oldHighlightedGeos.containsAll(highlightedGeos)) {
			return false;
		}

		return true;
	}

	/**
	 * Highlight single geo, blur all others.
	 * 
	 * @param geo
	 *            geo to higlight
	 * @return whether highlighting changed
	 */
	public boolean highlight(GeoElement geo) {
		boolean repaintNeeded = clearHighlightedGeos();

		if (geo != null) {
			highlightedGeos.add(geo);
			repaintNeeded = true;
		}

		return repaintNeeded;
	}

	/**
	 * @param geos
	 *            geos to gighlight
	 * @return whether higlighting changed
	 */
	public boolean highlight(ArrayList<GeoElement> geos) {
		boolean repaintNeeded = clearHighlightedGeos();

		if (geos != null && geos.size() > 0) {
			highlightedGeos.addAll(geos);
			repaintNeeded = true;
		}

		return repaintNeeded;
	}

	/**
	 * Clear selections and repaint.
	 */
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
		selection.clearLists();
		clearSelectionsKeepLists(repaint, updateSelection);
	}

	private void clearSelectionsKeepLists(boolean repaint,
			boolean updateSelection) {
		view.setBoundingBox(null);
		view.repaint();
		selection.clearSelectedGeos(repaint, updateSelection);

		// if we clear selection and highlighting,
		// we may want to clear justCreatedGeos also
		clearJustCreatedGeos();

		// clear highlighting
		refreshHighlighting(null, false); // this may call repaint
	}

	/**
	 * Clear selection and repaint.
	 */
	public final void clearSelected() {
		selection.clearLists();
		view.repaintView();
	}

	/**
	 * Clear selection and reset selection rectangle.
	 */
	public void clearSelectionAndRectangle() {
		clearSelections();
		view.setSelectionRectangle(null);
	}

	final protected boolean attachDetach(Hits hits, boolean selPreview) {
		if (detachFrom != null || needsAttach) {
			hits.remove(movedGeoPoint);

			// replace point with point it was dragged to
			if (hits.containsGeoPoint()
					&& movedGeoPoint.hasChildren()) {
				try {
					this.kernel.getConstruction().replace(
							(GeoElement) movedGeoPoint,
							hits.getFirstHit(TestGeo.GEOPOINTND));
				} catch (Exception e) {
					e.printStackTrace();
				} catch (MyError e) {
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

				// detach
				if (detachFrom != null && !hits.contains(detachFrom)) {
					String name = movedGeoPoint
							.getLabel(StringTemplate.defaultTemplate);
					this.app.getKernel().getAlgoDispatcher().detach(
							movedGeoPoint, view.toRealWorldCoordX(mouseLoc.x),
							view.toRealWorldCoordY(mouseLoc.y), detachFromPath,
							detachFromRegion);
					movedGeoPoint = (GeoPointND) this.kernel.getConstruction()
							.geoTableVarLookup(name);
				}

				// attach
				if (needsAttach) {
					if (!"".equals(attachTo)) {
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

		addSelectedRegion(hits, 1, false, selPreview);

		addSelectedPath(hits, 1, false, selPreview);

		addSelectedPoint(hits, 1, false, selPreview);

		if (getSelectedPointList().size() == 1) {
			GeoPointND p = getSelectedPointList().get(0);

			if (p.isPointOnPath() || p.isPointInRegion()) {
				getSelectedPointsND();
				getSelectedRegions();
				getSelectedPaths();

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
				// region (ie inside) not path (edge)
				Path[] paths = getSelectedPaths();
				GeoPointND[] points = getSelectedPointsND();

				if (paths[0].isChildOf(points[0])) {
					return false;
				}

				if (paths[0].isGeoPolygon()
						|| (paths[0].isGeoConic() && (((GeoConicND) paths[0])
										.getLastHitType() == HitType.ON_FILLING))) {
					GeoPointND ret = getAlgoDispatcher().attach(points[0],
							(Region) paths[0], view, getMouseLocRW());

					if (ret != null) {
						clearSelections();
						view.updateCursor(ret);
						return true;
					}

					return false;
				}

				GeoPointND ret = getAlgoDispatcher().attach(points[0], paths[0],
						view, getMouseLocRW());

				if (ret != null) {
					clearSelections();
					view.updateCursor(ret);
					return true;
				}

				return false;
			} else if (selRegions() == 1) {
				Region[] regions = getSelectedRegions();
				GeoPointND[] points = getSelectedPointsND();

				if (!regions[0].isChildOf(points[0])) {
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

	protected final GeoElement[] translateByVector(Hits hits,
			boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		// Transformable
		int count = 0;
		if (selGeos() == 0) {
			Hits transAbles = hits.getHits(TestGeo.TRANSLATEABLE, tempArrayList);
			count = addSelectedGeo(transAbles, 1, false, selPreview);
		}

		// polygon
		if (count == 0) {
			count = addSelectedPolygon(hits, 1, false, selPreview);
		}

		// list
		if (count == 0) {
			count = addSelectedList(hits, 1, false, selPreview);
		}

		// translation vector
		if (count == 0) {
			count = addSelectedVector(hits, 1, false, selPreview);
		}

		// create translation vector
		if (count == 0) {
			addSelectedPoint(hits, 2, false, selPreview);
			getSelectedGeoList().removeAll(getSelectedPointList());
			allowSelectionRectangleForTranslateByVector = false;
		}

		// we got the mirror point
		if (selVectors() == 1 || selPoints() == 2) {
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoVectorND vec;
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
				GeoVectorND vec;
				if (selVectors() == 1) {
					vec = getSelectedVectorsND()[0];
				} else {
					GeoPointND[] ab = getSelectedPointsND();
					vec = (GeoVectorND) vector(ab[0], ab[1]);
				}
				ArrayList<GeoElement> ret = new ArrayList<>();
				for (GeoElement geo : geos) {
					if (geo != vec) {
						if ((geo instanceof Translateable)
								|| geo.isGeoPolygon() || geo.isGeoList()) {
							ret.addAll(Arrays
									.asList(companion.translate(geo, vec)));
						}
					}
				}

				allowSelectionRectangleForTranslateByVector = true;
				return ret.toArray(new GeoElement[0]);
			}
		}

		return null;
	}

	protected final GeoElement[] rotateByAngle(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		// Transformable
		int count = 0;
		if (selGeos() == 0 || (selGeos() == 1
				&& getSelectedGeoList().get(0) instanceof GeoPointND)
				&& !hits.containsGeoPoint()) {
			// if the first geo to be selected is a point and the second is not,
			// the point will be used as rotation center
			Hits rotAbles = hits.getHits(TestGeo.TRANSFORMABLE, tempArrayList);
			count = addSelectedGeo(rotAbles, 2, false, selPreview);
		}

		// polygon
		if (count == 0) {
			count = addSelectedPolygon(hits, 1, false, selPreview);
		}

		// rotation center
		if (count == 0) {
			addSelectedPoint(hits, 1, false, selPreview);
		}

		if (selGeos() > 1 && selPoints() == 0
				&& getSelectedGeoList().get(0) instanceof GeoPointND) {
			// If a point is selected as first geo, it is not added to
			// selectedPoints, because the last point that is selected is used
			// as rotation center.
			// Therefore a point that was selected first has to be added to
			// selecetedPoints, if another geo is selected in the second step
			getSelectedPointList()
					.add((GeoPointND) getSelectedGeoList().get(0));
		}

		// we got the rotation center point
		if (selPoints() == 1 && selGeos() > 1) {
			GeoElement[] selGeos = getSelectedGeos();

			getDialogManager().showNumberInputDialogRotate(
					localization.getMenu(EuclidianConstants.getModeText(mode)),
					getSelectedPolygons(), getSelectedPointsND(), selGeos,
					this);
		}

		return null;
	}

	protected final GeoElement[] dilateFromPoint(Hits hits,
			boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		// dilateable
		int count = 0;
		if (selGeos() == 0) {
			Hits dilAbles = hits.getHits(TestGeo.DILATEABLE, tempArrayList);
			count = addSelectedGeo(dilAbles, 1, false, selPreview);
		}

		// polygon
		if (count == 0) {
			count = addSelectedPolygon(hits, 1, false, selPreview);
		}

		// dilation center
		if (count == 0) {
			addSelectedPoint(hits, 1, false, selPreview);
		}

		// we got the mirror point
		if (selPoints() == 1) {
			GeoElement[] selGeos = getSelectedGeos();

			getDialogManager().showNumberInputDialogDilate(
					localization.getMenu(EuclidianConstants.getModeText(mode)),
					getSelectedPolygons(), getSelectedPointsND(), selGeos,
					this);
		}

		return null;
	}

	protected final GeoElement[] fitLine(Hits hits, boolean selPreview) {
		GeoList list;

		addSelectedList(hits, 1, false, selPreview);

		GeoElement[] ret = { null };

		if (selLists() > 0) {
			list = getSelectedLists()[0];
			if (list != null) {
				ret[0] = fitLineY(null, list);
				return ret;
			}
		} else {
			addSelectedPoint(hits, 999, true, selPreview);

			if (selPoints() > 1) {
				GeoPoint[] points = getSelectedPoints();
				list = new CmdFitLineY(kernel).wrapInList(kernel, points,
						points.length, GeoClass.POINT);
				if (list != null) {
					ret[0] = fitLineY(null, list);
					return ret;
				}
			}
		}

		return null;
	}

	/**
	 * FitLineY[list of coords]
	 * 
	 * @param label
	 *            output label
	 * @param list
	 *            list of points
	 * @return line
	 */
	final public GeoLine fitLineY(String label, GeoList list) {
		AlgoFitLineY algo = new AlgoFitLineY(kernel.getConstruction(),
				list);
		algo.getFitLineY().setLabel(label);
		return algo.getFitLineY();
	}

	protected final GeoElement[] createList(Hits hits, boolean selPreview) {
		GeoList list;
		GeoElement[] ret = { null };

		if (!selPreview && (hits.size() > 1)) {
			list = getAlgoDispatcher().list(null, hits, false);
			if (list != null) {
				messageListCreated(list);
				ret[0] = list;
				return ret;
			}
		}

		return null;
	}

	private void messageListCreated(GeoList list) {
		if (view.getAllowToolTips() != EuclidianStyleConstants.TOOLTIPS_OFF) {
			showListCreated(list);
		}
	}

	protected void showListCreated(GeoList list) {
		String label = list.getLabel(StringTemplate.algebraTemplate);
		String message = localization.getPlain("ListCreated", label);
		showListToolTip(message);
	}

	public void showListToolTip(String message) {
		// Overridden in the subclasses
	}

	protected void calcRWcoords() {
		xRW = (mouseLoc.x - view.getXZero()) * view.getInvXscale();
		yRW = (view.getYZero() - mouseLoc.y) * view.getInvYscale();
	}

	/**
	 * Update cursor RW coords from point
	 * 
	 * @param tmpCoords3
	 *            inhom coordinates of a point
	 */
	public void setRwCoords(Coords tmpCoords3) {
		xRW = tmpCoords3.getX();
		yRW = tmpCoords3.getY();
	}

	final protected void setMouseLocation(AbstractEvent event) {
		getCompanion().setMouseLocation(event);
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
	final public double getPointCapturingPercentage() {
		return getCompanion().getPointCapturingPercentage();
	}

	/**
	 * COORD TRANSFORM SCREEN -> REAL WORLD
	 * <p/>
	 * real world coords -> screen coords ( xscale 0 xZero ) T = ( 0 -yscale
	 * yZero ) ( 0 0 1 )
	 * <p/>
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
				|| (mode == EuclidianConstants.MODE_POLYGON)
				|| (mode == EuclidianConstants.MODE_POLYLINE))
				&& useLineEndPoint && (lineEndPoint != null)) {
			xRW = lineEndPoint.x;
			yRW = lineEndPoint.y;
			return;
		}

		if ((mode == EuclidianConstants.MODE_MOVE)
				&& ((moveMode == MOVE_NUMERIC)
						|| (moveMode == MOVE_VECTOR_NO_GRID)
						|| (moveMode == MOVE_POINT_WITH_OFFSET))) {
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
				for (GeoPointND gp : spl) {
					if ((Math.abs(gp.getInhomX() - xRW)
							< (view.getGridDistances(0) * pointCapturingPercentage))
							&& (Math.abs(gp.getInhomY() - yRW)
							< (view.getGridDistances(1) * pointCapturingPercentage))) {
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
				int oddOrEvenRow = (int) Math.round((2.0
						* Math.abs(yRW - Kernel.roundToScale(yRW, isoGrid)))
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

			default:
			case EuclidianView.GRID_CARTESIAN:

				// X = (x, y) ... next grid point

				double x = Kernel.roundToScale(xRW, view.getGridDistances(0));
				double y = Kernel.roundToScale(yRW, view.getGridDistances(1));

				// if |X - XRW| < gridInterval * pointCapturingPercentage then
				// take the grid point
				double a = Math.abs(x - xRW);
				double b = Math.abs(y - yRW);

				if ((a < (view.getGridDistances(0) * pointCapturingPercentage))
						&& (b < (view.getGridDistances(1)
								* pointCapturingPercentage))) {
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
				if (angleOffset > (view.getGridDistances(2) / 2)) {
					angleOffset -= view.getGridDistances(2);
				} else if (angleOffset < -(view.getGridDistances(2) / 2)) {
					angleOffset += view.getGridDistances(2);
				}
				angle = angle - angleOffset;

				// get grid point
				double x1 = r2 * Math.cos(angle);
				double y1 = r2 * Math.sin(angle);

				// if |X - XRW| < gridInterval * pointCapturingPercentage then
				// take the grid point
				double a1 = Math.abs(r - r2);
				double b1 = Math.abs(r * angleOffset);

				if (pointCapturingPercentage > 0.5
						|| ((a1 < (view.getGridDistances(0)
								* pointCapturingPercentage))
						&& (b1 < (view.getGridDistances(0)
										* pointCapturingPercentage)))) {
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

	/**
	 * @return algo dispatcher
	 */
	public AlgoDispatcher getAlgoDispatcher() {
		return kernel.getAlgoDispatcher();
	}

	protected final GeoElement[] area(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		int count = addSelectedPolygon(hits, 1, false, selPreview);
		if (count == 0) {
			addSelectedConic(hits, 2, false, selPreview);
		}

		// area of CONIC
		if (selConics() == 1) {
			GeoConicND conic = getSelectedConicsND()[0];

			// check if arc
			if (conic.isGeoConicPart()) {
				GeoConicPartND conicPart = (GeoConicPartND) conic;
				if (conicPart
						.getConicPartType() == GeoConicNDConstants.CONIC_PART_ARC) {
					clearSelections();
					return null;
				}
			}

			// standard case: conic
			GeoNumeric area = getAlgoDispatcher().area(null, conic);

			return getTextDispatcher().getAreaText(conic, area, mouseLoc);
		}

		// area of polygon
		else if (selPolygons() == 1) {
			GeoPolygon[] poly = getSelectedPolygons();

			return getTextDispatcher().getAreaText(poly[0], poly[0], mouseLoc);
		}

		return null;
	}

	protected boolean regularPolygon(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return false;
		}

		// need two points
		addSelectedPoint(hits, 2, false, selPreview);

		if (selPoints() == 2) {
			GeoPointND[] points = getSelectedPointsND();
			getDialogManager().showNumberInputDialogRegularPolygon(
					localization.getMenu(EuclidianConstants.getModeText(mode)), this,
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
	 * @param selPreview
	 *            whether this is in preview mode
	 */
	protected void addSelectedPlanesForAngle(Hits hits, boolean selPreview) {
		// Overridden in 3D
	}

	/**
	 *
	 * @return angle from plane/plane or plane/line
	 */
	protected GeoAngle createAngle3D() {
		return null;
	}

	protected final GeoElement[] angle(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		int count = 0;
		if (selPoints() == 0) {
			if (selVectors() == 0) {
				count = addSelectedLine(hits, 2, false, selPreview);
			}
			if (selLines() == 0) {
				count = addSelectedVector(hits, 2, false, selPreview);
			}
		}
		if (count == 0) {
			count = addSelectedPoint(hits, 3, false, selPreview);
		}

		// try polygon too
		boolean polyFound = false;
		if (count == 0) {
			polyFound = 1 == addSelectedGeo(
					hits.getHits(TestGeo.GEOPOLYGON, tempArrayList), 1, false,
					selPreview);
		}

		// try planes (for 3D)
		if (!polyFound) {
			addSelectedPlanesForAngle(hits, selPreview);
		}

		GeoAngle angle = null;
		GeoElement[] angles = null;
		if (selPoints() == 3) {
			GeoPointND[] points = getSelectedPointsND();
			angle = companion.createAngle(points[0], points[1], points[2]);
		} else if (selVectors() == 2) {
			GeoVectorND[] vecs = getSelectedVectorsND();
			angle = companion.createAngle(vecs[0], vecs[1]);
		} else if (selLines() == 2) {
			GeoLineND[] lines = getSelectedLinesND();

			angle = companion.createLineAngle(lines[0], lines[1]);
		} else if (polyFound && (selGeos() == 1)) {
			angles = companion.createAngles((GeoPolygon) getSelectedGeos()[0]);
		} else { // 3D
			angle = createAngle3D();
		}

		if (angle != null) {
			return new GeoElement[]{ angle };
		}

		return angles;
	}

	protected TextDispatcher getTextDispatcher() {
		if (textDispatcher == null) {
			textDispatcher = new TextDispatcher(kernel, view);
		}
		return textDispatcher;
	}

	protected final GeoElementND[] distance(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		int count = addSelectedPoint(hits, 2, false, selPreview);
		if (count == 0) {
			addSelectedLine(hits, 2, false, selPreview);
			addSelectedConic(hits, 2, false, selPreview);
			addSelectedPolygon(hits, 2, false, selPreview);
			addSelectedPolyLine(hits, 2, false, selPreview);
			addSelectedSegment(hits, 2, false, selPreview);
		}

		// quit here, see #3885
		if (selPreview) {
			return null;
		}
		// TWO POINTS
		if (selPoints() == 2) {
			// length
			GeoPointND[] points = getSelectedPointsND();

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
			ret[0] = getTextDispatcher().createDistanceText(points[0],
					lines[0]);

			clearSelections(); // make sure segment will be unselected

			return ret;
		}

		// SEGMENT
		// make this after point-line
		else if (selSegments() == 1) {
			// length
			GeoSegmentND[] segments = getSelectedSegmentsND();

			// length
			GeoElementND seg = segments[0];
			if (seg.isLabelVisible()) {
				seg.setLabelMode(GeoElementND.LABEL_NAME_VALUE);
			} else {
				seg.setLabelMode(GeoElementND.LABEL_VALUE);
			}
			segments[0].setLabelVisible(true);
			segments[0].updateRepaint();
			// return this not null because the kernel has changed
			return new GeoElementND[]{ seg };
		}

		// TWO LINES
		else if (selLines() == 2) {
			GeoLineND[] lines = getSelectedLinesND();
			GeoElement[] ret = { null };

			ret[0] = getAlgoDispatcher().distance(null, lines[0], lines[1]);
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
		// perimeter of POLYLINE
		else if (selPolylines() == 1) {
			GeoPolyLine[] poly = getSelectedPolyLines();

			return getTextDispatcher().createPerimeterText(poly[0], mouseLoc);
		}

		return null;
	}

	private int selPolylines() {
		return getSelectedPolyLineList().size();
	}

	protected final boolean showCheckBox(boolean selPreview) {
		if (selPreview) {
			return false;
		}

		getDialogManager().showBooleanCheckboxCreationDialog(mouseLoc, null);
		return false;
	}

	protected final GeoElement[] compasses(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		// we already have two points that define the radius
		if (selPoints() == 2) {
			GeoPointND[] points = new GeoPointND[2];
			points[0] = getSelectedPointList().get(0);
			points[1] = getSelectedPointList().get(1);

			// check for centerPoint
			GeoPointND centerPoint = (GeoPointND) chooseGeo(hits,
					TestGeo.GEOPOINTND);

			if (centerPoint != null) {
				if (selPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add((GeoElement) centerPoint);
					addToHighlightedList(getSelectedPointList(), tempArrayList,
							3);
					return null;
				}

				// three points: center, distance between two points
				GeoElement circle = circleCompasses(centerPoint, points[0],
						points[1]);
				GeoElement[] ret = { circle };
				clearSelections();
				return ret;
			}
		}

		// we already have a circle that defines the radius
		else if (selConics() == 1) {
			GeoConicND circle = getSelectedConicNDList().get(0);

			// check for centerPoint
			GeoPointND centerPoint = (GeoPointND) chooseGeo(hits,
					TestGeo.GEOPOINTND);

			if (centerPoint != null) {
				if (selPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add((GeoElement) centerPoint);
					addToHighlightedList(getSelectedPointList(), tempArrayList,
							3);
					return null;
				}

				// center point and circle which defines radius
				GeoElement circlel = circle(centerPoint, circle);
				GeoElement[] ret = { circlel };
				clearSelections();
				return ret;
			}
		}
		// we already have a segment that defines the radius
		else if (selSegments() == 1) {
			GeoSegmentND segment = getSelectedSegmentList().get(0);

			// check for centerPoint
			GeoPointND centerPoint = (GeoPointND) chooseGeo(hits,
					TestGeo.GEOPOINTND);

			if (centerPoint != null) {
				if (selPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add((GeoElement) centerPoint);
					addToHighlightedList(getSelectedPointList(), tempArrayList,
							3);
					return null;
				}

				// center point and segment
				GeoElement circlel = companion.circle(kernel.getConstruction(),
						centerPoint, segment);
				GeoElement[] ret = { circlel };
				clearSelections();
				return ret;
			}
		}

		// don't have radius yet: need two points or segment
		boolean hitPoint = (addSelectedPoint(hits, 2, false, selPreview) != 0);
		if (!hitPoint && (selPoints() != 2)) {
			addSelectedSegment(hits, 1, false, selPreview);
			addSelectedConic(hits, 1, false, selPreview);

			// don't allow conics other than circles to be selected
			if (getSelectedConicNDList().size() > 0) {
				GeoConicND c = getSelectedConicNDList().get(0);
				if (!c.isCircle()) {
					getSelectedConicNDList().remove(0);
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
	private GeoConicND circle(
			// this is actually a macro
			GeoPointND A, GeoQuadricND c) {
		Construction cons = kernel.getConstruction();

		AlgoRadius radius = new AlgoRadius(cons, c);
		cons.removeFromConstructionList(radius);

		GeoConicND circle = companion.circle(cons, A, radius.getRadius());
		circle.setToSpecific();
		circle.update();
		return circle;
	}

	/**
	 * circle with midpoint M and radius BC Michael Borcherds 2008-03-14
	 */
	private GeoConicND circleCompasses(GeoPointND A, GeoPointND B,
			GeoPointND C) {
		Construction cons = kernel.getConstruction();

		AlgoElement algoSegment = companion.segmentAlgo(cons, B, C);
		cons.removeFromConstructionList(algoSegment);

		GeoConicND circle = companion.circle(cons, A,
				(GeoNumberValue) algoSegment.getOutput(0));
		circle.setToSpecific();
		circle.update();
		return circle;
	}

	protected final GeoElement[] vectorFromPoint(Hits hits,
			boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		// point
		int count = addSelectedPoint(hits, 1, false, selPreview);

		// vector
		if (count == 0) {
			addSelectedVector(hits, 1, false, selPreview);
		}

		if ((selPoints() == 1) && (selVectors() == 1)) {
			GeoVectorND[] vecs = getSelectedVectorsND();
			GeoPointND[] points = getSelectedPointsND();

			return new GeoElement[] {companion.vectorPoint(points[0], vecs[0])};
		}
		return null;
	}

	protected final boolean circlePointRadius(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return false;
		}

		addSelectedPoint(hits, 1, false, selPreview);

		// we got the center point
		if (selPoints() == 1) {
			getDialogManager().showNumberInputDialogCirclePointRadius(
					localization.getMenu(EuclidianConstants.getModeText(mode)),
					getSelectedPointsND()[0], view);
			return true;
		}

		return false;
	}

	/**
	 * @return the current movedGeoPoint
	 */
	public GeoElement getMovedGeoPoint() {
		return ((GeoElement) movedGeoPoint);
	}

	/**
	 * @param geo
	 *            moved point (unchecked cas)
	 */
	public void setMovedGeoPoint(GeoElementND geo) {
		movedGeoPoint = (GeoPointND) geo;

		AlgoElement algo = movedGeoPoint.getParentAlgorithm();
		if (algo instanceof AlgoDynamicCoordinatesInterface) {
			movedGeoPoint = ((AlgoDynamicCoordinatesInterface) algo)
					.getParentPoint();
		}

		view.setShowMouseCoords(
				!app.isApplet() && !movedGeoPoint.isPointOnPath());
		setDragCursor();
	}

	/**
	 * 
	 * @return true if we check transparency (of polygons, planes, etc.) for
	 *         sorting drawables
	 */
	public boolean checkTransparencyForSortingDrawables() {
		return true;
	}

	/**
	 * 
	 * @return true if in AR mode, and using tool that creates points
	 */
	public boolean isCreatingPointAR() {
		return false;
	}

	/**
	 * Create new point or update an existing one from hits.
	 * 
	 * @param forPreviewable
	 *            for preview?
	 * @param hits
	 *            hits
	 * @param onPathPossible
	 *            whether new point on path is allowed
	 * @param inRegionPossible
	 *            whether new point in region is allowed
	 * @param intersectPossible
	 *            whether intersect is allowed
	 * @param chooseGeo
	 *            choose geo?
	 * @param complexPoint
	 *            whether to use complex coords
	 * @return new / updated point
	 */
	public final GeoPointND updateNewPoint(boolean forPreviewable, Hits hits,
			boolean onPathPossible, boolean inRegionPossible,
			boolean intersectPossible, boolean chooseGeo, boolean complexPoint) {
		// create hits for region
		Hits regionHits = getRegionHits(hits);

		// make sure Point Tool works when you click on eg xAxis where a slider
		// is
		hits.removeSliders();

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
				createNewPoint((GeoPointND) hits
						.getHits(TestGeo.GEOPOINTND, tempArrayList).get(0));
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
			// point in a region
			if (mode != EuclidianConstants.MODE_POINT_ON_OBJECT) {
				regionHits.removeHasFacesIfFacePresent();
			}
			if (!regionHits.isEmpty()) {
				if (inRegionPossible) {
					if (chooseGeo) {
						region = (Region) chooseGeo(regionHits, true);
					} else {
						region = (Region) regionHits.get(0);
					}

					if (region != null) {
						// check if region is opaque
						if (!checkTransparencyForSortingDrawables() || region
								.getAlphaValue() > MAX_TRANSPARENT_ALPHA_VALUE) {
							hits.removeGeosAfter(region);
						}

						boolean sideInHits = false;
						if (region instanceof HasSegments) {
							GeoSegmentND[] sides = ((HasSegments) region)
									.getSegments();

							if (sides != null) {
								for (GeoSegmentND side : sides) {
									if (hits.contains(side)) {
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
									// is in hits, still don't need the
									// polygon as a path
									region = null;
								}
							}
						} else if (region.isGeoConic()) {
							if (createNewPointInRegionPossible(
									(GeoConicND) region)) {
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
								// treated as a path
							} else {
								createPoint = true;
							}
						}

						// if no polygon side in hits, then remove all polygons
						// for path (use it also when region is conic, etc.)
						if (!sideInHits) {
							createPoint = true;
							hits.removeHasSegmentsIfSideNotPresent(); // if a
							// polygon is a region, need only polygons
							// that should be a path
							if (mode == EuclidianConstants.MODE_POINT_ON_OBJECT) {
								hits.removeSegmentsFromPolygons(); // remove
								// polygon's segments to take the
								// polygon for path
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
			// point on a path
			if (createPointOnBoundary) {
				// special case for MODE_POINT_ON_OBJECT : if an edge of a
				// polygon is clicked, create Point[polygon]
				path = (Path) region;
				region = null;
				createPoint = true;
			} else {
				Hits pathHits = hits.getHits(TestGeo.PATH_NO_FILL_HIT,
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
				point = companion.createNewPoint(forPreviewable, path, complexPoint);
			} else if ((region != null) && inRegionPossible) {
				point = companion.createNewPoint(forPreviewable, region,
						complexPoint);
			} else {
				point = companion.createNewPoint(forPreviewable, complexPoint);
				view.setShowMouseCoords(true);
			}
		}

		return point;
	}

	protected boolean createNewPointInRegionPossible(GeoConicND conic) {
		return ((mode == EuclidianConstants.MODE_POINT_ON_OBJECT)
				&& (conic.getLastHitType() == HitType.ON_FILLING));
	}

	protected GeoPointND getNewPoint(Hits hits, boolean onPathPossible,
			boolean inRegionPossible, boolean intersectPossible,
			boolean complexPoint) {

		return updateNewPoint(false, hits, onPathPossible, inRegionPossible,
				intersectPossible, true, complexPoint);
	}

	protected boolean createNewPointND(Hits hits, boolean onPathPossible,
			boolean inRegionPossible, boolean intersectPossible,
			boolean doSingleHighlighting, boolean complexPoint) {
		pointCreated = null;

		if (!allowPointCreation()) {
			return false;
		}

		GeoPointND point = getNewPoint(hits, onPathPossible, inRegionPossible,
				intersectPossible, complexPoint);

		if (point != null) {
			pointCreated = point;

			handleMovedElement((GeoElement) point, false,
					PointerEventType.MOUSE);

			setDragCursor();
			if (doSingleHighlighting) {
				doSingleHighlighting(getMovedGeoPoint());
			}

			return true;
		}

		moveMode = MOVE_NONE;
		return false;
	}

	/**
	 * @param hits
	 *            hits
	 * @param onPathPossible
	 *            whether to allow point on path
	 * @param intersectPossible
	 *            whether to allow intersections
	 * @param doSingleHighlighting
	 *            whether to highlight the output
	 * @return success
	 */
	public final boolean createNewPoint(Hits hits, boolean onPathPossible,
			boolean intersectPossible, boolean doSingleHighlighting) {

		// inRegionpossible must be false so that the Segment Tool creates a
		// point on the edge of a circle
		return createNewPoint(hits, onPathPossible, false, intersectPossible,
				doSingleHighlighting, false);
	}

	protected final boolean button(boolean textfield, boolean selPreview) {
		if (!selPreview && (mouseLoc != null)) {
			getDialogManager().showButtonCreationDialog(mouseLoc.x, mouseLoc.y,
					textfield);
		}
		return false;
	}

	protected boolean switchModeForProcessMode(Hits hits, boolean isControlDown,
			final AsyncOperation<Boolean> callback, boolean selectionPreview) {
		Boolean changedKernel = false;
		GeoElementND[] ret = null;

		switch (mode) {
		case EuclidianConstants.MODE_MOVE:
		case EuclidianConstants.MODE_SELECT:
		case EuclidianConstants.MODE_SELECT_MOW:
			// highlight and select hits
			if (selectionPreview) {
				getSelectables(hits.getTopHits(), selectionPreview);
			} else {
				if (isDraggingOccuredBeyondThreshold() && (selection.selectedGeosSize() == 1)) {
					selection.clearSelectedGeos();
				}

			}
			break;

		case EuclidianConstants.MODE_MOVE_ROTATE:
			// moveRotate() is a dummy function for highlighting only
			if (selectionPreview) {
				moveRotate(hits.getTopHits(), selectionPreview);
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

				point(hits, selectionPreview);
			} else {
				GeoElement[] ret0 = { null };
				ret0[0] = hits.getFirstHit(TestGeo.GEOPOINTND);
				ret = ret0;
				clearSelection(getSelectedPointList());
			}
			break;

		// copy geo to algebra input
		case EuclidianConstants.MODE_SELECTION_LISTENER:
			geoElementSelected(hits.getTopHits(), isControlDown,
					selectionPreview);
			break;

		// new line through two points
		case EuclidianConstants.MODE_JOIN:
			ret = join(hits, selectionPreview);
			break;

		// new segment through two points
		case EuclidianConstants.MODE_SEGMENT:
			ret = segment(hits, selectionPreview);
			break;

		// segment for point and number
		case EuclidianConstants.MODE_SEGMENT_FIXED:
			changedKernel = segmentFixed(hits, selectionPreview);
			break;

		// angle for two points and number
		case EuclidianConstants.MODE_ANGLE_FIXED:
			ret = angleFixed(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_MIDPOINT:
			ret = midpoint(hits, selectionPreview);
			break;

		// new ray through two points or point and vector
		case EuclidianConstants.MODE_RAY:
			ret = ray(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_POLYLINE:
			ret = polyline(hits, selectionPreview);
			break;

		// new polygon through points
		case EuclidianConstants.MODE_POLYGON:
			polygonMode = POLYGON_NORMAL;
			ret = polygon(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_RIGID_POLYGON:
			polygonMode = POLYGON_RIGID;
			ret = polygon(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_VECTOR_POLYGON:
			polygonMode = POLYGON_VECTOR;
			ret = polygon(hits, selectionPreview);
			break;

		// new vector between two points
		case EuclidianConstants.MODE_VECTOR:
			ret = vector(hits, selectionPreview);
			break;

		// intersect two objects
		case EuclidianConstants.MODE_INTERSECT:
			ret = intersect(hits, selectionPreview);
			break;

		// new line through point with direction of vector or line
		case EuclidianConstants.MODE_PARALLEL:
			ret = parallel(hits, selectionPreview);
			break;

		// Michael Borcherds 2008-04-08
		case EuclidianConstants.MODE_PARABOLA:
			ret = parabola(hits, selectionPreview);
			break;

		// new line through point orthogonal to vector or line
		case EuclidianConstants.MODE_ORTHOGONAL:
		case EuclidianConstants.MODE_ORTHOGONAL_THREE_D:
			ret = orthogonal(hits, selectionPreview);
			break;

		// new line bisector
		case EuclidianConstants.MODE_LINE_BISECTOR:
			ret = lineBisector(hits, selectionPreview);
			break;

		// new angular bisector
		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
			ret = angularBisector(hits, selectionPreview);
			break;

		// new circle (2 points)
		case EuclidianConstants.MODE_CIRCLE_TWO_POINTS:
			// new semicircle (2 points)
		case EuclidianConstants.MODE_SEMICIRCLE:
			ret = circleOrSphere2(hits, mode, selectionPreview);
			break;

		case EuclidianConstants.MODE_LOCUS:
			ret = locus(hits, selectionPreview);
			break;

		// new circle (3 points)
		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			ret = threePoints(hits, mode, selectionPreview);
			break;

		// new conic (5 points)
		case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
			ret = conic5(hits, selectionPreview);
			break;

		// relation query
		case EuclidianConstants.MODE_RELATION:
			relation(hits.getTopHits(), selectionPreview);
			break;

		// new tangents
		case EuclidianConstants.MODE_TANGENTS:
			ret = tangents(hits.getTopHits(), selectionPreview);
			break;

		case EuclidianConstants.MODE_POLAR_DIAMETER:
			ret = polarLine(hits.getTopHits(), selectionPreview);
			break;

		// delete selected object
		case EuclidianConstants.MODE_ERASER:
			changedKernel = getDeleteMode().process(hits.getTopHits(),
					isControlDown, selectionPreview);
			view.setCursor(EuclidianCursor.ERASER);

			break;

		// delete selected object
		case EuclidianConstants.MODE_DELETE:
			changedKernel = getDeleteMode().process(hits.getTopHits(),
					isControlDown, selectionPreview);
			break;

		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
			if (showHideObject(hits.getTopHits(), selectionPreview)) {
				toggleModeChangedKernel = true;
			}
			break;

		case EuclidianConstants.MODE_SHOW_HIDE_LABEL:
			if (showHideLabel(hits.getTopHits(), selectionPreview)) {
				toggleModeChangedKernel = true;
			}
			break;

		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
			if (copyVisualStyle(hits.getTopHits(), selectionPreview)) {
				toggleModeChangedKernel = true;
			}
			break;

		// new text

		case EuclidianConstants.MODE_MEDIA_TEXT:
		case EuclidianConstants.MODE_TEXT:
			changedKernel = text(
					hits.getOtherHits(TestGeo.GEOIMAGE, tempArrayList),
					selectionPreview);
			break;

		case EuclidianConstants.MODE_AUDIO:
			// addAudio();
			break;
		// new image
		case EuclidianConstants.MODE_IMAGE:
			break;

		// new slider
		case EuclidianConstants.MODE_SLIDER:
			changedKernel = slider(selectionPreview);
			break;

		case EuclidianConstants.MODE_MIRROR_AT_POINT:
			ret = mirrorAtPoint(hits.getTopHits(), selectionPreview);
			break;

		case EuclidianConstants.MODE_MIRROR_AT_LINE:
			ret = mirrorAtLine(hits.getTopHits(), selectionPreview);
			break;

		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE:
			ret = mirrorAtCircle(hits.getTopHits(), selectionPreview);
			break;

		case EuclidianConstants.MODE_ATTACH_DETACH:
			changedKernel = attachDetach(hits.getTopHits(), selectionPreview);
			break;

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			ret = translateByVector(hits.getTopHits(), selectionPreview);
			break;

		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			ret = rotateByAngle(hits.getTopHits(), selectionPreview);
			break;

		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			ret = dilateFromPoint(hits.getTopHits(), selectionPreview);
			break;

		case EuclidianConstants.MODE_FITLINE:
			ret = fitLine(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_CREATE_LIST:
			ret = createList(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS:
			changedKernel = circlePointRadius(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_ANGLE:
			ret = angle(hits.getTopHits(), selectionPreview);
			break;

		case EuclidianConstants.MODE_VECTOR_FROM_POINT:
			ret = vectorFromPoint(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_DISTANCE:
			ret = distance(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_MACRO:
			// TODO: is memorizeJustCreatedGeos... needed here?
			// if not, wee needn't callback2 here, we can use the
			// another callback object in macro, which we got
			// in parameter.
			final boolean selPreview = selectionPreview;
			AsyncOperation<Boolean> callback2 = new AsyncOperation<Boolean>() {
				@Override
				public void callback(Boolean arg) {
					memorizeJustCreatedGeosAfterProcessMode(null, selPreview);
					if (callback != null) {
						callback.callback(arg);
					}
				}
			};

			return getMacroMode().macro(hits, callback2, selectionPreview);

		case EuclidianConstants.MODE_AREA:
			ret = area(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_SLOPE:
			ret = slope(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_REGULAR_POLYGON:
			changedKernel = regularPolygon(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX:
			changedKernel = showCheckBox(selectionPreview);
			break;

		case EuclidianConstants.MODE_BUTTON_ACTION:
			changedKernel = button(false, selectionPreview);
			break;

		case EuclidianConstants.MODE_TEXTFIELD_ACTION:
			changedKernel = button(true, selectionPreview);
			break;

		case EuclidianConstants.MODE_PEN:
		case EuclidianConstants.MODE_FREEHAND_SHAPE:
			// MOW-75
			view.setCursor(EuclidianCursor.PEN);
			break;

		case EuclidianConstants.MODE_HIGHLIGHTER:
			view.setCursor(EuclidianCursor.HIGHLIGHTER);
			break;

		// Michael Borcherds 2008-03-13
		case EuclidianConstants.MODE_COMPASSES:
			ret = compasses(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_FUNCTION_INSPECTOR:
			changedKernel = functionInspector(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_EXTREMUM:
			ret = extremum(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_ROOTS:
			ret = roots(hits, selectionPreview);
			break;

		default:
			// do nothing
		}

		return endOfSwitchModeForProcessMode(ret, changedKernel, callback,
				selectionPreview);
	}

	protected void showDynamicStylebar() {
		// implemented in EuclidianControllerW
	}

	final protected boolean endOfSwitchModeForProcessMode(GeoElementND[] ret,
			boolean changedKernel, AsyncOperation<Boolean> callback,
			boolean selPreview) {
		memorizeJustCreatedGeosAfterProcessMode(ret, selPreview);

		if (callback != null) {
			callback.callback(changedKernel || (ret != null));
		}

		return changedKernel || ret != null;
	}

	protected void memorizeJustCreatedGeosAfterProcessMode(GeoElementND[] ret,
			boolean selPreview) {
		if (ret != null) {
			memorizeJustCreatedGeos(ret);
		} else if (!selPreview) {
			clearJustCreatedGeos();
		}
	}

	/**
	 * Process mode lock.
	 */
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
			if (TestGeo.PATH_NO_FILL_HIT.check(geo) && !geo.isGeoPolygon()) {
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

	/**
	 * @param processHits
	 *            hits
	 * @param isControlDown
	 *            whether control is pressed
	 * @return whether kernel changed
	 */
	public final boolean processMode(Hits processHits, boolean isControlDown) {
		final Hits hits2 = processHits;
		AsyncOperation<Boolean> callback = new AsyncOperation<Boolean>() {

			@Override
			public void callback(Boolean changedKernel) {
				if (changedKernel.equals(true)) {
					storeUndoInfo();
				}
				endOfWrapMouseReleased(hits2, false, false, null);
				// type = null is not a problem since alt = false
			}
		};
		return processMode(processHits, isControlDown, callback);
	}

	/**
	 * @param processHits
	 *            hits
	 * @param isControlDown
	 *            whether control is pressed
	 * @param callback
	 *            callback after mode processing is complete (async with
	 *            dialogs)
	 * @return whether kernel changed
	 */
	public final boolean processMode(Hits processHits, boolean isControlDown,
			final AsyncOperation<Boolean> callback) {
		Hits hits = processHits;
		boolean changedKernel;

		if (hits == null) {
			hits = new Hits();
		}

		AsyncOperation<Boolean> callback2;
		if (callback == null) {
			callback2 = null;
		} else {
			callback2 = new AsyncOperation<Boolean>() {

				@Override
				public void callback(Boolean ret) {
					callback.callback(ret);
					updatePreview();
				}
			};
		}

		changedKernel = switchModeForProcessMode(hits, isControlDown, callback2,
				false);

		if (changedKernel) {
			toolCompleted();
		}

		if (callback == null) {
			updatePreview();
		}

		return changedKernel;
	}

	private void processModeForHighlight(Hits processHits,
			boolean isControlDown) {
		Hits hits = processHits;
		if (hits == null) {
			hits = new Hits();
		}

		switchModeForProcessMode(hits, isControlDown, null, true);
		updatePreview();
	}

	/**
	 * This method is called, if the construction of the selected tool is
	 * finished. E.g. after a new segment was created, if the segment tool is
	 * selected.
	 */
	public void toolCompleted() {
		// not used in common, overwritten for other projects
	}

	/**
	 * Update preview Drawable.
	 */
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
		if (app.isUsingFullGui()) {
			getMovedGeoPoint().resetTraceColumns();
		}
	}

	/**
	 * right-release the mouse makes stop 3D rotation
	 *
	 * @param x
	 *            mouse location (x)
	 * @param type
	 *            event type
	 *
	 * @return false
	 */
	protected boolean processReleaseForRotate3D(int x, PointerEventType type) {
		return false;
	}

	/**
	 * exit temporary mode (if set) and reset mode to old mode
	 */
	public void exitTemporaryMode() {
		if (temporaryMode) {
			view.setMode(oldMode, ModeSetter.EXIT_TEMPORARY_MODE);
			this.defaultEventType = this.oldEventType;
			temporaryMode = false;
		}
	}

	protected final void rotateObject(boolean repaint) {
		double newAngle = Math.atan2(yRW - rotationCenter.inhomY,
				xRW - rotationCenter.inhomX);
		double angle = newAngle - rotationLastAngle;
		if (tempNum == null) {
			tempNum = new MyDouble(kernel);
		}
		tempNum.set(angle);
		if (rotGeoElement.isPointerChangeable()) {
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
		movedLabelGeoElement.setLabelOffset(
				(oldLoc.x + mouseLoc.x) - startLoc.x,
				(oldLoc.y + mouseLoc.y) - startLoc.y);
		// no update cascade needed
		movedLabelGeoElement.update();
		kernel.notifyRepaint();
	}

	protected void movePointWithOffset(boolean repaint) {
		movedGeoPoint.setCoords(
				DoubleUtil.checkDecimalFraction(xRW - transformCoordsOffset[0]),
				DoubleUtil.checkDecimalFraction(yRW - transformCoordsOffset[1]),
				1.0);
		movedGeoPoint.updateCascade();

		if (repaint) {
			kernel.notifyRepaint();
		}
	}

	protected void moveLine(boolean repaint) {
		// make parallel geoLine through (xRW, yRW)
		movedGeoLine.setLineThrough(xRW, yRW);
		updateAfterMove(movedGeoLine, repaint);
	}

	protected final void moveVector(boolean repaint) {
		moveVector();
		updateAfterMove(movedGeoVector, repaint);
	}

	protected void moveVector() {
		GeoPointND P = movedGeoVector.getStartPoint();
		if (P == null) {
			moveVector(xRW - transformCoordsOffset[0],
					yRW - transformCoordsOffset[1]);
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
			movedGeoText.setAbsoluteScreenLoc(
					(oldLoc.x + mouseLoc.x) - startLoc.x,
					(oldLoc.y + mouseLoc.y) - startLoc.y);
		} else {
			if (movedGeoText.hasAbsoluteLocation()) {
				// absolute location: change location
				moveTextAbsoluteLocation();

			} else {
				// relative location: move label (change label offset)
				movedGeoText.setLabelOffset(
						(oldLoc.x + mouseLoc.x) - startLoc.x,
						(oldLoc.y + mouseLoc.y) - startLoc.y);
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
						corner.setCoords(corner.inhomX + vx, corner.inhomY + vy,
								1.0);
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
		if (isAltDown() && (movedGeoConic
				.getType() == GeoConicNDConstants.CONIC_PARABOLA
				|| movedGeoConic
						.getType() == GeoConicNDConstants.CONIC_DOUBLE_LINE)) {

			// drag a parabola bit keep the vertex fixed
			// CONIC_DOUBLE_LINE needed for y=0x^2

			double vX = movedGeoConic.b.getX();
			double vY = movedGeoConic.b.getY();

			int eigenvecIndex = movedGeoConic
					.getType() == GeoConicNDConstants.CONIC_PARABOLA ? 0 : 1;
			double c = movedGeoConic.getEigenvec(eigenvecIndex).getX();
			double s = movedGeoConic.getEigenvec(eigenvecIndex).getY();

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

			movedGeoConic.setCoeffs(coeff * s * s, -2 * coeff * s * c,
					coeff * c * c, -c, -s, 0);

			movedGeoConic.translate(vX, vY);
		} else {
			// just translate conic
			movedGeoConic.set(tempConic);
			movedGeoConic.translate(xRW - getStartPointX(),
					yRW - getStartPointY());
		}

		if (repaint) {
			movedGeoConic.updateRepaint();
		} else {
			movedGeoConic.updateCascade();
		}
	}

	protected final void moveImplicitCurve(boolean repaint) {
		movedGeoImplicitCurve.set(tempImplicitCurve);
		movedGeoImplicitCurve.translate(xRW - getStartPointX(),
				yRW - getStartPointY());

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
		}

		if (repaint) {
			movedGeoImplicitCurve.updateRepaint();
		} else {
			movedGeoImplicitCurve.updateCascade();
		}
	}

	protected final void moveFreehand(boolean repaint) {
		movedGeoFunction.set(tempFunction);
		movedGeoFunction.translate(xRW - getStartPointX(),
				yRW - getStartPointY());

		setStartPointLocation(xRW, yRW);

		if (repaint) {
			movedGeoFunction.updateRepaint();
		} else {
			movedGeoFunction.updateCascade();
		}
	}

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
					if (Double.isNaN(initxRW) || DoubleUtil.isZero(initxRW)) {
						initxRW = xRW;
						initFactor = 1;
					} else {
						if (!DoubleUtil.isZero(xRW)) {
							movedGeoFunction.getFunction()
									.dilateX(xRW / initxRW / initFactor);
							initFactor = xRW / initxRW;
							movedGeoFunction.updateRepaint();
						}
					}

				}

				return;
			}
		}

		if (quadratic) {
			double p = (yRW - vertexY) / ((xRW - vertexX) * (xRW - vertexX));

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
			movedGeoFunction.translate(xRW - getStartPointX(),
					yRW - getStartPointY());
		}

		if (repaint) {
			// GGB-1249 fast dragging of CAS functions
			movedGeoFunction.updateRepaint(true);
		} else {
			movedGeoFunction.updateCascade();
		}
	}

	protected final void moveBoolean(boolean repaint) {
		// part of snap to grid code
		movedGeoBoolean.setAbsoluteScreenLoc(
				view.toScreenCoordX(xRW - getStartPointX()),
				view.toScreenCoordY(yRW - getStartPointY()),
				isMoveCheckboxExpected());

		if (repaint) {
			movedGeoBoolean.updateRepaint();
		} else {
			movedGeoBoolean.updateCascade();
		}
	}

	protected final void moveButton(boolean repaint) {
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

	protected final void moveMedia(boolean repaint) {
		// part of snap to grid code
		movedGeoMedia.setAbsoluteScreenLoc(view.toScreenCoordX(xRW - getStartPointX()),
				view.toScreenCoordY(yRW - getStartPointY()));

		if (movedGeoMedia instanceof GeoVideo
				|| movedGeoMedia instanceof GeoEmbed) {
			moveVideo();
		}
		if (repaint) {
			movedGeoMedia.updateRepaint();
		} else {
			movedGeoMedia.updateCascade();
		}
	}

	protected final double getSliderValue(GeoNumeric movedSlider,
			boolean click) {
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

		// make sure we don't show eg 5.2 for slider <-5,5> in the hit threshold
		param = Math.max(0, Math.min(movedSlider.getSliderWidth(), param));
		param = (param * (max - min)) / movedSlider.getSliderWidth();

		// round to animation step scale
		param = Kernel.roundToScale(param, movedSlider.getAnimationStep());
		double val = min + param;

		if (movedSlider.getAnimationStep() > Kernel.MIN_PRECISION) {
			// round to decimal fraction, e.g. 2.800000000001 to 2.8
			val = DoubleUtil.checkDecimalFraction(val);
		}

		if (movedSlider.isGeoAngle()) {
			val = DoubleUtil.checkDecimalFraction(val * Kernel.CONST_180_PI)
					/ Kernel.CONST_180_PI;

		}

		if (!click) {
			// dragging with mouse
			return val;
		}

		// new behaviour from GeoGebra 4.2
		// clicking just moves slider up one "notch"
		// better for touch screens

		if (DoubleUtil.isEqual(val, movedSlider.getValue())) {
			return val;
		}

		double ret;

		if (val > movedSlider.getValue()) {
			ret = Math.min(
					movedSlider.getValue() + movedSlider.getAnimationStep(),
					movedSlider.getIntervalMax());
		} else {
			ret = Math.max(
					movedSlider.getValue() - movedSlider.getAnimationStep(),
					movedSlider.getIntervalMin());
		}

		return DoubleUtil.checkDecimalFraction(ret);
	}

	private void setAudioTimeValue(GeoAudio movedAudio) {
		double max = movedAudio.getDuration();

		DrawAudio da = (DrawAudio) view.getDrawableFor(movedAudio);
		int d = mouseLoc.x - da.getSliderLeft();
		double currTime = (max / da.getSliderWidth()) * d;
		int val = (int) currTime;
		movedAudio.setCurrentTime(val);
	}

	protected final void moveNumeric(boolean click) {
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

		// stop all animation if slider dragged
		if (movedGeoNumeric.isAnimating()) {
			kernel.getAnimatonManager().stopAnimation();
		}

		movedGeoNumeric.setValue(newVal);
		movedGeoNumeric.updateRepaint();
	}

	protected final void moveAudioSlider(boolean repaint) {
		GeoAudio audio = (GeoAudio) movedGeoMedia;
		setAudioTimeValue(audio);
		audio.updateRepaint();
	}

	protected final void moveSlider(boolean repaint) {
		// TEMPORARY_MODE true -> dragging slider using Slider Tool
		// or right-hand mouse button

		if (movedGeoNumeric.isAbsoluteScreenLocActive()) {
			// part of snap to grid code
			movedGeoNumeric.setAbsoluteScreenLoc(
					view.toScreenCoordX(xRW - getStartPointX()),
					view.toScreenCoordY(yRW - getStartPointY()), temporaryMode);
		} else {
			movedGeoNumeric.setSliderLocation(xRW - getStartPointX(),
					yRW - getStartPointY(), temporaryMode);
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
			tmpCoordsL3 = new Coords(4);
		}
		view.getCompanion().getCoordsFromView(xRW, yRW, tmpCoordsL3);
		MoveGeos.moveObjects(translateableGeos, translationVec, tmpCoordsL3,
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
		if (app.has(Feature.SELECT_TOOL_NEW_BEHAVIOUR)) {
			MoveGeos.moveObjects(moveMultipleObjectsList, translationVec, tmpCoordsL3, null, view);
		} else {
			MoveGeos.moveObjects(
					companion.removeParentsOfView(getAppSelectedGeos()),
					translationVec, tmpCoordsL3, null, view);
		}
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

	/**
	 * For some modes, polygons are not to be removed.
	 *
	 * @param hits
	 *            hits to be filtered
	 */
	protected void switchModeForRemovePolygons(Hits hits) {
		switch (mode) {
		case EuclidianConstants.MODE_POINT:
		case EuclidianConstants.MODE_COMPLEX_NUMBER:
		case EuclidianConstants.MODE_POINT_ON_OBJECT:
		case EuclidianConstants.MODE_ATTACH_DETACH:
			// removed: polygons can still be selected if they are the only
			// object clicked on
			break;
		case EuclidianConstants.MODE_MOVE:
		case EuclidianConstants.MODE_SELECT:
		case EuclidianConstants.MODE_SELECT_MOW:
			hits.removeHasSegmentsIfSidePresent();
			break;
		default:
			hits.removePolygons();
			break;
		}
	}

	protected boolean switchModeForMouseReleased(int evMode, Hits hitsReleased,
			boolean kernelChanged, boolean controlDown, PointerEventType type,
			boolean runScripts) {
		Hits hits;
		boolean changedKernel = kernelChanged;
		boolean focusNeeded = true;
		switch (evMode) {
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
		case EuclidianConstants.MODE_DILATE_FROM_POINT:
		case EuclidianConstants.MODE_MIRROR_AT_POINT:
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE:
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			setViewHits(type);
			hits = view.getHits();
			hits.removePolygons();
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
		case EuclidianConstants.MODE_TRANSLATEVIEW:
			if (draggingOccured || !temporaryMode) {
				changedKernel = true;

			} else {
				// Ctrl pressed, we need to select a point
				setViewHits(type);
				handleSelectClick(view.getHits().getTopHits(),
						controlDown);
			}
			break;
		case EuclidianConstants.MODE_SELECT:
			if (app.has(Feature.SELECT_TOOL_NEW_BEHAVIOUR)) {
				break;
			}
		case EuclidianConstants.MODE_MOVE:
		case EuclidianConstants.MODE_SELECTION_LISTENER:
		case EuclidianConstants.MODE_SELECT_MOW:
			// handle selection click
			setViewHits(type);
			handleSelectClick(view.getHits().getTopHits(),
					controlDown);
		default:

			// change checkbox (boolean) state on mouse up only if there's been
			// no drag
			setViewHits(type);
			hits = view.getHits().getTopHits();
			if (!hits.isEmpty()) {
				GeoElement hit = hits.get(0);
				if (hit != null) {
					if (hit.isGeoButton() && !(hit.isGeoInputBox() || hit.isGeoAudio())) {
						checkBoxOrButtonJustHitted = true;
						if (!app.showView(App.VIEW_PROPERTIES)) {
							selection.removeSelectedGeo(hit, true, false); // make
							// sure doesn't get selected
							app.updateSelection(false);
						}
					} else if (hit.isGeoBoolean()) {
						if (app.has(Feature.SELECT_TOOL_NEW_BEHAVIOUR)
								&& mode == EuclidianConstants.MODE_SELECT) {
							return false;
						}
						GeoBoolean bool = (GeoBoolean) (hits.get(0));
						if (!isCheckboxFixed(bool)) { // otherwise changed on
							// mouse down
							hitCheckBox(bool);
							if (!app.showView(App.VIEW_PROPERTIES)) {
								selection.removeSelectedGeo(bool, true, false); // make
								// sure doesn't get selected
								app.updateSelection(false);
								bool.updateCascade();
							}
						}
					} else {
						GeoElement geo1 = chooseGeo(hits, true);
						// ggb3D : geo1 may be null if it's axes or xOy plane
						if (geo1 != null) {
							focusNeeded = false;
							if (runScripts) {
								runScriptsIfNeeded(geo1);
							}
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

	protected void hitCheckBox(GeoBoolean bool) {
		if (app.has(Feature.SELECT_TOOL_NEW_BEHAVIOUR)
				&& mode == EuclidianConstants.MODE_SELECT) {
			return;
		}
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
		return EuclidianConstants.isMoveOrSelectionMode(evMode);
	}

	protected boolean hitResetIcon() {
		return view.showResetIcon()
				&& ((mouseLoc.y < 40) && (mouseLoc.x > (view.getViewWidth() - 40)));
	}

	protected void setHitCursor() {
		view.setCursor(EuclidianCursor.HIT);
	}

	protected void processMouseMoved(AbstractEvent event) {
		boolean repaintNeeded;

		// reset icon
		if (hitResetIcon()) {
			view.setToolTipText(localization.getPlainTooltip("resetConstruction"));
			setHitCursor();
			return;
		}

		// animation button
		boolean hitAnimationButton = view.hitAnimationButton(event.getX(),
				event.getY());
		repaintNeeded = view.setAnimationButtonsHighlighted(hitAnimationButton);
		if (hitAnimationButton) {
			if (kernel.isAnimationPaused()) {
				view.setToolTipText(localization.getPlainTooltip("Play"));
			} else {
				view.setToolTipText(localization.getPlainTooltip("Pause"));
			}

			setHitCursor();
			view.repaintView();
			return;
		}

		for (GeoElement hit : view.getHits().getTopHits()) {
			if (overComboBox(event, hit)) {
				return;
			}
		}

		Drawable d = view.getBoundingBoxHandlerHit(mouseLoc, event.getType());
		if (mode == EuclidianConstants.MODE_SHAPE_FREEFORM && view
				.getHitHandler() == EuclidianBoundingBoxHandler.UNDEFINED) {
			view.setCursor(EuclidianCursor.DEFAULT);
			getShapeMode().handleMouseMoveForShapeMode(event);
			return;
		}

		// standard handling
		Hits hits = new Hits();
		boolean noHighlighting = false;
		setAltDown(event.isAltDown());

		// label hit
		GeoElement geo = view.getLabelHit(mouseLoc, event.getType());
		mouseIsOverLabel = geo != null;

		// change cursor also in shape mode for hover
		if (moveMode(mode) || shapeMode(mode)) { // label hit in move mode:
													// block all other hits
			if (geo != null) {
				noHighlighting = true;
				tempArrayList.clear();
				tempArrayList.add(geo);
				hits = tempArrayList;
			}
			if (view.getBoundingBox() != null && geo == null) {
				if (d != null && view.getBoundingBox() == d.getBoundingBox()) {
					setBoundingBoxCursor(d);
					return;
				} else if (view.getBoundingBox()
						.hitSideOfBoundingBox(event.getX(), event.getY(),
								app.getCapturingThreshold(event.getType()))) {
					EuclidianBoundingBoxHandler handler = view.getBoundingBox()
							.getHitHandler(event.getX(), event.getY(),
									app.getCapturingThreshold(event.getType()));
					// set handler and cursor
					view.setHitHandler(handler);
					setBoundingBoxCursor(null);
					// if handler is UNDEFINED the side of the bounding box
					// was hit
					if (handler == EuclidianBoundingBoxHandler.UNDEFINED) {
						setDragCursor();
					}
					return;
				}
			}
		}

		if (hits.isEmpty()) {
			setViewHits(event.getType());
			hits = view.getHits();
			switchModeForRemovePolygons(hits);
		}

		if (hits.isEmpty()) {
			view.setToolTipText(null);
			view.setCursor(EuclidianCursor.DEFAULT);
			if (event.isShiftDown()
					|| mode == EuclidianConstants.MODE_TRANSLATEVIEW) {
				setCursorForTranslateViewNoHit();
			} else {
				view.setCursor(EuclidianCursor.DEFAULT);
			}
		} else {
			if ((event.isShiftDown()
					|| mode == EuclidianConstants.MODE_TRANSLATEVIEW)
					&& (hits.size() >= 1)) {
				setCursorForTranslateView(hits);
			} else {
				setCursorForProccessMouseMoveHit();
			}
		}

		// for testing: save the full hits for later use
		Hits tempFullHits = hits.cloneHits();

		hits = hits.getTopHits();

		if (hits.size() == 1) {
			GeoElement hit = hits.get(0);
			int labelMode = hit.getLabelMode();
			if (hit.isGeoNumeric() && ((GeoNumeric) hit).isSlider()
					&& ((labelMode == GeoElementND.LABEL_NAME_VALUE)
							|| (labelMode == GeoElementND.LABEL_VALUE))) {
				// only do this if we are not pasting something from the
				// clipboard right now
				// because moving on the label of a slider might move the pasted
				// objects away otherwise
				if (pastePreviewSelected == null || pastePreviewSelected.isEmpty()) {
					setStartPointLocation(((GeoNumeric) hit).getSliderX(),
							((GeoNumeric) hit).getSliderY());
				}

			}
		}

		if (!hits.isEmpty() && mode == EuclidianConstants.MODE_MOVE) {
			boolean alwaysOn = false;
			if (view.getAllowToolTips() == EuclidianStyleConstants.TOOLTIPS_ON) {
				alwaysOn = true;
			}

			String text = GeoElement.getToolTipDescriptionHTML(hits, true, true,
					alwaysOn);

			if ("<html></html>".equals(text)) {
				text = null;
			}

			view.setToolTipText(text);
		} else {
			view.setToolTipText(null);
		}

		// previewable will be updated in refreshHighlighting
		if (view.getPreviewDrawable() != null) {
			repaintNeeded = true;
		}

		// show Mouse coordinates, manage alt -> multiple of 15 degrees
		else if (view.getShowMouseCoords() && view.getAllowShowMouseCoords()) {
			transformCoords();
			repaintNeeded = true;
		}

		boolean control = app.isControlDown(event);
		if (noHighlighting ? refreshHighlighting(null, control)
				: refreshHighlighting(tempFullHits, control)) {

			kernel.notifyRepaint();
		} else if (repaintNeeded) {
			view.repaintView();
		}
	}

	protected void setCursorForProccessMouseMoveHit() {
		setDragCursor();
	}

	protected void setCursorForTranslateViewNoHit() {
		view.setCursor(EuclidianCursor.DEFAULT);
	}

	protected void setCursorForTranslateView(Hits hits) {
		if (hits.hasXAxis()) {
			view.setCursor(EuclidianCursor.RESIZE_X);
		} else if (hits.hasYAxis()) {
			view.setCursor(EuclidianCursor.RESIZE_Y);
		} else {
			setHitCursor();
		}
	}

	protected boolean overComboBox(AbstractEvent event, GeoElement hit) {
		if (hit.isGeoList()) {
			DrawableND dl = view.getDrawableFor(hit);
			if (dl instanceof DrawDropDownList) {
				((DrawDropDownList) dl).onOptionOver(event.getX(),
						event.getY());
				return dl.isCanvasDrawable();
			}
		}
		return false;
	}

	/**
	 * Handle movee event.
	 * 
	 * @param event
	 *            pointer move event
	 */
	public void wrapMouseMoved(AbstractEvent event) {
		if (isTextfieldHasFocus()) {
			return;
		}
		setMouseLocation(event);
		processMouseMoved(event);
	}

	protected abstract void resetToolTipManager();

    /**
     * Process mouse exit event.
     *
     * @param event mouse exit event
     */
    public void wrapMouseExited(AbstractEvent event) {
        if (isTextfieldHasFocus()) {
            return;
        }
        this.animationButtonPressed = false;
        app.storeUndoInfoIfSetCoordSystemOccured();

        refreshHighlighting(null, app.isControlDown(event));
        resetToolTipManager();
        view.setAnimationButtonsHighlighted(false);
        view.setShowMouseCoords(false);
        setMouseLocToNullIfNeeded();
        kernel.notifyRepaint();
        view.mouseExited();
    }

    protected void setMouseLocToNullIfNeeded() {
        mouseLoc = null;
    }

	protected void handleSelectClick(ArrayList<GeoElement> geos,
			boolean ctrlDown) {
		if (geos == null) {
			selection.clearSelectedGeos();
		} else {
			if (ctrlDown) {
				selection.toggleSelectedGeo(chooseGeo(geos, true));
			} else {
				Hits hits = new Hits();
				hits.addAll(geos);
				if (!moveModeSelectionHandled) {
					GeoElement geo = chooseGeo(geos, true);
					if (geo != null && !geo.isGeoButton()) {
						if (isSpecialPreviewPointFound(hits)) {
							previewPointHits = getPreviewSpecialPointHits(hits);
						} else {
							selection.clearSelectedGeos(false);
							selection.addSelectedGeo(geo);
						}
					}
				} else {
					if (isSpecialPreviewPointFound(hits)) {
						previewPointHits = getPreviewSpecialPointHits(hits);
					}
				}
			}
		}
	}

	protected void wrapMouseclicked(boolean control, int clickCount,
			PointerEventType type) {
		if (!app.showMenuBar() || control || penMode(this.mode)
				|| isDragTool()) {
			return;
		}
		// double-click on object selects MODE_MOVE and opens redefine dialog
		if (clickCount == 2) {
			selection.clearSelectedGeos(true, false);
			app.updateSelection(false);
			setViewHits(type);
			Hits hits = view.getHits().getTopHits();
			switchModeForRemovePolygons(hits);
			if (!hits.isEmpty()) {
				app.setMode(EuclidianConstants.MODE_MOVE);
				GeoElement geo0 = hits.get(0);

				if (app.has(Feature.MOW_TEXT_TOOL) && geo0.isGeoText()) {

					getTextController().edit((GeoText) geo0);
				} else if (geo0.isGeoNumeric()
						&& ((GeoNumeric) geo0).isSlider()) {
					// double-click slider -> Object Properties
					getDialogManager().showPropertiesDialog(hits);
				} else if (!geo0.isProtected(EventType.UPDATE)
						&& !(geo0.isGeoBoolean() && geo0.isIndependent())
						&& geo0.isRedefineable()
						&& !geo0.isGeoButton() && !(geo0.isGeoList()
								&& ((GeoList) geo0).drawAsComboBox())) {
					getDialogManager().showRedefineDialog(hits.get(0), true);
				}
			}
		}
	}

	/**
	 * @param x
	 *            mouse x
	 * @param y
	 *            mouse y
	 * @param type
	 *            event type
	 * @return whether textfield at given coords is focused
	 */
	public boolean textfieldJustFocused(int x, int y, PointerEventType type) {
		return view.textfieldClicked(x, y, type);
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
		setDragCursor();
		if (translationVec == null) {
			translationVec = new Coords(2);
		}
		if (app.has(Feature.SELECT_TOOL_NEW_BEHAVIOUR)) {
			// add free input points to move list
			if (moveMultipleObjectsList == null) {
				moveMultipleObjectsList = new ArrayList<>();
			} else {
				moveMultipleObjectsList.clear();
			}
			for (GeoElement geo : companion.removeParentsOfView(getAppSelectedGeos())) {
				moveMultipleObjectsList.add(geo);
				AlgoElement algo = geo.getParentAlgorithm();
				if (algo != null) { // add input points from algo
					for (GeoPointND point : geo.getFreeInputPoints(getView())) {
						moveMultipleObjectsList.add((GeoElement) point);
					}
				}
			}
		}
	}

	/**
	 * @param geo
	 *            moved geo
	 * @param multiple
	 *            whether to allow multiple geo move
	 * @param type
	 *            pointer device type
	 */
	public void handleMovedElement(GeoElement geo, boolean multiple,
			PointerEventType type) {
		resetMovedGeoPoint();
		movedGeoElement = geo;

		// default if nothing matches
		moveMode = MOVE_NONE;
		
		// do not move if there's a selected geo that's locked
		if (geo == null || selection.containsLockedGeo()) {
			return;
		}

		// multiple geos selected
		if (multiple) {
			handleMovedElementMultiple();
		}

		// DEPENDENT object: changeable parents?
		// move free parent points (e.g. for segments)
		else if (!movedGeoElement.isMoveable(view)
				&& !(isMoveButtonExpected(geo)
						|| isMoveTextFieldExpected(geo))) {
			handleMovedElementDependent();
		}

		// free point, line etc.
		else {
			handleMovedElementFree(type);
		}
	}

	final protected boolean handleMovedElementDependentWithChangeableParent() {
		// geo with changeable coord parent numbers
		if (movedGeoElement.hasChangeableCoordParentNumbers()) {
			translateableGeos = new ArrayList<>();
			translateableGeos.add(movedGeoElement);
			return true;
		}
		if (movedGeoElement.hasChangeableParent3D()) {
			translateableGeos = new ArrayList<>();
			translateableGeos.add(movedGeoElement);
			return true;
		}

		return false;
	}

	protected void handleMovedElementDependent() {
		translateableGeos = null;
		GeoVectorND vec = null;
		boolean sameVector = true;

		// allow dragging of Translate[Object, vector] if 'vector' is
		// independent
		if (movedGeoElement instanceof GeoPoly) {
			GeoPoly poly = (GeoPoly) movedGeoElement;
			GeoPointND[] pts = poly.getPoints();

			// get vector for first point
			AlgoElement algo = null;
			if (pts != null && pts[0] != null) {
				algo = pts[0].getParentAlgorithm();
			}
			if (algo instanceof AlgoTranslate) {
				GeoElement[] input = algo.getInput();

				if (input[1].isIndependent()) {
					vec = (GeoVectorND) input[1];

					// now check other points are translated by the same vector
					for (int i = 1; i < pts.length; i++) {
						algo = pts[i].getParentAlgorithm();
						if (!(algo instanceof AlgoTranslate)) {
							sameVector = false;
							break;
						}
						input = algo.getInput();

						GeoVectorND vec2 = (GeoVectorND) input[1];
						if (vec != vec2) {
							sameVector = false;
							break;
						}

					}
				}

			}
		} else if (movedGeoElement.isGeoSegment() || movedGeoElement.isGeoRay()
				|| (movedGeoElement
						.getParentAlgorithm() instanceof AlgoVector)) {
			GeoPointND start;
			GeoPointND end;
			if (movedGeoElement.getParentAlgorithm() instanceof AlgoVector) {
				// Vector[A,B]
				AlgoVector algoVec = (AlgoVector) movedGeoElement
						.getParentAlgorithm();
				start = algoVec.getP();
				end = algoVec.getQ();

				if (start.isIndependent() && !end.isIndependent()) {
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
					vec = (GeoVectorND) input[1];
					GeoElement[] input2 = algo2.getInput();
					GeoVectorND vec2 = (GeoVectorND) input2[1];

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
				if ((input[1].isIndependent() || input[1]
						.getParentAlgorithm() instanceof AlgoVectorPoint)
						&& input[1] instanceof GeoVectorND) {
					vec = (GeoVectorND) input[1];
				}
			}
		} else if (movedGeoElement
				.getParentAlgorithm() instanceof AlgoVectorPoint) {
			// allow Vector[(1,2)] to be dragged
			vec = (GeoVectorND) movedGeoElement;
		}

		if (vec != null) {
			if (vec.getParentAlgorithm() instanceof AlgoVectorPoint) {
				// unwrap Vector[(1,2)]
				AlgoVectorPoint algo = (AlgoVectorPoint) vec
						.getParentAlgorithm();
				moveMode = MOVE_POINT_WITH_OFFSET;
				transformCoordsOffset[0] = xRW - vec.getX();
				transformCoordsOffset[1] = yRW - vec.getY();
				movedGeoPoint = algo.getP();
				return;
			}

			if (sameVector && ((vec.getLabelSimple() == null)
					|| vec.isIndependent())) {
				transformCoordsOffset[0] = xRW - vec.getX();
				transformCoordsOffset[1] = yRW - vec.getY();
				movedGeoVector = vec;
				moveMode = MOVE_VECTOR_NO_GRID;
				return;
			}
		}

		// STANDARD case: get free input points of dependent movedGeoElement
		if (!handleMovedElementDependentWithChangeableParent()
				&& movedGeoElement.hasMoveableInputPoints(view)) {
			// allow only moving of the following object types
			if (movedGeoElement.isGeoLine() || movedGeoElement.isGeoPolygon()
					|| (movedGeoElement instanceof GeoPolyLine)
					|| movedGeoElement.isGeoConic()
					|| movedGeoElement.isGeoImage()
					|| movedGeoElement.isGeoList()
					|| movedGeoElement.isGeoVector()
					|| movedGeoElement instanceof GeoLocusStroke) {
				if (translateableGeos == null) {
					translateableGeos = new ArrayList<>();
				} else {
					translateableGeos.clear();
				}

				addMovedGeoElementFreeInputPointsToTranslateableGeos();

				if (movedGeoElement.isGeoList()) {
					translateableGeos.add(movedGeoElement);
				}
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
					// make sure snap-to-grid works for dragging
					// (a + x(A), b + x(B))
					transformCoordsOffset[0] = 0;
					transformCoordsOffset[1] = 0;

				} else {
					// snap to grid when dragging polygons, segments, images
					// etc use first point
					point.getInhomCoords(transformCoordsOffset);
					transformCoordsOffset[0] -= xRW;
					transformCoordsOffset[1] -= yRW;
				}
			}

			setStartPointLocation();

			setDragCursor();
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
		if (movedGeoElement instanceof GeoText) {
			moveMode = MOVE_TEXT;
			movedGeoText = (GeoText) movedGeoElement;
			view.setShowMouseCoords(false);
			setDragCursor();

			if (movedGeoText.isAbsoluteScreenLocActive()) {
				oldLoc.setLocation(movedGeoText.getAbsoluteScreenLocX(),
						movedGeoText.getAbsoluteScreenLocY());
				startLoc = mouseLoc;

			} else if (movedGeoText.hasAbsoluteLocation()) {
				// absolute location: change location
				GeoPointND loc = movedGeoText.getStartPoint();
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
					setStartPointLocationWithOrigin(loc.getInhomX(), loc.getInhomY());

					GeoPointND loc2 = loc.copy();
					movedGeoText.setNeedsUpdatedBoundingBox(true);
					movedGeoText.update();
					if (movedGeoText.getBoundingBox() != null) {
						loc2.setCoords(movedGeoText.getBoundingBox().getX(),
								movedGeoText.getBoundingBox().getY(), 1.0);

						transformCoordsOffset[0] = loc2.getInhomX() - xRW;
						transformCoordsOffset[1] = loc2.getInhomY() - yRW;
					}
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
			setDragCursor();
		}

		// free vector
		else if (movedGeoElement.isGeoVector()) {
			movedGeoVector = (GeoVectorND) movedGeoElement;

			// change vector itself or move only startpoint?
			// if vector is dependent or
			// mouseLoc is closer to the startpoint than to the end
			// point then move the startpoint of the vector
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
				Coords vCoords = view
						.getCoordsForView(movedGeoVector.getCoordsInD3());
				if ((2d * MyMath.length(xRW - sx, yRW - sy)) < MyMath
						.length(vCoords.getX(), vCoords.getY())) { // take
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
			setDragCursor();
		}

		// free text
		else if (handleMovedElementFreeText()) {
			return;
		}

		// free conic
		else if (movedGeoElement.isGeoConic()) {
			moveMode = MOVE_CONIC;
			movedGeoConic = (GeoConicND) movedGeoElement;
			view.setShowMouseCoords(false);
			setDragCursor();

			// make sure when a circle is dragged it stays in
			// (x+2)^2+(y-3)^2=25 form
			if (movedGeoConic.getType() == GeoConicNDConstants.CONIC_CIRCLE
					&& movedGeoConic
							.getToStringMode() == GeoConicND.EQUATION_USER) {
				movedGeoConic.setToStringMode(GeoConicND.EQUATION_SPECIFIC);
			}

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
		} else if (movedGeoElement.isGeoImplicitCurve()) {
			moveMode = MOVE_IMPLICIT_CURVE;
			movedGeoImplicitCurve = (GeoImplicitCurve) movedGeoElement;
			view.setShowMouseCoords(false);
			setDragCursor();
			setStartPointLocation();
			if (tempImplicitCurve == null) {
				tempImplicitCurve = new GeoImplicitCurve(movedGeoImplicitCurve);
			} else {
				tempImplicitCurve.set(movedGeoImplicitCurve);
			}

			if (tempDependentPointX == null) {
				tempDependentPointX = new ArrayList<>();
			} else {
				tempDependentPointX.clear();
			}

			if (tempDependentPointY == null) {
				tempDependentPointY = new ArrayList<>();
			} else {
				tempDependentPointY.clear();
			}

			if (moveDependentPoints == null) {
				moveDependentPoints = new ArrayList<>();
			} else {
				moveDependentPoints.clear();
			}

			for (GeoElement f : movedGeoImplicitCurve.getAllChildren()) {
				if ((f instanceof GeoPoint)
						&& movedGeoImplicitCurve.isParentOf(f)) {
					GeoPoint g = (GeoPoint) f;
					if (!DoubleUtil.isZero(g.getZ())) {
						moveDependentPoints.add(g);
						tempDependentPointX.add(g.getX() / g.getZ());
						tempDependentPointY.add(g.getY() / g.getZ());
					}
				}
			}

		}
		// else removed otherwise AlgoFunctionFreehand can't be dragged
		if (movedGeoElement.isGeoFunction()) {
			if (movedGeoElement
					.getParentAlgorithm() instanceof AlgoFunctionFreehand) {

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
				LinkedList<PolyFunction> factors = movedGeoFunction
						.getFunction().getPolynomialFactors(false, true);
				if (factors != null) {

					if (factors.size() == 1
							&& factors.get(0).getDegree() == 2) {
						double c = movedGeoFunction.value(0);
						double s = movedGeoFunction.value(1);
						double a = 0.5 * (s + movedGeoFunction.value(-1))
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
			setDragCursor();

			setStartPointLocation();
			if (tempFunction == null) {
				tempFunction = new GeoFunction(kernel.getConstruction());
			}
			tempFunction.set(movedGeoFunction);
		} else if (movedGeoElement instanceof GeoLocusStroke) {
			if (translationVec == null) {
				translationVec = new Coords(2);
			}
			translateableGeos = new ArrayList<>(1);
			translateableGeos.add(movedGeoElement);
			setStartPointLocation(xRW, yRW);
			moveMode = MOVE_STROKE;
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
								xRW - view.toRealWorldCoordX(oldLoc.x),
								yRW - view.toRealWorldCoordY(oldLoc.y));
						transformCoordsOffset[0] = view
								.toRealWorldCoordX(oldLoc.x) - xRW;
						transformCoordsOffset[1] = view
								.toRealWorldCoordY(oldLoc.y) - yRW;
					} else {
						setStartPointLocation(
								xRW - movedGeoNumeric.getRealWorldLocX(),
								yRW - movedGeoNumeric.getRealWorldLocY());
						transformCoordsOffset[0] = movedGeoNumeric
								.getRealWorldLocX() - xRW;
						transformCoordsOffset[1] = movedGeoNumeric
								.getRealWorldLocY() - yRW;
					}
				} else {
					setStartPointLocation(movedGeoNumeric.getSliderX(),
							movedGeoNumeric.getSliderY());

					// update straightaway in case it's just a click (no drag)
					moveNumeric(true);
				}
			}

			view.setShowMouseCoords(false);
			setDragCursor();
		}

		// checkbox
		else if (movedGeoElement.isGeoBoolean()) {
			movedGeoBoolean = (GeoBoolean) movedGeoElement;

			// if fixed checkbox dragged, behave as if it's been clicked
			// important for electronic whiteboards / tablets
			if (!isMoveCheckboxExpected()) {
				movedGeoBoolean.setValue(!movedGeoBoolean.getBoolean());

				if (!app.showView(App.VIEW_PROPERTIES)) {
					selection.removeSelectedGeo(movedGeoBoolean); // make sure
					// doesn't get selected
				}
				movedGeoBoolean.updateCascade();
				this.checkboxChangeOccured = true;
			}

			// move checkbox
			moveMode = MOVE_BOOLEAN;
			startLoc = mouseLoc;
			oldLoc.x = movedGeoBoolean.getAbsoluteScreenLocX();
			oldLoc.y = movedGeoBoolean.getAbsoluteScreenLocY();

			// part of snap to grid code (the constant 5 comes from DrawBoolean)
			setStartPointLocation(xRW - view.toRealWorldCoordX(oldLoc.x),
					yRW - view.toRealWorldCoordY(oldLoc.y));
			transformCoordsOffset[0] = view.toRealWorldCoordX(oldLoc.x + 5)
					- xRW;
			transformCoordsOffset[1] = view.toRealWorldCoordY(oldLoc.y + 5)
					- yRW;

			view.setShowMouseCoords(false);
			setDragCursor();
		}
		// button
		else if (movedGeoElement instanceof Furniture
				&& ((Furniture) movedGeoElement).isFurniture()
				&& !(movedGeoElement instanceof GeoEmbed)) {
			// for applets:
			// allow buttons to be dragged only if the button tool is selected
			// (important for tablets)
			boolean textField = movedGeoElement instanceof GeoInputBox;
			boolean textFieldSelected = textField
					&& oldMode == EuclidianConstants.MODE_TEXTFIELD_ACTION;
			boolean buttonSelected = !textField
					&& oldMode == EuclidianConstants.MODE_BUTTON_ACTION;
			boolean moveSelected = oldMode == EuclidianConstants.MODE_MOVE;

			if ((temporaryMode || textFieldSelected || buttonSelected
					|| (moveSelected && app.isRightClickEnabled()))) {

				if (textField && !isMoveTextFieldExpected((GeoInputBox)movedGeoElement)) {
					return;
				}

				// ie Button Mode is really selected
				movedGeoButton = (Furniture) movedGeoElement;
				// move button
				moveAbsoluteLocatable(movedGeoButton, MOVE_BUTTON);

			} else {
				// need to trigger scripts
				// (on tablets only get drag events)
				// we don't want to run InputBox's script if it was just clicked
				if (!(movedGeoElement instanceof GeoInputBox)) {
					runScriptsIfNeeded(movedGeoElement);
				}
			}
		} else if (movedGeoElement instanceof GeoWidget) {
			movedGeoMedia = (GeoWidget) movedGeoElement;
			if (movedGeoMedia.isGeoAudio()
					&& !isMoveAudioExpected(app.getCapturingThreshold(type))) {
				moveMode = MOVE_AUDIO_SLIDER;
				moveAudioSlider(true);
				return;
			}
			moveAbsoluteLocatable(movedGeoMedia, MOVE_MEDIA);
		}

		// image
		else if (movedGeoElement.isGeoImage()
				&& movedGeoElement.isMoveable(view)) {
			moveMode = MOVE_IMAGE;
			movedGeoImage = (GeoImage) movedGeoElement;
			view.setShowMouseCoords(false);
			setDragCursor();

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

	private boolean isMoveTextFieldExpected(GeoInputBox geoInputBox) {
		if (geoInputBox.isEditing()) {
			return false;
		}

		if (geoInputBox.isSymbolicMode() && isDraggingBeyondThreshold()) {
			view.hideSymbolicEditor();
		}

		return true;
	}

	private void moveAbsoluteLocatable(AbsoluteScreenLocateable geo, int absMoveMode) {
		moveMode = absMoveMode;
		startLoc = mouseLoc;
		if (geo instanceof GeoWidget) {
			((GeoWidget) geo).updateAbsLocation(view);
		}
		oldLoc.x = geo.getAbsoluteScreenLocX();
		oldLoc.y = geo.getAbsoluteScreenLocY();

		// part of snap to grid code
		setStartPointLocation(xRW - view.toRealWorldCoordX(oldLoc.x),
				yRW - view.toRealWorldCoordY(oldLoc.y));
		transformCoordsOffset[0] = view.toRealWorldCoordX(oldLoc.x) - xRW;
		transformCoordsOffset[1] = view.toRealWorldCoordY(oldLoc.y) - yRW;

		view.setShowMouseCoords(false);
		setDragCursor();
	}

	private void addMovedGeoElementFreeInputPointsToTranslateableGeos() {
		ArrayList<GeoPointND> freeInputPoints = movedGeoElement
				.getFreeInputPoints(view);
		for (GeoPointND p : freeInputPoints) {
			translateableGeos.add((GeoElement) p);
		}
	}

	private boolean tempRightClick() {
		return temporaryMode && app.isRightClickEnabled();
	}

	/**
	 * checks whether the slider itself or the point of the slider should be moved
	 *
	 * @return true if the slider should be moved; false if the point on the slider
	 *         should be moved (i.e. change the number)
	 */
	protected boolean isMoveSliderExpected(int hitThreshold) {
		DrawSlider ds = (DrawSlider) view.getDrawableFor(movedGeoNumeric);
		// TEMPORARY_MODE true -> dragging slider using Slider Tool
		// or right-hand mouse button

		boolean hitSliderNotBlob = ds.hitSliderNotBlob(mouseLoc.x, mouseLoc.y, hitThreshold);
		return ((tempRightClick()) || !movedGeoNumeric.isSliderFixed()) && hitSliderNotBlob;
	}

	protected boolean isMoveAudioExpected(int hitThreshold) {
		DrawAudio da = (DrawAudio) view.getDrawableFor(movedGeoMedia);
		boolean hitSlider = da.isSliderHit(mouseLoc.x, mouseLoc.y, hitThreshold);
		return (tempRightClick()) || !hitSlider;
	}

	protected boolean isMoveCheckboxExpected() {
		return (tempRightClick() || !movedGeoBoolean.isCheckboxFixed()
				|| app.getMode() == EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX);
	}

	protected boolean isMoveButtonExpected(GeoElementND geo) {
		if (!geo.isGeoButton()) {
			return false;
		}
		GeoButton button = (GeoButton) geo;
		return (!button.isGeoInputBox() && ((tempRightClick() || !button.isLocked()
				|| app.getMode() == EuclidianConstants.MODE_BUTTON_ACTION)));
	}

	protected boolean isMoveTextFieldExpected(GeoElementND geo) {
		if (!geo.isGeoInputBox()) {
			return false;
		}
		GeoInputBox textField = (GeoInputBox) geo;
		return (textField.isTextField() && ((tempRightClick() || !textField.isLocked()
				|| app.getMode() == EuclidianConstants.MODE_TEXTFIELD_ACTION)));
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
				|| (app.isHTML5Applet() && app.isApplet());
	}

	/**
	 * @return true if there is a selection rectangle, or the rectangle is
	 *         bigger than a threshold.
	 */
	private boolean shouldUpdateSelectionRectangle() {
		if (view.getSelectionRectangle() != null) {
			return true;
		}
		int dx = mouseLoc.x - selectionStartPoint.x;
		int dy = mouseLoc.y - selectionStartPoint.y;
		double distSqr = (dx * dx) + (dy * dy);
		return distSqr > SELECTION_RECT_THRESHOLD_SQR;
	}

	protected void updateSelectionRectangle(boolean keepScreenRatio) {
		if (!shouldUpdateSelectionRectangle()) {
			return;
		}

		if (view.getSelectionRectangle() == null) {
			view.setSelectionRectangle(
					AwtFactory.getPrototype().newRectangle(0, 0));
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
			double newRatio = dy == 0 ? ratio : Math.abs(dx / (double) dy);
			if (newRatio < Math.abs(ratio * ZOOM_RECTANGLE_SNAP_RATIO)
					&& Math.abs(ratio) < newRatio * ZOOM_RECTANGLE_SNAP_RATIO) {
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
				rect.setLocation(selectionStartPoint.x,
						selectionStartPoint.y + height);
				rect.setSize(width, -height);
			} else { // width < 0
				rect.setLocation(selectionStartPoint.x + width,
						selectionStartPoint.y + height);
				rect.setSize(-width, -height);
			}
		}
	}

	public boolean isDraggingBeyondThreshold() {
		return isDraggingBeyondThreshold(DRAG_THRESHOLD);
	}

	/**
	 * @param threshold
	 *            max ignored drag distance
	 * @return whether drag from selectionStartPoint to mouseLoc is beyon
	 *         threshold
	 */
	public boolean isDraggingBeyondThreshold(int threshold) {
		return mouseLoc != null && (Math
				.abs(mouseLoc.x - selectionStartPoint.x) > threshold
				|| Math.abs(mouseLoc.y - selectionStartPoint.y) > threshold);
	}

	/**
	 * @return true, if the freehand mode is prepared (e.g. polygons, circle)
	 */
	protected boolean freehandModePrepared() {
		return freehandModePrepared;
	}

	protected final void handleMouseDragged(boolean repaint,
			AbstractEvent event, boolean manual) {
		// do not allow right-click drag for MODE_SELECT_MOW
		if (mode == EuclidianConstants.MODE_SELECT_MOW
				&& event.isRightClick()) {
			return;
		}
		// handle rotation
		if (view.getHitHandler() == EuclidianBoundingBoxHandler.ROTATION) {
			GRectangle2D bounds = (getResizedShape() != null)
					? getResizedShape().getBounds()
					: view.getBoundingBox().getRectangle();
			// bounds exist
			if (bounds != null) {
				if (lastMouseLoc == null) {
					return;
				}
				// lastMouseLoc is not updated outside the view, but the event
				// contains values in that region too, so we clamp them
				double ex = MyMath.clamp(event.getX(), 0, view.getWidth()),
						ey = MyMath.clamp(event.getY(), 0, view.getHeight());
				// calc center coords
				double centerX = bounds.getMinX() + bounds.getWidth() / 2,
						centerY = bounds.getMinY() + bounds.getHeight() / 2;
				// create rotation point
				if (rotationCenter == null) {
					rotationCenter = new GeoPoint(
							app.getKernel().getConstruction(),
							view.toRealWorldCoordX(centerX),
							view.toRealWorldCoordY(centerY), 1);
				}
				// calc rotation angle
				NumberValue angle = new GeoNumeric(
						app.getKernel().getConstruction(),
						Math.atan2(-(ey - centerY), ex - centerX)
								- Math.atan2(-(lastMouseLoc.getY() - centerY),
										lastMouseLoc.getX() - centerX));
				// do rotate geos
				if (getResizedShape() != null || isMultiResize) {
					dontClearSelection = true;
					hideDynamicStylebar();
					for (GeoElement geo : selection.getSelectedGeos()) {
						((PointRotateable) geo).rotate(angle, rotationCenter);
						geo.updateRepaint();
					}
					return;
				}
			}
		} else {
			// resize, single selection
			if (getResizedShape() != null) {
				setBoundingBoxCursor(getResizedShape());
				// resize selected geo
				if (getResizedShape().getGeoElement().isSelected()) {
					dontClearSelection = true;
					GPoint2D p = AwtFactory.getPrototype()
							.newPoint2D(event.getX(), event.getY());
					getResizedShape().updateByBoundingBoxResize(p,
							view.getHitHandler());
				}
				hideDynamicStylebar();
				view.repaintView();
				return;
			}
			// resize, multi-selection
			else if (isMultiResize) {
				handleResizeMultiple(event, view.getHitHandler());
				return;
			}
		}
		if (freehandModePrepared()) {
			// no repaint, so that the line drawn by the freehand mode will not
			// disappear
			return;
		}
		if (draggingBeyondThreshold && (mode == EuclidianConstants.MODE_DELETE
				|| mode == EuclidianConstants.MODE_ERASER)) {

			getDeleteMode().handleMouseDraggedForDelete(event,
					getDeleteToolSize(), false);

			kernel.notifyRepaint();

			return;
		}
		altCopy = false;
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

		case MOVE_IMPLICIT_CURVE:
			moveImplicitCurve(repaint);
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
			moveNumeric(!manual);
			break;

		case MOVE_SLIDER:
			moveSlider(repaint);
			break;

		case MOVE_AUDIO_SLIDER:
			moveAudioSlider(repaint);
			break;

		case MOVE_BOOLEAN:
			moveBoolean(repaint);
			break;

		case MOVE_BUTTON:
			moveButton(repaint);
			break;
		case MOVE_MEDIA:
			moveMedia(repaint);
			break;
		case MOVE_STROKE:
			moveDependent(repaint);
			break;
		case MOVE_DEPENDENT:
			if (Algos.isUsedFor(Commands.AttachCopyToView, movedGeoElement)) {
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
					view.setCursor(EuclidianCursor.MOVE);
				}
				moveView();
			}
			break;

		case MOVE_X_AXIS:
			scaleXAxis(repaint);
			break;

		case MOVE_Y_AXIS:
			scaleYAxis(repaint);
			break;

		case MOVE_Z_AXIS:
			scaleZAxis(repaint);
			break;

		default: // do nothing
		}

		kernel.notifyRepaint();
	}

	private void handleResizeMultiple(AbstractEvent event,
			EuclidianBoundingBoxHandler handler) {
		// if for some reason there was no state initialized
		if (startBoundingBoxState == null) {
			startBoundingBoxState = new BoundingBoxResizeState(
					view.getBoundingBox().getRectangle(),
					selection.getSelectedGeos(), view);
		}
		GPoint2D mouseDistance = getMouseDistance(event, handler);

		// calc boundingbox width/height
		double bbWidth = startBoundingBoxState.getRectangle().getWidth(),
				bbHeight = startBoundingBoxState.getRectangle().getHeight(),
				bbMinX = startBoundingBoxState.getRectangle().getMinX(),
				bbMinY = startBoundingBoxState.getRectangle().getMinY();
		switch (handler) {
		case RIGHT:
		case TOP_RIGHT:
		case BOTTOM_RIGHT:
			bbWidth += mouseDistance.getX();
			break;
		case LEFT:
		case TOP_LEFT:
		case BOTTOM_LEFT:
			bbWidth -= mouseDistance.getX();
			bbMinX += mouseDistance.getX();
			break;
		default:
			break;
		}
		switch (handler) {
		case TOP:
		case TOP_LEFT:
		case TOP_RIGHT:
			bbHeight -= mouseDistance.getY();
			bbMinY += mouseDistance.getY();
			break;
		case BOTTOM:
		case BOTTOM_LEFT:
		case BOTTOM_RIGHT:
			bbHeight += mouseDistance.getY();
			break;
		default:
			break;
		}
		bbWidth = Math.max(bbWidth, startBoundingBoxState.getWidthThreshold());
		bbHeight = Math.max(bbHeight,
				startBoundingBoxState.getHeightThreshold());

		boolean thresholdXReached = (bbWidth <= startBoundingBoxState
				.getWidthThreshold());
		boolean thresholdYReached = (bbHeight <= startBoundingBoxState
				.getHeightThreshold());
		if (thresholdXReached) {
			// reset bounding box minx after threshold was reached
			if (mouseDistance.getX() > 0
					&& bbWidth < startBoundingBoxState.getRectangle()
					.getWidth()) {
				bbMinX = view.getBoundingBox().getRectangle().getMaxX()
						- bbWidth;
			}
		} else if (startBoundingBoxState.lastThresholdX) {
			// always finish last resize when threshold is reached
			startBoundingBoxState.lastThresholdX = false;
		}

		if (thresholdYReached) {
			// reset bounding box miny after threshold was reached
			if (mouseDistance.getY() > 0
					&& bbHeight < startBoundingBoxState.getRectangle()
					.getHeight()) {
				bbMinY = view.getBoundingBox().getRectangle().getMaxY()
						- bbHeight;
			}
		} else if (startBoundingBoxState.lastThresholdY) {
			// always finish last resize when threshold is reached
			startBoundingBoxState.lastThresholdY = false;
		}

		for (int i = 0; i < selection.getSelectedGeos().size(); i++) {
			GeoElement geo = selection.getSelectedGeos().get(i);
			Drawable dr = (Drawable) view.getDrawableFor(geo);
			// calculate new positions relative to bounding box
			double newMinX = startBoundingBoxState.getRatios(i)[0] * bbWidth,
					newMaxX = startBoundingBoxState.getRatios(i)[1] * bbWidth,
					newMinY = startBoundingBoxState.getRatios(i)[2]
							* bbHeight,
					newMaxY = startBoundingBoxState.getRatios(i)[3]
							* bbHeight;
			if (dr instanceof DrawSegment) {
				// segments must be handled differently (by translating the
				// points separately)
				handleResizeForSegment((GeoSegment) geo, dr, bbMinX, bbMinY,
						newMinX, newMaxX, newMinY, newMaxY);
			} else {
				GRectangle2D bounds = dr.getBounds();
				if (dr.getBoundingBox() != null) {
					// if it has bounding box override bounds
					bounds = dr.getBoundingBox().getRectangle();

					// the position of the maxX and maxY from the old minX and
					// minY
					double maxXFromOld = bounds.getMinX() + (newMaxX - newMinX),
							maxYFromOld = bounds.getMinY()
									+ (newMaxY - newMinY);
					// resize to new width and height
					GPoint2D point = AwtFactory.getPrototype()
							.newPoint2D(maxXFromOld, maxYFromOld);
					if (!DoubleUtil.isEqual(newMaxX - newMinX,
							bounds.getWidth())
							&& !startBoundingBoxState.lastThresholdX) {
						dr.updateByBoundingBoxResize(point,
								EuclidianBoundingBoxHandler.RIGHT);
						dr.updateGeo(point);
					}
					if (!DoubleUtil.isEqual(newMaxY - newMinY,
							bounds.getHeight())
							&& !startBoundingBoxState.lastThresholdY) {
						dr.updateByBoundingBoxResize(point,
								EuclidianBoundingBoxHandler.BOTTOM);
						dr.updateGeo(point);
					}
				}
				if (bounds != null) {
					// if the geo wasn't resized in a direction & the divisor
					// isn't 0, recalculate the minimum coordinates
					// the + 1 makes sure this happens even if the final size
					// is miscalculated, maybe caused by roundings (ie. images)
					if ((bounds.getWidth() <= dr.getWidthThreshold() + 1)
							&& (startBoundingBoxState.getRectangle().getWidth()
									- startBoundingBoxState.getWidth(i)) != 0) {
						double ratioX = (bbWidth - bounds.getWidth())
								/ (startBoundingBoxState.getRectangle()
										.getWidth()
										- startBoundingBoxState.getWidth(i));
						newMinX = startBoundingBoxState.getRatios(i)[0]
								* startBoundingBoxState.getRectangle()
										.getWidth()
								* ratioX;
					}
					if ((bounds.getHeight() <= dr.getHeightThreshold() + 1)
							&& (startBoundingBoxState.getRectangle().getHeight()
									- startBoundingBoxState
											.getHeight(i)) != 0) {
						double ratioY = (bbHeight - bounds.getHeight())
								/ (startBoundingBoxState.getRectangle()
										.getHeight()
										- startBoundingBoxState.getHeight(i));
						newMinY = startBoundingBoxState.getRatios(i)[2]
								* startBoundingBoxState.getRectangle()
										.getHeight()
								* ratioY;
					}
					// calculate the difference between the new and old
					// positions (minX) and then apply translate
					double dx = startBoundingBoxState.lastThresholdX ? 0
							: (newMinX + bbMinX - bounds.getMinX()),
							dy = startBoundingBoxState.lastThresholdY ? 0
									: (newMinY + bbMinY - bounds.getMinY());
					if (geo.isTranslateable() && (dx != 0 || dy != 0)) {
						((Translateable) geo).translate(new Coords(
								dx / view.getXscale(), -dy / view.getYscale()));
					}
				}
			}
			// last update for drawable
			dr.update();
		}
		startBoundingBoxState.lastThresholdX = thresholdXReached;
		startBoundingBoxState.lastThresholdY = thresholdYReached;
		view.repaintView();
	}

	/**
	 * @return distance from the point the mouse was pressed
	 **/
	private GPoint2D getMouseDistance(AbstractEvent event,
			EuclidianBoundingBoxHandler handler) {
		double distX = event.getX() - startPosition.getX();
		double distY = event.getY() - startPosition.getY();
		switch (handler) {
		case TOP_LEFT:
		case BOTTOM_RIGHT:
			distY = distX / startBoundingBoxState.getWidthHeightRatio();
			break;
		case TOP_RIGHT:
		case BOTTOM_LEFT:
			distY = -distX / startBoundingBoxState.getWidthHeightRatio();
			break;
		default:
			break;
		}
		return AwtFactory.getPrototype().newPoint2D(distX, distY);
	}

	/**
	 * handle resize for segments
	 **/
	private void handleResizeForSegment(GeoSegment seg, Drawable dr,
			double bbMinX, double bbMinY, double newMinX, double newMaxX,
			double newMinY, double newMaxY) {
		double dStartX = 0, dEndX = 0, dStartY = 0, dEndY = 0;
		// thresholds not reached
		if (!startBoundingBoxState.lastThresholdX) {
			if (seg.getEndPoint().getX() - seg.getStartPoint().getX() > 0) {
				dStartX = newMinX + bbMinX - dr.getBounds().getMinX();
				dEndX = newMaxX + bbMinX - dr.getBounds().getMaxX();
			} else {
				dEndX = newMinX + bbMinX - dr.getBounds().getMinX();
				dStartX = newMaxX + bbMinX - dr.getBounds().getMaxX();
			}
		}
		if (!startBoundingBoxState.lastThresholdY) {
			if (seg.getEndPoint().getY() - seg.getStartPoint().getY() < 0) {
				dStartY = newMinY + bbMinY - dr.getBounds().getMinY();
				dEndY = newMaxY + bbMinY - dr.getBounds().getMaxY();
			} else {
				dEndY = newMinY + bbMinY - dr.getBounds().getMinY();
				dStartY = newMaxY + bbMinY - dr.getBounds().getMaxY();
			}
		}
		if (dStartX != 0 || dStartY != 0) {
			seg.getStartPoint().translate(new Coords(dStartX / view.getXscale(),
					-dStartY / view.getYscale()));
		}
		if (dEndX != 0 || dEndY != 0) {
			seg.getEndPoint().translate(new Coords(dEndX / view.getXscale(),
					-dEndY / view.getYscale()));
		}
	}

	/**
	 * Sends the videos to background.
	 *
	 */
	public void widgetsToBackground() {
		if (app.getVideoManager() != null) {
			app.getVideoManager().backgroundAll();
		}
		if (app.getEmbedManager() != null) {
			app.getEmbedManager().backgroundAll();
		}
		lastVideo = null;
	}

	private void moveView() {
		snapMoveView();
	}

	protected void snapMoveView() {
		snapController.touchMoved(mouseLoc);

		GPoint delta = snapController.getDeltaPoint();
		view.setCoordSystemFromMouseMove(delta.x, delta.y, MOVE_VIEW);
	}

	protected void scaleXAxis(boolean repaint) {
		if (repaint) {
			if (temporaryMode) {
				view.setCursor(EuclidianCursor.RESIZE_X);
			}

			setScaleAxis(view.getXZero(), view.getXmin(), view.getXmax(),
					view.getWidth(), mouseLoc.x, xTemp);

			view.setCoordSystem(newZero, view.getYZero(), newScale,
					view.getYscale());
		}
	}

	protected void scaleYAxis(boolean repaint) {
		if (repaint) {
			if (temporaryMode) {
				view.setCursor(EuclidianCursor.RESIZE_Y);
			}

			// value have to be swapped due to y goes down on screen
			setScaleAxis(view.getYZero(), view.getYmax(), view.getYmin(),
					view.getHeight(), mouseLoc.y, yTemp);
			newScale *= -1;

			view.setCoordSystem(view.getXZero(), newZero, view.getXscale(),
					newScale);
		}
	}

	/**
	 * Scales the z-axis
	 *
	 * @param repaint
	 *            whether to repaint afterwards
	 */
	protected void scaleZAxis(boolean repaint) {
		// not needed in 2D
	}

	final protected void setScaleAxis(double viewZero, double viewMin,
			double viewMax, int viewSize, int mouse, double tmp) {
		// check if zero is on the screen
		double zero = viewZero;
		double zeroRW = 0;
		newZero = zero;
		if (zero < 0) {
			zero = 0;
			zeroRW = viewMin;
		} else if (zero > viewSize) {
			zero = viewSize;
			zeroRW = viewMax;
		}

		// take care when we get close to the origin
		int newMouse = mouse;
		if (Math.abs(mouse - zero) < MIN_MOUSE_MOVE_FOR_AXIS_SCALE) {
			newMouse = (int) Math
					.round(mouse > zero ? zero + MIN_MOUSE_MOVE_FOR_AXIS_SCALE
							: zero - MIN_MOUSE_MOVE_FOR_AXIS_SCALE);
		}
		newScale = (newMouse - zero) / (tmp - zeroRW);

		// move zero if off screen
		if (newZero < 0) {
			newZero = -zeroRW * newScale;
		} else if (newZero > viewSize) {
			newZero = viewSize - zeroRW * newScale;
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
		case EuclidianConstants.MODE_ZOOM_IN:
			return true;
		// move objects
		case EuclidianConstants.MODE_MOVE:
			return moveMode == MOVE_NONE && isAltDown();

		case EuclidianConstants.MODE_SELECT_MOW:
			return moveMode == MOVE_NONE;

		case EuclidianConstants.MODE_SELECT:
			return true;

		// move rotate objects
		case EuclidianConstants.MODE_MOVE_ROTATE:
			return selPoints() > 0; // need rotation center

		// object selection mode
		case EuclidianConstants.MODE_SELECTION_LISTENER:
			if (app.getCurrentSelectionListener() == null) {
				return false;
			}

			return !app.isUsingFullGui() || app.getGuiManager() == null
					|| !app.getGuiManager().isInputFieldSelectionListener();

		// transformations
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			return allowSelectionRectangleForTranslateByVector;

		case EuclidianConstants.MODE_DILATE_FROM_POINT:
		case EuclidianConstants.MODE_MIRROR_AT_POINT:
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE:
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
		case EuclidianConstants.MODE_FITLINE:
		case EuclidianConstants.MODE_CREATE_LIST:
		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
		case EuclidianConstants.MODE_RELATION:
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
		// ensure no wrong state due to something went wrong
		lastSelectionPressResult = SelectionToolPressResult.DEFAULT;
		// reset
		isMultiResize = false;
		startBoundingBoxState = null;
		// set handler
		if (view.getHitHandler() == EuclidianBoundingBoxHandler.UNDEFINED
				&& view.getBoundingBox() != null) {
			view.setHitHandler(view.getBoundingBox().getHitHandler(e.getX(),
					e.getY(), app.getCapturingThreshold(e.getType())));
		}
		// fix for meta-click to work on Mac/Linux
		if (app.isControlDown(e)) {
			return;
		}
		// move label?
		// warning: ensure that view.setLabelHitNeedsRefresh() is called e.g. at
		// EuclidianController.wrapMousePressed() start
		GeoElement geo = view.getLabelHitCheckRefresh(mouseLoc, e.getType());
		if (geo != null) {
			moveMode = MOVE_LABEL;
			movedLabelGeoElement = geo;
			oldLoc.setLocation(geo.labelOffsetX, geo.labelOffsetY);
			startLoc = mouseLoc;
			setDragCursor();
			return;
		}
		// bounding box handler hit
		if (view.getHitHandler() != EuclidianBoundingBoxHandler.UNDEFINED) {
			Drawable d = view.getBoundingBoxHandlerHit(mouseLoc, e.getType());
			if (d != null) {
				setBoundingBoxCursor(d);
				setResizedShape(d);
			} else if (isMultiSelection() && wasBoundingBoxHit) {
				isMultiResize = true;
				startBoundingBoxState = new BoundingBoxResizeState(
						view.getBoundingBox().getRectangle(),
						selection.getSelectedGeos(), view);
			}
		}
		// find and set movedGeoElement
		setViewHits(e.getType());
		Hits viewHits = view.getHits();

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
		Hits topHits = moveableList.getTopHits();

		// make sure that eg line takes precedence over a polygon (in the same
		// layer)
		topHits.removePolygonsIfNotOnlyCS2D();

		ArrayList<GeoElement> selGeos = getAppSelectedGeos();
		removeAxes(selGeos);
		// if object was chosen before, take it now!
		if (((selGeos.size() == 1) && !topHits.isEmpty()
				&& topHits.contains(selGeos.get(0)))
				|| selGeos.size() == 1 && wasBoundingBoxHit) {
			// object was chosen before: take it
			geo = selGeos.get(0);
		} else {
			// choose out of hits
			// testing needed - see GGB-1982
			geo = chooseGeo(topHits, true);

			if (selGeos.contains(geo)) {
				if (app.has(Feature.SELECT_TOOL_NEW_BEHAVIOUR)
						&& mode == EuclidianConstants.MODE_SELECT) {
					lastSelectionPressResult = SelectionToolPressResult.REMOVE;
					lastSelectionToolGeoToRemove = geo;
				} else if (mode == EuclidianConstants.MODE_SELECT_MOW) {
					// actually it's a geo to select but with the same reason
					lastSelectionToolGeoToRemove = geo;
				}
			} else {
				if (app.has(Feature.SELECT_TOOL_NEW_BEHAVIOUR)
						&& mode == EuclidianConstants.MODE_SELECT) {
					if (geo == null) {
						lastSelectionPressResult = SelectionToolPressResult.EMPTY;
					} else {
						lastSelectionPressResult = SelectionToolPressResult.ADD;
						selection.addSelectedGeo(geo, true, true);
					}
				} else if (mode == EuclidianConstants.MODE_SELECT_MOW) {
					if (!wasBoundingBoxHit) {
						if (geo == null) {
							lastSelectionPressResult = SelectionToolPressResult.EMPTY;
						} else {
							if (view.getSelectionRectangle() == null
									&& !e.isRightClick()) {
								selection.clearSelectedGeos(geo == null, false);
								selection.updateSelection(false);
								selection.addSelectedGeo(geo, true, true);
							}
						}
					}
				} else if (mode == EuclidianConstants.MODE_MOVE
						&& isSpecialPreviewPointFound(topHits)) {
					previewPointHits = getPreviewSpecialPointHits(topHits);
				} else {
					// repaint done next step, no update for properties view (will
					// display ev properties)
					selection.clearSelectedGeos(geo == null, false);
					selection.updateSelection(false);
					selection.addSelectedGeo(geo, true, true);
				}
			}
		}

		if (geo != null && view.getDrawableFor(geo) != null
				&& !wasBoundingBoxHit) {
			view.setBoundingBox(
					((Drawable) view.getDrawableFor(geo)).getBoundingBox());
			view.repaintView();
		}

		Hits th = viewHits.getTopHits();
		// make sure dragging a fixed eg button triggers the scripts
		// important for tablets, IWBs

		if (geo == null && th.size() > 0) {
			geo = th.get(0);

			if (geo.isLocked() && !isMoveButtonExpected(geo)
					&& !isMoveTextFieldExpected(geo)) {
				runScriptsIfNeeded(geo);
				moveMode = MOVE_NONE;
				resetMovedGeoPoint();
				return;

			}
		}

		if ((geo != null) && (!geo.isLocked() || isMoveButtonExpected(geo)
				|| isMoveTextFieldExpected(geo))) {
			moveModeSelectionHandled = true;
		} else if (!wasBoundingBoxHit) {
			// no geo clicked at
			moveMode = MOVE_NONE;
			resetMovedGeoPoint();
			return;
		}

		handleMovedElement(geo, selGeos.size() > 1, e.getType());

		view.repaintView();
	}

	/**
	 * @param hits
	 *         Contains the GeoElements where the user clicked/tapped
	 * @return whether there is any GeoPoint in the hits which is a preview Special point or not
	 */
	private boolean isSpecialPreviewPointFound(Hits hits) {
		if (!app.getConfig().hasPreviewPoints()) {
			return false;
		}

		List<GeoElement> selectedPreviewPoints = app.getSpecialPointsManager()
				.getSelectedPreviewPoints();

		if (selectedPreviewPoints != null) {
			for (GeoElement hit : hits) {
				if (selectedPreviewPoints.contains(hit)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param hits
	 *         Contains the GeoElements where the user clicked/tapped
	 * @return List of the hitted preview special points
	 */
	private ArrayList<GeoElement> getPreviewSpecialPointHits(Hits hits) {
		List<GeoElement> selectedPreviewPoints = app.getSpecialPointsManager()
				.getSelectedPreviewPoints();
		ArrayList<GeoElement> previewHits = new ArrayList<>();
		if (selectedPreviewPoints != null) {
			for (GeoElement hit : hits) {
				if (selectedPreviewPoints.contains(hit)) {
					previewHits.add(hit);
				}
			}
		}
		return previewHits;
	}

	/**
	 * Hides the dynamic stylebar.
	 */
	public void hideDynamicStylebar() {
		// Floating stylebar not supported
	}

	public void onArrowKeyTyped() {
		hideDynamicStylebar();
	}

	public void cancelDrag() {
		moveMode = MOVE_NONE;
	}

	protected boolean shouldCancelDrag() {
		if (System.currentTimeMillis()
				< draggingDelay + lastMousePressedTime) {
			// we wait at least DRAGGING_DELAY (100ms) before starting drag
			// used for interactive boards
			return !EuclidianView.isPenMode(mode);
		}
		return false;
	}

	protected boolean shouldSetToFreehandMode() {
		return (isDraggingBeyondThreshold() && pen != null && !penMode(mode)
				&& freehandModePrepared);
	}

	/**
	 * @param event
	 *            pointer drag event
	 * @param startCapture
	 *            whether to start capturing events (HTML5)
	 */
	public void wrapMouseDragged(AbstractEvent event, boolean startCapture) {
		if (pen != null && !penDragged && freehandModePrepared) {
			getPen().handleMouseDraggedForPenMode(event);
		}

		if (getTextController() != null && getTextController().isEditing()) {
			return;
		}
		if (view.hasDynamicStyleBar()
				&& ((mode == EuclidianConstants.MODE_SELECT_MOW
						&& !event.isRightClick())
						|| mode != EuclidianConstants.MODE_SELECT_MOW)) {
			this.hideDynamicStylebar();
		}

		lastSelectionToolGeoToRemove = null;

		if (shapeMode(mode) && !app.isRightClick(event)) {
			if (getResizedShape() == null) {
				Drawable d = view.getBoundingBoxHandlerHit(mouseLoc, null);
				if (d != null && view
						.getHitHandler() != EuclidianBoundingBoxHandler.UNDEFINED) {
					if (view.getBoundingBox() != null && view.getBoundingBox()
							.equals(d.getBoundingBox())) {
						setBoundingBoxCursor(d);
						setResizedShape(d);
					}
				} else {
					setMouseLocation(event);
					getShapeMode().handleMouseDraggedForShapeMode(event);
					return;
				}
			}
		}
		// preview shape for mow text tool
		if (mode == EuclidianConstants.MODE_MEDIA_TEXT
				&& app.has(Feature.MOW_TEXT_TOOL)) {
			view.setBoundingBox(null);
			updateTextRectangle(event);
			view.setShapeRectangle(textRectangleShape);
			view.repaintView();
		}

		if (!shouldCancelDrag()) {
			if (shouldSetToFreehandMode()) {
				oldMode = mode;
				setModeToFreehand();
			}
			// Set capture events only if the mouse is actually down,
			// because we need to release the capture on mouse up.
			if (startCapture) {
				startCapture(event);
			}
			wrapMouseDraggedND(event, startCapture);
		}
		if (movedGeoPoint != null && isModeCreatingObjectsByDrag()) {
			// nothing was dragged
			wrapMouseMoved(event);
		}

		if (view.getPreviewDrawable() != null
				&& event.getType() == PointerEventType.TOUCH) {
			this.view.updatePreviewableForProcessMode();
		}
	}

	private void updateTextRectangle(AbstractEvent event) {
		if (textRectangleShape == null) {
			textRectangleShape = AwtFactory.getPrototype().newRectangle();
		}

		int width = event.getX() - startPosition.getX();
		int height = event.getY() - startPosition.getY();

		if (height >= 0) {
			if (width >= 0) {
				textRectangleShape.setBounds(startPosition.getX(),
						startPosition.getY(), width, height);
			} else { // width < 0
				textRectangleShape.setBounds(startPosition.getX() + width,
						startPosition.getY(), -width, height);
			}
		} else { // height < 0
			if (width >= 0) {
				textRectangleShape.setBounds(startPosition.getX(),
						startPosition.getY() + height, width, -height);
			} else { // width < 0
				textRectangleShape.setBounds(startPosition.getX() + width,
						startPosition.getY() + height, -width, -height);
			}
		}
	}

	/**
	 * @param event
	 *            use its source to start capturing
	 */
	protected void startCapture(AbstractEvent event) {
		// for web
	}

	/**
	 * Handle pointer drag event.
	 * 
	 * @param event
	 *            pointer drag event
	 * @param startCapture
	 *            whether to start event capture in HTML5
	 */
	public void wrapMouseDraggedND(AbstractEvent event, boolean startCapture) {
		// kill view moving when animation button pressed
		if (shouldCancelDrag() || this.animationButtonPressed) {
			return;
		}
		scriptsHaveRun = false;

		if (isTextfieldHasFocus() && moveMode != MOVE_BUTTON) {
			return;
		}
		if (circleRadiusDrag(event)) {
			return;
		}
		if (pressedButton != null && !app.showView(App.VIEW_PROPERTIES)) {
			pressedButton.setDraggedOrContext(true);
		}
		if (penMode(mode)) {
			penDragged = true;
			getPen().handleMouseDraggedForPenMode(event);
			return;
		}

		DrawDropDownList dl = view.getOpenedComboBox();
		if (dl != null && isDraggingBeyondThreshold()) {
			if (dl.onDrag(event.getX(), event.getY())) {
				return;
			}
		}
		clearJustCreatedGeos();

		if (!draggingBeyondThreshold && isDraggingBeyondThreshold()) {
			draggingBeyondThreshold = true;
		}
		if (!draggingOccured) {
			draggingOccured = true;

			// make sure dragging triggers reset/play/pause
			// needed for tablets
			if (hitResetIcon()) {
				app.reset();
				return;
			} else if (view.hitAnimationButton(event.getX(), event.getY())) {
				this.animationButtonPressed = true;
				return;
			}

			if ((mode == EuclidianConstants.MODE_TRANSLATE_BY_VECTOR)
					&& (selGeos() == 0)) {
				translateHitsByVector(event.getType());
			}
			// Michael Borcherds 2007-10-07 allow right mouse button to drag
			// points
			// mathieu : also if it's mode point, we can drag the point
			if ((app.isRightClick(event)
					|| (mode == EuclidianConstants.MODE_POINT)
					|| (mode == EuclidianConstants.MODE_COMPLEX_NUMBER)
					|| (mode == EuclidianConstants.MODE_POINT_ON_OBJECT)
					|| (mode == EuclidianConstants.MODE_SLIDER)
					|| (mode == EuclidianConstants.MODE_BUTTON_ACTION)
					|| (mode == EuclidianConstants.MODE_TEXTFIELD_ACTION)
					|| (mode == EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX)
					|| (mode == EuclidianConstants.MODE_TEXT))
					&& mode != EuclidianConstants.MODE_SELECT_MOW) {
				setViewHits(event.getType());
				GeoElement geo0 = null;
				Hits hits0 = view.getHits();
				if (!hits0.isEmpty()) {
					geo0 = hits0.get(0);
				}
				if (!app.showToolBar() && geo0 != null
						&& (geo0.isGeoInputBox() || geo0.isGeoBoolean()
								|| geo0.isGeoButton() || (geo0.isGeoNumeric()
										&& ((GeoNumeric) geo0).isSlider()))) {
					draggingOccured = false;
					return;
				}
				// make sure slider tool drags only sliders, not other object
				// types
				if (mode == EuclidianConstants.MODE_SLIDER) {
					if (view.getHits().size() != 1) {
						filterHits(new Inspecting() {

							@Override
							public boolean check(ExpressionValue v) {
								return v instanceof GeoNumeric
										&& ((GeoNumeric) v).isSlider();
							}

						});
					}

					if (view.getHits().size() > 0
							&& !(view.getHits().get(0) instanceof GeoNumeric)) {
						return;
					}
				} else if ((mode == EuclidianConstants.MODE_BUTTON_ACTION)
						|| (mode == EuclidianConstants.MODE_TEXTFIELD_ACTION)) {
					if (view.getHits().size() != 1) {
						filterHits(new Inspecting() {

							@Override
							public boolean check(ExpressionValue v) {
								return v instanceof GeoButton;
							}
						});

					}

					if (view.getHits().size() > 0
							&& !(view.getHits().get(0) instanceof GeoButton)) {
						return;
					}
				} else if (mode == EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX) {
					if (view.getHits().size() != 1) {
						filterHits(new Inspecting() {

							@Override
							public boolean check(ExpressionValue v) {
								return v instanceof GeoBoolean;
							}
						});

					}

					if (!(view.getHits().size() > 0
							&& view.getHits().get(0) instanceof GeoBoolean)) {
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
					setTempMode(EuclidianConstants.MODE_MOVE);
					handleMousePressedForMoveMode(event, true);

					// make sure that dragging doesn't deselect the geos
					dontClearSelection = true;

					return;
				}
			}
			// Slider can be moved too on whiteboard without using right mouse
			// button or selecting slider tool.
			else if (app.isWhiteboardActive()) {
				setViewHits(event.getType());
				Hits hits0 = view.getHits();
				if (!hits0.isEmpty()) {
					GeoElement geo0 = hits0.get(0);
					if (geo0.isGeoNumeric() && ((GeoNumeric) geo0).isSlider()
							&& viewHasHitsForMouseDragged()) {
						setTempMode(EuclidianConstants.MODE_MOVE);
						handleMousePressedForMoveMode(event, true);
						// make sure that dragging doesn't deselect the geos
						dontClearSelection = true;
						return;
					}
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
			if ((!temporaryMode) || (view.getHits().size() == 0)
					|| !view.getHits().get(0).isMoveable(view)
					|| (!view.getHits().get(0).isGeoPoint()
							&& view.getHits().get(0).hasDrawable3D())) {
				if (processRotate3DView()) { // in 2D view, return false
					return;
				}
			}
		}

		// dragging eg a fixed point shouldn't start the selection rectangle
		if (view.getHits().isEmpty()) {
			boolean boundingBoxHit = wasBoundingBoxHit
					|| (moveMode == MOVE_MULTIPLE_OBJECTS);

			if (app.isSelectionRectangleAllowed()
					&& ((app.isRightClick(event)
							|| app.getMode() == EuclidianConstants.MODE_SELECT)
							|| (allowSelectionRectangle() && !shapeDragged))
					&& !temporaryMode
					&& ((!app.isRightClick(event) && app
							.getMode() == EuclidianConstants.MODE_SELECT_MOW)
							|| app.getMode() != EuclidianConstants.MODE_SELECT_MOW)
					&& !boundingBoxHit) {
				// Michael Borcherds 2007-10-07
				// set zoom rectangle's size
				// right-drag: zoom
				// Shift-right-drag: zoom without preserving aspect ratio

				// clear previous selection before starting a new selection
				// rectangle
				if (mode == EuclidianConstants.MODE_SELECT_MOW
						&& selection.selectedGeosSize() > 0) {
					clearSelections();
				}

				updateSelectionRectangle(event.isShiftDown()
						|| mode == EuclidianConstants.MODE_ZOOM_IN
						|| mode == EuclidianConstants.MODE_ZOOM_OUT);

				view.repaintView();
				return;
			}
		}

		updatePreviewableForMouseDragged();

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
				int steps = Math.min((int) (1.0 / factor),
						MAX_CONTINUITY_STEPS);
				int mlocx = mouseLoc.x;
				int mlocy = mouseLoc.y;

				for (int i = 1; i <= steps; i++) {
					mouseLoc.x = (int) Math.round(lastMouseLoc.x + (i * dx));
					mouseLoc.y = (int) Math.round(lastMouseLoc.y + (i * dy));
					calcRWcoords();

					handleMouseDragged(false, event, startCapture);
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
		handleMouseDragged(true, event, startCapture);
	}

	protected void updatePreviewableForMouseDragged() {
		if (view.getPreviewDrawable() != null) {
			view.getPreviewDrawable().updateMousePos(
					view.toRealWorldCoordX(mouseLoc.x),
					view.toRealWorldCoordY(mouseLoc.y));
		}
	}

	private void translateHitsByVector(PointerEventType type) {
		setViewHits(type);

		Hits hits = view.getHits().getTopHits();
		if (hits.size() == 0) {
			return;
		}
		GeoElement topHit = hits.get(0);

		if (topHit.isGeoVector()) {
			if ((topHit.getParentAlgorithm() instanceof AlgoVector)) { // Vector[A,B]
				AlgoVector algo = (AlgoVector) topHit.getParentAlgorithm();
				GeoPointND p = algo.getP();
				GeoPointND q = algo.getQ();

				GeoVector vec = getAlgoDispatcher().vector(0, 0);

				vec.setEuclidianVisible(false);
				vec.setAuxiliaryObject(true);
				vec.setLabel(null);
				GeoElement[] pp = getAlgoDispatcher().translate(null, p, vec);
				GeoElement[] qq = getAlgoDispatcher().translate(null, q, vec);
				AlgoVector newVecAlgo = new AlgoVector(kernel.getConstruction(),
						(GeoPointND) pp[0], (GeoPointND) qq[0]);
				setTranslateStart(topHit, vec);

				// make sure vector looks the same when translated
				pp[0].setEuclidianVisible(p.isEuclidianVisible());
				qq[0].update();
				qq[0].setEuclidianVisible(q.isEuclidianVisible());
				qq[0].update();
				newVecAlgo.getGeoElements()[0]
						.setVisualStyleForTransformations(topHit);
				newVecAlgo.getGeoElements()[0].setLabel(null);
				app.setMode(EuclidianConstants.MODE_MOVE);
				movedGeoVector = vec;
				moveMode = MOVE_VECTOR_NO_GRID;
				return;
			}
			movedGeoPoint = new GeoPoint(kernel.getConstruction(), null, 0, 0,
					0);

			GeoPointND p = (GeoPointND) getAlgoDispatcher().translateND(null,
					movedGeoPoint, (GeoVectorND) topHit)[0];

			GeoElement newVecGeo = getAlgoDispatcher().vectorND(null,
					movedGeoPoint, p);

			// make sure vector looks the same when translated
			movedGeoPoint.setEuclidianVisible(false);
			movedGeoPoint.update();
			p.setEuclidianVisible(false);
			p.update();
			newVecGeo
					.setVisualStyleForTransformations(topHit);
			newVecGeo.setLabel(null);
			moveMode = MOVE_POINT;
		}

		if (topHit.isTranslateable() || topHit instanceof GeoPoly) {
			GeoVectorND vec;
			if (topHit instanceof GeoPoly) {
				// for polygons, we need a labelled vector so that all
				// the vertices move together
				vec = createVectorForTranslation(null);
				vec.setEuclidianVisible(false);
				vec.setAuxiliaryObject(true);
			} else {
				vec = createVectorForTranslation();
			}
			GeoElement[] ret = getAlgoDispatcher().translateND(null, topHit,
					vec);
			setTranslateStart(topHit, vec);

			app.setMode(EuclidianConstants.MODE_MOVE, ModeSetter.TOOLBAR);
			movedGeoVector = vec;
			moveMode = MOVE_VECTOR_NO_GRID;
			// set moved geo for store undo on mouse release
			movedGeoElement = ret[0];
		}
	}

	protected GeoVectorND createVectorForTranslation() {
		return getAlgoDispatcher().vector();
	}

	protected GeoVectorND createVectorForTranslation(String label) {
		return getAlgoDispatcher().vector(label);
	}

	/**
	 * set translate start infos
	 *
	 * @param geo
	 *            needed in 3D
	 * @param vec
	 *            needed in 3D
	 */
	protected void setTranslateStart(GeoElement geo, GeoVectorND vec) {
		transformCoordsOffset[0] = xRW;
		transformCoordsOffset[1] = yRW;
	}

	/**
	 * @return true if a view button has been pressed (see 3D)
	 */
	protected boolean handleMousePressedForViewButtons() {
		return false;
	}

	/**
	 * right-press the mouse makes start 3D rotation
	 *
	 * @param event
	 *            used by actual 3D controller
	 */
	protected void processRightPressFor3D(AbstractEvent event) {
		// 3D only
	}

	protected void createNewPointForModePoint(Hits hits, boolean complexPoint) {
		if ((mode == EuclidianConstants.MODE_POINT)
				|| (mode == EuclidianConstants.MODE_COMPLEX_NUMBER)) {
			// remove polygons: point inside a polygon is created free, as in
			// v3.2
			hits.removeAllPolygons();
			hits.removeConicsHittedOnFilling();
			createNewPoint(hits, true, false, true, true, complexPoint);
		} else { // if mode==EuclidianView.MODE_POINT_ON_OBJECT, point can be in
			// a region
			createNewPoint(hits, true, true, true, true, complexPoint);
		}
	}

	protected void createNewPointForModeOther(Hits hits) {
		createNewPoint(hits, true, false, true, true, false);
	}

	protected boolean circleRadiusDrag(AbstractEvent event) {
		if (firstSelectedPoint != null
				&& this.mode == EuclidianConstants.MODE_CIRCLE_POINT_RADIUS) {
			// prevent further processing
			if (!withinPointSelectionDistance(startPosition, event)) {
				// update the preview circle
				wrapMouseMoved(event);
			}
			return true;
		}
		return false;
	}

	protected void handleMousePressedForRotateMode(PointerEventType type) {
		GeoElement geo;
		Hits hits;

		// we need the center of the rotation
		if (rotationCenter == null) {
			setViewHits(type);
			rotationCenter = (GeoPoint) chooseGeo(
					view.getHits().getHits(TestGeo.GEOPOINT, tempArrayList), true);
			selection.addSelectedGeo(rotationCenter);
			moveMode = MOVE_NONE;
		} else {
			setViewHits(type);
			hits = view.getHits();
			hits.removePolygons();
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

				// init values needed for rotation
				rotationLastAngle = Math.atan2(yRW - rotationCenter.inhomY,
						xRW - rotationCenter.inhomX);
				moveMode = MOVE_ROTATE;
			} else {
				moveMode = MOVE_NONE;
			}
		}
	}

	protected void setMoveModeIfAxis(Object hit) {
		if (hit == kernel.getXAxis()) {
			moveMode = MOVE_X_AXIS;
		}
		if (hit == kernel.getYAxis()) {
			moveMode = MOVE_Y_AXIS;
		}
	}

	protected final void mousePressedTranslatedView(PointerEventType type,
			boolean shiftOrMeta) {
		Hits hits;

		// check if axis is hit
		setViewHits(type);
		hits = view.getHits();
		hits.removePolygons();

		moveMode = MOVE_VIEW;
		if (!hits.isEmpty() && moveAxesPossible(shiftOrMeta)) {
			for (Object hit : hits) {
				setMoveModeIfAxis(hit);
			}
		}

		startLoc = mouseLoc;
		snapController.touchStarted(startLoc);

		setDragCursorIfMoveView();

		view.rememberOrigins();
		xTemp = xRW;
		yTemp = yRW;
		view.setShowAxesRatio(
				(moveMode == MOVE_X_AXIS) || (moveMode == MOVE_Y_AXIS));
	}

	protected boolean moveAxesPossible(boolean shiftOrMeta) {
		return !view.isLockedAxesRatio() && view.isZoomable()
				&& (shiftOrMeta || !isTemporaryMode());
	}

	protected void setDragCursorIfMoveView() {
		if (moveMode == MOVE_VIEW) {
			setDragCursor();
		}
	}

	private void setDragCursor() {
		view.setCursor(EuclidianCursor.DRAG);
	}

	protected void switchModeForMousePressedND(AbstractEvent e) {
		PointerEventType type = e.getType();
		Hits hits;
		// TODO we shall never get mode > 1000 here
		if (mode > 1000) {
			app.setMode(EuclidianConstants.MODE_MOVE);
		}
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

			if (hits.size() != 1 || !hits.get(0).isGeoPolygon()) {
				createNewPoint(hits, false, false, false, false, false);
			}

			break;

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			if (!allowSelectionRectangleForTranslateByVector) {
				setViewHits(type);
				hits = view.getHits();
				// remove polygons even if just one is selected
				hits.removeAllPolygons();

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
			if (hits.size() <= 0 || !hits.get(0).isGeoLine()) {
				createNewPoint(hits, false, false, false, false, false);
			}

			break;
		case EuclidianConstants.MODE_LINE_BISECTOR:
		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
		case EuclidianConstants.MODE_TANGENTS:
		case EuclidianConstants.MODE_POLAR_DIAMETER:
			break;

		case EuclidianConstants.MODE_COMPASSES: // Michael Borcherds 2008-03-13
			if (type == PointerEventType.TOUCH) {
				view.setPreview(null);
			}

			setViewHits(type);
			hits = view.getHits();
			hits.removePolygons();
			if (selConics() > 0 || selPoints() > 1 || selSegments() > 0) {
				createNewPoint(hits, true, true, true);
			}
			break;

		case EuclidianConstants.MODE_ANGLE:
			setViewHits(type);
			hits = view.getHits().getTopHits();

			hits.removeImages();

			// check if we got a polygon
			if (hits.isEmpty()) {
				createNewPoint(hits, false, false, true);
			}
			break;

		case EuclidianConstants.MODE_ANGLE_FIXED:
			setViewHits(type);
			hits = view.getHits();
			hits.removePolygons();
			if (hits.isEmpty() || (!hits.get(0).isGeoSegment()
					&& !hits.get(0).isGeoConic())) {
				createNewPoint(hits, false, false, true);
			}
			break;

		case EuclidianConstants.MODE_MIDPOINT:
			setViewHits(type);
			hits = view.getHits();
			hits.removePolygons();
			if (hits.isEmpty() || (!hits.get(0).isGeoSegment()
					&& !hits.get(0).isGeoConic()
					&& !hits.get(0).isGeoPolygon())) {
				createNewPoint(hits, false, false, true);
			}
			break;

		case EuclidianConstants.MODE_MOVE_ROTATE:
			handleMousePressedForRotateMode(type);
			break;

		// move an object
		case EuclidianConstants.MODE_MOVE:
		case EuclidianConstants.MODE_SELECT:
		case EuclidianConstants.MODE_SELECT_MOW:
			handleMousePressedForMoveMode(e, false);
			break;

		// move drawing pad or axis
		case EuclidianConstants.MODE_TRANSLATEVIEW:

			mousePressedTranslatedView(type, specialMoveEvent(e));

			break;

		case EuclidianConstants.MODE_ATTACH_DETACH:
			GeoPointND p = (GeoPointND) this.view.getHits()
					.getFirstHit(TestGeo.GEOPOINTND);
			if (p != null && p.isMoveable()) {
				// set movedGeoPoint etc.
				handleMovedElement(p.toGeoElement(), false,
						PointerEventType.MOUSE);
				this.moveMode = MOVE_ATTACH_DETACH;
			}
			break;

		case EuclidianConstants.MODE_DELETE:
			getDeleteMode().mousePressed(type);
			break;
		case EuclidianConstants.MODE_ERASER:
			getDeleteMode().mousePressed(type);

		default:
			moveMode = MOVE_NONE;
		}
	}

	/**
	 * Handle pointer down event.
	 * 
	 * @param event
	 *            pointer event
	 */
	public void wrapMousePressed(AbstractEvent event) {
		if (view.hasDynamicStyleBar()
				&& ((mode == EuclidianConstants.MODE_SELECT_MOW
						&& !event.isRightClick())
						|| mode != EuclidianConstants.MODE_SELECT_MOW)) {
			this.hideDynamicStylebar();
		}

		app.getAccessibilityManager().setTabOverGeos(true);
		// if we need label hit, it will be recomputed
		view.setLabelHitNeedsRefresh();

		long last = event.getType() == PointerEventType.MOUSE
				? this.lastMouseRelease : this.lastTouchRelease;
		if (last + EuclidianConstants.DOUBLE_CLICK_DELAY > System
				.currentTimeMillis() && lastMouseUpLoc != null
				&& MyMath.length(event.getX() - lastMouseUpLoc.x,
						event.getY() - lastMouseUpLoc.y) <= 3) {
			this.doubleClickStarted = true;
		}

		setMouseLocation(event);
		this.setViewHits(event.getType());

		setMoveModeForFurnitures();

		AutoCompleteTextField tf = view.getTextField();
		if (tf != null && tf.hasFocus()) {
			view.requestFocusInWindow();
		}

		if (isSymbolicEditorSelected()) {
			Log.debug("EDITOR IS ATTACHED");
			return;
		}

		altCopy = true;

		DrawDropDownList dl = getComboBoxHit();

		if (!event.isRightClick() && dl != null) {
			clearSelections();
			app.getSelectionManager().addSelectedGeo(dl.geo);
			dl.onMouseDown(event.getX(), event.getY());
			return;
		}

		DrawAudio da = getAudioHit();

		if (!event.isRightClick() && da != null && !isMultiSelection()) {
			clearSelections();
			app.getSelectionManager().addSelectedGeo(da.geo);
			if (da.onMouseDown(event.getX(), event.getY())) {
				return;
			}
		}

		handleVideoPressed(event);
		if (getTextController() != null) {
			getTextController().handleTextPressed();
		}

		lastMousePressedTime = System.currentTimeMillis();

		app.storeUndoInfoIfSetCoordSystemOccured();
		app.maySetCoordSystem();

		scriptsHaveRun = false;

		penDragged = false;
		shapeDragged = false;

		if (app.isUsingFullGui() && app.getGuiManager() != null) {
			// determine parent panel to change focus
			app.getGuiManager().setFocusedPanel(event, false);
			app.getGuiManager().mousePressedForPropertiesView();

			if (view instanceof PlotPanelEuclidianViewInterface) {
				setMode(EuclidianConstants.MODE_MOVE, ModeSetter.TOOLBAR);
			}
		} else if (app.isHTML5Applet()) {
			if (!isComboboxFocused() && !textfieldHasFocus) {
				view.requestFocus();
			}
		}

		if (handleMousePressedForViewButtons()) {
			return;
		}

		Hits hits;

		if (popupJustClosed) {
			popupJustClosed = false;
		} else if (penMode(mode)) {
			setViewHits(event.getType());
			hits = view.getHits();
			hits.removeAllButImages();
			getPen().handleMousePressedForPenMode(event, hits);
			return;
		}
		// check if side of bounding box was hit
		wasBoundingBoxHit = view.getBoundingBox() != null
				&& view.getBoundingBox().hitSideOfBoundingBox(event.getX(),
						event.getY(), app.getCapturingThreshold(event.getType()));

		Drawable d = view.getBoundingBoxHandlerHit(
				new GPoint(event.getX(), event.getY()), event.getType());
		if (EuclidianConstants.isMoveOrSelectionMode(mode)
				|| mode == EuclidianConstants.MODE_MEDIA_TEXT && app.has(Feature.MOW_TEXT_TOOL)) {
			// for now allow only corner handlers
			if (d != null && view
					.getHitHandler() != EuclidianBoundingBoxHandler.UNDEFINED) {
				setBoundingBoxCursor(d);
				setResizedShape(d);
			}
		}

		if (shapeMode(mode) && !app.isRightClick(event)) {
			// no hit or no bounding box, so we have to create
			// shape
			if ((view.getHits().isEmpty()
					&& view.getHitHandler() == EuclidianBoundingBoxHandler.UNDEFINED)
					|| view.getBoundingBox() == null) {
				// clear selection to be able to drag created shape with shape
				// tool
				selection.clearSelectedGeos();
				view.setDefaultShapeStyle();
				getShapeMode().handleMousePressedForShapeMode(event);
			} else {
				if (d != null && view.getBoundingBox() != null
						&& view.getBoundingBox()
								.equals(d.getBoundingBox())) {
					setResizedShape(d);
				} else if (view.getHits().size() == 1
						&& view.getHits().get(0) != null
						&& view.getHits().get(0).isShape()) {
					if (view.getDrawableFor(view.getHits().get(0)) != null
							&& ((Drawable) view
									.getDrawableFor(view.getHits().get(0)))
											.getBoundingBox() == view
													.getBoundingBox()) {
						shapeDragged = true;
						oldShapeMode = mode;
						mode = EuclidianConstants.MODE_MOVE;
					} else {
						selection.clearSelectedGeos();
						view.setDefaultShapeStyle();
						getShapeMode().handleMousePressedForShapeMode(event);
					}
				} else if (selection.getSelectedGeos().size() == 1
						&& selection.getSelectedGeos().get(0).isShape()
						&& view.getDrawableFor(selection.getSelectedGeos().get(0)) != null
						&& ((Drawable) view.getDrawableFor(
								selection.getSelectedGeos().get(0)))
										.getBoundingBox() == view
												.getBoundingBox()) {
					shapeDragged = true;
					oldShapeMode = mode;
					mode = EuclidianConstants.MODE_MOVE;
				// shape hit but not selected
				} else {
					selection.clearSelectedGeos();
					view.setDefaultShapeStyle();
					getShapeMode().handleMousePressedForShapeMode(event);
				}
			}
		}

		this.pressedButton = view.getHitButton(mouseLoc, event.getType());
		if (pressedButton != null) {
			if (!app.showView(App.VIEW_PROPERTIES)) {
				pressedButton.setPressed(true);
				pressedButton.setDraggedOrContext(
						event.isMetaDown() || event.isPopupTrigger());

				if (!event.isRightClick()) {
					runScriptsIfNeeded(pressedButton.getButton());
				}
			} else {
				app.getSelectionManager().clearSelectedGeos();
				app.getSelectionManager()
						.addSelectedGeo(pressedButton.getButton());
			}
		}

		// TODO:repaint?

		transformCoords();

		moveModeSelectionHandled = false;
		draggingOccured = false;
		draggingBeyondThreshold = false;
		view.setSelectionRectangle(null);

		if (mouseLoc != null) {
			selectionStartPoint.setLocation(mouseLoc);
		}

		if (hitResetIcon()
				|| view.hitAnimationButton(event.getX(), event.getY())) {
			// see mouseReleased
			return;
		}

		if (app.isRightClick(event)) {
			// ggb3D - for 3D rotation
			processRightPressFor3D(event);
			return;
		}
		setViewHits(event.getType());
		if (app.isWhiteboardActive()
				&& mode == EuclidianConstants.MODE_TRANSLATEVIEW
				&& !getView().getHits().isEmpty()) {
			app.setMode(EuclidianConstants.MODE_SELECT_MOW);
		}
		if (shallMoveView(event)) {
			// Michael Borcherds 2007-12-08 BEGIN
			// bugfix: couldn't select multiple objects with Ctrl

			hits = view.getHits();
			switchModeForRemovePolygons(hits);
			dontClearSelection = !hits.isEmpty();
			if (hasNoHitsDisablingModeForShallMoveView(hits, event)
					|| needsAxisZoom(hits, event) || specialMoveEvent(event)) {
				temporaryMode = true;
				oldMode = mode; // remember current mode
				if (mayPaste()) { // #5246 make sure we don't switch to
					// translation if we have geos to paste
					if (!view.isAREnabled()) {
						view.setMode(getModeForShallMoveView(event));
					}
				}
				// if over an axis, force the correct cursor to be displayed
				if (view.getHits().hasXAxis() || view.getHits().hasYAxis()) {
					setCursorForTranslateView(view.getHits());
				}
			}

		}
		switchModeForMousePressed(event);
	}

	public boolean isSymbolicEditorSelected() {
		return view.isSymbolicEditorClicked(mouseLoc);
	}

	private void handleVideoPressed(AbstractEvent event) {
		GeoFrame dv = getVideoHit();

		if (dv == null) {
			widgetsToBackground();
		}

		if ((mode != EuclidianConstants.MODE_MOVE
				&& mode != EuclidianConstants.MODE_SELECT_MOW)
				|| isMultiSelection()) {
			return;
		}

		if (!event.isRightClick() && dv != null) {
			clearSelections();
			lastVideo = dv;
		}
	}

	private void moveVideo() {
		if (mode != EuclidianConstants.MODE_MOVE
				&& mode != EuclidianConstants.MODE_SELECT_MOW) {
			return;
		}

		lastVideo = (GeoFrame) movedGeoMedia;
		videoMoved = true;
	}

	private boolean handleVideoReleased() {
		if (lastVideo == null) {
			return false;
		}

		if (videoMoved) {
			lastVideo.setReady();
			videoMoved = false;
			return false;
		}

		if (videoHasError()) {
			return false;
		}

		if (selection.containsSelectedGeo(lastVideo)) {
			if (lastVideo.isReady()) {
				view.setBoundingBox(null);
				view.repaintView();
			}
			if (lastVideo instanceof GeoVideo) {
				app.getVideoManager().play((GeoVideo) lastVideo);
			}
			if (lastVideo instanceof GeoEmbed) {
				app.getEmbedManager().play((GeoEmbed) lastVideo);
			}
			return true;
		}
		return false;
	}

	private boolean videoHasError() {
		return lastVideo instanceof GeoVideo
				&& app.getVideoManager().isPlayerOffline((GeoVideo) lastVideo);

		}

	private void setMoveModeForFurnitures() {
		Hits hits = view.getHits();
		if (!hits.isEmpty()) {
			GeoElement f = hits.get(0);
			boolean combo = f.isGeoList() && ((GeoList) f).drawAsComboBox();
			boolean slider = f.isGeoNumeric() && ((GeoNumeric) f).isSlider();

			if (app.has(Feature.SELECT_TOOL_NEW_BEHAVIOUR)
					&& mode == EuclidianConstants.MODE_SELECT
					&& (f.isGeoBoolean() || f.isGeoButton() || combo
							|| slider)) {
				return;
			}

			if (f.isGeoVideo()) {
				return;
			}

			if (mode != EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX
					&& f.isGeoBoolean()
					|| mode != EuclidianConstants.MODE_BUTTON_ACTION
							&& (f.isGeoButton() && !f.isGeoInputBox())
					|| mode != EuclidianConstants.MODE_TEXTFIELD_ACTION
							&& f.isGeoInputBox()
					|| combo
					|| !sliderHittingMode() && slider) {
				app.setMoveMode();
			}
		}
	}

	private boolean sliderHittingMode() {
		return mode == EuclidianConstants.MODE_SLIDER
				|| mode == EuclidianConstants.MODE_LOCUS;
	}

	/**
	 * @param event
	 *            needed for 3D
	 */
	protected boolean hasNoHitsDisablingModeForShallMoveView(Hits hits,
			AbstractEvent event) {
		for (GeoElement geo : hits) {
			if (!(geo instanceof GeoAxis)) {
				return false;
			}
		}
		return true;
	}

	private boolean needsAxisZoom(Hits hits, AbstractEvent event) {
		return (hits.hasXAxis() || hits.hasYAxis())
				&& this.specialMoveEvent(event);
	}

	/**
	 * @param event
	 *            event calling
	 * @return mode when "shall move view"
	 */
	protected int getModeForShallMoveView(AbstractEvent event) {
		return EuclidianConstants.MODE_TRANSLATEVIEW;
	}

	private boolean shallMoveView(AbstractEvent event) {
		return app.isShiftDragZoomEnabled()
				&& (!doubleClickStarted && (mode == EuclidianConstants.MODE_MOVE
						|| specialMoveEvent(event)));
	}

	private boolean specialMoveEvent(AbstractEvent event) {
		return app.isShiftDragZoomEnabled() && (
		// MacOS: shift-cmd-drag is zoom
		(event.isShiftDown() && !app.isControlDown(event)) // All Platforms: Shift key
				|| (event.isControlDown() && app.isWindows()
				// old Windows key: Ctrl key
				) || app.isMiddleClick(event));
	}

	protected void runScriptsIfNeeded(GeoElement geo1) {
		// GGB-1196
		// no script run if Properies View is open.

		if (app.showView(App.VIEW_PROPERTIES)) {
			return;
		}

		// GeoTextField: click scripts run when user presses <Enter>
		if (!scriptsHaveRun && !geo1.isGeoInputBox()) {
			// make sure that Input Boxes lose focus (and so update) before
			// running scripts GGB-1351
			view.requestFocusInWindow();
			scriptsHaveRun = true;
			app.runScripts(geo1, null);
		}
		else if (view.getHits().size() > 0
				&& view.getHits().get(0) instanceof GeoInputBox) {
			// iPad inputboxes depend on this r50117
			view.requestFocusInWindow();
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
		view.setSelectionRectangle(null);

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
			TestGeo test) {
		for (int i = 0; i < hits.size(); i++) {
			GeoElement geo = hits.get(i);
			if (!test.check(geo)) {
				hits.remove(i);
			}
		}

		removeParentPoints(hits);
		getSelectedGeoList().addAll(hits);
		setAppSelectedGeos(hits, false);
		app.updateSelection(hits.size() > 0);
	}

	protected void processSelectionRectangle(boolean alt, boolean isControlDown,
			boolean shift) {
		GRectangle oldRectangle = view.getSelectionRectangle();
		if (app.has(Feature.SELECT_TOOL_NEW_BEHAVIOUR)) {
			if (mode != EuclidianConstants.MODE_SELECT
					&& mode != EuclidianConstants.MODE_SELECT_MOW) {
				clearSelections();
			}
		} else {
			clearSelections();
		}

		view.setHits(oldRectangle);
		Hits hits = view.getHits();

		boolean changedKernel = false;

		switch (mode) {
		case EuclidianConstants.MODE_ZOOM_IN:
			processZoomRectangle();
			break;
		case EuclidianConstants.MODE_SELECTION_LISTENER:
			break;

		case EuclidianConstants.MODE_MIRROR_AT_POINT:
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE:
			processSelectionRectangleForTransformations(hits,
					TestGeo.TRANSFORMABLE);
			break;

		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			processSelectionRectangleForTransformations(hits,
					TestGeo.TRANSFORMABLE);
			break;

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			processSelectionRectangleForTransformations(hits,
					TestGeo.TRANSFORMABLE);
			break;

		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			processSelectionRectangleForTransformations(hits, TestGeo.DILATEABLE);
			break;

		case EuclidianConstants.MODE_CREATE_LIST:
			removeParentPoints(hits);
			getSelectedGeoList().addAll(hits);
			setAppSelectedGeos(hits);
			changedKernel = processMode(hits, isControlDown, null);
			view.setSelectionRectangle(null);
			break;

		case EuclidianConstants.MODE_FITLINE:
			// check for list first
			if (hits.size() == 1) {
				if (hits.get(0).isGeoList()) {
					getSelectedGeoList().addAll(hits);
					setAppSelectedGeos(hits);
					changedKernel = processMode(hits, isControlDown, null);
					view.setSelectionRectangle(null);
					break;
				}
			}

			// remove non-Points
			for (int i = 0; i < hits.size(); i++) {
				GeoElement geo = hits.get(i);
				if (!(TestGeo.GEOPOINT.check(geo))) {
					hits.remove(i);
				}
			}

			// Fit line makes sense only for more than 2 points (or one list)
			if (hits.size() < 3) {
				hits.clear();
			} else {
				removeParentPoints(hits);
				getSelectedGeoList().addAll(hits);
				setAppSelectedGeos(hits);
				changedKernel = processMode(hits, isControlDown, null);
				view.setSelectionRectangle(null);
			}
			break;

		case EuclidianConstants.MODE_RELATION:
			// check for list first
			if (hits.size() == 1) {
				if (hits.get(0).isGeoList()) {
					getSelectedGeoList().addAll(hits);
					setAppSelectedGeos(hits);
					changedKernel = processMode(hits, isControlDown, null);
					view.setSelectionRectangle(null);
					break;
				}
			}

			// remove non-Points
			for (int i = 0; i < hits.size(); i++) {
				GeoElement geo = hits.get(i);
				if (!(TestGeo.GEOPOINT.check(geo))) {
					hits.remove(i);
				}
			}

			// Fit line makes sense only for more than 1 point (or one list)
			if (hits.size() < 2) {
				hits.clear();
			} else {
				removeParentPoints(hits);
				getSelectedGeoList().addAll(hits);
				setAppSelectedGeos(hits);
				changedKernel = processMode(hits, isControlDown, null);
				view.setSelectionRectangle(null);
			}
			break;

		default:
			// STANDARD CASE
			if (app.has(Feature.SELECT_TOOL_NEW_BEHAVIOUR)
					&& mode == EuclidianConstants.MODE_SELECT) {
				if (hits != null) {
					selection.addSelectedGeos(hits, true);
				}
			} else if (mode == EuclidianConstants.MODE_SELECT_MOW) {
				// check if it was a selection with the rectangle or just a drag
				if (view.getSelectionRectangle() != null) {
					view.setSelectionRectangle(null);
					// hit found
					if (hits != null && hits.size() > 0) {
						selection.setSelectedGeos(hits, true);
						// single selection
						if (hits.size() == 1) {
							Drawable dr = ((Drawable) view
									.getDrawableFor(hits.get(0)));
							BoundingBox boundingBox = dr.getBoundingBox();

							view.setBoundingBox(boundingBox);
							view.repaintView();
						}
						// multi-selection
						else {
							setBoundingBoxFromList(hits);
						}
					}
				}
			} else {
				setAppSelectedGeos(hits, false);
			}
			app.updateSelection((hits != null));

			// if alt pressed, create list of objects as string and copy to
			// input bar
			if ((hits != null) && (hits.size() > 0) && alt
					&& app.isUsingFullGui() && app.getGuiManager() != null
					&& app.showAlgebraInput()) {

				StringBuilder sb = new StringBuilder();
				sb.append(" {");
				for (int i = 0; i < hits.size(); i++) {
					sb.append(hits.get(i)
							.getLabel(StringTemplate.defaultTemplate));
					if (i < (hits.size() - 1)) {
						sb.append(", ");
					}
				}
				sb.append("} ");

				app.getGuiManager().replaceInputSelection(sb.toString());
			} else if (shift) {
				processZoomRectangle();
				return;
			}
			break;
		}

		if (changedKernel) {
			storeUndoInfo();
		}

		kernel.notifyRepaint();
	}

	protected void processSelection() {
		Hits hits = new Hits();
		hits.addAll(getAppSelectedGeos());
		clearSelections();

		switch (mode) {
		case EuclidianConstants.MODE_MIRROR_AT_POINT:
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE:
			processSelectionRectangleForTransformations(hits,
					TestGeo.TRANSFORMABLE);
			break;

		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			processSelectionRectangleForTransformations(hits,
					TestGeo.TRANSFORMABLE);
			break;

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			processSelectionRectangleForTransformations(hits,
					TestGeo.TRANSFORMABLE);
			break;

		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			processSelectionRectangleForTransformations(hits, TestGeo.DILATEABLE);
			break;

		// case EuclidianConstants.MODE_CREATE_LIST:
		case EuclidianConstants.MODE_FITLINE:
			for (int i = 0; i < hits.size(); i++) {
				GeoElement geo = hits.get(i);
				if (!(TestGeo.GEOPOINT.check(geo))) {
					hits.remove(i);
				}
			}
			// Fit line makes sense only for more than 2 points
			if (hits.size() < 3) {
				hits.clear();
			} else {
				removeParentPoints(hits);
				getSelectedGeoList().addAll(hits);
				setAppSelectedGeos(hits);
				processMode(hits, false, null);

				view.setSelectionRectangle(null);
			}
			break;

		case EuclidianConstants.MODE_RELATION:
			for (int i = 0; i < hits.size(); i++) {
				GeoElement geo = hits.get(i);
				if (!(TestGeo.GEOPOINT.check(geo))) {
					hits.remove(i);
				}
			}
			// Relation makes sense only for more than 1 point
			if (hits.size() < 2) {
				hits.clear();
			} else {
				removeParentPoints(hits);
				getSelectedGeoList().addAll(hits);
				setAppSelectedGeos(hits);
				processMode(hits, false, null);

				view.setSelectionRectangle(null);
			}
			break;

		default:
			break;
		}

		kernel.notifyRepaint();
	}

	/**
	 * Clear selection and show context menu.
	 * 
	 * @param mouse
	 *            pointer position
	 */
	public void clearAndShowDrawingPadPopup(GPoint mouse) {
		app.getSelectionManager().clearSelectedGeos();
		showDrawingPadPopup(mouse);
	}

	/**
	 * Show context menu.
	 * 
	 * @param mouse
	 *            pointer position
	 */
	public void showDrawingPadPopup(GPoint mouse) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().showDrawingPadPopup(view, mouse);
		}
	}

	protected boolean withinPointSelectionDistance(GPoint p, AbstractEvent q) {
		if (p == null || q == null) {
			return true;
		}
		double distance = Math.sqrt((p.x - q.getX()) * (p.x - q.getX())
				+ (p.y - q.getY()) * (p.y - q.getY()));
		return distance < DrawPoint
				.getSelectionThreshold(app.getCapturingThreshold(q.getType()));
	}

	protected boolean createNewPoint(Hits hits, boolean onPathPossible,
			boolean inRegionPossible, boolean intersectPossible,
			boolean doSingleHighlighting, boolean complexPoint) {
		boolean newPointCreated = createNewPointND(hits, onPathPossible,
				inRegionPossible, intersectPossible, doSingleHighlighting,
				complexPoint);
		GeoElement point = this.view.getHits().getFirstHit(TestGeo.GEOPOINT);
		if (point != null && !newPointCreated && this.selPoints() == 1
				&& (this.mode == EuclidianConstants.MODE_JOIN
						|| this.mode == EuclidianConstants.MODE_SEGMENT
						|| this.mode == EuclidianConstants.MODE_RAY
						|| this.mode == EuclidianConstants.MODE_VECTOR
						|| this.mode == EuclidianConstants.MODE_CIRCLE_TWO_POINTS
						|| this.mode == EuclidianConstants.MODE_SEMICIRCLE
						|| this.mode == EuclidianConstants.MODE_REGULAR_POLYGON)) {
			handleMovedElement(point, false, PointerEventType.MOUSE);
		}

		return newPointCreated;
	}

	protected boolean isDraggingOccuredBeyondThreshold() {
		return draggingOccured && draggingBeyondThreshold;
	}

	/**
	 * Handle pointer release.
	 * 
	 * @param event
	 *            pointer event
	 */
	public void wrapMouseReleased(AbstractEvent event) {
		if (getTextController() != null
				&& getTextController().isEditing())  {
			return;
		}
		// will be reset in wrapMouseReleased
		AccessibilityManagerInterface am = app.getAccessibilityManager();
		if (am != null && !app.getKernel().getConstruction().isEmpty()) {
			am.setTabOverGeos(true);
		}
		// handle video/audio/embeded/text release (mow)
		if (handleVideoReleased()) {
			return;
		}
		if (app.isWhiteboardActive()
				&& mode == EuclidianConstants.MODE_TRANSLATEVIEW
				&& !draggingOccured) {
			app.setMode(EuclidianConstants.MODE_SELECT_MOW);
		}
		if (getTextController() != null
				&& getTextController().handleTextReleased(draggingOccured,
						event.getX(), event.getY())
				&& !draggingOccured) {
			ArrayList<GeoElement> elements = selection.getSelectedGeos();
			if (elements.size() == 1) {
				GeoElement selected = elements.get(0);
				if (selected.hasPreviewPopup()) {
					showSpecialPointPopup(elements);
				} else {
					showDynamicStylebar();
				}
			}
			return;
		}

		GeoPointND p = this.selPoints() == 1 ? getSelectedPointList().get(0)
				: null;

		DrawDropDownList dl = view.getOpenedComboBox();
		if (dl != null) {
			dl.onMouseUp(event.getX(), event.getY());
			return;
		}
		// reset the center of rotation for bounding box; don't delete center
		// for "move around point"
		if (rotationCenter != null && !rotationCenter.isLabelSet()) {
			rotationCenter.remove();
			rotationCenter = null;
		}

		if (this.mode == EuclidianConstants.MODE_CIRCLE_POINT_RADIUS) {
			view.setPreview(null);
			if (firstSelectedPoint != null
					&& !withinPointSelectionDistance(startPosition, event)) {
				double x = view.toRealWorldCoordX(event.getX());
				double y = view.toRealWorldCoordY(event.getY());
				double distance = Math.sqrt(
						Math.pow((firstSelectedPoint.getInhomX() - x), 2) + Math
								.pow((firstSelectedPoint.getInhomY() - y), 2));
				kernel.getAlgoDispatcher().circle(null, firstSelectedPoint,
						new GeoNumeric(kernel.getConstruction(), distance));
				firstSelectedPoint = null;
				storeUndoInfo();
				return;
			}
		}
		// resize, single selection
		if (getResizedShape() != null) {
			getResizedShape().updateGeo(AwtFactory.getPrototype()
					.newPoint2D(event.getX(), event.getY()));
			selection.addSelectedGeo(getResizedShape().getGeoElement());
			if (!isDraggingOccuredBeyondThreshold()) {
				showDynamicStylebar();
			}
			storeUndoInfo();
			setResizedShape(null);
			view.setHitHandler(EuclidianBoundingBoxHandler.UNDEFINED);
		}
		// resize, multi-selection
		else if (isMultiResize) {
			for (GeoElement geo : selection.getSelectedGeos()) {
				((Drawable) view.getDrawableFor(geo)).updateGeo(AwtFactory
						.getPrototype().newPoint2D(event.getX(), event.getY()));
			}
		}
		// undo/redo for multi-selection
		if (isMultiResize) {
			storeUndoInfo();
			isMultiResize = false;
			setBoundingBoxFromList(selection.getSelectedGeos());
			view.setHitHandler(EuclidianBoundingBoxHandler.UNDEFINED);
		}

		if (shapeMode(mode) && !app.isRightClick(event) && !shapeDragged) {
			GeoElement geo = getShapeMode()
						.handleMouseReleasedForShapeMode(event);
			if (geo != null && geo.isShape()
						&& view.getDrawableFor(geo) != null) {
				Drawable d = ((Drawable) view.getDrawableFor(geo));
				d.update();
				if (d.getBoundingBox().getRectangle() != null) {
					view.setBoundingBox(d.getBoundingBox());
					view.repaintView();
					selection.addSelectedGeo(geo);
					app.setMode(EuclidianConstants.MODE_SELECT_MOW,
							ModeSetter.DOCK_PANEL);
				}
			}
			if (!isDraggingOccuredBeyondThreshold()) {
				showDynamicStylebar();
			}
			view.setCursor(EuclidianCursor.DEFAULT);
			storeUndoInfo();
			return;
		}

		// after finished drag switch back mode
		// also ignore drag start point
		if (EuclidianConstants.isMoveOrSelectionMode(mode) && shapeDragged) {
			shapeDragged = false;
			mode = oldShapeMode;
			getShapeMode().setDragStartPointSet(false);
		}

		if (!event.isRightClick() && isDragTool()) {
			if (withinPointSelectionDistance(startPosition, event)) {
				this.view.setHits(new GPoint(event.getX(), event.getY()),
						event.getType());

				if (this.selPoints() == 1 && !view.getHits().contains(p)) {
					wrapMouseReleasedND(event, true);
				} else {
					checkResetOrAnimationHit(event.getX(), event.getY());
				}

				return;
			}

			wrapMouseReleasedND(event, true);

			this.view.setHits(new GPoint(event.getX(), event.getY()),
					event.getType());
			Hits hits = view.getHits();

			if (p != null && hits.getFirstHit(TestGeo.GEOPOINTND) == null) {
				if (!getSelectedPointList().contains(p)) {
					this.getSelectedPointList().add(p);
				}
				createNewPointForModeOther(hits);
				this.view.setHits(new GPoint(event.getX(), event.getY()),
						event.getType());
				hits = view.getHits();
				boolean kernelChange = switchModeForProcessMode(hits,
						event.isControlDown(), null, false);
				if (kernelChange) {
					storeUndoInfo();
				}
			}
		} else {
			wrapMouseReleasedND(event, true);
		}

		// Quick fix for GeoFunctions.
		// TODO: call it once in the method.
		if (app.isUnbundledOrWhiteboard()
				&& EuclidianConstants
						.isMoveOrSelectionModeCompatibleWithDragging(mode,
								isDraggingOccuredBeyondThreshold())
				&& !event.isRightClick()) {
			if (app.getConfig().hasPreviewPoints() && previewPointHits != null
					&& !previewPointHits.isEmpty()) {
				hideDynamicStylebar();
				highlightPreviewPoint(previewPointHits.get(0));
				showSpecialPointPopup(previewPointHits);
				previewPointHits.clear();
			} else {
				ArrayList<GeoElement> geos = selection.getSelectedGeos();
				if (geos.size() == 1 && geos.get(0).hasPreviewPopup()) {
					showSpecialPointPopup(geos);
				} else {
					showDynamicStylebar();
				}
			}
		}

		if (getResizedShape() != null) {
			view.setHitHandler(EuclidianBoundingBoxHandler.UNDEFINED);
			setResizedShape(null);
		}

		if (this.pointerUpCallback != null) {
			runPointerCallback(pointerUpCallback);
			this.pointerUpCallback = null;
		}
	}

	/**
	 * It is highlighting the currently hit preview point, and at the same time
	 * removing the highlight from the previously hit preview point (if there
	 * was any)
	 *
	 * @param geoElement
	 *            preview point to highlight
	 */
	private void highlightPreviewPoint(GeoElement geoElement) {
		if (app.getSpecialPointsManager().getSelectedPreviewPoints() == null) {
			return;
		}

		for (GeoElement geo : app.getSpecialPointsManager()
				.getSelectedPreviewPoints()) {
			geo.setHighlighted(false);
		}
		geoElement.setHighlighted(true);
		geoElement.updateRepaint();
	}

	/**
	 * Needs to be synchronous in some environments and asynchronous in others
	 *
	 * @param callback
	 *            callback for pointer up
	 */
	protected void runPointerCallback(Runnable callback) {
		callback.run();
	}

	private boolean isDragTool() {
		return this.mode == EuclidianConstants.MODE_JOIN
				|| this.mode == EuclidianConstants.MODE_SEGMENT
				|| this.mode == EuclidianConstants.MODE_RAY
				|| this.mode == EuclidianConstants.MODE_VECTOR
				|| this.mode == EuclidianConstants.MODE_CIRCLE_TWO_POINTS
				|| this.mode == EuclidianConstants.MODE_SEMICIRCLE
				|| this.mode == EuclidianConstants.MODE_REGULAR_POLYGON;
	}

	/**
	 * @param event
	 *            mouse release event
	 * @param mayFocus
	 *            whether focusing view is allowed
	 */
	public void wrapMouseReleasedND(final AbstractEvent event,
			boolean mayFocus) {
		int x = event.getX();
		int y = event.getY();
		boolean right = app.isRightClick(event);
		boolean control = app.isControlDown(event);
		final boolean alt = event.isAltDown();
		final boolean meta = event.isPopupTrigger() || event.isMetaDown();
		PointerEventType type = event.getType();

		if (isDraggingOccuredBeyondThreshold()) {
			if (shouldClearSelectionAfterMove(right)) {
				clearSelectionsKeepLists(true, true);
			} else {
				dontClearSelection = true;
			}
		} else {
			if (app.has(Feature.SELECT_TOOL_NEW_BEHAVIOUR)
					&& mode == EuclidianConstants.MODE_SELECT) {
				switch (lastSelectionPressResult) {
				case REMOVE:
					selection.removeSelectedGeo(lastSelectionToolGeoToRemove, true, true);
					lastSelectionToolGeoToRemove = null;
					break;
				case EMPTY:
					selection.clearSelectedGeos(true, true);
					break;
				case ADD:
				case DEFAULT:
				default:
					// nothing to do
					break;
				}
			} else if (mode == EuclidianConstants.MODE_SELECT_MOW) {
				switch (lastSelectionPressResult) {
				case EMPTY:
					clearSelections();
					break;
				case DEFAULT:
				default:
					// select the geo that was clicked and set boundingbox
					if (isMultiSelection() && !wasBoundingBoxHit
							&& !event.isRightClick()
							&& lastSelectionToolGeoToRemove != null) {
						selection.clearSelectedGeos(false, false);
						selection.addSelectedGeo(lastSelectionToolGeoToRemove,
								true, true);
						view.setBoundingBox(((Drawable) view
								.getDrawableFor(lastSelectionToolGeoToRemove))
										.getBoundingBox());
						view.repaintView();
						lastSelectionToolGeoToRemove = null;
					}
					break;
				}
			}
		}

		if (app.has(Feature.SELECT_TOOL_NEW_BEHAVIOUR)) {
			lastSelectionPressResult = SelectionToolPressResult.DEFAULT;
		}

		if (this.doubleClickStarted && !isDraggingOccuredBeyondThreshold() && !right) {
			wrapMouseclicked(control, 2, type);
		}
		this.doubleClickStarted = false;
		if (type == PointerEventType.MOUSE) {
			this.lastMouseRelease = System.currentTimeMillis();
		} else {
			this.lastTouchRelease = System.currentTimeMillis();
		}
		this.setLastMouseUpLoc(new GPoint(x, y));

		app.storeUndoInfoIfSetCoordSystemOccured();

		if (pressedButton != null && !(app.showView(App.VIEW_PROPERTIES))) {
			pressedButton.setDraggedOrContext(
					pressedButton.getDraggedOrContext() || meta);

			// make sure that Input Boxes lose focus (and so update) before
			// running scripts
			view.requestFocusInWindow();

			pressedButton.setPressed(false);
			pressedButton = null;
		}

		boolean isPenDragged = penMode(mode) && penDragged;
		// remove deletion rectangle
		if (view.getDeletionRectangle() != null) {
			// ended deletion
			view.setDeletionRectangle(null);
			view.repaintView();
			if (!isPenDragged) {
				storeUndoInfo();
			}
		}

		// reset
		transformCoordsOffset[0] = 0;
		transformCoordsOffset[1] = 0;

		if (!event.isRightClick() && this.textfieldJustFocused(x, y, type)) {
			draggingOccured = false;
			return;
		}
		// make sure we start the timer also for single point
		if (!isPenDragged && penMode(mode)) {
			getPen().startTimer();
		}
		if (penMode(mode)) {
			boolean geoCreated = getPen().handleMouseReleasedForPenMode(right, x, y);
			if (geoCreated) {
				storeUndoInfo();
			}
			draggingOccured = false;
			return;
		}

		boolean changedKernel0 = false;
		if (pastePreviewSelected != null) {
			// add moved points to sticky points again
			for (GeoElement geo : pastePreviewSelectedAndDependent) {
				if (geo.isGeoPoint()) {
					if (!view.getStickyPointList().contains(geo)) {
						view.getStickyPointList().add((GeoPointND) geo);
					}
				}
			}
			persistentStickyPointList = new ArrayList<>();

			pastePreviewSelected = null;
			pastePreviewSelectedAndDependent = null;
			view.setPointCapturing(previousPointCapturing);
			changedKernel0 = true;
			app.getKernel().getConstruction().getUndoManager()
					.storeUndoInfoAfterPasteOrAdd();
		}

		if (getMovedGeoPoint() != null) {
			processReleaseForMovedGeoPoint(right);
		}
		if (movedGeoElement instanceof GeoPointND
				&& movedGeoElement.hasChangeableCoordParentNumbers()
				&& !isDraggingOccuredBeyondThreshold()) {
			this.switchPointMoveMode();
		}
		if (movedGeoNumeric != null) {
			if (app.isUsingFullGui()) {
				movedGeoNumeric.resetTraceColumns();
			}
		}

		if (mayFocus && !hitComboBoxOrTextfield()) {
			view.requestFocusInWindow();
		}

		setMouseLocation(alt, x, y);

		transformCoords();
		Hits hits = null;

		if (specialRelease(x, y, event, right, alt, control, type)) {
			draggingOccured = false;
			return;
		}

		// handle moving
		boolean changedKernel = false;
		if (isDraggingOccuredBeyondThreshold()) {
			draggingOccurredBeforeRelease = true;
			draggingOccured = false;

			// check movedGeoElement.isLabelSet() to stop moving points
			// in Probability Calculator triggering Undo
			changedKernel = ((movedGeoElement != null)
					&& movedGeoElement.isLabelSet()) && (moveMode != MOVE_NONE)
					&& modeTriggersUndoOnDragGeo(mode);
			movedGeoElement = null;
			rotGeoElement = null;

			// Michael Borcherds 2007-10-08 allow dragging with right mouse
			// button
			if (!temporaryMode) {
				// Michael Borcherds 2007-10-08
				if (allowSelectionRectangle()) {
					processSelectionRectangle(alt, control,
							event.isShiftDown());

					return;
				}
			}
			notifyCoordSystemListeners();
		} else {
			movedGeoElement = null;
			// no hits: release mouse button creates a point
			// for the transformation tools
			// (note: this cannot be done in mousePressed because
			// we want to be able to select multiple objects using the selection
			// rectangle)
			changedKernel = switchModeForMouseReleased(mode, hits,
					changedKernel, control, type, mayFocus);
		}

		// remember helper point, see createNewPoint()
		if ((changedKernel || this.checkboxChangeOccured) && !changedKernel0
				&& !modeCreatesHelperPoints(mode)) {
			this.checkboxChangeOccured = false;
			storeUndoInfo();
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
			this.defaultEventType = oldEventType;
			// Michael Borcherds 2007-12-08 BEGIN bugfix: couldn't select
			// multiple points with Ctrl
			if (!dontClearSelection) {
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
			AsyncOperation<Boolean> callback = new AsyncOperation<Boolean>() {

				@Override
				public void callback(Boolean arg) {
					if (arg.equals(true)) {
						storeUndoInfo();
					}
					endOfWrapMouseReleased(hits2, event);
				}
			};

			processMode(hits, control, callback);

		}

		endOfWrapMouseReleased(hits, event);

		draggingOccurredBeforeRelease = false;
	}

	private boolean shouldClearSelectionForMove() {
		List<GeoElement> selectedGeos = selection.getSelectedGeos();
		return !(selectedGeos.size() == 1
				&& (selectedGeos.get(0) instanceof GeoFunction || selectedGeos.get(0).isGeoAudio())
				&& mode != EuclidianConstants.MODE_MOVE);
	}

	private boolean wasBoundingBoxDrag() {
		if (!app.isWhiteboardActive()) {
			return false;
		}
		// do not deselect during resizing with bounding/crop box
		if (selection == null || view.boundingBox == null) {
			return false;
		}
		List<GeoElement> selectedGeos = selection.getSelectedGeos();
		if (selectedGeos != null && selectedGeos.size() == 1) {
			DrawableND d = view.getDrawable(selectedGeos.get(0));
			return d != null && ((Drawable) d).getBoundingBox() != null
					&& view.boundingBox.equals(((Drawable) d).getBoundingBox());
		}
		return false;
	}

	private boolean shouldClearSelectionAfterMove(boolean rightClick) {
		boolean shouldClear = !EuclidianView.usesSelectionRectangleAsInput(mode) && !rightClick
				&& mode != EuclidianConstants.MODE_SELECT
				&& (mode != EuclidianConstants.MODE_TRANSLATEVIEW
						&& temporaryMode
						&& oldMode == EuclidianConstants.MODE_SELECT_MOW)
				&& !wasBoundingBoxDrag();
		shouldClear &= shouldClearSelectionForMove();
		return shouldClear;
	}

	protected void switchPointMoveMode() {
		// TODO Auto-generated method stub
	}

	private boolean specialRelease(int x, int y, AbstractEvent event,
			boolean right, boolean alt, boolean control,
			PointerEventType type) {
		if (checkResetOrAnimationHit(x, y)) {
			return true;
		}

		// if rotate, set continue animation / stop it
		if (processReleaseForRotate3D(x, type)) {
			return true;
		}

		// allow drag with right mouse button or ctrl
		// make sure Ctrl still works for selection (when no dragging occured)
		if (right || (control && isDraggingOccuredBeyondThreshold())) {
			if (!temporaryMode) {
				processRightReleased(right, alt, control, event.isShiftDown(),
						type);
				return true;
			}
		}
		return false;
	}

	private boolean checkResetOrAnimationHit(int x, int y) {
		if (hitResetIcon()) {
			app.reset();
			return true;
		} else if (view.hitAnimationButton(x, y)
				|| this.animationButtonPressed) {
			this.animationButtonPressed = false;
			if (kernel.isAnimationRunning()) {
				kernel.getAnimatonManager().stopAnimation();
			} else {
				kernel.getAnimatonManager().startAnimation();
			}

			// make sure geo.updateRepaint(); doesn't trigger update scripts
			boolean oldBlockUpdateScripts = app.isBlockUpdateScripts();
			app.setBlockUpdateScripts(true);

			// update sliders in AV
			if (app.getGuiManager() != null
					&& app.getGuiManager().hasAlgebraView()) {
				for (GeoElement geo : kernel.getConstruction()
						.getGeoSetConstructionOrder()) {
					if (geo instanceof GeoNumeric) {
						geo.updateRepaint();
					}
				}
			}

			app.setBlockUpdateScripts(oldBlockUpdateScripts);

			view.repaintView();

			app.setUnsaved();
			return true;
		}
		return false;
	}

	private boolean hitComboBoxOrTextfield() {
		return view.getHits() != null && view.getHits().getTopHits().size() > 0
				&& (view.getHits().getTopHits().get(0) instanceof GeoInputBox
						|| view.getHits().getTopHits()
								.get(0) instanceof GeoList);
	}

	protected DrawDropDownList getComboBoxHit() {
		Hits hits = view.getHits();
		if (hits != null && hits.size() > 0) {
			GeoList list;
			for (GeoElement geo : hits.getTopHits()) {
				if (geo instanceof GeoList
						&& ((GeoList) geo).drawAsComboBox()) {
					list = (GeoList) geo;
					return (DrawDropDownList) (view.getDrawable(list));
				}
			}

		}
		return null;
	}

	protected DrawAudio getAudioHit() {
		Hits hits = view.getHits();
		if (hits != null && hits.size() > 0) {
			GeoAudio audio;
			for (GeoElement geo : hits.getTopHits()) {
				if (geo.isGeoAudio()) {
					audio = (GeoAudio) geo;
					return (DrawAudio) (view.getDrawable(audio));
				}
			}

		}
		return null;
	}

	protected GeoFrame getVideoHit() {
		Hits hits = view.getHits();
		if (hits != null && hits.size() > 0) {
			for (GeoElement geo : hits.getTopHits()) {
				if (geo instanceof GeoFrame) {
					return (GeoFrame) geo;
				}
			}
		}
		return null;
	}

	/**
	 * @param hits
	 *            hits
	 * @param event
	 *            pointer event
	 */
	public void endOfWrapMouseReleased(Hits hits, AbstractEvent event) {
		boolean control = app.isControlDown(event);
		boolean alt = event.isAltDown();
		PointerEventType type = event.getType();
		endOfWrapMouseReleased(hits, control, alt, type);
	}

	/**
	 * @param hits
	 *            hits
	 * @param control
	 *            whethe ctrl is pressed
	 * @param alt
	 *            whether alt is pessed
	 * @param type
	 *            pointer device type
	 */
	public void endOfWrapMouseReleased(Hits hits, boolean control, boolean alt,
			PointerEventType type) {
		if (!hits.isEmpty()) {
			view.setCursor(EuclidianCursor.DEFAULT);
		} else {
			setHitCursor();
		}

		refreshHighlighting(null, control);

		moveMode = MOVE_NONE;
		initShowMouseCoords();
		view.setShowAxesRatio(false);

		if (!hasJustCreatedGeos()) { // first try to set just created
			// geos as selected
			// if none, do specific stuff for properties view
			// prevent objects created by a script
			if (app.isUsingFullGui() && app.getGuiManager() != null) {
				app.getGuiManager().mouseReleasedForPropertiesView(
						!EuclidianConstants.isMoveOrSelectionMode(mode)
								&& mode != EuclidianConstants.MODE_MOVE_ROTATE);
			}
		}
		// Alt click: copy definition to input field
		if (alt && app.showAlgebraInput()) {
			altClicked(type);
		}
		// selection is not highlighted during move in 3D view
		selection.updateSelectionHighlight();
		kernel.notifyRepaint();
	}

	private void altClicked(PointerEventType type) {
		setViewHits(type);
		Hits hits = view.getHits().getTopHits();
		if ((hits != null) && (hits.size() > 0)) {
			hits.removePolygons();
			GeoElement geo = hits.get(0);

			// F3 key: copy definition to input bar
			if (mode != EuclidianConstants.MODE_ATTACH_DETACH && altCopy) {
				app.getGlobalKeyDispatcher().handleFunctionKeyForAlgebraInput(3,
						geo);
			}

			moveMode = MOVE_NONE;
		}
	}

	private void processRightReleased(boolean right, boolean alt,
			boolean control, boolean shift, PointerEventType type) {
		if (!app.isRightClickEnabled()) {
			return;
		}

		// make sure cmd-click selects multiple points (not open
		// properties)
		if ((app.isMacOS() && control) || !right) {
			return;
		}
		if (isDraggingOccuredBeyondThreshold()) {
			if (allowSelectionRectangle()) {
				processSelectionRectangle(alt, control, shift);
				return;
			}
		}
		// get selected GeoElements
		// show popup menu after right click
		setViewHits(type);
		Hits hits = view.getHits().getTopHits();
		// no hits
		if (hits.isEmpty()) {
			// no hit -> clear selection
			if (mode == EuclidianConstants.MODE_SELECT_MOW) {
				clearSelections();
			}

			if (app.isUsingFullGui() && app.getGuiManager() != null) {

				if (view.getSelectionRectangle() != null) {
					// don't show a contextMenu if there's a
					// selectionRectangle
					processSelectionRectangle(alt, control, shift);
				} else if (selection.selectedGeosSize() > 0) {
					if (app.isUnbundledOrWhiteboard()) {
						clearAndShowDrawingPadPopup(mouseLoc);
					} else {
						showPopupMenuChooseGeo(getAppSelectedGeos(), hits);
					}
				} else {
					showDrawingPadPopup(mouseLoc);
				}
			}
		} else {
			// there are hits
			if (selection.selectedGeosSize() > 0
					&& EuclidianConstants.isMoveOrSelectionMode(mode)) {
				// only for move mode
				// right click on already selected geos -> show menu for them
				// right click on object(s) not selected -> clear
				// selection and show menu just for new objects

				if (mode != EuclidianConstants.MODE_SELECT_MOW
						&& !hits.intersect(getAppSelectedGeos())) {
					selection.clearSelectedGeos(false); // repaint will be
					// done next step
					selection.addSelectedGeos(hits, true);
				}

				if (canShowPopupMenu()) {
					if (!hits.intersect(getAppSelectedGeos())) {
						clearSelections();
					}
					showPopupMenuChooseGeo(getAppSelectedGeos(), hits);
				}
			} else {
				// other modes : want to apply tool of one of the hits
				// (choose geo and show popup menu)
				// no selected geos: choose geo and show popup menu
				if (canShowPopupMenu()) {
					GeoElement geo = chooseGeo(hits, true, false);

					if (geo == null) {
						showDrawingPadPopup(mouseLoc);
					} else {
						ArrayList<GeoElement> geos = new ArrayList<>();
						geos.add(geo);
						showPopupMenuChooseGeo(geos, hits);
					}
				}
			}
		}
	}

	private boolean canShowPopupMenu() {
		return !isDraggingOccuredBeyondThreshold() && app.isUsingFullGui()
				&& app.getGuiManager() != null;
	}

	/**
	 * @return true if any just created geos
	 */
	private boolean hasJustCreatedGeos() {
		return justCreatedGeos != null && justCreatedGeos.size() > 0;
	}

	/**
	 * Handle mouse wheel event.
	 * 
	 * @param x
	 *            mouxe pointer x
	 * @param y
	 *            mouse pointer y
	 * @param delta
	 *            scroll difference
	 * @param shiftOrMeta
	 *            whether shift or meta keys are pressed
	 * @param alt
	 *            whether alt is pressed
	 */
	public void wrapMouseWheelMoved(int x, int y, double delta,
			boolean shiftOrMeta, boolean alt) {
		if (view.hasDynamicStyleBar()) {
			this.hideDynamicStylebar();
		}

		if (getTextController() != null) {
			getTextController().stopEditing();
		}

		if (isTextfieldHasFocus()) {
			return;
		}

		if (penMode(mode)) {
			return;
		}

		DrawDropDownList combo = view.getOpenedComboBox();
		if (combo != null) {
			combo.onMouseWheel(delta);
			return;
		}
		app.maySetCoordSystem();

		// don't allow mouse wheel zooming for applets if mode is not zoom mode
		if (!allowMouseWheel(shiftOrMeta)) {
			return;
		}

		setMouseLocation(alt, x, y);

		double px = mouseLoc.x;
		double py = mouseLoc.y;

		double xFactor = 1;
		if (alt) {
			xFactor = 1.5;
		}

		double reverse = -1;

		double factor = ((delta * reverse) > 0)
				? EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR * xFactor
				: 1d / (EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR * xFactor);

		// make zooming a little bit smoother by having some steps

		view.setAnimatedCoordSystem(
				px, py, factor, view.getXscale() * factor, 4, false);
		app.setUnsaved();
	}

	/**
	 * @param shiftOrMeta
	 *            whether shift or meta is pressed
	 * @return whether zoom is allowed and app is focused
	 */
	public boolean allowMouseWheel(boolean shiftOrMeta) {
		return !app.isApplet() || (mode == EuclidianConstants.MODE_ZOOM_IN)
				|| (mode == EuclidianConstants.MODE_ZOOM_OUT)
				|| (app.isShiftDragZoomEnabled()
						&& (app.hasFocus() || shiftOrMeta));
	}

	/**
	 * Update preview line endpoint. FIXME duplicate method
	 * 
	 * @param p
	 *            line endpoint
	 */
	public void setLineEndPoint(GPoint2D p) {
		if (p == null) {
			lineEndPoint = null;
		} else {
			lineEndPoint = new GPoint2D.Double(p.getX(), p.getY());
		}
		useLineEndPoint = true;
	}

	public Hits getHighlightedgeos() {
		return highlightedGeos.cloneHits();
	}

	/**
	 * Change opacity of selected geos.
	 * 
	 * @param alpha
	 *            opacity
	 */
	public void setAlpha(float alpha) {
		for (GeoElement geo : getAppSelectedGeos()) {
			geo.setAlphaValue(alpha);
			geo.updateRepaint();
		}
	}

	/**
	 * Change point size or line thickness of selected geos. TODO seems unused
	 * 
	 * @param size
	 *            size
	 */
	public void setSize(int size) {
		for (GeoElement geo : getAppSelectedGeos()) {
			if (geo instanceof PointProperties) {
				((PointProperties) geo).setPointSize(size);
				geo.updateRepaint();
			} else {
				geo.setLineThickness(size);
				geo.updateRepaint();
			}
		}
	}

	/**
	 * Update preview line endpoint.
	 * 
	 * @param point
	 *            line endpoint
	 */
	public void setLineEndPoint(GPoint2D.Double point) {
		lineEndPoint = point;
		useLineEndPoint = true;
	}

	protected Previewable switchPreviewableForInitNewMode(int mode1) {
		Previewable previewDrawable = null;
		// init preview drawables
		switch (mode1) {

		case EuclidianConstants.MODE_FREEHAND_SHAPE:
			if (pen == null || !pen.isFreehand()) {
				pen = new EuclidianPenFreehand(app, view);
			}
			break;
		case EuclidianConstants.MODE_PEN:
		case EuclidianConstants.MODE_HIGHLIGHTER:
			if (pen == null || pen.isFreehand()) {
				pen = new EuclidianPen(app, view);
			}
			break;

		case EuclidianConstants.MODE_PARALLEL:

			previewDrawable = view.createPreviewParallelLine(
					getSelectedPointList(), getSelectedLineList(),
					getSelectedFunctionList());
			break;

		case EuclidianConstants.MODE_PARABOLA:
			previewDrawable = view.createPreviewParabola(getSelectedPointList(),
					getSelectedLineList());
			break;

		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
			previewDrawable = view
					.createPreviewAngleBisector(getSelectedPointList());
			break;

		case EuclidianConstants.MODE_ORTHOGONAL:
		case EuclidianConstants.MODE_ORTHOGONAL_THREE_D:

			previewDrawable = view.createPreviewPerpendicularLine(
							getSelectedPointList(), getSelectedLineList(),
							getSelectedFunctionList());
			break;

		case EuclidianConstants.MODE_LINE_BISECTOR:
			previewDrawable = view
					.createPreviewPerpendicularBisector(getSelectedPointList());
			break;

		case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
			previewDrawable = view.createPreviewConic(mode1,
					getSelectedPointList());
			break;

		case EuclidianConstants.MODE_JOIN: // line through two points
			useLineEndPoint = false;
			previewDrawable = view.createPreviewLine(getSelectedPointList());
			break;

		case EuclidianConstants.MODE_SEGMENT:
			useLineEndPoint = false;
			previewDrawable = view.createPreviewSegment(getSelectedPointList());
			break;

		case EuclidianConstants.MODE_RAY:
			useLineEndPoint = false;
			previewDrawable = view.createPreviewRay(getSelectedPointList());
			break;

		case EuclidianConstants.MODE_VECTOR:
			useLineEndPoint = false;
			previewDrawable = view.createPreviewVector(getSelectedPointList());
			break;

		case EuclidianConstants.MODE_POLYGON:
		case EuclidianConstants.MODE_RIGID_POLYGON:
		case EuclidianConstants.MODE_VECTOR_POLYGON:
			previewDrawable = view.createPreviewPolygon(getSelectedPointList());
			break;

		case EuclidianConstants.MODE_POLYLINE:
			previewDrawable = view
					.createPreviewPolyLine(getSelectedPointList());
			break;

		case EuclidianConstants.MODE_CIRCLE_TWO_POINTS:
		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
			previewDrawable = view.createPreviewConic(mode1,
					getSelectedPointList());
			break;

		case EuclidianConstants.MODE_ANGLE:
			previewDrawable = view.createPreviewAngle(getSelectedPointList());
			break;

		// preview for compass: radius first
		case EuclidianConstants.MODE_COMPASSES:
			previewDrawable = new DrawConic(view, mode1, getSelectedPointList(),
					getSelectedSegmentList(), getSelectedConicNDList());
			break;

		// preview for arcs and sectors
		case EuclidianConstants.MODE_SEMICIRCLE:
		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			previewDrawable = new DrawConicPart(view, mode1,
					getSelectedPointList());
			break;

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			useLineEndPoint = false;
			previewDrawable = view.createPreviewVector(getSelectedPointList());
			break;

		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
			// do the following only once, from last EuclidianView
			// prevent clearSelections() from a next EV would break it all
			if (view != kernel.getLastAttachedEV()) {
				return previewDrawable;
			}

			// toggle currently selected geos visibility
			for (GeoElement geo : selection.getSelectedGeos()) {
				if (geo.isEuclidianToggleable()) {
					geo.setEuclidianVisible(!geo.isSetEuclidianVisible());
					geo.updateRepaint();
				}
			}
			selection.clearSelectedGeos(false, false);

			// select all hidden objects
			for (GeoElement geo : kernel.getConstruction()
					.getGeoSetConstructionOrder()) {
				// independent numbers should not be set visible
				// as this would produce a slider
				if (!geo.isSetEuclidianVisible()
						&& !((geo instanceof NumberValue
						|| geo instanceof BooleanValue)
						&& geo.isIndependent())) {
					geo.setEuclidianVisible(true);
					selection.addSelectedGeo(geo, false, false);
					geo.updateRepaint();
				}
			}
			kernel.notifyRepaint();
			selection.updateSelection();
			break;

		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
			app.setGeoForCopyStyle(null); // this will be the active geo
			// template
			break;

		case EuclidianConstants.MODE_MOVE_ROTATE:
			rotationCenter = null; // this will be the active geo template
			break;

		case EuclidianConstants.MODE_SHOW_HIDE_LABEL:
			// do the following only once, from last EuclidianView
			// prevent clearSelections() from a next EV would break it all
			if (view != kernel.getLastAttachedEV()) {
				return previewDrawable;
			}

			// toggle currently selected geos visibility
			for (GeoElement geo : selection.getSelectedGeos()) {
				if (geo.isLabelShowable()) {
					geo.setLabelVisible(!geo.isLabelVisible());
					geo.updateRepaint();
				}
			}
			selection.clearSelectedGeos(false, false);
			kernel.notifyRepaint();
			selection.updateSelection();
			break;

		case EuclidianConstants.MODE_DELETE:
			// do the following only once, from last EuclidianView
			// prevent clearSelections() from a next EV would break it all
			if (view != kernel.getLastAttachedEV()) {
				return previewDrawable;
			}
			app.deleteSelectedObjects(false);
			break;

		default:
			// macro mode?
			if (mode1 >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
				// get ID of macro
				getMacroMode().setMode(mode1);
				this.mode = EuclidianConstants.MODE_MACRO;
			}
			break;
		}

		return previewDrawable;
	}

	private ModeMacro getMacroMode() {
		if (modeMacro == null) {
			modeMacro = new ModeMacro(this);
		}
		return modeMacro;
	}

	protected void initNewMode(int newMode) {
		initNewMode(newMode, true);
	}

	protected void initNewMode(int newMode, boolean clear) {
		boolean wasUndoableMode = isUndoableMode();

		this.mode = newMode;
		initShowMouseCoords();
		if (clear && (!temporaryMode
				&& !(EuclidianView.usesSelectionRectangleAsInput(newMode)
						&& view.getSelectionRectangle() != null))) {
			clearSelections();
		}
		moveMode = MOVE_NONE;

		view.setPreview(switchPreviewableForInitNewMode(newMode));
		toggleModeChangedKernel = false;
		if (!temporaryMode) {
			// change tool: remove unfinished creation but not on move <=> move
			// view switch
			if (wasUndoableMode) {
				kernel.restoreStateForInitNewMode();
			}

			if (kernel.isUndoActive()) {
				kernel.storeStateForModeStarting();
			}
		}
	}

	/**
	 * @param altPressed
	 *            whether alt is pressed
	 * @param minusPressed
	 *            whether minus rather than plus is pressed
	 */
	public void zoomInOut(boolean altPressed, boolean minusPressed) {
		double factor = minusPressed
				? 1d / EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR
				: EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR;
		if (app.isUnbundled()) {
			factor = minusPressed ? 1d / EuclidianView.MODE_ZOOM_FACTOR
					: EuclidianView.MODE_ZOOM_FACTOR;
		}
		// accelerated zoom
		if (altPressed) {
			factor *= minusPressed ? 2d / 3d : 1.5;
		}

		zoomInOut(factor, 15);
	}

	/**
	 * @param factor
	 *            zoom factor (>1 for zoom in)
	 * @param steps
	 *            animation steps
	 */
	public void zoomInOut(double factor, int steps) {
		double px, py;
		if (mouseLoc != null) {
			px = mouseLoc.x;
			py = mouseLoc.y;
		} else {
			px = view.getWidth() / 2.0;
			py = view.getHeight() / 2.0;
		}
		zoomInOut(factor, steps, px, py);
	}

	/**
	 * Zoom around center.
	 * 
	 * @param factor
	 *            zoom factor (>1 for zoom in)
	 * @param steps
	 *            animation steps
	 * @param px
	 *            center x-coord
	 * @param py
	 *            center y-coord
	 */
	public void zoomInOut(double factor, int steps, double px, double py) {
		if (!allowZoom()) {
			return;
		}
		// make zooming a little bit smoother by having some steps
		view.setAnimatedCoordSystem(
				px, py, factor, view.getXscale() * factor, steps, false);
		app.setUnsaved();
	}

	/**
	 * @return whether zooming is allowed
	 */
	public boolean allowZoom() {
		return !app.isApplet() || (mode == EuclidianConstants.MODE_ZOOM_IN)
				|| (mode == EuclidianConstants.MODE_ZOOM_OUT)
				|| app.isShiftDragZoomEnabled();
	}

	/**
	 * @return application
	 */
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
		if (app.getGuiManager() != null) {
			app.getGuiManager().showPopupChooseGeo(selectedGeos1, hits, view,
					mouseLoc);
		}
	}

	/**
	 * @return pen mode controller
	 */
	public EuclidianPen getPen() {
		if (pen == null) {
			pen = new EuclidianPen(app, view);

		}
		return pen;
	}

	/**
	 * Make sure we start new penstroke.
	 */
	public void resetPen() {
		if (pen != null) {
			pen.resetPenOffsets();
		}
	}

	/**
	 * @return delete square size in px
	 */
	public int getDeleteToolSize() {
		EuclidianSettings settings = this.view.getSettings();
		if (settings != null) {
			return this.view.getSettings().getDeleteToolSize();
		}

		return EuclidianConstants.DEFAULT_ERASER_SIZE;
	}

	protected DialogManager getDialogManager() {
		return app.getDialogManager();
	}

	public ArrayList<GeoElement> getAppSelectedGeos() {
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
		// only needed in Web
	}

	/**
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param scaleFactor
	 *            zoom factor
	 */
	public void onPinchPhone(int x, int y, double scaleFactor) {
		double newX = x + (view.getXZeroOld() - twoTouchStartX) * scaleFactor;
		double newY = y + (view.getYZeroOld() - twoTouchStartY) * scaleFactor;
		view.setCoordSystem(newX, newY, view.getXScaleStart() * scaleFactor,
				view.getYScaleStart() * scaleFactor);
	}

	/**
	 * Animate zoom after pinch.
	 * 
	 * @param x
	 *            center x-coord
	 * @param y
	 *            center y-coord
	 * @param scaleFactor
	 *            zoom factor
	 */
	public void onPinch(int x, int y, double scaleFactor) {
		this.mouseLoc = new GPoint(x, y);
		zoomInOut(scaleFactor,
				scaleFactor < EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR ? 1 : 2, x,
				y);
	}

	public void twoTouchStart(double x1, double y1, double x2, double y2) {
		twoTouchStartCommon(x1, y1, x2, y2);
	}

	/**
	 * End multitouch.
	 */
	public void twoTouchEnd() {
		// assume that two touches moved the view
		draggingOccured = true;
		draggingBeyondThreshold = true;
	}

	/**
	 * Handle touch start.
	 * 
	 * @param e
	 *            touch start event
	 */
	public void touchStartPhone(AbstractEvent e) {
		this.mouseLoc = new GPoint(e.getX(), e.getY());

		if (view.wantsUpdatePreviewForTouchStartPhone(mode)) {
			view.setPreview(switchPreviewableForInitNewMode(mode));
			updatePreview();
			this.view.updatePreviewableForProcessMode();
		}

		this.view.setHits(mouseLoc, e.getType());
		wrapMousePressed(e);

		if (mode == EuclidianConstants.MODE_POLYGON
				|| mode == EuclidianConstants.MODE_RIGID_POLYGON
				|| mode == EuclidianConstants.MODE_VECTOR_POLYGON) {
			this.moveMode = EuclidianController.MOVE_NONE;
		}

		prepareModeForFreehand();
	}

	/**
	 * Handle touch end.
	 * 
	 * @param e
	 *            touch end event
	 */
	public void touchEndPhone(AbstractEvent e) {
		wrapMouseReleased(e);
		resetModeAfterFreehand();
		if (penMode(mode)) {
			app.refreshViews();
		}
		movePosition = null;

		hidePreviewForPhone();
	}

	protected void hidePreviewForPhone() {
		if (!(view.getPreviewDrawable() instanceof DrawPolyLine)
				&& !(view.getPreviewDrawable() instanceof DrawPolygon)) {
			view.setPreview(null);
		}
	}

	/**
	 * @param e
	 *            single touch move event
	 */
	public void touchMovePhone(AbstractEvent e) {
		if (shouldSetToFreehandMode()) {
			setModeToFreehand();
		}

		wrapMouseDragged(e, true);

		if (penMode(mode)) {
			view.repaint();
		}
	}

	static private boolean parentAlgoSecondInputIsFreeOrNotLabelSet(
			GeoElement geo) {
		AlgoElement algo = geo.getParentAlgorithm();
		if (algo == null) {
			return false;
		}

		if (algo.getInputLength() < 2) {
			return false;
		}

		GeoElement input = algo.input[1];
		return input.isIndependent() || !input.isLabelSet();
	}

	/**
	 * @param x1
	 *            first touch x-coord
	 * @param y1
	 *            first touch y-coord
	 * @param x2
	 *            second touch x-coord
	 * @param y2
	 *            second touch y-coord
	 */
	final public void twoTouchStartPhone(double x1, double y1, double x2,
			double y2) {
		scaleConic = null;

		if (this.view.getPreviewDrawable() != null) {
			mouseLoc = new GPoint((int) x1, (int) y1);
			movePosition = new GPoint((int) x2, (int) y2);
			return;
		}

		view.setHits(new GPoint((int) x1, (int) y1), PointerEventType.TOUCH);
		// needs to be copied, because the reference is changed in the next step
		Hits hits1 = new Hits();
		hits1.addAll(view.getHits());

		view.setHits(new GPoint((int) x2, (int) y2), PointerEventType.TOUCH);
		Hits hits2 = view.getHits();

		twoTouchStartX = (x1 + x2) / 2;
		twoTouchStartY = (y1 + y2) / 2;
		twoTouchStartDistance = MyMath.length(x1 - x2, y1 - y2);

		view.rememberOrigins();

		if (view.isAREnabled()) {
			return;
		}

		if (hits1.hasYAxis() && hits2.hasYAxis()) {
			multitouchMode = ScaleMode.zoomY;
			setOldDistance(y1 - y2);
			scale = view.getYscale();
		} else if (hits1.hasXAxis() && hits2.hasXAxis()) {
			multitouchMode = ScaleMode.zoomX;
			setOldDistance(x1 - x2);
			scale = this.view.getXscale();
		} else if (hits1.size() > 0 && hits2.size() > 0
				&& hits1.get(0) == hits2.get(0)
				&& hits1.get(0) instanceof GeoConic
				// isClosedPath: true for circle and ellipse
				&& ((GeoConic) hits1.get(0)).isClosedPath()
				&& ((hits1.get(0).getFreeInputPoints(view) != null
						&& hits1.get(0).getFreeInputPoints(view).size() >= 2)
						|| parentAlgoSecondInputIsFreeOrNotLabelSet(
								hits1.get(0)))) {
			scaleConic = (GeoConic) hits1.get(0);
			// TODO: select scaleConic

			if (hits1.get(0).getFreeInputPoints(this.view).size() >= 3) {
				multitouchMode = ScaleMode.circle3Points;
			} else if (hits1.get(0).getFreeInputPoints(this.view).size() == 2) {
				multitouchMode = ScaleMode.circle2Points;
			} else {
				AlgoElement algo = scaleConic.getParentAlgorithm();
				if (algo instanceof AlgoCirclePointRadius) {
					AlgoCirclePointRadius algoCirclePointRadius = (AlgoCirclePointRadius) algo;
					GeoElement radiusGeo = algoCirclePointRadius.getRadiusGeo();
					if (radiusGeo instanceof GeoNumeric
							&& radiusGeo.isIndependent()) {
						multitouchMode = ScaleMode.circleRadius;
						circleRadius = (GeoNumeric) radiusGeo;
						this.originalRadius = circleRadius.getDouble();
					}
				}
			}
			twoTouchStartCommon(x1, y1, x2, y2);

			midpoint = new double[] { scaleConic.getMidpoint().getX(),
					scaleConic.getMidpoint().getY() };

			ArrayList<GeoPointND> points = scaleConic
					.getFreeInputPoints(this.view);
			originalPointX = new double[points.size()];
			originalPointY = new double[points.size()];
			for (int i = 0; i < points.size(); i++) {
				originalPointX[i] = points.get(i).getCoords().getX();
				originalPointY[i] = points.get(i).getCoords().getY();
			}
		} else {
			if (shouldClearSelectionForMove()) {
				clearSelections();
			}
			multitouchMode = ScaleMode.view;
			twoTouchStartCommon(x1, y1, x2, y2);
		}
	}

	final public void twoTouchStartCommon(double x1, double y1, double x2,
			double y2) {
		this.setOldDistance(MyMath.length(x1 - x2, y1 - y2));
	}

	public void twoTouchMove(double x1, double y1, double x2, double y2) {
		twoTouchMoveCommon(x1, y1, x2, y2);
	}

	/**
	 *
	 * @param x1
	 *            x-coord
	 * @param y1
	 *            y-coord
	 * @return wrapped event
	 */
	public AbstractEvent createTouchEvent(int x1, int y1) {
		return null;
	}

	/**
	 * @param x1d
	 *            x-coord of the first touch
	 * @param y1d
	 *            y-coord of the first touch
	 * @param x2d
	 *            x-coord of the second touch
	 * @param y2d
	 *            y-coord of the second touch
	 */
	final public void twoTouchMovePhone(double x1d, double y1d, double x2d,
			double y2d) {
		int x1 = (int) x1d;
		int x2 = (int) x2d;
		int y1 = (int) y1d;
		int y2 = (int) y2d;

		if (movePosition != null) {
			// the view is moved while an element with preview is constructed
			// (e.g. a line)

			// move the view
			view.rememberOrigins();
			view.translateCoordSystemInPixels(x2 - movePosition.getX(),
					y2 - movePosition.getY(), 0);
			movePosition = new GPoint(x2, y2);

			// update the preview
			mouseLoc = new GPoint(x1, y1);
			updatePreview();
			this.view.updatePreviewableForProcessMode();

			// make sure the preview is redrawn
			AbstractEvent e = createTouchEvent(x1, y1);
			wrapMouseDragged(e, true);
			return;
		}

		if ((x1 == x2 && y1 == y2) || this.getOldDistance() == 0) {
			return;
		}

		if (view.isAREnabled()) {
			return;
		}

		switch (multitouchMode) {
		case zoomY:
			if (scale == 0) {
				return;
			}
			double newRatioY = scale * (y1 - y2) / this.getOldDistance();
			view.setCoordSystem(view.getXZero(), view.getYZero(),
					view.getXscale(), newRatioY);
			break;
		case zoomX:
			if (scale == 0) {
				return;
			}
			double newRatioX = scale * (x1 - x2) / getOldDistance();
			view.setCoordSystem(view.getXZero(), view.getYZero(), newRatioX,
					view.getYscale());
			break;
		case circle3Points:
			double dist = MyMath.length(x1 - x2, y1 - y2);
			scale = dist / getOldDistance();
			int i = 0;

			for (GeoPointND p : scaleConic.getFreeInputPoints(view)) {
				double newX = midpoint[0]
						+ (originalPointX[i] - midpoint[0]) * scale;
				double newY = midpoint[1]
						+ (originalPointY[i] - midpoint[1]) * scale;
				p.setCoords(newX, newY, 1.0);
				p.updateCascade();
				i++;
			}
			kernel.notifyRepaint();
			break;
		case circle2Points:
			double dist2P = MyMath.length(x1 - x2, y1 - y2);
			scale = dist2P / getOldDistance();

			// index 0 is the midpoint, index 1 is the point on the circle
			ArrayList<GeoPointND> points = scaleConic.getFreeInputPoints(view);

			if (points.size() > 1) {
				GeoPointND p = scaleConic.getFreeInputPoints(view).get(1);
				double newX = midpoint[0] + (originalPointX[1] - midpoint[0]) * scale;
				double newY = midpoint[1] + (originalPointY[1] - midpoint[1]) * scale;
				p.setCoords(newX, newY, 1.0);
				p.updateCascade();
				kernel.notifyRepaint();
			}
			break;
		case circleRadius:
			double distR = MyMath.length(x1 - x2, y1 - y2);
			scale = distR / getOldDistance();

			circleRadius.setValue(scale * originalRadius);
			circleRadius.updateCascade();

			kernel.notifyRepaint();
			break;
		default:
			// pinch
			double distance = MyMath.length(x1 - x2, y1 - y2);
			onPinchPhone((x1 + x2) / 2, (y1 + y2) / 2,
					distance / twoTouchStartDistance);
		}
	}

	/**
	 * @param x1
	 *            x-coord of the first touch
	 * @param y1
	 *            y-coord of the first touch
	 * @param x2
	 *            x-coord of the second touch
	 * @param y2
	 *            y-coord of the second touch
	 */
	final public void twoTouchMoveCommon(double x1, double y1, double x2,
			double y2) {
		int centerX, centerY;
		double newDistance;

		centerX = (int) (x1 + x2) / 2;
		centerY = (int) (y1 + y2) / 2;

		if (this.getOldDistance() > 0) {
			newDistance = MyMath.length(x1 - x2, y1 - y2);

			if (Math.abs(newDistance
					- this.getOldDistance()) > MINIMAL_PIXEL_DIFFERENCE_FOR_ZOOM) {
				onPinch(centerX, centerY, newDistance / this.getOldDistance());
				this.setOldDistance(newDistance);
			}
		}
	}

	public boolean isExternalHandling() {
		return externalHandling;
	}

	public void setExternalHandling(boolean b) {
		this.externalHandling = b;
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

	public final PointerEventType getDefaultEventType() {
		return this.defaultEventType;
	}

	/**
	 * @param pointerEventType
	 *            default event type
	 * @param down
	 *            whether current event is a pointer down event
	 */
	public final void setDefaultEventType(PointerEventType pointerEventType,
			boolean down) {
		if (app.has(Feature.MOW_PEN_EVENTS)) {
			if (pointerEventType == PointerEventType.PEN
					&& pointerEventType != defaultEventType
					&& app.getMode() == EuclidianConstants.MODE_MOVE) {
				app.setMode(EuclidianConstants.MODE_PEN, ModeSetter.DOCK_PANEL);
			}
			if (down && app.getMode() == EuclidianConstants.MODE_PEN
					&& pointerEventType != PointerEventType.PEN
					&& PointerEventType.PEN == defaultEventType) {
				setTempMode(EuclidianConstants.MODE_MOVE);
			}
		}
		this.defaultEventType = pointerEventType;
	}

	private void setTempMode(int modePen) {
		temporaryMode = true;
		oldMode = mode;
		oldEventType = defaultEventType;
		view.setMode(modePen, ModeSetter.DOCK_PANEL);
	}

	private void moveAttachDetach(boolean repaint, AbstractEvent event) {

		if (movedGeoPoint == null) {
			return;
		}

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
		Hits hits = view.getHits().cloneHits();
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
			selection.addToSelectionList(getSelectedPathList(),
					movedGeoPoint.getPath(), 1);
			movedGeoPoint.removePath();
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
			selection.addToSelectionList(getSelectedRegionList(),
					movedGeoPoint.getRegion(), 1);
			movedGeoPoint.setRegion(null);
			movedGeoPoint.setCoords(view.toRealWorldCoordX(event.getX()),
					view.toRealWorldCoordY(event.getY()), 1);
		} else {
			for (int i = hits.size() - 1; i >= 0; i--) {
				if (hits.get(i).isChildOf(movedGeoPoint)) {
					hits.remove(i);
				}
			}

			addSelectedPath(hits, 1, false, false);
			if (getSelectedPathList().size() > 0) {
				// moved point to a Path -> attach
				needsAttach = true;
				movedGeoPoint.setPath(getSelectedPathList().get(0));
				movedGeoPoint.setCoords(view.toRealWorldCoordX(event.getX()),
						view.toRealWorldCoordY(event.getY()), 1);
			} else {
				// move point
				companion.movePoint(repaint, event);
				// already includes updateCascade
				return;
			}
		}
		movedGeoPoint.updateCascade();
	}

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

	/**
	 * rest all the settings that have been changed in setModeToFreehand().
	 *
	 * no effect if setModeToFreehand() has not been called or had no effect
	 * (e.g. because the selected tool is not supported)
	 */
	public void resetModeAfterFreehand() {
		if (freehandModePrepared) {
			freehandModePrepared = false;
			pen = null;
			resetModeAfterFreehandClearCanvas();
		}
		if (freehandModeSet) {
			freehandModeSet = false;
			this.mode = previousMode;
			moveMode = MOVE_NONE;
			view.setPreview(switchPreviewableForInitNewMode(this.mode));
			pen = null;
			this.previousMode = -1;
			this.view.repaint();
		}
	}

	protected void resetModeAfterFreehandClearCanvas() {
		// DO NOT REMOVE: USED IN ANDROID
	}

	/**
	 * Prepare freehand pen for specific mode.
	 */
	public void prepareModeForFreehand() {
		if (getSelectedPointList().size() != 0) {
			// make sure to switch only for the first point
			return;
		}

		// defined at the beginning, because it is modified for some modes
		GeoPoint point = (GeoPoint) this.view.getHits()
				.getFirstHit(TestGeo.GEOPOINT);
		if (point == null && this.movedGeoPoint instanceof GeoPoint) {
			point = (GeoPoint) this.movedGeoPoint;
		}

		switch (this.mode) {
		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
			this.pen = new EuclidianPenFreehand(app, view);
			((EuclidianPenFreehand) pen)
					.setExpected(ShapeType.circleThreePoints);

			// the point will be deleted if no circle can be built, therefore
			// make sure that only a newly created point is set
			point = (this.pointCreated != null)
					&& movedGeoPoint instanceof GeoPoint
							? (GeoPoint) movedGeoPoint : null;
			break;
		case EuclidianConstants.MODE_POLYGON:
			this.pen = new EuclidianPenFreehand(app, view);
			((EuclidianPenFreehand) pen).setExpected(ShapeType.polygon);
			break;
		case EuclidianConstants.MODE_RIGID_POLYGON:
			this.pen = new EuclidianPenFreehand(app, view);
			((EuclidianPenFreehand) pen).setExpected(ShapeType.rigidPolygon);
			break;
		case EuclidianConstants.MODE_VECTOR_POLYGON:
			this.pen = new EuclidianPenFreehand(app, view);
			((EuclidianPenFreehand) pen).setExpected(ShapeType.vectorPolygon);
			break;
		default:
			return;
		}
		freehandModePrepared = true;
		pen.setInitialPoint(point, point != null && point.equals(pointCreated));
	}

	/**
	 * sets the mode to freehand_shape with an expected shape depending on the
	 * actual mode (has no effect if no mode is set that can be turned into
	 * freehand_shape)
	 *
	 * For some modes requires that view.setHits(...) has been called with the
	 * correct parameters or movedGeoPoint is set correct in order to use other
	 * GeoPoints (e.g. as the first point of a polygon). Also pointCreated needs
	 * to be set correctly.
	 *
	 */
	protected void setModeToFreehand() {
		// only executed if one of the specified modes is set
		this.previousMode = this.mode;
		this.mode = EuclidianConstants.MODE_FREEHAND_SHAPE;
		moveMode = MOVE_NONE;
		freehandModeSet = true;
	}

	/**
	 * @param e
	 *            touch start / mouse down event
	 */
	public void onPointerEventStart(AbstractEvent e) {
		// not used in common, overwritten for other projects
	}

	/**
	 *
	 * @return currently moved geo
	 */
	public GeoElement getMovedGeoElement() {
		return movedGeoElement;
	}

	/**
	 * necessary for webSimple, to exclude new focus
	 *
	 * @return whether dropdown list is focused
	 */
	public boolean isComboboxFocused() {
		return false;
	}

	protected void switchModeForMousePressed(AbstractEvent e) {
		startPosition = new GPoint(e.getX(), e.getY());

		switchModeForMousePressedND(e);

		if (this.selPoints() == 0 && (isModeCreatingObjectsByDrag()
				|| this.mode == EuclidianConstants.MODE_CIRCLE_POINT_RADIUS)) {
			this.mouseLoc = new GPoint(e.getX(), e.getY());
			this.view.setHits(this.mouseLoc, e.getType());

			if (mode != EuclidianConstants.MODE_CIRCLE_POINT_RADIUS) {
				wrapMouseReleasedND(e, false);
				e.release();
			}

			if (this.mode == EuclidianConstants.MODE_REGULAR_POLYGON
					&& this.view.getPreviewDrawable() == null) {
				this.view.setPreview(
						view.createPreviewSegment(getSelectedPointList()));
			}

			if (this.mode == EuclidianConstants.MODE_CIRCLE_POINT_RADIUS
					&& this.view.getPreviewDrawable() == null
					&& view.getHits().containsGeoPoint()) {
				firstSelectedPoint = (GeoPointND) view.getHits()
						.getFirstHit(TestGeo.GEOPOINTND);
				ArrayList<GeoPointND> list = new ArrayList<>();
				list.add(firstSelectedPoint);
				this.view.setPreview(view.createPreviewConic(this.mode, list));
			}

			this.updatePreview();
			this.view.updatePreviewableForProcessMode();
		}
	}

	private boolean isModeCreatingObjectsByDrag() {
		return this.mode == EuclidianConstants.MODE_JOIN
				|| this.mode == EuclidianConstants.MODE_SEGMENT
				|| this.mode == EuclidianConstants.MODE_RAY
				|| this.mode == EuclidianConstants.MODE_VECTOR
				|| this.mode == EuclidianConstants.MODE_CIRCLE_TWO_POINTS
				|| this.mode == EuclidianConstants.MODE_SEMICIRCLE
				|| this.mode == EuclidianConstants.MODE_REGULAR_POLYGON;
	}

	/**
	 * Store undo point for mode.
	 */
	public void storeUndoInfo() {
		// store undo info and state if we use the tool once again
		int m = temporaryMode ? oldMode : mode;
		app.storeUndoInfoAndStateForModeStarting(
				!EuclidianConstants.isMoveOrSelectionMode(m));
	}

	protected GeoElement[] extremum(Hits hits, boolean selPreview) {
		// find a function
		addSelectedFunction(hits, 1, false, selPreview);

		if (selFunctions() > 0) {
			Construction cons = kernel.getConstruction();

			// get the function and clear the selection
			GeoFunction function = getSelectedFunctions()[0];
			// not for rootfinding: x*sqrt(1-x^2) does not have polynomial
			// derivative
			if (function.isPolynomialFunction(false)) {
				// calculates all extremum points (e.g. x^2)
				AlgoExtremumPolynomial algo = new AlgoExtremumPolynomial(cons,
						null, function, true);
				return algo.getExtremumPoints();
			}

			// special case for If
			// non-polynomial -> undefined
			// eg f(x) = x^2 , (-2<x<2)
			ExpressionNode exp = function.getFunctionExpression();
			if (exp.getOperation().isIf()) {

				AlgoExtremumPolynomialInterval algo = new AlgoExtremumPolynomialInterval(
						cons, null, function);
				return algo.getRootPoints();

			}

			// calculates only the extremum points that are visible at the
			// moment (e.g. for sin(x))
			AlgoExtremumMulti algo = new AlgoExtremumMulti(cons, null, function,
					this.view);
			return algo.getExtremumPoints();
		}

		// else (no functions selected)
		addSelectedConic(hits, 1, false, selPreview);

		if (selConics() > 0) {
			GeoConic conic = getSelectedConics()[0];

			AlgoVertexConic algo = new AlgoVertexConic(kernel.getConstruction(),
					null, conic);

			kernel.getConstruction().addToConstructionList(algo,
					kernel.getConstruction().steps());

			return algo.getOutput();
		}

		return null;
	}

	protected GeoElement[] roots(Hits hits, boolean selPreview) {
		// find a function
		addSelectedFunction(hits, 1, false, selPreview);

		GeoFunctionable function = null;
		if (selFunctions() > 0) {
			// get the function and clear the selection
			function = getSelectedFunctions()[0];
		} else {
			addSelectedConic(hits, 1, false, selPreview);

			if (selConics() > 0) {
				GeoConic conic = getSelectedConics()[0];

				GeoLine line = kernel.getXAxis();

				AlgoIntersectLineConic algo = new AlgoIntersectLineConic(
						kernel.getConstruction(), line, conic);

				kernel.getConstruction().addToConstructionList(algo,
						kernel.getConstruction().steps());

				GeoElement[] ret = algo.getOutput();

				// make sure they get in the construction properly
				for (GeoElement geo : ret) {
					geo.setLabel(null);
				}

				return ret;
			}
			// if no function was found, test for lines
			addSelectedLine(hits, 1, false, selPreview);
			if (selLines() > 0) {
				// get the line and clear the selection
				function = getSelectedLines()[0];
			}
		}

		if (function != null) {
			Construction cons = kernel.getConstruction();

			if (function.isPolynomialFunction(true)) {
				// calculates all root points (e.g. x^2 - 1)
				AlgoRootsPolynomial algo = new AlgoRootsPolynomial(cons, null,
						function, true);
				return algo.getRootPoints();
			}

			// special case for If
			// non-polynomial -> undefined
			// eg f(x) = x^2 , (-2<x<2)
			ExpressionNode exp = function.getFunction()
					.getFunctionExpression();
			if (exp.getOperation().isIf()) {

				AlgoRootsPolynomialInterval algo = new AlgoRootsPolynomialInterval(
						cons, null, function);
				return algo.getRootPoints();
			}

			// calculates only the root points that are visible at the moment
			// (e.g. for sin(x))
			AlgoRoots algo = new AlgoRoots(this.kernel.getConstruction(), null,
					function, this.view);
			return algo.getRootPoints();
		}

		return null;
	}

	/**
	 * @param type
	 *            used in Web
	 */
	public void closePopups(int x, int y, PointerEventType type) {
		app.closePopups(x, y);
	}

	protected ArrayList<GeoPointND> getSelectedPointList() {
		return selection.getSelectedPointList();
	}

	protected ArrayList<GeoNumeric> getSelectedNumberList() {
		return selection.getSelectedNumberList();
	}

	protected ArrayList<GeoNumberValue> getSelectedNumberValueList() {
		return selection.getSelectedNumberValueList();
	}

	protected ArrayList<GeoLineND> getSelectedLineList() {
		return selection.getSelectedLineList();
	}

	protected ArrayList<Path> getSelectedPathList() {
		return selection.getSelectedPathList();
	}

	protected ArrayList<GeoConicND> getSelectedConicNDList() {
		return selection.getSelectedConicNDList();
	}

	protected ArrayList<GeoDirectionND> getSelectedDirectionList() {
		return selection.getSelectedDirectionList();
	}

	protected ArrayList<GeoSegmentND> getSelectedSegmentList() {
		return selection.getSelectedSegmentList();
	}

	protected ArrayList<Region> getSelectedRegionList() {
		return selection.getSelectedRegionList();
	}

	protected ArrayList<GeoImplicit> getSelectedImplicitpolyList() {
		return selection.getSelectedImplicitpolyList();
	}

	protected ArrayList<GeoImplicitSurfaceND> getSelectedImplicitSurfaceList() {
		return selection.getSelectedImplicitSurfaceList();
	}

	protected ArrayList<GeoFunction> getSelectedFunctionList() {
		return selection.getSelectedFunctionList();
	}

	protected ArrayList<GeoFunctionNVar> getSelectedFunctionNVarList() {
		return selection.getSelectedFunctionNVarList();
	}

	protected ArrayList<GeoCurveCartesian> getSelectedCurveList() {
		return selection.getSelectedCurveList();
	}

	protected ArrayList<GeoVectorND> getSelectedVectorList() {
		return selection.getSelectedVectorList();
	}

	protected ArrayList<GeoPolygon> getSelectedPolygonList() {
		return selection.getSelectedPolygonList();
	}

	protected ArrayList<GeoPolyLine> getSelectedPolyLineList() {
		return selection.getSelectedPolyLineList();
	}

	protected ArrayList<GeoElement> getSelectedGeoList() {
		return selection.getSelectedGeoList();
	}

	protected ArrayList<GeoList> getSelectedListList() {
		return selection.getSelectedListList();
	}

	public EuclidianView getView() {
		return view;
	}

	/**
	 * set the view attached to this
	 *
	 * @param view
	 *            view
	 */
	public void setView(EuclidianView view) {
		this.view = view;
	}

	public GPoint getMovePosition() {
		return movePosition;
	}

	public void setMovePosition(GPoint pos) {
		movePosition = pos;
	}

	/**
	 * different modes of a multitouch-event
	 */
	protected enum ScaleMode {
		/**
		 * scale x-axis (two TouchStartEvents on the x-axis)
		 */
		zoomX,
		/**
		 * scale y-axis (two TouchStartEvents on the y-axis)
		 */
		zoomY,
		/**
		 * scale a circle or ellipsis with three points or an ellipsis with 5
		 * points
		 */
		circle3Points,
		/**
		 * scale a circle with 2 points
		 */
		circle2Points,
		/**
		 * scale a circle given with midpoint and a number-input as radius
		 */
		circleRadius,
		/**
		 * zooming
		 */
		view
	}

	private void filterHits(Inspecting filter) {
			for (int i = 1; i < getView().getHits().size(); i++) {
			if (!filter.check(getView().getHits().get(i))) {
					return;
				}
				getView().getHits().remove(i);
			}
	}

	public MouseTouchGestureController getEuclidianTouchGestureListener() {
		return null;
	}

	/**
	 * Remove all references to geo
	 * 
	 * @param geo
	 *            construction element
	 */
	public void clear(GeoElement geo) {
		handleAddSelectedArrayList.remove(geo);
		highlightedGeos.remove(geo);
		tempArrayList.remove(geo);
		tempRegionHitsArrayList.remove(geo);
		removeFromPen(geo);
	}

	private void removeFromPen(GeoElement geo) {
		if (pen != null) {
			pen.remove(geo);
		}
	}

	public boolean isObjectMenuActive() {
		return objectMenuActive;
	}

	public void setObjectMenuActive(boolean objectMenuActive) {
		this.objectMenuActive = objectMenuActive;
	}

	/**
	 * Notify coord system listeners.
	 */
	public void notifyCoordSystemListeners() {
		for (CoordSystemListener listener: zoomerListeners) {
			if (listener != null) {
				listener.onCoordSystemChanged();
			}
		}
	}

	/**
	 * @param coordSystemListener
	 *            coord system listener
	 */
	public void addZoomerListener(CoordSystemListener coordSystemListener) {
		zoomerListeners.add(coordSystemListener);
	}

	/**
	 * @param coordSystemListener
	 *            coord system listener
	 */
	public void removeZoomerListener(CoordSystemListener coordSystemListener) {
		zoomerListeners.remove(coordSystemListener);
	}

	public void onCoordSystemChanged() {
		notifyCoordSystemListeners();
	}

	public MyModeChangedListener getModeChangeListener() {
		return modeChangeListener;
	}

	public void setModeChangeListener(MyModeChangedListener modeChangeListener) {
		this.modeChangeListener = modeChangeListener;
	}

	public void addPointerUpCallback(Runnable callback) {
		this.pointerUpCallback = callback;
	}

	/**
	 * Show popup when user clicks on the preview Special Point in EV
	 *
	 * @param previewPoints
	 *            preview points
	 */
	protected void showSpecialPointPopup(ArrayList<GeoElement> previewPoints) {
		// Should be implemented in subclass
	}

	/**
	 * Hide preview point popup
	 */
	protected void hideSpecialPointPopup() {
		// Should be implemented in subclass
	}

	public void setLastMouseUpLoc(GPoint lastMouseUpLoc) {
		this.lastMouseUpLoc = lastMouseUpLoc;
	}

	public void setDraggingDelay(long i) {
		this.draggingDelay = i;
	}

	@Override
	public void specialPointsChanged(SpecialPointsManager manager, List<GeoElement> specialPoints) {
		if (specialPoints == null) {
			previewPointHits.clear();
		}
	}

	private void setBoundingBoxCursor(Drawable drawable) {
		EuclidianBoundingBoxHandler nrHandler = view.getHitHandler();
		EuclidianCursor cursor = BoundingBox.getCursor(nrHandler, drawable);
		if (cursor != null) {
			view.setCursor(cursor);
		}
	}

	/**
	 * Calculate and set united bounding box for a list of GeoElements
	 *
	 * @param geos
	 *            list of GeoElements
	 */
	public void setBoundingBoxFromList(ArrayList<GeoElement> geos) {
		// do not update during rotation
		if (view.getHitHandler() == EuclidianBoundingBoxHandler.ROTATION) {
			return;
		}
		// init min/max vars
		double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY,
				maxX = Double.NEGATIVE_INFINITY,
				maxY = Double.NEGATIVE_INFINITY;
		boolean hasRotationHandler = true;
		// calc min/max from geos
		for (GeoElement geo : geos) {
			Drawable dr = ((Drawable) view.getDrawableFor(geo));
			if (dr != null) {
				if (!dr.hasRotationHandler()) {
					hasRotationHandler = false;
				}
				GRectangle2D bounds = dr.getBoundingBox() != null
						? dr.getBoundingBox().getRectangle()
						: dr.getBounds();
				if (bounds != null) {
					if (bounds.getMinX() < minX) {
						minX = bounds.getMinX();
					}
					if (bounds.getMaxX() > maxX) {
						maxX = bounds.getMaxX();
					}
					if (bounds.getMinY() < minY) {
						minY = bounds.getMinY();
					}
					if (bounds.getMaxY() > maxY) {
						maxY = bounds.getMaxY();
					}
				}
			}
		}
		// create union bounding box
		GRectangle rect = AwtFactory.getPrototype().newRectangle((int) minX,
				(int) minY, (int) (maxX - minX), (int) (maxY - minY));
		view.setBoundingBox(new BoundingBox(rect, false,
				hasRotationHandler));
	}

	/**
	 * 
	 * @return if a popup or a floating menu just closed.
	 */
	public boolean isPopupJustClosed() {
		return popupJustClosed;
	}

	/**
	 * Set this to indicate if a popup or menu is just closed.
	 * 
	 * @param value
	 *            to set.
	 */
	public void setPopupJustClosed(boolean value) {
		this.popupJustClosed = value;
	}

	public void resetPointCreated() {
		this.pointCreated = null;
	}

	public double getOldDistance() {
		return oldDistance;
	}

	public void setOldDistance(double oldDistance) {
		this.oldDistance = oldDistance;
	}

	public long getLastMousePressedTime() {
	    return lastMousePressedTime;
    }

    public void setLastMousePressedTime(long time) {
        lastMousePressedTime = time;
    }

    public long getElapsedTimeFromLastMousePressed() {
        return System.currentTimeMillis() - lastMousePressedTime;
    }
}
