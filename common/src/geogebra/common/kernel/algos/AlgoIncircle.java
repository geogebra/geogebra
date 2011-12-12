/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoIncircle.java, dsun48 [6/26/2011]
 *
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoIncircle extends AlgoElement {

    private GeoPointND A, B, C; // input    
    protected GeoConicND circle; // output     

    // angle bisector calculations
    private GeoLine bisectorC, bisectorB, sideBC, heightBC;
    private GeoPoint2 heightFoot, incenter;    
    private GeoPoint2 A1, B1, C1;

    public AlgoIncircle(
                           Construction cons,
                           String label,
                           GeoPointND A,
                           GeoPointND B,
                           GeoPointND C) {
        this(cons, A, B, C);
        circle.setLabel(label);
    }
    
    public AlgoIncircle(
                        Construction cons,           
                        GeoPointND A,
                        GeoPointND B,
                        GeoPointND C) {
        
        super(cons);
            
        this.A = A;
        this.B = B;
        this.C = C; 
 
        circle = new GeoConic(cons); // output

        bisectorC = new GeoLine(cons);
        bisectorB = new GeoLine(cons);
        heightFoot = new GeoPoint2(cons);            
        heightBC = new GeoLine(cons);
        sideBC = new GeoLine(cons);
        incenter = new GeoPoint2(cons);            
        A1 = new GeoPoint2(cons);            
        B1 = new GeoPoint2(cons);            
        C1 = new GeoPoint2(cons);            

        setInputOutput();

        compute();            
    }
    
    @Override
	public String getClassName() {
        return "AlgoIncircle";
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = (GeoElement) A;
        input[1] = (GeoElement) B;
        input[2] = (GeoElement) C;

        super.setOutputLength(1);
        super.setOutput(0, circle);
        setDependencies(); // done by AlgoElement
    }
    
    public GeoConicND getCircle() {
        return (GeoConicND) circle;
    }
    public GeoPoint2 getA() {
        return (GeoPoint2) A;
    }
    public GeoPoint2 getB() {
        return (GeoPoint2) B;
    }
    public GeoPoint2 getC() {
        return (GeoPoint2) C;
    }

    // compute incircle of triangle A, B, C
    @Override
	public void compute() {
        // bisector of angle ABC
        double dAB = getA().distance(getB());
        double dAC = getA().distance(getC());
        double dBC = getB().distance(getC());
        double dmax = dAB > dBC ? dAB : dBC;
        A1.setCoords(dmax/dAB * (getA().inhomX-getB().inhomX)+getB().inhomX, 
                     dmax/dAB * (getA().inhomY-getB().inhomY)+getB().inhomY, 
                     1.0d);  
        C1.setCoords(dmax/dBC * (getC().inhomX-getB().inhomX)+getB().inhomX,
                     dmax/dBC * (getC().inhomY-getB().inhomY)+getB().inhomY,
                     1.0d);
        B1.setCoords((A1.inhomX+C1.inhomX)/2.0d, (A1.inhomY+C1.inhomY)/2.0d, 1.0d);
        GeoVec3D.lineThroughPoints(getB(), B1, bisectorB);
        // bisector of angle BCA
        dmax = dAC > dBC ? dAC : dBC;
        A1.setCoords(dmax/dAC * (getA().inhomX-getC().inhomX)+getC().inhomX, 
                     dmax/dAC * (getA().inhomY-getC().inhomY)+getC().inhomY, 
                     1.0d);  
        B1.setCoords(dmax/dBC * (getB().inhomX-getC().inhomX)+getC().inhomX,
                     dmax/dBC * (getB().inhomY-getC().inhomY)+getC().inhomY,
                     1.0d);  
        C1.setCoords((A1.inhomX+B1.inhomX)/2.0d, (A1.inhomY+B1.inhomY)/2.0d, 1.0d);
        GeoVec3D.lineThroughPoints(getC(), C1, bisectorC);
        // intersect angle bisectors to get incenter
        GeoVec3D.lineThroughPoints(getB(), getC(), sideBC);
        GeoVec3D.cross(bisectorB, bisectorC, incenter);
        GeoVec3D.cross(incenter, sideBC.x, sideBC.y, 0.0, heightBC);
        GeoVec3D.cross(sideBC, heightBC, heightFoot);
        double dist = incenter.distance(heightFoot);
        circle.setCircle(incenter, dist);
    }

    @Override
	public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("IncircleOfTriangleABC", A.getLabel(), B.getLabel(), C.getLabel());
    }
}

// Local Variables:
// indent-tabs-mode: nil
// c-basic-offset: 4
// tab-width: 4
// End:
// vim: set expandtab shiftwidth=4 softtabstop=4 tabstop=4

