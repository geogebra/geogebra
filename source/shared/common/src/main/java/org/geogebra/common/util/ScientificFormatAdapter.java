package org.geogebra.common.util;

public interface ScientificFormatAdapter {

	public int getSigDigits();

	public void setSigDigits(int SigDigit);

	public void setMaxWidth(int mWidth);

	public String format(double d);

}
