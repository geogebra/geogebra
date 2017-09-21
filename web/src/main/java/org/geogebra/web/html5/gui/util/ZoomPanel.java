package org.geogebra.web.html5.gui.util;

import org.geogebra.common.euclidian.CoordSystemListener;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.css.ZoomPanelResources;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.StringHandler;
import org.geogebra.web.html5.util.ArticleElement;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ZoomPanel extends FlowPanel implements CoordSystemListener {

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
	boolean isFullScreen = false;

	private FlowPanel zoomPanel = this;
	private AppW app;
	private EuclidianView view;
	/** after we leave fullscreen, we must reset container position */
	private String containerPositionBefore;
	private String containerMarginLeft, containerMarginTop;

	public ZoomPanel(EuclidianView view) {
		this.view = view;
		this.app = (AppW) view.getApplication();
		view.getEuclidianController().setZoomerListener(this);
		setStyleName("zoomPanel");
		if (ZoomPanel.needsZoomButtons(app)) {
			addZoomButtons();
		}
		if (ZoomPanel.needsFullscreenButton(app)) {
			addFullscreenButton();
		}
		setLabels();

	}

	public void updateFullscreen() {
		ArticleElement ae = app.getArticleElement();
		if (!ae.getDataParamApp() && isFullScreen) {
			scaleApplet(ae.getParentElement(),
					ae.getParentElement().getParentElement());
		}
		if (ae.getDataParamApp() && fullscreenBtn != null) {
			fullscreenBtn.setVisible(
					isFullScreen || !Browser.isCoveringWholeScreen());
		}
	}

	/**
	 * add fullscreen button
	 */
	public void addFullscreenButton() {
		fullscreenBtn = new StandardButton(
				new ImageResourcePrototype(null,
						ZoomPanelResources.INSTANCE.fullscreen_black18()
								.getSafeUri(),
						0, 0, 18, 18, false, false),
				app);
		fullscreenBtn.getDownFace()
				.setImage(new Image(new ImageResourcePrototype(
						null, ZoomPanelResources.INSTANCE
								.fullscreen_exit_black18().getSafeUri(),
						0, 0, 18, 18, false, false)));

		fullscreenBtn.setStyleName("zoomPanelBtn");

		FastClickHandler handlerFullscreen = new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				toggleFullscreen();
			}
		};

		fullscreenBtn.addFastClickHandler(handlerFullscreen);
		Browser.addFullscreenListener(new StringHandler() {

			@Override
			public void handle(String obj) {
				if ("true".equals(obj)) {
					onFullscreen();
				} else {
					onExitFullscreen();
				}
			}
		});
		zoomPanel.add(fullscreenBtn);
	}

	/**
	 * Handler that runs on switching to fullscreen.
	 */
	void onFullscreen() {
		isFullScreen = true;
		fullscreenBtn
				.setIcon(new ImageResourcePrototype(
						null, ZoomPanelResources.INSTANCE
								.fullscreen_exit_black18().getSafeUri(),
						0, 0, 18, 18, false, false));
	}

	/**
	 * Handler that runs on exiting to fullscreen.
	 */
	void onExitFullscreen() {
		isFullScreen = false;
		fullscreenBtn.setIcon(new ImageResourcePrototype(null,
				ZoomPanelResources.INSTANCE.fullscreen_black18().getSafeUri(),
				0, 0, 18, 18, false, false));
		if (!app.getArticleElement().getDataParamFitToScreen()) {

			final Element scaler = app.getArticleElement().getParentElement();

			scaler.removeClassName("fullscreen");
			scaler.getStyle().setMarginLeft(0, Unit.PX);
			scaler.getStyle().setMarginTop(0, Unit.PX);
			dispatchResize();
			Element container = scaler.getParentElement();
			resetStyleAfterFullscreen(container);
			Browser.scale(scaler, app.getArticleElement().getDataParamScale(),
					0, 0);

		}
		Browser.scale(zoomPanel.getElement(), 1, 0, 0);
	}

	protected void resetStyleAfterFullscreen(Element container) {
		if (container != null) {
			container.getStyle().setProperty("position",
					containerPositionBefore);
			if (!StringUtil.empty(containerMarginLeft)) {
				container.getStyle().setProperty("marginLeft",
						containerMarginLeft);
			}
			if (!StringUtil.empty(containerMarginTop)) {
				container.getStyle().setProperty("marginTop",
						containerMarginTop);
			}
		}
	}

	/**
	 * Add zoom in/out buttons to GUI
	 */
	public void addZoomButtons() {

		// add home button
		homeBtn = new StandardButton(
				new ImageResourcePrototype(null,
						ZoomPanelResources.INSTANCE.home_zoom_black18()
								.getSafeUri(),
						0, 0, 18, 18, false, false),
				app);
		homeBtn.setStyleName("zoomPanelBtn");
		hideHomeButton();
		FastClickHandler handlerHome = new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				app.getEuclidianView1().setStandardView(true);
			}
		};
		homeBtn.addFastClickHandler(handlerHome);
		
		zoomPanel.add(homeBtn);
		if (!Browser.isMobile()) {
			addZoomInButton();
			addZoomOutButton();
		}

		ClickStartHandler.init(zoomPanel, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				// to stopPropagation and preventDefault.
			}
			
		});
	}

	private void addZoomOutButton() {
		zoomOutBtn = new StandardButton(
				new ImageResourcePrototype(null,
						ZoomPanelResources.INSTANCE.remove_black18()
								.getSafeUri(),
						0, 0, 18, 18, false, false),
				app);
		zoomOutBtn.setStyleName("zoomPanelBtn");
		FastClickHandler handlerZoomOut = new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				getEuclidianView().getEuclidianController().zoomInOut(false,
						true, ZoomPanel.this);
			}
		};
		zoomOutBtn.addFastClickHandler(handlerZoomOut);
		zoomPanel.add(zoomOutBtn);
	}

	private void addZoomInButton() {
		zoomInBtn = new StandardButton(new ImageResourcePrototype(null,
				ZoomPanelResources.INSTANCE.add_black18().getSafeUri(), 0, 0,
				18, 18, false, false), app);
		zoomInBtn.setStyleName("zoomPanelBtn");
		FastClickHandler handlerZoomIn = new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				getEuclidianView().getEuclidianController().zoomInOut(false,
						false, ZoomPanel.this);
			}
		};
		zoomInBtn.addFastClickHandler(handlerZoomIn);
		zoomPanel.add(zoomInBtn);
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
		homeBtn.addStyleName("zoomPanelHomeIn");
		homeBtn.removeStyleName("zoomPanelHomeOut");
	}

	/**
	 * Hides home button.
	 */
	void hideHomeButton() {
		if (homeBtn == null) {
			return;
		}

		homeBtn.addStyleName("zoomPanelHomeOut");
		homeBtn.removeStyleName("zoomPanelHomeIn");
	}

	private void updateHomeButton() {
		if (app.getEuclidianView1().isCoordSystemTranslatedByAnimation()) {
			return;
		}
		if (app.getEuclidianView1().isStandardView()) {
			hideHomeButton();
		} else {
			showHomeButton();
		}
	}

	@Override
	public void onCoordSystemChanged() {
		updateHomeButton();
	}

	protected native void dispatchResize() /*-{
		$wnd.dispatchEvent(new Event("resize"));

	}-*/;

	/**
	 * Switch between fullscreen and windowed mode.
	 */
	protected void toggleFullscreen() {
		final Element container;
		final boolean ipad = Browser.isIPad();
		if (app.getArticleElement().getDataParamFitToScreen()) {
			container = null;
		} else {
			final Element scaler = app.getArticleElement().getParentElement();
			container = scaler.getParentElement();
			if (!isFullScreen) {
				containerPositionBefore = container.getStyle().getPosition();
				containerMarginLeft = container.getStyle()
						.getProperty("marginLeft");
				containerMarginTop = container.getStyle()
						.getProperty("marginTop");
				container.getStyle().setProperty("marginTop", "0");
				container.getStyle().setProperty("marginLeft", "0");
				if (StringUtil.empty(containerPositionBefore)) {
					containerPositionBefore = "static";
				}
				scaler.addClassName("fullscreen");
				if (ipad) {
					scaler.addClassName("fullscreen-ipad");
				}
				Timer t = new Timer() {

					@Override
					public void run() {
						scaleApplet(scaler, container);
						if (ipad) {
							onFullscreen();
						}
					}
				};
				// delay scaling to make sure scrollbars disappear
				t.schedule(50);
			} else {
				if (ipad) {
					scaler.removeClassName("fullscreen-ipad");
					onExitFullscreen();
				}
			}
		}
		if (!ipad) {
			isFullScreen = !isFullScreen;
			Browser.toggleFullscreen(isFullScreen, container);
		}
	}

	private static double getDeviceScale(double xscale, double yscale) {
		if ((xscale < 1 || yscale < 1) || Browser.isIPad()) {
			return Math.min(1d, Math.min(xscale, yscale));
		}
		return Math.max(1d, Math.min(xscale, yscale));
	}

	protected void scaleApplet(Element scaler, Element container) {
		int margin = 32;
		double xscale = (Window.getClientWidth() - margin) / app.getWidth();
		double yscale = (Window.getClientHeight() - margin) / app.getHeight();
		double scale = getDeviceScale(xscale, yscale);
		Browser.scale(scaler, scale, 0, 0);
		Browser.scale(zoomPanel.getElement(), 1 / scale, 120, 100);
		container.getStyle().setWidth(100, Unit.PCT);
		container.getStyle().setHeight(100, Unit.PCT);
		container.getStyle().setPosition(Position.ABSOLUTE);
		double marginLeft = 0;
		double marginTop = 0;
		if (xscale > yscale) {
			marginLeft = (Window.getClientWidth() - app.getWidth() * scale) / 2;
		} else {
			marginTop = (Window.getClientHeight() - app.getHeight() * scale)
					/ 2;
		}
		scaler.getStyle().setMarginLeft(marginLeft, Unit.PX);
		scaler.getStyle().setMarginTop(marginTop, Unit.PX);
		app.getArticleElement().resetScale();
		app.recalculateEnvironments();

	}

	public void setLabels() {
		setButtonTitle(fullscreenBtn, "Fullscreen");
		setButtonTitle(homeBtn, "StandardView");
		setButtonTitle(zoomOutBtn,"ZoomOut.Tool");
		setButtonTitle(zoomInBtn,"ZoomIn.Tool");
	}

	private void setButtonTitle(StandardButton btn, String string) {
		if (btn != null) {
			btn.setTitle(app.getLocalization().getMenu(string));
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
				&& app.isShiftDragZoomEnabled();
	}

	public static boolean neededFor(AppW app) {
		return (needsZoomButtons(app) || needsFullscreenButton(app))
				&& app.has(Feature.ZOOM_PANEL);
	}

	/**
	 * 
	 * @return the minimum height that is needed to display zoomPanel.
	 */
	public int getMinHeight() {
		return needsZoomButtons(app) ? 200 : 100;
	}
}
