package geogebra.web.gui.util;

import geogebra.common.awt.Color;
import geogebra.common.awt.RenderingHints;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.web.awt.BasicStroke;
import geogebra.web.awt.BufferedImage;
import geogebra.web.awt.Dimension;
import geogebra.web.awt.Font;
import geogebra.web.awt.Graphics2D;
import geogebra.web.openjdk.awt.geom.Polygon;

import javax.swing.ImageIcon;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.user.client.ui.Image;

public class GeoGebraIcon {

	public static CanvasElement createUpDownTriangleIcon(boolean isRollOver, boolean isEnabled) {
		int h = 18;
		int w = 12;
		
	    Canvas icon = Canvas.createIfSupported();
	    Graphics2D g2 = new Graphics2D(icon);
	    if (!isEnabled) {
	    	//AGImageIcon ic = new ImageIcon(image);
			//AGreturn ic;
	    	return icon.getCanvasElement();
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
	    
	    return icon.getCanvasElement();
    }

	/** creates LineStyle icon
	 * @param dashStyle
	 * @param thickness
	 * @param iconSize
	 * @param fgColor
	 * @param bgColor
	 * @return Canvas with icon drawn
	 */
	public static CanvasElement createLineStyleIcon(int dashStyle, int thickness, Dimension iconSize, Color fgColor, Color bgColor) {
		int h = iconSize.getHeight();
		int w = iconSize.getWidth();

		Canvas image = Canvas.createIfSupported();
	    Graphics2D g2 = new Graphics2D(image);
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

		return image.getCanvasElement();
    }
	
	public static CanvasElement createEmptyIcon(int width, int height){

		Canvas image = Canvas.createIfSupported();
		image.setWidth(width+"px");
		image.setHeight(height+"px");
		image.setCoordinateSpaceHeight(height);
		image.setCoordinateSpaceWidth(width);
		return image.getCanvasElement();
	}

	/**
	 * @param pointStyle
	 * @param pointSize
	 * @param iconSize
	 * @param fgColor
	 * @param bgColor
	 * @return
	 */
	public static CanvasElement createPointStyleIcon(int pointStyle, int pointSize, Dimension iconSize, Color fgColor, Color bgColor) {
		GeoGebraIcon g = new GeoGebraIcon();
		PointStyleImage image = new PointStyleImage(iconSize, pointStyle, pointSize,  fgColor,  bgColor);

		return image.getCanvas().getCanvasElement();
    }
	
	public static CanvasElement createColorSwatchIcon(float alpha, Dimension iconSize, Color fgColor, Color bgColor){

		int h = iconSize.getHeight();
		int w = iconSize.getWidth();
		int offset = 2;
		float thickness = 3;

		// if fgColor is null then make it a transparent white
		if(fgColor == null)
			fgColor = geogebra.common.factories.AwtFactory.prototype.newColor(255,255,255,1);
		
		Canvas c = Canvas.createIfSupported();
		c.setWidth(w+"px");
		c.setHeight(h+"px");
		c.setCoordinateSpaceHeight(h);
		c.setCoordinateSpaceWidth(w);
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
		g2.setPaint(new geogebra.web.awt.Color(fgColor.getRed(), fgColor.getGreen(), fgColor.getBlue()/*,alpha*255*/));
		g2.fillRect(offset, offset, w-2*offset, h-2*offset);

		// border color with alpha = 1
		g2.setPaint(fgColor);
		g2.setStroke(new BasicStroke(thickness)); 
		g2.drawRect(offset, offset, w-2*offset, h-2*offset);

		return g2.getCanvas().getCanvasElement();
	}

	public static CanvasElement createTextSymbolIcon(String symbol,Font font, Dimension iconSize, Color fgColor, Color bgColor){

		int h = iconSize.getHeight();
		int w = iconSize.getWidth();

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = new Graphics2D(image.getCanvas());
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
		return g2.getCanvas().getCanvasElement();
	}
	
	public static CanvasElement createNullSymbolIcon(int width, int height){

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = new Graphics2D(image.getCanvas());
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setPaint(Color.GRAY);
		// draw a rectangle with an x inside
		g2.drawRect(3, 3, width-6, height-6);
		int k = 7;
		g2.drawLine(k, k, width-k, height-k);
		g2.drawLine(k, height-k, width-k, k );
		return g2.getCanvas().getCanvasElement();
	}

	public static CanvasElement createDownTriangleIcon() {
		int h = 18;
		int w = 12;
		
	    Canvas icon = Canvas.createIfSupported();
	    icon.setHeight(h+"px");
	    icon.setWidth(w+"px");
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
	    return g2.getCanvas().getCanvasElement();
    }


}
