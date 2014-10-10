package geogebra.web.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.html5.main.AppW;
import geogebra.web.gui.util.StyleBarW;

public class ConstructionProtocolStyleBarW extends StyleBarW {

	public ConstructionProtocolStyleBarW(AppW app){
		super(app, App.VIEW_CONSTRUCTION_PROTOCOL);
		addViewButton();
	}

	@Override
	public void setOpen(boolean showStyleBar) {
		// TODO Auto-generated method stub
	}
}
