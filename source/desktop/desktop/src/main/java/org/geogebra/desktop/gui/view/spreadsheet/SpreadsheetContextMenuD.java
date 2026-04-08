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

package org.geogebra.desktop.gui.view.spreadsheet;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetContextMenu;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetToolProcessor;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.ImageResourceD;

/**
 * Subclass of SpreadsheetContextMenu, implements the spreadsheet context menu
 * for desktop.
 * 
 * @author G. Sturr
 * 
 */
public class SpreadsheetContextMenuD extends SpreadsheetContextMenu<JMenu> {

	/** Desktop popUp panel */
	protected JPopupMenu popup;

	private static final Color bgColor = Color.white;
	private static final Color fgColor = Color.black;

	/**
	 * Constructor
	 * 
	 * @param table
	 *            spreadsheet table
	 */
	public SpreadsheetContextMenuD(MyTable table, SpreadsheetToolProcessor processor) {
		super(table, processor);
		createGUI();
	}

	/**
	 * @return true if data file can be read locally
	 */
	@Override
	public boolean enableDataImport() {
		return isEmptySelection();
	}

	/**
	 * Import data file
	 */
	@Override
	public void cmdImportDataFile() {

		File dataFile = ((AppD) app).getGuiManager().getDataFile();
		if (dataFile != null) {
			((SpreadsheetViewD) table.getView())
					.loadSpreadsheetFromURL(dataFile);
		}
	}

	// ======================================
	// GUI implementation
	// ======================================
	public JPopupMenu getMenuContainer() {
		return popup;
	}

	@Override
	public void createGUI() {
		popup = new JPopupMenu();
		popup.setBackground(bgColor);
		((AppD) app).setComponentOrientation(popup);
		initMenu();

	}

	// setTitle (copied from gui.ContextMenuGeoElement)
	@Override
	public void setTitle(String str) {
		JLabel title = new JLabel(str);
		title.setFont(((AppD) app).getBoldFont());
		title.setBackground(bgColor);
		title.setForeground(fgColor);

		title.setIcon(((AppD) app).getEmptyIcon());
		title.setBorder(BorderFactory.createEmptyBorder(5, 15, 2, 5));
		popup.add(title);

		title.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				popup.setVisible(false);
			}
		});

	}

	@Override
	public void addMenuItem(final MenuCommand command, String text,
			boolean enabled) {
		JMenuItem item = new JMenuItem(text);
		item.setIcon(getIcon(command));
		item.addActionListener(e -> doCommand(command));
		item.setEnabled(enabled);
		addItem(item);
	}

	@Override
	public void addCheckBoxMenuItem(final MenuCommand command, String text,
			boolean isSelected) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(text);
		item.setIcon(getIcon(command));
		item.addActionListener(e -> doCommand(command));
		item.setSelected(isSelected);
		addItem(item);
	}

	@Override
	public JMenu addSubMenu(String text, MenuCommand command) {
		JMenu menu = new JMenu(text);
		menu.setIcon(getIcon(command));
		addItem(menu);
		return menu;
	}

	@Override
	public void addSubMenuItem(JMenu menu, final MenuCommand cmdString, String text,
			boolean enabled) {
		JMenuItem item = new JMenuItem(text, getIcon(cmdString));
		item.addActionListener(e -> doCommand(cmdString));
		item.setEnabled(enabled);
		addSubItem(menu, item);
	}

	@Override
	public void addSeparator() {
		popup.addSeparator();
	}

	private void addItem(Component item) {
		item.setBackground(bgColor);
		popup.add(item);
	}

	private static void addSubItem(JMenu menu, JMenuItem item) {
		item.setBackground(bgColor);
		menu.add(item);
	}

	private Icon getIcon(MenuCommand cmdString) {
		if (cmdString == null) {
			return ((AppD) app).getEmptyIcon();
		}

		ImageResourceD iconResource = switch (cmdString) {
			case Copy -> GuiResourcesD.MENU_EDIT_COPY;
			case Cut -> GuiResourcesD.MENU_EDIT_CUT;
			case Paste -> GuiResourcesD.MENU_EDIT_PASTE;
			case Delete, DeleteObjects -> GuiResourcesD.DELETE_SMALL;
			case ShowObject -> GuiResourcesD.MODE_SHOWHIDEOBJECT_GIF;
			case ShowLabel -> GuiResourcesD.MODE_SHOWHIDELABEL;
			case RecordToSpreadsheet -> GuiResourcesD.SPREADSHEETTRACE;
			case Properties, SpreadsheetOptions -> GuiResourcesD.VIEW_PROPERTIES_16;
			default -> null;
		};

		// convert string to icon
		if (iconResource != null) {
			return ((AppD) app).getScaledIcon(iconResource);
		}
		return ((AppD) app).getEmptyIcon();
	}

}
