package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D.GeometryForExport;
import org.geogebra.common.util.debug.Log;

/**
 * Geometry (set of vertices, normals, etc. for e.g. triangles)
 * 
 * @author mathieu
 *
 */
public class Geometry implements GeometryForExport {

	private final ManagerShaders manager;
	private Type type;
	private GLBuffer v;
	private GLBuffer n;
	private GLBuffer t;
	private GLBuffer c;
	private int length;
	private GLBufferIndices arrayI = null;
	private int indicesLength;
	private boolean hasSharedIndexBuffer = false;

	/**
	 * Start a new geometry
	 * 
	 * @param manager
	 *            manager
	 * @param type
	 *            of primitives
	 */
	public Geometry(ManagerShaders manager, Type type) {
		this.manager = manager;
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
	 * @return type of primitives
	 */
	@Override
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

		if (arrayI == null) {
			return;
		}

		r.getRendererImpl().loadVertexBuffer(getVertices(), getLength());
		r.getRendererImpl().loadNormalBuffer(getNormals(), getLength());
		r.getRendererImpl().loadColorBuffer(getColors(), getLength());
		if (r.getRendererImpl().areTexturesEnabled()) {
			r.getRendererImpl().loadTextureBuffer(getTextures(),
					getLength());
		} else {
			r.getRendererImpl().disableTextureBuffer();
		}
		r.getRendererImpl().loadIndicesBuffer(arrayI, indicesLength);
		r.getRendererImpl().draw(getType(), indicesLength);

	}

	/**
	 * draw as label to renderer
	 * 
	 * @param r
	 *            renderer to draw into
	 */
	public void drawLabel(Renderer r) {

		if (arrayI == null) {
			return;
		}

		r.getRendererImpl().loadVertexBuffer(getVertices(), getLength());
		if (r.getRendererImpl().areTexturesEnabled()) {
			r.getRendererImpl().loadTextureBuffer(getTextures(),
					getLength());
		}
		r.getRendererImpl().loadIndicesBuffer(arrayI, indicesLength);
		r.getRendererImpl().draw(getType(), indicesLength);
	}

	/**
	 * bind the geometry to its GL buffer
	 * 
	 * @param size
	 *            indices size
	 * @param typeElement
	 *            type for elements indices
	 */
	public void bind(int size, TypeElement typeElement) {

		switch (typeElement) {
		case NONE:
			if (hasSharedIndexBuffer) {
				// need specific index if was sharing one
				arrayI = null;
			}
			if (arrayI == null) {
				arrayI = GLFactory.getPrototype().newBufferIndices();
			}

			indicesLength = getLength();

			if (!manager.getIndicesDone() || typeElement != manager.getOldType()
					|| arrayI.capacity() < indicesLength) {
				arrayI.allocate(indicesLength);
				for (short i = 0; i < indicesLength; i++) {
					arrayI.put(i);
				}
				arrayI.rewind();
				manager.setIndicesDone(true);
			}

			hasSharedIndexBuffer = false;
			break;

		case CURVE:
			arrayI = manager.getBufferIndicesForCurve(size);
			indicesLength = 3 * 2 * size * PlotterBrush.LATITUDES;
			hasSharedIndexBuffer = true;
			break;

		case SURFACE:
			indicesLength = size;
			hasSharedIndexBuffer = false;
			break;

		case FAN_DIRECT:
			arrayI = manager.getBufferIndicesForFanDirect(size);
			indicesLength = 3 * (size - 2);
			hasSharedIndexBuffer = true;
			break;
		case FAN_INDIRECT:
			arrayI = manager.getBufferIndicesForFanIndirect(size);
			indicesLength = 3 * (size - 2);
			hasSharedIndexBuffer = true;
			break;
		default:
			Log.debug("Missing case: " + typeElement);
		}

		manager.setOldType(typeElement);
	}

	/**
	 * 
	 * @param size
	 *            size
	 * @return indices buffer with correct size
	 */
	public GLBufferIndices getBufferI(int size) {
		if (arrayI == null || hasSharedIndexBuffer) {
			arrayI = GLFactory.getPrototype().newBufferIndices();
		}
		arrayI.allocate(size);
		return arrayI;
	}

	@Override
	public void initForExport() {
		// no need here
	}

	@Override
	public GLBufferIndices getBufferIndices() {
		return arrayI;
	}

	@Override
	public int getIndicesLength() {
		return indicesLength;
	}

	@Override
	public int getElementsOffset() {
		return 0; // no offset here
	}

	@Override
	public int getLengthForExport() {
		return getLength();
	}

	@Override
	public GLBuffer getVerticesForExport() {
		return getVertices();
	}

	@Override
	public GLBuffer getNormalsForExport() {
		return getNormals();
	}

}