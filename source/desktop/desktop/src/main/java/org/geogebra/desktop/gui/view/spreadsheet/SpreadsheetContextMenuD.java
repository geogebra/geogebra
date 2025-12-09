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
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetContextMenu;
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
	public SpreadsheetContextMenuD(MyTable table) {
		super(table);
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

	@Override
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
	public void addMenuItem(final String cmdString, String text,
			boolean enabled) {
		JMenuItem item = new JMenuItem(text);
		item.setIcon(getIcon(cmdString));
		item.addActionListener(e -> doCommand(cmdString));
		item.setEnabled(enabled);
		addItem(item);
	}

	@Override
	public void addCheckBoxMenuItem(final String cmdString, String text,
			boolean isSelected) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(text);
		item.setIcon(getIcon(cmdString));
		item.addActionListener(e -> doCommand(cmdString));
		item.setSelected(isSelected);
		addItem(item);
	}

	@Override
	public JMenu addSubMenu(String text, String cmdString) {
		JMenu menu = new JMenu(text);
		menu.setIcon(getIcon(cmdString));
		addItem(menu);
		return menu;
	}

	@Override
	public void addSubMenuItem(JMenu menu, final String cmdString, String text,
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

	private ImageIcon getIcon(String cmdString) {

		if (cmdString == null) {
			return ((AppD) app).getEmptyIcon();
		}

		ImageResourceD iconString = null;

		switch (MenuCommand.valueOf(cmdString)) {
		default:
			// do nothing
			break;
		case Copy:
			iconString = GuiResourcesD.MENU_EDIT_COPY;
			break;
		case Cut:
			iconString = GuiResourcesD.MENU_EDIT_CUT;
			break;
		case Paste:
			iconString = GuiResourcesD.MENU_EDIT_PASTE;
			break;
		case Delete:
		case DeleteObjects:
			iconString = GuiResourcesD.DELETE_SMALL;
			break;
		case ShowObject:
			iconString = GuiResourcesD.MODE_SHOWHIDEOBJECT_GIF;
			break;
		case ShowLabel:
			iconString = GuiResourcesD.MODE_SHOWHIDELABEL;
			break;
		case RecordToSpreadsheet:
			iconString = GuiResourcesD.SPREADSHEETTRACE;
			break;
		case Properties:
			iconString = GuiResourcesD.VIEW_PROPERTIES_16;
			break;
		case SpreadsheetOptions:
			iconString = GuiResourcesD.VIEW_PROPERTIES_16;
			break;
		}

		// convert string to icon
		if (iconString != null) {
			return ((AppD) app).getScaledIcon(iconString);
		}
		return ((AppD) app).getEmptyIcon();
	}

}