package org.geogebra.desktop.export.pstricks;

import java.io.IOException;

import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.export.epsgraphics.ColorMode;

// To avoid duplicate inequalities drawing algorithms replacing Graphics.
// In the three implementations (pstricks, pgf, asymptote) print the
// appropriate commands
abstract class MyGraphics extends GGraphics2DD {

	protected double[] ds;
	protected Inequality ineq;
	protected EuclidianView view;
	protected FunctionalNVar geo;

	public MyGraphics(FunctionalNVar geo, Inequality ineq,
			EuclidianView euclidianView) throws IOException {
		super(new MyGraphics2D(null, System.out, 0, 0, 0, 0,
				ColorMode.COLOR_RGB));
		view = euclidianView;
		this.geo = geo;
		this.ds = geo.getKernel().getViewBoundsForGeo((GeoElement) geo);
		this.ineq = ineq;
	}

	@Override
	public abstract void fill(GShape s);
}