package org.geogebra.common.euclidian.inline;

import org.geogebra.common.awt.GColor;

public interface InlineFormulaController {

	void setLocation(int x, int y);

	void setWidth(int width);

	void setHeight(int height);

	void setAngle(double angle);

	void toForeground(int x, int y);

	void toBackground();

	void updateContent(String content);

	void setColor(GColor objectColor);

	void setFontSize(int fontSize);

	boolean isInForeground();

	void discard();

	String getText();
}
