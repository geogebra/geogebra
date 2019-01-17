package org.geogebra.web.full.gui.layout.scientific;

import org.geogebra.common.kernel.undoredo.UndoRedoExecutor;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbarpanel.MenuToggleButton;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.view.button.header.FlatButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Builds and styles the Scientific Calculator header with all its buttons.
 */
class HeaderBuilder {

	private static final int BUTTON_SIZE = 24;

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
		header.setStyleName("smallScreenHeaderScientific");
		return header;
	}

	private void styleAndAddButtons() {
		styleAndAddMenuButton();
		styleAndAddSettingsButton();
		styleAndAddUndoRedoButtons();
	}

	private void styleAndAddMenuButton() {
		MenuToggleButton menuBtn = new MenuToggleButton(app);
		menuBtn.addStyleName("flatButtonHeader");
		menuBtn.addStyleName("menuBtnScientific");
		header.add(menuBtn);
	}

	private void styleAndAddSettingsButton() {
		StandardButton settingsButton =
				createFlatButtonWithIcon(MaterialDesignResources.INSTANCE.gear());
		settingsButton.setTitleWithLocalizationKey("Settings");
		settingsButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				getAppW().getGuiManager().showSciSettingsView();
			}
		});
		settingsButton.addStyleName("settingsBtnScientific");
		header.add(settingsButton);
	}

	private void styleAndAddUndoRedoButtons() {
		FlowPanel container = new FlowPanel();
		container.addStyleName("undoRedoCntScientific");
		addStyledUndoRedoButtonsTo(container);
		header.add(container);
	}

	private void addStyledUndoRedoButtonsTo(Panel undoRedoContainer) {
		StandardButton undoWidget = createStyledUndoButton();
		StandardButton redoWidget = createStyledRedoButton();
		UndoRedoExecutor.addUndoRedoFunctionality(undoWidget, redoWidget, app.getKernel());
		undoRedoContainer.add(undoWidget);
		undoRedoContainer.add(redoWidget);
	}

	private StandardButton createStyledUndoButton() {
		StandardButton undoButton =
				createFlatButtonWithIcon(MaterialDesignResources.INSTANCE.undo_black());
		undoButton.setTitleWithLocalizationKey("Undo");
		undoButton.addStyleName("undoBtnScientific");
		return undoButton;
	}

	private StandardButton createStyledRedoButton() {
		StandardButton redoButton =
				createFlatButtonWithIcon(MaterialDesignResources.INSTANCE.redo_black());
		redoButton.setTitleWithLocalizationKey("Redo");
		return redoButton;
	}

	private FlatButton createFlatButtonWithIcon(ResourcePrototype icon) {
		return new FlatButton(app, icon, BUTTON_SIZE);
	}
}
