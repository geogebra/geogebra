package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoElement;

public abstract class AbstractAnimationManager {

	public final static int STANDARD_ANIMATION_TIME = 10; // secs
	public final static int MAX_ANIMATION_FRAME_RATE = 30; // frames per second
	public final static int MIN_ANIMATION_FRAME_RATE = 2; // frames per second

	public abstract void addAnimatedGeo(GeoElement ge);
	public abstract void removeAnimatedGeo(GeoElement ge);
	public void stopAnimation() {
		// TODO Auto-generated method stub
		
	}
	public void startAnimation() {
		// TODO Auto-generated method stub
		
	}
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean isPaused() {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean needToShowAnimationButton() {
		// TODO Auto-generated method stub
		return false;
	}
	public void updateNeedToShowAnimationButton() {
		// TODO Auto-generated method stub
		
	}
	public void clearAnimatedGeos() {
		// TODO Auto-generated method stub
		
	}

}
