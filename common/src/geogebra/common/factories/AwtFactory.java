package geogebra.common.factories;

import geogebra.common.awt.BufferedImageAdapter;
import geogebra.common.awt.Color;
import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.Rectangle2D;

public abstract class AwtFactory {
	public static AwtFactory prototype = null;
	public abstract Color newColor(int RGB);
	public abstract Color newColor(int red, int green, int blue);
	public abstract Color newColor(int red, int green, int blue, int alpha);
	public abstract Color newColor(float red, float green, float blue, float alpha);
	public abstract AffineTransform newAffineTransform();
	public abstract Rectangle2D newRectangle();
	public abstract BufferedImageAdapter newBufferedImage(int pixelWidth,
			int pixelHeight, int typeIntArgb);
}
