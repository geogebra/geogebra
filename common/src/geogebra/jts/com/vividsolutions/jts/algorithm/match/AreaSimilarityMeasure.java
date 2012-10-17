package com.vividsolutions.jts.algorithm.match;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.algorithm.distance.*;

/**
 * Measures the degree of similarity between two {@link Geometry}s
 * using the area of intersection between the geometries.
 * The measure is normalized to lie in the range [0, 1].
 * Higher measures indicate a great degree of similarity.
 * <p>
 * NOTE: Currently experimental and incomplete.
 * 
 * @author mbdavis
 *
 */
public class AreaSimilarityMeasure 
	implements SimilarityMeasure
{
	/*
	public static double measure(Geometry a, Geometry b)
	{
		AreaSimilarityMeasure gv = new AreaSimilarityMeasure(a, b);
		return gv.measure();
	}
	*/
	
	public AreaSimilarityMeasure()
	{
	}
	
	public double measure(Geometry g1, Geometry g2)
	{		
		double areaInt = g1.intersection(g2).getArea();
		double areaUnion = g1.union(g2).getArea();
		return areaInt / areaUnion;
	}
	
	
}
