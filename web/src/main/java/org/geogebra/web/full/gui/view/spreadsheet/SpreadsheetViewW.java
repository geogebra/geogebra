package org.geogebra.web.full.gui.view.spreadsheet;

import java.util.HashMap;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.spreadsheet.CellRange;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SpreadsheetTableModelSimple;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.util.AdvancedFocusPanel;
import org.geogebra.web.full.gui.util.AdvancedFocusPanelI;
import org.geogebra.web.html5.awt.PrintableW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.TimerSystemW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class SpreadsheetViewW implements SpreadsheetViewInterface,
		SettingListener, SetLabels, PrintableW {

	// ggb fields
	protected AppW app;
	private Kernel kernel;

	// spreadsheet table and row header
	MyTableW table;
	protected SpreadsheetTableModelSimple tableModel;

	public static final int ROW_HEADER_WIDTH = 35; // wide enough for "9999"

	// TODO: should traceDialog belong to the SpreadsheetTraceManager?
	// private TraceDialog traceDialog;

	protected AdvancedFocusPanelI spreadsheetWrapper;
	private SpreadsheetStyleBarW styleBar;

	// toolbar manager
	SpreadsheetToolbarManagerW toolbarManager;

	// current toolbar mode
	private int mode = -1;

	boolean repaintScheduled = false; // to repaint less often, make it
										// quicker

	// panel that contains the spreadsheet table and headers
	private AbsolutePanel spreadsheet;
	private boolean allowSettingUpdate = true;

	GPoint scrollPos = new GPoint();

	// false, if user close keyboard with X button, in this case, keyboard won't
	// appear if a user click in a cell later, he should open it with keyboard
	// button
	private boolean keyboardEnabled = true;
	private boolean scrollToShow = false;
	private int waitForRepaint = TimerSystemW.SLEEPING_FLAG;
	private SpreadsheetKeyListenerW sskl;

	/******************************************************
	 * Construct spreadsheet view as a split panel. Left panel holds file tree
	 * browser, right panel holds spreadsheet.
	 */
	public SpreadsheetViewW(AppW app) {
		super();

		this.app = app;
		kernel = app.getKernel();

		// Initialize settings and register listener
		app.getSettings().getSpreadsheet().addListener(this);

		createGUI();

		updateFonts();
		attachView();

		// Create tool bar manager to handle tool bar mode changes
		toolbarManager = new SpreadsheetToolbarManagerW(app, this);

		settingsChanged(settings());

		this.spreadsheet.addBitlessDomHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				if (event.getTouches().length() > 1) {
					Touch t0 = event.getTouches().get(0);
					Touch t1 = event.getTouches().get(1);
					scrollPos.setLocation(
							getHorizontalScrollPosition()
									+ (t0.getScreenX() + t1.getScreenX()) / 2,
							getVerticalScrollPosition()
									+ (t0.getScreenY() + t1.getScreenY()) / 2);
				}
			}
		}, TouchStartEvent.getType());

		this.spreadsheet.addBitlessDomHandler(new TouchMoveHandler() {
			@Override
			public void onTouchMove(TouchMoveEvent event) {
				if (event.getTouches().length() > 1) {
					Touch t0 = event.getTouches().get(0);
					Touch t1 = event.getTouches().get(1);

					int x = (t0.getScreenX() + t1.getScreenX()) / 2;
					int y = (t0.getScreenY() + t1.getScreenY()) / 2;

					table.setHorizontalScrollPosition(scrollPos.x - x);
					table.setVerticalScrollPosition(scrollPos.y - y);
				}
			}
		}, TouchMoveEvent.getType());
	}

	private void createGUI() {

		// Build the spreadsheet table and enclosing scrollpane
		buildSpreadsheet();

		spreadsheetWrapper = new AdvancedFocusPanel(spreadsheet);
		sskl = new SpreadsheetKeyListenerW(app, table);
		spreadsheetWrapper.addDomHandler(sskl, KeyDownEvent.getType());
		spreadsheetWrapper.addDomHandler(sskl, KeyPressEvent.getType());
		spreadsheetWrapper.addDomHandler(sskl, KeyUpEvent.getType());
		spreadsheetWrapper.addPasteHandler(sskl);
	}

	private void buildSpreadsheet() {

		// Create the spreadsheet table model and the table
		tableModel = (SpreadsheetTableModelSimple) app.getSpreadsheetTableModel();
		table = new MyTableW(this, tableModel);

		spreadsheet = new AbsolutePanel();
		spreadsheet.add(table.getContainer());

	}

	// ===============================================================
	// Defaults
	// ===============================================================

	/**
	 * Reset selection to default.
	 */
	public void setDefaultSelection() {
		setSpreadsheetScrollPosition(0, 0);
		table.setInitialCellSelection(0, 0);
	}

	// ===============================================================
	// getters/setters
	// ===============================================================

	@Override
	public AppW getApplication() {
		return app;
	}

	@Override
	public MyTableW getSpreadsheetTable() {
		return table;
	}

	@Override
	public void rowHeaderRevalidate() {
		// TODO//spreadsheet.getRowHeader().revalidate();
	}

	@Override
	public void columnHeaderRevalidate() {
		// TODO//spreadsheet.getColumnHeader().revalidate();
	}

	@Override
	public int getMode() {
		return mode;
	}

	/**
	 * @return spreadsheet styleBar
	 */
	public SpreadsheetStyleBarW getSpreadsheetStyleBar() {
		if (styleBar == null) {
			styleBar = new SpreadsheetStyleBarW(this);
		}
		return styleBar;
	}

	// ===============================================================
	// VIEW Implementation
	// ===============================================================

	/**
	 * Attach to kernel & add all.
	 */
	public void attachView() {
		kernel.notifyAddAll(this);
		kernel.attach(this);
	}

	/**
	 * Detach from kernel.
	 */
	public void detachView() {
		kernel.detach(this);
	}

	@Override
	public void add(GeoElement geo) {
		update(geo);
		scrollIfNeeded(geo, null);
	}

	@Override
	public void scrollIfNeeded(GeoElement geo, String labelNew) {
		GPoint location = geo.getSpreadsheetCoords();

		if (labelNew != null && location == null) {
			location = GeoElementSpreadsheet.spreadsheetIndices(labelNew);
		}

		if (location == null || location.x == -1 && location.y == -1) {
			return;
		}

		// autoscroll to new cell's location
		if (scrollToShow) {
			table.scrollRectToVisible(
					table.getCellRect(location.y, location.x, true));
		}
	}

	@Override
	public void remove(GeoElement geo) {
		if (app.getTraceManager().isTraceGeo(geo)) {
			app.getTraceManager().removeSpreadsheetTraceGeo(geo);
			// TODO if (isTraceDialogVisible())
			// TODO traceDialog.updateTraceDialog();
		}

		GPoint location = geo.getSpreadsheetCoords();

		switch (geo.getGeoClassType()) {
		default:
			// do nothing
			break;
		case BOOLEAN:
		case BUTTON:
		case LIST:
			table.getOneClickEditMap().remove(location);
		}

		if (location != null) {
			table.updateCellFormat(location.y, location.x);
			table.syncRowHeaderHeight(location.y);
		}

		// update the rowHeader height in case an oversized element has been
		// removed and the table row has resized itself
		table.renderSelectionDeferred();
	}

	@Override
	public void rename(GeoElement geo) {
		// TODO
	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {
		// ignore
	}

	@Override
	public void repaintView() {
		if (waitForRepaint == TimerSystemW.SLEEPING_FLAG) {
			getApplication().ensureTimerRunning();
			waitForRepaint = TimerSystemW.SPREADSHEET_LOOPS;
		}
	}

	@Override
	public void clearView() {
		// restore defaults;
		app.getSettings().restoreDefaultSpreadsheetSettings();
		setDefaultSelection();
		table.getOneClickEditMap().clear();
		tableModel.clearView();
	}

	/** Respond to changes in mode sent by GUI manager */
	@Override
	public void setMode(int mode, ModeSetter m) {
		if (m != ModeSetter.TOOLBAR) {
			return;
		}

		this.mode = mode;
		toolbarManager.handleModeChange(mode);
	}

	/**
	 * Clear table and set to default layout. This method is called on startup
	 * or when new window is called
	 */
	public void restart() {

		clearView();
		tableModel.clearView();
		// TODO//updateColumnWidths();
		updateFonts();

		app.getTraceManager().loadTraceGeoCollection();

		table.getOneClickEditMap().clear();

		// clear the formats and call settingsChanged
		settings().setCellFormat(null);
	}

	/** Resets spreadsheet after undo/redo call. */
	@Override
	public void reset() {
		if (app.getTraceManager() != null) {
			app.getTraceManager().loadTraceGeoCollection();
		}
	}

	@Override
	public void update(GeoElement geo) {

		// table.setRepaintAll();
		GPoint location = geo.getSpreadsheetCoords();
		if (location != null
				&& location.x < app.getMaxSpreadsheetColumnsVisible()
				&& location.y < app.getMaxSpreadsheetRowsVisible()) {

			// TODO: rowHeader and column
			// changes should be handled by a table model listener

			if (location.y >= tableModel.getRowCount()) {
				tableModel.setRowCount(location.y + 1);
				// TODO//spreadsheet.getRowHeader().revalidate();
			}
			if (location.x >= tableModel.getColumnCount()) {
				tableModel.setColumnCount(location.x + 1);
				// TODO//JViewport cH = spreadsheet.getColumnHeader();

				// bugfix: double-click to load ggb file gives cH = null
				// TODO//if (cH != null)
				// TODO cH.revalidate();
			}

			// Mark this cell to be resized by height
			table.addResizeHeight(new GPoint(location.x, location.y));

			// put geos with special editors in the oneClickEditMap
			if (geo.isGeoBoolean() || geo.isGeoButton() || geo.isGeoList()) {
				table.getOneClickEditMap().put(location, geo);
			}

			// Update the cell format, it may change with this geo's properties
			table.updateCellFormat(location.y, location.x);
		}
	}

	@Override
	final public void updateVisualStyle(GeoElement geo, GProperty prop) {
		update(geo);
	}

	@Override
	public void updateHighlight(GeoElementND geo) {
		// nothing to do here
	}

	@Override
	public void updatePreviewFromInputBar(GeoElement[] geos) {
		// TODO
	}

	public void setScrollToShow(boolean scrollToShow) {
		this.scrollToShow = scrollToShow;
	}

	// =====================================================
	// Tracing
	// =====================================================

	@Override
	public void showTraceDialog(GeoElement geo, CellRange traceCell) {
		// not implemented yet
	}

	/**
	 * @param enableMode
	 *            whether trace dialog is active
	 */
	public void setTraceDialogMode(boolean enableMode) {
		if (enableMode) {
			table.setSelectionRectangleColor(GColor.GRAY);
			// table.setFocusable(false);
		} else {
			table.setSelectionRectangleColor(MyTableW.SELECTED_RECTANGLE_COLOR);
			// table.setFocusable(true);
		}
	}

	// ===============================================================
	// XML
	// ===============================================================

	/**
	 * returns settings in XML format
	 */
	public void getXML(StringBuilder sb, boolean asPreference) {
		sb.append("<spreadsheetView>\n");

		int width = spreadsheetWrapper.getOffsetWidth(); // getPreferredSize().width;
		int height = spreadsheetWrapper.getOffsetHeight(); // getPreferredSize().height;

		sb.append("\t<size ");
		sb.append(" width=\"");
		sb.append(width);
		sb.append("\"");
		sb.append(" height=\"");
		sb.append(height);
		sb.append("\"");
		sb.append("/>\n");

		sb.append("\t<prefCellSize ");
		sb.append(" width=\"");
		sb.append(table.preferredColumnWidth());
		sb.append("\"");
		sb.append(" height=\"");
		sb.append(table.minimumRowHeight
		// FIXME: temporarily, otherwise:
		// table.getRowHeight()
		);
		sb.append("\"");
		sb.append("/>\n");

		if (!asPreference) {

			settings().getWidthsAndHeightsXML(sb);

			// initial selection
			sb.append("\t<selection ");

			sb.append(" hScroll=\"");
			sb.append(0
			// FIXME: might not be the same for Desktop and Web
			// getHorizontalScrollPosition()
			// spreadsheet.getHorizontalScrollBar().getValue()
			);
			sb.append("\"");

			sb.append(" vScroll=\"");
			sb.append(0
			// FIXME: might not be the same for Desktop and Web
			// getVerticalScrollPosition()
			// spreadsheet.getVerticalScrollBar().getValue()
			);
			sb.append("\"");

			sb.append(" column=\"");
			sb.append(table.anchorSelectionColumn
			// getColumnModel().getSelectionModel().getAnchorSelectionIndex()
			);
			sb.append("\"");

			sb.append(" row=\"");
			sb.append(table.anchorSelectionRow
			// table.getSelectionModel().getAnchorSelectionIndex()
			);
			sb.append("\"");

			sb.append("/>\n");
		}

		settings().getLayoutXML(sb);

		// cell formats
		if (!asPreference) {
			table.getCellFormatHandler().getXML(sb);
		}

		sb.append("</spreadsheetView>\n");
	}

	// ===============================================================
	// Update
	// ===============================================================

	public void updateFonts() {
		table.updateFonts();
	}

	public void setRowHeight(int row, int height) {
		table.setRowHeight(row, height);
	}

	/**
	 * Update column widths from settings.
	 */
	public void setColumnWidthsFromSettings() {

		int prefWidth = table.preferredColumnWidth();
		HashMap<Integer, Integer> widthMap = settings().getWidthMap();

		for (int col = 0; col < table.getColumnCount(); ++col) {
			if (widthMap.containsKey(col)) {
				table.setColumnWidthSilent(col, widthMap.get(col));
			} else {
				table.setColumnWidthSilent(col, prefWidth);
			}
		}
	}

	/**
	 * Update row heights from settings.
	 */
	public void setRowHeightsFromSettings() {
		// first set all row heights the same
		int prefHeight = Math.max(settings().preferredRowHeight(),
				(int) (app.getFontSizeWeb() * 1.5));
		table.setRowHeight(prefHeight, false);

		// now set custom row heights
		HashMap<Integer, Integer> heightMap = settings().getHeightMap();
		Log.debug("height map size: " + heightMap.size());
		for (int row = 0; row < table.getRowCount(); ++row) {
			if (heightMap.containsKey(row)) {
				table.setRowHeight(row, heightMap.get(row), false);
			}
		}
	}

	/**
	 * @param hScroll
	 *            horizontal scroll
	 * @param vScroll
	 *            vertical scroll
	 */
	public void setSpreadsheetScrollPosition(int hScroll, int vScroll) {
		table.setHorizontalScrollPosition(hScroll);
		table.setVerticalScrollPosition(vScroll);

		settings().setHScrollBalValue(hScroll);
		settings().setVScrollBalValue(vScroll);
	}

	// ===============================================================
	// Data Import & File Browser
	// ===============================================================

	// ================================================
	// Spreadsheet Settings
	// ================================================

	public void setEnableAutoComplete(boolean enableAutoComplete) {
		table.setEnableAutoComplete(enableAutoComplete);
	}

	public void setShowRowHeader(boolean showRowHeader) {
		table.setShowRowHeader(showRowHeader);
	}

	public void setShowColumnHeader(boolean showColumnHeader) {
		table.setShowColumnHeader(showColumnHeader);
	}

	public void setShowVScrollBar(boolean showVScrollBar) {
		table.setShowVScrollBar(showVScrollBar);
	}

	public void setShowHScrollBar(boolean showHScrollBar) {
		table.setShowHScrollBar(showHScrollBar);
	}

	/**
	 * Update CSS to show/hide keyboard, update stylebar.
	 * 
	 * @param showGrid
	 *            whether to show grid
	 */
	public void setShowGrid(boolean showGrid) {
		if (showGrid) {
			table.getGrid().getElement().removeClassName("off");
		} else {
			table.getGrid().getElement().addClassName("off");
		}
		if (this.isVisibleStyleBar()) {
			getSpreadsheetStyleBar().updateStyleBar();
		}
	}

	public boolean getAllowToolTips() {
		return settings().allowToolTips();
	}

	public boolean getShowFormulaBar() {
		return settings().showFormulaBar();
	}

	/**
	 * @return whether stylebar is visible
	 */
	public boolean isVisibleStyleBar() {
		return styleBar == null || styleBar.isVisible();
	}

	public boolean isColumnSelect() {
		return settings().isColumnSelect();
	}

	public void updateAllowSpecialEditor() {
		repaintView();
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
	 * @return requirement that commands entered into cells must start with "="
	 */
	public boolean isEqualsRequired() {
		return settings().equalsRequired();
	}

	@Override
	public void updateCellFormat(String cellFormat) {
		if (!allowSettingUpdate) {
			return;
		}

		settings().removeListener(this);
		settings().setCellFormat(cellFormat);
		settings().addListener(this);
	}

	protected void updateRowHeightSetting(int row, int height) {
		if (!allowSettingUpdate) {
			return;
		}

		settings().removeListener(this);
		settings().getHeightMap().put(row, height);
		settings().addListener(this);
	}

	protected void updatePreferredRowHeight(int preferredRowHeight) {
		if (!allowSettingUpdate) {
			return;
		}

		settings().removeListener(this);
		settings().getHeightMap().clear();
		settings().setPreferredRowHeight(preferredRowHeight);
		settings().addListener(this);
	}

	protected void updateColumnWidth(int col, int colWidth) {
		if (!allowSettingUpdate) {
			return;
		}

		settings().removeListener(this);
		settings().getWidthMap().put(col, colWidth);
		settings().addListener(this);
	}

	protected void updatePreferredColumnWidth() {
		if (!allowSettingUpdate) {
			return;
		}

		settings().removeListener(this);
		settings().getWidthMap().clear();
		settings().setPreferredColumnWidth(table.preferredColumnWidth());
		settings().addListener(this);
	}

	protected SpreadsheetSettings settings() {
		return app.getSettings().getSpreadsheet();
	}

	@Override
	public void settingsChanged(AbstractSettings settings0) {
		Scheduler.get().scheduleDeferred(deferredSettingsChanged);
	}

	Scheduler.ScheduledCommand deferredSettingsChanged = new Scheduler.ScheduledCommand() {
		@Override
		public void execute() {
			settingsChangedCommand();
		}
	};

	/**
	 * Update view settings
	 */
	public void settingsChangedCommand() {
		allowSettingUpdate = true;

		// layout
		setShowColumnHeader(settings().showColumnHeader());
		setShowRowHeader(settings().showRowHeader());
		setShowVScrollBar(settings().showVScrollBar());
		setShowHScrollBar(settings().showHScrollBar());
		setShowGrid(settings().showGrid());
		updateAllowSpecialEditor();
		setEqualsRequired(settings().equalsRequired());
		setEnableAutoComplete(settings().isEnableAutoComplete());

		// row height and column widths
		setRowHeightsFromSettings();
		setColumnWidthsFromSettings();

		// cell format
		getSpreadsheetTable().getCellFormatHandler()
				.processXMLString(settings().cellFormat());

		// preferredSize
		setPreferredSize(settings().preferredSize().getWidth(),
				settings().preferredSize().getHeight());

		allowSettingUpdate = true;
		if (getFocusPanel().getParent() == null) {
			return;
		}
		int width = getFocusPanel().getParent().getOffsetWidth();
		int height = getFocusPanel().getParent().getOffsetHeight();
		onResize(width, height);
	}

	/**
	 * @param width
	 *            width
	 * @param height
	 *            height
	 */
	public void setPreferredSize(int width, int height) {
		if (width > 0 && height > 0) {
			spreadsheetWrapper.setWidth(width + "px");
			spreadsheetWrapper.setHeight(height + "px");
		}
	}

	// ================================================
	// Focus
	// ================================================

	/**
	 * Focus this view (deferred)
	 */
	public void requestFocus() {
		Log.debug("Spreadsheet requested focus");
		Scheduler.get().scheduleDeferred(requestFocusCommand);
	}

	Scheduler.ScheduledCommand requestFocusCommand = new Scheduler.ScheduledCommand() {
		@Override
		public void execute() {
			spreadsheetWrapper.setFocus(true);
			table.updateCopiableSelection();
		}
	};

	@Override
	public boolean hasFocus() {
		return app.getGuiManager().getLayout().getDockManager()
				.getFocusedViewId() == App.VIEW_SPREADSHEET;
	}

	@Override
	public int getViewID() {
		return App.VIEW_SPREADSHEET;
	}

	@Override
	public boolean isShowing() {
		// if this is attached, we shall make sure its parents are visible too
		return spreadsheetWrapper.isVisible() && spreadsheetWrapper.isAttached()
				&& table != null && table.getGrid().isVisible()
				&& table.getGrid().isAttached();
	}

	protected void onLoad() {
		// this may be important if the view is added/removed from the DOM
		// TODO: is this needed with stand alone spreadsheetView?
		// super.onLoad();
		repaintView();
	}

	/**
	 * This method is called from timers. Only call this method if you really
	 * know what you're doing. Otherwise just call repaint().
	 */
	public void doRepaint() {
		table.repaint();
	}

	/**
	 * timer system suggests a repaint
	 */
	@Override
	public boolean suggestRepaint() {
		if (waitForRepaint == TimerSystemW.SLEEPING_FLAG) {
			return false;
		}

		if (waitForRepaint == TimerSystemW.REPAINT_FLAG) {
			if (isShowing()) {
				doRepaint();
				waitForRepaint = TimerSystemW.SLEEPING_FLAG;
			}
			return true;
		}

		waitForRepaint--;
		return true;
	}

	/**
	 * This method is used from add and remove, to ensure it is executed after
	 * the loop is executed - so in theory, if there is a loop with add methods,
	 * all the add methods come first, and repaint is called only afterwards
	 */
	public void scheduleRepaint() {
		if (!repaintScheduled) {
			repaintScheduled = true;
			Scheduler.get().scheduleDeferred(scheduleRepaintCommand);
		}
	}

	Scheduler.ScheduledCommand scheduleRepaintCommand = new Scheduler.ScheduledCommand() {
		@Override
		public void execute() {
			repaintScheduled = false;
			repaintView();
		}
	};

	public Widget getFocusPanel() {
		return spreadsheetWrapper.asWidget();
	}

	@Override
	public void startBatchUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endBatchUpdate() {
		// TODO Auto-generated method stub

	}

	/**
	 * Resize the table.
	 * 
	 * @param width
	 *            width in px
	 * @param height
	 *            height in px
	 */
	public void onResize(int width, int height) {
		if (width <= 0 || height <= 0) {
			return;
		}
		getFocusPanel().setWidth(width + "px");
		getFocusPanel().setHeight(height + "px");

		if (table != null) {
			table.setSize(width, height);
			table.repaintAll();
			table.syncTableTop();
		}
	}

	public int getHorizontalScrollPosition() {
		return table.getHorizontalScrollPosition();
	}

	public int getVerticalScrollPosition() {
		return table.getVerticalScrollPosition();
	}

	@Override
	public final void setLabels() {
		if (this.styleBar != null) {
			styleBar.setLabels();
		}
	}

	/**
	 * Update objects that depend on pixel ratio (e.g. for LaTex rendered in
	 * cells).
	 * 
	 * @param ratio
	 *            CSS pixel ratio
	 */
	public void setPixelRatio(double ratio) {
		if (this.table != null) {
			table.setPixelRatio(ratio);
		}
	}

	@Override
	public void getPrintable(FlowPanel pPanel, Button btPrint) {
		// pPanel.add(table.);
	}

	public boolean isKeyboardEnabled() {
		return keyboardEnabled;
	}

	@Override
	public void setKeyboardEnabled(boolean b) {
		keyboardEnabled = b;
	}

	public void letterOrDigitTyped() {
		this.sskl.letterOrDigitTyped();
	}
}
