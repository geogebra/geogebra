package geogebra.touch.gui.laf;

import geogebra.touch.FileManagerM;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.model.TouchModel;

public class WindowsStoreLAF extends DefaultLAF
{
	@Override
	public void setTitle(String title)
	{

	}

	@Override
	public int getPanelsHeight()
	{
		return 60;
	}

	@Override
	public int getAppBarHeight()
	{
		return 0;
	}

	@Override
	public DefaultResources getIcons()
	{
		// FIXME return windows specific icons
		return DefaultResources.INSTANCE;
	}

	@Override
	public TabletHeaderPanel getTabletHeaderPanel()
	{
		return null;
	}
	
	@Override
	public void buildHeader(TabletGUI gui, TouchApp app, TouchModel touchModel,
			FileManagerM fm) {

	}
	
	@Override
	public boolean isMouseDownIgnored()
	{
	  return true;
	}
}
