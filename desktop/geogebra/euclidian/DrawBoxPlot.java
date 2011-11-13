package geogebra.euclidian;

import geogebra.kernel.AlgoBoxPlot;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class DrawBoxPlot extends Drawable {
	private boolean isVisible, labelVisible;
	private double [] coords = new double[2];
	private GeneralPathClipped gp;
	private GeoNumeric sum;
	private AlgoBoxPlot algo;
	private NumberValue a,b;
	public DrawBoxPlot(EuclidianView view, GeoNumeric n) {
    	this.view = view; 	
    	sum = n;
		geo = n;
		
		n.setDrawable(true);
    	
    	init();  
        update();
    }
	private void init() {
    	algo = (AlgoBoxPlot) geo.getDrawAlgorithm();    	
		a = algo.getA();
        b = algo.getB();  
    }
	@Override
	public void draw(Graphics2D g2) {
		if (isVisible) {
        	try {
	            if (geo.doHighlighting()) {
	                g2.setPaint(sum.getSelColor());
	                g2.setStroke(selStroke);            
	                g2.draw(gp);           
	            } 
        	} catch (Exception e) {
        		Application.debug(e.getMessage());
        	}
            
        	try {
        		fill(g2, gp, false); // fill using default/hatching/image as appropriate
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        	
			try {
				if (geo.lineThickness > 0) {
					g2.setPaint(sum.getObjectColor());
					g2.setStroke(objStroke);                                   
					g2.draw(gp);   
				}
			} catch (Exception e) {
				Application.debug(e.getMessage());
			}    
			
            if (labelVisible) {
				g2.setFont(view.fontConic);
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
            }        
        }
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public boolean hit(int x, int y) {
		return gp != null && (gp.contains(x, y) || gp.intersects(x-3, y-3, 6, 6));
	}

	@Override
	public boolean isInside(Rectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;

	}

	@Override
	public void update() {
		isVisible = geo.isEuclidianVisible();
        if (!isVisible) return;
        if(!geo.getDrawAlgorithm().equals(geo.getParentAlgorithm()))
        	init();
		labelVisible = geo.isLabelVisible();            
		updateStrokes(sum);
		
		if (gp == null)
			gp = new GeneralPathClipped(view);
		// init gp
		gp.reset();
		double yOff = a.getDouble();
		double yScale = b.getDouble();
	
		// plot upper/lower sum rectangles
		double [] leftBorder = algo.getLeftBorders();
		
		coords[0] = leftBorder[0];						
		coords[1] = -yScale + yOff;
		view.toScreenCoords(coords);
		gp.moveTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[0];						
		coords[1] = yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[0];						
		coords[1] = 0 + yOff;
		view.toScreenCoords(coords);
		gp.moveTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[1];						
		coords[1] = 0 + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[1];						
		coords[1] = yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[3];						
		coords[1] = yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[3];						
		coords[1] = -yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[1];						
		coords[1] = -yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[1];						
		coords[1] = 0 + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[3];						
		coords[1] = 0 + yOff;
		view.toScreenCoords(coords);
		gp.moveTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[4];						
		coords[1] = 0 + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[4];						
		coords[1] = yScale + yOff;
		view.toScreenCoords(coords);
		gp.moveTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[4];						
		coords[1] = -yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[2];						
		coords[1] = yScale + yOff;
		view.toScreenCoords(coords);
		gp.moveTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[2];						
		coords[1] = -yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			

		// gp on screen?		
		if (!gp.intersects(0,0, view.width, view.height)) {				
			isVisible = false;
        	// don't return here to make sure that getBounds() works for offscreen points too
		}		

		if (labelVisible) {
			xLabel = (int)coords[0];
			yLabel = (int)coords[1] - view.fontSize;
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}
    

	}

}
