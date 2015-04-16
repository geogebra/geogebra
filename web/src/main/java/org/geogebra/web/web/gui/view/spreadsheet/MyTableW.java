package org.geogebra.web.web.gui.view.spreadsheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.view.spreadsheet.CellFormat;
import org.geogebra.common.gui.view.spreadsheet.CellFormatInterface;
import org.geogebra.common.gui.view.spreadsheet.CellRange;
import org.geogebra.common.gui.view.spreadsheet.CellRangeProcessor;
import org.geogebra.common.gui.view.spreadsheet.CopyPasteCut;
import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
import org.geogebra.web.html5.awt.GBasicStrokeW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;
import org.geogebra.web.html5.util.SpreadsheetTableModelW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractNativeScrollbar;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MyTableW implements /* FocusListener, */MyTable {
	private static final long serialVersionUID = 1L;

	private int tableMode = MyTable.TABLE_MODE_STANDARD;

	public static final int MAX_CELL_EDIT_STRING_LENGTH = 10;

	public static final int DOT_SIZE = 6;
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
	private MyCellEditorW editor;

	// private MyCellEditorBooleanW editorBoolean;
	// private MyCellEditorButton editorButton;
	// private MyCellEditorList editorList;

	public MyCellEditorW getEditor() {
		return editor;
	}

	protected RelativeCopy relativeCopy;
	public CopyPasteCut copyPasteCut;

	protected SpreadsheetColumnControllerW scc;

	// protected SpreadsheetColumnControllerW.ColumnHeaderRenderer
	// columnHeaderRenderer;
	// protected SpreadsheetRowHeaderW.RowHeaderRenderer rowHeaderRenderer;

	protected SpreadsheetViewW view;
	protected SpreadsheetTableModel tableModel;
	private CellRangeProcessor crProcessor;
	// private MyTableColumnModelListener columnModelListener;
	MyCellRendererW defaultTableCellRenderer;

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

	// for emulating the JTable's changeSelection method, in TableModel
	// coordinates
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
	protected int dragingToRowOld = -1;
	protected int dragingToColumnOld = -1;
	protected boolean isOverDot = false;
	protected boolean isDragging = false;

	protected int minColumn = -1;
	protected int maxColumn = -1;
	protected int minRow = -1;
	protected int maxRow = -1;

	protected boolean showRowHeader = true;
	protected boolean showColumnHeader = true;

	protected boolean renderCellsFirstTime = true;

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

	boolean repaintAll = false;// sometimes only the repainting of
	                           // borders/background is needed

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
	public static int minusRowHeight = 2; // 12;
	public static int minusColumnWidth = 2; // 14;

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

	protected MyTable table;

	protected Grid ssGrid;

	public Grid getGrid() {
		return ssGrid;
	}

	protected TableScroller scroller;

	private FlowPanel tableWrapper;
	protected SpreadsheetRowHeaderW rowHeader;
	protected SpreadsheetColumnHeaderW columnHeader;

	private FlowPanel rowHeaderContainer;
	private HandlerRegistration scrollHandlerRegistration;

	public Widget getContainer() {
		return tableWrapper;
	}

	// special panels for editing and selection
	private SimplePanel selectionFrame;
	private SimplePanel dragFrame;
	private SimplePanel blueDot;
	private SimplePanel editorPanel;

	private AbsolutePanel gridPanel;

	private Grid upperLeftCorner;

	private Grid upperRightCorner;

	private FlowPanel headerRow;

	private Grid lowerLeftCorner;

	private FlowPanel ssGridContainer;

	private FlowPanel columnHeaderContainer;

	private FlowPanel cornerContainerUpperLeft;

	private FlowPanel cornerContainerLowerLeft;

	private FlowPanel cornerContainerUpperRight;

	public Grid dummyTable;

	private boolean autoScrolls = true;

	/*******************************************************************
	 * Construct table
	 */
	public MyTableW(SpreadsheetViewW view, SpreadsheetTableModel tableModel) {

		app = view.getApplication();
		kernel = app.getKernel();
		this.table = this;
		this.tableModel = tableModel;
		this.view = view;

		createFloatingElements();
		createGUI();

		cellResizeHeightSet = new HashSet<GPoint>();
		cellResizeWidthSet = new HashSet<GPoint>();

		for (int i = 0; i < getColumnCount(); ++i) {
			// TODO//getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
			ssGrid.getColumnFormatter().getElement(i).getStyle()
			        .setWidth(preferredColumnWidth, Style.Unit.PX);
		}

		// add cell renderer & editors
		defaultTableCellRenderer = new MyCellRendererW(app, view, this,
		        (CellFormat) getCellFormatHandler());

		// this needs defaultTableCellRenderer now
		((SpreadsheetTableModelW) tableModel).attachMyTable(this);

		// :NEXT:Grid.setCellFormatter
		editor = new MyCellEditorW(kernel, view, editorPanel);
		// setDefaultEditor(Object.class, editor);

		// initialize selection fields
		selectedCellRanges = new ArrayList<CellRange>();
		selectedCellRanges.add(new CellRange(app));

		selectionType = MyTable.CELL_SELECT;
		rowSelectionAllowed = columnSelectionAllowed = true;

		// add mouse and key listeners
		// scc = new SpreadsheetColumnControllerW(app, this);
		// srh = new SpreadsheetRowHeaderW(app, this);

		// key listener - old solution
		// KeyListener[] defaultKeyListeners = getKeyListeners();
		// for (int i = 0; i < defaultKeyListeners.length; ++i) {
		// removeKeyListener(defaultKeyListeners[i]);
		// }
		// addKeyListener(new SpreadsheetKeyListener(app, this));

		// addDomHandler(new SpreadsheetKeyListener(app, this),
		// KeyDownEvent.getType());

		// setup selection listener
		// TODO
		// These listeners are no longer needed.
		// getSelectionModel().addListSelectionListener(new
		// RowSelectionListener());
		// getColumnModel().getSelectionModel().addListSelectionListener(new
		// ColumnSelectionListener());
		// getColumnModel().getSelectionModel().addListSelectionListener(columnHeader);

		// add table model listener
		((SpreadsheetTableModelW) tableModel)
		        .setChangeListener(new MyTableModelListener());

		// relative copy
		relativeCopy = new RelativeCopy(kernel);
		copyPasteCut = new CopyPasteCutW(app);

		/*
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

		// rowHeaderRenderer = srh.new RowHeaderRenderer();
		// columnHeaderRenderer = scc.new ColumnHeaderRenderer();

		ssGrid.setCellPadding(0);
		ssGrid.setCellSpacing(0);
		ssGrid.getElement().addClassName("geogebraweb-table-spreadsheet");

		registerListeners();
		repaintAll();
	}

	private void registerListeners() {
		SpreadsheetMouseListenerW ml = new SpreadsheetMouseListenerW(app, this);
		gridPanel.addDomHandler(ml, MouseDownEvent.getType());
		gridPanel.addDomHandler(ml, MouseUpEvent.getType());
		gridPanel.addDomHandler(ml, MouseMoveEvent.getType());
		gridPanel.addDomHandler(ml, DoubleClickEvent.getType());
		gridPanel.addDomHandler(ml, TouchStartEvent.getType());
		gridPanel.addDomHandler(ml, TouchMoveEvent.getType());
		gridPanel.addDomHandler(ml, TouchEndEvent.getType());

		upperLeftCorner.addDomHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				selectAll();
			}
		}, ClickEvent.getType());

	}

	/**
	 * End table constructor
	 ******************************************************************/

	private void createFloatingElements() {

		editorPanel = new SimplePanel();
		editorPanel.addStyleName("editorPanel");
		editorPanel.getElement().getStyle().setZIndex(6);
		editorPanel.setVisible(false);

		selectionFrame = new SimplePanel();
		selectionFrame.addStyleName("geogebraweb-selectionframe-spreadsheet");
		selectionFrame.setVisible(false);

		dragFrame = new SimplePanel();
		dragFrame.getElement().getStyle().setZIndex(5);
		dragFrame.getElement().getStyle()
		        .setBorderStyle(Style.BorderStyle.SOLID);
		dragFrame.getElement().getStyle().setBorderWidth(2, Style.Unit.PX);
		dragFrame.getElement().getStyle()
		        .setBorderColor(GColor.GRAY.toString());
		dragFrame.setVisible(false);

		blueDot = new SimplePanel();
		blueDot.getElement().getStyle().setZIndex(7);
		blueDot.getElement().getStyle()
		        .setWidth(MyTableW.DOT_SIZE, Style.Unit.PX);
		blueDot.getElement().getStyle()
		        .setHeight(MyTableW.DOT_SIZE, Style.Unit.PX);
		blueDot.getElement().getStyle()
		        .setProperty("borderTop", "1px solid white");
		blueDot.getElement().getStyle()
		        .setProperty("borderLeft", "1px solid white");
		blueDot.getElement()
		        .getStyle()
		        .setBackgroundColor(
		                MyTableW.SELECTED_RECTANGLE_COLOR.toString());
		blueDot.setVisible(false);
		blueDot.setStyleName("cursor_default");

		dummyTable = new Grid(1, 1);
		dummyTable.getElement().getStyle()
		        .setVisibility(Style.Visibility.HIDDEN);
		dummyTable.setText(0, 0, "x");
		dummyTable.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		dummyTable.getElement().getStyle().setTop(0, Unit.PX);
		dummyTable.getElement().getStyle().setLeft(0, Unit.PX);
		dummyTable.getElement().addClassName("geogebraweb-table-spreadsheet");

	}

	private void createGUI() {

		int leftCornerWidth = SpreadsheetViewW.ROW_HEADER_WIDTH;
		int rightCornerWidth = AbstractNativeScrollbar
		        .getNativeScrollbarWidth();
		int lowerLeftCornerHeight = AbstractNativeScrollbar
		        .getNativeScrollbarHeight();

		// ------ upper left corner
		upperLeftCorner = new Grid(1, 1);
		upperLeftCorner.getElement().addClassName(
		        "geogebraweb-table-spreadsheet");
		upperLeftCorner.getCellFormatter().getElement(0, 0)
		        .addClassName("SVheader");
		upperLeftCorner.setText(0, 0, "9999");
		upperLeftCorner.setCellPadding(0);
		upperLeftCorner.setCellSpacing(0);
		Style s = upperLeftCorner.getElement().getStyle();
		
		upperLeftCorner.addStyleName("upperCorner");
		
		s.setWidth(leftCornerWidth, Unit.PX);
		//s.setBackgroundColor(BACKGROUND_COLOR_HEADER.toString());
		//s.setColor(BACKGROUND_COLOR_HEADER.toString());
		s.setPosition(Style.Position.ABSOLUTE);
		s.setTop(0, Unit.PX);
		s.setLeft(0, Unit.PX);

		// ----- upper right corner
		upperRightCorner = new Grid(1, 1);
		upperRightCorner.setText(0, 0, "xxx");
		upperRightCorner.getElement().addClassName(
		        "geogebraweb-table-spreadsheet");
		upperRightCorner.addStyleName("upperCorner");
		upperRightCorner.getCellFormatter().getElement(0, 0)
		        .addClassName("SVheader");
		s = upperRightCorner.getElement().getStyle();
		s.setWidth(rightCornerWidth, Unit.PX);
		//s.setBackgroundColor(BACKGROUND_COLOR_HEADER.toString());
		//s.setColor(BACKGROUND_COLOR_HEADER.toString());
		s.setPosition(Style.Position.ABSOLUTE);
		s.setTop(0, Unit.PX);
		s.setRight(0, Unit.PX);

		// ----- lower left corner
		lowerLeftCorner = new Grid(1, 1);
		upperRightCorner.setText(0, 0, "9999");
		lowerLeftCorner.getElement().addClassName(
		        "geogebraweb-table-spreadsheet-lowerLeftCorner");
		s = lowerLeftCorner.getElement().getStyle();
		s.setWidth(leftCornerWidth - 1, Unit.PX);
		s.setHeight(lowerLeftCornerHeight - 2, Unit.PX);
		//s.setBackgroundColor(BACKGROUND_COLOR_HEADER.toString());

		s.setPosition(Style.Position.ABSOLUTE);
		s.setLeft(0, Unit.PX);
		s.setBottom(0, Unit.PX);

		// ---- corner containers
		cornerContainerUpperLeft = new FlowPanel();
		cornerContainerUpperLeft.getElement().getStyle()
		        .setDisplay(Style.Display.BLOCK);
		cornerContainerUpperLeft.add(upperLeftCorner);
		cornerContainerLowerLeft = new FlowPanel();
		cornerContainerLowerLeft.getElement().getStyle()
		        .setDisplay(Style.Display.BLOCK);
		cornerContainerLowerLeft.add(lowerLeftCorner);
		cornerContainerUpperRight = new FlowPanel();
		cornerContainerUpperRight.getElement().getStyle()
		        .setDisplay(Style.Display.BLOCK);
		cornerContainerUpperRight.add(upperRightCorner);

		// ---- column header
		columnHeader = new SpreadsheetColumnHeaderW(app, this);
		s = columnHeader.getContainer().getElement().getStyle();
		s.setPosition(Style.Position.RELATIVE);

		columnHeaderContainer = new FlowPanel();
		s = columnHeaderContainer.getElement().getStyle();
		s.setDisplay(Display.BLOCK);
		s.setOverflow(Style.Overflow.HIDDEN);
		s.setMarginLeft(leftCornerWidth, Unit.PX);
		s.setMarginRight(rightCornerWidth, Unit.PX);
		columnHeaderContainer.add(columnHeader.getContainer());

		// ------ row header
		rowHeader = new SpreadsheetRowHeaderW(app, this);
		s = rowHeader.getContainer().getElement().getStyle();
		s.setPosition(Style.Position.RELATIVE);

		rowHeaderContainer = new FlowPanel();
		s = rowHeaderContainer.getElement().getStyle();
		s.setDisplay(Display.BLOCK);
		s.setMarginBottom(lowerLeftCornerHeight, Unit.PX);
		s.setOverflow(Style.Overflow.HIDDEN);
		s.setPosition(Style.Position.ABSOLUTE);
		// s.setTop(0, Unit.PX);
		s.setLeft(0, Unit.PX);

		rowHeaderContainer.add(rowHeader.getContainer());
		rowHeaderContainer.add(cornerContainerLowerLeft);

		// spreadsheet table
		ssGrid = new Grid(tableModel.getRowCount(), tableModel.getColumnCount());
		gridPanel = new AbsolutePanel();
		gridPanel.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		gridPanel.add(ssGrid);
		gridPanel.add(selectionFrame);
		gridPanel.add(dragFrame);
		gridPanel.add(blueDot);
		gridPanel.add(editorPanel);
		gridPanel.add(dummyTable);

		scroller = new TableScroller(this, rowHeader, columnHeader);

		ssGridContainer = new FlowPanel();
		s = ssGridContainer.getElement().getStyle();
		s.setVerticalAlign(Style.VerticalAlign.TOP);
		s.setDisplay(Display.INLINE_BLOCK);
		s.setMarginLeft(leftCornerWidth, Unit.PX);
		ssGridContainer.add(scroller);

		// create table header row
		headerRow = new FlowPanel();
		headerRow.getElement().getStyle()
		        .setWhiteSpace(Style.WhiteSpace.NOWRAP);
		headerRow.add(cornerContainerUpperLeft);
		headerRow.add(columnHeaderContainer);
		headerRow.add(cornerContainerUpperRight);

		// create table row
		FlowPanel tableRow = new FlowPanel();
		tableRow.getElement().getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
		tableRow.getElement().getStyle()
		        .setVerticalAlign(Style.VerticalAlign.TOP);
		tableRow.add(rowHeaderContainer);
		tableRow.add(ssGridContainer);

		// put rows together to complete the GUI
		tableWrapper = new FlowPanel();
		tableWrapper.add(headerRow);
		tableWrapper.add(tableRow);

	}

	public AbsolutePanel getGridPanel() {
		return gridPanel;
	}

	private void updateTableLayout() {

		int leftCornerWidth = SpreadsheetViewW.ROW_HEADER_WIDTH;

		if (showColumnHeader) {
			headerRow.getElement().getStyle().setDisplay(Style.Display.BLOCK);
		} else {
			headerRow.getElement().getStyle().setDisplay(Style.Display.NONE);
		}

		if (showRowHeader) {
			cornerContainerUpperLeft.getElement().getStyle()
			        .setDisplay(Style.Display.BLOCK);
			cornerContainerLowerLeft.getElement().getStyle()
			        .setDisplay(Style.Display.BLOCK);
			rowHeaderContainer.getElement().getStyle()
			        .setDisplay(Style.Display.BLOCK);
			ssGridContainer.getElement().getStyle()
			        .setMarginLeft(leftCornerWidth, Unit.PX);
			columnHeaderContainer.getElement().getStyle()
			        .setMarginLeft(leftCornerWidth, Unit.PX);

		} else {
			cornerContainerUpperLeft.getElement().getStyle()
			        .setDisplay(Style.Display.NONE);
			cornerContainerLowerLeft.getElement().getStyle()
			        .setDisplay(Style.Display.NONE);
			rowHeaderContainer.getElement().getStyle()
			        .setDisplay(Style.Display.NONE);
			ssGridContainer.getElement().getStyle().setMarginLeft(0, Unit.PX);
			columnHeaderContainer.getElement().getStyle()
			        .setMarginLeft(0, Unit.PX);
		}
	}

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

	public Grid getTableImpl() {
		return ssGrid;
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
	/*
	 * public MyCellEditorBooleanW getEditorBoolean() { if (editorBoolean ==
	 * null) editorBoolean = new MyCellEditorBooleanW(kernel); return
	 * editorBoolean; }
	 */

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

	public GeoClass getCellEditorType(int row, int column) {
		GPoint p = new GPoint(column, row);
		if (view.allowSpecialEditor() && oneClickEditMap.containsKey(p)
		        && kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {
			switch (oneClickEditMap.get(p).getGeoClassType()) {
			case BOOLEAN:
				return GeoClass.BOOLEAN;
			case BUTTON:
				return GeoClass.BUTTON;
			case LIST:
				return GeoClass.LIST;
			}
		}
		return GeoClass.DEFAULT;
	}

	public BaseCellEditor getCellEditor(int row, int column) {
		return editor;
	}

	public int getRowHeight(int row) {
		return ssGrid.getRowFormatter().getElement(row).getOffsetHeight();
	}

	public int getColumnWidth(int column) {
		// columnFormatter returns 0 (in Chrome at least)
		// so cellFormatter used instead
		return ssGrid.getCellFormatter().getElement(0, column).getOffsetWidth();
	}

	public int getLeadSelectionRow() {
		if (leadSelectionRow < 0) {
			return getSelectedRow();
		}
		return leadSelectionRow;
	}

	public int getLeadSelectionColumn() {
		if (leadSelectionColumn < 0) {
			return getSelectedColumn();
		}
		return leadSelectionColumn;
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
	public boolean isEqualsRequired() {
		return view.isEqualsRequired();
	}

	public void setLabels() {
		editor.setLabels();
	}

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

			renderCellsFirstTime = true;

			updateColumnCount();
			updateRowCount();

			// App.debug("ssGrid dim: " + ssGrid.getRowCount() + " x " +
			// ssGrid.getColumnCount());
		
			repaintAll();
		}

		public void valueChange() {
			updateCopiableSelection();
		}
	}

	public void updateCopiableSelection() {
		// TODO: can this be made more efficient?
		if (view != null && view.spreadsheetWrapper != null) {
			String cs = copyString();
			view.spreadsheetWrapper.setSelectedContent(cs);
			if (rowHeader != null && selectionType == MyTable.ROW_SELECT)
				rowHeader.focusPanel.setSelectedContent(cs);
		} else if (rowHeader != null && selectionType == MyTable.ROW_SELECT) {
			rowHeader.focusPanel.setSelectedContent(copyString());
		}
	}

	private void updateRowCount() {

		if (ssGrid.getRowCount() >= tableModel.getRowCount())
			return;

		int oldRowCount = ssGrid.getRowCount();
		ssGrid.resizeRows(tableModel.getRowCount());

		for (int row = oldRowCount; row < tableModel.getRowCount(); ++row) {
			setRowHeight(row, app.getSettings().getSpreadsheet()
			        .preferredRowHeight());
		}

		rowHeader.updateRowCount();
	}

	/**
	 * Appends columns to the table if table model column count is larger than
	 * current number of table columns.
	 */
	protected void updateColumnCount() {

		if (ssGrid.getColumnCount() >= tableModel.getColumnCount())
			return;

		int oldColumnCount = ssGrid.getColumnCount();
		ssGrid.resizeColumns(tableModel.getColumnCount());

		for (int col = oldColumnCount; col < tableModel.getColumnCount(); ++col) {
			ssGrid.getColumnFormatter().getElement(col).getStyle()
			        .setWidth(preferredColumnWidth, Style.Unit.PX);
		}

		columnHeader.updateColumnCount();

		// addColumn destroys custom row heights, so we must reset them
		// resetRowHeights();

	}

	// ===============================================================
	// Selection
	// ===============================================================
	/**
	 * @param point
	 *            y coordinate is row, x coordinate is column
	 */
	public void changeSelection(GPoint point, boolean extend) {
		changeSelection(point.getY(), point.getX(), extend);
	}

	public void changeSelection(int rowIndex, int columnIndex, boolean extend) {
		// force column selection
		if (view.isColumnSelect()) {
			setColumnSelectionInterval(columnIndex, columnIndex);
		}

		if (extend) {
			leadSelectionColumn = columnIndex;
			leadSelectionRow = rowIndex;
			if (anchorSelectionColumn == -1)
				anchorSelectionColumn = leadSelectionColumn;
			if (anchorSelectionRow == -1)
				anchorSelectionRow = leadSelectionRow;
		} else {
			anchorSelectionColumn = columnIndex;
			anchorSelectionRow = rowIndex;
			leadSelectionColumn = columnIndex;
			leadSelectionRow = rowIndex;
		}

		// let selectionChanged know about a change in single cell selection
		selectionChanged();

		if (autoScrolls) {
			GRectangle cellRect = getCellRect(rowIndex, columnIndex, false);
			if (cellRect != null) {
				scroller.scrollRectToVisible(cellRect);
			}
		}

	}

	public void selectAll() {

		setSelectionType(MyTable.CELL_SELECT);
		setAutoscrolls(false);

		// select the upper left corner cell
		changeSelection(0, 0, false);

		// extend the selection to the current lower right corner cell
		changeSelection(getRowCount() - 1, getColumnCount() - 1, true);

		setSelectAll(true);
		setAutoscrolls(true);
		scrollRectToVisible(getCellRect(0, 0, true));

		// setRowSelectionInterval(0, getRowCount()-1);
		// getColumnModel().getSelectionModel().setSelectionInterval(0,
		// getColumnCount()-1);
		// selectionChanged();
		// this.getSelectAll();

	}

	private void setAutoscrolls(boolean autoScrolls) {
		this.autoScrolls = autoScrolls;
	}

	protected void scrollRectToVisible(GRectangle contentRect) {
		scroller.scrollRectToVisible(contentRect);
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

		if (!GlobalKeyDispatcherW.getControlDown()) {
			selectedCellRanges.clear();
			selectedColumnSet.clear();
			selectedRowSet.clear();
			selectedCellRanges.add(0, newSelection);
		} else { // ctrl-select
			/*
			 * // return if we have already ctrl-selected this range for
			 * (CellRange cr : selectedCellRanges) { if
			 * (cr.equals(newSelection)){ App.debug("reutrned"); return; } }
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

		// newSelection.debug();
		// printSelectionParameters();

		if (isSelectNone && (minSelectionColumn != -1 || minSelectionRow != -1))
			setSelectNone(false);

		// TODO if (changedAnchor && !isEditing()) view.updateFormulaBar();

		// update the geo selection list
		ArrayList<GeoElement> list = new ArrayList<GeoElement>();
		for (int i = 0; i < selectedCellRanges.size(); i++) {
			list.addAll(0, (selectedCellRanges.get(i)).toGeoList());
		}

		// if the geo selection has changed, update selected geos
		boolean changed = !list.equals(app.getSelectionManager()
		        .getSelectedGeos());
		if (changed) {

			if (getTableMode() == MyTable.TABLE_MODE_AUTOFUNCTION) {
				this.updateAutoFunction();
			}

			app.getSelectionManager().setSelectedGeos(list, false);
			if (list.size() > 0) {
				app.updateSelection(true);
			} else {
				// don't update properties view for objects, but for spreadsheet
				app.updateSelection(false);
				app.setPropertiesViewPanel(OptionType.SPREADSHEET);
			}
		}
		
		if (view.isVisibleStyleBar()) {
			view.getSpreadsheetStyleBar().updateStyleBar();
		}

		// if the selection has changed or an empty cell has been clicked,
		// repaint
		if (changed || list.isEmpty()) {
			repaint();
			columnHeader.renderSelection();
			rowHeader.renderSelection();
			// ?//if (this.getTableHeader() != null)
			// ?// getTableHeader().repaint();
		}

		updateCopiableSelection();
		// App.debug("------------------");
		// for (CellRange cr: selectedCellRanges)cr.debug();
	}

	public void printSelectionParameters() {
		App.debug("----------------------------------");
		App.debug("minSelectionColumn = " + minSelectionColumn);
		App.debug("maxSelectionColumn = " + maxSelectionColumn);
		App.debug("minSelectionRow = " + minSelectionRow);
		App.debug("maxSelectionRow = " + maxSelectionRow);
		App.debug("----------------------------------");
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

		renderSelectionDeferred();
		columnHeader.renderSelection();
		rowHeader.renderSelection();

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

				setAutoscrolls(false);

				// row selection
				if (cr.isRow()) {
					setRowSelectionInterval(cr.getMinRow(), cr.getMaxRow());

					// column selection
				} else if (cr.isColumn()) {
					setColumnSelectionInterval(cr.getMinColumn(),
					        cr.getMaxColumn());

					// cell block selection
				} else {
					setSelectionType(MyTable.CELL_SELECT);
					changeSelection(cr.getMinRow(), cr.getMinColumn(), false);
					changeSelection(cr.getMaxRow(), cr.getMaxColumn(), true);
				}

				selectionChanged();

				// scroll to upper left corner of rectangle
				setAutoscrolls(true);
				scrollRectToVisible(getCellRect(cr.getMinRow(),
				        cr.getMinColumn(), true));
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
		anchorSelectionRow = row0;
		leadSelectionRow = row1;
		selectionChanged();
	}

	public void setColumnSelectionInterval(int col0, int col1) {
		setSelectionType(MyTable.COLUMN_SELECT);
		anchorSelectionColumn = col0;
		leadSelectionColumn = col1;
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
		return getPixel(column, row, min, true);
	}
	protected GPoint getPixel(int column, int row, boolean min, boolean scaleOffset) {

		if (column < 0 || row < 0) {
			return null;
		}

		if (min && column == 0 && row == 0) {
			// ? Why this returns 0, wt.getAbsoluteLeft would return a greater
			// number!
			// return new GPoint(0, 0);
		}

		Element wt = ssGrid.getCellFormatter().getElement(row, column);
		int offx = ssGrid.getAbsoluteLeft();
		int offy = ssGrid.getAbsoluteTop();
		int left, top;
		if(scaleOffset){
		    left = (int) ((wt.getAbsoluteLeft() - offx) / app
		        .getArticleElement().getScaleX()) + offx;
			top = (int) ((wt.getAbsoluteTop() - offy) / app.getArticleElement()
		        .getScaleY()) + offy;
		}else{
			left = (int) ((wt.getAbsoluteLeft()) / app
			        .getArticleElement().getScaleX());
			top = (int) ((wt.getAbsoluteTop() ) / app.getArticleElement()
			        .getScaleY()) ;	
		}
		// App.debug("-----------------------" + min);

		if (min) {
			// App.debug("col x row: " + column + " x " + row + "  pixels: " +
			// left + " x " + top);
			// getPixel2(column,row,min);
			return new GPoint(left, top);
		}
		// App.debug("col x row: " + column + " x " + row + "  pixels: " + (left
		// + wt.getOffsetWidth()) +
		// " x " + (top+wt.getOffsetHeight()));
		// getPixel2(column,row,min);
		return new GPoint(left + wt.getOffsetWidth(), top
		        + wt.getOffsetHeight());
	}

	protected GPoint getPixelRelative(int column, int row, boolean min) {

		if (column < 0 || row < 0) {
			return null;
		}

		if (column > getColumnCount() - 1) {
			column = getColumnCount() - 1;
		}

		if (row > getRowCount() - 1) {
			row = getRowCount() - 1;
		}

		GPoint p = new GPoint(0, 0);

		HashMap<Integer, Integer> widthMap = view.settings().getWidthMap();
		HashMap<Integer, Integer> heightMap = view.settings().getHeightMap();

		// adjust loop condition dependent on min
		int extraCell = min ? 0 : 1;

		for (int c = 0; c < column + extraCell; c++) {
			Integer w = (Integer) widthMap.get(c);
			if (w == null) {
				w = preferredColumnWidth;
			}
			p.x += w;
		}
		for (int r = 0; r < row + extraCell; r++) {
			Integer h = (Integer) heightMap.get(r);
			if (h == null) {
				h = view.settings().preferredRowHeight();
			}
			p.y += h;
		}

		// p.x += ssGrid.getAbsoluteLeft();
		// p.y += ssGrid.getAbsoluteTop();

		// App.debug("#2col x row: " + column + " x " + row + "  pixels: " + p.x
		// + " x " + p.y);
		return p;
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

		int columnFrom = 0;
		int rowFrom = 0;

		int indexX = -1;
		int indexY = -1;
		for (int i = columnFrom; i < getColumnCount(); ++i) {
			GPoint point = getPixel(i, rowFrom, false, false);
			if (x < point.getX()) {
				indexX = i;
				break;
			}
		}
		if (indexX == -1) {
			return null;
		}
		for (int i = rowFrom; i < getRowCount(); ++i) {
			GPoint point = getPixel(columnFrom, i, false, false);
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

	public GRectangle getCellRect(int row, int column, boolean spacing) {
		GPoint min = getPixel(column, row, true);
		if (min == null) {
			return null;
		}
		GPoint max = getPixel(column, row, false);
		if (max == null) {
			return null;
		}
		return new Rectangle(min.x, min.y, max.x - min.x, max.y - min.y);
	}

	public GRectangle getCellBlockRect(int column1, int row1, int column2,
	        int row2, boolean includeSpacing) {
		GRectangle r1 = getCellRect(row1, column1, includeSpacing);
		GRectangle r2 = getCellRect(row2, column2, includeSpacing);
		r1.setBounds((int) r1.getX(), (int) r1.getY(),
		        (int) ((r2.getX() - r1.getX()) + r2.getWidth()),
		        (int) ((r2.getY() - r1.getY()) + r2.getHeight()));
		return r1;
	}

	public GRectangle getSelectionRect(boolean includeSpacing) {
		return getCellBlockRect(minSelectionColumn, minSelectionRow,
		        maxSelectionColumn, maxSelectionRow, includeSpacing);
	}

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

	/**
	 * @param point
	 *            x column, y row
	 * @return true on success
	 */
	public boolean editCellAt(GPoint point) {
		return editCellAt(point.getY(), point.getX());
	}

	/**
	 * Starts in-cell editing for cells with short editing strings. For strings
	 * longer than MAX_CELL_EDIT_STRING_LENGTH, the redefine dialog is shown.
	 * Also prevents fixed cells from being edited.
	 */
	public boolean editCellAt(int row, int col) {

		if (row < 0 || col < 0)
			return false;

		Object ob = tableModel.getValueAt(row, col);

		// prepare editor to handle equals
		editor.setEqualsRequired(app.getSettings().getSpreadsheet()
		        .equalsRequired());
		if (ob instanceof GeoElement) {
			GeoElement geo = (GeoElement) ob;
			if (geo.isGeoButton() || geo.isGeoImage()) {
				app.getDialogManager().showPropertiesDialog();
				return true;
			}
			if (!view.getShowFormulaBar()) {
				if (!geo.isFixed()) {
					if (!geo.isGeoText()
					        && editor.getEditorInitString(geo).length() > MAX_CELL_EDIT_STRING_LENGTH) {
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
		if (isCellEditable(row, col) && !isEditing) {
			switch (getCellEditorType(row, col)) {
			case DEFAULT:
				isEditing = true;
				editRow = row;
				editColumn = col;

				AutoCompleteTextFieldW w = (AutoCompleteTextFieldW) ((MyCellEditorW) getCellEditor(
				        row, col)).getTableCellEditorWidget(this, ob, false,
				        row, col);

				// set height and position of the editor
				int editorHeight = ssGrid.getCellFormatter()
				        .getElement(row, col).getClientHeight();
				w.getTextField().getElement().getStyle()
				        .setHeight(editorHeight, Unit.PX);
				positionEditorPanel(true, row, col);

				// give it the focus
				w.requestFocus();
				renderSelection();

				return true;

			case BOOLEAN:
			case BUTTON:
			case LIST:
				// instead of editing the checkbox, do not go into editing mode
				// at all,
				// because we don't know when to stop editing

				isEditing = false;
				positionEditorPanel(false, 0, 0);

				renderSelection();
				return true;
			}
		}

		BaseCellEditor mce = getCellEditor(row, col);
		if (mce != null)
			mce.cancelCellEditing();
		return false;// TODO: implementation needed
	}

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
		if (view.allowSpecialEditor()
		        && oneClickEditMap.containsKey(new GPoint(column, row)))
			return true;

		// normal case: return false so we can handle double click in our //
		// mouseReleased
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

		// hide the editor
		positionEditorPanel(false, 0, 0);
		editRow = -1;
		editColumn = -1;

		view.requestFocus();

		// setRepaintAll();//TODO: don't call renderCells, just change the
		// edited cell
		repaint();
	}

	public void sendEditorKeyPressEvent(KeyPressEvent e) {
		editor.sendKeyPressEvent(e);
	}

	public void sendEditorKeyDownEvent(KeyDownEvent e) {
		editor.sendKeyDownEvent(e);
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

	// Keep column widths of table and column header in sync
	public void setColumnWidth(int column, int width) {
		int width2 = width;

		// TODO : check if this minimum width is valid,
		// if so create constant field

		// there is a minimal width in the Desktop version,
		// Web version should imitate this; this is visually looking
		// like 5px, but in the code, it seems that it is 15px
		if (width2 < 15) {
			width2 = 15;
		}

		if (column >= 0) {
			ssGrid.getColumnFormatter().getElement(column).getStyle()
			        .setWidth(width2, Style.Unit.PX);
			if (showColumnHeader) {
				columnHeader.setColumnWidth(column, width2);
			}
		}

	}

	public void setColumnWidth(final int width) {

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {

				minimumRowHeight = dummyTable.getCellFormatter()
				        .getElement(0, 0).getOffsetHeight();

				int width2 = Math.max(width, minimumRowHeight);

				for (int col = 0; col < getColumnCount(); col++) {
					ssGrid.getColumnFormatter().getElement(col).getStyle()
					        .setWidth(width2, Style.Unit.PX);
					if (showColumnHeader) {
						columnHeader.setColumnWidth(col, width2);
					}
				}

				if (view != null) {
					// view.updatePreferredRowHeight(rowHeight2);
				}

			}
		});
	}

	// Keep row heights of table and row header in sync
	public void setRowHeight(final int row, final int rowHeight) {

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {

				minimumRowHeight = dummyTable.getCellFormatter()
				        .getElement(0, 0).getOffsetHeight();

				int rowHeight2 = Math.max(rowHeight, minimumRowHeight);

				if (row >= 0) {
					ssGrid.getRowFormatter().getElement(row).getStyle()
					        .setHeight(rowHeight2, Style.Unit.PX);

					if (showRowHeader) {
						syncRowHeaderHeight(row);
					}
				}
				if (view != null) {
					if (doRecordRowHeights)
						adjustedRowHeights.add(new GPoint(row, rowHeight2));
				}
			}
		});
	}

	// Keep table and row header heights in sync
	public void syncRowHeaderHeight(final int row) {

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				int rowHeight = ssGrid.getRowFormatter().getElement(row)
				        .getOffsetHeight();
				rowHeader.setRowHeight(row, rowHeight);
			}
		});
	}

	public void setRowHeight(final int rowHeight) {

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {

				minimumRowHeight = dummyTable.getCellFormatter()
				        .getElement(0, 0).getOffsetHeight();

				int rowHeight2 = Math.max(rowHeight, minimumRowHeight);

				for (int row = 0; row < getRowCount(); row++) {
					ssGrid.getRowFormatter().getElement(row).getStyle()
					        .setHeight(rowHeight2, Style.Unit.PX);
					if (showRowHeader) {
						rowHeader.setRowHeight(row, rowHeight2);
					}
				}

				if (view != null) {
					// view.updatePreferredRowHeight(rowHeight2);
				}

			}
		});
	}

	// Reset the row heights --- used after addColumn destroys the row heights
	public void resetRowHeights() {

		doRecordRowHeights = false;
		for (GPoint p : adjustedRowHeights) {
			setRowHeight(p.x, p.y);
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

		// in table model coordinates

		Element prefElement = ssGrid.getCellFormatter().getElement(row, col);

		if (adjustWidth) {

			Element tableColumn = ssGrid.getColumnFormatter().getElement(col);

			int resultWidth = Math.max(tableColumn.getOffsetWidth(),
			        (int) prefElement.getOffsetWidth());
			tableColumn.getStyle().setWidth(resultWidth /*
														 * TODO this.
														 * getIntercellSpacing
														 * ().width
														 */
			, Style.Unit.PX);

			columnHeader.setColumnWidth(col, resultWidth);

		}

		if (adjustHeight) {

			int resultHeight = Math.max(ssGrid.getRowFormatter()
			        .getElement(row).getOffsetHeight(),
			        (int) prefElement.getOffsetHeight());
			int rowHeight2 = resultHeight;
			// if (rowHeight2 < minimumRowHeight)
			// rowHeight2 = minimumRowHeight;
			setRowHeight(row, rowHeight2);
		}

	}

	/**
	 * Adjust the width of a column to fit the maximum preferred width of its
	 * cell contents.
	 */
	public void fitColumn(int column) {

		// in grid coordinates

		Element tableColumn = ssGrid.getColumnFormatter().getElement(column);

		int prefWidth = 0;
		int tempWidth = -1;
		for (int row = 0; row < getRowCount(); row++) {
			if (column >= 0 && tableModel.getValueAt(row, column) != null) {
				tempWidth = ssGrid.getCellFormatter().getElement(row, column)
				        .getOffsetWidth();
				prefWidth = Math.max(prefWidth, tempWidth);
			}
		}

		// set the new column width
		if (tempWidth == -1) {
			// column is empty
			prefWidth = preferredColumnWidth /*
											 * TODO getIntercellSpacing().width
											 */;
		} else {
			// There was "15" here, but in Desktop, it should visually look
			// like if there was "5"...
			prefWidth = Math.max(prefWidth, 15 /*
												 * TODO
												 * tableColumn.getMinWidth()
												 */);
		}
		// note: the table might have its header set to null,
		// so we get the actual header from view
		// TODO//view.getTableHeader().setResizingColumn(tableColumn);
		tableColumn.getStyle().setWidth(prefWidth /*
												 * TODO getIntercellSpacing()
												 * .width
												 */
		, Style.Unit.PX);

		columnHeader.setColumnWidth(column, prefWidth);
	}

	/**
	 * Adjust the height of a row to fit the maximum preferred height of the its
	 * cell contents.
	 */
	public void fitRow(int row) {

		// in grid coordinates

		int prefHeight = ssGrid.getRowFormatter().getElement(row)
		        .getOffsetHeight();
		// int prefHeight = this.getRowHeight();
		int tempHeight = 0;
		for (int column = 0; column < this.getColumnCount(); column++) {
			tempHeight = ssGrid.getCellFormatter().getElement(row, column)
			        .getOffsetHeight();
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
			app.getSelectionManager().clearSelectedGeos();

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
		if (cr.isPartialRow()
		        || (!cr.isPartialColumn() && GlobalKeyDispatcherW
		                .getShiftDown())) {
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
			changeSelection(coords.y, coords.x, false);
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
	public String copyString() {
		return ((CopyPasteCutW)copyPasteCut).copyString(getSelectedColumn(), getSelectedRow(),
		        getMaxSelectedColumn(), getMaxSelectedRow());
	}

	public void copy(boolean altDown) {
		copyPasteCut.copy(getSelectedColumn(), getSelectedRow(),
		        getMaxSelectedColumn(), getMaxSelectedRow(), altDown);
	}

	public void copy(boolean altDown, boolean nat) {
		((CopyPasteCutW)copyPasteCut).copy(getSelectedColumn(), getSelectedRow(),
		        getMaxSelectedColumn(), getMaxSelectedRow(), altDown, nat);
	}

	public boolean paste() {
		return copyPasteCut.paste(getSelectedColumn(), getSelectedRow(),
		        getMaxSelectedColumn(), getMaxSelectedRow());
	}

	public boolean paste(String cont) {
		return ((CopyPasteCutW)copyPasteCut).paste(getSelectedColumn(), getSelectedRow(),
		        getMaxSelectedColumn(), getMaxSelectedRow(), cont);
	}

	public boolean cut() {
		return copyPasteCut.cut(getSelectedColumn(), getSelectedRow(),
		        getMaxSelectedColumn(), getMaxSelectedRow());
	}

	public boolean cut(boolean nat) {
		return ((CopyPasteCutW)copyPasteCut).cut(getSelectedColumn(), getSelectedRow(),
		        getMaxSelectedColumn(), getMaxSelectedRow(), nat);
	}

	public boolean delete() {
		return copyPasteCut.delete(getSelectedColumn(), getSelectedRow(),
		        getMaxSelectedColumn(), getMaxSelectedRow());
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

	/**
	 * Force repaint calls to update everything. (Useful for debugging.)
	 */
	public void setRepaintAll() {
		repaintAll = true;
	}

	/**
	 * Set to call extra things in renderCells() This hack is needed because
	 * something is not yet initialized when it is first used, so call it again
	 * after initialization happened.
	 */
	public void setRenderFirstTime() {
		renderCellsFirstTime = true;
	}

	
	/**
	 * Updates all cell formats and the current selection.
	 */
	public void repaintAll() {
		repaintAll = true;
		repaint();
	}

	/**
	 * Updates only the current selection. For efficiency the cell formats are
	 * not updated.
	 */
	public void repaint() {

		if (repaintAll) {
			updateAllCellFormats();
			repaintAll = false;
		}

		renderSelection();
	}

	public void updateCellFormat(int row, int column) {
		GeoElement geo = (GeoElement) tableModel.getValueAt(row, column);
		defaultTableCellRenderer.updateCellFormat(geo, row, column);
	}

	public void updateAllCellFormats() {
		for (int row = 0; row < getRowCount(); row++) {
			for (int column = 0; column < getColumnCount(); column++) {
				updateCellFormat(row, column);
			}
		}
	}

	public void updateCellFormat(ArrayList<CellRange> cellRangeList) {
		for (int i = 0; i < cellRangeList.size(); i++) {
			CellRange cr = cellRangeList.get(i);
			for (int row = cr.getMinRow(); row <= cr.getMaxRow(); row++) {
				for (int column = cr.getMinColumn(); column <= cr
				        .getMaxColumn(); column++) {
					updateCellFormat(row, column);
				}
			}
		}
	}

	public void renderCells() {
		Object gva;

		int colCount = getColumnCount();
		int rowCount = getRowCount();
		for (int i = colCount - 1; i >= 0; i--) {
			for (int j = rowCount - 1; j >= 0; j--) {

				if (renderCellsFirstTime) {
					// GeoElement or nothing
					gva = tableModel.getValueAt(j, i);

					// format table cells
					defaultTableCellRenderer.updateTableCellValue(ssGrid, gva,
					        j, i);
				}
				// otherwise updateTableCell will be called
				// at the time of value change anyway,
				// so it is not needed to call it here
			}
		}
	}

	public void updateTableCellValue(Object value, int row, int column) {
		if (defaultTableCellRenderer != null)
			defaultTableCellRenderer.updateTableCellValue(ssGrid, value, row,
			        column);
	}

	public boolean showCanDragBlueDot() {
		boolean showBlueDot = !isEditing();

		if (minSelectionRow != -1 && maxSelectionRow != -1
		        && minSelectionColumn != -1 && maxSelectionColumn != -1) {

			if (showBlueDot)
				for (int i = minSelectionRow; i <= maxSelectionRow; i++)
					for (int j = minSelectionColumn; j <= maxSelectionColumn; j++)
						if (tableModel.getValueAt(i, j) instanceof GeoElement)
							showBlueDot &= !((GeoElement) tableModel
							        .getValueAt(i, j)).isFixed();

			return showBlueDot;
		}
		return false;
	}

	public void renderSelectionDeferred() {

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				renderSelection();
			}
		});
	}

	private void renderSelection() {
		// TODO implement other features from the old paint method

		// draw dragging frame

		GPoint point1 = new GPoint(0, 0);
		GPoint point2 = new GPoint(0, 0);

		if (dragingToRow != -1 && dragingToColumn != -1) {

			// -|1|-
			// 2|-|3
			// -|4|-
			boolean visible = true;
			if (dragingToColumn < minSelectionColumn) { // 2
				point1 = getPixel(dragingToColumn, minSelectionRow, true);
				point2 = getPixel(minSelectionColumn - 1, maxSelectionRow,
				        false);

			} else if (dragingToRow > maxSelectionRow) { // 4
				point1 = getPixel(minSelectionColumn, maxSelectionRow + 1, true);
				point2 = getPixel(maxSelectionColumn, dragingToRow, false);

			} else if (dragingToRow < minSelectionRow) { // 1
				point1 = getPixel(minSelectionColumn, dragingToRow, true);
				point2 = getPixel(maxSelectionColumn, minSelectionRow - 1,
				        false);

			} else if (dragingToColumn > maxSelectionColumn) { // 3
				point1 = getPixel(maxSelectionColumn + 1, minSelectionRow, true);
				point2 = getPixel(dragingToColumn, maxSelectionRow, false);
			} else {
				visible = false;
			}

			updateDragFrame(visible, point1, point2);

		} else {

			updateDragFrame(false, point1, point2);
		}

		GPoint min = this.getMinSelectionPixel();
		GPoint max = this.getMaxSelectionPixel();

		if (minSelectionRow != -1 && maxSelectionRow != -1
		        && minSelectionColumn != -1 && maxSelectionColumn != -1) {
			updateSelectionFrame(true, showCanDragBlueDot(), min, max);
		} else {
			updateSelectionFrame(false, false, min, max);
		}

		// cells
		GeoElement geo = null;
		int maxColumn = tableModel.getHighestUsedColumn();
		int maxRow = tableModel.getHighestUsedRow();
		for (int col = maxColumn; col >= 0; col--) {
			for (int row = maxRow; row >= 0; row--) {
				geo = (GeoElement) tableModel.getValueAt(row, col);
				defaultTableCellRenderer.updateCellBackground(geo, row, col);
			}
		}

		// After rendering the LaTeX image for a geo, update the row height
		// with the preferred size set by the renderer.
		resizeMarkedCells();
	}

	public int getSelectedRow() {
		if (minSelectionRow < 0)
			return -1;
		return minSelectionRow;
	}

	public int getSelectedColumn() {
		if (minSelectionColumn < 0)
			return -1;
		return minSelectionColumn;
	}

	public int getMaxSelectedRow() {
		if (maxSelectionRow < 0)
			return -1;
		return maxSelectionRow;
	}

	public int getMaxSelectedColumn() {
		if (maxSelectionColumn < 0)
			return -1;
		return maxSelectionColumn;
	}

	public Widget getEditorWidget() {
		return editor.getTextfield();
	}

	/**
	 * Make the row header invisible to the user
	 * 
	 * @param showRow
	 *            true: show it; false: hide it
	 */
	public void setShowRowHeader(boolean showRow) {
		if (showRow) {
			if (!showRowHeader) {
				showRowHeader = true;

			}
		} else {
			showRowHeader = false;

		}
		updateTableLayout();
	}

	/**
	 * Make the column header invisible to the user
	 * 
	 * @param showCol
	 *            true: show it; false: hide it
	 */
	public void setShowColumnHeader(boolean showCol) {
		if (showCol) {
			if (!showColumnHeader) {
				showColumnHeader = true;

			}
		} else {
			showColumnHeader = false;

		}
		updateTableLayout();
	}

	public void updateCellFormat(String cellFormatString) {
		view.updateCellFormat(cellFormatString);
	}

	public boolean allowSpecialEditor() {
		return view.allowSpecialEditor();
	}

	public int getColumnCount() {
		return tableModel.getColumnCount();
	}

	public int getRowCount() {
		return tableModel.getRowCount();
	}

	public int getOffsetWidth() {
		return tableWrapper.getOffsetWidth();
	}

	public int getOffsetHeight() {
		return tableWrapper.getOffsetHeight();
	}

	public void setSize(int width, int height) {
		tableWrapper.setPixelSize(width, height);
		scroller.setPixelSize(width - rowHeader.getOffsetWidth(), height
		        - columnHeader.getOffsetHeight());
	}

	public void updateSelectionFrame(boolean visible, boolean showDragHandle,
	        GPoint corner1, GPoint corner2) {

		if (selectionFrame == null || corner1 == null || corner2 == null) {
			return;
		}

		int borderWidth = 2;

		// forcing a hide/show is needed to make Chrome redraw
		selectionFrame.setVisible(false);

		if (visible) {
			int x1 = Math.min(corner1.x, corner2.x);
			int x2 = Math.max(corner1.x, corner2.x);
			int y1 = Math.min(corner1.y, corner2.y);
			int y2 = Math.max(corner1.y, corner2.y);
			int h = y2 - y1 - 2 * borderWidth - 1;
			int w = x2 - x1 - 2 * borderWidth - 1;

			int ssTop = gridPanel.getAbsoluteTop();
			int ssLeft = gridPanel.getAbsoluteLeft();

			selectionFrame.setWidth(w + "px");
			selectionFrame.setHeight(h + "px");

			gridPanel
			        .setWidgetPosition(selectionFrame, x1 - ssLeft, y1 - ssTop);

			blueDot.setVisible(showDragHandle);
			if (showDragHandle) {
				gridPanel.setWidgetPosition(blueDot, x2 - ssLeft
				        - MyTableW.DOT_SIZE / 2 - 1, y2 - ssTop
				        - MyTableW.DOT_SIZE / 2 - 1);
			}

			selectionFrame.setVisible(true);
		}

	}

	public void updateDragFrame(boolean visible, GPoint corner1, GPoint corner2) {
		if (dragFrame == null) {
			return;
		}
		int borderWidth = 2;
		dragFrame.setVisible(visible);

		if (visible) {
			int x1 = Math.min(corner1.x, corner2.x);
			int x2 = Math.max(corner1.x, corner2.x);
			int y1 = Math.min(corner1.y, corner2.y);
			int y2 = Math.max(corner1.y, corner2.y);
			int h = y2 - y1 - 2 * borderWidth - 1;
			int w = x2 - x1 - 2 * borderWidth - 1;

			gridPanel.setWidgetPosition(dragFrame,
			        x1 - gridPanel.getAbsoluteLeft(),
			        y1 - gridPanel.getAbsoluteTop());

			dragFrame.setWidth(w + "px");
			dragFrame.setHeight(h + "px");
		}
	}

	public void positionEditorPanel(boolean visible, int row, int column) {
		if (editorPanel == null)
			return;
		editorPanel.setVisible(visible);
		if (visible) {
			GPoint p = getPixel(column, row, true);

			int ssTop = gridPanel.getAbsoluteTop();
			int ssLeft = gridPanel.getAbsoluteLeft();
			
			gridPanel.setWidgetPosition(editorPanel, p.x - ssLeft, p.y - ssTop);
			editorPanel.setVisible(true);
			
			int w = ssGrid.getCellFormatter().getElement(row, column)
			        .getClientWidth();
			int h = ssGrid.getCellFormatter().getElement(row, column)
			        .getClientHeight();
			
			editorPanel.getElement().getStyle().setWidth(w, Unit.PX);
			editorPanel.getElement().getStyle().setHeight(h, Unit.PX);
		}

	}

	public SimplePanel getEditorPanel() {
		return editorPanel;
	}

	public void setVerticalScrollPosition(int scrollPosition) {
		scroller.setVerticalScrollPosition(scrollPosition);
	}

	public void setHorizontalScrollPosition(int scrollPosition) {
		scroller.setHorizontalScrollPosition(scrollPosition);
	}

	public int getHorizontalScrollPosition() {
		return scroller.getHorizontalScrollPosition();
	}

	public int getVerticalScrollPosition() {
		return scroller.getVerticalScrollPosition();
	}

	public void updateFonts() {
		setRowHeight(0);
		resetRowHeights();
		renderSelection();
	}

	public void setShowVScrollBar(boolean showVScrollBar) {
		scroller.setShowVScrollBar(showVScrollBar);
	}

	public void setShowHScrollBar(boolean showHScrollBar) {
		scroller.setShowHScrollBar(showHScrollBar);
	}


	public void setEnableAutoComplete(boolean enableAutoComplete) {
		editor.setEnableAutoComplete(enableAutoComplete);
	}

	
	/**
	 * 
	 * @param toolTipText
	 *            the string to display in a tool tip; if the text is null, the
	 *            tool tip is turned off
	 */
	public void setToolTipText(String toolTipText) {

		ToolTipManagerW.sharedInstance().showToolTip(toolTipText);
	}
}
