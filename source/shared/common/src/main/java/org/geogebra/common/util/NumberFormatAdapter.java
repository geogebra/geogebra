package org.geogebra.common.util;

/**
 * Number formatter with fixed number of decimal places.
 */
public interface NumberFormatAdapter {

	public int getMaximumFractionDigits();

	public String format(double x);

}
