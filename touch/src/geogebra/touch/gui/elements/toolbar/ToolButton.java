package geogebra.touch.gui.elements.toolbar;

import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.utils.ToolBarCommand;

/**
 * A Button for the {@link ToolBar}, allowing an SVG graphic to be set as
 * background <br>
 * 
 * @author Matthias Meisinger
 * 
 */
public class ToolButton extends StandardImageButton
{
	private ToolBarCommand cmd;

	public ToolButton(ToolBarCommand cmd)
	{
		super(cmd.getIcon());
		this.cmd = cmd;
	}

	public ToolBarCommand getCmd()
	{
		return this.cmd;
	}

	public void setCmd(ToolBarCommand cmd)
	{
		super.setIcon(cmd.getIcon());
		this.cmd = cmd;
	}
}
