/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;


public class AlgoCentroidPolygon extends AlgoElement {

    private GeoPolygon p;  // input
    private GeoPoint centroid; // output                         
        
    public AlgoCentroidPolygon(Construction cons, String label,GeoPolygon p) {
        super(cons);
        this.p = p;       
        centroid = new GeoPoint(cons);  
        setInputOutput(); // for AlgoElement
                
        compute();              
        centroid.setLabel(label);         
    }   
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoCentroidPolygon;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = p;        

        super.setOutputLength(1);
        super.setOutput(0, centroid);
        setDependencies(); // done by AlgoElement
    }    
    
    GeoPolygon getPolygon() { return p; }
    public GeoPoint getPoint() { return centroid; }    
        
    @Override
	public final void compute() {
        p.calcCentroid(centroid);
    }
    
    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("CentroidOfA",p.getLabel(tpl));
    }

	// TODO Consider locusequability
}
