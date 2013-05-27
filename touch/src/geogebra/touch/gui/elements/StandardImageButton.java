package geogebra.touch.gui.elements;

import org.vectomatic.dom.svg.ui.SVGResource;
import com.google.gwt.user.client.ui.PushButton;

public class StandardImageButton extends PushButton
{
	private SVGResource icon;
	private boolean active;

	public StandardImageButton(SVGResource icon)
	{
		this.setStyleName("button");

		this.setIcon(icon);
	}

	public void setIcon(SVGResource icon)
	{
		this.icon = icon;
		String html = "<img src=\"" + this.icon.getSafeUri().asString() + "\" />";
		this.getElement().setInnerHTML(html);

	}

	public SVGResource getIcon()
	{
		return this.icon;
	}

	public void setActive(boolean active)
	{
		this.active = active; 
	}
	
	public boolean isActive()
	{
		return this.active; 
	}
}