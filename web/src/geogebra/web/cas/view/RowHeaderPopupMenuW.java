package geogebra.web.cas.view;

import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.web.javax.swing.GPopupMenuW;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.MenuItem;


public class RowHeaderPopupMenuW extends GPopupMenuW{
	
	private RowHeaderWidget rowHeader;
	private CASTableW table;
	private AppW app;
	
	public RowHeaderPopupMenuW(RowHeaderWidget rowHeaderWidget, CASTableW casTableW, AppW appl){
		super(appl);
		rowHeader = rowHeaderWidget;
		table = casTableW;
		app = appl;
		initMenu();
	}

	private void initMenu(){
		//"Insert Above" menuitem
		MenuItem miInsertAbove = new MenuItem(app.getMenu("InsertAbove"),
		        new ScheduledCommand() {
			        public void execute() {
				        actionPerformed("insertAbove");
			        }
		        });
		addItem(miInsertAbove);
		miInsertAbove.addStyleName("mi_no_image");
		
		//"Insert Below" menuitem
		MenuItem miInsertBelow = new MenuItem("InsertBelow",
		        new ScheduledCommand() {
			        public void execute() {
				        actionPerformed(app.getMenu("insertBelow"));
			        }
		        });
		addItem(miInsertBelow);
		miInsertBelow.addStyleName("mi_no_image");
	
		MenuItem miDelete = new MenuItem("Delete", new ScheduledCommand() {
			public void execute() {
				actionPerformed("delete");
			}
		});
		addItem(miDelete);
		miDelete.addStyleName("mi_no_image");


	}
	
	public void actionPerformed(String ac){
		int [] selRows = table.getSelectedRows();
		if (selRows.length == 0) return;
		
		boolean undoNeeded = true;
		
		if (ac.equals("insertAbove")) {
			GeoCasCell casCell = new GeoCasCell(app.getKernel().getConstruction());
			table.insertRow(selRows[0], casCell, true);
			undoNeeded = true;
		}
		else if (ac.equals("insertBelow")) {
			GeoCasCell casCell = new GeoCasCell(app.getKernel().getConstruction());
			table.insertRow(selRows[selRows.length-1]+1, casCell, true);
//			table.insertRow(table.getRowCount(), null, true);
			undoNeeded = true;
		}
		else if (ac.equals("delete")) {
			undoNeeded = table.getCASView().deleteCasCells(selRows);
		}
//		else if(ac.equals("useAsText")) {
//			GeoCasCell casCell2 = table.getGeoCasCell(selRows[0]);
//			casCell2.setUseAsText(cbUseAsText.isSelected());
//		}
		
		if (undoNeeded) {
			// store undo info
			table.getApplication().storeUndoInfo();
		}		
	}
    
}