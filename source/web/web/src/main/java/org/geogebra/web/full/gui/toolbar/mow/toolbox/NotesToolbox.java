package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SELECT_MOW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.ModeChangeListener;
import org.geogebra.common.exam.ExamListener;
import org.geogebra.common.exam.ExamState;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButtonWithMenu;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButtonWithPopup;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.ToolIconButton;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.pen.PenIconButton;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.ruler.RulerIconButton;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.text.TextIconButton;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.toolbox.ToolboxIcon;
import org.geogebra.web.html5.main.toolbox.ToolboxIconResource;
import org.geogebra.web.shared.mow.header.NotesTopBar;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.SimplePanel;

public class NotesToolbox extends FlowPanel implements SetLabels, ModeChangeListener, ExamListener {
	private final AppW appW;
	private final ToolboxDecorator decorator;
	private final ToolboxController controller;
	private final List<String> allTools;
	private @CheckForNull IconButton spotlightButton;
	private final List<IconButton> buttons = new ArrayList<>();
	private final ToolboxIconResource toolboxIconResource;
	private IconButton lastSelectedButtonWithMenu;
	private final Map<ToolboxCategory, IconButtonWithMenu> buttonsWithMenu = new HashMap<>();
	private ExamState renderedExamState = ExamState.IDLE;

	/**
	 * MOW toolbox
	 * @param appW - application
	 * @param isTopBarAttached - whether it has {@link NotesTopBar} or not
	 */
	public NotesToolbox(AppWFull appW, boolean isTopBarAttached) {
		this.appW = appW;
		decorator = new ToolboxDecorator(this, isTopBarAttached);
		controller = new ToolboxController(appW, this);
		toolboxIconResource = appW.getToolboxIconResource();
		allTools = appW.getAppletParameters().getDataParamCustomToolbox();
		appW.getExamEventBus().add(this);
		buildGui();
	}

	private void buildGui() {
		decorator.positionLeft();
		decorator.examMode(renderedExamState != ExamState.IDLE);

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

	private IconButton addToggleButton(IconSpec image,
			Runnable onHandler, Runnable offHandler) {
		IconButton iconButton = new IconButton(appW, image, "Spotlight.Tool", "Spotlight.Tool",
				"spotlightTool", onHandler, offHandler);
		add(iconButton);
		buttons.add(iconButton);
		return iconButton;
	}

	private void addToggleButtonWithMenuPopup(ToolboxCategory category, IconSpec image,
											  String ariaLabel, List<Integer> tools) {
		IconButtonWithMenu iconButton = new IconButtonWithMenu(appW, image, ariaLabel, tools,
				this::deselectButtons, this);
		add(iconButton);
		buttons.add(iconButton);
		buttonsWithMenu.put(category, iconButton);
	}

	private void addShapeToggleButton(IconSpec image, List<Integer> tools) {
		IconButtonWithPopup iconButton = new IconButtonWithPopup(appW, image, "Shape",
				tools, this::deselectButtons);
		add(iconButton);
		buttons.add(iconButton);
	}

	private List<Integer> filterTools(ToolboxCategory toolboxCategory) {
		Predicate<Integer> baseFilter = tool ->
			tool != EuclidianConstants.MODE_GRASPABLE_MATH
					|| Browser.isGeogebraOrInternalHost() && renderedExamState == ExamState.IDLE;
		Stream<Integer> available = toolboxCategory.getTools().stream().filter(baseFilter);
		if (allTools.isEmpty() || allTools.contains(toolboxCategory.getName())) {
			return available.collect(Collectors.toList());
		}
		List<String> inCategory = allTools.stream().filter(tool -> tool.contains("."))
				.map(tool -> tool.split("\\.")[1].toLowerCase(Locale.ROOT))
				.collect(Collectors.toList());
		return available
				.filter(tool -> inCategory.contains(
						normalizeIconName(tool).toLowerCase(Locale.ROOT)))
				.collect(Collectors.toList());
	}

	private String normalizeIconName(Integer tool) {
		return EuclidianConstants.getModeIconName(tool).replaceAll("^(Shape|Type\\.)", "");
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

		spotlightButton = addToggleButton(toolboxIconResource.getImageResource(
				ToolboxIcon.SPOTLIGHT),
				() -> {}, () -> {});
		spotlightButton.addFastClickHandler((source) -> {
			if (spotlightButton.isActive()) {
				controller.switchSpotlightOn();
			} else {
				controller.switchSpotlightOff();
			}
		});
	}

	private void addRulerButton() {
		if (!appW.isToolboxCategoryEnabled(ToolboxCategory.RULER.getName())) {
			return;
		}

		RulerIconButton rulerButton = new RulerIconButton(appW,
				toolboxIconResource.getImageResource(ToolboxIcon.RULER),
				appW.getToolAriaLabel(MODE_RULER),
				"Ruler", "selectModeButton" + MODE_RULER);
		add(rulerButton);
		buttons.add(rulerButton);
	}

	private void addTextButton() {
		List<Integer> tools = filterTools(ToolboxCategory.TEXT);
		if (!tools.isEmpty()) {
			TextIconButton textButton = new TextIconButton(appW, this::deselectButtons,
					tools);
			add(textButton);
			buttons.add(textButton);
		}
	}

	private void addUploadButton() {
		List<Integer> tools = filterTools(ToolboxCategory.UPLOAD);
		if (!tools.isEmpty() && renderedExamState == ExamState.IDLE) {
			addToggleButtonWithMenuPopup(ToolboxCategory.UPLOAD,
					toolboxIconResource.getImageResource(ToolboxIcon.UPLOAD),
					"Upload", tools);
		}
	}

	private void addLinkButton() {
		List<Integer> tools = filterTools(ToolboxCategory.LINK);
		if (!tools.isEmpty() && renderedExamState == ExamState.IDLE) {
			addToggleButtonWithMenuPopup(ToolboxCategory.LINK,
					toolboxIconResource.getImageResource(ToolboxIcon.LINK),
					"Link", tools);
		}
	}

	private void addSelectModeButton() {
		if (!appW.isToolboxCategoryEnabled(ToolboxCategory.SELECT.getName())) {
			return;
		}

		IconButton selectButton = new ToolIconButton(MODE_SELECT_MOW, appW,
				toolboxIconResource.getImageResource(ToolboxIcon.MOUSE_CURSOR),
				() -> {
					appW.setMode(MODE_SELECT_MOW);
					appW.closePopups();
				});
		add(selectButton);
		buttons.add(selectButton);
	}

	private void addPenModeButton() {
		List<Integer> tools = filterTools(ToolboxCategory.PEN);
		if (!tools.isEmpty()) {
			IconButton iconButton = new PenIconButton(appW, tools, this::deselectButtons);
			iconButton.setActive(true);
			add(iconButton);
			buttons.add(iconButton);
		}
	}

	private void addShapeButton() {
		List<Integer> tools = filterTools(ToolboxCategory.SHAPES);
		if (!tools.isEmpty()) {
			addShapeToggleButton(toolboxIconResource.getImageResource(ToolboxIcon.SHAPES),
					tools);
		}
	}

	private void addAppsButton() {
		List<Integer> tools = filterTools(ToolboxCategory.MORE);
		if (!tools.isEmpty()) {
			addToggleButtonWithMenuPopup(ToolboxCategory.MORE,
					toolboxIconResource.getImageResource(ToolboxIcon.APPS),
					"Tools.More", tools);
		}
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
	 * disable buttons other than spotlight
	 */
	public void disableNonSpotlightButtons() {
		for (IconButton button : buttons) {
			if (button != spotlightButton) {
				button.addStyleName("disabled");
			}
		}
	}

	@Override
	public void onModeChange(int mode) {
		for (IconButton button : buttons) {
			if (button instanceof RulerIconButton) {
				continue;
			}
			if (button.containsMode(mode) && lastSelectedButtonWithMenu == null) {
				button.setActive(true);
			} else {
				button.deactivate();
			}
		}
	}

	public void setLastSelectedButtonWithMenu(IconButton button) {
		lastSelectedButtonWithMenu = button;
	}

	/**
	 * Notify toolbox about deselecting a tool button
	 * @param button deselected tool button
	 */
	public void removeSelectedButtonWithMenu(IconButton button) {
		if (lastSelectedButtonWithMenu == button) {
			lastSelectedButtonWithMenu = null;
		}
	}

	/**
	 * Add a custom tool with given properties
	 *
	 * @param url the URL of the tool icon.
	 * @param name The name of the tool.
	 * @param category to put the tool in.
	 * @param callback the action of the tool.
	 */
	public void addCustomTool(String url, String name, String category, Object callback) {
		IconButtonWithMenu button = buttonsWithMenu.get(ToolboxCategory.byName(category));
        if (button != null) {
			button.addCustomTool(url, name, callback);
        } else {
			Log.debug("Category " + category.toLowerCase(Locale.ROOT)
					+ " is not allowed to have a custom tool");
		}
	}

	@Override
	public void examStateChanged(ExamState newState) {
		this.renderedExamState = newState;
		clear();
		buildGui();
	}
}