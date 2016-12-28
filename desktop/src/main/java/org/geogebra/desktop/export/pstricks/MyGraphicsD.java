package org.geogebra.desktop.export.pstricks;

import java.awt.image.BufferedImage;

import org.geogebra.common.awt.GShape;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.awt.GGraphics2DD;

// To avoid duplicate inequalities drawing algorithms replacing Graphics.
// In the three implementations (pstricks, pgf, asymptote) print the
// appropriate commands
abstract class MyGraphicsD extends GGraphics2DD {

	protected double[] ds;
	protected Inequality ineq;
	protected FunctionalNVar geo;

	public MyGraphicsD(FunctionalNVar geo, Inequality ineq) {

		// dummy canvas
		super(new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB)
				.createGraphics());

		this.geo = geo;
		this.ds = geo.getKernel().getViewBoundsForGeo((GeoElement) geo);
		this.ineq = ineq;
	}

	@Override
	public abstract void fill(GShape s);
}