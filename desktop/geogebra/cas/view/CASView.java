package geogebra.cas.view;

import geogebra.common.cas.GeoGebraCAS;
import geogebra.cas.view.CASStyleBar;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.View;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.gui.GuiManager;
import geogebra.gui.inputbar.InputBarHelpPanel;
import geogebra.gui.view.Gridable;
import geogebra.main.Application;

import java.util.ArrayList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Computer algebra view.
 * 
 * @author Markus Hohenwarter, Quan Yuan
 */
public class CASView extends JComponent implements View, Gridable {

	private static final long serialVersionUID = 1L;

	private Kernel kernel;
	
	private CASTable consoleTable;
	private CASInputHandler casInputHandler;
	private CASSubDialog subDialog;
	private ListSelectionModel listSelModel;
	private GeoGebraCAS cas;
	final private Application app;
	final private RowHeader rowHeader;
	private boolean toolbarIsUpdatedByDockPanel;
	/** stylebar */
	CASStyleBar styleBar;

	private CASControlPanel controlPanel;

	/**
	 * Creates new CAS view
	 * @param app application
	 */
	public CASView(Application app) {
		kernel = app.getKernel();
		this.app = app;
		listSelModel = new DefaultListSelectionModel();

		Thread initCAS = new Thread() {
			@Override
			public void run() {
				// init CAS
				getCAS();
			}
		};
		initCAS.start();

		// init commands subtable for cas-commands in inputbar-help
		kernel.getAlgebraProcessor().enableCAS();

		GuiManager gm = app.getGuiManager();
		if (gm != null) {
			((InputBarHelpPanel) gm.getInputHelpPanel()).setCommands();
		}

		// CAS input/output cells
		createCASTable();

		// row header
		rowHeader = new RowHeader(consoleTable, false, listSelModel);
		consoleTable.setSelectionModel(listSelModel);
		// init the scroll panel
		JScrollPane scrollPane = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setRowHeaderView(rowHeader);
		scrollPane.setViewportView(consoleTable);
		scrollPane.setBackground(Color.white);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		// set the lower left corner so that the horizontal scroller looks good
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1,
				geogebra.awt.Color
						.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR)));
		p.setBackground(Color.white);
		scrollPane.setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER, p);

		// put the scrollpanel in
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		
		controlPanel = new CASControlPanel(app,this);
		add(controlPanel.getControlPanel(), BorderLayout.SOUTH);
		
		this.setBackground(Color.white);

		consoleTable.getSelectionModel().addListSelectionListener(selectionListener());

		// listen to clicks below last row in consoleTable: create new row
		scrollPane.addMouseListener(scrollPaneListener());

		// input handler
		casInputHandler = new CASInputHandler(this);

		// addFocusListener(this);
		
		updateFonts();
	}

	public void showCalculatorPanel(boolean isVisible){
		controlPanel.showCalculatorPanel(isVisible);
	}
	
	private ListSelectionListener selectionListener() {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;

				// table selection changed -> update stylebar
				int[] selRows = getConsoleTable().getSelectedRows();
				if (selRows.length > 0) {
					// update list of selected objects in the stylebar
					ArrayList<GeoElement> targetCells = new ArrayList<GeoElement>();
					for (int i = 0; i < getConsoleTable().getRowCount(); i++)
						targetCells.add(getConsoleTable()
								.getGeoCasCell(selRows[0]));
					if (styleBar != null) {
						styleBar.setSelectedRows(targetCells);
					}
				}
			}
		};
	}

	private MouseListener scrollPaneListener() {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int clickedRow = getConsoleTable().rowAtPoint(e.getPoint());
				// boolean undoNeeded = false;

				if (clickedRow < 0) {
					// clicked outside of console table
					int rows = getConsoleTable().getRowCount();
					if (rows == 0) {
						// insert first row
						getConsoleTable().insertRow(null, true);
						// undoNeeded = true;
					} else {
						GeoCasCell cellValue = getConsoleTable()
								.getGeoCasCell(rows - 1);
						if (cellValue.isEmpty()) {
							getConsoleTable().startEditingRow(rows - 1);
						} else {
							getConsoleTable().insertRow(null, true);
							// undoNeeded = true;
						}
					}
				}

				// if (undoNeeded) {
				// // store undo info
				// getApp().storeUndoInfo();
				// }
			}
		};
	}

	/**
	 * Shows dialog for substitution tool
	 * @param prefix prefix (keep as is)
	 * @param evalText evaluable text (do substitution here)
	 * @param postfix postfix (keep as is again)
	 * @param selRow row index (starting from 0)
	 */
	public void showSubstituteDialog(String prefix, String evalText,
			String postfix, int selRow) {
		if (subDialog != null && subDialog.isShowing())
			return;

		CASSubDialog d = new CASSubDialog(this, prefix, evalText, postfix,
				selRow);
		d.setAlwaysOnTop(true);
		d.setVisible(true);
		setSubstituteDialog(d);
	}

	/**
	 * Make sure this view knows whether substitute dialog is open
	 * @param d substitute dialog; null to "close"
	 */
	public void setSubstituteDialog(CASSubDialog d) {
		subDialog = d;
	}

	/**
	 * Process currently selected cell using the given command and parameters,
	 * e.g. "Integral", [ "x" ]
	 * @param ggbcmd command name
	 * @param params parameters
	 */
	public void processInput(String ggbcmd, String[] params) {
		casInputHandler.processCurrentRow(ggbcmd, params);
		getApp().storeUndoInfo();
	}
	
	/**
	 * Processes given row.
	 * @see CASInputHandler#processRowThenEdit(int, boolean)
	 * @param row
	 *            row index
	 * @param flag
	 *            start editing
	 */
	public void processRowThenEdit(int row, boolean flag) {
		casInputHandler.processRowThenEdit(row, flag);
	}

	/**
	 * Resolves both static (#) and dynamic($) expressions in selected row,
	 * replacement of dynamic references is also done statically.
	 * @param inputExp input row
	 * @param row row the expression is in
	 * @return string with replaced references
	 */
	public String resolveCASrowReferences(String inputExp, int row) {
		String result = casInputHandler.resolveCASrowReferences(inputExp, row,
				CASInputHandler.ROW_REFERENCE_STATIC, false);
		return casInputHandler.resolveCASrowReferences(result, row,
				CASInputHandler.ROW_REFERENCE_DYNAMIC, false);
	}

	/**
	 * Updates GUI fonts
	 */
	public void updateFonts() {
		
		if (getFont() != null && app.getGUIFontSize() == getFont().getSize())
			return;

		setFont(app.getPlainFont());
		consoleTable.setFont(getFont());
		// make sure the row header resizes with the table
		SwingUtilities.updateComponentTreeUI(this);
	}

	private void createCASTable() {
		consoleTable = new CASTable(this);

		CASTableCellController inputListener = new CASTableCellController(this);
		consoleTable.getEditor().getInputArea().addKeyListener(inputListener);
		// consoleTable.addKeyListener(inputListener);

		// consoleTable.addKeyListener(new ConsoleTableKeyListener());

		// TableCellMouseListener tableCellMouseListener = new
		// TableCellMouseListener(this);
		// consoleTable.addMouseListener(tableCellMouseListener);

	}

	/**
	 * @return the GoGebraCAS used  by this view
	 */
	final public synchronized GeoGebraCAS getCAS() {
		if (cas == null) {
			cas = (geogebra.common.cas.GeoGebraCAS) kernel.getGeoGebraCAS();
		}

		return cas;
	}

	/**
	 * @return CAS table
	 */
	public CASTable getConsoleTable() {
		return consoleTable;
	}

	// /**
	// * returns settings in XML format
	// */
	// public String getGUIXML() {
	// StringBuilder sb = new StringBuilder();
	// sb.append("<casView>\n");
	//
	// int width = getWidth(); // getPreferredSize().width;
	// int height = getHeight(); // getPreferredSize().height;
	//
	// // if (width > MIN_WIDTH && height > MIN_HEIGHT)
	// {
	// sb.append("\t<size ");
	// sb.append(" width=\"");
	// sb.append(width);
	// sb.append("\"");
	// sb.append(" height=\"");
	// sb.append(height);
	// sb.append("\"");
	// sb.append("/>\n");
	// }
	//
	// sb.append("</casView>\n");
	// return sb.toString();
	// }

	// public void getSessionXML(StringBuilder sb) {
	// // get the number of pairs in the view
	// int numOfRows = consoleTable.getRowCount();
	//
	// // don't save session if there is only one empty row
	// if (numOfRows == 0 || consoleTable.getGeoCasCell(0).isEmpty())
	// return;
	//
	// // change kernel settings temporarily
	// int oldCoordStlye = kernel.getCoordStyle();
	// StringType oldPrintForm = kernel.getCASPrintForm();
	// boolean oldValue = kernel.isPrintLocalizedCommandNames();
	// kernel.setCoordStyle(Kernel.COORD_STYLE_DEFAULT);
	// kernel.setCASPrintForm(StringType.GEOGEBRA_XML);
	// kernel.setPrintLocalizedCommandNames(false);
	//
	// sb.append("<casSession>\n");
	//
	// // get the content of each pair in the table with a loop
	// // append the content to the string sb
	// for (int i = 0; i < numOfRows; ++i) {
	// GeoCasCell temp = consoleTable.getGeoCasCell(i);
	// sb.append(temp.getXML());
	// }
	//
	// sb.append("</casSession>\n");
	//
	// // set back kernel
	// kernel.setCoordStyle(oldCoordStlye);
	// kernel.setCASPrintForm(oldPrintForm);
	// kernel.setPrintLocalizedCommandNames(oldValue);
	// }

	/**
	 * Returns the output string in the n-th row of this CAS view.
	 * @param n row index (starting from 0)
	 * @return output value
	 */
	public String getRowOutputValue(int n) {
		ValidExpression outVE = consoleTable.getGeoCasCell(n)
				.getOutputValidExpression();

		// if we don't have an outputVE, we let GeoCasCell deal with it :)
		if (outVE == null) {
			return consoleTable.getGeoCasCell(n).getOutput(
					StringTemplate.numericDefault);
		}
		return outVE.toString(StringTemplate.numericDefault);
	}

	/**
	 * Returns the input string in the n-th row of this CAS view. If the n-th
	 * cell has no output string, the input string of this cell is returned.
	 * @param n row index (starting from 0)
	 * @return input string in the n-th row of this CAS view
	 */
	public String getRowInputValue(int n) {
		return consoleTable.getGeoCasCell(n).getInput(
				StringTemplate.defaultTemplate);
	}

	/**
	 * Returns the number of rows of this CAS view.
	 * @return the number of rows of this CAS view.
	 */
	public int getRowCount() {
		return consoleTable.getRowCount();
	}

	/**
	 * Component representation of this view
	 * @return reference to self
	 */
	public JComponent getCASViewComponent() {
		return this;
	}

	/**
	 * @return row headers
	 */
	public RowHeader getRowHeader() {
		return rowHeader;
	}

	/**
	 * @return application of this view
	 */
	public Application getApp() {
		return app;
	}

	// public void focusGained(FocusEvent arg0) {
	// firstSetModeAfterFocusGained = true;
	//
	// // // start editing last row
	// // int lastRow = consoleTable.getRowCount() - 1;
	// // if (lastRow >= 0)
	// // consoleTable.startEditingRow(lastRow);
	// }
	//
	// public void focusLost(FocusEvent arg0) {
	// firstSetModeAfterFocusGained = true;
	// }

	/**
	 * Defines new functions in the CAS
	 */
	public void add(GeoElement geo) {
		update(geo);
	}

	/**
	 * Removes function definitions from the CAS
	 */
	public void remove(GeoElement geo) {
		if (geo instanceof GeoCasCell) {
			GeoCasCell casCell = (GeoCasCell) geo;
			consoleTable.deleteRow(casCell.getRowNumber());
		}
	}

	/**
	 * Handles updates of geo in CAS view.
	 */
	public void update(GeoElement geo) {
		if (geo instanceof GeoCasCell) {
			GeoCasCell casCell = (GeoCasCell) geo;
			consoleTable.setRow(casCell.getRowNumber(), casCell);
		}
	}

	final public void updateVisualStyle(GeoElement geo) {
		update(geo);
	}

	/**
	 * Handles toolbar mode changes
	 */
	public void setMode(int mode) {
		if (toolbarIsUpdatedByDockPanel)
			return;

		String command = kernel.getModeText(mode); // e.g. "Derivative"

		switch (mode) {
		case EuclidianConstants.MODE_CAS_EVALUATE:
		case EuclidianConstants.MODE_CAS_NUMERIC:
		case EuclidianConstants.MODE_CAS_KEEP_INPUT:
		case EuclidianConstants.MODE_CAS_EXPAND:
		case EuclidianConstants.MODE_CAS_FACTOR:
		case EuclidianConstants.MODE_CAS_SUBSTITUTE:
			// no parameters

			processInput(command, null);
			break;
		case EuclidianConstants.MODE_DELETE:
			// no parameters

			boolean undo = deleteCasCells(consoleTable.getSelectedRows());
			if(undo)
				consoleTable.app.storeUndoInfo();
			break;

		case EuclidianConstants.MODE_CAS_SOLVE:
		case EuclidianConstants.MODE_CAS_DERIVATIVE:
		case EuclidianConstants.MODE_CAS_NUMERICAL_SOLVE:
		case EuclidianConstants.MODE_CAS_INTEGRAL:
			// use first variable in expression as parameter
			processInput(command, new String[] { "%0" });
			break;
		default:
			// ignore other modes
		}
	}
	
	/**
	 * Deletes given CAS cells both from view and from construction
	 * @param selRows selected rows
	 * @return true if undo needed
	 */
	public boolean deleteCasCells(int[] selRows) {
		boolean undoNeeded = false;
		//reverse order makes sure we don't move cells that are waiting for deletion
		for (int i=selRows.length-1; i >= 0; i--) {
			GeoCasCell casCell = consoleTable.getGeoCasCell(selRows[i]);
			if (casCell != null) {
				casCell.remove(true);
				undoNeeded = true;
			}
		}
		return undoNeeded;
	}

	/**
	 * Renames function definitions in the CAS
	 */
	public void rename(GeoElement geo) {
		update(geo);
	}

	public void clearView() {
		// delete all rows
		consoleTable.deleteAllRows();
		ensureOneEmptyRow();
	}

	public void repaintView() {
		repaint();
		// ensureOneEmptyRow();
	}

	public void reset() {
		repaintView();
	}

	public void updateAuxiliaryObject(GeoElement geo) {
		//do nothing
	}

	/**
	 * Attaches this view to kernel to receive messages
	 */
	public void attachView() {
		clearView();
		kernel.notifyAddAll(this);
		kernel.attach(this);
	}
	/**
	 * Detach this view from kernel
	 */
	public void detachView() {
		kernel.detach(this);
		clearView();
	}

	/**
	 * Makes sure we have an empty row at the end.
	 */
	private void ensureOneEmptyRow() {
		int rows = getRowCount();
		// add an empty one when we have no rows or last one is not empty
		// if (rows == 0 || !consoleTable.isRowEmpty(rows-1)) {
		if (rows == 0) {
			GeoCasCell casCell = new GeoCasCell(kernel.getConstruction());
			consoleTable.insertRow(rows, casCell, false);
		}
	}

	/**
	 * @return input handler
	 */
	public CASInputHandler getInputHandler() {
		return casInputHandler;
	}

	//
	// public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
	// throws PrinterException {
	// app.exporting=true;
	// int r=consoleTable.getPrintable(PrintMode.FIT_WIDTH, null,
	// null).print(graphics, pageFormat, pageIndex);
	// app.exporting=false;
	// return r;
	// }

	/**
	 * Updates labels to match current locale
	 */
	public void setLabels() {
		consoleTable.setLabels();
	}

	public int getViewID() {
		return AbstractApplication.VIEW_CAS;
	}

	/**
	 * This should be called with "false" to ignore mode changes temporarily 
	 * @param toolbarIsUpdatedByDockPanel whether toolbar is being updated by dock panel 
	 */

	public void setToolbarIsUpdatedByDockPanel(
			boolean toolbarIsUpdatedByDockPanel) {
		this.toolbarIsUpdatedByDockPanel = toolbarIsUpdatedByDockPanel;
	}

	public Application getApplication() {
		return app;
	}

	public int[] getGridColwidths() {
		return new int[] { rowHeader.getWidth() + consoleTable.getWidth() };
	}

	public int[] getGridRowHeights() {
		int[] heights = new int[consoleTable.getRowCount()];
		for (int i = 0; i < heights.length; i++) {
			heights[i] = consoleTable.getRowHeight(i);
		}
		return heights;
	}

	public Component[][] getPrintComponents() {
		return new Component[][] { { rowHeader, consoleTable } };
	}

	/**
	 * Returns stylebar for this view; if not initialized so far, creates new one
	 * @return style bar
	 */
	public CASStyleBar getCASStyleBar() {
		if (styleBar == null) {
			styleBar = newCASStyleBar();
		}
		return styleBar;
	}

	/**
	 * @return new instance of CASStyleBar
	 */
	protected CASStyleBar newCASStyleBar() {
		return new CASStyleBar(this, app);
	}
}