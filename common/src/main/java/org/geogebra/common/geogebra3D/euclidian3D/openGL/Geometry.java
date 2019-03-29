package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;

/**
 * Geometry (set of vertices, normals, etc. for e.g. triangles)
 * 
 * @author mathieu
 *
 */
abstract public class Geometry {
	/**
	 * type of primitives
	 */
	protected Type type;

	protected GLBuffer v;
	protected GLBuffer n;
	protected GLBuffer t;
	protected GLBuffer c;

	private int length;

	/**
	 * Start a new geometry
	 * 
	 * @param type
	 *            of primitives
	 */
	public Geometry(Type type) {
		this.type = type;
		setBuffers();
	}

	/**
	 * set the buffers
	 */
	protected void setBuffers() {
		this.v = GLFactory.getPrototype().newBuffer();
		this.n = GLFactory.getPrototype().newBuffer();
		this.t = GLFactory.getPrototype().newBuffer();
		this.c = GLFactory.getPrototype().newBuffer();
	}

	/**
	 * set the geometry type and mark buffers as empty
	 * 
	 * @param type
	 *            geometry type
	 */
	public void setType(Type type) {
		this.type = type;
		this.v.setEmpty();
		this.n.setEmpty();
		this.t.setEmpty();
		this.c.setEmpty();
	}

	/**
	 * allocate memory for buffers (for direct write)
	 * 
	 * @param size
	 *            vertices size
	 */
	public void allocateBuffers(int size) {
		// Log.debug("allocateBuffers: "+size);
		v.allocate(size * 3);
		n.allocate(size * 3);
		length = 0;
	}

	/**
	 * put vertex values into buffer
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public void vertexDirect(double x, double y, double z) {
		v.put(x);
		v.put(y);
		v.put(z);
		length++;
	}

	/**
	 * put normal values into buffer
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public void normalDirect(double x, double y, double z) {
		n.put(x);
		n.put(y);
		n.put(z);
	}

	/**
	 * ends geometry
	 */
	public void end() {
		v.setLimit(length * 3);
		n.setLimit(length * 3);
	}

	/**
	 * 
	 * @return type of primitives
	 */
	public Type getType() {
		return type;
	}

	/**
	 * set double buffer for vertices
	 * 
	 * @param array
	 *            double array
	 * @param length
	 *            length to copy
	 */
	public void setVertices(ArrayList<Double> array, int length) {
		// this.v = GLFactory.prototype.newBuffer();
		this.v.set(array, length);
	}

	/**
	 * 
	 * @return vertices buffer
	 */
	public GLBuffer getVertices() {
		return v;
	}

	/**
	 * set double buffer for normals
	 * 
	 * @param array
	 *            double array
	 * @param length
	 *            length to copy
	 */
	public void setNormals(ArrayList<Double> array, int length) {
		this.n.set(array, length);
	}

	/**
	 * 
	 * @return normals buffer
	 */
	public GLBuffer getNormals() {
		return n;
	}

	/**
	 * set double buffer for texture
	 * 
	 * @param array
	 *            double array
	 * @param length
	 *            length to copy
	 */
	public void setTextures(ArrayList<Double> array, int length) {
		this.t.set(array, length);
	}

	/**
	 * set textures to empty
	 */
	public void setTexturesEmpty() {
		this.t.setEmpty();
	}

	/**
	 * set color to empty
	 */
	public void setColorsEmpty() {
		// not used here
	}

	/**
	 * 
	 * @return texture buffer
	 */
	public GLBuffer getTextures() {
		return t;
	}

	/**
	 * set double buffer for colors
	 * 
	 * @param array
	 *            double array
	 * @param length
	 *            length to copy
	 */
	public void setColors(ArrayList<Double> array, int length) {
		this.c.set(array, length);
	}

	/**
	 * 
	 * @return colors buffer
	 */
	public GLBuffer getColors() {
		return c;
	}

	/**
	 * set vertices length
	 * 
	 * @param l
	 *            vertices length
	 */
	public void setLength(int l) {
		this.length = l;
	}

	/**
	 * 
	 * @return vertices length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * draw to renderer
	 * 
	 * @param r
	 *            renderer to draw into
	 */
	public void draw(Renderer r) {
		r.getRendererImpl().loadVertexBuffer(getVertices(), getLength());
		r.getRendererImpl().loadNormalBuffer(getNormals(), getLength());
		r.getRendererImpl().loadColorBuffer(getColors(), getLength());
		if (r.getRendererImpl().areTexturesEnabled()) {
			r.getRendererImpl().loadTextureBuffer(getTextures(),
					getLength());
		}
		r.getRendererImpl().draw(getType(), getLength());
	}

	/**
	 * draw as label to renderer
	 * 
	 * @param r
	 *            renderer to draw into
	 */
	public void drawLabel(Renderer r) {
		r.getRendererImpl().loadVertexBuffer(getVertices(), getLength());
		if (r.getRendererImpl().areTexturesEnabled()) {
			r.getRendererImpl().loadTextureBuffer(getTextures(),
					getLength());
		}
		r.getRendererImpl().draw(getType(), getLength());
	}

}