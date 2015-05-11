package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Textures;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererJogl.GL2ES2;

/**
 * renderer using shaders and drawElements()
 * 
 * @author mathieu
 *
 */
public class RendererShadersElements extends RendererShaders {

	/**
	 * constructor
	 * 
	 * @param view
	 *            3D view
	 * @param useCanvas
	 *            say if we use GLCanvas or GLJPanel
	 */
	public RendererShadersElements(EuclidianView3D view, boolean useCanvas) {
		super(view, useCanvas);
	}


	protected void draw() {

		resetOneNormalForAllVertices();
		disableTextures();

		setModelViewIdentity();


		// init drawing matrix to view3D toScreen matrix
		setMatrixView();

		setLightPosition();
		setLight(0);
		disableCulling();

		// setColor(1, 0, 0, 1);
		// setColor(0, 0, 1, 1);
		setColor(-1, -1, -1, -1);

		// disableLighting();

		enableAlphaTest();
		enableBlending();
		enableDash();
		setDashTexture(Textures.DASH_DOTTED);
		// enableFading();

		// ////////////
		// test


		int length = 4;

		GLBufferD fbVertices = new GLBufferD();
		fbVertices.allocate(length * 3);
		fbVertices.put(0);
		fbVertices.put(0);
		fbVertices.put(0);

		fbVertices.put(0);
		fbVertices.put(10);
		fbVertices.put(0);

		fbVertices.put(10);
		fbVertices.put(10);
		fbVertices.put(0);

		fbVertices.put(10);
		fbVertices.put(0);
		fbVertices.put(0);

		fbVertices.setLimit(length * 3);


		GLBufferD fbNormals = new GLBufferD();
		fbNormals.allocate(length * 3);
		fbNormals.put(0);
		fbNormals.put(0);
		fbNormals.put(10);

		fbNormals.put(0);
		fbNormals.put(0);
		fbNormals.put(1);

		fbNormals.put(0);
		fbNormals.put(0);
		fbNormals.put(1);

		fbNormals.put(0);
		fbNormals.put(0);
		fbNormals.put(1);

		fbNormals.setLimit(length * 3);

		GLBufferD fbColors = new GLBufferD();
		fbColors.allocate(length * 4);
		fbColors.put(1);
		fbColors.put(0);
		fbColors.put(0);
		fbColors.put(1);

		fbColors.put(0);
		fbColors.put(1);
		fbColors.put(0);
		fbColors.put(1);

		fbColors.put(0);
		fbColors.put(0);
		fbColors.put(1);
		fbColors.put(1);

		fbColors.put(0);
		fbColors.put(0);
		fbColors.put(0);
		fbColors.put(1);

		fbColors.setLimit(length * 4);

		GLBufferD fbTextures = new GLBufferD();
		fbTextures.allocate(length * 2);
		fbTextures.put(0);
		fbTextures.put(0);

		fbTextures.put(10);
		fbTextures.put(0);

		fbTextures.put(10);
		fbTextures.put(0);

		fbTextures.put(10);
		fbTextures.put(0);

		fbTextures.setLimit(length * 2);

		// DRAW ELEMENTS

		jogl.getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_POSITION);
		jogl.getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_COLOR);
		jogl.getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_NORMAL);
		jogl.getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_TEXTURE);

		int numBytes;

		jogl.getGL2ES2().glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vboVertices);
		numBytes = length * 12; // 4 bytes per float * 3 coords per vertex
		glBufferData(numBytes, fbVertices);
		jogl.getGL2ES2().glVertexAttribPointer(GLSL_ATTRIB_POSITION, 3,
				GL2ES2.GL_FLOAT, false, 0, 0);

		jogl.getGL2ES2().glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vboColors);
		numBytes = length * 16;
		glBufferData(numBytes, fbColors);
		jogl.getGL2ES2().glVertexAttribPointer(GLSL_ATTRIB_COLOR, 4,
				GL2ES2.GL_FLOAT, false, 0, 0);

		jogl.getGL2ES2().glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vboNormals);
		numBytes = length * 12; // 4 bytes per float * 3 coords per vertex
		glBufferData(numBytes, fbNormals);
		jogl.getGL2ES2().glVertexAttribPointer(GLSL_ATTRIB_NORMAL, 3,
				GL2ES2.GL_FLOAT, false, 0, 0);

		jogl.getGL2ES2().glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vboTextureCoords);
		numBytes = length * 8;
		glBufferData(numBytes, fbTextures);
		jogl.getGL2ES2().glVertexAttribPointer(GLSL_ATTRIB_TEXTURE, 2,
				GL2ES2.GL_FLOAT, false, 0, 0);


		// elements
		int indices[] = new int[] { 0, 1, 2, 0, 2, 3 };
		IntBuffer indicesBuf = IntBuffer.allocate(indices.length);
		for (int i = 0; i < indices.length; i++)
			indicesBuf.put(indices[i]);
		indicesBuf.rewind();
		jogl.getGL2().glDrawElements(GL2.GL_TRIANGLES, 6, GL.GL_UNSIGNED_INT,
				indicesBuf);
		jogl.getGL2().glFlush();



		jogl.getGL2ES2().glDisableVertexAttribArray(GLSL_ATTRIB_POSITION);
		jogl.getGL2ES2().glDisableVertexAttribArray(GLSL_ATTRIB_COLOR);
		jogl.getGL2ES2().glDisableVertexAttribArray(GLSL_ATTRIB_NORMAL);
		jogl.getGL2ES2().glDisableVertexAttribArray(GLSL_ATTRIB_TEXTURE);
	}


}
