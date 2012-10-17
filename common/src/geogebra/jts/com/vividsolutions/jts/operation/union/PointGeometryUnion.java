package com.vividsolutions.jts.operation.union;

import com.vividsolutions.jts.algorithm.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.util.*;
import java.util.*;

/**
 * Computes the union of a {@link Puntal} geometry with 
 * another arbitrary {@link Geometry}.
 * Does not copy any component geometries.
 * 
 * @author mbdavis
 *
 */
public class PointGeometryUnion 
{
	public static Geometry union(Puntal pointGeom, Geometry otherGeom)
	{
		PointGeometryUnion unioner = new PointGeometryUnion(pointGeom, otherGeom);
		return unioner.union();
	}
	
	private Geometry pointGeom;
	private Geometry otherGeom;
	private GeometryFactory geomFact;
	
	public PointGeometryUnion(Puntal pointGeom, Geometry otherGeom)
	{
		this.pointGeom = (Geometry) pointGeom;
		this.otherGeom = otherGeom;
		geomFact = otherGeom.getFactory();
	}
	
	public Geometry union()
	{
		PointLocator locater = new PointLocator();
		// use a set to eliminate duplicates, as required for union
		Set exteriorCoords = new TreeSet();
		
		for (int i =0 ; i < pointGeom.getNumGeometries(); i++) {
			Point point = (Point) pointGeom.getGeometryN(i);
			Coordinate coord = point.getCoordinate();
			int loc = locater.locate(coord, otherGeom);
			if (loc == Location.EXTERIOR)
				exteriorCoords.add(coord);
		}
		
		// if no points are in exterior, return the other geom
		if (exteriorCoords.size() == 0)
			return otherGeom;
		
		// make a puntal geometry of appropriate size
		Geometry ptComp = null;
		Coordinate[] coords = CoordinateArrays.toCoordinateArray(exteriorCoords);
		if (coords.length == 1) {
			ptComp = geomFact.createPoint(coords[0]);
		}
		else {
			ptComp = geomFact.createMultiPoint(coords);
		}
		
		// add point component to the other geometry
		return GeometryCombiner.combine(ptComp, otherGeom);
	}
}
