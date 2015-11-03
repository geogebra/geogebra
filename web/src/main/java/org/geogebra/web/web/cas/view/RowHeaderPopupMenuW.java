package org.geogebra.web.web.cas.view;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.view.spreadsheet.CopyPasteCutW;
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

	enum CellAction {
		COPY_INPUT, PASTE, INSERT_ABOVE, INSERT_BELOW, DELETE, TEXT, COPY, COPY_LATEX
	}

	private void initMenu() {
		// "Insert Above" menuitem
		boolean canSystemCopy = CASTableControllerW.checkClipboardSupported()
				&& CopyPasteCutW
.copyToSystemClipboard(" ");
		MenuItem miCopyInput = new MenuItem(app.getMenu("CopyInput"),
				new ScheduledCommand() {
					public void execute() {
						actionPerformed(CellAction.COPY_INPUT);
					}
				});
		rowHeaderPopupMenu.addItem(miCopyInput);
		miCopyInput.addStyleName("mi_no_image");

		MenuItem miPaste = new MenuItem(app.getMenu("Paste"),
				new ScheduledCommand() {
					public void execute() {
						actionPerformed(CellAction.PASTE);
					}
				});
		rowHeaderPopupMenu.addItem(miPaste);
		miPaste.addStyleName("mi_no_image");

		MenuItem miInsertAbove = new MenuItem(app.getMenu("InsertAbove"),
		        new ScheduledCommand() {
			        public void execute() {
						actionPerformed(CellAction.INSERT_ABOVE);
			        }
		        });
		rowHeaderPopupMenu.addItem(miInsertAbove);
		miInsertAbove.addStyleName("mi_no_image");

		// "Insert Below" menuitem
		MenuItem miInsertBelow = new MenuItem(app.getMenu("InsertBelow"),
		        new ScheduledCommand() {
			        public void execute() {
						actionPerformed(CellAction.INSERT_BELOW);
			        }
		        });
		rowHeaderPopupMenu.addItem(miInsertBelow);
		miInsertBelow.addStyleName("mi_no_image");

		int[] selRows = table.getSelectedRows();
		String strRows = getDeleteString(selRows);
		MenuItem miDelete = new MenuItem(strRows, new ScheduledCommand() {
			public void execute() {
				actionPerformed(CellAction.DELETE);
			}
		});
		rowHeaderPopupMenu.addItem(miDelete);
		miDelete.addStyleName("mi_no_image");

		rowHeaderPopupMenu.addSeparator();

		MenuItem miUseAsText = new MenuItem(app.getMenu("CasCellUseAsText"),
		        new ScheduledCommand() {
			        public void execute() {
						actionPerformed(CellAction.TEXT);
			        }
		        });
		rowHeaderPopupMenu.addItem(miUseAsText);
		miUseAsText.addStyleName("mi_no_image");


		if (canSystemCopy) {

			MenuItem copyItem = new MenuItem(app.getMenu("Copy"),
					new ScheduledCommand() {
						public void execute() {
							actionPerformed(CellAction.COPY);
						}
					});
			rowHeaderPopupMenu.addItem(copyItem);
			copyItem.addStyleName("mi_no_image");

			MenuItem latexItem = new MenuItem(app.getMenu("CopyAsLaTeX"),
				new ScheduledCommand() {
					public void execute() {
							actionPerformed(CellAction.COPY_LATEX);
					}
				});
			rowHeaderPopupMenu.addItem(latexItem);
			latexItem.addStyleName("mi_no_image");
		}
	}

	/**
	 * @param ac
	 *            action name
	 */
	void actionPerformed(CellAction ac) {
		int[] selRows = table.getSelectedRows();
		if (selRows.length == 0)
			return;

		boolean undoNeeded = true;

		switch(ac){
		case INSERT_ABOVE:
			GeoCasCell casCell = new GeoCasCell(app.getKernel()
			        .getConstruction());
			table.insertRow(selRows[0], casCell, true);
			undoNeeded = true;
			break;
		case INSERT_BELOW:
			casCell = new GeoCasCell(app.getKernel()
			        .getConstruction());
			table.insertRow(selRows[selRows.length - 1] + 1, casCell, true);
			undoNeeded = true;
			break;
		case DELETE:
			undoNeeded = table.getCASView().deleteCasCells(selRows);
			break;
		case TEXT:
			casCell = table.getGeoCasCell(selRows[0]);
			boolean useAsText = !casCell.isUseAsText();
			casCell.setUseAsText(useAsText);
			for (int i = 1; i < selRows.length; i++) {
				int selRow = selRows[i];
				casCell = table.getGeoCasCell(selRow);
				casCell.setUseAsText(useAsText);
			}
			undoNeeded = true;
			break;
		case COPY_LATEX:
			String toBeCopied = table.getCASView().getLaTeXfromCells(selRows);
			// it's possible that the last row is the input bar,
			// and if this is the case, the formula ends by:
			// \\ undefined \\
			if (toBeCopied != null) {
				// App.debug("*" + toBeCopied + "*");
				if (toBeCopied.endsWith("\\\\ undefined \\\\ ")) {
					toBeCopied = toBeCopied.substring(0,
							toBeCopied.length() - 14);
				} else if (toBeCopied.endsWith("\\\\ undefined \\\\")) {
					toBeCopied = toBeCopied.substring(0,
							toBeCopied.length() - 13);
				}
				CopyPasteCutW.copyToSystemClipboard(toBeCopied);
			}
			break;
		case COPY:
			toBeCopied = table.getCASView().getTextFromCells(selRows);
			// it's possible that the last row is the input bar,
			// and if this is the case, the formula ends by:
			// \\ undefined \\
			if (toBeCopied != null) {
				CopyPasteCutW.copyToSystemClipboard(toBeCopied);
			}
			break;
		case COPY_INPUT:
			toBeCopied = table.getCASView().getCellInput(selRows[0]);
			// it's possible that the last row is the input bar,
			// and if this is the case, the formula ends by:
			// \\ undefined \\
			App.debug("Copying" + toBeCopied);
			if (toBeCopied != null) {
				CopyPasteCutW.setClipboardContents(toBeCopied, null);
			}
			break;
		case PASTE:
			toBeCopied = CopyPasteCutW.getClipboardContents(null);
			// it's possible that the last row is the input bar,
			// and if this is the case, the formula ends by:
			// \\ undefined \\
			App.debug("Pasting" + toBeCopied);
			if (toBeCopied != null) {
				table.setCellInput(selRows[0], toBeCopied);
			}
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