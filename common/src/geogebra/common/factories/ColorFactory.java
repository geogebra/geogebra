package geogebra.common.factories;

import geogebra.common.awt.Color;

public abstract class ColorFactory {
	public abstract Color getColor(int red, int green, int blue);
	public abstract Color getColor(int red, int green, int blue, int alpha);
	public abstract Color getColor(float red, float green, float blue, float alpha);

}
