package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PEN;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.html5.css.ZoomPanelResources;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RootPanel;
import org.gwtproject.user.client.ui.SimplePanel;

public class ToolboxMow extends FlowPanel implements SetLabels {
	public final static int TOOLBOX_PADDING = 8;
	private final AppW appW;
	private ToolboxDecorator decorator;
	private ToolboxController controller;
	private IconButton spotlightButton;
	private final List<IconButton> buttons = new ArrayList<>();
	private final static List<Integer> uploadCategory = Arrays.asList(MODE_IMAGE, MODE_CAMERA,
			MODE_PDF);
	private final static List<Integer> linkCategory = Arrays.asList(MODE_EXTENSION, MODE_VIDEO,
			MODE_AUDIO, MODE_H5P);

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
		addPressButton(ToolbarSvgResources.INSTANCE.mode_pen(),
				"pen mode", "penBtn", () -> {
					appW.setMode(MODE_PEN, ModeSetter.TOOLBAR);
					GPopupPanel popup = new GPopupPanel(appW.getAppletFrame(), appW);
					popup.add(new ColorChooserPanel(appW, (color)
							-> getPen().setPenColor(color)));
					popup.showRelativeTo(this);
				});
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

	private IconButton addToggleButtonWithPopup(SVGResource image, String ariaLabel,
			List<Integer> tools) {
		IconButton iconButton = new IconButtonWithPopup(appW, image, ariaLabel, tools,
				() -> deselectButtons());
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
		spotlightButton.deactivate();
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
		addToggleButtonWithPopup(MaterialDesignResources.INSTANCE.upload(), "Upload",
				uploadCategory);
	}

	private void addLinkButton() {
		addToggleButtonWithPopup(MaterialDesignResources.INSTANCE.resource_card_shared(), "Link",
				linkCategory);
	}

	private void addMoveModeButton() {
		addToggleButton(MaterialDesignResources.INSTANCE.mouse_cursor(),
				getToolAriaLabel(MODE_SELECT_MOW), getToolDataTitle(MODE_SELECT_MOW), "",
				() -> {
			deselectButtons();
			appW.setMode(EuclidianConstants.MODE_SELECT_MOW, ModeSetter.DOCK_PANEL);
			}, null);
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

	private EuclidianPen getPen() {
		return appW.getActiveEuclidianView().getEuclidianController()
				.getPen();
	}

	private void deselectButtons() {
		buttons.forEach(IconButton::deactivate);
	}
}