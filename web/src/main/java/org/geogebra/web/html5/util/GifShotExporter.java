package org.geogebra.web.html5.util;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.euclidian.EuclidianViewW;

public class GifShotExporter {

	public static String export(App app, int timeBetweenFrames, GeoNumeric slider,
			boolean isLoop, String filename, double scale) {

		app.getKernel().getAnimatonManager().stopAnimation();

		int type = slider.getAnimationType();
		double min = slider.getIntervalMin();
		double max = slider.getIntervalMax();

		double val;

		double step;
		int n;

		switch (type) {
		case GeoElement.ANIMATION_DECREASING:
			step = -slider.getAnimationStep();
			n = (int) ((max - min) / -step);
			if (Kernel.isZero(((max - min) / -step) - n)) {
				n++;
			}
			if (n == 0) {
				n = 1;
			}
			val = max;
			break;

		case GeoElement.ANIMATION_OSCILLATING:
			step = slider.getAnimationStep();
			n = (int) ((max - min) / step) * 2;
			if (Kernel.isZero(((max - min) / step * 2) - n))
				n++;
			if (n == 0) {
				n = 1;
			}
			val = min;
			break;

		case GeoElement.ANIMATION_INCREASING:
		case GeoElement.ANIMATION_INCREASING_ONCE:
		default:
			step = slider.getAnimationStep();
			n = (int) ((max - min) / step);
			if (Kernel.isZero(((max - min) / step) - n)) {
				n++;
			}
			if (n == 0) {
				n = 1;
			}
			val = min;
			break;
		}

		final AnimatedGifEncoderW gifEncoder = new AnimatedGifEncoderW(
				timeBetweenFrames, isLoop, filename);

		FrameCollectorW collector = new FrameCollectorW() {

			private String result;

			public void addFrame(String url) {
				gifEncoder.addFrame(url);
			}

			public void finish(int width, int height) {
				result = gifEncoder.finish(width, height);
			}
			
			public String getResult() {
				return result;
			}
		};

		app.setWaitCursor();

		try {
			exportAnimatedGIF(app, collector, slider, n, val, min, max, step,
					scale);
		} catch (Exception ex) {
			app.showError("SaveFileFailed");
			ex.printStackTrace();
		} finally {
			app.setDefaultCursor();
		}
		
		return collector.getResult();
	}

	public static void exportAnimatedGIF(App app, FrameCollectorW gifEncoder,
			GeoNumeric num, int n, double val, double min, double max,
			double step, double scale) {
		Log.debug("exporting animation");
		EuclidianViewW ev = ((EuclidianViewW) app.getActiveEuclidianView());
		for (int i = 0; i < n; i++) {

			// avoid values like 14.399999999999968
			val = Kernel.checkDecimalFraction(val);
			num.setValue(val);
			num.updateRepaint();

			String url = ev.getExportImageDataUrl(scale, false);
			if (url == null) {
				Log.error("image null");
			} else {
				gifEncoder.addFrame(url);
			}
			val += step;

			if (val > max + 0.00000001 || val < min - 0.00000001) {
				val -= 2 * step;
				step *= -1;
			}

		}

		int width = (int) Math.round(ev.getExportWidth() * scale);
		int height = (int) Math.round(ev.getExportHeight() * scale);

		gifEncoder.finish(width, height);
	}

}
