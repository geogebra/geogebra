package org.geogebra.desktop.gui.view;

import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Timer;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.gui.view.algebra.AlgebraControllerD;
import org.geogebra.desktop.gui.view.algebra.AlgebraViewD;

/**
 * This class will collect update events in a time slice and bundles them in a
 * Set and after the time slice it will handle them to its attached view.
 * (Multiple updates of the same GeoElement in a time slice are only handled
 * down to the extends AlgebraView once at the end of the time slice.)
 * 
 * @author Lucas Binter
 */
public class CompressedAlgebraView extends AlgebraViewD implements
		CompressedView {
	private static final long serialVersionUID = 6383245533545749844L;

	private int ups;
	private Timer updateTimer;
	private Set<GeoElement> updateSet;
	private ActionListener updateListener;

	private final ReentrantLock lock = new ReentrantLock();

	private int rps;
	private Timer repaintTimer;
	private ActionListener repaintListener;

	private boolean needRepaint;

	/**
	 * @param algCtrl
	 *            the Algebra Controller for the extended AlgebraView
	 * @param updatesPerSecond
	 *            the updates per second handled down to the extended
	 *            AlgebraView
	 */
	public CompressedAlgebraView(AlgebraControllerD algCtrl,
			int updatesPerSecond) {
		this(algCtrl, updatesPerSecond, updatesPerSecond);
	}

	/**
	 * @param algCtrl
	 *            the Algebra Controller for the extended AlgebraView
	 * @param updatesPerSecond
	 *            the updates per second handled down to the extended
	 *            AlgebraView
	 * @param repaintsPerSecond
	 *            the maximum repaints per second rate
	 */
	public CompressedAlgebraView(AlgebraControllerD algCtrl,
			int updatesPerSecond, int repaintsPerSecond) {
		super(algCtrl);
		ups = updatesPerSecond;
		rps = repaintsPerSecond;
		updateTimer = new Timer(1000 / ups, null);
		updateTimer.setRepeats(false);
		repaintTimer = new Timer(1000 / rps, null);
		repaintTimer.setRepeats(false);
		updateSet = new HashSet<GeoElement>();
		updateListener = new CompressedUpdateListener(this, updateTimer,
				updateSet, lock);
		updateTimer.addActionListener(updateListener);
		repaintListener = new CompressedRepaintListener(this);
		repaintTimer.addActionListener(repaintListener);
		needRepaint = false;
	}

	@Override
	final public void update(GeoElement geo) {
		if (updateTimer.isRunning()) {
			lock.lock();
			updateSet.add(geo);
			lock.unlock();
		} else {
			updateTimer.start();
			updateNow(geo);
		}
	}

	public void updateNow(GeoElement geo) {
		super.update(geo);
	}

	@Override
	final public void updateVisualStyle(GeoElement geo) {
		update(geo);
	}

	@Override
	final public void repaintView() {
		repaint();
	}

	final public void repaintNow() {
		if (needRepaint) {
			super.repaint();
		}
		needRepaint = false;
	}

	@Override
	final public void repaint() {
		if (repaintTimer == null) {
			// needed because of null pointer error (Constructor)
			repaintNow();
			return;
		}

		if (repaintTimer.isRunning()) {
			needRepaint = true;
		} else {
			repaintTimer.start();
			needRepaint = true;
			repaintNow();
		}
	}

}
