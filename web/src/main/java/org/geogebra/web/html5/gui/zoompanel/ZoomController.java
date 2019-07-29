package org.geogebra.web.html5.gui.zoompanel;

import java.util.HashMap;
import java.util.Map.Entry;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArticleElementInterface;
import org.geogebra.web.resources.StyleInjector;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.StyleElement;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

/**
 * @author csilla
 *
 */
public class ZoomController {
	private StyleElement transformOverride;
	private AppW app;
	/**
	 * is in fullscreen mode
	 */
	boolean fullScreenActive = false;
	private double cssScale = 0;
	private GDimension oldSize;
	/** after we leave fullscreen, we must reset container position */
	private HashMap<String, String> containerProps = new HashMap<>();
	private boolean homeShown;
	private EuclidianView view;

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
	 *            true if fillscreen
	 * @param fullscreenBtn
	 *            button
	 */
	public void setFullScreenActive(boolean fullScreenActive,
			StandardButton fullscreenBtn) {
		this.fullScreenActive = fullScreenActive;
		if (fullscreenBtn != null) {
			fullscreenBtn.setDown(fullScreenActive);
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
		if (app.isMenuShowing()) {
			app.toggleMenu();
		}
		view.setStandardView(true);
		app.getAccessibilityManager().focusFirstElement();
	}

	/** Zoom In button handler. */
	public void onZoomInPressed() {
		if (app.isMenuShowing()) {
			app.toggleMenu();
		}
		view.getEuclidianController().zoomInOut(false,
				false);
	}

	/** Zoom Out button handler. */
	public void onZoomOutPressed() {
		if (app.isMenuShowing()) {
			app.toggleMenu();
		}
		view.getEuclidianController().zoomInOut(false,
				true);
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
			app.getGgbApi().setSize(Window.getClientWidth(),
					Window.getClientHeight());
			Browser.scale(scaler, 1, 0, 0);
		} else {
			double xscale = Window.getClientWidth() / app.getWidth();
			double yscale = Window.getClientHeight() / app.getHeight();
			scale = LayoutUtilW.getDeviceScale(xscale, yscale, true);
			Browser.scale(scaler, scale, 0, 0);
			Browser.scale(elem, 1 / scale, 120, 100);
			container.getStyle().setPosition(useEmulatedFullscreen(app)
					? Position.FIXED : Position.ABSOLUTE);
			double marginLeft = 0;
			double marginTop = 0;
			if (xscale > yscale) {
				marginLeft = (Window.getClientWidth() - app.getWidth() * scale)
						/ 2;
			} else {
				marginTop = (Window.getClientHeight() - app.getHeight() * scale)
						/ 2;
			}

			if (Browser.isSafariByVendor()) {
				marginLeft /= scale;
				marginTop /= scale;
			}

			scaler.getStyle().setMarginLeft(marginLeft, Unit.PX);
			scaler.getStyle().setMarginTop(marginTop, Unit.PX);
		}
		app.getArticleElement().resetScale(scale);
		app.recalculateEnvironments();
		app.deferredForceResize();
	}

	/**
	 * forces a resize event.
	 */
	protected native void dispatchResize() /*-{
		if (navigator.userAgent.indexOf('MSIE') !== -1
				|| navigator.appVersion.indexOf('Trident/') > 0) {
			var evt = document.createEvent('UIEvents');
			evt.initUIEvent('resize', true, false, window, 0);
			window.dispatchEvent(evt);
		} else {
			window.dispatchEvent(new Event('resize'));

		}
	}-*/;

	/**
	 * @param elem
	 *            element
	 * @param fullscreenButton
	 *            fullscreen button
	 */
	public void onExitFullscreen(Element elem,
			StandardButton fullscreenButton) {
		setFullScreenActive(false, fullscreenButton);
		if (!app.getArticleElement().getDataParamFitToScreen()) {
			final Element scaler = app.getArticleElement().getParentElement();
			// check for null in case external website removed applet from DOM
			if (scaler != null) {
				scaler.removeClassName("fullscreen");
				scaler.getStyle().setMarginLeft(0, Unit.PX);
				scaler.getStyle().setMarginTop(0, Unit.PX);
				dispatchResize();
				Element container = scaler.getParentElement();
				resetStyleAfterFullscreen(container);
				double scale = cssScale > 0 ? cssScale
						: app.getArticleElement().getDataParamScale();
				Browser.scale(scaler, scale, 0, 0);
				app.getArticleElement().resetScale(scale);
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
			final StandardButton fullscreenBtn) {
		if (app.isMenuShowing()) {
			app.toggleMenu();
		}
		final Element container;
		final boolean emulated = useEmulatedFullscreen(app);
		if (app.getArticleElement().getDataParamFitToScreen()) {
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
			ArticleElementInterface ae = app.getArticleElement();
			final Element scaler = ae.getParentElement();
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
				setCssScale(ae.getParentScaleX());
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

	private void handleIframeFullscreen(StandardButton fullscreenBtn) {
		if (isRunningInIframe() && useEmulatedFullscreen(app)) {
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
			transformOverride.removeFromParent();
		}
	}

	private void overrideParentTransform() {
		transformOverride = new StyleInjector.StyleInjectorImpl()
				.injectStyleSheet(
						"*:not(.ggbTransform){transform: none !important;}");
	}

	/**
	 * @param app
	 *            application
	 * @return whether emulated fullscreen mode is needed (enforced by browser
	 *         or applet parameter)
	 */
	public static boolean useEmulatedFullscreen(AppW app) {
		return Browser.isiOS() || !StringUtil
				.empty(app.getArticleElement().getParamFullscreenContainer());
	}

	/**
	 * @return whether the current window is an iframe embedded in another
	 *         window.
	 */
	protected static native boolean isRunningInIframe() /*-{
		return $wnd != $wnd.parent;
	}-*/;

	/**
	 * Handler that runs on switching to fullscreen.
	 *
	 * @param fullscreenBtn
	 *            fullscreen button
	 */
	void onFullscreen(StandardButton fullscreenBtn) {
		setFullScreenActive(true, fullscreenBtn);
		fullscreenBtn.getElement().focus();
	}

	/**
	 * @param homeBtn
	 *            shows home button
	 */
	void showHomeButton(StandardButton homeBtn) {
		if (homeBtn == null) {
			return;
		}
		homeShown = true;
		homeBtn.addStyleName("zoomPanelHomeIn");
		homeBtn.removeStyleName("zoomPanelHomeOut");
		AriaHelper.setHidden(homeBtn, false);
	}

	/**
	 * @param homeBtn
	 *            hides home button
	 */
	public void hideHomeButton(StandardButton homeBtn) {
		if (homeBtn == null) {
			return;
		}
		homeShown = false;
		homeBtn.addStyleName("zoomPanelHomeOut");
		homeBtn.removeStyleName("zoomPanelHomeIn");
		AriaHelper.setHidden(homeBtn, true);
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
		if (view.isStandardView()) {
			hideHomeButton(homeBtn);
		} else {
			showHomeButton(homeBtn);
		}
	}
}
