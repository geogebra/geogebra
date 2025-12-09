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

package org.geogebra.desktop.export.pstricks;

import java.awt.image.BufferedImage;

import org.geogebra.common.awt.GShape;
import org.geogebra.common.export.pstricks.GeoGebraExport;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.desktop.awt.GGraphics2DD;

// To avoid duplicate inequalities drawing algorithms replacing Graphics.
// In the three implementations (pstricks, pgf, asymptote) print the
// appropriate commands
final class ExportGraphicsD extends GGraphics2DD {

	private final GeoGebraExport export;
	private final Inequality ineq;
	private final FunctionalNVar geo;

	public ExportGraphicsD(FunctionalNVar geo, Inequality ineq, GeoGebraExport export) {

		// dummy canvas
		super(new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB)
				.createGraphics());

		this.geo = geo;
		this.ineq = ineq;
		this.export = export;
	}

	@Override
	public void fill(GShape s) {
		export.fillIneq(s, ineq, geo);
	}
}