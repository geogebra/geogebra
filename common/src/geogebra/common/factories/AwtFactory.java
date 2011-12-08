package geogebra.common.factories;

import geogebra.common.awt.Color;

public abstract class AwtFactory {
	public abstract Color newColor(int red, int green, int blue);
	public abstract Color newColor(int red, int green, int blue, int alpha);
	public abstract Color newColor(float red, float green, float blue, float alpha);

}
