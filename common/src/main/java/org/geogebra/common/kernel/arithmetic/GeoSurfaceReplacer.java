package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSurfaceCartesian3D;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;

/**
 * Replaces all GeoSurfaceCartesian3D with MyVect3D with expressions of
 * surface
 *
 */
public class GeoSurfaceReplacer implements Traversing {

	private static final GeoSurfaceReplacer replacer = new GeoSurfaceReplacer();

	@Override
	public ExpressionValue process(ExpressionValue ev) {
		if (ev instanceof ExpressionNode) {
			ExpressionNode node = (ExpressionNode) ev;
			if (node.getLeft() instanceof GeoSurfaceCartesian3D
					&& node.getRight() instanceof MyList) {
				GeoSurfaceCartesian3D surface = (GeoSurfaceCartesian3D) node
						.getLeft();
				FunctionNVar[] fun = surface.getFunctions();
				MyVec3DNode vect = new MyVec3DNode(
						((ExpressionNode) ev).getKernel(),
						fun[0].getExpression(), fun[1].getExpression(),
						fun[2].getExpression());
				return new ExpressionNode(((ExpressionNode) ev).getKernel(),
						vect);
			}

		}
		return ev;
	}

	/**
	 * @return instance of this traversing
	 */
	public static GeoSurfaceReplacer getInstance() {
		return replacer;
	}
}