/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.euclidian;

import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.kernel.AlgoDynamicCoordinates;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.AlgoPolygon;
import geogebra.kernel.Construction;
import geogebra.kernel.Dilateable;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoAxis;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoButton;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoConicPart;
import geogebra.kernel.GeoCurveCartesian;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoFunctionable;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoLocus;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolyLine;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.kernel.Path;
import geogebra.kernel.PointProperties;
import geogebra.kernel.PointRotateable;
import geogebra.kernel.Region;
import geogebra.kernel.Transformable;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.implicit.GeoImplicitPoly;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoSegmentND;
import geogebra.kernel.kernelND.GeoVectorND;
import geogebra.main.Application;
import geogebra.main.GeoElementSelectionListener;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.text.JTextComponent;

/**
 * EuclidianController.java
 *
 * Created on 16. October 2001, 15:41
 */
public class EuclidianController implements MouseListener,
MouseMotionListener, MouseWheelListener, ComponentListener, PropertiesPanelMiniListener {

	private int mx, my; // keep track of mouse position
	
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

	protected static final int MOVE_NUMERIC = 110; // for number on slider

	protected static final int MOVE_SLIDER = 111; // for slider itself

	protected static final int MOVE_IMAGE = 112;

	protected static final int MOVE_ROTATE = 113;

	protected static final int MOVE_DEPENDENT = 114;

	protected static final int MOVE_MULTIPLE_OBJECTS = 115; // for multiple objects

	protected static final int MOVE_X_AXIS = 116;
	protected static final int MOVE_Y_AXIS = 117;

	protected static final int MOVE_BOOLEAN = 118; // for checkbox moving
	protected static final int MOVE_BUTTON = 119; 

	public static final int MOVE_ROTATE_VIEW = 120; // for 3D 
	
	protected static final int MOVE_IMPLICITPOLY = 121;

	protected Application app;

	protected Kernel kernel;

	protected EuclidianViewInterface view;
	//protected EuclidianView view;

	protected Point startLoc, mouseLoc, lastMouseLoc; // current mouse location

	//protected double xZeroOld, yZeroOld;
	protected double xTemp, yTemp;

	protected Point oldLoc = new Point();

	protected double xRW;

	protected double yRW;

	double xRWold = Double.NEGATIVE_INFINITY, yRWold = xRWold, temp;

	// for moving conics:
	protected Point2D.Double startPoint = new Point2D.Double();

	protected boolean useLineEndPoint = false;
	protected Point2D.Double lineEndPoint = null;

	protected Point selectionStartPoint = new Point();

	protected GeoConic tempConic;
	
	protected GeoImplicitPoly tempImplicitPoly;
	protected ArrayList<Double> tempDependentPointX;
	protected ArrayList<Double> tempDependentPointY;
	protected ArrayList<GeoPoint> moveDependentPoints;

	protected GeoFunction tempFunction;

	// protected GeoVec2D b;

	protected GeoPointND movedGeoPoint;
	protected boolean movedGeoPointDragged = false;

	protected GeoLine movedGeoLine;

	//protected GeoSegment movedGeoSegment;

	protected GeoConic movedGeoConic;
	
	protected GeoImplicitPoly movedGeoImplicitPoly;

	protected GeoVector movedGeoVector;

	protected GeoText movedGeoText;

	protected GeoImage oldImage, movedGeoImage;	

	protected GeoFunction movedGeoFunction;

	protected GeoNumeric movedGeoNumeric;
	protected boolean movedGeoNumericDragged = false;

	protected GeoBoolean movedGeoBoolean;

	protected GeoButton movedGeoButton;

	protected GeoElement movedLabelGeoElement;

	protected GeoElement movedGeoElement;
	
	protected GeoElement recordObject = null;

	protected GeoElement rotGeoElement, rotStartGeo;
	protected GeoPoint rotationCenter;
	protected MyDouble tempNum;
	protected double rotStartAngle;
	protected ArrayList translateableGeos;
	protected Coords translationVec;

	protected Hits tempArrayList = new Hits();
	protected Hits tempArrayList2 = new Hits();
	protected Hits tempArrayList3 = new Hits();
	protected ArrayList selectedPoints = new ArrayList();

	protected ArrayList selectedNumbers = new ArrayList();
	protected ArrayList selectedNumberValues = new ArrayList();

	protected ArrayList selectedLines = new ArrayList();
	protected ArrayList<GeoDirectionND> selectedDirections = new ArrayList<GeoDirectionND>();

	protected ArrayList selectedSegments = new ArrayList();

	protected ArrayList selectedRegions = new ArrayList();
	protected ArrayList selectedPaths = new ArrayList();
	protected ArrayList selectedConicsND = new ArrayList<GeoConicND>();
	protected ArrayList selectedImplicitpoly = new ArrayList();

	protected ArrayList selectedFunctions = new ArrayList();
	protected ArrayList selectedCurves = new ArrayList();

	protected ArrayList selectedVectors = new ArrayList();

	protected ArrayList selectedPolygons = new ArrayList();
	protected ArrayList selectedPolyLines = new ArrayList();

	protected ArrayList<GeoElement> selectedGeos = new ArrayList<GeoElement>();
	protected ArrayList selectedLists = new ArrayList();

	//protected LinkedList highlightedGeos = new LinkedList();
	protected Hits highlightedGeos = new Hits();
	
	protected ArrayList<GeoElement> justCreatedGeos = new ArrayList<GeoElement>();

	protected boolean selectionPreview = false;
	public boolean hideIntersection = false;
	public boolean previewFromResultedGeo = false;
	public GeoElement resultedGeo;

	protected boolean TEMPORARY_MODE = false; // changed from QUICK_TRANSLATEVIEW Michael Borcherds 2007-10-08

	protected boolean DONT_CLEAR_SELECTION = false; // Michael Borcherds 2007-12-08

	protected boolean DRAGGING_OCCURED = false; // for moving objects

	protected boolean POINT_CREATED = false;

	protected boolean moveModeSelectionHandled;

	protected boolean highlightJustCreatedGeos = true;

	protected ArrayList<GeoElement> pastePreviewSelected = null;
	protected ArrayList<GeoElement> pastePreviewSelectedAndDependent;

	//protected MyPopupMenu popupMenu;

	protected int mode, oldMode, moveMode = MOVE_NONE;
	protected Macro macro;
	protected Class [] macroInput;

	protected int DEFAULT_INITIAL_DELAY;

	protected boolean toggleModeChangedKernel = false;

	boolean altDown=false;
	
	//boolean polygonRigid = false;
	
	private static int POLYGON_NORMAL = 0;
	private static int POLYGON_RIGID = 1;
	private static int POLYGON_VECTOR = 2;
	int polygonMode = POLYGON_NORMAL;

	// used for gridlock when dragging polygons, segments etc
	double[] transformCoordsOffset = new double[2];

	boolean allowSelectionRectangleForTranslateByVector = true;
	
	int previousPointCapturing;

	// just for the mergeStickyPointsAfterPaste method
	ArrayList<GeoPointND> persistentStickyPointList = new ArrayList<GeoPointND>();
	
	
	/*********************************************** 
	 * Creates new EuclidianController 
	 **********************************************/
	public EuclidianController(Kernel kernel) {
		setKernel(kernel);
		setApplication(kernel.getApplication());

		// for tooltip manager
		DEFAULT_INITIAL_DELAY = ToolTipManager.sharedInstance()
		.getInitialDelay();

		tempNum = new MyDouble(kernel);
	}


	public void setApplication(Application app) {
		this.app=app;
	}

	public Application getApplication() {
		return app;
	}

	public void setKernel(Kernel kernel) {
		this.kernel=kernel;
	}

	public Kernel getKernel() {
		return kernel;
	}

	protected void setView(EuclidianViewInterface view) {
		//void setView(EuclidianView view) {
		this.view = view;
	}

	//==============================================
	// Paste preview

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
						startPoint.setLocation(((GeoPoint)geo).inhomX, ((GeoPoint)geo).inhomY);
						firstMoveable = false;
					} else if (geo.isGeoText()) {
						if (((GeoText)geo).hasAbsoluteLocation()) {
							GeoPoint loc = (GeoPoint) ((GeoText)geo).getStartPoint();
							startPoint.setLocation(loc.inhomX, loc.inhomY);
							firstMoveable = false;
						}
					} else if (geo.isGeoNumeric()) {
						if (!((GeoNumeric)geo).isAbsoluteScreenLocActive()) {
							startPoint.setLocation(((GeoNumeric)geo).getRealWorldLocX(), ((GeoNumeric)geo).getRealWorldLocY());
							firstMoveable = false;
						} else {
							startPoint.setLocation(view.toRealWorldCoordX(((GeoNumeric)geo).getAbsoluteScreenLocX()), view.toRealWorldCoordY(((GeoNumeric)geo).getAbsoluteScreenLocY()));
							firstMoveable = false;
						}
					} else if (geo.isGeoImage()) {
						if (((GeoImage)geo).hasAbsoluteLocation()) {
							GeoPoint loc = (GeoPoint)((GeoImage)geo).getStartPoints()[2];
							if (loc != null) { // top left defined
								//transformCoordsOffset[0]=loc.inhomX-xRW;
								//transformCoordsOffset[1]=loc.inhomY-yRW;
								startPoint.setLocation(loc.inhomX, loc.inhomY);
								firstMoveable = false;
							} else {
								loc = (GeoPoint) ((GeoImage)geo).getStartPoint();
								if (loc != null) { // bottom left defined (default)
									//transformCoordsOffset[0]=loc.inhomX-xRW;
									//transformCoordsOffset[1]=loc.inhomY-yRW;
									startPoint.setLocation(loc.inhomX, loc.inhomY);
									firstMoveable = false;
								} else {
									loc = (GeoPoint) ((GeoImage)geo).getStartPoints()[1];
									if (loc != null) { // bottom right defined
										//transformCoordsOffset[0]=loc.inhomX-xRW;
										//transformCoordsOffset[1]=loc.inhomY-yRW;
										startPoint.setLocation(loc.inhomX, loc.inhomY);
										firstMoveable = false;
									}
								}
							}
						}
					} else if (geo.isGeoBoolean()) {
						//moveMode = MOVE_BOOLEAN;
						startPoint.setLocation(view.toRealWorldCoordX(((GeoBoolean) geo).getAbsoluteScreenLocX()), view.toRealWorldCoordY(((GeoBoolean) geo).getAbsoluteScreenLocY()+20));
						firstMoveable = false;
					} else if (geo.isGeoButton()) {
						startPoint.setLocation(view.toRealWorldCoordX(((GeoButton) geo).getAbsoluteScreenLocX() - 5), view.toRealWorldCoordY(((GeoButton) geo).getAbsoluteScreenLocY() + 30));
						firstMoveable = false;
					}
				}
			}
		}
		if (firstMoveable) {
			startPoint.setLocation((view.getXmin() + view.getXmax()) / 2, (view.getYmin() + view.getYmax()) / 2);
		}
		if (pastePreviewSelected != null && !pastePreviewSelected.isEmpty()) {
			previousPointCapturing = view.getPointCapturingMode();
			view.setPointCapturing(EuclidianView.POINT_CAPTURING_STICKY_POINTS);

			// remove moved points from sticky points temporarily
			for (int i = 0; i < pastePreviewSelectedAndDependent.size(); i++) {
				geo = pastePreviewSelectedAndDependent.get(i);
				if (geo instanceof GeoPointND) {
					if (view.getStickyPointList().contains(geo))
						view.getStickyPointList().remove(geo);
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
		if (pastePreviewSelected == null) return true;
		return pastePreviewSelected.isEmpty();
	}

	protected void updatePastePreviewPosition() {
		if (translationVec == null)
			translationVec = new Coords(2);
		translationVec.setX(xRW - startPoint.x);
		translationVec.setY(yRW - startPoint.y);
		startPoint.setLocation(xRW, yRW);
		GeoElement.moveObjects(pastePreviewSelected, translationVec, new Coords(xRW, yRW, 0), null);
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
			pastePreviewSelectedAndDependent = null;//new ArrayList<GeoElement>();
		}
	}

	public void mergeStickyPointsAfterPaste() {

		for (int i = 0; i < pastePreviewSelected.size(); i++) {
			GeoElement geo = pastePreviewSelected.get(i);
			if (geo.isGeoPoint() && geo.isIndependent()) {
				for (int j = 0; j < persistentStickyPointList.size(); j++) {
					GeoPoint geo2 = (GeoPoint)persistentStickyPointList.get(j);
					if (Kernel.isEqual(geo2.getInhomX(), ((GeoPoint)geo).getInhomX()) &&
						Kernel.isEqual(geo2.getInhomY(), ((GeoPoint)geo).getInhomY()))
					{
						geo.setEuclidianVisible(false);
						String geolabel = geo.getLabelSimple();
						kernel.getAlgebraProcessor().processAlgebraCommand(geo.getLabelSimple()+"="+geo2.getLabelSimple(), false);
						kernel.lookupLabel(geolabel).setEuclidianVisible(false);
						kernel.lookupLabel(geolabel).updateRepaint();
						break;
					}
				}
			}
		}
	}

	//==============================================
	//  Pen 
	
	private EuclidianPen pen;
	public void setPen(EuclidianPen pen){
		this.pen = pen;
	}
	public EuclidianPen getPen(){
		return pen;
	}
	
	
	
	public int getMode(){
		return mode;
	}

	public void setMode(int newMode) {

		
		if(newMode == EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS
				|| newMode == EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS
				|| newMode == EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS
				)
			return;


		endOfMode(mode);

		allowSelectionRectangleForTranslateByVector = true;

		if (EuclidianView.usesSelectionRectangleAsInput(newMode) && view.getSelectionRectangle() != null)
		{
			initNewMode(newMode);
			if (app.getActiveEuclidianView() == view)
				processSelectionRectangle(null);
		}
		else if (EuclidianView.usesSelectionAsInput(newMode))
		{
			initNewMode(newMode);
			if (app.getActiveEuclidianView() == view)
				processSelection();
		}
		else
		{
			if (!TEMPORARY_MODE) app.clearSelectedGeos(false);
			initNewMode(newMode);
		}

		kernel.notifyRepaint();
	}

	protected void endOfMode(int mode) {
		switch (mode) {
		case EuclidianConstants.MODE_RECORD_TO_SPREADSHEET:
			// just to be sure recordObject is set to null
			// usually this is already done at mouseRelease
			if (recordObject != null) {
				if (((SpreadsheetView)app.getGuiManager().getSpreadsheetView()).getTraceManager().isTraceGeo(recordObject))
					app.getGuiManager().removeSpreadsheetTrace(recordObject);
				recordObject.setSelected(false);
				recordObject = null;
			}
			break;
			
		case EuclidianConstants.MODE_MOVE:
			deletePastePreviewSelected();
			break;

		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:				
			// take all selected objects and hide them
			Collection coll = 	app.getSelectedGeos();				
			Iterator it = coll.iterator();
			while (it.hasNext()) {
				GeoElement geo = (GeoElement) it.next();					
				geo.setEuclidianVisible(false);
				geo.updateRepaint();								
			}				
			break;
			
		case EuclidianConstants.MODE_PEN:
			pen.resetPenOffsets();
			
			view.setSelectionRectangle(null);
			break;
		}

		if (toggleModeChangedKernel)
			app.storeUndoInfo();
	}
	
	
	
	
	protected void initNewMode(int mode) {
		this.mode = mode;
		initShowMouseCoords();
		// Michael Borcherds 2007-10-12
		//clearSelections();
		if (!TEMPORARY_MODE && !EuclidianView.usesSelectionRectangleAsInput(mode)) clearSelections();
		//		 Michael Borcherds 2007-10-12
		moveMode = MOVE_NONE;
		
		closeMiniPropertiesPanel();

		if (mode == EuclidianConstants.MODE_RECORD_TO_SPREADSHEET) {
			if (!app.getGuiManager().hasSpreadsheetView())
				app.getGuiManager().attachSpreadsheetView();
			if (!app.getGuiManager().showView(Application.VIEW_SPREADSHEET))
				app.getGuiManager().setShowView(true, Application.VIEW_SPREADSHEET);
		}


		view.setPreview(switchPreviewableForInitNewMode(mode));	
		toggleModeChangedKernel = false;
	}	
	
	
	protected Previewable switchPreviewableForInitNewMode(int mode){
		
		Previewable previewDrawable = null;
		// init preview drawables
		switch (mode) {
		
		case EuclidianConstants.MODE_PEN:
			
			boolean createUndo = true;
			// scale both EVs 1:1
			if (app.getEuclidianView().isVisible()) {
				app.getEuclidianView().zoomAxesRatio(1, true);
				createUndo = false;
			}
			
			if (app.hasEuclidianView2() && app.getEuclidianView2().isVisible()) {
				app.getEuclidianView2().zoomAxesRatio(1, createUndo);
			}
			
			ArrayList selection = app.getSelectedGeos();
			if (selection.size() == 1) {
				GeoElement geo = (GeoElement)selection.get(0);
				// getCorner(1) == null as we can't write to transformed images
				if (geo.isGeoImage()) {
					GeoPoint c1 = ((GeoImage)geo).getCorner(0);
					GeoPoint c2 = ((GeoImage)geo).getCorner(1);
					GeoPoint c3 = ((GeoImage)geo).getCorner(2);
								
					if (c3 == null &&
							(c2 == null // c2 = null -> not transformed							
						// or c1 and c2 are the correct spacing for the image not to be transformed
						// (ie image was probably created by the Pen Tool)
						|| (c1 != null && c2 != null && c1.inhomY == c2.inhomY && view.toScreenCoordX(c2.inhomX) - view.toScreenCoordX(c1.inhomX) == ((GeoImage)geo).getFillImage().getWidth() ))) 
						pen.setPenGeo( (GeoImage)geo); 
					else 
						pen.setPenGeo(null);
					
					pen.setPenWritingToExistingImage(pen.getPenGeo() != null);
				}
			}
			
			// no break;
			
		case EuclidianConstants.MODE_VISUAL_STYLE:
			
		//	openMiniPropertiesPanel();
			
			break;
			
		case EuclidianConstants.MODE_PARALLEL:
			previewDrawable = view.createPreviewParallelLine(selectedPoints, selectedLines);
			break;

		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
			previewDrawable = view.createPreviewAngleBisector(selectedPoints);
			break;

		case EuclidianConstants.MODE_ORTHOGONAL:
			previewDrawable = view.createPreviewPerpendicularLine(selectedPoints, selectedLines);
			break;

		case EuclidianConstants.MODE_LINE_BISECTOR:
			previewDrawable = view.createPreviewPerpendicularBisector(selectedPoints);
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
			previewDrawable = view.createPreviewConic(mode, selectedPoints);
			break;

		case EuclidianConstants.MODE_ANGLE:		
			previewDrawable = view.createPreviewAngle(selectedPoints);
			break;

			// preview for compass: radius first
		case EuclidianConstants.MODE_COMPASSES:
			previewDrawable = new DrawConic((EuclidianView) view, mode, selectedPoints, selectedSegments, selectedConicsND);
			break;

			// preview for arcs and sectors
		case EuclidianConstants.MODE_SEMICIRCLE:
		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			previewDrawable = new DrawConicPart((EuclidianView) view, mode, selectedPoints);
			break;

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			useLineEndPoint = false;
			previewDrawable = view.createPreviewVector(selectedPoints);
			break;

		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
			// select all hidden objects			
			Iterator it = kernel.getConstruction().getGeoSetConstructionOrder().iterator();
			while (it.hasNext()) {
				GeoElement geo = (GeoElement) it.next();
				// independent numbers should not be set visible
				// as this would produce a slider
				if (!geo.isSetEuclidianVisible() && 
						!(
								(geo.isNumberValue() || geo.isBooleanValue()) && geo.isIndependent())
				) 
				{
					geo.setEuclidianVisible(true);					
					app.addSelectedGeo(geo);
					geo.updateRepaint();										
				}
			}	
			break;

		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
			movedGeoElement = null; // this will be the active geo template
			break;

		case EuclidianConstants.MODE_MOVE_ROTATE:		
			rotationCenter = null; // this will be the active geo template
			break;

		case EuclidianConstants.MODE_RECORD_TO_SPREADSHEET:
			
			//G.Sturr 2010-5-14
			if(recordObject != null)
				app.getGuiManager().removeSpreadsheetTrace(recordObject);
			//END G.Sturr
			
			recordObject = null;
	
			break;

		default:
			previewDrawable = null;

			// macro mode?
			if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
				// get ID of macro
				int macroID = mode - EuclidianConstants.MACRO_MODE_ID_OFFSET;
				macro = kernel.getMacro(macroID);
				macroInput = macro.getInputTypes();
				this.mode = EuclidianConstants.MODE_MACRO;								
			}		
			break;
		}
		
		return previewDrawable;
	}



	protected void initShowMouseCoords() {
		view.setShowMouseCoords(mode == EuclidianConstants.MODE_POINT || mode == EuclidianConstants.MODE_MOVE);	}

	public void clearSelections() {

		clearSelection(selectedNumbers);
		clearSelection(selectedNumberValues);
		clearSelection(selectedPoints);
		clearSelection(selectedLines);
		clearSelection(selectedSegments);
		clearSelection(selectedConicsND);
		clearSelection(selectedVectors);
		clearSelection(selectedPolygons);
		clearSelection(selectedGeos);
		clearSelection(selectedFunctions);		
		clearSelection(selectedCurves);
		clearSelection(selectedLists);
		clearSelection(selectedPaths);
		clearSelection(selectedRegions);

		app.clearSelectedGeos();

		// if we clear selection and highlighting,
		// we may want to clear justCreatedGeos also
		clearJustCreatedGeos();

		// clear highlighting
		refreshHighlighting(null);		
	}


	public Point getMouseLoc(){
		return mouseLoc;
	}

	public void mouseClicked(MouseEvent e) {	
		
		if (mode == EuclidianConstants.MODE_PEN)
			return;
		
		Hits hits;
		//GeoElement geo;

		altDown=e.isAltDown();

		if (mode != EuclidianConstants.MODE_SELECTION_LISTENER)
			((JPanel) view).requestFocusInWindow();

		if (Application.isRightClick(e)) return;
		setMouseLocation(e);		


		// double-click on object selects MODE_MOVE and opens redefine dialog
		if (e.getClickCount() == 2) { 
			if (app.isApplet() || Application.isControlDown(e))
				return;

			app.clearSelectedGeos();
			//hits = view.getTopHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits().getTopHits();
			switchModeForRemovePolygons(hits);
			if (!hits.isEmpty()) {
				view.setMode(EuclidianConstants.MODE_MOVE);
				GeoElement geo0 = (GeoElement)hits.get(0);
				
				if (geo0.isGeoNumeric() && ((GeoNumeric)geo0).isSlider()) {
					// double-click slider -> Object Properties
					app.getGuiManager().showPropertiesDialog(hits);
				} else
				if (!geo0.isFixed() && !(geo0.isGeoBoolean() && geo0.isIndependent()) &&
						!(geo0.isGeoImage() && geo0.isIndependent())
						&& !geo0.isGeoButton())
					app.getGuiManager().showRedefineDialog((GeoElement)hits.get(0), true);
			}

		}

		mouseClickedMode(e,mode);

		// Alt click: copy definition to input field
		if (e.isAltDown() && app.showAlgebraInput()) {
			view.setHits(mouseLoc);
			hits = view.getHits().getTopHits();
			hits.removePolygons();
			if (hits != null && hits.size() > 0) {
				GeoElement geo = (GeoElement) hits.get(0);

				// F3 key: copy definition to input bar
				if (mode != EuclidianConstants.MODE_ATTACH_DETACH)
					app.getGlobalKeyDispatcher().handleFunctionKeyForAlgebraInput(3, geo);

				moveMode = MOVE_NONE;	
				return;
			}					
		}
	}
	
	
	
	protected void mouseClickedMode(MouseEvent e, int mode){
		

		switch (mode) {
		case EuclidianConstants.MODE_RECORD_TO_SPREADSHEET:
			clearSelections();
			break;
		case EuclidianConstants.MODE_VISUAL_STYLE:								
		case EuclidianConstants.MODE_MOVE:								
		case EuclidianConstants.MODE_SELECTION_LISTENER:
			switch (e.getClickCount()) {
			case 1:			
				// handle selection click	
				view.setHits(mouseLoc);
				handleSelectClick(view.getHits().getTopHits(),//view.getTopHits(mouseLoc), 
						Application.isControlDown(e));
				break;
				/*
			//	open properties dialog on double click
			case 2:
				if (app.isApplet())
					return;

				app.clearSelectedGeos();
				hits = view.getTopHits(mouseLoc);
				if (hits != null && mode == EuclidianConstants.MODE_MOVE) {
					GeoElement geo0 = (GeoElement)hits.get(0);
					if (!geo0.isFixed() && !(geo0.isGeoImage() && geo0.isIndependent()))
						app.getGuiManager().showRedefineDialog((GeoElement)hits.get(0));
				}
				break;*/
			}
			break;

		case EuclidianConstants.MODE_ZOOM_IN:
			view.zoom(mouseLoc.x, mouseLoc.y, EuclidianView.MODE_ZOOM_FACTOR, 15,  false);
			toggleModeChangedKernel = true;
			break;

		case EuclidianConstants.MODE_ZOOM_OUT:
			view.zoom(mouseLoc.x, mouseLoc.y, 1d/EuclidianView.MODE_ZOOM_FACTOR, 15, false);
			toggleModeChangedKernel = true;
			break;
		}
	}

	protected void handleSelectClick(ArrayList geos, boolean ctrlDown) {	
		if (geos == null) {			
			app.clearSelectedGeos();
		} else {					
			if (ctrlDown) {							
				GeoElement geo = chooseGeo(geos, true);
				//boolean selected = geo.is
				app.toggleSelectedGeo( chooseGeo(geos, true) ); 
				//app.geoElementSelected(geo, true); // copy definiton to input bar
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






	protected void mousePressedTranslatedView(MouseEvent e){

		Hits hits;

		// check if axis is hit
		//hits = view.getHits(mouseLoc);
		view.setHits(mouseLoc);
		hits = view.getHits();
		hits.removePolygons();
		//Application.debug("MODE_TRANSLATEVIEW - "+hits.toString());

		/*
		if (!hits.isEmpty() && hits.size() == 1) {
			Object hit0 = hits.get(0);
			if (hit0 == kernel.getXAxis())
				moveMode = MOVE_X_AXIS;
			else if (hit0 == kernel.getYAxis())
				moveMode = MOVE_Y_AXIS;
			else
				moveMode = MOVE_VIEW;
		} else {						
			moveMode = MOVE_VIEW;
		}	
		*/

		moveMode = MOVE_VIEW;	
		if (!hits.isEmpty() ) 
			for(Object hit:hits){
				if (hit == kernel.getXAxis())
					moveMode = MOVE_X_AXIS;
				if (hit == kernel.getYAxis())
					moveMode = MOVE_Y_AXIS;
			}	

		startLoc = mouseLoc; 
		if (!TEMPORARY_MODE) {
			if (moveMode == MOVE_VIEW)
				view.setMoveCursor();
			else
				view.setDragCursor();
		}

		//xZeroOld = view.getXZero();
		//yZeroOld = view.getYZero();
		view.rememberOrigins();
		xTemp = xRW;
		yTemp = yRW;
		view.setShowAxesRatio((moveMode == MOVE_X_AXIS) || (moveMode == MOVE_Y_AXIS));
		//view.setDrawMode(EuclidianConstants.DRAW_MODE_DIRECT_DRAW);

	}
	
	
	/**
	 * 
	 * @return true if a view button has been pressed (see 3D)
	 */
	protected boolean handleMousePressedForViewButtons(){
		return false;
	}

	public void mousePressed(MouseEvent e) {
		
		if (app.useFullGui()) {
			// determine parent panel to change focus
			//EuclidianDockPanelAbstract panel = (EuclidianDockPanelAbstract)SwingUtilities.getAncestorOfClass(EuclidianDockPanelAbstract.class, (Component)e.getSource());
			
			//if(panel != null) {
			//	app.getGuiManager().getLayout().getDockManager().setFocusedPanel(panel);
			//}
			app.getGuiManager().setFocusedPanel(e);
		}
		
		setMouseLocation(e);
		
		if (handleMousePressedForViewButtons())
			return;
		
		Hits hits;
		
		if (mode == EuclidianConstants.MODE_PEN) {
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removeAllButImages();
			pen.handleMousePressedForPenMode(e, hits);
			return;
		}

		//GeoElement geo;
		transformCoords();	

		moveModeSelectionHandled = false;
		DRAGGING_OCCURED = false;			
		view.setSelectionRectangle(null);
		selectionStartPoint.setLocation(mouseLoc);	

		if (hitResetIcon() || view.hitAnimationButton(e)) {				
			// see mouseReleased
			return;
		}

		if (Application.isRightClick(e)) {			
			//ggb3D - for 3D rotation
			processRightPressFor3D();

			return;
		} 
		else if (
				app.isShiftDragZoomEnabled() && 
				(
						// MacOS: shift-cmd-drag is zoom
						(e.isShiftDown() && !Application.isControlDown(e)) // All Platforms: Shift key
						|| 
						e.isControlDown() && Application.WINDOWS // old Windows key: Ctrl key 
						||
						Application.isMiddleClick(e)
				)) 
		{
			// Michael Borcherds 2007-12-08 BEGIN
			// bugfix: couldn't select multiple objects with Ctrl		


			view.setHits(mouseLoc);
			hits = view.getHits();
			switchModeForRemovePolygons(hits);
			if (!hits.isEmpty()) // bugfix 2008-02-19 removed this:&& ((GeoElement) hits.get(0)).isGeoPoint())
			{
				DONT_CLEAR_SELECTION=true;
			}
			//			 Michael Borcherds 2007-12-08 END
			TEMPORARY_MODE = true;
			oldMode = mode; // remember current mode	
			view.setMode(EuclidianConstants.MODE_TRANSLATEVIEW);				
		} 	
		
		switchModeForMousePressed(e);
		
	}
	
	protected void createNewPointForModePoint(Hits hits, boolean complex){
		if (mode==EuclidianConstants.MODE_POINT || mode==EuclidianConstants.MODE_COMPLEX_NUMBER){//remove polygons : point inside a polygon is created free, as in v3.2
			Application.debug("complex"+complex);
			hits.removeAllPolygons();
			hits.removeConicsHittedOnFilling();
			createNewPoint(hits, true, false, true, true, complex);
		}else {// if mode==EuclidianView.MODE_POINT_ON_OBJECT, point can be in a region
			createNewPoint(hits, true, true,  true, true, complex);
		}
	}
	
	protected void createNewPointForModeOther(Hits hits){
		createNewPoint(hits, true, false, true, true, false);
	}
	
	protected void switchModeForMousePressed(MouseEvent e){

		Hits hits;
		
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
			Application.debug(hits);
			// if mode==EuclidianView.MODE_POINT_ON_OBJECT, point can be in a region
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
			//hits = view.getHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			createNewPointForModeOther(hits);
			break;

		case EuclidianConstants.MODE_VECTOR_POLYGON:
		case EuclidianConstants.MODE_RIGID_POLYGON:
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			createNewPoint(hits, false, false, false, false, false); 
			break;

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			if (!allowSelectionRectangleForTranslateByVector) {
				view.setHits(mouseLoc);
				hits = view.getHits();
				hits.removePolygons();
				if (hits.size() == 0)
					createNewPoint(hits, false, true, true);
			}
			break;

		case EuclidianConstants.MODE_PARALLEL:
		case EuclidianConstants.MODE_PARABOLA: // Michael Borcherds 2008-04-08
		case EuclidianConstants.MODE_ORTHOGONAL:
		case EuclidianConstants.MODE_LINE_BISECTOR:
		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
		case EuclidianConstants.MODE_TANGENTS:		
		case EuclidianConstants.MODE_POLAR_DIAMETER:
			//hits = view.getHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			if (hits.size() == 0)
				createNewPoint(hits, false, true, true);
			break;		

		case EuclidianConstants.MODE_COMPASSES:		// Michael Borcherds 2008-03-13	
			//hits = view.getHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			if (hits.isEmpty()) 
				createNewPoint(hits, false, true, true);
			break;		

		case EuclidianConstants.MODE_ANGLE:
			//hits = view.getTopHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits().getTopHits();
			// check if we got a polygon
			if (hits.isEmpty()) {
				createNewPoint(hits, false, false, true);			
			}			
			break;

		case EuclidianConstants.MODE_ANGLE_FIXED:
		case EuclidianConstants.MODE_MIDPOINT:
			//hits = view.getHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			if (hits.isEmpty() || (!((GeoElement) hits.get(0)).isGeoSegment() && !((GeoElement) hits.get(0)).isGeoConic())) 
				createNewPoint(hits, false, false, true);			
			break;

		case EuclidianConstants.MODE_MOVE_ROTATE:
			handleMousePressedForRotateMode();
			break;

		case EuclidianConstants.MODE_RECORD_TO_SPREADSHEET:
			view.setHits(mouseLoc);
			hits = view.getHits();
			GeoElement tracegeo = hits.getFirstHit(GeoPoint.class);
			if (tracegeo == null)
				tracegeo = hits.getFirstHit(GeoVector.class);
			if (tracegeo == null)
				tracegeo = hits.getFirstHit(GeoNumeric.class);
			if (tracegeo == null)
				tracegeo = hits.getFirstHit(GeoList.class);
			if (tracegeo != null)
			{
				if (recordObject == null)
				{
					if (!((SpreadsheetView)app.getGuiManager().getSpreadsheetView()).getTraceManager().isTraceGeo(tracegeo))
						app.getGuiManager().addSpreadsheetTrace(tracegeo);
					recordObject = tracegeo;
				}
				handleMousePressedForMoveMode(e, false);
				tracegeo.updateRepaint();
			}
			break;

			// move an object
		case EuclidianConstants.MODE_MOVE:	
		case EuclidianConstants.MODE_VISUAL_STYLE:	
			handleMousePressedForMoveMode(e, false);			
			break;

			// move drawing pad or axis
		case EuclidianConstants.MODE_TRANSLATEVIEW:		

			mousePressedTranslatedView(e);

			break;								

		default:
			moveMode = MOVE_NONE;			 
		}
	}

	
	
	
	protected void handleMousePressedForRotateMode() {	
		GeoElement geo;
		Hits hits;

		// we need the center of the rotation
		if (rotationCenter == null) {
			view.setHits(mouseLoc);
			rotationCenter = (GeoPoint) chooseGeo(view.getHits().getHits(GeoPoint.class, tempArrayList), true);
			app.addSelectedGeo(rotationCenter);
			moveMode = MOVE_NONE;
		}
		else {	
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			//hits = view.getHits(mouseLoc);
			// got rotation center again: deselect
			if (!hits.isEmpty() && hits.contains(rotationCenter)) {
				app.removeSelectedGeo(rotationCenter);
				rotationCenter = null;
				moveMode = MOVE_NONE;
				return;
			}

			moveModeSelectionHandled = true;

			// find and set rotGeoElement
			hits = hits.getPointRotateableHits(view,rotationCenter);
			if (!hits.isEmpty() && hits.contains(rotGeoElement))
				geo = rotGeoElement;
			else {
				geo = chooseGeo(hits, true);				
				app.addSelectedGeo(geo);				
			}			
			rotGeoElement = geo;						

			if (geo != null) {							
				doSingleHighlighting(rotGeoElement);						
				//rotGeoElement.setHighlighted(true);

				// init values needed for rotation
				rotStartGeo = rotGeoElement.copy();
				rotStartAngle = Math.atan2(yRW - rotationCenter.inhomY, 
						xRW - rotationCenter.inhomX);
				moveMode = MOVE_ROTATE;
			} else {
				moveMode = MOVE_NONE;
			}
		}			
	}
	
	protected void handleMousePressedForMoveMode(MouseEvent e, boolean drag) {
		
		//long t0 = System.currentTimeMillis();


		//Application.debug("start");



		//view.resetTraceRow(); // for trace/spreadsheet

		// fix for meta-click to work on Mac/Linux
		if (Application.isControlDown(e)) return;

		// move label?
		GeoElement geo = view.getLabelHit(mouseLoc);
		//Application.debug("label("+(System.currentTimeMillis()-t0)+")");
		if (geo != null) {
			moveMode = MOVE_LABEL;
			movedLabelGeoElement = geo;
			oldLoc.setLocation(geo.labelOffsetX, geo.labelOffsetY);
			startLoc = mouseLoc;
			view.setDragCursor();
			return;
		}


		//Application.debug("laps("+(System.currentTimeMillis()-t0)+")");

		// find and set movedGeoElement
		view.setHits(mouseLoc); 
		
		// make sure that eg slider takes precedence over a polygon (in the same layer)
		view.getHits().removePolygons();

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
		if (drag)
			moveableList = view.getHits().getMoveableHits(view);
		else
			moveableList = view.getHits();

		Hits hits = moveableList.getTopHits();
		
		//Application.debug("end("+(System.currentTimeMillis()-t0)+")");

		ArrayList selGeos = app.getSelectedGeos();
		
		// if object was chosen before, take it now!
		if (selGeos.size() == 1 && 
				!hits.isEmpty() && hits.contains(selGeos.get(0))) 
		{
			// object was chosen before: take it			
			geo = (GeoElement) selGeos.get(0);			
		} else {
			// choose out of hits			
			geo = chooseGeo(hits, false);
			
			if (!selGeos.contains(geo)) {
				app.clearSelectedGeos();
				app.addSelectedGeo(geo);
				//app.geoElementSelected(geo, false); // copy definiton to input bar
			}
		}				

		if (geo != null && !geo.isFixed()) {		
			moveModeSelectionHandled = true;														
		} else {
			// no geo clicked at
			moveMode = MOVE_NONE;	
			resetMovedGeoPoint();
			return;
		}				

		


        handleMovedElement(geo,selGeos.size()>1);

		view.repaintView();												
	}
	public void handleMovedElement(GeoElement geo, boolean multiple){
		resetMovedGeoPoint();
		movedGeoElement = geo;

		// multiple geos selected
		if (movedGeoElement != null && multiple) {									
			moveMode = MOVE_MULTIPLE_OBJECTS;
			startPoint.setLocation(xRW, yRW);	
			startLoc = mouseLoc;
			view.setDragCursor();
			if (translationVec == null)
				translationVec = new Coords(2);
		}	

		// DEPENDENT object: changeable parents?
		// move free parent points (e.g. for segments)
		else if (!movedGeoElement.isMoveable(view)) {
			
			translateableGeos = null;
			// point with changeable coord parent numbers
			if (movedGeoElement.hasChangeableCoordParentNumbers()) {
				movedGeoElement.recordChangeableCoordParentNumbers();
				translateableGeos = new ArrayList();
				translateableGeos.add(movedGeoElement);
			}

			// STANDARD case: get free input points of dependent movedGeoElement
			else if (movedGeoElement.hasMoveableInputPoints(view)) {
				// allow only moving of the following object types
				if (movedGeoElement.isGeoLine() || 
						movedGeoElement.isGeoPolygon() ||					 
						movedGeoElement instanceof GeoPolyLine ||					 
						movedGeoElement.isGeoConic() || 
						movedGeoElement.isGeoImage() || 
						movedGeoElement.isGeoList() || 
						movedGeoElement.isGeoVector()) 
				{		
					translateableGeos = movedGeoElement.getFreeInputPoints(view);
				}
			}

			// init move dependent mode if we have something to move ;-)
			if (translateableGeos != null) {
				moveMode = MOVE_DEPENDENT;

				if (translateableGeos.get(0) instanceof GeoPoint){
					GeoPoint point = ((GeoPoint)translateableGeos.get(0));
					if (point.getParentAlgorithm() != null) {
						// make sure snap-to-grid works for dragging (a + x(A), b + x(B))
						transformCoordsOffset[0] = 0;
						transformCoordsOffset[1] = 0;

					} else {
						// snap to grid when dragging polygons, segments, images etc
						// use first point
						point.getInhomCoords(transformCoordsOffset);
						transformCoordsOffset[0]-=xRW;
						transformCoordsOffset[1]-=yRW;
					}
				}
				
				setStartPointLocation();

				view.setDragCursor();
				if (translationVec == null)
					translationVec = new Coords(2);
			} else {
				moveMode = MOVE_NONE;
			}				
		} 
		
		// free point
		else if (movedGeoElement.isGeoPoint()) {
			moveMode = MOVE_POINT;
			setMovedGeoPoint(movedGeoElement);
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
				//	if |mouse - startpoint| < 1/2 * |vec| then move
				// startpoint
				if (2d * GeoVec2D.length(xRW - sx, yRW - sy) < GeoVec2D
						.length(movedGeoVector.x, movedGeoVector.y)) { // take
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
				} else
					moveMode = MOVE_VECTOR;
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
				//startPoint.setLocation(xRW - view.toRealWorldCoordX(oldLoc.x), yRW - view.toRealWorldCoordY(oldLoc.y));
				//movedGeoText.setNeedsUpdatedBoundingBox(true);
		        //movedGeoText.update(); 
				//transformCoordsOffset[0]=movedGeoText.getBoundingBox().getX()-xRW;
				//transformCoordsOffset[1]=movedGeoText.getBoundingBox().getY()-yRW;
			}
			else if (movedGeoText.hasAbsoluteLocation()) {
				//	absolute location: change location
				GeoPoint loc = (GeoPoint) movedGeoText.getStartPoint();
				if (loc == null) {
					loc = new GeoPoint(kernel.getConstruction());
					loc.setCoords(0, 0, 1.0);
					try {
						movedGeoText.setStartPoint(loc);
					} catch (Exception ex) {
					}
					startPoint.setLocation(xRW, yRW);
				} else {
					startPoint.setLocation(xRW - loc.inhomX, yRW
							- loc.inhomY);

					GeoPoint loc2 = new GeoPoint(loc);
			        movedGeoText.setNeedsUpdatedBoundingBox(true);
			        movedGeoText.update(); 
					loc2.setCoords(movedGeoText.getBoundingBox().getX(), movedGeoText.getBoundingBox().getY(), 1.0);

					transformCoordsOffset[0]=loc2.inhomX-xRW;
					transformCoordsOffset[1]=loc2.inhomY-yRW;
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

			startPoint.setLocation(xRW, yRW);
			if (tempConic == null) {
				tempConic = new GeoConic(kernel.getConstruction());
			}
			tempConic.set(movedGeoConic);
		} 
		else if (movedGeoElement.isGeoImplicitPoly()){
			moveMode=MOVE_IMPLICITPOLY;
			movedGeoImplicitPoly=(GeoImplicitPoly) movedGeoElement;
			view.setShowMouseCoords(false);
			view.setDragCursor();
			
			startPoint.setLocation(xRW, yRW);
			if (tempImplicitPoly == null) {
				tempImplicitPoly = new GeoImplicitPoly(movedGeoImplicitPoly);
			}else
				tempImplicitPoly.set(movedGeoImplicitPoly);
			
			if (tempDependentPointX==null){
				tempDependentPointX=new ArrayList<Double>();
			}else{
				tempDependentPointX.clear();
			}
			
			if (tempDependentPointY==null){
				tempDependentPointY=new ArrayList<Double>();
			}else{
				tempDependentPointY.clear();
			}
			
			if (moveDependentPoints==null){
				moveDependentPoints=new ArrayList<GeoPoint>();
			}else{
				moveDependentPoints.clear();
			}
			
			for (GeoElement f: movedGeoImplicitPoly.getAllChildren()){
//				if (f instanceof GeoPoint && f.getParentAlgorithm().getInput().length==1 && f.getParentAlgorithm().getInput()[0] instanceof Path){
				if (f instanceof GeoPoint && movedGeoImplicitPoly.isParentOf(f)){
					GeoPoint g=(GeoPoint) f;
					if(!Kernel.isZero(g.getZ())){
						moveDependentPoints.add(g);
						tempDependentPointX.add(g.getX()/g.getZ());
						tempDependentPointY.add(g.getY()/g.getZ());
					}
				}
			}
//			for (GeoElement elem:movedGeoImplicitPoly.getAllChildren()){
//				if (elem instanceof GeoPoint){
//					if (movedGeoImplicitPoly.isParentOf(elem)){
//						tempDependentPointOnPath.add(((GeoPoint)elem).getPathParameter().getT());
//					}
//				}
//			}
			
		}
		else if (movedGeoElement.isGeoFunction()) {
			moveMode = MOVE_FUNCTION;
			movedGeoFunction = (GeoFunction) movedGeoElement;
			view.setShowMouseCoords(false);
			view.setDragCursor();

			startPoint.setLocation(xRW, yRW);
			if (tempFunction == null) {
				tempFunction = new GeoFunction(kernel.getConstruction());
			}
			tempFunction.set(movedGeoFunction);
		} 

		// free number
		else if (movedGeoElement.isGeoNumeric()) {															
			movedGeoNumeric = (GeoNumeric) movedGeoElement;
			moveMode = MOVE_NUMERIC;

			Drawable d = view.getDrawableFor(movedGeoNumeric);
			if (d instanceof DrawSlider) {
				// should we move the slider 
				// or the point on the slider, i.e. change the number
				DrawSlider ds = (DrawSlider) d;
				// TEMPORARY_MODE true -> dragging slider using Slider Tool
				// or right-hand mouse button

				// otherwise using Move Tool -> move dot
				if (((TEMPORARY_MODE && app.isRightClickEnabled()) || !movedGeoNumeric.isSliderFixed()) && !ds.hitPoint(mouseLoc.x, mouseLoc.y) &&
						ds.hitSlider(mouseLoc.x, mouseLoc.y)) {
					moveMode = MOVE_SLIDER;
					if (movedGeoNumeric.isAbsoluteScreenLocActive()) {
						oldLoc.setLocation(movedGeoNumeric.getAbsoluteScreenLocX(),
								movedGeoNumeric.getAbsoluteScreenLocY());
						startLoc = mouseLoc;

						// part of snap to grid code
						startPoint.setLocation(xRW - view.toRealWorldCoordX(oldLoc.x), yRW - view.toRealWorldCoordY(oldLoc.y));
						transformCoordsOffset[0]=view.toRealWorldCoordX(oldLoc.x)-xRW;
						transformCoordsOffset[1]=view.toRealWorldCoordY(oldLoc.y)-yRW;
					} else {
						startPoint.setLocation(xRW - movedGeoNumeric.getRealWorldLocX(),
								yRW - movedGeoNumeric.getRealWorldLocY());
						transformCoordsOffset[0] = movedGeoNumeric.getRealWorldLocX() - xRW;
						transformCoordsOffset[1] = movedGeoNumeric.getRealWorldLocY() - yRW;
					}
				}	
				else 
				{						
					startPoint.setLocation(movedGeoNumeric.getSliderX(), movedGeoNumeric.getSliderY());
					
					// update straightaway in case it's just a click (no drag)
					moveNumeric(true);
				}
			} 						

			view.setShowMouseCoords(false);
			view.setDragCursor();					
		}  

		//  checkbox
		else if (movedGeoElement.isGeoBoolean()) {
			movedGeoBoolean = (GeoBoolean) movedGeoElement;
			
			// if fixed checkbox dragged, behave as if it's been clicked
			// important for electronic whiteboards
			if (movedGeoBoolean.isCheckboxFixed()) {
				movedGeoBoolean.setValue(!movedGeoBoolean.getBoolean());
				app.removeSelectedGeo(movedGeoBoolean); // make sure doesn't get selected
				movedGeoBoolean.updateCascade();

			} 
			
			// move checkbox
			moveMode = MOVE_BOOLEAN;					
			startLoc = mouseLoc;
			oldLoc.x = movedGeoBoolean.getAbsoluteScreenLocX();
			oldLoc.y = movedGeoBoolean.getAbsoluteScreenLocY();

			// part of snap to grid code (the constant 5 comes from DrawBoolean)
			startPoint.setLocation(xRW - view.toRealWorldCoordX(oldLoc.x), yRW - view.toRealWorldCoordY(oldLoc.y));
			transformCoordsOffset[0]=view.toRealWorldCoordX(oldLoc.x+5)-xRW;
			transformCoordsOffset[1]=view.toRealWorldCoordY(oldLoc.y+5)-yRW;

			view.setShowMouseCoords(false);
			view.setDragCursor();			

		}

		//  button
		else if (movedGeoElement.isGeoButton()) {
			movedGeoButton = (GeoButton) movedGeoElement;
			// move checkbox
			moveMode = MOVE_BUTTON;					
			startLoc = mouseLoc;
			oldLoc.x = movedGeoButton.getAbsoluteScreenLocX();
			oldLoc.y = movedGeoButton.getAbsoluteScreenLocY();

			// part of snap to grid code
			startPoint.setLocation(xRW - view.toRealWorldCoordX(oldLoc.x), yRW - view.toRealWorldCoordY(oldLoc.y));
			transformCoordsOffset[0]=view.toRealWorldCoordX(oldLoc.x)-xRW;
			transformCoordsOffset[1]=view.toRealWorldCoordY(oldLoc.y)-yRW;

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
				startPoint.setLocation(xRW - view.toRealWorldCoordX(oldLoc.x), yRW - view.toRealWorldCoordY(oldLoc.y));
				transformCoordsOffset[0]=view.toRealWorldCoordX(oldLoc.x)-xRW;
				transformCoordsOffset[1]=view.toRealWorldCoordY(oldLoc.y)-yRW;
			} 
			else if (movedGeoImage.hasAbsoluteLocation()) {
				startPoint.setLocation(xRW, yRW);
				oldImage = new GeoImage(movedGeoImage);

				GeoPoint loc = (GeoPoint) movedGeoImage.getStartPoints()[2];
				if (loc != null) { // top left defined
					transformCoordsOffset[0]=loc.inhomX-xRW;
					transformCoordsOffset[1]=loc.inhomY-yRW;
				} else {
					loc = (GeoPoint) movedGeoImage.getStartPoint();
					if (loc != null) { // bottom left defined (default)
						transformCoordsOffset[0]=loc.inhomX-xRW;
						transformCoordsOffset[1]=loc.inhomY-yRW;
					} else {
						loc = (GeoPoint) movedGeoImage.getStartPoints()[1];
						if (loc != null) { // bottom right defined
							transformCoordsOffset[0]=loc.inhomX-xRW;
							transformCoordsOffset[1]=loc.inhomY-yRW;
						}
					}
				}
			} 				
		}
		else {
			moveMode = MOVE_NONE;
		}

	}



	////////////////////////////////////////////
	// setters movedGeoElement -> movedGeoPoint, ...
	public void setMovedGeoPoint(GeoElement geo){
		movedGeoPoint = (GeoPointND) movedGeoElement;

		AlgoElement algo = ((GeoElement) movedGeoPoint).getParentAlgorithm();
		if (algo != null && algo instanceof AlgoDynamicCoordinates) {
			movedGeoPoint = ((AlgoDynamicCoordinates)algo).getParentPoint();
		}

		view.setShowMouseCoords(!app.isApplet()
				&& !movedGeoPoint.hasPath());
		view.setDragCursor();
	}
	

	public void setStartPointLocation(){
		startPoint.setLocation(xRW, yRW);	
	}

	public void resetMovedGeoPoint(){
		movedGeoPoint = null;
	}



	protected boolean viewHasHitsForMouseDragged(){
		return !(view.getHits().isEmpty());
	}

	public void mouseDragged(MouseEvent e) {
		
		sliderValue = null;

		if (textfieldHasFocus) return;

		if (mode == EuclidianConstants.MODE_PEN) {
			pen.handleMousePressedForPenMode(e, null);
			return;
		}

		clearJustCreatedGeos();
		
		if (!DRAGGING_OCCURED) {
			
			DRAGGING_OCCURED = true;			

			// Michael Borcherds 2007-10-07 allow right mouse button to drag points
			// mathieu : also if it's mode point, we can drag the point
			if (Application.isRightClick(e) 
					|| mode == EuclidianConstants.MODE_POINT 
					|| mode == EuclidianConstants.MODE_COMPLEX_NUMBER
					|| mode == EuclidianConstants.MODE_POINT_ON_OBJECT
					|| mode == EuclidianConstants.MODE_SLIDER
					|| mode == EuclidianConstants.MODE_BUTTON_ACTION
					|| mode == EuclidianConstants.MODE_TEXTFIELD_ACTION
					|| mode == EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX
					|| mode == EuclidianConstants.MODE_TEXT
			){
				view.setHits(mouseLoc);
				
				// make sure slider tool drags only sliders, not other object types
				if (mode == EuclidianConstants.MODE_SLIDER) {
					if (view.getHits().size() != 1) return;
					
					if (!(view.getHits().get(0) instanceof GeoNumeric)) return;
				} else if (mode == EuclidianConstants.MODE_BUTTON_ACTION
						|| mode == EuclidianConstants.MODE_TEXTFIELD_ACTION ) {
					if (view.getHits().size() != 1) return;
					
					if (!(view.getHits().get(0) instanceof GeoButton)) return;
				} else if (mode == EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX ) {
					if (view.getHits().size() != 1) return;
					
					if (!(view.getHits().get(0) instanceof GeoBoolean)) return;
				} else if (mode == EuclidianConstants.MODE_TEXT ) {
					if (view.getHits().size() != 1) return;
					
					if (!(view.getHits().get(0) instanceof GeoText)) return;
				}

				
				
				if (viewHasHitsForMouseDragged()) 
				{
					TEMPORARY_MODE = true;
					oldMode = mode; // remember current mode			
					view.setMode(EuclidianConstants.MODE_MOVE);
					handleMousePressedForMoveMode(e, true);	

					// make sure that dragging doesn't deselect the geos
					DONT_CLEAR_SELECTION=true;

					return;
				}
				
			}
			if (!app.isRightClickEnabled()) return;
			//			 Michael Borcherds 2007-10-07


			if (mode == EuclidianConstants.MODE_MOVE_ROTATE) {
				app.clearSelectedGeos(false);
				app.addSelectedGeo(rotationCenter, false);						
			}
		}
		lastMouseLoc = mouseLoc;
		setMouseLocation(e);				
		transformCoords();


		//ggb3D - only for 3D view
		if (moveMode==MOVE_ROTATE_VIEW)
			if (processRotate3DView())
				return;
		
		
		if (Application.isRightClick(e)){
			// if there's no hit, or if first hit is not moveable, do 3D view rotation
			if ((!TEMPORARY_MODE) || view.getHits().size() == 0 
					|| !((GeoElement) view.getHits().get(0)).isMoveable(view)
					|| ( !((GeoElement) view.getHits().get(0)).isGeoPoint() && ((GeoElement) view.getHits().get(0)).hasDrawable3D() )
					)
				if (processRotate3DView()){ //in 2D view, return false
					if (TEMPORARY_MODE){
						TEMPORARY_MODE = false;
						mode = oldMode;
						view.setMode(mode);
					}
					return;
				}
		}


		// dragging eg a fixed point shouldn't start the selection rectangle
		if (view.getHits().isEmpty())

		// zoom rectangle (right drag) or selection rectangle (left drag)
		// Michael Borcherds 2007-10-07 allow dragging with right mouse button
		if (((Application.isRightClick(e)) || allowSelectionRectangle()) && !TEMPORARY_MODE) {
			//			 Michael Borcherds 2007-10-07 
			// set zoom rectangle's size
			// right-drag: zoom
			// Shift-right-drag: zoom without preserving aspect ratio
			updateSelectionRectangle((Application.isRightClick(e) && !e.isShiftDown())
					// MACOS:
					// Cmd-left-drag: zoom
					// Cmd-shift-left-drag: zoom without preserving aspect ratio
					|| (Application.MAC_OS && Application.isControlDown(e) && !e.isShiftDown() && !Application.isRightClick(e)));
			view.repaintView();
			return;
		}		

		// update previewable
		if (view.getPreviewDrawable() != null) {
			view.getPreviewDrawable().updateMousePos(view.toRealWorldCoordX(mouseLoc.x), view.toRealWorldCoordY(mouseLoc.y));
		}		

		/*
		 * Conintuity handling
		 * 
		 * If the mouse is moved wildly we take intermediate steps to
		 * get a more continous behaviour
		 */		 		
		if (kernel.isContinuous() && lastMouseLoc != null) {
			double dx = mouseLoc.x - lastMouseLoc.x;
			double dy = mouseLoc.y - lastMouseLoc.y;			
			double distsq = dx*dx + dy*dy;		
			if (distsq > MOUSE_DRAG_MAX_DIST_SQUARE) {										
				double factor = Math.sqrt(MOUSE_DRAG_MAX_DIST_SQUARE / distsq);				
				dx *= factor;
				dy *= factor;

				// number of continuity steps <= MAX_CONTINUITY_STEPS
				int steps = Math.min((int) (1.0 / factor), MAX_CONTINUITY_STEPS);
				int mx = mouseLoc.x;
				int my = mouseLoc.y;

				// Application.debug("BIG drag dist: " + Math.sqrt(distsq) + ", steps: " + steps  );
				for (int i=1; i <= steps; i++) {			
					mouseLoc.x = (int) Math.round(lastMouseLoc.x + i * dx);
					mouseLoc.y = (int) Math.round(lastMouseLoc.y + i * dy);
					calcRWcoords();

					handleMouseDragged(false);							
				}

				// set endpoint of mouse movement if we are not already there
				if (mouseLoc.x != mx || mouseLoc.y != my) {	
					mouseLoc.x = mx;
					mouseLoc.y = my;
					calcRWcoords();	
				}				
			} 
		}

		if (pastePreviewSelected != null)
			if (!pastePreviewSelected.isEmpty()) {
				updatePastePreviewPosition();
			}

		handleMouseDragged(true);								
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
			if (sel == null) return false;
			if (app.useFullGui()) {
				return !app.getGuiManager().isInputFieldSelectionListener();
			}
			else
				return sel != null;

			// transformations
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			return allowSelectionRectangleForTranslateByVector;

		case EuclidianConstants.MODE_DILATE_FROM_POINT:	
		case EuclidianConstants.MODE_MIRROR_AT_POINT:
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds 2008-03-23
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
		case EuclidianConstants.MODE_FITLINE:
		case EuclidianConstants.MODE_CREATE_LIST:
		case EuclidianConstants.MODE_VISUAL_STYLE:
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




	// square of maximum allowed pixel distance 
	// for continuous mouse movements
	protected static double MOUSE_DRAG_MAX_DIST_SQUARE = 36; 
	protected static int MAX_CONTINUITY_STEPS = 4; 

	protected void handleMouseDragged(boolean repaint) {
		// moveMode was set in mousePressed()		
		switch (moveMode) {
		case MOVE_ROTATE:
			rotateObject(repaint);
			break;

		case MOVE_POINT:

			//view.incrementTraceRow(); // for spreadsheet/trace

			
			movePoint(repaint);
			break;

		case MOVE_LINE:
			moveLine(repaint);
			break;

		case MOVE_VECTOR:
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
			//view.incrementTraceRow(); // for spreadsheet/trace

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
			moveDependent(repaint);
			break;

		case MOVE_MULTIPLE_OBJECTS:
			moveMultipleObjects(repaint);
			break;

		case MOVE_VIEW:
			if (repaint) {
				if (TEMPORARY_MODE) view.setMoveCursor();
				/*
					view.setCoordSystem(xZeroOld + mouseLoc.x - startLoc.x, yZeroOld
							+ mouseLoc.y - startLoc.y, view.getXscale(), view.getYscale());
				 */
				view.setCoordSystemFromMouseMove(mouseLoc.x - startLoc.x, mouseLoc.y - startLoc.y, MOVE_VIEW);
			}
			break;	

		case MOVE_X_AXIS:
			if (repaint) {
				if (TEMPORARY_MODE) view.setResizeXAxisCursor();

				// take care when we get close to the origin
				if (Math.abs(mouseLoc.x - view.getXZero()) < 2) {
					mouseLoc.x = (int) Math.round(mouseLoc.x > view.getXZero() ?  view.getXZero() + 2 : view.getXZero() - 2);						
				}											
				double xscale = (mouseLoc.x - view.getXZero()) / xTemp;					
				view.setCoordSystem(view.getXZero(), view.getYZero(), xscale, view.getYscale());
			}
			break;	

		case MOVE_Y_AXIS:
			if (repaint) {
				if (TEMPORARY_MODE) view.setResizeYAxisCursor();
				// take care when we get close to the origin
				if (Math.abs(mouseLoc.y - view.getYZero()) < 2) {
					mouseLoc.y = (int) Math.round(mouseLoc.y > view.getYZero() ?  view.getYZero() + 2 : view.getYZero() - 2);						
				}											
				double yscale = (view.getYZero() - mouseLoc.y) / yTemp;					
				view.setCoordSystem(view.getXZero(), view.getYZero(), view.getXscale(), yscale);					
			}
			break;	

		default: // do nothing			
		}
	}		

	protected void updateSelectionRectangle(boolean keepScreenRatio) {
		if (view.getSelectionRectangle() == null)
			view.setSelectionRectangle(new Rectangle());

		int dx = mouseLoc.x - selectionStartPoint.x;
		int dy = mouseLoc.y - selectionStartPoint.y;
		int dxabs = Math.abs(dx);
		int dyabs = Math.abs(dy);

		int width = dx;
		int height = dy;

		// the zoom rectangle should have the same aspect ratio as the view
		if (keepScreenRatio) {
			double ratio = (double) view.getViewWidth() / (double) view.getViewHeight();
			if (dxabs >= dyabs * ratio) {		
				height = (int) (Math.round(dxabs / ratio));
				if (dy < 0)
					height = -height;
			} else {
				width = (int) Math.round(dyabs * ratio);
				if (dx < 0)
					width = -width;			
			}
		}

		Rectangle rect = view.getSelectionRectangle();
		if (height >= 0) {			
			if (width >= 0) {
				rect.setLocation(selectionStartPoint);
				rect.setSize(width, height);
			} else { // width < 0
				rect.setLocation(selectionStartPoint.x + width, selectionStartPoint.y);
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

	// used for 3D
	protected void processReleaseForMovedGeoPoint(MouseEvent e){
		
		// deselect point after drag, but not on click
		// outdated - we want to leave the point selected after drag now
		//if (movedGeoPointDragged) getMovedGeoPoint().setSelected(false);

		if (mode != EuclidianConstants.MODE_RECORD_TO_SPREADSHEET && app.useFullGui())
			getMovedGeoPoint().resetTraceColumns();

	}
	
	public void showDrawingPadPopup(Point mouseLoc){
		app.getGuiManager().showDrawingPadPopup((JPanel) view, mouseLoc);		
	}

	public void mouseReleased(MouseEvent e) {	
		
		sliderValue = null;

		if (e != null) {
			mx = e.getX();
			my = e.getY();
		}
		// reset
		transformCoordsOffset[0] = 0;
		transformCoordsOffset[1] = 0;

		if (textfieldHasFocus) return;

		if (mode == EuclidianConstants.MODE_PEN) {
			pen.handleMouseReleasedForPenMode(e);
			return;
		}

		boolean changedKernel0 = false;
		if (pastePreviewSelected != null) {

			mergeStickyPointsAfterPaste();

			// add moved points to sticky points again
			for (int i = 0; i < pastePreviewSelectedAndDependent.size(); i++) {
				GeoElement geo = pastePreviewSelectedAndDependent.get(i);
				if (geo.isGeoPoint()) {
					if (!view.getStickyPointList().contains(geo))
						view.getStickyPointList().add((GeoPointND)geo);
				}
			}
			persistentStickyPointList = new ArrayList<GeoPointND>();

			pastePreviewSelected = null;
			pastePreviewSelectedAndDependent = null;
			view.setPointCapturing(previousPointCapturing);
			changedKernel0 = true;
			app.getKernel().getConstruction().getUndoManager().storeUndoInfoAfterPasteOrAdd();
		}

		//if (mode != EuclidianConstants.MODE_RECORD_TO_SPREADSHEET) view.resetTraceRow(); // for trace/spreadsheet
		if (getMovedGeoPoint() != null){

			processReleaseForMovedGeoPoint(e);
			/*
			// deselect point after drag, but not on click
			if (movedGeoPointDragged) getMovedGeoPoint().setSelected(false);

			if (mode != EuclidianConstants.MODE_RECORD_TO_SPREADSHEET) getMovedGeoPoint().resetTraceColumns();
			 */
		}
		if (movedGeoNumeric != null) {

			// deselect slider after drag, but not on click
			//if (movedGeoNumericDragged) movedGeoNumeric.setSelected(false);

			if (mode != EuclidianConstants.MODE_RECORD_TO_SPREADSHEET && app.useFullGui()) {
				movedGeoNumeric.resetTraceColumns();
			}
		}

		movedGeoPointDragged = false;
		movedGeoNumericDragged = false;

		((JPanel) view).requestFocusInWindow();
		setMouseLocation(e);

		altDown=e.isAltDown();

		transformCoords();
		Hits hits = null;
		GeoElement geo;

		if (hitResetIcon()) {				
			app.reset();
			return;
		}
		else if (view.hitAnimationButton(e)) {		
			if (kernel.isAnimationRunning())
				kernel.getAnimatonManager().stopAnimation();
			else
				kernel.getAnimatonManager().startAnimation();			
			view.repaintView();
			app.setUnsaved();
			return;
		}



		// Michael Borcherds 2007-10-08 allow drag with right mouse button
		if ((Application.isRightClick(e) || Application.isControlDown(e)))// && !TEMPORARY_MODE)
		{		
			if (processRightReleaseFor3D()) return;
			if (!TEMPORARY_MODE){
				if (!app.isRightClickEnabled()) return;
				if (processZoomRectangle()) return;
				//			 Michael Borcherds 2007-10-08

				// make sure cmd-click selects multiple points (not open properties)
				if (Application.MAC_OS && Application.isControlDown(e) 
						|| !Application.isRightClick(e))
					return;

				// get selected GeoElements						
				// show popup menu after right click
				view.setHits(mouseLoc);
				hits = view.getHits().getTopHits();
				if (hits.isEmpty()) {
					// no hits
					if (app.selectedGeosSize() > 0) {
						//GeoElement selGeo = (GeoElement) app.getSelectedGeos().get(0);
						app.getGuiManager().showPopupMenu(app.getSelectedGeos(), (JPanel) view, mouseLoc);
					}
					else {
						showDrawingPadPopup(mouseLoc);
					}
				} else {		
					// there are hits
					if (app.selectedGeosSize() > 0) {	

						// right click on already selected geos -> show menu for them
						// right click on object(s) not selected -> clear selection
						//     and show menu just for new objects
						if (!app.getSelectedGeos().contains(hits.get(0))) {
							app.clearSelectedGeos();
							app.addSelectedGeos(hits, true);
						} else {					
							app.addSelectedGeo((GeoElement) hits.get(0));
						}
						
						app.getGuiManager().showPopupMenu(app.getSelectedGeos(), (JPanel) view, mouseLoc);
					}
					else {
						// no selected geos: choose geo and show popup menu
						geo = chooseGeo(hits, false);
						if (geo!=null) {
							ArrayList<GeoElement> geos = new ArrayList();
							geos.add(geo);
							app.getGuiManager().showPopupMenu(geos,(JPanel) view, mouseLoc);
						}
						else
							//for 3D : if the geo hitted is xOyPlane, then chooseGeo return null
							//app.getGuiManager().showDrawingPadPopup((JPanel) view, mouseLoc);
							showDrawingPadPopup(mouseLoc);
					}																										
				}				
				return;
			}
		}

		// handle moving
		boolean changedKernel = false;		
		if (DRAGGING_OCCURED) {			

			DRAGGING_OCCURED = false;
			//			// copy value into input bar
			//			if (mode == EuclidianView.MODE_MOVE && movedGeoElement != null) {
			//				app.geoElementSelected(movedGeoElement,false);
			//			}


			// check movedGeoElement.isLabelSet() to stop moving points
			// in Probability Calculator triggering Undo
			changedKernel = (movedGeoElement != null && movedGeoElement.isLabelSet()) && (moveMode != MOVE_NONE);			
			movedGeoElement = null;
			rotGeoElement = null;	

			// Michael Borcherds 2007-10-08 allow dragging with right mouse button
			if (!TEMPORARY_MODE)
				// Michael Borcherds 2007-10-08
				if (allowSelectionRectangle()) {
					processSelectionRectangle(e);	

					return;
				}
		} else {	
			// no hits: release mouse button creates a point
			// for the transformation tools
			// (note: this cannot be done in mousePressed because
			// we want to be able to select multiple objects using the selection rectangle)
			
			changedKernel = switchModeForMouseReleased(mode, hits, changedKernel);
		}

		// remember helper point, see createNewPoint()
		if (changedKernel && !changedKernel0)
			app.storeUndoInfo();

		// make sure that when alt is pressed for creating a segment or line
		// it works if the endpoint is on a path
		if (useLineEndPoint && lineEndPoint != null) {
			mouseLoc.x = view.toScreenCoordX(lineEndPoint.x);
			mouseLoc.y = view.toScreenCoordY(lineEndPoint.y);
			useLineEndPoint = false;	
		}

		// now handle current mode
		view.setHits(mouseLoc);
		hits = view.getHits();
		switchModeForRemovePolygons(hits);
		//Application.debug(mode + "\n" + hits.toString());


		// Michael Borcherds 2007-12-08 BEGIN moved up a few lines (bugfix: Tools eg Line Segment weren't working with grid on)
		// grid capturing on: newly created point should be taken
		//Application.debug("POINT_CREATED="+POINT_CREATED+"\nhits=\n"+hits+"\ngetMovedGeoPoint()="+getMovedGeoPoint());
		if (POINT_CREATED) 			
			hits=addPointCreatedForMouseReleased(hits);		
		POINT_CREATED = false;		
		//		 Michael Borcherds 2007-12-08 END	



		if (TEMPORARY_MODE) {

			//			Michael Borcherds 2007-10-13 BEGIN
			view.setMode(oldMode);
			TEMPORARY_MODE = false;
			// Michael Borcherds 2007-12-08 BEGIN bugfix: couldn't select multiple points with Ctrl
			if (DONT_CLEAR_SELECTION==false)
				clearSelections();	
			DONT_CLEAR_SELECTION=false;
			//			 Michael Borcherds 2007-12-08 END
			//mode = oldMode;
			//			Michael Borcherds 2007-10-13 END
		} 
		//		 Michael Borcherds 2007-10-12 bugfix: ctrl-click on a point does the original mode's command at end of drag if a point was clicked on
		//  also needed for right-drag
		else
		{			
			if (mode != EuclidianConstants.MODE_RECORD_TO_SPREADSHEET){ 
				changedKernel = processMode(hits, e);
			}
			if (changedKernel)
				app.storeUndoInfo();
		}
		//Michael Borcherds 2007-10-12



		//		Michael Borcherds 2007-10-12
		//      moved up a few lines
		//		changedKernel = processMode(hits, e);
		//		if (changedKernel)
		//			app.storeUndoInfo();
		//		Michael Borcherds 2007-10-12

		
		if (!hits.isEmpty()){
			//Application.debug("hits ="+hits);
			view.setDefaultCursor();		
		}else
			view.setHitCursor();


		if (mode == EuclidianConstants.MODE_RECORD_TO_SPREADSHEET && recordObject != null) {
			clearSelections();
		} else {
			// this is in the else branch to avoid running it twice
			refreshHighlighting(null);
		}
		
		
		// reinit vars
		//view.setDrawMode(EuclidianConstants.DRAW_MODE_BACKGROUND_IMAGE);
		moveMode = MOVE_NONE;
		initShowMouseCoords();	
		view.setShowAxesRatio(false);
		kernel.notifyRepaint();					
	}
	
	
	protected Hits addPointCreatedForMouseReleased(Hits hits){
		
		if (hits.isEmpty()) {			
			hits = new Hits();
			hits.add(getMovedGeoPoint());			
		}
		
		return hits;
	}
	
	
	
	
	
	
	/**
	 * for some modes, polygons are not to be removed
	 * @param hits
	 */
	protected void switchModeForRemovePolygons(Hits hits){
		switch(mode){
		case EuclidianConstants.MODE_POINT:
		case EuclidianConstants.MODE_COMPLEX_NUMBER:
		case EuclidianConstants.MODE_POINT_ON_OBJECT:
		// removed: polygons can still be selected if they are the only object clicked on
		//case EuclidianView.MODE_INTERSECT:
		//case EuclidianView.MODE_INTERSECTION_CURVE:
			break;
		default:
			hits.removePolygons();	
		}
	}
	
	
	protected boolean switchModeForMouseReleased(int mode, Hits hits, boolean changedKernel){
		switch (mode) {
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
		case EuclidianConstants.MODE_DILATE_FROM_POINT:	
		case EuclidianConstants.MODE_MIRROR_AT_POINT:
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds 2008-03-23
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			//hits = view.getHits(mouseLoc);
			if (hits.isEmpty()) { 
				POINT_CREATED = createNewPoint(hits, false, false, true);					
			}
			changedKernel = POINT_CREATED;
			break;

		case EuclidianConstants.MODE_BUTTON_ACTION:
		case EuclidianConstants.MODE_TEXTFIELD_ACTION:
			// make sure script not triggered
			break;

		default:

			// change checkbox (boolean) state on mouse up only if there's been no drag
			view.setHits(mouseLoc);
			hits = view.getHits().getTopHits();
			//hits = view.getTopHits(mouseLoc);
			if (!hits.isEmpty()) {
				GeoElement hit = (GeoElement)hits.get(0);
				if (hit != null && hit.isGeoBoolean()) {
					GeoBoolean bool = (GeoBoolean)(hits.get(0));
					if (!bool.isCheckboxFixed()) { // otherwise changed on mouse down
						bool.setValue(!bool.getBoolean());
						app.removeSelectedGeo(bool); // make sure doesn't get selected
						bool.updateCascade();
					}
				} else if (hit != null) {
					GeoElement geo1 = chooseGeo(hits, true);
					//ggb3D : geo1 may be null if it's axes or xOy plane
					if (geo1!=null)
						geo1.runScripts(null);
						if (app.hasPythonBridge()) {
							app.getPythonBridge().click(geo1);
						}
				}
			}
		}

		return changedKernel;
	}
	
	
	

	protected boolean hitResetIcon() {
		return app.showResetIcon() &&
		(mouseLoc.y < 18 && mouseLoc.x > view.getViewWidth() - 18);
	}

	// return if we really did zoom
	protected boolean processZoomRectangle() {
		Rectangle rect = view.getSelectionRectangle();
		if (rect == null) 
			return false;

		if (rect.width < 30 || rect.height < 30
				|| !app.isShiftDragZoomEnabled() // Michael Borcherds 2007-12-11		
		) {
			view.setSelectionRectangle(null);
			view.repaintView();
			return false;
		}

		view.resetMode();
		// zoom zoomRectangle to EuclidianView's size
		//double factor = (double) view.width / (double) rect.width;
		//Point p = rect.getLocation();
		view.setSelectionRectangle(null);
		//view.setAnimatedCoordSystem((view.xZero - p.x) * factor,
		//		(view.yZero - p.y) * factor, view.xscale * factor, 15, true);

		// zoom without (necessarily) preserving the aspect ratio
		view.setAnimatedRealWorldCoordSystem(
				view.toRealWorldCoordX(rect.getMinX()),
				view.toRealWorldCoordX(rect.getMaxX()),
				view.toRealWorldCoordY(rect.getMaxY()),
				view.toRealWorldCoordY(rect.getMinY()),
				15 ,true);
		return true;
	}

	// select all geos in selection rectangle 
	protected void processSelectionRectangle(MouseEvent e) {
		clearSelections();
		view.setHits(view.getSelectionRectangle());
		Hits hits = view.getHits();

		switch (mode) {
		case EuclidianConstants.MODE_SELECTION_LISTENER:
			// tell properties dialog
			if (hits.size() > 0 &&
					app.useFullGui() &&
					app.getGuiManager().isPropertiesDialogSelectionListener()) 
			{
				GeoElement geo = (GeoElement) hits.get(0);
				app.geoElementSelected(geo, false);
				for (int i=1; i < hits.size(); i++) {
					app.geoElementSelected((GeoElement) hits.get(i), true);
				}
			} 
			break;

		case EuclidianConstants.MODE_MIRROR_AT_POINT:	
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds 2008-03-23
			processSelectionRectangleForTransformations(hits, Transformable.class);									
			break;

		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			processSelectionRectangleForTransformations(hits, Transformable.class);									
			break;		

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			processSelectionRectangleForTransformations(hits, Transformable.class);									
			break;	

		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			processSelectionRectangleForTransformations(hits, Dilateable.class);									
			break;	

		case EuclidianConstants.MODE_CREATE_LIST:
				removeParentPoints(hits);				
				selectedGeos.addAll(hits);
				app.setSelectedGeos(hits);
				processMode(hits, e);
				view.setSelectionRectangle(null);
			break;
			
		case EuclidianConstants.MODE_FITLINE:
			
			// check for list first
			if (hits.size() == 1) {
				if (hits.get(0).isGeoList()) {
					selectedGeos.addAll(hits);
					app.setSelectedGeos(hits);
					processMode(hits, e);
					view.setSelectionRectangle(null);
					break;
				}
			}
			
			// remove non-Points
			for (int i=0; i < hits.size(); i++) {
				GeoElement geo = (GeoElement) hits.get(i);
				if (!(GeoPoint.class.isInstance(geo))) {
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
				processMode(hits, e);
				view.setSelectionRectangle(null);
			}
			break;

		default:
			// STANDARD CASE
			app.setSelectedGeos(hits);

			// if alt pressed, create list of objects as string and copy to input bar
			if (hits != null && hits.size() > 0 && e != null && e.isAltDown() && app.useFullGui() && app.showAlgebraInput()) {

				JTextComponent textComponent = app.getGuiManager().getAlgebraInputTextField();				

				StringBuilder sb = new StringBuilder();
				sb.append(" {");
				for (int i = 0 ; i < hits.size() ; i++ ) {
					sb.append(((GeoElement)hits.get(i)).getLabel());
					if (i < hits.size() - 1 ) sb.append(", ");
				}
				sb.append("} ");
				//Application.debug(sb+"");
				textComponent.replaceSelection(sb.toString());
			}
			break;
		}

		kernel.notifyRepaint();
	}

	protected void processSelectionRectangleForTransformations(Hits hits, Class transformationInterface) {
		for (int i=0; i < hits.size(); i++) {
			GeoElement geo = (GeoElement) hits.get(i);
			if (!(transformationInterface.isInstance(geo) 
				//	|| geo.isGeoPolygon()
					)) {
				hits.remove(i);
			}
		}
		removeParentPoints(hits);
		selectedGeos.addAll(hits);
		app.setSelectedGeos(hits);
	}

	protected void processSelection() {
		Hits hits = new Hits();
		hits.addAll(app.getSelectedGeos());
		clearSelections();

		switch (mode) {
		case EuclidianConstants.MODE_MIRROR_AT_POINT:	
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds 2008-03-23
			processSelectionRectangleForTransformations(hits, Transformable.class);									
			break;

		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			processSelectionRectangleForTransformations(hits, Transformable.class);									
			break;		

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			processSelectionRectangleForTransformations(hits, Transformable.class);									
			break;	

		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			processSelectionRectangleForTransformations(hits, Dilateable.class);
			break;

		//case EuclidianConstants.MODE_CREATE_LIST:
		case EuclidianConstants.MODE_FITLINE:
			for (int i=0; i < hits.size(); i++) {
				GeoElement geo = (GeoElement) hits.get(i);
				if (!(GeoPoint.class.isInstance(geo))) {
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



	public void mouseMoved(MouseEvent e) {		

		if (textfieldHasFocus) return;

		setMouseLocation(e);
		processMouseMoved(e);
	}

	
	private boolean mouseIsOverLabel = false;
	
	/**
	 * 
	 * @return true if the mouse is over a label
	 */
	public boolean mouseIsOverLabel(){
		return mouseIsOverLabel;
	}


	protected void processMouseMoved(MouseEvent e) {	
		
		boolean repaintNeeded;
		
		// reset icon
		if (hitResetIcon()) {
			view.setToolTipText(app.getPlainTooltip("resetConstruction"));
			view.setHitCursor();
			return;
		} 

		// animation button
		boolean hitAnimationButton = view.hitAnimationButton(e);
		repaintNeeded = view.setAnimationButtonsHighlighted(hitAnimationButton);
		if (hitAnimationButton) {
			if (kernel.isAnimationPaused())
				view.setToolTipText(app.getPlainTooltip("Play"));
			else 
				view.setToolTipText(app.getPlainTooltip("Pause"));		
			view.setHitCursor();
			view.repaintView();
			return;
		}			

		// standard handling
		Hits hits = new Hits();
		boolean noHighlighting = false;		
		altDown=e.isAltDown();		

		// label hit
		GeoElement geo = view.getLabelHit(mouseLoc);
		if (geo != null)
			mouseIsOverLabel = true;
		else
			mouseIsOverLabel = false;
		if (moveMode(mode)) {	// label hit in move mode: block all other hits		
			if (geo != null) {				
				//Application.debug("hop");
				noHighlighting = true;
				tempArrayList.clear();
				tempArrayList.add(geo);
				hits = tempArrayList;						
			}
		}


		if (hits.isEmpty()){
			view.setHits(mouseLoc);
			hits = view.getHits();
			switchModeForRemovePolygons(hits);
		}

		if (hits.isEmpty()) {
			view.setToolTipText(null);
			view.setDefaultCursor();	
		}			
		else {
			if (e.isShiftDown() && hits.size() == 1 && hits.get(0) instanceof GeoAxis)
				if (((GeoAxis)hits.get(0)).getType() == GeoAxis.X_AXIS)
					view.setResizeXAxisCursor();
				else view.setResizeYAxisCursor();
			else
				view.setHitCursor();
		}


		//for testing: save the full hits for later use
		Hits tempFullHits = hits.clone();	
		//Application.debug("tempFullHits="+tempFullHits);
		
		// set tool tip text
		// the tooltips are only shown if algebra view is visible
		//if (app.isUsingLayout() && app.getGuiManager().showAlgebraView()) {
			//hits = view.getTopHits(hits);
		
		hits = hits.getTopHits();
			
			sliderValue = null;
			if (hits.size() == 1) {
				GeoElement hit = ((GeoElement)hits.get(0));
				int labelMode = hit.getLabelMode();
				if (hit.isGeoNumeric() && ((GeoNumeric)hit).isSlider() && 
						(labelMode == GeoElement.LABEL_NAME_VALUE || labelMode == GeoElement.LABEL_VALUE)) {

					// only do this if we are not pasting something from the clipboard right now
					// because moving on the label of a slider might move the pasted objects away otherwise 
					if ((pastePreviewSelected == null)?(true):(pastePreviewSelected.isEmpty())) {

						startPoint.setLocation(((GeoNumeric) hit).getSliderX(), ((GeoNumeric) hit).getSliderY());
					
						// preview just for fixed sliders
						if (((GeoNumeric) hit).isSliderFixed())					
							sliderValue = kernel.format(getSliderValue((GeoNumeric) hit));
					}
				} 
			}
			
			if (!hits.isEmpty()) {
				boolean alwaysOn = false;
				if (view instanceof EuclidianView)
					if (((EuclidianView)view).getAllowToolTips() == EuclidianView.TOOLTIPS_ON)
						alwaysOn = true;
				String text = GeoElement.getToolTipDescriptionHTML(hits,
						true, true, alwaysOn);				
				view.setToolTipText(text);
			} else
				view.setToolTipText(null);
		//}
		
		// update previewable
		if (view.getPreviewDrawable() != null) {	
			view.updatePreviewable();
			repaintNeeded = true;
		}
		
		if (pastePreviewSelected != null && !pastePreviewSelected.isEmpty()) {
			transformCoords();
			updatePastePreviewPosition();
			repaintNeeded = true;
		}

		// show Mouse coordinates, manage alt -> multiple of 15 degrees
		else if (view.getShowMouseCoords() && view.getAllowShowMouseCoords()) {
			transformCoords();
			repaintNeeded = true;
		}		

		//Application.debug(tempFullHits.getTopHits(2,10));
		//	manage highlighting & "snap to object"
		//Application.debug("noHighlighting = "+noHighlighting);
		//Application.debug("hits = "+hits.toString());		
		//repaintNeeded = noHighlighting ?  refreshHighlighting(null) : refreshHighlighting(hits) 
			//	|| repaintNeeded;
		
		repaintNeeded = noHighlighting ?  refreshHighlighting(null) : refreshHighlighting(tempFullHits) 
			|| repaintNeeded;
		if (repaintNeeded) {
			kernel.notifyRepaint();
		}
	}


	protected void doSingleHighlighting(GeoElement geo) {
		if (geo == null) return;

		if (highlightedGeos.size() > 0) {
			setHighlightedGeos(false);
		}		

		highlightedGeos.add(geo);
		geo.setHighlighted(true); 
		kernel.notifyRepaint();
	}

	// mode specific highlighting of selectable objects
	// returns wheter repaint is necessary
	public final boolean refreshHighlighting(Hits hits) {
		boolean repaintNeeded = false;
		
		//	clear old highlighting
		if (highlightedGeos.size() > 0) {
			setHighlightedGeos(false);
			repaintNeeded = true;
		}
		// find new objects to highlight
		highlightedGeos.clear();	
		selectionPreview = true; // only preview selection, see also
		// mouseReleased()
		processMode(hits, null); // build highlightedGeos List

		if (highlightJustCreatedGeos)
			highlightedGeos.addAll(justCreatedGeos); // we also highlight just created geos


		selectionPreview = false; // reactivate selection in mouseReleased()

		// set highlighted objects
		if (highlightedGeos.size() > 0) {
			setHighlightedGeos(true); 
			repaintNeeded = true;
		}		
		return repaintNeeded;
	}

	//	set highlighted state of all highlighted geos without repainting
	protected final void setHighlightedGeos(boolean highlight) {
		GeoElement geo;
		Iterator it = highlightedGeos.iterator();
		while (it.hasNext()) {
			geo = (GeoElement) it.next();
			geo.setHighlighted(highlight);
		}
	}
	
	
	
	
	protected boolean switchModeForProcessMode(Hits hits, MouseEvent e){
		
		Boolean changedKernel = false;
		GeoElement[] ret = null; 
		
		switch (mode) {
		case EuclidianConstants.MODE_VISUAL_STYLE:
		case EuclidianConstants.MODE_MOVE:
			// move() is for highlighting and selecting
			if (selectionPreview) {	
				move(hits.getTopHits());				
			} else {
				if (DRAGGING_OCCURED && app.selectedGeosSize() == 1)
					app.clearSelectedGeos();

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
				if (mode==EuclidianConstants.MODE_POINT || mode==EuclidianConstants.MODE_COMPLEX_NUMBER)
					hits.keepOnlyHitsForNewPointMode();

				point(hits);
			} else {
				GeoElement[] ret0 = { null };
				ret0[0] = hits.getFirstHit(GeoPointND.class);
				ret = ret0;
				clearSelection(selectedPoints);
			}
			break;

			// copy geo to algebra input
		case EuclidianConstants.MODE_SELECTION_LISTENER:
			boolean addToSelection = e != null && (Application.isControlDown(e));
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

			//	angle for two points and number
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
			if (showHideObject(hits.getTopHits()))
				toggleModeChangedKernel = true;
			break;

		case EuclidianConstants.MODE_SHOW_HIDE_LABEL:
			if (showHideLabel(hits.getTopHits()))
				toggleModeChangedKernel = true;
			break;

		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
			if (copyVisualStyle(hits.getTopHits()))
				toggleModeChangedKernel = true;
			break;

		//  new text
		case EuclidianConstants.MODE_TEXT:
			changedKernel = text(hits.getOtherHits(GeoImage.class, tempArrayList), mode, altDown); //e.isAltDown());
			break;
			
		//  new image
		case EuclidianConstants.MODE_IMAGE:
			changedKernel = image(hits.getOtherHits(GeoImage.class, tempArrayList), mode, altDown); //e.isAltDown());
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

		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds 2008-03-23
			ret = mirrorAtCircle(hits.getTopHits());
			break;

		case EuclidianConstants.MODE_ATTACH_DETACH: // Michael Borcherds 2008-03-23
			changedKernel = attachDetach(hits.getTopHits(), e);
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
			ret = distance(hits, e);
			break;	

		case EuclidianConstants.MODE_MACRO:			
			changedKernel = macro(hits);
			break;

		case EuclidianConstants.MODE_AREA:
			ret = area(hits, e);
			break;	

		case EuclidianConstants.MODE_SLOPE:
			ret = slope(hits);
			break;

		case EuclidianConstants.MODE_REGULAR_POLYGON:
			changedKernel = regularPolygon(hits);
			break;

		case EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX:
			changedKernel = showCheckBox(hits);
			break;

		case EuclidianConstants.MODE_BUTTON_ACTION:
			changedKernel = button(false);
			break;

		case EuclidianConstants.MODE_TEXTFIELD_ACTION:
			changedKernel = button(true);
			break;

		case EuclidianConstants.MODE_PEN:
			changedKernel = pen();
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

		if (ret != null)
			memorizeJustCreatedGeos(ret);
		else if (!selectionPreview)
			clearJustCreatedGeos();

		if (!changedKernel)
			return ret != null;

		return changedKernel;
	}

	public void memorizeJustCreatedGeos(GeoElement[] geos) {
		justCreatedGeos.clear();
		for (int i = 0; i < geos.length; i++)
		{
			if(geos[i]!=null)
				justCreatedGeos.add(geos[i]);
		}
		app.updateStyleBars();
		app.getGuiManager().updateMenubarSelection();
	}

	public void memorizeJustCreatedGeos(ArrayList<GeoElement> geos) {
		justCreatedGeos.clear();
		justCreatedGeos.addAll(geos);
		app.updateStyleBars();
		app.getGuiManager().updateMenubarSelection();
	}
	
	public void clearJustCreatedGeos()
	{
		justCreatedGeos.clear();
		app.updateStyleBars();
		
		if(app.useFullGui()) {
			app.getGuiManager().updateMenubarSelection();
		}
	}

	public ArrayList<GeoElement> getJustCreatedGeos()
	{
		return justCreatedGeos;
	}

	// process mode and return whether kernel was changed
	public final boolean processMode(Hits hits, MouseEvent e) {
		boolean changedKernel = false;

		if (hits==null)
			hits = new Hits();

		changedKernel = switchModeForProcessMode(hits, e);
		
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
	
	
	public void processModeLock(){
		
		// make previewable "lock" onto points & paths
		// priority for highlighted geos (points)
		Hits getTopHits = highlightedGeos.getTopHits();
		// nothing highlighted, look at eg circles, lines
		if (getTopHits.size() == 0) getTopHits = view.getHits().getTopHits();

		
		if (getTopHits.size() > 0 ) {
			GeoElement geo = (GeoElement)getTopHits.get(0);
			if (geo instanceof Path) {
				processModeLock((Path)geo);
			} else if (geo.isGeoPoint()) {
				processModeLock((GeoPointND) geo);
			} else transformCoords(); // grid lock
		} else transformCoords(); // grid lock
	}
	
	protected void processModeLock(Path path){
		GeoPoint p = kernel.Point(null, path, xRW, yRW, false, false);
		p.update();
		xRW = p.inhomX;
		yRW = p.inhomY;	
	}
	
	protected void processModeLock(GeoPointND point){
		Coords coords = point.getInhomCoordsInD(2);
		xRW = coords.getX();
		yRW = coords.getY();
	}

	public void mouseEntered(MouseEvent e) {

		if (textfieldHasFocus) return;

		initToolTipManager();
		initShowMouseCoords();
		view.mouseEntered();
	}

	public void mouseExited(MouseEvent e) {

		if (textfieldHasFocus) return;

		refreshHighlighting(null);
		resetToolTipManager();
		view.setAnimationButtonsHighlighted(false);
		view.setShowMouseCoords(false);
		mouseLoc = null;		
		view.repaintView();
		view.mouseExited();
	}

	/*
	public void focusGained(FocusEvent e) {				
		initToolTipManager();
	}

	public void focusLost(FocusEvent e) {
		resetToolTipManager();
	}*/

	protected void initToolTipManager() {
		// set tooltip manager
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(DEFAULT_INITIAL_DELAY / 2);
		ttm.setEnabled(app.getAllowToolTips());
	}

	protected void resetToolTipManager() {
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(DEFAULT_INITIAL_DELAY);
	}

	/* ****************************************************** */

	final protected void rotateObject(boolean repaint) {
		double angle = Math.atan2(yRW - rotationCenter.inhomY, 
				xRW - rotationCenter.inhomX)
				- rotStartAngle;

		tempNum.set(angle);
		rotGeoElement.set(rotStartGeo);	
		((PointRotateable) rotGeoElement).rotate(tempNum, rotationCenter);

		if (repaint)
			rotGeoElement.updateRepaint();
		else
			rotGeoElement.updateCascade();
	}

	final protected void moveLabel() {
		movedLabelGeoElement.setLabelOffset(oldLoc.x + mouseLoc.x
				- startLoc.x, oldLoc.y + mouseLoc.y - startLoc.y);
		// no update cascade needed
		movedLabelGeoElement.update();  
		kernel.notifyRepaint();
	}

	protected void movePoint(boolean repaint) {
		movedGeoPoint.setCoords(kernel.checkDecimalFraction(xRW), kernel.checkDecimalFraction(yRW), 1.0);
		((GeoElement) movedGeoPoint).updateCascade();	
		movedGeoPointDragged = true;

		if (repaint)
			kernel.notifyRepaint();
	}

	final protected void moveLine(boolean repaint) {
		// make parallel geoLine through (xRW, yRW)
		movedGeoLine.setCoords(movedGeoLine.x, movedGeoLine.y, 
				-(movedGeoLine.x * xRW + movedGeoLine.y * yRW));		
		if (repaint)
			movedGeoLine.updateRepaint();
		else
			movedGeoLine.updateCascade();	
	}

	final protected void moveVector(boolean repaint) {
		GeoPoint P = movedGeoVector.getStartPoint();
		if (P == null) {
			movedGeoVector.setCoords(xRW, yRW, 0.0);
		} else {
			movedGeoVector.setCoords(xRW - P.inhomX, yRW - P.inhomY, 0.0);
		}

		if (repaint)
			movedGeoVector.updateRepaint();
		else
			movedGeoVector.updateCascade();	
	}

	final protected void moveVectorStartPoint(boolean repaint) {
		GeoPoint P = movedGeoVector.getStartPoint();
		P.setCoords(xRW, yRW, 1.0);

		if (repaint)
			movedGeoVector.updateRepaint();
		else
			movedGeoVector.updateCascade();	
	}

	final protected void moveText(boolean repaint) {
		if (movedGeoText.isAbsoluteScreenLocActive()) {
			movedGeoText.setAbsoluteScreenLoc( oldLoc.x + mouseLoc.x-startLoc.x, 
					oldLoc.y + mouseLoc.y-startLoc.y);

			//part of snap to grid code - buggy, so commented out
			//movedGeoText.setAbsoluteScreenLoc(view.toScreenCoordX(xRW - startPoint.x), view.toScreenCoordY(yRW - startPoint.y));
		} else {
			if (movedGeoText.hasAbsoluteLocation()) {
				//	absolute location: change location
				GeoPoint loc = (GeoPoint) movedGeoText.getStartPoint();
				loc.setCoords(xRW - startPoint.x, yRW - startPoint.y, 1.0);
			} else {
				// relative location: move label (change label offset)
				movedGeoText.setLabelOffset(oldLoc.x + mouseLoc.x
						- startLoc.x, oldLoc.y + mouseLoc.y - startLoc.y);
			}
		}				

		if (repaint)
			movedGeoText.updateRepaint();
		else
			movedGeoText.updateCascade();	
	}

	final protected void moveImage(boolean repaint) {	
		if (movedGeoImage.isAbsoluteScreenLocActive()) {
			//movedGeoImage.setAbsoluteScreenLoc( oldLoc.x + mouseLoc.x-startLoc.x, 
			//		oldLoc.y + mouseLoc.y-startLoc.y);

			movedGeoImage.setAbsoluteScreenLoc(view.toScreenCoordX(xRW - startPoint.x), view.toScreenCoordY(yRW - startPoint.y));

			if (repaint)
				movedGeoImage.updateRepaint();
			else
				movedGeoImage.updateCascade();
		} else {
			if (movedGeoImage.hasAbsoluteLocation()) {
				//	absolute location: translate all defined corners
				double vx = xRW - startPoint.x;
				double vy = yRW - startPoint.y;
				movedGeoImage.set(oldImage);
				for (int i=0; i < 3; i++) {
					GeoPoint corner = movedGeoImage.getCorner(i);
					if (corner != null) {
						corner.setCoords(corner.inhomX + vx, corner.inhomY + vy, 1.0);
					}
				}

				if (repaint)
					movedGeoImage.updateRepaint();
				else
					movedGeoImage.updateCascade();
			} 	
		}
	}

	final protected void moveConic(boolean repaint) {
		movedGeoConic.set(tempConic);
		movedGeoConic.translate(xRW - startPoint.x, yRW - startPoint.y);		

		if (repaint)
			movedGeoConic.updateRepaint();
		else
			movedGeoConic.updateCascade();
	}
	
	final protected void moveImplicitPoly(boolean repaint) {
		movedGeoImplicitPoly.set(tempImplicitPoly);
		movedGeoImplicitPoly.translate(xRW - startPoint.x, yRW - startPoint.y);		
		

		//set points
		for (int i=0;i<moveDependentPoints.size();i++){
			GeoPoint g=moveDependentPoints.get(i);
			g.setCoords2D(tempDependentPointX.get(i),tempDependentPointY.get(i),1);
			g.translate(new Coords(xRW - startPoint.x, yRW - startPoint.y,1));
//			g.updateCascade();
		}
		
		if (repaint)
			movedGeoImplicitPoly.updateRepaint();
		else
			movedGeoImplicitPoly.updateCascade();
		
//		int i=0;
//		for (GeoElement elem:movedGeoImplicitPoly.getAllChildren()){
//			if (elem instanceof GeoPoint){
//				if (movedGeoImplicitPoly.isParentOf(elem)){
//					GeoPoint g=((GeoPoint)elem);
//					g.getPathParameter().setT(tempDependentPointOnPath.get(i++));
//					tempImplicitPoly.pathChanged(g);
//					g.translate(new Coords(xRW - startPoint.x, yRW - startPoint.y));
//				}
//			}else if (elem instanceof GeoImplicitPoly){
//				
//			}
//		}

	}

	final protected void moveFunction(boolean repaint) {
		movedGeoFunction.set(tempFunction);
		movedGeoFunction.translate(xRW - startPoint.x, yRW - startPoint.y);		

		if (repaint)
			movedGeoFunction.updateRepaint();
		else
			movedGeoFunction.updateCascade();
	}

	final protected void moveBoolean(boolean repaint) {
		//movedGeoBoolean.setAbsoluteScreenLoc( oldLoc.x + mouseLoc.x-startLoc.x, 
		//		oldLoc.y + mouseLoc.y-startLoc.y);

		// part of snap to grid code
		movedGeoBoolean.setAbsoluteScreenLoc(view.toScreenCoordX(xRW - startPoint.x), view.toScreenCoordY(yRW - startPoint.y));

		if (repaint)
			movedGeoBoolean.updateRepaint();
		else
			movedGeoBoolean.updateCascade();
	}

	final protected void moveButton(boolean repaint) {
		//movedGeoButton.setAbsoluteScreenLoc( oldLoc.x + mouseLoc.x-startLoc.x, 
		//		oldLoc.y + mouseLoc.y-startLoc.y);

		// part of snap to grid code
		movedGeoButton.setAbsoluteScreenLoc(view.toScreenCoordX(xRW - startPoint.x), view.toScreenCoordY(yRW - startPoint.y));

		if (repaint)
			movedGeoButton.updateRepaint();
		else
			movedGeoButton.updateCascade();
	}
	final protected double getSliderValue(GeoNumeric movedGeoNumeric) {
		double min = movedGeoNumeric.getIntervalMin();
		double max = movedGeoNumeric.getIntervalMax();
		double param;
		if (movedGeoNumeric.isSliderHorizontal()) {
			if (movedGeoNumeric.isAbsoluteScreenLocActive()) {				
				param = mouseLoc.x - startPoint.x;
			} else {
				param = xRW - startPoint.x;
			}
		}			
		else {
			if (movedGeoNumeric.isAbsoluteScreenLocActive()) {
				param = startPoint.y - mouseLoc.y ;
			} else {
				param = yRW - startPoint.y;
			}
		}							
		param = param * (max - min) / movedGeoNumeric.getSliderWidth();					

		// round to animation step scale				
		param = Kernel.roundToScale(param, movedGeoNumeric.getAnimationStep());
		double val = min + param;	

		if (movedGeoNumeric.getAnimationStep() > Kernel.MIN_PRECISION) {
			// round to decimal fraction, e.g. 2.800000000001 to 2.8
			val = kernel.checkDecimalFraction(val);
		}

		if (movedGeoNumeric.isGeoAngle()) {
			if (val < 0) 
				val = 0;
			else if (val > Kernel.PI_2)
				val = Kernel.PI_2;
			
			val = kernel.checkDecimalFraction(val * Kernel.CONST_180_PI) / Kernel.CONST_180_PI;

		}

		return val;
	}

	final protected void moveNumeric(boolean repaint) {
		
		double newVal = getSliderValue(movedGeoNumeric);
		double oldVal = movedGeoNumeric.getValue();
		
		// don't set the value unless needed
		// (causes update)
		double min = movedGeoNumeric.getIntervalMin();
		if (min == oldVal && newVal < min) return;
		double max = movedGeoNumeric.getIntervalMax();
		if (max == oldVal && newVal > max) return;

		// do not set value unless it really changed!
		if (oldVal == newVal)
			return;

		movedGeoNumeric.setValue(newVal);		
		movedGeoNumericDragged = true;

		//movedGeoNumeric.setAnimating(false); // stop animation if slider dragged

		//if (repaint)
		movedGeoNumeric.updateRepaint();
		//else
		//	movedGeoNumeric.updateCascade();
	}

	final protected void moveSlider(boolean repaint) {
		
		// TEMPORARY_MODE true -> dragging slider using Slider Tool
		// or right-hand mouse button
		
		if (movedGeoNumeric.isAbsoluteScreenLocActive()) {
			//movedGeoNumeric.setAbsoluteScreenLoc( oldLoc.x + mouseLoc.x-startLoc.x, 
			//		oldLoc.y + mouseLoc.y-startLoc.y, TEMPORARY_MODE);

			// part of snap to grid code
			movedGeoNumeric.setAbsoluteScreenLoc(view.toScreenCoordX(xRW - startPoint.x), view.toScreenCoordY(yRW - startPoint.y), TEMPORARY_MODE);
		} else {
			movedGeoNumeric.setSliderLocation(xRW - startPoint.x, yRW - startPoint.y, TEMPORARY_MODE);
		}		

		// don't cascade, only position of the slider has changed
		movedGeoNumeric.update();

		if (repaint)
			kernel.notifyRepaint();					
	}

	protected void moveDependent(boolean repaint) {

		translationVec.setX(xRW - startPoint.x);
		translationVec.setY(yRW - startPoint.y);

		startPoint.setLocation(xRW, yRW);

		// we don't specify screen coords for translation as all objects are Transformables
		GeoElement.moveObjects(translateableGeos, translationVec, new Coords(xRW, yRW, 0), null);		
		if (repaint)
			kernel.notifyRepaint();						
	}

	protected void moveMultipleObjects(boolean repaint) {		
		translationVec.setX(xRW - startPoint.x);
		translationVec.setY(yRW - startPoint.y);
		startPoint.setLocation(xRW, yRW);
		startLoc = mouseLoc;
		
		// move all selected geos
		GeoElement.moveObjects(removeParentsOfView(app.getSelectedGeos()), translationVec, new Coords(xRW, yRW, 0), null);									

		if (repaint)
			kernel.notifyRepaint();					
	}		
	

	protected ArrayList<GeoElement> removeParentsOfView(ArrayList<GeoElement> list){
		return list;
	}


	/**
	 * COORD TRANSFORM SCREEN -> REAL WORLD
	 * 
	 * real world coords -> screen coords 
	 *     ( xscale 0 xZero ) 
	 * T = ( 0 -yscale yZero ) 
	 *     ( 0 0 1 )
	 * 
	 * screen coords -> real world coords 
	 *          ( 1/xscale 0 -xZero/xscale ) 
	 * T^(-1) = ( 0 -1/yscale yZero/yscale ) 
	 *          ( 0 0 1 )
	 */

	/*
	protected void transformCoords() {
		transformCoords(false);
	} */

	protected void transformCoords() {
		// calc real world coords
		calcRWcoords();

		// if alt pressed, make sure slope is a multiple of 15 degrees
		if ((mode == EuclidianConstants.MODE_JOIN || mode == EuclidianConstants.MODE_SEGMENT
				|| mode == EuclidianConstants.MODE_RAY || mode == EuclidianConstants.MODE_VECTOR
				|| mode == EuclidianConstants.MODE_POLYGON || mode == EuclidianConstants.MODE_POLYLINE)
				&& useLineEndPoint && lineEndPoint != null) {
			xRW = lineEndPoint.x;
			yRW = lineEndPoint.y;
			return;
		}

		if (mode == EuclidianConstants.MODE_MOVE && 
				moveMode == MOVE_NUMERIC) return; // Michael Borcherds 2008-03-24 bugfix: don't want grid on

		//	point capturing to grid
		double pointCapturingPercentage = 1;
		switch (view.getPointCapturingMode()) {

		case EuclidianView.POINT_CAPTURING_STICKY_POINTS:
			pointCapturingPercentage = 0.125;
			ArrayList<GeoPointND> spl = view.getStickyPointList();
			boolean captured = false;
			if (spl != null) {
				for (int i = 0; i < spl.size(); i++) {
					GeoPoint gp = (GeoPoint)spl.get(i);
					if (Math.abs(gp.getInhomX() - xRW) < view.getGridDistances(0) * pointCapturingPercentage &&
						Math.abs(gp.getInhomY() - yRW) < view.getGridDistances(1) * pointCapturingPercentage) {
						xRW = gp.getInhomX();
						yRW = gp.getInhomY();
						captured = true;
						break;
					}
				}
			}
			if (captured)
				break;

		case EuclidianView.POINT_CAPTURING_AUTOMATIC:				
			if (!view.isGridOrAxesShown())break;

		case EuclidianView.POINT_CAPTURING_ON:
			pointCapturingPercentage = 0.125;

		case EuclidianView.POINT_CAPTURING_ON_GRID:
			
			xRW += transformCoordsOffset[0];
			yRW += transformCoordsOffset[1];

			switch (view.getGridType()) {
			case EuclidianView.GRID_ISOMETRIC:

				// isometric Michael Borcherds 2008-04-28
				// iso grid is effectively two rectangular grids overlayed (offset)
				// so first we decide which one we're on (oddOrEvenRow)
				// then compress the grid by a scale factor of root3 horizontally to make it square.

				double root3=Math.sqrt(3.0);
				double isoGrid=view.getGridDistances(0);
				int oddOrEvenRow = (int)Math.round(2.0*Math.abs(yRW - Kernel.roundToScale(yRW, isoGrid))/isoGrid);

				//Application.debug(oddOrEvenRow);

				if (oddOrEvenRow == 0)
				{
					// X = (x, y) ... next grid point
					double x = Kernel.roundToScale(xRW/root3, isoGrid);
					double y = Kernel.roundToScale(yRW, isoGrid);
					// if |X - XRW| < gridInterval * pointCapturingPercentage  then take the grid point
					double a = Math.abs(x - xRW/root3);
					double b = Math.abs(y - yRW);
					if (a < isoGrid * pointCapturingPercentage
							&& b < isoGrid *  pointCapturingPercentage) {
						xRW = x*root3 - transformCoordsOffset[0];
						yRW = y - transformCoordsOffset[1];
					} else {
						xRW -= transformCoordsOffset[0];
						yRW -= transformCoordsOffset[1];
					}

				}
				else
				{
					// X = (x, y) ... next grid point
					double x = Kernel.roundToScale(xRW/root3- view.getGridDistances(0)/2, isoGrid);
					double y = Kernel.roundToScale(yRW- isoGrid/2, isoGrid);
					// if |X - XRW| < gridInterval * pointCapturingPercentage  then take the grid point
					double a = Math.abs(x - (xRW/root3- isoGrid/2));
					double b = Math.abs(y - (yRW-isoGrid/2));
					if (a < isoGrid * pointCapturingPercentage
							&& b < isoGrid *  pointCapturingPercentage) {
						xRW = (x+ isoGrid/2)*root3 - transformCoordsOffset[0];
						yRW = y+ isoGrid/2 - transformCoordsOffset[1];
					} else {
						xRW -= transformCoordsOffset[0];
						yRW -= transformCoordsOffset[1];
					}

				}
				break;

			case EuclidianView.GRID_CARTESIAN:

				// X = (x, y) ... next grid point
				
				double x = Kernel.roundToScale(xRW, view.getGridDistances(0));
				double y = Kernel.roundToScale(yRW, view.getGridDistances(1));
				
				
				
				
				// if |X - XRW| < gridInterval * pointCapturingPercentage  then take the grid point
				double a = Math.abs(x - xRW);
				double b = Math.abs(y - yRW);

				if (a < view.getGridDistances(0) * pointCapturingPercentage
						&& b < view.getGridDistances(1) *  pointCapturingPercentage) {
					xRW = x - transformCoordsOffset[0];
					yRW = y - transformCoordsOffset[1];
				} else {
					xRW -= transformCoordsOffset[0];
					yRW -= transformCoordsOffset[1];
				}
				break;
		
			
		
		case EuclidianView.GRID_POLAR:
				
			// r = get nearest grid circle radius
			double r = GeoVec2D.length(xRW, yRW);
			double r2 = Kernel.roundToScale(r, view.getGridDistances(0));
			
			// get nearest radial gridline angle
			double angle = Math.atan2(yRW, xRW);
			double angleOffset = angle % view.getGridDistances(2);
			if(angleOffset < view.getGridDistances(2)/2)
				angle = angle - angleOffset;
			else
				angle = angle - angleOffset + view.getGridDistances(2);

			// get grid point
			double x1 = r2*Math.cos(angle);
			double y1 = r2*Math.sin(angle);
			
			// if |X - XRW| < gridInterval * pointCapturingPercentage  then take the grid point
			double a1 = Math.abs(x1 - xRW);
			double b1 = Math.abs(y1 - yRW);

			if (a1 < view.getGridDistances(0) * pointCapturingPercentage
					&& b1 < view.getGridDistances(1) *  pointCapturingPercentage) {
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

	/*
	final protected void transformCoords(boolean usePointCapturing) {
		// calc real world coords
		calcRWcoords();

		if (usePointCapturing) {	
			double pointCapturingPercentage = 1;
			switch (view.getPointCapturingMode()) {		
				case EuclidianConstants.POINT_CAPTURING_AUTOMATIC:
					if (!view.isGridOrAxesShown())break;

				case EuclidianView.POINT_CAPTURING_ON:
					pointCapturingPercentage = 0.125;

				case EuclidianView.POINT_CAPTURING_ON_GRID:								
					// X = (x, y) ... next grid point
					double x = Kernel.roundToScale(xRW, view.gridDistances[0]);
					double y = Kernel.roundToScale(yRW, view.gridDistances[1]);
					// if |X - XRW| < gridInterval * pointCapturingPercentage  then take the grid point
					double a = Math.abs(x - xRW);
					double b = Math.abs(y - yRW);
					if (a < view.gridDistances[0] * pointCapturingPercentage
						&& b < view.gridDistances[1] *  pointCapturingPercentage) {
						xRW = x;
						yRW = y;
						mouseLoc.x = view.toScreenCoordX(xRW);
						mouseLoc.y = view.toScreenCoordY(yRW);
					}

				default:
					// point capturing off
			}		
		}
	}
	 */

	protected void calcRWcoords() {
		xRW = (mouseLoc.x - view.getXZero()) * view.getInvXscale();
		yRW = (view.getYZero() - mouseLoc.y) * view.getInvYscale();
	}

	protected void setMouseLocation(MouseEvent e) {
		mouseLoc = e.getPoint();

		altDown = e.isAltDown();

		if (mouseLoc.x < 0)
			mouseLoc.x = 0;
		else if (mouseLoc.x > view.getViewWidth())
			mouseLoc.x = view.getViewWidth();
		if (mouseLoc.y < 0)
			mouseLoc.y = 0;
		else if (mouseLoc.y > view.getViewHeight())
			mouseLoc.y = view.getViewHeight();
	}

	/***************************************************************************
	 * mode implementations
	 * 
	 * the following methods return true if a factory method of the kernel was
	 * called
	 **************************************************************************/

	private boolean allowPointCreation() {
		return  mode == EuclidianConstants.MODE_POINT || 
		mode == EuclidianConstants.MODE_POINT_ON_OBJECT ||
		mode == EuclidianConstants.MODE_COMPLEX_NUMBER ||
		app.isOnTheFlyPointCreationActive(); 
	}

	final protected boolean createNewPoint(Hits hits,
			boolean onPathPossible, boolean intersectPossible, boolean doSingleHighlighting) {

		// inRegionpossible must be false so that the Segment Tool creates a point on the edge of a circle
		return createNewPoint(hits,onPathPossible, false, intersectPossible,  doSingleHighlighting, false);		
	}

	// create new point at current position if hits is null
	// or on path
	// or intersection point
	// returns wether new point was created or not
	final protected boolean createNewPoint(Hits hits,
			boolean onPathPossible, boolean inRegionPossible, boolean intersectPossible, boolean doSingleHighlighting, boolean complex) {

		if (!allowPointCreation()) 
			return false;



		GeoPointND point = getNewPoint(hits, onPathPossible, inRegionPossible, intersectPossible, doSingleHighlighting, complex);

		if (point != null) {

			updateMovedGeoPoint(point);

			movedGeoElement = getMovedGeoPoint();
			moveMode = MOVE_POINT;
			view.setDragCursor();
			if (doSingleHighlighting)
				doSingleHighlighting(getMovedGeoPoint());
			POINT_CREATED = true;

			return true;
		} else {
			moveMode = MOVE_NONE;
			POINT_CREATED = false;
			return false;
		}
	}


	// creates or get the new point (used for 3D)
	protected GeoPointND getNewPoint(Hits hits,
			boolean onPathPossible, boolean inRegionPossible, boolean intersectPossible, 
			boolean doSingleHighlighting, boolean complex) {

		return updateNewPoint(false, hits, 
				onPathPossible, inRegionPossible, intersectPossible, 
				doSingleHighlighting, true, complex);
	}
	
	protected Hits getRegionHits(Hits hits){
		return hits.getRegionHits(tempArrayList);
	}

		
	// update the new point (used for preview in 3D)
	public GeoPointND updateNewPoint(
			boolean forPreviewable,
			Hits hits,
			boolean onPathPossible, boolean inRegionPossible, boolean intersectPossible,
			boolean doSingleHighlighting, boolean chooseGeo, boolean complex) {

		// create hits for region
		Hits regionHits = getRegionHits(hits);

		// only keep polygon in hits if one side of polygon is in hits too
		// removed: Point Tool creates Point on edge of Polygon
		if (mode != EuclidianConstants.MODE_POINT
				&& mode != EuclidianConstants.MODE_POINT_ON_OBJECT
				&& mode != EuclidianConstants.MODE_COMPLEX_NUMBER
				&& !hits.isEmpty())
			hits.keepOnlyHitsForNewPointMode();
		
		//Application.debug(hits);
		
		Path path = null;	
		Region region = null;
		boolean createPoint = true;
		if (hits.containsGeoPoint()){
			createPoint = false;
			if (forPreviewable){
				createNewPoint((GeoPointND) hits.getHits(GeoPointND.class, tempArrayList).get(0));				
			}
		}


		GeoPointND point = null;

		
		//	try to get an intersection point
		if (createPoint && intersectPossible) {
			GeoPointND intersectPoint = getSingleIntersectionPoint(hits);
			if (intersectPoint != null) {
				if (!forPreviewable){
					point = intersectPoint;
					// we don't use an undefined or infinite
					// intersection point
					if (!point.showInEuclidianView()) {
						point.remove();
					} else
						createPoint = false;
				}else{
					createNewPointIntersection(intersectPoint);
					createPoint = false;
				}
			}
		}


		//Application.debug(hits+"\ncreatePoint="+createPoint+"\ninRegionPossible="+inRegionPossible+"\nchooseGeo="+chooseGeo);

		// check for paths and regions
		if (createPoint) {
			
			boolean createPointOnBoundary = false;
			
			// check if point lies in a region and if we are allowed to place a point
			// in a region
			if (!regionHits.isEmpty()) {
				if (inRegionPossible) {
					if (chooseGeo)
						region = (Region) chooseGeo(regionHits, true);
					else
						region = (Region) regionHits.get(0);
					if (region != null){
						if (((GeoElement) region).isGeoPolygon()){
							GeoSegmentND [] sides = ((GeoPolygon) region).getSegments();
							boolean sideInHits = false;
							for (int k=0; k < sides.length; k++) {
								//sideInHits = sideInHits || hits.remove(sides[k]); //not removing sides, just test
								if (hits.contains(sides[k])) {
									sideInHits = true;
									break;
								}
							}
							
							
							if (!sideInHits) {
								createPoint = true;	
								hits.removePolygonsIfSideNotPresent(); // if a polygon is a region, need only polygons that should be a path
								if (mode==EuclidianConstants.MODE_POINT_ON_OBJECT)
									hits.removeSegmentsFromPolygons(); // remove polygon's segments to take the polygon for path
							} else {
								if (mode==EuclidianConstants.MODE_POINT_ON_OBJECT){
									// if one wants a point on boundary of a polygon
									createPoint=false;
									createPointOnBoundary = true;
								}else{
									createPoint = false;
									hits.remove(region); // (OPTIONAL) if side is in hits, still don't need the polygon as a path
									region = null;
								}
							}
						}else if (((GeoElement) region).isGeoConic()){
							if (mode==EuclidianConstants.MODE_POINT_ON_OBJECT && ((GeoConicND) region).getLastHitType()==GeoConicND.HIT_TYPE_ON_FILLING){
								createPoint = true;	
								hits.remove(region); // conic won't be treated as a path
							}else{
								createPoint = true;
							}
						}
					}else
						createPoint = true;
				} else {
					createPoint = true;
					// if inRegionPossible is false, the point is created as a free point
				}

			}


			//check if point lies on path and if we are allowed to place a point
			// on a path	
			if (createPointOnBoundary){
				//special case for MODE_POINT_ON_OBJECT : if an edge of a polygon is clicked, create Point[polygon]
				path = (Path) region;
				region = null;
				createPoint = true;
			}else{
				Hits pathHits = hits.getHits(Path.class, tempArrayList);
				if (!pathHits.isEmpty()) {
					if (onPathPossible) {
						if (chooseGeo)
							path = (Path) chooseGeo(pathHits, true);
						else
							path = (Path) pathHits.get(0);
						createPoint = path != null;
					} else {
						createPoint = true; 
					}
				}
			}

		}



		//Application.debug("createPoint 3 = "+createPoint);

		if (createPoint) {
			transformCoords(); // use point capturing if on
			//branches reordered to prefer path, and then region
			if (path != null && onPathPossible) {
				point = createNewPoint(forPreviewable, path, complex);
			} else if (region != null && inRegionPossible) {
				point = createNewPoint(forPreviewable, region, complex);
			} else {
				point = createNewPoint(forPreviewable, complex);
				view.setShowMouseCoords(true);
			}
		}

		return point;
	}

	/** only used in 3D */
	protected void createNewPoint(GeoPointND sourcePoint){


	}

	/** only used in 3D */
	protected void createNewPointIntersection(GeoPointND intersectionPoint){
	}
	

	protected GeoPointND createNewPoint(boolean forPreviewable, boolean complex){
		GeoPointND ret = kernel.Point(null, kernel.checkDecimalFraction(xRW), kernel.checkDecimalFraction(yRW), complex);
		return ret;
	}

	protected GeoPointND createNewPoint(boolean forPreviewable, Path path, boolean complex){
		return createNewPoint(forPreviewable, path, kernel.checkDecimalFraction(xRW), kernel.checkDecimalFraction(yRW), 0, complex);
	}
	
	protected GeoPointND createNewPoint(boolean forPreviewable, Path path, double x, double y, double z, boolean complex){
		
		if (((GeoElement) path).isGeoElement3D()) //check if the path is not a 3D element
			return (GeoPointND) kernel.getManager3D().Point3D(null, path, x, y, z,!forPreviewable);
		else
			return createNewPoint2D(forPreviewable, path, x, y, complex);

	}
	
	protected GeoPointND createNewPoint2D(boolean forPreviewable, Path path, double x, double y, boolean complex){
		return kernel.Point(null, path, x, y, !forPreviewable, complex);
	}
	
	
	
	protected GeoPointND createNewPoint(boolean forPreviewable, Region region, boolean complex){
		return createNewPoint(forPreviewable, region, kernel.checkDecimalFraction(xRW), kernel.checkDecimalFraction(yRW), 0, complex);
	}
	

	protected GeoPointND createNewPoint(boolean forPreviewable, Region region, double x, double y, double z, boolean complex){
		
		if (((GeoElement) region).isGeoElement3D()) //check if the path is not a 3D element
			return (GeoPointND) kernel.getManager3D().Point3DIn(null, region, new Coords(x, y, z, 1), !forPreviewable);
		else
			return createNewPoint2D(forPreviewable, region, x, y, complex);

	}
	

	protected GeoPointND createNewPoint2D(boolean forPreviewable, Region region, double x, double y, boolean complex){
		GeoPointND ret = kernel.PointIn(null, region, x, y, true, complex);
		return ret;
	}
	
	protected void updateMovedGeoPoint(GeoPointND point){
		movedGeoPoint = point;
	}







	// get two points and create line through them
	final protected GeoElement[] join(Hits hits) {
		if (hits.isEmpty())
			return null;

		// points needed
		addSelectedPoint(hits, 2, false);
		//Application.debug("addSelectedPoint : "+hits+"\nselectedPoints = "+selectedPoints);
		if (selPoints() == 2) {
			// fetch the two selected points
			return join();
		}
		return null;
	}

	// fetch the two selected points
	/*
	protected void join(){
		GeoPoint[] points = getSelectedPoints();
		GeoLine line = kernel.Line(null, points[0], points[1]);
	}
	*/
	
	protected GeoElement[] join(){
		GeoPointND[] points = getSelectedPointsND();
		GeoElement[] ret = { null };
		if (((GeoElement) points[0]).isGeoElement3D() || ((GeoElement) points[1]).isGeoElement3D())
			ret[0] = getKernel().getManager3D().Line3D(null,points[0], points[1]);
		else
			ret[0] = getKernel().Line(null, (GeoPoint) points[0], (GeoPoint) points[1]);
		return ret;
	}

	private boolean noPointsIn(Hits hits)
	{
		for (int i = 0 ; i < hits.size(); i++) {
			if ( ((GeoElement)(hits.get(i))).isGeoPoint()) return false;
		}
		return true;
	}

	//	get two points and create line through them
	final protected GeoElement[] segment(Hits hits) {
		if (hits.isEmpty())
			return null;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
			return segment();
			/*
			GeoPoint[] points = getSelectedPoints();
			kernel.Segment(null, points[0], points[1]);
			 */
		}
		return null;
	}


	// fetch the two selected points for segment
	protected GeoElement[] segment(){
		GeoPointND[] points = getSelectedPointsND();
		GeoElement[] ret = { null };
		if (((GeoElement) points[0]).isGeoElement3D() || ((GeoElement) points[1]).isGeoElement3D())
			ret[0] = getKernel().getManager3D().Segment3D(null,points[0], points[1]);
		else
			ret[0] = getKernel().Segment(null, (GeoPoint) points[0], (GeoPoint) points[1]);
		return ret;
	}

	// get two points and create vector between them
	final protected GeoElement[] vector(Hits hits) {
		if (hits.isEmpty())
			return null;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
			/*
			GeoPoint[] points = getSelectedPoints();
			kernel.Vector(null, points[0], points[1]);
			*/
			return vector();
		}
		return null;
	}
	
	// fetch the two selected points for vector
	protected GeoElement[] vector(){
		GeoPointND[] points = getSelectedPointsND();
		GeoElement[] ret = { null };
		if ( ((GeoElement) points[0]).isGeoElement3D() || ((GeoElement) points[1]).isGeoElement3D() )
			ret[0] = kernel.getManager3D().Vector3D(null, points[0], points[1]);
		else
			ret[0] = kernel.Vector(null, (GeoPoint) points[0], (GeoPoint) points[1]);
		return ret;
	}

	//	get two points and create ray with them
	final protected GeoElement[] ray(Hits hits) {
		if (hits.isEmpty())
			return null;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
			/*
			GeoPoint[] points = getSelectedPoints();
			kernel.Ray(null, points[0], points[1]);
			 */
			return ray();
		}

		return null;
	}


	// fetch the two selected points for ray
	protected GeoElement[] ray(){
		GeoPointND[] points = getSelectedPointsND();
		GeoElement[] ret = { null };
		if (((GeoElement) points[0]).isGeoElement3D() || ((GeoElement) points[1]).isGeoElement3D())
			ret[0] = getKernel().getManager3D().Ray3D(null,points[0], points[1]);
		else
			ret[0] = getKernel().Ray(null, (GeoPoint) points[0], (GeoPoint) points[1]);
		return ret;
	}


	//	get at least 3 points and create polygon with them
	final protected GeoElement[] polygon(Hits hits) {
		if (hits.isEmpty())
			return null;

		// if the first point is clicked again, we are finished
		if (selPoints() > 2) {
			// check if first point was clicked again
			boolean finished = !selectionPreview
			&& hits.contains(selectedPoints.get(0));
			if (finished) {
				// build polygon
				return polygon();
				//kernel.Polygon(null, getSelectedPoints());
			}
		}

		// points needed
		if ( (polygonMode == POLYGON_RIGID || polygonMode == POLYGON_VECTOR)
				&& selPoints() > 0) { // only want free points withput children for rigid polys (apart from first)
			GeoElement geo = chooseGeo(hits, false);
			if (geo == null || !geo.isGeoPoint() || !geo.isIndependent() || geo.hasChildren()) {
				//addToSelectionList(selectedPoints, geo, GeoPolygon.POLYGON_MAX_POINTS);
				return null;
			}
		}
		addSelectedPoint(hits, GeoPolygon.POLYGON_MAX_POINTS, false);
		return null;
	}

	//	get at least 3 points and create polygon with them
	final protected GeoElement[] polyline(Hits hits) {
		if (hits.isEmpty())
			return null;

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


	// build polygon	
	protected GeoElement[] polygon(){
		if (polygonMode == POLYGON_RIGID) {
			GeoElement[] ret = { null };
			GeoElement [] ret0 = kernel.RigidPolygon(null, getSelectedPoints());
			if (ret0 != null)
				ret[0] = ret0[0];
			return ret;
		} else if (polygonMode == POLYGON_VECTOR) {
			GeoElement[] ret = { null };
			GeoElement [] ret0 = kernel.VectorPolygon(null, getSelectedPoints());
			if (ret0 != null)
				ret[0] = ret0[0];
			return ret;
		} else {
			//check if there is a 3D point
			GeoPointND[] pointsND = getSelectedPointsND();
			GeoPoint[] points = new GeoPoint[pointsND.length];
			boolean point3D = false;
			for (int i=0; i<pointsND.length && !point3D; i++)
				if (((GeoElement) pointsND[i]).isGeoElement3D())
					point3D=true;
				else 
					points[i]=(GeoPoint) pointsND[i];
			if (point3D) {
				GeoElement[] ret = { null };
				GeoElement [] ret0 = kernel.getManager3D().Polygon3D(null, pointsND);
				if (ret0 != null)
					ret[0] = ret0[0];
				return ret;
			} else {
				GeoElement[] ret = { null };
				GeoElement[] ret0 = kernel.Polygon(null, points);
				if (ret0 != null)
					ret[0] = ret0[0];
				return ret;
			}
		}
	}

	// get two objects (lines or conics) and create intersection point
	protected GeoElement[] intersect(Hits hits) {
		//Application.debug(selectedLines);
		
		// obscure bug: intersection of x=0 and (x-1)^2+(y-1)^=1 can intersect x=0 and y axis sometimes
		if (hits.size() > 2) removeAxes(hits);
		
		if (hits.isEmpty())
			return null;

		// when two objects are selected at once then only one single
		// intersection point should be created
		boolean singlePointWanted = selGeos() == 0;

		// check how many interesting hits we have
		if (!selectionPreview && hits.size() > 2 - selGeos()) {
			Hits goodHits = new Hits();
			//goodHits.add(selectedGeos);
			hits.getHits(GeoLine.class, tempArrayList);
			goodHits.addAll(tempArrayList);
			
			if (goodHits.size() < 2) {
				hits.getHits(GeoConic.class, tempArrayList);
				goodHits.addAll(tempArrayList);
			}
			if (goodHits.size() < 2) {
				hits.getHits(GeoFunction.class, tempArrayList);
				goodHits.addAll(tempArrayList);
			}
			if (goodHits.size() < 2) {
				hits.getHits(GeoPolygon.class, tempArrayList);
				goodHits.addAll(tempArrayList);
			}
			if (goodHits.size() < 2) {
				hits.getHits(GeoPolyLine.class, tempArrayList);
				goodHits.addAll(tempArrayList);		
			}
			
			//if (goodHits.size() > 2 - selGeos()) {
			//	//  choose one geo, and select only this one
			//	GeoElement geo = chooseGeo(goodHits, true);
			//	hits.clear();
			//	hits.add(geo);				
			//} else {
				hits = goodHits;
			//}
		}			

		// get lines, conics and functions
		// now there's no popup chooser, when we use the intersect Tool where multiple objects intersect
		// just choose any 2
		addSelectedLine(hits, 10, true);
		addSelectedConic(hits, 10, true);
		addSelectedFunction(hits, 10, true);	
		addSelectedImplicitpoly(hits, 10, true);
		addSelectedPolygon(hits, 10, true);
		addSelectedPolyLine(hits, 10, true);
		
		singlePointWanted = singlePointWanted && selGeos() >= 2;

		//if (selGeos() > 2)
		//	return false;

		// two lines
		if (selLines() >= 2) {
			GeoLineND[] lines = getSelectedLinesND();
			GeoElement[] ret = { null };
			ret[0] = (GeoElement) kernel.IntersectLines(null, lines[0], lines[1]);
			return ret;
		}
		// two conics
		else if (selConics() >= 2) {
			GeoConicND[] conics = getSelectedConicsND();
			GeoElement[] ret = { null };
			if (singlePointWanted)
				ret[0] = kernel.IntersectConicsSingle(null, (GeoConic) conics[0], (GeoConic) conics[1], xRW,
						yRW);
			else
				ret = (GeoElement[]) kernel.IntersectConics(null, conics[0], conics[1]);
			return ret;
		} else if (selFunctions() >= 2) {
			GeoFunction[] fun = getSelectedFunctions();
			boolean polynomials = fun[0].isPolynomialFunction(false)
			&& fun[1].isPolynomialFunction(false);
			if (!polynomials) {
				GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
				startPoint.setCoords(xRW, yRW, 1.0);
				return new GeoElement[] {
						kernel.IntersectFunctions(null, fun[0], fun[1], startPoint)
						};
			} else {
				// polynomials
				if (singlePointWanted) {
					return new GeoElement[] {
						kernel.IntersectPolynomialsSingle(null, fun[0], fun[1],
								xRW, yRW)
							};
				} else {
					return
						kernel.IntersectPolynomials(null, fun[0], fun[1]);
				}
			}
		}
		// one line and one conic
		else if (selLines() >= 1 && selConics() >= 1) {
			GeoConic[] conic = getSelectedConics();
			GeoLine[] line = getSelectedLines();
			GeoElement[] ret = { null };
			if (singlePointWanted)
				ret[0] = kernel.IntersectLineConicSingle(null, line[0], conic[0], xRW,
						yRW);
			else
				ret = kernel.IntersectLineConic(null, line[0], conic[0]);

			return ret;
		}
		// line and polyLine
		else if (selLines()>=1 && selPolyLines() >=1) {
			GeoLine line = getSelectedLines()[0];
			GeoPolyLine polyLine = getSelectedPolyLines()[0];
			GeoElement[] ret = { null };
			ret = kernel.IntersectLinePolyLine(new String[] {null}, line, polyLine);
			return ret;
		}
		// line and polygon
		else if (selLines()>=1 && selPolygons() >=1) {
			GeoLine line = getSelectedLines()[0];
			GeoPolygon polygon = getSelectedPolygons()[0];
			GeoElement[] ret = { null };
			ret = kernel.IntersectLinePolygon(new String[] {null}, line, polygon);
			return ret;
		}
		// line and function
		else if (selLines() >= 1 && selFunctions() >= 1) {
			GeoLine[] line = getSelectedLines();
			GeoFunction[] fun = getSelectedFunctions();
			GeoElement[] ret = { null };
			if (fun[0].isPolynomialFunction(false)) {
				if (singlePointWanted)
					ret[0] = kernel.IntersectPolynomialLineSingle(null, fun[0], line[0],
							xRW, yRW);
				else
					ret = kernel.IntersectPolynomialLine(null, fun[0], line[0]);
			} else {
				GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
				startPoint.setCoords(xRW, yRW, 1.0);
				ret[0] = kernel.IntersectFunctionLine(null, fun[0], line[0], startPoint);
			}
			return ret;
		// function and conic
		}else if (selFunctions() >= 1 && selConics() >= 1){
			GeoConic[] conic = getSelectedConics();
			GeoFunction[] fun = getSelectedFunctions();
			//if (fun[0].isPolynomialFunction(false)){
				if (singlePointWanted)
					return new GeoElement[] {
						kernel.IntersectPolynomialConicSingle(null, fun[0], conic[0],
							xRW, yRW)
				};
				else
					return
						kernel.IntersectPolynomialConic(null, fun[0], conic[0]);
			//}
		}else if (selImplicitpoly() >= 1){
			if (selFunctions() >=1 ){
				GeoImplicitPoly p=getSelectedImplicitpoly()[0];
				GeoFunction fun=getSelectedFunctions()[0];
				//if (fun.isPolynomialFunction(false)){
					if (singlePointWanted){
						return new GeoElement[] {
								kernel.IntersectImplicitpolyPolynomialSingle(null, p, fun, xRW, yRW)
						};
					}else
						return 
							kernel.IntersectImplicitpolyPolynomial(null, p, fun);
				//}else
				//	return null;
			}else if (selLines() >= 1){
				GeoImplicitPoly p=getSelectedImplicitpoly()[0];
				GeoLine l=getSelectedLines()[0];
				if (singlePointWanted){
					return new GeoElement[] {
							kernel.IntersectImplicitpolyLineSingle(null, p, l, xRW, yRW)
					};
				}else
					return
						kernel.IntersectImplicitpolyLine(null, p, l);
			}else if (selConics() >= 1){
				GeoImplicitPoly p=getSelectedImplicitpoly()[0];
				GeoConic c=getSelectedConics()[0];
				if (singlePointWanted){
					return new GeoElement[] {
							kernel.IntersectImplicitpolyConicSingle(null, p, c, xRW, yRW)
					};
				}else
					return
						kernel.IntersectImplicitpolyConic(null, p, c);
			}else if (selImplicitpoly() >= 2){
				GeoImplicitPoly[] p=getSelectedImplicitpoly();
				if (singlePointWanted){
					return new GeoElement[] {
							kernel.IntersectImplicitpolysSingle(null, p[0], p[1], xRW, yRW)
					};
				}else
					return
						kernel.IntersectImplicitpolys(null, p[0], p[1]);
			}
		}
		return null;
	}

	// tries to get a single intersection point for the given hits
	// i.e. hits has to include two intersectable objects.
	protected GeoPointND getSingleIntersectionPoint(Hits hits) {
		if (hits.isEmpty() || hits.size() < 2)
			return null;

		GeoElement a = (GeoElement) hits.get(0);
		GeoElement b = (GeoElement) hits.get(1);
		
		// first hit is a line
		if (a.isGeoLine()) {
			if (b.isGeoLine())
				if (!((GeoLine) a).linDep((GeoLine) b))
					return kernel.IntersectLines(null, (GeoLine) a, (GeoLine) b);
				else 
					return null;
			else if (b.isGeoConic())
				return kernel.IntersectLineConicSingle(null, (GeoLine) a,
						(GeoConic) b, xRW, yRW);
			else if (b.isGeoFunctionable()) {
				// line and function
				GeoFunction f = ((GeoFunctionable) b).getGeoFunction();
				if (f.isPolynomialFunction(false))
					return kernel.IntersectPolynomialLineSingle(null, f,
							(GeoLine) a, xRW, yRW);
				else {
					GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
					startPoint.setCoords(xRW, yRW, 1.0);
					return kernel.IntersectFunctionLine(null, f, (GeoLine) a,
							startPoint);
				}
			} else
				return null;
		}
		//	first hit is a conic
		else if (a.isGeoConic()) {
			if (b.isGeoLine())
				return kernel.IntersectLineConicSingle(null, (GeoLine) b,
						(GeoConic) a, xRW, yRW);
			else if (b.isGeoConic() && !a.isEqual(b)) {
				return kernel.IntersectConicsSingle(null, (GeoConic) a,
						(GeoConic) b, xRW, yRW);
			}
			else
				return null;
		}
		// first hit is a function
		else if (a.isGeoFunctionable()) {
			GeoFunction aFun = ((GeoFunctionable) a).getGeoFunction();
		    if (b.isGeoLine()) {
				// line and function
				if (aFun.isPolynomialFunction(false))
					return kernel.IntersectPolynomialLineSingle(null, aFun,
							(GeoLine) b, xRW, yRW);
				else {
					GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
					startPoint.setCoords(xRW, yRW, 1.0);
					return kernel.IntersectFunctionLine(null, aFun, (GeoLine) b,
							startPoint);
				}
			} 
		    else if (b.isGeoFunctionable()) {
				GeoFunction bFun = ((GeoFunctionable) b).getGeoFunction();
				if (aFun.isPolynomialFunction(false) && bFun.isPolynomialFunction(false))
					return kernel.IntersectPolynomialsSingle(null, aFun, bFun,
							xRW, yRW);
				else {
					GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
					startPoint.setCoords(xRW, yRW, 1.0);
					return kernel.IntersectFunctions(null, aFun, bFun,
							startPoint);
				}
			} 		    
		    else
				return null;
		} else
			return null;
	}

	// get point and line or vector;
	// create line through point parallel to line or vector
	final protected GeoElement[] parallel(Hits hits) {
		if (hits.isEmpty())
			return null;

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
				if (((GeoElement) points[0]).isGeoElement3D() || ((GeoElement) vectors[0]).isGeoElement3D())
					ret[0] = (GeoElement) getKernel().getManager3D().Line3D(null,points[0], vectors[0]);
				else
					ret[0] = kernel.Line(null, (GeoPoint) points[0], (GeoVector) vectors[0]);
				return ret;
			} else if (selLines() == 1) {
				// fetch selected point and vector
				GeoPointND[] points = getSelectedPointsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new line
				if (((GeoElement) points[0]).isGeoElement3D() || ((GeoElement) lines[0]).isGeoElement3D())
					ret[0] = (GeoElement) getKernel().getManager3D().Line3D(null,points[0], lines[0]);
				else
					ret[0] = getKernel().Line(null, (GeoPoint) points[0], (GeoLine) lines[0]);
				return ret;
			}
		}
		return null;
	}

	// get point and line 
	// create parabola (focus and directrix)
	// Michael Borcherds 2008-04-08
	final protected GeoElement[] parabola(Hits hits) {
		if (hits.isEmpty())
			return null;

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
				ret[0] = kernel.Parabola(null, points[0], lines[0]);
				return ret;
			}
		}
		return null;
	}

	// get point and line or vector;
	// create line through point orthogonal to line or vector
	protected GeoElement[] orthogonal(Hits hits) {
		if (hits.isEmpty())
			return null;
		
		boolean hitPoint = (addSelectedPoint(hits, 1, false) != 0);
		
		return orthogonal(hits, hitPoint);
		
	}
	
	protected GeoElement[] orthogonal(Hits hits, boolean hitPoint) {
		
		if (!hitPoint) {
			if (selLines() == 0) {
				addSelectedVector(hits, 1, false, GeoVector.class);
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
				//no defined line through a point and orthogonal to a vector in 3D
				if (((GeoElement) points[0]).isGeoElement3D())// || ((GeoElement) vectors[0]).isGeoElement3D())
					return null;
				else
					ret[0] = kernel.OrthogonalLine(null, (GeoPoint) points[0], (GeoVector) vectors[0]);
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
	
	protected GeoElement[] orthogonal(GeoPointND point, GeoLineND line){
		if (((GeoElement) point).isGeoElement3D() || ((GeoElement) line).isGeoElement3D())
			return new GeoElement[] {(GeoElement) getKernel().getManager3D().OrthogonalLine3D(null,point, line, ((EuclidianView) view).getDirection())};		
		else
			return orthogonal2D(point, line);		
	}
	
	protected GeoElement[] orthogonal2D(GeoPointND point, GeoLineND line){
		return new GeoElement[] {getKernel().OrthogonalLine(null, (GeoPoint) point, (GeoLine) line)};
	}
	
	// get two points, line segment or conic
	// and create midpoint/center for them/it
	final protected GeoElement[] midpoint(Hits hits) {
		if (hits.isEmpty())
			return null;

		boolean hitPoint = (addSelectedPoint(hits, 2, false) != 0);

		if (!hitPoint && selPoints() == 0) {
			addSelectedSegment(hits, 1, false); // segment needed
			if (selSegments() == 0)
				addSelectedConic(hits, 1, false); // conic needed
		}

		GeoElement[] ret = { null };
		
		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPointND[] points = getSelectedPointsND();
			if (((GeoElement) points[0]).isGeoElement3D() || ((GeoElement) points[1]).isGeoElement3D())
				ret[0] = (GeoElement) kernel.getManager3D().Midpoint(null, points[0], points[1]);
			else
				ret[0] = kernel.Midpoint(null, (GeoPoint) points[0], (GeoPoint) points[1]);
			return ret;
		} else if (selSegments() == 1) {
			// fetch the selected segment
			GeoSegmentND[] segments = getSelectedSegmentsND();
			if (((GeoElement) segments[0]).isGeoElement3D())
				ret[0] = (GeoElement) kernel.getManager3D().Midpoint(null, segments[0]);
			else
				ret[0] = kernel.Midpoint(null, (GeoSegment) segments[0]);
			return ret;
		} else if (selConics() == 1) {
			// fetch the selected segment
			GeoConic[] conics = getSelectedConics();
			ret[0] = kernel.Center(null, conics[0]);
			return ret;
		}
		return null;
	}

	
	
	// get a function and open the function inspector
	final protected boolean functionInspector(Hits hits) {
		if (hits.isEmpty())
			return false;

		boolean hitFunction = (addSelectedFunction(hits, 1, false) != 0);

		if(selFunctions() == 1){
			GeoFunction[] functions = getSelectedFunctions();
			
			app.getGuiManager().showFunctionInspector(functions[0]);
			app.setMoveMode();
		}
		
		
		return false;
	}
	
	
	
	// get two points and create line bisector for them
	// or get line segment and create line bisector for it
	final protected GeoElement[] lineBisector(Hits hits) {
		if (hits.isEmpty())
			return null;
		boolean hitPoint = false;

		if (selSegments() == 0)
			hitPoint = (addSelectedPoint(hits, 2, false) != 0);

		if (!hitPoint && selPoints() == 0)
			addSelectedSegment(hits, 1, false); // segment needed

		GeoElement[] ret = { null };
		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
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

	// get three points and create angular bisector for them
	// or bisector for two lines
	final protected GeoElement[] angularBisector(Hits hits) {
		if (hits.isEmpty())
			return null;
		boolean hitPoint = false;

		if (selLines() == 0) {
			hitPoint = (addSelectedPoint(hits, 3, false) != 0);
		}
		if (!hitPoint && selPoints() == 0) {
			addSelectedLine(hits, 2, false);
		}

		if (selPoints() == 3) {
			// fetch the three selected points
			GeoPoint[] points = getSelectedPoints();
			GeoElement[] ret = { null };
			ret[0] = kernel.AngularBisector(null, points[0], points[1], points[2]);
			return ret;
		} else if (selLines() == 2) {
			// fetch the two lines
			GeoLine[] lines = getSelectedLines();
			return kernel.AngularBisector(null, lines[0], lines[1]);
		}
		return null;
	}

	// get 3 points
	final protected GeoElement[] threePoints(Hits hits, int mode) {
		
		if (hits.isEmpty())
			return null;

		// points needed
		addSelectedPoint(hits, 3, false);
		if (selPoints() == 3) {
			return switchModeForThreePoints();
		}
		return null;
	}
	
	
	protected GeoElement[] switchModeForThreePoints(){
		// fetch the three selected points
		GeoPointND[] points = getSelectedPointsND();
		GeoElement[] ret = { null };
		switch (mode) {
		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
			if ( ((GeoElement) points[0]).isGeoElement3D() 
					|| ((GeoElement) points[1]).isGeoElement3D() 
					|| ((GeoElement) points[2]).isGeoElement3D() )
				ret[0] = kernel.getManager3D().Circle3D(null, points[0], points[1], points[2]);
			else
				ret[0] = kernel.Circle(null, (GeoPoint) points[0], (GeoPoint) points[1], (GeoPoint) points[2]);
			break;

		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
			ret[0] = kernel.Ellipse(null, (GeoPoint) points[0], (GeoPoint) points[1], (GeoPoint) points[2]);
			break;

		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
			ret[0] = kernel.Hyperbola(null, (GeoPoint) points[0], (GeoPoint) points[1], (GeoPoint) points[2]);
			break;

		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
			ret[0] = kernel.CircumcircleArc(null, (GeoPoint) points[0], (GeoPoint) points[1], (GeoPoint) points[2]);
			break;

		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			ret[0] = kernel.CircumcircleSector(null, (GeoPoint) points[0], (GeoPoint) points[1], (GeoPoint) points[2]);
			break;

		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
			ret[0] = kernel.CircleArc(null, (GeoPoint) points[0], (GeoPoint) points[1], (GeoPoint) points[2]);
			break;

		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
			ret[0] = kernel.CircleSector(null, (GeoPoint) points[0], (GeoPoint) points[1], (GeoPoint) points[2]);
			break;												

		default:
			return null;
		}
		
		return ret;

	}

	// get 2 lines, 2 vectors or 3 points
	final protected GeoElement[] angle(Hits hits) {
		if (hits.isEmpty())
			return null;

		int count = 0;
		if (selPoints() == 0) {
			if (selVectors() == 0)
				count = addSelectedLine(hits, 2, false);
			if (selLines() == 0) 
				count = addSelectedVector(hits, 2, false);			
		}		
		if (count == 0)
			count = addSelectedPoint(hits, 3, false);	

		// try polygon too
		boolean polyFound = false;
		if (count == 0)	{		
			polyFound = 1 == addSelectedGeo(hits.getHits(GeoPolygon.class, tempArrayList), 
					1, false);
		}

		GeoAngle angle = null;
		GeoElement [] angles = null;
		if (selPoints() == 3) {
			GeoPoint[] points = getSelectedPoints();
			angle = kernel.Angle(null, points[0], points[1], points[2]);					
		} else if (selVectors() == 2) {
			GeoVector[] vecs = getSelectedVectors();
			angle = kernel.Angle(null, vecs[0], vecs[1]);				
		} else if (selLines() == 2) {
			GeoLine[] lines = getSelectedLines();
			angle = createLineAngle(lines);			
		} else if (polyFound && selGeos() == 1) {
			angles = kernel.Angles(null,(GeoPolygon) getSelectedGeos()[0]);	
		}

		if (angle != null) {
			// commented in V3.0:
			// angle.setAllowReflexAngle(false);
			// 	make sure that we show angle value
			if (angle.isLabelVisible()) 
				angle.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			else 
				angle.setLabelMode(GeoElement.LABEL_VALUE);
			angle.setLabelVisible(true);
			angle.updateRepaint();
			GeoElement[] ret = { angle };
			return ret;
		} 
		else if (angles != null) {
			for (int i=0; i < angles.length; i++) {
				//	make sure that we show angle value
				if (angles[i].isLabelVisible()) 
					angles[i].setLabelMode(GeoElement.LABEL_NAME_VALUE);
				else 
					angles[i].setLabelMode(GeoElement.LABEL_VALUE);
				angles[i].setLabelVisible(true);
				angles[i].updateRepaint();
			}
			return angles;
		} else
			return null;
	}

	// build angle between two lines
	protected GeoAngle createLineAngle(GeoLine [] lines) {
		GeoAngle angle = null;

		// did we get two segments?
		if (lines[0] instanceof GeoSegment && 
				lines[1] instanceof GeoSegment) {
			// check if the segments have one point in common
			GeoSegment a = (GeoSegment) lines[0];
			GeoSegment b = (GeoSegment) lines[1];
			// get endpoints
			GeoPoint a1 = a.getStartPoint();
			GeoPoint a2 = a.getEndPoint();
			GeoPoint b1 = b.getStartPoint();
			GeoPoint b2 = b.getEndPoint();

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

		if (angle == null)
			angle = kernel.Angle(null, lines[0], lines[1]);

		return angle;
	}

	// get 2 points
	final protected GeoElement[] circleOrSphere2(Hits hits, int mode) {
		if (hits.isEmpty())
			return null;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the three selected points
			return switchModeForCircleOrSphere2(mode);
		}
		return null;
	}
	
	protected GeoElement[] switchModeForCircleOrSphere2(int mode){
		GeoPointND[] points = getSelectedPointsND();
		if (mode == EuclidianConstants.MODE_SEMICIRCLE)
			return new GeoElement[] {kernel.Semicircle(null, (GeoPoint) points[0], (GeoPoint) points[1])};
		else
			return createCircle2(points[0], points[1]);
		
	}
	
	protected GeoElement[] createCircle2(GeoPointND p0, GeoPointND p1){
		if ( ((GeoElement) p0).isGeoElement3D() || ((GeoElement) p1).isGeoElement3D() )
			return createCircle2ForPoints3D(p0, p1);
		else
			return new GeoElement[] {kernel.Circle(null, (GeoPoint) p0, (GeoPoint) p1)};
	}
	
	protected GeoElement[] createCircle2ForPoints3D(GeoPointND p0, GeoPointND p1){
		return new GeoElement[] {kernel.getManager3D().Circle3D(null, p0, p1, ((EuclidianView) view).getDirection())};
	}

	// get 2 points for locus
	// first point 
	final protected GeoElement[] locus(Hits hits) {
		if (hits.isEmpty())
			return null;

		// points needed
		addSelectedPoint(hits, 2, false);
		addSelectedNumeric(hits, 1, false);

		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
			GeoLocus locus;
			if (points[0].getPath() == null) {
				locus = kernel.Locus(null, points[0], points[1]);
			} else {
				locus = kernel.Locus(null, points[1], points[0]);
			}
			GeoElement[] ret = { null };
			ret[0] = locus;
			return ret;
		} else if (selPoints() == 1 && selNumbers() == 1) {
			GeoPoint[] points = getSelectedPoints();
			GeoNumeric[] numbers = getSelectedNumbers();
			GeoLocus locus = kernel.Locus(null, points[0], numbers[0]);
			GeoElement[] ret = { locus };
			return ret;
		}
		return null;
	}

	// get 5 points
	final protected GeoElement[] conic5(Hits hits) {
		if (hits.isEmpty())
			return null;

		// points needed
		addSelectedPoint(hits, 5, false);
		if (selPoints() == 5) {
			// fetch the three selected points
			GeoPoint[] points = getSelectedPoints();
			GeoElement[] ret = { null };
			ret[0] = kernel.Conic(null, points);
			return ret;
		}
		return null;
	}

	// get 2 GeoElements
	final protected boolean relation(Hits hits) {
		if (hits.isEmpty())
			return false;

		addSelectedGeo(hits, 2, false);
		if (selGeos() == 2) {
			// fetch the three selected points
			GeoElement[] geos = getSelectedGeos();
			app.showRelation(geos[0], geos[1]);
			return true;
		}
		return false;
	}

	// get 2 points, 2 lines or 1 point and 1 line
	final protected GeoElement[] distance(Hits hits, MouseEvent e) {
		if (hits.isEmpty())
			return null;

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
			GeoNumeric length = kernel.Distance(null, (GeoPointND) points[0], (GeoPointND) points[1]);								

			// set startpoint of text to midpoint of two points
			GeoPoint midPoint = kernel.Midpoint(points[0], points[1]);
			GeoElement[] ret = { null };
			ret[0] = createDistanceText(points[0], points[1], midPoint, length);
			return ret;
		} 

		// SEGMENT
		else if (selSegments() == 1) {
			// length
			GeoSegment[] segments = getSelectedSegments();

			// length			
			if (segments[0].isLabelVisible()) 
				segments[0].setLabelMode(GeoElement.LABEL_NAME_VALUE);
			else 
				segments[0].setLabelMode(GeoElement.LABEL_VALUE);
			segments[0].setLabelVisible(true);
			segments[0].updateRepaint();
			return segments; // return this not null because the kernel has changed 
		}

		// TWO LINES
		else if (selLines() == 2) {			
			GeoLine[] lines = getSelectedLines();
			GeoElement[] ret = { null };
			ret[0] = kernel.Distance(null, lines[0], lines[1]);
			return ret; // return this not null because the kernel has changed
		}

		// POINT AND LINE
		else if (selPoints() == 1 && selLines() == 1) {	
			GeoPoint[] points = getSelectedPoints();
			GeoLine[] lines = getSelectedLines();
			GeoNumeric length = kernel.Distance(null, points[0], lines[0]);						

			// set startpoint of text to midpoint between point and line
			GeoPoint midPoint = kernel.Midpoint(points[0], kernel.ClosestPoint(points[0], lines[0]));
			GeoElement[] ret = { null };
			ret[0] = createDistanceText(points[0],lines[0], midPoint, length);	
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
					if (conic.isLabelVisible()) 
						conic.setLabelMode(GeoElement.LABEL_NAME_VALUE);
					else 
						conic.setLabelMode(GeoElement.LABEL_VALUE);
					conic.updateRepaint();
					GeoElement [] ret = { conic };
					return ret; // return this not null because the kernel has changed
				}				
			} 

			// standard case: conic
			GeoNumeric circumFerence = kernel.Circumference(null, conic);

			// text			
			GeoText text = createDynamicText(app.getCommand("Circumference"), circumFerence, e.getPoint());			
			if (conic.isLabelSet()) {
				circumFerence.setLabel(removeUnderscores(app.getCommand("Circumference").toLowerCase(Locale.US) + conic.getLabel()));							
				text.setLabel(removeUnderscores(app.getPlain("Text") + conic.getLabel()));				
			}
			GeoElement[] ret = { text };
			return ret;
		}

		// perimeter of CONIC
		else if (selPolygons() == 1) {			
			GeoPolygon [] poly = getSelectedPolygons();
			GeoNumeric perimeter = kernel.Perimeter(null, poly[0]);

			// text			
			GeoText text = createDynamicText(descriptionPoints(app.getCommand("Perimeter"), poly[0]), 
					perimeter, e.getPoint());

			if (poly[0].isLabelSet()) {
				perimeter.setLabel(removeUnderscores(app.getCommand("Perimeter").toLowerCase(Locale.US) + poly[0].getLabel()));							
				text.setLabel(removeUnderscores(app.getPlain("Text") + poly[0].getLabel()));				
			} 
			GeoElement[] ret = { text };
			return ret;
		}

		return null;
	}	

	/**
	 * Creates a text that shows the distance length between geoA and geoB at the given startpoint.
	 */
	protected GeoText createDistanceText(GeoElement geoA, GeoElement geoB, 
			GeoPoint startPoint, GeoNumeric length) {
		// create text that shows length
		try {				
			String strText = "";
			boolean useLabels = geoA.isLabelSet() && geoB.isLabelSet();
			if (useLabels) {		
				length.setLabel(removeUnderscores(app.getCommand("Distance").toLowerCase(Locale.US) + geoA.getLabel() + geoB.getLabel()));
				//strText = "\"\\overline{\" + Name["+ geoA.getLabel() 
				//	+ "] + Name["+ geoB.getLabel() + "] + \"} \\, = \\, \" + "
				//	+ length.getLabel();	

				//DistanceAB="\\overline{" + %0 + %1 + "} \\, = \\, " + %2
				// or
				//DistanceAB=%0+%1+" \\, = \\, "+%2
				strText = app.getPlain("DistanceAB.LaTeX","Name["+ geoA.getLabel()+"]","Name["+ geoB.getLabel()+"]",length.getLabel());			
				//Application.debug(strText);
				geoA.setLabelVisible(true);				
				geoB.setLabelVisible(true);
				geoA.updateRepaint();
				geoB.updateRepaint();
			}
			else {
				length.setLabel(removeUnderscores(app.getCommand("Distance").toLowerCase(Locale.US)));					
				strText = "\"\"" + length.getLabel();
			}

			// create dynamic text
			GeoText text = kernel.getAlgebraProcessor().evaluateToText(strText, true, true);
			if (useLabels) {
				text.setLabel(removeUnderscores(app.getPlain("Text") + geoA.getLabel() + geoB.getLabel()));	
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

	/**
	 * Creates a text that shows a number value of geo at the current mouse position.
	 */
	protected GeoText createDynamicText(String descText, GeoElement value, Point loc) {
		// create text that shows length
		try {
			// create dynamic text
			String dynText = "\"" + descText + " = \" + " + value.getLabel();

			GeoText text = kernel.getAlgebraProcessor().evaluateToText(dynText, true, true);									
			text.setAbsoluteScreenLocActive(true);
			text.setAbsoluteScreenLoc(loc.x, loc.y);			
			text.updateRepaint();
			return text;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}

	protected String removeUnderscores(String label) {	
		// remove all indices
		return label.replaceAll("_", "");			
	}

	protected GeoElement[] area(Hits hits,  MouseEvent e) {
		if (hits.isEmpty())
			return null;

		int count = addSelectedPolygon(hits, 1, false);
		if (count == 0) {
			addSelectedConic(hits, 2, false);
		}				

		// area of CONIC
		if (selConics() == 1) {			
			GeoConic conic = getSelectedConics()[0];			

			//  check if arc
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
			GeoText text = createDynamicText(app.getCommand("Area"), area, e.getPoint());			
			if (conic.isLabelSet()) {					
				area.setLabel(removeUnderscores(app.getCommand("Area").toLowerCase(Locale.US) + conic.getLabel()));							
				text.setLabel(removeUnderscores(app.getPlain("Text") + conic.getLabel()));				
			}
			GeoElement[] ret = { text };
			return ret;
		}

		// area of polygon
		else if (selPolygons() == 1) {			
			GeoPolygon [] poly = getSelectedPolygons();						

			// dynamic text with polygon's area
			GeoText text = createDynamicText(descriptionPoints(app.getCommand("Area"), poly[0]), poly[0], e.getPoint());			
			if (poly[0].isLabelSet()) {					
				text.setLabel(removeUnderscores(app.getPlain("Text") + poly[0].getLabel()));				
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
		GeoPoint [] points = null;
		if (poly.getParentAlgorithm() instanceof AlgoPolygon) {
			points = ((AlgoPolygon) poly.getParentAlgorithm()).getPoints();
		}	

		if (points != null) {
			descText = descText + " \"";
			boolean allLabelsSet = true;
			for (int i=0; i < points.length; i++) {
				if (points[i].isLabelSet()) 
					descText = descText + " + Name[" + points[i].getLabel() + "]";
				else {
					allLabelsSet = false;
					i = points.length;
				}
			}

			if (allLabelsSet) {
				descText = descText + " + \"";
				for (int i=0; i < points.length; i++) {
					points[i].setLabelVisible(true);
					points[i].updateRepaint();
				}
			} else
				descText = app.getCommand("Area");
		}
		return descText;
	}

	protected GeoElement[] slope(Hits hits) {
		if (hits.isEmpty())
			return null;

		addSelectedLine(hits, 1, false);			

		if (selLines() == 1) {			
			GeoLine line = getSelectedLines()[0];						

			String strLocale = app.getLocale().toString();
			GeoNumeric slope;
			/*
			if (strLocale.equals("de_AT")) {
				slope = kernel.Slope("k", line);
			} else {
				slope = kernel.Slope("m", line);
			}			*/
			
			String label = app.getPlain("ExplicitLineGradient");
			
			// make sure automatic naming goes m, m_1, m_2, ..., m_{10}, m_{11} etc
			if (kernel.lookupLabel(label) != null) {
				int i = 1;
				while (kernel.lookupLabel(i > 9 ? label+"_{"+i+"}" : label+"_"+i) != null) i++;
				label = i > 9 ? label+"_{"+i+"}" : label+"_"+i;
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

	protected boolean regularPolygon(Hits hits) {
		if (hits.isEmpty())
			return false;

		// need two points
		addSelectedPoint(hits, 2, false);

		if (selPoints() == 2) {					
			GeoPoint [] points = getSelectedPoints();
			app.getGuiManager().showNumberInputDialogRegularPolygon(app.getMenu(getKernel().getModeText(mode)),
					points[0], points[1]);
			return true;
		}
		return false;
	}

	protected boolean showCheckBox(Hits hits) {
		if (selectionPreview)
			return false;

		app.getGuiManager().showBooleanCheckboxCreationDialog(mouseLoc, null);
		return false;
	}

	// get (point or line) and (conic or function or curve or implicitpoly)
	final protected GeoElement[] tangents(Hits hits) {
		if (hits.isEmpty())
			return null;

		boolean found=false;
		found = addSelectedConic(hits, 2, false) != 0;
		if (!found)
			found = addSelectedFunction(hits, 1, false) != 0;
		if (!found)
			found = addSelectedCurve(hits, 1, false) != 0;		
		if (!found)
			found = addSelectedImplicitpoly(hits, 1, false) != 0;	

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
				return kernel.Tangent(null, points[0], conics[0]);
			} else if (selLines() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoLine[] lines = getSelectedLines();
				// create new line
				return kernel.Tangent(null, lines[0], conics[0]);
			}
		} 
		else if (selConics() == 2) {
			GeoConic[] conics = getSelectedConics();
			// create new tangents
			return kernel.CommonTangents(null, conics[0], conics[1]);
		} 
		else if (selFunctions() == 1) {
			if (selPoints() == 1) {
				GeoFunction[] functions = getSelectedFunctions();
				GeoPoint[] points = getSelectedPoints();
				// create new tangents
				GeoElement[] ret = { null };
				ret[0] = kernel.Tangent(null, points[0], functions[0]);
				return ret;
			}
		}
		else if (selCurves() == 1) {
			if (selPoints() == 1) {
				GeoCurveCartesian[] curves = getSelectedCurves();
				GeoPoint [] points = getSelectedPoints();
				// create new tangents
				GeoElement[] ret = { null };
				ret[0] = kernel.Tangent(null, points[0], curves[0]);
				return ret;
			}
		}
		else if (selImplicitpoly() == 1) {
			if (selPoints() == 1) {
				GeoImplicitPoly implicitPoly = getSelectedImplicitpoly()[0];
				GeoPoint[] points = getSelectedPoints();
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

	// get (point or line or vector) and conic
	final protected GeoElement[] polarLine(Hits hits) {
		if (hits.isEmpty())
			return null;
		boolean hitConic = false;

		hitConic = (addSelectedConic(hits, 1, false) != 0);

		if (!hitConic ) {
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
				ret[0] = kernel.PolarLine(null, points[0], conics[0]);
				return ret;
			} else if (selLines() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoLine[] lines = getSelectedLines();
				// create new line
				ret[0] = kernel.DiameterLine(null, lines[0], conics[0]);
				return ret;
			}  else if (selVectors() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoVector[] vecs = getSelectedVectors();
				// create new line
				ret[0] = kernel.DiameterLine(null, vecs[0], conics[0]);
				return ret;
			}
		}
		return null;
	}

	final protected boolean delete(Hits hits) {
		if (hits.isEmpty())
			return false;

		addSelectedGeo(hits, 1, false);
		if (selGeos() == 1) {
			// delete this object
			GeoElement[] geos = getSelectedGeos();
			geos[0].removeOrSetUndefinedIfHasFixedDescendent();
			return true;
		}
		return false;
	}

	final protected boolean showHideObject(Hits hits) {
		if (hits.isEmpty())
			return false;

		if (selectionPreview) {
			addSelectedGeo(hits, 1000, false);
			return false;
		}

		GeoElement geo = chooseGeo(hits, true);
		if (geo != null) {
			// hide axis
			if (geo instanceof GeoAxis)	{
				switch (((GeoAxis) geo).getType()) {
				case GeoAxis.X_AXIS:
					//view.showAxes(false, view.getShowYaxis());
					view.setShowAxis(EuclidianViewInterface.AXIS_X, false, true);
					break;

				case GeoAxis.Y_AXIS:
					//view.showAxes(view.getShowXaxis(), false);
					view.setShowAxis(EuclidianViewInterface.AXIS_Y, false, true);
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

	final protected boolean showHideLabel(Hits hits) {
		if (hits.isEmpty())
			return false;

		if (selectionPreview) {
			addSelectedGeo(hits, 1000, false);
			return false;
		}

		GeoElement geo = chooseGeo(hits.getOtherHits(GeoAxis.class, tempArrayList), true);
		if (geo != null) {			
			geo.setLabelVisible(!geo.isLabelVisible());
			geo.updateRepaint();
			return true;
		}						
		return false;
	}

	final protected boolean copyVisualStyle(Hits hits) {
		if (hits.isEmpty())
			return false;

		if (selectionPreview) {
			addSelectedGeo(hits, 1000, false);
			return false;
		}

		GeoElement geo = chooseGeo(hits.getOtherHits(GeoAxis.class, tempArrayList), true);
		if (geo == null) return false;

		// movedGeoElement is the active geo
		if (movedGeoElement == null) {
			movedGeoElement = geo;
			Hits oldhits = new Hits();
			oldhits.addAll(app.getSelectedGeos());
			for (int i=oldhits.size()-1; i >= 0; i--) {
				GeoElement oldgeo = (GeoElement) oldhits.get(i);
				if (!(movedGeoElement.getClass().isInstance(oldgeo))) {
					oldhits.remove(i);
				}
			}
			if (oldhits.size() > 0) {
				// there were appropriate selected elements
				// apply visual style for them
				// standard case: copy visual properties
				for (int i=0; i < oldhits.size(); i++) {
					GeoElement oldgeo = (GeoElement) oldhits.get(i);
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
				if (toggleModeChangedKernel)
					app.storeUndoInfo();
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


	// get Transformables and point
	final protected GeoElement[] mirrorAtPoint(Hits hits) {	
		if (hits.isEmpty())
			return null;

		// try to get one Transformable	
		int count = 0;
		if (selGeos() == 0) {
			Hits mirAbles = hits.getHits(Transformable.class, tempArrayList);		
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
				return kernel.Mirror(null,  polys[0], points[0]);
			} 
			else if (selGeos() > 0) {					
				// mirror all selected geos
				GeoElement [] geos = getSelectedGeos();
				GeoPoint point = getSelectedPoints()[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				for (int i=0; i < geos.length; i++) {				
					if (geos[i] != point) {
						if (geos[i] instanceof Transformable) {
							ret.addAll(Arrays.asList(kernel.Mirror(null,  geos[i], point)));
						} else if (geos[i].isGeoPolygon()) {
							ret.addAll(Arrays.asList(kernel.Mirror(null, (GeoPolygon) geos[i], point)));
						}
					}
				}
				GeoElement[] retex = {};
				return ret.toArray(retex);
			}
		}
		return null;
	}

	/**
	 * Removes parent points of segments, rays, polygons, etc. from selGeos
	 * that are not necessary for transformations of these objects.
	 */
	protected void removeParentPoints(ArrayList selGeos) {
		tempArrayList.clear();	
		tempArrayList.addAll(selGeos);
		
		// remove parent points
		for (int i=0; i < selGeos.size(); i++) {
			GeoElement geo = (GeoElement) selGeos.get(i);

			switch (geo.getGeoClassType()) {			
			case GeoElement.GEO_CLASS_SEGMENT:
			case GeoElement.GEO_CLASS_RAY:
				// remove start and end point of segment
				GeoLine line = (GeoLine) geo;						
				tempArrayList.remove(line.getStartPoint());
				tempArrayList.remove(line.getEndPoint());									
				break;

			case GeoElement.GEO_CLASS_CONICPART:
				GeoConicPart cp = (GeoConicPart) geo;
				ArrayList ip = cp.getParentAlgorithm().getInputPoints();
				tempArrayList.removeAll(ip);
				break;

			case GeoElement.GEO_CLASS_POLYGON:
				// remove points and segments of poly
				GeoPolygon poly = (GeoPolygon) geo;
				GeoPointND [] points = poly.getPoints();
				for (int k=0; k < points.length; k++) {
					tempArrayList.remove(points[k]);
				}
				GeoSegmentND [] segs = poly.getSegments();
				for (int k=0; k < segs.length; k++) {
					tempArrayList.remove(segs[k]);
				}
				break;					

			case GeoElement.GEO_CLASS_POLYLINE:
				// remove points and segments of poly
				GeoPolyLine polyl = (GeoPolyLine) geo;
				points = polyl.getPoints();
				for (int k=0; k < points.length; k++) {
					tempArrayList.remove(points[k]);
				}
				break;					
			}				
		}		

		selGeos.clear();
		selGeos.addAll(tempArrayList);						
	}

	// get Transformable and line
	final protected GeoElement[] mirrorAtLine(Hits hits) {
		if (hits.isEmpty())
			return null;

		// Transformable	
		int count = 0;
		if (selGeos() == 0) {
			Hits mirAbles = hits.getHits(Transformable.class, tempArrayList);
			count =addSelectedGeo(mirAbles, 1, false);
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
				return kernel.Mirror(null,  polys[0], lines[0]);
			} 
			else if (selGeos() > 0) {					
				// mirror all selected geos
				GeoElement [] geos = getSelectedGeos();
				GeoLine line = getSelectedLines()[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				for (int i=0; i < geos.length; i++) {				
					if (geos[i] != line) {
						if (geos[i] instanceof Transformable)
							ret.addAll(Arrays.asList(kernel.Mirror(null,  geos[i], line)));
						else if (geos[i].isGeoPolygon()) {
							ret.addAll(Arrays.asList(kernel.Mirror(null, (GeoPolygon) geos[i], line)));
						}
					}
				}
				GeoElement[] retex = {};
				return ret.toArray(retex);
			}
		}
		return null;
	}


	// Michael Borcherds 2008-03-23
	final protected GeoElement[] mirrorAtCircle(Hits hits) {
		if (hits.isEmpty())
			return null;

		// Transformable	
		int count = 0;
		if (selGeos() == 0) {
			Hits mirAbles = hits.getHits(Transformable.class, tempArrayList);
			mirAbles.removeImages();
			count =addSelectedGeo(mirAbles, 1, false);
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
				return kernel.Mirror(null,  polys[0], lines[0]);
			} 
			else if (selGeos() > 0) {					
				// mirror all selected geos
				GeoElement [] geos = getSelectedGeos();
				GeoConic line = getSelectedCircles()[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				for (int i=0; i < geos.length; i++) {				
					if (geos[i] != line) {
						if (geos[i] instanceof Transformable)
							ret.addAll(Arrays.asList(kernel.Mirror(null,  geos[i], line)));
						else if (geos[i].isGeoPolygon()) {
							ret.addAll(Arrays.asList(kernel.Mirror(null, (GeoPolygon) geos[i], line)));
						}
					}
				}
				GeoElement[] retex = {};
				return ret.toArray(retex);
			}
		}
		return null;
	}

	final protected boolean attachDetach(Hits hits, MouseEvent e) {
		if (hits.isEmpty())
			return false;
		
		addSelectedRegion(hits, 1, false);
		
		addSelectedPath(hits, 1, false);

		addSelectedPoint(hits, 1, false);

		if (selectedPoints.size() == 1) {
			
			GeoPoint p = (GeoPoint)selectedPoints.get(0);
			
			
			if (p.isPointOnPath() || p.isPointInRegion()) {
				
				getSelectedPoints();
				getSelectedRegions();
				getSelectedPaths();
				
				// move point (20,20) pixels when detached
				double x = view.toScreenCoordX(p.inhomX) + 20;
				double y = view.toScreenCoordY(p.inhomY) + 20;
				
				try {
					Construction cons = kernel.getConstruction();
					boolean oldLabelCreationFlag = cons.isSuppressLabelsActive();
					cons.setSuppressLabelCreation(true);
					GeoPoint newPoint = new GeoPoint(kernel.getConstruction(),null, view.toRealWorldCoordX(x),view.toRealWorldCoordY(y),1.0);
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
			if (selPaths() == 1 && !altDown) { // press alt to force region (ie inside) not path (edge)
				Path paths[] = getSelectedPaths();
				GeoPoint[] points = getSelectedPoints();
				
				//Application.debug("path: "+paths[0]+"\npoint: "+points[0]);
				
				if (((GeoElement)paths[0]).isChildOf(points[0]))
					return false;
				
				if (((GeoElement)paths[0]).isGeoPolygon() 
						||
						(((GeoElement)paths[0]).isGeoConic()
						&& ((GeoConicND) paths[0]).getLastHitType()==GeoConicND.HIT_TYPE_ON_FILLING))
					return attach(points[0],(Region) paths[0]);
				
				
				return attach(points[0],paths[0]);
				
				
			}else if (selRegions() == 1) {
				Region regions[] = getSelectedRegions();
				GeoPoint[] points = getSelectedPoints();

				if (!((GeoElement)regions[0]).isChildOf(points[0])) {
					return attach(points[0],regions[0]);
				}
				
			} 
		} 
		return false;
	}
	
	final protected boolean attach(GeoPoint point, Path path){

		try {
			Construction cons = kernel.getConstruction();
			boolean oldLabelCreationFlag = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			GeoPoint newPoint = kernel.Point(null, path, view.toRealWorldCoordX(mx), view.toRealWorldCoordY(my), false, false);
			cons.setSuppressLabelCreation(oldLabelCreationFlag);
			kernel.getConstruction().replace(point, newPoint);
			clearSelections();
			return true;
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}				
	}
	
	final protected boolean attach(GeoPoint point, Region region){

		try {
			Construction cons = kernel.getConstruction();
			boolean oldLabelCreationFlag = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			GeoPoint newPoint = kernel.PointIn(null, region, view.toRealWorldCoordX(mx), view.toRealWorldCoordY(my), false, false);
			cons.setSuppressLabelCreation(oldLabelCreationFlag);
			kernel.getConstruction().replace(point, newPoint);
			clearSelections();
			return true;
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}		
	}

	// get Transformable and vector
	final protected GeoElement[] translateByVector(Hits hits) {
		if (hits.isEmpty())
			return null;

		// Transformable
		int count = 0;
		if (selGeos() == 0) {
			Hits transAbles = hits.getHits(Transformable.class, tempArrayList);
			count = addSelectedGeo(transAbles, 1, false);
		}

		// polygon
		if (count == 0) {					
			count = addSelectedPolygon(hits, 1, false);
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
		if (selVectors() == 1 || selPoints() == 2) {		
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoVector vec = null;
				if (selVectors() == 1)
					vec = getSelectedVectors()[0];
				else {
					GeoPoint[] ab = getSelectedPoints();
					vec = kernel.Vector(null, ab[0], ab[1]);
				}
				allowSelectionRectangleForTranslateByVector = true;
				return kernel.Translate(null,  polys[0], vec);
			}
			else if (selGeos() > 0) {
				// mirror all selected geos
				GeoElement [] geos = getSelectedGeos();
				GeoVector vec = null;
				if (selVectors() == 1)
					vec = getSelectedVectors()[0];
				else {
					GeoPoint[] ab = getSelectedPoints();
					vec = kernel.Vector(null, ab[0], ab[1]);
				}
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				for (int i=0; i < geos.length; i++) {
					if (geos[i] != vec) {
						if (geos[i] instanceof Transformable || geos[i].isGeoPolygon())
							ret.addAll(Arrays.asList(kernel.Translate(null,   geos[i], vec)));
					}
				}
				GeoElement[] retex = {};
				allowSelectionRectangleForTranslateByVector = true;
				return ret.toArray(retex);
			}
		}
		return null;
	}

	// get rotateable object, point and angle
	final protected GeoElement[] rotateByAngle(Hits hits) {
		if (hits.isEmpty())
			return null;

		// Transformable
		int count = 0;
		if (selGeos() == 0) {
			Hits rotAbles = hits.getHits(Transformable.class, tempArrayList);
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
		if (selPoints() == 1 && selGeos() > 0) {	
			
			GeoElement[] selGeos = getSelectedGeos();
			
			app.getGuiManager().showNumberInputDialogRotate(app.getMenu(getKernel().getModeText(mode)), getSelectedPolygons(), getSelectedPoints(), selGeos);
			
			return null;

		}

		return null;
	}		

	// get dilateable object, point and number
	final protected GeoElement[] dilateFromPoint(Hits hits) {
		if (hits.isEmpty())
			return null;

		// dilateable
		int count = 0;
		if (selGeos() == 0) {
			Hits dilAbles = hits.getHits(Dilateable.class, tempArrayList);
			count =	addSelectedGeo(dilAbles, 1, false);
		}

		//		 polygon
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
			
			app.getGuiManager().showNumberInputDialogDilate(app.getMenu(getKernel().getModeText(mode)), getSelectedPolygons(), getSelectedPoints(), selGeos);
			
			return null;

			
			/*
			NumberValue num = app.getGuiManager().showNumberInputDialog(app.getMenu(getKernel().getModeText(mode)),
					app.getPlain("Numeric"), null);			
			if (num == null) {
				view.resetMode();
				return null;
			}

			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoPoint[] points = getSelectedPoints();
				return kernel.Dilate(null,  polys[0], num, points[0]);
			} 
			else if (selGeos() > 0) {					
				// mirror all selected geos
				GeoElement [] geos = getSelectedGeos();
				GeoPoint point = getSelectedPoints()[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				for (int i=0; i < geos.length; i++) {				
					if (geos[i] != point) {
						if (geos[i] instanceof Dilateable || geos[i].isGeoPolygon())
							ret.addAll(Arrays.asList(kernel.Dilate(null,  geos[i], num, point)));
					}
				}
				GeoElement[] retex = {};
				return ret.toArray(retex);
			}		*/
		}
		return null;
	}


	// get point and number
	final protected boolean segmentFixed(Hits hits) {
		if (hits.isEmpty())
			return false;

		// dilation center
		addSelectedPoint(hits, 1, false);

		// we got the point
		if (selPoints() == 1) {
			// get length of segment
			app.getGuiManager().showNumberInputDialogSegmentFixed(app.getMenu(getKernel().getModeText(mode)),
					getSelectedPoints()[0]);

			return true;
		}
		return false;
	}



	final protected GeoElement[] fitLine(Hits hits) {

		GeoList list;

		addSelectedList(hits,1,false);

		GeoElement[] ret = { null };
		if (selLists() > 0)
		{
			list = getSelectedLists()[0];
			if (list != null) {
				ret[0] = kernel.FitLineY(null, list);
				return ret;   
			}
		}
		else
		{
			addSelectedPoint(hits, 999, true);

			if (selPoints() > 1) 
			{					
				GeoPoint[] points = getSelectedPoints();
				list = geogebra.kernel.commands.CommandProcessor.wrapInList(kernel,points, points.length, GeoElement.GEO_CLASS_POINT);
				if (list != null) {
					ret[0] = kernel.FitLineY(null, list);
					return ret;             	     	 
				} 
			}
		}
		return null;
	}

	final protected GeoElement[] createList(Hits hits) {
		GeoList list;
		GeoElement[] ret = { null };

		if (!selectionPreview && hits.size() > 1) 
		{					
			list = kernel.List(null, hits, false);
			if (list != null) {
				ret[0] = list;
				return ret;             	     	 
			} 
		}
		return null;
	}





	// Michael Borcherds 2008-03-14	
	// Markus 2008-07-30: added support for two identical input points (center *2 and point on edge)
	final protected GeoElement[] compasses(Hits hits) {
		if (hits.isEmpty())
			return null;

		// we already have two points that define the radius
		if (selPoints() == 2) {			
			GeoPoint [] points = new GeoPoint[2];
			points[0] = (GeoPoint) selectedPoints.get(0);
			points[1] = (GeoPoint) selectedPoints.get(1);

			// check for centerPoint
			GeoPoint centerPoint = (GeoPoint) chooseGeo(hits, GeoPoint.class);

			if (centerPoint != null) {
				if (selectionPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add(centerPoint);
					addToHighlightedList(selectedPoints, tempArrayList, 3);
					return null;
				}
				else {
					// three points: center, distance between two points		
					GeoElement circle = kernel.Circle(null, centerPoint, points[0], points[1],true);
					GeoElement[] ret = { circle };
					clearSelections();
					return ret;
				}
			}
		} 

		// we already have a circle that defines the radius
		else if (selConics() == 1) {
			GeoConic circle =  (GeoConic) selectedConicsND.get(0);

			// check for centerPoint
			GeoPoint centerPoint = (GeoPoint) chooseGeo(hits, GeoPoint.class);

			if (centerPoint != null) {	
				if (selectionPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add(centerPoint);
					addToHighlightedList(selectedPoints, tempArrayList, 3);
					return null;
				}
				else {
					// center point and circle which defines radius
					GeoElement circlel = kernel.Circle(null, centerPoint, circle);
					GeoElement ret[] = { circlel };
					clearSelections();
					return ret;
				}
			}
		}
		// we already have a segment that defines the radius
		else if (selSegments() == 1) {
			GeoSegment segment =  (GeoSegment) selectedSegments.get(0);

			// check for centerPoint
			GeoPoint centerPoint = (GeoPoint) chooseGeo(hits, GeoPoint.class);

			if (centerPoint != null) {	
				if (selectionPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add(centerPoint);
					addToHighlightedList(selectedPoints, tempArrayList, 3);
					return null;
				}
				else {
					// center point and segment
					GeoElement circlel = kernel.Circle(null, centerPoint, segment);
					GeoElement[] ret = { circlel };
					clearSelections();
					return ret;
				}
			}
		}


		// don't have radius yet: need two points or segment		
		boolean hitPoint = (addSelectedPoint(hits, 2, false) != 0);
		if (!hitPoint && selPoints() != 2 ) {
			addSelectedSegment(hits, 1, false);
			addSelectedConic(hits, 1, false);

			// don't allow conics other than circles to be selected
			if (selectedConicsND.size() > 0) {
				GeoConic c = (GeoConic)selectedConicsND.get(0);
				if (!c.isCircle()) {
					selectedConicsND.remove(0); 
					clearSelections();
				}
			}
		}

		return null;
	}

	// get two points and number
	final protected GeoElement[] angleFixed(Hits hits) {
		if (hits.isEmpty())
			return null;

		// dilation center
		int count = addSelectedPoint(hits, 2, false);

		if (count == 0) {
			addSelectedSegment(hits, 1, false);
		}				

		// we got the points		
		if (selPoints() == 2 || selSegments() == 1) {
			
			GeoElement[] selGeos = getSelectedGeos();

			app.getGuiManager().showNumberInputDialogAngleFixed(app.getMenu(getKernel().getModeText(mode)), getSelectedSegments(), getSelectedPoints(), selGeos);
			
			return null;

		}
		return null;
	}	

	// get center point and number
	final protected boolean circlePointRadius(Hits hits) {
		if (hits.isEmpty())
			return false;

		addSelectedPoint(hits, 1, false);		

		// we got the center point
		if (selPoints() == 1) {	
			app.getGuiManager().showNumberInputDialogCirclePointRadius(app.getMenu(getKernel().getModeText(mode)),
					getSelectedPointsND()[0],(EuclidianView) view);
			return true;
		}
		return false;
	}	

	// get point and vector
	final protected GeoElement[] vectorFromPoint(Hits hits) {
		if (hits.isEmpty())
			return null;

		// point	
		int count = addSelectedPoint(hits, 1, false);

		// vector
		if (count == 0) {
			addSelectedVector(hits, 1, false);
		}

		if (selPoints() == 1 && selVectors() == 1) {			
			GeoVector[] vecs = getSelectedVectors();			
			GeoPoint[] points = getSelectedPoints();
			GeoPoint endPoint = (GeoPoint) kernel.Translate(null, points[0], vecs[0])[0];
			GeoElement[] ret = { null };
			ret[0] = kernel.Vector(null, points[0], endPoint);
			return ret;
		}
		return null;
	}

	/**
	 * Handles selected objects for a macro
	 * @param hits
	 * @return
	 */
	final protected boolean macro(Hits hits) {		
		// try to get next needed type of macroInput
		int index = selGeos();

		// standard case: try to get one object of needed input type
		boolean objectFound = 1 == 
			handleAddSelected(hits, macroInput.length, false, selectedGeos, macroInput[index]);			

		//some old code for polygon removed in [6779]

		// we're done if in selection preview
		if (selectionPreview) 
			return false;


		// only one point needed: try to create it
		if (!objectFound && macroInput[index] == GeoPoint.class) {
			if (createNewPoint(hits, true, true, false)) {
				// take movedGeoPoint which is the newly created point								
				selectedGeos.add(getMovedGeoPoint());
				app.addSelectedGeo(getMovedGeoPoint());
				objectFound = true;
				POINT_CREATED = false;
			}
		}


		// object found in handleAddSelected()
		if (objectFound || macroInput[index] == GeoNumeric.class || macroInput[index] == GeoAngle.class) {
			if(!objectFound)index--;
			// look ahead if we need a number or an angle next			
			while (++index < macroInput.length) {				
				// maybe we need a number
				if (macroInput[index] == GeoNumeric.class) {									
					NumberValue num = app.getGuiManager().showNumberInputDialog(macro.getToolOrCommandName(),
							app.getPlain("Numeric" ), null);									
					if (num == null) {
						// no success: reset mode
						view.resetMode();
						return false;
					} else {
						// great, we got our number
						if (num.isGeoElement())
							selectedGeos.add(num.toGeoElement());
					}
				}	

				// maybe we need an angle
				else if (macroInput[index] == GeoAngle.class) {									
					Object [] ob = app.getGuiManager().showAngleInputDialog(macro.getToolOrCommandName(),
							app.getPlain("Angle"), "45\u00b0");
					NumberValue num = (NumberValue) ob[0];						

					if (num == null) {
						// no success: reset mode
						view.resetMode();
						return false;
					} else {
						// great, we got our angle
						if (num.isGeoElement())
							selectedGeos.add(num.toGeoElement());
					}
				}	

				else // other type needed, so leave loop 
					break;				
			}			
		}

		//Application.debug("index: " + index + ", needed type: " + macroInput[index]);

		// do we have everything we need?
		if (selGeos() == macroInput.length) {						
			kernel.useMacro(null, macro, getSelectedGeos());
			return true;
		} 		
		return false;
	}

	final protected boolean geoElementSelected(Hits hits, boolean addToSelection) {
		if (hits.isEmpty())
			return false;

		addSelectedGeo(hits, 1, false);
		if (selGeos() == 1) {
			GeoElement[] geos = getSelectedGeos();			
			app.geoElementSelected(geos[0], addToSelection);
		}
		return false;
	}

	// dummy function for highlighting:
	// used only in preview mode, see mouseMoved() and selectionPreview
	protected boolean move(Hits hits) {	
		addSelectedGeo(hits.getMoveableHits(view), 1, false);	
		return false;
	}



	// dummy function for highlighting:
	// used only in preview mode, see mouseMoved() and selectionPreview
	final protected boolean moveRotate(Hits hits) {				
		addSelectedGeo(hits.getPointRotateableHits(view, rotationCenter), 1, false);
		return false;
	}

	// dummy function for highlighting:
	// used only in preview mode, see mouseMoved() and selectionPreview
	final protected boolean point(Hits hits) {
		addSelectedGeo(hits.getHits(Path.class, tempArrayList), 1, false);
		return false;
	}

	final protected boolean text(Hits hits, int mode, boolean altDown) {
		GeoPointND loc = null; // location

		if (hits.isEmpty()) {
			if (selectionPreview)
				return false;
			else {
				// create new Point
				loc = new GeoPoint(kernel.getConstruction());			
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
			app.getGuiManager().showTextCreationDialog(loc);	
			return true;
		}

		return false;
	}

	final protected boolean image(Hits hits, int mode, boolean altDown) {
		GeoPoint loc = null; // location

		if (hits.isEmpty()) {
			if (selectionPreview)
				return false;
			else {
				// create new Point
				loc = new GeoPoint(kernel.getConstruction());			
				loc.setCoords(xRW, yRW, 1.0);	
			}
		} else {
			// points needed
			addSelectedPoint(hits, 1, false);
			if (selPoints() == 1) {
				// fetch the selected point
				GeoPoint[] points = getSelectedPoints();
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
	// new slider
	final protected boolean slider() {		
		if (!selectionPreview && mouseLoc != null) app.getGuiManager().showSliderCreationDialog(mouseLoc.x, mouseLoc.y);
		return false;
	}		

	// new button
	final protected boolean button(boolean textfield) {	
		if (!selectionPreview && mouseLoc != null) app.getGuiManager().showButtonCreationDialog(mouseLoc.x, mouseLoc.y, textfield);
		return false;
	}		

	final protected boolean pen() {	
		//Application.debug(app.getEuclidianView().getHeight()+" "+app.getEuclidianView().getWidth());
		return false;
	}		

	/***************************************************************************
	 * helper functions for selection sets
	 **************************************************************************/

	/*
	final protected boolean isSelected(GeoElement geo) {
		return selectedGeos.contains(geo);
	}*/

	//	final protected GeoElement getFirstSelectedInstance(Class myclass) {
	//		Iterator it = selectedGeos.iterator();		
	//		while (it.hasNext()) {
	//			GeoElement geo = (GeoElement) it.next();
	//			if (myclass.isInstance(geo))
	//				return geo;
	//		}
	//		return null;
	//	}

	final protected GeoElement[] getSelectedGeos() {
		GeoElement[] ret = new GeoElement[selectedGeos.size()];
		int i = 0;
		Iterator it = selectedGeos.iterator();
		while (it.hasNext()) {
			ret[i] = (GeoElement) it.next();
			i++;
		}
		clearSelection(selectedGeos);
		return ret;
	}	

	final protected void getSelectedPointsND(GeoPointND[] result) {	

		for (int i = 0; i < selectedPoints.size(); i++) {		
			result[i] = (GeoPointND) selectedPoints.get(i);
		}
		clearSelection(selectedPoints);

	}
	

	/** return selected points as ND points
	 * @return selected points
	 */
	final protected GeoPointND[] getSelectedPointsND() {		

		GeoPointND[] ret = new GeoPointND[selectedPoints.size()];
		getSelectedPointsND(ret);
		
		return ret;	
	}


	final protected GeoPoint[] getSelectedPoints() {		

		GeoPoint[] ret = new GeoPoint[selectedPoints.size()];
		getSelectedPointsND(ret);

		return ret;

	}

	final protected GeoNumeric[] getSelectedNumbers() {				
		GeoNumeric[] ret = new GeoNumeric[selectedNumbers.size()];
		for (int i = 0; i < selectedNumbers.size(); i++) {		
			ret[i] = (GeoNumeric) selectedNumbers.get(i);
		}
		clearSelection(selectedNumbers);
		return ret;
	}
	
	final protected NumberValue[] getSelectedNumberValues() {				
		NumberValue[] ret = new NumberValue[selectedNumberValues.size()];
		for (int i = 0; i < selectedNumberValues.size(); i++) {		
			ret[i] = (NumberValue) selectedNumberValues.get(i);
		}
		clearSelection(selectedNumberValues);
		return ret;
	}
	
	final protected GeoList[] getSelectedLists() {				
		GeoList[] ret = new GeoList[selectedLists.size()];
		for (int i = 0; i < selectedLists.size(); i++) {		
			ret[i] = (GeoList) selectedLists.get(i);
		}
		clearSelection(selectedLists);
		return ret;
	}

	final protected GeoPolygon[] getSelectedPolygons() {				
		GeoPolygon[] ret = new GeoPolygon[selectedPolygons.size()];
		for (int i = 0; i < selectedPolygons.size(); i++) {		
			ret[i] = (GeoPolygon) selectedPolygons.get(i);
		}
		clearSelection(selectedPolygons);
		return ret;
	}
	final protected GeoPolyLine[] getSelectedPolyLines() {				
		GeoPolyLine[] ret = new GeoPolyLine[selectedPolyLines.size()];
		for (int i = 0; i < selectedPolyLines.size(); i++) {		
			ret[i] = (GeoPolyLine) selectedPolyLines.get(i);
		}
		clearSelection(selectedPolyLines);
		return ret;
	}
	

	
	protected final void getSelectedLinesND(GeoLineND[] lines) {
		int i = 0;
		Iterator it = selectedLines.iterator();
		while (it.hasNext()) {
			lines[i] = (GeoLineND) it.next();
			i++;
		}
		clearSelection(selectedLines);
	}
	
	final protected GeoLineND[] getSelectedLinesND() {
		GeoLineND[] lines = new GeoLineND[selectedLines.size()];
		getSelectedLinesND(lines);

		return lines;
	}
	
	final protected GeoLine[] getSelectedLines() {
		GeoLine[] lines = new GeoLine[selectedLines.size()];
		getSelectedLinesND(lines);

		return lines;
	}
	
	
	
	final protected void getSelectedSegmentsND(GeoSegmentND[] segments) {
		int i = 0;
		Iterator it = selectedSegments.iterator();
		while (it.hasNext()) {
			segments[i] = (GeoSegmentND) it.next();
			i++;
		}
		clearSelection(selectedSegments);
	}
	
	
	final protected GeoSegmentND[] getSelectedSegmentsND() {
		GeoSegmentND[] segments = new GeoSegmentND[selectedSegments.size()];
		getSelectedSegmentsND(segments);

		return segments;
	}
	
	final protected GeoSegment[] getSelectedSegments() {
		GeoSegment[] segments = new GeoSegment[selectedSegments.size()];
		getSelectedSegmentsND(segments);

		return segments;
	}

	
	
	protected final void getSelectedVectorsND(GeoVectorND[] vectors) {
		int i = 0;
		Iterator it = selectedVectors.iterator();
		while (it.hasNext()) {
			vectors[i] = (GeoVectorND) it.next();
			i++;
		}
		clearSelection(selectedVectors);
	}
	
	final protected GeoVectorND[] getSelectedVectorsND() {
		GeoVectorND[] vectors = new GeoVectorND[selectedVectors.size()];
		getSelectedVectorsND(vectors);

		return vectors;
	}
	
	final protected GeoVector[] getSelectedVectors() {
		GeoVector[] vectors = new GeoVector[selectedVectors.size()];
		getSelectedVectorsND(vectors);

		return vectors;
	}

	final protected GeoConic[] getSelectedConics() {
		GeoConic[] conics = new GeoConic[selectedConicsND.size()];
		int i = 0;
		Iterator it = selectedConicsND.iterator();
		while (it.hasNext()) {
			conics[i] = (GeoConic) it.next();
			i++;
		}
		clearSelection(selectedConicsND);
		return conics;
	}
	
	final protected GeoConic[] getSelectedCircles() {
		GeoConic[] circles = new GeoConic[selectedConicsND.size()];
		int i = 0;
		Iterator it = selectedConicsND.iterator();
		while (it.hasNext()) {
			GeoConic c =(GeoConic) it.next();
			if(c.isCircle()){
				circles[i] = c;
				i++;
			}
		}
		clearSelection(selectedConicsND);
		return circles;
	}
	
	final protected GeoConicND[] getSelectedConicsND() {
		GeoConicND[] conics = new GeoConicND[selectedConicsND.size()];
		int i = 0;
		Iterator it = selectedConicsND.iterator();
		while (it.hasNext()) {
			conics[i] = (GeoConicND) it.next();
			i++;
		}
		clearSelection(selectedConicsND);
		return conics;
	}	
	
	final protected GeoDirectionND[] getSelectedDirections() {
		GeoDirectionND[] directions = new GeoDirectionND[selectedDirections.size()];
		int i = 0;
		Iterator it = selectedDirections.iterator();
		while (it.hasNext()) {
			directions[i] = (GeoDirectionND) it.next();
			i++;
		}
		clearSelection(selectedDirections);
		return directions;
	}
	
	final protected Region[] getSelectedRegions() {
		Region[] regions = new Region[selectedRegions.size()];
		int i = 0;
		Iterator it = selectedRegions.iterator();
		while (it.hasNext()) {
			regions[i] = (Region) it.next();
			i++;
		}
		clearSelection(selectedRegions);
		return regions;
	}
	
	final protected Path[] getSelectedPaths() {
		Path[] paths = new Path[selectedPaths.size()];
		int i = 0;
		Iterator it = selectedPaths.iterator();
		while (it.hasNext()) {
			paths[i] = (Path) it.next();
			i++;
		}
		clearSelection(selectedPaths);
		return paths;
	}
	
	final protected GeoImplicitPoly[] getSelectedImplicitpoly() {
		GeoImplicitPoly[] implicitPoly = new GeoImplicitPoly[selectedImplicitpoly.size()];
		int i = 0;
		Iterator it = selectedImplicitpoly.iterator();
		while (it.hasNext()) {
			implicitPoly[i] = (GeoImplicitPoly) it.next();
			i++;
		}
		clearSelection(selectedImplicitpoly);
		return implicitPoly;
	}

	final protected GeoFunction[] getSelectedFunctions() {
		GeoFunction[] functions = new GeoFunction[selectedFunctions.size()];
		int i = 0;
		Iterator it = selectedFunctions.iterator();
		while (it.hasNext()) {
			functions[i] = (GeoFunction) it.next();
			i++;
		}
		clearSelection(selectedFunctions);
		return functions;
	}


	final protected GeoCurveCartesian [] getSelectedCurves() {
		GeoCurveCartesian [] curves = new GeoCurveCartesian[selectedCurves.size()];
		int i = 0;
		Iterator it = selectedCurves.iterator();
		while (it.hasNext()) {
			curves[i] = (GeoCurveCartesian) it.next();
			i++;
		}
		clearSelection(selectedCurves);
		return curves;
	}

	final protected void clearSelection(ArrayList selectionList) {
		// unselect
		selectionList.clear();
		selectedGeos.clear();
		app.clearSelectedGeos();	
		view.repaintView();
	}

	final protected int addSelectedGeo(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedGeos, GeoElement.class);
	}		
	

	protected int handleAddSelected(Hits hits, int max, boolean addMore, ArrayList list, Class geoClass) {	
		
		
		if (selectionPreview)
			return addToHighlightedList(list, hits.getHits(geoClass, handleAddSelectedArrayList) , max);
		else
			return addToSelectionList(list, hits.getHits(geoClass, handleAddSelectedArrayList), max, addMore,
					hits.size()==1);
	}
	protected int handleAddSelectedRegions(Hits hits, int max, boolean addMore, ArrayList list) {	
		
		
		if (selectionPreview)
			return addToHighlightedList(list, hits.getRegionHits(handleAddSelectedArrayList) , max);
		else
			return addToSelectionList(list, hits.getRegionHits(handleAddSelectedArrayList), max, addMore,
					hits.size()==1);
	}
	protected Hits handleAddSelectedArrayList = new Hits();

	final protected int addSelectedPoint(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedPoints, GeoPointND.class);
	}

	public final int addSelectedNumeric(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedNumbers, GeoNumeric.class);
	}
	
	public final int addSelectedNumberValue(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedNumberValues, NumberValue.class);
	}

	final protected int addSelectedLine(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedLines, GeoLineND.class);
	}
	
	final protected int addSelectedDirection(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedDirections, GeoDirectionND.class);
	}

	final protected int addSelectedSegment(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedSegments, GeoSegmentND.class);
	}

	final protected int addSelectedVector(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return addSelectedVector(hits, max, addMoreThanOneAllowed, GeoVectorND.class);
	}
	
	final protected int addSelectedVector(Hits hits, int max,
			boolean addMoreThanOneAllowed, Class geoClass) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedVectors, geoClass);
	}

	
	final protected int addSelectedCircle(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		ArrayList<GeoConic> selectedCircles = new ArrayList<GeoConic>();
		for(Object c:selectedConicsND){
			if(((GeoConic)c).isCircle())
				selectedCircles.add((GeoConic)c);
		}
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedCircles, GeoConic.class);
	}
	
	final protected int addSelectedConic(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedConicsND, GeoConicND.class);
	}
	
	final protected int addSelectedPath(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedPaths, Path.class);
	}
	
	final protected int addSelectedRegion(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelectedRegions(hits, max, addMoreThanOneAllowed, selectedRegions);
	}
	
	final protected int addSelectedImplicitpoly(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedImplicitpoly, GeoImplicitPoly.class);
	}

	final protected int addSelectedFunction(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedFunctions, GeoFunction.class);
	}

	final protected int addSelectedCurve(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedCurves, GeoCurveCartesian.class);
	}

	final protected int addSelectedPolygon(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedPolygons, GeoPolygon.class);
	}
	final protected int addSelectedPolyLine(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedPolyLines, GeoPolyLine.class);
	}

	final protected int addSelectedList(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedLists, GeoList.class);
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
	
	final int selLists() {
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

	final int selSegments() {
		return selectedSegments.size();
	}

	protected final int selVectors() {
		return selectedVectors.size();
	}

	protected final int selConics() {
		return selectedConicsND.size();
	}
	
	
	final int selPaths() {
		return selectedPaths.size();
	}
	
	final int selRegions() {
		return selectedRegions.size();
	}
	
	final int selImplicitpoly() {
		return selectedImplicitpoly.size();
	}

	final int selFunctions() {
		return selectedFunctions.size();
	}

	final int selCurves() {
		return selectedCurves.size();
	}


	/**
	 * 	selectionList may only contain max objects
	 * a choose dialog will be shown if not all objects can be added
	 * @param geos: a clone of the to-be-added list
	 * @param addMoreThanOneAllowed: it's possible to add several objects
	 * without choosing
	 */
	final protected int addToSelectionList(ArrayList selectionList,
			ArrayList geos, int max, boolean addMoreThanOneAllowed, boolean tryDeselect) {
				
		if (geos == null)
			return 0;
		//GeoElement geo;

		// ONLY ONE ELEMENT IN THE EFFECTIVE HITS
		if (tryDeselect && geos.size()==1)
			//select or deselect it
			return addToSelectionList(selectionList, (GeoElement) geos.get(0), max);

		//	SEVERAL ELEMENTS
		// here none of the selected geos should be removed
		
		//we don't want to add repeated elements
		geos.removeAll(selectionList);
		//  too many objects -> choose one
		if (!addMoreThanOneAllowed || geos.size() + selectionList.size() > max){
			//Application.printStacktrace(geos.toString());
			return addToSelectionList(selectionList, chooseGeo(geos, true), max);
		}

		// already selected objects -> choose one
		boolean contained = false;
		for (int i = 0; i < geos.size(); i++) {
			if (selectionList.contains(geos.get(i)))
				contained = true;
		}
		if (contained)
			return addToSelectionList(selectionList, chooseGeo(geos, true), max);

		// add all objects to list
		int count = 0;
		for (int i = 0; i < geos.size(); i++) {
			count += addToSelectionList(selectionList, (GeoElement) geos.get(i), max);
		}
		return count;
	}

	//	selectionList may only contain max objects
	// an already selected objects is deselected
	final protected int addToSelectionList(ArrayList selectionList,
			GeoElement geo, int max) {
		if (geo == null)
			return 0;

		int ret = 0;
		if (selectionList.contains(geo)) { // remove from selection
			selectionList.remove(geo);
			if (selectionList != selectedGeos)
				selectedGeos.remove(geo);
			ret =  -1;
		} else { // new element: add to selection
			if (selectionList.size() < max) {
				selectionList.add(geo);
				if (selectionList != selectedGeos)
					selectedGeos.add(geo);
				ret = 1;
			} 
		}
		if (ret != 0) app.toggleSelectedGeo(geo);
		return ret;
	}

	// selectionList may only contain max objects
	final protected int addToHighlightedList(ArrayList selectionList,
			ArrayList<GeoElement> geos, int max) {
		
		
		if (geos == null)
			return 0;

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

	/**
	 * Shows dialog to choose one object out of hits[] that is an instance of
	   specified class (note: subclasses are included)
	 * 
	 */
	private GeoElement chooseGeo(Hits hits, Class geoclass) {
		return chooseGeo(hits.getHits(geoclass, tempArrayList), true);
	}

	protected GeoElement chooseGeo(ArrayList<GeoElement> geos, boolean includeFixed) {
		if (geos == null)
			return null;
		
		if (geos.size() > 1) removeAxes(geos);

		GeoElement ret = null;
		GeoElement retFree = null;
		GeoElement retPath = null;
		GeoElement retIndex = null;
		GeoElement retSegment = null;

		switch (geos.size()) {
		case 0:
			ret =  null;
			break;

		case 1:
			ret =  (GeoElement) geos.get(0);
			
			if (!includeFixed && ret.isFixed()) return null;
			
			break;

		default:	

			int maxLayer = -1;

			int layerCount = 0;

			// work out max layer, and
			// count no of objects in max layer
			for (int i = 0 ; i < geos.size() ; i++) {
				GeoElement geo = (GeoElement)(geos.get(i));
				int layer = geo.getLayer();

				if (layer > maxLayer && (includeFixed || !geo.isFixed())) {
					maxLayer = layer;
					layerCount = 1;
					ret = geo;
				}
				else if (layer == maxLayer)
				{
					layerCount ++;
				}

			}

			//Application.debug("maxLayer"+maxLayer);
			//Application.debug("layerCount"+layerCount);

			// only one object in top layer, return it.
			if (layerCount == 1) return ret;


			int pointCount = 0;
			int freePointCount = 0;
			int pointOnPathCount = 0;
			int segmentCount = 0;
			//int polygonCount = 0;
			int minIndex = Integer.MAX_VALUE;

			// count no of points in top layer
			for (int i = 0 ; i < geos.size() ; i++) {
				GeoElement geo = (GeoElement)(geos.get(i));
				
				if (geo.isGeoPoint() && geo.getLayer() == maxLayer
						&& (includeFixed || !geo.isFixed())) {
					pointCount ++;
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

					// find point-on-path/region with the highest construction index
					if (((GeoPointND)geo).isPointOnPath() || ((GeoPointND)geo).isPointInRegion()) {
						pointOnPathCount ++;
						if (retPath == null) {
							retPath = geo;
						}
						else
						{
							if (geo.getConstructionIndex() > retPath.getConstructionIndex()) retPath = geo;
						}
					}

					// find free point with the highest construction index
					if (geo.isIndependent()) {
						freePointCount ++;
						if (retFree == null) {
							retFree = geo;
						}
						else
						{
							if (geo.getConstructionIndex() > retFree.getConstructionIndex()) retFree = geo;						
						}
					}
				}
			}
			//Application.debug("pointOnPathCount"+pointOnPathCount);
			//Application.debug("freePointCount"+freePointCount);
			//Application.debug("pointCount"+pointCount);

			// return point-on-path with highest index
			if (pointOnPathCount > 0) return retPath;

			// return free-point with highest index
			if (freePointCount > 0) return retFree;

			// only one point in top layer, return it
			if (pointCount == 1) return ret;

			// just return the most recently created point
			if (pointCount > 1) return retIndex;

			/*
			try {
				throw new Exception("choose");
			} catch (Exception e) {
				e.printStackTrace();

			}
			 */

			boolean allFixed = false;

			// remove fixed objects (if there are some not fixed)
			if (!includeFixed && geos.size() > 1) {

				allFixed = true;
				for (int i = 0 ; i < geos.size() ; i++)
					if (!((GeoElement)geos.get(i)).isFixed())
						allFixed = false;

				if (!allFixed)
					for (int i = geos.size() - 1 ; i >= 0 ; i--) {
						GeoElement geo = (GeoElement)geos.get(i);
						if (geo.isFixed()) geos.remove(i);		
					}

				if (geos.size() == 1)
					return (GeoElement)geos.get(0);
			}
			
			//int maxPolygonLayer = 0;
			// count segments and polygons
			for (int i = 0 ; i < geos.size() ; i++) {
				GeoElement geo = (GeoElement)(geos.get(i));
				
				if (geo.isGeoSegment()) {
					segmentCount++;
					if (retSegment == null) {
						retSegment = geo;
					} else {
						
						// select Segment with lowest layer (& construction index)
						if (retSegment.getLayer() < geo.getLayer()||
						(retSegment.getLayer() == geo.getLayer() && retSegment.getConstructionIndex() > geo.getConstructionIndex())) {
							retSegment = geo;
							
						}
					}
				//} else if (geo.isGeoPolygon()) {
				//	polygonCount++;
				//	if (geo.getLayer() > maxPolygonLayer) maxPolygonLayer = geo.getLayer();
				}				
			}
			
			// check for edge of polygon being selected (priority over polygon itself)
			//if (segmentCount == 1 && (segmentCount + polygonCount == geos.size())) {
			//	if (retSegment.getLayer() >= maxPolygonLayer) return retSegment;
			//}
			
			// give segments priority over eg Polygons, Lines
			// that they might be drawn on top of
			if (segmentCount > 0) return retSegment;
			
			// don't want a popup in this case
			// eg multiple fixed images from Pen Tool
			if (!includeFixed && allFixed) return null; 
			
			/* no points selected, multiple objects selected
			// popup a menu to choose from
			ToolTipManager ttm = ToolTipManager.sharedInstance();		
			ttm.setEnabled(false);			
			ListDialog dialog = new ListDialog((JPanel) view, geos, null);
			if (app.areChooserPopupsEnabled()) ret = dialog.showDialog((JPanel) view, mouseLoc);			
			ttm.setEnabled(true);			*/
			
			// popup removed, just return something at this stage
			ret = geos.get(0);
		}
		return ret;	

	}

	private void removeAxes(ArrayList<GeoElement> geos) {

		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo = geos.get(i);
			if (geo instanceof GeoAxis) {
				geos.remove(i);
			}
		}
	}


	public void componentResized(ComponentEvent e) {
		// tell the view that it was resized
		view.updateSize();
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	/**
	 * Zooms in or out using mouse wheel
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {

		if (textfieldHasFocus) return;
		
		if (mode == EuclidianConstants.MODE_PEN) return;

		// don't allow mouse wheel zooming for applets if mode is not zoom mode
		boolean allowMouseWheel = 
			!app.isApplet() ||
			mode == EuclidianConstants.MODE_ZOOM_IN ||
			mode == EuclidianConstants.MODE_ZOOM_OUT ||
			(app.isShiftDragZoomEnabled() && 
					(e.isControlDown() || e.isMetaDown() || e.isShiftDown()));			
		if (!allowMouseWheel)
			return;

		setMouseLocation(e);

		//double px = view.width / 2d;
		//double py = view.height / 2d;
		double px = mouseLoc.x;
		double py = mouseLoc.y;
		//double dx = view.getXZero() - px;
		//double dy = view.getYZero() - py;

		double xFactor = 1;
		if (e.isAltDown())
			xFactor = 1.5;
		
		double reverse = app.isMouseWheelReversed() ? -1 : 1;


		double factor = (e.getWheelRotation() * reverse > 0) ?
				EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR * xFactor:
					1d / (EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR * xFactor);


		// make zooming a little bit smoother by having some steps

		view.setAnimatedCoordSystem(
				//px + dx * factor,
				//py + dy * factor,
				px, py, factor,
				view.getXscale() * factor, 4, false);
		//view.yscale * factor);
		app.setUnsaved();
	}

	public void zoomInOut(KeyEvent event) {
		boolean allowZoom = 
			!app.isApplet() ||
			mode == EuclidianConstants.MODE_ZOOM_IN ||
			mode == EuclidianConstants.MODE_ZOOM_OUT ||
			(app.isShiftDragZoomEnabled());			
		if (!allowZoom)
			return;
		double px,py;
		if(mouseLoc!=null){
			px = mouseLoc.x;
			py = mouseLoc.y;
		}else{
			px = view.getWidth()/2;
			py = view.getHeight()/2;
		}
		
		double factor = event.getKeyCode() == KeyEvent.VK_MINUS ? 1d / EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR
				: EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR;

		// accelerated zoom
		if (event.isAltDown())
			factor *= event.getKeyCode() == KeyEvent.VK_MINUS ? 2d/3d: 1.5;

		// make zooming a little bit smoother by having some steps
		view.setAnimatedCoordSystem(
				//px + dx * factor,
				//py + dy * factor,
				px,py,factor,
				view.getXscale() * factor, 4, false);
		//view.yscale * factor);
		app.setUnsaved();


	}

	/*
	 * when drawing a line, this is used when alt is down
	 * to set the angle to be a multiple of 15 degrees
	 */
	public void setLineEndPoint(Point2D.Double point) {
		lineEndPoint = point;
		useLineEndPoint = true;
	}

	///////////////////////////////////////////
	// moved GeoElements



	/** return the current movedGeoPoint */
	public GeoElement getMovedGeoPoint(){
		return ((GeoElement) movedGeoPoint);
	}

	private boolean textfieldHasFocus = false;

	public void textfieldHasFocus(boolean hasFocus) {
		textfieldHasFocus = hasFocus;
	}







	///////////////////////////////////////////
	// EMPTY METHODS USED FOR EuclidianView3D


	/** right-press the mouse makes start 3D rotation */
	protected void processRightPressFor3D(){

	}


	/** right-drag the mouse makes 3D rotation 
	 * @return false */
	protected boolean processRotate3DView(){
		return false;
	}



	/** right-release the mouse makes stop 3D rotation 
	 * @return false */
	protected boolean processRightReleaseFor3D(){
		return false;
	}

	
	

	
	//******************************
	// PropertiesPanelMini Listeners
	//******************************
	int penLineStyle = 0;
	public void setLineStyle(int lineStyle) {
		penLineStyle = lineStyle;
		
		//if (mode == EuclidianView.MODE_VISUAL_STYLE) {
			ArrayList geos = app.getSelectedGeos();
			
			for (int i = 0 ; i < geos.size() ; i++) {
				GeoElement geo = (GeoElement)geos.get(i);
				geo.setLineType(lineStyle);
				geo.updateRepaint();
				
			}
	//	}
	}

		
	
	final private static int eraserSize = 12;
	private boolean erasing = false;
	private int penSize = 3;
	
	public void setSize(int size) {
		penSize = size;
		
	//	if (mode == EuclidianView.MODE_VISUAL_STYLE) {
			ArrayList geos = app.getSelectedGeos();
			
			for (int i = 0 ; i < geos.size() ; i++) {
				GeoElement geo = (GeoElement)geos.get(i);
				if (geo instanceof PointProperties) {
					((PointProperties)geo).setPointSize(size);
					geo.updateRepaint();
				} else {
					geo.setLineThickness(size);
					geo.updateRepaint();
				}
			}
	//	}
		
	}
	
	Color penColor = Color.black;
	public void setColor(Color color) {
		penColor = color;
		
//		if (mode == EuclidianView.MODE_VISUAL_STYLE) {
			ArrayList geos = app.getSelectedGeos();
			
			for (int i = 0 ; i < geos.size() ; i++) {
				GeoElement geo = (GeoElement)geos.get(i);
				geo.setObjColor(color);
				geo.updateRepaint();
			}
	//	}

	}
	
	
	public void setAlpha(float alpha) {	
			ArrayList geos = app.getSelectedGeos();		
			for (int i = 0 ; i < geos.size() ; i++) {
				GeoElement geo = (GeoElement)geos.get(i);
				geo.setAlphaValue(alpha);
				geo.updateRepaint();
			}
	}
	
	
	private void openMiniPropertiesPanel() {
		if (!app.useFullGui()) return;
		if (app.isMiniPropertiesActive())
			app.getGuiManager().toggleMiniProperties(true);

	}

	private void closeMiniPropertiesPanel() {
		if (!app.useFullGui()) return;
		app.getGuiManager().toggleMiniProperties(false);

	}
	
	protected boolean moveMode(int mode) {
		if (mode == EuclidianConstants.MODE_MOVE ||
				mode == EuclidianConstants.MODE_VISUAL_STYLE)
			return true;
		
		else return false;
	}


	private String sliderValue = null;
	public String getSliderValue() {
		return sliderValue;
	}
	
	public Hits getHighlightedgeos(){
		return highlightedGeos.clone();
	}

}
