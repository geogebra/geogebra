package org.geogebra.web.html5.export;

import org.geogebra.common.awt.GShape;
import org.geogebra.common.export.pstricks.GeoGebraToPgf;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.awt.GGraphics2DW;

public class GeoGebraToPgfW extends GeoGebraToPgf {

	public GeoGebraToPgfW(App app) {
		super(app);
	}

	@Override
	protected GGraphics2DW createGraphics(FunctionalNVar ef,
			Inequality inequality) {
		try {
			return new MyGraphicsPgfW(ef, inequality);
		} catch (Exception ex) {
			Log.debug(ex);
			return null;
		}
	}

	class MyGraphicsPgfW extends MyGraphicsW {

		public MyGraphicsPgfW(FunctionalNVar geo, Inequality ineq) {

			super(geo, ineq);
		}

		@Override
		public void fill(GShape s) {
			superFill(s, ineq, geo, ds);
		}
	}
}
