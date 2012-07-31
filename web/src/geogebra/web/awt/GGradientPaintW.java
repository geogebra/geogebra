package geogebra.web.awt;

import geogebra.common.awt.GColor;

import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;

public class GGradientPaintW implements geogebra.common.awt.GGradientPaint {
	private GColor color1,color2;
	private float x1,x2,y1,y2;
	

	public CanvasGradient getGradient(Context2d c){
		CanvasGradient gradient = c.createLinearGradient(x1, y1, x2, y2);
		gradient.addColorStop(0, GColor.getColorString(color1));
		gradient.addColorStop(1, GColor.getColorString(color2));
		return gradient;
	}

	public GGradientPaintW(float x1,float y1,geogebra.common.awt.GColor color1,
			float x2,float y2,geogebra.common.awt.GColor color2){
		this.x1=x1;
		this.x2=x2;
		this.y1=y1;
		this.y2=y2;
		this.color1=color1;
		this.color2=color2;
	}

	public GGradientPaintW(GGradientPaintW gpaint) {
		this.x1 = gpaint.x1;
		this.x2 = gpaint.x2;
		this.y1 = gpaint.y1;
		this.y2 = gpaint.y2;
		this.color1 = gpaint.color1;
		this.color2 = gpaint.color2;
	}
}
