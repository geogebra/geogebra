package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.Stack;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.main.Feature;

/**
 * implementation for renderer using shaders
 * 
 * @author mathieu
 *
 */
public abstract class RendererImplShaders implements RendererImpl {

	static public int GLSL_ATTRIB_POSITION;
	static public int GLSL_ATTRIB_COLOR;
	static public int GLSL_ATTRIB_NORMAL;
	static public int GLSL_ATTRIB_TEXTURE;
	static public int GLSL_ATTRIB_INDEX;

	final static protected int TEXTURE_TYPE_NONE = 0;
	final static protected int TEXTURE_TYPE_FADING = 1;
	final static protected int TEXTURE_TYPE_TEXT = 2;
	final static protected int TEXTURE_TYPE_DASH = 4;

	// location values for shader fields
	protected Object matrixLocation; // matrix
	protected Object lightPositionLocation, ambiantDiffuseLocation,
			enableLightLocation, enableShineLocation; // light
	protected Object eyePositionLocation; // eye position
	protected Object cullingLocation; // culling type
	protected Object colorLocation; // color
	protected Object centerLocation; // center
	protected Object enableClipPlanesLocation, clipPlanesMinLocation,
			clipPlanesMaxLocation; // enable / disable clip planes
	protected Object labelRenderingLocation, labelOriginLocation;
	protected Object normalLocation; // one normal for all vertices
	protected Object textureTypeLocation; // textures
	protected Object dashValuesLocation; // values for dash

	protected GPUBuffer vboVertices;
	protected GPUBuffer vboColors;
	protected GPUBuffer vboNormals;
	protected GPUBuffer vboTextureCoords;
	protected GPUBuffer vboIndices;

	protected float[] tmpNormal3 = new float[3];

	protected CoordMatrix4x4 projectionMatrix = new CoordMatrix4x4();

	protected CoordMatrix4x4 tmpMatrix1 = new CoordMatrix4x4(),
			tmpMatrix2 = new CoordMatrix4x4();

	protected float[] tmpFloat16 = new float[16];

	protected boolean oneNormalForAllVertices;

	protected Object shaderProgram;
	protected Object vertShader;
	protected Object fragShader;

	protected EuclidianView3D view3D;

	protected Renderer renderer;

	public RendererImplShaders(Renderer renderer, EuclidianView3D view) {
		this.renderer = renderer;
		this.view3D = view;
	}

	abstract protected void createBufferFor(GPUBuffer buffer);

	final private void createBuffer(GPUBuffer buffer, Stack<Object> stack) {
		if (stack.isEmpty()) {
			createBufferFor(buffer);
		} else {
			buffer.set(stack.pop());
		}
	}

	private Stack<Object> removedBuffers = new Stack<Object>();
	private Stack<Object> removedElementBuffers = new Stack<Object>();

	final public void createArrayBuffer(GPUBuffer buffer) {
		createBuffer(buffer, removedBuffers);
	}

	final public void createElementBuffer(GPUBuffer buffer) {
		createBuffer(buffer, removedElementBuffers);
	}

	final private static void removeBuffer(GPUBuffer buffer, Stack<Object> stack) {
		stack.push(buffer.get());
	}

	public void removeArrayBuffer(GPUBuffer buffer) {
		removeBuffer(buffer, removedBuffers);
	}

	public void removeElementBuffer(GPUBuffer buffer) {
		removeBuffer(buffer, removedElementBuffers);
	}

	@Override
	public void storeBuffer(GLBuffer fb, int length, int size,
			GPUBuffer buffer, int attrib) {
		// Select the VBO, GPU memory data
		bindBuffer(buffer);

		// transfer data to VBO, this perform the copy of data from CPU -> GPU
		// memory
		glBufferData(getStoreBufferNumBytes(length, size), fb);

	}

	/**
	 * push buffer data
	 * 
	 * @param numBytes
	 *            data size
	 * @param fb
	 *            buffer array
	 */
	abstract protected void glBufferData(int numBytes, GLBuffer fb);

	abstract protected int getStoreBufferNumBytes(int length, int size);

	@Override
	final public void bindBufferForIndices(GPUBuffer buffer) {
		bindBuffer(getGL_ELEMENT_ARRAY_BUFFER(), buffer);
	}

	final protected void bindBuffer(GPUBuffer buffer) {
		bindBuffer(getGL_ARRAY_BUFFER(), buffer);
	}

	abstract protected void bindBuffer(int bufferType, GPUBuffer buffer);
	
	abstract protected int getGL_ELEMENT_ARRAY_BUFFER();

	abstract protected int getGL_ARRAY_BUFFER();



	/**
	 * set vertex attribute pointer
	 * 
	 * @param attrib
	 *            attribute
	 * @param size
	 *            size
	 */
	abstract protected void vertexAttribPointer(int attrib, int size);

	final protected void vertexAttribPointerGlobal(int attrib, int size) {
		// vertexAttribPointer(attrib, size);
	}

	@Override
	final public void bindBufferForVertices(GPUBuffer buffer, int size) {
		// Select the VBO, GPU memory data
		bindBuffer(buffer);
		// Associate Vertex attribute 0 with the last bound VBO
		vertexAttribPointer(GLSL_ATTRIB_POSITION, size);

		// enable VBO
		glEnableVertexAttribArray(GLSL_ATTRIB_POSITION);
	}

	@Override
	final public void bindBufferForColors(GPUBuffer buffer, int size,
			GLBuffer fbColors) {
		if (fbColors == null || fbColors.isEmpty()) {
			glDisableVertexAttribArray(GLSL_ATTRIB_COLOR);
			return;
		}

		// prevent use of global color
		setColor(-1, -1, -1, -1);

		// Select the VBO, GPU memory data, to use for normals
		bindBuffer(buffer);

		// Associate Vertex attribute 1 with the last bound VBO
		vertexAttribPointer(GLSL_ATTRIB_COLOR, size);

		glEnableVertexAttribArray(GLSL_ATTRIB_COLOR);
	}
	
	abstract protected void glUniform3fv(Object location, float[] values);
	abstract protected void glUniform3f(Object location, float x, float y, float z);

	protected void resetOneNormalForAllVertices() {
		oneNormalForAllVertices = false;
		glUniform3f(normalLocation, 2, 2, 2);
	}

	@Override
	final public void bindBufferForNormals(GPUBuffer buffer, int size,
			GLBuffer fbNormals) {
		if (fbNormals == null || fbNormals.isEmpty()) { // no normals
			glDisableVertexAttribArray(GLSL_ATTRIB_NORMAL);
			return;
		}

		if (fbNormals.capacity() == 3) { // one normal for all vertices
			fbNormals.array(tmpNormal3);
			glUniform3fv(normalLocation, tmpNormal3);
			oneNormalForAllVertices = true;
			glDisableVertexAttribArray(GLSL_ATTRIB_NORMAL);
			return;
		}

		// ///////////////////////////////////
		// VBO - normals

		if (oneNormalForAllVertices) {
			resetOneNormalForAllVertices();
		}

		// Select the VBO, GPU memory data, to use for normals
		bindBuffer(buffer);

		// Associate Vertex attribute 1 with the last bound VBO
		vertexAttribPointer(GLSL_ATTRIB_NORMAL, size);

		glEnableVertexAttribArray(GLSL_ATTRIB_NORMAL);
	}

	@Override
	public void bindBufferForTextures(GPUBuffer buffer, int size,
			GLBuffer fbTextures) {
		if (fbTextures == null || fbTextures.isEmpty()) {
			setCurrentGeometryHasNoTexture();
			glDisableVertexAttribArray(GLSL_ATTRIB_TEXTURE);
			return;
		}

		setCurrentGeometryHasTexture();

		// Select the VBO, GPU memory data, to use for normals
		bindBuffer(buffer);

		// Associate Vertex attribute 1 with the last bound VBO
		vertexAttribPointer(GLSL_ATTRIB_TEXTURE, size);

		glEnableVertexAttribArray(GLSL_ATTRIB_TEXTURE);
	}

	private boolean texturesEnabled;

	@Override
	final public void enableTextures() {
		texturesEnabled = true;
		setCurrentGeometryHasNoTexture(); // let first geometry init textures
	}

	@Override
	final public void disableTextures() {
		texturesEnabled = false;
		setCurrentTextureType(TEXTURE_TYPE_NONE);
		glDisableVertexAttribArray(GLSL_ATTRIB_TEXTURE);
	}

	/**
	 * tells that current geometry has a texture
	 */
	final public void setCurrentGeometryHasTexture() {
		if (areTexturesEnabled() && currentTextureType == TEXTURE_TYPE_NONE) {
			setCurrentTextureType(oldTextureType);
		}
	}

	/**
	 * tells that current geometry has no texture
	 */
	final public void setCurrentGeometryHasNoTexture() {
		if (areTexturesEnabled() && currentTextureType != TEXTURE_TYPE_NONE) {
			oldTextureType = currentTextureType;
			setCurrentTextureType(TEXTURE_TYPE_NONE);

		}
	}

	@Override
	public void enableFading() {
		enableTextures();
		setCurrentTextureType(TEXTURE_TYPE_FADING);
	}

	private int currentDash = Textures.DASH_INIT;

	@Override
	public void enableDash() {
		currentDash = Textures.DASH_INIT;
		enableTextures();
		setCurrentTextureType(TEXTURE_TYPE_DASH);
	}

	/**
	 * enable text textures
	 */
	@Override
	final public void enableTexturesForText() {
		setCurrentTextureType(TEXTURE_TYPE_TEXT);
	}

	private int currentTextureType = TEXTURE_TYPE_NONE;
	private int oldTextureType = TEXTURE_TYPE_NONE;

	private void setCurrentTextureType(int type) {
		currentTextureType = type;
		glUniform1i(textureTypeLocation, type);
	}

	@Override
	public boolean areTexturesEnabled() {
		return texturesEnabled;
	}

	@Override
	public void setDashTexture(int index) {
		if (currentDash == index) {
			return;
		}

		currentDash = index;
		if (index == Textures.DASH_NONE) {
			disableTextures();
		} else {
			enableTextures();
			setCurrentTextureType(TEXTURE_TYPE_DASH + index);
			glUniform1fv(dashValuesLocation, 4,
					Textures.DASH_SHADERS_VALUES[index - 1]);
		}
	}

	abstract protected void glUniform1i(Object location, int value);

	abstract protected void glUniform1fv(Object location, int length,
			float[] values);

	abstract protected void glEnableVertexAttribArray(int attrib);

	abstract protected void glDisableVertexAttribArray(int attrib);

	@Override
	public void loadVertexBuffer(GLBuffer fbVertices, int length) {

		// ///////////////////////////////////
		// VBO - vertices

		// Select the VBO, GPU memory data, to use for vertices
		bindBuffer(vboVertices);

		// transfer data to VBO, this perform the copy of data from CPU -> GPU
		// memory
		int numBytes = length * 12; // 4 bytes per float * 3 coords per vertex
		glBufferData(numBytes, fbVertices);

		// Associate Vertex attribute 0 with the last bound VBO
		vertexAttribPointerGlobal(GLSL_ATTRIB_POSITION, 3);

		// VBO
		glEnableVertexAttribArray(GLSL_ATTRIB_POSITION);
	}

	@Override
	public void loadColorBuffer(GLBuffer fbColors, int length) {

		if (fbColors == null || fbColors.isEmpty()) {
			glDisableVertexAttribArray(GLSL_ATTRIB_COLOR);
			return;
		}

		// prevent use of global color
		setColor(-1, -1, -1, -1);

		// Select the VBO, GPU memory data, to use for normals
		bindBuffer(vboColors);
		int numBytes = length * 16; // 4 bytes per float * 4 color values (rgba)
		glBufferData(numBytes, fbColors);

		// Associate Vertex attribute 1 with the last bound VBO
		vertexAttribPointerGlobal(GLSL_ATTRIB_COLOR, 4);

		glEnableVertexAttribArray(GLSL_ATTRIB_COLOR);
	}

	@Override
	public void loadTextureBuffer(GLBuffer fbTextures, int length) {

		if (fbTextures == null || fbTextures.isEmpty()) {
			setCurrentGeometryHasNoTexture();
			glDisableVertexAttribArray(GLSL_ATTRIB_TEXTURE);
			return;
		}

		setCurrentGeometryHasTexture();

		// Select the VBO, GPU memory data, to use for normals
		bindBuffer(vboTextureCoords);
		int numBytes = length * 8; // 4 bytes per float * 2 coords per texture
		glBufferData(numBytes, fbTextures);

		// Associate Vertex attribute 1 with the last bound VBO
		vertexAttribPointerGlobal(GLSL_ATTRIB_TEXTURE, 2);

		glEnableVertexAttribArray(GLSL_ATTRIB_TEXTURE);
	}

	@Override
	public void loadNormalBuffer(GLBuffer fbNormals, int length) {

		if (fbNormals == null || fbNormals.isEmpty()) { // no normals
			glDisableVertexAttribArray(GLSL_ATTRIB_NORMAL);
			return;
		}

		if (fbNormals.capacity() == 3) { // one normal for all vertices
			glDisableVertexAttribArray(GLSL_ATTRIB_NORMAL);
			fbNormals.array(tmpNormal3);
			glUniform3fv(normalLocation, tmpNormal3);
			oneNormalForAllVertices = true;
			return;
		}

		// ///////////////////////////////////
		// VBO - normals

		if (oneNormalForAllVertices) {
			resetOneNormalForAllVertices();
		}

		// Select the VBO, GPU memory data, to use for normals
		bindBuffer(vboNormals);
		int numBytes = length * 12; // 4 bytes per float * * 3 coords per normal
		glBufferData(numBytes, fbNormals);

		// Associate Vertex attribute 1 with the last bound VBO
		vertexAttribPointerGlobal(GLSL_ATTRIB_NORMAL, 3);

		glEnableVertexAttribArray(GLSL_ATTRIB_NORMAL);
	}

	@Override
	public void drawSurfacesOutline() {

		// TODO

	}

	@Override
	public void enableClipPlanes() {
		glUniform1i(enableClipPlanesLocation, 1);
	}

	@Override
	public void disableClipPlanes() {
		glUniform1i(enableClipPlanesLocation, 0);
	}

	@Override
	public void loadIndicesBuffer(GLBufferIndices arrayI, int length) {

		// ///////////////////////////////////
		// VBO - indices

		// Select the VBO, GPU memory data, to use for indices
		bindBufferForIndices(vboIndices);

		// transfer data to VBO, this perform the copy of data from CPU -> GPU
		// memory
		glBufferDataIndices(length * 2, arrayI);

	}

	abstract protected void glBufferDataIndices(int numBytes,
			GLBufferIndices arrayI);

	/**
	 * attribute vertex pointers
	 */
	protected void attribPointers() {

		bindBuffer(vboVertices);
		vertexAttribPointer(GLSL_ATTRIB_POSITION, 3);

		bindBuffer(vboNormals);
		vertexAttribPointer(GLSL_ATTRIB_NORMAL, 3);

		bindBuffer(vboColors);
		vertexAttribPointer(GLSL_ATTRIB_COLOR, 4);

		bindBuffer(vboTextureCoords);
		vertexAttribPointer(GLSL_ATTRIB_TEXTURE, 2);
	}


	abstract protected int getGLType(Type type);

	protected final void setModelViewIdentity() {
		projectionMatrix.getForGL(tmpFloat16);
		glUniformMatrix4fv(matrixLocation, tmpFloat16);
	}

	abstract protected void glUniformMatrix4fv(Object location, float[] values);

	@Override
	public void draw() {

		resetOneNormalForAllVertices();
		disableTextures();

		setModelViewIdentity();

	}

	@Override
	public void useShaderProgram() {
		glUseProgram(shaderProgram);
	}

	abstract protected void glUseProgram(Object program);

	@Override
	public void dispose() {

		glUseProgram(0);
		glDetachAndDeleteShader(shaderProgram, vertShader);
		glDetachAndDeleteShader(shaderProgram, fragShader);
		glDeleteProgram(shaderProgram);
	}

	abstract protected void glDetachAndDeleteShader(Object program,
			Object shader);

	abstract protected void glDeleteProgram(Object program);

	@Override
	public void setMatrixView() {

		if (renderer.isExportingImageEquirectangular()) {
			tmpMatrix2.set(view3D.getToScreenMatrix());
			tmpMatrix2.set(3, 4,
					tmpMatrix2.get(3, 4) + renderer.getEyeToScreenDistance());
			tmpMatrix1.setMul(projectionMatrix, tmpMatrix2);
		} else {
			tmpMatrix1.setMul(projectionMatrix, view3D.getToScreenMatrix());
		}

		tmpMatrix1.getForGL(tmpFloat16);

		glUniformMatrix4fv(matrixLocation, tmpFloat16);
	}

	@Override
	public void unsetMatrixView() {
		setModelViewIdentity();
	}

	abstract protected void glUniform4f(Object location, float a, float b,
			float c, float d);

	@Override
	public void setColor(float r, float g, float b, float a) {
		glUniform4f(colorLocation, r, g, b, a);
	}

	@Override
	public void initMatrix() {

		if (renderer.isExportingImageEquirectangular()) {
			tmpMatrix1.set(view3D.getToScreenMatrix());
			tmpMatrix1.set(3, 4,
					tmpMatrix1.get(3, 4) + renderer.getEyeToScreenDistance());
			tmpMatrix2.setMul(tmpMatrix1, renderer.getMatrix());
		} else {
			tmpMatrix2.setMul(view3D.getToScreenMatrix(), renderer.getMatrix());
		}

		tmpMatrix1.setMul(projectionMatrix, tmpMatrix2);
		tmpMatrix1.getForGL(tmpFloat16);

		glUniformMatrix4fv(matrixLocation, tmpFloat16);
	}

	@Override
	public void initMatrixForFaceToScreen() {

		tmpMatrix1.setMul(projectionMatrix, renderer.getMatrix());
		tmpMatrix1.getForGL(tmpFloat16);

		glUniformMatrix4fv(matrixLocation, tmpFloat16);
	}

	@Override
	public void resetMatrix() {
		setMatrixView();
	}

	@Override
	public void pushSceneMatrix() {
		// not used with shaders

	}

	@Override
	public void glLoadName(int loop) {
		// not used with shaders

	}

	@Override
	public void setLightPosition(float[] values) {
		glUniform3fv(lightPositionLocation, values);
		if (view3D.getMode() == EuclidianView3D.PROJECTION_PERSPECTIVE
				|| view3D.getMode() == EuclidianView3D.PROJECTION_PERSPECTIVE) {
			glUniform4fv(eyePositionLocation, view3D.getViewDirection()
					.get4ForGL());
		} else {
			glUniform4fv(eyePositionLocation, view3D.getEyePosition()
					.get4ForGL());
		}
	}

	abstract protected void glUniform4fv(Object location, float[] values);

	private float[][] ambiantDiffuse;

	@Override
	public void setLightAmbiantDiffuse(float ambiant0, float diffuse0,
			float ambiant1, float diffuse1) {

		float coeff = 1.414f;

		float a0 = ambiant0 * coeff;
		float d0 = 1 - a0;
		float a1 = ambiant1 * coeff;
		float d1 = 1 - a1;

		ambiantDiffuse = new float[][] { { a0, d0 }, { a1, d1 } };

	}

	abstract protected void glUniform2fv(Object location, float[] values);

	@Override
	public void setLight(int light) {

		glUniform2fv(ambiantDiffuseLocation, ambiantDiffuse[light]);
	}


	@Override
	public void setLightModel() {
		// not used with shaders
	}

	@Override
	public void setAlphaFunc() {
		// not used with shaders
	}

	@Override
	public void setView() {
		renderer.setProjectionMatrix();
	}

	@Override
	public void viewOrtho() {
		// the projection matrix is updated in updateOrthoValues()
	}

	@Override
	final public void updateOrthoValues() {

		projectionMatrix.set(1, 1, 2.0 / renderer.getWidth());
		projectionMatrix.set(2, 2, 2.0 / renderer.getHeight());
		projectionMatrix.set(3, 3, -2.0 / renderer.getVisibleDepth());
		projectionMatrix.set(4, 4, 1);

		projectionMatrix.set(2, 1, 0);
		projectionMatrix.set(3, 1, 0);
		projectionMatrix.set(4, 1, 0);

		projectionMatrix.set(1, 2, 0);
		projectionMatrix.set(3, 2, 0);
		projectionMatrix.set(4, 2, 0);

		projectionMatrix.set(1, 3, 0);
		projectionMatrix.set(2, 3, 0);
		projectionMatrix.set(4, 3, 0);

		projectionMatrix.set(1, 4, 0);
		projectionMatrix.set(2, 4, 0);
		projectionMatrix.set(3, 4, 0);

	}

	@Override
	public void viewPersp() {
		// the projection matrix is updated in updatePerspValues()

	}

	@Override
	public void updatePerspValues() {

		projectionMatrix
				.set(1,
						1,
						2
								* renderer.perspNear[renderer.eye]
								/ (renderer.perspRight[renderer.eye] - renderer.perspLeft[renderer.eye]));
		projectionMatrix.set(2, 1, 0);
		projectionMatrix.set(3, 1, 0);
		projectionMatrix.set(4, 1, 0);

		projectionMatrix.set(1, 2, 0);
		projectionMatrix
				.set(2,
						2,
						2
								* renderer.perspNear[renderer.eye]
								/ (renderer.perspTop[renderer.eye] - renderer.perspBottom[renderer.eye]));
		projectionMatrix.set(3, 2, 0);
		projectionMatrix.set(4, 2, 0);

		perspXZ = (renderer.perspRight[renderer.eye] + renderer.perspLeft[renderer.eye])
				/ (renderer.perspRight[renderer.eye] - renderer.perspLeft[renderer.eye]);

		projectionMatrix.set(1, 3, perspXZ);
		projectionMatrix
				.set(2,
						3,
						(renderer.perspTop[renderer.eye] + renderer.perspBottom[renderer.eye])
								/ (renderer.perspTop[renderer.eye] - renderer.perspBottom[renderer.eye]));
		projectionMatrix.set(3, 3, 2 * renderer.perspFocus[renderer.eye]
				/ renderer.getVisibleDepth());
		projectionMatrix.set(4, 3, -1);

		projectionMatrix.set(1, 4, 0);// (perspRight+perspLeft)/(perspRight-perspLeft)
										// * perspFocus);
		projectionMatrix.set(2, 4, 0);// (perspTop+perspBottom)/(perspTop-perspBottom)
										// * perspFocus);
		projectionMatrix.set(3, 4, renderer.getVisibleDepth() / 2);
		projectionMatrix.set(4, 4, -renderer.perspFocus[renderer.eye]);

	}

	protected double perspXZ, glassesXZ;

	@Override
	public void updateGlassesValues() {
		glassesXZ = (renderer.perspNear[renderer.eye]
				* (renderer.glassesEyeX[Renderer.EYE_LEFT] - renderer.glassesEyeX[Renderer.EYE_RIGHT]) / renderer.perspFocus[renderer.eye])
				/ (renderer.perspRight[renderer.eye] - renderer.perspLeft[renderer.eye]);
	}

	@Override
	public void viewGlasses() {

		if (renderer.eye == Renderer.EYE_LEFT) {
			projectionMatrix.set(1, 3, perspXZ + glassesXZ);
		} else {
			projectionMatrix.set(1, 3, perspXZ - glassesXZ);
		}

	}

	@Override
	public void viewOblique() {
		// the projection matrix is updated in updateProjectionObliqueValues()
	}

	@Override
	public void updateProjectionObliqueValues() {

		projectionMatrix.set(1, 1, 2.0 / renderer.getWidth());
		projectionMatrix.set(2, 1, 0);
		projectionMatrix.set(3, 1, 0);
		projectionMatrix.set(4, 1, 0);

		projectionMatrix.set(1, 2, 0);
		projectionMatrix.set(2, 2, 2.0 / renderer.getHeight());
		projectionMatrix.set(3, 2, 0);
		projectionMatrix.set(4, 2, 0);

		projectionMatrix.set(1, 3,
				renderer.obliqueX * 2.0 / renderer.getWidth());
		projectionMatrix.set(2, 3,
				renderer.obliqueY * 2.0 / renderer.getHeight());
		projectionMatrix.set(3, 3, -2.0 / renderer.getVisibleDepth());
		projectionMatrix.set(4, 3, 0);

		projectionMatrix.set(1, 4, 0);
		projectionMatrix.set(2, 4, 0);
		projectionMatrix.set(3, 4, 0);
		projectionMatrix.set(4, 4, 1);

	}
	



	private float[] clipPlanesMin = new float[3];
	private float[] clipPlanesMax = new float[3];

	@Override
	public void setClipPlanes(double[][] minMax) {
		for (int i = 0; i < 3; i++) {
			clipPlanesMin[i] = (float) minMax[i][0];
			clipPlanesMax[i] = (float) minMax[i][1];
		}

	}

	final private void setClipPlanesToShader() {

		glUniform3fv(clipPlanesMinLocation, clipPlanesMin);
		glUniform3fv(clipPlanesMaxLocation, clipPlanesMax);

	}



	@Override
	public void initRenderingValues() {

		// clip planes
		setClipPlanesToShader();
	}

	@Override
	public void drawFaceToScreenAbove() {
		glUniform1i(labelRenderingLocation, 1);
		resetCenter();
	}

	@Override
	public void drawFaceToScreenBelow() {
		glUniform1i(labelRenderingLocation, 0);
	}

	@Override
	public void setLabelOrigin(Coords origin) {
		glUniform3fv(labelOriginLocation, origin.get3ForGL());
	}

	@Override
	public void enableLighting() {
		if (view3D.getUseLight()) {
			glUniform1i(enableLightLocation, 1);
		}
	}

	@Override
	public void initLighting() {
		if (view3D.getUseLight()) {
			glUniform1i(enableLightLocation, 1);
		} else {
			glUniform1i(enableLightLocation, 0);
		}
		if (view3D.getApplication().has(Feature.SHINY_3D)) {
			glUniform1i(enableShineLocation, 0);
		}
	}

	@Override
	public void disableLighting() {
		if (view3D.getUseLight()) {
			glUniform1i(enableLightLocation, 0);
		}
	}

	@Override
	public void disableShine() {
		if (view3D.getApplication().has(Feature.SHINY_3D)) {
			if (view3D.getUseLight()) {
				glUniform1i(enableShineLocation, 0);
			}
		}
	}

	@Override
	public void enableShine() {
		if (view3D.getApplication().has(Feature.SHINY_3D)) {
			if (view3D.getUseLight()) {
				glUniform1i(enableShineLocation, 1);
			}
		}
	}

	@Override
	final public void setCenter(Coords center) {
		float[] c = center.get4ForGL();
		// set radius info
		c[3] *= DrawPoint3D.DRAW_POINT_FACTOR / view3D.getScale();
		glUniform4fv(centerLocation, c);
	}

	private float[] resetCenter = { 0f, 0f, 0f, 0f };

	@Override
	final public void resetCenter() {
		glUniform4fv(centerLocation, resetCenter);
	}

	@Override
	final public void disableCulling() {
		glDisable(getGL_CULL_FACE());
		glUniform1i(cullingLocation, 1);
	}

	abstract protected void glCullFace(int flag);

	abstract protected int getGL_FRONT();

	abstract protected int getGL_BACK();

	@Override
	final public void setCullFaceFront() {
		glCullFace(getGL_FRONT());
		glUniform1i(cullingLocation, -1);
	}

	@Override
	final public void setCullFaceBack() {
		glCullFace(getGL_BACK());
		glUniform1i(cullingLocation, 1);
	}

	@Override
	public void drawTranspNotCurved() {
		renderer.enableCulling();
		renderer.setCullFaceFront();
		renderer.drawable3DLists.drawTransp(renderer);
		renderer.drawable3DLists.drawTranspClosedNotCurved(renderer);
		renderer.setCullFaceBack();
		renderer.drawable3DLists.drawTransp(renderer);
		renderer.drawable3DLists.drawTranspClosedNotCurved(renderer);

	}

	@Override
	public void enableLightingOnInit() {
		// no need for shaders
	}

	@Override
	public void initCulling() {
		// no need for shaders
	}

	@Override
	public boolean useShaders() {
		return true;
	}

	abstract protected void glDepthMask(boolean flag);

	@Override
	final public void enableDepthMask() {
		glDepthMask(true);
	}

	@Override
	final public void disableDepthMask() {
		glDepthMask(false);
	}

	@Override
	public void setStencilLines() {
		// not implemented yet with shaders
	}

	abstract protected Object glGetUniformLocation(String name);

	/**
	 * set uniform locations for shaders
	 */
	final protected void setShaderLocations() {
		matrixLocation = glGetUniformLocation("matrix");
		lightPositionLocation = glGetUniformLocation("lightPosition");
		ambiantDiffuseLocation = glGetUniformLocation("ambiantDiffuse");
		eyePositionLocation = glGetUniformLocation("eyePosition");
		enableLightLocation = glGetUniformLocation("enableLight");
		if (view3D.getApplication().has(Feature.SHINY_3D)) {
			enableShineLocation = glGetUniformLocation("enableShine");
		}

		cullingLocation = glGetUniformLocation("culling");

		dashValuesLocation = glGetUniformLocation("dashValues");

		// texture
		textureTypeLocation = glGetUniformLocation("textureType");

		// color
		colorLocation = glGetUniformLocation("color");

		// normal
		normalLocation = glGetUniformLocation("normal");

		// center
		centerLocation = glGetUniformLocation("center");

		// clip planes
		enableClipPlanesLocation = glGetUniformLocation("enableClipPlanes");
		clipPlanesMinLocation = glGetUniformLocation("clipPlanesMin");
		clipPlanesMaxLocation = glGetUniformLocation("clipPlanesMax");

		// label rendering
		labelRenderingLocation = glGetUniformLocation("labelRendering");
		labelOriginLocation = glGetUniformLocation("labelOrigin");
	}

	@Override
	final public void initShaders() {
		compileShadersProgram();

		// Each shaderProgram must have
		// one vertex shader and one fragment shader.
		shaderProgram = glCreateProgram();
		glAttachShader(vertShader);
		glAttachShader(fragShader);

		setPredefinedAttributes();

		glBindAttribLocation(GLSL_ATTRIB_POSITION, "attribute_Position");
		glBindAttribLocation(GLSL_ATTRIB_NORMAL, "attribute_Normal");
		glBindAttribLocation(GLSL_ATTRIB_COLOR, "attribute_Color");
		glBindAttribLocation(GLSL_ATTRIB_TEXTURE, "attribute_Texture");

		glLinkProgram();

		setShaderLocations();
		createVBOs();
		attribPointers();
	}

	final protected void setPredefinedAttributes() {
		// Associate attribute ids with the attribute names inside
		// the vertex shader.
		GLSL_ATTRIB_POSITION = 0;
		GLSL_ATTRIB_COLOR = 1;
		GLSL_ATTRIB_NORMAL = 2;
		GLSL_ATTRIB_TEXTURE = 3;
		GLSL_ATTRIB_INDEX = 4;
	}

	abstract protected void compileShadersProgram();

	abstract protected Object glCreateProgram();

	abstract protected void glAttachShader(Object shader);

	abstract protected void glBindAttribLocation(int index, String name);

	abstract protected void glLinkProgram();

	abstract protected void createVBOs();

	@Override
	public void enableAlphaTest() {
		// done by shader
	}

	@Override
	public void disableAlphaTest() {
		// done by shader
	}


}
