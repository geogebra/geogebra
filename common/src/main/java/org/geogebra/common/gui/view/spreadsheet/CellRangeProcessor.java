package org.geogebra.common.gui.view.spreadsheet;

import java.util.ArrayList;
import java.util.HashSet;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentList;
import org.geogebra.common.kernel.algos.AlgoDependentPoint;
import org.geogebra.common.kernel.algos.AlgoPolyLine;
import org.geogebra.common.kernel.algos.AlgoSort;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

/**
 * 
 * Utility class with methods for processing cell ranges (e.g inserting rows,
 * creating lists of cells). Typical usage is via the instance of this class
 * created by the constructor of MyTable.
 * 
 * @author G. Sturr
 * 
 */
@SuppressWarnings("javadoc")
public class CellRangeProcessor {

	private MyTable table;
	private App app;
	private Localization loc;
	private Construction cons;
	private SpreadsheetTableModel tableModel;

	public CellRangeProcessor(MyTable table) {

		this.table = table;
		app = table.getKernel().getApplication();
		loc = app.getLocalization();
		tableModel = app.getSpreadsheetTableModel();
		cons = table.getKernel().getConstruction();

	}

	/**
	 * @param rangeList
	 *            cell range list to be cloned
	 * @return copy of given cell range list
	 */
	public static ArrayList<CellRange> clone(ArrayList<CellRange> rangeList) {
		ArrayList<CellRange> newList = new ArrayList<CellRange>();
		for (CellRange cr : rangeList) {
			newList.add(cr.clone());
		}
		return newList;
	}

	// ===============================================
	// Validation
	// ===============================================

	/**
	 * @param rangeList
	 *            given list of spreadsheet cell ranges
	 * @return true if the shapes of the given cell ranges support creating a
	 *         point list, does not test the cell contents
	 */
	public boolean isCreatePointListPossible(ArrayList<CellRange> rangeList) {

		// two adjacent rows or columns?
		if (rangeList.size() == 1
				&& (rangeList.get(0).is2D() || rangeList.get(0).is3D()))
			return true;

		// two non-adjacent rows or columns?
		else if (rangeList.size() == 2 && rangeList.get(0).getWidth() == 1
				&& rangeList.get(1).getWidth() == 1)
			return true;

		else if (rangeList.size() == 1)
			return rangeList.get(0).isPointList();

		return false;
	}

	/**
	 * top-left must be a function of 2 variables eg A4(x,y)=x y^2 top row and
	 * left column must be numbers other cells can be anything (will be erased)
	 * 
	 * @param rangeList
	 *            ranges
	 * @return whether operation table is possible
	 */
	public boolean isCreateOperationTablePossible(ArrayList<CellRange> rangeList) {

		if (rangeList.size() != 1)
			return false;

		CellRange cr = rangeList.get(0);
		int r1 = cr.getMinRow();
		int c1 = cr.getMinColumn();

		if (!(RelativeCopy.getValue(app, c1, r1) instanceof GeoFunctionNVar))
			return false;

		for (int r = r1 + 1; r <= cr.getMaxRow(); ++r) {
			if (!(RelativeCopy.getValue(app, c1, r) instanceof GeoNumeric))
				return false;
		}

		for (int c = c1 + 1; c <= cr.getMaxColumn(); ++c) {
			if (!(RelativeCopy.getValue(app, c, r1) instanceof GeoNumeric))
				return false;
		}

		return true;
	}

	public boolean isCreateMatrixPossible(ArrayList<CellRange> rangeList) {

		if (rangeList.size() == 1 && !rangeList.get(0).hasEmptyCells()) {
			return true;
		}
		return false;

		/*
		 * // ctrl-selection block if (rangeList.size() > 1){
		 * //rangeList.get(0).getWidth() == 1 && rangeList.get(1).getWidth() ==
		 * 1 ) return true; }
		 * 
		 * if (rangeList.size() == 2 && rangeList.get(0).getHeight() == 1 &&
		 * rangeList.get(1).getHeight() == 1 ) return true;
		 * 
		 * return false;
		 */

	}

	/**
	 * Returns true if at least two cells in rangeList are of the given GeoClass
	 * type
	 * 
	 * @param rangeList
	 * @param geoClass
	 * @return
	 */
	public boolean isOneVarStatsPossible(ArrayList<CellRange> rangeList,
			GeoClass geoClass) {

		if (rangeList == null || rangeList.size() == 0)
			return false;

		int count = 0;

		for (CellRange cr : rangeList) {
			count += cr.getGeoCount(geoClass);
			if (count >= 2)
				return true;
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
	public boolean isMultiVarStatsPossible(ArrayList<CellRange> rangeList) {

		if (rangeList == null || rangeList.size() == 0)
			return false;

		// if rangeList is a single cell range check that there are at least two
		// columns and at least three non-empty rows
		if (rangeList.size() == 1) {
			CellRange cr = rangeList.get(0);
			if (cr.getMaxColumn() - cr.getMinColumn() < 1)
				return false;

			for (int col = cr.getMinColumn(); col <= cr.getMaxColumn(); col++) {
				if (!containsMinimumGeoNumeric(
						new CellRange(app, col, cr.getMinRow(), col,
								cr.getMaxRow()), 3))
					return false;
			}
			return true;
		}

		// otherwise check if rangeList contains two or more columns and each
		// column
		// has at least three data values.
		int columnCount = 0;
		for (CellRange cr : rangeList) {
			if (!cr.isColumn())
				return false;
			if (!containsMinimumGeoNumeric(cr, 3))
				return false;
			columnCount += cr.getMaxColumn() - cr.getMinColumn() + 1;
		}
		return columnCount >= 2;
	}

	/**
	 * Returns true if the number of GeoNumeric cells in cellRange is at least
	 * minimumCount.
	 * 
	 * @param cellRange
	 * @param minimumCount
	 * @return
	 */
	private boolean containsMinimumGeoNumeric(CellRange cellRange,
			int minimumCount) {
		int count = 0;
		for (int col = cellRange.getMinColumn(); col <= cellRange
				.getMaxColumn(); ++col) {
			for (int row = cellRange.getMinRow(); row <= cellRange.getMaxRow(); ++row) {
				GeoElement geo = RelativeCopy.getValue(app, col, row);
				if (geo != null && geo.isGeoNumeric())
					++count;
				if (count >= minimumCount)
					return true;
			}
		}
		return false;
	}

	/**
	 * @param rangeList
	 * @param geoClass
	 * @return true if the given rangeList contains a GeoElement of the given
	 *         GeoClass type
	 */
	public boolean containsGeoClass(ArrayList<CellRange> rangeList,
			GeoClass geoClass) {
		for (CellRange cr : rangeList) {
			if (cr.containsGeoClass(geoClass))
				return true;
		}
		return false;
	}

	/**
	 * Returns the number of GeoElements of a given GeoClass type contained in
	 * the given list of cell ranges.
	 * 
	 * @param rangeList
	 * 
	 * @param geoClass
	 *            the GeoClass type to count. If null, then all GeoElements are
	 *            counted
	 * @return
	 */
	public int getGeoCount(ArrayList<CellRange> rangeList, GeoClass geoClass) {
		int count = 0;
		for (CellRange cr : rangeList) {
			count += cr.getGeoCount(geoClass);
		}
		return count;
	}

	public boolean is1DRangeList(ArrayList<CellRange> rangeList) {

		if (rangeList == null || rangeList.size() > 1) {
			return false;
		}

		return rangeList.get(0).is1D();
	}

	// ====================================================
	// Create Lists from Cells
	// ====================================================

	/**
	 * Creates a GeoList of lists where each element is a GeoList of cells in
	 * each column or row spanned by the given range list
	 */
	public GeoList createCollectionList(ArrayList<CellRange> rangeList,
			boolean copyByValue, boolean addToConstruction, boolean scanByColumn) {

		GeoList tempGeo = new GeoList(cons);
		boolean oldSuppress = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		CellRange tempRange = null;

		for (CellRange cr : rangeList) {
			if (scanByColumn) {
				for (int col = cr.getMinColumn(); col <= cr.getMaxColumn(); col++) {

					if (cr.isColumn()) {
						tempRange = new CellRange(app, col, -1);
						tempRange.setActualRange();
					} else {
						tempRange = new CellRange(app, col, cr.getMinRow(),
								col, cr.getMaxRow());
					}
					ArrayList<CellRange> tempList = new ArrayList<CellRange>();
					tempList.add(tempRange);
					tempGeo.add(createList(tempList, true, copyByValue, false,
							false, null, addToConstruction));
				}

			} else {

				for (int row = cr.getMinRow(); row <= cr.getMaxRow(); row++) {

					if (cr.isRow()) {
						tempRange = new CellRange(app, -1, row);
						tempRange.setActualRange();
					} else {
						tempRange = new CellRange(app, cr.getMinColumn(), row,
								cr.getMaxColumn(), row);
					}
					ArrayList<CellRange> tempList = new ArrayList<CellRange>();
					tempList.add(tempRange);
					tempGeo.add(createList(tempList, true, copyByValue, false,
							false, null, addToConstruction));
				}
			}
		}

		cons.setSuppressLabelCreation(oldSuppress);
		return tempGeo;
	}

	/**
	 * Creates a GeoPolyLine out of points constructed from the spreadsheet
	 * cells found in rangeList. Uses these defaults: no sorting, no undo point
	 * 
	 * @param rangeList
	 * @param byValue
	 * @param leftToRight
	 * @return
	 */
	public GeoElement createPolyLine(ArrayList<CellRange> rangeList,
			boolean byValue, boolean leftToRight) {
		return createPolyLine(rangeList, byValue, leftToRight, false, false);
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
	public GeoElement createPolyLine(ArrayList<CellRange> rangeList,
			boolean byValue, boolean leftToRight, boolean isSorted,
			boolean doStoreUndo) {

		boolean doCreateFreePoints = true;
		GeoList list = createPointGeoList(rangeList, byValue, leftToRight,
				isSorted, doStoreUndo, doCreateFreePoints);
		GeoElement ret;
		if (list != null && list.size() > 1 && list.get(0).isGeoElement3D()) {
			ret = list.getKernel().getManager3D().PolyLine3D(null, list)[0];
		} else {
			AlgoPolyLine al = new AlgoPolyLine(cons, list);
			ret = al.getOutput(0);
			ret.setLabel(null);
		}

		// need it in XML - used by Create Polyline tool, so don't want this
		// line
		// cons.removeFromConstructionList(al);


		return ret;
	}

	static class PointDimension {
		boolean doHorizontalPairs;
		int c1;
		int c2;
		int r1;
		int r2;
		int r3 = -1;
		int c3 = -1;
	}

	/**
	 * Given a cell range to converted into a point list, this determines if
	 * pairs are joined vertically or horizontally and gets the row column
	 * indices needed to traverse the cells.
	 */
	private static void getPointListDimensions(ArrayList<CellRange> rangeList,
			PointDimension pd) {

		pd.doHorizontalPairs = true;

		// note: we assume that rangeList has passed the
		// isCreatePointListPossible() test

		// CASE 1: selection is contiguous and 2D
		if (rangeList.size() == 1) {

			pd.doHorizontalPairs = rangeList.get(0).getWidth() == 2
					|| (rangeList.get(0).getWidth() == 3
							&& rangeList.get(0).getHeight() != 2);
			pd.c1 = rangeList.get(0).getMinColumn();
			pd.c2 = rangeList.get(0).getMaxColumn();
			pd.r1 = rangeList.get(0).getMinRow();
			pd.r2 = rangeList.get(0).getMaxRow();

			if (rangeList.get(0).getWidth() == 3
					&& pd.doHorizontalPairs) {
				pd.c2 = pd.c1 + 1;
				pd.c3 = pd.c1 + 2;
			} else if (rangeList.get(0).getHeight() == 3
					&& !pd.doHorizontalPairs) {
				pd.r2 = pd.r1 + 1;
				pd.r3 = pd.r1 + 2;
			}



			// CASE 2: non-contiguous with two ranges (either single row or
			// single column)
		} else {

			if (rangeList.get(0).getWidth() == 1
					&& rangeList.get(1).getWidth() == 1) {
				pd.doHorizontalPairs = true;
				// we are traversing down columns. so get min and max column
				// indices
				pd.c1 = Math.min(rangeList.get(0).getMinColumn(), rangeList
						.get(1).getMinColumn());
				pd.c2 = Math.max(rangeList.get(0).getMaxColumn(), rangeList
						.get(1).getMaxColumn());
				// but get the max-min and min-max row indices in case the
				// columns don't line up
				pd.r1 = Math.max(rangeList.get(0).getMinRow(), rangeList.get(1)
						.getMinRow());
				pd.r2 = Math.min(rangeList.get(0).getMaxRow(), rangeList.get(1)
						.getMaxRow());

			} else {
				pd.doHorizontalPairs = true;
				// we are traversing across rows. so get min and max row indices
				pd.r1 = Math.min(rangeList.get(0).getMinRow(), rangeList.get(1)
						.getMinRow());
				pd.r2 = Math.max(rangeList.get(0).getMaxRow(), rangeList.get(1)
						.getMaxRow());
				// but get the max-min and min-max column indices in case the
				// rows don't line up
				pd.c1 = Math.max(rangeList.get(0).getMinColumn(), rangeList
						.get(1).getMinColumn());
				pd.c2 = Math.min(rangeList.get(0).getMaxColumn(), rangeList
						.get(1).getMaxColumn());

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
	public GeoList createPointGeoList(ArrayList<CellRange> rangeList,
			boolean byValue, boolean leftToRight, boolean isSorted,
			boolean doStoreUndo, boolean doCreateFreePoints) {

		// get the orientation and dimensions of the list
		PointDimension pd = new PointDimension();
		getPointListDimensions(rangeList, pd);



		// build the string
		ArrayList<GeoElementND> list = new ArrayList<GeoElementND>();

		try {
			GeoElement xCoord, yCoord, zCoord;

			if (pd.doHorizontalPairs) {
				for (int i = pd.r1; i <= pd.r2; ++i) {

					xCoord = RelativeCopy.getValue(app, pd.c1, i);
					yCoord = RelativeCopy.getValue(app, pd.c2, i);
					if (pd.c3 < 0) {
						createPoint(xCoord, yCoord, byValue, leftToRight,
							doCreateFreePoints, list);
					} else {
						zCoord = RelativeCopy.getValue(app, pd.c3, i);
						createPoint3D(xCoord, yCoord, zCoord, byValue,
								leftToRight, doCreateFreePoints, list);
					}

				}

			} else { // vertical pairs
				for (int i = pd.c1; i <= pd.c2; ++i) {
					xCoord = RelativeCopy.getValue(app, i, pd.r1);
					yCoord = RelativeCopy.getValue(app, i, pd.r2);
					if (pd.r3 < 0) {
					createPoint(xCoord, yCoord, byValue, leftToRight,
							doCreateFreePoints, list);
					} else {
						zCoord = RelativeCopy.getValue(app, pd.r3, i);
						createPoint3D(xCoord, yCoord, zCoord, byValue,
								leftToRight, doCreateFreePoints, list);
					}
				}
			}

			// System.out.println(list.toString());
		}

		catch (Exception ex) {
			Log.debug(
					"Creating list of points expression failed with exception "
					+ ex);
		}

		AlgoDependentList dl = new AlgoDependentList(cons, list, false);
		cons.removeFromConstructionList(dl);
		return (GeoList) dl.getGeoElements()[0];

	}

	private void createPoint(GeoElement x, GeoElement y,
			boolean byValue, boolean leftToRight, boolean doCreateFreePoints,
			ArrayList<GeoElementND> list) {
		Kernel kernel = cons.getKernel();
		GeoElement xCoord = leftToRight ? x : y;
		GeoElement yCoord = leftToRight ? y : x;
		// don't process the point if either coordinate is null or
		// non-numeric,
		if (xCoord == null || yCoord == null || !xCoord.isGeoNumeric()
				|| !yCoord.isGeoNumeric())
			return;

		GeoPoint geoPoint;
		AlgoDependentPoint pointAlgo = null;

		if (byValue) {
			geoPoint = new GeoPoint(cons, ((GeoNumeric) xCoord).getDouble(),
					((GeoNumeric) yCoord).getDouble(), 1.0);
		} else {

			MyVecNode vec = new MyVecNode(kernel, xCoord, yCoord);
			ExpressionNode point = new ExpressionNode(kernel, vec,
					Operation.NO_OPERATION, null);
			point.setForcePoint();

			pointAlgo = new AlgoDependentPoint(cons, point, false);

			geoPoint = (GeoPoint) pointAlgo.getGeoElements()[0];

		}

		if (doCreateFreePoints) {
			// make sure points are independent of list (and so
			// draggable)
			geoPoint.setLabel(null);
		} else {
			if (pointAlgo != null)
				cons.removeFromConstructionList(pointAlgo);
		}

		list.add(geoPoint);

		if (yCoord.isAngle() || xCoord.isAngle())
			geoPoint.setPolar();

	}

	private void createPoint3D(GeoElement x, GeoElement y,
			GeoElement zCoord, boolean byValue, boolean leftToRight,
			boolean doCreateFreePoints, ArrayList<GeoElementND> list) {
		Kernel kernel = cons.getKernel();
		GeoElement xCoord = leftToRight ? x : y;
		GeoElement yCoord = leftToRight ? y : x;
		// don't process the point if either coordinate is null or
		// non-numeric,
		if (xCoord == null || yCoord == null || !xCoord.isGeoNumeric()
				|| !yCoord.isGeoNumeric() || zCoord == null
				|| !zCoord.isGeoNumeric())
			return;

		GeoPointND geoPoint;

		if (byValue) {
			geoPoint = kernel.getManager3D().Point3D(
					((GeoNumeric) xCoord).getDouble(),
					((GeoNumeric) yCoord).getDouble(),
					((GeoNumeric) zCoord).getDouble(), false);
		} else {

			MyVec3DNode vec = new MyVec3DNode(kernel,
					leftToRight ? xCoord : yCoord,
					leftToRight ? yCoord : xCoord, zCoord);
			ExpressionNode point = new ExpressionNode(kernel, vec,
					Operation.NO_OPERATION, null);
			point.setForcePoint();

			geoPoint = kernel.getManager3D().DependentPoint3D(point,
					doCreateFreePoints);


		}

		if (doCreateFreePoints) {
			// make sure points are independent of list (and so
			// draggable)
			geoPoint.setLabel(null);
		}

		list.add(geoPoint);

		// if (yCoord.isAngle() || xCoord.isAngle())
		// geoPoint.setPolar();

	}

	public String[] getPointListTitles(ArrayList<CellRange> rangeList,
			boolean leftToRight) {

		String[] title = new String[2];

		// return null titles if data source is a point list
		if (rangeList.size() == 1 && rangeList.get(0).isPointList())
			return title;

		// get the orientation and dimensions of the list
		PointDimension pd = new PointDimension();
		getPointListDimensions(rangeList, pd);

		if (pd.doHorizontalPairs) {
			// handle first title
			if (RelativeCopy.getValue(app, pd.c1, pd.r1).isGeoText()) {
				// header cell text
				title[0] = ((GeoText) RelativeCopy.getValue(app, pd.c1, pd.r1))
						.getTextString();
			} else if (pd.r1 == 0) {
				// column name
				title[0] = getCellRangeString(new CellRange(app, pd.c1, -1,
						pd.c1, -1));
			} else {
				// cell range
				title[0] = getCellRangeString(new CellRange(app, pd.c1, pd.r1,
						pd.c1, pd.r2));
			}

			// handle second title
			if (RelativeCopy.getValue(app, pd.c2, pd.r1).isGeoText()) {
				// header cell text
				title[1] = ((GeoText) RelativeCopy.getValue(app, pd.c2, pd.r1))
						.getTextString();
			} else if (pd.r1 == 0) {
				// column name
				title[1] = getCellRangeString(new CellRange(app, pd.c2, -1,
						pd.c2, -1));
			} else {
				// cell range
				title[1] = getCellRangeString(new CellRange(app, pd.c2, pd.r1,
						pd.c2, pd.r2));
			}

		} else { // vertical pairs

			// handle first title
			if (RelativeCopy.getValue(app, pd.c1, pd.r1).isGeoText()) {
				// header cell text
				title[0] = ((GeoText) RelativeCopy.getValue(app, pd.c1, pd.r1))
						.getTextString();
			} else if (pd.c1 == 0) {
				// row name
				title[0] = getCellRangeString(new CellRange(app, -1, pd.r1, -1,
						pd.r1));
			} else {
				// cell range
				title[0] = getCellRangeString(new CellRange(app, pd.c1, pd.r1,
						pd.c2, pd.r1));
			}

			// handle second title
			if (RelativeCopy.getValue(app, pd.c1, pd.r2).isGeoText()) {
				// header cell text
				title[1] = ((GeoText) RelativeCopy.getValue(app, pd.c1, pd.r2))
						.getTextString();
			} else if (pd.c1 == 0) {
				// row name
				title[1] = getCellRangeString(new CellRange(app, -1, pd.r2, -1,
						pd.r2));
			} else {
				// cell range
				title[1] = getCellRangeString(new CellRange(app, pd.c1, pd.r2,
						pd.c2, pd.r2));
			}

		}

		if (!leftToRight) {
			String temp = title[0];
			title[0] = title[1];
			title[1] = temp;
		}
		return title;
	}

	public String[] getColumnTitles(ArrayList<CellRange> rangeList) {

		ArrayList<String> titleList = new ArrayList<String>();

		for (CellRange cr : rangeList) {
			// get column header or column name from each column in this cell
			// range
			for (int col = cr.getMinColumn(); col <= cr.getMaxColumn(); col++) {
				if (RelativeCopy.getValue(app, col, 0) != null
						&& RelativeCopy.getValue(app, col, 0).isGeoText()) {
					// use header cell text
					titleList
							.add(((GeoText) RelativeCopy.getValue(app, col, 0))
									.getTextString());
				} else {
					// use column name
					titleList.add(getCellRangeString(new CellRange(app, col,
							-1, col, -1)));
				}
			}
		}

		String[] title = new String[titleList.size()];
		title = titleList.toArray(title);
		return title;
	}

	/**
	 * Creates a GeoList from the cells in an array of cellranges. Empty cells
	 * are ignored. Uses these defaults: do not create undo point, do not sort,
	 * do not filter by geo type, set a label.
	 */
	public GeoElement createList(ArrayList<CellRange> rangeList,
			boolean scanByColumn, boolean copyByValue) {
		return createList(rangeList, scanByColumn, copyByValue, false, false,
				null, true);
	}

	/**
	 * Creates a GeoList from the cells in an array of CellRange. Empty cells
	 * are ignored
	 */
	public GeoList createList(ArrayList<CellRange> rangeList,
			boolean scanByColumn, boolean copyByValue, boolean isSorted,
			boolean doStoreUndo, GeoClass geoTypeFilter, boolean setLabel) {

		GeoList geoList = null;
		ArrayList<GeoElementND> list = null;
		if (copyByValue)
			geoList = new GeoList(cons);
		else
			list = new ArrayList<GeoElementND>();

		ArrayList<GPoint> cellList = new ArrayList<GPoint>();

		// temporary fix for catching duplicate cells caused by ctrl-seelct
		// will not be needed when sorting of cells by row/column is done
		HashSet<GPoint> usedCells = new HashSet<GPoint>();

		try {

			// create cellList: this holds a list of cell index pairs for the
			// entire range
			for (CellRange cr : rangeList) {
				cellList.addAll(cr.toCellList(scanByColumn));
			}

			// iterate through the cells and add their contents to the
			// expression string
			for (GPoint cell : cellList) {
				if (!usedCells.contains(cell)) {
					GeoElement geo = RelativeCopy.getValue(app, cell.x, cell.y);
					if (geo != null
							&& (geoTypeFilter == null || geo.getGeoClassType() == geoTypeFilter)) {
						if (copyByValue)
							geoList.add(geo.copy());
						else
							list.add(geo);
					}
					usedCells.add(cell);
				}
			}

			// if !copyByValue convert dependent GeoList from geos collected
			// above
			if (!copyByValue) {
				AlgoDependentList algo = new AlgoDependentList(cons, list,
						false);
				if (!setLabel) {
					cons.removeFromConstructionList(algo);
				}
				geoList = (GeoList) algo.getGeoElements()[0];
			}

			if (isSorted) {
				AlgoSort algo = new AlgoSort(cons, geoList);
				cons.removeFromConstructionList(algo);
				geoList = (GeoList) algo.getGeoElements()[0];
			}

		} catch (Exception ex) {
			Log.debug("Creating list failed with exception " + ex);
		}

		if (doStoreUndo) {
			app.storeUndoInfo();
		}

		if (setLabel) {
			geoList.setLabel(null);
		}

		if (geoList != null) {
			return geoList;
		}
		return null;
	}

	/** Creates a list from all cells in a spreadsheet column */
	public GeoElement createListFromColumn(int column, boolean copyByValue,
			boolean isSorted, boolean storeUndoInfo, GeoClass geoTypeFilter,
			boolean addToConstruction) {

		ArrayList<CellRange> rangeList = new ArrayList<CellRange>();
		CellRange cr = new CellRange(app, column, -1);
		cr.setActualRange();
		rangeList.add(cr);

		return createList(rangeList, true, copyByValue, isSorted,
				storeUndoInfo, geoTypeFilter, addToConstruction);
	}

	/** Returns true if all cell ranges in the list are columns */
	public boolean isAllColumns(ArrayList<CellRange> rangeList) {
		boolean isAllColumns = true;
		for (CellRange cr : rangeList) {
			if (!cr.isColumn()) {
				isAllColumns = false;
			}
		}
		return isAllColumns;
	}

	/**
	 * Creates a string expression for a matrix where each sub-list is a list of
	 * cells in the columns spanned by the range list
	 */
	public String createColumnMatrixExpression(ArrayList<CellRange> rangeList,
			boolean copyByValue, boolean addToConstruction) {
		GeoElement tempGeo;
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (CellRange cr : rangeList) {
			for (int col = cr.getMinColumn(); col <= cr.getMaxColumn(); col++) {
				tempGeo = createListFromColumn(col, copyByValue, false, false,
						GeoClass.NUMERIC, addToConstruction);
				sb.append(tempGeo
						.getDefinition(StringTemplate.defaultTemplate));
				sb.append(",");
				tempGeo.remove();
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("}");

		return sb.toString();
	}

	/**
	 * Creates a string expression for a matrix formed by the cell range with
	 * upper left corner (column1, row1) and lower right corner (column2, row2).
	 * If transpose = true then the matrix is the formed by interchanging rows
	 * with columns
	 */
	public String createMatrixExpression(int column1, int column2, int row1,
			int row2, boolean copyByValue, boolean transpose) {

		GeoElement v2;
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		StringTemplate tpl = StringTemplate.defaultTemplate;
		if (!transpose) {
			for (int j = row1; j <= row2; ++j) {
				sb.append("{");
				for (int i = column1; i <= column2; ++i) {
					v2 = RelativeCopy.getValue(app, i, j);
					if (v2 != null) {
						if (copyByValue) {
							sb.append(v2.toDefinedValueString(tpl));
						} else {
							sb.append(v2.getLabel(tpl));
						}
						sb.append(',');
					} else {
						app.showError(loc.getPlain("CellAisNotDefined",
								GeoElementSpreadsheet.getSpreadsheetCellName(i,
										j)));
						return null;
					}
				}
				sb.deleteCharAt(sb.length() - 1); // remove trailing comma
				sb.append("},");
			}

		} else {
			for (int j = column1; j <= column2; ++j) {
				// if (selected.length > j && ! selected[j]) continue;
				sb.append("{");
				for (int i = row1; i <= row2; ++i) {
					v2 = RelativeCopy.getValue(app, j, i);
					if (v2 != null) {
						if (copyByValue) {
							sb.append(v2.toDefinedValueString(tpl));
						} else {
							sb.append(v2.getLabel(tpl));
						}
						sb.append(',');
					} else {
						app.showError(loc.getPlain("CellAisNotDefined",
								GeoElementSpreadsheet.getSpreadsheetCellName(i,
										j)));
						return null;
					}
				}
				sb.deleteCharAt(sb.length() - 1); // remove trailing comma
				sb.append("},");
			}
		}

		sb.deleteCharAt(sb.length() - 1); // remove trailing comma
		sb.append('}');

		// Application.debug(sb.toString());
		return sb.toString();
	}

	/**
	 * Creates a Matrix geo from the cell range with upper left corner (column1,
	 * row1) and lower right corner (column2, row2). NOTE: An undo point is not
	 * created.
	 * 
	 * @param column1
	 * @param column2
	 * @param row1
	 * @param row2
	 * @param copyByValue
	 * @return
	 */
	public GeoElementND createMatrix(int column1, int column2, int row1,
			int row2, boolean copyByValue) {
		return createMatrix(column1, column2, row1, row2, copyByValue, false);
	}

	/**
	 * Creates a Matrix geo from the cell range with upper left corner (column1,
	 * row1) and lower right corner (column2, row2). If transpose = true then
	 * the matrix is the formed by interchanging rows with columns. NOTE: An
	 * undo point is not created.
	 * 
	 * @param column1
	 * @param column2
	 * @param row1
	 * @param row2
	 * @param copyByValue
	 * @param transpose
	 * @return
	 */
	public GeoElementND createMatrix(int column1, int column2, int row1,
			int row2, boolean copyByValue, boolean transpose) {

		GeoElementND[] geos = null;
		String expr = null;

		try {
			expr = createMatrixExpression(column1, column2, row1, row2,
					copyByValue, transpose);
			// Application.debug(expr);
			geos = app.getKernel().getAlgebraProcessor()
					.processAlgebraCommandNoExceptions(expr, false);
		} catch (Exception ex) {
			Log.debug("creating matrix failed " + expr);
			ex.printStackTrace();
		}

		if (geos != null) {
			return geos[0];
		}
		return null;
	}

	/**
	 * Creates a TableText geo from the cell range with upper left corner
	 * (column1, row1) and lower right corner (column2, row2). If transpose =
	 * true then the TableText matrix is the formed by interchanging rows with
	 * columns. NOTE: An undo point is not created.
	 * 
	 * @param column1
	 * @param column2
	 * @param row1
	 * @param row2
	 * @param copyByValue
	 * @return
	 */
	public GeoElementND createTableText(int column1, int column2, int row1,
			int row2, boolean copyByValue, boolean transpose) {

		GeoElementND[] geos = null;
		StringBuilder text = new StringBuilder();

		try {
			text.append("TableText[");
			text.append(createMatrixExpression(column1, column2, row1, row2,
					copyByValue, transpose));
			text.append(",\"|_\"]");

			// Application.debug(text);
			geos = app.getKernel().getAlgebraProcessor()
					.processAlgebraCommandNoExceptions(text.toString(), false);

		} catch (Exception ex) {
			Log.debug("creating TableText failed " + text);
			ex.printStackTrace();
		}

		if (geos != null) {
			return geos[0];
		}
		return null;
	}

	// ===================================================
	// Insert Rows/Columns
	// ===================================================

	public enum Direction {
		/** left */
		Left,
		/** right */
		Right,
		/** up */
		Up,
		/** down */
		Down
	}

	/**
	 * @param column1
	 *            minimum selected column
	 * @param column2
	 *            maximum selected column
	 * @param insertLeft
	 *            true = insert left of column1, false = insert right of column2
	 */
	public void insertColumn(int column1, int column2, boolean insertLeft) {

		if (insertLeft) {
			shiftColumnsRight(column1);
			table.getCellFormatHandler().shiftFormats(column1, 1,
					Direction.Right);
		} else {
			shiftColumnsRight(column2 + 1);
			table.getCellFormatHandler().shiftFormats(column2 + 1, 1,
					Direction.Right);
		}
		table.repaintAll();
	}

	/**
	 * @param column1
	 *            minimum selected column
	 * @param column2
	 *            maximum selected column
	 */
	public void deleteColumns(int column1, int column2) {

		table.getCopyPasteCut().delete(column1, 0, column2,
				tableModel.getHighestUsedRow());
		shiftColumnsLeft(column2 + 1, column2 - column1 + 1);
		table.getCellFormatHandler().shiftFormats(column2 + 1,
				column2 - column1 + 1, Direction.Left);
		table.repaintAll();
	}

	private void shiftColumnsRight(int startColumn) {

		boolean succ = false;
		int maxColumn = tableModel.getHighestUsedColumn();
		int maxRow = tableModel.getHighestUsedRow();

		for (int column = maxColumn; column >= startColumn; --column) {
			for (int row = 0; row <= maxRow; ++row) {
				GeoElement geo = RelativeCopy.getValue(app, column, row);
				if (geo == null)
					continue;

				String newLabel = GeoElementSpreadsheet.getSpreadsheetCellName(
						column + 1, row);
				geo.setLabel(newLabel);
				succ = true;
			}
		}

		if (succ) {
			app.storeUndoInfo();
		}

	}

	private void shiftColumnsLeft(int startColumn, int shiftAmount) {

		boolean succ = false;
		int maxColumn = tableModel.getHighestUsedColumn();
		int maxRow = tableModel.getHighestUsedRow();

		for (int column = startColumn; column <= maxColumn; ++column) {
			for (int row = 0; row <= maxRow; ++row) {
				GeoElement geo = RelativeCopy.getValue(app, column, row);
				if (geo == null)
					continue;

				String newLabel = GeoElementSpreadsheet.getSpreadsheetCellName(
						column - shiftAmount, row);
				geo.setLabel(newLabel);
				succ = true;
			}
		}

		if (succ) {
			app.storeUndoInfo();
		}

	}

	/**
	 * @param row1
	 *            minimum selected row
	 * @param row2
	 *            maximum selected row
	 * @param insertAbove
	 *            true = insert above row1, false = insert below row2
	 */
	public void insertRow(int row1, int row2, boolean insertAbove) {

		if (insertAbove) {
			shiftRowsDown(row1);
			table.getCellFormatHandler().shiftFormats(row1, 1, Direction.Down);
		} else {
			shiftRowsDown(row2 + 1);
			table.getCellFormatHandler().shiftFormats(row2 + 1, 1,
					Direction.Down);
		}
		table.repaintAll();
	}

	/**
	 * @param row1
	 *            minimum selected row
	 * @param row2
	 *            maximum selected row
	 */
	public void deleteRows(int row1, int row2) {

		table.getCopyPasteCut().delete(0, row1,
				tableModel.getHighestUsedColumn(), row2);
		shiftRowsUp(row2 + 1, row2 - row1 + 1);
		table.getCellFormatHandler().shiftFormats(row2 + 1, row2 - row1 + 1,
				Direction.Up);
		table.repaintAll();
	}

	private void shiftRowsDown(int startRow) {

		int maxColumn = tableModel.getHighestUsedColumn();
		int maxRow = tableModel.getHighestUsedRow();
		boolean succ = false;

		for (int row = maxRow; row >= startRow; --row) {
			for (int column = 0; column <= maxColumn; ++column) {
				GeoElement geo = RelativeCopy.getValue(app, column, row);
				if (geo == null)
					continue;
				String newLabel = GeoElementSpreadsheet.getSpreadsheetCellName(
						column, row + 1);
				geo.setLabel(newLabel);
				succ = true;
			}
		}

		if (succ)
			app.storeUndoInfo();
	}

	private void shiftRowsUp(int startRow, int shiftAmount) {

		boolean succ = false;
		int maxColumn = tableModel.getHighestUsedColumn();
		int maxRow = tableModel.getHighestUsedRow();

		for (int row = startRow; row <= maxRow; ++row) {
			for (int column = 0; column <= maxColumn; ++column) {
				GeoElement geo = RelativeCopy.getValue(app, column, row);
				if (geo == null)
					continue;
				String newLabel = GeoElementSpreadsheet.getSpreadsheetCellName(
						column, row - shiftAmount);
				geo.setLabel(newLabel);
				succ = true;
			}
		}

		if (succ) {
			app.storeUndoInfo();
		}

	}

	/**
	 * Creates an operation table.
	 */
	public void createOperationTable(CellRange cr) {

		int r1 = cr.getMinRow();
		int c1 = cr.getMinColumn();
		String text = "";
		GeoElementND[] geos;
		GeoFunctionNVar fcn = (GeoFunctionNVar) RelativeCopy.getValue(app, c1,
				r1);

		for (int r = r1 + 1; r <= cr.getMaxRow(); ++r) {
			for (int c = c1 + 1; c <= cr.getMaxColumn(); ++c) {
				// System.out.println(AbstractGeoElementSpreadsheet.getSpreadsheetCellName(c,
				// r) + ": " + text);

				text = GeoElementSpreadsheet.getSpreadsheetCellName(c, r) + "="
						+ fcn.getLabel(StringTemplate.defaultTemplate) + "(";
				text += GeoElementSpreadsheet.getSpreadsheetCellName(c1, r);
				text += ",";
				text += GeoElementSpreadsheet.getSpreadsheetCellName(c, r1);
				text += ")";

				geos = app.getKernel().getAlgebraProcessor()
						.processAlgebraCommandNoExceptions(text, false);

				// geos[0].setLabel(AbstractGeoElementSpreadsheet.getSpreadsheetCellName(c,
				// r));
				geos[0].setAuxiliaryObject(true);
			}
		}
	}

	// Experimental ---- merging ctrl-selected cells

	// private static void consolidateRangeList(ArrayList<CellRange> rangeList)
	// {
	//
	// ArrayList<ArrayList<GPoint>> matrix = new ArrayList<ArrayList<GPoint>>();
	// int minRow = rangeList.get(0).getMinRow();
	// int maxRow = rangeList.get(0).getMaxRow();
	// int minColumn = rangeList.get(0).getMinColumn();
	// int maxColumn = rangeList.get(0).getMaxColumn();
	//
	// for (CellRange cr : rangeList) {
	//
	// minColumn = Math.min(cr.getMinColumn(), minColumn);
	// maxColumn = Math.max(cr.getMaxColumn(), maxColumn);
	// minRow = Math.min(cr.getMinRow(), minRow);
	// maxRow = Math.max(cr.getMaxRow(), maxRow);
	//
	// // create matrix of cells from all ranges in the list
	// for (int col = cr.getMinColumn(); col <= cr.getMaxColumn(); col++) {
	//
	// // add columns from this cell range to the matrix
	// if (matrix.get(col) == null) {
	// matrix.add(col, new ArrayList<GPoint>());
	// matrix.get(col).add(
	// new GPoint(cr.getMinColumn(), cr.getMaxColumn()));
	// } else {
	// // Point p = matrix.get(col).get(1);
	// // if(cr.getMinColumn()>)
	// // insertPoint(matrix, new Point(new
	// // Point(cr.getMinColumn(),cr.getMaxColumn())));
	// }
	// }
	//
	// // convert our matrix to a CellRange list
	// for (int col = minColumn; col <= maxColumn; col++) {
	// if (matrix.contains(col)) {
	// // ????
	// }
	// }
	// }
	// }

	public String getCellRangeString(CellRange range) {
		return getCellRangeString(range, true);
	}

	public String getCellRangeString(CellRange range, boolean onlyFirstRowColumn) {

		String s = "";

		if (range.isColumn()) {
			s = loc.getCommand("Column")
					+ " "
					+ GeoElementSpreadsheet.getSpreadsheetColumnName(range
							.getMinColumn());
			if (!onlyFirstRowColumn && !range.is1D()) {
				s += " : "
						+ loc.getCommand("Column")
						+ " "
						+ GeoElementSpreadsheet.getSpreadsheetColumnName(range
								.getMaxColumn());
			}

		} else if (range.isRow()) {
			s = loc.getCommand("Row") + " " + (range.getMinRow() + 1);

			if (!onlyFirstRowColumn && !range.is1D()) {
				s += " : " + loc.getCommand("Row") + " "
						+ (range.getMaxRow() + 1);
			}

		} else {
			s = GeoElementSpreadsheet.getSpreadsheetCellName(
					range.getMinColumn(), range.getMinRow());
			s += ":";
			s += GeoElementSpreadsheet.getSpreadsheetCellName(
					range.getMaxColumn(), range.getMaxRow());
		}

		return s;
	}

	public String getCellRangeString(ArrayList<CellRange> list) {
		// if (list == null) {
		// return "";
		// }
		StringBuilder sb = new StringBuilder();
		for (CellRange cr : list) {
			// cr.debug();
			sb.append(getCellRangeString(cr, false));
			sb.append(", ");
		}
		sb.deleteCharAt(sb.lastIndexOf(", "));

		return sb.toString();
	}

}
