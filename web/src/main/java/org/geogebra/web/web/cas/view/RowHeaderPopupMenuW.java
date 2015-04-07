package org.geogebra.web.web.cas.view;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.html5.AttachedToDOM;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.MenuItem;

public class RowHeaderPopupMenuW extends
        org.geogebra.common.cas.view.RowHeaderPopupMenu implements AttachedToDOM {

	private RowHeaderWidget rowHeader;
	private CASTableW table;
	private GPopupMenuW rowHeaderPopupMenu;

	public RowHeaderPopupMenuW(RowHeaderWidget rowHeaderWidget,
	        CASTableW casTableW, AppW appl) {
		rowHeaderPopupMenu = new GPopupMenuW(appl);
		rowHeader = rowHeaderWidget;
		table = casTableW;
		app = appl;
		initMenu();
	}

	private void initMenu() {
		// "Insert Above" menuitem
		MenuItem miInsertAbove = new MenuItem(app.getMenu("InsertAbove"),
		        new ScheduledCommand() {
			        public void execute() {
				        actionPerformed("insertAbove");
			        }
		        });
		rowHeaderPopupMenu.addItem(miInsertAbove);
		miInsertAbove.addStyleName("mi_no_image");

		// "Insert Below" menuitem
		MenuItem miInsertBelow = new MenuItem(app.getMenu("InsertBelow"),
		        new ScheduledCommand() {
			        public void execute() {
				        actionPerformed(app.getMenu("insertBelow"));
			        }
		        });
		rowHeaderPopupMenu.addItem(miInsertBelow);
		miInsertBelow.addStyleName("mi_no_image");

		int[] selRows = table.getSelectedRows();
		String strRows = getDeleteString(selRows);
		MenuItem miDelete = new MenuItem(strRows, new ScheduledCommand() {
			public void execute() {
				actionPerformed("delete");
			}
		});
		rowHeaderPopupMenu.addItem(miDelete);
		miDelete.addStyleName("mi_no_image");

		rowHeaderPopupMenu.addSeparator();

		MenuItem miUseAsText = new MenuItem(app.getMenu("CasCellUseAsText"),
		        new ScheduledCommand() {
			        public void execute() {
				        actionPerformed("useAsText");
			        }
		        });
		rowHeaderPopupMenu.addItem(miUseAsText);
		miUseAsText.addStyleName("mi_no_image");

	}

	public void actionPerformed(String ac) {
		int[] selRows = table.getSelectedRows();
		if (selRows.length == 0)
			return;

		boolean undoNeeded = true;

		if (ac.equals("insertAbove")) {
			GeoCasCell casCell = new GeoCasCell(app.getKernel()
			        .getConstruction());
			table.insertRow(selRows[0], casCell, true);
			undoNeeded = true;
		} else if (ac.equals("insertBelow")) {
			GeoCasCell casCell = new GeoCasCell(app.getKernel()
			        .getConstruction());
			table.insertRow(selRows[selRows.length - 1] + 1, casCell, true);
			undoNeeded = true;
		} else if (ac.equals("delete")) {
			undoNeeded = table.getCASView().deleteCasCells(selRows);
		} else if (ac.equals("useAsText")) {
			GeoCasCell casCell = table.getGeoCasCell(selRows[0]);
			boolean useAsText = !casCell.isUseAsText();
			casCell.setUseAsText(useAsText);
			for (int i = 1; i < selRows.length; i++) {
				int selRow = selRows[i];
				casCell = table.getGeoCasCell(selRow);
				casCell.setUseAsText(useAsText);
			}
			undoNeeded = true;
		}

		if (undoNeeded) {
			// store undo info
			table.getApplication().storeUndoInfo();
		}
	}

	public void removeFromDOM() {
		rowHeaderPopupMenu.removeFromDOM();
	}

	public void show(GPoint gPoint) {
		rowHeaderPopupMenu.show(gPoint);

	}

}