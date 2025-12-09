/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.gui.view.consprotocol;

import java.awt.Color;
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
		JMenu colMenu = new JMenu(loc.getMenu("Columns"));
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

		cbShowOnlyBreakpoints.addActionListener(e -> constprotView.showOnlyBreakpointsAction());
		JMenu optionsMenu = new JMenu(loc.getMenu("Options"));
		optionsMenu.add(cbShowOnlyBreakpoints);

		JCheckBoxMenuItem cbUseColors = new JCheckBoxMenuItem(
				loc.getMenu("ColorfulConstructionProtocol"));
		cbUseColors.setSelected(constprotView.getUseColors());
		cbUseColors.addActionListener(e -> {
			constprotView.setUseColors(!constprotView.getUseColors());
		});
		optionsMenu.add(cbUseColors);
		add(optionsMenu);

		// Export and Print menu
		add(constprotView.getExportHtmlAction());
		add(constprotView.getPrintPreviewAction());

		// Help menu
		JMenuItem mi = new JMenuItem(loc.getMenu("FastHelp"),
				app.getScaledIcon(GuiResourcesD.HELP));
		mi.addActionListener(e -> {
			app.showHelp("ConstructionProtocolHelp");
			requestFocus();
		});
		add(mi);

		app.setComponentOrientation(this);
	}

}
