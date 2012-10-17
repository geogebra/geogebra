package com.vividsolutions.jts.noding;

import java.util.*;
import com.vividsolutions.jts.algorithm.*;

/**
 * Finds if two sets of {@link SegmentStrings}s intersect.
 * Uses indexing for fast performance and to optimize repeated tests
 * against a target set of lines.
 * Short-circuited to return as soon an intersection is found.
 *
 * @version 1.7
 */
public class FastSegmentSetIntersectionFinder 
{
	private SegmentSetMutualIntersector segSetMutInt; 
	// for testing purposes
//	private SimpleSegmentSetMutualIntersector mci;  

	public FastSegmentSetIntersectionFinder(Collection baseSegStrings)
	{
		init(baseSegStrings);
	}
	
	private void init(Collection baseSegStrings)
	{
    segSetMutInt = new MCIndexSegmentSetMutualIntersector();
//    segSetMutInt = new MCIndexIntersectionSegmentSetMutualIntersector();
    
//		mci = new SimpleSegmentSetMutualIntersector();
		segSetMutInt.setBaseSegments(baseSegStrings);
	}
	
	/**
	 * Gets the segment set intersector used by this class.
	 * This allows other uses of the same underlying indexed structure.
	 * 
	 * @return the segment set intersector used
	 */
	public SegmentSetMutualIntersector getSegmentSetIntersector()
	{
		return segSetMutInt;
	}
	
  private static LineIntersector li = new RobustLineIntersector();

	public boolean intersects(Collection segStrings)
	{
		SegmentIntersectionDetector intFinder = new SegmentIntersectionDetector(li);
		segSetMutInt.setSegmentIntersector(intFinder);

		segSetMutInt.process(segStrings);
		return intFinder.hasIntersection();
	}
	
	public boolean intersects(Collection segStrings, SegmentIntersectionDetector intDetector)
	{
		segSetMutInt.setSegmentIntersector(intDetector);

		segSetMutInt.process(segStrings);
		return intDetector.hasIntersection();
	}
}
