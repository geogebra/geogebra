package geogebra.touch.gui.elements.stylingbar;

import geogebra.touch.gui.elements.toolbar.ToolButton;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class StylingBarButton extends ToolButton
{
	private SVGResource icon;

	public StylingBarButton(SVGResource svg)
	{
		super(svg);
		this.icon = svg;
	}

	public StylingBarButton(SVGResource svg, ClickHandler handler)
	{
		this(svg);
		addDomHandler(handler, ClickEvent.getType());
	}

	public SVGResource getIcon()
	{
		return this.icon;
	}
}