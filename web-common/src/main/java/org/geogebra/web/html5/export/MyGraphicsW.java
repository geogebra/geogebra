package org.geogebra.web.html5.export;

import org.geogebra.common.awt.GShape;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.awt.GGraphics2DW;

import com.google.gwt.canvas.client.Canvas;

// To avoid duplicate inequalities drawing algorithms replacing Graphics.
// In the three implementations (pstricks, pgf, asymptote) print the
// appropriate commands
abstract class MyGraphicsW extends GGraphics2DW {

	protected double[] ds;
	protected Inequality ineq;
	protected FunctionalNVar geo;

	public MyGraphicsW(FunctionalNVar geo, Inequality ineq) {

		// dummy canvas
		super(Canvas.createIfSupported());

		this.geo = geo;
		this.ds = geo.getKernel().getViewBoundsForGeo((GeoElement) geo);
		this.ineq = ineq;
	}

	@Override
	public abstract void fill(GShape s);
}