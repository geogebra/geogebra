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
			GPUBuffers buffers, int attrib);

	/**
	 * create gpu buffers and store ids
	 * 
	 * @param buffers
	 *            where to store ids
	 */
	public void createBuffers(GPUBuffers buffers);

	public void bindBufferForVertices(GPUBuffers buffers, int attrib, int size);

	public void bindBufferForColors(GPUBuffers buffers, int attrib, int size,
			GLBuffer fbColors);

	public void bindBufferForNormals(GPUBuffers buffers, int attrib, int size,
			GLBuffer fbNormals);

	public void bindBufferForTextures(GPUBuffers buffers, int attrib, int size,
			GLBuffer fbTextures);

}
