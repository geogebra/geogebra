package org.geogebra.web.html5.euclidian;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.euclidianForPlane.EuclidianViewForPlaneInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.TextValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.Test;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.euclidian.EuclidianPenFreehand.ShapeType;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.GestureChangeEvent;
import com.google.gwt.event.dom.client.GestureChangeHandler;
import com.google.gwt.event.dom.client.GestureEndEvent;
import com.google.gwt.event.dom.client.GestureEndHandler;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.GestureStartHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Event;

public class EuclidianControllerW extends EuclidianController implements
        MouseDownHandler, MouseUpHandler, MouseMoveHandler, MouseOutHandler,
        MouseOverHandler, MouseWheelHandler, TouchStartHandler,
        TouchEndHandler, TouchMoveHandler, TouchCancelHandler,
        GestureStartHandler, GestureEndHandler, GestureChangeHandler,
		IsEuclidianController, DropHandler {



	/**
	 * Threshold for the selection rectangle distance squared (10 pixel circle)
	 */
	public final static double SELECTION_RECT_THRESHOLD_SQR = 200.0;
	public final static double FREEHAND_MODE_THRESHOLD_SQR = 200.0;

	/**
	 * flag for blocking the scaling of the axes
	 */
	protected boolean moveAxesAllowed = true;

	private int previousMode = -1;



	/**
	 * whether to keep the actual tool after successfully constructing an
	 * element (if set to true) or to change back to the move tool (if set to
	 * false)
	 */
	public boolean USE_STICKY_TOOLS = true;

	private boolean actualSticky = false;

	@Override
	public EnvironmentStyleW getEnvironmentStyle() {
		return mtg.getEnvironmentStyle();
	}

	/**
	 * recalculates cached styles concerning browser environment
	 */
	@Override
	public void calculateEnvironment() {
		mtg.calculateEnvironment();
	}

	@Override
	public void moveIfWaiting() {
		mtg.moveIfWaiting();
	}

	public boolean isOffsetsUpToDate() {
		return mtg.isOffsetsUpToDate();
	}

	private MouseTouchGestureControllerW mtg;

	@Override
	protected void createCompanions() {
		super.createCompanions();
		mtg = new MouseTouchGestureControllerW((AppW) app, this);
	}

	public EuclidianControllerW(Kernel kernel) {
		super(kernel.getApplication());
		setKernel(kernel);

		tempNum = new MyDouble(kernel);
	}

	@Override
	public void handleLongTouch(int x, int y) {
		mtg.handleLongTouch(x, y);
	}

	@Override
	public void setView(EuclidianView view) {
		this.view = view;
	}

	@Override
	public void onGestureChange(GestureChangeEvent event) {
		mtg.onGestureChange(event);
	}

	@Override
	public void onGestureEnd(GestureEndEvent event) {
		mtg.onGestureEnd(event);
	}

	@Override
	public void onGestureStart(GestureStartEvent event) {
		mtg.onGestureStart(event);
	}

	@Override
	public void onTouchCancel(TouchCancelEvent event) {
		mtg.onTouchCancel(event);
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		mtg.onTouchMove(event);
	}

	public void twoTouchMove(Touch touch, Touch touch2) {
		mtg.twoTouchMove(touch, touch2);
	}


	@Override
	public void onTouchEnd(TouchEndEvent event) {
		mtg.onTouchEnd(event);
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		if ((app.getGuiManager() != null)
		        && (getEvNo() != EuclidianView.EVNO_GENERAL || (view instanceof EuclidianViewForPlaneInterface))) {
			// Probability calculator plot panel view should not set active
			// toolbar ID
			// this is used by DataDisplayPanelW and PlotPanelEuclidianViewW,
			// #plotpanelevno
			// probably both are Okay not changing the toolbar to full Graphics
			// view toolbar
			((GuiManagerInterfaceW) app.getGuiManager())
			        .setActiveToolbarId(App.VIEW_EUCLIDIAN);
		} else {
			setMode(EuclidianConstants.MODE_MOVE);
			// app.setMode(EuclidianConstants.MODE_MOVE);
			// app.getGuiManager().updateToolbar();
		}
		mtg.onTouchStart(event);
	}

	public void preventTouchIfNeeded(TouchStartEvent event) {
		mtg.preventTouchIfNeeded(event);
	}

	public void twoTouchStart(Touch touch, Touch touch2) {
		mtg.twoTouchStart(touch, touch2);
	}

	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		mtg.onMouseWheel(event);
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		mtg.onMouseOver(event);
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		mtg.onMouseOut(event);
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		mtg.onMouseMove(event);
	}

	public void onMouseMoveNow(PointerEvent event, long time,
	        boolean startCapture) {
		mtg.onMouseMoveNow(event, time, startCapture);
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		mtg.onMouseUp(event);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		mtg.onMouseDown(event);
	}

	public void onPointerEventStart(AbstractEvent event) {

		if (temporaryMode) {
			App.debug("UNCHECK");
			mtg.setComboboxFocused(false);
		}
		if ((app.getGuiManager() != null)
		        && (getEvNo() != EuclidianView.EVNO_GENERAL || (view instanceof EuclidianViewForPlaneInterface))) {
			// Probability calculator plot panel view should not set active
			// toolbar ID
			// this is used by DataDisplayPanelW and PlotPanelEuclidianViewW,
			// #plotpanelevno
			// probably both are Okay not changing the toolbar to full Graphics
			// view toolbar
			((GuiManagerInterfaceW) app.getGuiManager())
			        .setActiveToolbarId(App.VIEW_EUCLIDIAN);
		} else {
			setMode(EuclidianConstants.MODE_MOVE);
			// app.setMode(EuclidianConstants.MODE_MOVE);
			// app.getGuiManager().updateToolbar();
		}
		mtg.onPointerEventStart(event);
	}

	@Override
	protected void initToolTipManager() {
		mtg.initToolTipManager();
	}

	@Override
	protected void resetToolTipManager() {
		mtg.resetToolTipManager();
	}

	@Override
	protected boolean hitResetIcon() {
		return mtg.hitResetIcon();
	}



	@Override
	public boolean textfieldJustFocused(int x, int y, PointerEventType type) {


		if (isComboboxFocused()) {
			org.geogebra.common.util.debug.Log.info("isComboboxFocused!");
			this.draggingOccured = false;
			view.setHits(mouseLoc, type);
			Hits hits = view.getHits().getTopHits();
			if (!hits.isEmpty()) {
				GeoElement hit = hits.get(0);
				if (hit != null && !hit.isGeoButton() && !hit.isGeoTextField()
				        && !hit.isGeoBoolean()) {
					GeoElement geo = chooseGeo(hits, true);
					if (geo != null) {
						runScriptsIfNeeded(geo);
					}
				}
			}

			return true;
		}
		// return view.textfieldClicked(x, y, type) || isComboboxFocused();
		return view.textfieldClicked(x, y, type);
	}

	@Override
	public boolean isComboboxFocused() {
		return mtg.isComboboxFocused();
	}

	public void setComboboxFocused(boolean flag) {
		mtg.setComboboxFocused(flag);
	}


	/**
	 * @return the multiplier that must be used to multiply the native event
	 *         coordinates
	 */
	public float getScaleXMultiplier() {
		return mtg.getScaleXMultiplier();
	}

	/**
	 * @return the multiplier that must be used to multiply the native event
	 *         coordinates
	 */
	public float getScaleYMultiplier() {
		return mtg.getScaleYMultiplier();
	}

	/*
	 * @Override public int getEvID() { return view.getViewID(); }
	 */

	@Override
	public void twoTouchMove(double x1d, double y1d, double x2d, double y2d) {
		mtg.twoTouchMove(x1d, y1d, x2d, y2d);
	}

	@Override
	public void twoTouchStart(double x1, double y1, double x2, double y2) {
		mtg.twoTouchStart(x1, y1, x2, y2);
	}


	// /////////////////////////////////////////////////////
	// specific methods for 2D controller
	// /////////////////////////////////////////////////////







	@Override
	public void wrapMouseDragged(AbstractEvent event, boolean startCapture) {
		if (pen != null && !penDragged && freehandModePrepared) {
			getPen().handleMouseDraggedForPenMode(event);
		}

		if (firstSelectedPoint != null
		        && this.mode == EuclidianConstants.MODE_CIRCLE_POINT_RADIUS) {
			// prevent further processing
			if (!withinPointSelectionDistance(startPosition, event)) {
				// update the preview circle
				super.wrapMouseMoved(event);
			}
			return;
		}

		if (!shouldCancelDrag()) {
			if (shouldSetToFreehandMode()) {
				setModeToFreehand();
			}
			// Set capture events only if the mouse is actually down,
			// because we need to release the capture on mouse up.
			if (startCapture) {
				Event.setCapture(((PointerEvent) event).getRelativeElement());
			}
			super.wrapMouseDragged(event, startCapture);
		}
		if (movedGeoPoint != null
		        && (this.mode == EuclidianConstants.MODE_JOIN
		                || this.mode == EuclidianConstants.MODE_SEGMENT
		                || this.mode == EuclidianConstants.MODE_RAY
		                || this.mode == EuclidianConstants.MODE_VECTOR
		                || this.mode == EuclidianConstants.MODE_CIRCLE_TWO_POINTS
		                || this.mode == EuclidianConstants.MODE_SEMICIRCLE || this.mode == EuclidianConstants.MODE_REGULAR_POLYGON)) {
			// nothing was dragged
			super.wrapMouseMoved(event);
		}

		if (view.getPreviewDrawable() != null
		        && event.getType() == PointerEventType.TOUCH) {
			this.view.updatePreviewableForProcessMode();
		}
	}

	/**
	 * selects a GeoElement; no effect, if it is already selected
	 * 
	 * @param geo
	 *            the GeoElement to be selected
	 */
	public void select(GeoElement geo) {
		if (geo != null && !selectedGeos.contains(geo)) {
			Hits h = new Hits();
			h.add(geo);
			addSelectedGeo(h, 1, false);
		}
	}


	@Override
	protected boolean moveAxesPossible() {
		return super.moveAxesPossible() && this.moveAxesAllowed;
	}

	private boolean freehandModePrepared = false;
	private boolean freehandModeSet = false;

	@Override
	public void prepareModeForFreehand() {
		if (selectedPoints.size() != 0) {
			// make sure to switch only for the first point
			return;
		}

		// defined at the beginning, because it is modified for some modes
		GeoPoint point = (GeoPoint) this.view.getHits().getFirstHit(
		        Test.GEOPOINT);
		if (point == null && this.movedGeoPoint instanceof GeoPoint) {
			point = (GeoPoint) this.movedGeoPoint;
		}

		switch (this.mode) {
		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
			this.pen = new EuclidianPenFreehand(app, view);
			((EuclidianPenFreehand) pen).setExpected(ShapeType.circleThreePoints);

			// the point will be deleted if no circle can be built, therefore
			// make sure that only a newly created point is set
			point = (this.pointCreated != null)
			        && movedGeoPoint instanceof GeoPoint ? (GeoPoint) movedGeoPoint
			        : null;
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
		case EuclidianConstants.MODE_FREEHAND_CIRCLE:
			this.pen = new EuclidianPenFreehand(app, view);
			((EuclidianPenFreehand) pen).setExpected(ShapeType.circle);
			point = null;
			break;
		default:
			return;
		}
		freehandModePrepared = true;
		((EuclidianPenFreehand) pen).setInitialPoint(point, point != null
		        && point.equals(pointCreated));
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
	 * rest all the settings that have been changed in setModeToFreehand().
	 * 
	 * no effect if setModeToFreehand() has not been called or had no effect
	 * (e.g. because the selected tool is not supported)
	 */
	@Override
	public void resetModeAfterFreehand() {
		if (freehandModePrepared) {
			freehandModePrepared = false;
			pen = null;
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



	@Override
	protected boolean processZoomRectangle() {
		boolean processed = super.processZoomRectangle();
		if (processed) {
			selectionStartPoint.setLocation(mouseLoc);
		}
		return processed;
	}

	@Override
	protected void updateSelectionRectangle(boolean keepScreenRatio) {
		if (!shouldUpdateSelectionRectangle()) {
			return;
		}
		super.updateSelectionRectangle(keepScreenRatio);
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

	private boolean shouldSetToFreehandMode() {
		return (isDraggingBeyondThreshold() && pen != null && !penMode(mode) && freehandModePrepared);
	}

	@Override
	protected void showPopupMenuChooseGeo(ArrayList<GeoElement> selectedGeos1,
	        Hits hits) {
		ArrayList<GeoElement> geos = selectedGeos1 != null
		        && selectedGeos1.isEmpty() ? getAppSelectedGeos()
		        : selectedGeos1;
		app.getGuiManager().showPopupMenu(geos, view, mouseLoc);
	}

	@Override
	protected boolean freehandModePrepared() {
		return freehandModePrepared;
	}

	@Override
	public void toolCompleted() {
		if (!USE_STICKY_TOOLS && !actualSticky) {
			// changes the selected button in the toolbar
			((AppW) app).getToolbar().setMode(0, ModeSetter.TOOLBAR);

			// change mode of the EV
			app.getActiveEuclidianView().setMode(0, ModeSetter.TOOLBAR);
		}
	}

	/**
	 * set whether the actual tool should be kept after the element was
	 * constructed or not
	 * 
	 * @param sticky
	 *            keep the tool iff true
	 */
	public void setActualSticky(boolean sticky) {
		this.actualSticky = sticky;
	}

	public void onDrop(DropEvent event) {
		app.setActiveView(App.VIEW_EUCLIDIAN);
		app.setActiveView(App.VIEW_EUCLIDIAN2);
			
		EuclidianViewInterfaceCommon ev = 
				app.getActiveEuclidianView();

		GeoElement geo = app.getAlgebraView().getDraggedGeo();
		if (geo == null) {
			return;
		}
		ArrayList<String> list = new ArrayList<String>();
		list.add(geo.isLabelSet() ? geo.getLabelSimple() : "\""
		        + geo.getLaTeXAlgebraDescription(true,
		                StringTemplate.latexTemplate) + "\"");
		String text = EuclidianView.getDraggedLabels(list);

		GeoElement[] ret = app.getKernel().getAlgebraProcessor()
		        .processAlgebraCommand(text, true);

		if (ret != null && ret[0] instanceof TextValue) {
			GeoText geo0 = (GeoText) ret[0];
			geo0.setLaTeX(true, false);

			// TODO: h should equal the geo height, this is just an
			// estimate
			int x = event.getNativeEvent().getClientX();
			//double h = 2 * app.getFontSize();
			int y = event.getNativeEvent().getClientY();

			geo0.setRealWorldLoc(ev.toRealWorldCoordX(mtg.touchEventX(x)),
					ev.toRealWorldCoordY(mtg.touchEventY(y)));
			geo0.updateRepaint();

		}
	}

	public LongTouchManager getLongTouchManager() {
		return mtg.getLongTouchManager();
	}
}
