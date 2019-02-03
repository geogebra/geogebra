/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.dialog.ToolManagerDialogModel;
import org.geogebra.common.gui.dialog.ToolManagerDialogModel.ToolManagerDialogListener;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.desktop.export.GeoGebraTubeExportD;
import org.geogebra.desktop.gui.MyImageD;
import org.geogebra.desktop.gui.ToolNameIconPanelD;
import org.geogebra.desktop.gui.app.GeoGebraFrame;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.UtilD;

/**
 * Dialog to manage existing user defined tools (macros).
 * 
 * @author Markus Hohenwarter
 */
public class ToolManagerDialogD extends javax.swing.JDialog
		implements ToolManagerDialogListener {

	private static final long serialVersionUID = 1L;

	AppD app;
	final LocalizationD loc;
	private DefaultListModel<Macro> toolsModel;
	private ToolManagerDialogModel model;

	public ToolManagerDialogD(AppD app) {
		super(app.getFrame());
		setModal(true);

		model = new ToolManagerDialogModel(app, this);

		this.app = app;
		this.loc = app.getLocalization();
		initGUI();
	}

	@Override
	public void setVisible(boolean flag) {
		if (flag) {
			app.setMoveMode();
		} else {
			// recreate tool bar of application window
			updateToolBar(toolsModel);
		}

		super.setVisible(flag);
	}

	/**
	 * Updates the order of macros using the listModel.
	 */
	private void updateToolBar(DefaultListModel listModel) {
		model.addMacros(listModel.toArray());
		app.updateToolBar();
	}

	/**
	 * Deletes all selected tools that are not used in the construction.
	 */
	private void deleteTools(JList<Macro> toolList,
			DefaultListModel<Macro> listModel) {
		List<Macro> sel = toolList.getSelectedValuesList();
		if (sel == null || sel.size() == 0) {
			return;
		}

		// ARE YOU SURE ?
		Object[] options = { loc.getMenu("DeleteTool"),
				loc.getMenu("DontDeleteTool") };
		int returnVal = JOptionPane.showOptionDialog(this,
				loc.getMenu("Tool.DeleteQuestion"), loc.getMenu("Question"),
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
				options, options[1]);
		if (returnVal == 1) {
			return;
		}

		if (model.deleteTools(sel)) {
			updateToolBar(listModel);
		}

		for (Macro macro : model.getDeletedMacros()) {
			listModel.removeElement(macro);
		}
	}

	private void initGUI() {
		try {
			setTitle(loc.getMenu("Tool.Manage"));

			JPanel panel = new JPanel(new BorderLayout(5, 5));
			setContentPane(panel);
			panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			JPanel toolListPanel = new JPanel(new BorderLayout(5, 5));
			toolListPanel.setBorder(
					BorderFactory.createTitledBorder(loc.getMenu("Tools")));
			getContentPane().add(toolListPanel, BorderLayout.NORTH);

			toolsModel = new DefaultListModel<>();
			insertTools(toolsModel);
			final JList<Macro> toolList = new JList<>(toolsModel);
			toolList.setCellRenderer(new MacroCellRenderer());
			toolList.setVisibleRowCount(6);

			JPanel centerPanel = ToolCreationDialogD
					.createListUpDownRemovePanel(loc, toolList, null, false,
							true, false, null);

			// JScrollPane jScrollPane1 = new JScrollPane(toolList);
			// jScrollPane1.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED
			// ));
			// toolListPanel.add(jScrollPane1, BorderLayout.CENTER);
			toolListPanel.add(centerPanel, BorderLayout.CENTER);

			JPanel toolButtonPanel = new JPanel(
					new FlowLayout(FlowLayout.LEFT));
			toolListPanel.add(toolButtonPanel, BorderLayout.SOUTH);

			final JButton btDelete = new JButton();
			toolButtonPanel.add(btDelete);
			btDelete.setText(loc.getMenu("Delete"));

			final JButton btOpen = new JButton();
			toolButtonPanel.add(btOpen);
			btOpen.setText(loc.getMenu("Open"));

			final JButton btSave = new JButton();
			toolButtonPanel.add(btSave);
			btSave.setText(loc.getMenu("SaveAs") + " ...");

			final JButton btShare = new JButton();
			toolButtonPanel.add(btShare);
			btShare.setText(loc.getMenu("Share") + " ...");

			// name & icon
			final ToolNameIconPanelD namePanel = new ToolNameIconPanelD(app,
					true);
			namePanel.setBorder(
					BorderFactory.createTitledBorder(loc.getMenu("NameIcon")));
			panel.add(namePanel, BorderLayout.CENTER);

			JPanel closePanel = new JPanel();
			FlowLayout closePanelLayout = new FlowLayout();
			closePanelLayout.setAlignment(FlowLayout.RIGHT);
			closePanel.setLayout(closePanelLayout);
			final JButton btClose = new JButton(loc.getMenu("Close"));
			closePanel.add(btClose);
			panel.add(closePanel, BorderLayout.SOUTH);

			// action listener for buttone
			ActionListener ac = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Object src = e.getSource();
					if (src == btClose) {
						// ensure to set macro properties from namePanel
						namePanel.init(null, null);

						// make sure new macro command gets into dictionary
						app.updateCommandDictionary();

						// destroy dialog
						setVisible(false);
						dispose();

					} else if (src == btDelete) {
						deleteTools(toolList, toolsModel);
					} else if (src == btOpen) {
						openTools(toolList);
					} else if (src == btSave) {
						saveTools(toolList);
					} else if (src == btShare) {
						if (UtilD.isJava7()) {
							app.showReinstallMessage();
						} else {
							uploadToGeoGebraTube(toolList);
						}
					}
				}

			};

			btShare.addActionListener(ac);
			btSave.addActionListener(ac);
			btDelete.addActionListener(ac);
			btOpen.addActionListener(ac);
			btClose.addActionListener(ac);

			// add selection listener for list
			final ListSelectionModel selModel = toolList.getSelectionModel();
			ListSelectionListener selListener = new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (selModel.getValueIsAdjusting()) {
						return;
					}

					int[] selIndices = toolList.getSelectedIndices();
					if (selIndices == null || selIndices.length != 1) {
						// no or several tools selected
						namePanel.init(null, null);
					} else {
						Macro macro = (Macro) toolsModel
								.getElementAt(selIndices[0]);
						namePanel.init(ToolManagerDialogD.this, macro);
					}
				}
			};
			selModel.addListSelectionListener(selListener);

			// select first tool in list
			if (toolsModel.size() > 0) {
				toolList.setSelectedIndex(0);
			} else {
				namePanel.init(null, null);
			}

			setResizable(true);
			namePanel.setPreferredSize(new Dimension(400, 200));

			app.setComponentOrientation(this);

			pack();
			setLocationRelativeTo(app.getFrame()); // center
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Opens tools in different windows
	 * 
	 * @author Zbynek Konecny
	 * @param toolList
	 *            Tools to be opened
	 */
	private void openTools(JList<Macro> toolList) {
		Object[] sel = toolList.getSelectedValuesList().toArray();
		if (sel == null || sel.length == 0) {
			return;
		}

		for (int i = 0; i < sel.length; i++) {
			final Macro macro = (Macro) sel[i];
			Thread runner = new Thread() {
				@Override
				public void run() {
					app.setWaitCursor();
					// avoid deadlock with current app
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							GeoGebraFrame newframe = ((GeoGebraFrame) app
									.getFrame()).createNewWindow(null, macro);
							newframe.setTitle(macro.getCommandName());
							byte[] byteArray = app.getMacroFileAsByteArray();
							newframe.getApplication()
									.loadMacroFileFromByteArray(byteArray,
											false);
							newframe.getApplication().openMacro(macro);
							app.setDefaultCursor();

						}
					});

				}
			};
			runner.start();

			this.setVisible(false);
			this.dispose();
		}
	}

	private void insertTools(DefaultListModel<Macro> listModel) {
		Kernel kernel = app.getKernel();
		int size = kernel.getMacroNumber();
		for (int i = 0; i < size; i++) {
			Macro macro = kernel.getMacro(i);
			listModel.addElement(macro);
		}
	}

	private class MacroCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		/*
		 * This is the only method defined by ListCellRenderer. We just
		 * reconfigure the Jlabel each time we're called.
		 */
		@Override
		public Component getListCellRendererComponent(JList list, Object value, // value
																				// to
																				// display
				int index, // cell index
				boolean iss, // is the cell selected
				boolean chf) // the list and the cell have the focus
		{
			/*
			 * The DefaultListCellRenderer class will take care of the JLabels
			 * text property, it's foreground and background colors, and so on.
			 */
			super.getListCellRendererComponent(list, value, index, iss, chf);

			if (value != null) {
				Macro macro = (Macro) value;
				StringBuilder sb = new StringBuilder();
				sb.append("<html><b>");
				sb.append(macro.getToolName());
				sb.append("</b>: ");
				sb.append(macro.getNeededTypesString());
				sb.append("</html>");
				setText(sb.toString());

				MyImageD img = app.getExternalImage(macro.getIconFileName());
				if (img != null) {
					setIcon(new ImageIcon(img.getImage()));
					Dimension dim = getPreferredSize();
					dim.height = img.getHeight();
					setPreferredSize(dim);
					setMinimumSize(dim);
				}
			}
			return this;
		}
	}

	/*
	 * upload selected Tools to GeoGebraTube
	 */
	private void uploadToGeoGebraTube(final JList<Macro> toolList) {

		Thread runner = new Thread() {
			@Override
			public void run() {
				model.uploadToGeoGebraTube(
						toolList.getSelectedValuesList().toArray());
			}
		};
		runner.start();
	}

	/**
	 * Saves all selected tools in a new file.
	 */
	private void saveTools(JList<Macro> toolList) {
		Macro[] sel = toolList.getSelectedValuesList().toArray(new Macro[0]);
		if (sel == null || sel.length == 0) {
			return;
		}

		File file = app.getGuiManager().showSaveDialog(
				FileExtensions.GEOGEBRA_TOOL, null,
				GeoGebraConstants.APPLICATION_NAME + " " + loc.getMenu("Tools"),
				true, false);
		if (file == null) {
			return;
		}

		// save selected macros
		app.saveMacroFile(file, model.getAllTools(sel));
	}

	@Override
	public void removeMacroFromToolbar(int i) {
		app.getGuiManager().removeFromToolbarDefinition(i);
	}

	@Override
	public void refreshCustomToolsInToolBar() {
		app.getGuiManager().refreshCustomToolsInToolBar();

	}

	@Override
	public void uploadWorksheet(ArrayList<Macro> macros) {
		// create new exporter
		GeoGebraTubeExportD exporter = new GeoGebraTubeExportD(app);

		exporter.uploadWorksheet(macros);

	}

}
