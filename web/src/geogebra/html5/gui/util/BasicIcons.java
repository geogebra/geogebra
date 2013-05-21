package geogebra.html5.gui.util;

import geogebra.common.awt.GColor;
import geogebra.html5.awt.GGraphics2DW;
import geogebra.html5.openjdk.awt.geom.Polygon;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;

public class BasicIcons {
	private static Canvas tmpCanvas;
	
	protected static Canvas getTmpCanvas(int w, int h) {
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
	
	public static ImageData createUpDownTriangleIcon(boolean isRollOver, boolean isEnabled) {
		int h = 18;
		int w = 12;
		
		Canvas icon = getTmpCanvas(w, h);
	    GGraphics2DW g2 = new GGraphics2DW(icon);
	    if (!isEnabled) {
	    	//AGImageIcon ic = new ImageIcon(image);
			//AGreturn ic;
	    	return g2.getImageData(0, 0, w, h);
	    }
	    
	    if (isRollOver) {
	    	g2.setColor(GColor.LIGHT_GRAY);
	    	g2.fillRect(0, 0, w-1, h-1);
	    }
	    
	    g2.setColor(GColor.GRAY);
	    
	    if(isRollOver)
			g2.setColor(GColor.BLACK);
		else
			g2.setColor(GColor.DARK_GRAY);
		
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
}
