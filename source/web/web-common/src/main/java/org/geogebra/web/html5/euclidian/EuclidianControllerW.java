/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.euclidian.measurement.CreateToolImage;
import org.geogebra.common.euclidianForPlane.EuclidianViewForPlaneInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.TextValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.awt.MyImageW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.tooltip.PreviewPointPopup;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.DropEvent;
import org.gwtproject.event.dom.client.DropHandler;

import elemental2.dom.HTMLImageElement;
import elemental2.dom.WheelEvent;

/**
 * Web version of Euclidian controller
 *
 */
public class EuclidianControllerW extends EuclidianController implements
		IsEuclidianController, DropHandler {

	private MouseTouchGestureControllerW mtg;
	private CreateToolImage toolImageW;

	@Override
	protected void showSpecialPointPopup(
			final ArrayList<GeoElement> previewPoints) {
		if (!app.getConfig().hasPreviewPoints()) {
			return;
		}
		final PreviewPointPopup popup = new PreviewPointPopup(
				(AppW) getApplication(), previewPoints);
		popup.setPopupPositionAndShow(
				(offsetWidth, offsetHeight) ->
						popup.positionPopup(offsetWidth, offsetHeight, previewPoints));
	}

	@Override
	public void showListToolTip(String message) {
		if (!StringUtil.empty(message)) {
			ToolTipManagerW toolTipManager = ((AppW) app).getToolTipManager();
			toolTipManager.showBottomMessage(message, (AppW) app);
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

	/**
	 * Handle mouse wheel event.
	 * @param event mouse wheel event
	 */
	public void onMouseWheel(WheelEvent event) {
		mtg.onMouseWheel(event);
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
		}
		mtg.onPointerEventStart(event);
	}

	private boolean shouldSetToolbar() {
		return getEvNo() != EuclidianView.EVNO_GENERAL
				|| (getView() instanceof EuclidianViewForPlaneInterface);
	}

	@Override
	protected void resetToolTipManager() {
		mtg.resetToolTipManager();
	}

	@Override
	public boolean isInputBoxClicked(int x, int y, PointerEventType type) {
		if (isComboboxFocused()) {
			this.draggingOccurred = false;
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
		return super.isInputBoxClicked(x, y, type);
	}

	@Override
	public boolean isComboboxFocused() {
		return mtg.isComboboxFocused();
	}

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
		if (app.isUnbundledOrWhiteboard()) {
			ArrayList<GeoElement> geos = selectedGeos1 != null
					&& selectedGeos1.isEmpty() ? getAppSelectedGeos()
					: selectedGeos1;
			app.getGuiManager().showPopupMenu(geos, getView(), mouseLoc);
		} else {
			super.showPopupMenuChooseGeo(selectedGeos1, hits);
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

			geo0.setRealWorldLoc(getView().toRealWorldCoordX(x),
					getView().toRealWorldCoordY(y));
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
		if (((AppW) app).allowStylebar() && !selectedGeoShouldHaveQuickStyleBar()) {
			getView().getDynamicStyleBar().setVisible(true);
			getView().getDynamicStyleBar().updateStyleBar();
		}
	}

	/**
	 * Measurement tools, spotlight and input-box has no style bar.
	 * @return whether selected geos should have quick style bar
	 */
	private boolean selectedGeoShouldHaveQuickStyleBar() {
		return getAppSelectedGeos().stream().anyMatch(f -> f.isMeasurementTool()
			|| f.isSpotlight() || f.isGeoInputBox());
	}

	@Override
	public void onPointerEventMove(PointerEvent event) {
		//disable pointer events for the zoom panel when dragging something over it
		if (draggingOccurred) {
			((EuclidianViewW) getView())
					.getDockPanel().enableZoomPanelEvents(false);
		}
		mtg.onMouseMoveNow(event, System.currentTimeMillis(), true);
	}

	@Override
	public void onPointerEventEnd(PointerEvent event) {
		//enable pointer events for the zoom panel again after dragging something over it
		if (draggingOccurred) {
			((EuclidianViewW) getView())
					.getDockPanel().enableZoomPanelEvents(true);
		}
		mtg.onPointerEventEnd(event);
	}

	@Override
	public MouseTouchGestureControllerW getOffsets() {
		return mtg;
	}

	/**
	 * @return MouseTouchGestureControllerW instance
	 */
	public MouseTouchGestureControllerW getMouseTouchGestureController() {
		return mtg;
	}

	@Override
	protected boolean isDragging() {
		return mtg.isDragging();
	}

	@Override
	protected GeoImage createMeasurementToolImage(int mode, String fileName) {
		if (toolImageW == null) {
			toolImageW = new CreateToolImageW((AppW) app);
		}
		return toolImageW.create(mode, fileName);
	}

	@Override
	public MyImage getRotationImage() {
		HTMLImageElement img = Dom.createImage();
		img.src = GuiResourcesSimple.INSTANCE.rotateIcon().getSafeUri().asString();
		return new MyImageW(img, true);
	}
}

