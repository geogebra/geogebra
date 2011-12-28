package geogebra.gui.view;

import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Timer;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.gui.view.algebra.AlgebraController;
import geogebra.gui.view.algebra.AlgebraView;

/**
 * This class will collect update events in a time slice and bundles them in a
 * Set and after the time slice it will handle them to its attached view.
 * (Multiple updates of the same GeoElement in a time slice are only handled
 * down to the extends AlgebraView once at the end of the time slice.)
 * 
 * @author Lucas Binter
 */
public class CompressedAlgebraView extends AlgebraView implements
		CompressedView {
	private static final long serialVersionUID = 6383245533545749844L;

	private int ups;
	private Timer updateTimer;
	private Set<GeoElement> updateSet;
	private ActionListener updateListener;

	private final ReentrantLock lock = new ReentrantLock();

	/**
	 * @param algCtrl
	 *            the Algebra Controller for the extended AlgebraView
	 * @param updatesPerSecond
	 *            the updates per second handled down to the extended
	 *            AlgebraView
	 */
	public CompressedAlgebraView(AlgebraController algCtrl, int updatesPerSecond) {
		super(algCtrl);
		ups = updatesPerSecond;
		updateTimer = new Timer(1000 / ups, null);
		updateSet = new HashSet<GeoElement>();
		updateListener = new CompressedUpdateListener(this, updateTimer,
				updateSet, lock);
		updateTimer.addActionListener(updateListener);
	}

	@Override
	public void update(GeoElement geo) {
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

}
