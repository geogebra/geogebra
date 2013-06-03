package geogebra.gui.view.consprotocol;
/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

import geogebra.common.javax.swing.table.GAbstractTableModel;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.ConstructionProtocolSettings;
import geogebra.common.main.settings.SettingListener;
import geogebra.common.util.StringUtil;
import geogebra.gui.GuiManagerD;
import geogebra.gui.TitlePanel;
import geogebra.gui.view.algebra.InputPanelD;
import geogebra.javax.swing.GImageIconD;
import geogebra.javax.swing.table.GAbstractTableModelD;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

public class ConstructionProtocolView extends geogebra.common.gui.view.consprotocol.ConstructionProtocolView implements Printable, ActionListener, SettingListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1152223555575098008L;
	private static Color COLOR_STEP_HIGHLIGHT = AppD.COLOR_SELECTION;
	private static Color COLOR_DRAG_HIGHLIGHT = new Color(250, 250, 200);
	private static Color COLOR_DROP_HIGHLIGHT = Color.lightGray;

	JTable table;
	public JPanel cpPanel;

	private TableColumn[] tableColumns;

	//private AbstractAction printPreviewAction, exportHtmlAction;

	private boolean useColors, addIcons;

	// for drag & drop
	private boolean dragging = false;
	private int dragIndex = -1; // dragged construction index
	private int dropIndex = -1;

	public ArrayList<ConstructionProtocolNavigation> navigationBars = new ArrayList<ConstructionProtocolNavigation>();
	public ConstructionProtocolNavigation protNavBar; // navigation bar of
														// protocol window
	private ConstructionProtocolView view=this;
	public JScrollPane scrollPane;
	private ConstructionProtocolStyleBar helperBar;
	private AbstractAction exportHtmlAction, printPreviewAction;
	
	public ConstructionProtocolView(final AppD app) {
		cpPanel = new JPanel(new BorderLayout());
		
		this.app = app;
		kernel = app.getKernel();
		data = new ConstructionTableData();
		useColors = true;
		addIcons = false;

		table = new JTable();
		table.setAutoCreateColumnsFromModel(false);
		table.setModel(((GAbstractTableModelD) data.getImpl()).getImpl());
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
			if (data.columns[k].getInitShow())
				table.addColumn(tableColumns[k]);
			if (data.columns[k].getTitle()=="Caption"){
				tableColumns[k].setCellEditor(new ConstructionTableCellEditor());
			}
		}
		// first column "No." should have fixed width
		tableColumns[0].setMaxWidth(tableColumns[0].getMinWidth());

		table.getColumnModel().addColumnModelListener(
				((ConstructionTableData)data).new ColumnMovementListener());

		scrollPane = new JScrollPane(table);
		scrollPane.getViewport().setBackground(Color.white);
		cpPanel.add(scrollPane, BorderLayout.CENTER);
		
		// clicking
		ConstructionMouseListener ml = new ConstructionMouseListener();
		table.addMouseListener(ml);
		table.addMouseMotionListener(ml);
		header.addMouseListener(ml); // double clicking, right-click menu
		scrollPane.addMouseListener(ml); //right-click menu
		
		// keys
		ConstructionKeyListener keyListener = new ConstructionKeyListener();
		table.addKeyListener(keyListener);

		// navigation bar
		protNavBar = (ConstructionProtocolNavigation) app.getConstructionProtocolNavigation();
		protNavBar.register(this);
		//protNavBar.setPlayButtonVisible(false);
		//protNavBar.setConsProtButtonVisible(false);
		//add(protNavBar, BorderLayout.SOUTH);
		//Util.addKeyListenerToAll(protNavBar, keyListener);
		
		
		initGUI();
		initActions();

		ConstructionProtocolSettings cps = app.getSettings().getConstructionProtocol();
		settingsChanged(cps);
		cps.addListener(this);
		
	}
	
	public App getApp(){
		return app;
	}
	
	public JPanel getCpPanel(){
		return cpPanel;
	}

	public int getConstructionIndex(int row){
		return data.getConstructionIndex(row);
	}
	
	public AppD getApplication() {
		return (AppD)app;
	}

	public void registerNavigationBar(ConstructionProtocolNavigation nb) {
		if (!navigationBars.contains(nb)) {
			navigationBars.add(nb);
			((ConstructionTableData)data).attachView();
		}
	}

	public void unregisterNavigationBar(ConstructionProtocolNavigation nb) {
		navigationBars.remove(nb);
		((ConstructionTableData)data).detachView(); // only done if there are no more navigation bars
	}

	public void initProtocol() {
		if (!isViewAttached)
			((ConstructionTableData)data).initView();
	}
	
	@Override
	protected void updateNavigationBars() {
		// update the navigation bar of the protocol window
		protNavBar.update();
	
		// update all registered navigation bars
		int size = navigationBars.size();
		for (int i = 0; i < size; i++) {
			navigationBars.get(i).update();
		}
	}	
	
	@Override
	protected void repaint(){
		cpPanel.repaint();
	}

	/**
	 * inits GUI with labels of current language
	 */
	public void initGUI() {
		//setTitle(app.getPlain("ConstructionProtocol"));
		cpPanel.setFont(((AppD)app).getPlainFont());
		//setMenuBar();
		getStyleBar().setLabels();
		// set header values (language may have changed)
		for (int k = 0; k < tableColumns.length; k++) {
			tableColumns[k].setHeaderValue(data.columns[k].getTranslatedTitle());
		}
		table.updateUI();
		table.setFont(((AppD)app).getPlainFont());
		((ConstructionTableData) data).updateAll();
	}

	public void setUseColors(boolean flag) {
		useColors = flag;
		((ConstructionTableData) data).updateAll();
	}

	public void setAddIcons(boolean flag) {
		addIcons = flag;
		((ConstructionTableData) data).updateAll();
	}

	public boolean getAddIcons(){
		return addIcons;
	}
	
	// Michael Borcherds 2008-05-15
	public void update() {
		((ConstructionTableData) data).updateAll();
	}
	
	public TableColumn[] getTableColumns(){
		return tableColumns;
	}
	
	public boolean getUseColors(){
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
			((ConstructionTableData) data).attachView();
		} else {
			((ConstructionTableData) data).detachView();
		}
		cpPanel.setVisible(flag);
	}

	public void scrollToConstructionStep() {
		int rowCount = table.getRowCount();
		if (rowCount == 0)
			return;

		int step = kernel.getConstructionStep();
		int row = 0;
		for (int i = Math.max(step, 0); i < rowCount; i++) {
			if (data.getConstructionIndex(i) <= step)
				row = i;
			else
				break;
		}

		table.setRowSelectionInterval(row, row);
		table.repaint();
	}

	public JTable getTable(){
		return table;
	}
	
	public ConstructionTableData getData(){
		return (ConstructionTableData) data;
	}
	
	public AbstractAction getExportHtmlAction(){
		return exportHtmlAction;
	}

	public AbstractAction getPrintPreviewAction(){
		return printPreviewAction;
	}
	
	
	/**
	 * @return The style bar for this view.
	 */
	public ConstructionProtocolStyleBar getStyleBar() {
		if(helperBar == null) {
			helperBar = newConstructionProtocolHelperBar();
		}
		
		return helperBar;
	}
	
	/**
	 * @return new Construction Protocol style bar
	 */
	protected ConstructionProtocolStyleBar newConstructionProtocolHelperBar(){
		return new ConstructionProtocolStyleBar(this, (AppD)app);
	}
	
	private void initActions() {

		exportHtmlAction = new AbstractAction(app.getPlain("ExportAsWebpage")
				+ " (" + AppD.FILE_EXT_HTML + ") ...") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();

				Thread runner = new Thread() {
					@Override
					public void run() {
						JDialog d = new geogebra.export.ConstructionProtocolExportDialog(view);
						d.setVisible(true);
					}
				};
				runner.start();

				app.setDefaultCursor();
			}
		};
		
		
		printPreviewAction = new AbstractAction(app.getMenu("Print")
				+ "...", ((AppD)app).getImageIcon("document-print-preview.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();

				Thread runner = new Thread() {
					@Override
					public void run() {
						
						try {
							Construction cons = app.getKernel().getConstruction();
							getTable().print(JTable.PrintMode.FIT_WIDTH, 
									new MessageFormat(tableHeader(cons)), 
									new MessageFormat("{0}"), // page numbering 
									/*showPrintDialog*/ true, 
									/*attr*/ null, 
									/*interactive*/ true /*,*/ 
									/*service*/ /*null*/);
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
					
						TitlePanel tp = new TitlePanel((AppD)app);
						String author = tp.loadAuthor();
						String title = cons.getTitle();
						String date = tp.configureDate(cons.getDate());
						
						if (title.equals(""))
							title = app.getPlain("UntitledConstruction");
						if (author.equals(""))
							return title + " (" + date + ")";
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
			case KeyEvent.VK_DELETE:
				ConstructionElement ce = kernel.getConstructionElement(kernel
						.getConstructionStep());
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

	class ConstructionMouseListener implements MouseListener,
			MouseMotionListener {

		// smallest and larges possible construction index for dragging
		private int minIndex, maxIndex;

		public void mouseClicked(MouseEvent e) {
			Object ob = e.getSource();
			if (ob == table) {
				Point origin = e.getPoint();
				geogebra.common.awt.GPoint mouseCoords = new geogebra.common.awt.GPoint(e.getPoint().x,e.getPoint().y);
				int row = table.rowAtPoint(origin);
				if (row < 0)
					return;

				// right click
				if (AppD.isRightClick(e)) {
					GeoElement geo = ((ConstructionTableData) data).getGeoElement(row);
					ArrayList<GeoElement> temp = new ArrayList<GeoElement>();
					temp.add(geo);
					((GuiManagerD)app.getGuiManager()).showPopupMenu(temp, table, mouseCoords);
				} else { // left click

					if (e.getClickCount() == 1) {

						// click on breakpoint column?
						int column = table.columnAtPoint(origin);
						String colName = table.getColumnName(column);

						//if (colName.equals("Breakpoint")) {
						if (colName.equals("H")) {
							RowData rd = (RowData) data.getRow(row);
							GeoElement geo = rd.getGeo();
							boolean newVal = !geo.isConsProtocolBreakpoint();
							geo.setConsProtocolBreakpoint(newVal);

							// update only current row
							rd.updateAll();

							if (kernel.getConstruction().showOnlyBreakpoints() && !newVal) {
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
			} else if (ob == table.getTableHeader()&&(e.getClickCount() == 2)) {
				setConstructionStep(-1);
				table.repaint();
			} else if ((e.getClickCount() == 1)&&(AppD.isRightClick(e))&&((ob == table.getTableHeader())||(ob == scrollPane))){
				ConstructionProtocolContextMenu contextMenu = new ConstructionProtocolContextMenu((AppD)app);
				contextMenu.show(view.cpPanel, e.getPoint().x, e.getPoint().y);
				
			}
		}

		public void mousePressed(MouseEvent e) {
			if (e.getSource() != table)
				return;
			int row = table.rowAtPoint(e.getPoint());
			if (row >= 0) { // init drag
				GeoElement geo = ((ConstructionTableData) data).getGeoElement(row);
				dragIndex = geo.getConstructionIndex();
				minIndex = geo.getMinConstructionIndex();
				maxIndex = geo.getMaxConstructionIndex();
			}
		}

		public void mouseReleased(MouseEvent e) {			
			if (e.getSource() != table)
				return;		
			if (!dragging){
				table.repaint();
				return;
			}		
			// drop
			int row = table.rowAtPoint(e.getPoint());
			if (row >= 0) {
				dropIndex = data.getConstructionIndex(row);
				boolean kernelChanged = ((ConstructionTableData) data).moveInConstructionList(dragIndex,
						dropIndex);
				if (kernelChanged)
					app.storeUndoInfo();
			}
			// reinit vars
			dragging = false;
			dragIndex = -1;
			dropIndex = -1;
			table.setCursor(Cursor.getDefaultCursor());
			table.repaint();
		}

		public void mouseDragged(MouseEvent e) {
			if (e.getSource() != table || dragIndex == -1)
				return;

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

		public void mouseMoved(MouseEvent e) {
			//do nothing
		}

		public void mouseEntered(MouseEvent e) {
			//do nothing
		}

		public void mouseExited(MouseEvent arg0) {
			//do nothing
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

		public void actionPerformed(ActionEvent e) {
			//JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
			TableColumnModel model = table.getColumnModel();
			
			if (colData.isVisible() == false){
				colData.setVisible(true);
				model.addColumn(column);
				// column is added at right end of model
				// move column to its default place
				int lastPos = model.getColumnCount() - 1;
				int pos = data.getColumnNumber(colData);
				if (pos >= 0 && pos < lastPos)
					model.moveColumn(lastPos, pos);
				cpPanel.setSize(cpPanel.getWidth() + column.getPreferredWidth(), cpPanel.getHeight());

				// show breakPointColumn => show all lines
				if (isBreakPointColumn) {
					kernel.getConstruction().setShowOnlyBreakpoints(false);
					//cbShowOnlyBreakpoints.setSelected(false);
				}
			} else {
				colData.setVisible(false);
				model.removeColumn(column);
				//setSize(getWidth() - column.getWidth(), getHeight());
				//setSize(view.getWidth(), getHeight());
			}
			table.tableChanged(new TableModelEvent(((GAbstractTableModelD) data.getImpl()).getImpl()));

			// reinit view to update possible breakpoint changes
			((ConstructionTableData) data).initView();
			SwingUtilities.updateComponentTreeUI(view.cpPanel);
		}
	}

	class ConstructionTableCellEditor extends AbstractCellEditor implements TableCellEditor {

		private static final long serialVersionUID = 1L;
		
		InputPanelD inputPanel;
		GeoElement geo;
		
		@Override
		public boolean stopCellEditing(){
			super.stopCellEditing();
			//data.updateAll();
			return true;
		}
		
		public Object getCellEditorValue() {
			return inputPanel.getText();
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int rowIndex, int columnIndex) {
			
			geo = ((ConstructionTableData) data).getGeoElement(rowIndex);
			String val = geo.getCaptionDescription(StringTemplate.defaultTemplate);		
			inputPanel = new InputPanelD("", (AppD)app, 20,false);				
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
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			// Boolean value: show as checkbox
			boolean isBoolean = value instanceof Boolean;
			
			boolean isImage = value instanceof ImageIcon;
			
			Component comp = isBoolean ? cbTemp : (Component) this;
			
			if (isBoolean)
				comp = cbTemp;
			else if (isImage)
				comp = iTemp;
			else
				comp = this;
			
			int step = kernel.getConstructionStep();
			RowData rd = (RowData) data.getRow(row);
			int index = rd.getGeo().getConstructionIndex();
			if (useColors)
				comp.setForeground(geogebra.awt.GColorD.getAwtColor(rd.getGeo().getObjectColor()));
			else
				comp.setForeground(Color.black);

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

			comp.setFont(table.getFont());

			if (isBoolean) {
				cbTemp.setSelected(((Boolean) value).booleanValue());
				cbTemp.setEnabled(true);
				return cbTemp;
			}
			if (isImage) {
				/* Scaling does not work yet. I wonder why.
				Image miniImage = ((ImageIcon) value).getImage().getScaledInstance(16,16,0);
				ImageIcon miniIcon = new ImageIcon();
				miniIcon.setImage(miniImage);
				iTemp.setIcon((ImageIcon) value);
				iTemp.setHorizontalAlignment(JLabel.CENTER);
				iTemp.setMaximumSize(new Dimension(16,16));
				return iTemp;
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
			setBackground(geogebra.awt.GColorD.getAwtColor(GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER));
			// setBorder(BorderFactory.createBevelBorder(0));
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1,
					geogebra.awt.GColorD.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR)));

		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			setFont(table.getFont());
			setText((value == null) ? "" : " " + value.toString());
			
			if((row==-1)&&(column==0)){  //No. column header
				int width_header = getPreferredSize().width + 2;
				int width_numbers = 25;
				
				if(data.getRowCount()>0){
					TableCellRenderer r = table.getCellRenderer(data.getRowCount()-1, 0);
					Component c = r.getTableCellRendererComponent(table, ((ConstructionTableData) data).getValueAt(data.getRowCount()-1, 0), false, false,
							data.getRowCount()-1, 0);
					//width = Math.max(width, c.getPreferredSize().width +2);
					width_numbers = Math.max(width_numbers, c.getPreferredSize().width +2);
				}
				
				//tableColumns[0].setMaxWidth(width);
				tableColumns[0].setMinWidth(width_numbers);
				tableColumns[0].setMaxWidth(Math.max(width_numbers, width_header));
				//tableColumns[0].setPreferredWidth(width);
			}	
			
			
			return this;
		}
	}

	public class ConstructionTableData
			extends
			geogebra.common.gui.view.consprotocol.ConstructionProtocolView.ConstructionTableData {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6933858200673625046L;

		protected MyGAbstractTableModel ctDataImpl;
		
		public ConstructionTableData() {
			super();
			ctDataImpl = new MyGAbstractTableModel();
//			rowList = new ArrayList<RowData>();
//			geoMap = new HashMap<GeoElement, RowData>();
		}
		
		public GAbstractTableModel getImpl(){
			return ctDataImpl;
		}
		
		boolean moveInConstructionList(int fromIndex, int toIndex) {
			//kernel.detach(this);
			boolean changed = kernel.moveInConstructionList(fromIndex, toIndex);
			//kernel.attach(this);

			// reorder rows in this view
			ConstructionElement ce = kernel.getConstructionElement(toIndex);
			GeoElement[] geos = ce.getGeoElements();
			for (int i = 0; i < geos.length; ++i) {
				remove(geos[i]);
				add(geos[i]);
			}
			return changed;
		}

		public GeoElement getGeoElement(int row) {
			return rowList.get(row).getGeo();
		}

		public void initView() {
			// init view
			rowList.clear();
			geoMap.clear();
			notifyAddAll(kernel.getLastConstructionStep());
		}
		private boolean notifyUpdateCalled;
		private void notifyAddAll(int lastConstructionStep) {
			notifyUpdateCalled = true;
			kernel.notifyAddAll(this,kernel.getLastConstructionStep());
			notifyUpdateCalled = false;
			updateAll();
			
		}

		public void attachView() {
			if (!isViewAttached) {
				kernel.attach(this);
				initView();
				isViewAttached = true;
			}

			scrollToConstructionStep();
		}

		public void detachView() {
			// only detach view if there are
			// no registered navitagion bars
			if (isViewAttached && navigationBars.size() == 0) {
				// clear view
				rowList.clear();
				geoMap.clear();
				kernel.detach(this);
				isViewAttached = false;

				// side effect: go to last construction step
				setConstructionStep(kernel.getLastConstructionStep());
			}
		}

		private Color getColorAt(int nRow, int nCol) {
			try {
				if (useColors)
					return geogebra.awt.GColorD.getAwtColor(rowList.get(nRow).getGeo().getObjectColor());
				else
					return Color.black;
			} catch (Exception e) {
				return Color.black;
			}
		}

		public Object getValueAt(int nRow, int nCol) {
			if (nRow < 0 || nRow >= getRowCount())
				return "";
			switch (nCol) {
			case 0:
				return rowList.get(nRow).getIndex() + "";
			case 1:
				return rowList.get(nRow).getName();
			case 2:
				return ((GImageIconD) (rowList.get(nRow).getToolbarIcon())).getImpl();
			case 3:
				return rowList.get(nRow).getDefinition();
			case 4:
				return rowList.get(nRow).getCommand();
			case 5:
				return rowList.get(nRow).getAlgebra();
			case 7:
				return rowList.get(nRow).getCPVisible();
			case 6:
				return rowList.get(nRow).getCaption();
			}
			return "";
		}

		// no html code but plain text
		public String getPlainTextAt(int nRow, int nCol) {
			if (nRow < 0 || nRow >= getRowCount())
				return "";
			switch (nCol) {
			case 0:
				return ""
						+ (rowList.get(nRow).getGeo()
								.getConstructionIndex() + 1);
			case 1:
				return "";
			case 2:
				return rowList.get(nRow).getGeo().getNameDescription();
			case 3:
				return rowList.get(nRow).getGeo()
						.getDefinitionDescription(StringTemplate.defaultTemplate);
			case 4:
				return rowList.get(nRow).getGeo()
						.getCommandDescription(StringTemplate.defaultTemplate);
			case 5:
				return rowList.get(nRow).getGeo()
						.getAlgebraDescriptionDefault();
			case 7:
				return rowList.get(nRow).getCPVisible()
						.toString();
			}
			return "";
		}

		// html code without <html> tags
		public String getPlainHTMLAt(int nRow, int nCol, String thisPath) {
			
			/* Only one toolbar should be displayed for each step,
			 * even if multiple substeps are present in a step (i.e. more rows).
			 * For that, we calculate the index for the current and the previous row
			 * and check if they are equal.
			 */
			int index;
			int prevIndex;

			index = (nRow < 0) ? -1 : data.getConstructionIndex(nRow);
			prevIndex = (nRow < 1) ? -1 : data.getConstructionIndex(nRow - 1);

			
			if (nRow < 0 || nRow >= getRowCount())
				return "";
			switch (nCol) {
			case 0:
				return ""
						+ (rowList.get(nRow).getGeo()
								.getConstructionIndex() + 1);
			case 1:
				return rowList.get(nRow).getGeo()
						.getNameDescriptionHTML(false, false);

			case 2: { // Displaying toolbar icons in the list on demand.

				int m;
				// Markus' idea to find the correct icon:
				// 1) check if an object has a parent algorithm:
				GeoElement ge = rowList.get(nRow).getGeo();
				if (ge.getParentAlgorithm() != null) {
					// 2) if it has a parent algorithm and its modeID returned
					// is > -1, then use this one:
					m = ge.getParentAlgorithm()
							.getRelatedModeID();
				}
				// 3) otherwise use the modeID of the GeoElement itself:
				else
					m = rowList.get(nRow).getGeo().getRelatedModeID();

				if (m == -1 || index == prevIndex)
					return "";

				/*
				 * Hopefully the mode icons are detected correctly in the
				 * Algo*.java files. If not, here are the steps how to fix an
				 * incorrect icon:
				 * 
				 * 1. Search for the m*.gif icon in the export HTML directory.
				 * 
				 * 2. The number of the icon will be defined in
				 * EuclidianConstans.java, so you can find the appropriate
				 * MODE_TOOL constant.
				 * 
				 * 3. Search for this constant in Algo*.java. Try to change it
				 * to a different constant, recompile GeoGebra and try another
				 * export. Then "go to 1" if your fix was not accurate.
				 */
				String gifFileName;
				
				if(thisPath != null){  //thisPath==null if we want to copy to the clipboard
					
					ImageIcon icon = ((AppD)app).getModeIcon(m);
					gifFileName = "m" + Integer.toString(m) + ".gif";
	
					Image img1 = icon.getImage();
					
					BufferedImage img2 = toBufferedImage(img1);
	
					File gifFile = new File(thisPath + "/" + gifFileName);
					try {
						ImageIO.write(img2, "gif", gifFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}else{
				
					String modeText = app.getKernel().getModeText(m);
					gifFileName = "http://www.geogebra.org/icons/mode_" + modeText.toLowerCase(Locale.US)
							+ "_32.gif";
				}
				
				return "<img src=\"" + gifFileName + "\">";
			}
			case 3:
				return rowList.get(nRow).getGeo()
						.getDefinitionDescriptionHTML(false);
			case 4:
				return rowList.get(nRow).getGeo()
						.getCommandDescriptionHTML(false);
			case 5:
				return rowList.get(nRow).getGeo()
						.getAlgebraDescriptionHTMLDefault();
		
			case 6:
				return rowList.get(nRow).getGeo()
						.getCaptionDescriptionHTML(false,StringTemplate.defaultTemplate);			
				
			case 7:
				return rowList.get(nRow).getCPVisible()
						.toString();
				
			}
			
			return "";
		}

		/*
		 * The following code has been copy-pasted from
		 * http://forums.sun.com/thread.jspa?threadID=5330345, posted by _Matt_.
		 * Its purpose is to convert an icon to a format which can be copied to
		 * the file system.
		 */
		public BufferedImage toBufferedImage(Image i) {
			if (i instanceof BufferedImage) {
				return (BufferedImage) i;
			}
			Image img;
			img = new ImageIcon(i).getImage();
			BufferedImage b;
			b = new BufferedImage(img.getWidth(null), img.getHeight(null),
					BufferedImage.TYPE_INT_ARGB);
			Graphics g = b.createGraphics();
			g.drawImage(img, 0, 0, null);
			g.dispose();
			return b;
		}

		/***********************
		 * View Implementation *
		 ***********************/

		public void add(GeoElement geo) {
			if ((!geo.isLabelSet() && !geo.isGeoCasCell())
					|| (kernel.getConstruction().showOnlyBreakpoints() && !geo
							.isConsProtocolBreakpoint()))
				return;

			RowData row = (RowData) geoMap.get(geo); // lookup row for geo
			if (row == null) { // new row
				int index = geo.getConstructionIndex();
				int pos = 0; // there may be more rows with same index
				int size = rowList.size();
				while (pos < size
						&& index >= rowList.get(pos).getGeo()
								.getConstructionIndex())
					pos++;

				row = new RowData(geo);
				if (pos < size) {
					rowList.add(pos, row);
				} else {
					pos = size;
					rowList.add(row);
				}

				// insert new row
				geoMap.put(geo, row); // insert (geo, row) pair in map
				updateRowNumbers(pos);
				updateIndices();
				ctDataImpl.fireTableRowsInserted(pos, pos);
				updateAll();
				updateNavigationBars();
			}
		}

		public void remove(GeoElement geo) {
			RowData row = (RowData) geoMap.get(geo);
			// lookup row for GeoElement
			if (row != null) {
				rowList.remove(row); // remove row
				geoMap.remove(geo); // remove (geo, row) pair from map
				updateRowNumbers(row.getRowNumber());
				updateIndices();
				ctDataImpl.fireTableRowsDeleted(row.getRowNumber(), row.getRowNumber());
				updateAll();
				updateNavigationBars();
			}
		}

		public void clearView() {
			rowList.clear();
			geoMap.clear();
			updateNavigationBars();
		}

		final public void repaintView() {
			repaint();
		}

		// update all row numbers >= row
		private void updateRowNumbers(int row) {
			if (row < 0)
				return;
			int size = rowList.size();
			for (int i = row; i < size; ++i) {
				//rowList.get(i).rowNumber = i;
				rowList.get(i).setRowNumber(i);
			}
		}

		// update all indices
		private void updateIndices() {
			int size = rowList.size();
			if (size == 0)
				return;

			int lastIndex = -1;
			int count = 0;
			RowData row;
			for (int i = 0; i < size; ++i) {
				row = (RowData) rowList.get(i);
				int newIndex = row.getGeo().getConstructionIndex();
				if (lastIndex != newIndex) {
					lastIndex = newIndex;
					count++;
				}
				row.setIndex(count);
			}
		}

		public void rename(GeoElement geo) {
			// renaming may affect multiple rows
			// so let's update whole table
			updateAll();
		}

		public void repaint() {
			table.repaint();
		}

		void updateAll() {
			if(notifyUpdateCalled)
				return;
			int size = rowList.size();

			int toolbarIconHeight = 0;
			// If displaying toolbarIcon is set, row height must be at least 32
			// + 1:
			if (isColumnInModel(tableColumns[1]))
				toolbarIconHeight = 32 + 1;
			/*
			 * FIXME: The cell content is not aligned vertically centered. I
			 * don't think it is possible to easily solve this because JTable
			 * does not offer a convenient way for vertical alignment of the
			 * cell content. Probably
			 * http://articles.techrepublic.com.com/5100-10878_11-5032692.html
			 * may help, or to use the same technique which is introduced in
			 * GeoGebraCAS.
			 */

			for (int i = 0; i < size; ++i) {
				RowData row = (RowData) rowList.get(i);
				row.updateAll();

				// it seems there isn't fit to content (automatic) row height in JTable,
				// so we use the most frequent option, 2 lines of text in a row
				// (this is still better than 1 lines of text in a row)
				if (row.getIncludesIndex()) {
					table.setRowHeight(i, Math.max(
							(table.getFont().getSize() * 2 + 16), toolbarIconHeight));
				} else {
					table.setRowHeight(i, Math.max(
							(table.getFont().getSize() * 2 + 12), toolbarIconHeight));
				}
			}
			ctDataImpl.fireTableRowsUpdated(0, size - 1);
		}

		final public void update(GeoElement geo) {
			RowData row = (RowData) geoMap.get(geo);
			if (row != null) {
				// remove row if only breakpoints
				// are shown and this is no longer a breakpoint (while loading a
				// construction)
				if (!geo.isConsProtocolBreakpoint()
						&& kernel.getConstruction().showOnlyBreakpoints())
					remove(geo);
				else {
					row.updateAlgebraAndName();
					row.updateCaption();
					ctDataImpl.fireTableRowsUpdated(row.getRowNumber(), row.getRowNumber());
				}
			} else {
				// missing row: should be added if only breakpoints
				// are shown and this became a breakpoint (while loading a
				// construction)
				if (kernel.getConstruction().showOnlyBreakpoints()
						&& geo.isConsProtocolBreakpoint())
					add(geo);
			}
		}
		

		final public void updateVisualStyle(GeoElement geo) {
			update(geo);
		}

		final public void updateAuxiliaryObject(GeoElement geo) {
			// update(geo);
		}

		private class ColumnMovementListener implements
				TableColumnModelListener {

			public void columnAdded(TableColumnModelEvent e) {
				columnsCount++;
			}

			public void columnRemoved(TableColumnModelEvent e) {
				columnsCount--;
			}

			public void columnMarginChanged(ChangeEvent e) {
			}

			public void columnMoved(TableColumnModelEvent e) {
			}

			public void columnSelectionChanged(ListSelectionEvent e) {
			}
		}

		public void reset() {
			repaint();
		}
		
		public void setValueAt(Object value, int row, int col) {

        	if((this.columns[col].getTitle()).equals("Caption")){
        		data.getRow(row).getGeo().setCaption(value.toString());
        		data.getRow(row).getGeo().update();
        		//updateAll();
        		kernel.notifyRepaint();     		
        	}
        }

		public int getViewID() {
			return App.VIEW_CONSTRUCTION_PROTOCOL;
		}

		public boolean hasFocus() {
		    App.debug("unimplemented");
			return false;
		}

		public boolean isShowing() {
		    App.debug("unimplemented");
			return false;
		}

		public class MyGAbstractTableModel extends GAbstractTableModelD{

			@Override
			public int getRowCount() {
				return ctData.getRowCount();
			}

			@Override
			public Object getValueAt(int nRow, int nCol) {
				return ((ConstructionTableData)ctData).getValueAt(nRow, nCol);
			}

			@Override
			public int getColumnCount() {
				return ctData.getColumnCount();
			}
			
			@Override
			public boolean isCellEditable(int nRow, int nCol) {
				return ctData.isCellEditable(nRow, nCol);
			}
			
			@Override
			public void setValueAt(Object value, int row, int col) {
				((ConstructionTableData)ctData).setValueAt(value, row, col);
	        }
			
		}
		
	}

	/************
	 * PRINTING *
	 ************/

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		
		if(!isViewAttached){
			data.clearView();
			((ConstructionTableData) data).notifyAddAll(kernel.getConstruction().getStep());
			
			JFrame tempFrame = new JFrame();
			tempFrame.add(this.cpPanel);
			tempFrame.pack();
		}
		int r=table.getPrintable(PrintMode.FIT_WIDTH, null, null).print(graphics, pageFormat, pageIndex);
				
		return r;
	}
	
	
	/*
	public int print(Graphics pg, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		Application.debug("CPView-print");
		if (pageIndex >= maxNumPage)
			return NO_SUCH_PAGE;

		pg.translate((int) pageFormat.getImageableX(),
				(int) pageFormat.getImageableY());
		int wPage = (int) pageFormat.getImageableWidth();
		int hPage = (int) pageFormat.getImageableHeight();
		pg.setClip(0, 0, wPage, hPage);

		// construction title
		int y = 0;
		Font titleFont = table.getFont().deriveFont(Font.BOLD,
				table.getFont().getSize() + 2);
		pg.setFont(titleFont);
		pg.setColor(Color.black);
		// Font fn = pg.getFont();
		FontMetrics fm = pg.getFontMetrics();

		// title
		Construction cons = kernel.getConstruction();
		String title = cons.getTitle();
		if (!title.equals("")) {
			y += fm.getAscent();
			pg.drawString(title, 0, y);
		}

		// construction author and date
		String author = cons.getAuthor();
		String date = cons.getDate();
		String line = null;
		if (!author.equals("")) {
			line = author;
		}
		if (!date.equals("")) {
			if (line == null)
				line = date;
			else
				line = line + " - " + date;
		}
		if (line != null) {
			pg.setFont(table.getFont());
			// fn = pg.getFont();
			fm = pg.getFontMetrics();
			y += fm.getHeight();
			pg.drawString(line, 0, y);
		}

		y += 20; // space between title and table headers

		Font headerFont = table.getFont().deriveFont(Font.BOLD);
		pg.setFont(headerFont);
		fm = pg.getFontMetrics();

		TableColumnModel colModel = table.getColumnModel();
		int nColumns = colModel.getColumnCount();
		int x[] = new int[nColumns];
		x[0] = 0;

		int h = fm.getAscent();
		y += h; // add ascent of header font because of baseline

		int nRow, nCol;
		for (nCol = 0; nCol < nColumns; nCol++) {
			TableColumn tk = colModel.getColumn(nCol);
			int width = tk.getWidth();
			// only print column if there is enough space for it
			// if (x[nCol] + width > wPage) {
			// nColumns = nCol;
			// break;
			// }
			if (nCol + 1 < nColumns)
				x[nCol + 1] = x[nCol] + width;
			title = (String) tk.getIdentifier();
			pg.drawString(title, x[nCol], y);
		}

		Font tableFont = table.getFont();
		pg.setFont(tableFont);
		fm = pg.getFontMetrics();

		int header = y;
		h = fm.getHeight();
		int rowH = Math.max(h, 10);
		int rowPerPage = (hPage - header) / rowH;
		maxNumPage = Math.max(
				(int) Math.ceil(table.getRowCount() / (double) rowPerPage), 1);

		// TableModel tblModel = table.getModel();
		int iniRow = pageIndex * rowPerPage;
		int endRow = Math.min(table.getRowCount(), iniRow + rowPerPage);
		int yAdd, maxYadd = 0;

		for (nRow = iniRow; nRow < endRow; nRow++) {
			y = y + h + maxYadd; // maxYadd is additional space for indices of
									// last line
			maxYadd = 0;
			for (nCol = 0; nCol < nColumns; nCol++) {
				int col = table.getColumnModel().getColumn(nCol)
						.getModelIndex();
				// Object obj = data.getValueAt(nRow, col);
				// String str = obj.toString();
				// pg.drawString(str, x[nCol], y);
				String str = data.getPlainTextAt(nRow, col);
				pg.setColor(data.getColorAt(nRow, col));
				yAdd = Drawable.drawIndexedString(app, (Graphics2D) pg, str,
						x[nCol], y).y;
				if (yAdd > maxYadd)
					maxYadd = yAdd;
			}
		}

		return PAGE_EXISTS;
	}

*/
	/***************
	 * HTML export *
	 ***************/

	/**
	 * Returns a html representation of the construction protocol.
	 * 
	 * @param thisPath
	 * @param imgFile
	 *            : image file to be included
	 * @throws IOException
	 */
	public String getHTML(File imgFile, String thisPath) throws IOException {
		StringBuilder sb = new StringBuilder();

		boolean icon_column;
		
		// Let's be W3C compliant:
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n");
		sb.append("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">");
		sb.append("<head>\n");
		sb.append("<title>");
		sb.append(StringUtil.toHTMLString(app.getPlain("ApplicationName")));
		sb.append(" - ");
		sb.append(app.getPlain("ConstructionProtocol"));
		sb.append("</title>\n");
		sb.append("<meta keywords = \"");
		sb.append(StringUtil.toHTMLString(app.getPlain("ApplicationName")));
		sb.append(" export\">");
		String css = ((AppD)app).getSetting("cssConstructionProtocol");
		if (css != null) {
			sb.append(css);
			sb.append("\n");
		}
		sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">");
		sb.append("</head>\n");

		sb.append("<body>\n");

		// header with title
		Construction cons = kernel.getConstruction();
		String title = cons.getTitle();
		if (!title.equals("")) {
			sb.append("<h1>");
			sb.append(StringUtil.toHTMLString(title));
			sb.append("</h1>\n");
		}

		// header with author and date
		String author = cons.getAuthor();
		String date = cons.getDate();
		String line = null;
		if (!author.equals("")) {
			line = author;
		}
		if (!date.equals("")) {
			if (line == null)
				line = date;
			else
				line = line + " - " + date;
		}
		if (line != null) {
			sb.append("<h3>");
			sb.append(StringUtil.toHTMLString(line));
			sb.append("</h3>\n");
		}

		// include image file
		if (imgFile != null) {
			sb.append("<p>\n");
			sb.append("<img src=\"");
			sb.append(imgFile.getName());
			sb.append("\" alt=\"");
			sb.append(StringUtil.toHTMLString(app.getPlain("ApplicationName")));
			sb.append(' ');
			sb.append(StringUtil.toHTMLString(app.getPlain("DrawingPad")));
			sb.append("\" border=\"1\">\n");
			sb.append("</p>\n");
		}

		// table
		sb.append("<table border=\"1\">\n");

		// table headers
		sb.append("<tr>\n");
		TableColumnModel colModel = table.getColumnModel();
		int nColumns = colModel.getColumnCount();

		for (int nCol = 0; nCol < nColumns; nCol++) {
			// toolbar icon will only be inserted on request

			icon_column = table.getColumnName(nCol).equals("ToolbarIcon");
			if ((icon_column && addIcons) || !icon_column) {
				TableColumn tk = colModel.getColumn(nCol);
				title = (String) tk.getIdentifier();
				sb.append("<th>");
				sb.append(StringUtil.toHTMLString(title));
				sb.append("</th>\n");
			}

		}
		sb.append("</tr>\n");

		// table rows
		int endRow = table.getRowCount();
		for (int nRow = 0; nRow < endRow; nRow++) {
			sb.append("<tr  valign=\"baseline\">\n");
			for (int nCol = 0; nCol < nColumns; nCol++) {

				// toolbar icon will only be inserted on request
				icon_column = table.getColumnName(nCol).equals("ToolbarIcon");
				if ((icon_column && addIcons) || !icon_column) {
					int col = table.getColumnModel().getColumn(nCol)
							.getModelIndex();
					String str = StringUtil.toHTMLString(((ConstructionTableData) data).getPlainHTMLAt(nRow, col, thisPath));
					sb.append("<td>");
					if (str.equals(""))
						sb.append("&nbsp;"); // space
					else {
						Color color = ((ConstructionTableData) data).getColorAt(nRow, col);
						if (color != Color.black) {
							sb.append("<span style=\"color:#");
							sb.append(StringUtil.toHexString(new geogebra.awt.GColorD(color)));
							sb.append("\">");
							sb.append(str);
							sb.append("</span>");
						} else
							sb.append(str);
					}
					sb.append("</td>\n");
				}

			}
			sb.append("</tr>\n");
		}

		sb.append("</table>\n");

		// footer
		sb.append(((GuiManagerD)app.getGuiManager()).getCreatedWithHTML(false));
		
		// append base64 string so that file can be reloaded with File -> Open
		sb.append("\n<!-- Base64 string so that this file can be opened in GeoGebra with File -> Open -->");
		sb.append("\n<applet style=\"display:none\">");
		sb.append("\n<param name=\"ggbBase64\" value=\"");
		appendBase64((AppD)app,sb);
		sb.append("\">\n<applet>");


		sb.append("\n</body>");
		sb.append("\n</html>");

		return sb.toString();
	}
	
	/**
	 * @param app app
	 * @param sb sb
	 * @return .ggb file encoded as base64 string
	 */
	public static boolean appendBase64(AppD app, StringBuilder sb) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			app.getXMLio().writeGeoGebraFile(baos, false);
			sb.append(geogebra.common.util.Base64.encode(baos.toByteArray(), 0));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
/*
	public void showHTMLExportDialog() {
		Application.printStacktrace("showHTMLExportDialog");
		exportHtmlAction.actionPerformed(null);
	}
*/
	public String getConsProtocolXML() {
		StringBuilder sb = new StringBuilder();

		// COLUMNS
		sb.append("\t<consProtColumns ");
		for (int i = 0; i < data.columns.length; i++) {
			sb.append(" col");
			sb.append(i);
			sb.append("=\"");
			sb.append(data.columns[i].isVisible());
			sb.append("\"");
		}
		sb.append("/>\n");

		// consProtocol
		sb.append("\t<consProtocol ");
		sb.append("useColors=\"");
		sb.append(useColors);
		sb.append("\"");
		sb.append(" addIcons=\"");
		sb.append(addIcons);
		sb.append("\"");
		sb.append(" showOnlyBreakpoints=\"");
		sb.append(kernel.getConstruction().showOnlyBreakpoints());
		sb.append("\"");
		sb.append("/>\n");

		return sb.toString();
	}
/*
	public void add(GeoElement geo) {
		// TODO Auto-generated method stub
	}

	public void remove(GeoElement geo) {
		// TODO Auto-generated method stub
	}

	public void rename(GeoElement geo) {
		// TODO Auto-generated method stub
	}

	public void update(GeoElement geo) {
		// TODO Auto-generated method stub
		data.update(geo);
	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Auto-generated method stub
	}

	public void repaintView() {
		// TODO Auto-generated method stub
		data.repaintView();
	}

	public void reset() {
		// TODO Auto-generated method stub
	}

	public void clearView() {
		// TODO Auto-generated method stub
	}

	public void setMode(int mode) {
		// TODO Auto-generated method stub
	}
	
	public void attachView() {
		kernel.notifyAddAll(this);
		kernel.attach(this);
	}
*/

	public void actionPerformed(ActionEvent e) {
		kernel.getConstruction().setShowOnlyBreakpoints(!kernel.getConstruction().showOnlyBreakpoints());
		getData().initView();
		repaint();
	}
	
	public void settingsChanged(AbstractSettings settings) {
		ConstructionProtocolSettings cps = (ConstructionProtocolSettings)settings;

		boolean gcv[] = cps.getColsVisibility();
		if (gcv != null) if (gcv.length > 0)
			setColsVisibility(gcv);

		update();
		getData().initView();
		repaint();
		
	
	}

	private void setColsVisibility(boolean[] colsVisibility) {
		TableColumnModel model = table.getColumnModel();
		
		int k = Math.min(colsVisibility.length, data.columns.length);
		
		for(int i=0; i<k; i++){
			TableColumn column = getTableColumns()[i];
			model.removeColumn(column);
			if (colsVisibility[i] == true){
				model.addColumn(column);
			} 
			//else {
			//	model.removeColumn(column);
			//}
			((ConstructionTableData) data).initView();
			data.columns[i].setVisible(colsVisibility[i]);
		}
		
	}
}
