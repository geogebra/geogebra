package org.geogebra.common.kernel.stepbystep.steps;

public class RegroupTracker {

	private int colorTracker;

	private boolean onlyInDenominator;
	private boolean weakFactor;
	private boolean integerFractions;

	private boolean inDenominator;

	public RegroupTracker() {
		this.colorTracker = 1;
		this.integerFractions = true;
	}

	public RegroupTracker setOnlyInDenominator() {
		this.onlyInDenominator = true;
		return this;
	}

	public RegroupTracker setWeakFactor() {
		this.weakFactor = true;
		return this;
	}

	public RegroupTracker setIntegerFractions() {
		this.integerFractions = false;
		return this;
	}

	public void setDenominator(boolean inDenominator) {
		this.inDenominator = inDenominator;
	}

	public boolean getDenominatorSetting() {
		return !onlyInDenominator || inDenominator;
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
		this.inDenominator = false;
	}
}
