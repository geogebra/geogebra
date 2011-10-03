package geogebra.kernel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.swing.Timer;

public class AnimationManager implements ActionListener {
		
	public final static int STANDARD_ANIMATION_TIME = 10; // secs
	public final static int MAX_ANIMATION_FRAME_RATE = 30; // frames per second
	public final static int MIN_ANIMATION_FRAME_RATE = 2; // frames per second

	private Kernel kernel;
	private ArrayList animatedGeos, changedGeos;
	private Timer timer;
	private double frameRate = MAX_ANIMATION_FRAME_RATE;
	private boolean needToShowAnimationButton;
	
	public AnimationManager(Kernel kernel) {	
		this.kernel = kernel;
		animatedGeos = new ArrayList();
		changedGeos = new ArrayList();
		
		timer = new Timer(1000 / MAX_ANIMATION_FRAME_RATE, this);		
	}
		
	public synchronized void startAnimation() {
		if (!timer.isRunning() && animatedGeos.size() > 0) {
			updateNeedToShowAnimationButton();
			timer.start();			
		}
	}
	
	public synchronized void stopAnimation() {
		if (timer.isRunning()) {			
			timer.stop();
			updateNeedToShowAnimationButton();
		}
	}
	
	public void clearAnimatedGeos() {
		for (int i=0; i < animatedGeos.size(); i++) {
			GeoElement geo = (GeoElement) animatedGeos.get(i);
			geo.setAnimating(false);
		}
		
		animatedGeos.clear();		
		updateNeedToShowAnimationButton();
	}
	
	/**
	 * Returns whether the animation is currently running.
	 */
	public boolean isRunning() {
		return timer.isRunning();
	}
	
	/**
	 * Returns whether the animation is currently paused, i.e.
	 * the animation is not running but there are elements with 
	 * "Animation on" set.
	 */
	public boolean isPaused() {
		return !timer.isRunning() && animatedGeos.size() > 0;
	}
	
	/**
	 * Returns whether the animation button needs to be drawn in the graphics view.
	 * This is only needed when there are animated geos with non-dynamic speed. 
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
		for (int i=0; i < size; i++) {
			GeoElement geo = (GeoElement) animatedGeos.get(i);
			GeoElement animObj = geo.getAnimationSpeedObject();
			if (animObj == null || !animObj.isLabelSet() && animObj.isIndependent()) {
				needToShowAnimationButton = true;
				return;
			}
			
			
			// TODO: check with anim_autocomplete.ggb
//			else if (animObj.isIndependent()) {
//				if (!animObj.isLabelSet() || !animObj.isVisible()) { 
//					// unlabeled or invisible speed object
//					needToShowAnimationButton = true;
//					return;
//				}
//			}
//			else {
//				// check if at least one free parent of animObj is visible
//				TreeSet ind = animObj.getAllIndependentPredecessors();
//				Iterator it = ind.iterator();
//				boolean oneVisible = false;
//				while (it.hasNext()) {
//					GeoElement parent = (GeoElement) it.next();
//					if (parent.isVisible())
//						oneVisible = true;
//				}
//				
//				// no free parent is visible: show animation button
//				if (!oneVisible) {
//					needToShowAnimationButton = true;
//					return;
//				}
//			}
			
		}		
		

		
		// all animated geos have dynamic speed
		needToShowAnimationButton = false;
	}

	
	/**
	 * Adds geo to the list of animated GeoElements.
	 */
	final public synchronized void addAnimatedGeo(GeoElement geo) {
		if (geo.isAnimating() && !animatedGeos.contains(geo)) {
			animatedGeos.add(geo);		
			//if (animatedGeos.size() == 1) removed, might have geos with variable controlling speed
				updateNeedToShowAnimationButton();
		}
	}
	
	/**
	 * Removes geo from the list of animated GeoElements.
	 */
	final public synchronized void removeAnimatedGeo(GeoElement geo) {
		if (animatedGeos.remove(geo) && animatedGeos.size() == 0) { 
				stopAnimation();
		}
		updateNeedToShowAnimationButton(); // added, might have geos with variable controlling speed
	}
	
	/**
	 * Updates all geos in the updateCascadeQueue and their dependent algorithms 
	 * and repaints all views.
	 */
	final public synchronized void actionPerformed(ActionEvent e) {	
		// skip animation frames while kernel is saving XML
		if (kernel.isSaving()) return;
		
		long startTime = System.currentTimeMillis();
		
		// clear list of geos that need to be updated
		changedGeos.clear();
		
		// perform animation step for all animatedGeos
		int size = animatedGeos.size();
		for (int i=0; i < size; i++) {
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
			
			//System.out.println("UPDATE compTime: " + compTime + ", frameRate: " + frameRate);		
		}
	}
	
	private TreeSet tempSet;	
	private TreeSet getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet();
		}
		return tempSet;
	}
	
	/**
	 * Adapts the frame rate depending on how long it took to compute the last frame.
	 * @param frameTime
	 */
	private void adaptFrameRate(long compTime) {				
		// only allow to use 80% of CPU time for animation (800 millis out of 1 sec)
		double framesPossible = 800.0 / compTime;
		
		// the frameRate is too high: decrease it
		if (framesPossible < frameRate) {			
			frameRate = Math.max(framesPossible, MIN_ANIMATION_FRAME_RATE);
			timer.setDelay((int) Math.round(1000.0 / frameRate));
			
			//System.out.println("DECREASED frame rate: " + frameRate + ", framesPossible: " + framesPossible);	
		}
				
		// the frameRate is too low: try to increase it
		else if (frameRate < MAX_ANIMATION_FRAME_RATE) {			
			frameRate = Math.min(framesPossible, MAX_ANIMATION_FRAME_RATE);
			timer.setDelay((int) Math.round(1000.0 / frameRate));
			
			//System.out.println("INCREASED frame rate: " + frameRate + ", framesPossible: " + framesPossible);
		}
		

	}
	
}
