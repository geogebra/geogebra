package com.vividsolutions.jts.algorithm.match;

public class SimilarityMeasureCombiner 
{
	public static double combine(double measure1, double measure2)
	{
		return Math.min(measure1, measure2);
	}

}
