package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.fixedfunc.GLLightingFunc;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.RendererImplShaders;
import org.geogebra.common.geogebra3D.main.FragmentShader;
import org.geogebra.common.geogebra3D.main.VertexShader;
import org.geogebra.common.jre.openGL.GLBufferIndicesJre;
import org.geogebra.common.jre.openGL.GLBufferJre;
import org.geogebra.common.util.Charsets;
import org.geogebra.common.util.debug.Log;
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
	public RendererImplShadersD(Renderer renderer, EuclidianView3D view,
			RendererJogl jogl) {
		super(renderer, view);
		this.jogl = jogl;
		Log.debug(
				"============== RendererImplShadersD: Renderer with shaders created (shaders checked ok)");

	}

	private GL getGL() {

		return jogl.getGL2ES2();
	}

	// private int normalMatrixLocation;

	private int[] vboHandles;

	@Override
	final protected void compileShadersProgram() {

		String vertexShaderString, fragmentShaderString;

		vertexShaderString = VertexShader.getVertexShaderShiny(false);
		fragmentShaderString = FragmentShader
				.getFragmentShaderShinyForPacking(false);

		if (jogl.getGL2ES2().isGL3core()) {
			Log.debug(
					"GL3 core detected: explicitly add #version 130 to shaders");
			vertexShaderString = "#version 130\n" + vertexShaderString;
			fragmentShaderString = "#version 130\n" + fragmentShaderString;
		}

		// Create GPU shader handles
		// OpenGL ES returns an index id to be stored for future reference.
		int vertShader = jogl.getGL2ES2()
				.glCreateShader(GL2ES2.GL_VERTEX_SHADER);
		setVertShader(vertShader);
		int fragShader = jogl.getGL2ES2()
				.glCreateShader(GL2ES2.GL_FRAGMENT_SHADER);
		setFragShader(fragShader);

		// Compile the vertexShader String into a program.
		String[] vlines = new String[] { vertexShaderString };

		// for (int i = 0; i < vlines.length; i++)
		// System.out.println(vlines[i]);

		int[] vlengths = new int[] { vlines[0].length() };
		jogl.getGL2ES2().glShaderSource( vertShader, vlines.length,
				vlines, vlengths, 0);
		jogl.getGL2ES2().glCompileShader( vertShader);

		// Check compile status.
		int[] compiled = new int[1];
		jogl.getGL2ES2().glGetShaderiv( vertShader,
				GL2ES2.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] != 0) {
			Log.debug("Vertex shader compiled");
		} else {
			int[] logLength = new int[1];
			jogl.getGL2ES2().glGetShaderiv( vertShader,
					GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

			byte[] log = new byte[logLength[0]];
			jogl.getGL2ES2().glGetShaderInfoLog( vertShader,
					logLength[0], (int[]) null, 0, log, 0);

			Log.error("Error compiling the vertex shader: "
					+ new String(log, Charsets.getUtf8()));
			AppD.exit(1);
		}

		// Compile the fragmentShader String into a program.
		String[] flines = new String[] { fragmentShaderString };
		int[] flengths = new int[] { flines[0].length() };
		jogl.getGL2ES2().glShaderSource(fragShader, flines.length,
				flines, flengths, 0);
		jogl.getGL2ES2().glCompileShader(fragShader);

		// Check compile status.
		jogl.getGL2ES2().glGetShaderiv(fragShader,
				GL2ES2.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] != 0) {
			Log.debug("Fragment shader compiled");
		} else {
			int[] logLength = new int[1];
			jogl.getGL2ES2().glGetShaderiv(fragShader,
					GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

			byte[] log = new byte[logLength[0]];
			jogl.getGL2ES2().glGetShaderInfoLog(fragShader,
					logLength[0], (int[]) null, 0, log, 0);

			Log.error("Error compiling the fragment shader: "
					+ new String(log, Charsets.getUtf8()));
			AppD.exit(1);
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
		vboHandles = new int[GLSL_ATTRIB_SIZE];
		jogl.getGL2ES2().glGenBuffers(GLSL_ATTRIB_SIZE, vboHandles, 0);
	}

	@Override
	final protected int getStoreBufferNumBytes(int length, int size) {
		return length * size * 4; // 4 bytes per float
	}

	@Override
	final protected void bindBuffer(int bufferType, int buffer) {
		jogl.getGL2ES2().glBindBuffer(bufferType, vboHandles[buffer]);
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
	protected void vertexAttribPointer(int attrib, int size) {
		jogl.getGL2ES2().glVertexAttribPointer(attrib, size, GL.GL_FLOAT, false,
				0, 0);
	}

	@Override
	protected void glUniform3fv(Object location, float[] values) {
		jogl.getGL2ES2().glUniform3fv((Integer) location, 1, values, 0);
	}

	@Override
	protected void glUniform3f(Object location, float x, float y, float z) {
		jogl.getGL2ES2().glUniform3f((Integer) location, x, y, z);
	}

	@Override
	protected void glEnableVertexAttribArray(int attrib) {
		jogl.getGL2ES2().glEnableVertexAttribArray(attrib);
	}

	@Override
	protected void glBufferData(int numBytes, GLBuffer fb) {
		jogl.getGL2ES2().glBufferData(GL.GL_ARRAY_BUFFER, numBytes,
				((GLBufferJre) fb).getBuffer(), RendererJogl.GL_STREAM_DRAW);

	}

	@Override
	protected void glBufferDataIndices(int numBytes, GLBufferIndices arrayI) {
		jogl.getGL2ES2().glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, numBytes,
				((GLBufferIndicesJre) arrayI).getBuffer(),
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

	@Override
	final protected void glUseProgram(Object program) {
		jogl.getGL2ES2().glUseProgram((Integer) program);
	}

	@Override
	final protected void glDisableVertexAttribArray(int attrib) {
		jogl.getGL2ES2().glDisableVertexAttribArray(attrib);
	}

	@Override
	final protected void glDetachAndDeleteShader(Object program,
			Object shader) {
		jogl.getGL2ES2().glDetachShader((Integer) program, (Integer) shader);
		jogl.getGL2ES2().glDeleteShader((Integer) shader);
	}

	@Override
	final protected void glDeleteProgram(Object program) {
		jogl.getGL2ES2().glDeleteProgram((Integer) program);
	}

	@Override
	final protected void glUniform4f(Object location, float a, float b, float c,
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
	protected void glViewPort(int width, int height) {
		jogl.getGL2ES2().glViewport(0, 0, width, height);
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
		return GL.GL_FRONT;
	}

	@Override
	final protected int getGL_BACK() {
		return GL.GL_BACK;
	}

	@Override
	public void setBufferLeft() {
		jogl.getGL2().glDrawBuffer(GL2GL3.GL_BACK_LEFT);
		// zspace seems to be swapped
		// jogl.getGL2().glDrawBuffer(GLlocal.GL_BACK_RIGHT);
	}

	@Override
	public void setBufferRight() {
		jogl.getGL2().glDrawBuffer(GL2GL3.GL_BACK_RIGHT);
		// zspace seems to be swapped
		// jogl.getGL2().glDrawBuffer(GLlocal.GL_BACK_LEFT);
	}

	@Override
	public void setStencilFunc(int value) {
		getGL().glStencilFunc(GL.GL_EQUAL, value, 0xFF);
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
		glEnable(GL.GL_MULTISAMPLE);
	}

	@Override
	public final void disableMultisample() {
		glDisable(GL.GL_MULTISAMPLE);
	}

	@Override
	public int getGL_BLEND() {
		return GL.GL_BLEND;
	}

	@Override
	public int getGL_CULL_FACE() {
		return GL.GL_CULL_FACE;
	}

	@Override
	public void glClear(int flag) {
		getGL().glClear(flag);
	}

	@Override
	public int getGL_COLOR_BUFFER_BIT() {
		return GL.GL_COLOR_BUFFER_BIT;
	}

	@Override
	public int getGL_DEPTH_BUFFER_BIT() {
		return GL.GL_DEPTH_BUFFER_BIT;
	}

	@Override
	public int getGL_DEPTH_TEST() {
		return GL.GL_DEPTH_TEST;
	}

	@Override
	protected void bindFramebuffer(Object id) {
		getGL().glBindFramebuffer(GL.GL_FRAMEBUFFER, (Integer) id);
	}

	@Override
	protected void bindRenderbuffer(Object id) {
		getGL().glBindRenderbuffer(GL.GL_RENDERBUFFER, (Integer) id);
	}

	@Override
	protected void unbindFramebuffer() {
		bindFramebuffer(0);
	}

	@Override
	protected void unbindRenderbuffer() {
		bindRenderbuffer(0);
	}

	@Override
	protected void textureParametersNearest() {
		getGL().glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_NEAREST);
		getGL().glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_NEAREST);
	}

	@Override
	protected void textureImage2DForBuffer(int width, int height) {
		getGL().glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, width, height, 0,
				GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, null);
	}

	@Override
	protected void renderbufferStorage(int width, int height) {
		getGL().glRenderbufferStorage(GL.GL_RENDERBUFFER,
				GL2ES2.GL_DEPTH_COMPONENT, width, height);
	}

	private int[] tmp = new int[1];

	@Override
	protected Object genRenderbuffer() {
		getGL().glGenRenderbuffers(1, tmp, 0);
		return tmp[0];
	}

	@Override
	protected Object genFramebuffer() {
		getGL().glGenFramebuffers(1, tmp, 0);
		return tmp[0];
	}

	@Override
	protected void framebuffer(Object colorId, Object depthId) {
		getGL().glFramebufferTexture2D(GL.GL_FRAMEBUFFER,
				GL.GL_COLOR_ATTACHMENT0, GL.GL_TEXTURE_2D,
				(Integer) colorId, 0);
		getGL().glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER,
				GL.GL_DEPTH_ATTACHMENT, GL.GL_RENDERBUFFER,
				(Integer) depthId);
	}

	@Override
	protected boolean checkFramebufferStatus() {
		return getGL().glCheckFramebufferStatus(
				GL.GL_FRAMEBUFFER) == GL.GL_FRAMEBUFFER_COMPLETE;
	}

	@Override
	protected void glResetProgram() {
		glUseProgram(0);
	}

	@Override
	public void createDummyTexture() {
		// TODO: implement it?
	}

}
