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
import geogebra.common.kernel.Region;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.main.App;


public class AlgoPoint3DInRegion extends AlgoElement3D {

	
	private Region region; // input
    private GeoPoint3D P; // output       

    public AlgoPoint3DInRegion(
        Construction cons,
        Region region,
        Coords coords) {
        super(cons);
        this.region = region;
        P = new GeoPoint3D(cons, region);
        
        setInputOutput(); // for AlgoElement
        
        //App.printStacktrace(""+coords);

        if (coords!=null){
        	P.setCoords(coords); 
        }

        // compute 
        compute();

        
    }
    
    public AlgoPoint3DInRegion(
            Construction cons,
            String label,
            Region region,
            Coords coords) {
    	
    	this(cons, region, coords);
        P.setLabel(label);
    }
    
    
    

    @Override
	public Algos getClassName() {
        return Algos.AlgoPoint3DInRegion;
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
    	
    	input = new GeoElement[1];  	
        input[0] = region.toGeoElement();

        setOnlyOutput(P);
        setDependencies(); // done by AlgoElement
        
     }

    public GeoPoint3D getP() {
        return P;
    }
    
    Region getRegion() {
        return region;
    }

    @Override
	public final void compute() {
    	
    	if (input[0].isDefined()) {	 
	        //Application.debug("coords=\n"+P.getCoordsInD(3));
	        region.regionChanged(P);
	        //Application.debug("coords=\n"+P.getCoordsInD(3)+"\nrp=\n"+P.getRegionParameters().getT1()+"\n"+P.getRegionParameters().getT2());
	        //P.updateCoords();
    	} else {
    		P.setUndefined();
    	}
    	
    }

    @Override
	final public String toString(StringTemplate tpl) {
        StringBuilder sb = new StringBuilder();
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("PointInA",input[0].getLabel(tpl)));
        
        return sb.toString();
    }

	// TODO Consider locusequability
}
