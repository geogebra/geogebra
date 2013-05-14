package geogebra.web.gui.view.consprotocol;

import geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import geogebra.common.gui.view.consprotocol.ConstructionProtocolView.ConstructionTableData;
import geogebra.web.main.AppW;

public class ConstructionProtocolViewW implements ConstructionProtocolView{

	private ConstructionProtocolNavigationW protNavBar;
	private AppW app;
	ConstructionTableData data;

	public ConstructionProtocolViewW(final AppW app) {
		this.app = app;
		
		protNavBar = app.getConstructionProtocolNavigation();
		protNavBar.register(this);	
	}
	
	public int getCurrentStepNumber() {
		//return data.getCurrentStepNumber();
		return app.getKernel().getConstructionStep();
	}

	public int getLastStepNumber() {
	    return app.getKernel().getConstruction().getLastGeoElement().getConstructionIndex();
    }
	
	
}
