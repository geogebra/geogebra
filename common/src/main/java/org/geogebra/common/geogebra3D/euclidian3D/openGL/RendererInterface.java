package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawLabel3D;
import org.geogebra.common.kernel.geos.GeoElement;

public interface RendererInterface {
	/**
	 * 
	 * @return canvas (for desktop version at least)
	 */
	abstract public Object getCanvas();

	/**
	 * re-calc the display immediately
	 */
	abstract public void display();

	/**
	 * enable culling
	 */
	abstract public void enableCulling();

	/**
	 * disable culling
	 */
	abstract public void disableCulling();

	/**
	 * cull front faces
	 */
	abstract public void setCullFaceFront();

	/**
	 * cull back faces
	 */
	abstract public void setCullFaceBack();

	/**
	 * disable blending
	 */
	abstract public void disableBlending();

	/**
	 * enable blending
	 */
	abstract public void enableBlending();

	/**
	 * enable textures
	 */
	abstract public void enableTextures();

	/**
	 * disable multi samples (for antialiasing)
	 */
	abstract public void disableTextures();

	/**
	 * enable multi samples (for antialiasing)
	 */
	abstract public void enableMultisample();

	/**
	 * disable textures
	 */
	abstract public void disableMultisample();

	/**
	 * enable alpha test : avoid z-buffer writing for transparent parts
	 */
	abstract public void enableAlphaTest();

	/**
	 * disable alpha test
	 */
	abstract public void disableAlphaTest();

	/**
	 * disable lighting
	 */
	abstract public void disableLighting();

	/**
	 * enable lighting
	 */
	abstract public void enableLighting();

	/**
	 * init lighting
	 */
	abstract public void initLighting();

	/**
	 * set real-world origin for label
	 * 
	 * @param origin
	 *            real-world coordinates
	 */
	abstract public void setLabelOrigin(float[] origin);

	/**
	 * sets the clip planes
	 * 
	 * @param minMax
	 *            min/max for x/y/z
	 */
	abstract public void setClipPlanes(double[][] minMax);

	/**
	 * enable depth mask (write in depth buffer)
	 */
	abstract public void enableDepthMask();

	/**
	 * disable depth mask (write in depth buffer)
	 */
	abstract public void disableDepthMask();

	/**
	 * enable depth test
	 */
	abstract public void enableDepthTest();

	/**
	 * disable depth test
	 */
	abstract public void disableDepthTest();

	/**
	 * set line width
	 * 
	 * @param width
	 *            line width
	 */
	abstract public void setLineWidth(double width);

	// layer
	/**
	 * sets the layer to l. Use gl.glPolygonOffset( ).
	 * 
	 * @param l
	 *            the layer
	 */
	abstract public void setLayer(int l);

	/**
	 * sets the drawing matrix to openGLlocal. same as
	 * initMatrix(m_drawingMatrix)
	 */
	abstract public void initMatrix();

	/**
	 * set the matrix for face to screen part
	 */
	abstract public void initMatrixForFaceToScreen();

	/**
	 * turn off the last drawing matrix set in openGLlocal.
	 */
	abstract public void resetMatrix();

	/**
	 * enable GL textures 2D
	 */
	abstract public void enableTextures2D();

	/**
	 * disable GL textures 2D
	 */
	abstract public void disableTextures2D();

	/**
	 * generate textures
	 * 
	 * @param number
	 *            texture length
	 * @param index
	 *            indices
	 */
	abstract public void genTextures2D(int number, int[] index);

	/**
	 * bind the texture
	 * 
	 * @param index
	 *            texture index
	 */
	abstract public void bindTexture(int index);

	abstract public GBufferedImage createBufferedImage(DrawLabel3D label);

	/**
	 * create alpha texture for label from image
	 * 
	 * @param label
	 *            label
	 * @param bimg
	 *            buffered image
	 */
	abstract public void createAlphaTexture(DrawLabel3D label, GBufferedImage bimg);

	/**
	 * 
	 * @param sizeX
	 *            width
	 * @param sizeY
	 *            height
	 * @param buf
	 *            image data
	 * @return a texture for alpha channel
	 */
	abstract public int createAlphaTexture(int sizeX, int sizeY, byte[] buf);

	/**
	 * @param sizeX
	 *            width
	 * @param sizeY
	 *            height
	 * @param buf
	 *            image data
	 */
	abstract public void textureImage2D(int sizeX, int sizeY, byte[] buf);

	/**
	 * set texture linear parameters
	 */
	abstract public void setTextureLinear();

	/**
	 * set texture nearest parameters
	 */
	abstract public void setTextureNearest();

	/**
	 * set hits for mouse location
	 * 
	 * @param mouseLoc
	 *            mouse location
	 * @param threshold
	 *            threshold
	 */
	abstract public void setHits(GPoint mouseLoc, int threshold);

	/**
	 * set label hits for mouse location
	 * 
	 * @param mouseLoc
	 *            mouse location
	 * @return first label hitted geo
	 */
	abstract public GeoElement getLabelHit(GPoint mouseLoc);

	/**
	 * process picking for intersection curves SHOULD NOT BE CALLED OUTSIDE THE
	 * DISPLAY LOOP
	 */
	abstract public void pickIntersectionCurves();

	/**
	 * enable dash (for not hidden lines)
	 */
	public void enableDash();
	
	/**
	 * enable dash (for hidden lines)
	 */
	public void enableDashHidden();

	abstract public void setDashTexture(int index);

	/**
	 * 
	 * @return true if it uses shaders
	 */
	abstract public boolean useShaders();

	/**
	 * enable fading (e.g. for planes)
	 */
	abstract public void enableFading();

	/**
	 * create a dummy texture to please the GL shader language, that needs
	 * something correct to be bound on texture 0
	 */
	abstract public void createDummyTexture();

	/**
	 * set clear color
	 * 
	 * @param r
	 *            red
	 * @param g
	 *            green
	 * @param b
	 *            blue
	 * @param a
	 *            alpha
	 */
	abstract public void setClearColor(float r, float g, float b, float a);

	/**
	 * ensure that animation is on (needed when undocking/docking 3D view)
	 */
	abstract public void resumeAnimator();

	/**
	 * for shaders : update projection matrix
	 */
	abstract public void updateOrthoValues();

}
