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
	private Consumer<Integer> updateParentCallback;
	private IconButton lastSelectedButton;
	private FlowPanel contentPanel;
	private List<IconButton> buttons = new ArrayList<>();
	private Integer defaultTool;

	/**
	 * Constructor
	 * @param app - application
	 * @param tools - list of tools
	 * @param updateParentCallback - callback to update anchor
	 */
	public CategoryPopup(AppW app, List<Integer> tools, Consumer<Integer> updateParentCallback) {
		super(app.getAppletFrame(), app);
		setAutoHideEnabled(true);
		this.updateParentCallback = updateParentCallback;
		defaultTool = tools.get(0);

		addStyleName("categoryPopup");
		buildBaseGui(tools);
	}

	public void addContent(Widget widget) {
		contentPanel.add(widget);
	}

	private void buildBaseGui(List<Integer> tools) {
		contentPanel = new FlowPanel();
		for (Integer mode : tools) {
			IconButton button = new IconButton(mode, (AppW) app);
			if (defaultTool == mode) {
				app.setMode(mode);
				updateButtonSelection(button);
			}
			buttons.add(button);
			button.addFastClickHandler(source -> {
				app.setMode(mode);
				updateParentCallback.accept(mode);
				updateButtonSelection(button);
				hide();
			});
			contentPanel.add(button);
		}
		add(contentPanel);
	}

	private void updateButtonSelection(IconButton newSelectedButton) {
		if (lastSelectedButton != null) {
			lastSelectedButton.deactivate();
		}

		lastSelectedButton = newSelectedButton;
		lastSelectedButton.setActive(true);
	}

	public Integer getLastSelectedMode() {
		return lastSelectedButton != null ? lastSelectedButton.getMode() : -1;
	}

	@Override
	public void setLabels() {
		buttons.forEach(SetLabels::setLabels);
	}
}
