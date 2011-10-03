/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

/**
 * Take first n objects from a list
 * @author Michael Borcherds
 * @version 2008-03-04
 */

public class AlgoFirst extends AlgoElement {

	private static final long serialVersionUID = 1L;
	protected GeoElement inputList; //input
	protected GeoNumeric n; //input
    protected GeoList outputList; //output	
    protected int size;

    AlgoFirst(Construction cons, String label, GeoElement inputList, GeoNumeric n) {
        super(cons);
        this.inputList = inputList;
        this.n=n;

               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    public String getClassName() {
        return "AlgoFirst";
    }

    protected void setInputOutput(){
    	
    	if (n != null) {
	        input = new GeoElement[2];
	        input[0] = inputList;
	        input[1] = n;
    	}
    	else
    	{
            input = new GeoElement[1];
            input[0] = inputList;
        }    		

        output = new GeoElement[1];
        output[0] = outputList;
        setDependencies(); // done by AlgoElement
    }

    GeoList getResult() {
        return outputList;
    }

    protected void compute() {
    	
    	size = ((GeoList)inputList).size();
    	int outsize = n == null ? 1 : (int)n.getDouble();
    	
    	if (!inputList.isDefined() ||  size == 0 || outsize < 0 || outsize > size) {
    		outputList.setUndefined();
    		return;
    	} 
       
    	outputList.setDefined(true);
    	outputList.clear();
    	
    	if (outsize == 0) return; // return empty list
    	
    	for (int i=0 ; i<outsize ; i++)
    		outputList.add(((GeoList)inputList).get(i).copyInternal(cons));
   }
  
}
