package geogebra.web.factories;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.BasicStroke;
import geogebra.common.awt.BufferedImageAdapter;
import geogebra.common.awt.Color;
import geogebra.common.awt.Dimension;
import geogebra.common.awt.GeneralPath;
import geogebra.common.awt.Line2D;
import geogebra.common.awt.Point2D;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;

public class AwtFactory extends geogebra.common.factories.AwtFactory {

	@Override
	public Color newColor(int RGB) {
		return new geogebra.web.awt.Color(RGB);
	}

	@Override
	public Color newColor(int red, int green, int blue) {
		return new geogebra.web.awt.Color(red, green, blue);
	}

	@Override
	public Color newColor(int red, int green, int blue, int alpha) {
		// TODO Auto-generated method stub
		return new geogebra.web.awt.Color(red, green, blue, alpha);
	}

	@Override
	public Color newColor(float red, float green, float blue, float alpha) {
		// TODO Auto-generated method stub
		return new geogebra.web.awt.Color(red,green,blue,alpha);
	}

	@Override
	public Color newColor(float red, float green, float blue) {
		return new geogebra.web.awt.Color(red, green, blue);
	}

	@Override
	public AffineTransform newAffineTransform() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle2D newRectangle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle newRectangle(int x, int y, int w, int h) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BufferedImageAdapter newBufferedImage(int pixelWidth,
	        int pixelHeight, int typeIntArgb) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dimension newDimension(int width, int height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point2D newPoint2D() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle newRectangle(int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point2D newPoint2D(double d, double coord) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeneralPath newGeneralPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BasicStroke newMyBasicStroke(float f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BasicStroke newBasicStroke(float width, int endCap, int lineJoin,
	        float miterLimit, float[] dash, float f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Line2D newLine2D() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle newRectangle(Rectangle bb) {
		// TODO Auto-generated method stub
		return null;
	}

}
