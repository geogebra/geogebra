package geogebra.cas.view;

import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.main.AppD;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Popup menu for row headers
 *
 */
public class RowHeaderPopupMenu extends JPopupMenu implements ActionListener {

	private static final long serialVersionUID = -592258674730774706L;

	private JList rowHeader;
	private CASTableD table;
	private AppD app;
	
	private JMenuItem cbUseAsText;

	/**
	 * Creates new popup menu
	 * @param rowHeader row headers
	 * @param table CAS table
	 */
	public RowHeaderPopupMenu(JList rowHeader, CASTableD table) {
		this.rowHeader = rowHeader;
		this.table = table;
		app = (AppD)table.getApplication();		
		initMenu();
	}

	/**
	 * Create menu items and put them into the menu
	 */
	protected void initMenu() {
		// insert above
		JMenuItem item5 = new JMenuItem(app.getMenu("InsertAbove"));
		item5.setIcon(app.getEmptyIcon());
		item5.setActionCommand("insertAbove");
		item5.addActionListener(this);
		add(item5);
		
		// insert below
		JMenuItem item6 = new JMenuItem(app.getMenu("InsertBelow"));
		item6.setIcon(app.getEmptyIcon());
		item6.setActionCommand("insertBelow");
		item6.addActionListener(this);
		add(item6);
		addSeparator();
		
		// delete rows item
		int [] selRows = rowHeader.getSelectedIndices();	
		String strRows;
		if (selRows.length == 1) {
			strRows = app.getLocalization().getPlain("DeleteRowA", Integer.toString(selRows[0]+1));			
		} else {
			strRows = app.getLocalization().getPlain("DeleteRowsAtoB", 
						Integer.toString(selRows[0]+1), 
						Integer.toString(selRows[selRows.length-1]+1));
		}
		JMenuItem item7 = new JMenuItem(strRows);		
		item7.setIcon(app.getEmptyIcon());
		item7.setActionCommand("delete");
		item7.addActionListener(this);
		add(item7);
		
		//handle cell as Textcell
		cbUseAsText = new JCheckBoxMenuItem(app.getMenu("CasCellUseAsText"));
		cbUseAsText.setActionCommand("useAsText");
		cbUseAsText.setIcon(app.getEmptyIcon());
		int [] selRows2 = rowHeader.getSelectedIndices();
		if (selRows2.length != 0) {
			GeoCasCell casCell = table.getGeoCasCell(selRows2[0]);
			cbUseAsText.setSelected(casCell.isUseAsText());
		}
		cbUseAsText.addActionListener(this);
		add(cbUseAsText);  
		
	}

	public void actionPerformed(ActionEvent e) {
		int [] selRows = rowHeader.getSelectedIndices();
		if (selRows.length == 0) return;
		
		boolean undoNeeded = true;
		
		String ac = e.getActionCommand();
		if (ac.equals("insertAbove")) {
			table.insertRow(selRows[0], null, true);
			undoNeeded = true;
		}
		else if (ac.equals("insertBelow")) {
			table.insertRow(selRows[selRows.length-1]+1, null, true);
			undoNeeded = true;
		}
		else if (ac.equals("delete")) {
			undoNeeded = table.getCASView().deleteCasCells(selRows);
		}
		else if(ac.equals("useAsText")) {
			GeoCasCell casCell2 = table.getGeoCasCell(selRows[0]);
			casCell2.setUseAsText(cbUseAsText.isSelected());
		}
		
		if (undoNeeded) {
			// store undo info
			table.getApplication().storeUndoInfo();
		}
	}
}