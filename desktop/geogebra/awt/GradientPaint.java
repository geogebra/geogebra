package geogebra.awt;

public class GradientPaint implements geogebra.common.awt.GradientPaint{
	private java.awt.GradientPaint impl;
	public GradientPaint(float x1,float y1,geogebra.common.awt.Color color1,
			float x2,float y2,geogebra.common.awt.Color color2){
		impl = new java.awt.GradientPaint(x1, y1, Color.getAwtColor(color1),
				x2, y2, Color.getAwtColor(color2));
	}
	public java.awt.GradientPaint getPaint(){
		return impl;
	}
}
