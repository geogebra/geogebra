package org.geogebra.desktop.export.pstricks;

import java.io.IOException;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.export.pstricks.GeoGebraToPgf;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.main.App;

public class GeoGebraToPgfD extends GeoGebraToPgf {

	public GeoGebraToPgfD(App app) {
		super(app);
	}

	@Override
	protected GGraphics2D createGraphics(FunctionalNVar ef,
			Inequality inequality, EuclidianView euclidianView2) {
		try {
			return new MyGraphicsPgf(ef, inequality, euclidianView2);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	class MyGraphicsPgf extends MyGraphics {

		public MyGraphicsPgf(FunctionalNVar geo, Inequality ineq,
				EuclidianView euclidianView) throws IOException {

			super(geo, ineq, euclidianView);
		}

		@Override
		public void fill(GShape s) {

			superFill(s, ineq, geo, ds);

		}
	}
}
