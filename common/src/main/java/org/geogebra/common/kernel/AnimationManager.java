package org.geogebra.common.kernel;

import java.util.ArrayList;
import java.util.TreeSet;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.Animatable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.MyMath;

import com.google.j2objc.annotations.Weak;

/**
 * Updates all animated geos based on slider ticks
 */
public class AnimationManager implements GTimerListener {
	/** animation time */
	public final static int STANDARD_ANIMATION_TIME = 10; // secs
	/** max frames per second */
	public final static int MAX_ANIMATION_FRAME_RATE = 30; // frames per second
	/** min frames per second */
	public final static int MIN_ANIMATION_FRAME_RATE = 6; // frames per second
	/** kernel */
	@Weak
	protected Kernel kernel;
	/** animated geos */
	protected ArrayList<GeoElement> animatedGeos;
	/** changed geos */
	protected ArrayList<GeoElementND> changedGeos;
	/** current frame rate */
	protected double frameRate = MAX_ANIMATION_FRAME_RATE;
	private boolean needToShowAnimationButton;

	private final GTimer timer;

	private TreeSet<AlgoElement> tempSet;
	private long lastStart = 0;

	/**
	 * @param kernel2
	 *            kernel
	 */
	public AnimationManager(Kernel kernel2) {
		this.kernel = kernel2;
		animatedGeos = new ArrayList<>();
		changedGeos = new ArrayList<>();
		timer = kernel.getApplication().newTimer(this,
				1000 / MAX_ANIMATION_FRAME_RATE);
	}

	/**
	 * Returns whether the animation button needs to be drawn in the graphics
	 * view. This is only needed when there are animated geos with non-dynamic
	 * speed.
	 * 
	 * @return true if we need to draw animation button
	 */
	final public boolean needToShowAnimationButton() {
		return needToShowAnimationButton;
	}

	/**
	 * Updates the needToShowAnimationButton value.
	 */
	public void updateNeedToShowAnimationButton() {
		int size = animatedGeos.size();
		if (size == 0) {
			needToShowAnimationButton = false;
			return;
		}

		// if one animated geo has a static speed, we need to get out of here
		for (GeoElement geo : animatedGeos) {
			GeoElement animObj = geo.getAnimationSpeedObject();
			if (animObj == null
					|| !animObj.isLabelSet() && animObj.isIndependent()) {
				needToShowAnimationButton = true;
				return;
			}
		}

		// all animated geos have dynamic speed
		needToShowAnimationButton = false;
	}

	/**
	 * Adds geo to the list of animated GeoElements.
	 * 
	 * @param geo
	 *            the GeoElement to add
	 */
	final public synchronized void addAnimatedGeo(GeoElement geo) {
		if (geo.isAnimating() && !animatedGeos.contains(geo)) {
			animatedGeos.add(geo);
			// if (animatedGeos.size() == 1) removed, might have geos with
			// variable controlling speed
			updateNeedToShowAnimationButton();
		}
	}

	/**
	 * Removes geo from the list of animated GeoElements.
	 * 
	 * @param geo
	 *            the GeoElement to remove
	 */
	final public synchronized void removeAnimatedGeo(GeoElement geo) {
		if (animatedGeos.remove(geo) && animatedGeos.size() == 0) {
			stopAnimation();
		}
		updateNeedToShowAnimationButton(); // added, might have geos with
											// variable controlling speed
	}

	/**
	 * Starts animation
	 */
	public synchronized void startAnimation() {

		if (kernel.getApplication().isScreenshotGenerator()) {
			return;
		}

		if (!isRunning() && animatedGeos.size() > 0) {
			updateNeedToShowAnimationButton();
			startTimer();
		}
	}

	/**
	 * Stops animation
	 */
	public synchronized void stopAnimation() {
		if (isRunning()) {
			stopTimer();
			updateNeedToShowAnimationButton();
			lastStart = 0;
		}
	}

	/**
	 * Returns whether the animation is currently paused, i.e. the animation is
	 * not running but there are elements with "Animation on" set.
	 * 
	 * @return true when paused
	 */
	public boolean isPaused() {
		return !isRunning() && animatedGeos.size() > 0;
	}

	/**
	 * Empties list of animated geos
	 */
	public void clearAnimatedGeos() {
		for (int i = 0; i < animatedGeos.size(); i++) {
			GeoElement geo = animatedGeos.get(i);
			geo.setAnimating(false);
		}

		animatedGeos.clear();
		updateNeedToShowAnimationButton();
	}

	/**
	 * Adapts the frame rate depending on how long it took to compute the last
	 * frame.
	 * 
	 * @param compTime
	 *            computation time
	 */
	private void adaptFrameRate(long compTime) {
		// only allow to use 80% of CPU time for animation (800 millis out of 1
		// sec)
		double framesPossible = 800.0 / compTime;

		// the frameRate is too high: decrease it
		if (framesPossible < frameRate) {
			frameRate = Math.max(framesPossible, MIN_ANIMATION_FRAME_RATE);
			setTimerDelay((int) Math.round(1000.0 / frameRate));
		}

		// the frameRate is too low: try to increase it
		else if (frameRate < MAX_ANIMATION_FRAME_RATE) {
			frameRate = Math.min(framesPossible, MAX_ANIMATION_FRAME_RATE);
			setTimerDelay((int) Math.round(1000.0 / frameRate));
		}

	}

	private TreeSet<AlgoElement> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<>();
		}
		return tempSet;
	}

	/**
	 * Perform one step
	 */
	protected void sliderStep() {
		// skip animation frames while kernel is saving XML
		if (kernel.isSaving()) {
			return;
		}

		kernel.notifyBatchUpdate();

		long startTime = System.currentTimeMillis();
		double actualFrameRate = lastStart == 0
				? MAX_ANIMATION_FRAME_RATE
				: MyMath.clamp(1000.0 / (startTime - this.lastStart),
				MIN_ANIMATION_FRAME_RATE, MAX_ANIMATION_FRAME_RATE);
		this.lastStart = startTime;

		// clear list of geos that need to be updated
		changedGeos.clear();

		// perform animation step for all animatedGeos
		// go right to left to ensure removing geos animated once does not kill
		// this #4193
		int size = animatedGeos.size();

		for (int i = size - 1; i >= 0; i--) {
			Animatable anim = (Animatable) animatedGeos.get(i);
			GeoElementND changed = anim.doAnimationStep(actualFrameRate, null);
			if (changed != null) {
				changedGeos.add(changed);
			}
		}
		// do we need to update anything?
		if (changedGeos.size() > 0) {
			// efficiently update all changed GeoElements
			GeoElement.updateCascade(changedGeos, getTempSet(), false);
			// repaint views
			kernel.notifyRepaint();
			// check frame rate
			long compTime = System.currentTimeMillis() - startTime;
			if (kernel.getApplication().getEuclidianView1() != null) {
				compTime += kernel.getApplication().getEuclidianView1()
						.getLastRepaintTime();
			}
			if (kernel.getApplication().hasEuclidianView2(1)) {
				compTime += kernel.getApplication().getEuclidianView2(1)
						.getLastRepaintTime();
			}
			adaptFrameRate(compTime);

			// collect some potential garbage
			kernel.notifyRemoveGroup();
		}

		kernel.notifyEndBatchUpdate();
	}

	/**
	 * @return whether the animation is currently running.
	 */
	public boolean isRunning() {
		return timer.isRunning();
	}

	/**
	 * @param i
	 *            delay in milliseconds
	 */

	protected void setTimerDelay(int i) {
		timer.setDelay(i);
	}

	@Override
	public void onRun() {
		sliderStep();
	}

	/**
	 * stops timer
	 */
	protected void stopTimer() {
		timer.stop();
		kernel.getApplication().getEventDispatcher().stopAnimation();
	}

	/**
	 * starts timer
	 */
	protected void startTimer() {
		timer.startRepeat();
		kernel.getApplication().getEventDispatcher().startAnimation();
	}

	/**
	 * current frame rate
	 * 
	 * @return in seconds
	 */
	public double getFrameRate() {
		return frameRate;
	}

}
