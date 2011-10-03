/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoVertex.java
 *
 * Created on 11. November 2001, 21:37
 */

package geogebra.kernel;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoVertex extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoConic c;  // input
    private GeoPoint [] vertex;  // output        
                                  
    transient private double temp1, temp2;
    private GeoVec2D b;
    private GeoVec2D [] eigenvec;
        
    AlgoVertex(Construction cons, String label, GeoConic c) {
        this(cons, c);
        GeoElement.setLabels(label, vertex);            
    }
    
    AlgoVertex(Construction cons, String [] labels, GeoConic c) {
        this(cons, c);
        GeoElement.setLabels(labels, vertex);            
    }
    
    AlgoVertex(Construction cons, GeoConic c) {
        super(cons);
        this.c = c;        
        vertex = new GeoPoint[4];       
        for (int i=0; i < vertex.length; i++) {
        	vertex[i] = new GeoPoint(cons);
        	// only first undefined point should be shown in algebra window 
        	vertex[i].showUndefinedInAlgebraView(i == 0);
      	}
        
        setInputOutput(); // for AlgoElement
        
        b = c.b;
        eigenvec = c.eigenvec;
                
        compute();                      
    }   
    
    public String getClassName() {
        return "AlgoVertex";
    }
    
    // for AlgoElement
    public void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;        
        
        output = vertex;        
        setDependencies(); // done by AlgoElement
    }    
    
    GeoConic getConic() { return c; }
    GeoPoint [] getVertex() { return vertex; }    
        
    protected final void compute() {  
        switch (c.type) {
            case GeoConic.CONIC_CIRCLE:                                      
            case GeoConic.CONIC_ELLIPSE:
                temp1 = c.halfAxes[0] * eigenvec[0].x;
                temp2 = c.halfAxes[0] * eigenvec[0].y;
                vertex[0].setCoords(b.x - temp1, b.y - temp2, 1.0);
                vertex[1].setCoords(b.x + temp1, b.y + temp2, 1.0);
                
                temp1 = c.halfAxes[1] * eigenvec[1].x;
                temp2 = c.halfAxes[1] * eigenvec[1].y;
                vertex[2].setCoords( b.x - temp1, b.y - temp2, 1.0);
                vertex[3].setCoords( b.x + temp1, b.y + temp2, 1.0);   
                break;
                
            case GeoConic.CONIC_HYPERBOLA:
                temp1 = c.halfAxes[0] * eigenvec[0].x;
                temp2 = c.halfAxes[0] * eigenvec[0].y;
                vertex[0].setCoords(b.x - temp1, b.y - temp2, 1.0d);
                vertex[1].setCoords(b.x + temp1, b.y + temp2, 1.0d);
                // third and fourth vertex undefined
                vertex[2].setUndefined();
                vertex[3].setUndefined();                
                break;
                
            case GeoConic.CONIC_PARABOLA:
            case GeoConic.CONIC_PARALLEL_LINES:
            case GeoConic.CONIC_DOUBLE_LINE:
                vertex[0].setCoords(b.x, b.y, 1.0);

                // other vertex undefined
                vertex[1].setUndefined();
                vertex[2].setUndefined();
                vertex[3].setUndefined();
                break;
                
            default:
                // no vertex defined
                vertex[0].setUndefined();
                vertex[1].setUndefined();
                vertex[2].setUndefined();
                vertex[3].setUndefined();
        }
    }
    
    public final String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("VertexOfA",c.getLabel());

    }
}
