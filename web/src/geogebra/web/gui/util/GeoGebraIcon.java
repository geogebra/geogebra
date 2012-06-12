package geogebra.web.gui.util;


import geogebra.common.awt.Color;
import geogebra.common.awt.RenderingHints;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.main.AbstractApplication;
import geogebra.web.awt.BasicStroke;
import geogebra.web.awt.Dimension;
import geogebra.web.awt.Font;
import geogebra.web.awt.Graphics2D;
import geogebra.web.openjdk.awt.geom.Polygon;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

public class GeoGebraIcon {

	private static Canvas tmpCanvas;

	public static ImageData createUpDownTriangleIcon(boolean isRollOver, boolean isEnabled) {
		int h = 18;
		int w = 12;
		
		Canvas icon = getTmpCanvas(w, h);
	    Graphics2D g2 = new Graphics2D(icon);
	    if (!isEnabled) {
	    	//AGImageIcon ic = new ImageIcon(image);
			//AGreturn ic;
	    	return g2.getImageData(0, 0, w, h);
	    }
	    
	    if (isRollOver) {
	    	g2.setColor(Color.LIGHT_GRAY);
	    	g2.fillRect(0, 0, w-1, h-1);
	    }
	    
	    g2.setColor(Color.GRAY);
	    
	    if(isRollOver)
			g2.setColor(Color.BLACK);
		else
			g2.setColor(Color.DARK_GRAY);
		
		int midx = w/2;
		int midy = h/2;

		Polygon p = new Polygon();
		// make a triangle.
		p.addPoint(midx-3,midy-1);
		p.addPoint(midx+3,midy-1);
		p.addPoint(midx,midy-6);

		g2.fillPolygon(p);  

		// make a triangle.
		p = new Polygon();
		p.addPoint(midx-3,midy+1);
		p.addPoint(midx+3,midy+1);
		p.addPoint(midx,midy+6);

		g2.fillPolygon(p);

		/*
		g2.drawLine(x, y, x+6, y);
		g2.drawLine(x+1, y+1, x+5, y+1);
		g2.drawLine(x+2, y+2, x+4, y+2);
		g2.drawLine(x+3, y+3, x+3, y+3);
		 */
	    
	    return g2.getImageData(0, 0, w, h);
    }

	/** creates LineStyle icon
	 * @param dashStyle
	 * @param thickness
	 * @param iconSize
	 * @param fgColor
	 * @param bgColor
	 * @return Canvas with icon drawn
	 */
	public static ImageData createLineStyleIcon(int dashStyle, int thickness, Dimension iconSize, Color fgColor, Color bgColor) {
		int h = iconSize.getHeight();
		int w = iconSize.getWidth();

		Canvas c = getTmpCanvas(w, h);
	    Graphics2D g2 = new Graphics2D(c);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if(bgColor != null){
			g2.setPaint(bgColor);
			g2.fillRect(0, 0, w, h);
		}

		// draw dashed line
		g2.setPaint(fgColor);
		g2.setStroke(new geogebra.web.awt.BasicStroke(EuclidianStatic.getStroke(thickness, dashStyle)));
		int mid = h / 2;
		g2.drawLine(4, mid, w - 4, mid);

		return g2.getImageData(0, 0, w, h);
    }
	
	public static ImageData createEmptyIcon(int width, int height){

		Canvas image =	getTmpCanvas(width, height);
		image.setWidth(width+"px");
		image.setHeight(height+"px");
		image.setCoordinateSpaceHeight(height);
		image.setCoordinateSpaceWidth(width);
		return image.getContext2d().getImageData(0, 0, width, height);
	}

	/**
	 * @param pointStyle
	 * @param pointSize
	 * @param iconSize
	 * @param fgColor
	 * @param bgColor
	 * @return
	 */
	public static ImageData createPointStyleIcon(int pointStyle, int pointSize, Dimension iconSize, Color fgColor, Color bgColor) {
		GeoGebraIcon g = new GeoGebraIcon();
		PointStyleImage image = new PointStyleImage(iconSize, pointStyle, pointSize,  fgColor,  bgColor);
		return image.getCanvas().getContext2d().getImageData(0, 0, image.getCanvas().getCanvasElement().getWidth(), image.getCanvas().getCanvasElement().getHeight());
    }
	
	public static ImageData createColorSwatchIcon(float alpha, Dimension iconSize, Color fgColor, Color bgColor){

		int h = iconSize.getHeight();
		int w = iconSize.getWidth();
		int offset = 2;
		float thickness = 3;

		// if fgColor is null then make it a transparent white
		if(fgColor == null)
			fgColor = geogebra.common.factories.AwtFactory.prototype.newColor(255,255,255,1);
		
		Canvas c = getTmpCanvas(w,h);
		Graphics2D g2 = new Graphics2D(c);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		--h;
		--w;

		if(bgColor != null){
			g2.setPaint(bgColor);
			g2.fillRect(0, 0, w, h);
		}

		// interior fill color using alpha level

		//float[] rgb = new float[3];
		//fgColor.getRGBColorComponents(rgb);
		g2.setPaint(new geogebra.web.awt.Color(fgColor.getRed(), fgColor.getGreen(), fgColor.getBlue(),alpha*255));
		g2.fillRect(offset, offset, w-2*offset, h-2*offset);

		// border color with alpha = 1
		g2.setPaint(fgColor);
		g2.setStroke(new BasicStroke(thickness)); 
		g2.drawRect(offset, offset, w-2*offset, h-2*offset);

		return g2.getImageData(0, 0, iconSize.getWidth(), iconSize.getHeight());
	}

	public static ImageData createTextSymbolIcon(String symbol,Font font, Dimension iconSize, Color fgColor, Color bgColor){

		int h = iconSize.getHeight();
		int w = iconSize.getWidth();

		Canvas c = getTmpCanvas(w, h);
		Graphics2D g2 = new Graphics2D(c);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if(bgColor != null)
			g2.setBackground(bgColor);

		g2.setColor (fgColor);
		g2.setFont (new Font (font.getFontFamily()));

		//fm = g2.getFontMetrics ();
		int symbolWidth = (int) g2.getCanvas().getContext2d().measureText(symbol).getWidth();
		//int ascent = fm.getMaxAscent ();
		//int descent= fm.getMaxDescent ();
		int msg_x = w/2 - symbolWidth/2;
		int msg_y = h/2; //- descent/2 + ascent/2;

		g2.drawString (symbol, msg_x, msg_y-2);
		g2.fillRect(1, h-5, w-1, 3);

		/*ImageIcon ic = new ImageIcon(image);
		//ensureIconSize(ic, iconSize);

		return ic;*/
		return g2.getImageData(0, 0, w, h);
	}
	
	public static ImageData createNullSymbolIcon(int width, int height){

		Canvas c = getTmpCanvas(width, height);

		Graphics2D g2 = new Graphics2D(c);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setPaint(Color.GRAY);
		// draw a rectangle with an x inside
		g2.drawRect(3, 3, width-6, height-6);
		int k = 7;
		g2.drawLine(k, k, width-k, height-k);
		g2.drawLine(k, height-k, width-k, k );
		return g2.getImageData(0, 0, width, height);
	}

	public static ImageData createDownTriangleIcon() {
		int h = 18;
		int w = 12;
		
		Canvas icon = getTmpCanvas(w,h);
	    icon.setCoordinateSpaceHeight(h);
	    icon.setCoordinateSpaceWidth(w);
	    Graphics2D g2 = new Graphics2D(icon);
	    g2.setColor(Color.WHITE);
	    g2.fillRect(0, 0, g2.getCanvas().getCoordinateSpaceWidth(), g2.getCanvas().getCoordinateSpaceHeight());
	    g2.setColor(Color.GRAY);
	    
	    int midx = w/2;
		int midy = h/2;
		
		Polygon p = new Polygon();
		p.addPoint(midx-3,midy-3);
		p.addPoint(midx+3,midy-3);
		p.addPoint(midx,midy+3);

		g2.fillPolygon(p);
	    return g2.getImageData(0, 0, w, h);
    }

	public static ImageData createStringIcon(String str, Font font, boolean isBold, boolean isItalic, 
			boolean isCentered, Dimension iconSize, Color fgColor, Color bgColor){
		int h = iconSize.getHeight();
		int w = iconSize.getWidth();

		Canvas c = getTmpCanvas(w,h);
		Graphics2D g2 = new Graphics2D(c);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if(bgColor != null)
			g2.setBackground(bgColor);

		g2.setColor (fgColor);
		//font = font.deriveFont((h-6)*1.0f);
		if(isBold)
			font = (Font) font.deriveFont(Font.BOLD);
		if(isItalic)
			font = (Font) font.deriveFont(Font.ITALIC);
		g2.setFont (font);


		//FontMetrics fm = g2.getFontMetrics ();
		TextMetrics fm = g2.getCanvas().getContext2d().measureText(str);
		double symbolWidth = fm.getWidth();
		//int ascent = fm.getMaxAscent ();
		//int descent= fm.getMaxDescent ();
		double x = (isCentered) ? w/2 - symbolWidth/2 : 1;
		double mid_y = 0; // there is not easy way to check the height of the text now h/2 - descent/2 + ascent/2 - 1;

		g2.drawString (str,(int) x, (int) mid_y);

		return g2.getImageData(0, 0, w, h);
    }

	private static Canvas getTmpCanvas(int w, int h) {
	    if (tmpCanvas == null) {
	    	tmpCanvas = Canvas.createIfSupported();
	    }
	    Context2d ctx = tmpCanvas.getContext2d();
	    tmpCanvas.setCoordinateSpaceWidth(w);
	    tmpCanvas.setCoordinateSpaceHeight(h);
	    ctx.setTransform(1, 0, 0, 1, 0, 0);
	    
	    ctx.clearRect(0, 0, tmpCanvas.getCoordinateSpaceWidth(), tmpCanvas.getCoordinateSpaceHeight());
	    return tmpCanvas;
    }

	public static ImageData createFileImageIcon(AbstractApplication app, String fileName, float alpha, Dimension iconSize){

		int h = iconSize.getHeight();
		int w = iconSize.getWidth();
		Canvas c = getTmpCanvas(w,h);
		c.setCoordinateSpaceWidth(w);
		c.setCoordinateSpaceHeight(h);
		c.getContext2d().fillText("?", 0, 0);
		
		return c.getContext2d().getImageData(0, 0, w, h);
	}
	
	public static ImageData ensureIconSize(ImageData icon, Dimension iconSize){

		int h = iconSize.getHeight();
		int w = iconSize.getWidth();
		int h2 = icon.getHeight();
		int w2 = icon.getWidth();
		if(h2 == h && w2 == w) 
			return icon;

		int wInset = (w - w2) > 0 ? (w-w2)/2 : 0;
		int hInset = (h - h2) > 0 ? (h-h2)/2 : 0;
		

		Graphics2D g2 = new Graphics2D(getTmpCanvas(w, h));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		try {	
			if(icon !=null){
				g2.putImageData(icon, wInset, hInset);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return g2.getImageData(0, 0, w, h);
	}

	public static ImageData joinIcons(ImageData leftIcon,
            ImageResource rightIcon) {
		int w1 = leftIcon.getWidth();
		int w2 = rightIcon.getWidth();
		int h1 = leftIcon.getHeight();
		int h2 = rightIcon.getHeight();
		int h = Math.max(h1, h2);
		int mid = h/2;
		Canvas c = getTmpCanvas(w1 + w2, h);
		Graphics2D g2 = new Graphics2D(c);
		g2.putImageData(leftIcon, 0, mid - h1/2);
		g2.getCanvas().getContext2d().drawImage(ImageElement.as(new Image(rightIcon.getSafeUri()).getElement()), w1, mid - h2 / 2, w2, h2);

		return g2.getImageData(0, 0, w1 +  w2, h);
    }

}
