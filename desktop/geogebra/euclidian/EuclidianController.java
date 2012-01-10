/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.euclidian;

import geogebra.common.awt.Point;
import geogebra.common.awt.Point2D;
import geogebra.common.euclidian.DrawConic;
import geogebra.common.euclidian.DrawConicPart;
import geogebra.common.euclidian.DrawSlider;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.Previewable;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoDynamicCoordinates;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoPolygon;
import geogebra.common.kernel.algos.AlgoTranslate;
import geogebra.common.kernel.algos.AlgoVector;
import geogebra.common.kernel.algos.AlgoVectorPoint;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.VectorValue;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoAxis;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoConicPart;
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
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.GeoClass;
import geogebra.common.util.MyMath;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.ToolTipManager;
import javax.swing.text.JTextComponent;

/**
 * EuclidianController.java
 * 
 * Created on 16. October 2001, 15:41
 */
public class EuclidianController extends geogebra.common.euclidian.AbstractEuclidianController implements MouseListener, MouseMotionListener,
		MouseWheelListener, ComponentListener, PropertiesPanelMiniListener {

	

	
	

	// protected GeoVec2D b;

	

	// protected GeoSegment movedGeoSegment;

	

	// protected MyPopupMenu popupMenu;

	

	// boolean polygonRigid = false;

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

	public void setApplication(AbstractApplication app) {
		this.app = app;
	}

	public Application getApplication() {
		return (Application) app;
	}

	protected void setView(EuclidianViewInterface view) {
		// void setView(EuclidianView view) {
		this.view = view;
	}


	public void setPen(EuclidianPen pen) {
		this.pen = pen;
	}

	public EuclidianPen getPen() {
		return (EuclidianPen) pen;
	}

	public void setMode(int newMode) {

		if ((newMode == EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS)
				|| (newMode == EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS)
				|| (newMode == EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS)) {
			return;
		}

		endOfMode(mode);

		allowSelectionRectangleForTranslateByVector = true;

		if (AbstractEuclidianView.usesSelectionRectangleAsInput(newMode)
				&& (((EuclidianViewInterface) view).getSelectionRectangle() != null)) {
			initNewMode(newMode);
			if (((Application)app).getActiveEuclidianView() == view) {
				processSelectionRectangle(null);
			}
		} else if (AbstractEuclidianView.usesSelectionAsInput(newMode)) {
			initNewMode(newMode);
			if (((Application)app).getActiveEuclidianView() == view) {
				processSelection();
			}
		} else {
			if (!TEMPORARY_MODE) {
				((Application)app).clearSelectedGeos(false);
			}
			initNewMode(newMode);
		}

		kernel.notifyRepaint();
	}

	protected void initNewMode(int mode) {
		this.mode = mode;
		initShowMouseCoords();
		// Michael Borcherds 2007-10-12
		// clearSelections();
		if (!TEMPORARY_MODE
				&& !AbstractEuclidianView.usesSelectionRectangleAsInput(mode)) {
			clearSelections();
		}
		// Michael Borcherds 2007-10-12
		moveMode = MOVE_NONE;

		closeMiniPropertiesPanel();

		if (mode == EuclidianConstants.MODE_RECORD_TO_SPREADSHEET) {
			if (!((Application) app).getGuiManager().hasSpreadsheetView()) {
				((Application) app).getGuiManager().attachSpreadsheetView();
			}
			if (!((Application) app).getGuiManager().showView(
					AbstractApplication.VIEW_SPREADSHEET)) {
				((Application) app).getGuiManager().setShowView(true,
						AbstractApplication.VIEW_SPREADSHEET);
			}
		}

		((EuclidianViewInterface) view).setPreview(switchPreviewableForInitNewMode(mode));
		toggleModeChangedKernel = false;
	}

	protected Previewable switchPreviewableForInitNewMode(int mode) {

		Previewable previewDrawable = null;
		// init preview drawables
		switch (mode) {

		case EuclidianConstants.MODE_FREEHAND:
			pen.setFreehand(true);

			break;
		case EuclidianConstants.MODE_PEN:
			pen.setFreehand(false);

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

			ArrayList<GeoElement> selection = ((Application)app).getSelectedGeos();
			if (selection.size() == 1) {
				GeoElement geo = selection.get(0);
				// getCorner(1) == null as we can't write to transformed images
				if (geo.isGeoImage()) {
					GeoPoint2 c1 = ((GeoImage) geo).getCorner(0);
					GeoPoint2 c2 = ((GeoImage) geo).getCorner(1);
					GeoPoint2 c3 = ((GeoImage) geo).getCorner(2);

					if ((c3 == null)
							&& ((c2 == null // c2 = null -> not transformed
							)
							// or c1 and c2 are the correct spacing for the
							// image not to be transformed
							// (ie image was probably created by the Pen Tool)
							|| ((c1 != null) && (c2 != null)
									&& (c1.inhomY == c2.inhomY) && ((((EuclidianViewInterface) view)
									.toScreenCoordX(c2.inhomX) - ((EuclidianViewInterface) view)
									.toScreenCoordX(c1.inhomX)) == ((GeoImage) geo)
									.getFillImage().getWidth())))) {
						pen.setPenGeo((GeoImage) geo);
					} else {
						pen.setPenGeo(null);
					}

					pen.setPenWritingToExistingImage(pen.getPenGeo() != null);
				}
			}

			// no break;

		case EuclidianConstants.MODE_VISUAL_STYLE:

			// openMiniPropertiesPanel();

			break;

		case EuclidianConstants.MODE_PARALLEL:
			previewDrawable = ((EuclidianViewInterface) view).createPreviewParallelLine(selectedPoints,
					selectedLines);
			break;

		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
			previewDrawable = ((EuclidianViewInterface) view).createPreviewAngleBisector(selectedPoints);
			break;

		case EuclidianConstants.MODE_ORTHOGONAL:
			previewDrawable = ((EuclidianViewInterface) view).createPreviewPerpendicularLine(
					selectedPoints, selectedLines);
			break;

		case EuclidianConstants.MODE_LINE_BISECTOR:
			previewDrawable = ((EuclidianViewInterface) view)
					.createPreviewPerpendicularBisector(selectedPoints);
			break;

		case EuclidianConstants.MODE_JOIN: // line through two points
			useLineEndPoint = false;
			previewDrawable = ((EuclidianViewInterface) view).createPreviewLine(selectedPoints);
			break;

		case EuclidianConstants.MODE_SEGMENT:
			useLineEndPoint = false;
			previewDrawable = ((EuclidianViewInterface) view).createPreviewSegment(selectedPoints);
			break;

		case EuclidianConstants.MODE_RAY:
			useLineEndPoint = false;
			previewDrawable = ((EuclidianViewInterface) view).createPreviewRay(selectedPoints);
			break;

		case EuclidianConstants.MODE_VECTOR:
			useLineEndPoint = false;
			previewDrawable = ((EuclidianViewInterface) view).createPreviewVector(selectedPoints);
			break;

		case EuclidianConstants.MODE_POLYGON:
		case EuclidianConstants.MODE_RIGID_POLYGON:
		case EuclidianConstants.MODE_VECTOR_POLYGON:
			previewDrawable = ((EuclidianViewInterface) view).createPreviewPolygon(selectedPoints);
			break;

		case EuclidianConstants.MODE_POLYLINE:
			previewDrawable = ((EuclidianViewInterface) view).createPreviewPolyLine(selectedPoints);
			break;

		case EuclidianConstants.MODE_CIRCLE_TWO_POINTS:
		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
			previewDrawable = ((EuclidianViewInterface) view).createPreviewConic(mode, selectedPoints);
			break;

		case EuclidianConstants.MODE_ANGLE:
			previewDrawable = ((EuclidianViewInterface) view).createPreviewAngle(selectedPoints);
			break;

		// preview for compass: radius first
		case EuclidianConstants.MODE_COMPASSES:
			previewDrawable = new DrawConic((EuclidianView) view, mode,
					selectedPoints, selectedSegments, selectedConicsND);
			break;

		// preview for arcs and sectors
		case EuclidianConstants.MODE_SEMICIRCLE:
		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			previewDrawable = new DrawConicPart((EuclidianView) view, mode,
					selectedPoints);
			break;

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			useLineEndPoint = false;
			previewDrawable = ((EuclidianViewInterface) view).createPreviewVector(selectedPoints);
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
					((Application)app).addSelectedGeo(geo);
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

			// G.Sturr 2010-5-14
			if (recordObject != null) {
				((Application) app).getGuiManager().removeSpreadsheetTrace(recordObject);
				// END G.Sturr
			}

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
	
	protected void wrapMouseclicked(AbstractEvent event) {
		
		if ((mode == EuclidianConstants.MODE_PEN)
				|| (mode == EuclidianConstants.MODE_FREEHAND)) {
			return;
		}

		Hits hits;
		// GeoElement geo;

		setAltDown(event.isAltDown());

		if (mode != EuclidianConstants.MODE_SELECTION_LISTENER) {
			view.requestFocusInWindow();
		}

		if (Application.isRightClick(event)) {
			return;
		}
		setMouseLocation(event);

		// double-click on object selects MODE_MOVE and opens redefine dialog
		if (event.getClickCount() == 2) {
			if (app.isApplet() || Application.isControlDown(event)) {
				return;
			}

			app.clearSelectedGeos();
			// hits = view.getTopHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits().getTopHits();
			switchModeForRemovePolygons(hits);
			if (!hits.isEmpty()) {
				view.setMode(EuclidianConstants.MODE_MOVE);
				GeoElement geo0 = hits.get(0);

				if (geo0.isGeoNumeric() && ((GeoNumeric) geo0).isSlider()) {
					// double-click slider -> Object Properties
					app.getGuiManager().getDialogManager()
							.showPropertiesDialog(hits);
				} else if (!geo0.isFixed()
						&& !(geo0.isGeoBoolean() && geo0.isIndependent())
						&& !(geo0.isGeoImage() && geo0.isIndependent())
						&& !geo0.isGeoButton()) {
					app.getGuiManager().getDialogManager()
							.showRedefineDialog(hits.get(0), true);
				}
			}

		}

		mouseClickedMode(event, mode);

		// Alt click: copy definition to input field
		if (event.isAltDown() && app.showAlgebraInput()) {
			((EuclidianViewInterface) view).setHits(mouseLoc);
			hits = ((EuclidianViewInterface) view).getHits().getTopHits();
			hits.removePolygons();
			if ((hits != null) && (hits.size() > 0)) {
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

	public void mouseClicked(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseclicked(event);
		/*if ((mode == EuclidianConstants.MODE_PEN)
				|| (mode == EuclidianConstants.MODE_FREEHAND)) {
			return;
		}

		Hits hits;
		// GeoElement geo;

		setAltDown(e.isAltDown());

		if (mode != EuclidianConstants.MODE_SELECTION_LISTENER) {
			((EuclidianViewInterface) view).requestFocusInWindow();
		}

		if (Application.isRightClick(e)) {
			return;
		}
		setMouseLocation(geogebra.euclidian.event.MouseEvent.wrapEvent(e));

		// double-click on object selects MODE_MOVE and opens redefine dialog
		if (e.getClickCount() == 2) {
			if (((Application)app).isApplet() || Application.isControlDown(e)) {
				return;
			}

			((Application)app).clearSelectedGeos();
			// hits = view.getTopHits(mouseLoc);
			((EuclidianViewInterface) view).setHits(mouseLoc);
			hits = ((EuclidianViewInterface) view).getHits().getTopHits();
			switchModeForRemovePolygons(hits);
			if (!hits.isEmpty()) {
				view.setMode(EuclidianConstants.MODE_MOVE);
				GeoElement geo0 = hits.get(0);

				if (geo0.isGeoNumeric() && ((GeoNumeric) geo0).isSlider()) {
					// double-click slider -> Object Properties
					((Application) app).getGuiManager().getDialogManager()
							.showPropertiesDialog(hits);
				} else if (!geo0.isFixed()
						&& !(geo0.isGeoBoolean() && geo0.isIndependent())
						&& !(geo0.isGeoImage() && geo0.isIndependent())
						&& !geo0.isGeoButton()) {
					((Application) app).getGuiManager().getDialogManager()
							.showRedefineDialog(hits.get(0), true);
				}
			}

		}

		mouseClickedMode(e, mode);

		// Alt click: copy definition to input field
		if (e.isAltDown() && ((Application) app).showAlgebraInput()) {
			((EuclidianViewInterface) view).setHits(mouseLoc);
			hits = ((EuclidianViewInterface) view).getHits().getTopHits();
			hits.removePolygons();
			if ((hits != null) && (hits.size() > 0)) {
				GeoElement geo = hits.get(0);

				// F3 key: copy definition to input bar
				if (mode != EuclidianConstants.MODE_ATTACH_DETACH) {
					((Application) app).getGlobalKeyDispatcher()
							.handleFunctionKeyForAlgebraInput(3, geo);
				}

				moveMode = MOVE_NONE;
				return;
			}
		}*/
	}

	protected void mouseClickedMode(AbstractEvent event, int mode) {

		switch (mode) {
		case EuclidianConstants.MODE_RECORD_TO_SPREADSHEET:
			clearSelections();
			break;
		case EuclidianConstants.MODE_VISUAL_STYLE:
		case EuclidianConstants.MODE_MOVE:
		case EuclidianConstants.MODE_SELECTION_LISTENER:
			switch (event.getClickCount()) {
			case 1:
				// handle selection click
				((EuclidianViewInterface) view).setHits(mouseLoc);
				handleSelectClick(((EuclidianViewInterface) view).getHits().getTopHits(),// view.getTopHits(mouseLoc),
						Application.isControlDown(event));
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
			((EuclidianViewInterface) view).zoom(mouseLoc.x, mouseLoc.y, EuclidianView.MODE_ZOOM_FACTOR,
					15, false);
			toggleModeChangedKernel = true;
			break;

		case EuclidianConstants.MODE_ZOOM_OUT:
			((EuclidianViewInterface) view).zoom(mouseLoc.x, mouseLoc.y,
					1d / EuclidianView.MODE_ZOOM_FACTOR, 15, false);
			toggleModeChangedKernel = true;
			break;
		}
	}

	protected void handleSelectClick(ArrayList<GeoElement> geos,
			boolean ctrlDown) {
		if (geos == null) {
			((Application)app).clearSelectedGeos();
		} else {
			if (ctrlDown) {
				// boolean selected = geo.is
				((Application) app).toggleSelectedGeo(chooseGeo(geos, true));
				// app.geoElementSelected(geo, true); // copy definiton to input
				// bar
			} else {
				if (!moveModeSelectionHandled) {
					GeoElement geo = chooseGeo(geos, true);
					if (geo != null) {
						((Application)app).clearSelectedGeos(false);
						((Application)app).addSelectedGeo(geo);
					}
				}
			}
		}
	}

	protected void mousePressedTranslatedView(AbstractEvent e) {

		Hits hits;

		// check if axis is hit
		// hits = view.getHits(mouseLoc);
		((EuclidianViewInterface) view).setHits(mouseLoc);
		hits = ((EuclidianViewInterface) view).getHits();
		hits.removePolygons();
		// Application.debug("MODE_TRANSLATEVIEW - "+hits.toString());

		/*
		 * if (!hits.isEmpty() && hits.size() == 1) { Object hit0 = hits.get(0);
		 * if (hit0 == kernel.getXAxis()) moveMode = MOVE_X_AXIS; else if (hit0
		 * == kernel.getYAxis()) moveMode = MOVE_Y_AXIS; else moveMode =
		 * MOVE_VIEW; } else { moveMode = MOVE_VIEW; }
		 */

		moveMode = MOVE_VIEW;
		if (!hits.isEmpty()) {
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
				((EuclidianViewInterface) view).setMoveCursor();
			} else {
				((EuclidianViewInterface) view).setDragCursor();
			}
		}

		// xZeroOld = view.getXZero();
		// yZeroOld = view.getYZero();
		((EuclidianViewInterface) view).rememberOrigins();
		xTemp = xRW;
		yTemp = yRW;
		((EuclidianViewInterface) view).setShowAxesRatio((moveMode == MOVE_X_AXIS)
				|| (moveMode == MOVE_Y_AXIS));
		// view.setDrawMode(EuclidianConstants.DRAW_MODE_DIRECT_DRAW);

	}

	/**
	 * 
	 * @return true if a view button has been pressed (see 3D)
	 */
	protected boolean handleMousePressedForViewButtons() {
		return false;
	}
	
	protected void wrapMousePressed(AbstractEvent event) {
		
		if (app.isUsingFullGui()) {
			// determine parent panel to change focus
			// EuclidianDockPanelAbstract panel =
			// (EuclidianDockPanelAbstract)SwingUtilities.getAncestorOfClass(EuclidianDockPanelAbstract.class,
			// (Component)e.getSource());

			// if(panel != null) {
			// app.getGuiManager().getLayout().getDockManager().setFocusedPanel(panel);
			// }
			app.getGuiManager().setFocusedPanel(event);
		}

		setMouseLocation(event);

		if (handleMousePressedForViewButtons()) {
			return;
		}

		Hits hits;

		if ((mode == EuclidianConstants.MODE_PEN)
				|| (mode == EuclidianConstants.MODE_FREEHAND)) {
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removeAllButImages();
			pen.handleMousePressedForPenMode(event, hits);
			return;
		}

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

		if (Application.isRightClick(event)) {
			// ggb3D - for 3D rotation
			processRightPressFor3D();

			return;
		} else if (app.isShiftDragZoomEnabled() && (
		// MacOS: shift-cmd-drag is zoom
				(event.isShiftDown() && !Application.isControlDown(event)) // All
																	// Platforms:
																	// Shift key
						|| (event.isControlDown() && Application.WINDOWS // old
																		// Windows
																		// key:
																		// Ctrl
																		// key
						) || Application.isMiddleClick(event))) {
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

	public void mousePressed(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMousePressed(event);
		/*if (((Application)app).isUsingFullGui()) {
			// determine parent panel to change focus
			// EuclidianDockPanelAbstract panel =
			// (EuclidianDockPanelAbstract)SwingUtilities.getAncestorOfClass(EuclidianDockPanelAbstract.class,
			// (Component)e.getSource());

			// if(panel != null) {
			// app.getGuiManager().getLayout().getDockManager().setFocusedPanel(panel);
			// }
			((Application) app).getGuiManager().setFocusedPanel(e);
		}

		setMouseLocation(geogebra.euclidian.event.MouseEvent.wrapEvent(e));

		if (handleMousePressedForViewButtons()) {
			return;
		}

		Hits hits;

		if ((mode == EuclidianConstants.MODE_PEN)
				|| (mode == EuclidianConstants.MODE_FREEHAND)) {
			((EuclidianViewInterface) view).setHits(mouseLoc);
			hits = ((EuclidianViewInterface) view).getHits();
			hits.removeAllButImages();
			((EuclidianPen) pen).handleMousePressedForPenMode(e, hits);
			return;
		}

		// GeoElement geo;
		transformCoords();

		moveModeSelectionHandled = false;
		DRAGGING_OCCURED = false;
		((EuclidianViewInterface) view).setSelectionRectangle(null);
		selectionStartPoint.setLocation(mouseLoc);

		if (hitResetIcon() || ((EuclidianViewInterface) view).hitAnimationButton(e)) {
			// see mouseReleased
			return;
		}

		if (Application.isRightClick(e)) {
			// ggb3D - for 3D rotation
			processRightPressFor3D();

			return;
		} else if (((Application) app).isShiftDragZoomEnabled() && (
		// MacOS: shift-cmd-drag is zoom
				(e.isShiftDown() && !Application.isControlDown(e)) // All
																	// Platforms:
																	// Shift key
						|| (e.isControlDown() && Application.WINDOWS // old
																		// Windows
																		// key:
																		// Ctrl
																		// key
						) || Application.isMiddleClick(e))) {
			// Michael Borcherds 2007-12-08 BEGIN
			// bugfix: couldn't select multiple objects with Ctrl

			((EuclidianViewInterface) view).setHits(mouseLoc);
			hits = ((EuclidianViewInterface) view).getHits();
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

		switchModeForMousePressed(e);*/

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
			AbstractApplication.debug("complex" + complex);
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

	protected void switchModeForMousePressed(AbstractEvent e) {

		Hits hits;

		switch (mode) {
		// create new point at mouse location
		// this point can be dragged: see mouseDragged() and mouseReleased()
		case EuclidianConstants.MODE_COMPLEX_NUMBER:
			((EuclidianViewInterface) view).setHits(mouseLoc);
			hits = ((EuclidianViewInterface) view).getHits();
			createNewPointForModePoint(hits, true);
			break;
		case EuclidianConstants.MODE_POINT:
		case EuclidianConstants.MODE_POINT_ON_OBJECT:
			((EuclidianViewInterface) view).setHits(mouseLoc);
			hits = ((EuclidianViewInterface) view).getHits();
			AbstractApplication.debug(hits);
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
			((EuclidianViewInterface) view).setHits(mouseLoc);
			hits = ((EuclidianViewInterface) view).getHits();
			hits.removePolygons();
			createNewPointForModeOther(hits);
			break;

		case EuclidianConstants.MODE_VECTOR_POLYGON:
		case EuclidianConstants.MODE_RIGID_POLYGON:
			((EuclidianViewInterface) view).setHits(mouseLoc);
			hits = ((EuclidianViewInterface) view).getHits();
			hits.removePolygons();
			createNewPoint(hits, false, false, false, false, false);
			break;

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			if (!allowSelectionRectangleForTranslateByVector) {
				((EuclidianViewInterface) view).setHits(mouseLoc);
				hits = ((EuclidianViewInterface) view).getHits();
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
			((EuclidianViewInterface) view).setHits(mouseLoc);
			hits = ((EuclidianViewInterface) view).getHits();
			hits.removePolygons();
			if (hits.size() == 0) {
				createNewPoint(hits, false, true, true);
			}
			break;

		case EuclidianConstants.MODE_COMPASSES: // Michael Borcherds 2008-03-13
			// hits = view.getHits(mouseLoc);
			((EuclidianViewInterface) view).setHits(mouseLoc);
			hits = ((EuclidianViewInterface) view).getHits();
			hits.removePolygons();
			if (hits.isEmpty()) {
				createNewPoint(hits, false, true, true);
			}
			break;

		case EuclidianConstants.MODE_ANGLE:
			// hits = view.getTopHits(mouseLoc);
			((EuclidianViewInterface) view).setHits(mouseLoc);
			hits = ((EuclidianViewInterface) view).getHits().getTopHits();
			// check if we got a polygon
			if (hits.isEmpty()) {
				createNewPoint(hits, false, false, true);
			}
			break;

		case EuclidianConstants.MODE_ANGLE_FIXED:
		case EuclidianConstants.MODE_MIDPOINT:
			// hits = view.getHits(mouseLoc);
			((EuclidianViewInterface) view).setHits(mouseLoc);
			hits = ((EuclidianViewInterface) view).getHits();
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
			((EuclidianViewInterface) view).setHits(mouseLoc);
			hits = ((EuclidianViewInterface) view).getHits();
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
					if (!((Application) app).getTraceManager().isTraceGeo(tracegeo)) {
						((Application) app).getGuiManager().addSpreadsheetTrace(tracegeo);
					}
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
			((EuclidianViewInterface) view).setHits(mouseLoc);
			rotationCenter = (GeoPoint2) chooseGeo(
					((EuclidianViewInterface) view).getHits().getHits(Test.GEOPOINT2, tempArrayList),
					true);
			((Application)app).addSelectedGeo(rotationCenter);
			moveMode = MOVE_NONE;
		} else {
			((EuclidianViewInterface) view).setHits(mouseLoc);
			hits = ((EuclidianViewInterface) view).getHits();
			hits.removePolygons();
			// hits = view.getHits(mouseLoc);
			// got rotation center again: deselect
			if (!hits.isEmpty() && hits.contains(rotationCenter)) {
				((Application)app).removeSelectedGeo(rotationCenter);
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
				((Application)app).addSelectedGeo(geo);
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

	protected void handleMousePressedForMoveMode(AbstractEvent e, boolean drag) {

		// long t0 = System.currentTimeMillis();

		// Application.debug("start");

		// view.resetTraceRow(); // for trace/spreadsheet

		// fix for meta-click to work on Mac/Linux
		if (Application.isControlDown(e)) {
			return;
		}

		// move label?
		GeoElement geo = ((EuclidianViewInterface) view).getLabelHit(mouseLoc);
		// Application.debug("label("+(System.currentTimeMillis()-t0)+")");
		if (geo != null) {
			moveMode = MOVE_LABEL;
			movedLabelGeoElement = geo;
			oldLoc.setLocation(geo.labelOffsetX, geo.labelOffsetY);
			startLoc = mouseLoc;
			((EuclidianViewInterface) view).setDragCursor();
			return;
		}

		// Application.debug("laps("+(System.currentTimeMillis()-t0)+")");

		// find and set movedGeoElement
		((EuclidianViewInterface) view).setHits(mouseLoc);

		// make sure that eg slider takes precedence over a polygon (in the same
		// layer)
		((EuclidianViewInterface) view).getHits().removePolygons();

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
			moveableList = ((EuclidianViewInterface) view).getHits().getMoveableHits(view);
		} else {
			moveableList = ((EuclidianViewInterface) view).getHits();
		}

		Hits hits = moveableList.getTopHits();

		// Application.debug("end("+(System.currentTimeMillis()-t0)+")");

		ArrayList<GeoElement> selGeos = ((Application)app).getSelectedGeos();

		// if object was chosen before, take it now!
		if ((selGeos.size() == 1) && !hits.isEmpty()
				&& hits.contains(selGeos.get(0))) {
			// object was chosen before: take it
			geo = selGeos.get(0);
		} else {
			// choose out of hits
			geo = chooseGeo(hits, false);

			if (!selGeos.contains(geo)) {
				((Application)app).clearSelectedGeos();
				((Application)app).addSelectedGeo(geo);
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

	public void handleMovedElement(GeoElement geo, boolean multiple) {
		resetMovedGeoPoint();
		movedGeoElement = geo;

		// multiple geos selected
		if ((movedGeoElement != null) && multiple) {
			moveMode = MOVE_MULTIPLE_OBJECTS;
			startPoint.setLocation(xRW, yRW);
			startLoc = mouseLoc;
			((EuclidianViewInterface) view).setDragCursor();
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
			if (movedGeoElement.isGeoPolygon()) {
				GeoPolygon poly = (GeoPolygon) movedGeoElement;
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
				GeoPoint2 start = null;
				GeoPoint2 end = null;
				if (movedGeoElement.getParentAlgorithm() instanceof AlgoVector) {
					// Vector[A,B]
					AlgoVector algoVec = (AlgoVector) movedGeoElement
							.getParentAlgorithm();
					start = algoVec.getInputPoints().get(0);
					end = algoVec.getInputPoints().get(1);

					if (start.isIndependent() && !end.isIndependent()) {
						end = null;
						transformCoordsOffset[0] = xRW - start.inhomX;
						transformCoordsOffset[1] = yRW - start.inhomY;
						moveMode = MOVE_POINT_WITH_OFFSET;
						movedGeoPoint = start;
						return;

					}

				} else {
					// Segment/ray
					GeoLine line = (GeoLine) movedGeoElement;
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

				if (sameVector && ((vec.label == null) || vec.isIndependent())) {
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
					translateableGeos = movedGeoElement
							.getFreeInputPoints(view);
				}
			}

			// init move dependent mode if we have something to move ;-)
			if (translateableGeos != null) {
				moveMode = MOVE_DEPENDENT;

				if (translateableGeos.get(0) instanceof GeoPoint2) {
					GeoPoint2 point = ((GeoPoint2) translateableGeos.get(0));
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

				((EuclidianViewInterface) view).setDragCursor();
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
			((EuclidianViewInterface) view).setShowMouseCoords(true);
			((EuclidianViewInterface) view).setDragCursor();
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
				GeoPoint2 sP = movedGeoVector.getStartPoint();
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
						sP = new GeoPoint2(kernel.getConstruction());
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

			((EuclidianViewInterface) view).setShowMouseCoords(true);
			((EuclidianViewInterface) view).setDragCursor();
		}

		// free text
		else if (movedGeoElement.isGeoText()) {
			moveMode = MOVE_TEXT;
			movedGeoText = (GeoText) movedGeoElement;
			((EuclidianViewInterface) view).setShowMouseCoords(false);
			((EuclidianViewInterface) view).setDragCursor();

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
				GeoPoint2 loc = (GeoPoint2) movedGeoText.getStartPoint();
				if (loc == null) {
					loc = new GeoPoint2(kernel.getConstruction());
					loc.setCoords(0, 0, 1.0);
					try {
						movedGeoText.setStartPoint(loc);
					} catch (Exception ex) {
					}
					startPoint.setLocation(xRW, yRW);
				} else {
					startPoint.setLocation(xRW - loc.inhomX, yRW - loc.inhomY);

					GeoPoint2 loc2 = new GeoPoint2(loc);
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
			((EuclidianViewInterface) view).setShowMouseCoords(false);
			((EuclidianViewInterface) view).setDragCursor();

			startPoint.setLocation(xRW, yRW);
			if (tempConic == null) {
				tempConic = new GeoConic(kernel.getConstruction());
			}
			tempConic.set(movedGeoConic);
		} else if (movedGeoElement.isGeoImplicitPoly()) {
			moveMode = MOVE_IMPLICITPOLY;
			movedGeoImplicitPoly = (GeoImplicitPoly) movedGeoElement;
			((EuclidianViewInterface) view).setShowMouseCoords(false);
			((EuclidianViewInterface) view).setDragCursor();

			startPoint.setLocation(xRW, yRW);
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
				moveDependentPoints = new ArrayList<GeoPoint2>();
			} else {
				moveDependentPoints.clear();
			}

			for (GeoElement f : movedGeoImplicitPoly.getAllChildren()) {
				// if (f instanceof GeoPoint &&
				// f.getParentAlgorithm().getInput().length==1 &&
				// f.getParentAlgorithm().getInput()[0] instanceof Path){
				if ((f instanceof GeoPoint2)
						&& movedGeoImplicitPoly.isParentOf(f)) {
					GeoPoint2 g = (GeoPoint2) f;
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

		} else if (movedGeoElement.isGeoFunction()) {
			moveMode = MOVE_FUNCTION;
			movedGeoFunction = (GeoFunction) movedGeoElement;
			((EuclidianViewInterface) view).setShowMouseCoords(false);
			((EuclidianViewInterface) view).setDragCursor();

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

			Drawable d = ((EuclidianViewInterface) view).getDrawableFor(movedGeoNumeric);
			if (d instanceof DrawSlider) {
				// should we move the slider
				// or the point on the slider, i.e. change the number
				DrawSlider ds = (DrawSlider) d;
				// TEMPORARY_MODE true -> dragging slider using Slider Tool
				// or right-hand mouse button

				// otherwise using Move Tool -> move dot
				if (((TEMPORARY_MODE && ((Application) app).isRightClickEnabled()) || !movedGeoNumeric
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

			((EuclidianViewInterface) view).setShowMouseCoords(false);
			((EuclidianViewInterface) view).setDragCursor();
		}

		// checkbox
		else if (movedGeoElement.isGeoBoolean()) {
			movedGeoBoolean = (GeoBoolean) movedGeoElement;

			// if fixed checkbox dragged, behave as if it's been clicked
			// important for electronic whiteboards
			if (movedGeoBoolean.isCheckboxFixed()) {
				movedGeoBoolean.setValue(!movedGeoBoolean.getBoolean());
				((Application)app).removeSelectedGeo(movedGeoBoolean); // make sure doesn't get
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

			((EuclidianViewInterface) view).setShowMouseCoords(false);
			((EuclidianViewInterface) view).setDragCursor();

		}

		// button
		else if (movedGeoElement.isGeoButton()) {
			movedGeoButton = (GeoButton) movedGeoElement;
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

			((EuclidianViewInterface) view).setShowMouseCoords(false);
			((EuclidianViewInterface) view).setDragCursor();
		}

		// image
		else if (movedGeoElement.isGeoImage()) {
			moveMode = MOVE_IMAGE;
			movedGeoImage = (GeoImage) movedGeoElement;
			((EuclidianViewInterface) view).setShowMouseCoords(false);
			((EuclidianViewInterface) view).setDragCursor();

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
				startPoint.setLocation(xRW, yRW);
				oldImage = new GeoImage(movedGeoImage);

				GeoPoint2 loc = movedGeoImage.getStartPoints()[2];
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
		} else {
			moveMode = MOVE_NONE;
		}

	}

	// //////////////////////////////////////////
	// setters movedGeoElement -> movedGeoPoint, ...
	public void setMovedGeoPoint(GeoElement geo) {
		movedGeoPoint = (GeoPointND) movedGeoElement;

		AlgoElement algo = ((GeoElement) movedGeoPoint).getParentAlgorithm();
		if ((algo != null) && (algo instanceof AlgoDynamicCoordinates)) {
			movedGeoPoint = ((AlgoDynamicCoordinates) algo).getParentPoint();
		}

		((EuclidianViewInterface) view).setShowMouseCoords(!((Application)app).isApplet() && !movedGeoPoint.hasPath());
		((EuclidianViewInterface) view).setDragCursor();
	}

	public void setStartPointLocation() {
		startPoint.setLocation(xRW, yRW);
	}

	public void resetMovedGeoPoint() {
		movedGeoPoint = null;
	}

	protected boolean viewHasHitsForMouseDragged() {
		return !(((EuclidianViewInterface) view).getHits().isEmpty());
	}
	
	protected void wrapMouseDragged(AbstractEvent event) {
		sliderValue = null;

		if (textfieldHasFocus) {
			return;
		}

		if ((mode == EuclidianConstants.MODE_PEN)
				|| (mode == EuclidianConstants.MODE_FREEHAND)) {
			pen.handleMousePressedForPenMode(event, null);
			return;
		}

		clearJustCreatedGeos();

		if (!DRAGGING_OCCURED) {

			DRAGGING_OCCURED = true;

			if ((mode == EuclidianConstants.MODE_TRANSLATE_BY_VECTOR)
					&& (selGeos() == 0)) {
				((EuclidianViewInterface) view).setHits(mouseLoc);

				Hits hits = ((EuclidianViewInterface) view).getHits().getTopHits();

				GeoElement topHit = hits.get(0);

				if (topHit.isGeoVector()) {

					if ((topHit.getParentAlgorithm() instanceof AlgoVector)) { // Vector[A,B]
						AlgoVector algo = (AlgoVector) topHit
								.getParentAlgorithm();
						GeoPoint2 p = algo.getInputPoints().get(0);
						GeoPoint2 q = algo.getInputPoints().get(1);
						GeoVector vec = kernel.Vector(null, 0, 0);
						vec.setEuclidianVisible(false);
						vec.setAuxiliaryObject(true);
						GeoElement[] pp = kernel.Translate(null, p, vec);
						GeoElement[] qq = kernel.Translate(null, q, vec);
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
					} else {// if (topHit.isIndependent()) {
						movedGeoPoint = new GeoPoint2(kernel.getConstruction(),
								null, 0, 0, 0);
						AlgoTranslate algoTP = new AlgoTranslate(
								kernel.getConstruction(), null,
								(GeoElement) movedGeoPoint, (GeoVec3D) topHit);
						GeoPoint2 p = (GeoPoint2) algoTP.getGeoElements()[0];

						AlgoVector newVecAlgo = new AlgoVector(kernel.getConstruction(), null,
								movedGeoPoint, p);
						
						// make sure vector looks the same when translated
						((GeoPoint2) movedGeoPoint).setEuclidianVisible(false);
						((GeoPoint2) movedGeoPoint).update();
						p.setEuclidianVisible(false);
						p.update();
						newVecAlgo.getGeoElements()[0].setVisualStyleForTransformations(topHit);
						
						moveMode = MOVE_POINT;
					}
				}

				if (topHit.isTranslateable() || topHit.isGeoPolygon()) {
					GeoVector vec;
					if (topHit.isGeoPolygon()) {
						// for polygons, we need a labelled vector so that all
						// the vertices move together
						vec = kernel.Vector(null, 0, 0);
						vec.setEuclidianVisible(false);
						vec.setAuxiliaryObject(true);
					} else {
						vec = kernel.Vector(0, 0);
					}
					kernel.Translate(null, hits.get(0), vec);
					transformCoordsOffset[0] = xRW;
					transformCoordsOffset[1] = yRW;

					((Application)app).setMode(EuclidianConstants.MODE_MOVE);
					movedGeoVector = vec;
					moveMode = MOVE_VECTOR_NO_GRID;
					return;
				}
			}

			// Michael Borcherds 2007-10-07 allow right mouse button to drag
			// points
			// mathieu : also if it's mode point, we can drag the point
			if (Application.isRightClick(event)
					|| (mode == EuclidianConstants.MODE_POINT)
					|| (mode == EuclidianConstants.MODE_COMPLEX_NUMBER)
					|| (mode == EuclidianConstants.MODE_POINT_ON_OBJECT)
					|| (mode == EuclidianConstants.MODE_SLIDER)
					|| (mode == EuclidianConstants.MODE_BUTTON_ACTION)
					|| (mode == EuclidianConstants.MODE_TEXTFIELD_ACTION)
					|| (mode == EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX)
					|| (mode == EuclidianConstants.MODE_TEXT)) {
				((EuclidianViewInterface) view).setHits(mouseLoc);

				// make sure slider tool drags only sliders, not other object
				// types
				if (mode == EuclidianConstants.MODE_SLIDER) {
					if (((EuclidianViewInterface) view).getHits().size() != 1) {
						return;
					}

					if (!(((EuclidianViewInterface) view).getHits().get(0) instanceof GeoNumeric)) {
						return;
					}
				} else if ((mode == EuclidianConstants.MODE_BUTTON_ACTION)
						|| (mode == EuclidianConstants.MODE_TEXTFIELD_ACTION)) {
					if (((EuclidianViewInterface) view).getHits().size() != 1) {
						return;
					}

					if (!(((EuclidianViewInterface) view).getHits().get(0) instanceof GeoButton)) {
						return;
					}
				} else if (mode == EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX) {
					if (((EuclidianViewInterface) view).getHits().size() != 1) {
						return;
					}

					if (!(((EuclidianViewInterface) view).getHits().get(0) instanceof GeoBoolean)) {
						return;
					}
				} else if (mode == EuclidianConstants.MODE_TEXT) {
					if (((EuclidianViewInterface) view).getHits().size() != 1) {
						return;
					}

					if (!(((EuclidianViewInterface) view).getHits().get(0) instanceof GeoText)) {
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
				app.addSelectedGeo(rotationCenter, false);
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

		if (Application.isRightClick(event)) {
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
			// zoom rectangle (right drag) or selection rectangle (left drag)
			// Michael Borcherds 2007-10-07 allow dragging with right mouse
			// button
			if (((Application.isRightClick(event)) || allowSelectionRectangle())
					&& !TEMPORARY_MODE) {
				// Michael Borcherds 2007-10-07
				// set zoom rectangle's size
				// right-drag: zoom
				// Shift-right-drag: zoom without preserving aspect ratio
				updateSelectionRectangle((Application.isRightClick(event) && !event
						.isShiftDown())
				// MACOS:
				// Cmd-left-drag: zoom
				// Cmd-shift-left-drag: zoom without preserving aspect ratio
						|| (Application.MAC_OS && Application.isControlDown(event)
								&& !event.isShiftDown() && !Application
									.isRightClick(event)));
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
				int mx = mouseLoc.x;
				int my = mouseLoc.y;

				// Application.debug("BIG drag dist: " + Math.sqrt(distsq) +
				// ", steps: " + steps );
				for (int i = 1; i <= steps; i++) {
					mouseLoc.x = (int) Math.round(lastMouseLoc.x + (i * dx));
					mouseLoc.y = (int) Math.round(lastMouseLoc.y + (i * dy));
					calcRWcoords();

					handleMouseDragged(false);
				}

				// set endpoint of mouse movement if we are not already there
				if ((mouseLoc.x != mx) || (mouseLoc.y != my)) {
					mouseLoc.x = mx;
					mouseLoc.y = my;
					calcRWcoords();
				}
			}
		}

		if (pastePreviewSelected != null) {
			if (!pastePreviewSelected.isEmpty()) {
				updatePastePreviewPosition();
			}
		}

		handleMouseDragged(true);
	}

	public void mouseDragged(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseDragged(event);
		/*sliderValue = null;

		if (textfieldHasFocus) {
			return;
		}

		if ((mode == EuclidianConstants.MODE_PEN)
				|| (mode == EuclidianConstants.MODE_FREEHAND)) {
			((EuclidianPen) pen).handleMousePressedForPenMode(e, null);
			return;
		}

		clearJustCreatedGeos();

		if (!DRAGGING_OCCURED) {

			DRAGGING_OCCURED = true;

			if ((mode == EuclidianConstants.MODE_TRANSLATE_BY_VECTOR)
					&& (selGeos() == 0)) {
				((EuclidianViewInterface) view).setHits(mouseLoc);

				Hits hits = ((EuclidianViewInterface) view).getHits().getTopHits();

				GeoElement topHit = hits.get(0);

				if (topHit.isGeoVector()) {

					if ((topHit.getParentAlgorithm() instanceof AlgoVector)) { // Vector[A,B]
						AlgoVector algo = (AlgoVector) topHit
								.getParentAlgorithm();
						GeoPoint2 p = algo.getInputPoints().get(0);
						GeoPoint2 q = algo.getInputPoints().get(1);
						GeoVector vec = kernel.Vector(null, 0, 0);
						vec.setEuclidianVisible(false);
						vec.setAuxiliaryObject(true);
						GeoElement[] pp = kernel.Translate(null, p, vec);
						GeoElement[] qq = kernel.Translate(null, q, vec);
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

						((Application)app).setMode(EuclidianConstants.MODE_MOVE);
						movedGeoVector = vec;
						moveMode = MOVE_VECTOR_NO_GRID;
						return;
					} else {// if (topHit.isIndependent()) {
						movedGeoPoint = new GeoPoint2(kernel.getConstruction(),
								null, 0, 0, 0);
						AlgoTranslate algoTP = new AlgoTranslate(
								kernel.getConstruction(), null,
								(GeoElement) movedGeoPoint, (GeoVec3D) topHit);
						GeoPoint2 p = (GeoPoint2) algoTP.getGeoElements()[0];

						AlgoVector newVecAlgo = new AlgoVector(kernel.getConstruction(), null,
								movedGeoPoint, p);
						
						// make sure vector looks the same when translated
						((GeoPoint2) movedGeoPoint).setEuclidianVisible(false);
						((GeoPoint2) movedGeoPoint).update();
						p.setEuclidianVisible(false);
						p.update();
						newVecAlgo.getGeoElements()[0].setVisualStyleForTransformations(topHit);
						
						moveMode = MOVE_POINT;
					}
				}

				if (topHit.isTranslateable() || topHit.isGeoPolygon()) {
					GeoVector vec;
					if (topHit.isGeoPolygon()) {
						// for polygons, we need a labelled vector so that all
						// the vertices move together
						vec = kernel.Vector(null, 0, 0);
						vec.setEuclidianVisible(false);
						vec.setAuxiliaryObject(true);
					} else {
						vec = kernel.Vector(0, 0);
					}
					kernel.Translate(null, hits.get(0), vec);
					transformCoordsOffset[0] = xRW;
					transformCoordsOffset[1] = yRW;

					((Application)app).setMode(EuclidianConstants.MODE_MOVE);
					movedGeoVector = vec;
					moveMode = MOVE_VECTOR_NO_GRID;
					return;
				}
			}

			// Michael Borcherds 2007-10-07 allow right mouse button to drag
			// points
			// mathieu : also if it's mode point, we can drag the point
			if (Application.isRightClick(e)
					|| (mode == EuclidianConstants.MODE_POINT)
					|| (mode == EuclidianConstants.MODE_COMPLEX_NUMBER)
					|| (mode == EuclidianConstants.MODE_POINT_ON_OBJECT)
					|| (mode == EuclidianConstants.MODE_SLIDER)
					|| (mode == EuclidianConstants.MODE_BUTTON_ACTION)
					|| (mode == EuclidianConstants.MODE_TEXTFIELD_ACTION)
					|| (mode == EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX)
					|| (mode == EuclidianConstants.MODE_TEXT)) {
				((EuclidianViewInterface) view).setHits(mouseLoc);

				// make sure slider tool drags only sliders, not other object
				// types
				if (mode == EuclidianConstants.MODE_SLIDER) {
					if (((EuclidianViewInterface) view).getHits().size() != 1) {
						return;
					}

					if (!(((EuclidianViewInterface) view).getHits().get(0) instanceof GeoNumeric)) {
						return;
					}
				} else if ((mode == EuclidianConstants.MODE_BUTTON_ACTION)
						|| (mode == EuclidianConstants.MODE_TEXTFIELD_ACTION)) {
					if (((EuclidianViewInterface) view).getHits().size() != 1) {
						return;
					}

					if (!(((EuclidianViewInterface) view).getHits().get(0) instanceof GeoButton)) {
						return;
					}
				} else if (mode == EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX) {
					if (((EuclidianViewInterface) view).getHits().size() != 1) {
						return;
					}

					if (!(((EuclidianViewInterface) view).getHits().get(0) instanceof GeoBoolean)) {
						return;
					}
				} else if (mode == EuclidianConstants.MODE_TEXT) {
					if (((EuclidianViewInterface) view).getHits().size() != 1) {
						return;
					}

					if (!(((EuclidianViewInterface) view).getHits().get(0) instanceof GeoText)) {
						return;
					}
				}

				if (viewHasHitsForMouseDragged()) {
					TEMPORARY_MODE = true;
					oldMode = mode; // remember current mode
					view.setMode(EuclidianConstants.MODE_MOVE);
					handleMousePressedForMoveMode(e, true);

					// make sure that dragging doesn't deselect the geos
					DONT_CLEAR_SELECTION = true;

					return;
				}

			}
			if (!((Application) app).isRightClickEnabled()) {
				return;
				// Michael Borcherds 2007-10-07
			}

			if (mode == EuclidianConstants.MODE_MOVE_ROTATE) {
				((Application)app).clearSelectedGeos(false);
				((Application)app).addSelectedGeo(rotationCenter, false);
			}
		}
		lastMouseLoc = mouseLoc;
		setMouseLocation(geogebra.euclidian.event.MouseEvent.wrapEvent(e));
		transformCoords();

		// ggb3D - only for 3D view
		if (moveMode == MOVE_ROTATE_VIEW) {
			if (processRotate3DView()) {
				return;
			}
		}

		if (Application.isRightClick(e)) {
			// if there's no hit, or if first hit is not moveable, do 3D view
			// rotation
			if ((!TEMPORARY_MODE)
					|| (((EuclidianViewInterface) view).getHits().size() == 0)
					|| !((EuclidianViewInterface) view).getHits().get(0).isMoveable(view)
					|| (!((EuclidianViewInterface) view).getHits().get(0).isGeoPoint() && ((EuclidianViewInterface) view).getHits()
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
		if (((EuclidianViewInterface) view).getHits().isEmpty()) {
			// zoom rectangle (right drag) or selection rectangle (left drag)
			// Michael Borcherds 2007-10-07 allow dragging with right mouse
			// button
			if (((Application.isRightClick(e)) || allowSelectionRectangle())
					&& !TEMPORARY_MODE) {
				// Michael Borcherds 2007-10-07
				// set zoom rectangle's size
				// right-drag: zoom
				// Shift-right-drag: zoom without preserving aspect ratio
				updateSelectionRectangle((Application.isRightClick(e) && !e
						.isShiftDown())
				// MACOS:
				// Cmd-left-drag: zoom
				// Cmd-shift-left-drag: zoom without preserving aspect ratio
						|| (Application.MAC_OS && Application.isControlDown(e)
								&& !e.isShiftDown() && !Application
									.isRightClick(e)));
				view.repaintView();
				return;
			}
		}

		// update previewable
		if (((EuclidianViewInterface) view).getPreviewDrawable() != null) {
			((EuclidianViewInterface) view).getPreviewDrawable().updateMousePos(
					view.toRealWorldCoordX(mouseLoc.x),
					view.toRealWorldCoordY(mouseLoc.y));
		}

		/*
		 * Conintuity handling
		 * 
		 * If the mouse is moved wildly we take intermediate steps to get a more
		 * continous behaviour
		 */
		/*
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
				int mx = mouseLoc.x;
				int my = mouseLoc.y;

				// Application.debug("BIG drag dist: " + Math.sqrt(distsq) +
				// ", steps: " + steps );
				for (int i = 1; i <= steps; i++) {
					mouseLoc.x = (int) Math.round(lastMouseLoc.x + (i * dx));
					mouseLoc.y = (int) Math.round(lastMouseLoc.y + (i * dy));
					calcRWcoords();

					handleMouseDragged(false);
				}

				// set endpoint of mouse movement if we are not already there
				if ((mouseLoc.x != mx) || (mouseLoc.y != my)) {
					mouseLoc.x = mx;
					mouseLoc.y = my;
					calcRWcoords();
				}
			}
		}

		if (pastePreviewSelected != null) {
			if (!pastePreviewSelected.isEmpty()) {
				updatePastePreviewPosition();
			}
		}

		handleMouseDragged(true);*/
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
			GeoElementSelectionListener sel = ((Application) app).getCurrentSelectionListener();
			if (sel == null) {
				return false;
			}
			if (((Application)app).isUsingFullGui()) {
				return !((Application) app).getGuiManager().isInputFieldSelectionListener();
			} else {
				return sel != null;
			}

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
			movePoint(repaint);
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
			moveDependent(repaint);
			break;

		case MOVE_MULTIPLE_OBJECTS:
			moveMultipleObjects(repaint);
			break;

		case MOVE_VIEW:
			if (repaint) {
				if (TEMPORARY_MODE) {
					((EuclidianViewInterface) view).setMoveCursor();
				}
				/*
				 * view.setCoordSystem(xZeroOld + mouseLoc.x - startLoc.x,
				 * yZeroOld + mouseLoc.y - startLoc.y, view.getXscale(),
				 * view.getYscale());
				 */
				((EuclidianViewInterface) view).setCoordSystemFromMouseMove(mouseLoc.x - startLoc.x,
						mouseLoc.y - startLoc.y, MOVE_VIEW);
			}
			break;

		case MOVE_X_AXIS:
			if (repaint) {
				if (TEMPORARY_MODE) {
					((EuclidianViewInterface) view).setResizeXAxisCursor();
				}

				// take care when we get close to the origin
				if (Math.abs(mouseLoc.x - ((EuclidianViewInterface) view).getXZero()) < 2) {
					mouseLoc.x = (int) Math
							.round(mouseLoc.x > ((EuclidianViewInterface) view).getXZero() ? ((EuclidianViewInterface) view)
									.getXZero() + 2 : ((EuclidianViewInterface) view).getXZero() - 2);
				}
				double xscale = (mouseLoc.x - ((EuclidianViewInterface) view).getXZero()) / xTemp;
				((EuclidianViewInterface) view).setCoordSystem(((EuclidianViewInterface) view).getXZero(), ((EuclidianViewInterface) view).getYZero(), xscale,
						view.getYscale());
			}
			break;

		case MOVE_Y_AXIS:
			if (repaint) {
				if (TEMPORARY_MODE) {
					((EuclidianViewInterface) view).setResizeYAxisCursor();
				}
				// take care when we get close to the origin
				if (Math.abs(mouseLoc.y - ((EuclidianViewInterface) view).getYZero()) < 2) {
					mouseLoc.y = (int) Math
							.round(mouseLoc.y > ((EuclidianViewInterface) view).getYZero() ? ((EuclidianViewInterface) view)
									.getYZero() + 2 : ((EuclidianViewInterface) view).getYZero() - 2);
				}
				double yscale = (((EuclidianViewInterface) view).getYZero() - mouseLoc.y) / yTemp;
				((EuclidianViewInterface) view).setCoordSystem(((EuclidianViewInterface) view).getXZero(), ((EuclidianViewInterface) view).getYZero(),
						view.getXscale(), yscale);
			}
			break;

		default: // do nothing
		}
	}

	protected void updateSelectionRectangle(boolean keepScreenRatio) {
		if (((EuclidianViewInterface) view).getSelectionRectangle() == null) {
			((EuclidianViewInterface) view).setSelectionRectangle(geogebra.common.factories.AwtFactory.prototype.newRectangle(0,0));
		}

		int dx = mouseLoc.x - selectionStartPoint.x;
		int dy = mouseLoc.y - selectionStartPoint.y;
		int dxabs = Math.abs(dx);
		int dyabs = Math.abs(dy);

		int width = dx;
		int height = dy;

		// the zoom rectangle should have the same aspect ratio as the view
		if (keepScreenRatio) {
			double ratio = (double) ((EuclidianViewInterface) view).getViewWidth()
					/ (double) ((EuclidianViewInterface) view).getViewHeight();
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

		Rectangle rect = ((EuclidianViewInterface) view).getSelectionRectangle();
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

	// used for 3D
	protected void processReleaseForMovedGeoPoint(AbstractEvent event) {

		// deselect point after drag, but not on click
		// outdated - we want to leave the point selected after drag now
		// if (movedGeoPointDragged) getMovedGeoPoint().setSelected(false);

		if ((mode != EuclidianConstants.MODE_RECORD_TO_SPREADSHEET)
				&& ((Application)app).isUsingFullGui()) {
			getMovedGeoPoint().resetTraceColumns();
		}

	}

	public void showDrawingPadPopup(Point mouseLoc) {
		((Application) app).getGuiManager().showDrawingPadPopup(((EuclidianViewInterface) view).getJPanel(), mouseLoc);
	}
	
	protected void wrapMouseReleased(AbstractEvent event) {
		
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

		if ((mode == EuclidianConstants.MODE_PEN)
				|| (mode == EuclidianConstants.MODE_FREEHAND)) {
			((EuclidianPen) pen).handleMouseReleasedForPenMode(event);
			return;
		}

		boolean changedKernel0 = false;
		if (pastePreviewSelected != null) {

			mergeStickyPointsAfterPaste();

			// add moved points to sticky points again
			for (int i = 0; i < pastePreviewSelectedAndDependent.size(); i++) {
				GeoElement geo = pastePreviewSelectedAndDependent.get(i);
				if (geo.isGeoPoint()) {
					if (!((EuclidianViewInterface) view).getStickyPointList().contains(geo)) {
						((EuclidianViewInterface) view).getStickyPointList().add((GeoPointND) geo);
					}
				}
			}
			persistentStickyPointList = new ArrayList<GeoPointND>();

			pastePreviewSelected = null;
			pastePreviewSelectedAndDependent = null;
			((EuclidianViewInterface) view).setPointCapturing(previousPointCapturing);
			changedKernel0 = true;
			((Application)app).getKernel().getConstruction().getUndoManager()
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
					&& ((Application)app).isUsingFullGui()) {
				movedGeoNumeric.resetTraceColumns();
			}
		}

		movedGeoPointDragged = false;
		movedGeoNumericDragged = false;

		((EuclidianViewInterface) view).requestFocusInWindow();
		setMouseLocation(event);

		setAltDown(event.isAltDown());

		transformCoords();
		Hits hits = null;
		GeoElement geo;

		if (hitResetIcon()) {
			((Application) app).reset();
			return;
		} else if (view.hitAnimationButton(event)) {
			if (kernel.isAnimationRunning()) {
				kernel.getAnimatonManager().stopAnimation();
			} else {
				kernel.getAnimatonManager().startAnimation();
			}
			view.repaintView();
			((Application)app).setUnsaved();
			return;
		}

		// Michael Borcherds 2007-10-08 allow drag with right mouse button
		if ((Application.isRightClick(event) || Application.isControlDown(event)))// &&
																			// !TEMPORARY_MODE)
		{
			if (processRightReleaseFor3D()) {
				return;
			}
			if (!TEMPORARY_MODE) {
				if (!((Application) app).isRightClickEnabled()) {
					return;
				}
				if (processZoomRectangle()) {
					return;
					// Michael Borcherds 2007-10-08
				}

				// make sure cmd-click selects multiple points (not open
				// properties)
				if ((Application.MAC_OS && Application.isControlDown(event))
						|| !Application.isRightClick(event)) {
					return;
				}

				// get selected GeoElements
				// show popup menu after right click
				view.setHits(mouseLoc);
				hits = view.getHits().getTopHits();
				if (hits.isEmpty()) {
					// no hits
					if (app.selectedGeosSize() > 0) {
						// GeoElement selGeo = (GeoElement)
						// app.getSelectedGeos().get(0);
						app.getGuiManager().showPopupMenu(
								app.getSelectedGeos(),  view, mouseLoc);
					} else {
						showDrawingPadPopup(mouseLoc);
					}
				} else {
					// there are hits
					if (app.selectedGeosSize() > 0) {

						// right click on already selected geos -> show menu for
						// them
						// right click on object(s) not selected -> clear
						// selection
						// and show menu just for new objects
						if (!app.getSelectedGeos().contains(hits.get(0))) {
							app.clearSelectedGeos();
							app.addSelectedGeos(hits, true);
						} else {
							app.addSelectedGeo(hits.get(0));
						}

						app.getGuiManager().showPopupMenu(
								app.getSelectedGeos(), view, mouseLoc);
					} else {
						// no selected geos: choose geo and show popup menu
						geo = chooseGeo(hits, false);
						if (geo != null) {
							ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
							geos.add(geo);
							app.getGuiManager().showPopupMenu(geos,
									view, mouseLoc);
						} else {
							// for 3D : if the geo hitted is xOyPlane, then
							// chooseGeo return null
							// app.getGuiManager().showDrawingPadPopup((EuclidianView)
							// view, mouseLoc);
							showDrawingPadPopup(mouseLoc);
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
			refreshHighlighting(null);
		}

		// reinit vars
		// view.setDrawMode(EuclidianConstants.DRAW_MODE_BACKGROUND_IMAGE);
		moveMode = MOVE_NONE;
		initShowMouseCoords();
		((EuclidianViewInterface) view).setShowAxesRatio(false);
		kernel.notifyRepaint();
	}

	public void mouseReleased(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseReleased(event);
		/*sliderValue = null;

		if (e != null) {
			mx = e.getX();
			my = e.getY();
		}
		// reset
		transformCoordsOffset[0] = 0;
		transformCoordsOffset[1] = 0;

		if (textfieldHasFocus) {
			return;
		}

		if ((mode == EuclidianConstants.MODE_PEN)
				|| (mode == EuclidianConstants.MODE_FREEHAND)) {
			((EuclidianPen) pen).handleMouseReleasedForPenMode(e);
			return;
		}

		boolean changedKernel0 = false;
		if (pastePreviewSelected != null) {

			mergeStickyPointsAfterPaste();

			// add moved points to sticky points again
			for (int i = 0; i < pastePreviewSelectedAndDependent.size(); i++) {
				GeoElement geo = pastePreviewSelectedAndDependent.get(i);
				if (geo.isGeoPoint()) {
					if (!((EuclidianViewInterface) view).getStickyPointList().contains(geo)) {
						((EuclidianViewInterface) view).getStickyPointList().add((GeoPointND) geo);
					}
				}
			}
			persistentStickyPointList = new ArrayList<GeoPointND>();

			pastePreviewSelected = null;
			pastePreviewSelectedAndDependent = null;
			((EuclidianViewInterface) view).setPointCapturing(previousPointCapturing);
			changedKernel0 = true;
			((Application)app).getKernel().getConstruction().getUndoManager()
					.storeUndoInfoAfterPasteOrAdd();
		}

		// if (mode != EuclidianConstants.MODE_RECORD_TO_SPREADSHEET)
		// view.resetTraceRow(); // for trace/spreadsheet
		if (getMovedGeoPoint() != null) {

			processReleaseForMovedGeoPoint(e);
			/*
			 * // deselect point after drag, but not on click if
			 * (movedGeoPointDragged) getMovedGeoPoint().setSelected(false);
			 * 
			 * if (mode != EuclidianConstants.MODE_RECORD_TO_SPREADSHEET)
			 * getMovedGeoPoint().resetTraceColumns();
			 */
//		}
//		if (movedGeoNumeric != null) {

			// deselect slider after drag, but not on click
			// if (movedGeoNumericDragged) movedGeoNumeric.setSelected(false);

//			if ((mode != EuclidianConstants.MODE_RECORD_TO_SPREADSHEET)
//					&& ((Application)app).isUsingFullGui()) {
//				movedGeoNumeric.resetTraceColumns();
//			}
//		}

//		movedGeoPointDragged = false;
//		movedGeoNumericDragged = false;

//		((EuclidianViewInterface) view).requestFocusInWindow();
//		setMouseLocation(geogebra.euclidian.event.MouseEvent.wrapEvent(e));

//		setAltDown(e.isAltDown());

//		transformCoords();
//		Hits hits = null;
//		GeoElement geo;

//		if (hitResetIcon()) {
//			((Application) app).reset();
//			return;
//		} else if (((EuclidianViewInterface) view).hitAnimationButton(e)) {
//			if (kernel.isAnimationRunning()) {
//				kernel.getAnimatonManager().stopAnimation();
//			} else {
//				kernel.getAnimatonManager().startAnimation();
//			}
//			view.repaintView();
//			((Application)app).setUnsaved();
//			return;
//		}

		// Michael Borcherds 2007-10-08 allow drag with right mouse button
//		if ((Application.isRightClick(e) || Application.isControlDown(e)))// &&
																			// !TEMPORARY_MODE)
//		{
//			if (processRightReleaseFor3D()) {
//				return;
//			}
//			if (!TEMPORARY_MODE) {
//				if (!((Application) app).isRightClickEnabled()) {
//					return;
//				}
//				if (processZoomRectangle()) {
//					return;
					// Michael Borcherds 2007-10-08
//				}

				// make sure cmd-click selects multiple points (not open
				// properties)
//				if ((Application.MAC_OS && Application.isControlDown(e))
//						|| !Application.isRightClick(e)) {
//					return;
//				}

				// get selected GeoElements
				// show popup menu after right click
//				((EuclidianViewInterface) view).setHits(mouseLoc);
//				hits = ((EuclidianViewInterface) view).getHits().getTopHits();
//				if (hits.isEmpty()) {
					// no hits
//					if (((Application) app).selectedGeosSize() > 0) {
						// GeoElement selGeo = (GeoElement)
						// app.getSelectedGeos().get(0);
//						((Application) app).getGuiManager().showPopupMenu(
//								((Application)app).getSelectedGeos(),  ((EuclidianViewInterface) view).getJPanel(), mouseLoc);
//					} else {
//						showDrawingPadPopup(mouseLoc);
//					}
//				} else {
					// there are hits
//					if (((Application) app).selectedGeosSize() > 0) {

						// right click on already selected geos -> show menu for
						// them
						// right click on object(s) not selected -> clear
						// selection
						// and show menu just for new objects
//						if (!((Application)app).getSelectedGeos().contains(hits.get(0))) {
//							((Application)app).clearSelectedGeos();
//							((Application)app).addSelectedGeos(hits, true);
//						} else {
//							((Application)app).addSelectedGeo(hits.get(0));
//						}

//						((Application) app).getGuiManager().showPopupMenu(
//								((Application)app).getSelectedGeos(), ((EuclidianViewInterface) view).getJPanel(), mouseLoc);
//					} else {
						// no selected geos: choose geo and show popup menu
//						geo = chooseGeo(hits, false);
//						if (geo != null) {
//							ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
//							geos.add(geo);
//							((Application) app).getGuiManager().showPopupMenu(geos,
//									((EuclidianViewInterface) view).getJPanel(), mouseLoc);
//						} else {
							// for 3D : if the geo hitted is xOyPlane, then
							// chooseGeo return null
							// app.getGuiManager().showDrawingPadPopup((EuclidianView)
							// view, mouseLoc);
//							showDrawingPadPopup(mouseLoc);
//						}
//					}
//				}
//				return;
//			}
//		}

		// handle moving
//		boolean changedKernel = false;
//		if (DRAGGING_OCCURED) {

//			DRAGGING_OCCURED = false;
			// // copy value into input bar
			// if (mode == EuclidianView.MODE_MOVE && movedGeoElement != null) {
			// app.geoElementSelected(movedGeoElement,false);
			// }

			// check movedGeoElement.isLabelSet() to stop moving points
			// in Probability Calculator triggering Undo
//			changedKernel = ((movedGeoElement != null) && movedGeoElement
//					.isLabelSet()) && (moveMode != MOVE_NONE);
//			movedGeoElement = null;
//			rotGeoElement = null;

			// Michael Borcherds 2007-10-08 allow dragging with right mouse
			// button
//			if (!TEMPORARY_MODE) {
				// Michael Borcherds 2007-10-08
//				if (allowSelectionRectangle()) {
//					processSelectionRectangle(e);

//					return;
//				}
//			}
//		} else {
			// no hits: release mouse button creates a point
			// for the transformation tools
			// (note: this cannot be done in mousePressed because
			// we want to be able to select multiple objects using the selection
			// rectangle)

//			changedKernel = switchModeForMouseReleased(mode, hits,
//					changedKernel);
//		}

		// remember helper point, see createNewPoint()
//		if (changedKernel && !changedKernel0) {
//			((Application)app).storeUndoInfo();
//		}

		// make sure that when alt is pressed for creating a segment or line
		// it works if the endpoint is on a path
//		if (useLineEndPoint && (lineEndPoint != null)) {
//			mouseLoc.x = ((EuclidianViewInterface) view).toScreenCoordX(lineEndPoint.x);
//			mouseLoc.y = ((EuclidianViewInterface) view).toScreenCoordY(lineEndPoint.y);
//			useLineEndPoint = false;
//		}

		// now handle current mode
//		((EuclidianViewInterface) view).setHits(mouseLoc);
//		hits = ((EuclidianViewInterface) view).getHits();
//		switchModeForRemovePolygons(hits);
		// Application.debug(mode + "\n" + hits.toString());

		// Michael Borcherds 2007-12-08 BEGIN moved up a few lines (bugfix:
		// Tools eg Line Segment weren't working with grid on)
		// grid capturing on: newly created point should be taken
		// Application.debug("POINT_CREATED="+POINT_CREATED+"\nhits=\n"+hits+"\ngetMovedGeoPoint()="+getMovedGeoPoint());
//		if (POINT_CREATED) {
//			hits = addPointCreatedForMouseReleased(hits);
//		}
//		POINT_CREATED = false;
		// Michael Borcherds 2007-12-08 END

//		if (TEMPORARY_MODE) {

			// Michael Borcherds 2007-10-13 BEGIN
//			view.setMode(oldMode);
//			TEMPORARY_MODE = false;
			// Michael Borcherds 2007-12-08 BEGIN bugfix: couldn't select
			// multiple points with Ctrl
//			if (DONT_CLEAR_SELECTION == false) {
//				clearSelections();
//			}
//			DONT_CLEAR_SELECTION = false;
			// Michael Borcherds 2007-12-08 END
			// mode = oldMode;
			// Michael Borcherds 2007-10-13 END
//		}
		// Michael Borcherds 2007-10-12 bugfix: ctrl-click on a point does the
		// original mode's command at end of drag if a point was clicked on
		// also needed for right-drag
//		else {
//			if (mode != EuclidianConstants.MODE_RECORD_TO_SPREADSHEET) {
//				changedKernel = processMode(hits, e);
//			}
//			if (changedKernel) {
//				((Application)app).storeUndoInfo();
//			}
//		}
		// Michael Borcherds 2007-10-12

		// Michael Borcherds 2007-10-12
		// moved up a few lines
		// changedKernel = processMode(hits, e);
		// if (changedKernel)
		// app.storeUndoInfo();
		// Michael Borcherds 2007-10-12

//		if (!hits.isEmpty()) {
			// Application.debug("hits ="+hits);
//			((EuclidianViewInterface) view).setDefaultCursor();
//		} else {
//			((EuclidianViewInterface) view).setHitCursor();
//		}

//		if ((mode == EuclidianConstants.MODE_RECORD_TO_SPREADSHEET)
//				&& (recordObject != null)) {
//			clearSelections();
//		} else {
			// this is in the else branch to avoid running it twice
//			refreshHighlighting(null);
//		}

		// reinit vars
		// view.setDrawMode(EuclidianConstants.DRAW_MODE_BACKGROUND_IMAGE);
//		moveMode = MOVE_NONE;
//		initShowMouseCoords();
//		((EuclidianViewInterface) view).setShowAxesRatio(false);
//		kernel.notifyRepaint();*/
	}

	protected Hits addPointCreatedForMouseReleased(Hits hits) {

		if (hits.isEmpty()) {
			hits = new Hits();
			hits.add(getMovedGeoPoint());
		}

		return hits;
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

	protected boolean switchModeForMouseReleased(int mode, Hits hits,
			boolean changedKernel) {
		switch (mode) {
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
		case EuclidianConstants.MODE_DILATE_FROM_POINT:
		case EuclidianConstants.MODE_MIRROR_AT_POINT:
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds
														// 2008-03-23
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			((EuclidianViewInterface) view).setHits(mouseLoc);
			hits = ((EuclidianViewInterface) view).getHits();
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
			((EuclidianViewInterface) view).setHits(mouseLoc);
			hits = ((EuclidianViewInterface) view).getHits().getTopHits();
			// hits = view.getTopHits(mouseLoc);
			if (!hits.isEmpty()) {
				GeoElement hit = hits.get(0);
				if ((hit != null) && hit.isGeoBoolean()) {
					GeoBoolean bool = (GeoBoolean) (hits.get(0));
					if (!bool.isCheckboxFixed()) { // otherwise changed on mouse
													// down
						bool.setValue(!bool.getBoolean());
						((Application)app).removeSelectedGeo(bool); // make sure doesn't get
														// selected
						bool.updateCascade();
					}
				} else if (hit != null) {
					GeoElement geo1 = chooseGeo(hits, true);
					// ggb3D : geo1 may be null if it's axes or xOy plane
					if (geo1 != null) {
						geo1.runScripts(null);
					}
					if (((Application) app).hasPythonBridge()) {
						((Application) app).getPythonBridge().click(geo1);
					}
				}
			}
		}

		return changedKernel;
	}

	protected boolean hitResetIcon() {
		return ((Application) app).showResetIcon()
				&& ((mouseLoc.y < 18) && (mouseLoc.x > (((EuclidianViewInterface) view).getViewWidth() - 18)));
	}

	// return if we really did zoom
	protected boolean processZoomRectangle() {
		Rectangle rect = ((EuclidianViewInterface) view).getSelectionRectangle();
		if (rect == null) {
			return false;
		}

		if ((rect.width < 30) || (rect.height < 30)
				|| !((Application) app).isShiftDragZoomEnabled() // Michael Borcherds 2007-12-11
		) {
			((EuclidianViewInterface) view).setSelectionRectangle(null);
			view.repaintView();
			return false;
		}

		((EuclidianViewInterface) view).resetMode();
		// zoom zoomRectangle to EuclidianView's size
		// double factor = (double) view.width / (double) rect.width;
		// Point p = rect.getLocation();
		((EuclidianViewInterface) view).setSelectionRectangle(null);
		// view.setAnimatedCoordSystem((view.xZero - p.x) * factor,
		// (view.yZero - p.y) * factor, view.xscale * factor, 15, true);

		// zoom without (necessarily) preserving the aspect ratio
		((EuclidianViewInterface) view).setAnimatedRealWorldCoordSystem(
				view.toRealWorldCoordX(rect.getMinX()),
				view.toRealWorldCoordX(rect.getMaxX()),
				view.toRealWorldCoordY(rect.getMaxY()),
				view.toRealWorldCoordY(rect.getMinY()), 15, true);
		return true;
	}

	// select all geos in selection rectangle
	protected void processSelectionRectangle(AbstractEvent e) {
		clearSelections();
		((EuclidianViewInterface) view).setHits(((EuclidianViewInterface) view).getSelectionRectangle());
		Hits hits = ((EuclidianViewInterface) view).getHits();

		switch (mode) {
		case EuclidianConstants.MODE_SELECTION_LISTENER:
			// tell properties dialog
			if ((hits.size() > 0)
					&& ((Application)app).isUsingFullGui()
					&& ((Application) app).getGuiManager()
							.isPropertiesDialogSelectionListener()) {
				GeoElement geo = hits.get(0);
				((Application) app).geoElementSelected(geo, false);
				for (int i = 1; i < hits.size(); i++) {
					((Application) app).geoElementSelected(hits.get(i), true);
				}
			}
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
			((Application) app).setSelectedGeos(hits);
			processMode(hits, e);
			((EuclidianViewInterface) view).setSelectionRectangle(null);
			break;

		case EuclidianConstants.MODE_FITLINE:

			// check for list first
			if (hits.size() == 1) {
				if (hits.get(0).isGeoList()) {
					selectedGeos.addAll(hits);
					((Application) app).setSelectedGeos(hits);
					processMode(hits, e);
					((EuclidianViewInterface) view).setSelectionRectangle(null);
					break;
				}
			}

			// remove non-Points
			for (int i = 0; i < hits.size(); i++) {
				GeoElement geo = hits.get(i);
				if (!(GeoPoint2.class.isInstance(geo))) {
					hits.remove(i);
				}
			}

			// Fit line makes sense only for more than 2 points (or one list)
			if (hits.size() < 3) {
				hits.clear();
			} else {
				removeParentPoints(hits);
				selectedGeos.addAll(hits);
				((Application) app).setSelectedGeos(hits);
				processMode(hits, e);
				((EuclidianViewInterface) view).setSelectionRectangle(null);
			}
			break;

		default:
			// STANDARD CASE
			((Application) app).setSelectedGeos(hits);

			// if alt pressed, create list of objects as string and copy to
			// input bar
			if ((hits != null) && (hits.size() > 0) && (e != null)
					&& e.isAltDown() && ((Application)app).isUsingFullGui()
					&& ((Application) app).showAlgebraInput()) {

				JTextComponent textComponent = ((Application) app).getGuiManager()
						.getAlgebraInputTextField();

				StringBuilder sb = new StringBuilder();
				sb.append(" {");
				for (int i = 0; i < hits.size(); i++) {
					sb.append(hits.get(i).getLabel());
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

		kernel.notifyRepaint();
	}

	// TODO replace Class<?> by Class<GeoElement> ?
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
		((Application) app).setSelectedGeos(hits);
	}

	protected void processSelection() {
		Hits hits = new Hits();
		hits.addAll(((Application)app).getSelectedGeos());
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
				if (!(GeoPoint2.class.isInstance(geo))) {
					hits.remove(i);
				}
			}
			// Fit line makes sense only for more than 2 points
			if (hits.size() < 3) {
				hits.clear();
			} else {
				removeParentPoints(hits);
				selectedGeos.addAll(hits);
				((Application) app).setSelectedGeos(hits);
				processMode(hits, null);
				((EuclidianViewInterface) view).setSelectionRectangle(null);
			}
			break;

		default:
			break;
		}

		kernel.notifyRepaint();
	}

	public void mouseMoved(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseMoved(event);
		/*if (textfieldHasFocus) {
			return;
		}

		setMouseLocation(e);
		processMouseMoved(e);*/
	}
	
	protected void wrapMouseMoved(AbstractEvent event) {
		
		if (textfieldHasFocus) {
			return;
		}
		
		setMouseLocation(event);
		processMouseMoved(event);
		//event.release(e.getID()); //does it necessary?
		
	}

	protected void processMouseMoved(AbstractEvent event) {

		boolean repaintNeeded;

		// reset icon
		if (hitResetIcon()) {
			((EuclidianViewInterface) view).setToolTipText(((Application) app).getPlainTooltip("resetConstruction"));
			((EuclidianViewInterface) view).setHitCursor();
			return;
		}

		// animation button
		boolean hitAnimationButton = view.hitAnimationButton(event);
		repaintNeeded = ((EuclidianViewInterface) view).setAnimationButtonsHighlighted(hitAnimationButton);
		if (hitAnimationButton) {
			if (kernel.isAnimationPaused()) {
				((EuclidianViewInterface) view).setToolTipText(((Application) app).getPlainTooltip("Play"));
			} else {
				((EuclidianViewInterface) view).setToolTipText(((Application) app).getPlainTooltip("Pause"));
			}
			((EuclidianViewInterface) view).setHitCursor();
			view.repaintView();
			return;
		}

		// standard handling
		Hits hits = new Hits();
		boolean noHighlighting = false;
		setAltDown(event.isAltDown());

		// label hit
		GeoElement geo = ((EuclidianViewInterface) view).getLabelHit(mouseLoc);
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
			((EuclidianViewInterface) view).setHits(mouseLoc);
			hits = ((EuclidianViewInterface) view).getHits();
			switchModeForRemovePolygons(hits);
		}

		if (hits.isEmpty()) {
			((EuclidianViewInterface) view).setToolTipText(null);
			((EuclidianViewInterface) view).setDefaultCursor();
		} else {
			if (event.isShiftDown() && (hits.size() == 1)
					&& (hits.get(0) instanceof GeoAxis)) {
				if (((GeoAxis) hits.get(0)).getType() == GeoAxisND.X_AXIS) {
					((EuclidianViewInterface) view).setResizeXAxisCursor();
				} else {
					((EuclidianViewInterface) view).setResizeYAxisCursor();
				}
			} else {
				((EuclidianViewInterface) view).setHitCursor();
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

					// preview just for fixed sliders
					if (((GeoNumeric) hit).isSliderFixed()) {
						sliderValue = kernel
								.format(getSliderValue((GeoNumeric) hit));
					}
				}
			}
		}

		if (!hits.isEmpty()) {
			boolean alwaysOn = false;
			if (view instanceof EuclidianView) {
				if ( ((EuclidianViewInterface) view).getAllowToolTips() == EuclidianStyleConstants.TOOLTIPS_ON) {
					alwaysOn = true;
				}
			}
			String text = GeoElement.getToolTipDescriptionHTML(hits, true,
					true, alwaysOn);
			((EuclidianViewInterface) view).setToolTipText(text);
		} else {
			((EuclidianViewInterface) view).setToolTipText(null);
			// }
		}

		// update previewable
		if (((EuclidianViewInterface) view).getPreviewDrawable() != null) {
			((EuclidianViewInterface) view).updatePreviewable();
			repaintNeeded = true;
		}

		if ((pastePreviewSelected != null) && !pastePreviewSelected.isEmpty()) {
			transformCoords();
			updatePastePreviewPosition();
			repaintNeeded = true;
		}

		// show Mouse coordinates, manage alt -> multiple of 15 degrees
		else if (((EuclidianViewInterface) view).getShowMouseCoords() && ((EuclidianViewInterface) view).getAllowShowMouseCoords()) {
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

		repaintNeeded = noHighlighting ? refreshHighlighting(null)
				: refreshHighlighting(tempFullHits) || repaintNeeded;
		if (repaintNeeded) {
			kernel.notifyRepaint();
		}
	}

	// mode specific highlighting of selectable objects
	// returns wheter repaint is necessary
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

	protected boolean switchModeForProcessMode(Hits hits, AbstractEvent event) {

		Boolean changedKernel = false;
		GeoElement[] ret = null;

		switch (mode) {
		case EuclidianConstants.MODE_VISUAL_STYLE:
		case EuclidianConstants.MODE_MOVE:
			// move() is for highlighting and selecting
			if (selectionPreview) {
				move(hits.getTopHits());
			} else {
				if (DRAGGING_OCCURED && (((Application) app).selectedGeosSize() == 1)) {
					((Application)app).clearSelectedGeos();
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
					&& (Application.isControlDown(event));
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
					hits.getOtherHits(Test.GEOIMAGE, tempArrayList), mode,
					isAltDown()); // e.isAltDown());
			break;

		// new image
		case EuclidianConstants.MODE_IMAGE:
			changedKernel = image(
					hits.getOtherHits(Test.GEOIMAGE, tempArrayList), mode,
					isAltDown()); // e.isAltDown());
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
			changedKernel = attachDetach(hits.getTopHits(), event);
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
			changedKernel = showCheckBox(hits);
			break;

		case EuclidianConstants.MODE_BUTTON_ACTION:
			changedKernel = button(false);
			break;

		case EuclidianConstants.MODE_TEXTFIELD_ACTION:
			changedKernel = button(true);
			break;

		case EuclidianConstants.MODE_PEN:
		case EuclidianConstants.MODE_FREEHAND:
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

	// process mode and return whether kernel was changed
	public final boolean processMode(Hits hits, AbstractEvent event) {
		boolean changedKernel = false;

		if (hits == null) {
			hits = new Hits();
		}

		changedKernel = switchModeForProcessMode(hits, event);

		// update preview
		if (((EuclidianViewInterface) view).getPreviewDrawable() != null) {
			((EuclidianViewInterface) view).getPreviewDrawable().updatePreview();
			if (mouseLoc != null) {
				xRW = view.toRealWorldCoordX(mouseLoc.x);
				yRW = view.toRealWorldCoordY(mouseLoc.y);

				processModeLock();

				((EuclidianViewInterface) view).getPreviewDrawable().updateMousePos(xRW, yRW);
			}
			view.repaintView();
		}

		return changedKernel;
	}

	public void processModeLock() {

		// make previewable "lock" onto points & paths
		// priority for highlighted geos (points)
		Hits getTopHits = highlightedGeos.getTopHits();
		// nothing highlighted, look at eg circles, lines
		if (getTopHits.size() == 0) {
			getTopHits = ((EuclidianViewInterface) view).getHits().getTopHits();
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
			transformCoords(); // grid lock
		}
	}

	protected void processModeLock(Path path) {
		GeoPoint2 p = kernel.Point(null, path, xRW, yRW, false, false);
		p.update();
		xRW = p.inhomX;
		yRW = p.inhomY;
	}

	protected void processModeLock(GeoPointND point) {
		Coords coords = point.getInhomCoordsInD(2);
		xRW = coords.getX();
		yRW = coords.getY();
	}
	
	
	
	public void mouseEntered(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseEntered(event);
		/*if (textfieldHasFocus) {
			return;
		}

		initToolTipManager();
		initShowMouseCoords();
		((EuclidianViewInterface) view).mouseEntered();*/
	}
	
	protected void wrapMouseExited(AbstractEvent event) {
		if (textfieldHasFocus) {
			return;
		}
			
		refreshHighlighting(null);
		resetToolTipManager();
		view.setAnimationButtonsHighlighted(false);
		view.setShowMouseCoords(false);
		mouseLoc = null;
		view.repaintView();
		view.mouseExited();
		
	}

	public void mouseExited(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseExited(event);
		/*if (textfieldHasFocus) {
			return;
		}

		refreshHighlighting(null);
		resetToolTipManager();
		((EuclidianViewInterface) view).setAnimationButtonsHighlighted(false);
		((EuclidianViewInterface) view).setShowMouseCoords(false);
		mouseLoc = null;
		view.repaintView();
		((EuclidianViewInterface) view).mouseExited();*/
	}

	/*
	 * public void focusGained(FocusEvent e) { initToolTipManager(); }
	 * 
	 * public void focusLost(FocusEvent e) { resetToolTipManager(); }
	 */

	public void initToolTipManager() {
		// set tooltip manager
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(DEFAULT_INITIAL_DELAY / 2);
		ttm.setEnabled(((Application) app).getAllowToolTips());
	}

	protected void resetToolTipManager() {
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(DEFAULT_INITIAL_DELAY);
	}

	/* ****************************************************** */

	final protected void rotateObject(boolean repaint) {
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

	final protected void moveLabel() {
		movedLabelGeoElement.setLabelOffset((oldLoc.x + mouseLoc.x)
				- startLoc.x, (oldLoc.y + mouseLoc.y) - startLoc.y);
		// no update cascade needed
		movedLabelGeoElement.update();
		kernel.notifyRepaint();
	}

	protected void movePoint(boolean repaint) {
		movedGeoPoint.setCoords(Kernel.checkDecimalFraction(xRW),
				Kernel.checkDecimalFraction(yRW), 1.0);
		((GeoElement) movedGeoPoint).updateCascade();
		movedGeoPointDragged = true;

		if (repaint) {
			kernel.notifyRepaint();
		}
	}

	protected void movePointWithOffset(boolean repaint) {
		movedGeoPoint.setCoords(
				kernel.checkDecimalFraction(xRW - transformCoordsOffset[0]),
				kernel.checkDecimalFraction(yRW - transformCoordsOffset[1]),
				1.0);
		((GeoElement) movedGeoPoint).updateCascade();
		movedGeoPointDragged = true;

		if (repaint) {
			kernel.notifyRepaint();
		}
	}

	final protected void moveLine(boolean repaint) {
		// make parallel geoLine through (xRW, yRW)
		movedGeoLine.setCoords(movedGeoLine.x, movedGeoLine.y,
				-((movedGeoLine.x * xRW) + (movedGeoLine.y * yRW)));
		if (repaint) {
			movedGeoLine.updateRepaint();
		} else {
			movedGeoLine.updateCascade();
		}
	}

	final protected void moveVector(boolean repaint) {
		GeoPoint2 P = movedGeoVector.getStartPoint();
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

	final protected void moveVectorStartPoint(boolean repaint) {
		GeoPoint2 P = movedGeoVector.getStartPoint();
		P.setCoords(xRW, yRW, 1.0);

		if (repaint) {
			movedGeoVector.updateRepaint();
		} else {
			movedGeoVector.updateCascade();
		}
	}

	final protected void moveText(boolean repaint) {
		if (movedGeoText.isAbsoluteScreenLocActive()) {
			movedGeoText.setAbsoluteScreenLoc((oldLoc.x + mouseLoc.x)
					- startLoc.x, (oldLoc.y + mouseLoc.y) - startLoc.y);

			// part of snap to grid code - buggy, so commented out
			// movedGeoText.setAbsoluteScreenLoc(view.toScreenCoordX(xRW -
			// startPoint.x), view.toScreenCoordY(yRW - startPoint.y));
		} else {
			if (movedGeoText.hasAbsoluteLocation()) {
				// absolute location: change location
				GeoPoint2 loc = (GeoPoint2) movedGeoText.getStartPoint();
				loc.setCoords(xRW - startPoint.x, yRW - startPoint.y, 1.0);
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

	final protected void moveImage(boolean repaint) {
		if (movedGeoImage.isAbsoluteScreenLocActive()) {
			// movedGeoImage.setAbsoluteScreenLoc( oldLoc.x +
			// mouseLoc.x-startLoc.x,
			// oldLoc.y + mouseLoc.y-startLoc.y);

			movedGeoImage.setAbsoluteScreenLoc(
					((EuclidianViewInterface) view).toScreenCoordX(xRW - startPoint.x),
					((EuclidianViewInterface) view).toScreenCoordY(yRW - startPoint.y));

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
					GeoPoint2 corner = movedGeoImage.getCorner(i);
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

	final protected void moveConic(boolean repaint) {
		movedGeoConic.set(tempConic);
		movedGeoConic.translate(xRW - startPoint.x, yRW - startPoint.y);

		if (repaint) {
			movedGeoConic.updateRepaint();
		} else {
			movedGeoConic.updateCascade();
		}
	}

	final protected void moveImplicitPoly(boolean repaint) {
		movedGeoImplicitPoly.set(tempImplicitPoly);
		movedGeoImplicitPoly.translate(xRW - startPoint.x, yRW - startPoint.y);

		// set points
		for (int i = 0; i < moveDependentPoints.size(); i++) {
			GeoPoint2 g = moveDependentPoints.get(i);
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

	final protected void moveFunction(boolean repaint) {
		movedGeoFunction.set(tempFunction);
		movedGeoFunction.translate(xRW - startPoint.x, yRW - startPoint.y);

		if (repaint) {
			movedGeoFunction.updateRepaint();
		} else {
			movedGeoFunction.updateCascade();
		}
	}

	final protected void moveBoolean(boolean repaint) {
		// movedGeoBoolean.setAbsoluteScreenLoc( oldLoc.x +
		// mouseLoc.x-startLoc.x,
		// oldLoc.y + mouseLoc.y-startLoc.y);

		// part of snap to grid code
		movedGeoBoolean.setAbsoluteScreenLoc(
				((EuclidianViewInterface) view).toScreenCoordX(xRW - startPoint.x),
				((EuclidianViewInterface) view).toScreenCoordY(yRW - startPoint.y));

		if (repaint) {
			movedGeoBoolean.updateRepaint();
		} else {
			movedGeoBoolean.updateCascade();
		}
	}

	final protected void moveButton(boolean repaint) {
		// movedGeoButton.setAbsoluteScreenLoc( oldLoc.x +
		// mouseLoc.x-startLoc.x,
		// oldLoc.y + mouseLoc.y-startLoc.y);

		// part of snap to grid code
		movedGeoButton.setAbsoluteScreenLoc(
				((EuclidianViewInterface) view).toScreenCoordX(xRW - startPoint.x),
				((EuclidianViewInterface) view).toScreenCoordY(yRW - startPoint.y));

		if (repaint) {
			movedGeoButton.updateRepaint();
		} else {
			movedGeoButton.updateCascade();
		}
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
		} else {
			if (movedGeoNumeric.isAbsoluteScreenLocActive()) {
				param = startPoint.y - mouseLoc.y;
			} else {
				param = yRW - startPoint.y;
			}
		}
		param = (param * (max - min)) / movedGeoNumeric.getSliderWidth();

		// round to animation step scale
		param = Kernel.roundToScale(param,
				movedGeoNumeric.getAnimationStep());
		double val = min + param;

		if (movedGeoNumeric.getAnimationStep() > Kernel.MIN_PRECISION) {
			// round to decimal fraction, e.g. 2.800000000001 to 2.8
			val = Kernel.checkDecimalFraction(val);
		}

		if (movedGeoNumeric.isGeoAngle()) {
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

	final protected void moveNumeric(boolean repaint) {

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

	final protected void moveSlider(boolean repaint) {

		// TEMPORARY_MODE true -> dragging slider using Slider Tool
		// or right-hand mouse button

		if (movedGeoNumeric.isAbsoluteScreenLocActive()) {
			// movedGeoNumeric.setAbsoluteScreenLoc( oldLoc.x +
			// mouseLoc.x-startLoc.x,
			// oldLoc.y + mouseLoc.y-startLoc.y, TEMPORARY_MODE);

			// part of snap to grid code
			movedGeoNumeric.setAbsoluteScreenLoc(
					((EuclidianViewInterface) view).toScreenCoordX(xRW - startPoint.x),
					((EuclidianViewInterface) view).toScreenCoordY(yRW - startPoint.y), TEMPORARY_MODE);
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

	protected void moveMultipleObjects(boolean repaint) {
		translationVec.setX(xRW - startPoint.x);
		translationVec.setY(yRW - startPoint.y);
		startPoint.setLocation(xRW, yRW);
		startLoc = mouseLoc;

		// move all selected geos
		GeoElement.moveObjects(removeParentsOfView(((Application)app).getSelectedGeos()),
				translationVec, new Coords(xRW, yRW, 0), null);

		if (repaint) {
			kernel.notifyRepaint();
		}
	}

	protected ArrayList<GeoElement> removeParentsOfView(
			ArrayList<GeoElement> list) {
		return list;
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

	/*
	 * protected void transformCoords() { transformCoords(false); }
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
		switch (((EuclidianViewInterface) view).getPointCapturingMode()) {

		case EuclidianStyleConstants.POINT_CAPTURING_STICKY_POINTS:
			pointCapturingPercentage = 0.125;
			ArrayList<GeoPointND> spl = ((EuclidianViewInterface) view).getStickyPointList();
			boolean captured = false;
			if (spl != null) {
				for (int i = 0; i < spl.size(); i++) {
					GeoPoint2 gp = (GeoPoint2) spl.get(i);
					if ((Math.abs(gp.getInhomX() - xRW) < (((EuclidianViewInterface)view).getGridDistances(0) * pointCapturingPercentage))
							&& (Math.abs(gp.getInhomY() - yRW) < (((EuclidianViewInterface)view).getGridDistances(1) * pointCapturingPercentage))) {
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
			if (!((EuclidianViewInterface) view).isGridOrAxesShown()) {
				break;
			}

		case EuclidianStyleConstants.POINT_CAPTURING_ON:
			pointCapturingPercentage = 0.125;

		case EuclidianStyleConstants.POINT_CAPTURING_ON_GRID:

			xRW += transformCoordsOffset[0];
			yRW += transformCoordsOffset[1];

			switch (((EuclidianViewInterface) view).getGridType()) {
			case EuclidianView.GRID_ISOMETRIC:

				// isometric Michael Borcherds 2008-04-28
				// iso grid is effectively two rectangular grids overlayed
				// (offset)
				// so first we decide which one we're on (oddOrEvenRow)
				// then compress the grid by a scale factor of root3
				// horizontally to make it square.

				double root3 = Math.sqrt(3.0);
				double isoGrid = ((EuclidianViewInterface)view).getGridDistances(0);
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
							- (((EuclidianViewInterface)view).getGridDistances(0) / 2), isoGrid);
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

			case EuclidianView.GRID_CARTESIAN:

				// X = (x, y) ... next grid point

				double x = Kernel.roundToScale(xRW,
						((EuclidianViewInterface)view).getGridDistances(0));
				double y = Kernel.roundToScale(yRW,
						((EuclidianViewInterface)view).getGridDistances(1));

				// if |X - XRW| < gridInterval * pointCapturingPercentage then
				// take the grid point
				double a = Math.abs(x - xRW);
				double b = Math.abs(y - yRW);

				if ((a < (((EuclidianViewInterface)view).getGridDistances(0) * pointCapturingPercentage))
						&& (b < (((EuclidianViewInterface)view).getGridDistances(1) * pointCapturingPercentage))) {
					xRW = x - transformCoordsOffset[0];
					yRW = y - transformCoordsOffset[1];
				} else {
					xRW -= transformCoordsOffset[0];
					yRW -= transformCoordsOffset[1];
				}
				break;

			case EuclidianView.GRID_POLAR:

				// r = get nearest grid circle radius
				double r = MyMath.length(xRW, yRW);
				double r2 = Kernel.roundToScale(r,
						((EuclidianViewInterface)view).getGridDistances(0));

				// get nearest radial gridline angle
				double angle = Math.atan2(yRW, xRW);
				double angleOffset = angle % ((EuclidianViewInterface)view).getGridDistances(2);
				if (angleOffset < (((EuclidianViewInterface)view).getGridDistances(2) / 2)) {
					angle = angle - angleOffset;
				} else {
					angle = (angle - angleOffset) + ((EuclidianViewInterface)view).getGridDistances(2);
				}

				// get grid point
				double x1 = r2 * Math.cos(angle);
				double y1 = r2 * Math.sin(angle);

				// if |X - XRW| < gridInterval * pointCapturingPercentage then
				// take the grid point
				double a1 = Math.abs(x1 - xRW);
				double b1 = Math.abs(y1 - yRW);

				if ((a1 < (((EuclidianViewInterface)view).getGridDistances(0) * pointCapturingPercentage))
						&& (b1 < (((EuclidianViewInterface)view).getGridDistances(1) * pointCapturingPercentage))) {
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
	 * final protected void transformCoords(boolean usePointCapturing) { // calc
	 * real world coords calcRWcoords();
	 * 
	 * if (usePointCapturing) { double pointCapturingPercentage = 1; switch
	 * (view.getPointCapturingMode()) { case
	 * EuclidianConstants.POINT_CAPTURING_AUTOMATIC: if
	 * (!view.isGridOrAxesShown())break;
	 * 
	 * case EuclidianView.POINT_CAPTURING_ON: pointCapturingPercentage = 0.125;
	 * 
	 * case EuclidianView.POINT_CAPTURING_ON_GRID: // X = (x, y) ... next grid
	 * point double x = Kernel.roundToScale(xRW, view.gridDistances[0]); double
	 * y = Kernel.roundToScale(yRW, view.gridDistances[1]); // if |X - XRW| <
	 * gridInterval * pointCapturingPercentage then take the grid point double a
	 * = Math.abs(x - xRW); double b = Math.abs(y - yRW); if (a <
	 * view.gridDistances[0] * pointCapturingPercentage && b <
	 * view.gridDistances[1] * pointCapturingPercentage) { xRW = x; yRW = y;
	 * mouseLoc.x = view.toScreenCoordX(xRW); mouseLoc.y =
	 * view.toScreenCoordY(yRW); }
	 * 
	 * default: // point capturing off } } }
	 */

	protected void calcRWcoords() {
		xRW = (mouseLoc.x - ((EuclidianViewInterface) view).getXZero()) * ((EuclidianViewInterface) view).getInvXscale();
		yRW = (((EuclidianViewInterface) view).getYZero() - mouseLoc.y) * ((EuclidianViewInterface) view).getInvYscale();
	}

	protected void setMouseLocation(AbstractEvent event) {
		mouseLoc = event.getPoint();

		setAltDown(event.isAltDown());

		if (mouseLoc.x < 0) {
			mouseLoc.x = 0;
		} else if (mouseLoc.x > ((EuclidianViewInterface) view).getViewWidth()) {
			mouseLoc.x = ((EuclidianViewInterface) view).getViewWidth();
		}
		if (mouseLoc.y < 0) {
			mouseLoc.y = 0;
		} else if (mouseLoc.y > ((EuclidianViewInterface) view).getViewHeight()) {
			mouseLoc.y = ((EuclidianViewInterface) view).getViewHeight();
		}
	}

	final protected boolean createNewPoint(Hits hits, boolean onPathPossible,
			boolean intersectPossible, boolean doSingleHighlighting) {

		// inRegionpossible must be false so that the Segment Tool creates a
		// point on the edge of a circle
		return createNewPoint(hits, onPathPossible, false, intersectPossible,
				doSingleHighlighting, false);
	}

	// create new point at current position if hits is null
	// or on path
	// or intersection point
	// returns wether new point was created or not
	final protected boolean createNewPoint(Hits hits, boolean onPathPossible,
			boolean inRegionPossible, boolean intersectPossible,
			boolean doSingleHighlighting, boolean complex) {

		if (!allowPointCreation()) {
			return false;
		}

		GeoPointND point = getNewPoint(hits, onPathPossible, inRegionPossible,
				intersectPossible, doSingleHighlighting, complex);

		if (point != null) {

			updateMovedGeoPoint(point);

			movedGeoElement = getMovedGeoPoint();
			moveMode = MOVE_POINT;
			((EuclidianViewInterface) view).setDragCursor();
			if (doSingleHighlighting) {
				doSingleHighlighting(getMovedGeoPoint());
			}
			POINT_CREATED = true;

			return true;
		} else {
			moveMode = MOVE_NONE;
			POINT_CREATED = false;
			return false;
		}
	}

	// creates or get the new point (used for 3D)
	protected GeoPointND getNewPoint(Hits hits, boolean onPathPossible,
			boolean inRegionPossible, boolean intersectPossible,
			boolean doSingleHighlighting, boolean complex) {

		return updateNewPoint(false, hits, onPathPossible, inRegionPossible,
				intersectPossible, doSingleHighlighting, true, complex);
	}

	// update the new point (used for preview in 3D)
	public GeoPointND updateNewPoint(boolean forPreviewable, Hits hits,
			boolean onPathPossible, boolean inRegionPossible,
			boolean intersectPossible, boolean doSingleHighlighting,
			boolean chooseGeo, boolean complex) {

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
									&& (((GeoConicND) region).getLastHitType() == GeoConicND.HIT_TYPE_ON_FILLING)) {
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
				((EuclidianViewInterface) view).setShowMouseCoords(true);
			}
		}

		return point;
	}

	

	// fetch the two selected points
	/*
	 * protected void join(){ GeoPoint[] points = getSelectedPoints(); GeoLine
	 * line = kernel.Line(null, points[0], points[1]); }
	 */

	// get 2 lines, 2 vectors or 3 points
	final protected GeoElement[] angle(Hits hits) {
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

	protected GeoAngle createAngle(GeoPointND A, GeoPointND B, GeoPointND C) {
		return kernel.Angle(null, (GeoPoint2) A, (GeoPoint2) B, (GeoPoint2) C);
	}

	// build angle between two lines
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

	// get 2 points
	final protected GeoElement[] circleOrSphere2(Hits hits, int mode) {
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

	protected GeoElement[] switchModeForCircleOrSphere2(int mode) {
		GeoPointND[] points = getSelectedPointsND();
		if (mode == EuclidianConstants.MODE_SEMICIRCLE) {
			return new GeoElement[] { kernel.Semicircle(null,
					(GeoPoint2) points[0], (GeoPoint2) points[1]) };
		} else {
			return createCircle2(points[0], points[1]);
		}

	}

	protected GeoElement[] createCircle2(GeoPointND p0, GeoPointND p1) {
		if (((GeoElement) p0).isGeoElement3D()
				|| ((GeoElement) p1).isGeoElement3D()) {
			return createCircle2ForPoints3D(p0, p1);
		} else {
			return new GeoElement[] { kernel.Circle(null, (GeoPoint2) p0,
					(GeoPoint2) p1) };
		}
	}

	protected GeoElement[] createCircle2ForPoints3D(GeoPointND p0, GeoPointND p1) {
		return new GeoElement[] { kernel.getManager3D().Circle3D(null, p0, p1,
				((EuclidianView) view).getDirection()) };
	}

	// get 2 points, 2 lines or 1 point and 1 line
	final protected GeoElement[] distance(Hits hits, AbstractEvent event) {
		if (hits.isEmpty()) {
			return null;
		}
		
		Point mouseCoords = new Point(event.getPoint().x,event.getPoint().y);

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
			GeoText text = createDynamicText(((Application)app).getCommand("Circumference"),
					circumFerence, mouseCoords);
			if (conic.isLabelSet()) {
				circumFerence.setLabel(removeUnderscores(((Application)app).getCommand(
						"Circumference").toLowerCase(Locale.US)
						+ conic.getLabel()));
				text.setLabel(removeUnderscores(((Application)app).getPlain("Text")
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
					descriptionPoints(((Application)app).getCommand("Perimeter"), poly[0]),
					perimeter, mouseCoords);

			if (poly[0].isLabelSet()) {
				perimeter.setLabel(removeUnderscores(((Application)app)
						.getCommand("Perimeter").toLowerCase(Locale.US)
						+ poly[0].getLabel()));
				text.setLabel(removeUnderscores(((Application)app).getPlain("Text")
						+ poly[0].getLabel()));
			}
			GeoElement[] ret = { text };
			return ret;
		}

		return null;
	}

	/**
	 * Creates a text that shows the distance length between geoA and geoB at
	 * the given startpoint.
	 */
	protected GeoText createDistanceText(GeoElement geoA, GeoElement geoB,
			GeoPoint2 startPoint, GeoNumeric length) {
		// create text that shows length
		try {
			String strText = "";
			boolean useLabels = geoA.isLabelSet() && geoB.isLabelSet();
			if (useLabels) {
				length.setLabel(removeUnderscores(((Application)app).getCommand("Distance")
						.toLowerCase(Locale.US)
						+ geoA.getLabel()
						+ geoB.getLabel()));
				// strText = "\"\\overline{\" + Name["+ geoA.getLabel()
				// + "] + Name["+ geoB.getLabel() + "] + \"} \\, = \\, \" + "
				// + length.getLabel();

				// DistanceAB="\\overline{" + %0 + %1 + "} \\, = \\, " + %2
				// or
				// DistanceAB=%0+%1+" \\, = \\, "+%2
				strText = ((Application)app).getPlain("DistanceAB.LaTeX",
						"Name[" + geoA.getLabel() + "]",
						"Name[" + geoB.getLabel() + "]", length.getLabel());
				// Application.debug(strText);
				geoA.setLabelVisible(true);
				geoB.setLabelVisible(true);
				geoA.updateRepaint();
				geoB.updateRepaint();
			} else {
				length.setLabel(removeUnderscores(((Application)app).getCommand("Distance")
						.toLowerCase(Locale.US)));
				strText = "\"\"" + length.getLabel();
			}

			// create dynamic text
			GeoText text = kernel.getAlgebraProcessor().evaluateToText(strText,
					true, true);
			if (useLabels) {
				text.setLabel(removeUnderscores(((Application)app).getPlain("Text")
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

	/**
	 * Creates a text that shows a number value of geo at the current mouse
	 * position.
	 */
	protected GeoText createDynamicText(String descText, GeoElement value,
			Point loc) {
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

	protected String removeUnderscores(String label) {
		// remove all indices
		return label.replaceAll("_", "");
	}

	protected GeoElement[] area(Hits hits, AbstractEvent event) {
		if (hits.isEmpty()) {
			return null;
		}
		
		Point mouseCoords = new Point(event.getPoint().x,event.getPoint().y);

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
			GeoText text = createDynamicText(((Application)app).getCommand("Area"), area,
					mouseCoords);
			if (conic.isLabelSet()) {
				area.setLabel(removeUnderscores(((Application)app).getCommand("Area")
						.toLowerCase(Locale.US) + conic.getLabel()));
				text.setLabel(removeUnderscores(((Application)app).getPlain("Text")
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
					descriptionPoints(((Application)app).getCommand("Area"), poly[0]),
					poly[0], mouseLoc);
			if (poly[0].isLabelSet()) {
				text.setLabel(removeUnderscores(((Application)app).getPlain("Text")
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
				descText = ((Application)app).getCommand("Area");
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
			((Application)app).getGuiManager()
					.getDialogManager()
					.showNumberInputDialogRegularPolygon(
							((Application)app).getMenu(getKernel().getModeText(mode)),
							points[0], points[1]);
			return true;
		}
		return false;
	}

	protected boolean showCheckBox(Hits hits) {
		if (selectionPreview) {
			return false;
		}

		((Application) app).getGuiManager().getDialogManager()
				.showBooleanCheckboxCreationDialog(mouseLoc, null);
		return false;
	}

	final protected boolean showHideObject(Hits hits) {
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
					((EuclidianViewInterface) view).setShowAxis(EuclidianViewInterface.AXIS_X, false, true);
					break;

				case GeoAxisND.Y_AXIS:
					// view.showAxes(view.getShowXaxis(), false);
					((EuclidianViewInterface) view).setShowAxis(EuclidianViewInterface.AXIS_Y, false, true);
					break;
				}
				((Application) app).updateMenubar();
			} else {
				((Application) app).toggleSelectedGeo(geo);
			}
			return true;
		}
		return false;
	}

	// get Transformables and point
	final protected GeoElement[] mirrorAtPoint(Hits hits) {
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

	// get Transformable and line
	final protected GeoElement[] mirrorAtLine(Hits hits) {
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

	// Michael Borcherds 2008-03-23
	final protected GeoElement[] mirrorAtCircle(Hits hits) {
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

	final protected boolean attachDetach(Hits hits, AbstractEvent event) {
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
				double x = ((EuclidianViewInterface) view).toScreenCoordX(p.inhomX) + 20;
				double y = ((EuclidianViewInterface) view).toScreenCoordY(p.inhomY) + 20;

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

	final protected boolean attach(GeoPoint2 point, Path path) {

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

	final protected boolean attach(GeoPoint2 point, Region region) {

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

	// get Transformable and vector
	final protected GeoElement[] translateByVector(Hits hits) {
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

	protected GeoElement[] translate(GeoElement geo, GeoVectorND vec) {
		return kernel.Translate(null, geo, (GeoVector) vec);
	}

	// get rotateable object, point and angle
	final protected GeoElement[] rotateByAngle(Hits hits) {
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

			((Application) app).getGuiManager()
					.getDialogManager()
					.showNumberInputDialogRotate(
							((Application)app).getMenu(getKernel().getModeText(mode)),
							getSelectedPolygons(), getSelectedPoints(), selGeos);

			return null;

		}

		return null;
	}

	// get dilateable object, point and number
	final protected GeoElement[] dilateFromPoint(Hits hits) {
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

			((Application) app).getGuiManager()
					.getDialogManager()
					.showNumberInputDialogDilate(
							((Application)app).getMenu(getKernel().getModeText(mode)),
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

	// get point and number
	final protected boolean segmentFixed(Hits hits) {
		if (hits.isEmpty()) {
			return false;
		}

		// dilation center
		addSelectedPoint(hits, 1, false);

		// we got the point
		if (selPoints() == 1) {
			// get length of segment
			((Application) app).getGuiManager()
					.getDialogManager()
					.showNumberInputDialogSegmentFixed(
							((Application)app).getMenu(getKernel().getModeText(mode)),
							getSelectedPoints()[0]);

			return true;
		}
		return false;
	}

	final protected GeoElement[] fitLine(Hits hits) {

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

	final protected GeoElement[] createList(Hits hits) {
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

	// Michael Borcherds 2008-03-14
	// Markus 2008-07-30: added support for two identical input points (center
	// *2 and point on edge)
	final protected GeoElement[] compasses(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}

		// we already have two points that define the radius
		if (selPoints() == 2) {
			GeoPoint2[] points = new GeoPoint2[2];
			points[0] = (GeoPoint2) selectedPoints.get(0);
			points[1] = (GeoPoint2) selectedPoints.get(1);

			// check for centerPoint
			GeoPoint2 centerPoint = (GeoPoint2) chooseGeo(hits, Test.GEOPOINT2);

			if (centerPoint != null) {
				if (selectionPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add(centerPoint);
					addToHighlightedList(selectedPoints, tempArrayList, 3);
					return null;
				} else {
					// three points: center, distance between two points
					GeoElement circle = kernel.Circle(null, centerPoint,
							points[0], points[1], true);
					GeoElement[] ret = { circle };
					clearSelections();
					return ret;
				}
			}
		}

		// we already have a circle that defines the radius
		else if (selConics() == 1) {
			GeoConic circle = selectedConicsND.get(0);

			// check for centerPoint
			GeoPoint2 centerPoint = (GeoPoint2) chooseGeo(hits, Test.GEOPOINT2);

			if (centerPoint != null) {
				if (selectionPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add(centerPoint);
					addToHighlightedList(selectedPoints, tempArrayList, 3);
					return null;
				} else {
					// center point and circle which defines radius
					GeoElement circlel = kernel.Circle(null, centerPoint,
							circle);
					GeoElement ret[] = { circlel };
					clearSelections();
					return ret;
				}
			}
		}
		// we already have a segment that defines the radius
		else if (selSegments() == 1) {
			GeoSegment segment = selectedSegments.get(0);

			// check for centerPoint
			GeoPoint2 centerPoint = (GeoPoint2) chooseGeo(hits, Test.GEOPOINT2);

			if (centerPoint != null) {
				if (selectionPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add(centerPoint);
					addToHighlightedList(selectedPoints, tempArrayList, 3);
					return null;
				} else {
					// center point and segment
					GeoElement circlel = kernel.Circle(null, centerPoint,
							segment);
					GeoElement[] ret = { circlel };
					clearSelections();
					return ret;
				}
			}
		}

		// don't have radius yet: need two points or segment
		boolean hitPoint = (addSelectedPoint(hits, 2, false) != 0);
		if (!hitPoint && (selPoints() != 2)) {
			addSelectedSegment(hits, 1, false);
			addSelectedConic(hits, 1, false);

			// don't allow conics other than circles to be selected
			if (selectedConicsND.size() > 0) {
				GeoConic c = selectedConicsND.get(0);
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

			((Application) app).getGuiManager()
					.getDialogManager()
					.showNumberInputDialogAngleFixed(
							((Application)app).getMenu(getKernel().getModeText(mode)),
							getSelectedSegments(), getSelectedPoints(), selGeos);

			return null;

		}
		return null;
	}

	// get center point and number
	final protected boolean circlePointRadius(Hits hits) {
		if (hits.isEmpty()) {
			return false;
		}

		addSelectedPoint(hits, 1, false);

		// we got the center point
		if (selPoints() == 1) {
			((Application) app).getGuiManager()
					.getDialogManager()
					.showNumberInputDialogCirclePointRadius(
							((Application)app).getMenu(getKernel().getModeText(mode)),
							getSelectedPointsND()[0], (EuclidianView) view);
			return true;
		}
		return false;
	}

	// get point and vector
	final protected GeoElement[] vectorFromPoint(Hits hits) {
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
			GeoPoint2[] points = getSelectedPoints();
			GeoPoint2 endPoint = (GeoPoint2) kernel.Translate(null, points[0],
					vecs[0])[0];
			GeoElement[] ret = { null };
			ret[0] = kernel.Vector(null, points[0], endPoint);
			return ret;
		}
		return null;
	}

	/**
	 * Handles selected objects for a macro
	 * 
	 * @param hits
	 * @return
	 */
	final protected boolean macro(Hits hits) {
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
		if (!objectFound && macroInput[index].equals(GeoPoint2.class.getName())) {
			if (createNewPoint(hits, true, true, false)) {
				// take movedGeoPoint which is the newly created point
				selectedGeos.add(getMovedGeoPoint());
				((Application)app).addSelectedGeo(getMovedGeoPoint());
				objectFound = true;
				POINT_CREATED = false;
			}
		}

		// object found in handleAddSelected()
		if (objectFound || macroInput[index].equals(Test.GEONUMERIC)
				|| macroInput[index].equals(GeoAngle.class.getName())) {
			if (!objectFound) {
				index--;
			}
			// look ahead if we need a number or an angle next
			while (++index < macroInput.length) {
				// maybe we need a number
				if (macroInput[index].equals(Test.GEONUMERIC)) {
					NumberValue num = ((Application) app)
							.getGuiManager()
							.getDialogManager()
							.showNumberInputDialog(
									macro.getToolOrCommandName(),
									((Application)app).getPlain("Numeric"), null);
					if (num == null) {
						// no success: reset mode
						((EuclidianViewInterface) view).resetMode();
						return false;
					} else {
						// great, we got our number
						if (num.isGeoElement()) {
							selectedGeos.add(num.toGeoElement());
						}
					}
				}

				// maybe we need an angle
				else if (macroInput[index].equals(GeoAngle.class.getName())) {
					Object[] ob = ((Application) app)
							.getGuiManager()
							.getDialogManager()
							.showAngleInputDialog(macro.getToolOrCommandName(),
									((Application)app).getPlain("Angle"), "45\u00b0");
					NumberValue num = (NumberValue) ob[0];

					if (num == null) {
						// no success: reset mode
						((EuclidianViewInterface) view).resetMode();
						return false;
					} else {
						// great, we got our angle
						if (num.isGeoElement()) {
							selectedGeos.add(num.toGeoElement());
						}
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
			kernel.useMacro(null, macro, getSelectedGeos());
			return true;
		}
		return false;
	}

	final protected boolean geoElementSelected(Hits hits, boolean addToSelection) {
		if (hits.isEmpty()) {
			return false;
		}

		addSelectedGeo(hits, 1, false);
		if (selGeos() == 1) {
			GeoElement[] geos = getSelectedGeos();
			((Application)app).geoElementSelected(geos[0], addToSelection);
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
		addSelectedGeo(hits.getPointRotateableHits(view, rotationCenter), 1,
				false);
		return false;
	}

	// dummy function for highlighting:
	// used only in preview mode, see mouseMoved() and selectionPreview
	final protected boolean point(Hits hits) {
		addSelectedGeo(hits.getHits(Test.PATH, tempArrayList), 1, false);
		return false;
	}

	final protected boolean text(Hits hits, int mode, boolean altDown) {
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
			((Application) app).getGuiManager().getDialogManager().showTextCreationDialog(loc);
			return true;
		}

		return false;
	}

	final protected boolean image(Hits hits, int mode, boolean altDown) {
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
			((Application) app).getGuiManager().loadImage(loc, null, altDown);
			return true;
		}

		return false;
	}

	// new slider
	final protected boolean slider() {
		if (!selectionPreview && (mouseLoc != null)) {
			((Application) app).getGuiManager().getDialogManager()
					.showSliderCreationDialog(mouseLoc.x, mouseLoc.y);
		}
		return false;
	}

	// new button
	final protected boolean button(boolean textfield) {
		if (!selectionPreview && (mouseLoc != null)) {
			((Application) app).getGuiManager()
					.getDialogManager()
					.showButtonCreationDialog(mouseLoc.x, mouseLoc.y, textfield);
		}
		return false;
	}

	final protected static boolean pen() {
		// Application.debug(app.getEuclidianView().getHeight()+" "+app.getEuclidianView().getWidth());
		return false;
	}

	public void componentResized(ComponentEvent e) {
		// tell the view that it was resized
		((EuclidianViewInterface) view).updateSize();
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}
	
	protected void wrapMouseWheelMoved(AbstractEvent event) {
		
		if (textfieldHasFocus) {
			return;
		}

		if ((mode == EuclidianConstants.MODE_PEN)
				|| (mode == EuclidianConstants.MODE_FREEHAND)) {
			return;
		}

		// don't allow mouse wheel zooming for applets if mode is not zoom mode
		boolean allowMouseWheel = !((Application)app).isApplet()
				|| (mode == EuclidianConstants.MODE_ZOOM_IN)
				|| (mode == EuclidianConstants.MODE_ZOOM_OUT)
				|| (app.isShiftDragZoomEnabled() && (event.isControlDown()
						|| event.isMetaDown() || event.isShiftDown()));
		if (!allowMouseWheel) {
			return;
		}

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

		((EuclidianViewInterface) view).setAnimatedCoordSystem(
		// px + dx * factor,
		// py + dy * factor,
				px, py, factor, view.getXscale() * factor, 4, false);
		// view.yscale * factor);
		app.setUnsaved();
	}

	/**
	 * Zooms in or out using mouse wheel
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseWheelMoved(event);
		/*if (textfieldHasFocus) {
			return;
		}

		if ((mode == EuclidianConstants.MODE_PEN)
				|| (mode == EuclidianConstants.MODE_FREEHAND)) {
			return;
		}

		// don't allow mouse wheel zooming for applets if mode is not zoom mode
		boolean allowMouseWheel = !((Application)app).isApplet()
				|| (mode == EuclidianConstants.MODE_ZOOM_IN)
				|| (mode == EuclidianConstants.MODE_ZOOM_OUT)
				|| (((Application) app).isShiftDragZoomEnabled() && (e.isControlDown()
						|| e.isMetaDown() || e.isShiftDown()));
		if (!allowMouseWheel) {
			return;
		}

		setMouseLocation(geogebra.euclidian.event.MouseEvent.wrapEvent(e));

		// double px = view.width / 2d;
		// double py = view.height / 2d;
		double px = mouseLoc.x;
		double py = mouseLoc.y;
		// double dx = view.getXZero() - px;
		// double dy = view.getYZero() - py;

		double xFactor = 1;
		if (e.isAltDown()) {
			xFactor = 1.5;
		}

		double reverse = ((Application) app).isMouseWheelReversed() ? -1 : 1;

		double factor = ((e.getWheelRotation() * reverse) > 0) ? EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR
				* xFactor
				: 1d / (EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR * xFactor);

		// make zooming a little bit smoother by having some steps

		((EuclidianViewInterface) view).setAnimatedCoordSystem(
		// px + dx * factor,
		// py + dy * factor,
				px, py, factor, view.getXscale() * factor, 4, false);
		// view.yscale * factor);
		((Application)app).setUnsaved();*/
	}

	public void zoomInOut(KeyEvent event) {
		boolean allowZoom = !((Application)app).isApplet()
				|| (mode == EuclidianConstants.MODE_ZOOM_IN)
				|| (mode == EuclidianConstants.MODE_ZOOM_OUT)
				|| (((Application) app).isShiftDragZoomEnabled());
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

		double factor = event.getKeyCode() == KeyEvent.VK_MINUS ? 1d / EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR
				: EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR;

		// accelerated zoom
		if (event.isAltDown()) {
			factor *= event.getKeyCode() == KeyEvent.VK_MINUS ? 2d / 3d : 1.5;
		}

		// make zooming a little bit smoother by having some steps
		((EuclidianViewInterface) view).setAnimatedCoordSystem(
		// px + dx * factor,
		// py + dy * factor,
				px, py, factor, view.getXscale() * factor, 4, false);
		// view.yscale * factor);
		((Application)app).setUnsaved();

	}

	/*
	 * when drawing a line, this is used when alt is down to set the angle to be
	 * a multiple of 15 degrees
	 */
	public void setLineEndPoint(Point2D.Double point) {
		lineEndPoint = point;
		useLineEndPoint = true;
	}

	// /////////////////////////////////////////
	// moved GeoElements

	/** return the current movedGeoPoint */
	public GeoElement getMovedGeoPoint() {
		return ((GeoElement) movedGeoPoint);
	}

	

	// /////////////////////////////////////////
	// EMPTY METHODS USED FOR EuclidianView3D

	/** right-press the mouse makes start 3D rotation */
	protected void processRightPressFor3D() {

	}

	/**
	 * right-drag the mouse makes 3D rotation
	 * 
	 * @return false
	 */
	protected boolean processRotate3DView() {
		return false;
	}

	/**
	 * right-release the mouse makes stop 3D rotation
	 * 
	 * @return false
	 */
	protected boolean processRightReleaseFor3D() {
		return false;
	}

	// ******************************
	// PropertiesPanelMini Listeners
	// ******************************
	int penLineStyle = 0;

	public void setLineStyle(int lineStyle) {
		penLineStyle = lineStyle;

		// if (mode == EuclidianView.MODE_VISUAL_STYLE) {
		ArrayList<GeoElement> geos = ((Application)app).getSelectedGeos();

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			geo.setLineType(lineStyle);
			geo.updateRepaint();

		}
		// }
	}

	public void setSize(int size) {
		// if (mode == EuclidianView.MODE_VISUAL_STYLE) {
		ArrayList<GeoElement> geos = ((Application)app).getSelectedGeos();

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

	Color penColor = Color.black;

	public void setColor(Color color) {
		penColor = color;

		// if (mode == EuclidianView.MODE_VISUAL_STYLE) {
		ArrayList<GeoElement> geos = ((Application)app).getSelectedGeos();

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			geo.setObjColor(new geogebra.awt.Color(color));
			geo.updateRepaint();
		}
		// }
	}

	public void setAlpha(float alpha) {
		ArrayList<GeoElement> geos = ((Application)app).getSelectedGeos();
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			geo.setAlphaValue(alpha);
			geo.updateRepaint();
		}
	}

	private void openMiniPropertiesPanel() {
		if (!((Application)app).isUsingFullGui()) {
			return;
		}
		if (Application.isMiniPropertiesActive()) {
			((Application) app).getGuiManager().toggleMiniProperties(true);
		}
	}

	private void closeMiniPropertiesPanel() {
		if (!((Application)app).isUsingFullGui()) {
			return;
		}
		((Application) app).getGuiManager().toggleMiniProperties(false);

	}

	protected boolean moveMode(int mode) {
		if ((mode == EuclidianConstants.MODE_MOVE)
				|| (mode == EuclidianConstants.MODE_VISUAL_STYLE)) {
			return true;
		} else {
			return false;
		}
	}

	private String sliderValue = null;

	public String getSliderValue() {
		return sliderValue;
	}

	public Hits getHighlightedgeos() {
		return highlightedGeos.clone();
	}

	public boolean isAltDown() {
		return altDown;
	}

	public void setAltDown(boolean altDown) {
		this.altDown = altDown;
	}

	public void setLineEndPoint(geogebra.common.awt.Point2D p) {
		if(p==null)
			lineEndPoint = null;
		else
		lineEndPoint = new Point2D.Double(p.getX(),p.getY());
		useLineEndPoint = true;
	}

	public GeoElement getRecordObject() {
		return recordObject;
	}

}
