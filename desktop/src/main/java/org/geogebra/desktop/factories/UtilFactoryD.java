package org.geogebra.desktop.factories;

import org.geogebra.common.jre.util.UtilFactoryJre;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.URLEncoder;
import org.geogebra.desktop.util.GTimerD;
import org.geogebra.desktop.util.HttpRequestD;
import org.geogebra.desktop.util.ProverD;
import org.geogebra.desktop.util.URLEncoderD;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org> Desktop implementations for
 *         various utils
 */
public class UtilFactoryD extends UtilFactoryJre {

	@Override
	public HttpRequest newHttpRequest() {
		return new HttpRequestD();
	}

	@Override
	public URLEncoder newURLEncoder() {
		return new URLEncoderD();
	}

	@Override
	public Prover newProver() {
		return new ProverD();
	}

	@Override
	public double getMillisecondTime() {
		return System.nanoTime() / 1000000d;
	}

	@Override
	public GTimer newTimer(GTimerListener listener, int delay) {
		return new GTimerD(listener, delay);
	}
}
