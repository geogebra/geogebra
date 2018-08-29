package org.geogebra.web.html5.gui.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.euclidian.CoordSystemListener;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.layout.GUITabs;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.css.ZoomPanelResources;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.TabHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArticleElementInterface;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Place of the zoom buttons.
 * 
 * @author zbynek, laszlo
 *
 */
public class ZoomPanel extends FlowPanel
		implements CoordSystemListener, TabHandler {

	private StandardButton homeBtn;
	private StandardButton zoomInBtn;
	private StandardButton zoomOutBtn;
	/**
	 * enter/exit fullscreen mode
	 */
	StandardButton fullscreenBtn;
	/**
	 * is in fullscreen mode
	 */
	boolean fullScreenActive = false;

	/** application */
	private AppW app;
	private EuclidianView view;
	/** after we leave fullscreen, we must reset container position */
	private HashMap<String, String> containerProps = new HashMap<>();
	private GDimension oldSize;

	private double cssScale = 0;
	private List<StandardButton> buttons = null;
	private boolean homeShown;

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
		if (view != null) {
			view.getEuclidianController().addZoomerListener(this);
		}
		setStyleName("zoomPanel");
		if (app.isWhiteboardActive()) {
			if (app.has(Feature.MOW_MULTI_PAGE)) {
				addStyleName("zoomPanelWithPageControl");
			} else {
				addStyleName("zommPanelMow");
			}
		} else {
			addStyleName("zoomPanelPosition");
		}
		if (ZoomPanel.needsZoomButtons(app) && zoomable) {
			addZoomButtons();
		}
		if (ZoomPanel.needsFullscreenButton(app) && rightBottom) {
			addFullscreenButton();
		}
		setLabels();
		buttons = Arrays.asList(homeBtn, zoomInBtn, zoomOutBtn,
					fullscreenBtn);
		setTabIndexes();
	}

	/**
	 * Updates fullscreen button and article.
	 */
	public void updateFullscreen() {
		ArticleElementInterface ae = app.getArticleElement();
		if (!ae.getDataParamApp() && fullScreenActive) {
			scaleApplet(ae.getParentElement(),
					ae.getParentElement().getParentElement());
		}
		if (ae.getDataParamApp() && fullscreenBtn != null) {
			fullscreenBtn.setVisible(
					fullScreenActive || !Browser.isCoveringWholeScreen());
		}
	}

	/**
	 * add fullscreen button
	 */
	public void addFullscreenButton() {
		fullscreenBtn = new StandardButton(
				ZoomPanelResources.INSTANCE.fullscreen_black18(), null, 24,
				app);
		fullscreenBtn.getDownFace()
				.setImage(new NoDragImage(ZoomPanelResources.INSTANCE
						.fullscreen_exit_black18(), 24));

		fullscreenBtn.setStyleName("zoomPanelBtn");

		FastClickHandler handlerFullscreen = new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				onFullscreenPressed();
			}
		};

		fullscreenBtn.addTabHandler(this);

		fullscreenBtn.addFastClickHandler(handlerFullscreen);
		Browser.addFullscreenListener(new AsyncOperation<String>() {

			@Override
			public void callback(String obj) {
				if (!"true".equals(obj)) {
					onExitFullscreen();
				}
			}
		});
		add(fullscreenBtn);
	}

	/**
	 * Handler that runs on switching to fullscreen.
	 */
	void onFullscreen() {
		fullScreenActive = true;
		fullscreenBtn
				.setIcon(ZoomPanelResources.INSTANCE.fullscreen_exit_black18());
	}

	/**
	 * Handler that runs on exiting to fullscreen.
	 */
	void onExitFullscreen() {
		fullScreenActive = false;
		fullscreenBtn.setIcon(ZoomPanelResources.INSTANCE.fullscreen_black18());
		if (!app.getArticleElement().getDataParamFitToScreen()) {

			final Element scaler = app.getArticleElement().getParentElement();

			scaler.removeClassName("fullscreen");
			scaler.getStyle().setMarginLeft(0, Unit.PX);
			scaler.getStyle().setMarginTop(0, Unit.PX);
			dispatchResize();
			Element container = scaler.getParentElement();
			resetStyleAfterFullscreen(container);
			double scale = cssScale > 0 ? cssScale
					: app.getArticleElement().getDataParamScale();
			Browser.scale(scaler,
					scale,
					0, 0);
			app.getArticleElement().resetScale(scale);
			app.checkScaleContainer();
		}
		Browser.scale(getElement(), 1, 0, 0);
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
				}
			}
		}
		if (oldSize != null && app.isUnbundled()) {
			app.getGgbApi().setSize(oldSize.getWidth(), oldSize.getHeight());
		}
	}

	/**
	 * Add zoom in/out buttons to GUI
	 */
	public void addZoomButtons() {
		homeBtn = new StandardButton(
				ZoomPanelResources.INSTANCE.home_zoom_black18(),
				null, 20, app);
		homeBtn.setStyleName("zoomPanelBtn");
		homeBtn.addStyleName("zoomPanelBtnSmall");
		hideHomeButton();
		FastClickHandler handlerHome = new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				onHomePressed();
			}
		};
		homeBtn.addFastClickHandler(handlerHome);

		homeBtn.addTabHandler(this);

		add(homeBtn);
		if (!Browser.isMobile()) {
			addZoomInButton();
			addZoomOutButton();
		}

		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				// to stopPropagation and preventDefault.
			}
		});
	}

	private void addZoomOutButton() {
		zoomOutBtn = new StandardButton(
					ZoomPanelResources.INSTANCE.zoomout_black24(), null, 24,
					app);
		zoomOutBtn.setStyleName("zoomPanelBtn");

		FastClickHandler handlerZoomOut = new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				onZoomOutPressed();
			}
		};
		zoomOutBtn.addFastClickHandler(handlerZoomOut);
		add(zoomOutBtn);
	}

	private void addZoomInButton() {
		zoomInBtn = new StandardButton(
					ZoomPanelResources.INSTANCE.zoomin_black24(), null, 24,
					app);
		zoomInBtn.setStyleName("zoomPanelBtn");

		FastClickHandler handlerZoomIn = new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				onZoomInPressed();
			}
		};
		zoomInBtn.addFastClickHandler(handlerZoomIn);
		zoomInBtn.addTabHandler(this);
		add(zoomInBtn);
	}

	/**
	 * @return view connected to this panel
	 */
	protected EuclidianViewInterfaceSlim getEuclidianView() {
		return view;
	}

	/**
	 * Shows home button.
	 */
	void showHomeButton() {
		if (homeBtn == null) {
			return;
		}
		homeShown = true;
		homeBtn.addStyleName("zoomPanelHomeIn");
		homeBtn.removeStyleName("zoomPanelHomeOut");
		AriaHelper.setHidden(homeBtn, false);
	}

	/**
	 * Hides home button.
	 */
	void hideHomeButton() {
		if (homeBtn == null) {
			return;
		}
		homeShown = false;
		homeBtn.addStyleName("zoomPanelHomeOut");
		homeBtn.removeStyleName("zoomPanelHomeIn");
		AriaHelper.setHidden(homeBtn, true);
	}

	private void updateHomeButton() {
		if (app.getActiveEuclidianView().isCoordSystemTranslatedByAnimation()) {
			return;
		}
		if (app.getActiveEuclidianView().isStandardView()) {
			hideHomeButton();
		} else {
			showHomeButton();
		}
	}

	@Override
	public void onCoordSystemChanged() {
		updateHomeButton();
	}

	/**
	 * forces a resize event.
	 */
	protected native void dispatchResize() /*-{
		$wnd.dispatchEvent(new Event("resize"));
	}-*/;

	/** Home button handler. */
	protected void onHomePressed() {
		app.getActiveEuclidianView().setStandardView(true);
		app.getAccessibilityManager().focusMenu();
	}

	/** Zoom In button handler. */
	protected void onZoomInPressed() {
		getEuclidianView().getEuclidianController().zoomInOut(false, false);
	}

	/** Zoom Out button handler. */
	protected void onZoomOutPressed() {
		getEuclidianView().getEuclidianController().zoomInOut(false, true);
	}

	/** Full screen button handler. */
	protected void onFullscreenPressed() {
		final Element container;
		final boolean ipad = Browser.isIPad();
		if (app.getArticleElement().getDataParamFitToScreen()) {
			container = null;
		} else {
			ArticleElementInterface ae = app.getArticleElement();
			final Element scaler = ae.getParentElement();
			container = scaler.getParentElement();
			if (!fullScreenActive) {
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
				oldSize = app.getPreferredSize();
				scaler.addClassName("fullscreen");
				cssScale = ae.getParentScaleX();
				if (ipad) {
					setContainerProp(container, "left", "0px");
					scaler.addClassName("fullscreen-ipad");
				}

				Timer t = new Timer() {

					@Override
					public void run() {
						scaleApplet(scaler, container);
						onFullscreen();
					}
				};
				// delay scaling to make sure scrollbars disappear
				t.schedule(50);
			} else {
				if (ipad) {
					scaler.removeClassName("fullscreen-ipad");
					onExitFullscreen();
					if (cssScale != 0) {
						Browser.scale(scaler, cssScale, 0, 0);
					}
				}
			}
		}

		if (!ipad) {
			fullScreenActive = !fullScreenActive;
			Browser.toggleFullscreen(fullScreenActive, container);
		}
	}

	private void setContainerProp(Element container, String propName,
			String value) {
		containerProps.put(propName, container.getStyle().getProperty(propName));
		container.getStyle().setProperty(propName, value);
	}

	/**
	 * Scales the applet to fit the screen.
	 * 
	 * @param scaler
	 *            the applet scaler element.
	 * @param container
	 *            content to scale.
	 */
	protected void scaleApplet(Element scaler, Element container) {
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
			Browser.scale(getElement(), 1 / scale, 120, 100);
			container.getStyle().setPosition(Position.ABSOLUTE);
			double marginLeft = 0;
			double marginTop = 0;
			if (xscale > yscale) {
				marginLeft = (Window.getClientWidth() - app.getWidth() * scale)
						/ 2;
			} else {
				marginTop = (Window.getClientHeight() - app.getHeight() * scale)
						/ 2;
			}
			scaler.getStyle().setMarginLeft(marginLeft, Unit.PX);
			scaler.getStyle().setMarginTop(marginTop, Unit.PX);
		}
		app.getArticleElement().resetScale(scale);
		app.recalculateEnvironments();
	}

	/**
	 * Sets translated titles of the buttons.
	 */
	public void setLabels() {
		setButtonTitleAndAltText(fullscreenBtn, "Fullscreen");
		setButtonTitleAndAltText(homeBtn, "StandardView");
		setButtonTitleAndAltText(zoomOutBtn, "ZoomOut.Tool");
		setButtonTitleAndAltText(zoomInBtn, "ZoomIn.Tool");
	}

	private void setButtonTitleAndAltText(StandardButton btn, String string) {
		if (btn != null) {
			btn.setTitle(app.getLocalization().getMenu(string));
			btn.setAltText(app.getLocalization().getMenu(string));
		}
	}

	private static boolean needsFullscreenButton(AppW app) {
		return app.getArticleElement().getDataParamShowFullscreenButton()
				|| (app.getArticleElement().getDataParamApp()
						&& !Browser.isMobile());
	}

	private static boolean needsZoomButtons(AppW app) {
		return (app.getArticleElement().getDataParamShowZoomButtons()
				|| app.getArticleElement().getDataParamApp())
				&& app.isShiftDragZoomEnabled() && !app.isWhiteboardActive();
	}

	/**
	 * Checks if the current app needs zoom panel or not.
	 * 
	 * @param app
	 *            the application to check.
	 * @return true if app needs zoom panel.
	 */
	public static boolean neededFor(AppW app) {
		return needsZoomButtons(app) || needsFullscreenButton(app);
	}

	/**
	 * 
	 * @return the minimum height that is needed to display zoomPanel.
	 */
	public int getMinHeight() {
		return needsZoomButtons(app) ? 200 : 60;
	}

	/**
	 * Sets tab order for header buttons.
	 */
	public void setTabIndexes() {
		int tabIndex = GUITabs.ZOOM;
		for (StandardButton btn : buttons) {
			if (btn != null) {
				btn.setTabIndex(tabIndex);
				tabIndex++;
			}
		}
	}

	@Override
	public boolean onTab(Widget source, boolean shiftDown) {
		if (source == getFirstButton() && shiftDown) {
			app.getAccessibilityManager().focusPrevious(this);
			return true;
		} else if (source == getLastButton() && !shiftDown) {
			app.getAccessibilityManager().focusNext(this);
			return true;
		}

		return false;
	}

	/** Focus the first available button on zoom panel. */
	public void focusFirstButton() {
		Widget btn = getFirstButton();
		if (btn != null) {
			btn.getElement().focus();
		}
	}

	/** Focus the last available button on zoom panel. */
	public void focusLastButton() {
		Widget btn = getLastButton();
		if (btn != null) {
			btn.getElement().focus();
		}
	}

	private Widget getFirstButton() {
		if (homeBtn != null && homeShown) {
			return homeBtn;
		}

		if (zoomInBtn != null) {
			return zoomInBtn;
		}

		return fullscreenBtn;
	}

	private Widget getLastButton() {
		if (fullscreenBtn != null) {
			return fullscreenBtn;
		}

		if (zoomOutBtn != null) {
			return zoomOutBtn;
		}

		if (homeBtn != null && homeShown) {
			return homeBtn;
		}

		return null;
	}

	/**
	 * @return whether fullscreen is active
	 */
	public boolean isFullScreen() {
		return this.fullScreenActive;
	}
}
