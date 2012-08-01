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
		this.add(new ToolButton(ToolBarCommand.Angle));
		this.add(new ToolButton(ToolBarCommand.Hyperbola));
		this.add(new ToolButton(ToolBarCommand.Area));
		this.add(new ToolButton(ToolBarCommand.DeleteObject));
		this.add(new ToolButton(ToolBarCommand.CheckBoxToShowHideObjects));
		this.add(new ToolButton(ToolBarCommand.Slope));
	}
}
