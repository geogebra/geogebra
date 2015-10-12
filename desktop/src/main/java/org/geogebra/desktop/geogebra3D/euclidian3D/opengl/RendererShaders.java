package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Stack;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D.IntersectionCurve;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GPUBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShadersWithTemplates;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.RendererShadersInterface;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Textures;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererJogl.GL2ES2;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererJogl.GLlocal;
import org.geogebra.desktop.main.AppD;

/**
 * Renderer using shaders
 * 
 * @author mathieu
 *
 */
public class RendererShaders extends RendererD implements
		RendererShadersInterface {

	final static public int GLSL_ATTRIB_POSITION = 0;
	final static public int GLSL_ATTRIB_COLOR = 1;
	final static public int GLSL_ATTRIB_NORMAL = 2;
	final static public int GLSL_ATTRIB_TEXTURE = 3;
	final static public int GLSL_ATTRIB_INDEX = 4;

	/**
	 * constructor
	 * 
	 * @param view
	 *            3D view
	 * @param useCanvas
	 *            says if we want a canvas
	 */
	public RendererShaders(EuclidianView3D view, boolean useCanvas) {
		super(view, useCanvas);
		hitting = new Hitting(view3D);

		App.debug("Renderer with shaders created");
	}

	@Override
	public GL getGL() {

		return jogl.getGL2ES2();
	}

	private int shaderProgram;
	private int vertShader;
	private int fragShader;

	// location values for shader fields
	private int matrixLocation; // matrix
	private int lightPositionLocation, ambiantDiffuseLocation,
			enableLightLocation; // light
	private int eyePositionLocation; // eye position
	private int cullingLocation; // culling type
	private int dashValuesLocation; // values for dash
	private int textureTypeLocation; // textures
	private int colorLocation; // color
	protected int normalLocation; // one normal for all vertices
	private int centerLocation; // center
	private int enableClipPlanesLocation, clipPlanesMinLocation,
			clipPlanesMaxLocation; // enable / disable clip planes
	private int labelRenderingLocation, labelOriginLocation;
	// private int normalMatrixLocation;

	final static private int TEXTURE_TYPE_NONE = 0;
	final static private int TEXTURE_TYPE_FADING = 1;
	final static private int TEXTURE_TYPE_TEXT = 2;
	final static private int TEXTURE_TYPE_DASH = 4;

	private int[] vboHandles;
	protected int vboVertices;
	protected int vboColors;
	protected int vboNormals;
	protected int vboTextureCoords;
	protected int vboIndices;

	private String loadTextFile(String file) {

		return ((AppD) view3D.getApplication())
				.loadTextFile("/org/geogebra/desktop/geogebra3D/euclidian3D/opengl/shaders/" + file
						+ ".txt");
	}

	@Override
	protected void initShaders() {

		/*
		 * The initialization below will use the OpenGL ES 2 API directly to
		 * setup the two shader programs that will be run on the GPU.
		 * 
		 * Its recommended to use the jogamp/opengl/util/glsl/ classes import
		 * com.jogamp.opengl.util.glsl.ShaderCode; import
		 * com.jogamp.opengl.util.glsl.ShaderProgram; import
		 * com.jogamp.opengl.util.glsl.ShaderState; to simplify shader
		 * customization, compile and loading.
		 * 
		 * You may also want to look at the JOGL RedSquareES2 demo
		 * http://jogamp.
		 * org/git/?p=jogl.git;a=blob;f=src/test/com/jogamp/opengl/
		 * test/junit/jogl/demos/es2/RedSquareES2.java;hb=HEAD#l78 to see how
		 * the shader customization, compile and loading is done using the
		 * recommended JogAmp GLSL utility classes.
		 */

		String vertexShaderString, fragmentShaderString;

		vertexShaderString = loadTextFile("vertexShader");
		fragmentShaderString = loadTextFile("fragmentShader");

		// Make the shader strings compatible with OpenGL 3 core if needed
		// GL2ES2 also includes the intersection of GL3 core
		// The default implicit GLSL version 1.1 is now depricated in GL3 core
		// GLSL 1.3 is the minimum version that now has to be explicitly set.
		// This allows the shaders to compile using the latest
		// desktop OpenGL 3 and 4 drivers.
		if (jogl.getGL2ES2().isGL3core()) {
			System.out
					.println("GL3 core detected: explicit add #version 130 to shaders");
			vertexShaderString = "#version 130\n" + vertexShaderString;
			fragmentShaderString = "#version 130\n" + fragmentShaderString;
		}

		// Create GPU shader handles
		// OpenGL ES retuns a index id to be stored for future reference.
		vertShader = jogl.getGL2ES2().glCreateShader(GL2ES2.GL_VERTEX_SHADER);
		fragShader = jogl.getGL2ES2().glCreateShader(GL2ES2.GL_FRAGMENT_SHADER);

		// Compile the vertexShader String into a program.
		String[] vlines = new String[] { vertexShaderString };

		// for (int i = 0; i < vlines.length; i++)
		// System.out.println(vlines[i]);

		int[] vlengths = new int[] { vlines[0].length() };
		jogl.getGL2ES2().glShaderSource(vertShader, vlines.length, vlines,
				vlengths, 0);
		jogl.getGL2ES2().glCompileShader(vertShader);

		// Check compile status.
		int[] compiled = new int[1];
		jogl.getGL2ES2().glGetShaderiv(vertShader, GL2ES2.GL_COMPILE_STATUS,
				compiled, 0);
		if (compiled[0] != 0) {
			System.out.println("Horray! vertex shader compiled");
		} else {
			int[] logLength = new int[1];
			jogl.getGL2ES2().glGetShaderiv(vertShader,
					GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

			byte[] log = new byte[logLength[0]];
			jogl.getGL2ES2().glGetShaderInfoLog(vertShader, logLength[0],
					(int[]) null, 0, log, 0);

			System.err.println("Error compiling the vertex shader: "
					+ new String(log));
			System.exit(1);
		}

		// Compile the fragmentShader String into a program.
		String[] flines = new String[] { fragmentShaderString };
		int[] flengths = new int[] { flines[0].length() };
		jogl.getGL2ES2().glShaderSource(fragShader, flines.length, flines,
				flengths, 0);
		jogl.getGL2ES2().glCompileShader(fragShader);

		// Check compile status.
		jogl.getGL2ES2().glGetShaderiv(fragShader, GL2ES2.GL_COMPILE_STATUS,
				compiled, 0);
		if (compiled[0] != 0) {
			System.out.println("Horray! fragment shader compiled");
		} else {
			int[] logLength = new int[1];
			jogl.getGL2ES2().glGetShaderiv(fragShader,
					GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

			byte[] log = new byte[logLength[0]];
			jogl.getGL2ES2().glGetShaderInfoLog(fragShader, logLength[0],
					(int[]) null, 0, log, 0);

			System.err.println("Error compiling the fragment shader: "
					+ new String(log));
			System.exit(1);
		}

		// Each shaderProgram must have
		// one vertex shader and one fragment shader.
		shaderProgram = jogl.getGL2ES2().glCreateProgram();
		jogl.getGL2ES2().glAttachShader(shaderProgram, vertShader);
		jogl.getGL2ES2().glAttachShader(shaderProgram, fragShader);

		// Associate attribute ids with the attribute names inside
		// the vertex shader.
		jogl.getGL2ES2().glBindAttribLocation(shaderProgram,
				GLSL_ATTRIB_POSITION, "attribute_Position");
		jogl.getGL2ES2().glBindAttribLocation(shaderProgram, GLSL_ATTRIB_COLOR,
				"attribute_Color");
		jogl.getGL2ES2().glBindAttribLocation(shaderProgram,
				GLSL_ATTRIB_NORMAL, "attribute_Normal");
		jogl.getGL2ES2().glBindAttribLocation(shaderProgram,
				GLSL_ATTRIB_TEXTURE, "attribute_Texture");

		jogl.getGL2ES2().glLinkProgram(shaderProgram);

		// Get a id number to the uniform_Projection matrix
		// so that we can update it.
		matrixLocation = jogl.getGL2ES2().glGetUniformLocation(shaderProgram,
				"matrix");

		// normalMatrixLocation =
		// jogl.getGL2ES2().glGetUniformLocation(shaderProgram, "normalMatrix");
		lightPositionLocation = jogl.getGL2ES2().glGetUniformLocation(
				shaderProgram, "lightPosition");
		ambiantDiffuseLocation = jogl.getGL2ES2().glGetUniformLocation(
				shaderProgram, "ambiantDiffuse");
		eyePositionLocation = jogl.getGL2ES2().glGetUniformLocation(
				shaderProgram, "eyePosition");
		enableLightLocation = jogl.getGL2ES2().glGetUniformLocation(
				shaderProgram, "enableLight");

		cullingLocation = jogl.getGL2ES2().glGetUniformLocation(shaderProgram,
				"culling");

		dashValuesLocation = jogl.getGL2ES2().glGetUniformLocation(
				shaderProgram, "dashValues");

		// texture
		textureTypeLocation = jogl.getGL2ES2().glGetUniformLocation(
				shaderProgram, "textureType");

		// color
		colorLocation = jogl.getGL2ES2().glGetUniformLocation(shaderProgram,
				"color");

		// normal
		normalLocation = jogl.getGL2ES2().glGetUniformLocation(shaderProgram,
				"normal");

		// center
		centerLocation = jogl.getGL2ES2().glGetUniformLocation(shaderProgram,
				"center");

		// clip planes
		enableClipPlanesLocation = jogl.getGL2ES2().glGetUniformLocation(
				shaderProgram, "enableClipPlanes");
		clipPlanesMinLocation = jogl.getGL2ES2().glGetUniformLocation(
				shaderProgram, "clipPlanesMin");
		clipPlanesMaxLocation = jogl.getGL2ES2().glGetUniformLocation(
				shaderProgram, "clipPlanesMax");

		// label rendering
		labelRenderingLocation = jogl.getGL2ES2().glGetUniformLocation(
				shaderProgram, "labelRendering");
		labelOriginLocation = jogl.getGL2ES2().glGetUniformLocation(
				shaderProgram, "labelOrigin");

		/*
		 * GL2ES2 also includes the intersection of GL3 core GL3 core and later
		 * mandates that a "Vector Buffer Object" must be created and bound
		 * before calls such as gl.glDrawArrays is used. The VBO lines in this
		 * demo makes the code forward compatible with OpenGL 3 and ES 3 core
		 * and later where a default vector buffer object is deprecated.
		 * 
		 * Generate two VBO pointers / handles VBO is data buffers stored inside
		 * the graphics card memory.
		 */
		vboHandles = new int[5];
		jogl.getGL2ES2().glGenBuffers(5, vboHandles, 0);
		vboColors = vboHandles[GLSL_ATTRIB_COLOR];
		vboVertices = vboHandles[GLSL_ATTRIB_POSITION];
		vboNormals = vboHandles[GLSL_ATTRIB_NORMAL];
		vboTextureCoords = vboHandles[GLSL_ATTRIB_TEXTURE];
		vboIndices = vboHandles[GLSL_ATTRIB_INDEX];
		// super.init(drawable);

		attribPointers();

	}

	/*
	 * private void drawTriangle(float[] vertices, float[] normals, float[]
	 * colors, float[] textureCoords){
	 * 
	 * //jogl.getGL2ES2().glUniform1i(jogl.getGL2ES2().glGetUniformLocation(
	 * shaderProgram, "Texture0"), 0);
	 * //getGL().glActiveTexture(GLlocal.GL_TEXTURE0);
	 * 
	 * 
	 * 
	 * byte[] bytes = new byte[]{ (byte) 255, (byte) 255, (byte) 255, (byte) 0,
	 * (byte) 0, (byte) 0 };
	 * 
	 * int texture = getTextures().createAlphaTexture(2, 2, bytes);
	 * 
	 * 
	 * getGL().glEnable(GLlocal.GL_TEXTURE_2D);
	 * getTextures().setTextureLinear(texture);
	 * //getGL().glActiveTexture(GLlocal.GL_TEXTURE0);
	 * //getGL().glBindTexture(GLlocal.GL_TEXTURE_2D, texture);
	 * 
	 * 
	 * //enableTextures(); //enableFading();
	 * 
	 * ArrayList<Float> array = new ArrayList<Float>();
	 * 
	 * for (int i = 0; i < 3 * 3; i++){ array.add(vertices[i]); }
	 * loadVertexBuffer(ManagerShaders.floatBuffer(array), 3);
	 * 
	 * array.clear(); for (int i = 0; i < 3 * 3; i++){ array.add(normals[i]); }
	 * loadNormalBuffer(ManagerShaders.floatBuffer(array), 3);
	 * 
	 * array.clear(); for (int i = 0; i < 3 * 2; i++){
	 * array.add(textureCoords[i]); }
	 * loadTextureBuffer(ManagerShaders.floatBuffer(array), 3);
	 * 
	 * draw(Manager.Type.TRIANGLES, 3);
	 * 
	 * getGL().glBindTexture(GLlocal.GL_TEXTURE_2D, 0);
	 * getTextures().removeTexture(texture);
	 * 
	 * }
	 */

	// @Override
	// public void createBuffers(GPUBuffer... buffers) {
	// final int length = buffers.length;
	// int[] b = new int[length];
	// jogl.getGL2ES2().glGenBuffers(b.length, b, 0);
	// for (int i = 0; i < length; i++) {
	// ((GPUBufferD) buffers[i]).set(b[i]);
	// }
	// }

	private void createBuffer(GPUBuffer buffer, Stack<Integer> stack) {
		if (stack.isEmpty()) {
			int[] b = new int[1];
			jogl.getGL2ES2().glGenBuffers(1, b, 0);
			((GPUBufferD) buffer).set(b[0]);
		} else {
			((GPUBufferD) buffer).set(stack.pop());
		}

	}

	public void createArrayBuffer(GPUBuffer buffer) {
		createBuffer(buffer, removedBuffers);
	}

	public void createElementBuffer(GPUBuffer buffer) {
		createBuffer(buffer, removedElementBuffers);
	}

	private Stack<Integer> removedBuffers = new Stack<Integer>();
	private Stack<Integer> removedElementBuffers = new Stack<Integer>();

	private static void removeBuffer(GPUBuffer buffer, Stack<Integer> stack) {
		stack.push(((GPUBufferD) buffer).get());
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
		int numBytes = length * size * 4; // 4 bytes per float
		glBufferData(numBytes, fb);

	}

	@Override
	public void storeElementBuffer(short[] fb, int length,
			GPUBuffer buffers) {
		// Select the VBO, GPU memory data
		bindBufferForIndices(buffers);

		// transfer data to VBO, this perform the copy of data from CPU -> GPU
		// memory
		jogl.getGL2ES2().glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, length * 2,
				ShortBuffer.wrap(fb), RendererJogl.GL_STREAM_DRAW);

	}

	@Override
	public void bindBufferForIndices(GPUBuffer buffer) {
		// Select the VBO, GPU memory data
		jogl.getGL2ES2().glBindBuffer(
				GL2ES2.GL_ELEMENT_ARRAY_BUFFER,
				((GPUBufferD) buffer).get());
	}

	final private void bindBuffer(GPUBuffer buffer) {
		jogl.getGL2ES2().glBindBuffer(GL2ES2.GL_ARRAY_BUFFER,
				((GPUBufferD) buffer).get());
	}

	/**
	 * enable vertex attribute
	 * 
	 * @param attrib
	 *            attribute
	 */
	protected void enableAttrib(int attrib) {
		jogl.getGL2ES2().glEnableVertexAttribArray(attrib);
	}

	/**
	 * disable vertex attribute
	 * 
	 * @param attrib
	 *            attribute
	 */
	protected void disableAttrib(int attrib) {
		jogl.getGL2ES2().glDisableVertexAttribArray(attrib);
	}

	/**
	 * set vertex attribute pointer
	 * 
	 * @param attrib
	 *            attribute
	 * @param size
	 *            size
	 */
	private void vertexAttribPointer(int attrib, int size) {
		jogl.getGL2ES2().glVertexAttribPointer(attrib, size, GL2ES2.GL_FLOAT,
				false, 0, 0);
	}

	private void vertexAttribPointerGlobal(int attrib, int size) {
		// vertexAttribPointer(attrib, size);
	}

	@Override
	public void bindBufferForVertices(GPUBuffer buffer, int size) {
		// Select the VBO, GPU memory data
		bindBuffer(buffer);
		// Associate Vertex attribute 0 with the last bound VBO
		vertexAttribPointer(GLSL_ATTRIB_POSITION, 3);

		// enable VBO
		enableAttrib(GLSL_ATTRIB_POSITION);
	}

	@Override
	public void bindBufferForColors(GPUBuffer buffer, int size,
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

	@Override
	public void bindBufferForNormals(GPUBuffer buffer, int size,
			GLBuffer fbNormals) {
		if (fbNormals == null || fbNormals.isEmpty()) { // no normals
			disableAttrib(GLSL_ATTRIB_NORMAL);
			return;
		}

		if (fbNormals.capacity() == 3) { // one normal for all vertices
			fbNormals.array(tmpNormal3);
			jogl.getGL2ES2().glUniform3fv(normalLocation, 1, tmpNormal3, 0);
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




	@Override
	public void loadVertexBuffer(GLBuffer fbVertices, int length) {

		// ///////////////////////////////////
		// VBO - vertices

		// Select the VBO, GPU memory data, to use for vertices
		jogl.getGL2ES2().glBindBuffer(GL.GL_ARRAY_BUFFER, vboVertices);

		// transfer data to VBO, this perform the copy of data from CPU -> GPU
		// memory
		int numBytes = length * 12; // 4 bytes per float * 3 coords per vertex
		glBufferData(numBytes, fbVertices);

		// Associate Vertex attribute 0 with the last bound VBO
		vertexAttribPointerGlobal(GLSL_ATTRIB_POSITION, 3);

		// VBO
		jogl.getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_POSITION);
	}

	@Override
	public void loadIndicesBuffer(GLBufferIndices arrayI, int length) {

		// ///////////////////////////////////
		// VBO - indices

		// Select the VBO, GPU memory data, to use for indices
		jogl.getGL2ES2().glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER,
				vboIndices);


		// transfer data to VBO, this perform the copy of data from CPU -> GPU
		// memory
		jogl.getGL2ES2().glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, length * 2,
				((GLBufferIndicesD) arrayI).getBuffer(),
				RendererJogl.GL_STREAM_DRAW);

	}

	protected boolean oneNormalForAllVertices;

	protected void resetOneNormalForAllVertices() {
		oneNormalForAllVertices = false;
		jogl.getGL2ES2().glUniform3f(normalLocation, 2, 2, 2);
	}

	/**
	 * push buffer data
	 * 
	 * @param numBytes
	 *            data size
	 * @param fb
	 *            buffer array
	 */
	protected void glBufferData(int numBytes, GLBuffer fb) {
		jogl.getGL2ES2().glBufferData(GL.GL_ARRAY_BUFFER, numBytes,
				((GLBufferD) fb).getBuffer(), RendererJogl.GL_STREAM_DRAW);

	}

	protected float[] tmpNormal3 = new float[3];

	@Override
	public void loadNormalBuffer(GLBuffer fbNormals, int length) {

		if (fbNormals == null || fbNormals.isEmpty()) { // no normals
			return;
		}

		if (fbNormals.capacity() == 3) { // one normal for all vertices
			fbNormals.array(tmpNormal3);
			jogl.getGL2ES2().glUniform3fv(normalLocation, 1, tmpNormal3, 0);
			oneNormalForAllVertices = true;
			return;
		}

		// ///////////////////////////////////
		// VBO - normals

		if (oneNormalForAllVertices) {
			resetOneNormalForAllVertices();
		}

		// Select the VBO, GPU memory data, to use for normals
		jogl.getGL2ES2().glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vboNormals);
		int numBytes = length * 12; // 4 bytes per float * * 3 coords per normal
		glBufferData(numBytes, fbNormals);

		// Associate Vertex attribute 1 with the last bound VBO
		vertexAttribPointerGlobal(GLSL_ATTRIB_NORMAL, 3);

		jogl.getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_NORMAL);
	}

	@Override
	public void loadTextureBuffer(GLBuffer fbTextures, int length) {

		if (fbTextures == null || fbTextures.isEmpty()) {
			setCurrentGeometryHasNoTexture();
			return;
		}

		setCurrentGeometryHasTexture();

		// Select the VBO, GPU memory data, to use for normals
		jogl.getGL2ES2().glBindBuffer(GL.GL_ARRAY_BUFFER, vboTextureCoords);
		int numBytes = length * 8; // 4 bytes per float * 2 coords per texture
		glBufferData(numBytes, fbTextures);

		// Associate Vertex attribute 1 with the last bound VBO
		vertexAttribPointerGlobal(GLSL_ATTRIB_TEXTURE, 2);

		jogl.getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_TEXTURE);
	}

	@Override
	public void loadColorBuffer(GLBuffer fbColors, int length) {


		if (fbColors == null || fbColors.isEmpty()) {
			return;
		}

		// prevent use of global color
		setColor(-1, -1, -1, -1);

		// Select the VBO, GPU memory data, to use for normals
		jogl.getGL2ES2().glBindBuffer(GL.GL_ARRAY_BUFFER, vboColors);
		int numBytes = length * 16; // 4 bytes per float * 4 color values (rgba)
		glBufferData(numBytes, fbColors);

		// Associate Vertex attribute 1 with the last bound VBO
		vertexAttribPointerGlobal(GLSL_ATTRIB_COLOR, 4);

		jogl.getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_COLOR);
	}

	/**
	 * attribute vertex pointers
	 */
	private void attribPointers() {

		jogl.getGL2ES2().glBindBuffer(GL.GL_ARRAY_BUFFER, vboVertices);
		vertexAttribPointer(GLSL_ATTRIB_POSITION, 3);

		jogl.getGL2ES2().glBindBuffer(GL.GL_ARRAY_BUFFER, vboNormals);
		vertexAttribPointer(GLSL_ATTRIB_NORMAL, 3);

		jogl.getGL2ES2().glBindBuffer(GL.GL_ARRAY_BUFFER, vboColors);
		vertexAttribPointer(GLSL_ATTRIB_COLOR, 4);

		jogl.getGL2ES2().glBindBuffer(GL.GL_ARRAY_BUFFER, vboTextureCoords);
		vertexAttribPointer(GLSL_ATTRIB_TEXTURE, 2);
	}

	@Override
	public void draw(Manager.Type type, int length) {

		// ///////////////////////
		// draw

		jogl.getGL2ES2().glDrawArrays(ManagerD.getGLType(type), 0, length);
	}

	protected final void setModelViewIdentity() {
		projectionMatrix.getForGL(tmpFloat16);
		jogl.getGL2ES2().glUniformMatrix4fv(matrixLocation, 1, false,
				tmpFloat16, 0);
	}

	@Override
	protected void draw() {

		resetOneNormalForAllVertices();
		disableTextures();

		setModelViewIdentity();

		super.draw();
	}

	private boolean objDone = false;

	private void doObj() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"geogebra3D/test.obj"));
			writer.write("######## CREATED WITH GEOGEBRA ########");

			((ManagerShadersObj) getGeometryManager()).startObjFile(writer);

			App.debug("=== Creating .OBJ === ");
			drawable3DLists.drawInObjFormat(this);

			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * @Override public void display(GLAutoDrawable drawable) { //protected void
	 * draw(){
	 * 
	 * //update 3D controller view3D.getEuclidianController().update();
	 * 
	 * view3D.updateAnimation();
	 * 
	 * // say that 3D view changed has been performed view3D.resetViewChanged();
	 * 
	 * 
	 * // Update variables used in animation double t1 =
	 * System.currentTimeMillis(); theta += (t1-t0)*0.005f; t0 = t1; s =
	 * Math.sin(theta);
	 * 
	 * // Get gl setGL(drawable);
	 * 
	 * 
	 * 
	 * // Clear screen jogl.getGL2ES2().glClearColor(0, 0, 0, 1f); // Purple
	 * jogl.getGL2ES2().glClear(GL2ES2.GL_STENCIL_BUFFER_BIT |
	 * GL2ES2.GL_COLOR_BUFFER_BIT | GL2ES2.GL_DEPTH_BUFFER_BIT );
	 * 
	 * 
	 * useShaderProgram();
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * setView(); setMatrixView();
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * //float[] normalMatrix = view3D.getRotationMatrix().get3x3ForGL();
	 * //jogl.getGL2ES2().glUniformMatrix3fv(normalMatrixLocation, 1, false,
	 * normalMatrix, 0);
	 * 
	 * 
	 * 
	 * // light
	 * 
	 * setLightPosition(); setLight(GLlocal.GL_LIGHT0);
	 * 
	 * 
	 * 
	 * 
	 * drawSample();
	 * 
	 * 
	 * releaseVBOs();
	 * 
	 * 
	 * }
	 */

	@Override
	protected void useShaderProgram() {
		jogl.getGL2ES2().glUseProgram(shaderProgram);
	}

	/*
	 * private void drawSample(){
	 * 
	 * // texture
	 * 
	 * //jogl.getGL2ES2().glUniform1i(fadingLocation, 0);
	 * 
	 * 
	 * float[] textureCoords = { 0, 0, 0, 1f, 1f, 1f };
	 * 
	 * 
	 * // draw
	 * 
	 * 
	 * float l = 1f;
	 * 
	 * float[] vertices = { 0.0f, 0f, 0.0f, 0, l, 0, 0, 0, l };
	 * 
	 * float[] normals = { 1, 0, 0, 1, 0, 0, 1, 0, 0 };
	 * 
	 * float alpha = 1f;
	 * 
	 * 
	 * float[] color = {1,0,1,1}; jogl.getGL2ES2().glUniform4fv(colorLocation,
	 * 1, color, 0); //loadVertexBuffer(vertices);//, normals, textureCoords);
	 * //draw(Manager.TRIANGLES, 3);
	 * 
	 * jogl.getGL2ES2().glUniform4fv(colorLocation, 1, PER_VERTEX_COLOR, 0);
	 * 
	 * float[] vertices2 = { 0.0f, 0f, 0f, l, 0, 0f, 0, 0, l };
	 * 
	 * float[] normals2 = { 0, 1, 0, 0, 1, 0, 0, 1, 0 };
	 * 
	 * float[] colors2 = { 0.0f, 1.0f, 0.0f, alpha, //Top color (red) 0.0f,
	 * 1.0f, 0.0f, alpha, //Bottom Left color (black) 0.0f, 1.0f, 0.0f, alpha
	 * //Bottom Right color (yellow) with 10% transparence };
	 * 
	 * 
	 * drawTriangle(vertices2, normals2, colors2, textureCoords);
	 * 
	 * 
	 * 
	 * float[] vertices4 ={ 0, -l, l, l, 0, 0f, 0, 0, l };
	 * 
	 * float[] normals4 = { 0, 0, 1, 0, 0, 1, 0, 0, 1 };
	 * 
	 * drawTriangle(vertices4, normals4, colors2, textureCoords);
	 * 
	 * 
	 * float z = 0f;
	 * 
	 * float[] vertices3 = { 0.0f, 0f, z, 0, l, z, l, 0, z }; float[] colors3 =
	 * { 0.0f, 0.0f, 1.0f, alpha, //Top color (red) 0.0f, 0.0f, 1.0f, alpha,
	 * //Bottom Left color (black) 0.0f, 0.0f, 1.0f, alpha //Bottom Right color
	 * (yellow) with 10% transparence };
	 * 
	 * drawTriangle(vertices3, normals4, colors3, textureCoords);
	 * 
	 * }
	 */

	private void releaseVBOs() {
		jogl.getGL2ES2().glDisableVertexAttribArray(GLSL_ATTRIB_POSITION); // Allow
																			// release
																			// of
																			// vertex
																			// position
																			// memory
		jogl.getGL2ES2().glDisableVertexAttribArray(GLSL_ATTRIB_COLOR); // Allow
																		// release
																		// of
																		// vertex
																		// color
																		// memory
		jogl.getGL2ES2().glDisableVertexAttribArray(GLSL_ATTRIB_NORMAL); // Allow
																			// release
																			// of
																			// vertex
																			// normal
																			// memory
		jogl.getGL2ES2().glDisableVertexAttribArray(GLSL_ATTRIB_TEXTURE); // Allow
																			// release
																			// of
																			// vertex
																			// texture
																			// memory

		jogl.getGL2ES2().glDeleteBuffers(4, vboHandles, 0); // Release VBO,
															// color and
															// vertices, buffer
															// GPU memory.
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		System.out.println("cleanup, remember to release shaders");

		setGL(drawable);

		jogl.getGL2ES2().glUseProgram(0);
		jogl.getGL2ES2().glDetachShader(shaderProgram, vertShader);
		jogl.getGL2ES2().glDeleteShader(vertShader);
		jogl.getGL2ES2().glDetachShader(shaderProgram, fragShader);
		jogl.getGL2ES2().glDeleteShader(fragShader);
		jogl.getGL2ES2().glDeleteProgram(shaderProgram);
		// System.exit(0);
	}

	@Override
	protected void setMatrixView() {

		tmpMatrix1.setMul(projectionMatrix, view3D.getToScreenMatrix());
		tmpMatrix1.getForGL(tmpFloat16);

		jogl.getGL2ES2().glUniformMatrix4fv(matrixLocation, 1, false,
				tmpFloat16, 0);
	}

	@Override
	protected void unsetMatrixView() {
		setModelViewIdentity();
	}

	@Override
	protected void setExportImage() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setColor(float r, float g, float b, float a) {
		jogl.getGL2ES2().glUniform4f(colorLocation, r, g, b, a);
	}

	private float[] tmpFloat16 = new float[16];

	@Override
	public void initMatrix() {

		tmpMatrix1.setMul(projectionMatrix,
				tmpMatrix2.setMul(view3D.getToScreenMatrix(), getMatrix()));
		tmpMatrix1.getForGL(tmpFloat16);

		jogl.getGL2ES2().glUniformMatrix4fv(matrixLocation, 1, false,
				tmpFloat16, 0);
	}

	@Override
	public void resetMatrix() {
		setMatrixView();
	}

	@Override
	public void drawMouseCursor() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setGLForPicking() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void pushSceneMatrix() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doPick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pickIntersectionCurves() {

		ArrayList<IntersectionCurve> curves = ((EuclidianController3D) view3D
				.getEuclidianController()).getIntersectionCurves();

		// picking objects
		for (IntersectionCurve intersectionCurve : curves) {
			Drawable3D d = intersectionCurve.drawable;
			d.updateForHitting(); // we may need an update
			if (!d.hit(hitting)
					|| d.getPickingType() != PickingType.POINT_OR_CURVE) { // we
																			// assume
																			// that
																			// hitting
																			// infos
																			// are
																			// updated
																			// from
																			// last
																			// mouse
																			// move
				d.setZPick(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
			}

		}

	}

	@Override
	public void glLoadName(int loop) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setLightPosition(float[] values) {
		jogl.getGL2ES2().glUniform3fv(lightPositionLocation, 1, values, 0);
		if (view3D.getMode() == EuclidianView3D.PROJECTION_PERSPECTIVE
				|| view3D.getMode() == EuclidianView3D.PROJECTION_PERSPECTIVE) {
			jogl.getGL2ES2().glUniform4fv(eyePositionLocation, 1,
					view3D.getViewDirection().get4ForGL(), 0);
		} else {
			jogl.getGL2ES2().glUniform4fv(eyePositionLocation, 1,
					view3D.getEyePosition().get4ForGL(), 0);
		}
	}

	private float[][] ambiantDiffuse;

	@Override
	protected void setLightAmbiantDiffuse(float ambiant0, float diffuse0,
			float ambiant1, float diffuse1) {

		float coeff = 1.414f;

		float a0 = ambiant0 * coeff;
		float d0 = 1 - a0;
		float a1 = ambiant1 * coeff;
		float d1 = 1 - a1;

		ambiantDiffuse = new float[][] { { a0, d0 }, { a1, d1 } };

		// ambiantDiffuse = new float[][] {
		// {ambiant0, diffuse0},
		// {ambiant1, diffuse1}
		// };

	}

	@Override
	protected void setLight(int light) {

		jogl.getGL2ES2().glUniform2fv(ambiantDiffuseLocation, 1,
				ambiantDiffuse[light], 0);
	}

	@Override
	protected void setColorMaterial() {
		getGL().glEnable(GLlocal.GL_COLOR_MATERIAL);

	}

	@Override
	protected void setLightModel() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setAlphaFunc() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setView() {

		setProjectionMatrix();

	}
	
	@Override
	protected void multProjectionMatrixForExportImage() {
		// TODO
	}

	private CoordMatrix4x4 projectionMatrix = new CoordMatrix4x4();

	private CoordMatrix4x4 tmpMatrix1 = new CoordMatrix4x4(),
			tmpMatrix2 = new CoordMatrix4x4();

	@Override
	protected void viewOrtho() {
		// the projection matrix is updated in updateOrthoValues()
	}

	@Override
	final public void updateOrthoValues() {

		projectionMatrix.set(1, 1, 2.0 / getWidth());
		projectionMatrix.set(2, 2, 2.0 / getHeight());
		projectionMatrix.set(3, 3, -2.0 / getVisibleDepth());
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
	protected void viewPersp() {
		// the projection matrix is updated in updatePerspValues()

	}

	@Override
	protected void updatePerspValues() {

		super.updatePerspValues();

		projectionMatrix.set(1, 1, 2 * perspNear[eye] / (perspRight[eye] - perspLeft[eye]));
		projectionMatrix.set(2, 1, 0);
		projectionMatrix.set(3, 1, 0);
		projectionMatrix.set(4, 1, 0);

		projectionMatrix.set(1, 2, 0);
		projectionMatrix.set(2, 2, 2 * perspNear[eye] / (perspTop[eye] - perspBottom[eye]));
		projectionMatrix.set(3, 2, 0);
		projectionMatrix.set(4, 2, 0);

		perspXZ = (perspRight[eye] + perspLeft[eye]) / (perspRight[eye] - perspLeft[eye]);

		projectionMatrix.set(1, 3, perspXZ);
		projectionMatrix.set(2, 3, (perspTop[eye] + perspBottom[eye])
				/ (perspTop[eye] - perspBottom[eye]));
		projectionMatrix.set(3, 3, 2 * perspFocus[eye] / getVisibleDepth());
		projectionMatrix.set(4, 3, -1);

		projectionMatrix.set(1, 4, 0);// (perspRight+perspLeft)/(perspRight-perspLeft)
										// * perspFocus);
		projectionMatrix.set(2, 4, 0);// (perspTop+perspBottom)/(perspTop-perspBottom)
										// * perspFocus);
		projectionMatrix.set(3, 4, getVisibleDepth() / 2);
		projectionMatrix.set(4, 4, -perspFocus[eye]);

	}

	private double perspXZ, glassesXZ;

	@Override
	public void updateGlassesValues() {
		super.updateGlassesValues();
		glassesXZ = (perspNear[eye] * (glassesEyeX[EYE_LEFT] - glassesEyeX[EYE_RIGHT]) / perspFocus[eye])
				/ (perspRight[eye] - perspLeft[eye]);
	}

	@Override
	protected void viewGlasses() {

		if (eye == EYE_LEFT) {
			projectionMatrix.set(1, 3, perspXZ + glassesXZ);
		} else {
			projectionMatrix.set(1, 3, perspXZ - glassesXZ);
		}

	}

	@Override
	protected void viewOblique() {
		// the projection matrix is updated in updateProjectionObliqueValues()
	}

	@Override
	public void updateProjectionObliqueValues() {
		super.updateProjectionObliqueValues();

		projectionMatrix.set(1, 1, 2.0 / getWidth());
		projectionMatrix.set(2, 1, 0);
		projectionMatrix.set(3, 1, 0);
		projectionMatrix.set(4, 1, 0);

		projectionMatrix.set(1, 2, 0);
		projectionMatrix.set(2, 2, 2.0 / getHeight());
		projectionMatrix.set(3, 2, 0);
		projectionMatrix.set(4, 2, 0);

		projectionMatrix.set(1, 3, obliqueX * 2.0 / getWidth());
		projectionMatrix.set(2, 3, obliqueY * 2.0 / getHeight());
		projectionMatrix.set(3, 3, -2.0 / getVisibleDepth());
		projectionMatrix.set(4, 3, 0);

		projectionMatrix.set(1, 4, 0);
		projectionMatrix.set(2, 4, 0);
		projectionMatrix.set(3, 4, 0);
		projectionMatrix.set(4, 4, 1);

	}

	@Override
	protected void setStencilLines() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Manager createManager() {
		return new ManagerShadersWithTemplates(this, view3D);
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
		super.enableTexturesForText();
		setCurrentTextureType(TEXTURE_TYPE_TEXT);
	}

	private int currentTextureType = TEXTURE_TYPE_NONE;
	private int oldTextureType = TEXTURE_TYPE_NONE;

	private void setCurrentTextureType(int type) {
		currentTextureType = type;
		jogl.getGL2ES2().glUniform1i(textureTypeLocation, type);
	}

	@Override
	public boolean areTexturesEnabled() {
		return texturesEnabled;
	}

	@Override
	public void setLineWidth(int width) {
		getGL().glLineWidth(width);
	}

	@Override
	protected float[] getLightPosition() {
		return LIGHT_POSITION_D;
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
			jogl.getGL2ES2().glUniform1fv(dashValuesLocation, 4,
					Textures.DASH_SHADERS_VALUES[index - 1], 0);
		}
	}

	@Override
	protected void drawSurfacesOutline() {

		// TODO

	}

	@Override
	protected void enableClipPlanes() {
		jogl.getGL2ES2().glUniform1i(enableClipPlanesLocation, 1);
	}

	@Override
	protected void disableClipPlanes() {
		jogl.getGL2ES2().glUniform1i(enableClipPlanesLocation, 0);
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

	private void setClipPlanesToShader() {

		jogl.getGL2ES2().glUniform3fv(clipPlanesMinLocation, 1, clipPlanesMin,
				0);
		jogl.getGL2ES2().glUniform3fv(clipPlanesMaxLocation, 1, clipPlanesMax,
				0);

	}

	@Override
	protected void initRenderingValues() {

		super.initRenderingValues();

		// clip planes
		setClipPlanesToShader();
	}

	@Override
	protected void drawFaceToScreen() {
		jogl.getGL2ES2().glUniform1i(labelRenderingLocation, 1);
		resetCenter();
		super.drawFaceToScreen();
		jogl.getGL2ES2().glUniform1i(labelRenderingLocation, 0);
	}
	
	@Override
	protected void drawFaceToScreenEnd() {
		jogl.getGL2ES2().glUniform1i(labelRenderingLocation, 1);
		resetCenter();
		super.drawFaceToScreenEnd();
		jogl.getGL2ES2().glUniform1i(labelRenderingLocation, 0);
	}

	@Override
	public void setLabelOrigin(Coords origin) {
		jogl.getGL2ES2().glUniform3fv(labelOriginLocation, 1,
				origin.get3ForGL(), 0);
	}

	private Hitting hitting;

	@Override
	public void setHits(GPoint mouseLoc, int threshold) {

		if (mouseLoc == null) {
			return;
		}

		hitting.setHits(mouseLoc, threshold);

	}

	@Override
	public GeoElement getLabelHit(GPoint mouseLoc) {

		if (mouseLoc == null) {
			return null;
		}

		return hitting.getLabelHit(mouseLoc);
	}

	@Override
	public void enableLighting() {
		if (view3D.getUseLight()){
			jogl.getGL2ES2().glUniform1i(enableLightLocation, 1);
		}
	}
	
	@Override
	public void initLighting() {
		if (view3D.getUseLight()) {
			jogl.getGL2ES2().glUniform1i(enableLightLocation, 1);
		} else {
			jogl.getGL2ES2().glUniform1i(enableLightLocation, 0);
		}
	}

	@Override
	public void disableLighting() {
		if (view3D.getUseLight()){
			jogl.getGL2ES2().glUniform1i(enableLightLocation, 0);
		}
	}

	@Override
	public boolean useLogicalPicking() {
		return true;
	}

	@Override
	public Hitting getHitting() {
		return hitting;
	}

	@Override
	public void setCenter(Coords center) {
		float[] c = center.get4ForGL();
		// set radius info
		c[3] *= DrawPoint3D.DRAW_POINT_FACTOR / view3D.getScale();
		jogl.getGL2ES2().glUniform4fv(centerLocation, 1, c, 0);
	}

	private float[] resetCenter = { 0f, 0f, 0f, 0f };

	@Override
	public void resetCenter() {
		jogl.getGL2ES2().glUniform4fv(centerLocation, 1, resetCenter, 0);
	}

	@Override
	public void disableCulling() {
		super.disableCulling();
		jogl.getGL2ES2().glUniform1i(cullingLocation, 1);
	}

	@Override
	public void setCullFaceFront() {
		super.setCullFaceFront();
		jogl.getGL2ES2().glUniform1i(cullingLocation, -1);
	}

	@Override
	public void setCullFaceBack() {
		super.setCullFaceBack();
		jogl.getGL2ES2().glUniform1i(cullingLocation, 1);
	}

	@Override
	protected void drawTranspNotCurved() {
		enableCulling();
		setCullFaceFront();
		drawable3DLists.drawTransp(this);
		drawable3DLists.drawTranspClosedNotCurved(this);
		setCullFaceBack();
		drawable3DLists.drawTransp(this);
		drawable3DLists.drawTranspClosedNotCurved(this);

	}

	@Override
	protected void enableLightingOnInit() {
		// no need for shaders
	}

	@Override
	protected void initCulling() {
		// no need for shaders
	}
}
