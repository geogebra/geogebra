package org.geogebra.common.euclidian;

public class LatexRendererSettings implements TextRendererSettings {
	private final int fixMargin;
	private final int minHeight;
	private final int rightMargin;
	private int bottomOffset;
	private final int fontSize;

	/**
	 *
	 * @param fixMargin fix vertical margin.
	 * @param minHeight minimum height of the input.
	 * @param rightMargin right margin.
	 * @param bottomOffset offset of bottom
	 * @param fontSize font size.
	 */
	public LatexRendererSettings(int fixMargin, int minHeight, int rightMargin, int bottomOffset,
			int fontSize) {
		this.fixMargin = fixMargin;
		this.minHeight = minHeight;
		this.rightMargin = rightMargin;
		this.bottomOffset = bottomOffset;
		this.fontSize = fontSize;
	}

	/**
	 * Creates an instance with a given parameters
	 * @param fontSize the base fontSize.
	 * @return a new instance
	 */
	public static LatexRendererSettings create(int fontSize) {
		return new LatexRendererSettings(2, 40, 8, 10,
				fontSize);
	}

	/**
	 * Like create(fontSize), but no bottomOffset, as input boxes require it.
	 * @param fontSize the base fontSize.
	 * @return a new instance
	 */
	public static LatexRendererSettings createForInputBox(int fontSize) {
		LatexRendererSettings settings = create(fontSize);
		settings.bottomOffset = 0;
		return settings;
	}

	@Override
	public int getFixMargin() {
		return fixMargin;
	}

	@Override
	public int getMinHeight() {
		return minHeight;
	}

	@Override
	public int getRightMargin() {
		return rightMargin;
	}

	@Override
	public int getBottomOffset() {
		return bottomOffset;
	}

	@Override
	public int getRendererFontSize() {
		return fontSize;
	}

	@Override
	public int getEditorFontSize() {
		return getRendererFontSize() + 3;
	}
}
