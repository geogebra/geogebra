/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoCircleTwoPoints.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoSphereNDTwoPoints;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoQuadricND;


/**
 *
 * @author  Matthieu
 * @version 
 */
public class AlgoSphereTwoPoints extends AlgoSphereNDTwoPoints {

     public AlgoSphereTwoPoints(
        Construction cons,
        GeoPointND M,
        GeoPointND P) {
        super(cons,M,P);
    }
    
    public AlgoSphereTwoPoints(
            Construction cons,
            String label,
            GeoPointND M,
            GeoPointND P) {
         super(cons, label,M, P);
    }
    
    @Override
	protected GeoQuadricND createSphereND(Construction cons){
    	GeoQuadric3D sphere = new GeoQuadric3D(cons);
        //circle.addPointOnConic((GeoPoint) getP()); //TODO do this in AlgoSphereNDTwoPoints
        return sphere;
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoSphereTwoPoints;
    }



    public GeoQuadric3D getSphere() {
        return (GeoQuadric3D) getSphereND();
    }

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("SphereThroughAwithCenterB",
        		getP().getLabel(tpl),
        		getM().getLabel(tpl));

    }

	// TODO Consider locusequability
}
