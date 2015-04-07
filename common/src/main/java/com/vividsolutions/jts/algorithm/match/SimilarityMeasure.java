package com.vividsolutions.jts.algorithm.match;

import com.vividsolutions.jts.geom.*;

/**
 * An interface for classes which measures the degree of similarity between two {@link Geometry}s.
 * The computed measure lies in the range [0, 1].
 * Higher measures indicate a great degree of similarity.
 * A measure of 1.0 indicates that the input geometries are identical
 * A measure of 0.0 indicates that the geometries
 * have essentially no similarity.
 * The precise definition of "identical" and "no similarity" may depend on the 
 * exact algorithm being used.
 * 
 * @author mbdavis
 *
 */
public interface SimilarityMeasure
{
	
	double measure(Geometry g1, Geometry g2);
}
