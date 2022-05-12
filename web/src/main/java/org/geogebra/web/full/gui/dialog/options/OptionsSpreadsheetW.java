package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Spreadsheet settings
 */
public class OptionsSpreadsheetW implements OptionPanelW, SetLabels {
	private AppW app;
	private FlowPanel optionsPanel;
	private AlgebraStyleListBox description;
	private Label descriptionLabel;

	private ComponentCheckbox  cbShowFormulaBar;
	private ComponentCheckbox  cbShowGrid;
	private ComponentCheckbox cbShowRowHeader;
	private ComponentCheckbox  cbShowColumnHeader;
	private ComponentCheckbox  cbShowHScrollbar;
	private ComponentCheckbox  cbShowVScrollbar;
	private ComponentCheckbox  cbAllowSpecialEditor;
	private ComponentCheckbox  cbAllowToolTips;
	private ComponentCheckbox  cbPrependCommands;
	private ComponentCheckbox  cbEnableAutoComplete;
	private ComponentCheckbox  cbShowNavigation;

	/**
	 * @param app - app
	 */
	public OptionsSpreadsheetW(AppW app) {
		this.app = app;
		createGUI();
	}

	@Override
	public Widget getWrappedPanel() {
		return optionsPanel;
	}

	@Override
	public void onResize(int height, int width) {
		// nothing to do here
	}

	private void createGUI() {
		cbShowFormulaBar = newCheckBox("ShowInputField", ()
				-> settings().setShowFormulaBar(cbShowFormulaBar.isSelected()));
		cbShowGrid = newCheckBox("ShowGridlines", ()
				-> settings().setShowGrid(cbShowGrid.isSelected()));
		cbShowRowHeader = newCheckBox("ShowRowHeader", ()
				-> settings().setShowRowHeader(cbShowRowHeader.isSelected()));
		cbShowColumnHeader = newCheckBox("ShowColumnHeader", ()
				-> settings().setShowColumnHeader(cbShowColumnHeader.isSelected()));
		cbShowHScrollbar = newCheckBox("ShowHorizontalScrollbars", ()
				-> settings().setShowHScrollBar(cbShowHScrollbar.isSelected()));
		cbShowVScrollbar = newCheckBox("ShowVerticalScrollbars", ()
				-> settings().setShowVScrollBar(cbShowVScrollbar.isSelected()));
		cbAllowSpecialEditor = newCheckBox("UseButtonsAndCheckboxes", ()
				-> settings().setAllowSpecialEditor(cbAllowSpecialEditor.isSelected()));
		cbAllowToolTips = newCheckBox("AllowTooltips", ()
				-> settings().setAllowToolTips(cbAllowToolTips.isSelected()));
		cbPrependCommands = newCheckBox("RequireEquals", ()
				-> settings().setEqualsRequired(cbPrependCommands.isSelected()));
		cbEnableAutoComplete = newCheckBox("UseAutoComplete", ()
				-> settings().setEnableAutoComplete(cbEnableAutoComplete.isSelected()));
		cbShowNavigation = newCheckBox("NavigationBar", ()
				-> app.toggleShowConstructionProtocolNavigation(App.VIEW_SPREADSHEET));

		optionsPanel = new FlowPanel();
		optionsPanel.addStyleName("propertiesPanel");
		optionsPanel.addStyleName("simplePropertiesPanel");

		optionsPanel.add(cbShowGrid);
		optionsPanel.add(cbShowColumnHeader);
		optionsPanel.add(cbShowRowHeader);
		optionsPanel.add(cbShowVScrollbar);
		optionsPanel.add(cbShowHScrollbar);

		description = new AlgebraStyleListBox(app, true);
		descriptionLabel = new Label();
		optionsPanel.add(LayoutUtilW.panelRow(descriptionLabel, description));
		optionsPanel.add(cbAllowSpecialEditor);
		optionsPanel.add(cbAllowToolTips);
		optionsPanel.add(cbPrependCommands);
		optionsPanel.add(cbEnableAutoComplete);
		optionsPanel.add(cbShowNavigation);

		setLabels();
		updateGUI();
	}

	private ComponentCheckbox newCheckBox(String label, Runnable handler) {
		ComponentCheckbox cb = new ComponentCheckbox(app.getLocalization(),
				false, label, () -> {
			handler.run();
			description.update();
		});
		return cb;
	}

	@Override
	public void updateGUI() {
		updateCheckBox(cbShowFormulaBar, settings().showFormulaBar());
		updateCheckBox(cbShowGrid, settings().showGrid());
		updateCheckBox(cbShowRowHeader, settings().showRowHeader());
		updateCheckBox(cbShowColumnHeader, settings().showColumnHeader());
		updateCheckBox(cbShowHScrollbar, settings().showHScrollBar());
		updateCheckBox(cbShowVScrollbar, settings().showVScrollBar());
		updateCheckBox(cbAllowSpecialEditor, settings().allowSpecialEditor());
		updateCheckBox(cbAllowToolTips, settings().allowToolTips());
		updateCheckBox(cbPrependCommands, settings().equalsRequired());
		updateCheckBox(cbEnableAutoComplete, settings().isEnableAutoComplete());
		updateCheckBox(cbShowNavigation,
				app.showConsProtNavigation(App.VIEW_SPREADSHEET));
		description.update();
	}

	private static void updateCheckBox(ComponentCheckbox cb, boolean value) {
		cb.setSelected(value);
	}

	/**
	 * Update spreadsheet panel labels. Should be applied if the language was
	 * changed. Will be called after initialization automatically.
	 */
	@Override
	public void setLabels() {
		cbShowFormulaBar.setLabels();
		cbShowGrid.setLabels();
		cbShowColumnHeader.setLabels();
		cbShowRowHeader.setLabels();
		cbShowHScrollbar.setLabels();
		cbShowVScrollbar.setLabels();
		cbAllowSpecialEditor.setLabels();
		cbAllowToolTips.setLabels();
		cbPrependCommands.setLabels();
		cbEnableAutoComplete.setLabels();
		cbShowNavigation.setLabels();
		description.update();
		descriptionLabel.setText(app.getLocalization().getMenu("AlgebraDescriptions"));
	}

	private SpreadsheetSettings settings() {
		return app.getSettings().getSpreadsheet();
	}

	@Override
	public MultiRowsTabPanel getTabPanel() {
		return null;
	}

}
