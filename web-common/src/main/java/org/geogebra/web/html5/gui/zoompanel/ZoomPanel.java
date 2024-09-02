package org.geogebra.web.html5.gui.zoompanel;

import org.geogebra.common.euclidian.CoordSystemListener;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.css.ZoomPanelResources;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.FastClickHandler;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.Display;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.DomGlobal;

/**
 * Place of the zoom buttons.
 * 
 * @author zbynek, laszlo
 *
 */
public class ZoomPanel extends FlowPanel implements CoordSystemListener {
	private StandardButton homeBtn;
	private StandardButton zoomInBtn;
	private StandardButton zoomOutBtn;
	/**
	 * enter/exit fullscreen mode
	 */
	private ToggleButton fullscreenBtn;

	/** application */
	private AppW app;
	private final EuclidianView view;

	private ZoomController zoomController;
	private boolean zoomButtonsVisible;
	private LocalizationW loc;

	/**
	 *
	 * @param view
	 *            The Euclidian View to put zoom buttons onto.
	 * @param app
	 *            see {@link AppW}
	 * @param rightBottom
	 *            whether this is placed in the right bottom
	 * @param zoomable
	 *            whether zoom buttons are allowed in this view
	 */
	public ZoomPanel(EuclidianView view, AppW app, boolean rightBottom,
			boolean zoomable) {
		this.view = view;
		this.app = app;
		loc = app.getLocalization();
		zoomController = new ZoomController(app, view);
		if (view != null) {
			view.getEuclidianController().addZoomerListener(this);
		}
		setStyleName("zoomPanel");
		updatePosition(false);
		if (ZoomPanel.needsZoomButtons(app)
				&& !app.isWhiteboardActive() && zoomable) {
			addZoomButtons();
		}

		if (ZoomPanel.needsFullscreenButton(app) && rightBottom) {
			addFullscreenButton();
		}

		setLabels();
	}

	/**
	 * @param isAbovePageControlButton whether page control button is showing
	 */
	public void updatePosition(boolean isAbovePageControlButton) {
		Dom.toggleClass(this, "zoomPanelWithPageControl",
				"zoomPanelPosition", isAbovePageControlButton);
	}

	/**
	 * @return controller for zoom panel and its buttons
	 */
	public ZoomController getZoomController() {
		return zoomController;
	}

	/**
	 * Updates fullscreen button and article.
	 */
	public void updateFullscreen() {
		AppletParameters ae = app.getAppletParameters();
		GeoGebraElement element = app.getGeoGebraElement();
		if (!ae.getDataParamApp() && isFullScreen()) {
			getZoomController().scaleApplet(element.getParentElement(),
					element.getParentElement().getParentElement(),
					getPanelElement());
		}
		if (ae.getDataParamApp() && fullscreenBtn != null) {
			fullscreenBtn.setVisible(
					isFullScreen() || !Browser.isCoveringWholeScreen());
		}
	}

	/**
	 * add fullscreen button
	 */
	public void addFullscreenButton() {
		fullscreenBtn = new ToggleButton(ZoomPanelResources.INSTANCE.fullscreen_black18(),
				ZoomPanelResources.INSTANCE.fullscreen_exit_black18());
		fullscreenBtn.setStyleName("zoomPanelBtn");
		registerFocusable(fullscreenBtn, AccessibilityGroup.ViewControlId.FULL_SCREEN);

		fullscreenBtn.addFastClickHandler(source -> {
			getZoomController().onFullscreenPressed(getPanelElement(),
					fullscreenBtn);
			setFullScreenAuralText();
		});
		fullscreenBtn.setSelected(Browser.isFullscreen());

		app.getGlobalHandlers().addEventListener(DomGlobal.document,
		Browser.getFullscreenEventName(), event -> {
			if (!Browser.isFullscreen()) {
				onExitFullscreen();
			}
		});
		add(fullscreenBtn);
	}

	/**
	 * @return we need getElement() for zoom controller
	 */
	public Element getPanelElement() {
		return getElement();
	}

	/**
	 * Handler that runs on exiting to fullscreen.
	 */
	void onExitFullscreen() {
		// we may have multiple zoom panels; if this one doesn't have FS button,
		// it shouldn't handle FS
		if (fullscreenBtn != null) {
			getZoomController().onExitFullscreen(getElement(), fullscreenBtn);
		}
	}

	/**
	 * Add zoom in/out buttons to GUI
	 */
	public void addZoomButtons() {
		homeBtn = new StandardButton(
				ZoomPanelResources.INSTANCE.home_zoom_black18(),
				null, 20);
		homeBtn.setStyleName("zoomPanelBtn");
		homeBtn.addStyleName("zoomPanelBtnSmall");
		getZoomController().hideHomeButton(homeBtn);
		FastClickHandler handlerHome = source -> getZoomController().onHomePressed();
		homeBtn.addFastClickHandler(handlerHome);
		add(homeBtn);
		registerFocusable(homeBtn, AccessibilityGroup.ViewControlId.ZOOM_PANEL_HOME);
		if (!NavigatorUtil.isMobile()) {
			addZoomInButton();
			registerFocusable(zoomInBtn, AccessibilityGroup.ViewControlId.ZOOM_PANEL_PLUS);
			addZoomOutButton();
			registerFocusable(zoomOutBtn, AccessibilityGroup.ViewControlId.ZOOM_PANEL_MINUS);
		}
	}

	private void registerFocusable(Widget btn, AccessibilityGroup.ViewControlId group) {
		new FocusableWidget(AccessibilityGroup.getViewGroup(getViewID()), group, btn).attachTo(app);
	}

	private void addZoomOutButton() {
		zoomOutBtn = new StandardButton(
					GuiResourcesSimple.INSTANCE.zoom_out(), null, 24
		);
		zoomOutBtn.setStyleName("zoomPanelBtn");

		FastClickHandler handlerZoomOut = source -> getZoomController().onZoomOutPressed();
		zoomOutBtn.addFastClickHandler(handlerZoomOut);
		add(zoomOutBtn);
	}

	private void addZoomInButton() {
		zoomInBtn = new StandardButton(
					GuiResourcesSimple.INSTANCE.zoom_in(), null, 24
		);
		zoomInBtn.setStyleName("zoomPanelBtn");

		FastClickHandler handlerZoomIn = source -> getZoomController().onZoomInPressed();
		zoomInBtn.addFastClickHandler(handlerZoomIn);
		add(zoomInBtn);
	}

	/**
	 * @return view connected to this panel
	 */
	protected EuclidianViewInterfaceSlim getEuclidianView() {
		return view;
	}

	@Override
	public void onCoordSystemChanged() {
		getZoomController().updateHomeButton(homeBtn);
	}

	/**
	 * Sets translated titles of the buttons.
	 */
	public void setLabels() {
		setFullScreenAuralText();
		setZoomAuralText(homeBtn, true, "StandardView", "Home button selected");
		setZoomAuralText(zoomOutBtn, fullscreenBtn != null, "ZoomOut.Tool",
				"Zoom out button selected");
		setZoomAuralText(zoomInBtn, true, "ZoomIn.Tool", "Zoom in button selected");
	}

	/**
	 * Update title / screen reader description for fullscreen button
	 */
	void setFullScreenAuralText() {
		if (fullscreenBtn == null) {
			return;
		}
		ScreenReaderBuilder sb = new ScreenReaderBuilder(loc);
		if (isFullScreen()) {
			sb.appendMenuDefault("FullscreenExitButtonSelected",
					"Full screen button selected (currently full screen)");
		} else {
			sb.appendMenuDefault("FullscreenButtonSelected",
					"Full screen button selected (currently not full screen)");
		}
		if (!Browser.needsAccessibilityView()) {
			addFullscreenKeyboardControls(sb);
		}
		setButtonTitleAndAltText(fullscreenBtn, loc.getMenu("Fullscreen"), sb.toString());
	}

	private void addFullscreenKeyboardControls(ScreenReaderBuilder sb) {
		addSpaceControl(sb);
		sb.append(loc.getMenuDefault("PressTabToSelectNext", "Press tab to select next object"));
		sb.endSentence();
	}

	private void setZoomAuralText(StandardButton btn, boolean controlNext,
			String transKey, String auralDefault) {
		if (btn == null) {
			return;
		}
		String title = loc.getMenuDefault(transKey, auralDefault);

		ScreenReaderBuilder sb = new ScreenReaderBuilder(loc);
		sb.append(title);
		if (!Browser.needsAccessibilityView()) {
			addZoomKeyboardControls(sb, controlNext);
		}

		setButtonTitleAndAltText(btn, title, sb.toString());
	}

	private void addZoomKeyboardControls(ScreenReaderBuilder sb, boolean controlNext) {
		addSpaceControl(sb);
		if (controlNext) {
			sb.appendMenuDefault("PressTabToSelectControls",
					"Press tab to select controls");
		} else {
			sb.appendMenuDefault("PressTabToSelectNext",
					"Press tab to select next object");
		}
	}

	private void addSpaceControl(ScreenReaderBuilder sb) {
		sb.appendSpace();
		sb.appendMenuDefault("PressSpaceToActivate", "press space to activate");
		sb.endSentence();
	}

	private static void setButtonTitleAndAltText(Widget btn, String dataTitle,
			String ariaLabel) {
		if (btn != null) {
			btn.getElement().setAttribute("data-title", dataTitle);
			btn.getElement().setAttribute("aria-label", ariaLabel);
		}
	}

	private static boolean needsFullscreenButton(AppW app) {
		if (app.getAppletParameters().getDataParamApp()) {
			return ZoomController.isRunningInIframe() || !NavigatorUtil.isMobile();
		} else {
			if (!app.getAppletParameters().getDataParamShowFullscreenButton()) {
				return false;
			}

			return !(NavigatorUtil.isiOS() && ZoomController.isRunningInIframe());
		}
	}

	/**
	 *
	 * @param app see {@link AppW}
	 * @return whether zoom buttons needed
	 */
	public static boolean needsZoomButtons(AppW app) {
		return (app.getAppletParameters().getDataParamShowZoomButtons()
				|| app.getAppletParameters().getDataParamApp())
				&& app.isShiftDragZoomEnabled();
	}

	/**
	 * Checks if the current app needs zoom panel or not.
	 * 
	 * @param app
	 *            the application to check.
	 * @return true if app needs zoom panel.
	 */
	public static boolean neededFor(AppW app) {
		return (needsZoomButtons(app) && !app.isWhiteboardActive())
			|| needsFullscreenButton(app);
	}

	/**
	 * @return whether fullscreen is active
	 */
	public boolean isFullScreen() {
		return getZoomController().isFullScreenActive();
	}

	/**
	 * @param isFullscreen whether fullscreen should be active
	 */
	public void setFullScreen(boolean isFullscreen) {
		getZoomController().setFullScreenActive(isFullscreen, fullscreenBtn);
	}

	/**
	 * @return whether home button is shown
	 */
	public boolean isHomeShown() {
		return getZoomController().isHomeShown();
	}

	/**
	 * Hides buttons that don't fit to the height
	 * 
	 * @param height
	 *            max height
	 */
	public void setMaxHeight(int height) {
		setHidden(height < 60);
		zoomButtonsVisible = height >= 200;
		if (zoomInBtn != null) {
			zoomInBtn.setVisible(zoomButtonsVisible);
		}
		if (zoomOutBtn != null) {
			zoomOutBtn.setVisible(zoomButtonsVisible);
		}
		if (homeBtn != null) {
			// change style directly, aria-hidden + visibility should depend on zoom
			Display display = zoomButtonsVisible ? Display.BLOCK : Display.NONE;
			homeBtn.getElement().getStyle().setDisplay(display);
		}
	}

	/**
	 * Hide this using CSS class
	 * 
	 * @param hidden
	 *            whether this should be hidden
	 */
	public void setHidden(boolean hidden) {
		Dom.toggleClass(this, "hidden", hidden);
	}

	/**
	 * @return ID of the associated view
	 */
	public int getViewID() {
		return view == null ? -1 : view.getViewID();
	}

	/**
	 * @param bottomRight
	 *            whether the zoom panel's parent is now bottom right panel
	 */
	public void updateFullscreenVisibility(boolean bottomRight) {
		if (ZoomPanel.needsFullscreenButton(app) && bottomRight) {
			if (fullscreenBtn == null) {
				addFullscreenButton();
			} else {
				add(fullscreenBtn);
			}
		} else if (fullscreenBtn != null) {
			fullscreenBtn.removeFromParent();
			fullscreenBtn = null;
		}
	}
}
