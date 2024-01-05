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