package org.geogebra.web.web.gui.view.consprotocol;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.ConstructionProtocolSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.web.html5.awt.GColorW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.layout.panels.ConstructionProtocolStyleBarW;
import org.geogebra.web.web.gui.util.StyleBarW;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Web implementation of ConstructionProtocol
 *
 */
public class ConstructionProtocolViewW extends ConstructionProtocolView implements SetLabels, SettingListener{

	/** contains a scrollPanel with the {@link #table constructionstep-table} **/
	public FlowPanel cpPanel;
	/** table with constructionsteps **/
	protected CellTable<RowData> table;
	private StyleBarW styleBar;
	/** possible drop index **/
	int minIndex;
	/** possible drop index **/
	int maxIndex;
	/** the dragged row **/
	protected Element draggedRow;
	/** index of dragged row **/
	protected int dragIndex;
	GPopupMenuW popupMenu;


	/**
	 * 
	 * @param app {@link AppW}
	 */
	public ConstructionProtocolViewW(final AppW app) {
		cpPanel = new FlowPanel();
		this.app = app;
		kernel = app.getKernel();
		data = new ConstructionTableDataW(this);

		app.getGuiManager().registerConstructionProtocolView(this);

		table = new CellTable<RowData>();
		table.addStyleName("cpTable");
				
//		first attempt with flextable
//		table = new FlexTable();
//		for (int k = 0; k < data.columns.length; k++) {
//			if ((data.columns[k].getTitle() == "number") ||
//					(data.columns[k].getTitle() == "name") ||
//					(data.columns[k].getTitle() == "definition"))
//				table.setText(0, k, data.columns[k].getTitle());
//		}		
//		
//		

		ScrollPanel scrollPane = new ScrollPanel(table);
		scrollPane.setStyleName("cpScrollPanel");
		cpPanel.add(scrollPane);
		
		addDragDropHandlers();
		
		ConstructionProtocolSettings cps = app.getSettings().getConstructionProtocol();
		settingsChanged(cps);
		cps.addListener(this);

		initGUI();
	}
	
	/**
	 * adds handlers for dragging rows. Overridden for touch. 
	 */
    protected void addDragDropHandlers() {
       	table.addDomHandler(new DragStartHandler() {
			
			@Override
			public void onDragStart(DragStartEvent event) {
				handleDrag(event.getNativeEvent().getClientY());
			}
		}, DragStartEvent.getType());
		
		table.addDomHandler(new DragEndHandler() {
			@Override
			public void onDragEnd(DragEndEvent event) {
				if (draggedRow != null) {
					draggedRow.removeClassName("isDragging");
				}
				if (Window.getClientWidth()+event.getNativeEvent().getScreenX() > table.getElement().getAbsoluteRight() ||
						Window.getClientWidth()+event.getNativeEvent().getScreenX() < table.getElement().getAbsoluteLeft()) {
					return;
				}
				handleDrop(event.getNativeEvent().getClientY());
			}
		}, DragEndEvent.getType());
    }

    /**
	 * @param y coordinate of dragStart
	 */
    protected void handleDrag(int y) {
        for (int i = 0; i < table.getRowCount(); i++) {
			if (y > table.getRowElement(i).getAbsoluteTop() && y < table.getRowElement(i).getAbsoluteBottom()) {
				draggedRow = table.getRowElement(i);
				draggedRow.addClassName("isDragging");
				GeoElement geo = data.getRow(i).getGeo();
				dragIndex = geo.getConstructionIndex();
				minIndex = geo.getMinConstructionIndex();
				maxIndex = geo.getMaxConstructionIndex();
				return;
			}
		}
    }
    
    /**
     * @param y coordinate of dropTarget
     */
    protected void handleDrop(int y) {
    	for (int i = 0; i < table.getRowCount(); i++) {
    		if ((y > table.getRowElement(i).getAbsoluteTop() && y < table.getRowElement(i).getAbsoluteBottom()) ||
    				//dragEnd happens below the last row
    				(i == table.getRowCount()-1 && y > table.getRowElement(i).getAbsoluteBottom())) {
    			int dropIndex = data.getConstructionIndex(i);
    			if (dropIndex == dragIndex) {
    				//no changes necessary
    				return;
    			}
    			if (dropIndex < minIndex || dropIndex > maxIndex) {
    				//drop not possible
    				//TODO change cursor style before releasing mouse
					ToolTipManagerW.sharedInstance().showBottomMessage(
							"Drop not possible", true, (AppW) app);
    				return;
    			}
    			boolean kernelChanged = ((ConstructionTableDataW)data).moveInConstructionList(dragIndex, dropIndex);
    			if (kernelChanged) {
    				app.storeUndoInfo();
    			}
       			return;
    		}
    	}
    }

    public void setLabels(){
    	initGUI();
    }
    
	public CellTable<RowData> getTable() {
		return table;
	}

	void initGUI() {
		//remove all columns if there are
		int colCount = table.getColumnCount();
		for(int i=0; i<colCount; i++){
			table.removeColumn(0);
		}
		
		if (!app.has(Feature.CP_POPUP)) {  //old source inserted back here
			for (int i = 0; i < data.getColumnCount(); i++) {
				if (data.columns[i].isVisible()) {
					String title = data.columns[i].getTitle();
					Column<RowData, ?> col = getColumn(title);
					if (col != null) {
						table.addColumn(col, app.getPlain(title));
					}
				}
			}
			tableInit();
			rowCountChanged();
			return;
		}
				

		int lastVisibleColData = data.getColumnCount() - 1;
		
		while (!data.columns[lastVisibleColData].isVisible()
				|| "ToolbarIcon"
						.equals(data.columns[lastVisibleColData].getTitle())
				|| "Command".equals(data.columns[lastVisibleColData].getTitle())
				|| "Caption".equals(data.columns[lastVisibleColData].getTitle())
				|| "Breakpoint"
						.equals(data.columns[lastVisibleColData].getTitle())) {
			lastVisibleColData--;
		}
		
		initPopupMenu();
		
		for (int i = 0; i < data.getColumnCount(); i++) {
			if (data.columns[i].isVisible()) {
				String title = data.columns[i].getTitle();
				Column<RowData, ?> col = getColumn(title);
				if (col != null) {
					SafeHtmlBuilder sb = new SafeHtmlBuilder();
					sb.append(SafeHtmlUtils.fromSafeConstant("<div>"
							+ app.getPlain(title) + "</div>"));
					if (i != 0 && i != lastVisibleColData) {
						sb.append(SafeHtmlUtils
								.fromSafeConstant("<div title = " + title + ">"));
						sb.append(AbstractImagePrototype.create(
								GuiResources.INSTANCE.dockbar_close())
								.getSafeHtml());
						sb.append(SafeHtmlUtils.fromSafeConstant("</div>"));
					} else if (i != 0) {
						sb.append(SafeHtmlUtils.fromSafeConstant("<div id = \"CP_popupImage\">"));
						sb.append(AbstractImagePrototype.create(
								GuiResources.INSTANCE.menu_dots())
								.getSafeHtml());
						sb.append(SafeHtmlUtils.fromSafeConstant("</div>"));

					}
					col.setDataStoreName(title);
					table.addColumn(col, sb.toSafeHtml());
				}
			}
		}
		tableInit();
		rowCountChanged();
	}

	protected void initPopupMenu() {
		int k0 = 0;
		final String[] tableColumns = {};
		int lastVisibleColData = -1;
		int visibleCols = 0;
		for (int k = 0; k < data.getColumnCount(); k++) {
			ColumnData colData = data.getColumns()[k];
			// On web there is no all columns yet, so temporary must hide
			// some
			// column on stylebar too
			if (!"No.".equals(colData.getTitle())
					&& !"ToolbarIcon".equals(colData.getTitle())
					&& !"Command".equals(colData.getTitle())
					&& !"Caption".equals(colData.getTitle())
					&& !"Breakpoint".equals(colData.getTitle())) {
				if (colData.isVisible()) {
					lastVisibleColData = k;
					visibleCols++;
				} else {
					tableColumns[k0] = colData.getTranslatedTitle();
					k0++;
				}
			}
		}

		popupMenu = new GPopupMenuW((AppW) app);
		ScheduledCommand com;

		if (visibleCols > 1) {
			final int n = lastVisibleColData;
			com = new ScheduledCommand() {
				public void execute() {
					data.columns[n].setVisible(false);
					initGUI();
				}
			};
			popupMenu.addItem(new MenuItem(app.getMenu("Close"), com));
		}

		if (tableColumns.length > 0) {
			if (visibleCols > 1) {
				popupMenu.addSeparator();
			}
			
			for (int i = 0; i < tableColumns.length; i++) {

				final int j = i;
				com = new ScheduledCommand() {

					public void execute() {
						data.columns[data
								.getColumnNumberByTitle(tableColumns[j])]
								.setVisible(true);
						initGUI();
					}
				};

				popupMenu.addItem(tableColumns[i], com);
			}
		}

		table.addHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Element el = Element
						.as(event.getNativeEvent().getEventTarget());
				Element imgElement = DOM.getElementById("CP_popupImage")
						.getElementsByTagName("img").getItem(0);
				if (el.equals(imgElement)) {

					popupMenu.show(new GPoint(event.getClientX(), event
							.getClientY()));
				} else if ("img".equals(el.getTagName().toLowerCase())) {
					String colTitle = el.getParentElement().getAttribute(
							"title");
					if (colTitle != null) {
						data.columns[data.getColumnNumberByTitle(colTitle)]
								.setVisible(false);
						table.removeColumn(getColumnByTitle(colTitle));
						initPopupMenu();
					}
				}
			}

		}, ClickEvent.getType());

	}

	public Column<RowData, ?> getColumn(String title) {
		Column<RowData, ?> col = null;
		if ("No.".equals(title)) {
			col = getColumnId();
		} else if ("Name".equals(title)) {
			col = getColumnName();
		} else if ("ToolbarIcon".equals(title)) {
			// col = getColumnToolbarIcon();
		} else if ("Definition".equals(title)) {
			col = getColumnDefinition();
		} else if ("Command".equals(title)) {
			// TODO
		} else if ("Value".equals(title)) {
			col = getColumnValue();
		} else if ("Caption".equals(title)) {
			// TODO
		} else { // if ("Breakpoint".equals(title)) {
			// TODO
		}
		
		return col;

	}

	/*
	 * Add a number column to show the id.
	 */
	public Column<RowData, Number> getColumnId() {
	    Cell<Number> idCell = new NumberCell();
	    Column<RowData, Number> idColumn = new Column<RowData, Number>(idCell) {
	      @Override
	      public Number getValue(RowData object) {
	        return object.getIndex();
	      }
	    };

		return idColumn;
		
	}

	/*
	 * Add a text column to show the name.
	 */
	private Column<RowData, SafeHtml> getColumnName() {
		Column<RowData, SafeHtml> nameColumn = new Column<RowData, SafeHtml>(
		        new SafeHtmlCell()) {
			
			@Override
            public SafeHtml getValue(RowData object) {
				return SafeHtmlUtils.fromTrustedString(object.getName());
			}

		};    
		return nameColumn;
	}


	/*
	 * Add a text column to show the definition.
	 */
	private Column<RowData, SafeHtml> getColumnDefinition() {
		Column<RowData, SafeHtml> defColumn = new Column<RowData, SafeHtml>(
		        new SafeHtmlCell()) {
			
			@Override
			public SafeHtml getValue(RowData object) {
				return SafeHtmlUtils.fromTrustedString(object.getDefinition());
			}

		};
		return defColumn;
	}

	/*
	 * Add a text column to show the value.
	 */
	private Column<RowData, SafeHtml> getColumnValue() {
		Column<RowData, SafeHtml> valColumn = new Column<RowData, SafeHtml>(
		        new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(RowData object) {
				return SafeHtmlUtils.fromTrustedString(object.getAlgebra());
			}

		};
		return valColumn;
	}
	

	public void settingsChanged(AbstractSettings settings) {
		ConstructionProtocolSettings cps = (ConstructionProtocolSettings)settings;

		boolean gcv[] = cps.getColsVisibility();
		if (gcv != null) if (gcv.length > 0)
			setColsVisibility(gcv);

		initGUI();
//		update();
		getData().initView();
//		repaint();	
	}
	
	/**
     * @param colsVisibility intended visibility of columns 
     */
	private void setColsVisibility(boolean[] colsVisibility) {

		int k = Math.min(colsVisibility.length, data.columns.length);

		for (int i = 0; i < k; i++) {
			data.columns[i].setVisible(colsVisibility[i]);
		}

	}

	
	@Override
	public void scrollToConstructionStep() {
		if(kernel.isViewReiniting()){
			return;
		}
		if (table != null) {
			int rowCount = table.getRowCount();
			if (rowCount == 0)
				return;

			int step = kernel.getConstructionStep();
			int row = -1; //it's possible that ConsStep == 0, so we need index -1 to deselect all rows
			for (int i = 0; i < rowCount; i++) {
				if (data.getConstructionIndex(i) <= step)
					row = i;
				else
					break;
			}
			final int row2 = row;
			app.getGuiManager().invokeLater(new Runnable(){

				@Override
                public void run() {
					markRowsActive(row2);
	                
                }});
			
			
		}
	}
	
	/**
	 * marks the rows (up to {@code index}) selected
	 * @param index int
	 */
	void markRowsActive(int index) {
		for (int i = 0; i < table.getRowCount(); i++) {
			if (i <= index) {
				GColorW color = (GColorW) data.getRow(i).getGeo().getAlgebraColor();
				table.getRowElement(i).setAttribute("style", "color:"+ GColor.getColorString(color));
			} else {
				table.getRowElement(i).setAttribute("style", "opacity:0.5");
			}
		}
	}
	
	/**
	 * @return widget representing this view
	 */
	public FlowPanel getCpPanel(){
		return cpPanel;
	}
	
	@Override
    public void repaint(){
		tableInit();
	}
	
	/**
	 * Make all currrent rows draggable
	 */
	void makeTableRowsDragable() {
	    for (int i = 0; i < table.getRowCount(); i++) {
	    		table.getRowElement(i).setDraggable(Element.DRAGGABLE_TRUE);
	    }
    }	
	
	/**
	 * Rebuild construction table
	 */
	public void tableInit(){
//		data.updateAll();
		table.setRowCount(data.getrowList().size());
	    table.setRowData(0, data.getrowList());
	    table.setVisibleRange(0, data.getrowList().size()+1);
		scrollToConstructionStep();

	}

	void rowCountChanged() {
		app.getGuiManager().invokeLater(new Runnable() {
	    	public void run(){
	    		makeTableRowsDragable();
	    	}
	    });
		scrollToConstructionStep();
	}
	/**
	 * Web implementation of ConstructionTableData
	 */
	class ConstructionTableDataW extends ConstructionTableData{

		/**
		 * @param gui gui element on which we delegate setLabels
		 */
		public ConstructionTableDataW(SetLabels gui){
			super(gui);
//			ctDataImpl = new MyGAbstractTableModel();
		}
		
		/**
		 * 
		 * @param fromIndex from
		 * @param toIndex to
		 * @return whether something changed
		 */
		public boolean moveInConstructionList(int fromIndex, int toIndex) {
			boolean changed = kernel.moveInConstructionList(fromIndex, toIndex);

			// reorder rows in this view
			ConstructionElement ce = kernel.getConstructionElement(toIndex);
			GeoElement[] geos = ce.getGeoElements();
			for (int i = 0; i < geos.length; ++i) {
				remove(geos[i]);
				add(geos[i]);
			}
			return changed;
		}
		
		@Override
		public void fireTableRowsInserted(int firstRow, int lastRow){
			//TODO: maybe it's not necessary to reinit the all table
			if(table != null){
				table.setRowCount(0);
				tableInit();
				rowCountChanged();
			}
		}
		
		@Override
		public void fireTableRowsDeleted(int firstRow, int lastRow){
			//TODO: maybe it's not necessary to reinit the all table
			if(table != null){
				table.setRowCount(0);
				tableInit();
				rowCountChanged();
			}
		}

		@Override
        public void updateAll(){
			int size = rowList.size();

			for (int i = 0; i < size; ++i) {
				RowData row = rowList.get(i);
				row.updateAll();
			}
		}
	}

	public Column<RowData, ?> getColumnByTitle(String t) {
		if (t == null){
			return null;
		}
		Column<RowData, ?> col;
		for (int i = 0; i < table.getColumnCount(); i++) {
			col = table.getColumn(i);
			if (t.equals(col.getDataStoreName())) {
				return col;
			}
		}
		return null;
	}

	/**
	 * @return stylebar
	 */
	public StyleBarW getStyleBar() {
	    if(styleBar == null){
			styleBar = new ConstructionProtocolStyleBarW(this, (AppW) this.app);
	    }
	    return styleBar;
    }
}
