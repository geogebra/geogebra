package geogebra.touch.utils;

import geogebra.common.awt.GColor;
import geogebra.html5.awt.GColorW;
import geogebra.touch.gui.CommonResources;

import org.vectomatic.dom.svg.ui.SVGResource;

public enum StylingBarEntries
{
	Point(GColor.blue, new SVGResource[] { CommonResources.INSTANCE.color(), CommonResources.INSTANCE.label() }), DependentPoints(GColor.darkGray,
	    new SVGResource[] { CommonResources.INSTANCE.color(), CommonResources.INSTANCE.label() }), Line(GColor.black,
	    new SVGResource[] { CommonResources.INSTANCE.color(), CommonResources.INSTANCE.properties_default() }), Polygon(new GColorW(153, 51, 0),
	    new SVGResource[] { CommonResources.INSTANCE.color(), CommonResources.INSTANCE.properties_default() }), Move(null, 
	    new SVGResource[] { CommonResources.INSTANCE.show_or_hide_the_axes(),  CommonResources.INSTANCE.show_or_hide_the_grid()}), 
	    Angle(GColor.green, new SVGResource[] { CommonResources.INSTANCE.color(), CommonResources.INSTANCE.properties_default() });

	GColor defaultColor;
	SVGResource[] entry;

	StylingBarEntries(GColor color, SVGResource[] entries)
	{
		this.defaultColor = color;
		this.entry = entries;
	}

	public GColor getColor()
	{
		return this.defaultColor;
	}

	public SVGResource[] getResources()
	{
		return this.entry;
	}
}
