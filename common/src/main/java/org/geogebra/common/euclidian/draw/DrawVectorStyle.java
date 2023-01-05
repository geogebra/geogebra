package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GShape;

public interface DrawVectorStyle {
	void update(DrawVectorProperties properties, VectorShape vectorShape);

	void draw(GGraphics2D g2);

	GShape getShape();
}
