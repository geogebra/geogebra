package geogebra.common.kernel.geos;

import geogebra.common.kernel.arithmetic.NumberValue;

public interface GeoNumericInterface extends NumberValue, GeoElementInterface {

	public static int DEFAULT_SLIDER_WIDTH_RW = 4;//private
	public static int DEFAULT_SLIDER_WIDTH_PIXEL = 100;//private
	public static int DEFAULT_SLIDER_WIDTH_PIXEL_ANGLE = 72;//private
	/** Default maximum value when displayed as slider*/
	public static double DEFAULT_SLIDER_MIN = -5;
	/** Default minimum value when displayed as slider*/
	public static double DEFAULT_SLIDER_MAX = 5;
	/** Default increment when displayed as slider*/
	public static double DEFAULT_SLIDER_INCREMENT = 0.1;
	/** Default increment when displayed as slider*/
	public static double DEFAULT_SLIDER_SPEED = 1;



	double getValue();

	void setValue(double d);

	void updateRandomGeo();

	boolean isRandomGeo();
	
	public double getRealWorldLocX();
	
	public double getRealWorldLocY();
	
	public void setRealWorldLoc(double x, double y);
	
	public boolean isSliderFixed();//for final
	
	public boolean isLabelSet();
	
	public void updateRandom();

	public GeoElement getIntervalMaxObject();
	
	public GeoElement getIntervalMinObject();
	
	public boolean isChangeable();//GeoElement

	public boolean isIntervalMinActive();
	
	public boolean isIntervalMaxActive();
	
	public double getIntervalMin();
	
	public double getIntervalMax();

	double getAnimationSpeed();

	public double getAnimationStep();
}
