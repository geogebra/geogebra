package geogebra.gui.view.spreadsheet;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

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
 * (1) maintains TraceGeoCollection, a hash table that matches all GeoElements that
 *     trace to the spreadsheet with their trace settings
 * 
 * (2) creates and updates spreadsheet cell traces based on type of geo (e.g.
 *     angles, polar points, lists)
 * 
 * (3) determines the column/row location for a geo trace
 *  
 * 
 * @author G. Sturr 2010-4-22
 * 
 */


public class SpreadsheetTraceManager {
	
	// external components
	private Application app;
	private Kernel kernel;
	private SpreadsheetView view;
	private MyTable table; 
	
	
	
	// collection of all geos currently traced
	private HashMap<GeoElement, TraceSettings> traceGeoCollection;	
	
	
	// temporary collection of trace geos, held during construction updates 
	private HashSet<GeoElement> storedTraces;
	private boolean collectingTraces = false;
	
	// misc variables
	private boolean doShiftCellsUp = true; 
	private double[] coords = new double[2];
	private ArrayList<Double> currentTrace = new ArrayList<Double>();
	
	
	
	public SpreadsheetTraceManager(SpreadsheetView view ){
		
		this.view = view;
		app = view.getApplication();
		kernel = app.getKernel();
		table = view.getTable();
		
		traceGeoCollection = new HashMap<GeoElement, TraceSettings >();		
		storedTraces = new HashSet<GeoElement>();
		
	}
	
	

	// =============================================	
	//            Add/Remove Geo Trace 
	// =============================================	
	 
	
	/**  Add a geo to the trace collection  */
	public void addSpreadsheetTraceGeo(GeoElement geo){
			
		TraceSettings t = geo.getTraceSettings();
		Construction cons = app.getKernel().getConstruction();
		
		//Set trace columns
		if(t.traceColumn1 == -1){
			t.traceColumn1 = getNextTraceColumn(); 
		}
		
		if(t.doTraceGeoCopy){
			t.traceColumn2 = t.traceColumn1;
		}else{
			// set number of columns by geotype
			if(geo.isGeoNumeric()){
				t.traceColumn2 = t.traceColumn1;
			}
			else if(geo.isGeoPoint() || geo.isGeoVector()){
				t.traceColumn2 = t.traceColumn1 + 1;
			}
			else if(geo.isGeoList()){
				t.traceColumn2 = t.traceColumn1 + ((GeoList)geo).size() - 1;
				for(int index = 0; index < ((GeoList)geo).size(); index++ ){
					if(((GeoList)geo).get(index).isGeoPoint())
						t.traceColumn2 ++;
				}
			}		
		}


		//Set trace rows
		if(t.traceRow1 == -1){
			t.traceRow1 = 0; //default to first row
		}
			
		t.headerOffset = 0;
		if(t.showLabel) ++t.headerOffset ; 
		if(t.showTraceList) ++t.headerOffset; 	
		
		if(t.doRowLimit){
			t.traceRow2 = t.traceRow1 + t.numRows - 1 + t.headerOffset;
		}else{
			t.traceRow2 = SpreadsheetView.MAX_ROWS;		
		}	
				
		t.tracingRow = t.traceRow1;
		
		t.lastTrace.clear();
		
		// add the geo and its settings to the hash table
		traceGeoCollection.put(geo, t);	
		
		
	
		//set the tracing flag for this geo
		geo.setSpreadsheetTrace(true);
		
		//clear the trace columns and put the current trace into the spreadsheet
		clearGeoTraceColumns(geo);
		
		if(t.showTraceList){		
			for(int column = t.traceColumn1; column <= t.traceColumn2; column++){
				createTraceListCell(cons,  column,  t.traceRow1);
			}
		}
		
		//traceToSpreadsheet(geo);
		setHeader(geo,cons);
		view.repaint();
		
	}
		
	
	public void updateTraceSettings(GeoElement geo){
		TraceSettings t = geo.getTraceSettings();
		//clearGeoTraceColumns(geo);
		table.copyPasteCut.delete(t.traceColumn1, t.traceRow1, t.traceColumn2, SpreadsheetView.MAX_ROWS);
		addSpreadsheetTraceGeo(geo);	
	}

	
	

	
	/** Remove a geo from the trace collection   */
	public void removeSpreadsheetTraceGeo(GeoElement geo){
	
		if(!traceGeoCollection.containsKey(geo)) return;		
		traceGeoCollection.remove(geo);
		view.repaint();
			
	}
	
	
	
	/** Remove all geos from the trace collection. */
	public void removeAllSpreadsheetTraceGeos(){
		
		for(GeoElement geo:traceGeoCollection.keySet()){
			//geo.setSpreadsheetTrace(false);
		}
		traceGeoCollection.clear();	
		view.repaint();
		//Application.printStacktrace("remove all traces ");
	}
	
	
	/** Load all tracing geos into the TraceGeoCollection. 
	 * Called by SpreadsheetView after undo or load file.*/ 
	public void loadTraceGeoCollection(){
		//Application.debug("loading trace geos");
		traceGeoCollection.clear();	
		TreeSet<GeoElement> ts = app.getKernel().getConstruction().getGeoSetConstructionOrder();
			for(GeoElement geo: ts){
				if( geo.getSpreadsheetTrace()){
					traceGeoCollection.put(geo,geo.getTraceSettings());
					//System.out.println("load this geo: " + geo.toString());
				}
		}
		view.repaint();
	}

	
	
	
	// =============================================	
	//              Utility Methods
 	// =============================================
	
	
	
	private int getNextTraceColumn(){
		return Math.max( view.getHighestUsedColumn() , getHighestTraceColumn())+ 1 ;
	}
	
	
	private int getHighestTraceColumn() {
		
		int max = -1;	
		for ( GeoElement geo : traceGeoCollection.keySet()) {
			if ( geo.getTraceSettings().traceColumn2 > max)
				max = geo.getTraceSettings().traceColumn2;
		}	
		return max;
	}
	
	
	public CellRange getNextTraceCell(){
		return new CellRange(table, getNextTraceColumn(), 0);
	}
	
	public boolean isTraceGeo(GeoElement geo){
		
		return traceGeoCollection.containsKey(geo);
		
	}
	
	
	public boolean isTraceColumn(int column){
		TraceSettings t = new TraceSettings();
		for ( GeoElement geo : traceGeoCollection.keySet()) {
			t = traceGeoCollection.get(geo);
			if ( column >= t.traceColumn1 && 
					column <= t.traceColumn2)
				return true;
		}
		
		return false;
	}
	
	
	public ArrayList<GeoElement> getTraceGeoList(){
	
		ArrayList<GeoElement> traceGeoList = new ArrayList<GeoElement>();
		for ( GeoElement geo : traceGeoCollection.keySet()) {
			traceGeoList.add(geo);
		}
		return traceGeoList;
		
	}
	
	public GeoElement getTraceGeo(int column){
		TraceSettings t = new TraceSettings();
		for ( GeoElement geo : traceGeoCollection.keySet()) {
			t = traceGeoCollection.get(geo);
			if ( column >= t.traceColumn1 && 
					column <= t.traceColumn2)
				return geo;
		}
		
		return null;
		
	}
	
	public TraceSettings getTraceSettings(GeoElement geo){
		
			return traceGeoCollection.get(geo);		
	}
	
	
	/**
 	* Delete the elements of all trace columns, excluding headers.
 	* This is called when a user resets the row limit.	  
 	*/
	private void clearAllTraceColumns(){
		
		for(GeoElement geo: traceGeoCollection.keySet()){			
			clearGeoTraceColumns(geo);
		}		
	}
	
	/**
	 * Delete the elements in the trace columns of a single geo.
	 */
	public void clearGeoTraceColumns(GeoElement geo){
		
		TraceSettings t = geo.getTraceSettings();
		if (t==null) return;
		 
		table.copyPasteCut.delete(t.traceColumn1, t.traceRow1, t.traceColumn2, SpreadsheetView.MAX_ROWS);
		t.tracingRow = t.traceRow1;	
		t.lastTrace.clear();
		
	}
	
	
	
	
	/**
	 * If a user deletes the contents of a trace column then
	 *   1) re-insert the header text cell (if header is shown)  
	 *   2) reset the trace row to the top of the column
	 */
	public void handleColumnDelete(int column1, int column2){
		TraceSettings t;
		for ( GeoElement geo : traceGeoCollection.keySet()) {
			t = geo.getTraceSettings();
			if ( column2 >= t.traceColumn1 && column1 <= t.traceColumn2 ){
				//removeSpreadsheetTraceGeo(geo);
				Construction cons = app.getKernel().getConstruction();
				setHeader(geo,cons);
				t.tracingRow = 1;	
			}
		}
		
		view.repaint();
		
	}
	
	public TraceSettings getDefaultTraceSettings(){
		return new TraceSettings();
	}
	
	
	public void setNeedsColumnReset(GeoElement geo, boolean flag){
		if(!traceGeoCollection.containsKey(geo)) return;
		traceGeoCollection.get(geo).needsColumnReset = flag;
		
	}

	
	
	

	// =============================================	
	//                XML 
 	// =============================================
	
	

	public String getTraceXML(GeoElement geo){
		TraceSettings t = geo.getTraceSettings();
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
		sb.append(t.doColumnReset  ? "true" : "false" );
		sb.append("\"");
		
		sb.append(" doRowLimit=\"");
		sb.append(t.doRowLimit  ? "true" : "false" );
		sb.append("\"");
		
		sb.append(" showLabel=\"");
		sb.append(t.showLabel  ? "true" : "false" );
		sb.append("\"");
		
		sb.append(" showTraceList=\"");
		sb.append(t.showTraceList  ? "true" : "false" );
		sb.append("\"");
		
		sb.append(" doTraceGeoCopy=\"");
		sb.append(t.doTraceGeoCopy  ? "true" : "false" );
		sb.append("\"");
		
		sb.append("/>\n");
		
		
		/* this param is not included:
		 * 
				public ArrayList<Double> lastTrace = new ArrayList<Double>();
				
		   do we need it?
		*/
				
		return sb.toString();	
	}
	
	
	// =============================================	
	//                Tracing 
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
		
		for(GeoElement geo:storedTraces) {
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
		if (kernel.isViewReiniting()) return;

		if(!traceGeoCollection.containsKey(geo)) return;				
		TraceSettings t = traceGeoCollection.get(geo);
		Construction cons = app.getKernel().getConstruction();

		//TODO 
		// 1) test if equals is working here
		// 2) add code to override this test if grouping of trace elements is allowed
		// 3) improve selection rectangle handling

		// get the current trace for this geo and compare with last trace
		// exit if no change in trace
		getCurrentTrace(geo);	
		if(!t.doTraceGeoCopy && !geo.isRandomGeo() && currentTrace.equals(t.lastTrace)) return;	
		t.lastTrace = (ArrayList<Double>) currentTrace.clone();


		// if only collecting traces, then record this geo for later tracing and exit.
		if (collectingTraces) {
			storedTraces.add(geo);
			return;
		}


		// handle column reset
		if(t.needsColumnReset && t.doColumnReset){
			t.traceColumn1 = getNextTraceColumn();
			t.tracingRow = t.traceRow1;
			t.needsColumnReset = false;
		}


		// allow autoscrolling to keep the current trace in view 
		view.setScrollToShow(true);


		// set the headers
		if(t.tracingRow == t.traceRow1 && t.headerOffset > 0) 
			setHeader(geo,cons);

		// 'row' is the temporary row counter actually used for creating traces. 
		// 't.tracingRow' is the row counter kept in memory. 	
		// When row size is limited then tracingRow = -1 serves as a flag to
		// keep our row counter from going past traceRow2
		int row = t.tracingRow + t.headerOffset;
		if(t.tracingRow == -1)
			row = t.traceRow2;



		// add geo traces if we are NOT in the last row
		if(t.tracingRow != -1){
			setGeoTraceRow(geo,cons, t.lastTrace, row);
		} 

		// if in the last row, shift cells up and put new trace in last row  
		else if(doShiftCellsUp){

			GeoElement sourceCell;
			//GeoElement targetCell; 

			int minTraceRow = t.traceRow1 + t.headerOffset + 1;
			if(t.numRows == 1) --minTraceRow;

			for(int c = t.traceColumn1; c <= t.traceColumn2; c++){
				for (int r = minTraceRow; r <= t.traceRow2; r++){

					// get the source cell
					sourceCell = RelativeCopy.getValue(table, c, r);

					// copy the value from the source cell into the target cell below
					// (don't do this if there is only one row)
					if(t.numRows > 1){
						if(sourceCell != null){

							if(t.doTraceGeoCopy){
								setTraceCellAsGeoCopy(cons, sourceCell,c,r-1);

							}else{

								setTraceCell(cons,c,r-1,((GeoNumeric)sourceCell).getValue(),GeoElement.GEO_CLASS_NUMERIC);
							}
						}
					}

					// if the source cell is in the last row, update it with a new trace 
					if(r == t.traceRow2){

						if(t.doTraceGeoCopy){
							setTraceCellAsGeoCopy(cons,geo,c,r);
						}else{	
							setTraceCell(cons,c,r,t.lastTrace.get(c - t.traceColumn1),GeoElement.GEO_CLASS_NUMERIC);
						}
					}	
				}
			}	   		
		}

		// draw the selection rectangle around the last trace row
		//table.setSelectionRectangle(new CellRange(table,t.traceColumn1, row, t.traceColumn2, row));


		// update geo.traceRow counter
		t.tracingRow = (row < t.traceRow2) ? t.tracingRow + 1 : -1;

		//update trace lists
		if(t.showTraceList){
			int traceIndex = 0;
			for(int column = t.traceColumn1; column <= t.traceColumn2; column++){
				updateTraceListCell(cons,geo,column, t.traceRow1,t.lastTrace.get(traceIndex));
				++traceIndex;
			}
		}

		view.setScrollToShow(false);

}
	
	
	/** create array of GeoElements to be traced */
	private GeoElement[] getElementList(GeoElement geo){

		GeoElement[] geos;
		if(geo.isGeoList()){	
			geos = new GeoElement[((GeoList)geo).size()];
			for (int i = 0; i < ((GeoList)geo).size(); i++) {
				geos[i] = ((GeoList)geo).get(i);
			}		
		}else{		
			geos = geo.getGeoElements();
		}
		return geos;
	}


	
	/** Create a row of trace cell(s) in the trace column(s) of a geo. */  
	private void setGeoTraceRow(GeoElement geo, Construction cons, ArrayList<Double> traceArray,  int row) {

		TraceSettings t = traceGeoCollection.get(geo);
		int column = t.traceColumn1;
		int traceIndex = 0;
		GeoElement[] geos = getElementList(geo);
		
		if(t.doTraceGeoCopy){
			setTraceCellAsGeoCopy(cons,geo,t.traceColumn1,row);
			return;
		}
		
		// handle null trace (when shifting cells a null trace is sometimes needed)	
		if(traceArray == null){
			traceArray = new ArrayList<Double>();
			traceArray.add(Double.NaN);
			traceArray.add(Double.NaN);
		}
		
		// trace 
		for (int i = 0; i < geos.length; i++) {		

			switch (geos[i].getGeoClassType()) {

			case GeoElement.GEO_CLASS_POINT: 

				setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoElement.GEO_CLASS_NUMERIC);
				++column;
				++traceIndex;

				if (((GeoPoint) geos[i]).getMode() == Kernel.COORD_POLAR)
					setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoElement.GEO_CLASS_ANGLE);
				else
					setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoElement.GEO_CLASS_NUMERIC);
				++column;
				++traceIndex;

				break;


			case GeoElement.GEO_CLASS_VECTOR: 
				
				setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoElement.GEO_CLASS_NUMERIC);
				++column;
				++traceIndex;
				
				setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoElement.GEO_CLASS_NUMERIC);
				++column;
				++traceIndex;
				break;


			case GeoElement.GEO_CLASS_NUMERIC:
				
				setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoElement.GEO_CLASS_NUMERIC);
				++column;
				++traceIndex;
				break;


			case GeoElement.GEO_CLASS_ANGLE: 
				
				setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoElement.GEO_CLASS_ANGLE);
				++column;
				++traceIndex;
				break;

			}
		}					
	}


	
	private void setTraceCellAsGeoCopy(Construction cons, GeoElement geo, int column, int row){
		
		GeoElement cell = RelativeCopy.getValue(table, column, row);
		String text = "";
		try {
			//GeoElement newCell = RelativeCopy.prepareAddingValueToTableNoStoringUndoInfo(kernel,table,text,cell,column,row);
			//if(newCell != null)
			//	newCell.setLabelVisible(false);
					
			if(cell != null){
				cell.remove();
			}
			
			cell = geo.copyInternal(cons);
			cell.setLabel(GeoElement.getSpreadsheetCellName(column,row));
			
			/*
			}else{
				text = geo.toValueString();
				cell = kernel.getAlgebraProcessor().changeGeoElementNoExceptionHandling(cell, text, true, false);		
			}
			*/
			
			cell.setAllVisualProperties(geo, true);
			cell.setSpreadsheetTrace(false);
			cell.setTraceSettings(null);
			cell.setAuxiliaryObject(true);
			cell.setLabelVisible(false);
			cell.updateCascade();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setTraceCell(Construction cons, int column, int row, Object value, int geoClassType){

		GeoElement cell = RelativeCopy.getValue(table, column, row);
		boolean isUpdateCell = cell != null && cell.getGeoClassType() == geoClassType;

		if(isUpdateCell){	
			switch (geoClassType) {
			case GeoElement.GEO_CLASS_NUMERIC:
				((GeoNumeric) cell).setValue((Double)value); 
				break;

			case GeoElement.GEO_CLASS_ANGLE:
				((GeoAngle) cell).setValue((Double)value); 
				break;
				
			case GeoElement.GEO_CLASS_TEXT:
				((GeoText) cell).setTextString((String)value); 
				break;
				
			}

		}else{
			// delete old cell geo
			if(cell != null)
				table.copyPasteCut.delete(column, row, column, row);
			
			String cellName = GeoElement.getSpreadsheetCellName(column,row);
			switch (geoClassType) {

			case GeoElement.GEO_CLASS_NUMERIC:
				cell = new GeoNumeric(cons, cellName, (Double)value);
				break;

			case GeoElement.GEO_CLASS_ANGLE:
				cell = new GeoAngle(cons, cellName, (Double)value);
				break;
				
			case GeoElement.GEO_CLASS_TEXT:
				cell = new GeoText(cons, cellName, (String)value);
				break;

			}
			cell.setEuclidianVisible(false);
		}

		cell.setAuxiliaryObject(true);
		cell.updateCascade();
	}
	
	
	
	//======================================
	// List Tracing
	
	private void createTraceListCell(Construction cons, int column, int row){

		GeoElement cell = RelativeCopy.getValue(table, column, row);
		if(cell != null)
			table.copyPasteCut.delete(column, row, column, row);

		try {
			cell = new GeoList(cons);
			cell.setLabel(GeoElement.getSpreadsheetCellName(column,row));
			cell.setEuclidianVisible(false);
			cell.setAuxiliaryObject(true);
			cell.updateCascade();

		} catch (Exception e) {		
			e.printStackTrace();
		}
	}
	
	
	private void updateTraceListCell(Construction cons, GeoElement geo, int column, int row, Object value){

		GeoElement cell = RelativeCopy.getValue(table, column, row);
		if(cell == null || !cell.isGeoList()) return;

		if(geo.getTraceSettings().doTraceGeoCopy){
			
			// add a copy of the trace
			((GeoList) cell).add(geo.copyInternal(cons));     
				
			
		}else{
			// add the numeric value of the trace 
			((GeoList) cell).add(new GeoNumeric(cons, (Double)value));  
		}
		
		cell.updateCascade();
	}
	
	//    End List Tracing
	//======================================
	
	
	
	
	private void getCurrentTrace(GeoElement geo) {

		currentTrace.clear();
		Construction cons = app.getKernel().getConstruction();

		if (geo.isGeoList()) {
			for (int elem = 0; elem < ((GeoList) geo).size(); elem++) {
				addElementTrace(((GeoList) geo).get(elem), cons,currentTrace);
			}
		} else {
			addElementTrace(geo, cons, currentTrace);
		}

	}
	
	
	private void addElementTrace(GeoElement geo, Construction cons, ArrayList<Double> currentTrace){

		switch (geo.getGeoClassType()) {

		case GeoElement.GEO_CLASS_POINT:

			GeoPoint P = (GeoPoint) geo;
			boolean polar = P.getMode() == Kernel.COORD_POLAR;

			if (polar)
				P.getPolarCoords(coords);
			else
				P.getInhomCoords(coords);

			currentTrace.add(coords[0]);
			currentTrace.add(coords[1]);		
			break;

			
		case GeoElement.GEO_CLASS_VECTOR:
			GeoVector vector = (GeoVector) geo;
			vector.getInhomCoords(coords);

			currentTrace.add(coords[0]);
			currentTrace.add(coords[1]);

			break;

		case GeoElement.GEO_CLASS_NUMERIC:
			GeoNumeric num = (GeoNumeric) geo;			
			currentTrace.add(num.getValue());
			break;
			
		case GeoElement.GEO_CLASS_ANGLE:
			GeoAngle angle = (GeoAngle) geo;			
			currentTrace.add(angle.getValue());
			break;
			
		// all other geos ... these should be traced as geo copies 	
		default:
			currentTrace.add(0.0);
		}
			
	}
	
	

	/**  Create header cell(s) for each trace column of a geo.   */
	private void setHeader(GeoElement geo, Construction cons) {

		TraceSettings t = traceGeoCollection.get(geo);
		int column, row;
		String headerText = "";
		GeoElement[] geos = getElementList(geo);

		if(t.showLabel){
			row = t.traceRow1 + t.headerOffset - 1;
			column = t.traceColumn1;
			for (int i = 0; i < geos.length; i++) {		
				if (!t.doTraceGeoCopy && (geos[i].isGeoPoint() || geos[i].isGeoVector()) ) {				
					headerText = "x( " + geos[i].getLabel() + " )";
					setTraceCell(cons, column, row, headerText, GeoElement.GEO_CLASS_TEXT);			
					headerText = "y( " + geos[i].getLabel() + " )";
					setTraceCell(cons, column + 1, row, headerText, GeoElement.GEO_CLASS_TEXT);
					column = column + 2;

				} else {
					headerText = geos[i].getLabel(); 
					setTraceCell(cons, column, row, headerText, GeoElement.GEO_CLASS_TEXT);
					++column;
				}
			}
		}
	}

	
}


