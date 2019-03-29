package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D.GeometryForExport;
import org.geogebra.common.util.debug.Log;

public class GeometryElementsGlobalBuffer extends Geometry implements GeometryForExport {

	/**
	 * 
	 */
	private final ManagerShadersElementsGlobalBuffer manager;

	private GLBufferIndices arrayI = null;

	private int indicesLength;

	private boolean hasSharedIndexBuffer = false;

	public GeometryElementsGlobalBuffer(
			ManagerShadersElementsGlobalBuffer manager, Type type) {
		super(type);
		this.manager = manager;
	}

	/**
	 * remove buffers
	 * 
	 * @param r
	 *            GL renderer
	 */
	public void removeBuffers(Renderer r) {
		// TODO not needed?
	}

	/**
	 * bind the geometry to its GL buffer
	 * 
	 * @param r
	 *            renderer
	 * @param size
	 *            indices size
	 * @param typeElement
	 *            type for elements indices
	 */
	public void bind(Renderer r, int size,
			TypeElement typeElement) {

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

			if (!manager.indicesDone || typeElement != manager.oldType
					|| arrayI.capacity() < indicesLength) {
				ManagerShadersElementsGlobalBuffer.debug("NEW index buffer");
				arrayI.allocate(indicesLength);
				for (short i = 0; i < indicesLength; i++) {
					arrayI.put(i);
				}
				arrayI.rewind();
				manager.indicesDone = true;
			} else {
				ManagerShadersElementsGlobalBuffer.debug("keep same index buffer");
			}

			hasSharedIndexBuffer = false;
			break;

		case CURVE:
			ManagerShadersElementsGlobalBuffer.debug("curve: shared index buffer");
			arrayI = manager.getBufferIndicesForCurve(r, size);
			indicesLength = 3 * 2 * size * PlotterBrush.LATITUDES;
			hasSharedIndexBuffer = true;
			// debug("curve: NOT shared index buffer");
			// bufferI = getBufferIndicesForCurve(bufferI, r, size,
			// indicesLength / (3 * 2 * PlotterBrush.LATITUDES));
			// indicesLength = 3 * 2 * size * PlotterBrush.LATITUDES;
			// hasSharedIndexBuffer = false;
			break;

		case SURFACE:
			ManagerShadersElementsGlobalBuffer.debug("surface -- keep same index buffer");
			indicesLength = size;
			hasSharedIndexBuffer = false;
			break;

		case FAN_DIRECT:
			ManagerShadersElementsGlobalBuffer.debug("fan direct: shared index buffer");
			arrayI = manager.getBufferIndicesForFanDirect(r, size);
			indicesLength = 3 * (size - 2);
			hasSharedIndexBuffer = true;
			break;
		case FAN_INDIRECT:
			ManagerShadersElementsGlobalBuffer.debug("fan indirect: shared index buffer");
			arrayI = manager.getBufferIndicesForFanIndirect(r, size);
			indicesLength = 3 * (size - 2);
			hasSharedIndexBuffer = true;
			break;
		default:
			Log.debug("Missing case: " + typeElement);
		}

		manager.oldType = typeElement;
	}

	@Override
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

	@Override
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