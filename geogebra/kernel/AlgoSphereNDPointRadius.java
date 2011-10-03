/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoCirclePointRadius.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoQuadricND;
import geogebra.kernel.kernelND.GeoSegmentND;

/**
 *
 * @author Markus + Mathieu
 * 
 * @version 
 * 
 * Generalization of algo for circle/sphere
 */
public abstract class AlgoSphereNDPointRadius extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPointND M; // input
    private NumberValue r; // input
    private GeoElement rgeo;
    private GeoQuadricND sphereND; // output    
    
    private int type;
    final static int TYPE_RADIUS  = 0;
    final static int TYPE_SEGMENT = 1;

    protected AlgoSphereNDPointRadius(
            Construction cons,
            String label,
            GeoPointND M,
            NumberValue r) {
        	
            this(cons, M, r);
            sphereND.setLabel(label);
        }
        
    protected AlgoSphereNDPointRadius(
            Construction cons,
            String label,
            GeoPointND M,
            GeoSegmentND segment, boolean dummy) {
        	
            this(cons, M, segment, dummy);
            sphereND.setLabel(label);
        }
        
    public AlgoSphereNDPointRadius(
            Construction cons,
            GeoPointND M,
            NumberValue r) {
        	
            super(cons);
            
            type=TYPE_RADIUS;
            
            this.M = M;
            this.r = r;
            rgeo = r.toGeoElement();
            sphereND = createSphereND(cons);
            
            setInputOutput(); // for AlgoElement

            compute();            
        }
    
    protected AlgoSphereNDPointRadius(
            Construction cons,
            GeoPointND M,
            GeoSegmentND rgeo, boolean dummy) {
        	
            super(cons);
            
            type=TYPE_SEGMENT;  
            
            this.M = M;
            this.rgeo= (GeoElement) rgeo;
            
            sphereND = createSphereND(cons);
            
            setInputOutput(); // for AlgoElement

            compute();            
        }
    
    
    /** return a conic (2D) or a quadric (3D)
     * @param cons
     * @return a conic (2D) or a quadric (3D)
     */
    abstract protected GeoQuadricND createSphereND(Construction cons);
    


    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement) M;
        input[1] = rgeo;
        output = new GeoElement[1];
        output[0] = sphereND;
        setDependencies(); // done by AlgoElement
    }

    public GeoQuadricND getSphereND() {
        return sphereND;
    }
    
    
    protected GeoPointND getM() {
        return M;
    }
    
    protected GeoElement getRGeo() {
        return rgeo;
    }
    
    // compute circle with midpoint M and radius r
    protected final void compute() {
        switch (type) {
        case TYPE_RADIUS:
        	sphereND.setSphereND(M, r.getDouble());
        	break;
        case TYPE_SEGMENT:
        	sphereND.setSphereND(M, (GeoSegment)rgeo);
        	break;
        }
    }
    
    protected int getType(){
    	return this.type;
    }
}
