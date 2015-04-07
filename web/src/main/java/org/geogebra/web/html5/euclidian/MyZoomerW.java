package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MyZoomer;
import org.geogebra.web.html5.gawt.Timer;
import org.geogebra.web.html5.kernel.HasTimerAction;

public class MyZoomerW extends MyZoomer implements HasTimerAction {
	protected Timer timer; // for animation

	public MyZoomerW(EuclidianView view) {
		super(view);
		timer = new Timer(DELAY, this);
	}

	protected void stopTimer() {
		timer.stop();
	}

	protected boolean hasTimer() {
		return timer != null;
	}

	public synchronized void actionPerformed() {
		step();
	}

	@Override
	protected void startTimer() {
		timer.start();
	}
}
