package geogebra.mobile.gui.elements;

import geogebra.mobile.utils.ToolBarCommand;

import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;

/**
 * @see ButtonBar
 * 
 * @author Matthias Meisinger
 * 
 */
public class ToolBar extends ButtonBar
{

	public ToolBar()
	{
		this.addStyleName("toolbar");
	}

	public void makeTabletToolBar()
	{
		//TODO add correct buttons for the toolbar
		for(ToolBarCommand t : ToolBarCommand.values())
		{
			this.add(new ToolButton(t));
		}
	}
}
