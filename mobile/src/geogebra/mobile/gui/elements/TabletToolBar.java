package geogebra.mobile.gui.elements;

import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBarButtonBase;

public class TabletToolBar extends ButtonBar
{
	public TabletToolBar()
	{
		this.addStyleName("toolbar");

		ButtonBarButtonBase[] b = new ButtonBarButtonBase[10];
		for (int i = 0; i < 10; i++)
		{
			b[i] = new ButtonBarButtonBase(null);	
			b[i].setTitle("bla" + i);
			b[i].addStyleName("toolbutton" + i);
			this.add(b[i]);
		}

	}
}
