package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.kernel.Matrix.Coords;

/**
 * interface for renderers using shaders
 * 
 * @author mathieu
 *
 */
public interface RendererShadersInterface {

	public void loadColorBuffer(GLBuffer fbColors, int length);

	public void loadNormalBuffer(GLBuffer fbNormals, int length);

	public void loadTextureBuffer(GLBuffer fbTextures, int length);

	public void loadVertexBuffer(GLBuffer fbVertices, int length);

	public void setCenter(Coords center);

	public void resetCenter();

	/**
	 * @return true if textures are enabled
	 */
	public boolean areTexturesEnabled();

	public void draw(Manager.Type type, int length);

	/**
	 * bind cpu buffer to gpu buffer
	 * 
	 * @param fb
	 *            cpu buffer
	 * @param length
	 *            buffer length
	 * @param size
	 *            buffer element size
	 * @param buffers
	 *            gpu buffers
	 * @param attrib
	 *            attrib in gpu buffers
	 */
	public void storeBuffer(GLBuffer fb, int length, int size,
			GPUBuffer buffers, int attrib);

	/**
	 * 
	 * @param fb
	 *            cpu buffer
	 * @param length
	 *            buffer length
	 * @param buffers
	 *            gpu buffers
	 */
	public void storeElementBuffer(short fb[], int length,
			GPUBuffer buffers);

	/**
	 * 
	 * @param buffers
	 *            gpu buffers
	 */
	public void bindBufferForIndices(GPUBuffer buffer);

	/**
	 * create gpu buffer and store id
	 * 
	 * @param buffer
	 *            where to store id
	 */
	public void createBuffer(GPUBuffer buffer);

	/**
	 * remove gpu buffer
	 * 
	 * @param buffer
	 *            id for gpu buffer
	 */
	public void removeBuffer(GPUBuffer buffer);

	public void bindBufferForVertices(GPUBuffer buffer, int size);

	public void bindBufferForColors(GPUBuffer buffer, int size,
			GLBuffer fbColors);

	public void bindBufferForNormals(GPUBuffer buffer, int size,
			GLBuffer fbNormals);

	public void bindBufferForTextures(GPUBuffer buffer, int size,
			GLBuffer fbTextures);

}
