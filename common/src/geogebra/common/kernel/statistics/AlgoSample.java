/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

import java.util.ArrayList;


/**
 * Sample from a list. Adapted from AlgoMode
 * @author Michael Borcherds
 * @version 
 */

public class AlgoSample extends AlgoElement {
	
	// maximum size for a sample
	private static int SAMPLE_MAXSIZE = 10000;

	
	private GeoList inputList; //input
	private GeoBoolean replacement;
	private NumberValue num;
    private GeoList outputList; //output	

    public AlgoSample(Construction cons, String label, GeoList inputList, NumberValue num, GeoBoolean replacement) {
        super(cons);
        this.inputList = inputList;
        this.replacement = replacement;
        this.num = num;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    @Override
	public Commands getClassName() {
        return Commands.Sample;
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[replacement == null ? 2 : 3];
        input[0] = inputList;
        input[1] = num.toGeoElement();
        if (replacement != null) input[2] = replacement;

        setOutputLength(1);
        setOutput(0, outputList);
        setDependencies(); // done by AlgoElement
    }

    public GeoList getResult() {
        return outputList;
    }

    @Override
	public final void compute() {
    	
    	int size = (int) num.getDouble();
    	if (!inputList.isDefined() || num.getDouble() < 1 || num.getDouble() > SAMPLE_MAXSIZE) {
    		outputList.setUndefined();
    		return;
    	} 
    	
    	boolean withReplacement = true;
    	
    	if (replacement != null) {
    		withReplacement = replacement.getBoolean();
    	}
           
    	
 
       int inputListSize = inputList.size();
       
       outputList.clear();

       if (withReplacement) {
	       for (int i=0 ; i<size ; i++)
	       {
	    	   NumberValue geo;
	    	   
	    	   try { geo = (NumberValue)inputList.get((int)Math.floor(cons.getApplication().getRandomNumber()*inputListSize));
	    	   } catch (ClassCastException e) {
		   			outputList.setUndefined();
		    		return;			   		   
	    	   }
	    	   setListElement(i, geo.getDouble());
	       }
       } else {
    	   
    	   // sampling without replacement
    	   
    	   if (size > inputListSize) {
	       		outputList.setUndefined();
	    		return;    		   
    	   }
    	   
	       	ArrayList<GeoElement> list = new ArrayList<GeoElement>();
	
	        // copy inputList into arraylist
	        for (int i=0 ; i<inputListSize ; i++)
	        {
	        	list.add(inputList.get(i));
	        }
	        
	        // copy the geos back into a GeoList in a random order
	        for (int i=0 ; i<size ; i++)
	        {
	        	int pos = (int) Math.floor(cons.getApplication().getRandomNumber() * (inputListSize - i));
	        	outputList.add(list.get(pos));
	        	list.remove(pos);
	        }

       }
       
       outputList.setDefined(true);

       
    }
    
    // copied from AlgoIterationList.java
    // TODO should it be centralised?
    private void setListElement(int index, double value) {
    	GeoNumeric listElement;
    	if (index < outputList.getCacheSize()) {
    		// use existing list element
    		listElement = (GeoNumeric) outputList.getCached(index);    	
    	} else {
    		// create a new list element
    		listElement = new GeoNumeric(cons);
    		listElement.setParentAlgorithm(this);
    		listElement.setConstructionDefaults();
    		listElement.setUseVisualDefaults(false);	    		
    	}
    	
    	outputList.add(listElement);
    	listElement.setValue(value);
    }    

	// TODO Consider locusequability

}
