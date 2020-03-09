package org.geogebra.web.html5.gui.zoompanel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.euclidian.CoordSystemListener;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.layout.GUITabs;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.css.ZoomPanelResources;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.html5.util.ArticleElementInterface;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

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
	private StandardButton fullscreenBtn;

	/** application */
	private AppW app;
	private final EuclidianView view;

	private List<StandardButton> buttons;
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
		addStyleName(app.isWhiteboardActive()
				? "zoomPanelWithPageControl" : "zoomPanelPosition");
		if (ZoomPanel.needsZoomButtons(app) && zoomable) {
			addZoomButtons();
		}
		buttons = new ArrayList<>();
		buttons.addAll(Arrays.asList(homeBtn, zoomInBtn, zoomOutBtn));
		if (ZoomPanel.needsFullscreenButton(app) && rightBottom) {
			addFullscreenButton();
		}

		setLabels();
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
					isFullScreen() || !Browser.isCoveringWholeScreen());
		}
	}

	/**
	 * add fullscreen button
	 */
	public void addFullscreenButton() {
		fullscreenBtn = new StandardButton(
				ZoomPanelResources.INSTANCE.fullscreen_black18(), null, 24,
				app);
		NoDragImage exitFullscreenImage = new NoDragImage(ZoomPanelResources.INSTANCE
				.fullscreen_exit_black18(), 24);
		exitFullscreenImage.setPresentation();
		fullscreenBtn.getDownFace()
				.setImage(exitFullscreenImage);

		fullscreenBtn.setStyleName("zoomPanelBtn");

		FastClickHandler handlerFullscreen = new FastClickHandler.Typed() {
			@Override
			public void onClick(Widget source) {
				// never called - do nothing
			}

			@Override
			public void onClick(String type) {
				getZoomController().onFullscreenPressed(getPanelElement(),
						fullscreenBtn, type);
				setFullScreenAuralText();
			}
		};

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
		buttons.add(fullscreenBtn);
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
		ScreenReaderBuilder sb = new ScreenReaderBuilder();
		if (isFullScreen()) {
			sb.append(loc.getMenuDefault("FullscreenExitButtonSelected",
					"Full screen button selected (currently full screen)"));
		} else {
			sb.append(loc.getMenuDefault("FullscreenButtonSelected",
					"Full screen button selected (currently not full screen)"));
		}
		if (!Browser.needsAccessibilityView()) {
			addFullscreenKeyboardControls(sb);
		}
		setButtonTitleAndAltText(fullscreenBtn, sb.toString());
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
		ScreenReaderBuilder sb = new ScreenReaderBuilder();
		sb.append(loc.getMenuDefault(transKey, auralDefault));
		if (!Browser.needsAccessibilityView()) {
			addZoomKeyboardControls(sb, controlNext);
		}

		setButtonTitleAndAltText(btn, sb.toString());
	}

	private void addZoomKeyboardControls(ScreenReaderBuilder sb, boolean controlNext) {
		addSpaceControl(sb);
		if (controlNext) {
			sb.append(loc.getMenuDefault("PressTabToSelectControls",
					"Press tab to select controls"));
		} else {
			sb.append(loc.getMenuDefault("PressTabToSelectNext",
					"Press tab to select next object"));
		}
	}

	private void addSpaceControl(ScreenReaderBuilder sb) {
		sb.appendSpace();
		sb.append(loc.getMenuDefault("PressSpaceToActivate", "press space to activate"));
		sb.endSentence();
	}

	private static void setButtonTitleAndAltText(StandardButton btn, String string) {
		if (btn != null) {
			btn.setTitle(string);
			btn.setAltText(string);
		}
	}

	private static boolean needsFullscreenButton(AppW app) {
		if (app.getArticleElement().getDataParamApp()) {
			return ZoomController.isRunningInIframe() || !Browser.isMobile();
		} else {
			if (!app.getArticleElement().getDataParamShowFullscreenButton()) {
				return false;
			}

			return !(Browser.isiOS() && ZoomController.isRunningInIframe());
		}
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
	 * Sets tab order for header buttons.
	 */
	public void setTabIndexes() {
		int tabIndex = GUITabs.ZOOM;
		if (view != null) {
			tabIndex += view.getViewID() + buttons.size();
		}
		for (StandardButton btn : buttons) {
			if (btn != null) {
				GUITabs.setTabIndex(btn.getElement(), tabIndex);
				tabIndex++;
			}
		}
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
		if (zoomButtonsVisible) {
			if (homeBtn != null && isHomeShown()) {
				return homeBtn;
			}
			if (zoomInBtn != null) {
				return zoomInBtn;
			}
		}
		return fullscreenBtn;
	}

	private Widget getLastButton() {
		if (fullscreenBtn != null) {
			return fullscreenBtn;
		}
		if (zoomButtonsVisible) {
			if (zoomOutBtn != null) {
				return zoomOutBtn;
			}

			if (zoomInBtn != null) {
				return zoomInBtn;
			}

			if (homeBtn != null && isHomeShown()) {
				return homeBtn;
			}
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
			homeBtn.setVisible(zoomButtonsVisible);
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
