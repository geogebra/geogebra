package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * 
 * Different formats for 3D printers
 * 
 */
abstract public class Format {

	private String newline = ExportToPrinter3D.NEWLINE;

	/**
	 * @return file extension for this format
	 */
	abstract public String getExtension();

	/**
	 * script start
	 * 
	 * @param sb
	 *            output builder
	 */
	abstract public void getScriptStart(StringBuilder sb);

	/**
	 * script end
	 * 
	 * @param sb
	 *            output builder
	 */
	abstract public void getScriptEnd(StringBuilder sb);

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
	abstract public void getObjectStart(StringBuilder sb, String type,
			GeoElement geo, boolean transparency,
			GColor color, double alpha);

	/**
	 * start for new polyhedron
	 * 
	 * @param sb
	 *            output builder
	 * @param isFlat
	 *            all geometries are in the same plane
	 * @param isCurve
	 *            geometry is a curve
	 */
	abstract public void getPolyhedronStart(StringBuilder sb, boolean isFlat,
			boolean isCurve);

	/**
	 * end for polyhedron
	 * 
	 * @param sb
	 *            output builder
	 */
	abstract public void getPolyhedronEnd(StringBuilder sb);

	/**
	 * 
	 * start for new vertices list
	 * 
	 * @param count
	 *            vertices length
	 * @param sb
	 *            output builder
	 */
	abstract public void getVerticesStart(StringBuilder sb, int count);

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
	abstract public void getVertices(StringBuilder sb, double x, double y,
			double z);

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
	 * @param thickness
	 *            thickness (for surfaces)
	 */
	abstract public void getVertices(StringBuilder sb, double x, double y,
			double z, double thickness);

	/**
	 * separator for vertices list
	 * 
	 * @param sb
	 *            output builder
	 */
	abstract public void getVerticesSeparator(StringBuilder sb);

	/**
	 * 
	 * end for vertex
	 * 
	 * @param sb
	 *            output builder
	 */
	abstract public void getVerticesEnd(StringBuilder sb);

	/**
	 * 
	 * start for new normals
	 * 
	 * @param sb
	 *            output builder
	 * @param count
	 *            normals length
	 */
	abstract public void getNormalsStart(StringBuilder sb, int count);

	/**
	 * @param sb
	 *            output builder
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord normal description
	 * @param withThickness
	 *            if uses thickness (for surfaces)
	 */
	abstract public void getNormal(StringBuilder sb, double x, double y,
			double z, boolean withThickness);

	/**
	 * 
	 * separator for normals list
	 * 
	 * @param sb
	 *            output builder
	 */
	abstract public void getNormalsSeparator(StringBuilder sb);

	/**
	 * 
	 * end for normals
	 * 
	 * @param sb
	 *            output builder
	 */
	abstract public void getNormalsEnd(StringBuilder sb);

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
	abstract public void getFacesStart(StringBuilder sb, int count,
			boolean hasSpecificNormals);

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
	 * @return true if vertices order is consistent with normal orientation
	 */
	abstract public boolean getFaces(StringBuilder sb, int v1, int v2, int v3,
			int normal);

	/**
	 * 
	 * separator for faces list
	 * 
	 * @param sb
	 *            output builder
	 */
	abstract public void getFacesSeparator(StringBuilder sb);

	/**
	 * end for face
	 * 
	 * @param sb
	 *            output builder
	 */
	abstract public void getFacesEnd(StringBuilder sb);

	/**
	 * 
	 * @return true if this format can export surfaces
	 */
	abstract public boolean handlesSurfacesDirectly();

	/**
	 * 
	 * @return true if needs closed objects (for stl export)
	 */
	abstract public boolean needsClosedObjectsForCurves();

	/**
	 * 
	 * @return true if needs closed objects (for stl export)
	 */
	abstract public boolean needsClosedObjectsForSurfaces();

	/**
	 * 
	 * @return true if needs scaling (for stl export)
	 */
	abstract public boolean needsScale();

	/**
	 * 
	 * @return true if it handles normals
	 */
	abstract public boolean handlesNormals();

	/**
	 * 
	 * @return true if it always uses a specific view for export
	 */
	abstract public boolean useSpecificViewForExport();

	/**
	 * set scale for vertices
	 * 
	 * @param scale
	 *            scale
	 */
	abstract public void setScale(double scale);

	/**
	 * 
	 * @param newline
	 *            set string used for newline
	 */
	public void setNewlineString(String newline) {
		this.newline = newline;
	}

	/**
	 * append a newline string to string builder
	 * 
	 * @param sb
	 *            string builder
	 */
	protected void appendNewline(StringBuilder sb) {
		sb.append(newline);
	}

	/**
	 * 
	 * @return true if it needs both-sided surfaces
	 */
	abstract boolean needsBothSided();

	/**
	 * set if it wants filled solids
	 * 
	 * @param flag
	 *            flag
	 */
	abstract public void setWantsFilledSolids(boolean flag);

	/**
	 * 
	 * @return true if wants "filled solids"
	 */
	abstract public boolean wantsFilledSolids();

	/**
	 * set if it exports lines and points
	 * 
	 * @param flag
	 *            flag
	 */
	abstract public void setExportsPointsAndLines(boolean flag);

	/**
	 * 
	 * @return true if exports lines and points
	 */
	abstract public boolean exportsPointsAndLines();
}
