package org.geogebra.web.full.gui.layout.scientific;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbarpanel.MenuToggleButton;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Builds and styles the Scientific Calculator header with all its buttons.
 */
class HeaderBuilder {

	private static final int BUTTON_SIZE = 24;
	private static final String SMALL_SCREEN_HEADER_STYLE = "smallScreenHeaderScientific";
	private static final String FLAT_BUTTON_STYLE = "flatButtonHeader";
	private static final String MENU_BUTTON_STYLE = "menuBtnScientific";
	private static final String SETTINGS_BUTTON_STYLE = "settingsBtnScientific";
	private static final String UNDO_REDO_CNT_STYLE = "undoRedoCntScientific";
	private static final String UNDO_BTN_STYLE = "undoBtnScientific";

	private Panel header;
	private AppW app;

	/**
	 * @param app the app
	 */
	HeaderBuilder(AppW app) {
		this.app = app;
	}

	/**
	 * @return see {@link AppW}
	 */
	public AppW getAppW() {
		return app;
	}

	/**
	 * Builds and styles the Scientific Calculator header with all its buttons
	 *
	 * @return the built Scientific Calculator header
	 */
	Panel buildHeader() {
		if (header == null) {
			header = createStyledHeaderWithButtons();
		}
		return header;
	}

	private Panel createStyledHeaderWithButtons() {
		header = new FlowPanel();
		styleAndAddButtons();
		header.setStyleName(SMALL_SCREEN_HEADER_STYLE);
		return header;
	}

	private void styleAndAddButtons() {
		styleAndAddMenuButton();
		styleAndAddSettingsButton();
		styleAndAddUndoRedoButtons();
	}

	private void styleAndAddMenuButton() {
		MenuToggleButton menuBtn = new MenuToggleButton(app);
		addStylesTo(menuBtn, FLAT_BUTTON_STYLE, MENU_BUTTON_STYLE);
		header.add(menuBtn);
	}

	private void styleAndAddSettingsButton() {
		StandardButton settingsButton =
				createStandardButtonWithIcon(MaterialDesignResources.INSTANCE.gear());
		settingsButton.setTitleWithLocalizationKey("Settings");
		settingsButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				getAppW().getGuiManager().showSciSettingsView();
			}
		});
		addStylesTo(settingsButton, FLAT_BUTTON_STYLE, SETTINGS_BUTTON_STYLE);
		header.add(settingsButton);
	}

	private void styleAndAddUndoRedoButtons() {
		FlowPanel container = new FlowPanel();
		container.addStyleName(UNDO_REDO_CNT_STYLE);
		styleAndAddUndoButtonToContainer(container);
		styleAndAddRedoButtonToContainer(container);
		header.add(container);
	}

	private void styleAndAddUndoButtonToContainer(Panel container) {
		StandardButton undoButton =
				createStandardButtonWithIcon(MaterialDesignResources.INSTANCE.undo_black());
		undoButton.setTitleWithLocalizationKey("Undo");
		addStylesTo(undoButton, FLAT_BUTTON_STYLE, UNDO_BTN_STYLE);
		container.add(undoButton);
	}

	private void styleAndAddRedoButtonToContainer(Panel container) {
		StandardButton redoButton =
				createStandardButtonWithIcon(MaterialDesignResources.INSTANCE.redo_black());
		redoButton.setTitleWithLocalizationKey("Redo");
		redoButton.addStyleName(FLAT_BUTTON_STYLE);
		container.add(redoButton);
	}

	private void addStylesTo(UIObject uiObject, String... styleNames) {
		for (String styleName: styleNames) {
			uiObject.addStyleName(styleName);
		}
	}

	private StandardButton createStandardButtonWithIcon(ResourcePrototype icon) {
		return new StandardButton(icon, null, BUTTON_SIZE, app);
	}
}
