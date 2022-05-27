package org.geogebra.web.html5.gui.zoompanel;

import java.util.HashMap;
import java.util.Map.Entry;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.util.StringUtil;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.resources.StyleInjector;
import org.gwtproject.timer.client.Timer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;

import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.HTMLStyleElement;

/**
 * @author csilla
 *
 */
public class ZoomController {
	private HTMLStyleElement transformOverride;
	private AppW app;
	/**
	 * is in fullscreen mode
	 */
	private boolean fullScreenActive = false;
	private double cssScale = 0;
	private GDimension oldSize;
	/** after we leave fullscreen, we must reset container position */
	private HashMap<String, String> containerProps = new HashMap<>();
	private boolean homeShown;
	private EuclidianView view;

	private boolean emulated;

	/**
	 * @param app
	 *            see {@link AppW}
	 * @param view
	 *            euclidian view
	 */
	public ZoomController(AppW app, EuclidianView view) {
		this.app = app;
		this.view = view;
	}

	/**
	 * @return true if in fullscreen
	 */
	public boolean isFullScreenActive() {
		return fullScreenActive;
	}

	/**
	 * @param fullScreenActive
	 *            true if fullscreen
	 * @param fullscreenBtn
	 *            button
	 */
	public void setFullScreenActive(boolean fullScreenActive,
			ToggleButton fullscreenBtn) {
		this.fullScreenActive = fullScreenActive;
		if (fullscreenBtn != null && fullscreenBtn.isSelected() != fullScreenActive) {
			fullscreenBtn.setSelected(fullScreenActive);
		}
	}

	/**
	 * @return css scale
	 */
	public double getCssScale() {
		return cssScale;
	}

	/**
	 * @param cssScale
	 *            css scale
	 */
	public void setCssScale(double cssScale) {
		this.cssScale = cssScale;
	}

	/**
	 * @return old size
	 */
	public GDimension getOldSize() {
		return oldSize;
	}

	/**
	 * @param oldSize
	 *            old size
	 */
	public void setOldSize(GDimension oldSize) {
		this.oldSize = oldSize;
	}

	/**
	 * @return container
	 */
	public HashMap<String, String> getContainerProps() {
		return containerProps;
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
			Element elem) {
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
			container.getStyle().setPosition(emulated
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
	 * @param elem
	 *            element
	 * @param fullscreenButton
	 *            fullscreen button
	 */
	public void onExitFullscreen(Element elem,
			ToggleButton fullscreenButton) {
		setFullScreenActive(false, fullscreenButton);
		if (!app.getAppletParameters().getDataParamFitToScreen()) {
			final Element scaler = app.getGeoGebraElement().getParentElement();
			// check for null in case external website removed applet from DOM
			if (scaler != null) {
				scaler.removeClassName("fullscreen");
				scaler.getStyle().setMarginLeft(0, Unit.PX);
				scaler.getStyle().setMarginTop(0, Unit.PX);
				dispatchResize();
				Element container = scaler.getParentElement();
				resetStyleAfterFullscreen(container);
				double scale = cssScale > 0 ? cssScale
						: app.getAppletParameters().getDataParamScale();
				Browser.scale(scaler, scale, 0, 0);
				app.getGeoGebraElement().resetScale();
				app.checkScaleContainer();
			}
		}
		Browser.scale(elem, 1, 0, 0);
	}

	/**
	 * Resetting position and margins.
	 *
	 * @param container
	 *            to reset.
	 */
	protected void resetStyleAfterFullscreen(Element container) {
		if (container != null) {
			for (Entry<String, String> e : containerProps.entrySet()) {
				if (!StringUtil.empty(e.getValue())) {
					container.getStyle().setProperty(e.getKey(), e.getValue());
				} else {
					container.getStyle().clearProperty(e.getKey());
				}
			}
		}
		if (oldSize != null && app.isUnbundled()) {
			app.getGgbApi().setSize(oldSize.getWidth(), oldSize.getHeight());
		}
	}

	/**
	 * @param container
	 *            container
	 * @param propName
	 *            property name
	 * @param value
	 *            value of property
	 */
	public void setContainerProp(Element container, String propName,
			String value) {
		containerProps.put(propName,
				container.getStyle().getProperty(propName));
		container.getStyle().setProperty(propName, value);
	}

	/**
	 * Full screen button handler.
	 *
	 * @param elem
	 *            element
	 * @param fullscreenBtn
	 *            fullscreen button
	 */
	protected void onFullscreenPressed(final Element elem,
			final ToggleButton fullscreenBtn) {
		app.closeMenuHideKeyboard();
		final Element container;
		emulated = useEmulatedFullscreen(app);
		if (app.getAppletParameters().getDataParamFitToScreen()) {
			container = null;
			if (!isFullScreenActive()) {
				Timer t = new Timer() {

					@Override
					public void run() {
						onFullscreen(fullscreenBtn);
					}
				};
				// delay scaling to make sure scrollbars disappear
				t.schedule(50);
			}
			handleIframeFullscreen(fullscreenBtn);
		} else {
			GeoGebraElement geoGebraElement = app.getGeoGebraElement();
			final Element scaler = geoGebraElement.getParentElement();
			container = scaler.getParentElement();
			if (!isFullScreenActive()) {
				String containerPositionBefore = container.getStyle()
						.getPosition();
				if (StringUtil.empty(containerPositionBefore)) {
					containerPositionBefore = "static";
				}
				containerProps.clear();
				containerProps.put("position", containerPositionBefore);
				setContainerProp(container, "width", "100%");
				setContainerProp(container, "height", "100%");
				setContainerProp(container, "maxWidth", "100%");
				setContainerProp(container, "maxHeight", "100%");
				setContainerProp(container, "marginLeft", "0");
				setContainerProp(container, "marginTop", "0");
				setOldSize(app.getPreferredSize());
				scaler.addClassName("fullscreen");
				setCssScale(geoGebraElement.getParentScaleX());
				if (emulated) {
					overrideParentTransform();
					setContainerProp(container, "left", "0px");
					container.addClassName("GeoGebraFullscreenContainer");
				}
				Timer t = new Timer() {

					@Override
					public void run() {
						scaleApplet(scaler, container, elem);
						onFullscreen(fullscreenBtn);
					}
				};
				// delay scaling to make sure scrollbars disappear
				t.schedule(50);
			} else {
				if (emulated) {
					removeTransformOverride();
					container.removeClassName("GeoGebraFullscreenContainer");
					onExitFullscreen(elem, fullscreenBtn);
					if (getCssScale() != 0) {
						Browser.scale(scaler, getCssScale(),
								0, 0);
					}
				}
			}
		}
		if (!emulated) {
			setFullScreenActive(!isFullScreenActive(), fullscreenBtn);
			Browser.toggleFullscreen(isFullScreenActive(), container);
		}
	}

	private void handleIframeFullscreen(ToggleButton fullscreenBtn) {
		if (isRunningInIframe() && emulated) {
			FullScreenHandler fullScreenHandler = app.getVendorSettings().getFullscreenHandler();
			if (fullScreenHandler != null) {
				fullScreenHandler.toggleFullscreen();
				setFullScreenActive(!fullScreenActive, fullscreenBtn);
			}
		}
	}

	/**
	 * Remove the inline style for transform overriding
	 */
	protected void removeTransformOverride() {
		if (transformOverride != null) {
			transformOverride.remove();
		}
	}

	private void overrideParentTransform() {
		transformOverride = StyleInjector.injectStyleSheet(
						"*:not(.ggbTransform){transform: none !important;}");
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
	 * @param fullscreenBtn
	 *            fullscreen button
	 */
	void onFullscreen(ToggleButton fullscreenBtn) {
		setFullScreenActive(true, fullscreenBtn);
		fullscreenBtn.getElement().focus();
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
