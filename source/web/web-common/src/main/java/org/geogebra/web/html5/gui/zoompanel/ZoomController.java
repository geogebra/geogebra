package org.geogebra.web.html5.gui.zoompanel;

import java.util.function.Consumer;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.util.StringUtil;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.Position;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.timer.client.Timer;

import elemental2.dom.DomGlobal;
import elemental2.dom.Event;

/**
 * @author csilla
 *
 */
public class ZoomController {

	private AppW app;

	/** after we leave fullscreen, we must reset container position */

	private boolean homeShown;
	private EuclidianView view;

	private final FullScreenState state;

	/**
	 * @param app
	 *            see {@link AppW}
	 * @param view
	 *            euclidian view
	 */
	public ZoomController(AppW app, EuclidianView view) {
		this.app = app;
		this.view = view;
		this.state = app.getFullscreenState();
	}

	/**
	 * @return true if in fullscreen
	 */
	public boolean isFullScreenActive() {
		return state.fullScreenActive;
	}

	/**
	 * @param fullScreenActive
	 *            true if fullscreen
	 * @param fullscreenBtnSelectCB
	 *            callback to update button select state
	 */
	public void setFullScreenActive(boolean fullScreenActive,
			Consumer<Boolean> fullscreenBtnSelectCB) {
		state.fullScreenActive = fullScreenActive;
		fullscreenBtnSelectCB.accept(fullScreenActive);
	}

	/**
	 * @return true if home btn is visible
	 */
	public boolean isHomeShown() {
		return homeShown;
	}

	/** Home button handler. */
	public void onHomePressed() {
		app.closeMenuHideKeyboard();
		view.setStandardView(true);
		app.getAccessibilityManager().focusFirstElement();
	}

	/** Zoom In button handler. */
	public void onZoomInPressed() {
		app.closeMenuHideKeyboard();
		zoomInOut(false);
	}

	/** Zoom Out button handler. */
	public void onZoomOutPressed() {
		app.closeMenuHideKeyboard();
		zoomInOut(true);
	}

	private void zoomInOut(boolean out) {
		double factor = out ? 1d / EuclidianView.MODE_ZOOM_FACTOR
				: EuclidianView.MODE_ZOOM_FACTOR;
		double px = view.getWidth() / 2.0;
		double py = view.getHeight() / 2.0;

		view.getEuclidianController().zoomInOut(factor, 15, px, py);
	}

	/**
	 * Scales the applet to fit the screen.
	 *
	 * @param scaler
	 *            the applet scaler element.
	 * @param container
	 *            content to scale.
	 * @param elem
	 *            element
	 */
	protected void scaleApplet(Element scaler, Element container,
			@CheckForNull Element elem) {
		double scale = 1;
		if (app.isUnbundled()) {
			app.getGgbApi().setSize(NavigatorUtil.getWindowWidth(),
					NavigatorUtil.getWindowHeight());
			Browser.scale(scaler, 1, 0, 0);
		} else {
			double xscale = NavigatorUtil.getWindowWidth() / app.getWidth();
			double yscale = NavigatorUtil.getWindowHeight() / app.getHeight();
			scale = LayoutUtilW.getDeviceScale(xscale, yscale, true);
			Browser.scale(scaler, scale, 0, 0);
			Browser.scale(elem, 1 / scale, 120, 100);
			container.getStyle().setPosition(state.emulated
					? Position.FIXED : Position.ABSOLUTE);
			double marginLeft = 0;
			double marginTop = 0;
			if (xscale > yscale) {
				marginLeft = (NavigatorUtil.getWindowWidth() - app.getWidth() * scale)
						/ 2;
			} else {
				marginTop = (NavigatorUtil.getWindowHeight() - app.getHeight() * scale)
						/ 2;
			}

			if (Browser.isSafariByVendor()) {
				marginLeft /= scale;
				marginTop /= scale;
			}

			scaler.getStyle().setMarginLeft(marginLeft, Unit.PX);
			scaler.getStyle().setMarginTop(marginTop, Unit.PX);
		}
		app.getGeoGebraElement().resetScale();
		app.recalculateEnvironments();
		app.deferredForceResize();
	}

	/**
	 * forces a resize event.
	 */
	protected void dispatchResize() {
		DomGlobal.window.dispatchEvent(new Event("resize"));
	}

	/**
	 * @param elem - element
	 * @param fullscreenBtnSelectCB - fullscreen button select callback
	 */
	public void onExitFullscreen(@CheckForNull  Element elem,
			Consumer<Boolean> fullscreenBtnSelectCB) {
		setFullScreenActive(false, fullscreenBtnSelectCB);
		if (!app.getAppletParameters().getDataParamFitToScreen()) {
			final Element scaler = app.getGeoGebraElement().getParentElement();
			// check for null in case external website removed applet from DOM
			if (scaler != null) {
				scaler.removeClassName("fullscreen");
				scaler.getStyle().setMarginLeft(0, Unit.PX);
				scaler.getStyle().setMarginTop(0, Unit.PX);
				dispatchResize();
				Element container = scaler.getParentElement();
				state.resetStyleAfterFullscreen(container, app);
				double scale = state.getCssScale() > 0 ? state.getCssScale()
						: app.getAppletParameters().getDataParamScale();
				Browser.scale(scaler, scale, 0, 0);
				app.getGeoGebraElement().resetScale();
				app.checkScaleContainer();
				Browser.scale(elem, 1, 0, 0);
			}
		} else {
			Browser.scale(elem, 1, 0, 0);
		}
	}

	/**
	 * Full screen button handler.
	 * @param elem - element
	 * @param fullscreenBtnSelectCB - callback to update button select state
	 */
	public void onFullscreenPressed(final Element elem,
			Consumer<Boolean> fullscreenBtnSelectCB) {
		app.closeMenuHideKeyboard();
		final Element container;
		state.emulated = useEmulatedFullscreen(app);
		if (app.getAppletParameters().getDataParamFitToScreen()) {
			container = null;
			if (!isFullScreenActive()) {
				Timer t = new Timer() {

					@Override
					public void run() {
						onFullscreen(fullscreenBtnSelectCB);
					}
				};
				// delay scaling to make sure scrollbars disappear
				t.schedule(50);
			}
			handleIframeFullscreen(fullscreenBtnSelectCB);
		} else {
			GeoGebraElement geoGebraElement = app.getGeoGebraElement();
			final Element scaler = geoGebraElement.getParentElement();
			container = scaler.getParentElement();
			if (!isFullScreenActive()) {
				state.store(container, app, geoGebraElement.getParentScaleX());
				scaler.addClassName("fullscreen");
				Timer t = new Timer() {

					@Override
					public void run() {
						scaleApplet(scaler, container, elem);
						onFullscreen(fullscreenBtnSelectCB);
					}
				};
				// delay scaling to make sure scrollbars disappear
				t.schedule(50);
			} else {
				if (state.emulated) {
					state.removeTransformOverride();
					container.removeClassName("GeoGebraFullscreenContainer");
					onExitFullscreen(elem, fullscreenBtnSelectCB);
					if (state.getCssScale() != 0) {
						Browser.scale(scaler, state.getCssScale(),
								0, 0);
					}
				}
			}
		}
		if (!state.emulated) {
			setFullScreenActive(!isFullScreenActive(), fullscreenBtnSelectCB);
			Browser.toggleFullscreen(isFullScreenActive(), container);
		}
	}

	private void handleIframeFullscreen(Consumer<Boolean> fullscreenBtnSelectCB) {
		if (isRunningInIframe() && state.emulated) {
			FullScreenHandler fullScreenHandler = app.getVendorSettings().getFullscreenHandler();
			if (fullScreenHandler != null) {
				fullScreenHandler.toggleFullscreen();
				setFullScreenActive(!state.fullScreenActive, fullscreenBtnSelectCB);
			}
		}
	}

	/**
	 * @param app - application
	 * @return whether fullscreen button should be added
	 */
	public static boolean needsFullscreenButton(AppW app) {
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
	 * @param app
	 *            application
	 * @return whether emulated fullscreen mode is needed (enforced by browser
	 *         or applet parameter)
	 */
	public static boolean useEmulatedFullscreen(AppW app) {
		return NavigatorUtil.isiOS()
				|| !StringUtil.empty(app.getAppletParameters().getParamFullscreenContainer());
	}

	/**
	 * @return whether the current window is an iframe embedded in another
	 *         window.
	 */
	protected static boolean isRunningInIframe() {
		return DomGlobal.window != DomGlobal.window.parent;
	}

	/**
	 * Handler that runs on switching to fullscreen.
	 *
	 * @param fullscreenBtnSelectCB
	 *            callback to update button select state
	 */
	void onFullscreen(Consumer<Boolean> fullscreenBtnSelectCB) {
		setFullScreenActive(true, fullscreenBtnSelectCB);
	}

	private void setHomeButtonVisible(StandardButton homeBtn, boolean visible) {
		if (homeBtn == null) {
			return;
		}
		homeShown = visible;
		Dom.toggleClass(homeBtn, "zoomPanelHomeIn", "zoomPanelHomeOut", visible);
		AriaHelper.setHidden(homeBtn, !visible);
	}

	/**
	 * @param homeBtn
	 *            hides home button
	 */
	public void hideHomeButton(StandardButton homeBtn) {
		setHomeButtonVisible(homeBtn, false);
	}

	/**
	 * @param homeBtn
	 *            show/hide home button
	 */
	public void updateHomeButton(StandardButton homeBtn) {
		if (view == null) {
			return;
		}
		if (view.isCoordSystemTranslatedByAnimation()) {
			return;
		}
		setHomeButtonVisible(homeBtn, !view.isStandardView());
	}
}
