package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.Stack;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;

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

	protected Integer normalLocation; // one normal for all vertices
	protected Integer textureTypeLocation; // textures
	protected Integer dashValuesLocation; // values for dash

	protected float[] tmpNormal3 = new float[3];

	protected boolean oneNormalForAllVertices;

	protected EuclidianView3D view3D;

	protected Renderer renderer;

	public RendererImplShaders(Renderer renderer, EuclidianView3D view) {
		this.renderer = renderer;
		this.view3D = view;
	}

	protected abstract void createBuffer(GPUBuffer buffer, Stack<Integer> stack);

	private Stack<Integer> removedBuffers = new Stack<Integer>();
	private Stack<Integer> removedElementBuffers = new Stack<Integer>();

	final public void createArrayBuffer(GPUBuffer buffer) {
		createBuffer(buffer, removedBuffers);
	}

	final public void createElementBuffer(GPUBuffer buffer) {
		createBuffer(buffer, removedElementBuffers);
	}

	abstract protected void removeBuffer(GPUBuffer buffer, Stack<Integer> stack);

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
	 * enable vertex attribute
	 * 
	 * @param attrib
	 *            attribute
	 */
	abstract protected void enableAttrib(int attrib);

	/**
	 * disable vertex attribute
	 * 
	 * @param attrib
	 *            attribute
	 */
	abstract protected void disableAttrib(int attrib);

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
		vertexAttribPointer(GLSL_ATTRIB_POSITION, 3);

		// enable VBO
		enableAttrib(GLSL_ATTRIB_POSITION);
	}

	@Override
	final public void bindBufferForColors(GPUBuffer buffer, int size,
			GLBuffer fbColors) {
		if (fbColors == null || fbColors.isEmpty()) {
			disableAttrib(GLSL_ATTRIB_COLOR);
			return;
		}

		// prevent use of global color
		setColor(-1, -1, -1, -1);

		// Select the VBO, GPU memory data, to use for normals
		bindBuffer(buffer);

		// Associate Vertex attribute 1 with the last bound VBO
		vertexAttribPointer(GLSL_ATTRIB_COLOR, 4);

		enableAttrib(GLSL_ATTRIB_COLOR);
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
			disableAttrib(GLSL_ATTRIB_NORMAL);
			return;
		}

		if (fbNormals.capacity() == 3) { // one normal for all vertices
			fbNormals.array(tmpNormal3);
			glUniform3fv(normalLocation, tmpNormal3);
			oneNormalForAllVertices = true;
			disableAttrib(GLSL_ATTRIB_NORMAL);
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
		vertexAttribPointer(GLSL_ATTRIB_NORMAL, 3);

		enableAttrib(GLSL_ATTRIB_NORMAL);
	}

	@Override
	public void bindBufferForTextures(GPUBuffer buffer, int size,
			GLBuffer fbTextures) {
		if (fbTextures == null || fbTextures.isEmpty()) {
			setCurrentGeometryHasNoTexture();
			disableAttrib(GLSL_ATTRIB_TEXTURE);
			return;
		}

		setCurrentGeometryHasTexture();

		// Select the VBO, GPU memory data, to use for normals
		bindBuffer(buffer);

		// Associate Vertex attribute 1 with the last bound VBO
		vertexAttribPointer(GLSL_ATTRIB_TEXTURE, 2);

		enableAttrib(GLSL_ATTRIB_TEXTURE);
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

}
