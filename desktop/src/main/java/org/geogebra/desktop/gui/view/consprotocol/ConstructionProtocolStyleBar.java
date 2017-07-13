/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.desktop.gui.view.consprotocol;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JToolBar;
import javax.swing.table.TableColumn;

import org.geogebra.common.main.Localization;
import org.geogebra.desktop.gui.util.PopupMenuButtonD;
import org.geogebra.desktop.gui.view.consprotocol.ConstructionProtocolViewD.ColumnKeeper;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * Stylebar for construction protocol view
 */
public class ConstructionProtocolStyleBar extends JToolBar
		implements ActionListener {

	private static final long serialVersionUID = 1L;

	/**
	 * The construction protocol view which uses this tool bar.
	 */
	protected ConstructionProtocolViewD cpView;

	/**
	 * Instance of the application.
	 */
	protected AppD app;
	/** Columns button */
	PopupMenuButtonD btnColumns;
	/** Options button */
	PopupMenuButtonD btnOptions;
	private JButton btnExport, btnPrint, btnHelp;
	/** Item for Show only breakpoints option */
	JCheckBoxMenuItem miShowOnlyBreakpoints;
	/** Item for Colorful protocol option */
	JCheckBoxMenuItem miColorfulConstructionProtocol;

	private Localization loc;

	/**
	 * Helper bar.
	 * 
	 * @param cpView
	 *            construction protocol view
	 * @param app
	 *            application
	 */
	public ConstructionProtocolStyleBar(ConstructionProtocolViewD cpView,
			AppD app) {
		this.cpView = cpView;
		this.app = app;
		this.loc = app.getLocalization();

		setFloatable(false);

		addButtons();
	}

	/**
	 * add the buttons
	 */
	protected void addButtons() {

		// "columns" button

		btnColumns = new PopupMenuButtonD(app) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean prepareToShowPopup() {
				JCheckBoxMenuItem item;
				removeAllMenuItems();
				for (int k = 1; k < cpView.getTableColumns().length; k++) {
					item = new JCheckBoxMenuItem(
							cpView.getData().getColumns()[k]
									.getTranslatedTitle());
					TableColumn column = cpView.getTableColumns()[k];
					item.setSelected(cpView.isColumnInModel(column));
					ColumnKeeper colKeeper = cpView.new ColumnKeeper(column,
							cpView.getData().columns[k]);
					item.addActionListener(colKeeper);
					btnColumns.addPopupMenuItem(item);

				}

				return true;
			}
		};
		btnColumns.setKeepVisible(true);
		btnColumns.setStandardButton(true); // mouse clicks over total button
											// region
		btnColumns.setIcon(app.getScaledIcon(GuiResourcesD.COLUMN_HEADER));

		add(btnColumns);

		addSeparator();

		// options button
		// PopupMenuButton without selection table, add JMenuItems directly.

		btnOptions = new PopupMenuButtonD(app) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean prepareToShowPopup() {
				miShowOnlyBreakpoints.setSelected(app.getKernel()
						.getConstruction().showOnlyBreakpoints());
				miColorfulConstructionProtocol
						.setSelected(cpView.getUseColors());
				return true;
			}
		};
		btnOptions.setKeepVisible(true);
		btnOptions.setStandardButton(true); // mouse clicks over total button
											// region
		btnOptions
				.setIcon(app.getScaledIcon(GuiResourcesD.DOCUMENT_PROPERTIES));

		miShowOnlyBreakpoints = new JCheckBoxMenuItem(
				loc.getMenu("ShowOnlyBreakpoints"));
		miShowOnlyBreakpoints.setSelected(
				app.getKernel().getConstruction().showOnlyBreakpoints());
		miShowOnlyBreakpoints.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cpView.showOnlyBreakpointsAction();
			}
		});
		btnOptions.addPopupMenuItem(miShowOnlyBreakpoints);

		miColorfulConstructionProtocol = new JCheckBoxMenuItem(
				loc.getMenu("ColorfulConstructionProtocol"));
		miColorfulConstructionProtocol.setSelected(cpView.getUseColors());
		miColorfulConstructionProtocol.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cpView.setUseColors(!cpView.getUseColors());
			}
		});
		btnOptions.addPopupMenuItem(miColorfulConstructionProtocol);
		add(btnOptions);

		addSeparator();

		// export button

		btnExport = new JButton(app.getScaledIcon(GuiResourcesD.TEXT_HTML));
		btnExport.setToolTipText(loc.getPlainTooltip("ExportAsWebpage"));
		btnExport.addActionListener(this);
		add(btnExport);

		addSeparator();

		// print button
		btnPrint = new JButton(
				app.getScaledIcon(GuiResourcesD.DOCUMENT_PRINT_PREVIEW));
		btnPrint.setToolTipText(loc.getPlainTooltip("Print"));
		btnPrint.addActionListener(this);
		add(btnPrint);

		addSeparator();

		// Help button
		btnHelp = new JButton(app.getScaledIcon(GuiResourcesD.HELP));
		// btnHelp.setToolTipText(loc.getPlainTooltip("FastHelp"));
		btnHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						app.getGuiManager().openHelp("Construction_Protocol");
					}
				};
				runner.start();
			}
		});
		add(btnHelp);

		setLabels();
	}

	/**
	 * Set the tool tip texts (used for language change, and at initialization
	 * labels).
	 */
	public void setLabels() {
		btnColumns.setToolTipText(loc.getMenuTooltip("Columns"));
		btnOptions.setToolTipText(loc.getMenuTooltip("Options"));
		btnExport.setToolTipText(loc.getPlainTooltip("ExportAsWebpage"));
		btnPrint.setToolTipText(loc.getMenuTooltip("Print"));
		btnHelp.setToolTipText(loc.getMenuTooltip("FastHelp"));
		miShowOnlyBreakpoints.setText(loc.getMenu("ShowOnlyBreakpoints"));
		miColorfulConstructionProtocol
				.setText(loc.getMenu("ColorfulConstructionProtocol"));
	}

	/** reset actions and buttons */
	public void reinit() {
		removeAll();
		addButtons();
	}

	/**
	 * React to button presses.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnColumns) {
			int selIndex = btnColumns.getSelectedIndex();
			TableColumn column = cpView.getTableColumns()[selIndex];
			ColumnKeeper colKeeper = cpView.new ColumnKeeper(column,
					cpView.getData().columns[selIndex]);
			colKeeper.actionPerformed(e);

		}

		if (e.getSource() == btnOptions) {
			if (btnOptions.getSelectedIndex() == 0) {
				cpView.showOnlyBreakpointsAction();
			} else if (btnOptions.getSelectedIndex() == 1) {
				cpView.setUseColors(!cpView.getUseColors());
			}
		}

		if (e.getSource() == btnExport) {
			cpView.getExportHtmlAction().actionPerformed(e);
		}
		if (e.getSource() == btnPrint) {
			cpView.getPrintPreviewAction().actionPerformed(e);
		}
	}
}
