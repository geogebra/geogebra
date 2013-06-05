package geogebra.touch.gui.laf;

import geogebra.touch.TouchApp;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.model.GuiModel;

public interface LookAndFeel {

	void buildHeader(TabletGUI gui, TouchApp application, GuiModel giModel);

	void setTitle(String title);

	int getPanelsHeight();

	int getAppBarHeight();

}
