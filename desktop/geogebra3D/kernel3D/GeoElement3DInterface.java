package geogebra3D.kernel3D;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;

/**
 * GeoElement3D's common interface, used for special cases 
 * where the GeoElement3D must extend a GeoElement (e.g. GeoPolygon3D extends GeoPolygon).
 * 
 * See {@link GeoElement3D} for standard implementation.
 * 
 * @author ggb3D
 *
 */
public interface GeoElement3DInterface {
	
	
	// link to 2D GeoElement
    /**
     * return if linked to a 2D GeoElement
     * @return has a 2D GeoElement
     */
    public boolean hasGeoElement2D();
    

    /**
     * return the 2D GeoElement linked to
     * @return 2D GeoElement
     */
    public GeoElement getGeoElement2D();
    
    
    /**
     * set the 2D GeoElement linked to
     * @param geo a 2D GeoElement
     */
    public void setGeoElement2D(GeoElement geo);
	

    /** return a direction for view (orthogonal to plane, through line, etc.)
     * (null if none)
     * @return a direction for view 
     */
	public Coords getMainDirection();

}
