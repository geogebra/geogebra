package geogebra.common.util;

public interface NumberFormatAdapter {

	public int getMaximumFractionDigits();

	public void setGroupingUsed(boolean b);

	public String format(double x);

	public void setMaximumFractionDigits(int decimals);

	public void applyPattern(String string);

	

}
