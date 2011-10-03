package geogebra.cas.view;

import geogebra.cas.GeoGebraCAS;
import geogebra.euclidian.EuclidianConstants;
import geogebra.gui.view.Gridable;
import geogebra.kernel.GeoCasCell;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.View;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.ValidExpression;

import geogebra.main.Application;
import geogebra.main.GeoGebraColorConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Computer algebra view.
 * 
 * @author Markus Hohenwarter, Quan Yuan
 */
public class CASView extends JComponent implements View, Gridable {

	private Kernel kernel;

	private CASTable consoleTable;
	private CASInputHandler casInputHandler;
	private CASSubDialog subDialog;

	private GeoGebraCAS cas;
	private Application app;
	private JPanel btPanel;
	private final RowHeader rowHeader;
	private boolean toolbarIsUpdatedByDockPanel;




	public CASView(Application app) {
		kernel = app.getKernel();
		this.app = app;

		Thread initCAS = new Thread() {
			public void run() {
				// init CAS
				getCAS();
			}
		};
		initCAS.start();

		// CAS input/output cells
		createCASTable();

		// row header
		//final JList rowHeader = new RowHeader(consoleTable);
		rowHeader = new RowHeader(consoleTable, true);
		
		// init the scroll panel
		JScrollPane scrollPane = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setRowHeaderView(rowHeader);
		scrollPane.setViewportView(consoleTable);
		scrollPane.setBackground(Color.white);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		//set the lower left corner so that the horizontal scroller looks good
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, GeoGebraColorConstants.TABLE_GRID_COLOR));
		p.setBackground(Color.white);
		scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER, p);
		

		// put the scrollpanel in
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		this.setBackground(Color.white);

		// tell rowheader about selection updates in table
		consoleTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						if (e.getValueIsAdjusting())
							return;

						// table slection changed -> rowheader table selection
						int[] selRows = consoleTable.getSelectedRows();
						if (selRows.length > 0)
							rowHeader.setSelectedIndices(selRows);
					}
				});

		// listen to clicks below last row in consoleTable: create new row
		scrollPane.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int clickedRow = consoleTable.rowAtPoint(e.getPoint());
				//boolean undoNeeded = false;

				if (clickedRow < 0) {
					// clicked outside of console table
					int rows = consoleTable.getRowCount();
					if (rows == 0) {
						// insert first row
						consoleTable.insertRow(null, true);
						//undoNeeded = true;
					} else {
						GeoCasCell cellValue = consoleTable
								.getGeoCasCell(rows - 1);
						if (cellValue.isEmpty()) {
							consoleTable.startEditingRow(rows - 1);
						} else {
							consoleTable.insertRow(null, true);
							//undoNeeded = true;
						}
					}
				}

//				if (undoNeeded) {
//					// store undo info
//					getApp().storeUndoInfo();
//				}
			}
		});

		// input handler
		casInputHandler = new CASInputHandler(this);

		//addFocusListener(this);
	}
	
	
	public void showSubstituteDialog(String prefix, String evalText, String postfix, int selRow) {
		if (subDialog != null && subDialog.isShowing()) return;
		
		CASSubDialog d = new CASSubDialog(this, prefix, evalText, postfix, selRow);
		d.setAlwaysOnTop(true);
		d.setVisible(true);
		setSubstituteDialog(d);
	}
	
	public void setSubstituteDialog(CASSubDialog d) {
		subDialog = d;
	}
	
	public CASSubDialog getSubstituteDialog() {
		return subDialog;
	}

	/**
	 * Process currently selected cell using the given command and parameters,
	 * e.g. "Integral", [ "x" ]
	 */
	public void processInput(String ggbcmd, String[] params) {
		casInputHandler.processCurrentRow(ggbcmd, params);
		getApp().storeUndoInfo();
	}
	
	public void processRowThenEdit(int row, boolean flag) {
		casInputHandler.processRowThenEdit(row, flag);
	}
	
	public String resolveCASrowReferences(String inputExp, int row) {
		String result = casInputHandler.resolveCASrowReferences(inputExp, row, CASInputHandler.ROW_REFERENCE_STATIC);
		return casInputHandler.resolveCASrowReferences(result, row, CASInputHandler.ROW_REFERENCE_DYNAMIC);
	}

	public void updateFonts() {
		if (app.getGUIFontSize() == getFont().getSize())
			return;

		setFont(app.getPlainFont());
		consoleTable.setFont(getFont());
		validate();
	}

	public Font getBoldFont() {
		return app.getBoldFont();
	}

	private void createCASTable() {
		consoleTable = new CASTable(this);

		CASTableCellController inputListener = new CASTableCellController(this);
		consoleTable.getEditor().getInputArea().addKeyListener(inputListener);
		// consoleTable.addKeyListener(inputListener);

		// consoleTable.addKeyListener(new ConsoleTableKeyListener());

//		TableCellMouseListener tableCellMouseListener = new TableCellMouseListener(this);
//		consoleTable.addMouseListener(tableCellMouseListener);
		
	}

	final public synchronized GeoGebraCAS getCAS() {
		if (cas == null) {
			cas = (geogebra.cas.GeoGebraCAS) kernel.getGeoGebraCAS();
		}

		return cas;
	}

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

//	public void getSessionXML(StringBuilder sb) {
//		// get the number of pairs in the view
//		int numOfRows = consoleTable.getRowCount();
//		
//		// don't save session if there is only one empty row
//		if (numOfRows == 0 || consoleTable.getGeoCasCell(0).isEmpty()) 
//			return;				
//
//		// change kernel settings temporarily
//		int oldCoordStlye = kernel.getCoordStyle();
//		int oldPrintForm = kernel.getCASPrintForm();
//        boolean oldValue = kernel.isPrintLocalizedCommandNames();
//		kernel.setCoordStyle(Kernel.COORD_STYLE_DEFAULT);	
//		kernel.setCASPrintForm(ExpressionNode.STRING_TYPE_GEOGEBRA_XML);
//        kernel.setPrintLocalizedCommandNames(false); 
//
//		sb.append("<casSession>\n");		
//
//		// get the content of each pair in the table with a loop
//		// append the content to the string sb
//		for (int i = 0; i < numOfRows; ++i) {
//			GeoCasCell temp = consoleTable.getGeoCasCell(i);
//			sb.append(temp.getXML());
//		}
//
//		sb.append("</casSession>\n");
//		
//		// set back kernel
//		kernel.setCoordStyle(oldCoordStlye);
//		kernel.setCASPrintForm(oldPrintForm);
//		kernel.setPrintLocalizedCommandNames(oldValue);      
//	}

	/**
	 * Returns the output string in the n-th row of this CAS view.
	 */
	public String getRowOutputValue(int n) {
		ValidExpression outVE = consoleTable.getGeoCasCell(n).getOutputValidExpression();
		
		// if we don't have an outputVE, we let GeoCasCell deal with it :)
		if (outVE == null)
			return consoleTable.getGeoCasCell(n).getOutput();
		else
			return outVE.toString();
	}

	/**
	 * Returns the input string in the n-th row of this CAS view. If the n-th
	 * cell has no output string, the input string of this cell is returned.
	 */
	public String getRowInputValue(int n) {
		return consoleTable.getGeoCasCell(n).getInput();
	}

	/**
	 * Returns the number of rows of this CAS view.
	 */
	public int getRowCount() {
		return consoleTable.getRowCount();
	}

	public JComponent getCASViewComponent() {
		return this;
	}
	public RowHeader getRowHeader(){
		return rowHeader;
	}

	

	public Application getApp() {
		return app;
	}

//	public void focusGained(FocusEvent arg0) {
//		firstSetModeAfterFocusGained = true;
//		
////		// start editing last row
////		int lastRow = consoleTable.getRowCount() - 1;
////		if (lastRow >= 0)
////			consoleTable.startEditingRow(lastRow);
//	}
//
//	public void focusLost(FocusEvent arg0) {
//		firstSetModeAfterFocusGained = true;
//	}

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
		if (toolbarIsUpdatedByDockPanel) return;
		
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
			
			case EuclidianConstants.MODE_CAS_SOLVE:
			case EuclidianConstants.MODE_CAS_DERIVATIVE:
			case EuclidianConstants.MODE_CAS_INTEGRAL:
				// use first variable in expression as parameter
				processInput(command, new String[] {"%0"});
				break;
			default:
				// ignore other modes
		}				
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
		//ensureOneEmptyRow();
	}

	public void reset() {
		repaintView();		
	}

	public void updateAuxiliaryObject(GeoElement geo) {
	}

	public void attachView() {
		clearView();
		kernel.notifyAddAll(this);
		kernel.attach(this);		
	}

	public void detachView() {
		kernel.detach(this);
		clearView();
	}
	
	/**
     * Makes sure we have an empty row at the end.
     * @return The new row.
     */
    private void ensureOneEmptyRow() {
    	int rows = getRowCount();
    	//  add an empty one when we have no rows or last one is not empty 
    	//if (rows == 0 || !consoleTable.isRowEmpty(rows-1)) {
    	if (rows == 0) {
    		GeoCasCell casCell = new GeoCasCell(kernel.getConstruction());
    		consoleTable.insertRow(rows, casCell, false);
    	}     	    	
    }
	
	public CASInputHandler getInputHandler()
	{
		return casInputHandler;
	}

//
//	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
//			throws PrinterException {
//		app.exporting=true;
//		int r=consoleTable.getPrintable(PrintMode.FIT_WIDTH, null, null).print(graphics, pageFormat, pageIndex);
//		app.exporting=false;
//		return r;
//	}

	public void setLabels() {
		consoleTable.setLabels();
	}


	public int getViewID() {
		return Application.VIEW_CAS;
	}
	
	public boolean isToolbarIsUpdatedByDockPanel() {
		return toolbarIsUpdatedByDockPanel;
	}

	public void setToolbarIsUpdatedByDockPanel(boolean toolbarIsUpdatedByDockPanel) {
		this.toolbarIsUpdatedByDockPanel = toolbarIsUpdatedByDockPanel;
	}


	public Application getApplication() {
		return app;
	}


	public int[] getGridColwidths() {
		return new int[]{rowHeader.getWidth()+consoleTable.getWidth()};
	}


	public int[] getGridRowHeights() {
		int[] heights=new int[consoleTable.getRowCount()];
		for (int i=0;i<heights.length;i++){
			heights[i]=consoleTable.getRowHeight(i);
		}
		return heights;
	}
	


	public Component[][] getPrintComponents() {
		return new Component[][]{{rowHeader,consoleTable}};
	}
}