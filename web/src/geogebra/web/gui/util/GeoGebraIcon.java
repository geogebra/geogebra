package geogebra.web.gui.util;


import geogebra.common.awt.Color;
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

}
