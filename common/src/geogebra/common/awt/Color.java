package geogebra.common.awt;

public abstract class Color {

	public static Color black = null;
	public static Color white = null;
	public abstract int getRed();
	public abstract int getBlue();
	public abstract int getGreen();
	public abstract int getAlpha();
	/*ÉJ float[]*/ public abstract void getRGBColorComponents(float[] rgb);
	
	//public Color(float r, float g, float b, float alpha);
    public static int HSBtoRGB(float hue, float saturation, float brightness) {
    	int r = 0, g = 0, b = 0;
        	if (saturation == 0) {
    	    r = g = b = (int) (brightness * 255.0f + 0.5f);
    	} else {
    	    float h = (hue - (float)Math.floor(hue)) * 6.0f;
    	    float f = h - (float)java.lang.Math.floor(h);
    	    float p = brightness * (1.0f - saturation);
    	    float q = brightness * (1.0f - saturation * f);
    	    float t = brightness * (1.0f - (saturation * (1.0f - f)));
    	    switch ((int) h) {
    	    case 0:
    		r = (int) (brightness * 255.0f + 0.5f);
    		g = (int) (t * 255.0f + 0.5f);
    		b = (int) (p * 255.0f + 0.5f);
    		break;
    	    case 1:
    		r = (int) (q * 255.0f + 0.5f);
    		g = (int) (brightness * 255.0f + 0.5f);
    		b = (int) (p * 255.0f + 0.5f);
    		break;
    	    case 2:
    		r = (int) (p * 255.0f + 0.5f);
    		g = (int) (brightness * 255.0f + 0.5f);
    		b = (int) (t * 255.0f + 0.5f);
    		break;
    	    case 3:
    		r = (int) (p * 255.0f + 0.5f);
    		g = (int) (q * 255.0f + 0.5f);
    		b = (int) (brightness * 255.0f + 0.5f);
    		break;
    	    case 4:
    		r = (int) (t * 255.0f + 0.5f);
    		g = (int) (p * 255.0f + 0.5f);
    		b = (int) (brightness * 255.0f + 0.5f);
    		break;
    	    case 5:
    		r = (int) (brightness * 255.0f + 0.5f);
    		g = (int) (p * 255.0f + 0.5f);
    		b = (int) (q * 255.0f + 0.5f);
    		break;
    	    }
    	}
    	return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
        }

}
