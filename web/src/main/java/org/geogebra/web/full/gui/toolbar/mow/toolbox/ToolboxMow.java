package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.html5.css.ZoomPanelResources;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RootPanel;
import org.gwtproject.user.client.ui.SimplePanel;

public class ToolboxMow extends FlowPanel implements SetLabels {
	private final AppW appW;
	private ToolboxDecorator decorator;
	private ToolboxController controller;
	private IconButton spotlightButton;
	private RulerIconButton rulerButton;
	private IconButton moveModeButton;

	/**
	 * MOW toolbox
	 * @param appW - application
	 */
	public ToolboxMow(AppW appW) {
		this.appW = appW;
		decorator = new ToolboxDecorator(this);
		controller = new ToolboxController(appW, this);
		RootPanel.get().add(this);
		buildGui();
	}

	private void buildGui() {
		decorator.positionLeft();

		addSpotlightButton();

		addDivider();

		addRulerButton();

		addMoveModeButton();
	}

	private IconButton addPressButton(SVGResource image, String ariaLabel, String dataTest,
			Runnable onHandler) {
		IconButton iconButton = new IconButton(appW.getLocalization(), image, ariaLabel, ariaLabel,
				dataTest, onHandler);
		add(iconButton);
		return iconButton;
	}

	private IconButton addToggleButton(SVGResource image, String ariaLabel, String dataTitle,
			String dataTest, Runnable onHandler, Runnable offHandler) {
		IconButton iconButton = new IconButton(appW, image, ariaLabel, dataTitle,
				dataTest, onHandler, offHandler);
		add(iconButton);
		return iconButton;
	}

	private void addDivider() {
		SimplePanel divider = new SimplePanel();
		divider.setStyleName("divider");

		add(divider);
	}

	/**
	 * switch spotlight button off
	 */
	public void switchSpotlightOff() {
		spotlightButton.setActive(false,
				appW.getGeoGebraElement().getDarkColor(appW.getFrameElement()));
	}

	private void addSpotlightButton() {
		spotlightButton = addToggleButton(ZoomPanelResources.INSTANCE.target(), "Spotlight.Tool",
				"Spotlight.Tool", "spotlightTool",
				controller.getSpotlightOnHandler(), () -> {});
	}

	private void addRulerButton() {
		String ariaLabel = appW.getToolName(MODE_RULER) + ". " + appW.getToolHelp(MODE_RULER);
		rulerButton = new RulerIconButton(appW,
				ToolbarSvgResources.INSTANCE.mode_ruler(), ariaLabel, "Ruler",
				"selectModeButton" + MODE_RULER);
		add(rulerButton);
	}

	private void addMoveModeButton() {
		moveModeButton = addPressButton(ToolbarSvgResources.INSTANCE.mode_pen(),
				"move mode", "moveBtn", appW::setMoveMode);
	}

	@Override
	public void setLabels() {
		spotlightButton.setLabels();
		rulerButton.setLabels();
		moveModeButton.setLabels();
	}
}