package org.geogebra.web.full.gui.toolbar.mow.toolbox.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

public class CategoryPopup extends GPopupPanel implements SetLabels {
	private final Consumer<Integer> updateParentCallback;
	private IconButton lastSelectedButton;
	private FlowPanel contentPanel;
	private final List<IconButton> buttons = new ArrayList<>();
	private final Integer defaultTool;
	private boolean preventHide;

	/**
	 * Constructor
	 * @param app - application
	 * @param tools - list of tools
	 * @param updateParentCallback - callback to update anchor
	 * @param preventHide - whether it should close on tool selection or not
	 */
	public CategoryPopup(AppW app, List<Integer> tools, Consumer<Integer> updateParentCallback,
			boolean preventHide) {
		super(app.getAppletFrame(), app);
		setAutoHideEnabled(true);
		this.updateParentCallback = updateParentCallback;
		this.preventHide = preventHide;
		defaultTool = tools.get(0);

		addStyleName("categoryPopup");
		buildBaseGui(tools);
	}

	/**
	 * Constructor
	 * @param app - application
	 * @param tools - list of tools
	 * @param updateParentCallback - callback to update anchor
	 */
	public CategoryPopup(AppW app, List<Integer> tools, Consumer<Integer> updateParentCallback) {
		this(app, tools, updateParentCallback, false);
	}

	public void addContent(Widget widget) {
		contentPanel.add(widget);
	}

	private void buildBaseGui(List<Integer> tools) {
		contentPanel = new FlowPanel();

		FlowPanel toolsPanel = new FlowPanel();
		toolsPanel.addStyleName("toolsHolder");

		for (Integer mode : tools) {
			IconButton button = createButton(mode);
			if (defaultTool.equals(mode)) {
				app.setMode(mode);
				updateButtonSelection(button);
			}
			toolsPanel.add(button);
		}

		contentPanel.add(toolsPanel);
		add(contentPanel);
	}

	private IconButton createButton(Integer mode) {
		IconButton button = new IconButton(mode, (AppW) app);
		button.addFastClickHandler(source -> {
			app.setMode(mode);
			updateButtonSelection(button);
			updateParentCallback.accept(mode);
			if (!preventHide) {
				hide();
			}
		});
		buttons.add(button);
		return button;
	}

	private void updateButtonSelection(IconButton newSelectedButton) {
		if (lastSelectedButton != null) {
			lastSelectedButton.deactivate();
		}

		lastSelectedButton = newSelectedButton;
		lastSelectedButton.setActive(true);
	}

	public int getLastSelectedMode() {
		return lastSelectedButton != null ? lastSelectedButton.getMode() : -1;
	}

	@Override
	public void setLabels() {
		buttons.forEach(SetLabels::setLabels);
	}
}
