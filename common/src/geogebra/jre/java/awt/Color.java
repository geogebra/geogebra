package java.awt;

public class Color {
	
		private int r;
		private int g;
		private int b;
		private float alpha;
	public static final Color black = new Color();
	public static final Color white = new Color(255,255,255);
	public static final Color blue = new Color(0,0,255);
	public static final Color darkGray = new Color(64,64,64);
	public static final Color lightGray = new Color(192,192,192);
	
	
	public Color() {
		setRed(0);
		setGreen(0);
		setBlue(0);
		setAlpha(255);
	}

	public Color(int r, int g, int b) {
		setRed(r);
		setGreen(g);
		setBlue(b);
		setAlpha(255);
		// TODO Auto-generated constructor stub
	}

	public Color(int rgb) {
		int b = rgb % 256;
		int g = (rgb / 256) % 256;
		int r = ((rgb / 256) / 256) % 256;
		setRed(r);
		setGreen(g);
		setBlue(b);
		setAlpha(255);
	}
	
	public Color(int r, int g, int b, float alpha) {
		setRed(r);
		setGreen(g);
		setBlue(b);
		setAlpha(alpha);
		// TODO Auto-generated constructor stub
	}

	public Color(float r, float g, float b, float alpha) {
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
		setAlpha(alpha*255);
		
		// TODO Auto-generated constructor stub
	}

	public void setRed(int r) {
		this.r = r;
	}

	public int getRed() {
		return r;
	}

	public void setGreen(int g) {
		this.g = g;
	}

	public int getGreen() {
		return g;
	}

	public void setBlue(int b) {
		this.b = b;
	}

	public int getBlue() {
		return b;
	}

	public void setAlpha(float alpha2) {
		this.alpha = alpha2;
	}

	public float getAlpha() {
		return alpha;
	}

	public void getRGBColorComponents(float[] rgb) {
		rgb[0] = (float) (getRed() / 255.0);
		rgb[1] = (float) (getGreen() / 255.0);
		rgb[2] = (float) (getBlue() / 255.0);
		// TODO Auto-generated method stub
		
	}

	public int getRGBOnly() {
		return (((getRed()*256)+ getGreen())* 256)+getBlue();
	}

}
