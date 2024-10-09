/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import java.util.ArrayList;
import java.util.Iterator;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoListForCellRange;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.common.spreadsheet.core.TabularRange;

/**
 * Algorithm to create a GeoList with GeoElement objects of a given range in
 * GeoGebra's spreadsheet. For example, CellRange[A1, B2] (or A1:B2) returns the
 * list {A1, B1, A2, B2}.
 * 
 * @author Markus Hohenwarter
 * @since 29.06.2008
 */
public class AlgoCellRange extends AlgoElement {

	private GeoListForCellRange geoList; // output list of range
	private String startCell; // start cell name
	private String endCell; // end cell name
	private String toStringOutput;

	private TabularRange tabularRange;
	private ArrayList<GeoElement> listItems;
	private SpreadsheetCoords startCoords;
	private SpreadsheetCoords endCoords;
	/**
	 * max column location for existing values
	 */
	private int maxExistingCol;
	/**
	 * max row location for existing values in max column location
	 */
	private int maxExistingRow;

	/**
	 * Creates an algorithm that produces a list of GeoElements for a range of
	 * cells in the spreadsheet.
	 * 
	 * @param startCell
	 *            e.g. A1
	 * @param endCell
	 *            e.g. B2
	 */
	public AlgoCellRange(Construction cons, String label, String startCell,
			String endCell) {
		super(cons);
		this.startCell = startCell;
		this.endCell = endCell;
		setInputOutput();
		geoList.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		// rather than writing <command name="CellRange"> <input a0="B2" a1="B3"
		// a2="B4" etc />
		// write an expression: <expression label="list1" exp="A1:A3" />
		return Algos.Expression;
	}

	@Override
	public void remove() {
		if (removed) {
			return;
		}

		// don't remove if there is some child
		if (geoList.hasChildren()) {
			return;
		}
		// remove this from item update sets
		for (GeoElement geo : listItems) {
			geo.removeFromUpdateSets(this);
		}

		super.remove();

		cons.getApplication().getSpreadsheetTableModel().getCellRangeManager()
				.unregisterCellRangeListenerAlgo(this);

		clearGeoList();
	}

	private void clearGeoList() {
		geoList.clear();
	}

	/**
	 * update list (add/remove geo)
	 * 
	 * @param geo
	 *            geo to add/remove
	 * @param isRemoveAction
	 *            true if remove, false if add
	 */
	public void updateList(GeoElement geo, boolean isRemoveAction) {

		if (listItems.contains(geo)) {
			// exit if geo is already in the list
			if (!isRemoveAction) {
				return;
			}
			listItems.remove(geo);

		} else {
			listItems = initCellRangeList(startCoords, endCoords);
		}

		updateList();
		update();
		geoList.updateRepaint();
	}

	private void updateList() {
		geoList.clear();
		for (GeoElement geo : listItems) {
			add(geo);
		}
	}

	private void add(GeoElement geo) {

		// add to geo list
		geoList.add(geo);

		// add this to geo update set
		geo.addToUpdateSetOnly(this);

		// add to list update set to geo update set
		Iterator<AlgoElement> it = geoList.getAlgoUpdateSet().getIterator();
		while (it.hasNext()) {
			geo.addToUpdateSetOnly(it.next());
		}

	}

	/**
	 * add geo at location into the list
	 * 
	 * @param geo
	 *            element
	 * @param loc
	 *            location on spreadsheet
	 */
	public void addToList(GeoElement geo, SpreadsheetCoords loc) {

		// check if we just add at the end of the list
		if (loc.column >= maxExistingCol && loc.row > maxExistingRow) {
			maxExistingCol = loc.column;
			maxExistingRow = loc.row;
			addToList(geo);
		} else { // recompute the list
			updateList(geo, false);
		}
	}

	private void addToList(GeoElement geo) {

		listItems.add(geo);

		add(geo);

		geoList.updateRepaint();

	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		startCoords = GeoElementSpreadsheet
				.getSpreadsheetCoordsForLabel(startCell);
		endCoords = GeoElementSpreadsheet.getSpreadsheetCoordsForLabel(endCell);
		toStringOutput = startCell + ":" + endCell;

		tabularRange = new TabularRange(startCoords.row, startCoords.column,
				endCoords.row, endCoords.column);

		// build list with cells in range
		listItems = initCellRangeList(startCoords, endCoords);

		// create dependent geoList for cells in range
		geoList = new GeoListForCellRange(cons, this);

		// input: size 0
		// needed for XML saving only
		input = new GeoElement[0];

		updateList();
		update();

		setOnlyOutput(geoList);

		setDependenciesOutputOnly();

		// see this.getClassName() for better solution
		// change input now for XML saving
		// input = new GeoElement[2];
		// input[0] = startCell;
		// input[1] = endCell;
	}

	/**
	 * Builds geoList with current objects in range of spreadsheet. Renaming of
	 * all cells added to the geoList is turned off, otherwise the user could
	 * move an object out of the range by renaming it.
	 * 
	 * @param rangeStart
	 *            range start point
	 * @param rangeEnd
	 *            range end point
	 */
	private ArrayList<GeoElement> initCellRangeList(SpreadsheetCoords rangeStart,
			SpreadsheetCoords rangeEnd) {
		ArrayList<GeoElement> listItems1 = new ArrayList<>();

		// check if we have valid spreadsheet coordinates
		boolean validRange = rangeStart != null && rangeEnd != null;
		if (!validRange) {
			return listItems1;
		}

		// min and max column and row of range
		int minCol = Math.min(rangeStart.column, rangeEnd.column);
		int maxCol = Math.max(rangeStart.column, rangeEnd.column);
		int minRow = Math.min(rangeStart.row, rangeEnd.row);
		int maxRow = Math.max(rangeStart.row, rangeEnd.row);

		maxExistingCol = minCol - 1;
		maxExistingRow = minRow - 1;

		// build the list
		for (int colIndex = minCol; colIndex <= maxCol; colIndex++) {
			for (int rowIndex = minRow; rowIndex <= maxRow; rowIndex++) {
				// get cell object for col, row
				String cellLabel = GeoElementSpreadsheet
						.getSpreadsheetCellName(colIndex, rowIndex);
				GeoElement geo = kernel.lookupLabel(cellLabel);

				// create missing object in cell range
				if (geo == null || geo.isEmptySpreadsheetCell()) {
					// geo = cons
					// .createSpreadsheetGeoElement(startCell, cellLabel);
					continue;
				}

				// we got the cell object, add it to the list
				listItems1.add(geo);
				maxExistingCol = colIndex;
				maxExistingRow = rowIndex; // we want max existing row in max
											// col

				// make sure that this cell object cannot be renamed by the user
				// renaming would move the object outside of our range
				// geo.addCellRangeUser();
			}
		}

		return listItems1;
	}

	public GeoList getList() {
		return geoList;
	}

	public TabularRange getRange() {
		return tabularRange;
	}

	@Override
	public final void compute() {
		// just update list
		geoList.update();
	}

	@Override
	final public String getDefinition(StringTemplate tpl) {
		return getStringOutput(tpl);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getStringOutput(tpl);
	}

	private String getStringOutput(StringTemplate tpl) {
		if (StringTemplate.xmlTemplate.equals(tpl) && geoList.getTypeStringForXML() != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("CellRange(");
			sb.append(startCell);
			sb.append(",");
			sb.append(endCell);
			sb.append(",\"");
			sb.append(geoList.getTypeStringForXML());
			sb.append("\")");
			return sb.toString();
		}
		return toStringOutput;
	}

	/**
	 * @return {start point, end point}
	 */
	public SpreadsheetCoords[] getRectangle() {
		SpreadsheetCoords startCoords1 = GeoElementSpreadsheet
				.getSpreadsheetCoordsForLabel(startCell);
		SpreadsheetCoords endCoords1 = GeoElementSpreadsheet
				.getSpreadsheetCoordsForLabel(endCell);

		SpreadsheetCoords[] ret = { startCoords1, endCoords1 };
		return ret;
	}

	/**
	 * add algo to input items update sets
	 * 
	 * @param algo
	 *            algorithm
	 */
	public void addToItemsAlgoUpdateSets(AlgoElement algo) {
		for (GeoElement geo : listItems) {
			geo.addToUpdateSetOnly(algo);
		}
	}

	public String getStart() {
		return startCell;
	}

	public String getEnd() {
		return endCell;
	}

}
