package geogebra.web.gui.view.consprotocol;

import geogebra.common.awt.GColor;
import geogebra.common.gui.SetLabels;
import geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.ConstructionProtocolSettings;
import geogebra.html5.awt.GColorW;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.main.AppW;
import geogebra.web.gui.layout.panels.ConstructionProtocolStyleBarW;
import geogebra.web.gui.util.StyleBarW;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class ConstructionProtocolViewW extends ConstructionProtocolView implements SetLabels{

	private ConstructionProtocolNavigationW protNavBar;
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


	/**
	 * 
	 * @param app {@link AppW}
	 */
	public ConstructionProtocolViewW(final AppW app) {
		cpPanel = new FlowPanel();
		this.app = app;
		kernel = app.getKernel();
		data = new ConstructionTableDataW(this);
		protNavBar = (ConstructionProtocolNavigationW) (app.getGuiManager().getConstructionProtocolNavigation());
		protNavBar.register(this);
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
				
		initGUI();
		
		addDragDropHandlers();
		
		ConstructionProtocolSettings cps = app.getSettings().getConstructionProtocol();
		settingsChanged(cps);
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
    				ToolTipManagerW.sharedInstance().showBottomMessage("Drop not possible", true);
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
	public void initGUI(){
		//remove all columns if there are
		int colCount = table.getColumnCount();
		for(int i=0; i<colCount; i++){
			table.removeColumn(0);
		}		
		
	    // Add a number column to show the id.
	    Cell<Number> idCell = new NumberCell();
	    Column<RowData, Number> idColumn = new Column<RowData, Number>(idCell) {
	      @Override
	      public Number getValue(RowData object) {
	        return object.getIndex();
	      }
	    };
	    table.addColumn(idColumn, app.getPlain("No."));
		
		
	    // Add a text column to show the name.
		Column<RowData, SafeHtml> nameColumn = new Column<RowData, SafeHtml>(
		        new SafeHtmlCell()) {
			
			@Override
            public SafeHtml getValue(RowData object) {
				return SafeHtmlUtils.fromTrustedString(object.getName());
			}

		};    
	    table.addColumn(nameColumn, app.getPlain("Name"));
	    
//	    // Add a text column to show the definition.
//	    TextColumn<RowData> defColumn = new TextColumn<RowData>() {
//	      @Override
//	      public String getValue(RowData object) {
//	        return object.getDefinition();
//	      }
//	    };
//	    table.addColumn(defColumn, app.getPlain("Definition"));


	    // Add a text column to show the value.
		Column<RowData, SafeHtml> valColumn = new Column<RowData, SafeHtml>(
		        new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(RowData object) {
				return SafeHtmlUtils.fromTrustedString(object.getAlgebra());
			}

		};
		table.addColumn(valColumn, app.getPlain("Value"));
	    
	    tableInit();	    
	}
	
	public void settingsChanged(AbstractSettings settings) {
		App.debug("ConstructinProtocolView.settingsChanged");
		ConstructionProtocolSettings cps = (ConstructionProtocolSettings)settings;

		boolean gcv[] = cps.getColsVisibility();
		if (gcv != null) if (gcv.length > 0)
			setColsVisibility(gcv);

//		update();
		getData().initView();
//		repaint();	
	}
	
	private void setColsVisibility(boolean[] colsVisibility) {
		//TODO
	}
	
	@Override
	public void updateNavigationBars() {
		// update the navigation bar of the protocol window
		protNavBar.update();
	
//		// update all registered navigation bars
//		int size = navigationBars.size();
//		for (int i = 0; i < size; i++) {
//			navigationBars.get(i).update();
//		}
	}
	
	@Override
	public void scrollToConstructionStep() {
		if (table != null) {
			int rowCount = table.getRowCount();
			if (rowCount == 0)
				return;

			int step = kernel.getConstructionStep();
			int row = -1; //it's possible that ConsStep == 0, so we need index -1 to deselect all rows
			for (int i = Math.max(step, 0); i < rowCount; i++) {
				if (data.getConstructionIndex(i) <= step)
					row = i;
				else
					break;
			}
			markRowsAtive(row);
		}
	}
	
	/**
	 * marks the rows (up to {@code index}) selected
	 * @param index int
	 */
	private void markRowsAtive(int index) {
		for (int i = 0; i < table.getRowCount(); i++) {
			if (i <= index) {
				GColorW color = (GColorW) data.getRow(i).getGeo().getAlgebraColor();
				table.getRowElement(i).setAttribute("style", "color:"+ GColor.getColorString(color));
			} else {
				table.getRowElement(i).setAttribute("style", "opacity:0.5");
			}
		}
	}
	
	public FlowPanel getCpPanel(){
		return cpPanel;
	}
	
	public void repaint(){
		tableInit();
	}
	
	private void makeTableRowsDragable() {
	    for (int i = 0; i < table.getRowCount(); i++) {
	    	table.getRowElement(i).setDraggable(Element.DRAGGABLE_TRUE);
	    }
    }	
	
	public void tableInit(){
//		data.updateAll();
		table.setRowCount(data.getrowList().size());
	    table.setRowData(0, data.getrowList());
	    table.setVisibleRange(0, data.getrowList().size()+1);
	    makeTableRowsDragable();
		scrollToConstructionStep();
	}
	
	class ConstructionTableDataW extends ConstructionTableData{

		public ConstructionTableDataW(SetLabels gui){
			super(gui);
//			ctDataImpl = new MyGAbstractTableModel();
		}
		
		/**
		 * 
		 * @param fromIndex
		 * @param toIndex
		 * @return 
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
			}
		}
		
		@Override
		public void fireTableRowsDeleted(int firstRow, int lastRow){
			//TODO: maybe it's not necessary to reinit the all table
			if(table != null){
				table.setRowCount(0);
				tableInit();
			}
		}

		public void updateAll(){
			// avoid too frequent App.debug messages in Trunk version
			// the goal of App.debug is debugging anyway
			// App.debug("ConstuctionTableDataW - implementation needed - just finishing");
			int size = rowList.size();

			for (int i = 0; i < size; ++i) {
				RowData row = rowList.get(i);
				row.updateAll();
			}
		}
	}

	public StyleBarW getStyleBar() {
	    if(styleBar == null){
	    	styleBar = new ConstructionProtocolStyleBarW((AppW) this.app);
	    }
	    return styleBar;
    }
}
