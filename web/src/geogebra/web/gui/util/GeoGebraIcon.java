package geogebra.web.gui.util;

import geogebra.common.awt.Color;
import geogebra.common.awt.RenderingHints;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.web.awt.BufferedImage;
import geogebra.web.awt.Dimension;
import geogebra.web.awt.Graphics2D;
import geogebra.web.openjdk.awt.geom.Polygon;

import javax.swing.ImageIcon;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;

public class GeoGebraIcon {

	public static Canvas createUpDownTriangleIcon(boolean isRollOver, boolean isEnabled) {
		int h = 18;
		int w = 12;
		
	    Canvas icon = Canvas.createIfSupported();
	    Graphics2D g2 = new Graphics2D(icon);
	    if (!isEnabled) {
	    	//AGImageIcon ic = new ImageIcon(image);
			//AGreturn ic;
	    	return icon;
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
	    
	    return icon;
    }

	/** creates LineStyle icon
	 * @param dashStyle
	 * @param thickness
	 * @param iconSize
	 * @param fgColor
	 * @param bgColor
	 * @return Canvas with icon drawn
	 */
	public static Canvas createLineStyleIcon(int dashStyle, int thickness, Dimension iconSize, Color fgColor, Color bgColor) {
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

		return image;
    }
	
	public static Canvas createEmptyIcon(int width, int height){

		Canvas image = Canvas.createIfSupported();
		image.setWidth(width+"px");
		image.setHeight(height+"px");
		image.setCoordinateSpaceHeight(height);
		image.setCoordinateSpaceWidth(width);
		return image;
	}

	/**
	 * @param pointStyle
	 * @param pointSize
	 * @param iconSize
	 * @param fgColor
	 * @param bgColor
	 * @return
	 */
	public static Canvas createPointStyleIcon(int pointStyle, int pointSize, Dimension iconSize, Color fgColor, Color bgColor) {
		GeoGebraIcon g = new GeoGebraIcon();
		PointStyleImage image = new PointStyleImage(iconSize, pointStyle, pointSize,  fgColor,  bgColor);

		return image.getCanvas();
    }

}
