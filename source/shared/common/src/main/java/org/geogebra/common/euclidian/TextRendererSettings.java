package org.geogebra.common.euclidian;

import org.geogebra.common.annotation.MissingDoc;

/**
 * Text rendering settings for input boxes.
 */
public interface TextRendererSettings {
	@MissingDoc
	int getFixMargin();

	@MissingDoc
	int getMinHeight();

	@MissingDoc
	int getRightMargin();

	@MissingDoc
	int getBottomOffset();

	@MissingDoc
	int getRendererFontSize();

	@MissingDoc
	int getEditorFontSize();
}
