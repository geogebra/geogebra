package org.geogebra.web.full.gui.util;

import org.geogebra.common.euclidian.CoordSystemListener;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.css.ZoomPanelResources;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.gui.zoompanel.ZoomController;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 *
 */
public class ZoomPanelMow extends FlowPanel
		implements SetLabels, CoordSystemListener {
	private AppW appW;
	private StandardButton dragPadBtn;
	private StandardButton zoomInBtn;
	private StandardButton zoomOutBtn;
	private StandardButton homeBtn;
	private ZoomController zoomController;

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public ZoomPanelMow(AppW app) {
		this.appW = app;
		zoomController = new ZoomController(appW, app.getActiveEuclidianView());
		if (app.getActiveEuclidianView() != null) {
			app.getActiveEuclidianView().getEuclidianController()
					.addZoomerListener(this);
		}
		buildGui();
	}

	private void buildGui() {
		addStyleName("mowZoomPanel");
		addDragPadButton();
		addZoomButtons();
	}

	/**
	 * @return zoom controller
	 */
	public ZoomController getZoomController() {
		return zoomController;
	}

	/**
	 * @return see {@link AppW}
	 */
	public AppW getAppW() {
		return appW;
	}

	/**
	 * @return drag pad button
	 */
	public StandardButton getDragPadBtn() {
		return dragPadBtn;
	}

	/**
	 * remove selected style
	 */
	public void deselectDragBtn() {
		getDragPadBtn().removeStyleName("selected");
		getAppW().setMode(EuclidianConstants.MODE_SELECT_MOW);
	}

	private void addDragPadButton() {
		dragPadBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.move_canvas(), null, 24);
		dragPadBtn.setStyleName("zoomPanelBtn");
		registerFocusable(dragPadBtn, AccessibilityGroup.ViewControlId.ZOOM_NOTES_DRAG_VIEW);
		TestHarness.setAttr(dragPadBtn, "panViewTool");

		FastClickHandler handlerDragPad = new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				getAppW().setMode(EuclidianConstants.MODE_TRANSLATEVIEW);
				getDragPadBtn().addStyleName("selected");
				getAppW().hideMenu();
			}
		};
		dragPadBtn.addFastClickHandler(handlerDragPad);
		add(dragPadBtn);
		// click handler
		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				// to stopPropagation and preventDefault.
			}
		});
	}

	private void registerFocusable(StandardButton dragPadBtn,
			AccessibilityGroup.ViewControlId group) {
		new FocusableWidget(AccessibilityGroup.getViewGroup(getViewId()), group,
				dragPadBtn).attachTo(appW);
	}

	private int getViewId() {
		return appW.getActiveEuclidianView().getViewID();
	}

	/**
	 * Add zoom in/out buttons to GUI
	 */
	private void addZoomButtons() {
		if (!Browser.isMobile()) {
			addZoomInButton();
			addZoomOutButton();
		}
		homeBtn = new StandardButton(
				ZoomPanelResources.INSTANCE.home_zoom_black18(), null, 20);
		registerFocusable(homeBtn, AccessibilityGroup.ViewControlId.ZOOM_NOTES_HOME);
		homeBtn.setStyleName("zoomPanelBtn");
		homeBtn.addStyleName("zoomPanelBtnSmall");
		getZoomController().hideHomeButton(homeBtn);

		homeBtn.addFastClickHandler(source -> {
			getZoomController().onHomePressed();
			deselectDragBtn();
		});

		add(homeBtn);
		// click handler
		ClickStartHandler.initDefaults(this, true, true);
	}

	private void addZoomOutButton() {
		zoomOutBtn = new StandardButton(
				GuiResourcesSimple.INSTANCE.zoom_out(), null, 24);
		zoomOutBtn.setStyleName("zoomPanelBtn");
		registerFocusable(zoomOutBtn, AccessibilityGroup.ViewControlId.ZOOM_NOTES_MINUS);

		zoomOutBtn.addFastClickHandler(source -> {
			getZoomController().onZoomOutPressed();
			deselectDragBtn();
		});

		add(zoomOutBtn);
	}

	private void addZoomInButton() {
		zoomInBtn = new StandardButton(
				GuiResourcesSimple.INSTANCE.zoom_in(), null, 24);
		zoomInBtn.setStyleName("zoomPanelBtn");
		registerFocusable(zoomInBtn, AccessibilityGroup.ViewControlId.ZOOM_NOTES_PLUS);

		zoomInBtn.addFastClickHandler(source -> {
			getZoomController().onZoomInPressed();
			deselectDragBtn();
		});

		add(zoomInBtn);
	}

	/**
	 * Sets translated titles of the buttons.
	 */
	@Override
	public void setLabels() {
		setButtonTitleAndAltText(dragPadBtn, "PanView");
		setButtonTitleAndAltText(homeBtn, "StandardView");
		setButtonTitleAndAltText(zoomOutBtn, "ZoomOut.Tool");
		setButtonTitleAndAltText(zoomInBtn, "ZoomIn.Tool");
	}

	private void setButtonTitleAndAltText(StandardButton btn, String string) {
		if (btn != null) {
			btn.setTitle(appW.getLocalization().getMenu(string));
			btn.setAltText(appW.getLocalization().getMenu(string));
		}
	}

	@Override
	public void onCoordSystemChanged() {
		getZoomController().updateHomeButton(homeBtn);
	}
}
