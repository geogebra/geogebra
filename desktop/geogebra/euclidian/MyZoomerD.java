package geogebra.euclidian;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.MyZoomer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class MyZoomerD extends MyZoomer implements ActionListener {
	protected Timer timer; // for animation

	public MyZoomerD(EuclidianView view) {
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

	public synchronized void actionPerformed(ActionEvent e) {
		step();
	}

	@Override
	protected void startTimer() {
		timer.start();
	}

}