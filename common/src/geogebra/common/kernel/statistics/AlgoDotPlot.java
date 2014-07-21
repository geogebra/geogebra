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
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;


/**
 * Create a dot plot. 
 * 
 * Input: list of unsorted raw numeric data 
 * Output: sorted list of points forming a dot plot of the raw data
 * 
 * A dot plot is a set of points for which:
 *  x coordinates = values from a list of numeric data
 *  y coordinates = number of times the x data value has occurred 
 *  
 *  example:
 *      raw data = { 5,11,12,12,12,5 }
 *      dot plot = { (5,1), (5,2), (11,1), (12,1), (12,2), (12,3) }
 *  
 * Adapted from AlgoSort and AlgoPointList
 * @author G.Sturr
 * @version 2010-8-10
 */

public class AlgoDotPlot extends AlgoUsingUniqueAndFrequency {

	protected GeoList inputList; //input
    protected GeoList outputList; //output	
    private int size;
    private boolean useDensityPlot = false;
   

	public AlgoDotPlot(Construction cons, String label, GeoList inputList) {
		this(cons, label, inputList, null);
	}
	
	public AlgoDotPlot(Construction cons, GeoList inputList) {
		this(cons, inputList, null);
	}
	
    protected AlgoDotPlot(Construction cons, String label, GeoList inputList, GeoNumeric scale) {
        
    	this(cons, inputList, scale);
    	outputList.setLabel(label);
        
    }

    protected AlgoDotPlot(Construction cons, GeoList inputList, GeoNumeric scale) {
        super(cons);
        this.inputList = inputList;
        setScale(scale);
               
        outputList = new GeoList(cons){
        	public String getTooltipText(boolean colored, boolean alwaysOn){        		
        		return ((AlgoDotPlot)getParentAlgorithm()).getTooltipText();
        	}
        };
        setInputOutput();
        compute();
        
    }
    
    /**
     * 
     * @param scale vertical scale (if specified)
     */
    protected void setScale(GeoNumeric scale){
    	//not used here
    }

    
    @Override
	public Commands getClassName() {
		return Commands.DotPlot;
	}
    
    /**
     * set the input
     */
    protected void setInput(){
    	 input = new GeoElement[1];
         input[0] = inputList;
    }

    @Override
	protected void setInputOutput(){
    	
        createHelperAlgos(inputList);

        setInput();
        
        setOutputLength(1);
        setOutput(0,outputList);
        setDependencies(); // done by AlgoElement
    }

    public GeoList getResult() {
        return outputList;
    }
    
    private int oldListSize;
	private String toolTipText;

    @Override
	public void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size == 0) {
    		outputList.setUndefined();
    		return;
    	} 

    	//========================================
    	// sort the raw data
    	GeoList list1 = algoUnique.getResult();		
    	GeoList list2 = algoFreq.getResult();
	       
        
		// prepare output list. Pre-existing geos will be recycled, 
		// but extra geos are removed when outputList is too long
		outputList.setDefined(true);
		for (int i = outputList.size() - 1; i >= size; i--) {
			GeoElement extraGeo = outputList.get(i);
			extraGeo.remove();
			outputList.remove(extraGeo);
		}	
		
		oldListSize = outputList.size();
    	 
                     
        //========================================
        // create dot plot points
        boolean suppressLabelCreation = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		
		int index = 0;
		for (int i = 0; i < list1.size(); i++) {
			double x;
			if (list1.get(i).isGeoNumeric()) {
				x = list1.get(i).evaluateDouble();
			} else {
				// use integers 1,2,3 ...  to position non-numeric data 
				x = i+1;
			}
			
			int height = (int) list2.get(i).evaluateDouble();
			
			for (int y = 1; y <= height; y++){
				double scaledY = getScaledY(y);
				if(index < oldListSize){
					((GeoPoint)outputList.get(index)).setCoords(x, scaledY, 1.0);
				}else{
					outputList.add(new GeoPoint(cons, null, x, scaledY, 1.0));
				}
				index++;
			}

		}

		
        cons.setSuppressLabelCreation(suppressLabelCreation); 
    }
   
    /**
     * 
     * @param y current height
     * @return scaled y
     */
    protected double getScaledY(int y){
    	return y;
    }

	public GeoList getUniqueXList() {
		return algoUnique.getResult();
	}

	public GeoList getFrequencyList() {
		return algoFreq.getResult();
	}

	public String getTooltipText() {
		return toolTipText;
	}

	public void setToolTipPointText(String text) {
		toolTipText = text;
	}

	public boolean useDensityPlot() {
		return useDensityPlot;
	}

	public void setUseDensityPlot(boolean useDensityPlot) {
		this.useDensityPlot = useDensityPlot;
	}
	
	// TODO Consider locusequability
  
}
