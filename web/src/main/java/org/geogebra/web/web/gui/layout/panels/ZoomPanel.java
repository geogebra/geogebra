package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.euclidian.MyZoomerListener;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.StringHandler;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ZoomPanel extends FlowPanel implements MyZoomerListener {

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

	public ZoomPanel(EuclidianView view) {
		this.view = view;
		this.app = (AppW) view.getApplication();
		view.getEuclidianController().setZoomerListener(this);

	}
	public void updateFullscreen() {
		ArticleElement ae = app.getArticleElement();
		if (!ae.getDataParamApp() && isFullScreen) {
			scaleApplet(ae.getParentElement(),
					ae.getParentElement().getParentElement());
		}

	}

	/**
	 * add fullscreen button
	 */
	void addFullscreenButton() {
		// add fullscreen button
		fullscreenBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.fullscreen_black18());
		fullscreenBtn.getDownFace().setImage(new Image(
				MaterialDesignResources.INSTANCE.fullscreen_exit_black18()));
		fullscreenBtn.setTitle(app.getLocalization().getMenu("Fullscreen"));
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
					isFullScreen = true;
					fullscreenBtn.setIcon(MaterialDesignResources.INSTANCE
							.fullscreen_exit_black18());
				} else {
					isFullScreen = false;
					fullscreenBtn.setIcon(MaterialDesignResources.INSTANCE
							.fullscreen_black18());
					if (!app.getArticleElement()
							.getDataParamFitToScreen()) {

						final Element scaler = app.getArticleElement()
								.getParentElement();

						scaler.removeClassName("fullscreen");
						scaler.getStyle().setMarginLeft(0, Unit.PX);
						scaler.getStyle().setMarginTop(0, Unit.PX);
						dispatchResize();
					}
					Browser.scale(zoomPanel.getElement(), 1, 0, 0);
				}

			}
		});
		zoomPanel.add(fullscreenBtn);

	}

	void addZoomButtons() {

		// add home button
		homeBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.home_zoom_black18());
		homeBtn.setTitle(app.getLocalization().getMenu("Home"));
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

	}

	private void addZoomOutButton() {
		zoomOutBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.remove_black18());
		zoomOutBtn.setTitle(app.getLocalization().getMenu("ZoomOut.Tool"));
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
		zoomInBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.add_black18());
		zoomInBtn.setTitle(app.getLocalization().getMenu("ZoomIn.Tool"));
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
		if (app.getEuclidianView1().isStandardView()) {
			hideHomeButton();
		} else {
			showHomeButton();
		}
	}

	@Override
	public void onZoomStart() {
		// only zoom end important
	}

	@Override
	public void onZoomStep() {
		// only zoom end important
	}

	@Override
	public void onZoomEnd() {
		updateHomeButton();
	}

	@Override
	public void onCoordSystemChanged() {
		updateHomeButton();
	}

	protected native void dispatchResize() /*-{
		$wnd.dispatchEvent(new Event("resize"));

	}-*/;

	protected void toggleFullscreen() {
		final Element container;
		if (app.getArticleElement().getDataParamFitToScreen()) {
			container = null;
		} else {
			final Element scaler = app.getArticleElement().getParentElement();
			container = scaler.getParentElement();
			if (!isFullScreen) {
				scaler.addClassName("fullscreen");
				Timer t = new Timer() {

					@Override
					public void run() {
						scaleApplet(scaler, container);

					}
				};
				// delay scaling to make sure scrollbars disappear
				t.schedule(50);
			}
		}
		Browser.toggleFullscreen(!isFullScreen, container);
	}

	protected void scaleApplet(Element scaler, Element container) {
		double xscale = Window.getClientWidth() / app.getWidth();
		double yscale = Window.getClientHeight() / app.getHeight();
		double scale = Math.max(1d, Math.min(xscale, yscale));
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
}
