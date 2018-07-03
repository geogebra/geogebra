package org.geogebra.desktop.gui.view.consprotocol;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTable.PrintMode;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.common.jre.util.Base64;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.ConstructionProtocolSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.export.ConstructionProtocolExportDialogD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.TitlePanel;
import org.geogebra.desktop.gui.view.algebra.InputPanelD;
import org.geogebra.desktop.javax.swing.GImageIconD;
import org.geogebra.desktop.javax.swing.table.GAbstractTableModelD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;

import com.himamis.retex.editor.share.util.Unicode;

public class ConstructionProtocolViewD extends ConstructionProtocolView
		implements Printable, SettingListener, SetLabels {

	static Color COLOR_STEP_HIGHLIGHT = AppD.COLOR_SELECTION;
	private static Color COLOR_DRAG_HIGHLIGHT = new Color(250, 250, 200);
	private static Color COLOR_DROP_HIGHLIGHT = Color.lightGray;

	public JTable table;
	// public JPanel cpPanel;

	private TableColumn[] tableColumns;

	// private AbstractAction printPreviewAction, exportHtmlAction;

	// for drag & drop
	private boolean dragging = false;
	int dragIndex = -1; // dragged construction index
	private int dropIndex = -1;

	// public ConstructionProtocolNavigationD protNavBar; // navigation bar of
	// protocol window
	private ConstructionProtocolViewD view = this;
	public JScrollPane scrollPane;
	private ConstructionProtocolStyleBar helperBar;
	private AbstractAction exportHtmlAction, printPreviewAction;
	private LocalizationD loc;

	public ConstructionProtocolViewD(final AppD app) {
		// cpPanel = new JPanel(new BorderLayout());

		this.app = app;
		this.loc = app.getLocalization();
		kernel = app.getKernel();
		data = new ConstructionTableDataD(this);
		useColors = true;
		addIcons = false;

		table = new JTable();
		table.setAutoCreateColumnsFromModel(false);
		table.setModel(((ConstructionTableDataD) data).getImpl().getImpl());
		table.setRowSelectionAllowed(true);
		table.setGridColor(Color.lightGray);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		// header
		JTableHeader header = table.getTableHeader();
		// header.setUpdateTableInRealTime(true);
		header.setReorderingAllowed(false);

		// init model
		ConstructionTableCellRenderer renderer;
		HeaderRenderer headerRend = new HeaderRenderer();
		tableColumns = new TableColumn[data.columns.length];

		for (int k = 0; k < data.columns.length; k++) {
			renderer = new ConstructionTableCellRenderer();
			renderer.setHorizontalAlignment(data.columns[k].getAlignment());
			tableColumns[k] = new TableColumn(k,
					data.columns[k].getPreferredWidth(), renderer, null);
			tableColumns[k].setMinWidth(data.columns[k].getMinWidth());
			tableColumns[k].setHeaderRenderer(headerRend);
			if (data.columns[k].getInitShow()) {
				table.addColumn(tableColumns[k]);
			}
			if ("Caption".equals(data.columns[k].getTitle())) {
				tableColumns[k]
						.setCellEditor(new ConstructionTableCellEditor());
			}
		}
		// first column "No." should have fixed width
		tableColumns[0].setMaxWidth(tableColumns[0].getMinWidth());

		table.getColumnModel().addColumnModelListener(
				((ConstructionTableDataD) data).new ColumnMovementListener());

		scrollPane = new JScrollPane(table);
		scrollPane.getViewport().setBackground(Color.white);
		// cpPanel.add(scrollPane, BorderLayout.CENTER);

		// clicking
		ConstructionMouseListener ml = new ConstructionMouseListener();
		table.addMouseListener(ml);
		table.addMouseMotionListener(ml);
		header.addMouseListener(ml); // double clicking, right-click menu
		scrollPane.addMouseListener(ml); // right-click menu

		// keys
		ConstructionKeyListener keyListener = new ConstructionKeyListener();
		table.addKeyListener(keyListener);

		app.getGuiManager().registerConstructionProtocolView(this);

		// navigation bar
		// protNavBar = new ConstructionProtocolNavigationD(app);
		// protNavBar.register(this);
		// protNavBar.setPlayButtonVisible(false);
		// protNavBar.setConsProtButtonVisible(false);
		// this.cpPanel.add(protNavBar.getImpl(), BorderLayout.SOUTH);
		// org.geogebra.desktop.util.Util.addKeyListenerToAll(protNavBar.getImpl(),
		// keyListener);

		initGUI();
		initActions();

		ConstructionProtocolSettings cps = app.getSettings()
				.getConstructionProtocol();
		settingsChanged(cps);
		cps.addListener(this);

	}

	public App getApp() {
		return app;
	}

	public JScrollPane getCpPanel() {
		return scrollPane;
	}

	public int getConstructionIndex(int row) {
		return data.getConstructionIndex(row);
	}

	public AppD getApplication() {
		return (AppD) app;
	}

	public void unregisterNavigationBar(ConstructionProtocolNavigationD nb) {
		navigationBars.remove(nb);
		((ConstructionTableDataD) data).detachView(); // only done if there are
														// no more navigation
														// bars
	}

	public void initProtocol() {
		if (!isViewAttached) {
			((ConstructionTableDataD) data).initView();
		}
	}

	protected void repaintScrollpane() {
		scrollPane.repaint();
	}

	/**
	 * inits GUI with labels of current language
	 */
	public void initGUI() {
		// setTitle(loc.getMenu("ConstructionProtocol"));
		scrollPane.setFont(((AppD) app).getPlainFont());
		// setMenuBar();
		getStyleBar().setLabels();
		// set header values (language may have changed)
		for (int k = 0; k < tableColumns.length; k++) {
			tableColumns[k]
					.setHeaderValue(data.columns[k].getTranslatedTitle());
		}
		table.updateUI();
		table.setFont(((AppD) app).getPlainFont());
		((ConstructionTableDataD) data).updateAll();
		getStyleBar().reinit();
		// protNavBar.updateIcons();
	}

	public void setUseColors(boolean flag) {
		useColors = flag;
		((ConstructionTableDataD) data).updateAll();
	}

	public void setAddIcons(boolean flag) {
		addIcons = flag;
		((ConstructionTableDataD) data).updateAll();
	}

	public boolean getAddIcons() {
		return addIcons;
	}

	public void update() {
		((ConstructionTableDataD) data).updateAll();
	}

	public TableColumn[] getTableColumns() {
		return tableColumns;
	}

	public boolean getUseColors() {
		return useColors;
	}

	public boolean isColumnInModel(TableColumn col) {
		boolean ret = false;
		TableColumnModel model = table.getColumnModel();
		int size = model.getColumnCount();
		for (int i = 0; i < size; ++i) {
			if (model.getColumn(i) == col) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	/**
	 * shows this dialog centered on screen
	 */
	public void setVisible(boolean flag) {
		if (flag) {
			((ConstructionTableDataD) data).attachView();
		} else {
			((ConstructionTableDataD) data).detachView();
		}
		scrollPane.setVisible(flag);
	}

	@Override
	public void scrollToConstructionStep() {
		int rowCount = table.getRowCount();
		if (rowCount == 0) {
			return;
		}

		int step = kernel.getConstructionStep();
		int row = 0;
		for (int i = Math.max(step, 0); i < rowCount; i++) {
			if (data.getConstructionIndex(i) <= step) {
				row = i;
			} else {
				break;
			}
		}

		table.setRowSelectionInterval(row, row);
		table.repaint();
	}

	public JTable getTable() {
		return table;
	}

	public AbstractAction getExportHtmlAction() {
		return exportHtmlAction;
	}

	public AbstractAction getPrintPreviewAction() {
		return printPreviewAction;
	}

	/**
	 * @return The style bar for this view.
	 */
	public ConstructionProtocolStyleBar getStyleBar() {
		if (helperBar == null) {
			helperBar = newConstructionProtocolHelperBar();
		}

		return helperBar;
	}

	/**
	 * @return new Construction Protocol style bar
	 */
	protected ConstructionProtocolStyleBar newConstructionProtocolHelperBar() {
		return new ConstructionProtocolStyleBar(this, (AppD) app);
	}

	private void initActions() {

		exportHtmlAction = new AbstractAction(loc.getMenu("ExportAsWebpage")
				+ " (" + FileExtensions.HTML + ") ...") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();

				Thread runner = new Thread() {
					@Override
					public void run() {
						JDialog d = new ConstructionProtocolExportDialogD(view);
						d.setVisible(true);
					}
				};
				runner.start();

				app.setDefaultCursor();
			}
		};

		printPreviewAction = new AbstractAction(
				loc.getMenu("Print") + Unicode.ELLIPSIS, ((AppD) app)
						.getScaledIcon(GuiResourcesD.DOCUMENT_PRINT_PREVIEW)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();

				Thread runner = new Thread() {
					@Override
					public void run() {

						try {
							Construction cons = app.getKernel()
									.getConstruction();
							getTable().print(JTable.PrintMode.FIT_WIDTH,
									new MessageFormat(tableHeader(cons)),
									new MessageFormat("{0}"), // page numbering
									/* showPrintDialog */true, /* attr */null,
									/* interactive */true /* , */
							/* service *//* null */);
							// service must be omitted for Java version 1.5.0
						} catch (HeadlessException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						} catch (PrinterException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						}

					}

					// This may be too long. FIXME
					private String tableHeader(Construction cons) {

						TitlePanel tp = new TitlePanel((AppD) app);
						String author = tp.loadAuthor();
						String title = cons.getTitle();
						String date = tp.configureDate(cons.getDate());

						if ("".equals(title)) {
							title = loc.getMenu("UntitledConstruction");
						}
						if ("".equals(author)) {
							return title + " (" + date + ")";
						}
						return author + ": " + title + " (" + date + ")";

					}
				};
				runner.start();

				app.setDefaultCursor();
			}
		};

	}

	class ConstructionKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent event) {
			// SPECIAL KEYS
			int keyCode = event.getKeyCode();
			switch (keyCode) {
			default:
				// do nothing
				break;
			case KeyEvent.VK_DELETE:
				ConstructionElement ce = kernel
						.getConstructionElement(kernel.getConstructionStep());
				if (ce != null) {
					ce.remove();
					app.storeUndoInfo();
				}
				break;

			case KeyEvent.VK_UP:
			case KeyEvent.VK_RIGHT:
				previousStep();
				scrollToConstructionStep();
				break;

			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_LEFT:
				nextStep();
				scrollToConstructionStep();
				break;

			case KeyEvent.VK_HOME:
			case KeyEvent.VK_PAGE_UP:
				setConstructionStep(-1);
				scrollToConstructionStep();
				break;

			case KeyEvent.VK_END:
			case KeyEvent.VK_PAGE_DOWN:
				setConstructionStep(kernel.getLastConstructionStep());
				scrollToConstructionStep();
				break;
			}
		}
	}

	class ConstructionMouseListener
			implements MouseListener, MouseMotionListener {

		// smallest and larges possible construction index for dragging
		private int minIndex, maxIndex;

		@Override
		public void mouseClicked(MouseEvent e) {
			Object ob = e.getSource();
			if (ob == table) {
				Point origin = e.getPoint();
				GPoint mouseCoords = new GPoint(e.getPoint().x, e.getPoint().y);
				int row = table.rowAtPoint(origin);
				if (row < 0) {
					return;
				}

				// right click
				if (AppD.isRightClick(e)) {
					GeoElement geo = ((ConstructionTableDataD) data)
							.getGeoElement(row);
					ArrayList<GeoElement> temp = new ArrayList<GeoElement>();
					temp.add(geo);
					((GuiManagerD) app.getGuiManager()).showPopupMenu(temp,
							table, mouseCoords);
				} else { // left click

					if (e.getClickCount() == 1) {

						// click on breakpoint column?
						int column = table.columnAtPoint(origin);
						String colName = table.getColumnName(column);

						// if ("Breakpoint".equals(colName)) {
						if ("H".equals(colName)) {
							RowData rd = data.getRow(row);
							GeoElement geo = rd.getGeo();
							boolean newVal = !geo.isConsProtocolBreakpoint();
							geo.setConsProtocolBreakpoint(newVal);

							// update only current row
							rd.updateAll();

							if (kernel.getConstruction().showOnlyBreakpoints()
									&& !newVal) {
								data.remove(geo);
							}

							/*
							 * // update geo and all siblings GeoElement []
							 * siblings = geo.getSiblings(); if (siblings !=
							 * null) { data.updateAll(); } else { // update only
							 * current row rd.updateAll(); }
							 * 
							 * // no longer a breakpoint: hide it if
							 * (kernel.showOnlyBreakpoints() && !newVal) { if
							 * (siblings == null) data.remove(geo); else { for
							 * (int i=0; i < siblings.length; i++) {
							 * data.remove(siblings[i]); } } }
							 */
						}
					}

					// double click
					if (e.getClickCount() == 2) {
						data.setConstructionStepForRow(row);
						table.repaint();
					}
				}
			} else if (ob == table.getTableHeader()
					&& (e.getClickCount() == 2)) {
				setConstructionStep(-1);
				table.repaint();
			} else if ((e.getClickCount() == 1) && (AppD.isRightClick(e))
					&& ((ob == table.getTableHeader()) || (ob == scrollPane))) {
				ConstructionProtocolContextMenu contextMenu = new ConstructionProtocolContextMenu(
						(AppD) app);
				contextMenu.show(view.scrollPane, e.getPoint().x,
						e.getPoint().y);

			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getSource() != table) {
				return;
			}
			int row = table.rowAtPoint(e.getPoint());
			if (row >= 0) { // init drag
				GeoElement geo = ((ConstructionTableDataD) data)
						.getGeoElement(row);
				dragIndex = geo.getConstructionIndex();
				minIndex = geo.getMinConstructionIndex();
				maxIndex = geo.getMaxConstructionIndex();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getSource() != table) {
				return;
			}
			if (!dragging) {
				table.repaint();
				return;
			}
			// drop
			int row = table.rowAtPoint(e.getPoint());
			if (row >= 0) {
				dropIndex = data.getConstructionIndex(row);
				boolean kernelChanged = ((ConstructionTableDataD) data)
						.moveInConstructionList(dragIndex, dropIndex);
				if (kernelChanged) {
					app.storeUndoInfo();
				}
			}
			// reinit vars
			dragging = false;
			dragIndex = -1;
			dropIndex = -1;
			table.setCursor(Cursor.getDefaultCursor());
			table.repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (e.getSource() != table || dragIndex == -1) {
				return;
			}

			int row = table.rowAtPoint(e.getPoint());
			int index = (row < 0) ? -1 : data.getConstructionIndex(row);
			// drop possible
			if (minIndex <= index && index <= maxIndex) {
				table.setCursor(DragSource.DefaultMoveDrop);
			}
			// drop impossible
			else {
				table.setCursor(DragSource.DefaultMoveNoDrop);
			}

			if (index != dropIndex) {
				dragging = true;
				dropIndex = index;
				table.repaint();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// do nothing
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// do nothing
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// do nothing
		}
	}

	public class ColumnKeeper implements ActionListener {
		protected TableColumn column;
		protected ColumnData colData;

		private boolean isBreakPointColumn;

		public ColumnKeeper(TableColumn column, ColumnData colData) {
			this.column = column;
			this.colData = colData;

			isBreakPointColumn = colData.getTitle().equals("Breakpoint");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
			TableColumnModel model = table.getColumnModel();

			if (!colData.isVisible()) {
				colData.setVisible(true);
				model.addColumn(column);
				// column is added at right end of model
				// move column to its default place
				int lastPos = model.getColumnCount() - 1;
				int pos = data.getColumnNumber(colData);
				if (pos >= 0 && pos < lastPos) {
					model.moveColumn(lastPos, pos);
				}
				scrollPane.setSize(
						scrollPane.getWidth() + column.getPreferredWidth(),
						scrollPane.getHeight());

				// show breakPointColumn => show all lines
				if (isBreakPointColumn) {
					kernel.getConstruction().setShowOnlyBreakpoints(false);
					// cbShowOnlyBreakpoints.setSelected(false);
				}
			} else {
				colData.setVisible(false);
				model.removeColumn(column);
				// setSize(getWidth() - column.getWidth(), getHeight());
				// setSize(view.getWidth(), getHeight());
			}
			table.tableChanged(new TableModelEvent(
					((ConstructionTableDataD) data).getImpl().getImpl()));

			// reinit view to update possible breakpoint changes
			((ConstructionTableDataD) data).initView();
			SwingUtilities.updateComponentTreeUI(view.scrollPane);
		}
	}

	class ConstructionTableCellEditor extends AbstractCellEditor
			implements TableCellEditor {

		private static final long serialVersionUID = 1L;

		InputPanelD inputPanel;
		GeoElement geo;

		@Override
		public boolean stopCellEditing() {
			super.stopCellEditing();
			// data.updateAll();
			return true;
		}

		@Override
		public Object getCellEditorValue() {
			return inputPanel.getText();
		}

		@Override
		public Component getTableCellEditorComponent(JTable table1,
				Object value, boolean isSelected, int rowIndex,
				int columnIndex) {

			geo = ((ConstructionTableDataD) data).getGeoElement(rowIndex);
			String val = geo
					.getCaptionDescription(StringTemplate.defaultTemplate);
			inputPanel = new InputPanelD("", (AppD) app, 20, false);
			inputPanel.setText(val);
			inputPanel.setEnabled(true);
			inputPanel.setVisible(true);
			return inputPanel;
		}

	}

	class ConstructionTableCellRenderer extends DefaultTableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = -9165858653728142643L;

		private JCheckBox cbTemp = new JCheckBox();
		private JLabel iTemp = new JLabel();

		public ConstructionTableCellRenderer() {
			setOpaque(true);
			setVerticalAlignment(TOP);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table1,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			// Boolean value: show as checkbox
			boolean isBoolean = value instanceof Boolean;

			boolean isImage = value instanceof ImageIcon;

			Component comp = isBoolean ? cbTemp : (Component) this;

			if (isBoolean) {
				comp = cbTemp;
			} else if (isImage) {
				comp = iTemp;
			} else {
				comp = this;
			}

			int step = kernel.getConstructionStep();
			RowData rd = data.getRow(row);
			int index = rd.getGeo().getConstructionIndex();
			if (useColors) {
				comp.setForeground(
						GColorD.getAwtColor(rd.getGeo().getAlgebraColor()));
			} else {
				comp.setForeground(Color.black);
			}

			if (index == step) { // current construction step background color
				comp.setBackground(COLOR_STEP_HIGHLIGHT);
			} else if (index < step) {
				comp.setBackground(Color.white);
			} else {
				comp.setForeground(Color.gray);
				comp.setBackground(Color.white);
			}

			// set background color
			if (dragging) {
				if (index == dragIndex) { // drag & drop background color
					comp.setBackground(COLOR_DRAG_HIGHLIGHT);
				} else if (index == dropIndex) { // drag & drop background color
					comp.setBackground(COLOR_DROP_HIGHLIGHT);
				}
			}

			comp.setFont(table1.getFont());

			if (isBoolean) {
				cbTemp.setSelected(((Boolean) value).booleanValue());
				cbTemp.setEnabled(true);
				return cbTemp;
			}
			if (isImage) {
				/*
				 * Scaling does not work yet. I wonder why. Image miniImage =
				 * ((ImageIcon) value).getImage().getScaledInstance(16,16,0);
				 * ImageIcon miniIcon = new ImageIcon();
				 * miniIcon.setImage(miniImage); iTemp.setIcon((ImageIcon)
				 * value); iTemp.setHorizontalAlignment(JLabel.CENTER);
				 * iTemp.setMaximumSize(new Dimension(16,16)); return iTemp;
				 */
				iTemp.setIcon((ImageIcon) value);
				return iTemp;
			}

			setText((value == null) ? "" : value.toString());
			return this;

		}
	}

	class HeaderRenderer extends JLabel implements TableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8149210120003929698L;

		public HeaderRenderer() {
			setOpaque(true);
			// setForeground(UIManager.getColor("TableHeader.foreground"));
			// setBackground(UIManager.getColor("TableHeader.background"));
			// setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			// better for Macs?
			setForeground(Color.black);
			setBackground(GColorD.getAwtColor(
					GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER));
			// setBorder(BorderFactory.createBevelBorder(0));
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, GColorD
					.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR)));

		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			setFont(table.getFont());
			setText((value == null) ? "" : " " + value.toString());

			if ((row == -1) && (column == 0)) { // No. column header
				int width_header = getPreferredSize().width + 2;
				int width_numbers = 25;

				if (data.getRowCount() > 0) {
					TableCellRenderer r = table
							.getCellRenderer(data.getRowCount() - 1, 0);
					Component c = r.getTableCellRendererComponent(table,
							((ConstructionTableDataD) data)
									.getValueAt(data.getRowCount() - 1, 0),
							false, false, data.getRowCount() - 1, 0);
					// width = Math.max(width, c.getPreferredSize().width +2);
					width_numbers = Math.max(width_numbers,
							c.getPreferredSize().width + 2);
				}

				// tableColumns[0].setMaxWidth(width);
				tableColumns[0].setMinWidth(width_numbers);
				tableColumns[0]
						.setMaxWidth(Math.max(width_numbers, width_header));
				// tableColumns[0].setPreferredWidth(width);
			}

			return this;
		}
	}

	public class ConstructionTableDataD extends ConstructionTableData {

		protected MyGAbstractTableModel ctDataImpl;
		protected ConstructionTableData ctData = this;

		public ConstructionTableDataD(SetLabels gui) {
			super(gui);
			ctDataImpl = new MyGAbstractTableModel();
			// rowList = new ArrayList<RowData>();
			// geoMap = new HashMap<GeoElement, RowData>();
		}

		public GAbstractTableModelD getImpl() {
			return ctDataImpl;
		}

		boolean moveInConstructionList(int fromIndex, int toIndex) {
			// kernel.detach(this);
			boolean changed = kernel.moveInConstructionList(fromIndex, toIndex);
			// kernel.attach(this);

			// reorder rows in this view
			ConstructionElement ce = kernel.getConstructionElement(toIndex);
			GeoElementND[] geos = ce.getGeoElements();
			for (int i = 0; i < geos.length; ++i) {
				remove(geos[i].toGeoElement());
				add(geos[i].toGeoElement());
			}
			return changed;
		}

		public GeoElement getGeoElement(int row) {
			return rowList.get(row).getGeo();
		}

		@Override
		public void repaintView() {
			repaintScrollpane();
		}

		private Color getColorAt(int nRow, int nCol) {
			try {
				if (useColors) {
					return GColorD.getAwtColor(
							rowList.get(nRow).getGeo().getAlgebraColor());
				}
				return Color.black;
			} catch (Exception e) {
				return Color.black;
			}
		}

		public Object getValueAt(int nRow, int nCol) {
			if (nRow < 0 || nRow >= getRowCount()) {
				return "";
			}
			switch (nCol) {
			case 0:
				return rowList.get(nRow).getIndex() + "";
			case 1:
				return rowList.get(nRow).getName();
			case 2:
				GImageIconD toolbarIcon = (GImageIconD) rowList.get(nRow)
						.getToolbarIcon();
				return (toolbarIcon == null) ? null : toolbarIcon.getImpl();
			case 3:
				return rowList.get(nRow).getDescription();
			case 4:
				return rowList.get(nRow).getDefinition();
			case 5:
				return rowList.get(nRow).getAlgebra();
			case 7:
				return Boolean.valueOf(rowList.get(nRow).getCPVisible());
			case 6:
				return rowList.get(nRow).getCaption();
			}
			return "";
		}

		public void repaint() {
			table.repaint();
		}

		@Override
		public void updateAll() {
			if (isNotifyUpdateCalled()) {
				return;
			}
			int size = rowList.size();

			int toolbarIconHeight = 0;
			// If displaying toolbarIcon is set, row height must be at least 32
			// + 1:
			if (isColumnInModel(tableColumns[1])) {
				toolbarIconHeight = 32 + 1;
				/*
				 * FIXME: The cell content is not aligned vertically centered. I
				 * don't think it is possible to easily solve this because
				 * JTable does not offer a convenient way for vertical alignment
				 * of the cell content. Probably
				 * http://articles.techrepublic.com.com/5100-10878_11-5032692.
				 * html may help, or to use the same technique which is
				 * introduced in GeoGebraCAS.
				 */
			}

			for (int i = 0; i < size; ++i) {
				RowData row = rowList.get(i);
				row.updateAll();

				// it seems there isn't fit to content (automatic) row height in
				// JTable,
				// so we use the most frequent option, 2 lines of text in a row
				// (this is still better than 1 lines of text in a row)
				if (row.getIncludesIndex()) {
					table.setRowHeight(i,
							Math.max((table.getFont().getSize() * 2 + 16),
									toolbarIconHeight));
				} else {
					table.setRowHeight(i,
							Math.max((table.getFont().getSize() * 2 + 12),
									toolbarIconHeight));
				}
			}
			ctDataImpl.fireTableRowsUpdated(0, size - 1);
		}

		@Override
		protected void fireTableRowsUpdated(int rowNumber, int rowNumber2) {
			ctDataImpl.fireTableRowsUpdated(rowNumber, rowNumber2);
		}

		@Override
		final public void updateVisualStyle(GeoElement geo, GProperty prop) {
			update(geo);
		}

		@Override
		final public void updateAuxiliaryObject(GeoElement geo) {
			// update(geo);
		}

		private class ColumnMovementListener
				implements TableColumnModelListener {

			@Override
			public void columnAdded(TableColumnModelEvent e) {
				columnsCount++;
			}

			@Override
			public void columnRemoved(TableColumnModelEvent e) {
				columnsCount--;
			}

			@Override
			public void columnMarginChanged(ChangeEvent e) {
				// only handle removed / added
			}

			@Override
			public void columnMoved(TableColumnModelEvent e) {
				// only handle removed / added
			}

			@Override
			public void columnSelectionChanged(ListSelectionEvent e) {
				// only handle removed / added
			}
		}

		@Override
		public void reset() {
			repaint();
		}

		public void setValueAt(Object value, int row, int col) {

			if ((this.columns[col].getTitle()).equals("Caption")) {
				data.getRow(row).getGeo().setCaption(value.toString());
				data.getRow(row).getGeo().update();
				// updateAll();
				kernel.notifyRepaint();
			}
		}

		@Override
		public int getViewID() {
			return App.VIEW_CONSTRUCTION_PROTOCOL;
		}

		@Override
		public boolean hasFocus() {
			return false;
		}

		public class MyGAbstractTableModel extends GAbstractTableModelD {

			@Override
			public int getRowCount() {
				return ctData.getRowCount();
			}

			@Override
			public Object getValueAt(int nRow, int nCol) {
				return ((ConstructionTableDataD) ctData).getValueAt(nRow, nCol);
			}

			@Override
			public int getColumnCount() {
				return ctData.getColumnCount();
			}

			@Override
			public boolean isCellEditable(int nRow, int nCol) {
				return ctData.isCellEditable(nCol);
			}

			@Override
			public void setValueAt(Object value, int row, int col) {
				((ConstructionTableDataD) ctData).setValueAt(value, row, col);
			}

		}

		@Override
		public void fireTableRowsDeleted(int firstRow, int lastRow) {
			ctDataImpl.fireTableRowsDeleted(firstRow, lastRow);
		}

		@Override
		public void fireTableRowsInserted(int firstRow, int lastRow) {
			ctDataImpl.fireTableRowsInserted(firstRow, lastRow);
		}

	}

	/************
	 * PRINTING *
	 ************/

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex0)
			throws PrinterException {

		int pageIndex = ((AppD) kernel.getApplication()).getPrintPreview()
				.adjustIndex(pageIndex0);

		if (!isViewAttached) {
			data.clearView();
			data.notifyAddAll(kernel.getConstruction().getStep());

			JFrame tempFrame = new JFrame();
			tempFrame.add(this.scrollPane);
			tempFrame.pack();
		}
		int r = table.getPrintable(PrintMode.FIT_WIDTH, null, null)
				.print(graphics, pageFormat, pageIndex);

		return r;
	}

	/*
	 * public int print(Graphics pg, PageFormat pageFormat, int pageIndex)
	 * throws PrinterException { Application.debug("CPView-print"); if
	 * (pageIndex >= maxNumPage) return NO_SUCH_PAGE;
	 * 
	 * pg.translate((int) pageFormat.getImageableX(), (int)
	 * pageFormat.getImageableY()); int wPage = (int)
	 * pageFormat.getImageableWidth(); int hPage = (int)
	 * pageFormat.getImageableHeight(); pg.setClip(0, 0, wPage, hPage);
	 * 
	 * // construction title int y = 0; Font titleFont =
	 * table.getFont().deriveFont(Font.BOLD, table.getFont().getSize() + 2);
	 * pg.setFont(titleFont); pg.setColor(Color.black); // Font fn =
	 * pg.getFont(); FontMetrics fm = pg.getFontMetrics();
	 * 
	 * // title Construction cons = kernel.getConstruction(); String title =
	 * cons.getTitle(); if (!"".equals(title)) { y += fm.getAscent();
	 * pg.drawString(title, 0, y); }
	 * 
	 * // construction author and date String author = cons.getAuthor(); String
	 * date = cons.getDate(); String line = null; if (!"".equals(author)) { line
	 * = author; } if (!"".equals(date)) { if (line == null) line = date; else
	 * line = line + " - " + date; } if (line != null) {
	 * pg.setFont(table.getFont()); // fn = pg.getFont(); fm =
	 * pg.getFontMetrics(); y += fm.getHeight(); pg.drawString(line, 0, y); }
	 * 
	 * y += 20; // space between title and table headers
	 * 
	 * Font headerFont = table.getFont().deriveFont(Font.BOLD);
	 * pg.setFont(headerFont); fm = pg.getFontMetrics();
	 * 
	 * TableColumnModel colModel = table.getColumnModel(); int nColumns =
	 * colModel.getColumnCount(); int x[] = new int[nColumns]; x[0] = 0;
	 * 
	 * int h = fm.getAscent(); y += h; // add ascent of header font because of
	 * baseline
	 * 
	 * int nRow, nCol; for (nCol = 0; nCol < nColumns; nCol++) { TableColumn tk
	 * = colModel.getColumn(nCol); int width = tk.getWidth(); // only print
	 * column if there is enough space for it // if (x[nCol] + width > wPage) {
	 * // nColumns = nCol; // break; // } if (nCol + 1 < nColumns) x[nCol + 1] =
	 * x[nCol] + width; title = (String) tk.getIdentifier();
	 * pg.drawString(title, x[nCol], y); }
	 * 
	 * Font tableFont = table.getFont(); pg.setFont(tableFont); fm =
	 * pg.getFontMetrics();
	 * 
	 * int header = y; h = fm.getHeight(); int rowH = Math.max(h, 10); int
	 * rowPerPage = (hPage - header) / rowH; maxNumPage = Math.max( (int)
	 * Math.ceil(table.getRowCount() / (double) rowPerPage), 1);
	 * 
	 * // TableModel tblModel = table.getModel(); int iniRow = pageIndex *
	 * rowPerPage; int endRow = Math.min(table.getRowCount(), iniRow +
	 * rowPerPage); int yAdd, maxYadd = 0;
	 * 
	 * for (nRow = iniRow; nRow < endRow; nRow++) { y = y + h + maxYadd; //
	 * maxYadd is additional space for indices of // last line maxYadd = 0; for
	 * (nCol = 0; nCol < nColumns; nCol++) { int col =
	 * table.getColumnModel().getColumn(nCol) .getModelIndex(); // Object obj =
	 * data.getValueAt(nRow, col); // String str = obj.toString(); //
	 * pg.drawString(str, x[nCol], y); String str = data.getPlainTextAt(nRow,
	 * col); pg.setColor(data.getColorAt(nRow, col)); yAdd =
	 * Drawable.drawIndexedString(app, (Graphics2D) pg, str, x[nCol], y).y; if
	 * (yAdd > maxYadd) maxYadd = yAdd; } }
	 * 
	 * return PAGE_EXISTS; }
	 */
	/***************
	 * HTML export *
	 ***************/

	/**
	 * @param app
	 *            app
	 * @param sb
	 *            sb
	 * @return .ggb file encoded as base64 string
	 */
	public static boolean appendBase64(AppD app, StringBuilder sb) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			app.getXMLio().writeGeoGebraFile(baos, false);
			sb.append(Base64.encodeToString(baos.toByteArray(), false));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * public void add(GeoElement geo) { // TODO Auto-generated method stub }
	 * 
	 * public void remove(GeoElement geo) { // TODO Auto-generated method stub }
	 * 
	 * public void rename(GeoElement geo) { // TODO Auto-generated method stub }
	 * 
	 * public void update(GeoElement geo) { // TODO Auto-generated method stub
	 * data.update(geo); }
	 * 
	 * public void updateAuxiliaryObject(GeoElement geo) { // TODO
	 * Auto-generated method stub }
	 * 
	 * public void repaintView() { // TODO Auto-generated method stub
	 * data.repaintView(); }
	 * 
	 * public void reset() { // TODO Auto-generated method stub }
	 * 
	 * public void clearView() { // TODO Auto-generated method stub }
	 * 
	 * public void setMode(int mode) { // TODO Auto-generated method stub }
	 * 
	 * public void attachView() { kernel.notifyAddAll(this);
	 * kernel.attach(this); }
	 */

	@Override
	public void settingsChanged(AbstractSettings settings) {
		ConstructionProtocolSettings cps = (ConstructionProtocolSettings) settings;

		boolean gcv[] = cps.getColsVisibility();
		if (gcv != null) {
			if (gcv.length > 0) {
				setColsVisibility(gcv);
			}
		}

		update();
		((ConstructionTableDataD) getData()).initView();
		repaintScrollpane();

	}

	private void setColsVisibility(boolean[] colsVisibility) {
		TableColumnModel model = table.getColumnModel();

		int k = Math.min(colsVisibility.length, data.columns.length);

		for (int i = 0; i < k; i++) {
			TableColumn column = getTableColumns()[i];
			model.removeColumn(column);
			if (colsVisibility[i]) {
				model.addColumn(column);
			}
			// else {
			// model.removeColumn(column);
			// }

			data.columns[i].setVisible(colsVisibility[i]);
		}
	}

	@Override
	public void setLabels() {
		this.initGUI();
	}

	public ArrayList<Columns> getColumns() {
		int n = table.getColumnModel().getColumnCount();
		ArrayList<Columns> columns = new ArrayList<>();

		for (int i = 0; i < n; i++) {
			TableColumn col = table.getColumnModel().getColumn(i);
			columns.add(Columns.lookUp(col.getHeaderValue() + "", loc));
		}

		return columns;

	}

}
