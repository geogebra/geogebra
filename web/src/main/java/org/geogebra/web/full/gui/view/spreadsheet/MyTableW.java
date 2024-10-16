package org.geogebra.web.full.gui.view.spreadsheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.view.spreadsheet.CellRangeProcessor;
import org.geogebra.common.gui.view.spreadsheet.CellRangeUtil;
import org.geogebra.common.gui.view.spreadsheet.CopyPasteCut;
import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetModeProcessor;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetTableController;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.main.SpreadsheetTableModelSimple;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.spreadsheet.core.SelectionType;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.spreadsheet.style.CellFormatInterface;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;
import org.geogebra.web.html5.util.keyboard.KeyboardManagerInterface;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.BorderStyle;
import org.gwtproject.dom.style.shared.Display;
import org.gwtproject.dom.style.shared.Overflow;
import org.gwtproject.dom.style.shared.Position;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.dom.style.shared.VerticalAlign;
import org.gwtproject.dom.style.shared.Visibility;
import org.gwtproject.dom.style.shared.WhiteSpace;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.DoubleClickEvent;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.KeyPressEvent;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseMoveEvent;
import org.gwtproject.event.dom.client.MouseOutEvent;
import org.gwtproject.event.dom.client.MouseUpEvent;
import org.gwtproject.event.dom.client.TouchEndEvent;
import org.gwtproject.event.dom.client.TouchMoveEvent;
import org.gwtproject.event.dom.client.TouchStartEvent;
import org.gwtproject.user.client.ui.AbsolutePanel;
import org.gwtproject.user.client.ui.AbstractNativeScrollbar;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Grid;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * HTML5 implementation of spreadsheet table
 */
@SuppressWarnings("javadoc")
public class MyTableW implements /* FocusListener, */MyTable {

	private int tableMode = MyTable.TABLE_MODE_STANDARD;
	/** dot for rectangle selection */
	public static final int DOT_SIZE = 6;

	/** Selection rectangle color */
	public static final GColor SELECTED_RECTANGLE_COLOR = GColor.BLUE;

	// final static double dash1[] = { 2.0 };
	// final static GBasicStrokeW dashed = new GBasicStrokeW(3.0,
	// DefaultBasicStroke.CAP_BUTT, DefaultBasicStroke.JOIN_MITER, 10.0,
	// dash1);

	protected Kernel kernel;
	/** application */
	private AppW app;
	private MyCellEditorW editor;

	/** copy/paste utility */
	private final CopyPasteCut copyPasteCut;

	// protected SpreadsheetColumnControllerW.ColumnHeaderRenderer
	// columnHeaderRenderer;
	// protected SpreadsheetRowHeaderW.RowHeaderRenderer rowHeaderRenderer;
	/** view */
	protected SpreadsheetViewW view;
	/** table model */
	protected SpreadsheetTableModel tableModel;
	/** cell processor */
	private CellRangeProcessor crProcessor;
	// private MyTableColumnModelListener columnModelListener;
	/** Cell renderer */
	MyCellRendererW defaultTableCellRenderer;

	/**
	 * All currently selected cell ranges are held in this list. Cell ranges are
	 * added when selecting with ctrl-down. The first element is the most
	 * recently selected cell range.
	 */
	private final ArrayList<TabularRange> selectedRanges;

	// These keep track of internal selection using actual ranges and do not
	// use -1 flags for row and column.
	// Note: selectedRanges.get(0) gives the same selection but uses -1
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

	// Used for rendering headers with ctrl-select
	protected HashSet<Integer> selectedColumnSet = new HashSet<>();
	protected HashSet<Integer> selectedRowSet = new HashSet<>();

	private SelectionType selectionType;

	private GColor selectionRectangleColor = SELECTED_RECTANGLE_COLOR;

	// Dragging vars
	protected int draggingToRow = -1;
	protected int draggingToColumn = -1;
	protected boolean isDragging = false;

	protected boolean showRowHeader = true;
	protected boolean showColumnHeader = true;

	protected boolean editing = false;

	boolean repaintAll = false; // sometimes only the repainting of
								// borders/background is needed

	// Cells to be resized on next repaint are put in these HashSets.
	// A cell is added to a set when editing is done. The cells are removed
	// after a repaint in MyTable.
	private final HashSet<GPoint> cellResizeHeightSet;
	private final HashSet<GPoint> cellResizeWidthSet;

	private final ArrayList<GPoint> adjustedRowHeights = new ArrayList<>();
	private boolean doRecordRowHeights = true;

	private int preferredColumnWidth = SpreadsheetSettings.TABLE_CELL_WIDTH;

	// there should be place left for the textfield
	protected int minimumRowHeight = SpreadsheetSettings.TABLE_CELL_HEIGHT + 4;

	private HashMap<SpreadsheetCoords, GeoElement> oneClickEditMap = new HashMap<>();
	private boolean allowEditing = false;

	private SpreadsheetModeProcessor spredsheetModeProcessor;

	protected Grid ssGrid;

	protected TableScroller scroller;

	private FlowPanel tableWrapper;
	protected SpreadsheetRowHeaderW rowHeader;
	protected SpreadsheetColumnHeaderW columnHeader;

	private FlowPanel rowHeaderContainer;

	// special panels for editing and selection
	private SimplePanel selectionFrame;
	private SimplePanel dragFrame;
	private SimplePanel blueDot;
	private SimplePanel editorPanel;

	private AbsolutePanel gridPanel;

	private Grid upperLeftCorner;

	private FlowPanel headerRow;

	private FlowPanel ssGridContainer;

	private FlowPanel columnHeaderContainer;

	private FlowPanel cornerContainerUpperLeft;

	private FlowPanel cornerContainerLowerLeft;

	private FlowPanel cornerContainerUpperRight;

	protected Grid dummyTable;

	private boolean autoScrolls = true;

	private boolean isSelectAll = false;

	private SpreadsheetTableController controller;

	/*******************************************************************
	 * Construct table
	 */
	public MyTableW(SpreadsheetViewW view, SpreadsheetTableModel tableModel) {

		app = view.getApplication();
		kernel = app.getKernel();
		this.tableModel = tableModel;
		this.view = view;

		createFloatingElements();
		createGUI();

		cellResizeHeightSet = new HashSet<>();
		cellResizeWidthSet = new HashSet<>();

		for (int i = 0; i < getColumnCount(); ++i) {
			// TODO//getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
			ssGrid.getColumnFormatter().getElement(i).getStyle()
			        .setWidth(preferredColumnWidth, Unit.PX);
		}

		// add cell renderer & editors
		defaultTableCellRenderer = new MyCellRendererW(app, view, this,
		        (CellFormat) getCellFormatHandler());

		// this needs defaultTableCellRenderer now
		((SpreadsheetTableModelSimple) tableModel).attachMyTable(this);

		// :NEXT:Grid.setCellFormatter
		editor = new MyCellEditorW(kernel, editorPanel,
				getEditorController());
		Dom.addEventListener(tableWrapper.getElement(), "focusout", evt -> onFocusOut());
		// setDefaultEditor(Object.class, editor);

		// initialize selection fields
		selectedRanges = new ArrayList<>();

		selectionType = SelectionType.CELLS;

		// add table model listener
		((SpreadsheetTableModelSimple) tableModel)
		        .setChangeListener(new MyTableModelListener());

		copyPasteCut = new CopyPasteCutW(app);

		ssGrid.setCellPadding(0);
		ssGrid.setCellSpacing(0);
		ssGrid.getElement().addClassName("geogebraweb-table-spreadsheet");

		registerListeners();
		repaintAll();
	}

	private void onFocusOut() {
		editor.stopCellEditingAndProcess();
		finishEditing(true);
	}

	@Override
	public ArrayList<TabularRange> getSelectedRanges() {
		return selectedRanges;
	}

	/**
	 * @return grid
	 */
	public Grid getGrid() {
		return ssGrid;
	}

	/**
	 * @return wrapping widget
	 */
	public Widget getContainer() {
		return tableWrapper;
	}

	/**
	 * @return editor
	 */
	public MyCellEditorW getEditor() {
		return editor;
	}

	/**
	 * @return whether editor is active
	 */
	public boolean isEditing() {
		return editing;
	}

	/**
	 * @return Collection of cells that contain geos that can be edited with one
	 *         click, e.g. booleans, buttons, lists
	 */
	public HashMap<SpreadsheetCoords, GeoElement> getOneClickEditMap() {
		return oneClickEditMap;
	}

	/**
	 * @param oneClickEditMap
	 *            fast editable geos, see {@link #getOneClickEditMap()}
	 */
	public void setOneClickEditMap(HashMap<SpreadsheetCoords, GeoElement> oneClickEditMap) {
		this.oneClickEditMap = oneClickEditMap;
	}

	private void registerListeners() {
		SpreadsheetMouseListenerW ml = new SpreadsheetMouseListenerW(app, this);
		gridPanel.addDomHandler(ml, MouseDownEvent.getType());
		gridPanel.addDomHandler(ml, MouseUpEvent.getType());
		gridPanel.addDomHandler(ml, MouseMoveEvent.getType());
		gridPanel.addDomHandler(ml, DoubleClickEvent.getType());
		gridPanel.addBitlessDomHandler(ml, TouchStartEvent.getType());
		gridPanel.addBitlessDomHandler(ml, TouchMoveEvent.getType());
		gridPanel.addBitlessDomHandler(ml, TouchEndEvent.getType());
		gridPanel.addDomHandler(ml, MouseOutEvent.getType());

		upperLeftCorner.addBitlessDomHandler(event -> selectAll(), ClickEvent.getType());

		ClickStartHandler.init(gridPanel, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				CancelEventTimer.keyboardSetVisible();
			}
		});

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
		        .setBorderStyle(BorderStyle.SOLID);
		dragFrame.getElement().getStyle().setBorderWidth(2, Unit.PX);
		dragFrame.getElement().getStyle()
		        .setBorderColor(GColor.GRAY.toString());
		dragFrame.setVisible(false);

		blueDot = new SimplePanel();
		blueDot.setVisible(false);
		blueDot.setPixelSize(MyTableW.DOT_SIZE, MyTableW.DOT_SIZE);
		blueDot.setStyleName("spreadsheetDot cursor_default");

		dummyTable = new Grid(1, 1);
		dummyTable.getElement().getStyle()
		        .setVisibility(Visibility.HIDDEN);
		dummyTable.setText(0, 0, "x");
		dummyTable.getElement().getStyle().setPosition(Position.ABSOLUTE);
		dummyTable.getElement().getStyle().setTop(0, Unit.PX);
		dummyTable.getElement().getStyle().setLeft(0, Unit.PX);
		dummyTable.getElement().addClassName("geogebraweb-table-spreadsheet");
	}

	private void createGUI() {
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
		int leftCornerWidth = SpreadsheetViewW.ROW_HEADER_WIDTH;
		s.setWidth(leftCornerWidth, Unit.PX);
		//s.setBackgroundColor(BACKGROUND_COLOR_HEADER.toString());
		//s.setColor(BACKGROUND_COLOR_HEADER.toString());
		s.setPosition(Position.ABSOLUTE);
		s.setTop(0, Unit.PX);
		s.setLeft(0, Unit.PX);

		// ----- upper right corner
		Grid upperRightCorner = new Grid(1, 1);
		upperRightCorner.setText(0, 0, "xxx");
		upperRightCorner.getElement().addClassName(
		        "geogebraweb-table-spreadsheet");
		upperRightCorner.addStyleName("upperCorner");
		upperRightCorner.getCellFormatter().getElement(0, 0)
		        .addClassName("SVheader");
		s = upperRightCorner.getElement().getStyle();
		int rightCornerWidth = AbstractNativeScrollbar
				.getNativeScrollbarWidth();
		s.setWidth(rightCornerWidth, Unit.PX);
		//s.setBackgroundColor(BACKGROUND_COLOR_HEADER.toString());
		//s.setColor(BACKGROUND_COLOR_HEADER.toString());
		s.setPosition(Position.ABSOLUTE);
		s.setTop(0, Unit.PX);
		s.setRight(0, Unit.PX);

		// ----- lower left corner
		Grid lowerLeftCorner = new Grid(1, 1);
		upperRightCorner.setText(0, 0, "9999");
		lowerLeftCorner.getElement().addClassName(
		        "geogebraweb-table-spreadsheet-lowerLeftCorner");
		s = lowerLeftCorner.getElement().getStyle();
		s.setWidth(leftCornerWidth - 1, Unit.PX);
		int lowerLeftCornerHeight = AbstractNativeScrollbar
				.getNativeScrollbarHeight();
		s.setHeight(lowerLeftCornerHeight - 2, Unit.PX);
		//s.setBackgroundColor(BACKGROUND_COLOR_HEADER.toString());

		s.setPosition(Position.ABSOLUTE);
		s.setLeft(0, Unit.PX);
		s.setBottom(0, Unit.PX);

		// ---- corner containers
		cornerContainerUpperLeft = new FlowPanel();
		cornerContainerUpperLeft.getElement().getStyle()
		        .setDisplay(Display.BLOCK);
		cornerContainerUpperLeft.add(upperLeftCorner);
		cornerContainerLowerLeft = new FlowPanel();
		cornerContainerLowerLeft.getElement().getStyle()
		        .setDisplay(Display.BLOCK);
		cornerContainerLowerLeft.add(lowerLeftCorner);
		cornerContainerUpperRight = new FlowPanel();
		cornerContainerUpperRight.getElement().getStyle()
		        .setDisplay(Display.BLOCK);
		cornerContainerUpperRight.add(upperRightCorner);

		// ---- column header
		columnHeader = new SpreadsheetColumnHeaderW(app, this);
		s = columnHeader.getContainer().getElement().getStyle();
		s.setPosition(Position.RELATIVE);

		columnHeaderContainer = new FlowPanel();
		s = columnHeaderContainer.getElement().getStyle();
		s.setDisplay(Display.BLOCK);
		s.setOverflow(Overflow.HIDDEN);
		s.setMarginLeft(leftCornerWidth, Unit.PX);
		s.setMarginRight(rightCornerWidth, Unit.PX);
		columnHeaderContainer.add(columnHeader.getContainer());

		// ------ row header
		rowHeader = new SpreadsheetRowHeaderW(app, this);
		s = rowHeader.getContainer().getElement().getStyle();
		s.setPosition(Position.RELATIVE);

		rowHeaderContainer = new FlowPanel();
		s = rowHeaderContainer.getElement().getStyle();
		s.setDisplay(Display.BLOCK);
		s.setMarginBottom(lowerLeftCornerHeight, Unit.PX);
		s.setOverflow(Overflow.HIDDEN);
		s.setPosition(Position.ABSOLUTE);
		// s.setTop(0, Unit.PX);
		s.setLeft(0, Unit.PX);

		rowHeaderContainer.add(rowHeader.getContainer());
		rowHeaderContainer.add(cornerContainerLowerLeft);

		// spreadsheet table
		ssGrid = new Grid(tableModel.getRowCount(), tableModel.getColumnCount());
		gridPanel = new AbsolutePanel();
		gridPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		gridPanel.add(ssGrid);
		gridPanel.add(selectionFrame);
		gridPanel.add(dragFrame);
		gridPanel.add(blueDot);
		gridPanel.add(editorPanel);
		gridPanel.add(dummyTable);

		scroller = new TableScroller(this, rowHeader, columnHeader);

		ssGridContainer = new FlowPanel();
		s = ssGridContainer.getElement().getStyle();
		s.setVerticalAlign(VerticalAlign.TOP);
		s.setDisplay(Display.INLINE_BLOCK);
		s.setMarginLeft(leftCornerWidth, Unit.PX);
		ssGridContainer.add(scroller);

		// create table header row
		headerRow = new FlowPanel();
		headerRow.getElement().getStyle()
		        .setWhiteSpace(WhiteSpace.NOWRAP);
		headerRow.add(cornerContainerUpperLeft);
		headerRow.add(columnHeaderContainer);
		headerRow.add(cornerContainerUpperRight);

		// create table row
		FlowPanel tableRow = new FlowPanel();
		tableRow.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
		tableRow.getElement().getStyle()
		        .setVerticalAlign(VerticalAlign.TOP);
		tableRow.add(rowHeaderContainer);
		tableRow.add(ssGridContainer);

		// put rows together to complete the GUI
		tableWrapper = new FlowPanel();
		tableWrapper.add(headerRow);
		tableWrapper.add(tableRow);

	}

	/**
	 * @return wrapping panel
	 */
	public AbsolutePanel getGridPanel() {
		return gridPanel;
	}

	private void updateTableLayout() {

		int leftCornerWidth = SpreadsheetViewW.ROW_HEADER_WIDTH;

		if (showColumnHeader) {
			headerRow.getElement().getStyle().setDisplay(Display.BLOCK);
		} else {
			headerRow.getElement().getStyle().setDisplay(Display.NONE);
		}

		if (showRowHeader) {
			cornerContainerUpperLeft.getElement().getStyle()
			        .setDisplay(Display.BLOCK);
			cornerContainerLowerLeft.getElement().getStyle()
			        .setDisplay(Display.BLOCK);
			rowHeaderContainer.getElement().getStyle()
			        .setDisplay(Display.BLOCK);
			ssGridContainer.getElement().getStyle()
			        .setMarginLeft(leftCornerWidth, Unit.PX);
			columnHeaderContainer.getElement().getStyle()
			        .setMarginLeft(leftCornerWidth, Unit.PX);

		} else {
			cornerContainerUpperLeft.getElement().getStyle()
			        .setDisplay(Display.NONE);
			cornerContainerLowerLeft.getElement().getStyle()
			        .setDisplay(Display.NONE);
			rowHeaderContainer.getElement().getStyle()
			        .setDisplay(Display.NONE);
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
	@Override
	public SpreadsheetViewInterface getView() {
		return view;
	}

	/**
	 * Simple getter method
	 * 
	 * @return App
	 */
	@Override
	public App getApplication() {
		return app;
	}

	/**
	 * Simple getter method
	 * 
	 * @return Kernel
	 */
	@Override
	public Kernel getKernel() {
		return kernel;
	}

	/**
	 * @return table model
	 */
	public SpreadsheetTableModel getModel() {
		return tableModel;
	}

	@Override
	public CopyPasteCut getCopyPasteCut() {
		return copyPasteCut;
	}

	/**
	 * Returns CellRangeProcessor for this table. If none exists, a new one is
	 * created.
	 */
	@Override
	public CellRangeProcessor getCellRangeProcessor() {
		if (crProcessor == null) {
			crProcessor = new CellRangeProcessor(this, app);
		}
		return crProcessor;
	}

	/**
	 * Returns CellFormat helper class for this table. If none exists, a new one
	 * is created.
	 */
	@Override
	public CellFormatInterface getCellFormatHandler() {
		return app.getSpreadsheetTableModel().getCellFormat(this);
	}

	/**
	 * Get element type of given cell
	 * 
	 * @param row
	 *            row
	 * @param column
	 *            column
	 * @return element type
	 */
	public GeoClass getCellEditorType(int row, int column) {
		SpreadsheetCoords p = new SpreadsheetCoords(row, column);
		if (view.allowSpecialEditor() && oneClickEditMap.containsKey(p)
				&& kernel.getAlgebraStyleSpreadsheet() == Kernel.ALGEBRA_STYLE_VALUE) {
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

	public BaseCellEditor getCellEditor() {
		return editor;
	}

	public int getRowHeight(int row) {
		return ssGrid.getRowFormatter().getElement(row).getOffsetHeight();
	}

	/**
	 * @param column
	 *            column index
	 * @return width in pixels
	 */
	public int getColumnWidth(int column) {
		// columnFormatter returns 0 (in Chrome at least)
		// so cellFormatter used instead
		return ssGrid.getCellFormatter().getElement(0, column).getOffsetWidth();
	}

	/**
	 * @return Row where selection ended (start = anchor row)
	 */
	public int getLeadSelectionRow() {
		if (leadSelectionRow < 0) {
			return getSelectedRow();
		}
		return leadSelectionRow;
	}

	/**
	 * @return Column where selection ended (start = anchor row)
	 */
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
	 * 
	 * @return whether = is required to interpret content as command
	 */
	public boolean isEqualsRequired() {
		return view.isEqualsRequired();
	}

	public void setLabels() {
		// not needed
	}

	public int preferredColumnWidth() {
		return preferredColumnWidth;
	}

	public void setPreferredColumnWidth(int preferredColumnWidth) {
		this.preferredColumnWidth = preferredColumnWidth;
	}

	public class MyTableModelListener implements
	        SpreadsheetTableModelSimple.ChangeListener {

		@Override
		public void dimensionChange() {
			// TODO: comment them out to imitate the Desktop behaviour
			// TODO//getView().updateRowHeader();

			updateColumnCount();
			updateRowCount();
			repaintAll();
		}

		@Override
		public void valueChange() {
			if (isEditing()) {
				updateCopiableSelection();
			}
		}
	}

	/**
	 * Update the content of the copiable textarea.
	 */
	public void updateCopiableSelection() {
		// TODO: can this be made more efficient?
		if (view != null && view.spreadsheetWrapper != null) {
			String cs = copyString();
			view.spreadsheetWrapper.setSelectedContent(cs);
			if (rowHeader != null
					&& selectionType == SelectionType.ROWS) {
				rowHeader.focusPanel.setSelectedContent(cs);
			}
		} else if (rowHeader != null
				&& selectionType == SelectionType.ROWS) {
			rowHeader.focusPanel.setSelectedContent(copyString());
		}
	}

	/**
	 * Update row heights and count in the header.
	 */
	void updateRowCount() {
		if (ssGrid.getRowCount() >= tableModel.getRowCount()) {
			return;
		}

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

		if (ssGrid.getColumnCount() >= tableModel.getColumnCount()) {
			return;
		}

		int oldColumnCount = ssGrid.getColumnCount();
		ssGrid.resizeColumns(tableModel.getColumnCount());

		for (int col = oldColumnCount; col < tableModel.getColumnCount(); ++col) {
			ssGrid.getColumnFormatter().getElement(col).getStyle()
			        .setWidth(preferredColumnWidth, Unit.PX);
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
	public void changeSelection(SpreadsheetCoords point, boolean extend) {
		changeSelection(point.row, point.column, extend);
	}

	@Override
	public void changeSelection(int rowIndex, int columnIndex, boolean extend) {
		// force column selection
		if (view.isColumnSelect()) {
			setColumnSelectionInterval(columnIndex, columnIndex);
		}

		if (extend) {
			leadSelectionColumn = columnIndex;
			leadSelectionRow = rowIndex;
			if (anchorSelectionColumn == -1) {
				anchorSelectionColumn = leadSelectionColumn;
			}
			if (anchorSelectionRow == -1) {
				anchorSelectionRow = leadSelectionRow;
			}
		} else {
			anchorSelectionColumn = columnIndex;
			anchorSelectionRow = rowIndex;
			leadSelectionColumn = columnIndex;
			leadSelectionRow = rowIndex;
		}

		// let selectionChanged know about a change in single cell selection
		selectionChanged();

		if (autoScrolls) {
			GRectangle cellRect = getCellRect(rowIndex, columnIndex, true);
			if (cellRect != null) {
				scroller.scrollRectToVisible(columnIndex, rowIndex);
			}
		}
	}

	/**
	 * Select all cells
	 */
	public void selectAll() {

		setSelectionType(SelectionType.CELLS);
		setAutoscrolls(false);

		// select the upper left corner cell
		changeSelection(0, 0, false);

		// extend the selection to the current lower right corner cell
		changeSelection(getRowCount() - 1, getColumnCount() - 1, true);

		setSelectAll(true);
		setAutoscrolls(true);
		scrollRectToVisible(0, 0);

		// setRowSelectionInterval(0, getRowCount()-1);
		// getColumnModel().getSelectionModel().setSelectionInterval(0,
		// getColumnCount()-1);
		// selectionChanged();
		// this.getSelectAll();

	}

	private void setAutoscrolls(boolean autoScrolls) {
		this.autoScrolls = autoScrolls;
	}

	protected void scrollRectToVisible(int x, int y) {
		scroller.scrollRectToVisible(x, y);
	}

	/**
	 * This handles all selection changes for the table.
	 */
	@Override
	public void selectionChanged() {

		// create a cell range object to store
		// the current table selection

		TabularRange newSelection;

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
		default:
		case CELLS:
			newSelection = new TabularRange(anchorSelectionRow, anchorSelectionColumn,
					leadSelectionRow, leadSelectionColumn);
			break;

		case ROWS:
			newSelection = new TabularRange(anchorSelectionRow, -1, leadSelectionRow, -1
			);
			break;

		case COLUMNS:
			newSelection = new TabularRange(-1, anchorSelectionColumn,
					-1, leadSelectionColumn);
			break;
		case ALL:
			newSelection = new TabularRange(-1, -1,
					-1, -1);
		}

		// update sets of selected rows/columns (used for rendering in the
		// headers)
		if (selectionType == SelectionType.COLUMNS) {
			for (int i = newSelection.getMinColumn(); i <= newSelection
			        .getMaxColumn(); i++) {
				selectedColumnSet.add(i);
			}
		}

		if (selectionType == SelectionType.ROWS) {
			for (int i = newSelection.getMinRow(); i <= newSelection
			        .getMaxRow(); i++) {
				selectedRowSet.add(i);
			}
		}

		// update the selection list and internal variables
		newSelection = CellRangeUtil.getActual(newSelection, app);

		if (!GlobalKeyDispatcherW.getControlDown()) {
			selectedRanges.clear();
			selectedColumnSet.clear();
			selectedRowSet.clear();
			selectedRanges.add(0, newSelection);
		} else { // ctrl-select
			// handle dragging
			if (!selectedRanges.isEmpty() && selectedRanges.get(0).hasSameAnchor(newSelection)) {
				selectedRanges.remove(0);
			}
			// add the selection to the list
			selectedRanges.add(0, newSelection);
		}

		minSelectionRow = newSelection.getMinRow();
		minSelectionColumn = newSelection.getMinColumn();
		maxSelectionColumn = newSelection.getMaxColumn();
		maxSelectionRow = newSelection.getMaxRow();

		// update the geo selection list
		ArrayList<GeoElement> list = new ArrayList<>();
		for (TabularRange selectedRange : selectedRanges) {
			list.addAll(0, CellRangeUtil.toGeoList(selectedRange, app));
		}

		// if the geo selection has changed, update selected geos
		boolean changed = !list.equals(app.getSelectionManager()
		        .getSelectedGeos());
		if (changed) {

			if (getTableMode() == MyTable.TABLE_MODE_AUTOFUNCTION) {
				getSpreadsheetModeProcessor().updateAutoFunction();
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
	}

	/**
	 * Sets the initial selection parameters to a single cell. Does this without
	 * calling changeSelection, so it should only be used at startup.
	 */
	public void setInitialCellSelection(int row0, int column0) {

		setSelectionType(SelectionType.CELLS);
		int row = row0;
		int column = column0;
		if (column == -1) {
			column = 0;
		}
		if (row == -1) {
			row = 0;
		}
		minSelectionColumn = column;
		maxSelectionColumn = column;
		minSelectionRow = row;
		maxSelectionRow = row;

		renderSelectionDeferred();
		columnHeader.renderSelection();
		rowHeader.renderSelection();
	}

	@Override
	public boolean setSelection(int c, int r) {
		TabularRange tr = new TabularRange(r, c, r, c);
		return setSelection(tr);
	}

	/**
	 * 
	 * @param c1
	 *            min column
	 * @param r1
	 *            min row
	 * @param c2
	 *            max column
	 * @param r2
	 *            max row
	 */
	public void setSelection(int c1, int r1, int c2, int r2) {
		TabularRange tr = new TabularRange(r1, c1, r2, c2);
		if (tr.isValid()) {
			setSelection(tr);
		}
	}

	@Override
	public boolean setSelection(TabularRange tr) {
		if (tr != null && !tr.isValid()) {
			return false;
		}

		try {
			if (tr == null || tr.isEmptyRange()) {
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
				if (tr.isRow()) {
					setRowSelectionInterval(tr.getMinRow(), tr.getMaxRow());

					// column selection
				} else if (tr.isColumn()) {
					setColumnSelectionInterval(tr.getMinColumn(),
					        tr.getMaxColumn());

					// cell block selection
				} else {
					setSelectionType(SelectionType.CELLS);
					changeSelection(tr.getMinRow(), tr.getMinColumn(), false);
					changeSelection(tr.getMaxRow(), tr.getMaxColumn(), true);
				}

				selectionChanged();

				// scroll to upper left corner of rectangle
				setAutoscrolls(true);
				scrollRectToVisible(tr.getMinColumn(), tr.getMinRow());
				repaint();
			}
		} catch (Exception e) {
			Log.debug(e);
			return false;
		}

		return true;
	}

	/**
	 * Switch between column / row / range selection.
	 * 
	 * @param selType
	 *            MyTableInterface.*_SELECT
	 */
	public void setSelectionType(SelectionType selType) {

		if (view.isColumnSelect()) {
			this.selectionType = SelectionType.COLUMNS;
		} else {

			// in web, selectionType should do what setSelectionMode do too
			this.selectionType = selType;
		}

	}

	@Override
	public SelectionType getSelectionType() {
		return selectionType;
	}

	/**
	 * @param row0
	 *            anchor row
	 * @param row1
	 *            lead row
	 */
	public void setRowSelectionInterval(int row0, int row1) {
		setSelectionType(SelectionType.ROWS);
		anchorSelectionRow = row0;
		leadSelectionRow = row1;
		selectionChanged();
	}

	/**
	 * @param col0
	 *            anchor column
	 * @param col1
	 *            lead column
	 */
	public void setColumnSelectionInterval(int col0, int col1) {
		setSelectionType(SelectionType.COLUMNS);
		anchorSelectionColumn = col0;
		leadSelectionColumn = col1;
		selectionChanged();
	}

	@Override
	public boolean isSelectAll() {
		return isSelectAll;
	}

	/**
	 * @param isSelectAll
	 *            whether all cells are selected
	 */
	public void setSelectAll(boolean isSelectAll) {
		this.isSelectAll = isSelectAll;
	}

	/**
	 * @return selected column indices
	 */
	public ArrayList<Integer> getSelectedColumnsList() {

		ArrayList<Integer> columns = new ArrayList<>();

		for (TabularRange cr : this.selectedRanges) {
			for (int c = cr.getMinColumn(); c <= cr.getMaxColumn(); ++c) {
				if (!columns.contains(c)) {
					columns.add(c);
				}
			}
		}
		return columns;
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

		if (column < 0 || row < 0 || column >= this.getColumnCount()
				|| row >= this.getRowCount()) {
			return null;
		}

		Element wt = ssGrid.getCellFormatter().getElement(row, column);
		int offx = ssGrid.getAbsoluteLeft();
		int offy = ssGrid.getAbsoluteTop();
		int left, top;
		if (scaleOffset) {
			left = (int) ((wt.getAbsoluteLeft() - offx)
					/ getScale()) + offx;
			top = (int) ((wt.getAbsoluteTop() - offy)
					/ getScale()) + offy;
		} else {
			left = (int) (wt.getAbsoluteLeft()
					/ getScale());
			top = (int) (wt.getAbsoluteTop()
					/ getScale());
		}

		if (min) {
			return new GPoint(left, top);
		}
		return new GPoint(left + wt.getOffsetWidth(), top
		        + wt.getOffsetHeight());
	}

	private double getScale() {
		// based on GPopupPanel.getScale. Assumes that x and y scale are the same
		return Browser.isSafariByVendor() ? 1 : app.getGeoGebraElement().getScaleX();
	}

	protected GPoint getPixelRelative(int column, int row) {
		Element wt = ssGrid.getCellFormatter().getElement(
				(int) MyMath.clamp(row, 0, getRowCount() - 1),
				(int) MyMath.clamp(column, 0, getColumnCount() - 1));
		int offx = column == getColumnCount() ? wt.getOffsetWidth() : 0;
		int offy = row == getRowCount() ? wt.getOffsetHeight() : 0;
		return new GPoint(wt.getOffsetLeft() + offx, wt.getOffsetTop() + offy);
	}

	protected GPoint getMinSelectionPixel() {
		return getPixel(minSelectionColumn, minSelectionRow, true);
	}

	protected GPoint getMaxSelectionPixel(boolean scaleOffset) {
		return getPixel(maxSelectionColumn, maxSelectionRow, false,
				scaleOffset);
	}

	/**
	 * Returns Point(columnIndex, rowIndex), cell indices for the given pixel
	 * location
	 *
	 * @return spreadsheet coordinates from pixel
	 */
	public SpreadsheetCoords getIndexFromPixel(int x, int y) {
		return getIndexFromPixel(x, y, 0);
	}

	/**
	 * Returns Point(columnIndex, rowIndex), cell indices for the given pixel
	 * location
	 * 
	 * @return spreadsheet coordinates from pixel
	 */
	public SpreadsheetCoords getIndexFromPixel(int x, int y, int diff) {
		if (x < 0 || y < 0) {
			return null;
		}

		int columnFrom = 0;
		int rowFrom = 0;

		int column = -1;
		int row = -1;
		for (int i = columnFrom; i < getColumnCount(); ++i) {
			GPoint point = getPixel(i, rowFrom, false, false);
			if (x + diff < point.getX()) {
				column = i;
				break;
			}
		}
		if (column == -1) {
			return null;
		}
		for (int i = rowFrom; i < getRowCount(); ++i) {
			GPoint point = getPixel(columnFrom, i, false, false);
			if (y + diff < point.getY()) {
				row = i;
				break;
			}
		}
		if (row == -1) {
			return null;
		}
		return new SpreadsheetCoords(row, column);
	}

	/**
	 * @param x x-offset with respect to the whole grid
	 * @return cell x-coordinate or -1 if not found
	 */
	public int getIndexFromPixelRelativeX(int x) {
		if (x < 0) {
			return -1;
		}

		int rowFrom = 0;

		int indexX = -1;
		for (int i = 0; i < getColumnCount(); ++i) {
			Element point = ssGrid.getCellFormatter().getElement(rowFrom, i);
			if (x <= point.getOffsetLeft()) {
				indexX = i;
				break;
			}
		}
		return indexX;
	}

	/**
	 * @param y y-offset with respect to the whole grid
	 * @return cell y-coordinates or -1 if not found
	 */
	public int getIndexFromPixelRelativeY(int y) {
		if (y < 0) {
			return -1;
		}

		int columnFrom = 0;

		int indexY = -1;

		for (int i = 0; i < getRowCount(); ++i) {
			Element point = ssGrid.getCellFormatter().getElement(i, columnFrom);
			if (y <= point.getOffsetTop()) {
				indexY = i;
				break;
			}
		}
		return indexY;
	}

	/**
	 * @return rectangle (with screen coordinates)
	 */
	public GRectangle getCellRect(int row, int column,
			boolean offset) {
		GPoint min = getPixel(column, row, true, offset);
		if (min == null) {
			return null;
		}
		GPoint max = getPixel(column, row, false, offset);
		if (max == null) {
			return null;
		}
		return new Rectangle(min.x, min.y, max.x - min.x, max.y - min.y);
	}

	/**
	 * @param point
	 *            x column, y row
	 * @return true on success
	 */
	public boolean editCellAt(SpreadsheetCoords point) {
		return editCellAt(point.row, point.column);
	}

	/**
	 * Starts in-cell editing for cells with short editing strings. For strings
	 * longer than MAX_CELL_EDIT_STRING_LENGTH, the redefine dialog is shown.
	 * Also prevents fixed cells from being edited.
	 */
	@Override
	public boolean editCellAt(final int row, final int col) {

		if (row < 0 || col < 0) {
			return false;
		}

		Object ob = tableModel.getValueAt(row, col);

		// prepare editor to handle equals
		editor.setEqualsRequired(app.getSettings().getSpreadsheet()
		        .equalsRequired());
		if (ob instanceof GeoElement) {
			GeoElement geo = (GeoElement) ob;
			if (geo.isGeoButton() || geo.isGeoImage()) {
				ArrayList<GeoElement> sel = new ArrayList<>();
				sel.add(geo);
				app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS,
						sel);
				return true;
			}
			if (!view.getShowFormulaBar()) {
				if (getEditorController().redefineIfNeeded(geo)) {
					return true;
				}
			}
		}
		// STANDARD case: in cell editing
		if (isCellEditable(row, col) && !editing) {
			switch (getCellEditorType(row, col)) {
			case DEFAULT:
				editing = true;

				AutoCompleteTextFieldW textField = ((MyCellEditorW) getCellEditor())
						.getTableCellEditorWidget(this, ob, false,
				        row, col);
				// w.getElement().setAttribute("display", "none");

				if (view.isKeyboardEnabled()) {
					KeyboardManagerInterface keyboardManager = app.getKeyboardManager();
					if (keyboardManager != null) {
						app.showKeyboard(textField,
								!keyboardManager.isKeyboardClosedByUser());
					} else {
						app.showKeyboard(textField, true);
					}
					Scheduler.get().scheduleDeferred(() -> scrollRectToVisible(col, row));

					if (NavigatorUtil.isMobile()) {
						textField.prepareShowSymbolButton(false);
						textField.enableGGBKeyboard();
						textField.addDummyCursor();
						textField.setCursorPos(textField.getText().length());
					}
				} else if (!app.isWhiteboardActive()) {
					// if keyboard isn't enabled, inserts openkeyboard button
					// if there is no in the SV yet
					app.showKeyboard(textField, false);
				}

				// set position of the editor
				positionEditorPanel(true, row, col);

				// give it the focus
				textField.requestFocus();
				renderSelection();

				return true;

			case BOOLEAN:
			case BUTTON:
			case LIST:
				// instead of editing the checkbox, do not go into editing mode
				// at all,
				// because we don't know when to stop editing

				editing = false;
				positionEditorPanel(false, 0, 0);

				renderSelection();
				return true;
			}
		}

		BaseCellEditor mce = getCellEditor();
		if (mce != null) {
			mce.cancelCellEditing();
		}
		return false;
	}

	private SpreadsheetTableController getEditorController() {
		if (controller == null) {
			controller = new SpreadsheetTableController(app);
		}
		return controller;
	}

	public void setAllowEditing(boolean allowEditing) {
		this.allowEditing = allowEditing;
	}

	/**
	 * we need to return false for this normally, otherwise we can't detect
	 * double-clicks
	 * 
	 * @param row
	 *            row index
	 * @param column
	 *            column index
	 * @return whether given cell is editable
	 */
	public boolean isCellEditable(int row, int column) {
		if (view.isColumnSelect()) {
			return false;
		}

		// allow use of special editors for e.g. buttons, lists
		if (view.allowSpecialEditor()
		        && oneClickEditMap.containsKey(new SpreadsheetCoords(row, column))) {
			return true;
		}

		// normal case: return false so we can handle double click in our //
		// mouseReleased
		if (!allowEditing) {
			return false;
		}

		// prevent editing fixed geos when allowEditing == true
		GeoElement geo = (GeoElement) getModel().getValueAt(row, column);
		return geo == null || !geo.isProtected(EventType.UPDATE);
	}

	/**
	 * Finish editing current cell.
	 * 
	 * @param editNext
	 *            whether to go to next cell after
	 */
	public void finishEditing(boolean editNext) {
		editing = false;

		// hide the editor
		positionEditorPanel(false, 0, 0);
		if (!editNext) {
			view.requestFocus();
		}

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

	/**
	 * Keep column widths of table and column header in sync.
	 * 
	 * @param column
	 *            column index
	 * @param width
	 *            column width
	 */
	public void setColumnWidth(int column, int width) {
		setColumnWidthSilent(column, width);
		this.view.settings().addWidthNoFire(column, width);

	}

	void setColumnWidthSilent(int column, int width) {
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
			        .setWidth(width2, Unit.PX);
			if (showColumnHeader) {
				columnHeader.setColumnWidth(column, width2);
			}
		}
	}

	/**
	 * Change width of all columns.
	 * 
	 * @param width
	 *            width in pixels
	 */
	public void setColumnWidth(final int width) {

		Scheduler.get().scheduleDeferred(() -> {

			minimumRowHeight = dummyTable.getCellFormatter()
					.getElement(0, 0).getOffsetHeight();

			int width2 = Math.max(width, minimumRowHeight);

			for (int col = 0; col < getColumnCount(); col++) {
				ssGrid.getColumnFormatter().getElement(col).getStyle()
						.setWidth(width2, Unit.PX);
				if (showColumnHeader) {
					columnHeader.setColumnWidth(col, width2);
				}
			}
		});
	}

	// Keep row heights of table and row header in sync
	public void setRowHeight(final int row, final int rowHeight) {
		setRowHeight(row, rowHeight, true);
	}

	protected void setRowHeight(final int row, final int rowHeight,
			final boolean updateSettings) {
		Scheduler.get().scheduleDeferred(() -> {
			minimumRowHeight = dummyTable.getCellFormatter()
					.getElement(0, 0).getOffsetHeight();
			int rowHeight2 = Math.max(rowHeight, minimumRowHeight);
			setRowHeightCallback(row, rowHeight);
			if (updateSettings) {
				view.settings().addHeightNoFire(row, rowHeight2);
			}
		});
	}

	protected void setRowHeightCallback(int row, int rowHeight2) {
		if (row >= 0) {
			ssGrid.getRowFormatter().getElement(row).getStyle()
					.setHeight(rowHeight2, Unit.PX);

			if (showRowHeader) {
				syncRowHeaderHeight(row);
			}
		}
		if (view != null) {
			if (doRecordRowHeights) {
				adjustedRowHeights.add(new GPoint(row, rowHeight2));
			}
		}
	}

	/**
	 * Keep table and row header heights in sync
	 * 
	 * @param row
	 *            row to sync
	 */
	public void syncRowHeaderHeight(final int row) {

		Scheduler.get().scheduleDeferred(() -> {
			int rowHeight = ssGrid.getRowFormatter().getElement(row)
					.getOffsetHeight();
			rowHeader.setRowHeight(row, rowHeight);
		});
	}
	
	/* 
	 * Fits the content of spreadsheet for its header on the left.
	 */
	public void syncTableTop() {
		scroller.syncTableTop();
	}

	/**
	 * Change row height of all rows
	 * 
	 * @param rowHeight
	 *            row height in pixels
	 * @param updateSettings
	 *            whether to change settings too
	 */
	public void setRowHeight(final int rowHeight,
			final boolean updateSettings) {

		Scheduler.get().scheduleDeferred(() -> {

			minimumRowHeight = dummyTable.getCellFormatter()
					.getElement(0, 0).getOffsetHeight();

			int rowHeight2 = Math.max(rowHeight, minimumRowHeight);

			for (int row = 0; row < getRowCount(); row++) {
				ssGrid.getRowFormatter().getElement(row).getStyle()
						.setHeight(rowHeight2, Unit.PX);
				if (showRowHeader) {
					rowHeader.setRowHeight(row, rowHeight2);
				}
			}

			if (view != null && updateSettings) {
				view.updatePreferredRowHeight(rowHeight2);
			}

		});
	}

	/**
	 * Reset the row heights --- used after addColumn destroys the row heights.
	 */
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
	 * Note: this is just temporary (e.g. dynamic LaTeX), do NOT update settings
	 */
	private void setPreferredCellSize(int row, int col, boolean adjustWidth,
	        boolean adjustHeight) {

		// in table model coordinates

		Element prefElement = ssGrid.getCellFormatter().getElement(row, col);

		if (adjustWidth) {

			Element tableColumn = ssGrid.getColumnFormatter().getElement(col);

			int resultWidth = Math.max(tableColumn.getOffsetWidth(),
			        prefElement.getOffsetWidth());
			// TODO this. getIntercellSpacing ().width
			tableColumn.getStyle().setWidth(resultWidth, Unit.PX);

			columnHeader.setColumnWidth(col, resultWidth);
		}

		if (adjustHeight) {
			int resultHeight = Math.max(ssGrid.getRowFormatter()
			        .getElement(row).getOffsetHeight(),
			        prefElement.getOffsetHeight());
			setRowHeight(row, resultHeight, false);
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
		// TODO getIntercellSpacing().width
		tableColumn.getStyle().setWidth(prefWidth, Unit.PX);

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

	// ==================================================
	// Table mode change
	// ==================================================

	@Override
	public int getTableMode() {
		return tableMode;
	}

	/**
	 * Sets the table mode
	 * 
	 * @param tableMode
	 *            mode from the MyTable.TABLE_ values
	 */
	@Override
	public void setTableMode(int tableMode) {
		if (tableMode == MyTable.TABLE_MODE_AUTOFUNCTION) {
			if (!initAutoFunction()) {
				return;
			}
		}

		else if (tableMode == MyTable.TABLE_MODE_DROP) {
			// nothing to do (yet)
		}

		else {
			// color is standard
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
		if (selectedRanges.size() == 1
		        && selectedRanges.get(0).isSingleCell()) {

			// Clear the target cell, exit if this is not possible
			if (RelativeCopy.getValue(app, minSelectionColumn, minSelectionRow) != null) {
				boolean isOK = copyPasteCut.delete(minSelectionColumn,
				        minSelectionRow, minSelectionColumn, minSelectionRow);
				if (!isOK) {
					return false;
				}
			}

			// Set targetCell as a GeoNumeric that can be used to preview the
			// autofunction result
			// (later it will be set as a GeoList)
			getSpreadsheetModeProcessor().initTargetCell(minSelectionColumn,
					minSelectionRow);

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
		else if (selectedRanges.size() == 1) {

			try {
				getSpreadsheetModeProcessor()
						.performAutoFunctionCreation(selectedRanges.get(0),
								GlobalKeyDispatcherW.getShiftDown());
			} catch (Exception e) {
				Log.debug(e);
			}

			// Don't stay in this mode, we're done
			return false;
		} else {
			return false;
		}

		return true;
	}

	// ===========================================
	// copy/paste/cut/delete methods
	//
	// this is temporary code while cleaning up
	// ===========================================
	/**
	 * Copy and return string from selected cells
	 * 
	 * @return the content of selected cells
	 */
	public String copyString() {
		return copyPasteCut.copyString(getSelectedColumn(),
				getSelectedRow(), getMaxSelectedColumn(), getMaxSelectedRow());
	}

	/**
	 * Copy selected cells to (virtual) clipboard.
	 * 
	 * @param altDown
	 *            whether alt is pressed
	 */
	public void copy(boolean altDown) {
		copyPasteCut.copy(getSelectedColumn(), getSelectedRow(),
				getMaxSelectedColumn(), getMaxSelectedRow(), altDown);
	}

	/**
	 * Copy string from selected cells
	 * 
	 * @param altDown
	 *            whether alt is pressed
	 * @param nat
	 *            whether this is native event
	 */
	public void copy(boolean altDown, boolean nat) {
		((CopyPasteCutW) copyPasteCut).copy(getSelectedColumn(),
				getSelectedRow(), getMaxSelectedColumn(), getMaxSelectedRow(),
				altDown, nat);
	}

	/**
	 * Paste string to selected cells
	 * 
	 * @param content
	 *            cell content
	 * @return success
	 */
	public boolean paste(String content) {
		return ((CopyPasteCutW) copyPasteCut).paste(getSelectedColumn(),
				getSelectedRow(), getMaxSelectedColumn(), getMaxSelectedRow(),
				content);
	}

	/**
	 * Cut string from selected cells
	 * 
	 * @return if at least one object was deleted
	 */
	public boolean cut() {
		return copyPasteCut.cut(getSelectedColumn(), getSelectedRow(),
				getMaxSelectedColumn(), getMaxSelectedRow());
	}

	/**
	 * Cut string from selected cells
	 * 
	 * @param nat
	 *            whether this is native event
	 * @return success
	 */
	public boolean cut(boolean nat) {
		return ((CopyPasteCutW) copyPasteCut).cut(getSelectedColumn(),
				getSelectedRow(),
		        getMaxSelectedColumn(), getMaxSelectedRow(), nat);
	}

	/**
	 * Delete content of selected cells.
	 * 
	 * @return success
	 */
	public boolean delete() {
		return copyPasteCut.delete(getSelectedColumn(), getSelectedRow(),
		        getMaxSelectedColumn(), getMaxSelectedRow());
	}

	/**
	 * Updates all cell formats and the current selection.
	 */
	@Override
	public void repaintAll() {
		repaintAll = true;
		repaint();
	}

	/**
	 * Updates only the current selection. For efficiency the cell formats are
	 * not updated.
	 */
	@Override
	public void repaint() {
		if (repaintAll) {
			updateAllCellFormats();
			repaintAll = false;
		}

		renderSelection();
	}

	/**
	 * @param row
	 *            row
	 * @param column
	 *            column
	 */
	public void updateCellFormat(int row, int column) {
		GeoElement geo = (GeoElement) tableModel.getValueAt(row, column);
		defaultTableCellRenderer.updateCellFormat(geo, row, column);
	}

	/**
	 * Update format of all cells.
	 */
	public void updateAllCellFormats() {
		for (int row = 0; row < getRowCount(); row++) {
			for (int column = 0; column < getColumnCount(); column++) {
				defaultTableCellRenderer.clearBorder(row, column);
				updateCellFormat(row, column);
			}
		}
		for (int row = 0; row < getRowCount(); row++) {
			for (int column = 0; column < getColumnCount(); column++) {
				defaultTableCellRenderer.updateCellBorder(row, column);
			}
		}
	}

	/**
	 * Update format of given cells.
	 * 
	 * @param cellRangeList
	 *            cells to update
	 */
	public void updateCellFormat(ArrayList<TabularRange> cellRangeList) {
		for (int i = 0; i < cellRangeList.size(); i++) {
			TabularRange tr = cellRangeList.get(i);
			for (int row = tr.getMinRow(); row <= tr.getMaxRow(); row++) {
				for (int column = tr.getMinColumn(); column <= tr
				        .getMaxColumn(); column++) {
					updateCellFormat(row, column);
				}
			}
		}
	}

	@Override
	public void updateTableCellValue(Object value, int row, int column) {
		if (defaultTableCellRenderer != null) {
			defaultTableCellRenderer.updateTableCellValue(ssGrid, value, row,
			        column);
		}
	}

	/**
	 * Check we can show blue dot: not editing, selection has no fixed cells.
	 * 
	 * @return whether to show blue dot
	 */
	public boolean showCanDragBlueDot() {
		boolean showBlueDot = !isEditing();

		if (minSelectionRow != -1 && maxSelectionRow != -1
		        && minSelectionColumn != -1 && maxSelectionColumn != -1) {

			if (showBlueDot) {
				for (int i = minSelectionRow; i <= maxSelectionRow; i++) {
					for (int j = minSelectionColumn; j <= maxSelectionColumn; j++) {
						if (tableModel.getValueAt(i, j) instanceof GeoElement) {
							showBlueDot &= !((GeoElement) tableModel
									.getValueAt(i, j))
											.isProtected(EventType.UPDATE);
						}
					}
				}
			}

			return showBlueDot;
		}
		return false;
	}

	/**
	 * Render selection with a delay.
	 */
	public void renderSelectionDeferred() {

		Scheduler.get().scheduleDeferred(this::renderSelection);
	}

	void renderSelection() {
		// TODO implement other features from the old paint method

		// draw dragging frame

		GPoint point1 = new GPoint(0, 0);
		GPoint point2 = new GPoint(0, 0);

		if (draggingToRow != -1 && draggingToColumn != -1) {

			// -|1|-
			// 2|-|3
			// -|4|-
			boolean visible = true;
			if (draggingToColumn < minSelectionColumn) { // 2
				point1 = getPixel(draggingToColumn, minSelectionRow, true);
				point2 = getPixel(minSelectionColumn - 1, maxSelectionRow,
				        false);

			} else if (draggingToRow > maxSelectionRow) { // 4
				point1 = getPixel(minSelectionColumn, maxSelectionRow + 1, true);
				point2 = getPixel(maxSelectionColumn, draggingToRow, false);

			} else if (draggingToRow < minSelectionRow) { // 1
				point1 = getPixel(minSelectionColumn, draggingToRow, true);
				point2 = getPixel(maxSelectionColumn, minSelectionRow - 1,
				        false);

			} else if (draggingToColumn > maxSelectionColumn) { // 3
				point1 = getPixel(maxSelectionColumn + 1, minSelectionRow, true);
				point2 = getPixel(draggingToColumn, maxSelectionRow, false);
			} else {
				visible = false;
			}

			updateDragFrame(visible, point1, point2);

		} else {

			updateDragFrame(false, point1, point2);
		}

		GPoint min = this.getMinSelectionPixel();
		GPoint max = this.getMaxSelectionPixel(true);

		if (minSelectionRow != -1 && maxSelectionRow != -1
		        && minSelectionColumn != -1 && maxSelectionColumn != -1) {
			updateSelectionFrame(true, showCanDragBlueDot(), min, max);
		} else {
			updateSelectionFrame(false, false, min, max);
		}

		// cells
		GeoElement geo;
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

	/**
	 * @return min selected row
	 */
	public int getSelectedRow() {
		if (minSelectionRow < 0) {
			return -1;
		}
		return minSelectionRow;
	}

	/**
	 * @return min selected ccolumn
	 */
	public int getSelectedColumn() {
		if (minSelectionColumn < 0) {
			return -1;
		}
		return minSelectionColumn;
	}

	/**
	 * @return max selected row
	 */
	public int getMaxSelectedRow() {
		if (maxSelectionRow < 0) {
			return -1;
		}
		return maxSelectionRow;
	}

	/**
	 * @return max selected column
	 */
	public int getMaxSelectedColumn() {
		if (maxSelectionColumn < 0) {
			return -1;
		}
		return maxSelectionColumn;
	}

	/**
	 * Change row and column header visibility
	 * 
	 * @param showRow whether to show row header
	 * @param showCol whether to show column header
	 */
	public void setShowHeader(boolean showRow, boolean showCol) {
		showRowHeader = showRow;
		showColumnHeader = showCol;
		updateTableLayout();
	}

	@Override
	public void updateCellFormat(String cellFormatString) {
		view.updateCellFormat(cellFormatString);
	}

	@Override
	public boolean allowSpecialEditor() {
		return view.allowSpecialEditor();
	}

	@Override
	public int getColumnCount() {
		return tableModel.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return tableModel.getRowCount();
	}

	public int getOffsetWidth() {
		return tableWrapper.getOffsetWidth();
	}

	public int getOffsetHeight() {
		return tableWrapper.getOffsetHeight();
	}

	/**
	 * @param width
	 *            table width
	 * @param height
	 *            table height
	 */
	public void setSize(int width, int height) {
		tableWrapper.setPixelSize(width, height);
		scroller.setPixelSize(width - rowHeader.getOffsetWidth(), height
		        - columnHeader.getOffsetHeight());
	}

	/**
	 * 
	 * @param visible
	 *            whether selection is visible
	 * @param showDragHandle
	 *            whether to show blue dot
	 * @param corner1
	 *            top left corner
	 * @param corner2
	 *            bottom right corner
	 */
	public void updateSelectionFrame(boolean visible, boolean showDragHandle,
	        GPoint corner1, GPoint corner2) {
		if (selectionFrame == null) {
			return;
		}
		selectionFrame.setVisible(false);
		if (!visible) {
			blueDot.setVisible(false);
		} else if (corner1 != null && corner2 != null) {
			showSelectionFrame(corner1, corner2, showDragHandle);
		}
	}

	private void showSelectionFrame(GPoint corner1, GPoint corner2, boolean showDragHandle) {
		int borderWidth = 2;
		int x1 = Math.min(corner1.x, corner2.x);
		int x2 = Math.max(corner1.x, corner2.x);
		int y1 = Math.min(corner1.y, corner2.y);
		int y2 = Math.max(corner1.y, corner2.y);
		int h = y2 - y1 - 2 * borderWidth;
		int w = x2 - x1 - 2 * borderWidth;
		if (w >= 0 && h >= 0) {
			int ssTop = gridPanel.getAbsoluteTop();
			int ssLeft = gridPanel.getAbsoluteLeft();

			selectionFrame.setWidth(w + "px");
			selectionFrame.setHeight(h + "px");

			gridPanel.setWidgetPosition(selectionFrame, x1 - ssLeft,
					y1 - ssTop);

			blueDot.setVisible(showDragHandle);
			if (showDragHandle) {
				gridPanel.setWidgetPosition(blueDot,
						x2 - ssLeft - MyTableW.DOT_SIZE / 2 - 1,
						y2 - ssTop - MyTableW.DOT_SIZE / 2 - 1);
			}
			selectionFrame.setVisible(true);
		}
	}

	private void updateDragFrame(boolean visible, GPoint corner1,
			GPoint corner2) {
		if (dragFrame == null) {
			return;
		}
		int borderWidth = 1;
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

	private void positionEditorPanel(boolean visible, int row, int column) {
		if (editorPanel == null) {
			return;
		}
		editorPanel.setVisible(visible);
		if (visible) {
			GPoint p = getPixel(column, row, true);
			GPoint m = getPixel(column, row, false, true);

			int ssTop = gridPanel.getAbsoluteTop();
			int ssLeft = gridPanel.getAbsoluteLeft();
			
			gridPanel.setWidgetPosition(editorPanel, p.x - ssLeft, p.y - ssTop);
			editorPanel.setVisible(true);
			int w = m.x - p.x;
			int h = m.y - p.y;
			editorPanel.getElement().getStyle().setWidth(w, Unit.PX);
			editorPanel.getElement().getStyle().setHeight(h, Unit.PX);
		}
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

	/**
	 * Update font sizes.
	 */
	public void updateFonts() {
		setRowHeight(0, false);
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
	 * Update spreadsheet when zoom level changes
	 * 
	 * @param ratio
	 *            CSS pixel ratio
	 */
	public void setPixelRatio(double ratio) {
		if (editor != null) {
			editor.stopCellEditing();
		}
		view.setRowHeightsFromSettings();
		for (int row = 0; row < this.getRowCount(); row++) {
			for (int column = 0; column < this.getColumnCount(); column++) {
				if (ssGrid.getWidget(row, column) instanceof Canvas) {
					this.updateTableCellValue(app.getSpreadsheetTableModel()
							.getValueAt(row, column), row, column);
				}
			}
		}
	}

	/**
	 * Allow automatic edits.
	 */
	public void setAllowAutoEdit() {
		if (editor != null) {
			editor.setAllowAutoEdit();
		}
	}

	/**
	 * 
	 * @return spreadsheet mode processor
	 */
	public SpreadsheetModeProcessor getSpreadsheetModeProcessor() {
		if (this.spredsheetModeProcessor == null) {
			this.spredsheetModeProcessor = new SpreadsheetModeProcessor(app,
					this);
		}
		return this.spredsheetModeProcessor;
	}

	public void addResizeHeight(GPoint gPoint) {
		cellResizeHeightSet.add(gPoint);
	}
}
