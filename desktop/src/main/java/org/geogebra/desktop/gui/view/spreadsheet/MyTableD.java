package org.geogebra.desktop.gui.view.spreadsheet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.view.spreadsheet.CellRangeProcessor;
import org.geogebra.common.gui.view.spreadsheet.CellRangeUtil;
import org.geogebra.common.gui.view.spreadsheet.CopyPasteCut;
import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetModeProcessor;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetTableController;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.spreadsheet.core.SelectionType;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.spreadsheet.style.CellFormatInterface;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.virtualkeyboard.VirtualKeyboardD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

public class MyTableD extends JTable implements FocusListener, MyTable {
	private static final long serialVersionUID = 1L;

	private int tableMode = MyTable.TABLE_MODE_STANDARD;

	public static final int DOT_SIZE = 7;
	public static final int LINE_THICKNESS1 = 3;
	public static final int LINE_THICKNESS2 = 2;
	public static final Color SELECTED_BACKGROUND_COLOR = GColorD.getAwtColor(
			GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR);
	public static final Color SELECTED_BACKGROUND_COLOR_HEADER = GColorD
			.getAwtColor(
					GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR_HEADER);
	public static final Color BACKGROUND_COLOR_HEADER = GColorD
			.getAwtColor(GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER);
	public static final Color TABLE_GRID_COLOR = GColorD
			.getAwtColor(GeoGebraColorConstants.GRAY2);
	public static final Color HEADER_GRID_COLOR = GColorD
			.getAwtColor(GeoGebraColorConstants.GRAY4);
	public static final Color SELECTED_RECTANGLE_COLOR = Color.BLUE;

	protected Kernel kernel;
	protected AppD app;
	protected MyCellEditorSpreadsheet editor;
	private MyCellEditorBoolean editorBoolean;
	private MyCellEditorButton editorButton;
	private MyCellEditorList editorList;

	protected RelativeCopy relativeCopy;
	protected CopyPasteCutD copyPasteCut;
	protected SpreadsheetColumnControllerD.ColumnHeaderRenderer headerRenderer;
	protected SpreadsheetViewD view;
	protected DefaultTableModel tableModel;
	private CellRangeProcessor crProcessor;
	private MyTableColumnModelListener columnModelListener;
	private boolean isSelectAll = false;
	private boolean isSelectNone = false;
	private Rectangle targetcellFrame;
	final static float[] dash1 = { 2.0f };
	final static BasicStroke dashed = new BasicStroke(3.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

	private boolean allowEditing = false;

	private SpreadsheetModeProcessor spreadsheetModeProcessor;

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
	protected int minSelectionRow = -1;
	protected int maxSelectionRow = -1;
	protected int minSelectionColumn = -1;
	protected int maxSelectionColumn = -1;
	public boolean[] selectedColumns;

	// Used for rendering headers with ctrl-select
	protected HashSet<Integer> selectedColumnSet = new HashSet<>();
	protected HashSet<Integer> selectedRowSet = new HashSet<>();

	private SelectionType selectionType = SelectionType.CELLS;

	private boolean doShowDragHandle = true;
	private Color selectionRectangleColor = SELECTED_RECTANGLE_COLOR;

	// Dragging vars
	protected boolean isDraggingDot = false;
	protected int draggingToRow = -1;
	protected int draggingToColumn = -1;
	protected boolean isOverDot = false;
	protected boolean isDragging2 = false;

	protected int minColumn2 = -1;
	protected int maxColumn2 = -1;
	protected int minRow2 = -1;
	protected int maxRow2 = -1;

	protected boolean isOverDnDRegion = false;

	// Keep track of ctrl-down. This is needed in some
	// selection methods that do not receive key events.
	protected boolean metaDown = false;

	// Cells to be resized on next repaint are put in these HashSets.
	// A cell is added to a set when editing is done. The cells are removed
	// after a repaint in MyTable.
	protected HashSet<GPoint> cellResizeHeightSet;
	protected HashSet<GPoint> cellResizeWidthSet;

	private final ArrayList<GPoint> adjustedRowHeights = new ArrayList<>();
	private boolean doRecordRowHeights = true;

	public int preferredColumnWidth = SpreadsheetSettings.TABLE_CELL_WIDTH;

	// cursors
	protected Cursor defaultCursor = Cursor.getDefaultCursor();
	protected Cursor crossHairCursor = Cursor
			.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	protected Cursor handCursor = Cursor
			.getPredefinedCursor(Cursor.HAND_CURSOR);
	protected Cursor grabbingCursor;
	protected Cursor grabCursor;

	private SpreadsheetTableController controller;

	// Collection of cells that contain geos that can be edited with one click,
	// e.g. booleans, buttons, lists
	protected HashMap<SpreadsheetCoords, GeoElement> oneClickEditMap = new HashMap<>();

	public boolean isOverDnDRegion() {
		return isOverDnDRegion;
	}

	@Override
	public ArrayList<TabularRange> getSelectedRanges() {
		return selectedRanges;
	}

	public HashMap<SpreadsheetCoords, GeoElement> getOneClickEditMap() {
		return oneClickEditMap;
	}

	public void setOneClickEditMap(
			HashMap<SpreadsheetCoords, GeoElement> oneClickEditMap) {
		this.oneClickEditMap = oneClickEditMap;
	}

	/*******************************************************************
	 * Construct table
	 */
	public MyTableD(SpreadsheetViewD view, DefaultTableModel tableModel) {
		super(tableModel);

		cellResizeHeightSet = new HashSet<>();
		cellResizeWidthSet = new HashSet<>();

		app = view.getApplication();
		kernel = app.getKernel();
		this.tableModel = tableModel;
		this.view = view;

		grabCursor = createCursor(
				app.getImageIcon(GuiResourcesD.CURSOR_GRAB).getImage(), true);
		grabbingCursor = createCursor(
				app.getImageIcon(GuiResourcesD.CURSOR_GRABBING).getImage(),
				true);

		// set row height
		setRowHeight(SpreadsheetSettings.TABLE_CELL_HEIGHT);

		// prepare column headers
		SpreadsheetColumnControllerD columnController = new SpreadsheetColumnControllerD(
				app, this);
		headerRenderer = columnController.new ColumnHeaderRenderer();
		getTableHeader().setFocusable(true);
		getTableHeader().addMouseListener(columnController);
		getTableHeader().addMouseMotionListener(columnController);
		getTableHeader().addKeyListener(columnController);
		getTableHeader().setReorderingAllowed(false);
		setAutoCreateColumnsFromModel(false);

		// set columns and column headers
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		headerRenderer.setPreferredSize(new Dimension(preferredColumnWidth,
				SpreadsheetSettings.TABLE_CELL_HEIGHT));
		for (int i = 0; i < getColumnCount(); ++i) {
			getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
			getColumnModel().getColumn(i)
					.setPreferredWidth(preferredColumnWidth);
		}

		// set visual appearance
		setShowGrid(true);
		setGridColor(TABLE_GRID_COLOR);
		setSelectionBackground(SELECTED_BACKGROUND_COLOR);
		setSelectionForeground(Color.BLACK);

		// add cell renderer & editors
		setDefaultRenderer(Object.class, new SpreadsheetCellRendererD(this));
		editor = new MyCellEditorSpreadsheet(kernel, getEditorController());
		setDefaultEditor(Object.class, editor);

		// initialize selection fields
		selectedRanges = new ArrayList<>();
		selectedRanges.add(new TabularRange(-1, -1));
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setCellSelectionEnabled(true);

		// add mouse and key listeners
		SpreadsheetMouseListenerD ml = new SpreadsheetMouseListenerD(app, this);

		MouseListener[] mouseListeners = getMouseListeners();
		addMouseListener(ml);
		for (int i = 0; i < mouseListeners.length; ++i) {
			removeMouseListener(mouseListeners[i]);
			addMouseListener(mouseListeners[i]);
		}

		MouseMotionListener[] mouseMotionListeners = getMouseMotionListeners();
		addMouseMotionListener(ml);
		for (int i = 0; i < mouseMotionListeners.length; ++i) {
			removeMouseMotionListener(mouseMotionListeners[i]);
			addMouseMotionListener(mouseMotionListeners[i]);
		}

		// key listener
		KeyListener[] defaultKeyListeners = getKeyListeners();
		for (int i = 0; i < defaultKeyListeners.length; ++i) {
			removeKeyListener(defaultKeyListeners[i]);
		}
		addKeyListener(new SpreadsheetKeyListenerD(app, this));

		// setup selection listener
		// TODO
		// These listeners are no longer needed.
		// getSelectionModel().addListSelectionListener(new
		// RowSelectionListener());
		// getColumnModel().getSelectionModel().addListSelectionListener(new
		// ColumnSelectionListener());
		// getColumnModel().getSelectionModel().addListSelectionListener(columnHeader);

		// add table model listener
		tableModel.addTableModelListener(new MyTableModelListener());

		// relative copy
		relativeCopy = new RelativeCopy(kernel);
		copyPasteCut = new CopyPasteCutD(app);

		// - see ticket #135
		addFocusListener(this);

		// editing
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		columnModelListener = new MyTableColumnModelListener();
		getColumnModel().addColumnModelListener(columnModelListener);

		// set first cell active
		// needed in case spreadsheet selected with ctrl-tab rather than mouse
		// click
		// changeSelection(0, 0, false, false);

	}

	private SpreadsheetTableController getEditorController() {
		if (controller == null) {
			controller = new SpreadsheetTableController(app);
		}
		return controller;
	}

	/**
	 * End table constructor
	 ******************************************************************/

	/**
	 * Simple getter method
	 * 
	 * @return CopyPasteCut
	 */
	@Override
	public CopyPasteCut getCopyPasteCut() {
		return copyPasteCut;
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
	 * Simple getter method
	 * 
	 * @return App
	 */
	@Override
	public App getApplication() {
		return app;
	}

	/**
	 * Returns parent SpreadsheetView for this table
	 * 
	 * @return SpreadsheetView
	 */
	@Override
	public SpreadsheetViewD getView() {
		return view;
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
	 * @return boolean editor (checkbox) for this table. If none exists, a new
	 * one is created.
	 */
	public MyCellEditorBoolean getEditorBoolean() {
		if (editorBoolean == null) {
			editorBoolean = new MyCellEditorBoolean();
		}
		return editorBoolean;
	}

	/**
	 * @return button editor for this table. If none exists, a new one is
	 * created.
	 */
	public MyCellEditorButton getEditorButton() {
		if (editorButton == null) {
			editorButton = new MyCellEditorButton();
		}
		return editorButton;
	}

	/**
	 * @return list editor (comboBox) for this table. If none exists, a new one
	 * is created.
	 */
	public MyCellEditorList getEditorList() {
		if (editorList == null) {
			editorList = new MyCellEditorList();
		}
		return editorList;
	}

	/**
	 * Appends columns to the table if table model column count is larger than
	 * current number of table columns.
	 */
	protected void updateColumnCount() {

		if (tableModel.getColumnCount() <= this.getColumnCount()) {
			return;
		}

		// ensure that auto-create is off
		if (this.getAutoCreateColumnsFromModel()) {
			throw new IllegalStateException();
		}

		// add new columns to table
		for (int i = this.getColumnCount(); i < tableModel
				.getColumnCount(); ++i) {
			TableColumn col = new TableColumn(i);
			col.setHeaderRenderer(headerRenderer);
			col.setPreferredWidth(preferredColumnWidth);
			addColumn(col);
		}

		// addColumn destroys custom row heights, so we must reset them
		resetRowHeights();

	}

	@Override
	public TableCellEditor getCellEditor(int row, int column) {

		SpreadsheetCoords p = new SpreadsheetCoords(row, column);
		if (view.allowSpecialEditor() && oneClickEditMap.containsKey(p)
				&& kernel
						.getAlgebraStyleSpreadsheet() == Kernel.ALGEBRA_STYLE_VALUE) {

			switch (oneClickEditMap.get(p).getGeoClassType()) {
			case BOOLEAN:
				return getEditorBoolean();
			case BUTTON:
				return getEditorButton();
			case LIST:
				return getEditorList();
			}
		}
		return editor;
	}

	public void setEnableAutoComplete(boolean enableAutoComplete) {
		editor.setEnableAutoComplete(enableAutoComplete);
	}

	/**
	 * sets requirement that commands entered into cells must start with "="
	 */
	public void setEqualsRequired(boolean isEqualsRequired) {
		editor.setEqualsRequired(isEqualsRequired);
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

	public class MyTableModelListener implements TableModelListener {

		@Override
		public void tableChanged(TableModelEvent e) {
			// force rowHeader redraw when a new row is added (after drag
			// down or arrow down)
			if (e.getType() == TableModelEvent.INSERT) {
				getView().updateRowHeader();
			}
			// update table column model if new columns added
			if (e.getType() == TableModelEvent.UPDATE) {
				updateColumnCount();
			}

		}
	}

	// ===============================================================
	// Selection
	// ===============================================================
	@Override
	public void changeSelection(int rowIndex, int columnIndex, boolean extend) {
		this.changeSelection(rowIndex, columnIndex, false, extend);
	}

	/**
	 * JTable does not support non-contiguous cell selection. It treats
	 * ctrl-down cell selection as if it was shift-extend. To prevent this
	 * behavior the JTable changeSelection method is overridden here.
	 */
	@Override
	public void changeSelection(int rowIndex, int columnIndex, boolean toggle,
			boolean extend) {
		// if(Application.getControlDown())
		// super.changeSelection(rowIndex, columnIndex, false, false);
		// else

		// force column selection
		if (view.isColumnSelect()) {
			setColumnSelectionInterval(columnIndex, columnIndex);
		}

		super.changeSelection(rowIndex, columnIndex, toggle, extend);
		// let selectionChanged know about a change in single cell selection
		selectionChanged();
	}

	@Override
	public void selectAll() {
		setSelectionType(SelectionType.CELLS);
		this.setAutoscrolls(false);
		// select the upper left corner cell
		changeSelection(0, 0, false, false);
		// extend the selection to the current lower right corner cell
		changeSelection(getRowCount() - 1, getColumnCount() - 1, false, true);
		setSelectAll(true);
		this.setAutoscrolls(true);

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
	@Override
	public void selectionChanged() {

		// create a cell range object to store
		// the current table selection

		TabularRange newSelection;

		if (view.isTraceDialogVisible()) {

			newSelection = view.getTraceSelectionRange(
					getColumnModel().getSelectionModel()
							.getAnchorSelectionIndex(),
					getSelectionModel().getAnchorSelectionIndex());
			if (newSelection == null) {
				return;
			}
			scrollRectToVisible(getCellRect(newSelection.getMinRow(),
					newSelection.getMaxColumn(), true));

		} else {

			switch (selectionType) {

			default:
			case CELLS:
				newSelection = new TabularRange(
						getSelectionModel().getAnchorSelectionIndex(),
						getColumnModel().getSelectionModel().getAnchorSelectionIndex(),
						getSelectionModel().getLeadSelectionIndex(),
						getColumnModel().getSelectionModel().getLeadSelectionIndex()
				);
				break;

			case ROWS:
				newSelection = new TabularRange(getSelectionModel().getAnchorSelectionIndex(), -1,
						getSelectionModel().getLeadSelectionIndex(), -1
				);
				break;

			case COLUMNS:
				newSelection = new TabularRange(
						-1, getColumnModel().getSelectionModel()
								.getAnchorSelectionIndex(),
						-1, getColumnModel().getSelectionModel()
								.getLeadSelectionIndex()
				);
				break;
			case ALL:
				newSelection = new TabularRange(-1, -1,
						-1, -1);
				break;
			}

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

		// check for change in anchor cell (for now this is minrow and mincol
		// ...)
		boolean changedAnchor = minSelectionColumn
				- newSelection.getMinColumn() != 0
				|| minSelectionRow - newSelection.getMinRow() != 0;

		// update selection list and internal variables
		newSelection = CellRangeUtil.getActual(newSelection, app);
		if (!app.getControlDown()) {
			selectedRanges.clear();
			selectedColumnSet.clear();
			selectedRowSet.clear();
			selectedRanges.add(0, newSelection);

		} else { // ctrl-select
			// handle dragging
			if (selectedRanges.get(0).hasSameAnchor(newSelection)) {
				selectedRanges.remove(0);
			}

			// add the selection to the list
			selectedRanges.add(0, newSelection);
		}
		minSelectionColumn = newSelection.getMinColumn();
		maxSelectionColumn = newSelection.getMaxColumn();
		minSelectionRow = newSelection.getMinRow();
		maxSelectionRow = newSelection.getMaxRow();

		// newSelection.debug();
		// printSelectionParameters();

		if (isSelectNone && (minSelectionColumn != -1 || minSelectionRow != -1)) {
			setSelectNone(false);
		}

		if (changedAnchor && !isEditing()) {
			view.updateFormulaBar();
		}

		// update the geo selection list
		ArrayList<GeoElement> list = new ArrayList<>();
		for (int i = 0; i < selectedRanges.size(); i++) {
			list.addAll(0, CellRangeUtil.toGeoList(selectedRanges.get(i), app));
		}

		// if the geo selection has changed, update selected geos
		boolean changed = !list
				.equals(app.getSelectionManager().getSelectedGeos());
		if (changed) {

			if (getTableMode() == MyTable.TABLE_MODE_AUTOFUNCTION) {
				getSpreadsheetModeProcessor().updateAutoFunction();
			}

			if (view.isVisibleStyleBar()) {
				view.getSpreadsheetStyleBar().updateStyleBar();
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

		// if the selection has changed or an empty cell has been clicked,
		// repaint
		if (changed || list.isEmpty()) {
			repaint();
			if (this.getTableHeader() != null) {
				getTableHeader().repaint();
			}
		}
	}

	/**
	 * Sets the initial selection parameters to a single cell. Does this without
	 * calling changeSelection, so it should only be used at startup.
	 */
	public void setInitialCellSelection(int row, int column) {

		setSelectionType(SelectionType.CELLS);

		if (column == -1) {
			minSelectionColumn = 0;
		} else {
			minSelectionColumn = column;
		}
		if (row == -1) {
			minSelectionRow = 0;
		} else {
			minSelectionRow = row;
		}
		maxSelectionColumn = minSelectionColumn;
		maxSelectionRow = minSelectionRow;

		getColumnModel().getSelectionModel().setSelectionInterval(
				minSelectionColumn, maxSelectionColumn);
		getSelectionModel().setSelectionInterval(minSelectionRow,
				maxSelectionRow);
	}

	/**
	 * @param cellName cell name
	 * @return success
	 */
	public boolean setSelection(String cellName) {

		if (cellName == null) {
			return setSelection(-1, -1, -1, -1);
		}

		SpreadsheetCoords newCell = GeoElementSpreadsheet.spreadsheetIndices(cellName);
		if (newCell.column != -1 && newCell.row != -1) {
			return setSelection(newCell.column, newCell.row);
		}
		return false;
	}

	@Override
	public boolean setSelection(int c, int r) {
		TabularRange tr = new TabularRange(r, c, r, c);
		return setSelection(tr);
	}

	/**
	 * @param c1 start column
	 * @param r1 start row
	 * @param c2 end column
	 * @param r2 end row
	 * @return success
	 */
	public boolean setSelection(int c1, int r1, int c2, int r2) {
		TabularRange tr = new TabularRange(r1, c1, r2, c2);
		if (!tr.isValid()) {
			return false;
		}

		return setSelection(tr);
	}

	@Override
	public boolean setSelection(TabularRange tr) {

		if (tr != null && !tr.isValid()) {
			return false;
		}

		try {
			if (tr == null || tr.isEmptyRange()) {
				getSelectionModel().clearSelection();

			} else {

				this.setAutoscrolls(false);

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
					changeSelection(tr.getMinRow(), tr.getMinColumn(), false,
							false);
					changeSelection(tr.getMaxRow(), tr.getMaxColumn(), false,
							true);
				}

				selectionChanged();

				// scroll to upper left corner of rectangle
				this.setAutoscrolls(true);
				scrollRectToVisible(
						getCellRect(tr.getMinRow(), tr.getMinColumn(), true));
				repaint();
			}
		} catch (Exception e) {
			Log.debug(e);
			return false;
		}

		return true;
	}

	/**
	 * @param selType0 selection type
	 */
	public void setSelectionType(SelectionType selType0) {
		SelectionType selType = selType0;
		if (view.isColumnSelect()) {
			selType = SelectionType.COLUMNS;
		}

		switch (selType) {

		default:
		case CELLS:
			setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			setColumnSelectionAllowed(true);
			setRowSelectionAllowed(true);
			break;

		case ROWS:
			setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			setColumnSelectionAllowed(false);
			setRowSelectionAllowed(true);
			break;

		case COLUMNS:
			setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			setColumnSelectionAllowed(true);
			setRowSelectionAllowed(false);
			break;

		}

		this.selectionType = selType;

	}

	@Override
	public SelectionType getSelectionType() {
		return selectionType;
	}

	// By adding a call to selectionChanged in JTable's setRowSelectionInterval
	// and setColumnSelectionInterval methods, selectionChanged becomes
	// the sole handler for selection events.
	@Override
	public void setRowSelectionInterval(int row0, int row1) {
		setSelectionType(SelectionType.ROWS);
		super.setRowSelectionInterval(row0, row1);
		selectionChanged();
	}

	@Override
	public void setColumnSelectionInterval(int col0, int col1) {
		setSelectionType(SelectionType.COLUMNS);
		super.setColumnSelectionInterval(col0, col1);
		selectionChanged();
	}

	public boolean isSelectNone() {
		return isSelectNone;
	}

	/**
	 * @param isSelectNone whether to empty selection
	 */
	public void setSelectNone(boolean isSelectNone) {

		this.isSelectNone = isSelectNone;

		if (isSelectNone) {
			setSelection(-1, -1, -1, -1);
			view.updateFormulaBar();
		}

	}

	@Override
	public boolean isSelectAll() {
		return isSelectAll;
	}

	public void setSelectAll(boolean isSelectAll) {
		this.isSelectAll = isSelectAll;
	}

	/**
	 * @return list of selected columns
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

	@Override
	public int[] getSelectedColumns() {

		ArrayList<Integer> columns = getSelectedColumnsList();
		int[] ret = new int[columns.size()];
		for (int c = 0; c < columns.size(); c++) {
			ret[c] = columns.get(c);
		}

		return ret;
	}

	public void setSelectionRectangleColor(Color color) {
		selectionRectangleColor = color;
	}

	protected GPoint getPixel(int column, int row, boolean min) {
		if (column < 0 || row < 0) {
			return null;
		}
		if (min && column == 0 && row == 0) {
			return new GPoint(0, 0);
		}

		Rectangle cellRect = getCellRect(row, column, false);
		if (min) {
			return new GPoint(cellRect.x, cellRect.y);
		}
		return new GPoint(cellRect.x + cellRect.width,
				cellRect.y + cellRect.height);
	}

	protected GPoint getMinSelectionPixel() {
		return getPixel(minSelectionColumn, minSelectionRow, true);
	}

	protected GPoint getMaxSelectionPixel() {
		return getPixel(maxSelectionColumn, maxSelectionRow, false);
	}

	/**
	 * @return Point(columnIndex, rowIndex), cell indices for the given pixel
	 * location
	 */
	public SpreadsheetCoords getIndexFromPixel(int x, int y) {
		if (x < 0 || y < 0) {
			return null;
		}
		int column = -1;
		int row = -1;
		for (int i = 0; i < getColumnCount(); ++i) {
			GPoint point = getPixel(i, 0, false);
			if (x < point.getX()) {
				column = i;
				break;
			}
		}
		if (column == -1) {
			return null;
		}
		for (int i = 0; i < getRowCount(); ++i) {
			GPoint point = getPixel(0, i, false);
			if (y < point.getY()) {
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
	 * @param column1 start column
	 * @param row1 start row
	 * @param column2 end column
	 * @param row2 end row
	 * @param includeSpacing whether to include spacing
	 * @return rectangle
	 */
	public Rectangle getCellBlockRect(int column1, int row1, int column2,
			int row2, boolean includeSpacing) {
		Rectangle r1 = getCellRect(row1, column1, includeSpacing);
		Rectangle r2 = getCellRect(row2, column2, includeSpacing);
		r1.setBounds(r1.x, r1.y, (r2.x - r1.x) + r2.width,
				(r2.y - r1.y) + r2.height);
		return r1;
	}

	/**
	 * @return selection rectangle
	 */
	public Rectangle getSelectionRect() {
		return getCellBlockRect(minSelectionColumn, minSelectionRow,
				maxSelectionColumn, maxSelectionRow, true);
	}

	// target selection frame
	// =============================

	public Rectangle getTargetcellFrame() {
		return targetcellFrame;
	}

	public void setTargetcellFrame(Rectangle targetcellFrame) {
		this.targetcellFrame = targetcellFrame;
	}

	/**
	 * Checks selection state and fixed geos
	 * @return whether dragging iis possible
	 */
	public boolean showCanDragBlueDot() {
		boolean showBlueDot = !editor.isEditing()
				&& !view.isTraceDialogVisible();

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

	// ===============================================================
	// Paint
	// ===============================================================

	/**
	 * Overrides the paint() to draw special spreadsheet table graphics, e.g.
	 * selection rectangle and custom borders
	 */
	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);

		Graphics2D g2 = (Graphics2D) graphics;

		// draw custom borders
		SpreadsheetBorders.drawFormatBorders(g2, this);

		// draw special target cell frame
		if (targetcellFrame != null) {
			g2.setColor(GColorD.getAwtColor(GeoGebraColorConstants.DARKBLUE));
			g2.setStroke(dashed);
			g2.draw(targetcellFrame);
		}

		// if the spreadsheet doesn't have focus
		// then don't draw the selection graphics ... exit now
		if (!view.hasViewFocus()) {
			if (!isSelectNone) {
				setSelectNone(true);
			}
			return;
		}

		// draw special dragging frame for cell editor
		if (isDragging2) {
			GPoint point1 = getPixel(minColumn2, minRow2, true);
			GPoint point2 = getPixel(maxColumn2, maxRow2, false);
			int x1 = point1.getX();
			int y1 = point1.getY();
			int x2 = point2.getX();
			int y2 = point2.getY();
			graphics.setColor(Color.GRAY);
			graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS1);
			graphics.fillRect(x1, y1, LINE_THICKNESS1, y2 - y1);
			graphics.fillRect(x1, y2 - LINE_THICKNESS1, x2 - x1,
					LINE_THICKNESS1);
			graphics.fillRect(x2 - LINE_THICKNESS1, y1, LINE_THICKNESS1,
					y2 - y1);
		}

		// draw dragging frame
		if (draggingToRow != -1 && draggingToColumn != -1) {
			// -|1|-
			// 2|-|3
			// -|4|-
			graphics.setColor(Color.gray);
			if (draggingToColumn < minSelectionColumn) { // 2
				GPoint point1 = getPixel(draggingToColumn, minSelectionRow,
						true);
				GPoint point2 = getPixel(minSelectionColumn - 1,
						maxSelectionRow, false);
				int x1 = point1.getX();
				int y1 = point1.getY();
				int x2 = point2.getX();
				int y2 = point2.getY();
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS1);
				graphics.fillRect(x1, y1, LINE_THICKNESS1, y2 - y1);
				graphics.fillRect(x1, y2 - LINE_THICKNESS1, x2 - x1,
						LINE_THICKNESS1);
			} else if (draggingToRow > maxSelectionRow) { // 4
				GPoint point1 = getPixel(minSelectionColumn,
						maxSelectionRow + 1, true);
				GPoint point2 = getPixel(maxSelectionColumn, draggingToRow,
						false);
				int x1 = point1.getX();
				int y1 = point1.getY();
				int x2 = point2.getX();
				int y2 = point2.getY();
				graphics.fillRect(x1, y1, LINE_THICKNESS1, y2 - y1);
				graphics.fillRect(x1, y2 - LINE_THICKNESS1, x2 - x1,
						LINE_THICKNESS1);
				graphics.fillRect(x2 - LINE_THICKNESS1, y1, LINE_THICKNESS1,
						y2 - y1);
			} else if (draggingToRow < minSelectionRow) { // 1
				GPoint point1 = getPixel(minSelectionColumn, draggingToRow,
						true);
				GPoint point2 = getPixel(maxSelectionColumn,
						minSelectionRow - 1, false);
				int x1 = point1.getX();
				int y1 = point1.getY();
				int x2 = point2.getX();
				int y2 = point2.getY();
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS1);
				graphics.fillRect(x1, y1, LINE_THICKNESS1, y2 - y1);
				graphics.fillRect(x2 - LINE_THICKNESS1, y1, LINE_THICKNESS1,
						y2 - y1);
			} else if (draggingToColumn > maxSelectionColumn) { // 3
				GPoint point1 = getPixel(maxSelectionColumn + 1,
						minSelectionRow, true);
				GPoint point2 = getPixel(draggingToColumn, maxSelectionRow,
						false);
				int x1 = point1.getX();
				int y1 = point1.getY();
				int x2 = point2.getX();
				int y2 = point2.getY();
				graphics.fillRect(x2 - LINE_THICKNESS1, y1, LINE_THICKNESS1,
						y2 - y1);
				graphics.fillRect(x1, y2 - LINE_THICKNESS1, x2 - x1,
						LINE_THICKNESS1);
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS1);
			}
		}

		// draw dragging dot
		GPoint pixel1 = getMaxSelectionPixel();
		if (doShowDragHandle && pixel1 != null && !editor.isEditing()) {

			if (showCanDragBlueDot()) {
				// Highlight the dragging dot if mouseover
				if (isOverDot) {
					graphics.setColor(Color.gray);
				} else {
					graphics.setColor(selectionRectangleColor);
				}

				int x = pixel1.getX() - (DOT_SIZE + 1) / 2;
				int y = pixel1.getY() - (DOT_SIZE + 1) / 2;
				graphics.fillRect(x, y, DOT_SIZE, DOT_SIZE);
			}
		}

		if (minSelectionRow != -1 && maxSelectionRow != -1
				&& minSelectionColumn != -1 && maxSelectionColumn != -1) {
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
				graphics.fillRect(x2 - LINE_THICKNESS2, y1, LINE_THICKNESS2,
						y2 - y1);
				graphics.fillRect(x1, y2 - LINE_THICKNESS2, x2 - x1,
						LINE_THICKNESS2);
			}
			// draw small frame around current editing cell
			else {
				x1 -= LINE_THICKNESS2 - 1;
				x2 += LINE_THICKNESS2 - 1;
				y1 -= LINE_THICKNESS2 - 1;
				y2 += LINE_THICKNESS2 - 1;
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS2);
				graphics.fillRect(x1, y1, LINE_THICKNESS2, y2 - y1);
				graphics.fillRect(x2 - LINE_THICKNESS2, y1, LINE_THICKNESS2,
						y2 - y1);
				graphics.fillRect(x1, y2 - LINE_THICKNESS2, x2 - x1,
						LINE_THICKNESS2);
			}
		}

		// After rendering the LaTeX image for a geo, update the row height
		// with the preferred size set by the renderer.
		resizeMarkedCells();

	}

	/**
	 * Starts in-cell editing for cells with short editing strings. For strings
	 * longer than MAX_CELL_EDIT_STRING_LENGTH, the redefine dialog is shown.
	 * Also prevents fixed cells from being edited.
	 */
	@Override
	public boolean editCellAt(int row, int col) {
		Object ob = getValueAt(row, col);

		// prepare editor to handle equals
		editor.setEqualsRequired(
				app.getSettings().getSpreadsheet().equalsRequired());

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
		return super.editCellAt(row, col);
	}

	// This handles ctrl-select dragging of cell blocks
	// because JTable does not do this correctly.
	// TODO: JTable is still making selections that are not overridden,
	// so sometimes you can still get unwanted extended selection.
	//
	protected void handleControlDragSelect(MouseEvent e) {

		Point p = e.getPoint();
		int row = this.rowAtPoint(p);
		int column = this.columnAtPoint(p);
		ListSelectionModel cm = getColumnModel().getSelectionModel();
		ListSelectionModel rm = getSelectionModel();

		/*
		 * //handle startup case of empty selection if ((column == -1) && (row
		 * == -1)){ cm.setSelectionInterval(0, 0); rm.setSelectionInterval(0,
		 * 0); }
		 */

		if ((column == -1) || (row == -1)) {
			return;
		}

		// adjust the selection if mouse has left the old selected cell
		if (row != this.getSelectedRow()
				|| column != this.getSelectedColumn()) {
			// boolean selected = true;
			int colAnchor = cm.getAnchorSelectionIndex();
			int rowAnchor = rm.getAnchorSelectionIndex();

			if (rowAnchor == -1 || rowAnchor >= getRowCount()) {
				rowAnchor = 0;
				// selected = false;
			}

			if (colAnchor == -1 || colAnchor >= getColumnCount()) {
				colAnchor = 0;
				// selected = false;
			}

			// selected = selected && isCellSelected(rowAnchor, colAnchor);

			cm.setSelectionInterval(colAnchor, column);
			rm.setSelectionInterval(rowAnchor, row);

			selectionChanged();

		}

	}

	@Override
	public int convertColumnIndexToModel(int viewColumnIndex) {
		return viewColumnIndex;
	}

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
	@Override
	public boolean isCellEditable(int row, int column) {
		if (view.isColumnSelect()) {
			return false;
		}

		// allow use of special editors for e.g. buttons, lists
		if (view.allowSpecialEditor()
				&& oneClickEditMap.containsKey(new SpreadsheetCoords(row, column))) {
			return true;
		}

		// normal case: return false so we can handle double click in our
		// mouseReleased
		if (!allowEditing) {
			return false;
		}

		// prevent editing fixed geos when allowEditing == true
		GeoElement geo = (GeoElement) getModel().getValueAt(row, column);
		// return true when editing is allowed (mostly for blank cells). This
		// lets
		// the JTable mousePressed listener catch double clicks and invoke the
		// editor
		return geo == null || !geo.isProtected(EventType.UPDATE);
	}

	/** Set editor text */
	public void updateEditor(String text) {
		if (this.isEditing()) {
			editor.setText(text);
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (AppD.isVirtualKeyboardActive()) {
			((GuiManagerD) app.getGuiManager()).toggleKeyboard(true);
		}

	}

	@Override
	public void focusLost(FocusEvent e) {
		// avoid infinite loop!
		if (e.getOppositeComponent() instanceof VirtualKeyboardD) {
			return;
		}
		if (AppD.isVirtualKeyboardActive()) {
			((GuiManagerD) app.getGuiManager()).toggleKeyboard(false);
		}

	}

	// Keep row heights of table and rowHeader in sync
	@Override
	public void setRowHeight(int row, int rowHeight) {
		super.setRowHeight(row, rowHeight);
		try {
			if (view != null) {
				view.updateRowHeader();
				if (doRecordRowHeights) {
					adjustedRowHeights.add(new GPoint(row, rowHeight));
				}
				view.updateRowHeightSetting(row, rowHeight);
			}
		} catch (Exception e) {
			Log.debug(e);
		}
	}

	@Override
	public void setRowHeight(int rowHeight) {
		super.setRowHeight(rowHeight);
		try {
			if (view != null) {
				view.updateRowHeader();
				view.updatePreferredRowHeight(rowHeight);
			}
		} catch (Exception e) {
			Log.debug(e);
		}

	}

	/** Reset the row heights --- used after addColumn destroys the row heights */
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

		Dimension prefSize = this
				.getCellRenderer(row, col).getTableCellRendererComponent(this,
						this.getValueAt(row, col), false, false, row, col)
				.getPreferredSize();

		if (adjustWidth) {

			TableColumn tableColumn = this.getColumnModel().getColumn(col);

			int resultWidth = Math.max(tableColumn.getWidth(),
					(int) prefSize.getWidth());
			tableColumn
					.setWidth(resultWidth + this.getIntercellSpacing().width);
		}

		if (adjustHeight) {

			int resultHeight = Math.max(getRowHeight(row),
					(int) prefSize.getHeight());
			setRowHeight(row, resultHeight);
		}

	}

	/**
	 * Adjust the width of a column to fit the maximum preferred width of its
	 * cell contents.
	 */
	public void fitColumn(int column) {

		TableColumn tableColumn = getColumnModel().getColumn(column);

		int prefWidth = 0;
		int tempWidth = -1;
		for (int row = 0; row < getRowCount(); row++) {
			if (getValueAt(row, column) != null) {
				tempWidth = (int) getCellRenderer(row, column)
						.getTableCellRendererComponent(this,
								getValueAt(row, column), false, false, row,
								column)
						.getPreferredSize().getWidth();
				prefWidth = Math.max(prefWidth, tempWidth);
			}
		}

		// set the new column width
		if (tempWidth == -1) {
			// column is empty
			prefWidth = preferredColumnWidth - getIntercellSpacing().width;
		} else {
			prefWidth = Math.max(prefWidth, tableColumn.getMinWidth());
		}
		// note: the table might have its header set to null,
		// so we get the actual header from view
		view.getTableHeader().setResizingColumn(tableColumn);
		tableColumn.setWidth(prefWidth + getIntercellSpacing().width);

	}

	/**
	 * Adjust the height of a row to fit the maximum preferred height of the its
	 * cell contents.
	 */
	public void fitRow(int row) {

		int prefHeight = this.getRowHeight();
		int tempHeight = 0;
		for (int column = 0; column < this.getColumnCount(); column++) {

			tempHeight = (int) this.getCellRenderer(row, column)
					.getTableCellRendererComponent(this,
							getValueAt(row, column), false, false, row, column)
					.getPreferredSize().getHeight();

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
			Log.debug("MyTableD.fitAll is only partly implemented");
			// for (int column = 0; column < getColumnCount(); column++) {
			// TODO:test//fitColumn(column);
			// }
		}
	}

	/**
	 * Column model listener --- used to reset the preferred column width when
	 * all columns have been selected.
	 */
	public class MyTableColumnModelListener
			implements TableColumnModelListener {

		@Override
		public void columnMarginChanged(ChangeEvent e) {
			if (isSelectAll() && minSelectionColumn >= 0) {
				preferredColumnWidth = getColumnModel()
						.getColumn(minSelectionColumn).getPreferredWidth();
				// view.updatePreferredColumnWidth(preferredColumnWidth);
			}
			// TODO: find more efficient way to record column widths
			view.updateAllColumnWidthSettings();
		}

		@Override
		public void columnAdded(TableColumnModelEvent arg0) {
			// only care about margin
		}

		@Override
		public void columnMoved(TableColumnModelEvent arg0) {
			// only care about margin
		}

		@Override
		public void columnRemoved(TableColumnModelEvent arg0) {
			// only care about margin
		}

		@Override
		public void columnSelectionChanged(ListSelectionEvent arg0) {
			// only care about margin
		}
	}

	// When the spreadsheet is smaller than the viewport fill the extra space
	// with
	// the same background color as the spreadsheet.
	// This gives a smoother look when the spreadsheet auto-adjusts to fill the
	// space.

	@Override
	protected void configureEnclosingScrollPane() {
		super.configureEnclosingScrollPane();
		Container p = getParent();
		if (p instanceof JViewport) {
			((JViewport) p).setBackground(getBackground());
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
	 * @param tableMode table mode
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
			// Clear the targetcellFrame and ensure the selection rectangle
			// color is standard
			targetcellFrame = null;
			this.setSelectionRectangleColor(Color.BLUE);
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
			if (RelativeCopy.getValue(app, minSelectionColumn,
					minSelectionRow) != null) {
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

			// Set the targetcellFrame so the Paint method can use it to draw a
			// dashed frame
			targetcellFrame = this.getCellBlockRect(minSelectionColumn,
					minSelectionRow, minSelectionColumn, minSelectionRow, true);

			// Change the selection frame color to gray
			// and clear the current selection
			setSelectionRectangleColor(Color.GRAY);
			minSelectionColumn = -1;
			maxSelectionColumn = -1;
			minSelectionRow = -1;
			maxSelectionRow = -1;
			app.getSelectionManager().clearSelectedGeos();

		}

		// try to create autoFunction cell(s) adjacent to the selection
		else if (selectedRanges.size() == 1) {

			try {
				getSpreadsheetModeProcessor().performAutoFunctionCreation(
						selectedRanges.get(0), app.getShiftDown());
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
	 * Copy selection
	 * @param altDown whether alt is pressed (skips copy to internal buffer)
	 */
	public void copy(boolean altDown) {
		copyPasteCut.copy(minSelectionColumn, minSelectionRow,
				maxSelectionColumn, maxSelectionRow, altDown);
	}

	/**
	 * Paste into selection
	 * @return success
	 */
	public boolean paste() {
		return copyPasteCut.paste(minSelectionColumn, minSelectionRow,
				maxSelectionColumn, maxSelectionRow);
	}

	/**
	 * Cut selection
	 * @return success
	 */
	public boolean cut() {
		return copyPasteCut.cut(minSelectionColumn, minSelectionRow,
				maxSelectionColumn, maxSelectionRow);
	}

	/**
	 * Delete selection
	 * @return success
	 */
	public boolean delete() {
		return copyPasteCut.delete(minSelectionColumn, minSelectionRow,
				maxSelectionColumn, maxSelectionRow);
	}

	private static Cursor createCursor(Image cursorImage, boolean center) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Point cursorHotSpot;
		if (center) {
			cursorHotSpot = new Point(cursorImage.getWidth(null) / 2,
					cursorImage.getHeight(null) / 2);
		} else {
			cursorHotSpot = new Point(0, 0);
		}
		Cursor cursor = toolkit.createCustomCursor(cursorImage, cursorHotSpot,
				null);
		return cursor;
	}

	@Override
	public void updateCellFormat(String cellFormat) {
		view.updateCellFormat(cellFormat);

	}

	@Override
	public boolean allowSpecialEditor() {
		return view.allowSpecialEditor();
	}

	@Override
	public void updateTableCellValue(Object value, int i, int j) {
		// only used in Web
	}

	@Override
	public void repaintAll() {
		repaint();
		// method for web, do nothing else here
	}

	/**
	 * @return spreadsheet mode processor
	 */
	public SpreadsheetModeProcessor getSpreadsheetModeProcessor() {
		if (this.spreadsheetModeProcessor == null) {
			this.spreadsheetModeProcessor = new SpreadsheetModeProcessor(app,
					this);
		}
		return this.spreadsheetModeProcessor;
	}

}
