package geogebra.common.kernel;

import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.Animatable;
import geogebra.common.kernel.geos.GeoElement;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Updates all animated geos based on slider ticks
 */
public abstract class AnimationManager {
	/** animation time*/
	public final static int STANDARD_ANIMATION_TIME = 10; // secs
	/** max frames per second*/
	public final static int MAX_ANIMATION_FRAME_RATE = 30; // frames per second
	/** min frames per second */
	public final static int MIN_ANIMATION_FRAME_RATE = 2; // frames per second
	/** kernel */
	protected Kernel kernel;
	/** animated geos*/
	protected ArrayList<GeoElement> animatedGeos;
	/** changed geos*/
	protected ArrayList<Animatable> changedGeos;
	/** current frame rate*/
	protected double frameRate = MAX_ANIMATION_FRAME_RATE;
	private boolean needToShowAnimationButton;
	
	
	/**
	 * @param kernel2 kernel
	 */
	public AnimationManager(Kernel kernel2) {
		this.kernel = kernel2;
		animatedGeos = new ArrayList<GeoElement>();
		changedGeos = new ArrayList<Animatable>();
	}

	/**
	 * Returns whether the animation button needs to be drawn in the graphics
	 * view. This is only needed when there are animated geos with non-dynamic
	 * speed.
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
		for (int i = 0; i < size; i++) {
			GeoElement geo = animatedGeos.get(i);
			GeoElement animObj = geo.getAnimationSpeedObject();
			if (animObj == null || !animObj.isLabelSet()
					&& animObj.isIndependent()) {
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
		}
	}
	
	/**
	 * Returns whether the animation is currently paused, i.e. the animation is
	 * not running but there are elements with "Animation on" set.
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
	 * @param frameTime
	 */
	private void adaptFrameRate(long compTime) {
		// only allow to use 80% of CPU time for animation (800 millis out of 1
		// sec)
		double framesPossible = 800.0 / compTime;

		// the frameRate is too high: decrease it
		if (framesPossible < frameRate) {
			frameRate = Math.max(framesPossible, MIN_ANIMATION_FRAME_RATE);
			setTimerDelay((int) Math.round(1000.0 / frameRate));

			// System.out.println("DECREASED frame rate: " + frameRate +
			// ", framesPossible: " + framesPossible);
		}

		// the frameRate is too low: try to increase it
		else if (frameRate < MAX_ANIMATION_FRAME_RATE) {
			frameRate = Math.min(framesPossible, MAX_ANIMATION_FRAME_RATE);
			setTimerDelay((int) Math.round(1000.0 / frameRate));

			// System.out.println("INCREASED frame rate: " + frameRate +
			// ", framesPossible: " + framesPossible);
		}

	}
	
	private TreeSet<AlgoElement> tempSet;

	private TreeSet<AlgoElement> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<AlgoElement>();
		}
		return tempSet;
	}
	
	/**
	 * Perform one step
	 */
	protected void sliderStep(){
		// skip animation frames while kernel is saving XML
		if (kernel.isSaving())
			return;

		long startTime = System.currentTimeMillis();

		// clear list of geos that need to be updated
		changedGeos.clear();

		// perform animation step for all animatedGeos
		int size = animatedGeos.size();
		for (int i = 0; i < size; i++) {
			Animatable anim = (Animatable) animatedGeos.get(i);
			boolean changed = anim.doAnimationStep(frameRate);
			if (changed)
				changedGeos.add(anim);
		}

		// do we need to update anything?
		if (changedGeos.size() > 0) {
			// efficiently update all changed GeoElements
			GeoElement.updateCascade(changedGeos, getTempSet(), false);

			// repaint views
			kernel.notifyRepaint();

			// check frame rate
			long compTime = System.currentTimeMillis() - startTime;
			adaptFrameRate(compTime);

			// System.out.println("UPDATE compTime: " + compTime +
			// ", frameRate: " + frameRate);
		}
	}
	
	/**
	 * @return whether the animation is currently running.
	 */
	public abstract boolean isRunning();
	
	/**
	 * @param i delay in miliseconds
	 */
	protected abstract void setTimerDelay(int i);
	/**
	 * stops timer
	 */
	protected abstract void stopTimer();
	/**
	 * starts timer
	 */
	protected abstract void startTimer();

}
