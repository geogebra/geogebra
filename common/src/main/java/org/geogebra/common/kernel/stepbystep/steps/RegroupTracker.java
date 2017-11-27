package org.geogebra.common.kernel.stepbystep.steps;

public class RegroupTracker {

	private int colorTracker;

	private boolean onlyInDenominator;
	private boolean inDenominator;

	private boolean weakFactor;

	public RegroupTracker() {
		this(false, false);
	}

	public RegroupTracker(boolean onlyInDenominator, boolean weakFactor) {
		this.colorTracker = 1;

		this.onlyInDenominator = onlyInDenominator;
		this.inDenominator = false;

		this.weakFactor = weakFactor;
	}
	
	public boolean wasChanged() {
		return colorTracker > 1;
	}

	public int incColorTracker() {
		return colorTracker++;
	}

	public int getColorTracker() {
		return colorTracker;
	}

	public void resetTracker() {
		this.colorTracker = 1;
		this.inDenominator = false;
	}

	public void setColorTracker(int colorTracker) {
		this.colorTracker = colorTracker;
	}

	public void setDenominator() {
		this.inDenominator = true;
	}
	
	public boolean getDenominatorSetting() {
		return !onlyInDenominator || inDenominator;
	}
	
	public void setWeakFactor() {
		this.weakFactor = true;
	}
	
	public boolean isWeakFactor() {
		return weakFactor;
	}

}
