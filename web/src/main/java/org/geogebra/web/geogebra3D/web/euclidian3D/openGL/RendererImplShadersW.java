package org.geogebra.web.geogebra3D.web.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawLabel3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GPUBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShadersElementsGlobalBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.RendererImplShaders;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.shaders.GpuBlacklist;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.shaders.ShaderProvider;
import org.geogebra.web.html5.gawt.GBufferedImageW;

import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.ImageElement;
import com.googlecode.gwtgl.binding.WebGLFramebuffer;
import com.googlecode.gwtgl.binding.WebGLProgram;
import com.googlecode.gwtgl.binding.WebGLRenderbuffer;
import com.googlecode.gwtgl.binding.WebGLRenderingContext;
import com.googlecode.gwtgl.binding.WebGLShader;
import com.googlecode.gwtgl.binding.WebGLTexture;
import com.googlecode.gwtgl.binding.WebGLUniformLocation;

/**
 * Renderer using shaders
 * 
 * @author mathieu
 *
 */
public class RendererImplShadersW extends RendererImplShaders {

	private WebGLRenderingContext glContext;

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
	public RendererImplShadersW(Renderer renderer, EuclidianView3D view) {
		super(renderer, view);
		Log.debug("============== RendererImplShadersW: Renderer with shaders created");

	}

	/**
	 * set GL context
	 * 
	 * @param glContext
	 *            GL context
	 */
	public void setGL(WebGLRenderingContext glContext) {
		this.glContext = glContext;
		Log.debug("============== RendererImplShadersW: set GL context");
	}

	private WebGLRenderingContext getGL() {

		return glContext;
	}


	private WebGLShader getShader(int type, String source) {
		WebGLShader shader = glContext.createShader(type);

		glContext.shaderSource(shader, source);
		glContext.compileShader(shader);

		if (!glContext.getShaderParameterb(shader,
				WebGLRenderingContext.COMPILE_STATUS)) {
			Log.debug("ERROR COMPILING SHADER: "
					+ glContext.getShaderInfoLog(shader));
			throw new RuntimeException(glContext.getShaderInfoLog(shader));
		}

		return shader;
	}

	@Override
	final protected void compileShadersProgram() {

		boolean needsSmallFragmentShader = GpuBlacklist
				.isCurrentGpuBlacklisted(glContext);
		boolean shiny = view3D.getApplication().has(Feature.SHINY_3D);
		fragShader = getShader(
				WebGLRenderingContext.FRAGMENT_SHADER,
				ShaderProvider.getFragmentShader(needsSmallFragmentShader,
						shiny));
		vertShader = getShader(
				WebGLRenderingContext.VERTEX_SHADER,
				ShaderProvider.getVertexShader(needsSmallFragmentShader, shiny));




	}

	@Override
	final protected Object glCreateProgram() {
		return glContext.createProgram();
	}

	@Override
	final protected void glAttachShader(Object shader) {
		glContext.attachShader((WebGLProgram) shaderProgram,
				(WebGLShader) shader);
	}

	@Override
	final protected void glBindAttribLocation(int index, String name) {
		glContext.bindAttribLocation((WebGLProgram) shaderProgram, index,
				name);
	}


	@Override
	final protected void glLinkProgram() {
		glContext.linkProgram((WebGLProgram) shaderProgram);

		if (!glContext.getProgramParameterb((WebGLProgram) shaderProgram,
				WebGLRenderingContext.LINK_STATUS)) {
			throw new RuntimeException("Could not initialise shaders");
		}

		// use the program
		glContext.useProgram((WebGLProgram) shaderProgram);

		// attributes : note that vertex shader must use it, otherwise
		// getAttribLocation will return -1 (undefined)
		GLSL_ATTRIB_POSITION = getAttribLocation("attribute_Position");
		GLSL_ATTRIB_NORMAL = getAttribLocation("attribute_Normal");
		GLSL_ATTRIB_COLOR = getAttribLocation("attribute_Color");
		GLSL_ATTRIB_TEXTURE = getAttribLocation("attribute_Texture");

		Log.debug("vertexPositionAttribute=" + GLSL_ATTRIB_POSITION + ","
				+ "normalAttribute=" + GLSL_ATTRIB_NORMAL + ","
				+ "colorAttribute=" + GLSL_ATTRIB_COLOR + ","
				+ "textureAttribute=" + GLSL_ATTRIB_TEXTURE);
	}

	private int getAttribLocation(String name) {
		return glContext.getAttribLocation((WebGLProgram) shaderProgram, name);
	}

	@Override
	final protected Object glGetUniformLocation(String name) {
		return glContext.getUniformLocation((WebGLProgram) shaderProgram,
				name);
	}


	@Override
	final protected void createVBOs() {
		vboColors = new GPUBufferW();
		vboVertices = new GPUBufferW();
		vboNormals = new GPUBufferW();
		vboTextureCoords = new GPUBufferW();
		vboIndices = new GPUBufferW();
		vboColors.set(glContext.createBuffer());
		vboVertices.set(glContext.createBuffer());
		vboNormals.set(glContext.createBuffer());
		vboTextureCoords.set(glContext.createBuffer());
		vboIndices.set(glContext.createBuffer());
	}


	@Override
	final protected void createBufferFor(GPUBuffer buffer) {
		buffer.set(glContext.createBuffer());
	}


	@Override
	final protected int getStoreBufferNumBytes(int length, int size) {
		return length * size * 4; // 4 bytes per float
	}

	final static private int GL_TYPE_DRAW_TO_BUFFER = WebGLRenderingContext.STREAM_DRAW;

	@Override
	public void storeElementBuffer(short[] fb, int length,
			GPUBuffer buffers) {
		// Select the VBO, GPU memory data
		bindBufferForIndices(buffers);

		// transfer data to VBO, this perform the copy of data from CPU -> GPU
		// memory
		glContext.bufferData(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER,
				MyInt16Array.create(fb), GL_TYPE_DRAW_TO_BUFFER);

	}


	@Override
	final protected void bindBuffer(int bufferType, GPUBuffer buffer) {
		glContext.bindBuffer(bufferType, ((GPUBufferW) buffer).get());
	}

	@Override
	final protected int getGL_ELEMENT_ARRAY_BUFFER() {
		return WebGLRenderingContext.ELEMENT_ARRAY_BUFFER;
	}

	@Override
	final protected int getGL_ARRAY_BUFFER() {
		return WebGLRenderingContext.ARRAY_BUFFER;
	}


	@Override
	protected void vertexAttribPointer(int attrib, int size) {
		glContext.vertexAttribPointer(attrib, size,
				WebGLRenderingContext.FLOAT,
				false, 0, 0);
	}

	@Override
	protected void glUniform3fv(Object location, float[] values) {
		glContext.uniform3fv((WebGLUniformLocation) location, values);
	}

	@Override
	protected void glUniform3f(Object location, float x, float y,
			float z){
		glContext.uniform3f((WebGLUniformLocation) location, x, y, z);
	}





	@Override
	protected void glEnableVertexAttribArray(int attrib) {
		glContext.enableVertexAttribArray(attrib);
	}


	@Override
	protected void glBufferData(int numBytes, GLBuffer fb) {
		glContext.bufferData(WebGLRenderingContext.ARRAY_BUFFER,
				((GLBufferW) fb).getBuffer(), GL_TYPE_DRAW_TO_BUFFER);

	}

	@Override
	protected void glBufferDataIndices(int numBytes, GLBufferIndices arrayI) {
		glContext
				.bufferData(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER,
						((GLBufferIndicesW) arrayI).getBuffer(),
						GL_TYPE_DRAW_TO_BUFFER);

	}


	@Override
	public void draw(Manager.Type type, int length) {
		glContext.drawElements(getGLType(type), length,
				WebGLRenderingContext.UNSIGNED_SHORT, 0);
	}

	@Override
	protected int getGLType(Type type) {
		switch (type) {
		case TRIANGLE_STRIP:
			return WebGLRenderingContext.TRIANGLE_STRIP;
		case TRIANGLE_FAN:
			// if (Browser.supportsWebGLTriangleFan()){ // no TRIANGLE_FAN for
			// internet explorer
			// return WebGLRenderingContext.TRIANGLE_FAN;
			// }

			// wait for fix : detect webGL support correctly
			return WebGLRenderingContext.TRIANGLE_STRIP;
		case TRIANGLES:
			return WebGLRenderingContext.TRIANGLES;
		case LINE_LOOP:
			return WebGLRenderingContext.LINE_LOOP;
		case LINE_STRIP:
			return WebGLRenderingContext.LINE_STRIP;
		}

		return WebGLRenderingContext.TRIANGLES;
	}

	@Override
	protected final void glUniformMatrix4fv(Object location, float[] values) {
		glContext.uniformMatrix4fv((WebGLUniformLocation) location, false,
				values);
	}




	@Override
	final protected void glUseProgram(Object program) {
		glContext.useProgram((WebGLProgram) program);
	}

	@Override
	final protected void glDisableVertexAttribArray(int attrib) {
		glContext.disableVertexAttribArray(attrib);
	}


	@Override
	final protected void glDetachAndDeleteShader(Object program, Object shader) {
		glContext.detachShader((WebGLProgram) program, (WebGLShader) shader);
		glContext.deleteShader((WebGLShader) shader);
	}

	@Override
	final protected void glDeleteProgram(Object program) {
		glContext.deleteProgram((WebGLProgram) program);
	}

	@Override
	final protected void glUniform4f(Object location, float a, float b,
			float c,
			float d) {
		glContext.uniform4f((WebGLUniformLocation) location, a, b, c, d);
	}


	@Override
	final protected void glUniform4fv(Object location, float[] values) {
		glContext.uniform4fv((WebGLUniformLocation) location, values);
	}

	@Override
	final protected void glUniform2fv(Object location, float[] values) {
		glContext.uniform2fv((WebGLUniformLocation) location, values);
	}

	@Override
	public void setColorMaterial() {
		// not used in WebGL
	}

	
	@Override
	protected void glViewPort(int width, int height) {
		glContext.viewport(0, 0, width, height);
	}

	@Override
	public Manager createManager() {
		return new ManagerShadersElementsGlobalBuffer(renderer, view3D);
	}


	@Override
	protected void glUniform1i(Object location, int value) {
		glContext.uniform1i((WebGLUniformLocation) location, value);
	}

	@Override
	protected void glUniform1fv(Object location, int length, float[] values) {
		glContext.uniform1fv((WebGLUniformLocation) location, values);
	}

	@Override
	public float[] getLightPosition() {
		return Renderer.LIGHT_POSITION_W;
	}


	@Override
	final protected void glCullFace(int flag) {
		getGL().cullFace(flag);
	}

	@Override
	final protected int getGL_FRONT() {
		return WebGLRenderingContext.FRONT;
	}

	@Override
	final protected int getGL_BACK() {
		return WebGLRenderingContext.BACK;
	}


	@Override
	public void setBufferLeft() {
		// not used in web
	}

	@Override
	public void setBufferRight() {
		// not used in web
	}

	@Override
	public void setStencilFunc(int value) {
		// not used in web
	}

	@Override
	final protected void glDepthMask(boolean flag) {
		getGL().depthMask(flag);
	}


	@Override
	public void setColorMask(boolean r, boolean g, boolean b, boolean a) {
		getGL().colorMask(r, g, b, a);
	}

	@Override
	public void setClearColor(float r, float g, float b, float a) {
		getGL().clearColor(r, g, b, a);
	}

	@Override
	public void setPolygonOffset(float factor, float units) {
		getGL().polygonOffset(factor, units);
	}

	@Override
	public void genTextures2D(int number, int[] index) {
		int size = texturesArray.size();
		for (int i = 0; i < number; i++) { // add new textures
			index[i] = size + i;
			texturesArray.add(glContext.createTexture());
		}
	}

	private ArrayList<WebGLTexture> texturesArray = new ArrayList<WebGLTexture>();

	@Override
	public void bindTexture(int index) {
		glContext.bindTexture(WebGLRenderingContext.TEXTURE_2D,
				texturesArray.get(index));
	}

	@Override
	public void glEnable(int flag) {
		getGL().enable(flag);
	}

	@Override
	public void glDisable(int flag) {
		getGL().disable(flag);
	}

	@Override
	public final void enableMultisample() {
		// not used in web
	}

	@Override
	public final void disableMultisample() {
		// not used in web
	}


	@Override
	public int getGL_BLEND() {
		return WebGLRenderingContext.BLEND;
	}

	@Override
	public int getGL_CULL_FACE() {
		return WebGLRenderingContext.CULL_FACE;
	}

	@Override
	public void glClear(int flag) {
		getGL().clear(flag);
	}

	@Override
	public int getGL_COLOR_BUFFER_BIT() {
		return WebGLRenderingContext.COLOR_BUFFER_BIT;
	}

	@Override
	public int getGL_DEPTH_BUFFER_BIT() {
		return WebGLRenderingContext.DEPTH_BUFFER_BIT;
	}

	@Override
	public int getGL_DEPTH_TEST() {
		return WebGLRenderingContext.DEPTH_TEST;
	}


	/**
	 * create alpha texture from image for the label
	 * 
	 * @param label
	 *            label
	 * @param image
	 *            image
	 * @param bimg
	 *            buffered image
	 */
	public void createAlphaTexture(DrawLabel3D label, ImageElement image,
			GBufferedImageW bimg) {

		if (label.isPickable()) {
			// values for picking (ignore transparent bytes)
			ImageData data = bimg.getImageData();
			int xmin = label.getWidth(), xmax = 0, ymin = label.getHeight(), ymax = 0;
			for (int y = 0; y < label.getHeight(); y++) {
				for (int x = 0; x < label.getWidth(); x++) {
					int alpha = data.getAlphaAt(x, y);
					if (alpha != 0) {
						if (x < xmin) {
							xmin = x;
						}
						if (x > xmax) {
							xmax = x;
						}
						if (y < ymin) {
							ymin = y;
						}
						if (y > ymax) {
							ymax = y;
						}
					}
				}
			}
			label.setPickingDimension(xmin, ymin, xmax - xmin + 1, ymax - ymin
					+ 1);
		}

		// create texture
		WebGLTexture texture;

		int textureIndex = label.getTextureIndex();

		if (textureIndex == -1) {
			textureIndex = texturesArray.size();
			texture = glContext.createTexture();
			texturesArray.add(texture);
		} else {
			texture = texturesArray.get(textureIndex);
		}

		glContext.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture);

		glContext.texImage2D(WebGLRenderingContext.TEXTURE_2D, 0,
				WebGLRenderingContext.RGBA, WebGLRenderingContext.RGBA,
				WebGLRenderingContext.UNSIGNED_BYTE, image);

		glContext.generateMipmap(WebGLRenderingContext.TEXTURE_2D);

		glContext.bindTexture(WebGLRenderingContext.TEXTURE_2D, null);

		label.setTextureIndex(textureIndex);
	}

	protected void bindFramebuffer(Object id) {
		getGL().bindFramebuffer(WebGLRenderingContext.FRAMEBUFFER,
				(WebGLFramebuffer) id);
	}

	protected void bindRenderbuffer(Object id) {
		getGL().bindRenderbuffer(WebGLRenderingContext.RENDERBUFFER,
				(WebGLRenderbuffer) id);
	}

	protected void unbindFramebuffer() {
		bindFramebuffer(null);
	}

	protected void unbindRenderbuffer() {
		bindRenderbuffer(null);
	}

	protected void textureParametersNearest() {
		getGL().texParameterf(WebGLRenderingContext.TEXTURE_2D,
				WebGLRenderingContext.TEXTURE_MAG_FILTER,
				WebGLRenderingContext.NEAREST);
		getGL().texParameterf(WebGLRenderingContext.TEXTURE_2D,
				WebGLRenderingContext.TEXTURE_MIN_FILTER,
				WebGLRenderingContext.NEAREST);
	}

	protected void textureImage2DForBuffer(int width, int height) {
		getGL().texImage2D(WebGLRenderingContext.TEXTURE_2D, 0,
				WebGLRenderingContext.RGBA, width, height, 0,
				WebGLRenderingContext.RGBA,
				WebGLRenderingContext.UNSIGNED_BYTE, null);
	}

	protected void renderbufferStorage(int width, int height) {
		getGL().renderbufferStorage(WebGLRenderingContext.RENDERBUFFER,
				WebGLRenderingContext.DEPTH_COMPONENT, width, height);
	}


	protected Object genTexture() {
		return getGL().createTexture();
	}

	protected Object genRenderbuffer() {
		return getGL().createRenderbuffer();
	}

	protected Object genFramebuffer() {
		return getGL().createFramebuffer();
	}

	protected void framebuffer(Object colorId, Object depthId) {
		getGL().framebufferTexture2D(WebGLRenderingContext.FRAMEBUFFER,
				WebGLRenderingContext.COLOR_ATTACHMENT0,
				WebGLRenderingContext.TEXTURE_2D, (WebGLTexture) colorId, 0);
		getGL().framebufferRenderbuffer(WebGLRenderingContext.FRAMEBUFFER,
				WebGLRenderingContext.DEPTH_ATTACHMENT,
				WebGLRenderingContext.RENDERBUFFER, (WebGLRenderbuffer) depthId);
	}

	protected boolean checkFramebufferStatus() {
		return getGL()
				.checkFramebufferStatus(
				WebGLRenderingContext.FRAMEBUFFER) == WebGLRenderingContext.FRAMEBUFFER_COMPLETE;
	}

}
