package org.geogebra.web.html5.export;

import java.io.IOException;

import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.export.pstricks.GeoGebraToPgf;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.awt.GGraphics2DW;

public class GeoGebraToPgfW extends GeoGebraToPgf {

	public GeoGebraToPgfW(App app) {
		super(app);
	}

	@Override
	protected GGraphics2DW createGraphics(FunctionalNVar ef,
			Inequality inequality, EuclidianView euclidianView2) {
		try {
			return new MyGraphicsPgfW(ef, inequality, euclidianView2);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	class MyGraphicsPgfW extends MyGraphicsW {

		public MyGraphicsPgfW(FunctionalNVar geo, Inequality ineq,
				EuclidianView euclidianView) throws IOException {

			super(geo, ineq, euclidianView);
		}

		@Override
		public void fill(GShape s) {
			superFill(s, ineq, geo, ds);
		}
	}
}
