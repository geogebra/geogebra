/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.factories;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.Reflection;
import org.geogebra.common.util.URLEncoder;
import org.geogebra.web.html5.euclidian.profiler.FpsProfilerW;
import org.geogebra.web.html5.sound.GTimerW;
import org.geogebra.web.html5.util.HttpRequestW;
import org.geogebra.web.html5.util.ProverW;

import elemental2.core.Global;

/**
 * Web implementations for various utils
 * 
 * @author Zoltan Kovacs
 */
public class UtilFactoryW extends UtilFactory {

	@Override
	public HttpRequest newHttpRequest() {
		return new HttpRequestW();
	}

	@Override
	public URLEncoder newURLEncoder() {
		return Global::encodeURIComponent;
	}

	@Override
	public Prover newProver() {
		return new ProverW();
	}

	@Override
	public double getMillisecondTime() {
		return FpsProfilerW.getMillisecondTimeNative();
	}

	@Override
	public Reflection newReflection(Class clazz) {
		// used by BatchedUpdateWrapper
		// not needed currently
		return null;
	}

	@Override
	public GTimer newTimer(GTimerListener listener, int delay) {
		return new GTimerW(listener, delay);
	}
}
