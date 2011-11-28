package geogebra.common.util;

public interface ColorAdapter {

	int getRed();
	int getBlue();
	int getGreen();
	int getAlpha();
	float[] getRGBColorComponents(float[] rgb);	

}
