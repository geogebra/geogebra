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

package geogebra.web.gui.dialog;

import geogebra.common.gui.dialog.ToolManagerDialogModel;
import geogebra.common.gui.dialog.ToolManagerDialogModel.ToolManagerDialogListener;
import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.main.App;
import geogebra.common.util.AsyncOperation;
import geogebra.html5.gui.util.LayoutUtil;
import geogebra.html5.gui.util.ListBoxApi;
import geogebra.html5.javax.swing.GOptionPaneW;
import geogebra.html5.main.AppW;
import geogebra.html5.main.LocalizationW;
import geogebra.web.gui.ToolNameIconPanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;


public class ToolManagerDialogW extends DialogBoxW implements
		ClickHandler, ToolManagerDialogListener {

	private class MacroListBox extends ListBox {
		List<Macro> macros;
		public MacroListBox() {
			macros = new ArrayList<Macro>();
		}
	
		private String getMacroText(Macro macro) {
			return macro.getToolName() + ": " + macro.getNeededTypesString();
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
        
        @Override
        public void removeItem(int index) {
        	macros.remove(index);
        	super.removeItem(index);
        	
        }
        
        public List<Macro> getSelectedMacros() {
        	List<Macro> sel = null;
        	for (int i=0;i < getItemCount(); i++) {
        		if (isItemSelected(i)) {
        			if (sel == null)  {
        				sel = new ArrayList<Macro>();
        			}
        			sel.add(getMacro(i));
        		}
        		
        	}
        	
        	return sel;
        }
	}
	private static final long serialVersionUID = 1L;

	AppW app;
	final LocalizationW loc;
	private ToolManagerDialogModel model;

	private Button btUp;

	private Button btDown;

	private MacroListBox toolList;

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
			updateToolBar();
		}

		super.setVisible(flag);
	}

	/**
	 * Updates the order of macros using the listModel.
	 */
	private void updateToolBar() {
		//model.addMacros(listModel.toArray());
		app.updateToolBar();
	}

	/**
	 * Deletes all selected tools that are not used in the construction.
	 */
	private void deleteTools() {
		final List<String> sel = ListBoxApi.getSelection(toolList);
		final List<Integer> selIndexes = ListBoxApi.getSelectionIndexes(toolList);
		
		if (sel.isEmpty()) {
			return;
		}

		String[] options = { loc.getMenu("DeleteTool"),
				loc.getMenu("DontDeleteTool") };

		GOptionPaneW.INSTANCE.showOptionDialog(app, loc.getMenu("Tool.DeleteQuestion"),
				loc.getPlain("Question"), GOptionPane.CANCEL_OPTION,
		        GOptionPane.QUESTION_MESSAGE, null, options, new AsyncOperation() {
					
					@Override
					public void callback(Object obj) {
		
						String[] dialogResult = (String[])obj;
				        if ("0".equals(dialogResult[0])) {
				        	
				        	List<Macro> macros = toolList.getSelectedMacros();
				        	// need this because of removing 
				    	    
				        	Collections.reverse(selIndexes);
				
				    	    for (Integer idx : selIndexes) {
				    			toolList.removeItem(idx);
				    		}

				    		if (model.deleteTools(macros.toArray())) {
				    			updateToolBar();
				    		}
				        }
				       
					}
					
		});
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
		insertTools();
		toolList.setVisibleItemCount(6);

		FlowPanel centerPanel = LayoutUtil.panelRow(toolList, createListUpDownRemovePanel());
		centerPanel.setStyleName("manageToolsList");
		toolListPanel.add(centerPanel);

		FlowPanel toolButtonPanel = new FlowPanel();
		toolListPanel.add(toolButtonPanel);

		final Button btDelete = new Button();
		toolButtonPanel.add(btDelete);
		btDelete.setText(loc.getPlain("Delete"));

		final Button btOpen = new Button();
		toolButtonPanel.add(btOpen);
		btOpen.setText(loc.getPlain("Open"));

		final Button btSave = new Button();
		toolButtonPanel.add(btSave);
		btSave.setText(loc.getMenu("SaveAs") + " ...");

		final Button btShare = new Button();
		toolButtonPanel.add(btShare);
		btShare.setText(loc.getMenu("Share") + " ...");

		// name & icon
		final ToolNameIconPanel namePanel = new ToolNameIconPanel(app);
		namePanel.setTitle(app.getMenu("NameIcon"));
		panel.add(namePanel);

		FlowPanel closePanel = new FlowPanel();
		final Button btClose = new Button(loc.getMenu("Close"));
		closePanel.add(btClose);
		panel.add(closePanel);

		ClickHandler btnClickHandler = new ClickHandler() {
			public void actionPerformed(Object src) {
				if (src == btClose) {
					// ensure to set macro properties from namePanel
				//	namePanel.init(null, null);

					// make sure new macro command gets into dictionary
					app.updateCommandDictionary();

					// destroy dialog
					hide();
					
				} else if (src == btDelete) {
					deleteTools();
				} else if (src == btOpen) {
					openTools();
				} else if (src == btSave) {
					saveTools();
				} else if (src == btShare) {
					uploadToGeoGebraTube();
				}
			}
			
			public void onClick(ClickEvent event) {
				actionPerformed(event.getSource());
	}
		};

		btShare.addClickHandler(btnClickHandler);
		btSave.addClickHandler(btnClickHandler);
		btDelete.addClickHandler(btnClickHandler);
		btOpen.addClickHandler(btnClickHandler);
		btClose.addClickHandler(btnClickHandler);

		toolList.addChangeHandler(new ChangeHandler() {
			
			public void onChange(ChangeEvent event) {
				Macro macro = toolList.getSelectedMacro();
				namePanel.setMacro(macro);
			}
			
		});
		// add selection listener for list
//		final ListSelectionModel selModel = toolList.getSelectionModel();
//		ListSelectionListener selListener = new ListSelectionListener() {
//			public void valueChanged(ListSelectionEvent e) {
//				if (selModel.getValueIsAdjusting())
//					return;
//
//				int[] selIndices = toolList.getSelectedIndices();
//				if (selIndices == null || selIndices.length != 1) {
//					// no or several tools selected
//					namePanel.init(null, null);
//				} else {
//					Macro macro = (Macro) toolsModel
//							.getElementAt(selIndices[0]);
//					namePanel.init(ToolManagerDialogW.this, macro);
//				}
//			}
//		};
//		selModel.addListSelectionListener(selListener);

//		// select first tool in list
//		if (toolsModel.size() > 0)
//			toolList.setSelectedIndex(0);
//		else
//			namePanel.init(null, null);
//
//		setResizable(true);
//		namePanel.setPreferredSize(new Dimension(400, 200));
//
//		app.setComponentOrientation(this);
//
//		pack();
//		setLocationRelativeTo(app.getFrame()); // center
}

	/**
	 * Opens tools in different windows
	 * 
	 * @author Zbynek Konecny
	 * @param toolList
	 *            Tools to be opened
	 */
	private void openTools() {
//		Object[] sel = toolList.getSelectedValues();
//		if (sel == null || sel.length == 0)
//			return;
//
//		for (int i = 0; i < sel.length; i++) {
//			final Macro macro = (Macro) sel[i];
//			Thread runner = new Thread() {
//				@Override
//				public void run() {
//					App.debug("before" + app.hashCode());
//					app.setWaitCursor();
//					GeoGebraFrame newframe = GeoGebraFrame.createNewWindow(
//							null, macro);
//					newframe.setTitle(macro.getCommandName());
//					byte[] byteArray = app.getMacroFileAsByteArray();
//					newframe.getApplication().loadMacroFileFromByteArray(
//							byteArray, false);
//					newframe.getApplication().openMacro(macro);
//					app.setDefaultCursor();
//
//				}
//			};
//			runner.start();
//
//			this.setVisible(false);
//		}
	}

	private void insertTools() {
		toolList.clear();
		Kernel kernel = app.getKernel();
		int size = kernel.getMacroNumber();

		for (int i = 0; i < size; i++) {
			Macro macro = kernel.getMacro(i);
			toolList.addMacro(macro);
		}
	}

	/*
	 * upload selected Tools to GeoGebraTube
	 */
	private void uploadToGeoGebraTube() {

		List<Macro> macros = new ArrayList<Macro>();
       	List<Integer> selIndexes = ListBoxApi.getSelectionIndexes(toolList);
		for (Integer i: selIndexes) {
			macros.add(app.getKernel().getMacro(i));
		}
				
       	model.uploadToGeoGebraTube(macros.toArray());
	}

	/**
	 * Saves all selected tools in a new file.
	 */
	private void saveTools() {
//		Object[] sel = toolList.getSelectedValues();
//		if (sel == null || sel.length == 0)
//			return;
//
//		File file = app.getGuiManager()
//				.showSaveDialog(
//						AppD.FILE_EXT_GEOGEBRA_TOOL,
//						null,
//						GeoGebraConstants.APPLICATION_NAME + " "
//								+ loc.getMenu("Tools"), true, false);
//		if (file == null)
//			return;
//
//		// save selected macros
//		app.saveMacroFile(file, model.getAllTools(sel));
	}

	public void removeMacroFromToolbar(int i) {
		app.getGuiManager().removeFromToolbarDefinition(i);
	}

	public void refreshCustomToolsInToolBar() {
		//app.getGuiManager().refreshCustomToolsInToolBar();

	}

	public void uploadWorksheet(ArrayList<Macro> macros) {
		// create new exporter
//		geogebra.export.GeoGebraTubeExportDesktop exporter = new geogebra.export.GeoGebraTubeExportDesktop(
//				app);
//
//		exporter.uploadWorksheet(macros);

	}

	public void onClick(ClickEvent event) {
	    Object src = event.getSource();
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
	    } else  if (src == btDown) {
	    	App.debug("Dowm");
	    	if (idx + selSize < toolList.getItemCount()) {
	    		toolList.insertMacro(toolList.getMacro(idx + selSize), idx);
	    		toolList.removeItem(idx + selSize + 1);
	    	}
	    }
	 }

}
