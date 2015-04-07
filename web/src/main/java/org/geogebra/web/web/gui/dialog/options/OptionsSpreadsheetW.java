package org.geogebra.web.web.gui.dialog.options;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.View;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class OptionsSpreadsheetW implements OptionPanelW, ClickHandler,
        SetLabels {

	private AppW app;
	private FlowPanel optionsPanel;

	private CheckBox cbShowFormulaBar, cbShowGrid, cbShowRowHeader,
	        cbShowColumnHeader, cbShowHScrollbar, cbShowVScrollbar,
	        cbAllowSpecialEditor, cbAllowToolTips, cbPrependCommands,
	        cbEnableAutoComplete;

	public OptionsSpreadsheetW(AppW app, View spreadsheetView) {
		this.app = app;
		createGUI();
	}

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

		optionsPanel = new FlowPanel();
		optionsPanel.addStyleName("objectPropertiesPanel");

	//	optionsPanel.add(cbShowFormulaBar);
		optionsPanel.add(cbShowGrid);
		optionsPanel.add(cbShowColumnHeader);
		optionsPanel.add(cbShowRowHeader);
		optionsPanel.add(cbShowVScrollbar);
		optionsPanel.add(cbShowHScrollbar);

		// spacer
		// layoutOptions.add(Box.createVerticalStrut(16));

		optionsPanel.add(cbAllowSpecialEditor);
		optionsPanel.add(cbAllowToolTips);
		optionsPanel.add(cbPrependCommands);
		optionsPanel.add(cbEnableAutoComplete);

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
	}

	private static void updateCheckBox(CheckBox cb, boolean value) {
		cb.setValue(value);
	}

	/**
	 * Update spreadsheet panel labels. Should be applied if the language was
	 * changed. Will be called after initialization automatically.
	 */
	public void setLabels() {

		cbShowFormulaBar.setText(app.getMenu("ShowInputField"));
		cbShowGrid.setText(app.getMenu("ShowGridlines"));
		cbShowColumnHeader.setText(app.getMenu("ShowColumnHeader"));
		cbShowRowHeader.setText(app.getMenu("ShowRowHeader"));
		cbShowHScrollbar.setText(app.getMenu("ShowHorizontalScrollbars"));
		cbShowVScrollbar.setText(app.getMenu("ShowVerticalScrollbars"));
		cbAllowSpecialEditor.setText(app.getMenu("UseButtonsAndCheckboxes"));
		cbAllowToolTips.setText(app.getMenu("AllowTooltips"));
		cbPrependCommands.setText(app.getMenu("RequireEquals"));
		cbEnableAutoComplete.setText(app.getMenu("UseAutoComplete"));
	}

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

		updateGUI();
	}

	private SpreadsheetSettings settings() {
		return app.getSettings().getSpreadsheet();
	}

}
