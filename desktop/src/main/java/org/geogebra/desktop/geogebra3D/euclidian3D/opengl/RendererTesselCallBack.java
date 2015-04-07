package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import javax.media.opengl.glu.GLUtessellatorCallback;

import org.geogebra.desktop.main.AppD;

public class RendererTesselCallBack implements GLUtessellatorCallback {
	private RendererGL2 renderer;

	public RendererTesselCallBack(RendererGL2 renderer) {
		this.renderer = renderer;
	}

	public void begin(int type) {
		renderer.jogl.getGL2().glBegin(type);
	}

	public void end() {
		renderer.jogl.getGL2().glEnd();
	}

	public void vertex(Object vertexData) {

		double[] pointer;
		if (vertexData instanceof double[]) {
			pointer = (double[]) vertexData;
			// if (pointer.length == 6) jogl.getGL2().glColor3dv(pointer, 3);
			renderer.jogl.getGL2().glVertex3dv(pointer, 0);
		}

	}

	public void vertexData(Object vertexData, Object polygonData) {
	}

	/*
	 * combineCallback is used to create a new vertex when edges intersect.
	 * coordinate location is trivial to calculate, but weight[4] may be used to
	 * average color, normal, or texture coordinate data. In this program, color
	 * is weighted.
	 */
	public void combine(double[] coords, Object[] data, //
			float[] weight, Object[] outData) {
		// double[] vertex = new double[6];
		// int i;

		double[] vertex = new double[3];

		vertex[0] = coords[0];
		vertex[1] = coords[1];
		vertex[2] = coords[2];
		/*
		 * for (i = 3; i < 6; i++) vertex[i] = weight[0] // ((double[])
		 * data[0])[i] + weight[1] ((double[]) data[1])[i] + weight[2]
		 * ((double[]) data[2])[i] + weight[3] ((double[]) data[3])[i];
		 */
		outData[0] = vertex;
	}

	public void combineData(double[] coords, Object[] data, //
			float[] weight, Object[] outData, Object polygonData) {
	}

	public void error(int errnum) {
		String estring;

		estring = renderer.glu.gluErrorString(errnum);
		AppD.debug("Tessellation Error: " + estring);
		// System.exit(0);
	}

	public void beginData(int type, Object polygonData) {
	}

	public void endData(Object polygonData) {
	}

	public void edgeFlag(boolean boundaryEdge) {
	}

	public void edgeFlagData(boolean boundaryEdge, Object polygonData) {
	}

	public void errorData(int errnum, Object polygonData) {
	}
}
