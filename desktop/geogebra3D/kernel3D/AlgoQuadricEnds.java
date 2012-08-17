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
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;


/**
 * Compute the ends of a limited quadric
 *
 * @author  matthieu
 * @version 
 */
public class AlgoQuadricEnds extends AlgoElement3D {

 
    private GeoQuadric3DLimited quadric; // input
    private GeoConic3D section1, section2; // output       
    private CoordSys coordsys1, coordsys2;

    /**
     * 
     * @param cons construction
     * @param quadric quadric
     */
    public AlgoQuadricEnds(Construction cons, GeoQuadric3DLimited quadric) {
        super(cons);
        
        this.quadric = quadric;
        section1 = new GeoConic3D(cons);
        coordsys1 = new CoordSys(2);
		section1.setCoordSys(coordsys1);
		section1.setIsEndOfQuadric(true);
        section2 = new GeoConic3D(cons);
        coordsys2 = new CoordSys(2);
		section2.setCoordSys(coordsys2);
		section2.setIsEndOfQuadric(true);
		
		setInputOutput(new GeoElement[] {quadric},  new GeoElement[] {section1, section2});

		compute();

    }


    public GeoConic3D getSection1() {
        return section1;
    }
  
    public GeoConic3D getSection2() {
        return section2;
    }


    
    @Override
	public final void compute() {
    	
    	
    	if (!quadric.isDefined()){
    		section1.setUndefined();
    		section2.setUndefined();
    		return;
    	}

    	section1.setDefined();
    	section2.setDefined();
    	
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
    	
    	coordsys1.resetCoordSys();
      	coordsys1.addPoint(o1);
       	coordsys1.addVector(v[0]);
       	coordsys1.addVector(v[1].mul(-1)); //orientation out of the quadric
       	coordsys1.makeOrthoMatrix(false, false);
        	
    	section1.setMatrix(cm);
    	
    	//section2
    	pm.setOrigin(o2);
    	pmt = pm.transposeCopy();
    	
    	cm = pmt.mul(qm).mul(pm);
    	
    	coordsys2.resetCoordSys();
      	coordsys2.addPoint(o2);
       	coordsys2.addVector(v[0]);
       	coordsys2.addVector(v[1]);
       	coordsys2.makeOrthoMatrix(false, false);
        	
    	section2.setMatrix(cm);
    	
    	
    	//areas
    	section1.calcArea();
    	section2.calcArea();

    }
    
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoQuadricEnds;
    }
    


	@Override
	public void remove() {
		if(removed)
			return;
		super.remove();
		quadric.remove();
	}       
    
    /*
    final public String toString() {
    	return app.getPlain("EndsOfABetweenBC",((GeoElement) quadric).getLabel(),point.getLabel(),pointThrough.getLabel());
    }
    */

	// TODO Consider locusequability
}
