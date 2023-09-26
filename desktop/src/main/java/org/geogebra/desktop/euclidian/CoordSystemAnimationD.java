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