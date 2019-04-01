package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;

/**
 *
 */
@SuppressWarnings("serial")
class GeometriesSetElementsGlobalBuffer extends GeometriesSet {
	/**
	 * 
	 */
	private final ManagerShaders manager;

	/**
	 * @param manager
	 *            geometry manager
	 */
	GeometriesSetElementsGlobalBuffer(ManagerShaders manager) {
		this.manager = manager;
	}

	@Override
	protected Geometry newGeometry(Type type) {
		return new GeometryElementsGlobalBuffer(this.manager, type);
	}

	@Override
	public void bindGeometry(int size, TypeElement type) {
		((GeometryElementsGlobalBuffer) currentGeometry)
				.bind(this.manager.renderer, size, type);
	}

	/**
	 * remove GL buffers
	 */
	public void removeBuffers() {
		for (int i = 0; i < getGeometriesLength(); i++) {
			((GeometryElementsGlobalBuffer) get(i))
					.removeBuffers(this.manager.renderer);
		}

	}
}