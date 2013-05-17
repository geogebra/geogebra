package geogebra.web.euclidian;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GShape;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.web.awt.GBufferedImageW;
import geogebra.web.helper.ImageLoadCallback;
import geogebra.web.helper.ImageWrapper;

import com.google.gwt.core.client.Scheduler;

public class EuclidianStaticW extends geogebra.common.euclidian.EuclidianStatic {

	public EuclidianStaticW(){
		
	}
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

	// to make code more efficient in the following method
	private boolean repaintDeferred = false;

	// to avoid infinite loop in the following method
	private int repaintsFromHereInProgress = 0;

	@Override
	protected void doFillAfterImageLoaded(final geogebra.common.awt.GShape shape, final geogebra.common.awt.GGraphics2D g3, geogebra.common.awt.GBufferedImage gi, final App app)
	{
		if (((GBufferedImageW)gi).isLoaded()) {
			// when the image is already loaded, no new repaint is necessary
			// in theory, the image will be loaded after some repaints so
			// this will not be an infinite loop ...
			g3.fill(shape);
		} else if (repaintsFromHereInProgress == 0) {
			// the if condition makes sure there will be no infinite loop

			// note: AFAIK (?), DOM's addEventListener method can add more listeners 
			ImageWrapper.nativeon(
				((GBufferedImageW)gi).getImageElement(), 
				"load", 
				new ImageLoadCallback() { 
					public void onLoad() {
						if (!repaintDeferred) {
							repaintDeferred = true;
							// otherwise, at the first time, issue a complete repaint
							// but schedule it deferred to avoid conflicts in repaints
							Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
								public void execute() {
									repaintDeferred = false;
									repaintsFromHereInProgress++;
									((EuclidianViewWeb)app.getEuclidianView1()).doRepaint();
									if (app.hasEuclidianView2())
										((EuclidianViewWeb)app.getEuclidianView2()).doRepaint();
									repaintsFromHereInProgress--;
								}
							});
						}
					} 
				}
			);
		}
	}

	@Override
	protected void doFillWithValueStrokePure(GShape shape, GGraphics2D g2) {
		g2.fill(shape);
	}

	@Override
	protected void doDrawWithValueStrokePure(GShape shape, GGraphics2D g2) {
		g2.draw(shape);
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
