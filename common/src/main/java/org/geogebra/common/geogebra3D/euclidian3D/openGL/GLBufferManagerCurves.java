package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.List;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;

/**
 * manager for packing buffers (for curves)
 */
public class GLBufferManagerCurves extends GLBufferManager {

	// complex materials need more than 1500
	static final private int ELEMENTS_SIZE_START = 2048;
	// for now on, only segments are drawn here, so we count 1 triangle per vertex,
	// and 3 indices per triangle
	static final private int INDICES_SIZE_START = ELEMENTS_SIZE_START * 3;

	private ManagerShaders manager;

	/**
	 * 
	 * @param manager
	 *            manager
	 */
	public GLBufferManagerCurves(ManagerShaders manager) {
		this.manager = manager;
	}

	@Override
	protected int calculateIndicesLength(int size, TypeElement type) {
		switch (type) {
		case CURVE:
			return 3 * 2 * size * PlotterBrush.LATITUDES;
		case TRIANGLES:
			return 3 * size;
		case TEMPLATE:
			return size;
		default: // should not happen
			return 0;
		}
	}

	@Override
	protected void putIndices(int size, TypeElement type,
			boolean reuseSegment) {
		switch (type) {
		case CURVE:
			putToIndicesForCurve(size);
			break;
		case TRIANGLES:
			for (int i = 0; i < 3 * size; i++) {
				putToIndices(i);
			}
			break;
		case TEMPLATE:
			List<Short> indicesArray = manager.getBufferTemplates()
					.getCurrentIndicesArray();
			for (short i : indicesArray) {
				putToIndices(i);
			}
			break;
		default: // should not happen
			break;
		}
	}

	/**
	 * draw
	 * 
	 * @param r
	 *            renderer
	 * @param hidden
	 *            if hidden
	 */
	public void draw(Renderer r, boolean hidden) {
		((TexturesShaders) r.getTextures()).setPackedDash();
		r.getRendererImpl().setDashTexture(
				hidden ? Textures.DASH_PACKED_HIDDEN : Textures.DASH_PACKED);
		drawBufferPacks(r);
	}

	@Override
	protected int getElementSizeStart() {
		return ELEMENTS_SIZE_START;
	}

	@Override
	protected int getIndicesSizeStart() {
		return INDICES_SIZE_START;
	}

	/**
	 * draw a point
	 * 
	 */
	public void drawPoint() {
		manager.getBufferTemplates().drawSphere(manager);
	}

	@Override
	protected void setElements(boolean reuseSegment, TypeElement type) {
		if (type == TypeElement.TEMPLATE) {
			currentBufferPack.setElements(manager.getTranslate(),
					manager.getScale(), reuseSegment);
		} else {
			super.setElements(reuseSegment, type);
		}
	}

	@Override
	protected boolean checkCurrentBufferSegmentDoesNotFit(int indicesLength,
			TypeElement type) {
		return type != currentBufferSegment.type
				|| super.checkCurrentBufferSegmentDoesNotFit(indicesLength,
						type);
	}

}
