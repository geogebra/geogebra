package geogebra.cas.view;

import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;

public class RowHeaderPopupMenu extends JPopupMenu implements ActionListener {

	private static final long serialVersionUID = -592258674730774706L;

	private JList rowHeader;
	private CASTable table;
	private Application app;
	
	private JMenuItem cbUseAsText;

	public RowHeaderPopupMenu(JList rowHeader, CASTable table) {
		this.rowHeader = rowHeader;
		this.table = table;
		app = table.app;		
		initMenu();
	}

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
			strRows = app.getPlain("DeleteRowA", Integer.toString(selRows[0]+1));			
		} else {
			strRows = app.getPlain("DeleteRowsAtoB", 
						Integer.toString(selRows[0]+1), 
						Integer.toString(selRows[selRows.length-1]+1));
		}
		JMenuItem item7 = new JMenuItem(strRows);		
		item7.setIcon(app.getEmptyIcon());
		item7.setActionCommand("delete");
		item7.addActionListener(this);
		add(item7);
		
		//handle cell as Textcell
		cbUseAsText = new JCheckBoxMenuItem(app.getMenu("UseAsText"));
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
			for (int i=selRows.length-1; i >= 0; i--) {
				GeoCasCell casCell = table.getGeoCasCell(selRows[i]);
				if (casCell != null) {
					casCell.remove();
					undoNeeded = true;
				}
			}
		}
		else if(ac.equals("useAsText")) {
			GeoCasCell casCell2 = table.getGeoCasCell(selRows[0]);
			casCell2.setUseAsText(cbUseAsText.isSelected());
		}
		
		if (undoNeeded) {
			// store undo info
			table.app.storeUndoInfo();
		}
	}
}