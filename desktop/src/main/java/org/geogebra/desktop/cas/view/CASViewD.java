package org.geogebra.desktop.cas.view;

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

import org.geogebra.common.cas.view.CASInputHandler;
import org.geogebra.common.cas.view.CASTable;
import org.geogebra.common.cas.view.CASView;
import org.geogebra.common.gui.SetOrientation;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.view.Gridable;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.CASDragGestureListener;
import org.geogebra.desktop.util.CASDropTargetListener;

/**
 * Computer algebra view.
 * 
 * @author Markus Hohenwarter, Quan Yuan
 */
public class CASViewD extends CASView implements Gridable, SetOrientation {

	private JComponent component;

	private CASTableD consoleTable;

	private CASSubDialogD subDialog;
	private ListSelectionModel listSelModel;

	final private AppD app;
	final private RowHeaderD rowHeader;

	/** stylebar */
	CASStyleBar styleBar;

	/**
	 * Component representing this view
	 */
	static class CASComponent extends JComponent {
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Creates new CAS view
	 * 
	 * @param app
	 *            application
	 */
	public CASViewD(final AppD app) {
		super(app.getKernel());
		component = new CASComponent();
		this.app = app;
		listSelModel = new DefaultListSelectionModel();


		// CAS input/output cells
		createCASTable();
		// row header
		rowHeader = new RowHeaderD(consoleTable, true, listSelModel, app);
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
				GColorD.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR)));
		p.setBackground(Color.white);
		scrollPane.setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER, p);

		// put the scrollpanel in
		component.setLayout(new BorderLayout());
		component.add(scrollPane, BorderLayout.CENTER);

		component.setBackground(Color.white);

		getConsoleTable().getSelectionModel()
				.addListSelectionListener(selectionListener());

		// listen to clicks below last row in consoleTable: create new row
		scrollPane.addMouseListener(scrollPaneListener());
		// input handler
		setCasInputHandler(new CASInputHandler(this));

		// addFocusListener(this);

		// Create new DragGestureListener and enable Drag
		CASDragGestureListener dragGestListener = new CASDragGestureListener(
				kernel, consoleTable);
		dragGestListener.enableDnD();

		// Create new CASDropTargetListener and enable Drop
		CASDropTargetListener dropTargetListener = new CASDropTargetListener(
				app, this, consoleTable);
		dropTargetListener.enableDnD();

		updateFonts();

		Thread initCAS = new Thread() {
			@Override
			public void run() {
				getCAS().initCurrentCAS();
				GuiManagerD gm = (GuiManagerD) app.getGuiManager();
				if (gm != null) {
					gm.reInitHelpPanel(true);
				}

			}
		};
		initCAS.start();
	}

	private ListSelectionListener selectionListener() {
		return new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}

				// table selection changed -> update stylebar
				int[] selRows = getConsoleTable().getSelectedRows();
				if (selRows.length > 0) {
					// update list of selected objects in the stylebar
					ArrayList<GeoElement> targetCells = new ArrayList<>();
					for (int i = 0; i < getConsoleTable().getRowCount(); i++) {
						GeoElement cell = getConsoleTable()
								.getGeoCasCell(selRows[0]);
						if (cell != null) {
							targetCells.add(cell);
						}
					}
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
						getConsoleTable().stopEditing();
						GeoCasCell cellValue = getConsoleTable()
								.getGeoCasCell(rows - 1);
						if (!cellValue.isInputEmpty()
								&& cellValue.isOutputEmpty()) {
							getConsoleTable().startEditingRow(rows - 1);
							processInput("Evaluate", true);
							ensureOneEmptyRow();
							getConsoleTable()
									.startEditingRow(getRowCount() - 1);
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
	public void showSubstituteDialog(final String prefix, final String evalText,
			final String postfix, final int selRow) {
		if (subDialog != null && subDialog.isShowing()) {
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				CASSubDialogD d = new CASSubDialogD(getCASViewD(), prefix,
						evalText, postfix, selRow);
				d.setAlwaysOnTop(true);
				d.setVisible(true);
				setSubstituteDialog(d);
			}
		});
	}

	/**
	 * Make sure this view knows whether substitute dialog is open
	 * 
	 * @param d
	 *            substitute dialog; null to "close"
	 */
	public void setSubstituteDialog(CASSubDialogD d) {
		subDialog = d;
	}

	/**
	 * Updates GUI fonts
	 */
	public void updateFonts() {

		if (component.getFont() != null
				&& app.getGUIFontSize() == component.getFont().getSize()) {
			return;
		}
		component.setFont(app.getPlainFont());
		getConsoleTable().setFont(component.getFont());
		if (rowHeader != null) {
			rowHeader.updateIcons();
		}
		getCASStyleBar().reinit();
		// make sure the row header resizes with the table
		SwingUtilities.updateComponentTreeUI(component);
	}

	private void createCASTable() {
		consoleTable = new CASTableD(this);

		CASTableCellControllerD inputListener = new CASTableCellControllerD(
				this);
		getConsoleTable().getEditor().getInputArea()
				.addKeyListener(inputListener);
		getConsoleTable().getEditor().getInputArea()
				.addMouseListener(inputListener);
	}

	@Override
	public CASTableD getConsoleTable() {
		return consoleTable;
	}

	/**
	 * Component representation of this view
	 * 
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

	@Override
	public void repaintView() {
		component.repaint();
	}

	@Override
	public AppD getApplication() {
		return app;
	}

	@Override
	public int[] getGridColwidths() {
		return new int[] {
				rowHeader.getWidth() + getConsoleTable().getWidth() };
	}

	@Override
	public int[] getGridRowHeights() {
		int[] heights = new int[getConsoleTable().getRowCount()];
		for (int i = 0; i < heights.length; i++) {
			heights[i] = getConsoleTable().getRowHeight(i);
		}
		return heights;
	}

	@Override
	public Component[][] getPrintComponents() {
		return new Component[][] { { rowHeader, consoleTable } };
	}

	/**
	 * Returns stylebar for this view; if not initialized so far, creates new
	 * one
	 * 
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

	@Override
	public boolean hasFocus() {
		Log.debug("unimplemented");
		return false;
	}

	@Override
	public boolean isShowing() {
		Log.debug("unimplemented");
		return false;
	}

	/**
	 * @return this casView
	 */
	public CASViewD getCASViewD() {
		return this;
	}

	@Override
	public boolean suggestRepaint() {
		return false;
		// only used in web
	}

	@Override
	public void setOrientation() {
		getConsoleTable().setOrientation();
	}

	/**
	 * Stop editing
	 */
	public void resetCursor() {
		CASTable table = getConsoleTable();
		table.stopEditing();
	}

	/**
	 * @return list model
	 */
	public ListSelectionModel getListSelModel() {
		return listSelModel;
	}

	@Override
	public void resetItems(boolean unselectAll) {
		// nothing to do in desktop
	}
}