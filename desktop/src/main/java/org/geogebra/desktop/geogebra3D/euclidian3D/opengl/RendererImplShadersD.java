package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ShortBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.fixedfunc.GLLightingFunc;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndicesJavaNio;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GPUBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShadersElementsGlobalBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.RendererImplShaders;
import org.geogebra.common.geogebra3D.main.FragmentShader;
import org.geogebra.common.geogebra3D.main.VertexShader;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererJogl.GLlocal;
import org.geogebra.desktop.main.AppD;

/**
 * Renderer using shaders
 * 
 * @author mathieu
 *
 */
public class RendererImplShadersD extends RendererImplShaders {

	private RendererJogl jogl;

	/**
	 * Constructor
	 * 
	 * @param renderer
	 *            GL renderer
	 * 
	 * @param view
	 *            view
	 * @param jogl
	 *            java openGL implementation
	 */
	public RendererImplShadersD(Renderer renderer,
			EuclidianView3D view,
			RendererJogl jogl) {
		super(renderer, view);
		this.jogl = jogl;
		Log.debug("============== RendererImplShadersD: Renderer with shaders created (shaders checked ok)");

	}

	private GL getGL() {

		return jogl.getGL2ES2();
	}


	// private int normalMatrixLocation;


	private int[] vboHandles;


	private String loadTextFile(String file) {

		return ((AppD) view3D.getApplication())
				.loadTextFile("/org/geogebra/desktop/geogebra3D/euclidian3D/opengl/shaders/" + file
						+ ".txt");
	}

	@Override
	final protected void compileShadersProgram() {


		String vertexShaderString, fragmentShaderString;

		App app = view3D.getApplication();

		if (app.has(Feature.SHINY_3D)) {
			vertexShaderString = VertexShader.getVertexShaderShiny(false);
			// vertexShaderString = loadTextFile("vertexShaderSpecular");
			fragmentShaderString = FragmentShader.getFragmentShaderShiny(0.2f,
					false);
			// fragmentShaderString = loadTextFile("fragmentShaderSpecular");
		} else {
			vertexShaderString = VertexShader.getVertexShader(false);
			// vertexShaderString = loadTextFile("vertexShader");
			fragmentShaderString = FragmentShader.getFragmentShader(false);
			// fragmentShaderString = loadTextFile("fragmentShader");
		}


		if (jogl.getGL2ES2().isGL3core()) {
			Log.debug(
					"GL3 core detected: explicitly add #version 130 to shaders");
			vertexShaderString = "#version 130\n" + vertexShaderString;
			fragmentShaderString = "#version 130\n" + fragmentShaderString;
		}

		// Create GPU shader handles
		// OpenGL ES returns an index id to be stored for future reference.
		vertShader = jogl.getGL2ES2().glCreateShader(
				javax.media.opengl.GL2ES2.GL_VERTEX_SHADER);
		fragShader = jogl.getGL2ES2().glCreateShader(
				javax.media.opengl.GL2ES2.GL_FRAGMENT_SHADER);

		// Compile the vertexShader String into a program.
		String[] vlines = new String[] { vertexShaderString };

		// for (int i = 0; i < vlines.length; i++)
		// System.out.println(vlines[i]);

		int[] vlengths = new int[] { vlines[0].length() };
		jogl.getGL2ES2().glShaderSource((Integer) vertShader, vlines.length,
				vlines,
				vlengths, 0);
		jogl.getGL2ES2().glCompileShader((Integer) vertShader);

		// Check compile status.
		int[] compiled = new int[1];
		jogl.getGL2ES2().glGetShaderiv((Integer) vertShader,
				javax.media.opengl.GL2ES2.GL_COMPILE_STATUS,
				compiled, 0);
		if (compiled[0] != 0) {
			Log.debug("Vertex shader compiled");
		} else {
			int[] logLength = new int[1];
			jogl.getGL2ES2().glGetShaderiv((Integer) vertShader,
					javax.media.opengl.GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

			byte[] log = new byte[logLength[0]];
			jogl.getGL2ES2().glGetShaderInfoLog((Integer) vertShader,
					logLength[0],
					(int[]) null, 0, log, 0);

			Log.error("Error compiling the vertex shader: "
					+ new String(log));
			System.exit(1);
		}

		// Compile the fragmentShader String into a program.
		String[] flines = new String[] { fragmentShaderString };
		int[] flengths = new int[] { flines[0].length() };
		jogl.getGL2ES2().glShaderSource((Integer) fragShader, flines.length,
				flines,
				flengths, 0);
		jogl.getGL2ES2().glCompileShader((Integer) fragShader);

		// Check compile status.
		jogl.getGL2ES2().glGetShaderiv((Integer) fragShader,
				javax.media.opengl.GL2ES2.GL_COMPILE_STATUS,
				compiled, 0);
		if (compiled[0] != 0) {
			Log.debug("Fragment shader compiled");
		} else {
			int[] logLength = new int[1];
			jogl.getGL2ES2().glGetShaderiv((Integer) fragShader,
					javax.media.opengl.GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

			byte[] log = new byte[logLength[0]];
			jogl.getGL2ES2().glGetShaderInfoLog((Integer) fragShader,
					logLength[0],
					(int[]) null, 0, log, 0);

			Log.error("Error compiling the fragment shader: "
					+ new String(log));
			System.exit(1);
		}



	}

	@Override
	final protected Object glCreateProgram() {
		return jogl.getGL2ES2().glCreateProgram();
	}

	@Override
	final protected void glAttachShader(Object shader) {
		jogl.getGL2ES2().glAttachShader((Integer) shaderProgram,
				(Integer) shader);
	}

	@Override
	final protected void glBindAttribLocation(int index, String name) {
		jogl.getGL2ES2().glBindAttribLocation((Integer) shaderProgram, index,
				name);
	}

	@Override
	final protected void glLinkProgram() {
		jogl.getGL2ES2().glLinkProgram((Integer) shaderProgram);
	}

	@Override
	final protected Object glGetUniformLocation(String name) {
		return jogl.getGL2ES2().glGetUniformLocation((Integer) shaderProgram,
				name);
	}



	@Override
	final protected void createVBOs() {
		vboHandles = new int[5];
		jogl.getGL2ES2().glGenBuffers(5, vboHandles, 0);

		vboColors = new GPUBufferD();
		vboVertices = new GPUBufferD();
		vboNormals = new GPUBufferD();
		vboTextureCoords = new GPUBufferD();
		vboIndices = new GPUBufferD();
		vboColors.set(vboHandles[GLSL_ATTRIB_COLOR]);
		vboVertices.set(vboHandles[GLSL_ATTRIB_POSITION]);
		vboNormals.set(vboHandles[GLSL_ATTRIB_NORMAL]);
		vboTextureCoords.set(vboHandles[GLSL_ATTRIB_TEXTURE]);
		vboIndices.set(vboHandles[GLSL_ATTRIB_INDEX]);
	}


	@Override
	final protected void createBufferFor(GPUBuffer buffer) {
		int[] b = new int[1];
		jogl.getGL2ES2().glGenBuffers(1, b, 0);
		buffer.set(b[0]);
	}


	@Override
	final protected int getStoreBufferNumBytes(int length, int size) {
		return length * size * 4; // 4 bytes per float
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
	final protected void bindBuffer(int bufferType, GPUBuffer buffer) {
		jogl.getGL2ES2().glBindBuffer(bufferType, ((GPUBufferD) buffer).get());
	}

	@Override
	final protected int getGL_ELEMENT_ARRAY_BUFFER() {
		return GL.GL_ELEMENT_ARRAY_BUFFER;
	}

	@Override
	final protected int getGL_ARRAY_BUFFER() {
		return GL.GL_ARRAY_BUFFER;
	}

	@Override
	protected void enableAttrib(int attrib) {
		jogl.getGL2ES2().glEnableVertexAttribArray(attrib);
	}

	@Override
	protected void disableAttrib(int attrib) {
		jogl.getGL2ES2().glDisableVertexAttribArray(attrib);
	}

	@Override
	protected void vertexAttribPointer(int attrib, int size) {
		jogl.getGL2ES2().glVertexAttribPointer(attrib, size, GL.GL_FLOAT,
				false, 0, 0);
	}

	@Override
	protected void glUniform3fv(Object location, float[] values) {
		jogl.getGL2ES2().glUniform3fv((Integer) location, 1, values, 0);
	}

	@Override
	protected void glUniform3f(Object location, float x, float y,
			float z){
		jogl.getGL2ES2().glUniform3f((Integer) location, x, y, z);
	}





	@Override
	protected void glEnableVertexAttribArray(int attrib) {
		jogl.getGL2ES2().glEnableVertexAttribArray(attrib);
	}



	@Override
	protected void glBufferData(int numBytes, GLBuffer fb) {
		jogl.getGL2ES2().glBufferData(GL.GL_ARRAY_BUFFER, numBytes,
				((GLBufferD) fb).getBuffer(), RendererJogl.GL_STREAM_DRAW);

	}

	@Override
	protected void glBufferDataIndices(int numBytes, GLBufferIndices arrayI) {
		jogl.getGL2ES2().glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, numBytes,
				((GLBufferIndicesJavaNio) arrayI).getBuffer(),
				RendererJogl.GL_STREAM_DRAW);

	}


	@Override
	public void draw(Manager.Type type, int length) {

		jogl.getGL2().glDrawElements(getGLType(type), length,
				GL.GL_UNSIGNED_SHORT, 0);
	}

	@Override
	protected int getGLType(Type type) {
		switch (type) {
		case TRIANGLE_STRIP:
			return GL.GL_TRIANGLE_STRIP;
		case TRIANGLE_FAN:
			return GL.GL_TRIANGLE_STRIP;
		case TRIANGLES:
			return GL.GL_TRIANGLES;
		case LINE_LOOP:
			return GL.GL_LINE_LOOP;
		case LINE_STRIP:
			return GL.GL_LINE_STRIP;
		}

		return 0;
	}


	@Override
	protected final void glUniformMatrix4fv(Object location, float[] values) {
		jogl.getGL2ES2().glUniformMatrix4fv((Integer) location, 1, false,
				values, 0);
	}



	private boolean objDone = false;

	private void doObj() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"geogebra3D/test.obj"));
			writer.write("######## CREATED WITH GEOGEBRA ########");

			((ManagerShadersObj) renderer.getGeometryManager())
					.startObjFile(writer);

			// App.debug("=== Creating .OBJ === ");
			renderer.drawable3DLists.drawInObjFormat(renderer);

			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	@Override
	final protected void glUseProgram(Object program) {
		jogl.getGL2ES2().glUseProgram((Integer) program);
	}

	@Override
	final protected void glDisableVertexAttribArray(int attrib) {
		jogl.getGL2ES2().glDisableVertexAttribArray(attrib);
	}

	final private void releaseVBOs() {
		glDisableVertexAttribArray(GLSL_ATTRIB_POSITION); // Allow
															// release
															// of
															// vertex
															// position
															// memory
		glDisableVertexAttribArray(GLSL_ATTRIB_COLOR); // Allow
														// release
														// of
														// vertex
														// color
														// memory
		glDisableVertexAttribArray(GLSL_ATTRIB_NORMAL); // Allow
														// release
														// of
														// vertex
														// normal
														// memory
		glDisableVertexAttribArray(GLSL_ATTRIB_TEXTURE); // Allow
															// release
															// of
															// vertex
															// texture
															// memory

		glDeleteBuffers(4, vboHandles); // Release VBO,
										// color and
										// vertices, buffer
										// GPU memory.
	}

	final private void glDeleteBuffers(int size, int[] buffers) {
		jogl.getGL2ES2().glDeleteBuffers(size, buffers, 0);
	}

	@Override
	final protected void glDetachAndDeleteShader(Object program, Object shader) {
		jogl.getGL2ES2().glDetachShader((Integer) program, (Integer) shader);
		jogl.getGL2ES2().glDeleteShader((Integer) shader);
	}

	@Override
	final protected void glDeleteProgram(Object program) {
		jogl.getGL2ES2().glDeleteProgram((Integer) program);
	}

	@Override
	final protected void glUniform4f(Object location, float a, float b,
			float c,
			float d) {
		jogl.getGL2ES2().glUniform4f((Integer) location, a, b, c, d);
	}


	@Override
	final protected void glUniform4fv(Object location, float[] values) {
		jogl.getGL2ES2().glUniform4fv((Integer) location, 1, values, 0);
	}

	@Override
	final protected void glUniform2fv(Object location, float[] values) {
		jogl.getGL2ES2().glUniform2fv((Integer) location, 1, values, 0);
	}

	@Override
	public void setColorMaterial() {
		getGL().glEnable(GLLightingFunc.GL_COLOR_MATERIAL);

	}


	@Override
	public void setView() {
		super.setView();

		// this part is needed for export image (pull up?)
		jogl.getGL2ES2().glViewport(0, 0, renderer.getWidth(),
				renderer.getHeight());
	}
	

	@Override
	public Manager createManager() {
		return new ManagerShadersElementsGlobalBuffer(renderer, view3D);
	}


	@Override
	protected void glUniform1i(Object location, int value) {
		jogl.getGL2ES2().glUniform1i((Integer) location, value);
	}

	@Override
	protected void glUniform1fv(Object location, int length, float[] values) {
		jogl.getGL2ES2().glUniform1fv((Integer) location, length, values, 0);
	}

	@Override
	public float[] getLightPosition() {
		return Renderer.LIGHT_POSITION_D;
	}


	@Override
	final protected void glCullFace(int flag) {
		getGL().glCullFace(flag);
	}

	@Override
	final protected int getGL_FRONT() {
		return GLlocal.GL_FRONT;
	}

	@Override
	final protected int getGL_BACK() {
		return GLlocal.GL_BACK;
	}


	@Override
	public void setBufferLeft() {
		jogl.getGL2().glDrawBuffer(GLlocal.GL_BACK_LEFT);
		// zspace seems to be swapped
		// jogl.getGL2().glDrawBuffer(GLlocal.GL_BACK_RIGHT);
	}

	@Override
	public void setBufferRight() {
		jogl.getGL2().glDrawBuffer(GLlocal.GL_BACK_RIGHT);
		// zspace seems to be swapped
		// jogl.getGL2().glDrawBuffer(GLlocal.GL_BACK_LEFT);
	}

	@Override
	public void setStencilFunc(int value) {
		getGL().glStencilFunc(GLlocal.GL_EQUAL, value, 0xFF);
	}

	@Override
	final protected void glDepthMask(boolean flag) {
		getGL().glDepthMask(flag);
	}


	@Override
	public void setColorMask(boolean r, boolean g, boolean b, boolean a) {
		getGL().glColorMask(r, g, b, a);
	}

	@Override
	public void setClearColor(float r, float g, float b, float a) {
		getGL().glClearColor(r, g, b, a);
	}

	@Override
	public void setPolygonOffset(float factor, float units) {
		getGL().glPolygonOffset(factor, units);
	}

	@Override
	public void genTextures2D(int number, int[] index) {
		getGL().glGenTextures(number, index, 0);
	}

	@Override
	public void bindTexture(int index) {
		getGL().glBindTexture(GL.GL_TEXTURE_2D, index);
	}

	@Override
	public void glEnable(int flag) {
		getGL().glEnable(flag);
	}

	@Override
	public void glDisable(int flag) {
		getGL().glDisable(flag);
	}

	@Override
	public final void enableMultisample() {
		glEnable(GLlocal.GL_MULTISAMPLE);
	}

	@Override
	public final void disableMultisample() {
		glDisable(GLlocal.GL_MULTISAMPLE);
	}


	@Override
	public int getGL_BLEND() {
		return GLlocal.GL_BLEND;
	}

	@Override
	public int getGL_CULL_FACE() {
		return GLlocal.GL_CULL_FACE;
	}

	@Override
	public void glClear(int flag) {
		getGL().glClear(flag);
	}

	@Override
	public int getGL_COLOR_BUFFER_BIT() {
		return GLlocal.GL_COLOR_BUFFER_BIT;
	}

	@Override
	public int getGL_DEPTH_BUFFER_BIT() {
		return GLlocal.GL_DEPTH_BUFFER_BIT;
	}

	@Override
	public int getGL_DEPTH_TEST() {
		return GLlocal.GL_DEPTH_TEST;
	}


}
