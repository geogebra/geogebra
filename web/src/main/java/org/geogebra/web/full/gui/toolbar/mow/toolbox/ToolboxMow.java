package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SELECT_MOW;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.web.full.css.MaterialDesignResources;
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
	private final List<SetLabels> buttons = new ArrayList<>();

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

		addMoveModeButton();
		addPenModeButton();
		addUploadButton();
		addLinkButton();

		addDivider();

		addRulerButton();
		addSpotlightButton();
	}

	private IconButton addPressButton(SVGResource image, String ariaLabel, String dataTest,
			Runnable onHandler) {
		IconButton iconButton = new IconButton(appW.getLocalization(), image, ariaLabel, ariaLabel,
				dataTest, onHandler);
		add(iconButton);
		buttons.add(iconButton);
		return iconButton;
	}

	private IconButton addToggleButton(SVGResource image, String ariaLabel, String dataTitle,
			String dataTest, Runnable onHandler, Runnable offHandler) {
		IconButton iconButton = new IconButton(appW, image, ariaLabel, dataTitle,
				dataTest, onHandler, offHandler);
		add(iconButton);
		buttons.add(iconButton);
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
		RulerIconButton rulerButton = new RulerIconButton(appW,
				ToolbarSvgResources.INSTANCE.mode_ruler(), getToolAriaLabel(MODE_RULER), "Ruler",
				"selectModeButton" + MODE_RULER);
		add(rulerButton);
		buttons.add(rulerButton);
	}

	private void addUploadButton() {
		addToggleButton(MaterialDesignResources.INSTANCE.upload(), "Upload", "Upload",
				"", () -> {}, () -> {});
	}

	private void addLinkButton() {
		addToggleButton(MaterialDesignResources.INSTANCE.resource_card_shared(), "Link", "Link",
				"", () -> {}, () -> {});
	}

	private void addMoveModeButton() {
		addToggleButton(MaterialDesignResources.INSTANCE.mouse_cursor(),
				getToolAriaLabel(MODE_SELECT_MOW), getToolDataTitle(MODE_SELECT_MOW), "",
				() -> appW.setMode(EuclidianConstants.MODE_SELECT_MOW, ModeSetter.DOCK_PANEL),
				null);
	}

	private void addPenModeButton() {
		addPressButton(ToolbarSvgResources.INSTANCE.mode_pen(),
				"pen mode", "penBtn", appW::setMoveMode);
	}

	private String getToolAriaLabel(int mode) {
		return appW.getToolName(mode) + ". " + appW.getToolHelp(mode);
	}

	private String getToolDataTitle(int mode) {
		return appW.getToolName(mode);
	}

	@Override
	public void setLabels() {
		buttons.forEach(SetLabels::setLabels);
	}
}