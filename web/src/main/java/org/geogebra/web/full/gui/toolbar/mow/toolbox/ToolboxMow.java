package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventListener;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.html5.css.ZoomPanelResources;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RootPanel;
import org.gwtproject.user.client.ui.SimplePanel;

public class ToolboxMow extends FlowPanel {
	private final AppW appW;
	private ToolboxDecorator decorator;
	private IconButton spotlightBtn;

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

		spotlightBtn = addToggleButton(ZoomPanelResources.INSTANCE.target(), "Spotlight.Tool",
				"Spotlight.Tool", "spotlightTool", () -> {
					DockPanelW dp = (DockPanelW) appW.getGuiManager().getLayout().getDockManager()
							.getPanel(App.VIEW_EUCLIDIAN);
					dp.getComponent().addStyleName("graphicsWithSpotlight");
					appW.getActiveEuclidianView().getEuclidianController().spotlightOn();
					initSpotlightOff();
					appW.hideMenu();
				}, () -> {});

		addDivider();
		addPressButton(ToolbarSvgResources.INSTANCE.mode_pen(), "move mode", "moveBtn",
				() -> appW.setMoveMode());
	}

	private void initSpotlightOff() {
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
					spotlightBtn.setActive(false);
					appW.getEventDispatcher().removeEventListener(this);
				}
			}

			@Override
			public void reset() {
				// not needed
			}
		});
	}

	private void addPressButton(SVGResource image, String ariaLabel, String dataTest,
			Runnable onHandler) {
		IconButton iconButton = new IconButton(appW.getLocalization(), image, ariaLabel, ariaLabel,
				dataTest, onHandler);
		add(iconButton);
	}

	private IconButton addToggleButton(SVGResource image, String ariaLabel, String dataTitle,
			String dataTest, Runnable onHandler, Runnable offHandler) {
		IconButton iconButton = new IconButton(appW, image, ariaLabel, dataTitle,
				dataTest, onHandler, offHandler);
		add(iconButton);
		return iconButton;
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