package org.geogebra.common.euclidian;

/**
 * Text rendering settings for input boxes.
 */
public interface TextRendererSettings {
	int getFixMargin();

	int getMinHeight();

	int getRightMargin();

	int getBottomOffset();

	int getRendererFontSize();

	int getEditorFontSize();
}
