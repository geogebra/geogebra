package org.geogebra.desktop.export.pstricks;


import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianView;
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
			Inequality inequality, EuclidianView euclidianView2) {
		try {
			return new MyGraphicsPstricks(ef, inequality, euclidianView2);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	class MyGraphicsPstricks extends MyGraphics {

		public MyGraphicsPstricks(FunctionalNVar geo, Inequality ineq,
				EuclidianView euclidianView) {

			super(geo, ineq, euclidianView);
		}

		public void fill(GShape s) {
			superFill(s, ineq, geo, ds);
		}
	}
}
