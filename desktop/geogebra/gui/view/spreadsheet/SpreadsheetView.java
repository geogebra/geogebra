package geogebra.gui.view.spreadsheet;

import geogebra.common.awt.GPoint;
import geogebra.common.gui.view.spreadsheet.CellRange;
import geogebra.common.gui.view.spreadsheet.MyTableInterface;
import geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.SettingListener;
import geogebra.common.main.settings.SpreadsheetSettings;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.layout.LayoutD;
import geogebra.gui.view.Gridable;
import geogebra.main.AppD;
import geogebra.main.SpreadsheetTableModelD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class SpreadsheetView implements SpreadsheetViewInterface,
		ComponentListener, FocusListener, Gridable, SettingListener {

	private static final long serialVersionUID = 1L;

	// ggb fields
	protected AppD app;
	private Kernel kernel;

	// spreadsheet gui components
	private JPanel spreadsheetWrapper;
	private MyTableD table;
	protected SpreadsheetTableModelD tableModel;
	private SpreadsheetRowHeader rowHeader;
	private JTableHeader tableHeader;

	// moved to kernel
	// if these are increased above 32000, you need to change traceRow to an
	// int[]
	// public static int MAX_COLUMNS = 9999; // TODO make sure this is actually
	// used
	// public static int MAX_ROWS = 9999; // TODO make sure this is actually
	// used

	private static int DEFAULT_COLUMN_WIDTH = 70;
	public static final int ROW_HEADER_WIDTH = 35; // wide enough for "9999"

	// TODO: should traceDialog belong to the SpreadsheetTraceManager?
	private TraceDialog traceDialog;
	// button to launch trace dialog from upper left corner
	private JButton btnTraceDialog;

	// fields for split panel, stylebar
	private JScrollPane spreadsheet;
	private int defaultDividerLocation = 150;
	private SpreadsheetStyleBar styleBar;
	private JPanel restorePanel;

	// toolbar manager
	SpreadsheetToolbarManager toolbarManager;

	// current toolbar mode
	private int mode = -1;

	private FormulaBar formulaBar;
	private JPanel spreadsheetPanel;

	private SpreadsheetViewDnD dndHandler;

	/******************************************************
	 * Construct spreadsheet view.
	 */
	public SpreadsheetView(AppD app) {

		this.app = app;
		kernel = app.getKernel();

		// Initialize settings and register listener
		app.getSettings().getSpreadsheet().addListener(this);

		createGUI();

		spreadsheetWrapper.addFocusListener(this);
		updateFonts();
		attachView();

		// Create tool bar manager to handle tool bar mode changes
		toolbarManager = new SpreadsheetToolbarManager(app, this);

		dndHandler = new SpreadsheetViewDnD(app, this);

		settingsChanged(settings());

	}

	private void createGUI() {

		spreadsheetWrapper = new JPanel();

		// Build the spreadsheet table and enclosing scrollpane
		buildSpreadsheet();

		// Build the spreadsheet panel: formulaBar above, spreadsheet in Center
		spreadsheetPanel = new JPanel(new BorderLayout());
		spreadsheetPanel.add(spreadsheet, BorderLayout.CENTER);

		spreadsheetWrapper.setLayout(new BorderLayout());
		spreadsheetWrapper.add(spreadsheetPanel, BorderLayout.CENTER);

		spreadsheetWrapper.setBorder(BorderFactory.createEmptyBorder());

	}

	private void buildSpreadsheet() {

		// Create the spreadsheet table model and the table
		tableModel = (SpreadsheetTableModelD) app.getSpreadsheetTableModel();
		table = new MyTableD(this, tableModel.getDefaultTableModel());

		// Create row header
		rowHeader = new SpreadsheetRowHeader(app, table);

		// Set column width
		table.headerRenderer.setPreferredSize(new Dimension(
				(table.preferredColumnWidth),
				(SpreadsheetSettings.TABLE_CELL_HEIGHT)));

		// Put the table and the row header into a scroll plane
		// The scrollPane is named as spreadsheet
		spreadsheet = new JScrollPane();
		spreadsheet.setBorder(BorderFactory.createEmptyBorder());
		spreadsheet.setRowHeaderView(rowHeader);
		spreadsheet.setViewportView(table);

		// save the table header
		tableHeader = table.getTableHeader();

		// Create and set the scrollpane corners
		spreadsheet.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER,
				newUpperLeftCorner());
		spreadsheet.setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER,
				new Corner());
		spreadsheet.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
				new Corner());

		// Add a resize listener to the table so it can auto-enlarge if needed
		table.addComponentListener(this);

	}

	// ===============================================================
	// Corners
	// ===============================================================

	private static class Corner extends JComponent {
		private static final long serialVersionUID = -4426785169061557674L;

		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(MyTableD.BACKGROUND_COLOR_HEADER);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	private Corner newUpperLeftCorner() {

		Corner upperLeftCorner = new Corner(); // use FlowLayout

		upperLeftCorner.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 1,
						MyTableD.HEADER_GRID_COLOR), BorderFactory
						.createEmptyBorder(0, 5, 0, 0)));

		upperLeftCorner.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				table.selectAll();
			}
		});

		// add trace dialog button
		btnTraceDialog = new JButton(
				app.getScaledIcon("spreadsheettrace_button.gif")) {
		};
		btnTraceDialog.setBorderPainted(false);
		btnTraceDialog.setPreferredSize(new Dimension(18, 18));
		btnTraceDialog.setContentAreaFilled(false);
		// invisible button unless a trace is set
		btnTraceDialog.setVisible(false);
		btnTraceDialog.setToolTipText(app.getLocalization().getMenuTooltip(
				"TraceToSpreadsheet"));
		btnTraceDialog.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				showTraceDialog(null, table.selectedCellRanges.get(0));
			}
		});

		upperLeftCorner.setLayout(new BorderLayout());
		upperLeftCorner.add(btnTraceDialog, BorderLayout.WEST);

		return upperLeftCorner;
	}

	// ===============================================================
	// Defaults
	// ===============================================================

	public void setDefaultSelection() {
		setSpreadsheetScrollPosition(0, 0);
		table.setInitialCellSelection(0, 0);
	}

	// ===============================================================
	// getters/setters
	// ===============================================================

	public AppD getApplication() {
		return app;
	}

	public MyTableInterface getSpreadsheetTable() {
		return table;
	}

	public JViewport getRowHeader() {
		return spreadsheet.getRowHeader();
	}

	public void rowHeaderRevalidate() {
		spreadsheet.getRowHeader().revalidate();
	}

	public JViewport getColumnHeader() {
		return spreadsheet.getColumnHeader();
	}

	public void columnHeaderRevalidate() {
		spreadsheet.getColumnHeader().revalidate();
	}

	public JTableHeader getTableHeader() {
		return tableHeader;
	}

	public int getMode() {
		return mode;
	}

	/**
	 * get spreadsheet styleBar
	 */
	public SpreadsheetStyleBar getSpreadsheetStyleBar() {
		if (styleBar == null) {
			styleBar = new SpreadsheetStyleBar(this);
		}
		return styleBar;
	}

	/**
	 * @return panel that contains the entire spreadsheet GUI
	 */
	public JComponent getContainerPanel() {
		return spreadsheetWrapper;
	}

	// ===============================================================
	// VIEW Implementation
	// ===============================================================

	public void attachView() {
		// clearView();
		kernel.notifyAddAll(this);
		kernel.attach(this);
	}

	public void detachView() {
		kernel.detach(this);
		// clearView();
		// kernel.notifyRemoveAll(this);
	}

	public void add(GeoElement geo) {
		// Application.debug(new Date() + " ADD: " + geo);

		update(geo);
		GPoint location = geo.getSpreadsheetCoords();

		// autoscroll to new cell's location
		if (scrollToShow && location != null)
			table.scrollRectToVisible(table.getCellRect(location.y, location.x,
					true));

	}

	public void remove(GeoElement geo) {
		// Application.debug(new Date() + " REMOVE: " + geo);

		if (app.getTraceManager().isTraceGeo(geo)) {
			app.getTraceManager().removeSpreadsheetTraceGeo(geo);
			if (isTraceDialogVisible())
				traceDialog.updateTraceDialog();
		}

		GPoint location = geo.getSpreadsheetCoords();

		switch (geo.getGeoClassType()) {
		case BOOLEAN:
		case BUTTON:
		case LIST:
			table.oneClickEditMap.remove(geo);
		}
	}

	public void rename(GeoElement geo) {

		/*
		 * if(app.getTraceManager().isTraceGeo(geo))
		 * app.getTraceManager().updateTraceSettings(geo);
		 */

		if (isTraceDialogVisible()) {
			traceDialog.updateTraceDialog();
		}

	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// ignore
	}

	public void repaintView() {
		btnTraceDialog.setVisible(app.hasGeoTraced());
		spreadsheetWrapper.repaint();
	}

	public void clearView() {

		// restore defaults;
		app.getSettings().restoreDefaultSpreadsheetSettings();
		setDefaultSelection();
		table.oneClickEditMap.clear();
		tableModel.clearView();

	}

	/** Respond to changes in mode sent by GUI manager */
	public void setMode(int mode, ModeSetter m) {

		this.mode = mode;

		if (isTraceDialogVisible()) {
			traceDialog.toolbarModeChanged(mode);
		}

		// String command = kernel.getModeText(mode); // e.g. "Derivative"

		toolbarManager.handleModeChange(mode);

	}

	/**
	 * Clear table and set to default layout. This method is called on startup
	 * or when new window is called
	 */
	public void restart() {

		clearView();
		tableModel.clearView();
		updateColumnWidths();
		updateFonts();

		app.getTraceManager().loadTraceGeoCollection();

		table.oneClickEditMap.clear();

		// clear the formats and call settingsChanged
		settings().setCellFormat(null);

	}

	/** Resets spreadsheet after undo/redo call. */
	public void reset() {
		if (app.getTraceManager() != null)
			app.getTraceManager().loadTraceGeoCollection();
	}

	public void update(GeoElement geo) {
		GPoint location = geo.getSpreadsheetCoords();
		if (location != null
				&& location.x < Kernel.MAX_SPREADSHEET_COLUMNS_VISIBLE
				&& location.y < Kernel.MAX_SPREADSHEET_ROWS_VISIBLE) {

			// TODO: rowHeader and column
			// changes should be handled by a table model listener

			if (location.y >= tableModel.getRowCount()) {
				// tableModel.setRowCount(location.y + 1);
				spreadsheet.getRowHeader().revalidate();
			}
			if (location.x >= tableModel.getColumnCount()) {
				tableModel.setColumnCount(location.x + 1);
				JViewport cH = spreadsheet.getColumnHeader();

				// bugfix: double-click to load ggb file gives cH = null
				if (cH != null)
					cH.revalidate();
			}

			// Mark this cell to be resized by height
			table.cellResizeHeightSet.add(new GPoint(location.x, location.y));

			// put geos with special editors in the oneClickEditMap
			if (geo.isGeoBoolean() || geo.isGeoButton() || geo.isGeoList()) {
				table.oneClickEditMap.put(location, geo);
			}
		}
	}

	final public void updateVisualStyle(GeoElement geo) {
		update(geo);
	}

	public boolean isShowing() {
		return spreadsheetWrapper.isShowing();
	}

	private boolean scrollToShow = false;

	public void setScrollToShow(boolean scrollToShow) {
		this.scrollToShow = scrollToShow;
	}

	// =====================================================
	// Formula Bar
	// =====================================================

	public FormulaBar getFormulaBar() {
		if (formulaBar == null) {
			// Build the formula bar
			formulaBar = new FormulaBar(app, this);
			formulaBar.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 0,
							SystemColor.controlShadow), BorderFactory
							.createEmptyBorder(4, 4, 4, 4)));
		}
		return formulaBar;
	}

	public void updateFormulaBar() {
		if (formulaBar != null && settings().showFormulaBar())
			formulaBar.update();
	}

	// =====================================================
	// Tracing
	// =====================================================

	public void showTraceDialog(GeoElement geo, CellRange traceCell) {
		if (traceDialog == null) {
			traceDialog = new TraceDialog(app, geo, traceCell);
		} else {
			traceDialog.setTraceDialogSelection(geo, traceCell);
		}
		traceDialog.setVisible(true);
	}

	public boolean isTraceDialogVisible() {
		return (traceDialog != null && traceDialog.isVisible());
	}

	public CellRange getTraceSelectionRange(int anchorColumn, int anchorRow) {
		if (traceDialog == null) {
			return null;
		}
		return traceDialog.getTraceSelectionRange(anchorColumn, anchorRow);
	}

	public void setTraceDialogMode(boolean enableMode) {
		if (enableMode) {
			table.setSelectionRectangleColor(Color.GRAY);
			// table.setFocusable(false);
		} else {
			table.setSelectionRectangleColor(MyTableD.SELECTED_RECTANGLE_COLOR);
			// table.setFocusable(true);
		}
	}

	// ===============================================================
	// XML
	// ===============================================================

	// ===============================================================
	// Update
	// ===============================================================

	public void setLabels() {
		if (traceDialog != null)
			traceDialog.setLabels();

		if (table != null)
			table.setLabels();
		if (formulaBar != null) {
			formulaBar.setLabels();
		}
		btnTraceDialog.setToolTipText(app.getLocalization().getMenuTooltip(
				"TraceToSpreadsheet"));
	}

	public void updateFonts() {

		Font font = app.getPlainFont();

		MyTextField dummy = new MyTextField(app);
		dummy.setFont(font);
		dummy.setText("9999"); // for row header width
		int h = dummy.getPreferredSize().height;
		int w = dummy.getPreferredSize().width;
		rowHeader.setFixedCellWidth(w);

		// TODO: column widths are not set from here
		// need to revise updateColumnWidths() to do this correctly
		dummy.setText("MMMMMMMMMM"); // for column width
		h = dummy.getPreferredSize().height;
		w = dummy.getPreferredSize().width;
		table.setRowHeight(h);
		table.setPreferredColumnWidth(w);
		table.headerRenderer.setPreferredSize(new Dimension(w, h));

		table.setFont(app.getPlainFont());

		table.headerRenderer.setFont(font);

		// Adjust row heights for tall LaTeX images
		table.fitAll(true, false);

		if (formulaBar != null)
			formulaBar.updateFonts(font);

		if (styleBar != null) {
			styleBar.reinit();
		}
	}

	public void setColumnWidth(int col, int width) {
		// Application.debug("col = "+col+" width = "+width);
		TableColumn column = table.getColumnModel().getColumn(col);
		column.setPreferredWidth(width);
		// column.
	}

	public void setRowHeight(int row, int height) {
		table.setRowHeight(row, height);
	}

	public void updateColumnWidths() {
		Font font = app.getPlainFont();

		int size = font.getSize();
		if (size < 12) {
			size = 12; // minimum size
		}
		double multiplier = (size) / 12.0;
		table.setPreferredColumnWidth((int) (SpreadsheetSettings.TABLE_CELL_WIDTH * multiplier));
		for (int i = 0; i < table.getColumnCount(); ++i) {
			table.getColumnModel().getColumn(i)
					.setPreferredWidth(table.preferredColumnWidth());
		}

	}

	public void setColumnWidthsFromSettings() {

		table.setPreferredColumnWidth(settings().preferredColumnWidth());
		HashMap<Integer, Integer> widthMap = settings().getWidthMap();
		for (int i = 0; i < table.getColumnCount(); ++i) {
			if (widthMap.containsKey(i)) {
				table.getColumnModel().getColumn(i)
						.setPreferredWidth(widthMap.get(i));
			} else {
				table.getColumnModel().getColumn(i)
						.setPreferredWidth(table.preferredColumnWidth());
			}
		}
	}

	public void setRowHeightsFromSettings() {
		HashMap<Integer, Integer> heightMap = app.getSettings()
				.getSpreadsheet().getHeightMap();
		table.setRowHeight(app.getSettings().getSpreadsheet()
				.preferredRowHeight());
		if (!heightMap.isEmpty()) {
			for (Integer r : heightMap.keySet()) {
				table.setRowHeight(r, heightMap.get(r));
			}
		}
	}

	public void updateRowHeader() {
		if (rowHeader != null) {
			rowHeader.updateRowHeader();
		}
	}

	public void setSpreadsheetScrollPosition(int hScroll, int vScroll) {
		spreadsheet.getHorizontalScrollBar().setValue(hScroll);
		spreadsheet.getVerticalScrollBar().setValue(vScroll);

		settings().setHScrollBalValue(hScroll);
		settings().setVScrollBalValue(vScroll);
	}

	// ==========================================================
	// Handle spreadsheet resize.
	//
	// Adds extra rows and columns to fill the enclosing scrollpane.
	// This is sometimes needed when rows or columns are resized
	// or the application window is enlarged.

	/**
	 * Tests if the spreadsheet fits the enclosing scrollpane viewport. Adds
	 * rows or columns if needed to fill the viewport.
	 */
	public void expandSpreadsheetToViewport() {

		if (table.getWidth() < spreadsheet.getWidth()) {

			int newColumns = (spreadsheet.getWidth() - table.getWidth())
					/ table.preferredColumnWidth();
			table.removeComponentListener(this);
			tableModel.setColumnCount(table.getColumnCount() + newColumns);
			table.addComponentListener(this);

		}
		if (table.getHeight() < spreadsheet.getHeight()) {
			int newRows = (spreadsheet.getHeight() - table.getHeight())
					/ table.getRowHeight();
			table.removeComponentListener(this);
			tableModel.setRowCount(table.getRowCount() + newRows);
			table.addComponentListener(this);

		}

		// if table has grown after resizing all rows or columns, then select
		// all again
		// TODO --- why doesn't this work:
		/*
		 * if(table.isSelectAll()){ table.selectAll(); }
		 */

	}

	// Listener for a resized column or row

	public void componentResized(ComponentEvent e) {
		expandSpreadsheetToViewport();
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	// ===============================================================
	// Data Import
	// ===============================================================

	public boolean loadSpreadsheetFromURL(File f) {

		boolean succ = false;

		URL url = null;
		try {
			url = f.toURI().toURL();
			succ = loadSpreadsheetFromURL(url);
		}

		catch (IOException ex) {
			ex.printStackTrace();
		}

		return succ;
	}

	public boolean loadSpreadsheetFromURL(URL url) {

		boolean succ = table.copyPasteCut.pasteFromURL(url);
		if (succ) {
			app.storeUndoInfo();
		}
		return succ;
	}

	// ================================================
	// Spreadsheet Settings
	// ================================================

	public void setEnableAutoComplete(boolean enableAutoComplete) {
		table.setEnableAutoComplete(enableAutoComplete);
	}

	public void setShowRowHeader(boolean showRowHeader) {
		if (showRowHeader) {
			spreadsheet.setRowHeaderView(rowHeader);
		} else {
			spreadsheet.setRowHeaderView(null);
		}
	}

	public void setShowColumnHeader(boolean showColumnHeader) {
		if (showColumnHeader) {
			table.setTableHeader(tableHeader);
			spreadsheet.setColumnHeaderView(tableHeader);
		} else {
			table.setTableHeader(null);
			spreadsheet.setColumnHeaderView(null);
		}
	}

	public void setShowVScrollBar(boolean showVScrollBar) {
		if (showVScrollBar) {
			spreadsheet
					.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		} else {
			spreadsheet
					.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		}
	}

	public void setShowHScrollBar(boolean showHScrollBar) {
		if (showHScrollBar) {
			spreadsheet
					.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		} else {
			spreadsheet
					.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		}
	}

	public void setShowGrid(boolean showGrid) {
		table.setShowGrid(showGrid);
		if (showGrid)
			table.setIntercellSpacing(new Dimension(1, 1));
		else
			table.setIntercellSpacing(new Dimension(0, 0));
		getSpreadsheetStyleBar().updateStyleBar();
	}

	public boolean getAllowToolTips() {
		return settings().allowToolTips();
	}

	public void setAllowToolTips(boolean allowToolTips) {
		// do nothing yet
	}

	public void setShowFormulaBar(boolean showFormulaBar) {
		if (showFormulaBar)
			spreadsheetPanel.add(getFormulaBar(), BorderLayout.NORTH);
		else if (formulaBar != null)
			spreadsheetPanel.remove(formulaBar);

		if (formulaBar != null)
			formulaBar.update();
		spreadsheetWrapper.revalidate();
		spreadsheetWrapper.repaint();
		getSpreadsheetStyleBar().updateStyleBar();
	}

	public boolean getShowFormulaBar() {
		return settings().showFormulaBar();
	}

	public boolean isVisibleStyleBar() {
		return styleBar == null || styleBar.isVisible();
	}

	public void setColumnSelect(boolean isColumnSelect) {
		// do nothing yet
	}

	public boolean isColumnSelect() {
		return settings().isColumnSelect();
	}

	public void setAllowSpecialEditor(boolean allowSpecialEditor) {
		spreadsheetWrapper.repaint();
	}

	public boolean allowSpecialEditor() {
		return settings().allowSpecialEditor();
	}

	/**
	 * sets requirement that commands entered into cells must start with "="
	 */
	public void setEqualsRequired(boolean isEqualsRequired) {
		table.setEqualsRequired(isEqualsRequired);
	}

	/**
	 * gets requirement that commands entered into cells must start with "="
	 */
	public boolean isEqualsRequired() {
		return settings().equalsRequired();
	}

	boolean allowSettingUpate = true;

	public void updateCellFormat(String cellFormat) {
		if (!allowSettingUpate)
			return;

		settings().removeListener(this);
		settings().setCellFormat(cellFormat);
		settings().addListener(this);
	}

	protected void updateAllRowSettings() {
		if (!allowSettingUpate)
			return;

		settings().removeListener(this);
		settings().setPreferredRowHeight(table.getRowHeight());
		settings().getHeightMap().clear();
		for (int row = 0; row < table.getRowCount(); row++) {
			int rowHeight = table.getRowHeight(row);
			if (rowHeight != table.getRowHeight())
				settings().getHeightMap().put(row, rowHeight);
		}
		settings().addListener(this);
	}

	protected void updateRowHeightSetting(int row, int height) {
		if (!allowSettingUpate)
			return;

		settings().removeListener(this);
		settings().getHeightMap().put(row, height);
		settings().addListener(this);
	}

	protected void updatePreferredRowHeight(int preferredRowHeight) {
		if (!allowSettingUpate)
			return;

		settings().removeListener(this);
		settings().getHeightMap().clear();
		settings().setPreferredRowHeight(preferredRowHeight);
		settings().addListener(this);
	}

	protected void updateColumnWidth(int col, int colWidth) {
		if (!allowSettingUpate)
			return;

		settings().removeListener(this);
		settings().getWidthMap().put(col, colWidth);
		settings().addListener(this);
	}

	protected void updatePreferredColumnWidth(int colWidth) {
		if (!allowSettingUpate)
			return;

		settings().removeListener(this);
		settings().getWidthMap().clear();
		settings().setPreferredColumnWidth(table.preferredColumnWidth);
		settings().addListener(this);
	}

	protected void updateAllColumnWidthSettings() {
		if (!allowSettingUpate)
			return;

		settings().removeListener(this);
		settings().setPreferredColumnWidth(table.preferredColumnWidth);
		settings().getWidthMap().clear();
		for (int col = 0; col < table.getColumnCount(); col++) {
			TableColumn column = table.getColumnModel().getColumn(col);
			int colWidth = column.getWidth();
			if (colWidth != table.preferredColumnWidth)
				settings().getWidthMap().put(col, colWidth);
		}
		settings().addListener(this);
	}

	protected SpreadsheetSettings settings() {
		return app.getSettings().getSpreadsheet();
	}

	public void settingsChanged(AbstractSettings settings0) {

		allowSettingUpate = false;

		// layout
		setShowColumnHeader(settings().showColumnHeader());
		setShowRowHeader(settings().showRowHeader());
		setShowVScrollBar(settings().showVScrollBar());
		setShowHScrollBar(settings().showHScrollBar());
		setShowGrid(settings().showGrid());
		setAllowToolTips(settings().allowToolTips());
		setShowFormulaBar(settings().showFormulaBar());
		setColumnSelect(settings().isColumnSelect());
		setAllowSpecialEditor(settings().allowSpecialEditor());
		setEqualsRequired(settings().equalsRequired());
		setEnableAutoComplete(settings().isEnableAutoComplete());

		// row height and column widths
		setColumnWidthsFromSettings();
		setRowHeightsFromSettings();

		// cell format
		getSpreadsheetTable().getCellFormatHandler().processXMLString(
				settings().cellFormat());

		// preferredSize
		spreadsheetWrapper.setPreferredSize(geogebra.awt.GDimensionD
				.getAWTDimension(settings().preferredSize()));

		// initial position
		// TODO not working yet ...
		// setSpreadsheetScrollPosition(settings.scrollPosition().x,
		// settings.scrollPosition().y);
		// getTable().setInitialCellSelection(settings.selectedCell().x,
		// settings.selectedCell().y);

		allowSettingUpate = true;

	}

	// ================================================
	// Focus
	// ================================================

	protected boolean hasViewFocus() {
		boolean hasFocus = false;
		try {
			if (((LayoutD) app.getGuiManager().getLayout()).getDockManager()
					.getFocusedPanel() != null)
				hasFocus = ((LayoutD) app.getGuiManager().getLayout())
						.getDockManager().getFocusedPanel()
						.isAncestorOf(spreadsheetWrapper);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hasFocus;
	}

	// transfer focus to the table
	public void requestFocus() {
		if (table != null)
			table.requestFocus();
	}

	// test all components of SpreadsheetView for hasFocus
	@Override
	public boolean hasFocus() {
		if (table == null)
			return false;
		return table.hasFocus()
				|| rowHeader.hasFocus()
				|| (table.getTableHeader() != null && table.getTableHeader()
						.hasFocus())
				|| spreadsheet.getCorner(ScrollPaneConstants.UPPER_LEFT_CORNER)
						.hasFocus()
				|| (formulaBar != null && formulaBar.hasFocus());
	}

	public void focusGained(FocusEvent arg0) {

	}

	public void focusLost(FocusEvent arg0) {
		getSpreadsheetTable().repaint();

	}

	public int getViewID() {
		return App.VIEW_SPREADSHEET;
	}

	public int[] getGridColwidths() {
		int[] colWidths = new int[2 + tableModel.getHighestUsedColumn()];
		colWidths[0] = rowHeader.getWidth();
		for (int c = 0; c <= tableModel.getHighestUsedColumn(); c++) {
			colWidths[c + 1] = table.getColumnModel().getColumn(c).getWidth();
		}
		return colWidths;
	}

	public int[] getGridRowHeights() {
		int[] rowHeights = new int[2 + tableModel.getHighestUsedRow()];

		if (table.getTableHeader() == null)
			rowHeights[0] = 0;
		else
			rowHeights[0] = table.getTableHeader().getHeight();

		for (int r = 0; r <= tableModel.getHighestUsedRow(); r++) {
			rowHeights[r + 1] = table.getRowHeight(r);
		}
		return rowHeights;
	}

	public Component[][] getPrintComponents() {
		return new Component[][] {
				{ spreadsheet.getCorner(ScrollPaneConstants.UPPER_LEFT_CORNER),
						spreadsheet.getColumnHeader() },
				{ spreadsheet.getRowHeader(), table } };
	}

	public void startBatchUpdate() {
		// TODO Auto-generated method stub

	}

	public void endBatchUpdate() {
		// TODO Auto-generated method stub

	}

	public JComponent getViewContainer() {
		return spreadsheetWrapper;
	}

	public boolean suggestRepaint() {
		return false;
		// only for web
	}
}