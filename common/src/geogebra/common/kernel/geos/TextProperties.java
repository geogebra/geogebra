package geogebra.common.kernel.geos;

/**
 * Element with text properties
 */
public interface TextProperties {
	/**
	 * 
	 * @return font size (relative)
	 */
	public double getFontSizeMultiplier();
	/**
	 * 
	 * @param size font size (relative)
	 */
	public void setFontSizeMultiplier(double size);
	/**
	 * 
	 * @return font style
	 */
	public int getFontStyle();
	/**
	 * 
	 * @param fontStyle font style
	 */
	public void setFontStyle(int fontStyle);
	/**
	 * 
	 * @return print decimals (-1 for kernel default)
	 */
	public int getPrintDecimals();
	/**
	 * 
	 * @return print figures (-1 for kernel default)
	 */
	public int getPrintFigures();
	/**
	 * 
	 * @param printDecimals print decimals
	 * @param update true to update the text
	 */
	public void setPrintDecimals(int printDecimals, boolean update);
	/**
	 * 
	 * @param printFigures print figures
	 * @param update true to update the text
	 */
	public void setPrintFigures(int printFigures, boolean update);
	/**
	 * 
	 * @return true iff using serif font
	 */
	public boolean isSerifFont();
	/**
	 * 
	 * @param serifFont true for serif font
	 */
	public void setSerifFont(boolean serifFont);
	/**
	 * 
	 * @return true iff significant figures should be used
	 */
	public boolean useSignificantFigures();
	/**
	 * For buttons
	 * @return true if most of these properties are ignored and just size is taken into account 
	 */
	public boolean justFontSize();
	
	/**
	 * @return whether this was created using a command with LaTeX output
	 */
	public boolean isLaTeXTextCommand();

}
