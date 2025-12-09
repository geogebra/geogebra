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

package org.geogebra.desktop.factories;

import org.geogebra.common.jre.util.UtilFactoryJre;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.Prover;
import org.geogebra.desktop.util.GTimerD;
import org.geogebra.desktop.util.HttpRequestD;
import org.geogebra.desktop.util.ProverD;

/**
 * Desktop implementations for various utils
 * @author Zoltan Kovacs
 */
public class UtilFactoryD extends UtilFactoryJre {

	@Override
	public HttpRequest newHttpRequest() {
		return new HttpRequestD();
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
