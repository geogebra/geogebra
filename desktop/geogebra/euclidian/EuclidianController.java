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
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.Previewable;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.VectorValue;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.PointProperties;
import geogebra.common.kernel.geos.Test;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
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
			previewDrawable = new DrawConic((AbstractEuclidianView) view, mode,
					selectedPoints, selectedSegments, selectedConicsND);
			break;

		// preview for arcs and sectors
		case EuclidianConstants.MODE_SEMICIRCLE:
		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			previewDrawable = new DrawConicPart((AbstractEuclidianView) view, mode,
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

	public void mouseClicked(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseclicked(event);
	}

	public void mousePressed(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMousePressed(event);
	}

	private EuclidianViewInterfaceCommon setShowMouseCoords(boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	public void mouseDragged(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseDragged(event);
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
			pen.handleMouseReleasedForPenMode(event);
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

		view.requestFocusInWindow();
		setMouseLocation(event);

		setAltDown(event.isAltDown());

		transformCoords();
		Hits hits = null;
		GeoElement geo;

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
				if ((Application.MAC_OS && app.isControlDown(event))
						|| !app.isRightClick(event)) {
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
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseMoved(event);
	}
	
	public void mouseEntered(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseEntered(event);
	}
	
	public void mouseExited(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseExited(event);
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

	public void resetToolTipManager() {
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(DEFAULT_INITIAL_DELAY);
	}

	/* ****************************************************** */

	

	

	

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

	

	

	

	

	

	// fetch the two selected points
	/*
	 * protected void join(){ GeoPoint[] points = getSelectedPoints(); GeoLine
	 * line = kernel.Line(null, points[0], points[1]); }
	 */

	public GeoElement[] createCircle2ForPoints3D(GeoPointND p0, GeoPointND p1) {
		return new GeoElement[] { kernel.getManager3D().Circle3D(null, p0, p1,
				((AbstractEuclidianView) view).getDirection()) };
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

		double factor = ((event.getWheelRotation() * reverse) > 0) ? AbstractEuclidianView.MOUSE_WHEEL_ZOOM_FACTOR
				* xFactor
				: 1d / (AbstractEuclidianView.MOUSE_WHEEL_ZOOM_FACTOR * xFactor);

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

		double factor = event.getKeyCode() == KeyEvent.VK_MINUS ? 1d / AbstractEuclidianView.MOUSE_WHEEL_ZOOM_FACTOR
				: AbstractEuclidianView.MOUSE_WHEEL_ZOOM_FACTOR;

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

	

	

	// /////////////////////////////////////////
	// EMPTY METHODS USED FOR EuclidianView3D

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

	public Hits getHighlightedgeos() {
		return highlightedGeos.clone();
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
