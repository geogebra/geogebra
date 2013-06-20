package geogebra.web.gui.view.consprotocol;

import geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import geogebra.common.main.App;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.ConstructionProtocolSettings;
import geogebra.web.main.AppW;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class ConstructionProtocolViewW extends ConstructionProtocolView{

	private ConstructionProtocolNavigationW protNavBar;
	private AppW app;
	public FlowPanel cpPanel;
	private CellTable<RowData> table;

	public ConstructionProtocolViewW(final AppW app) {
		cpPanel = new FlowPanel();
		this.app = app;
		kernel = app.getKernel();
		data = new ConstructionTableData();
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
		//scrollPane.setScrollingEnabledX(false);
		//scrollPane.scrollToRight();
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
	    TextColumn<RowData> nameColumn = new TextColumn<RowData>() {
	      @Override
	      public String getValue(RowData object) {
	        return object.getName();
	      }
	    };
	    table.addColumn(nameColumn, app.getPlain("Name"));
	    
	    table.setRowData(0, data.getrowList());
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
		App.debug("ConstructionProtocolViewW.setColsVisibility - implementation needed - just finishing");
//		TableColumnModel model = table.getColumnModel();
		
		int k = Math.min(colsVisibility.length, data.columns.length);
		
		for(int i=0; i<k; i++){
//			TableColumn column = getTableColumns()[i];
//			model.removeColumn(column);
//			if (colsVisibility[i] == true){
//				model.addColumn(column);
//			} 
			//else {
			//	model.removeColumn(column);
			//}
			data.initView();
		}	
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
}
