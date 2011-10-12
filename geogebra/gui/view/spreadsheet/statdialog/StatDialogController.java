package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.gui.view.spreadsheet.CellRange;
import geogebra.gui.view.spreadsheet.CellRangeProcessor;
import geogebra.gui.view.spreadsheet.MyTable;
import geogebra.gui.view.spreadsheet.RelativeCopy;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.kernel.AlgoDependentList;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.Point;
import java.util.ArrayList;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.apache.commons.math.util.MultidimensionalCounter.Iterator;

public class StatDialogController {


	private Application app;
	private Kernel kernel; 
	private Construction cons;
	private MyTable spreadsheetTable;
	private SpreadsheetView spView;
	private StatDialog sd;	
	private StatGeo statGeo; 

	private Object dataSource;
	private GeoList dataAll, dataSelected;


	public GeoList getDataAll() {
		return dataAll;
	}

	public GeoList getDataSelected() {
		return dataSelected;
	}

	protected GeoElement geoRegression;

	private int mode;
	private boolean leftToRight = true;
	public void setLeftToRight(boolean leftToRight) {
		this.leftToRight = leftToRight;
	}

	public GeoElement getRegressionModel() {
		return geoRegression;
	}
	public void setRegressionModel(GeoFunction regressionModel) {
		this.geoRegression = regressionModel;
	}



	public StatDialogController(Application app, SpreadsheetView spView, StatDialog statDialog){

		this.app = app;
		this.kernel = app.getKernel();
		this.cons = kernel.getConstruction();
		this.spView = spView;
		this.spreadsheetTable = spView.getTable();
		this.sd = statDialog;
		this.mode = sd.getMode();
		this.statGeo = sd.getStatGeo();

	}


	/**
	 * Sets the data source. Returns false if data is invalid. Data may come
	 * from either a selected GeoList or the currently selected spreadsheet cell
	 * range.
	 */
	protected boolean setDataSource(){

		dataSource = null;
		CellRangeProcessor cr = spreadsheetTable.getCellRangeProcessor();
		boolean success = true;

		try {
			GeoElement geo = app.getSelectedGeos().get(0);
			if(geo.isGeoList()){
				// TODO: handle validation for a geoList source
				dataSource = geo;
			} else {
				ArrayList<CellRange> rangeList = spreadsheetTable.selectedCellRanges;			
				if(mode == StatDialog.MODE_ONEVAR){
					success = cr.isOneVarStatsPossible(rangeList);
				}
				else if(mode == StatDialog.MODE_REGRESSION){
					success = cr.isCreatePointListPossible(rangeList);
				}
				else if(mode == StatDialog.MODE_MULTIVAR){
					success = cr.isMultiVarStatsPossible(rangeList);
				}

				if(success)
					dataSource = (ArrayList<CellRange>) rangeList.clone();	
			}

		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}

		return success;
	}



	/**
	 * Returns true if the current data source contains the specified GeoElement
	 */
	protected boolean isInDataSource(GeoElement geo){

		if(dataSource == null) return false;

		// TODO handle case of GeoList data source
		if(dataSource instanceof GeoList){
			return geo.equals(((GeoList)dataSource));
		}else{

			Point location = geo.getSpreadsheetCoords();
			boolean isCell = (location != null && location.x < SpreadsheetView.MAX_COLUMNS && location.y < SpreadsheetView.MAX_ROWS);

			if(isCell){	
				//Application.debug("---------> is cell:" + geo.toString());
				for(CellRange cr: (ArrayList<CellRange>)dataSource)
					if(cr.contains(geo)) return true;		

				//Application.debug("---------> is not in data source:" + geo.toString());
			}
		}

		return false;
	}


	/**
	 * Copies values from the current DataSource into the GeoList dataListAll
	 * and then stores references to these values in the GeoList dataListSelected.
	 */
	protected void loadDataLists(){

		if(dataSource == null) return;

		CellRangeProcessor crProcessor = spreadsheetTable.getCellRangeProcessor();
		String text = "";

		boolean scanByColumn = true;
		//boolean isSorted = false;
		boolean copyByValue = true;
		boolean doStoreUndo = false;


		//=======================================
		// create/update dataListAll 
		if(dataAll != null) dataAll.remove();

		if(dataSource instanceof GeoList){
			//dataListAll = dataSource;
			text = ((GeoList)dataSource).getLabel();
			//if(isSorted)
			//	text = "Sort[" + text + "]";

		}else{

			ArrayList<CellRange> cellRangeList =  (ArrayList<CellRange>) dataSource;
			switch (mode){

			case StatDialog.MODE_ONEVAR:
				dataAll = (GeoList) crProcessor.createList(
						cellRangeList, 
						scanByColumn,
						copyByValue, 
						false, 
						doStoreUndo, 
						GeoElement.GEO_CLASS_NUMERIC, false);

				break;

			case StatDialog.MODE_REGRESSION:
				
				if( cellRangeList.size()==1 && cellRangeList.get(0).isPointList()){
					dataAll = (GeoList) crProcessor.createList(
							cellRangeList, 
							scanByColumn,
							copyByValue, 
							false, 
							doStoreUndo, 
							GeoElement.GEO_CLASS_POINT, false);
				}
				
				else{
					
					dataAll = (GeoList) crProcessor.createPointGeoList(
							cellRangeList, 
							copyByValue, 
							leftToRight,
							false, 
							doStoreUndo);
				}
				break;

				case StatDialog.MODE_MULTIVAR:
					cons.setSuppressLabelCreation(true);
					dataAll = crProcessor.createCollectionList((ArrayList<CellRange>)dataSource, true); 
					cons.setSuppressLabelCreation(false);
					break;

			}
		}	

		
		//=======================================
		// create/update dataListSelected

		if(dataSelected == null){
			cons.setSuppressLabelCreation(true);
			dataSelected = new GeoList(cons);			
			cons.setSuppressLabelCreation(false);
		}
		

		try {			
			dataSelected.clear();
			for(int i=0; i<dataAll.size(); ++i)
				dataSelected.add(dataAll.get(i));		
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		if( !sd.isIniting && sd.dataPanel != null){
			sd.dataPanel.updateDataTable(this.dataAll);
		}

	}



	/**
	 * Add/remove elements from the selected data list. 
	 * Called by the data panel on checkbox click.
	 */
	public void updateSelectedDataList(int index, boolean doAdd) {

		GeoElement geo = dataAll.get(index);

		if(doAdd){
			dataSelected.add(geo);
		}else{
			dataSelected.remove(geo);
		}

		dataSelected.updateCascade();
		updateAllStatPanels(false);
		if(sd.regressionPanel != null)
			sd.regressionPanel.updateRegressionPanel();
		//Application.debug("updateSelectedList: " + index + doAdd);

	}


	/**
	 * Gets the data titles from the source cells.
	 */
	public String[] getDataTitles(){

		if(dataSource == null) return null;

		CellRangeProcessor cr = spreadsheetTable.getCellRangeProcessor();
		String[] title = null;

		switch(mode){

		case StatDialog.MODE_ONEVAR:

			title = new String[1];		

			if(dataSource instanceof GeoList){
				title[0] = ((GeoList) dataSource).getLabel();

			}else{

				CellRange range = ((ArrayList<CellRange>)dataSource).get(0);
				if(range.isColumn()) {
					GeoElement geo = RelativeCopy.getValue(spreadsheetTable, range.getMinColumn(), range.getMinRow());
					if(geo != null && geo.isGeoText())
						title[0] = geo.toDefinedValueString();
					else
						title[0]= app.getCommand("Column") + " " + 
						GeoElement.getSpreadsheetColumnName(range.getMinColumn());		

				}else{
					title[0] = app.getMenu("Untitled");
				}
			}

			break;

		case StatDialog.MODE_REGRESSION:
			if(dataSource instanceof GeoList){
				//TODO -- handle geolist data source titles
				//title[0] = ((GeoList) dataSource).getLabel();
			}else{
				title = cr.getPointListTitles((ArrayList<CellRange>)dataSource, leftToRight);
			}
			break;

		case StatDialog.MODE_MULTIVAR:
			if(dataSource instanceof GeoList){
				//TODO -- handle geolist data source titles
				//title[0] = ((GeoList) dataSource).getLabel();
			}else{
				title = cr.getColumnTitles((ArrayList<CellRange>)dataSource);
			}
			break;

		}

		return title;
	}


	public void swapXY(){
		leftToRight = !leftToRight;
		updateDialog(false);
	}



	public void updateDialog(boolean doSetDataSource){

		removeStatGeos();
		boolean hasValidDataSource = doSetDataSource? setDataSource() : true;
		if(dataSource == null) return;

		if(hasValidDataSource){
			loadDataLists();

			updateAllStatPanels(true);

			if(mode == StatDialog.MODE_REGRESSION){
				setRegressionGeo();
				if(sd.regressionPanel != null)
					sd.regressionPanel.updateRegressionPanel();
			}
		}else{
			//TODO --- handle bad data	
		}

	}

	public void updateAllStatPanels(boolean doCreateGeo){

		sd.comboStatPanel.updatePlot(doCreateGeo);
		if(sd.comboStatPanel2 != null)
			sd.comboStatPanel2.updatePlot(doCreateGeo);
		sd.statisticsPanel.updatePanel();

	}




	protected void handleRemovedDataGeo(GeoElement geo){

		//System.out.println("removed: " + geo.toString());
		if (isInDataSource(geo)) {	
			//System.out.println("stat dialog removed: " + geo.toString());
			//removeStatGeos();
			dataSource = null;
			updateDialog(false);
		}

	}


	public void setRegressionGeo(){

		if(geoRegression != null){
			geoRegression.remove();
		}

		geoRegression = (GeoElement)statGeo.createRegressionPlot(dataSelected, sd.getRegressionMode(), sd.getRegressionOrder(), false);
		geoRegression.removeView(Application.VIEW_EUCLIDIAN);
		geoRegression.setAuxiliaryObject(true);
		app.getEuclidianView().remove(geoRegression);
		geoRegression.setLabel("regressionModel");
		updateAllStatPanels(true);
	}


	/**
	 * Removes all geos maintained by this dialog and its child components
	 */
	public void removeStatGeos(){

		removeStatGeo(dataAll);
		removeStatGeo(dataSelected);
		removeStatGeo(geoRegression);

		if(sd.comboStatPanel != null)
			sd.comboStatPanel.removeGeos();

		if(sd.comboStatPanel2 != null)
			sd.comboStatPanel2.removeGeos();

	}

	private void removeStatGeo(GeoElement statGeo){
		if(statGeo != null){
			statGeo.remove();
			statGeo = null;
		}
	}


	public SummaryStatistics getSummaryStatistics(GeoList dataList){

		SummaryStatistics stats = new SummaryStatistics();
		for (int i=0; i < dataList.size(); i++) {
			GeoElement geo = dataList.get(i);
			if (geo.isNumberValue()) {
				NumberValue num = (NumberValue) geo;
				stats.addValue(num.getDouble());
			}    		    		
		}   				
		return stats;
	}



	public double[] getValueArray(GeoList dataList){
		ArrayList<Double> list = new ArrayList<Double>();
		for (int i=0; i < dataList.size(); i++) {
			GeoElement geo = dataList.get(i);
			if (geo.isNumberValue()) {
				NumberValue num = (NumberValue) geo;
				list.add(num.getDouble());
			}    		    		
		}   		
		double[] val = new double[list.size()];
		for (int i=0; i < list.size(); i++) 
			val[i] = list.get(i);

		return val;
	}





}
