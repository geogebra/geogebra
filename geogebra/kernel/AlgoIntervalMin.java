/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


public class AlgoIntervalMin extends AlgoIntervalAbstract {


	public AlgoIntervalMin(Construction cons, String label, GeoInterval s) {
		super(cons, label, s);
	}


	public String getClassName() {
        return "AlgoIntervalMin";
    }


    protected final void compute() {
    	
    	result.setValue(interval.getMin());
    }
    
}
