package org.geogebra.desktop.export.pstricks;

import java.io.IOException;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.export.pstricks.GeoGebraToPdf;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.main.App;

/**
 * @author Hoszu Henrietta 
 */
/**
 * For Animated PDF with Pgf/Tikz (from GeoGebraToPgf)
 *
 */
public class GeoGebraToPdfD extends GeoGebraToPdf {

	/**
	 * @param app
	 */
	public GeoGebraToPdfD(App app) {
		super(app);
	}

	@Override
	protected GGraphics2D createGraphics(FunctionalNVar ef,
			Inequality inequality) {
		try {
			return new MyGraphicsPdf(ef, inequality);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	class MyGraphicsPdf extends MyGraphicsD {

		public MyGraphicsPdf(FunctionalNVar geo, Inequality ineq)
				throws IOException {

			super(geo, ineq);
		}

		@Override
		public void fill(GShape s) {

			superFill(s, ineq, geo, ds);

		}
	}
}
