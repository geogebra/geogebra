package org.geogebra.web.geogebra3D.web.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLFactory;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.Geometry;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;

import com.googlecode.gwtgl.binding.WebGLBuffer;
import com.googlecode.gwtgl.binding.WebGLRenderingContext;

/**
 * renderer using shaders and drawElements()
 * 
 * @author mathieu
 *
 */
public class RendererShadersElementsW extends RendererW {

	/**
	 * constructor
	 * 
	 * @param view
	 *            3D view
	 */
	public RendererShadersElementsW(EuclidianView3DW view) {
		super(view);
	}


	@Override
	protected void draw() {

		resetOneNormalForAllVertices();
		disableTextures();

		setModelViewIdentity();


		// init drawing matrix to view3D toScreen matrix
		setMatrixView();

		setLightPosition();
		setLight(0);
		disableCulling();

		enableMultisample();


		// ////////////
		// tests

		// points test

		// disableLighting();
		setColor(-1, 0, 0, 1);


		updateBuffers();

		glEnableVertexAttribArray(GLSL_ATTRIB_POSITION);
		glEnableVertexAttribArray(GLSL_ATTRIB_COLOR);
		glEnableVertexAttribArray(GLSL_ATTRIB_NORMAL);



		// elements
		glContext.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER,
				vboIndices);
		glContext.bufferData(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER,
						MyInt16Array.create(bufferI),
				WebGLRenderingContext.STREAM_DRAW);
		glContext.drawElements(WebGLRenderingContext.TRIANGLES, bufferL,
				WebGLRenderingContext.UNSIGNED_SHORT, 0);
		glContext.flush();


		glDisableVertexAttribArray(GLSL_ATTRIB_POSITION);
		glDisableVertexAttribArray(GLSL_ATTRIB_COLOR);
		glDisableVertexAttribArray(GLSL_ATTRIB_NORMAL);


		// // simple test
		//
		// enableAlphaTest();
		// enableBlending();
		// enableDash();
		// setDashTexture(Textures.DASH_DOTTED);
		// enableLighting();
		//
		// setColor(-1, -1, -1, -1);
		//
		// int length = 4;
		//
		// GLBuffer fbVertices = GLFactory.prototype.newBuffer();
		// fbVertices.allocate(length * 3);
		// fbVertices.put(0);
		// fbVertices.put(0);
		// fbVertices.put(0);
		//
		// fbVertices.put(0);
		// fbVertices.put(10);
		// fbVertices.put(0);
		//
		// fbVertices.put(10);
		// fbVertices.put(10);
		// fbVertices.put(0);
		//
		// fbVertices.put(10);
		// fbVertices.put(0);
		// fbVertices.put(0);
		//
		// fbVertices.setLimit(length * 3);
		//
		// GLBuffer fbNormals = GLFactory.prototype.newBuffer();
		// fbNormals.allocate(length * 3);
		// fbNormals.put(0);
		// fbNormals.put(0);
		// fbNormals.put(10);
		//
		// fbNormals.put(0);
		// fbNormals.put(0);
		// fbNormals.put(1);
		//
		// fbNormals.put(0);
		// fbNormals.put(0);
		// fbNormals.put(1);
		//
		// fbNormals.put(0);
		// fbNormals.put(0);
		// fbNormals.put(1);
		//
		// fbNormals.setLimit(length * 3);
		//
		// GLBuffer fbColors = GLFactory.prototype.newBuffer();
		// fbColors.allocate(length * 4);
		// fbColors.put(1);
		// fbColors.put(0);
		// fbColors.put(0);
		// fbColors.put(1);
		//
		// fbColors.put(0);
		// fbColors.put(1);
		// fbColors.put(0);
		// fbColors.put(1);
		//
		// fbColors.put(0);
		// fbColors.put(0);
		// fbColors.put(1);
		// fbColors.put(1);
		//
		// fbColors.put(0);
		// fbColors.put(0);
		// fbColors.put(0);
		// fbColors.put(1);
		//
		// fbColors.setLimit(length * 4);
		//
		// GLBuffer fbTextures = GLFactory.prototype.newBuffer();
		// fbTextures.allocate(length * 2);
		// fbTextures.put(0);
		// fbTextures.put(0);
		//
		// fbTextures.put(10);
		// fbTextures.put(0);
		//
		// fbTextures.put(10);
		// fbTextures.put(0);
		//
		// fbTextures.put(10);
		// fbTextures.put(0);
		//
		// fbTextures.setLimit(length * 2);
		//
		// // DRAW ELEMENTS
		//
		// glEnableVertexAttribArray(GLSL_ATTRIB_POSITION);
		// glEnableVertexAttribArray(GLSL_ATTRIB_COLOR);
		// glEnableVertexAttribArray(GLSL_ATTRIB_NORMAL);
		// glEnableVertexAttribArray(GLSL_ATTRIB_TEXTURE);
		//
		// int numBytes;
		//
		// glBindBuffer(vboVertices);
		// numBytes = length * 12; // 4 bytes per float * 3 coords per vertex
		// glBufferData(numBytes, fbVertices);
		// glVertexAttribPointer(GLSL_ATTRIB_POSITION, 3);
		//
		// glBindBuffer(vboColors);
		// numBytes = length * 16;
		// glBufferData(numBytes, fbColors);
		// glVertexAttribPointer(GLSL_ATTRIB_COLOR, 4);
		//
		// glBindBuffer(vboNormals);
		// numBytes = length * 12; // 4 bytes per float * 3 coords per vertex
		// glBufferData(numBytes, fbNormals);
		// glVertexAttribPointer(GLSL_ATTRIB_NORMAL, 3);
		//
		// glBindBuffer(vboTextureCoords);
		// numBytes = length * 8;
		// glBufferData(numBytes, fbTextures);
		// glVertexAttribPointer(GLSL_ATTRIB_TEXTURE, 2);
		//
		// // // elements
		// // IntBuffer indicesBuf;
		// // indicesBuf = IntBuffer.allocate(6);
		// // indicesBuf.put(0);
		// // indicesBuf.put(1);
		// // indicesBuf.put(2);
		// // indicesBuf.rewind();
		// // jogl.getGL2().glDrawElements(GL2.GL_TRIANGLES, 3,
		// GL.GL_UNSIGNED_INT,
		// // indicesBuf);
		// // indicesBuf.put(0);
		// // indicesBuf.put(2);
		// // indicesBuf.put(3);
		// // indicesBuf.rewind();
		// // jogl.getGL2().glDrawElements(GL2.GL_TRIANGLES, 3,
		// GL.GL_UNSIGNED_INT,
		// // indicesBuf);
		// // jogl.getGL2().glFlush();
		//
		// glDisableVertexAttribArray(GLSL_ATTRIB_POSITION);
		// glDisableVertexAttribArray(GLSL_ATTRIB_COLOR);
		// glDisableVertexAttribArray(GLSL_ATTRIB_NORMAL);
		// glDisableVertexAttribArray(GLSL_ATTRIB_TEXTURE);
	}

	private GLBuffer bufferV, bufferC, bufferN;
	private short[] bufferI;
	private int bufferL;

	private boolean bufferUpdated = false;

	private void updateBuffers() {

		if (bufferUpdated) {
			return;
		}

		bufferL = 0;
		ArrayList<Drawable3D> list = drawable3DLists
				.getList(Drawable3D.DRAW_TYPE_POINTS);
		for (Drawable3D d : list) {
			// App.debug("(" + d.getGeometryIndex() + ") " + d.getGeoElement());
			Geometry geometry = ((ManagerShaders) getGeometryManager())
					.getGeometry(d.getGeometryIndex());
			bufferL += geometry.getLength();
			// App.debug("" + geometry.getType());
		}
		// App.debug("" + lv);

		bufferV = GLFactory.prototype.newBuffer();
		bufferV.allocate(bufferL * 3);
		bufferC = GLFactory.prototype.newBuffer();
		bufferC.allocate(bufferL * 4);
		bufferN = GLFactory.prototype.newBuffer();
		bufferN.allocate(bufferL * 3);
		bufferI = new short[bufferL * 3];

		short index = 0;
		for (Drawable3D d : list) {
			// App.debug("(" + d.getGeometryIndex() + ") " + d.getGeoElement());
			Coords center = ((DrawPoint3D) d).getCenter();
			double radius = center.getW() * DrawPoint3D.DRAW_POINT_FACTOR
					/ view3D.getScale();
			Geometry geometry = ((ManagerShaders) getGeometryManager())
					.getGeometry(d.getGeometryIndex());
			GLBuffer vertices = geometry.getVertices();
			GLBuffer normals = geometry.getNormals();
			GColor color = d.getGeoElement().getObjectColor();
			for (int i = 0; i < geometry.getLength(); i++) {

				bufferV.put(center.getX() + vertices.get() * radius);
				bufferV.put(center.getY() + vertices.get() * radius);
				bufferV.put(center.getZ() + vertices.get() * radius);

				bufferC.put(color.getRed() / 255.0);
				bufferC.put(color.getGreen() / 255.0);
				bufferC.put(color.getBlue() / 255.0);
				bufferC.put(color.getAlpha() / 255.0);

				bufferN.put(normals.get());
				bufferN.put(normals.get());
				bufferN.put(normals.get());

				bufferI[index] = index;
				index++;
			}
			vertices.rewind();
			normals.rewind();

		}
		bufferV.setLimit(bufferL * 3);
		bufferC.setLimit(bufferL * 4);
		bufferN.setLimit(bufferL * 3);

		glBindBuffer(vboVertices);
		glBufferData(bufferL * 12, bufferV);
		glVertexAttribPointer(GLSL_ATTRIB_POSITION, 3);

		glBindBuffer(vboColors);
		glBufferData(bufferL * 16, bufferC);
		glVertexAttribPointer(GLSL_ATTRIB_COLOR, 4);

		glBindBuffer(vboNormals);
		glBufferData(bufferL * 12, bufferN);
		glVertexAttribPointer(GLSL_ATTRIB_NORMAL, 3);

		bufferUpdated = true;
	}

	protected void glEnableVertexAttribArray(int attrib) {
		glContext.enableVertexAttribArray(attrib);
	}
	
	protected void glDisableVertexAttribArray(int attrib) {
		glContext.disableVertexAttribArray(attrib);
	}

	protected void glBindBuffer(WebGLBuffer vbo) {
		glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, vbo);
	}

	protected void glBufferData(int length, GLBuffer buffer) {
		glBufferData(buffer);
	}

	protected void glVertexAttribPointer(int attrib, int size) {
		glContext.vertexAttribPointer(attrib, size,
	        WebGLRenderingContext.FLOAT, false, 0, 0);
	}

	@Override
	public void bindTexture(int index) {
	}
}
