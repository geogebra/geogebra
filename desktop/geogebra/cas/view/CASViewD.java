package geogebra.cas.view;

import geogebra.common.cas.view.CASInputHandler;
import geogebra.common.cas.view.CASView;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.gui.GuiManagerD;
import geogebra.gui.view.Gridable;
import geogebra.main.AppD;
import geogebra.util.CASDragGestureListener;
import geogebra.util.CASDropTargetListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
public class CASViewD  extends CASView implements Gridable {

	
	private JComponent component;
	
	
	private CASTableD consoleTable;
	
	private CASSubDialog subDialog;
	private ListSelectionModel listSelModel;

	final private AppD app;
	final private RowHeaderD rowHeader;
	
	/** stylebar */
	CASStyleBar styleBar;
	
	/**
	 * Component representing this view
	 * @author Zbynek Konecny
	 */
	class CASComponent extends JComponent{
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Creates new CAS view
	 * @param app application
	 */
	public CASViewD(final AppD app) { 
		component = new CASComponent();
		kernel = app.getKernel();
		this.app = app;
		listSelModel = new DefaultListSelectionModel();	
		getCAS();	

		// init commands subtable for cas-commands in inputbar-help
		kernel.getAlgebraProcessor().enableCAS();
		
		// CAS input/output cells
		createCASTable();
		// row header
		rowHeader = new RowHeaderD(consoleTable, true, listSelModel);
		getConsoleTable().setSelectionModel(listSelModel);
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
				geogebra.awt.GColorD
						.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR)));
		p.setBackground(Color.white);
		scrollPane.setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER, p);

		// put the scrollpanel in
		component.setLayout(new BorderLayout());
		component.add(scrollPane, BorderLayout.CENTER);
		
		component.setBackground(Color.white);

		getConsoleTable().getSelectionModel().addListSelectionListener(selectionListener());

		// listen to clicks below last row in consoleTable: create new row
		scrollPane.addMouseListener(scrollPaneListener());
		// input handler
		casInputHandler = new CASInputHandler(this);

		// addFocusListener(this);
		
		
		//Create new DragGestureListener and enable Drag
		CASDragGestureListener dragGestListener = new CASDragGestureListener(kernel , consoleTable);
		dragGestListener.enableDnD();
		
		//Create new CASDropTargetListener and enable Drop
		CASDropTargetListener dropTargetListener = new CASDropTargetListener(app, this,  consoleTable);
		dropTargetListener.enableDnD();
		
		updateFonts();

		Thread initCAS = new Thread() {
			@Override
			public void run() {
				getCAS().initCurrentCAS();
				GuiManagerD gm = (GuiManagerD) app.getGuiManager();
				if (gm != null && gm.hasInputHelpPanel()) {
					gm.reInitHelpPanel();
				}
				
			}
		};
		initCAS.start();
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
						insertRow(null, true);
						// undoNeeded = true;
					} else {
						GeoCasCell cellValue = getConsoleTable()
								.getGeoCasCell(rows - 1);
						if (cellValue.isEmpty()) {
							getConsoleTable().startEditingRow(rows - 1);
						} else {
							insertRow(null, true);
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

	@Override
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
		consoleTable = new CASTableD(this);

		CASTableCellController inputListener = new CASTableCellController(this);
		getConsoleTable().getEditor().getInputArea().addKeyListener(inputListener);
		getConsoleTable().getEditor().getInputArea().addMouseListener(inputListener);
	}

	@Override
	public CASTableD getConsoleTable() {
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
	public RowHeaderD getRowHeader() {
		return rowHeader;
	}

	/**
	 * @return application of this view
	 */
	@Override
	public AppD getApp() {
		return app;
	}
	

	public void repaintView() {
		component.repaint();
		// ensureOneEmptyRow();
	}

	public AppD getApplication() {
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

	public boolean hasFocus() {
	    App.debug("unimplemented");
		return false;
	}

	public void repaint() {
	}

	public boolean isShowing() {
		App.debug("unimplemented");
		return false;
	}
}