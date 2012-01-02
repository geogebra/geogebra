package geogebra.gui.view.spreadsheet;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.algos.AlgoDependentList;
import geogebra.common.kernel.algos.AlgoDependentPoint;
import geogebra.common.kernel.algos.AlgoPolyLine;
import geogebra.common.kernel.algos.AlgoSort;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.MyVecNode;
import geogebra.common.kernel.arithmetic.Operation;
import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.AbstractApplication;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElementSpreadsheet;
import geogebra.main.Application;

import geogebra.common.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;

import com.google.gwt.regexp.shared.MatchResult;

/**
 * 
 * Utility class with methods for processing cell ranges (e.g inserting rows,
 * creating lists of cells). Typical usage is via the instance of this class
 * created by the constructor of MyTable.
 * 
 * @author G. Sturr
 * 
 */
public class CellRangeProcessor {

	private MyTable table;
	private Application app;		
	private Construction cons;

	public CellRangeProcessor(MyTable table) {

		this.table = table;
		app = (Application)table.kernel.getApplication();
		cons = table.kernel.getConstruction();

	}



	//===============================================
	//            Validation
	//===============================================

	public boolean isCreatePointListPossible(ArrayList<CellRange> rangeList) {

		// two adjacent rows or columns?
		if (rangeList.size() == 1 && rangeList.get(0).is2D())
			return true;
		
		// two non-adjacent rows or columns?
		else if (rangeList.size() == 2 && 
				rangeList.get(0).getWidth() == 1 && rangeList.get(1).getWidth() == 1 )
			return true;

		else if (rangeList.size() == 1)
			return rangeList.get(0).isPointList();

		return false;
	}

	

	/*
	 * top-left must be a function of 2 variables eg A4(x,y)=x y^2
	 * top row and left column must be numbers
	 * other cells can be anything (will be erased)
	 */
	public boolean isCreateOperationTablePossible(ArrayList<CellRange> rangeList){

		if (rangeList.size() != 1) return false;

		CellRange cr = rangeList.get(0);
		int r1 = cr.getMinRow();
		int c1 = cr.getMinColumn();

		if (!(RelativeCopy.getValue(table, c1,r1) instanceof GeoFunctionNVar)) return false;

		for(int r = r1+1; r <= cr.getMaxRow(); ++r){
			if (!(RelativeCopy.getValue(table, c1,r) instanceof GeoNumeric)) return false;
		}

		for(int c = c1+1; c <= cr.getMaxColumn(); ++c){
			if (!(RelativeCopy.getValue(table, c,r1) instanceof GeoNumeric)) return false;
		}

		return true;
	}


	public boolean isCreateMatrixPossible(ArrayList<CellRange> rangeList){

		if(rangeList.size() == 1 && !rangeList.get(0).hasEmptyCells()) 
			return true;	
		else
			return false;

		/*
		// ctrl-selection block 
		if (rangeList.size() > 1){							 
				//rangeList.get(0).getWidth() == 1 && rangeList.get(1).getWidth() == 1 )
			return true;
		}

		if (rangeList.size() == 2 && 
				rangeList.get(0).getHeight() == 1 && rangeList.get(1).getHeight() == 1 )
			return true;

		return false;

		 */

	}


	/**
	 * Returns true if at least three cells in rangeList are GeoNumeric
	 */
	public boolean isOneVarStatsPossible(ArrayList<CellRange> rangeList) {

		if(rangeList == null || rangeList.size() == 0) return false; 

		for(CellRange cr:rangeList){
			if(containsMinimumGeoNumeric(cr,3)) return true;
		}

		return false;

	}


	/**
	 * Returns true if either: 1) rangeList is a single cell range with at least
	 * two columns and at least three non-empty rows or 2) rangeList contains
	 * two or more columns and each column has at least three data values.
	 * 
	 * @param rangeList
	 * @return
	 */
	public boolean isMultiVarStatsPossible(ArrayList<CellRange> rangeList){

		if(rangeList == null || rangeList.size() == 0) return false; 

		// if rangeList is a single cell range
		// check that there is at least two columns and at least three non-empty rows 
		if(rangeList.size()==1)
		{
			CellRange cr = rangeList.get(0);
			if (cr.getMaxColumn()-cr.getMinColumn() < 1)
				return false;

			for(int col=cr.getMinColumn(); col<=cr.getMaxColumn(); col++){			
				if(!containsMinimumGeoNumeric(new CellRange(table, col, cr.getMinRow(), col, cr.getMaxRow()) ,3)) 
					return false;
			}
			return true;
		}

		// otherwise check if rangeList contains two or more columns and each column
		// has at least three data values.
		else
		{
			int columnCount = 0;
			for(CellRange cr : rangeList){
				if(!cr.isColumn()) return false;
				if(!containsMinimumGeoNumeric(cr,3)) return false;
				columnCount += cr.getMaxColumn() - cr.getMinColumn() + 1;
			}
			return columnCount >= 2;
		}
		
		
	}

	/**
	 * Returns true if the number of GeoNumeric cells in cellRange is at least minimumCount.
	 * @param cellRange
	 * @param minimumCount
	 * @return
	 */
	private boolean containsMinimumGeoNumeric(CellRange cellRange, int minimumCount){
		int count = 0;
		for (int col = cellRange.getMinColumn(); col <= cellRange.getMaxColumn(); ++col) {
			for (int row = cellRange.getMinRow(); row <= cellRange.getMaxRow(); ++row) {
				GeoElement geo = RelativeCopy.getValue(table, col, row);
				if (geo != null && geo.isGeoNumeric()) ++count;
				if(count >= minimumCount) return true;
			}
		}
		return false;
	}



	//====================================================
	//           Create Lists from Cells
	//====================================================

	
	/**
	 * Creates a GeoList of lists where each element is a list
	 * of cells in each column spanned by the range list
	 */
	public GeoList createCollectionList(ArrayList<CellRange> rangeList, boolean copyByValue) {

		GeoList tempGeo = new GeoList(cons);
		boolean oldSuppress = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		for(CellRange cr : rangeList){
			for(int col=cr.getMinColumn(); col<=cr.getMaxColumn(); col++){
				tempGeo.add(createListFromColumn(col, copyByValue, false, false, GeoClass.NUMERIC));
			}
		}
		cons.setSuppressLabelCreation(oldSuppress);
		return tempGeo;
	}




	/**
	 * Creates a GeoPolyLine out of points constructed from the spreadsheet
	 * cells found in rangeList.
	 * Uses these defaults: no sorting, no undo point 
	 * @param rangeList
	 * @param byValue
	 * @param leftToRight
	 * @return
	 */
	public GeoElement createPolyLine(ArrayList<CellRange> rangeList, boolean byValue, boolean leftToRight) {
		return  createPolyLine(rangeList, byValue, leftToRight, false, false);
	}

	/**
	 * * Creates a GeoPolyLine out of points constructed from the spreadsheet
	 * cells found in rangeList.
	 * 
	 * @param rangeList
	 * @param byValue
	 * @param leftToRight
	 * @param isSorted
	 * @param doStoreUndo
	 * @return
	 */
	public GeoElement createPolyLine(ArrayList<CellRange> rangeList, boolean byValue, boolean leftToRight,
			boolean isSorted, boolean doStoreUndo) {

		boolean doCreateFreePoints = true;
		GeoList list = createPointGeoList(rangeList, byValue,  leftToRight, isSorted, doStoreUndo, doCreateFreePoints);

		AlgoPolyLine al = new AlgoPolyLine(cons, list);

		// need it in XML - used by Create Polyline tool, so don't want this line
		//cons.removeFromConstructionList(al);
		
		GeoElement ret = al.getGeoElements()[0];
		
		ret.setLabel(null);

		return ret;
	}

	private class PointDimension{
		private boolean doHorizontalPairs;
		private int c1, c2, r1, r2;
	}


	/**
	 * Given a cell range to converted into a point list, this determines if
	 * pairs are joined vertically or horizontally and gets the row column
	 * indices needed to traverse the cells.
	 */
	private static void getPointListDimensions(ArrayList<CellRange> rangeList, PointDimension pd) {

		pd.doHorizontalPairs = true;

		// note: we assume that rangeList has passed the isCreatePointListPossible() test 

		// CASE 1: selection is contiguous and 2D
		if (rangeList.size() == 1) {

			pd.doHorizontalPairs = rangeList.get(0).getWidth() == 2;
			pd.c1 = rangeList.get(0).getMinColumn();
			pd.c2 = rangeList.get(0).getMaxColumn();
			pd.r1 = rangeList.get(0).getMinRow();
			pd.r2 = rangeList.get(0).getMaxRow();


			// CASE 2: non-contiguous with two ranges (either single row or single column)
		} else {

			if(rangeList.get(0).getWidth() == 1 && rangeList.get(1).getWidth() == 1){
				pd.doHorizontalPairs = true;
				// we are traversing down columns. so get min and max column indices
				pd.c1 = Math.min(rangeList.get(0).getMinColumn(), rangeList.get(1).getMinColumn());
				pd.c2 = Math.max(rangeList.get(0).getMaxColumn(), rangeList.get(1).getMaxColumn());
				// but get the max-min and min-max row indices in case the columns don't line up
				pd.r1 = Math.max(rangeList.get(0).getMinRow(), rangeList.get(1).getMinRow());
				pd.r2 = Math.min(rangeList.get(0).getMaxRow(), rangeList.get(1).getMaxRow());

			}else{
				pd.doHorizontalPairs = true;
				// we are traversing across rows. so get min and max row indices
				pd.r1 = Math.min(rangeList.get(0).getMinRow(), rangeList.get(1).getMinRow());
				pd.r2 = Math.max(rangeList.get(0).getMaxRow(), rangeList.get(1).getMaxRow());	
				// but get the max-min and min-max column indices in case the rows don't line up
				pd.c1 = Math.max(rangeList.get(0).getMinColumn(), rangeList.get(1).getMinColumn());
				pd.c2 = Math.min(rangeList.get(0).getMaxColumn(), rangeList.get(1).getMaxColumn());

			}
		}

	}


	
	/**
	 * Creates a GeoList containing points constructed from the spreadsheet
	 * cells found in rangeList. note: It is assumed that rangeList has passed
	 * the isCreatePointListPossible() test
	 * 
	 * @param rangeList
	 * @param byValue
	 * @param leftToRight
	 * @param isSorted
	 * @param doStoreUndo
	 * @param doCreateFreePoints
	 *            if freePoints is true then a set of independent GeoPoints is
	 *            created in addition to the list
	 * @return GeoList
	 */
	public GeoList createPointGeoList(ArrayList<CellRange> rangeList, boolean byValue, boolean leftToRight,
			boolean isSorted, boolean doStoreUndo, boolean doCreateFreePoints) {

		// get the orientation and dimensions of the list
		PointDimension pd = new PointDimension();
		getPointListDimensions(rangeList, pd);

		AbstractKernel kernel = cons.getKernel();

		// build the string
		ArrayList<GeoElement> list = new ArrayList<GeoElement>();

		try {			
			GeoElement xCoord, yCoord;

			if (pd.doHorizontalPairs) {
				for (int i = pd.r1; i <= pd.r2; ++i) {
					
					xCoord = RelativeCopy.getValue(table, pd.c1, i);
					yCoord = RelativeCopy.getValue(table, pd.c2, i);
					
					// don't process the point if either coordinate is null or non-numeric, 
					if (xCoord == null || yCoord == null || !xCoord.isGeoNumeric() || !yCoord.isGeoNumeric()) 
					continue;
					
					GeoPoint2 geoPoint;
					AlgoDependentPoint pointAlgo = null;

					if (byValue) {
						if(leftToRight)
							geoPoint = new GeoPoint2(cons, ((GeoNumeric)xCoord).getDouble(), ((GeoNumeric)yCoord).getDouble(), 1.0);
						else
							geoPoint = new GeoPoint2(cons, ((GeoNumeric)yCoord).getDouble(), ((GeoNumeric)xCoord).getDouble(), 1.0);

					} else {
					
						MyVecNode vec = new MyVecNode( kernel, leftToRight ? xCoord : yCoord, leftToRight ? yCoord : xCoord);
						ExpressionNode point = new ExpressionNode(kernel, vec, Operation.NO_OPERATION, null);
						point.setForcePoint();
	
						pointAlgo = new AlgoDependentPoint(cons, point, false);
						
						geoPoint = (GeoPoint2) pointAlgo.getGeoElements()[0];
					
					}

					if (doCreateFreePoints) {
						// make sure points are independent of list (and so draggable)
						geoPoint.setLabel(null);	
					} else {
						if (pointAlgo != null)
							cons.removeFromConstructionList(pointAlgo);
					}
					
					list.add(geoPoint);
					
					if(yCoord.isAngle() || xCoord.isAngle())
						geoPoint.setPolar();
					
				}

			} else {   // vertical pairs
				for (int i = pd.c1; i <= pd.c2; ++i) {
					xCoord = RelativeCopy.getValue(table, i, pd.r1);
					yCoord = RelativeCopy.getValue(table, i, pd.r2);
					
					// don't process the point if either coordinate is null or non-numeric, 
					if (xCoord == null || yCoord == null || !xCoord.isGeoNumeric() || !yCoord.isGeoNumeric()) 
					continue;
					
					GeoPoint2 geoPoint;
					AlgoDependentPoint pointAlgo = null;

					if (byValue) {
						geoPoint = new GeoPoint2(cons, ((GeoNumeric)xCoord).getDouble(), ((GeoNumeric)yCoord).getDouble(), 1.0);
					} else {
					
						MyVecNode vec = new MyVecNode( kernel, leftToRight ? xCoord : yCoord, leftToRight ? yCoord : xCoord);
						ExpressionNode point = new ExpressionNode(kernel, vec, Operation.NO_OPERATION, null);
						point.setForcePoint();
	
						pointAlgo = new AlgoDependentPoint(cons, point, false);
						
						geoPoint = (GeoPoint2) pointAlgo.getGeoElements()[0];
					
					}

					if (doCreateFreePoints) {
						// make sure points are independent of list (and so draggable)
						geoPoint.setLabel(null);	
					} else {
						if (pointAlgo != null)
							cons.removeFromConstructionList(pointAlgo);
					}
					
					list.add(geoPoint);
					
					if(yCoord.isAngle() || xCoord.isAngle())
						geoPoint.setPolar();
				}
			}

			//System.out.println(list.toString());
		}


		catch (Exception ex) {
			AbstractApplication.debug("Creating list of points expression failed with exception " + ex);
		}
		
		AlgoDependentList dl = new AlgoDependentList(cons, list, false);
		cons.removeFromConstructionList(dl);
		return (GeoList) dl.getGeoElements()[0];

	}



	/**
	 * Creates a string expression for a point.
	 * @param leftCoord
	 * @param rightCoord
	 * @param byValue
	 * @param leftToRight
	 * @return
	 */
	private static String pointString(GeoElement leftCoord, GeoElement rightCoord, boolean byValue, boolean leftToRight) {

		// return null and exit if either coordinate is null or non-numeric 
		if (leftCoord == null || rightCoord == null || !leftCoord.isGeoNumeric() || !rightCoord.isGeoNumeric()) 
			return null;

		// set the coords to leftward or rightward orientation
		GeoElement xCoord, yCoord;
		xCoord = leftToRight? leftCoord: rightCoord;
		yCoord = leftToRight? rightCoord: leftCoord;

		String pointString = "";
		boolean isPolar = false;
		try {
			// test for polar point
			isPolar = yCoord.isAngle();
			String separator = isPolar? ";" : ",";

			if(byValue)
				pointString = "(" + ((GeoNumeric)xCoord).getDouble() 
				+ separator + ((GeoNumeric)yCoord).getDouble() + ")";
			else
				pointString = "(" + xCoord.getLabel() + separator + yCoord.getLabel() + ")";

		} catch (Exception ex) {
			AbstractApplication.debug("Creating point string failed with exception: " + ex);
		}

		return pointString;
	}



	public String[] getPointListTitles(ArrayList<CellRange> rangeList, boolean leftToRight) {

		String[] title = new String[2];

		// return null titles if data source is a point list
		if(rangeList.size()==1 && rangeList.get(0).isPointList())
			return title;
		
		// get the orientation and dimensions of the list
		PointDimension pd = new PointDimension();
		getPointListDimensions(rangeList, pd);

		if (pd.doHorizontalPairs) {
			// handle first title
			if(RelativeCopy.getValue(table, pd.c1, pd.r1).isGeoText()){
				//header cell text
				title[0] = ((GeoText)RelativeCopy.getValue(table, pd.c1, pd.r1)).getTextString();
			}
			else if(pd.r1 == 0){
				// column name
				title[0] = getCellRangeString(new CellRange(table,pd.c1,-1,pd.c1,-1));
			} else{
				// cell range
				title[0] = getCellRangeString(new CellRange(table,pd.c1,pd.r1,pd.c1,pd.r2));
			}

			// handle second title
			if(RelativeCopy.getValue(table, pd.c2, pd.r1).isGeoText()){
				//header cell text
				title[1] = ((GeoText)RelativeCopy.getValue(table, pd.c2, pd.r1)).getTextString();
			}
			else if(pd.r1 == 0){
				// column name
				title[1] = getCellRangeString(new CellRange(table,pd.c2,-1,pd.c2,-1));
			} else{
				// cell range
				title[1] = getCellRangeString(new CellRange(table,pd.c2,pd.r1,pd.c2,pd.r2));
			}


		} else {   // vertical pairs

			// handle first title
			if(RelativeCopy.getValue(table, pd.c1, pd.r1).isGeoText()){
				//header cell text
				title[0] = ((GeoText)RelativeCopy.getValue(table, pd.c1, pd.r1)).getTextString();
			}
			else if(pd.c1 == 0){
				// row name
				title[0] = getCellRangeString(new CellRange(table,-1,pd.r1,-1,pd.r1));
			} else{
				// cell range
				title[0] = getCellRangeString(new CellRange(table,pd.c1,pd.r1,pd.c2,pd.r1));
			}

			// handle second title
			if(RelativeCopy.getValue(table, pd.c1, pd.r2).isGeoText()){
				//header cell text
				title[1] = ((GeoText)RelativeCopy.getValue(table, pd.c1, pd.r2)).getTextString();
			}
			else if(pd.c1 == 0){
				// row name
				title[1] = getCellRangeString(new CellRange(table,-1,pd.r2,-1,pd.r2));
			} else{
				// cell range
				title[1] = getCellRangeString(new CellRange(table,pd.c1,pd.r2,pd.c2,pd.r2));
			}

		}

		if(!leftToRight){
			String temp = title[0];
			title[0]=title[1];
			title[1]=temp;
		}
		return title;
	}




	public String[] getColumnTitles(ArrayList<CellRange> rangeList) {

		ArrayList<String> titleList = new ArrayList<String>();

		for(CellRange cr : rangeList){
			// get column header or column name from each column in this cell range
			for(int col = cr.getMinColumn(); col <= cr.getMaxColumn(); col++){
				if(RelativeCopy.getValue(table, col, 0) != null && RelativeCopy.getValue(table, col, 0).isGeoText()){
					// use header cell text
					titleList.add(((GeoText)RelativeCopy.getValue(table, col, 0)).getTextString());
				}else{
					// use column name
					titleList.add(getCellRangeString(new CellRange(table,col,-1,col,-1)));
				}
			}
		}

		String[] title = new String[titleList.size()];
		title = titleList.toArray(title);
		return title;
	}






	/** Creates a GeoList from the cells in an array of cellranges. Empty cells are ignored.
	 * Uses these defaults: do not create undo point, do not sort, do not filter by geo type. */
	public GeoElement createList(ArrayList<CellRange> rangeList,  boolean scanByColumn, boolean copyByValue) {
		return  createList(rangeList,  scanByColumn, copyByValue, false, false, null, true) ;
	}

	/** Creates a GeoList from the cells in an array of cellranges. Empty cells are ignored */
	public GeoElement createList(ArrayList<CellRange> rangeList,  boolean scanByColumn, boolean copyByValue, 
			boolean isSorted, boolean doStoreUndo, GeoClass geoTypeFilter, boolean setLabel) {

		//StringBuilder listString = new StringBuilder();
		
		GeoList geoList = null;
		ArrayList<GeoElement> list = null;
		if (copyByValue) geoList = new GeoList(cons);
		else list = new ArrayList<GeoElement>();
		
		ArrayList<Point> cellList = new ArrayList<Point>();

		// temporary fix for catching duplicate cells caused by ctrl-seelct
		// will not be needed when sorting of cells by row/column is done
		HashSet<Point> usedCells = new HashSet<Point>();

		try {
			// get list string
			/*
			CellRange cr = new CellRange(table);
			for (int i = 0; i < rangeList.size(); i++) {
				cr = (CellRange) rangeList.get(i);
				list.addAll(0, cr.toGeoLabelList(scanByColumn, copyByValue));
			}
			 */

			//listString.append("{");

			// create cellList: this holds a list of cell index pairs for the entire range
			for(CellRange cr:rangeList){
				cellList.addAll(cr.toCellList(scanByColumn));
			}
			
			// iterate through the cells and add their contents to the expression string
			for(Point cell: cellList){
				if(!usedCells.contains(cell)){
					GeoElement geo = RelativeCopy.getValue(table, cell.x, cell.y);
					if (geo != null && (geoTypeFilter == null || geo.getGeoClassType() == geoTypeFilter)){
						if(copyByValue)
							//listString.append(geo.toDefinedValueString());
							geoList.add(geo.copy());
						else
							//listString.append(geo.getLabel());
							list.add(geo);

						//listString.append(geo.getFormulaString(StringType.GEOGEBRA, copyByValue));
						//listString.append(",");
					}

					usedCells.add(cell);
				}
			}

			// remove last comma
			//if(listString.length()>1)
			//	listString.deleteCharAt(listString.length()-1);

			//listString.append("}");
			
			if (!copyByValue) {
				AlgoDependentList algo = new AlgoDependentList(cons, list, false);
				geoList = (GeoList) algo.getGeoElements()[0];
			}

			if(isSorted){
				//listString.insert(0, "Sort[" );
				//listString.append("]");
				AlgoSort algo = new AlgoSort(cons, geoList);
				cons.removeFromConstructionList(algo);
				geoList = (GeoList)algo.getGeoElements()[0];
			}


			//Application.debug(listString);
			// convert list string to geo
			//geos = table.kernel.getAlgebraProcessor()
			//.processAlgebraCommandNoExceptions(listString.toString(), false);



		} catch (Exception ex) {
			AbstractApplication.debug("Creating list failed with exception " + ex);
		}

		if(doStoreUndo)
			app.storeUndoInfo();
		
		if (setLabel)
			geoList.setLabel(null);

		if(geoList != null)
			return geoList;
		else 
			return null;

	}
	
	
	/**
	 * Creates a GeoList from the cells in a frequency table. Empty and text cells are ignored.
	 * @param cr
	 * @param scanByColumn
	 * @param copyByValue
	 * @param isSorted
	 * @param doStoreUndo
	 * @param geoTypeFilter
	 * @param setLabel
	 * @return
	 */
	public GeoElement createListFromFrequencyTable(CellRange cr,  boolean scanByColumn, boolean copyByValue, 
			boolean isSorted, boolean doStoreUndo, Integer geoTypeFilter, boolean setLabel) {

		int freq;

		GeoElement valueGeo, freqGeo;
		GeoList geoList = new GeoList(cons);

		try {
			int c = cr.getMinColumn();
			for(int r = cr.getMinRow(); r <= cr.getMaxRow(); r++){
				
					valueGeo = RelativeCopy.getValue(table, c, r);
					freqGeo = RelativeCopy.getValue(table, c+1, r);

					// validate (null or text geos)
					//if (geo != null && (geoTypeFilter == null || geo.getGeoClassType() == geoTypeFilter)){

					freq = (int) ((GeoNumeric)freqGeo).getDouble();

					for(int i=0; i<freq; i++){
						geoList.add(valueGeo.copy());
					}
			}

			if(isSorted){
				AlgoSort algo = new AlgoSort(cons, geoList);
				cons.removeFromConstructionList(algo);
				geoList = (GeoList)algo.getGeoElements()[0];
			}


		} catch (Exception ex) {
			AbstractApplication.debug("Creating list from frequency table failed with exception " + ex);
		}

		if(doStoreUndo)
			app.storeUndoInfo();
		
		if (setLabel)
			geoList.setLabel(null);

		if(geoList != null) {
			return geoList;
		} else {
			return null;
			}
	}
	
	/** Creates a list from all cells in a spreadsheet column */
	public GeoElement createListFromColumn(int column, boolean copyByValue, boolean isSorted,
			boolean storeUndoInfo, GeoClass geoTypeFilter) {

		ArrayList<CellRange> rangeList = new ArrayList<CellRange>();
		CellRange cr = new CellRange(table,column,-1);
		cr.setActualRange();
		rangeList.add(cr);

		return createList(rangeList,  true, copyByValue, isSorted, storeUndoInfo, geoTypeFilter, true) ;
	}

	/** Returns true if all cell ranges in the list are columns */
	public boolean isAllColumns(ArrayList<CellRange> rangeList){
		boolean isAllColumns = true;
		for(CellRange cr : rangeList){
			if(!cr.isColumn()) isAllColumns = false;
		}
		return isAllColumns;
	}

	/**
	 * Creates a string expression for a matrix where each sub-list is a list
	 * of cells in the columns spanned by the range list
	 */
	public String createColumnMatrixExpression(ArrayList<CellRange> rangeList, boolean copyByValue) {
		GeoElement tempGeo;
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for(CellRange cr : rangeList){
			for(int col=cr.getMinColumn(); col<=cr.getMaxColumn(); col++){
				tempGeo = createListFromColumn(col, copyByValue, false, false, GeoClass.NUMERIC);
				sb.append(tempGeo.getCommandDescription());
				sb.append(",");
				tempGeo.remove();
			}
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("}");

		return sb.toString();
	}
	
	/**
	 * Creates a string expression for a matrix formed by the cell range with
	 * upper left corner (column1, row1) and lower right corner (column2, row2).
	 * If transpose = true then the matrix is the formed by interchanging rows
	 * with columns
	 */
	public String createMatrixExpression(int column1, int column2, int row1, int row2, boolean copyByValue, boolean transpose){

		GeoElement v2;
		StringBuilder sb = new StringBuilder();
		sb.append("{");

		if(!transpose){
			for (int j = row1; j <= row2; ++ j) {
				sb.append("{");
				for (int i = column1; i <= column2; ++ i) {
					v2 = RelativeCopy.getValue(table, i, j);
					if (v2 != null) {
						if(copyByValue){
							sb.append(v2.toDefinedValueString());
						}else{
							sb.append(v2.getLabel());
						}
						sb.append(',');
					}
					else {
						app.showErrorDialog(app.getPlain("CellAisNotDefined",GeoElementSpreadsheet.getSpreadsheetCellName(i,j)));
						return null;
					}
				}
				sb.deleteCharAt(sb.length()-1); // remove trailing comma
				sb.append("},");
			}

		} else {
			for (int j = column1; j <= column2; ++ j) {
				//if (selected.length > j && ! selected[j])  continue; 	
				sb.append("{");
				for (int i = row1; i <= row2; ++ i) {
					v2 = RelativeCopy.getValue(table, j, i);
					if (v2 != null) {
						if(copyByValue){
							sb.append(v2.toDefinedValueString());
						}else{
							sb.append(v2.getLabel());
						}
						sb.append(',');
					}
					else {
						app.showErrorDialog(app.getPlain("CellAisNotDefined",GeoElementSpreadsheet.getSpreadsheetCellName(i,j)));
						return null;
					}
				}
				sb.deleteCharAt(sb.length()-1); // remove trailing comma
				sb.append("},");
			}	
		}

		sb.deleteCharAt(sb.length()-1); // remove trailing comma
		sb.append('}');

		// Application.debug(sb.toString());
		return sb.toString();
	}






	/**
	 * Creates a Matrix geo from the cell range with upper left corner (column1,
	 * row1) and lower right corner (column2, row2). 
	 * NOTE: An undo point is not created.
	 * 
	 * @param column1
	 * @param column2
	 * @param row1
	 * @param row2
	 * @param copyByValue
	 * @return
	 */
	public GeoElement createMatrix(int column1, int column2, int row1, int row2, boolean copyByValue){
		return createMatrix( column1,  column2,  row1,  row2,  copyByValue, false);
	}

	/**
	 * Creates a Matrix geo from the cell range with upper left corner (column1,
	 * row1) and lower right corner (column2, row2). If transpose = true then
	 * the matrix is the formed by interchanging rows with columns. 
	 * NOTE: An undo point is not created.
	 * 
	 * @param column1
	 * @param column2
	 * @param row1
	 * @param row2
	 * @param copyByValue
	 * @param transpose
	 * @return
	 */
	public GeoElement createMatrix(int column1, int column2, int row1, int row2, boolean copyByValue, boolean transpose){

		GeoElement[] geos = null;
		String expr = null;
		
		try {
			expr = createMatrixExpression( column1, column2, row1, row2, copyByValue, transpose);
			//Application.debug(expr);
			geos = (GeoElement[])table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptions(expr, false);
		} 
		catch (Exception ex) {
			AbstractApplication.debug("creating matrix failed "+ expr);
			ex.printStackTrace();
		} 

		if(geos != null)
			return geos[0];
		else 
			return null;

	}




	/**
	 * Creates a TableText geo from the cell range with upper left corner (column1,
	 * row1) and lower right corner (column2, row2). If transpose = true then
	 * the TableText matrix is the formed by interchanging rows with columns. 
	 * NOTE: An undo point is not created.
	 * @param column1
	 * @param column2
	 * @param row1
	 * @param row2
	 * @param copyByValue 
	 * @return
	 */
	public GeoElement createTableText(int column1, int column2, int row1, int row2, boolean copyByValue, boolean transpose){

		GeoElement[] geos = null;
		StringBuilder text= new StringBuilder();
		
		try {
			text.append("TableText[");		
			text.append(createMatrixExpression( column1, column2, row1, row2, copyByValue, transpose));
			text.append(",\"|_\"]");

			//Application.debug(text);
			geos = (GeoElement[])table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptions(text.toString(), false);

		} 
		catch (Exception ex) {
			AbstractApplication.debug("creating TableText failed " + text);
			ex.printStackTrace();
		} 

		if(geos != null)
			return geos[0];
		else 
			return null;
	}


	


	//===================================================
	//              Insert Rows/Columns
	//===================================================


	//TODO: these methods are taken from the old code and need work
	// They behave badly when cells have references to cells on the other
	// side of a newly inserted row or column



	public void InsertLeft(int column1, int column2){ 

		int columns = table.getModel().getColumnCount();
		if (columns == column1 + 1){
			// last column: need to insert one more
			table.setMyColumnCount(table.getColumnCount() +1);		
			table.getView().getColumnHeader().revalidate();
			columns++;
		}
		int rows = table.getModel().getRowCount();
		boolean succ = table.copyPasteCut.delete(columns - 1, 0, columns - 1, rows - 1);
		for (int x = columns - 2; x >= column1; -- x) {
			for (int y = 0; y < rows; ++ y) {
				GeoElement geo = RelativeCopy.getValue(table, x, y);
				if (geo == null) continue;

				MatchResult matcher = GeoElementSpreadsheet.spreadsheetPattern.exec(geo.getLabel());
				int column = GeoElementSpreadsheet.getSpreadsheetColumn(matcher);
				int row = GeoElementSpreadsheet.getSpreadsheetRow(matcher);
				column += 1;
				String newLabel = GeoElementSpreadsheet.getSpreadsheetCellName(column, row);
				geo.setLabel(newLabel);
				succ = true;
			}
		}

		if (succ)
			app.storeUndoInfo();
	}


	public void InsertRight(int column1, int column2){

		int columns = table.getModel().getColumnCount();
		int rows = table.getModel().getRowCount();
		boolean succ = false;
		if (columns == column1 + 1){
			// last column: insert another on right
			table.setMyColumnCount(table.getColumnCount() +1);		
			table.getView().getColumnHeader().revalidate();
			// can't be undone
		}
		else
		{
			succ = table.copyPasteCut.delete(columns - 1, 0, columns - 1, rows - 1);
			for (int x = columns - 2; x >= column2 + 1; -- x) {
				for (int y = 0; y < rows; ++ y) {
					GeoElement geo = RelativeCopy.getValue(table, x, y);
					if (geo == null) continue;

					MatchResult matcher = GeoElementSpreadsheet.spreadsheetPattern.exec(geo.getLabel());
					int column = GeoElementSpreadsheet.getSpreadsheetColumn(matcher);
					int row = GeoElementSpreadsheet.getSpreadsheetRow(matcher);
					column += 1;
					String newLabel = GeoElementSpreadsheet.getSpreadsheetCellName(column, row);
					geo.setLabel(newLabel);
					succ = true;
				}
			}
		}

		if (succ)
			app.storeUndoInfo();
	}



	public void InsertAbove(int row1, int row2){
		int columns = table.getModel().getColumnCount();
		int rows = table.getModel().getRowCount();
		if (rows == row2 + 1){
			// last row: need to insert one more
			table.tableModel.setRowCount(table.getRowCount() +1);		
			table.getView().getRowHeader().revalidate();
			rows++;
		}
		boolean succ = table.copyPasteCut.delete(0, rows - 1, columns - 1, rows - 1);
		for (int y = rows - 2; y >= row1; -- y) {
			for (int x = 0; x < columns; ++ x) {
				GeoElement geo = RelativeCopy.getValue(table, x, y);
				if (geo == null) continue;

				MatchResult matcher = GeoElementSpreadsheet.spreadsheetPattern.exec(geo.getLabel());
				int column = GeoElementSpreadsheet.getSpreadsheetColumn(matcher);
				int row = GeoElementSpreadsheet.getSpreadsheetRow(matcher);
				row += 1;
				String newLabel = GeoElementSpreadsheet.getSpreadsheetCellName(column, row);
				geo.setLabel(newLabel);
				succ = true;
			}
		}

		if (succ)
			app.storeUndoInfo();
	}


	public void InsertBelow(int row1, int row2){	
		int columns = table.getModel().getColumnCount();
		int rows = table.getModel().getRowCount();
		boolean succ = false;
		if (rows == row2 + 1){
			// last row: need to insert one more
			table.tableModel.setRowCount(table.getRowCount() +1);		
			table.getView().getRowHeader().revalidate();
			// can't be undone
		}
		else
		{
			succ = table.copyPasteCut.delete(0, rows - 1, columns - 1, rows - 1);
			for (int y = rows - 2; y >= row2 + 1; -- y) {
				for (int x = 0; x < columns; ++ x) {
					GeoElement geo = RelativeCopy.getValue(table, x, y);
					if (geo == null) continue;
					MatchResult matcher = GeoElementSpreadsheet.spreadsheetPattern.exec(geo.getLabel());
					int column = GeoElementSpreadsheet.getSpreadsheetColumn(matcher);
					int row = GeoElementSpreadsheet.getSpreadsheetRow(matcher);
					row += 1;
					String newLabel = GeoElementSpreadsheet.getSpreadsheetCellName(column, row);
					geo.setLabel(newLabel);
					succ = true;
				}
			}
		}

		if (succ)
			app.storeUndoInfo();
	}




	/**
	 * Creates an operation table. 
	 */
	public void createOperationTable(CellRange cr, GeoFunctionNVar fcn ){

		int r1 = cr.getMinRow();
		int c1 = cr.getMinColumn();
		String text = "";
		GeoElement[] geos;
		fcn = (GeoFunctionNVar) RelativeCopy.getValue(table, c1,r1);

		for(int r = r1+1; r <= cr.getMaxRow(); ++r){
			for(int c = c1+1; c <= cr.getMaxColumn(); ++c){
				//System.out.println(GeoElementSpreadsheet.getSpreadsheetCellName(c, r) + ": " + text);

				text = GeoElementSpreadsheet.getSpreadsheetCellName(c, r) + "=" + fcn.getLabel() + "(";
				text += GeoElementSpreadsheet.getSpreadsheetCellName(c1, r);
				text += ",";
				text += GeoElementSpreadsheet.getSpreadsheetCellName(c, r1);
				text += ")";

				geos = (GeoElement[])table.kernel.getAlgebraProcessor()
				.processAlgebraCommandNoExceptions(text,false);

				//geos[0].setLabel(GeoElementSpreadsheet.getSpreadsheetCellName(c, r));
				geos[0].setAuxiliaryObject(true);
			}
		}
	}

	//Experimental ---- merging ctrl-selected cells 

	private static void consolidateRangeList(ArrayList<CellRange> rangeList){

		ArrayList<ArrayList<Point>> matrix = new ArrayList<ArrayList<Point>>();
		int minRow = rangeList.get(0).getMinRow();
		int maxRow = rangeList.get(0).getMaxRow();
		int minColumn = rangeList.get(0).getMinColumn();
		int maxColumn = rangeList.get(0).getMaxColumn();


		for(CellRange cr:rangeList){

			minColumn = Math.min(cr.getMinColumn(), minColumn);
			maxColumn = Math.max(cr.getMaxColumn(), maxColumn);
			minRow = Math.min(cr.getMinRow(), minRow);
			maxRow = Math.max(cr.getMaxRow(), maxRow);

			// create matrix of cells from all ranges in the list
			for(int col = cr.getMinColumn(); col <= cr.getMaxColumn(); col++){

				// add columns from this cell range to the matrix
				if(matrix.get(col) == null){
					matrix.add(col, new ArrayList<Point>());
					matrix.get(col).add(new Point(cr.getMinColumn(),cr.getMaxColumn()));
				} else {
					// Point p = matrix.get(col).get(1);
					//	if(cr.getMinColumn()>)
					//	insertPoint(matrix, new Point(new Point(cr.getMinColumn(),cr.getMaxColumn())));
				}
			}


			// convert our matrix to a CellRange list
			for(int col = minColumn; col <= maxColumn; col++){
				if(matrix.contains(col)){
					// TODO ?
				}
			}
		}
	}

	public String getCellRangeString(CellRange range){

		String s = "";

		if(range.isColumn()) {
			s = app.getCommand("Column") + " " + GeoElementSpreadsheet.getSpreadsheetColumnName(range.getMinColumn());

		} else if(range.isRow()) {
			s = app.getCommand("Row") + " " + (range.getMinRow() + 1);

		} else {
			s = GeoElementSpreadsheet.getSpreadsheetCellName(range.getMinColumn(), range.getMinRow());
			s += ":";
			s += GeoElementSpreadsheet.getSpreadsheetCellName(range.getMaxColumn(),range.getMaxRow());
		}

		return s;
	}


}
