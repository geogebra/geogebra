package geogebra.touch.gui.elements;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.PushButton;

public class StandardImageButton extends PushButton
{
	private SVGResource icon;
	public static final int HEIGHT = 48; 

	public StandardImageButton(SVGResource icon)
	{

		this.getElement().getStyle().setMarginLeft(3, Unit.PX);
		this.getElement().getStyle().setMarginRight(3, Unit.PX);

		this.setIcon(icon);
	}

	public void setIcon(SVGResource icon)
	{
		this.icon = icon;
		String html = "<img src=\"" + this.icon.getSafeUri().asString() + "\" style=\"width: "+ HEIGHT +"px; height: "+ HEIGHT +"px;\">";
		this.getElement().setInnerHTML(html);

	}

	public SVGResource getIcon()
	{
		return this.icon;
	}
}