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
	 *            the lock to avoid loosing changed GeoElements
	 *            (view.update(geo) => set.add(geo) lock)
	 */
	public CompressedUpdateListener(CompressedView view, Timer timer,
			Set<GeoElement> set, ReentrantLock lock) {
		updateTimer = timer;
		updateSet = set;
		this.view = view;
		this.lock = lock;
	}

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
		{
			updateTimer.start();
			isWorking = true;

			work = new GeoElement[updateSet.size()];
			updateSet.toArray(work);
			updateSet.clear();
		}
		lock.unlock();

		for (GeoElement geo : work) {
			view.updateNow(geo);
		}
		isWorking = false;
	}

}
