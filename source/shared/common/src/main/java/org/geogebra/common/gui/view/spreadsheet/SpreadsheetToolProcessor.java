/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details'
 */

package org.geogebra.common.gui.view.spreadsheet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentList;
import org.geogebra.common.kernel.algos.AlgoDependentPoint;
import org.geogebra.common.kernel.algos.AlgoPolyLine;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.spreadsheet.style.CellFormatInterface;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;

/**
 * Creates objects (lists, matrices, polylines) from ranges of spreadsheet cells.
 */
public final class SpreadsheetToolProcessor {

	@Weak
	private final App app;
	private final SpreadsheetTableModel tableModel;
	@Weak
	private final Construction cons;
	@Weak
	private final Localization loc;
	private final CellFormatInterface cellFormat;

	private static final class PointDimension {
		boolean doHorizontalPairs;
		int c1;
		int c2;
		int r1;
		int r2;
		int r3 = -1;
		int c3 = -1;
	}

	/**
	 * Creates processor for spreadsheet tools.
	 * @param app application
	 * @param cellFormat cell format
	 */
	public SpreadsheetToolProcessor(App app, CellFormatInterface cellFormat) {
		this.app = app;
		this.tableModel = app.getSpreadsheetTableModel();
		this.cons = app.getKernel().getConstruction();
		this.loc = app.getLocalization();
		this.cellFormat = cellFormat;
	}

	/**
	 * Creates a TableText geo from the cell range with upper left corner
	 * (column1, row1) and lower right corner (column2, row2). If transpose =
	 * true then the TableText matrix is the formed by interchanging rows with
	 * columns. NOTE: An undo point is not created.
	 *
	 * @param column1
	 *            min column
	 * @param column2
	 *            max column
	 * @param row1
	 *            min row
	 * @param row2
	 *            max row
	 * @param copyByValue
	 *            whether to use cells only as values
	 * @param transpose
	 *            whether to transpose the table
	 * @return table (using TableText)
	 */
	public GeoElementND createTableText(int column1, int column2, int row1,
			int row2, boolean copyByValue, boolean transpose) {

		GeoElementND[] geos = null;
		StringBuilder text = new StringBuilder();

		try {
			text.append("TableText[");
			text.append(createMatrixExpression(column1, column2, row1, row2,
					copyByValue, transpose));
			text.append(",\"|_");
			// formatting eg "lcr"
			text.append(getAlignmentString(column1, column2, row1, row2,
					transpose));
			text.append("\"]");
			geos = app.getKernel().getAlgebraProcessor()
					.processAlgebraCommandNoExceptions(text.toString(), false);

		} catch (RuntimeException ex) {
			Log.debug("creating TableText failed " + text);
			Log.debug(ex);
		}

		if (geos != null) {
			return geos[0];
		}
		return null;
	}

	/**
	 * Creates a Matrix geo from the cell range with upper left corner (column1,
	 * row1) and lower right corner (column2, row2). If transpose = true then
	 * the matrix is the formed by interchanging rows with columns. NOTE: An
	 * undo point is not created.
	 *
	 * @param column1
	 *            min column
	 * @param column2
	 *            max column
	 * @param row1
	 *            min row
	 * @param row2
	 *            max row
	 * @param copyByValue
	 *            whether to use cells only as values
	 * @param transpose
	 *            whether to transpose the matrix
	 * @return matrix
	 */
	public GeoElementND createMatrix(int column1, int column2, int row1,
			int row2, boolean copyByValue, boolean transpose) {

		GeoElementND[] geos = null;
		String expr = null;

		try {
			expr = createMatrixExpression(column1, column2, row1, row2,
					copyByValue, transpose);
			geos = app.getKernel().getAlgebraProcessor()
					.processAlgebraCommandNoExceptions(expr, false);
		} catch (RuntimeException ex) {
			Log.debug("creating matrix failed " + expr);
			Log.debug(ex);
		}

		if (geos != null) {
			return geos[0];
		}
		return null;
	}

	/*
	 * return string like "llcr" for the alignment of each column (row)
	 *
	 * just take alignment from first cell in each column (row)
	 *
	 */
	private String getAlignmentString(int column1, int column2, int row1,
			int row2, boolean transpose) {

		StringBuilder sb = new StringBuilder();

		if (!transpose) {
			for (int i = column1; i <= column2; ++i) {
				sb.append(alignmentChar(i, row1, cellFormat));
			}

		} else {
			for (int i = row1; i <= row2; ++i) {
				sb.append(alignmentChar(column1, i, cellFormat));
			}
		}

		return sb.toString();
	}

	// ===================================================
	// Insert Rows/Columns
	// ===================================================

	private static char alignmentChar(int col, int row,
			CellFormatInterface formatHandler) {
		Object alignment = formatHandler.getCellFormat(col, row,
				CellFormat.FORMAT_ALIGN);

		int alignmentI = CellFormat.ALIGN_LEFT;

		if (alignment instanceof Integer) {
			alignmentI = (Integer) alignment;
		}

		return CellFormat.getAlignmentString(alignmentI);
	}

	/**
	 * Creates a string expression for a matrix formed by the cell range with
	 * upper left corner (minColumn, minRow) and lower right corner (maxColumn, maxRow).
	 * If transpose = true then the matrix is the formed by interchanging rows
	 * with columns
	 *
	 * @param minColumn min column index
	 * @param maxColumn max column index
	 * @param minRow min row index
	 * @param maxRow max row index
	 * @param copyByValue whether to copy values (result is independent)
	 * @param transpose whether to transpose the matrix
	 *
	 * @return matrix definition
	 */
	public String createMatrixExpression(int minColumn, int maxColumn, int minRow,
			int maxRow, boolean copyByValue, boolean transpose) {

		GeoElement v2;
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		StringTemplate tpl = StringTemplate.defaultTemplate;
		if (!transpose) {
			for (int j = minRow; j <= maxRow; ++j) {
				sb.append("{");
				for (int i = minColumn; i <= maxColumn; ++i) {
					v2 = RelativeCopy.getValue(tableModel, i, j);
					if (v2 != null) {
						if (copyByValue) {
							sb.append(v2.toDefinedValueString(tpl));
						} else {
							sb.append(v2.getLabel(tpl));
						}
						sb.append(',');
					} else {
						app.showError(MyError.Errors.CellAisNotDefined.getError(loc,
								GeoElementSpreadsheet.getSpreadsheetCellName(i, j)));
						return null;
					}
				}
				sb.deleteCharAt(sb.length() - 1); // remove trailing comma
				sb.append("},");
			}

		} else {
			for (int j = minColumn; j <= maxColumn; ++j) {
				// if (selected.length > j && ! selected[j]) continue;
				sb.append("{");
				for (int i = minRow; i <= maxRow; ++i) {
					v2 = RelativeCopy.getValue(tableModel, j, i);
					if (v2 != null) {
						if (copyByValue) {
							sb.append(v2.toDefinedValueString(tpl));
						} else {
							sb.append(v2.getLabel(tpl));
						}
						sb.append(',');
					} else {
						app.showError(MyError.Errors.CellAisNotDefined.getError(loc,
								GeoElementSpreadsheet.getSpreadsheetCellName(i, j)));
						return null;
					}
				}
				sb.deleteCharAt(sb.length() - 1); // remove trailing comma
				sb.append("},");
			}
		}

		sb.deleteCharAt(sb.length() - 1); // remove trailing comma
		sb.append('}');
		return sb.toString();
	}

	/**
	 * Creates a GeoList from the cells in an array of tabular ranges. Empty cells
	 * are ignored
	 *
	 * @param rangeList list of cell ranges
	 * @param scanByColumn whether to iterate vertically first
	 * @param copyByValue whether to make independent copies
	 * @param geoTypeFilter filter for geos by type
	 * @param setLabel whether to label the result
	 * @return list
	 */
	public GeoList createList(List<TabularRange> rangeList,
			boolean scanByColumn, boolean copyByValue,
			GeoClass geoTypeFilter, boolean setLabel) {

		GeoList geoList = null;
		ArrayList<GeoElementND> list = null;
		if (copyByValue) {
			geoList = new GeoList(cons);
		} else {
			list = new ArrayList<>();
		}

		ArrayList<SpreadsheetCoords> cellList = new ArrayList<>();

		// temporary fix for catching duplicate cells caused by ctrl-select
		// will not be needed when sorting of cells by row/column is done
		HashSet<SpreadsheetCoords> usedCells = new HashSet<>();

		try {

			// create cellList: this holds a list of cell index pairs for the
			// entire range
			for (TabularRange range : rangeList) {
				cellList.addAll(range.toCellList(scanByColumn));
			}

			// iterate through the cells and add their contents to the
			// expression string
			for (SpreadsheetCoords cell : cellList) {
				if (!usedCells.contains(cell)) {
					GeoElement geo = RelativeCopy.getValue(tableModel, cell.column, cell.row);
					if (geo != null && (geoTypeFilter == null
							|| geo.getGeoClassType() == geoTypeFilter)) {
						if (copyByValue) {
							geoList.add(geo.copy());
						} else {
							list.add(geo);
						}
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

		} catch (RuntimeException ex) {
			Log.debug("Creating list failed with exception " + ex);
		}
		if (setLabel && geoList != null) {
			geoList.setLabel(null);
		}
		return geoList;
	}

	/**
	 * Creates a GeoList from the cells in an array of tabular ranges. Empty cells
	 * are ignored. Uses these defaults: do not create undo point, do not sort,
	 * do not filter by geo type, set a label.
	 * @param rangeList cell ranges
	 * @param scanByColumn whether to iterate vertically first
	 * @param copyByValue whether to create independent copies
	 * @return list
	 */
	public GeoElement createList(List<TabularRange> rangeList,
			boolean scanByColumn, boolean copyByValue) {
		return createList(rangeList, scanByColumn, copyByValue,
				null, true);
	}

	/**
	 * Creates a Matrix geo from the cell range with upper left corner (column1,
	 * row1) and lower right corner (column2, row2). NOTE: An undo point is not
	 * created.
	 *
	 * @param column1
	 *            min column
	 * @param column2
	 *            max column
	 * @param row1
	 *            min row
	 * @param row2
	 *            max row
	 * @param copyByValue
	 *            whether to use cells only as values
	 * @return matrix
	 */
	public GeoElementND createMatrix(int column1, int column2, int row1,
			int row2, boolean copyByValue) {
		return createMatrix(column1, column2, row1, row2, copyByValue, false);
	}

	/**
	 * Creates a GeoList containing points constructed from the spreadsheet
	 * cells found in rangeList. note: It is assumed that rangeList has passed
	 * the isCreatePointListPossible() test
	 *
	 * @param rangeList
	 *            selected ranges
	 * @param byValue
	 *            whether to use inputs as values
	 * @param leftToRight
	 *            whether to scan left to right
	 * @param doStoreUndo
	 *            whether to store undo
	 * @param doCreateFreePoints
	 *            if freePoints is true then a set of independent GeoPoints is
	 *            created in addition to the list
	 * @return GeoList
	 */
	public GeoList createPointGeoList(List<TabularRange> rangeList,
			boolean byValue, boolean leftToRight,
			boolean doStoreUndo, boolean doCreateFreePoints) {

		// get the orientation and dimensions of the list
		PointDimension pd = new PointDimension();
		getPointListDimensions(rangeList, pd);

		// build the string
		ArrayList<GeoElementND> list = new ArrayList<>();

		try {
			GeoElement xCoord, yCoord, zCoord;

			if (pd.doHorizontalPairs) {
				for (int i = pd.r1; i <= pd.r2; ++i) {

					xCoord = RelativeCopy.getValue(tableModel, pd.c1, i);
					yCoord = RelativeCopy.getValue(tableModel, pd.c2, i);
					if (pd.c3 < 0) {
						createPoint(xCoord, yCoord, byValue, leftToRight,
								doCreateFreePoints, list);
					} else {
						zCoord = RelativeCopy.getValue(tableModel, pd.c3, i);
						createPoint3D(xCoord, yCoord, zCoord, byValue,
								leftToRight, doCreateFreePoints, list);
					}

				}

			} else { // vertical pairs
				for (int i = pd.c1; i <= pd.c2; ++i) {
					xCoord = RelativeCopy.getValue(tableModel, i, pd.r1);
					yCoord = RelativeCopy.getValue(tableModel, i, pd.r2);
					if (pd.r3 < 0) {
						createPoint(xCoord, yCoord, byValue, leftToRight,
								doCreateFreePoints, list);
					} else {
						zCoord = RelativeCopy.getValue(tableModel, pd.r3, i);
						createPoint3D(xCoord, yCoord, zCoord, byValue,
								leftToRight, doCreateFreePoints, list);
					}
				}
			}
		} catch (RuntimeException ex) {
			Log.debug(
					"Creating list of points expression failed with exception "
							+ ex);
		}

		AlgoDependentList dl = new AlgoDependentList(cons, list, false);
		cons.removeFromConstructionList(dl);
		return (GeoList) dl.getGeoElements()[0];

	}

	private void createPoint(GeoElement x, GeoElement y, boolean byValue,
			boolean leftToRight, boolean doCreateFreePoints,
			ArrayList<GeoElementND> list) {
		Kernel kernel = cons.getKernel();
		GeoElement xCoord = leftToRight ? x : y;
		GeoElement yCoord = leftToRight ? y : x;
		// don't process the point if either coordinate is null or
		// non-numeric,
		if (xCoord == null || yCoord == null || !xCoord.isGeoNumeric()
				|| !yCoord.isGeoNumeric()) {
			return;
		}

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

			geoPoint = pointAlgo.getPoint();

		}

		if (doCreateFreePoints) {
			// make sure points are independent of list (and so
			// draggable)
			geoPoint.setLabel(null);
		} else {
			if (pointAlgo != null) {
				cons.removeFromConstructionList(pointAlgo);
			}
		}

		list.add(geoPoint);

		if (yCoord.isAngle() || xCoord.isAngle()) {
			geoPoint.setPolar();
		}

	}

	private void createPoint3D(GeoElement x, GeoElement y, GeoElement zCoord,
			boolean byValue, boolean leftToRight, boolean doCreateFreePoints,
			ArrayList<GeoElementND> list) {
		Kernel kernel = cons.getKernel();
		GeoElement xCoord = leftToRight ? x : y;
		GeoElement yCoord = leftToRight ? y : x;
		// don't process the point if either coordinate is null or
		// non-numeric,
		if (xCoord == null || yCoord == null || !xCoord.isGeoNumeric()
				|| !yCoord.isGeoNumeric() || zCoord == null
				|| !zCoord.isGeoNumeric()) {
			return;
		}

		GeoPointND geoPoint;

		if (byValue) {
			geoPoint = kernel.getManager3D().point3D(
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

			geoPoint = kernel.getManager3D().dependentPoint3D(point,
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

	/**
	 * Given a cell range to converted into a point list, this determines if
	 * pairs are joined vertically or horizontally and gets the row column
	 * indices needed to traverse the cells.
	 */
	static void getPointListDimensions(List<TabularRange> rangeList,
			PointDimension pd) {
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

			if (rangeList.get(0).getWidth() == 3 && pd.doHorizontalPairs) {
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
				pd.c1 = Math.min(rangeList.get(0).getMinColumn(),
						rangeList.get(1).getMinColumn());
				pd.c2 = Math.max(rangeList.get(0).getMaxColumn(),
						rangeList.get(1).getMaxColumn());
				// but get the max-min and min-max row indices in case the
				// columns don't line up
				pd.r1 = Math.max(rangeList.get(0).getMinRow(),
						rangeList.get(1).getMinRow());
				pd.r2 = Math.min(rangeList.get(0).getMaxRow(),
						rangeList.get(1).getMaxRow());

			} else {
				pd.doHorizontalPairs = true;
				// we are traversing across rows. so get min and max row indices
				pd.r1 = Math.min(rangeList.get(0).getMinRow(),
						rangeList.get(1).getMinRow());
				pd.r2 = Math.max(rangeList.get(0).getMaxRow(),
						rangeList.get(1).getMaxRow());
				// but get the max-min and min-max column indices in case the
				// rows don't line up
				pd.c1 = Math.max(rangeList.get(0).getMinColumn(),
						rangeList.get(1).getMinColumn());
				pd.c2 = Math.min(rangeList.get(0).getMaxColumn(),
						rangeList.get(1).getMaxColumn());

			}
		}
	}

	/**
	 * Creates a GeoPolyLine out of points constructed from the spreadsheet
	 * cells found in rangeList. Uses these defaults: no sorting, no undo point
	 *
	 * @param rangeList
	 *            selected ranges
	 * @param byValue
	 *            whether to use inputs as values
	 * @param leftToRight
	 *            whether to sort left to right
	 * @return polyline
	 */
	public GeoElement createPolyLine(List<TabularRange> rangeList,
			boolean byValue, boolean leftToRight) {
		return createPolyLine(rangeList, byValue, leftToRight, false);
	}

	/**
	 * * Creates a GeoPolyLine out of points constructed from the spreadsheet
	 * cells found in rangeList.
	 *
	 * @param rangeList
	 *            selected ranges
	 * @param byValue
	 *            whether to use inputs as values
	 * @param leftToRight
	 *            whether to sort left to right
	 * @param doStoreUndo
	 *            whether to store an undo point
	 * @return polyline
	 */
	public GeoElement createPolyLine(List<TabularRange> rangeList,
			boolean byValue, boolean leftToRight, boolean doStoreUndo) {

		boolean doCreateFreePoints = true;
		GeoList list = createPointGeoList(rangeList, byValue, leftToRight,
				doStoreUndo, doCreateFreePoints);
		GeoElement ret;
		if (list != null && list.size() > 1 && list.get(0).isGeoElement3D()) {
			ret = list.getKernel().getManager3D().polyLine3D(null, list)[0];
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

	/**
	 * Creates an operation table.
	 *
	 * @param range
	 *            selected range
	 */
	public void createOperationTable(TabularRange range) {
		int r1 = range.getMinRow();
		int c1 = range.getMinColumn();
		String text;
		GeoElementND[] geos;
		GeoFunctionNVar fcn = (GeoFunctionNVar) RelativeCopy.getValue(tableModel, c1,
				r1);

		for (int r = r1 + 1; r <= range.getMaxRow(); ++r) {
			for (int c = c1 + 1; c <= range.getMaxColumn(); ++c) {

				text = GeoElementSpreadsheet.getSpreadsheetCellName(c, r) + "="
						+ fcn.getLabel(StringTemplate.defaultTemplate) + "(";
				text += GeoElementSpreadsheet.getSpreadsheetCellName(c1, r);
				text += ",";
				text += GeoElementSpreadsheet.getSpreadsheetCellName(c, r1);
				text += ")";

				geos = app.getKernel().getAlgebraProcessor()
						.processAlgebraCommandNoExceptions(text, false);
				geos[0].setAuxiliaryObject(true);
			}
		}
	}

	/**
	 * @param rangeList
	 *            given list of spreadsheet cell ranges
	 * @return true if the shapes of the given cell ranges support creating a
	 *         point list, does not test the cell contents
	 */
	public boolean isCreatePointListPossible(List<TabularRange> rangeList) {

		// two adjacent rows or columns?
		if (rangeList.size() == 1
				&& (rangeList.get(0).is2D() || rangeList.get(0).is3D())) {
			return true;
		} else if (rangeList.size() == 2 && rangeList.get(0).getWidth() == 1
				&& rangeList.get(1).getWidth() == 1) {
			return true;
		} else if (rangeList.size() == 1) {
			return CellRangeUtil.isPointList(rangeList.get(0), tableModel);
		}

		return false;
	}

	// MARK: validation

	/**
	 * top-left must be a function of 2 variables eg A4(x,y)=x y^2 top row and
	 * left column must be numbers other cells can be anything (will be erased)
	 *
	 * @param rangeList
	 *            ranges
	 * @return whether operation table is possible
	 */
	public boolean isCreateOperationTablePossible(
			List<TabularRange> rangeList) {

		if (rangeList.size() != 1) {
			return false;
		}

		TabularRange tr = rangeList.get(0);
		int r1 = tr.getMinRow();
		int c1 = tr.getMinColumn();

		if (!(RelativeCopy.getValue(tableModel, c1, r1) instanceof GeoFunctionNVar)) {
			return false;
		}

		for (int r = r1 + 1; r <= tr.getMaxRow(); ++r) {
			if (!(RelativeCopy.getValue(tableModel, c1, r) instanceof GeoNumeric)) {
				return false;
			}
		}

		for (int c = c1 + 1; c <= tr.getMaxColumn(); ++c) {
			if (!(RelativeCopy.getValue(tableModel, c, r1) instanceof GeoNumeric)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @param rangeList
	 *            selected ranges
	 * @return whether matrix can be created
	 */
	public boolean isCreateMatrixPossible(List<TabularRange> rangeList) {
		return rangeList.size() == 1 && !CellRangeUtil.hasEmptyCells(rangeList.get(0), tableModel);
	}

}
