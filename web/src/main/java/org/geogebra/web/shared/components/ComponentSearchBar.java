package org.geogebra.web.shared.components;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;

public class ComponentSearchBar extends FlowPanel {
	private final AppW app;
	private InputPanelW inputTextField;
	private StandardButton clearButton;

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

		clearButton = new StandardButton(MaterialDesignResources.INSTANCE.clear(), 24);
		clearButton.setVisible(false);

		inputTextField = new InputPanelW("", app, -1, -1, false);
		inputTextField.getTextComponent().getTextBox().getElement().setAttribute(
				"placeholder", app.getLocalization().getMenu("search_geogebra_materials"));
		inputTextField.addStyleName("searchInputField");
		inputTextField.getTextComponent().addKeyHandler(evt -> {
			if (evt.isEnterKey()) {
				startSearch();
				inputTextField.getTextComponent().getTextBox().setFocus(false);
			}
		});
		inputTextField.getTextComponent().getTextBox().addKeyUpHandler(evt -> {
			clearButton.setVisible(!inputTextField.getText().isEmpty());
		});
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
		clearButton.addStyleName("clearBtn");
		clearButton.addStyleName("flatButtonHeader");
		Dom.addEventListener(clearButton.getElement(), "pointerdown", (event) -> {
			inputTextField.getTextComponent().setText("");
			clearButton.setVisible(false);
			if (!getElement().isOrHasChild(Dom.getActiveElement())) {
				startSearch();
			} else {
				event.preventDefault(); // do not lose focus
			}
		});

		add(clearButton);
	}

	private void addSearchButton() {
		StandardButton searchButton = new StandardButton(
				MaterialDesignResources.INSTANCE.search_black(), 24);
		searchButton.addStyleName("searchBtn");
		searchButton.addStyleName("flatButtonHeader");
		searchButton.addFastClickHandler((event) -> {
			startSearch();
		});

		add(searchButton);
	}

	private void startSearch() {
		app.getGuiManager().getBrowseView().displaySearchResults(inputTextField.getText());
	}
}
