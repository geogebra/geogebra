package geogebra.common.euclidian;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;

public interface DrawEquationInterface {

	void setUseJavaFontsForLaTeX(AbstractApplication app, boolean b);
	public geogebra.common.awt.Dimension drawEquation(AbstractApplication app,
			GeoElement geo, geogebra.common.awt.Graphics2D g2, int x, int y, String text,
			geogebra.common.awt.Font font, boolean serif, geogebra.common.awt.Color fgColor, geogebra.common.awt.Color bgColor,
			boolean useCache); 
}
