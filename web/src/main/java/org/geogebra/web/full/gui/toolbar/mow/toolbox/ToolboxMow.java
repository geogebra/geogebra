package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_AUDIO;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CALCULATOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CAMERA;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_EXTENSION;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_GRASPABLE_MATH;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_H5P;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_IMAGE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MASK;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MIND_MAP;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PDF;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SELECT_MOW;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_CIRCLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_ELLIPSE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_FREEFORM;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_LINE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_PENTAGON;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_RECTANGLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_SQUARE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_TRIANGLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TABLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_VIDEO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButtonWithMenu;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButtonWithPopup;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.pen.PenIconButton;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.ruler.RulerIconButton;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.text.TextIconButton;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.css.ZoomPanelResources;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RootPanel;
import org.gwtproject.user.client.ui.SimplePanel;

public class ToolboxMow extends FlowPanel implements SetLabels {
	private final AppW appW;
	private final ToolboxDecorator decorator;
	private final ToolboxController controller;
	private @CheckForNull IconButton spotlightButton;
	private @CheckForNull IconButton selectButton;
	private final List<IconButton> buttons = new ArrayList<>();
	private final static List<Integer> uploadCategory = Arrays.asList(MODE_IMAGE, MODE_CAMERA,
			MODE_PDF);
	private final static List<Integer> linkCategory = Arrays.asList(
			MODE_EXTENSION, MODE_VIDEO, MODE_AUDIO);
	private final static List<Integer> shapeCategory = Arrays.asList(MODE_SHAPE_RECTANGLE,
			MODE_SHAPE_SQUARE , MODE_SHAPE_TRIANGLE , MODE_SHAPE_CIRCLE , MODE_SHAPE_ELLIPSE,
			MODE_SHAPE_PENTAGON, MODE_SHAPE_LINE, MODE_SHAPE_FREEFORM, MODE_MASK);
	private final static List<Integer> appsCategory = Arrays.asList(
			MODE_CALCULATOR, MODE_MIND_MAP, MODE_TABLE);

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

		addSelectModeButton();
		addPenModeButton();
		addShapeButton();
		addTextButton();
		addUploadButton();
		addLinkButton();
		addAppsButton();

		if (shouldAddDivider()) {
			addDivider();
		}

		addRulerButton();
		addSpotlightButton();
	}

	private boolean shouldAddDivider() {
		return appW.isToolboxCategoryEnabled(ToolboxCategory.SPOTLIGHT.getName())
				|| appW.isToolboxCategoryEnabled(ToolboxCategory.RULER.getName());
	}

	private IconButton addPressButton(SVGResource image, String ariaLabel, String dataTest,
			Runnable onHandler) {
		IconButton iconButton = new IconButton(appW, image, ariaLabel, ariaLabel, dataTest,
				onHandler);
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

	private void addToggleButtonWithMenuPopup(SVGResource image, String ariaLabel,
			List<Integer> tools) {
		IconButton iconButton = new IconButtonWithMenu(appW, image, ariaLabel, tools,
				this::deselectButtons);
		add(iconButton);
		buttons.add(iconButton);
	}

	private void addToggleButtonWithPopup(SVGResource image, String ariaLabel,
			List<Integer> tools) {
		IconButtonWithPopup iconButton = new IconButtonWithPopup(appW, image, ariaLabel, tools,
				this::deselectButtons);
		add(iconButton);
		buttons.add(iconButton);
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
		if (spotlightButton != null) {
			spotlightButton.deactivate();
		}
	}

	private void addSpotlightButton() {
		if (!appW.isToolboxCategoryEnabled(ToolboxCategory.SPOTLIGHT.getName())) {
			return;
		}

		spotlightButton = addToggleButton(ZoomPanelResources.INSTANCE.target(), "Spotlight.Tool",
				"Spotlight.Tool", "spotlightTool",
				controller.getSpotlightOnHandler(), () -> {});
	}

	private void addRulerButton() {
		if (!appW.isToolboxCategoryEnabled(ToolboxCategory.RULER.getName())) {
			return;
		}

		RulerIconButton rulerButton = new RulerIconButton(appW,
				ToolbarSvgResources.INSTANCE.mode_ruler(), appW.getToolAriaLabel(MODE_RULER),
				"Ruler", "selectModeButton" + MODE_RULER);
		add(rulerButton);
		buttons.add(rulerButton);
	}

	private void addTextButton() {
		if (!appW.isToolboxCategoryEnabled(ToolboxCategory.TEXT.getName())) {
			return;
		}

		TextIconButton textButton = new TextIconButton(appW, this::deselectButtons);
		add(textButton);
		buttons.add(textButton);
	}

	private void addUploadButton() {
		if (!appW.isToolboxCategoryEnabled(ToolboxCategory.UPLOAD.getName())) {
			return;
		}

		addToggleButtonWithMenuPopup(MaterialDesignResources.INSTANCE.upload(), "Upload",
				uploadCategory);
	}

	private void addLinkButton() {
		if (!appW.isToolboxCategoryEnabled(ToolboxCategory.LINK.getName())) {
			return;
		}

		List<Integer> linkTools = appW.getVendorSettings().isH5PEnabled()
				? concat(linkCategory, MODE_H5P) : linkCategory;
		addToggleButtonWithMenuPopup(MaterialDesignResources.INSTANCE.resource_card_shared(),
				"Link", linkTools);
	}

	private List<Integer> concat(List<Integer> tools, int tool) {
		return Stream.concat(tools.stream(), Stream.of(tool)).collect(Collectors.toList());
	}

	private void addSelectModeButton() {
		if (!appW.isToolboxCategoryEnabled(ToolboxCategory.SELECT.getName())) {
			return;
		}

		selectButton = addPressButton(MaterialDesignResources.INSTANCE.mouse_cursor(),
				appW.getToolName(MODE_SELECT_MOW), appW.getToolName(MODE_SELECT_MOW),
				() -> appW.setMode(MODE_SELECT_MOW));
	}

	private void addPenModeButton() {
		if (!appW.isToolboxCategoryEnabled(ToolboxCategory.PEN.getName())) {
			return;
		}

		IconButton iconButton = new PenIconButton(appW, this::deselectButtons);
		iconButton.setActive(true);
		add(iconButton);
		buttons.add(iconButton);
	}

	private void addShapeButton() {
		if (!appW.isToolboxCategoryEnabled(ToolboxCategory.SHAPES.getName())) {
			return;
		}

		addToggleButtonWithPopup(MaterialDesignResources.INSTANCE.shapes(), "Shape",
				shapeCategory);
	}

	private void addAppsButton() {
		if (!appW.isToolboxCategoryEnabled(ToolboxCategory.MORE.getName())) {
			return;
		}

		List<Integer> appsTools = Browser.isGraspableMathEnabled()
				? concat(appsCategory, MODE_GRASPABLE_MATH) : appsCategory;

		addToggleButtonWithMenuPopup(MaterialDesignResources.INSTANCE.apps(), "Tools.More",
				appsTools);
	}

	@Override
	public void setLabels() {
		buttons.forEach(SetLabels::setLabels);
	}

	private void deselectButtons() {
		for (IconButton button : buttons) {
			if (!(button instanceof RulerIconButton)) {
				button.deactivate();
			}
		}
	}

	/**
	 * @param mode - tool mode
	 */
	public void setMode(int mode) {
		if (MODE_SELECT_MOW == mode) {
			deselectButtons();
			if (selectButton != null) {
				selectButton.setActive(true);
			}
		}
	}
}