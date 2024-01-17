package org.geogebra.web.shared.components;

import org.geogebra.common.euclidian.event.FocusListenerDelegate;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.keyboard.base.KeyboardType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.keyboard.KeyboardManager;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.keyboard.KeyboardManagerInterface;
import org.gwtproject.user.client.ui.FlowPanel;

public class ComponentSearchBar extends FlowPanel implements FocusListenerDelegate {
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

		inputTextField = new InputPanelW(app, -1, false);
		inputTextField.getTextComponent().getTextBox().getElement().setAttribute(
				"placeholder", app.getLocalization().getMenu("search_geogebra_materials"));
		inputTextField.addStyleName("searchInputField");
		inputTextField.getTextComponent().addFocusListener(this);
		inputTextField.getTextComponent().addKeyHandler(evt -> {
			if (evt.isEnterKey()) {
				startSearch();
			}
		});
		inputTextField.getTextComponent().getTextBox().addKeyUpHandler(evt -> {
			clearButton.setVisible(!inputTextField.getText().isEmpty());
		});
		add(inputTextField);

		addClearButton();
	}

	/**
	 * sets the style of InputPanel to focus state and shows keyboard
	 */
	protected void setFocusState() {
		addStyleName("focusState");
		if (NavigatorUtil.isMobile()) {
			KeyboardManagerInterface keyboard = app.getKeyboardManager();
			if (keyboard instanceof KeyboardManager) {
				((KeyboardManager) keyboard).resizeKeyboard();
				((KeyboardManager) keyboard).selectTab(KeyboardType.ABC);
			}
			app.showKeyboard(inputTextField.getTextComponent(), true);
		}
	}

	/**
	 * Resets input style on blur and hides keyboard
	 */
	public void removeFocusState() {
		removeStyleName("focusState");
		app.hideKeyboard();
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

	@Override
	public void focusLost() {
		removeStyleName("focusState");
		app.hideKeyboard();
	}

	@Override
	public void focusGained() {
		addStyleName("focusState");
		if (NavigatorUtil.isMobile()) {
			KeyboardManagerInterface keyboard = app.getKeyboardManager();
			if (keyboard instanceof KeyboardManager) {
				((KeyboardManager) keyboard).selectTab(KeyboardType.ABC);
			}
			app.showKeyboard(inputTextField.getTextComponent(), true);
		}
	}
}
