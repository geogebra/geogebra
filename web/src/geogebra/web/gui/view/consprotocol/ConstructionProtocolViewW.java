package geogebra.web.gui.view.consprotocol;

import geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
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
	}
	
}
