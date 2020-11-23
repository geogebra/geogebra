package org.geogebra.web.html5.euclidian;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.euclidianForPlane.EuclidianViewForPlaneInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.TextValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.tooltip.PreviewPointPopup;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
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

/**
 * Web version of Euclidian controller
 *
 */
public class EuclidianControllerW extends EuclidianController implements
        MouseDownHandler, MouseUpHandler, MouseMoveHandler, MouseOutHandler,
        MouseOverHandler, MouseWheelHandler, TouchStartHandler,
        TouchEndHandler, TouchMoveHandler, TouchCancelHandler,
        GestureStartHandler, GestureEndHandler, GestureChangeHandler,
		IsEuclidianController, DropHandler {

	private MouseTouchGestureControllerW mtg;

	@Override
	public EnvironmentStyleW getEnvironmentStyle() {
		return mtg.getEnvironmentStyle();
	}

	@Override
	protected void showSpecialPointPopup(
			final ArrayList<GeoElement> previewPoints) {
		if (!app.getConfig().hasPreviewPoints()) {
			return;
		}
		final PreviewPointPopup popup = new PreviewPointPopup(
				(AppW) getApplication(), previewPoints);
		popup.setPopupPositionAndShow(new GPopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				popup.positionPopup(offsetWidth, offsetHeight, previewPoints);
			}
		});
	}

	@Override
	public void showListToolTip(String message) {
		if (message != null && !"".equals(message)) {
			ToolTipManagerW.sharedInstance().setBlockToolTip(false);
			ToolTipManagerW.sharedInstance().showBottomInfoToolTip(message,
					" ", ToolTipLinkType.Help, (AppW) app,
					((AppW) app).getAppletFrame().isKeyboardShowing());
			ToolTipManagerW.sharedInstance().setBlockToolTip(true);
		}
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

	@Override
	protected void createCompanions() {
		super.createCompanions();
		mtg = new MouseTouchGestureControllerW((AppW) app, this);
	}

	/**
	 * Creates a new controller
	 *
	 * @param kernel
	 *            kernel
	 */
	public EuclidianControllerW(Kernel kernel) {
		super(kernel.getApplication());
		setKernel(kernel);
	}

	@Override
	public void handleLongTouch(int x, int y) {
		mtg.handleLongTouch(x, y);
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

	/**
	 * @param touch
	 *            first finger
	 * @param touch2
	 *            second finger
	 */
	public void twoTouchMove(Touch touch, Touch touch2) {
		mtg.twoTouchMove(touch, touch2);
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		mtg.onTouchEnd(event);
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		if ((app.getGuiManager() != null) && shouldSetToolbar()) {
			// Probability calculator plot panel view should not set active
			// toolbar ID
			// this is used by DataDisplayPanelW and PlotPanelEuclidianViewW,
			// #plotpanelevno
			// probably both are Okay not changing the toolbar to full Graphics
			// view toolbar
			((GuiManagerInterfaceW) app.getGuiManager())
					.setActivePanelAndToolbar(App.VIEW_EUCLIDIAN);
		} else {
			setMode(EuclidianConstants.MODE_MOVE, ModeSetter.TOOLBAR);
		}
		mtg.onTouchStart(event);
	}

	/**
	 * @param event
	 *            touch event
	 */
	public void preventTouchIfNeeded(TouchStartEvent event) {
		mtg.preventTouchIfNeeded(event);
	}

	/**
	 * @param touch
	 *            first finger
	 * @param touch2
	 *            second finger
	 */
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
		if ((app.getGuiManager() != null) && shouldSetToolbar()) {
			// Probability calculator plot panel view should not set active
			// toolbar ID
			// this is used by DataDisplayPanelW and PlotPanelEuclidianViewW,
			// #plotpanelevno
			// probably both are Okay not changing the toolbar to full Graphics
			// view toolbar
			((GuiManagerInterfaceW) app.getGuiManager())
					.setActivePanelAndToolbar(App.VIEW_EUCLIDIAN);
		} else {
			if (EuclidianConstants.isMoveOrSelectionMode(mode)
					|| mode == EuclidianConstants.MODE_TRANSLATEVIEW
					|| mode == EuclidianConstants.MODE_SELECTION_LISTENER) {
				setMode(mode, ModeSetter.TOOLBAR);
			}
			// app.setMode(EuclidianConstants.MODE_MOVE);
			// app.getGuiManager().updateToolbar();
		}
		mtg.onPointerEventStart(event);
	}

	private boolean shouldSetToolbar() {
		return (getEvNo() != EuclidianView.EVNO_GENERAL
				|| (getView() instanceof EuclidianViewForPlaneInterface));
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
			this.draggingOccured = false;
			getView().setHits(mouseLoc, type);
			Hits hits = getView().getHits().getTopHits();
			if (!hits.isEmpty()) {
				GeoElement hit = hits.get(0);
				if (hit != null && !hit.isGeoButton() && !hit.isGeoInputBox()
				        && !hit.isGeoBoolean()) {
					GeoElement geo = chooseGeo(hits, true);
					if (geo != null) {
						runScriptsIfNeeded(geo);
					}
				}
			}

			return true;
		}
		return getView().textfieldClicked(x, y, type);
	}

	@Override
	public boolean isComboboxFocused() {
		return mtg.isComboboxFocused();
	}

	/**
	 * @param flag
	 *            whether a combobox has focus
	 */
	public void setComboboxFocused(boolean flag) {
		mtg.setComboboxFocused(flag);
	}

	/**
	 * @return the multiplier that must be used to multiply the native event
	 *         coordinates
	 */
	public double getScaleXMultiplier() {
		return mtg.getScaleXMultiplier();
	}

	/**
	 * @return the multiplier that must be used to multiply the native event
	 *         coordinates
	 */
	public double getScaleYMultiplier() {
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

	@Override
	protected void startCapture(AbstractEvent event) {
		// removed
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
	protected void showPopupMenuChooseGeo(ArrayList<GeoElement> selectedGeos1,
	        Hits hits) {
		ArrayList<GeoElement> geos = selectedGeos1 != null
		        && selectedGeos1.isEmpty() ? getAppSelectedGeos()
		        : selectedGeos1;
		app.getGuiManager().showPopupMenu(geos, getView(), mouseLoc);
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
		// TODO decide if we ever want to do this
	}

	@Override
	public void onDrop(DropEvent event) {
		GeoElement geo = app.getAlgebraView().getDraggedGeo();
		if (geo == null) {
			return;
		}
		ArrayList<String> list = new ArrayList<>();
		list.add(geo.isLabelSet() ? geo.getLabelSimple() : "\""
		        + geo.getLaTeXAlgebraDescription(true,
		                StringTemplate.latexTemplate) + "\"");
		String text = EuclidianView.getDraggedLabels(list);

		GeoElementND[] ret = app.getKernel().getAlgebraProcessor()
		        .processAlgebraCommand(text, true);

		if (ret != null && ret[0] instanceof TextValue) {
			GeoText geo0 = (GeoText) ret[0];
			geo0.setLaTeX(true, false);

			int x = event.getNativeEvent().getClientX();
			int y = event.getNativeEvent().getClientY();

			geo0.setRealWorldLoc(getView().toRealWorldCoordX(mtg.touchEventX(x)),
					getView().toRealWorldCoordY(mtg.touchEventY(y)));
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
		app.closePopups(wrap.getX(), wrap.getY());
	}

	@Override
	public void hideDynamicStylebar() {
		if (getView().hasDynamicStyleBar()) {
			getView().getDynamicStyleBar().setVisible(false);
		}
	}

	@Override
	public void showDynamicStylebar() {
		if (((AppW) app).allowStylebar()) {
			getView().getDynamicStyleBar().setVisible(true);
			getView().getDynamicStyleBar().updateStyleBar();
		}
	}

	@Override
	public void onPointerEventMove(PointerEvent event) {
		mtg.onMouseMoveNow(event, System.currentTimeMillis(), true);
	}

	@Override
	public void onPointerEventEnd(PointerEvent event) {
		mtg.onPointerEventEnd(event);
	}

	@Override
	public MouseTouchGestureControllerW getOffsets() {
		return mtg;
	}

	@Override
	protected void runPointerCallback(Runnable pointerUpCallback) {
		if (Browser.isIE()) {
			app.invokeLater(pointerUpCallback);
		} else {
			pointerUpCallback.run();
		}
	}

	/**
	 * @return MouseTouchGestureControllerW instance
	 */
	public MouseTouchGestureControllerW getMouseTouchGestureController() {
		return mtg;
	}
}

