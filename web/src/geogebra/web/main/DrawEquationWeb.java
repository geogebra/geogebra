package geogebra.web.main;

import geogebra.common.awt.Color;
import geogebra.common.awt.Dimension;
import geogebra.common.awt.Font;
import geogebra.common.awt.Graphics2D;
import geogebra.common.euclidian.DrawEquationInterface;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;

import com.google.gwt.canvas.dom.client.Context2d;

public class DrawEquationWeb implements DrawEquationInterface {

	public void setUseJavaFontsForLaTeX(AbstractApplication app, boolean b) {
	    // not relevant for web
    }

	public Dimension drawEquation(AbstractApplication app, GeoElement geo,
            Graphics2D g2, int x, int y, String mathml, Font font, boolean serif,
            Color fgColor, Color bgColor, boolean useCache) {
	    drawEquation(((geogebra.web.awt.Graphics2D)g2).getCanvas().getContext2d(), mathml, x, y);
	    
	    // TODO: correct dimensions
	    return new geogebra.web.awt.Dimension(100, 100);
    }
	
	public static native void drawEquation(Context2d ctx, String mathml, int x, int y) /*-{
	if (typeof $wnd.ggbOnInit === 'function')
		$wnd.drawEquation(ctx, mathml, x, y);
	}-*/;



}
