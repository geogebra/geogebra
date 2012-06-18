package geogebra.cas.view;

import geogebra.common.cas.view.CASInputHandler;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.gui.GuiManager;
import geogebra.gui.inputbar.InputBarHelpPanel;
import geogebra.gui.view.Gridable;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

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
public class CASView  extends geogebra.common.cas.view.CASView implements Gridable {

	
	private JComponent component;
	
	
	private CASTable consoleTable;
	
	private CASSubDialog subDialog;
	private ListSelectionModel listSelModel;

	final private Application app;
	final private RowHeader rowHeader;
	
	/** stylebar */
	CASStyleBar styleBar;

	private CASControlPanel controlPanel;
	
	class CASComponent extends JComponent{
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Creates new CAS view
	 * @param app application
	 */
	public CASView(Application app) {
		long a = System.currentTimeMillis(); 
		component = new CASComponent();
		kernel = app.getKernel();
		this.app = app;
		listSelModel = new DefaultListSelectionModel();	
		getCAS();
	AbstractApplication.debug(System.currentTimeMillis()-a);	

		// init commands subtable for cas-commands in inputbar-help
		kernel.getAlgebraProcessor().enableCAS();
	AbstractApplication.debug(System.currentTimeMillis()-a);
		GuiManager gm = app.getGuiManager();
		if (gm != null && gm.hasInputHelpPanel()) {
			gm.reInitHelpPanel();
		}
	AbstractApplication.debug(System.currentTimeMillis()-a);
		// CAS input/output cells
		createCASTable();
	AbstractApplication.debug(System.currentTimeMillis()-a);
		// row header
		rowHeader = new RowHeader(consoleTable, false, listSelModel);
		getConsoleTable().setSelectionModel(listSelModel);
		// init the scroll panel
		JScrollPane scrollPane = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setRowHeaderView(rowHeader);
		scrollPane.setViewportView(consoleTable);
		scrollPane.setBackground(Color.white);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
	AbstractApplication.debug(System.currentTimeMillis()-a);
		// set the lower left corner so that the horizontal scroller looks good
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1,
				geogebra.awt.Color
						.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR)));
		p.setBackground(Color.white);
		scrollPane.setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER, p);

		// put the scrollpanel in
		component.setLayout(new BorderLayout());
		component.add(scrollPane, BorderLayout.CENTER);
		
		controlPanel = new CASControlPanel(app,this);
		component.add(controlPanel.getControlPanel(), BorderLayout.SOUTH);
		
		component.setBackground(Color.white);

		getConsoleTable().getSelectionModel().addListSelectionListener(selectionListener());

		// listen to clicks below last row in consoleTable: create new row
		scrollPane.addMouseListener(scrollPaneListener());
	AbstractApplication.debug(System.currentTimeMillis()-a);
		// input handler
		casInputHandler = new CASInputHandler(this);

		// addFocusListener(this);
		
		updateFonts();
	AbstractApplication.debug(System.currentTimeMillis()-a);
		Thread initCAS = new Thread() {
			@Override
			public void run() {
				getCAS().initCurrentCAS();
				
			}
		};
		initCAS.start();
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
	 * Updates GUI fonts
	 */
	public void updateFonts() {
		
		if (component.getFont() != null && app.getGUIFontSize() == component.getFont().getSize())
			return;

		component.setFont(app.getPlainFont());
		getConsoleTable().setFont(component.getFont());
		// make sure the row header resizes with the table
		SwingUtilities.updateComponentTreeUI(component);
	}

	private void createCASTable() {
		consoleTable = new CASTable(this);

		CASTableCellController inputListener = new CASTableCellController(this);
		getConsoleTable().getEditor().getInputArea().addKeyListener(inputListener);
		// getConsoleTable().addKeyListener(inputListener);

		// getConsoleTable().addKeyListener(new ConsoleTableKeyListener());

		// TableCellMouseListener tableCellMouseListener = new
		// TableCellMouseListener(this);
		// getConsoleTable().addMouseListener(tableCellMouseListener);

	}

	/**
	 * @return CAS table
	 */
	@Override
	public CASTable getConsoleTable() {
		return consoleTable;
	}


	/**
	 * Component representation of this view
	 * @return reference to self
	 */
	public JComponent getCASViewComponent() {
		return component;
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
	@Override
	public Application getApp() {
		return app;
	}
	

	public void repaintView() {
		component.repaint();
		// ensureOneEmptyRow();
	}
	
	public void repaint() {
		component.repaint();
		// ensureOneEmptyRow();
	}

	public Application getApplication() {
		return app;
	}

	public int[] getGridColwidths() {
		return new int[] { rowHeader.getWidth() + getConsoleTable().getWidth() };
	}

	public int[] getGridRowHeights() {
		int[] heights = new int[getConsoleTable().getRowCount()];
		for (int i = 0; i < heights.length; i++) {
			heights[i] = getConsoleTable().getRowHeight(i);
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