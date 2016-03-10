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
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
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

	@Override
	public void onPointerEventStart(AbstractEvent event) {

		if (temporaryMode) {
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
			if (mode == EuclidianConstants.MODE_MOVE
					|| mode == EuclidianConstants.MODE_TRANSLATEVIEW
					|| mode == EuclidianConstants.MODE_SELECTION_LISTENER) {
			setMode(mode);
			}
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
	protected void startCapture(AbstractEvent event) {
		Event.setCapture(((PointerEvent) event).getRelativeElement());
	}



	@Override
	protected boolean moveAxesPossible() {
		return super.moveAxesPossible() && this.moveAxesAllowed;
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



	@Override
	protected void showPopupMenuChooseGeo(ArrayList<GeoElement> selectedGeos1,
	        Hits hits) {
		ArrayList<GeoElement> geos = selectedGeos1 != null
		        && selectedGeos1.isEmpty() ? getAppSelectedGeos()
		        : selectedGeos1;
		app.getGuiManager().showPopupMenu(geos, view, mouseLoc);
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
	@Override
	public void setActualSticky(boolean sticky) {
		this.actualSticky = sticky;
	}

	@Override
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

	@Override
	public LongTouchManager getLongTouchManager() {
		return mtg.getLongTouchManager();
	}

	@Override
	public void closePopups(int x, int y, PointerEventType type) {
		PointerEvent wrap = new PointerEvent(x, y, type, mtg);
		((AppW) app).closePopups(wrap.getX(), wrap.getY());

	}
}

