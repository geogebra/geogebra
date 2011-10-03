/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoFoci.java
 *
 * Created on 11. November 2001, 21:37
 */

package geogebra.kernel;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoFocus extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoConic c;  // input
    private GeoPoint [] focus;  // output     
    
    transient private double temp1, temp2;
    GeoVec2D b;
    GeoVec2D [] eigenvec;
        
    AlgoFocus(Construction cons, String label, GeoConic c) {
        this(cons, c);
        GeoElement.setLabels(label, focus); 
    }
    
    AlgoFocus(Construction cons, String [] labels, GeoConic c) {
        this(cons, c);
        GeoElement.setLabels(labels, focus); 
    }
    
    AlgoFocus(Construction cons, GeoConic c) {
        super(cons);
        this.c = c;     
        focus = new GeoPoint[2];
        for (int i=0; i < focus.length; i++) {
        	focus[i] = new GeoPoint(cons);        	
        	// only first undefined point should be shown in algebra window 
        	focus[i].showUndefinedInAlgebraView(i == 0);
      	}
        
        setInputOutput(); // for AlgoElement
        
        b = c.b;
        eigenvec = c.eigenvec;        
                
        compute();                                          
    }   
       
    
    public String getClassName() {
        return "AlgoFocus";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;        
        
        output = focus;        
        setDependencies(); // done by AlgoElement
    }    
    
    GeoConic getConic() { return c; }
    GeoPoint [] getFocus() { return focus; }    
        
    protected final void compute() {  
        switch (c.type) {
            case GeoConic.CONIC_CIRCLE:
                focus[0].setCoords(b.x, b.y, 1.0);
                focus[1].setCoords(b.x, b.y, 1.0); 
                break;                
                
            case GeoConic.CONIC_ELLIPSE:
            case GeoConic.CONIC_HYPERBOLA:
                temp1 = c.linearEccentricity * eigenvec[0].x;
                temp2 = c.linearEccentricity * eigenvec[0].y;
                focus[0].setCoords( b.x - temp1, b.y - temp2, 1.0d);
                focus[1].setCoords( b.x + temp1, b.y + temp2, 1.0d);
                break;
                
            case GeoConic.CONIC_PARABOLA:
                temp1 = c.p / 2;
                focus[0].setCoords( b.x + temp1 * eigenvec[0].x,
                                              b.y + temp1 * eigenvec[0].y,
                                              1.0 ); 
                // second focus undefined
                focus[1].setUndefined();
                break;
                
            default:
                // both focus undefined
                focus[0].setUndefined();
                focus[1].setUndefined();
        }
    }
    
    public final String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("FocusOfA",c.getLabel());

    }
}
