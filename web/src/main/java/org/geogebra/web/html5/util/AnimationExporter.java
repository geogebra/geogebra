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
	 * @param isLoop
	 *            loop?
	 * @param filename
	 *            filename
	 * @param scale
	 *            scale
	 * @param rotate
	 *            rotation
	 * @param frameFormat
	 *            GIF/WebM/PDF
	 * @return result as base64 URL (not GIF though)
	 */
	public static String export(App app, int timeBetweenFrames,
			GeoNumeric slider,
			boolean isLoop, String filename, double scale, double rotate,
			ExportType frameFormat) {

		app.getKernel().getAnimatonManager().stopAnimation();

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

		EuclidianViewWInterface ev = ((EuclidianViewWInterface) app
				.getActiveEuclidianView());

		final Encoder encoder = getEncoder(timeBetweenFrames, isLoop, filename,
				frameFormat, ev);

		FrameCollectorW collector = new FrameCollectorW() {

			@Override
			public String finish(int width, int height) {
				// Log.debug("finished");
				return encoder.finish(width, height);
			}

			@Override
			public void addFrame(EuclidianViewWInterface view,
					double exportScale, ExportType format) {
				String url = view.getExportImageDataUrl(exportScale, false,
						format, false);
				encoder.addFrame(url);
			}

		};

		app.setWaitCursor();

		try {
			return exportAnimatedGIF(app, collector, slider, n, val, min, max,
					step,
					scale, rotate, frameFormat);
		} catch (Exception ex) {
			app.showError(Errors.SaveFileFailed);
			Log.debug(ex.getMessage());
			ex.printStackTrace();
		} finally {
			app.setDefaultCursor();
		}

		return null;
	}

	private static Encoder getEncoder(int timeBetweenFrames, boolean isLoop,
			String filename, ExportType frameFormat,
			EuclidianViewWInterface ev) {

		switch (frameFormat) {
		default:
		case PNG:
			return new AnimatedGifEncoderW(timeBetweenFrames, isLoop, filename);
		case WEBP:
			return new WebMEncoderW(timeBetweenFrames, isLoop, filename);

		case PDF_HTML5:
			return new PDFEncoderW(ev);
		}

	}

	private static String exportAnimatedGIF(App app, FrameCollectorW encoder,
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

			// String url = ev.getExportImageDataUrl(scale, false, format);
			// if (url == null) {
			// Log.error("image null");
			// } else

			encoder.addFrame(ev, scale, format);

			val += step;

			if (val > max + Kernel.STANDARD_PRECISION
					|| val < min - Kernel.STANDARD_PRECISION) {
				val -= 2 * step;
				step *= -1;
			}
		}

		int width = (int) Math.round(ev.getExportWidth() * scale);
		int height = (int) Math.round(ev.getExportHeight() * scale);

		return encoder.finish(width, height);
	}

}
