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

package org.geogebra.common.euclidian.plot.implicit.classification;

import java.util.List;

import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.plot.implicit.ClippedFragment;
import org.geogebra.common.euclidian.plot.implicit.FragmentEndpoint;
import org.geogebra.common.kernel.MyPoint;

final class GraphBuildSignature {
	long of(GRectangle2D rect, List<ClippedFragment> fragments) {
		long signature = 0xcbf29ce484222325L;
		signature = mix(signature, Double.doubleToLongBits(rect.getMinX()));
		signature = mix(signature, Double.doubleToLongBits(rect.getMaxX()));
		signature = mix(signature, Double.doubleToLongBits(rect.getMinY()));
		signature = mix(signature, Double.doubleToLongBits(rect.getMaxY()));
		signature = mix(signature, fragments.size());
		for (ClippedFragment fragment : fragments) {
			signature = mix(signature, fragment.sourceContourId());
			signature = mix(signature, fragment.closed() ? 1 : 0);
			List<MyPoint> points = fragment.points();
			signature = mix(signature, points.size());
			for (MyPoint point : points) {
				signature = mix(signature, Double.doubleToLongBits(point.x));
				signature = mix(signature, Double.doubleToLongBits(point.y));
			}
			signature = mix(signature, endpointSignature(fragment.start()));
			signature = mix(signature, endpointSignature(fragment.end()));
		}
		return signature;
	}

	private long endpointSignature(FragmentEndpoint endpoint) {
		if (endpoint == null) {
			return 0x9e3779b97f4a7c15L;
		}
		long signature = 0x84222325cbf29ce4L;
		signature =
				mix(signature, endpoint.getEdge() == null ? -1 : endpoint.getEdge().ordinal());
		signature = mix(signature, Double.doubleToLongBits(endpoint.getPoint().x));
		signature = mix(signature, Double.doubleToLongBits(endpoint.getPoint().y));
		signature = mix(signature, Double.doubleToLongBits(endpoint.getSPerimeter()));
		return signature;
	}

	private long mix(long hash, long value) {
		long mixed = hash ^ value;
		mixed *= 0x100000001b3L;
		return mixed;
	}
}
