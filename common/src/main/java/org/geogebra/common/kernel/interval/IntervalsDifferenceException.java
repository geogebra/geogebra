package org.geogebra.common.kernel.interval;

public class IntervalsDifferenceException extends Exception {
	public IntervalsDifferenceException() {
		super("Difference creates multiple intervals.");
	}
}
