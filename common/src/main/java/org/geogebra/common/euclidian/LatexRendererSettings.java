package org.geogebra.common.euclidian;

import java.util.HashMap;
import java.util.Map;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class LatexRendererSettings implements TextRendererSettings {
	private final int fixMargin;
	private final int rightMargin;
	private int bottomOffset;
	private final int fontSize;
	private double fontMultiplier = 1.0;
	private int baseFontSize;
	private Map<Double, Integer> minHeights = new HashMap<>();

	/**
	 * @param fixMargin fix vertical margin.
	 * @param rightMargin right margin.
	 * @param bottomOffset offset of bottom
	 * @param fontSize font size.
	 */
	public LatexRendererSettings(int fixMargin, int rightMargin, int bottomOffset,
			int fontSize) {
		this.fixMargin = fixMargin;
		this.rightMargin = rightMargin;
		this.bottomOffset = bottomOffset;
		this.fontSize = fontSize;
		minHeights.put(1.0, 40);
		minHeights.put(1.4, 48);
	}

	/**
	 * Creates an instance with a given parameters
	 * @param fontSize the base fontSize.
	 * @return a new instance
	 */
	public static LatexRendererSettings create(int fontSize) {
		return new LatexRendererSettings(2, 8, 10,
				fontSize);
	}

	/**
	 * Like create(baseFontSize), but no bottomOffset, as input boxes require it.
	 * @param baseFontSize the base baseFontSize.
	 * @return a new instance
	 */
	public static LatexRendererSettings createForInputBox(int baseFontSize, double fontMultiplier) {
		LatexRendererSettings settings = create(baseFontSize);
		settings.bottomOffset = 0;
		settings.baseFontSize = baseFontSize;
		settings.fontMultiplier = fontMultiplier;
		return settings;
	}

	@Override
	public int getFixMargin() {
		return fixMargin;
	}

	@Override
	public int getMinHeight() {
		FactoryProvider.debugS("" + fontMultiplier);
		return minHeights.getOrDefault(fontMultiplier, 0);
 	}

	private int multiply(double value) {
		return (int) Math.round(value * fontMultiplier);
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
		return multiply(baseFontSize);
	}

	@Override
	public int getEditorFontSize() {
		return getRendererFontSize() + 3;
	}
}
