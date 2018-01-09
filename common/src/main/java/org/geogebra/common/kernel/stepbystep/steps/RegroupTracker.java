package org.geogebra.common.kernel.stepbystep.steps;

public class RegroupTracker {

	private int colorTracker;

	private boolean weakFactor;
	private boolean integerFractions;
	private boolean strongExpand;

	private boolean inNumerator;
	private boolean inDenominator;

	private boolean currentlyInNumerator;
	private boolean currentlyInDenominator;

	public RegroupTracker() {
		this.colorTracker = 1;
		this.integerFractions = true;
	}

	public RegroupTracker setInNumerator() {
		this.inNumerator = true;
		return this;
	}

	public RegroupTracker setInDenominator() {
		this.inDenominator = true;
		return this;
	}

	public RegroupTracker setWeakFactor() {
		this.weakFactor = true;
		return this;
	}

	public RegroupTracker unsetIntegerFractions() {
		this.integerFractions = false;
		return this;
	}

	public RegroupTracker setStrongExpand() {
		this.strongExpand = true;
		return this;
	}

	public void setNumerator(boolean inNumerator){
		this.currentlyInNumerator = inNumerator;
	}

	public void setDenominator(boolean inDenominator) {
		this.currentlyInDenominator = inDenominator;
	}

	public boolean getExpandSettings() {
		return strongExpand || (inNumerator && currentlyInNumerator) || (inDenominator && currentlyInDenominator);
	}

	public boolean isWeakFactor() {
		return weakFactor;
	}

	public boolean isIntegerFractions() {
		return integerFractions;
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

	public void setColorTracker(int colorTracker) {
		this.colorTracker = colorTracker;
	}

	public void resetTracker() {
		this.colorTracker = 1;
		this.currentlyInDenominator = false;
	}
}
