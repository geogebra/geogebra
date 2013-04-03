package geogebra.common.kernel.geos;


/**
 * Interface for geos coming from a "meta geo", e.g. a segment coming from a polygon
 * @author mathieu
 *
 */
public interface FromMeta {

	/**
	 * 
	 * @return "meta geos", e.g. polygon for a segment
	 */
	public GeoElement[] getMetas();
}
