///* 
//GeoGebra - Dynamic Mathematics for Everyone
//http://www.geogebra.org
//
//This file is part of GeoGebra.
//
//This program is free software; you can redistribute it and/or modify it 
//under the terms of the GNU General Public License as published by 
//the Free Software Foundation.
//
// */

package org.geogebra.web.web.gui.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.gui.dialog.ToolManagerDialogModel;
import org.geogebra.common.gui.dialog.ToolManagerDialogModel.ToolManagerDialogListener;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.gui.util.LayoutUtil;
import org.geogebra.web.html5.gui.util.ListBoxApi;
import org.geogebra.web.html5.javax.swing.GOptionPaneW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.web.gui.ToolNameIconPanel;
import org.geogebra.web.web.gui.ToolNameIconPanel.MacroChangeListener;
import org.geogebra.web.web.gui.util.PopupBlockAvoider;
import org.geogebra.web.web.gui.util.SaveDialogW;
import org.geogebra.web.web.main.GeoGebraTubeExportWeb;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;


public class ToolManagerDialogW extends DialogBoxW implements
 ClickHandler,
        ToolManagerDialogListener, MacroChangeListener {

	private class MacroListBox extends ListBox {
		List<Macro> macros;
		public MacroListBox() {
			macros = new ArrayList<Macro>();
		}

		private String getMacroText(Macro macro) {
			return macro.getToolName() + ": " + macro.getNeededTypesString();
		}

		public List<Macro> getMacros() {
			return macros;
		}

		public Macro getMacro(int index) {
			return macros.get(index);
		}

		public Macro getSelectedMacro() {
			int idx = getSelectedIndex();
			if (idx == -1) {
				return null;
			}
			return getMacro(idx);
		}

		public void setSelectedMacro(Macro macro) {
			int idx = getSelectedIndex();
			if (idx == -1) {
				return;
			}
			macros.set(idx, macro);
			setItemText(idx, getMacroText(macro));

		}
		public String getMacroText(int index) {
			return getMacroText(getMacro(index));
		}

		public void addMacro(Macro macro) {
			macros.add(macro);
			addItem(getMacroText(macro));
		}

		public void insertMacro(Macro macro, int index) {
			macros.add(index, macro);
			insertItem(getMacroText(macro), index);
		}

		public void setMacro(Macro macro, int index) {
			macros.set(index, macro);
			setItemText(index, getMacroText(macro));
		}

		@Override
		public void removeItem(int index) {
			macros.remove(index);
			super.removeItem(index);

		}

		public List<Macro> getSelectedMacros() {
			List<Macro> sel = null;
			for (int i = 0; i < getItemCount(); i++) {
				if (isItemSelected(i)) {
					if (sel == null) {
						sel = new ArrayList<Macro>();
					}
					sel.add(getMacro(i));
				}

			}

			return sel;
		}

		public boolean isEmpty() {
			return macros.isEmpty();
		}
	}
	private static final long serialVersionUID = 1L;

	AppW app;
	final LocalizationW loc;
	private ToolManagerDialogModel model;

	private Button btUp;

	private Button btDown;

	MacroListBox toolList;

	private Button btDelete;

	private Button btOpen;

	private Button btSave;

	private Button btClose;

	private ToolNameIconPanel macroPanel;
	private int lastMacroIdx;

	private Button btShare;


	public ToolManagerDialogW(AppW app) {
		setModal(true);
		model = new ToolManagerDialogModel(app, this);

		this.app = app;
		this.loc = (LocalizationW) app.getLocalization();
		initGUI();
		center();
	}

	@Override
	public void setVisible(boolean flag) {
		if (flag) {
			app.setMoveMode();
		} else {
			// recreate tool bar of application window
			// updateToolBar();
		}

		super.setVisible(flag);
	}

	/**
	 * Updates the order of macros.
	 */
	private void updateToolBar() {
		model.addMacros(toolList.getMacros().toArray());
		app.updateToolBar();
	}

	private void deleteTools() {
		// final List<String> sel = ListBoxApi.getSelection(toolList);
		List<Integer> selIndexesTemp = ListBoxApi.getSelectionIndexes(toolList);

		if (selIndexesTemp.isEmpty()) {
			return;
		}
		// List<Macro> macros = toolList.getSelectedMacros();
		String macroNamesNoDel = "";
		String macroNamesDel = "";

		for (int j = 0; j < selIndexesTemp.size(); j++) {
			int i = selIndexesTemp.get(j);
			if (toolList.getMacro(i).isUsed()) {
				macroNamesNoDel += "\n"
						+ toolList.getMacro(i).getToolOrCommandName() + ": "
						+ toolList.getMacro(i).getNeededTypesString();
				toolList.setItemSelected(j, false);
			} else {
				macroNamesDel += "\n"
						+ toolList.getMacro(i).getToolOrCommandName() + ": "
						+ toolList.getMacro(i).getNeededTypesString();
			}
		}
		final List<Integer> selIndexes = ListBoxApi
				.getSelectionIndexes(toolList);
		String question = "";
		String message = "";
		if (macroNamesDel.isEmpty()) {
			app.showError(app.getLocalization().getError("Tool.DeleteUsed")
					+ " " + macroNamesNoDel);
		} else {
			question = loc.getPlain("Question");
			message = loc.getMenu("Tool.DeleteQuestion") + macroNamesDel;

			if (!macroNamesNoDel.isEmpty()) {
				message += "\n" + loc.getError("Tool.DeleteUsed")
						+ macroNamesNoDel;
			}
			String[] options = { loc.getMenu("DeleteTool"),
					loc.getMenu("DontDeleteTool") };
			GOptionPaneW.INSTANCE.showOptionDialog(app, message, question,
					GOptionPane.CUSTOM_OPTION, GOptionPane.QUESTION_MESSAGE,
					null, options, new AsyncOperation() {

						@Override
						public void callback(Object obj) {

							String[] dialogResult = (String[]) obj;
							if ("0".equals(dialogResult[0])) {

								List<Macro> macros = toolList
										.getSelectedMacros();
								// need this because of removing

								Collections.reverse(selIndexes);

								for (Integer idx : selIndexes) {
									toolList.removeItem(idx);
								}

								if (!toolList.isEmpty()) {
									toolList.setSelectedIndex(0);
								} else {
									macroPanel.setMacro(null);
								}

								updateMacroPanel();

								if (model.deleteTools(macros.toArray())) {
									applyChanges();
									updateToolBar();
								}
							}

						}

					});
		}
	}

	private FlowPanel createListUpDownRemovePanel() {
		btUp = new Button("\u25b2");
		btUp.setTitle(app.getPlain("Up"));
		btUp.addClickHandler(this);
		btUp.getElement().getStyle().setMargin(3, Style.Unit.PX);

		btDown = new Button("\u25bc");
		btDown.setTitle(app.getPlain("Down"));
		btDown.addClickHandler(this);
		btDown.getElement().getStyle().setMargin(3, Style.Unit.PX);

		FlowPanel panel = new FlowPanel();
		panel.add(btUp);
		panel.add(btDown);

		return panel;
	}

	private void initGUI() {
		addStyleName("GeoGebraPopup");
		getCaption().setText(loc.getMenu("Tool.Manage"));


		FlowPanel panel = new FlowPanel();

		FlowPanel toolListPanel = new FlowPanel();
		Label lblTitle = new Label(loc.getMenu("Tools"));
		lblTitle.setStyleName("panelTitle");
		panel.add(lblTitle);
		panel.add(toolListPanel);
		setWidget(panel);

		toolList = new MacroListBox();
		toolList.setMultipleSelect(true);

		toolList.setVisibleItemCount(6);

		FlowPanel centerPanel = LayoutUtil.panelRow(toolList, createListUpDownRemovePanel());
		centerPanel.setStyleName("manageToolsList");
		toolListPanel.add(centerPanel);

		FlowPanel toolButtonPanel = new FlowPanel();
		toolListPanel.add(toolButtonPanel);

		btDelete = new Button();
		toolButtonPanel.add(btDelete);
		btDelete.setText(loc.getPlain("Delete"));

		if (app.has(Feature.TOOL_EDITOR)) {
			btOpen = new Button();
			toolButtonPanel.add(btOpen);
			btOpen.setText(loc.getPlain("Open"));
			btOpen.addClickHandler(this);
		}

		btSave = new Button();
		toolButtonPanel.add(btSave);
		btSave.setText(loc.getMenu("SaveAs") + " ...");

		btShare = new Button();
		toolButtonPanel.add(btShare);
		btShare.setText(loc.getMenu("Share") + " ...");

		// name & icon
		macroPanel = new ToolNameIconPanel(app);
		macroPanel.setTitle(app.getMenu("NameIcon"));
		macroPanel.setMacroChangeListener(this);
		panel.add(macroPanel);


		FlowPanel closePanel = new FlowPanel();
		btClose = new Button(loc.getMenu("Close"));
		closePanel.add(btClose);
		panel.add(closePanel);
		btShare.addClickHandler(this);
		btSave.addClickHandler(this);
		btDelete.addClickHandler(this);
		btClose.addClickHandler(this);

		insertTools();

		toolList.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				updateMacroPanel();
			}

		});


	}

	private void updateMacroPanel() {
		macroPanel.setMacro(toolList.getSelectedMacro());
	}

	private void openTools() {
		App.debug("before" + app.hashCode());
		app.setWaitCursor();
		// for (Macro macro : toolList.getSelectedMacros()) {
		app.storeMacro(toolList.getSelectedMacro(), false);
		Window.open(Window.Location.getHref(), "", "");

		app.setDefaultCursor();
		hide();
	}

	private void insertTools() {
		toolList.clear();
		Kernel kernel = app.getKernel();
		int size = kernel.getMacroNumber();

		for (int i = 0; i < size; i++) {
			Macro macro = kernel.getMacro(i);
			toolList.addMacro(macro);
		}
		toolList.setSelectedIndex(0);
		updateMacroPanel();
		lastMacroIdx = -1;
	}


	/**
	 * Saves all selected tools in a new file.
	 */
	private void saveTools() {
		applyChanges();
		SaveDialogW dlg = new SaveDialogW(app);
		dlg.setSaveType(MaterialType.ggt);
		dlg.show();

	}
	public void removeMacroFromToolbar(int i) {

		app.getGuiManager().removeFromToolbarDefinition(i);
	}

	public void refreshCustomToolsInToolBar() {
		app.getGuiManager().refreshCustomToolsInToolBar();
		app.getGuiManager().updateToolbar();
	}

	public void uploadWorksheet(ArrayList<Macro> macros) {
		GeoGebraTubeExportWeb exporter = new GeoGebraTubeExportWeb(app);

		exporter.uploadWorksheet(macros, new PopupBlockAvoider());

	}

	public void onClick(ClickEvent event) {
		Object src = event.getSource();

		if (src == btClose) {
			applyChanges();
			hide();

		}

		int idx = toolList.getSelectedIndex();
		if (idx == -1) {
			return;
		}

		List<Integer> sel = ListBoxApi.getSelectionIndexes(toolList);
		int selSize = sel.size(); 

		if (src == btUp) {
			App.debug("Up");
			if (idx > 0) {
				toolList.insertMacro(toolList.getMacro(idx - 1), idx + selSize);
				toolList.removeItem(idx - 1);
			}
		} else if (src == btDown) {
			App.debug("Dowm");
			if (idx + selSize < toolList.getItemCount()) {
				toolList.insertMacro(toolList.getMacro(idx + selSize), idx);
				toolList.removeItem(idx + selSize + 1);
			}
		} else if (src == btDelete) {
			deleteTools();
		} else if (src == btOpen) {
			openTools();
		} else if (src == btSave) {
			saveTools();
		} else if (src == btShare) {
			model.uploadToGeoGebraTube(toolList.getSelectedMacros().toArray());
		}
	}

	private void applyChanges() {
		if (toolList.isEmpty()) {
			return;
		}

		model.addMacros(toolList.getMacros().toArray());
		app.updateCommandDictionary();
		refreshCustomToolsInToolBar();

	}
	public void onMacroChange(Macro macro) {
		App.debug("[MACROLIST] onMacroChange " + macro.getCommandName());
		Macro m = toolList.getSelectedMacro();
		m.setCommandName(macro.getCommandName());
		m.setToolName(macro.getToolName());
		m.setToolHelp(macro.getToolHelp());
		m.setIconFileName(macro.getIconFileName());
		m.setShowInToolBar(macro.isShowInToolBar());
		toolList.setSelectedMacro(m);

	}
	
	@Override
	public void onShowToolChange(Macro macro) {
		onMacroChange(macro);
		applyChanges();
	}

}
