package geogebra.common.gui.view.spreadsheet;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.SpreadsheetTraceable;
import geogebra.common.main.App;
import geogebra.common.plugin.GeoClass;
import geogebra.common.util.SpreadsheetTraceSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * This class manages tracing of GeoElements to the spreadsheet. A trace is a
 * spreadsheet cell, or set of cells in the same row, that holds the numeric
 * value(s) of a GeoElement.
 * 
 * Specifically, the class:
 * 
 * (1) maintains TraceGeoCollection, a hash table that matches all GeoElements
 * that trace to the spreadsheet with their trace settings
 * 
 * (2) creates and updates spreadsheet cell traces based on type of geo (e.g.
 * angles, polar points, lists)
 * 
 * (3) determines the column/row location for a geo trace
 * 
 * 
 * @author G. Sturr 2010-4-22
 * 
 */

public class SpreadsheetTraceManager {

	// external components
	private App app;
	private Kernel kernel;


	// collection of all geos currently traced
	protected HashMap<GeoElement, SpreadsheetTraceSettings> traceGeoCollection;

	// temporary collection of trace geos, held during construction updates
	private HashSet<GeoElement> storedTraces;
	private boolean collectingTraces = false;

	// misc variables
	private boolean doShiftCellsUp = true;
	private double[] coords = new double[3];
	private ArrayList<Double> currentTrace = new ArrayList<Double>();

	public SpreadsheetTraceManager(App app) {

		this.app = app;
		kernel = app.getKernel();

		traceGeoCollection = new HashMap<GeoElement, SpreadsheetTraceSettings>();
		storedTraces = new HashSet<GeoElement>();

	}

	// =============================================
	// Add/Remove Geo Trace
	// =============================================

	/** Add a geo to the trace collection */
	public void addSpreadsheetTraceGeo(GeoElement spreadsheetTraceable) {
		
		SpreadsheetTraceSettings t = spreadsheetTraceable.getTraceSettings();
		Construction cons = app.getKernel().getConstruction();

		// Set trace columns
		if (t.traceColumn1 == -1) {
			t.traceColumn1 = getNextTraceColumn();
		}

		if (t.doTraceGeoCopy) {
			t.traceColumn2 = t.traceColumn1;
		} else {
			ArrayList<GeoNumeric> traceList = new ArrayList<GeoNumeric>();
			spreadsheetTraceable.addToSpreadsheetTraceList(traceList);
			t.traceColumn2 = t.traceColumn1 + traceList.size() - 1;
		}

		// Set trace rows
		if (t.traceRow1 == -1) {
			t.traceRow1 = 0; // default to first row
		}

		t.headerOffset = 0;
		if (t.showLabel)
			++t.headerOffset;
		if (t.showTraceList)
			++t.headerOffset;

		if (t.doRowLimit) {
			t.traceRow2 = t.traceRow1 + t.numRows - 1 + t.headerOffset;
		} else {
			t.traceRow2 = Kernel.MAX_SPREADSHEET_ROWS;
		}

		t.tracingRow = t.traceRow1;

		t.lastTrace.clear();

		// add the geo and its settings to the hash table
		traceGeoCollection.put(spreadsheetTraceable, t);

		// set the tracing flag for this geo
		spreadsheetTraceable.setSpreadsheetTrace(true);

		// clear the trace columns and put the current trace into the
		// spreadsheet
		clearGeoTraceColumns(spreadsheetTraceable);

		if (t.showTraceList) {
			for (int column = t.traceColumn1; column <= t.traceColumn2; column++) {
				createTraceListCell(cons, column, t.traceRow1);
			}
		}

		// traceToSpreadsheet(geo);
		setHeader(spreadsheetTraceable, cons);
		app.repaintSpreadsheet();

	}

	public void updateTraceSettings(GeoElement geo) {
		if (geo==null)
			return;
		
		SpreadsheetTraceSettings t = geo.getTraceSettings();
		// clearGeoTraceColumns(geo);
		CopyPasteCut.delete(app, t.traceColumn1, t.traceRow1, t.traceColumn2,
				Kernel.MAX_SPREADSHEET_ROWS, MyTable.CELL_SELECT);
		addSpreadsheetTraceGeo(geo);
	}

	/** Remove a geo from the trace collection */
	public void removeSpreadsheetTraceGeo(GeoElement geo) {

		if (!traceGeoCollection.containsKey(geo))
			return;
		traceGeoCollection.remove(geo);
		app.repaintSpreadsheet();
	}

	/** Remove all geos from the trace collection. */
	public void removeAllSpreadsheetTraceGeos() {

		/*
		 * for(GeoElement geo:traceGeoCollection.keySet()){
		 * geo.setSpreadsheetTrace(false); }
		 */
		traceGeoCollection.clear();
		app.repaintSpreadsheet();
		// Application.printStacktrace("remove all traces ");
	}

	/**
	 * Load all tracing geos into the TraceGeoCollection. Called by
	 * SpreadsheetView after undo or load file.
	 */
	public void loadTraceGeoCollection() {
		// Application.debug("loading trace geos");
		traceGeoCollection.clear();
		TreeSet<GeoElement> ts = app.getKernel().getConstruction()
				.getGeoSetConstructionOrder();
		for (GeoElement geo : ts) {
			if (geo.getSpreadsheetTrace()) {
				traceGeoCollection.put(geo, geo.getTraceSettings());
				// System.out.println("load this geo: " + geo.toString());
			}
		}
		app.repaintSpreadsheet();
	}

	// =============================================
	// Utility Methods
	// =============================================

	public int getNextTraceColumn() {
		return Math.max(app.getSpreadsheetTableModel().getHighestUsedColumn(), getHighestTraceColumn()) + 1;
	}

	private int getHighestTraceColumn() {

		int max = -1;
		for (GeoElement geo : traceGeoCollection.keySet()) {
			if (geo.getTraceSettings().traceColumn2 > max)
				max = geo.getTraceSettings().traceColumn2;
		}
		return max;
	}

	

	public boolean isTraceGeo(GeoElement geo) {

		return traceGeoCollection.containsKey(geo);

	}

	public boolean isTraceColumn(int column) {
		SpreadsheetTraceSettings t = new SpreadsheetTraceSettings();
		for (GeoElement geo : traceGeoCollection.keySet()) {
			t = traceGeoCollection.get(geo);
			if (column >= t.traceColumn1 && column <= t.traceColumn2)
				return true;
		}

		return false;
	}

	public ArrayList<GeoElement> getTraceGeoList() {

		ArrayList<GeoElement> traceGeoList = new ArrayList<GeoElement>();
		for (GeoElement geo : traceGeoCollection.keySet()) {
			traceGeoList.add(geo);
		}
		return traceGeoList;

	}

	public GeoElement getTraceGeo(int column) {
		SpreadsheetTraceSettings t = new SpreadsheetTraceSettings();
		for (GeoElement geo : traceGeoCollection.keySet()) {
			t = traceGeoCollection.get(geo);
			if (column >= t.traceColumn1 && column <= t.traceColumn2)
				return geo;
		}

		return null;

	}

	public SpreadsheetTraceSettings getTraceSettings(GeoElement geo) {

		return traceGeoCollection.get(geo);
	}

	/**
	 * Delete the elements of all trace columns, excluding headers. This is
	 * called when a user resets the row limit.
	 */
	private void clearAllTraceColumns() {

		for (GeoElement geo : traceGeoCollection.keySet()) {
			clearGeoTraceColumns(geo);
		}
	}

	/**
	 * Delete the elements in the trace columns of a single geo.
	 */
	public void clearGeoTraceColumns(GeoElement geo) {

		SpreadsheetTraceSettings t = geo.getTraceSettings();
		if (t == null)
			return;

		CopyPasteCut.delete(app, t.traceColumn1, t.traceRow1, t.traceColumn2,
				Kernel.MAX_SPREADSHEET_ROWS, MyTable.CELL_SELECT);
		t.tracingRow = t.traceRow1;
		t.lastTrace.clear();
	}

	
	public void handleColumnDelete(int column1, int column2) {
		SpreadsheetTraceSettings t;
		for (GeoElement geo : traceGeoCollection.keySet()) {
			t = geo.getTraceSettings();
			if (column2 >= t.traceColumn1 && column1 <= t.traceColumn2) {
				Construction cons = app.getKernel().getConstruction();
				setHeader(geo, cons);
				t.tracingRow = 0;
			}
		}

		app.repaintSpreadsheet();
	}

	public SpreadsheetTraceSettings getDefaultTraceSettings() {
		return new SpreadsheetTraceSettings();
	}

	public void setNeedsColumnReset(GeoElement geo, boolean flag) {
		if (!traceGeoCollection.containsKey(geo))
			return;
		traceGeoCollection.get(geo).needsColumnReset = flag;
	}

	// =============================================
	// XML
	// =============================================

	public String getTraceXML(GeoElement geo) {
		SpreadsheetTraceSettings t = geo.getTraceSettings();
		StringBuilder sb = new StringBuilder();

		sb.append("\t<spreadsheetTrace val=\"true\"");

		sb.append(" traceColumn1=\"");
		sb.append(t.traceColumn1);
		sb.append("\"");

		sb.append(" traceColumn2=\"");
		sb.append(t.traceColumn2);
		sb.append("\"");

		sb.append(" traceRow1=\"");
		sb.append(t.traceRow1);
		sb.append("\"");

		sb.append(" traceRow2=\"");
		sb.append(t.traceRow2);
		sb.append("\"");

		sb.append(" tracingRow=\"");
		sb.append(t.tracingRow);
		sb.append("\"");

		sb.append(" numRows=\"");
		sb.append(t.numRows);
		sb.append("\"");

		sb.append(" headerOffset=\"");
		sb.append(t.headerOffset);
		sb.append("\"");

		sb.append(" doColumnReset=\"");
		sb.append(t.doColumnReset ? "true" : "false");
		sb.append("\"");

		sb.append(" doRowLimit=\"");
		sb.append(t.doRowLimit ? "true" : "false");
		sb.append("\"");

		sb.append(" showLabel=\"");
		sb.append(t.showLabel ? "true" : "false");
		sb.append("\"");

		sb.append(" showTraceList=\"");
		sb.append(t.showTraceList ? "true" : "false");
		sb.append("\"");

		sb.append(" doTraceGeoCopy=\"");
		sb.append(t.doTraceGeoCopy ? "true" : "false");
		sb.append("\"");

		sb.append("/>\n");

		/*
		 * this param is not included:
		 * 
		 * public ArrayList<Double> lastTrace = new ArrayList<Double>();
		 * 
		 * do we need it?
		 */

		return sb.toString();
	}

	// =============================================
	// Tracing
	// =============================================

	/**
	 * Turns on the collectingTraces flag and clears the storedTraces collection
	 * so that trace geos will be recorded but not traced while a construction
	 * updates.
	 */
	public void startCollectingSpreadsheetTraces() {

		collectingTraces = true;
		storedTraces.clear();
	}

	/**
	 * Turns off the collectingTraces flag after a construction has updated and
	 * then calls traceToSpreadsheet to trace all geos that have been put on
	 * hold during the update.
	 */
	public void stopCollectingSpreadsheetTraces() {

		collectingTraces = false;

		for (GeoElement geo : storedTraces) {
			traceToSpreadsheet(geo);
		}
		storedTraces.clear();
	}

	/**
	 * Trace the current value(s) of a geo into the spreadsheet.
	 * 
	 * This will either create new cells to display trace values for the geo or
	 * update old trace cells with new trace values. Placement in the
	 * spreadsheet is determined by geo.traceSettings.
	 * 
	 * Traces are displayed according to geo type. An optional header cell of
	 * type GeoText can be placed in the first tracing row. This displays the
	 * geo name, or the name of each element in a GeoList.
	 * 
	 */
	public void traceToSpreadsheet(GeoElement geo) {
		
		// stop spurious numbers after undo
		if (kernel.isViewReiniting())
			return;

		if (!traceGeoCollection.containsKey(geo))
			return;
		SpreadsheetTraceSettings t = traceGeoCollection.get(geo);
		Construction cons = app.getKernel().getConstruction();

		// TODO
		// 1) test if equals is working here
		// 2) add code to override this test if grouping of trace elements is
		// allowed
		// 3) improve selection rectangle handling

		// get the current trace for this geo 
		// TRACE ALSO IF EQUAL TO LAST TRACE
		getCurrentTrace(geo);

		t.lastTrace = (ArrayList<Double>) currentTrace.clone();

		// if only collecting traces, then record this geo for later tracing and
		// exit.
		if (collectingTraces) {
			storedTraces.add(geo);
			return;
		}

		// handle column reset
		if (t.needsColumnReset && t.doColumnReset) {
			t.traceColumn1 = getNextTraceColumn();
			t.tracingRow = t.traceRow1;
			t.needsColumnReset = false;
		}

		// allow autoscrolling to keep the current trace in view
		app.setScrollToShow(true);

		// set the headers
		/*
		if (t.tracingRow == t.traceRow1 && t.headerOffset > 0) {
			setHeader(geo, cons);
		}
		*/
		
		// 'row' is the temporary row counter actually used for creating traces.
		// 't.tracingRow' is the row counter kept in memory.
		// When row size is limited then tracingRow = -1 serves as a flag to
		// keep our row counter from going past traceRow2
		int row = t.tracingRow + t.headerOffset;
		if (t.tracingRow == -1)
			row = t.traceRow2;

		// add geo traces if we are NOT in the last row
		if (t.tracingRow != -1) {
			setGeoTraceRow(geo, cons, t.lastTrace, row);
		}

		// if in the last row, shift cells up and put new trace in last row
		else if (doShiftCellsUp) {

			GeoElement sourceCell;
			// GeoElement targetCell;

			int minTraceRow = t.traceRow1 + t.headerOffset + 1;
			if (t.numRows == 1)
				--minTraceRow;

			for (int c = t.traceColumn1; c <= t.traceColumn2; c++) {
				for (int r = minTraceRow; r <= t.traceRow2; r++) {

					// get the source cell
					sourceCell = RelativeCopy.getValue(app, c, r);

					// copy the value from the source cell into the target cell
					// below
					// (don't do this if there is only one row)
					if (t.numRows > 1) {
						if (sourceCell != null) {

							if (t.doTraceGeoCopy) {
								setTraceCellAsGeoCopy(cons, sourceCell, c,
										r - 1);

							} else {

								setTraceCell(cons, c, r - 1,
										((GeoNumeric) sourceCell).getValue(),
										GeoClass.NUMERIC);
							}
						}
					}

					// if the source cell is in the last row, update it with a
					// new trace
					if (r == t.traceRow2) {

						if (t.doTraceGeoCopy) {
							setTraceCellAsGeoCopy(cons, geo, c, r);
						} else {
							setTraceCell(cons, c, r,
									t.lastTrace.get(c - t.traceColumn1),
									GeoClass.NUMERIC);
						}
					}
				}
			}
		}

		// draw the selection rectangle around the last trace row
		// table.setSelectionRectangle(new CellRange(table.app,t.traceColumn1, row,
		// t.traceColumn2, row));

		// update geo.traceRow counter
		t.tracingRow = (row < t.traceRow2) ? t.tracingRow + 1 : -1;

		// update trace lists
		if (t.showTraceList) {
			int traceIndex = 0;
			for (int column = t.traceColumn1; column <= t.traceColumn2; column++) {
				updateTraceListCell(cons, geo, column, t.traceRow1,
						t.lastTrace.get(traceIndex));
				++traceIndex;
			}
		}

		app.setScrollToShow(false);

	}


	/** Create a row of trace cell(s) in the trace column(s) of a geo. */
	protected boolean setGeoTraceRow(GeoElement geo, Construction cons,
			ArrayList<Double> traceArray, int row) {

		SpreadsheetTraceSettings t = traceGeoCollection.get(geo);
		int column = t.traceColumn1;
		int traceIndex = 0;
		GeoElement[] geos = geo.getGeoElements();

		if (t.doTraceGeoCopy) {
			setTraceCellAsGeoCopy(cons, geo, t.traceColumn1, row);
			return true;
		}

		// handle null trace (when shifting cells a null trace is sometimes
		// needed)
		if (traceArray == null) {
			traceArray = new ArrayList<Double>();
			traceArray.add(Double.NaN);
			traceArray.add(Double.NaN);
		}

		// trace
		ArrayList<GeoNumeric> traceList = new ArrayList<GeoNumeric>();
		for (int i = 0; i < geos.length; i++) {

			if (geos[i] instanceof SpreadsheetTraceable) {
				//AbstractApplication.debug("SpreadsheetTraceable");
				SpreadsheetTraceable traceGeo = (SpreadsheetTraceable) geos[i];

				traceList.clear();
				traceGeo.addToSpreadsheetTraceList(traceList);

				for (int j = 0; j < traceList.size(); j++) {

					setTraceCell(cons, column, row, traceArray.get(traceIndex),
							traceList.get(j).isGeoAngle() ? GeoClass.ANGLE
									: GeoClass.NUMERIC);
					++column;
					++traceIndex;
				}

			} else {
				App.warn("not SpreadsheetTraceable "
						+ geos[i].getClassName());

			}
		}

		return false;
	}

	protected void setTraceCellAsGeoCopy(Construction cons, GeoElement geo,
			int column, int row) {
		
		GeoElement cell = RelativeCopy.getValue(app, column, row);
		// String text = "";
		try {
			// GeoElement newCell =
			// RelativeCopy.prepareAddingValueToTableNoStoringUndoInfo(kernel,table,text,cell,column,row);
			// if(newCell != null)
			// newCell.setLabelVisible(false);

			if (cell != null) {
				cell.remove();
			}

			cell = geo.deepCopyGeo();
			cell.setLabel(GeoElementSpreadsheet.getSpreadsheetCellName(column,
					row));

			/*
			 * }else{ text = geo.toValueString(); cell =
			 * kernel.getAlgebraProcessor
			 * ().changeGeoElementNoExceptionHandling(cell, text, true, false);
			 * }
			 */

			cell.setAllVisualProperties(geo, true);
			cell.setSpreadsheetTrace(false);
			cell.setTraceSettings(null);
			cell.setAuxiliaryObject(true);
			cell.setLabelVisible(false);
			
			if (cell.isGeoText())
				cell.setEuclidianVisible(false);
			
			cell.updateCascade();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void setTraceCell(Construction cons, int column, int row,
			Object value, GeoClass geoClassType) {

		GeoElement cell = RelativeCopy.getValue(app, column, row);
		boolean isUpdateCell = cell != null
				&& cell.getGeoClassType().equals(geoClassType);

		if (isUpdateCell) {
			switch (geoClassType) {
			case NUMERIC:
				((GeoNumeric) cell).setValue((Double) value);
				break;

			case ANGLE:
				((GeoAngle) cell).setValue((Double) value);
				break;

			case TEXT:
				((GeoText) cell).setTextString((String) value);
				break;

			}

		} else {
			// delete old cell geo
			if (cell != null)
				CopyPasteCut.delete(app, column, row, column, row, MyTable.CELL_SELECT);

			String cellName = GeoElementSpreadsheet.getSpreadsheetCellName(
					column, row);
			switch (geoClassType) {

			case NUMERIC:
				cell = new GeoNumeric(cons, cellName, (Double) value);
				break;

			case ANGLE:
				cell = new GeoAngle(cons, cellName, (Double) value);
				break;

			case TEXT:
				cell = new GeoText(cons, cellName, (String) value);
				break;

			}
			cell.setEuclidianVisible(false);
		}

		cell.setAuxiliaryObject(true);
		//cell.updateCascade();
	}

	// ======================================
	// List Tracing

	private void createTraceListCell(Construction cons, int column, int row) {

		GeoElement cell = RelativeCopy.getValue(app, column, row);
		if (cell != null)
			CopyPasteCut.delete(app, column, row, column, row, MyTable.CELL_SELECT);

		try {
			cell = new GeoList(cons);
			cell.setLabel(GeoElementSpreadsheet.getSpreadsheetCellName(column,
					row));
			cell.setEuclidianVisible(false);
			cell.setAuxiliaryObject(true);
			cell.updateCascade();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateTraceListCell(Construction cons, GeoElement geo,
			int column, int row, Object value) {

		GeoElement cell = RelativeCopy.getValue(app, column, row);
		if (cell == null || !cell.isGeoList())
			return;

		if (geo.getTraceSettings().doTraceGeoCopy) {

			// add a copy of the trace
			((GeoList) cell).add(geo.copyInternal(cons));

		} else {
			// add the numeric value of the trace
			((GeoList) cell).add(new GeoNumeric(cons, (Double) value));
		}

		cell.updateCascade();
	}

	// End List Tracing
	// ======================================

	private void getCurrentTrace(GeoElement geo) {

		currentTrace.clear();
		Construction cons = app.getKernel().getConstruction();

		if (geo.isGeoList()) {
			for (int elem = 0; elem < ((GeoList) geo).size(); elem++) {
				addElementTrace(((GeoList) geo).get(elem), cons, currentTrace);
			}
		} else {
			addElementTrace(geo, cons, currentTrace);
		}
	}

	protected boolean addElementTrace(GeoElement geo, Construction cons,
			ArrayList<Double> currentTrace) {
		if (geo instanceof SpreadsheetTraceable) {
			SpreadsheetTraceable traceGeo = (SpreadsheetTraceable) geo;

			ArrayList<GeoNumeric> traceList = new ArrayList<GeoNumeric>();
					
			traceGeo.addToSpreadsheetTraceList(traceList);

			for (int i = 0; i < traceList.size(); i++) {
				currentTrace.add(traceList.get(i).getValue());
			}

		} 

		return false;

	}

	/** Create header cell(s) for each trace column of a geo. */
	private void setHeader(GeoElement geo, Construction cons) {

		SpreadsheetTraceSettings t = traceGeoCollection.get(geo);
		int column, row;
		GeoText headerText = null;
		GeoElement[] geos = geo.getGeoElements();

		if (t.showLabel) {
			row = t.traceRow1 + t.headerOffset - 1;
			column = t.traceColumn1;
			for (int i = 0; i < geos.length; i++) {

				if (geos[i] instanceof SpreadsheetTraceable) {
					ArrayList<GeoText> strings = ((SpreadsheetTraceable)geos[i]).getColumnHeadings();
					
					for (int j = 0 ; j < strings.size() ; j++) {
						headerText = strings.get(j);
						String cellName = GeoElementSpreadsheet.getSpreadsheetCellName(
								column, row);
						headerText.setLabel(cellName);
						column++;
					}
				} 
			}
		}
	}

}
