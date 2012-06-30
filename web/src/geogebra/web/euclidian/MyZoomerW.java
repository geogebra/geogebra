package geogebra.web.euclidian;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.AbstractZoomer;

import java.awt.event.ActionEvent;

import geogebra.web.kernel.HasTimerAction;
import geogebra.web.kernel.gawt.Timer;



public class MyZoomerW extends AbstractZoomer implements HasTimerAction {
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
