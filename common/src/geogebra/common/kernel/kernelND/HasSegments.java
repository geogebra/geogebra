package geogebra.common.kernel.kernelND;


/**
 * interface for polygons, polyhedrons, etc., that have segments
 * @author mathieu
 *
 */
public interface HasSegments {
	
	/**
	 * 
	 * @return segments
	 */
	public GeoSegmentND[] getSegments();

}
