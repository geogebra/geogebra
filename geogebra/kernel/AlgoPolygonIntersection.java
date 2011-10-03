/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

//
 
package geogebra.kernel;



public class AlgoPolygonIntersection extends AlgoPolygonOperation {
    
	public AlgoPolygonIntersection(Construction cons, String[] labels,
			GeoPolygon inPoly0, GeoPolygon inPoly1) {
		
		super(cons, labels, inPoly0, inPoly1, AlgoPolygonOperation.TYPE_INTERSECTION);
	}
	
	@Override
	public String getClassName() {
		return "AlgoPolygonIntersection";
	}
	
}