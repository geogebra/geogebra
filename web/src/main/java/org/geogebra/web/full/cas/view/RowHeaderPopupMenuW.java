package org.geogebra.web.full.cas.view;

import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.util.CopyPaste;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.view.spreadsheet.CopyPasteCutW;
import org.geogebra.web.full.html5.AttachedToDOM;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.CopyPasteW;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * The one popup menu used in web CAS
 *
 */
public class RowHeaderPopupMenuW extends
        org.geogebra.common.cas.view.RowHeaderPopupMenu implements AttachedToDOM {

	private CASTableW table;
	private GPopupMenuW rowHeaderPopupMenu;

	/**
	 * @param casTableW
	 *            table
	 * @param appl
	 *            application
	 */
	public RowHeaderPopupMenuW(
	        CASTableW casTableW, AppW appl) {
		super(appl);
		rowHeaderPopupMenu = new GPopupMenuW(appl);
		table = casTableW;
		initMenu();
	}

	@SuppressWarnings("javadoc")
	enum CellAction {
		COPY_INPUT, PASTE, INSERT_ABOVE, INSERT_BELOW, DELETE, TEXT, COPY, COPY_LATEX
	}

	private void initMenu() {
		// "Insert Above" menuitem
		AriaMenuItem miCopyInput = new AriaMenuItem(loc.getMenu("CopyInput"),
				false,
				new ScheduledCommand() {
					@Override
					public void execute() {
						actionPerformed(CellAction.COPY_INPUT);
					}
				});
		rowHeaderPopupMenu.addItem(miCopyInput);

		AriaMenuItem miPaste = new AriaMenuItem(loc.getMenu("Paste"), false,
				new ScheduledCommand() {
					@Override
					public void execute() {
						actionPerformed(CellAction.PASTE);
					}
				});
		rowHeaderPopupMenu.addItem(miPaste);
		rowHeaderPopupMenu.addSeparator();

		AriaMenuItem miInsertAbove = new AriaMenuItem(
				loc.getMenu("InsertAbove"),
				false,
		        new ScheduledCommand() {
			        @Override
					public void execute() {
						actionPerformed(CellAction.INSERT_ABOVE);
			        }
		        });
		rowHeaderPopupMenu.addItem(miInsertAbove);

		// "Insert Below" menuitem
		AriaMenuItem miInsertBelow = new AriaMenuItem(
				loc.getMenu("InsertBelow"), false,
		        new ScheduledCommand() {
			        @Override
					public void execute() {
						actionPerformed(CellAction.INSERT_BELOW);
			        }
		        });
		rowHeaderPopupMenu.addItem(miInsertBelow);

		int[] selRows = table.getSelectedRows();
		String strRows = getDeleteString(selRows);
		AriaMenuItem miDelete = new AriaMenuItem(strRows, false,
				new ScheduledCommand() {
			@Override
			public void execute() {
				actionPerformed(CellAction.DELETE);
			}
		});
		rowHeaderPopupMenu.addItem(miDelete);

		rowHeaderPopupMenu.addSeparator();

		AriaMenuItem miUseAsText = new AriaMenuItem(
				loc.getMenu("CasCellUseAsText"), false,
		        new ScheduledCommand() {
			        @Override
					public void execute() {
						actionPerformed(CellAction.TEXT);
			        }
		        });
		rowHeaderPopupMenu.addItem(miUseAsText);

		if (CopyPasteCutW.checkClipboardSupported()) {

			AriaMenuItem copyItem = new AriaMenuItem(loc.getMenu("Copy"), false,
					new ScheduledCommand() {
						@Override
						public void execute() {
							actionPerformed(CellAction.COPY);
						}
					});
			rowHeaderPopupMenu.addItem(copyItem);

			AriaMenuItem latexItem = new AriaMenuItem(
					loc.getMenu("CopyAsLaTeX"), false,
					new ScheduledCommand() {
						@Override
						public void execute() {
							actionPerformed(CellAction.COPY_LATEX);
						}
					});
			rowHeaderPopupMenu.addItem(latexItem);
		}
	}

	/**
	 * @param ac
	 *            action name
	 */
	void actionPerformed(CellAction ac) {
		int[] selRows = table.getSelectedRows();
		if (selRows.length == 0) {
			return;
		}

		CopyPaste copyPaste = app.getCopyPaste();
		boolean undoNeeded = true;

		switch (ac) {
		case INSERT_ABOVE:
			GeoCasCell casCell = new GeoCasCell(app.getKernel()
			        .getConstruction());
			app.getKernel().getConstruction().setNotXmlLoading(true);
			table.insertRow(selRows[0], casCell, true);
			app.getKernel().getConstruction().setNotXmlLoading(false);
			undoNeeded = true;
			break;
		case INSERT_BELOW:
			casCell = new GeoCasCell(app.getKernel()
			        .getConstruction());
			app.getKernel().getConstruction().setNotXmlLoading(true);
			table.insertRow(selRows[selRows.length - 1] + 1, casCell, true);
			app.getKernel().getConstruction().setNotXmlLoading(false);
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
				copyPaste.copyTextToSystemClipboard(toBeCopied);
			}
			break;
		case COPY:
			toBeCopied = table.getCASView().getTextFromCells(selRows);
			// it's possible that the last row is the input bar,
			// and if this is the case, the formula ends by:
			// \\ undefined \\
			if (toBeCopied != null) {
				copyPaste.copyTextToSystemClipboard(toBeCopied);
			}
			break;
		case COPY_INPUT:
			toBeCopied = table.getCASView().getCellInput(selRows[0]);
			// it's possible that the last row is the input bar,
			// and if this is the case, the formula ends by:
			// \\ undefined \\
			if (toBeCopied != null) {
				copyPaste.copyTextToSystemClipboard(toBeCopied);
			}
			break;
		case PASTE:
			CopyPasteW.pasteNative(app, (content) -> {
				// it's possible that the last row is the input bar,
				// and if this is the case, the formula ends by:
				// \\ undefined \\
				Log.debug("Pasting" + content);
				if (content != null) {
					table.setCellInput(selRows[0], content);
				}
			});
		}

		if (undoNeeded) {
			// store undo info
			table.getApplication().storeUndoInfo();
		}
	}

	@Override
	public void removeFromDOM() {
		rowHeaderPopupMenu.removeFromDOM();
	}

	/**
	 * show the popup
	 * 
	 * @param x
	 *            x-coord of the point where the popup should appear
	 *  @param y
	 * 	          y-coord of the point where the popup should appear
	 */
	public void show(double x, double y) {
		rowHeaderPopupMenu.show(x, y);
	}

}
