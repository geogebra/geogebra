package org.geogebra.web.html5.util;

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;

/**
 * Export multiple frames eg to GIF, PDF, WebM
 *
 */
public class AnimationExporter {

	/**
	 * @param app
	 *            app
	 * @param timeBetweenFrames
	 *            delay
	 * @param slider
	 *            slider
	 * @param consumer
	 *            callback that gets the base64-encoded file
	 * @param scale
	 *            scale
	 * @param rotate
	 *            rotation
	 * @param frameFormat
	 *            GIF/WebM/PDF
	 */
	public static void export(App app, int timeBetweenFrames,
			GeoNumeric slider,
			StringConsumer consumer, double scale, double rotate,
			ExportType frameFormat) {

		app.getKernel().getAnimationManager().stopAnimation();

		int type = slider.getAnimationType();
		double min = slider.getIntervalMin();
		double max = slider.getIntervalMax();

		double val;

		double step;
		int n;

		// pages in order, once each!
		if (frameFormat == ExportType.PDF_HTML5) {
			type = GeoElementND.ANIMATION_INCREASING_ONCE;
		}

		switch (type) {
		case GeoElementND.ANIMATION_DECREASING:
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

		case GeoElementND.ANIMATION_OSCILLATING:
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

		case GeoElementND.ANIMATION_INCREASING:
		case GeoElementND.ANIMATION_INCREASING_ONCE:
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

		final FrameCollectorW encoder = getEncoder(timeBetweenFrames, consumer,
				frameFormat);

		app.setWaitCursor();

		try {
			exportAnimation(app, encoder, slider, n, val, min, max,
					step,
					scale, rotate);
		} catch (RuntimeException ex) {
			if (ex.getMessage().contains("Font not loaded")) {
				throw ex;
			}
			app.showError(Errors.SaveFileFailed);
			Log.debug(ex);
		} finally {
			app.setDefaultCursor();
		}
	}

	private static FrameCollectorW getEncoder(int timeBetweenFrames,
			StringConsumer consumer, ExportType frameFormat) {

		switch (frameFormat) {
		case WEBP:
		case WEBM:
			return new WebMEncoderW(timeBetweenFrames, consumer);
		case PDF_HTML5:
			return new PDFEncoderW(consumer);
		case PNG:
		default:
			return new AnimatedGifEncoderW(timeBetweenFrames, consumer);
		}

	}

	private static void exportAnimation(App app, FrameCollectorW encoder,
			GeoNumeric num, int n, double val0, double min, double max,
			double step0, double scale, double rotate) {
		Log.debug("exporting animation");
		double val = val0;
		double step = step0;
		EuclidianViewWInterface ev = (EuclidianViewWInterface) app
				.getActiveEuclidianView();

		for (int i = 0; i < n; i++) {

			// avoid values like 14.399999999999968
			val = DoubleUtil.checkDecimalFraction(val);
			num.setValue(val);
			num.updateRepaint();

			if (rotate > 0 && ev instanceof EuclidianView3DInterface) {
				((EuclidianView3DInterface) ev).setRotAnimation(-i * rotate / n,
						false, false);
				ev.repaintView();
			}

			encoder.addFrame(ev, scale);

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
