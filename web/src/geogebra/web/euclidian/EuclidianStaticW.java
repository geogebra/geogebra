package geogebra.web.euclidian;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GShape;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;

public class EuclidianStaticW extends geogebra.common.euclidian.EuclidianStatic {

	@Override
	protected GRectangle doDrawMultilineLaTeX(App app,
	        GGraphics2D tempGraphics, GeoElement geo, GGraphics2D g2, GFont font,
	        GColor fgColor, GColor bgColor, String labelDesc, int x,
	        int y, boolean serif) {
		GDimension dim = app.getDrawEquation().drawEquation(app, geo, g2, x, y, labelDesc, font, serif, fgColor, bgColor, false);
		
		// TODO: dimension
		return new geogebra.web.awt.GRectangleW(x, y, dim.getWidth(), dim.getHeight());
	}

	private static GFont getIndexFont(GFont f) {
		// index font size should be at least 8pt
		int newSize = Math.max((int) (f.getSize() * 0.9), 8);
		return f.deriveFont(f.getStyle(), newSize);
	}

	
	@Override
	protected void doFillWithValueStrokePure(GShape shape, GGraphics2D g2) {
		g2.fill(shape);

		App.debug("implementation needed or OK");
		// TODO can we emulate somehow the "pure stroke" behavior?
	}

	@Override
	protected void doDrawWithValueStrokePure(GShape shape, GGraphics2D g2) {
		g2.draw(shape);

		App.debug("implementation needed or OK");
		// TODO can we emulate somehow the "pure stroke" behavior?
	}

	@Override
    protected Object doSetInterpolationHint(GGraphics2D g3,
            boolean needsInterpolationRenderingHint) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    protected void doResetInterpolationHint(GGraphics2D g3, Object hint) {
	    // TODO Auto-generated method stub
	    
    }

}
