package org.geogebra.common.gui.view.spreadsheet;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.plugin.EventType;

/**
 * Superclass that creates a context menu for spreadsheet cells, rows and
 * columns. GUI methods must be implemented by subclasses.
 * 
 * @author G. Sturr
 * 
 */
public class SpreadsheetContextMenu {

	/** application */
	protected App app;

	/** spreadsheet table */
	protected MyTable table = null;

	private ArrayList<GeoElement> geos;
	private CellRangeProcessor cp;

	private ArrayList<CellRange> selectedCellRanges;
	private int selectionType;

	/** minimum selected row */
	private int row1 = -1;

	/** maximum selected row */
	private int row2 = -1;

	/** minimum selected column */
	private int column1 = -1;

	/** maximum selected column */
	private int column2 = -1;

	private Localization loc;

	@SuppressWarnings("javadoc")
	public enum MenuCommand {
		ShowObject, ShowLabel, HideLabel,

		RecordToSpreadsheet, DontRecordToSpreadsheet,

		Copy, Paste, Duplicate, Cut, Delete, DeleteObjects,

		InsertLeft, InsertRight, InsertAbove, InsertBelow,

		DeleteRow, DeleteRows, DeleteColumn, DeleteColumns,

		List, ListOfPoints, Matrix, Table, PolyLine, OperationTable,

		ImportDataFile,

		SpreadsheetOptions, Properties, Create;
	}

	/**
	 * Constructor
	 * 
	 * @param table
	 *            spreadsheet table
	 */
	public SpreadsheetContextMenu(MyTable table) {

		this.table = table;
		updateFields();
		createGUI();
	}

	/**
	 * Update private fields.
	 */
	protected void updateFields() {
		app = table.getApplication();
		this.loc = app.getLocalization();
		cp = table.getCellRangeProcessor();

		column1 = table.getSelectedCellRanges().get(0).getMinColumn();
		column2 = table.getSelectedCellRanges().get(0).getMaxColumn();
		row1 = table.getSelectedCellRanges().get(0).getMinRow();
		row2 = table.getSelectedCellRanges().get(0).getMaxRow();
		selectionType = table.getSelectionType();
		selectedCellRanges = table.getSelectedCellRanges();
		geos = app.getSelectionManager().getSelectedGeos();
	}

	// ===============================================
	// Menu
	// ===============================================

	protected void addCut() {
		String cmdString = MenuCommand.Cut.toString();
		addMenuItem(cmdString, loc.getMenu(cmdString), !isEmptySelection());
	}

	protected void addCopy() {
		String cmdString = MenuCommand.Copy.toString();
		addMenuItem(cmdString, loc.getMenu(cmdString), !isEmptySelection());
	}

	protected void addPaste() {
		String cmdString = MenuCommand.Paste.toString();
		addMenuItem(cmdString, loc.getMenu(cmdString), true);
	}

	protected void addDelete() {
		// TODO use "DeleteObjects" as text ?
		String cmdString = (geos != null && geos.size() > 1)
				? MenuCommand.DeleteObjects.toString()
				: MenuCommand.Delete.toString();

		addMenuItem(cmdString, loc.getMenu(cmdString), !allProtected());

	}

	protected void addEditItems() {
		addCopy();
		addPaste();
		addCut();
		addDelete();
	}

	/**
	 * Load menu items
	 */
	protected void initMenu() {

		Object subMenu = null;
		String cmdString = null;

		setTitle(getTitleString());
		addSeparator();

		// ===============================================
		// Cut-Copy-Paste-Delete
		// ===============================================

		addEditItems();

		// ===============================================
		// Insert/Delete Rows or Columns
		// ===============================================
		boolean enabled = false;

		if (selectionType == MyTableInterface.COLUMN_SELECT
				|| selectionType == MyTableInterface.ROW_SELECT) {

			addSeparator();

			subMenu = addSubMenu(loc.getMenu("Insert"), null);

			if (selectionType == MyTableInterface.COLUMN_SELECT) {

				cmdString = MenuCommand.InsertLeft.toString();
				addSubMenuItem(subMenu, cmdString, loc.getMenu(cmdString),
						true);

				cmdString = MenuCommand.InsertRight.toString();
				addSubMenuItem(subMenu, cmdString, loc.getMenu(cmdString),
						true);
			}

			if (selectionType == MyTableInterface.ROW_SELECT) {

				cmdString = MenuCommand.InsertAbove.toString();
				addSubMenuItem(subMenu, cmdString, loc.getMenu(cmdString),
						true);

				cmdString = MenuCommand.InsertBelow.toString();
				addSubMenuItem(subMenu, cmdString, loc.getMenu(cmdString),
						true);
			}

			if (selectionType == MyTableInterface.COLUMN_SELECT) {
				cmdString = MenuCommand.DeleteColumn.toString();
				enabled = true;
				addMenuItem(cmdString, getDeleteColumnString(), enabled);
			}

			if (selectionType == MyTableInterface.ROW_SELECT) {
				cmdString = MenuCommand.DeleteRow.toString();
				enabled = true;
				addMenuItem(cmdString, getDeleteRowString(), enabled);
			}

		}

		// ===============================================
		// Create (Lists, Matrix, etc.)
		// ===============================================

		if (!isEmptySelection()) {

			addSeparator();

			subMenu = addSubMenu(loc.getMenu("Create"),
					MenuCommand.Create.toString());

			cmdString = MenuCommand.List.toString();
			enabled = true;
			addSubMenuItem(subMenu, cmdString, loc.getMenu(cmdString), enabled);

			cmdString = MenuCommand.ListOfPoints.toString();
			enabled = cp.isCreatePointListPossible(selectedCellRanges);
			addSubMenuItem(subMenu, cmdString, loc.getMenu(cmdString), enabled);

			cmdString = MenuCommand.Matrix.toString();
			enabled = cp.isCreateMatrixPossible(selectedCellRanges);
			addSubMenuItem(subMenu, cmdString, loc.getMenu(cmdString), enabled);

			cmdString = MenuCommand.Table.toString();
			enabled = cp.isCreateMatrixPossible(selectedCellRanges);
			addSubMenuItem(subMenu, cmdString, loc.getMenu(cmdString), enabled);

			cmdString = MenuCommand.PolyLine.toString();
			enabled = cp.isCreatePointListPossible(selectedCellRanges);
			addSubMenuItem(subMenu, cmdString, loc.getMenu(cmdString), enabled);

			cmdString = MenuCommand.OperationTable.toString();
			enabled = cp.isCreateOperationTablePossible(selectedCellRanges);
			addSubMenuItem(subMenu, cmdString, loc.getMenu(cmdString), enabled);

		}

		// ===============================================
		// Show Object or Label
		// ===============================================

		if (!isEmptySelection()) {
			final GeoElement geo = geos.get(0);

			boolean doObjectMenu = geo.isEuclidianShowable()
					&& geo.getShowObjectCondition() == null
					&& (!geo.isGeoBoolean() || geo.isIndependent());

			boolean doLabelMenu = geo.isLabelShowable();

			if (doObjectMenu || doLabelMenu) {
				addSeparator();

				if (doObjectMenu) {
					addShowObject(geo);
				}

				if (doLabelMenu) {
					cmdString = MenuCommand.ShowLabel.toString();
					addCheckBoxMenuItem(cmdString, loc.getMenu(cmdString),
							geo.isLabelVisible());
				}
			}

			// ===============================================
			// Tracing
			// ===============================================

			app.isHTML5Applet();
			if (geo.isSpreadsheetTraceable()
					&& selectionType != MyTableInterface.ROW_SELECT) {

				boolean showRecordToSpreadsheet = true;
				// check if other geos are recordable
				for (int i = 1; i < geos.size() && showRecordToSpreadsheet; i++) {
					showRecordToSpreadsheet &= geos.get(i)
							.isSpreadsheetTraceable();
				}

				if (showRecordToSpreadsheet) {
					cmdString = MenuCommand.RecordToSpreadsheet.toString();

					addCheckBoxMenuItem(cmdString, loc.getMenu(cmdString),
							geo.getSpreadsheetTrace());
				}
			}

		}

		// ===============================================
		// Import Data
		// ===============================================

		if (enableDataImport()) {
			cmdString = MenuCommand.ImportDataFile.toString();
			addMenuItem(cmdString, loc.getMenu(cmdString), true);
		}

		// ===============================================
		// Spreadsheet Options
		// ===============================================

		if (isEmptySelection()) {
			addSeparator();

			cmdString = MenuCommand.SpreadsheetOptions.toString();
			addMenuItem(cmdString, loc.getMenu(cmdString), true);
		}

		// ===============================================
		// Object properties
		// ===============================================

		if (app.getSelectionManager().selectedGeosSize() > 0
				&& app.letShowPropertiesDialog()) {
			addSeparator();

			cmdString = MenuCommand.Properties.toString();
			addMenuItem(cmdString, loc.getMenu(cmdString), true);
		}
	}

	protected void addShowObject(GeoElement geo) {
		String cmdString = MenuCommand.ShowObject.toString();
		addCheckBoxMenuItem(cmdString, loc.getMenu(cmdString),
				geo.isSetEuclidianVisible());
	}

	private String getTitleString() {
		if (selectedCellRanges.size() > 1) {
			return loc.getMenu("Selection");
		}
		String title = GeoElementSpreadsheet.getSpreadsheetCellName(column1,
				row1);
		if (column1 != column2 || row1 != row2) {
			title += ":" + GeoElementSpreadsheet.getSpreadsheetCellName(column2,
					row2);
		}
		// title = geo description if single geo in cell
		else if (geos != null && geos.size() == 1) {
			GeoElement geo0 = geos.get(0);
			title = geo0.getLongDescriptionHTML(false, true);
			if (title.length() > 80) {
				title = geo0.getNameDescriptionHTML(false, true);
			}
		}

		return title;
	}

	/**
	 * @return true if data file can be read locally
	 */
	public boolean enableDataImport() {
		// to be overridden in Desktop
		return false;
	}

	private boolean allProtected() {
		boolean allFixed = true;
		if (geos != null && geos.size() > 0) {
			for (int i = 0; (i < geos.size() && allFixed); i++) {
				GeoElement geo = geos.get(i);
				if (!geo.isProtected(EventType.REMOVE)) {
					allFixed = false;
				}
			}
		}
		return allFixed;
	}

	/**
	 * @return true if no GeoElements selected
	 */
	public boolean isEmptySelection() {
		return (app.getSelectionManager().getSelectedGeos().isEmpty());
	}

	private String getDeleteRowString() {

		String strRows;

		if (row1 == row2) {
			strRows = app.getLocalization().getPlain("DeleteRowA",
					Integer.toString(row1 + 1));
		} else {
			strRows = app.getLocalization().getPlain("DeleteRowsAtoB",
					Integer.toString(row1 + 1), Integer.toString(row2 + 1));
		}
		return strRows;
	}

	private String getDeleteColumnString() {

		String strColumns;

		if (column1 == column2) {
			strColumns = app.getLocalization().getPlain("DeleteColumnA",
					GeoElementSpreadsheet.getSpreadsheetColumnName(column1));
		} else {
			strColumns = app.getLocalization().getPlain("DeleteColumnsAtoB",
					GeoElementSpreadsheet.getSpreadsheetColumnName(column1),
					GeoElementSpreadsheet.getSpreadsheetColumnName(column2));
		}
		return strColumns;
	}

	// ===============================================
	// Command Processor
	// ===============================================

	/**
	 * Performs menu item command for given command key
	 * 
	 * @param cmdString
	 *            command key
	 */
	public void doCommand(String cmdString) {
		boolean succ = false;

		switch (MenuCommand.valueOf(cmdString)) {

		default:
			// do nothing
			break;
		case ShowObject:
			cmdShowObject();
			break;

		case ShowLabel:
			cmdShowLabel();
			break;

		case RecordToSpreadsheet:
			cmdRecordToSpreadsheet();
			break;

		case Copy:
			cmdCopy();
			break;

		case Duplicate:
			cmdCopy();
			cmdPaste();
			break;

		case Paste:
			cmdPaste();
			break;

		case Cut:
			succ = table.getCopyPasteCut().cut(column1, row1, column2, row2);
			if (succ) {
				app.storeUndoInfo();
			}
			break;

		case Delete:
		case DeleteObjects:
			succ = table.getCopyPasteCut().delete(column1, row1, column2, row2);
			if (succ) {
				app.storeUndoInfo();
			}
			break;

		case InsertLeft:
			cp.insertColumn(column1, column2, true);
			break;

		case InsertRight:
			cp.insertColumn(column1, column2, false);
			break;

		case InsertAbove:
			cp.insertRow(row1, row2, true);
			break;

		case InsertBelow:
			cp.insertRow(row1, row2, false);
			break;

		case DeleteColumn:
			cp.deleteColumns(column1, column2);
			break;

		case DeleteRow:
			cp.deleteRows(row1, row2);
			break;

		case List:
			cp.createList(selectedCellRanges, true, false);
			break;

		case ListOfPoints:
			cmdListOfPoints();
			break;

		case Matrix:
			cp.createMatrix(column1, column2, row1, row2, false);
			break;

		case Table:
			cp.createTableText(column1, column2, row1, row2, false, false);
			break;

		case PolyLine:
			cmdPolyLine();
			break;

		case OperationTable:
			cp.createOperationTable(selectedCellRanges.get(0));
			break;

		case ImportDataFile:
			cmdImportDataFile();
			break;

		case SpreadsheetOptions:
			cmdSpreadsheetOptions();
			break;

		case Properties:
			cmdProperties();
			break;

		}

	}

	private void cmdCopy() {
		table.getCopyPasteCut().copy(column1, row1, column2, row2, false);

	}

	private void cmdPaste() {
		boolean succ = table.getCopyPasteCut().paste(column1, row1, column2,
				row2);
		if (succ) {
			app.storeUndoInfo();
		}
		table.getView().rowHeaderRevalidate();

	}

	// =============================
	// Action Commands
	// =============================

	/**
	 * Hide/show selected cells in EV
	 */
	public void cmdShowObject() {
		for (int i = geos.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos.get(i);
			geo1.setEuclidianVisible(!geo1.isSetEuclidianVisible());
			geo1.updateRepaint();

		}
		app.storeUndoInfo();
	}

	/**
	 * Hide/show labels of selected cells in EV
	 */
	public void cmdShowLabel() {
		for (int i = geos.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos.get(i);
			geo1.setLabelVisible(!geo1.isLabelVisible());
			geo1.updateRepaint();

		}
		app.storeUndoInfo();
	}

	/**
	 * Trace selected cells in the spreadsheet TODO: is this needed?
	 */
	public void cmdRecordToSpreadsheet() {
		GeoElement geo = geos.get(0);
		GeoElement geoRecordToSpreadSheet;
		if (geos.size() == 1) {
			geoRecordToSpreadSheet = geo;
		} else {
			geoRecordToSpreadSheet = app.getKernel().getAlgoDispatcher()
					.list(null, geos, false);
			geoRecordToSpreadSheet.setAuxiliaryObject(true);
		}

		table.getView().showTraceDialog(geoRecordToSpreadSheet, null);
	}

	/**
	 * Create list of points from selected cells
	 */
	public void cmdListOfPoints() {
		GeoElement newGeo = cp.createPointGeoList(selectedCellRanges, false,
				true, true, true);
		app.getKernel().getConstruction()
				.addToConstructionList(newGeo.getParentAlgorithm(), true);
		newGeo.setLabel(null);
	}

	/**
	 * Create PolyLine object from selected cells
	 */
	public void cmdPolyLine() {
		GeoElement newGeo = cp.createPolyLine(selectedCellRanges, false, true);
		app.getKernel().getConstruction()
				.addToConstructionList(newGeo.getParentAlgorithm(), true);
		newGeo.setLabel(null);
	}

	/**
	 * Import data file
	 */
	public void cmdImportDataFile() {
		// to be overridden
	}

	/**
	 * Open spreadsheet options dialog
	 */
	public void cmdSpreadsheetOptions() {
		app.getDialogManager().showPropertiesDialog(OptionType.SPREADSHEET,
				null);
	}

	/**
	 * Open Object Properties dialog
	 */
	public void cmdProperties() {
		app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS, null);
	}

	// ==================================================
	//
	// GUI methods to be implemented in subclasses.
	//
	// ==================================================

	/**
	 * @return PopUp menu container
	 */
	public Object getMenuContainer() {
		// to be overridden
		return null;
	}

	/**
	 * Create popUp menu using the initMenu() method.
	 */
	public void createGUI() {
		// to be overridden
	}

	/**
	 * @param cmdString
	 *            Action command key (and icon key)
	 * @param text
	 *            Text to be displayed in the menu
	 * @param enabled
	 *            Flag to enable/disable the menu item
	 */
	public void addMenuItem(final String cmdString, String text,
			boolean enabled) {
		// to be overridden
	}

	/**
	 * @param cmdString
	 *            Action command key (and icon key)
	 * @param text
	 *            Text to be displayed in the menu
	 * @param isSelected
	 *            flag Flag to set selection state
	 */
	public void addCheckBoxMenuItem(final String cmdString, String text,
			boolean isSelected) {
		// to be overridden
	}

	/**
	 * @param text
	 *            Text to be displayed in the menu
	 * @param cmdString
	 *            Action command key (and icon key)
	 * @return Menu object
	 */
	public Object addSubMenu(String text, String cmdString) {
		// to be overridden
		return null;
	}

	/**
	 * @param menu
	 *            Menu container to add new menu item
	 * @param cmdString
	 *            Action command key (and icon key)
	 * @param text
	 *            Text to be displayed in the menu
	 * @param enabled
	 *            Flag to enable/disable the menu item
	 */
	public void addSubMenuItem(Object menu, final String cmdString, String text,
			boolean enabled) {
		// to be overridden
	}

	/**
	 * Add separator bar to menu
	 */
	public void addSeparator() {
		// to be overridden
	}

	/**
	 * Sets the menu title and adds mouse handling to close the menu if clicked.
	 * 
	 * @param str
	 *            Title string to add to top of menu
	 */
	public void setTitle(String str) {
		// to be overridden
	}

}
