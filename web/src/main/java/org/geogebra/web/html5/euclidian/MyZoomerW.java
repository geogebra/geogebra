package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MyZoomer;
import org.geogebra.common.util.GTimer.GTimerListener;
import org.geogebra.web.html5.sound.GTimerW;

public class MyZoomerW extends MyZoomer implements GTimerListener {
	protected GTimerW timer; // for animation

	public MyZoomerW(EuclidianView view) {
		super(view);
		timer = new GTimerW(this, DELAY);
	}

	protected void stopTimer() {
		timer.stop();
	}

	protected boolean hasTimer() {
		return timer != null;
	}

	public synchronized void onRun() {
		step();
	}

	@Override
	protected void startTimer() {
		timer.startRepeat();
	}
}
