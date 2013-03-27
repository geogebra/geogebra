package geogebra.touch.gui.elements.header;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.PushButton;

public class HeaderImageButton extends PushButton
{
	public HeaderImageButton()
	{
		this.getElement().getStyle().setMarginLeft(3, Unit.PX);
		this.getElement().getStyle().setMarginRight(3, Unit.PX);
	}

	/**
	 * set background-images via HTML
	 */
	@Override
	public void setText(String text)
	{
		String html = "<img src=\"" + text + "\" style=\"width: 48px; height: 48px;\">";
		this.getElement().setInnerHTML(html);
	}
}