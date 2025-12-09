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

package org.geogebra.desktop.euclidian;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.geogebra.common.euclidian.CoordSystemAnimation;
import org.geogebra.common.euclidian.EuclidianView;

public class CoordSystemAnimationD extends CoordSystemAnimation implements ActionListener {
	protected Timer timer; // for animation

	/**
	 * @param view view
	 */
	public CoordSystemAnimationD(EuclidianView view) {
		super(view);
		timer = new Timer(DELAY, this);
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
	public synchronized void actionPerformed(ActionEvent e) {
		step();
	}

	@Override
	protected void startTimer() {
		timer.start();
	}

}