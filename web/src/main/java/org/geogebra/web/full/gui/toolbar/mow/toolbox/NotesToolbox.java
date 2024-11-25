package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SELECT_MOW;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.ModeChangeListener;
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
import org.geogebra.web.shared.mow.header.NotesTopBar;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.SimplePanel;

public class NotesToolbox extends FlowPanel implements SetLabels, ModeChangeListener {
	private final AppW appW;
	private final ToolboxDecorator decorator;
	private final ToolboxController controller;
	private @CheckForNull IconButton spotlightButton;
	private final List<IconButton> buttons = new ArrayList<>();

	/**
	 * MOW toolbox
	 * @param appW - application
	 * @param isTopBarAttached - whether it has {@link NotesTopBar} or not
	 */
	public NotesToolbox(AppW appW, boolean isTopBarAttached) {
		this.appW = appW;
		decorator = new ToolboxDecorator(this, isTopBarAttached);
		controller = new ToolboxController(appW, this);
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
				this::deselectButtons, this);
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
				ToolboxConstants.uploadCategory);
	}

	private void addLinkButton() {
		if (!appW.isToolboxCategoryEnabled(ToolboxCategory.LINK.getName())) {
			return;
		}

		List<Integer> linkTools = ToolboxConstants.linkCategory;
		addToggleButtonWithMenuPopup(MaterialDesignResources.INSTANCE.resource_card_shared(),
				"Link", linkTools);
	}

	private void addSelectModeButton() {
		if (!appW.isToolboxCategoryEnabled(ToolboxCategory.SELECT.getName())) {
			return;
		}

		IconButton selectButton = new IconButton(MODE_SELECT_MOW, appW,
				MaterialDesignResources.INSTANCE.mouse_cursor(),
				() -> {
					appW.setMode(MODE_SELECT_MOW);
					appW.closePopups();
				});
		add(selectButton);
		buttons.add(selectButton);
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
				ToolboxConstants.shapeCategory);
	}

	private void addAppsButton() {
		if (!appW.isToolboxCategoryEnabled(ToolboxCategory.MORE.getName())) {
			return;
		}

		List<Integer> appsTools
				= ToolboxConstants.getAppsCategory(Browser.isGraspableMathEnabled());
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

	@Override
	public void onModeChange(int mode) {
		for (IconButton button : buttons) {
			if (button instanceof RulerIconButton) {
				continue;
			}
			if (button.containsMode(mode)) {
				button.setActive(true);
			} else {
				button.deactivate();
			}
		}
	}
}