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
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;


/**
 * Sum[{A,B,C}]
 * @author Michael Borcherds
 * @version adapted from SumPoints
 */

public class AlgoSumText extends AlgoElement {

	private GeoList geoList; //input
    public GeoNumeric Truncate; //input	
    public GeoText result; //output	
    
	private StringBuilder sb;
    
    public AlgoSumText(Construction cons, String label, GeoList geoList) {
        this(cons, label, geoList, null);
    }

    public AlgoSumText(Construction cons, String label, GeoList geoList, GeoNumeric Truncate) {
        super(cons);
        this.geoList = geoList;

        this.Truncate=Truncate;
        
        result  = new GeoText(cons);

        setInputOutput();
        compute();
        result.setLabel(label);
        result.setIsTextCommand(true);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoSumText;
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
        super.setOutput(0, result);
        setDependencies(); // done by AlgoElement
    }

    public GeoText getResult() {
        return result;
    }  

    @Override
	public final void compute() {
    	
    	// TODO: remove
    	//Application.debug("compute: " + geoList);
    	
    	int truncate;
    	int size = geoList.size();

    	if (Truncate != null)
    	{
    		truncate=(int)Truncate.getDouble();
    		if (truncate < 1 || truncate > size)
    		{
        		result.setUndefined();
        		return;
    		}
    		size = truncate; // truncate the list
    	}
    	
    	if (!geoList.isDefined() ||  size == 0) {
    		result.setUndefined();
    		return;
    	}  	
    	
    	if (sb == null) {
    		sb = new StringBuilder();
    	} else {
    		sb.setLength(0);
    	}
    	
    	for (int i = 0 ; i < size ; i++) {
    		GeoElement p = geoList.get(i);
    		if (p.isGeoText()) {
    			sb.append(((GeoText)p).getTextString());
    		} else {
				result.setUndefined();
				return;
    		}
    	} 	
   	
    	result.setTextString(sb.toString());
    }

	// TODO Consider locusequability
    
}
