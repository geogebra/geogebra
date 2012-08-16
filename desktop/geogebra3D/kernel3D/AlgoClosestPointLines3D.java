/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;




public class AlgoClosestPointLines3D extends AlgoElement3D {

	private GeoLineND g3D, h3D;
	private AlgoDistanceLines3D helpAlgo;
	
	private GeoPoint3D geoPointOnG;

    public AlgoClosestPointLines3D(Construction c,
    		String label, GeoLineND g3D, GeoLineND h3D) {
        super(c);
        this.g3D = g3D;
        this.h3D = h3D;
        helpAlgo = new AlgoDistanceLines3D(c,null,g3D,h3D);
        helpAlgo.setPrintedInXML(false);
        geoPointOnG = new GeoPoint3D(c);
        setInputOutput(); // for AlgoElement

        // compute length
        compute();
        geoPointOnG.setLabel(label);
    }
    

    @Override
	public Algos getClassName() {
        return Algos.AlgoClosestPointLines3D;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement)h3D;
        input[1] = (GeoElement)g3D;

        super.setOutputLength(1);
        super.setOutput(0, (GeoElement3D)geoPointOnG);
        setDependencies(); // done by AlgoElement
    }
 
    GeoLineND getg() {
        return g3D;
    }
    GeoLineND geth() {
        return h3D;
    }

    public GeoPoint3D getPoint() {
    	return geoPointOnG;
    }
    
    @Override
	public void compute() {
    	geoPointOnG.setCoords(helpAlgo.getPointOnG(), false);
    	        
    }

	


	

    
}
