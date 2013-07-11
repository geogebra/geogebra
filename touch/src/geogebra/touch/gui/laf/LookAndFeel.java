package geogebra.touch.gui.laf;

import geogebra.touch.FileManagerM;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.model.TouchModel;

public interface LookAndFeel
{
	public void buildHeader(TabletGUI gui, TouchApp application, TouchModel touchModel, FileManagerM fm);

	public void setTitle(String title);

	public int getPanelsHeight();

	public int getAppBarHeight();

	public DefaultIcons getIcons();

	public TabletHeaderPanel getTabletHeaderPanel();
}
