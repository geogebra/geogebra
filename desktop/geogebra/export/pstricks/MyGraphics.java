package geogebra.export.pstricks;

import geogebra.awt.GGraphics2DD;
import geogebra.common.awt.GShape;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.arithmetic.Inequality;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.export.epsgraphics.ColorMode;

import java.io.IOException;

// To avoid duplicate inequalities drawing algorithms replacing Graphics.
// In the three implementations (pstricks, pgf, asymptote) print the
// appropriate commands
abstract class MyGraphics extends GGraphics2DD {

	protected double[] ds;
	protected Inequality ineq;
	protected EuclidianView view;
	protected FunctionalNVar geo;

	public MyGraphics(FunctionalNVar geo, Inequality ineq,
			EuclidianViewND euclidianView) throws IOException {
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