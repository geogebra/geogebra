package geogebra.kernel;

import geogebra.common.kernel.AnimationManager;
import geogebra.common.kernel.Kernel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * Desktop version of animation manager
 *
 */
public class AnimationManagerD extends AnimationManager implements
		ActionListener {

	
	private Timer timer;
	

	/**
	 * Creates new animation manager
	 * @param kernel kernel
	 */
	public AnimationManagerD(Kernel kernel) {
		super(kernel);

		timer = new Timer(1000 / MAX_ANIMATION_FRAME_RATE, this);
	}
	
	/**
	 * Updates all geos in the updateCascadeQueue and their dependent algorithms
	 * and repaints all views.
	 */
	final public synchronized void actionPerformed(ActionEvent e) {
		sliderStep();
	}


	@Override
	public boolean isRunning() {
		return timer.isRunning();
	}


	@Override
	protected void setTimerDelay(int i) {
		timer.setDelay(i);
		
	}

	@Override
	protected void stopTimer() {
		timer.stop();
	}

	@Override
	protected void startTimer() {
		timer.start();
	}

}
