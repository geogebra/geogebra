package geogebra.web.awt;

import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;

import geogebra.common.awt.Color;

public class GradientPaint implements geogebra.common.awt.GradientPaint {
	private Color color1,color2;
	private float x1,x2,y1,y2;
	

	public CanvasGradient getGradient(Context2d c){
		CanvasGradient gradient = c.createLinearGradient(x1, y1, x2, y2);
		gradient.addColorStop(0, Color.getColorString(color1));
		gradient.addColorStop(1, Color.getColorString(color2));
		return gradient;
	}

	public GradientPaint(float x1,float y1,geogebra.common.awt.Color color1,
			float x2,float y2,geogebra.common.awt.Color color2){
		this.x1=x1;
		this.x2=x2;
		this.y1=y1;
		this.y2=y2;
		this.color1=color1;
		this.color2=color2;
	}
}
