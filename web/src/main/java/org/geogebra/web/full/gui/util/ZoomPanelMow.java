package org.geogebra.web.full.gui.util;

import org.geogebra.common.euclidian.CoordSystemListener;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventListener;
import org.geogebra.common.plugin.EventType;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.css.ZoomPanelResources;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.gui.zoompanel.ZoomController;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.SimplePanel;

/**
 * @author csilla
 *
 */
public class ZoomPanelMow extends FlowPanel
		implements SetLabels, CoordSystemListener {
	private final AppW appW;
	private StandardButton spotlightOnBtn;
	private StandardButton dragPadBtn;
	private StandardButton zoomInBtn;
	private StandardButton zoomOutBtn;
	private StandardButton homeBtn;
	private final ZoomController zoomController;

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public ZoomPanelMow(AppW app) {
		this.appW = app;
		zoomController = new ZoomController(appW, app.getActiveEuclidianView());
		if (app.getActiveEuclidianView() != null) {
			app.getActiveEuclidianView().getEuclidianController()
					.addZoomerListener(this);
		}
		buildGui();
	}

	private void buildGui() {
		addStyleName("mowZoomPanel");
		addDragPadButton();
		addSpotlightButton();
		addZoomButtons();
	}

	/**
	 * @return zoom controller
	 */
	public ZoomController getZoomController() {
		return zoomController;
	}

	/**
	 * @return see {@link AppW}
	 */
	public AppW getAppW() {
		return appW;
	}

	/**
	 * @return drag pad button
	 */
	public StandardButton getDragPadBtn() {
		return dragPadBtn;
	}

	/**
	 * remove selected style
	 */
	public void deselectDragBtn() {
		getDragPadBtn().removeStyleName("selected");
		getAppW().setMode(EuclidianConstants.MODE_SELECT_MOW);
	}

	private void addDragPadButton() {
		dragPadBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.move_canvas(), null, 24);
		dragPadBtn.setStyleName("zoomPanelBtn");
		registerFocusable(dragPadBtn, AccessibilityGroup.ViewControlId.ZOOM_NOTES_DRAG_VIEW);
		TestHarness.setAttr(dragPadBtn, "panViewTool");

		ClickStartHandler.init(dragPadBtn, new ClickStartHandler(false, false) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				getAppW().setMode(EuclidianConstants.MODE_TRANSLATEVIEW);
				dragPadBtn.addStyleName("selected");
				getAppW().hideMenu();
			}
		});
		add(dragPadBtn);
	}

	private void addSpotlightButton() {
		spotlightOnBtn = new StandardButton(
				ZoomPanelResources.INSTANCE.target(), null, 24);
		spotlightOnBtn.setStyleName("zoomPanelBtn");
		registerFocusable(spotlightOnBtn, AccessibilityGroup.ViewControlId.ZOOM_NOTES_SPOTLIGHT);
		TestHarness.setAttr(spotlightOnBtn, "spotlightTool");

		ClickStartHandler.init(spotlightOnBtn, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				DockPanelW dp = (DockPanelW) appW.getGuiManager().getLayout().getDockManager()
						.getPanel(App.VIEW_EUCLIDIAN);
				dp.getComponent().addStyleName("graphicsWithSpotlight");
				getEuclidianController().spotlightOn();
				appW.getAppletFrame().add(ZoomPanelMow.this::initSpotlightOff);
				appW.hideMenu();
			}
		});
		add(spotlightOnBtn);
	}

	private EuclidianController getEuclidianController() {
		return appW.getActiveEuclidianView().getEuclidianController();
	}

	private SimplePanel initSpotlightOff() {
		SimplePanel spotlightOff = new SimplePanel();
		spotlightOff.addStyleName("spotlightOffBtn");

		StandardButton spotlightOffBtn = new StandardButton(
				ZoomPanelResources.INSTANCE.target(), null, 24);
		setButtonTitleAndAltText(spotlightOffBtn, "Spotlight.Tool");
		spotlightOffBtn.addStyleName("zoomPanelBtn");
		appW.getEventDispatcher().addEventListener(new EventListener() {
			@Override
			public void sendEvent(Event evt) {
				if (evt.getType() == EventType.REMOVE
						&& evt.getTarget() != null && evt.getTarget().isSpotlight()
				) {
					EuclidianView view = appW.getActiveEuclidianView();
					DockPanelW dp = (DockPanelW) appW.getGuiManager().getLayout().getDockManager()
							.getPanel(App.VIEW_EUCLIDIAN);
					dp.getComponent().removeStyleName("graphicsWithSpotlight");
					view.clearSpotlight();
					spotlightOff.removeFromParent();
					appW.getEventDispatcher().removeEventListener(this);
				}
			}

			@Override
			public void reset() {
				// not needed
			}
		});
		ClickStartHandler.init(spotlightOffBtn, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				getEuclidianController().spotlightOff();
			}
		});

		spotlightOff.add(spotlightOffBtn);
		return spotlightOff;
	}

	private void registerFocusable(StandardButton dragPadBtn,
			AccessibilityGroup.ViewControlId group) {
		new FocusableWidget(AccessibilityGroup.getViewGroup(getViewId()), group,
				dragPadBtn).attachTo(appW);
	}

	private int getViewId() {
		return appW.getActiveEuclidianView().getViewID();
	}

	/**
	 * Add zoom in/out buttons to GUI
	 */
	private void addZoomButtons() {
		if (!NavigatorUtil.isMobile()) {
			addZoomInButton();
			addZoomOutButton();
		}
		homeBtn = new StandardButton(
				ZoomPanelResources.INSTANCE.home_zoom_black18(), null, 20);
		registerFocusable(homeBtn, AccessibilityGroup.ViewControlId.ZOOM_NOTES_HOME);
		homeBtn.setStyleName("zoomPanelBtn");
		homeBtn.addStyleName("zoomPanelBtnSmall");
		getZoomController().hideHomeButton(homeBtn);

		ClickStartHandler.init(homeBtn, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				zoomController.onHomePressed();
				deselectDragBtn();
			}
		});

		add(homeBtn);
		// click handler
		ClickStartHandler.initDefaults(this, true, true);
	}

	private void addZoomOutButton() {
		zoomOutBtn = new StandardButton(
				GuiResourcesSimple.INSTANCE.zoom_out(), null, 24);
		zoomOutBtn.setStyleName("zoomPanelBtn");
		registerFocusable(zoomOutBtn, AccessibilityGroup.ViewControlId.ZOOM_NOTES_MINUS);

		ClickStartHandler.init(zoomOutBtn, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				zoomController.onZoomOutPressed();
				deselectDragBtn();
			}
		});

		add(zoomOutBtn);
	}

	private void addZoomInButton() {
		zoomInBtn = new StandardButton(
				GuiResourcesSimple.INSTANCE.zoom_in(), null, 24);
		zoomInBtn.setStyleName("zoomPanelBtn");
		registerFocusable(zoomInBtn, AccessibilityGroup.ViewControlId.ZOOM_NOTES_PLUS);

		ClickStartHandler.init(zoomInBtn, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				zoomController.onZoomInPressed();
				deselectDragBtn();
			}
		});
		add(zoomInBtn);
	}

	/**
	 * Sets translated titles of the buttons.
	 */
	@Override
	public void setLabels() {
		setButtonTitleAndAltText(dragPadBtn, "PanView");
		setButtonTitleAndAltText(homeBtn, "StandardView");
		setButtonTitleAndAltText(zoomOutBtn, "ZoomOut.Tool");
		setButtonTitleAndAltText(zoomInBtn, "ZoomIn.Tool");
		setButtonTitleAndAltText(spotlightOnBtn, "Spotlight.Tool");
	}

	private void setButtonTitleAndAltText(StandardButton btn, String string) {
		if (btn != null) {
			btn.setTitle(appW.getLocalization().getMenu(string));
			btn.setAltText(appW.getLocalization().getMenu(string));
		}
	}

	@Override
	public void onCoordSystemChanged() {
		getZoomController().updateHomeButton(homeBtn);
	}
}
