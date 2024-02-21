package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import static org.geogebra.common.euclidian.EuclidianConstants.*;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.html5.css.ZoomPanelResources;
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

		addActionButton(ZoomPanelResources.INSTANCE.target(),
				() -> {
					DockPanelW dp = (DockPanelW) appW.getGuiManager().getLayout().getDockManager()
							.getPanel(App.VIEW_EUCLIDIAN);
					dp.getComponent().addStyleName("graphicsWithSpotlight");
					appW.getActiveEuclidianView().getEuclidianController().spotlightOn();
					//appW.getAppletFrame().add(ZoomPanelMow.this::initSpotlightOff);
					appW.hideMenu();
				});

		addActionButton(ToolbarSvgResources.INSTANCE.mode_pen(),
				() -> appW.setMoveMode());
		addDivider();
		addCategoryButton(MaterialDesignResources.INSTANCE.toolbar_tools(), Arrays.asList(MODE_PEN,
				MODE_HIGHLIGHTER, MODE_ERASER));
	}

	private void addActionButton(SVGResource image, Runnable handler) {
		IconButton iconButton = new IconButton(image, "Spotlight", () -> {
			handler.run();
			// handle switch on/off style
		});
		add(iconButton);
	}

	private void addCategoryButton(SVGResource image, List<Integer> tools) {
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
	}

	private void addDivider() {
		SimplePanel divider = new SimplePanel();
		divider.setStyleName("divider");

		add(divider);
	}
}
