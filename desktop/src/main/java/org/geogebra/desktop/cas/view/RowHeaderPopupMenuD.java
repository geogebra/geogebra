package org.geogebra.desktop.cas.view;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.desktop.main.AppD;

/**
 * Popup menu for row headers
 * 
 */
public class RowHeaderPopupMenuD extends
		org.geogebra.common.cas.view.RowHeaderPopupMenu implements ActionListener {

	private static final long serialVersionUID = -592258674730774706L;

	private JList rowHeader;
	private CASTableD table;
	private JMenuItem cbUseAsText;
	private JPopupMenu rowHeaderPopupMenu;

	/**
	 * Creates new popup menu
	 * 
	 * @param rowHeader
	 *            row headers
	 * @param table
	 *            CAS table
	 */
	public RowHeaderPopupMenuD(JList rowHeader, CASTableD table) {
		rowHeaderPopupMenu = new JPopupMenu();
		this.rowHeader = rowHeader;
		this.table = table;
		app = (AppD) table.getApplication();
		initMenu();
	}

	/**
	 * Create menu items and put them into the menu
	 */
	protected void initMenu() {
		// insert above
		JMenuItem item5 = new JMenuItem(app.getMenu("InsertAbove"));
		item5.setIcon(((AppD) app).getEmptyIcon());
		item5.setActionCommand("insertAbove");
		item5.addActionListener(this);
		rowHeaderPopupMenu.add(item5);

		// insert below
		JMenuItem item6 = new JMenuItem(app.getMenu("InsertBelow"));
		item6.setIcon(((AppD) app).getEmptyIcon());
		item6.setActionCommand("insertBelow");
		item6.addActionListener(this);
		rowHeaderPopupMenu.add(item6);
		rowHeaderPopupMenu.addSeparator();

		// delete rows item
		int[] selRows = rowHeader.getSelectedIndices();
		String strRows = getDeleteString(selRows);
		JMenuItem item7 = new JMenuItem(strRows);
		item7.setIcon(((AppD) app).getEmptyIcon());
		item7.setActionCommand("delete");
		item7.addActionListener(this);
		rowHeaderPopupMenu.add(item7);

		// handle cell as Textcell
		cbUseAsText = new JCheckBoxMenuItem(app.getMenu("CasCellUseAsText"));
		cbUseAsText.setActionCommand("useAsText");
		cbUseAsText.setIcon(((AppD) app).getEmptyIcon());
		int[] selRows2 = rowHeader.getSelectedIndices();
		if (selRows2.length != 0) {
			GeoCasCell casCell = table.getGeoCasCell(selRows2[0]);
			cbUseAsText.setSelected(casCell.isUseAsText());
		}
		cbUseAsText.addActionListener(this);
		rowHeaderPopupMenu.add(cbUseAsText);

		// copy selected rows as LaTeX
		JMenuItem latexItem = new JMenuItem(app.getMenu("CopyAsLaTeX"));
		latexItem.setIcon(((AppD) app).getEmptyIcon());
		latexItem.setActionCommand("copyAsLaTeX");
		latexItem.addActionListener(this);
		rowHeaderPopupMenu.add(latexItem);

	}

	public void actionPerformed(ActionEvent e) {
		int[] selRows = rowHeader.getSelectedIndices();
		if (selRows.length == 0)
			return;

		boolean undoNeeded = true;

		String ac = e.getActionCommand();
		if (ac.equals("insertAbove")) {
			table.insertRow(selRows[0], null, true);
			undoNeeded = true;
		} else if (ac.equals("insertBelow")) {
			table.insertRow(selRows[selRows.length - 1] + 1, null, true);
			undoNeeded = true;
		} else if (ac.equals("delete")) {
			undoNeeded = table.getCASView().deleteCasCells(selRows);
		} else if (ac.equals("useAsText")) {
			GeoCasCell casCell2 = table.getGeoCasCell(selRows[0]);
			casCell2.setUseAsText(cbUseAsText.isSelected());
		} else if (ac.equals("copyAsLaTeX")) {
			StringSelection data = new StringSelection(table.getCASView()
					.getLaTeXfromCells(selRows));
			if (data != null) {
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

	public void show(Component component, int x, int y) {
		rowHeaderPopupMenu.show(component, x, y);
	}
}