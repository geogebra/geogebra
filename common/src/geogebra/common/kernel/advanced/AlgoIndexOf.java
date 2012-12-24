/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;

/**
 * Algorithm for searching in text or list
 * @author Zbynek Konecny
 */
public class AlgoIndexOf extends AlgoElement {

	private GeoElement hayStack; //input
	private GeoElement needle; //input
    private GeoNumeric index; //output	
	private NumberValue start;

    /**
     * Creates new index of algorithm
     * @param cons
     * @param label
     * @param needle what we want to find
     * @param hayStack GeoList of GeoText in which we want to search
     */
    public AlgoIndexOf(Construction cons, String label, GeoElement needle, GeoElement hayStack) {
        super(cons);
         
        this.hayStack = hayStack;
        this.needle = needle;
              
        index = new GeoNumeric(cons);

        setInputOutput();
        compute();
        index.setLabel(label);
    }

    /**
     * Creates new index of algorithm
     * @param cons
     * @param label
     * @param needle what we want to find
     * @param hayStack GeoList of GeoText in which we want to search
     * @param start start index (1 means "search from beginning") 
     */
    public AlgoIndexOf(Construction cons, String label,GeoElement needle,GeoElement hayStack,NumberValue start) {
        super(cons);
        
        this.hayStack = hayStack;
        this.needle = needle;
        this.start = start;      
        index = new GeoNumeric(cons);

        setInputOutput();
        compute();
        index.setLabel(label);
    }

    @Override
	public Commands getClassName() {
        return Commands.IndexOf;
    }

    @Override
	protected void setInputOutput(){                
        if (start == null) {
        	input = new GeoElement[2];
        	input[0] = needle;
	        input[1] = hayStack;
	        
        } else { 
        	input = new GeoElement[3];
            input[0] = needle;        	
            input[1] = hayStack;
            input[2] = start.toGeoElement();
        }

        setOutputLength(1);
        setOutput(0,index);
        setDependencies(); // done by AlgoElement
    }
    
    /**
     * Returns the resulting index
     * @return the resulting index
     */
    public GeoNumeric getResult() {
        return index;
    }

    @Override
	public final void compute() {
    	    	
    	index.setUndefined();
    	
    	int startAt = start != null? (int)start.getDouble()-1:0;
    	if(hayStack.isGeoText()){
    		int pos = ((GeoText)hayStack).getTextString().indexOf(((GeoText)needle).getTextString(),startAt);
    		if(pos > -1)index.setValue(pos+1);
    		return;
    	}
    	int	size = ((GeoList)hayStack).size();	
    	for(int i=startAt;i<size;i++){
    		GeoElement cmp = ((GeoList)hayStack).get(i);
    		if(needle.isEqual(cmp)){
    			index.setValue(i+1);
    			break;
    		}
    	}
   }

	// TODO Consider locusequability
  
}
