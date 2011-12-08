package geogebra.factories;

import geogebra.common.adapters.FactoryAdapter;
import geogebra.common.awt.Color;

public class ColorFactory extends geogebra.common.factories.ColorFactory{

	public static ColorFactory prototype = null;
	
	@Override
	public Color getColor(int red, int green, int blue) {
		return new geogebra.awt.Color(red, green, blue);
	}
	
	@Override
	public Color getColor(int red, int green, int blue, int alpha) {
		return new geogebra.awt.Color(red, green, blue, alpha);
	}
	
	@Override
	public Color getColor(float red, float green, float blue, float alpha) {
		return new geogebra.awt.Color(red, green, blue, alpha);
	}
}
