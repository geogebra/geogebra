package geogebra.touch.utils;

import geogebra.common.awt.GColor;
import geogebra.html5.awt.GColorW;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.laf.DefaultIcons;

import org.vectomatic.dom.svg.ui.SVGResource;

public enum StylingBarEntries
{
	Point(GColor.blue, new SVGResource[] { getLafIcons().color(), getLafIcons().label() }), DependentPoints(GColor.darkGray,
	    new SVGResource[] { getLafIcons().color(), getLafIcons().label() }), Line(GColor.black, new SVGResource[] {
	    getLafIcons().color(), getLafIcons().properties_default() }), Polygon(new GColorW(153, 51, 0), new SVGResource[] {
	    getLafIcons().color(), getLafIcons().properties_default() }), Move(null, new SVGResource[] {
	    getLafIcons().show_or_hide_the_axes(), getLafIcons().show_or_hide_the_grid() }), Angle(GColor.green, new SVGResource[] {
	    getLafIcons().color(), getLafIcons().properties_default() });

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

	private static DefaultIcons getLafIcons()
	{
		return TouchEntryPoint.getLookAndFeel().getIcons();
	}
}
