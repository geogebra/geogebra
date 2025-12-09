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

package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.CoordSystemAnimation;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.web.html5.sound.GTimerW;

public class CoordSystemAnimationW extends CoordSystemAnimation implements GTimerListener {
	protected GTimerW timer; // for animation

	/**
	 * @param view
	 *            zoomed / panned Euclidian view
	 */
	public CoordSystemAnimationW(EuclidianView view) {
		super(view);
		timer = new GTimerW(this, CoordSystemAnimation.DELAY);
	}

	@Override
	protected void stopTimer() {
		timer.stop();
	}

	@Override
	protected boolean hasTimer() {
		return timer != null;
	}

	@Override
	public synchronized void onRun() {
		step();
	}

	@Override
	protected void startTimer() {
		timer.startRepeat();
	}
}
