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

package org.geogebra.desktop.cas.view;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.geogebra.common.cas.view.RowHeaderPopupMenu;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.desktop.main.AppD;

/**
 * Popup menu for row headers
 * 
 */
public class RowHeaderPopupMenuD extends RowHeaderPopupMenu
		implements ActionListener {

	private final AppD app;
	private final JList rowHeader;
	private final CASTableD table;
	private JMenuItem cbUseAsText;
	private final JPopupMenu rowHeaderPopupMenu;

	/**
	 * Creates new popup menu
	 * 
	 * @param rowHeader
	 *            row headers
	 * @param table
	 *            CAS table
	 * @param app application
	 */
	public RowHeaderPopupMenuD(JList rowHeader, CASTableD table, AppD app) {
		super(table.getApplication());
		this.app = app;
		rowHeaderPopupMenu = new JPopupMenu();
		this.rowHeader = rowHeader;
		this.table = table;
		initMenu();
	}

	/**
	 * Create menu items and put them into the menu
	 */
	protected void initMenu() {
		// insert above
		JMenuItem item5 = new JMenuItem(loc.getMenu("InsertAbove"));
		Icon emptyIcon = app.getEmptyIcon();
		item5.setIcon(emptyIcon);
		item5.setActionCommand("insertAbove");
		item5.addActionListener(this);
		rowHeaderPopupMenu.add(item5);

		// insert below
		JMenuItem item6 = new JMenuItem(loc.getMenu("InsertBelow"));
		item6.setIcon(emptyIcon);
		item6.setActionCommand("insertBelow");
		item6.addActionListener(this);
		rowHeaderPopupMenu.add(item6);
		rowHeaderPopupMenu.addSeparator();

		// delete rows item
		int[] selRows = rowHeader.getSelectedIndices();
		String strRows = getDeleteString(selRows);
		JMenuItem item7 = new JMenuItem(strRows);
		item7.setIcon(emptyIcon);
		item7.setActionCommand("delete");
		item7.addActionListener(this);
		rowHeaderPopupMenu.add(item7);

		// handle cell as Textcell
		cbUseAsText = new JCheckBoxMenuItem(loc.getMenu("CasCellUseAsText"));
		cbUseAsText.setActionCommand("useAsText");
		cbUseAsText.setIcon(emptyIcon);
		int[] selRows2 = rowHeader.getSelectedIndices();
		if (selRows2.length != 0) {
			GeoCasCell casCell = table.getGeoCasCell(selRows2[0]);
			cbUseAsText.setSelected(casCell.isUseAsText());
		}
		cbUseAsText.addActionListener(this);
		rowHeaderPopupMenu.add(cbUseAsText);

		// copy selected rows as LaTeX
		JMenuItem latexItem = new JMenuItem(loc.getMenu("CopyAsLaTeX"));
		latexItem.setIcon(emptyIcon);
		latexItem.setActionCommand("copyAsLaTeX");
		latexItem.addActionListener(this);
		rowHeaderPopupMenu.add(latexItem);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int[] selRows = rowHeader.getSelectedIndices();
		if (selRows.length == 0) {
			return;
		}

		boolean undoNeeded = true;

		String ac = e.getActionCommand();
		if ("insertAbove".equals(ac)) {
			app.getKernel().getConstruction().setNotXmlLoading(true);
			table.insertRow(selRows[0], null, true);
			app.getKernel().getConstruction().setNotXmlLoading(false);
			undoNeeded = true;
		} else if ("insertBelow".equals(ac)) {
			app.getKernel().getConstruction().setNotXmlLoading(true);
			table.insertRow(selRows[selRows.length - 1] + 1, null, true);
			app.getKernel().getConstruction().setNotXmlLoading(false);
			undoNeeded = true;
		} else if ("delete".equals(ac)) {
			undoNeeded = table.getCASView().deleteCasCells(selRows);
		} else if ("useAsText".equals(ac)) {
			GeoCasCell casCell2 = table.getGeoCasCell(selRows[0]);
			casCell2.setUseAsText(cbUseAsText.isSelected());
		} else if ("copyAsLaTeX".equals(ac)) {
			String text = table.getCASView().getLaTeXfromCells(selRows);
			if (text != null) {
				StringSelection data = new StringSelection(text);
				Clipboard sysClip = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				sysClip.setContents(data, null);
			}

		}

		if (undoNeeded) {
			// store undo info
			table.getApplication().storeUndoInfo();
		}
	}

	/**
	 * Displays the popup menu at the position x,y in the coordinate
	 * space of the component invoker.
	 * @param component invoker
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public void show(Component component, int x, int y) {
		rowHeaderPopupMenu.show(component, x, y);
	}
}