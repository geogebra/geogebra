package geogebra.kernel;

public interface TextProperties {
	public int getFontSize();
	public void setFontSize(int size);
	public int getFontStyle();
	public void setFontStyle(int fontStyle);
	public int getPrintDecimals();
	public int getPrintFigures();
	public void setPrintDecimals(int printDecimals, boolean update);
	public void setPrintFigures(int printFigures, boolean update);
	public boolean isSerifFont();
	public void setSerifFont(boolean serifFont);
	public boolean useSignificantFigures();
	public boolean justFontSize();

}
