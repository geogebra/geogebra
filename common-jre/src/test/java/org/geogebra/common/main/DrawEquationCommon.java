package org.geogebra.common.main;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.io.FactoryProviderCommon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Image;

public class DrawEquationCommon extends DrawEquation {

	public GDimension drawEquation(App app, GeoElementND geo, GGraphics2D g2,
			int x, int y, String text, GFont font, boolean serif,
			GColor fgColor, GColor bgColor, boolean useCache,
			boolean updateAgain, Runnable callback) {
		// TODO Auto-generated method stub
		return null;
	}

	public Image getCachedDimensions(String text, GeoElementND geo,
			Color fgColor, GFont font, int style, int[] ret) {
		// TODO Auto-generated method stub
		return null;
	}

	public void checkFirstCall(App app) {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
		checkFirstCallStatic();

	}

	public Color convertColor(GColor color) {
		// TODO Auto-generated method stub
		return null;
	}

	public GDimension measureEquation(App app, GeoElement geo0, String text,
			GFont font, boolean serif) {
		// TODO Auto-generated method stub
		return null;
	}

}
