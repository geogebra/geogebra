/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.RegionParameters;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;


/**
 * Point in region algorithm
 * @author mathieu
 *
 */
public class AlgoPointInRegion extends AlgoElement {

	private Region region; // input
    private GeoPoint P; // output   
    
    private NumberValue param1, param2;
    
    public AlgoPointInRegion(
            Construction cons,
            String label,
            Region region,
            double x,
            double y) {
    	
    	this(cons, label, region, x, y, null, null);
    }

    public AlgoPointInRegion(
        Construction cons,
        String label,
        Region region,
        double x,
        double y,
        NumberValue param1,
        NumberValue param2) {
        super(cons);
        this.region = region;
        
        P = new GeoPoint(cons, region);
        P.setCoords(x, y, 1.0);

        this.param1 = param1;
        this.param2 = param2;
        

        setInputOutput(); // for AlgoElement

        
        compute();
        P.setLabel(label);
    }

    @Override
	public Commands getClassName() {
        return Commands.PointIn;
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_POINT_ON_OBJECT;
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
    	if(param1 == null){
    		input = new GeoElement[1];
    		input[0] = region.toGeoElement();
    	}else{
    		input = new GeoElement[3];
            input[0] = region.toGeoElement();
            input[1] = param1.toGeoElement();
            input[2] = param2.toGeoElement();
    	}

        setOutputLength(1);
        setOutput(0,P);
        setDependencies(); // done by AlgoElement
    }

    /** returns the point 
     * @return resulting point 
     */
    public GeoPoint getP() {
        return P;        
    }
    /**
     * Returns the region
     * @return region
     */
    Region getRegion() {
        return region;
    }

    @Override
	public final void compute() {
    	
    	if(param1 != null){
    		RegionParameters rp = P.getRegionParameters();
    		rp.setIsOnPath(false);
    		rp.setT1(param1.getDouble());
    		rp.setT2(param2.getDouble());
    		//pp.setT(PathNormalizer.toParentPathParameter(param.getDouble(), path.getMinParameter(), path.getMaxParameter()));
    	}
    	
    	//App.debug(P.getRegionParameters().getT1()+","+P.getRegionParameters().getT2());
    	
    	
    	if (input[0].isDefined()) {	    	
	        region.regionChanged(P);
	        P.updateCoords();
    	} else {
    		P.setUndefined();
    	}
    }

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return loc.getPlain("PointInA",input[0].getLabel(tpl));

    }

	// TODO Consider locusequability
}
