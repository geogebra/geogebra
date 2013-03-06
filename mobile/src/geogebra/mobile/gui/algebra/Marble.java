package geogebra.mobile.gui.algebra;

import geogebra.mobile.gui.CommonResources;

import com.google.gwt.user.client.ui.SimplePanel;

public class Marble extends SimplePanel
{

	/**
	 * set background-images via HTML
	 */
	public void setImage(String text)
	{
		String html = "<img src=\"" + text + "\" style=\"height: 19px;margin-right: 5px;\">";
		this.getElement().setInnerHTML(html);
	}

	public void setchecked(boolean value)
	{
		if (value)
		{
			setImage(CommonResources.INSTANCE.algebra_shown().getSafeUri().asString());
		}
		else
		{
			setImage(CommonResources.INSTANCE.algebra_hidden().getSafeUri().asString());
		}
	}

}
