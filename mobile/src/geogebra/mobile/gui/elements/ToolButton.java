package geogebra.mobile.gui.elements;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.dom.client.Style.Unit;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBarButtonBase;

public class ToolButton extends ButtonBarButtonBase
{

	public ToolButton(SVGResource icon)
	{		
		super(null);
		this.addStyleName("toolbutton");
		super.getElement().getStyle().setHeight(44, Unit.PX);
		super.getElement().getStyle().setBackgroundImage("url(" + icon.getSafeUri().asString() + ")");
	}

}
