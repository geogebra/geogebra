package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.geogebra3D.euclidian3D.xr.XRManagerInterface;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;

/**
 * implementation for renderer using shaders
 * 
 * @author mathieu
 *
 */
public abstract class RendererImplShaders extends RendererImpl {

	final static public int GLSL_ATTRIB_POSITION = 0;
	final static public int GLSL_ATTRIB_COLOR = 1;
	final static public int GLSL_ATTRIB_NORMAL = 2;
	final static public int GLSL_ATTRIB_TEXTURE = 3;
	final static public int GLSL_ATTRIB_INDEX = 4;
	final static public int GLSL_ATTRIB_SIZE = 5;

	final static protected int TEXTURE_TYPE_NONE = 0;
	final static public int TEXTURE_TYPE_FADING = 1;
	final static public int TEXTURE_TYPE_TEXT = 2;
	final static public int TEXTURE_TYPE_DASH = 4;

	// location values for shader fields
	protected Object matrixLocation; // matrix
	// light
	protected Object lightPositionLocation;
	protected Object ambiantDiffuseLocation;
	protected Object enableLightLocation;
	protected Object enableShineLocation;
	/** eye position */
	protected Object eyePositionLocation;
	/** culling type */
	protected Object cullingLocation;
	protected Object colorLocation; // color
	// enable / disable clip planes
	protected Object enableClipPlanesLocation;
	protected Object clipPlanesMinLocation;
	protected Object clipPlanesMaxLocation;

	protected Object labelRenderingLocation;
	protected Object labelOriginLocation;
	protected Object normalLocation; // one normal for all vertices
	protected Object textureTypeLocation; // textures
	protected Object dashValuesLocation; // values for dash
	protected Object layerLocation; // layer value

	protected Object opaqueSurfacesLocation; // special drawing for opaque
												// surfaces

	protected float[] tmpNormal3 = new float[3];

	protected CoordMatrix4x4 projectionMatrix = new CoordMatrix4x4();

	protected CoordMatrix4x4 tmpMatrix1 = new CoordMatrix4x4();
	protected CoordMatrix4x4 tmpMatrix2 = new CoordMatrix4x4();

	protected float[] tmpFloat16 = new float[16];

	protected boolean oneNormalForAllVertices;

	protected Object shaderProgram;
	private Object vertShader;

	private Object fragShader;

	protected boolean texturesEnabled;

	private int currentDash = Textures.DASH_INIT;

	private int currentTextureType = TEXTURE_TYPE_NONE;
	private int oldTextureType = TEXTURE_TYPE_NONE;

	private double perspXZ;
	private double perspYZ;
	private double[] glassesXZ = new double[2];
	private double[] glassesYZ = new double[2];

	private float[] clipPlanesMin = new float[3];
	private float[] clipPlanesMax = new float[3];

	private int currentLayer;
	private float[] eyeOrDirection = new float[4];
	protected float[][] ambiantDiffuse;

	/**
	 * dash values for shaders
	 */
	private static final float[][] DASH_SHADERS_VALUES = {
			// coeff, a, b, c
			// in shaders : x = mod(dashValues[0] * coordTexture.x, 1.0)
			// if (x > a || (x > b && x <= c)) then discard
			{ 2.0f, 0.5f, -1f, -1f }, // {true, false, true, false}, //
										// DASH_SHORT
			{ 1.0f, 0.25f, -1f, -1f }, // {true, false, false, false}, //
										// DASH_LONG_HIDDEN
			{ 4.0f, 0.5f, -1f, -1f }, // {true, false, true, false, true, false,
										// true, false}, // DASH_DOTTED
			{ 2.0f, 0.25f, -1f, -1f }, // {true, false, false, false, true,
										// false, false, false}, //
										// DASH_DOTTED_HIDDEN
			{ 1.0f, 0.5f, -1f, -1f }, // {true, true, false, false}, //
										// DASH_NONE_HIDDEN
			{ 1.0f, 0.25f, -1f, -1f }, // {true, false, false, false}, //
										// DASH_SHORT_HIDDEN
			{ 1.0f, 0.5f, -1f, -1f }, // {true, true, false, false}, //
										// DASH_LONG
			{ 1.0f, 12f / 16f, 7f / 16f, 11f / 16f }, // {true,true,true,true,
														// true,true,true,false,
														// false,false,false,true,
														// false,false,false,false},
														// // DASH_DOTTED_DASHED
			{ 1.0f, 12f / 16f, 3f / 16f, 11f / 16f }, // {false,false,true,true,
														// true,false,false,false,
														// false,false,false,true,
														// false,false,false,false}
														// //
														// DASH_DOTTED_DASHED_HIDDEN

	};

	public RendererImplShaders(Renderer renderer, EuclidianView3D view) {
		super(renderer, view);
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
	final public void bindBufferForIndices(int buffer) {
		bindBuffer(getGL_ELEMENT_ARRAY_BUFFER(), buffer);
	}

	final protected void bindBuffer(int buffer) {
		bindBuffer(getGL_ARRAY_BUFFER(), buffer);
	}

	abstract protected void bindBuffer(int bufferType, int buffer);

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
	
	abstract protected void glUniform3fv(Object location, float[] values);

	abstract protected void glUniform3f(Object location, float x, float y,
			float z);

	protected void setVertShader(Object vertShader) {
		this.vertShader = vertShader;
	}

	protected void setFragShader(Object fragShader) {
		this.fragShader = fragShader;
	}

	protected void resetOneNormalForAllVertices() {
		oneNormalForAllVertices = false;
		glUniform3f(normalLocation, 2, 2, 2);
	}

	@Override
	public void setNormalToNone() {
        oneNormalForAllVertices = true;
        glUniform3f(normalLocation, -2, -2, -2);
    }

	@Override
	public void enableTextures() {
		texturesEnabled = true;
		setCurrentGeometryHasNoTexture(); // let first geometry init textures
	}

	@Override
	public void disableTextures() {
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

	@Override
	public void enableDash() {
		currentDash = Textures.DASH_INIT;
		enableTextures();
		setCurrentTextureType(TEXTURE_TYPE_DASH);
	}

	@Override
	public void enableDashHidden() {
		enableDash();
	}

	/**
	 * enable text textures
	 */
	@Override
	final public void enableTexturesForText() {
		setCurrentTextureType(TEXTURE_TYPE_TEXT);
	}

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
			if (index <= DASH_SHADERS_VALUES.length) {
				glUniform1fv(dashValuesLocation, 4, DASH_SHADERS_VALUES[index - 1]);
			}
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
		bindBuffer(GLSL_ATTRIB_POSITION);

		// transfer data to VBO, this perform the copy of data from CPU -> GPU
		// memory
		int numBytes = length * 12; // 4 bytes per float * 3 coords per vertex
		glBufferData(numBytes, fbVertices);

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
		bindBuffer(GLSL_ATTRIB_COLOR);
		int numBytes = length * 16; // 4 bytes per float * 4 color values (rgba)
		glBufferData(numBytes, fbColors);

		glEnableVertexAttribArray(GLSL_ATTRIB_COLOR);
	}

	@Override
	public void loadTextureBuffer(GLBuffer fbTextures, int length) {
		if (fbTextures == null || fbTextures.isEmpty()) {
			disableTextureBuffer();
			return;
		}

		setCurrentGeometryHasTexture();

		// Select the VBO, GPU memory data, to use for normals
		bindBuffer(GLSL_ATTRIB_TEXTURE);
		int numBytes = length * 8; // 4 bytes per float * 2 coords per texture
		glBufferData(numBytes, fbTextures);

		glEnableVertexAttribArray(GLSL_ATTRIB_TEXTURE);
	}

	@Override
	public void disableTextureBuffer() {
		setCurrentGeometryHasNoTexture();
		glDisableVertexAttribArray(GLSL_ATTRIB_TEXTURE);
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
		bindBuffer(GLSL_ATTRIB_NORMAL);
		int numBytes = length * 12; // 4 bytes per float * * 3 coords per normal
		glBufferData(numBytes, fbNormals);

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
		bindBufferForIndices(GLSL_ATTRIB_INDEX);

		// transfer data to VBO, this perform the copy of data from CPU -> GPU
		// memory
		glBufferDataIndices(length * 2, arrayI);

	}

	abstract protected void glBufferDataIndices(int numBytes,
			GLBufferIndices arrayI);

	/**
	 * attribute vertex pointers
	 */
	@Override
	public void attribPointers() {

		bindBuffer(GLSL_ATTRIB_POSITION);
		vertexAttribPointer(GLSL_ATTRIB_POSITION, 3);

		bindBuffer(GLSL_ATTRIB_NORMAL);
		vertexAttribPointer(GLSL_ATTRIB_NORMAL, 3);

		bindBuffer(GLSL_ATTRIB_COLOR);
		vertexAttribPointer(GLSL_ATTRIB_COLOR, 4);

		bindBuffer(GLSL_ATTRIB_TEXTURE);
		vertexAttribPointer(GLSL_ATTRIB_TEXTURE, 2);
	}

	abstract protected int getGLType(Type type);

	protected final void setModelViewIdentity() {
		projectionMatrix.getForGL(tmpFloat16);
		glCopyToMatrixLocation(tmpFloat16);
	}

	/**
	 * copy values to GL matrix location
	 * 
	 * @param values
	 *            values
	 */
	protected void glCopyToMatrixLocation(float[] values) {
		glUniformMatrix4fv(matrixLocation, values);
	}

	abstract protected void glUniformMatrix4fv(Object location, float[] values);

	@Override
	public void draw() {
		resetOneNormalForAllVertices();
		disableTextures();

		setModelViewIdentity();

		disableOpaqueSurfaces();
	}

	@Override
	public void useShaderProgram() {
		glUseProgram(shaderProgram);
	}

	abstract protected void glUseProgram(Object program);

	@Override
	public void dispose() {
		glResetProgram();
		glDetachAndDeleteShader(shaderProgram, vertShader);
		glDetachAndDeleteShader(shaderProgram, fragShader);
		glDeleteProgram(shaderProgram);
	}

	/**
	 * Reset program to 0
	 */
	protected abstract void glResetProgram();

	abstract protected void glDetachAndDeleteShader(Object program,
			Object shader);

	abstract protected void glDeleteProgram(Object program);

	@Override
	public void setMatrixView(CoordMatrix4x4 matrix) {
		tmpMatrix1.setMul(projectionMatrix, matrix);
		tmpMatrix1.getForGL(tmpFloat16);
		glCopyToMatrixLocation(tmpFloat16);
	}

	@Override
	public void setProjectionMatrixViewForAR() {
		XRManagerInterface<?> arManager = renderer.getXRManager();
		if (arManager != null) {
			arManager.setProjectionMatrixViewForXR(projectionMatrix);
		}
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
		tmpMatrix2.setMul(renderer.getToScreenMatrix(), renderer.getMatrix());
		tmpMatrix1.setMul(projectionMatrix, tmpMatrix2);
		tmpMatrix1.getForGL(tmpFloat16);

		glCopyToMatrixLocation(tmpFloat16);
	}

	@Override
	public void initMatrixForFaceToScreen() {
		tmpMatrix1.setMul(projectionMatrix, renderer.getMatrix());
		tmpMatrix1.getForGL(tmpFloat16);

		glCopyToMatrixLocation(tmpFloat16);
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
	public void setLightPosition(float[] values) {
		glUniform3fv(lightPositionLocation, values);
		view3D.getEyePosition().get4ForGL(eyeOrDirection);
		if (!view3D.hasParallelProjection()) {
			eyeOrDirection[0] *= view3D.getXscale();
			eyeOrDirection[1] *= view3D.getYscale();
			eyeOrDirection[2] *= view3D.getZscale();
		}
		glUniform4fv(eyePositionLocation, eyeOrDirection);
	}

	abstract protected void glUniform4fv(Object location, float[] values);

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
	final public void setView() {
		renderer.setProjectionMatrix();
        glViewPort();
	}

	@Override
	public void glViewPort() {
        glViewPort(renderer.getWidthInPixels(), renderer.getHeightInPixels());
    }

	abstract protected void glViewPort(int width, int height);

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

		// usually perspXZ and perspYZ are equal to 0 since left = -right and
		// bottom = -top
		perspXZ = (renderer.getRight() + renderer.getLeft())
				/ (renderer.eyeToScreenDistance[renderer.eye]
						* renderer.getWidth());
		perspYZ = (renderer.getTop() + renderer.getBottom())
				/ (renderer.eyeToScreenDistance[renderer.eye]
						* renderer.getHeight());

		// X row
		projectionMatrix.set(1, 1, 2.0 / renderer.getWidth());
		projectionMatrix.set(1, 2, 0);
		projectionMatrix.set(1, 3, perspXZ);
		projectionMatrix.set(1, 4, 0);

		// Y row
		projectionMatrix.set(2, 1, 0);
		projectionMatrix.set(2, 2, 2.0 / renderer.getHeight());
		projectionMatrix.set(2, 3, perspYZ);
		projectionMatrix.set(2, 4, 0);

		// Z row
		double a;
		double b;
		final double dd = renderer.getVisibleDepth() / 2.0;
		final double eye = renderer.getEyeToScreenDistance();
		final double k = 0.9;
		if (dd > k * eye) {
			// eye is too close
			// z goes from -visibleDepth / 2 (far) to 90% eye distance (near)
			a = (eye * k - dd - 2 * eye) / ((dd + eye * k) * eye);
			b = (eye * k - dd + 2 * dd * k) / (dd + eye * k);
		} else {
			// z goes from -visibleDepth / 2 (far) to visibleDepth / 2 (near)
			a = -1 / dd;
			b = dd / eye;
		}
		projectionMatrix.set(3, 1, 0);
		projectionMatrix.set(3, 2, 0);
		projectionMatrix.set(3, 3, a);
		projectionMatrix.set(3, 4, b);

		// W row
		projectionMatrix.set(4, 1, 0);
		projectionMatrix.set(4, 2, 0);
		projectionMatrix.set(4, 3,
				-1.0 / renderer.eyeToScreenDistance[renderer.eye]);
		projectionMatrix.set(4, 4, 1);

	}

	@Override
	public void updateGlassesValues() {
		for (int i = 0; i < 2; i++) {
			glassesXZ[i] = -2.0 * renderer.glassesEyeX[i]
					/ (renderer.eyeToScreenDistance[i] * renderer.getWidth());
			glassesYZ[i] = -2.0 * renderer.glassesEyeY[i]
					/ (renderer.eyeToScreenDistance[i] * renderer.getHeight());
		}
	}

	@Override
	public void viewGlasses() {
		projectionMatrix.set(1, 3, perspXZ + glassesXZ[renderer.eye]);
		projectionMatrix.set(2, 3, perspYZ + glassesYZ[renderer.eye]);
	}

	@Override
	public void viewOblique() {
		// the projection matrix is updated in updateProjectionObliqueValues()
	}

	@Override
	public void updateProjectionObliqueValues() {
		projectionMatrix.set(1, 1, 2.0 / renderer.getWidth());
		projectionMatrix.set(1, 2, 0);
		projectionMatrix.set(1, 3,
				renderer.obliqueX * 2.0 / renderer.getWidth());
		projectionMatrix.set(1, 4, 0);

		projectionMatrix.set(2, 1, 0);
		projectionMatrix.set(2, 2, 2.0 / renderer.getHeight());
		projectionMatrix.set(2, 3,
				renderer.obliqueY * 2.0 / renderer.getHeight());
		projectionMatrix.set(2, 4, 0);

		projectionMatrix.set(3, 1, 0);
		projectionMatrix.set(3, 2, 0);
		projectionMatrix.set(3, 3, -2.0 / renderer.getVisibleDepth());
		projectionMatrix.set(3, 4, 0);

		projectionMatrix.set(4, 1, 0);
		projectionMatrix.set(4, 2, 0);
		projectionMatrix.set(4, 3, 0);
		projectionMatrix.set(4, 4, 1);

	}

	@Override
	public void setClipPlanes(double[][] minMax) {
		for (int i = 0; i < 3; i++) {
			double scale = view3D.getScale(i);
			setClipValues(i, minMax[i][0] * scale, minMax[i][1] * scale);
		}
	}

	/**
	 * 
	 * @param index
	 *            index for x/y/z
	 * @param min
	 *            min
	 * @param max
	 *            max
	 */
	protected void setClipValues(int index, double min, double max) {
		clipPlanesMin[index] = (float) min;
		clipPlanesMax[index] = (float) max;
	}

	@Override
	final protected void updateClipPlanes() {
		glUniform3fv(clipPlanesMinLocation, clipPlanesMin);
		glUniform3fv(clipPlanesMaxLocation, clipPlanesMax);
	}

	@Override
	public void initRenderingValues() {
		disableOpaqueSurfaces();
		updateClipPlanes();
		initLayer();
	}

	@Override
	public void drawFaceToScreenAbove() {
	    setNormalToNone();
		glUniform1i(labelRenderingLocation, 1);
	}

	@Override
	public void drawFaceToScreenBelow() {
		glUniform1i(labelRenderingLocation, 0);
	}

	@Override
	public void setLabelOrigin(float[] origin) {
		glUniform3fv(labelOriginLocation, origin);
	}

	@Override
	public void enableLighting() {
		if (view3D.getUseLight()) {
			enableLightingNoCheck();
		}
	}

	/**
	 * enable lighting without checking if light is used
	 */
	protected void enableLightingNoCheck() {
		glUniform1i(enableLightLocation, 1);
	}

	@Override
	public void initLighting() {
		if (view3D.getUseLight()) {
			enableLightingNoCheck();
		} else {
			disableLightingNoCheck();
		}

		disableShineNoCheck();
	}

	/**
	 * disable lighting without checking if light is used
	 */
	protected void disableLightingNoCheck() {
		glUniform1i(enableLightLocation, 0);
	}

	@Override
	public void disableLighting() {
		if (view3D.getUseLight()) {
			disableLightingNoCheck();
		}
	}

	/**
	 * disable shining without checking if light is used
	 */
	protected void disableShineNoCheck() {
		glUniform1i(enableShineLocation, 0);
	}

	/**
	 * enable shining without checking if light is used
	 */
	protected void enableShineNoCheck() {
		glUniform1i(enableShineLocation, 1);
	}

	@Override
	public void disableShine() {
		if (view3D.getUseLight()) {
			disableShineNoCheck();
		}
	}

	@Override
	public void enableShine() {
		if (view3D.getUseLight()) {
			enableShineNoCheck();
		}
	}

	/**
	 * disables flag for opaque surfaces
	 */
	protected void disableOpaqueSurfaces() {
		glUniform1i(opaqueSurfacesLocation, 0);
	}

	/**
	 * enables flag for opaque surfaces
	 */
	protected void enableOpaqueSurfaces() {
		glUniform1i(opaqueSurfacesLocation, 1);
	}

	@Override
	public void disableCulling() {
		glDisable(getGL_CULL_FACE());
		glUniform1i(cullingLocation, 1);
	}

	abstract protected void glCullFace(int flag);

	abstract protected int getGL_FRONT();

	abstract protected int getGL_BACK();

	@Override
	public void setCullFaceFront() {
		glCullFace(getGL_FRONT());
		glUniform1i(cullingLocation, -1);
	}

	@Override
	public void setCullFaceBack() {
		glCullFace(getGL_BACK());
		glUniform1i(cullingLocation, 1);
	}

	@Override
	public void drawTranspNotCurved() {
		renderer.enableCulling();
		setCullFaceFront();
		drawTransp();
		setCullFaceBack();
		drawTransp();
	}

	private void drawTransp() {
		((ManagerShaders) renderer.getGeometryManager()).drawSurfaces(renderer);
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
		cullingLocation = glGetUniformLocation("culling");
		enableShineLocation = glGetUniformLocation("enableShine");

		dashValuesLocation = glGetUniformLocation("dashValues");

		// texture
		textureTypeLocation = glGetUniformLocation("textureType");

		// color
		colorLocation = glGetUniformLocation("color");

		// normal
		normalLocation = glGetUniformLocation("normal");

		// clip planes
		enableClipPlanesLocation = glGetUniformLocation("enableClipPlanes");
		clipPlanesMinLocation = glGetUniformLocation("clipPlanesMin");
		clipPlanesMaxLocation = glGetUniformLocation("clipPlanesMax");

		// label rendering
		labelRenderingLocation = glGetUniformLocation("labelRendering");
		labelOriginLocation = glGetUniformLocation("labelOrigin");
		
		// layer
		layerLocation = glGetUniformLocation("layer");

		// opaque surfaces special drawing
		opaqueSurfacesLocation = glGetUniformLocation("opaqueSurfaces");
	}

	@Override
	public void initShaders() {
		compileShadersProgram();

		// Each shaderProgram must have
		// one vertex shader and one fragment shader.
		shaderProgram = glCreateProgram();
		if (shaderProgram == null) {
			return;
		}
		glAttachShader(vertShader);
		glAttachShader(fragShader);

		glBindAttribLocation(GLSL_ATTRIB_POSITION, "attribute_Position");
		glBindAttribLocation(GLSL_ATTRIB_NORMAL, "attribute_Normal");
		glBindAttribLocation(GLSL_ATTRIB_COLOR, "attribute_Color");
		glBindAttribLocation(GLSL_ATTRIB_TEXTURE, "attribute_Texture");

		glLinkProgram();

		setShaderLocations();
		createVBOs();
		attribPointers();
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

	@Override
	public void setLayer(int layer) {
		if (layer == currentLayer) {
			return;
		}
		currentLayer = layer;
		glUniform1i(layerLocation, currentLayer);
	}

	final private void initLayer() {
		currentLayer = 0;
		glUniform1i(layerLocation, 0);
	}

	@Override
	public Manager createManager() {
		return new ManagerShaders(renderer, view3D);
	}

	@Override
	public void drawNotHidden() {
		ManagerShaders manager = (ManagerShaders) renderer.getGeometryManager();
		manager.drawCurves(renderer, false);
		manager.drawCurvesClipped(renderer, false);
	}

	@Override
	public void drawHiddenTextured() {
		ManagerShaders manager = (ManagerShaders) renderer.getGeometryManager();
		manager.drawCurves(renderer, true);
		manager.drawCurvesClipped(renderer, true);
	}

	@Override
	public void drawHiddenNotTextured() {
		((ManagerShaders) renderer.getGeometryManager()).drawPoints(renderer);
	}

	@Override
	public void drawTranspClosedCurved() {
		((ManagerShaders) renderer.getGeometryManager())
				.drawSurfacesClosed(renderer);
	}

	@Override
	public void drawClosedSurfacesForHiding() {
		((ManagerShaders) renderer.getGeometryManager())
				.drawSurfacesClosed(renderer);
	}

	@Override
	public void drawClippedSurfacesForHiding() {
		((ManagerShaders) renderer.getGeometryManager())
				.drawSurfacesClipped(renderer);
	}

	@Override
	public void drawTranspClipped() {
		((ManagerShaders) renderer.getGeometryManager())
				.drawSurfacesClipped(renderer);
	}

	@Override
	public void drawSurfacesForHiding() {
		((ManagerShaders) renderer.getGeometryManager()).drawSurfaces(renderer);
	}

	@Override
	public void drawOpaqueSurfaces() {
		setLight(1);
		enableOpaqueSurfaces();
		disableCulling();
		ManagerShaders manager = (ManagerShaders) renderer.getGeometryManager();
		manager.drawSurfaces(renderer);
		manager.drawSurfacesClosed(renderer);
		renderer.enableClipPlanesIfNeeded();
		manager.drawSurfacesClipped(renderer);
		renderer.disableClipPlanesIfNeeded();
		renderer.enableCulling();
		disableOpaqueSurfaces();
		setLight(0);
	}

}
