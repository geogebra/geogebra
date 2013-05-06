package geogebra.touch.gui.elements;

import geogebra.common.awt.GColor;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.PushButton;

public class StandardImageButton extends PushButton
{
	private SVGResource icon;
	private boolean active;
	public static final int HEIGHT = 48;
	public static final int BORDER_WIDTH = 2;

	public StandardImageButton(SVGResource icon)
	{

		this.getElement().getStyle().setMarginLeft(3, Unit.PX);
		this.getElement().getStyle().setMarginRight(3, Unit.PX);

		// transparent
		this.getElement().getStyle().setBorderColor("rgba(0,0,0,0)");
		this.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		this.getElement().getStyle().setBorderWidth(BORDER_WIDTH, Unit.PX);

		this.setIcon(icon);
	}

	public void setIcon(SVGResource icon)
	{
		this.icon = icon;
		String html = "<img src=\"" + this.icon.getSafeUri().asString() + "\" style=\"width: " + HEIGHT + "px; height: " + HEIGHT + "px;\">";
		this.getElement().setInnerHTML(html);

	}

	public SVGResource getIcon()
	{
		return this.icon;
	}

	public void setActive(boolean active)
	{
		this.active = active; 
		if(active)
		{
			this.getElement().getStyle().setBorderColor(GColor.BLUE.toString());
		}
		else
		{
			this.getElement().getStyle().setBorderColor("rgba(0,0,0,0)");
		}
	}
	
	public boolean isActive()
	{
		return this.active; 
	}
	
	@Override
	public int getOffsetHeight()
	{
		return 64;
	}
	
	@Override
	public int getOffsetWidth()
	{
		return 64;
	}

}