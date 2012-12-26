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
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;



/**
 * Sort a list. Adapted from AlgoMax and AlgoIterationList
 * @author Michael Borcherds
 * @version 04-01-2008
 */

public class AlgoPointList extends AlgoElement {

	private GeoList inputList; //input
    private GeoList outputList; //output	
    private int size;

    public AlgoPointList(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    @Override
	public Commands getClassName() {
        return Commands.PointList;
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = inputList;

        super.setOutputLength(1);
        super.setOutput(0, outputList);
        setDependencies(); // done by AlgoElement
    }

    public GeoList getResult() {
        return outputList;
    }

    @Override
	public final void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size == 0) {
    		outputList.setUndefined();
    		return;
    	} 
     
        // copy the sorted treeset back into a list
        outputList.setDefined(true);
        outputList.clear();
        
        boolean suppressLabelCreation = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
        for (int i = 0 ; i < size ; i++ ){
        	GeoElement geo = inputList.get(i);
        	if (geo.isGeoList()) {
        		GeoList list = (GeoList)geo;
        		if (list.size() == 2) {
        			GeoElement geoX = list.get(0);
        			GeoElement geoY = list.get(1);
        			if (geoX.isGeoNumeric() && geoY.isGeoNumeric()) {
        				outputList.add(new GeoPoint(cons, null, ((GeoNumeric)geoX).getDouble(), ((GeoNumeric)geoY).getDouble(), 1.0));
        			}
        		}
        		
        	}
        	
        }
		cons.setSuppressLabelCreation(suppressLabelCreation);       
    }

	// TODO Consider locusequability
  
}
