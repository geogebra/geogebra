package geogebra.cas.view;

import geogebra.kernel.GeoCasCell;
import geogebra.main.Application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class RowHeaderPopupMenu extends JPopupMenu implements ActionListener {

	private static final long serialVersionUID = -592258674730774706L;

	private JList rowHeader;
	private CASTable table;
	private Application app;

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
		
		if (undoNeeded) {
			// store undo info
			table.app.storeUndoInfo();
		}
	}
}