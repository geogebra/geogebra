package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

/**
 * 
 * Different formats for 3D printers
 * 
 */
public interface Format {

	/**
	 * 
	 * file extension for this format
	 */
	public void getExtension(StringBuilder sb);

	/**
	 * 
	 * script start
	 */
	public void getScriptStart(StringBuilder sb);

	/**
	 * 
	 * script end
	 */
	public void getScriptEnd(StringBuilder sb);

	/**
	 * 
	 * @param type
	 *            object type
	 * @param label
	 *            object label string when new object starts
	 */
	public void getObjectStart(StringBuilder sb, String type, String label);

	/**
	 * 
	 * start for new polyhedron
	 */
	public void getPolyhedronStart(StringBuilder sb);

	/**
	 * 
	 * end for polyhedron
	 */
	public void getPolyhedronEnd(StringBuilder sb);

	/**
	 * 
	 * start for new vertex
	 */
	public void getVerticesStart(StringBuilder sb);

	/**
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord vertex description
	 */
	public void getVertices(StringBuilder sb, double x, double y, double z);

	/**
	 * 
	 * separator for vertices list
	 */
	public void getVerticesSeparator(StringBuilder sb);

	/**
	 * 
	 * end for vertex
	 */
	public void getVerticesEnd(StringBuilder sb);

	/**
	 * 
	 * start for new normals
	 */
	public void getNormalsStart(StringBuilder sb);

	/**
	 * 
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
	 */
	public void getNormalsSeparator(StringBuilder sb);

	/**
	 * 
	 * end for normals
	 */
	public void getNormalsEnd(StringBuilder sb);

	/**
	 * 
	 * start for new face
	 */
	public void getFacesStart(StringBuilder sb);

	/**
	 * 
	 * @param v1
	 *            first index
	 * @param v2
	 *            second index
	 * @param v3
	 *            third index face description
	 */
	public void getFaces(StringBuilder sb, int v1, int v2, int v3);

	/**
	 * 
	 * separator for faces list
	 */
	public void getFacesSeparator(StringBuilder sb);

	/**
	 * 
	 * end for face
	 */
	public void getFacesEnd(StringBuilder sb);

	/**
	 * append list type
	 * 
	 * @param sb
	 *            string builder
	 * @param type
	 *            list type
	 */
	public void getListType(StringBuilder sb, int type);

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

}
