package org.geogebra.web.geogebra3D.web.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawLabel3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.RendererImplShaders;
import org.geogebra.common.geogebra3D.main.FragmentShader;
import org.geogebra.common.geogebra3D.main.VertexShader;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gawt.GBufferedImageW;

import elemental2.core.Uint8Array;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.ImageData;
import elemental2.webgl.WebGLBuffer;
import elemental2.webgl.WebGLFramebuffer;
import elemental2.webgl.WebGLProgram;
import elemental2.webgl.WebGLRenderbuffer;
import elemental2.webgl.WebGLRenderingContext;
import elemental2.webgl.WebGLShader;
import elemental2.webgl.WebGLTexture;
import elemental2.webgl.WebGLUniformLocation;
import jsinterop.base.Js;

/**
 * Renderer using shaders
 * 
 * @author mathieu
 *
 */
public class RendererImplShadersW extends RendererImplShaders {

	private static final int GL_TYPE_DRAW_TO_BUFFER = WebGLRenderingContext.STREAM_DRAW;
	private WebGLRenderingContext glContext;
	private ArrayList<WebGLTexture> texturesArray = new ArrayList<>();
	private WebGLBuffer[] vboHandles;

	/**
	 * Constructor
	 *
	 * @param renderer
	 *            GL renderer
	 *
	 * @param view
	 *            view
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

		if (Js.isFalsy(glContext.getShaderParameter(shader,
				WebGLRenderingContext.COMPILE_STATUS))) {
			Log.debug("ERROR COMPILING SHADER: "
					+ glContext.getShaderInfoLog(shader));
			throw new RuntimeException(glContext.getShaderInfoLog(shader));
		}

		return shader;
	}

	@Override
	protected final void compileShadersProgram() {
		setFragShader(getShader(
				WebGLRenderingContext.FRAGMENT_SHADER,
				FragmentShader.getFragmentShaderShinyForPacking(true)));
		setVertShader(getShader(
				WebGLRenderingContext.VERTEX_SHADER,
				VertexShader.getVertexShaderShiny(true)));
	}

	@Override
	protected final Object glCreateProgram() {
		return glContext.createProgram();
	}

	@Override
	protected final void glAttachShader(Object shader) {
		glContext.attachShader((WebGLProgram) shaderProgram,
				(WebGLShader) shader);
	}

	@Override
	protected final void glBindAttribLocation(int index, String name) {
		glContext.bindAttribLocation((WebGLProgram) shaderProgram, index,
				name);
	}

	@Override
	protected final void glLinkProgram() {
		glContext.linkProgram((WebGLProgram) shaderProgram);

		if (Js.isFalsy(glContext.getProgramParameter((WebGLProgram) shaderProgram,
				WebGLRenderingContext.LINK_STATUS))) {
			throw new RuntimeException("Could not initialise shaders");
		}

		// use the program
		glContext.useProgram((WebGLProgram) shaderProgram);
	}

	@Override
	protected final Object glGetUniformLocation(String name) {
		return glContext.getUniformLocation((WebGLProgram) shaderProgram,
				name);
	}

	@Override
	protected final void createVBOs() {
		vboHandles = new WebGLBuffer[GLSL_ATTRIB_SIZE];
		for (int i = 0; i < 5; i++) {
			vboHandles[i] = glContext.createBuffer();
		}
	}

	@Override
	protected final int getStoreBufferNumBytes(int length, int size) {
		return length * size * 4; // 4 bytes per float
	}

	@Override
	protected final void bindBuffer(int bufferType, int buffer) {
		glContext.bindBuffer(bufferType, vboHandles[buffer]);
	}

	@Override
	protected final int getGL_ELEMENT_ARRAY_BUFFER() {
		return WebGLRenderingContext.ELEMENT_ARRAY_BUFFER;
	}

	@Override
	protected final int getGL_ARRAY_BUFFER() {
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
		glContext.uniform3fv((WebGLUniformLocation) location,
				WebGLRenderingContext.Uniform3fvValueUnionType.of(values));
	}

	@Override
	protected void glUniform3f(Object location, float x, float y,
			float z) {
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
				Js.<double[]>uncheckedCast(values));
	}

	@Override
	protected final void glUseProgram(Object program) {
		glContext.useProgram((WebGLProgram) program);
	}

	@Override
	protected final void glDisableVertexAttribArray(int attrib) {
		glContext.disableVertexAttribArray(attrib);
	}

	@Override
	protected final void glDetachAndDeleteShader(Object program,
			Object shader) {
		glContext.detachShader((WebGLProgram) program, (WebGLShader) shader);
		glContext.deleteShader((WebGLShader) shader);
	}

	@Override
	protected final void glDeleteProgram(Object program) {
		glContext.deleteProgram((WebGLProgram) program);
	}

	@Override
	protected final void glUniform4f(Object location, float a, float b,
			float c,
			float d) {
		glContext.uniform4f((WebGLUniformLocation) location, a, b, c, d);
	}

	@Override
	protected final void glUniform4fv(Object location, float[] values) {
		glContext.uniform4fv((WebGLUniformLocation) location,
				WebGLRenderingContext.Uniform4fvValueUnionType.of(values));
	}

	@Override
	protected final void glUniform2fv(Object location, float[] values) {
		glContext.uniform2fv((WebGLUniformLocation) location,
				WebGLRenderingContext.Uniform2fvValueUnionType.of(values));
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
	protected void glUniform1i(Object location, int value) {
		glContext.uniform1i((WebGLUniformLocation) location, value);
	}

	@Override
	protected void glUniform1fv(Object location, int length, float[] values) {
		glContext.uniform1fv((WebGLUniformLocation) location,
				Js.<double[]>uncheckedCast(values));
	}

	@Override
	public float[] getLightPosition() {
		return Renderer.LIGHT_POSITION_W;
	}

	@Override
	protected final void glCullFace(int flag) {
		getGL().cullFace(flag);
	}

	@Override
	protected final int getGL_FRONT() {
		return WebGLRenderingContext.FRONT;
	}

	@Override
	protected final int getGL_BACK() {
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
	protected final void glDepthMask(boolean flag) {
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
	public void createAlphaTexture(DrawLabel3D label, HTMLImageElement image,
			GBufferedImageW bimg) {

		if (label.isPickable()) {
			// values for picking (ignore transparent bytes)
			ImageData data = bimg.getImageData();
			updatePickingDimension(label, data);
		}

		int textureIndex = createAlphaTexture(label.getTextureIndex(), image, bimg);
		label.setTextureIndex(textureIndex);
	}

	/**
	* create alpha texture from image
	* 
	* @param index
	*            reusable index
	* @param image
	*            image
	* @param bimg
	*            buffered image
	* @return new index if needed
	*/

	public int createAlphaTexture(int index, HTMLImageElement image, GBufferedImageW bimg) {
		// create texture
		WebGLTexture texture;

		int newIndex = index;
		if (newIndex == -1) {
			newIndex = texturesArray.size();
			texture = glContext.createTexture();
			texturesArray.add(texture);
		} else {
			texture = texturesArray.get(newIndex);
		}

		glContext.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture);
		Object data = image == null
				? bimg.getCanvas().getCanvasElement() : image;
		glContext.texImage2D(WebGLRenderingContext.TEXTURE_2D, 0,
				WebGLRenderingContext.RGBA, WebGLRenderingContext.RGBA,
				WebGLRenderingContext.UNSIGNED_BYTE,
				Js.<elemental2.dom.ImageData>uncheckedCast(data));
		glContext.generateMipmap(WebGLRenderingContext.TEXTURE_2D);

		return newIndex;
	}

	/**
	* 
	* @param sizeX
	*            width
	* @param sizeY
	*            height
	* @param buf
	*            buffer
	* @return a texture for alpha channel
	*/
	public int createAlphaTexture(int sizeX, int sizeY, byte[] buf) {

		// create texture
		WebGLTexture texture;

		int newIndex = texturesArray.size();
		texture = glContext.createTexture();
		texturesArray.add(texture);

		// create array with alpha channel
		Uint8Array array = new Uint8Array(buf.length * 4);
		for (int i = 0; i < buf.length; i++) {
			array.setAt(i * 4 + 3, (double) buf[i]);
		}
		
		glContext.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture);
		glContext.texImage2D(WebGLRenderingContext.TEXTURE_2D, 0,
				WebGLRenderingContext.RGBA, sizeX, sizeY, 0,
				WebGLRenderingContext.RGBA, WebGLRenderingContext.UNSIGNED_BYTE, array);
		glContext.generateMipmap(WebGLRenderingContext.TEXTURE_2D);

		return newIndex;
	}

	@Override
	public void createDummyTexture() {
		createAlphaTexture(-1, null, new GBufferedImageW(2, 2, 1));
	}

	private static void updatePickingDimension(DrawLabel3D label,
			ImageData data) {
		int xmin = label.getWidth(), xmax = 0, ymin = label.getHeight(),
				ymax = 0;
		for (int y = 0; y < label.getHeight(); y++) {
			for (int x = 0; x < label.getWidth(); x++) {
				Double alpha = data.data.getAt(4 * (x + y * data.width) + 3);
				if (alpha != null && alpha != 0) {
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
		label.setPickingDimension(xmin, ymin, xmax - xmin + 1, ymax - ymin + 1);
	}

	@Override
	protected void bindFramebuffer(Object id) {
		getGL().bindFramebuffer(WebGLRenderingContext.FRAMEBUFFER,
				(WebGLFramebuffer) id);
	}

	@Override
	protected void bindRenderbuffer(Object id) {
		getGL().bindRenderbuffer(WebGLRenderingContext.RENDERBUFFER,
				(WebGLRenderbuffer) id);
	}

	@Override
	protected void unbindFramebuffer() {
		bindFramebuffer(null);
	}

	@Override
	protected void unbindRenderbuffer() {
		bindRenderbuffer(null);
	}

	@Override
	protected void textureParametersNearest() {
		getGL().texParameterf(WebGLRenderingContext.TEXTURE_2D,
				WebGLRenderingContext.TEXTURE_MAG_FILTER,
				WebGLRenderingContext.NEAREST);
		getGL().texParameterf(WebGLRenderingContext.TEXTURE_2D,
				WebGLRenderingContext.TEXTURE_MIN_FILTER,
				WebGLRenderingContext.NEAREST);
	}

	@Override
	protected void textureImage2DForBuffer(int width, int height) {
		getGL().texImage2D(WebGLRenderingContext.TEXTURE_2D, 0,
				WebGLRenderingContext.RGBA, width, height, 0,
				WebGLRenderingContext.RGBA,
				WebGLRenderingContext.UNSIGNED_BYTE, null);
	}

	@Override
	protected void renderbufferStorage(int width, int height) {
		getGL().renderbufferStorage(WebGLRenderingContext.RENDERBUFFER,
				WebGLRenderingContext.DEPTH_COMPONENT, width, height);
	}

	@Override
	protected Object genRenderbuffer() {
		return getGL().createRenderbuffer();
	}

	@Override
	protected Object genFramebuffer() {
		return getGL().createFramebuffer();
	}

	@Override
	protected void framebuffer(Object colorId, Object depthId) {
		getGL().framebufferTexture2D(WebGLRenderingContext.FRAMEBUFFER,
				WebGLRenderingContext.COLOR_ATTACHMENT0,
				WebGLRenderingContext.TEXTURE_2D, (WebGLTexture) colorId, 0);
		getGL().framebufferRenderbuffer(WebGLRenderingContext.FRAMEBUFFER,
				WebGLRenderingContext.DEPTH_ATTACHMENT,
				WebGLRenderingContext.RENDERBUFFER, (WebGLRenderbuffer) depthId);
	}

	@Override
	protected boolean checkFramebufferStatus() {
		int status = getGL().checkFramebufferStatus(
						WebGLRenderingContext.FRAMEBUFFER);
		return status == WebGLRenderingContext.FRAMEBUFFER_COMPLETE;
	}

	@Override
	protected void glResetProgram() {
		glUseProgram(null);
	}

	@Override
	public final void selectFBO() {
		// TODO implement?
	}

	@Override
	public final void unselectFBO() {
		// TODO implement?
	}

	@Override
	public void needExportImage(double scale, int w, int h) {
		// TODO implement?
	}

	@Override
	protected void setExportImageDimension(int w, int h) {
		// TODO implement?
	}

}
