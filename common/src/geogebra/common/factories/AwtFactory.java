package geogebra.common.factories;

import geogebra.common.awt.Color;
import geogebra.common.awt.AffineTransform;

public abstract class AwtFactory {
	public static AwtFactory prototype = null;
	public abstract Color newColor(int red, int green, int blue);
	public abstract Color newColor(int red, int green, int blue, int alpha);
	public abstract Color newColor(float red, float green, float blue, float alpha);
	public abstract AffineTransform newAffineTransform();
}
