package org.geogebra.web.html5.util;

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;

public class GifShotExporter {

	public static void export(App app, int timeBetweenFrames, GeoNumeric slider,
			boolean isLoop, String filename, double scale, double rotate,
			boolean gif) {

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
			if (DoubleUtil.isZero(((max - min) / -step) - n)) {
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
			if (DoubleUtil.isZero(((max - min) / step * 2) - n)) {
				n++;
			}
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
			if (DoubleUtil.isZero(((max - min) / step) - n)) {
				n++;
			}
			if (n == 0) {
				n = 1;
			}
			val = min;
			break;
		}

		final Encoder encoder = getEncoder(timeBetweenFrames, isLoop, filename);

		FrameCollectorW collector = new FrameCollectorW() {

			@Override
			public void addFrame(String url) {
				// Log.debug("adding frame");
				encoder.addFrame(url);
			}

			@Override
			public void finish(int width, int height) {
				// Log.debug("finished");
				encoder.finish(width, height);
			}
			
		};

		app.setWaitCursor();

		try {
			exportAnimatedGIF(app, collector, slider, n, val, min, max, step,
					scale, rotate, gif ? ExportType.PNG : ExportType.WEBP);
		} catch (Exception ex) {
			app.localizeAndShowError("SaveFileFailed");
			Log.debug(ex.getMessage());
			ex.printStackTrace();
		} finally {
			app.setDefaultCursor();
		}
	}

	private static Encoder getEncoder(int timeBetweenFrames, boolean isLoop,
			String filename) {
		if (filename.endsWith(".webm")) {
			return new WebMEncoderW(timeBetweenFrames, isLoop, filename);
		}
		
		return new AnimatedGifEncoderW(timeBetweenFrames, isLoop, filename);
	}

	public static void exportAnimatedGIF(App app, FrameCollectorW encoder,
			GeoNumeric num, int n, double val0, double min, double max,
			double step0, double scale, double rotate, ExportType format) {
		Log.debug("exporting animation");
		double val = val0;
		double step = step0;
		EuclidianViewWInterface ev = ((EuclidianViewWInterface) app
				.getActiveEuclidianView());

		// Log.debug("using view " + ev.getViewID());

		for (int i = 0; i < n; i++) {

			// avoid values like 14.399999999999968
			val = DoubleUtil.checkDecimalFraction(val);
			num.setValue(val);
			num.updateRepaint();

			if (rotate > 0 && ev instanceof EuclidianView3DInterface) {
				((EuclidianView3DInterface) ev).setRotAnimation(-i * rotate / n,
						false, false);
				((EuclidianView3DInterface) ev).repaintView();
			}

			String url = ev.getExportImageDataUrl(scale, false, format);
			if (url == null) {
				Log.error("image null");
			} else {
				encoder.addFrame(url);
			}
			val += step;

			if (val > max + Kernel.STANDARD_PRECISION
					|| val < min - Kernel.STANDARD_PRECISION) {
				val -= 2 * step;
				step *= -1;
			}

		}

		int width = (int) Math.round(ev.getExportWidth() * scale);
		int height = (int) Math.round(ev.getExportHeight() * scale);

		encoder.finish(width, height);
	}

}
