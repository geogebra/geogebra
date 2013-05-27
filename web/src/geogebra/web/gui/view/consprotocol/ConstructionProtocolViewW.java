package geogebra.web.gui.view.consprotocol;

import geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import geogebra.common.main.App;
import geogebra.web.main.AppW;

public class ConstructionProtocolViewW implements ConstructionProtocolView{

	private ConstructionProtocolNavigationW protNavBar;
	private AppW app;
	ConstructionTableData data;

	public ConstructionProtocolViewW(final AppW app) {
		this.app = app;
		
		protNavBar = (ConstructionProtocolNavigationW) (app.getConstructionProtocolNavigation());
		protNavBar.register(this);	
	}
	
	public int getCurrentStepNumber() {
		//return data.getCurrentStepNumber();
		return app.getKernel().getConstructionStep();
	}

	public int getLastStepNumber() {
	    return app.getKernel().getConstruction().getLastGeoElement().getConstructionIndex();
    }

	public void setConstructionStep(int consStep) {
		App.debug("ConstructionProtocolViewW.setConstructionStep(int) - implementation needed");
	    // TODO Auto-generated method stub
	    
    }
	
	
}
