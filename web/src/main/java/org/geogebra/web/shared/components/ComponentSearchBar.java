package org.geogebra.web.shared.components;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;

public class ComponentSearchBar extends FlowPanel {
	private final AppW app;
	private InputPanelW inputTextField;

	/**
	 * @param app - application
	 */
	public ComponentSearchBar(AppW app) {
		this.app = app;
		addStyleName("searchBar");
		buildGUI();
	}

	private void buildGUI() {
		addSearchButton();

		inputTextField = new InputPanelW("", app, -1, 1, false);
		inputTextField.getTextComponent().getTextBox().getElement().setAttribute(
				"placeholder", app.getLocalization().getMenu("search_geogebra_materials"));
		inputTextField.addStyleName("searchInputField");
		add(inputTextField);
		addFocusBlurHandlers();

		addClearButton();
	}

	private void addFocusBlurHandlers() {
		inputTextField.getTextComponent().getTextBox()
				.addFocusHandler(event -> setFocusState());
		inputTextField.getTextComponent().getTextBox()
				.addBlurHandler(event -> resetInputField());
	}

	/**
	 * sets the style of InputPanel to focus state
	 */
	protected void setFocusState() {
		addStyleName("focusState");
	}

	/**
	 * Resets input style on blur
	 */
	public void resetInputField() {
		removeStyleName("focusState");
	}

	private void addClearButton() {
		StandardButton clearButton = new StandardButton(
				MaterialDesignResources.INSTANCE.clear(), 24);
		clearButton.addStyleName("clearBtn");
		clearButton.addStyleName("flatButtonHeader");
		clearButton.addFastClickHandler((event) -> inputTextField.getTextComponent().setText(""));

		add(clearButton);
	}

	private void addSearchButton() {
		StandardButton searchButton = new StandardButton(
				MaterialDesignResources.INSTANCE.search_black(), 24);
		searchButton.addStyleName("searchBtn");
		searchButton.addStyleName("flatButtonHeader");
		searchButton.addFastClickHandler((event) -> {
			app.getGuiManager().getBrowseView().displaySearchResults(inputTextField.getText());
		});

		add(searchButton);
	}
}
