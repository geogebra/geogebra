package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * manager for packing buffers (for curves)
 */
public class GLBufferManagerTemplatesForPoints extends GLBufferManager {

	// we don't use buffers here
	static final private int ELEMENTS_SIZE_START = 0;
	static final private int INDICES_SIZE_START = 0;

	/**
	 * number of templates for points
	 */
	final static private int POINT_TEMPLATES_COUNT = 3;

	private GLBufferIndicesArray bufferIndicesArray;

	private ArrayList<Double>[] vertexTemplates;
	private ArrayList<Double>[] normalTemplates;
	private ArrayList<Short>[] indicesTemplates;

	private ArrayList<Double> currentVertexArray;
	private ArrayList<Double> currentNormalArray;
	private ArrayList<Short> currentIndicesArray;

	/**
	 * 
	 * @param pointSize
	 *            point size
	 * @return template index for this size
	 */
	static public int getIndexForPointSize(float pointSize) {
		return pointSize < 2.5f ? 0 : (pointSize > 5.5f ? 2 : 1);
	}

	/**
	 * 
	 * @param index
	 *            template index
	 * @return sphere size for template index
	 */
	static public int getSphereSizeForIndex(int index) {
		switch (index) {
		case 0:
			return 2;
		case 1:
			return 4;
		default:
			return 7;
		}
	}

	/**
	 * constructor
	 */
	@SuppressWarnings("unchecked")
	public GLBufferManagerTemplatesForPoints() {
		vertexTemplates = new ArrayList[POINT_TEMPLATES_COUNT];
		normalTemplates = new ArrayList[POINT_TEMPLATES_COUNT];
		indicesTemplates = new ArrayList[POINT_TEMPLATES_COUNT];
	}

	@Override
	protected int calculateIndicesLength(int size, TypeElement type) {
		// not used
		return size;
	}

	@Override
	protected void putIndices(int size, TypeElement type,
			boolean reuseSegment) {
		// not used
	}

	/**
	 * draw
	 * 
	 * @param r
	 *            renderer
	 */
	public void draw(Renderer r) {
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
	 * 
	 * @return new indices array
	 */
	public GLBufferIndicesArray getBufferIndicesArray() {
		bufferIndicesArray = new GLBufferIndicesArray(0);
		return bufferIndicesArray;
	}

	/**
	 * 
	 * @return current indices array
	 */
	public List<Short> getCurrentIndicesArray() {
		return currentIndicesArray;
	}

	@Override
	public void setIndices(int size, TypeElement type) {
		// not used
	}

	@Override
	public boolean isTemplateForPoints() {
		return true;
	}

	/**
	 * 
	 * @param manager
	 *            manager
	 * @param pointSize
	 *            point size
	 */
	public void createSphereIfNeeded(ManagerShaders manager, int pointSize) {

		int templateIndex = getIndexForPointSize(pointSize);

		currentVertexArray = vertexTemplates[templateIndex];
		if (currentVertexArray == null) {
			createSphere(manager, templateIndex);
		}
	}

	private void createSphere(ManagerShaders manager, int templateIndex) {

		manager.setScalerIdentity();
		manager.drawSphere(getSphereSizeForIndex(templateIndex),
				Coords.O, 1d, -1);
		manager.setScalerView();

		currentVertexArray = new ArrayList<>();
		for (int i = 0; i < elementsLength * 3; i++) {
			currentVertexArray.add(vertexArray.get(i));
		}
		vertexTemplates[templateIndex] = currentVertexArray;

		currentNormalArray = new ArrayList<>();
		for (int i = 0; i < elementsLength * 3; i++) {
			currentNormalArray.add(normalArray.get(i));
		}
		normalTemplates[templateIndex] = currentNormalArray;

		currentIndicesArray = new ArrayList<>();
		currentIndicesArray.addAll(bufferIndicesArray);
		indicesTemplates[templateIndex] = currentIndicesArray;

		vertexArray = null;
		normalArray = null;
		bufferIndicesArray = null;
	}

	/**
	 * select template according to point size
	 * 
	 * @param manager
	 *            manager
	 * @param pointSize
	 *            point size
	 */
	public void selectSphereAndCreateIfNeeded(ManagerShaders manager,
			float pointSize) {

		int templateIndex = getIndexForPointSize(pointSize);
		currentVertexArray = vertexTemplates[templateIndex];
		if (currentVertexArray == null) {
			createSphere(manager, templateIndex);
		} else {
			elementsLength = currentVertexArray.size() / 3;
			currentNormalArray = normalTemplates[templateIndex];
			currentIndicesArray = indicesTemplates[templateIndex];
		}
	}

	/**
	 * select sphere corresponding to point size. WARNING: geometries must have
	 * been created first
	 * 
	 * @param pointSize
	 *            point size
	 */
	public void selectSphere(int pointSize) {
		int templateIndex = getIndexForPointSize(pointSize);
		currentVertexArray = vertexTemplates[templateIndex];
		elementsLength = currentVertexArray.size() / 3;
		currentNormalArray = normalTemplates[templateIndex];
		currentIndicesArray = indicesTemplates[templateIndex];
	}

	/**
	 * draw current sphere
	 * 
	 * @param manager
	 *            manager
	 */
	public void drawSphere(ManagerShaders manager) {
		manager.startGeometry(Manager.Type.TRIANGLES);
		manager.endGeometry(currentIndicesArray.size(), elementsLength,
				currentVertexArray, currentNormalArray);
		manager.endList();
	}

}
