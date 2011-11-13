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
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoPointND;


/**
 * Compute a circle with point and radius (missing direction)
 *
 * @author  matthieu
 * @version 
 */
public abstract class AlgoCircle3DPointDirection extends AlgoElement3D {

 
	private GeoPointND point; // input
	private GeoElement secondInput; // input
	private GeoDirectionND forAxis; // input
	private GeoConic3D circle; // output       
    private CoordSys coordsys;

    /**
     * 
     * @param cons
     * @param label
     * @param point
     * @param secondInput 
     * @param forAxis
     */
    public AlgoCircle3DPointDirection(Construction cons, String label, GeoPointND point, GeoElement secondInput, GeoDirectionND forAxis) {
        super(cons);
        
        this.point = point;
        this.forAxis = forAxis;
        this.secondInput=secondInput;
        circle = new GeoConic3D(cons);
        coordsys = new CoordSys(2);
		circle.setCoordSys(coordsys);
		
        setInputOutput(new GeoElement[] {(GeoElement) point, secondInput, (GeoElement) forAxis}, new GeoElement[] {circle});

        // compute line 
        compute();
        circle.setLabel(label);
    }
    


    /**
     * 
     * @return the circle
     */
    public GeoConic3D getCircle() {
        return circle;
    }
    

    
    protected final void compute() {
    	
    	
		//recompute the coord sys
    	coordsys.resetCoordSys();
		
    	coordsys.addPoint(point.getCoordsInD(3));
    	Coords[] v = forAxis.getDirectionInD3().completeOrthonormal();
		coordsys.addVector(v[0]);
		coordsys.addVector(v[1]);
		
		coordsys.makeOrthoMatrix(false,false);
		
    	
		//set the circle
    	circle.setSphereND(new Coords(0,0), getRadius());

    }
    
    /**
     * 
     * @return the radius
     */
    protected abstract double getRadius();
    
    /**
     * 
     * @return center
     */
    protected GeoPointND getCenter(){
    	return point;
    }
    
    /**
     * 
     * @return direction
     */
    protected GeoDirectionND getDirection(){
    	return forAxis;
    }
    
    /**
     * 
     * @return second input (radius or point)
     */
    protected GeoElement getSecondInput(){
    	return secondInput;
    }
    
    /**
     * 
     * @return direction of the axis
     */
    protected GeoDirectionND getForAxis(){
    	return forAxis;
    }
    

    public String getClassName() {
        return "AlgoCirclePointRadiusDirection";
    }


    final public String toString() {
    	return app.getPlain(getCommandString(),((GeoElement) point).getLabel(),secondInput.getLabel(),((GeoElement) forAxis).getLabel());
    }
    
    /**
     * 
     * @return command string
     */
    abstract protected String getCommandString();

    
}
