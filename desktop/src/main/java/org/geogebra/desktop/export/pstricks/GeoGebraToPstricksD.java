package org.geogebra.desktop.export.pstricks;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.export.pstricks.GeoGebraToPstricks;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.main.App;

public class GeoGebraToPstricksD extends GeoGebraToPstricks {

	public GeoGebraToPstricksD(App app) {
		super(app);
	}

	@Override
	protected GGraphics2D createGraphics(FunctionalNVar ef,
			Inequality inequality) {
		return new MyGraphicsPstricks(ef, inequality);
	}

	class MyGraphicsPstricks extends MyGraphicsD {

		public MyGraphicsPstricks(FunctionalNVar geo, Inequality ineq) {

			super(geo, ineq);
		}

		@Override
		public void fill(GShape s) {
			superFill(s, ineq, geo, ds);
		}
	}
}
