/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;

/**
 * Replaces all GeoSurfaceCartesian3D with MyVect3D with expressions of
 * surface
 *
 */
public final class GeoSurfaceReplacer implements Traversing {

	private static final GeoSurfaceReplacer replacer = new GeoSurfaceReplacer();

	private GeoSurfaceReplacer() {
		// singleton constructor
	}

	@Override
	public ExpressionValue process(ExpressionValue ev) {
		if (ev instanceof ExpressionNode) {
			ExpressionNode node = (ExpressionNode) ev;
			if (node.getLeft() instanceof GeoSurfaceCartesianND
					&& node.getRight() instanceof MyList) {
				GeoSurfaceCartesianND surface = (GeoSurfaceCartesianND) node
						.getLeft();
				FunctionNVar[] fun = surface.getFunctions();
				MyVecNDNode vect;
				if (fun.length > 2) {
					vect = new MyVec3DNode(
							((ExpressionNode) ev).getKernel(),
							fun[0].getExpression(),
							fun[1].getExpression(),
							fun[2].getExpression());
				} else {
					vect = new MyVecNode(
							((ExpressionNode) ev).getKernel(),
							fun[0].getExpression(),
							fun[1].getExpression());
				}
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