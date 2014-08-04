package geogebra.web.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.web.gui.util.StyleBarW;
import geogebra.web.main.AppW;

public class ConstructionProtocolStyleBarW extends StyleBarW {

	public ConstructionProtocolStyleBarW(AppW app){
		super(app, App.VIEW_CONSTRUCTION_PROTOCOL);
		getViewButton();
	}

	@Override
	public void setOpen(boolean showStyleBar) {
		// TODO Auto-generated method stub
	}
}
