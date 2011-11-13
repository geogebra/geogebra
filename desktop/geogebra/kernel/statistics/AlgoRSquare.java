/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.statistics;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoFunctionable;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;


/**
 * Command: RSquare[<list>,<function>]
 * Calculates coefficient of determination, R^2, for a function f(x) fitted to the list.
 * @author 	G.Sturr

 */

public class AlgoRSquare extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
	private GeoFunctionable function; //input
    private GeoNumeric r2; //output	
    private int size;

    public AlgoRSquare(Construction cons, String label, GeoList inputList,GeoFunctionable function) {
    	this(cons, inputList,function);
        r2.setLabel(label);
    }

    public AlgoRSquare(Construction cons, GeoList inputList,GeoFunctionable function) {
        super(cons);
        this.inputList = inputList;
        this.function=function;
               
        r2 = new GeoNumeric(cons);

        setInputOutput();
        compute();
    }

    public String getClassName() {
        return "AlgoRSquare";
    }

    protected void setInputOutput(){
        input = new GeoElement[2];
        input[0] = inputList;
        input[1] = function.toGeoElement();
        
        output = new GeoElement[1];
        output[0] = r2;
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getRSquare() {
        return r2;
    }

    protected final void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  !function.toGeoElement().isDefined()) {
    		r2.setUndefined();
    		return;
    	} 
    	
    	GeoFunction funGeo = function.getGeoFunction();
    	
        //Calculate errorsum and ssy:
    	double sumyy = 0.0d;
    	double sumy = 0.0d;
    	double syy = 0.0d;
    	double	errorsum	=	0.0d;
    	GeoElement geo		=	null;
    	GeoPoint  point	=	null;
    	double	x,y,v;
    	
    	for(int i=0;i<size;i++){
    		geo=inputList.get(i);
    		if(geo.isGeoPoint()){
    			point=(GeoPoint)geo;
    			x=point.getX();
    			y=point.getY();
    			v=funGeo.evaluate(x);
    			errorsum+=(v-y)*(v-y);
    			sumy+=y;
    			sumyy+=y*y;
    		} else{
    			r2.setUndefined();
        		return;   			
    		}//if calculation is possible
    	}//for all points
       
    	syy = sumyy-sumy*sumy/size;
    	
    	//calculate RSquare
        r2.setValue(1-errorsum/syy);
      
    }//compute()
}//class AlgoRSquare

