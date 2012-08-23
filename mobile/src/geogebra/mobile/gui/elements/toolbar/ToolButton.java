package geogebra.mobile.gui.elements.toolbar;

import org.vectomatic.dom.svg.ui.SVGResource;

import geogebra.mobile.utils.ToolBarCommand;

import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBarButtonBase;

/**
 * A Button for the {@link ToolBar}, allowing an SVG graphic to be set as background <br>
 * css-styling: {@code -webkit-background-size: 100%;} <br>
 * for the correct size of the SVG
 * 
 * @see ButtonBarButtonBase
 * @author Matthias Meisinger
 * 
 */
public class ToolButton extends ButtonBarButtonBase
{

	private ToolBarCommand cmd;

	public ToolButton(ToolBarCommand cmd)
	{
		super(null);
		this.addStyleName("toolbutton");

		this.cmd = cmd;
		super.getElement().getStyle().setBackgroundImage(cmd.getIconUrlAsString());
	}

	public ToolButton(SVGResource icon)
	{
		super(null);
		this.addStyleName("toolbutton");
		super.getElement().getStyle().setBackgroundImage("url(" + icon.getSafeUri().asString() + ")");
	}

	public ToolBarCommand getCmd()
	{
		return this.cmd;
	}

	public void setCmd(ToolBarCommand cmd)
	{
		this.cmd = cmd;
		super.getElement().getStyle().setBackgroundImage(cmd.getIconUrlAsString());
	}

}
