package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * 
 * Different formats for 3D printers
 * 
 */
public interface Format {

	/**
	 * @return file extension for this format
	 */
	public String getExtension();

	/**
	 * script start
	 * 
	 * @param sb
	 *            output builder
	 */
	public void getScriptStart(StringBuilder sb);

	/**
	 * script end
	 * 
	 * @param sb
	 *            output builder
	 */
	public void getScriptEnd(StringBuilder sb);

	/**
	 * @param sb
	 *            output builder
	 * @param type
	 *            object type
	 * @param geo
	 *            geo
	 * @param transparency
	 *            if this object is transparent
	 * @param color
	 *            color
	 * @param alpha
	 *            object alpha
	 */
	public void getObjectStart(StringBuilder sb, String type, GeoElement geo, boolean transparency,
			GColor color, double alpha);

	/**
	 * start for new polyhedron
	 * 
	 * @param sb
	 *            output builder
	 */
	public void getPolyhedronStart(StringBuilder sb);

	/**
	 * end for polyhedron
	 * 
	 * @param sb
	 *            output builder
	 */
	public void getPolyhedronEnd(StringBuilder sb);

	/**
	 * 
	 * start for new vertices list
	 * 
	 * @param count
	 *            vertices length
	 * @param sb
	 *            output builder
	 */
	public void getVerticesStart(StringBuilder sb, int count);

	/**
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord vertex description
	 * @param sb
	 *            output builder
	 */
	public void getVertices(StringBuilder sb, double x, double y, double z);

	/**
	 * separator for vertices list
	 * 
	 * @param sb
	 *            output builder
	 */
	public void getVerticesSeparator(StringBuilder sb);

	/**
	 * 
	 * end for vertex
	 * 
	 * @param sb
	 *            output builder
	 */
	public void getVerticesEnd(StringBuilder sb);

	/**
	 * 
	 * start for new normals
	 * 
	 * @param sb
	 *            output builder
	 * @param count
	 *            normals length
	 */
	public void getNormalsStart(StringBuilder sb, int count);

	/**
	 * @param sb
	 *            output builder
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord normal description
	 */
	public void getNormal(StringBuilder sb, double x, double y, double z);

	/**
	 * 
	 * separator for normals list
	 * 
	 * @param sb
	 *            output builder
	 */
	public void getNormalsSeparator(StringBuilder sb);

	/**
	 * 
	 * end for normals
	 * 
	 * @param sb
	 *            output builder
	 */
	public void getNormalsEnd(StringBuilder sb);

	/**
	 * 
	 * start for new face
	 * 
	 * @param sb
	 *            output builder
	 * @param count
	 *            faces length
	 * @param hasSpecificNormals
	 *            says if we'll pass specific normals indices
	 */
	public void getFacesStart(StringBuilder sb, int count, boolean hasSpecificNormals);

	/**
	 * @param sb
	 *            output builder
	 * @param v1
	 *            first index
	 * @param v2
	 *            second index
	 * @param v3
	 *            third index face description
	 * @param normal
	 *            normal index
	 */
	public void getFaces(StringBuilder sb, int v1, int v2, int v3, int normal);

	/**
	 * 
	 * separator for faces list
	 * 
	 * @param sb
	 *            output builder
	 */
	public void getFacesSeparator(StringBuilder sb);

	/**
	 * end for face
	 * 
	 * @param sb
	 *            output builder
	 */
	public void getFacesEnd(StringBuilder sb);

	/**
	 * 
	 * @return true if this format can export surfaces
	 */
	public boolean handlesSurfaces();

	/**
	 * 
	 * @return true if needs closed objects (for stl export)
	 */
	public boolean needsClosedObjects();

	/**
	 * 
	 * @return true if it handles normals
	 */
	public boolean handlesNormals();

	/**
	 * 
	 * @return true if it always uses a specific view for export
	 */
	public boolean useSpecificViewForExport();

	/**
	 * set scale for vertices
	 * 
	 * @param scale
	 *            scale
	 */
	public void setScale(double scale);

}
