package org.geogebra.web.html5.euclidian;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.gawt.GBufferedImageW;
import org.geogebra.web.html5.util.ImageLoadCallback;
import org.geogebra.web.html5.util.ImageWrapper;

import com.google.gwt.core.client.Scheduler;

public class EuclidianStaticW extends org.geogebra.common.euclidian.EuclidianStatic {

	public EuclidianStaticW() {

	}


	/*private static GFont getIndexFont(GFont f) {
		// index font size should be at least 8pt
		int newSize = Math.max((int) (f.getSize() * 0.9), 8);
		return f.deriveFont(f.getStyle(), newSize);
	}*/

	// to make code more efficient in the following method
	private boolean repaintDeferred = false;

	// to avoid infinite loop in the following method
	private int repaintsFromHereInProgress = 0;

	@Override
	protected void doFillAfterImageLoaded(
	        final org.geogebra.common.awt.GShape shape,
	        final org.geogebra.common.awt.GGraphics2D g3,
	        org.geogebra.common.awt.GBufferedImage gi, final App app) {
		if (((GBufferedImageW) gi).isLoaded()) {
			// when the image is already loaded, no new repaint is necessary
			// in theory, the image will be loaded after some repaints so
			// this will not be an infinite loop ...
			g3.fill(shape);
		} else if (repaintsFromHereInProgress == 0) {
			// the if condition makes sure there will be no infinite loop

			// note: AFAIK (?), DOM's addEventListener method can add more
			// listeners
			ImageWrapper.nativeon(((GBufferedImageW) gi).getImageElement(),
			        "load", new ImageLoadCallback() {
				        public void onLoad() {
					        if (!repaintDeferred) {
						        repaintDeferred = true;
						        // otherwise, at the first time, issue a
								// complete repaint
						        // but schedule it deferred to avoid conflicts
								// in repaints
						        Scheduler.get().scheduleDeferred(
						                new Scheduler.ScheduledCommand() {
							                public void execute() {
								                repaintDeferred = false;
								                repaintsFromHereInProgress++;
								                ((EuclidianViewW) app
								                        .getEuclidianView1())
								                        .doRepaint();
								                if (app.hasEuclidianView2(1))
									                ((EuclidianViewW) app
									                        .getEuclidianView2(1))
									                        .doRepaint();
								                repaintsFromHereInProgress--;
							                }
						                });
					        }
				        }
			        });
		}
	}



}
