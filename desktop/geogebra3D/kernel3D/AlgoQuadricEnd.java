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
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;


/**
 * Compute one end of a limited quadric
 *
 * @author  matthieu
 */
public abstract class AlgoQuadricEnd extends AlgoElement3D {

 
    private GeoQuadric3DLimited quadric; // input
    private GeoConic3D section; // output       
    private CoordSys coordsys;

    /**
     * 
     * @param cons construction
     * @param quadric quadric
     */
    public AlgoQuadricEnd(Construction cons, GeoQuadric3DLimited quadric) {
        super(cons);
        
        this.quadric = quadric;
        section = new GeoConic3D(cons);
        coordsys = new CoordSys(2);
		section.setCoordSys(coordsys);
		section.setIsEndOfQuadric(true);
		
		setInputOutput(new GeoElement[] {quadric},  new GeoElement[] {section});

		compute();
		
    }
		
    /**
     * 
     * @param cons construction
     * @param label label
     * @param quadric quadric
     */
	public AlgoQuadricEnd(Construction cons, String label, GeoQuadric3DLimited quadric) {
		this(cons, quadric);
		section.setLabel(label);
    }


    /**
     * 
     * @return section
     */
    public GeoConic3D getSection() {
        return section;
    }
  


    
    @Override
	public final void compute() {
    	
    	
    	if (!quadric.isDefined()){
    		section.setUndefined();
    		return;
    	}

    	section.setDefined();
    	
    	CoordMatrix qm = quadric.getSymetricMatrix();
    	CoordMatrix pm = new CoordMatrix(4,3);
    	Coords d = quadric.getEigenvec3D(2);
    	Coords o1 = quadric.getMidpoint3D().add(d.mul(quadric.getBottomParameter()));   	
    	Coords o2 =quadric.getMidpoint3D().add(d.mul(quadric.getTopParameter()));
    	pm.setOrigin(o2);
    	Coords[] v = d.completeOrthonormal();  	
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
