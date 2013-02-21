package geogebra.web.awt;

import geogebra.common.awt.GColor;

public class GColorW extends GColor {

	private int r;
	private int g;
	private int b;
	private int alpha;

	private static final double FACTOR = 0.7;

	public GColorW(GColorW col) {
		setRed(col.getRed());
		setGreen(col.getGreen());
		setBlue(col.getBlue());
		setAlpha(col.getAlpha());
	}

	public GColorW() {
		setRed(0);
		setGreen(0);
		setBlue(0);
		setAlpha(255);
	}

	public GColorW(int r, int g, int b) {
		setRed(r);
		setGreen(g);
		setBlue(b);
		setAlpha(255);
	}

	public GColorW(int rgb) {
		int b = rgb % 256;
		int g = (rgb / 256) % 256;
		int r = ((rgb / 256) / 256) % 256;
		setRed(r);
		setGreen(g);
		setBlue(b);
		setAlpha(255);
	}
	
	public GColorW(int r, int g, int b, int alpha) {
		setRed(r);
		setGreen(g);
		setBlue(b);
		setAlpha(alpha);
	}

	public GColorW(float r, float g, float b, float alpha) {
		if (r>1) 
			r=1;
		if (r<0)
			r=0;
		
		if (g>1) 
			g=1;
		if (g<0)
			g=0;
		
		
		if (b>1) 
			b=1;
		if (b<0)
			b=0;
		
		
		setRed((int) (r*255));
		setGreen((int) (g*255));
		setBlue((int) (b*255));
		setAlpha((int) (alpha*255));
		
	}

	public GColorW(float red, float green, float blue) {
	    this(red,green,blue,1);
    }

	public void setRed(int r) {
		this.r = r;
	}

	@Override
    public int getRed() {
		return r;
	}

	public void setGreen(int g) {
		this.g = g;
	}

	@Override
    public int getGreen() {
		return g;
	}

	public void setBlue(int b) {
		this.b = b;
	}

	@Override
    public int getBlue() {
		return b;
	}

	public void setAlpha(int alpha2) {
		this.alpha = alpha2;
	}

	@Override
    public int getAlpha() {
		return alpha;
	}

	@Override
    public void getRGBColorComponents(float[] rgb) {
		rgb[0] = (float) (getRed() / 255.0);
		rgb[1] = (float) (getGreen() / 255.0);
		rgb[2] = (float) (getBlue() / 255.0);
		
	}

	public int getRGBOnly() {
		return (((getRed()*256)+ getGreen())* 256)+getBlue();
	}
	
	public GColorW darker() {
    	return new GColorW(Math.max((int)(getRed()  *FACTOR), 0), 
			 Math.max((int)(getGreen()*FACTOR), 0),
			 Math.max((int)(getBlue() *FACTOR), 0));
    }

	@Override
	public geogebra.common.awt.GColor brighter() {
		return new GColorW(Math.min((int)(getRed()  /FACTOR), 255), 
				 Math.min((int)(getGreen()/FACTOR), 255),
				 Math.min((int)(getBlue() /FACTOR), 255));
	}

	// this could have been in GColor,
	// but is its default toString needed somewhere?
	@Override
	public String toString() {
		return getColorString(this);
	}
}
