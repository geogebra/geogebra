package geogebra.mobile.gui.elements;

import geogebra.mobile.gui.Resources;

import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;

public class TabletToolBar extends ButtonBar
{
	public TabletToolBar()
	{
		this.addStyleName("toolbar");

		ToolButton[] b = new ToolButton[10];
		for (int i = 0; i < 10; i++)
		{
			b[i] = new ToolButton(Resources.INSTANCE.tux());	
			b[i].setTitle("bla" + i);
			b[i].addStyleName("toolbutton" + i);
			this.add(b[i]);
		}		
	}	
}
