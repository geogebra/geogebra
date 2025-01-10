package org.geogebra.common.gui.view.spreadsheet;

import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.spreadsheet.core.CopyPasteCutTabularData;
import org.geogebra.common.spreadsheet.core.SelectionType;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;

public abstract class CopyPasteCut {

	protected final CopyPasteAdapter adapter;
	// ggb support classes
	@Weak
	protected Kernel kernel;
	@Weak
	protected App app;
	private SpreadsheetTableModel tableModel;

	private SpreadsheetViewInterface view;
	private MyTableInterface table;

	/**
	 * Stores copied cell geo values as a tab-delimited string.
	 */
	private String cellBufferStr;

	/**
	 * Stores copied cell geos as GeoElement[columns][rows]
	 */
	private GeoElement[][] cellBufferGeo;

	/**
	 * Records the first row of the current cell range copy source
	 */
	protected int sourceColumn1;
	/**
	 * Records the first column of the current cell range copy source
	 */
	protected int sourceRow1;

	/**
	 * Stores construction index values while performing a paste
	 */
	private Record[] constructionIndexes;
	private static Comparator<Record> comparator;

	/**
	 * Constructor
	 * 
	 * @param app
	 *            application
	 */
	public CopyPasteCut(App app) {
		tableModel = app.getSpreadsheetTableModel();
		this.app = app;
		kernel = app.getKernel();
		adapter = new CopyPasteAdapter(app, tableModel);
	}

	private SpreadsheetViewInterface getView() {
		if (view == null) {
			view = app.getGuiManager()
					.getSpreadsheetView();
		}

		return view;
	}

	protected MyTableInterface getTable() {
		if (table == null) {
			table = getView().getSpreadsheetTable();
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
	 *            min column
	 * @param row1
	 *            min row
	 * @param column2
	 *            max column
	 * @param row2
	 *            max row
	 * @param skipGeoCopy
	 *            whether to skip copy to internal buffer
	 */
	abstract public void copy(int column1, int row1, int column2, int row2,
			boolean skipGeoCopy);

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
	 *            min column
	 * @param row1
	 *            min row
	 * @param column2
	 *            max column
	 * @param row2
	 *            max row
	 * @return if at least one object was deleted
	 */
	public boolean cut(int column1, int row1, int column2, int row2) {

		copy(column1, row1, column2, row2, false);
		// null out the external buffer so that paste will not do a relative
		// copy
		resetCellBuffer();
		return delete(column1, row1, column2, row2);
	}

	/**
	 * Pastes data from the clipboard into the given spreadsheet cell range.
	 * 
	 * @param cr
	 *            the target cell range
	 * @return true if successful
	 */
	public boolean paste(TabularRange cr) {
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
	 * @return true if successful
	 */
	abstract public boolean paste(int column1, int row1, int column2, int row2);

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
	 * @return true if successful
	 */
	public boolean pasteInternalMultiple(int column1, int row1, int column2,
			int row2) {
		boolean succ = true;
		Construction cons = kernel.getConstruction();
		try {

			int columnStep = getCellBufferGeo().length;
			int rowStep = getCellBufferGeo()[0].length;
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
			for (int c = column1; c <= column2; c += columnStep) {
				for (int r = row1; r <= row2; r += rowStep) {
					succ = succ && pasteInternal(c, r, maxColumn, maxRow);
				}
			}

			// now do all redefining and build new construction
			cons.processCollectedRedefineCalls();

		} catch (Exception ex) {
			app.showGenericError(ex);

		} finally {
			cons.stopCollectingRedefineCalls();
			app.setDefaultCursor();
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
	 * @return true if successful
	 */
	public boolean pasteInternal(int column1, int row1, int maxColumn,
			int maxRow) {
		int width = getCellBufferGeo().length;
		if (width == 0) {
			return false;
		}
		int height = getCellBufferGeo()[0].length;
		if (height == 0) {
			return false;
		}

		app.setWaitCursor();
		boolean succ = false;
		int x1 = sourceColumn1;
		int y1 = sourceRow1;
		int x2 = sourceColumn1 + width - 1;
		int y2 = sourceRow1 + height - 1;
		int x3 = column1;
		int y3 = row1;
		int x4 = column1 + width - 1;
		int y4 = row1 + height - 1;
		GeoElementND[][] values2 = RelativeCopy.getValues(app, x3, y3, x4, y4);
		/*
		 * for (int i = 0; i < values2.length; ++ i) { for (int j = 0; j <
		 * values2[i].length; ++ j) { if (values2[i][j] != null) {
		 * values2[i][j].remove(); values2[i][j] = null; } } } /*
		 */

		int size = (x2 - x1 + 1) * (y2 - y1 + 1);
		if (constructionIndexes == null || constructionIndexes.length < size) {
			constructionIndexes = new Record[size];
		}

		int count = 0;

		// ensure the table is large enough to contain the new data
		if (tableModel.getRowCount() < y4 + 1) {
			tableModel.setRowCount(y4 + 1);
		}
		if (tableModel.getColumnCount() < x4 + 1) {
			tableModel.setColumnCount(x4 + 1);
		}

		GeoElement[][] values1 = getCellBufferGeo();
		try {
			for (int x = x1; x <= x2; ++x) {
				int relX = x - x1;
				for (int y = y1; y <= y2; ++y) {
					int relY = y - y1;

					// check if we're pasting back into what we're copying from
					boolean inSource = x + (x3 - x1) <= x2
							&& x + (x3 - x1) >= x1 && y + (y3 - y1) <= y2
							&& y + (y3 - y1) >= y1;

					if (relX + column1 <= maxColumn && relY + row1 <= maxRow// ) {
																		// //
																		// check
																		// not
																		// outside
																		// selection
																		// rectangle
							&& (!inSource)) { // check we're not pasting over
												// what we're copying

						if (values1[relX][relY] != null) {

							// just record the coordinates for pasting
							constructionIndexes[count] = new Record(
									values1[relX][relY].getConstructionIndex(), relX,
									relY, x3 - x1, y3 - y1);
							count++;
						}
						// values2[relX][relY] =
						// RelativeCopy.doCopyNoStoringUndoInfo0(kernel, table,
						// values1[relX][relY], values2[relX][relY], x3 - x1, y3 - y1);
						// if (values1[relX][relY] != null && values2[relX][relY] !=
						// null)
						// values2[relX][relY].setAllVisualProperties(values1[relX][relY]);
					}
				}
			}

			// sort according to the construction index
			// so that objects are pasted in the correct order
			Arrays.sort(constructionIndexes, 0, count, getComparator());
			RelativeCopy relativeCopy = new RelativeCopy(kernel);
			// do the pasting
			for (int i = 0; i < count; i++) {
				Record r = constructionIndexes[i];
				int relX = r.getx1();
				int relY = r.gety1();
				values2[relX][relY] = relativeCopy.doCopyNoStoringUndoInfo0(values1[relX][relY],
						values2[relX][relY], r.getx2(), r.gety2());

			}

			succ = true;
		} catch (CircularDefinitionException | ParseException | RuntimeException e) {
			Log.debug(e);
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
	 *            data
	 * @param destination
	 *            cell range
	 * @return whether all cells were pasted successfully
	 */
	protected boolean pasteExternalMultiple(String[][] data, TabularRange destination) {
		boolean oldEqualsSetting = app.getSettings().getSpreadsheet()
				.equalsRequired();
		app.getSettings().getSpreadsheet().setEqualsRequired(true);
		TabularRange tiledRange = CopyPasteCutTabularData.getTiledRange(destination, data);
		boolean success = tiledRange != null
				&& adapter.pasteExternalMultiple(data, tiledRange);
		app.getSettings().getSpreadsheet().setEqualsRequired(oldEqualsSetting);
		return success;
	}

	/**
	 * Creates new cell geos using the string values stored in the given
	 * String[][]. Cells are named to correspond with the target cell range
	 * defined by upper left corner (column1, row1) and lower right corner
	 * (maxColumn, maxRow). Does not apply relative cell references.
	 * 
	 * @param data
	 *            tabular data
	 * @param column1
	 *            min column
	 * @param row1
	 *            min row
	 * @param maxColumn
	 *            max column
	 * @param maxRow
	 *            max row
	 * @return whether paste was successful
	 */
	public boolean pasteExternal(String[][] data, int column1, int row1,
			int maxColumn, int maxRow) {
		return adapter.pasteExternal(data, column1, row1, maxColumn, maxRow);
	}

	/**
	 * @param column1
	 *            start column
	 * @param row1
	 *            start row
	 * @param column2
	 *            end column
	 * @param row2
	 *            end row
	 * @return if at least one object was deleted
	 */
	public boolean delete(int column1, int row1, int column2, int row2) {
		return delete(app, column1, row1, column2, row2,
				getTable().getSelectionType());
	}

	public void deleteAll() {
		delete(0, 0, tableModel.getColumnCount(), tableModel.getRowCount());
	}

	/**
	 * @param app
	 *            application
	 * @param column1
	 *            start column
	 * @param row1
	 *            start row
	 * @param column2
	 *            end column
	 * @param row2
	 *            end row
	 * @param selectionType
	 *            selection type
	 * @return if at least one object was deleted
	 */
	public static boolean delete(App app, int column1, int row1, int column2,
			int row2, SelectionType selectionType) {
		boolean succ = false;
		TreeSet<GeoElement> toRemove = new TreeSet<>();
		for (int column = column1; column <= column2; ++column) {
			for (int row = row1; row <= row2; ++row) {
				GeoElement value0 = RelativeCopy.getValue(app, column, row);
				if (value0 != null && !value0.isProtected(EventType.REMOVE)) {
					toRemove.add(value0);
				}
			}
		}
		app.getKernel().setSpreadsheetBatchRunning(true);
		int size = toRemove.size();
		for (int i = 0; i < size; i++) {
			toRemove.last().removeOrSetUndefinedIfHasFixedDescendent();
			succ = true;
			toRemove.remove(toRemove.last());
		}
		app.getKernel().setSpreadsheetBatchRunning(false);

		// Let the trace manager know about the delete
		// TODO add SelectAll
		if (selectionType == SelectionType.COLUMNS) {
			app.getTraceManager().handleColumnDelete(column1, column2);
		} else {
			app.getTraceManager().handleColumnDelete(column1, row1, column2,
					row2);
		}

		if (succ) {
			app.getKernel().notifyRepaint();
		}

		return succ;
	}

	private static class Record {
		int id;
		int x1;
		int y1;
		int x2;
		int y2;

		public Record(int id, int x1, int y1, int x2, int y2) {
			this.id = id;
			this.x1 = x1;
			this.x2 = x2;
			this.y1 = y1;
			this.y2 = y2;
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
	}

	/**
	 * Get a rectangular area of the spreadsheet as tab separated values.
	 * The value is also stored into the buffer.
	 * @param column1 left column
	 * @param row1 top row
	 * @param column2 right column
	 * @param row2 bottom row
	 * @return spreadsheet data in TSV format
	 */
	public String copyStringToBuffer(int column1, int row1, int column2, int row2) {
		String copyString = copyString(column1, row1, column2, row2);
		cellBufferStr = copyString;
		return copyString;
	}

	/**
	 * Just copying the selection as string text format
	 *
	 * @return selection content as tab separated string
	 */
	public String copyString(int column1, int row1, int column2, int row2) {
		StringBuilder cellBufferStrLoc = new StringBuilder();
		StringTemplate preciseTemplate = StringTemplate.maxPrecision;
		for (int row = row1; row <= row2; ++row) {
			for (int column = column1; column <= column2; ++column) {
				GeoElement value = RelativeCopy.getValue(app, column, row);
				if (value != null) {
					String valueString = value
							.toValueString(preciseTemplate);

					valueString = removeTrailingZeros(valueString);

					cellBufferStrLoc.append(valueString);
				}
				if (column != column2) {
					cellBufferStrLoc.append('\t');
				}
			}
			if (row != row2) {
				cellBufferStrLoc.append('\n');
			}
		}
		return cellBufferStrLoc.toString();
	}

	private String removeTrailingZeros(String valueString) {
		int indx = valueString
				.indexOf(app.getKernel().getLocalization().getDecimalPoint());
		if (indx > -1) {
			int end = valueString.length() - 1;
			// only in this case, we should remove trailing zeroes!
			while (valueString.charAt(end) == '0') {
				end--;
			}
			if (end == indx) {
				end--;
			}
			return valueString.substring(0, end + 1);
		}
		return valueString;
	}

	/**
	 * used to sort Records based on the id (which is the construction index)
	 * 
	 * @return comparator
	 */
	public static Comparator<Record> getComparator() {
		if (comparator == null) {
			comparator = new Comparator<Record>() {
				@Override
				public int compare(Record a, Record b) {
					return a.id - b.id;
				}

			};

		}

		return comparator;
	}

	/**
	 * @return copied cell geo values as a tab-delimited string.
	 */
	protected boolean isCellBuffer(String content) {
		return content.equals(cellBufferStr);
	}

	/**
	 * Reset cell buffer to null
	 */
	protected void resetCellBuffer() {
		this.cellBufferStr = null;
	}

	protected GeoElement[][] getCellBufferGeo() {
		return cellBufferGeo;
	}

	protected void setCellBufferGeo(GeoElement[][] cellBufferGeo) {
		this.cellBufferGeo = cellBufferGeo;
	}
}
