package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GetViewId;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.StringHandler;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.util.StandardButton;
import org.geogebra.web.web.gui.view.consprotocol.ConstructionProtocolNavigationW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract class for all "euclidian" panels.
 * 
 * @author arpad (based on EuclidianDockPanelAbstract by Mathieu)
 * @remark {@link #getEuclidianView()} has to be overridden if
 *         {@link #getComponent()} does not return the euclidian view directly
 */
public abstract class EuclidianDockPanelWAbstract extends DockPanelW
		implements GetViewId {
	private ConstructionProtocolNavigationW consProtNav;
	private boolean hasEuclidianFocus;
	private boolean hasZoomPanel = false;
	/**
	 * panel with home,+,-,fullscreen btns
	 */
	FlowPanel zoomPanel;
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

	/**
	 * default constructor
	 * 
	 * @param id
	 *            id
	 * @param title
	 *            title
	 * @param toolbar
	 *            toolbar string
	 * @param hasStyleBar
	 *            whether to show stylebar
	 * @param hasZoomPanel
	 *            - true if it has zoom panel
	 * @param menuOrder
	 *            The location of this view in the view menu, -1 if the view
	 *            should not appear at all
	 * @param shortcut
	 *            letter for Ctrl+Shift+letter shortcut
	 */
	public EuclidianDockPanelWAbstract(int id, String title, String toolbar,
			boolean hasStyleBar, boolean hasZoomPanel, int menuOrder,
			char shortcut) {
		super(id, title, toolbar, hasStyleBar, menuOrder,
				shortcut);
		this.hasZoomPanel = hasZoomPanel;
	}

	/**
	 * sets this euclidian panel to have the "euclidian focus"
	 * 
	 * @param hasFocus
	 *            whether to focus
	 */
	public final void setEuclidianFocus(boolean hasFocus) {
		hasEuclidianFocus = hasFocus;
	}
	
	@Override
	protected boolean titleIsBold(){
		return super.titleIsBold() || hasEuclidianFocus;
	}

	@Override
	public boolean updateResizeWeight(){
		return true;
	}
	
	/**
	 * @return view in this dock panel
	 */
	abstract public EuclidianView getEuclidianView();

	@Override
	public void setVisible(boolean sv) {
		super.setVisible(sv);
		// if (getEuclidianView() != null) {// also included in:
		if (getEuclidianView() instanceof EuclidianViewWInterface) {
			((EuclidianViewWInterface) getEuclidianView()).updateFirstAndLast(
					sv,
						false);
			}
		// }
	}

	/**
	 * Adds navigation bar
	 */
	public final void addNavigationBar() {
		consProtNav = (ConstructionProtocolNavigationW) (app.getGuiManager()
				.getConstructionProtocolNavigation(id));
		consProtNav.getImpl().addStyleName("consProtNav");
		if (getEuclidianPanel() == null) {
			loadComponent();
		}
		getEuclidianPanel().add(consProtNav.getImpl()); // may be invisible, but
														// made
													// visible later
		updateNavigationBar();
	}

	@Override
	public final void updateNavigationBar() {
		// ConstructionProtocolSettings cps = app.getSettings()
		// .getConstructionProtocol();
		// ((ConstructionProtocolNavigationW) consProtNav).settingsChanged(cps);
		// cps.addListener((ConstructionProtocolNavigation)consProtNav);

		if (app.getShowCPNavNeedsUpdate(id)) {
			app.setShowConstructionProtocolNavigation(
					app.showConsProtNavigation(id), id);
		}
		if (app.showConsProtNavigation(id)
				&& consProtNav == null) {
			this.addNavigationBar();
		}
		if (consProtNav != null) {
			consProtNav.update();
			consProtNav.setVisible(app.showConsProtNavigation(id));
			getEuclidianPanel().onResize();
		}
	}

	@Override
	public int navHeight() {
		if (this.consProtNav != null
				&& this.consProtNav.getImpl().getOffsetHeight() != 0) {
			return this.consProtNav.getImpl().getOffsetHeight();
		}
		return 30;
	}

	/**
	 * Wrapper of euclidian view
	 */
	public static class EuclidianPanel extends FlowPanel
			implements RequiresResize {

		/** dock panel */
		EuclidianDockPanelWAbstract dockPanel;
		/** panel for positioning furniture */
		AbsolutePanel absoluteEuclidianPanel;
		/** current height */
		int oldHeight = 0;
		/** current width */
		int oldWidth = 0;

		/**
		 * @param dockPanel
		 *            parent dock panel
		 */
		public EuclidianPanel(EuclidianDockPanelWAbstract dockPanel) {
			this(dockPanel, new AbsolutePanel());
		}

		/**
		 * @param dockPanel
		 *            parent dock panel
		 * @param absPanel
		 *            absolute panel (for positioning stuff over canvas)
		 */
		public EuclidianPanel(EuclidianDockPanelWAbstract dockPanel,
				AbsolutePanel absPanel) {
			super();
			this.dockPanel = dockPanel;
			add(absoluteEuclidianPanel = absPanel);
			absoluteEuclidianPanel.addStyleName("EuclidianPanel");
			absoluteEuclidianPanel.getElement().getStyle()
					.setOverflow(Overflow.VISIBLE);
			checkFocus();
		}

		@Override
		public void onResize() {

			if (dockPanel.getApp() != null) {

				int h = dockPanel.getComponentInteriorHeight()
						- dockPanel.navHeightIfShown();
				int w = dockPanel.getComponentInteriorWidth();


				// TODO handle this better?
				// exit if new size cannot be determined
				// one dimension may be intentionally 0, resize to avoid DOM
				// overflow
				if (h < 0 || w < 0 || (w == 0 && h == 0)) {
					return;
				}
				if (h != oldHeight || w != oldWidth) {
					dockPanel.resizeView(w, h);
					oldHeight = h;
					oldWidth = w;
				} else {
					// it's possible that the width/height didn't change but the
					// position of EV did
					dockPanel.calculateEnvironment();
				}
			}
		}

		// hack to fix GGB-697
		private native void checkFocus() /*-{
			var that = this;
			var forceResize = function() {
				that.@org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelWAbstract.EuclidianPanel::forceResize()()
			};

			$wnd.visibilityEventMain(forceResize, forceResize);
		}-*/ ;

		private void forceResize() {
			EuclidianView view = dockPanel.getEuclidianView();
			if (view instanceof EuclidianViewWInterface) {
				((EuclidianViewWInterface) view).getG2P().forceResize();
				view.repaintView();
				view.suggestRepaint();
			}
		}

		@Override
		public boolean remove(Widget w) {
			return absoluteEuclidianPanel.remove(w);
		}

		public AbsolutePanel getAbsolutePanel() {
			return absoluteEuclidianPanel;
		}
	}

	protected abstract EuclidianPanel getEuclidianPanel();

	public AppW getApp() {
		return app;
	}

	public final AbsolutePanel getAbsolutePanel() {
		return getEuclidianPanel() == null ? null : getEuclidianPanel()
				.getAbsolutePanel();
	}

	private boolean allowZoomPanel() {
		return hasZoomPanel
				&& (app.getArticleElement().getDataParamShowZoomControls()
						|| app.getArticleElement().getDataParamApp()
								&& app.has(Feature.ZOOM_PANEL))
				&& !Browser.isMobile();
	}

	@Override
	protected void addZoomPanel(MyDockLayoutPanel dockPanel) {
		if (allowZoomPanel()) {
			dockPanel.addSouth(zoomPanel, 0);
		}
	}

	@Override
	protected void tryBuildZoomPanel() {
		if (allowZoomPanel()) {
			buildZoomPanel();
		}
	}

	private void buildZoomPanel() {
		zoomPanel = new FlowPanel();
		zoomPanel.setStyleName("zoomPanel");

		// add home button
		homeBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.home_zoom_black18());
		homeBtn.setStyleName("zoomPanelBtn");
		FastClickHandler handlerHome = new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				app.getEuclidianView1().setStandardView(true);
			}
		};
		homeBtn.addFastClickHandler(handlerHome);
		zoomPanel.add(homeBtn);

		// add zoom in button
		zoomInBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.add_black18());
		zoomInBtn.setStyleName("zoomPanelBtn");
		FastClickHandler handlerZoomIn = new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				getEuclidianView().getEuclidianController()
						.zoomInOut(false, false);
			}
		};
		zoomInBtn.addFastClickHandler(handlerZoomIn);
		zoomPanel.add(zoomInBtn);

		// add zoom out button
		zoomOutBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.remove_black18());
		zoomOutBtn.setStyleName("zoomPanelBtn");
		FastClickHandler handlerZoomOut = new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				getEuclidianView().getEuclidianController()
						.zoomInOut(false, true);
			}
		};
		zoomOutBtn.addFastClickHandler(handlerZoomOut);
		zoomPanel.add(zoomOutBtn);

		// add fullscreen button
		fullscreenBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.fullscreen_black18());
		fullscreenBtn.getDownFace().setImage(new Image(
				MaterialDesignResources.INSTANCE.fullscreen_exit_black18()));
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
				Log.debug(obj);
				if ("true".equals(obj)) {
					isFullScreen = true;
					fullscreenBtn.setIcon(MaterialDesignResources.INSTANCE
							.fullscreen_exit_black18());
				} else {
					isFullScreen = false;
					fullscreenBtn.setIcon(MaterialDesignResources.INSTANCE
							.fullscreen_black18());
					if (!getApp().getArticleElement()
							.getDataParamFitToScreen()) {

						final Element scaler = app.getArticleElement()
								.getParentElement();
						scaler.getStyle().setMarginLeft(0, Unit.PX);
						scaler.getStyle().setMarginTop(0, Unit.PX);
					}
					Browser.scale(zoomPanel.getElement(), 1, 0, 0);
				}

			}
		});
		zoomPanel.add(fullscreenBtn);
	}

	protected void toggleFullscreen() {
		final Element container;
		if (app.getArticleElement().getDataParamFitToScreen()) {
			container = null;
		} else {
			final Element scaler = app.getArticleElement().getParentElement();
			container = scaler.getParentElement();
			if (!isFullScreen) {
				Timer t = new Timer() {

					@Override
					public void run() {
						scaleApplet(scaler, container);

					}
				};
				t.schedule(100);

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
		if (xscale > yscale) {
			scaler.getStyle().setMarginLeft(
					(Window.getClientWidth() - app.getWidth() * scale) / 2,
					Unit.PX);
		} else {
			Log.debug(Window.getClientHeight() / yscale);
			scaler.getStyle().setMarginTop(
					(Window.getClientHeight() - app.getHeight() * scale) / 2,
					Unit.PX);
		}
		app.getArticleElement().resetScale();
		app.recalculateEnvironments();

	}

	public abstract void calculateEnvironment();

	public abstract void resizeView(int width, int height);
	
}
