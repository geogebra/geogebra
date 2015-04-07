package org.geogebra.common.util;

public interface ScientificFormatAdapter {

	public int getSigDigits();

	public int getMaxWidth();

	public boolean getScientificNotationStyle();

	public void setSigDigits(int SigDigit);

	public void setMaxWidth(int mWidth);

	public void setScientificNotationStyle(boolean sciNote);

	public String format(double d);

}
