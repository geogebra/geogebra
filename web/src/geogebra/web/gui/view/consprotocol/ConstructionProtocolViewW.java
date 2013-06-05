package geogebra.web.gui.view.consprotocol;

import geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import geogebra.common.main.App;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.ConstructionProtocolSettings;
import geogebra.web.main.AppW;

public class ConstructionProtocolViewW extends ConstructionProtocolView{

	private ConstructionProtocolNavigationW protNavBar;
	private AppW app;

	public ConstructionProtocolViewW(final AppW app) {
		this.app = app;
		kernel = app.getKernel();
		data = new ConstructionTableData();
		protNavBar = (ConstructionProtocolNavigationW) (app.getConstructionProtocolNavigation());
		protNavBar.register(this);
		
		ConstructionProtocolSettings cps = app.getSettings().getConstructionProtocol();
		settingsChanged(cps);
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
			boolean oldNotViewActive = kernel.isNotifyViewsActive();
			data.initView();
		}	
	}
}
