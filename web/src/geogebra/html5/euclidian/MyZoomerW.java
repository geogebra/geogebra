package geogebra.html5.euclidian;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.MyZoomer;
import geogebra.html5.gawt.Timer;
import geogebra.html5.kernel.HasTimerAction;



public class MyZoomerW extends MyZoomer implements HasTimerAction {
	protected Timer timer; // for animation
	
		public MyZoomerW(EuclidianView view) {
		super(view);
		timer = new Timer(DELAY, this);
	}

	protected void stopTimer(){
		timer.stop();
	}
	
	protected boolean hasTimer(){
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
