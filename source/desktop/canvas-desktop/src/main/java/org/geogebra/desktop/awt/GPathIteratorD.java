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

package org.geogebra.desktop.awt;

import java.awt.geom.PathIterator;

import org.geogebra.common.awt.GPathIterator;

public class GPathIteratorD implements GPathIterator {

	PathIterator impl;

	public GPathIteratorD(PathIterator pathIterator) {
		impl = pathIterator;
	}

	@Override
	public int getWindingRule() {
		return impl.getWindingRule();
	}

	@Override
	public boolean isDone() {
		return impl.isDone();
	}

	@Override
	public void next() {
		impl.next();
	}

	@Override
	public int currentSegment(double[] coords) {
		return impl.currentSegment(coords);
	}

}
