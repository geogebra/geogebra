package org.geogebra.common.kernel.geos;

import org.geogebra.common.euclidian.draw.ArrowVectorShape;
import org.geogebra.common.euclidian.draw.DefaultVectorShape;
import org.geogebra.common.euclidian.draw.DrawVectorModel;
import org.geogebra.common.euclidian.draw.VectorShape;

/**
 * Vector decorations.
 */
public enum VectorHeadStyle {
	DEFAULT {
		@Override
		public VectorShape createShape(DrawVectorModel model) {
			return new DefaultVectorShape(model);
		}
	}, ARROW {
		@Override
		public VectorShape createShape(DrawVectorModel model) {
			return new ArrowVectorShape(model);
		}
	};

	/**
	 * @param model vector model
	 * @return vector shape
	 */
	public abstract VectorShape createShape(DrawVectorModel model);
}
