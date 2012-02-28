package geogebra.gui.view.spreadsheet;

import geogebra.common.gui.view.spreadsheet.AbstractSpreadsheetTableModel;
import geogebra.common.gui.view.spreadsheet.CellRange;
import geogebra.common.gui.view.spreadsheet.RelativeCopy;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.main.Application;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class CopyPasteCut {

	// ggb support classes
	protected Kernel kernel;
	protected Application app;
	private AbstractSpreadsheetTableModel tableModel;

	private SpreadsheetView view;
	private MyTable table;

	/**
	 * Stores copied cell geo values as a tab-delimited string.
	 */
	protected String cellBufferStr;

	/**
	 * Stores copied cell geos as GeoElement[columns][rows]
	 */
	protected GeoElement[][] cellBufferGeo;

	/**
	 * Records the first row and first column of the current cell range copy
	 * source
	 */
	protected int sourceColumn1, sourceRow1;

	/**
	 * Stores construction index values while performing a paste
	 */
	private Object[] constructionIndexes;

	/***************************************
	 * Constructor
	 */
	public CopyPasteCut(Application app) {

		tableModel = app.getSpreadsheetTableModel();
		this.app = app;
		kernel = app.getKernel();

	}

	private SpreadsheetView getView() {
		if (view == null) {
			view = app.getGuiManager().getSpreadsheetView();
		}

		return view;
	}

	private MyTable getTable() {
		if (table == null) {
			table = getView().getTable();
		}

		return table;
	}

	/**
	 * Combines the GeoElement.toValueStrings from a given block of cell geos
	 * into a single tab-delimited string. This string is stored in (1) the
	 * global String field cellBufferStr and (2) the system clipboard.
	 * 
	 * If skipGeoCopy = false, the geos are also stored in the global
	 * GeoElement[][] field cellBufferGeo
	 * 
	 * The cell block is defined by upper-left corner (column1, row1) and lower
	 * left corner (column2, row2)
	 * 
	 * @param column1
	 * @param row1
	 * @param column2
	 * @param row2
	 * @param skipGeoCopy
	 */
	public void copy(int column1, int row1, int column2, int row2,
			boolean skipGeoCopy) {

		// copy tab-delimited geo values into the external buffer
		cellBufferStr = "";
		for (int row = row1; row <= row2; ++row) {
			for (int column = column1; column <= column2; ++column) {
				GeoElement value = RelativeCopy.getValue(app, column, row);
				if (value != null) {
					cellBufferStr += value
							.toValueString(StringTemplate.maxPrecision);
				}
				if (column != column2) {
					cellBufferStr += "\t";
				}
			}
			if (row != row2) {
				cellBufferStr += "\n";
			}
		}

		// store the tab-delimited values in the clipboard
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection stringSelection = new StringSelection(cellBufferStr);
		clipboard.setContents(stringSelection, null);

		// store copies of the actual geos in the internal buffer
		if (skipGeoCopy) {
			cellBufferGeo = null;
		} else {
			sourceColumn1 = column1;
			sourceRow1 = row1;
			cellBufferGeo = RelativeCopy.getValues(app, column1, row1, column2,
					row2);
		}
	}

	/**
	 * Copies the contents of the cell block defined by upper-left corner
	 * (column1, row1) and lower left corner (column2, row2) into the system
	 * clipboard and then deletes these geos.
	 * 
	 * TODO: The external buffer is nulled out so that a followup paste will not
	 * perform a relative copy. This needs to be fixed, relative copy is
	 * expected by the user.
	 * 
	 * @param column1
	 * @param row1
	 * @param column2
	 * @param row2
	 * @return
	 */
	public boolean cut(int column1, int row1, int column2, int row2) {

		copy(column1, row1, column2, row2, false);
		// null out the external buffer so that paste will not do a relative
		// copy
		cellBufferStr = null;
		return delete(column1, row1, column2, row2);
	}

	/**
	 * Pastes data from the clipboard into the given spreadsheet cell range.
	 * 
	 * @param cr
	 *            the target cell range
	 * @return
	 */
	public boolean paste(CellRange cr) {
		return paste(cr.getMinColumn(), cr.getMinRow(), cr.getMaxColumn(),
				cr.getMaxRow());
	}

	/**
	 * Pastes data from the clipboard into the given spreadsheet cells.
	 * 
	 * @param column1
	 *            first column of the target cell range
	 * @param row1
	 *            first row of the target cell range
	 * @param column2
	 *            last column of the target cell range
	 * @param row2
	 *            last row of the target cell range
	 * @return
	 */
	public boolean paste(int column1, int row1, int column2, int row2) {

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);

		return paste(column1, row1, column2, row2, contents);
	}

	/**
	 * Pastes data from given Transferable into the given spreadsheet cells.
	 * 
	 * @param column1
	 *            first column of the target cell range
	 * @param row1
	 *            first row of the target cell range
	 * @param column2
	 *            last column of the target cell range
	 * @param row2
	 *            last row of the target cell range
	 * @param contents
	 * @return
	 */
	public boolean paste(int column1, int row1, int column2, int row2,
			Transferable contents) {

		boolean succ = false;
		boolean isCSV = false;
		String transferString = null;

		// extract a String from the Transferable contents
		transferString = DataImport.convertTransferableToString(contents);
		if (transferString == null)
			return false;

		// isCSV = DataImport.hasHTMLFlavor(contents);
		// System.out.println("transfer string: " + transferString);

		// test if the transfer string is the same as the internal cell copy
		// string. If true, then we have a tab-delimited list of cell geos and
		// can paste them with relative cell references
		boolean doInternalPaste = cellBufferStr != null
				&& transferString.equals(cellBufferStr);

		if (doInternalPaste && cellBufferGeo != null) {

			// use the internal field cellBufferGeo to paste geo copies
			// with relative cell references
			succ = pasteInternalMultiple(column1, row1, column2, row2);

		} else {

			// use the transferString data to create and paste new geos
			// into the target cells without relative cell references

			String[][] data = DataImport.parseExternalData(app, transferString,
					isCSV);
			succ = pasteExternalMultiple(data, column1, row1, column2, row2);

			// Application.debug("newline index "+buf.indexOf("\n"));
			// Application.debug("length "+buf.length());

		}

		return succ;
	}

	/**
	 * Copies geos from the field cellBufferGeo and then pastes (renames) them
	 * into the given target cell range using relative cell references in their
	 * definitions. The data may be pasted multiple times to fill in the target
	 * rectangle (and maybe overflow a bit)
	 * 
	 * @param column1
	 *            first column of the target cell range
	 * @param row1
	 *            first row of the target cell range
	 * @param column2
	 *            last column of the target cell range
	 * @param row2
	 *            last row of the target cell range
	 * @return
	 */
	public boolean pasteInternalMultiple(int column1, int row1, int column2,
			int row2) {
		boolean succ = true;
		Construction cons = kernel.getConstruction();
		try {

			int columnStep = cellBufferGeo.length;
			int rowStep = cellBufferGeo[0].length;
			int maxColumn = column2;
			int maxRow = row2;

			// paste all data if just one cell selected
			// ie overflow selection rectangle
			if (row2 == row1 && column2 == column1) {
				maxColumn = column1 + columnStep;
				maxRow = row1 + rowStep;
			}

			// collect all redefine operations
			cons.startCollectingRedefineCalls();

			// paste data multiple times to fill in the selection rectangle (and
			// maybe overflow a bit)
			for (int c = column1; c <= column2; c += columnStep)
				for (int r = row1; r <= row2; r += rowStep)
					succ = succ && pasteInternal(c, r, maxColumn, maxRow);

			// now do all redefining and build new construction
			cons.processCollectedRedefineCalls();

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			app.showError(ex.getMessage());

		} finally {
			cons.stopCollectingRedefineCalls();
			((Application) kernel.getApplication()).setDefaultCursor();
		}

		return succ;
	}

	/**
	 * Creates copies of the geos stored in the global field cellBufferGeo. The
	 * copied values are named as spreadsheet cells corresponding to the given
	 * target cell range and the original source cell locations. Relative cell
	 * references are then applied to match the location of these new geos.
	 * 
	 * The target cell range is defined by upper left corner (column1, row1) and
	 * lower right corner (maxColumn, maxRow).
	 * 
	 * @param column1
	 *            minimum target column
	 * @param row1
	 *            minimum target row
	 * @param maxColumn
	 *            maximum target column
	 * @param maxRow
	 *            maximum target row
	 * @return
	 * @throws Exception
	 */
	public boolean pasteInternal(int column1, int row1, int maxColumn,
			int maxRow) throws Exception {
		int width = cellBufferGeo.length;
		if (width == 0)
			return false;
		int height = cellBufferGeo[0].length;
		if (height == 0)
			return false;

		app.setWaitCursor();
		boolean succ = false;

		// Application.debug("height = " + height+" width = "+width);
		int x1 = sourceColumn1;
		int y1 = sourceRow1;
		int x2 = sourceColumn1 + width - 1;
		int y2 = sourceRow1 + height - 1;
		int x3 = column1;
		int y3 = row1;
		int x4 = column1 + width - 1;
		int y4 = row1 + height - 1;
		GeoElement[][] values2 = RelativeCopy.getValues(app, x3, y3, x4, y4);
		/*
		 * for (int i = 0; i < values2.length; ++ i) { for (int j = 0; j <
		 * values2[i].length; ++ j) { if (values2[i][j] != null) {
		 * values2[i][j].remove(); values2[i][j] = null; } } } /*
		 */

		int size = (x2 - x1 + 1) * (y2 - y1 + 1);
		if (constructionIndexes == null || constructionIndexes.length < size)
			constructionIndexes = new Object[size];

		int count = 0;

		// ensure the table is large enough to contain the new data
		if (tableModel.getRowCount() < y4 + 1) {
			tableModel.setRowCount(y4 + 1);
		}
		if (tableModel.getColumnCount() < x4 + 1) {
			getTable().setMyColumnCount(x4 + 1);
		}

		GeoElement[][] values1 = cellBufferGeo;// RelativeCopy.getValues(table,
												// x1, y1, x2, y2);
		try {
			for (int x = x1; x <= x2; ++x) {
				int ix = x - x1;
				for (int y = y1; y <= y2; ++y) {
					int iy = y - y1;

					// check if we're pasting back into what we're copying from
					boolean inSource = x + (x3 - x1) <= x2
							&& x + (x3 - x1) >= x1 && y + (y3 - y1) <= y2
							&& y + (y3 - y1) >= y1;

					// Application.debug("x1="+x1+" x2="+x2+" x3="+x3+" x4="+x4+" x="+x+" ix="+ix);
					// Application.debug("y1="+y1+" y2="+y2+" y3="+y3+" y4="+y4+" y="+y+" iy="+iy);

					if (ix + column1 <= maxColumn && iy + row1 <= maxRow// ) {
																		// //
																		// check
																		// not
																		// outside
																		// selection
																		// rectangle
							&& (!inSource)) { // check we're not pasting over
												// what we're copying

						if (values1[ix][iy] != null) {

							// just record the coordinates for pasting
							constructionIndexes[count] = new Record(
									values1[ix][iy].getConstructionIndex(), ix,
									iy, x3 - x1, y3 - y1);
							count++;
						}
						// values2[ix][iy] =
						// RelativeCopy.doCopyNoStoringUndoInfo0(kernel, table,
						// values1[ix][iy], values2[ix][iy], x3 - x1, y3 - y1);
						// if (values1[ix][iy] != null && values2[ix][iy] !=
						// null)
						// values2[ix][iy].setAllVisualProperties(values1[ix][iy]);
					}
				}
			}

			// sort according to the construction index
			// so that objects are pasted in the correct order
			Arrays.sort(constructionIndexes, 0, count, getComparator());

			// do the pasting
			for (int i = 0; i < count; i++) {
				Record r = (Record) constructionIndexes[i];
				int ix = r.getx1();
				int iy = r.gety1();
				values2[ix][iy] = RelativeCopy.doCopyNoStoringUndoInfo0(kernel,
						app, values1[ix][iy], values2[ix][iy], r.getx2(),
						r.gety2());

			}

			succ = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			app.setDefaultCursor();
		}

		return succ;
	}

	/**
	 * Pastes data from 2D String array into a given cell range. The data may be
	 * pasted multiple times to fill in an oversized target rectangle (and maybe
	 * overflow a bit).
	 * 
	 * @param data
	 * @param column1
	 *            minimum target column
	 * @param row1
	 *            minimum target row
	 * @param column2
	 *            maximum target column
	 * @param row2
	 *            maximum target row
	 * @return
	 */
	private boolean pasteExternalMultiple(String[][] data, CellRange cr) {
		return pasteExternalMultiple(data, cr.getMinColumn(), cr.getMinRow(),
				cr.getMaxColumn(), cr.getMaxRow());
	}

	/**
	 * Pastes data from 2D String array into a given set of cells. The data may
	 * be pasted multiple times to fill in an oversized target rectangle (and
	 * maybe overflow a bit).
	 * 
	 * @param data
	 * @param column1
	 *            minimum target column
	 * @param row1
	 *            minimum target row
	 * @param column2
	 *            maximum target column
	 * @param row2
	 *            maximum target row
	 * @return
	 */
	private boolean pasteExternalMultiple(String[][] data, int column1,
			int row1, int column2, int row2) {

		boolean oldEqualsSetting = app.getSettings().getSpreadsheet()
				.equalsRequired();
		app.getSettings().getSpreadsheet().setEqualsRequired(true);

		boolean succ = true;
		int rowStep = data.length;
		int columnStep = data[0].length;

		if (columnStep == 0)
			return false;

		int maxColumn = column2;
		int maxRow = row2;

		// paste all data if just one cell selected
		// ie overflow selection rectangle
		if (row2 == row1 && column2 == column1) {
			maxColumn = column1 + columnStep;
			maxRow = row1 + rowStep;
		}

		// paste data multiple times to fill in the selection rectangle (and
		// maybe overflow a bit)
		for (int c = column1; c <= column2; c += columnStep)
			for (int r = row1; r <= row2; r += rowStep)
				succ = succ && pasteExternal(data, c, r, maxColumn, maxRow);

		app.getSettings().getSpreadsheet().setEqualsRequired(oldEqualsSetting);

		return succ;
	}

	/**
	 * Creates new cell geos using the string values stored in the given
	 * String[][]. Cells are named to correspond with the target cell range
	 * defined by upper left corner (column1, row1) and lower right corner
	 * (maxColumn, maxRow). Does not apply relative cell references.
	 * 
	 * @param data
	 * @param column1
	 * @param row1
	 * @param maxColumn
	 * @param maxRow
	 * @return
	 */
	public boolean pasteExternal(String[][] data, int column1, int row1,
			int maxColumn, int maxRow) {
		app.setWaitCursor();
		boolean succ = false;

		try {
			if (tableModel.getRowCount() < row1 + data.length) {
				tableModel.setRowCount(row1 + data.length);
			}
			GeoElement[][] values2 = new GeoElement[data.length][];
			int maxLen = -1;
			for (int row = row1; row < row1 + data.length; ++row) {
				if (row < 0 || row > maxRow)
					continue;
				int iy = row - row1;
				values2[iy] = new GeoElement[data[iy].length];
				if (maxLen < data[iy].length)
					maxLen = data[iy].length;
				if (tableModel.getColumnCount() < column1 + data[iy].length) {
					getTable().setMyColumnCount(column1 + data[iy].length);
				}
				for (int column = column1; column < column1 + data[iy].length; ++column) {
					if (column < 0 || column > maxColumn)
						continue;
					int ix = column - column1;
					// Application.debug(iy + " " + ix + " [" + data[iy][ix] +
					// "]");
					if (data[iy][ix] == null)
						continue;
					data[iy][ix] = data[iy][ix].trim();
					if (data[iy][ix].length() == 0) {
						GeoElement value0 = RelativeCopy.getValue(app, column,
								row);
						if (value0 != null) {
							// Application.debug(value0.toValueString());
							// MyCellEditor.prepareAddingValueToTable(kernel,
							// table, null, value0, column, row);
							// value0.remove();
							value0.removeOrSetUndefinedIfHasFixedDescendent();
						}
					} else {
						GeoElement value0 = RelativeCopy.getValue(app, column,
								row);
						values2[iy][ix] = RelativeCopy
								.prepareAddingValueToTableNoStoringUndoInfo(
										kernel, app, data[iy][ix], value0,
										column, row);
						// values2[iy][ix].setAuxiliaryObject(values2[iy][ix].isGeoNumeric());
						values2[iy][ix].setAuxiliaryObject(true);
						tableModel.setValueAt(values2[iy][ix], row, column);
					}
				}
			}
			// Application.debug("maxLen=" + maxLen);
			app.repaintSpreadsheet();

			/*
			 * if (values2.length == 1 || maxLen == 1) {
			 * createPointsAndAList1(values2); } if (values2.length == 2 ||
			 * maxLen == 2) { createPointsAndAList2(values2); }
			 */

			succ = true;
		} catch (Exception ex) {
			// app.showError(ex.getMessage());
			// Util.handleException(table, ex);
			ex.printStackTrace();
		} finally {
			app.setDefaultCursor();
		}

		return succ;
	}

	public boolean delete(int column1, int row1, int column2, int row2) {

		return delete(app, column1, row1, column2, row2, getTable()
				.getSelectionType());
	}

	public void deleteAll() {
		delete(0, 0, tableModel.getColumnCount(), tableModel.getRowCount());
	}

	public static boolean delete(AbstractApplication app, int column1,
			int row1, int column2, int row2, int selectionType) {
		boolean succ = false;
		for (int column = column1; column <= column2; ++column) {
			for (int row = row1; row <= row2; ++row) {
				GeoElement value0 = RelativeCopy.getValue(app, column, row);
				if (value0 != null && !value0.isFixed()) {
					value0.removeOrSetUndefinedIfHasFixedDescendent();
					succ = true;
				}
			}
		}

		// Let the trace manager know about the delete
		// TODO add SelectAll
		if (selectionType == MyTable.COLUMN_SELECT) {
			app.getTraceManager().handleColumnDelete(column1, column2);
		}

		return succ;
	}

	private static class Record {
		int id, x1, y1, x2, y2;

		public Record(int id, int x1, int y1, int x2, int y2) {
			this.id = id;
			this.x1 = x1;
			this.x2 = x2;
			this.y1 = y1;
			this.y2 = y2;
		}

		public int getId() {
			return id;
		}

		public int getx1() {
			return x1;
		}

		public int getx2() {
			return x2;
		}

		public int gety1() {
			return y1;
		}

		public int gety2() {
			return y2;
		}

		public int compareTo(Object o) {
			AbstractApplication.debug(o.getClass() + "");
			// int id = ((Record) o).getId();
			// return id - this.id;
			return 0;
		}
	}

	/**
	 * used to sort Records based on the id (which is the construction index)
	 */
	public static Comparator getComparator() {
		if (comparator == null) {
			comparator = new Comparator() {
				public int compare(Object a, Object b) {
					Record itemA = (Record) a;
					Record itemB = (Record) b;

					return itemA.id - itemB.id;
				}

			};

		}

		return comparator;
	}

	private static Comparator comparator;

	// ====================================================
	// File and URL
	// ====================================================

	// default pasteFromFile: clear spreadsheet and then paste from upper left
	// corner
	public boolean pasteFromURL(URL url) {

		CellRange cr = new CellRange(app, 0, 0, 0, 0);
		return pasteFromURL(url, cr, true);

	}

	public boolean pasteFromURL(URL url, CellRange targetRange,
			boolean clearSpreadsheet) {

		// read file
		StringBuilder contents = new StringBuilder();

		boolean oldEqualsSetting = app.getSettings().getSpreadsheet()
				.equalsRequired();
		app.getSettings().getSpreadsheet().setEqualsRequired(true);

		boolean isCSV = getExtension(url.getFile()).equals("csv");

		try {
			InputStream is = url.openStream();
			BufferedReader input = new BufferedReader(new InputStreamReader(is));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			} finally {
				input.close();

			}
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}

		// System.out.println(dataFile.getName() + ": " + contents.capacity());

		boolean succ = true;

		String[][] data = DataImport.parseExternalData(app,
				contents.toString(), isCSV);

		if (data != null) {
			if (clearSpreadsheet)
				deleteAll();
			succ = pasteExternalMultiple(data, targetRange);
		} else {
			succ = false;
		}

		app.getSettings().getSpreadsheet().setEqualsRequired(oldEqualsSetting);

		return succ;

	}

	/**
	 * Return the extension portion of the file's name.
	 * 
	 * @param f
	 * @return "ext" for file "filename.ext"
	 */
	private static String getExtension(String filename) {
		if (filename != null) {
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1)
				return filename.substring(i + 1).toLowerCase(Locale.US);
		}
		return null;
	}

}
