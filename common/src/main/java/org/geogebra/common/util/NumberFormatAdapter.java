package org.geogebra.common.util;

public interface NumberFormatAdapter {

	public int getMaximumFractionDigits();

	public String format(double x);

}
