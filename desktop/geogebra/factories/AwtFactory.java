package geogebra.factories;

import geogebra.common.awt.Dimension;
import geogebra.common.awt.GeneralPath;
import geogebra.common.awt.Point2D;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.BufferedImageAdapter;
import geogebra.common.awt.Color;
import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.Rectangle2D;

public class AwtFactory extends geogebra.common.factories.AwtFactory{
	@Override
	public Color newColor(int RGB) {
		return new geogebra.awt.Color(RGB);
	}
	
	@Override
	public Color newColor(int red, int green, int blue) {
		return new geogebra.awt.Color(red, green, blue);
	}
	
	@Override
	public Color newColor(int red, int green, int blue, int alpha) {
		return new geogebra.awt.Color(red, green, blue, alpha);
	}
	
	@Override
	public Color newColor(float red, float green, float blue, float alpha) {
		return new geogebra.awt.Color(red, green, blue, alpha);
	}

	@Override
	public Color newColor(int red, float green, int blue) {
		return new geogebra.awt.Color(red, green, blue);
	}
	
	@Override
	public AffineTransform newAffineTransform() {
		return new geogebra.awt.AffineTransform();
	}

	@Override
	public Rectangle2D newRectangle() {
		return new geogebra.awt.Rectangle2D();
	}
	
	@Override
	public Rectangle newRectangle(int x,int y,int w, int h) {
		return new geogebra.awt.Rectangle(x,y,w,h);
	}

	@Override
	public BufferedImageAdapter newBufferedImage(int pixelWidth,
			int pixelHeight, int type) {
		// TODO Auto-generated method stub
		return new geogebra.awt.BufferedImage(pixelWidth,pixelHeight,type);
	}

	@Override
	public Dimension newDimension(int width, int height) {
		return new geogebra.awt.Dimension(width, height);
	}

	@Override
	public Point2D newPoint2D() {
		return new geogebra.awt.Point2D();
	}
	
	@Override
	public Point2D newPoint2D(double x,double y) {
		return new geogebra.awt.Point2D(x,y);
	}

	@Override
	public Rectangle newRectangle(int i, int j) {
		return new geogebra.awt.Rectangle(i, j);
	}
	
	@Override
	public GeneralPath newGeneralPath(){
		return new geogebra.awt.GeneralPath();
	}
}
