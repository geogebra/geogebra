package geogebra.common.awt.font;

import geogebra.common.awt.Graphics2D;
import geogebra.common.awt.Rectangle2D;

public interface TextLayout {

	float getAdvance();

	Rectangle2D getBounds();

	float getAscent();

	void draw(Graphics2D g2, int i, int j);

}
