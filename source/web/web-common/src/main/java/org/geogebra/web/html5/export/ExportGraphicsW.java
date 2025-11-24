package org.geogebra.web.html5.export;

import org.geogebra.common.awt.GShape;
import org.geogebra.common.export.pstricks.GeoGebraExport;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.web.awt.GGraphics2DW;
import org.gwtproject.canvas.client.Canvas;

// To avoid duplicate inequalities drawing algorithms replacing Graphics.
// In the three implementations (pstricks, pgf, asymptote) print the
// appropriate commands
final class ExportGraphicsW extends GGraphics2DW {

	private final GeoGebraExport export;
	private final Inequality ineq;
	private final FunctionalNVar geo;

	public ExportGraphicsW(FunctionalNVar geo, Inequality ineq, GeoGebraExport export) {

		// dummy canvas
		super(Canvas.createIfSupported());

		this.geo = geo;
		this.ineq = ineq;
		this.export = export;
	}

	@Override
	public void fill(GShape s) {
		export.fillIneq(s, ineq, geo);
	}
}