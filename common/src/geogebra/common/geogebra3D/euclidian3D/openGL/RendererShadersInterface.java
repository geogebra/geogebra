package geogebra.common.geogebra3D.euclidian3D.openGL;


/**
 * interface for renderers using shaders
 * @author mathieu
 *
 */
public interface RendererShadersInterface {

	public void loadColorBuffer(GLBuffer fbColors, int length);

	public void loadNormalBuffer(GLBuffer fbNormals, int length);

	public void loadTextureBuffer(GLBuffer fbTextures, int length);

	public void loadVertexBuffer(GLBuffer fbVertices, int length);

	/**
	 * @return true if textures are enabled
	 */
	public boolean areTexturesEnabled();

	public void draw(Manager.Type type, int length);

}
