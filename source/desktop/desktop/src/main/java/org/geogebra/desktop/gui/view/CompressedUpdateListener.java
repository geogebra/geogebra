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

package org.geogebra.desktop.gui.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Timer;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * 
 * @author Lucas Binter
 */
public class CompressedUpdateListener implements ActionListener {
	private Timer updateTimer;
	private Set<GeoElement> updateSet;

	private CompressedView view;

	private final ReentrantLock lock;

	private boolean isWorking = false;

	/**
	 * @param timer
	 *            the updateTimer to invoke / use
	 * @param set
	 *            a set containing all changed geo elements
	 * @param view
	 *            the compressedView attached this ActionListener is attached to
	 * @param lock
	 *            the lock to avoid losing changed GeoElements
	 *            (view.update(geo) =&gt; set.add(geo) lock)
	 */
	public CompressedUpdateListener(CompressedView view, Timer timer,
			Set<GeoElement> set, ReentrantLock lock) {
		updateTimer = timer;
		updateSet = set;
		this.view = view;
		this.lock = lock;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (updateSet.isEmpty()) {
			return;
		}
		if (isWorking) {
			updateTimer.start();
			return;
		}

		GeoElement[] work;
		lock.lock();
		try {
			updateTimer.start();
			isWorking = true;

			work = new GeoElement[updateSet.size()];
			updateSet.toArray(work);
			updateSet.clear();
		} finally {
			lock.unlock();
		}

		for (GeoElement geo : work) {
			view.updateNow(geo);
		}
		isWorking = false;
	}

}
