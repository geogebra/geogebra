package geogebra.touch.gui.laf;

import geogebra.touch.TouchApp;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.model.TouchModel;

public interface LookAndFeel {

	void buildHeader(TabletGUI gui, TouchApp application, TouchModel touchModel);

	void setTitle(String title);

	int getPanelsHeight();

	int getAppBarHeight();

}
