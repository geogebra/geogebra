package geogebra.touch.gui.elements.header;

import com.googlecode.mgwt.ui.client.widget.HeaderButton;

public class HeaderImageButton extends HeaderButton
{

	/**
	 * set background-images via HTML
	 */
	@Override
	public void setText(String text)
	{
		String html = "<img src=\"" + text + "\" style=\"height: 100%;margin-right: 5px;\">";
		this.getElement().setInnerHTML(html);
	}

}
