package geogebra.euclidian;

import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.AbstractZoomer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class MyZoomer extends AbstractZoomer implements ActionListener {
	protected Timer timer; // for animation
	
		public MyZoomer(AbstractEuclidianView view) {
		super(view);
		timer = new Timer(DELAY, this);
	}

	protected void stopTimer(){
		timer.stop();
	}
	
	protected boolean hasTimer(){
		return timer != null;
	}
	
	public synchronized void actionPerformed(ActionEvent e) {
		step();
	}

	@Override
	protected void startTimer() {
		timer.start();
	}
	
}