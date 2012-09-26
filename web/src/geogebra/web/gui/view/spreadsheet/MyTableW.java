package geogebra.web.gui.view.spreadsheet;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GPoint;
import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.main.OptionType;
import geogebra.common.gui.view.spreadsheet.CellFormatInterface;
import geogebra.common.gui.view.spreadsheet.CellFormat;
import geogebra.common.gui.view.spreadsheet.CellRange;
import geogebra.common.gui.view.spreadsheet.CellRangeProcessor;
import geogebra.common.gui.view.spreadsheet.CopyPasteCut;
import geogebra.common.gui.view.spreadsheet.MyTable;
import geogebra.common.gui.view.spreadsheet.RelativeCopy;
import geogebra.common.main.SpreadsheetTableModel;
import geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.common.main.settings.SpreadsheetSettings;
import geogebra.web.awt.GBasicStrokeW;
//import geogebra.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.web.main.AppW;
import geogebra.web.gui.inputfield.AutoCompleteTextFieldW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MyTableW extends Grid implements /* FocusListener, */MyTable {
	private static final long serialVersionUID = 1L;

	private int tableMode = MyTable.TABLE_MODE_STANDARD;

	public static final int MAX_CELL_EDIT_STRING_LENGTH = 10;

	public static final int DOT_SIZE = 7;
	public static final int LINE_THICKNESS1 = 3;
	public static final int LINE_THICKNESS2 = 2;
	public static final GColor SELECTED_BACKGROUND_COLOR = GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR;
	public static final GColor SELECTED_BACKGROUND_COLOR_HEADER = GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR_HEADER;
	public static final GColor BACKGROUND_COLOR_HEADER = GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER;
	public static final GColor TABLE_GRID_COLOR = GeoGebraColorConstants.GRAY2;
	public static final GColor HEADER_GRID_COLOR = GeoGebraColorConstants.GRAY4;
	public static final GColor SELECTED_RECTANGLE_COLOR = GeoGebraColorConstants.BLUE;

	protected Kernel kernel;
	protected AppW app;
	protected MyCellEditorW editor;
	private MyCellEditorBooleanW editorBoolean;
	// private MyCellEditorButton editorButton;
	// private MyCellEditorList editorList;

	protected RelativeCopy relativeCopy;
	public CopyPasteCut copyPasteCut;

	protected SpreadsheetColumnController scc;
	protected SpreadsheetRowHeader srh;
	protected SpreadsheetColumnController.ColumnHeaderRenderer columnHeaderRenderer;
	protected SpreadsheetRowHeader.RowHeaderRenderer rowHeaderRenderer;
	protected SpreadsheetRowHeader.MyListModel rowHeaderModel;

	protected SpreadsheetView view;
	protected SpreadsheetTableModel tableModel;
	private CellRangeProcessor crProcessor;
	// private MyTableColumnModelListener columnModelListener;
	MyCellRenderer defaultTableCellRenderer;

	private CellFormatInterface formatHandler;

	private GeoElement targetCell;

	/**
	 * All currently selected cell ranges are held in this list. Cell ranges are
	 * added when selecting with ctrl-down. The first element is the most
	 * recently selected cell range.
	 */
	public ArrayList<CellRange> selectedCellRanges;

	public ArrayList<CellRange> getSelectedCellRanges() {
		return selectedCellRanges;
	}

	// These keep track of internal selection using actual ranges and do not
	// use -1 flags for row and column.
	// Note: selectedCellRanges.get(0) gives the same selection but uses -1
	// flags
	// the following are in Grid coordinates (TableModel coordinates+1)
	protected int minSelectionRow = -1;
	protected int maxSelectionRow = -1;
	protected int minSelectionColumn = -1;
	protected int maxSelectionColumn = -1;
	protected int minSelectionRowOld = -1;
	protected int maxSelectionRowOld = -1;
	protected int minSelectionColumnOld = -1;
	protected int maxSelectionColumnOld = -1;

	// for emulating the JTable's changeSelection method, in TableModel coordinates
	protected int anchorSelectionRow = -1;
	protected int anchorSelectionColumn = -1;
	protected int leadSelectionRow = -1;
	protected int leadSelectionColumn = -1;

	public boolean[] selectedColumns;

	// Used for rendering headers with ctrl-select
	protected HashSet<Integer> selectedColumnSet = new HashSet<Integer>();
	protected HashSet<Integer> selectedRowSet = new HashSet<Integer>();

	private int selectionType = MyTable.CELL_SELECT;

	private boolean columnSelectionAllowed;
	private boolean rowSelectionAllowed;

	private boolean doShowDragHandle = true;
	private GColor selectionRectangleColor = SELECTED_RECTANGLE_COLOR;

	// Dragging vars
	protected boolean isDragingDot = false;
	protected int dragingToRow = -1;
	protected int dragingToColumn = -1;
	protected boolean isOverDot = false;
	protected boolean isDragging2 = false;

	protected int minColumn2 = -1;
	protected int maxColumn2 = -1;
	protected int minRow2 = -1;
	protected int maxRow2 = -1;

	protected boolean isOverDnDRegion = false;

	public boolean isOverDnDRegion() {
		return isOverDnDRegion;
	}

	protected boolean isEditing = false;

	public boolean isEditing() {
		return isEditing;
	}

	protected int editRow = -1;
	protected int editColumn = -1;

	// Keep track of ctrl-down. This is needed in some
	// selection methods that do not receive key events.
	protected boolean metaDown = false;

	boolean repaintAll = false;// sometimes only the repainting of borders/background is needed

	// Cells to be resized on next repaint are put in these HashSets.
	// A cell is added to a set when editing is done. The cells are removed
	// after a repaint in MyTable.
	public HashSet<GPoint> cellResizeHeightSet;
	public HashSet<GPoint> cellResizeWidthSet;

	private ArrayList<GPoint> adjustedRowHeights = new ArrayList<GPoint>();
	private boolean doRecordRowHeights = true;

	public int preferredColumnWidth = SpreadsheetSettings.TABLE_CELL_WIDTH;

	// there should be place left for the textfield
	public static int minimumRowHeight = SpreadsheetSettings.TABLE_CELL_HEIGHT + 4;
	// the textfield is this much smaller than the row height and column width
	public static int minusRowHeight = 12;
	public static int minusColumnWidth = 14;

	// Collection of cells that contain geos that can be edited with one click,
	// e.g. booleans, buttons, lists
	protected HashMap<GPoint, GeoElement> oneClickEditMap = new HashMap<GPoint, GeoElement>();

	public HashMap<GPoint, GeoElement> getOneClickEditMap() {
		return oneClickEditMap;
	}

	public void setOneClickEditMap(HashMap<GPoint, GeoElement> oneClickEditMap) {
		this.oneClickEditMap = oneClickEditMap;
	}

	// cursors
	// protected Cursor defaultCursor = Cursor.getDefaultCursor();
	// protected Cursor crossHairCursor = Cursor
	// .getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	// protected Cursor handCursor = Cursor
	// .getPredefinedCursor(Cursor.HAND_CURSOR);
	// protected Cursor grabbingCursor, grabCursor;

	/*******************************************************************
	 * Construct table
	 */
	public MyTableW(SpreadsheetView view, SpreadsheetTableModel tableModel) {
		super(tableModel.getRowCount() + 1, tableModel.getColumnCount() + 1);

		cellResizeHeightSet = new HashSet<GPoint>();
		cellResizeWidthSet = new HashSet<GPoint>();

		app = (AppW) view.getApplication();
		kernel = app.getKernel();
		this.tableModel = tableModel;
		this.view = view;

		// grabCursor = createCursor(app.getImageIcon("cursor_grab.gif")
		// .getImage(), true);
		// grabbingCursor = createCursor(app.getImageIcon("cursor_grabbing.gif")
		// .getImage(), true);

		// set row height
		setRowHeight(minimumRowHeight);

		/*
		 * // prepare column headers SpreadsheetColumnController
		 * columnController = new SpreadsheetColumnController( app, this);
		 * headerRenderer = columnController.new ColumnHeaderRenderer();
		 * getTableHeader().setFocusable(true);
		 * getTableHeader().addMouseListener(columnController);
		 * getTableHeader().addMouseMotionListener(columnController);
		 * getTableHeader().addKeyListener(columnController);
		 * getTableHeader().setReorderingAllowed(false);
		 * setAutoCreateColumnsFromModel(false);
		 * 
		 * // set columns and column headers
		 * setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		 * 
		 * headerRenderer.setPreferredSize(new Dimension(preferredColumnWidth,
		 * SpreadsheetSettings.TABLE_CELL_HEIGHT));
		 */
		for (int i = 0; i < getColumnCount(); ++i) {
			// TODO//getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
			getColumnFormatter().getElement(i).getStyle()
			        .setWidth(preferredColumnWidth, Style.Unit.PX);
		}

		// set visual appearance
		setBorderWidth(1);
		getElement().getStyle().setBorderColor(TABLE_GRID_COLOR.toString());
		getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		// TODO//setSelectionBackground(SELECTED_BACKGROUND_COLOR);
		// TODO//setSelectionForeground(Color.BLACK);

		// add cell renderer & editors
		defaultTableCellRenderer = new MyCellRenderer(app, view,
		        (CellFormat) this.getCellFormatHandler());

		//:NEXT:Grid.setCellFormatter
		editor = new MyCellEditorW(kernel);
		//setDefaultEditor(Object.class, editor);

		// initialize selection fields
		selectedCellRanges = new ArrayList<CellRange>();
		selectedCellRanges.add(new CellRange(app));

		selectionType = MyTable.CELL_SELECT;
		rowSelectionAllowed = columnSelectionAllowed = true;

		// add mouse and key listeners
		scc = new SpreadsheetColumnController(app, this);
		srh = new SpreadsheetRowHeader(app, this);
		SpreadsheetMouseListener ml = new SpreadsheetMouseListener(app, this);
		addDomHandler(ml, MouseDownEvent.getType());
		addDomHandler(ml, MouseUpEvent.getType());
		addDomHandler(ml, MouseMoveEvent.getType());
		addDomHandler(ml, ClickEvent.getType());
		addDomHandler(ml, DoubleClickEvent.getType());

		/*
		 * // key listener KeyListener[] defaultKeyListeners =
		 * getKeyListeners(); for (int i = 0; i < defaultKeyListeners.length;
		 * ++i) { removeKeyListener(defaultKeyListeners[i]); }
		 * addKeyListener(new SpreadsheetKeyListener(app, this));
		 * 
		 * // setup selection listener // TODO // These listeners are no longer
		 * needed. // getSelectionModel().addListSelectionListener(new //
		 * RowSelectionListener()); //
		 * getColumnModel().getSelectionModel().addListSelectionListener(new //
		 * ColumnSelectionListener()); //
		 * getColumnModel().getSelectionModel().addListSelectionListener
		 * (columnHeader);
		 */
		// add table model listener
		((SpreadsheetTableModelW) tableModel)
		        .setChangeListener(new MyTableModelListener());

		// relative copy
		relativeCopy = new RelativeCopy(kernel);
		/*
		 * copyPasteCut = new CopyPasteCutD(app);
		 * 
		 * // - see ticket #135 addFocusListener(this);
		 * 
		 * // editing putClientProperty("terminateEditOnFocusLost",
		 * Boolean.TRUE);
		 * 
		 * columnModelListener = new MyTableColumnModelListener();
		 * getColumnModel().addColumnModelListener(columnModelListener);
		 * 
		 * // set first cell active // needed in case spreadsheet selected with
		 * ctrl-tab rather than mouse // click // changeSelection(0, 0, false,
		 * false);
		 */
		rowHeaderModel = new SpreadsheetRowHeader.MyListModel(
		        (SpreadsheetTableModelW) tableModel);
		rowHeaderRenderer = srh.new RowHeaderRenderer();
		columnHeaderRenderer = scc.new ColumnHeaderRenderer();

		setCellPadding(0);
		setCellSpacing(0);
		getElement().getStyle().setTableLayout(Style.TableLayout.FIXED);
		getElement().getStyle().setWidth(100, Style.Unit.PCT);

		getElement().addClassName("geogebraweb-table-spreadsheet");

		setRepaintAll();
		repaint();
	}

	/**
	 * End table constructor
	 ******************************************************************/

	/**
	 * Returns parent SpreadsheetView for this table
	 * 
	 * @return SpreadsheetView
	 */
	public SpreadsheetViewInterface getView() {
		return view;
	}

	/**
	 * Simple getter method
	 * 
	 * @return App
	 */
	public App getApplication() {
		return app;
	}

	/**
	 * Simple getter method
	 * 
	 * @return Kernel
	 */
	public Kernel getKernel() {
		return kernel;
	}

	public SpreadsheetTableModel getModel() {
		return tableModel;
	}

	public CopyPasteCut getCopyPasteCut() {
		return copyPasteCut;
	}

	/**
	 * Returns CellRangeProcessor for this table. If none exists, a new one is
	 * created.
	 */
	public CellRangeProcessor getCellRangeProcessor() {
		if (crProcessor == null)
			crProcessor = new CellRangeProcessor(this);
		return crProcessor;
	}

	/**
	 * Returns CellFormat helper class for this table. If none exists, a new one
	 * is created.
	 */
	public CellFormatInterface getCellFormatHandler() {
		if (formatHandler == null)
			formatHandler = new CellFormat(this);
		return formatHandler;
	}

	/**
	 * Returns boolean editor (checkbox) for this table. If none exists, a new
	 * one is created.
	 */
	public MyCellEditorBooleanW getEditorBoolean() {
		if (editorBoolean == null)
			editorBoolean = new MyCellEditorBooleanW(kernel);
		return editorBoolean;
	}

	/**
	 * Returns button editor for this table. If none exists, a new one is
	 * created.
	 */
	/*
	 * public MyCellEditorButton getEditorButton() { if (editorButton == null)
	 * editorButton = new MyCellEditorButton(); return editorButton; }
	 */

	/**
	 * Returns list editor (comboBox) for this table. If none exists, a new one
	 * is created.
	 */
	/*
	 * public MyCellEditorList getEditorList() { if (editorList == null)
	 * editorList = new MyCellEditorList(); return editorList; }
	 */

	/**
	 * Appends columns to the table if table model column count is larger than
	 * current number of table columns.
	 */
	protected void updateColumnCount() {

		if (tableModel.getColumnCount() + 1 <= this.getColumnCount())
			return;

		// ensure that auto-create is off
		// if (this.getAutoCreateColumnsFromModel()) {
		// throw new IllegalStateException();
		// }

		int cc = this.getColumnCount();

		resizeColumns(tableModel.getColumnCount() + 1);

		// add new columns to table
		for (int i = cc; i < tableModel.getColumnCount() + 1; ++i) {
			getColumnFormatter().getElement(i).getStyle()
			        .setWidth(preferredColumnWidth, Style.Unit.PX);
			// TableColumn col = new TableColumn(i);
			// TODO// col.setHeaderRenderer(headerRenderer);
			// addColumn(col);
		}

		// addColumn destroys custom row heights, so we must reset them
		resetRowHeights();

	}

	public BaseCellEditor getCellEditor(int row, int column) {
		GPoint p = new GPoint(column, row);
		if (view.allowSpecialEditor() &&
			oneClickEditMap.containsKey(p) && kernel.getAlgebraStyle() ==
			Kernel.ALGEBRA_STYLE_VALUE) {
			switch (oneClickEditMap.get(p).getGeoClassType()) {
				case BOOLEAN: return getEditorBoolean();
				case BUTTON: return null;//TODO! getEditorButton();
				case LIST: return null;//TODO! getEditorList();
			}
		}
		return editor;
	}

	/**
	 * sets requirement that commands entered into cells must start with "="
	 */
	public void setEqualsRequired(boolean isEqualsRequired) {
		editor.setEqualsRequired(isEqualsRequired);
	}

	/**
	 * gets flag for requirement that commands entered into cells must start
	 * with "="
	 */
	public boolean isEqualsRequired() { return view.isEqualsRequired(); }

	public void setLabels() { editor.setLabels(); }

	public int preferredColumnWidth() {
		return preferredColumnWidth;
	}

	public void setPreferredColumnWidth(int preferredColumnWidth) {
		this.preferredColumnWidth = preferredColumnWidth;
	}

	public class MyTableModelListener implements
	        SpreadsheetTableModelW.ChangeListener {

		public void dimensionChange() {
			// TODO: comment them out to imitate the Desktop behaviour
			// TODO//getView().updateRowHeader();
			updateColumnCount();

			// web-specific solution
			if (tableModel.getRowCount() + 1 <= getRowCount())
				return;

			resizeRows(tableModel.getRowCount() + 1);
		}

		public void valueChange() {
			setRepaintAll();
		}
	}

	// ===============================================================
	// Selection
	// ===============================================================

	/**
	 * JTable does not support non-contiguous cell selection. It treats
	 * ctrl-down cell selection as if it was shift-extend. To prevent this
	 * behavior the JTable changeSelection method is overridden here.
	 */
	public void changeSelection(int rowIndex, int columnIndex, boolean toggle,
	        boolean extend) {
		// if(Application.getControlDown())
		// super.changeSelection(rowIndex, columnIndex, false, false);
		// else

		// force column selection
		if (view.isColumnSelect()) {
			setColumnSelectionInterval(columnIndex, columnIndex);
		}

		if (toggle) {
			// not used anyway
		} else {
			if (extend) {
				leadSelectionColumn = columnIndex - 1;
				leadSelectionRow = rowIndex - 1;
			} else {
				anchorSelectionColumn = columnIndex - 1;
				anchorSelectionRow = rowIndex - 1;
				leadSelectionColumn = columnIndex - 1;
				leadSelectionRow = rowIndex - 1;
			}
		}
		// let selectionChanged know about a change in single cell selection
		selectionChanged();
	}

	public void selectAll() {
		setSelectionType(MyTable.CELL_SELECT);
		// ?//this.setAutoscrolls(false);
		// select the upper left corner cell
		changeSelection(0, 0, false, false);
		// extend the selection to the current lower right corner cell
		changeSelection(getRowCount() - 1, getColumnCount() - 1, false, true);
		setSelectAll(true);
		// ?//this.setAutoscrolls(true);

		// this.scrollRectToVisible(getCellRect(0,0,true));
		// setRowSelectionInterval(0, getRowCount()-1);
		// getColumnModel().getSelectionModel().setSelectionInterval(0,
		// getColumnCount()-1);
		// selectionChanged();
		// this.getSelectAll();

	}

	/**
	 * This handles all selection changes for the table.
	 */
	public void selectionChanged() {

		// create a cell range object to store
		// the current table selection

		CellRange newSelection = new CellRange(app);

		/*
		 * TODO if (view.isTraceDialogVisible()) {
		 * 
		 * newSelection = view.getTraceSelectionRange(getColumnModel()
		 * .getSelectionModel().getAnchorSelectionIndex(),
		 * getSelectionModel().getAnchorSelectionIndex());
		 * 
		 * scrollRectToVisible(getCellRect(newSelection.getMinRow(),
		 * newSelection.getMaxColumn(), true));
		 * 
		 * } else {
		 */

		switch (selectionType) {

		case MyTable.CELL_SELECT:
			newSelection.setCellRange(anchorSelectionColumn,
			        anchorSelectionRow, leadSelectionColumn, leadSelectionRow);
			break;

		case MyTable.ROW_SELECT:
			newSelection.setCellRange(-1, anchorSelectionRow, -1,
			        leadSelectionRow);
			break;

		case MyTable.COLUMN_SELECT:
			newSelection.setCellRange(anchorSelectionColumn, -1,
			        leadSelectionColumn, -1);
			break;
		}
		/*
		 * }
		 */
		// newSelection.debug();
		/*
		 * // return if it is not really a new cell
		 * if(selectedCellRanges.size()>0 &&
		 * newSelection.equals(selectedCellRanges.get(0))) return;
		 */

		// update the selection list

		if (!AppW.getControlDown()) {
			selectedCellRanges.clear();
			selectedColumnSet.clear();
			selectedRowSet.clear();
			selectedCellRanges.add(0, newSelection);
		} else { // ctrl-select
			/*
			 * // return if we have already ctrl-selected this range for
			 * (CellRange cr : selectedCellRanges) { if
			 * (cr.equals(newSelection)){ System.out.println("reutrned");
			 * return; } }
			 */

			// handle dragging
			if (selectedCellRanges.get(0).hasSameAnchor(newSelection)) {
				selectedCellRanges.remove(0);
			}

			// add the selection to the list
			selectedCellRanges.add(0, newSelection);
		}

		// update sets of selected rows/columns (used for rendering in the
		// headers)
		if (selectionType == MyTable.COLUMN_SELECT)
			for (int i = newSelection.getMinColumn(); i <= newSelection
			        .getMaxColumn(); i++)
				selectedColumnSet.add(i);

		if (selectionType == MyTable.ROW_SELECT)
			for (int i = newSelection.getMinRow(); i <= newSelection
			        .getMaxRow(); i++)
				selectedRowSet.add(i);

		// check for change in anchor cell (for now this is minrow and mincol
		// ...)
		boolean changedAnchor = minSelectionColumn
		        - newSelection.getMinColumn() != 0
		        || minSelectionRow - newSelection.getMinRow() != 0;

		// update internal selection variables
		newSelection.setActualRange();
		minSelectionRow = newSelection.getMinRow();
		minSelectionColumn = newSelection.getMinColumn();
		maxSelectionColumn = newSelection.getMaxColumn();
		maxSelectionRow = newSelection.getMaxRow();

		if (minSelectionRow != -1) minSelectionRow += 1;
		if (minSelectionColumn != -1) minSelectionColumn += 1;
		if (maxSelectionColumn != -1) maxSelectionColumn += 1;
		if (maxSelectionRow != -1) maxSelectionRow += 1;

		// newSelection.debug();
		// printSelectionParameters();

		if (isSelectNone && (minSelectionColumn != -1 || minSelectionRow != -1))
			setSelectNone(false);

		//TODO if (changedAnchor && !isEditing()) view.updateFormulaBar();

		// update the geo selection list
		ArrayList<GeoElement> list = new ArrayList<GeoElement>();
		for (int i = 0; i < selectedCellRanges.size(); i++) {
			list.addAll(0, (selectedCellRanges.get(i)).toGeoList());
		}

		// if the geo selection has changed, update selected geos
		boolean changed = !list.equals(app.getSelectedGeos());
		if (changed) {

			if (getTableMode() == MyTable.TABLE_MODE_AUTOFUNCTION) {
				this.updateAutoFunction();
			}

			/*
			 * TODO if (view.isVisibleStyleBar())
			 * view.getSpreadsheetStyleBar().updateStyleBar();
			 */

			app.setSelectedGeos(list, false);
			if (list.size() > 0) {
				app.updateSelection(true);
			} else {
				// don't update properties view for objects, but for spreadsheet
				app.updateSelection(false);
				app.setPropertiesViewPanel(OptionType.SPREADSHEET);
			}
		}

		// if the selection has changed or an empty cell has been clicked,
		// repaint
		if (changed || list.isEmpty()) {
			repaint();
			//?//if (this.getTableHeader() != null)
			//?//	getTableHeader().repaint();
		}

		// System.out.println("------------------");
		// for (CellRange cr: selectedCellRanges)cr.debug();
	}

	private void printSelectionParameters() {
		System.out.println("----------------------------------");
		System.out.println("minSelectionColumn = " + minSelectionColumn);
		System.out.println("maxSelectionColumn = " + maxSelectionColumn);
		System.out.println("minSelectionRow = " + minSelectionRow);
		System.out.println("maxSelectionRow = " + maxSelectionRow);
		System.out.println("----------------------------------");
	}

	/**
	 * Sets the initial selection parameters to a single cell. Does this without
	 * calling changeSelection, so it should only be used at startup.
	 */
	public void setInitialCellSelection(int row, int column) {

		setSelectionType(MyTable.CELL_SELECT);

		if (column == -1)
			column = 0;
		if (row == -1)
			row = 0;
		minSelectionColumn = column;
		maxSelectionColumn = column;
		minSelectionRow = row;
		maxSelectionRow = row;

		// ?//getColumnModel().getSelectionModel().setSelectionInterval(column,
		// column);
		// ?//getSelectionModel().setSelectionInterval(row, row);
	}

	/*
	 * public void setSelectionRectangle(CellRange cr){
	 * 
	 * if (cr == null){ this.minSelectionColumn = -1; this.minSelectionRow = -1;
	 * this.maxSelectionColumn = -1; this.maxSelectionRow = -1; return; }
	 * 
	 * this.minSelectionColumn = cr.getMinColumn(); this.minSelectionRow =
	 * cr.getMinRow(); this.maxSelectionColumn = cr.getMaxColumn();
	 * this.maxSelectionRow = cr.getMaxRow(); this.repaint();
	 * 
	 * }
	 */

	/*
	 * public void setTraceSelectionRectangle() {
	 * 
	 * if (view.getSelectedTrace() == null) { cellFrame = null; } else {
	 * 
	 * int c1 = view.getSelectedTrace().traceColumn1; int r1 =
	 * view.getSelectedTrace().traceRow1; int c2 =
	 * view.getSelectedTrace().traceColumn2; int r2 =
	 * view.getSelectedTrace().doRowLimit ? view.getSelectedTrace().traceRow2 :
	 * getRowCount();
	 * 
	 * Point point1 = getPixel(c1,r1, true); Point point2 = getPixel(c2,r2,
	 * false);
	 * 
	 * cellFrame.setFrameFromDiagonal(point1, point2);
	 * 
	 * // scroll to upper left corner of rectangle
	 * scrollRectToVisible(table.getCellRect(r1,c1, true));
	 * 
	 * } repaint();
	 * 
	 * }
	 */

	public boolean setSelection(String cellName) {

		if (cellName == null)
			return setSelection(-1, -1, -1, -1);

		GPoint newCell = GeoElementSpreadsheet.spreadsheetIndices(cellName);
		if (newCell.x != -1 && newCell.y != -1) {
			return setSelection(newCell.x, newCell.y);
		}
		return false;
	}

	public boolean setSelection(int c, int r) {
		CellRange cr = new CellRange(app, c, r, c, r);
		return setSelection(cr);
	}

	public boolean setSelection(int c1, int r1, int c2, int r2) {

		CellRange cr = new CellRange(app, c1, r1, c2, r2);
		if (!cr.isValid())
			return false;

		// ArrayList<CellRange> list = new ArrayList<CellRange>();
		// list.add(cr);

		return setSelection(cr);

	}

	public boolean setSelection(CellRange cr) {

		if (cr != null && !cr.isValid())
			return false;

		try {
			if (cr == null || cr.isEmptyRange()) {

				minSelectionColumn = -1;
				minSelectionRow = -1;
				maxSelectionColumn = -1;
				maxSelectionRow = -1;
				anchorSelectionColumn = -1;
				anchorSelectionRow = -1;
				leadSelectionColumn = -1;
				leadSelectionRow = -1;

			} else {

				// ?//this.setAutoscrolls(false);

				// row selection
				if (cr.isRow()) {
					setRowSelectionInterval(cr.getMinRow(), cr.getMaxRow());

					// column selection
				} else if (cr.isColumn()) {
					setColumnSelectionInterval(cr.getMinColumn(), cr.getMaxColumn());

					// cell block selection
				} else {
					setSelectionType(MyTable.CELL_SELECT);
					changeSelection(cr.getMinRow(), cr.getMinColumn(), false,
					        false);
					changeSelection(cr.getMaxRow(), cr.getMaxColumn(), false,
					        true);
				}

				selectionChanged();

				// scroll to upper left corner of rectangle
				// ?//this.setAutoscrolls(true);

				// TODO//scrollRectToVisible(getCellRect(cr.getMinRow(),
				// TODO cr.getMinColumn(), true));
				repaint();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// TODO Handle selection for a list of cell ranges

	/*
	 * public void setSelection(ArrayList<CellRange> selection){
	 * 
	 * selectionRectangleColor = (color == null) ? SELECTED_RECTANGLE_COLOR :
	 * color;
	 * 
	 * // rectangle not drawn correctly without handle ... needs fix
	 * this.doShowDragHandle = true; // doShowDragHandle;
	 * 
	 * if (selection == null) {
	 * 
	 * setSelectionType(COLUMN_SELECT);
	 * 
	 * // clear the selection visuals and the deselect geos from here //TODO:
	 * this should be handled by the changeSelection() method
	 * selectedColumnSet.clear(); selectedRowSet.clear();
	 * this.minSelectionColumn = -1; this.minSelectionRow = -1;
	 * this.maxSelectionColumn = -1; this.maxSelectionRow = -1;
	 * app.setSelectedGeos(null); //setSelectionType(COLUMN_SELECT);
	 * view.repaint(); setSelectionType(CELL_SELECT);
	 * 
	 * } else {
	 * 
	 * for (CellRange cr : selection) {
	 * 
	 * this.setAutoscrolls(false);
	 * 
	 * if (cr.isRow()) { setRowSelectionInterval(cr.getMinRow(),
	 * cr.getMaxRow()); } else if (cr.isColumn()) {
	 * setColumnSelectionInterval(cr.getMinColumn(), cr .getMaxColumn()); } else
	 * { changeSelection(cr.getMinRow(), cr.getMinColumn(), false, false);
	 * changeSelection(cr.getMaxRow(), cr.getMaxColumn(), false, true); }
	 * 
	 * // scroll to upper left corner of rectangle
	 * 
	 * this.setAutoscrolls(true);
	 * scrollRectToVisible(getCellRect(cr.getMinRow(), cr.getMinColumn(),
	 * true)); }
	 * 
	 * 
	 * }
	 * 
	 * }
	 */

	public void setSelectionType(int selType) {

		if (view.isColumnSelect()) {
			selType = MyTable.COLUMN_SELECT;
		}

		switch (selType) {

		case MyTable.CELL_SELECT:
			setColumnSelectionAllowed(true);
			setRowSelectionAllowed(true);
			break;

		case MyTable.ROW_SELECT:
			setColumnSelectionAllowed(false);
			setRowSelectionAllowed(true);
			break;

		case MyTable.COLUMN_SELECT:
			setColumnSelectionAllowed(true);
			setRowSelectionAllowed(false);
			break;

		}

		// in web, selectionType should do what setSelectionMode do too
		this.selectionType = selType;

	}

	public void setColumnSelectionAllowed(boolean allow) {
		columnSelectionAllowed = allow;
	}

	public void setRowSelectionAllowed(boolean allow) {
		rowSelectionAllowed = allow;
	}

	public int getSelectionType() {
		return selectionType;
	}

	// By adding a call to selectionChanged in JTable's setRowSelectionInterval
	// and setColumnSelectionInterval methods, selectionChanged becomes
	// the sole handler for selection events.
	public void setRowSelectionInterval(int row0, int row1) {
		setSelectionType(MyTable.ROW_SELECT);
		anchorSelectionRow = row0 - 1;
		leadSelectionRow = row1 - 1;
		selectionChanged();
	}

	public void setColumnSelectionInterval(int col0, int col1) {
		setSelectionType(MyTable.COLUMN_SELECT);
		anchorSelectionColumn = col0 - 1;
		leadSelectionColumn = col1 - 1;
		selectionChanged();
	}

	private boolean isSelectAll = false;
	private boolean isSelectNone = false;

	public boolean isSelectNone() {
		return isSelectNone;
	}

	public void setSelectNone(boolean isSelectNone) {

		this.isSelectNone = isSelectNone;

		if (isSelectNone == true) {
			setSelection(-1, -1, -1, -1);
			// TODO//view.updateFormulaBar();
		}

	}

	public boolean isSelectAll() {
		return isSelectAll;
	}

	public void setSelectAll(boolean isSelectAll) {
		this.isSelectAll = isSelectAll;
	}

	public ArrayList<Integer> getSelectedColumnsList() {

		ArrayList<Integer> columns = new ArrayList<Integer>();

		for (CellRange cr : this.selectedCellRanges) {
			for (int c = cr.getMinColumn(); c <= cr.getMaxColumn(); ++c) {
				if (!columns.contains(c))
					columns.add(c);
			}
		}
		return columns;
	}

	// @Override
	public int[] getSelectedColumns() {

		ArrayList<Integer> columns = getSelectedColumnsList();
		int[] ret = new int[columns.size()];
		for (int c = 0; c < columns.size(); c++)
			ret[c] = columns.get(c);

		return ret;
	}

	// ===============================================================
	// Selection Utilities
	// ===============================================================

	public GColor getSelectionRectangleColor() {
		return selectionRectangleColor;
	}

	public void setSelectionRectangleColor(GColor color) {
		selectionRectangleColor = color;
	}

	protected GPoint getPixel(int column, int row, boolean min) {
		if (column < 0 || row < 0) {
			return null;
		}
		if (min && column == 0 && row == 0) {
			return new GPoint(0, 0);
		}

		Widget wt = getWidget(row, column);
		if (min) {
			return new GPoint(wt.getAbsoluteLeft(), wt.getAbsoluteTop());
		}
		return new GPoint(wt.getAbsoluteLeft() + wt.getOffsetWidth(),
		        wt.getAbsoluteTop() + wt.getOffsetHeight());
	}

	protected GPoint getMinSelectionPixel() {
		return getPixel(minSelectionColumn, minSelectionRow, true);
	}

	protected GPoint getMaxSelectionPixel() {
		return getPixel(maxSelectionColumn, maxSelectionRow, false);
	}

	/**
	 * Returns Point(columnIndex, rowIndex), cell indices for the given pixel
	 * location
	 */
	public GPoint getIndexFromPixel(int x, int y) {
		if (x < 0 || y < 0)
			return null;
		int indexX = -1;
		int indexY = -1;
		for (int i = 0; i < getColumnCount(); ++i) {
			GPoint point = getPixel(i, 0, false);
			if (x < point.getX()) {
				indexX = i;
				break;
			}
		}
		if (indexX == -1) {
			return null;
		}
		for (int i = 0; i < getRowCount(); ++i) {
			GPoint point = getPixel(0, i, false);
			if (y < point.getY()) {
				indexY = i;
				break;
			}
		}
		if (indexY == -1) {
			return null;
		}
		return new GPoint(indexX, indexY);
	}

	/*
	 * public GRectangle getCellBlockRect(int column1, int row1, int column2,
	 * int row2, boolean includeSpacing) { GRectangle r1 = getCellRect(row1,
	 * column1, includeSpacing); GRectangle r2 = getCellRect(row2, column2,
	 * includeSpacing); r1.setBounds((int)r1.getX(), (int)r1.getY(),
	 * (int)((r2.getX() - r1.getX()) + r2.getWidth()), (int)((r2.getY() -
	 * r1.getY()) + r2.getHeight())); return r1; }
	 * 
	 * public Rectangle getSelectionRect(boolean includeSpacing) { return
	 * getCellBlockRect(minSelectionColumn, minSelectionRow, maxSelectionColumn,
	 * maxSelectionRow, includeSpacing); }
	 */

	// target selection frame
	// =============================
	private GRectangle targetcellFrame;

	public GRectangle getTargetcellFrame() {
		return targetcellFrame;
	}

	public void setTargetcellFrame(GRectangle targetcellFrame) {
		this.targetcellFrame = targetcellFrame;
	}

	final static float dash1[] = { 2.0f };
	final static GBasicStrokeW dashed = new GBasicStrokeW(3.0f,
	        GBasicStrokeW.CAP_BUTT, GBasicStrokeW.JOIN_MITER, 10.0f, dash1,
	        0.0f);

	// ===============================================================
	// Paint
	// ===============================================================

	/**
	 * Overrides the paint() to draw special spreadsheet table graphics, e.g.
	 * selection rectangle and custom borders
	 */
	/* @Override
	public void paint(Graphics graphics) { super.paint(graphics);

		Graphics2D g2 = (Graphics2D) graphics;

		// draw custom borders
		SpreadsheetBorders.drawFormatBorders(g2, this);

		// draw special target cell frame
		if (targetcellFrame != null) {
			g2.setColor(geogebra.awt.GColorD
					.getAwtColor(GeoGebraColorConstants.DARKBLUE)); g2.setStroke(dashed);
			g2.draw(targetcellFrame);
		}

		// if the spreadsheet doesn't have focus
		// then don't draw the selection graphics ... exit now
		if (!view.hasViewFocus()) {
			if (!isSelectNone)
				setSelectNone(true);
			return;
		}

		//draw special dragging frame for cell editor
		if (isDragging2) {
			GPoint point1 = getPixel(minColumn2, minRow2, true);
			GPoint point2 = getPixel(maxColumn2, maxRow2, false);
			int x1 = point1.getX();
			int y1 = point1.getY();
			int x2 = point2.getX();
			int y2 = point2.getY();
			graphics.setColor(Color.GRAY);
			// Application.debug(x1 + "," + y1 + "," + x2 + "," + y2);
			graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS1);
			graphics.fillRect(x1, y1, LINE_THICKNESS1, y2 - y1);
			graphics.fillRect(x1, y2 - LINE_THICKNESS1, x2 - x1, LINE_THICKNESS1);
			graphics.fillRect(x2 - LINE_THICKNESS1, y1, LINE_THICKNESS1, y2 - y1);
		}

		// draw dragging frame
		if (dragingToRow != -1 && dragingToColumn != -1) {*/
	/*
	 * Application.debug("minSelectionRow = " + minSelectionRow);
	 * Application.debug("minSelectionColumn = " + minSelectionColumn);
	 * Application.debug("maxSelectionRow = " + maxSelectionRow);
	 * Application.debug("maxSelectionColumn = " + maxSelectionColumn);
	 * Application.debug("dragingToRow = " + dragingToRow);
	 * Application.debug("dragingToColumn = " + dragingToColumn); /*
	 */
	/*	// -|1|- // 2|-|3 // -|4|-
			graphics.setColor(Color.gray);
			if (dragingToColumn < minSelectionColumn) { // 2
				GPoint point1 = getPixel(dragingToColumn, minSelectionRow, true);
				GPoint point2 = getPixel(minSelectionColumn - 1, maxSelectionRow, false);
				int x1 = point1.getX();
				int y1 = point1.getY();
				int x2 = point2.getX();
				int y2 = point2.getY();
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS1);
				graphics.fillRect(x1, y1, LINE_THICKNESS1, y2 - y1);
				graphics.fillRect(x1, y2 - LINE_THICKNESS1, x2 - x1, LINE_THICKNESS1);
			}
			else if (dragingToRow > maxSelectionRow) { // 4
				GPoint point1 = getPixel(minSelectionColumn, maxSelectionRow + 1, true);
				GPoint point2 = getPixel(maxSelectionColumn, dragingToRow, false);
				int x1 = point1.getX();
				int y1 = point1.getY();
				int x2 = point2.getX();
				int y2 = point2.getY();
				graphics.fillRect(x1, y1, LINE_THICKNESS1, y2 - y1);
				graphics.fillRect(x1, y2 - LINE_THICKNESS1, x2 - x1, LINE_THICKNESS1);
				graphics.fillRect(x2 - LINE_THICKNESS1, y1, LINE_THICKNESS1, y2 - y1);
			}
			else if (dragingToRow < minSelectionRow) { // 1
				GPoint point1 = getPixel(minSelectionColumn, dragingToRow, true);
				GPoint point2 = getPixel(maxSelectionColumn, minSelectionRow - 1, false);
				int x1 = point1.getX();
				int y1 = point1.getY();
				int x2 = point2.getX();
				int y2 = point2.getY();
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS1);
				graphics.fillRect(x1, y1, LINE_THICKNESS1, y2 - y1);
				graphics.fillRect(x2 - LINE_THICKNESS1, y1, LINE_THICKNESS1, y2 - y1);
			} else if (dragingToColumn > maxSelectionColumn) { // 3
				GPoint point1 = getPixel(maxSelectionColumn + 1, minSelectionRow, true);
				GPoint point2 = getPixel(dragingToColumn, maxSelectionRow, false);
				int x1 = point1.getX();
				int y1 = point1.getY();
				int x2 = point2.getX();
				int y2 = point2.getY();
				graphics.fillRect(x2 - LINE_THICKNESS1, y1, LINE_THICKNESS1, y2 - y1);
				graphics.fillRect(x1, y2 - LINE_THICKNESS1, x2 - x1, LINE_THICKNESS1);
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS1);
			}
		}
		// draw dragging dot GPoint pixel1 = getMaxSelectionPixel();
		if (doShowDragHandle && pixel1 != null && !editor.isEditing()) {
			// Highlight the dragging dot if mouseover
			if (isOverDot) {
				graphics.setColor(Color.gray);
			}
			else // {graphics.setColor(Color.BLUE);}
			{
				graphics.setColor(selectionRectangleColor);
			}
			int x = pixel1.getX() - (DOT_SIZE + 1) / 2;
			int y = pixel1.getY() - (DOT_SIZE + 1) / 2;
			graphics.fillRect(x, y, DOT_SIZE, DOT_SIZE);
		}

		if (minSelectionRow != -1 && maxSelectionRow != -1 && minSelectionColumn
			!= -1 && maxSelectionColumn != -1) {
			GPoint min = this.getMinSelectionPixel();
			GPoint max = this.getMaxSelectionPixel();
			int x1 = min.getX();
			int y1 = min.getY();
			int x2 = max.getX();
			int y2 = max.getY();

			// graphics.setColor(Color.BLUE);
			graphics.setColor(selectionRectangleColor);

			// draw frame around current selection
			// G.Sturr 2009-9-23 adjusted parameters to work with getPixel fix
			if (!editor.isEditing()) {
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS2);
				graphics.fillRect(x1, y1, LINE_THICKNESS2, y2 - y1);
				graphics.fillRect(x2 - LINE_THICKNESS2, y1, LINE_THICKNESS2, y2 - y1 - DOT_SIZE / 2 - 1);
				graphics.fillRect(x1, y2 - LINE_THICKNESS2, x2 - x1 - DOT_SIZE / 2 - 1, LINE_THICKNESS2);
			}
			// draw small frame around current editing cell
			else
			{
				x1 -= LINE_THICKNESS2 - 1;
				x2 += LINE_THICKNESS2 - 1;
				y1 -= LINE_THICKNESS2 - 1;
				y2 += LINE_THICKNESS2 - 1;
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS2);
				graphics.fillRect(x1, y1, LINE_THICKNESS2, y2 - y1);
				graphics.fillRect(x2 - LINE_THICKNESS2, y1, LINE_THICKNESS2, y2 - y1);
				graphics.fillRect(x1, y2 - LINE_THICKNESS2, x2 - x1, LINE_THICKNESS2);
			}
		}

		// After rendering the LaTeX image for a geo, update the row height
		//with the preferred size set by the renderer.
		resizeMarkedCells();
	}*/

	/**
	 * Starts in-cell editing for cells with short editing strings. For strings
	 * longer than MAX_CELL_EDIT_STRING_LENGTH, the redefine dialog is shown.
	 * Also prevents fixed cells from being edited.
	 */
	public boolean editCellAt(int row, int col) {
		Object ob = tableModel.getValueAt(row-1, col-1);

		// prepare editor to handle equals
		editor.setEqualsRequired(app.getSettings().getSpreadsheet().equalsRequired());
		if (ob instanceof GeoElement) {
			GeoElement geo = (GeoElement) ob;
			if (geo.isGeoButton() || geo.isGeoImage()) {
				app.getDialogManager().showPropertiesDialog();
				return true;
			}
			if (!view.getShowFormulaBar()) {
				if (!geo.isFixed()) {
					if (!geo.isGeoText() &&
					editor.getEditorInitString(geo).length() > MAX_CELL_EDIT_STRING_LENGTH) {
						app.getDialogManager().showRedefineDialog(geo, false);
						return true;
					}
					if (geo.isGeoText() && ((GeoText) geo).isLaTeX()) {
						app.getDialogManager().showRedefineDialog(geo, true);
						return true;
					}
				}
			}
		}
		// STANDARD case: in cell editing
		if (isCellEditable(row - 1, col - 1) && !isEditing) {
			isEditing = true;
			editRow = row;
			editColumn = col;
			Object mce = getCellEditor(row, col);

			// do this now, and do it later in renderCells - memorized row and col
			AutoCompleteTextFieldW w = (AutoCompleteTextFieldW)
				((MyCellEditorW)mce).getTableCellEditorWidget(this, ob, false, row, col);
			w.getTextField().setHeight((minimumRowHeight-minusRowHeight)+"px");
			w.getTextField().setWidth((preferredColumnWidth-minusColumnWidth)+"px");
			setWidget(row, col, w);
			getCellFormatter().getElement(row, col).getStyle().setBorderColor(TABLE_GRID_COLOR.toString());
			getCellFormatter().getElement(row, col).getStyle().setBorderStyle(Style.BorderStyle.SOLID);
			return true;
		}
		getCellEditor(row, col).cancelCellEditing();
		return false;// TODO: implementation needed
		//return super.editCellAt(row, col);
	}

	// This handles ctrl-select dragging of cell blocks
	// because JTable does not do this correctly.
	// TODO: JTable is still making selections that are not overridden,
	// so sometimes you can still get unwanted extended selection.
	//
	/*
	 * protected void handleControlDragSelect(MouseEvent e) {
	 * 
	 * java.awt.Point p = e.getPoint(); int row = this.rowAtPoint(p); int column
	 * = this.columnAtPoint(p); ListSelectionModel cm =
	 * getColumnModel().getSelectionModel(); ListSelectionModel rm =
	 * getSelectionModel();
	 */
	/*
	 * //handle startup case of empty selection if ((column == -1) && (row ==
	 * -1)){ cm.setSelectionInterval(0, 0); rm.setSelectionInterval(0, 0); }
	 */
	/*
	 * if ((column == -1) || (row == -1)) { return; }
	 * 
	 * // adjust the selection if mouse has left the old selected cell if (row
	 * != this.getSelectedRow() || column != this.getSelectedColumn()) { //
	 * boolean selected = true; int colAnchor = cm.getAnchorSelectionIndex();
	 * int rowAnchor = rm.getAnchorSelectionIndex();
	 * 
	 * if (rowAnchor == -1 || rowAnchor >= getRowCount()) { rowAnchor = 0; //
	 * selected = false; }
	 * 
	 * if (colAnchor == -1 || colAnchor >= getColumnCount()) { colAnchor = 0; //
	 * selected = false; }
	 * 
	 * // selected = selected && isCellSelected(rowAnchor, colAnchor);
	 * 
	 * cm.setSelectionInterval(colAnchor, column);
	 * rm.setSelectionInterval(rowAnchor, row);
	 * 
	 * selectionChanged();
	 * 
	 * }
	 * 
	 * }
	 */

	// @Override
	public int convertColumnIndexToModel(int viewColumnIndex) {
		return viewColumnIndex;
	}

	private boolean allowEditing = false;

	public boolean isAllowEditing() {
		return allowEditing;
	}

	public void setAllowEditing(boolean allowEditing) {
		this.allowEditing = allowEditing;
	}

	/*
	 * we need to return false for this normally, otherwise we can't detect
	 * double-clicks
	 */
	public boolean isCellEditable(int row, int column) {

		if (view.isColumnSelect())
			return false;

		// allow use of special editors for e.g. buttons, lists
		if (view.allowSpecialEditor() &&
			oneClickEditMap.containsKey(new GPoint(column, row)))
			return true;

		// normal case: return false so we can handle double click in our // mouseReleased
		if (!allowEditing)
			return false;

		// prevent editing fixed geos when allowEditing == true
		GeoElement geo = (GeoElement) getModel().getValueAt(row, column);
		if (geo != null && geo.isFixed())
			return false;

		// return true when editing is allowed (mostly for blank cells). This
		// lets
		// the JTable mousePressed listener catch double clicks and invoke the
		// editor
		return true;
	}

	public void updateEditor(String text) {
		if (this.isEditing()) {
			editor.setText(text);
		}
	}

	public void finishEditing() {
		isEditing = false;
		editRow = -1;
		editColumn = -1;

		setRepaintAll();//TODO: don't call renderCells, just change the edited cell
		repaint();
	}

	/*
	 * public void focusGained(FocusEvent e) { if
	 * (AppD.isVirtualKeyboardActive())
	 * ((GuiManagerD)app.getGuiManager()).toggleKeyboard(true);
	 * 
	 * }
	 * 
	 * public void focusLost(FocusEvent e) { // avoid infinite loop! if
	 * (e.getOppositeComponent() instanceof VirtualKeyboard) return; if
	 * (AppD.isVirtualKeyboardActive())
	 * ((GuiManagerD)app.getGuiManager()).toggleKeyboard(false);
	 * 
	 * }
	 */

	// Keep row heights of table and rowHeader in sync
	public void setRowHeight(int row, int rowHeight) {
		int rowHeight2 = rowHeight;
		if (rowHeight2 < minimumRowHeight)
			rowHeight2 = minimumRowHeight;

		getRowFormatter().getElement(row).getStyle()
		        .setHeight(rowHeight2, Style.Unit.PX);
		try {
			if (view != null) {
				// TODO//view.updateRowHeader();
				if (doRecordRowHeights)
					adjustedRowHeights.add(new GPoint(row, rowHeight));
				view.updateRowHeightSetting(row, rowHeight);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setRowHeight(int rowHeight) {
		int rowHeight2 = rowHeight;
		if (rowHeight2 < minimumRowHeight)
			rowHeight2 = minimumRowHeight;

		for (int i = 0; i < getRowCount(); i++)
			getRowFormatter().getElement(i).getStyle()
			        .setHeight(rowHeight2, Style.Unit.PX);
		try {
			if (view != null) {
				// TODO//view.updateRowHeader();
				view.updatePreferredRowHeight(rowHeight);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Reset the row heights --- used after addColumn destroys the row heights
	public void resetRowHeights() {
		doRecordRowHeights = false;
		for (GPoint p : adjustedRowHeights) {
			int rowHeight2 = p.y;
			if (rowHeight2 < minimumRowHeight)
				rowHeight2 = minimumRowHeight;
			setRowHeight(p.x, rowHeight2);
		}
		doRecordRowHeights = true;
	}

	// ==================================================
	// Table row and column size adjustment methods
	// ==================================================

	/**
	 * Enlarge the row and/or column of all marked cells. A cell is marked by
	 * placing it in one of two hashSets: cellResizeHeightSet or
	 * cellResizeWidthSet. Currently, this is only done after a geo is added to
	 * a cell and the row needs to be widened to fit the LaTeX image.
	 * 
	 */
	public void resizeMarkedCells() {

		if (!cellResizeHeightSet.isEmpty()) {
			for (GPoint cellPoint : cellResizeHeightSet) {
				setPreferredCellSize(cellPoint.getY(), cellPoint.getX(), false,
				        true);
			}
			cellResizeHeightSet.clear();
		}

		if (!cellResizeWidthSet.isEmpty()) {
			for (GPoint cellPoint : cellResizeWidthSet) {
				setPreferredCellSize(cellPoint.getY(), cellPoint.getX(), true,
				        false);
			}
			cellResizeWidthSet.clear();
		}
	}

	/**
	 * Enlarge the row and/or column of a cell to fit the cell's preferred size.
	 */
	public void setPreferredCellSize(int row, int col, boolean adjustWidth,
	        boolean adjustHeight) {

		Widget prefWidget = defaultTableCellRenderer
		        .getTableCellRendererWidget(this,
		                tableModel.getValueAt(row + 1, col + 1), false, false,
		                row, col);

		if (adjustWidth) {

			Element tableColumn = getColumnFormatter().getElement(col);

			int resultWidth = Math.max(tableColumn.getOffsetWidth(),
			        (int) prefWidget.getOffsetWidth());
			tableColumn.getStyle().setWidth(resultWidth + 1 /*
															 * TODO this.
															 * getIntercellSpacing
															 * ().width
															 */
			, Style.Unit.PX);
		}

		if (adjustHeight) {

			int resultHeight = Math.max(getRowFormatter().getElement(row)
			        .getOffsetHeight(), (int) prefWidget.getOffsetHeight());
			int rowHeight2 = resultHeight;
			if (rowHeight2 < minimumRowHeight)
				rowHeight2 = minimumRowHeight;
			setRowHeight(row, rowHeight2);
		}

	}

	/**
	 * Adjust the width of a column to fit the maximum preferred width of its
	 * cell contents.
	 */
	public void fitColumn(int column) {

		Element tableColumn = getColumnFormatter().getElement(column);

		int prefWidth = 0;
		int tempWidth = -1;
		for (int row = 1; row < getRowCount(); row++) {
			if (tableModel.getValueAt(row - 1, column - 1) != null) {
				tempWidth = defaultTableCellRenderer
				        .getTableCellRendererWidget(this,
				                tableModel.getValueAt(row - 1, column - 1),
				                false, false, row, column).getOffsetWidth();
				prefWidth = Math.max(prefWidth, tempWidth);
			}
		}

		// set the new column width
		if (tempWidth == -1) {
			// column is empty
			prefWidth = preferredColumnWidth - 1 /*
												 * TODO
												 * getIntercellSpacing().width
												 */;
		} else {
			prefWidth = Math.max(prefWidth, 15 /*
												 * TODO
												 * tableColumn.getMinWidth()
												 */);
		}
		// note: the table might have its header set to null,
		// so we get the actual header from view
		// TODO//view.getTableHeader().setResizingColumn(tableColumn);
		tableColumn.getStyle().setWidth(prefWidth + 1 /*
													 * TODO
													 * getIntercellSpacing()
													 * .width
													 */
		, Style.Unit.PX);
	}

	/**
	 * Adjust the height of a row to fit the maximum preferred height of the its
	 * cell contents.
	 */
	public void fitRow(int row) {

		int prefHeight = getRowFormatter().getElement(row).getOffsetHeight();
		// int prefHeight = this.getRowHeight();
		int tempHeight = 0;
		for (int column = 1; column < this.getColumnCount(); column++) {

			tempHeight = defaultTableCellRenderer.getTableCellRendererWidget(
			        this, tableModel.getValueAt(row - 1, column - 1), false,
			        false, row, column).getOffsetHeight();

			prefHeight = Math.max(prefHeight, tempHeight);

		}

		// set the new row height
		this.setRowHeight(row, prefHeight);
	}

	/**
	 * Adjust all rows/columns to fit the maximum preferred height/width of
	 * their cell contents.
	 * 
	 */
	public void fitAll(boolean doRows, boolean doColumns) {
		if (doRows) {
			for (int row = 0; row < getRowCount(); row++) {
				fitRow(row);
			}
		}
		if (doColumns) {
			for (int column = 0; column < getColumnCount(); column++) {
				// ?//fitRow(column);
				fitColumn(column);
			}
		}
	}

	/**
	 * Column model listener --- used to reset the preferred column width when
	 * all columns have been selected.
	 */
	/*
	 * public class MyTableColumnModelListener implements
	 * TableColumnModelListener {
	 * 
	 * public void columnMarginChanged(ChangeEvent e) { if (isSelectAll() &&
	 * minSelectionColumn >= 0) { preferredColumnWidth =
	 * getColumnModel().getColumn( minSelectionColumn).getPreferredWidth(); //
	 * view.updatePreferredColumnWidth(preferredColumnWidth); } // TODO: find
	 * more efficient way to record column widths
	 * view.updateAllColumnWidthSettings(); }
	 * 
	 * public void columnAdded(TableColumnModelEvent arg0) { }
	 * 
	 * public void columnMoved(TableColumnModelEvent arg0) { }
	 * 
	 * public void columnRemoved(TableColumnModelEvent arg0) { }
	 * 
	 * public void columnSelectionChanged(ListSelectionEvent arg0) { } }
	 */

	// When the spreadsheet is smaller than the viewport fill the extra space
	// with
	// the same background color as the spreadsheet.
	// This gives a smoother look when the spreadsheet auto-adjusts to fill the
	// space.

	/*
	 * @Override protected void configureEnclosingScrollPane() {
	 * super.configureEnclosingScrollPane(); Container p = getParent(); if (p
	 * instanceof JViewport) { ((JViewport) p).setBackground(getBackground()); }
	 * }
	 */

	// ==================================================
	// Table mode change
	// ==================================================

	public int getTableMode() {
		return tableMode;
	}

	/**
	 * Sets the table mode
	 * 
	 * @param tableMode
	 */
	public void setTableMode(int tableMode) {

		if (tableMode == MyTable.TABLE_MODE_AUTOFUNCTION) {

			if (!initAutoFunction())
				return;
		}

		else if (tableMode == MyTable.TABLE_MODE_DROP) {
			// nothing to do (yet)
		}

		else {
			// Clear the targetcellFrame and ensure the selection rectangle
			// color is standard
			targetcellFrame = null;
			this.setSelectionRectangleColor(GColor.BLUE);
		}

		this.tableMode = tableMode;
		repaint();
	}

	// ==================================================
	// Autofunction handlers
	// ==================================================

	/**
	 * Initializes the autoFunction feature. The targetCell is prepared and the
	 * GUI is adjusted to handle selection drag with an autoFunction
	 */
	protected boolean initAutoFunction() {

		// Selection is a single cell.
		// The selected cell is the target cell. Allow the user to drag a new
		// selection for the
		// autoFunction. The autoFunction values are previewed in the targetCell
		// while dragging.
		if (selectedCellRanges.size() == 1
		        && selectedCellRanges.get(0).isSingleCell()) {

			// Clear the target cell, exit if this is not possible
			if (RelativeCopy.getValue(app, minSelectionColumn, minSelectionRow) != null) {
				boolean isOK = copyPasteCut.delete(minSelectionColumn,
				        minSelectionRow, minSelectionColumn, minSelectionRow);
				if (!isOK)
					return false;
			}

			// Set targetCell as a GeoNumeric that can be used to preview the
			// autofunction result
			// (later it will be set as a GeoList)
			targetCell = new GeoNumeric(kernel.getConstruction(), 0);
			targetCell.setLabel(GeoElementSpreadsheet.getSpreadsheetCellName(
			        minSelectionColumn, minSelectionRow));
			targetCell.setUndefined();

			// Set the targetcellFrame so the Paint method can use it to draw a
			// dashed frame - will be implemented differently in the web version
			// (TODO)
			// targetcellFrame = this.getCellBlockRect(minSelectionColumn,
			// minSelectionRow, minSelectionColumn, minSelectionRow, true);

			// Change the selection frame color to gray
			// and clear the current selection
			setSelectionRectangleColor(GColor.GRAY);
			minSelectionColumn = -1;
			maxSelectionColumn = -1;
			minSelectionRow = -1;
			maxSelectionRow = -1;
			app.clearSelectedGeos();

		}

		// try to create autoFunction cell(s) adjacent to the selection
		else if (selectedCellRanges.size() == 1) {

			try {
				performAutoFunctionCreation(selectedCellRanges.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Don't stay in this mode, we're done
			return false;
		}

		// Exit if any other type of selection exists
		else
			return false;

		return true;
	}

	/**
	 * Creates autofunction cells based on the given cell range and the current
	 * autofunction mode.
	 */
	protected void performAutoFunctionCreation(CellRange cr) {

		if (cr.isColumn() || cr.isRow())
			return;

		boolean isOK = true;
		GeoElement targetCell = null;
		CellRange targetRange;

		// Case 1: Partial row, targetCell created beneath the column
		if (cr.isPartialRow() || (!cr.isPartialColumn() && AppW.getShiftDown())) {
			targetRange = new CellRange(app, cr.getMaxColumn() + 1,
			        cr.getMinRow(), cr.getMaxColumn() + 1, cr.getMaxRow());
			for (int row = cr.getMinRow(); row <= cr.getMaxRow(); row++) {

				// try to clear the target cell, exit if this is not possible
				if (RelativeCopy.getValue(app, cr.getMaxColumn() + 1, row) != null) {
					isOK = copyPasteCut.delete(cr.getMaxColumn() + 1, row,
					        cr.getMaxColumn() + 1, row);
				}
				// create new targetCell
				if (isOK) {
					targetCell = new GeoNumeric(kernel.getConstruction(), 0);
					targetCell
					        .setLabel(GeoElementSpreadsheet
					                .getSpreadsheetCellName(
					                        cr.getMaxColumn() + 1, row));
					createAutoFunctionCell(
					        targetCell,
					        new CellRange(app, cr.getMinColumn(), row, cr
					                .getMaxColumn(), row));
				}
			}

			app.setMoveMode();
			setSelection(targetRange);
			repaint();
		} else {

			targetRange = new CellRange(app, cr.getMinColumn(),
			        cr.getMaxRow() + 1, cr.getMaxColumn(), cr.getMaxRow() + 1);
			for (int col = cr.getMinColumn(); col <= cr.getMaxColumn(); col++) {

				// try to clear the target cell, exit if this is not possible
				if (RelativeCopy.getValue(app, col, cr.getMaxRow() + 1) != null) {
					isOK = copyPasteCut.delete(col, cr.getMaxRow() + 1, col,
					        cr.getMaxRow() + 1);
				}
				// create new targetCell
				if (isOK) {
					targetCell = new GeoNumeric(kernel.getConstruction(), 0);
					targetCell.setLabel(GeoElementSpreadsheet
					        .getSpreadsheetCellName(col, cr.getMaxRow() + 1));
					createAutoFunctionCell(targetCell, new CellRange(app, col,
					        cr.getMinRow(), col, cr.getMaxRow()));
				}
			}

			app.setMoveMode();
			setSelection(targetRange);
			repaint();
		}
	}

	/**
	 * Stops the autofunction from updating and creates a new geo for the target
	 * cell based on the current autofunction mode.
	 */
	protected void stopAutoFunction() {

		setTableMode(MyTable.TABLE_MODE_STANDARD);

		if (createAutoFunctionCell(targetCell, selectedCellRanges.get(0))) {
			// select the new geo
			app.setMoveMode();
			GPoint coords = targetCell.getSpreadsheetCoords();
			changeSelection(coords.y, coords.x, false, false);
			repaint();
		}
	}

	/**
	 * Creates an autofunction in the given target cell based on the current
	 * autofunction mode and the given cell range.
	 */
	protected boolean createAutoFunctionCell(GeoElement targetCell, CellRange cr) {

		boolean success = true;

		// Get the targetCell label and the selected cell range
		String targetCellLabel = targetCell.getLabelSimple();
		String cellRangeString = getCellRangeProcessor().getCellRangeString(cr);

		// Create a String expression for the new autofunction command geo
		String cmd = null;
		if (view.getMode() == EuclidianConstants.MODE_SPREADSHEET_SUM)
			cmd = "Sum";
		else if (view.getMode() == EuclidianConstants.MODE_SPREADSHEET_COUNT)
			cmd = "Length";
		else if (view.getMode() == EuclidianConstants.MODE_SPREADSHEET_AVERAGE)
			cmd = "Mean";
		else if (view.getMode() == EuclidianConstants.MODE_SPREADSHEET_MAX)
			cmd = "Max";
		else if (view.getMode() == EuclidianConstants.MODE_SPREADSHEET_MIN)
			cmd = "Min";

		String expr = targetCellLabel + " = " + cmd + "[" + cellRangeString
		        + "]";

		// Create the new geo
		if (!selectedCellRanges.get(0).contains(targetCell)) {
			kernel.getAlgebraProcessor().processAlgebraCommandNoExceptions(
			        expr, false);
		} else {
			targetCell.setUndefined();
			success = false;
		}

		return success;
	}

	/**
	 * Updates the autofunction by recalculating the autofunction value as the
	 * user drags the mouse to create a selection. The current autofunction
	 * value is displayed in the targetCell.
	 */
	public void updateAutoFunction() {

		if (targetCell == null || selectedCellRanges.get(0).isEmpty()
		        || tableMode != MyTable.TABLE_MODE_AUTOFUNCTION) {
			app.setMoveMode();
			return;
		}

		// Get a string representation of the seleced range (e.g. A1:B3)
		String cellRangeString = getCellRangeProcessor().getCellRangeString(
		        selectedCellRanges.get(0));

		// Build a String expression for the autofunction
		String cmd = null;
		if (view.getMode() == EuclidianConstants.MODE_SPREADSHEET_SUM)
			cmd = "Sum";
		else if (view.getMode() == EuclidianConstants.MODE_SPREADSHEET_COUNT)
			cmd = "Length";
		else if (view.getMode() == EuclidianConstants.MODE_SPREADSHEET_AVERAGE)
			cmd = "Mean";
		else if (view.getMode() == EuclidianConstants.MODE_SPREADSHEET_MAX)
			cmd = "Max";
		else if (view.getMode() == EuclidianConstants.MODE_SPREADSHEET_MIN)
			cmd = "Min";

		String expr = cmd + "[" + cellRangeString + "]";

		// Evaluate the autofunction and put the result in targetCell
		if (!selectedCellRanges.get(0).contains(targetCell)) {
			((GeoNumeric) targetCell).setValue(kernel.getAlgebraProcessor()
			        .evaluateToDouble(expr));
		} else {
			((GeoNumeric) targetCell).setUndefined();
		}
	}

	// ===========================================
	// copy/paste/cut/delete methods
	//
	// this is temporary code while cleaning up
	// ===========================================
	public void copy(boolean altDown) {
		copyPasteCut.copy(minSelectionColumn, minSelectionRow,
		        maxSelectionColumn, maxSelectionRow, altDown);
	}

	public boolean paste() {
		return copyPasteCut.paste(minSelectionColumn, minSelectionRow,
		        maxSelectionColumn, maxSelectionRow);
	}

	public boolean cut() {
		return copyPasteCut.cut(minSelectionColumn, minSelectionRow,
		        maxSelectionColumn, maxSelectionRow);
	}

	public boolean delete() {
		return copyPasteCut.cut(minSelectionColumn, minSelectionRow,
		        maxSelectionColumn, maxSelectionRow);
	}

	/*
	 * private static Cursor createCursor(Image cursorImage, boolean center) {
	 * Toolkit toolkit = Toolkit.getDefaultToolkit(); java.awt.Point
	 * cursorHotSpot; if (center) { cursorHotSpot = new
	 * java.awt.Point(cursorImage.getWidth(null) / 2,
	 * cursorImage.getHeight(null) / 2); } else { cursorHotSpot = new
	 * java.awt.Point(0, 0); } Cursor cursor =
	 * toolkit.createCustomCursor(cursorImage, cursorHotSpot, null); return
	 * cursor; }
	 */

	public void setRepaintAll() {
		repaintAll = true;
	}

	public void repaint() {

		if (repaintAll) {
			renderCells();
			repaintAll = false;
		}

		renderSelection();
		// TODO: implementation needed
	}

	public void renderCells() {

		Widget prob = null;
		Object gva = null;

		if (getColumnCount() != tableModel.getColumnCount() + 1) {
			updateColumnCount();
			if (getColumnCount() != tableModel.getColumnCount() + 1)
				resizeColumns(tableModel.getColumnCount() + 1);
		}

		if (getRowCount() != tableModel.getRowCount() + 1) {
			resizeRows(tableModel.getRowCount() + 1);
		}

		int colCount = getColumnCount();
		int rowCount = getRowCount();
		for (int i = colCount - 1; i >= 0; i--) {
			for (int j = rowCount - 1; j >= 0; j--) {
				if (i == 0) {
					if (j == 0) {
						prob = rowHeaderRenderer.getListCellRendererWidget("",
						        j, false, false);
						prob.getElement()
						        .getStyle()
						        .setBackgroundColor(
						                MyTableW.BACKGROUND_COLOR_HEADER
						                        .toString());
						getCellFormatter().getElement(j, i).addClassName(
						        "geogebraweb-th-corner");
					} else {
						gva = rowHeaderModel.getElementAt(j - 1);
						prob = rowHeaderRenderer.getListCellRendererWidget(gva,
						        j, false, false);
						getCellFormatter().getElement(j, i).addClassName(
						        "geogebraweb-th-rows");
					}
				} else if (j == 0) {
					gva = GeoElementSpreadsheet.getSpreadsheetColumnName(i - 1);
					prob = columnHeaderRenderer.getTableCellRendererWidget(
					        this, gva, false, false, j, i);
					getCellFormatter().getElement(j, i).addClassName(
					        "geogebraweb-th-columns");
				} else {
					gva = tableModel.getValueAt(j - 1, i - 1);
					prob = defaultTableCellRenderer.getTableCellRendererWidget(
					        this, gva, false, false, j, i);

					// just a workaround for now to show something:
				 	prob.getElement().getStyle().setBackgroundColor(GColor.WHITE.toString());
				 	getCellFormatter().getElement(j, i).getStyle().setBackgroundColor(GColor.WHITE.toString());
				}
				setWidget(j, i, prob);
				getCellFormatter().getElement(j, i).getStyle().setBorderColor(TABLE_GRID_COLOR.toString());
				getCellFormatter().getElement(j, i).getStyle().setBorderStyle(Style.BorderStyle.SOLID);
			}
		}
	}

	public void renderSelection() {

		// TODO implement other features from the old paint method

		GPoint cellPoint = new GPoint();
		GColor bgColor;
		Element operate = null;
		for (int i = getColumnCount() - 1; i >= 1; i--) {
			if (getWidget(0, i) != null)
				operate = getWidget(0, i).getElement();
			else
				operate = getCellFormatter().getElement(0, i);

			if (i >= minSelectionColumn && i <= maxSelectionColumn && selectionType != MyTable.ROW_SELECT)
				operate.getStyle().setBackgroundColor(
					MyTableW.SELECTED_BACKGROUND_COLOR_HEADER.toString());
			else
				operate.getStyle().setBackgroundColor(
					MyTableW.BACKGROUND_COLOR_HEADER.toString());
		}
		for (int j = getRowCount() - 1; j >= 1; j--) {
			if (getWidget(j, 0) != null)
				operate = getWidget(j, 0).getElement();
			else
				operate = getCellFormatter().getElement(j, 0);

			if (j >= minSelectionRow && j <= maxSelectionRow && selectionType != MyTable.COLUMN_SELECT)
				operate.getStyle().setBackgroundColor(
					MyTableW.SELECTED_BACKGROUND_COLOR_HEADER.toString());
			else
				operate.getStyle().setBackgroundColor(
					MyTableW.BACKGROUND_COLOR_HEADER.toString());
		}

		int colCount = tableModel.getHighestUsedColumn() + 2;
		int rowCount = tableModel.getHighestUsedRow() + 2;
		for (int i = colCount - 1; i >= 1; i--) {
			for (int j = rowCount - 1; j >= 1; j--) {

				if (getWidget(j, i) != null)
					operate = getWidget(j, i).getElement();
				else
					operate = getCellFormatter().getElement(j, i);

				cellPoint.setLocation(i - 1, j - 1);
				bgColor = (GColor)formatHandler.getCellFormat(cellPoint, CellFormat.FORMAT_BGCOLOR);
				GeoElement geo = null;
				if (tableModel.getValueAt(j-1, i-1) instanceof GeoElement)
					geo = (GeoElement)tableModel.getValueAt(j - 1, i - 1);

				if (bgColor == null && geo != null && geo.getBackgroundColor() != null)
					bgColor = geo.getBackgroundColor();

				// adjust selection color when there is a bgColor
				if (geo != null && geo.doHighlighting()) {
					if (bgColor != null) {
						bgColor = bgColor.darker();
					} else {
						bgColor = MyTableW.SELECTED_BACKGROUND_COLOR;
					}
				}
				if (bgColor != null)
					operate.getStyle().setBackgroundColor(bgColor.toString());
				else
					operate.getStyle().setBackgroundColor(GColor.WHITE.toString());
			}
		}

		if (minSelectionRowOld != -1 && maxSelectionRowOld != -1
		        && minSelectionColumnOld != -1 && maxSelectionColumnOld != -1) {

			// At first, the program should delete any previous selection
			// created by this method

			for (int i = minSelectionRowOld; i <= maxSelectionRowOld; i++) {
				getCellFormatter().getElement(i, minSelectionColumnOld)
				        .getStyle().setProperty("borderLeft", TABLE_GRID_COLOR.toString()+" solid 1px");
				getCellFormatter().getElement(i, maxSelectionColumnOld)
				        .getStyle().setProperty("borderRight", TABLE_GRID_COLOR.toString()+" solid 1px");
			}
			for (int i = minSelectionColumnOld; i <= maxSelectionColumnOld; i++) {
				getCellFormatter().getElement(minSelectionRowOld, i).getStyle()
				        .setProperty("borderTop", TABLE_GRID_COLOR.toString()+" solid 1px");
				getCellFormatter().getElement(maxSelectionRowOld, i)
				        .getStyle().setProperty("borderBottom", TABLE_GRID_COLOR.toString()+" solid 1px");
			}
		}

		minSelectionRowOld = minSelectionRow;
		maxSelectionRowOld = maxSelectionRow;
		minSelectionColumnOld = minSelectionColumn;
		maxSelectionColumnOld = maxSelectionColumn;

		if (minSelectionRow != -1 && maxSelectionRow != -1
		        && minSelectionColumn != -1 && maxSelectionColumn != -1) {

			for (int i = minSelectionRow; i <= maxSelectionRow; i++) {
				getCellFormatter()
				        .getElement(i, minSelectionColumn)
				        .getStyle()
				        .setProperty(
				                "borderLeft",
				                selectionRectangleColor.toString() + " solid "
				                        + LINE_THICKNESS2 + "px");
				getCellFormatter()
				        .getElement(i, maxSelectionColumn)
				        .getStyle()
				        .setProperty(
				                "borderRight",
				                selectionRectangleColor.toString() + " solid "
				                        + LINE_THICKNESS2 + "px");
			}
			for (int i = minSelectionColumn; i <= maxSelectionColumn; i++) {
				getCellFormatter()
				        .getElement(minSelectionRow, i)
				        .getStyle()
				        .setProperty(
				                "borderTop",
				                selectionRectangleColor.toString() + " solid "
				                        + LINE_THICKNESS2 + "px");
				getCellFormatter()
				        .getElement(maxSelectionRow, i)
				        .getStyle()
				        .setProperty(
				                "borderBottom",
				                selectionRectangleColor.toString() + " solid "
				                        + LINE_THICKNESS2 + "px");
			}
		}

		// After rendering the LaTeX image for a geo, update the row height
		// with the preferred size set by the renderer.
		resizeMarkedCells();
	}

	public int getSelectedRow() {//in grid (presentation) coordinates
		if (minSelectionRow > 0)
			return minSelectionRow;
		return -1;
	}

	public int getSelectedColumn() {//in grid (presentation) coordinates
		if (minSelectionColumn > 0)
			return minSelectionColumn;
		return -1;
	}

	public Widget getEditorWidget() {
		return editor.getTextfield();
	}
}
