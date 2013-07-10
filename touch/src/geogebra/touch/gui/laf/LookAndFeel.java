package geogebra.touch.gui.laf;

import geogebra.touch.FileManagerM;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.model.TouchModel;

public interface LookAndFeel {

	void buildHeader(TabletGUI gui, TouchApp application, TouchModel touchModel, FileManagerM fm);

	void setTitle(String title);

	int getPanelsHeight();

	int getAppBarHeight();

}
