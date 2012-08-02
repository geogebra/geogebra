package geogebra.mobile.gui.elements;

import geogebra.mobile.utils.ToolBarCommand;

import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

public class EuclidianViewPanel extends LayoutPanel
{
	public EuclidianViewPanel()
	{
		this.addStyleName("euclidianview");
		
		for(ToolBarCommand t : ToolBarCommand.values())
		{
			this.add(new ToolButton(t));
		}
	}
}
