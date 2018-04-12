package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.List;

import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * manager for packing buffers (for curves)
 */
public class GLBufferManagerPoints extends GLBufferManager {

	// regular point is 98 vertices (others are 26 or 402), we start with up to
	// 20 points = 1960 vertices
	static final private int ELEMENTS_SIZE_START = 1960;
	// regular point is 576 indices (others are 144 or 2400), we start with up
	// to 20 points = 11520 indices
	static final private int INDICES_SIZE_START = 11520;

	private ManagerShadersElementsGlobalBufferPacking manager;
	private GLBufferManagerTemplatesForPoints bufferTemplates;

	private float[] translate;
	private float scale;

	/**
	 * constructor
	 * 
	 * @param manager
	 *            geometries manager
	 */
	public GLBufferManagerPoints(
			ManagerShadersElementsGlobalBufferPacking manager) {
		this.manager = manager;
		bufferTemplates = new GLBufferManagerTemplatesForPoints();
		translate = new float[3];
	}

	@Override
	protected int calculateIndicesLength(int size, TypeElement type) {
		return size;
	}

	@Override
	protected void putIndices(int size, TypeElement type,
			boolean reuseSegment) {
		if (!reuseSegment) {
			List<Short> indicesArray = bufferTemplates.getCurrentIndicesArray();
			for (short i : indicesArray) {
				putToIndices(i);
			}
		}
	}

	/**
	 * draw
	 * 
	 * @param r
	 *            renderer
	 */
	public void draw(RendererShadersInterface r) {
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

	@Override
	protected void setElements(boolean reuseSegment) {
		currentBufferPack.setElements(translate, scale, reuseSegment);
	}

	/**
	 * 
	 * @param d
	 *            point drawable
	 * @param size
	 *            point size
	 * @param center
	 *            point center
	 * @param index
	 *            old geometry index
	 * @return point geometry index
	 */
	public int drawPoint(DrawPoint3D d, int size, Coords center, int index) {

		// get/create point geometry with template buffer
		manager.setCurrentBufferManager(bufferTemplates);
		bufferTemplates.selectSphere(manager, size);
		manager.setCurrentBufferManager(this);

		// copy into this buffer
		scale = size * DrawPoint3D.DRAW_POINT_FACTOR;
		manager.scaleXYZ(center);
		center.get(translate);
		int ret = bufferTemplates.drawSphere(manager, index);

		manager.setCurrentBufferManager(null);
		return ret;
	}

	/**
	 * 
	 * @return buffer templates
	 */
	public GLBufferManagerTemplatesForPoints getBufferTemplates() {
		return bufferTemplates;
	}

}
