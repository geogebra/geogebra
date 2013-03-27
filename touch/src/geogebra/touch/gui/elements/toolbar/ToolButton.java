package geogebra.touch.gui.elements.toolbar;

import geogebra.touch.gui.elements.header.HeaderImageButton;
import geogebra.touch.utils.ToolBarCommand;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * A Button for the {@link ToolBar}, allowing an SVG graphic to be set as
 * background <br>
 * css-styling: {@code -webkit-background-size: 100%;} <br>
 * for the correct size of the SVG
 * 
 * @see ButtonBarButtonBase
 * @author Matthias Meisinger
 * 
 */
public class ToolButton extends HeaderImageButton
{
	private ToolBarCommand cmd;

	public ToolButton(ToolBarCommand cmd)
	{
		this.cmd = cmd;
		super.setText(cmd.getIconUrlAsString());
	}

	public ToolButton(SVGResource icon)
	{
		super.setText(icon.getSafeUri().asString());
	}

	public ToolBarCommand getCmd()
	{
		return this.cmd;
	}

	public void setCmd(ToolBarCommand cmd)
	{
		this.cmd = cmd;
		super.setText(cmd.getIconUrlAsString());
	}
}
