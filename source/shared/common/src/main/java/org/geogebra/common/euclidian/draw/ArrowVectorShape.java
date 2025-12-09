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

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GShape;

public class ArrowVectorShape implements VectorShape {
	private final GLine2D line;
	private final DrawVectorModel model;

	/**
	 *
	 * @param model {@link DrawVectorModel}
	 */
	public ArrowVectorShape(DrawVectorModel model) {
		this.model = model;
		line = AwtFactory.getPrototype().newLine2D();
	}

	@Override
	public DrawVectorModel model() {
		return model;
	}

	@Override
	public GLine2D body() {
		line.setLine(model.getStartX(), model.getStartY(),
				model.getEndX(), model.getEndY());
		return line;
	}

	@Override
	public GShape head() {
		RotatedArrow rotatedArrow = new RotatedArrow(line, model.getLineThickness(),
				model.getStroke());
		return rotatedArrow.get();
	}

	@Override
	public GLine2D clipLine(int width, int height) {
		return body();
	}
}
