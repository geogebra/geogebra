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

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;


/**
 * Algorithm for conic vertices
 * @author  Markus
 */
public class AlgoVertex extends AlgoElement {

    private GeoConic c;  // input
    private GeoPoint [] vertex;  // output        
                                  
    transient private double temp1, temp2;
    private GeoVec2D b;
    private GeoVec2D [] eigenvec;
        
    /**
     * @param cons construction
     * @param label label for ouputs (for A outputs A_1,A_2,... will be created)
     * @param c conic
     */
    AlgoVertex(Construction cons, String label, GeoConic c) {
        this(cons, c);
        GeoElement.setLabels(label, vertex);            
    }
    /**
     * @param cons construction
     * @param labels labels for ouputs
     * @param c conic
     */
    public AlgoVertex(Construction cons, String [] labels, GeoConic c) {
        this(cons, c);
        GeoElement.setLabels(labels, vertex);            
    }
    /**
     * 
     * @param cons construction
     * @param c conic
     */
    public AlgoVertex(Construction cons, GeoConic c) {
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
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoVertex;
    }
    
    // for AlgoElement
    @Override
	public void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;        
         
        super.setOutput(vertex);
        setDependencies(); // done by AlgoElement
    }    
    
    /**
     * @return input conic
     */
    GeoConic getConic() { return c; }
    /**
     * 
     * @return array of conic vertices
     */
    public GeoPoint [] getVertex() { return vertex; }    
        
    @Override
	public final void compute() {  
        switch (c.type) {
            case GeoConicNDConstants.CONIC_CIRCLE:                                      
            case GeoConicNDConstants.CONIC_ELLIPSE:
                temp1 = c.halfAxes[0] * eigenvec[0].getX();
                temp2 = c.halfAxes[0] * eigenvec[0].getY();
                vertex[0].setCoords(b.getX() - temp1, b.getY() - temp2, 1.0);
                vertex[1].setCoords(b.getX() + temp1, b.getY() + temp2, 1.0);
                
                temp1 = c.halfAxes[1] * eigenvec[1].getX();
                temp2 = c.halfAxes[1] * eigenvec[1].getY();
                vertex[2].setCoords( b.getX() - temp1, b.getY() - temp2, 1.0);
                vertex[3].setCoords( b.getX() + temp1, b.getY() + temp2, 1.0);   
                break;
                
            case GeoConicNDConstants.CONIC_HYPERBOLA:
                temp1 = c.halfAxes[0] * eigenvec[0].getX();
                temp2 = c.halfAxes[0] * eigenvec[0].getY();
                vertex[0].setCoords(b.getX() - temp1, b.getY() - temp2, 1.0d);
                vertex[1].setCoords(b.getX() + temp1, b.getY() + temp2, 1.0d);
                // third and fourth vertex undefined
                vertex[2].setUndefined();
                vertex[3].setUndefined();                
                break;
                
            case GeoConicNDConstants.CONIC_PARABOLA:
            case GeoConicNDConstants.CONIC_PARALLEL_LINES:
            case GeoConicNDConstants.CONIC_DOUBLE_LINE:
                vertex[0].setCoords(b.getX(), b.getY(), 1.0);

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
    
    @Override
	public final String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("VertexOfA",c.getLabel(tpl));

    }

	// TODO Consider locusequability
}
