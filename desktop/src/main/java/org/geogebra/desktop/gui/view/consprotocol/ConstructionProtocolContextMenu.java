/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package org.geogebra.desktop.gui.view.consprotocol;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.TableColumn;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.main.Localization;
import org.geogebra.desktop.gui.view.consprotocol.ConstructionProtocolViewD.ColumnKeeper;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * Context menu for construction protocol
 * 
 *
 */
public class ConstructionProtocolContextMenu extends JPopupMenu {

	private static final long serialVersionUID = 1L;
	/** Application */
	AppD app;
	private Construction cons;
	/** Construction protocol view associated with this menu */
	ConstructionProtocolViewD constprotView;

	/**
	 * Creates new context menu
	 * 
	 * @param app
	 *            application
	 */
	public ConstructionProtocolContextMenu(AppD app) {
		this.app = app;
		this.cons = app.getKernel().getConstruction();
		constprotView = (ConstructionProtocolViewD) app.getGuiManager()
				.getConstructionProtocolView();
		initItems();
	}

	/**
	 * Initialize the menu items.
	 */
	private void initItems() {
		Localization loc = app.getLocalization();
		// title for menu
		JLabel title = new JLabel(loc.getMenu("ConstructionProtocol"));

		JMenu colMenu = new JMenu(loc.getMenu("Columns"));
		JMenu optionsMenu = new JMenu(loc.getMenu("Options"));

		title.setFont(app.getBoldFont());
		title.setBackground(Color.white);
		title.setForeground(Color.black);

		title.setIcon(app.getEmptyIcon());
		title.setBorder(BorderFactory.createEmptyBorder(5, 15, 2, 5));
		add(title);
		addSeparator();

		title.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
			}
		});

		// menu items

		// "Columns" menu
		for (int k = 1; k < constprotView.getTableColumns().length; k++) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(
					constprotView.getData().columns[k].getTranslatedTitle());
			TableColumn column = constprotView.getTableColumns()[k];
			item.setSelected(constprotView.isColumnInModel(column));
			ColumnKeeper colKeeper = constprotView.new ColumnKeeper(column,
					constprotView.getData().columns[k]);
			item.addActionListener(colKeeper);
			colMenu.add(item);

		}
		add(colMenu);

		// "Options" menu
		JCheckBoxMenuItem cbShowOnlyBreakpoints = new JCheckBoxMenuItem(
				loc.getMenu("ShowOnlyBreakpoints"));
		cbShowOnlyBreakpoints.setSelected(cons.showOnlyBreakpoints());

		cbShowOnlyBreakpoints.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				constprotView.showOnlyBreakpointsAction();
			}
		});
		optionsMenu.add(cbShowOnlyBreakpoints);

		JCheckBoxMenuItem cbUseColors = new JCheckBoxMenuItem(
				loc.getMenu("ColorfulConstructionProtocol"));
		cbUseColors.setSelected(constprotView.getUseColors());
		cbUseColors.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				constprotView.setUseColors(!constprotView.getUseColors());
				// constprotView.getData().updateAll();
			}
		});
		optionsMenu.add(cbUseColors);
		add(optionsMenu);

		// Export and Print menu
		add(constprotView.getExportHtmlAction());
		add(constprotView.getPrintPreviewAction());

		// Help menu
		JMenuItem mi = new JMenuItem(loc.getMenu("FastHelp"),
				app.getScaledIcon(GuiResourcesD.HELP));
		ActionListener lstHelp = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				app.showHelp("ConstructionProtocolHelp");
				requestFocus();
			}
		};
		mi.addActionListener(lstHelp);
		add(mi);

		app.setComponentOrientation(this);

	}

}
