package geogebra.touch.gui.elements.stylingbar;

import geogebra.touch.gui.elements.StandardImageButton;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class StylingBarButton extends StandardImageButton
{
	public StylingBarButton(SVGResource svg, ClickHandler handler)
	{
		super(svg);
		addDomHandler(handler, ClickEvent.getType());
	}
}