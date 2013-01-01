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
import geogebra.common.kernel.Path;
import geogebra.common.kernel.algos.AlgoClosestPoint;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.kernelND.GeoPointND;




public class AlgoClosestPoint3D extends AlgoClosestPoint {


    public AlgoClosestPoint3D(Construction c, Path path, GeoPointND point) {
        super(c, path, point);
        
    }
    
    public AlgoClosestPoint3D(Construction cons, String label, Path path, GeoPointND point) {
    	super(cons,label,path,point);
	}
    
    protected void createOutputPoint(Construction cons, Path path){
        P = new GeoPoint3D(cons);
        ((GeoPoint3D) P).setPath(path);
    }
    

    @Override
	public Algos getClassName() {
        return Algos.AlgoClosestPoint3D;
    }
    
    @Override
	protected void setCoords(){
    	((GeoPoint3D) P).setCoords(point);
    }
    

    @Override
	protected void addIncidence() {
    	//TODO
		
	}
	


	

    
}
