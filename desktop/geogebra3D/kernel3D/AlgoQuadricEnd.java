/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.CoordMatrix;
import geogebra.kernel.Matrix.CoordMatrix4x4;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoQuadricND;
import geogebra.main.Application;


/**
 * Compute one end of a limited quadric
 *
 * @author  matthieu
 * @version 
 */
public abstract class AlgoQuadricEnd extends AlgoElement3D {

 
    private GeoQuadric3DLimited quadric; // input
    private GeoConic3D section; // output       
    private CoordSys coordsys;

    
    public AlgoQuadricEnd(Construction cons, String label, GeoQuadric3DLimited quadric) {
        super(cons);
        
        this.quadric = quadric;
        section = new GeoConic3D(cons);
        coordsys = new CoordSys(2);
		section.setCoordSys(coordsys);
		section.setIsEndOfQuadric(true);
		
		setInputOutput(new GeoElement[] {(GeoElement) quadric},  new GeoElement[] {section});

		compute();
		
		section.setLabel(label);

    }


    public GeoConic3D getSection() {
        return section;
    }
  


    
    protected final void compute() {
    	
    	
    	if (!quadric.isDefined()){
    		section.setUndefined();
    		return;
    	}

    	section.setDefined();
    	
    	CoordMatrix qm = quadric.getSymetricMatrix();
    	CoordMatrix pm = new CoordMatrix(4,3);
    	Coords o1 = quadric.getMidpoint3D().add(quadric.getEigenvec3D(2).mul(quadric.getMin()));//point.getInhomCoordsInD(3);
    	Coords o2 = quadric.getMidpoint3D().add(quadric.getEigenvec3D(2).mul(quadric.getMax()));//pointThrough.getInhomCoordsInD(3);
    	pm.setOrigin(o1);
    	Coords[] v = o2.sub(o1).completeOrthonormal();  	
    	pm.setVx(v[0]);
    	pm.setVy(v[1]);
    	CoordMatrix pmt = pm.transposeCopy();
     	
    	//sets the conic matrix from plane and quadric matrix
    	CoordMatrix cm = pmt.mul(qm).mul(pm);
    	
    	//Application.debug("pm=\n"+pm+"\nqm=\n"+qm+"\ncm=\n"+cm);
    	
    	coordsys.resetCoordSys();
      	coordsys.addPoint(getOrigin(o1, o2));
       	coordsys.addVector(v[0]);
       	coordsys.addVector(getV1(v[1])); 
       	coordsys.makeOrthoMatrix(false, false);
        	
    	section.setMatrix(cm);
    	
    	
    	//areas
    	section.calcArea();

    }
    
 
    abstract protected Coords getOrigin(Coords o1, Coords o2);
    
    //orientation out of the quadric
    abstract protected Coords getV1(Coords v1);
    
    

}
