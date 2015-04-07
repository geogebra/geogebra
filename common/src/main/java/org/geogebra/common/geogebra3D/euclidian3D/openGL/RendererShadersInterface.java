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

}
