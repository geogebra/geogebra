package geogebra.gui.view.data;

import geogebra.common.gui.view.spreadsheet.CellRange;
import geogebra.common.gui.view.spreadsheet.CellRangeProcessor;
import geogebra.common.gui.view.spreadsheet.RelativeCopy;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoDependentList;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.common.plugin.GeoClass;
import geogebra.gui.GuiManagerD;
import geogebra.gui.view.spreadsheet.MyTableD;

import java.util.ArrayList;

/**
 * A DataItem maintains a reference to an existing GeoList or a set of
 * GeoElements that can be used to generate a single list of data values.
 * 
 * DataItems are the basic elements used by the classes DataVariable and
 * DataSource to manage data for DataAnalysisView.
 * 
 * @author G. Sturr
 * 
 */
public class DataItem {

	/**
	 * Identifiers for the possible sources of a DataItem
	 */
	public static enum SourceType {
		SPREADSHEET, LIST, CLASS, INTERNAL, EMPTY
	};

	private SourceType sourceType;
	private GeoClass geoClass = GeoClass.NUMERIC;

	// source objects
	private ArrayList<CellRange> rangeList;
	private GeoList geoList;
	private Double[] leftBorder;
	private String[] strInternal;

	private String description = " ";

	// =======================================
	// Constructors
	// =======================================

	/**
	 * Constructs a DataItem from a GeoList.
	 * 
	 * @param geoList
	 */
	public DataItem(GeoList geoList) {
		this.geoList = geoList;
		this.sourceType = SourceType.LIST;
	}

	/**
	 * Constructs a DataItem from a list of spreadsheet cell ranges.
	 * 
	 * @param rangeList
	 */
	public DataItem(ArrayList<CellRange> rangeList) {
		this.rangeList = rangeList;
		this.sourceType = SourceType.SPREADSHEET;
	}

	/**
	 * Constructs a DataItem from a single spreadsheet cell range.
	 * 
	 * @param cellRange
	 */
	public DataItem(CellRange cellRange) {
		rangeList = new ArrayList<CellRange>();
		rangeList.add(cellRange);
		this.sourceType = SourceType.SPREADSHEET;
	}

	/**
	 * Constructs a DataItem from an array of double (used for class borders
	 * when classes are generated automatically).
	 * 
	 * @param leftBorder
	 */
	public DataItem(Double[] leftBorder) {
		this.leftBorder = leftBorder;
		this.sourceType = SourceType.CLASS;
	}

	/**
	 * @param internalData
	 */
	public DataItem(String[] internalData) {
		this.sourceType = SourceType.INTERNAL;
	}

	/**
	 * Constructs a DataItem without a given source object.
	 */
	public DataItem() {
		sourceType = SourceType.EMPTY;
	}

	// ============================================
	// Getters/Setters
	// ============================================

	/**
	 * Clears this DataItem and sets the source to the given list of spreadsheet
	 * cell ranges.
	 * 
	 * @param rangeList
	 */
	public void setDataItem(ArrayList<CellRange> rangeList) {
		clearItem();
		this.rangeList = rangeList;
		this.sourceType = SourceType.SPREADSHEET;
	}

	/**
	 * Clears this DataItem and sets the source to the given GeoList.
	 * 
	 * @param geoList
	 */
	public void setDataItem(GeoList geoList) {
		clearItem();
		this.geoList = geoList;
		this.sourceType = SourceType.LIST;
	}

	/**
	 * Clears this DataItem and sets the source to the given array of double.
	 * (used for class borders when classes are generated automatically).
	 * 
	 * @param leftBorder
	 */
	public void setDataItem(Double[] leftBorder) {
		clearItem();
		this.leftBorder = leftBorder;
		this.sourceType = SourceType.CLASS;
	}

	public ArrayList<CellRange> getRangeList() {
		return rangeList;
	}

	public GeoList getGeoList() {
		return geoList;
	}

	public Double[] getLeftBorder() {
		return leftBorder;
	}

	public String[] getStrInternal() {
		return strInternal;
	}

	public SourceType getType() {
		return sourceType;
	}

	public GeoClass getGeoClass() {
		return geoClass;
	}

	public void setGeoClass(GeoClass geoClass) {
		this.geoClass = geoClass;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	// ==============================================
	// Utility methods
	// ==============================================

	public void clearItem() {
		// TODO: dereference the geo fields -- needed ??
	}

	public boolean containsGeoClass(GeoClass geoClass) {

		if (sourceType == SourceType.EMPTY) {
			return false;
		}

		switch (sourceType) {
		case LIST:
			if (geoList == null) {
				return false;
			}
			for (int i = 0; i < geoList.size(); i++) {
				if (geoList.get(i).getGeoClassType() == geoClass) {
					return true;
				}
			}
			break;
		case SPREADSHEET:
			if (rangeList == null) {
				return false;
			}
			for (CellRange cr : rangeList) {
				if (cr.containsGeoClass(geoClass))
					return true;
			}
			break;
		}
		return false;
	}

	/**
	 * @return true if this DataItem has a null source
	 */
	private boolean isEmptyItem() {

		// TODO also check for empty sources?
		/*
		 * if(getGeoCount() == 0){ return false; }
		 */

		switch (sourceType) {
		case LIST:
			return geoList == null;
		case SPREADSHEET:
			return rangeList == null;
		case CLASS:
			return leftBorder == null;
		case INTERNAL:
			return strInternal == null;
		case EMPTY:
			return true;
		default:
			return false;
		}
	}

	/**
	 * @return number of GeoElements referenced by this DataItem
	 */
	public int getGeoCount() {
		switch (sourceType) {
		case LIST:
			return geoList.size();
		case SPREADSHEET:
			int count = 0;
			for (CellRange cr : rangeList) {
				count += cr.getGeoCount(null);
			}
			return count;
		case CLASS:
			return leftBorder.length;
		case INTERNAL:
			return strInternal.length;
		case EMPTY:
			return 0;
		default:
			return 0;
		}
	}

	/**
	 * Returns a string description of the data source
	 * 
	 * @param app
	 * 
	 * @return either a spreadsheet cell range name or a GeoList label
	 */
	public String getSourceString(App app) {

		String sourceString = " ";

		switch (sourceType) {
		case LIST:
			sourceString = getGeoList()
					.getLabel(StringTemplate.defaultTemplate);
			break;

		case SPREADSHEET:
			sourceString = spreadsheetTable(app).getCellRangeProcessor()
					.getCellRangeString(getRangeList());
			break;

		case INTERNAL:
			sourceString = "Untitled";
			break;

		case CLASS:
			sourceString = " ";
			break;
		}

		return sourceString;
	}

	/**
	 * @param app
	 * @param spreadsheetTable
	 * @param enableHeader
	 * @return DataItem title
	 */
	public String getDataTitle(App app, boolean enableHeader) {

		if (!enableHeader || sourceType == SourceType.LIST) {
			return (getSourceString(app));

		} else if (enableHeader && sourceType == SourceType.SPREADSHEET) {

			StringTemplate tpl = StringTemplate.defaultTemplate;
			CellRange range = getRangeList().get(0);

			if (range.isColumn() || range.isPartialColumn()) {
				GeoElement geo = RelativeCopy.getValue(app,
						range.getMinColumn(), range.getMinRow());

				if (geo != null) {
					return geo.toDefinedValueString(tpl);
				}
			}
		}

		return app.getMenu("Untitled");
	}

	/**
	 * Converts DataItem into a GeoList
	 * 
	 * @param app
	 * @param enableHeader
	 * @param leftToRight
	 * @param doCopy
	 * 
	 * @return A GeoList containing elements corresponding to this DataItem
	 */
	protected GeoList toGeoList(App app, boolean enableHeader,
			boolean leftToRight, boolean doCopy) {

		if (sourceType == SourceType.EMPTY) {
			return null;
		}

		Construction cons = app.getKernel().getConstruction();
		GeoList list;
		
		switch (sourceType) {

		case LIST:
			
			if (doCopy) {
				list = dependentListCopy(cons, getGeoList());
			}else{
				list = getGeoList();
			}
			if(!leftToRight){
				swapXYCoords(list);
			}
			
			break;

		case SPREADSHEET:

			
			boolean scanByColumn = true;
			boolean copyByValue = doCopy; // allows dynamic changes
			boolean doStoreUndo = false;
			boolean isSorted = false;
			boolean setLabel = false;

			try {
				ArrayList<CellRange> rangeListCopy = rangeListCopy(
						getRangeList(), enableHeader);

				list = crProcessor(app).createList(rangeListCopy, scanByColumn,
						copyByValue, isSorted, doStoreUndo, geoClass, setLabel);

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			
			break;

		case CLASS:

			list = new GeoList(cons);
			for (int i = 0; i < getLeftBorder().length; i++) {
				list.add(new GeoNumeric(cons, getLeftBorder()[i]));
			}

			break;

		case INTERNAL:

			String[] s = getStrInternal();

			boolean oldSuppress = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);

			list = new GeoList(cons);
			for (int i = 0; i < s.length; i++) {

				try {
					double num = Double.parseDouble(s[i]);
					// System.out.println(num);
					GeoElement geo = new GeoNumeric(cons);
					((GeoNumeric) geo).setValue(num);
					list.add(geo);
				} catch (Exception e) {
					// e.printStackTrace();
					App.error(e.getMessage());
				}
			}
			cons.setSuppressLabelCreation(oldSuppress);

			break;

		default:
			return null;
		}
		
		if(!leftToRight && list.getElementType() == GeoClass.POINT){
			swapXYCoords(list);
		}
		
		return list;
	}

	/**
	 * Copies a list of cell ranges with option to remove header cell
	 * 
	 * @param list
	 * @param removeHeaderCell
	 * @return
	 */
	private static ArrayList<CellRange> rangeListCopy(
			ArrayList<CellRange> list, boolean removeHeaderCell) {

		ArrayList<CellRange> list2 = new ArrayList<CellRange>();

		list2.add(rangeCopy(list.get(0), removeHeaderCell));

		for (int i = 1; i < list.size(); i++) {
			list2.add(list.get(i).clone());
		}
		return list2;
	}

	/**
	 * Copies a cell range with option to remove header cell
	 * 
	 * @param cr
	 * @param removeHeaderCell
	 * @return
	 */
	private static CellRange rangeCopy(CellRange cr, boolean removeHeaderCell) {
		CellRange cr2 = cr.clone();
		if (removeHeaderCell) {
			cr2.setCellRange(cr.getMinColumn(), cr.getMinRow() + 1,
					cr.getMaxColumn(), cr.getMaxRow());
		}
		return cr2;

	}

	private static GeoList dependentListCopy(Construction cons, GeoList geoList) {

		ArrayList<GeoElement> copyList = new ArrayList<GeoElement>();

		for (int i = 0; i < geoList.size(); i++) {
			copyList.add(geoList.get(i).copy());
		}

		AlgoDependentList algo = new AlgoDependentList(cons, copyList, false);
		cons.removeFromConstructionList(algo);

		return (GeoList) algo.getGeoElements()[0];
	}

	private static void swapXYCoords(GeoList geoList) {
		
		if (geoList.getElementType() != GeoClass.POINT) {
			return;
		}
		for (int i = 0; i < geoList.size(); i++) {
			double x = ((GeoPoint) geoList.get(i)).x;
			double y = ((GeoPoint) geoList.get(i)).y;
			((GeoPoint) geoList.get(i)).setCoords(y, x, 1.0);
		}
		geoList.updateCascade();
	}

	/**
	 * @param enableHeader
	 * @return
	 */
	public String[] toStringArray(boolean enableHeader) {
		ArrayList<String> list = toStringList(enableHeader);
		if (list == null) {
			return new String[0];
		}

		return list.toArray(new String[list.size()]);

	}

	/**
	 * @param enableHeader
	 *            if true then the first cell of a spreadsheet range is ignored
	 * @return list of String descriptions of the data represented by this
	 *         DataItem
	 */
	public ArrayList<String> toStringList(boolean enableHeader) {

		if (isEmptyItem()) {
			return null;
		}

		ArrayList<String> strList = new ArrayList<String>();

		try {
			switch (sourceType) {

			case LIST:
				for (int i = 0; i < geoList.size(); i++) {

					if (geoList.get(i) == null || !(geoList.get(i).isDefined())) {
						continue;
					}
					if (isValidDataType(geoList.get(i))) {
						strList.add(i, geoList.get(i).getValueForInputBar());
					} else {
						strList.add(i, "<html><i><font color = gray>"
								+ geoList.get(i).getValueForInputBar()
								+ "</font></i></html>");
					}
				}

				break;

			case SPREADSHEET:

				boolean skipFirstCell = enableHeader;

				for (CellRange cr : rangeList) {

					ArrayList<GeoElement> list = cr.toGeoList();

					// iterate through the list and set the row values
					for (int i = 0; i < list.size(); i++) {
						if (skipFirstCell) {
							skipFirstCell = false;
							continue;
						}
						if (list.get(i) == null || !(list.get(i).isDefined())) {
							continue;
						}
						if (isValidDataType(list.get(i))) {
							strList.add(list.get(i).getValueForInputBar());
						} else {
							strList.add("<html><i><font color = gray>"
									+ list.get(i).getValueForInputBar()
									+ "</font></i></html>");
						}
					}
				}
				break;

			case INTERNAL:
				String[] str = getStrInternal();

				// load the array into the column
				for (int i = 0; i < str.length; i++) {
					if (i < str.length && str[i] != null) {
						strList.add(i, str[i]);
					} else {
						strList.add(i, " ");
					}
				}
				break;

			case CLASS:
				Double[] leftBorder = getLeftBorder();
				// System.out.println("=====> " + Arrays.toString(leftBorder));

				// load the array into the column
				for (int i = 0; i < leftBorder.length - 1; i++) {
					if (i < leftBorder.length && leftBorder[i] != null) {
						String interval = leftBorder[i] + " - "
								+ leftBorder[i + 1];
						strList.add(i, interval);
					} else {
						strList.add(i, " ");
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return strList;
	}

	/**
	 * @return true if the given GeoElement is a valid element for this DataItem
	 */
	private boolean isValidDataType(GeoElement geo) {

		return geo.getGeoClassType() == geoClass;
	}

	private static CellRangeProcessor crProcessor(App app) {
		MyTableD spreadsheetTable = (MyTableD) ((GuiManagerD) app
				.getGuiManager()).getSpreadsheetView().getSpreadsheetTable();
		return spreadsheetTable.getCellRangeProcessor();
	}

	private static MyTableD spreadsheetTable(App app) {
		return (MyTableD) ((GuiManagerD) app.getGuiManager())
				.getSpreadsheetView().getSpreadsheetTable();
	}

}
