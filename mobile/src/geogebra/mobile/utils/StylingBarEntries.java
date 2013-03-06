package geogebra.mobile.utils;

import geogebra.common.awt.GColor;
import geogebra.mobile.gui.CommonResources;
import geogebra.web.awt.GColorW;

import org.vectomatic.dom.svg.ui.SVGResource;

public enum StylingBarEntries
{
	Point(GColor.blue, new SVGResource[] { CommonResources.INSTANCE.label() }), DependentPoints(GColor.darkGray,
	    new SVGResource[] { CommonResources.INSTANCE.label() }), Line(GColor.black,
	    new SVGResource[] { CommonResources.INSTANCE.properties_defaults() }), Polygon(new GColorW(153, 51, 0),
	    new SVGResource[] { CommonResources.INSTANCE.properties_defaults() });

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
