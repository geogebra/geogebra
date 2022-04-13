package org.geogebra.web.html5.export;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.export.pstricks.GeoGebraToPstricks;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

public class GeoGebraToPstricksW extends GeoGebraToPstricks {

	public GeoGebraToPstricksW(App app) {
		super(app);
	}

	@Override
	protected GGraphics2D createGraphics(FunctionalNVar ef,
			Inequality inequality) {
		try {
			return new MyGraphicsPstricks(ef, inequality);
		} catch (Exception ex) {
			Log.debug(ex);
			return null;
		}
	}

	class MyGraphicsPstricks extends MyGraphicsW {

		public MyGraphicsPstricks(FunctionalNVar geo, Inequality ineq) {

			super(geo, ineq);
		}

		@Override
		public void fill(GShape s) {

			superFill(s, ineq, geo, ds);

		}
	}
}
