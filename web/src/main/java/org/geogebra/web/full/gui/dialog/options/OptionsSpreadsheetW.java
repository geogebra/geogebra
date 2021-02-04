package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Spreadsheet settings
 */
public class OptionsSpreadsheetW implements OptionPanelW, ClickHandler,
        SetLabels {

	private AppW app;
	private FlowPanel optionsPanel;
	private AlgebraStyleListBox description;
	private Label descriptionLabel;

	private CheckBox cbShowFormulaBar;
	private CheckBox cbShowGrid;
	private CheckBox cbShowRowHeader;
	private CheckBox cbShowColumnHeader;
	private CheckBox cbShowHScrollbar;
	private CheckBox cbShowVScrollbar;
	private CheckBox cbAllowSpecialEditor;
	private CheckBox cbAllowToolTips;
	private CheckBox cbPrependCommands;
	private CheckBox cbEnableAutoComplete;
	private CheckBox cbShowNavigation;

	/**
	 * @param app
	 *            app
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
		// TODO Auto-generated method stub

	}

	private void createGUI() {

		cbShowFormulaBar = newCheckBox();
		cbShowGrid = newCheckBox();
		cbShowRowHeader = newCheckBox();
		cbShowColumnHeader = newCheckBox();
		cbShowHScrollbar = newCheckBox();
		cbShowVScrollbar = newCheckBox();
		cbAllowSpecialEditor = newCheckBox();
		cbAllowToolTips = newCheckBox();
		cbPrependCommands = newCheckBox();
		cbEnableAutoComplete = newCheckBox();
		cbShowNavigation = newCheckBox();

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

	private CheckBox newCheckBox() {
		CheckBox cb = new CheckBox();
		cb.addClickHandler(this);
		cb.setStyleName("checkBoxPanel");
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

	private static void updateCheckBox(CheckBox cb, boolean value) {
		cb.setValue(value);
	}

	/**
	 * Update spreadsheet panel labels. Should be applied if the language was
	 * changed. Will be called after initialization automatically.
	 */
	@Override
	public void setLabels() {
		Localization loc = app.getLocalization();
		cbShowFormulaBar.setText(loc.getMenu("ShowInputField"));
		cbShowGrid.setText(loc.getMenu("ShowGridlines"));
		cbShowColumnHeader.setText(loc.getMenu("ShowColumnHeader"));
		cbShowRowHeader.setText(loc.getMenu("ShowRowHeader"));
		cbShowHScrollbar.setText(loc.getMenu("ShowHorizontalScrollbars"));
		cbShowVScrollbar.setText(loc.getMenu("ShowVerticalScrollbars"));
		cbAllowSpecialEditor.setText(loc.getMenu("UseButtonsAndCheckboxes"));
		cbAllowToolTips.setText(loc.getMenu("AllowTooltips"));
		cbPrependCommands.setText(loc.getMenu("RequireEquals"));
		cbEnableAutoComplete.setText(loc.getMenu("UseAutoComplete"));
		cbShowNavigation.setText(loc.getMenu("NavigationBar"));
		description.update();
		descriptionLabel.setText(loc.getMenu("AlgebraDescriptions"));
	}

	@Override
	public void onClick(ClickEvent event) {
		doActionPerformed(event.getSource());
	}

	private void doActionPerformed(Object source) {

		// ========================================
		// layout options

		if (source == cbShowFormulaBar) {
			settings().setShowFormulaBar(cbShowFormulaBar.getValue());
		}

		if (source == cbShowGrid) {
			settings().setShowGrid(cbShowGrid.getValue());
		}

		else if (source == cbShowRowHeader) {
			settings().setShowRowHeader(cbShowRowHeader.getValue());
		}

		else if (source == cbShowColumnHeader) {
			settings().setShowColumnHeader(cbShowColumnHeader.getValue());
		}

		else if (source == cbShowHScrollbar) {
			settings().setShowHScrollBar(cbShowHScrollbar.getValue());
		}

		else if (source == cbShowVScrollbar) {
			settings().setShowVScrollBar(cbShowVScrollbar.getValue());
		}

		else if (source == cbAllowSpecialEditor) {
			settings().setAllowSpecialEditor(cbAllowSpecialEditor.getValue());
		}

		else if (source == cbAllowToolTips) {
			settings().setAllowToolTips(cbAllowToolTips.getValue());
		}

		else if (source == cbPrependCommands) {
			settings().setEqualsRequired(cbPrependCommands.getValue());
		}

		else if (source == cbEnableAutoComplete) {
			settings().setEnableAutoComplete(cbEnableAutoComplete.getValue());
		}

		else if (source == cbShowNavigation) {
			app.toggleShowConstructionProtocolNavigation(App.VIEW_SPREADSHEET);
		}

		updateGUI();
	}

	private SpreadsheetSettings settings() {
		return app.getSettings().getSpreadsheet();
	}

	@Override
	public MultiRowsTabPanel getTabPanel() {
		// TODO Auto-generated method stub
		return null;
	}

}
