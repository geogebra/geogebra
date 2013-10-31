package geogebra.html5.euclidian;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GShape;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.html5.awt.GBufferedImageW;
import geogebra.html5.util.ImageLoadCallback;
import geogebra.html5.util.ImageWrapper;

import com.google.gwt.core.client.Scheduler;

public class EuclidianStaticW extends geogebra.common.euclidian.EuclidianStatic {

	public EuclidianStaticW(){
		
	}
	@Override
	protected GRectangle doDrawMultilineLaTeX(App app,
	        GGraphics2D tempGraphics, GeoElement geo, GGraphics2D g2, GFont font,
	        GColor fgColor, GColor bgColor, String labelDesc0, int x,
	        int y, boolean serif) {

		String labelDesc = labelDesc0;

		if (labelDesc == null)
			return null;

		// rotation seems to be clockwise
		double rotateDegree = 0;

		if (labelDesc.startsWith("\\rotatebox{")) {
			// getting rotation degree...

			// chop "\\rotatebox{"
			labelDesc = labelDesc.substring(11);

			// get value
			int index = labelDesc.indexOf("}{ ");
			rotateDegree = Double.parseDouble(labelDesc.substring(0, index));

			// chop "}{"
			labelDesc = labelDesc.substring(index + 3);

			// chop " }"
			labelDesc = labelDesc.substring(0, labelDesc.length() - 2);

			if (labelDesc.startsWith("\\text{ ")) {
				// chop "text", seems to prevent the sqrt sign showing
				labelDesc = labelDesc.substring(7);
				labelDesc = labelDesc.substring(0, labelDesc.length() - 3);
			}
		}

		GDimension dim = app.getDrawEquation().drawEquation(app, geo, g2, x, y, labelDesc, font, serif, fgColor, bgColor, false, rotateDegree);

		int dimWidth = dim.getWidth();
		if (dimWidth <= 0)
			dimWidth = 1;

		int dimHeight = dim.getHeight();
		if (dimHeight <= 0)
			dimHeight = 1;

		double dimTopCorr = 0;
		double dimLeftCorr = 0;

		if (rotateDegree != 0) {

			double rotateDegreeForTrig = rotateDegree;

			while (rotateDegreeForTrig < 0)
				rotateDegreeForTrig += 360;

			if (rotateDegreeForTrig > 180)
				rotateDegreeForTrig -= 180;

			if (rotateDegreeForTrig > 90)
				rotateDegreeForTrig = 180 - rotateDegreeForTrig;

			// Now rotateDegreeForTrig is between 0 and 90 degrees

			rotateDegreeForTrig *= Math.PI / 180;

			// Now rotateDegreeForTrig is between 0 and PI/2, it is in radians actually!
			// INPUT for algorithm got: rotateDegreeForTrig, dimWidth, dimHeight

			// dimWidth and dimHeight are the scaled and rotated dims...
			// only the scaled, but not rotated versions should be computed from them:

			double helper = Math.cos(2 * rotateDegreeForTrig);
			double dimHeight0 = (dimHeight * Math.cos(rotateDegreeForTrig) - dimWidth * Math.sin(rotateDegreeForTrig)) / helper;
			double dimWidth0 = (dimWidth * Math.cos(rotateDegreeForTrig) - dimHeight * Math.sin(rotateDegreeForTrig)) / helper;

			// dimHeight0 and dimWidth0 are the values this algorithm needs

			double dimHalfDiag = Math.sqrt(dimWidth0 * dimWidth0 + dimHeight0 * dimHeight0) / 2.0;

			// We also have to compute the bigger and lesser degrees at the diagonals
			// Tangents will be positive, as they take positive numbers (and in radians)
			// between 0 and Math.PI / 2

			double diagDegreeWidth = Math.atan(dimHeight0 / dimWidth0);
			double diagDegreeHeight = Math.atan(dimWidth0 / dimHeight0);

			diagDegreeWidth += rotateDegreeForTrig;
			diagDegreeHeight += rotateDegreeForTrig;

			// diagDegreeWidth might slide through the other part, so substract it from Math.PI, if necessary
			if (diagDegreeWidth > Math.PI / 2)
				diagDegreeWidth = Math.PI - diagDegreeWidth;

			// doing the same for diagDegreeHeight
			if (diagDegreeHeight > Math.PI / 2)
				diagDegreeHeight = Math.PI - diagDegreeHeight;

			// half-height of new formula: dimHalfDiag * sin(diagDegreeWidth)
			dimTopCorr = dimHalfDiag * Math.sin(diagDegreeWidth);
			dimTopCorr = dimHeight0 / 2.0 - dimTopCorr;

			// half-width of new formula: dimHalfDiag * sin(diagDegreeHeight)
			dimLeftCorr = dimHalfDiag * Math.sin(diagDegreeHeight);
			dimLeftCorr = dimWidth0 / 2.0 - dimLeftCorr;
		}

		return new geogebra.html5.awt.GRectangleW(x + (int)dimLeftCorr, y + (int)dimTopCorr, dimWidth, dimHeight);
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
