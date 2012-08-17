/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 * Sum[{A,B,C}]
 * @author Michael Borcherds
 * @version 2008-10-16
 */

public class AlgoSumPoints extends AlgoElement {

	private GeoList geoList; //input
    public GeoNumeric Truncate; //input	
    public GeoElement result; //output	
    
    public AlgoSumPoints(Construction cons, String label, GeoList geoList) {
        this(cons, label, geoList, null);
    }

    public AlgoSumPoints(Construction cons, String label, GeoList geoList, GeoNumeric Truncate) {
        super(cons);
        this.geoList = geoList;

        this.Truncate=Truncate;
        
        GeoElement geo0 = geoList.get(0);
        
        // make sure output is same type as input (GeoVector, GeoPoint, GeoPoint3D)
        result = geo0.copyInternal(cons);

        setInputOutput();
        compute();
        result.setLabel(label);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoSumPoints;
    }

    @Override
	protected void setInputOutput(){
    	if (Truncate == null) {
	        input = new GeoElement[1];
	        input[0] = geoList;
    	}
    	else {
    		 input = new GeoElement[2];
             input[0] = geoList;
             input[1] = Truncate;
    	}

        super.setOutputLength(1);
        super.setOutput(0, result );
        setDependencies(); // done by AlgoElement
    }

    public GeoElement getResult() {
        return result;
    }
    
    @Override
	public final void compute() {
    	
    	// TODO: remove
    	//Application.debug("compute: " + geoList);
    	
    	int truncate;
    	int size = geoList.size();

    	if (Truncate!=null)
    	{
    		truncate=(int)Truncate.getDouble();
    		if (truncate<1 || truncate>size)
    		{
        		result.setUndefined();
        		return;
    		}
    		size=truncate; // truncate the list
    	}
    	
    	if (!geoList.isDefined() ||  size == 0) {
    		result.setUndefined();
    		return;
    	}
    	
    	
    	double x = 0, y = 0, z = 0;
    	
    	for (int i = 0 ; i < size ; i++) {
    		GeoElement p = geoList.get(i);
    		if (p instanceof GeoPoint) {
	        	x += ((GeoPoint)p).getInhomX();
	        	y += ((GeoPoint)p).getInhomY();    			
    		}
    		else if (p instanceof GeoPointND) { // 3D
	        	double[] coords = new double[3];
				((GeoPointND)p).getInhomCoords(coords);
	        	x += coords[0];
	        	y += coords[1];
        		z += coords[2];
    		} else if (p.isGeoVector()) {
	        	x += ((GeoVector)p).getX();
	        	y += ((GeoVector)p).getY();   		
    		} else if (p.isNumberValue()) {
	        	x += ((NumberValue)p).getDouble();
    		} else {
				result.setUndefined();
				return;
    		}
    	}
   	
   	
    	if (result.isGeoVector() || result instanceof GeoPoint)
    		((GeoVec3D)result).setCoords(x, y, 1.0);
    	else
    	{ // 3D
    		
    		((GeoPointND)result).setCoords(x, y, z, 1.0);
    	}
   	

    }

	// TODO Consider locusequability
    
}
