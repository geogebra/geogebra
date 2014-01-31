package geogebra.touch.utils;

import geogebra.common.awt.GColor;
import geogebra.common.main.GeoGebraColorConstants;

public enum StyleBarDefaultSettings {

	
	Point(GColor.blue, new OptionType[] { OptionType.Color,OptionType.PointStyle,
			OptionType.CaptionStyle }), 
	PointOnObject(
			GeoGebraColorConstants.LIGHTBLUE, new OptionType[] {
					OptionType.Color,OptionType.PointStyle,
					OptionType.CaptionStyle }), 
	DependentPoints(GColor.darkGray, new OptionType[] { OptionType.Color,OptionType.PointStyle,
					OptionType.CaptionStyle }), 
	Line(GColor.black, new OptionType[] { OptionType.Color,	OptionType.CaptionStyle, OptionType.LineStyle }), 
	Polygon(
			GeoGebraColorConstants.BROWN, new OptionType[] {
					OptionType.Color,	OptionType.CaptionStyle, OptionType.LineStyle }), 
	Move(
			null, new OptionType[] { OptionType.Axes,OptionType.Grid, OptionType.StandardView }), 
	Angle(
			GeoGebraColorConstants.DARKGREEN, new OptionType[] {
					OptionType.Color,	OptionType.CaptionStyle, OptionType.LineStyle });


	private GColor defaultColor;

	private OptionType[] options;

	StyleBarDefaultSettings(final GColor color, final OptionType[] options) {
		this.defaultColor = color;
		this.options = options;
	}

	public GColor getColor() {
		return this.defaultColor;
	}
	
	public OptionType[] getOptions() {
		return this.options;
	}
}
