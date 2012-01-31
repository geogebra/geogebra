package geogebra.web.awt;

public class Color extends geogebra.common.awt.Color {
	
		private int r;
		private int g;
		private int b;
		private int alpha;
		public static final Color white = new Color(255,255,255);
		public static final Color black = new Color(0, 0, 0);
		public static final Color RED = new Color(255, 0, 0);
		public static final Color WHITE = new Color(255, 255, 255);
		public static final Color BLACK = new Color(0, 0, 0);
		public static final Color BLUE = new Color(0, 0, 255);
		public static final Color GRAY = new Color(128, 128, 128);
		public static final Color GREEN = new Color(0, 255, 0);
		public static final Color YELLOW = new Color(255, 255, 0);
		public static final Color DARK_GRAY = new Color(68, 68, 68);
		public static final Color red = new Color(255, 0, 0);
		public static final Color yellow = new Color(255, 255, 0);
		public static final Color green = new Color(0, 255, 0);
		public static final Color blue = new Color(0, 0, 255);
		public static final Color cyan = new Color(0, 255, 255);
		public static final Color magenta = new Color(255, 0, 255);
		public static final Color lightGray = new Color(192, 192, 192);
		public static final Color gray = new Color(128, 128, 128);
		public static final Color darkGray = new Color(68, 68, 68);
		
		private static final double FACTOR = 0.7;
	
	
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
	
	public Color(int r, int g, int b, int alpha) {
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
		setAlpha((int) (alpha*255));
		
		// TODO Auto-generated constructor stub
	}

	public Color(float red, float green, float blue) {
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
	
	public Color darker() {
    	return new Color(Math.max((int)(getRed()  *FACTOR), 0), 
			 Math.max((int)(getGreen()*FACTOR), 0),
			 Math.max((int)(getBlue() *FACTOR), 0));
    }

	@Override
	public geogebra.common.awt.Color brighter() {
		return new Color(Math.min((int)(getRed()  /FACTOR), 255), 
				 Math.min((int)(getGreen()/FACTOR), 255),
				 Math.min((int)(getBlue() /FACTOR), 255));
	}
	

}
