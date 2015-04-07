package org.geogebra.common.geogebra3D.euclidian3D.plots;

/**
 * An interface used in BucketPQ to assign elements to buckets
 * 
 * @author André Eriksson
 * 
 * @param <ET>
 */
public interface BucketAssigner<ET> {

	/**
	 * Assigns a bucket between 0 and bucketAmt to the provided object. If the
	 * priority queue is to work well, it is important that this function
	 * imposes a total ordering on the objects it's intended for.
	 * 
	 * @param o
	 *            an object to insert
	 * @param bucketAmt
	 *            the amount of buckets used in the queue
	 * @return a bucket number (between 0 and bucketAmt)
	 */
	int getBucketIndex(Object o, int bucketAmt);
}
