package geogebra.touch.gui.laf;

import geogebra.touch.TouchApp;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.model.TouchModel;

public class DefaultLAF implements LookAndFeel
{

	private TabletHeaderPanel hp;

	@Override
	public void buildHeader(TabletGUI gui, TouchApp app, TouchModel touchModel)
	{
		this.hp = new TabletHeaderPanel(gui, app, touchModel);
		gui.setHeaderWidget(this.hp);
		gui.addResizeListener(this.hp);
	}

	@Override
	public void setTitle(String title)
	{
		this.hp.setTitle(title);
	}

	@Override
	public int getPanelsHeight()
	{
		return 122;
	}

	public TabletHeaderPanel getTabletHeaderPanel()
	{
		return this.hp;
	}

	@Override
	public int getAppBarHeight() {
		return 62;
	}
}
