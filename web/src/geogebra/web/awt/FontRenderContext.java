package geogebra.web.awt;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;

public class FontRenderContext extends geogebra.common.awt.FontRenderContext {
	
	private Context2d context;
	
	public FontRenderContext(Context2d ctx) {
		this.context = ctx;
	}
	
	public int measureText(String text, String cssFontString) {
		String oldFont = context.getFont();
		context.setFont(cssFontString);
		TextMetrics measure = context.measureText(text);
		context.setFont(oldFont);
		return (int) measure.getWidth();
	}

}
