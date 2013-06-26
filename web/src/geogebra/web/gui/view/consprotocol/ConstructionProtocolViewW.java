package geogebra.web.gui.view.consprotocol;

import geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import geogebra.common.main.App;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.ConstructionProtocolSettings;
import geogebra.web.main.AppW;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class ConstructionProtocolViewW extends ConstructionProtocolView{

	private ConstructionProtocolNavigationW protNavBar;
	private AppW app;
	public FlowPanel cpPanel;
	CellTable<RowData> table;

	public ConstructionProtocolViewW(final AppW app) {
		cpPanel = new FlowPanel();
		this.app = app;
		kernel = app.getKernel();
		data = new ConstructionTableDataW();
		protNavBar = (ConstructionProtocolNavigationW) (app.getConstructionProtocolNavigation());
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
		
		initGui();
		
		ConstructionProtocolSettings cps = app.getSettings().getConstructionProtocol();
		settingsChanged(cps);
	}
	
//	My first attempt was a FlexTable for table, this code created for that.
//	I guess it's not needed more, maybe I'll remove this.
//	public void initGUI(){
//		for (int k = 0; k < data.columns.length; k++) {
//			if ((data.columns[k].getTitle() == "number") ||
//					(data.columns[k].getTitle() == "name") ||
//					(data.columns[k].getTitle() == "definition"))
//				table.setText(0, k, data.columns[k].getTitle());
//		}		
//	}
    
	public void initGui(){
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
	    TextColumn<RowData> valColumn = new TextColumn<RowData>() {
	      @Override
	      public String getValue(RowData object) {
	        return object.getAlgebra();
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
		App.debug("ConstructionProtocolViewW.setColsVisibility - implementation needed");
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
	
	public FlowPanel getCpPanel(){
		return cpPanel;
	}
	
	public void tableInit(){
	    table.setRowData(0, data.getrowList());
	    table.setVisibleRange(0, data.getrowList().size()+1);
	}
	
	class ConstructionTableDataW extends ConstructionTableData{

		public ConstructionTableDataW(){
			super();
//			ctDataImpl = new MyGAbstractTableModel();
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
	}
}
