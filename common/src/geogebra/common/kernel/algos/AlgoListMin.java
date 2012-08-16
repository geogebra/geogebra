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
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;


/**
 * Minimum value of a list.
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoListMin extends AlgoElement {

	private GeoList geoList; //input
    private GeoNumeric min; //output	

    public AlgoListMin(Construction cons, String label, GeoList geoList) {
        this(cons, geoList);
        
        min.setLabel(label);
    }

    public AlgoListMin(Construction cons, GeoList geoList) {
        super(cons);
        this.geoList = geoList;
               
        min = new GeoNumeric(cons);

        setInputOutput();
        compute();
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoListMin;
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = geoList;

        super.setOutputLength(1);
        super.setOutput(0, min);
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getMin() {
        return min;
    }

    @Override
	public final void compute() {
    	int size = geoList.size();
    	if (!geoList.isDefined() ||  size == 0) {
    		min.setUndefined();
    		return;
    	}
    	
    	double minVal = Double.POSITIVE_INFINITY;
    	for (int i=0; i < size; i++) {
    		GeoElement geo = geoList.get(i);
    		if (geo.isNumberValue()) {
    			NumberValue num = (NumberValue) geo;
    			minVal = Math.min(minVal, num.getDouble());
    		} else {
    			min.setUndefined();
        		return;
    		}    		    		
    	}   
    	
    	min.setValue(minVal);
    }

	// TODO Consider locusequability
    
}
