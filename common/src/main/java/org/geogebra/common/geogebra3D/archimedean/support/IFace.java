package org.geogebra.common.geogebra3D.archimedean.support;

/**
 * The interface that all sides of an Archimedean solid should implement.
 * 
 * @author kasparianr
 * 
 */

public interface IFace {
	/**
	 * Indices into the array of vertices contained by the parent Archimedean
	 * solid. These will be returned in clockwise order when facing the exterior
	 * side of this face. Exterior means in this case, the side that would be
	 * visible when viewed from a point outside the Archimedean solid.
	 */
	public int[] getVertexIndices();

	/**
	 * Get the number of vertices in this IFace
	 * 
	 * @return
	 */
	public int getVertexCount();

}
