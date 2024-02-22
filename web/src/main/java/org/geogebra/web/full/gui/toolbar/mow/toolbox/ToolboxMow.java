package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventListener;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.html5.css.ZoomPanelResources;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RootPanel;
import org.gwtproject.user.client.ui.SimplePanel;

public class ToolboxMow extends FlowPanel {
	private final AppW appW;
	private ToolboxDecorator decorator;

	/**
	 * MOW toolbox
	 * @param appW - application
	 */
	public ToolboxMow(AppW appW) {
		this.appW = appW;
		decorator = new ToolboxDecorator(this);
		RootPanel.get().add(this);
		buildGui();
	}

	private void buildGui() {
		decorator.positionLeft();

		addPressButton(ZoomPanelResources.INSTANCE.target(), "Spotlight.Tool", "spotlightTool",
				() -> {
					DockPanelW dp = (DockPanelW) appW.getGuiManager().getLayout().getDockManager()
							.getPanel(App.VIEW_EUCLIDIAN);
					dp.getComponent().addStyleName("graphicsWithSpotlight");
					appW.getActiveEuclidianView().getEuclidianController().spotlightOn();
					appW.getAppletFrame().add(ToolboxMow.this::initSpotlightOff);
					appW.hideMenu();
				});

		addDivider();
		//addActionButton(ToolbarSvgResources.INSTANCE.mode_pen(), () -> appW.setMoveMode());
	}

	private SimplePanel initSpotlightOff() {
		SimplePanel spotlightOff = new SimplePanel();
		spotlightOff.addStyleName("spotlightOffBtn");

		StandardButton spotlightOffBtn = new StandardButton(
				ZoomPanelResources.INSTANCE.target().withFill(GColor.PURPLE_A700.toString()),
				null, 24);
		spotlightOffBtn.setTitle(appW.getLocalization().getMenu("Spotlight.Tool"));
		spotlightOffBtn.setAltText(appW.getLocalization().getMenu("Spotlight.Tool"));
		spotlightOffBtn.addStyleName("iconButton active");
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
				appW.getActiveEuclidianView().getEuclidianController().spotlightOff();
			}
		});

		spotlightOff.add(spotlightOffBtn);
		return spotlightOff;
	}

	private void addPressButton(SVGResource image, String ariaLabel, String dataTest,
			Runnable onHandler) {
		IconButton iconButton = new IconButton(appW.getLocalization(), image, ariaLabel, ariaLabel,
				dataTest, () -> onHandler.run());
		add(iconButton);
	}

	/*private void addCategoryButton(SVGResource image, List<Integer> tools) {
		StandardButton categoryBtn = new StandardButton(image, null, 24, 24);
		categoryBtn.addStyleName("actionButton");
		categoryBtn.addFastClickHandler((event)
				-> {
			CategoryPopup popup = new CategoryPopup(appW, tools);
			popup.show(categoryBtn.getElement().getAbsoluteRight() + 16,
					categoryBtn.getElement().getAbsoluteTop()
							- categoryBtn.getElement().getClientHeight() - 16);
		});

		add(categoryBtn);
	}*/

	private void addDivider() {
		SimplePanel divider = new SimplePanel();
		divider.setStyleName("divider");

		add(divider);
	}
}
