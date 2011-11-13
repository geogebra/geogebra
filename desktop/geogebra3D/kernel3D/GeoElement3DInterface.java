package geogebra3D.kernel3D;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Matrix.CoordMatrix4x4;
import geogebra.kernel.Matrix.Coords;
import geogebra3D.euclidian3D.Drawable3D;



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
	
	/** returns a 4x4 matrix for drawing the {@link Drawable3D} 
	 * @return the drawing matrix*/
	public CoordMatrix4x4 getDrawingMatrix();
	
	
	/** sets the 4x4 matrix for drawing the {@link Drawable3D} 
	 * @param a_drawingMatrix the drawing matrix*/
	public void setDrawingMatrix(CoordMatrix4x4 a_drawingMatrix);
	

	
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
