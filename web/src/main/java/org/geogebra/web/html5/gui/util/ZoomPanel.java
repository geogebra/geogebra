package org.geogebra.web.html5.gui.util;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.euclidian.CoordSystemListener;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.layout.GUITabs;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.css.ZoomPanelResources;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.TabHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.html5.util.ArticleElementInterface;

import com.google.gwt.dom.client.Element;
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

	/** application */
	private AppW app;
	private EuclidianView view;

	private List<StandardButton> buttons = null;
	private ZoomController zoomController;

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
		zoomController = new ZoomController(app);
		if (view != null) {
			view.getEuclidianController().addZoomerListener(this);
		}
		setStyleName("zoomPanel");
		addStyleName(app.isWhiteboardActive() && app.has(Feature.MOW_MULTI_PAGE)
				? "zoomPanelWithPageControl" : "zoomPanelPosition");
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
	 * @return controller for zoom panel and its buttons
	 */
	public ZoomController getZoomController() {
		return zoomController;
	}

	/**
	 * Updates fullscreen button and article.
	 */
	public void updateFullscreen() {
		ArticleElementInterface ae = app.getArticleElement();
		if (!ae.getDataParamApp() && isFullScreen()) {
			getZoomController().scaleApplet(ae.getParentElement(),
					ae.getParentElement().getParentElement(),
					getPanelElement());
		}
		if (ae.getDataParamApp() && fullscreenBtn != null) {
			fullscreenBtn.setVisible(
					isFullScreen()
							|| !Browser.isCoveringWholeScreen());
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
				getZoomController().onFullscreenPressed(getPanelElement(),
						fullscreenBtn);
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
	 * @return we need getElement() for zoom controller
	 */
	public Element getPanelElement() {
		return getElement();
	}

	/**
	 * Handler that runs on exiting to fullscreen.
	 */
	void onExitFullscreen() {
		getZoomController().setFullScreenActive(false);
		fullscreenBtn.setIcon(ZoomPanelResources.INSTANCE.fullscreen_black18());
		getZoomController().onExitFullscreen(getElement());
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
		getZoomController().hideHomeButton(homeBtn);
		FastClickHandler handlerHome = new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				getZoomController().onHomePressed();
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
				getZoomController().onZoomOutPressed();
			}
		};
		zoomOutBtn.addFastClickHandler(handlerZoomOut);
		zoomOutBtn.addTabHandler(this);
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
				getZoomController().onZoomInPressed();
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

	@Override
	public void onCoordSystemChanged() {
		getZoomController().updateHomeButton(homeBtn);
	}

	/**
	 * Sets translated titles of the buttons.
	 */
	public void setLabels() {
		setFullScreenAuralText();
		setButtonTitleAndAltText(homeBtn, "StandardView");
		setButtonTitleAndAltText(zoomOutBtn, "ZoomOut.Tool");
		setButtonTitleAndAltText(zoomInBtn, "ZoomIn.Tool");
	}

	private void setFullScreenAuralText() {
		if (fullscreenBtn == null) {
			return;
		}
		ScreenReaderBuilder sb = new ScreenReaderBuilder();
		LocalizationW loc = app.getLocalization();
		sb.append(loc.getMenuDefault("FullscreenButtonSelected",
				"Full screen button selected Press space to go full screen"));
		sb.appendSpace();
		sb.append(loc.getMenuDefault("PressTabToSelectNext", "Press tab to select next object"));
		sb.endSentence();
		setButtonTitleAndAltText(fullscreenBtn, sb.toString());
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
		if (view != null) {
			tabIndex += view.getViewID() + buttons.size();
		}
		for (StandardButton btn : buttons) {
			if (btn != null) {
				btn.setTabIndex(tabIndex);
				tabIndex++;
			}
		}
	}

	@Override
	public boolean onTab(Widget source, boolean shiftDown) {
		Log.debug("WW: source: " + source.getElement().getAttribute("aria-label") + " lastButton: "
				+ getLastButton().getElement().getAttribute("aria-label"));
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

	/**
	 * 
	 * @return if panel have visible buttons.
	 */
	public boolean hasButtons() {
		return getFirstButton() != null;
	}

	/** Focus the last available button on zoom panel. */
	public void focusLastButton() {
		Widget btn = getLastButton();
		if (btn != null) {
			btn.getElement().focus();
		}
	}

	private Widget getFirstButton() {
		if (homeBtn != null && isHomeShown()) {
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

		if (zoomInBtn != null) {
			return zoomInBtn;
		}

		if (homeBtn != null && isHomeShown()) {
			return homeBtn;
		}
		return null;
	}

	/**
	 * @return whether fullscreen is active
	 */
	public boolean isFullScreen() {
		return getZoomController().isFullScreenActive();
	}

	/**
	 * @return whether home button is shown
	 */
	public boolean isHomeShown() {
		return getZoomController().isHomeShown();
	}
}
