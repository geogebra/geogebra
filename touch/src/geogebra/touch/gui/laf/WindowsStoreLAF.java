package geogebra.touch.gui.laf;

import geogebra.touch.FileManagerM;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.model.TouchModel;

public class WindowsStoreLAF implements LookAndFeel {

	@Override
	public void buildHeader(TabletGUI gui,
			TouchApp application, TouchModel touchModel, FileManagerM fm) {

	}

	@Override
	public void setTitle(String title) {
		
	}

	@Override
	public int getPanelsHeight() {
		return 60;
	}

	@Override
	public int getAppBarHeight() {
		return 0;
	}

	

}
