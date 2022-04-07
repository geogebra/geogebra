package org.geogebra.web.html5.export;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.export.pstricks.GeoGebraToAsymptote;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

public class GeoGebraToAsymptoteW extends GeoGebraToAsymptote {

	public GeoGebraToAsymptoteW(App app) {
		super(app);
	}

	@Override
	protected GGraphics2D createGraphics(FunctionalNVar ef,
			Inequality inequality) {

		try {
			return new MyGraphicsAs(ef, inequality);
		} catch (RuntimeException e) {
			Log.debug(e);
			return null;
		}
	}

	class MyGraphicsAs extends MyGraphicsW {

		public MyGraphicsAs(FunctionalNVar geo, Inequality ineq) {
			super(geo, ineq);
		}

		@Override
		public void fill(GShape s) {

			superFill(s, ineq, geo, ds);

		}
	}

}
