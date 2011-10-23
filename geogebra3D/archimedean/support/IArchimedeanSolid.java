package geogebra3D.archimedean.support;

/**
 * The interface that all Archimedean solids should implement.
 * 
 * @author kasparianr
 * 
 */
public interface IArchimedeanSolid {
	/**
	 * Get the number of vertices in this IArchimedeanSolid
	 * 
	 * @return
	 */
	public int getVertexCount();

	/**
	 * Return all the vertices of this IArchimedeanSolid.
	 * 
	 * @return an array of vertices
	 */
	public Point[] getVertices();

	/**
	 * Get the number of faces in this IArchimedeanSolid
	 * 
	 * @return
	 */
	public int getFaceCount();

	/**
	 * Return all the faces of this IArchimedeanSolid.
	 * 
	 * @return an array of faces
	 */
	public IFace[] getFaces();

	public IFace createFace();

}
