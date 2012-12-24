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
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

import java.util.ArrayList;


/**
 * Sort a list. Adapted from AlgoMax and AlgoIterationList
 * @author Michael Borcherds
 * @version 04-01-2008
 */

public class AlgoShuffle extends AlgoElement {

	
	private GeoList inputList; //input
    private GeoList outputList; //output	
    private int size;

    public AlgoShuffle(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        outputList = new GeoList(cons);
        
        cons.addRandomGeo(outputList);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    @Override
	public Commands getClassName() {
        return Commands.Shuffle;
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = inputList;

        setOnlyOutput(outputList);
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
    	
    	ArrayList<GeoElement> list = new ArrayList();

        // copy inputList into arraylist
        for (int i=0 ; i<size ; i++)
        {
        	list.add(inputList.get(i));
        }
        
        // copy the geos back into a GeoList in a random order
        outputList.setDefined(true);
        outputList.clear();
        for (int i=0 ; i<size ; i++)
        {
        	int pos = (int) Math.floor(cons.getApplication().getRandomNumber() * (size - i));
        	outputList.add(list.get(pos));
        	list.remove(pos);
        }


    }

	// TODO Consider locusequability
  
}
